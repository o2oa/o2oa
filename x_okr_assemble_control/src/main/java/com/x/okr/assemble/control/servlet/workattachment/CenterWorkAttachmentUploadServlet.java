package com.x.okr.assemble.control.servlet.workattachment;

import static com.x.base.core.entity.StorageType.okr;

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
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

/**
 * 附件上传服务
 * @author LIYI
 *
 */
@WebServlet(urlPatterns= "/servlet/upload/center/*" )
@MultipartConfig
public class CenterWorkAttachmentUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( CenterWorkAttachmentUploadServlet.class );
	
	@HttpMethodDescribe(value = "上传附件 servlet/upload/center/{id}", response = Object.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<Object> result = new ActionResult<>();
		List<OkrAttachmentFileInfo> attachments = new ArrayList<OkrAttachmentFileInfo>();
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		EffectivePerson effectivePerson = null;
		ServletFileUpload upload = null;
		FileItemIterator fileItemIterator = null;
		FileItemStream item = null;
		String centerId = null;
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
				centerId = FileUploadServletTools.getURIPart( request.getRequestURI(), "center" );
			}catch(Exception e){
				check = false;
				result.setUserMessage( "系统从URL获取中心工作ID参数时发生异常。" );
				result.error( e );
				logger.error( "system get work id from url got an exception." );
			}
		}
		if( check ){
			try {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
					if ( null == okrCenterWorkInfo ) {
						check = false;
						result.error( new Exception( "中心工作信息不存在:" + centerId ) );
						result.setUserMessage( "中心工作信息不存在。" );
						logger.error( "[UploadServlet]okrCenterWorkInfo{id:" + centerId + "} not existed." );
					}
				}
			}catch(Exception e){
				check = false;
				result.setUserMessage( "系统从数据库中根据工作ID获取工作基础信息时发生异常。" );
				result.error( e );
				logger.error( "[UploadServlet]system get okrCenterWorkInfo{id:" + centerId + "} from database got an exception." );
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
							StorageMapping mapping = ThisApplication.storageMappings.random( okr );
							okrAttachmentFileInfo = concreteAttachment( effectivePerson.getName(), okrCenterWorkInfo, mapping, FileUploadServletTools.getFileName( item.getName() ), site );
							okrAttachmentFileInfo.saveContent( mapping, input, item.getName() );
							attachments.add( okrAttachmentFileInfo );
						}
					}finally{
						input.close();
					}
				}
			}catch(Exception e){
				check = false;
				result.setUserMessage( "系统从数据库中根据中心工作ID获取工作基础信息时发生异常。" );
				result.error( e );
				logger.error( "[UploadServlet]system try to save okrAttachmentFileInfo to Storage got an exception.", e);
			}
		}		
		if( check ){
			if( okrAttachmentFileInfo != null ){
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
					if( okrCenterWorkInfo != null ){
						if( okrCenterWorkInfo.getAttachmentList() == null ) {
							okrCenterWorkInfo.setAttachmentList( new ArrayList<String>());
						}
						emc.beginTransaction( OkrAttachmentFileInfo.class );
						emc.beginTransaction( OkrWorkBaseInfo.class );
						emc.persist( okrAttachmentFileInfo, CheckPersistType.all );
						okrCenterWorkInfo.getAttachmentList().add( okrAttachmentFileInfo.getId() );
						result.setUserMessage( okrAttachmentFileInfo.getId() );
						emc.check( okrCenterWorkInfo, CheckPersistType.all );
						emc.commit();
					}					
				}catch( Exception e ){
					check = false;
					result.setUserMessage( "系统向数据库存储附件和中心工作信息时发生异常。" );
					result.error( e );
					logger.error( "[UploadServlet]system try to save okrAttachmentFileInfo to database got an exception.", e);
				}
			}
		}
		FileUploadServletTools.result( response, result );
	}
	
	private OkrAttachmentFileInfo concreteAttachment( String person, OkrCenterWorkInfo okrCenterWorkInfo, StorageMapping storage, String name, String site ) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		OkrAttachmentFileInfo attachment = new OkrAttachmentFileInfo();
		if (StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
			attachment.setExtension(extension);
		}
		attachment.setFileHost( storage.getHost() );
		attachment.setFilePath( "" );
		attachment.setName( name );
		attachment.setFileName( fileName );
		attachment.setStorageName( storage.getName() );
		attachment.setWorkInfoId( null );
		attachment.setCenterId( okrCenterWorkInfo.getId() );
		attachment.setStatus( "正常" );
		attachment.setParentType( "中心工作" );
		attachment.setKey( okrCenterWorkInfo.getId() );
		attachment.setCreatorUid(person);
		attachment.setCreateTime( new Date() );
		attachment.setSite(site);
		return attachment;
	}
}