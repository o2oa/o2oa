package com.x.organization.assemble.control.jaxrs.person;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

class ActionSetIcon extends BaseAction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSetIcon.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Person person = business.person().pick(flag);
			if (null == person) {
				throw new ExceptionPersonNotExist(flag);
			}
			if (!business.editable(effectivePerson, person)) {
				throw new ExceptionDenyEditPerson(effectivePerson, person.getName());
			}
			// 从内存中pick出来的无法作为实体保存
			person = emc.find(person.getId(), Person.class);
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ByteArrayOutputStream baos_m = new ByteArrayOutputStream();
					ByteArrayOutputStream baos_l = new ByteArrayOutputStream()) {
				BufferedImage image = ImageIO.read(bais);

				BufferedImage scalrImage = Scalr.resize(image, 144, 144);
				ImageIO.write(scalrImage, "png", baos);
				String icon = Base64.encodeBase64String(baos.toByteArray());

				BufferedImage scalrImage_m = Scalr.resize(image, 72, 72);
				ImageIO.write(scalrImage_m, "png", baos_m);
				String icon_m = Base64.encodeBase64String(baos_m.toByteArray());

				BufferedImage scalrImage_l = Scalr.resize(image, 36, 36);
				ImageIO.write(scalrImage_l, "png", baos_l);
				String icon_l = Base64.encodeBase64String(baos_l.toByteArray());

				emc.beginTransaction(Person.class);
				person.setIcon(icon);
				person.setIconMdpi(icon_m);
				person.setIconLdpi(icon_l);

				emc.commit();
				CacheManager.notify(Person.class);
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
			}
			/* 通知x_collect_service_transmit同步数据到collect */
			business.instrument().collect().person();
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
