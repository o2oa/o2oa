//MWF.require(["MWF.widget.Common", "MWF.widget.Identity", "MWF.widget.O2Identity"], null, false);
MWF.require(["MWF.widget.Common", "MWF.widget.O2Identity"], null, false);
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.xDesktop.requireApp("process.Xform", "lp." + MWF.language, null, false);
//MWF.xDesktop.requireApp("process.Xform", "Package", null, false);

/** @class Form 流程表单。
 * @o2cn 流程表单
 * @o2category FormComponents
 * @o2range {Process}
 * @example
 * //可以在脚本中获取表单
 * //方法1：
 * var form = this.form.getApp().appForm; //获取表单
 * //方法2
 * var form = this.target; //在表单本身的事件脚本中获取
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Form = MWF.APPForm = new Class(
    /** @lends MWF.xApplication.process.Xform.Form# */
{
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "readonly": false,
        "cssPath": "",
        "macro": "FormContext",
        "parameters": null,
        "moduleEvents": [
            /**
             * 表单加载前触发。数据（businessData）、预加载脚本和表单html已经就位。
             * @event MWF.xApplication.process.Xform.Form#queryLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "queryLoad",
            /**
             * 表单加载前触发。如果是流程表单，已提示抢办锁定。
             * @event MWF.xApplication.process.Xform.Form#beforeLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeLoad",
            /**
             * 表单的所有组件加载前触发，此时表单的样式和js head已经加载。
             * @event MWF.xApplication.process.Xform.Form#beforeModulesLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeModulesLoad",
            /**
             * 表单加载后触发。主表单的组件加载完成，但不保证子表单、子页面、部件加载完成。
             * @event MWF.xApplication.process.Xform.Form#postLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "postLoad",
            /**
             * 表单的所有组件加载后触发。表单包含有子表单、子页面、部件时，此事件会在这些组件加载后触发。
             * 如果包含异步加载的组件，如异步加载的下拉框选项等，会在这些组件加载完成后执行。
             * @event MWF.xApplication.process.Xform.Form#afterModulesLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterModulesLoad",
            /**
             * 表单加载后触发。表单包含有子表单、子页面、部件时，此事件会在这些组件加载后触发。
             * @event MWF.xApplication.process.Xform.Form#afterLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterLoad",
            /**
             * 保存前触发。如果是流程表单，流转前也会触发本事件。
             * @event MWF.xApplication.process.Xform.Form#beforeSave
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeSave",
            /**
             * 保存后触发。如果是流程表单，流转后也会触发本事件。
             * @event MWF.xApplication.process.Xform.Form#afterSave
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterSave",
            /**
             * 关闭前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeClose
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeClose",
            /**
             * 弹出提交界面前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeProcessWork
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeProcessWork",
            /**
             * 流转前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeProcess
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeProcess",
            /**
             * 流转后触发。
             * @event MWF.xApplication.process.Xform.Form#afterProcess
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterProcess",
            /**
             * 加载弹出的提交界面以后执行，this.event指向弹出界面对象。
             * @event MWF.xApplication.process.Xform.Form#afterLoadProcessor
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterLoadProcessor",
            /**
             * 关闭弹出的提交界面以后执行。
             * @event MWF.xApplication.process.Xform.Form#closeProcessor
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "closeProcessor",
            /**
             * 重置处理人前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeReset
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeReset",
            /**
             * 重置处理人后触发。
             * @event MWF.xApplication.process.Xform.Form#afterReset
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterReset",
            /**
             * 撤回前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeRetract
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeRetract",
            /**
             * 撤回后触发。
             * @event MWF.xApplication.process.Xform.Form#afterRetract
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterRetract",
            /**
             * 调度前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeReroute
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeReroute",
            /**
             * 调度后触发。
             * @event MWF.xApplication.process.Xform.Form#afterReroute
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterReroute",
            /**
             * 删除工作前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeDelete
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeDelete",
            /**
             * 删除工作后触发。
             * @event MWF.xApplication.process.Xform.Form#afterDelete
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterDelete",
            "resize",
            /**
             * 已阅前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeReaded
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeReaded",
            /**
             * 已阅后触发。
             * @event MWF.xApplication.process.Xform.Form#afterReaded
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterReaded",

            /**
             * 加签前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeAddTask
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeAddTask",

            /**
             * 加签后触发。
             * @event MWF.xApplication.process.Xform.Form#afterAddTask
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterAddTask",

            /**
             * 退回前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeGoBack
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeGoBack",

            /**
             * 退回后触发。
             * @event MWF.xApplication.process.Xform.Form#afterGoBack
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterGoBack",

            /**
             * 回溯前触发。
             * @event MWF.xApplication.process.Xform.Form#beforeRollback
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeRollback",

            /**
             * 回溯后触发。
             * @event MWF.xApplication.process.Xform.Form#afterRollback
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterRollback"
        ]
    },
    initialize: function (node, data, options) {
        this.setOptions(options);

        /**
         * @summary 表单容器
         * @see https://mootools.net/core/docs/1.6.0/Element/Element
         * @member {Element}
         * @example
         *  //可以在脚本中获取表单容器
         * var formContainer = this.form.getApp().appForm.container;
         */
        this.container = $(node);
        this.container.setStyle("-webkit-user-select", "text");
        if (Browser.firefox) this.container.setStyle("opacity", 0);

        this.data = data;
        //var jsonData = JSON.parse(data)

        /**
         * @summary 表单的配置信息，比如表单名称，提交方式等等.
         * @member {Object}
         * @example
         *  //可以在脚本中获取表单配置信息
         * var json = this.form.getApp().appForm.json; //表单配置信息
         * var name = json.name; //表单名称
         */
        this.json = data.json;
        this.html = data.html;

        this.path = "../x_component_process_Xform/$Form/";
        this.cssPath = this.options.cssPath || "../x_component_process_Xform/$Form/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.sectionListObj = {};

        /**
         * @summary 表单中的所有组件数组.
         * @member {Array}
         * @example
         * //下面的样例对表单组件进行循环，并且判断是输入类型的组件
         * var modules = this.form.getApp().appForm.modules; //获取所有表单组件
         * for( var i=0; i<modules.length; i++ ){ //循环处理组件
         *   //获取组件的类型
            var moduleName = module.json.moduleName;
            if( !moduleName ){
                moduleName = typeOf(module.json.type) === "string" ? module.json.type.toLowerCase() : "";
            }
            if( ["calendar","combox","number","textfield"].contains( moduleName )){ //输入类型框
                //do something
             }
         * }
         */
        this.modules = [];

        /**
         * 该对象的key是组件标识，value是组件对象，可以使用该对象根据组件标识获取组件。<br/>
         * 需要注意的是，在子表单中嵌入不绑定数据的组件（比如div,common,button等等），系统允许重名。<br/>
         * 在打开表单的时候，系统会根据重名情况，自动在组件的标识后跟上 "_1", "_2"。
         * @summary 表单中的所有组件对象.
         * @member {Object}
         * @example
         * var moduleAll = this.form.getApp().appForm.all; //获取组件对象
         * var subjectField = moduleAll["subject"] //获取名称为subject的组件
         */
        this.all = {};
        this.allForName = {};
        this.forms = {};

        //if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    parseCSS: function (css) {
        var rex = /(url\(.*\))/g;
        var match;
        while ((match = rex.exec(css)) !== null) {
            var pic = match[0];
            var len = pic.length;
            var s = pic.substring(pic.length - 2, pic.length - 1);
            var n0 = (s === "'" || s === "\"") ? 5 : 4;
            var n1 = (s === "'" || s === "\"") ? 2 : 1;
            pic = pic.substring(n0, pic.length - n1);

            if ((pic.indexOf("x_processplatform_assemble_surface") != -1 || pic.indexOf("x_portal_assemble_surface") != -1)) {
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (pic.indexOf("/x_processplatform_assemble_surface") !== -1) {
                    pic = pic.replace("/x_processplatform_assemble_surface", host1 + "/x_processplatform_assemble_surface");
                } else if (pic.indexOf("x_processplatform_assemble_surface") !== -1) {
                    pic = pic.replace("x_processplatform_assemble_surface", host1 + "/x_processplatform_assemble_surface");
                }
                if (pic.indexOf("/x_portal_assemble_surface") !== -1) {
                    pic = pic.replace("/x_portal_assemble_surface", host2 + "/x_portal_assemble_surface");
                } else if (pic.indexOf("x_portal_assemble_surface") !== -1) {
                    pic = pic.replace("x_portal_assemble_surface", host2 + "/x_portal_assemble_surface");
                }
                pic = o2.filterUrl(pic);
            }
            pic = "url('" + pic + "')";
            var len2 = pic.length;

            css = css.substring(0, match.index) + pic + css.substring(rex.lastIndex, css.length);
            rex.lastIndex = rex.lastIndex + (len2 - len);
        }
        return css;
    },
    loadCss: function () {
        var cssText = (this.json.css) ? this.json.css.code : "";
        //var head = (document.head || document.getElementsByTagName("head")[0] || document.documentElement);
        var styleNode = $("style" + this.json.id);
        //if (styleNode) styleNode.destroy();
        if (!styleNode && cssText) {

            //删除注释
            cssText = cssText.replace(/\/\*[\s\S]*?\*\/\n|([^:]|^)\/\/.*\n$/g, '').replace(/\\n/, '');

            cssText = this.parseCSS(cssText);

            var rex = new RegExp("(.+)(?=\\{)", "g");
            var match;
            var id = this.json.id.replace(/\-/g, "");
            var prefix = ".css" + id + " ";

            while ((match = rex.exec(cssText)) !== null) {
                var rulesStr = match[0];
                var startWith = rulesStr.substring(0, 1);
                if (startWith === "@" || startWith === ":" || rulesStr.indexOf("%") !== -1) {

                }else if (rulesStr.trim()==='from' || rulesStr.trim()==='to'){

                } else {
                    if (rulesStr.indexOf(",") != -1) {
                        //var rules = rulesStr.split(/\s*,\s*/g);
                        var rules = rulesStr.split(/,/g);
                        rules = rules.map(function (r) {
                            return prefix + r;
                        });
                        var rule = rules.join(",");
                        cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                        rex.lastIndex = rex.lastIndex + (prefix.length * rules.length);

                    } else {
                        var rule = prefix + match[0];
                        cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                        rex.lastIndex = rex.lastIndex + prefix.length;
                    }
                }
            }

            styleNode = document.createElement("style");
            styleNode.setAttribute("type", "text/css");
            styleNode.id = "style" + this.json.id;
            styleNode.inject(document.head, "bottom");

            if (styleNode.styleSheet) {
                var setFunc = function () {
                    styleNode.styleSheet.cssText = cssText;
                };
                if (styleNode.styleSheet.disabled) {
                    setTimeout(setFunc, 10);
                } else {
                    setFunc();
                }
            } else {
                var cssTextNode = document.createTextNode(cssText);
                styleNode.appendChild(cssTextNode);
            }
            return "css" + id;
        }else if( cssText ){
            return "css" + this.json.id.replace(/\-/g, "");
        }
        return "";
    },
    keyLock: function (async) {
        var lockData = null;
        var key = this.businessData.work.id + "-" + this.businessData.work.activityToken;
        o2.Actions.load("x_processplatform_assemble_surface").KeyLockAction.lock({ "key": key }, function (json) {
            flagData = json.data;
            if (async && flagData.success) this.keyLockTimeoutId = window.setTimeout(function () { this.keyLock(true) }.bind(this), 90000);
            if (async && !flagData.success) this.app.reload();
        }.bind(this), null, !!async);
        return flagData;
    },
    checkLock: function () {
        if (this.businessData.control.allowProcessing && this.businessData.activity && this.businessData.activity.manualMode == "grab") {
            this.app.addEvent("queryClose", function () {
                if (this.keyLockTimeoutId) window.clearTimeout(this.keyLockTimeoutId);
            }.bind(this));
            var lockData = this.keyLock();
            if (lockData.success) {
                this.keyLock(true);
            } else {
                this.businessData.control.allowFlow = false;
                this.businessData.control.allowProcessing = false;
                this.businessData.control.allowSave = false;
                this.businessData.control.allowReset = false;
                this.businessData.control.allowReroute = false;
                this.businessData.control.allowDelete = false;
                this.businessData.control.allowAddSplit = false;
                this.businessData.control.allowRetract = false;
                this.businessData.control.allowRollback = false;
                this.lockDataPerson = lockData.person;
                // var text = MWF.xApplication.process.Xform.LP.keyLockInfor;
                // text = text.replace("{name}", o2.name.cn(lockData.person));
                // var title = MWF.xApplication.process.Xform.LP.keyLockTitle;
                // this.app.alert("info", "center", title, text, 400, 160);

                // o2.DL.open({
                //     "title": title,
                //     "text": text,
                //     "width": 400
                // })
            }
        }
    },
    load: function (callback) {
        this.loadMacro(function () {
            debugger
            this.loadLanguage(function(flag){
                this.isParseLanguage = flag;
                if (flag && this.formDataText){
                    var data = o2.bindJson(this.formDataText,  {"lp": MWF.xApplication.process.Xform.LP.form});
                    this.data = JSON.parse(data);

                    this.json = this.data.json;
                    this.html = this.data.html;
                }
                this.checkLock();

                this.loadExtendStyle(function () {
                    if (this.app) {
                        if (this.app.formNode) this.app.formNode.setStyles(this.json.styles);
                        if (this.app.addEvent) {
                            this.app.addEvent("resize", function () {
                                this.fireEvent("resize");
                            }.bind(this));
                            this.app.addEvent("queryClose", function () {
                                this.beforeCloseWork();
                            }.bind(this));
                        }
                    }
                    if (!this.businessData.control.allowSave) this.setOptions({ "readonly": true });

                    var cssClass = "";
                    if (this.json.css && this.json.css.code) cssClass = this.loadCss();


                    //this.container.setStyle("opacity", 0);


                    this.container.set("html", this.html);
                    this.node = this.container.getFirst();
                    if (cssClass) this.node.addClass(cssClass);

                    this._loadEvents();

                    this.loadRelatedScript();
                    //this.loadResource( function () {
                    // this.loadDictionaryList(function () {

                    this.fireEvent("queryLoad");
                    if (this.event_resolve) {
                        this.event_resolve(function () {
                            this.loadForm(callback)
                        }.bind(this));
                    } else {
                        this.loadForm(callback);
                    }

                }.bind(this));



                // }.bind(this));

                //}.bind(this));

            }.bind(this));
        }.bind(this));
    },
    loadLanguage: function(callback){
        if (this.json.languageType!=="script" && this.json.languageType!=="default" && this.json.languageType!=="lib" && this.json.languageType!=="dict"){
            if (callback) callback();
            return true;
        }

        var language = MWF.xApplication.process.Xform.LP.form;
        var languageJson = null;
        var name = "lp-"+o2.language;
        var app;
        if (this.options.macro==="PageContext") {
            app = (this.app.portal && this.app.portal.id) ? this.app.portal.id : this.json.application;
        }else{
            app = (this.businessData.work || this.businessData.workCompleted).application;
        }

        if (this.json.languageType=="script"){
            if (this.json.languageScript && this.json.languageScript.code){
                languageJson = this.Macro.exec(this.json.languageScript.code, this);
            }
        }else if (this.json.languageType=="default") {
            var p1, p2;
            if (this.options.macro==="PageContext"){
                var portal = (this.app.portal && this.app.portal.id) ? this.app.portal.id : this.json.application;

                p1 = this.workAction.getDictRoot(name, portal, function(d){
                    return d.data;
                }, function(){
                    return true;
                });

                p2 = new Promise(function(resolve, reject){
                    this.workAction.getScriptByNameV2(portal, name, function(d){
                        if (d.data.text) {
                            resolve(this.Macro.exec(d.data.text, this));
                        }
                    }.bind(this), function(){reject("");});
                }.bind(this));
                languageJson = Promise.any([p1, p2]);
            }else{
                var application = (this.businessData.work || this.businessData.workCompleted).application;
                p1 = this.workAction.getDictRoot(name, application, function(d){
                    return d.data;
                }, function(){
                    return true;
                });
                p2 = new Promise(function(resolve, reject){
                    this.workAction.getScriptByNameV2(name, application, function(d){
                        if (d.data.text) {
                            resolve(this.Macro.exec(d.data.text, this));
                        }
                    }.bind(this), function(){reject("");});
                }.bind(this));
                languageJson = Promise.any([p1, p2]);
            }
        }else if (this.json.languageType=="lib") {
            var par1 = (this.options.macro==="PageContext") ? app : name;
            var par2 = (this.options.macro==="PageContext") ? name : app;
            languageJson = new Promise(function(resolve, reject){
                this.workAction.getScriptByNameV2(par1, par2, function(d){
                    if (d.data.text) {
                        resolve(this.Macro.exec(d.data.text, this));
                    }
                }.bind(this), function(){reject("");});
            }.bind(this));

        }else if (this.json.languageType=="dict") {
            languageJson = this.workAction.getDictRoot(name, app, function(d){
                return d.data;
            }, function(){
                return true;
            });
        }

        if (languageJson){
            if (languageJson.then && o2.typeOf(languageJson.then)=="function"){
                languageJson.then(function(json) {
                    if (!json.data){
                        var o = Object.clone(json);
                        json.data = o;
                    }
                    MWF.xApplication.process.Xform.LP.form = Object.merge(MWF.xApplication.process.Xform.LP.form, json);
                    if (callback) callback(true);
                }, function(){
                    if (callback) callback(true);
                })
            }else{
                MWF.xApplication.process.Xform.LP.form = Object.merge(MWF.xApplication.process.Xform.LP.form, languageJson);
                if (callback) callback(true);
            }
        }else{
            if (callback) callback(true);
        }

    },
    loadRelatedScript: function () {

        if (this.json.includeScripts && this.json.includeScripts.length) {
            var includeScriptText = "";
            var includedIds = [];
            this.json.includeScripts.each(function (s) {
                if (this.app.relatedScriptMap && this.app.relatedScriptMap[s.id]) {
                    includeScriptText += "\n" + this.app.relatedScriptMap[s.id].text;
                    includedIds.push(s.id);
                }
            }.bind(this));

            if (includeScriptText) this.Macro.exec(includeScriptText, this);
        }
    },
    loadForm: function (callback) {
        if (this.lockDataPerson) {
            var text = MWF.xApplication.process.Xform.LP.keyLockInfor;
            text = text.replace("{name}", o2.name.cn(this.lockDataPerson));
            var title = MWF.xApplication.process.Xform.LP.keyLockTitle;
            this.app.alert("info", "center", title, text, 400, 160);
        }

        if (this.app) if (this.app.fireEvent) this.app.fireEvent("queryLoad");
        this._loadBusinessData();
        this.fireEvent("beforeLoad");
        if (this.app) if (this.app.fireEvent) this.app.fireEvent("beforeLoad");
        this.loadContent(callback);
    },
    loadExtendStyle: function (callback) {
        if (!this.json.styleConfig || !this.json.styleConfig.extendFile) {
            if (callback) callback();
            return;
        }
        if (this.json["$version"] == "5.2") {
            if (callback) callback();
            return;
        }
        var stylesUrl = "../x_component_process_FormDesigner/Module/Form/skin/" + this.json.styleConfig.extendFile;
        MWF.getJSON(stylesUrl, {
                "onSuccess": function (responseJSON) {
                    if (responseJSON && responseJSON.form) {
                        this.json = Object.merge(this.json, responseJSON.form);
                    }
                    if (callback) callback();
                }.bind(this),
                "onRequestFailure": function () {
                    if (callback) callback();
                }.bind(this),
                "onError": function () {
                    if (callback) callback();
                }.bind(this)
            }
        );
    },
    loadMacro: function (callback) {
        //if (!MWF.Macro[this.options.macro || "FormContext"]){
        MWF.require("MWF.xScript.Macro", function () {
            this.Macro = new MWF.Macro[this.options.macro || "FormContext"](this);
            if (callback) callback();
        }.bind(this));
        // }else{
        //     this.Macro = new MWF.Macro[this.options.macro || "FormContext"](this);
        //     if (callback) callback();
        // }
    },
    loadContent: function (callback) {
        this.subformCount = 0;
        this.subformLoadedCount = 0;
        this.subformLoaded = [this.json.id];

        this.subpageCount = 0;
        this.subpageLoadedCount = 0;
        this.subpageLoaded = [];

        this.widgetCount = 0;
        this.widgetLoadedCount = 0;
        this.widgetLoaded = [];

        this._loadHtml();
        this._loadForm();
        this.fireEvent("beforeModulesLoad");
        if (this.app && this.app.fireEvent) this.app.fireEvent("beforeModulesLoad");
        this._loadModules(this.node);
        if (Browser.firefox) this.container.setStyle("opacity", 1);

        if (this.json.mode === "Mobile") {
            var node = document.body.getElement(".o2_form_mobile_actions");
            if (node) {
                node.empty();
                this._loadMobileActions(node, callback);
            } else {
                if (callback) callback();
                //console.log("没有找到移动端底部操作栏！")
            }
        } else {
            if (callback) callback();
        }

        this.fireEvent("postLoad");
        if (this.app && this.app.fireEvent) this.app.fireEvent("postLoad");
        this.checkSubformLoaded(true);
    },
    checkSubformLoaded: function (isAllSubformLoaded) {
        if (isAllSubformLoaded) {
            this.isAllSubformLoaded = true;
        }
        if (!this.isAllSubformLoaded) return;
        //console.log( "checkSubformLoaded this.subformCount="+ this.subformCount + " this.subformLoadedCount="+this.subformLoadedCount );
        if ((!this.subformCount || this.subformCount === this.subformLoadedCount) &&
            (!this.subpageCount || this.subpageCount === this.subpageLoadedCount) &&
            (!this.widgetCount || this.widgetCount === this.widgetLoadedCount)
        ) {
            //this.container.setStyle("opacity", 1);

            var moduleAgList = [];
            this.modules.each( function(module){
                if( module.moduleValueAG )moduleAgList.push( module.moduleValueAG );
                if( module.moduleSelectAG && module.moduleValueAG !== module.moduleSelectAG )moduleAgList.push(module.moduleSelectAG);
            });


            Promise.all( moduleAgList ).then(function () {
                this.fireEvent("afterModulesLoad");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterModulesLoad");

                this.fireEvent("afterLoad");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterLoad");
                this.isLoaded = true;
            }.bind(this));

        }
    },
    _loadMobileDefaultTools: function (callback) {
        if (this.json.multiTools) {
            if (callback) callback();
        }else if (this.json.defaultTools) {
            if (callback) callback();
        } else {
            this.json.defaultTools = o2.JSON.get("../x_component_process_FormDesigner/Module/Form/toolbars.json", function (json) {
                this.json.defaultTools = json;
                if (callback) callback();
            }.bind(this));
        }
    },

    _loadMobileActions: function (node, callback) {
        var tools = [];
        this._loadMobileDefaultTools(function () {
            var jsonStr;
            if( this.json.multiTools ){
                jsonStr = JSON.stringify(this.json.multiTools);
                jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.process.Xform.LP.form});
                this.multiToolsJson = JSON.parse(jsonStr);
                var json = Array.clone(this.multiToolsJson);
                json.each(function (tool) {
                    var flag;
                    if( tool.system ){
                        flag = this._checkDefaultMobileActionItem(tool, this.options.readonly);
                    }else{
                        flag = this._checkCustomMobileActionItem(tool, this.options.readonly)
                    }
                    if (flag) tools.push(tool);
                }.bind(this));
            }else{
                if (this.json.defaultTools) {
                    jsonStr = JSON.stringify(this.json.defaultTools);
                    jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.process.Xform.LP.form});
                    this.json.defaultTools = JSON.parse(jsonStr);
                    this.json.defaultTools.each(function (tool) {
                        var flag = this._checkDefaultMobileActionItem(tool, this.options.readonly);
                        if (flag) tools.push(tool);
                    }.bind(this));
                }
                if (this.json.tools) {
                    jsonStr = JSON.stringify(this.json.tools);
                    jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.process.Xform.LP.form});
                    this.json.tools = JSON.parse(jsonStr);
                    this.json.tools.each(function (tool) {
                        var flag = this._checkCustomMobileActionItem(tool, this.options.readonly);
                        if (flag) tools.push(tool);
                    }.bind(this));
                }
            }
            this.mobileTools = tools;
            if (tools.length <= 0) {
                if (node) node.hide();
            } else {
                // app上用原来的按钮样式
                if (window.o2android || window.flutter_inappwebview || (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.o2mLog)) {
                    if (node) this._createMobileActions(node, tools);
                } else {
                    if (node) this._createMobileActionsDingdingStyle(node, tools);
                }
            }
            if (callback) callback();
        }.bind(this));
    },
    // 钉钉 企业微信的按钮样式
    _createMobileActionsDingdingStyle: function (node, tools) {
        node.show();
        var count = tools.length;
        if (count <= 2) {
            //左边 间隔
            var dingdingSplitLeft = new Element("div", { "styles": this.css.html5ActionButtonDingdingSplit, "text": " " }).inject(node);
            var splitSize = dingdingSplitLeft.getSize();
            var size = document.body.getSize();
            var buttonWidth = (size.x - splitSize.x * (count + 1) - (count * 2)) / count;
            tools.each(function (tool) {
                var actionStyle = this.css.html5ActionButtonDingdingNormal;
                var classBg = "";
                debugger;
                if (tool.action === "processWork" || tool.action === "flowWork" || tool.action === "retractWork" || tool.id === "action_processWork" || tool.id === "action_retract" || tool.id === "action_flowWork") {
                    actionStyle = this.css.html5ActionButtonDingdingPrimary;
                    classBg = "mainColor_bg mainColor_border";
                } else if (tool.action === "deleteWork" || tool.id === "action_delete") {
                    actionStyle = this.css.html5ActionButtonDingdingDanger;
                }
                actionStyle.width = buttonWidth + "px";
                var action = new Element("div", { "styles": actionStyle, "class": classBg, "text": tool.text }).inject(node);
                if (tool.id && tool.id !== "") {
                    action.set("id", tool.id);
                }
                if( o2.typeOf(tool.properties) === "object" && Object.keys(tool.properties).length )action.set(tool.properties);
                action.store("tool", tool);
                action.addEvent("click", function (e) {
                    var clickFun = function () {
                        var t = e.target.retrieve("tool");
                        e.setDisable = function () { };
                        if (t.actionScript) {
                            this._runCustomAction(t.actionScript);
                        } else {
                            if (this[t.action]) this[t.action](e);
                        }
                    }.bind(this);
                    if (tool.action === "processWork" || tool.id === "action_processWork" || tool.action === "flowWork" || tool.id === "action_flowWork") {
                        //输入法激活的时候，需要一段时间等待输入法关闭
                        window.setTimeout(clickFun, 100)
                    } else {
                        clickFun();
                    }
                }.bind(this));
                new Element("div", { "styles": this.css.html5ActionButtonDingdingSplit, "text": " " }).inject(node);
            }.bind(this));
        } else {
            //左边 间隔
            var dingdingSplitLeft = new Element("div", { "styles": this.css.html5ActionButtonDingdingSplit, "text": " " }).inject(node);
            var splitSize = dingdingSplitLeft.getSize();
            var size = document.body.getSize();
            var buttonWidth = (size.x - splitSize.x * 4 - 6) / 5;
            for (var i = 0; i < 3; i++) {
                tool = tools[i];
                var actionStyle = this.css.html5ActionButtonDingdingNormal;
                var classBg = "";
                if (tool.action === "processWork" || tool.action === "flowWork" || tool.action === "retractWork") {
                    actionStyle = this.css.html5ActionButtonDingdingPrimary;
                    classBg = "mainColor_bg mainColor_border";
                } else if (tool.action === "deleteWork") {
                    actionStyle = this.css.html5ActionButtonDingdingDanger;
                }
                if (i == 2) {
                    this.css.html5ActionButtonDingdingMore.width = buttonWidth + "px";
                    var action = new Element("div", { "styles": this.css.html5ActionButtonDingdingMore, "text": "…"}).inject(node);
                    action.addEvent("click", function (e) {
                        this._loadMoreMobileActionsDingdingStyle(tools, 2, node);
                    }.bind(this));
                } else {
                    actionStyle.width = (buttonWidth * 2) + "px";
                    var action = new Element("div", { "styles": actionStyle, "class": classBg, "text": tool.text }).inject(node);
                    if (tool.id && tool.id !== "") {
                        action.set("id", tool.id);
                    }
                    if( o2.typeOf(tool.properties) === "object" && Object.keys(tool.properties).length )action.set(tool.properties);
                    action.store("tool", tool);
                    action.addEvent("click", function (e) {
                        var t = e.target.retrieve("tool");
                        e.setDisable = function () { }
                        if (t.actionScript) {
                            this._runCustomAction(t.actionScript);
                        } else {
                            if (this[t.action]) this[t.action](e);
                        }
                    }.bind(this));
                }
                new Element("div", { "styles": this.css.html5ActionButtonDingdingSplit, "text": " " }).inject(node);
            }
        }
    },
    _loadMoreMobileActionsDingdingStyle: function (tools, n, node) {
        document.body.mask({
            "style": {
                "background-color": "#cccccc",
                "opacity": 0.6
            },
            "hideOnClick": true,
            "onHide": function () {
                this.actionMoreArea.setStyle("display", "none");
            }.bind(this)
        });
        if (this.actionMoreArea) {
            this.actionMoreArea.setStyle("display", "block");
        } else {
            var size = document.body.getSize();
            this.actionMoreArea = new Element("div", { "styles": this.css.html5ActionOtherArea }).inject(document.body);
            var pl = this.actionMoreArea.getStyle("padding-left").toInt();
            var pr = this.actionMoreArea.getStyle("padding-right").toInt();
            var w = size.x - pl - pr;
            this.actionMoreArea.setStyle("width", "" + w + "px");
            for (var i = n; i < tools.length; i++) {
                tool = tools[i];
                var actionStyle = this.css.html5ActionButtonDingdingNormal;
                if (tool.action === "processWork" || tool.action === "flowWork" || tool.action === "retractWork") {
                    actionStyle = this.css.html5ActionButtonDingdingPrimary;
                } else if (tool.action === "deleteWork") {
                    actionStyle = this.css.html5ActionButtonDingdingDanger;
                }
                actionStyle.width = "100%";
                var action = new Element("div", { "styles": actionStyle, "text": tool.text }).inject(this.actionMoreArea);
                if (tool.id && tool.id !== "") {
                    action.set("id", tool.id);
                }
                if( o2.typeOf(tool.properties) === "object" && Object.keys(tool.properties).length )action.set(tool.properties);
                action.store("tool", tool);
                action.addEvent("click", function (e) {
                    var t = e.target.retrieve("tool");
                    e.setDisable = function () { }
                    if (t.actionScript) {
                        this._runCustomAction(t.actionScript);
                    } else {
                        if (this[t.action]) this[t.action](e);
                    }
                    // 关闭
                    if (this.actionMoreArea) {
                        this.actionMoreArea.setStyle("display", "none");
                        document.body.unmask();
                    }
                }.bind(this));
            }
        }
    },

    _createMobileActions: function (node, tools) {
        node.show();
        var count = tools.length;
        if (count <= 2) {
            this.css.html5ActionButton.width = "100%";
            if (count == 2) this.css.html5ActionButton.width = "49%";
            tools.each(function (tool) {
                var action = new Element("div", { "styles": this.css.html5ActionButton, "class": "mainColor_color", "text": tool.text }).inject(node);
                if (tool.id && tool.id !== "") {
                    action.set("id", tool.id);
                }
                if( o2.typeOf(tool.properties) === "object" && Object.keys(tool.properties).length )action.set(tool.properties);
                action.store("tool", tool);
                action.addEvent("click", function (e) {
                    var t = e.target.retrieve("tool");
                    e.setDisable = function () { }
                    if (t.actionScript) {
                        this._runCustomAction(t.actionScript);
                    } else {
                        if (this[t.action]) this[t.action](e);
                    }
                }.bind(this));
                this._setMobileBottonStyle(action);
            }.bind(this));
            if (count == 2) new Element("div", { "styles": this.css.html5ActionButtonSplit }).inject(node.getLast(), "before");
        } else {
            this.css.html5ActionButton.width = "38%"
            for (var i = 0; i < 2; i++) {
                tool = tools[i];
                var action = new Element("div", { "styles": this.css.html5ActionButton, "class": "mainColor_color", "text": tool.text }).inject(node);
                if (tool.id && tool.id !== "") {
                    action.set("id", tool.id);
                }
                if( o2.typeOf(tool.properties) === "object" && Object.keys(tool.properties).length )action.set(tool.properties);
                action.store("tool", tool);
                action.addEvent("click", function (e) {
                    var t = e.target.retrieve("tool");
                    e.setDisable = function () { }
                    if (t.actionScript) {
                        this._runCustomAction(t.actionScript);
                    } else {
                        if (this[t.action]) this[t.action](e);
                    }
                }.bind(this));
                this._setMobileBottonStyle(action);
            }
            new Element("div", { "styles": this.css.html5ActionButtonSplit }).inject(node.getLast(), "before");
            new Element("div", { "styles": this.css.html5ActionButtonSplit }).inject(node);
            this.css.html5ActionButton.width = "23%"
            var action = new Element("div", { "styles": this.css.html5ActionButton, "class": "mainColor_color", "text": "…" }).inject(node);
            action.addEvent("click", function (e) {
                this._loadMoreMobileActions(tools, 2, node);
            }.bind(this));
            this._setMobileBottonStyle(action);
        }
    },
    _loadMoreMobileActions: function (tools, n, node) {
        document.body.mask({
            "style": {
                "background-color": "#cccccc",
                "opacity": 0.6
            },
            "hideOnClick": true,
            "onHide": function () {
                this.actionMoreArea.setStyle("display", "none");
            }.bind(this)
        });
        if (this.actionMoreArea) {
            this.actionMoreArea.setStyle("display", "block");
        } else {

            var size = document.body.getSize();
            this.actionMoreArea = new Element("div", { "styles": this.css.html5ActionOtherArea }).inject(document.body);
            var pl = this.actionMoreArea.getStyle("padding-left").toInt();
            var pr = this.actionMoreArea.getStyle("padding-right").toInt();
            var w = size.x - pl - pr;
            this.actionMoreArea.setStyle("width", "" + w + "px");
            for (var i = n; i < tools.length; i++) {
                tool = tools[i];
                var action = new Element("div", { "styles": this.css.html5ActionOtherButton, "class": "mainColor_color", "text": tool.text }).inject(this.actionMoreArea);
                if (tool.id && tool.id !== "") {
                    action.set("id", tool.id);
                }
                if( o2.typeOf(tool.properties) === "object" && Object.keys(tool.properties).length )action.set(tool.properties);
                action.store("tool", tool);
                action.addEvent("click", function (e) {
                    var t = e.target.retrieve("tool");
                    e.setDisable = function () { }
                    if (t.actionScript) {
                        this._runCustomAction(t.actionScript);
                    } else {
                        if (this[t.action]) this[t.action](e);
                    }
                     // 关闭
                     if (this.actionMoreArea) {
                        this.actionMoreArea.setStyle("display", "none");
                        document.body.unmask();
                    }
                }.bind(this));
                this._setMobileBottonStyle(action);
            }
        }

        // actionArea.position({
        //     relativeTo: node,
        //     position: 'topCenter',
        //     edge: 'bottomCenter'
        // });
    },
    _setMobileBottonStyle: function (action) {
        var _self = this;
        action.addEvents({
            "mouseover": function (e) { this.setStyles(_self.css.html5ActionButton_over) },
            "mouseout": function (e) { this.setStyles(_self.css.html5ActionButton_up) },
            "mousedown": function (e) { this.setStyles(_self.css.html5ActionButton_over) },
            "mouseup": function (e) { this.setStyles(_self.css.html5ActionButton_up) },
            "touchstart": function (e) { this.setStyles(_self.css.html5ActionButton_over) },
            "touchcancel": function (e) { this.setStyles(_self.css.html5ActionButton_up) },
            "touchend": function (e) { this.setStyles(_self.css.html5ActionButton_up) },
            "touchmove": function (e) { this.setStyles(_self.css.html5ActionButton_over) }
        });
    },
    _runCustomAction: function (actionScript) {
        //var script = bt.node.retrieve("script");
        this.Macro.exec(actionScript, this);
    },
    _checkCustomMobileActionItem: function (tool, readonly) {
        var flag = true;
        if (readonly) {
            flag = tool.readShow;
        } else {
            flag = tool.editShow;
        }
        if (flag) {
            flag = true;
            if (tool.control) {
                flag = this.form.businessData.control[tool.control]
            }
            if (tool.condition) {
                var hideFlag = this.Macro.exec(tool.condition, this);
                flag = !hideFlag;
            }
        }
        return flag;
    },
    _checkDefaultMobileActionItem: function (tool, readonly, noCondition) {
        var flag = true;
        if (tool.control) {
            flag = this.businessData.control[tool.control]
        }
        if (!noCondition) if (tool.condition) {
            var hideFlag = this.Macro.exec(tool.condition, this);
            flag = flag && (!hideFlag);
        }
        if (tool.id == "action_processWork" || tool.id == "action_flowWork") {
            if (this.businessData.work.startTime) { // 正常模式
                if (!this.businessData.task || !this.businessData.work || !this.businessData.work.startTime) {
                    flag = false;
                }
            } else { // 草稿模式
                if (!this.businessData.work) {
                    flag = false;
                }
            }
        }
        if (tool.id == "action_rollback") tool.read = true;
        if (readonly) if (!tool.read) flag = false;
        return flag;
    },
    _loadBusinessData: function () {
        if (!this.businessData) {
            this.businessData = {};
            // this.businessData = {
            //     "data": {
            //         "select": "222",
            //         "radio": "bbb",
            //         "checkbox": ["check1", "check3"],
            //         "orderData": [
            //             {
            //                 "orderName": {"namefield": "电脑"},
            //                 "orderCount": {"countField": "3"},
            //                 "priceCount": {"priceField": "9000"}
            //             },
            //             {
            //                 "orderName": {"namefield": "路由器"},
            //                 "orderCount": {"countField": "2"},
            //                 "priceCount": {"priceField": "1000"}
            //             },
            //             {
            //                 "orderName": {"namefield": "网线"},
            //                 "orderCount": {"countField": "10"},
            //                 "priceCount": {"priceField": "200"}
            //             }
            //         ]
            //
            //     }
            // };
        }
    },

    _loadHtml: function () {
        // this.container.set("html", this.html);
        // this.node = this.container.getFirst();
        //this.node.setStyle("overflow", "hidden");
        this.node.addEvent("selectstart", function (e) {
            var select = "text";
            if (e.target.getStyle("-webkit-user-select")) {
                select = e.target.getStyle("-webkit-user-select").toString().toLowerCase();
            }

            if (select !== "text" && select !== "auto") e.preventDefault();
        });
    },

    _loadForm: function () {
        this._loadStyles();
        this._loadCssLinks();
        this._loadScriptSrc();
        this._loadJsheader();
        //this._loadEvents();
    },
    _loadStyles: function () {
        if (this.json.styles) Object.each(this.json.styles, function (value, key) {
            if ((value.indexOf("x_processplatform_assemble_surface") != -1 || value.indexOf("x_portal_assemble_surface") != -1)) {
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (value.indexOf("/x_processplatform_assemble_surface") !== -1) {
                    value = value.replace("/x_processplatform_assemble_surface", host1 + "/x_processplatform_assemble_surface");
                } else if (value.indexOf("x_processplatform_assemble_surface") !== -1) {
                    value = value.replace("x_processplatform_assemble_surface", host1 + "/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface") !== -1) {
                    value = value.replace("/x_portal_assemble_surface", host2 + "/x_portal_assemble_surface");
                } else if (value.indexOf("x_portal_assemble_surface") !== -1) {
                    value = value.replace("x_portal_assemble_surface", host2 + "/x_portal_assemble_surface");
                }
            }
            value = o2.filterUrl(value);
            this.node.setStyle(key, value);
        }.bind(this));
        //this.node.setStyles(this.json.styles);
    },
    _loadCssLinks: function () {
        var urls = this.json.cssLinks;
        urls.each(function (url) {
            new Element("link", {
                "rel": "stylesheet",
                "type": "text/css",
                "href": url
            }).inject($(document.head));
        });
    },
    _loadScriptSrc: function () {
        var urls = this.json.scriptSrc;
        urls.each(function (url) {
            new Element("script", {
                "src": url
            }).inject($(document.head));
        });
    },
    _loadJsheader: function () {
        var code = (this.json.jsheader) ? this.json.jsheader.code : "";
        if (code) Browser.exec(code);
    },
    _loadEvents: function () {
        Object.each(this.json.events, function (e, key) {
            if (e.code) {
                if (this.options.moduleEvents.indexOf(key) !== -1) {
                    this.addEvent(key, function (event) {
                        return this.Macro.fire(e.code, this, event);
                    }.bind(this));
                } else {
                    if (key === "load") {
                        this.addEvent("postLoad", function () {
                            return this.Macro.fire(e.code, this);
                        }.bind(this));
                    } else if (key === "submit") {
                        this.addEvent("beforeProcess", function () {
                            return this.Macro.fire(e.code, this);
                        }.bind(this));
                    } else {
                        this.node.addEvent(key, function (event) {
                            return this.Macro.fire(e.code, this, event);
                        }.bind(this));
                    }
                }
            }
        }.bind(this));
    },
    addModuleEvent: function (key, fun) {
        if (this.options.moduleEvents.indexOf(key) !== -1) {
            this.addEvent(key, function (event) {
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        } else {
            if (key === "load") {
                this.addEvent("postLoad", function (event) {
                    return (fun) ? fun(this, event) : null;
                }.bind(this));
            } else if (key === "submit") {
                this.addEvent("beforeProcess", function (event) {
                    return (fun) ? fun(this, event) : null;
                }.bind(this));
            } else {
                this.node.addEvent(key, function (event) {
                    return (fun) ? fun(this, event) : null;
                }.bind(this));
            }
        }
    },

    _getDomjson: function (dom) {
        var mwfType = dom.get("MWFtype") || dom.get("mwftype");
        switch (mwfType) {
            case "form":
                return this.json;
            case "":
                return null;
            default:
                var id = dom.get("id");
                if (!id) id = dom.get("MWFId");
                if (id) {
                    return this.json.moduleList[id];
                } else {
                    return null;
                }
        }
    },
    _getModuleNodes: function (dom, dollarFlag ) {
        var moduleNodes = [];
        var subDom = dom.getFirst();
        while (subDom) {
            var mwftype = subDom.get("MWFtype") || subDom.get("mwftype");
            if (mwftype) {
                var type = mwftype;
                if (type.indexOf("$") === -1 || dollarFlag===true) {
                    moduleNodes.push(subDom);
                }
                // && mwftype !== "tab$Content"
                if (mwftype !== "datagrid" && mwftype !== "datatable" && mwftype !== "subSource" && mwftype !== "tab$Content" && mwftype !== "datatemplate") {
                    moduleNodes = moduleNodes.concat(this._getModuleNodes(subDom, dollarFlag));
                }
            } else {
                moduleNodes = moduleNodes.concat(this._getModuleNodes(subDom, dollarFlag));
            }
            subDom = subDom.getNext();
        }
        return moduleNodes;
    },

    _loadModules: function (dom, beforeLoadModule, replace, callback) {
        var moduleNodes = this._getModuleNodes(dom);
        var modules = [], jsons = [];

        moduleNodes.each(function (node) {
            var json = this._getDomjson(node);
            jsons.push( json );

            var module = this._loadModule(json, node, beforeLoadModule, replace);
            this.modules.push(module);
            modules.push( module );
        }.bind(this));
        if( callback )callback( moduleNodes, jsons, modules )
    },
    _loadModule: function (json, node, beforeLoad, replace) {
        if( !json )return null;
        //console.log( json.id );
        if (json.type === "Subform" || json.moduleName === "subform") this.subformCount++;
        //if( json.type === "Subform" || json.moduleName === "subform" ){
        //    console.log( "add subformcount ， this.subformCount = " + this.subformCount );
        //}
        if (json.type === "Subpage" || json.moduleName === "subpage") this.subpageCount++;
        if (json.type === "Widget" || json.moduleName === "widget") this.widgetCount++;
        if (!MWF["APP" + json.type]) {
            var moduleType = json.type;
            if(moduleType === "AttachmentDg")moduleType = "Attachment";
            MWF.xDesktop.requireApp("process.Xform", moduleType, null, false);
        }
        var module = new MWF["APP" + json.type](node, json, this);
        if (beforeLoad) beforeLoad.apply(module);
        if (replace || !this.all[json.id]) this.all[json.id] = module;

        if (json.name) {
            if (this.allForName[json.name]) {
                var item = this.allForName[json.name];
                typeOf(item) === "array" ? item.push(module) : this.allForName[json.name] = [item, module];
            } else {
                this.allForName[json.name] = module;
            }
        }

        if (module.field) {
            if (replace || !this.forms[json.id]) this.forms[json.id] = module;
        }
        module.readonly = this.options.readonly;
        module.load();
        return module;
    },
    saveOpinion: function (module) {
        var op = module._getBusinessSectionDataByPerson();
        MWF.UD.getDataJson("userOpinion", function (json) {
            if (!json) json = [];
            var idx = json.indexOf(op);
            if (idx == -1) {
                if (json.length >= 50) json.shift();

            } else {
                json.splice(idx, 1);
            }
            json.push(op);
            MWF.UD.putData("userOpinion", json);
        }.bind(this), false);
    },
    loadPathData: function (path) {
        var data = null;
        this.workAction.getJobDataByPath(this.businessData.work.job, path, function (json) {
            data = json.data || null;
        }, null, false);
        return data;
    },
    /**
     * @summary 获取表单的所有数据.
     * @example
     * var data = this.form.getApp().appForm.getData();
     * @return {Object}
     */
    getData: function (issubmit) {
        //var data = Object.clone(this.businessData.data);
        var data = this.businessData.data;
        Object.each(this.forms, function (module, id) {

            //对id类似于 xx..0..xx 的字段 不处理
            if( id.indexOf("..") > 0 )return;

            if (module.json.type === "Opinion") {

                if (issubmit) {
                    this.saveOpinion(module);

                    var key = layout.desktop.session.user.id;
                    if (typeOf(data[id]) === "object" && typeOf(data[id][key]) === "string") {
                        data[id][key] = "";
                    } else if (typeOf(data[id]) === "string") {
                        data[id] = "";
                    }
                    // delete data[id];
                } else {
                    var v = module.getData();
                    // var d = this.loadPathData(id);
                    // if (d) data[id] = d;
                    data[id] = this.getSectionDataByPerson(v, data[id]);
                }
            } else {
                if (module.json.section === "yes") {
                    //     var d = this.loadPathData(id);
                    //     if (d) data[id] = d;
                    var v = this.getSectionData(module, data[id]);
                    //if (o2.typeOf(v)==="string") v = o2.txt(v);
                    data[id] = v
                } else {
                    if (module.fieldModuleLoaded!==false && module.readonly!==true){
                        var v = module.getData();
                        //if (o2.typeOf(v)==="string") v = o2.txt(v);
                        data[id] = v;
                    }
                }
            }
        }.bind(this));
        this.fireEvent("getData", [data]);
        this.businessData.data = data;
        this.Macro.environment.setData(this.businessData.data);
        return data;
    },
    getSectionData: function (module, obj) {
        var v = module.getData();
        switch (module.json.sectionBy) {
            case "person":
                return this.getSectionDataByPerson(v, obj);
            case "unit":
                return this.getSectionDataByUnit(v, obj);
            case "activity":
                return this.getSectionDataByPActivity(v, obj);
            case "splitValue":
                return this.getSectionDataBySplitValue(v, obj);
            case "script":
                return this.getSectionDataByScript(module.json.sectionByScript.code, v, obj);
            default:
                return v;
        }
    },
    getSectionDataByPerson: function (v, obj) {
        var key = layout.desktop.session.user.id;
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        obj[key] = v;
        return obj;
    },
    getSectionDataByUnit: function (v, obj) {
        var key = (this.businessData.task) ? this.businessData.task.unit : "";
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        if (key) obj[key] = v;
        return obj;
    },
    getSectionDataByPActivity: function (v, obj) {
        var key = (this.businessData.work) ? this.businessData.work.activity : "";
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        if (key) obj[key] = v;
        return obj;
    },
    getSectionDataBySplitValue: function (v, obj) {
        var key = (this.businessData.work) ? this.businessData.work.splitValue : "";
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        if (key) obj[key] = v;
        return obj;
    },

    getSectionDataByScript: function (code, v, obj) {
        var key = this.Macro.exec(code, this);
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        if (key) obj[key] = v;
        return obj;
    },

    setSection: function (json, data) {
        var obj = data[json.name];
        switch (json.sectionBy) {
            case "person":
                return this.setSectionByPerson(obj, json.name);
            case "unit":
                return this.setSectionByUnit(obj, json.name);
            case "activity":
                return this.setSectionByPActivity(obj, json.name);
            case "splitValue":
                return this.setSectionBySplitValue(obj, json.name);
            case "script":
                return this.setSectionByScript(json.sectionByScript.code, obj, json.name);
            default:
                return v;
        }
    },
    setSectionByPerson: function (obj, name) {
        var key = layout.desktop.session.user.id;
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        //obj[key] = v;
        this.sectionListObj[name] = key;
        return obj;
    },
    setSectionByUnit: function (obj, name) {
        var key = (this.businessData.task) ? this.businessData.task.unit : "";
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        this.sectionListObj[name] = key || "";
        //if (key) obj[key] = v;
        return obj;
    },
    setSectionByPActivity: function (obj, name) {
        var key = (this.businessData.work) ? this.businessData.work.activity : "";
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        this.sectionListObj[name] = key || "";
        //if (key) obj[key] = v;
        return obj;
    },
    setSectionBySplitValue: function (obj, name) {
        var key = (this.businessData.work) ? this.businessData.work.splitValue : "";
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        this.sectionListObj[name] = key || "";
        //if (key) obj[key] = v;
        return obj;
    },

    setSectionByScript: function (code, obj, name) {
        var key = this.Macro.exec(code, this);
        if (!obj || (typeOf(obj) !== "object")) obj = {};
        this.sectionListObj[name] = key || "";
        //if (key) obj[key] = v;
        return obj;
    },
    saveWork: function (callback, silent) {

        if( this.disallowSaving )return;

        if (this.businessData.control["allowSave"]) {

            if (!this.formSaveValidation()) {
                if (callback) callback();
                return false;
            }

            this.fireEvent("beforeSave");
            this.fireEvent("beforeSaveWork");

            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeSave");
            this.saveFormData(function (json) {
                if (this.app && !silent) this.app.notice(MWF.xApplication.process.Xform.LP.dataSaved, "success");
                if (callback && typeOf(callback) === "function") callback(json);
                this.fireEvent("afterSave");
                this.fireEvent("afterSaveWork");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterSave");
            }.bind(this));

        } else {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            //if (failure) failure(null, "Permission Denied", "");
        }
    },

    getSectionList: function () {
        return Object.keys(this.sectionListObj).map(function (p) {
            var o = { "path": p };
            if (this.sectionListObj[p]) o.key = this.sectionListObj[p];
            return o;
        }.bind(this));
    },


    setModifedDataByPathList: function (data, pathList) {
        var d = this.modifedData;
        for (var i = 0; i < pathList.length; i++) {
            if (i === pathList.length - 1) {
                d[pathList[i]] = data;
            } else {
                if (typeOf(d[pathList[i]]) === "object" || typeOf(d[pathList[i]]) === "array") {
                    d = d[pathList[i]]
                } else if (typeOf(pathList[i]) === "number") {
                    d = d[pathList[i]] = [];
                } else {
                    d = d[pathList[i]] = {};
                }
            }
        }
    },
    getOrigianlPathData: function (pathList) {
        var d = this.businessData.originalData;
        for (var i = 0; i < pathList.length; i++) {
            if (i === pathList.length - 1) {
                d = d[pathList[i]];
            } else {
                if (typeOf(d[pathList[i]]) === "object" || typeOf(d[pathList[i]]) === "array") {
                    d = d[pathList[i]];
                } else {
                    return null;
                }
            }
        }
        return d;
    },
    setModifedData: function (data, pathList) {
        pathList = pathList || [];
        if (typeOf(data) === "object") {
            for (var key in data) {
                //if (key.substring(0,2)!=="__"){
                var pList = Array.clone(pathList);
                pList.push(key);
                this.setModifedData(data[key], pList);
                //}
            }
        } else if (typeOf(data) === "array") {
            var od = this.getOrigianlPathData(pathList);
            // if (typeOf(od) !== "array" || od.length !== data.length || JSON.stringify(od) !== JSON.stringify(data)) {
            if (typeOf(od) !== "array" || od.length !== data.length || !this.compareObjects( od, data ) ) {
                this.setModifedDataByPathList(data, pathList);
            }
            //}else{
            //    for( var i=0; i<data.length; i++ ){
            //        this.setModifedData(data[i], pathList.push(i));
            //    }
            //}
        } else if (typeOf(data) !== "null") { //后台对null是忽略处理的，认为值没有变化
            var od = this.getOrigianlPathData(pathList);
            if (typeOf(data) !== typeOf(od) || data !== od) {
                this.setModifedDataByPathList(data, pathList);
            }
        }
    },
    compareObjects: function(o, p, deep){
        if( !deep )deep = 0;
        if( deep > 15 )return false; //最大层数，避免相互嵌套
        var type1 = typeOf( o ), type2 = typeOf( p );
        if( type1 !== type2 )return false;

        if( type1 === "object" ){
            for( var k in o ){
                if( o[k] === null || o[k] === undefined )delete o[k]
            }
            for( var k in p ){
                if( p[k] === null || p[k] === undefined )delete p[k]
            }
        }
        switch (type1) {
            case "object":
            case "array":
                var i, keysO = Object.keys(o), keysP = Object.keys(p);
                if (keysO.length !== keysP.length){
                    return false;
                }
                keysO.sort();
                keysP.sort();
                deep++;
                for ( i=0; i<keysO.length; i++ ){
                    var key = keysO[i];
                    if( type1 === "array" )key = key.toInt();
                    var valueO = o[key], valueP = p[key];
                    if( this.compareObjects( valueO, valueP, deep ) === false ){
                        return false;
                    }
                }
                break;
            case "function":
               break;
            default:
                if  (o!==p){
                    return false;
                }
        }
        return true;
    },

    saveFormData: function (callback, failure, history, data, issubmit, isstart) {
        if (this.businessData.work.startTime) {
            this.saveFormDataInstance(callback, failure, history, data, issubmit);
        } else {
            this.saveFormDataDraft(callback, failure, history, data, issubmit, isstart);
        }
    },
    saveFormDataInstance: function (callback, failure, history, data, issubmit) {
        if (this.officeList) {
            this.officeList.each(function (module) {
                module.save(history);
            });
        }
        var data = data || this.getData(issubmit);

        this.modifedData = {};
        this.setModifedData(data);

        if (this.toWordSaveList && this.toWordSaveList.length){
            var p = [];
            this.toWordSaveList.each(function(editor){
                if (editor.docToWord) p.push(new Promise(function(resolve){ editor.docToWord(resolve) }));
            });
            var copyData = Object.clone(data);
            Promise.all(p).then(function(){
                this.workAction.saveData(function () {
                    this.businessData.originalData = null;
                    this.businessData.originalData = copyData;
                    if(callback)callback();
                }.bind(this), failure, this.businessData.work.id, this.modifedData);
            }.bind(this));
        }else{
            var copyData = Object.clone(data);
            this.workAction.saveData(function () {
                this.businessData.originalData = null;
                this.businessData.originalData = copyData;
                if(callback)callback();
            }.bind(this), failure, this.businessData.work.id, this.modifedData);
        }
    },
    saveFormDataDraft: function (callback, failure, history, data, issubmit, isstart) {
        if (this.officeList) {
            this.officeList.each(function (module) {
                module.save(history);
            });
        }
        var data = data || this.getData(issubmit);
        var draft = {
            "data": data,
            "work": this.businessData.work,
            "identity": this.businessData.work.creatorIdentityDn
        }
        var copyData = Object.clone(data);
        this.workAction.saveDraft(draft, function (json) {

            this.businessData.originalData = null;
            this.businessData.originalData = copyData;

            this.workAction.getDraft(json.data.id, function (json) {
                this.businessData.work = json.data.work;
                this.app.options.draftId = json.data.work.id;

                if (layout.app && layout.app.inBrowser) {
                    if (layout.app) layout.app.$openWithSelf = true;
                    if (callback) callback();
                    if (!isstart) layout.desktop.openApplication(null, "process.Work", { "draftId": this.app.options.draftId });
                } else {
                    this.app.options.desktopReload = true;

                    this.app.appId = "process.Work" + json.data.work.id;
                    if (layout.desktop.apps) {
                        delete layout.desktop.apps[this.app.options.appId];
                    } else {
                        layout.desktop.apps = {};
                    }
                    layout.desktop.apps[this.app.appId] = this.app;

                    if (callback) callback();

                    if (!isstart) this.app.reload();
                }

            }.bind(this));
        }.bind(this), failure);
    },
    setProcessorSectionOrgList: function (data) {
        if (!this.routeDataList) this.getRouteDataList();
        var routeList = this.routeDataList;
        //var processorSectionOrg = [];
        for (var i = 0; i < routeList.length; i++) {
            (routeList[i].selectConfigList || []).each(function (config, j) {
                if (config.section == "yes") {
                    this.setSection(config, data);
                }
            }.bind(this))
        }
    },

    /**
     * @summary 获取当前工作的路由配置数据.
     * @example
     * this.form.getApp().appForm.getRouteDataList();
     * @return {Object[]}
     */
    getRouteDataList: function () {
        if (!this.routeDataList) {
            o2.Actions.get("x_processplatform_assemble_surface").listRoute({ "valueList": this.businessData.task.routeList }, function (json) {
                json.data.each(function (d) {
                    d.selectConfigList = JSON.parse(d.selectConfig || "[]");
                }.bind(this));
                this.routeDataList = json.data;
            }.bind(this), null, false);
        }
        return this.routeDataList;
    },

    beforeCloseWork: function () {
        this.fireEvent("beforeClose");
        if (this.app && this.app.fireEvent) {
            this.app.fireEvent("beforeClose");
            //    this.fireEvent("afterClose");
        }
        if (!this.options.readonly) {
            if (this.businessData.work && this.businessData.work.id) {
                if (!this.isSendBeacon) {
                    if (this.app.inBrowser && navigator.sendBeacon) {
                        var obj = this.workAction.action.actions["checkDraft"];
                        var url = this.workAction.action.address + obj.uri;
                        url = url.replace("{id}", this.businessData.work.id);
                        navigator.sendBeacon(url);
                    } else {
                        this.workAction.checkDraft(this.businessData.work.id, function () {
                            if (layout.desktop.apps) {
                                if (layout.desktop.apps["TaskCenter"] && layout.desktop.apps["TaskCenter"].window) {
                                    layout.desktop.apps["TaskCenter"].content.unmask();
                                    layout.desktop.apps["TaskCenter"].refreshAll();
                                }
                            }
                        }.bind(this), null, false);
                    }
                    this.isSendBeacon = true;
                }
            }
        } else {
            this.app.refreshTaskCenter();
        }
    },
    closeWork: function () {
        // this.fireEvent("beforeClose");
        // if (this.app && this.app.fireEvent){
        //     this.app.fireEvent("beforeClose");
        // //    this.fireEvent("afterClose");
        // }
        // debugger;
        // if (!this.options.readonly)
        //     if (this.businessData.work) this.workAction.checkDraft(this.businessData.work.id);

        // this.app.close();

        if (layout.mobile) {
            //移动端页面关闭
            this.finishOnMobileReal();
        } else {
            this.app.close();
        }

    },
    getMessageContent: function (data, maxLength, titlelp) {
        var content = "";
        if (data.completed) {
            content += MWF.xApplication.process.Xform.LP.workCompleted;
        } else {
            if (data.occurSignalStack) {
                if (data.signalStack && data.signalStack.length) {
                    var activityUsers = [];
                    data.signalStack.each(function (stack) {
                        var idList = [];
                        if (stack.splitExecute) {
                            idList = stack.splitExecute.splitValueList || [];
                        }
                        if (stack.manualExecute) {
                            idList = stack.manualExecute.identities || [];
                        }
                        var count = 0;
                        var ids = [];
                        idList.each( function(i){
                            var cn = o2.name.cn(i);
                            if( !ids.contains( cn ) ){
                                ids.push(cn)
                            }
                        });
                        if (ids.length > 8) {
                            count = ids.length;
                            ids = ids.slice(0, 8);
                        }
                        ids = o2.name.cns(ids);
                        var lp = MWF.xApplication.process.Xform.LP;
                        var t = "<b>" + lp.nextActivity + "</b><span style='color: #ea621f'>" + stack.name + "</span>；<b>" + lp.nextUser + "</b><span style='color: #ea621f'>" + ids.join(",") + "</span> <b>" + ((count) ? "," + lp.next_etc.replace("{count}", count) : "") + "</b>";
                        activityUsers.push(t);
                    }.bind(this));
                    content += activityUsers.join("<br>");
                } else {
                    content += MWF.xApplication.process.Xform.LP.taskCompleted;
                }
            } else {
                if (data.properties.nextManualList && data.properties.nextManualList.length) {
                    var activityUsers = [];
                    data.properties.nextManualList.each(function (a) {
                        var ids = [];
                        a.taskIdentityList.each(function (i) {
                            var cn = o2.name.cn(i);
                            if( !ids.contains( cn ) ){
                                ids.push(cn)
                            }
                        });
                        var t = "<b>" + MWF.xApplication.process.Xform.LP.nextActivity + "</b><span style='color: #ea621f'>" + o2.txt(a.activityName) + "</span>；<b>" + MWF.xApplication.process.Xform.LP.nextUser + "</b><span style='color: #ea621f'>" + ids.join(",") + "</span>";
                        activityUsers.push(t);
                    });
                    content += activityUsers.join("<br>");

                    if (data.manualTaskIdentityMatrix && data.manualTaskIdentityMatrix.matrix){
                        var manualTaskIdentityMatrix = data.manualTaskIdentityMatrix.matrix;
                        manualTaskIdentityMatrix = (manualTaskIdentityMatrix.flat) ? manualTaskIdentityMatrix.flat() :  manualTaskIdentityMatrix.reduce(function(acc, val){
                            return acc.concat(val);
                        }, []);
                        manualTaskIdentityMatrix = manualTaskIdentityMatrix.filter(function(id){
                            return !data.properties.nextManualTaskIdentityList.contains(id);
                        });
                        var len = manualTaskIdentityMatrix.length
                        if (len){
                            var idText = (len>8) ? o2.name.cns(manualTaskIdentityMatrix.slice(0.8)).join(", ")+" ..." : o2.name.cns(manualTaskIdentityMatrix).join(", ");
                            content += "<br><b>"+MWF.xApplication.process.Xform.LP.nextTaskMatrix+"</b><span style='color: #ea621f'>"+idText+ "</span>";
                        }
                    }

                } else {
                    if (data.arrivedActivityName) {
                        content += MWF.xApplication.process.Xform.LP.arrivedActivity + o2.txt(data.arrivedActivityName);
                    } else {
                        content += MWF.xApplication.process.Xform.LP.taskCompleted;
                    }

                }
            }
        }
        var title = this.businessData.data.title || this.businessData.data.subject || this.businessData.work.title
        if (maxLength && title.length > maxLength) {
            title = title.substr(0, maxLength) + "..."
        }
        return "<div>" + (titlelp || MWF.xApplication.process.Xform.LP.taskProcessedMessage) + "“" + o2.txt(title) + "”</div>" + content;
    },
    addMessage: function (data, notShowBrowserDkg) {
        if (layout.desktop.message) {
            var msg = {
                "subject": MWF.xApplication.process.Xform.LP.taskProcessed,
                "content": this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.taskProcessedMessage)
            };
            layout.desktop.message.addTooltip(msg);
            return layout.desktop.message.addMessage(msg);
        } else {
            if (this.app.inBrowser && !notShowBrowserDkg) {
                this.inBrowserDkg(this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.taskProcessedMessage));
            }
        }
    },
    formSaveValidation: function(){
        var flag = true;
        Object.each(this.forms, function (field, key) {
            if( !field.json.id || field.json.id.indexOf("..") > 0 )return;
            field.validationMode();
            if (!field.saveValidation()) flag = false;
        }.bind(this));
        return flag;
    },
    formValidation: function (routeName, opinion, medias) {
        if (this.options.readonly) return true;
        this.Macro.environment.form.currentRouteName = routeName;
        this.Macro.environment.form.opinion = opinion;
        this.Macro.environment.form.medias = medias;

        var flag = true;
        //flag = this.validation();
        Object.each(this.forms, function (field, key) {
            field.validationMode();
            if (!field.validation(routeName, opinion, medias)) flag = false;
        }.bind(this));
        return flag;
    },
    validationOtherFlow: function (routeName, opinion, processor, flowData) {
        this.Macro.environment.form.currentRouteName = routeName;
        this.Macro.environment.form.opinion = opinion;
        this.Macro.environment.form.flowData = flowData;
        if (!this.json.validationOtherFlow) return true;
        if (!this.json.validationOtherFlow.code) return true;
        var flag = this.Macro.exec(this.json.validationOtherFlow.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString() !== "true") {
            MWF.xDesktop.notice(
                "error",
                (processor) ? { "x": "center", "y": "top" } : { "x": "right", "y": "top" },
                flag,
                (processor) ? processor.container : (layout.mobile ? $(document.body) : this.app.content),
                null,  //{"x": 0, "y": 30}
                { "closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000 }
            );
            return false;
        }
        return true;
    },
    validation: function (routeName, opinion, processor, medias) {
        this.Macro.environment.form.currentRouteName = routeName;
        this.Macro.environment.form.opinion = opinion;
        this.Macro.environment.form.medias = medias;
        var routeFlag = this.validationRoute(processor);
        var opinionFlag = this.validationOpinion(processor);
        return routeFlag && opinionFlag;
    },
    validationRoute: function (processor) {
        if (!this.json.validationRoute) return true;
        if (!this.json.validationRoute.code) return true;
        var flag = this.Macro.exec(this.json.validationRoute.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString() != "true") {
            this.notValidationRouteMode(flag, processor);
            return false;
        }
        return true;
    },
    validationOpinion: function (processor) {
        if (!this.json.validationOpinion) return true;
        if (!this.json.validationOpinion.code) return true;
        var flag = this.Macro.exec(this.json.validationOpinion.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString() != "true") {
            this.notValidationOpinionMode(flag, processor);
            return false;
        }
        return true;
    },
    formCustomValidation: function () {
        if (!this.json.validationFormCustom) return true;
        if (!this.json.validationFormCustom.code) return true;
        var flag = this.Macro.exec(this.json.validationFormCustom.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString() != "true") {
            this.notValidationOpinionMode(flag);
            return false;
        }
        return true;
    },
    notValidationRouteMode: function (flag, processor) {
        if (processor) {
            var node = (processor.routeSelectorArea || processor.routeWraper);
            if(node)node.setStyle("background-color", "#ffe9e9");
        }
        MWF.xDesktop.notice(
            "error",
            { "x": "center", "y": "top" },
            flag,
            (processor) ? processor.routeSelectorArea : (layout.mobile ? $(document.body) : this.app.content),
            null,  //{"x": 0, "y": 30}
            { "closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000 }
        );
        //new mBox.Notice({
        //    type: "error",
        //    position: {"x": "center", "y": "top"},
        //    move: false,
        //    target: (processor) ? processor.routeSelectorArea : this.app.content,
        //    delayClose: 6000,
        //    content: flag
        //});
    },
    notValidationOpinionMode: function (flag, processor) {
        if (processor) {
            var node = (processor.inputTextarea || processor.opinionTextarea);
            if(node)node.setStyle("background-color", "#ffe9e9");
        }
        MWF.xDesktop.notice(
            "error",
            (processor) ? { "x": "center", "y": "top" } : { "x": "right", "y": "top" },
            flag,
            (processor) ? processor.inputTextarea : (layout.mobile ? $(document.body) : this.app.content),
            null,  //{"x": 0, "y": 30}
            { "closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000 }
        );
        //new mBox.Notice({
        //    type: "error",
        //    position: (processor) ? {"x": "center", "y": "top"} : {"x": "right", "y": "top"},
        //    move: false,
        //    target: (processor) ? processor.inputTextarea : this.app.content,
        //    delayClose: 6000,
        //    content: flag
        //});
    },


    //fireRtEvent: function(type, args, delay){
    //    type = removeOn(type);
    //    var events = this.$events[type];
    //    if (!events) return this;
    //    if (!events.length) return this;
    //    var event = events[events.length-1];
    //    args = Array.from(args);
    //    if (delay) fn.delay(delay, this, args);
    //    else return fn.apply(this, args);
    //    return this;
    //},
    getIgnoreImpowerIdentity: function (processorOrgList) {

        var list = [];
        var check = function (org, isProcessOrg) {
            var moduleData = isProcessOrg ? org.getValue() : org.getData();
            var flag = false;
            if (typeOf(moduleData) === "array" && moduleData.length) {
                moduleData.each(function (d) {
                    if (d.ignoreEmpower) {
                        list.push(d.distinguishedName || d.unique || d.id);
                        d.ignoredEmpower = true;
                        delete d.ignoreEmpower;
                        flag = true;
                    }
                })
            }
            if (flag) org.setData(moduleData);
        }

        var modules = this.modules;
        for (var i = 0; i < modules.length; i++) {
            var module = modules[i];
            var moduleName = module.json.moduleName;
            if (!moduleName) moduleName = typeOf(module.json.type) === "string" ? module.json.type.toLowerCase() : "";
            if (moduleName === "org") {
                check(module)
            }
        }
        if (processorOrgList && processorOrgList.length > 0) {
            for (var i = 0; i < processorOrgList.length; i++) {
                check(processorOrgList[i], true)
            }
        }

        return list;
    },
    //saveDocumentEditor
    submitWork: function (routeName, opinion, medias, callback, processor, data, appendTaskIdentityList, processorOrgList, callbackBeforeSave) {
        if (!this.businessData.control["allowProcessing"] && !this.businessData.control["allowFlow"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            this.app.content.unmask();
            if (processor && processor.node) processor.node.unmask();
            return false;
        }

        if (!this.formValidation(routeName, opinion, medias)) {
            this.app.content.unmask();
            if (callback) callback();
            return false;
        }
        if (!this.validation(routeName, opinion, processor, medias)) {
            if (processor && processor.node) processor.node.unmask();
            if (callback) callback();
            return false;
        }
        if (!opinion) {
            var idx = this.businessData.task.routeNameList.indexOf(routeName);
            if (this.businessData.task.routeOpinionList[idx]) {
                opinion = this.businessData.task.routeOpinionList[idx];
            }
        }
        this.fireEvent("beforeProcess", {routeName: routeName, opinion: opinion});
        if (this.app && this.app.fireEvent) this.app.fireEvent("beforeProcess");

        //处理忽略授权
        debugger;
        var ignoreEmpowerIdentityList = this.getIgnoreImpowerIdentity(processorOrgList);

        var _self = this;
        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            // 适配移动端
            if (layout.mobile) {
                this.mask.load();
            } else {
                this.mask.loadNode(this.app.content);
            }
            if (callbackBeforeSave) callbackBeforeSave();
            this.fireEvent("beforeSave");
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeSave");
            this.saveFormData(function (json) {
                this.businessData.task.routeName = routeName;
                this.businessData.task.opinion = opinion || "";

                // var mediaIds = [];
                // if (medias && medias.length) {
                //     medias.each(function (file, i) {
                //         var formData = new FormData();
                //         var fileName = "mediaOpinion_"+i+"_"+new Date().getTime();
                //         if( file.type && file.type.contains("/") ) {
                //             file.name = fileName + "." + file.type.split("/")[1];
                //         }else{
                //             file.name = fileName + ".unknow";
                //         }
                //
                //         formData.append("file", file, file.name);
                //         formData.append("site", "$mediaOpinion");
                //
                //
                //         this.workAction.uploadAttachment(this.businessData.work.id, formData, file, function (json) {
                //             mediaIds.push(json.data.id);
                //         }.bind(this), null, false);
                //     }.bind(this));
                // }
                // if (mediaIds.length) this.businessData.task.mediaOpinion = mediaIds.join(",");

                this.saveMedias(medias).then(function(){
                    if (appendTaskIdentityList && appendTaskIdentityList.length) {
                        var list = [];
                        appendTaskIdentityList.each(function (identity) {
                            if (typeOf(identity) === "object") {
                                list.push(identity.distinguishedName || identity.unique || identity.id)
                            } else {
                                list.push(identity);
                            }
                        }.bind(this));
                        this.businessData.task.appendTaskIdentityList = list;
                    }

                    this.businessData.task.ignoreEmpowerIdentityList = ignoreEmpowerIdentityList;

                    this.fireEvent("afterSave");
                    if (this.app && this.app.fireEvent) this.app.fireEvent("afterSave");

                    // var promiseList = [];
                    // if (this.documenteditorList && this.documenteditorList.length) {
                    //     var promiseList = [];
                    //     this.documenteditorList.each(function (module) {
                    //         promiseList.push(module.checkSaveNewHistroy());
                    //     });
                    // }
                    // Promise.all(promiseList).then(function(){
                    this.workAction.processTask(function (json) {
                        //if (processor) processor.destroy();
                        //if (processNode) processNode.destroy();
                        if (callback) callback(json);

                        this.taskList = json.data;
                        this.fireEvent("afterProcess", {routeName: routeName, opinion: opinion});
                        if (this.app && this.app.fireEvent) this.app.fireEvent("afterProcess");
                        //    this.notice(MWF.xApplication.process.Xform.LP.taskProcessed, "success");
                        this.addMessage(json.data, true);

                        if (this.app.taskObject) this.app.taskObject.destroy();

                        this.finishOnFlow("process", json.data);

                        //window.setTimeout(function(){this.app.close();}.bind(this), 2000);
                    }.bind(this), null, this.businessData.task.id, this.businessData.task);
                    // }.bind(this), function(){});

                }.bind(this));


            }.bind(this), null, true, data, true);

        }.bind(this));
    },

    saveMedias: function(medias){
        return new Promise(function(resolve){
            var mediaIds = [];

            if (medias && medias.length) {
                var mPs = [];
                medias.each(function (file, i) {
                    var formData = new FormData();
                    var fileName = "mediaOpinion_"+i+"_"+new Date().getTime();
                    if( file.type && file.type.contains("/") ) {
                        file.name = fileName + "." + file.type.split("/")[1];
                    }else{
                        file.name = fileName + ".unknow";
                    }

                    formData.append("file", file, file.name);
                    formData.append("site", "$mediaOpinion");

                    mPs.push(this.uploadMedia(formData, file).then(function(id){
                        mediaIds.push(id);
                    }));
                }.bind(this));

                Promise.all(mPs).then(function(){
                    if (mediaIds.length) this.businessData.task.mediaOpinion = mediaIds.join(",");
                    resolve();
                }.bind(this));
            }else{
                resolve();
            }
        }.bind(this))
    },
    uploadMedia(formData, file){
        return new Promise(function(resolve){
            this.workAction.uploadAttachment(this.businessData.work.id, formData, file, function(json){
                // mediaIds.push(json.data.id);
                resolve(json.data.id);
            }.bind(this))
        }.bind(this));
    },


    finishOnFlow: function(type, data, notCloseWindow){
        if (this.closeImmediatelyOnProcess && !notCloseWindow) {
            this.app.close();
        } else if (typeOf(this.showCustomSubmitedDialog) === "function") {
            this.showCustomSubmitedDialog(data);
        } else if (layout.mobile) {
            //移动端页面关闭
            this.finishOnMobile();
        } else {
            if (this.app.inBrowser) {
                if (this.mask) this.mask.hide();
                if (this.json.isPrompt !== false) {
                    switch (type) {
                        case "process":
                        case "goBack":
                            this.showSubmitedDialog(data);
                            break;
                        case "reset":
                            this.addResetMessage(data, notCloseWindow);
                            break;
                        case "addTask":
                            this.addAddTaskMessage(data, notCloseWindow);
                            break;
                    }
                } else {
                    if (this.json.afterProcessAction == "redirect" && this.json.afterProcessRedirectScript && this.json.afterProcessRedirectScript.code) {
                        var url = this.Macro.exec(this.json.afterProcessRedirectScript.code, this);
                        (new URI(url)).go();
                    } else {
                        // this.app.close();
                        this.dingTalkPcCloseOrAppClose();
                    }
                }
                //}

            } else {
                switch (type) {
                    // case "process":
                    //     this.showSubmitedDialog(data);
                    //     break;
                    case "reset":
                        this.addResetMessage(data, notCloseWindow);
                        break;
                    case "addTask":
                        this.addAddTaskMessage(data, notCloseWindow);
                        break;
                    case "goBack":
                        this.showSubmitedDialog(data, notCloseWindow);
                        break;
                }
                if( notCloseWindow ){
                    this.app.refresh();
                }else{
                    this.app.close();
                }
            }
        }
    },
    showSubmitedDialog: function (data) {
        var content = this.getMessageContent(data, this.json.submitedDlgStyle ? this.json.submitedDlgStyle.maxTitleLength : 60);
        //if( this.json.submitedDlgUseNotice ){
        //    MWF.xDesktop.notice("success", {x: "right", y:"top"}, content);
        //    if (this.json.isPrompt!==false){
        //        if (this.json.promptCloseTime!=0){
        //            var t = this.json.promptCloseTime || 2;
        //            t = t.toInt()*1000;
        //            var _work = this;
        //            window.setTimeout(function(){ _work.app.close();}, t);
        //        }
        //    }else{
        //        this.app.close();
        //    }
        //}else{
        var div = new Element("div", { "styles": { "margin": "10px 10px 0px 10px", "padding": "5px", "overflow": "hidden", "width": "270px" } }).inject(this.app.content);
        div.set("html", content);
        var timerNode = new Element("div", { "styles": { "margin-top": "5px" } }).inject(div);
        var options = {
            "content": div,
            "isTitle": false,
            "width": 350,
            "height": 180,
            "buttonList": [
                {
                    "text": this.app.lp.closePage,
                    "action": function () {
                        dlg.close();
                        if (this.json.afterProcessAction == "redirect" && this.json.afterProcessRedirectScript && this.json.afterProcessRedirectScript.code) {
                            var url = this.Macro.exec(this.json.afterProcessRedirectScript.code, this);
                            (new URI(url)).go();
                        } else {
                            // this.app.close();
                            this.dingTalkPcCloseOrAppClose();
                        }
                    }.bind(this)
                }
            ]
        };
        if (this.json.submitedDlgStyle) {
            options = Object.merge(options, this.json.submitedDlgStyle);
            if (this.json.submitedDlgStyle.contentStyle) {
                div.setStyles(this.json.submitedDlgStyle.contentStyle);
                delete options.contentStyle;
            }
        }
        var size = this.app.content.getSize();
        switch (options.promptPosition || this.json.promptPosition || "righttop") {
            case "lefttop":
                options.top = 10;
                options.left = 10;
                options.fromTop = 10;
                options.fromLeft = 10;
                break;
            case "righttop":
                options.top = 10;
                options.left = size.x - options.width - 10;
                options.fromTop = 10;
                options.fromLeft = size.x - 10;
                break;
            case "leftbottom":
                options.top = size.y - options.height - 10;
                options.left = 10;
                options.fromTop = size.y - 10;
                options.fromLeft = 10;
                break;
            case "rightbottom":
                options.top = size.y - options.height - 10;
                options.left = size.x - options.width - 10;
                options.fromTop = size.y - 10;
                options.fromLeft = size.x - 10;
                break;
            default:
                delete options.top;
                delete options.left;
                delete options.fromTop;
                delete options.fromLeft;
        }
        var _work = this;
        options.onPostLoad = function () {

            var dialog = this;
            dialog.node.setStyle("display", "block");
            var nodeSize = div.getSize();
            dialog.content.setStyles({
                //"width" : nodeSize.x,
                "height": nodeSize.y
            });
            dialog.setContentSize();

            if ((options.promptCloseTime || _work.json.promptCloseTime) != 0) {
                var t = options.promptCloseTime || _work.json.promptCloseTime || 2;
                t = t.toInt() * 1000;

                if (options.isCountDown) {
                    timerNode.set("text", _work.app.lp.closePageCountDownText.replace("{second}", Math.ceil(t / 1000).toString()));
                    t = t - 1000;

                    var countDown = function () {
                        if (t > 0) {
                            timerNode.set("text", _work.app.lp.closePageCountDownText.replace("{second}", Math.ceil(t / 1000).toString()));
                            t = t - 1000;
                            window.setTimeout(countDown, 1000);
                        } else {
                            dlg.close();

                            if (_work.json.afterProcessAction == "redirect" && _work.json.afterProcessRedirectScript && _work.json.afterProcessRedirectScript.code) {
                                var url = _work.Macro.exec(_work.json.afterProcessRedirectScript.code, _work);
                                (new URI(url)).go();
                            } else {
                                // _work.app.close();
                                _work.dingTalkPcCloseOrAppClose();
                            }
                        }
                    };
                    window.setTimeout(countDown, 1000);
                } else {
                    window.setTimeout(function () {
                        if (_work.json.afterProcessAction == "redirect" && _work.json.afterProcessRedirectScript && _work.json.afterProcessRedirectScript.code) {
                            var url = _work.Macro.exec(_work.json.afterProcessRedirectScript.code, _work);
                            (new URI(url)).go();
                        } else {
                            // _work.app.close();
                            _work.dingTalkPcCloseOrAppClose();
                        }
                    }, t);
                }
            }
        };
        var dlg = o2.DL.open(options);

    },
    startDraftProcess: function ( action ) {
        if (!this.formCustomValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }
        if (!this.formValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }
        this.saveFormData(function () {
            this.workAction.startDraft(this.businessData.work.id, function (json) {
                this.app.options.workId = json.data[0].work;
                if (layout.mobile || !layout.desktop.message) {
                    if (layout.notice) {
                        layout.notice(MWF.xApplication.process.Xform.LP.processStartedMessage + "“[" + o2.txt(json.data[0].processName) + "]" + o2.txt(this.businessData.data.title || this.businessData.data.subject));
                    }
                } else {
                    if (layout.desktop.message) {
                        var msg = {
                            "subject": MWF.xApplication.process.Xform.LP.processStarted,
                            "content": "<div>" + MWF.xApplication.process.Xform.LP.processStartedMessage + "“[" + o2.txt(json.data[0].processName) + "]" + o2.txt(this.businessData.data.title || this.businessData.data.subject) + "”</div>"
                        };

                        var tooltip = layout.desktop.message.addTooltip(msg);
                        var item = layout.desktop.message.addMessage(msg);
                    }
                }
                // 多次加载的bug
                // if (layout.app && layout.app.inBrowser) {
                //     if (layout.app) layout.app.$openWithSelf = true;
                //     layout.desktop.openApplication(null, "process.Work", { "workId": this.app.options.workId, "action": "processTask" });
                // }
                this.app.options.action = action || "processTask";
                this.app.reload();

                //this.app.notice(MWF.xApplication.process.Xform.LP.dataSaved, "success");
                //草稿模式暂时不能上传附件，不能直接流转文件
                // o2.Actions.invokeAsync([
                //     {"action": this.workAction, "name": "loadWork"},
                //     {"action": this.workAction, "name": "getWorkControl"},
                //     {"action": this.workAction, "name": "getWorkLog"},
                //     {"action": this.workAction, "name": "getRecordLog"},
                //     {"action": this.workAction, "name": "listAttachments"}
                // ], {"success": function(json_work, json_control, json_log, json_record, json_att){
                //     if (json_work && json_control && json_log && json_att){
                //         this.app.parseData(json_work.data, json_control.data, null, json_log.data, json_record.data, json_att.data);
                //         var workData = json_work.data;
                //         this.businessData.activity = workData.activity;
                //         this.businessData.originalData = Object.clone( this.businessData.data );
                //         this.businessData.taskList = workData.taskList;
                //         this.businessData.task = this.getCurrentTaskData(workData);
                //         this.businessData.taskList = workData.taskList;
                //         this.businessData.readList = workData.readList;
                //         this.businessData.work = workData.work;
                //         this.businessData.workCompleted = (workData.work.completedTime) ? workData.work : null;
                //
                //         this.businessData.workLogList = json_log.data;
                //         this.businessData.recordList = json_record.data;
                //         this.businessData.attachmentList = json_att.data;
                //         this.businessData.control = json_control.data;
                //
                //         if (this.businessData.task){
                //             this.processWork();
                //         }else{
                //             this.app.options.workId = json.data[0].work;
                //             this.app.reload();
                //         }
                //     }
                // }.bind(this), "failure": function(){}}, json.data[0].work);

            }.bind(this));
        }.bind(this), null, false, null, false, true)
    },
    getCurrentTaskData: function (data) {
        if ((data.currentTaskIndex || data.currentTaskIndex === 0) && data.currentTaskIndex != -1) {
            this.app.options.taskId = this.businessData.taskList[data.currentTaskIndex].id;
            return this.businessData.taskList[data.currentTaskIndex];
        }
        return null;
    },

    flowWork: function ( defaultRoute ) {
        if( !this.isLoaded ){ //未加载完成需要等待加载完成再执行
            var flowWorkFun = function () {
                this.removeEvent( "afterLoad", flowWorkFun );
                this._flowWork( defaultRoute )
            }.bind(this);
            this.addEvent("afterLoad", flowWorkFun)
        }else{
            this._flowWork( defaultRoute );
        }
    },
    _flowWork: function( defaultRoute ){
        if (!this.checkUploadAttachment()) return false;

        if (!this.businessData.work.startTime) {
            this.startDraftProcess();
        } else {
            if (this.json.mode == "Mobile") {
                setTimeout(function () {
                    this.flowWork_mobile( defaultRoute );
                }.bind(this), 100);
            } else {
                this.flowWork_pc( defaultRoute );
            }
        }
    },
    flowWork_pc: function ( defaultRoute ) {
        var _self = this;
        //? 添加事件
        this.fireEvent("beforeProcessWork");
        if (this.app && this.app.fireEvent) this.app.fireEvent("beforeProcessWork");

        if (!this.formCustomValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }

        if (!this.formValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }

        var flowNode = new Element("div", { "styles": this.app.css.flowNode_Area }).inject(this.node);
        flowNode.setStyle("opacity", 0);

        var setSize = function (notRecenter) {

            var dlg = this;
            if (!dlg || !dlg.node) return;
            dlg.node.setStyle("display", "block");
            //var size = flowNode.getSize();

            //希望滚动条在flow里面
            var maxHeight = dlg.getContentMaxHeight();
            var s = _self.flow.getSize();

            dlg.content.setStyles({
                "height": Math.min(s.y, maxHeight),
                "width": s.x,
                "padding-right": "0px"
            });

            s = dlg.setContentSize();
            if (!notRecenter) dlg.reCenter();
        };

        this.loadFlow(flowNode, "default", function (flow) {
            this.flowDlg = o2.DL.open({
                "title": this.app.lp.flowWork,
                "style": this.json.dialogStyle || "user",
                "zindex": 20001,
                "isResize": false,
                "content": flowNode,
                "maskNode": this.app.content,
                "positionHeight": 900,
                "maxHeight": 900,
                "maxHeightPercent": "98%",
                "minTop": 5,
                "width": "auto",
                "height": "auto",
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": function (d, e) {
                            //避免双击
                            if (this.flowTimer) {
                                clearTimeout(this.flowTimer);
                                this.flowTimer = null;
                            }
                            this.flowTimer = setTimeout(function(){
                                if (this.flow) this.flow.submit();
                                this.flowTimer = null;
                            }.bind(this), 200)
                        }.bind(this)
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () {
                            this.flowDlg.close();
                        }.bind(this)
                    }
                ],
                "onQueryClose": function(){
                    if (this.flow) this.flow.destroy();
                }.bind(this),
                "onPostLoad": function () {
                    flowNode.setStyle("opacity", 1);
                    setSize.call(this)
                }
            })
        }.bind(this), function () {
            if (this.flowDlg) setSize.call(this.flowDlg, true)
        }.bind(this), defaultRoute);
    },
    flowWork_mobile: function ( defaultRoute ) {
        if (this.app.inBrowser) {
            this.app.content.setStyle("height", document.body.getSize().y);
        }

        this.fireEvent("beforeProcessWork");
        if (this.app && this.app.fireEvent) this.app.fireEvent("beforeProcessWork");

        // if (this.json.mode != "Mobile") {
        //     this.app.content.mask({
        //         "destroyOnHide": true,
        //         "style": this.app.css.maskNode,
        //         "useIframeShim": true,
        //         "iframeShimOptions": { "browsers": true },
        //         "onShow": function () {
        //             this.shim.shim.setStyles({
        //                 "opacity": 0,
        //                 "top": "" + position.y + "px",
        //                 "left": "" + position.x + "px"
        //             });
        //         }
        //     });
        // }

        if (!this.formCustomValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }

        if (!this.formValidation("", "")) {
            this.app.content.unmask();
            return false;
        }

        var processNode = new Element("div.flowNode_mobile", { "styles": this.app.css.flowNode_mobile }).inject(document.body);
        // processNode.position({
        //     relativeTo: this.app.content,
        //     position: "topcenter",
        //     edge: "topcenter"
        // });
        this.loadFlow(processNode, null, null, null, defaultRoute);
    },
    loadFlow: function (hanlderNode, style, postLoadFun, resizeFun, defaultRoute) {
        var _self = this;
        MWF.xDesktop.requireApp("process.Work", layout.mobile ? "FlowMobile" : "Flow", null, false);
        var op = this.getOpinion();
        var mds = op.medias;
        var innerNode;
        if (layout.mobile) {
            innerNode = new Element("div").inject(hanlderNode);
        }

        var options = {
            "style": style || "default",
            "onResize": function () {
                if (resizeFun) resizeFun();
            },
            "onLoad": function () {
                if (postLoadFun) postLoadFun(this);

                _self.fireEvent("afterLoadProcessor", [this]);
                if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterLoadProcessor", [this]);
            },
            "onCancel": function () {
                //this.destroy();
                hanlderNode.destroy();
                //_self.app.content.unmask();
                _self.fireEvent("closeProcessor", [this]);
                if (_self.app && _self.app.fireEvent) _self.app.fireEvent("closeProcessor", [this]);
            },
            "opinionOptions": {
                "opinion": op.opinion,
                "tabletToolHidden": this.json.tabletToolHidden || [],
                "tabletWidth": this.json.tabletWidth || 0,
                "tabletHeight": this.json.tabletHeight || 0,
            },
            "processOptions": {
                "defaultRoute": defaultRoute,
                "isHandwriting": this.json.isHandwriting === "no" ? false : true,
                "onSubmit": function (routeName, opinion, medias, appendTaskIdentityList, processorOrgList, callbackBeforeSave) {
                    debugger;
                    if (!medias || !medias.length) {
                        medias = mds;
                    } else {
                        medias = medias.concat(mds)
                    }

                    var promise;
                    if (_self.toWordSubmitList && _self.toWordSubmitList.length){
                        var p = [];
                        _self.toWordSubmitList.each(function(editor){
                            if (editor.docToWord) p.push(new Promise(function(resolve){ editor.docToWord(resolve) }));
                        });
                        Promise.all(p).then(function(){
                            _self.submitWork(routeName, opinion, medias, function () {
                                this.destroy();
                                hanlderNode.destroy();
                                if (_self.flowDlg) _self.flowDlg.close();
                                delete this;
                            }.bind(this), this, null, appendTaskIdentityList, processorOrgList, callbackBeforeSave);
                        }.bind(this));
                    }else{
                        _self.submitWork(routeName, opinion, medias, function () {
                            this.destroy();
                            hanlderNode.destroy();
                            if (_self.flowDlg) _self.flowDlg.close();
                            delete this;
                        }.bind(this), this, null, appendTaskIdentityList, processorOrgList, callbackBeforeSave);
                    }
                }
            },
            "addTaskOptions":{
                "isHandwriting": false,
                "onSubmit": function (names, opinion, mode, before, routeName, userOpinion) {
                    MWF.require("MWF.widget.Mask", function () {
                        var data = {mode: mode, opinion: opinion, before: before, names:names, userOpinion:userOpinion};
                        _self.fireEvent("beforeAddTask", data);
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeAddTask");

                        _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                        if (layout.mobile) {
                            _self.mask.load();
                        } else {
                            _self.mask.loadNode(_self.app.content);
                        }

                        if( !_self.validationOtherFlow('addTask', userOpinion, this, data) ){
                            if (_self.mask) { _self.mask.hide(); _self.mask = null; }
                            return;
                        }

                        _self.fireEvent("beforeSave");
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeSave");
                        _self.saveFormData(function (json) {

                            _self.fireEvent("afterSave");
                            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterSave");

                            _self.AddTaskToPeson(names, opinion, mode, before, routeName, function (workJson) {
                                _self.fireEvent("afterAddTask", data);
                                if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterAddTask");
                                // _self.addResetMessage(workJson.data);
                                this.destroy();
                                hanlderNode.destroy();
                                if (_self.flowDlg) _self.flowDlg.close();

                                _self.finishOnFlow("addTask", workJson.data);

                            }.bind(this), function (xhr, text, error) {
                                var errorText = error + ":" + text;
                                if (xhr) errorText = xhr.responseText;
                                _self.app.notice("request json error: " + errorText, "error", _self.flowDlg.node);
                                if (_self.mask) { _self.mask.hide(); _self.mask = null; }
                            }.bind(this));
                        }.bind(this))
                    }.bind(this));
                }
            },
            "resetOptions":{
                "isHandwriting": false,
                "onSubmit": function (names, opinion, routeName, userOpinion) {
                    MWF.require("MWF.widget.Mask", function () {
                        var data = {routeName: routeName, opinion: opinion, userOpinion:userOpinion, names:names};
                        _self.fireEvent("beforeReset", data);
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeReset");

                        _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                        if (layout.mobile) {
                            _self.mask.load();
                        } else {
                            _self.mask.loadNode(_self.app.content);
                        }

                        if( !_self.validationOtherFlow('reset', userOpinion, this, data) ){
                            if (_self.mask) { _self.mask.hide(); _self.mask = null; }
                            return;
                        }

                        _self.fireEvent("beforeSave");
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeSave");
                        _self.saveFormData(function (json) {

                            _self.fireEvent("afterSave");
                            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterSave");

                            _self.resetToPeson(names, opinion, routeName, function (workJson) {
                                _self.fireEvent("afterReset", data);
                                if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterReset");
                                // _self.addResetMessage(workJson.data);
                                this.destroy();
                                hanlderNode.destroy();
                                // if (!_self.app.inBrowser) _self.app.close();
                                if (_self.flowDlg) _self.flowDlg.close();
                                // if (_self.mask) { _self.mask.hide(); _self.mask = null; }

                                _self.finishOnFlow("reset", workJson.data);

                            }.bind(this), function (xhr, text, error) {
                                var errorText = error + ":" + text;
                                if (xhr) errorText = xhr.responseText;
                                _self.app.notice("request json error: " + errorText, "error", _self.flowDlg.node);
                                if (_self.mask) { _self.mask.hide(); _self.mask = null; }
                            }.bind(this));
                        }.bind(this))
                    }.bind(this));
                }
            },
            "goBackOptions":{
                "isHandwriting": false,
                "onSubmit": function (opinion, routeName, activity, way, userOpinion) {
                    MWF.require("MWF.widget.Mask", function () {
                        var data = {routeName: routeName, opinion: opinion, activity:activity, way:way, userOpinion:userOpinion};
                        _self.fireEvent("beforeGoBack", data);
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeGoBack");

                        _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                        if (layout.mobile) {
                            _self.mask.load();
                        } else {
                            _self.mask.loadNode(_self.app.content);
                        }

                        if( !_self.validationOtherFlow('goBack', userOpinion, this, data) ){
                            if (_self.mask) { _self.mask.hide(); _self.mask = null; }
                            return;
                        }

                        _self.fireEvent("beforeSave");
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeSave");
                        _self.saveFormData(function (json) {

                            _self.fireEvent("afterSave");
                            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterSave");

                            _self.goBackToPerson(routeName, opinion, activity, way, function (workJson) {
                                _self.fireEvent("afterGoBack", data);
                                if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterGoBack");
                                this.destroy();
                                hanlderNode.destroy();
                                if (_self.flowDlg) _self.flowDlg.close();
                                _self.addMessage(workJson.data, true);
                                if (_self.app.taskObject) _self.app.taskObject.destroy();
                                _self.finishOnFlow("goBack", workJson.data);
                            }.bind(this));
                        }.bind(this))
                    }.bind(this));
                }
            }
        };

        if( this.json.mode == "Mobile" ){
            this.flow = new MWF.xApplication.process.Work.FlowMobile(innerNode || hanlderNode, this.businessData.task, options, this);
        }else{
            this.flow = new MWF.xApplication.process.Work.Flow(innerNode || hanlderNode, this.businessData.task, options, this);
        }
    },

    resetToPeson: function (identityList, opinion, routeName, success, failure) {
        var data = {
            "opinion": opinion,
            "routeName": routeName || MWF.xApplication.process.Xform.LP.reset,
            "identityList": identityList
            // "keep": !!keep
        };
        o2.Actions.load("x_processplatform_assemble_surface").TaskAction.V2Reset(
            //this.workAction.resetWork(
            function (json) {
                if (success) success(json);
            }.bind(this),
            function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            },
            this.businessData.task.id, data
        );
    },

    AddTaskToPeson: function (names, opinion, mode, before, routeName, success, failure) {
        var data = {
            "mode": mode,
            "before": !!before,
            "opinion": opinion,
            "routeName": routeName || MWF.xApplication.process.Xform.LP.addTask,
            "distinguishedNameList": names
        };
        o2.Actions.load("x_processplatform_assemble_surface").TaskAction.v3Add(
            //this.workAction.resetWork(
            function (json) {
                if (success) success(json);
            }.bind(this),
            function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            },
            this.businessData.task.id, data
        );
    },

    goBackToPerson: function(routeName, opinion, activity, way, callback){
        this.businessData.task.decision = routeName;
        this.businessData.task.routeName = routeName;
        this.businessData.task.opinion = opinion;
        this.businessData.task.action = "goBack";
        this.businessData.task.option = {
            "activity": activity,
            "way": way
        };

        // this.submitWork(routeName, opinion, null, function () {
        //     if(callback)callback();
        // }.bind(this));

        this.workAction.processTask(function (json) {
            if (callback) callback(json);
        }.bind(this), null, this.businessData.task.id, this.businessData.task);
    },


    processWork: function ( defaultRoute ) {
        if( !this.isLoaded ){ //未加载完成需要等待加载完成再执行
            var processWorkFun = function () {
                this.removeEvent( "afterLoad", processWorkFun );
                this._processWork( defaultRoute )
            }.bind(this);
            this.addEvent("afterLoad", processWorkFun)
        }else{
            this._processWork( defaultRoute );
        }
    },
    checkUploadAttachment: function(){
        if (o2.runningRequestsList.length){
            var runningRequests = [];
            var reg = /\/jaxrs\/attachment\/upload\/work\/([\w-]*)/;
            o2.runningRequestsList.forEach(function(r){
                var method = (r.requestOptions[0] || "get").toLowerCase();
                var url = r.requestOptions[1] || "";

                var m = url.match(reg);
                if (m && m[1]===this.businessData.work.id && method==="post"){
                    runningRequests.push(r);
                }
            }.bind(this));

            if (runningRequests.length){
                this.app.notice(MWF.xApplication.process.Xform.LP.uploading, "info");
                return false;
            }
        }
        return true;
    },
    _processWork: function( defaultRoute ) {
        if (!this.checkUploadAttachment()) return false;

        var _self = this;

        if (!this.businessData.work.startTime) {
            this.startDraftProcess();
        } else if (this.json.submitFormType === "select") {
            this.processWork_custom( defaultRoute );
        } else if (this.json.submitFormType === "script") {
            this.processWork_custom( defaultRoute );
        } else {
            if (this.json.mode == "Mobile") {
                setTimeout(function () {
                    this.processWork_mobile( defaultRoute );
                }.bind(this), 100);
            } else {
                this.processWork_pc( defaultRoute );
            }
        }
    },
    processWork_custom: function ( defaultRoute ) {
        this.fireEvent("beforeProcessWork");
        if (this.app && this.app.fireEvent) this.app.fireEvent("beforeProcessWork");

        if (!this.formCustomValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }

        if (!this.formValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }

        debugger;
        if (!this.submitFormModule) {
            if (!MWF["APPSubmitform"]) {
                MWF.xDesktop.requireApp("process.Xform", "Subform", null, false);
            }
            var submitFormContainer = new Element("div").inject(layout.mobile ? $(document.body) : this.app.content);
            this.submitFormModule = new MWF["APPSubmitform"](submitFormContainer, {
                id: this.json.id,
                submitFormSelected: this.json.submitFormSelected,
                submitFormAppSelected: this.json.submitFormAppSelected,
                submitFormType: this.json.submitFormType,
                submitFormScript: this.json.submitFormScript,
                submitScript: this.json.submitScript
            }, this);
            this.submitFormModule.addEvent("afterModulesLoad", function () {
                this.submitFormModule.show( defaultRoute );
                this.fireEvent("afterLoadProcessor", [this.submitFormModule]);
            }.bind(this))
            this.submitFormModule.load();
        } else {
            this.submitFormModule.show( defaultRoute );
            this.fireEvent("afterLoadProcessor", [this.submitFormModule]);
        }
    },
    processWork_pc: function ( defaultRoute ) {
        var _self = this;
        this.fireEvent("beforeProcessWork");
        if (this.app && this.app.fireEvent) this.app.fireEvent("beforeProcessWork");

        if (!this.formCustomValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }
        // MWF.require("MWF.widget.Mask", function() {
        //     this.mask = new MWF.widget.Mask({"style": "desktop", "zIndex": 50000});
        //     this.mask.loadNode(this.app.content);

        if (!this.formValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }

        var setSize = function (notRecenter) {

            var dlg = this;
            if (!dlg || !dlg.node) return;
            dlg.node.setStyle("display", "block");
            var size = processNode.getSize();
            dlg.content.setStyles({
                "height": size.y,
                "width": size.x
            });

            var s = dlg.setContentSize();
            // if ( dlg.content.getStyle("overflow-y") === "auto" && dlg.content.getStyle("overflow-x") !== "auto" ) {
            //     var paddingRight = (dlg.content.getStyle("padding-right").toInt() || 0 );
            //     if( paddingRight < 20 ){
            //         dlg.node.setStyle("width", dlg.node.getStyle("width").toInt() + 20 + "px");
            //         dlg.content.setStyle("width", dlg.content.getStyle("width").toInt() + 20 + "px");
            //     }
            // }
            if (!notRecenter) dlg.reCenter();
        }

        //var node = new Element("div", {"styles": this.css.rollbackAreaNode});
        var processNode = new Element("div", { "styles": this.app.css.processNode_Area }).inject(this.node);
        processNode.setStyle("opacity", 0);
        this.setProcessNode(processNode, "process", function (processor) {
            this.processDlg = o2.DL.open({
                "title": this.app.lp.process,
                "style": this.json.dialogStyle || "user",
                "isResize": false,
                "content": processNode,
                "maskNode": this.app.content,
                "positionHeight": 800,
                "maxHeight": 800,
                "maxHeightPercent": "98%",
                "minTop": 5,
                "width": "auto", //processNode.retrieve("width") || 1000, //600,
                "height": "auto", //processNode.retrieve("height") || 401,
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": function (d, e) {
                            //避免双击
                            if (this.processTimer) {
                                clearTimeout(this.processTimer);
                                this.processTimer = null;
                            }
                            this.processTimer = setTimeout(function(){
                                if (this.processor) this.processor.okButton.click();
                                this.processTimer = null;
                            }.bind(this), 200)
                        }.bind(this)
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () {
                            _self.fireEvent("closeProcessor", [this]);
                            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("closeProcessor", [this]);
                            this.processDlg.close();
                        }.bind(this)
                    }
                ],
                "onQueryClose": function(){
                    if (this.processor) this.processor.destroy();
                }.bind(this),
                "onPostLoad": function () {
                    processNode.setStyle("opacity", 1);
                    processor.options.mediaNode = this.content;
                    setSize.call(this)
                }
            })
        }.bind(this), function () {
            if (this.processDlg) setSize.call(this.processDlg, true)
        }.bind(this), defaultRoute);
    },
    processWork_mobile: function ( defaultRoute ) {
        if (this.app.inBrowser) {
            this.app.content.setStyle("height", document.body.getSize().y);
        }

        this.fireEvent("beforeProcessWork");
        if (this.app && this.app.fireEvent) this.app.fireEvent("beforeProcessWork");
        var position = this.app.content.getPosition(this.app.content.getOffsetParent());

        if (this.json.mode != "Mobile") {
            this.app.content.mask({
                "destroyOnHide": true,
                "style": this.app.css.maskNode,
                "useIframeShim": true,
                "iframeShimOptions": { "browsers": true },
                "onShow": function () {
                    this.shim.shim.setStyles({
                        "opacity": 0,
                        "top": "" + position.y + "px",
                        "left": "" + position.x + "px"
                    });
                }
            });
        }

        if (!this.formCustomValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }
        // MWF.require("MWF.widget.Mask", function() {
        //     this.mask = new MWF.widget.Mask({"style": "desktop", "zIndex": 50000});
        //     this.mask.loadNode(this.app.content);

        if (!this.formValidation("", "")) {
            this.app.content.unmask();
            //    if (callback) callback();
            return false;
        }

        var processNode = this.createProcessNode();

        //this.setProcessNode(processNode);
        this.setProcessNode(processNode, null, null, null, defaultRoute);

        this.showProcessNode(processNode);
        processNode.setStyle("overflow", "auto");
        //}.bind(this));
    },

    createProcessNode: function () {
        var fromCss = this.app.css.processNode_from;
        var css = this.app.css.processNode;
        if (layout.mobile) {
            fromCss = this.app.css.processNodeMobile_from;
            css = this.app.css.processNodeMobile;

            // var contentSize = this.app.content.getSize();
            fromCss.width = "100%";
            css.width = "100%";
            fromCss.height = "100%";
            css.height = "100%";
        }

        if (this.json.mode == "Mobile") {
            var processNode = new Element("div", { "styles": fromCss }).inject(document.body);
        } else {
            var processNode = new Element("div", { "styles": fromCss }).inject(this.app.content);
        }


        processNode.position({
            relativeTo: this.app.content,
            position: "topcenter",
            edge: "topcenter"
        });
        return processNode;
    },
    getOpinion: function () {
        var opinion = "";
        var medias = [];
        Object.each(this.forms, function (m, id) {
            if (m.json.type === "Opinion") if (this.businessData.data[id]) opinion += " " + m._getBusinessSectionDataByPerson();
            if (m.handwritingFile) if (m.handwritingFile[layout.session.user.distinguishedName]) medias.push(m.handwritingFile[layout.session.user.distinguishedName]);
            if (m.soundFile) if (m.soundFile[layout.session.user.distinguishedName]) medias.push(m.soundFile[layout.session.user.distinguishedName]);
            if (m.videoFile) if (m.videoFile[layout.session.user.distinguishedName]) medias.push(m.videoFile[layout.session.user.distinguishedName]);
        }.bind(this));
        return { "opinion": opinion.trim(), "medias": medias };
    },
    setProcessNode: function (processNode, style, postLoadFun, resizeFun, defaultRoute) {
        var _self = this;
        MWF.xDesktop.requireApp("process.Work", "Processor", function () {
            var op = this.getOpinion();
            var mds = op.medias;

            var innerNode;
            if (layout.mobile) {
                innerNode = new Element("div").inject(processNode);
            }
            this.processor = new MWF.xApplication.process.Work.Processor(innerNode || processNode, this.businessData.task, {
                "style": (layout.mobile) ? "mobile" : (style || "default"),
                "opinion": op.opinion,
                "isHandwriting": this.json.isHandwriting === "no" ? false : true,
                "tabletToolHidden": this.json.tabletToolHidden || [],
                "tabletWidth": this.json.tabletWidth || 0,
                "tabletHeight": this.json.tabletHeight || 0,
                "defaultRoute": defaultRoute,
                "onPostLoad": function () {
                    if (postLoadFun) postLoadFun(this);
                    _self.fireEvent("afterLoadProcessor", [this]);
                },
                "onResize": function () {
                    if (resizeFun) resizeFun();
                },
                "onCancel": function () {
                    processNode.destroy();
                    _self.app.content.unmask();
                    delete this;
                },
                "onSubmit": function (routeName, opinion, medias, appendTaskIdentityList, processorOrgList, callbackBeforeSave) {
                    if (!medias || !medias.length) {
                        medias = mds;
                    } else {
                        medias = medias.concat(mds)
                    }

                    var promise;
                    if (_self.toWordSubmitList && _self.toWordSubmitList.length){
                        var p = [];
                        _self.toWordSubmitList.each(function(editor){
                            if (editor.docToWord) p.push(new Promise(function(resolve){ editor.docToWord(resolve) }));
                        });
                        Promise.all(p).then(function(){
                            _self.submitWork(routeName, opinion, medias, function () {
                                this.destroy();
                                processNode.destroy();
                                if (_self.processDlg) _self.processDlg.close();
                                delete this;
                            }.bind(this), this, null, appendTaskIdentityList, processorOrgList, callbackBeforeSave);
                        }.bind(this));
                    }else{
                        _self.submitWork(routeName, opinion, medias, function () {
                            debugger;
                            this.destroy();
                            processNode.destroy();
                            if (_self.processDlg) _self.processDlg.close();
                            delete this;
                        }.bind(this), this, null, appendTaskIdentityList, processorOrgList, callbackBeforeSave);
                    }
                }
            }, this);
        }.bind(this));
    },
    showProcessNode: function (processNode) {
        if (layout.mobile) {
            processNode.setStyles(this.app.css.processNodeMobile)
        } else {
            var size = this.app.content.getSize();
            var nodeSize = processNode.getSize();

            var top = size.y / 2 - nodeSize.y / 2 - 20;
            var left = size.x / 2 - nodeSize.x / 2;
            if (top < 0) top = 0;

            this.app.css.processNode.top = "" + top + "px";
            this.app.css.processNode.left = "" + left + "px";

            var morph = new Fx.Morph(processNode, {
                "duration": 300,
                "transition": Fx.Transitions.Expo.easeOut
            });
            morph.start(this.app.css.processNode);
        }

    },


    confirm: function (type, e, title, text, width, height, ok, cancel, callback, mask, style) {
        MWF.require("MWF.xDesktop.Dialog", function () {
            var size = this.container.getSize();
            var x = 0;
            var y = 0;

            if (typeOf(e) === "element") {
                var position = e.getPosition(this.app.content);
                x = position.x;
                y = position.y;
            } else {
                if (Browser.name == "firefox") {
                    x = parseFloat(e.event.clientX || e.event.x);
                    y = parseFloat(e.event.clientY || e.event.y);
                } else {
                    x = parseFloat(e.event.x);
                    y = parseFloat(e.event.y);
                }

                if (e.target) {
                    var position = e.target.getPosition(this.app.content);
                    //var position =  e.target.getPosition();
                    x = position.x;
                    y = position.y;
                }
            }

            // if (Browser.Platform.ios){
            //     $("textdiv").set("text", "$(document.body).getScroll().y: "+$(document.body).getScroll().y);
            //     y = y-$(document.body).getScroll().y;
            // }

            if (x + parseFloat(width) > size.x) {
                x = x - parseFloat(width);
            }
            if (x < 0) x = 10;
            if (y + parseFloat(height) > size.y) {
                y = y - parseFloat(height);
            }
            if (y < 0) y = 10;

            //var x = parseFloat((Browser.name==="firefox") ? e.event.clientX : e.event.x);
            //var y = parseFloat((Browser.name==="firefox") ? e.event.clientY : e.event.y);

            // if (x+parseFloat(width)>size.x){
            //     x = x-parseFloat(width);
            // }
            if (x < 0) x = 20;
            if (!layout.mobile) { // pc上鼠标位置偏移20
                x = x - 20
            }


            var opt = {
                "title": title,
                "style": style || "o2",
                "top": y,
                "left": x,
                "fromTop": e.event.y,
                "fromLeft": (Browser.name === "firefox") ? e.event.clientX - 20 : e.event.x - 20,
                "width": width,
                "height": height,
                "container": this.app.content,
                "maskNode": mask || this.app.content,
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": ok
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": cancel
                    }
                ]
            };


            if (typeOf(text).toLowerCase() === "object") {
                if( text.html )opt.html = text.html;
                if( text.text )opt.text = text.text;
            } else {
                if( /<\/?[a-z][\s\S]*>/i.test(text||"")){
                    opt.html = text;
                }else{
                    opt.text = text
                }
            }

            var dlg = new MWF.xDesktop.Dialog(opt);

            switch (type.toLowerCase()) {
                case "success":
                    if (this.json.confirmIcon && this.json.confirmIcon.success) {
                        dlg.content.setStyle("background-image", "url(" + this.json.confirmIcon.success + ")");
                    } else {
                        dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAB1hJREFUeNqsWGtsVEUUPnMf+y6rLcW2tDxUKARaikqgiWh8BlH8IwYkaozhh4nhB1FMTKkxQtQYQzRGE2JEfMRHYhQSVChgFYIGqLSUtoKUQmlp2b53u233de94zuzcZbfdbhdwkpPZmbl3zjffnHPuOcue/WgxZNnc3OT3cQ4rGIMlwNg8BjATGEwDDgHOeZdpQis3eKMR5Sd62kaO/PHp5QDub2ba9OtNTYnf2lQIcOO5igpr8eeT3kL9XneuCi6vAvYcFWxOBqrO6BlvZIx7w8PGwlG/uWZkwADNzo4//e7CfQMdYz/88t6F8/i+icB4Jl0sEzPIxEbsXiwotVd6C3TwTFezZRGCfQb4r0bhSnPo78io8dWP1ed24nRkPFNTMoMnnYNsbGYK2zR/pYsRGxJc1mDcuQqKHbwF2t3/Hh29a+3bC8oHOkM7UPk5UpGOpQQzFsINHyxahDaxdeYix/r8223AFLjpxpGL3rYIXDw5um+gc+ydwx9fqsPpKC0lP6eWr54hfjT+2gPP7Fg0R1HgreIyx/rpc2zxjfjNCzXXrSo4PMr8sWFecEuRo6mjMdBPdpQMJuWa6GoKF9jX55bo13UlE5jg8szobshyotG+RtT1OJrBAA43o/hRYhOYKVuVvxFtZPusCie7GUbQvcnmIBbh4noEoqR15zQV/N1GeXFZzvD5Y4P1ydclwJD7om1sn3uPs0S3x1++ESHlJgJB74FiXgkD4XZQLGr4NQtBh2DDvWa+3aOd7D4b7CGDFjcjr2dt3mxbpQNjB53sRsTA7YiN0IgBRWYlrJz2suhpTPO0bj1LegpKHWWFpZ6nUL0ngYOAUkBz34JAYjytEO1GJN5Pth4LmRAajkGxuQJWFb0CLpdL9DSmeVpPfp/0uXP1B2+b5y5A/cJbVLSVh9252uu5M/WM1BMYSLKBdFczS6mEx0peBbfbDU6nE1RVhdnOZdDj78AruyyvLP6+ZmMQDQMCYc3tp/xnKSAq9K2xuxmYBp8oeIJY2ITwSAxm8uWip7E43bj1ErYCHpsVB0KsOBwO0dOY5mdrlXhdSe+ikN6cPNtSeTsqgV2iOxRchFRBh4uGOSpCY8QTP5C/SfQ0pnkjmrq+es6WBBBN0wQrNpsNvF4vFBYWwgvL3ofFeY/EmZQ6SK/do5YiECeFGYW+vprGUu0AaY/iHYeDceqfmLtFKKGexjRP15K8ngxEUa6FbfpNwH5qfQua+w8lGCUhvbpDLZE2g8xgGkAhP4WRCJ3YhFk6KrozrignJ0f0NKb50LCRsp4OCJNu/X3LG3Cm92Dcm5LYJ71oO9MtMJrIRyguGzwRPelu5zoqYc28a4rodLqui2eexPk9/3DRTwXku6ZqaOo7KOw2bdqgMLf8EigaJUaxCHgT+yCY8hmPwrrFb4oNLbEUkGITj7iuoloozwTk28ZqONMzOZA4U3w07mLANMrQ0CO85GpWO+M7iKsMNlRsk2zxxP2TYo/HIwBZ43RAvmmohkZfzaRAqIlgGDH7rEChUaqIXrFQUVPfauiqEcifvWubUJAMiLwkLeUSyNenEMjVzECokTdGQman/FiaGuWs6DlrdNvENxs6DwCuw3PLtqcAygTkq5Nb4XT31EAEGIragVgrBTz6PmmUPBNdppH+hfrOGhEbnl8+OSALyJfHtwpGswFiXdNgV6jFAqPm3+7yOb36A5pdKaY906UF3f4LcNXfDhUlDyUUjwey+6+qOPAs0w8KH0NXI00nvu/aFQoaPnxtWKFyAhHui4Yw/0B20goyU3+5BnYfq0oASPYymqd1em7SPcYJ6fP7wn8OdYcp0RoRzFBiHPCFexRdqdR0VsRkzjpBiKGhC+BDhpbOfijBzOdHq+BU+4H4ic3sJIYRPtAbbWk+1Pv54JXQRdxmiExI+CTVNVROjI2YPGPeggrrLh2AXUeqBCvU09jk15f7kJ6+S6P7244PUT0VkDYTz/QoGf+ntr9h/srcIs2mLFVY5oyua7AVfIF2qGvbn5rFZSHESn9HaG/Nhxc/wxmylUErDxbMyBomQnVNcDC2Lyq9a1LB051o3T/hWzOV0L6D3eHalsN936K+PgkkYiWkyVWR+dsnl85RXRP0R3+OxbioEP4vof2GfOHac0f6v7h4cqhZghlNLldS6iZCiA/6qK7RnapLtSvlwCm43ES1QFdjco6s722q6d2NFcFp1NMjbSWWsdbGypIshj7POatfu+MlT55tnd2lljHOso1l18yIYYIeNFrIWGt3tv8o2SAZJu8h80iutRPMWE0aNFEXobqGygk0ar+iM5eqswIrqE0w3ASAeD8WjDX1d4ztIfet3+v7XRprL/0nQIxYtba8kan/hUDUikx8PJTFl96fdx/lrJQqUoZGiRHlI5QG0NeXPnr0raEQf7a2r04GtICU4FT/QmTDPJOGTqAcMnl2yrFNJkZWMIhJ7yAZk5E1JMfm+EI/naLraQRKlQBUKUoSGFNWh4YEZowv7jO1/wQYAIxJoZGb/Cz/AAAAAElFTkSuQmCC)");
                    }
                    break;
                case "error":
                    if (this.json.confirmIcon && this.json.confirmIcon.error) {
                        dlg.content.setStyle("background-image", "url(" + this.json.confirmIcon.error + ")");
                    } else {
                        dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABsVJREFUeNqkWFtsFGUU/nZn2r21IqX3llp6AQmkWDVGAgIlGI0EeMAHffAFa998MCQaE8JDxZCgSHzQKIm3qPHFGC7GW0xqkIgIKhhEwFJaKSDQUtplu73s7vidmX/q32F2uw2TnOzMv2fO+f5z/8fci7yvWAZYRXo4CCwLAM1cq+HvXRYwQrrM/7rTwB+TwC/dwKG3uU75mVxCO7T7wExgKHiBATzJ2411wMoy3pSQ5gg6UiFpgpQgDZNukK6TLgBHuf7lAPD5q8DfXMpQl5U3mA4P4ztAO3+2tADLCQSV+VsR/5L+If0G/EqgH78EvKtwT1lqr0en6SfoLaCe1niB7nj+CQIuV+uZWYApV8RNPPAVcP/rQMtF4I03gbNcpjdvt5KxQXs4SKKflxBI54PAs20EElNvZTQJucjLFyUtpZwioJVurFtMD/4MXBXWDUqnL5jHHYt0PgQ8da/4UFMwThpTz0HF7wfEj0/kSKwVAwsZU5U1wKkTwOBBj7GD08xE17QSSJPanVCKlCSNkM5s2mT/JtV6epZ8InclsH4R9TjYRKWPZQixnch2POJsZNpOb5HOb9yIi5s3I5XJIHb2rL2LoBZL+fBZKhOZaS3LgPgh4HcnYZ34scFI+goQxsj8iA+QHipItrejrKwMiaVLMZJIIEpFAaUkH76AFrEVfLxEzzEej/0FXFOGc8CQ8bmFTOE6DciEUnCBCsapoLGxETU1NYhGo7i+YAHiSlFauWMmvqAGKOzcVzDlh2mdo2o/loCJkeEVRnldSMsGUdCrKaiqqkJxcTEikQgKCgpsRbJzk4oukm8iB1+CfEUKkLtZub/CZOsFvht0Qi1lrAfW0WwvN3gyI7J1K+7ZswfNzc0oLS1FKBRCMBiEaZoIh8OOovp6jI6NYXLLFjQ1NdlAxCKGYaCwsBAlJSWoJ08lwQZTKaSPHJmSL9YZZWZx438eZ8yLMwwWtWeYaqvv9oBJ8UWDyovWrUMgEPi/ZPPeBWT/rlhhx0h1dbUNRABPpSrvBVhixw4kd+26rRyMOq3jCl31kzya0vSiKgW91/DOnbZJ53V22iAsy5pSIopra2vtNflP3KIDcTcwuH074pQT8JEvelkMF4kjpBuY0n1Dbjj7XDcpSCCU+gCKxWK+77hABghkOAsQuUIOivmq3xrSm2qMLJZxrwEKlGJQ5QGUC8gVBSSYQ67hoCidAiPzSCCHZSxVlXopeHhiAk30v8RBtivFQO3etg1Du3fbbihQKe0L3MmqmGrYwaAMRuPKMl6aVCkeJ11jRvSuWYO+vj4kk0lf4bIu/wuf8MfV+5NZ5I87RhhVuAKmTGhsbHPCWSwiwoYoOMQ60tDQgPLycjvNfWOA6/J/Op3GefJzsMLcAwfs6PSz0JhTXAfcBDNlVCS0xaYHSEql3jCBRLSC5k3faV1XZZnwySWABmUqJKCo8oUOaNTZbL9SlzE4Niwh8lURLf/TyoQzAZFgdcmvDklhjKsKXKAqsF5rZEztAboOAz+KA4xHmeo0+tNFqky7VMkKfJ+nAnuV2rtn1pS0td32n16B67kpRjZuqQrs6pB5mW37s5OswoLNaOTUdRfQRjPWGhrqOF80aYVSTwXWgfQQSL8URiqa6wGkV+B+ZuAlTwUWF/VxyPoUeD/uTH5x4xhjiNapoHXWhj3l+ubhw0hTkbtz3SXdBNJHIJgFn+Vx0Tlg37eOi+RAkTTk+MDueY1WWc64qQ5oZpSXhpSiedrOz1HBBVWZZ8Pn0phzcjj9DfBBvz1r4aYkrz3PvEhZq9lIyfgY3RXwzrY3lKKytWtxhgp6fHaaL5+AoU8stulPvgB+UFZJuPOMPaF/D5wgoGq6q9XMosianER3FiD58iWcDNr/GvCegwtDbjeywShAGQ5Y3aYzZC00PELsDkxFmOGokosv6cy/XV8DHyr3XFfL1rSBnL/WNqKUcw3rQWWhD6A7oaSTPV1dwEecX07CmX1v6W3Re4iz5IAl5xqCiTIMW0zJ5DsAkXKOLxbHy/1iEQ3IiHdYmAbGdZccsBhDXXKcoMAyWqjCynJwywVCqjgbz2kJVokR5RoXyKRkctYTpQ5Iepica+Q4QesMU0GUoCozPjGS0QZ5t9uzJ51ioO6T9FVZc1XFiLgm5X6ROJjvJ5EOZ4iXwaeIs2Elz1WreExtlVFRJjQZjGQekTFAuq80PRazbp6JTtOyxy87FX9EkYCY8H6v6fDMNzNdagayQYXVZ5mIei7UmrHrnQlFSZXJY9qnECuXIjMPMJZ2lHIPj6aaGg0FNOD5CJHWjtl5f0n5T4ABAFHaXG6UVjGNAAAAAElFTkSuQmCC)");
                    }
                    break;
                case "info":
                    if (this.json.confirmIcon && this.json.confirmIcon.info) {
                        dlg.content.setStyle("background-image", "url(" + this.json.confirmIcon.info + ")");
                    } else {
                        dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABvBJREFUeNqsWF1sFFUUPndm9n+3W5aWLi2VGgJtgy3UEn6MQGI0GgmC0UgfTEjEBxPDA29qYqLGGOODifGBGGOUYOKDPIg2QgykWgUtP1WgLT+lFKFCf9l2uz+z83c9Z3p3u1u6u4Pxpqczd+7MPd8999xzvrPskb2fgsMW4NzaBpxvBsbWMWCrgUEdAKsA4HHO+R2wrOucmxe5qZ9Jjt3ovtX1eRznt0pN2ndof+5eKYcAJ34YJPlFvH3OFV7+uOyPgOQLg+wJAXP5gMkueifM9XTYzMw2W+mZnWbqHjDF09Pc8WFneur2kaHOjwbxewuB8VK6WCnLoCVexcsrnmWNW1zhKMiBKqdWBDM5CfrMKKh3+8+bWurw1W/f/gwfawstVdYyuNIGtMYBxqT9/lVbmRyIZMFlUeKfCdyiPi0WN02ScPdkvGX2KxJa0IOiVETbU0O/Ptr00getamzkY1R+lbAuZiV52fpnC4FY5lqQpPe80bX7/A2bmIRbQcpzggAQLFhaGiw1aV+5nqEPEQcjWDnAJJLLC57q1Ux2+9tATzwUXN40PH3j7Nj4hWMW6cbr4mDmLIJAals63Esbsk8LhFsGAkjBY3UaPN8M8HKbBGsiHBRmwK1pEy0kC+Pkf4eK/EtA8gTX8Mxs1Lukti9+6+IUAco3ROE24dZ4apo6XEvq57dkQbPQKtsQ575NleB1z30erQbYsMoApScJ3bd1kMRWLWw0r9/Ud+Ci72H3AMoMinGfZchZ0Ufe961Yz/LNvFBoi/ZuDMKaukoIBAIQDofB7XaD1+MGl8Thl6EMWkYq+r3srQAzfrc1VN8yG7t26k/UpGfNJ+WOL54ab30746TQMkuIBVuaaiAUCoHf7wdFUewr9ek5jZf8HucnPe7Q0j3R9t0tqNdtn4AsGIoj7sjKLbI3ZDtiKSEnvTyqgSzLhScB+/ScxsvNQXq8NY0twdrGF/DTYBYH/QtQQJN9lbZzlhOa7MRADHRDnB4h1KfnNO5kHtLnCkSeCERXR4V1QK5e98yTij/ypquyrug+Fwhu7+BoGsbjGngVCaoq3NA7PAuHT4/BjxdjUMrf8oUpqN/IRNGO/TM3e69QQFQo1zB3wN7PMokht+802Q/nUij/5MVyNnesJTnrAmUb6UXfacPb71ESCiU9CkxQBsxcfFHB0tXFjz2CkRQP5iw/AlIcgSG9sjfYiLc+CjMKZV8mk4GM0mBw/MDTUdjc4ANVVUHXdftk5AIWnqozf6tw8FQc44yz/EV6ZZe3XvgM9ogGUFwoYxmav7IyAitXLgNN0yCRSNiAcgHN5YJdyyU42N2LSzYdopHId6rmwdh8BBz4DMA7Ry7D71fG4d2OFjvQFVqOg2EY837lsGGADIhMIFGojIOpoWUMB2LCsd4RSGdKbKmjeYSgXgSeEoZnCjE0y8iEMa06Wgk3DQxOJiZvdFhJWsTRnVuGIxjL0CazGVWxqaKeaba5iLMZcoGu2Dg4BYPUA0/niEiWlkKc1TLUnXYQcjKBZZQd55azhaFeMNLx6xTwiHApRJ65oTleTdn3rAewDOpVY3cGcmCIxQfrPD3I6DYRuS5vGbPsuBOfISqiJyb7Jge6zmE3TVslUTmBCDs5miy3qqJCJ6CMItPMnbxSQvoyM2OnM9N3iWglbcsQW6dyAq2yW5Hk9rncUiQ3oSKT9hnjCTkwRd15DKb93DRwkQwToVw8R5Hl0CoDscE/TmI3jqLSBttnk+oaKiesTJIT4V5MuGHY5Ht7cxWk00jGrcL8RH16TuM2STcMKDYX6UlN3Dw+PdQzKMBoOdpJDH1qoOuvSOPWWklxt9krWkg3cTVv7NkAr+3aaFNNsko+n6G+z+eDra0PQU2lD37rv7MonSBfUaduHx0+/skXODqGEsvyYNsyoobRqK4xUrFOCkZ2vMgThqYPBUMQDAbtYJcPJCv0nMbpPXp/4Rw0L/pI12T/yW9Q36QAomU5cEFFiQWW0vDU6xu9kRVvuXwVO+wE+n81pB2Z+HjX1JXuQ1NzJ2i0aHVADbeLU4FFdY3s9vkll6eVAWcLa6cHFeQ/XL03cnTi0k9fYUVwgVQJXzGKVpTCfywqsBB9F5UTyDmq8aTVsP8Cgk5ZJjGQHL32NfkIBrjhPCA6uUfRijIfEO0l1TWKJ3gWnXoG61w/U1zRnFPC/VVjlvFRM9REH4aM7yYunfhy7PzRn4WzThC9pOFsrZ0PpuSvEOhDkiA+QWLxS5u2byPOSlSRGBoRI+IjRAMo+1LSo1xDIZ4iqwhocSGJcr9COCGITJw6AuUVpY1P9N2CGDFhHkOcDk2E+KQIaNS3Ck24uKIHaQRKFgBkIVIeGFJoCjHE1XI6+b8CDABnZtjY0mkIGQAAAABJRU5ErkJggg==)");
                    }
                    break;
                case "warn":
                    if (this.json.confirmIcon && this.json.confirmIcon.warn) {
                        dlg.content.setStyle("background-image", "url(" + this.json.confirmIcon.warn + ")");
                    } else {
                        dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABgtJREFUeNqsWG2IlFUUft6vmdlZd539GFdTY5VMomy1oBJUSPLXkmQt5I8gCIMK+iH0K4ooEvtTRP7JX9JKWCC1mUUkIkQKSoaZH60t2pboOK37Mc6Ozsw779t57t5xx5ndnTvhC4d373nnnnPuOeee85y1Jr+G6dNcCrBB6AnbQo9tY4UFLLYstIYhMsK/IjRULOF0voATx87jp60fICPygzmFbpn+26pnzK0ilrk2+kTp5kgC6+w4YDfJxpiQJ+QAYUmoKHQLCG4K5YDsCI7fzOPgcBr7172BP0VUILrC/22MnHSbvF6KLcRap1WMmGfsRQRZ2Z8BJv7BybEs9t6/DbuFXaj2VKUx7ize6BZvbHcdvB67D5bdrD/ocwUiruiLZPFGSbziiHci4iVPpEn41MM9pPZWPBofwiOX9uDh05fwkSgf5Dln8lKNZwo+HnRsvBVbjK1eJ39RdWIxJCfhGB0HxjNTBtGQhHiuPQHEY9MG3X5EbXEESA3i4KmL2Ln5Xfwi3CINmjVM9IjnYGeTGOJ2zOx+SU5cE8Hp/DMIopvgxFejlDsFO38IC6ID6JIDRCMz7/WvA1cG8d2PJ/H2y5/gLA2a9ndVmBia2CIxpL3yJ1XG5MUzTh8S3e/B9Zo09x74xSeRS7vyfT+i3sx7KXfRCvSuz2NUltuFJmhj+btdmazMEa+NsZidfIYlsQXzE51oa2tDV1eXenNNPr/PtZ/ylybxwtEP8Ypw4pU22OXrq27NvZIh4dzCeI07lvQiHo8jFovBdV315pp8fp9rP+VTT/cCPL/jRawSTqScma4OT1+sA2vtqN4w552V03meMsKyprLblowlj2s/qC+DepJLsWpjD56T5aDOnRI908yC5jTVOVEl1THWhKhPwrVx/UNYqL0DmyU+0iyVNWooKKxjTGgmh/o6k+h5tRcbhBNTDla9JtKAV+6SZ5RBondZF9YwOkKOq5qeZ6CkUpmJMQYP9Xa0YqX8ySRxXdV9bXMBloShnLg134RvhQ3IEr2tTViqc8ZxNQwwFuCJANsqiOJ4jSHke40cTPQ2RdFZNsYmHrEaiHVEmqI/drTGO+paC5/fTWVRghTaZl1ibJvAqG6hqqygIsG+/iXCID8VFk1ck+9Z5rKoV8BYThc9yyVCE2A0nyDJKOmEoiP98GV7mNwKO7EOwfjPwL9fKL7q2CUzWTRGANiILgghjRkKfTwAyxw4cWt4pR+F4X72NAn2FIxQzg4aECMtcmISl3WzDFxi1sDH046hZ4JQ45kbgmeyFXhGUGB7i8YzhgcTvbg2jiHCKPrTJXgmE56ZgKIoH5XGn/YEz3QLnpm/GrcmTiE9dkiOOaBuU9QzN+bsMM7dNoYo/qk1OC597vEahDbDU5BtuVbBMysr8ExS45lBV74LnjHwMhFjahRndn2rUN9NhsrmOEEUX/LNbgB/F13yLBJtyTvwDNfkNyLnj8s4dv5vBbQmVdcmWuc4IYl0MjC44jz0guWb0NLSojAMoQTfXJPvGNQs6hGvnNt7GIeFkyGk4hcVGM41HCcEZIV1ix53jJ+QieDOWKi18CN2fWOo58QF/PD5ETVPZXTO3IZ8Aeea9Dj2FOt4R7WDq1L0SlVFT9bke3WMofzf/8I3fTvwlXAYomy5IChj9AxT4FyTmsBBPyyXoVpSRe9qP8LfXkNw7ZAaIfnmmnwbs++l3AspHPl4APuEw2I3pr0S1owqMsO4B97BYz3L8eaiFvR6uHsPceWFNI7s/h6f7TqgblBq1umgPCRwwOJcc3EEe3NsOXN4yYRUkRQ5vw5j4P19+FQbkha6Ud04aiZK8Y6lS2ALxwmi+GQcqxyGKDT3RCBSSkKpLM4xWXWOjGi6UXeirDKI1yXOcYIonuC5s1lQoTbKKlPZCdYUBZpSGZxhHeH11bdmVOdIrnLWNv4vhPzQ1sBnHlE8wTMxK6EiERqBEfEIYQC7L5seew1LPCurLmgZTdl6/4UwaWmWzq2IRvHNGrNGNLmYdpCvb0dBl/hJXdAKJrOF1eClsHX4XP12NM+qGFJKmnz9NgYV/wkwAMYATK0QLuhAAAAAAElFTkSuQmCC)");
                    }
                    break;
                default:
                    if (this.json.confirmIcon && this.json.confirmIcon.warn) {
                        dlg.content.setStyle("background-image", "url(" + this.json.confirmIcon.warn + ")");
                    }
                    break;
            }
            dlg.show();
        }.bind(this));
    },
    alert: function (type, title, text, width, height) {
        this.app.alert(type, "center", title, text, width, height);
    },
    notice: function (content, type, target, where, offset, option) {
        if (!where) where = { "x": "right", "y": "top" };
        //if (!target) target = this.node;
        if (!type) type = "ok";
        var type2;
        switch (type) {
            case "warn":
            case "wran":
                type2 = "notice";
                break;
            case "success":
                type2 = "ok";
                break;
            default:
                type2 = type;
        }
        var noticeTarget = target || ((layout.mobile && document && document.body) ? $(document.body) : this.app.window.content);
        var off = offset;
        if (!off) {
            off = {
                x: 10,
                y: where.y.toString().toLowerCase() == "bottom" ? 10 : 10
            };
        }
        var options = {
            type: type2,
            position: where,
            move: false,
            target: noticeTarget,
            delayClose: (type === "error") ? 10000 : 5000,
            //delayClose: 20000000,
            offset: off,
            content: content
        }
        if (this.json.noticeStyle) {
            options = Object.merge(options, this.json.noticeStyle);
        }
        if (this.json["notice" + type2.capitalize() + "Style"]) {
            options = Object.merge(options, this.json["notice" + type2.capitalize() + "Style"]);
        }
        if (option && typeOf(option) === "object") {
            options = Object.merge(options, option);
        }
        new mBox.Notice(options);
    },
    dialog: function( options ){
        if( !options )options = {};
        var opts = {
            "style" : options.style || "user",
            "title": options.title || "",
            "width": options.width || 300,
            "height" : options.height || 150,
            "isMax": o2.typeOf( options.isMax ) === "boolean" ? options.isMax : false,
            "isClose": o2.typeOf( options.isClose ) === "boolean"  ? options.isClose : true,
            "isResize": o2.typeOf( options.isResize ) === "boolean"  ? options.isResize : true,
            "isMove": o2.typeOf( options.isMove ) === "boolean"  ? options.isMove : true,
            "isTitle": o2.typeOf( options.isTitle ) === "boolean"  ? options.isTitle : true,
            "offset": options.offset || null,
            "mask": o2.typeOf( options.mask ) === "boolean"  ? options.mask : true,
            "container": options.container ||  ( layout.mobile ? $(document.body) : this.app.content ),
            "duration": options.duration || 200,
            "lp": options.lp || null,
            "zindex": ( options.zindex || 100 ).toInt(),
            "buttonList": options.buttonList || [
                {
                    "type": "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function(){
                        if(options.ok){
                            var flag = options.ok.call( this );
                            if( flag === true || o2.typeOf(flag) === "null" )this.close();
                        }else{
                            this.close();
                        }

                    }
                },
                {
                    "type": "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function(){
                        if(options.close){
                            var flag = options.close.call(this);
                            if( flag === true || o2.typeOf(flag) === "null" )this.close();
                        }else{
                            this.close();
                        }
                    }
                }
            ]
        };

        var positionNode;
        if( options.moduleName ){
            var module, name = options.moduleName, subformName = options.subformName;
            if( subformName && this.all[subformName +"_"+ name] ){
                module = this.all[subformName +"_"+ name];
            }else{
                module = this.all[name];
            }
            if( module ){
                opts.content = module.node;
                positionNode = new Element("div", {style:"display:none;"}).inject( opts.content, "before" );
            }
        }else if( options.content ) {
            opts.content = options.content;
            var parent = opts.content.getParent();
            if(parent)positionNode = new Element("div", {style:"display:none;"}).inject( opts.content, "before" );
        }
        if( options.url )opts.url = options.url;
        if( options.html )opts.html = options.html;
        if( options.text )opts.text = options.text;

        opts.onQueryClose = function(){
            if( positionNode && opts.content ){
                opts.content.inject( positionNode, "after" );
                positionNode.destroy();
            }
            if( o2.typeOf(options.onQueryClose) === "function" )options.onQueryClose.call( this );
        }
        if(opts.onPostClose)opts.onPostClose = options.onPostClose;
        if(opts.onQueryLoad)opts.onQueryLoad = options.onQueryLoad;
        if(opts.onPostLoad)opts.onPostLoad = options.onPostLoad;
        if(opts.onQueryShow)opts.onQueryShow = options.onQueryShow;
        if(opts.onPostShow)opts.onPostShow = options.onPostShow;

        for( var key in options ){
            if( !opts.hasOwnProperty( key ) ){
                opts[key] = options[key];
            }
        }
        var dialog;
        MWF.require("MWF.xDesktop.Dialog", function(){
            dialog = o2.DL.open(opts)
        }, null, false);
        return dialog;
    },
    addSplit: function () {
        if (!this.businessData.control["allowAddSplit"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }
        MWF.require("MWF.xDesktop.Dialog", function () {
            var width = 600;
            var height = 330;
            var p = MWF.getCenterPosition(this.app.content, width, height);

            var _self = this;
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.app.lp.addSplit,
                //"style": "work","
                "style": this.json.dialogStyle || "user",
                "top": p.y - 100,
                "left": p.x,
                "fromTop": p.y - 100,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "url": this.app.path + "split.html",
                "lp": MWF.xApplication.process.Xform.LP.form,
                "container": this.app.content,
                "isClose": true,
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": function (d, e) {
                            //this.doResetWork(dlg);
                            var input = dlg.content.getElement("input");
                            var checks = dlg.content.getElements(".o2_addSplit_radio");
                            var value = input.get("value");
                            var trimExist = true;
                            if (checks[1].checked) trimExist = false;

                            var opinion="";
                            var textarea = dlg.content.getElement(".addSplit_opinion");
                            if(textarea)opinion = textarea.get("value");

                            _self.doAddSplit(dlg, value, trimExist, opinion);
                        }.bind(this)
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    //var okButton = dlg.content.getElement(".o2_addSplit_okButton");
                    //var cancelButton = dlg.content.getElement(".o2_addSplit_cancelButton");
                    var selectButton = dlg.content.getElement(".o2_addSplit_selector");
                    var input = dlg.content.getElement("input");
                    var checks = dlg.content.getElements(".o2_addSplit_radio");

                    //okButton.addEvent("click", function(){
                    //    var value = input.get("value");
                    //    var trimExist = true;
                    //    if (checks[1].checked) trimExist = false;
                    //    _self.doAddSplit(this, value, trimExist);
                    //}.bind(this));
                    //cancelButton.addEvent("click", function(){
                    //    this.close();
                    //}.bind(this));
                    selectButton.addEvent("click", function () {
                        var value = input.get("value");
                        MWF.xDesktop.requireApp("Selector", "package", function () {
                            new o2.O2Selector(_self.app.content, {
                                "type": "",
                                "count": 0,
                                "values": (value) ? value.split(o2.splitStr) : [],
                                "types": ["unit", "identity", "group", "role"],
                                "onComplete": function (items) {
                                    var v = [];
                                    items.each(function (item) {
                                        v.push(item.data.distinguishedName);
                                    });
                                    input.set("value", v.join(", "));
                                }
                            });
                        }.bind(this));
                        //_self.selectSplitUnit(this);
                    }.bind(this));
                }
            });
            dlg.show();
        }.bind(this));
    },
    doAddSplit: function (dlg, splitValues, trimExist, opinion) {
        if (!splitValues) {
            this.app.notice(MWF.xApplication.process.Xform.LP.inputSplitValue, "error", dlg.node);
            return false;
        }
        MWF.require("MWF.widget.Mask", function () {
            var splitValue = splitValues.split(o2.splitStr);

            var splitText = splitValue.map(function (sValue) {
                return sValue.split("@")[0];
            })
            var routeName = MWF.xApplication.process.Xform.LP.form.split+":"+splitText.join(", ");
            if(!opinion)opinion = routeName;

            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            this.mask.loadNode(this.app.content);

            this.fireEvent("beforeAddSplit");
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeAddSplit");

            this.addSplitWork(splitValue, trimExist, function (json) {
                this.fireEvent("afterAddSplit");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterAddSplit");
                this.addAddSplitMessage(json.data);
                // this.workAction.loadWork(function(workJson){
                //     this.fireEvent("afterAddSplit");
                //     if (this.app && this.app.fireEvent) this.app.fireEvent("afterAddSplit");
                //     this.addAddSplitMessage(workJson.data);
                // }.bind(this), null, this.businessData.work.id);
                dlg.close();
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), function (xhr, text, error) {
                var errorText = error + ":" + text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: " + errorText, "error", dlg.node);
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), routeName, opinion);
        }.bind(this));
    },
    addSplitWork: function (splitValue, trimExist, success, failure, routeName, opinion) {
        var data = {
            "splitValueList": splitValue,
            "trimExist": trimExist,
            "routeName": routeName,
            "opinion": opinion
        };
        if (this.options.readonly) {
            o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2AddSplit(this.businessData.work.id, data, function (json) {
                if (success) success(json);
            }.bind(this),
                function (xhr, text, error) {
                    if (failure) failure(xhr, text, error);
                });
            // this.workAction.addSplit(
            //     function (json) {
            //         if (success) success(json);
            //     }.bind(this),
            //     function (xhr, text, error) {
            //         if (failure) failure(xhr, text, error);
            //     },
            //     this.businessData.work.id, data
            // );
        } else {
            this.saveFormData(
                function (json) {
                    o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2AddSplit(this.businessData.work.id, data, function (json) {
                        if (success) success(json);
                    }.bind(this),
                        function (xhr, text, error) {
                            if (failure) failure(xhr, text, error);
                        });
                    // this.workAction.addSplit(
                    //     function (json) {
                    //         if (success) success(json);
                    //     }.bind(this),
                    //     function (xhr, text, error) {
                    //         if (failure) failure(xhr, text, error);
                    //     },
                    //     this.businessData.work.id, data
                    // );
                }.bind(this),
                function (xhr, text, error) {
                    if (failure) failure(xhr, text, error);
                }, true, null, true
            );
        }

    },
    setRollBackChecked: function (item) {
        item.store("isSelected", true);
        item.setStyles(this.css.rollbackItemNode_current);

        item.getFirst().setStyles(this.css.rollbackItemIconNode_current);

        var node = item.getLast().getFirst();
        node.getFirst().setStyles(this.css.rollbackItemActivityNode_current);
        node.getLast().setStyles(this.css.rollbackItemTimeNode_current);

        node = item.getLast().getLast();
        node.getFirst().setStyles(this.css.rollbackItemTaskTitleNode_current);
        node.getLast().setStyles(this.css.rollbackItemTaskBodyNode_current);

        var checkeds = item.getElements("input");
        if (checkeds){
            checkeds.set("checked", true);
            checkeds.set("disabled", false);
        }
    },
    setRollBackUnchecked: function (item) {
        item.store("isSelected", false);
        item.setStyles(this.css.rollbackItemNode);

        item.getFirst().setStyles(this.css.rollbackItemIconNode);

        var node = item.getLast().getFirst();
        node.getFirst().setStyles(this.css.rollbackItemActivityNode);
        node.getLast().setStyles(this.css.rollbackItemTimeNode);

        node = item.getLast().getLast();
        node.getFirst().setStyles(this.css.rollbackItemTaskTitleNode);
        node.getLast().setStyles(this.css.rollbackItemTaskBodyNode);

        var checkeds = item.getElements("input");
        if (checkeds) {
            checkeds.set("checked", false);
            checkeds.set("disabled", true);
        }
    },
    getRollbackLogs: function (rollbackItemNode) {
        var _self = this;
        o2.Actions.load("x_processplatform_assemble_surface").WorkLogAction.listRollbackWithWorkOrWorkCompleted(this.businessData.work.id, function (json) {

            json.data.each(function (log) {
                //if (!log.splitting && log.connected && (log.taskCompletedList.length || log.readList.length || log.readCompletedList.length)) {
                if (!log.splitting && log.connected) {
                    var node = new Element("div", { "styles": this.css.rollbackItemNode }).inject(rollbackItemNode);
                    node.store("log", log);
                    var iconNode = new Element("div", { "styles": this.css.rollbackItemIconNode }).inject(node);
                    var contentNode = new Element("div", { "styles": this.css.rollbackItemContentNode }).inject(node);

                    var div = new Element("div", { "styles": { "overflow": "hidden" } }).inject(contentNode);
                    var activityNode = new Element("div", { "styles": this.css.rollbackItemActivityNode, "text": log.fromActivityName }).inject(div);
                    var timeNode = new Element("div", { "styles": this.css.rollbackItemTimeNode, "text": log.arrivedTime }).inject(div);
                    div = new Element("div", { "styles": { "overflow": "hidden" } }).inject(contentNode);
                    var taskTitleNode = new Element("div", { "styles": this.css.rollbackItemTaskTitleNode, "text": this.app.lp.taskCompletedPerson + ": " }).inject(div);

                    var taskBodyNode = new Element("div", {
                        "styles": this.css.rollbackItemTaskBodyNode
                    }).inject(div);

                    if (log.taskCompletedList.length) {
                        log.taskCompletedList.each(function (o) {
                            var itemNode = new Element("div", {
                                style: "float:left;overflow:hidden;"
                            }).inject(taskBodyNode);
                            var vfor = Math.random().toString();
                            var text = o2.name.cn(o.person) + "(" + o.completedTime + ")";
                            var check = new Element("input", {
                                "id": vfor,
                                "value": o.identity,
                                "type": "checkbox",
                                "disabled": true,
                                "styles": this.css.rollbackItemTaskCheckNode
                            }).inject(itemNode);
                            check.addEvent("click", function (e) {
                                e.stopPropagation();
                            });
                            var taskNode = new Element("label", { "styles": this.css.rollbackItemTaskNode, "text": text, "for": vfor }).inject(itemNode);
                            taskNode.addEvent("click", function (e) {
                                e.stopPropagation();
                            });
                        }.bind(this));
                    } else {
                        var text = this.app.lp.systemFlow;
                        var taskNode = new Element("div", { "styles": this.css.rollbackItemTaskNode, "text": text }).inject(taskBodyNode);
                    }



                    node.addEvents({
                        "mouseover": function () {
                            var isSelected = this.retrieve("isSelected");
                            if (!isSelected) this.setStyles(_self.css.rollbackItemNode_over);
                        },
                        "mouseout": function () {
                            var isSelected = this.retrieve("isSelected");
                            if (!isSelected) this.setStyles(_self.css.rollbackItemNode)
                        },
                        "click": function () {
                            var isSelected = this.retrieve("isSelected");
                            if (isSelected) {
                                _self.setRollBackUnchecked(this);
                            } else {
                                var items = rollbackItemNode.getChildren();
                                items.each(function (item) {
                                    _self.setRollBackUnchecked(item);
                                });
                                _self.setRollBackChecked(this);
                            }
                        }
                    });
                }
            }.bind(this));

        }.bind(this), null, false);

    },
    rollback: function () {
        if (!this.businessData.control["allowRollback"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }
        var lp = MWF.xApplication.process.Xform.LP;
        var node = new Element("div", { "styles": this.css.rollbackAreaNode });
        var html = "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;float:left;\">"+lp.selectRollbackActivity+"</div>";
        //html += "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;float:right;\"><input class='rollback_flowOption' checked type='checkbox' />"+lp.tryToProcess+"</div>";
        html += "<div style=\"clear:both; max-height: 300px; margin-bottom:10px; margin-top:10px; overflow-y:auto;\"></div>";
        node.set("html", html);
        if( layout.mobile ){
            node.getFirst().setStyle("float", "none");
            node.getFirst().getNext().setStyle("float", "none");
        }
        var rollbackItemNode = node.getLast();
        this.getRollbackLogs(rollbackItemNode);
        node.inject(this.app.content);

        var dlg = o2.DL.open({
            "title": this.app.lp.rollback,
            "style": this.json.dialogStyle || "user",
            "isResize": false,
            "content": node,
            "width": layout.mobile ? "100%" : 600,
            "buttonList": [
                {
                    "type": "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function (d, e) {
                        this.doRollback(node, e, dlg);
                    }.bind(this)
                },
                {
                    "type": "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function () { dlg.close(); }
                }
            ]
        });
    },
    doRollback: function (node, e, dlg) {
        var rollbackItemNode = node.getLast();
        var items = rollbackItemNode.getChildren();
        //var flowOption = (node.getElement(".rollback_flowOption").checked);
        var _self = this;
        for (var i = 0; i < items.length; i++) {
            if (items[i].retrieve("isSelected")) {
                var text = this.app.lp.rollbackConfirmContent;
                var log = items[i].retrieve("log");
                var checks = items[i].getElements("input:checked");

                var idList = [];
                checks.each(function (check) {
                    var id = check.get("value");
                    if (idList.indexOf(id) == -1) idList.push(id);
                });

                text = text.replace("{log}", log.fromActivityName + "(" + log.arrivedTime + ")");
                this.app.confirm("infor", e, this.app.lp.rollbackConfirmTitle, text, 450, 120, function () {
                    _self.doRollbackAction(log.id, dlg, idList, log);

                    dlg.close();

                    this.close();
                }, function () {
                    this.close();
                }, null, null, this.json.confirmStyle);
                break;
            }
        }
    },

    doRollbackAction: function (log, dlg, idList, logObj) {
        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            this.mask.loadNode(this.app.content);

            this.fireEvent("beforeRollback");
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeRollback");

            this.doRollbackActionInvoke(log, idList, function (json) {
                if (json.data.properties) {
                    this.fireEvent("afterRollback");
                    if (this.app && this.app.fireEvent) this.app.fireEvent("afterRollback");
                    this.addRollbackMessage(json.data);

                    if (!this.app.inBrowser) this.app.close();
                    if (this.mask) { this.mask.hide(); this.mask = null; }
                } else {
                    var id = json.data.id;
                    this.workAction.listTaskByWork(function (workJson) {
                        this.fireEvent("afterRollback");
                        if (this.app && this.app.fireEvent) this.app.fireEvent("afterRollback");
                        this.addRollbackMessage_old(workJson.data);
                        //this.app.notice(MWF.xApplication.process.Xform.LP.rollbackOk+": "+MWF.name.cns(names).join(", "), "success");
                        //if (!this.app.inBrowser) this.app.close();

                        if (!this.app.inBrowser) this.app.close();
                        if (this.mask) { this.mask.hide(); this.mask = null; }
                    }.bind(this), null, id);
                }
            }.bind(this), function (xhr, text, error) {
                var errorText = error + ":" + text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: " + errorText, "error");
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), logObj);
        }.bind(this));
    },
    doRollbackActionInvoke: function (id, idList, success, failure, logObj) {
        var opinion = MWF.xApplication.process.Xform.LP.rollbackTo+":"+logObj.fromActivityName;
        if (this.businessData.work.completedTime) {
            var method = "rollbackWorkcompleted";
            o2.Actions.get("x_processplatform_assemble_surface")[method](this.businessData.work.id, {
                "workLog": id,
                "distinguishedNameList": idList,
                //"processing": !!flowOption,
                "opinion": opinion
            }, function (json) {
                if (success) success(json);
            }.bind(this), function (xhr, text, error) {
                if (failure) failure(xhr, text, error)
            }.bind(this));
        } else {
            var body = {
                "workLog": id,
                "distinguishedNameList": idList,
                //"processing": !!flowOption,
                "opinion": opinion
            }
            o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2Rollback(this.businessData.work.id, body, function (json) {
                //o2.Actions.get("x_processplatform_assemble_surface")[method](this.businessData.work.id, { "workLog": id }, function (json) {
                if (success) success(json);
            }.bind(this), function (xhr, text, error) {
                if (failure) failure(xhr, text, error)
            }.bind(this));
        }

    },

    inBrowserDkg: function (content, notCloseWindow) {
        if (this.mask) this.mask.hide();

        if (this.json.submitedDlgUseNotice) {
            MWF.xDesktop.notice("success", { x: "right", y: "top" }, content);
            if (this.json.isPrompt !== false) {
                if (this.json.promptCloseTime != 0) {
                    var t = this.json.promptCloseTime || 2;
                    t = t.toInt() * 1000;
                    var _work = this;
                    window.setTimeout(function () { _work.app.close(); }, t);
                }
            } else {
                this.app.close();
            }
        } else {
            var div = new Element("div", { "styles": { "margin": "10px 10px 0px 10px", "padding": "5px", "overflow": "hidden" } }).inject(this.app.content);
            div.set("html", content);

            if (this.json.isPrompt !== false) {
                var options = {
                    "content": div,
                    "isTitle": false,
                    "width": 350,
                    "height": 180,
                    "buttonList": [
                        {
                            "text": MWF.xApplication.process.Xform.LP.ok,
                            "action": function () { dlg.close(); this.app.close(); }.bind(this)
                        }
                    ]
                }
                var size = this.app.content.getSize();
                switch (this.json.promptPosition || "righttop") {
                    case "lefttop":
                        options.top = 10;
                        options.left = 10;
                        options.fromTop = 10;
                        options.fromLeft = 10;
                        break;
                    case "righttop":
                        options.top = 10;
                        options.left = size.x - 360;
                        options.fromTop = 10;
                        options.fromLeft = size.x - 10;
                        break;
                    case "leftbottom":
                        options.top = size.y - 190;
                        options.left = 10;
                        options.fromTop = size.y - 10;
                        options.fromLeft = 10;
                        break;
                    case "rightbottom":
                        options.top = size.y - 190;
                        options.left = size.x - 360;
                        options.fromTop = size.y - 10;
                        options.fromLeft = size.x - 10;
                        break;
                    default:
                        delete options.top;
                        delete options.left;
                        delete options.fromTop;
                        delete options.fromLeft;
                }
                var dlg = o2.DL.open(options);
                if (this.json.promptCloseTime != 0) {
                    var t = this.json.promptCloseTime || 2;
                    t = t.toInt() * 1000;
                    var _work = this;
                    window.setTimeout(function () {
                        dlg.close();
                        if (notCloseWindow){
                            window.location.reload();
                        }else{
                            _work.app.close();
                        }
                    }, t);
                }
            } else {
                if (notCloseWindow){
                    window.location.reload();
                }else{
                    this.app.close();
                }
            }
        }
    },
    addRollbackMessage_old: function (data) {
        var users = [];
        data.each(function (task) {
            users.push(MWF.name.cn(task.person) + "(" + MWF.name.cn(task.unit) + ")");
        }.bind(this));
        var content = "<div><b>" + MWF.xApplication.process.Xform.LP.currentActivity + "<font style=\"color: #ea621f\">" + o2.txt(data[0].activityName) + "</font>, " + MWF.xApplication.process.Xform.LP.nextUser + "<font style=\"color: #ea621f\">" + users.join(", ") + "</font></b></div>";


        if (layout.desktop.message) {
            var msg = {
                "subject": MWF.xApplication.process.Xform.LP.workRollback,
                "content": "<div>" + MWF.xApplication.process.Xform.LP.rollbackWorkInfor + "“" + o2.txt(this.businessData.work.title) + "”</div>" + content
            };
            layout.desktop.message.addTooltip(msg);
            return layout.desktop.message.addMessage(msg);
        } else {
            if (this.app.inBrowser) {
                this.inBrowserDkg("<div>" + MWF.xApplication.process.Xform.LP.rollbackWorkInfor + "“" + o2.txt(this.businessData.work.title) + "”</div>" + content);
            }
        }
    },

    addRollbackMessage: function (data) {


        if (layout.desktop.message) {
            var msg = {
                "subject": MWF.xApplication.process.Xform.LP.workRollback,
                "content": this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.rollbackWorkInfor)
            };
            layout.desktop.message.addTooltip(msg);
            return layout.desktop.message.addMessage(msg);
        } else {
            if (this.app.inBrowser) {
                this.inBrowserDkg(this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.rollbackWorkInfor));
            }
        }
    },

    /**
     * 需要判断权限
     * @summary 给待办人发送提醒(催促办理).
     * @example
     * if( this.workContext.getControl().allowPress ){ //判断流程节点是否设置了催办并且当前人员是否有催办权限
     *     this.form.getApp().appForm.pressWork();
     * }
     */
    pressWork: function (e) {
        if (e && e.setDisable) e.setDisable(true);
        o2.Actions.get("x_processplatform_assemble_surface").press(this.businessData.work.id, function (json) {
            var users = o2.name.cns(json.data.valueList).join(", ");
            this.app.notice(MWF.xApplication.process.Xform.LP.sendTaskNotice.replace("{users}", users), "success");
            if (e && e.setDisable) e.setDisable(false);
        }.bind(this), function (xhr, text, error) {
            //e.setDisable(false);
            if (xhr.status != 0) {
                var errorText = error;
                if (xhr) {
                    var json = JSON.decode(xhr.responseText);
                    if (json) {
                        errorText = json.message.trim() || "request json error";
                    } else {
                        errorText = "request json error: " + xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", { x: "right", y: "top" }, errorText);
            }
        });
    },

    /**
     * @summary 退回到之前流转过的活动（根据活动配置列出可退回的活动）.
     * @example
     * this.form.getApp().appForm.goBack();
     */
    goBack: function(){
        if (!this.businessData.control["allowGoBack"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }
        if( !this.businessData.task ){
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, MWF.xApplication.process.Xform.LP.form.noTaskToReset);
            return false;
        }
        if (!this.checkUploadAttachment()) return false;

        o2.Actions.load('x_processplatform_assemble_surface').WorkAction.V2ListActivityGoBack(this.businessData.task.work, function(json){
            var activitys = json.data;
        // var activitys = [{
        //     name: "拟稿",
        //     activity: "123",
        //     way: "custom",
        //     lastModifyTime: "2023-01-23 12:34:12",
        //     lastIdentityList: ["张三", "李四"],
        //     activityTokenList: ["", ""]
        // },{
        //     name: "拟稿",
        //     activity: "345",
        //     way: "jump",
        //     lastModifyTime: "2023-01-23 12:34:12",
        //     lastIdentityList: ["王五六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六", "赵六六"],
        //     activityTokenList: ["", ""]
        // }];
            if (activitys.length){
                var h = this.app.content.getSize().y*0.7-271;
                var size = activitys.length*61;
                h = (size<h) ? size+271 : "70%";
                var _self = this;
                o2.DL.open({
                    "title": this.app.lp.goBack,
                    "style": this.json.dialogStyle || "user", //|| "work",
                    "width":   (layout.mobile) ? "100%" : 680,
                    "height":  (layout.mobile) ? "100%" : h,
                    "url": this.app.path + ( (layout.mobile) ? "goBackMobile" : "goBack") +".html",
                    "lp": o2.xApplication.process.Xform.LP.form,
                    "container": (layout.mobile) ? document.body : this.app.content,
                    "maskNode": this.app.content,
                    "offset": (layout.mobile) ? null : {y: -50},
                    "buttonList": [
                        {
                            "type": "ok",
                            "text": o2.LP.process.button.ok,
                            "action": function (d, e) {
                                _self.doGoBack(this, activitys);
                            }
                        },
                        {
                            "type": "cancel",
                            "text": MWF.LP.process.button.cancel,
                            "action": function () {
                                this.close();
                            }
                        }
                    ],
                    "onPostShow": function () {
                        debugger;
                        var node = this.node.getElement('.activesArea');
                        activitys.forEach(function(a, i){
                            _self.createGoBackActivity(node, a, i);
                        });
                    }

                });
            }
        }.bind(this));
    },

    createGoBackActivity: function(area, activity, i){
        var item = new Element('div.item', {styles: this.css[layout.mobile ? 'goBack_activity_mobile' : 'goBack_activity']}).inject(area);

        var itemCheck = new Element('div', {styles: this.css.goBack_activity_check, html: "<input type='radio' name='goBackActivity' value='"+activity.activity+"' data-text='"+activity.name+"'/>"}).inject(item);
        var radio = itemCheck.getElement('input');

        var itemContent = new Element('div', {styles: this.css.goBack_activity_content}).inject(item);
        var html = activity.name + "<span style='color:#999999; font-size: 12px; margin-left: 10px'>("+activity.lastModifyTime+")</span>"
        var itemName = new Element('div', {styles: this.css.goBack_activity_name, html: html}).inject(itemContent);

        var ids = o2.name.cns(activity.lastIdentityList);
        var idsStr = (ids.length>8) ? ids.slice(0,8).join(', ')+' ...' : ids.join(', ');
        var itemInfo = new Element('div', {styles: this.css.goBack_activity_info, text: '处理人：'+idsStr, title: ids.join(',')}).inject(itemContent);


        item.addEvent("click", function(e){
            // var radio = this.getPrevious('div').getElement('input');

            var radio = this.getElement('input');
            radio.click();

            var items = area.getElements(".item");
            items.each(function(i){
                var actRadio = i.getFirst().getElement('input');
                var wayArea = i.getElement('.wayArea'); //i.getLast().getFirst();
                if (actRadio.checked){
                    if(wayArea)wayArea.getFirst().show();
                    i.addClass('lightColor_bg');
                }else{
                    if(wayArea)wayArea.getFirst().hide();
                    i.removeClass('lightColor_bg');
                }
            });

        });

        var wayRadio = "<div value='"+activity.way+"'></div>";
        if (activity.way==="custom"){
            wayRadio = "<div><div><label style='cursor: pointer'><input type='radio' checked name='"+activity.activity+"goBackWay' value='step'/>"+o2.xApplication.process.Xform.LP.form.goBackActivityWayStep+"</label></div>" +
                "<div><label style='cursor: pointer'><input type='radio' name='"+activity.activity+"goBackWay' value='jump'/>"+o2.xApplication.process.Xform.LP.form.goBackActivityWayJump+"</label></div></div>"
        }
        var itemWay = new Element('div.wayArea', {styles: this.css[ layout.mobile ? 'goBack_activity_way_mobile' : 'goBack_activity_way' ], html:wayRadio}).inject( layout.mobile ? itemContent : item);
        itemWay.getFirst().hide();
    },

    doGoBack: function(dlg, activitys){
        var node = dlg.node;
        debugger;
        var check = node.querySelector('input[name="goBackActivity"]:checked');

        if (!check) {
            this.app.notice(MWF.xApplication.process.Xform.LP.form.selectGoBackActivity, "error", dlg.node);
            return false;
        }

        var wayNode = check.getParent().getParent().getLast().getFirst();

        // var wayNode = node.getElement('.item').getLast().getFirst();
        var wayCheckNode = wayNode.querySelector('input:checked');
        var opinionNode = node.querySelector('textarea');

        var activity = check.value;
        var way = (wayCheckNode) ? wayCheckNode.value : wayNode.value;
        var opinion = opinionNode.value || o2.xApplication.process.Xform.LP.form.goBackTo+check.dataset.text;
        var decision = o2.xApplication.process.Xform.LP.form.goBack;


        this.businessData.task.decision = decision;
        this.businessData.task.opinion = opinion;
        this.businessData.task.action = "goBack";
        this.businessData.task.option = {
            "activity": activity,
            "way": way
        }

        var flowData = {routeName: "", opinion: opinion, activity:activity, way:way, userOpinion:opinionNode.value};
        if( !this.validationOtherFlow('goBack', opinionNode.value, null, flowData) ){
            if (this.mask) { this.mask.hide(); this.mask = null; }
            return;
        }

        this.submitWork(decision, opinion, null, function () {
            dlg.close();
        }.bind(this));
    },


    /**
     * @summary 将待办设置为挂起状态，不计算工作时长.
     * @example
     * this.form.getApp().appForm.pauseTask();
     */
    pauseTask: function (e) {
        if (!this.businessData.control["allowPause"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }

        if (this.businessData.task){
            if (e && e.disable) e.disable(true);
            return o2.Actions.get("x_processplatform_assemble_surface").pauseTask(this.businessData.task.id, function (json) {
                this.app.notice(MWF.xApplication.process.Xform.LP.pauseWork, "success");
                this.businessData.control["allowResume"] = true;
                if (e && e.enable) e.enable(false);
            }.bind(this), function (xhr, text, error) {
                //e.setDisable(false);
                if (xhr.status != 0) {
                    var errorText = error;
                    if (xhr) {
                        var json = JSON.decode(xhr.responseText);
                        if (json) {
                            errorText = json.message.trim() || "request json error";
                        } else {
                            errorText = "request json error: " + xhr.responseText;
                        }
                    }
                    MWF.xDesktop.notice("error", { x: "right", y: "top" }, errorText);
                }
            });
        }
    },

    /**
     * @summary 将待办从挂起状态恢复为正常状态.
     * @example
     * this.form.getApp().appForm.resumeTask();
     */
    resumeTask: function (e) {
        if (!this.businessData.control["allowResume"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }
        if (this.businessData.task){
            if (e && e.disable) e.disable(true);
            return o2.Actions.get("x_processplatform_assemble_surface").resumeTask(this.businessData.task.id, function (json) {
                this.app.notice(MWF.xApplication.process.Xform.LP.resumeWork, "success");
                this.businessData.control["allowPause"] = true;
                if (e && e.enable) e.enable(false);
            }.bind(this), function (xhr, text, error) {
                //e.setDisable(false);
                if (xhr.status != 0) {
                    var errorText = error;
                    if (xhr) {
                        var json = JSON.decode(xhr.responseText);
                        if (json) {
                            errorText = json.message.trim() || "request json error";
                        } else {
                            errorText = "request json error: " + xhr.responseText;
                        }
                    }
                    MWF.xDesktop.notice("error", { x: "right", y: "top" }, errorText);
                }
            });
        }
    },

    downloadAll: function () {

        var htmlFormId = "";
        var html = this.app.content.get("html");
        var port = layout.port === "" ? "" : ":" + port;

        html = html.replace(/\.\.\/(x_|o2_)/g, "http://127.0.0.1" + port + "/$1");

        o2.Actions.load("x_processplatform_assemble_surface").AttachmentAction.uploadWorkInfo(this.businessData.work.id, "pdf", {
            "workHtml": encodeURIComponent(html),
            "pageWidth": 1000
        }, function (json) {
            htmlFormId = json.data.id;
        }.bind(this), null, false);
        htmlFormId = htmlFormId.replace("#", "%23");
        var url = "/x_processplatform_assemble_surface/jaxrs/attachment/batch/download/work/" + this.businessData.work.id + "/site/(0)/stream";
        url = o2.filterUrl(o2.Actions.getHost("x_processplatform_assemble_surface") + url);

        var downloadUrl = o2.filterUrl(url + "?fileName=&flag=" + htmlFormId);
        if ((o2.thirdparty.isDingdingPC() || o2.thirdparty.isQywxPC())) {
            var xtoken = layout.session.token;
            downloadUrl += "&" + o2.tokenName + "=" + xtoken;
        }
        window.open(downloadUrl);
    },
    monitor: function () {

        var node = new Element("div");
        var container = new Element("div").inject(node);
        var monitor;
        var monitorDlg = o2.DL.open({
            "title": MWF.xApplication.process.Xform.LP.monitor,
            "width": "1100",
            "isResize" : true,
            "height" : "720px",
            "maxHeightPercent" : "98%",
            "mask": true,
            "isMax" : true,
            "content": node,
            "container": this.app.content,
            "maskNode": this.app.content,
            "onQueryClose": function(){

            }.bind(this),
            "buttonList": [
                {
                    "text": MWF.xApplication.process.Xform.LP.close,
                    "action": function(){
                        monitorDlg.close();
                    }.bind(this)
                }
            ],
            "onMax" : function(){
                monitor.logProcessChartNode.setStyle("height",this.contentHeight+"px");
                monitor.paperNode.setStyle("height",(this.contentHeight - 60)+"px");
            },
            "onRestore" : function(){
                monitor.logProcessChartNode.setStyle("height",this.contentHeight+"px");
                monitor.paperNode.setStyle("height",(this.contentHeight - 60)+"px");
            },
            "onPostShow": function(){

                var dlg = monitorDlg;
                var size = dlg.content.getSize();
                dlg.options.contentWidth = size.x;
                dlg.options.contentHeight = size.y; //+100;
                dlg.setContentSize();
                dlg.node.setStyles({
                    "height": ""+dlg.options.height+"px",
                    "width": ""+(dlg.options.width)+"px"
                });
                dlg.reCenter();
                dlg.content.setStyle("overflow","hidden");


                MWF.xDesktop.requireApp("process.Xform", "widget.Monitor", null, false);
                var process = (this.businessData.work) ? this.businessData.work.process : this.businessData.workCompleted.process;

                monitor = new MWF.xApplication.process.Xform.widget.Monitor(container, this.businessData.workLogList, this.businessData.recordList,process,{
                    onPostLoad : function(){

                        monitor.paperNode.setStyles({
                            "box-shadow":"none",
                            "margin-bottom" : "0px"
                        });
                        var logProcessChartNode =  monitor.logProcessChartNode;
                        logProcessChartNode.setStyle("border","0px");

                        logProcessChartNode.setStyle("height",(size.y) +"px");
                        monitor.paperNode.setStyle("height",(size.y-48)+"px");

                    }.bind(this)
                });

            }.bind(this)
        });

    },
    resetWork: function () {
        if (!this.businessData.control["allowReset"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }
        if (!this.checkUploadAttachment()) return false;

        if( !this.businessData.task ){
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, MWF.xApplication.process.Xform.LP.form.noTaskToReset);
            return false;
        }
        MWF.require("MWF.xDesktop.Dialog", function () {
            var width = 680;
            var height = 300;
            var p;
            if(!layout.mobile)p = MWF.getCenterPosition(this.app.content, width, height);

            var _self = this;
            o2.DL.open({
                "title": this.app.lp.reset,
                "style": this.json.dialogStyle || "user", //|| "work",
                "top": (layout.mobile) ? 0 : (p.y - 100),
                "left": (layout.mobile) ? 0 : p.x,
                "fromTop": (layout.mobile) ? 0 : (p.y - 100),
                "fromLeft": (layout.mobile) ? 0 : p.x,
                "width": (layout.mobile) ? "100%" : width,
                "height": (layout.mobile) ? "100%" : height,
                "url": this.app.path + ( (layout.mobile) ? "resetMobile" : "reset") + ".html",
                "lp": MWF.xApplication.process.Xform.LP.form,
                "container": (layout.mobile) ? document.body : this.app.content,
                "isClose": !(layout.mobile),
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": function (d, e) {
                            _self.doResetWork(this);
                        }
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () { this.close(); }
                    }
                ],
                "onPostShow": function () {
                    //$("resetWork_okButton").addEvent("click", function(){
                    //    _self.doResetWork(this);
                    //}.bind(this));
                    //$("resetWork_cancelButton").addEvent("click", function(){
                    //    this.close();
                    //}.bind(this));

                    $("resetWork_selPeopleButton").addEvent("click", function () {
                        _self.selectPeople(this);
                    }.bind(this));
                }
            });
        }.bind(this));
    },
    selectPeople: function(dlg){
        // o2.Actions.get("x_processplatform_assemble_surface").listTaskByWork(this.businessData.work.id, function(json){
            var identityList = [];
            // json.data.each(function(task){
            //     identityList.push(task.identity);
            // });
            this._selectPeople(dlg, identityList);
        // }.bind(this))
    },
    _selectPeople: function (dlg, exclude) {
        var range = this.businessData.activity.resetRange || "department";
        var count = this.businessData.activity.resetCount || 0;
        switch (range) {
            case "unit":
                this.selectPeopleUnit(dlg, this.businessData.task.unitDn, count, null,  exclude);
                // this.personActions.getDepartmentByIdentity(function(json){
                //     this.selectPeopleDepartment(dlg, json.data, count);
                // }.bind(this), null, this.businessData.task.identity);
                break;
            case "topUnit":
                MWF.require("MWF.xScript.Actions.UnitActions", function () {
                    orgActions = new MWF.xScript.Actions.UnitActions();
                    var data = { "unitList": [this.businessData.task.unitDn] };
                    orgActions.listUnitSupNested(data, function (json) {
                        v = json.data[0];
                        this.selectPeopleUnit(dlg, v, count, null, exclude);
                    }.bind(this));
                }.bind(this));
                // this.personActions.getCompanyByIdentity(function(json){
                //     this.selectPeopleCompany(dlg, json.data, count)
                // }.bind(this), null, this.businessData.task.identity);
                break;
            case "script":
                o2.Actions.load("x_processplatform_assemble_surface").ProcessAction.getActivity(this.businessData.work.activity, "manual", function (activityJson) {
                    var scriptText = activityJson.data.activity.resetRangeScriptText;
                    if (!scriptText) return;
                    var resetRange = this.Macro.exec(activityJson.data.activity.resetRangeScriptText, this);
                    this.selectPeopleUnit(dlg, "", count, resetRange, exclude);
                }.bind(this))
                break;
            default:
                this.selectPeopleAll(dlg, count);
        }
    },
    selectPeopleUnit: function (dlg, unit, count, include, exclude) {
        var names = dlg.identityList || [];
        var areaNode = $("resetWork_selPeopleArea");
        var options = {
            "values": names,
            "type": "identity",
            "count": count,
            "units": (unit) ? [unit] : [],
            "onComplete": function (items) {
                areaNode.empty();
                var identityList = [];
                items.each(function (item) {
                    new MWF.widget.O2Identity(item.data, areaNode, { "style": "reset" });
                    identityList.push(item.data.distinguishedName);
                }.bind(this));
                dlg.identityList = identityList;
            }.bind(this)
        };
        if (include) {
            options.noUnit = true;
            options.include = typeOf(include) === "array" ? include : [include];
        }
        if( exclude ){
            options.exclude = exclude;
        }
        MWF.xDesktop.requireApp("Selector", "package", function () {
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));

    },
    selectPeopleAll: function (dlg, count) {
        var names = dlg.identityList || [];
        var areaNode = $("resetWork_selPeopleArea");
        var options = {
            "values": names,
            "type": "identity",
            "count": count,
            "onComplete": function (items) {
                areaNode.empty();
                var identityList = [];
                items.each(function (item) {
                    new MWF.widget.O2Identity(item.data, areaNode, { "style": "reset" });
                    identityList.push(item.data.distinguishedName);
                }.bind(this));
                dlg.identityList = identityList;
            }.bind(this)
        };
        MWF.xDesktop.requireApp("Selector", "package", function () {
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));

    },


    doResetWork: function (dlg) {
        var names = dlg.identityList || [];
        if (!names.length) {
            this.app.notice(MWF.xApplication.process.Xform.LP.inputResetPeople, "error", dlg.node);
            return false;
        }
        var opinion = $("resetWork_opinion").get("value");
        // var checkbox = dlg.content.getElement(".resetWork_keepOption");
        // var keep = (checkbox.checked);

        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            this.mask.loadNode(this.app.content);

            var data = {routeName: "", opinion: opinion, names:names, userOpinion:opinion};

            if( !this.validationOtherFlow('reset', opinion, null, data) ){
                if (this.mask) { this.mask.hide(); this.mask = null; }
                return;
            }

            this.fireEvent("beforeReset", data);
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeReset");

            this.resetWorkToPeson(names, opinion, "", function (workJson) {
                //this.workAction.loadWork(function (workJson) {
                this.fireEvent("afterReset", data);
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterReset");
                this.addResetMessage(workJson.data);
                //this.app.notice(MWF.xApplication.process.Xform.LP.resetOk + ": " + MWF.name.cns(names).join(", "), "success");
                if (!this.app.inBrowser) this.app.close();
                //}.bind(this), null, this.businessData.work.id);
                dlg.close();
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), function (xhr, text, error) {
                var errorText = error + ":" + text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: " + errorText, "error", dlg.node);
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this));
        }.bind(this));

        //var data = {
        //    "opinion": opinion,
        //    "routeName": MWF.xApplication.process.Xform.LP.reset,
        //    "identityList": names
        //}
        //
        //this.workAction.resetWork(function(json){
        //
        //}.bind(this), null, this.businessData.task.id, data);
    },
    resetWorkToPeson: function (identityList, opinion, routeName, success, failure) {

        var nameText = [];
        (identityList || []).each(function (n) { nameText.push(MWF.name.cn(n)); });
        if (!opinion) {
            opinion = MWF.xApplication.process.Xform.LP.resetTo + ": " + nameText.join(", ");
        }

        var n = nameText.length > 3 ? (nameText[0]+"、"+nameText[1]+"、"+nameText[2]+"...") : nameText.join(", ");
        if(!routeName)routeName = MWF.xApplication.process.Xform.LP.resetTo+":"+n;

        var data = {
            "opinion": opinion,
            "routeName": routeName,
            "identityList": identityList,
            //"keep": !!keep
        };
        this.saveFormData(
            function (json) {
                o2.Actions.load("x_processplatform_assemble_surface").TaskAction.V2Reset(
                    //this.workAction.resetWork(
                    function (json) {
                        if (success) success(json);
                    }.bind(this),
                    function (xhr, text, error) {
                        if (failure) failure(xhr, text, error);
                    },
                    this.businessData.task.id, data
                );
            }.bind(this),
            function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            }, true, null, true
        );
    },
    addAddSplitMessage: function (data) {
        // var content = "";
        // if (data && data.length) {
        //     data.each(function (work) {
        //         var users = [];
        //         work.taskList.each(function (task) {
        //             users.push(MWF.name.cn(task.person) + "(" + MWF.name.cn(task.unit) + ")");
        //         }.bind(this));
        //         content += "<div><b>" + MWF.xApplication.process.Xform.LP.nextActivity + "<font style=\"color: #ea621f\">" + work.activityName + "</font>, " + MWF.xApplication.process.Xform.LP.nextUser + "<font style=\"color: #ea621f\">" + users.join(", ") + "</font></b></div>";
        //     }.bind(this));
        // } else {
        //     content += MWF.xApplication.process.Xform.LP.workCompleted;
        // }

        if (layout.desktop.message) {
            //var content = "<div><b>"+MWF.xApplication.process.Xform.LP.currentActivity+"<font style=\"color: #ea621f\">"+data.work.activityName+"</font>, "+MWF.xApplication.process.Xform.LP.nextUser+"<font style=\"color: #ea621f\">"+users.join(", ")+"</font></b></div>";
            var msg = {
                "subject": MWF.xApplication.process.Xform.LP.addSplitWork,
                "content": this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.addSplitWorkInfor)
            };
            layout.desktop.message.addTooltip(msg);
            return layout.desktop.message.addMessage(msg);
        } else {
            if (this.app.inBrowser) {
                this.inBrowserDkg(this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.addSplitWorkInfor));
            }
        }
    },
    addResetMessage: function (data) {
        // var content = "";
        // if (data.completed){
        //     content += MWF.xApplication.process.Xform.LP.workCompleted;
        // }else{
        //     if (data.properties.nextManualList && data.properties.nextManualList.length){
        //         var activityUsers = [];
        //         data.properties.nextManualList.each(function(a){
        //             var ids = [];
        //             a.taskIdentityList.each(function(i){
        //                 ids.push(o2.name.cn(i))
        //             });
        //             var t = "<b>"+MWF.xApplication.process.Xform.LP.nextActivity + "</b><span style='color: #ea621f'>"+a.activityName+"</span>；<b>"+ MWF.xApplication.process.Xform.LP.nextUser+ "</b><span style='color: #ea621f'>"+ids.join(",")+"</span>";
        //             activityUsers.push(t);
        //         });
        //         content += activityUsers.join("<br>");
        //     }else{
        //         content += MWF.xApplication.process.Xform.LP.taskCompleted;
        //     }
        // }

        if (layout.desktop.message) {
            var msg = {
                "subject": MWF.xApplication.process.Xform.LP.workReset,
                "content": this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.resetWorkInfor)
            };
            layout.desktop.message.addTooltip(msg);
            return layout.desktop.message.addMessage(msg);
        } else {
            if (this.app.inBrowser) {
                this.inBrowserDkg(this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.resetWorkInfor));
            }
        }
    },

    retractWork: function (e, ev) {
        var _self = this;
        if (this.json.mode == "Mobile") {
            //window.confirm 在ios移动端不可用 ??
            // if (window.confirm(MWF.xApplication.process.Xform.LP.retractText)) {

            var p = MWF.getCenterPosition(document.body, 300, 150);
            console.log("position x:" + p.x + " , y:" + p.y);
            var x = p.x;
            if (p.x < 20) {
                x = 20;
            } else {
                x = p.x;
            }
            var event = {
                "event": {
                    "x": x,
                    "y": p.y - 200,
                    "clientX": x,
                    "clientY": p.y - 200
                }
            };
            this.app.confirm("infor", event, MWF.xApplication.process.Xform.LP.retractTitle, MWF.xApplication.process.Xform.LP.retractText, 300, 120, function () {
                _self.app.content.mask({
                    "style": {
                        "background-color": "#999",
                        "opacity": 0.6
                    }
                });

                MWF.require("MWF.widget.Mask", function () {
                    _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                    _self.mask.loadNode(_self.app.content);

                    _self.fireEvent("beforeRetract");
                    if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeRetract");

                    _self.doRetractWork(function () {
                        //_self.workAction.getJobByWork(function(workJson){
                        _self.fireEvent("afterRetract");
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterRetract");
                        _self.app.notice(MWF.xApplication.process.Xform.LP.workRetract, "success");
                        _self.app.content.unmask();
                        if (_self.mask) { _self.mask.hide(); _self.mask = null; }

                        _self.finishOnMobile();
                    }.bind(this), function (xhr, text, error) {
                        _self.app.content.unmask();
                        var errorText = error + ":" + text;
                        if (xhr) errorText = xhr.responseText;
                        _self.app.notice("request json error: " + errorText, "error");
                        if (_self.mask) { _self.mask.hide(); _self.mask = null; }
                    });
                }.bind(this));
            }, function () {
                this.close();
            }, null, null, this.json.confirmStyle);





        } else {
            var p = MWF.getCenterPosition(this.app.content, 300, 150);
            var event = {
                "event": {
                    "x": p.x,
                    "y": p.y - 200,
                    "clientX": p.x,
                    "clientY": p.y - 200
                }
            };
            this.app.confirm("infor", event, MWF.xApplication.process.Xform.LP.retractTitle, MWF.xApplication.process.Xform.LP.retractText, 300, 120, function () {
                _self.app.content.mask({
                    "style": {
                        "background-color": "#999",
                        "opacity": 0.6
                    }
                });

                MWF.require("MWF.widget.Mask", function () {
                    _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                    _self.mask.loadNode(_self.app.content);

                    _self.fireEvent("beforeRetract");
                    if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeRetract");

                    _self.doRetractWork(function (json) {
                        //_self.workAction.getJobByWork(function(workJson){
                        _self.fireEvent("afterRetract");
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterRetract");
                        //_self.addRetractMessage(json.data);
                        _self.app.notice(MWF.xApplication.process.Xform.LP.workRetract, "success");
                        _self.app.content.unmask();
                        if (_self.mask) { _self.mask.hide(); _self.mask = null; }
                        _self.app.reload();
                        //}, null, _self.businessData.work.id);
                        this.close();
                    }.bind(this), function (xhr, text, error) {
                        _self.app.content.unmask();
                        var errorText = error + ":" + text;
                        if (xhr) errorText = xhr.responseText;
                        _self.app.notice("request json error: " + errorText, "error");
                        if (_self.mask) { _self.mask.hide(); _self.mask = null; }
                    });
                }.bind(this));

                //this.close();
            }, function () {
                this.close();
            }, null, null, this.json.confirmStyle);
        }
    },
    doRetractWork: function (success, failure) {
        if (this.businessData.control["allowRetract"]) {
            o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2Retract(this.businessData.work.id, null, function (json) {
                if (success) success(json);
            }.bind(this), function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            });
            // this.workAction.retractWork(function (json) {
            //     if (success) success();
            // }.bind(this), function (xhr, text, error) {
            //     if (failure) failure(xhr, text, error);
            // }, this.businessData.work.id);
        } else {
            if (failure) failure(null, "Permission Denied", "");
        }
    },
    addRetractMessage: function (data) {
        // var users = [];
        // data.taskList.each(function (task) {
        //     users.push(MWF.name.cn(task.person) + "(" + MWF.name.cn(task.unit) + ")");
        // }.bind(this));
        // var content = "<div><b>" + MWF.xApplication.process.Xform.LP.currentActivity + "<font style=\"color: #ea621f\">" + data.work.activityName + "</font>, " + MWF.xApplication.process.Xform.LP.nextUser + "<font style=\"color: #ea621f\">" + users.join(", ") + "</font></b></div>";

        if (layout.desktop.message) {
            var msg = {
                "subject": MWF.xApplication.process.Xform.LP.workRetract,
                "content": this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.retractWorkInfor)
            };
            layout.desktop.message.addTooltip(msg);
            return layout.desktop.message.addMessage(msg);
        } else {
            if (this.app.inBrowser) {
                this.inBrowserDkg(this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.retractWorkInfor));
            }
        }
    },
    /**
     * 如果当前人员没有调度权限或者流程节点未配置调度，则提醒Permission Denied.
     * @summary 弹出调度界面
     * @example
     * this.form.getApp().appForm.rerouteWork();
     */
    rerouteWork: function (e, ev) {
        if (!this.businessData.control["allowReroute"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }
        if (!this.checkUploadAttachment()) return false;

        MWF.require("MWF.xDesktop.Dialog", function () {
            var width = 660;
            var height = 350;
            var p = MWF.getCenterPosition(this.app.content, width, height);

            var _self = this;
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.app.lp.reroute,
                "style": this.json.dialogStyle || "user", //|| "work",
                "top": p.y - 100,
                "left": p.x,
                "fromTop": p.y - 100,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "url": this.app.path + "reroute.html",
                "lp": MWF.xApplication.process.Xform.LP.form,
                "container": this.app.content,
                "isClose": true,
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": function (d, e) {
                            _self.doRerouteWork(dlg);
                        }.bind(this)
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    o2.Actions.load("x_processplatform_assemble_surface").JobAction.findWorkWorkCompleted( _self.businessData.work.job, function(json){
                        if( json.data.workList && json.data.workList.length > 1 ){
                            var checkbox = this.node.getElement(".rerouteWork_mergeWork");
                            if(checkbox)checkbox.getParent().getParent().show();
                        }
                    }.bind(this));

                    var select = $("rerouteWork_selectActivity");
                    var createActivityOption = function(list, name){
                        list.each(function (activity) {
                            var activityType = name.replace("List", "");
                            new Element("option", {
                                "value": activity.id + "#"+activityType,
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));
                    }
                    _self.workAction.getRerouteTo(_self.businessData.work.process, function (json) {
                        [
                            "agentList",
                            "cancelList",
                            "choiceList",
                            "delayList",
                            "embedList",
                            "publishList",
                            "endList",
                            "invokeList",
                            "manualList",
                            "mergeList",
                            "parallelList",
                            "serviceList",
                            "splitList"
                        ].forEach(function(name){
                            createActivityOption(json.data[name], name);
                        });
                    }.bind(_self));

                    var selPeopleButton = this.content.getElement(".rerouteWork_selPeopleButton");
                    selPeopleButton.addEvent("click", function () {
                        _self.selectReroutePeople(this);
                    }.bind(this));
                }
            });
            dlg.show();
        }.bind(this));
    },
    selectReroutePeople: function (dlg) {
        var names = dlg.identityList || [];
        var areaNode = dlg.content.getElement(".rerouteWork_selPeopleArea");
        var options = {
            "values": names,
            "type": "identity",
            "count": 0,
            "title": this.app.lp.reroute,
            "onComplete": function (items) {
                areaNode.empty();
                var identityList = [];
                items.each(function (item) {
                    new MWF.widget.O2Identity(item.data, areaNode, { "style": "reset" });
                    identityList.push(item.data.distinguishedName);
                }.bind(this));
                dlg.identityList = identityList;
            }.bind(this)
        };
        MWF.xDesktop.requireApp("Selector", "package", function () {
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    doRerouteWork: function (dlg) {
        var opinion = $("rerouteWork_opinion").get("value");
        var select = $("rerouteWork_selectActivity");

        debugger;
        var checkbox = dlg.node.getElement(".rerouteWork_mergeWork");
        var mergeWork = !!(checkbox && checkbox.checked);

        var activity = select.options[select.selectedIndex].get("value");
        var activityName = select.options[select.selectedIndex].get("text");
        var tmp = activity.split("#");
        activity = tmp[0];
        var type = tmp[1];


        var nameArr = [];
        var names = dlg.identityList || [];
        names.each(function (n) { nameArr.push(n); });

        var nameCNs = names.map(function(id){
            return o2.name.cn(id);
        });

        var routeName = MWF.xApplication.process.Xform.LP.rerouteTo + activityName;
        if( nameArr.length ){
            routeName += ":" + nameCNs.join(", ");
        }

        if( !opinion )opinion = routeName;

        //var nameText = nameArr.join(", ");
        // if (!opinion) {
        //     opinion = MWF.xApplication.process.Xform.LP.resetTo + ": " + nameText.join(", ");
        // }

        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            this.mask.loadNode(this.app.content);

            this.fireEvent("beforeReroute");
            if (this.app && this.app.fireEvent) this.app.fireEvent("afterRetract");

            this.rerouteWorkToActivity(activity, type, opinion, nameArr, function (workJson) {
                //this.workAction.loadWork(function (workJson) {
                this.fireEvent("afterReroute");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterReroute");
                this.addRerouteMessage(workJson.data);
                this.app.notice(MWF.xApplication.process.Xform.LP.rerouteOk + ": " + activityName, "success");
                if (!this.app.inBrowser) this.app.close();
                //}.bind(this), null, this.businessData.work.id);
                dlg.close();
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), function (xhr, text, error) {
                var errorText = error + ":" + text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: " + errorText, "error", dlg.node);
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), mergeWork, routeName);
        }.bind(this));
    },
    rerouteWorkToActivity: function (activity, type, opinion, nameArr, success, failure, mergeWork, routeName) {
        var body = {
            "activity": activity,
            "activityType": type,
            "mergeWork": mergeWork || false,
            "distinguishedNameList": nameArr,
            "routeName": routeName,
            "opinion": opinion
        };
        if (this.businessData.task) {
            this.saveFormData(function (json) {
                o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2Reroute(this.businessData.work.id, body, function (json) {
                    if (success) success(json);
                }.bind(this), function (xhr, text, error) {
                    if (failure) failure(xhr, text, error);
                });
                // this.workAction.rerouteWork(function (json) {
                //     if (success) success();
                // }.bind(this), function (xhr, text, error) {
                //     if (failure) failure(xhr, text, error);
                // }, this.businessData.work.id, activity, type);
            }.bind(this), function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            }, true, null, true);
        } else {
            o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2Reroute(this.businessData.work.id, body, function (json) {
                if (success) success(json);
            }.bind(this), function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            });

            // this.workAction.rerouteWork(function (json) {
            //     if (success) success();
            // }.bind(this), function (xhr, text, error) {
            //     if (failure) failure(xhr, text, error);
            // }, this.businessData.work.id, activity, type);
        }
    },
    addRerouteMessage: function (data) {

        if (layout.desktop.message) {
            var msg = {
                "subject": MWF.xApplication.process.Xform.LP.workReroute,
                "content": this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.rerouteWorkInfor)
            };
            layout.desktop.message.addTooltip(msg);
            return layout.desktop.message.addMessage(msg);
        } else {
            if (this.app.inBrowser) {
                this.inBrowserDkg(this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.rerouteWorkInfor));
            }
        }
    },

    deleteDraftWork: function () {
        var _self = this;
        if (this.json.mode === "Mobile") {
            var p = MWF.getCenterPosition(document.body, 300, 150);
            console.log("position x:" + p.x + " , y:" + p.y);
            var x = p.x;
            if (p.x < 20) {
                x = 20;
            } else {
                x = p.x;
            }
            var event = {
                "event": {
                    "x": x,
                    "y": p.y - 200,
                    "clientX": x,
                    "clientY": p.y - 200
                }
            };
            this.app.confirm("infor", event, MWF.xApplication.process.Xform.LP.deleteWorkTitle, MWF.xApplication.process.Xform.LP.deleteWorkText.text, 300, 120, function () {
                _self.app.content.mask({
                    "style": {
                        "background-color": "#999",
                        "opacity": 0.6
                    }
                });
                // if (window.confirm(MWF.xApplication.process.Xform.LP.deleteWorkText.text)) {
                MWF.require("MWF.widget.Mask", function () {
                    _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                    _self.mask.loadNode(_self.app.content);

                    _self.doDeleteWork(function () {
                        _self.app.notice(MWF.xApplication.process.Xform.LP.workDelete + ": “" + _self.businessData.work.title + "”", "success");
                        if (_self.mask) {
                            _self.mask.hide();
                            _self.mask = null;
                        }
                        _self.finishOnMobile()
                    }.bind(this), function (xhr, text, error) {
                        var errorText = error + ":" + text;
                        if (xhr) errorText = xhr.responseText;
                        _self.app.notice("request json error: " + errorText, "error");
                        if (_self.mask) {
                            _self.mask.hide();
                            _self.mask = null;
                        }
                    }.bind(this));
                }.bind(this));
            }, function () {
                this.close();
            }, null, null, this.json.confirmStyle);
        } else {
            var p = MWF.getCenterPosition(this.app.content, 380, 150);
            var event = {
                "event": {
                    "x": p.x,
                    "y": p.y - 200,
                    "clientX": p.x,
                    "clientY": p.y - 200
                }
            };
            this.app.confirm("infor", event, MWF.xApplication.process.Xform.LP.deleteWorkTitle, MWF.xApplication.process.Xform.LP.deleteWorkText, 380, 120, function () {
                MWF.require("MWF.widget.Mask", function () {
                    _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                    _self.mask.loadNode(_self.app.content);

                    _self.doDeleteWork(function () {
                        _self.app.notice(MWF.xApplication.process.Xform.LP.workDelete + ": “" + _self.businessData.work.title + "”", "success");
                        _self.app.close();
                        this.close();
                        if (_self.mask) {
                            _self.mask.hide();
                            _self.mask = null;
                        }
                    }.bind(this), function (xhr, text, error) {
                        var errorText = error + ":" + text;
                        if (xhr) errorText = xhr.responseText;
                        _self.app.notice("request json error: " + errorText, "error");
                        if (_self.mask) {
                            _self.mask.hide();
                            _self.mask = null;
                        }
                    }.bind(this));
                }.bind(this));
            }, function () {
                this.close();
            }, null, this.app.content, this.json.confirmStyle);
        }
    },
    deleteWork: function () {
        if (!this.businessData.work.startTime) {
            this.deleteDraftWork();
        } else {
            var _self = this;
            if (this.json.mode === "Mobile") {
                var p = MWF.getCenterPosition(document.body, 300, 150);
                console.log("position x:" + p.x + " , y:" + p.y);
                var x = p.x;
                if (p.x < 20) {
                    x = 20;
                } else {
                    x = p.x;
                }
                var event = {
                    "event": {
                        "x": x,
                        "y": p.y - 200,
                        "clientX": x,
                        "clientY": p.y - 200
                    }
                };
                this.app.confirm("infor", event, MWF.xApplication.process.Xform.LP.deleteWorkTitle, MWF.xApplication.process.Xform.LP.deleteWorkText.text, 300, 120, function () {
                    _self.app.content.mask({
                        "style": {
                            "background-color": "#999",
                            "opacity": 0.6
                        }
                    });
                    // if (window.confirm(MWF.xApplication.process.Xform.LP.deleteWorkText.text)) {
                    MWF.require("MWF.widget.Mask", function () {
                        _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                        _self.mask.loadNode(_self.app.content);

                        _self.fireEvent("beforeDelete");
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeDelete");

                        _self.doDeleteWork(function () {
                            _self.fireEvent("afterDelete");
                            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterDelete");
                            _self.app.notice(MWF.xApplication.process.Xform.LP.workDelete + ": “" + _self.businessData.work.title + "”", "success");
                            if (_self.mask) {
                                _self.mask.hide();
                                _self.mask = null;
                            }
                            _self.finishOnMobile()
                        }.bind(this), function (xhr, text, error) {
                            var errorText = error + ":" + text;
                            if (xhr) errorText = xhr.responseText;
                            _self.app.notice("request json error: " + errorText, "error", dlg.node);
                            if (_self.mask) {
                                _self.mask.hide();
                                _self.mask = null;
                            }
                        }.bind(this));
                    }.bind(this));
                }, function () {
                    this.close();
                }, null, this.app.content, this.json.confirmStyle);
            } else {
                var p = MWF.getCenterPosition(this.app.content, 380, 150);
                var event = {
                    "event": {
                        "x": p.x,
                        "y": p.y - 200,
                        "clientX": p.x,
                        "clientY": p.y - 200
                    }
                };
                this.app.confirm("infor", event, MWF.xApplication.process.Xform.LP.deleteWorkTitle, MWF.xApplication.process.Xform.LP.deleteWorkText, 380, 120, function () {
                    // _self.app.content.mask({
                    //    "style": {
                    //        "background-color": "#999",
                    //        "opacity": 0.6
                    //    }
                    // });


                    MWF.require("MWF.widget.Mask", function () {
                        _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                        _self.mask.loadNode(_self.app.content);

                        _self.fireEvent("beforeDelete");
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeDelete");

                        _self.doDeleteWork(function () {
                            _self.fireEvent("afterDelete");
                            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterDelete");
                            _self.app.notice(MWF.xApplication.process.Xform.LP.workDelete + ": “" + _self.businessData.work.title + "”", "success");
                            _self.app.close();
                            this.close();
                            if (_self.mask) {
                                _self.mask.hide();
                                _self.mask = null;
                            }
                        }.bind(this), function (xhr, text, error) {
                            var errorText = error + ":" + text;
                            if (xhr) errorText = xhr.responseText;
                            _self.app.notice("request json error: " + errorText, "error", dlg.node);
                            if (_self.mask) {
                                _self.mask.hide();
                                _self.mask = null;
                            }
                        }.bind(this));
                    }.bind(this));


                    //_self.workAction.deleteWork(function(json){
                    //    _self.app.notice(MWF.xApplication.process.Xform.LP.workDelete+": “"+_self.businessData.work.title+"”", "success");
                    //    _self.app.close();
                    //    this.close();
                    //}.bind(this), null, _self.businessData.work.id);
                    //this.close();
                }, function () {
                    this.close();
                }, null, this.app.content, this.json.confirmStyle);
            }
        }
    },
    doDeleteDraftWork: function (success, failure) {
        this.workAction.deleteDraftWork(function (json) {
            if (success) success(json);
        }.bind(this), function (xhr, text, error) {
            if (failure) failure(xhr, text, error);
        }, this.businessData.work.id);
    },
    doDeleteWork: function (success, failure) {
        if (!this.businessData.work.startTime) {
            this.doDeleteDraftWork(success, failure);
        } else {
            if (this.businessData.control["allowDelete"]) {
                //this.workAction.deleteWork(function (json) {
                this.workAction.abandoned(function (json) {
                    if (success) success(json);
                }.bind(this), function (xhr, text, error) {
                    if (failure) failure(xhr, text, error);
                }, this.businessData.work.id);
            //}
            }else {
                if (failure) failure(null, "Permission Denied", "");
            }
        }
    },

    //printWork: function(){
    //    var form = this.json.id;
    //    if (this.json.printForm){
    //        form = this.json.printForm;
    //    }
    //    window.open("../x_desktop/printWork.html?workid="+this.businessData.work.id+"&app="+this.businessData.work.application+"&form="+form);
    //},
    printWork: function (app, form) {
        var application = app || (this.businessData.work) ? this.businessData.work.application : this.businessData.workCompleted.application;
        var form = form;
        if (!form || form === "none") {
            form = this.json.id;
            if (this.json.printForm && this.json.printForm!=="none" ) form = this.json.printForm;
        }
        if (this.businessData.workCompleted) {
            var application = app || this.businessData.workCompleted.application;
            var url = o2.filterUrl("../x_desktop/printWork.html?workCompletedId=" + this.businessData.workCompleted.id + "&app=" + application + "&form=" + form);
            if ((o2.thirdparty.isDingdingPC() || o2.thirdparty.isQywxPC())) {
                var xtoken = layout.session.token;
                url += "&" + o2.tokenName + "=" + xtoken;
            }
            window.open(url);
        } else {
            var application = app || this.businessData.work.application;
            var url = o2.filterUrl("../x_desktop/printWork.html?workid=" + this.businessData.work.id + "&app=" + application + "&form=" + form);
            if ((o2.thirdparty.isDingdingPC() || o2.thirdparty.isQywxPC())) {
                var xtoken = layout.session.token;
                url += "&" + o2.tokenName + "=" + xtoken;
            }
            window.open(url);
        }
    },
    /**
     * @summary 将当前处理人的待阅设置为已阅.
     * @param {Event|Element} [e] - Event 或者Mootools Element，指定提示框弹出的位置
     * @example
     * if( this.workContext.getControl().allowReadProcessing ){ //是否有待阅
     *     this.form.getApp().appForm.readedWork();
     * }
     */
    readedWork: function (e) {
        if (!this.businessData.control["allowReadProcessing"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }
        MWF.require("MWF.xDesktop.Dialog", function () {
            // 判断是否在移动端
            var content = layout.mobile ? $(document.body) : this.app.content || $(document.body);
			var size = content.getSize();

            var width = 680;
            var height = 300;
            if (layout.mobile) {
                width = size.x - 40;
            }

            var p = MWF.getCenterPosition(content, width, height);

            var _self = this;

            //"您确定要将“" + title + "”标记为已阅吗？";
            var title = this.businessData.work.title;
            var text = MWF.xApplication.process.Xform.LP.setReadedConfirmContent.replace("{title}",title);
            MWF.xApplication.process.Xform.LP.form.setReadedConfirmInfo = text;

            var dlg = new MWF.xDesktop.Dialog({
                "title": MWF.xApplication.process.Xform.LP.setReadedConfirmTitle,
                "style": this.json.dialogStyle || "user", //|| "work",
                "top": layout.mobile ? p.y : p.y - 100,
                "left": p.x,
                "fromTop": layout.mobile ? p.y : p.y - 100,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "url": this.app.path + ( layout.mobile ? "readedmobile.html" : "readed.html" ),
                "lp": MWF.xApplication.process.Xform.LP.form,
                "container": layout.mobile ? content : this.app.content,
                "isClose": true,
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": function (d, e) {
                            this.doReadedWork(dlg);
                        }.bind(this)
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () { dlg.close(); }
                    }
                ]
            });
            dlg.show();
        }.bind(this));

        // if( !e )e = new Event(event);
        // this.fireEvent("beforeReaded");
        // var _self = this;
        // var title = this.businessData.work.title;
        // if (title.length > 75) {
        //     title = title.substr(0, 74) + "..."
        // }
        // //"您确定要将“" + title + "”标记为已阅吗？";
        // var text = MWF.xApplication.process.Xform.LP.setReadedConfirmContent.replace("{title}",title);
        //
        // this.app.confirm("infor", e,  MWF.xApplication.process.Xform.LP.setReadedConfirmTitle, text, 300, 120, function () {
        //     var confirmDlg = this;
        //     var read = null;
        //     for (var i = 0; i < _self.businessData.readList.length; i++) {
        //         if (_self.businessData.readList[i].person === layout.session.user.distinguishedName) {
        //             read = _self.businessData.readList[i];
        //             break;
        //         }
        //     }
        //
        //     if (read) {
        //         _self.app.action.setReaded(function () {
        //             _self.fireEvent("afterReaded");
        //             _self.app.reload();
        //             if (layout.mobile) {
        //
        //                 //移动端页面关闭
        //                 _self.finishOnMobile()
        //             } else {
        //                 confirmDlg.close();
        //             }
        //         }, null, read.id, read);
        //     } else {
        //         _self.app.reload();
        //         if (layout.mobile) {
        //
        //             //移动端页面关闭
        //             _self.finishOnMobile()
        //         } else {
        //             confirmDlg.close();
        //         }
        //     }
        //
        // }, function () {
        //     this.close();
        // }, null, this.app.content, this.json.confirmStyle);
    },
    doReadedWork: function(dlg){
        var opinion = dlg.content.getElement(".readedWork_opinion").get("value");

        var read = null;
        for (var i = 0; i < this.businessData.readList.length; i++) {
            if (this.businessData.readList[i].person === layout.session.user.distinguishedName) {
                read = this.businessData.readList[i];
                break;
            }
        }

        var _self = this;
        if (read) {
            MWF.require("MWF.widget.Mask", function () {
                this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                this.mask.loadNode(this.app.content);

                read.opinion = opinion;

                this.fireEvent("beforeReaded");

                this.app.action.setReaded(function () {
                    if (_self.mask) { _self.mask.hide(); _self.mask = null; }

                    _self.fireEvent("afterReaded");

                    if (layout.mobile) {
                        _self.finishOnMobile();
                    } else {
                        _self.app.reload();
                        dlg.close();
                    }
                }, null, read.id, read);
            }.bind(this));
        } else {
            if (layout.mobile) {
                _self.finishOnMobile();
            } else {
                _self.app.reload();
                dlg.close();
            }
        }
    },

    openWindow: function (form, app) {
        //var application = app || (this.businessData.work) ? this.businessData.work.application : this.businessData.workCompleted.application;
        var form = form;
        if (!form) {
            form = this.json.id;
            //if (this.json.printForm) form = this.json.printForm;
        }
        if (this.businessData.workCompleted) {
            var application = app || this.businessData.workCompleted.application;
            var url = o2.filterUrl("../x_desktop/printWork.html?workCompletedId=" + this.businessData.workCompleted.id + "&app=" + application + "&form=" + form);
            if ((o2.thirdparty.isDingdingPC() || o2.thirdparty.isQywxPC())) {
                var xtoken = layout.session.token;
                url += "&" + o2.tokenName + "=" + xtoken;
            }
            window.open(url);
        } else {
            var application = app || this.businessData.work.application;
            var url = o2.filterUrl("../x_desktop/printWork.html?workid=" + this.businessData.work.id + "&app=" + application + "&form=" + form);
            if ((o2.thirdparty.isDingdingPC() || o2.thirdparty.isQywxPC())) {
                var xtoken = layout.session.token;
                url += "&" + o2.tokenName + "=" + xtoken;
            }
            window.open(url);
        }
        //window.open("../x_desktop/printWork.html?workid="+this.businessData.work.id+"&app="+this.businessData.work.application+"&form="+form);
    },
    /**
     * @summary 将新上传的附件在指定的附件组件中展现.
     * @param {String} site - 附件组件的标识
     * @param {String} id - 新上传的附件id
     * @example
     * this.form.getApp().appForm.uploadedAttachment(site, id);
     */
    uploadedAttachment: function (site, id) {
        this.workAction.getAttachment(id, this.businessData.work.id, function (json) {

            var flag = this.businessData.attachmentList.some(function (attData) {
                return json.data.id === attData.id;
            }.bind(this));
            if( !flag ){
                this.businessData.attachmentList.push(json.data);
            }

            var att = this.all[site];
            if (att) {
                if (json.data) att.attachmentController.addAttachment(json.data);
                att.attachmentController.checkActions();
                att.fireEvent("upload", [json.data]);
            }
        }.bind(this));
    },
    replacedAttachment: function (site, id) {
        this.workAction.getAttachment(id, this.businessData.work.id, function (json) {

            var att = this.all[site];
            if (att) {
                var attachmentController = att.attachmentController;
                var attachment = null;
                for (var i = 0; i < attachmentController.attachments.length; i++) {
                    if (attachmentController.attachments[i].data.id === id) {
                        attachment = attachmentController.attachments[i];
                        break;
                    }
                }
                attachment.data = json.data;
                attachment.reload();
                attachmentController.checkActions();
            }
        }.bind(this))
    },
    uploadedAttachmentDatagrid: function (site, id, moduleId) {
        this.workAction.getAttachment(id, this.businessData.work.id, function (json) {

            var flag = this.businessData.attachmentList.some(function (attData) {
                return json.data.id === attData.id;
            }.bind(this));
            if( !flag ){
                this.businessData.attachmentList.push(json.data);
            }

            var att = this.all[moduleId];
            if (att) {
                if (json.data) att.attachmentController.addAttachment(json.data);
                att.setAttachmentBusinessData();
                att.attachmentController.checkActions();
                att.fireEvent("upload", [json.data]);
                att.fireEvent("change", [json.data]);
            }
        }.bind(this));
    },
    replacedAttachmentDatagrid: function (site, id, moduleId) {
        this.workAction.getAttachment(id, this.businessData.work.id, function (json) {

            var att = this.all[moduleId];
            if (att) {
                var attachmentController = att.attachmentController;
                var attachment = null;
                for (var i = 0; i < attachmentController.attachments.length; i++) {
                    if (attachmentController.attachments[i].data.id === id) {
                        attachment = attachmentController.attachments[i];
                        break;
                    }
                }
                attachment.data = json.data;
                att.setAttachmentBusinessData();
                attachment.reload();
                attachmentController.checkActions();
                att.fireEvent("change", [json.data]);
            }
        }.bind(this))
    },

    // 打开工作关联聊天
    openIMChatStarter: function(jobId) {
        MWF.xDesktop.requireApp("IMV2", "Starter", function () {
            var starter = new MWF.xApplication.IMV2.Starter({}, this.app, {
                "businessId":jobId,
                "businessType": "process"
            });
            starter.load();
        }.bind(this));
    },
    // 分享到IM聊天
    shareToIMChat: function() {
        // app端 分享到聊天
        if (window.o2android && window.o2android.postMessage) {
            var body = {
                type: "shareToIMChat",
            }
            window.o2android.postMessage(JSON.stringify(body));
        } else {
            var imJson = {}
            if (this.businessData.workCompleted) {
                imJson = {
                    type: "process",
                    title: this.businessData.workCompleted.title,
                    work: this.businessData.workCompleted.id,
                    process: this.businessData.workCompleted.process,
                    processName: this.businessData.workCompleted.processName,
                    application: this.businessData.workCompleted.application,
                    applicationName: this.businessData.workCompleted.applicationName,
                    job: this.businessData.workCompleted.job,
                }
            } else if (this.businessData.work) {
                imJson = {
                    type: "process",
                    title: this.businessData.work.title,
                    work: this.businessData.work.id,
                    process: this.businessData.work.process,
                    processName: this.businessData.work.processName,
                    application: this.businessData.work.application,
                    applicationName: this.businessData.work.applicationName,
                    job: this.businessData.work.job,
                }
            } else {
                this.app.notice(MWF.xApplication.process.Xform.LP.noPermissionOrWorkNotExisted, "error")
                return
            }
            MWF.xDesktop.requireApp("IMV2", "Starter", function () {
                var share = new MWF.xApplication.IMV2.ShareToConversation({
                    msgBody: imJson
                }, this.app);
                share.load();
            }.bind(this));
        }
    },

    //移动端页面 工作处理完成后
    finishOnMobile: function () {
        var _self = this;
        //新建检查
        // if (this.json.checkDraft){
        //     this.workAction.checkDraft(this.businessData.work.id, function (json) {
        //         // var str = JSON.stringify(json);
        //         _self.finishOnMobileReal();
        //     }.bind(this), function () {
        //         _self.finishOnMobileReal();
        //     }, false);
        // }else {
        //     _self.finishOnMobileReal();
        // }
        this.workAction.checkDraft(this.businessData.work.id, function (json) {
            // var str = JSON.stringify(json);
            _self.finishOnMobileReal();
        }.bind(this), function () {
            _self.finishOnMobileReal();
        }, false);
    },

    finishOnMobileReal: function () {
        if (window.o2android && window.o2android.postMessage) {
            var body = {
                type: "closeWork",
            }
            window.o2android.postMessage(JSON.stringify(body));
        } else if (window.o2android && window.o2android.closeWork) {
            window.o2android.closeWork("");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.closeWork) {
            window.webkit.messageHandlers.closeWork.postMessage("");
        } else if (window.wx && window.__wxjs_environment === 'miniprogram') { //微信小程序 关闭页面
            wx.miniProgram.navigateBack({ delta: 1 });
        } else if (window.uni && window.uni.navigateBack) { // uniapp 关闭页面
            window.uni.navigateBack();
        } else if (this.json.afterProcessAction === "redirect" && this.json.afterProcessRedirectScript && this.json.afterProcessRedirectScript.code) {
            var url = this.Macro.exec(this.json.afterProcessRedirectScript.code, this);
            (new URI(url)).go();
        } else {
            // var len = window.history.length;
            // if (len > 1) {
            //     history.back();
            // } else {
            //     var uri = new URI(window.location.href);
            //     var redirectlink = uri.getData("redirectlink");
            //     if (redirectlink) {
            //         history.replaceState(null, "work", redirectlink);
            //         redirectlink.toURI().go();
            //     } else {
            //         // window.location = o2.filterUrl("../x_desktop/appMobile.html?app=process.TaskCenter");
            //         history.replaceState(null, "work", o2.filterUrl("../x_desktop/appMobile.html?app=process.TaskCenter"));
            //         o2.filterUrl("../x_desktop/appMobile.html?app=process.TaskCenter").toURI().go();
            //     }
            // }
            var uri = new URI(window.location.href);
            var redirectlink = uri.getData("redirectlink");
            if (redirectlink) {
                history.replaceState(null, "work", redirectlink);
                redirectlink.toURI().go();
            } else {
                // window.location = o2.filterUrl("../x_desktop/appMobile.html?app=process.TaskCenter");
                history.replaceState(null, "work", o2.filterUrl("../x_desktop/appMobile.html?app=process.TaskCenter"));
                o2.filterUrl("../x_desktop/appMobile.html?app=process.TaskCenter").toURI().go();
            }
        }
    },
    // 判断是否是钉钉pc上
    dingTalkPcCloseOrAppClose: function () {
        if ((o2.thirdparty.isDingdingPC() || o2.thirdparty.isQywxPC()) && layout.inBrowser) { // 如果是钉钉pc上 并且是浏览器模式
            var centerUrl = o2.filterUrl("../x_desktop/app.html?app=process.TaskCenter");
            history.replaceState(null, "work", centerUrl);
            centerUrl.toURI().go();
        } else {
            this.app.close();
        }
    },
    /**
     * @summary 获取组件的类型(小写).
     * @param {Object|String} module - 组件或组件Id
     * @return {String} 组件类型（小写）
     * @example
     * //假设有一个文本输入组件id为subject
     * var module = this.form.get("subject");
     * //moduleType 为 textfield;
     * var moduleType = this.form.getApp().appForm.getModuleType();
     * @example
     * //假设有一个附件组件id为att,
     * var moduleType = this.form.getApp().appForm.getModuleType("att");
     * //moduleType 为 attachment;
     */
    getModuleType : function (module) {
        if( typeOf(module) === "string" )module = this.all[module];
        if( module ){
            var moduleType = module.json.moduleName || "";
            if( !moduleType ){
                moduleType = typeOf(module.json.type) === "string" ? module.json.type.toLowerCase() : "";
            }
            return moduleType.toLowerCase();
        }else{
            return "";
        }
    },

    sendRead: function () {
        var opt = {};
        MWF.require("MWF.xDesktop.Dialog", function () {
            var width = 560;
            var height = 270;
            var p = MWF.getCenterPosition(this.app.content, width, height);

            var _self = this;
            var dlg = new MWF.xDesktop.Dialog({
                "title": MWF.xApplication.process.Xform.LP.sendRead,
                "style": this.json.dialogStyle || "user", //|| "work",
                "top": p.y - 100,
                "left": p.x,
                "fromTop": p.y - 100,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "url": this.app.path + "sendRead.html",
                "lp": MWF.xApplication.process.Xform.LP.form,
                "container": this.app.content,
                "isClose": true,
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": function (d, e) {
                            if( !opt.identityList || !opt.identityList.length ){
                                _self.app.notice(MWF.xApplication.process.Xform.LP.inputSendReadPeople, "error", dlg.node);
                            }else{
                                var notify = dlg.content.getElement(".sendRead_notify").get("checked");
                                opt.notify = !!notify;
                                _self.doSendRead(opt);
                            }
                        }.bind(this)
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    opt.dlg = this;
                    opt.workId = (_self.businessData.work || _self.businessData.workCompleted).id;
                    opt.complete = !!(_self.businessData.work || _self.businessData.workCompleted).completedTime;
                    var selPeopleButton = this.content.getElement(".sendRead_selPeopleButton");
                    selPeopleButton.addEvent("click", function () {
                        _self.selectReadPeople(this, opt);
                    }.bind(this));
                }
            });
            dlg.show();
        }.bind(this));
    },
    selectReadPeople: function (dlg, opt ) {
        var names = opt.identityList || [];
        var areaNode = dlg.content.getElement(".sendRead_selPeopleArea");
        var options = {
            "values": names,
            "type": "identity",
            "count": 0,
            "title": MWF.xApplication.process.Xform.LP.sendRead,
            "onComplete": function (items) {
                areaNode.empty();
                var identityList = [];
                items.each(function (item) {
                    new MWF.widget.O2Identity(item.data, areaNode, { "style": "reset" });
                    identityList.push(item.data.distinguishedName);
                }.bind(this));
                opt.identityList = identityList;
            }.bind(this)
        };
        MWF.xDesktop.requireApp("Selector", "package", function () {
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    doSendRead: function(opt){
        if( !opt.identityList || !opt.identityList.length ) {
            this.app.notice(MWF.xApplication.process.Xform.LP.sendReadPeopleCanNotEmpty, "error", opt.dlg ? opt.dlg.node : null);
            return false;
        }

        var nameArr = [];
        var names = opt.identityList || [];
        names.each(function (n) { nameArr.push(n.split("@")[0]); });


        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            this.mask.loadNode(this.app.content);

            this.fireEvent("beforeSendRead");
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeSendRead");

            if( !opt.workId ){
                opt.workId = (this.businessData.work || this.businessData.workCompleted).id;
                opt.complete = !!(this.businessData.work || this.businessData.workCompleted).completedTime;
            }

            var method = opt.complete ? "createWithWorkCompleted" : "createWithWork";

            o2.Actions.load("x_processplatform_assemble_surface").ReadAction[method](opt.workId, {
                "identityList": opt.identityList,
                "notify": opt.notify
            }, function () {
                this.fireEvent("afterSendRead");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterSendRead");
                this.app.notice(MWF.xApplication.process.Xform.LP.sendReadOk + ": " + nameArr, "success");
                if(opt.dlg)opt.dlg.close();
                if(opt.success)opt.success();
                if (this.mask) { this.mask.hide(); this.mask = null; }
                this.app.reload();
            }.bind(this), function (xhr, text, error) {
                var errorText = error + ":" + text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: " + errorText, "error", opt.dlg ? opt.dlg.node : null);
                if(opt.failure)opt.failure();
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this));
        }.bind(this));
    },

    addReview: function () {
        var opt = {};
        MWF.require("MWF.xDesktop.Dialog", function () {
            var width = 560;
            var height = 270;
            var p = MWF.getCenterPosition(this.app.content, width, height);

            var _self = this;
            var dlg = new MWF.xDesktop.Dialog({
                "title": MWF.xApplication.process.Xform.LP.sendReview,
                "style": this.json.dialogStyle || "user", //|| "work",
                "top": p.y - 100,
                "left": p.x,
                "fromTop": p.y - 100,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "url": this.app.path + "sendReview.html",
                "lp": MWF.xApplication.process.Xform.LP.form,
                "container": this.app.content,
                "isClose": true,
                "buttonList": [
                    {
                        "type": "ok",
                        "text": MWF.LP.process.button.ok,
                        "action": function (d, e) {
                            if( !opt.personList || !opt.personList.length ){
                                _self.app.notice(MWF.xApplication.process.Xform.LP.inputSendReviewPeople, "error", dlg.node);
                            }else{
                                _self.doAddReview(opt);
                            }
                        }.bind(this)
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    opt.dlg = this;
                    opt.workId = (_self.businessData.work || _self.businessData.workCompleted).id;
                    opt.complete = !!(_self.businessData.work || _self.businessData.workCompleted).completedTime;
                    var selPeopleButton = this.content.getElement(".sendReview_selPeopleButton");
                    selPeopleButton.addEvent("click", function () {
                        _self.selectReviewPeople(this, opt);
                    }.bind(this));
                }
            });
            dlg.show();
        }.bind(this));
    },
    selectReviewPeople: function (dlg, opt ) {
        var names = opt.personList || [];
        var areaNode = dlg.content.getElement(".sendReview_selPeopleArea");
        var options = {
            "values": names,
            "type": "identity",
            "resultType": "person",
            "count": 0,
            "title": MWF.xApplication.process.Xform.LP.sendReview,
            "onComplete": function (items) {
                areaNode.empty();
                var personList = [];
                items.each(function (item) {
                    new MWF.widget.O2Person(item.data, areaNode, { "style": "reset" });
                    personList.push(item.data.distinguishedName);
                }.bind(this));
                opt.personList = personList;
            }.bind(this)
        };
        MWF.xDesktop.requireApp("Selector", "package", function () {
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    doAddReview: function(opt){
        if( !opt.personList || !opt.personList.length ) {
            this.app.notice(MWF.xApplication.process.Xform.LP.sendReviewPeopleCanNotEmpty, "error", opt.dlg ? opt.dlg.node : null);
            return false;
        }

        var nameArr = [];
        var names = opt.personList || [];
        names.each(function (n) { nameArr.push(n.split("@")[0]); });


        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            this.mask.loadNode(this.app.content);

            this.fireEvent("beforeAddReview");
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeAddReview");

            if( !opt.workId ){
                opt.workId = (this.businessData.work || this.businessData.workCompleted).id;
                opt.complete = !!(this.businessData.work || this.businessData.workCompleted).completedTime;
            }

            var method = opt.complete ? "createWithWorkCompleted" : "createWithWork";
            var data = { "personList": opt.personList };
            if( opt.complete ){
                data.workCompleted = opt.workId;
            }else{
                data.work = opt.workId;
            }

            o2.Actions.load("x_processplatform_assemble_surface").ReviewAction[method]( data, function () {
                this.fireEvent("afterAddReview");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterAddReview");
                this.app.notice(MWF.xApplication.process.Xform.LP.sendReviewOk + ": " + nameArr, "success");
                if(opt.dlg)opt.dlg.close();
                if(opt.success)opt.success();
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), function (xhr, text, error) {
                var errorText = error + ":" + text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: " + errorText, "error", opt.dlg ? opt.dlg.node : null);
                if(opt.failure)opt.failure();
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this));
        }.bind(this));
    },

    checkControl: function(key){
        if (!this.businessData.control[key]) {
            o2.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }
        if( !this.businessData.task ){
            o2.xDesktop.notice("error", { x: "right", y: "top" }, o2.xApplication.process.Xform.LP.form.noTaskToReset);
            return false;
        }
        return true;
    },
    addTask: function(){
        if (!this.checkUploadAttachment()) return false;
        if (this.checkControl("allowAddTask")){
            var _self = this;
            var opt = {};

            o2.DL.open({
                "title": o2.xApplication.process.Xform.LP.form.addTask,
                "style": this.json.dialogStyle || "user",
                "width":   (layout.mobile) ? "100%" : 680,
                "height":  (layout.mobile) ? "100%" : 380,
                "url": this.app.path + ( (layout.mobile) ? "addTaskMobile" : "addTask") +".html",
                "lp": o2.xApplication.process.Xform.LP.form,
                "container": (layout.mobile) ? document.body : this.app.content,
                "maskNode": this.app.content,
                "offset": (layout.mobile) ? null : {y: -120},
                // "top": (layout.mobile) ? 0 : undefined,
                // "left": (layout.mobile) ? 0 : undefined,
                "buttonList": [
                    {
                        "type": "ok",
                        "text": o2.LP.process.button.ok,
                        "action": function (d, e) {
                            if( !this.identityList || !this.identityList.length ){
                                _self.app.notice(o2.xApplication.process.Xform.LP.inputAddTaskPeople, "error", this.node);
                            }else{
                                _self.doAddTask(this);
                            }
                        }
                    },
                    {
                        "type": "cancel",
                        "text": MWF.LP.process.button.cancel,
                        "action": function () {
                            this.close();
                        }
                    }
                ],
                "onPostShow": function () {
                    var selPeopleButton = this.content.getElement(".addTask_selPeopleButton");
                    selPeopleButton.addEvent("click", function () {
                        _self.selectPeople(this);
                    }.bind(this));
                }

            });
        }
    },
    getRadioValue: function(node, selector){
        var nodes = node.getElements(selector);
        for (var i=0; i<nodes.length; i++){
            if (nodes[i].checked){
                return nodes[i].value;
            }
        }
        return "";
    },
    // checkAddTaskIdentity: function(identityList, position, node, callback){
    //     var manualTaskIdentityMatrix = this.businessData.work.manualTaskIdentityMatrix.matrix;
    //     manualTaskIdentityMatrix = (manualTaskIdentityMatrix.flat) ? manualTaskIdentityMatrix.flat() :  manualTaskIdentityMatrix.reduce(function(acc, val){
    //         return acc.concat(val);
    //     }, []);
    //     var repeated = [];
    //
    //     var optionList = identityList.reduce(function(pv, cv){
    //         if (manualTaskIdentityMatrix.indexOf(cv)===-1){
    //             return pv.concat({
    //                 position: position,
    //                 identityList: [cv]
    //             });
    //         }else{
    //             repeated.push(o2.name.cn(cv));
    //             return pv;
    //         }
    //     }, []);
    //
    //     if (repeated.length){
    //         var content = o2.xApplication.process.Xform.LP.form.addTaskRepeatedInfo;
    //         content = content.replace("{repeated}", repeated.join(", "));
    //         o2.DL.open({
    //             isClose: false,
    //             title: o2.xApplication.process.Xform.LP.form.addTaskRepeatedTitle,
    //             html: content,
    //             container: node,
    //             buttonList: [{
    //                 "text": MWF.xApplication.process.Xform.LP.ok,
    //                 "action": function(){
    //                     this.close();
    //                     if (callback) callback(optionList);
    //                 }
    //             }]
    //         })
    //     }else{
    //         if (callback) callback(optionList);
    //     }
    // },
    // doAddTask: function(dlg){
    //     MWF.require("MWF.widget.Mask", function () {
    //
    //         var position = this.getRadioValue(dlg.content, ".addTask_type") || "after";
    //
    //         this.checkAddTaskIdentity(dlg.identityList, position, dlg.node, function(optionList){
    //             if (optionList && optionList.length){
    //                 this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
    //                 this.mask.loadNode(this.app.content);
    //
    //                 var nameArr = optionList.map(function(op){
    //                     return o2.name.cn(op.identityList[0]);
    //                 });
    //
    //                 var opinion = dlg.content.getElement(".addTask_opinion").get("value");
    //                 if (!opinion) opinion = o2.xApplication.process.Xform.LP.form.addTask+":"+nameArr.join(", ");
    //                 var taskId = this.businessData.task.id;
    //
    //                 var addTaskOptions = {
    //                     optionList: optionList,
    //                     remove: false,
    //                     opinion: opinion,
    //                     routeName: o2.xApplication.process.Xform.LP.form.addTask+":"+nameArr.join(", ")
    //                 }
    //                 this.fireEvent("beforeAddTask");
    //                 if (this.app && this.app.fireEvent) this.app.fireEvent("beforeAddTask");
    //
    //                 this.saveFormData(function(){
    //                     o2.Actions.load("x_processplatform_assemble_surface").TaskAction.V2Add(taskId, addTaskOptions, function (json) {
    //                         this.fireEvent("afterAddTask");
    //                         if (this.app && this.app.fireEvent) this.app.fireEvent("afterAddTask");
    //                         this.app.notice(MWF.xApplication.process.Xform.LP.addTaskOk + ": " + nameArr, "success");
    //
    //                         dlg.close();
    //                         if (this.mask) this.mask.hide();
    //
    //                         var notCloseWindow = position!=="before";
    //                         this.addAddTaskMessage(json.data, notCloseWindow);
    //                         if (!this.app.inBrowser){
    //                             this.app[(notCloseWindow ? "refresh" : "close")]();
    //                         }
    //
    //                     }.bind(this), function (xhr, text, error) {
    //                         var errorText = error + ":" + text;
    //                         if (xhr) errorText = xhr.responseText
    //                         this.app.notice("request json error: " + errorText, "error", dlg ? dlg.node : null);
    //
    //                         if (this.mask) this.mask.hide();
    //                     }.bind(this)).catch(function(){});
    //                 }.bind(this));
    //             }else{
    //                 if (this.mask)  this.mask.hide();
    //             }
    //         }.bind(this));
    //
    //     }.bind(this));
    // },
    doAddTask: function(dlg){
        MWF.require("MWF.widget.Mask", function () {

            var position = this.getRadioValue(dlg.content, ".addTask_type") || "after";
            var mode = this.getRadioValue(dlg.content, ".mode_type") || "single";

            if (dlg.identityList && dlg.identityList.length){
                this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                if( layout.mobile ){
                    this.mask.load();
                }else{
                    this.mask.loadNode(this.app.content);
                }

                var nameArr = dlg.identityList.map(function(id){
                    return o2.name.cn(id);
                });

                var opinion = dlg.content.getElement(".addTask_opinion").get("value");

                var data = {mode: mode, opinion: opinion, before: position === "before", names: dlg.identityList, userOpinion: opinion};

                if( !this.validationOtherFlow('addTask', opinion, null, data) ){
                    if (this.mask) { this.mask.hide(); this.mask = null; }
                    return;
                }

                this.fireEvent("beforeAddTask", {mode: mode, opinion: opinion, before: position === "before", names: dlg.identityList});
                if (this.app && this.app.fireEvent) this.app.fireEvent("beforeAddTask");

                this.doAddTaskToPeople(dlg.identityList, opinion, mode, position === "before", "", function (json) {
                        this.fireEvent("afterAddTask", data);
                        if (this.app && this.app.fireEvent) this.app.fireEvent("afterAddTask");
                        this.app.notice(MWF.xApplication.process.Xform.LP.addTaskOk + ": " + nameArr, "success");

                        dlg.close();
                        if (this.mask) this.mask.hide();

                        var notCloseWindow = false; //position!=="before";
                        this.addAddTaskMessage(json.data, notCloseWindow);
                        if (!this.app.inBrowser){
                            this.app[(notCloseWindow ? "refresh" : "close")]();
                        }

                    }.bind(this), function (xhr, text, error) {
                        var errorText = error + ":" + text;
                        if (xhr) errorText = xhr.responseText;
                        this.app.notice("request json error: " + errorText, "error", dlg ? dlg.node : null);

                        if (this.mask) this.mask.hide();
                    }.bind(this))

            }else{
                if (this.mask)  this.mask.hide();
            }

        }.bind(this));
    },
    doAddTaskToPeople: function (names, opinion, mode, before, routeName, success, failure) {

        var lp = o2.xApplication.process.Xform.LP.form;

        var leftText = (!!before ? lp.addTaskBefore : lp.addTaskAfter)+lp[mode];

        var nameArr = names.map(function(id){
            return o2.name.cn(id);
        });
        var n = nameArr.length > 3 ? (nameArr[0]+"、"+nameArr[1]+"、"+nameArr[2]+"...") : nameArr.join(", ");
        var routeName = leftText+":"+n;

        if (!opinion) opinion = leftText+":"+nameArr.join(", "); //o2.xApplication.process.Xform.LP.form.addTask+":"+nameArr.join(", ");

        var data = {
            "mode": mode,
            "before": !!before,
            "opinion": opinion,
            "routeName": routeName,
            "distinguishedNameList": names
        };
        this.saveFormData(
            function(json){
                o2.Actions.load("x_processplatform_assemble_surface").TaskAction.v3Add(
                    //this.workAction.resetWork(
                    function (json) {
                        if (success) success(json);
                    }.bind(this),
                    function (xhr, text, error) {
                        if (failure) failure(xhr, text, error);
                    },
                    this.businessData.task.id, data
                );
            }.bind(this),
            function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            });
    },

    addAddTaskMessage: function (data, notCloseWindow) {
        var content = this.getMessageContent(data, 0, MWF.xApplication.process.Xform.LP.addTaskInfor);
        if (layout.desktop.message) {
            var msg = {
                "subject": MWF.xApplication.process.Xform.LP.form.addTask,
                "content": content
            };
            layout.desktop.message.addTooltip(msg);
            return layout.desktop.message.addMessage(msg);
        } else {
            if (this.app.inBrowser) {
                this.inBrowserDkg(content, notCloseWindow);
            }
        }
    },

    terminateWork: function(){
        if (!this.businessData.control["allowTerminate"]) {
            MWF.xDesktop.notice("error", { x: "right", y: "top" }, "Permission Denied");
            return false;
        }

        var _self = this;
        var opt = {};

        o2.DL.open({
            "title": o2.xApplication.process.Xform.LP.terminateWorkTitle,
            "style": this.json.dialogStyle || "user",
            "width":   (layout.mobile) ? "100%" : 620,
            "height":  (layout.mobile) ? "100%" : 300,
            "url": this.app.path + ( (layout.mobile) ? "terminateMobile" : "terminate") +".html",
            "lp": o2.xApplication.process.Xform.LP,
            "container": (layout.mobile) ? document.body : this.app.content,
            "maskNode": this.app.content,
            "offset": (layout.mobile) ? null : {y: -120},
            "buttonList": [
                {
                    "type": "ok",
                    "text": o2.LP.process.button.ok,
                    "action": function (d, e) {
                        _self.fireEvent("beforeTerminat");
                        if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeTerminat");

                        var opinion = this.content.getElement(".terminateWork_opinion").get("value");

                        _self.doTerminateWork(function () {
                            _self.fireEvent("afterTerminat");
                            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterTerminat");
                            _self.app.notice(MWF.xApplication.process.Xform.LP.workTerminate + ": “" + _self.businessData.work.title + "”", "success");
                            _self.json.mode === "Mobile" ? _self.finishOnMobile() : _self.app.close();
                            this.close();
                        }.bind(this), function (xhr, text, error) {
                            var errorText = error + ":" + text;
                            if (xhr) errorText = xhr.responseText;
                            _self.app.notice("request json error: " + errorText, "error");
                        }.bind(this), opinion);
                    }
                },
                {
                    "type": "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function () {
                        this.close();
                    }
                }
            ]

        });
    },
    // terminateWork: function(e, ev){
    //     var _self = this;
    //     if (this.json.mode === "Mobile") {
    //         var p = MWF.getCenterPosition(document.body, 300, 150);
    //         var x = p.x;
    //         if (p.x < 20) {
    //             x = 20;
    //         } else {
    //             x = p.x;
    //         }
    //         var event = {
    //             "event": {
    //                 "x": x,
    //                 "y": p.y - 200,
    //                 "clientX": x,
    //                 "clientY": p.y - 200
    //             }
    //         };
    //         this.app.confirm("infor", event, MWF.xApplication.process.Xform.LP.terminateWorkTitle, MWF.xApplication.process.Xform.LP.terminateWorkText, 300, 120, function () {
    //             _self.app.content.mask({
    //                 "style": {
    //                     "background-color": "#999",
    //                     "opacity": 0.6
    //                 }
    //             });
    //             MWF.require("MWF.widget.Mask", function () {
    //                 _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
    //                 _self.mask.loadNode(_self.app.content);
    //
    //                 _self.fireEvent("beforeTerminat");
    //                 if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeTerminat");
    //
    //                 _self.doTerminateWork(function () {
    //                     _self.fireEvent("afterTerminat");
    //                     if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterTerminat");
    //                     _self.app.notice(MWF.xApplication.process.Xform.LP.workTerminate + ": “" + _self.businessData.work.title + "”", "success");
    //                     if (_self.mask) {
    //                         _self.mask.hide();
    //                         _self.mask = null;
    //                     }
    //                     _self.finishOnMobile()
    //                 }.bind(this), function (xhr, text, error) {
    //                     var errorText = error + ":" + text;
    //                     if (xhr) errorText = xhr.responseText;
    //                     _self.app.notice("request json error: " + errorText, "error");
    //                     if (_self.mask) {
    //                         _self.mask.hide();
    //                         _self.mask = null;
    //                     }
    //                 }.bind(this));
    //             }.bind(this));
    //         }, function () {
    //             this.close();
    //         }, null, this.app.content, this.json.confirmStyle);
    //     } else {
    //         var p = MWF.getCenterPosition(this.app.content, 380, 150);
    //         var event = {
    //             "event": {
    //                 "x": p.x,
    //                 "y": p.y - 200,
    //                 "clientX": p.x,
    //                 "clientY": p.y - 200
    //             }
    //         };
    //         this.app.confirm("infor", event, MWF.xApplication.process.Xform.LP.terminateWorkTitle, MWF.xApplication.process.Xform.LP.terminateWorkText, 380, 120, function () {
    //             // _self.app.content.mask({
    //             //    "style": {
    //             //        "background-color": "#999",
    //             //        "opacity": 0.6
    //             //    }
    //             // });
    //
    //
    //             MWF.require("MWF.widget.Mask", function () {
    //                 _self.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
    //                 _self.mask.loadNode(_self.app.content);
    //
    //                 _self.fireEvent("beforeTerminat");
    //                 if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeTerminat");
    //
    //                 _self.doTerminateWork(function () {
    //                     _self.fireEvent("afterTerminat");
    //                     if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterTerminat");
    //                     _self.app.notice(MWF.xApplication.process.Xform.LP.workTerminate + ": “" + _self.businessData.work.title + "”", "success");
    //                     _self.app.close();
    //                     this.close();
    //                     if (_self.mask) {
    //                         _self.mask.hide();
    //                         _self.mask = null;
    //                     }
    //                 }.bind(this), function (xhr, text, error) {
    //                     var errorText = error + ":" + text;
    //                     if (xhr) errorText = xhr.responseText;
    //                     _self.app.notice("request json error: " + errorText, "error", dlg.node);
    //                     if (_self.mask) {
    //                         _self.mask.hide();
    //                         _self.mask = null;
    //                     }
    //                 }.bind(this));
    //             }.bind(this));
    //         }, function () {
    //             this.close();
    //         }, null, this.app.content, this.json.confirmStyle);
    //     }
    // },


    doTerminateWork: function (success, failure, opinion) {
        if (this.businessData.control["allowTerminate"]) {
            o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2Terminate(this.businessData.work.id, {
                opinion: opinion || "",
                routeName: MWF.xApplication.process.Xform.LP.terminateWork
            }, function (json) {
                if (success) success(json);
            }.bind(this), function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            });
        }else {
            if (failure) failure(null, "Permission Denied", "");
        }
    }

});


