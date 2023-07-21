package com.x.meeting.assemble.control.jaxrs.room;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Room;

class ActionSetPhoto extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Room room = emc.find(id, Room.class);
			if (null == room) {
				throw new ExceptionRoomNotExist(id);
			}
			if (!business.roomEditAvailable(effectivePerson, room)) {
				throw new ExceptionRoomAccessDenied(effectivePerson, room.getName());
			}
			try (InputStream input = new ByteArrayInputStream(bytes);
					ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				BufferedImage image = ImageIO.read(input);
				BufferedImage scalrImage = Scalr.resize(image, 512, 512);
				ImageIO.write(scalrImage, "png", baos);
				emc.beginTransaction(Room.class);
				String str = Base64.encodeBase64String(baos.toByteArray());
				room.setPhoto(str);
				emc.commit();
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}