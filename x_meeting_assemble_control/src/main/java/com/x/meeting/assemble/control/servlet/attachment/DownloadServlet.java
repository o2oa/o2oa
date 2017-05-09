package com.x.meeting.assemble.control.servlet.attachment;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.StorageMapping;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

@WebServlet(urlPatterns = "/servlet/attachment/download/*")
public class DownloadServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;

	@HttpMethodDescribe(value = "下载附件 servlet/attachment/{id}/download 输出Content-Type, servlet/attachment/{id}/download/stream 流文件")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setCharacterEncoding("UTF-8");
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String part = this.getURIPart(request.getRequestURI(), "download");
			String id = part;
			Boolean stream = false;
			if (StringUtils.endsWith(part, "/stream")) {
				id = StringUtils.substringBeforeLast(part, "/stream");
				stream = true;
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
				Meeting meeting = emc.find(attachment.getMeeting(), Meeting.class, ExceptionWhen.not_found);
				business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
				OutputStream output = response.getOutputStream();
				this.setResponseHeader(response, stream, attachment);
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						attachment.getStorage());
				attachment.readContent(mapping, output);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}

	private void setResponseHeader(HttpServletResponse response, boolean streamContentType, Attachment attachment)
			throws Exception {
		if (streamContentType) {
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode(attachment.getName(), "utf-8"));
		} else {
			response.setHeader("Content-Type", Config.mimeTypes().getMimeByExtension("." + attachment.getExtension()));
			response.setHeader("Content-Disposition",
					"inline; filename=" + URLEncoder.encode(attachment.getName(), "utf-8"));
		}
		response.setIntHeader("Content-Length", attachment.getLength().intValue());
	}
}