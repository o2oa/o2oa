MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.InquiryDesigner = MWF.xApplication.query.InquiryDesigner || {};

MWF.APPDVD = MWF.xApplication.query.InquiryDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);

MWF.APPISTD = MWF.xApplication.query.InquiryDesigner;

MWF.xDesktop.requireApp("query.InquiryDesigner", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("query.ViewDesigner", "View", null, false);
MWF.xDesktop.requireApp("query.InquiryDesigner", "Property", null, false);

MWF.xApplication.query.InquiryDesigner.View = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "../x_component_query_InquiryDesigner/$View/view.html"
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "../x_component_query_InquiryDesigner/$View/";
        this.cssPath = "../x_component_query_InquiryDesigner/$View/"+this.options.style+"/css.wcss";

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
    parseData: function(){
        this.json = this.data;
        if( !this.json.data.events ){
            var url = "../x_component_query_InquiryDesigner/$View/view.json";
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
            this.property = new MWF.xApplication.query.InquiryDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
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
                                                iconNode.setStyle("background", "url(" + "../x_component_process_InquiryDesigner/$View/default/icon/down.png) center center no-repeat");
                                            }else{
                                                this.setStyles( _self.json.data.viewStyles["groupExpandNode"] )
                                            }
                                        }else{
                                            subtrs.each(function(subtr){ subtr.setStyle("display", "none"); });
                                            if( iconNode ) {
                                                iconNode.setStyle("background", "url(" + "../x_component_process_InquiryDesigner/$View/default/icon/right.png) center center no-repeat");
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
            }.bind(this));
        }
    },
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
            var column = new MWF.xApplication.query.InquiryDesigner.View.Column(json, this);
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
    unSelected: function(){
        this.currentSelectedModule = null;
        this.hideProperty();
    },
    loadViewColumns: function(){
    //    for (var i=0; i<10; i++){
        if (this.json.data.selectList) {
            this.json.data.selectList.each(function (json) {
                this.items.push(new MWF.xApplication.query.InquiryDesigner.View.Column(json, this));

            }.bind(this));
        }
    //    }
    },
    showActionbar : function( noSetHeight ){
        this.actionbarNode.show();
        if( !this.json.data.actionbarList )this.json.data.actionbarList = [];
        if( !this.actionbarList || this.actionbarList.length == 0 ){
            if( this.json.data.actionbarList.length ){
                this.json.data.actionbarList.each( function(json){
                    this.actionbarList.push( new MWF.xApplication.query.InquiryDesigner.View.Actionbar( json, this.json.data.actionbarList, this) )
                }.bind(this));
            }else{
                this.actionbarList.push( new MWF.xApplication.query.InquiryDesigner.View.Actionbar( null, this.json.data.actionbarList, this) )
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
                    this.pagingList.push( new MWF.xApplication.query.InquiryDesigner.View.Paging( json, this.json.data.pagingList, this) )
                }.bind(this));
            }else{
                this.pagingList.push( new MWF.xApplication.query.InquiryDesigner.View.Paging( null, this.json.data.pagingList, this) )
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

    saveAs: function(){
        var form = new MWF.xApplication.query.InquiryDesigner.View.NewNameForm(this, {
            name : this.data.name + "_" + MWF.xApplication.query.InquiryDesigner.LP.copy,
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

MWF.xApplication.query.InquiryDesigner.View.Column = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.Column
});

MWF.xApplication.query.InquiryDesigner.View.Actionbar = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.Actionbar
});

MWF.xApplication.query.InquiryDesigner.View.Paging = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.Paging
});
