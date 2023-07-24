package com.x.base.core.project.tools;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class DomainTools {

	public static final String HTTP_PROTOCOL_HEAD = "http://";
	public static final int HTTP_PROTOCOL_HEAD_LENGTH = HTTP_PROTOCOL_HEAD.length();
	public static final String HTTPS_PROTOCOL_HEAD = "https://";
	public static final int HTTPS_PROTOCOL_HEAD_LENGTH = HTTPS_PROTOCOL_HEAD.length();

	public static final boolean isLetterOrDigit(char c) {
		return ('0' <= c && c <= '9') || ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
	}

	/**
	 * 获取url的域名部分，注意：域名部分包括端口号。
	 * 
	 * @param url
	 * @return
	 */
	public static final String getDomain(String url) {
		// 0. 参数检验
		if (url == null)
			return null;

		// 1. 剔出域名
		String domain;

		int l = 0;
		if (url.startsWith(HTTP_PROTOCOL_HEAD)) {
			l = HTTP_PROTOCOL_HEAD_LENGTH;
		} else if (url.startsWith(HTTPS_PROTOCOL_HEAD)) {
			l = HTTPS_PROTOCOL_HEAD_LENGTH;
		}
		int slash = url.indexOf('/', l);
		if (slash > 0) {
			domain = url.substring(l, slash);
		} else {
			if (l > 0) {
				domain = url.substring(l);
			} else {
				domain = url;
			}
		}
		return domain;
	}

	public static final String getDomainWithoutPort(String url) {
		String domain = getDomain(url);
		if (domain == null)
			return null;
		int idx = domain.indexOf(':');
		if (idx > 0) {
			return domain.substring(0, idx);
		} else {
			return domain;
		}
	}

	public static final String getDomainWithProtocal(String url) {
		// 0. 参数检验
		if (url == null)
			return null;

		// 1. 剔出域名
		String domain;

		int l = 0;
		if (url.startsWith(HTTP_PROTOCOL_HEAD)) {
			l = HTTP_PROTOCOL_HEAD_LENGTH;
		} else if (url.startsWith(HTTPS_PROTOCOL_HEAD)) {
			l = HTTPS_PROTOCOL_HEAD_LENGTH;
		}

		int slash = url.indexOf('/', l);
		if (slash > 0) {
			domain = url.substring(0, slash);
		} else {
			domain = url;
		}
		return domain;
	}

	public static final String regulateUrl(String url) {
		if (url == null)
			return null;

		boolean needProt = false;
		boolean needTail = false;
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			needProt = true;
		}
		if (isDomain(url) && !url.endsWith("/")) {
			needTail = true;
		}
		if (needProt) {
			url = "http://" + url;
		}
		if (needTail) {
			url += '/';
		}

		return url;
	}

	/**
	 * 判定一个字符串是否为URL，并返回归一化后的URL字符串。 归一化规则：1.以http://打头; 2.端口号为80时，要省略; 3.
	 * 纯域名时，要加"/"作结尾
	 * 
	 * @param query 检查字符串
	 * @return 当结果为URL时，返回归一化的结果，否则返回null。
	 */
	private static final String[] traditionalUrlPostfix = { ".com", ".biz", ".pro", ".aero", ".coop", ".museum",
			".mobi", ".edu", ".gov", ".info", ".mil", ".name", ".net", ".org", ".jobs", ".travel", ".mil", ".arpa",
			".int", ".cat", ".asia", ".tel" };
	private static final String[] internationalTraditionalUrlPostfix = { ".com", ".net", ".edu", };
	private static final String[] regionalUrlPostfix = {

			".ac", ".ad", ".ae", ".af", ".ag", ".ai", ".al", ".am", ".an", ".ao", ".aq", ".ar", ".as", ".at", ".au",
			".aw", ".az", ".ba", ".bb", ".bd", ".be", ".bf", ".bg", ".bh", ".bi", ".bj", ".bm", ".bn", ".bo", ".br",
			".bs", ".bt", ".bv", ".bw", ".by", ".bz", ".ca", ".cc", ".cd", ".cf", ".cg", ".ch", ".ci", ".ck", ".cl",
			".cm", ".cn", ".co", ".cr", ".cs", ".cu", ".cv", ".cx", ".cy", ".cz", ".de", ".dj", ".dk", ".dm", ".do",
			".dz", ".ec", ".eu", ".fi", ".fj", ".fk", ".fm", ".fo", ".fr", ".fx", ".ga", ".gb", ".gd", ".ge", ".gf",
			".gh", ".gi", ".gl", ".gp", ".gq", ".gf", ".gm", ".gn", ".gr", ".gs", ".gt", ".gu", ".gw", ".gy", ".hk",
			".hm", ".hn", ".hr", ".ht", ".hu", ".id", ".ie", ".il", ".in", ".io", ".iq", ".ir", ".is", ".it", ".jm",
			".jo", ".jp", ".ke", ".kg", ".kh", ".ki", ".km", ".kn", ".kp", ".kr", ".kw", ".ky", ".kz", ".la", ".lb",
			".lc", ".li", ".lk", ".lr", ".ls", ".lt", ".lu", ".lv", ".ly", ".ma", ".mc", ".md", ".mg", ".mh", ".mk",
			".ml", ".mm", ".mn", ".mo", ".mp", ".mq", ".mr", ".ms", ".mt", ".mu", ".mv", ".mw", ".mx", ".my", ".mz",
			".na", ".nc", ".ne", ".nf", ".ng", ".ni", ".nl", ".no", ".np", ".nr", ".nt", ".nu", ".nz", ".om", ".pa",
			".pe", ".pf", ".pg", ".ph", ".pk", ".pl", ".pm", ".pn", ".pt", ".pr", ".pw", ".py", ".qa", ".re", ".ro",
			".ru", ".rw", ".sa", ".sb", ".sc", ".sd", ".se", ".sg", ".sh", ".si", ".sj", ".sk", ".sl", ".sm", ".sn",
			".so", ".sr", ".st", ".su", ".sv", ".sy", ".sz", ".tc", ".td", ".tf", ".tg", ".th", ".tj", ".tk", ".tm",
			".tn", ".to", ".tp", ".tr", ".tt", ".tv", ".tw", ".tz", ".ua", ".ug", ".uk", ".um", ".us", ".uy", ".uz",
			".va", ".vc", ".ve", ".vg", ".vi", ".vn", ".vu", ".wf", ".ws", ".ye", ".yt", ".yu", ".za", ".zm", ".zr",
			".zw", ".ad", ".ae", ".af", ".ag", ".ai", ".al", ".am", ".an", ".ao", ".aq", ".ar", ".as", ".at", ".au",
			".aw", ".az", ".ba", ".bb", ".bd", ".be", ".bf", ".bg", ".bh", ".bi", ".bj", ".bm", ".bn", ".bo", ".br",
			".bs", ".bt", ".bv", ".bw", ".by", ".bz", ".ca", ".cc", ".cf", ".cg", ".ch", ".ci", ".ck", ".cl", ".cm",
			".cn", ".co", ".cq", ".cr", ".cu", ".cv", ".cx", ".cy", ".cz", ".de", ".dj", ".dk", ".dm", ".do", ".dz",
			".ec", ".ee", ".eg", ".eh", ".es", ".et", ".ev", ".fi", ".fj", ".fk", ".fm", ".fo", ".fr", ".ga", ".gb",
			".gd", ".ge", ".gf", ".gh", ".gi", ".gl", ".gm", ".gn", ".gp", ".gr", ".gt", ".gu", ".gw", ".gy", ".hk",
			".hm", ".hn", ".hr", ".ht", ".hu", ".id", ".ie", ".il", ".in", ".io", ".iq", ".ir", ".is", ".it", ".jm",
			".jo", ".jp", ".ke", ".kg", ".kh", ".ki", ".km", ".kn", ".kp", ".kr", ".kw", ".ky", ".kz", ".la", ".lb",
			".lc", ".li", ".lk", ".lr", ".ls", ".lt", ".lu", ".lv", ".ly", ".ma", ".mc", ".md", ".me", ".mg", ".mh",
			".ml", ".mm", ".mn", ".mo", ".mp", ".mq", ".mr", ".ms", ".mt", ".mv", ".mw", ".mx", ".my", ".mz", ".na",
			".nc", ".ne", ".nf", ".ng", ".ni", ".nl", ".no", ".np", ".nr", ".nt", ".nu", ".nz", ".om", ".pa", ".pe",
			".pf", ".pg", ".ph", ".pk", ".pl", ".pm", ".pn", ".pr", ".pt", ".pw", ".py", ".qa", ".re", ".ro", ".rs",
			".ru", ".rw", ".sa", ".sb", ".sc", ".sd", ".se", ".sg", ".sh", ".si", ".sj", ".sk", ".sl", ".sm", ".sn",
			".so", ".sr", ".st", ".su", ".sy", ".sz", ".tc", ".td", ".tf", ".tg", ".th", ".tj", ".tk", ".tl", ".tm",
			".tn", ".to", ".tp", ".tr", ".tt", ".tv", ".tw", ".tz", ".ua", ".ug", ".uk", ".us", ".uy", ".va", ".vc",
			".ve", ".vg", ".vn", ".vu", ".wf", ".ws", ".ye", ".yu", ".za", ".zm", ".zr", ".zw"

	};
	// A C－科研机构； COM－工、商、金融等专业； EDU－教育机构； GOV－政府部门； NET－互
	// 联网络、接入网络的信息中心和运行中心； ORG
	private static final String[] fixupPostfix = new String[] { ".cn", ".bj", ".id", ".co", ".il", ".co", ".jp", ".co",
			".kr", ".co", ".nr", ".co", ".uk", ".co", ".uz", ".co", ".cn", ".ac", ".cn", ".com", ".cn", ".edu", ".cn",
			".gov", ".cn", ".net", ".cn", ".org", ".cn", ".sh", ".cn", ".tj", ".cn", ".cq", ".cn", ".he", ".cn", ".sx",
			".cn", ".nm", ".cn", ".ln", ".cn", ".jl", ".cn", ".hl", ".cn", ".js", ".cn", ".zj", ".cn", ".ah", ".cn",
			".fj", ".cn", ".jx", ".cn", ".sd", ".cn", ".ha", ".cn", ".hb", ".cn", ".hn", ".cn", ".gd", ".cn", ".gx",
			".cn", ".hi", ".cn", ".sc", ".cn", ".gz", ".cn", ".yn", ".cn", ".xz", ".cn", ".sn", ".cn", ".gs", ".cn",
			".qh", ".cn", ".nx", ".cn", ".xj", ".cn", ".tw", ".cn", ".hk", ".cn", ".mo", ".ru", ".net", };
	public static HashMap<String, HashMap<String, Object>> urlPostfixMap = new HashMap<String, HashMap<String, Object>>();
	public static HashMap<String, String> regionalUrlPostfixMap = new HashMap<String, String>();
	public static HashMap<String, String> traditionalUrlPostfixMap = new HashMap<String, String>();
	// 所有顶级域名列表(不带前边的.)
	public static HashMap<String, HashMap<String, Object>> urlPostfixMap_noDot = new HashMap<String, HashMap<String, Object>>();
	// 最初定义的几组业务相关的顶级域名列表(不带前边的.)
	public static HashMap<String, String> regionalUrlPostfixMap_noDot = new HashMap<String, String>();
	// 后来加入的国家级顶级域名列表(不带前边的.)
	public static HashMap<String, String> traditionalUrlPostfixMap_noDot = new HashMap<String, String>();

	static {
		for (int i = 0; i < traditionalUrlPostfix.length; i++) {
			if (traditionalUrlPostfix[i] != null) {
				String temp = traditionalUrlPostfix[i].trim();
				traditionalUrlPostfixMap.put(temp, null);
				urlPostfixMap.put(temp, null);
				if (temp.length() > 0) {
					temp = temp.substring(1);
					traditionalUrlPostfixMap_noDot.put(temp, null);
					urlPostfixMap_noDot.put(temp, null);
				}
			}
		}
		for (int i = 0; i < regionalUrlPostfix.length; i++) {
			if (regionalUrlPostfix[i] != null) {
				String temp = regionalUrlPostfix[i].trim();
				regionalUrlPostfixMap.put(temp, null);
				urlPostfixMap.put(temp, null);
				HashMap<String, Object> obj = (HashMap<String, Object>) urlPostfixMap.get(temp);
				for (String international : internationalTraditionalUrlPostfix) {
					if (obj == null) {
						obj = new HashMap<String, Object>();
						urlPostfixMap.put(temp, obj);
					}
					obj.put(international, null);
				}
				if (temp.length() > 0) {
					temp = temp.substring(1);
					regionalUrlPostfixMap_noDot.put(temp.substring(1), null);
					urlPostfixMap_noDot.put(temp, null);
				}
			}
		}
		for (int i = 0; i < fixupPostfix.length && i + 1 < fixupPostfix.length; i += 2) {
			String key = fixupPostfix[i];
			String val = fixupPostfix[i + 1];
			{
				HashMap<String, Object> obj = (HashMap<String, Object>) urlPostfixMap.get(key);
				if (obj == null) {
					obj = new HashMap<String, Object>();
					urlPostfixMap.put(key, obj);
				}
				obj.put(val, null);
			}
			if (key.length() > 0 && val.length() > 0) {
				key = key.substring(1);
				val = val.substring(1);
				HashMap<String, Object> obj = (HashMap<String, Object>) urlPostfixMap_noDot.get(key);
				if (obj == null) {
					obj = new HashMap<String, Object>();
					urlPostfixMap_noDot.put(key, obj);
				}
				obj.put(val, null);
			}
		}
	}

	public static final String URL_PATH_SEPERATOR = "/";
	public static final String URL_HTTP_HEAD = "http://";
	public static final String URL_DOMAIN_SEPERATOR = ".";

	/**
	 * 判定一个字符串是否为URL，并返回归一化后的URL字符串。 归一化规则：1.以http://打头; 2.端口号为80时，要省略; 3.
	 * 纯域名时，要加"/"作结尾
	 * 
	 * @param query 检查字符串
	 * @return 当结果为URL时，返回归一化的结果，否则返回null。
	 */
	public static final String getLookupUrl(String query) {
		String temp = query.trim();
		String domain;
		String filePath = URL_PATH_SEPERATOR;

		String protocalHead = HTTP_PROTOCOL_HEAD;

		String tempLower = temp.toLowerCase();
		if (tempLower.startsWith(HTTP_PROTOCOL_HEAD)) {
			protocalHead = HTTP_PROTOCOL_HEAD;
			temp = temp.substring(HTTP_PROTOCOL_HEAD_LENGTH);
		} else if (tempLower.startsWith(HTTPS_PROTOCOL_HEAD)) {
			protocalHead = HTTPS_PROTOCOL_HEAD;
			temp = temp.substring(HTTPS_PROTOCOL_HEAD_LENGTH);
		}
		int idxSlash = temp.indexOf('/');
		int idxColon = temp.indexOf(':');

		int port = 80;
		if (idxSlash < 0) { // 纯域名
			if (idxColon > 0) {
				try {
					port = Integer.parseInt(temp.substring(idxColon + 1));
				} catch (NumberFormatException e) {
					return null;
				}
				domain = temp.substring(0, idxColon);
			} else
				domain = temp;
			filePath = URL_PATH_SEPERATOR;
		} else { // 域名＋目录
			if (idxColon > 0 && idxColon < idxSlash) {
				try {
					port = Integer.parseInt(temp.substring(idxColon + 1, idxSlash));
				} catch (NumberFormatException e) {
					return null;
				}
				domain = temp.substring(0, idxColon);
			} else {
				domain = temp.substring(0, idxSlash);
			}
			filePath = temp.substring(idxSlash);
		}
		// 判断 port 是否在合法范围内
		if (port <= 0 || port > 65535) {
			return null;
		}
		// 判断域名部分是否合法

		domain = validateDomain(domain);
		// 确定为URL
		if (domain != null) {

			String result;
			if (port == 80) {
				result = protocalHead + domain + filePath;
			} else {
				result = protocalHead + domain + ':' + port + filePath;
			}
			return result;
		}
		return null;
	}

	public static final boolean isIP(String domain) {
		if (domain == null)
			return false;

		boolean isValid = false;
		// 判断一：xxx.xxx.xxx.xxx形式的IP地址
		try {
			StringTokenizer token = new StringTokenizer(domain, URL_DOMAIN_SEPERATOR);
			int i;
			for (i = 0; i < 4; i++) {
				int tempInt = Integer.parseInt(token.nextToken());
				if (tempInt < 0 || tempInt > 255)
					break;
			}
			if (i == 4) {
				if (!token.hasMoreTokens()) {
					// 验证成功
					isValid = true;
				}
			}
		} catch (NoSuchElementException e) {
		} catch (NumberFormatException e) {
		}
		return isValid;

	}

	/**
	 * 检验域名部分是否符合RFC规范
	 * 
	 * @param domain
	 * @return 如果返回null，说明参数不是域名，否则就返回domain自身
	 * 
	 */
	public static final String validateDomain(String domain) {
		if (domain == null)
			return null;

		// 判断零：不含非法字符
		for (int i = 0; i < domain.length(); i++) {

			char c = domain.charAt(i);

			if (c > 0x7f) {
				return null;
			} else if (!isLetterOrDigit(c)) {
				// 域名不能包含符号, 但可以包含'.'或'-'或'_',且不能以这三个符号打头或结尾
				if ((c == '.' && i != 0 && i != domain.length() - 1)
						|| ((c == '-' || c == '_') && i != 0 && i != domain.length() - 1)) {
					continue;
				} else {
					return null;
				}
			}
		}

		boolean isValid = false;
		do {
			if (isIP(domain)) {
				isValid = true;
				break;
			}
			// 否则判断是否满足其他的形式
			{
				isValid = true;
				// 判断二.1：xx.xxxx.com形式的域名(判断字符组成的合法性)
				StringTokenizer token = new StringTokenizer(domain, URL_DOMAIN_SEPERATOR);
				while (token.hasMoreTokens()) {
					String tok = token.nextToken();
					if (tok.length() == 0 || tok.startsWith(".") || tok.endsWith(".") || tok.startsWith("-")
							|| tok.endsWith("-") || tok.startsWith("_") || tok.endsWith("_")) {
						isValid = false;
						break;
					}
				}
				if (isValid && domain.indexOf("..") >= 0)
					isValid = false;
				// 不满足域名形式，跳出
				if (!isValid)
					break; // do .. while(false);
			}
			// 判断二：xx.xxxx.com形式的域名(根据后缀判断)
			{
				isValid = false;
				domain = domain.toLowerCase();
				int p = domain.lastIndexOf('.');
				try {
					String postfix = domain.substring(p);
					if (urlPostfixMap.containsKey(postfix)) {
						isValid = true;
						// 验证成功，跳出不再执行其他的模板判断
						break; // do .. while(false);
					}
				} catch (IndexOutOfBoundsException e) {
				}
			}
		} while (false);

		// 确定为URL
		if (isValid) {
			return domain;
		} else {
			return null;
		}
	}

	/**
	 * 判断URL是不是域名形式的URL
	 * 
	 * @param url url必须是符合本类中的URL归一化函数规则的URL
	 * @return
	 */
	public static final boolean isDomain(String url) {
		int t = 0;
		if (url.startsWith(HTTP_PROTOCOL_HEAD)) {
			t = HTTP_PROTOCOL_HEAD_LENGTH;
		} else if (url.startsWith(HTTPS_PROTOCOL_HEAD)) {
			t = HTTPS_PROTOCOL_HEAD_LENGTH;
		}
		t = url.indexOf('/', t);
		if (t < 0 || t == url.length() - 1)
			return true;
		return false;
	}

	/**
	 * 找出url的一级域名 一级域名的格式: [a-z0-9]([a-z0-9\-]*[a-z0-9])?\.{顶级域名} 或
	 * [a-z0-9]([a-z0-9\-]*[a-z0-9])?\.{域名商提供的域名}.{顶级域名}
	 * 
	 * @param url url必须是符合本类中的URL归一化函数规则的URL
	 * @return 如果参数不是一个url返回null, 否则返回对应的顶级域名串,如:
	 *         "http://www.sogou.com.cn/"返回值是"sogou.com.cn"
	 */
	@SuppressWarnings("rawtypes")
	public static final String getMainDomain(String url) {

		String domain = getDomainWithoutPort(url);

		if (domain == null)
			return null;

		HashMap map = urlPostfixMap;
		int lastDot = domain.length();
		int last = lastDot;
		do {
			last = domain.lastIndexOf('.', lastDot - 1);

			// 前边已经没有'.'了
			if (last < 0)
				break;
			// 已经没有第n+1级域名了
			if (map == null)
				break;

			String topDomain = domain.substring(last, lastDot);

			if (!map.containsKey(topDomain))
				break;
			else
				map = (HashMap) map.get(topDomain);
			lastDot = last;
		} while (true);
		if (lastDot == domain.length()) {
			return null; // 没有顶级域名
		} else {
			if (last < 0) { // xxx.com.cn
				return domain;
			} else { // xxx.domain.com.cn
				return domain.substring(last + 1);
			}
		}
	}

	/**
	 * 检查url是否为不以www开头的一级域名 一级域名的格式: [a-z0-9]([a-z0-9\-]*[a-z0-9])?\.{任意顶级域名} 或
	 * [a-z0-9]([a-z0-9\-]*[a-z0-9])?\.{传统顶级域名}.{地区顶级域名}
	 * 
	 * @param url url必须是符合本类中的URL归一化函数规则的URL
	 * @return
	 */
	public static final boolean isNonWWW(String url) {

		String domain = getDomainWithoutPort(url);

		if (domain == null)
			return false;

		String mainDomain = getMainDomain(domain);
		return (mainDomain != null && mainDomain.equals(domain));

	}

	/**
	 * 从URL串中获取QueryString串
	 * 
	 * @param url 完整的URL串，包括协议头、域名部分等
	 * @return null 如果url参数为null，或url中不含'?'字符
	 *         否则根据RFC标准,返回第一个'?'以后，第一个'#'中间的部分作为QueryString串
	 */
	public static final String getQueryString(String url) {
		if (url == null)
			return null;
		int index = url.indexOf('?');
		if (index < 0) {
			return null;
		}
		index++;
		int hash = url.indexOf('#', index);
		if (hash < 0) {
			return url.substring(index);
		} else {
			return url.substring(index, hash);
		}
	}

	private static final boolean checkHexChar(byte[] str, int i) {
		if (str == null || i >= str.length)
			return false;
		byte ch1 = str[i];
		return (ch1 >= '0' && ch1 <= '9') || (ch1 >= 'a' && ch1 <= 'f') || (ch1 >= 'A' && ch1 <= 'F');
	}

	private static final boolean checkMultiHexChar(byte[] str, int idx, int n) {
		for (int i = 0; i < n; i++) {
			if (!checkHexChar(str, idx + i))
				return false;
		}
		return true;
	}

	private static final boolean tryPut(byte[] buff, int idx, byte b) {
		if (buff == null || idx < 0 || idx >= buff.length)
			return false;
		buff[idx] = b;
		return true;
	}
