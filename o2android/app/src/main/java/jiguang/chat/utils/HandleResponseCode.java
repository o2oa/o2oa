package jiguang.chat.utils;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import jiguang.chat.activity.LoginActivity;


public class HandleResponseCode {
    public static void onHandle(Context context, int status, boolean isCenter){
        Toast toast = new Toast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(IdHelper.getLayout(context, "jmui_default_toast"), null);
        TextView content = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_toast_content_tv"));
        switch (status){
            case 0:
                break;
            case 1000:
                content.setText(IdHelper.getString(context, "jmui_record_voice_permission_denied"));
                break;
            case 1001:
                content.setText(IdHelper.getString(context, "jmui_local_picture_not_found_toast"));
                break;
            case 1002:
                content.setText(IdHelper.getString(context, "jmui_user_already_exist_toast"));
                break;
            case 1003:
                content.setText(IdHelper.getString(context, "jmui_illegal_state_toast"));
                break;
            case 800002:
                content.setText(IdHelper.getString(context, "jmui_server_800002"));
                break;
            case 800003:
                content.setText(IdHelper.getString(context, "jmui_server_800003"));
                break;
            case 800004:
                content.setText(IdHelper.getString(context, "jmui_server_800004"));
                break;
            case 800005:
                content.setText(IdHelper.getString(context, "jmui_server_800005"));
                break;
            case 800006:
                content.setText(IdHelper.getString(context, "jmui_server_800006"));
                break;
            case 800012:
                content.setText(IdHelper.getString(context, "jmui_server_800012"));
                break;
            case 800013:
                content.setText(IdHelper.getString(context, "jmui_server_800013"));
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                context.startActivity(intent);
                break;
            case 800014:
                content.setText(IdHelper.getString(context, "jmui_server_800014"));
                break;
            case 801001:
            case 802001:
                content.setText(IdHelper.getString(context, "jmui_server_802001"));
                break;
            case 802002:
            case 898002:
            case 801003:
            case 899002:
                content.setText(IdHelper.getString(context, "jmui_server_801003"));
                break;
            case 899004:
            case 801004:
                content.setText(IdHelper.getString(context, "jmui_server_801004"));
                break;
            case 803001:
                content.setText(IdHelper.getString(context, "jmui_server_803001"));
                break;
            case 803002:
                content.setText(IdHelper.getString(context, "jmui_server_803002"));
                break;
            case 803003:
                content.setText(IdHelper.getString(context, "jmui_server_803003"));
                break;
            case 803004:
                content.setText(IdHelper.getString(context, "jmui_server_803004"));
                break;
            case 803005:
                content.setText(IdHelper.getString(context, "jmui_server_803005"));
                break;
            case 803008:
                content.setText(IdHelper.getString(context, "jmui_server_803008"));
                break;
            case 803009:
                content.setText(IdHelper.getString(context, "jmui_server_803009"));
                break;
            case 803010:
                content.setText(IdHelper.getString(context, "jmui_server_803010"));
                break;
            case 805002:
                content.setText(IdHelper.getString(context, ""));
            case 808003:
                content.setText(IdHelper.getString(context, "jmui_server_808003"));
                break;
            case 808004:
                content.setText(IdHelper.getString(context, "jmui_server_808004"));
                break;
            case 810003:
                content.setText(IdHelper.getString(context, "jmui_server_810003"));
                break;
            case 810005:
                content.setText(IdHelper.getString(context, "jmui_server_810005"));
                break;
            case 810007:
                content.setText(IdHelper.getString(context, "jmui_server_810007"));
                break;
            case 810008:
                content.setText(IdHelper.getString(context, "jmui_server_810008"));
                break;
            case 810009:
                content.setText(IdHelper.getString(context, "jmui_server_810009"));
                break;
            case 811003:
                content.setText(IdHelper.getString(context, "jmui_server_811003"));
                break;
            case 812002:
                content.setText(IdHelper.getString(context, "jmui_server_812002"));
                break;
            case 818001:
                content.setText(IdHelper.getString(context, "jmui_server_818001"));
                break;
            case 818002:
                content.setText(IdHelper.getString(context, "jmui_server_818002"));
                break;
            case 818003:
                content.setText(IdHelper.getString(context, "jmui_server_818003"));
                break;
            case 818004:
                content.setText(IdHelper.getString(context, "jmui_server_818004"));
                break;
            case 899001:
            case 898001:
                content.setText(IdHelper.getString(context, "jmui_sdk_http_899001"));
                break;
            case 898005:
                content.setText(IdHelper.getString(context, "jmui_sdk_http_898005"));
                break;
            case 898006:
                content.setText(IdHelper.getString(context, "jmui_sdk_http_898006"));
                break;
            case 898008:
                content.setText(IdHelper.getString(context, "jmui_sdk_http_898008"));
                break;
            case 898009:
                content.setText(IdHelper.getString(context, "jmui_sdk_http_898009"));
                break;
            case 898010:
                content.setText(IdHelper.getString(context, "jmui_sdk_http_898010"));
                break;
            case 898030:
                content.setText(IdHelper.getString(context, "jmui_sdk_http_898030"));
                break;
            case 800009:
            case 871104:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871104"));
                break;
            case 871300:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871300"));
                break;
            case 871303:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871303"));
                break;
            case 871304:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871304"));
                break;
            case 871305:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871305"));
                break;
            case 871309:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871309"));
                break;
            case 871310:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871310"));
                break;
            case 871311:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871311"));
                break;
            case 871312:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871312"));
                break;
            case 871319:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871319"));
                break;
            case 871403:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871403"));
                break;
            case 871404:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871404"));
                break;
            case 871501:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871501"));
                break;
            case 871502:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871502"));
                break;
            case 871503:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871503"));
                break;
            case 871504:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871504"));
                break;
            case 871505:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871505"));
                break;
            case 871506:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871506"));
                break;
            case 871102:
            case 871201:
                content.setText(IdHelper.getString(context, "jmui_sdk_87x_871201"));
                break;
            default:
                content.setText("Response code: " + status);
                break;
        }
        if(isCenter){
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        view.getBackground().setAlpha(150);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
