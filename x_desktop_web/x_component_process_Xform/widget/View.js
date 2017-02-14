MWF.xApplication.process.Xform.widget = MWF.xApplication.process.Xform.widget || {};
MWF.require("MWF.widget.Common", null, false);
MWF.xApplication.process.Xform.widget.View = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default"
    },
    initialize: function(container, json, options){
        this.setOptions(options);

        this.path = "/x_component_process_Xform/widget/$View/";
        this.cssPath = "/x_component_process_Xform/widget/$View/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);

        this.items = [];
        this.selectedItems = [];

        this.json = json;
        this.viewJson = null;
        this.gridJson = null;
        this.load();
    },
    load: function(){
        debugger;
        this.container.empty();
        this.container.setStyles(this.css.viewNode);

        this.createViewNode();
    },
    createViewNode: function(){
        this.viewTable = new Element("table", {
            "styles": this.css.viewTitleTableNode,
            "border": "0px",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.container);
        this.loadingAreaNode = new Element("div", {"styles": this.css.viewLoadingAreaNode}).inject(this.container);
        new Element("div", {"styles": {"height": "5px"}}).inject(this.loadingAreaNode);
        var loadingNode = new Element("div", {"styles": this.css.viewLoadingNode}).inject(this.loadingAreaNode);
        new Element("div", {"styles": this.css.viewLoadingIconNode}).inject(loadingNode);
        var loadingTextNode = new Element("div", {"styles": this.css.viewLoadingTextNode}).inject(loadingNode);
        loadingTextNode.set("text", "loading...");

        if (this.json.isTitle!="no"){
            this.viewTitleLine = new Element("tr", {"styles": this.css.viewTitleLineNode}).inject(this.viewTable);
            //if (this.json.select!="no"){
                this.selectTitleCell = new Element("td", {
                    "styles": this.css.viewTitleCellNode
                }).inject(this.viewTitleLine);
                this.selectTitleCell.setStyle("width", "10px");
                if (this.json.titleStyles) this.selectTitleCell.setStyles(this.json.titleStyles);
            //}
            this.getView(function(){
                this.viewJson.selectEntryList.each(function(column){
                    //if (column.export){
                    var viewCell = new Element("td", {
                        "styles": this.css.viewTitleCellNode,
                        "text": column.displayName
                    }).inject(this.viewTitleLine);
                    if (this.json.titleStyles) viewCell.setStyles(this.json.titleStyles);
                    //}
                }.bind(this));
                this.lookup();
            }.bind(this));
        }else{
            this.getView(function(){
                this.lookup();
            }.bind(this));
            //this.lookup();
        }
    },
    lookup: function(){
        this.getLookupAction(function(){
            if (this.json.application){
                this.lookupAction.invoke({"name": "lookup","async": true, "parameter": {"view": this.json.viewName, "application": this.json.application},"success": function(json){
                    if (this.viewJson.groupEntry.column){
                        this.gridJson = json.data.groupGrid;
                        this.loadGroupData();
                    }else{
                        this.gridJson = json.data.grid;
                        this.loadData();
                    }
                    if (this.loadingAreaNode){
                        this.loadingAreaNode.destroy();
                        this.loadingAreaNode = null;
                    }
                    //if (json.data.length){
                    //    json.data.each(function(line, idx){
                    //        this.items.push(new MWF.xApplication.process.Xform.widget.View.Item(this, line));
                    //    }.bind(this));
                    //}
                }.bind(this)});
            }else{
                //this.lookupAction.invoke({"name": "lookup","async": true, "parameter": {"id": this.json.view},"success": function(json){
                //    if (json.data.length){
                //        json.data.each(function(line, idx){
                //            this.items.push(new MWF.xApplication.process.Xform.widget.View.Item(this, line));
                //        }.bind(this));
                //    }
                //}.bind(this)});
            }
        }.bind(this));
    },
    loadData: function(){
        if (this.gridJson.length){
            this.gridJson.each(function(line, idx){
                this.items.push(new MWF.xApplication.process.Xform.widget.View.Item(this, line));
            }.bind(this));
        }
    },
    loadGroupData: function(){
        if (this.selectTitleCell){
            this.selectTitleCell.set("html", "<span style='font-family: Webdings'>"+"<img src='/x_component_process_Xform/widget/$View/"+this.options.style+"/icon/right.png'/>"+"</span>");
            this.selectTitleCell.setStyle("cursor", "pointer");
            this.selectTitleCell.addEvent("click", this.expandOrCollapseAll.bind(this));
        }

        if (this.gridJson.length){
            this.gridJson.each(function(data, idx){
                this.items.push(new MWF.xApplication.process.Xform.widget.View.ItemCategory(this, data));
            }.bind(this));
        }
    },
    expandOrCollapseAll: function(){
        var icon = this.selectTitleCell.getElement("span");
        if (icon.get("html").indexOf("right.png")==-1){
            this.items.each(function(item){
                item.collapse();
                icon.set("html", "<img src='/x_component_process_Xform/widget/$View/"+this.options.style+"/icon/right.png'/>");
            }.bind(this));
        }else{
            this.items.each(function(item, i){
                window.setTimeout(function(){
                    item.expand();
                }.bind(this), 10*i+5);

                icon.set("html", "<img src='/x_component_process_Xform/widget/$View/"+this.options.style+"/icon/down.png'/>");
            }.bind(this));
        }
    },
    getView: function(callback){
        this.getLookupAction(function(){
            if (this.json.application){
                this.lookupAction.invoke({"name": "getView","async": true, "parameter": {"view": this.json.viewName, "application": this.json.application},"success": function(json){
                    this.viewJson = JSON.decode(json.data.data);
                    //var viewData = JSON.decode(json.data.data);
                    if (callback) callback();
                }.bind(this)});
            }else{
                //this.lookupAction.invoke({"name": "getView","async": true, "parameter": {"id": this.json.view},"success": function(json){
                //    var viewData = JSON.decode(json.data.data);
                //    viewData.columnList.each(function(column){
                //        if (column.export){
                //            var viewCell = new Element("td", {
                //                "styles": this.css.viewTitleCellNode,
                //                "text": column.text
                //            }).inject(this.viewTitleLine);
                //            if (this.json.titleStyles) viewCell.setStyles(this.json.titleStyles);
                //        }
                //    }.bind(this));
                //
                //    if (callback) callback();
                //}.bind(this)});
            }
        }.bind(this));
    },
    getLookupAction: function(callback){
        if (!this.lookupAction){
            MWF.require("MWF.xDesktop.Actions.RestActions", function(){
                this.lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
                this.lookupAction.getActions = function(actionCallback){
                    this.actions = {
                        //"lookup": {"uri": "/jaxrs/view/{id}"},
                        //"lookupName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}"},
                        //"getView": {"uri": "/jaxrs/view/{id}/design"},
                        //"getViewName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}/design"}
                        //"lookup": {"uri": "/jaxrs/view/{id}"},
                        "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                        "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                        //"getViewName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}/design"}
                    };
                    if (actionCallback) actionCallback();
                }
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    getData: function(){
        if (this.selectedItems.length){
            var arr = [];
            this.selectedItems.each(function(item){
                arr.push(item.data);
            });
            return arr;
        }else{
            return [];
        }
    }
});

MWF.xApplication.process.Xform.widget.View.Item = new Class({
    initialize: function(view, data, prev){
        this.view = view;
        this.data = data;
        this.css = this.view.css;
        this.isSelected = false;
        this.prev = prev;
        this.load();
    },
    load: function(){
        this.node = new Element("tr", {"styles": this.css.viewContentTrNode});
        if (this.prev){
            this.node.inject(this.prev.node, "after");
        }else{
            this.node.inject(this.view.viewTable);
        }
        //if (this.view.json.select!="no"){
            this.selectTd = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
            //if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);

            this.selectTd.setStyles({"cursor": "pointer"});
            if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
        //}
        Object.each(this.data.data, function(cell, k){
            var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
            if (k!= this.view.viewJson.groupEntry.column) td.set("text", cell);
            if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
        }.bind(this));
        //this.data.each(function(cell, i){
        //    var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
        //    td.set("text", cell);
        //    //if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
        //}.bind(this));

        this.setEvent();
    },
    setEvent: function(){
        if (this.view.json.select!="no"){
            this.node.addEvents({
                "mouseover": function(){
                    if (!this.isSelected){
                        var iconName = "checkbox";
                        if (this.view.json.select=="single") iconName = "radiobox";
                        this.selectTd.setStyles({"background": "url("+"/x_component_process_Xform/$Form/default/icon/"+iconName+".png) center center no-repeat"});
                    }
                }.bind(this),
                "mouseout": function(){
                    if (!this.isSelected) this.selectTd.setStyles({"background": "transparent"});
                }.bind(this),
                "click": function(){this.select();}.bind(this)
            });
        }
    },

    select: function(){
        if (this.isSelected){
            if (this.view.json.select=="single"){
                this.unSelectedSingle();
            }else{
                this.unSelected();
            }
        }else{
            if (this.view.json.select=="single"){
                this.selectedSingle();
            }else{
                this.selected();
            }
        }
        this.view.fireEvent("select");
    },

    selected: function(){
        this.view.selectedItems.push(this);
        this.selectTd.setStyles({"background": "url("+"/x_component_process_Xform/$Form/default/icon/checkbox_checked.png) center center no-repeat"});
        this.node.setStyles(this.css.viewContentTrNode_selected);
        this.isSelected = true;
    },
    unSelected: function(){
        this.view.selectedItems.erase(this);
        this.selectTd.setStyles({"background": "transparent"});
        this.node.setStyles(this.css.viewContentTrNode);
        this.isSelected = false;
    },
    selectedSingle: function(){
        if (this.view.currentSelectedItem) this.view.currentSelectedItem.unSelectedSingle();
        this.view.selectedItems = [this];
        this.view.currentSelectedItem = this;
        this.selectTd.setStyles({"background": "url("+"/x_component_process_Xform/$Form/default/icon/radiobox_checked.png) center center no-repeat"});
        this.node.setStyles(this.css.viewContentTrNode_selected);
        this.isSelected = true;
    },
    unSelectedSingle: function(){
        this.view.selectedItems = [];
        this.view.currentSelectedItem = null;
        this.selectTd.setStyles({"background": "transparent"});
        this.node.setStyles(this.css.viewContentTrNode);
        this.isSelected = false;
    }
});

MWF.xApplication.process.Xform.widget.View.ItemCategory = new Class({
    initialize: function(view, data){
        this.view = view;
        this.data = data;
        this.css = this.view.css;
        this.items = [];
        this.loadChild = false;
        this.load();
    },
    load: function(){
        this.node = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.view.viewTable);
        //if (this.view.json.select!="no"){
            this.selectTd = new Element("td", {"styles": this.css.viewContentCategoryTdNode}).inject(this.node);
            if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
        //}
        this.categoryTd = new Element("td", {
            "styles": this.css.viewContentCategoryTdNode,
            "colspan": this.view.viewJson.selectEntryList.length
        }).inject(this.node);
        //this.categoryIconNode = new Element("div", {"styles": this.css.viewContentCategoryIconNode}).inject(this.categoryTd);
        this.categoryTd.set("html", "<span style='font-family: Webdings'><img src='/x_component_process_Xform/widget/$View/"+this.view.options.style+"/icon/right.png'/></span> "+this.data.group);
        if (this.view.json.itemStyles) this.categoryTd.setStyles(this.view.json.itemStyles);

        //this.data.list.each(function(line){
        //    this.items.push(new MWF.xApplication.process.Xform.widget.View.Item(this.view, line));
        //}.bind(this));

        this.setEvent();
    },
    setEvent: function(){
        if (this.selectTd){
            this.node.addEvents({
                //"mouseover": function(){
                //    if (!this.isSelected){
                //        var iconName = "checkbox";
                //        if (this.view.json.select=="single") iconName = "radiobox";
                //        this.selectTd.setStyles({"background": "url("+"/x_component_process_Xform/$Form/default/icon/"+iconName+".png) center center no-repeat"});
                //    }
                //}.bind(this),
                //"mouseout": function(){
                //    if (!this.isSelected) this.selectTd.setStyles({"background": "transparent"});
                //}.bind(this),

                "click": function(){this.expandOrCollapse();}.bind(this)
            });

        }
    },
    expandOrCollapse: function(){
        var t = this.node.getElement("span").get("html");
        if (t.indexOf("right.png")==-1){
            this.collapse();
        }else{
            this.expand();
        }
    },
    collapse: function(){
        this.items.each(function(item){
            item.node.setStyle("display", "none");
        }.bind(this));
        this.node.getElement("span").set("html", "<img src='/x_component_process_Xform/widget/$View/"+this.view.options.style+"/icon/right.png'/>");
    },
    expand: function(){
        this.items.each(function(item){
            item.node.setStyle("display", "table-row");
        }.bind(this));
        this.node.getElement("span").set("html", "<img src='/x_component_process_Xform/widget/$View/"+this.view.options.style+"/icon/down.png'/>");
        if (!this.loadChild){
            //window.setTimeout(function(){
                this.data.list.each(function(line){
                    this.items.push(new MWF.xApplication.process.Xform.widget.View.Item(this.view, line, this));
                }.bind(this));
                this.loadChild = true;
            //}.bind(this), 10);
        }
    }
});