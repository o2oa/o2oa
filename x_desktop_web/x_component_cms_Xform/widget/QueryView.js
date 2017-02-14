MWF.xApplication.cms.Xform.widget = MWF.xApplication.cms.Xform.widget || {};
MWF.require("MWF.widget.Common", null, false);
MWF.xApplication.cms.Xform.widget.QueryView = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default"
    },
    initialize: function(container, json, options){
        this.setOptions(options);

        this.path = "/x_component_cms_Xform/widget/$QueryView/";
        this.cssPath = "/x_component_cms_Xform/widget/$QueryView/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);

        this.items = [];
        this.selectedItems = [];

        this.json = json;
        this.queryviewJson = null;
        this.gridJson = null;
        this.load();
    },
    load: function(){
        this.container.empty();
        this.container.setStyles(this.css.viewNode);

        this.createQueryViewNode();
    },
    createQueryViewNode: function(){
        this.queryviewTable = new Element("table", {
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
            this.queryviewTitleLine = new Element("tr", {"styles": this.css.viewTitleLineNode}).inject(this.queryviewTable);
            //if (this.json.select!="no"){
                this.selectTitleCell = new Element("td", {
                    "styles": this.css.viewTitleCellNode
                }).inject(this.queryviewTitleLine);
                this.selectTitleCell.setStyle("width", "10px");
                if (this.json.titleStyles) this.selectTitleCell.setStyles(this.json.titleStyles);
            //}
            this.getQueryView(function(){
                this.queryviewJson.selectEntryList.each(function(column){
                    //if (column.export){
                    var queryviewCell = new Element("td", {
                        "styles": this.css.viewTitleCellNode,
                        "text": column.displayName
                    }).inject(this.queryviewTitleLine);
                    if (this.json.titleStyles) queryviewCell.setStyles(this.json.titleStyles);
                    //}
                }.bind(this));
                this.lookup();
            }.bind(this));
        }else{
            this.getQueryView(function(){
                this.lookup();
            }.bind(this));
            //this.lookup();
        }
    },
    lookup: function(){
        this.getLookupAction(function(){
            if (this.json.application){
                this.lookupAction.invoke({"name": "lookup","async": true, "parameter": {"queryview": this.json.queryviewName, "application": this.json.application},"success": function(json){
                    if (this.queryviewJson.groupEntry.column){
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
                    //        this.items.push(new MWF.xApplication.cms.Xform.widget.QueryView.Item(this, line));
                    //    }.bind(this));
                    //}
                }.bind(this)});
            }else{
                //this.lookupAction.invoke({"name": "lookup","async": true, "parameter": {"id": this.json.queryview},"success": function(json){
                //    if (json.data.length){
                //        json.data.each(function(line, idx){
                //            this.items.push(new MWF.xApplication.cms.Xform.widget.QueryView.Item(this, line));
                //        }.bind(this));
                //    }
                //}.bind(this)});
            }
        }.bind(this));
    },
    loadData: function(){
        if (this.gridJson.length){
            this.gridJson.each(function(line, idx){
                this.items.push(new MWF.xApplication.cms.Xform.widget.QueryView.Item(this, line));
            }.bind(this));
        }
    },
    loadGroupData: function(){
        if (this.selectTitleCell){
            this.selectTitleCell.set("html", "<span style='font-family: Webdings'>"+"<img src='/x_component_cms_Xform/widget/$QueryView/"+this.options.style+"/icon/right.png'/>"+"</span>");
            this.selectTitleCell.setStyle("cursor", "pointer");
            this.selectTitleCell.addEvent("click", this.expandOrCollapseAll.bind(this));
        }

        if (this.gridJson.length){
            this.gridJson.each(function(data, idx){
                this.items.push(new MWF.xApplication.cms.Xform.widget.QueryView.ItemCategory(this, data));
            }.bind(this));
        }
    },
    expandOrCollapseAll: function(){
        var icon = this.selectTitleCell.getElement("span");
        if (icon.get("html").indexOf("right.png")==-1){
            this.items.each(function(item){
                item.collapse();
                icon.set("html", "<img src='/x_component_cms_Xform/widget/$QueryView/"+this.options.style+"/icon/right.png'/>");
            }.bind(this));
        }else{
            this.items.each(function(item, i){
                window.setTimeout(function(){
                    item.expand();
                }.bind(this), 10*i+5);

                icon.set("html", "<img src='/x_component_cms_Xform/widget/$QueryView/"+this.options.style+"/icon/down.png'/>");
            }.bind(this));
        }
    },
    getQueryView: function(callback){
        this.getLookupAction(function(){
            if (this.json.application){
                this.lookupAction.invoke({"name": "getQueryView","async": true, "parameter": {"queryview": this.json.queryviewName, "application": this.json.application},"success": function(json){
                    this.queryviewJson = JSON.decode(json.data.data);
                    //var queryviewData = JSON.decode(json.data.data);
                    if (callback) callback();
                }.bind(this)});
            }else{
                //this.lookupAction.invoke({"name": "getQueryView","async": true, "parameter": {"id": this.json.queryview},"success": function(json){
                //    var queryviewData = JSON.decode(json.data.data);
                //    queryviewData.columnList.each(function(column){
                //        if (column.export){
                //            var queryviewCell = new Element("td", {
                //                "styles": this.css.viewTitleCellNode,
                //                "text": column.text
                //            }).inject(this.queryviewTitleLine);
                //            if (this.json.titleStyles) queryviewCell.setStyles(this.json.titleStyles);
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
                this.lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
                this.lookupAction.getActions = function(actionCallback){
                    this.actions = {
                        //"lookup": {"uri": "/jaxrs/view/{id}"},
                        //"lookupName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}"},
                        //"getView": {"uri": "/jaxrs/view/{id}/design"},
                        //"getViewName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}/design"}
                        //"lookup": {"uri": "/jaxrs/view/{id}"},
                        //"lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                        //"getQueryView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                        "lookup": {"uri": "/jaxrs/queryview/flag/{queryview}/application/flag/{application}/execute", "method":"PUT"},
                        "getQueryView": {"uri": "/jaxrs/queryview/flag/{queryview}/application/flag/{application}"}
                        //"getQueryViewName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}/design"}
                    };
                    if (actionCallback) actionCallback();
                };
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

MWF.xApplication.cms.Xform.widget.QueryView.Item = new Class({
    initialize: function(queryview, data, prev){
        this.queryview = queryview;
        this.data = data;
        this.css = this.queryview.css;
        this.isSelected = false;
        this.prev = prev;
        this.load();
    },
    load: function(){
        this.node = new Element("tr", {"styles": this.css.viewContentTrNode});
        if (this.prev){
            this.node.inject(this.prev.node, "after");
        }else{
            this.node.inject(this.queryview.queryviewTable);
        }
        //if (this.queryview.json.select!="no"){
            this.selectTd = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
            //if (this.queryview.json.itemStyles) this.selectTd.setStyles(this.queryview.json.itemStyles);

            this.selectTd.setStyles({"cursor": "pointer"});
            if (this.queryview.json.itemStyles) this.selectTd.setStyles(this.queryview.json.itemStyles);
        //}
        Object.each(this.data.data, function(cell, k){
            var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
            if (k!= this.queryview.queryviewJson.groupEntry.column) td.set("text", cell);
            if (this.queryview.json.itemStyles) td.setStyles(this.queryview.json.itemStyles);
        }.bind(this));
        //this.data.each(function(cell, i){
        //    var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
        //    td.set("text", cell);
        //    //if (this.queryview.json.itemStyles) td.setStyles(this.queryview.json.itemStyles);
        //}.bind(this));

        this.setEvent();
    },
    setEvent: function(){
        if (this.queryview.json.select!="no"){
            this.node.addEvents({
                "mouseover": function(){
                    if (!this.isSelected){
                        var iconName = "checkbox";
                        if (this.queryview.json.select=="single") iconName = "radiobox";
                        this.selectTd.setStyles({"background": "url("+"/x_component_cms_Xform/$Form/default/icon/"+iconName+".png) center center no-repeat"});
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
            if (this.queryview.json.select=="single"){
                this.unSelectedSingle();
            }else{
                this.unSelected();
            }
        }else{
            if (this.queryview.json.select=="single"){
                this.selectedSingle();
            }else{
                this.selected();
            }
        }
    },

    selected: function(){
        this.queryview.selectedItems.push(this);
        this.selectTd.setStyles({"background": "url("+"/x_component_cms_Xform/$Form/default/icon/checkbox_checked.png) center center no-repeat"});
        this.node.setStyles(this.css.viewContentTrNode_selected);
        this.isSelected = true;
    },
    unSelected: function(){
        this.queryview.selectedItems.erase(this);
        this.selectTd.setStyles({"background": "transparent"});
        this.node.setStyles(this.css.viewContentTrNode);
        this.isSelected = false;
    },
    selectedSingle: function(){
        if (this.queryview.currentSelectedItem) this.queryview.currentSelectedItem.unSelectedSingle();
        this.queryview.selectedItems = [this];
        this.queryview.currentSelectedItem = this;
        this.selectTd.setStyles({"background": "url("+"/x_component_cms_Xform/$Form/default/icon/radiobox_checked.png) center center no-repeat"});
        this.node.setStyles(this.css.viewContentTrNode_selected);
        this.isSelected = true;
    },
    unSelectedSingle: function(){
        this.queryview.selectedItems = [];
        this.queryview.currentSelectedItem = null;
        this.selectTd.setStyles({"background": "transparent"});
        this.node.setStyles(this.css.viewContentTrNode);
        this.isSelected = false;
    }
});

MWF.xApplication.cms.Xform.widget.QueryView.ItemCategory = new Class({
    initialize: function(queryview, data){
        this.queryview = queryview;
        this.data = data;
        this.css = this.queryview.css;
        this.items = [];
        this.loadChild = false;
        this.load();
    },
    load: function(){
        this.node = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.queryview.queryviewTable);
        //if (this.queryview.json.select!="no"){
            this.selectTd = new Element("td", {"styles": this.css.viewContentCategoryTdNode}).inject(this.node);
            if (this.queryview.json.itemStyles) this.selectTd.setStyles(this.queryview.json.itemStyles);
        //}
        this.categoryTd = new Element("td", {
            "styles": this.css.viewContentCategoryTdNode,
            "colspan": this.queryview.queryviewJson.selectEntryList.length
        }).inject(this.node);
        //this.categoryIconNode = new Element("div", {"styles": this.css.viewContentCategoryIconNode}).inject(this.categoryTd);
        this.categoryTd.set("html", "<span style='font-family: Webdings'><img src='/x_component_cms_Xform/widget/$QueryView/"+this.queryview.options.style+"/icon/right.png'/></span> "+this.data.group);
        if (this.queryview.json.itemStyles) this.categoryTd.setStyles(this.queryview.json.itemStyles);

        //this.data.list.each(function(line){
        //    this.items.push(new MWF.xApplication.cms.Xform.widget.QueryView.Item(this.queryview, line));
        //}.bind(this));

        this.setEvent();
    },
    setEvent: function(){
        if (this.selectTd){
            this.node.addEvents({
                //"mouseover": function(){
                //    if (!this.isSelected){
                //        var iconName = "checkbox";
                //        if (this.queryview.json.select=="single") iconName = "radiobox";
                //        this.selectTd.setStyles({"background": "url("+"/x_component_cms_Xform/$Form/default/icon/"+iconName+".png) center center no-repeat"});
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
        this.node.getElement("span").set("html", "<img src='/x_component_cms_Xform/widget/$QueryView/"+this.queryview.options.style+"/icon/right.png'/>");
    },
    expand: function(){
        this.items.each(function(item){
            item.node.setStyle("display", "table-row");
        }.bind(this));
        this.node.getElement("span").set("html", "<img src='/x_component_cms_Xform/widget/$QueryView/"+this.queryview.options.style+"/icon/down.png'/>");
        if (!this.loadChild){
            //window.setTimeout(function(){
                this.data.list.each(function(line){
                    this.items.push(new MWF.xApplication.cms.Xform.widget.QueryView.Item(this.queryview, line, this));
                }.bind(this));
                this.loadChild = true;
            //}.bind(this), 10);
        }
    }
});