package com.x.cms.assemble.control.servlet.file.upload;

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
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

@WebServlet( urlPatterns = "/servlet/upload/document/*" )
@MultipartConfig
public class UploadServlet extends AbstractServletAction {

	private Logger logger = LoggerFactory.getLogger( UploadServlet.class );
	private static final long serialVersionUID = 5628571943877405247L;
	private LogService logService = new LogService();

	@HttpMethodDescribe(value = "上传附件 servlet/upload/document/{documentId}", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<List<WrapOutId>> result = new ActionResult< List<WrapOutId> >();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		DocumentInfoServiceAdv documentInfoServiceAdv = new DocumentInfoServiceAdv();
		List<WrapOutId> wraps = new ArrayList<>();
		WrapOutId wrap = null;
		String site = null;
		Boolean check = true;
		String documentId = null;
		Document document = null;
		ServletFileUpload upload = null;
		FileItemIterator fileItemIterator = null;
		FileItemStream item = null;
		FileInfo fileInfo = null;
		String name = null;
		InputStream input = null;
		
		request.setCharacterEncoding("UTF-8");
		
		if ( !ServletFileUpload.isMultipartContent( request ) ) {
			check = false;
			result.error( new Exception("not mulit part request.") );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
		
		if( check ){
			try{
				documentId = this.getURIPart( request.getRequestURI(), "document" );
			}catch( Exception e ){
				check = false;
				Exception exception = new URLParameterGetException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null );
				response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			}
		}
		
		if( check ){
			//判断文档是否已经存在
			try {
				document = documentInfoServiceAdv.get( documentId );
				if ( null == document ) {
					throw new Exception( "document{id:" + documentId + "} not existed." );
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
					try {
						input = item.openStream();
						if ( item.isFormField() ) {
							String str = Streams.asString(input);
							if ( StringUtils.equals( name, "site" ) ) {
								site = str;
							}
						} else {
							wrap = saveAttachmetFile( effectivePerson.getName(), document, item, site, input );
							wraps.add( wrap );
						}
					}finally{
						input.close();
					}
				}
				if( wraps != null && !wraps.isEmpty() && site!=null && !site.isEmpty() ){
					for( WrapOutId _wrap : wraps ){
						try( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();){
							fileInfo = emc.find( _wrap.getId(), FileInfo.class);
							emc.beginTransaction( FileInfo.class );
							fileInfo.setSite(site);
							emc.check( fileInfo, CheckPersistType.all);
							emc.commit();
						}
					}
				}
				
				result.setData(wraps);
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

	private WrapOutId saveAttachmetFile( String personName, Document document, FileItemStream item, String site, InputStream input ) throws Exception {
		WrapOutId wrap = null;
		String name = null;
		FileInfo fileInfo = null;
		EntityManagerContainer emc = null;
		StorageMapping mapping = null;
		
		if( item != null ){
			
			emc = EntityManagerContainerFactory.instance().create();
			
			document = emc.find( document.getId(), Document.class);
			if( document.getHasIndexPic() == null ){
				document.setHasIndexPic( false );
			}
			emc.beginTransaction( FileInfo.class );
			emc.beginTransaction( Document.class );
			mapping = ThisApplication.context().storageMappings().random( FileInfo.class );
			
			fileInfo = concreteFileInfo( personName, document, mapping, this.getFileName( item.getName() ), site );
			name = fileInfo.getName();
			
			//先检查对象是否能够被保存，如果能保存，再进行文件存储
			emc.check( fileInfo, CheckPersistType.all);
			
			fileInfo.saveContent( mapping, input, item.getName() );
			
			if( document.getAttachmentList() == null ){
				document.setAttachmentList( new ArrayList<>() );
			}
			if( !document.getAttachmentList().contains( fileInfo.getId() ) ){
				document.getAttachmentList().add( fileInfo.getId() );
			}
			fileInfo.setName(name);
			emc.check( document, CheckPersistType.all);
			emc.persist( fileInfo, CheckPersistType.all );
			
			wrap = new WrapOutId( fileInfo.getId());
			
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