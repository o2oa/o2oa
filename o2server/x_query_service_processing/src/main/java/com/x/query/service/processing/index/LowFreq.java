package com.x.query.service.processing.index;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.query.core.entity.index.State;
import com.x.query.service.processing.Business;

public abstract class LowFreq extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(LowFreq.class);

    protected <T extends JpaObject> List<Pair<String, Date>> list(State state, Class<T> clazz, int size)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(clazz);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
            Root<T> root = cq.from(clazz);
            Predicate p = null;
            if (!Objects.isNull(state.getLatestUpdateTime())) {
                Date latestTime = DateUtils.truncate(state.getLatestUpdateTime(), Calendar.SECOND);
                p = cb.greaterThanOrEqualTo(root.get(JpaObject_.createTime), latestTime);
//                p = cb.and(p, cb.not(cb.and(cb.equal(root.get(JpaObject_.createTime), latestTime),
//                        root.get(JpaObject.id_FIELDNAME).in(state.getLatestIdList()))));
                p = cb.and(p, cb.not(root.get(JpaObject.id_FIELDNAME).in(state.getLatestIdList())));
            } else {
                p = cb.conjunction();
            }
            Path<String> idPath = root.get(JpaObject.id_FIELDNAME);
            Path<Date> createTimePath = root.get(JpaObject.createTime_FIELDNAME);
            cq.multiselect(idPath, createTimePath).where(p).orderBy(cb.asc(root.get(JpaObject_.createTime)));
            return em.createQuery(cq).setMaxResults(size).getResultList().stream()
                    .map(o -> Pair.of(o.get(idPath), o.get(createTimePath))).collect(Collectors.toList());
        }
    }

    protected List<Pair<String, Doc>> index(List<String> ids,
            Function<Pair<Business, String>, Pair<String, Optional<Doc>>> function) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return ids.stream().map(o -> Pair.of(business, o)).map(function)
                    .filter(o -> o.second().isPresent()).map(o -> Pair.of(o.first(), o.second().get()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList<>();
    }

}