//	private static final boolean tryMultiPut(byte[]buff, int idx, byte[] b){
//		if( buff == null || b == null || idx < 0 || idx + b.length > buff.length ) return false;
//		System.arraycopy(b, 0, buff, idx, b.length);
//		return true;
//	}

	/**
	 * 通用url解码，将%xx等编码的字符串直接转成对应的byte，不考虑具体编码
	 * 
	 * @see genericUrlDecode(String url, byte[]buff, int flag)
	 * @param url  被解码的url.
	 * @param buff 用于存放解码后数据的缓存
	 * @return >=0 解析成功，buff中数据的长度 -1 参数有问题，url为空 -2 参数有问题，buff空间不足 -3 未知问题 -4
	 *         确认编码为GBK
	 */
	public static final int genericUrlDecode(String url, byte[] buff) {
		return genericUrlDecode(url, buff, 0);
	}

	/**
	 * 通用url解码，将%xx直接转成对应的byte，不考虑具体编码 支持%FF，%uFFFF，四种编码方法。注意，这里指的编码同"GBK"不同，不要混淆
	 * 
	 * @param url  被解码的url.
	 * @param buff 用于存放解码后数据的缓存
	 * @param flag 格式扩展参数， 0表示仅针对url编码处理 第一位为1，表示需要处理apache格式的编码
	 * @return >=0 解析成功，buff中数据的长度 -1 参数有问题，url为空 -2 参数有问题，buff空间不足 -3 未知问题 -4
	 *         确认编码为GBK
	 */
	public static final int genericUrlDecode(String url1, byte[] buff, int flag) {
		if (url1 == null || buff == null) {
			return -1;
		}
		byte[] data = url1.getBytes();
		byte[] bb = buff;
		int idx = 0;
		for (int i = 0; i < data.length; i++) { // 已知的做了编码的字符串必定超过3个字符
			if (data[i] == '%') { // 处理%FF和%uFFFF的情况
				if (checkMultiHexChar(data, i + 1, 2)) { // 处理%ff格式
					try {
						int a = Integer.parseInt(new String(data, i + 1, 2), 16);
						if (!tryPut(bb, idx, (byte) a))
							return -2;
						idx++;
						i += 2;
					} catch (Exception e) {
						return -3;
					}
				} else if (i + 1 < data.length && data[i + 1] == 'u') { // 处理%uFFFF格式
					if (checkMultiHexChar(data, i + 2, 4)) {
						// 可以确认为gbk编码
						return -4;
					}
				}
			} else if ((flag & 1) == 1) {
				// 为apache格式解析预留的扩展
				if (data[i] == '\\' && i + 1 < data.length && data[i + 1] == 'x') { // 处理\xFF格式
					if (checkMultiHexChar(data, i + 2, 2)) {
						try {
							int a = Integer.parseInt(new String(data, i + 2, 2), 16);
							if (!tryPut(bb, idx, (byte) a))
								return -2;
							idx++;
							i += 3;
						} catch (Exception e) {
							return -3;
						}
					}
				}
			} else {
				// 放入其他字符
				if (!tryPut(bb, idx, (byte) data[i]))
					return -2;
				idx++;
			}
		}

		return idx;
	}

	/**
	 * 根据一串byte码流判断可能的字符编码 依赖于jchardet包，根据其提供的probe机制判断对应的编码类型，有一定的误判率
	 * 码流必须是按照一种统一的规则来编码的，码流的数据越多，判定的正确性越高
	 * 
	 * @param s 待判定的码流
	 * @return 可能的编码类型数组，"UTF-8", "GBK", "UTF-16"等等. 如果参数有问题，返回值为null
	 */
