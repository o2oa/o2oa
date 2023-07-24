package com.x.mind.assemble.control.jaxrs.mind;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionParameterEmpty;
import com.x.mind.entity.MindBaseInfo;

public class ActionMindIconUpdate extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionMindIconUpdate.class);

	@AuditLog(operation = "更新缩略图")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String mindId, 
			Integer size, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<Wo> result = new ActionResult<>();
		MindBaseInfo mindBaseInfo = null;
		String base64 = null;
		Boolean check = true;	
		if( check ){
			if( StringUtils.isEmpty(mindId) ){
				check = false;
				Exception exception = new ExceptionParameterEmpty( "未获取到脑图ID[mindId]!" );
				result.error( exception );
			}
		}
		if( check ){
			if( size== 0 ){
				size = 128;
			}
		}
		//判断脑图信息是否已经存在
		if( check ){
			try {
				mindBaseInfo = mindInfoService.getMindBaseInfo( mindId );
				if (null == mindBaseInfo ) {
					check = false;
					Exception exception = new ExceptionMindNotExists( mindId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "根据ID查询脑图信息时发生异常！ID:" + mindId );
				result.error( exception );
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
					if( height >   size ) {
						step = (double)size/(double)height;
						height = size;
						width = (int)(width * step);
					}
				}else {
					if( width >   size ) {
						step = (double)size/(double)width;
						width = size;
						height = (int)(height * step);
					}
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
				mindInfoService.updateIcon( mindId,    base64 );
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
