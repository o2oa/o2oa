package com.x.meeting.assemble.control.servlet.attachment;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

@WebServlet(urlPatterns = "/servlet/attachment/upload/meeting/*")
@MultipartConfig
public class UploadServlet extends AbstractServletAction {

	private static final long serialVersionUID = 5628571943877405247L;

	@HttpMethodDescribe(value = "/servlet/attachment/upload/meeting/{id}创建Attachment对象,/servlet/attachment/meeting/upload/{id}/summary 创建Attachment summary对象.", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not mulit part request.");
			}
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String part = this.getURIPart(request.getRequestURI(), "meeting");
			String meetingId = part;
			Boolean summary = false;
			if (part.endsWith("/summary")) {
				summary = true;
				meetingId = StringUtils.substringBeforeLast(part, "/summary");
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Meeting meeting = emc.find(meetingId, Meeting.class, ExceptionWhen.not_found);
				business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator fileItemIterator = upload.getItemIterator(request);
				while (fileItemIterator.hasNext()) {
					FileItemStream item = fileItemIterator.next();
					if (!item.isFormField()) {
						try (InputStream input = item.openStream()) {
							StorageMapping mapping = ThisApplication.context().storageMappings()
									.random(Attachment.class);
							emc.beginTransaction(Attachment.class);
							Attachment attachment = this.concreteAttachment(meeting, summary);
							attachment.saveContent(mapping, input, FilenameUtils.getName(item.getName()));
							emc.persist(attachment, CheckPersistType.all);
							emc.commit();
							wrap = new WrapOutId(attachment.getId());
						}
					}
				}
			}
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result(response, result);
	}

	private Attachment concreteAttachment(Meeting meeting, Boolean summary) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setMeeting(meeting.getId());
		attachment.setSummary(summary);
		return attachment;
	}

}