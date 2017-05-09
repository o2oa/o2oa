package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.AttachmentIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.AttachmentNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.AttachmentNotImageException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ImageEncodeBase64Exception;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SizeFormatException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectInfoProcessException;
import com.x.bbs.entity.BBSSubjectAttachment;

public class ExcuteImageToBase64 extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteImageToBase64.class );
	
	protected ActionResult<WrapOutString> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, String size ) throws Exception {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		BBSSubjectAttachment fileInfo = null;
		Integer sizeNum = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new AttachmentIdEmptyException();
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if (check) {
			if (size != null && !size.isEmpty()) {
				if (NumberUtils.isNumber(size)) {
					sizeNum = Integer.parseInt(size);
				} else {
					check = false;
					Exception exception = new SizeFormatException(size);
					result.error(exception);
					logger.error(exception, effectivePerson, request, null);
				}
			} else {
				sizeNum = 800;
			}
		}
		if (check) {
			try {
				fileInfo = subjectInfoServiceAdv.getAttachment(id);
				if (fileInfo == null) {
					check = false;
					Exception exception = new AttachmentNotExistsException(id);
					result.error(exception);
					logger.error(exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectInfoProcessException(e, "根据指定ID查询附件信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}

		}
		if (check) {
			if ( !isImage(fileInfo) ) {
				check = false;
				Exception exception = new AttachmentNotImageException(id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		BufferedImage image = null;
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output_for_ftp = new ByteArrayOutputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		if (check) {
			try {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(BBSSubjectAttachment.class, fileInfo.getStorage());
				fileInfo.readContent(mapping, output_for_ftp);
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectInfoProcessException(e, "从文件存储服务器中获取文件流时发生异常.ID:" + id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if (check) {
			input = new ByteArrayInputStream(output_for_ftp.toByteArray());
			if (input != null) {
				try {
					image = ImageIO.read(input);
				} catch (IOException e) {
					check = false;
					Exception exception = new SubjectInfoProcessException(e, "从文件存储服务器中获取文件流时发生异常.ID:" + id);
					result.error(exception);
					logger.error(exception, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				int width = image.getWidth();
				int height = image.getHeight();
				if (sizeNum > 0) {
					if (width * height > sizeNum * sizeNum) {
						image = Scalr.resize(image, sizeNum);
					}
				}
				ImageIO.write(image, "png", output);
				wrap = new WrapOutString();
				wrap.setValue(Base64.encodeBase64String(output.toByteArray()));
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ImageEncodeBase64Exception(e, id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		return result;
	}

}