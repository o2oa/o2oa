package com.x.file.assemble.control.jaxrs.attachment2;

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
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Attachment2_;

class ActionListFileTypePaging extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionListFileTypePaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (wi == null) {
				wi = new Wi();
			}
			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);
			List<Attachment2> os = this.list(effectivePerson, business, adjustPage, adjustPageSize, wi);
			List<Wo> wos = Wo.copier.copy(os);
			wos.stream().forEach(wo -> {
				try {
					wo.setPath(business.folder2().getSupPath(wo.getFolder()));
				} catch (Exception e) {
					logger.error(e);
				}
			});
			result.setData(wos);
			result.setCount(this.count(effectivePerson, business, wi));
			return result;
		}
	}

	private List<Attachment2> list(EffectivePerson effectivePerson, Business business, Integer adjustPage,
			Integer adjustPageSize, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Attachment2> cq = cb.createQuery(Attachment2.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatus.VALID.getName()));
		if(StringUtils.isNotEmpty(wi.getFileType())){
			p = cb.and(p, cb.equal(root.get(Attachment2_.type), wi.getFileType()));
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get(Attachment2_.createTime)));
		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	private Long count(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatus.VALID.getName()));
		if(StringUtils.isNotEmpty(wi.getFileType())){
			p = cb.and(p, cb.equal(root.get(Attachment2_.type), wi.getFileType()));
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("文件分类：图片(image)|文档(office)|音乐(music)|视频(movie)|其它(other)")
		private String fileType;


		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
	}

	public static class Wo extends Attachment2 {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Attachment2, Wo> copier = WrapCopierFactory.wo(Attachment2.class, Wo.class,
				JpaObject.singularAttributeField(Attachment2.class, true, true), null);

		@FieldDescribe("文件路径")
		private String path;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}

}
