MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.StatDesigner = MWF.xApplication.process.StatDesigner || {};
MWF.APPSTD = MWF.xApplication.process.StatDesigner;

MWF.xDesktop.requireApp("process.StatDesigner", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("process.ViewDesigner", "View", null, false);
MWF.xDesktop.requireApp("process.StatDesigner", "Property", null, false);

MWF.xApplication.process.StatDesigner.Stat = new Class({
    Extends: MWF.xApplication.process.ViewDesigner.View,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "/x_component_process_StatDesigner/$Stat/stat.html"
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "/x_component_process_StatDesigner/$Stat/";
        this.cssPath = "/x_component_process_StatDesigner/$Stat/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.data = data;
        if (!this.data.data) this.data.data = {};
        this.parseData();

        this.node = this.designer.designNode;
        this.areaNode = new Element("div", {"styles": {"height": "100%", "overflow": "auto"}});

        this.propertyListNode = this.designer.propertyDomArea;

        if(this.designer.application) this.data.applicationName = this.designer.application.name;
        if(this.designer.application) this.data.application = this.designer.application.id;

        this.isNewView = (this.data.id) ? false : true;

        this.items = [];
        this.view = this;
        this.queryView = null;

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    changeViewSelected: function(){
        if (this.json.queryView){
            if (!this.queryView){
                this.designer.actions.getView(this.json.queryView, function(view){
                    this.queryView = JSON.decode(view.data.data);
                    this.items.each(function(item){
                        item.changeViewSelected(this.queryView);
                    }.bind(this));
                    this.checkIsGroupRadioDisplay();
                }.bind(this));
            }else{
                this.items.each(function(item){
                    item.changeViewSelected(this.queryView);
                }.bind(this));
                this.checkIsGroupRadioDisplay();
            }
        }else{
            //item.changeViewSelected();
        }
    },
    checkIsGroupRadioDisplay: function(){
        if (this.property){
            var groupNode = this.property.propertyContent.getElement(".MWFIsGroupArea");
            if (groupNode){
                if (this.queryView.groupEntry.column){
                    groupNode.setStyle("display", "block");
                }else{
                    this.json.data.isGroup = false;
                    var radios = groupNode.getElements("input");
                    for (var i=0; i<radios.length; i++){
                        if (radios[i].value=="false"){
                            radios[i].set("checked", true);
                            break;
                        }
                    }
                    this.hideGroupTitle();
                    groupNode.setStyle("display", "none");
                }
            }
        }
    },
    checkIsGroupRadio: function(){
        if (!this.queryView){
            this.designer.actions.getView(this.json.queryView, function(view){
                this.queryView = JSON.decode(view.data.data);
                this.checkIsGroupRadioDisplay();
            }.bind(this));
        }else{
            this.checkIsGroupRadioDisplay();
        }
    },

    showProperty: function(){

        if (!this.property){
            this.property = new MWF.xApplication.process.StatDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
                "path": this.options.propertyPath,
                "onPostLoad": function(){
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    },

    //getDataTr: function(ri){
    //    var tr = null;
    //    var trs = this.viewContentTableNode.getElements("tr");
    //    if (trs.length) if (trs[ri]) tr = trs[ri];
    //    return tr;
    //},
    //getDataTd: function(tr, ci){
    //    var td = null;
    //    var tds = tr.getElements("td");
    //    if (tds.length) if (tds[ci]) td = tds[ci];
    //    return td;
    //},
    //getDataLastColumnCell: function(ri, ci){
    //    var trs = this.viewContentTableNode.getElements("tr");
    //    if (trs.length) if (trs[ri]) tr = trs[ri];
    //    var td = null;
    //    var tds = tr.getElements("td");
    //    if (tds.length) if (tds[ci]) td = tds[ci];
    //    return td;
    //},
    loadViewData: function(){
        if (this.data.id){
            this.saveSilence(function(){
                this.viewContentBodyNode.empty();
                this.viewContentTableNode = new Element("table", {
                    "styles": this.css.viewContentTableNode,
                    "border": "0px",
                    "cellPadding": "0",
                    "cellSpacing": "0"
                }).inject(this.viewContentBodyNode);

                this.designer.actions.loadStat(this.data.id, function(json){
                    var entries = {};
                    json.data.selectEntryList.each(function(entry){entries[entry.column] = entry;}.bind(this));

                    if (this.json.data.calculate.isGroup){
                        if (json.data.calculateGrid.length){
                            json.data.calculateGrid.each(function(d){
                                var groupColumn = null;
                                for (var c = 0; c<json.data.selectEntryList.length; c++){
                                    if (json.data.selectEntryList[c].column === json.data.groupEntry.column){
                                        groupColumn = json.data.selectEntryList[c];
                                        break;
                                    }
                                }

                                var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                                var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                if (groupColumn){
                                    td.set("text", (groupColumn.code) ? MWF.Macro.exec(groupColumn.code, {"value": d.group, "data": json.data}) : d.group);
                                }else{
                                    td.set("text", d.group);
                                }

                                //td.set("text", d.group);
                                td.setStyle("font-weight", "bold");
                                d.list.each(function(c){
                                    var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                    td.set("text", (entries[c.column].code) ? MWF.Macro.exec(entries[c.column].code, {"value": c.value, "data": json.data}) : c.value);
                                }.bind(this));

                            }.bind(this));
                        }
                        if (json.data.calculateAmountGrid){
                            var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                            var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                            td.set("text", this.designer.lp.amount);
                            td.setStyles({"font-weight": "bold", "color": "#0000FF"});
                            json.data.calculateAmountGrid.each(function(c){
                                var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                td.set("text", c.value);
                                td.setStyles({"font-weight": "bold", "color": "#0000FF"});
                            }.bind(this));
                        }
                    }else{
                        if (json.data.calculateGrid.length){
                            var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                            json.data.calculateGrid.each(function(d){
                                var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                td.set("text", d.value);
                            }.bind(this));
                        }
                    }
                    this.setContentColumnWidth();
                    this.setContentHeight();

                }.bind(this));
            }.bind(this));
        }
    },

    addColumn: function(){

        MWF.require("MWF.widget.UUID", function(){
            var id = (new MWF.widget.UUID).id;
            var json = {
                "id": id,
                "column": "",
                "displayName": this.designer.lp.unnamed,
                "calculateType": "sum",
                "orderType": "original",
                "orderEffectType": "key"
            };
            if (!this.json.data.calculate.calculateEntryList) this.json.data.calculate.calculateEntryList = [];
            this.json.data.calculate.calculateEntryList.push(json);
            var column = new MWF.xApplication.process.StatDesigner.Stat.Column(json, this);
            this.items.push(column);
            column.selected();

            if (this.viewContentTableNode){
                var trs = this.viewContentTableNode.getElements("tr");
                trs.each(function(tr){
                    new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr)
                }.bind(this));
                //this.setContentColumnWidth();
            }
            this.setViewWidth();
            this.addColumnNode.scrollIntoView(true);

        }.bind(this));
        //new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 0}).toRight();
    },

    loadViewColumns: function(){
    //    for (var i=0; i<10; i++){
        if (this.json.data.calculate.calculateEntryList) {
            this.json.data.calculate.calculateEntryList.each(function (json) {
                this.items.push(new MWF.xApplication.process.StatDesigner.Stat.Column(json, this));
            }.bind(this));
        }
    //    }
    },

    saveSilence: function(callback){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.notice.inputName, "error");
            return false;
        }
        if (this.data.queryview){
            this.designer.notice(this.designer.lp.notice.inputView, "error");
            return false;
        }
        if (!this.checkViewAndColumn()){
            this.designer.notice(this.designer.lp.notice.errorViewColumn, "error");
            return false;
        }

        this.designer.actions.saveStat(this.data, function(json){
            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            if (callback) callback();
        }.bind(this));
    },
    save: function(callback){
        //if (this.designer.tab.showPage==this.page){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.notice.inputName, "error");
            return false;
        }
        if (this.data.queryview){
            this.designer.notice(this.designer.lp.notice.inputView, "error");
            return false;
        }
        if (!this.checkViewAndColumn()){
            this.designer.notice(this.designer.lp.notice.errorViewColumn, "error");
            return false;
        }
        //}
        this.designer.actions.saveStat(this.data, function(json){
            this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            if (callback) callback();
        }.bind(this));
    },
    checkViewAndColumn: function(){
        if (this.json.queryView){
            var flag = true;
            for (var i = 0; i<this.items.length; i++){
                if (!this.items[i].checkColumn()) flag = false;
            }
            return flag;
        }else{
            return false;
        }
    },
    _setEditStyle: function(name, input, oldValue){

        if (name=="queryView"){
            if (this.data.queryView!=oldValue){
                this.viewContentBodyNode.empty();
                this.designer.actions.getView(this.json.queryView, function(view){
                    this.queryView = JSON.decode(view.data.data);
                }.bind(this), null, false);

                this.changeViewSelected();
                this.checkViewAndColumn();
            }
        }
        if (name=="data.calculate.isGroup"){
            this.viewContentBodyNode.empty();
            if (this.data.data.calculate.isGroup){
                this.showGroupTitle();
            }else{
                this.hideGroupTitle();
            }
        }
        if (name=="data.calculate.title"){
            if (!this.data.data.calculate.title){
                this.data.data.calculate.title = this.designer.lp.category;
            }
            if (this.data.data.calculate.title!= oldValue){
                if (this.groupTitleNode){
                    this.groupTitleNode.getFirst().getFirst().set("text", this.data.data.calculate.title);
                }
            }
        }
    },
    showGroupTitle: function(){
        if (!this.groupTitleNode) this.createGroupTitltNode();
    },
    hideGroupTitle: function(){
        if (this.groupTitleNode){
            this.groupTitleNode.destroy();
            this.groupTitleNode = null;
        }
    },
    createGroupTitltNode: function(){
        this.data.data.calculate.title = this.data.data.calculate.title || this.designer.lp.category;

        this.groupTitleNode = new Element("td", {"styles": this.css.viewGroupTitleNode});
        var node = new Element("div", {
            "styles": this.css.viewGroupTitleColumnNode
        }).inject(this.groupTitleNode);
        var textNode = new Element("div", {
            "styles": this.css.viewGroupTitleColumnTextNode,
            "text": this.data.data.calculate.title
        }).inject(node);
        if (this.items.length){
            this.groupTitleNode.inject(this.items[0].areaNode, "before");
        }else{
            this.groupTitleNode.inject(this.viewTitleTrNode);
        }
    },
    saveAs: function(){},
    explode: function(){},
    implode: function(){}
    //_setEditStyle: function(){}

});


