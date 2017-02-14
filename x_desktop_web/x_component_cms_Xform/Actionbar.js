MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.require("MWF.widget.Tree", null, false);
MWF.xApplication.cms.Xform.Actionbar = MWF.CMSActionbar =  new Class({
	Extends: MWF.CMS$Module,

	_loadUserInterface: function(){
        if (this.form.json.mode == "Mobile"){
            this.node.empty();
        }else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();

            MWF.require("MWF.widget.SimpleToolbar", function(){
                this.toolbarWidget = new MWF.widget.SimpleToolbar(this.toolbarNode, {"style": this.json.style}, this);
                //alert(this.readonly)
                if (this.readonly){
                    this.setToolbars(this.json.sysTools.readTools, this.toolbarNode);
 //                   this.setToolbars(this.json.tools.readTools, this.toolbarNode);
                }else{
                    this.setToolbars(this.json.sysTools.editTools, this.toolbarNode);
 //                   this.setToolbars(this.json.tools.editTools, this.toolbarNode);
                }
                this.setCustomToolbars(this.json.tools, this.toolbarNode);


                this.toolbarWidget.load();

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
        }
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
                    var hideFlag = this.form.CMSMacro.exec(tool.condition, this);
                    flag = !hideFlag;
                }
                if (flag){
                    var actionNode = new Element("div", {
                        "id": tool.id,
                        "MWFnodetype": tool.type,
                        "MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                        "MWFButtonImageOver": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img_over,
                        "title": tool.title,
                        "MWFButtonAction": "runCustomAction",
                        "MWFButtonText": tool.text,
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
    setToolbars: function(tools, node){
        tools.each(function(tool){
            var flag = true;
            if (tool.control){
                flag = this.form.businessData.control[tool.control]
            }
            //if (tool.id == "action_cmsWork"){
            //    if (!this.form.businessData.task){
            //        flag = false;
            //    }
            //}
            if (flag){
                var actionNode = new Element("div", {
                    "id": tool.id,
                    "MWFnodetype": tool.type,
                    "MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                    "MWFButtonImageOver": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img_over,
                    "title": tool.title,
                    "MWFButtonAction": tool.action,
                    "MWFButtonText": tool.text,
                }).inject(node);
                if (tool.sub){
                    var subNode = node.getLast();
                    this.setToolbars(tool.sub, subNode);
                }
            }
        }.bind(this));
    },
    runCustomAction: function(bt){
        var script = bt.node.retrieve("script");
        this.form.CMSMacro.exec(script, this);
    },
    saveDocument: function(){
        this.form.saveDocument();
    },
    closeDocument: function(){
        this.form.closeDocument();
    },
    publishDocument: function(){
        this.form.publishDocument();
    },
    archiveDocument: function(){
        this.form.archiveDocument();
    },
    redraftDocument: function(){
        this.form.redraftDocument();
    },
    deleteDocument: function(){
        this.form.deleteDocument();
    },
    editDocument: function(){
        this.form.editDocument();
    },
    setPopularDocument: function(){
        this.form.setPopularDocument();
    }
}); 