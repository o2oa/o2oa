MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xDesktop.requireApp("CRM", "lp."+MWF.language, null, false);

MWF.xApplication.CRM.Explorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isAdmin": false,
        "searchKey" : ""
    },

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_CRM/$Explorer/";
        this.cssPath = "/x_component_CRM/$Explorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    initData: function(){
        this.toolItemNodes = [];
    },
    reload: function(){
        this.node.empty();
        this.load();
    },
    load: function(){
        this.loadToolbar();
        this.loadContentNode();

        this.loadView();
        this.setNodeScroll();

    },
    destroy : function(){
        this.node.empty();
        delete  this;
    },
    loadToolbar: function(){
        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        this.toolbarNode.inject(this.node);

        var toolbarUrl = this.path+"toolbar.json";
        MWF.getJSON(toolbarUrl, function(json){
            json.each(function(tool){
                this.createToolbarItemNode(tool);
            }.bind(this));
        }.bind(this));
        //this.createSearchElementNode();
    },
    createToolbarItemNode : function( tool ){
        var toolItemNode = new Element("div", {
            "styles": (tool.styles && this.css[tool.styles]) ? this.css[tool.styles] : this.css.toolbarItemNode
        });
        if( tool.id ){
            toolItemNode.set( 'name' , tool.id );
        }
        toolItemNode.store("toolData", tool );

        if( tool.icon ){
            var iconNode =  new Element("div", {
                "styles": this.css.toolbarItemIconNode
            }).inject(toolItemNode);
            iconNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/"+tool.icon+")");
        }

        if( tool.title ){
            var textNode =  new Element("div", {
                "styles": this.css.toolbarItemTextNode,
                "text": tool.title
            });
            if( tool.text )textNode.set("title", tool.text);
            textNode.inject(toolItemNode);
        }

        toolItemNode.inject(this.toolbarNode);

        this.toolItemNodes.push(toolItemNode);

        this.setToolbarItemEvent(toolItemNode);

    },
    setToolbarItemEvent:function(toolItemNode){
        var _self = this;
        toolItemNode.addEvents({
            "click": function () {
                var data = this.retrieve("toolData");
                if( _self[data.action] )_self[data.action].apply(_self,[this]);
            }
        })
    },
    loadContentNode: function(){
        this.elementContentNode = new Element("div.elementContentNode", {
            "styles": this.css.elementContentNode
        }).inject(this.node);
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));

    },
    loadView : function(){
        this.view = new MWF.xApplication.CRM.Explorer.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.load();
        this.setContentSize();
    },

    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : {"x":0,"y":0};
        var titlebarSize = this.app.titleBar ? this.app.titleBar.getSize() : {"x":0,"y":0};
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

        var filterConditionSize = this.filterConditionNode ? this.filterConditionNode.getSize() : {"x":0,"y":0};
        var height = nodeSize.y-toolbarSize.y-pt-pb-filterConditionSize.y-titlebarSize.y;
        //this.elementContentNode.setStyle("height", ""+height+"px");
        this.elementContentNode.setStyle("height", "360px");
        this.elementContentNode.setStyle("width", "700px");

        this.pageCount = (height/30).toInt()+5;

        this._setContentSize();
        if (this.view && this.view.items.length<this.pageCount){
            this.view.loadElementList(this.pageCount-this.view.items.length);
        }
    },
    _setContentSize: function(){

    },
    setNodeScroll: function(){
        var _self = this;
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.elementContentNode, {
                "indent": false,"style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
                "onScroll": function(y){
                    var scrollSize = _self.elementContentNode.getScrollSize();
                    var clientSize = _self.elementContentNode.getSize();
                    var scrollHeight = scrollSize.y-clientSize.y;
                    if (y+200>scrollHeight) {
                        if (!_self.view.isItemsLoaded) _self.view.loadElementList();
                    }
                }
            });
        }.bind(this));
    }
});


