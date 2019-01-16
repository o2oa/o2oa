package jiguang.chat.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ${chenyn} on 2017/2/16.
 */

public class ToastUtil {
    public static void shortToast(Context context, String desc) {
        Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
    }
}
