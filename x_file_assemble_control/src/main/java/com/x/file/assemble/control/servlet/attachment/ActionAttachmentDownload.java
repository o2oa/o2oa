package com.x.file.assemble.control.servlet.attachment;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;

@WebServlet(urlPatterns = "/servlet/attachment/download/*")
public class ActionAttachmentDownload extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;

	@HttpMethodDescribe(value = "创建Attachment对象./servlet/attachment/download/{id}")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			request.setCharacterEncoding(DefaultCharset.name);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String id = this.getURIPart(request.getRequestURI(), "download");
			/* 确定是否要用application/octet-stream输出 */
			boolean streamContentType = StringUtils.endsWith(request.getRequestURI(), "/stream");
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			if (!StringUtils.equals(effectivePerson.getName(), attachment.getPerson())
					&& (!attachment.getShareList().contains(effectivePerson.getName()))
					&& (!attachment.getEditorList().contains(effectivePerson.getName()))) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} access attachment{id:" + id
						+ "} access denied.");
			}
			this.setResponseHeader(response, attachment, streamContentType);
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			if (null == mapping) {
				throw new StorageMappingNotExistedException(attachment.getStorage());
			}
			OutputStream output = response.getOutputStream();
			attachment.readContent(mapping, output);
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().print(result.toJson());
		}

	}
}