package com.x.base.core.project.tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

public class ExtractTextTools {

	private static Logger logger = LoggerFactory.getLogger(ExtractTextTools.class);

	private static ITesseract tesseract = null;

	private static Tika tika = null;

	public static final Integer MAXLENGTH = 1024 * 1024 * 32;

	public static boolean support(String name) {
		String ext = StringUtils.substringAfterLast(name, ".");
		if (StringUtils.isNotEmpty(ext)) {
			ext = "." + StringUtils.lowerCase(ext);
			return SUPPORT_TYPES.contains(ext);
		}
		return false;
	}

	public static boolean supportImage(String name) {
		String ext = StringUtils.substringAfterLast(name, ".");
		if (StringUtils.isNotEmpty(ext)) {
			ext = "." + StringUtils.lowerCase(ext);
			return SUPPORT_IMAGE_TYPES.contains(ext);
		}
		return false;
	}

	public static boolean available(byte[] bytes) {
		if (null == bytes || bytes.length == 0 || bytes.length > MAXLENGTH) {
			return false;
		}
		return true;
	}

	public static final List<String> SUPPORT_TYPES = UnmodifiableList.unmodifiableList(ListTools.toList(".doc", ".docx",
			".pdf", ".xls", ".xlsx", ".txt", ".bmp", ".jpg", ".png", ".gif", ".jpeg", "jpe"));

	public static final List<String> SUPPORT_IMAGE_TYPES = UnmodifiableList
			.unmodifiableList(ListTools.toList(".bmp", ".jpg", ".png", ".gif", ".jpeg", "jpe"));

	public static String extract(byte[] bytes, String name, boolean word, boolean excel, boolean pdf, boolean txt,
			boolean image) throws Exception {
		if ((null != bytes) && bytes.length > 0 && bytes.length < 1024 * 1024 * 10) {
			if (word && (StringUtils.endsWithIgnoreCase(name, ".doc")
					|| StringUtils.endsWithIgnoreCase(name, ".docx"))) {
				return word(bytes);
			}
			if (excel && (StringUtils.endsWithIgnoreCase(name, ".xls")
					|| StringUtils.endsWithIgnoreCase(name, ".xlsx"))) {
				return excel(bytes);
			}
			if (pdf && StringUtils.endsWithIgnoreCase(name, ".pdf")) {
				return pdf(bytes);
			}
			if (txt && StringUtils.endsWithIgnoreCase(name, ".txt")) {
				return text(bytes);
			}
			if (image && (StringUtils.endsWithIgnoreCase(name, ".jpg") || StringUtils.endsWithIgnoreCase(name, ".png")
					|| StringUtils.endsWithIgnoreCase(name, ".gif") || StringUtils.endsWithIgnoreCase(name, ".bmp")
					|| StringUtils.endsWithIgnoreCase(name, ".jpeg") || StringUtils.endsWithIgnoreCase(name, ".jpe"))) {
				return image(bytes);
			}
		}
		return null;
	}

	public static String extract(byte[] bytes, String name) throws Exception {
		return extract(bytes, name, true, false, true, true, false);
	}

	public static String pdf(byte[] bytes) {
		try {
			PDFParser parser = new PDFParser(new RandomAccessBuffer(bytes));
			parser.parse();
			try (COSDocument cos = parser.getDocument(); PDDocument pd = new PDDocument(cos)) {
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setStartPage(1);
				stripper.setEndPage(pd.getNumberOfPages());
				return stripper.getText(pd);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static String word(byte[] bytes) throws Exception {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			return tikaInstance().parseToString(in);
		}
	}

	public static String excel(byte[] bytes) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			return tikaInstance().parseToString(in);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static String text(byte[] bytes) {
		return new String(bytes, DefaultCharset.charset);
	}

	public static String image(byte[] bytes) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			BufferedImage image = ImageIO.read(in);
			return tesseractInstance().doOCR(image);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private static ITesseract tesseractInstance() throws Exception {
		if (null == tesseract) {
			synchronized (ExtractTextTools.class) {
				if (null == tesseract) {
//					tesseract = new Tesseract();
					ITesseract tesseract = new Tesseract();
					tesseract.setDatapath(Config.dir_commons_tess4j_tessdata().getAbsolutePath());// 设置训练库的位置
					tesseract.setLanguage(Config.query().getTessLanguage());// 中文识别
				}
			}
		}
		return tesseract;
	}

	private static Tika tikaInstance() throws Exception {
		if (null == tika) {
			synchronized (ExtractTextTools.class) {
				if (null == tika) {
					tika = new Tika();
				}
			}
		}
		return tika;
	}

}
