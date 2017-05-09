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

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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

@WebServlet(urlPatterns = "/servlet/attachment/update/*")
@MultipartConfig
public class UpdateServlet extends AbstractServletAction {

	private static final long serialVersionUID = 5628571943877405247L;

	@HttpMethodDescribe(value = "/servlet/attachment/update/{id} 更新Attachment对象.", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String id = this.getURIPart(request.getRequestURI(), "update");
			Business business = new Business(emc);
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			Meeting meeting = emc.find(attachment.getMeeting(), Meeting.class, ExceptionWhen.not_found);
			business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				if (!item.isFormField()) {
					try (InputStream input = item.openStream()) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								attachment.getStorage());
						attachment.updateContent(mapping, input);
						emc.beginTransaction(Attachment.class);
						emc.persist(attachment);
						emc.commit();
					}
					wrap = new WrapOutId(attachment.getId());
					result.setData(wrap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result(response, result);
	}
}