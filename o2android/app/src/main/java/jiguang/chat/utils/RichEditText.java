package jiguang.chat.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jiguang.chat.adapter.TextWatcherAdapter;

/**
 * 一个简单的富文本编辑器
 * 实现了@(AT)和##的Tag匹配功能，
 * 具有Tag删除判断，和光标定位判断；预防用户胡乱篡改
 *
 */
public class RichEditText extends android.support.v7.widget.AppCompatEditText {
    public static final String MATCH_MENTION = "@([^@^\\s^:^,^;^'，'^'；'^>^<]{1,})";//@([^@^\\s^:]{1,})([\\s\\:\\,\\;]{0,1})");//@.+?[\\s:]
    public static final String MATCH_TOPIC = "#.+?#";
    public static boolean DEBUG = false;
    private static final String TAG = RichEditText.class.getName();
    private final TagSpanTextWatcher mTagSpanTextWatcher = new TagSpanTextWatcher();
    private OnKeyArrivedListener mOnKeyArrivedListener;

    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addTextChangedListener(mTagSpanTextWatcher);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ZanyInputConnection(super.onCreateInputConnection(outAttrs), true);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        Spannable spannable = new SpannableString(text);
        spannable = matchMention(spannable);
        spannable = matchTopic(spannable);
        super.setText(spannable, type);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        log("onSelectionChanged:" + selStart + " " + selEnd);
        Editable message = getText();

        if (selStart == selEnd) {
            TagSpan[] list = message.getSpans(selStart - 1, selStart, TagSpan.class);
            if (list.length > 0) {
                // Get first tag
                TagSpan span = list[0];
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                log("onSelectionChanged#Yes:" + spanStart + " " + spanEnd);
                // Check index
                if (Math.abs(selStart - spanStart) > Math.abs(selStart - spanEnd)) {
                    Selection.setSelection(message, spanEnd);
                    replaceCacheTagSpan(message, span, false);
                    return;
                } else {
                    Selection.setSelection(message, spanStart);
                }
            }
        } else {
            TagSpan[] list = message.getSpans(selStart, selEnd, TagSpan.class);
            if (list.length == 0)
                return;
            int start = selStart;
            int end = selEnd;
            for (TagSpan span : list) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);

                if (spanStart < start)
                    start = spanStart;

