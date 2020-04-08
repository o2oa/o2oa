package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionEmptyExtension;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.URLParameterGetException;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionWorkAttachmentUpload extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionWorkAttachmentUpload.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, 
			String workId, String site, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<Wo> result = new ActionResult<>();
		OkrAttachmentFileInfo attachment = null;
		OkrWorkBaseInfo work = null;
		StorageMapping mapping = null;
		String fileName = null;
		Boolean check = true;		
		
		if( check ){
			if( StringUtils.isEmpty(workId) ){
				check = false;
				Exception exception = new URLParameterGetException( new Exception("未获取到汇报ID") );
				result.error( exception );
			}
		}
		
		if( check ){//判断工作信息是否已经存在
			try {
				work = okrWorkBaseInfoService.get( workId );
				if (null == work) {
					check = false;
					Exception exception = new ExceptionWorkNotExists( workId );
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
				mapping = ThisApplication.context().storageMappings().random( OkrAttachmentFileInfo.class );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "系统在获取存储的时候发生异常！" );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		if( check ){
			try {
				attachment = this.concreteAttachment( mapping, work, fileName, effectivePerson, site );
				attachment.saveContent(mapping, bytes, fileName);
				System.out.println("保存附件信息对象：attachment.getId()=" + attachment.getId() );
				System.out.println("保存附件信息对象：workId=" + workId );
				attachment = okrWorkBaseInfoService.saveAttachment( workId, attachment );
				Wo wo = new Wo();
				System.out.println("attachment.id=" + attachment.getId() );
				wo.setId( attachment.getId() );
				result.setData(wo);
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "系统在保存文件和更新数据时候发生异常！" );
				logger.error( e, effectivePerson, request, null );
			}
		}
		return result;
	}

	private OkrAttachmentFileInfo concreteAttachment(StorageMapping mapping, OkrWorkBaseInfo work, String name, EffectivePerson effectivePerson, String site) throws Exception {
		OkrAttachmentFileInfo attachment = new OkrAttachmentFileInfo();
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
		attachment.setWorkInfoId( work.getId() );
		attachment.setCenterId( work.getCenterId() );
		attachment.setStatus( "正常" );
		attachment.setParentType( "工作" );
		attachment.setCreatorUid( effectivePerson.getDistinguishedName() );
		attachment.setSite( site );
		attachment.setFileHost( "" );
		attachment.setFilePath( "" );
		attachment.setKey( work.getId() );
		return attachment;
	}

	public static class Wo extends WoId {

	}
}
