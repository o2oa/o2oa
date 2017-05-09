package com.x.cms.assemble.control.servlet.file.update;

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
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.FileInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

@WebServlet(urlPatterns = "/servlet/update/*")
@MultipartConfig
public class UpdateServlet extends AbstractServletAction {

	private Logger logger = LoggerFactory.getLogger( UpdateServlet.class );
	private static final long serialVersionUID = 5628571943877405247L;
	private LogService logService = new LogService();
	
	@HttpMethodDescribe(value = "更新FileInfo对象:/servlet/update/{id}/document/{documentId}", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		DocumentInfoServiceAdv documentInfoServiceAdv = new DocumentInfoServiceAdv();
		FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
		
		List<WrapOutId> wraps = new ArrayList<>();
		WrapOutId wrap = null;
		EffectivePerson effectivePerson = null;
		String site = null;
		Boolean check = true;
		String documentId = null;
		Document document = null;
		ServletFileUpload upload = null;
		FileItemIterator fileItemIterator = null;
		FileItemStream item = null;
		String name = null;
		InputStream input = null;
		String id = null;
		FileInfo fileInfo = null;
		
		if( check ){
			try{
				effectivePerson = this.effectivePerson( request );
				request.setCharacterEncoding("UTF-8");
				if (!ServletFileUpload.isMultipartContent(request)) {
					throw new Exception("not multi part request.");
				}
				id = this.getURIPart(request.getRequestURI(), "update");
				documentId = this.getURIPart(request.getRequestURI(), "document");
			}catch(Exception e){
				check = false;
				Exception exception = new URLParameterGetException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		
		if( check ){
			//判断文档是否已经存在
			try {
				document = documentInfoServiceAdv.get( documentId );
				if ( null == document ) {
					throw new Exception( "document{id:" + documentId + "} not existed." );
				}
				if ( !document.getAttachmentList().contains(id) ) {
					throw new Exception("document{id:" + documentId + "} not contains fileInfo{id:" + id + "}.");
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null );
				response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			}
		}
		
		if( check ){
			//判断文档是否已经存在
			try {
				fileInfo = fileInfoServiceAdv.get( id );
				if ( null == fileInfo ) {
					throw new Exception( "fileInfo{id:" + id + "} not existed." );
				}
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
					input = item.openStream();
					if ( item.isFormField() ) {
						String str = Streams.asString(input);
						if ( StringUtils.equals( name, "site" ) ) {
							site = str;
						}
					} else {
						wrap = updateAttachmetFile( id, effectivePerson.getName(), document, item, site, input );
						wraps.add( wrap );
					}
				}
				
				if( wraps != null && !wraps.isEmpty() && site!=null && !site.isEmpty() ){
					for( WrapOutId _wrap : wraps ){
						try( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();){
							fileInfo = emc.find( _wrap.getId(), FileInfo.class);
							if( fileInfo != null ){
								emc.beginTransaction( FileInfo.class );
								fileInfo.setSite(site);
								emc.check( fileInfo, CheckPersistType.all);
								emc.commit();
							}
						}
					}
				}
				
			} catch ( Exception e ) {
				result.error( e );
				logger.error( e, effectivePerson, request, null );
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} finally {
				if( input != null ){
					input.close();
				}
			}
		}
		this.result(response, result);
	}

	private WrapOutId updateAttachmetFile( String fileId, String personName, Document document, FileItemStream item, String site, InputStream input ) throws Exception {
		WrapOutId wrap = null;
		FileInfo fileInfo = null;
		FileInfo fileInfo_old = null;
		EntityManagerContainer emc = null;
		StorageMapping mapping = null;
		
		if( item != null ){
			
			emc = EntityManagerContainerFactory.instance().create();
			document = emc.find( document.getId(), Document.class);
			
			emc.beginTransaction( FileInfo.class );
			emc.beginTransaction( Document.class );
			mapping = ThisApplication.context().storageMappings().random( FileInfo.class );
			
			fileInfo = concreteFileInfo( personName, document, mapping, this.getFileName( item.getName() ), site );
			//先检查对象是否能够被保存，如果能保存，再进行新的文件存储
			emc.check( fileInfo, CheckPersistType.all);	
			
			//将新的文件保存到存储系统
			fileInfo.saveContent( mapping, input, item.getName() );
			
			//将新的附件ID加入到文档的附件列表中
			if( document.getAttachmentList() == null ){
				document.setAttachmentList( new ArrayList<>() );
			}
			if( !document.getAttachmentList().contains( fileInfo.getId() ) ){
				document.getAttachmentList().add( fileInfo.getId() );
			}
			
			//尝试删除原来的附件记录对象
			fileInfo_old = emc.find( fileId, FileInfo.class );
			fileInfo_old.deleteContent( mapping );
			//从文档附件列表中删除该附件的ID
			document.getAttachmentList().remove( fileId );
			emc.remove( fileInfo_old, CheckRemoveType.all );	
			
			emc.check( document, CheckPersistType.all);
			emc.persist( fileInfo, CheckPersistType.all );
			wrap = new WrapOutId( fileInfo.getId() );
			logService.log( emc, personName, fileInfo.getName(), fileInfo.getAppId(), fileInfo.getCategoryId(), fileInfo.getDocumentId(), fileInfo.getId(), "FILE", "上传");
			emc.commit();
			ApplicationCache.notify( FileInfo.class );
			ApplicationCache.notify( Document.class );
		}
		return wrap;
		
	}
	
	private FileInfo concreteFileInfo( String person, Document document, StorageMapping storage, String name, String site) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		FileInfo attachment = new FileInfo();
		if ( StringUtils.isEmpty(extension) ) {
			throw new Exception("file extension is empty.");
		}else{
			fileName = fileName + "." + extension;
		}
		if (name.indexOf("\\") > 0) {
			name = StringUtils.substringAfterLast(name, "\\");
		}
		if (name.indexOf("/") > 0) {
			name = StringUtils.substringAfterLast(name, "/");
		}
		attachment.setCreateTime( new Date() );
		attachment.setLastUpdateTime( new Date() );
		attachment.setExtension( extension );
		attachment.setName( name );
		attachment.setFileName( fileName );
		attachment.setStorage( storage.getName() );
		attachment.setAppId( document.getAppId() );
		attachment.setCategoryId( document.getCategoryId() );
		attachment.setDocumentId( document.getId() );
		attachment.setCreatorUid( person );
		attachment.setSite( site );
		attachment.setFileHost( "" );
		attachment.setFilePath( "" );
		attachment.setFileType("ATTACHMENT");
		
		return attachment;
	}
	
}