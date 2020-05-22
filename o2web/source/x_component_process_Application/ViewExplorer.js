MWF.require("MWF.widget.Mask", null, false);
MWF.xDesktop.requireApp("process.Application", "WorkExplorer", null, false);
MWF.xDesktop.requireApp("process.Application", "Viewer", null, false);
MWF.xApplication.process.Application.ViewExplorer = new Class({
    Extends: MWF.xApplication.process.Application.WorkExplorer,
    Implements: [Options, Events],

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "../x_component_process_Application/$WorkExplorer/";
        this.cssPath = "../x_component_process_Application/$WorkExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.items=[];
    },
    load: function(){
        this.loadFilterNode();
        this.loadContentNode();
    },
    loadFilterNode: function(){
        this.filterNode = new Element("div", {"styles": this.css.viewFilterNode}).inject(this.node);
        this.filterNode.setStyle("padding", "0px 10px");

        this.exportNode = new Element("div", {"styles": this.css.exportViewNode, "text": this.app.lp.exportExcel}).inject(this.filterNode);
        this.exportNode.addEvent("click", function(e){
            this.exportView();
        }.bind(this));

        this.viewListAreaNode = new Element("div", {"styles": this.css.viewFilterListAreaNode}).inject(this.filterNode);
        this.viewListTitleNode = new Element("div", {"styles": this.css.viewFilterListTitleNode, "text": this.app.lp.view}).inject(this.viewListAreaNode);
        this.viewListNode = new Element("div", {"styles": this.css.viewFilterListNode}).inject(this.viewListAreaNode);

        this.loadViewList();
    },
    exportView: function(){
        if (this.currentViewer){
            var filterData = this.currentViewer.getFilter();
            this.actions.exportView(this.currentViewer.json.application, this.currentViewer.json.id, {"filter": filterData});
        }
    },
    loadViewList: function(){
        this.actions.listView(this.app.options.id, function(json){
            if (json.data.length){
                json.data.each(function(process){
                    this.loadViewListNode(process);
                }.bind(this));
                if (this.currentViewNode){
                    this.currentViewNode.click();
                }else{
                    if (this.items[0]) this.items[0].click();
                }
            }else{
                this.filterNode.destroy();
                var noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": this.app.lp.noView
                }).inject(this.elementContentNode);
            }
        }.bind(this));
    },
    loadViewListNode: function(view){
        var viewNode = new Element("div", {"styles": this.css.filterViewNode}).inject(this.viewListNode);
        viewNode.set("text", view.name);
        viewNode.store("view", view);
        this.items.push(viewNode);

        if (this.app.status){
            if (this.app.status.viewName){
                if (view.name === this.app.status.viewName) this.currentViewNode = viewNode;
            }
        }

        var _self = this;
        viewNode.addEvent("click", function(){
            _self.loadViewData(this);
        });
        viewNode.makeLnk({
            "par": this._getLnkPar(view)
        });
    },
    loadViewData: function(node, viewObj){
        if (this.currentViewer) this.currentViewer.hide();
        if (node){
            this.items.each(function(item){
                item.setStyles(this.css.filterViewNode);
            }.bind(this));
            node.setStyles(this.css.filterViewNode_current);

            var viewer = node.retrieve("viewer");
            if (viewer){
                this.currentViewer = viewer;
                viewer.reload();
            }else{
                var view = (viewObj) ? viewObj : node.retrieve("view");
                viewer = new MWF.xApplication.process.Application.Viewer(this.elementContentNode, view);
                this.currentViewer = viewer;
            }
        }
    },

    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
    },

    createWorkListHead: function(){},
    setContentSize: function(){
        //var toolbarSize = this.toolbarNode.getSize();
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        var filterSize = this.filterNode.getSize();

        var height = nodeSize.y-pt-pb-filterSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");
        this.elementContentNode.fireEvent("resize");
    },
    createSearchElementNode: function(){
        return false;
    },

    _getLnkPar: function(view){
        return {
            "icon": this.path+this.options.style+"/viewIcon/lnk.png",
            "title": view.name,
            "par": "process.Application#{\"navi\": 2, \"id\": \""+this.app.options.id+"\", \"viewName\": \""+view.name+"\", \"hideMenu\": true}"
        };
    }
});