//	public static final String[] probeAllCharsets(byte[] s) {
//		if( s == null || s.length == 0 ) return null;
//		return probeAllCharsets(s,s.length);
//	}
//	public static final String[] probeAllCharsets(byte[] s, int limit) {
//		if( s == null || limit <=0 || limit > s.length ) return null;
//		nsDetector det = new nsDetector(nsDetector.SIMPLIFIED_CHINESE);
//		nsICharsetDetectionObserver c = null;
//		det.Init(c);
//		int limitPerIteration = 1024;
//		if (limit <= limitPerIteration) {
//			det.DoIt(s, limit, false);
//			det.Done();
//
//			String prob[] = det.getProbableCharsets();
//			return prob;
//		}
//		byte[] bytes = new byte[limitPerIteration];
//		int index = 0;
//		while ((index + limitPerIteration) < limit) {
//			System.arraycopy(s, index, bytes, 0, limitPerIteration);
//			if (det.DoIt(bytes, bytes.length, false)) {
//				det.Done();
//				String prob[] = det.getProbableCharsets();
//				return prob;
//			}
//			index += limitPerIteration;
//		}
//		System.arraycopy(s, index, bytes, 0, limit - index);
//		det.DoIt(bytes, bytes.length, false);
//		det.Done();
//		String prob[] = det.getProbableCharsets();
//
//		return prob;
//	}

	/**
	 * 判断字符串可能的编码类型
	 * 
	 * @param line 经过编码的字符串，一般应该是url参数
	 * @return 可能的编码类型"GBK"，"UTF-8"等，如果出现错误，返回值是null
	 */
