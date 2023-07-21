package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ImageTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 修改栏目图标
 * @author sword
 */
public class ActionAppIconUpload extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAppIconUpload.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String appId,
									   Integer size, byte[] bytes) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if( size == null|| size== 0 ){
			size = 72;
		}

		AppInfo appInfo = appInfoServiceAdv.get( appId );
		if (null == appInfo) {
			throw new ExceptionEntityNotExist(appId);
		}

		Business business = new Business(null);
		if (!business.isAppInfoManager(effectivePerson, appInfo)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		try ( InputStream input = new ByteArrayInputStream(bytes);
			  ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
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
			image = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, width, height, Scalr.OP_ANTIALIAS);

			String iconMainColor = ImageTools.hue(image);

			//再获取图片base64编码信息
			ImageIO.write(image, "png", bos);
			String base64 = Base64.encodeBase64String(bos.toByteArray());

			appInfoServiceAdv.saveAppInfoIcon( appId, base64, iconMainColor );
			CacheManager.notify( AppInfo.class );
		}

		return result;
	}

	public static class Wo extends WrapOutId {

	}
}
