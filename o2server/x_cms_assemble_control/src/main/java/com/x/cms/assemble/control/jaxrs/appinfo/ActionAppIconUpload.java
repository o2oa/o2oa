package com.x.cms.assemble.control.jaxrs.appinfo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.fileinfo.URLParameterGetException;
import com.x.cms.common.image.maincolor.ImageMainColorUtil;
import com.x.cms.core.entity.AppInfo;

public class ActionAppIconUpload extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionAppIconUpload.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, 
			String appId, Integer size, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<Wo> result = new ActionResult<>();
		AppInfo appInfo = null;
		ImageMainColorUtil imageUtil = new ImageMainColorUtil();
		List<String> colorList = null;
		String iconMainColor = null;
		String base64 = null;
		Boolean check = true;

		if( size == null|| size== 0 ){
			size = 72;
		}

		if( check ){
			if( StringUtils.isEmpty(appId) ){
				check = false;
				Exception exception = new URLParameterGetException( new Exception("appId can not be empty!") );
				result.error( exception );
			}
		}
		
		if( check ){//判断栏目信息是否已经存在
			try {
				appInfo = appInfoServiceAdv.get( appId );
				if (null == appInfo) {
					check = false;
					Exception exception = new ExceptionAppInfoNotExists( appId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		if( check ){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try ( InputStream input = new ByteArrayInputStream(bytes)) {
				BufferedImage image = ImageIO.read(input);
				int height = image.getHeight(null);
				int width = image.getWidth(null);
				//计算新的宽高
				double step = 0;
				if(height > width ) {
					if( height > size ) {
						step = (double)size/(double)height;
						height = size;
						width = (int)(width * step);
					}
				}else {
					if( width > size ) {
						step = (double)size/(double)width;
						width = size;
						height = (int)(height * step);
					}
				}
				//先取图片主色调
				colorList = imageUtil.getColorSolution( image, 30, 1);
				if( colorList != null && !colorList.isEmpty() ){
					iconMainColor = colorList.get(0);
				}
				
				image = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, width, height, Scalr.OP_ANTIALIAS);

				//再获取图片base64编码信息
				ImageIO.write(image, "png", baos);
				base64 = Base64.encodeBase64String(baos.toByteArray());
			} catch (IOException e) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		if( check ){
			try {
				appInfoServiceAdv.saveAppInfoIcon( appId, base64, iconMainColor );
				ApplicationCache.notify( AppInfo.class );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null );
			}
		}
		
		return result;
	}
	
	public static class Wo extends WrapOutId {
		
	}
}
