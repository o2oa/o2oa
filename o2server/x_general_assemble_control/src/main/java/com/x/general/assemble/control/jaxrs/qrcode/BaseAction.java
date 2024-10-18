package com.x.general.assemble.control.jaxrs.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

abstract class BaseAction extends StandardJaxrsAction {

	protected static final String FILENAME = "qrcode.png";

	private static final Map<EncodeHintType, String> HINTS = new EnumMap<>(EncodeHintType.class);

	static {
		HINTS.put(EncodeHintType.MARGIN, "1");
		HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q.toString());
		HINTS.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	}

	private static final String FORMAT = "png";
	private static final int DEFAULTWIDTH = 200;
	private static final int MAXWIDTH = 400;
	private static final int MINWIDTH = 40;

	private static final int DEFAULTHEIGHT = 200;
	private static final int MAXHEIGHT = 400;
	private static final int MINHEIGHT = 40;

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	protected byte[] create(Integer width, Integer height, String text) throws WriterException, IOException {
		text = text.replace("\\n", "\n");
		int w = this.width(width);
		int h = this.height(height);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, w, h, HINTS);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
			}
		}
		byte[] bytes = null;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(image, FORMAT, out);
			bytes = out.toByteArray();
		}
		return bytes;
	}

	private Integer width(Integer width) {
		return (null == width) || (width < MINWIDTH) || (width > MAXWIDTH) ? DEFAULTWIDTH : width;
	}

	private Integer height(Integer height) {
		return (null == height) || (height < MINHEIGHT) || (height > MAXHEIGHT) ? DEFAULTHEIGHT : height;
	}

}
