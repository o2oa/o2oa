package jiguang.chat.utils.pinyin;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class HanyuPinyin {

    private static HanyuPinyin mInstance = new HanyuPinyin();
    private HanyuPinyinOutputFormat format = null;
    private String[] pinyin;

    public HanyuPinyin() {
        format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        pinyin = null;
    }

    public static HanyuPinyin getInstance() {
        return mInstance;
    }

    //转换拼音首字母
    public String getCharacterPinYin(char c) {
        String str ;
        try {
            str = getHanyuPinYin(c);
        }
        catch(Exception e) {
            e.printStackTrace();
            str = null;
        }

        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if(str == null) return null;

        // 只取一个发音，如果是多音字，仅取第一个发音
        return str.substring(0, 1);
    }
    //中文转拼音
    public String getHanyuPinYin(char c) {
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
        }
        catch(BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if(pinyin == null) return null;

         //todo 多音字 取第一个
        return pinyin[0];
    }

    //转换一个字符串
    public String getStringPinYin(String str) {
        StringBuilder sb = new StringBuilder();
        String tempPinyin = null;
        for(int i = 0; i < str.length(); ++i)
        {
            tempPinyin =getCharacterPinYin(str.charAt(i));
            if(tempPinyin == null)
            {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            }
            else
            {
                sb.append(tempPinyin);
            }
        }
        return sb.toString();
    }

    public String getStringQuanPin(String str) {
        StringBuilder sb = new StringBuilder();
        String tempPinyin = null;
        for(int i = 0; i < str.length(); ++i)
        {
            tempPinyin =getHanyuPinYin(str.charAt(i));
            if(tempPinyin == null)
            {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            }
            else
            {
                sb.append(tempPinyin);
            }
        }
        return sb.toString();
    }
}
