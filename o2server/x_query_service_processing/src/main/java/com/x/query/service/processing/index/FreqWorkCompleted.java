package com.x.query.service.processing.index;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;
import com.x.query.core.entity.index.State;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.IndexWriteQueue;
import com.x.query.service.processing.ThisApplication;

public abstract class FreqWorkCompleted extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreqWorkCompleted.class);

    protected List<Pair<String, Date>> list(State state, int size) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(WorkCompleted.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
            Root<WorkCompleted> root = cq.from(WorkCompleted.class);
            Predicate p = null;
            if (StringUtils.isNotEmpty(state.getLatestId())
                    && (!Objects.isNull(state.getLatestUpdateTime()))) {
                p = cb.greaterThanOrEqualTo(root.get(JpaObject_.updateTime), state.getLatestUpdateTime());
                p = cb.and(p, cb.not(cb.and(cb.equal(root.get(JpaObject_.updateTime), state.getLatestUpdateTime()),
                        cb.equal(root.get(WorkCompleted_.id), state.getLatestId()))));
            } else {
                p = cb.conjunction();
            }
            Path<String> idPath = root.get(WorkCompleted_.id);
            Path<Date> updateTimePath = root.get(JpaObject_.updateTime);
            cq.multiselect(idPath, updateTimePath).where(p).orderBy(cb.asc(root.get(JpaObject_.updateTime)));
            return em.createQuery(cq).setMaxResults(size).getResultList().stream()
                    .map(o -> Pair.of(o.get(idPath), o.get(updateTimePath))).collect(Collectors.toList());
        }
    }

    protected State getState(String freq) throws Exception {
        State state = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            state = emc.firstEqualAndEqualAndEqual(State.class, State.NODE_FIELDNAME, Config.node(),
                    State.FREQ_FIELDNAME, freq, State.TYPE_FIELDNAME,
                    State.TYPE_WORKCOMPLETED);
            if (null != state) {
                emc.get(State.class).detach(state);
            }
        }
        if (null == state) {
            state = new State();
            state.setNode(Config.node());
            state.setType(State.TYPE_WORKCOMPLETED);
            state.setFreq(freq);
        }
        return state;
    }

    protected void updateState(State state) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(State.class);
            State exist = emc.find(state.getId(), State.class);
            if (null == exist) {
                emc.check(state, CheckPersistType.all);
                emc.persist(state);
            } else {
                exist.setLatestId(state.getLatestId());
                exist.setLatestUpdateTime(state.getLatestUpdateTime());
                emc.check(exist, CheckPersistType.all);
            }
            emc.commit();
        }
    }

    protected void index(List<String> ids) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<Doc> list = ids.stream().map(o -> Pair.of(business, o)).map(DocFunction.wrapWorkCompleted)
                    .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            ThisApplication.indexWriteQueue.send(new IndexWriteQueue.UpdateMessage(list, true));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}