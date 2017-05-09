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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.cms.assemble.control.jaxrs.image.exception.Base64EncodeException;
import com.x.cms.assemble.control.jaxrs.image.exception.ImageIsNullException;
import com.x.cms.assemble.control.jaxrs.image.exception.LoadImageFromURLException;
import com.x.cms.assemble.control.jaxrs.image.exception.URLEmptyException;
import com.x.cms.assemble.control.jaxrs.image.exception.URLInvalidException;
import com.x.cms.assemble.control.jaxrs.image.exception.WrapInConvertException;

@Path("image/encode")
public class ImageBase64Action extends AbstractJaxrsAction {
	
	private Logger logger = LoggerFactory.getLogger( ImageBase64Action.class );
	
	@Path("base64")
	@HttpMethodDescribe(value = "将URL指向的图片转换成base64String", request = JsonElement.class , response = String.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response convert( @Context HttpServletRequest request, JsonElement jsonElement ) { 
		ActionResult<String> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInImage wrapIn = null;
		String wrap = null;
		URL url = null;
		BufferedImage image = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInImage.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			if( wrapIn.getUrl() != null || wrapIn.getUrl().isEmpty() ){
				check = false;
				Exception exception = new URLEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getSize() != null || wrapIn.getSize() == 0 ){
				wrapIn.setSize(800);
			}
		}
		if( check ){
			try {
				url = new URL( wrapIn.getUrl() );
			} catch ( MalformedURLException e ) {
				check = false;
				Exception exception = new URLInvalidException();
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				image = ImageIO.read( url );
				if( image == null ){
					check = false;
					Exception exception = new ImageIsNullException( url.toString() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (IOException e) {
				check = false;
				Exception exception = new LoadImageFromURLException( e, url.toString() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			int width = image.getWidth();
			int height = image.getHeight();
			if( width * height > wrapIn.getSize() * wrapIn.getSize() ){
				image = Scalr.resize( image, wrapIn.getSize() );
			}
		}
		if( check ){
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write( image, "png", baos );
				wrap = Base64.encodeBase64String(baos.toByteArray());
				result.setData(wrap);
			} catch ( Exception e ) {
				check = false;
				Exception exception = new Base64EncodeException( e, url.toString() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}