MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.ViewDesigner = MWF.xApplication.cms.ViewDesigner || {};
MWF.CMSVD = MWF.xApplication.cms.ViewDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("cms.ViewDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.ViewDesigner.View = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "showTab": true
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "/x_component_cms_ViewDesigner/$View/";
        this.cssPath = "/x_component_cms_ViewDesigner/$View/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.documentFields =designer.documentFields;
        this.formFields=designer.formFields;
        this.relativeForm = data.content.relativeForm;
        this.actions = designer.actions;
        this.application = designer.application;
        this.lp = this.designer.lp;


        this.node = this.designer.designNode;
        this.tab = this.designer.tab;

        this.areaNode = new Element("div.areaNode", {"styles": {"overflow": "hidden"}});

        //this.propertyListNode = this.designer.propertyDomArea;
        //this.propertyNode = this.designer.propertyContentArea;

        this.data = data.content;

        this.isNewView = this.data.isNew;

        this.columns = [];
        this.columnsRemoved = [];

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    autoSave: function(){
        this.autoSaveTimerID = window.setInterval(function(){
            if (!this.autoSaveCheckNode) this.autoSaveCheckNode = this.designer.contentToolbarNode.getElement("#MWFViewAutoSaveCheck");
            if (this.autoSaveCheckNode){
                if (this.autoSaveCheckNode.get("checked")){
                    this.save();
                }
            }
        }.bind(this), 60000);

    },


    load : function(){
        this.setAreaNodeSize();
        this.designer.addEvent("resize", function(){
            this.setAreaNodeSize();
            this.setPropertyContentResize();
            this.setViewNodeWidth();
        }.bind(this));

        this.page = this.tab.addTab(this.areaNode, this.data.name || this.designer.lp.newView, (!this.data.isNew && this.data.id!=this.designer.options.id));
        this.page.view = this;

        this.page.addEvent("show", function(){
            this.designer.viewListAreaNode.getChildren().each(function(node){
                var view = node.retrieve("view");
                if (view.id==this.data.id || (view.content.isNew && this.isNewView) ){
                    if (this.designer.currentListViewItem){
                        this.designer.currentListViewItem.setStyles(this.designer.css.listViewItem);
                    }
                    node.setStyles(this.designer.css.listViewItem_current);
                    this.designer.currentListViewItem = node;
                    this.lisNode = node;
                }
            }.bind(this));
            if(!this.propertyNode)this.loadProperty();

        }.bind(this));
        this.page.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
            this.saveSilence();
            if (this.lisNode) this.lisNode.setStyles(this.designer.css.listScriptItem);
        }.bind(this));
        this.page.tabNode.addEvent("dblclick", this.designer.maxOrReturnEditor.bind(this.designer));

        this.createViewNode();

        if (this.options.showTab) this.page.showTabIm();
        this.setPropertyContentResize();
    },

    saveSilence: function(callback){
        this._save(callback);
    },
    save: function(callback){
        //if (this.designer.tab.showPage==this.page){
        //
        //}
        this._save( callback, true );
    },

    _save : function(callback, isNotice ){
        var _self = this;
        if (!this.data.name || this.data.name==""){
            this.designer.notice(this.lp.notice.inputName, "error");
            return false;
        }
        //var flag = true;
        //if( flag ){
        //    this.columns.each(function(column){
        //        flag = column.save();
        //    })
        //}
        //if(!flag)return false;
        //this.columnsRemoved.each(function(column){
        //    column.delete(function(){
        //        _self.columnsRemoved.erase(this);
        //    }.bind(column));
        //})
        var data = {};
        data.isNew = this.isNewView; //this.data.isNew;
        data.id = this.data.id;
        data.name = this.data.name;
        data.alias = this.data.alias;
        data.description = this.data.description;
        data.appId = this.data.application;
        data.formId = this.data.relativeForm.id;
        data.orderType = this.data.sortType;
        data.orderField = this.data.sortField;
        data.orderFieldType   = this.data.sortFieldType;

        this.data.isNew = false;
        this.data.columns = this.getColumnsData();
        data.fields = this.getColumnsItemData();
        data.content = JSON.stringify(this.data);

        this.designer.actions.saveView(data, function(json){
            this.data.id = json.data.id;
            if( isNotice ){
                this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
            }
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
                if( this.isNewView ){
                    this.lisNode.eliminate("view");
                    this.lisNode.store("view",json.data);
                }
            }
            this.data.isNew = false;
            this.isNewView = false;
            this.page.textNode.set("text", this.data.name);
            if (callback) callback();
        }.bind(this));
    },
    saveAs: function(){
        var form = new MWF.xApplication.cms.ViewDesigner.View.NewName(this, {
            name : this.data.name + "_副本"
        }, {
            onSave : function( data, callback ){
                this._saveAs( data.name , callback);
            }.bind(this)
        }, {
            app: this.designer
        });
        form.edit()
    },
    clone : function( obj ){
        if (null == obj || "object" != typeof obj) return obj;

        if ( typeof obj.length==='number'){ //数组
            //print( "array" );
            var copy = [];
            for (var i = 0, len = obj.length; i < len; ++i) {
                copy[i] = this.clone(obj[i]);
            }
            return copy;
        }else{
            var copy = {};
            for (var attr in obj) {
                copy[attr] = this.clone(obj[attr]);
            }
            return copy;
        }
    },
    _saveAs : function( name , callback){
        var _self = this;

        var d = this.clone( this.data );
        d.name = name;
        d.alias = "";

        var data = {};
        data.isNew = true; //this.data.isNew;
        data.id = this.designer.actions.getUUID();
        data.name = name;
        data.alias = "";
        data.description = d.description;
        data.appId = d.application;
        data.formId = d.relativeForm.id;
        data.orderType = d.sortType;
        data.orderField = d.sortField;
        data.orderFieldType   = d.sortFieldType;

        d.isNew = false;

        var columnsData = this.clone( this.getColumnsData() );
        var fieldData = this.clone( this.getColumnsItemData() );
        columnsData.each( function( column, i ){
            var field = fieldData[i];
            var id = this.designer.actions.getUUID();
            column.id = id;
            column.isNew = false;
            column.viewId = data.id;

            field.id = id;
            field.isNew = true;
            field.viewId = data.id;
        }.bind(this));

        d.columns = columnsData;

        data.fields = fieldData;
        data.content = JSON.stringify(d);

        this.designer.actions.saveView(data, function(json){
            this.designer.notice(this.designer.lp.notice.saveAs_success, "success", this.node, {"x": "left", "y": "bottom"});
            if (callback) callback();
        }.bind(this));
    },
    explode: function(){},
    implode: function(){},

    setAreaNodeSize: function(){
        var size = this.node.getSize();
        var tabSize = this.tab.tabNodeContainer.getSize();
        var y = parseInt(size.y - tabSize.y);
        this.areaNode.setStyle("height", ""+y+"px");
    },
    setViewNodeWidth: function(){
        this.columnWidth = this.getColumnsWidth();
        var cWidth =  this.columnWidth + ( this.columns.length * 2 ) + 300;
        if( this.node.getSize().x - 10 > cWidth ){
            this.viewNode.setStyle( "width", this.node.getSize().x - 10  + "px");
        }else{
            this.viewNode.setStyle( "width" , cWidth + "px" );
        }
    },
    createViewNode: function() {
        this.viewAreaNode = new Element("div.viewAreaNode", { styles :  { "overflow-x" : "scroll", "overflow-y" : "hidden" } }).inject( this.areaNode );

        this.viewAreaNode.addEvent("scroll", function() {
            if (this.currentColumn)this.currentColumn._hideActions();
        }.bind(this));
        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.viewAreaNode, {"distance": 100});
        //}.bind(this));

        this.setViewAreaNodeSize();
        this.viewAreaNode.addEvent( "click", function(){
            if(this.currentColumn) {
                this.currentColumn.cancelCurrent();
                this.currentColumn.hideProperty();
            }
            this.showPropertyContent();
        }.bind(this));
        this.viewNode = new Element("div.viewNode", { "styles": this.css.viewNode }).inject( this.viewAreaNode );

        this.headBar = new MWF.xApplication.cms.ViewDesigner.View.HeadBar(this);
        if( this.data.columns && this.data.columns.length > 0 ){
            for(var i=0;i<this.data.columns.length;i++){
                var c = this.data.columns[i];
                this.addColumn( i, c );
            }
        }else {
            this.addColumn(0);
        }
        this.setViewNodeWidth();
    },
    setViewAreaNodeSize: function(){
        var size = this.node.getSize();
        var tabSize = this.tab.tabNodeContainer.getSize();
        var y = parseInt((size.y - tabSize.y)/3);
        this.viewAreaNode.setStyle("height", ""+y+"px");
    },

    getTemplateData: function( callback){
        if (this.dataTemplate){
            if (callback) callback(this.dataTemplate);
        }else{
            var templateUrl = this.path +this.options.style+"/columnTemplate.json";
            MWF.getJSON(templateUrl, function(responseJSON, responseText){
                this.dataTemplate = responseJSON;
                if (callback) callback(responseJSON);
            }.bind(this), false );
        }
    },

    addColumn : function( index , data ){
        if( !data ){
            this.getTemplateData();
            data = Object.clone( this.dataTemplate );
            data.isNew = true;
            data.id = this.actions.getUUID();
        }
        index = index || 0;
        if( this.columns.length <= index  ){
            index = this.columns.length;
        }
        var  column = new MWF.xApplication.cms.ViewDesigner.View.Column(this, data , index );
        if( this.columns.length == index ){
            this.columns.push( column );
        }else{
            var tmpColumns = this.columns.splice( index, this.columns.length - index , column );
            tmpColumns.each(function(c){
                c.data.index = c.data.index + 1;
                c.node.set("index", c.data.index);
            });
            this.columns = this.columns.concat( tmpColumns );
        }
        this.setEachColumnWidth();
        this.setViewNodeWidth();
    },
    moveColumn : function(fromIndex, toIndex){
        if( fromIndex == toIndex )return;
        var tmpColumns = [];
        for(var i=0; i<this.columns.length; i++){
            if( i != fromIndex  ){
                if( i == toIndex && toIndex != this.columns.length ) {
                    tmpColumns.push( this.columns[fromIndex] );
                }
                tmpColumns.push( this.columns[i] );
                if( i == this.columns.length-1 && toIndex == this.columns.length ) {
                    tmpColumns.push( this.columns[fromIndex] );
                }
            }
        }
        this.columns = tmpColumns;
        for(var i=0; i<this.columns.length; i++){
            c = this.columns[i];
            c.data.index = i;
            c.node.set("index",i);
        }
        this.setViewNodeWidth();
    },
    removeColumn : function( index ){
        if( this.columns.length <= 1 ){
            this.designer.notice(this.designer.lp.notice.noRemoveOnlyColumn, "error");
            return;
        }
        for( var i=index+1; i<this.columns.length;i++ ){
            c = this.columns[i];
            c.data.index = c.data.index - 1;
            //c.contentNode.set("text",c.index);
            c.node.set("index", c.data.index);
        }
        this.showPropertyContent();
        var column = this.columns.splice(index, 1);
        if( !column[0].data.isNew ){
            this.columnsRemoved.push(column[0]);
        }
        column[0].removeNode();
        this.setEachColumnWidth();
        this.setViewNodeWidth();
    },
    getColumnNodes : function(){
        var columnNodes = [];
        this.columns.each(function(column){
            columnNodes.push(column.node);
        });
        return columnNodes;
    },
    getColumnsWidth : function(){
        var width = 0;
        this.columns.each(function(column){
            width = width + column.data.width;
        });
        return width;
    },
    setEachColumnWidth : function(){
        var totalWidth = this.getColumnsWidth();
        this.columns.each(function(column){
            if( column.property ){
                var per = Math.round( (column.data.width/totalWidth) * 100 );
                column.property.columnPercentageWidthNode.set("text", per );
                column.data.widthPer = per;
            }
        })
    },
    getColumnsData : function(){
        var data = [];
        this.columns.each(function(column){
            data.push(column.data);
        });
        return data;
    },
    getColumnsItemData: function( ignoreNew ){
        var data = [];
        this.columns.each(function(column){
            data.push(column.getData());
            if(!ignoreNew)column.data.isNew = false;
        });
        return data;
    },

    //loadProperty------------------------
    loadProperty: function(){
        this.propertyNode = new Element("div", {
            "styles": this.css.propertyNode
        }).inject(this.areaNode);

        this.propertyContentNode = new Element("div.propertyContentNode", {
            "styles": this.css.propertyContentNode
        }).inject(this.propertyNode);


        this.viewAreaPercent = 0.3;
        this.propertyContentResizeNode = new Element("div", {
            "styles": this.css.propertyContentResizeNode
        }).inject(this.propertyContentNode);


        this.propertyTitleNode = new Element("div.propertyTitleNode", {
            "styles": this.css.propertyTitleNode,
            "text": this.lp.viewProperty
        }).inject(this.propertyContentNode);

        this.propertyContentArea = new Element("div.propertyContentArea", {
            "styles": this.css.propertyContentArea
        }).inject(this.propertyContentNode);

        this.loadPropertyContentResize();

        this.setPropertyContent();
        this.propertyNode.addEvent("keydown", function(e){e.stopPropagation();});
    },

    setPropertyContent: function(){

        this.propertyContentContainArea  = new Element("div.propertyContentContainArea").inject(this.propertyContentArea);

        this.viewPropertyNode = new Element("div.viewPropertyNode", {"styles": this.css.viewPropertyNode});
        //this.eventsNode = new Element("div.eventsNode", {"styles": this.css.eventsNode});

        MWF.require("MWF.widget.Tab", function(){
            this.propertyTab = new MWF.widget.Tab(this.propertyContentContainArea, {"style": "moduleList"});
            this.propertyTab.load();

            var page = this.propertyTab.addTab(this.viewPropertyNode, this.lp.base, false);
            page.contentNodeArea.set("class","viewContentNodeArea");
            this.setScrollBar(page.contentNodeArea, "small", null, null);

            //page = this.propertyTab.addTab(this.eventsNode, this.lp.events, false);
            //page.contentNodeArea.set("class","eventsContentNodeArea");
            //this.setScrollBar(page.contentNodeArea, "small", null, null);

            this.propertyTab.pages[0].showTab();
        }.bind(this));

        var table = new Element("table", { "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.editTable, "class" : "editTable"}).inject( this.viewPropertyNode );

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.id  }).inject(tr);
        var td = this.propertyIdNode = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue , "text": this.data.id ? this.data.id : "" }).inject(tr);

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.relativeForm  }).inject(tr);
        var td = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue , "text": this.relativeForm.name }).inject(tr);

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.name  }).inject(tr);
        var td = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue }).inject(tr);
        this.propertyNameNode = new Element("input", {"styles": this.css.editTableInput}).inject(td);
        this.propertyNameNode.addEvent("change",function(){
            this.data.name = this.propertyNameNode.get("value");
        }.bind(this));

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.alias  }).inject(tr);
        var td = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue }).inject(tr);
        this.propertyAliasNode = new Element("input", {"styles": this.css.editTableInput}).inject(td);
        this.propertyAliasNode.addEvent("change",function(){
            this.data.alias = this.propertyAliasNode.get("value");
        }.bind(this));

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.sortColumn  }).inject(tr);
        var td = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue }).inject(tr);
        this.propertySortFieldNode = this.getFieldsSelectElement();
        this.propertySortFieldNode.inject(td);
        this.propertySortFieldNode.addEvent("change",function(){
            this.data.sortField = this.getSelectText(this.propertySortFieldNode);
            this.data.sortFieldType = this.getSelectValue(this.propertySortFieldNode);
        }.bind(this));

        this.propertySortTypeNode = this.getSortSelectElement();
        this.propertySortTypeNode.inject(td);
        this.propertySortTypeNode.addEvent("change",function(){
            this.data.sortType = this.getSelectValue(this.propertySortTypeNode);
        }.bind(this));

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.description  }).inject(tr);
        var td = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue }).inject(tr);
        this.propertyDescriptionNode = new Element("textarea", {"styles": this.css.editTableTextarea}).inject(td);
        this.propertyDescriptionNode.addEvent("change",function(){
            this.data.description = this.propertyDescriptionNode.get("text");
        }.bind(this));

        //var tr = new Element("tr").inject(table);
        //var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : "JS Header"  }).inject(tr);
        //this.jsHeaderContainer = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue }).inject(tr);

        this.setPropertyValue();
    },

    hidePropertyContent : function(){
        this.propertyContentContainArea.setStyle("display", "none");
    },

    showPropertyContent : function(){
        if(this.currentColumn) {
            this.currentColumn.hideProperty();
        }
        this.propertyTitleNode.set("text",this.lp.viewProperty);
        this.propertyContentContainArea.setStyle("display", "block");
    },

    getFormSelectElement : function(formName, formId ){
        var obj = new Element("select", { "styles" : this.css.propertyFormNode });
        this.actions.listForm(this.application.id, function(json){
            json.data.each(function( form ){
                var opt = new Element("option", {
                    "value":form.id,
                    "text":form.name
                }).inject(obj);
                if( formId == form.id ){
                    opt.selected = true;
                }
            })
        }.bind(this), null, false);
        return obj;
    },

    getFieldsSelectElement : function(){
        var obj = new Element("select");
        new Element("option", {
            "value":"",
            "text":""
        }).inject(obj);
        this.documentFields.concat(this.formFields).each(function( field ){
            var opt = new Element("option", {
                "value":field.type,
                "text":field.name
            }).inject(obj);
            if( this.data.sortField == field.name ){
                opt.selected = true;
            }else if( field.name === "createTime" ){
                opt.selected = true;
            }
        }.bind(this));
        return obj;
    },

    getSortSelectElement : function(){
        var obj = new Element("select", {"styles":{"margin-left":"5px"}});
        var opt = new Element("option", {
            "value":"ASC",
            "text":this.lp.asc
        }).inject(obj);
        if( this.data.sortType == "ASC" ){
            opt.selected = true;
        }
        var opt = new Element("option", {
            "value":"DESC",
            "text":this.lp.desc
        }).inject(obj);
        if( this.data.sortType == "DESC" ){
            opt.selected = true;
        }
        return obj;
    },

    getSelectValue : function( el ){
        var value;
        el.getElements("option").each(function(opt){
            if(opt.selected){
                value = opt.value;
            }
        });
        return value;
    },
    getSelectText : function( el ){
        var text;
        el.getElements("option").each(function(opt){
            if(opt.selected){
                text = opt.text;
            }
        });
        return text;
    },

    loadPropertyContentResize: function(){
        this.propertyContentResize = new Drag(this.propertyContentResizeNode, {
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.viewAreaNode.getSize();
                el.store("initialHeight", size.y);
            }.bind(this),
            "onDrag": function(el, e){

                var size = this.areaNode.getSize();

                //			var x = e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                var position = el.retrieve("position");
                var dy = y.toFloat()-position.y.toFloat();

                var initialHeight = el.retrieve("initialHeight").toFloat();
                var height = initialHeight+dy;
                if (height<60) height = 60;
                if (height> size.y-60) height = size.y-60;

                this.viewAreaPercent = height/size.y;

                this.setPropertyContentResize();

            }.bind(this)
        });
    },
    setPropertyContentResize: function(){
        var size = this.areaNode.getSize();
        var resizeNodeSize = this.propertyContentResizeNode.getSize();
        var height = size.y-resizeNodeSize.y-27;

        var domHeight = this.viewAreaPercent*height;
        var contentHeight = height-domHeight;

        this.viewAreaNode.setStyle("height", ""+domHeight+"px");
        this.propertyContentNode.setStyle("height", ""+contentHeight+"px");

        var tabSize = this.propertyTab.tabNodeContainer.getSize();
        var titleSize = this.propertyTitleNode.getSize();
        var areaHeight = this.propertyPageHeight = contentHeight-tabSize.y-titleSize.y-20;

        this.propertyTab.pages.each(function( page ){
            page.contentNodeArea.setStyle("height",""+ areaHeight +"px" );
        });

        if( this.currentColumn && this.currentColumn.property ){
            this.currentColumn.property.propertyTab.pages.each(function( page ){
                page.contentNodeArea.setStyle("height",""+ areaHeight +"px" );
            })
        }

    },

    setPropertyValue: function(){
        this.propertyIdNode.set("text", this.data.id);
        this.propertyNameNode.set("value", this.data.name);
        this.propertyAliasNode.set("value", this.data.alias);
        this.propertyDescriptionNode.set("value", this.data.description);

        //this.loadScriptEditor( this.jsHeaderContainer, "jsheader", "JS Header"  );

        //if( !this.data.events ){
        //    this.getViewEventsData();
        //}
        //this.loadEventsEditor( this.eventsNode, this.data.events );
    },

    loadEventsEditor: function( eventsNode, eventsData ){
        MWF.xDesktop.requireApp("cms.FormDesigner", "widget.EventsEditor", function(){
            var eventsEditor = new MWF.xApplication.cms.FormDesigner.widget.EventsEditor( eventsNode, this.designer, {
                "helpStyle" : "cmsView",
                "maxObj": this.node
            });
            eventsEditor.load( eventsData );
        }.bind(this));
    },
    loadScriptEditor: function(node, name, title, style){
        var scriptContent = this.data[name];
        MWF.require("MWF.widget.ScriptArea", function(){
            var scriptArea = this.scriptArea = new MWF.widget.ScriptArea(node, {
                "title": title,
                "maxObj": this.node,
                "onChange": function(){
                    this.data[name] = scriptArea.toJson();
                }.bind(this),
                "onSave": function(){
                    this.save();
                }.bind(this),
                "style": style || "default",
                "helpStyle" : "cmsView"
            });
            scriptArea.load(scriptContent);
        }.bind(this));
    },

    getViewEventsData: function( callback){
        var templateUrl = this.path +this.options.style+"/viewEventsTemplate.json";
        MWF.getJSON(templateUrl, function(responseJSON, responseText){
            this.data.events = responseJSON;
            if (callback) callback(responseJSON);
        }.bind(this), false );
    },

    getOperationConfig: function( callback){
        if (this.operationConfig){
            if (callback) callback(this.operationConfig);
        }else{
            var templateUrl = this.path +this.options.style+"/operation.json";
            MWF.getJSON(templateUrl, function(responseJSON, responseText){
                this.operationConfig = responseJSON;
                if (callback) callback(responseJSON);
            }.bind(this), false );
        }
        return this.operationConfig;
    }

});


