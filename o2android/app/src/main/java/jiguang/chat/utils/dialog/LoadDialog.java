package jiguang.chat.utils.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.utils.ToastUtil;


public class LoadDialog extends Dialog {

    /**
     * LoadDialog
     */
    private static LoadDialog loadDialog;
    /**
     * canNotCancel, the mDialogTextView dimiss or undimiss flag
     */
    private boolean canNotCancel;
    /**
     * if the mDialogTextView don't dimiss, what is the tips.
     */
    private String tipMsg;

    private TextView mShowMessage;

    /**
     * the LoadDialog constructor
     *
     * @param ctx          Context
     * @param canNotCancel boolean
     * @param tipMsg       String
     */
    public LoadDialog(final Context ctx, boolean canNotCancel, String tipMsg) {
        super(ctx);

        this.canNotCancel = canNotCancel;
        this.tipMsg = tipMsg;
        this.getContext().setTheme(android.R.style.Theme_InputMethod);
        setContentView(R.layout.layout_dialog_loading);

        if (!TextUtils.isEmpty(this.tipMsg)) {
            mShowMessage = (TextView) findViewById(R.id.show_message);
            mShowMessage.setText(this.tipMsg);
        }

        Window window = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributesParams.dimAmount = 0.5f;
        window.setAttributes(attributesParams);

        window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (canNotCancel) {
                ToastUtil.shortToast(getContext(), tipMsg);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * show the mDialogTextView
     *
     * @param context
     */
    public static void show(Context context) {
        show(context, null, false);
    }

    /**
     * show the mDialogTextView
     *
     * @param context Context
     * @param message String
     */
    public static void show(Context context, String message) {
        show(context, message, false);
    }

    /**
     * show the mDialogTextView
     *
     * @param context  Context
     * @param message  String, show the message to user when isCancel is true.
     * @param isCancel boolean, true is can't dimiss，false is can dimiss
     */
    private static void show(Context context, String message, boolean isCancel) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (loadDialog != null && loadDialog.isShowing()) {
            return;
        }
        loadDialog = new LoadDialog(context, isCancel, message);
        loadDialog.show();
    }

    /**
     * dismiss the mDialogTextView
     */
    public static void dismiss(Context context) {
        try {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    loadDialog = null;
                    return;
                }
            }

            if (loadDialog != null && loadDialog.isShowing()) {
                Context loadContext = loadDialog.getContext();
                if (loadContext != null && loadContext instanceof Activity) {
                    if (((Activity) loadContext).isFinishing()) {
                        loadDialog = null;
                        return;
                    }
                }
                loadDialog.dismiss();
                loadDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDialog = null;
        }
    }
}
