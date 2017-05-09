package com.x.cms.assemble.control.servlet.file.download;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.fileinfo.WrapOutFileInfo;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.assemble.control.servlet.file.download.exception.DownloadLogSaveException;
import com.x.cms.assemble.control.servlet.file.download.exception.EffectivePersonGetException;
import com.x.cms.assemble.control.servlet.file.download.exception.FileInfoContentReadException;
import com.x.cms.assemble.control.servlet.file.download.exception.ResponseHeaderSetException;
import com.x.cms.assemble.control.servlet.file.download.exception.URLParameterGetException;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

@WebServlet(urlPatterns = "/servlet/download/*")
public class DownloadServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger(DownloadServlet.class);

	@HttpMethodDescribe(value = "下载附件 servlet/download/{id}/stream", response = WrapOutFileInfo.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<Object> result = new ActionResult<>();
		boolean check = true;
		String fileId = null;
		FileInfo fileInfo = null;
		Document document = null;
		boolean streamContentType = false;
		EffectivePerson effectivePerson = null;
		if (check) {
			try {
				effectivePerson = this.effectivePerson(request);
			} catch (Exception e) {
				check = false;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				Exception exception = new EffectivePersonGetException(e);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				fileId = this.getURIPart(request.getRequestURI(), "download");
			} catch (Exception e) {
				check = false;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				Exception exception = new URLParameterGetException(e);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				// 校验文件和文档是正存在
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					fileInfo = emc.find(fileId, FileInfo.class);
					if (null == fileInfo) {
						throw new Exception("fileInfo{id" + fileId + "} not existed.");
					}
					if (fileInfo.getDocumentId() != null && !fileInfo.getDocumentId().isEmpty()) {
						document = emc.find(fileInfo.getDocumentId(), Document.class);
						if ( null == document ) {
							throw new Exception("document{id:" + fileInfo.getDocumentId() + "} not existed.");
						}
						// 用户是否有文档的访问权限
						if ( !business.documentAllowRead(request, effectivePerson, document.getId()) ) {
							throw new Exception("person access document{id:" + document.getId() + "} was deined.");
						}
					}
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.error(e, effectivePerson, request, null);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}

		if (check) {
			streamContentType = StringUtils.endsWith(request.getRequestURI(), "/stream");
			request.setCharacterEncoding("UTF-8");
		}

		if (check) {
			// 进行文件下载
			StorageMapping mapping = null;
			// 如果缓存中找不到，再从数据库中进行查询
			if (check) {
				try {
					mapping = ThisApplication.context().storageMappings().get( FileInfo.class, fileInfo.getStorage() );
					this.setResponseHeader( response, fileInfo, streamContentType );
					response.setContentType("text/x-msdownload");
				} catch (Exception e) {
					check = false;
					Exception exception = new ResponseHeaderSetException(e);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}

			if (check) {
				try {
					fileInfo.readContent( mapping, response.getOutputStream() );
				} catch (Exception e) {
					check = false;
					Exception exception = new FileInfoContentReadException(e);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					this.result(response, result);
				}
			}

			if (check) {
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					new LogService().log(emc, effectivePerson.getName(), fileInfo.getName(), fileInfo.getAppId(), fileInfo.getCategoryId(), fileInfo.getDocumentId(), fileInfo.getId(), "FILE", "下载");
				} catch (Exception e) {
					check = false;
					Exception exception = new DownloadLogSaveException(e);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					this.result(response, result);
				}
			}
		}
	}

}