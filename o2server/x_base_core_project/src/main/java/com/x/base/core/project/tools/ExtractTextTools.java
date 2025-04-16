package com.x.base.core.project.tools;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

public class ExtractTextTools {

	private static final Logger logger = LoggerFactory.getLogger(ExtractTextTools.class);

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
		return null;
	}


	private static Tika tikaInstance() {
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
