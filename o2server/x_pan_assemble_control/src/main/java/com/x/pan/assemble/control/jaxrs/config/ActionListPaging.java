package com.x.pan.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class ActionListPaging extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);

			EntityManager em = emc.get(FileConfig3.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FileConfig3> cq = cb.createQuery(FileConfig3.class);
			Root<FileConfig3> root = cq.from(FileConfig3.class);
			Predicate p;
			if(StringUtils.isBlank(wi.getPerson())){
				p = cb.notEqual(root.get(FileConfig3.person_FIELDNAME), Business.SYSTEM_CONFIG);
			}else{
				String person = business.organization().person().get(wi.getPerson());
				if(StringUtils.isBlank(person)){
					person = wi.getPerson();
				}
				p = cb.equal(root.get(FileConfig3.person_FIELDNAME), person);
			}
			List<Wo> wos = emc.fetchAscPaging(FileConfig3.class, Wo.copier, p, adjustPage, adjustPageSize, FileConfig3.person_FIELDNAME);

			result.setData(wos);
			result.setCount(emc.count(FileConfig3.class, p));
			return result;
		}
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("用户")
		private String person;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}
	}

	public static class Wo extends FileConfig3 {

		private static final long serialVersionUID = -314452776065265453L;
		static WrapCopier<FileConfig3, Wo> copier = WrapCopierFactory.wo(FileConfig3.class, Wo.class,
				JpaObject.singularAttributeField(FileConfig3.class, true, true), null);

	}

}
