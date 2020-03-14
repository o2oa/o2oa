//MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.require("MWF.widget.Tree", null, false);
//MWF.require("MWF.widget.Toolbar", null, false);
MWF.xApplication.process.Xform.Actionbar = MWF.APPActionbar =  new Class({
	Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad", "afterLoad"]
    },
	_loadUserInterface: function(){
        // if (this.form.json.mode == "Mobile"){
        //     this.node.empty();
        // }else if (COMMON.Browser.Platform.isMobile){
        //     this.node.empty();
        // }else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();

            MWF.require("MWF.widget.Toolbar", function(){
                this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {
                    "style": this.json.style,
                    "onPostLoad" : function(){
                        this.fireEvent("afterLoad");
                    }.bind(this)
                }, this);
                if (this.json.actionStyles) this.toolbarWidget.css = this.json.actionStyles;
                //alert(this.readonly)

                if (this.json.hideSystemTools){
                    this.setCustomToolbars(this.json.tools, this.toolbarNode);
                    this.toolbarWidget.load();
                }else{
                    if (this.json.defaultTools){
                        var addActions = [
                            {
                                "type": "MWFToolBarButton",
                                "img": "read.png",
                                "title": "标记为已阅",
                                "action": "readedWork",
                                "text": "已阅",
                                "id": "action_readed",
                                "control": "allowReadProcessing",
                                "condition": "",
                                "read": true
                            }
                        ];
                        //this.form.businessData.control.allowReflow =

                        //this.json.defaultTools.push(o);
                        this.setToolbars(this.json.defaultTools, this.toolbarNode, this.readonly);
                        this.setToolbars(addActions, this.toolbarNode, this.readonly);

                        this.setCustomToolbars(this.json.tools, this.toolbarNode);
                        this.toolbarWidget.load();
                    }else{
                        MWF.getJSON(this.form.path+"toolbars.json", function(json){
                            this.setToolbars(json, this.toolbarNode, this.readonly, true);
                            this.setCustomToolbars(this.json.tools, this.toolbarNode);
                            this.toolbarWidget.load();
                        }.bind(this), null);
                    }
                }

            }.bind(this));
        // }
	},

    setCustomToolbars: function(tools, node){
        var path = "/x_component_process_FormDesigner/Module/Actionbar/";
        var iconPath = "";
        if( this.json.customIconStyle ){
            iconPath = this.json.customIconStyle+"/";
        }
        tools.each(function(tool){
            var flag = true;
            if (this.readonly){
                flag = tool.readShow;
            }else{
                flag = tool.editShow;
            }
            if (flag){
                flag = true;
                if (tool.control){
                    flag = this.form.businessData.control[tool.control]
                }
                if (tool.condition){
                    var hideFlag = this.form.Macro.exec(tool.condition, this);
                    flag = !hideFlag;
                }
                if (flag){
                    var actionNode = new Element("div", {
                        "id": tool.id,
                        "MWFnodetype": tool.type,
                        "MWFButtonImage": path+""+this.form.options.style+"/custom/"+iconPath+tool.img,
                        "title": tool.title,
                        "MWFButtonAction": "runCustomAction",
                        "MWFButtonText": tool.text
                    }).inject(node);
                    if( this.json.customIconOverStyle ){
                        actionNode.set("MWFButtonImageOver" , path+""+this.form.options.style +"/custom/"+this.json.customIconOverStyle+ "/" +tool.img );
                    }
                    if( tool.properties ){
                        actionNode.set(tool.properties);
                    }
                    if (tool.actionScript){
                        actionNode.store("script", tool.actionScript);
                    }
                    if (tool.sub){
                        var subNode = node.getLast();
                        this.setCustomToolbars(tool.sub, subNode);
                    }
                }
            }
        }.bind(this));
    },

    setToolbarItem: function(tool, node, readonly, noCondition){
        var path = "/x_component_process_FormDesigner/Module/Actionbar/";
        var flag = true;
        if (tool.control){
            flag = this.form.businessData.control[tool.control]
        }
        if (!noCondition) if (tool.condition){
            var hideFlag = this.form.Macro.exec(tool.condition, this);
            flag = flag && (!hideFlag);
        }
        if (tool.id == "action_processWork"){
            if (!this.form.businessData.task){
                flag = false;
            }
        }
        if (tool.id == "action_rollback") tool.read = true;
        if (readonly) if (!tool.read) flag = false;
        if (flag){
            var actionNode = new Element("div", {
                "id": tool.id,
                "MWFnodetype": tool.type,
                //"MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                "MWFButtonImage": path+(this.options.style||"default") +"/tools/"+ (this.json.style || "default") +"/"+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            if( this.json.iconOverStyle ){
                actionNode.set("MWFButtonImageOver" , path+""+(this.options.style||"default")+"/tools/"+( this.json.iconOverStyle || "default" )+"/"+tool.img );
            }
            if( tool.properties ){
                actionNode.set(tool.properties);
            }
            if (tool.sub){
                var subNode = node.getLast();
                this.setToolbars(tool.sub, subNode, readonly, noCondition);
            }
        }
    },
    setToolbars: function(tools, node, readonly, noCondition){
        tools.each(function(tool){
            this.setToolbarItem(tool, node, readonly, noCondition);
        }.bind(this));
    },
    runCustomAction: function(bt){
        var script = bt.node.retrieve("script");
        this.form.Macro.exec(script, this);
    },
    saveWork: function(){
        this.form.saveWork();
    },
    closeWork: function(){
        this.form.closeWork();
    },
    processWork: function(){
        this.form.processWork();
    },
    resetWork: function(){
        this.form.resetWork();
    },
    retractWork: function(e, ev){
        this.form.retractWork(e, ev);
    },
    rerouteWork: function(e, ev){
        this.form.rerouteWork(e, ev);
    },
    deleteWork: function(){
        this.form.deleteWork();
    },
    printWork: function(){
        this.form.printWork();
    },
    readedWork: function(b,e){
        this.form.readedWork(e);
    },
    addSplit: function(e){
        this.form.addSplit(e);
    },
    rollback: function(e){
        this.form.rollback(e);
    },
    downloadAll: function(e){
        this.form.downloadAll(e);
    },
    pressWork: function(e){
        this.form.pressWork(e);
    }

}); 