/**
 * @class PortalPage 门户页面。
 * @o2cn 门户页面
 * @o2category FormComponents
 * @o2range {Portal}
 * @extends MWF.xApplication.process.Xform.Form
 * @example
 * //可以在脚本中获取页面
 * //方法1：
 * var page = this.form.getApp().appForm; //获取页面
 * //方法2
 * var page = this.target; //在页面本身的事件脚本中获取
 * @hideconstructor
 */
var PortalPage="";

/**
 * @event PortalPage#beforeProcessWork
 * @ignore
 */
/**
 * @event PortalPage#beforeProcess
 * @ignore
 */
/**
 * @event PortalPage#afterProcess
 * @ignore
 */
/**
 * @event PortalPage#beforeReset
 * @ignore
 */
/**
 * @event PortalPage#afterReset
 * @ignore
 */
/**
 * @event PortalPage#beforeRetract
 * @ignore
 */
/**
 * @event PortalPage#afterRetract
 * @ignore
 */
/**
 * @event PortalPage#beforeReroute
 * @ignore
 */
/**
 * @event PortalPage#afterReroute
 * @ignore
 */
/**
 *  @event PortalPage#beforeDelete
 * @ignore
 */
/**
 * @event PortalPage#afterDelete
 * @ignore
 */
/**
 *  @event PortalPage#beforeReaded
 * @ignore
 */
/**
 * @event PortalPage#afterReaded
 * @ignore
 */
/**
 * @event PortalPage#afterLoadProcessor
 * @ignore
 */
/**
 * @event PortalPage#beforeAddTask
 * @ignore
 */
/**
 * @event PortalPage#afterAddTask
 * @ignore
 */
/**
 * @method PortalPage#getRouteDataList
 * @ignore
 */
/**
 * @method PortalPage#pressWork
 * @ignore
 */
/**
 * @method PortalPage#rerouteWork
 * @ignore
 */
/**
 * @method PortalPage#readedWork
 * @ignore
 */
/**
 * @method PortalPage#uploadedAttachment
 * @ignore
 */
/**
 * @method PortalPage#pauseTask
 * @ignore
 */
/**
 * @method PortalPage#resumeTask
 * @ignore
 */
