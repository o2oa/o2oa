MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.cms.Document = MWF.xApplication.cms.Document || {};
MWF.xDesktop.requireApp("cms.Document", "lp."+MWF.language, null, false);
MWF.xApplication.cms.Document.Processor = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	
	initialize: function(node, task, options){
		this.setOptions(options);
		
		this.path = "/x_component_cms_Document/$Processor/";
		this.cssPath = "/x_component_cms_Document/$Processor/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.task = task;
		this.node = $(node);
        this.selectedRoute = null;

        this.load();
	},
    load: function(){
        this.routeSelectorTile = new Element("div", {"styles": this.css.routeSelectorTile, "text": MWF.xApplication.cms.Document.LP.selectRoute}).inject(this.node);
        this.routeSelectorArea = new Element("div", {"styles": this.css.routeSelectorArea}).inject(this.node);
        this.setRouteList();

        this.routeOpinionTile = new Element("div", {"styles": this.css.routeOpinionTile, "text": MWF.xApplication.cms.Document.LP.inputOpinion}).inject(this.node);
        this.routeOpinionArea = new Element("div", {"styles": this.css.routeOpinionArea}).inject(this.node);
        this.setOpinion();

        this.buttonsArea = new Element("div", {"styles": this.css.buttonsArea}).inject(this.node);
        this.setButtons();
    },

    setRouteList: function(){
        var _self = this;
        //this.task.routeNameList = ["送审核", "送办理", "送公司领导阅"];
        this.task.routeNameList.each(function(route){
            var routeNode = new Element("div", {"styles": this.css.routeNode}).inject(this.routeSelectorArea);
            var routeIconNode = new Element("div", {"styles": this.css.routeIconNode}).inject(routeNode);
            var routeTextNode = new Element("div", {"styles": this.css.routeTextNode, "text": route}).inject(routeNode);

            routeNode.addEvents({
                "mouseover": function(e){_self.overRoute(this);},
                "mouseout": function(e){_self.outRoute(this);},
                "click": function(e){_self.selectRoute(this);}
            });

            if (this.task.routeNameList.length==1) this.selectRoute(routeNode);

        }.bind(this));
    },
    overRoute: function(node){
        if (this.selectedRoute){
            if (this.selectedRoute.get("text") != node.get("text")){
                node.setStyle("background-color", "#f7e1d0");
            }
        }else{
            node.setStyle("background-color", "#f7e1d0");
        }
    },
    outRoute: function(node){
        if (this.selectedRoute){
            if (this.selectedRoute.get("text") != node.get("text")){
                node.setStyle("background-color", "#E3E3E3");
            }
        }else{
            node.setStyle("background-color", "#E3E3E3");
        }
    },
    selectRoute: function(node){
        if (this.selectedRoute){
            if (this.selectedRoute.get("text") != node.get("text")){
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.getFirst().setStyles(this.css.routeIconNode);
                this.selectedRoute.getLast().setStyles(this.css.routeTextNode);

                this.selectedRoute = node;
                node.setStyle("background-color", "#da7429");
                node.getFirst().setStyle("background-image", "url("+"/x_component_cms_Document/$Processor/default/checked.png)");
                node.getLast().setStyle("color", "#FFF");
            }else{
                this.selectedRoute.setStyles(this.css.routeNode);
                this.selectedRoute.getFirst().setStyles(this.css.routeIconNode);
                this.selectedRoute.getLast().setStyles(this.css.routeTextNode);

                this.selectedRoute = null;
            }
        }else{
            this.selectedRoute = node;
            node.setStyle("background-color", "#da7429");
            node.getFirst().setStyle("background-image", "url("+"/x_component_cms_Document/$Processor/default/checked.png)");
            node.getLast().setStyle("color", "#FFF");
        }
        this.routeSelectorArea.setStyle("background-color", "#FFF");
    },

    setOpinion: function(){
        this.selectIdeaNode = new Element("div", {"styles": this.css.selectIdeaNode}).inject(this.routeOpinionArea);
        this.selectIdeaScrollNode = new Element("div", {"styles": this.css.selectIdeaScrollNode}).inject(this.selectIdeaNode);
        this.selectIdeaAreaNode = new Element("div", {"styles": {
            "overflow": "hidden"
        }}).inject(this.selectIdeaScrollNode);

        this.inputOpinionNode = new Element("div", {"styles": this.css.inputOpinionNode}).inject(this.routeOpinionArea);
        this.inputTextarea = new Element("textarea", {"styles": this.css.inputTextarea, "value": MWF.xApplication.cms.Document.LP.inputText}).inject(this.inputOpinionNode);
        this.inputTextarea.addEvents({
            "focus": function(){if (this.get("value")==MWF.xApplication.cms.Document.LP.inputText) this.set("value", "");},
            "blur": function(){if (!this.get("value")) this.set("value", MWF.xApplication.cms.Document.LP.inputText);}
        });


        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.selectIdeaScrollNode, {
                "style":"small", "where": "before", "distance": 30, "friction": 4, "indent": false,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        MWF.require("MWF.widget.UUID", function(){
            MWF.UD.getDataJson("idea", function(json){
                if (json){
                    if (json.ideas){
                        this.setIdeaList(json.ideas);
                    }
                }else{
                    MWF.UD.getPublicData("idea", function(pjson){
                        if (pjson.ideas){
                            this.setIdeaList(pjson.ideas);
                        }
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },
    setIdeaList: function(ideas){
        var _self = this;
        ideas.each(function(idea){
            new Element("div", {
                "styles": this.css.selectIdeaItemNode,
                "text": idea,
                "events": {
                    "dblclick": function(){
                        if (_self.inputTextarea.get("value")==MWF.xApplication.cms.Document.LP.inputText){
                            _self.inputTextarea.set("value", this.get("text"));
                        }else{
                            _self.inputTextarea.set("value", _self.inputTextarea.get("value")+", "+this.get("text"));
                        }
                    },
                    "mouseover": function(){this.setStyles(_self.css.selectIdeaItemNode_over);},
                    "mouseout": function(){this.setStyles(_self.css.selectIdeaItemNode);}
                }
            }).inject(this.selectIdeaAreaNode);
        }.bind(this));
    },
    setButtons: function(){
        this.cancelButton = new Element("div", {"styles": this.css.cancelButton}).inject(this.buttonsArea);
        var iconNode = new Element("div", {"styles": this.css.cancelIconNode}).inject(this.cancelButton);
        var textNode = new Element("div", {"styles": this.css.cancelTextNode, "text": MWF.xApplication.cms.Document.LP.cancel}).inject(this.cancelButton);

        this.okButton = new Element("div", {"styles": this.css.okButton}).inject(this.buttonsArea);
        var iconNode = new Element("div", {"styles": this.css.okIconNode}).inject(this.okButton);
        var textNode = new Element("div", {"styles": this.css.okTextNode, "text": MWF.xApplication.cms.Document.LP.ok}).inject(this.okButton);

        this.cancelButton.addEvent("click", function(){
            this.destroy();
            this.fireEvent("cancel");
        }.bind(this));

        this.okButton.addEvent("click", function(){
            if (!this.selectedRoute) {
                this.routeSelectorArea.setStyle("background-color", "#ffe9e9");
                new mBox.Notice({
                    type: "error",
                    position: {"x": "center", "y": "top"},
                    move: false,
                    target: this.routeSelectorArea,
                    delayClose: 6000,
                    content: MWF.xApplication.cms.Document.LP.mustSelectRoute
                });
                return false;
            }
            this.node.mask({
                "inject": {"where": "bottom", "target": this.node},
                "destroyOnHide": true,
                "style": {
                    "background-color": "#999",
                    "opacity": 0.3,
                    "z-index":600
                }
            });
            var routeName = this.selectedRoute.get("text");
            var opinion = this.inputTextarea.get("value");
            if (opinion == MWF.xApplication.cms.Document.LP.inputText) opinion = "";
            //this.destroy();
            this.fireEvent("submit", [routeName, opinion]);

        }.bind(this));
    },


    destroy: function(){
        this.node.empty();
        delete this.task;
        delete this.node;
        delete this.routeSelectorTile;
        delete this.routeSelectorArea;
        delete this.routeOpinionTile;
        delete this.routeOpinionArea;
        delete this.buttonsArea;
        delete this.inputOpinionNode;
        delete this.inputTextarea;
        delete this.cancelButton;
        delete this.okButton;
    }

});
