package com.x.base.core.project.tools;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.helpers.MessageFormatter;

public class StringTools {
	public static final Pattern MOBILE_REGEX = Pattern.compile(
			"(^(\\+)?0{0,2}852\\d{8}$)|(^(\\+)?0{0,2}853\\d{8}$)|(^(\\+)?0{0,2}886\\d{9}$)|(^1(3|4|5|6|7|8|9)\\d{9}$)");
	/** 中文,英文,数字,-,. 【】（） */
	public static final Pattern SIMPLY_REGEX = Pattern
			.compile("^[\u4e00-\u9fa5a-zA-Z0-9\\_\\(\\)\\-\\ \\.\\【\\】\\（\\）]*$");
	public static final Pattern FILENAME_REGEX = Pattern.compile("[^/\\\\<>*?|\"]+(\\.?)[^/\\\\<>*?|\"]+");
	/**
	 * RFC822 compliant regex adapted for Java
	 * http://stackoverflow.com/questions/8204680/java-regex-email
	 */
	public static final Pattern MAIL_REGEX = Pattern.compile(
			"(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");

	public static final Pattern UUID_REGEX = Pattern
			.compile("^[a-zA-Z_0-9]{8}-[a-zA-Z_0-9]{4}-[a-zA-Z_0-9]{4}-[a-zA-Z_0-9]{4}-[a-zA-Z_0-9]{12}$");

	public static final String[] SQL_LIKE = new String[] { "_", "%" };

	public static final String[] SQL_LIKE_SHIFT = new String[] { "\\\\_", "\\\\%" };

	private static final Random random = new Random();

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
//		if (StringUtils.isNotEmpty(str) && StringUtils.isNotEmpty(pattern) && StringUtils.contains(pattern, "*")) {
//			if (StringUtils.equals(pattern, "*")) {
//				return true;
//			}
//			if (StringUtils.startsWith(pattern, "*")) {
//				return StringUtils.endsWith(str, StringUtils.substringAfter(pattern, "*"));
//			}
//			if (StringUtils.endsWith(pattern, "*")) {
//				return StringUtils.startsWith(str, StringUtils.substringBeforeLast(pattern, "*"));
//			}
//			String[] parts = StringUtils.split(pattern, "*", 2);
//			if (StringUtils.startsWith(str, parts[0]) && StringUtils.endsWith(str, parts[1])) {
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			return StringUtils.equals(str, pattern);
//		}
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
}