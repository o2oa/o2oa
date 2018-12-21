
jQuery.extend({


    createUploadIframe: function (id, uri) {//id为当前系统时间字符串，uri是外部传入的json对象的一个参数
        //create frame
        var frameId = 'jUploadFrame' + id; //给iframe添加一个独一无二的id
        var iframeHtml = '<iframe id="' + frameId + '" name="' + frameId + '" style="position:absolute; top:-9999px; left:-9999px"'; //创建iframe元素
        if (window.ActiveXObject) {//判断浏览器是否支持ActiveX控件
            if (typeof uri == 'boolean') {
                iframeHtml += ' src="' + 'javascript:false' + '"';

            }
            else if (typeof uri == 'string') {
                iframeHtml += ' src="' + uri + '"';
            }
        }
        iframeHtml += ' />';
        jQuery(iframeHtml).appendTo(document.body); //将动态iframe追加到body中

        return jQuery('#' + frameId).get(0); //返回iframe对象
    },
    createUploadForm: function (id, fileElementId, data) {//id为当前系统时间字符串，fileElementId为页面<input type='file' />的id，data的值需要根据传入json的键来决定
        //create form	
        var formId = 'jUploadForm' + id; //给form添加一个独一无二的id
        var fileId = 'jUploadFile' + id; //给<input type='file' />添加一个独一无二的id
        var form = jQuery('<form  action="" method="POST" name="' + formId + '" id="' + formId + '" enctype="multipart/form-data" ></form>'); //创建form元素
        if (data) {//通常为false
            for (var i in data) {
                jQuery('<input type="hidden" name="' + i + '" value="' + data[i] + '" />').appendTo(form); //根据data的内容，创建隐藏域，这部分我还不知道是什么时候用到。估计是传入json的时候，如果默认传一些参数的话要用到。
            }
        }
        var oldElement = jQuery('#' + fileElementId); //得到页面中的<input type='file' />对象
        var newElement = jQuery(oldElement).clone(); //克隆页面中的<input type='file' />对象
        jQuery(oldElement).attr('id', fileId); //修改原对象的id
        jQuery(oldElement).before(newElement); //在原对象前插入克隆对象
        jQuery(oldElement).appendTo(form); //把原对象插入到动态form的结尾处



        //set attributes
        jQuery(form).css('position', 'absolute'); //给动态form添加样式，使其浮动起来，
        jQuery(form).css('top', '-1200px');
        jQuery(form).css('left', '-1200px');
        jQuery(form).appendTo('body'); //把动态form插入到body中
        return form;
    },

    ajaxFileUpload: function (s) {//这里s是个json对象，传入一些ajax的参数
        // TODO introduce global settings, allowing the client to modify them for all requests, not only timeout		
        s = jQuery.extend({}, jQuery.ajaxSettings, s); //此时的s对象是由jQuery.ajaxSettings和原s对象扩展后的对象
        var id = new Date().getTime(); //取当前系统时间，目的是得到一个独一无二的数字
        var form = jQuery.createUploadForm(id, s.fileElementId, (typeof (s.data) == 'undefined' ? false : s.data)); //创建动态form
        var io = jQuery.createUploadIframe(id, s.secureuri); //创建动态iframe
        var frameId = 'jUploadFrame' + id; //动态iframe的id
        var formId = 'jUploadForm' + id; //动态form的id
        // Watch for a new set of requests
        if (s.global && !jQuery.active++) {//当jQuery开始一个ajax请求时发生
            jQuery.event.trigger("ajaxStart"); //触发ajaxStart方法
        }
        var requestDone = false; //请求完成标志
        // Create the request object
        var xml = {};
        if (s.global)
            jQuery.event.trigger("ajaxSend", [xml, s]); //触发ajaxSend方法
        // Wait for a response to come back
        var uploadCallback = function (isTimeout) {//回调函数
            var io = document.getElementById(frameId); //得到iframe对象
            try {
                if (io.contentWindow) {//动态iframe所在窗口对象是否存在
                    xml.responseText = io.contentWindow.document.body ? io.contentWindow.document.body.innerHTML : null;
                    xml.responseXML = io.contentWindow.document.XMLDocument ? io.contentWindow.document.XMLDocument : io.contentWindow.document;

                } else if (io.contentDocument) {//动态iframe的文档对象是否存在
                    xml.responseText = io.contentDocument.document.body ? io.contentDocument.document.body.innerHTML : null;
                    xml.responseXML = io.contentDocument.document.XMLDocument ? io.contentDocument.document.XMLDocument : io.contentDocument.document;
                }
            } catch (e) {
                jQuery.handleError(s, xml, null, e);
            }
            if (xml || isTimeout == "timeout") {//xml变量被赋值或者isTimeout == "timeout"都表示请求发出，并且有响应
                requestDone = true; //请求完成
                var status;
                try {
                    status = isTimeout != "timeout" ? "success" : "error"; //如果不是“超时”，表示请求成功
                    // Make sure that the request was successful or notmodified
                    if (status != "error") {
                        // process the data (runs the xml through httpData regardless of callback)
                        var data = jQuery.uploadHttpData(xml, s.dataType); //根据传送的type类型，返回json对象，此时返回的data就是后台操作后的返回结果
                        // If a local callback was specified, fire it and pass it the data
                        if (s.success)
                            s.success(data, status); //执行上传成功的操作

                        // Fire the global callback
                        if (s.global)
                            jQuery.event.trigger("ajaxSuccess", [xml, s]);
                    } else
                        jQuery.handleError(s, xml, status);
                } catch (e) {
                    status = "error";
                    jQuery.handleError(s, xml, status, e);
                }

                // The request was completed
                if (s.global)
                    jQuery.event.trigger("ajaxComplete", [xml, s]);

                // Handle the global AJAX counter
                if (s.global && ! --jQuery.active)
                    jQuery.event.trigger("ajaxStop");

                // Process result
                if (s.complete)
                    s.complete(xml, status);

                jQuery(io).unbind();//移除iframe的事件处理程序

                setTimeout(function () {//设置超时时间
                    try {
                        jQuery(io).remove();//移除动态iframe
                        jQuery(form).remove();//移除动态form

                    } catch (e) {
                        jQuery.handleError(s, xml, null, e);
                    }

                }, 100)

                xml = null

            }
        }
        // Timeout checker
        if (s.timeout > 0) {//超时检测
            setTimeout(function () {
                // Check to see if the request is still happening
                if (!requestDone) uploadCallback("timeout");//如果请求仍未完成，就发送超时信号
            }, s.timeout);
        }
        try {

            var form = jQuery('#' + formId);
            jQuery(form).attr('action', s.url);//传入的ajax页面导向url
            jQuery(form).attr('method', 'POST');//设置提交表单方式
            jQuery(form).attr('target', frameId);//返回的目标iframe，就是创建的动态iframe
            if (form.encoding) {//选择编码方式
                jQuery(form).attr('encoding', 'multipart/form-data');
            }
            else {
                jQuery(form).attr('enctype', 'multipart/form-data');
            }
            jQuery(form).submit();//提交form表单

        } catch (e) {
            jQuery.handleError(s, xml, null, e);
        }

        jQuery('#' + frameId).load(uploadCallback); //ajax 请求从服务器加载数据，同时传入回调函数
        return { abort: function () { } };

    },

    uploadHttpData: function (r, type) {
        var data = !type;
        data = type == "xml" || data ? r.responseXML : r.responseText;
        // If the type is "script", eval it in global context
        if (type == "script")
            jQuery.globalEval(data);
        // Get the JavaScript object, if JSON is used.
        if (type == "json")
            eval("data = " + data);
        // evaluate scripts within html
        if (type == "html")
            jQuery("<div>").html(data).evalScripts();

        return data;
    }
})

