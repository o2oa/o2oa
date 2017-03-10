package com.x.okr.assemble.control.servlet.workattachment;

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
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

/**
 * 附件上传服务
 * @author LIYI
 *
 */
@WebServlet(urlPatterns= "/servlet/upload/work/*" )
@MultipartConfig
public class WorkAttachmentUploadServlet extends AbstractServletAction {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( WorkAttachmentUploadServlet.class );
	
	@HttpMethodDescribe(value = "上传附件 servlet/upload/work/{id}", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		List<OkrAttachmentFileInfo> attachments = new ArrayList<OkrAttachmentFileInfo>();
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		EffectivePerson effectivePerson = null;
		ServletFileUpload upload = null;
		FileItemIterator fileItemIterator = null;
		FileItemStream item = null;
		String workId = null;
		String name = null;
		String site = null;
		boolean check = true;
		InputStream input = null;
		request.setCharacterEncoding( "UTF-8" );
		
		if (!ServletFileUpload.isMultipartContent(request)) {
			check = false;
			logger.warn("not mulit part request." ); 
			result.error( new Exception( "请求不是Multipart，无法获取文件信息。" ) );
		}
		if ( check ) {
			try {
				effectivePerson = this.effectivePerson( request );
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get effectivePerson from request url got an exception." ); 
				logger.error(e);
			}
		}
		if( check ){
			try {
				workId = this.getURIPart( request.getRequestURI(), "work" );
			}catch(Exception e){
				check = false;
				result.error(e);
				logger.warn("system get workId from request url got an exception." );
				logger.error(e);
			}
		}
		if( check ){
			try {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
					if ( null == okrWorkBaseInfo ) {
						check = false;
						Exception exception = new WorkNotExistsException( workId );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}catch(Exception e){
				check = false;
				Exception exception = new WorkQueryByIdException( e, workId );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
							String str = Streams.asString(input);
							if ( StringUtils.equals( name, "site" ) ) {
								site = str;
							}
						} else {
							StorageMapping mapping = ThisApplication.storageMappings.random( OkrAttachmentFileInfo.class );
							okrAttachmentFileInfo = concreteAttachment( effectivePerson.getName(), okrWorkBaseInfo, mapping, this.getFileName( item.getName() ), site );
							okrAttachmentFileInfo.saveContent( mapping, input, item.getName() );
							attachments.add( okrAttachmentFileInfo );
						}
					}finally{
						input.close();
					}
				}
			}catch(Exception e){
				check = false;
				logger.warn( "[UploadServlet]system try to save okrAttachmentFileInfo to Storage got an exception." );
				logger.error(e);
			}
		}
		
		if( check ){
			if( okrAttachmentFileInfo != null ){
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
					if( okrWorkBaseInfo != null ){
						if( okrWorkBaseInfo.getAttachmentList() == null ) {
							okrWorkBaseInfo.setAttachmentList( new ArrayList<String>());
						}
						emc.beginTransaction( OkrAttachmentFileInfo.class );
						emc.beginTransaction( OkrWorkBaseInfo.class );
						emc.persist( okrAttachmentFileInfo, CheckPersistType.all );
						okrWorkBaseInfo.getAttachmentList().add( okrAttachmentFileInfo.getId() );
						emc.check( okrWorkBaseInfo, CheckPersistType.all );
						emc.commit();
					}
					result.setData( new WrapOutId( okrAttachmentFileInfo.getId()));
				}catch( Exception e ){
					check = false;
					result.error( e );
					logger.warn( "[UploadServlet]system try to save okrAttachmentFileInfo to database got an exception." );
					logger.error(e);
				}
			}
		}
		this.result( response, result );
	}
	
	private OkrAttachmentFileInfo concreteAttachment( String person, OkrWorkBaseInfo okrWorkBaseInfo, StorageMapping storage, String name, String site ) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		OkrAttachmentFileInfo attachment = new OkrAttachmentFileInfo();
		if (StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
			attachment.setExtension(extension);
		}
		attachment.setFileHost( storage.getHost() );
		attachment.setFilePath( "" );
		if( name.indexOf( "\\" ) >0 ){
			name = StringUtils.substringAfterLast( name, "\\");
		}
		if( name.indexOf( "/" ) >0 ){
			name = StringUtils.substringAfterLast( name, "/");
		}
		attachment.setName( name );
		attachment.setFileName( fileName );
		attachment.setStorageName( storage.getName() );
		attachment.setWorkInfoId( okrWorkBaseInfo.getId() );
		attachment.setCenterId( okrWorkBaseInfo.getCenterId() );
		attachment.setStatus( "正常" );
		attachment.setParentType( "工作" );
		attachment.setKey( okrWorkBaseInfo.getId() );
		attachment.setCreatorUid(person);
		attachment.setCreateTime( new Date() );
		attachment.setSite(site);
		return attachment;
	}
}