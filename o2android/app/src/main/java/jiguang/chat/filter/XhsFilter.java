package jiguang.chat.filter;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jiguang.chat.utils.keyboard.interfaces.EmoticonFilter;


public class XhsFilter extends EmoticonFilter {

    public static final int WRAP_DRAWABLE = -1;
    private int emoticonSize = -1;
    public static final Pattern XHS_RANGE = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]");

    public static Matcher getMatcher(CharSequence matchStr) {
        return XHS_RANGE.matcher(matchStr);
    }

    @Override
    public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
//        emoticonSize = emoticonSize == -1 ? EmoticonsKeyboardUtils.getFontHeight(editText) : emoticonSize;
//        clearSpan(editText.getText(), start, text.toString().length());
//        Matcher m = getMatcher(text.toString().substring(start, text.toString().length()));
//        if (m != null) {
//            while (m.find()) {
//                String key = m.group();
//                String icon = DefXhsEmoticons.sXhsEmoticonHashMap.get(key);
//                if (!TextUtils.isEmpty(icon)) {
//                    emoticonDisplay(editText.getContext(), editText.getText(), icon, emoticonSize, start + m.start(), start + m.end());
//                }
//            }
//        }
    }

//    public static Spannable spannableFilter(Context context, Spannable spannable, CharSequence text, int fontSize, EmojiDisplayListener emojiDisplayListener) {
//        Matcher m = getMatcher(text);
//        if (m != null) {
//            while (m.find()) {
//                String key = m.group();
//                String icon = DefXhsEmoticons.sXhsEmoticonHashMap.get(key);
//                if (emojiDisplayListener == null) {
//                    if (!TextUtils.isEmpty(icon)) {
//                        emoticonDisplay(context, spannable, icon, fontSize, m.start(), m.end());
//                    }
//                } else {
//                    emojiDisplayListener.onEmojiDisplay(context, spannable, icon, fontSize, m.start(), m.end());
//                }
//            }
//        }
//        return spannable;
//    }
//
//    private void clearSpan(Spannable spannable, int start, int end) {
//        if (start == end) {
//            return;
//        }
//        EmoticonSpan[] oldSpans = spannable.getSpans(start, end, EmoticonSpan.class);
//        for (int i = 0; i < oldSpans.length; i++) {
//            spannable.removeSpan(oldSpans[i]);
//        }
//    }
//
//    public static void emoticonDisplay(Context context, Spannable spannable, String emoticonName, int fontSize, int start, int end) {
//        Drawable drawable = getDrawableFromAssets(context, emoticonName);
//        if (drawable != null) {
//            int itemHeight;
//            int itemWidth;
//            if (fontSize == WRAP_DRAWABLE) {
//                itemHeight = drawable.getIntrinsicHeight();
//                itemWidth = drawable.getIntrinsicWidth();
//            } else {
//                itemHeight = fontSize;
//                itemWidth = fontSize;
//            }
//
//            drawable.setBounds(0, 0, itemHeight, itemWidth);
//            EmojiSpan imageSpan = new EmojiSpan(drawable);
//            spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        }
//    }
}
