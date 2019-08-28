MWF.xDesktop.requireApp("process.FormDesigner", "Property", null, false);
MWF.xApplication.cms.FormDesigner.Property = MWF.CMSFCProperty = new Class({
    Extends: MWF.FCProperty,

    loadScriptEditor: function(scriptAreas, style){
        scriptAreas.each(function(node){
            var title = node.get("title");
            var name = node.get("name");
            var scriptContent = this.data[name];

            MWF.require("MWF.widget.ScriptArea", function(){
                var scriptArea = new MWF.widget.ScriptArea(node, {
                    "title": title,
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.formContentNode,
                    "onChange": function(){
                        this.data[name] = scriptArea.toJson();
                    }.bind(this),
                    "onSave": function(){
                        this.designer.saveForm();
                    }.bind(this),
                    "style": style || "default",
                    "helpStyle" : "cms"
                });
                scriptArea.load(scriptContent);
            }.bind(this));

        }.bind(this));
    },
    getViewList: function(callback, refresh){
        if (!this.views || refresh){
            this.form.designer.actions.listQueryView(this.form.designer.application.id, function(json){
                this.views = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    loadActionArea: function(){
        var actionAreas = this.propertyContent.getElements(".MWFActionArea");
        actionAreas.each(function(node){
            var name = node.get("name");
            var actionContent = this.data[name];
            MWF.xDesktop.requireApp("cms.FormDesigner", "widget.ActionsEditor", function(){
                var actionEditor = new MWF.xApplication.cms.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "onChange": function(){
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));
        }.bind(this));

        //var actionAreas = this.propertyContent.getElements(".MWFActionArea");
        //actionAreas.each(function(node){
        //    var name = node.get("name");
        //    var actionContent = this.data[name];
        //    MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function(){
        //
        //        var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
        //            "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
        //            "onChange": function(){
        //                this.data[name] = actionEditor.data;
        //                this.changeData(name);
        //            }.bind(this)
        //        });
        //        actionEditor.load(actionContent);
        //    }.bind(this));
        //
        //}.bind(this));

        var actionAreas = this.propertyContent.getElements(".MWFDefaultActionArea");
        actionAreas.each(function(node){
            var name = node.get("name");
            var actionContent = this.data[name] || this.module.defaultToolBarsData;
            MWF.xDesktop.requireApp("cms.FormDesigner", "widget.ActionsEditor", function(){
                var actionEditor = new MWF.xApplication.cms.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "isSystemTool" : true,
                    "noCreate": true,
                    "noDelete": false,
                    "noCode": true,
                    "noReadShow": true,
                    "noEditShow": true,
                    "onChange": function(){
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));

        }.bind(this));

    },
    //loadActionArea: function(){
    //    var actionAreas = this.propertyContent.getElements(".MWFActionArea");
    //    actionAreas.each(function(node){
    //        var name = node.get("name");
    //        var actionContent = this.data[name];
    //
    //        MWF.xDesktop.requireApp("cms.FormDesigner", "widget.ActionsEditor", function(){
    //            var actionEditor = new MWF.xApplication.cms.FormDesigner.widget.ActionsEditor(node, this.designer, {
    //                "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
    //                "onChange": function(){
    //                    this.data[name] = actionEditor.data;
    //                }.bind(this)
    //            });
    //            actionEditor.load(actionContent);
    //        }.bind(this));
    //    }.bind(this));
    //},
    loadEventsEditor: function(){
        var events = this.propertyContent.getElement(".MWFEventsArea");
        if (events){
            var name = events.get("name");
            var eventsObj = this.data[name];
            MWF.xDesktop.requireApp("cms.FormDesigner", "widget.EventsEditor", function(){
                var eventsEditor = new MWF.xApplication.cms.FormDesigner.widget.EventsEditor(events, this.designer, {
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.formContentNode
                });
                eventsEditor.load(eventsObj);
            }.bind(this));
        }
    },
    loadValidation: function(){
        var nodes = this.propertyContent.getElements(".MWFValidation");
        if (nodes.length){
            nodes.each(function(node){
                var name = node.get("name");
                MWF.xDesktop.requireApp("cms.FormDesigner", "widget.ValidationEditor", function(){
                    var validationEditor = new MWF.xApplication.cms.FormDesigner.widget.ValidationEditor(node, this.designer, {
                        "onChange": function(){
                            var data = validationEditor.getValidationData();
                            this.data[name] = data;
                        }.bind(this)
                    });
                    validationEditor.load(this.data[name])
                }.bind(this));

                //new MWF.xApplication.process.FormDesigner.widget.ValidationEditor(node, this.designer);
            }.bind(this));
        }
    }
});

MWF.xApplication.cms.FormDesigner.PropertyMulti = new Class({
    Extends: MWF.xApplication.cms.FormDesigner.Property,
    Implements: [Options, Events],

    initialize: function(form, modules, propertyNode, designer, options){
        this.setOptions(options);
        this.modules = modules;
        this.form = form;
        //    this.data = module.json;
        this.data = {};
        this.htmlPath = this.options.path;
        this.designer = designer;
        this.maplists = {};
        this.propertyNode = propertyNode;
    },
    load: function(){
        if (this.fireEvent("queryLoad")){
            MWF.getRequestText(this.htmlPath, function(responseText, responseXML){
                this.htmlString = responseText;
                MWF.require("MWF.widget.JsonTemplate", function(){
                    this.fireEvent("postLoad");
                }.bind(this));
            }.bind(this));
        }
    },
    show: function(){
        if (!this.propertyContent){
            if (this.htmlString){
                this.JsonTemplate = new MWF.widget.JsonTemplate({}, this.htmlString);
                this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
                this.propertyContent.set("html", this.JsonTemplate.load());

                this.setEditNodeEvent();
                this.setEditNodeStyles(this.propertyContent);
                this.loadPropertyTab();
                this.loadMaplist();
                this.loadScriptArea();
                this.loadTreeData();
                this.loadArrayList();
                //this.loadEventsEditor();
                //this.loadHTMLArea();
                //this.loadJSONArea();
//			this.loadScriptInput();
                //MWF.process.widget.EventsEditor
            }
        }else{
            this.propertyContent.setStyle("display", "block");
        }

    },
    hide: function(){
        if (this.propertyContent) this.propertyContent.destroy();
    },
    changeStyle: function(name){
        this.modules.each(function(module){
            module.setPropertiesOrStyles(name);
        }.bind(this));
    },
    changeData: function(name, input, oldValue){

        this.modules.each(function(module){
            module._setEditStyle(name, input, oldValue);
        }.bind(this));
    },
    changeJsonDate: function(key, value){
        //alert(key+": "+value );
        this.modules.each(function(module){
            module.json[key] = value;
        }.bind(this));
    }
});