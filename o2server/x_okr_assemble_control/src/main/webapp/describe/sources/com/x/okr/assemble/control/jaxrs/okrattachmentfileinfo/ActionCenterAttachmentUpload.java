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
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionEmptyExtension;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.URLParameterGetException;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ActionCenterAttachmentUpload extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionCenterAttachmentUpload.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, 
			String centerId, String site, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<Wo> result = new ActionResult<>();
		OkrAttachmentFileInfo attachment = null;
		OkrCenterWorkInfo center = null;
		StorageMapping mapping = null;
		String fileName = null;
		Boolean check = true;		
		
		if( check ){
			if( StringUtils.isEmpty(centerId) ){
				check = false;
				Exception exception = new URLParameterGetException( new Exception("未获取到中心工作ID") );
				result.error( exception );
			}
		}
		
		if( check ){//判断工作信息是否已经存在
			try {
				center = okrCenterWorkInfoService.get( centerId );
				if (null == center) {
					check = false;
					Exception exception = new ExceptionCenterWorkNotExists( centerId );
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
				attachment = this.concreteAttachment( mapping, center, fileName, effectivePerson, site );
				attachment.saveContent(mapping, bytes, fileName);
				attachment = okrCenterWorkInfoService.saveAttachment( centerId, attachment );
				Wo wo = new Wo();
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

	private OkrAttachmentFileInfo concreteAttachment(StorageMapping mapping, OkrCenterWorkInfo center, String name, EffectivePerson effectivePerson, String site) throws Exception {
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
		attachment.setWorkInfoId("");
		attachment.setCenterId( center.getId() );
		attachment.setStatus( "正常" );
		attachment.setParentType( "工作" );
		attachment.setCreatorUid( effectivePerson.getDistinguishedName() );
		attachment.setSite( site );
		attachment.setFileHost( "" );
		attachment.setFilePath( "" );
		attachment.setKey( center.getId() );
		return attachment;
	}

	public static class Wo extends WoId {

	}
}
