package com.x.base.core.project.tools;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.x.base.core.project.logger.MessageFormatter;

public class StringTools {

	private StringTools() {
		// nothing
	}

	// 空代码判断正则表达式
	private static final Pattern EMPTY_SCRIPT_CODE_REGEX = Pattern.compile("/\\*[^*]*(?:\\*(?!/)[^*]*)*\\*/|//.*");

	// 脚本文本
	public static final Pattern SCRIPTTEXT_REGEX = Pattern.compile("^\\((.+?)\\)$");
	public static final Pattern MOBILE_REGEX = Pattern.compile(
			"(^(\\+)?0{0,2}852\\d{8}$)|(^(\\+)?0{0,2}853\\d{8}$)|(^(\\+)?0{0,2}886\\d{9}$)|(^1(3|4|5|6|7|8|9)\\d{9}$)");
	/** 中文,英文,数字,-,.· 【】（） */
//    public static final Pattern SIMPLY_REGEX = Pattern
//            .compile("^[\u4e00-\u9fa5a-zA-Z0-9\\_\\(\\)\\-\\ \\.\\ \\·\\【\\】\\（\\）]*$");
	public static final Pattern SIMPLY_REGEX = Pattern
			.compile("^[\u2e80-\ufe4fa-zA-Z0-9\\_\\(\\)\\-\\ \\.\\ \\·\\【\\】\\（\\）]*$");
	/**
	 * MSDN
	 * https://docs.microsoft.com/zh-cn/windows/win32/fileio/naming-a-file?redirectedfrom=MSDN#file_and_directory_names
	 */
	public static final Pattern FILENAME_REGEX = Pattern.compile(
			"# Match a valid Windows filename (unspecified file system).          \n"
					+ "^                                # Anchor to start of string.        \n"
					+ "(?!                              # Assert filename is not: CON, PRN, \n"
					+ "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n"
					+ "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n"
					+ "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n"
					+ "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n"
					+ "  (?:\\.[^.]*)?                  # followed by optional extension    \n"
					+ "  $                              # and end of string                 \n"
					+ ")                                # End negative lookahead assertion. \n"
					+ "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n"
					+ "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n"
					+ "$                                # Anchor to end of string.            ",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);

	/**
	 * RFC822 compliant regex adapted for Java
	 * http://stackoverflow.com/questions/8204680/java-regex-email
	 */
	public static final Pattern MAIL_REGEX = Pattern.compile(
			"(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");

