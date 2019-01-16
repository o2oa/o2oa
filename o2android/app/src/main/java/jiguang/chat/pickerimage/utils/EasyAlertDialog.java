package jiguang.chat.pickerimage.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * 普通提示包含两个按钮以及Title和Message(居中显示).
 * <p/>
 * 警告提示使用包含一个按钮以及Title和Message(居中显示).
 * <p/>
 * 错误提示使用包含一个按钮(红色背景)以及Title和Message(居中显示).
 * <p/>
 * 特殊布局需求可以自定义布局.
 */
public class EasyAlertDialog extends Dialog {
	private Context context;

    public static final int NO_TEXT_COLOR = -99999999;

    public static final int NO_TEXT_SIZE = -99999999;

    private View titleView;

    private ImageButton titleBtn;

    private TextView titleTV;

    private TextView messageTV;

    private TextView message2TV;

    private Button positiveBtn, negativeBtn;

    private View btnDivideView;

    private CharSequence title = "", message = "", message2 = "", positiveBtnTitle = "", negativeBtnTitle = "";

    private int titleTextColor = NO_TEXT_COLOR, msgTextColor = NO_TEXT_COLOR,
            positiveBtnTitleTextColor = NO_TEXT_COLOR, negativeBtnTitleTextColor = NO_TEXT_COLOR;

    private float titleTextSize = NO_TEXT_SIZE, msgTextSize = NO_TEXT_SIZE, positiveBtnTitleTextSize = NO_TEXT_SIZE,
            negativeBtnTitleTextSize = NO_TEXT_SIZE;

    private int resourceId;

    private boolean isPositiveBtnVisible = true, isNegativeBtnVisible = false;

    private boolean isTitleVisible = false , isMessageVisble = true, isTitleBtnVisible = false;

    private View.OnClickListener positiveBtnListener, negativeBtnListener;

    private HashMap<Integer, View.OnClickListener> mViewListener = new HashMap<Integer, View.OnClickListener>();