//	public static final String probeCharset(String line){
//		String dft = null;
//		String ret = null;
//		int limit = Integer.MAX_VALUE>>2;
//		if( line == null || line.length() == 0 || line.length() >= limit ) 
//			return dft;
//		byte[] buff = new byte[line.length()*2];
//		int n = genericUrlDecode(line, buff);
//		if( n >= 0 ){
//			ret = probeCharset(buff, 0, n);
//			try{
//				String oldret = probeCharset2(buff, 0, n);
//				if (!ret.equals(oldret)){
//					StringBuilder sb = new StringBuilder();
//					sb.append("charset diff:");
//					sb.append(ret);
//					sb.append(" ");
//					sb.append(oldret);
//					sb.append(" ");
//					sb.append(line);
//					System.out.println(sb.toString());
//				}
//			}catch(Exception e){
//				System.out.println("charset diff:exception "+line);
//			}
//		} else if( n == -1 ){
//			// 参数有问题
//			ret = dft;
//		} else if( n == -2 ){ // 空间不足，重试
//			buff = new byte[line.length()*4];
//			n = genericUrlDecode(line, buff);
//			if( n < 0 ){
//				ret = dft;
//			} else {
//				ret = probeCharset(buff, 0, n);
//			}
//		} else if( n == -4 ){ // n == -4 编码类型确认为GBK
//			ret = "GBK";
//		} else {// n == -3 未知错误
//			ret = dft;
//		}
//		return ret;
//		
//	}

