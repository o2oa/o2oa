package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.content.Context;
import android.graphics.Color;

import com.afollestad.materialdialogs.MaterialDialog;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * Created by FancyLou on 2016/7/12.
 */
public class MaterialDialogSupport {

    /**
     * 打开自定义界面 dialog
     * @param context
     * @param title
     * @param viewLayout
     * @param positiveCallback
     */
    public static MaterialDialog openCustomViewDialog(Context context, String title, int viewLayout, MaterialDialog.SingleButtonCallback positiveCallback) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .customView(viewLayout, true)
                .positiveText(R.string.positive)
                .negativeText(R.string.cancel)
                .negativeColor(Color.parseColor("#9B9B9B"))
                .onPositive(positiveCallback).show();
    }

    /**
     *
     * @param context
     * @param title
     * @param positiveText
     * @param negativeText
     * @param viewLayout
     * @param positiveCallback
     * @return
     */
    public static MaterialDialog openCustomViewDialog(Context context, String title, String positiveText,
                                                      String negativeText, int viewLayout,
                                                      MaterialDialog.SingleButtonCallback positiveCallback,
                                                      MaterialDialog.SingleButtonCallback negativeCallback) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .customView(viewLayout, true)
                .positiveText(positiveText)
                .negativeText(negativeText)
                .negativeColor(Color.parseColor("#9B9B9B"))
                .onPositive(positiveCallback)
                .onNegative(negativeCallback).show();
    }

    /**
     * 单个按钮 提示对话框
     * @param context
     * @param message
     * @param positiveCallback
     */
    public static void openAlertDialog(Context context, String message, MaterialDialog.SingleButtonCallback positiveCallback) {
        new MaterialDialog.Builder(context)
                .title(R.string.hint)
                .content(message)
                .positiveText(R.string.positive)
                .onPositive(positiveCallback)
                .show();
    }

    /**
     * 确认对话框
     * @param context
     * @param message
     * @param positiveCallback
     */
    public static void openConfirmDialog(Context context, String message, MaterialDialog.SingleButtonCallback positiveCallback) {
        new MaterialDialog.Builder(context)
                .title(R.string.confirm)
                .iconRes(R.mipmap.icon_okr_overtime)
                .content(message)
                .positiveText(R.string.positive)
                .negativeText(R.string.cancel)
                .negativeColor(Color.parseColor("#9B9B9B"))
                .onPositive(positiveCallback)
                .show();
    }

    public static void openDialog(Context context, String title, String message, String positiveText, String cancelText, MaterialDialog.SingleButtonCallback callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(positiveText)
                .negativeText(cancelText)
                .negativeColor(Color.parseColor("#9B9B9B"))
                .onPositive(callback)
                .show();
    }

    /**
     * 单选dialog
     * @param context
     * @param title
     * @param items
     * @param selectIndex
     * @param callback
     */
    public static void openChoiceDialog(Context context, String title, String[] items, int selectIndex, MaterialDialog.ListCallbackSingleChoice callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .items(items)
                .itemsCallbackSingleChoice(selectIndex, callback)
                .positiveText(R.string.positive)
                .negativeText(R.string.cancel)
                .negativeColor(Color.parseColor("#9B9B9B"))
                .show();
    }

    /**
     * 单选dialog 加取消操作
     * @param context
     * @param title
     * @param items
     * @param selectIndex
     * @param callback
     * @param negativeCallback
     */
    public static void openChoiceDialog(Context context, String title, String[] items, int selectIndex,
                                        MaterialDialog.ListCallbackSingleChoice callback, MaterialDialog.SingleButtonCallback negativeCallback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .items(items)
                .itemsCallbackSingleChoice(selectIndex, callback)
                .positiveText(R.string.positive)
                .negativeText(R.string.cancel)
                .onNegative(negativeCallback)
                .negativeColor(Color.parseColor("#9B9B9B"))
                .show();
    }
}
