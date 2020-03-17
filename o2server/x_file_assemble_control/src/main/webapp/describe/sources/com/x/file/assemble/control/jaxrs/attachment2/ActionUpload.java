package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.FileUtil;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Attachment2_;
import com.x.file.core.entity.personal.Folder2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

class ActionUpload extends StandardJaxrsAction {

	// @HttpMethodDescribe(value = "创建Attachment对象,如果没有上级目录用(0)替代.", response =
	// WrapOutId.class)
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String folderId, String fileName, String fileMd5, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			if ((!StringUtils.isEmpty(folderId)) && (!StringUtils.equalsIgnoreCase(folderId, EMPTY_SYMBOL))) {
				Folder2 folder = emc.find(folderId, Folder2.class);
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
			StorageMapping mapping = ThisApplication.context().storageMappings().random(OriginFile.class);
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
			if(StringUtils.isEmpty(fileMd5)){
				if(bytes==null) {
					throw new ExceptionEmptyExtension("上传文件的md5值为空: {}.", fileName);
				}
				fileMd5 = FileUtil.getFileMD5(bytes);
			}
			OriginFile originFile = business.originFile().getByMd5(fileMd5);
			Attachment2 attachment2 = null;
			if(originFile==null){
				if(bytes==null){
					throw new ExceptionAttachmentNone(fileName);
				}
				originFile = new OriginFile(mapping.getName(), fileName, effectivePerson.getDistinguishedName(), fileMd5);
				emc.check(originFile, CheckPersistType.all);
				originFile.saveContent(mapping, bytes, fileName);
				attachment2 = new Attachment2(fileName, effectivePerson.getDistinguishedName(),
						folderId, originFile.getId(), originFile.getLength(), originFile.getType());
				emc.check(attachment2, CheckPersistType.all);
				emc.beginTransaction(OriginFile.class);
				emc.beginTransaction(Attachment2.class);
				emc.persist(originFile);
				emc.persist(attachment2);
				emc.commit();
			}else{
				attachment2 = new Attachment2(fileName, effectivePerson.getDistinguishedName(),
						folderId, originFile.getId(), originFile.getLength(), originFile.getType());
				emc.check(attachment2, CheckPersistType.all);
				emc.beginTransaction(Attachment2.class);
				emc.persist(attachment2);
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setId(attachment2.getId());
			result.setData(wo);
			return result;
		}
	}

	private Boolean exist(Business business, String fileName, String folderId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.name), fileName);
		if (StringUtils.isNotEmpty(folderId)) {
			p = cb.and(p, cb.equal(root.get(Attachment2_.folder), folderId));
		} else {
			p = cb.and(p, cb.or(cb.isNull(root.get(Attachment2_.folder)), cb.equal(root.get(Attachment2_.folder), "")));
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	public static class Wo extends WoId {
	}
}