package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.assemble.control.jaxrs.commend.BaseAction;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.FileInfo_;

class ActionListFilter extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(FileInfo.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FileInfo> cq = cb.createQuery(FileInfo.class);
			Root<FileInfo> root = cq.from(FileInfo.class);
			Predicate p = cb.conjunction();
			boolean flag = false;
			if (StringUtils.isNotBlank(wi.getAppId())){
				flag = true;
				p = cb.and(p, cb.equal(root.get(FileInfo_.appId), wi.getAppId()));
			}
			if (StringUtils.isNotBlank(wi.getCategoryId())){
				flag = true;
				p = cb.and(p, cb.equal(root.get(FileInfo_.categoryId), wi.getCategoryId()));
			}
			if (StringUtils.isNotBlank(wi.getDocumentId())){
				flag = true;
				p = cb.and(p, cb.equal(root.get(FileInfo_.documentId), wi.getDocumentId()));
			}
			if (StringUtils.isNotBlank(wi.getName())){
				flag = true;
				String key = StringTools.escapeSqlLikeKey(wi.getName());
				p = cb.and(p, cb.like(root.get(FileInfo_.name), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
			}
			List<Wo> wos = new ArrayList<>();
			if(flag) {
				wos = emc.fetch(FileInfo.class, Wo.copier, p);
			}
			result.setData(wos);
			result.setCount(Long.valueOf(wos.size()));
			return result;
		}
	}

	public static class Wi {

		@FieldDescribe("文件名称")
		private String name;
		@FieldDescribe("文件所属应用ID")
		private String appId;
		@FieldDescribe("文件所属分类ID")
		private String categoryId;
		@FieldDescribe("文件所属文档ID")
		private String documentId;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public String getDocumentId() {
			return documentId;
		}

		public void setDocumentId(String documentId) {
			this.documentId = documentId;
		}
	}

	public static class Wo extends FileInfo {

		private static final long serialVersionUID = -5640556392454126453L;

		static WrapCopier<FileInfo, Wo> copier = WrapCopierFactory.wo(FileInfo.class, Wo.class,
				JpaObject.singularAttributeField(FileInfo.class, true, true), null);

	}
}