                if (spanEnd > end)
                    end = spanEnd;
            }
            if (start != selStart || end != selEnd) {
                Selection.setSelection(message, start, end);
                log("onSelectionChanged#No:" + start + " " + end);
            }
        }

        replaceCacheTagSpan(message, null, false);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        // Handle the paste option
        if (id == android.R.id.paste) {
            // Handle to the clipboard service.
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // Handle the data.
            if (clipboard.hasPrimaryClip()) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if (item != null) {
                    // Gets the clipboard date to string and do trim
                    String paste = item.coerceToText(getContext()).toString().trim();
                    // Check need space
                    if (mTagSpanTextWatcher != null && mTagSpanTextWatcher.checkCommit(paste))
                        paste = " " + paste;
                    // Clear add span
                    Spannable spannablePaste = new SpannableString(paste);
                    spannablePaste = matchMention(spannablePaste);
                    spannablePaste = matchTopic(spannablePaste);
                    getText().replace(getSelectionStart(), getSelectionEnd(), spannablePaste);
                    return true;
                }
            }
        }
        // Call super
        return super.onTextContextMenuItem(id);
    }

    public void setOnKeyArrivedListener(OnKeyArrivedListener listener) {
        mOnKeyArrivedListener = listener;
    }

    protected boolean callToMention() {
        OnKeyArrivedListener listener = mOnKeyArrivedListener;
        return listener == null || listener.onMentionKeyArrived(this);
    }

    protected boolean callToTopic() {
        OnKeyArrivedListener listener = mOnKeyArrivedListener;
        return listener == null || listener.onTopicKeyArrived(this);
    }

    private void replaceCacheTagSpan(Editable message, TagSpan span, boolean targetDelState) {
        if (mTagSpanTextWatcher != null) {
            mTagSpanTextWatcher.replaceSpan(message, span, targetDelState);
        }
    }

    private String filterDirty(String str) {
        return str.replace("#", "").replace("@", "").replace(" ", "");
    }

    private void replaceLastChar(@NonNull String chr, SpannableString spannable) {
        Editable msg = getText();
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();

        int selStartBefore = selStart - 1;
        if (selStart == selEnd && selStart > 0
                && chr.equals(msg.subSequence(selStartBefore, selEnd).toString())
                && msg.getSpans(selStartBefore, selEnd, RichEditText.TagSpan.class).length == 0) {
            selStart = selStartBefore;
        }

        msg.replace(selStart >= 0 ? selStart : 0, selEnd >= 0 ? selEnd : 0, spannable);
    }

    /**
     * 添加提到字符串
     *
     * @param mentions 提及的人，不含@
     */
    @SuppressWarnings("unused")
    public void appendMention(String... mentions) {
        if (mentions == null || mentions.length == 0)
            return;

        String mentionStr = "";

        for (String mention : mentions) {
            if (mention == null || TextUtils.isEmpty(mention = mention.trim())
                    || TextUtils.isEmpty(mention = filterDirty(mention)))
                continue;
            mentionStr += String.format("@%s ", mention);
        }
        if (TextUtils.isEmpty(mentionStr))
            return;

        SpannableString spannable = new SpannableString(mentionStr);
        RichEditText.matchMention(spannable);

        replaceLastChar("@", spannable);
    }

    /**
     * 添加话题字符串
     *
     * @param topics 话题，不含#
     */
    @SuppressWarnings("unused")
    public void appendTopic(String... topics) {
        if (topics == null || topics.length == 0)
            return;

        String topicStr = "";

        for (String topic : topics) {
            if (topic == null || TextUtils.isEmpty(topic = topic.trim())
                    || TextUtils.isEmpty(topic = filterDirty(topic)))
                continue;
            topicStr += String.format("#%s# ", topic);
        }
        if (TextUtils.isEmpty(topicStr))
            return;

        SpannableString spannable = new SpannableString(topicStr);
        RichEditText.matchTopic(spannable);

        replaceLastChar("#", spannable);
    }

    private class ZanyInputConnection extends InputConnectionWrapper {

        ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (!RichEditText.this.mTagSpanTextWatcher.checkKeyDel())
                    return false;
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            return checkCommitWithCacheTagSpan(text) && super.commitText(text, newCursorPosition);
        }

        @Override
        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            return checkCommitWithCacheTagSpan(text) && super.setComposingText(text, newCursorPosition);
        }

        private boolean checkCommitWithCacheTagSpan(CharSequence text) {
            if ("@".equals(text)) {
                return callToMention();
            } else if ("#".equals(text)) {
                return callToTopic();
            } else {
                boolean needSpace = mTagSpanTextWatcher.checkCommit(text);
                if (needSpace) {
                    // Send a " " string to edit
                    super.commitText(" ", 1);
                }
                return true;
            }
        }
    }

    private class TagSpanTextWatcher extends TextWatcherAdapter {
        private TagSpan willDelSpan;

        void replaceSpan(Editable message, TagSpan span, boolean targetDelState) {
            if (span != null)
                span.changeRemoveState(targetDelState, message);

            if (willDelSpan != span) {
                // When different
                TagSpan cacheSpan = willDelSpan;
                if (cacheSpan != null) {
                    cacheSpan.changeRemoveState(false, message);
                }
                willDelSpan = span;
            }
        }

        boolean checkKeyDel() {
            int selStart = getSelectionStart();
            int selEnd = getSelectionEnd();
            Editable message = getText();
            log("TagSpanTextWatcher#checkKeyDel:" + selStart + " " + selEnd);
            if (selStart == selEnd) {
                int start = selStart - 1;
                int count = 1;

                start = start < 0 ? 0 : start;

                int end = start + count;
                TagSpan[] list = message.getSpans(start, end, TagSpan.class);

                if (list.length > 0) {
                    // Only get first
                    final TagSpan span = list[0];
                    final TagSpan cacheSpan = willDelSpan;

                    if (span == cacheSpan) {
                        if (span.willRemove)
                            return true;
                        else {
                            span.changeRemoveState(true, message);
                            return false;
                        }
                    }
                }
            }
            // Replace cache tag to null
            replaceSpan(message, null, false);
            return true;
        }

        boolean checkCommit(CharSequence s) {
            if (willDelSpan != null) {
                willDelSpan.willRemove = false;
                willDelSpan = null;
                return s != null && s.length() > 0 && !" ".equals(s.subSequence(0, 1));
            }
            return false;
        }

        @Override
        public void afterTextChanged(Editable s) {
            final TagSpan span = willDelSpan;
            log("TagSpanTextWatcher#willRemove#span:" + (span == null ? "null" : span.toString()));
            if (span != null && span.willRemove) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);

                // Remove the span
                s.removeSpan(span);

                // Remove the remaining emoticon text.
                if (start != end) {
                    s.delete(start, end);
                }
            }
        }
    }

    public interface OnKeyArrivedListener {
        boolean onMentionKeyArrived(RichEditText editText);

        boolean onTopicKeyArrived(RichEditText text);
    }

    public static Spannable matchMention(Spannable spannable) {
        String text = spannable.toString();

        Pattern pattern = Pattern.compile(MATCH_MENTION);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            spannable.setSpan(new TagSpan(str), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            log("matchMention:" + str + " " + matcherStart + " " + matcherEnd);
        }
        return spannable;
    }

    public static Spannable matchTopic(Spannable spannable) {
        String text = spannable.toString();

        Pattern pattern = Pattern.compile(MATCH_TOPIC);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            spannable.setSpan(new TagSpan(str), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            log("matchTopic:" + str + " " + matcherStart + " " + matcherEnd);
        }

        return spannable;
    }

    private static void log(String msg) {
        if (DEBUG)
            Log.e(TAG, msg);
    }

    @SuppressWarnings("WeakerAccess")
    public static class TagSpan extends ForegroundColorSpan implements Parcelable {
        private String value;
        public boolean willRemove;

        public TagSpan(String value) {
            super(0xFF24cf5f);
            this.value = value;
        }

        public TagSpan(Parcel src) {
            super(src);
            value = src.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TagSpan> CREATOR = new Creator<TagSpan>() {
            @Override
            public TagSpan createFromParcel(Parcel in) {
                return new TagSpan(in);
            }

            @Override
            public TagSpan[] newArray(int size) {
                return new TagSpan[size];
            }
        };

        @Override
        public void updateDrawState(TextPaint ds) {
            //log("TagSpan:updateDrawState:" + isPreDeleteState);
//            ds.setFakeBoldText(true);//设置@后的名字加粗和文字颜色
            if (willRemove) {
                ds.setColor(0xFFFFFFFF);
                ds.bgColor = 0xFF24cf5f;
            } else {
                super.updateDrawState(ds);
            }
        }

        void changeRemoveState(boolean willRemove, Editable message) {
            if (this.willRemove == willRemove)
                return;
            this.willRemove = willRemove;
            int cacheSpanStart = message.getSpanStart(this);
            int cacheSpanEnd = message.getSpanEnd(this);
            if (cacheSpanStart >= 0 && cacheSpanEnd >= cacheSpanStart) {
                message.setSpan(this, cacheSpanStart, cacheSpanEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        public String getValue() {
            return value;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(value);
        }

        @Override
        public String toString() {
            return "TagSpan{" +
                    "value='" + value + '\'' +
                    ", willRemove=" + willRemove +
                    '}';
        }
    }


}
