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
import com.x.base.core.cache.ApplicationCache;
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
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;

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
	
	@HttpMethodDescribe(value = "上传附件 servlet/upload/subject/{subjectId}", response = WrapOutId.class)
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		ActionResult<List<WrapOutId>> result = new ActionResult<>();
		BBSSubjectAttachment subjectAttachment = null;
		List<WrapOutId> wraps = new ArrayList<>();
		WrapOutId wrap = null;
		BBSSubjectInfo subject = null;
		EffectivePerson effectivePerson = null;
		ServletFileUpload upload = null;
		FileItemIterator fileItemIterator = null;
		FileItemStream item = null;
		String subjectId = null;
		String name = null;
		String site = null;
		Boolean check = true;
		InputStream input = null;
		
		BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
		
		request.setCharacterEncoding( "UTF-8" );
		
		if (!ServletFileUpload.isMultipartContent(request)) {
			check = false;
			result.error( new Exception( "请求不是Multipart，无法获取文件信息。" ) );
			logger.warn( "not mulit part request." );
		}
		
		if( check ){
			try{
				subjectId = this.getURIPart( request.getRequestURI(), "subject" );
				if( subjectId == null || subjectId.isEmpty() ){
					check = false;
					Exception exception = new URLParameterGetException( new Exception("未获取到主贴ID") );
					result.error( exception );
					response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new URLParameterGetException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null );
				response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			}
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
			//判断文档是否已经存在
			try {
				subject = subjectInfoServiceAdv.get( subjectId );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null );
				response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
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
							wrap = saveAttachmetFile( effectivePerson.getName(), subject, item, site, input );
							wraps.add( wrap );
							
						}
					}finally{
						input.close();
					}
				}
				
				if( wraps != null && !wraps.isEmpty() && site!=null && !site.isEmpty() ){
					for( WrapOutId _wrap : wraps ){
						try( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();){
							subjectAttachment = emc.find( _wrap.getId(), BBSSubjectAttachment.class);
							emc.beginTransaction( BBSSubjectAttachment.class );
							subjectAttachment.setSite(site);
							emc.check( subjectAttachment, CheckPersistType.all);
							emc.commit();
						}
					}
				}
				
				result.setData( wraps );
			}catch(Exception e){
				check = false;
				result.error( e );
				logger.warn( "system try to save subjectAttachment to Storage got an exception." );
				logger.error(e);
			}
		}

		this.result( response, result );
	}
	
	private WrapOutId saveAttachmetFile( String personName, BBSSubjectInfo subject, FileItemStream item, String site, InputStream input ) throws Exception {
		WrapOutId wrap = null;
		String name = null;
		BBSSubjectAttachment subjectAttachment = null;
		EntityManagerContainer emc = null;
		StorageMapping mapping = null;
		
		if( item != null ){
			
			emc = EntityManagerContainerFactory.instance().create();
			
			if( subject != null ){
				subject = emc.find( subject.getId(), BBSSubjectInfo.class);
			}
			
			emc.beginTransaction( BBSSubjectAttachment.class );
			
			mapping = ThisApplication.context().storageMappings().random( BBSSubjectAttachment.class );
			
			subjectAttachment = concreteAttachment( personName, subject, mapping, this.getFileName( item.getName() ), site );
			name = subjectAttachment.getName();
			//先检查对象是否能够被保存，如果能保存，再进行文件存储
			emc.check( subjectAttachment, CheckPersistType.all);
			
			subjectAttachment.saveContent( mapping, input, item.getName() );	
			if( subject != null ){
				if( subject.getAttachmentList() == null ){
					subject.setAttachmentList( new ArrayList<>() );
				}
				if( !subject.getAttachmentList().contains( subjectAttachment.getId() ) ){
					subject.getAttachmentList().add( subjectAttachment.getId() );
				}
				emc.check( subject, CheckPersistType.all);
			}
			subjectAttachment.setName(name);
			emc.persist( subjectAttachment, CheckPersistType.all );
			
			wrap = new WrapOutId( subjectAttachment.getId());
			
			emc.commit();
			ApplicationCache.notify( BBSSubjectAttachment.class );
			if( subject != null ){
				ApplicationCache.notify( BBSSubjectInfo.class );
			}
		}
		return wrap;
	}
	
	private BBSSubjectAttachment concreteAttachment( String person, BBSSubjectInfo subject, StorageMapping storage, String name, String site ) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		BBSSubjectAttachment attachment = new BBSSubjectAttachment();
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
		attachment.setFileHost( storage.getHost() );
		attachment.setFilePath( "" );
		attachment.setStorage( storage.getName() );
		attachment.setSite(site);
		attachment.setCreateTime( new Date() );
		attachment.setCreatorUid( person );
		if( subject != null ){
			attachment.setDescription( subject.getTitle() );
		}
		attachment.setLastUpdateTime( new Date() );
		attachment.setLength( 0L );
		return attachment;
	}
}