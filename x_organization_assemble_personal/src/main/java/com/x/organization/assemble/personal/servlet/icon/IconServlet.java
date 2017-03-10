package com.x.organization.assemble.personal.servlet.icon;

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
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

@WebServlet("/servlet/icon")
@MultipartConfig
public class IconServlet extends AbstractServletAction {

	private static final long serialVersionUID = -516827649716075968L;

	@HttpMethodDescribe(value = "更新用户Icon", response = WrapOutId.class)
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			EffectivePerson effectivePerson = this.effectivePerson(request);
			if (!Config.token().isInitialManager(effectivePerson.getName())) {
				/* 非静态管理员 */
				Business business = new Business(emc);
				String id = business.person().getWithName(effectivePerson.getName());
				if (StringUtils.isNotEmpty(id)) {
					Person person = emc.find(id, Person.class, ExceptionWhen.not_found);
					ServletFileUpload upload = new ServletFileUpload();
					FileItemIterator fileItemIterator = upload.getItemIterator(request);
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
									wrap = new WrapOutId(person.getId());
									ApplicationCache.notify(Person.class, person.getId());
								}
							}
						}
					}
				}
			} else {
				/* 静态管理员 */
				wrap = new WrapOutId(Config.token().initialManagerInstance().getId());
			}
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result(response, result);
	}
}