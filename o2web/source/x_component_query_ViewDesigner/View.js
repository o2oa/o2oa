MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.APPDVD = MWF.xApplication.query.ViewDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);

MWF.xDesktop.requireApp("query.ViewDesigner", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("query.ViewDesigner", "Property", null, false);

MWF.xApplication.query.ViewDesigner.View = new Class({
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

        this.isNewView = (this.data.name) ? false : true;

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
        if( !this.json.data.events ){
            var url = "../x_component_query_ViewDesigner/$View/view.json";
            MWF.getJSON(url, {
                "onSuccess": function(obj){
                    this.json.data.events = obj.data.events;
                }.bind(this),
                "onerror": function(text){
                    this.notice(text, "error");
                }.bind(this),
                "onRequestFailure": function(xhr){
                    this.notice(xhr.responseText, "error");
                }.bind(this)
            },false);
        }
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

        this.loadTemplateStyle( function () {

            this.loadActionbar();

            this.loadView();

            this.loadPaging();

            this.selected();
            this.setEvent();

            //if (this.options.showTab) this.page.showTabIm();
            this.setViewWidth();

            this.designer.addEvent("resize", this.setViewWidth.bind(this));

            this.fireEvent("postLoad");

        }.bind(this))
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
                                var groupTr = new Element("tr", {
                                    "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTr"] : this.css.viewContentTrNode,
                                    "data-is-group" : "yes"
                                }).inject(this.viewContentTableNode);
                                var colSpan = this.items.length ;
                                var td = new Element("td", {
                                    "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentGroupTd"] : this.css.viewContentGroupTdNode,
                                    "colSpan": colSpan
                                }).inject(groupTr);

                                var groupAreaNode;
                                if( this.json.data.viewStyles ){
                                    groupAreaNode = new Element("div", {"styles": this.json.data.viewStyles["groupCollapseNode"]}).inject(td);
                                    groupAreaNode.set("text", line.group);
                                }else{
                                    groupAreaNode = new Element("div", {"styles": this.css.viewContentTdGroupNode}).inject(td);
                                    var groupIconNode = new Element("div", {"styles": this.css.viewContentTdGroupIconNode}).inject(groupAreaNode);
                                    var groupTextNode = new Element("div", {"styles": this.css.viewContentTdGroupTextNode}).inject(groupAreaNode);
                                    if (groupColumn){
                                        //groupTextNode.set("text", (groupColumn.code) ? MWF.Macro.exec(groupColumn.code, {"value": line.group, "gridData": json.data.groupGrid, "data": json.data, "entry": line}) : line.group);
                                        groupTextNode.set("text", line.group);
                                    }else{
                                        groupTextNode.set("text", line.group);
                                    }

                                }



                                var subtrs = [];

                                line.list.each(function(entry){
                                    var tr = new Element("tr", {
                                        "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTr"] : this.css.viewContentTrNode
                                    }).inject(this.viewContentTableNode);
                                    tr.setStyle("display", "none");

                                    //this.createViewCheckboxTd( tr );

                                    var td = new Element("td", {
                                        "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTd"] : this.css.viewContentTdNode
                                    }).inject(tr);

                                    Object.each(entries, function(c, k){
                                        var d = entry.data[k];
                                        if (d!=undefined){
                                            if (k!=this.json.data.group.column){
                                                var td = new Element("td", {
                                                    "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTd"] : this.css.viewContentTdNode
                                                }).inject(tr);
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
                                            if( iconNode ) {
                                                iconNode.setStyle("background", "url(" + "../x_component_query_ViewDesigner/$View/default/icon/down.png) center center no-repeat");
                                            }else{
                                                this.setStyles( _self.json.data.viewStyles["groupExpandNode"] )
                                            }
                                        }else{
                                            subtrs.each(function(subtr){ subtr.setStyle("display", "none"); });
                                            if( iconNode ) {
                                                iconNode.setStyle("background", "url(" + "../x_component_query_ViewDesigner/$View/default/icon/right.png) center center no-repeat");
                                            }else{
                                                this.setStyles( _self.json.data.viewStyles["groupCollapseNode"] )
                                            }
                                        }
                                    }
                                    _self.setContentHeight();
                                });
                            }.bind(this));
                            this.setContentColumnWidth();
                            this.setContentHeight();
                        }else if(this.json.data.noDataText){
                            var noDataTextNodeStyle = this.css.noDataTextNode;
                            if( this.json.data.viewStyles ){
                                if( this.json.data.viewStyles["noDataTextNode"] ){
                                    noDataTextNodeStyle = this.json.data.viewStyles["noDataTextNode"]
                                }else{
                                     this.json.data.viewStyles["noDataTextNode"] = this.css.noDataTextNode
                                }
                            }
                            this.noDataTextNode = new Element( "div", {
                                "styles": noDataTextNodeStyle,
                                "text" : this.json.data.noDataText
                            }).inject( this.viewContentBodyNode );
                        }

                    }else{

                        if (json.data.grid.length){
                            json.data.grid.each(function(line, idx){
                                var tr = new Element("tr", {
                                    "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTr"] : this.css.viewContentTrNode
                                }).inject(this.viewContentTableNode);

                                //this.createViewCheckboxTd( tr );

                                Object.each(entries, function(c, k){
                                    var d = line.data[k];
                                    if (d!=undefined){
                                        var td = new Element("td", {
                                            "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTd"] : this.css.viewContentTdNode
                                        }).inject(tr);
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
                        }else if(this.json.data.noDataText){
                            var noDataTextNodeStyle = this.css.noDataTextNode;
                            if( this.json.data.viewStyles ){
                                if( this.json.data.viewStyles["noDataTextNode"] ){
                                    noDataTextNodeStyle = this.json.data.viewStyles["noDataTextNode"]
                                }else{
                                    this.json.data.viewStyles["noDataTextNode"] = this.css.noDataTextNode
                                }
                            }
                            this.noDataTextNode = new Element( "div", {
                                "styles": noDataTextNodeStyle,
                                "text" : this.json.data.noDataText
                            }).inject( this.viewContentBodyNode );
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

        debugger;

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
            var column = new MWF.xApplication.query.ViewDesigner.View.Column(json, this);
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
        var actionbarSize = this.actionbarNode ? this.actionbarNode.getSize() : {x:0, y:0};
        var pagingSize = this.pagingNode ? this.pagingNode.getSize() : {x:0, y:0};
        var height = size.y-titleSize.y-actionbarSize.y-pagingSize.y-2;

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
                this.items.push(new MWF.xApplication.query.ViewDesigner.View.Column(json, this));

            }.bind(this));
        }
    //    }
    },
    loadViewSelectAllNode : function(){
        var _self = this;
        var td = new Element("td.viewTitleCheckboxTd",{ "styles": this.css.viewTitleColumnAreaNode }).inject( this.viewTitleTrNode );
        td.setStyles({
            "width":"30px", "text-align" : "center",
            "display" : this.json.data.selectAllEnable ? "table-cell" : "none"
        });
        new Element("input",{
            "type" : "checkbox",
            "events" : {
                "change" : function(){
                    _self.viewContentTableNode.getElements(".viewContentCheckbox").set("checked", this.checked )
                }
            }
        }).inject(td);
    },
    createViewCheckboxTd : function( tr ){
        var td = new Element("td.viewContentCheckboxTd", {"styles": this.css.viewContentTdNode}).inject(tr, "top");
        td.setStyles({
            "width":"30px", "text-align" : "center",
            "display" : this.json.data.selectAllEnable ? "table-cell" : "none"
        });
        new Element("input.viewContentCheckbox",{
            "type" : "checkbox"
        }).inject(td);
    },
    loadView: function(){
        this.loadViewNodes();
        //this.loadViewSelectAllNode();
        this.loadViewColumns();
//        this.addTopItemNode.addEvent("click", this.addTopItem.bind(this));
    },
    loadActionbar: function(){
        this.actionbarNode = new Element("div#actionbarNode", {"styles": this.css.actionbarNode}).inject(this.areaNode);
        this.actionbarList = [];
        if( !this.json.data.actionbarHidden ){
            this.showActionbar( true );
        }
    },
    hideActionbar : function(){
        this.actionbarNode.hide();
        this.setContentHeight();
    },
    showActionbar : function( noSetHeight ){
        this.actionbarNode.show();
        if( !this.json.data.actionbarList )this.json.data.actionbarList = [];
        if( !this.actionbarList || this.actionbarList.length == 0 ){
            if( this.json.data.actionbarList.length ){
                this.json.data.actionbarList.each( function(json){
                    this.actionbarList.push( new MWF.xApplication.query.ViewDesigner.View.Actionbar( json, this.json.data.actionbarList, this) )
                }.bind(this));
            }else{
                this.actionbarList.push( new MWF.xApplication.query.ViewDesigner.View.Actionbar( null, this.json.data.actionbarList, this) )
            }
        }
        if( !noSetHeight )this.setContentHeight();
    },
    loadPaging: function( noSetHeight ){
        this.pagingNode = new Element("div#pagingNode", {"styles": this.css.pagingNode}).inject(this.areaNode);
        this.pagingList = [];
        if( !this.json.data.pagingList )this.json.data.pagingList = [];
        if( !this.pagingList || this.pagingList.length == 0 ){
            if( this.json.data.pagingList.length ){
                this.json.data.pagingList.each( function(json){
                    this.pagingList.push( new MWF.xApplication.query.ViewDesigner.View.Paging( json, this.json.data.pagingList, this) )
                }.bind(this));
            }else{
                this.pagingList.push( new MWF.xApplication.query.ViewDesigner.View.Paging( null, this.json.data.pagingList, this) )
            }
        }
        // if( !noSetHeight )this.setContentHeight();
    },
    setViewWidth: function(){
        if( !this.viewAreaNode )return;
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

    preview: function(){
        if( this.isNewView ){
            this.designer.notice( this.designer.lp.saveViewNotice, "error" );
            return;
        }
        this.saveSilence( function () {
            var url = "../x_desktop/app.html?app=query.Query&status=";
            url += JSON.stringify({
                id : this.data.application,
                viewId : this.data.id
            });
            window.open(o2.filterUrl(url),"_blank");
        }.bind(this));
    },
    saveSilence: function(callback){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.notice.inputName, "error");
            return false;
        }

        if( !this.data.data && !this.data.data.where ){
            if( this.data.type === "cms" ){
                this.designer.notice(this.designer.lp.notice.selectCMS, "error");
                return false;
            }else{
                this.designer.notice(this.designer.lp.notice.selectProcess, "error");
                return false;
            }
        }else{
            var where = this.data.data.where;
            if( this.data.type === "cms" ){
                var appInfoList = where.appInfoList;
                var categoryInfoList = where.categoryInfoList;
                if( (!appInfoList || !appInfoList.length) && (!categoryInfoList || !categoryInfoList.length) ){
                    this.designer.notice(this.designer.lp.notice.selectCMS, "error");
                    return false;
                }
            }else{
                var applicationList = where.applicationList;
                var processList = where.processList;
                if( (!applicationList || !applicationList.length) && (!processList || !processList.length) ){
                    this.designer.notice(this.designer.lp.notice.selectProcess, "error");
                    return false;
                }
            }
            if( where.dateRange && where.dateRange.dateRangeType === "range" ){
                if( !where.dateRange.start || !where.dateRange.completed ){
                    this.designer.notice(this.designer.lp.notice.selectDateRange, "error");
                    return false;
                }
            }
        }

        // var list;
        // if( this.data.data && this.data.data.where ){
        //     if( this.data.data.where.creatorIdentityList ){
        //         list = this.data.data.where.creatorIdentityList;
        //         for( var i=0; i< list.length ; i++){
        //             if( typeOf( list[i] ) === "object" )list[i] = list[i].name || "";
        //         }
        //     }
        //     if( this.data.data.where.creatorPersonList ){
        //         list = this.data.data.where.creatorPersonList;
        //         for( var i=0; i< list.length ; i++){
        //             if( typeOf( list[i] ) === "object" )list[i] = list[i].name || "";
        //         }
        //     }
        //     if( this.data.data.where.creatorUnitList ){
        //         list = this.data.data.where.creatorIdentityList;
        //         for( var i=0; i< list.length ; i++){
        //             if( typeOf( list[i] ) === "object" )list[i] = list[i].name || "";
        //         }
        //     }
        // }

        this.designer.actions.saveView(this.data, function(json){
            this.data.id = json.data.id;
            this.isNewView = false;
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

        if( !this.data.data && !this.data.data.where ){
            if( this.data.type === "cms" ){
                this.designer.notice(this.designer.lp.notice.selectCMS, "error");
                return false;
            }else{
                this.designer.notice(this.designer.lp.notice.selectProcess, "error");
                return false;
            }
        }else{
            var where = this.data.data.where;
            if( this.data.type === "cms" ){
                var appInfoList = where.appInfoList;
                var categoryInfoList = where.categoryInfoList;
                if( (!appInfoList || !appInfoList.length) && (!categoryInfoList || !categoryInfoList.length) ){
                    this.designer.notice(this.designer.lp.notice.selectCMS, "error");
                    return false;
                }
            }else{
                var applicationList = where.applicationList;
                var processList = where.processList;
                if( (!applicationList || !applicationList.length) && (!processList || !processList.length) ){
                    this.designer.notice(this.designer.lp.notice.selectProcess, "error");
                    return false;
                }
            }
            if( where.dateRange && where.dateRange.dateRangeType === "range" ){
                if( !where.dateRange.start || !where.dateRange.completed ){
                    this.designer.notice(this.designer.lp.notice.selectDateRange, "error");
                    return false;
                }
            }
        }

        debugger;
            // var list;
            // if( this.data.data && this.data.data.where ){
            //     if( this.data.data.where.creatorIdentityList ){
            //         list = this.data.data.where.creatorIdentityList;
            //         for( var i=0; i< list.length ; i++){
            //             if( typeOf( list[i] ) === "object" )list[i] = list[i].name || "";
            //         }
            //     }
            //     if( this.data.data.where.creatorPersonList ){
            //         list = this.data.data.where.creatorPersonList;
            //         for( var i=0; i< list.length ; i++){
            //             if( typeOf( list[i] ) === "object" )list[i] = list[i].name || "";
            //         }
            //     }
            //     if( this.data.data.where.creatorUnitList ){
            //         list = this.data.data.where.creatorUnitList;
            //         for( var i=0; i< list.length ; i++){
            //             if( typeOf( list[i] ) === "object" )list[i] = list[i].name || "";
            //         }
            //     }
            // }

        this.designer.actions.saveView(this.data, function(json){
            this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
            this.isNewView = false;
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
        if( name=="data.actionbarHidden" ){
            if( this.json.data.actionbarHidden ){
                this.hideActionbar()
            }else{
                this.showActionbar()
            }
        }
        if( name=="data.selectAllEnable" ){
            if( this.json.data.selectAllEnable ){
                this.viewTitleTrNode.getElement(".viewTitleCheckboxTd").setStyle("display","table-cell");
                this.viewContentTableNode.getElements(".viewContentCheckboxTd").setStyle("display","table-cell");
            }else{
                this.viewTitleTrNode.getElement(".viewTitleCheckboxTd").setStyle("display","none");
                this.viewContentTableNode.getElements(".viewContentCheckboxTd").setStyle("display","none");
            }
        }
        if (name=="data.viewStyleType"){

            var file = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType].file : null;
            var extendFile = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType].extendFile : null;
            this.loadTemplateStyles( file, extendFile, function( templateStyles ){
                this.templateStyles = templateStyles;

                var oldFile, oldExtendFile;
                if( oldValue && this.stylesList[oldValue] ){
                    oldFile = this.stylesList[oldValue].file;
                    oldExtendFile = this.stylesList[oldValue].extendFile;
                }
                this.loadTemplateStyles( oldFile, oldExtendFile, function( oldTemplateStyles ){

                    this.json.data.styleConfig = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType] : null;

                    if (oldTemplateStyles["view"]) this.clearTemplateStyles(oldTemplateStyles["view"]);
                    if (this.templateStyles["view"]) this.setTemplateStyles(this.templateStyles["view"]);
                    this.setAllStyles();

                    this.actionbarList.each( function (module) {
                            if (oldTemplateStyles["actionbar"]){
                                module.clearTemplateStyles(oldTemplateStyles["actionbar"]);
                            }
                            module.setStyleTemplate();
                            module.setAllStyles();
                    })

                    this.pagingList.each( function (module) {
                        if (oldTemplateStyles["paging"]){
                            module.clearTemplateStyles(oldTemplateStyles["paging"]);
                        }
                        module.setStyleTemplate();
                        module.setAllStyles();
                    });

                    // this.moduleList.each(function(module){
                    //     if (oldTemplateStyles[module.moduleName]){
                    //         module.clearTemplateStyles(oldTemplateStyles[module.moduleName]);
                    //     }
                    //     module.setStyleTemplate();
                    //     module.setAllStyles();
                    // }.bind(this));
                }.bind(this))

            }.bind(this))
        }
        if (name=="data.viewStyles"){
            this.setCustomStyles();
        }
    },
    setAllStyles: function(){
        // this.setPropertiesOrStyles("styles");
        // this.setPropertiesOrStyles("properties");
        this.setCustomStyles();
        this.reloadMaplist();
    },
    reloadMaplist: function(){
        if (this.property) Object.each(this.property.maplists, function(map, name){ map.reload(this.json[name]);}.bind(this));
    },
    // setPropertiesOrStyles: function(name){
    //     if (name=="styles"){
    //         this.setCustomStyles();
    //     }
    //     if (name=="properties"){
    //         this.node.setProperties(this.json.properties);
    //     }
    // },
    setCustomStyles: function(){
        this.items.each( function( item ){
            item.setCustomStyles()
        }.bind(this));
        // var border = this.node.getStyle("border");
        // this.node.clearStyles();
        // this.node.setStyles((this.options.mode==="Mobile") ? this.css.formMobileNode : this.css.formNode);
        // var y = this.container.getStyle("height");
        // y = (y) ? y.toInt()-2 : this.container.getSize().y-2;
        // this.node.setStyle("min-height", ""+y+"px");
        //
        // if (this.initialStyles) this.node.setStyles(this.initialStyles);
        // this.node.setStyle("border", border);
        //
        // Object.each(this.json.styles, function(value, key){
        //     var reg = /^border\w*/ig;
        //     if (!key.test(reg)){
        //         this.node.setStyle(key, value);
        //     }
        // }.bind(this));
    },

    loadTemplateStyle : function( callback ){
        this.loadStylesList(function(){
            var oldStyleValue = "";
            if ((!this.json.data.viewStyleType) || !this.stylesList[this.json.data.viewStyleType]) this.json.data.viewStyleType="default";
            // if (this.options.mode=="Mobile"){
            //     if (this.json.viewStyleType != "defaultMobile"){
            //         var styles = this.stylesList[this.json.viewStyleType];
            //         if( !styles || typeOf(styles.mode)!=="array" || !styles.mode.contains( "mobile" ) ){
            //             oldStyleValue = this.json.viewStyleType;
            //             this.json.viewStyleType = "defaultMobile";
            //         }
            //     }
            // }

            this.loadTemplateStyles( this.stylesList[this.json.data.viewStyleType].file, this.stylesList[this.json.data.viewStyleType].extendFile,
                function( templateStyles ){
                    this.templateStyles = templateStyles;

                    // this.loadDomModules();
                    if( !this.json.data.viewStyleType )this.json.data.viewStyleType = "default";

                    if ( this.templateStyles && this.templateStyles["view"]){
                        if(!this.json.data.viewStyles){
                            this.json.data.viewStyles = Object.clone(this.templateStyles["view"]);
                        }else{
                            this.setTemplateStyles(this.templateStyles["view"]);
                        }
                    }

                    this.setCustomStyles();
                    // this.node.setProperties(this.json.data.properties);

                    if(callback)callback();

                    // this.setNodeEvents();

                    // if (this.options.mode=="Mobile"){
                    //     if (oldStyleValue) this._setEditStyle("viewStyleType", null, oldStyleValue);
                    // }
                }.bind(this)
            );
        }.bind(this));
    },
    removeStyles: function(from, to){
        if (this.json.data.viewStyles[to]){
            Object.each(from, function(style, key){
                if (this.json.data.viewStyles[to][key] && this.json.data.viewStyles[to][key]==style){
                    delete this.json.data.viewStyles[to][key];
                }
            }.bind(this));
        }
    },
    copyStyles: function(from, to){
        if (!this.json.data.viewStyles[to]) this.json.data.viewStyles[to] = {};
        Object.each(from, function(style, key){
            if (!this.json.data.viewStyles[to][key]) this.json.data.viewStyles[to][key] = style;
        }.bind(this));
    },
    // clearTemplateStyles: function(styles){
    //     if (styles){
    //         if (styles.styles) this.removeStyles(styles.styles, "styles");
    //         if (styles.properties) this.removeStyles(styles.properties, "properties");
    //     }
    // },
    // setTemplateStyles: function(styles){
    //     if (styles.styles) this.copyStyles(styles.styles, "styles");
    //     if (styles.properties) this.copyStyles(styles.properties, "properties");
    // },
    clearTemplateStyles: function(styles){
        if (styles){
            if (styles.container) this.removeStyles(styles.container, "container");
            if (styles.table) this.removeStyles(styles.table, "table");
            if (styles.titleTr) this.removeStyles(styles.titleTr, "titleTr");
            if (styles.titleTd) this.removeStyles(styles.titleTd, "titleTd");
            if (styles.contentTr) this.removeStyles(styles.contentTr, "contentTr");
            if (styles.contentSelectedTr) this.removeStyles(styles.contentSelectedTr, "contentSelectedTr");
            if (styles.contentTd) this.removeStyles(styles.contentTd, "contentTd");
            if (styles.contentGroupTd) this.removeStyles(styles.contentGroupTd, "contentGroupTd");
            if (styles.groupCollapseNode) this.removeStyles(styles.groupCollapseNode, "groupCollapseNode");
            if (styles.groupExpandNode) this.removeStyles(styles.groupExpandNode, "groupExpandNode");
            if (styles.checkboxNode) this.removeStyles(styles.checkboxNode, "checkboxNode");
            if (styles.checkedCheckboxNode) this.removeStyles(styles.checkedCheckboxNode, "checkedCheckboxNode");
            if (styles.radioNode) this.removeStyles(styles.radioNode, "radioNode");
            if (styles.checkedRadioNode) this.removeStyles(styles.checkedRadioNode, "checkedRadioNode");
            if (styles.tableProperties) this.removeStyles(styles.tableProperties, "tableProperties");
        }
    },

    setTemplateStyles: function(styles){
        if (styles.container) this.copyStyles(styles.container, "container");
        if (styles.table) this.copyStyles(styles.table, "table");
        if (styles.titleTr) this.copyStyles(styles.titleTr, "titleTr");
        if (styles.titleTd) this.copyStyles(styles.titleTd, "titleTd");
        if (styles.contentTr) this.copyStyles(styles.contentTr, "contentTr");
        if (styles.contentSelectedTr) this.copyStyles(styles.contentSelectedTr, "contentSelectedTr");
        if (styles.contentTd) this.copyStyles(styles.contentTd, "contentTd");
        if (styles.contentGroupTd) this.copyStyles(styles.contentGroupTd, "contentGroupTd");
        if (styles.groupCollapseNode) this.copyStyles(styles.groupCollapseNode, "groupCollapseNode");
        if (styles.groupExpandNode) this.copyStyles(styles.groupExpandNode, "groupExpandNode");
        if (styles.checkboxNode) this.copyStyles(styles.checkboxNode, "checkboxNode");
        if (styles.checkedCheckboxNode) this.copyStyles(styles.checkedCheckboxNode, "checkedCheckboxNode");
        if (styles.radioNode) this.copyStyles(styles.radioNode, "radioNode");
        if (styles.checkedRadioNode) this.copyStyles(styles.checkedRadioNode, "checkedRadioNode");
        if (styles.tableProperties) this.copyStyles(styles.tableProperties, "tableProperties");
    },

    loadTemplateStyles : function( file, extendFile, callback ){
        if( !file ){
            if (callback) callback({});
            return;
        }
        this.templateStylesList = this.templateStylesList || {};
        if( this.templateStylesList[file] ){
            if (callback) callback(this.templateStylesList[file]);
            return;
        }
        this.loadTemplateStyleFile( file, function( json_file ){
            this.loadTemplateExtendStyleFile( extendFile, function( json_extend ){
                this.templateStylesList[file] = Object.merge( json_file, json_extend );
                if (callback) callback(this.templateStylesList[file]);
            }.bind(this))
        }.bind(this))

    },
    loadTemplateStyleFile : function(file, callback ){
        if( !file ){
            if (callback) callback({});
            return;
        }
        var stylesUrl = "../x_component_query_ViewDesigner/$View/skin/"+file;
        MWF.getJSON(stylesUrl,{
                "onSuccess": function(responseJSON){
                    //this.templateStylesList[file] = responseJSON;
                    if (callback) callback(responseJSON);
                }.bind(this),
                "onRequestFailure": function(){
                    if (callback) callback({});
                }.bind(this),
                "onError": function(){
                    if (callback) callback({});
                }.bind(this)
            }
        );
    },
    loadTemplateExtendStyleFile : function(extendFile, callback ){
        if( !extendFile ){
            if (callback) callback({});
            return;
        }
        var stylesUrl = "../x_component_query_ViewDesigner/$View/skin/"+extendFile;
        MWF.getJSON(stylesUrl,{
                "onSuccess": function(responseJSON){
                    //this.templateStylesList[file] = responseJSON;
                    if (callback) callback(responseJSON);
                }.bind(this),
                "onRequestFailure": function(){
                    if (callback) callback({});
                }.bind(this),
                "onError": function(){
                    if (callback) callback({});
                }.bind(this)
            }
        );
    },
    loadStylesList: function(callback){
        var configUrl = "../x_component_query_ViewDesigner/$View/skin/config.json";
        MWF.getJSON(configUrl,{
                "onSuccess": function(responseJSON){
                    this.stylesList = responseJSON;
                    if (callback) callback(this.stylesList);
                }.bind(this),
                "onRequestFailure": function(){
                    this.stylesList = {};
                    if (callback) callback(this.stylesList);
                }.bind(this),
                "onError": function(){
                    this.stylesList = {};
                    if (callback) callback(this.stylesList);
                }.bind(this)
            }
        );
    },

    saveAs: function(){
        var form = new MWF.xApplication.query.ViewDesigner.View.NewNameForm(this, {
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

MWF.xApplication.query.ViewDesigner.View.$Module = MWF.QV$Module = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    copyStyles: function(from, to){
        if (!this.json[to]) this.json[to] = {};
        Object.each(from, function(style, key){
            //if (!this.json[to][key])
            this.json[to][key] = style;
        }.bind(this));
    },
    removeStyles: function(from, to){
        if (this.json[to]){
            Object.each(from, function(style, key){
                if (this.json[to][key] && this.json[to][key]==style){
                    delete this.json[to][key];
                }
                //if (this.json[from][key]){
                //   delete this.json[to][key];
                //}
            }.bind(this));
        }
    },
    setTemplateStyles: function(styles){
        if (styles.styles) this.copyStyles(styles.styles, "styles");
        if (styles.properties) this.copyStyles(styles.properties, "properties");
    },
    clearTemplateStyles: function(styles){
        if (styles){
            if (styles.styles) this.removeStyles(styles.styles, "styles");
            if (styles.properties) this.removeStyles(styles.properties, "properties");
        }
    },
    setStyleTemplate: function(){
        if( this.view.templateStyles && this.view.templateStyles[this.moduleName] ){
            this.setTemplateStyles(this.view.templateStyles[this.moduleName]);
        }
    },
    setAllStyles: function(){
        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("inputStyles");
        this.setPropertiesOrStyles("properties");
        this.reloadMaplist();
    },
    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.query.ViewDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
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
    hideProperty: function(){
        if (this.property) this.property.hide();
    },

    deletePropertiesOrStyles: function(name, key){
        if (name=="properties"){
            try{
                this.node.removeProperty(key);
            }catch(e){}
        }
    },
    setPropertiesOrStyles: function(name){
        if (name=="styles"){
            try{
                this.setCustomStyles();
            }catch(e){}
        }
        if (name=="properties"){
            try{
                this.node.setProperties(this.json.properties);
            }catch(e){}
        }
    },
    setCustomNodeStyles: function(node, styles){
        var border = node.getStyle("border");
        node.clearStyles();
        //node.setStyles(styles);
        node.setStyle("border", border);

        Object.each(styles, function(value, key){
            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                node.setStyle(key, value);
            }
        }.bind(this));
    },
    setCustomStyles: function(){
        var border = this.node.getStyle("border");
        this.node.clearStyles();
        this.node.setStyles(this.css.moduleNode);

        if (this.initialStyles) this.node.setStyles(this.initialStyles);
        this.node.setStyle("border", border);

        if (this.json.styles) Object.each(this.json.styles, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
                value = o2.filterUrl(value);
            }

            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                if (key){
                    if (key.toString().toLowerCase()==="display"){
                        if (value.toString().toLowerCase()==="none"){
                            this.node.setStyle("opacity", 0.3);
                        }else{
                            this.node.setStyle("opacity", 1);
                            this.node.setStyle(key, value);
                        }
                    }else{
                        this.node.setStyle(key, value);
                    }
                }
            }
            //this.node.setStyle(key, value);
        }.bind(this));
    },

    _setEditStyle: function(name, obj, oldValue){
        var title = "";
        var text = "";
        if (name==="name"){
            title = this.json.name || this.json.id;
            text = this.json.type.substr(this.json.type.lastIndexOf("$")+1, this.json.type.length);
            this.treeNode.setText("<"+text+"> "+title);
        }
        if (name==="id"){
            title = this.json.name || this.json.id;
            if (!this.json.name){
                text = this.json.type.substr(this.json.type.lastIndexOf("$")+1, this.json.type.length);
                this.treeNode.setText("<"+text+"> "+this.json.id);
            }
            this.treeNode.setTitle(this.json.id);
            this.node.set("id", this.json.id);
        }

        this._setEditStyle_custom(name, obj, oldValue);
    },

    reloadMaplist: function(){
        if (this.property) Object.each(this.property.maplists, function(map, name){ map.reload(this.json[name]);}.bind(this));
    },
    getHtml: function(){
        var copy = this.node.clone(true, true);
        copy.clearStyles(true);

        var html = copy.outerHTML;
        copy.destroy();

        return html;
    },
    getJson: function(){
        var json = Object.clone(this.json);
        var o = {};
        o[json.id] = json;
        return o;
    }
});

