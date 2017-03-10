package com.x.bbs.assemble.control.servlet.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSSectionInfoService;
import com.x.bbs.entity.BBSSectionInfo;

@WebServlet(urlPatterns = "/servlet/section/*")
@MultipartConfig
public class SectionIconServlet extends AbstractServletAction {

	private static final long serialVersionUID = -516827649716075968L;
	private Logger logger = LoggerFactory.getLogger(SectionIconServlet.class);
	private BBSSectionInfoService sectionInfoService = new BBSSectionInfoService();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();

	@HttpMethodDescribe(value = "更新SectionInfo中的icon图标: /servlet/section/{id}/icon", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		Boolean check = true;
		String part = null;
		String sectionId = null;
		String icon = null;
		BBSSectionInfo sectionInfo = null;
		FileItemIterator fileItemIterator = null;
		request.setCharacterEncoding("UTF-8");
		EffectivePerson effectivePerson = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (!ServletFileUpload.isMultipartContent(request)) {
			check = false;
			result.error(new Exception("not mulit part request."));
		}

		if (check) {
			try {
				effectivePerson = this.effectivePerson(request);
			} catch (Exception e) {
				check = false;
				result.error(new Exception("获取登录用户信息发生异常，请重新登录BBS应用！"));
				logger.error(e);
			}
		}

		if (check) {
			try {
				part = this.getURIPart(request.getRequestURI(), "section");
				sectionId = StringUtils.substringBefore(part, "/icon");
				icon = null;
			} catch (Exception e) {
				check = false;
				result.error(new Exception("获取并且解析传入的参数时发生异常！"));
				logger.error(e);
			}
		}

		if (check) {
			try {
				sectionInfo = sectionInfoService.get(sectionId);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system query section info with id got an exception!id:" + sectionId);
				logger.error(e);
			}
		}

		if (check) {
			if (sectionInfo == null) {
				check = false;
				result.error(new Exception("版块信息不存在，无法继续进行图标更新操作！ID：" + sectionId));
			}
		}

		if (check) {
			try {
				fileItemIterator = new ServletFileUpload().getItemIterator(request);
			} catch (FileUploadException e) {
				check = false;
				result.error(new Exception("从请求中获取图片文件信息发生异常！"));
			}
		}

		if (check) {
			try {
				while (fileItemIterator.hasNext()) {
					FileItemStream item = fileItemIterator.next();
					try (InputStream input = item.openStream()) {
						if (!item.isFormField()) {
							ByteArrayOutputStream output = new ByteArrayOutputStream();
							BufferedImage image = ImageIO.read(input);
							BufferedImage scalrImage = Scalr.resize(image, 72, 72);
							ImageIO.write(scalrImage, "png", output);
							icon = Base64.encodeBase64String(output.toByteArray());
						}
					}
				}
			} catch (FileUploadException e) {
				check = false;
				result.error(e);
				logger.warn("system get image from request got an exception.");
				logger.error(e);
			}
		}

		if (check) {
			if (icon == null || icon.isEmpty()) {
				check = false;
				result.error(new Exception("系统未能从请求中获取到任何图片信息！"));
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				sectionInfo = emc.find(sectionId, BBSSectionInfo.class);
				if (sectionInfo != null) {
					emc.beginTransaction(BBSSectionInfo.class);
					sectionInfo.setIcon(icon);
					emc.commit();
					operationRecordService.sectionIconOperation(effectivePerson.getName(), sectionInfo, "UPLOAD",
							hostIp, hostName);
				} else {
					check = false;
					result.error(new Exception("版块信息不存在，无法保存版块图信息！ID：" + sectionId));
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system save image for section icon got an exception.");
				logger.error(e);
			}
		}
		this.result(response, result);
	}
}