package com.x.query.service.processing.index;

import java.util.List;
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
import com.x.query.core.entity.index.State;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.IndexWriteQueue;
import com.x.query.service.processing.ThisApplication;

public abstract class FrequencyDocument extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrequencyDocument.class);

    protected List<Pair<String, String>> list(State state, int size) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(com.x.cms.core.entity.Document.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
            Root<com.x.cms.core.entity.Document> root = cq.from(com.x.cms.core.entity.Document.class);
            Predicate p = null;
            if (StringUtils.isNotEmpty(state.getLatestUpdateId())
                    && StringUtils.isNotEmpty(state.getLatestUpdateSequence())) {
                p = cb.notEqual(root.get(com.x.cms.core.entity.Document_.id), state.getLatestUpdateId());
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(JpaObject_.sequence), state.getLatestUpdateSequence()));
            } else {
                p = cb.conjunction();
            }
            Path<String> idPath = root.get(com.x.cms.core.entity.Document_.id);
            Path<String> sequencePath = root.get(JpaObject_.sequence);
            cq.multiselect(idPath, sequencePath).where(p).orderBy(cb.asc(root.get(JpaObject_.sequence)));
            return em.createQuery(cq).setMaxResults(size).getResultList().stream()
                    .map(o -> Pair.of(o.get(idPath), o.get(sequencePath))).collect(Collectors.toList());
        }
    }

    protected State getState(String frequency) throws Exception {
        State state = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            state = emc.firstEqualAndEqualAndEqual(State.class, State.NODE_FIELDNAME, Config.node(),
                    State.FREQUENCY_FIELDNAME, frequency, State.TYPE_FIELDNAME, State.TYPE_DOCUMENT);
            if (null != state) {
                emc.get(State.class).detach(state);
            }
        }
        if (null == state) {
            state = new State();
            state.setNode(Config.node());
            state.setType(State.TYPE_DOCUMENT);
            state.setFrequency(frequency);
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
                exist.setLatestUpdateId(state.getLatestUpdateId());
                exist.setLatestUpdateSequence(state.getLatestUpdateSequence());
                emc.check(exist, CheckPersistType.all);
            }
            emc.commit();
        }
    }

    protected void index(List<String> ids) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<Doc> list = ids.stream().map(o -> Pair.of(business, o)).map(DocFunction.wrapDocument)
                    .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            ThisApplication.indexWriteQueue.send(new IndexWriteQueue.UpdateMessage(list, true));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

}