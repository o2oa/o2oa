package com.x.file.assemble.control.servlet.update;

import static com.x.base.core.entity.StorageType.file;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.Attachment;

@WebServlet(urlPatterns = "/servlet/update/*")
@MultipartConfig
public class UpdateServlet extends HttpServlet {

	private static final long serialVersionUID = 5628571943877405247L;

	@HttpMethodDescribe(value = "创建Attachment对象.", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			String id = FileUploadServletTools.getURIPart(request.getRequestURI(), "update");
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			if ((!StringUtils.equals(effectivePerson.getName(), attachment.getPerson()))
					&& (!attachment.getEditorList().contains(effectivePerson.getName()))) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} access attachment{id:" + id
						+ "} access denied.");
			}
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				try (InputStream input = item.openStream()) {
					if (item.isFormField()) {
						/* ignore */
					} else {
						StorageMapping mapping = ThisApplication.storageMappings.get(file, attachment.getStorage());
						emc.beginTransaction(Attachment.class);
						attachment.setLastUpdatePerson(effectivePerson.getName());
						attachment.updateContent(mapping, input);
						emc.check(attachment, CheckPersistType.all);
						emc.commit();
					}
				}
				wrap = new WrapOutId(attachment.getId());
				result.setData(wrap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		FileUploadServletTools.result(response, result);
	}
}