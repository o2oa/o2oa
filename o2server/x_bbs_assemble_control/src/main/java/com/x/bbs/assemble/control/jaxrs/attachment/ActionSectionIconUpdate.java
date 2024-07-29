package com.x.bbs.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionSectionIconUpdate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSectionIconUpdate.class);

	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson,
			String sectionId, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		if(effectivePerson.isAnonymous()){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSectionInfo sectionInfo = null;
		String icon = null;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if( check ){
			if( StringUtils.isEmpty(sectionId) ){
				check = false;
				Exception exception = new ExceptionURLParameterGet( new Exception("未获取到版块ID") );
				result.error( exception );
			}
		}

		if (check) {
			try {
				sectionInfo = sectionInfoService.get(sectionId);
				if (sectionInfo == null) {
					check = false;
					result.error(new Exception("版块信息不存在，无法继续进行图标更新操作！ID：" + sectionId));
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system query section info with id got an exception!id:" + sectionId);
				logger.error(e);
			}
		}

		if( check ){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try ( InputStream input = new ByteArrayInputStream(bytes)) {
				BufferedImage image = ImageIO.read(input);
				BufferedImage scalrImage = Scalr.resize(image, 72, 72);
				ImageIO.write(scalrImage, "png", baos);
				icon = Base64.encodeBase64String(baos.toByteArray());
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null );
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				sectionInfo = emc.find(sectionId, BBSSectionInfo.class);
				if (sectionInfo != null) {
					emc.beginTransaction(BBSSectionInfo.class);
					sectionInfo.setIcon(icon);
					emc.commit();
					operationRecordService.sectionIconOperation(effectivePerson.getDistinguishedName(), sectionInfo, "UPLOAD", hostIp, hostName);
					WrapOutId wrap = new WrapOutId(sectionInfo.getId());
					result.setData( wrap );
				} else {
					check = false;
					result.error(new Exception("版块信息不存在，无法保存版块图信息！ID：" + sectionId));
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system save image for section icon got an exception.");
				logger.error(e);
			}
		}
		return result;
	}
}
