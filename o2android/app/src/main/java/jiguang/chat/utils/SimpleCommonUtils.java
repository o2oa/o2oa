package jiguang.chat.utils;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.IOException;
import java.lang.reflect.Constructor;

import jiguang.chat.adapter.BigEmoticonsAdapter;
import jiguang.chat.adapter.BigEmoticonsAndTitleAdapter;
import jiguang.chat.adapter.TextEmoticonsAdapter;
import jiguang.chat.filter.XhsFilter;
import jiguang.chat.model.Constants;
import jiguang.chat.utils.keyboard.adpater.EmoticonsAdapter;
import jiguang.chat.utils.keyboard.adpater.PageSetAdapter;
import jiguang.chat.utils.keyboard.data.EmoticonEntity;
import jiguang.chat.utils.keyboard.data.EmoticonPageEntity;
import jiguang.chat.utils.keyboard.data.EmoticonPageSetEntity;
import jiguang.chat.utils.keyboard.data.PageEntity;
import jiguang.chat.utils.keyboard.data.PageSetEntity;
import jiguang.chat.utils.keyboard.interfaces.EmoticonClickListener;
import jiguang.chat.utils.keyboard.interfaces.EmoticonDisplayListener;
import jiguang.chat.utils.keyboard.interfaces.PageViewInstantiateListener;
import jiguang.chat.utils.keyboard.utils.EmoticonsKeyboardUtils;
import jiguang.chat.utils.keyboard.utils.imageloader.ImageBase;
import jiguang.chat.utils.keyboard.utils.imageloader.ImageLoader;
import jiguang.chat.utils.keyboard.widget.EmoticonPageView;
import jiguang.chat.utils.keyboard.widget.EmoticonsEditText;
import jiguang.chat.view.SimpleAppsGridView;


public class SimpleCommonUtils {

    public static void initEmoticonsEditText(EmoticonsEditText etContent) {
//        etContent.addEmoticonFilter(new EmojiFilter());
//        etContent.addEmoticonFilter(new XhsFilter());
    }

