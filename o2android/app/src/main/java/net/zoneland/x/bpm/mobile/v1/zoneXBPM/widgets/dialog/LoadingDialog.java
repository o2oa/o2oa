package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.StringUtil;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * Created by FancyLou on 2016/1/21.
 */
public class LoadingDialog  {

    private Dialog loadingDialog;
//    private ImageView loadingImage;
//    private Animation mAnimation;

    public LoadingDialog(Activity activity){
        this(activity, null);
    }
    public LoadingDialog(Activity activity, String message) {
        loadingDialog = new Dialog(activity, R.style.dialog_translucent);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (!StringUtil.isEmpty(message)){
            TextView textView = (TextView) loadingDialog.findViewById(R.id.tv_loading_message);
            if (textView!=null){
                textView.setText(message);
            }
        }
//        loadingImage = (ImageView) loadingDialog.findViewById(R.id.image_loading_view);
//        mAnimation = AnimationUtils.loadAnimation(activity, R.anim.loading_progress_round);
    }

    public void show() {
        if (loadingDialog == null) {
            return;
        }
//        loadingImage.startAnimation(mAnimation);
        loadingDialog.show();
    }

    public void dismiss() {
        loadingDialog.dismiss();
    }
}
