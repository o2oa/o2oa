package com.x.query.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.index.State;
import com.x.query.core.entity.index.State_;
import com.x.query.service.processing.AbstractFactory;
import com.x.query.service.processing.Business;

public class StateFactory extends AbstractFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateFactory.class);

    public StateFactory(Business business) throws Exception {
        super(business);
    }

    public State getState(String mode, String type, String freq, String node) throws Exception {
        State state = null;
        EntityManager em = this.entityManagerContainer().get(State.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<State> cq = cb.createQuery(State.class);
        Root<State> root = cq.from(State.class);
        Predicate p = cb.equal(root.get(State_.type), type);
        p = cb.and(p, cb.equal(root.get(State_.freq), freq));
        p = cb.and(p, cb.equal(root.get(State_.mode), mode));
        if (StringUtils.equals(mode, State.MODE_LOCALDIRECTORY)) {
            p = cb.and(p, cb.equal(root.get(State_.node), node));
        }
        cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject_.updateTime)));
        List<State> os = em.createQuery(cq).setMaxResults(1).getResultList();
        if (!os.isEmpty()) {
            state = os.get(0);
            this.business().entityManagerContainer().get(State.class).detach(state);
        } else {
            state = new State();
            state.setNode(node);
            state.setType(type);
            state.setFreq(freq);
            state.setMode(mode);
        }
        return state;
    }

    public void updateState(State state) throws Exception {
        EntityManagerContainer emc = this.entityManagerContainer();
        emc.beginTransaction(State.class);
        State exist = emc.find(state.getId(), State.class);
        if (null == exist) {
            emc.check(state, CheckPersistType.all);
            emc.persist(state);
        } else {
            exist.setLatestIdList(state.getLatestIdList());
            exist.setLatestUpdateTime(state.getLatestUpdateTime());
            emc.check(exist, CheckPersistType.all);
        }
        emc.commit();
    }
}
