package com.x.bbs.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.tools.FileTools;
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
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionUpload extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUpload.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,
			String subjectId, String site, byte[] bytes, FormDataContentDisposition disposition) throws Exception{
		if(effectivePerson.isAnonymous()){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		ActionResult<Wo> result = new ActionResult<>();
		BBSSubjectAttachment attachment = null;
		BBSSubjectInfo subject = null;
		StorageMapping mapping = null;
		String fileName = null;
		Boolean check = true;

		if( check ){
			if( StringUtils.isEmpty(subjectId) ){
				check = false;
				Exception exception = new ExceptionURLParameterGet( new Exception("未获取到主贴ID") );
				result.error( exception );
			}
		}

		if( check ){
			//判断文档是否已经存在
			try {
				subject = subjectInfoServiceAdv.get( subjectId );
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

		FileTools.verifyConstraint(bytes.length, fileName, null);

		if( check ){

			try {
				mapping = ThisApplication.context().storageMappings().random( BBSSubjectAttachment.class );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "系统在获取存储的时候发生异常！" );
				logger.error( e, effectivePerson, request, null );
			}
		}
		if( check ){
			try {
				attachment = this.concreteAttachment( mapping, subject, fileName, effectivePerson, site );
				attachment.saveContent(mapping, bytes, fileName);
				attachment = subjectInfoServiceAdv.saveAttachment( subjectId, attachment );
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
		logger.info(result.getMessage());
		return result;
	}

	private BBSSubjectAttachment concreteAttachment(StorageMapping mapping, BBSSubjectInfo subject, String name, EffectivePerson effectivePerson, String site) throws Exception {
		BBSSubjectAttachment attachment = new BBSSubjectAttachment();
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
		attachment.setExtension(extension);
		attachment.setName(name);
		attachment.setFileName(fileName);
		attachment.setExtension(extension);
		attachment.setFileHost( mapping.getHost() );
		attachment.setFilePath( "" );
		attachment.setStorage( mapping.getName() );
		attachment.setSite(site);
		attachment.setCreateTime( new Date() );
		attachment.setCreatorUid( effectivePerson.getDistinguishedName() );
		if( subject != null ){
			attachment.setDescription( subject.getTitle() );
		}
		attachment.setLastUpdateTime( new Date() );
		attachment.setLength( 0L );
		return attachment;
	}

	public static class Wo extends WoId {

	}
}