MWF.xApplication.process.StatDesigner.Stat.Column = new Class({
    Extends:MWF.xApplication.process.ViewDesigner.View.Column,
	initialize: function(json, view, next){
        this.propertyPath = "/x_component_process_StatDesigner/$Stat/column.html";
		this.view = view;
        this.json = json;
        this.next = next;
        this.css = this.view.css;
        this.content = this.view.viewTitleTrNode;
        this.domListNode = this.view.domListNode;
        this.load();
	},

    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.process.StatDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
                "path": this.propertyPath,
                "onPostLoad": function(){
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    },

    _setEditStyle: function(name, input, oldValue){
        //if (name=="displayName") this.resetTextNode();
        //if (name=="selectType") this.resetTextNode();
        //if (name=="attribute") this.resetTextNode();
        //if (name=="path") this.resetTextNode();
        if (name=="displayName") this.resetTextNode();
        if (name=="column") this.checkColumn();
    },
    resetTextNode: function(){
        var listText = this.json.displayName+"("+this.json.calculateType+")";

        this.textNode.set("text", this.json.displayName);
        this.listNode.getLast().set("text", listText);
    },
    "delete": function(e){
        var _self = this;
        if (!e) e = this.node;
        this.view.designer.confirm("warn", e, MWF.APPSTD.LP.notice.deleteColumnTitle, MWF.APPSTD.LP.notice.deleteColumn, 300, 120, function(){
            _self.destroy();

            this.close();
        }, function(){
            this.close();
        }, null);
    },

    addColumn: function(e, data){
        MWF.require("MWF.widget.UUID", function(){
            var json;
            if (data){
                json = Object.clone(data);
                json.id = (new MWF.widget.UUID).id;
                json.column = (new MWF.widget.UUID).id;
            }else{
                var id = (new MWF.widget.UUID).id;
                json = {
                    "id": id,
                    "column": "",
                    "displayName": this.view.designer.lp.unnamed,
                    "calculateType": "sum",
                    "orderType": "original",
                    "orderEffectType": "key"
                };
            }

            var idx = this.view.json.data.calculate.calculateEntryList.indexOf(this.json);
            this.view.json.data.calculate.calculateEntryList.splice(idx, 0, json);

            var column = new MWF.xApplication.process.StatDesigner.Stat.Column(json, this.view, this);
            this.view.items.splice(idx, 0, column);
            column.selected();

            if (this.view.viewContentTableNode){
                var trs = this.view.viewContentTableNode.getElements("tr");
                trs.each(function(tr){
                    var td = tr.insertCell(idx);
                    td.setStyles(this.css.viewContentTdNode);
                }.bind(this));
            }
            this.view.setViewWidth();

        }.bind(this));
    },

    _setNodeMove: function(droppables, e){
        this._setMoveNodePosition(e);
        var movePosition = this.moveNode.getPosition();
        var moveSize = this.moveNode.getSize();
        var contentPosition = this.content.getPosition();
        var contentSize = this.content.getSize();

        var nodeDrag = new Drag.Move(this.moveNode, {
            "droppables": droppables,
            "limit": {
                "x": [contentPosition.x, contentPosition.x+contentSize.x],
                "y": [movePosition.y, movePosition.y+moveSize.y]
            },
            "onEnter": function(dragging, inObj){
                if (!this.moveFlagNode) this.createMoveFlagNode();
                this.moveFlagNode.inject(inObj, "before");
            }.bind(this),
            "onLeave": function(dragging, inObj){
                if (this.moveFlagNode){
                    this.moveFlagNode.dispose();
                }
            }.bind(this),
            "onDrop": function(dragging, inObj){
                if (inObj){
                    this.areaNode.inject(inObj, "before");
                    var column = inObj.retrieve("column");
                    this.listNode.inject(column.listNode, "before");
                    var idx = this.view.json.data.calculate.calculateEntryList.indexOf(column.json);

                    this.view.json.data.calculate.calculateEntryList.erase(this.json);
                    this.view.items.erase(this);

                    this.view.json.data.calculate.calculateEntryList.splice(idx, 0, this.json);
                    this.view.items.splice(idx, 0, this);

                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode) this.moveFlagNode.destroy();
                    this._setActionAreaPosition();
                }else{
                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode) this.moveFlagNode.destroy();
                }
            }.bind(this),
            "onCancel": function(dragging){
                if (this.moveNode) this.moveNode.destroy();
                if (this.moveFlagNode) this.moveFlagNode.destroy();
            }.bind(this)
        });
        nodeDrag.start(e);
    },
    changeViewSelected: function(json){

        if (json){
            this.changeViewColumnOptions(json);
        }else{
            if (this.view.json.queryView){
                if (!this.view.queryView){
                    this.view.designer.actions.getView(this.view.json.queryView, function(view){
                        this.view.queryView = JSON.decode(view.data.data);
                        this.changeViewColumnOptions(this.view.queryView);
                    }.bind(this));
                }else{
                    this.changeViewColumnOptions(this.view.queryView);
                }
            }else{
                this.changeViewColumnOptions(json);
            }
        }
    },
    changeViewColumnOptions: function(json){
        if (this.property){
            var nodes = this.property.propertyContent.getElements(".MWFViewColumnSelect");
            nodes.each(function(node){
                node.empty();
                if (json){
                    new Element("option", {
                        "value": "",
                        "text": "(none)",
                        "selected": (!this.json.column)
                    }).inject(node);
                    json.selectEntryList.each(function(col){
                        var o = new Element("option", {
                            "value": col.column,
                            "text": col.displayName,
                            "selected": (col.column==this.json.column)
                        }).inject(node);
                    }.bind(this));
                }else{
                    this.json.column = "";
                }
            }.bind(this));
        }
    },
    checkColumn: function(){
        var flag = true;
        if (!this.view.queryView){
            this.designer.actions.getView(this.view.json.queryView, function(view){
                this.view.queryView = JSON.decode(view.data.data);
            }.bind(this), null, false);
        }

        var col = this.view.queryView.selectEntryList.filter(function(c){
            return (c.column==this.json.column);
        }.bind(this));
        if (!col.length){
            this.errorMark();
            flag = false;
        }else{
            this.errorMark(true);
        }
        return flag;
    },
    errorMark: function(flag){
        if (flag){
            this.isError = false;
            if (!this.isSelected) this.node.setStyles(this.css.viewTitleColumnNode);
        }else{
            this.isError = true;
            if (!this.isSelected) this.node.setStyles(this.css.viewTitleColumnNode_error);
        }
    }
});