package com.x.query.service.processing.helper;

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
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

import net.sourceforge.tess4j.Tesseract;

public class ExtractTextHelper {

	private static Logger logger = LoggerFactory.getLogger(ExtractTextHelper.class);

	private static Tesseract tesseract = null;

	private static Tika tika = null;

	public static boolean support(String name) {
		String ext = StringUtils.substringAfterLast(name, ".");
		if (StringUtils.isNotEmpty(ext)) {
			ext = "." + StringUtils.lowerCase(ext);
			return SUPPORT_TYPES.contains(ext);
		}
		return false;
	}

	public static final List<String> SUPPORT_TYPES = UnmodifiableList.unmodifiableList(
			ListTools.toList(".doc", ".docx", ".pdf", ".xls", ".xlsx", ".txt", ".jpg", ".png", ".gif"));

	public static String extract(byte[] bytes, String name, Boolean office, Boolean pdf, Boolean txt, Boolean image) {
		if ((null != bytes) && bytes.length > 0) {
			if (office) {
				if (StringUtils.endsWithIgnoreCase(name, ".doc") || StringUtils.endsWithIgnoreCase(name, ".docx")) {
					return word(bytes);
				}
				if (StringUtils.endsWithIgnoreCase(name, ".xls") || StringUtils.endsWithIgnoreCase(name, ".xlsx")) {
					return excel(bytes);
				}
			}
			if (pdf) {
				if (StringUtils.endsWithIgnoreCase(name, ".pdf")) {
					return pdf(bytes);
				}
			}
			if (txt) {
				if (StringUtils.endsWithIgnoreCase(name, ".txt")) {
					return text(bytes);
				}
			}
			if (image) {
				if (StringUtils.endsWithIgnoreCase(name, ".jpg") || StringUtils.endsWithIgnoreCase(name, ".png")
						|| StringUtils.endsWithIgnoreCase(name, ".gif")) {
					return image(bytes);
				}
			}
		}
		return null;
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

	public static String word(byte[] bytes) {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			return tikaInstance().parseToString(in);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
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
		try {

			try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);) {
				BufferedImage image = ImageIO.read(in);
				return tesseractInstance().doOCR(image);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private static Tesseract tesseractInstance() throws Exception {
		if (null == tesseract) {
			synchronized (ExtractTextHelper.class) {
				if (null == tesseract) {
					tesseract = new Tesseract();
					tesseract.setDatapath(Config.base() + "/commons/tess4j/tessdata");// 设置训练库的位置
					tesseract.setLanguage(Config.query().getTessLanguage());// 中文识别
				}
			}
		}
		return tesseract;
	}

	private static Tika tikaInstance() throws Exception {
		if (null == tika) {
			synchronized (ExtractTextHelper.class) {
				if (null == tika) {
					tika = new Tika();
				}
			}
		}
		return tika;
	}

}
