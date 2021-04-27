MWF.xDesktop.requireApp("process.Xform", "Actionbar", null, false);
MWF.xApplication.cms.Xform.Actionbar = MWF.CMSActionbar =  new Class({
    Extends: MWF.APPActionbar,
    reload : function(){
        this._loadUserInterface();
    },
    _loadUserInterface: function(){
        debugger;
        //if (this.form.json.mode == "Mobile"){
        //    this.node.empty();
        //}else if (COMMON.Browser.Platform.isMobile){
        //    this.node.empty();
        //}else{
        this.toolbarNode = this.node.getFirst("div");
        if (!this.toolbarNode)  this.toolbarNode = this.node;
        if (this.toolbarNode) this.toolbarNode.empty();

        //this.node.empty();

        MWF.require("MWF.widget.SimpleToolbar", function(){
            this.toolbarWidget = new MWF.widget.SimpleToolbar(this.toolbarNode, {
                "style": this.json.style,
                "onPostLoad" : function(){
                    this.fireEvent("afterLoad");
                }.bind(this)
            }, this);
            if (this.json.actionStyles) this.toolbarWidget.css = this.json.actionStyles;

            //var json = this.readonly ? this.json.sysTools.readTools : this.json.sysTools.editTools;
            //if( this.json.style == "xform_red_simple" ){
            //    json.each( function( j ){
            //        var names = j.img.split(".");
            //        j.img = names[0] + "_red." + names[1];
            //    });
            //}
            //this.setToolbars(json, this.toolbarNode);
            //this.setCustomToolbars(this.json.tools, this.toolbarNode);
            //
            //this.toolbarWidget.load();
            if( this.json.multiTools ){

                var jsonStr = JSON.stringify(this.json.multiTools);
                jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.cms.Xform.LP.form});
                this.json.multiTools = JSON.parse(jsonStr);

                this.json.multiTools.each( function (tool) {
                    if( tool.system ){
                        if( !this.json.hideSystemTools ){
                            this.setToolbars( [tool], this.toolbarNode, this.readonly);
                        }
                    }else{
                        this.setCustomToolbars([tool], this.toolbarNode);
                    }
                }.bind(this));
                this.toolbarWidget.load();
            }else{
                if (this.json.hideSystemTools){
                    this.setCustomToolbars(this.json.tools, this.toolbarNode);
                    this.toolbarWidget.load();
                }else{
                    if (this.json.defaultTools){
                        this.setToolbars(this.json.defaultTools, this.toolbarNode, this.readonly);
                        this.setCustomToolbars(this.json.tools, this.toolbarNode);
                        this.toolbarWidget.load();
                    }else{
                        MWF.getJSON("../x_component_cms_Xform/$Form/toolbars.json", function(json){
                            this.setToolbars(json, this.toolbarNode, this.readonly, true);
                            this.setCustomToolbars(this.json.tools, this.toolbarNode);

                            this.toolbarWidget.load();
                        }.bind(this), false);
                    }

                    //MWF.getJSON("../x_component_cms_Xform/$Form/toolbars.json", function(json){
                    //    this.setToolbars(json, this.toolbarNode, this.readonly);
                    //    this.setCustomToolbars(this.json.tools, this.toolbarNode);
                    //
                    //    this.toolbarWidget.load();
                    //}.bind(this), false);
                }
            }
        }.bind(this));
        //}
    },
    setCustomToolbars: function(tools, node){
        var path = "../x_component_cms_FormDesigner/Module/Actionbar/";
        //var style = (this.json.style || "default").indexOf("red") > -1 ? "red" : "blue";
        var style;
        if( this.json.customIconStyle ){
            style = this.json.customIconStyle;
        }else{
            style = (this.json.style || "default").indexOf("red") > -1 ? "red" : "blue";
        }
        var style_over = this.json.customIconOverStyle || "white";

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
                        "MWFButtonImage": path+""+this.form.options.style+"/custom/"+ style + "/" +tool.img,
                        "MWFButtonImageOver": path+""+this.form.options.style+"/custom/" + style_over + "/"+tool.img,
                        //"MWFButtonImage": "../x_component_cms_FormDesigner/Module/Actionbar/"+ (this.options.style||"default") +"/tools/"+ (this.json.style || "default") +"/"+tool.img,
                        //"MWFButtonImageOver": "../x_component_cms_FormDesigner/Module/Actionbar/"+ (this.options.style||"default")+"/tools/"+ (this.json.style || "default") +"/"+tool.img_over,
                        "title": tool.title,
                        "MWFButtonAction": "runCustomAction",
                        "MWFButtonText": tool.text
                    }).inject(node);
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
    setToolbars: function(tools, node, readonly, noCondition){
        tools.each(function(tool){
            var flag = true;
            if (tool.control){
                flag = this.form.businessData.control[tool.control]
            }
            if (!noCondition) if (tool.condition){
                var hideFlag = this.form.Macro.exec(tool.condition, this);
                flag = flag && (!hideFlag);
            }
            //if (tool.id == "action_processWork"){
            //    if (!this.form.businessData.task){
            //        flag = false;
            //    }
            //}
            if (readonly){
                if (!tool.read) flag = false;
            }else{
                if (!tool.edit) flag = false;
            }
            if (this.json.hideSetPopularDocumentTool && tool.id == "action_popular"){
                flag = false;
            }
            if (flag){
                var actionNode = new Element("div", {
                    "id": tool.id,
                    "MWFnodetype": tool.type,
                    "MWFButtonImage": "../x_component_cms_FormDesigner/Module/Actionbar/"+(this.options.style||"default") +"/tools/"+ (this.json.style || "default") +"/"+tool.img,
                    "MWFButtonImageOver": "../x_component_cms_FormDesigner/Module/Actionbar/"+(this.options.style||"default")+"/tools/"+ (this.json.style || "default") +"/"+tool.img_over,
                    "title": tool.title,
                    "MWFButtonAction": tool.action,
                    "MWFButtonText": tool.text
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
        this.form.Macro.exec(script, this);
    },
    saveDocument: function(){
        this.form.saveDocument();
    },
    saveDraftDocument: function(){
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
    },
    printDocument: function(){
        this.form.printDocument();
    }
}); 