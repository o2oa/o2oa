MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.require("MWF.widget.Tree", null, false);
MWF.xApplication.process.Xform.Actionbar = MWF.APPActionbar =  new Class({
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
        // if (this.form.json.mode == "Mobile"){
        //     this.node.empty();
        // }else if (COMMON.Browser.Platform.isMobile){
        //     this.node.empty();
        // }else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();

            MWF.require("MWF.widget.Toolbar", function(){
                this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);
                //alert(this.readonly)

                if (this.json.hideSystemTools){
                    his.setCustomToolbars(this.json.tools, this.toolbarNode);
                    this.toolbarWidget.load();
                }else{
                    debugger;
                    if (this.json.defaultTools){
                        this.setToolbars(this.json.defaultTools, this.toolbarNode, this.readonly);
                        this.setCustomToolbars(this.json.tools, this.toolbarNode);
                        this.toolbarWidget.load();
                    }else{
                        MWF.getJSON(this.form.path+"toolbars.json", function(json){
                            this.setToolbars(json, this.toolbarNode, this.readonly, true);
                            this.setCustomToolbars(this.json.tools, this.toolbarNode);
                            this.toolbarWidget.load();
                        }.bind(this), false);
                    }
                }


 //               if (this.readonly){
 //                   this.setToolbars(this.json.sysTools.readTools, this.toolbarNode);
 ////                   this.setToolbars(this.json.tools.readTools, this.toolbarNode);
 //               }else{
 //                   this.setToolbars(this.json.sysTools.editTools, this.toolbarNode);
 ////                   this.setToolbars(this.json.tools.editTools, this.toolbarNode);
 //               }
 //               this.setCustomToolbars(this.json.tools, this.toolbarNode);
 //
 //
 //               this.toolbarWidget.load();

                //   var size = this.toolbarNode.getSize();
                //   this.node.setStyle("height", ""+size.y+"px");
                //   var nodeSize = this.toolbarNode.getSize();
                //   this.toolbarNode.setStyles({
                //       "width": ""+nodeSize.x+"px",
                ////       "position": "absolute",
                //       "z-index": 50000
                //   });
                //   this.toolbarNode.position({"relativeTo": this.node, "position": "upperLeft", "edge": "upperLeft"});
                //
                //   this.form.node.addEvent("scroll", function(){
                //       alert("ddd")
                //   }.bind(this));

            }.bind(this));
        // }
	},
    setCustomToolbars: function(tools, node){
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
                        "MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                        "title": tool.title,
                        "MWFButtonAction": "runCustomAction",
                        "MWFButtonText": tool.text
                    }).inject(node);
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
    setToolbars: function(tools, node, readonly, noCondition){
        debugger;
        tools.each(function(tool){
            var flag = true;
            if (tool.control){
                flag = this.form.businessData.control[tool.control]
            }
            if (!noCondition) if (tool.condition){
                var hideFlag = this.form.Macro.exec(tool.condition, this);
                flag = !hideFlag;
            }
            if (tool.id == "action_processWork"){
                if (!this.form.businessData.task){
                    flag = false;
                }
            }
            if (readonly) if (!tool.read) flag = false;
            if (flag){
                var actionNode = new Element("div", {
                    "id": tool.id,
                    "MWFnodetype": tool.type,
                    "MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                    "title": tool.title,
                    "MWFButtonAction": tool.action,
                    "MWFButtonText": tool.text
                }).inject(node);
                if (tool.sub){
                    var subNode = node.getLast();
                    this.setToolbars(tool.sub, subNode, readonly, noCondition);
                }
            }
        }.bind(this));
    },
    runCustomAction: function(bt){
        var script = bt.node.retrieve("script");
        this.form.Macro.exec(script, this);
    },
    saveWork: function(){
        debugger;
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
    }
}); 