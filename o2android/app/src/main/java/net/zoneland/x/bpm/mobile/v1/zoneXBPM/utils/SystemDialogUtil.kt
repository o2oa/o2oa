package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import java.util.*


/**
 * Created by fancyLou on 2017/12/11.
 * Copyright © 2017 O2. All rights reserved.
 */

object SystemDialogUtil {
    /**
     * 确认对话框
     *
     * @param context activity
     * @param message
     * @param listener
     */
    fun getConfirmDialog(context: Context, message: String,
                         listener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        builder.setPositiveButton("确定", listener)
        builder.create()
        builder.show()
    }

    /**
     * 单选对话框
     * @param context avtivity
     * @param title 标题
     * @param items 单选的选项
     * @param defaultIndex 默认选中的项的索引
     * @param listener 监听
     */
    fun getSingleChooseDialog(context: Context, title: String,
                              items: Array<String>, defaultIndex: Int,
                              listener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        /**
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
         * 第二个参数代表索引，指定默认哪一个单选框被勾选上
         * 第三个参数给每一个单选项绑定一个监听器
         */
        builder.setSingleChoiceItems(items, defaultIndex, listener)
        builder.setPositiveButton("确定", listener)
        builder.setNegativeButton("取消", listener)
        builder.show()
    }

    /**
     * 返回一个自定义View对话框
     * @param context activity
     * @param title
     * @param view
     * @return
     */
    fun getDialogWithCustomView(context: Context, title: String, view: View): AlertDialog {
        val dialog = AlertDialog.Builder(context).create()
        dialog.setTitle(title)
        dialog.setView(view)
        dialog.show()
        return dialog
    }

    /**
     * 返回一个等待信息弹窗
     *
     * @param aty
     * 要显示弹出窗的Activity
     * @param msg
     * 弹出窗上要显示的文字
     * @param cancel
     * dialog是否可以被取消
     */
    fun getProgress(aty: Activity, msg: String,
                    cancel: Boolean): ProgressDialog {
        // 实例化一个ProgressBarDialog
        val progressDialog = ProgressDialog(aty)
        progressDialog.setMessage(msg)
        progressDialog.window!!.setLayout(DensityUtil.getScreenW(aty),
                DensityUtil.getScreenH(aty))
        progressDialog.setCancelable(cancel)
        // 设置ProgressBarDialog的显示样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()
        return progressDialog
    }


    /**
     * 返回一个日期对话框
     */
    /**
     *
     * @param title 对话框标题
     * @param textView TextView 显示日期的 日期格式 yyyy-MM-dd
     */
    fun getDateDialog(title: String, textView: TextView) {
        var textViewTime = textView.text.toString()
        if (StringUtil.isEmpty(textViewTime)) {
            textViewTime = DateHelper.nowByFormate("yyyy-MM-dd")
        }
        val time = textViewTime.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val year = StringUtil.toInt(time[0], 0)
        val month = StringUtil.toInt(time[1], 1)
        val day = StringUtil.toInt(time[2], 0)
        val dialog = DatePickerDialog(textView.context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    textView.text = DateHelper.getDate(calendar.time)
                }, year, month - 1, day)
        dialog.setTitle(title)
        dialog.show()
    }
    /**
     *
     */
    /**
     * 返回一个时间对话框
     * @param title 对话框标题
     * @param textView TextView 显示时间的 日期格式 HH:mm
     */
    fun getTimeDialog(title: String, textView: TextView) {
        var textViewTime = textView.text.toString()
        if (StringUtil.isEmpty(textViewTime)) {
            textViewTime = DateHelper.nowByFormate("HH:mm")
        }
        val time = textViewTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hour = StringUtil.toInt(time[0], 0)
        val minute = StringUtil.toInt(time[1], 1)
        val dialog = TimePickerDialog(textView.context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    textView.text = DateHelper.getDateTime("HH:mm", calendar.time)
                }, hour, minute, true)
        dialog.setTitle(title)
        dialog.show()
    }

    private val calendar = Calendar.getInstance(Locale.CHINA)

}