MWF.xApplication.cms.ViewDesigner.View.Column = new Class({

    Implements: [Options, Events],
    options: {
        "style": "default",
        //"propertyPath": "/x_component_process_FormDesigner/Module/Table$Td/table$td.html",
        "actions": [
            {
                "name": "insertColLeft",
                "icon": "insertColLeft.png",
                "event": "click",
                "action": "insertColLeft",
                "title": MWF.xApplication.cms.ViewDesigner.LP.insertColLeft
            },
            {
                "name": "insertColRight",
                "icon": "insertColRight.png",
                "event": "click",
                "action": "insertColRight",
                "title": MWF.xApplication.cms.ViewDesigner.LP.insertColRight
            },
            {
                "name": "deleteCol",
                "icon": "deleteCol1.png",
                "event": "click",
                "action": "deleteCol",
                "title": MWF.xApplication.cms.ViewDesigner.LP.deleteCol
            },
            {
                "name": "moveCol",
                "icon": "move1.png",
                "event": "click",
                "action": "moveCol",
                "title": MWF.xApplication.cms.ViewDesigner.LP.moveCol
            }
        ],
        "actionNodeStyles": {
            "width": "16px",
            "height": "16px",
            "margin-left": "2px",
            "margin-right": "2px",
            "float": "left",
            "border": "1px solid #F1F1F1",
            "cursor": "pointer"
        }
    },
    initialize: function(view, data, index ){
        this.view = view;
        this.css = view.css;
        this.designer = view.designer;
        this.data = data;
        this.container = view.viewNode;
        this.data.index = index;
        this.isCurrent = false;
        this.load();
    },
    load: function(){
        this.createNodes();
        this.createIconAction();
        this.setEvent();
    },
    createNodes : function(){
        this.node = new Element("div.column", {"styles": this.view.css.columnNode , "index" : this.data.index } );
        this.node.store("column", this);

        var tmpNode = this.container.getFirst("div.column[index="+this.data.index+"]");
        if( !tmpNode ){
            this.node.inject(this.container)
        }else{
            this.node.inject(tmpNode,"before");
        }

        this.contentNode = new Element("div", {"styles": this.view.css.columnContentNode } ).inject(this.node);
        this.contentTitleNode = new Element("div.columnContentTitleNode", {"styles": this.view.css.columnContentTitleNode } ).inject(this.contentNode);
        if( this.data.title ){
            this.contentTitleNode.set("text",this.data.title);
        }else{
            this.contentTitleNode.set("text", this.view.lp.noTitle );
        }
        if( this.data.width ){
            this.contentNode.setStyle("width",this.data.width);
        }else{
            this.contentNode.setStyle("width","150px");
            this.data.width = 150;
        }
        if( this.data.align ){
            //this.contentNode.setStyle("text-align", this.data.align );
            this.setAlignIcon();
        }
        this.resizeNode = new Element("div",{"styles":this.view.css.columnResizeNode}).inject(this.node);
        this.loadResize();

        if( this.data.operation ){
            for(var o in this.data.operation){
                op = this.data.operation[o];
                this.setOperation(op.name,op.text,op.icon,op.iconOver, op.action);
            }
        }

        if( this.data.sortByClickTitle == "yes" ){
            this.setSortIcon();
        }
    },
    setEvent:function(){
        this.node.addEvents({
            "click":function(e){
                if( !this.view.isOnDragging ){
                    this.setCurrent();
                }
                e.stopPropagation();
            }.bind(this),
            "mouseover" : function(e){
                if(!this.isCurrent)this.contentNode.setStyles( this.view.css.columnContentNode_over );
            }.bind(this),
            "mouseout" : function(e){
                if(!this.isCurrent)this.contentNode.setStyles( this.view.css.columnContentNode );
            }.bind(this)
        })
    },
    _showActions: function(){
        if (this.actionArea){
            if (this.options.actions.length){
                this._setActionAreaPosition();
                this.actionArea.setStyle("display", "block");
            }
        }
    },
    _hideActions: function(){
        if (this.actionArea) this.actionArea.setStyle("display", "none");
    },
    createIconAction: function(){
        this.actionNodes = this.actionNodes || {};
        if (!this.actionArea){
            this.actionArea = new Element("div", {
                styles: {
                    "display": "none",
                    //	"width": 18*this.options.actions.length,
                    "position": "absolute",
                    "background-color": "#F1F1F1",
                    "padding": "1px",
                    "padding-right": "0px",
                    "border": "1px solid #AAA",
                    "box-shadow": "0px 2px 5px #999",
                    "opacity": 1,
                    "z-index": 100
                }
            }).inject(this.container, "after");

            this.options.actions.each(function(action){
                var actionNode = this.actionNodes[action.name] = new Element("div", {
                    "styles": this.options.actionNodeStyles,
                    "title": action.title
                }).inject(this.actionArea);
                actionNode.setStyle("background", "url("+this.view.path+this.options.style+"/icon/"+action.icon+") no-repeat left center");
                actionNode.addEvent(action.event, function(e){
                    this[action.action](e);
                    e.stopPropagation();
                }.bind(this));
                actionNode.addEvents({
                    "mouseover": function(e){
                        e.target.setStyle("border", "1px solid #999");
                    }.bind(this),
                    "mouseout": function(e){
                        e.target.setStyle("border", "1px solid #F1F1F1");
                    }.bind(this)
                });


            }.bind(this));

        }
    },

    _setActionAreaPosition: function(){
        var p = this.node.getPosition(this.designer.designNode.getOffsetParent());
        var y = p.y-25;
        var x = p.x;
        this.actionArea.setPosition({"x": x, "y": y});
    },
    insertColLeft : function(){
        var index = this.data.index;
        this.view.addColumn( index );
        this.view.columns[index].setCurrent();
    },
    insertColRight : function(){
        var index = this.data.index + 1;
        this.view.addColumn( index );
        this.view.columns[index].setCurrent();
    },
    deleteCol : function(){
        var _self = this;
        this.designer.confirm("warn", this.actionNodes.deleteCol, MWF.xApplication.cms.ViewDesigner.LP.deleteColConfirmTitle, MWF.xApplication.cms.ViewDesigner.LP.deleteColConfirm, 300, 120, function(){
            _self.view.removeColumn(_self.data.index);
            this.close();
        }, function(){
            this.close();
        });
    },
    removeNode : function(){
        if(this.actionArea)this.actionArea.destroy();
        this.node.destroy();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.contentNode.setStyles(this.view.css.columnContentNode);
        this._hideActions()
    },
    setCurrent : function(){
        if(this.view.currentColumn) {
            if( this.view.currentColumn.currentTimeout ){
                clearTimeout( this.view.currentColumn.currentTimeout );
            }
            this.view.currentColumn.cancelCurrent();
            this.view.currentColumn.hideProperty();
        }
        this.contentNode.setStyles( this.view.css.columnContentNode_current );
        this.isCurrent = true;
        this.setNodeScroll();

        this.currentTimeout = setTimeout( function(){
            this._showActions();
            this.showProperty();
            if( this.view.propertyPageHeight ){
                this.property.propertyTab.pages.each(function( page ){
                    page.contentNodeArea.setStyle("height",""+ this.view.propertyPageHeight +"px" );
                }.bind(this))
            }
            this.view.currentColumn = this;
            this.currentTimeout = null;
        }.bind(this), 100 );
    },
    setNodeScroll : function(){
        var viewAreaNode = this.view.viewAreaNode;
        var viewNode = this.view.viewNode;
        var viewAreaCrd = viewAreaNode.getCoordinates();
        var leftPoint = viewAreaCrd.left;
        var rightPoint = leftPoint + viewAreaCrd.width;
        var nodeCrd = this.node.getCoordinates();
        if( rightPoint - nodeCrd.left < 100 ){
            var d = nodeCrd.left + 100 - rightPoint;
            if( viewAreaNode.getScroll().x + d < viewNode.getSize().x ){
                viewAreaNode.scrollTo(viewAreaNode.getScroll().x + d, 0);
            }else{
                viewAreaNode.scrollTo( viewNode.getSize().x, 0);
            }
        }else if( leftPoint >  nodeCrd.left ){
            var d = viewAreaNode.getScroll().x - (leftPoint - nodeCrd.left) - 10;
            if( d  > 0 ){
                viewAreaNode.scrollTo( d, 0);
            }else{
                viewAreaNode.scrollTo(0, 0);
            }
        }
    },
    loadResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
        this.resize = new Drag(this.resizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.contentNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
                var bodySize = this.view.viewNode.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = position.x.toFloat()-x.toFloat();

                var width = initialWidth-dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                //this.contentNode.setStyle("margin-right", width+1);
                this.contentNode.setStyle("width", width);
                this.data.width = width+10;
                if(  this.property ){
                    this.property.columnWidthNode.set("value",Math.round(width)+10);
                }
                this.view.setEachColumnWidth();
                this.view.setViewNodeWidth();
            }.bind(this)
        });
    },


    moveCol: function(e){
        this._createMoveNode();
        this._setNodeMove(e);
        this._hideActions();
    },
    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "MWFType": "label",
            "styles": this.view.css.moduleNodeMove,
            "text": this.node.get("text"),
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.container);
    },
    _setNodeMove: function(e){
        this._setMoveNodePosition(e);

        var droppables = this.view.getColumnNodes(); //[this.container].concat(this.view.node, this.view.areaNode,this.view.columns);
        droppables.push( this.view.headBar.node );
        //debugger;
        var nodeDrag = new Drag.Move(this.moveNode, {
            "droppables": droppables,
            "onEnter": function(dragging, inObj){
                var column = inObj.retrieve("column");
                if (column) column._dragIn(this);
            }.bind(this),
            "onLeave": function(dragging, inObj){
                var column = inObj.retrieve("column");
                if (column) column._dragOut(this);
            }.bind(this),
            "onDrag": function(e){
                this.view.isOnDragging = true;
                this._setScroll();
                //this._nodeDrag(e, nodeDrag);
            }.bind(this),
            "onDrop": function(dragging, inObj, e){
                if (inObj){
                    var column = inObj.retrieve("column");
                    if (column){
                        if( column.isHeadBar ){
                            if( this.data.index==0 ){
                                this._dragCancel(dragging);
                            }else{
                                this._dragComplete( column.node );
                                this.view.moveColumn( this.data.index, 0 );
                            }
                        }else{
                            if( column.data.index+1 == this.data.index || column.data.index==this.data.index ){
                                this._dragCancel(dragging);
                            }else{
                                this._dragComplete( column.node );
                                this.view.moveColumn( this.data.index, column.data.index+1 );
                            }
                        }
                        column._dragDrop(this);
                    }else{
                        this._dragCancel(dragging);
                    }
                }else{
                    this._dragCancel(dragging);
                }
                if( this.dragColInterval ){
                    clearInterval( this.dragColInterval );
                    this.dragColInterval = null;
                }
                setTimeout( function(){
                    this.view.isOnDragging = false;
                }.bind(this), 100 );
                e.stopPropagation();
            }.bind(this),
            "onCancel": function(dragging){
                if( this.dragColInterval ){
                    clearInterval( this.dragColInterval );
                    this.dragColInterval = null;
                }
                setTimeout( function(){
                    this.view.isOnDragging = false;
                }.bind(this), 100 )
            }.bind(this)
        });
        nodeDrag.start(e);


        // this.form.moveModule = this;
        //this.form.recordCurrentSelectedModule = this.form.currentSelectedModule;

        //this.form.selected();

    },
    _setScroll : function(){
        var viewAreaNode = this.view.viewAreaNode;

        var viewAreaCrd = viewAreaNode.getCoordinates();
        var leftPoint = viewAreaCrd.left;
        var rightPoint = leftPoint + viewAreaCrd.width;

        var viewNode = this.view.viewNode;
        var coordinates = this.moveNode.getCoordinates();
        if( coordinates.left + coordinates.width > rightPoint ) {
            if (!this.dragColInterval) {
                this.dragColInterval = setInterval(function () {
                    if( viewAreaNode.getScroll().x + 15 < viewNode.getSize().x ){
                        viewAreaNode.scrollTo(viewAreaNode.getScroll().x + 15, 0);
                    }else{
                        viewAreaNode.scrollTo( viewNode.getSize().x, 0);
                    }
                }.bind(this), 100)
            }
        }else if( coordinates.left < leftPoint ){
            if (!this.dragColInterval) {
                this.dragColInterval = setInterval(function () {
                    if( viewAreaNode.getScroll().x - 15  > 0 ){
                        viewAreaNode.scrollTo(viewAreaNode.getScroll().x - 15, 0);
                    }else{
                        viewAreaNode.scrollTo(0, 0);
                    }
                }.bind(this), 100)
            }
        }else{
            if( this.dragColInterval ){
                clearInterval( this.dragColInterval );
                this.dragColInterval = null;
            }
        }
    },
    _dragIn : function(){   //移动时鼠标进入 
        this.resizeNode.setStyles( this.view.css.columnResizeNode_dragIn );
    },
    _dragOut : function(){  //移动时鼠标移出
        this.resizeNode.setStyles( this.view.css.columnResizeNode );
    },
    _dragDrop : function(){ //移动到该对象时鼠标松开
        this.resizeNode.setStyles( this.view.css.columnResizeNode );
    },
    _dragComplete: function( toNode ){ //拖拽完成
        this.node.inject(toNode,"after");
        this.setCurrent();
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
    },
    _dragCancel: function(){  //拖拽取消
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
    },

    _setMoveNodePosition: function(e){
        var x = e.page.x+2;
        var y = e.page.y+2;
        this.moveNode.positionTo(x, y);
    },

    showProperty: function(){
        this.view.hidePropertyContent();
        this.view.propertyTitleNode.set("text", this.view.lp.columnProperty);
        if (!this.property){
            this.property = new MWF.xApplication.cms.ViewDesigner.View.ColumnProperty(this, this.view.propertyContentArea, this.designer, {
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

    setPropertiesOrStyles: function(name){
        if (name=="styles"){
            this.setCustomStyles();
        }
        if (name=="properties"){
            this.node.setProperties(this.data.properties);
        }
    },
    setCustomStyles: function(){
        var border = this.node.getStyle("border");
        this.node.clearStyles();
        this.node.setStyles(this.css.moduleNode);

        if (this.initialStyles) this.node.setStyles(this.initialStyles);
        this.node.setStyle("border", border);

        Object.each(this.data.styles, function(value, key){
            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                this.node.setStyle(key, value);
            }
        }.bind(this));
    },

    setSortIcon : function(){
        if( this.sortIconNode ){
            this.sortIconNode.setStyle("display","inline");
        }else{
            this.sortIconNode = new Element("div",{"styles":this.css.sortIconNode}).inject( this.contentTitleNode, "before" );
        }
    },
    cancelSortIcon : function(){
        this.sortIconNode.setStyle("display","none");
    },
    setAlignIcon: function(){
        if( this.alignIconNode )this.alignIconNode.destroy();
        if( this.data.align == "left" ){
            this.alignIconNode = new Element("div",{"styles":this.css.alignleftNode}).inject( this.contentTitleNode, "after" );
        }else if( this.data.align == "right" ){
            this.alignIconNode = new Element("div",{"styles":this.css.alignrightNode}).inject( this.contentTitleNode, "after" );
        }
    },
    setOperation : function(name, title, image, imageOver, action){
        this.optionNodes = this.optionNodes || {};
        var _self = this;
        var path = this.view.path +this.view.options.style+"/operationIcon/";
        if( !this.optionNodes[name] ){
            if( this.contentTitleNode.get("text") == this.view.lp.noTitle ){
                this.contentTitleNode.set("text","");
            }
            var node = this.optionNodes[name] = new Element("div", {"styles": this.view.css.operationNode, "title": title }).inject(this.contentNode, "bottom" );
            node.setStyle("background-image","url("+path+image+")");
        }
    },
    deleteOperation : function( name ) {
        if (this.optionNodes && this.optionNodes[name]) {
            this.optionNodes[name].destroy();
            this.optionNodes[name] = null;
            delete this.optionNodes[name];
        }
        flag = false;
        for (var op in this.optionNodes) {
            flag = true;
        }
        if (!flag) {
            if (this.contentTitleNode.get("text") == "") {
                this.contentTitleNode.set("text", this.view.lp.noTitle );
            }
        }
    },
    delete : function(callback){
        if( !this.data.isNew && this.data.id ){
            this.view.actions.deleteViewColumn( this.data.id, function(json){
                if(callback)callback();
            }.bind(this));
        }
    },
    getData: function(){
        var data = {};
        data.id = this.data.id;
        data.isNew = this.data.isNew;
        data.viewId = this.view.data.id;
        data.fieldTitle = this.data.title;
        data.fieldName = this.data.value;
        data.xshowSequence = this.view.data.relativeForm.id;
        return data;
    },
    save : function(callback){
        var flag = true;
        if( this.data.value && this.data.value!="" ){
            var data = {};
            data.id = this.data.id;
            data.isNew = this.data.isNew;
            data.viewId = this.view.data.id;
            data.fieldTitle = this.data.title;
            data.fieldName = this.data.value;
            data.xshowSequence = this.view.data.relativeForm.id;
            this.view.actions.saveViewColumn( data, function(json){
                //this.data.id = json.data.id;
                this.data.isNew = false;
                if(callback)callback();
            }.bind(this), function(){
                flag = false;
            }.bind(this), false );
        }else{ //如果字段为空，且已经保存过，则删除
            if( !this.data.isNew ){
                this["delete"]( callback );
                this.data.isNew = true;
            }
        }
        return flag;
    }

});


MWF.xApplication.cms.ViewDesigner.View.HeadBar = new Class({

    initialize: function(view){
        this.view = view;
        this.designer = view.designer;
        this.container = view.viewNode;
        this.isHeadBar = true;
        this.load();
    },
    load: function(){
        this.createNodes();
    },
    createNodes : function(){
        this.node = new Element("div.column", {"styles": this.view.css.headBarNode } );
        this.node.store("column", this);

        this.node.inject(this.container);

        this.headBarContentNode = new Element("div", {"styles": this.view.css.headBarContentNode } ).inject(this.node);

        this.resizeNode = new Element("div",{"styles":this.view.css.headBarResizeNode}).inject(this.node);

    },

    _dragIn : function(){   //移动时鼠标进入
        this.resizeNode.setStyles( this.view.css.headBarResizeNode_dragIn );
    },
    _dragOut : function(){  //移动时鼠标移出
        this.resizeNode.setStyles( this.view.css.headBarResizeNode );
    },
    _dragDrop : function(){ //移动到该对象时鼠标松开
        this.resizeNode.setStyles( this.view.css.headBarResizeNode );
    }
});


MWF.xApplication.cms.ViewDesigner.View.ColumnProperty = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function(column, propertyNode, designer, options){

        this.setOptions(options);
        this.column = column;
        this.data = column.data;
        this.css = column.css;
        this.lp = column.view.lp;

        this.designer = designer;

        this.propertyNode = propertyNode;
    },

    load: function(){
        this.fireEvent( "queryLoad" );
        this.fireEvent( "postLoad" );
    },
    editProperty: function(td){
    },
    show: function(){
        if (!this.propertyContent){
            this.createNode();
            //this.loadEventsEditor( this.eventsNode, this.data.events );
        }else{
            this.propertyContent.setStyle("display", "block");
        }
    },
    hide: function(){
        //this.JsonTemplate = null;
        //this.propertyNode.set("html", "");
        if (this.propertyContent) this.propertyContent.setStyle("display", "none");
    },
    createNode : function(){
        var _self = this;
        this.propertyContent = new Element("div", {"styles": this.css.columnPropertyContent }).inject(this.propertyNode);

        this.basePropertyNode = new Element("div" );
        //this.eventsNode = new Element("div");

        MWF.require("MWF.widget.Tab", function(){
            this.propertyTab = new MWF.widget.Tab(this.propertyContent, {"style": "moduleList"});
            this.propertyTab.load();

            var page = this.propertyTab.addTab(this.basePropertyNode, this.lp.base, false);
            this.setScrollBar(page.contentNodeArea, "small", null, null);

            //page = this.propertyTab.addTab(this.eventsNode, this.lp.events, false);
            //this.setScrollBar(page.contentNodeArea, "small", null, null);

            this.propertyTab.pages[0].showTab();
        }.bind(this));

        var table = new Element("table", { "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.editTable, "class" : "editTable"}).inject( this.basePropertyNode );

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.columnTitle  }).inject(tr);
        var td = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue }).inject(tr);
        this.columnTitleNode = new Element("input", {
            "type" : "text",
            "class" : "editTableInput",
            "styles" : this.css.editTableInput,
            "value" : this.data.title
        }).inject(td);
        this.columnTitleNode.addEvents({
            "change" : function(){
                var val = this.columnTitleNode.get("value");
                this.data.title = val;
                this.column.contentTitleNode.set("text",val);
            }.bind(this)
        });

        var tr = new Element("tr").inject(table);
        var td = new Element("td", { "class" : "editTableTitle",  "styles" : this.css.editTableTitle  }).inject(tr);
        var td = new Element("td", {  "class" : "editTableValue",  "styles" : this.css.editTableValue  }).inject(tr);
        this.loadSort(td);

        var tr = new Element("tr").inject(table);
        var td = new Element("td", { "class" : "editTableTitle",  "styles" : this.css.editTableTitle,  "text" : this.lp.columnValue  }).inject(tr);
        var td = new Element("td", {  "class" : "editTableValue",  "styles" : this.css.editTableValue  }).inject(tr);
        this.columnValueNode = this.getFieldSelectElement();
        this.columnValueNode.inject(td);
        this.columnValueNode.addEvent("change",function(){
            this.data.value = this.column.view.getSelectValue(this.columnValueNode);
        }.bind(this));

        var tr = new Element("tr").inject(table);
        var td = new Element("td", { "class" : "editTableTitle",  "styles" : this.css.editTableTitle,  "text" : this.lp.columnWidth  }).inject(tr);
        var td = new Element("td", {  "class" : "editTableValue",  "styles" : this.css.editTableValue  }).inject(tr);

        if( this.data.widthPer ){
            this.columnPercentageWidthNode = new Element("span",{"text": this.data.widthPer }).inject(td);
        }else{
            this.columnPercentageWidthNode = new Element("span",{"text": Math.round( this.data.width/this.column.view.getColumnsWidth() * 100) }).inject(td);
        }
        if( this.data.widthType == "px"  ){
            this.columnPercentageWidthNode.setStyle("display","none");
        }

        this.columnWidthNode = new Element("input", {
            "type" : "text",
            "class" : "editTableInput",
            "styles" : this.css.editTableInputNoWidth,
            "value" : this.data.width
        }).inject(td);
        this.columnWidthNode.setStyles({"width":"50px"});
        if( this.data.widthType != "px"  ){
            this.columnWidthNode.setStyle("display","none");
        }
        this.columnWidthNode.addEvents({
            "change" : function(){
                var width = Math.round(this.value);
                if( !isNaN( width ) ){
                    if( width > 10 ){
                        _self.column.node.setStyle("width",width);
                        _self.column.contentNode.setStyle("width",width-10);
                        _self.data.width = width;
                        _self.column.view.setViewNodeWidth();
                    }
                }
            }
        });

        this.columnWidthTypeNode = new Element("select").inject( td  );
        new Element("option" , {"value": "percentage", "text":this.lp.percentage }).inject(this.columnWidthTypeNode);
        var option = new Element("option" , {"value": "px", "text":this.lp.px }).inject(this.columnWidthTypeNode);
        if( this.data.widthType == "px"  )option.selected = true;
        this.columnWidthTypeNode.addEvents({
            "change" : function(){
                for(var i=0; i<this.options.length;i++){
                    option = this.options[i];
                    if(option.selected){
                        _self.data.widthType = option.value;
                        if( option.value == "percentage" ){
                            _self.widthType = "percentage";
                            _self.columnWidthNode.setStyle("display","none");
                            _self.columnPercentageWidthNode.setStyle("display","inline");
                            var per = Math.round(_self.column.node.getSize().x / _self.column.view.getColumnsWidth() * 100);
                            _self.columnPercentageWidthNode.set("text", per  );
                            _self.data.widthPer = per;
                            _self.column.view.setViewNodeWidth();
                        }else{
                            _self.widthType = "px";
                            _self.columnWidthNode.setStyle("display","inline");
                            var width = _self.column.node.getSize().x;
                            _self.columnWidthNode.set("value",width);
                            _self.data.width = width;
                            _self.columnPercentageWidthNode.setStyle("display","none");
                            _self.column.view.setViewNodeWidth();
                        }
                    }
                }
            }
        });

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.columnAlign  }).inject(tr);
        var td = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue }).inject(tr);
        this.columnAlignNode = this.loadAlign(td);
        this.columnAlignNode.addEvent("change",function(){
            this.data.align = this.column.view.getSelectValue(this.columnAlignNode);
            this.column.setAlignIcon();
        }.bind(this));

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "class" : "editTableTitle", "styles" : this.css.editTableTitle, "text" : this.lp.action  }).inject(tr);
        var td = new Element("td", { "class" : "editTableValue", "styles" : this.css.editTableValue }).inject(tr);
        this.loadOperation(td);

    },
    loadSort : function( container ){
        var _self = this;
        var node = new Element("span", {"styles":this.css.propertyCheckBox } ).inject( container );
        var input = new Element("input",{
            "type" : "checkbox",
            "value" : "yes"
        }).inject( node );
        if( this.data.sortByClickTitle == "yes" )input.checked = true;
        new Element("span" , { "text" : this.lp.sortByClickTitle } ).inject( node );
        input.addEvents({
            "click" : function(el){
                if( this.checked ){
                    _self.data.sortByClickTitle = "yes";
                    _self.column.setSortIcon()
                }else{
                    _self.data.sortByClickTitle = "no";
                    _self.column.cancelSortIcon()
                }
            }
        })
    },
    getFieldSelectElement : function(){
        var obj = new Element("select");
        new Element("option", {
            "value":"",
            "text":""
        }).inject(obj);
        this.column.view.documentFields.concat(this.column.view.formFields).each(function( field ){
            var opt = new Element("option", {
                "value":field.name,
                "text":field.name
            }).inject(obj);
            if( this.data.value == field.name ){
                opt.selected = true;
            }
        }.bind(this));
        return obj;
    },
    loadEventsEditor: function( eventsNode, eventsObj ){
        MWF.xDesktop.requireApp("cms.FormDesigner", "widget.EventsEditor", function(){
            var eventsEditor = new MWF.xApplication.cms.FormDesigner.widget.EventsEditor( eventsNode, this.designer, {
                "helpStyle" : "cmsViewColumn",
                "maxObj": this.column.view.node
            });
            eventsEditor.load( eventsObj );
        }.bind(this));
    },
    loadAlign : function( container ){
        var obj = new Element("select").inject(container);
        var columnAlignValues = this.lp.columnAlignValue.split(",");
        var columnAlignTexts = this.lp.columnAlignText.split(",");
        columnAlignValues.each(function( v, i ){
            var opt = new Element("option", {
                "value":v,
                "text":columnAlignTexts[i]
            }).inject(obj);
            if( this.data.align == v ){
                opt.selected = true;
            }
        }.bind(this));
        return obj;
    },
    loadOperation : function( container ){
        var _self = this;
        this.data.operation = this.data.operation || {};
        var config = this.column.view.getOperationConfig();
        if( config.default ){
            for( var name  in config.default ){
                var op = config.default[name];
                op.name = name;
                op.text = _self.lp[op.title] ? _self.lp[op.title] : op.title;
                var node = new Element("span", {"styles":this.css.propertyCheckBox } ).inject( container );
                var input = new Element("input",{
                    "type" : "checkbox",
                    "value" : op.name
                }).inject( node );
                if( this.data.operation[name] )input.checked = true;
                new Element("span" , { "text" : op.text} ).inject( node );
                input.store("op", op );
                input.addEvents({
                    "click" : function(el){
                        var op = this.retrieve("op");
                        if( this.checked ){
                            _self.data.operation[op.name] = op;
                            _self.column.setOperation( op.name,op.text,op.icon,op.iconOver, op.action )
                        }else{
                            if( _self.data.operation[op.name] ){
                                delete _self.data.operation[op.name];
                            }
                            _self.column.deleteOperation(op.name)
                        }
                    }
                })
            }
        }
    }

});



MWF.xApplication.cms.ViewDesigner.View.NewName = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "blue",
        "width": 700,
        //"height": 300,
        "height": "220",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "新列表名称"
    },
    _createTableContent: function () {

        var html = "<table width='80%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin: 20px auto 0px auto; '>" +
            "<tr><td styles='formTableTitle' lable='name' width='25%'></td>" +
            "    <td styles='formTableValue' item='name' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data || {}, {
                isEdited: true,
                style : "cms",
                hasColon : true,
                itemTemplate: {
                    name: { text : "名称", notEmpty : true }
                }
            }, this.app);
            this.form.load();

        }.bind(this), null, true)

    },
    ok: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            this.fireEvent("save", [data, function(){
                this.close();
            }.bind(this)])
        }
    }
});