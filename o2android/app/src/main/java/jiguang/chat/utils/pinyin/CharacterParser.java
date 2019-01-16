package jiguang.chat.utils.pinyin;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * Java汉字转换为拼音
 */
public class CharacterParser {


    private static int[] pyvalue = new int[] {-20319, -20317, -20304, -20295,
            -20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036,
            -20032, -20026, -20002, -19990, -19986, -19982, -19976, -19805,
            -19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741,
            -19739, -19728, -19725, -19715, -19540, -19531, -19525, -19515,
            -19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275,
            -19270, -19263, -19261, -19249, -19243, -19242, -19238, -19235,
            -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006,
            -19003, -18996, -18977, -18961, -18952, -18783, -18774, -18773,
            -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697,
            -18696, -18526, -18518, -18501, -18490, -18478, -18463, -18448,
            -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201,
            -18184, -18183, -18181, -18012, -17997, -17988, -17970, -17964,
            -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752,
            -17733, -17730, -17721, -17703, -17701, -17697, -17692, -17683,
            -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427,
            -17417, -17202, -17185, -16983, -16970, -16942, -16915, -16733,
            -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470,
            -16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423,
            -16419, -16412, -16407, -16403, -16401, -16393, -16220, -16216,
            -16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158,
            -16155, -15959, -15958, -15944, -15933, -15920, -15915, -15903,
            -15889, -15878, -15707, -15701, -15681, -15667, -15661, -15659,
            -15652, -15640, -15631, -15625, -15454, -15448, -15436, -15435,
            -15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369,
            -15363, -15362, -15183, -15180, -15165, -15158, -15153, -15150,
            -15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121,
            -15119, -15117, -15110, -15109, -14941, -14937, -14933, -14930,
            -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902,
            -14894, -14889, -14882, -14873, -14871, -14857, -14678, -14674,
            -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429,
            -14407, -14399, -14384, -14379, -14368, -14355, -14353, -14345,
            -14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135,
            -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094,
            -14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907,
            -13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847,
            -13831, -13658, -13611, -13601, -13406, -13404, -13400, -13398,
            -13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343,
            -13340, -13329, -13326, -13318, -13147, -13138, -13120, -13107,
            -13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888,
            -12875, -12871, -12860, -12858, -12852, -12849, -12838, -12831,
            -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556,
            -12359, -12346, -12320, -12300, -12120, -12099, -12089, -12074,
            -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798,
            -11781, -11604, -11589, -11536, -11358, -11340, -11339, -11324,
            -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041,
            -11038, -11024, -11020, -11019, -11018, -11014, -10838, -10832,
            -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533,
            -10519, -10331, -10329, -10328, -10322, -10315, -10309, -10307,
            -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254
    };
    public static String[] pystr = new String[] {"a", "ai", "an", "ang", "ao",
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
            "zong", "zou", "zu", "zuan", "zui", "zun", "zuo"
    };
    private StringBuffer buffer;
    private String resource;
    private static CharacterParser singleInstance;

