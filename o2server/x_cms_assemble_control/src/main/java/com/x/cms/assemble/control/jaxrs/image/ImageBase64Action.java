package com.x.cms.assemble.control.jaxrs.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ExceptionWrapInConvert;

@Path("image/encode")
public class ImageBase64Action extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ImageBase64Action.class);

	@Path("base64")
	// @HttpMethodDescribe(value = "灏哢RL鎸囧悜鐨勫浘鐗囪浆鎹㈡垚base64String", request =
	// JsonElement.class , response = String.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void convert( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<String> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInImage wrapIn = null;
		String wrap = null;
		URL url = null;
		BufferedImage image = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WrapInImage.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (wrapIn.getUrl() != null || wrapIn.getUrl().isEmpty()) {
				check = false;
				Exception exception = new ExceptionURLEmpty();
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (wrapIn.getSize() != null || wrapIn.getSize() == 0) {
				wrapIn.setSize(800);
			}
		}
		if (check) {
			try {
				url = new URL(wrapIn.getUrl());
			} catch (MalformedURLException e) {
				check = false;
				Exception exception = new ExceptionURLInvalid();
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				image = ImageIO.read(url);
				if (image == null) {
					check = false;
					Exception exception = new ExceptionImageIsNull(url.toString());
					result.error(exception);
					// logger.error( e, effectivePerson, request, null);
				}
			} catch (IOException e) {
				check = false;
				Exception exception = new ExceptionLoadImageFromURL(e, url.toString());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			int width = image.getWidth();
			int height = image.getHeight();
			if (width * height > wrapIn.getSize() * wrapIn.getSize()) {
				image = Scalr.resize(image, wrapIn.getSize());
			}
		}
		if (check) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(image, "png", baos);
				wrap = Base64.encodeBase64String(baos.toByteArray());
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionBase64Encode(e, url.toString());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}