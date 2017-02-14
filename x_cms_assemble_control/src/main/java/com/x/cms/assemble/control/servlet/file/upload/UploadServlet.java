package com.x.cms.assemble.control.servlet.file.upload;

import static com.x.base.core.entity.StorageType.cms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

@WebServlet(urlPatterns = "/servlet/upload/document/*")
@MultipartConfig
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( UploadServlet.class );
	private LogService logService = new LogService();

	@HttpMethodDescribe(value = "上传附件 servlet/upload/document/{documentId}", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<List<WrapOutId>> result = new ActionResult<List<WrapOutId>>();
		List<WrapOutId> wraps = new ArrayList<WrapOutId>();
		try {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("[UploadServlet]not mulit part request.");
			}
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			logger.debug("获取操作用户帐号：person=" + effectivePerson.getName());
			String documentId = FileUploadServletTools.getURIPart(request.getRequestURI(), "document");
			Document document = null;
			logger.debug("[UploadServlet]用户[" + effectivePerson.getName() + "]正在尝试上传附件，文档ID=[" + documentId + "]。");
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				// 从数据库里根据documentId查询出文档的信息，只查询出几个属性
				document = emc.fetchAttribute(documentId, Document.class, "appId", "catagoryId", "id");
				if (null == document) {
					throw new Exception("[UploadServlet]document{id:" + documentId + "} not existed.");
				}
				/* 判断用户是否可以上传附件 */
				if (!business.documentAllowSave(request, effectivePerson, documentId)) {
					throw new Exception("[UploadServlet]person{name:" + effectivePerson.getName() + "} access document{id:" + documentId + "} was denied.");
				}
			}
			/* 附件分类信息 */
			String site = null;
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			List<FileInfo> attachments = new ArrayList<FileInfo>();
			logger.debug("[UploadServlet]系统正在尝试保存附件内容......");
			FileItemStream item = null;
			String name = null;
			FileInfo fileInfo = null;
			InputStream input = null;

			while (fileItemIterator.hasNext()) {
				item = fileItemIterator.next();
				name = item.getFieldName();
				try {
					input = item.openStream();
					if (item.isFormField()) {
						String str = Streams.asString(input);
						if (StringUtils.equals(name, "site")) {
							site = str;
						}
					} else {
						StorageMapping mapping = ThisApplication.storageMappings.random(cms);
						fileInfo = this.concreteAttachment(effectivePerson.getName(), document, mapping,
								FileUploadServletTools.getFileName(item.getName()), site);
						fileInfo.saveContent(mapping, input, item.getName());
						attachments.add(fileInfo);
					}
				} finally {
					input.close();
				}
			}
			// 将所有的附件信息存储到数据库里
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);
				emc.beginTransaction(FileInfo.class);
				emc.beginTransaction(Document.class);
				document = emc.find(documentId, Document.class);
				for (FileInfo o : attachments) {
					emc.persist(o, CheckPersistType.all);
					document.getAttachmentList().add(o.getId());
					wraps.add(new WrapOutId(o.getId()));
				}
				emc.commit();
				for (FileInfo o : attachments) {
					logService.log( emc, effectivePerson.getName(), "[UploadServlet]成功上传附件信息", o.getAppId(), o.getCatagoryId(), o.getDocumentId(), o.getId(), "FILE", "上传");
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

	private FileInfo concreteAttachment(String person, Document document, StorageMapping storage, String name,
			String site) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension(name);
		FileInfo attachment = new FileInfo();
		if (StringUtils.isNotEmpty(extension)) {
			fileName = fileName + "." + extension;
			attachment.setExtension(extension);
		}
		attachment.setFileHost("");
		attachment.setFilePath("");
		attachment.setFileType("ATTACHMENT");
		attachment.setName(name);
		attachment.setFileName(fileName);
		attachment.setStorageName(storage.getName());
		attachment.setAppId(document.getAppId());
		attachment.setCatagoryId(document.getCatagoryId());
		attachment.setDocumentId(document.getId());
		attachment.setCreatorUid(person);
		attachment.setCreateTime(new Date());
		attachment.setSite(site);
		return attachment;
	}
}