//	private static final float count(String str){
//		if( str == null || str.length() == 0 ) return 1.0f;
//		int total = 0;
//		float sum = 0.0f;
//		for(int i=0;i<str.length(); i++){
//			char ch = str.charAt(i);
//			if( ch > 128 ){
//				total ++;
//				if( !GB2312Charset.has(ch) ){
//					if( ch >= '\ufff0' ){
//						sum += 2.0f;
//					} else {
//						sum += 1.0f;
//					}
//				} else {
//					if( ch < 39 * 8 * 64 ){
//						sum += 0.05;
//					} else if( ch < 58 * 8 * 64 ){
//						sum += 0.07;
//					} else if( ch > 80 * 8 * 64 ){
//						sum += 0.2;
//					}
//				}
//			}
//		}
//		return total == 0 ? 1 : (sum)/total;
//	}

//	private static final float count2(String str){
//		if( str == null || str.length() == 0 ) return 1.0f;
//		int total = 0;
//		float sum = 0.0f;
//		for(int i=0;i<str.length(); i++){
//			char ch = str.charAt(i);
//			if( ch > 128 ){
//				total ++;
//				if( !GB2312Charset.has(ch) ){
//					if( ch >= '\ufff0' ){
//						sum += 2.0f;
//					} else {
//						sum += 1.0f;
//					}
//				} else {
//					if( ch < 39 * 8 * 64 ){
//						sum += 0.05;
//					} else if( ch < 58 * 8 * 64 ){
//						sum += 0.07;
//					} else if( ch > 80 * 8 * 64 ){
//						sum += 0.2;
//					} else if( !GB2312Charset.has2312(ch) ){
//						sum += 0.15;
//					}
//				}
//			}
//		}
//		return total == 0 ? 1 : (sum)/total;
//	}
	/**
	 * 判定一段码流对应的编码类型
	 * 
	 * @param buff  原始码流，数据量越大越准确，但是性能也随之下降
	 * @param start 起始位置
	 * @param limit 码流的长度
	 * @return null 参数有误，或者判定失败
	 */
