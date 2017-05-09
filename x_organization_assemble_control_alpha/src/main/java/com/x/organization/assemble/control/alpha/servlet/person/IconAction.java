package com.x.organization.assemble.control.alpha.servlet.person;

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
import org.imgscalr.Scalr;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Person;

@WebServlet(urlPatterns = "/servlet/person/*")
@MultipartConfig
public class IconAction extends AbstractServletAction {

	private static final long serialVersionUID = 4202924267632769560L;

	private static Logger logger = LoggerFactory.getLogger(IconAction.class);

	@HttpMethodDescribe(value = "更新Person的icon图标,url=/servlet/person/{id}/icon", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		wrap.setValue(false);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			this.setCharacterEncoding(request, response);
			if (!this.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			String id = this.getURIPart(request.getRequestURI(), "person");
			Person person = emc.find(id, Person.class);
			if (null == person) {
				throw new Exception("person{id:" + id + "} not existed.");
			}
			if (!business.personUpdateAvailable(effectivePerson, person)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			FileItemIterator fileItemIterator = this.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				try (InputStream input = item.openStream()) {
					if (!item.isFormField()) {
						try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
							BufferedImage image = ImageIO.read(input);
							BufferedImage scalrImage = Scalr.resize(image, 72, 72);
							ImageIO.write(scalrImage, "png", baos);
							emc.beginTransaction(Person.class);
							String icon = Base64.encodeBase64String(baos.toByteArray());
							person.setIcon(icon);
							emc.commit();
							ApplicationCache.notify(Person.class);
							wrap.setValue(true);
						}
					}
				}
			}
			result.setData(wrap);
		} catch (Exception e) {
			logger.error(e);
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result(response, result);
	}
}