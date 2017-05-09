package com.x.file.assemble.control.servlet.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;

@WebServlet(urlPatterns = "/servlet/attachment/update/*")
@MultipartConfig
public class ActionAttachmentUpdate extends AbstractServletAction {

	private static final long serialVersionUID = 5628571943877405247L;

	@HttpMethodDescribe(value = "创建Attachment对象./servlet/attachment/update/{id}", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.setCharacterEncoding(request, response);
			if (!this.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String id = this.getURIPart(request.getRequestURI(), "update");
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
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								attachment.getStorage());
						if (null == mapping) {
							throw new StorageMappingNotExistedException(attachment.getStorage());
						}
						attachment.setLastUpdatePerson(effectivePerson.getName());
						/** 禁止不带扩展名的文件上传 */
						if (StringUtils.isEmpty(FilenameUtils.getExtension(item.getName()))) {
							throw new EmptyExtensionException(item.getName());
						}
						/** 不允许不同的扩展名上传 */
						if (!Objects.equals(StringUtils.lowerCase(FilenameUtils.getExtension(item.getName())),
								attachment.getExtension())) {
							throw new ExtensionNotMatchException(item.getName(), attachment.getExtension());
						}
						emc.beginTransaction(Attachment.class);
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
		this.result(response, result);
	}
}