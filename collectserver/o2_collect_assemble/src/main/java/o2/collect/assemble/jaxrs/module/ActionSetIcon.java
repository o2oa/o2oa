package o2.collect.assemble.jaxrs.module;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.core.entity.Module;

class ActionSetIcon extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Module module = emc.find(id, Module.class);
			if (null == module) {
				throw new ExceptionEntityNotExist(id, Module.class);
			}
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
					ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				BufferedImage image = ImageIO.read(bais);
				BufferedImage scalrImage = Scalr.resize(image, Method.QUALITY, 144, 144);
				ImageIO.write(scalrImage, "png", baos);
				emc.beginTransaction(Module.class);
				String icon = Base64.encodeBase64String(baos.toByteArray());
				module.setIcon(icon);
				emc.commit();
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
				ApplicationCache.notify(Module.class);
			}
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}