package com.x.meeting.assemble.control.servlet.room;

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
import org.imgscalr.Scalr;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Room;

@WebServlet("/servlet/room/*")
@MultipartConfig
public class RoomServlet extends HttpServlet {

	private static final long serialVersionUID = 4202924267632769560L;

	@HttpMethodDescribe(value = "更新Room的Photo", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			Business business = new Business(emc);
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			String id = FileUploadServletTools.getURIPart(request.getRequestURI(), "room", "photo");
			Room room = emc.find(id, Room.class, ExceptionWhen.not_found);
			business.roomEditAvailable(effectivePerson, room, ExceptionWhen.not_allow);
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				try (InputStream input = item.openStream()) {
					if (!item.isFormField()) {
						try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
							BufferedImage image = ImageIO.read(input);
							BufferedImage scalrImage = Scalr.resize(image, 512, 512);
							ImageIO.write(scalrImage, "png", baos);
							emc.beginTransaction(Room.class);
							String str = Base64.encodeBase64String(baos.toByteArray());
							room.setPhoto(str);
							emc.commit();
							wrap = new WrapOutId(room.getId());
						}
					}
				}
			}
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		FileUploadServletTools.result(response, result);
	}
}