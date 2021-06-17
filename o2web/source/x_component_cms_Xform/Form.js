MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};

MWF.require("MWF.widget.Common", null, false);
// MWF.require("MWF.xAction.org.express.RestActions", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("process.Xform", "Form", null, false);
MWF.require("MWF.widget.O2Identity", null, false);

MWF.xDesktop.requireApp("cms.Xform", "Package", null, false);

/** @class CMSForm 内容管理表单。
 * @o2category FormComponents
 * @o2range {CMS}
 * @alias CMSForm
 * @example
 * //可以在脚本中获取表单
 * //方法1：
 * var form = this.form.getApp().appForm; //获取表单
 * //方法2
 * var form = this.target; //在表单本身的事件脚本中获取
 * @hideconstructor
 */
MWF.xApplication.cms.Xform.Form = MWF.CMSForm = new Class(
    /** @lends CMSForm# */
{
    Implements: [Options, Events],
    Extends: MWF.APPForm,
    options: {
        "style": "default",
        "readonly": false,
        "cssPath": "",
        "autoSave": false,
        "saveOnClose": false,
        "showAttachment": true,
        "moduleEvents": [
             /**
             * 表单加载前触发。表单html已经就位。
             * @event CMSForm#queryLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "queryLoad",
             /**
             * 表单加载前触发。数据(businessData)已经就绪。
             * @event CMSForm#beforeLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeLoad",
            /**
             * 表单的所有组件加载前触发，此时表单的样式和js head已经加载。
             * @event CMSForm#beforeModulesLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeModulesLoad",
            /**
             * 表单加载后触发。
             * @event CMSForm#postLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "postLoad",
            /**
             * 表单的所有组件加载后触发。
             * @event CMSForm#afterModulesLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterModulesLoad",
            /**
             * 表单加载后触发。
             * @event CMSForm#afterLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterLoad",
            /**
             * 保存前触发。
             * @event CMSForm#beforeSave
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeSave",
            /**
             * 数据已经整理完成，但还未保存到后台时触发。this.event指向整理完成的数据
             * @event CMSForm#afterSave
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "postSave",
            /**
             * 数据保存到后台后触发。
             * @event CMSForm#afterSave
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterSave",
            /**
             * 关闭前触发。
             * @event CMSForm#beforeClose
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeClose",
            /**
             * 发布前触发。
             * @event CMSForm#beforePublish
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforePublish",
            /**
             * 数据已经整理完成，但还未调用服务发布触发。this.event指向整理完成的数据
             * @event CMSForm#postPublish
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "postPublish",
            /**
             * 执行后台服务发布后触发。
             * @event CMSForm#afterPublish
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterPublish",
            /**
             * 删除前触发。
             * @event CMSForm#beforeDelete
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "beforeDelete",
            /**
             * 删除后触发。
             * @event CMSForm#afterDelete
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "afterDelete",
            "resize"
        ]
    },
    /**
     * @summary 获取表单的所有数据.
     * @method getData
     * @memberof CMSForm
     * @example
     * var data = this.form.getApp().appForm.getData();
     * @return {Object}
     */
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
        this.data = data;

        /**
         * @summary 表单的配置信息，比如表单名称等等.
         * @member {Object}
         * @example
         *  //可以在脚本中获取表单配置信息
         * var json = this.form.getApp().appForm.json; //表单配置信息
         * var name = json.name; //表单名称
         */
        this.json = data.json;
        this.html = data.html;

        this.path = "../x_component_cms_Xform/$Form/";
        this.cssPath = this.options.cssPath || "../x_component_cms_Xform/$Form/" + this.options.style + "/css.wcss";
        this._loadCss();

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
        this.forms = {};

        //if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    load: function (callback) {
        if (this.app) {
            if (this.app.formNode) this.app.formNode.setStyles(this.json.styles);
            if (this.app.addEvent) this.app.addEvent("resize", function () {
                this.fireEvent("resize");
            }.bind(this))
        }
        //if (!this.businessData.control.allowSave) this.setOptions({"readonly": true});

        this.Macro = new MWF.CMSMacro.CMSFormContext(this);


        this.loadLanguage(function(flag) {
            if (flag && this.formDataText) {
                var data = o2.bindJson(this.formDataText, {"lp": MWF.xApplication.cms.Xform.LP.form});
                this.data = JSON.parse(data);

                this.json = this.data.json;
                this.html = this.data.html;
            }

            this.container.set("html", this.html);
            this.node = this.container.getFirst();

            this._loadEvents();
            this.loadRelatedScript();

            if (this.fireEvent("queryLoad")) {
                // MWF.xDesktop.requireApp("cms.Xform", "lp." + MWF.language, null, false);

                //		this.container.setStyles(this.css.container);
                this._loadBusinessData();
                this.fireEvent("beforeLoad");
                if (this.app) if (this.app.fireEvent) this.app.fireEvent("beforeLoad");

                this.loadContent(callback)
            }

        }.bind(this));
    },
    loadLanguage: function(callback){
        MWF.xDesktop.requireApp("cms.Xform", "lp." + MWF.language, null, false);

        //formDataText
        if (this.json.languageType!=="script" && this.json.languageType!=="default"){
            if (callback) callback();
            return true;
        }

        var language = MWF.xApplication.cms.Xform.LP.form;
        var languageJson = null;

        if (this.json.languageType=="script"){
            if (this.json.languageScript && this.json.languageScript.code){
                languageJson = this.Macro.exec(this.json.languageScript.code, this);
            }
        }else if (this.json.languageType=="default") {
            var name = "lp-"+o2.language;
            var application = this.businessData.document.appId;

            var p1 = this.documentAction.getDictRoot(name, application, function(d){
                return d.data;
            }, function(){});
            var p2 = this.documentAction.getScriptByNameV2(name, application, function(d){
                return this.Macro.exec(d.data.text, this);
            }.bind(this), function(){});
            languageJson = Promise.any([p1, p2]);
        }

        if (languageJson){
            if (languageJson.then && o2.typeOf(languageJson.then)=="function"){
                languageJson.then(function(json) {
                    MWF.xApplication.cms.Xform.LP.form = Object.merge(MWF.xApplication.cms.Xform.LP.form, json);
                    if (callback) callback(true);
                }, function(){
                    if (callback) callback(true);
                })
            }else{
                MWF.xApplication.cms.Xform.LP.form = Object.merge(MWF.xApplication.cms.Xform.LP.form, languageJson);
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
    loadContent: function (callback) {
        this.subformCount = 0;
        this.subformLoadedCount = 0;
        this.subformLoaded = [this.json.id];

        this._loadHtml();
        this._loadForm();
        this.fireEvent("beforeModulesLoad");
        this._loadModules(this.node);

        if (!this.options.readonly) {
            if (this.options.autoSave) this.autoSave();
            this.app.addEvent("queryClose", function () {
                if (this.options.saveOnClose && this.businessData.document.docStatus == "draft") this.saveDocument(null, true);
                //if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
                Object.each(this.forms, function (module, id) {
                    if (module.json.type == "Htmleditor" && module.editor) {
                        //if(CKEDITOR.currentImageDialog)CKEDITOR.currentImageDialog.destroy();
                        //CKEDITOR.currentImageDialog = null;
                        CKEDITOR.remove(module.editor);
                        delete module.editor
                    }
                });
            }.bind(this));
        }
        this.fireEvent("afterModulesLoad");
        this.fireEvent("postLoad");
        this.fireEvent("afterLoad");
        if (this.app && this.app.fireEvent) {
            this.app.fireEvent("afterModulesLoad");
            this.app.fireEvent("postLoad");
            this.app.fireEvent("afterLoad");
        }
        // 告诉移动端表单加载完成
        if (this.app && this.app.mobile) {
            if (callback) callback();
        }
    },
    autoSave: function () {
        //this.autoSaveTimerID = window.setInterval(function(){
        //    this.saveDocument();
        //}.bind(this), 300000);
    },
    _loadBusinessData: function () {
        if (!this.businessData) {
            this.businessData = {
                "data": {}
            };
        }
    },

    _loadEvents: function () {
        Object.each(this.json.events, function (e, key) {
            if (e.code) {
                if (this.options.moduleEvents.indexOf(key) != -1) {
                    this.addEvent(key, function (event) {
                        return this.Macro.fire(e.code, this, event);
                    }.bind(this));
                } else {
                    if (key == "load") {
                        this.addEvent("postLoad", function () {
                            return this.Macro.fire(e.code, this);
                        }.bind(this));
                    } else if (key == "submit") {
                        this.addEvent("beforePublish", function () {
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



    _loadModules: function (dom) {
        //var subDom = this.node.getFirst();
        //while (subDom){
        //    if (subDom.get("MWFtype")){
        //        var json = this._getDomjson(subDom);
        //        var module = this._loadModule(json, subDom);
        //        this.modules.push(module);
        //    }
        //    subDom = subDom.getNext();
        //}

        var moduleNodes = this._getModuleNodes(dom);

        moduleNodes.each(function (node) {
            var json = this._getDomjson(node);
            if (!this.options.showAttachment && json.type == "Attachment") {
                return;
            }
            //移动端去掉操作栏
            if (this.app.mobile && json.type === "Actionbar") {
                return;
            }
            var module = this._loadModule(json, node);
            this.modules.push(module);
        }.bind(this));
    },
    _loadModule: function (json, node, beforeLoad) {
        if (!json) return;
        if (!MWF["CMS" + json.type]) {
            MWF.xDesktop.requireApp("cms.Xform", json.type, null, false);
        }
        var module = new MWF["CMS" + json.type](node, json, this);
        if (beforeLoad) beforeLoad.apply(module);
        if (!this.all[json.id]) this.all[json.id] = module;
        if (module.field) {
            if (!this.forms[json.id]) this.forms[json.id] = module;
        }
        module.readonly = this.options.readonly;
        module.load();
        return module;
    },
    //getData: function(){
    //    var data= Object.clone(this.businessData.data);
    //    Object.each(this.forms, function(module, id){
    //        debugger;
    //        if (module.json.section=="yes"){
    //            data[id] = this.getSectionData(module, data[id]);
    //        }else{
    //            data[id] = module.getData();
    //        }
    //    }.bind(this));
    //
    //    this.businessData.data = data;
    //    this.Macro.environment.setData(this.businessData.data);
    //    return data;
    //},
    trim: function (array) {
        var arr = [];
        array.each(function (v) {
            if (v) arr.push(v);
        });
        return arr;
    },
    transportPermissionData: function (array, t) {
        var result = [];
        array.each(function (data) {
            var dn = typeOf(data) === "string" ? data : data.distinguishedName;
            if (dn) {
                var flag = dn.substr(dn.length - 1, 1);
                var type;
                switch (flag.toLowerCase()) {
                    case "i":
                        type = "人员"; //"身份";
                        break;
                    case "p":
                        type = "人员";
                        break;
                    case "u":
                        type = "组织";
                        break;
                    case "g":
                        type = "群组";
                        break;
                    case "r":
                        type = "角色";
                        break;
                    default:
                        type = "";
                    //result.push( data );
                }
                if (type) {
                    var name;
                    if( typeOf(data) === "object" && data.name ){
                        name = data.name;
                    }else if( MWF.name && MWF.name.cn ){
                        name = MWF.name.cn( dn );
                    }else{
                        name = dn.split("@")[0];
                    }
                    result.push({
                        permission: t == "author" ? "作者" : "阅读",
                        permissionObjectType: type,
                        permissionObjectName: name,
                        permissionObjectCode: dn
                    })
                }
            }
        });
        return result.length > 0 ? result : null;
    },
    getSpecialData: function () {
        var data = this.businessData.data;
        var readers = [];
        var authors = [];
        var pictures = [];
        var cloudPictures = [];
        var summary = "";
        Object.each(this.forms, function (module, id) {
            if (module.json.type == "Readerfield" || module.json.type == "Reader") {
                if (module.json.section == "yes") {
                    readers = readers.concat(this.getSectionData(module, data[id]));
                } else {
                    readers = readers.concat(module.getData());
                }
            }
            if (module.json.type == "Authorfield" || module.json.type == "Author") {
                if (module.json.section == "yes") {
                    authors = authors.concat(this.getSectionData(module, data[id]));
                } else {
                    authors = authors.concat(module.getData());
                }
            }
            if (module.json.type == "ImageClipper") {
                var d = module.getData();
                if (d) pictures.push(d);
            }
            if (module.json.type == "Htmleditor") {
                var text = module.getText();
                summary = text.substr(0, 80);

                cloudPictures = cloudPictures.concat(module.getImageIds());
            }
        });
        if (data.processOwnerList && typeOf(data.processOwnerList) == "array") { //如果是流程中发布的
            var owner = { personValue: [] };
            data.processOwnerList.each(function (p) {
                owner.personValue.push({
                    name: p,
                    type: "person"
                });
            });
            readers = readers.concat(owner);
        }
        return {
            readers: this.transportPermissionData(readers, "reader"),
            authors: this.transportPermissionData(authors, "author"),
            pictures: pictures,
            summary: summary,
            cloudPictures: cloudPictures
        };
    },
    getDocumentData: function (formData) {
        var data = Object.clone(this.businessData.document);
        if (formData.subject) {
            data.title = formData.subject;
            data.subject = formData.subject;
            this.businessData.document.title = formData.subject;
            this.businessData.document.subject = formData.subject;
        }
        data.isNewDocument = false;
        return data;
    },
    saveDocument: function (callback, sync) {
        this.fireEvent("beforeSave");
        if (this.businessData.document.docStatus == "published") {
            if (!this.formValidation("publish")) {
                this.app.content.unmask();
                //if (callback) callback();
                return false;
            }
        }
        if (!this.formSaveValidation()) {
            this.app.content.unmask();
            if (callback) callback();
            return false;
        }
        var data = this.getData();
        var specialData = this.getSpecialData();
        var documentData = this.getDocumentData(data);
        documentData.readerList = specialData.readers;
        documentData.authorList = specialData.authors;
        documentData.pictureList = specialData.pictures;
        documentData.summary = specialData.summary;
        documentData.cloudPictures = specialData.cloudPictures;
        documentData.docData = data;
        delete documentData.attachmentList;
        this.fireEvent("postSave", [documentData]);
        if (this.officeList) {
            this.officeList.each(function (module) {
                module.save();
            });
        }
        this.documentAction.saveDocument(documentData, function () {
            //this.documentAction.saveData(function(json){
            this.app.notice(MWF.xApplication.cms.Xform.LP.dataSaved, "success");
            this.businessData.data.isNew = false;
            this.fireEvent("afterSave");
            if (callback) callback();
            //}.bind(this), null, this.businessData.document.id, data, !sync );
        }.bind(this), null, !sync);
    },
    closeDocument: function () {
        this.fireEvent("beforeClose");
        if (this.app) {
            this.app.close();
        }
    },
    printDocument: function (form) {
        var form = form;
        if (!form) {
            form = this.json.id;
            if (this.json.printForm && this.json.printForm !== "none") form = this.json.printForm;
        }
        window.open(o2.filterUrl("../x_desktop/printcmsdoc.html?documentid=" + this.businessData.document.id + "&form=" + form));
    },

    formValidation: function (status) {
        if (this.options.readonly) return true;
        var flag = true;
        //flag = this.validation();
        Object.each(this.forms, function (field, key) {
            field.validationMode();
            if (!field.validation(status)) {
                flag = false;
            }
        }.bind(this));
        return flag;
    },
    formSaveValidation: function () {
        if (!this.json.validationSave) return true;
        if (!this.json.validationSave.code) return true;
        var flag = this.Macro.exec(this.json.validationSave.code, this);
        if (!flag) flag = MWF.xApplication.cms.Xform.LP.notValidation;
        if (typeOf(flag) === "string") {
            if (flag !== "true") {
                this.app.notice(flag, "error");
                return false;
            }
        } else if (flag.toString() != "true") {
            return false;
        }
        return true;
    },
    formPublishValidation: function () {
        if (!this.json.validationPublish) return true;
        if (!this.json.validationPublish.code) return true;
        var flag = this.Macro.exec(this.json.validationPublish.code, this);
        if (!flag) flag = MWF.xApplication.cms.Xform.LP.notValidation;
        if (typeOf(flag) === "string") {
            if (flag !== "true") {
                this.app.notice(flag, "error");
                return false;
            }
        } else if (flag.toString() != "true") {
            return false;
        }
        return true;
    },
    publishDocument: function (callback) {
        this.fireEvent("beforePublish");
        this.app.content.mask({
            "destroyOnHide": true,
            "style": this.app.css.maskNode
        });
        if (!this.formValidation("publish")) {
            this.app.content.unmask();
            //if (callback) callback();
            return false;
        }
        if (!this.formPublishValidation()) {
            this.app.content.unmask();
            if (callback) callback();
            return false;
        }

        var data = this.getData();
        var specialData = this.getSpecialData();
        //this.documentAction.saveData(function(json){
        var documentData = this.getDocumentData(data);
        documentData.readerList = specialData.readers;
        documentData.authorList = specialData.authors;
        documentData.pictureList = specialData.pictures;
        documentData.summary = specialData.summary;
        documentData.cloudPictures = specialData.cloudPictures;
        documentData.docData = data;
        delete documentData.attachmentList;
        //this.documentAction.saveDocument(documentData, function(){
        this.fireEvent("postPublish", [documentData]);
        if (this.app) if (this.app.fireEvent) this.app.fireEvent("postPublish",[documentData]);
        if (this.officeList) {
            this.officeList.each(function (module) {
                module.save();
            });
        }
        this.documentAction.publishDocumentComplex(documentData, function (json) {
            this.businessData.data.isNew = false;
            this.fireEvent("afterPublish", [this, json.data]);
            if (this.app) if (this.app.fireEvent) this.app.fireEvent("afterPublish",[this, json.data]);
            if (callback) callback();
            if (this.app.mobile) {
                this.app.content.unmask();
                // console.log('这里是移动端');
            } else {
                if (this.businessData.document.title) {
                    this.app.notice(MWF.xApplication.cms.Xform.LP.documentPublished + ": “" + this.businessData.document.title + "”", "success");
                } else {
                    this.app.notice(MWF.xApplication.cms.Xform.LP.documentPublished, "success");
                }
                this.options.saveOnClose = false;
            }
            debugger;
            if( layout.inBrowser ){
                try{
                    if( window.opener && window.opener.o2RefreshCMSView ){
                        window.opener.o2RefreshCMSView();
                    }
                }catch (e) {}
                window.setTimeout(function () {
                    this.app.close();
                }.bind(this), 1500)
            }else{
                this.app.close();
            }
        }.bind(this));

        //}.bind(this))
        //}.bind(this), null, this.businessData.document.id, data);
    },
    //publishDocument_bak: function(callback){
    //    this.fireEvent("beforePublish");
    //    this.app.content.mask({
    //        "destroyOnHide": true,
    //        "style": this.app.css.maskNode
    //    });
    //    if (!this.formValidation("publish")){
    //        this.app.content.unmask();
    //        if (callback) callback();
    //        return false;
    //    }
    //
    //    var data = this.getData();
    //    var specialData = this.getSpecialData();
    //    this.documentAction.saveData(function(json){
    //        this.businessData.data.isNew = false;
    //        var documentData = this.getDocumentData(data);
    //        documentData.permissionList = specialData.readers;
    //        documentData.pictureList = specialData.pictures;
    //        documentData.summary = specialData.summary;
    //        delete documentData.attachmentList;
    //        this.documentAction.saveDocument(documentData, function(){
    //            this.documentAction.publishDocument(documentData, function(json){
    //                this.fireEvent("afterPublish");
    //                this.fireEvent("postPublish");
    //                if (callback) callback();
    //                this.app.notice(MWF.xApplication.cms.Xform.LP.documentPublished+": “"+this.businessData.document.title+"”", "success");
    //                this.options.saveOnClose = false;
    //                this.app.close();
    //                //this.close();
    //            }.bind(this) );
    //        }.bind(this))
    //    }.bind(this), null, this.businessData.document.id, data);
    //},

    deleteDocumentForMobile: function () {
        if (this.app.mobile) {
            this.app.content.mask({
                "style": {
                    "background-color": "#999",
                    "opacity": 0.6
                }
            });

            this.fireEvent("beforeDelete");
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeDelete");

            this.documentAction.removeDocument(this.businessData.document.id, function (json) {
                this.fireEvent("afterDelete");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterDelete");
                this.app.notice(MWF.xApplication.cms.Xform.LP.documentDelete + ": “" + this.businessData.document.title + "”", "success");
                this.options.autoSave = false;
                this.options.saveOnClose = false;
                this.fireEvent("postDelete");
                this.app.close();
            }.bind(this));
        }
    },

    /**
     * @summary 弹出删除文档确认框.
     * @method deleteDocument
     * @memberof CMSForm
     * @example
     * this.form.getApp().appForm.deleteDocument();
     */
    deleteDocument: function () {
        var _self = this;
        var p = MWF.getCenterPosition(this.app.content, 380, 150);
        var event = {
            "event": {
                "x": p.x,
                "y": p.y - 200,
                "clientX": p.x,
                "clientY": p.y - 200
            }
        };
        this.app.confirm("infor", event, MWF.xApplication.cms.Xform.LP.deleteDocumentTitle, MWF.xApplication.cms.Xform.LP.deleteDocumentText, 380, 120, function () {
            _self.app.content.mask({
                "style": {
                    "background-color": "#999",
                    "opacity": 0.6
                }
            });

            _self.fireEvent("beforeDelete");
            if (_self.app && _self.app.fireEvent) _self.app.fireEvent("beforeDelete");

            _self.documentAction.removeDocument(_self.businessData.document.id, function (json) {
                _self.fireEvent("afterDelete");
                if (_self.app && _self.app.fireEvent) _self.app.fireEvent("afterDelete");
                _self.app.notice(MWF.xApplication.cms.Xform.LP.documentDelete + ": “" + _self.businessData.document.title + "”", "success");
                _self.options.autoSave = false;
                _self.options.saveOnClose = false;
                _self.fireEvent("postDelete");
                _self.app.close();
                this.close();
            }.bind(this));
            //this.close();
        }, function () {
            this.close();
        });
    },

    /**
     * @summary 编辑文档.
     * @method editDocument
     * @memberof CMSForm
     * @example
     * this.form.getApp().appForm.editDocument();
     */
    editDocument: function () {
        if (this.app.inBrowser) {
            this.modules.each(function (module) {
                MWF.release(module);
            });
            //MWF.release(this);
            this.app.node.destroy();

            this.app.options.readonly = false;

            this.app.loadApplication();
        } else {
            var options = { "documentId": this.businessData.document.id, "readonly": false }; //this.explorer.app.options.application.allowControl};

            if (this.app.options.postPublish)options.postPublish = this.app.options.postPublish;
            if (this.app.options.afterPublish)options.afterPublish = this.app.options.afterPublish;
            if (this.app.options.postDelete)options.postDelete = this.app.options.postDelete;

            if (this.app.options.formEditId) options.formEditId = this.app.options.formEditId;
            this.app.desktop.openApplication(null, "cms.Document", options);
            this.app.close();
        }
    },

    //2019-11-29 移动端 开启编辑模式
    /**
     * @summary 移动端开启编辑模式.
     * @method editDocumentForMobile
     * @memberof CMSForm
     * @example
     * this.form.getApp().appForm.editDocumentForMobile();
     */
    editDocumentForMobile: function () {
        if (this.app.mobile) {
            this.app.options.readonly = false;
            this.app.loadDocument(this.app.options);
        }
    },

    /**
     * @summary 弹出设置热点的界面.
     * @method setPopularDocument
     * @memberof CMSForm
     * @example
     * this.form.getApp().appForm.setPopularDocument();
     */
    setPopularDocument: function () {
        this.app.setPopularDocument();
    },

    printWork: function (app, form) {
        var application = app || this.businessData.work.application;
        var form = form;
        if (!form) {
            form = this.json.id;
            if (this.json.printForm) form = this.json.printForm;
        }
        window.open(o2.filterUrl("../x_desktop/printWork.html?workid=" + this.businessData.work.id + "&app=" + this.businessData.work.application + "&form=" + form));
    },
    openWindow: function (form, app) {
        var form = form;
        if (!form) {
            form = this.json.id;
        }
        if (this.businessData.document) {
            //var application = app;
            //window.open("../x_desktop/printWork.html?workCompletedId="+this.businessData.workCompleted.id+"&app="+application+"&form="+form);
        }
    },

    /**
     * @summary 将新上传的附件在指定的附件组件中展现.
     * @method uploadedAttachment
     * @memberof CMSForm
     * @param {String} site - 附件组件的标识
     * @param {String} id - 新上传的附件id
     * @example
     * this.form.getApp().appForm.uploadedAttachment(site, id);
     */
    uploadedAttachment: function (site, id) {
        this.documentAction.getAttachment(id, this.businessData.document.id, function (json) {
            var att = this.all[site];
            if (att) {
                if (json.data) att.attachmentController.addAttachment(json.data);
                att.attachmentController.checkActions();
                att.fireEvent("upload", [json.data]);
            }
        }.bind(this));
    },
    replacedAttachment: function (site, id) {
        this.documentAction.getAttachment(id, this.businessData.document.id, function (json) {

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
    }



});
