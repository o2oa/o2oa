package com.x.pan.assemble.control.jaxrs.attachment3;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Attachment3_;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class ActionManagerListPaging extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionManagerListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			if(!business.controlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);

			EntityManager em = emc.get(Attachment3.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Attachment3> cq = cb.createQuery(Attachment3.class);
			Root<Attachment3> root = cq.from(Attachment3.class);
			Predicate p = cb.conjunction();
			if(StringUtils.isNotBlank(wi.getPerson())){
				String person = business.organization().person().get(wi.getPerson());
				if(StringUtils.isBlank(person)){
					person = wi.getPerson();
				}
				p = cb.and(p, cb.equal(root.get(Attachment3_.person), person));
			}
			if(StringUtils.isNotBlank(wi.getFileName())){
				String key = StringTools.escapeSqlLikeKey(wi.getFileName());
				p = cb.and(p, cb.like(root.get(Attachment3_.name), key + "%", StringTools.SQL_ESCAPE_CHAR));
			}
			List<Wo> wos = emc.fetchDescPaging(Attachment3.class, Wo.copier, p, adjustPage, adjustPageSize, Attachment3.lastUpdateTime_FIELDNAME);

			result.setData(wos);
			result.setCount(emc.count(FileConfig3.class, p));
			return result;
		}
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("创建用户")
		private String person;

		@FieldDescribe("附件名称")
		private String fileName;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}

	public static class Wo extends Attachment3 {

		private static final long serialVersionUID = 5050265572359201452L;

		static WrapCopier<Attachment3, Wo> copier = WrapCopierFactory.wo(Attachment3.class, Wo.class,
				JpaObject.singularAttributeField(Attachment3.class, true, true), null);

	}

}