MWF.xApplication.query.ViewDesigner.View.Column = new Class({
    Extends: MWF.QV$Module,
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
        if( !this.json.events ){
            this.loadDefaultJson(function () {
                this._load()
            }.bind(this))
        }else{
            this._load();
        }
    },
    _load: function(){
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

        this.setCustomStyles();
    },
    loadDefaultJson: function(callback){
        if( this.view.defaultColumnJson ){
            this.json = Object.merge( this.json, Object.clone(this.view.defaultColumnJson) );
            if (callback) callback(this.json);
            return;
        }
        var url = this.view.path+"column.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.view.defaultColumnJson = Object.clone(obj);
                this.json = Object.merge( this.json, Object.clone(obj) );
                if (callback) callback(this.json);
            }.bind(this),
            "onerror": function(text){
                this.view.designer.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function(xhr){
                this.view.designer.notice(xhr.responseText, "error");
            }.bind(this)
        }, false);
    },
    setCustomStyles : function(){
        var viewStyles = this.view.json.data.viewStyles;
        var border = this.areaNode.getStyle("border");
        this.areaNode.clearStyles();
        this.areaNode.setStyles(this.css.viewTitleColumnAreaNode);
        // var y = this.container.getStyle("height");
        // y = (y) ? y.toInt()-2 : this.container.getSize().y-2;
        // this.node.setStyle("min-height", ""+y+"px");

        // if (this.initialStyles) this.node.setStyles(this.initialStyles);
        this.node.setStyle("border", border);

        Object.each(viewStyles.titleTd, function(value, key){
            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                this.node.setStyle(key, value);
            }
        }.bind(this));
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
    isOrderColumn : function(){
        var sortList = this.view.json.data.orderList || [];
        var flag = false;
        sortList.each(function(order){
            if (order.column==this.json.column)flag = true;
        }.bind(this));
        return flag;
    },
    isGroupColumn : function() {
        if (!this.view.json || !this.view.json.data || !this.view.json.data.group) return false;
        return this.view.json.data.group.column === this.json.column;
    },
    destroy: function(){
        if (this.view.currentSelectedModule==this) this.view.currentSelectedModule = null;
        if (this.actionArea) this.actionArea.destroy();
        if (this.listNode) this.listNode.destroy();
        if (this.property) this.property.propertyContent.destroy();

        var idx = this.view.items.indexOf(this);

        if (this.view.viewContentTableNode){
            var trs = this.view.viewContentTableNode.getElements("tr");
            var isGroup = this.isGroupColumn();
            trs.each(function(tr){
                if( isGroup ){
                    if( tr.get("data-is-group") === "yes" ){
                        tr.destroy()
                    }
                }else{
                    if( tr.get("data-is-group") !== "yes" ){
                        tr.deleteCell(idx);
                    }
                }
            }.bind(this));
        }

        if (this.view.json.data.group.column === this.json.column){
            this.view.json.data.group.column = null;
        }

        var sortList = this.view.json.data.orderList || [];
        var deleteItem = null;
        sortList.each(function(order){
            if (order.column==this.json.column){
                deleteItem = order;
            }
        }.bind(this));
        if (deleteItem) sortList.erase(deleteItem);

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

            var column = new MWF.xApplication.query.ViewDesigner.View.Column(json, this.view, this);
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

MWF.require("MWF.widget.Toolbar", null, false);
MWF.xApplication.query.ViewDesigner.View.Actionbar = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.$Module,
    options : {
        "style" : "default",
        "customImageStyle" : "default"
    },
    initialize: function(json, jsonList, view, options){
        this.setOptions( options );
        this.propertyPath = "../x_component_query_ViewDesigner/$View/actionbar.html";
        this.path = "../x_component_query_ViewDesigner/$View/";
        this.imagePath_default = "../x_component_query_ViewDesigner/$View/";
        this.imagePath_custom = "../x_component_process_FormDesigner/Module/Actionbar/";
        this.cssPath = "../x_component_query_ViewDesigner/$View/"+this.options.style+"/actionbar.wcss";

        this.view = view;
        this.json = json;
        this.jsonList = jsonList;
        this.css = this.view.css;
        this.container = this.view.actionbarNode;
        this.moduleName = "actionbar";
        this.load();
    },
    load: function(){
        this.systemTools = [];
        this.customTools = [];
        if( !this.json ){
            this.loadDefaultJson(function(){
                this._load()
            }.bind(this));
        }else{
            this._load()
        }
    },
    _load : function(){
        this.json.moduleName = this.moduleName;
        this._createNode();
        //this._createIconAction();
        //if (!this.json.export) this.hideMode();
        this.setEvent();
    },
    loadDefaultJson: function(callback){
        var url = this.path+"actionbar.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.view.designer.actions.getUUID(function(id){
                    obj.id=id;
                    //obj.isNewView = true;
                    //obj.application = this.view.designer.application.id;
                    this.json = obj;
                    this.jsonList.push( this.json );
                    if (callback) callback(obj);
                }.bind(this));
            }.bind(this),
            "onerror": function(text){
                this.view.designer.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function(xhr){
                this.view.designer.notice(xhr.responseText, "error");
            }.bind(this)
        });
    },
    setTemplateStyles: function(styles){
        this.json.style = styles.style;
        this.json.iconStyle = styles.iconStyle || "";
        this.json.iconOverStyle = styles.iconOverStyle || "";
        this.json.customIconStyle = styles.customIconStyle;
        this.json.customIconOverStyle = styles.customIconOverStyle || "";
        this.json.forceStyles = styles.forceStyles || "";
    },
    clearTemplateStyles: function(styles){
        this.json.style = "form";
        this.json.iconStyle = "";
        this.json.iconOverStyle = "";
        this.json.customIconStyle = "";
        this.json.customIconOverStyle = "";
        this.json.forceStyles = "";
    },
    setAllStyles: function(){
        this._resetActionbar();
    },
    setEvent: function(){
        this.node.addEvents({
            "click": function(e){this.selected(); e.stopPropagation();}.bind(this),
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.toolbarWarpNode_over)}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css.toolbarWarpNode) }.bind(this)
        });
    },
    selected: function(){
        if (this.view.currentSelectedModule){
            if (this.view.currentSelectedModule==this){
                return true;
            }else{
                this.view.currentSelectedModule.unSelected();
            }
        }
        this.node.setStyles(this.css.toolbarWarpNode_selected);
        //this.listNode.setStyles(this.css.cloumnListNode_selected);
        new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        //new Fx.Scroll(this.view.designer.propertyDomArea, {"wheelStops": false, "duration": 100}).toElement(this.listNode);

        this.view.currentSelectedModule = this;
        this.isSelected = true;
        //this._showActions();
        this.showProperty();
    },
    unSelected: function(){
        this.view.currentSelectedModule = null;
        this.node.setStyles(this.css.toolbarWarpNode)

        //this.listNode.setStyles(this.css.cloumnListNode);
        this.isSelected = false;
        //this._hideActions();
        this.hideProperty();
    },

    resetTextNode: function(){
        var listText = (this.json.selectType=="attribute") ? (this.json.attribute || "") : (this.json.path || "");
        if (!listText) listText = "unnamed";

        this.textNode.set("text", this.json.displayName);
        this.listNode.getLast().set("text", this.json.displayName+"("+listText+")");
    },
    getJsonPath : function(){
        return this.path+"toolbars.json";
    },
    _createNode: function(callback){
        this.node = new Element("div", {
            "id": this.json.id,
            "MWFType": "actionbar",
            "styles": this.css.toolbarWarpNode,
            "events": {
                "selectstart": function(e){
                    e.preventDefault();
                }
            }

        }).inject(this.container );

        this.toolbarNode = new Element("div").inject(this.node);

        this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);
        if (!this.json.actionStyles){
            this.json.actionStyles = Object.clone(this.toolbarWidget.css);
        }else{
            this.toolbarWidget.css = Object.clone(this.json.actionStyles);
        }

        this.loadMultiToolbar();

        // if (this.json.defaultTools){
        //     var json = Array.clone(this.json.defaultTools);
        //     this.setToolbars(json, this.toolbarNode);
        //     if (this.json.tools){
        //         this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
        //     }
        //     this.toolbarWidget.load();
        //     this._setEditStyle_custom("hideSystemTools");
        // }else{
        //     MWF.getJSON( this.getJsonPath(), function(json){
        //         this.json.defaultTools = json;
        //         var json = Array.clone(this.json.defaultTools);
        //         this.setToolbars(json, this.toolbarNode);
        //         if (this.json.tools){
        //             this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
        //         }
        //         this.toolbarWidget.load();
        //         this._setEditStyle_custom("hideSystemTools");
        //     }.bind(this), false);
        // }
    },

    _refreshActionbar: function(){
        //if (this.form.options.mode == "Mobile"){
        //    this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
        //    this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        //}else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();
            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);
            if (!this.json.actionStyles) this.json.actionStyles = Object.clone(this.toolbarWidget.css);
            this.toolbarWidget.css = this.json.actionStyles;

            this.loadMultiToolbar();

            // if (this.json.defaultTools){
            //     var json = Array.clone(this.json.defaultTools);
            //     this.setToolbars(json, this.toolbarNode);
            //     if (this.json.tools){
            //         this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
            //     }
            //     this.toolbarWidget.load();
            // }else{
            //     MWF.getJSON( this.getJsonPath(), function(json){
            //         this.json.defaultTools = json;
            //         var json = Array.clone(this.json.defaultTools);
            //         this.setToolbars(json, this.toolbarNode);
            //         if (this.json.tools){
            //             this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
            //         }
            //         this.toolbarWidget.load();
            //     }.bind(this), false);
            // }
        //}

    },
    _resetActionbar: function(){
        //if (this.form.options.mode == "Mobile"){
        //    this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
        //    this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        //}else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();
            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);
            if (!this.json.actionStyles){
                this.json.actionStyles = Object.merge( Object.clone( this.toolbarWidget.css ), this.json.forceStyles || {} );
            }else{
                this.toolbarWidget.css = Object.merge( Object.clone( this.json.actionStyles ), this.toolbarWidget.css, this.json.forceStyles || {} );
                this.json.actionStyles = Object.clone(this.toolbarWidget.css);
            }

            this.loadMultiToolbar();

            // if (this.json.defaultTools){
            //     var json = Array.clone(this.json.defaultTools);
            //     this.setToolbars(json, this.toolbarNode);
            //     if (this.json.tools){
            //         this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
            //     }
            //     this.toolbarWidget.load();
            // }else{
            //     MWF.getJSON(this.path+"toolbars.json", function(json){
            //         this.json.defaultTools = json;
            //         var json = Array.clone(this.json.defaultTools);
            //         this.setToolbars(json, this.toolbarNode);
            //         if (this.json.tools){
            //             this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
            //         }
            //         this.toolbarWidget.load();
            //     }.bind(this), false);
            // }
        //}
    },
    loadMultiToolbar : function(){
        if( this.json.multiTools ){
            var json = Array.clone(this.json.multiTools);
            this.setMultiToolbars(json, this.toolbarNode);
            this.toolbarWidget.load();
            this._setEditStyle_custom("hideSystemTools");
        }else if( this.json.defaultTools ){
            this.json.multiTools = this.json.defaultTools.map( function (d) { d.system = true; return d; });
            if (this.json.tools){
                this.json.multiTools = this.json.multiTools.concat( this.json.tools )
            }
            this.setMultiToolbars( Array.clone(this.json.multiTools), this.toolbarNode);
            this.toolbarWidget.load();
            this._setEditStyle_custom("hideSystemTools");
        }else{
            // MWF.getJSON(this.path+"toolbars.json", function(json){
            MWF.getJSON(this.getJsonPath(), function(json){
                this.json.multiTools = json.map( function (d) { d.system = true; return d; });
                if (this.json.tools){
                    this.json.multiTools = this.json.multiTools.concat( this.json.tools )
                }
                this.setMultiToolbars(Array.clone(this.json.multiTools), this.toolbarNode);
                this.toolbarWidget.load();
                this._setEditStyle_custom("hideSystemTools");
            }.bind(this), false);
        }
    },
    setMultiToolbars: function(tools, node){
        tools.each(function(tool){
            if( tool.system ){
                this.setToolbars( [tool], node );
            }else{
                this.setCustomToolbars( [tool], node );
            }
        }.bind(this));
    },
    setToolbars: function(tools, node){
        tools.each(function(tool){
            var actionNode = new Element("div", {
                "MWFnodetype": tool.type,
                "MWFButtonImage": this.imagePath_default+""+this.options.style+"/actionbar/"+( this.json.iconStyle || "default" )+"/"+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            if( this.json.iconOverStyle ){
                actionNode.set("MWFButtonImageOver" , this.imagePath_default+""+this.options.style+"/actionbar/"+this.json.iconOverStyle+"/"+tool.img );
            }
            this.systemTools.push(actionNode);
            if (tool.sub){
                var subNode = node.getLast();
                this.setToolbars(tool.sub, subNode);
            }
        }.bind(this));
    },
    setCustomToolbars: function(tools, node){
        //var style = (this.json.style || "default").indexOf("red") > -1 ? "red" : "blue";
        var path = "";
        if( this.json.customIconStyle ){
            path = this.json.customIconStyle+ "/";
        }

        tools.each(function(tool){
            var actionNode = new Element("div", {
                "MWFnodetype": tool.type,
                "MWFButtonImage": this.imagePath_custom+""+this.options.customImageStyle +"/custom/"+path+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            if( this.json.customIconOverStyle ){
                actionNode.set("MWFButtonImageOver" , this.imagePath_custom+""+this.options.customImageStyle +"/custom/"+this.json.customIconOverStyle+ "/" +tool.img );
            }
            this.customTools.push(actionNode);
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
        if (name=="defaultTools" || name=="tools" || name=="multiTools" || name==="actionStyles"){
            this._refreshActionbar();
        }

    }

});


MWF.require("MWF.widget.Paging", null, false);
MWF.xApplication.query.ViewDesigner.View.Paging = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.$Module,
    options : {
        "style" : "default"
    },
    initialize: function(json, jsonList, view, options){
        this.setOptions( options );
        this.propertyPath = "../x_component_query_ViewDesigner/$View/paging.html";

        this.view = view;
        this.json = json;
        this.jsonList = jsonList;
        this.css = this.view.css;
        this.container = this.view.pagingNode;
        this.moduleName = "paging";
        this.load();
    },
    load: function(){
        this.systemTools = [];
        this.customTools = [];
        if( !this.json ){
            this.loadDefaultJson(function(){
                this._load();
            }.bind(this));
        }else{
            this._load();
        }
    },
    _load : function(){
        this.json.moduleName = this.moduleName;
        this._createNode();
        this.setEvent();
    },
    loadDefaultJson: function(callback){
        var url = this.view.path+"paging.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.view.designer.actions.getUUID(function(id){
                    obj.id=id;
                    this.json = obj;
                    this.jsonList.push( this.json );
                    if (callback) callback(obj);
                }.bind(this));
            }.bind(this),
            "onerror": function(text){
                this.view.designer.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function(xhr){
                this.view.designer.notice(xhr.responseText, "error");
            }.bind(this)
        });
    },
    setTemplateStyles: function(styles){
        if( this.json.buttonStyle === "rect" ){
            this.json.style = styles.style_rect;
        }else if( this.json.buttonStyle === "round" ){
            this.json.style = styles.style_round;
        }else{
            this.json.style = styles.style;
        }
        this.json.forceStyles = styles.forceStyles || "";
    },
    clearTemplateStyles: function(styles){
        this.json.style = "blue_round";
        this.json.forceStyles = "";
    },
    setAllStyles: function(){
        this._resetPaging();
    },
    setEvent: function(){
        this.node.addEvents({
            "click": function(e){this.selected(); e.stopPropagation();}.bind(this),
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.pagingWarpNode_over)}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css.pagingWarpNode) }.bind(this)
        });
    },
    selected: function(){
        if (this.view.currentSelectedModule){
            if (this.view.currentSelectedModule==this){
                return true;
            }else{
                this.view.currentSelectedModule.unSelected();
            }
        }
        this.node.setStyles(this.css.pagingWarpNode_selected);
        new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);

        this.view.currentSelectedModule = this;
        this.isSelected = true;
        this.showProperty();
    },
    unSelected: function(){
        this.view.currentSelectedModule = null;
        this.node.setStyles(this.css.pagingWarpNode);

        this.isSelected = false;
        this.hideProperty();
    },



    resetTextNode: function(){
        var listText = (this.json.selectType=="attribute") ? (this.json.attribute || "") : (this.json.path || "");
        if (!listText) listText = "unnamed";

        this.textNode.set("text", this.json.displayName);
        this.listNode.getLast().set("text", this.json.displayName+"("+listText+")");
    },


    _createNode: function(callback){
        this.node = new Element("div", {
            "id": this.json.id,
            "MWFType": "paging",
            "styles": this.css.pagingWarpNode,
            "events": {
                "selectstart": function(e){
                    e.preventDefault();
                }
            }

        }).inject(this.container );

        this.pagingNode = new Element("div").inject(this.node);
        this.loadWidget();
        // this.pagingWidget = new MWF.widget.Paging(this.pagingNode, {"style": this.json.style}, this);
        // if (!this.json.pagingStyles){
        //     this.json.pagingStyles = Object.clone(this.pagingWidget.css);
        // }
        // this.pagingWidget.load();
    },
    loadWidget : function( isReset ){
        var visiblePages = this.json.visiblePages ? this.json.visiblePages.toInt() : 9;
        this.pagingWidget = new o2.widget.Paging(this.pagingNode, {
            style : this.json.style || "default",
            countPerPage: 20, //this.json.pageSize || this.options.perPageCount,
            visiblePages: visiblePages,
            currentPage: 1,
            itemSize: visiblePages * 20 * 3,
            // pageSize: this.pages,
            hasNextPage: typeOf( this.json.hasPreNextPage ) === "boolean" ? this.json.hasPreNextPage : true,
            hasPrevPage: typeOf( this.json.hasPreNextPage ) === "boolean" ? this.json.hasPreNextPage : true,
            hasTruningBar: typeOf( this.json.hasTruningBar ) === "boolean" ? this.json.hasTruningBar : true,
            hasBatchTuring: typeOf( this.json.hasBatchTuring ) === "boolean" ? this.json.hasBatchTuring : true,
            hasFirstPage: typeOf( this.json.hasFirstLastPage ) === "boolean" ? this.json.hasFirstLastPage : true,
            hasLastPage: typeOf( this.json.hasFirstLastPage ) === "boolean" ? this.json.hasFirstLastPage : true,
            hasJumper: typeOf( this.json.hasPageJumper ) === "boolean" ? this.json.hasPageJumper : true,
            hiddenWithDisable: false,
            // hiddenWithNoItem: true,
            text: {
                prePage: this.json.prePageText,
                nextPage: this.json.nextPageText,
                firstPage: this.json.firstPageText,
                lastPage: this.json.lastPageText
            },
            onJumpingPage : function( pageNum, itemNum ){
            }.bind(this),
            onPostLoad : function () {
                this.view.setContentHeight()
                // if(this.setContentHeightFun)this.setContentHeightFun();
            }.bind(this)
        }, isReset ? {} : (this.json.pagingStyles || {}));
        if( isReset ){
            if (!this.json.pagingStyles){
                this.json.pagingStyles = Object.merge( Object.clone( this.pagingWidget.css ), this.json.forceStyles || {} );
            }else{
                this.pagingWidget.css = Object.merge( Object.clone( this.json.pagingStyles ), this.pagingWidget.css, this.json.forceStyles || {} );
                this.json.pagingStyles = Object.clone(this.pagingWidget.css);
            }
        }else{
            if (!this.json.pagingStyles){
                this.json.pagingStyles = Object.clone(this.pagingWidget.css);
            }
        }
        this.pagingWidget.load();
    },
    _resetPaging : function(){
        this.pagingNode.empty();
        this.loadWidget( true );
    },
    _refreshPaging: function(){
        this.pagingNode.empty();
        this.loadWidget();
    },
    _setEditStyle_custom: function(name, obj, oldValue){
        if ( ["hasTruningBar","visiblePages","hasBatchTuring",
            "hasFirstLastPage","hasPreNextPage","hasPageJumper",
            "firstPageText","lastPageText","prePageText","nextPageText",
            "pagingStyles"].contains(name)){
            this._refreshPaging();
        }else if( name === "buttonStyle" ){
            if( this.json.buttonStyle === "rect" ){
                this.json.style = this.view.templateStyles.paging.style_rect;
            }else{
                this.json.style = this.view.templateStyles.paging.style_round;
            }
            this._resetPaging();
        }
    }

});
