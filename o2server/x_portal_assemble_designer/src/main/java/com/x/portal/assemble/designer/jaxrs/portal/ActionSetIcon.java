package com.x.portal.assemble.designer.jaxrs.portal;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

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
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;

class ActionSetIcon extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String protalId, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				InputStream input = new ByteArrayInputStream(bytes);
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Portal portal = emc.find(protalId, Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(protalId);
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new PortalInsufficientPermissionException(effectivePerson.getDistinguishedName(),
						portal.getName(), portal.getId());
			}
			BufferedImage image = ImageIO.read(input);
			BufferedImage scalrImage = Scalr.resize(image, 72, 72);
			ImageIO.write(scalrImage, "png", output);
			String icon = Base64.encodeBase64String(output.toByteArray());
			emc.beginTransaction(Portal.class);
			portal.setIcon(icon);
			portal.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			portal.setLastUpdateTime(new Date());
			emc.commit();
			Wo wo = new Wo();
			CacheManager.notify(Portal.class);
			wo.setId(portal.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
