package jiguang.chat.utils.photochoose;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;


/**
 * [底部弹出dialog]
 *
 **/
public class BottomMenuDialog extends Dialog implements View.OnClickListener {

    private Button photographBtn;
    private Button localPhotosBtn;
    private Button cancelBtn;

    private View.OnClickListener confirmListener;
    private View.OnClickListener cancelListener;
    private View.OnClickListener middleListener;

    private String confirmText;
    private String middleText;
    private String cancelText;

    /**
     * @param context
     */
    public BottomMenuDialog(Context context) {
        super(context, R.style.dialogFullscreen);
    }

    /**
     * @param context
     * @param theme
     */
    public BottomMenuDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * @param context
     */
    public BottomMenuDialog(Context context, String confirmText, String middleText) {
        super(context, R.style.dialogFullscreen);
        this.confirmText = confirmText;
        this.middleText = middleText;
    }

    /**
     * @param context
     */
    public BottomMenuDialog(Context context, String confirmText, String middleText, String cancelText) {
        super(context, R.style.dialogFullscreen);
        this.confirmText = confirmText;
        this.middleText = middleText;
        this.cancelText = cancelText;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_bottom);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);

        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        photographBtn = (Button) findViewById(R.id.photographBtn);
        localPhotosBtn = (Button) findViewById(R.id.localPhotosBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        if (!TextUtils.isEmpty(confirmText)) {
            photographBtn.setText(confirmText);
        }
        if (!TextUtils.isEmpty(middleText)) {
            localPhotosBtn.setText(middleText);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            cancelBtn.setText(cancelText);
        }

        cancelBtn.setOnClickListener(this);
        photographBtn.setOnClickListener(this);
        localPhotosBtn.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.photographBtn) {
            if (confirmListener != null) {
                confirmListener.onClick(v);
            }
            return;
        }
        if (id == R.id.localPhotosBtn) {
            if (middleListener != null) {
                middleListener.onClick(v);
            }
            return;
        }
        if (id == R.id.cancelBtn) {
            if (cancelListener != null) {
                cancelListener.onClick(v);
            }
            dismiss();
            return;
        }
    }

    public View.OnClickListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public View.OnClickListener getCancelListener() {
        return cancelListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public View.OnClickListener getMiddleListener() {
        return middleListener;
    }

    public void setMiddleListener(View.OnClickListener middleListener) {
        this.middleListener = middleListener;
    }
}
