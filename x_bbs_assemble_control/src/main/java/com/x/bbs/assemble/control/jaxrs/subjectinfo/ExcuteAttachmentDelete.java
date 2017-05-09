package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.AttachmentIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.AttachmentNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectQueryByIdException;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteAttachmentDelete extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteAttachmentDelete.class );
	
	protected ActionResult<WrapOutSubjectAttachment> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutSubjectAttachment> result = new ActionResult<>();
		BBSSubjectAttachment subjectAttachment = null;
		BBSSubjectInfo subjectInfo = null;
		StorageMapping mapping = null;
		boolean check = true;
		if ( id == null || id.isEmpty() ) {
			check = false;
			Exception exception = new AttachmentIdEmptyException();
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}
		if ( check ) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				subjectAttachment = emc.find(id, BBSSubjectAttachment.class);
				if (null == subjectAttachment) {
					check = false;
					Exception exception = new AttachmentNotExistsException(id);
					result.error(exception);
					logger.error(exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectInfoProcessException(e, "根据指定ID查询附件信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if ( check ) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				subjectInfo = emc.find(subjectAttachment.getSubjectId(), BBSSubjectInfo.class);
				if (null == subjectInfo) {
					logger.warn("subjectInfo{id:" + subjectAttachment.getSubjectId()
							+ "} is not exists, anyone can delete the attachments.");
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new SubjectQueryByIdException(e, subjectAttachment.getSubjectId());
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if (check) {
			if ( subjectAttachment != null ) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					mapping = ThisApplication.context().storageMappings().get(BBSSubjectAttachment.class, subjectAttachment.getStorage());
					// 对文件进行删除
					subjectAttachment.deleteContent(mapping);
					// 对数据库记录进行删除
					subjectAttachment = emc.find(id, BBSSubjectAttachment.class);
					subjectInfo = emc.find(subjectAttachment.getSubjectId(), BBSSubjectInfo.class);
					emc.beginTransaction(BBSSubjectAttachment.class);
					emc.beginTransaction(BBSSubjectInfo.class);
					if (subjectInfo != null && subjectInfo.getAttachmentList() != null) {
						subjectInfo.getAttachmentList().remove(subjectAttachment.getId());
						emc.check(subjectInfo, CheckPersistType.all);
					}
					emc.remove(subjectAttachment, CheckRemoveType.all);
					emc.commit();
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectInfoProcessException(e, "根据指定ID删除附件信息时发生异常.ID:" + subjectAttachment.getSubjectId());
					result.error(exception);
					logger.error(exception, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

}