	public static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}$");

	public static final Pattern PERCENT_REGEX = Pattern.compile("^-?\\d+\\.?\\d*\\%?$");

	public static final String[] SQL_LIKE = new String[] { "_", "%" };

	public static final String[] SQL_LIKE_SHIFT = new String[] { "^_", "^%" };

	public static final char SQL_ESCAPE_CHAR = '^';

	public static final String CRLF = "\r\n";
	public static final String CR = "\r";
	public static final String LF = "\n";

	public static final String TWO_HYPHENS = "--";

	private static final SecureRandom random = new SecureRandom();

	public static int utf8Length(String str) {
		if (StringUtils.isEmpty(str)) {
			return 0;
		}
		return str.getBytes(Charset.forName("UTF-8")).length;
	}

	@Deprecated
	public static String utf8SubString_old(String str, int len) {
		if (len < 0 || StringUtils.isEmpty(str) || utf8Length(str) <= len) {
			return str;
		}
		byte[] bs = ArrayUtils.subarray(str.getBytes(Charset.forName("UTF-8")), 0, len);
		String cut = new String(bs, Charset.forName("UTF-8"));
		if (!cut.isEmpty()) {
			cut = cut.substring(0, cut.length() - 1);
		}
		for (int i = cut.length(); i < str.length(); i++) {
			String buf = cut + str.charAt(i);
			if (utf8Length(buf) == len) {
				return buf;
			} else if (utf8Length(buf) > len) {
				return cut;
			} else {
				cut = buf;
			}
		}
		return "";
	}

	public static String utf8SubString(String str, int len) {
		if (len < 0 || StringUtils.isEmpty(str) || utf8Length(str) <= len) {
			return str;
		}
		byte[] bs = Arrays.copyOf(str.getBytes(Charset.forName("UTF-8")), len);
		try {
			for (int i = 0; i < len; i++) {
				ByteArrayInputStream input = new ByteArrayInputStream(Arrays.copyOf(bs, len - i));
				InputStreamReader reader = new InputStreamReader(input, "UTF-8");
				String value = IOUtils.toString(reader);
				if (utf8Length(value) <= len) {
					return value;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 截断超长文件名,尽量保留后缀
	 *
	 * @param str
	 * @param len
	 * @return
	 */
	public static String utf8FileNameSubString(String str, int len) {
		if (len < 0 || StringUtils.isEmpty(str) || utf8Length(str) <= len) {
			return str;
		}
		String extension = StringUtils.substringAfterLast(str, ".");
		if (StringUtils.isNotEmpty(extension)) {
			extension = "." + extension;
			String name = StringUtils.substringBeforeLast(str, ".");
			int nameLength = len - utf8Length(extension);
			if (nameLength <= 0) {
				return utf8SubString(extension, len);
			} else {
				return utf8SubString(name, nameLength) + extension;
			}
		}
		return utf8SubString(str, len);
	}

	public static String check(String str, int min, int max, boolean cutoff) throws Exception {
		int len = utf8Length(str);
		if (min > len) {
			throw new Exception(str + " length is (" + len + "), is too short, min (" + min + "), max (" + max + ").");
		}
		if (len <= max) {
			return str;
		} else {
			if (cutoff == true) {
				return utf8SubString(str, max);
			} else {
				throw new Exception(str + " length is (" + len + "), min (" + min + "), max (" + max + ").");
			}
		}
	}

	public static String uniqueToken() {
		return UUID.randomUUID().toString();
	}

	public static String random6() {
		String str = UUID.randomUUID().toString();
		return StringUtils.substring(str, 0, 6);
	}

	public static String random4() {
		String str = UUID.randomUUID().toString();
		return StringUtils.substring(str, 0, 4);
	}

	public static String toString(Object obj, String propertyName) throws Exception {
		Object o = obj;
		if (null != obj) {
			o = PropertyUtils.getProperty(o, propertyName);
		}
		return Objects.toString(o);
	}

	public static boolean isMobile(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		Matcher matcher = MOBILE_REGEX.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean isSimply(String str) {
		Matcher matcher = SIMPLY_REGEX.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean isFileName(String str) {
		Matcher matcher = FILENAME_REGEX.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean isPercent(String str) {
		Matcher matcher = PERCENT_REGEX.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean isMail(String str) {
		Matcher matcher = MAIL_REGEX.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static String randomNumber4() {
		return "" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10) + random.nextInt(10);
	}

	public static List<String> filterLessThanOrEqualToUtf8Length(List<String> list, Integer length) {
		if (ListTools.isEmpty(list)) {
			return new ArrayList<String>();
		}
		return list.stream().filter(s -> {
			return utf8Length(s) <= length;
		}).collect(Collectors.toList());
	}

	public static List<String> trimUnique(List<String> list) {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		if ((null != list) && (!list.isEmpty())) {
			for (String str : list) {
				if (StringUtils.isNotBlank(str)) {
					set.add(str);
				}
			}
		}
		return new ArrayList<String>(set.asList());
	}

	public static void replaceFieldValue(Object object, Field field, String oldVal, String newVal) throws Exception {
		try {
			if (field.getType().isAssignableFrom(String.class)) {
				Object o = FieldUtils.readField(field, object, true);
				if (null != o) {
					String str = o.toString();
					if (StringUtils.equals(str, oldVal)) {
						FieldUtils.writeField(field, object, newVal, true);

					}
				}
				return;
			} else if (field.getType().isAssignableFrom(List.class)) {
				Object o = FieldUtils.readField(field, object, true);
				// Object o = field.get(object);
				if (null != o) {
					List<String> list = (List<String>) o;
					Collections.replaceAll(list, oldVal, newVal);
				}
				return;
			}
		} catch (Exception e) {
			throw new Exception(
					"can not replaceFieldValue, class: " + object.getClass().getName() + ", field: " + field.getName()
							+ ", oldVal: " + oldVal + ", newVal: " + newVal + ", error: " + e.getMessage() + ".");
		}
		throw new Exception("only support string or string list, " + object.getClass().getName() + ", field: "
				+ field.getName() + ", oldVal: " + oldVal + ", newVal: " + newVal + ".");

	}

	public static Comparator<String> emptyLastComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				if (StringUtils.isEmpty(a)) {
					return (StringUtils.isEmpty(b)) ? 0 : 1;
				} else if (StringUtils.isEmpty(b)) {
					return -1;
				} else {
					return a.compareTo(b);
				}
			}
		};
	}

	public static String JoinUrl(String... strs) {
		String value = "";
		for (String str : strs) {
			if (StringUtils.isNotEmpty(str)) {
				if (StringUtils.isEmpty(value)) {
					value = str;
				} else {
					if (StringUtils.endsWith(value, "/")) {
						if (StringUtils.startsWith(str, "/")) {
							value += str.substring(1);
						} else {
							value += str;
						}
					} else {
						if (StringUtils.startsWith(str, "/")) {
							value += str;
						} else {
							value += "/" + str;
						}
					}
				}
			}
		}
		return value;
	}

	public static String url(String... strs) throws Exception {
		List<String> os = new ArrayList<>();
		for (String str : strs) {
			String value = str;
			if (StringUtils.endsWith(value, "/")) {
				value = StringUtils.substring(value, 0, value.length() - 1);
			}
			if (StringUtils.startsWith(value, "/")) {
				value = value.substring(1);
			}
			if (StringUtils.isNotEmpty(value)) {
				if (StringUtils.containsIgnoreCase(value, "http://")
						|| StringUtils.containsIgnoreCase(value, "https://")) {
					os.add(value);
				} else {
					os.add(URLEncoder.encode(value, "UTF-8"));
				}
			}
		}
		return StringUtils.join(os, "/");

	}

	public static String sha(Object o) {
		String str = Objects.toString(o, "");
		return DigestUtils.sha256Hex(str);
	}

	public static boolean isUUIDFormat(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		Matcher matcher = UUID_REGEX.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static String format(String message, Object... os) {
		return MessageFormatter.arrayFormat(message, os).getMessage();
	}

	public static String[] fill(Integer size, String value) {
		String[] os = new String[size];
		for (int i = 0; i < os.length; i++) {
			os[i] = value;
		}
		return os;
	}

	public static boolean matchWildcard(String str, String pattern) {
		return Objects.toString(str, "").matches(Objects.toString(pattern, "").replace("?", ".?").replace("*", ".*?"));
	}

	public static List<String> includesExcludesWithWildcard(List<String> list, Collection<String> includes,
			Collection<String> excludes) {
		if (list == null || list.isEmpty()) {
			return list;
		}
		List<String> in = new ArrayList<>();
		if (includes == null || includes.isEmpty()) {
			in.addAll(list);
		} else {
			for (String str : list) {
				loop: for (String include : includes) {
					if (matchWildcard(str, include)) {
						in.add(str);
						break loop;
					}
				}
			}
		}
		if (excludes == null || excludes.isEmpty()) {
			return in;
		} else {
			List<String> ex = new ArrayList<>();
			for (String str : in) {
				loop: for (String exclude : excludes) {
					if (matchWildcard(str, exclude)) {
						ex.add(str);
						break loop;
					}
				}
			}
			return ListUtils.subtract(in, ex);
		}
	}

	public static String escapeSqlLikeKey(String str) {
		String text = StringUtils.replace(str, "\u3000", " ");
		if (StringUtils.isEmpty(text)) {
			return str;
		} else {
			return StringUtils.trim(StringUtils.replaceEach(text, SQL_LIKE, SQL_LIKE_SHIFT));
		}
	}

	private static final int[] SIZE_TABLE = new int[] { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999,
			2147483647 };

	public static int sizeOfInt(int x) {
		int i;
		for (i = 0; x > SIZE_TABLE[i]; ++i) {
		}

		return i + 1;
	}

	public static boolean isCharEqual(String str) {
		return str.replace(str.charAt(0), ' ').trim().length() == 0;
	}

	public static boolean isNumeric(String str) {
		int i = str.length();

		do {
			--i;
			if (i < 0) {
				return true;
			}
		} while (Character.isDigit(str.charAt(i)));

		return false;
	}

	public static boolean equalsNull(String str) {
		int strLen;
		if (str != null && (strLen = str.length()) != 0 && !str.equalsIgnoreCase("null")) {
			for (int i = 0; i < strLen; ++i) {
				if (!Character.isWhitespace(str.charAt(i))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public static boolean isAbsolutePath(String path) {
		if (path.startsWith("/") || path.indexOf(":") > 0) {
			return true;
		}
		return false;
	}

	// copy from /org/apache/tools/ant/types/Commandline.java
	public static String[] translateCommandline(String toProcess) throws Exception {
		if (toProcess == null || toProcess.isEmpty()) {
			// no command? no string
			return new String[0];
		}
		// parse with a simple finite state machine

		final int normal = 0;
		final int inQuote = 1;
		final int inDoubleQuote = 2;
		int state = normal;
		final StringTokenizer tok = new StringTokenizer(toProcess, "\"\' ", true);
		final ArrayList<String> result = new ArrayList<>();
		final StringBuilder current = new StringBuilder();
		boolean lastTokenHasBeenQuoted = false;

		while (tok.hasMoreTokens()) {
			String nextTok = tok.nextToken();
			switch (state) {
			case inQuote:
				if ("\'".equals(nextTok)) {
					lastTokenHasBeenQuoted = true;
					state = normal;
				} else {
					current.append(nextTok);
				}
				break;
			case inDoubleQuote:
				if ("\"".equals(nextTok)) {
					lastTokenHasBeenQuoted = true;
					state = normal;
				} else {
					current.append(nextTok);
				}
				break;
			default:
				if ("\'".equals(nextTok)) {
					state = inQuote;
				} else if ("\"".equals(nextTok)) {
					state = inDoubleQuote;
				} else if (" ".equals(nextTok)) {
					if (lastTokenHasBeenQuoted || current.length() > 0) {
						result.add(current.toString());
						current.setLength(0);
					}
				} else {
					current.append(nextTok);
				}
				lastTokenHasBeenQuoted = false;
				break;
			}
		}
		if (lastTokenHasBeenQuoted || current.length() > 0) {
			result.add(current.toString());
		}
		if (state == inQuote || state == inDoubleQuote) {
			throw new Exception("unbalanced quotes in " + toProcess);
		}
		return result.toArray(new String[result.size()]);
	}

	public static String getMethodName(String name) {
		return methodName("get", name);
	}

	public static String setMethodName(String name) {
		return methodName("set", name);
	}

	private static String methodName(String getOrSet, String name) {
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(getOrSet)) {
			return name;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getOrSet);
		if ((name.length() > 1) && (StringUtils.isAllLowerCase(name.substring(0, 1))
				&& StringUtils.isAllUpperCase(name.substring(1, 2)))) {
			sb.append(name);
		} else {
			sb.append(StringUtils.capitalize(name));
		}
		return sb.toString();
	}

	/**
	 * 文本搜索
	 *
	 * @param keyword        搜索关键字
	 * @param content        文本
	 * @param caseSensitive  大小写敏感
	 * @param matchWholeWord 是否全字匹配
	 * @param matchRegExp    正则表达式搜索
	 * @return
	 */
	public static boolean matchKeyword(String keyword, String content, Boolean caseSensitive, Boolean matchWholeWord,
			Boolean matchRegExp) {
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(content)) {
			return false;
		}
		if (BooleanUtils.isTrue(matchRegExp)) {
			Pattern pattern = Pattern.compile(keyword);
			Matcher matcher = pattern.matcher(content);
			return matcher.find();
		} else if (BooleanUtils.isTrue(matchWholeWord)) {
			if (BooleanUtils.isTrue(caseSensitive)) {
				Pattern pattern = Pattern.compile("\\b(" + keyword + ")\\b");
				Matcher matcher = pattern.matcher(content);
				return matcher.find();
			} else {
				Pattern pattern = Pattern.compile("\\b(" + keyword + ")\\b", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(content);
				return matcher.find();
			}
		} else {
			if (BooleanUtils.isTrue(caseSensitive)) {
				return (content.indexOf(keyword) > -1);
			} else {
				return (content.toLowerCase().indexOf(keyword.toLowerCase()) > -1);
			}
		}
	}

	/**
	 * 检查去除注释后是否有有效的代码.
	 * 
	 * @param script
	 * @return
	 */
	public static boolean ifScriptHasEffectiveCode(String script) {
		if (StringUtils.isEmpty(script)) {
			return false;
		}
		return StringUtils.isNotBlank(StringUtils.trimToEmpty(EMPTY_SCRIPT_CODE_REGEX.matcher(script).replaceAll("")));
	}

}
