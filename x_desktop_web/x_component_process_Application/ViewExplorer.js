MWF.require("MWF.widget.Mask", null, false);
MWF.xDesktop.requireApp("process.Application", "WorkExplorer", null, false);
MWF.xApplication.process.Application.ViewExplorer = new Class({
	Extends: MWF.xApplication.process.Application.WorkExplorer,
	Implements: [Options, Events],

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "/x_component_process_Application/$WorkExplorer/";
        this.cssPath = "/x_component_process_Application/$WorkExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.items=[];
    },
    load: function(){
        if (!this.actions){
            MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
                this.actions = new MWF.xApplication.process.Application.Actions.RestActions();
                this.loadToolbar();
                this.loadFilterNode();
                //this.loadFilterConditionNode();
                this.loadContentNode();
                this.setNodeScroll();
            }.bind(this));
        }else{
            this.loadToolbar();
            this.loadFilterNode();
            //this.loadFilterConditionNode();
            this.loadContentNode();
            this.setNodeScroll();
        }

        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.node);

        //this.loadElementList();
    },
    createWorkListHead: function(){},
    setContentSize: function(){
        var toolbarSize = this.toolbarNode.getSize();
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        var filterSize = this.filterNode.getSize();

        var height = nodeSize.y-toolbarSize.y-pt-pb-filterSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/40).toInt()+5;
    },
    createSearchElementNode: function(){
        return false;
    },

    loadFilterNode: function(){
        this.filterNode = new Element("div", {"styles": this.css.filterNode}).inject(this.node);
        this.filterNode.setStyle("padding", "0px 10px");
        //this.filterProcessAreaNode = new Element("div", {"styles": this.css.filterProcessAreaNode}).inject(this.filterNode);
        //this.filterProcessListNode = new Element("div", {"styles": this.css.filterProcessListNode}).inject(this.filterProcessAreaNode);

        this.loadViewList();
    },
    loadViewList: function(){
        this.actions.listView(this.app.options.id, function(json){
            if (json.data.length){
                json.data.each(function(process){
                    this.loadViewListNode(process);
                }.bind(this));
                if (this.mask){
                    this.mask.hide();
                    this.mask = null;
                }
                if (this.currentView){
                    this.currentView.click();
                }else{
                    this.items[0].click();
                }
            }else{
                this.filterNode.destroy();
                var noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": this.app.lp.noView
                }).inject(this.elementContentListNode);
                if (this.mask){
                    this.mask.hide();
                    this.mask = null;
                }
            }
        }.bind(this));
    },
    loadViewListNode: function(view){
        var viewNode = new Element("div", {"styles": this.css.filterViewNode}).inject(this.filterNode);
        viewNode.set("text", view.name);
        viewNode.store("view", view);
        this.items.push(viewNode);

        if (this.app.status.viewName){
            if (view.name == this.app.status.viewName) this.currentView = viewNode;
        }

        var _self = this;
        viewNode.addEvent("click", function(){
            _self.loadViewData(this);
        });
        viewNode.makeLnk({
            "par": this._getLnkPar(view)
        });
    },
    _getLnkPar: function(view){
        return {
            "icon": this.path+this.options.style+"/viewIcon/lnk.png",
            "title": view.name,
            "par": "process.Application#{\"navi\": 2, \"id\": \""+this.app.options.id+"\", \"viewName\": \""+view.name+"\", \"hideMenu\": true}"
        };
    },
    loadViewData: function(node){
        if (!this.mask){
            this.mask = new MWF.widget.Mask({"style": "desktop"});
            this.mask.loadNode(this.node);
        }

        this.items.each(function(item){
            item.setStyles(this.css.filterViewNode);
        }.bind(this));
        node.setStyles(this.css.filterViewNode_current);

        var view = node.retrieve("view");
        this.actions.loadView(function(json){
            this.showViewData(json.data);

            if (this.mask){
                this.mask.hide();
                this.mask = null;
            }
        }.bind(this), null, view.id, this.app.options.id);
    },
    showViewData: function(data){
        this.elementContentListNode.empty();
        this.viewTable = null;
        this.loadViewDataTitle(data.selectEntryList);
        this.loadViewDataLine(data);
    },

    loadViewDataTitle: function(titleList){
        this.viewTable = new Element("table", {
            "styles": this.css.viewTableNode,
            "width": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.elementContentListNode);
        this.viewHeadTr = new Element("tr").inject(this.viewTable);
        titleList.each(function(title){
            var th = new Element("th", {
                "styles": this.css.viewHeadTh,
                "text": title.displayName
            }).inject(this.viewHeadTr);
        }.bind(this));
    },

    loadViewDataLine: function(data){
        if ((data.groupEntry) && (data.groupEntry.column)){
            if (data.groupGrid.length){
                data.groupGrid.each(function(line, idx){
                    var groupTr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewTable);
                    var colSpan = data.selectEntryList.length;
                    var td = new Element("td", {"styles": this.css.viewContentGroupTdNode, "colSpan": colSpan}).inject(groupTr);
                    var groupAreaNode = new Element("div", {"styles": this.css.viewContentTdGroupNode}).inject(td);
                    var groupIconNode = new Element("div", {"styles": this.css.viewContentTdGroupIconNode}).inject(groupAreaNode);
                    var groupTextNode = new Element("div", {"styles": this.css.viewContentTdGroupTextNode}).inject(groupAreaNode);
                    groupTextNode.set("text", line.group);

                    var subtrs = [];
                    line.list.each(function(entry){
                        var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewTable);
                        tr.setStyle("display", "none")
                        var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                        Object.each(entry.data, function(d, k){
                            if (k!=data.groupEntry.column){
                                var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                td.set("text", d);
                            }
                        }.bind(this));
                        subtrs.push(tr)
                    }.bind(this));

                    groupAreaNode.store("subtrs", subtrs);

                    var _self = this;
                    groupAreaNode.addEvent("click", function(){
                        var subtrs = this.retrieve("subtrs");
                        var iconNode = groupAreaNode.getFirst("div");
                        if (subtrs[0]){
                            if (subtrs[0].getStyle("display")=="none"){
                                subtrs.each(function(subtr){ subtr.setStyle("display", "table-row"); });
                                iconNode.setStyle("background", "url("+"/x_component_process_ViewDesigner/$View/default/icon/down.png) center center no-repeat");
                            }else{
                                subtrs.each(function(subtr){ subtr.setStyle("display", "none"); });
                                iconNode.setStyle("background", "url("+"/x_component_process_ViewDesigner/$View/default/icon/right.png) center center no-repeat");
                            }
                        }
                        _self.setContentHeight();
                    });
                }.bind(this));
            }

        }else{
            if (data.grid.length){
                data.grid.each(function(line, idx){
                    var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewTable);
                    Object.each(line.data, function(d, k){
                        var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                        td.set("text", d);
                    }.bind(this));
                }.bind(this));
            }
        }

    }
});