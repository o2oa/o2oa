package com.x.processplatform.assemble.surface.servlet.attachment;

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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.Storage;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

@WebServlet(urlPatterns = "/servlet/attachment/update/*")
@MultipartConfig
public class UpdateServlet extends BaseServlet {

	private static final long serialVersionUID = 5628571943877405247L;

	@HttpMethodDescribe(value = "更新Attachment对象.", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			String part = FileUploadServletTools.getURIPart(request.getRequestURI(), "update");
			String id = StringUtils.substringBefore(part, "/work/");
			String workId = StringUtils.substringAfter(part, "/work/");
			Attachment attachment = null;
			Work work = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				work = emc.find(workId, Work.class, ExceptionWhen.not_found);
				if (!work.getAttachmentList().contains(id)) {
					throw new Exception("work{id:" + workId + "} not contains attachment{id:" + id + "}.");
				}
				Control control = business.getControlOfWorkComplex(effectivePerson, work);
				if (BooleanUtils.isNotTrue(control.getAllowProcessing())) {
					throw new Exception("person access work{id:" + workId + "} was deined.");
				}
				attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
				if (business.attachment().multiReferenced(attachment)) {
					throw new Exception(
							"attachment{id:" + attachment.getId() + "} referenced by multi work, can not update.");
				}
			}
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				try (InputStream input = item.openStream()) {
					if (!item.isFormField()) {
						StorageMapping mapping = ThisApplication.storageMappings
								.random(Attachment.class.getAnnotation(Storage.class).type());
						try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
							emc.beginTransaction(Attachment.class);
							// emc.beginTransaction(AttachmentLog.class);
							attachment = emc.find(id, Attachment.class);
							attachment.updateContent(mapping, input);
							attachment.setLastUpdatePerson(effectivePerson.getName());
							// AttachmentLog attachmentLog =
							// this.concreteAttachmentLog(attachment);
							// attachmentLog.setAttachmentLogType(AttachmentLogType.update);
							// emc.persist(attachmentLog, CheckPersistType.all);
							emc.commit();
						}
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