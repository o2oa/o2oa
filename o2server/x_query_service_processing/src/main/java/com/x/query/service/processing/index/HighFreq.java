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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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

public abstract class HighFreq extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighFreq.class);

    protected <T extends JpaObject> List<T> list(State state, Class<T> clazz, int size)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(clazz);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(clazz);
            Root<T> root = cq.from(clazz);
            Predicate p = cb.conjunction();
            if (!Objects.isNull(state.getLatestUpdateTime())) {
                Date latestTime = DateUtils.truncate(state.getLatestUpdateTime(), Calendar.SECOND);
                p = cb.greaterThanOrEqualTo(root.get(JpaObject_.createTime), latestTime);
                p = cb.and(p, cb.not(root.get(JpaObject.id_FIELDNAME).in(state.getLatestIdList())));
            }
            cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject_.createTime)));
            LOGGER.debug("freq list qurey sql:{}.", cq.toString());
            return em.createQuery(cq).setMaxResults(size).getResultList();
        }
    }

    protected List<Doc> index(List<String> ids,
            Function<Pair<Business, String>, Pair<String, Optional<Doc>>> function) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return ids.stream().map(o -> Pair.of(business, o)).map(function)
                    .filter(o -> o.second().isPresent()).map(o -> o.second().get())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList<>();
    }

}