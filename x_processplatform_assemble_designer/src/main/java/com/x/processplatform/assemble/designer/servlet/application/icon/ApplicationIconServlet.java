package com.x.processplatform.assemble.designer.servlet.application.icon;

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
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;

@WebServlet("/servlet/application/*")
@MultipartConfig
public class ApplicationIconServlet extends AbstractServletAction {

	private static Logger logger = LoggerFactory.getLogger(ApplicationIconServlet.class);

	private static final long serialVersionUID = -516827649716075968L;

	@HttpMethodDescribe(value = "更新Application中的icon图标.", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson effectivePerson = null;
		try {
			effectivePerson = this.effectivePerson(request);
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not mulit part request.");
			}
			String part = this.getURIPart(request.getRequestURI(), "application");
			String applicationId = StringUtils.substringBefore(part, "/icon");
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Application application = emc.find(applicationId, Application.class);
				if (null == application) {
					throw new ApplicationNotExistedException(applicationId);
				}
				if (!business.applicationEditAvailable(effectivePerson, application)) {
					throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
							application.getId());
				}
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
			logger.error(e, effectivePerson, request, null);
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result(response, result);
	}
}