package com.x.bbs.assemble.control.servlet.attachment;

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
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.StorageMapping;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.entity.BBSSubjectAttachment;

@WebServlet(urlPatterns = "/servlet/download/subjectattachment/*")
public class SubjectAttachmentDownloadServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger(SubjectAttachmentDownloadServlet.class);

	@HttpMethodDescribe(value = "下载附件 servlet/download/subjectattachment/{id}/stream", response = Object.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<Object> result = new ActionResult<>();
		BBSSubjectAttachment subjectAttachment = null;
		StorageMapping mapping = null;
		String part = null;
		String attachId = null;
		boolean streamContentType = false;
		boolean check = true;

		request.setCharacterEncoding("UTF-8");

		if (check) {
			try {
				part = this.getURIPart(request.getRequestURI(), "subjectattachment");
				attachId = StringUtils.substringBefore(part, "/");
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get id from request url got an exception.");
				logger.error(e);
			}
		}

		if (check) {
			streamContentType = StringUtils.endsWith(part, "/stream");
			logger.info("streamContentType = " + streamContentType);
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				subjectAttachment = emc.find(attachId, BBSSubjectAttachment.class);
				if (null == subjectAttachment) {
					check = false;
					result.error(new Exception("文件信息不存在:" + attachId));
					logger.warn("subjectAttachment{'id':'" + attachId + "'} not existed.");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get subjectAttachment{'id':'" + attachId + "'} got an exception.", e);
				logger.error(e);
			}
		}
		// 文件下载
		if (check) {
			try {
				mapping = ThisApplication.storageMappings.get(BBSSubjectAttachment.class,
						subjectAttachment.getStorage());
				if (mapping == null) {
					check = false;
					logger.warn("bbs mapping is null.storage:" + subjectAttachment.getStorage());
				} else {
					this.setResponseHeader(response, streamContentType, subjectAttachment);
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system set response header got an exception.");
				logger.error(e);
			}
		}
		if (check) {
			try {
				subjectAttachment.readContent(mapping, response.getOutputStream());
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system read content got an exception.");
				logger.error(e);
			}
		}

		if (!check) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}

	private void setResponseHeader(HttpServletResponse response, boolean streamContentType,
			BBSSubjectAttachment subjectAttachment) throws Exception {
		if (streamContentType) {
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition",
					"fileInfo; filename=" + URLEncoder.encode(subjectAttachment.getName(), "utf-8"));
		} else {
			response.setHeader("Content-Type",
					Config.mimeTypes().getMimeByExtension("." + subjectAttachment.getExtension()));
			response.setHeader("Content-Disposition",
					"inline; filename=" + URLEncoder.encode(subjectAttachment.getName(), "utf-8"));
		}
		response.setIntHeader("Content-Length", subjectAttachment.getLength().intValue());
	}
}