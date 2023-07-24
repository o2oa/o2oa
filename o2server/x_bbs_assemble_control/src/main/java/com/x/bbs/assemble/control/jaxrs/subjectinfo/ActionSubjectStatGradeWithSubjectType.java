package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSSubjectInfo_;

public class ActionSubjectStatGradeWithSubjectType extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSubjectStatGradeWithSubjectType.class );

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String sectionName, String subjectType ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			EntityManager em = emc.get(BBSSubjectInfo.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Wo> cq = cb.createQuery(Wo.class);
			Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
			Predicate p = cb.equal(root.get(BBSSubjectInfo_.sectionName), sectionName);
			p = cb.and( p, cb.equal( root.get(BBSSubjectInfo_.type ), subjectType ) );
			Path<Integer> grade = root.get(BBSSubjectInfo_.grade);
			cq.multiselect(grade, cb.count(root).as(Integer.class)).where(p).groupBy(grade).orderBy(cb.desc(grade));
			List<Wo> wos = em.createQuery(cq).getResultList();

			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private Integer grade;

		private Integer count;

		public Wo(Integer grade, Integer count){
			this.grade = grade;
			this.count = count;
		}

		public Integer getGrade() {
			return grade;
		}

		public void setGrade(Integer grade) {
			this.grade = grade;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}
	}
}