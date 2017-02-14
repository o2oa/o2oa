package com.x.processplatform.assemble.designer.servlet.application.icon;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;

@WebServlet("/servlet/application/*")
@MultipartConfig
public class ApplicationIconServlet extends HttpServlet {

	private static final long serialVersionUID = -516827649716075968L;

	@HttpMethodDescribe(value = "更新Application中的icon图标.", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not mulit part request.");
			}
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			String part = FileUploadServletTools.getURIPart(request.getRequestURI(), "application");
			String applicationId = StringUtils.substringBefore(part, "/icon");
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Application application = emc.find(applicationId, Application.class,ExceptionWhen.not_found);
				business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
				FileItemIterator fileItemIterator = new ServletFileUpload().getItemIterator(request);
				while (fileItemIterator.hasNext()) {
					FileItemStream item = fileItemIterator.next();
					try (InputStream input = item.openStream()) {
						if (!item.isFormField()) {
							try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
								BufferedImage image = ImageIO.read(input);
								BufferedImage scalrImage = Scalr.resize(image, 72, 72);
								ImageIO.write(scalrImage, "png", output);
								String icon = Base64.encodeBase64String(output.toByteArray());
								emc.beginTransaction(Application.class);
								application.setIcon(icon);
								emc.commit();
								wrap = new WrapOutId(application.getId());
								result.setData(wrap);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		FileUploadServletTools.result(response, result);
	}
}