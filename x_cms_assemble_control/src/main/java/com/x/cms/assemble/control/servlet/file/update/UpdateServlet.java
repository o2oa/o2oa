package com.x.cms.assemble.control.servlet.file.update;

import static com.x.base.core.entity.StorageType.cms;

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
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

@WebServlet(urlPatterns = "/servlet/update/*")
@MultipartConfig
public class UpdateServlet extends HttpServlet {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( UpdateServlet.class );
	private LogService logService = new LogService();

	@HttpMethodDescribe(value = "更新FileInfo对象:/servlet/update/{id}/document/{documentId}", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		FileInfo fileInfo = null;
		FileInfo fileInfo_new = null;
		Document document = null;
		String documentId = null;
		String id = null;
		String part = null;
		StorageMapping mapping = null;
		EffectivePerson effectivePerson = null;
		FileItemIterator fileItemIterator = null;
		ServletFileUpload upload = null;
		FileItemStream item = null;	
		InputStream input = null;
		String site = null;
		String name = null;
		List<FileInfo> attachments = new ArrayList<FileInfo>();
		boolean check = true;
		
		if( check ){
			try{
				effectivePerson = FileUploadServletTools.effectivePerson(request);
				request.setCharacterEncoding("UTF-8");
				if (!ServletFileUpload.isMultipartContent(request)) {
					throw new Exception("not multi part request.");
				}
				part = FileUploadServletTools.getURIPart(request.getRequestURI(), "update");
				id = StringUtils.substringBefore(part, "/document/");
				documentId = StringUtils.substringAfter(part, "/document/");
			}catch(Exception e){
				check = false;
				result.error(e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setUserMessage( "系统在解析传入的URL参数时发生异常！" );
			}
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);

				logger.debug("系统尝试根据ID查询文档信息是否存在, documentId=" + documentId);
				document = emc.find(documentId, Document.class);
				if (null == document) {
					throw new Exception("document{id:" + documentId + "} not existed.");
				}
				logger.debug("系统尝试文档信息的附件列表信息判断是否包括需要更新的附件信息ID, id=" + id);
				if (!document.getAttachmentList().contains(id)) {
					throw new Exception("document{id:" + documentId + "} not contains fileInfo{id:" + id + "}.");
				}
				logger.debug("系统判断用户中否拥有该文档的修改保存权限, person=" + effectivePerson.getName());
				if (!business.documentAllowSave(request, effectivePerson, document.getId())) {
					throw new Exception("person access document{id:" + documentId + "} was deined.");
				}
				logger.debug("系统尝试从数据库根据ID查询附件文件信息, id=" + id);
				fileInfo = emc.find(id, FileInfo.class);
				if (null == fileInfo) {
					throw new Exception("fileInfo{id:" + id + "} not existed.");
				}
			}catch( Exception e ){
				check = false;
				result.error(e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setUserMessage( "系统在校验传入的参数合法性时发生异常！" );
			}
		}
		
		if( check ){
			try{
				upload = new ServletFileUpload();
				fileItemIterator = upload.getItemIterator(request);
			}catch( Exception e ){
				check = false;
				result.error(e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setUserMessage( "系统在获取传递的文件时发生异常！" );
			}
		}
		
		if( check ){
			//先保存新的附件
			try{
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
							mapping = ThisApplication.storageMappings.random( cms );
							fileInfo_new = this.concreteAttachment( effectivePerson.getName(), document, mapping, FileUploadServletTools.getFileName(item.getName()), site );
							fileInfo_new.saveContent( mapping, input, item.getName() );
							attachments.add( fileInfo_new );
						}
					}finally{
						input.close();
					}
				}
			}catch( Exception e ){
				check = false;
				result.error(e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setUserMessage( "系统在保存新的文件时发生异常！" );
			}
		}
		
		if( check ){
			// 将所有的附件信息存储到数据库里
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.beginTransaction(FileInfo.class);
				emc.beginTransaction(Document.class);
				document = emc.find( documentId, Document.class );
				for (FileInfo o : attachments) {
					emc.persist(o, CheckPersistType.all);
					document.getAttachmentList().add(o.getId());
				}
				emc.commit();
			} catch (Exception e) {
				check = false;
				result.error(e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setUserMessage( "系统在保存新的文件信息记录时发生异常！" );
			}
		}
		
		if( check ){
			//再删除原来的附件
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				mapping = ThisApplication.storageMappings.random( cms );
				fileInfo.deleteContent(mapping);
				Business business = new Business(emc);
				
				ApplicationCache.notify( FileInfo.class );
				
				emc.beginTransaction( FileInfo.class );
				emc.beginTransaction( Document.class);
				if( document != null && document.getAttachmentList() != null ){
					document.getAttachmentList().remove( fileInfo.getId() );
				}
				emc.remove( fileInfo, CheckRemoveType.all );
				emc.commit();
				//成功删除一个附件信息
				logService.log( emc, effectivePerson.getName(), "成功更新一个附件信息", fileInfo.getAppId(), fileInfo.getId(), fileInfo.getDocumentId(), fileInfo.getId(), "FILE", "删除" );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
				logger.error("系统在根据ID删除文件时发生异常！");
			}
		}
		FileUploadServletTools.result(response, result);
	}
	
	private FileInfo concreteAttachment(String person, Document document, StorageMapping storage, String name, String site )
			throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension(name);
		FileInfo attachment = new FileInfo();
		if (StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
			attachment.setExtension(extension);
		}
		attachment.setFileHost("");
		attachment.setFilePath("");
		attachment.setFileType("ATTACHMENT");
		attachment.setName(name);
		attachment.setFileName(fileName);
		attachment.setStorageName(storage.getName());
		attachment.setAppId(document.getAppId());
		attachment.setCatagoryId(document.getCatagoryId());
		attachment.setDocumentId(document.getId());
		attachment.setCreatorUid(person);
		attachment.setCreateTime(new Date());
		attachment.setSite(site);
		return attachment;
	}
	
}