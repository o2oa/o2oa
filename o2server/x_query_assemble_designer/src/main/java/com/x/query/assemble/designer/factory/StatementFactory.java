package com.x.query.assemble.designer.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.AbstractFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Statement_;

public class StatementFactory extends AbstractFactory {

    public StatementFactory(Business business) throws Exception {
        super(business);
    }

    public <T extends Statement> List<T> sort(List<T> list) {
        if (null == list) {
            return new ArrayList<>();
        }
        list = list.stream()
                .sorted(Comparator.comparing(Statement::getAlias, StringTools.emptyLastComparator())
                        .thenComparing(Comparator.comparing(Statement::getName, StringTools.emptyLastComparator())))
                .collect(Collectors.toList());
        return list;
    }

    public List<Statement> listWithQueryObject(String queryId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Statement.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Statement> cq = cb.createQuery(Statement.class);
        Root<Statement> root = cq.from(Statement.class);
        Predicate p = cb.equal(root.get(Statement_.query), queryId);
        cq.select(root).where(p);
        return em.createQuery(cq).getResultList();
    }

}