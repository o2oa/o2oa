MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.APPDVD = MWF.xApplication.query.ViewDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);

MWF.xDesktop.requireApp("query.ViewDesigner", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("query.ViewDesigner", "Property", null, false);

MWF.xApplication.query.ViewDesigner.ViewBase = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "../x_component_query_ViewDesigner/$View/view.html"
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "../x_component_query_ViewDesigner/$View/";
        this.cssPath = "../x_component_query_ViewDesigner/$View/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.data = data;
        if (!this.data.data) this.data.data = {};
        this.parseData();

        this.node = this.designer.designNode;
        //this.tab = this.designer.tab;

        this.areaNode = new Element("div", {"styles": {"height": "100%", "overflow": "auto"}});

        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.areaNode, {"distance": 100});
        //}.bind(this));


        this.propertyListNode = this.designer.propertyDomArea;
        //this.propertyNode = this.designer.propertyContentArea;

        if(this.designer.application) this.data.applicationName = this.designer.application.name;
        if(this.designer.application) this.data.application = this.designer.application.id;

        this.isNewView = (this.data.id) ? false : true;

        this.items = [];
        this.view = this;

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
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
            this.property = new MWF.xApplication.query.ViewDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
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

                this.designer.actions.loadView(this.data.id, null,function(json){
                    var entries = {};
                    json.data.selectList.each(function(entry){entries[entry.column] = entry;}.bind(this));

                    if (this.json.data.group.column){
                        if (json.data.groupGrid.length){
                            var groupColumn = null;
                            for (var c = 0; c<json.data.selectList.length; c++){
                                if (json.data.selectList[c].column === json.data.group.column){
                                    groupColumn = json.data.selectList[c];
                                    break;
                                }
                            }

                            json.data.groupGrid.each(function(line, idx){
                                var groupTr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                                var colSpan = this.items.length;
                                var td = new Element("td", {"styles": this.css.viewContentGroupTdNode, "colSpan": colSpan}).inject(groupTr);
                                var groupAreaNode = new Element("div", {"styles": this.css.viewContentTdGroupNode}).inject(td);
                                var groupIconNode = new Element("div", {"styles": this.css.viewContentTdGroupIconNode}).inject(groupAreaNode);
                                var groupTextNode = new Element("div", {"styles": this.css.viewContentTdGroupTextNode}).inject(groupAreaNode);
                                if (groupColumn){
                                    //groupTextNode.set("text", (groupColumn.code) ? MWF.Macro.exec(groupColumn.code, {"value": line.group, "gridData": json.data.groupGrid, "data": json.data, "entry": line}) : line.group);
                                    groupTextNode.set("text", line.group);
                                }else{
                                    groupTextNode.set("text", line.group);
                                }


                                var subtrs = [];

                                line.list.each(function(entry){
                                    var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                                    tr.setStyle("display", "none");
                                    var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);

                                    Object.each(entries, function(c, k){
                                        var d = entry.data[k];
                                        if (d!=undefined){
                                            if (k!=this.json.data.group.column){
                                                var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                                //td.set("text", (entries[k].code) ? MWF.Macro.exec(entries[k].code, {"value": d, "gridData": json.data.groupGrid, "data": json.data, "entry": entry}) : d);

                                                if (c.isHtml){
                                                    td.set("html", d);
                                                }else{
                                                    td.set("text", d);
                                                }

                                            }
                                        }
                                    }.bind(this));

                                    // Object.each(entry.data, function(d, k){
                                    //     if (k!=this.json.data.group.column){
                                    //         var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                    //         td.set("text", (entries[k].code) ? MWF.Macro.exec(entries[k].code, {"value": d, "gridData": json.data.groupGrid, "data": json.data, "entry": entry}) : d);
                                    //     }
                                    // }.bind(this));
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
                                            iconNode.setStyle("background", "url("+"../x_component_query_ViewDesigner/$View/default/icon/down.png) center center no-repeat");
                                        }else{
                                            subtrs.each(function(subtr){ subtr.setStyle("display", "none"); });
                                            iconNode.setStyle("background", "url("+"../x_component_query_ViewDesigner/$View/default/icon/right.png) center center no-repeat");
                                        }
                                    }
                                    _self.setContentHeight();
                                });
                            }.bind(this));
                            this.setContentColumnWidth();
                            this.setContentHeight();
                        }

                    }else{

                        if (json.data.grid.length){
                            json.data.grid.each(function(line, idx){
                                var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);

                                Object.each(entries, function(c, k){
                                    var d = line.data[k];
                                    if (d!=undefined){
                                        var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                        //td.set("text", (entries[k].code) ? MWF.Macro.exec(entries[k].code, {"value": d, "gridData": json.data.grid, "data": json.data, "entry": line}) : d);
                                        if (c.isHtml){
                                            td.set("html", d);
                                        }else{
                                            td.set("text", d);
                                        }
                                        //td.set("text", d);
                                    }
                                }.bind(this));

                                // Object.each(line.data, function(d, k){
                                //     var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                                //     td.set("text", (entries[k].code) ? MWF.Macro.exec(entries[k].code, {"value": d, "gridData": json.data.grid, "data": json.data, "entry": line}) : d);
                                // }.bind(this));
                            }.bind(this));
                            this.setContentColumnWidth();
                            this.setContentHeight();
                        }
                    }
                }.bind(this));

                //this.getLookupAction(function(){
                //    this.lookupAction.invoke({"name": "lookup","async": true, "parameter": {"id": this.data.id},"success": function(json){
                //        if (json.data.length){
                //            json.data.each(function(line, idx){
                //                var tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.viewContentTableNode);
                //                line.each(function(cell, i){
                //                    var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr);
                //                    td.set("text", cell);
                //                }.bind(this));
                //            }.bind(this));
                //            this.setContentColumnWidth();
                //            this.setContentHeight();
                //        }
                //    }.bind(this)});
                //}.bind(this));
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


    //getLookupAction: function(callback){
    //    if (!this.lookupAction){
    //        MWF.require("MWF.xDesktop.Actions.RestActions", function(){
    //            this.lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface_lookup", "");
    //            this.lookupAction.getActions = function(actionCallback){
    //
    //                this.actions = {"lookup": {"uri": "/jaxrs/view/{id}"}};
    //                if (actionCallback) actionCallback();
    //            }
    //            if (callback) callback();
    //        }.bind(this));
    //    }else{
    //        if (callback) callback();
    //    }
    //},
    addColumn: function(){

        MWF.require("MWF.widget.UUID", function(){
            var id = (new MWF.widget.UUID).id;
            var json = {
                "id": id,
                "column": id,
                "displayName": this.designer.lp.unnamed,
                "orderType": "original"
            };
            if (!this.json.data.selectList) this.json.data.selectList = [];
            this.json.data.selectList.push(json);
            var column = new MWF.xApplication.query.ViewDesigner.ViewBase.Column(json, this);
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
        if (this.json.data.selectList) {
            this.json.data.selectList.each(function (json) {
                this.items.push(new MWF.xApplication.query.ViewDesigner.ViewBase.Column(json, this));

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

        this.designer.actions.saveView(this.data, function(json){
            this.data.id = json.data.id;
            //this.page.textNode.set("text", this.data.name);
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
        //}
        this.designer.actions.saveView(this.data, function(json){
            this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            //this.page.textNode.set("text", this.data.name);
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            if (callback) callback();
        }.bind(this));
    },
    explode: function(){},
    implode: function(){},
    _setEditStyle: function(name, input, oldValue){
        if (name=="type"){
            this.items.each(function(item){
                if (item.property){
                    var processDiv = item.property.propertyContent.getElements("#"+item.json.id+"dataPathSelectedProcessArea");
                    var cmsDiv = item.property.propertyContent.getElements("#"+item.json.id+"dataPathSelectedCMSArea");
                    if (this.json[name]=="cms"){
                        cmsDiv.setStyle("display", "block");
                        processDiv.setStyle("display", "none");
                    }else{
                        cmsDiv.setStyle("display", "none");
                        processDiv.setStyle("display", "block");
                    }
                }
            }.bind(this));
        }
    },

    saveAs: function(){
        var form = new MWF.xApplication.query.ViewDesigner.ViewBase.NewNameForm(this, {
            name : this.data.name + "_" + MWF.xApplication.query.ViewDesigner.LP.copy,
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

        var d = this.cloneObject( this.data );

        d.isNewView = true;
        d.id = this.designer.actions.getUUID();
        d.name = data.name;
        d.alias = "";
        d.query = data.query;
        d.queryName = data.queryName;
        d.application = data.query;
        d.applicationName = data.queryName;
        d.pid = d.id + d.id;

        delete d[this.data.id+"viewFilterType"];
        d[d.id+"viewFilterType"]="custom";

        d.data.selectList.each( function( entry ){
            entry.id = (new MWF.widget.UUID).id;
        }.bind(this));

        this.designer.actions.saveView(d, function(json){
            this.designer.notice(this.designer.lp.notice.saveAs_success, "success", this.node, {"x": "left", "y": "bottom"});
            if (callback) callback();
        }.bind(this));
    }

});


MWF.xApplication.query.ViewDesigner.ViewBase.Column = new Class({
	initialize: function(json, view, next){
        this.propertyPath = "../x_component_query_ViewDesigner/$View/column.html";
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
            this.property = new MWF.xApplication.query.ViewDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
                "path": this.propertyPath,
                "onPostLoad": function(){
                    this.property.show();

                    var processDiv = this.property.propertyContent.getElements("#"+this.json.id+"dataPathSelectedProcessArea");
                    var cmsDiv = this.property.propertyContent.getElements("#"+this.json.id+"dataPathSelectedCMSArea");

                    if (this.view.json.type=="cms"){
                        processDiv.setStyle("display", "none");
                        cmsDiv.setStyle("display", "block");
                    }else{
                        processDiv.setStyle("display", "block");
                        cmsDiv.setStyle("display", "none");
                    }
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
        if (name=="displayName") this.resetTextNode();
        if (name=="selectType") this.resetTextNode();
        if (name=="attribute") this.resetTextNode();
        if (name=="path") this.resetTextNode();
        if (name=="column"){
            this.view.json.data.orderList.each(function(order){
                if (order.column==oldValue) order.column = this.json.column
            }.bind(this));
            if (this.view.json.data.group.column == oldValue) this.view.json.data.group.column = this.json.column;
        }
    },
    resetTextNode: function(){
        var listText = (this.json.selectType=="attribute") ? (this.json.attribute || "") : (this.json.path || "");
        if (!listText) listText = "unnamed";

        this.textNode.set("text", this.json.displayName);
        this.listNode.getLast().set("text", this.json.displayName+"("+listText+")");
    },
    "delete": function(e){
        var _self = this;
        if (!e) e = this.node;
        this.view.designer.confirm("warn", e, MWF.APPDVD.LP.notice.deleteColumnTitle, MWF.APPDVD.LP.notice.deleteColumn, 300, 120, function(){
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
                    "column": id,
                    "displayName": this.view.designer.lp.unnamed,
                    "orderType": "original"
                };
            }

            var idx = this.view.json.data.selectList.indexOf(this.json);
            this.view.json.data.selectList.splice(idx, 0, json);

            var column = new MWF.xApplication.query.ViewDesigner.ViewBase.Column(json, this.view, this);
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
                    // var idx = this.view.json.data.selectList.indexOf(column.json);

                    this.view.json.data.selectList.erase(this.json);
                    this.view.items.erase(this);

                    var idx = this.view.json.data.selectList.indexOf(column.json);

                    this.view.json.data.selectList.splice(idx, 0, this.json);
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
    }
    //hideMode: function(){
    //    if (!this.columnHideFlagNode){
    //        this.columnHideFlagNode = new Element("div", {"styles": this.view.css.columnHideFlagNode}).inject(this.node);
    //    }
    //},
    //showMode: function(){
    //    if (this.columnHideFlagNode) this.columnHideFlagNode.destroy();
    //    this.columnHideFlagNode = null;
    //}

});


