package com.x.query.assemble.designer.jaxrs.query;

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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ImageTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;

class ActionSetIcon extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Query query = emc.flag(flag, Query.class );
			if (null == query) {
				throw new ExceptionQueryNotExist(flag);
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName(),
						query.getId());
			}
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
					ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				BufferedImage image = ImageIO.read(bais);
				BufferedImage scalrImage = Scalr.resize(image, 72, 72);
				ImageIO.write(scalrImage, "png", baos);
				String icon = Base64.encodeBase64String(baos.toByteArray());
				String iconHue = ImageTools.hue(scalrImage);
				emc.beginTransaction(Query.class);
				query.setIcon(icon);
				query.setIconHue(iconHue);
				emc.commit();
				CacheManager.notify(Query.class);
				Wo wo = new Wo();
				wo.setId(query.getId());
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}