//	public static final String probeCharset(byte[] bb, int start, int limit){
//
//		if (bb == null || start < 0 || limit <= 0 || limit + start > bb.length) {
//			return null;
//		}
//		byte[] buff = new byte[limit];
//		System.arraycopy(bb, start, buff, 0, limit);
//		String[] css = probeAllCharsets(buff);
//		for (String cs : css) {
//			if ( cs.equals("GBK") || cs.equals("GB2312")
//					|| cs.equals("GB18030"))
//				return "GBK";
//			else if( cs.equals("UTF-8") ){
//				// fixup
//				// 发现chardet包对“一”等字的识别有明显的问题
//				{
//					try{
//						String str = new String( bb, start, limit, cs);
//						if (str != null && str.contains("中國")){
//							return "UTF-8";
//						}
//						if (str != null && str.contains("豬v")){
//							return "GBK";
//						}
//						if(count(str) > 0.5){
//							cs = "GBK";
//						}
//						if(count(str) == 0.5){
//							try{
//								str = new String( bb, start, limit, "GBK");
//								if (count(str) < 0.5){
//									return "GBK";
//								}
//							}catch(Exception e){
//								cs = "UTF-8";
//							}
//						}
//					}catch(Exception e){
//						cs = "GBK";
//					}
//				}
//				return cs;
//			}
//		}
//		return null;
//
//	}
//	
//	public static final String probeCharset2(byte[] bb, int start, int limit){
//
//		if (bb == null || start < 0 || limit <= 0 || limit + start > bb.length) {
//			return null;
//		}
//		byte[] buff = new byte[limit];
//		System.arraycopy(bb, start, buff, 0, limit);
//		String[] css = probeAllCharsets(buff);
//		String maybeSET = null;
//		for (String cs : css) {
//			if ( cs.equals("GBK") || cs.equals("GB2312")
//					|| cs.equals("GB18030"))
//				return "GBK";
//			else if( cs.equals("UTF-8") ){
//				// fixup
//				// 发现chardet包对“一”等字的识别有明显的问题
//				{
//					try{
//						String str = new String( bb, start, limit, cs);
//						float cnt = count2(CharsetConverter.getInstance().getGBKOfBig5(str));  
//						if (cnt > 0.5){
//							return "GBK";
//						}
//						
//						try{
//							str = new String( bb, start, limit, "GBK");
//							float cnt2 = count2(str);  
//							if (cnt2+0.1 < cnt){
//								//gbk的可能性比utf8高，但如果chardet没有判断出gbk，则仍然用utf8
//								maybeSET = "UTF-8";
//								continue;
//							}else if (cnt2 == 0.0 && cnt > 0.0){
//								maybeSET = "UTF-8";
//								continue;
//							}
//						}catch(Exception e){
//							cs = "UTF-8";
//						}
//						
//					}catch(Exception e){
//						cs = "GBK";
//					}
//				}
//				return cs;
//			}
//		}
//		return maybeSET;
//
//	}

	/**
	 * 从url参数，或者带参数的url中截取一个特定的参数，考虑了"#"锚标的情况
	 * 
	 * @param url   原始字符串
	 * @param param 需要找的参数
	 * @return 找到的对应参数 null 参数不存在，或者参数非法
	 */
	public static final String getParameter(String url, String param) {
		if (url == null || param == null)
			return null;
		String key = param + "=";
		int right = url.indexOf("/#");
		if (right < 0) {
			right = url.indexOf('#');
		} else {
			right = url.indexOf('#', right + 2);
		}
		if (right < 0)
			right = url.length();
		int left = -1;
		while (true) {
			int idx = url.indexOf(key, left + 1);
			if (idx < 0) {
				return null;
			} else if (idx == 0) {
				left = idx;
				break;
			} else if (url.charAt(idx - 1) == '?' || url.charAt(idx - 1) == '&' || url.charAt(idx - 1) == '#') {
				left = idx;
				break;
			} else {
				left = idx;
			}
		}
		// 未找到
		if (left < 0)
			return null;
		left += key.length();
		// 处理#锚标
		if (left >= right)
			return null;

		int end = url.indexOf('&', left + 1);
		if (end > 0 && end < right)
			right = end;
		return url.substring(left, right);
	}

}
