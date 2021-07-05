MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.ImporterDesigner = MWF.xApplication.query.ImporterDesigner || {};
MWF.APPDIPD = MWF.APPDIPD || MWF.xApplication.query.ImporterDesigner;
MWF.xDesktop.requireApp("query.ViewDesigner", "View", null, false);

MWF.xDesktop.requireApp("query.ImporterDesigner", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("query.ImporterDesigner", "Property", null, false);

MWF.xApplication.query.ImporterDesigner.Importer = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "../x_component_query_ImporterDesigner/$Importer/importer.html"
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "../x_component_query_ImporterDesigner/$Importer/";
        this.cssPath = "../x_component_query_ImporterDesigner/$Importer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.data = data;

        if (!this.data.data) this.data.data = {};
        this.parseData();

        this.node = this.designer.designNode;
        //this.tab = this.designer.tab;

        this.areaNode = new Element("div.areaNode", {"styles": {"height": "100%", "overflow": "auto"}});

        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.areaNode, {"distance": 100});
        //}.bind(this));


        this.propertyListNode = this.designer.propertyDomArea;
        //this.propertyNode = this.designer.propertyContentArea;

        if(this.designer.application) this.data.applicationName = this.designer.application.name;
        if(this.designer.application) this.data.application = this.designer.application.id;

        this.isNewImportModel = (this.data.name) ? false : true;

        this.items = [];
        this.calculateItems = [];

        this.importer = this.view = this;


        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    loadViewNodes: function(){
        debugger;
        this.viewAreaNode = new Element("div#viewAreaNode", {"styles": this.css.viewAreaNode}).inject(this.areaNode);

        this.viewTitleNode = new Element("div#viewTitleNode", {"styles": this.css.viewTitleNode}).inject(this.viewAreaNode);

        this.excelTitleNode = new Element("div#excelTitleNode", {
            "styles": this.css.excelTitleNode,
            "text" : this.designer.lp.columnField+":"
        }).inject(this.viewTitleNode);

        // this.refreshNode = new Element("div", {"styles": this.css.refreshNode}).inject(this.viewTitleNode);
        this.addColumnLeftNode = new Element("div", {"styles": this.css.addColumnLeftNode}).inject(this.viewTitleNode);
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
        // MWF.require("MWF.widget.ScrollBar", function(){
        //     new MWF.widget.ScrollBar(this.viewContentScrollNode, {"style": "view", "distance": 100, "indent": false});
        // }.bind(this));

        // this.contentLeftNode = new Element("div", {"styles": this.css.contentLeftNode}).inject(this.viewContentNode);
        // this.contentRightNode = new Element("div", {"styles": this.css.contentRightNode}).inject(this.viewContentNode);

        this.calculateTitleNode = new Element("div#calculateTitleNode", {
            "styles": this.css.calculateTitleNode,
            "text" : this.designer.lp.calculateField+":"
        }).inject(this.viewContentNode);
        this.addCalculateFieldNode = new Element("div#addCalculateFieldNode", {
            "styles": this.css.addCalculateFieldNode,
        }).inject(this.calculateTitleNode);

        this.viewContentBodyNode = new Element("div", {"styles": this.css.viewContentBodyNode}).inject(this.viewContentNode);


        // this.viewContentTableNode = new Element("table", {
        //     "styles": this.css.viewContentTableNode,
        //     "border": "0px",
        //     "cellPadding": "0",
        //     "cellSpacing": "0"
        // }).inject(this.viewContentBodyNode);
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
        debugger;
        if( o2.typeOf(this.data.data)==="string"){
            this.data.data = JSON.parse(this.data.data);
        }
        this.json = this.data;
        if (!this.json.data )this.json.data = {};
        if (!this.json.data.calculateFieldList) this.json.data.calculateFieldList = [];
        if (!this.json.data.columnList) this.json.data.columnList = [];
        if( !this.json.data.events ){
            var url = "../x_component_query_ImporterDesigner/$Importer/importer.json";
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
            this.property = new MWF.xApplication.query.ImporterDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
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
            var importer = node.retrieve("importer");
            if (importer && importer.id==this.data.id){
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

        this.fireEvent("postLoad");
    },
    setEvent: function(){
        this.areaNode.addEvent("click", this.selected.bind(this));
        // this.refreshNode.addEvent("click", function(e){
        //     // this.loadViewData();
        //     e.stopPropagation();
        // }.bind(this));
        this.addColumnLeftNode.addEvent("click", function(e){
            this.addColumn( true );
            e.stopPropagation();
        }.bind(this));
        this.addColumnNode.addEvent("click", function(e){
            this.addColumn();
            e.stopPropagation();
        }.bind(this));
        this.addCalculateFieldNode.addEvent("click", function(e){
            this.addCalculateField();
            e.stopPropagation();
        }.bind(this));
    },
    addCalculateField: function(){
        var id = (new MWF.widget.UUID).id;
        var json = {
            "id": id,
            "displayName": this.designer.lp.unnamed,
        };

        this.json.data.calculateFieldList.push(json);
        var field = new MWF.xApplication.query.ImporterDesigner.Importer.CalculateField(json, this, null);
        this.calculateItems.push(field);

        field.selected();
    },

    addColumn: function( addToLeft ){

        debugger;

        MWF.require("MWF.widget.UUID", function(){
            var id = (new MWF.widget.UUID).id;
            var json = {
                "id": id,
                "displayName": this.designer.lp.unnamed,
            };

            var column;
            if( !addToLeft || this.json.data.columnList.length === 0 ){
                this.json.data.columnList.push(json);
                column = new MWF.xApplication.query.ImporterDesigner.Importer.Column(json, this, null);
                this.items.push(column);
            }else{
                this.json.data.columnList.unshift(json);
                column = new MWF.xApplication.query.ImporterDesigner.Importer.Column(json, this, this.items[0]);
                this.items.unshift(column);
            }

            column.selected();

            // if (this.viewContentTableNode){
            //     var trs = this.viewContentTableNode.getElements("tr");
            //     trs.each(function(tr){
            //         new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr)
            //     }.bind(this));
            //     //this.setContentColumnWidth();
            // }
            this.setViewWidth();

            this.items.each( function (item, i) {
                item.resetIndex(i);
            });

            if( addToLeft ){
                this.addColumnLeftNode.scrollIntoView(true);
            }else{
                this.addColumnNode.scrollIntoView(true);
            }

        }.bind(this));
        //new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 0}).toRight();
    },


    loadViewColumns: function(){
        //    for (var i=0; i<10; i++){
        if (this.json.data.columnList) {
            this.json.data.columnList.each(function (json, i) {
                this.items.push(new MWF.xApplication.query.ImporterDesigner.Importer.Column(json, this, null, i));

            }.bind(this));
        }
        //    }
    },
    loadViewCalculateFields: function(){
        //    for (var i=0; i<10; i++){
        if (this.json.data.calculateFieldList) {
            this.json.data.calculateFieldList.each(function (json, i) {
                this.calculateItems.push(new MWF.xApplication.query.ImporterDesigner.Importer.CalculateField(json, this, null, i));

            }.bind(this));
        }
        //    }
    },
    // loadViewSelectAllNode : function(){
    //     var _self = this;
    //     var td = new Element("td.viewTitleCheckboxTd",{ "styles": this.css.viewTitleColumnAreaNode }).inject( this.viewTitleTrNode );
    //     td.setStyles({
    //         "width":"30px", "text-align" : "center",
    //         "display" : this.json.data.selectAllEnable ? "table-cell" : "none"
    //     });
    //     new Element("input",{
    //         "type" : "checkbox",
    //         "events" : {
    //             "change" : function(){
    //                 _self.viewContentTableNode.getElements(".viewContentCheckbox").set("checked", this.checked )
    //             }
    //         }
    //     }).inject(td);
    // },
    // createViewCheckboxTd : function( tr ){
    //     var td = new Element("td.viewContentCheckboxTd", {"styles": this.css.viewContentTdNode}).inject(tr, "top");
    //     td.setStyles({
    //         "width":"30px", "text-align" : "center",
    //         "display" : this.json.data.selectAllEnable ? "table-cell" : "none"
    //     });
    //     new Element("input.viewContentCheckbox",{
    //         "type" : "checkbox"
    //     }).inject(td);
    // },
    loadView: function(){
        this.loadViewNodes();
        //this.loadViewSelectAllNode();
        this.loadViewColumns();
        this.loadViewCalculateFields();
//        this.addTopItemNode.addEvent("click", this.addTopItem.bind(this));
    },
    setViewWidth: function(){
        if( !this.viewAreaNode )return;
        this.viewAreaNode.setStyle("width", "auto");
        this.viewTitleNode.setStyle("width", "auto");

        var s1 = this.viewTitleTableNode.getSize();

        var m1 = this.viewTitleNode.getStyle("margin-left");
        var m2 = this.viewTitleNode.getStyle("margin-right");

        var s2 = this.addColumnLeftNode.getSize();
        var s3 = this.addColumnNode.getSize();
        var width = s1.x+s2.x+s2.x - m1.toFloat() - m2.toFloat();
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
    setContentHeight: function(){
        var size = this.areaNode.getSize();

        var titleSize = this.viewTitleNode.getSize();
        var calculateTitleNodeSize = this.calculateTitleNode ? this.calculateTitleNode.getSize() : {x:0, y:0};
        var height = size.y-titleSize.y-calculateTitleNodeSize.y-2;

        // this.viewContentScrollNode.setStyle("height", height);

        // var contentSize = this.viewContentBodyNode.getSize();
        // if (height<contentSize.y) height = contentSize.y+10;

        // this.viewContentNode.setStyle("height", height);
    },


    setAreaNodeSize: function(){

    },


    saveSilence: function(callback){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.notice.inputName, "error");
            return false;
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

        this.designer.actions.saveImportModel(this.data, function(json){
            this.data.id = json.data.id;
            this.isNewImportModel = false;
            //this.page.textNode.set("text", this.data.name);
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            if (callback) callback();
        }.bind(this));
    },
    save: function(callback){
        debugger;
        if (!this.data.name){
            this.designer.notice(this.designer.lp.notice.inputName, "error");
            return false;
        }

        var fieldArr=[], textArr=[], data = this.json.data;
        var fieldArr2 = [], textArr2 = [];
        if (this.json.type==="cms"){
            if( data.documentPublisher !== "importer" ){
                fieldArr.push("documentPublisherField");
                textArr.push("publisher");
            }
            if( data.documentPublishTime !== "importer" ){
                fieldArr.push("documentPublisherTimeField");
                textArr.push("publishTime");
            }
        }else if(this.json.type==="process"){
            if( data.processDrafter !== "importer" ){
                fieldArr.push("processDrafterField");
                textArr.push("processDrafter");
            }
            if( data.processStatus === "completed" ){
                fieldArr = fieldArr.concat(["processStartTimeField", "processCompleteTimeField"]);
                textArr = textArr.concat([ "startTimeField", "completeTimeField"]);

                fieldArr2.push("processForm");
                textArr2.push("selectForm");
            }
        }

        var noteTextArr = [];
        var lp = this.view.designer.lp.propertyTemplate;
        if( fieldArr.length || fieldArr2.length){

            fieldArr2.each( function(field, i) {
                if (!data[field])noteTextArr.push(lp[textArr2[i]]);
            });

            var columnList = [].concat(data.columnList, data.calculateFieldList);
            fieldArr.each( function(field, i){
                if( !data[field] ){
                    noteTextArr.push( lp[textArr[i]] );
                }else {
                    var path = data[field];
                    var flag = columnList.some(function (c) { return c.path === path;});
                    if(!flag)noteTextArr.push( lp[textArr[i]] );
                }
            }.bind(this));
        }


        if( noteTextArr.length ){
            var _self = this;
            var text = MWF.APPDIPD.LP.notice.someFieldIsEmpty.replace("{text}", noteTextArr.join("、"));
            this.designer.confirm("warn", this.node, MWF.APPDIPD.LP.notice.saveNotice, text, 300, 120, function(){
                _self._save();
                this.close();
            }, function(){
                this.close();
            }, null);
        }else{
            this._save(callback)
        }
    },
    _save: function(callback){
        //if (this.designer.tab.showPage==this.page){


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

        this.designer.actions.saveImportModel(this.data, function(json){
            this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
            this.isNewImportModel = false;
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
    _setEditStyle: function(name, input, oldValue) {
        debugger;
        if(name === "data.process"){
            this.property.loadFormSelect();
        }
    },
    reloadMaplist: function(){
        if (this.property) Object.each(this.property.maplists, function(map, name){ map.reload(this.json[name]);}.bind(this));
    },
    setCustomStyles: function(){
        // this.items.each( function( item ){
        //     item.setCustomStyles()
        // }.bind(this));
    },
    saveAs: function(){
        var form = new MWF.xApplication.query.ImporterDesigner.Importer.NewNameForm(this, {
            name : this.data.name + "_" + MWF.xApplication.query.ImporterDesigner.LP.copy,
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

        d.isNewImportModel = true;
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

        d.data.columnList.each( function( entry ){
            entry.id = (new MWF.widget.UUID).id;
        }.bind(this));

        this.designer.actions.saveImportModel(d, function(json){
            this.designer.notice(this.designer.lp.notice.saveAs_success, "success", this.node, {"x": "left", "y": "bottom"});
            if (callback) callback();
        }.bind(this));
    },
    checkUniqueSetting: function (column, name , value) {
        debugger;
        if (value !== "true")return;
        ([].concat(this.items, this.calculateItems)).each(function (col) {
            if( col !== column && (col.json[name] === "true" || col.json[name] === true) ){
                col.json[name] = false;
                if(col.property){
                    var radios = col.property.propertyContent.getElements("input[name$='"+name+"']"); //$匹配结尾
                    radios.each(function (r) {
                        if (r.value === "true") r.set("checked", false);
                        if (r.value === "false") r.set("checked", true);
                    });
                }
            }
        });
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
                importerId : this.data.id
            });
            window.open(o2.filterUrl(url),"_blank");
        }.bind(this));
    },
});


MWF.xApplication.query.ImporterDesigner.Importer.Column = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.Column,
    initialize: function(json, view, next, index){
        this.propertyPath = "../x_component_query_ImporterDesigner/$Importer/column.html";
        this.view = view;
        this.json = json;
        this.next = next;
        this.index = index;
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


        var colName = this.index2ColumnName( this.index );
        colName = colName ? ( colName + ": " ) : "";

        var displayName;
        if( this.json.path && (!this.json.displayName || this.json.displayName === this.view.designer.lp.unnamed) ){
            displayName = this.json.path;
        }else{
            displayName = this.json.displayName
        }

        this.textNode = new Element("div", {
            "styles": this.css.viewTitleColumnTextNode,
            "text": colName + displayName
        }).inject(this.node);

        this.createDomListItem();


        this._createIconAction();

        //if (!this.json.export) this.hideMode();

        this.setEvent();
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
                "title": MWF.APPDIPD.LP.action.move
            });
            this._createAction({
                "name": "add",
                "icon": "add.png",
                "event": "click",
                "action": "addColumn",
                "title": MWF.APPDIPD.LP.action.add
            });
            this._createAction({
                "name": "delete",
                "icon": "delete1.png",
                "event": "click",
                "action": "delete",
                "title": MWF.APPDIPD.LP.action["delete"]
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

    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.query.ImporterDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
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
    "delete": function(e){
        var _self = this;
        if (!e) e = this.node;
        this.view.designer.confirm("warn", e, MWF.APPDIPD.LP.notice.deleteColumnTitle, MWF.APPDIPD.LP.notice.deleteColumn, 300, 120, function(){
            _self.destroy();
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    _setEditStyle: function(name, input, oldValue){
        //column
        debugger;
        if (name==="displayName") {
            this.resetTextNode();
            this.view.property.loadSelectField();
        }
        if (name==="path") {
            this.resetTextNode();
            this.view.property.loadSelectField();
        }
        if (name==="selectType") this.resetTextNode();
        if (name==="attribute") this.resetTextNode();

        // if (["isTitle","isSummary","isPublisher","isProcessTitle","isProcessDrafter"].contains(name)){
        //     this.view.checkUniqueSetting(this, name, input.get("value"));
        // }

    },
    resetTextNode: function( index ){
        var listText = (this.json.selectType=="attribute") ? (this.json.attribute || "") : (this.json.path || "");
        if (!listText) listText = "unnamed";

        var colName = this.index2ColumnName( index || this.index );
        colName = colName ? ( colName + ": " ) : "";

        var displayName;
        if( this.json.path && (!this.json.displayName || this.json.displayName === this.view.designer.lp.unnamed) ){
            displayName = this.json.path;
        }else{
            displayName = this.json.displayName
        }

        this.textNode.set("text", colName + displayName );
        this.listNode.getLast().set("text", this.json.displayName+"("+listText+")");
    },
    destroy: function(){
        if (this.view.currentSelectedModule==this) this.view.currentSelectedModule = null;
        if (this.actionArea) this.actionArea.destroy();
        if (this.listNode) this.listNode.destroy();
        if (this.property) this.property.propertyContent.destroy();

        var idx = this.view.items.indexOf(this);

        // if (this.view.viewContentTableNode){
        //     var trs = this.view.viewContentTableNode.getElements("tr");
        //     var isGroup = this.isGroupColumn();
        //     trs.each(function(tr){
        //         if( isGroup ){
        //             if( tr.get("data-is-group") === "yes" ){
        //                 tr.destroy()
        //             }
        //         }else{
        //             if( tr.get("data-is-group") !== "yes" ){
        //                 tr.deleteCell(idx);
        //             }
        //         }
        //     }.bind(this));
        // }

        if (this.view.json.data.columnList) this.view.json.data.columnList.erase(this.json);
        // if (this.view.json.data.calculate) if (this.view.json.data.calculate.calculateList) this.view.json.data.calculate.calculateList.erase(this.json);
        this.view.items.erase(this);
        if (this.view.property) this.view.property.loadStatColumnSelect();

        this.areaNode.destroy();

        this.view.property.loadSelectField();

        this.view.selected();

        this.view.setViewWidth();

        this.view.items.each( function (item, i) {
            item.resetIndex(i);
        });

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

            var idx = this.view.json.data.columnList.indexOf(this.json);
            this.view.json.data.columnList.splice(idx, 0, json);

            var column = new MWF.xApplication.query.ImporterDesigner.Importer.Column(json, this.view, this);
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

            this.view.items.each( function (item, i) {
                item.resetIndex(i);
            });

        }.bind(this));
    },
    resetIndex : function( index ){
        this.index = index;
        this.resetTextNode();
    },
    index2ColumnName : function( index ){
        if( typeOf(index) !== "number" )return null;
        if (index < 0) return null;
        var num = 65;// A的Unicode码
        var colName = "";
        do {
            if (colName.length > 0)index--;
            var remainder = index % 26;
            colName =  String.fromCharCode(remainder + num) + colName;
            index = (index - remainder) / 26;
        } while (index > 0);
        return colName;
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

                    this.view.json.data.columnList.erase(this.json);
                    this.view.items.erase(this);

                    var idx = this.view.json.data.columnList.indexOf(column.json);

                    this.view.json.data.columnList.splice(idx, 0, this.json);
                    this.view.items.splice(idx, 0, this);

                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode) this.moveFlagNode.destroy();
                    this._setActionAreaPosition();

                    this.view.items.each( function (item, i) {
                        item.resetIndex(i);
                    });

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

});

MWF.xApplication.query.ImporterDesigner.Importer.CalculateField = new Class({
    Extends: MWF.QV$Module,
    initialize: function(json, view, next){
        this.propertyPath = "../x_component_query_ImporterDesigner/$Importer/calculateField.html";
        this.view = view;
        this.json = json;
        this.next = next;
        this.css = this.view.css;
        this.content = this.view.viewContentBodyNode;
        // this.domListNode = this.view.domListNode;
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
        this.areaNode = new Element("div", {"styles": this.css.viewCalculateFieldAreaNode});
        this.areaNode.store("column", this);

        if (this.next){
            this.areaNode.inject(this.next.areaNode, "before");
        }else{
            this.areaNode.inject(this.content);
        }

        this.node = new Element("div", {
            "styles": this.css.viewCalculateFieldNode
        }).inject(this.areaNode);
        this.textNode = new Element("div", {
            "styles": this.css.viewCalculateFieldTextNode,
            "text": this.json.displayName
        }).inject(this.node);

        // this.createDomListItem();

        this._createIconAction();

        this.setEvent();

        this.setCustomStyles();
    },
    loadDefaultJson: function(callback){
        if( this.view.defaultCalculateFieldJson ){
            this.json = Object.merge( this.json, Object.clone(this.view.defaultCalculateFieldJson) );
            if (callback) callback(this.json);
            return;
        }
        var url = this.view.path+"calculateField.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.view.defaultCalculateFieldJson = Object.clone(obj);
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
    },
    selected: function(){
        if (this.view.currentSelectedModule){
            if (this.view.currentSelectedModule==this){
                return true;
            }else{
                this.view.currentSelectedModule.unSelected();
            }
        }
        this.node.setStyles(this.css.viewCalculateFieldNode_selected);
        // this.listNode.setStyles(this.css.cloumnListNode_selected);
        new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        // new Fx.Scroll(this.view.designer.propertyDomArea, {"wheelStops": false, "duration": 100}).toElement(this.listNode);

        this.view.currentSelectedModule = this;
        this.isSelected = true;
        this._showActions();
        this.showProperty();
    },
    unSelected: function(){
        this.view.currentSelectedModule = null;
        //this.node.setStyles(this.css.viewTitleColumnNode);
        // if (this.isError){
        //     this.node.setStyles(this.css.viewTitleColumnNode_error)
        // }else{
            this.node.setStyles(this.css.viewCalculateFieldNode)
        // }

        // this.listNode.setStyles(this.css.cloumnListNode);
        this.isSelected = false;
        this._hideActions();
        this.hideProperty();
    },
    _createIconAction: function(){
        if (!this.actionArea){
            this.actionArea = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.view.areaNode, "after");

            this._createAction({
                "name": "move",
                "icon": "move1.png",
                "event": "mousedown",
                "action": "move",
                "title": MWF.APPDIPD.LP.action.move
            });
            this._createAction({
                "name": "add",
                "icon": "add.png",
                "event": "click",
                "action": "addColumn",
                "title": MWF.APPDIPD.LP.action.add
            });
            this._createAction({
                "name": "delete",
                "icon": "delete1.png",
                "event": "click",
                "action": "delete",
                "title": MWF.APPDIPD.LP.action["delete"]
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

    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.query.ImporterDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
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
    "delete": function(e){
        var _self = this;
        if (!e) e = this.node;
        this.view.designer.confirm("warn", e, MWF.APPDIPD.LP.notice.deleteFieldTitle, MWF.APPDIPD.LP.notice.deleteField, 300, 120, function(){
            _self.destroy();
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    // setEditStyle: function(name, input, oldValue){
    //     debugger;
    //     if (name=="displayName") this.resetTextNode();
    //     if (name=="selectType") this.resetTextNode();
    //     if (name=="attribute") this.resetTextNode();
    //     if (name=="path") this.resetTextNode();
    // },
    _setEditStyle_custom: function(name, input, oldValue){
        //calculate
        debugger;
        if (name=="displayName"){
            this.resetTextNode();
            this.view.property.loadSelectField();
        }
        if (name=="selectType") this.resetTextNode();
        if (name=="attribute") this.resetTextNode();
        if (name=="path"){
            this.resetTextNode();
            this.view.property.loadSelectField();
        }
        // if (["isTitle","isSummary","isPublisher","isProcessTitle","isProcessDrafter"].contains(name)){
        //     this.view.checkUniqueSetting(this, name, input.get("value"));
        // }
    },
    resetTextNode: function( index ){
        // var listText = (this.json.selectType=="attribute") ? (this.json.attribute || "") : (this.json.path || "");
        // if (!listText) listText = "unnamed";

        // var colName = this.index2ColumnName( index || this.index );
        // colName = colName ? ( colName + ": " ) : "";

        var displayName;
        if( this.json.path && (!this.json.displayName || this.json.displayName === this.view.designer.lp.unnamed) ){
            displayName = this.json.path;
        }else{
            displayName = this.json.displayName
        }

        this.textNode.set("text", displayName );
    },
    destroy: function(){
        if (this.view.currentSelectedModule==this) this.view.currentSelectedModule = null;
        if (this.actionArea) this.actionArea.destroy();
        // if (this.listNode) this.listNode.destroy();
        if (this.property) this.property.propertyContent.destroy();

        var idx = this.view.calculateItems.indexOf(this);

        if (this.view.json.data.calculateFieldList) this.view.json.data.calculateFieldList.erase(this.json);
        // if (this.view.json.data.calculate) if (this.view.json.data.calculate.calculateList) this.view.json.data.calculate.calculateList.erase(this.json);
        this.view.calculateItems.erase(this);
        if (this.view.property) this.view.property.loadStatColumnSelect();

        this.areaNode.destroy();


        this.view.property.loadSelectField();

        this.view.selected();


        // this.view.setViewWidth();

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

            var idx = this.view.json.data.calculateFieldList.indexOf(this.json);
            this.view.json.data.calculateFieldList.splice(idx, 0, json);

            var column = new MWF.xApplication.query.ImporterDesigner.Importer.CalculateField(json, this.view, this);
            this.view.calculateItems.splice(idx, 0, column);
            column.selected();

        }.bind(this));
    },
    move: function(e){
        var columnNodes = [];
        this.view.calculateItems.each(function(item){
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
        this.moveFlagNode = new Element("td", {"styles": this.css.moveFieldFlagNode});
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
                    // this.listNode.inject(column.listNode, "before");

                    this.view.json.data.calculateFieldList.erase(this.json);
                    this.view.calculateItems.erase(this);

                    var idx = this.view.json.data.calculateFieldList.indexOf(column.json);

                    this.view.json.data.calculateFieldList.splice(idx, 0, this.json);
                    this.view.calculateItems.splice(idx, 0, this);

                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode) this.moveFlagNode.destroy();
                    this._setActionAreaPosition();

                    // this.view.items.each( function (item, i) {
                    //     item.resetIndex(i);
                    // });

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
})