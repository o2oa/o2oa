package com.x.bbs.assemble.control.servlet.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.entity.BBSSubjectAttachment;

/**
 * 附件上传服务
 * @author LIYI
 *
 */
@WebServlet(urlPatterns= "/servlet/upload/subject/*" )
@MultipartConfig
public class SubjectAttachmentUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( SubjectAttachmentUploadServlet.class );
	
	@HttpMethodDescribe(value = "上传附件 servlet/upload/subject", response = Object.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<Object> result = new ActionResult<>();
		
		List<BBSSubjectAttachment> attachments = new ArrayList<BBSSubjectAttachment>();
		BBSSubjectAttachment subjectAttachment = null;
		EffectivePerson effectivePerson = null;
		ServletFileUpload upload = null;
		FileItemIterator fileItemIterator = null;
		FileItemStream item = null;
		String name = null;
		String site = null;
		boolean check = true;
		InputStream input = null;
		
		request.setCharacterEncoding( "UTF-8" );
		
		if (!ServletFileUpload.isMultipartContent(request)) {
			check = false;
			result.error( new Exception( "请求不是Multipart，无法获取文件信息。" ) );
			result.setUserMessage( "请求不是Multipart，无法获取文件信息。" );
			logger.error( "not mulit part request." );
		}
		
		//从请求对象里获取操作用户信息
		if ( check ) {
			try {
				effectivePerson = FileUploadServletTools.effectivePerson( request );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统从请求对象里获取操作用户信息发生异常。" );
				logger.error( "system get effectivePerson from request got an exception.", e );
			}
		}
		
		if( check ){
			try {
				upload = new ServletFileUpload();
				fileItemIterator = upload.getItemIterator( request );
				while ( fileItemIterator.hasNext() ) {
					item = fileItemIterator.next();
					name = item.getFieldName();
					try {
						input = item.openStream();
						if (item.isFormField()) {
							String str = Streams.asString( input );
							if ( StringUtils.equals( name, "site" ) ) {
								site = str;
							}
						} else {
							StorageMapping mapping = ThisApplication.storageMappings.random( StorageType.bbs );
							subjectAttachment = concreteAttachment( effectivePerson.getName(), mapping, FileUploadServletTools.getFileName( item.getName() ), site );
							subjectAttachment.saveContent( mapping, input, item.getName() );
							attachments.add( subjectAttachment );
						}
					}finally{
						input.close();
					}
				}
			}catch(Exception e){
				check = false;
				result.setUserMessage( "系统从数据库中根据主题ID获取主题基础信息时发生异常。" );
				result.error( e );
				logger.error( "system try to save subjectAttachment to Storage got an exception.", e);
			}
		}
		
		if( check ){
			if( attachments != null && attachments.size() > 0 ){
				for( BBSSubjectAttachment attachment : attachments ){
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						emc.beginTransaction( BBSSubjectAttachment.class );
						emc.persist( attachment, CheckPersistType.all );
						emc.commit();
						result.setUserMessage( subjectAttachment.getId() );
					}catch( Exception e ){
						check = false;
						result.setUserMessage( "系统向数据库存储附件信息时发生异常。" );
						result.error( e );
						logger.error( "system try to save subjectAttachment to database got an exception.", e);
					}
				}
			}
		}
		FileUploadServletTools.result( response, result );
	}
	
	private BBSSubjectAttachment concreteAttachment( String person, StorageMapping storage, String name, String site ) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		BBSSubjectAttachment attachment = new BBSSubjectAttachment();
		if (StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
			attachment.setExtension(extension);
		}
		attachment.setName(fileName);
		attachment.setFileName(fileName);
		attachment.setExtension(extension);
		attachment.setFileHost( storage.getHost() );
		attachment.setFilePath( "" );
		attachment.setStorageName( storage.getName() );
		attachment.setSite(site);
		attachment.setCreateTime( new Date() );
		attachment.setCreatorUid( person );
		attachment.setDescription( "" );
		attachment.setLastUpdateTime( new Date() );
		attachment.setLength( 0L );
		return attachment;
	}
}