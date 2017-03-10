package com.x.bbs.assemble.control.jaxrs.image;

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
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;


@Path("image/encode")
public class ImageBase64Action extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( ImageBase64Action.class );

	@Path("base64")
	@HttpMethodDescribe(value = "将URL指向的图片转换成base64String", response = String.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response convert( @Context HttpServletRequest request, JsonElement jsonElement ) { 
		ActionResult<String> result = new ActionResult<>();
		WrapInImage wrapIn = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
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
			logger.error( exception, currentPerson, request, null);
		}
		if( check ){
			if( wrapIn.getUrl() != null || wrapIn.getUrl().isEmpty() ){
				check = false;
				Exception exception = new URLEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
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
				Exception exception = new URLEmptyException( e, wrapIn.getUrl() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				image = ImageIO.read( url );
				if( image == null ){
					check = false;
					result.error( new Exception("system can not read image in url.") );
				}
			} catch (IOException e) {
				check = false;
				result.error( e );
				logger.warn( "system read picture with url got an exception!url:" + url );
				logger.error(e);
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
				result.error( e );
				logger.warn( "system encode picture in base64 got an exception!" );
				logger.error(e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}