    public static CharacterParser getInstance() {
        if (singleInstance == null) {
            synchronized (CharacterParser.class) {
                if (singleInstance == null) {
                    singleInstance = new CharacterParser();
                }
            }
        }
        return singleInstance;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * 汉字转成ASCII码 * * @param chs * @return
     */
    private int getChsAscii(String chs) {
        int asc = 0;
        try {
            byte[] bytes = chs.getBytes("gb2312");
            if (bytes == null || bytes.length > 2 || bytes.length <= 0) {
                throw new RuntimeException("illegal resource string");
            }
            if (bytes.length == 1) {
                asc = bytes[0];
            }
            if (bytes.length == 2) {
                int hightByte = 256 + bytes[0];
                int lowByte = 256 + bytes[1];
                asc = (256 * hightByte + lowByte) - 256 * 256;
            }
        } catch (Exception e) {
            System.out
                    .println("ERROR:ChineseSpelling.class-getChsAscii(String chs)"
                            + e);
        }
        return asc;
    }

    /**
     * 单字解析 * * @param str * @return
     */
    public String convert(String str) {
        String result = null;
        int ascii = getChsAscii(str);
        if (ascii > 0 && ascii < 160) {
            result = String.valueOf((char) ascii);
        } else {
            for (int i = (pyvalue.length - 1); i >= 0; i--) {
                if (pyvalue[i] <= ascii) {
                    result = pystr[i];
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 词组解析 * * @param chs * @return
     */
    public String getSpelling(String chs) {
        if (chs == null) {
            return null;
        }
        String key, value;
        buffer = new StringBuffer();
        for (int i = 0; i < chs.length(); i++) {
            key = chs.substring(i, i + 1);
            if (key.getBytes().length >= 2) {
                value = convert(key);
                if (value == null) {
                    value = "unknown";
                }
            } else {
                value = key;
            }
            buffer.append(value);
        }
        return buffer.toString();
    }


    public SpannableStringBuilder getColoredDisplayName(String filterStr, String displayName) {
        return getColored(filterStr, displayName);
    }


    public SpannableStringBuilder getColoredName(String filterStr, String name) {
        return getColored(filterStr, name);
    }

    public SpannableStringBuilder getColored(String filterStr, String name) {
        try {
            String lowerCaseFilterStr = filterStr.toLowerCase();
            String lowerCaseName = name.toLowerCase();
            String lowerCaseNameSpelling = getSpelling(name).toLowerCase();
            if (lowerCaseName.contains(lowerCaseFilterStr)) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(name);
                int index = lowerCaseName.indexOf(lowerCaseFilterStr);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), index, index + filterStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                return spannableStringBuilder;
            } else if (lowerCaseNameSpelling.startsWith(lowerCaseFilterStr)) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(name);
                int nameLength = name.length();
                int showCount = 1;
                for (int i = 0; i < nameLength; i++) {
                    String subName = name.substring(0, i + 1);
                    if (filterStr.length() > getSpelling(subName).length()) {
                        showCount++;
                        continue;
                    } else {
                        break;
                    }
                }
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), 0, showCount, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                return spannableStringBuilder;
            } else {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(name);
                return spannableStringBuilder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableStringBuilder(name);
    }

    public SpannableStringBuilder getColoredGroupName(String filterStr, String groupName) {
        return getColored(filterStr, groupName);
    }

//    public SpannableStringBuilder getColoredNameList(String filterStr, List<GroupMember> filterGroupMemberList) {
//        SpannableStringBuilder nameList = new SpannableStringBuilder();
//        for (GroupMember groupMember : filterGroupMemberList) {
//            if (!TextUtils.isEmpty(groupMember.getDisplayName())) {
//                SpannableStringBuilder spannableStringBuilder = getColored(filterStr, groupMember.getDisplayName());
//                nameList.append(spannableStringBuilder)
//                        .append(",");
//            } else {
//                SpannableStringBuilder spannableStringBuilder = getColored(filterStr, groupMember.getName());
//                nameList.append(spannableStringBuilder)
//                        .append(",");
//            }
//        }
//        SpannableStringBuilder nameListDisplay;
//        int length = nameList.length();
//        if (length > 1) {
//            nameListDisplay = nameList.delete(length - 1, length);
//        } else {
//            nameListDisplay = SpannableStringBuilder.valueOf("");
//        }
//        return nameListDisplay;
//    }

//    public SpannableStringBuilder getColoredChattingRecord(String filterStr, MessageContent messageContent) {
//        SpannableStringBuilder messageText = new SpannableStringBuilder();
//        if (messageContent instanceof TextMessage) {
//            TextMessage textMessage = (TextMessage) messageContent;
//            String textMessageContent = textMessage.getContent();
//            messageText = getOmitColored(filterStr, textMessageContent, 0);
//        }
//        if (messageContent instanceof RichContentMessage) {
//            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
//            String messageTitle = richContentMessage.getTitle();
//            messageText = getOmitColored(filterStr, messageTitle, 1);
//            if (messageText.length() == 0) {
//                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("[链接] ");
//                spannableStringBuilder.append(messageTitle);
//                messageText = spannableStringBuilder;
//            }
//        }
//        if (messageContent instanceof FileMessage) {
//            FileMessage fileMessage = (FileMessage) messageContent;
//            String fileName = fileMessage.getName();
//            messageText = getOmitColored(filterStr, fileName, 2);
//        }
//        return messageText;
//    }

    private SpannableStringBuilder getOmitColored(String filterStr, String content, int type) {
        SpannableStringBuilder messageText = new SpannableStringBuilder();
        String lowerCaseFilterStr = filterStr.toLowerCase();
        String lowerCaseText = content.toLowerCase();
        if (lowerCaseText.contains(lowerCaseFilterStr)) {
            SpannableStringBuilder finalBuilder = new SpannableStringBuilder();
            if (type == 0) {
            } else if (type == 1) {
                finalBuilder.append("[链接] ");
            } else if (type == 2) {
                finalBuilder.append("[文件] ");
            }
            int length = content.length();
            int firstIndex = lowerCaseText.indexOf(lowerCaseFilterStr);
            String subString = content.substring(firstIndex);
            int restLength;
            if (subString != null) {
                restLength = subString.length();
            } else {
                restLength = 0;
            }
            if (length <= 12) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), firstIndex, firstIndex + filterStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                return finalBuilder.append(spannableStringBuilder);

            } else {
                //首次出现搜索字符的index加上filter的length；
                int totalLength = firstIndex + filterStr.length();
                if (totalLength < 12) {
                    String smallerString = content.substring(0, 12);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(smallerString);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), firstIndex, firstIndex + filterStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append("...");
                    return finalBuilder.append(spannableStringBuilder);
                } else if (restLength < 12) {
                    String smallerString = content.substring(length - 12, length);
                    String smallerStringLowerCase = lowerCaseText.substring(length - 12, length);
                    int index = smallerStringLowerCase.indexOf(lowerCaseFilterStr);
                    SpannableStringBuilder builder = new SpannableStringBuilder("...");
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(smallerString);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), index, index + filterStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.append(spannableStringBuilder);
                    return finalBuilder.append(builder);
                } else {
                    String smallerString = content.substring(firstIndex - 5, firstIndex + 7);
                    String smallerStringLowerCase = lowerCaseText.substring(firstIndex - 5, firstIndex + 7);
                    int index = smallerStringLowerCase.indexOf(lowerCaseFilterStr);
                    SpannableStringBuilder builder = new SpannableStringBuilder("...");
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(smallerString);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#0099ff")), index, getSmallerLength(smallerString.length(), index + filterStr.length()), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.append(spannableStringBuilder);
                    builder.append("...");
                    return finalBuilder.append(builder);
                }
            }
        }
        return messageText;
    }


//    public Friend generateFriendFromUserInfo(UserInfo userInfo) {
//        Friend friend = new Friend();
//        if (userInfo != null) {
//            friend.setUserId(userInfo.getUserId());
//            friend.setName(userInfo.getName());
//            Uri uri = userInfo.getPortraitUri();
//            friend.setPortraitUri(uri != null ?
//                    uri.toString() : null);
//        }
//        return friend;
//    }



    private int getSmallerLength(int stringLength, int endIndex) {
        return stringLength > endIndex + 1 ? endIndex : stringLength;
    }
}
