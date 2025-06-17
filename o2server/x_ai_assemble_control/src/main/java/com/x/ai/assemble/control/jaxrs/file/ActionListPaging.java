package com.x.ai.assemble.control.jaxrs.file;

import com.google.gson.JsonElement;
import com.x.ai.core.entity.File;
import com.x.ai.core.entity.File_;
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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

class ActionListPaging extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			if(effectivePerson.isNotManager()){
				throw new ExceptionAccessDenied(effectivePerson);
			}

			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);

			EntityManager em = emc.get(File.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<File> cq = cb.createQuery(File.class);
			Root<File> root = cq.from(File.class);
			Predicate p = cb.conjunction();
			if(StringUtils.isNotBlank(wi.getFileId())){
				p = cb.and(p, cb.equal(root.get(File_.fileId), wi.getFileId()));
			}
			if(StringUtils.isNotBlank(wi.getFileName())){
				String key = StringTools.escapeSqlLikeKey(wi.getFileName());
				p = cb.and(p, cb.like(root.get(File_.name), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
			}
			List<Wo> wos = emc.fetchDescPaging(File.class, Wo.copier, p, adjustPage, adjustPageSize, File.LASTUPDATETIME_FIELDNAME);

			result.setData(wos);
			result.setCount(emc.count(File.class, p));
			return result;
		}
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("关联文件ID")
		private String fileId;

		@FieldDescribe("附件名称")
		private String fileName;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileId() {
			return fileId;
		}

		public void setFileId(String fileId) {
			this.fileId = fileId;
		}
	}

	public static class Wo extends File {

		private static final long serialVersionUID = 5050265572359201452L;

		static WrapCopier<File, Wo> copier = WrapCopierFactory.wo(File.class, Wo.class,
				JpaObject.singularAttributeField(File.class, true, true), null);

	}

}
