package com.x.okr.assemble.control.servlet.workattachment;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.StorageMapping;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.entity.OkrAttachmentFileInfo;

@WebServlet(urlPatterns = "/servlet/download/workattachment/*")
public class WorkAttachmentDownloadServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger( WorkAttachmentDownloadServlet.class );

	@HttpMethodDescribe(value = "下载附件 servlet/download/workattachment/{id}/stream", response = Object.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<Object> result = new ActionResult<>();
		EffectivePerson effectivePerson = null;
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		StorageMapping mapping = null;
		String part = null;
		String attachId = null;
		boolean streamContentType = false;
		boolean check = true;

		request.setCharacterEncoding("UTF-8");

		try {
			effectivePerson = this.effectivePerson(request);
		} catch (Exception e ) {
			check = false;
			result.error(e);
			logger.warn("system get attachId from request url got an exception." ); 
			logger.error(e);
		}
		
		// 获取文件ID
		if (check) {
			try {
				part = this.getURIPart(request.getRequestURI(), "workattachment");
				attachId = StringUtils.substringBefore(part, "/");
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get attachId from request url got an exception." );
				logger.error(e);
			}
		}

		/* 确定是否要用application/octet-stream输出 */
		if (check) {
			streamContentType = StringUtils.endsWith(part, "/stream");
			logger.info("streamContentType = " + streamContentType);
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrAttachmentFileInfo = emc.find(attachId, OkrAttachmentFileInfo.class);
				if (null == okrAttachmentFileInfo) {
					check = false;
					Exception exception = new AttachmentNotExistsException( attachId );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttachmentQueryByIdException( e, attachId );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		//文件下载		
		if (check) {
			try {
			mapping = ThisApplication.storageMappings.get( OkrAttachmentFileInfo.class, okrAttachmentFileInfo.getStorage() );
				this.setResponseHeader( response, streamContentType, okrAttachmentFileInfo );
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system set response header got an exception." );
				logger.error(e);
			}
		}

		if (check) {
			try {
				okrAttachmentFileInfo.readContent( mapping, response.getOutputStream() );
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system read content got an exception." );
				logger.error(e);
			}
		}

		if (!check) {
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			this.result( response, result );
		}
	}

	private void setResponseHeader(HttpServletResponse response, boolean streamContentType,
			OkrAttachmentFileInfo okrAttachmentFileInfo) throws Exception {
		if (streamContentType) {
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "fileInfo; filename=" + URLEncoder.encode(okrAttachmentFileInfo.getName(), "utf-8"));
		} else {
			response.setHeader("Content-Type",Config.mimeTypes().getMimeByExtension("." + okrAttachmentFileInfo.getExtension()));
			response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(okrAttachmentFileInfo.getName(), "utf-8"));
		}
		response.setIntHeader("Content-Length", okrAttachmentFileInfo.getLength().intValue());
	}
}