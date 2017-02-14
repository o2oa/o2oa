package com.x.file.assemble.control.servlet.download;

import static com.x.base.core.entity.StorageType.file;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.StorageMapping;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.Attachment;

@WebServlet(urlPatterns = "/servlet/download/*")
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = -4314532091497625540L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			request.setCharacterEncoding(DefaultCharset.name);
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			String part = FileUploadServletTools.getURIPart(request.getRequestURI(), "download");
			String id = StringUtils.substringBefore(part, "/");
			/* 确定是否要用application/octet-stream输出 */
			boolean streamContentType = StringUtils.endsWith(part, "/stream");
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			if (!StringUtils.equals(effectivePerson.getName(), attachment.getPerson())
					&& (!attachment.getShareList().contains(effectivePerson.getName()))
					&& (!attachment.getEditorList().contains(effectivePerson.getName()))) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} access attachment{id:" + id
						+ "} access denied.");
			}
			this.setResponseHeader(response, streamContentType, attachment);
			StorageMapping mapping = ThisApplication.storageMappings.get(file, attachment.getStorage());
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

	private void setResponseHeader(HttpServletResponse response, boolean streamContentType, Attachment attachment)
			throws Exception {
		if (streamContentType) {
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode(attachment.getName(), "utf-8"));
		} else {
			response.setHeader("Content-Type", Config.mimeTypes().getContentType(attachment.getName()));
			response.setHeader("Content-Disposition",
					"inline; filename=" + URLEncoder.encode(attachment.getName(), "utf-8"));
		}
		response.setIntHeader("Content-Length", attachment.getLength().intValue());
	}
}