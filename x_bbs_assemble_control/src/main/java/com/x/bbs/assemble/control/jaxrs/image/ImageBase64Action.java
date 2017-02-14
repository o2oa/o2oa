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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;


@Path("image/encode")
public class ImageBase64Action extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( ImageBase64Action.class );

	@Path("base64")
	@HttpMethodDescribe(value = "将URL指向的图片转换成base64String", response = String.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response convert( @Context HttpServletRequest request, WrapInImage wrapIn ) { 
		ActionResult<String> result = new ActionResult<>();
		String wrap = null;
		URL url = null;
		BufferedImage image = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("parameter is null!") );
				result.setUserMessage( "系统未获取到参数信息,无法转换互联网图片信息!" );
			}
		}
		if( check ){
			if( wrapIn.getUrl() != null || wrapIn.getUrl().isEmpty() ){
				check = false;
				result.error( new Exception("picture url is null!") );
				result.setUserMessage( "系统未获取到参数url,无法获取互联网图片信息!" );
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
				result.error( e );
				result.setUserMessage( "图片地址URL不合法,无法获取互联网图片信息!" );
				logger.error( "picture url is invalid!url:" + url, e );
			}
		}
		if( check ){
			try {
				image = ImageIO.read( url );
				if( image == null ){
					check = false;
					result.error( new Exception("system can not read image in url.") );
					result.setUserMessage( "系统未能从图片地址URL中获也不能任何图片信息!" );
				}
			} catch (IOException e) {
				check = false;
				result.error( e );
				result.setUserMessage( "从指定的URL获取图片信息发生异常!" );
				logger.error( "system read picture with url got an exception!url:" + url, e );
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
				result.setUserMessage( "系统对图片进行base64编码转换时发生异常!" );
				logger.error( "system encode picture in base64 got an exception!", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}