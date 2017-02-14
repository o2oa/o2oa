MWF.require("MWF.widget.UUID", null, false);
//MWF.xDesktop.requireApp("AppDesigner", "Actions.RestActions", null, false);
MWF.xApplication.AppDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "AppDesigner",
		"icon": "icon.png",
		"width": "1200",
		"height": "800",
		"title": MWF.xApplication.AppDesigner.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.AppDesigner.LP;
	},
	loadApplication: function(callback){
        if (!this.options.isRefresh){
            this.maxSize(function(){
                this.loadDesigner();
            }.bind(this));
        }else{
            this.loadDesigner();
        }
	},
    loadDesigner: function(){
        this.createNode();
        this.loadLayout();

        this.loadListToolBar();
        this.loadDesignerToolBar();
        this.loadDesignerTab();

        MWF.UD.getDataJson("appDesigner", function(components){
            this.components = components;
        }.bind(this));
    },
    loadLayout: function(){
        this.leftAreaNode = new Element("div", {"styles": this.css.leftAreaNode}).inject(this.node);
        this.rightAreaNode = new Element("div", {"styles": this.css.rightAreaNode}).inject(this.node);
        this.loadLeftAreaLayout();
        this.loadRightAreaLayout();
        this.setLayoutSize();
        this.addEvent("resize", this.setLayoutSize.bind(this));
        //this.windowDesignerNode = new Element("div", {"styles": this.css.designerAreaNode}).inject(this.designerAreaNode);
        //this.propertyDesignerNode = new Element("div", {"styles": this.css.designerAreaNode}).inject(this.designerAreaNode);
    },
    loadLeftAreaLayout: function(){
        this.leftAreaResizeNode = new Element("div", {"styles": this.css.leftAreaResizeNode}).inject(this.leftAreaNode);
        this.appListAreaNode = new Element("div", {"styles": this.css.appListAreaNode}).inject(this.leftAreaNode);
        this.appListToolbarNode = new Element("div", {"styles": this.css.appListToolbarNode}).inject(this.appListAreaNode);
        this.appListScrollNode = new Element("div", {"styles": this.css.appListScrollNode}).inject(this.appListAreaNode);
        this.appListContentNode = new Element("div", {"styles": this.css.appListContentNode}).inject(this.appListScrollNode);
    },
    loadRightAreaLayout: function(){
        this.designerToolbarNode = new Element("div", {"styles": this.css.designerToolbarNode}).inject(this.rightAreaNode);
        this.designerTabAreaNode = new Element("div", {"styles": this.css.designerTabAreaNode}).inject(this.rightAreaNode);
    },
    setLayoutSize: function(){
        var size = this.node.getSize();
        var toolbarSize = this.appListToolbarNode.getSize();
        var y = size.y-toolbarSize.y;
        this.appListScrollNode.setStyle("height", ""+y+"px");
        this.designerTabAreaNode.setStyle("height", ""+y+"px");
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);
    },

    loadListToolBar: function(callback){
        var toolbarUrl = this.path+"listToolbar.html";
        this.getToolbarHTML(toolbarUrl, function(toolbarNode){
            var spans = toolbarNode.getElements("span");
            spans.each(function(item, idx){
                var img = item.get("MWFButtonImage");
                if (img){
                    item.set("MWFButtonImage", this.path+""+this.options.style+"/icon/listToolbar/"+img);
                }
            }.bind(this));
            toolbarNode.inject(this.appListToolbarNode);

            MWF.require("MWF.widget.Toolbar", function(){
                this.listToolbar = new MWF.widget.Toolbar(toolbarNode, {"style": "designer"}, this);
                this.listToolbar.load();
                this.listToolbar.childrenButton[this.listToolbar.childrenButton.length-1].node.setStyle("float", "right");
                if (callback) callback();
            }.bind(this));
        }.bind(this));
    },
    loadDesignerToolBar: function(callback){
        var toolbarUrl = this.path+"designerToolbar.html";
        this.getToolbarHTML(toolbarUrl, function(toolbarNode){
            var spans = toolbarNode.getElements("span");
            spans.each(function(item, idx){
                var img = item.get("MWFButtonImage");
                if (img){
                    item.set("MWFButtonImage", this.path+""+this.options.style+"/icon/designerToolbar/"+img);
                }
            }.bind(this));
            toolbarNode.inject(this.designerToolbarNode);

            MWF.require("MWF.widget.Toolbar", function(){
                this.designerToolbar = new MWF.widget.Toolbar(toolbarNode, {"style": "designer"}, this);
                this.designerToolbar.load();
                if (callback) callback();
            }.bind(this));
        }.bind(this));
    },

    getToolbarHTML: function(toolbarUrl, callback){
        var r = new Request.HTML({
            url: toolbarUrl,
            method: "get",
            onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
                var toolbarNode = responseTree[0];
                if (callback) callback(toolbarNode);
            }.bind(this),
            onFailure: function(xhr){
                this.notice("request processToolbars error: "+xhr.responseText, "error");
            }.bind(this)
        });
        r.send();
    },
    loadDesignerTab: function(){
        MWF.require("MWF.widget.Tab", function(){
            this.designerTab = new MWF.widget.Tab(this.designerTabAreaNode, {"style": "script"});
            this.designerTab.load();
        }.bind(this), false);
    },

    createComponent: function(){
        MWF.require("MWF.xDesktop.Dialog", function(){
            var width = 600;
            var height = 230;
            var p = MWF.getCenterPosition(this.content, width, height);

            var _self = this;
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.lp.create,
                "style": "appDesigner",
                "top": p.y-100,
                "left": p.x,
                "fromTop": p.y-100,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "url": this.path+"create.html",
                "container": this.content,
                "isClose": true,
                "onPostShow": function(){
                    $("createComponent_okButton").addEvent("click", function(){
                        _self.doCreateComponent(this);
                    }.bind(this));
                    $("createComponent_cancelButton").addEvent("click", function(){
                        this.close();
                    }.bind(this));
                }
            });
            dlg.show();
        }.bind(this));
    },
    createComponentCheck: function(name, title, path, dlg){
        if (!name || !title || path){
            this.notice(this.lp.createComponent_input, "error", dlg.node);
            return false;
        }
        for (var i=0; i<this.components.length; i++){
            var component = this.components[i];
            if (component.name == name){
                this.notice(this.lp.createComponent_nameExist, "error", dlg.node);
                return false;
            }
            if (component.title == title){
                this.notice(this.lp.createComponent_titleExist, "error", dlg.node);
                return false;
            }
            if (component.path == path){
                this.notice(this.lp.createComponent_pathExist, "error", dlg.node);
                return false;
            }
        }
        return true;
    },
    doCreateComponent: function(dlg){
        var name = $("createComponent_name").get("value");
        var title = $("createComponent_title").get("value");
        var path = $("createComponent_path").get("value");

        var checked = this.createComponentCheck(name, title, path, dlg);
        if (checked){
            var data = {
                "name": name,
                "title": title,
                "path": path,
                "context": "x_component_"+path.replace(/\./g, "_"),
                "id": (new MWF.widget.UUID).toString()
            }
            this.components.push(data);
            MWF.UD.putData("appDesigner", this.components, function(){
                this.doCreateComponentModules(data);
            }.bind(this));
            dlg.close();
        }
    },
    doCreateComponentModules: function(data){
        //this.appListContentNode
        var mainData = this.getCreateComponentMainData(data);




        this.appListContentNode
    },
    getCreateComponentMainData: function(data){
        var mainData = null;
        var url = "/x_component_"+data.path.replace(/\./g, "_")+"/template/Main.json";
        MWF.getJSON(url, function(json){
            mainData = json;
        }, false);

        mainData.options.name = data.path;
        mainData.options.title = data.path;

        return mainData;
    }

});