    public static EmoticonClickListener getCommonEmoticonClickListener(final EditText editText) {
        return new EmoticonClickListener() {
            @Override
            public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
                if (isDelBtn) {
                    SimpleCommonUtils.delClick(editText);
                } else {
                    if (o == null) {
                        return;
                    }
                    if (actionType == Constants.EMOTICON_CLICK_TEXT) {
                        String content = null;
//                        if (o instanceof EmojiBean) {
//                            content = ((EmojiBean) o).emoji;
//                        } else
                            if (o instanceof EmoticonEntity) {
                            content = ((EmoticonEntity) o).getContent();
                        }

                        if (TextUtils.isEmpty(content)) {
                            return;
                        }
                        int index = editText.getSelectionStart();
                        Editable editable = editText.getText();
                        editable.insert(index, content);
                    }
                }
            }
        };
    }

    public static PageSetAdapter sCommonPageSetAdapter;

    public static PageSetAdapter getCommonAdapter(Context context, EmoticonClickListener emoticonClickListener) {

        if(sCommonPageSetAdapter != null){
            return sCommonPageSetAdapter;
        }

        PageSetAdapter pageSetAdapter = new PageSetAdapter();

        addEmojiPageSetEntity(pageSetAdapter, context, emoticonClickListener);

//        addXhsPageSetEntity(pageSetAdapter, context, emoticonClickListener);

//        addWechatPageSetEntity(pageSetAdapter, context, emoticonClickListener);

        addGoodGoodStudyPageSetEntity(pageSetAdapter, context, emoticonClickListener);

        addKaomojiPageSetEntity(pageSetAdapter, context, emoticonClickListener);

//        addTestPageSetEntity(pageSetAdapter, context); //控制能否从表情滑动到更多功能

        return pageSetAdapter;
    }

    /**
     * 插入emoji表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addEmojiPageSetEntity(PageSetAdapter pageSetAdapter, Context context, final EmoticonClickListener emoticonClickListener) {
//        ArrayList<EmojiBean> emojiArray = new ArrayList<>();
//        Collections.addAll(emojiArray, DefEmoticons.sEmojiArray);
//        EmoticonPageSetEntity emojiPageSetEntity
//                = new EmoticonPageSetEntity.Builder()
//                .setLine(3)
//                .setRow(7)
//                .setEmoticonList(emojiArray)
//                .setIPageViewInstantiateItem(getDefaultEmoticonPageViewInstantiateItem(new EmoticonDisplayListener<Object>() {
//                    @Override
//                    public void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {
//                        final EmojiBean emojiBean = (EmojiBean) object;
//                        if (emojiBean == null && !isDelBtn) {
//                            return;
//                        }
//
//                        viewHolder.ly_root.setBackgroundResource(R.drawable.bg_emoticon);
//
//                        if (isDelBtn) {
//                            viewHolder.iv_emoticon.setImageResource(R.mipmap.icon_del);
//                        } else {
//                            viewHolder.iv_emoticon.setImageResource(emojiBean.icon);
//                        }
//
//                        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (emoticonClickListener != null) {
//                                    emoticonClickListener.onEmoticonClick(emojiBean, Constants.EMOTICON_CLICK_TEXT, isDelBtn);
//                                }
//                            }
//                        });
//                    }
//                }))
//                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
//                .setIconUri(ImageBase.Scheme.DRAWABLE.toUri("icon_emoji"))
//                .build();
//        pageSetAdapter.add(emojiPageSetEntity);
    }


    /**
     * 插入JG表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addXhsPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        EmoticonPageSetEntity xhsPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(7)
                .setEmoticonList(ParseDataUtils.ParseXhsData(DefXhsEmoticons.xhsEmoticonArray, ImageBase.Scheme.ASSETS))
                .setIPageViewInstantiateItem(getDefaultEmoticonPageViewInstantiateItem(getCommonEmoticonDisplayListener(emoticonClickListener, Constants.EMOTICON_CLICK_TEXT)))
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .setIconUri(ImageBase.Scheme.ASSETS.toUri("j_qinqin.png"))
                .build();
        pageSetAdapter.add(xhsPageSetEntity);
    }

    /**
     * 插入微信表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addWechatPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        String filePath = FileUtils.getFolderPath("wxemoticons");
        EmoticonPageSetEntity<EmoticonEntity> emoticonPageSetEntity = ParseDataUtils.parseDataFromFile(context, filePath, "wxemoticons.zip", "wxemoticons.xml");
        if (emoticonPageSetEntity == null) {
            return;
        }
        EmoticonPageSetEntity pageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(emoticonPageSetEntity.getLine())
                .setRow(emoticonPageSetEntity.getRow())
                .setEmoticonList(emoticonPageSetEntity.getEmoticonList())
                .setIPageViewInstantiateItem(getEmoticonPageViewInstantiateItem(BigEmoticonsAdapter.class, emoticonClickListener))
                .setIconUri(ImageBase.Scheme.FILE.toUri(filePath + "/" + emoticonPageSetEntity.getIconUri()))
                .build();
        pageSetAdapter.add(pageSetEntity);
    }

    /**
     * 插入JG熊图片集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addGoodGoodStudyPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        String filePath = FileUtils.getFolderPath("goodgoodstudy");
        EmoticonPageSetEntity<EmoticonEntity> emoticonPageSetEntity = ParseDataUtils.parseDataFromFile(context, filePath, "goodgoodstudy.zip", "goodgoodstudy.xml");
        if (emoticonPageSetEntity == null) {
            return;
        }
        EmoticonPageSetEntity pageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(emoticonPageSetEntity.getLine())
                .setRow(emoticonPageSetEntity.getRow())
                .setEmoticonList(emoticonPageSetEntity.getEmoticonList())
                .setIPageViewInstantiateItem(getEmoticonPageViewInstantiateItem(BigEmoticonsAndTitleAdapter.class, emoticonClickListener))
                .setIconUri(ImageBase.Scheme.FILE.toUri(filePath + "/" + emoticonPageSetEntity.getIconUri()))
                .build();
        pageSetAdapter.add(pageSetEntity);
    }


    /**
     * 插入颜文字表情集
     *
     * @param pageSetAdapter
     * @param context
     * @param emoticonClickListener
     */
    public static void addKaomojiPageSetEntity(PageSetAdapter pageSetAdapter, Context context, EmoticonClickListener emoticonClickListener) {
        EmoticonPageSetEntity kaomojiPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(3)
                .setEmoticonList(ParseDataUtils.parseKaomojiData(context))
                .setIPageViewInstantiateItem(getEmoticonPageViewInstantiateItem(TextEmoticonsAdapter.class, emoticonClickListener))
                .setIconUri(ImageBase.Scheme.DRAWABLE.toUri("icon_kaomoji"))
                .build();
        pageSetAdapter.add(kaomojiPageSetEntity);
    }

    /**
     * 测试页集
     *
     * @param pageSetAdapter
     * @param context
     */
    public static void addTestPageSetEntity(PageSetAdapter pageSetAdapter, Context context) {
        PageSetEntity pageSetEntity = new PageSetEntity.Builder()
                .addPageEntity(new PageEntity(new SimpleAppsGridView(context)))
                .setIconUri(ImageBase.Scheme.DRAWABLE.toUri("icon_kaomoji"))
                .setShowIndicator(false)
                .build();
        pageSetAdapter.add(pageSetEntity);
    }

    @SuppressWarnings("unchecked")
    public static Object newInstance(Class _Class, Object... args) throws Exception {
        return newInstance(_Class, 0, args);
    }

    @SuppressWarnings("unchecked")
    public static Object newInstance(Class _Class, int constructorIndex, Object... args) throws Exception {
        Constructor cons = _Class.getConstructors()[constructorIndex];
        return cons.newInstance(args);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getDefaultEmoticonPageViewInstantiateItem(final EmoticonDisplayListener<Object> emoticonDisplayListener) {
        return getEmoticonPageViewInstantiateItem(EmoticonsAdapter.class, null, emoticonDisplayListener);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getEmoticonPageViewInstantiateItem(final Class _class, EmoticonClickListener onEmoticonClickListener) {
        return getEmoticonPageViewInstantiateItem(_class, onEmoticonClickListener, null);
    }

    public static PageViewInstantiateListener<EmoticonPageEntity> getEmoticonPageViewInstantiateItem(final Class _class, final EmoticonClickListener onEmoticonClickListener, final EmoticonDisplayListener<Object> emoticonDisplayListener) {
        return new PageViewInstantiateListener<EmoticonPageEntity>() {
            @Override
            public View instantiateItem(ViewGroup container, int position, EmoticonPageEntity pageEntity) {
                if (pageEntity.getRootView() == null) {
                    EmoticonPageView pageView = new EmoticonPageView(container.getContext());
                    pageView.setNumColumns(pageEntity.getRow());
                    pageEntity.setRootView(pageView);
                    try {
                        EmoticonsAdapter adapter = (EmoticonsAdapter) newInstance(_class, container.getContext(), pageEntity, onEmoticonClickListener);
                        if (emoticonDisplayListener != null) {
                            adapter.setOnDisPlayListener(emoticonDisplayListener);
                        }
                        pageView.getEmoticonsGridView().setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return pageEntity.getRootView();
            }
        };
    }

    public static EmoticonDisplayListener<Object> getCommonEmoticonDisplayListener(final EmoticonClickListener onEmoticonClickListener, final int type) {
        return new EmoticonDisplayListener<Object>() {
            @Override
            public void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {

                final EmoticonEntity emoticonEntity = (EmoticonEntity) object;
                if (emoticonEntity == null && !isDelBtn) {
                    return;
                }
                viewHolder.ly_root.setBackgroundResource(R.drawable.bg_emoticon);

                if (isDelBtn) {
                    viewHolder.iv_emoticon.setImageResource(R.mipmap.icon_del);
                } else {
                    try {
                        ImageLoader.getInstance(viewHolder.iv_emoticon.getContext()).displayImage(emoticonEntity.getIconUri(), viewHolder.iv_emoticon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onEmoticonClickListener != null) {
                            onEmoticonClickListener.onEmoticonClick(emoticonEntity, type, isDelBtn);
                        }
                    }
                });
            }
        };
    }

    public static void delClick(EditText editText) {
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        editText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    public static void spannableEmoticonFilter(TextView tv_content, String content) {
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);

//            Spannable spannable = EmojiDisplay.spannableFilter(tv_content.getContext(),
//                    spannableStringBuilder,
//                    content,
//                    EmoticonsKeyboardUtils.getFontHeight(tv_content));

//            Spannable spannable = XhsFilter.spannableFilter(tv_content.getContext(),
//                    spannableStringBuilder,
//                    content,
//                    EmoticonsKeyboardUtils.getFontHeight(tv_content),
//                    null);
            tv_content.setText(content);
        } catch (Exception e) {
            Log.e("SimpleCommonUtils","EmojiDisplay error!!!!!!!!!!!!");
        }
    }
}
