MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatDesigner = MWF.xApplication.query.StatDesigner || {};
MWF.APPDSTD = MWF.xApplication.query.StatDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);

MWF.xDesktop.requireApp("query.StatDesigner", "lp."+MWF.language, null, false);
//MWF.xDesktop.requireApp("query.ViewDesigner", "View", null, false);
MWF.xDesktop.requireApp("query.StatDesigner", "Property", null, false);

MWF.xApplication.query.StatDesigner.Stat = new Class({
    //Extends: MWF.xApplication.query.ViewDesigner.View,
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "/x_component_query_StatDesigner/$Stat/stat.html"
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "/x_component_query_StatDesigner/$Stat/";
        this.cssPath = "/x_component_query_StatDesigner/$Stat/"+this.options.style+"/css.wcss";

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
        if (this.json.view){
            if (!this.queryView){
                this.designer.actions.getView(this.json.view, function(view){
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
                if (this.queryView.group.column){
                    groupNode.setStyle("display", "block");
                }else{
                    this.json.data.calculate.isGroup = false;
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
            this.designer.actions.getView(this.json.view, function(view){
                this.queryView = JSON.decode(view.data.data);
                this.checkIsGroupRadioDisplay();
            }.bind(this));
        }else{
            this.checkIsGroupRadioDisplay();
        }
    },
    autoSave: function(){
        this.autoSaveTimerID = window.setInterval(function(){
            if (!this.autoSaveCheckNode) this.autoSaveCheckNode = this.designer.contentToolbarNode.getElement("#MWFDictionaryAutoSaveCheck");
            if (this.autoSaveCheckNode){
                if (this.autoSaveCheckNode.get("checked")){
                    this.save();
                }
            }
        }.bind(this), 60000);
    },
    parseData: function(){
        this.json = this.data;
    },
    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.query.StatDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
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
    hideProperty: function(){
        if (this.property) this.property.hide();
    },

    load : function(){
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));
        this.areaNode.inject(this.node);

        this.designer.viewListAreaNode.getChildren().each(function(node){
            var view = node.retrieve("view");
            if (view.id==this.data.id){
                if (this.designer.currentListViewItem){
                    this.designer.currentListViewItem.setStyles(this.designer.css.listViewItem);
                }
                node.setStyles(this.designer.css.listViewItem_current);
                this.designer.currentListViewItem = node;
                this.lisNode = node;
            }
        }.bind(this));

        this.domListNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.designer.propertyDomArea);

        this.loadView();

        this.selected();
        this.setEvent();

        //if (this.options.showTab) this.page.showTabIm();
        this.setViewWidth();
        this.designer.addEvent("resize", this.setViewWidth.bind(this));
    },
    setEvent: function(){
        this.areaNode.addEvent("click", this.selected.bind(this));
        this.refreshNode.addEvent("click", function(e){
            this.loadViewData();
            e.stopPropagation();
        }.bind(this));
        this.addColumnNode.addEvent("click", function(e){
            this.addColumn();
            e.stopPropagation();
        }.bind(this));
    },
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

                this.designer.actions.loadStat(this.data.id, null, function(json){
                    var entries = {};
                    json.data.calculate.calculateList.each(function(entry){entries[entry.id] = entry;}.bind(this));

                    if (this.json.data.calculate.isGroup){
                        if (json.data.calculateGrid.length){
                            json.data.calculateGrid.each(function(d){
                                // var groupColumn = null;
                                // for (var c = 0; c<json.data.calculate.calculateList.length; c++){
                                //     if (json.data.calculate.calculateList[c].column === json.data.group.column){
                                //         groupColumn = json.data.calculate.calculateList[c];
                                //         break;
                                //     }
                                // }

                                var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                                var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                // if (groupColumn){
                                //     td.set("text", (groupColumn.code) ? MWF.Macro.exec(groupColumn.code, {"value": d.group, "data": json.data}) : d.group);
                                // }else{
                                     td.set("text", d.group);
                                // }

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
    setContentColumnWidth: function(){
        var titleTds = this.viewTitleTrNode.getElements("td");
        var widthList = [];
        titleTds.each(function(td){widthList.push(td.getSize().x);});

        var flag = false;

        if (this.viewContentTableNode){
            trs = this.viewContentTableNode.getElements("tr");
            for (var i=0; i<trs.length; i++){
                var tr = trs[i];
                var tds = tr.getElements("td");
                tds.each(function(contentTd, i){
                    if (contentTd.get("colSpan")==1){
                        contentTd.setStyle("width", ""+widthList[i]+"px");
                        flag = true;
                    }
                });
                if (flag) break;
            }

            //var tr = this.viewContentTableNode.getFirst("tr");
            //if (tr){
            //    var tds = tr.getElements("td");
            //    tds.each(function(contentTd, i){
            //        if (!contentTd.get("colSpan")){
            //            contentTd.setStyle("width", ""+widthList[i]+"px");
            //        }
            //    });
            //}
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
            if (!this.json.data.calculate.calculateList) this.json.data.calculate.calculateList = [];
            this.json.data.calculate.calculateList.push(json);
            var column = new MWF.xApplication.query.StatDesigner.Stat.Column(json, this);
            this.items.push(column);
            column.selected();
            if (this.property) this.property.loadStatColumnSelect();

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
   selected: function(){
        if (this.currentSelectedModule){
            if (this.currentSelectedModule==this){
                return true;
            }else{
                this.currentSelectedModule.unSelected();
            }
        }
        this.currentSelectedModule = this;
        this.showProperty();
    },
    unSelected: function(){
        this.currentSelectedModule = null;
        this.hideProperty();
    },

    loadViewNodes: function(){
        this.viewAreaNode = new Element("div#viewAreaNode", {"styles": this.css.viewAreaNode}).inject(this.areaNode);
        this.viewTitleNode = new Element("div#viewTitleNode", {"styles": this.css.viewTitleNode}).inject(this.viewAreaNode);

        this.refreshNode = new Element("div", {"styles": this.css.refreshNode}).inject(this.viewTitleNode);
        this.addColumnNode = new Element("div", {"styles": this.css.addColumnNode}).inject(this.viewTitleNode);

        this.viewTitleContentNode = new Element("div", {"styles": this.css.viewTitleContentNode}).inject(this.viewTitleNode);
        this.viewTitleTableNode = new Element("table", {
            "styles": this.css.viewTitleTableNode,
            "border": "0px",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.viewTitleContentNode);
        this.viewTitleTrNode = new Element("tr", {"styles": this.css.viewTitleTrNode}).inject(this.viewTitleTableNode);


        this.viewContentScrollNode = new Element("div", {"styles": this.css.viewContentScrollNode}).inject(this.viewAreaNode);
        this.viewContentNode = new Element("div", {"styles": this.css.viewContentNode}).inject(this.viewContentScrollNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.viewContentScrollNode, {"style": "view", "distance": 100, "indent": false});
        }.bind(this));

        this.contentLeftNode = new Element("div", {"styles": this.css.contentLeftNode}).inject(this.viewContentNode);
        this.contentRightNode = new Element("div", {"styles": this.css.contentRightNode}).inject(this.viewContentNode);
        this.viewContentBodyNode = new Element("div", {"styles": this.css.viewContentBodyNode}).inject(this.viewContentNode);
        this.viewContentTableNode = new Element("table", {
            "styles": this.css.viewContentTableNode,
            "border": "0px",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.viewContentBodyNode);
    },
    setContentHeight: function(){
        var size = this.areaNode.getSize();
        var titleSize = this.viewTitleNode.getSize();
        var height = size.y-titleSize.y-2;

        this.viewContentScrollNode.setStyle("height", height);

        var contentSize = this.viewContentBodyNode.getSize();
        if (height<contentSize.y) height = contentSize.y+10;

        this.viewContentNode.setStyle("height", height);
        this.contentLeftNode.setStyle("height", height);
        this.contentRightNode.setStyle("height", height);
        //this.viewContentBodyNode.setStyle("min-height", height);
    },
    loadViewColumns: function(){
    //    for (var i=0; i<10; i++){
        if (this.json.data.calculate.calculateList) {
            this.json.data.calculate.calculateList.each(function (json) {
                this.items.push(new MWF.xApplication.query.StatDesigner.Stat.Column(json, this));
            }.bind(this));
        }
    //    }
    },
    loadView: function(){
        this.loadViewNodes();
        this.loadViewColumns();
//        this.addTopItemNode.addEvent("click", this.addTopItem.bind(this));
    },
    setViewWidth: function(){
        this.viewAreaNode.setStyle("width", "auto");
        this.viewTitleNode.setStyle("width", "auto");

        var s1 = this.viewTitleTableNode.getSize();
        var s2 = this.refreshNode.getSize();
        var s3 = this.addColumnNode.getSize();
        var width = s1.x+s2.x+s2.x;
        var size = this.areaNode.getSize();

        if (width>size.x){
            this.viewTitleNode.setStyle("width", ""+width+"px");
            this.viewAreaNode.setStyle("width", ""+width+"px");
        }else{
            this.viewTitleNode.setStyle("width", ""+size.x+"px");
            this.viewAreaNode.setStyle("width", ""+size.x+"px");
        }
        this.setContentColumnWidth();
        this.setContentHeight();
    },

    //setPropertyContent: function(){
    //    this.designer.propertyIdNode.set("text", this.data.id);
    //    this.designer.propertyNameNode.set("value", this.data.name);
    //    this.designer.propertyAliasNode.set("value", this.data.alias);
    //    this.designer.propertyDescriptionNode.set("value", this.data.description);
    //
    //    this.designer.jsonDomNode.empty();
    //    MWF.require("MWF.widget.JsonParse", function(){
    //        this.jsonParse = new MWF.widget.JsonParse(this.data.data, this.designer.jsonDomNode, this.designer.jsonTextAreaNode);
    //        window.setTimeout(function(){
    //            this.jsonParse.load();
    //        }.bind(this), 1);
    //    }.bind(this));
    //},
    setAreaNodeSize: function(){
        //var size = this.node.getSize();
        ////var tabSize = this.tab.tabNodeContainer.getSize();
        //var tabSize = this.node.getSize();
        //var y = size.y - tabSize.y;
        //this.areaNode.setStyle("height", ""+y+"px");
        //if (this.editor) if (this.editor.editor) this.editor.editor.resize();
    },

    // createRootItem: function() {
    //     this.items.push(new MWF.xApplication.process.DictionaryDesigner.Dictionary.item("ROOT", this.data.data, null, 0, this, true));
    // },
    saveSilence: function(callback){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.notice.inputName, "error");
            return false;
        }
        // if (!this.data.view){
        //     this.designer.notice(this.designer.lp.notice.inputView, "error");
        //     return false;
        // }
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
        // if (!this.data.view){
        //     this.designer.notice(this.designer.lp.notice.inputView, "error");
        //     return false;
        // }
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
        // if (this.json.view){
        var flag = true;
        for (var i = 0; i<this.items.length; i++){
            if (!this.items[i].checkColumn()) flag = false;
        }
        return flag;
        // }else{
        //     return false;
        // }
    },
    explode: function(){},
    implode: function(){},
    _setEditStyle: function(name, input, oldValue){

        // if (name=="view"){
        //     if (this.data.view!=oldValue){
        //         this.viewContentBodyNode.empty();
        //         this.designer.actions.getView(this.json.view, function(view){
        //             this.queryView = JSON.decode(view.data.data);
        //         }.bind(this), null, false);
        //
        //         this.changeViewSelected();
        //         this.checkViewAndColumn();
        //     }
        // }
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
    saveAs: function(){

        var form = new MWF.xApplication.query.StatDesigner.Stat.NewNameForm(this, {
            name : this.data.name + "_" + MWF.xApplication.query.StatDesigner.LP.copy,
            view : this.data.view,
            query : this.data.query || this.data.application,
            queryName :	this.data.queryName || this.data.applicationName
        }, {
            onSave : function( data, callback ){
                this._saveAs( data, callback );
            }.bind(this)
        }, {
            app: this.designer
        });
        form.edit()
    },
    cloneObject : function( obj ){
        if (null == obj || "object" != typeof obj) return obj;

        if ( typeof obj.length==='number'){ //数组
            //print( "array" );
            var copy = [];
            for (var i = 0, len = obj.length; i < len; ++i) {
                copy[i] = this.cloneObject(obj[i]);
            }
            return copy;
        }else{
            var copy = {};
            for (var attr in obj) {
                copy[attr] = this.cloneObject(obj[attr]);
            }
            return copy;
        }
    },
    _saveAs : function( data , callback){
        var _self = this;

        var d = Object.clone( this.data );

        d.isNewView = true;
        d.id = this.designer.actions.getUUID();
        d.name = data.name;
        d.alias = "";
        d.query = data.query;
        d.queryName = data.queryName;
        d.application = data.query;
        d.applicationName = data.queryName;
        d.view = data.view;
        //d.pid = d.id + d.id;

        //delete d[this.data.id+"viewFilterType"];
        //d[d.id+"viewFilterType"]="custom";

        if( d.data.calculate && d.data.calculate.calculateList ){
            d.data.calculate.calculateList.each( function( entry ){
                entry.id = (new MWF.widget.UUID).id;
            }.bind(this));
        }

        this.designer.actions.saveStat(d, function(json){
            this.designer.notice(this.designer.lp.notice.saveAs_success, "success", this.node, {"x": "left", "y": "bottom"});
            if (callback) callback();
        }.bind(this));
    }
    //_setEditStyle: function(){}

});


MWF.xApplication.query.StatDesigner.Stat.Column = new Class({
    //Extends:MWF.xApplication.query.ViewDesigner.View.Column,
	initialize: function(json, view, next){
        this.propertyPath = "/x_component_query_StatDesigner/$Stat/column.html";
		this.view = view;
        this.json = json;
        this.next = next;
        this.css = this.view.css;
        this.content = this.view.viewTitleTrNode;
        this.domListNode = this.view.domListNode;
        this.load();
	},
    load: function(){
        this.areaNode = new Element("td", {"styles": this.css.viewTitleColumnAreaNode});
        this.areaNode.store("column", this);

        if (this.next){
            this.areaNode.inject(this.next.areaNode, "before");
        }else{
            this.areaNode.inject(this.content);
        }

        this.node = new Element("div", {
            "styles": this.css.viewTitleColumnNode
        }).inject(this.areaNode);
        this.textNode = new Element("div", {
            "styles": this.css.viewTitleColumnTextNode,
            "text": this.json.displayName
        }).inject(this.node);

        this.createDomListItem();


        this._createIconAction();

        //if (!this.json.export) this.hideMode();

        this.setEvent();
    },
    createDomListItem: function(){
        this.listNode = new Element("div", {"styles": this.css.cloumnListNode});
        if (this.next){
            this.listNode.inject(this.next.listNode, "before");
        }else{
            this.listNode.inject(this.domListNode);
        }
        var listIconNode = new Element("div", {"styles": this.css.cloumnListIconNode}).inject(this.listNode);
        var listTextNode = new Element("div", {"styles": this.css.cloumnListTextNode}).inject(this.listNode);
        this.resetTextNode();
    },
    setEvent: function(){
        this.node.addEvents({
            "click": function(e){this.selected(); e.stopPropagation();}.bind(this),
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.viewTitleColumnNode_over)}.bind(this),
            "mouseout": function(){if (!this.isSelected) if (this.isError){
                this.node.setStyles(this.css.viewTitleColumnNode_error)
            }else{
                this.node.setStyles(this.css.viewTitleColumnNode)
            }}.bind(this)
        });
        this.listNode.addEvents({
            "click": function(e){this.selected(); e.stopPropagation();}.bind(this),
            "mouseover": function(){debugger; if (!this.isSelected) this.listNode.setStyles(this.css.cloumnListNode_over)}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.listNode.setStyles(this.css.cloumnListNode)}.bind(this)
        });
    },
    _createIconAction: function(){
        if (!this.actionArea){
            this.actionArea = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.view.areaNode, "after");

            this._createAction({
                "name": "move",
                "icon": "move1.png",
                "event": "mousedown",
                "action": "move",
                "title": MWF.APPDVD.LP.action.move
            });
            this._createAction({
                "name": "add",
                "icon": "add.png",
                "event": "click",
                "action": "addColumn",
                "title": MWF.APPDVD.LP.action.add
            });
            this._createAction({
                "name": "delete",
                "icon": "delete1.png",
                "event": "click",
                "action": "delete",
                "title": MWF.APPDVD.LP.action["delete"]
            });
        }
    },
    _createAction: function(action){
        var actionNode = new Element("div", {
            "styles": this.css.actionNodeStyles,
            "title": action.title
        }).inject(this.actionArea);
        actionNode.setStyle("background", "url("+this.view.path+this.view.options.style+"/action/"+action.icon+") no-repeat left center");
        actionNode.addEvent(action.event, function(e){
            this[action.action](e);
        }.bind(this));
        actionNode.addEvents({
            "mouseover": function(e){
                e.target.setStyle("border", "1px solid #999");
            }.bind(this),
            "mouseout": function(e){
                e.target.setStyle("border", "1px solid #F1F1F1");
            }.bind(this)
        });
    },
    _setActionAreaPosition: function(){
        var p = this.node.getPosition(this.view.areaNode.getOffsetParent());
        var y = p.y-25;
        var x = p.x;
        this.actionArea.setPosition({"x": x, "y": y});
    },
    _showActions: function(){
        if (this.actionArea){
            this._setActionAreaPosition();
            this.actionArea.setStyle("display", "block");
        }
    },
    _hideActions: function(){
        if (this.actionArea) this.actionArea.setStyle("display", "none");
    },

    selected: function(){
        if (this.view.currentSelectedModule){
            if (this.view.currentSelectedModule==this){
                return true;
            }else{
                this.view.currentSelectedModule.unSelected();
            }
        }
        this.node.setStyles(this.css.viewTitleColumnNode_selected);
        this.listNode.setStyles(this.css.cloumnListNode_selected);
        new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        new Fx.Scroll(this.view.designer.propertyDomArea, {"wheelStops": false, "duration": 100}).toElement(this.listNode);

        this.view.currentSelectedModule = this;
        this.isSelected = true;
        this._showActions();
        this.showProperty();
    },
    unSelected: function(){
        this.view.currentSelectedModule = null;
        //this.node.setStyles(this.css.viewTitleColumnNode);
        if (this.isError){
            this.node.setStyles(this.css.viewTitleColumnNode_error)
        }else{
            this.node.setStyles(this.css.viewTitleColumnNode)
        }

        this.listNode.setStyles(this.css.cloumnListNode);
        this.isSelected = false;
        this._hideActions();
        this.hideProperty();
    },
    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.query.StatDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
                "path": this.propertyPath,
                "onPostLoad": function(){
                    this.property.show();
                    this.changeViewSelected();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    },
    hideProperty: function(){
        if (this.property) this.property.hide();
    },
    _setEditStyle: function(name, input, oldValue){
        //if (name=="displayName") this.resetTextNode();
        //if (name=="selectType") this.resetTextNode();
        //if (name=="attribute") this.resetTextNode();
        //if (name=="path") this.resetTextNode();
        if (name=="displayName") this.resetTextNode();
        if (name=="column") this.checkColumn();
        if (name=="view"){
            if (this.json.view!=oldValue){
                this.view.viewContentBodyNode.empty();
                this.view.designer.actions.getView(this.json.view, function(view){
                    this.queryView = JSON.decode(view.data.data);
                }.bind(this), null, false);

                this.changeViewSelected();
                //this.checkViewAndColumn();
            }
        }
    },
    resetTextNode: function(){
        var listText = this.json.displayName+"("+this.json.calculateType+")";

        this.textNode.set("text", this.json.displayName);
        this.listNode.getLast().set("text", listText);

        if (this.view.property) this.view.property.loadStatColumnSelect();
    },
    "delete": function(e){
        var _self = this;
        if (!e) e = this.node;
        this.view.designer.confirm("warn", e, MWF.APPDSTD.LP.notice.deleteColumnTitle, MWF.APPDSTD.LP.notice.deleteColumn, 300, 120, function(){
            _self.destroy();

            this.close();
        }, function(){
            this.close();
        }, null);
    },
    destroy: function(){
        if (this.view.currentSelectedModule==this) this.view.currentSelectedModule = null;
        if (this.actionArea) this.actionArea.destroy();
        if (this.listNode) this.listNode.destroy();
        if (this.property) this.property.propertyContent.destroy();

        var idx = this.view.items.indexOf(this);

        if (this.view.viewContentTableNode){
            var trs = this.view.viewContentTableNode.getElements("tr");
            trs.each(function(tr){
                tr.deleteCell(idx);
            }.bind(this));
        }

        if (this.view.json.data.selectList) this.view.json.data.selectList.erase(this.json);
        if (this.view.json.data.calculate) if (this.view.json.data.calculate.calculateList) this.view.json.data.calculate.calculateList.erase(this.json);
        this.view.items.erase(this);
        if (this.view.property) this.view.property.loadStatColumnSelect();

        this.areaNode.destroy();
        this.view.selected();

        this.view.setViewWidth();

        MWF.release(this);
        delete this;
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

            var idx = this.view.json.data.calculate.calculateList.indexOf(this.json);
            this.view.json.data.calculate.calculateList.splice(idx, 0, json);

            var column = new MWF.xApplication.query.StatDesigner.Stat.Column(json, this.view, this);
            this.view.items.splice(idx, 0, column);
            column.selected();
            if (this.view.property) this.view.property.loadStatColumnSelect();

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
    move: function(e){
        var columnNodes = [];
        this.view.items.each(function(item){
            if (item!=this){
                columnNodes.push(item.areaNode);
            }
        }.bind(this));

        this._createMoveNode();

        this._setNodeMove(columnNodes, e);
    },
    _createMoveNode: function(){
        this.moveNode = new Element("div", {"text": this.node.get("text")});
        this.moveNode.inject(this.view.designer.content);
        this.moveNode.setStyles({
            "border": "2px dashed #ffa200",
            "opacity": 0.7,
            "height": "30px",
            "line-height": "30px",
            "padding": "0px 10px",
            "position": "absolute"
        });
    },
    _setMoveNodePosition: function(e){
        var x = e.page.x+2;
        var y = e.page.y+2;
        this.moveNode.positionTo(x, y);
    },
    createMoveFlagNode: function(){
        this.moveFlagNode = new Element("td", {"styles": this.css.moveFlagNode});
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
                    var idx = this.view.json.data.calculate.calculateList.indexOf(column.json);

                    this.view.json.data.calculate.calculateList.erase(this.json);
                    this.view.items.erase(this);

                    this.view.json.data.calculate.calculateList.splice(idx, 0, this.json);
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
            if (this.json.view){
                if (!this.queryView){
                    this.view.designer.actions.getView(this.json.view, function(view){
                        this.queryView = JSON.decode(view.data.data);
                        this.changeViewColumnOptions(this.queryView);
                    }.bind(this));
                }else{
                    this.changeViewColumnOptions(this.queryView);
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
                    json.selectList.each(function(col){
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
        if (!this.queryView){
            if (this.json.view){
                this.view.designer.actions.getView(this.json.view, function(view){
                    this.queryView = JSON.decode(view.data.data);
                }.bind(this), null, false);
            }else{
                this.errorMark();
                return false;
            }
        }

        var col = this.queryView.selectList.filter(function(c){
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