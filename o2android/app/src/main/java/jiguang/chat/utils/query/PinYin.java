package jiguang.chat.utils.query;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PinYin {
	private static final String ASSET = "pinyin/index.dat";

	private static final char START = 0x4e00;
	
	private static final char END = 0x9fa5;

	private static final String[] pinyin = { "a", "ai", "an", "ang", "ao",
			"ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi",
			"bian", "biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai",
			"can", "cang", "cao", "ce", "ceng", "cha", "chai", "chan", "chang",
			"chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu",
			"chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong",
			"cou", "cu", "cuan", "cui", "cun", "cuo", "da", "dai", "dan",
			"dang", "dao", "de", "deng", "di", "dian", "diao", "die", "ding",
			"diu", "dong", "dou", "du", "duan", "dui", "dun", "duo", "e", "en",
			"er", "fa", "fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu",
			"ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng",
			"gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun",
			"guo", "ha", "hai", "han", "hang", "hao", "he", "hei", "hen",
			"heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui",
			"hun", "huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin",
			"jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai",
			"kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku",
			"kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai",
			"lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian",
			"liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu",
			"lv", "luan", "lue", "lun", "luo", "ma", "mai", "man", "mang",
			"mao", "me", "mei", "men", "meng", "mi", "mian", "miao", "mie",
			"min", "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan",
			"nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang",
			"niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan",
			"nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei",
			"pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po",
			"pu", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing",
			"qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao",
			"re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui",
			"run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen",
			"seng", "sha", "shai", "shan", "shang", "shao", "she", "shen",
			"sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",
			"shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui",
			"sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng",
			"ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan",
			"tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen",
			"weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie",
			"xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya",
			"yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong",
			"you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang",
			"zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang",
			"zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu",
			"zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi",
			"zong", "zou", "zu", "zuan", "zui", "zun", "zuo" };
	
	private static final String[] pinyinT9 = { "2", "24", "26", "264", "26",
			"22", "224", "226", "2264", "226", "234", "236", "2364", "24",
			"2426", "2426", "243", "246", "2464", "26", "28", "22", "224",
			"226", "2264", "226", "23", "2364", "242", "2424", "2426", "24264",
			"2426", "243", "2436", "24364", "244", "24664", "2468", "248",
			"24824", "24826", "248264", "2484", "2486", "2486", "24", "2664",
			"268", "28", "2826", "284", "286", "286", "32", "324", "326",
			"3264", "326", "33", "3364", "34", "3426", "3426", "343", "3464",
			"348", "3664", "368", "38", "3826", "384", "386", "386", "3", "36",
			"37", "32", "326", "3264", "334", "336", "3364", "36", "368", "38",
			"42", "424", "426", "4264", "426", "43", "434", "436", "4364",
			"4664", "468", "48", "482", "4824", "4826", "48264", "484", "486",
			"486", "42", "424", "426", "4264", "426", "43", "434", "436",
			"4364", "4664", "468", "48", "482", "4824", "4826", "48264", "484",
			"486", "486", "54", "542", "5426", "54264", "5426", "543", "546",
			"5464", "54664", "548", "58", "5826", "583", "586", "52", "524",
			"526", "5264", "526", "53", "536", "5364", "5664", "568", "58",
			"582", "5824", "5826", "58264", "584", "586", "586", "52", "524",
			"526", "5264", "526", "53", "534", "5364", "54", "542", "5426",
			"54264", "5426", "543", "546", "5464", "548", "5664", "568", "58",
			"58", "5826", "583", "586", "586", "62", "624", "626", "6264",
			"626", "63", "634", "636", "6364", "64", "6426", "6426", "643",
			"646", "6464", "648", "66", "668", "68", "62", "624", "626",
			"6264", "626", "63", "634", "636", "6364", "64", "6426", "64264",
			"6426", "643", "646", "6464", "648", "6664", "68", "68", "6826",
			"683", "686", "6", "68", "72", "724", "726", "7264", "726", "734",
			"736", "7364", "74", "7426", "7426", "743", "746", "7464", "76",
			"78", "74", "742", "7426", "74264", "7426", "743", "746", "7464",
			"74664", "748", "78", "7826", "783", "786", "726", "7264", "726",
			"73", "736", "7364", "74", "7664", "768", "78", "7826", "784",
			"786", "786", "72", "724", "726", "7264", "726", "73", "736",
			"7364", "742", "7424", "7426", "74264", "7426", "743", "7436",
			"74364", "744", "7468", "748", "7482", "74824", "74826", "748264",
			"7484", "7486", "7486", "74", "7664", "768", "78", "7826", "784",
			"786", "786", "82", "824", "826", "8264", "826", "83", "8364",
			"84", "8426", "8426", "843", "8464", "8664", "868", "88", "8826",
			"884", "886", "886", "92", "924", "926", "9264", "934", "936",
			"9364", "96", "98", "94", "942", "9426", "94264", "9426", "943",
			"946", "9464", "94664", "948", "98", "9826", "983", "986", "92",
			"926", "9264", "926", "93", "94", "946", "9464", "96", "9664",
			"968", "98", "9826", "983", "986", "92", "924", "926", "9264",
			"926", "93", "934", "936", "9364", "942", "9424", "9426", "94264",
			"9426", "943", "9436", "94364", "944", "94664", "9468", "948",
			"9482", "94824", "94826", "948264", "9484", "9486", "9486", "94",
			"9664", "968", "98", "9826", "984", "986", "986" };

	private static final char[] leadingCUp = new char[] { 'A', 'A', 'A', 'A', 'A',
			'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B',
			'B', 'B', 'B', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C',
			'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C',
			'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'D', 'D', 'D',
			'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',
			'D', 'D', 'D', 'D', 'E', 'E', 'E', 'F', 'F', 'F', 'F', 'F', 'F',
			'F', 'F', 'F', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G',
			'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'H', 'H', 'H', 'H',
			'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H',
			'H', 'H', 'J', 'J', 'J', 'J', 'J', 'J', 'J', 'J', 'J', 'J', 'J',
			'J', 'J', 'J', 'K', 'K', 'K', 'K', 'K', 'K', 'K', 'K', 'K', 'K',
			'K', 'K', 'K', 'K', 'K', 'K', 'K', 'K', 'L', 'L', 'L', 'L', 'L',
			'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L',
			'L', 'L', 'L', 'L', 'L', 'L', 'L', 'M', 'M', 'M', 'M', 'M', 'M',
			'M', 'M', 'M', 'M', 'M', 'M', 'M', 'M', 'M', 'M', 'M', 'M', 'M',
			'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N',
			'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'O', 'O', 'P',
			'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P',
			'P', 'P', 'Q', 'Q', 'Q', 'Q', 'Q', 'Q', 'Q', 'Q', 'Q', 'Q', 'Q',
			'Q', 'Q', 'Q', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R',
			'R', 'R', 'R', 'R', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S',
			'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S',
			'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'T',
			'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T',
			'T', 'T', 'T', 'T', 'T', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W',
			'W', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X',
			'X', 'X', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y', 'Y',
			'Y', 'Y', 'Y', 'Y', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z',
			'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z',
			'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z', 'Z',
			'Z' };
	
	private static final char[] leadingCLo = new char[] { 'a', 'a', 'a', 'a',
			'a', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b',
			'b', 'b', 'b', 'b', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c',
			'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c',
			'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'c', 'd', 'd',
			'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd',
			'd', 'd', 'd', 'd', 'd', 'e', 'e', 'e', 'f', 'f', 'f', 'f', 'f',
			'f', 'f', 'f', 'f', 'g', 'g', 'g', 'g', 'g', 'g', 'g', 'g', 'g',
			'g', 'g', 'g', 'g', 'g', 'g', 'g', 'g', 'g', 'g', 'h', 'h', 'h',
			'h', 'h', 'h', 'h', 'h', 'h', 'h', 'h', 'h', 'h', 'h', 'h', 'h',
			'h', 'h', 'h', 'j', 'j', 'j', 'j', 'j', 'j', 'j', 'j', 'j', 'j',
			'j', 'j', 'j', 'j', 'k', 'k', 'k', 'k', 'k', 'k', 'k', 'k', 'k',
			'k', 'k', 'k', 'k', 'k', 'k', 'k', 'k', 'k', 'l', 'l', 'l', 'l',
			'l', 'l', 'l', 'l', 'l', 'l', 'l', 'l', 'l', 'l', 'l', 'l', 'l',
			'l', 'l', 'l', 'l', 'l', 'l', 'l', 'l', 'm', 'm', 'm', 'm', 'm',
			'm', 'm', 'm', 'm', 'm', 'm', 'm', 'm', 'm', 'm', 'm', 'm', 'm',
			'm', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n',
			'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'o', 'o',
			'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p',
			'p', 'p', 'p', 'q', 'q', 'q', 'q', 'q', 'q', 'q', 'q', 'q', 'q',
			'q', 'q', 'q', 'q', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'r', 'r',
			'r', 'r', 'r', 'r', 'r', 's', 's', 's', 's', 's', 's', 's', 's',
			's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's',
			's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's', 's',
			't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't',
			't', 't', 't', 't', 't', 't', 'w', 'w', 'w', 'w', 'w', 'w', 'w',
			'w', 'w', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x',
			'x', 'x', 'x', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y',
			'y', 'y', 'y', 'y', 'y', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z',
			'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z',
			'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z', 'z',
			'z', 'z', };
		
	private static final String[] leadingSUp = new String[] { "A", "A", "A",
			"A", "A", "B", "B", "B", "B", "B", "B", "B", "B", "B", "B", "B",
			"B", "B", "B", "B", "B", "C", "C", "C", "C", "C", "C", "C", "C",
			"C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C",
			"C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "C", "D",
			"D", "D", "D", "D", "D", "D", "D", "D", "D", "D", "D", "D", "D",
			"D", "D", "D", "D", "D", "D", "E", "E", "E", "F", "F", "F", "F",
			"F", "F", "F", "F", "F", "G", "G", "G", "G", "G", "G", "G", "G",
			"G", "G", "G", "G", "G", "G", "G", "G", "G", "G", "G", "H", "H",
			"H", "H", "H", "H", "H", "H", "H", "H", "H", "H", "H", "H", "H",
			"H", "H", "H", "H", "J", "J", "J", "J", "J", "J", "J", "J", "J",
			"J", "J", "J", "J", "J", "K", "K", "K", "K", "K", "K", "K", "K",
			"K", "K", "K", "K", "K", "K", "K", "K", "K", "K", "L", "L", "L",
			"L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L", "L",
			"L", "L", "L", "L", "L", "L", "L", "L", "L", "M", "M", "M", "M",
			"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M",
			"M", "M", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N",
			"N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "N", "O",
			"O", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P", "P",
			"P", "P", "P", "P", "Q", "Q", "Q", "Q", "Q", "Q", "Q", "Q", "Q",
			"Q", "Q", "Q", "Q", "Q", "R", "R", "R", "R", "R", "R", "R", "R",
			"R", "R", "R", "R", "R", "R", "S", "S", "S", "S", "S", "S", "S",
			"S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S",
			"S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S",
			"S", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T",
			"T", "T", "T", "T", "T", "T", "T", "W", "W", "W", "W", "W", "W",
			"W", "W", "W", "X", "X", "X", "X", "X", "X", "X", "X", "X", "X",
			"X", "X", "X", "X", "Y", "Y", "Y", "Y", "Y", "Y", "Y", "Y", "Y",
			"Y", "Y", "Y", "Y", "Y", "Y", "Z", "Z", "Z", "Z", "Z", "Z", "Z",
			"Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z",
			"Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z",
			"Z", "Z", "Z" };
	
	private static final String[] leadingSLo = new String[] { "a", "a", "a",
			"a", "a", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b",
			"b", "b", "b", "b", "b", "c", "c", "c", "c", "c", "c", "c", "c",
			"c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c",
			"c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "d",
			"d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d",
			"d", "d", "d", "d", "d", "d", "e", "e", "e", "f", "f", "f", "f",
			"f", "f", "f", "f", "f", "g", "g", "g", "g", "g", "g", "g", "g",
			"g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "h", "h",
			"h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h",
			"h", "h", "h", "h", "j", "j", "j", "j", "j", "j", "j", "j", "j",
			"j", "j", "j", "j", "j", "k", "k", "k", "k", "k", "k", "k", "k",
			"k", "k", "k", "k", "k", "k", "k", "k", "k", "k", "l", "l", "l",
			"l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l",
			"l", "l", "l", "l", "l", "l", "l", "l", "l", "m", "m", "m", "m",
			"m", "m", "m", "m", "m", "m", "m", "m", "m", "m", "m", "m", "m",
			"m", "m", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n",
			"n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "o",
			"o", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p",
			"p", "p", "p", "p", "q", "q", "q", "q", "q", "q", "q", "q", "q",
			"q", "q", "q", "q", "q", "r", "r", "r", "r", "r", "r", "r", "r",
			"r", "r", "r", "r", "r", "r", "s", "s", "s", "s", "s", "s", "s",
			"s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s",
			"s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s", "s",
			"s", "t", "t", "t", "t", "t", "t", "t", "t", "t", "t", "t", "t",
			"t", "t", "t", "t", "t", "t", "t", "w", "w", "w", "w", "w", "w",
			"w", "w", "w", "x", "x", "x", "x", "x", "x", "x", "x", "x", "x",
			"x", "x", "x", "x", "y", "y", "y", "y", "y", "y", "y", "y", "y",
			"y", "y", "y", "y", "y", "y", "z", "z", "z", "z", "z", "z", "z",
			"z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z",
			"z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z", "z",
			"z", "z", "z", };
	
	private static Context context;
	
	private static byte[] indexes;
	
	private static final Object lock = new Object();
	
	private static byte[] loadIndexes(Context context) {
		byte[] indexes = null;
		
		InputStream is = null;
		try {
			is = context.getAssets().open(ASSET);
			
			indexes = new byte[(END - START + 1) * 2];
			
			is.read(indexes);	
		} catch (Throwable tr) {
			tr.printStackTrace();
			
			indexes = null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return indexes;
	}
	
	private static void ensureIndexes() {
		if (indexes != null) {
			return;
		}
		
		//
		// usually not reach 
		// 
		
		byte[] tIndexes = null;
		
		// protect
		synchronized (lock) {
			if (context != null) {
				tIndexes = loadIndexes(context);
				if (tIndexes != null) {
					context = null;
				}
			}
		}
		
		// may race
		if (tIndexes != null) {
			indexes = tIndexes;
		}
	}
	
	public static final void init(Context ctx) {
		context = ctx.getApplicationContext();
	}
	
	public static final String validate() {		
		ensureIndexes();
		
		StringBuilder invalid = new StringBuilder();
		StringBuilder miss = new StringBuilder();
		
		for (char c = START; c <= END; c++) {
			// offset
			int offset = (c - START) * 2;
			
			// raw
			int raw = indexes[offset] << 8 | 0xff & indexes[offset + 1];
			
			if (raw < 0 || raw > pinyin.length) {
				invalid.append(c);
			}
			
			if (raw == 0) {
				miss.append(c);
			}
		}
		
		boolean t9 = validatePinYinT9();
		
		String result = "pinyin(" + pinyin.length + ") " +  
				"pinyinT9(" + t9 + ") " +  
				"leadingCUp(" + leadingCUp.length +  ") " + 
				"leadingCLo(" + leadingCLo.length +  ") " + 
				"leadingSUp(" + leadingSUp.length +  ") " + 
				"leadingSLo(" + leadingSLo.length +  ") " + 
				"invalid(" + invalid.toString() +  ") " + 
				"miss(" + miss.toString() + ") ";
		
		return result;
	}
	
	private static final char[] T9 = { '2', '2', '2', '3', '3', '3', '4', '4',
		'4', '5', '5', '5', '6', '6', '6', '7', '7', '7', '7', '8', '8',
		'8', '9', '9', '9', '9' };
	
	private static final boolean validatePinYinT9() {
		if (pinyin.length != pinyinT9.length) {
			return false;
		}
		
		for (int i = 0; i < pinyin.length; i++) {
			String p = pinyin[i];
			String t = pinyinT9[i];
			
			if (p.length() != t.length()) {
				return false;
			}
			
			for (int j = 0; j < p.length(); j++) {
				if (T9[p.charAt(j) - 'a'] != t.charAt(j)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static final int getIndex(char c) {
		ensureIndexes();
		
		if (indexes == null) {
			return -1;
		}
		
		// out of bound
		if (c < START || c > END) {
			return -1;
		}
		
		// offset
		int offset = (c - START) * 2;
		
		// raw
		int raw = indexes[offset] << 8 | 0xff & indexes[offset + 1];
	
		return raw - 1;
	}
	
	public static final char getLeadingUp(char c, char d) {
		int index = getIndex(c);
		
		return index != -1 ? leadingCUp[index] : d;
	}
	
	public static final char getLeadingLo(char c, char d) {
		int index = getIndex(c);
		
		return index != -1 ? leadingCLo[index] : d;
	}
	
	public static final String getLeadingUp(char c) {
		int index = getIndex(c);
		
		return index != -1 ? leadingSUp[index] : null;
	}
	
	public static final String getLeadingLo(char c) {
		int index = getIndex(c);
		
		return index != -1 ? leadingSLo[index] : null;
	}
	
	public static final String getLeadingUp(String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		
		StringBuilder leadings = new StringBuilder();
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			int index = getIndex(c);
						
			if (index != -1) {
				leadings.append(leadingCUp[index]);
			} else {
				leadings.append(c);
			}
		}
			
		return leadings.toString();
	}
	
	public static final String getLeadingLo(String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		
		StringBuilder leadings = new StringBuilder();
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			int index = getIndex(c);
						
			if (index != -1) {
				leadings.append(leadingCLo[index]);
			} else {
				leadings.append(c);
			}
		}
			
		return leadings.toString();
	}
	
	public static final String getPinYin(char c) {
		int index = getIndex(c);
		
		return index != -1 ? pinyin[index] : null;
	}
	
	public static final String getPinYinT9(char c) {
		int index = getIndex(c);
		
		return index != -1 ? pinyinT9[index] : null;
	}
	
	public static final String getPinYin(String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		
		StringBuilder pinyins = new StringBuilder();
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			int index = getIndex(c);
						
			if (index != -1) {
				pinyins.append(pinyin[index]);
			} else {
				pinyins.append(c);
			}
		}
			
		return pinyins.toString();
	}
	
	public static final String[] getPinYins(String text) {	
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		
		StringBuilder pinyins = new StringBuilder();
		StringBuilder leadings = new StringBuilder();
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			int index = getIndex(c);
						
			if (index != -1) {
				pinyins.append(pinyin[index]);
				leadings.append(leadingCLo[index]);
			} else {
				pinyins.append(c);
				leadings.append(c);
			}
		}
		
		String[] result =  new String[] {pinyins.toString(), leadings.toString()};
	
		return result;
	}
	
	public static final List<String[]> getPinYins(List<String> texts) {	
		if (texts == null) {
			return null;
		}
		
		List<String[]> results = new ArrayList<String[]>(texts.size());

		for (String text : texts) {
			results.add(getPinYins(text));
		}
		
		return results;
	}
}
