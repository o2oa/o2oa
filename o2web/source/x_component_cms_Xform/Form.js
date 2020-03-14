MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};

MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xAction.org.express.RestActions", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("process.Xform", "Form", null, false);
MWF.require("MWF.widget.O2Identity", null, false);

MWF.xDesktop.requireApp("cms.Xform", "Package", null, false);
MWF.xApplication.cms.Xform.Form = MWF.CMSForm = new Class({
    Implements: [Options, Events],
    Extends: MWF.APPForm,
    options: {
        "style": "default",
        "readonly": false,
        "cssPath": "",
        "autoSave": false,
        "saveOnClose": false,
        "showAttachment": true,
        "moduleEvents": ["queryLoad",
            "beforeLoad",
            "postLoad",
            "afterLoad",
            "beforeSave",
            "postSave",
            "afterSave",
            "beforeClose",
            "beforePublish",
            "postPublish",
            "afterPublish",
            "beforeDelete",
            "afterDelete",
            "beforeModulesLoad",
            "resize",
            "afterModulesLoad"]
    },
    initialize: function (node, data, options) {
        this.setOptions(options);

        this.container = $(node);
        this.container.setStyle("-webkit-user-select", "text");
        this.data = data;
        this.json = data.json;
        this.html = data.html;

        this.path = "/x_component_cms_Xform/$Form/";
        this.cssPath = this.options.cssPath || "/x_component_cms_Xform/$Form/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.modules = [];
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

        this.container.set("html", this.html);
        this.node = this.container.getFirst();

        this._loadEvents();
        if (this.fireEvent("queryLoad")) {

            MWF.xDesktop.requireApp("cms.Xform", "lp." + MWF.language, null, false);
            //		this.container.setStyles(this.css.container);
            this._loadBusinessData();
            this.fireEvent("beforeLoad");
            if (this.app) if (this.app.fireEvent) this.app.fireEvent("beforeLoad");

            this.loadContent(callback)
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
                    result.push({
                        permission: t == "author" ? "作者" : "阅读",
                        permissionObjectType: type,
                        permissionObjectName: dn
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
                module.save(history);
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
        window.open("/x_desktop/printcmsdoc.html?documentid=" + this.businessData.document.id + "&form=" + form);
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
        if (this.officeList) {
            this.officeList.each(function (module) {
                module.save(history);
            });
        }
        this.documentAction.publishDocumentComplex(documentData, function (json) {
            this.businessData.data.isNew = false;
            this.fireEvent("afterPublish");
            if (callback) callback();
            if (this.app.mobile) {
                this.app.content.unmask();
                console.log('这里是移动端');
            } else {
                if (this.businessData.document.title) {
                    this.app.notice(MWF.xApplication.cms.Xform.LP.documentPublished + ": “" + this.businessData.document.title + "”", "success");
                } else {
                    this.app.notice(MWF.xApplication.cms.Xform.LP.documentPublished, "success");
                }
                this.options.saveOnClose = false;
            }
            this.app.close();
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
            if (this.app.options.formEditId) options.formEditId = this.app.options.formEditId;
            this.app.desktop.openApplication(null, "cms.Document", options);
            this.app.close();
        }
    },

    //2019-11-29 移动端 开启编辑模式
    editDocumentForMobile: function () {
        if (this.app.mobile) {
            this.app.options.readonly = false;
            this.app.loadDocument(this.app.options);
        }
    },

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
        window.open("/x_desktop/printWork.html?workid=" + this.businessData.work.id + "&app=" + this.businessData.work.application + "&form=" + form);
    },
    openWindow: function (form, app) {
        var form = form;
        if (!form) {
            form = this.json.id;
        }
        if (this.businessData.document) {
            //var application = app;
            //window.open("/x_desktop/printWork.html?workCompletedId="+this.businessData.workCompleted.id+"&app="+application+"&form="+form);
        }
    },

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