MWF.xApplication.CRM.Explorer.View = new Class({

    initialize: function( container, app,explorer, searchKey ){
        this.container = container;
        this.app = app;
        this.explorer = explorer;
        this.css = explorer.css;
        this.actions = explorer.actions;
        this.searchKey = searchKey;

        this.listItemUrl = this.explorer.path+"listItem.json";
    },
    initData: function(){
        this.items=[];
        this.documents = {};
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        this.count = 0;
        //this.controllers =[];
    },
    load : function(){
        this.initData();

        this.node = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.container);

        this.table = new Element("table",{ "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "class" : "editTable"}).inject(this.node);
        this.initSortData();
        this.createListHead();
        this.loadElementList();
    },
    initSortData : function(){
        this.sortField = null;
        this.sortType = null;
        this.sortFieldDefault = null;
        this.sortTypeDefault = null;
    },
    clear: function(){
        this.documents = null;
        MWF.release(this.items);
        this.items=[];
        this.documents = {};
        this.container.empty();
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        //this.count = 0;
    },
    reload: function(){
        this.clear();
        this.node = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.container);
        this.table = new Element("table",{ "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "class" : "editTable"}).inject(this.node);
        this.createListHead();
        this.loadElementList();
    },
    resort : function(th){
        this.sortField = th.retrieve("sortField");
        var sortType = th.retrieve("sortType");
        //th.eliminate(sortType);
        if( sortType == "" ){
            this.sortType = "asc";
        }else if( this.sortType == "asc" ){
            this.sortType = "desc";
        }else{
            this.sortField = null;
            this.sortType = null;
        }
        this.reload();
    },
    createListHead : function(){
        var _self = this;
        var headNode = new Element("tr", {"styles": this.css.listHeadNode}).inject(this.table);
        MWF.getJSON( this.listItemUrl, function(json){
            this.listItemTemplate = json;
            json.each(function(cell){
                var isShow = true;
                if( cell.access ){
                    if( cell.access == "admin" && !this.explorer.options.isAdmin ){
                        isShow = false;
                    }
                }
                if(isShow) {
                    var th = new Element("th", {
                        "styles": this.css[cell.headStyles],
                        "text": cell.title,
                        "width": cell.width
                    }).inject(headNode);
                    if( cell.name == "checkbox" ){
                        this.checkboxElement = new Element("input",{
                            "type" : "checkbox"
                        }).inject( th );
                        this.checkboxElement.addEvent("click",function(){
                            this.selectAllCheckbox()
                        }.bind(this))
                    }
                    if( cell.defaultSort && cell.defaultSort != "" ){
                        this.sortFieldDefault = cell.name;
                        this.sortTypeDefault = cell.defaultSort;
                    }
                    if( cell.sort && cell.sort != "" ){
                        th.store("sortField",cell.name);
                        if( this.sortField  == cell.name && this.sortType!="" ){
                            th.store("sortType",this.sortType);
                            this.sortIconNode = new Element("div",{
                                "styles": this.sortType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                            }).inject( th, "top" );
                        }else{
                            th.store("sortType","");
                            this.sortIconNode = new Element("div",{"styles":this.css.sortIconNode}).inject( th, "top" );
                        }
                        th.setStyle("cursor","pointer");
                        th.addEvent("click",function(){
                            _self.resort( this );
                        })
                    }
                }
            }.bind(this));
        }.bind(this),false);
    },
    selectAllCheckbox : function(){
        var flag = this.checkboxElement.get("checked");
        this.items.each( function( it ){
            if( it.checkboxElement )it.checkboxElement.set("checked",flag );
        }.bind(this))
    },
    loadElementList: function(count){
        if (!this.isItemsLoaded){
            if (!this.isItemLoadding){
                this.isItemLoadding = true;
                this._getCurrentPageData(function(json){
                    //if( !json.data )return;
                    json.data = json.data || [];
                    var length = json.count;  //|| json.data.length;

                    //if (!this.isCountShow){
                    //    this.filterAllProcessNode.getFirst("span").set("text", "("+this.count+")");
                    //    this.isCountShow = true;
                    //}
                    if ( length <=this.items.length){
                        this.isItemsLoaded = true;
                    }
                    json.data.each(function(data){
                        if (!this.documents[data.id]){
                            var item = this._createItem(data);
                            this.items.push(item);
                            this.documents[data.id] = item;
                        }
                    }.bind(this));

                    this.isItemLoadding = false;

                    if (this.loadItemQueue>0){
                        this.loadItemQueue--;
                        this.loadElementList();
                    }
                }.bind(this), count);
            }else{
                this.loadItemQueue++;
            }
        }
    },
    _createItem: function(data){
        return new MWF.xApplication.CRM.Explorer.Document(this.table, data, this.explorer, this);
    },
    _getCurrentPageData: function(callback, count){
        /* if(!count)count=20;
         var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
         var data = {
             "catagoryIdList": [
                 {
                     "name": "catagoryId",
                     "value": this.explorer.categoryData.id
                 }
             ],
             "statusList": [
                 {
                     "name": "docStatus",
                     "value": this.explorer.options.status
                 }
             ]
         }
         if( this.searchKey && this.searchKey!="" ){
             data.titleList = [{
                 "name" :"title",
                 "value" : this.searchKey
             }]
         }
         if (this.filter && this.filter.filter ){
             var filterResult = this.filter.getFilterResult();
             for(var f in filterResult ){
                 data[f] = filterResult[f];
             }
             this.actions.listDocumentFilterNext(id, count || this.pageCount, data, function(json){
                 if (callback) callback(json);
             });
         }else{
             this.actions.listDocumentFilterNext(id, count || this.pageCount, data, function(json){
                 if (callback) callback(json);
             });
         }*/
    },

    _removeDocument: function(documentData, all){
        //var id = document.data.id;
        //this.actions.removeDocument(id, function(json){
        //    //json.data.each(function(item){
        //    this.items.erase(this.documents[id]);
        //    this.documents[id].destroy();
        //    MWF.release(this.documents[id]);
        //    delete this.documents[id];
        //    this.app.notice(this.app.lp.deleteDocumentOK, "success");
        //    // }.bind(this));
        //}.bind(this));
    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

    }
});

MWF.xApplication.CRM.Explorer.Document = new Class({
    initialize: function(container, data, explorer, view){
        this.explorer = explorer;
        this.app = explorer.app;
        this.data = data;
        this.container = container;
        this.view = view;
        this.css = this.explorer.css;

        this.load();
    },

    load: function(){
        this.node = new Element("tr", {"styles": this.css.documentItemNode});

        this.node.inject(this.container);

        //this.documentAreaNode =  new Element("td", {"styles": this.css.documentItemDocumentNode}).inject(this.node);

        this.view.listItemTemplate.each(function(cell){
            var isShow = true;
            if( cell.access ){
                if( cell.access == "admin" && !this.explorer.options.isAdmin ){
                    isShow = false;
                }
            }
            if(isShow){
                var value;
                if( cell.item.substr( 0, "function".length ) == "function" ){
                    eval( "var fun = " + cell.item );
                    value = fun.call( this, this.data  );
                }else if( typeOf(this.data[cell.item]) == "number" ){
                    value = this.data[cell.item];
                }else{
                    value = this.data[cell.item] ? this.data[cell.item] : "";
                }
                var td = this[cell.name] = new Element("td",{
                    "styles":this.css[cell.contentStyles],
                    "text" : value
                }).inject(this.node);
                if( cell.name == "actions" && typeOf( cell.sub )=="array"){
                    this.setActions( this[cell.name], cell.sub );
                }
                if( cell.name == "checkbox" ){
                    var showCheckBox = true;
                    if( cell.condition  && cell.condition.substr( 0, "function".length ) == "function" ) {
                        eval("var fun = " + cell.condition);
                        showCheckBox = fun.call(this, this.data);
                    }
                    if( showCheckBox ){
                        this.checkboxElement = new Element("input",{
                            "type" : "checkbox"
                        }).inject( td );
                        this.checkboxElement.addEvent("click",function(ev){
                            ev.stopPropagation();
                        }.bind(this));
                        td.addEvent("click",function(ev){
                            this.checkboxElement.set("checked", !this.checkboxElement.get("checked") );
                            ev.stopPropagation();
                        }.bind(this))
                    }
                }
            }
        }.bind(this));

        this.node.addEvents({
            "mouseover": function(){if (!this.readyRemove) this.node.setStyles(this.css.documentItemDocumentNode_over);}.bind(this),
            "mouseout": function(){if (!this.readyRemove) this.node.setStyles(this.css.documentItemDocumentNode);}.bind(this),
            "click": function(e){
                this.openDocument(e);
            }.bind(this)
        });
    },
    //setEvents: function(){
    //
    //    this.node.addEvents({
    //        "mouseover": function(){if (!this.readyRemove) this.node.setStyles(this.css.documentItemDocumentNode_over);}.bind(this),
    //        "mouseout": function(){if (!this.readyRemove) this.node.setStyles(this.css.documentItemDocumentNode);}.bind(this),
    //        "click": function(e){
    //            this.openDocument(e);
    //        }.bind(this)
    //    });
    //
    //    if (this.deleteNode){
    //        this.deleteNode.addEvents({
    //            "mouseover": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
    //            "mouseout": function(){this.deleteNode.setStyles(this.css.actionDeleteNode);}.bind(this),
    //            "mousedown": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_down);}.bind(this),
    //            "mouseup": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
    //            "click": function(e){
    //                this.remove(e);
    //                e.stopPropagation();
    //            }.bind(this)
    //        });
    //    }
    //},
    setActions: function( actionsNode, data ){
        var _self = this;
        data.each(function( d ){
            if( !d.action || !this[d.action])return;

            if( d.condition ){
                if( d.condition.substr( 0, "function".length ) == "function" ) {
                    eval("var fun = " +  d.condition );
                    if( ! fun.call(this, this.data) ){
                        return;
                    }
                }
            }

            var node = this[d.action+"Node"] = new Element("div", {"title": d.title}).inject(actionsNode);
            var styles, overStyles, downStyles;
            if( typeOf( d.styles) == "string" ) styles = this.css[d.styles];
            if( typeOf(d.styles) == "object" ) styles = d.styles;

            if( typeOf( d.overStyles) == "string" ) overStyles = this.css[d.overStyles];
            if( typeOf(d.overStyles) == "object" ) overStyles = d.overStyles;

            if( typeOf( d.downStyles) == "string" ) downStyles = this.css[d.downStyles];
            if( typeOf(d.downStyles) == "object" ) downStyles = d.downStyles;

            if( styles  )node.setStyles( styles );
            if( overStyles && styles ){
                node.addEvent( "mouseover", function(ev){ ev.target.setStyles( this.styles ); }.bind({"styles" : overStyles }) );
                node.addEvent( "mouseout", function(ev){  ev.target.setStyles( this.styles ); }.bind({"styles" : styles}) );
            }
            if( downStyles && ( overStyles || styles)){
                node.addEvent( "mousedown", function(ev){  ev.target.setStyles( this.styles ); }.bind({"styles" : downStyles }) );
                node.addEvent( "mouseup", function(ev){  ev.target.setStyles( this.styles );  }.bind({"styles" : overStyles || styles }) )
            }

            if( this[d.action] ){
                node.addEvent("click", function(ev){
                    this.fun.call( _self, ev );
                    ev.stopPropagation();
                }.bind({fun : this[d.action]}))
            }
        }.bind(this));

        //if( this.actionAreaNode ){
        //    if( this.explorer.options.isAdmin ){
        //        this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.app.lp.delete}).inject(this.actionAreaNode);
        //    }
        //}
    },
    openDocument: function(e){
        //var options = {"documentId": this.data.id }//this.explorer.app.options.application.allowControl};
        //this.explorer.app.desktop.openApplication(e, "cms.Document", options);
        this.view._openDocument( this.data );
    },
    remove: function(e){
        var lp = this.app.lp;
        var text = lp.deleteDocument.replace(/{title}/g, this.data.title);
        var _self = this;
        this.node.setStyles(this.css.documentItemDocumentNode_remove);
        this.readyRemove = true;
        this.explorer.app.confirm("warn", e, lp.deleteDocumentTitle, text, 350, 120, function(){

            //var inputs = this.content.getElements("input");
            //var flag = "";
            //for (var i=0; i<inputs.length; i++){
            //    if (inputs[i].checked){
            //        flag = inputs[i].get("value");
            //        break;
            //    }
            //}
            //if (flag){
            //if (flag=="all"){
            //_self.explorer.removeDocument(_self, true);
            //}else{
            _self.view._removeDocument(_self.data, false);
            //}
            this.close();
            //}else{
            //    this.content.getElement("#deleteDocument_checkInfor").set("text", lp.deleteAllDocumentCheck).setStyle("color", "red");
            //}
        }, function(){
            _self.node.setStyles(_self.css.documentItemDocumentNode);
            _self.readyRemove = false;
            this.close();
        });
    },

    destroy: function(){
        this.node.destroy();
    }
});

MWF.xApplication.CRM.Explorer.PopupForm = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "width": "500",
        "height": "400"
    },
    initialize: function( explorer, data,options){
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.data = data || {};
        this.css = this.explorer.css;

        this.load();
    },
    load: function(){

    },

    open: function(e){
        this.isNew = false;
        this.isEdited = false;
        this._open();
    },
    create: function(){
        this.isNew = true;
        this._open();
    },
    edit: function(){
        this.isEdited = true;
        this._open();
    },
    _open : function(){
        this.formMaskNode = new Element("div", {
            "styles": this.css.formMaskNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content, "after");

        this.formAreaNode = new Element("div", {
            "styles": this.css.formAreaNode
        });

        this.createFormNode();

        this.formAreaNode.inject(this.formMaskNode, "after");
        this.formAreaNode.fade("in");

        this.setFormNodeSize();
        this.setFormNodeSizeFun = this.setFormNodeSize.bind(this);
        this.addEvent("resize", this.setFormNodeSizeFun);
    },
    createFormNode: function(){
        var _self = this;

        this.formNode = new Element("div", {
            "styles": this.css.formNode
        }).inject(this.formAreaNode);

        this.formIconNode = new Element("div", {
            "styles": this.isNew ? this.css.formNewNode : this.css.formIconNode
        }).inject(this.formNode);


        this.formFormNode = new Element("div", {
            "styles": this.css.formFormNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div", {
            "styles": this.css.formTableContainer
        }).inject(this.formFormNode);

        this.formTableArea = new Element("div", {
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);


        this._createTableContent();
        //formFormNode.set("html", html);

        //this.setScrollBar(this.formTableContainer)

        this._createAction();
    },
    _createTableContent: function(){

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
            "<tr><td styles='formTabelTitle' lable='empName'></td>"+
            "    <td styles='formTableValue' item='empName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='unitName'></td>"+
            "    <td styles='formTableValue' item='unitName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='recordDateString'></td>"+
            "    <td styles='formTableValue' item='recordDateString'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='status'></td>"+
            "    <td styles='formTableValue' item='status'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='appealReason'></td>"+
            "    <td styles='formTableValue' item='appealReason'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='appealDescription'></td>"+
            "    <td styles='formTableValue' item='appealDescription'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='opinion1'></td>"+
            "    <td styles='formTableValue' item='opinion1'></td></tr>" +
            "</table>";
        this.formTableArea.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, {empName:"xadmin"}, {
                isEdited : this.isEdited || this.isNew,
                itemTemplate : {
                    empName : { text:"姓名", type : "innertext" },
                    unitName : { text:"部门",  tType : "unit", notEmpty:true },
                    recordDateString : { text:"日期",  tType : "date"},
                    status : {  text:"状态", tType : "number" },
                    appealReason : {
                        text:"下拉框",
                        type : "select",
                        selectValue : ["测试1","测试2"]
                    },
                    appealDescription : {  text:"描述", type : "textarea" },
                    opinion1 : {  text:"测试", type : "button", "value" : "测试" }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options && this.options.width ? this.options.width : "50%";
        if (!height)height = this.options && this.options.height ? this.options.height : "50%";
        if (!top) top = this.options && this.options.top ? this.options.top : 0;
        if (!left) left = this.options && this.options.left ? this.options.left : 0;

        var allSize = this.app.content.getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);
        top = top || parseInt((limitHeight - height) / 2, 10);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.formAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.formNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.formFormNode.setStyles({
            "height": "" + contentHeight + "px"
        });
    },
    //setFormNodeSize: function(){
    //    var size = this.app.node.getSize();
    //    var allSize = this.app.content.getSize();
    //
    //    this.formAreaNode.setStyles({
    //        "width": ""+size.x+"px",
    //        "height": ""+size.y+"px"
    //    });
    //    var hY = size.y*0.8;
    //    var mY = size.y*0.2/2;
    //    this.formNode.setStyles({
    //        "height": ""+hY+"px",
    //        "margin-top": ""+mY+"px"
    //    });
    //
    //    var iconSize =  this.formIconNode ? this.formIconNode.getSize() : {x:0,y:30};
    //    var formHeight = hY*0.8;
    //    if (formHeight>250) formHeight = 250;
    //    var formMargin = hY*0.3/2-iconSize.y;
    //    this.formFormNode.setStyles({
    //        "height": ""+formHeight+"px",
    //        "margin-top": ""+formMargin+"px"
    //    });
    //},
    _createAction : function(){
        this.cancelActionNode = new Element("div", {
            "styles": this.css.formCancelActionNode,
            "text": this.app.lp.cancel
        }).inject(this.formFormNode);


        this.cancelActionNode.addEvent("click", function(e){
            this.cancel(e);
        }.bind(this));

        if( this.isNew || this.isEdited){

            this.okActionNode = new Element("div", {
                "styles": this.css.formOkActionNode,
                "text": this.app.lp.ok
            }).inject(this.formFormNode);

            this.okActionNode.addEvent("click", function(e){
                this.ok(e);
            }.bind(this));
        }
    },
    cancel: function(e){
        this.close();
    },
    close: function(e){
        this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        delete this;
    },
    ok: function(e){
        var data = this.form.getResult(true,",",true,false,true);
        if( data ){
            this._ok( data, function( json ){
                if( json.type == "ERROR" ){
                    this.app.notice( json.message  , "error");
                }else{
                    this.formMaskNode.destroy();
                    this.formAreaNode.destroy();
                    if(this.explorer.view)this.explorer.view.reload();
                    this.app.notice( this.isNew ? this.app.lp.createSuccess : this.app.lp.updateSuccess , "success");
                }
            }.bind(this))
        }
    },
    _ok: function( data, callback ){
        //this.app.restActions.saveDocument( this.data.id, data, function(json){
        //    if( callback )callback(json);
        //}.bind(this));
    }
});
