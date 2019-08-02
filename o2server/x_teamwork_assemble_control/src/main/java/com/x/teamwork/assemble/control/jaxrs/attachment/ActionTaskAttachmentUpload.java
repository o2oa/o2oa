package com.x.teamwork.assemble.control.jaxrs.attachment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Task;

public class ActionTaskAttachmentUpload extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionTaskAttachmentUpload.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, 
			String taskId, String site, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = null;
		Task task = null;
		StorageMapping mapping = null;
		String fileName = null;
		Boolean check = true;		
		Wo wo = new Wo();
		
		if( check ){
			if( StringUtils.isEmpty( taskId ) ){
				check = false;
				Exception exception = new ExceptionTaskIdEmpty( );
				result.error( exception );
			}
		}
		
		if( check ){//判断工作信息是否已经存在
			try {
				task = taskQueryService.get( taskId );
				if (null == task) {
					check = false;
					Exception exception = new ExceptionTaskNotExists( taskId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		if( check ){
			try {
				fileName = FilenameUtils.getName(new String(disposition.getFileName().getBytes(DefaultCharset.name_iso_8859_1), DefaultCharset.name));
				/** 禁止不带扩展名的文件上传 */
				if (StringUtils.isEmpty(fileName)) {
					check = false;
					Exception exception = new ExceptionEmptyExtension( fileName );
					result.error( exception );
				} 
			} catch (Exception e) {
				check = false;
				result.error( e );
			}
		}
		
		if( check ){
			try {
				mapping = ThisApplication.context().storageMappings().random( Attachment.class );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "系统在获取存储的时候发生异常！" );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		if( check ){
			try {
				attachment = this.concreteAttachment( mapping, task, fileName, effectivePerson, site );
				attachment.saveContent(mapping, bytes, fileName);
				attachment = attachmentPersistService.saveAttachment( task, attachment );
				wo.setId( attachment.getId() );				
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "系统在保存附件和更新附件数据时候发生异常！" );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		if (check) {
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
		}
		result.setData(wo);
		return result;
	}

	private Attachment concreteAttachment(StorageMapping mapping, Task task, String name, EffectivePerson effectivePerson, String site) throws Exception {
		Attachment attachment = new Attachment();
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		if ( StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
		}else{
			throw new Exception("file extension is empty.");
		}
		if( name.indexOf( "\\" ) >0 ){
			name = StringUtils.substringAfterLast( name, "\\");
		}
		if( name.indexOf( "/" ) >0 ){
			name = StringUtils.substringAfterLast( name, "/");
		}
		attachment.setCreateTime( new Date() );
		attachment.setLastUpdateTime( new Date() );
		attachment.setExtension( extension );
		attachment.setName( name );
		attachment.setFileName( fileName );
		attachment.setStorage( mapping.getName() );
		attachment.setProjectId( task.getProject() );
		attachment.setTaskId( task.getId() );
		attachment.setBundleObjType("TASK") ;
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
