package com.x.attendance.assemble.control.servlet.download;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo.WrapOutAttendanceImportFileInfo;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;

@WebServlet(urlPatterns = "/servlet/download/*")
public class DownloadServlet extends AbstractServletAction {

	private static final long serialVersionUID = -4314532091497625540L;
	private Logger logger = LoggerFactory.getLogger(DownloadServlet.class);

	@HttpMethodDescribe(value = "下载附件 servlet/download/{id}/stream", response = WrapOutAttendanceImportFileInfo.class)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AttendanceImportFileInfo fileInfo = null;
		boolean streamContentType = false;
		request.setCharacterEncoding("UTF-8");
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			logger.debug("[downloadFile]用户" + effectivePerson.getName() + "尝试下载数据文件.....");
			String part = this.getURIPart(request.getRequestURI(), "download");
			logger.debug("[downloadFile]截取URL, part=[" + part + "]");
			// 从URI里截取需要的信息
			String id = StringUtils.substringBefore(part, "/"); // 附件的ID
			logger.debug("[downloadFile]从URL中获取文件的ID, id=[" + id + "]");
			// part = StringUtils.substringAfter(part, "/");
			logger.debug("[downloadFile]从URL中获取part, part=[" + part + "]");

			streamContentType = StringUtils.endsWith(part, "/stream");
			logger.info("[downloadFile]用application/octet-stream输出，streamContentType=" + streamContentType);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				OutputStream output = response.getOutputStream();
				logger.debug("[downloadFile]系统尝试从数据库中根据ID进行查询......");
				fileInfo = emc.find(id, AttendanceImportFileInfo.class);
				if (fileInfo != null) {
					logger.debug("[downloadFile]成功从数据库中查询到文件信息：filename=" + fileInfo.getFileName());
					try {
						byte[] buffer = fileInfo.getFileBody();
						logger.debug("[downloadFile]设置浏览器响应头......");
						this.setResponseHeader(response, streamContentType, fileInfo);
						output.write(buffer, 0, buffer.length);
						output.flush();
					} catch (Exception e) {
						logger.info("[downloadFile]数据文件流向浏览器输出时发生异常！");
						e.printStackTrace();
					} finally {
						if (output != null) {
							output.close();
						}
					}
				} else {
					logger.info("[downloadFile]系统未能从数据库中查询到文件信息attendanceImportFileInfo{'id':'" + id + "'}");
					ActionResult<Object> result = new ActionResult<>();
					result.error(new Exception("系统未能从数据库中查询到文件信息attendanceImportFileInfo{'id':'" + id + "'}"));
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					this.result(response, result);
				}
			}
		} catch (Exception e) {
			logger.info("[downloadFile]用户下载数据文件发生未知异常！");
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}

	private void setResponseHeader(HttpServletResponse response, boolean streamContentType,
			AttendanceImportFileInfo fileInfo) throws Exception {
		if (streamContentType) {
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition",
					"fileInfo; filename=" + URLEncoder.encode(fileInfo.getFileName(), "utf-8"));
		} else {
			response.setHeader("Content-Type", Config.mimeTypes().getMimeByExtension("." + fileInfo.getExtension()));
			response.setHeader("Content-Disposition",
					"inline; filename=" + URLEncoder.encode(fileInfo.getFileName(), "utf-8"));
		}
		response.setIntHeader("Content-Length", fileInfo.getLength().intValue());
	}
}