    public EasyAlertDialog(Context context, int resourceId, int style) {
        super(context, style);
        this.context = context;
        if (-1 != resourceId) {
            setContentView(resourceId);
            this.resourceId = resourceId;
        }
        WindowManager.LayoutParams Params = getWindow().getAttributes();
        Params.width = LayoutParams.MATCH_PARENT;
        Params.height = LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) Params);
    }

    public EasyAlertDialog(Context context, int style) {
        this(context, -1, style);
        resourceId = R.layout.easy_alert_dialog_default_layout;
    }

    public EasyAlertDialog(Context context) {
        this(context, R.style.dialog_default_style);
        resourceId = R.layout.easy_alert_dialog_default_layout;
    }

	public void setTitle(CharSequence title) {
		isTitleVisible = TextUtils.isEmpty(title) ? false : true;
		setTitleVisible(isTitleVisible);
		if (null != title) {
			this.title = title;
			if (null != titleTV)
				titleTV.setText(title);
		}
	}

    public void setTitleVisible(boolean visible){
        isTitleVisible = visible;
        if(titleView != null){
        	titleView.setVisibility(isTitleVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void setTitleBtnVisible(boolean visible) {
    	isTitleBtnVisible = visible;
    	if (titleBtn != null) {
    		titleBtn.setVisibility(isTitleBtnVisible ? View.VISIBLE : View.GONE);
    	}
	}

    public void setTitleTextColor(int color) {
        titleTextColor = color;
        if (null != titleTV && NO_TEXT_COLOR != color)
            titleTV.setTextColor(color);
    }

    public void setMessageTextColor(int color) {
        msgTextColor = color;
        if (null != messageTV && NO_TEXT_COLOR != color)
            messageTV.setTextColor(color);

    }

    public void setMessageTextSize(float size) {
        msgTextSize = size;
        if (null != messageTV && NO_TEXT_SIZE != size)
            messageTV.setTextSize(size);
    }

    public void setTitleTextSize(float size) {
        titleTextSize = size;
        if (null != titleTV && NO_TEXT_SIZE != size)
            titleTV.setTextSize(size);
    }

    public void setMessageVisible(boolean visible){
        isMessageVisble = visible;
        if(messageTV != null){
            messageTV.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setMessage(CharSequence message) {
        if (null != message) {
            this.message = message;
            if (null != messageTV)
                messageTV.setText(message);
        }
    }

    public void setMessage2(CharSequence message) {
        if(!TextUtils.isEmpty(message)) {
            this.message2 = message;
            if(null != message2TV) {
                message2TV.setText(message);
            }
        }
    }

	public void addPositiveButton(CharSequence title, int color, float size,
                                  View.OnClickListener positiveBtnListener) {
		isPositiveBtnVisible = true;
		positiveBtnTitle = TextUtils.isEmpty(title) ? context
				.getString(R.string.ok) : title;
		positiveBtnTitleTextColor = color;
		positiveBtnTitleTextSize = size;
		this.positiveBtnListener = positiveBtnListener;

		if (positiveBtn != null) {
			positiveBtn.setText(positiveBtnTitle);
			positiveBtn.setTextColor(positiveBtnTitleTextColor);
			positiveBtn.setTextSize(positiveBtnTitleTextSize);
			positiveBtn.setOnClickListener(positiveBtnListener);
		}
	}

	public void addNegativeButton(CharSequence title, int color, float size,
                                  View.OnClickListener negativeBtnListener) {
		isNegativeBtnVisible = true;
		negativeBtnTitle = TextUtils.isEmpty(title) ? context
				.getString(R.string.cancel) : title;
		negativeBtnTitleTextColor = color;
		negativeBtnTitleTextSize = size;
		this.negativeBtnListener = negativeBtnListener;

		if (negativeBtn != null) {
			negativeBtn.setText(negativeBtnTitle);
			negativeBtn.setTextColor(negativeBtnTitleTextColor);
			negativeBtn.setTextSize(negativeBtnTitleTextSize);
			negativeBtn.setOnClickListener(negativeBtnListener);
		}
	}

	public void addPositiveButton(CharSequence title,
			View.OnClickListener positiveBtnListener) {
		addPositiveButton(title, NO_TEXT_COLOR, NO_TEXT_SIZE,
				positiveBtnListener);
	}

	public void addNegativeButton(CharSequence title,
			View.OnClickListener negativeBtnListener) {
		addNegativeButton(title, NO_TEXT_COLOR, NO_TEXT_SIZE,
				negativeBtnListener);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resourceId);
        try {
        	ViewGroup root = (ViewGroup) findViewById(R.id.easy_alert_dialog_layout);
        	if (root != null) {
        		ViewGroup.LayoutParams params = root.getLayoutParams();
                params.width = (int)ScreenUtil.getDialogWidth();
                root.setLayoutParams(params);
        	}

        	titleView = findViewById(R.id.easy_dialog_title_view);
        	if (titleView != null) {
        		setTitleVisible(isTitleVisible);
        	}
        	titleBtn = (ImageButton) findViewById(R.id.easy_dialog_title_button);
        	if (titleBtn != null) {
        		setTitleBtnVisible(isTitleBtnVisible);
        	}
            titleTV = (TextView) findViewById(R.id.easy_dialog_title_text_view);
            if (titleTV != null) {
                titleTV.setText(title);
                if (NO_TEXT_COLOR != titleTextColor)
                    titleTV.setTextColor(titleTextColor);
                if (NO_TEXT_SIZE != titleTextSize)
                    titleTV.setTextSize(titleTextSize);
            }

            messageTV = (TextView) findViewById(R.id.easy_dialog_message_text_view);
            if (messageTV != null) {
                messageTV.setText(message);
                setMessageVisible(isMessageVisble);
                if (NO_TEXT_COLOR != msgTextColor)
                    messageTV.setTextColor(msgTextColor);
                if (NO_TEXT_SIZE != msgTextSize)
                    messageTV.setTextSize(msgTextSize);
            }

            message2TV = (TextView) findViewById(R.id.easy_dialog_message_2);
            if(message2TV != null && !TextUtils.isEmpty(message2)) {
            	message2TV.setVisibility(View.VISIBLE);
                message2TV.setText(message2);
            }

            positiveBtn = (Button) findViewById(R.id.easy_dialog_positive_btn);
            if (isPositiveBtnVisible && positiveBtn != null) {
                positiveBtn.setVisibility(View.VISIBLE);
                if (NO_TEXT_COLOR != positiveBtnTitleTextColor) {
                    positiveBtn.setTextColor(positiveBtnTitleTextColor);
                }
                if (NO_TEXT_SIZE != positiveBtnTitleTextSize) {
                    positiveBtn.setTextSize(positiveBtnTitleTextSize);
                }
                positiveBtn.setText(positiveBtnTitle);
                positiveBtn.setOnClickListener(positiveBtnListener);
            }

            negativeBtn = (Button) findViewById(R.id.easy_dialog_negative_btn);
            btnDivideView = findViewById(R.id.easy_dialog_btn_divide_view);
            if (isNegativeBtnVisible) {
                negativeBtn.setVisibility(View.VISIBLE);
                btnDivideView.setVisibility(View.VISIBLE);
                if (NO_TEXT_COLOR != this.negativeBtnTitleTextColor) {
                    negativeBtn.setTextColor(negativeBtnTitleTextColor);
                }
                if (NO_TEXT_SIZE != this.negativeBtnTitleTextSize) {
                    negativeBtn.setTextSize(negativeBtnTitleTextSize);
                }
                negativeBtn.setText(negativeBtnTitle);
                negativeBtn.setOnClickListener(negativeBtnListener);
            }

            if (mViewListener != null && mViewListener.size() != 0) {
                Iterator iter = mViewListener.entrySet().iterator();
                View view = null;
                while (iter.hasNext()) {
                    Map.Entry<Integer, View.OnClickListener> entry = (Map.Entry) iter.next();
                    view = findViewById(entry.getKey());
                    if(view != null && entry.getValue() != null) {
                        view.setOnClickListener(entry.getValue());
                    }
                }
            }

        } catch (Exception e) {

        }
    }

	public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public Button getPositiveBtn() {
        return positiveBtn;
    }

    public Button getNegativeBtn() {
        return negativeBtn;
    }

    public void setViewListener(int viewId, View.OnClickListener listener) {
        mViewListener.put(viewId, listener);
    }
}
