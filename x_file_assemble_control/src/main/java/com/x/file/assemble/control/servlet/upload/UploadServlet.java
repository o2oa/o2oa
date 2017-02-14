package com.x.file.assemble.control.servlet.upload;

import static com.x.base.core.entity.StorageType.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
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
import com.x.file.core.entity.Folder;

@WebServlet(urlPatterns = "/servlet/upload/*")
@MultipartConfig
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 5628571943877405247L;

	@HttpMethodDescribe(value = "创建Attachment对象.", response = WrapOutId.class)
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<List<WrapOutId>> result = new ActionResult<>();
		List<WrapOutId> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String folderId = null;
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			folderId = FileUploadServletTools.getURIPart(request.getRequestURI(), "folder");
			if (!StringUtils.isEmpty(folderId)) {
				Folder folder = emc.find(folderId, Folder.class, ExceptionWhen.not_found);
				if (!StringUtils.equals(folder.getPerson(), effectivePerson.getName())) {
					throw new Exception("person{name:" + effectivePerson.getName() + "} access folder{id:" + folderId
							+ "} was denied.");
				}
				folderId = folder.getId();
			} else {
				folderId = null;
			}
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				try (InputStream input = item.openStream()) {
					if (!item.isFormField()) {
						StorageMapping mapping = ThisApplication.storageMappings.random(file);
						Attachment attachment = new Attachment();
						attachment.setFolder(folderId);
						attachment.setPerson(effectivePerson.getName());
						attachment.setLastUpdatePerson(effectivePerson.getName());
						attachment.saveContent(mapping, input, FilenameUtils.getName(item.getName()));
						emc.beginTransaction(Attachment.class);
						emc.persist(attachment, CheckPersistType.all);
						emc.commit();
						wraps.add(new WrapOutId(attachment.getId()));
					}
				}
			}
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		FileUploadServletTools.result(response, result);
	}
}