MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};

MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xDesktop.requireApp("process.Xform", "Form", null, false);
MWF.require("MWF.widget.Identity", null,false);

MWF.xDesktop.requireApp("cms.Xform", "Package", null, false);
MWF.xApplication.cms.Xform.Form = MWF.CMSForm =  new Class({
    Implements: [Options, Events],
    Extends: MWF.APPForm,
    options: {
        "style": "default",
        "readonly": false,
        "cssPath": "",
        "autoSave" : false,
        "saveOnClose" : false,
        "showAttachment" : true,
        "moduleEvents": ["postLoad", "afterLoad", "beforeSave", "afterSave", "beforeClose", "beforePublish", "afterPublish"]
    },
    initialize: function(node, data, options){
        this.setOptions(options);

        this.container = $(node);
        this.container.setStyle("-webkit-user-select", "text");
        this.data = data;
        this.json = data.json;
        this.html = data.html;

        this.path = "/x_component_process_Xform/$Form/";
        this.cssPath = this.options.cssPath || "/x_component_process_Xform/$Form/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.modules = [];
        this.all = {};
        this.forms = {};

        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    load: function(){
        if (this.app){
            if (this.app.formNode) this.app.formNode.setStyles(this.json.styles);
        }
        //if (!this.businessData.control.allowSave) this.setOptions({"readonly": true});
        if (this.fireEvent("queryLoad")){
            this.fireEvent("beforeLoad");

            MWF.xDesktop.requireApp("cms.Xform", "lp."+MWF.language, null, false);
            //		this.container.setStyles(this.css.container);
            this._loadBusinessData();

            this.Macro = new MWF.CMSMacro.CMSFormContext(this);

            this._loadHtml();
            this._loadForm();
            this._loadModules(this.node);
            if(!this.options.readonly){
                if(this.options.autoSave)this.autoSave();
                this.app.addEvent("queryClose", function(){
                    if( this.options.saveOnClose && this.businessData.document.docStatus == "draft" )this.saveDocument(null, true);
                    //if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
                }.bind(this));
            }

            this.fireEvent("postLoad");
            this.fireEvent("afterLoad");
        }
    },
    autoSave: function(){
        //this.autoSaveTimerID = window.setInterval(function(){
        //    this.saveDocument();
        //}.bind(this), 300000);
    },
    _loadBusinessData: function(){
        if (!this.businessData){
            this.businessData = {
                "data": {

                }
            };
        }
    },

    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!=-1){
                    this.addEvent(key, function(event){
                        return this.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    if (key=="load"){
                        this.addEvent("postLoad", function(){
                            return this.Macro.fire(e.code, this);
                        }.bind(this));
                    }else if (key=="submit"){
                        this.addEvent("beforePublish", function(){
                            return this.Macro.fire(e.code, this);
                        }.bind(this));
                    }else{
                        this.node.addEvent(key, function(event){
                            return this.Macro.fire(e.code, this, event);
                        }.bind(this));
                    }
                }
            }
        }.bind(this));
    },



    _loadModules: function(dom){
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

        moduleNodes.each(function(node){
            var json = this._getDomjson(node);
            if( !this.options.showAttachment && json.type == "Attachment" ){
                return;
            }
            var module = this._loadModule(json, node);
            this.modules.push(module);
        }.bind(this));
    },
    _loadModule: function(json, node, beforeLoad){
        if( !json )return;
        var module = new MWF["CMS"+json.type](node, json, this);
        if (beforeLoad) beforeLoad.apply(module);
        if (!this.all[json.id]) this.all[json.id] = module;
        if (module.field){
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
    trim: function( array ){
        var arr = [];
        array.each(function(v){
            if (v) arr.push(v);
        });
        return arr;
    },
    transportReaderData : function( data ){
        var cnArray = ["公司","部门","人员","群组"];
        var keyArray = ["companyValue","departmentValue","personValue","groupValue"];
        var result = [];
        data.each( function( item ){
            for( var key in item ){
                var it = item[key];
                it.each( function( i ){
                    result.push({
                        permission : "阅读",
                        permissionObjectType : cnArray[ keyArray.indexOf(key) ],
                        permissionObjectName : i.name
                    })
                })
            }
        });
        return result.length > 0 ? result : null;
    },
    getReaderData: function(){
        var data= this.businessData.data;
        var readers = [];
        Object.each(this.forms, function(module, id){
            if( module.json.type == "Readerfield" ){
                if (module.json.section=="yes"){
                    readers = readers.concat( this.getSectionData(module, data[id]) );
                }else{
                    readers = readers.concat( module.getData() );
                }
            }
        });
        r = this.transportReaderData( readers );
        return r;
    },
    getDocumentData: function( formData ){
        var data= Object.clone(this.businessData.document);
        if( formData.htmleditor ){
            var div = new Element( "div" , {
                "styles" : { "display" : "none" },
                "html" : formData.htmleditor
            } ).inject( this.container );
            div.getElements("img").each( function( el ){
                el.setStyle( "max-width" , "100%" );
            });
            formData.htmleditor = div.get("html");
            div.destroy();
        }
        if( formData.subject ){
            data.title = formData.subject;
            data.subject = formData.subject;
            this.businessData.document.title = formData.subject;
            this.businessData.document.subject = formData.subject;
        }
        data.isNewDocument = false;
        return data;
    },
    saveDocument: function(callback, sync ){
        this.fireEvent("beforeSave");
        if( this.businessData.document.docStatus == "published" ){
            if (!this.formValidation("publish")){
                if (callback) callback();
                return false;
            }
        }
        var data = this.getData();
        var documentData = this.getDocumentData(data);
        documentData.permissionList = this.getReaderData();
        delete documentData.attachmentList;
        this.documentAction.saveDocument(documentData, function(){
            this.documentAction.saveData(function(json){
                this.notice(MWF.xApplication.cms.Xform.LP.dataSaved, "success");
                this.businessData.data.isNew = false;
                this.fireEvent("afterSave");
                if (callback) callback();
            }.bind(this), null, this.businessData.document.id, data, !sync );
        }.bind(this),null, !sync );
    },
    closeDocument: function(){
        this.fireEvent("beforeClose");
        if (this.app){
            this.app.close();
        }
    },

    formValidation: function( status ){
        if (this.options.readonly) return true;
        var flag = true;
        //flag = this.validation();
        Object.each(this.forms, function(field, key){
            field.validationMode();
            if (!field.validation( status )){
                flag = false;
            }
        }.bind(this));
        return flag;
    },
    publishDocument: function(callback){
        this.fireEvent("beforePublish");
        this.app.content.mask({
            "destroyOnHide": true,
            "style": this.app.css.maskNode
        });
        if (!this.formValidation("publish")){
            this.app.content.unmask();
            if (callback) callback();
            return false;
        }

        var data = this.getData();
        var readerData = this.getReaderData();
        this.documentAction.saveData(function(json){
            this.businessData.data.isNew = false;
            var documentData = this.getDocumentData(data);
            documentData.permissionList = readerData;
            delete documentData.attachmentList;
            this.documentAction.saveDocument(documentData, function(){
                this.documentAction.publishDocument(documentData, function(json){
                    this.fireEvent("afterPublish");
                    this.fireEvent("postPublish");
                    if (callback) callback();
                    this.app.notice(MWF.xApplication.cms.Xform.LP.documentPublished+": “"+this.businessData.document.title+"”", "success");
                    this.options.saveOnClose = false;
                    this.app.close();
                    //this.close();
                }.bind(this) );
            }.bind(this))
        }.bind(this), null, this.businessData.document.id, data);
    },

    deleteDocument: function(){
        var _self = this;
        var p = MWF.getCenterPosition(this.app.content, 380, 150);
        var event = {
            "event":{
                "x": p.x,
                "y": p.y-200,
                "clientX": p.x,
                "clientY": p.y-200
            }
        }
        this.app.confirm("infor", event, MWF.xApplication.cms.Xform.LP.deleteDocumentTitle, MWF.xApplication.cms.Xform.LP.deleteDocumentText, 380, 120, function(){
            _self.app.content.mask({
                "style": {
                    "background-color": "#999",
                    "opacity": 0.6
                }
            });
            _self.documentAction.removeDocument(_self.businessData.document.id, function(json){
                _self.app.notice(MWF.xApplication.cms.Xform.LP.documentDelete+": “"+_self.businessData.document.title+"”", "success");
                _self.options.autoSave = false;
                _self.options.saveOnClose = false;
                _self.fireEvent("postDelete");
                _self.app.close();
                this.close();
            }.bind(this) );
            //this.close();
        }, function(){
            this.close();
        });
    },

    editDocument: function(){
        var options = {"documentId": this.businessData.document.id, "readonly" : false }; //this.explorer.app.options.application.allowControl};
        this.app.desktop.openApplication(null, "cms.Document", options);
        this.app.close();
    },

    setPopularDocument : function(){
        this.app.setPopularDocument();
    },

    printWork: function(app, form){
        var application = app || this.businessData.work.application;
        var form = form;
        if (!form){
            form = this.json.id;
            if (this.json.printForm) form = this.json.printForm;
        }
        window.open("/x_desktop/printWork.html?workid="+this.businessData.work.id+"&app="+this.businessData.work.application+"&form="+form);
    }



});