package com.x.cms.assemble.control.jaxrs.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutString;

public class ActionImageBase64Encode extends BaseAction {

	protected ActionResult<WrapOutString> execute( HttpServletRequest request, EffectivePerson effectivePerson, 
			Integer size, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try ( InputStream input = new ByteArrayInputStream(bytes)) {
			BufferedImage image = ImageIO.read(input);
			int width = image.getWidth();
			int height = image.getHeight();
			if (size > 0) {
				if (width * height > size * size) {
					image = Scalr.resize(image, size);
				}
			}
			ImageIO.write(image, "png", baos);
			byte[] byteArray = baos.toByteArray();
			WrapOutString wrap = new WrapOutString();
			wrap.setValue(Base64.encodeBase64String(byteArray));
			result.setData( wrap);
		} catch (IOException e) {
			e.printStackTrace();
			result.error(e);
		}
		return result;
	}
}
