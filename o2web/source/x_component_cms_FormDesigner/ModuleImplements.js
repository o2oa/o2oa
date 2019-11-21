MWF.xDesktop.requireApp("process.FormDesigner", "Property", null, false);

if( !MWF.CMSProperty_Process ){

    MWF.CMSProperty_Process = {
        getViewList : MWF.xApplication.process.FormDesigner.Property.prototype.getViewList.$origin,
        loadScriptEditor : MWF.xApplication.process.FormDesigner.Property.prototype.loadScriptEditor.$origin,
        loadActionArea : MWF.xApplication.process.FormDesigner.Property.prototype.loadActionArea.$origin,
        loadEventsEditor : MWF.xApplication.process.FormDesigner.Property.prototype.loadEventsEditor.$origin,
        loadValidation : MWF.xApplication.process.FormDesigner.Property.prototype.loadValidation.$origin,
        loadFormFieldInput : MWF.xApplication.process.FormDesigner.Property.prototype.loadFormFieldInput.$origin,
        loadPersonInput : MWF.xApplication.process.FormDesigner.Property.prototype.loadPersonInput.$origin,
        loadProcessApplictionSelect : MWF.xApplication.process.FormDesigner.Property.prototype.loadProcessApplictionSelect.$origin
    };

    MWF.xApplication.process.FormDesigner.Property.implement({
        loadProcessApplictionSelect : function(node, appNodeName, callback){
            var isCMS= this.designer.options.name.toLowerCase().contains("cms");
            if( isCMS ){
                this.loadProcessApplictionSelect_CMS(node, appNodeName, callback);
            }else{
                this.loadProcessApplictionSelect_Process(node, appNodeName, callback);
            }
        },
        loadProcessApplictionSelect_Process : MWF.CMSProperty_Process.loadProcessApplictionSelect,
        loadProcessApplictionSelect_CMS : function(node, appNodeName, callback){
            var application = appNodeName ? this.data[appNodeName] : "";
            MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function() {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "title" : this.form.designer.lp.selectApplication,
                    "type": "CMSApplication",
                    "count" : 1,
                    "names": application ? [ {id : application} ] : [],
                    "onChange": function (apps) {
                        callback(apps)
                    }.bind(this)
                });
            }.bind(this))
        },
        loadFormFieldInput: function(){
            //var isCMS = layout.desktop.currentApp.options.name.toLowerCase().contains("cms");
            var isCMS= this.designer.options.name.toLowerCase().contains("cms");
            if( isCMS ){
                this.loadFormFieldInput_CMS();
            }else{
                this.loadFormFieldInput_Process();
            }
        },
        loadFormFieldInput_Process : MWF.CMSProperty_Process.loadFormFieldInput,
        loadFormFieldInput_CMS: function(){
            var fieldNodes = this.propertyContent.getElements(".MWFFormFieldPerson");
            MWF.xDesktop.requireApp("cms.QueryViewDesigner", "widget.PersonSelector", function(){
                fieldNodes.each(function(node){
                    new MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector(node, this.form.designer, {
                        "type": "formField",
                        "form": this.form.json.id,
                        "fieldType": "person",
                        "names": this.data[node.get("name")],
                        "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                    });
                }.bind(this));
            }.bind(this));
        },
        //getViewList: function(callback, refresh){
        //    var isCMS= this.designer.options.name.toLowerCase().contains("cms");
        //    if( isCMS ){
        //        this.getViewList_CMS( callback, refresh );
        //    }else{
        //        this.getViewList_Process( callback, refresh );
        //    }
        //},
        //getViewList_Process : MWF.CMSProperty_Process.getViewList,
        //getViewList_CMS : function(callback, refresh){
        //    if (!this.views || refresh){
        //        this.form.designer.actions.listQueryView(this.form.designer.application.id, function(json){
        //            this.views = json.data;
        //            if (callback) callback();
        //        }.bind(this));
        //    }else{
        //        if (callback) callback();
        //    }
        //},
        loadScriptEditor: function(scriptAreas, style){
            //var isCMS = layout.desktop.currentApp.options.name.toLowerCase().contains("cms");
            var isCMS= this.designer.options.name.toLowerCase().contains("cms");
            if( isCMS ){
                this.loadScriptEditor_CMS( scriptAreas, style );
            }else{
                this.loadScriptEditor_Process( scriptAreas, style );
            }
        },
        loadScriptEditor_Process : MWF.CMSProperty_Process.loadScriptEditor,
        loadScriptEditor_CMS: function(scriptAreas, style){
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
                        "helpStyle" :  "cms"
                    });
                    scriptArea.load(scriptContent);
                }.bind(this));

            }.bind(this));
        },
        loadActionArea: function(){
            //var isCMS = layout.desktop.currentApp.options.name.toLowerCase().contains("cms");
            var isCMS= this.designer.options.name.toLowerCase().contains("cms");
            if( isCMS ){
                this.loadActionArea_CMS(  );
            }else{
                this.loadActionArea_Process( );
            }
        },
        loadActionArea_Process : MWF.CMSProperty_Process.loadActionArea,
        loadActionArea_CMS: function(){
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
        //loadActionArea_CMS: function(){
        //    var actionAreas = this.propertyContent.getElements(".MWFActionArea");
        //    actionAreas.each(function(node){
        //        var name = node.get("name");
        //        var actionContent = this.data[name];
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
            //var isCMS = layout.desktop.currentApp.options.name.toLowerCase().contains("cms");
            var isCMS= this.designer.options.name.toLowerCase().contains("cms");
            if( isCMS ){
                this.loadEventsEditor_CMS(  );
            }else{
                this.loadEventsEditor_Process( );
            }
        },
        loadEventsEditor_Process : MWF.CMSProperty_Process.loadEventsEditor,
        loadEventsEditor_CMS: function(){
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
            //var isCMS = layout.desktop.currentApp.options.name.toLowerCase().contains("cms");
            var isCMS= this.designer.options.name.toLowerCase().contains("cms");
            if( isCMS ){
                this.loadValidation_CMS();
            }else{
                this.loadValidation_Process();
            }
        },
        loadValidation_Process : MWF.CMSProperty_Process.loadValidation,
        loadValidation_CMS: function(){
            MWF.xDesktop.requireApp("cms.FormDesigner", "widget.ValidationEditor", null, false);
            var nodes = this.propertyContent.getElements(".MWFValidation");
            if (nodes.length){
                nodes.each(function(node){
                    var name = node.get("name");
                    var validationEditor = new MWF.xApplication.cms.FormDesigner.widget.ValidationEditor(node, this.designer, {
                        "onChange": function(){
                            var data = validationEditor.getValidationData();
                            this.data[name] = data;
                        }.bind(this)
                    });
                    validationEditor.load(this.data[name]);
                    //new MWF.xApplication.process.FormDesigner.widget.ValidationEditor(node, this.designer);
                }.bind(this));
            }
        }//,
    //    loadPersonInput: function(){
    //        var isCMS= this.designer.options.name.toLowerCase().contains("cms");
    //        if( isCMS ){
    //            this.loadPersonInput_CMS();
    //        }else{
    //            this.loadPersonInput_Process();
    //        }
    //    },
    //    loadPersonInput_Process : MWF.CMSProperty_Process.loadPersonInput,
    //    loadPersonInput_CMS: function(){
    //        var personNameNodes = this.propertyContent.getElements(".MWFPersonName");
    //        var personIdentityNodes = this.propertyContent.getElements(".MWFPersonIdentity");
    //        var personUnitNodes = this.propertyContent.getElements(".MWFPersonUnit");
    //        var dutyNodes = this.propertyContent.getElements(".MWFDutySelector");
    //
    //        var viewNodes = this.propertyContent.getElements(".MWFViewSelect");
    //        var cmsviewNodes = this.propertyContent.getElements(".MWFCMSViewSelect");
    //
    //        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function(){
    //            personNameNodes.each(function(node){
    //                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
    //                    "type": "person",
    //                    "names": this.data[node.get("name")],
    //                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
    //                });
    //            }.bind(this));
    //
    //            personIdentityNodes.each(function(node){
    //                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
    //                    "type": "identity",
    //                    "names": this.data[node.get("name")],
    //                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
    //                });
    //            }.bind(this));
    //
    //
    //            personUnitNodes.each(function(node){
    //                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
    //                    "type": "unit",
    //                    "names": this.data[node.get("name")],
    //                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
    //                });
    //            }.bind(this));
    //
    //            dutyNodes.each(function(node){
    //                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
    //                    "type": "duty",
    //                    "names": this.data[node.get("name")],
    //                    "onChange": function(ids){this.addDutyItem(node, ids);}.bind(this),
    //                    "onRemoveDuty": function(item){this.removeDutyItem(node, item);}.bind(this)
    //                });
    //            }.bind(this));
    //
    //            viewNodes.each(function(node){
    //                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
    //                    "type": "View",
    //                    "count": 1,
    //                    "names": [this.data[node.get("name")]],
    //                    "onChange": function(ids){this.saveViewItem(node, ids);}.bind(this)
    //                });
    //            }.bind(this));
    //
    //            cmsviewNodes.each(function(node){
    //                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
    //                    "type": "CMSView",
    //                    "count": 1,
    //                    "names": [this.data[node.get("name")]],
    //                    "onChange": function(ids){this.saveViewItem(node, ids);}.bind(this)
    //                });
    //            }.bind(this));
    //        }.bind(this));
    //    }
    });

}

MWF.xApplication.cms.FormDesigner.ModuleImplements = MWF.CMSFCMI = new Class({

});