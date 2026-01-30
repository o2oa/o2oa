package com.x.teamwork.assemble.control.jaxrs.attachment;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.Task;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author sword
 */
public class ActionTaskAttachmentUpload extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTaskAttachmentUpload.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,
			String taskId, String site, byte[] bytes, FormDataContentDisposition disposition) throws Exception{
		ActionResult<Wo> result = new ActionResult<>();
		String fileName = this.fileName(disposition);
		Wo wo = new Wo();

		if( StringUtils.isEmpty( taskId ) ){
			throw new ExceptionTaskIdEmpty();
		}

		Task task = taskQueryService.get( taskId );
		if (null == task) {
			throw new ExceptionTaskNotExists( taskId );
		}

		if(ProjectStatusEnum.isEndStatus(task.getWorkStatus())){
			throw new ExceptionAttachmentOperate("当前任务不允许上传附件!");
		}

		if(!this.isManager(task.getId(), effectivePerson)){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		StorageMapping mapping = ThisApplication.context().storageMappings().random( Attachment.class );
		if(mapping == null){
			throw new ExceptionAllocateStorageMaaping();
		}
		/** 禁止不带扩展名的文件上传 */
		if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
			throw new ExceptionEmptyExtension(fileName);
		}

		Attachment attachment = this.concreteAttachment( mapping, task, fileName, effectivePerson, site );
		attachment.saveContent(mapping, bytes, fileName);
		attachment = attachmentPersistService.saveAttachment( task, attachment );
		wo.setId( attachment.getId() );

		try {
			Dynamic dynamic = dynamicPersistService.uploadAttachmentDynamic(attachment, effectivePerson);
			if( dynamic != null ) {
				List<WoDynamic> dynamics = new ArrayList<>();
				dynamics.add( WoDynamic.copier.copy( dynamic ) );
				wo.setDynamics(dynamics);
			}
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
		}
		result.setData(wo);
		return result;
	}

	private Attachment concreteAttachment(StorageMapping mapping, Task task, String name, EffectivePerson effectivePerson, String site) throws Exception {
		Attachment attachment = new Attachment();
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		fileName = fileName + "." + extension;
		attachment.setCreateTime( new Date() );
		attachment.setLastUpdateTime( new Date() );
		attachment.setExtension( extension );
		attachment.setName( name );
		attachment.setFileName( fileName );
		attachment.setStorage( mapping.getName() );
		attachment.setProjectId( task.getProject() );
		attachment.setTaskId( task.getId() );
		attachment.setBundleObjType(Task.class.getSimpleName().toUpperCase()) ;
		attachment.setCreatorUid( effectivePerson.getDistinguishedName() );
		attachment.setSite( site );
		attachment.setFileHost( "" );
		attachment.setFilePath( "" );
		return attachment;
	}

	public static class Wo extends WoId {
		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
	}

	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}
