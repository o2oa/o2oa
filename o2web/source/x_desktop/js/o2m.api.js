/** ***** BEGIN LICENSE BLOCK *****
 * |------------------------------------------------------------------------------|
 * | O2OA 活力办公 创意无限    o2m.api.js                                            |
 * |------------------------------------------------------------------------------|
 * | Distributed under the AGPL license:                                          |
 * |------------------------------------------------------------------------------|
 * | Copyright © 2018, o2oa.net, o2server.io O2 Team                              |
 * | All rights reserved.                                                         |
 * |------------------------------------------------------------------------------|
 *
 *  This file is part of O2OA.
 *
 *  O2OA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  O2OA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ******/



/**
 * <b>o2m</b> 是O2OA移动端APP提供的调用原生控件的能力，帮助开发者高效使用拍照、定位等手机系统的能力，同时可以直接使用扫一扫、打开原生应用、选择时间，人员，组织等业务的能力，带给门户接近原生代码的体验
 * <br/>
 * <b>o2m</b> 只有在O2OA移动端APP中才能提供能力
 * @module o2m
 * @o2ordernumber 160
 * @o2range {流程表单|门户}
 * @o2syntax
 * // 可以在移动端 流程表单、门户 上使用
 * this.o2m
 * 
 */
(function () {
  this.o2m = {
    version: {
      v: "1.1.0",
      build: "2021.01.20",
      info: "O2OA 活力办公 创意无限. Copyright © 2021, o2oa.net O2 Team All rights reserved."
    },
    log: function (message) {
      window.o2android && window.o2android.o2mLog ? window.o2android.o2mLog(message) : window.webkit.messageHandlers.o2mLog.postMessage(message);
    }
  };

  /** ***** BEGIN NOTIFICATION BLOCK *****
    notification 模块   
      alert
      confirm
      prompt
      vibrate
      toast
      actionSheet
      showLoading
      hideLoading
  * ***** END NOTIFICATION BLOCK ******/

  this.o2m.notification = {};

  var _notification_post = function (body, onFail) {
    if (body == null) {
      if (onFail && typeof onFail === "function") {
        onFail("参数异常！");
        return
      }
    }
    var message = JSON.stringify(body);
    if ((window.o2mNotification && window.o2mNotification.postMessage) || (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.o2mNotification)) {
      window.o2mNotification && window.o2mNotification.postMessage ? window.o2mNotification.postMessage(message) : window.webkit.messageHandlers.o2mNotification.postMessage(message);
    } else {
      if (onFail && typeof onFail === "function") {
        onFail("请在O2OA移动端使用！");
        return
      }
    }
  };


  this.o2m.notification.alertSuccess = function () {
    console.log("notification alert back");
  };
  var _o2m_n_alert = function (alert) {
    var message = alert && alert.message ? alert.message : "";
    var title = alert && alert.title ? alert.title : "";
    var buttonName = alert && alert.buttonName ? alert.buttonName : "";
    var onSuccess = alert && alert.onSuccess ? alert.onSuccess : null;
    var onFail = alert && alert.onFail ? alert.onFail : null;
    if (message === "") {
      if (typeof onFail === "function") { onFail("消息内容不能为空！"); }
      return;
    }
    if (onSuccess && typeof onSuccess === "function") {
      o2m.notification.alertSuccess = onSuccess;
    }
    var body = {
      type: "alert",
      callback: "o2m.notification.alertSuccess",
      data: {
        message: message,
        title: title,
        buttonName: buttonName,
      }
    };
    _notification_post(body, onFail);
  };
  /**
   * 原生Alert提示弹出窗
   * @method alert
   * @o2membercategory notification
   * @static
   * @param {Object} obj 提示窗传入对象
   * <pre><code class='language-js'>{
   *  "title": "提示",  //消息标题，可为空
   *  "message": "这里是消息内容", //消息内容
   *  "buttonName": "确定", //确定按钮名称
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/notification_alert.jpeg">
   * </caption>
   * o2m.notification.alert({
   *  message: "亲爱的",
   *  title: "提示",//可传空
   *  buttonName: "收到",
   *  onSuccess : function() {//onSuccess将在点击button之后回调},
   *  onFail : function(err) {}
   * });
   * 
   */
  this.o2m.notification.alert = _o2m_n_alert;

  //notification.confirm
  this.o2m.notification.confirmSuccess = function (index) {
    console.log("notification confirm back, click button index: " + index);
  };
  var _o2m_n_confirm = function (c) {
    var buttonLabels = c && c.buttonLabels ? c.buttonLabels : ["确定", "取消"];
    var message = c && c.message ? c.message : "";
    var title = c && c.title ? c.title : "";
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (message === "") {
      if (typeof onFail === "function") { onFail("消息内容message不能为空！"); }
      return;
    }
    if (buttonLabels.length != 2) {
      if (typeof onFail === "function") { onFail("按钮名称数组长度只能是2！"); }
      return;
    }
    if (onSuccess && typeof onSuccess === "function") {
      o2m.notification.confirmSuccess = onSuccess;
    }
    var body = {
      type: "confirm",
      callback: "o2m.notification.confirmSuccess",
      data: {
        message: message,
        title: title,
        buttonLabels: buttonLabels,
      }
    };
    _notification_post(body, onFail);
  }
  /**
   * 原生confirm提示弹出窗
   * @method confirm
   * @o2membercategory notification
   * @static
   * @param {Object} obj 提示窗传入对象
   * <pre><code class='language-js'>{
   *  "title": "提示",  //消息标题，可为空
   *  "message": "这里是消息内容", //消息内容
   *  "buttonLabels": ["确定", "取消"], //按钮名称 Array[String]
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/notification_confirm.jpeg">
   * </caption>
   * o2m.notification.confirm({
   *  message: "你爱我吗",
   *  title: "提示",//可传空
   *  buttonLabels: ['爱', '不爱'],
   *  onSuccess : function(buttonIndex) {
   *    //onSuccess将在点击button之后回调
   *    //buttonIndex: 0 被点击按钮的索引值，Number类型，从0开始
   * },
   *  onFail : function(err) {}
   * });
   * 
   */
  this.o2m.notification.confirm = _o2m_n_confirm;

  //notification.prompt
  this.o2m.notification.promptSuccess = function (result) {
    console.log("notification prompt back, click button result: " + result);
  };
  var _o2m_n_prompt = function (c) {
    var buttonLabels = c && c.buttonLabels ? c.buttonLabels : ["确定", "取消"];
    var message = c && c.message ? c.message : "";
    var title = c && c.title ? c.title : "";
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (message === "") {
      if (typeof onFail === "function") { onFail("消息内容message不能为空！"); }
      return;
    }
    if (buttonLabels.length != 2) {
      if (typeof onFail === "function") { onFail("按钮名称数组长度只能是2！"); }
      return;
    }
    if (onSuccess && typeof onSuccess === "function") {
      o2m.notification.promptSuccess = onSuccess;
    }
    var body = {
      type: "prompt",
      callback: "o2m.notification.promptSuccess",
      data: {
        message: message,
        title: title,
        buttonLabels: buttonLabels,
      }
    };
    _notification_post(body, onFail);
  }
  /**
   * 原生prompt提示弹出窗
   * @method prompt
   * @o2membercategory notification
   * @static
   * @param {Object} obj prompt需要传入对象
   * <pre><code class='language-js'>{
   *  "title": "提示",  //消息标题，可为空
   *  "message": "这里是消息内容", //消息内容
   *  "buttonLabels": ['继续', '不玩了'], //按钮名称 Array[String]
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/notification_prompt.jpeg">
   * </caption>
   * o2m.notification.prompt({
   *  message: "再说一遍？",
   *  title: "提示",//可传空
   *  buttonLabels: ['继续', '不玩了'],
   *  onSuccess : function(result) {
   *     //result是一个字符串，格式是json格式，内容如下：
   *     //{
   *     //    buttonIndex: 0, //被点击按钮的索引值，Number类型，从0开始
   *     //    value: '' //输入的值
   *     //}
   * },
   *  onFail : function(err) {}
   * });
   * 
   */
  this.o2m.notification.prompt = _o2m_n_prompt;


  //notification.vibrate
  this.o2m.notification.vibrateSuccess = function () {
    console.log("notification vibrate back, click button");
  };
  var _o2m_n_vibrate = function (c) {
    var duration = c && c.duration ? c.duration : 300;
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;

    if (onSuccess && typeof onSuccess === "function") {
      o2m.notification.vibrateSuccess = onSuccess;
    }
    var body = {
      type: "vibrate",
      callback: "o2m.notification.vibrateSuccess",
      data: {
        duration: duration
      }
    };
    _notification_post(body, onFail);
  };

  /**
   * 手机震动
   * @method vibrate
   * @o2membercategory notification
   * @static
   * @param {Object} obj 震动需要传入对象
   * <pre><code class='language-js'>{
   *  "duration": 300,  //震动时间，android可配置 iOS忽略
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * o2m.notification.vibrate({
   *  duration: 300, 
   *  onSuccess : function() {
   * },
   *  onFail : function(err) {}
   * });
   * 
   */
  this.o2m.notification.vibrate = _o2m_n_vibrate;

  //notification.toast
  this.o2m.notification.toastSuccess = function () {
    console.log("notification toast back, click button");
  };
  var _o2m_n_toast = function (c) {
    var duration = c && c.duration ? c.duration : 300;
    var message = c && c.message ? c.message : "";
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (message === "") {
      if (typeof onFail === "function") { onFail("消息内容message不能为空！"); }
      return;
    }
    if (onSuccess && typeof onSuccess === "function") {
      o2m.notification.toastSuccess = onSuccess;
    }
    var body = {
      type: "toast",
      callback: "o2m.notification.toastSuccess",
      data: {
        duration: duration,
        message: message
      }
    };
    _notification_post(body, onFail);
  };

  /**
   * toast提示
   * @method toast
   * @o2membercategory notification
   * @static
   * @param {Object} obj toast需要传入对象
   * <pre><code class='language-js'>{
   *  "message": "这里是提示信息",
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * o2m.notification.toast({
   *  message: "提示消息内容", 
   *  onSuccess : function() {
   * },
   *  onFail : function(err) {}
   * });
   * 
   */
  this.o2m.notification.toast = _o2m_n_toast;

  //notification.actionSheet
  this.o2m.notification.actionSheetSuccess = function (buttonIndex) {
    console.log("notification actionSheet back, click button:" + buttonIndex);
  };
  var _o2m_n_actionSheet = function (c) {
    var title = c && c.title ? c.title : "";
    var cancelButton = c && c.cancelButton ? c.cancelButton : "取消";
    var otherButtons = c && c.otherButtons ? c.otherButtons : [];
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (title === "") {
      if (typeof onFail === "function") { onFail("title标题不能为空！"); }
      return;
    }
    if (otherButtons.length < 1) {
      if (typeof onFail === "function") { onFail("其他按钮列表不能为空！"); }
      return;
    }
    if (onSuccess && typeof onSuccess === "function") {
      o2m.notification.actionSheetSuccess = onSuccess;
    }
    var body = {
      type: "actionSheet",
      callback: "o2m.notification.actionSheetSuccess",
      data: {
        title: title,
        cancelButton: cancelButton,
        otherButtons: otherButtons
      }
    };
    _notification_post(body, onFail);
  };
  /**
   * 底部弹出菜单
   * @method actionSheet
   * @o2membercategory notification
   * @static
   * @param {Object} obj actionSheet需要传入对象
   * <pre><code class='language-js'>{
   *  "title": "谁是最棒哒？",//标题
   *  "cancelButton": '取消', //取消按钮文本
   *  "otherButtons": ["孙悟空","猪八戒","唐僧","沙和尚"], //其他选项按钮名称
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/notification_action_sheet.jpeg">
   * </caption>
   * o2m.notification.actionSheet({
   *  title: "谁是最棒哒？", //标题
   *  cancelButton: '取消', //取消按钮文本
   *  otherButtons: ["孙悟空","猪八戒","唐僧","沙和尚"],
   *  onSuccess : function(buttonIndex) {
   *  //buttonIndex: 0 被点击按钮的索引值，Number，从0开始, 取消按钮为-1
   * },
   *  onFail : function(err) {}
   * });
   * 
   */
  this.o2m.notification.actionSheet = _o2m_n_actionSheet;

  //notification.showLoading
  this.o2m.notification.showLoadingSuccess = function () {
    console.log("notification showLoading back");
  };
  var _o2m_n_showLoading = function (c) {
    var text = c && c.text ? c.text : "";
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.notification.showLoadingSuccess = onSuccess;
    }
    var body = {
      type: "showLoading",
      callback: "o2m.notification.showLoadingSuccess",
      data: {
        text: text
      }
    };
    _notification_post(body, onFail);
  };
  /**
   * 显示Loading浮层，请和hideLoading配合使用
   * @method showLoading
   * @o2membercategory notification
   * @static
   * @param {Object} obj showLoading需要传入对象
   * <pre><code class='language-js'>{
   *  text: "使劲加载中..", //loading显示的字符，空表示不显示文字
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/notification_showLoading.jpeg">
   * </caption>
   * o2m.notification.showLoading({
   *  text: "使劲加载中..", //loading显示的字符，空表示不显示文字
   *  onSuccess : function() {
   * },
   *  onFail : function(err) {}
   * });
   * 
   */
  this.o2m.notification.showLoading = _o2m_n_showLoading;

  //notification.hideLoading
  this.o2m.notification.hideLoadingSuccess = function () {
    console.log("notification hideLoading back");
  };
  var _o2m_n_hideLoading = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.notification.hideLoadingSuccess = onSuccess;
    }
    var body = {
      type: "hideLoading",
      callback: "o2m.notification.hideLoadingSuccess",
      data: {
      }
    };
    _notification_post(body, onFail);
  }
  /**
   * 隐藏Loading浮层
   * @method hideLoading
   * @o2membercategory notification
   * @static
   * @param {Object} obj hideLoading需要传入对象
   * <pre><code class='language-js'>{
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * o2m.notification.hideLoading({
   *  onSuccess : function() {
   * },
   *  onFail : function(err) {}
   * });
   * 
   */
  this.o2m.notification.hideLoading = _o2m_n_hideLoading;





  /** ***** BEGIN UTIL BLOCK *****
    util 模块   
      date
        o2m.util.date.datePicker
        o2m.util.date.timePicker
        o2m.util.date.dateTimePicker
      calendar
        o2m.util.calendar.chooseOneDay
        o2m.util.calendar.chooseDateTime
        o2m.util.calendar.chooseInterval
      device
        o2m.util.device.getPhoneInfo
        o2m.util.device.scan
        o2m.util.deveice.location
      navigation
        o2m.util.navigation.setTitle
        o2m.util.navigation.close
        o2m.util.navigation.goBack
  
  
  
  * ***** END UTIL BLOCK ******/

  this.o2m.util = {
    date: {},
    calendar: {},
    device: {},
    navigation: {}
  };

  var _util_post = function (body, onFail) {
    if (body == null) {
      if (onFail && typeof onFail === "function") {
        onFail("参数异常！");
        return;
      }
    }
    var message = JSON.stringify(body);
    if ((window.o2mUtil && window.o2mUtil.postMessage) || (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.o2mUtil)) {
      window.o2mUtil && window.o2mUtil.postMessage ? window.o2mUtil.postMessage(message) : window.webkit.messageHandlers.o2mUtil.postMessage(message);
    } else {
      if (onFail && typeof onFail === "function") {
        onFail("请在O2OA移动端使用！");
      }
    }
  };

  //o2m.util.date.datePicker
  this.o2m.util.date.datePickerSuccess = function (result) {
    console.log("util date datePicker back, result:" + result);
  };
  var _o2m_u_date_datePicker = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    var value = c && c.value ? c.value : "";
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.date.datePickerSuccess = onSuccess;
    }
    var body = {
      type: "date.datePicker",
      callback: "o2m.util.date.datePickerSuccess",
      data: {
        value: value
      }
    };
    _util_post(body, onFail);
  };
   
  /**
   * 日期选择器
   * @method datePicker
   * @o2membercategory util.date
   * @static
   * @param {Object} obj  datePicker需要传入对象
   * <pre><code class='language-js'>{
   *  value: '2019-04-17', //默认显示日期
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/util_date_picker.jpeg">
   * </caption>
   * o2m.util.date.datePicker({
   * value: '2019-04-17', //默认显示日期
   * onSuccess : function(result) {
   *     //onSuccess将在点击完成之后回调
   *     {
   *         value: "2019-02-10"
   *     }
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.date.datePicker = _o2m_u_date_datePicker;

  //o2m.util.date.timePicker
  this.o2m.util.date.timePickerSuccess = function (result) {
    console.log("util date timePicker back, result:" + result);
  };
  var _o2m_u_date_timePicker = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    var value = c && c.value ? c.value : "";
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.date.timePickerSuccess = onSuccess;
    }
    var body = {
      type: "date.timePicker",
      callback: "o2m.util.date.timePickerSuccess",
      data: {
        value: value
      }
    };
    _util_post(body, onFail);
  };
  /**
   * 时间选择器
   * @method timePicker
   * @o2membercategory util.date
   * @static
   * @param {Object} obj  timePicker需要传入对象
   * <pre><code class='language-js'>{
   *  value: '14:00', //默认显示时间
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/util_time_picker.jpeg">
   * </caption>
   * o2m.util.date.timePicker({
   * value: '14:00', //默认显示时间
   * onSuccess : function(result) {
   *     //onSuccess将在点击完成之后回调
   *     {
   *         value: "18:10"
   *     }
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.date.timePicker = _o2m_u_date_timePicker;



  //o2m.util.date.dateTimePicker
  this.o2m.util.date.dateTimePickerSuccess = function (result) {
    console.log("util date dateTimePicker back, result:" + result);
  };
  var _o2m_u_date_dateTimePicker = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    var value = c && c.value ? c.value : "";
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.date.dateTimePickerSuccess = onSuccess;
    }
    var body = {
      type: "date.dateTimePicker",
      callback: "o2m.util.date.dateTimePickerSuccess",
      data: {
        value: value
      }
    };
    _util_post(body, onFail);
  };
  /**
   * 日期时间选择器
   * @method dateTimePicker
   * @o2membercategory util.date
   * @static
   * @param {Object} obj  dateTimePicker需要传入对象
   * <pre><code class='language-js'>{
   *  value: '2019-05-05 14:00', //默认显示时间
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/util_date_time_picker.jpeg">
   * </caption>
   * o2m.util.date.dateTimePicker({
   * value: '2019-05-05 14:00', //默认显示时间
   * onSuccess : function(result) {
   *     //onSuccess将在点击完成之后回调
   *     {
   *         value: "2019-03-18 18:10"
   *     }
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.date.dateTimePicker = _o2m_u_date_dateTimePicker;



  //o2m.util.calendar.chooseOneDay
  this.o2m.util.calendar.chooseOneDaySuccess = function (result) {
    console.log("util calendar chooseOneDay back, result:" + result);
  };
  var _o2m_u_calendar_chooseOneDay = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    var value = c && c.value ? c.value : "";
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.calendar.chooseOneDaySuccess = onSuccess;
    }
    var body = {
      type: "calendar.chooseOneDay",
      callback: "o2m.util.calendar.chooseOneDaySuccess",
      data: {
        value: value
      }
    };
    _util_post(body, onFail);
  };
  /**
   * 月历日期选择器
   * @method chooseOneDay
   * @o2membercategory util.date
   * @static
   * @param {Object} obj  chooseOneDay需要传入对象
   * <pre><code class='language-js'>{
   *  value: '2019-05-05', //默认显示日期
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/util_choose_one_day.jpeg">
   * </caption>
   * o2m.util.date.chooseOneDay({
   * value: '2019-05-05', //默认显示日期
   * onSuccess : function(result) {
   *     //onSuccess将在点击完成之后回调
   *     {
   *         value: "2019-03-18"
   *     }
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.calendar.chooseOneDay = _o2m_u_calendar_chooseOneDay;


  //o2m.util.calendar.chooseDateTime
  this.o2m.util.calendar.chooseDateTimeSuccess = function (result) {
    console.log("util calendar chooseDateTime back, result:" + result);
  };
  var _o2m_u_calendar_chooseDateTime = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    var value = c && c.value ? c.value : "";
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.calendar.chooseDateTimeSuccess = onSuccess;
    }
    var body = {
      type: "calendar.chooseDateTime",
      callback: "o2m.util.calendar.chooseDateTimeSuccess",
      data: {
        value: value
      }
    };
    _util_post(body, onFail);
  };
  /**
   * 月历日期时间选择器
   * @method chooseDateTime
   * @o2membercategory util.date
   * @static
   * @param {Object} obj  chooseDateTime需要传入对象
   * <pre><code class='language-js'>{
   *  value: '2019-05-05 11:00', //默认显示时间
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/util_choose_date_time.jpeg">
   * </caption>
   * o2m.util.date.chooseDateTime({
   * value: '2019-05-05 11:00', //默认显示时间
   * onSuccess : function(result) {
   *     //onSuccess将在点击完成之后回调
   *     {
   *         value: "2019-03-18 18:45"
   *     }
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.calendar.chooseDateTime = _o2m_u_calendar_chooseDateTime;


  //o2m.util.calendar.chooseInterval
  this.o2m.util.calendar.chooseIntervalSuccess = function (result) {
    console.log("util calendar chooseInterval back, result:" + result);
  };
  var _o2m_u_calendar_chooseInterval = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    var startDate = c && c.startDate ? c.startDate : "";
    var endDate = c && c.endDate ? c.endDate : "";
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.calendar.chooseIntervalSuccess = onSuccess;
    }
    var body = {
      type: "calendar.chooseInterval",
      callback: "o2m.util.calendar.chooseIntervalSuccess",
      data: {
        startDate: startDate,
        endDate: endDate
      }
    };
    _util_post(body, onFail);
  };
  /**
   * 月历日期区间选择器
   * @method chooseInterval
   * @o2membercategory util.date
   * @static
   * @param {Object} obj  chooseInterval需要传入对象
   * <pre><code class='language-js'>{
   *  startDate: '2019-05-05',
   *  endDate: '2019-05-06',
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/util_choose_interval.jpeg">
   * </caption>
   * o2m.util.date.chooseInterval({
   * value: '2019-05-05 11:00', //默认显示时间
   * onSuccess : function(result) {
   *     //onSuccess将在点击完成之后回调
   *     {
   *         startDate: "2019-05-05", 
            endDate: "2019-05-06",
   *     }
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.calendar.chooseInterval = _o2m_u_calendar_chooseInterval;


  //o2m.util.device.getPhoneInfo
  this.o2m.util.device.getPhoneInfoSuccess = function (result) {
    console.log("util calendar chooseInterval back, result:" + result);
  };
  var _o2m_u_device_getPhoneInfo = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.device.getPhoneInfoSuccess = onSuccess;
    }
    var body = {
      type: "device.getPhoneInfo",
      callback: "o2m.util.device.getPhoneInfoSuccess",
      data: {

      }
    };
    _util_post(body, onFail);
  };
  /**
   * 获取手机基础信息
   * @method getPhoneInfo
   * @o2membercategory util.device
   * @static
   * @param {Object} obj  getPhoneInfo需要传入对象
   * <pre><code class='language-js'>{
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * o2m.util.device.getPhoneInfo({
   * onSuccess : function(result) {
   *     {
   *      screenWidth: 1080, // 手机屏幕宽度
   *      screenHeight: 1920, // 手机屏幕高度
   *      brand:'Mi'， // 手机品牌
   *      model:'Note4', // 手机型号
   *      version:'7.0'. // 版本
   *      netInfo:'wifi' , // 网络类型 wifi／4g／3g 
   *      operatorType :'xx' // 运营商信息
   *     }
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.device.getPhoneInfo = _o2m_u_device_getPhoneInfo;


  //o2m.util.device.scan
  this.o2m.util.device.scanSuccess = function (result) {
    console.log("util device scan back, result:" + result);
  };
  var _o2m_u_device_scan = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.device.scanSuccess = onSuccess;
    }
    var body = {
      type: "device.scan",
      callback: "o2m.util.device.scanSuccess",
      data: {

      }
    };
    _util_post(body, onFail);
  };
  /**
   * 扫二维码
   * @method scan
   * @o2membercategory util.device
   * @static
   * @param {Object} obj  scan需要传入对象
   * <pre><code class='language-js'>{
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * o2m.util.device.scan({
   * onSuccess : function(result) {
   *     { 'text': '扫码内容'}
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.device.scan = _o2m_u_device_scan;


  //o2m.util.device.location
  this.o2m.util.device.locationSuccess = function (result) {
    console.log("util device location back, result:" + result);
  };
  var _o2m_u_device_location = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.device.locationSuccess = onSuccess;
    }
    var body = {
      type: "device.location",
      callback: "o2m.util.device.locationSuccess",
      data: {

      }
    };
    _util_post(body, onFail);
  };
  /**
   * 单次定位
   * @method location
   * @o2membercategory util.device
   * @static
   * @param {Object} obj  location需要传入对象
   * <pre><code class='language-js'>{
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * o2m.util.device.location({
   * onSuccess : function(result) {
   *     //这里返回百度坐标系的定位信息 
   *     { 
   *      'latitude': 39.903578, // 纬度
   *      'longitude': 116.473565, // 经度
   *      'address': '地址描述'
   *      }
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.device.location = _o2m_u_device_location;


  //o2m.util.navigation.setTitle
  this.o2m.util.navigation.setTitleSuccess = function (result) {
    console.log("util calendar chooseInterval back, result:" + result);
  };
  var _o2m_u_navigation_setTitle = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    var title = c && c.title ? c.title : "";
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.navigation.setTitleSuccess = onSuccess;
    }
    var body = {
      type: "navigation.setTitle",
      callback: "o2m.util.navigation.setTitleSuccess",
      data: {
        title: title
      }
    };
    _util_post(body, onFail);
  };
  /**
   * 设置原生页面标题
   * @method setTitle
   * @o2membercategory util.navigation
   * @static
   * @param {Object} obj  setTitle需要传入对象
   * <pre><code class='language-js'>{
   *  title : '导航标题',    
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * o2m.util.navigation.setTitle({
   * title : '导航标题', 
   * onSuccess : function() {
   *  },
   *  onFail : function(err) {}
   *});
   */
  this.o2m.util.navigation.setTitle = _o2m_u_navigation_setTitle;


  //o2m.util.navigation.close
  this.o2m.util.navigation.closeSuccess = function (result) {
    console.log("util calendar chooseInterval back, result:" + result);
  };
  var _o2m_u_navigation_close = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.navigation.closeSuccess = onSuccess;
    }
    var body = {
      type: "navigation.close",
      callback: "o2m.util.navigation.closeSuccess",
      data: {
      }
    };
    _util_post(body, onFail);
  };
  /**
   * 关闭当前原生页面
   * @method close
   * @o2membercategory util.navigation
   * @static
   * @example
   * o2m.util.navigation.close();
   */
  this.o2m.util.navigation.close = _o2m_u_navigation_close;


  //o2m.util.navigation.goBack
  this.o2m.util.navigation.goBackSuccess = function (result) {
    console.log("util calendar chooseInterval back, result:" + result);
  };
  var _o2m_u_navigation_goBack = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.util.navigation.goBackSuccess = onSuccess;
    }
    var body = {
      type: "navigation.goBack",
      callback: "o2m.util.navigation.goBackSuccess",
      data: {
      }
    };
    _util_post(body, onFail);
  };
  /**
   * 返回上级原生页面
   * @method goBack
   * @o2membercategory util.navigation
   * @static
   * @example
   * o2m.util.navigation.goBack();
   */
  this.o2m.util.navigation.goBack = _o2m_u_navigation_goBack;




  /** ***** BEGIN BIZ BLOCK *****
   biz 模块
   contact
   o2m.biz.contact.PersonPicker
   o2m.biz.contact.IdentityPicker
   o2m.biz.contact.departmentsPicker
   o2m.biz.contact.ComplexPicker
   o2m.biz.contact.GroupPicker

   * ***** END UTIL BLOCK ******/

  this.o2m.biz = {
    contact: {}
  };

  var _biz_post = function (body, onFail) {
    if (body == null) {
      if (onFail && typeof onFail === "function") {
        onFail("参数异常！");
        return;
      }
    }
    var message = JSON.stringify(body);
    if ((window.o2mBiz && window.o2mBiz.postMessage) || (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.o2mBiz)) {
      window.o2mBiz && window.o2mBiz.postMessage ? window.o2mBiz.postMessage(message) : window.webkit.messageHandlers.o2mBiz.postMessage(message);
    } else {
      if (onFail && typeof onFail === "function") {
        onFail("请在O2OA移动端使用！");
      }
    }
  };

  //o2m.biz.workClose()
  var _o2m_b_work_close = function () {
    if (window.o2android && window.o2android.closeWork) {
      window.o2android.closeWork("");
    } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.closeWork) {
      window.webkit.messageHandlers.closeWork.postMessage("");
    } else {
      console.log("请在O2OA移动端使用, workClose");
    }
  }
  /**
   * 关闭当前工作页面，<b>只能在工作表单中可以使用</b>
   * @method workClose
   * @o2membercategory biz
   * @static
   * @example
   * o2m.biz.workClose();
   */
  this.o2m.biz.workClose = _o2m_b_work_close;


  //o2m.biz.contact.departmentsPicker
  this.o2m.biz.contact.departmentsPickerSuccess = function (result) {
    console.log("biz contact departmentsPicker back, result:" + result);
  };
  var _o2m_b_contact_department_picker = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.biz.contact.departmentsPickerSuccess = onSuccess;
    }
    var topList = c && c.topList ? c.topList : [];
    var orgType = c && c.orgType ? c.orgType : "";
    var multiple = c && c.multiple ? c.multiple : false;
    var maxNumber = c && c.maxNumber ? c.maxNumber : 0;
    var pickedDepartments = c && c.pickedDepartments ? c.pickedDepartments : [];
    var body = {
      type: "contact.departmentPicker",
      callback: "o2m.biz.contact.departmentsPickerSuccess",
      data: {
        topList: topList,
        orgType: orgType,
        multiple: multiple,
        maxNumber: maxNumber,
        pickedDepartments: pickedDepartments,
      }
    };
    _biz_post(body, onFail);
  };
  /**
   * 通讯录选部门
   * @method departmentsPicker
   * @o2membercategory biz
   * @static
   * @param {Object} obj  departmentsPicker需要传入对象
   * <pre><code class='language-js'>{
   *  topList: [],//Array[String] 可选的顶级组织列表，不传或列表为空的时候，显示全部组织
   *  orgType: "",//String 可选择的组织类别。为空就是全部组织类型都可以
   *  multiple:true, //是否多选
   *  maxNumber: 0, //Int 当multiple为true的时候，最多可选择的部门数
   *  pickedDepartments:[],//Array[String] 已经选择的部门distinguishedName列表
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/biz_dept_choose.png">
   * </caption>
   * o2m.biz.contact.departmentsPicker({
   * topList: [],//不传或者空列表，显示全部组织
   * orgType: "",//可传空 只显示某种类型的组织
   * multiple:true, //是否多选
   * maxNumber: 0, //最大选择数量
   * pickedDepartments:[],//已选部门
   * onSuccess : function(result) {
   *     //返回结果样例
   *     {
   *       departments:[{
   *       "id":"xxxx",
   *       "name":"部门名称", 
   *       "unique":"xxxx",
   *       "distinguishedName":"部门@xxxx@U",
   *       "typeList":["xxxx"],
   *       "shortName":"xxxx",
   *       "level": 0,
   *       "levelName":"xxxx",
   *       }]
   *     }
   *   
   * },
   * onFail : function(err) {}
   * });
   */
  this.o2m.biz.contact.departmentsPicker = _o2m_b_contact_department_picker;


  //o2m.biz.contact.IdentityPicker
  this.o2m.biz.contact.IdentityPickerSuccess = function (result) {
    console.log("biz contact IdentityPicker back, result:" + result);
  };
  var _o2m_b_contact_identity_picker = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.biz.contact.IdentityPickerSuccess = onSuccess;
    }
    var topList = c && c.topList ? c.topList : [];
    var multiple = c && c.multiple ? c.multiple : false;
    var maxNumber = c && c.maxNumber ? c.maxNumber : 0;
    var pickedIdentities = c && c.pickedIdentities ? c.pickedIdentities : [];
    var duty = c && c.duty ? c.duty : [];
    var body = {
      type: "contact.identityPicker",
      callback: "o2m.biz.contact.IdentityPickerSuccess",
      data: {
        topList: topList,
        multiple: multiple,
        maxNumber: maxNumber,
        pickedIdentities: pickedIdentities,
        duty: duty,
      }
    };
    _biz_post(body, onFail);
  };
  /**
   * 通讯录选身份
   * @method IdentityPicker
   * @o2membercategory biz
   * @static
   * @param {Object} obj  IdentityPicker需要传入对象
   * <pre><code class='language-js'>{
   *  topList: [],//Array[String] 可选的顶级组织列表，不传或列表为空的时候，显示全部组织
   *  multiple:true, //Boolean 是否多选
   *  maxNumber: 0, //Int 当multiple为true的时候，最多可选择的身份数
   *  pickedIdentities:[],//Array[String] 已经选择的身份distinguishedName列表
   *  duty: [],//Array[String] 可选择的人员职责
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/biz_identity_choose.png">
   * </caption>
   * o2m.biz.contact.IdentityPicker({
   * topList: [],//不传或者空列表，显示全部组织
   * multiple:true, //是否多选
   * maxNumber: 0, //最大选择数量
   * pickedIdentities:[],//已选身份列表
   * duty: [],//人员职责
   * onSuccess : function(result) {
   *     //返回结果样例
   *     {
   *       identities:[{
   *         "id":"xxxx",
   *         "name":"姓名", 
   *         "distinguishedName":"姓名@xxxx@I",
   *         "person":"xxx", 
   *         "unique":"xxxx",
   *         "unit":"xxx",
   *         "unitName":"xxxx",
   *         "unitLevel": 0,
   *         "unitLevelName":"xxxx",
   *         "personName":"xxxx",
   *         "personUnique":"xxx",
   *         "personDn":"xxx"
   *         }]
   *     }
   *   
   * },
   * onFail : function(err) {}
   * });
   */
  this.o2m.biz.contact.IdentityPicker = _o2m_b_contact_identity_picker;



  //o2m.biz.contact.GroupPicker
  this.o2m.biz.contact.GroupPickerSuccess = function (result) {
    console.log("biz contact GroupPicker back, result:" + result);
  };
  var _o2m_b_contact_group_picker = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.biz.contact.GroupPickerSuccess = onSuccess;
    }
    var multiple = c && c.multiple ? c.multiple : false;
    var maxNumber = c && c.maxNumber ? c.maxNumber : 0;
    var pickedGroups = c && c.pickedGroups ? c.pickedGroups : [];
    var body = {
      type: "contact.groupPicker",
      callback: "o2m.biz.contact.GroupPickerSuccess",
      data: {
        multiple: multiple,
        maxNumber: maxNumber,
        pickedGroups: pickedGroups,
      }
    };
    _biz_post(body, onFail);
  };
  /**
   * 群组选择
   * @method GroupPicker
   * @o2membercategory biz
   * @static
   * @param {Object} obj  GroupPicker需要传入对象
   * <pre><code class='language-js'>{
   *  multiple:true, //Boolean 是否多选
   *  maxNumber: 0, //Int 当multiple为true的时候，最多可选择的身份数
   *  pickedGroups:[],//Array[String] 已经选择的群组distinguishedName列表
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/biz_group_choose.png">
   * </caption>
   * o2m.biz.contact.GroupPicker({
   * multiple:true, //是否多选
   * maxNumber: 0, //最大选择数量
   * pickedGroups:[],//已选群组列表
   * onSuccess : function(result) {
   *     //返回结果样例
   *     {
   *        groups:[{
   *           "id":"xxxx", 
   *           "name":"群组名称", 
   *           "distinguishedName":"群组名称@xxxx@G"
   *           "unique":"xxxx", 
   *           }]
   *     }
   *   
   * },
   * onFail : function(err) {}
   * });
   */
  this.o2m.biz.contact.GroupPicker = _o2m_b_contact_group_picker;


  //o2m.biz.contact.PersonPicker
  this.o2m.biz.contact.PersonPickerSuccess = function (result) {
    console.log("biz contact PersonPicker back, result:" + result);
  };
  var _o2m_b_contact_person_picker = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.biz.contact.PersonPickerSuccess = onSuccess;
    }
    var multiple = c && c.multiple ? c.multiple : false;
    var maxNumber = c && c.maxNumber ? c.maxNumber : 0;
    var pickedUsers = c && c.pickedUsers ? c.pickedUsers : [];
    var body = {
      type: "contact.personPicker",
      callback: "o2m.biz.contact.PersonPickerSuccess",
      data: {
        multiple: multiple,
        maxNumber: maxNumber,
        pickedUsers: pickedUsers,
      }
    };
    _biz_post(body, onFail);
  };
  /**
   * 人员选择
   * @method PersonPicker
   * @o2membercategory biz
   * @static
   * @param {Object} obj  PersonPicker需要传入对象
   * <pre><code class='language-js'>{
   *  multiple:true, //Boolean 是否多选
   *  maxNumber: 0, //Int 当multiple为true的时候，最多可选择的身份数
   *  pickedUsers:[],//Array[String] 已经选择的人员distinguishedName列表
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/biz_person_choose.png">
   * </caption>
   * o2m.biz.contact.PersonPicker({
   * multiple:true, //是否多选
   * maxNumber: 0, //最大选择数量
   * pickedUsers:[],//已选人员列表
   * onSuccess : function(result) {
   *     //返回结果样例
   *     {
   *         users:[{
   *           "id":"xxx", 
   *           "name":"姓名", 
   *           "unique":"xxx", 
   *           "distinguishedName":"姓名@xxxx@P"
   *           "genderType":"xxx", 
   *           "employee":"xxx", 
   *           "mail":"xxx", 
   *           "weixin":"xxx", 
   *           "qq":"xxx", 
   *           "mobile":"xxx", 
   *           "officePhone":"xxx"
   *           }]
   *     }
   *   
   * },
   * onFail : function(err) {}
   * });
   */
  this.o2m.biz.contact.PersonPicker = _o2m_b_contact_person_picker;


  //o2m.biz.contact.ComplexPicker
  this.o2m.biz.contact.ComplexPickerSuccess = function (result) {
    console.log("biz contact ComplexPicker back, result:" + result);
  };
  var _o2m_b_contact_complex_picker = function (c) {
    var onSuccess = c && c.onSuccess ? c.onSuccess : null;
    var onFail = c && c.onFail ? c.onFail : null;
    if (onSuccess && typeof onSuccess === "function") {
      o2m.biz.contact.ComplexPickerSuccess = onSuccess;
    }
    var pickMode = c && c.pickMode ? c.pickMode : [];
    var multiple = c && c.multiple ? c.multiple : false;
    var maxNumber = c && c.maxNumber ? c.maxNumber : 0;
    var topList = c && c.topList ? c.topList : [];
    var orgType = c && c.orgType ? c.orgType : "";
    var duty = c && c.duty ? c.duty : [];
    var pickedGroups = c && c.pickedGroups ? c.pickedGroups : [];
    var pickedUsers = c && c.pickedUsers ? c.pickedUsers : [];
    var pickedIdentities = c && c.pickedIdentities ? c.pickedIdentities : [];
    var pickedDepartments = c && c.pickedDepartments ? c.pickedDepartments : [];
    var body = {
      type: "contact.complexPicker",
      callback: "o2m.biz.contact.ComplexPickerSuccess",
      data: {
        pickMode: pickMode,
        multiple: multiple,
        maxNumber: maxNumber,
        topList: topList,
        orgType: orgType,
        duty: duty,
        pickedGroups: pickedGroups,
        pickedUsers: pickedUsers,
        pickedIdentities: pickedIdentities,
        pickedDepartments: pickedDepartments,
      }
    };
    _biz_post(body, onFail);
  };
  /**
   * 复合选择器，可配置选择多种数据
   * @method ComplexPicker
   * @o2membercategory biz
   * @static
   * @param {Object} obj  ComplexPicker需要传入对象
   * <pre><code class='language-js'>{
   *  pickMode: ["departmentPicker", "identityPicker"], //Array[String] 选择器类型，可传入值：departmentPicker、identityPicker、groupPicker、personPicker
   *  topList：Array[String] 可选的顶级组织列表，不传或列表为空的时候，显示全部组织
   *  duty: Array[String] 可选择的人员职责
   *  orgType：String 可选择的组织类别。为空就是全部组织类型都可以
   *  multiple:true, //Boolean 是否多选
   *  maxNumber: 0, //Int 当multiple为true的时候，最多可选择的身份数
   *  pickedDepartments：Array[String] 已经选择的部门distinguishedName列表
   *  pickedIdentities：Array[String] 已经选择的身份distinguishedName列表
   *  pickedGroups： Array[String] 已经选择的群组distinguishedName列表
   *  pickedUsers:[],//Array[String] 已经选择的人员distinguishedName列表
   *  "onSuccess": function,  //成功回调
   *  "onFail": function, //失败回调
   * }</code></pre>
   * @example
   * <caption>
   * 样例效果：<br/>
   * <img src="img/module/o2m/biz_complex_choose.png">
   * </caption>
   * o2m.biz.contact.ComplexPicker({
   * pickMode: ["departmentPicker", "identityPicker"], //选择器类型
   * topList: [],//不传或者空列表，显示全部组织
   * orgType: "",//可传空 只显示某种类型的组织
   * duty: [],//人员职责
   * multiple:true, //是否多选
   * maxNumber: 0, //最大选择数量
   * pickedDepartments:[],//已选部门
   * pickedIdentities:[],//已选身份列表
   * pickedGroups:[],//已选群组列表
   * pickedUsers:[],//已选人员列表
   * onSuccess : function(result) {
   *     //返回结果样例
   *     {
   *       departments:[{
   *       "id":"xxxx",
   *       "name":"部门名称", 
   *       "unique":"xxxx",
   *       "distinguishedName":"部门@xxxx@U",
   *       "typeList":["xxxx"],
   *       "shortName":"xxxx",
   *       "level": 0,
   *       "levelName":"xxxx",
   *       }],
   *       identities:[{
   *       "id":"xxxx",
   *       "name":"姓名", 
   *       "distinguishedName":"姓名@xxxx@I",
   *       "person":"xxx", 
   *       "unique":"xxxx",
   *       "unit":"xxx",
   *       "unitName":"xxxx",
   *       "unitLevel": 0,
   *       "unitLevelName":"xxxx",
   *       "personName":"xxxx",
   *       "personUnique":"xxx",
   *       "personDn":"xxx"
   *       }],
   *       groups:[{
   *       "id":"xxxx", 
   *       "name":"群组名称", 
   *       "distinguishedName":"群组名称@xxxx@G"
   *       "unique":"xxxx", 
   *       }],
   *       users:[{
   *       "id":"xxx", 
   *       "name":"姓名", 
   *       "unique":"xxx", 
   *       "distinguishedName":"姓名@xxxx@P"
   *       "genderType":"xxx", 
   *       "employee":"xxx", 
   *       "mail":"xxx", 
   *       "weixin":"xxx", 
   *       "qq":"xxx", 
   *       "mobile":"xxx", 
   *       "officePhone":"xxx"
   *       }]
   *     }
   * },
   * onFail : function(err) {}
   * });
   */
  this.o2m.biz.contact.ComplexPicker = _o2m_b_contact_complex_picker;



})();
