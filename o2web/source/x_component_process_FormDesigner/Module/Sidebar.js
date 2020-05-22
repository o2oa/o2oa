MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.require("MWF.widget.Toolbar", null, false);
MWF.xApplication.process.FormDesigner.Module.Sidebar = MWF.FCSidebar = new Class({
    Extends: MWF.FC$Element,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "propertyPath": "../x_component_process_FormDesigner/Module/Sidebar/sidebar.html"
    },
    addAction: function(){

    },

    initialize: function(form, options){
        this.setOptions(options);

        this.path = "../x_component_process_FormDesigner/Module/Sidebar/";
        this.cssPath = "../x_component_process_FormDesigner/Module/Sidebar/"+this.options.style+"/css.wcss";

        this._loadCss();
        this.moduleType = "component";
        this.moduleName = "sidebar";

        this.Node = null;
        this.form = form;
        this.container = null;
        this.containerNode = null;
        this.systemTools = [];
        //this.containers = [];
        //this.elements = [];
    },
    setTemplateStyles: function(styles){
        this.json.style = styles.style;
    },
    clearTemplateStyles: function(styles){
        this.json.style = "xform_side_blue_simple";
    },
    setAllStyles: function(){
        this._refreshActionbar();
    },
    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "MWFType": "sidebar",
            "id": this.json.id,
            "styles": this.css.moduleNodeMove,
            "events": {
                "selectstart": function(e){
                    e.preventDefault();
                }
            }
        }).inject(this.form.container);
    },
    _createNode: function(callback){
        this.node = new Element("div", {
            "id": this.json.id,
            "MWFType": "actionbar",
            "styles": this.css.moduleNode,
            "events": {
                "selectstart": function(e){
                    e.preventDefault();
                }
            }

        }).inject(this.form.node);
        if (this.form.options.mode == "Mobile"){
            this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
            this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        }else{
            this.toolbarNode = new Element("div").inject(this.node);

            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);

            MWF.getJSON(this.path+"toolbars.json", function(json){
                this.json.defaultTools = json;
                this.setToolbars(json, this.toolbarNode);
                this.toolbarWidget.load();
            }.bind(this), false);
        }
    },
    _initModule: function(){
        this.setStyleTemplate();
        this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction();
        this._setNodeEvent();
        this._refreshActionbar();
        this.loadPosition();

        this.form.designer.designNode.addEvent("scroll", function(){

        }.bind(this));
    },

    position: function(){
        if (this.parentContainer){
            this.sideNode = this.parentContainer.node;
            //var p = this.sideNode.getStyles(["top", "left", "bottom", "right", "width", "height"]);
            var p = this.sideNode.getPosition();
            var s = this.sideNode.getSize();
            if (!this.sidePosition || !(this.sidePosition.p.x==p.x && this.sidePosition.p.y==p.y && this.sidePosition.s.x==s.x && this.sidePosition.s.y==s.y)){
                this.loadPosition();
            }
        }
    },

    loadPosition: function(){
        this.sideNode = this.parentContainer.node;
        var size = this.form.designer.designNode.getSize();
        var scroll = this.form.designer.designNode.getScroll();

        var sideSize = this.sideNode.getSize();
        var sidePosition = this.sideNode.getPosition(this.sideNode.getOffsetParent());
        var nodeSize = this.node.getSize();

        if (sideSize.y>size.y){
            var center = (size.y/2-nodeSize.y/2);
            if (center<sidePosition.y){
                this.node.setStyle("top", ""+sidePosition.y+"px");
            }else if (center>(sidePosition.y+sideSize.y)){
                var tmp = (sidePosition.y+sideSize.y)-nodeSize.y;
                this.node.setStyle("top", ""+tmp+"px");
            }else{
                this.node.setStyle("top", ""+center+"px");
            }
        }else{
            var top = sidePosition.y+sideSize.y/2-nodeSize.y/2;
            if (top>size.y){
                if (sidePosition.y+nodeSize.y>size.y){
                    this.node.setStyle("top", ""+sidePosition.y+"px");
                }else{
                    var tmp = size.y-nodeSize.y;
                    this.node.setStyle("top", ""+tmp+"px");
                }
            }else if(top<=45){
                if(sidePosition.y+sideSize.y<nodeSize.y+45){
                    var tmp = sidePosition.y+sideSize.y-nodeSize.y;
                    this.node.setStyle("top", ""+tmp+"px");
                }else{
                    this.node.setStyle("top", "45px");
                }
            }else{
                this.node.setStyle("top", ""+top+"px");
            }
        }


        var left = sideSize.x+sidePosition.x+5;
        this.node.setStyle("left", ""+left+"px");
        this.node.setStyle("position", "absolute");

        // var y=0, x=5;
        // if (sideSize.y>size.y) y = (scroll.y+size.y/2)-sideSize.y/2;
        //
        // var position = "centerRight";
        // var edge = "centerLeft";
        // if (this.parentContainer.moduleType==="form"){
        //     edge = "centerRight";
        //     x = -5;
        // }
        //
        // if (this.json.barPosition=="left"){
        //     position = "centerLeft";
        //     edge = "centerRight";
        //     x = -5;
        //     if (this.parentContainer.moduleType==="form"){
        //         edge = "centerLeft";
        //         x = 5;
        //     }
        // }
        //
        // this.node.position({
        //     "relativeTo": this.sideNode,
        //     "position": position,
        //     "edge": edge,
        //     "offset" : {"y": y}
        // });
        // this.json.styles.top = top;
        // if (top) this.node.setStyle("top", top);

        this.json.styles = this.node.getStyles(["top", "left", "bottom", "right", "position"]);
        this.json.styles.bottom = "auto";
        this.json.styles.right = "auto";

        var p = this.sideNode.getPosition();
        var s = this.sideNode.getSize();
        this.sidePosition = {"p": p, "s": s};
        this.node.setStyles({"right": "auto", "bottom": "auto"});
        //
        // var p = this.node.getPosition(offsetParentNode);
        // var s = this.node.getSize();
        // var pSize = offsetParentNode.getSize();
        // var right = pSize.x-p.x-s.x;
        // this.node.setStyles({"right": right, "left": "auto"});
        //
        //

    },

    _refreshActionbar: function(){
        if (this.form.options.mode == "Mobile"){
            this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
            this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        }else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();
            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);

            if (this.json.defaultTools){
                var json = Array.clone(this.json.defaultTools);
                if (this.json.tools) json.append(this.json.tools);
                this.setToolbars(json, this.toolbarNode);
                this.toolbarWidget.load();
                //json = null;
            }else{
                MWF.getJSON(this.path+"toolbars.json", function(json){
                    this.json.defaultTools = json;
                    var json = Array.clone(this.json.defaultTools);
                    if (this.json.tools) json.append(this.json.tools);
                    this.setToolbars(json, this.toolbarNode);
                    this.toolbarWidget.load();
                    //json = null;
                }.bind(this), false);
            }
            //   if (this.json.sysTools.editTools){
            //       this.setToolbars(this.json.sysTools.editTools, this.toolbarNode);
            ////       this.setToolbars(this.json.tools.editTools, this.toolbarNode);
            //   }else{
            //       this.setToolbars(this.json.sysTools, this.toolbarNode);
            ////       this.setToolbars(this.json.tools, this.toolbarNode);
            //   }

        }

    },
    setToolbars: function(tools, node){
        tools.each(function(tool){
            var actionNode = new Element("div", {
                "MWFnodetype": tool.type,
                "MWFButtonImage": this.path+""+this.options.style+"/tools/"+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            this.systemTools.push(actionNode);
            if (tool.sub){
                var subNode = node.getLast();
                this.setToolbars(tool.sub, subNode);
            }
        }.bind(this));
    },
    _setEditStyle_custom: function(name){
        if (name=="hideSystemTools"){
            if (this.json.hideSystemTools){
                this.systemTools.each(function(tool){
                    tool.setStyle("display", "none");
                });
            }else{
                this.systemTools.each(function(tool){
                    tool.setStyle("display", "block");
                });
            }
        }
        // if (name=="barPosition"){
        //     this.loadPosition();
        // }
        if (name=="barPosition" || name=="styles"){
            // var top = this.json.styles.top;
            this.loadPosition();
            // this.json.styles.top = top;
            // if (top) this.node.setStyle("top", top);
        }
        if (name=="defaultTools" || name=="tools"){
            this._refreshActionbar();
        }

    }

});
