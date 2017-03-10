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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
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
public class SubjectAttachmentUploadServlet extends AbstractServletAction {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( SubjectAttachmentUploadServlet.class );
	
	@HttpMethodDescribe(value = "上传附件 servlet/upload/subject", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
			logger.warn( "not mulit part request." );
		}
		
		//从请求对象里获取操作用户信息
		if ( check ) {
			try {
				effectivePerson = this.effectivePerson( request );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system get effectivePerson from request got an exception." );
				logger.error(e);
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
							StorageMapping mapping = ThisApplication.storageMappings.random( BBSSubjectAttachment.class );
							subjectAttachment = concreteAttachment( effectivePerson.getName(), mapping, this.getFileName( item.getName() ), site );
							subjectAttachment.saveContent( mapping, input, item.getName() );
							attachments.add( subjectAttachment );
						}
					}finally{
						input.close();
					}
				}
			}catch(Exception e){
				check = false;
				result.error( e );
				logger.warn( "system try to save subjectAttachment to Storage got an exception." );
				logger.error(e);
			}
		}
		
		if( check ){
			if( attachments != null && attachments.size() > 0 ){
				for( BBSSubjectAttachment attachment : attachments ){
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						emc.beginTransaction( BBSSubjectAttachment.class );
						emc.persist( attachment, CheckPersistType.all );
						emc.commit();
						result.setData( new WrapOutId(subjectAttachment.getId()) );
					}catch( Exception e ){
						check = false;
						result.error( e );
						logger.warn( "system try to save subjectAttachment to database got an exception." );
						logger.error(e);
					}
				}
			}
		}
		this.result( response, result );
	}
	
	private BBSSubjectAttachment concreteAttachment( String person, StorageMapping storage, String name, String site ) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		BBSSubjectAttachment attachment = new BBSSubjectAttachment();
		if (StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
			attachment.setExtension(extension);
		}
		if( name.indexOf( "\\" ) >0 ){
			name = StringUtils.substringAfterLast( name, "\\");
		}
		if( name.indexOf( "/" ) >0 ){
			name = StringUtils.substringAfterLast( name, "/");
		}
		attachment.setName(name);
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