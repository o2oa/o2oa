package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

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
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.entity.Bind;

class ActionBind extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBind.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		Audit audit = logger.audit(effectivePerson);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String meta = StringTools.uniqueToken();
		/** 二维码内容 */
		String url = UriBuilder.fromUri(Config.collect().getAppUrl()).queryParam("meta", meta).build().toASCIIString();
		int width = 200; // 二维码图片宽度
		int height = 200; // 二维码图片高度
		String format = "png";// 二维码的图片格式

		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		// hints.put(EncodeHintType.CHARACTER_SET, DefaultCharset.name); //
		// 内容所使用字符集编码
		hints.put(EncodeHintType.MARGIN, "1");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q.toString());
		// hints.put(EncodeHintType.QR_VERSION, "7");

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
		audit.log();
		return result;
	}

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

//	private static final byte[] logoBytes = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
//			0, 0, 0, 64, 0, 0, 0, 64, 8, 6, 0, 0, 0, -86, 105, 113, -34, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79, -113,
//			11, -4, 97, 5, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 38, 0, 0, -128, -124, 0, 0, -6, 0, 0, 0, -128, -24,
//			0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0, 23, 112, -100, -70, 81, 60, 0, 0, 0, 6, 98, 75, 71, 68,
//			0, 0, 0, 0, 0, 0, -7, 67, -69, 127, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 11, 18, 0, 0, 11, 18, 1, -46, -35,
//			126, -4, 0, 0, 3, 36, 73, 68, 65, 84, 120, -38, -19, -101, -51, 79, 19, 65, 24, -58, -97, 89, 49, -107,
//			-125, -75, 74, 37, 86, 69, 18, 67, -62, 87, -119, 7, 77, -76, -127, -58, -125, 127, 0, -31, 35, 106, 60,
//			-101, 72, 52, 24, 14, 4, 57, 122, -82, 112, -63, 24, 76, -4, 7, -60, -92, -59, -60, -77, 94, 10, 42, 28,
//			-107, 34, 120, 48, -126, 32, 6, -86, -108, 30, -124, 30, -24, 122, 104, 119, 83, 118, 27, 29, 103, 63, -34,
//			118, 119, 127, -57, -39, -39, -23, -13, 60, -23, -68, -99, -66, -39, 101, 40, 65, -106, -27, 65, 0, 93, 0,
//			-94, 0, 66, 112, 22, 27, 0, -110, 0, 102, 24, 99, -113, -107, 65, 86, 52, 30, 0, -16, 20, -64, 13, 106,
//			-107, 54, 49, 5, 96, -128, 49, -106, 81, 2, 120, -18, 34, -13, 106, 8, -116, -79, -101, -84, -8, -75, -97,
//			-96, 86, 67, -60, -3, 26, 20, -10, -68, -54, -20, -89, 77, -116, 37, 82, 72, 103, 115, -44, -30, 76, 37,
//			-24, -9, 97, -72, -73, 29, -99, -83, -11, -91, -61, 93, 18, 10, 5, 79, -59, -119, -26, 1, 32, -99, -51, 97,
//			44, -111, -46, 14, 71, 37, 104, -86, -67, 19, -51, -1, -59, 91, 72, -94, 22, 69, -115, 23, 0, -75, 0, 106,
//			106, 120, 39, 62, 27, -116, -96, -27, -20, 49, 106, -67, 92, 44, -81, 103, 113, 123, -30, 45, -41, 92, -18,
//			111, 64, -75, -104, 7, -128, -26, 51, 126, -18, -71, -82, -33, 2, -36, 1, 124, -8, -70, 77, -83, -43, 18,
//			-72, 107, -64, -67, -55, 57, 106, -83, 101, 9, -6, 125, 24, -23, 11, 35, -46, 114, 82, -24, -2, -86, -33, 2,
//			-23, 108, 14, -79, -8, -126, -16, -3, 85, 31, -128, 18, -126, -85, 3, 48, -126, -124, 66, -89, 68, 37, -24,
//			-9, 81, 107, -78, 61, -128, 100, -23, -64, 104, 127, 7, 66, 39, 106, -87, 117, -39, 70, 13, -128, 25, 0,
//			-41, -107, -127, -53, -51, 65, -68, 120, 112, -107, 90, 87, 89, 22, 87, 51, -72, -13, -28, -67, -87, 107,
//			74, -59, 6, -31, 20, -75, 57, 30, -38, -50, 5, 76, 95, -45, 43, -126, -59, -98, -96, -37, 26, -94, 42, -70,
//			-98, -32, -69, -91, 45, 60, 74, -92, -80, -75, -77, 7, 0, 56, 117, -68, 22, -93, -3, 97, 92, 108, -86, -125,
//			118, -34, -40, 116, 10, -101, -103, 61, -31, 15, -105, 36, -122, 75, 77, 117, 120, 120, -21, 2, -114, -42,
//			30, 46, -67, 116, -115, 49, -10, 6, 0, 100, 89, -106, -83, 12, 64, -41, 19, -116, -59, 23, 84, -13, 0, -16,
//			99, 123, -73, 92, 47, 13, -79, -8, -126, 33, -13, 0, -112, -49, -53, -104, -1, -100, 70, 124, 118, 69, 123,
//			41, 42, -78, -98, 104, 0, -1, -20, 9, -82, -3, -4, -83, 27, 51, -77, 119, -8, -3, -41, -82, 118, -24, -68,
//			-99, 1, -72, 26, -31, 0, -22, 3, 71, -88, -75, -45, 6, 48, -46, 27, 118, -60, -119, -111, -69, 31, -96, -27,
//			127, 79, -116, -5, 121, 25, -29, -45, -117, 120, 53, -1, -115, -38, -13, 1, 108, -85, 1, -121, 36, -122,
//			-114, -58, 0, -75, 95, -70, 0, 42, 21, -37, 2, -40, -49, -53, -8, -72, -110, -95, -10, -85, 67, -72, 6, -52,
//			45, -89, 49, -2, 50, -123, 13, -3, 111, 120, 85, 33, 28, 64, 44, 97, -4, 36, 88, 9, 8, 111, 1, 39, -104, 55,
//			20, -128, 83, -88, -120, 0, 78, -21, 15, 84, 95, -20, -6, 108, -31, 26, 96, 6, -54, -33, -31, -66, -50, 70,
//			-19, -91, -92, -56, 122, 21, 17, -64, -28, -35, 43, 8, 27, 63, -16, -68, -74, -72, 13, -96, 98, -6, 22, 48,
//			-63, -68, -83, 84, 68, 13, 112, 84, 0, -117, -85, 25, -53, -60, 90, -79, -74, -23, 53, -64, -20, -66, -67,
//			-43, 120, 91, -128, 90, 0, 53, 94, 0, -62, 55, 50, 70, -83, -35, 20, 45, -62, 1, -12, 68, 26, 42, 34, 4,
//			-119, 49, -12, 68, 26, -124, -17, 23, -2, 21, 24, -22, 110, -61, 80, 119, 27, -75, 127, -61, 120, 53, -128,
//			90, 0, 53, -36, 1, 44, -81, 103, -87, -75, 114, -77, -76, -74, -61, 61, -105, -69, 6, -16, 62, 123, 91, 109,
//			120, 91, -128, 90, 0, 53, 94, 0, 112, -47, 115, -126, 101, -68, 109, -24, -98, 19, 28, -18, 109, 119, 100,
//			8, -54, 107, 115, 26, -110, -82, 127, 113, -46, -11, -81, -50, 42, 69, 112, 0, 85, -14, -80, -92, 89, -26,
//			-117, -98, 113, -32, -17, -100, 27, 95, -97, -1, 3, 15, 32, -32, 95, -60, -127, 21, 87, 0, 0, 0, 0, 73, 69,
//			78, 68, -82, 66, 96, -126 };

	public class Wo extends Bind {

		private static final long serialVersionUID = -3574645735233129236L;

		@FieldDescribe("Base64编码图像.")
		private String image;

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

	}

}
