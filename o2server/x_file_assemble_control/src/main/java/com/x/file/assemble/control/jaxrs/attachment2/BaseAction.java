package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileConfig;
import com.x.file.core.entity.open.FileConfigProperties;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Attachment2_;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

abstract class BaseAction extends StandardJaxrsAction {

	protected void message_send_attachment_share(Attachment attachment, String person) throws Exception {
		String title = "收到来自(" + OrganizationDefinition.name(attachment.getPerson()) + ")的共享文件:" + attachment.getName()
				+ ".";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_SHARE, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_shareCancel(Attachment attachment, String person) throws Exception {
		String title = "(" + OrganizationDefinition.name(attachment.getPerson()) + ")取消了对:" + attachment.getName()
				+ ",文件的共享.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_SHARECANCEL, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editor(Attachment attachment, String person) throws Exception {
		String title = "收到来自(" + OrganizationDefinition.name(attachment.getPerson()) + ")的可编辑共享文件:"
				+ attachment.getName() + ".";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITOR, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editorCancel(Attachment attachment, String person) throws Exception {
		String title = "(" + OrganizationDefinition.name(attachment.getPerson()) + ")取消了对:" + attachment.getName()
				+ ",文件的共享编辑.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITORCANCEL, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editorModify(Attachment attachment, String editor, String person)
			throws Exception {
		String title = "(" + OrganizationDefinition.name(editor) + ")对文件:" + attachment.getName() + ",进行了修改.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITORMODIFY, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected Boolean exist(Business business, String fileName, String folderId, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.name), fileName);
		p = cb.and(p, cb.equal(root.get(Attachment2_.person), person));
		p = cb.and(p, cb.equal(root.get(Attachment2_.folder), folderId));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	protected void verifyConstraint(Business business, String person, long size, String fileName) throws Exception{
		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(FileConfig.class);
		Cache.CacheKey cacheKey = new Cache.CacheKey(FileConfig.class, Business.SYSTEM_CONFIG);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		FileConfig config = null;
		if(optional.isPresent()){
			config = (FileConfig)optional.get();
		}else{
			config = business.entityManagerContainer().firstEqual(FileConfig.class, FileConfig.person_FIELDNAME, Business.SYSTEM_CONFIG);
			if(config != null){
				CacheManager.put(cacheCategory, cacheKey, config);
			}
		}
		if (config != null){
			if(config.getCapacity()!=null && config.getCapacity()>0) {
				long usedCapacity = (business.attachment2().getUseCapacity(person) + size) / (1024 * 1024);
				if (usedCapacity > config.getCapacity()) {
					throw new ExceptionCapacityOut(usedCapacity, config.getCapacity());
				}
			}
			FileConfigProperties properties = config.getProperties();
			String fileType = FilenameUtils.getExtension(fileName).toLowerCase();
			if(properties!=null){
				if(properties.getFileTypeIncludes()!=null && !properties.getFileTypeIncludes().isEmpty()){
					if(!ListTools.contains(properties.getFileTypeIncludes(), fileType)){
						throw new ExceptionAttachmentUploadDenied(fileName);
					}
				}
				if(properties.getFileTypeExcludes()!=null && !properties.getFileTypeExcludes().isEmpty()){
					if(ListTools.contains(properties.getFileTypeExcludes(), fileType)){
						throw new ExceptionAttachmentUploadDenied(fileName);
					}
				}
			}
		}
	}
}
