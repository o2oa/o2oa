package com.x.cms.assemble.control.servlet.file.download;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.StorageType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.StorageMapping;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.fileinfo.WrapOutFileInfo;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

@WebServlet(urlPatterns = "/servlet/download/*")
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger( DownloadServlet.class );
	private LogService logService = new LogService();

	@HttpMethodDescribe(value = "下载附件 servlet/download/{id}/stream", response = WrapOutFileInfo.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<Object> result = new ActionResult<>();
		boolean check = true;
		String part = null;
		String fileId = null;
		FileInfo fileInfo = null;
		Document document = null;
		boolean streamContentType = false;
		EffectivePerson effectivePerson = null;
		if( check ){
			try {
				effectivePerson = FileUploadServletTools.effectivePerson(request);
			} catch (Exception e) {
				check = false;
				result.error(e);
				response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
				result.setUserMessage( "系统在获取登录用户信息时发生异常！" );
				logger.error( "系统在获取登录用户信息时发生异常！" , e );
			}
		}
		if( check ){
			try {
				part = FileUploadServletTools.getURIPart(request.getRequestURI(), "download");
				fileId = StringUtils.substringBefore(part, "/"); // 附件的ID
			} catch (Exception e) {
				check = false;
				result.error(e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setUserMessage( "系统在解析传入的URL参数时发生异常！" );
				logger.error( "系统在解析传入的URL参数时发生异常！" , e );
			}
		}
		if( check ){
			try {
				//校验文件和文档是正存在
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					fileInfo = emc.find( fileId, FileInfo.class );
					if ( null == fileInfo) {
						throw new Exception( "fileInfo{id" + fileId + "} not existed." );
					}
					if( fileInfo.getDocumentId() != null && !fileInfo.getDocumentId().isEmpty() ){
						document = emc.find( fileInfo.getDocumentId(), Document.class);
						if ( null == document ) {
							throw new Exception("document{id:" + fileInfo.getDocumentId() + "} not existed.");
						}
						// 用户是否有文档的访问权限
						if (!business.documentAllowRead(request, effectivePerson, document.getId())) {
							throw new Exception("person access document{id:" + document.getId() + "} was deined.");
						}
					}
				}
			} catch ( Exception e ) {
				check = false;
				result.error(e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setUserMessage( "系统在检测参数信息有效性时发生异常!" );
				logger.error( "系统在检测参数信息有效性时发生异常!" , e );
			}
		}		
		
		if( check ){
			streamContentType = StringUtils.endsWith( part, "/stream");
			request.setCharacterEncoding("UTF-8");
		}
		
		if( check ){
			//进行文件下载
			StorageMapping mapping = null;
			// 如果缓存中找不到，再从数据库中进行查询				
			if (check) {
				mapping = ThisApplication.storageMappings.get( StorageType.cms, fileInfo.getStorage());
				try {
					this.setResponseHeader( response, streamContentType, fileInfo );
				} catch (Exception e) {
					check = false;
					result.setUserMessage("系统下载文件时设置responseHeader时发生异常。");
					result.error(e);
					logger.error("system set response header got an exception.", e);
				}
			}

			if (check) {
				try {
					fileInfo.readContent( mapping, response.getOutputStream() );
				} catch (Exception e) {
					check = false;
					result.setUserMessage("系统读取文件输出时发生异常。");
					result.error(e);
					logger.error("system read content got an exception.", e);
				}
			}
			
			if( check ){
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					logService.log( emc, effectivePerson.getName(), "成功下载附件信息", fileInfo.getAppId(), fileInfo.getCatagoryId(), fileInfo.getDocumentId(), fileInfo.getId(), "FILE", "下载");
				} catch (Exception e) {
					check = false;
					result.setUserMessage("系统记录下载日志时发生异常。");
					result.error(e);
					logger.error("system record download log got an exception.", e);
				}
			}
		}
		if (!check) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			FileUploadServletTools.result( response, result );
		}
	}

	private void setResponseHeader(HttpServletResponse response, boolean streamContentType, FileInfo fileInfo) throws Exception {
		if ( streamContentType ) {
			response.setHeader("Content-Type", "application/octet-stream");
			if( fileInfo.getFileName() != null && !fileInfo.getFileName().isEmpty() ){
				response.setHeader("Content-Disposition", "fileInfo; filename=" + URLEncoder.encode( fileInfo.getFileName(), "utf-8") );
			}
		} else {
			response.setHeader("Content-Type", Config.mimeTypes().getContentType( fileInfo.getName()) );
			if( fileInfo.getFileName() != null && !fileInfo.getFileName().isEmpty() ){
				response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileInfo.getFileName(), "utf-8"));
			}
		}
		if( fileInfo != null && fileInfo.getLength() != null ){
			response.setIntHeader("Content-Length", fileInfo.getLength().intValue());
		}
	}
}