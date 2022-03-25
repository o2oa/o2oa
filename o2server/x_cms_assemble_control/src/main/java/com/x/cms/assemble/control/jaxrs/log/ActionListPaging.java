package com.x.cms.assemble.control.jaxrs.log;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.Log_;

class ActionListPaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(Log.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Log> root = cq.from(Log.class);
			Predicate p = cb.conjunction();
			if (ListTools.isNotEmpty(wi.getAppIdList())) {
				p = cb.and(p, root.get(Log_.appId).in(wi.getAppIdList()));
			}
			if (ListTools.isNotEmpty(wi.getCategoryIdList())) {
				p = cb.and(p, root.get(Log_.categoryId).in(wi.getCategoryIdList()));
			}
			if (ListTools.isNotEmpty(wi.getOperatorList())) {
				List<String> person_ids = business.organization().person().list(wi.getOperatorList());
				if (ListTools.isNotEmpty(person_ids)) {
					p = cb.and(p, root.get(Log_.operatorUid).in(person_ids));
				}
			}
			if (StringUtils.isNotBlank(wi.getOperationType())) {
				p = cb.and(p, cb.equal(root.get(Log_.operationType), wi.getOperationType()));
			}
			if (StringUtils.isNoneBlank(wi.getKey())) {
				String key = StringTools.escapeSqlLikeKey(wi.getKey());
				p = cb.and(p, cb.like(root.get(Log_.description), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
			}
			List<Wo> wos = emc.fetchDescPaging(Log.class, Wo.copier, p, page, size, Log.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(Log.class, p));
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject{

		private static final long serialVersionUID = -1713805760437845242L;

		@FieldDescribe("用于过滤条件的栏目ID列表.")
		private List<String> appIdList;

		@FieldDescribe("用于过滤条件的分类ID列表.")
		private List<String> categoryIdList;

		@FieldDescribe("用于过滤条件的操作者列表.")
		private List<String> operatorList;

		@FieldDescribe("用于过滤条件的操作列别.")
		private String operationType;

		@FieldDescribe("用于标题搜索的关键字.")
		private String key;

		public List<String> getAppIdList() {
			return appIdList;
		}

		public void setAppIdList(List<String> appIdList) {
			this.appIdList = appIdList;
		}

		public List<String> getCategoryIdList() {
			return categoryIdList;
		}

		public void setCategoryIdList(List<String> categoryIdList) {
			this.categoryIdList = categoryIdList;
		}

		public List<String> getOperatorList() {
			return operatorList;
		}

		public void setOperatorList(List<String> operatorList) {
			this.operatorList = operatorList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getOperationType() {
			return operationType;
		}

		public void setOperationType(String operationType) {
			this.operationType = operationType;
		}
	}

	public static class Wo extends Log {

		private static final long serialVersionUID = -5076990764713538973L;

		public static final WrapCopier<Log, Wo> copier = WrapCopierFactory.wo(Log.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}
