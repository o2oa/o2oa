package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Attachment_;
import com.x.file.core.entity.personal.Folder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

class ActionUpload extends BaseAction {

	// @HttpMethodDescribe(value = "创建Attachment对象,如果没有上级目录用(0)替代.", response =
	// WrapOutId.class)
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String folderId, String fileName, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			if ((!StringUtils.isEmpty(folderId)) && (!StringUtils.equalsIgnoreCase(folderId, EMPTY_SYMBOL))) {
				Folder folder = emc.find(folderId, Folder.class);
				if (null == folder) {
					throw new ExceptionFolderNotExist(folderId);
				}
				if ((!StringUtils.equals(business.organization().person().get(folder.getPerson()),
						effectivePerson.getDistinguishedName())) && (effectivePerson.isNotManager())) {
					throw new ExceptionFolderAccessDenied(effectivePerson, folder);
				}
				folderId = folder.getId();
			} else {
				folderId = null;
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMaaping();
			}
			/** 由于需要校验要把所有的必要字段进行填写 */

			/** 文件名编码转换 */
			if (StringUtils.isEmpty(fileName)) {
				fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
						DefaultCharset.charset);
			}
			fileName = FilenameUtils.getName(fileName);
			/** 禁止不带扩展名的文件上传 */
			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new ExceptionEmptyExtension(fileName);
			}
			/** 同一目录下文件名唯一 */
			if (this.exist(business, fileName, folderId)) {
				throw new ExceptionSameNameFileExist(fileName);
			}
			FileTools.verifyConstraint(bytes.length, fileName, null);
			Attachment attachment = new Attachment(mapping.getName(), fileName, effectivePerson.getDistinguishedName(),
					folderId);
			emc.check(attachment, CheckPersistType.all);
			attachment.saveContent(mapping, bytes, fileName);
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	private Boolean exist(Business business, String fileName, String folderId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal(root.get(Attachment_.name), fileName);
		if (StringUtils.isNotEmpty(folderId)) {
			p = cb.and(p, cb.equal(root.get(Attachment_.folder), folderId));
		} else {
			p = cb.and(p, cb.or(cb.isNull(root.get(Attachment_.folder)), cb.equal(root.get(Attachment_.folder), "")));
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	public static class Wo extends WoId {
	}
}
