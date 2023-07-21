package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.entity.Bind;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionBind extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBind.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String meta = StringTools.uniqueToken();
		// 二维码内容
		String url = UriBuilder.fromUri(Config.collect().getAppUrl()).queryParam("meta", meta).build().toASCIIString();
		int width = 200; // 二维码图片宽度
		int height = 200; // 二维码图片高度
		String format = "png";// 二维码的图片格式

		Map<EncodeHintType, String> hints = new HashMap<>();
		// 内容所使用字符集编码
		hints.put(EncodeHintType.MARGIN, "1");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q.toString());

		BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
			}
		}
		Graphics2D graphics = image.createGraphics();

		Image logo = ImageIO.read(new ByteArrayInputStream(Config.bindLogo()));
		graphics.drawImage(logo, 68, 68, null);
		graphics.dispose();
		logo.flush();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(image, format, out);
			wo.setImage(Base64.encodeBase64String(out.toByteArray()));
		}
		wo.setMeta(meta);
		result.setData(wo);
		return result;
	}

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionBind$Wo")
	public class Wo extends Bind {

		private static final long serialVersionUID = -3574645735233129236L;

		@FieldDescribe("Base64编码图像.")
		@Schema(description = "Base64编码图像.")
		private String image;

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

	}

}
