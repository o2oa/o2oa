MWF.xApplication.CRM = MWF.xApplication.CRM || {};
MWF.xApplication.CRM.Template = MWF.xApplication.CRM.Template || {};
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);

String.implement({

    toDOM: function( container, callback ){
        var wrapper =	this.test('^<the|^<tf|^<tb|^<colg|^<ca') && ['<table>', '</table>', 1] ||
            this.test('^<col') && ['<table><colgroup>', '</colgroup><tbody></tbody></table>',2] ||
            this.test('^<tr') && ['<table><tbody>', '</tbody></table>', 2] ||
            this.test('^<th|^<td') && ['<table><tbody><tr>', '</tr></tbody></table>', 3] ||
            this.test('^<li') && ['<ul>', '</ul>', 1] ||
            this.test('^<dt|^<dd') && ['<dl>', '</dl>', 1] ||
            this.test('^<le') && ['<fieldset>', '</fieldset>', 1] ||
            this.test('^<opt') && ['<select multiple="multiple">', '</select>', 1] ||
            ['', '', 0];
        if( container ){
            var el = new Element('div', {html: wrapper[0] + this + wrapper[1]}).getChildren();
            while(wrapper[2]--) el = el[0].getChildren();
            el.inject( container )
            if( callback )callback( container );
            return el;
        }else{
            var div = new Element('div', {html: wrapper[0] + this + wrapper[1]});
            div.setStyle("display","none").inject( $(document.body) );
            if( callback )callback( div );
            var el = div.getChildren();
            while(wrapper[2]--) el = el[0].getChildren();
            div.dispose();
            return el;
        }
    }

});



MWF.xApplication.CRM.Template.Select = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "800",
        "height": "100%"
    },
    initialize: function (node ,explorer, actions, options) {
        this.setOptions(options);
        this.app = explorer.app;
        this.explorer = explorer;
        this.lp = this.app.lp.template;
        this.actions = this.app.restActions;
        this.path = "/x_component_CRM/Template/";
        this.loadCss();

        this.node = $(node);
        this.actions = actions;
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$Template/" + this.options.style + "/css.wcss";
        this._loadCss();
    },

    load:function(data,callback){
        this._width = this.options.width?this.options.width:230;
        this._height = this.options.height?this.options.height:30;
        this._available = this.options.available?this.options.available:"yes";

        this.createDefault();

        this.explorer.allArrowArr.push(this.selectArrowDiv);

        //this.setList(data,callback);
        if(callback)callback();

    },
    createDefault:function(){
        if(this.node)this.node.empty();
        this.selectValueDiv = new Element("div.selectValueDiv",{
            "styles":this.css.selectValueDiv,
            "id":this.node.get("id")+"Value",
            "text":this.lp.defaultSelect
        }).inject(this.node);

        this.selectArrowDiv = new Element("div.selectArrowDiv",{
            "styles":this.css.selectArrowDiv
        }).inject(this.node);
        this.selectArrowDiv.setStyles({
            "width":this._height+"px",
            "height":this._height+"px"
        });

        this.node.setStyles(this.css.selectDiv);
        this.node.setStyles({
            "width":this._width+"px",
            "height":this._height+"px",
            "background-color":this._available=="no"?"#eeeeee":""
        });
        this.node.set("available",this._available);

        this.selectValueDiv.setStyles({
            "width":(this._width-this._height-10)+"px",
            "height":this._height+"px",
            "line-height":this._height+"px"
        });
    },
    setAddress:function(data,callback){
        data = data || {};
        var _self = this;
        this.node.removeEvents("click");
        this.node.addEvents({
            "click":function(e){
                if(!data)return false;
                if(_self.node.get("available")=="no") return false;
                _self.selectArrowDiv.setStyles({
                    "background":"url(/x_component_CRM/$Template/default/icons/arrow-up.png) no-repeat center"
                });
                if(_self.explorer.listContentDiv)_self.explorer.listContentDiv.destroy();
                if(_self.explorer.listDiv)_self.explorer.listDiv.destroy();
                _self.explorer.listContentDiv = new Element("div.listContentDiv",{"styles":_self.css.listContentDiv,"id":"listContentDiv"}).inject(_self.node);
                _self.explorer.listContentDiv.setStyles({
                    "width":_self.node.getSize().x+"px",
                    "margin-top":(_self.node.getSize().y)+"px",
                    "z-index":"300"
                });

                _self.listDiv = new Element("div.listDiv",{"styles":_self.css.listDiv}).inject(_self.explorer.listContentDiv);
                _self.app.setScrollBar(_self.listDiv);

                data.unshift({
                    "cityname":_self.lp.defaultSelect
                });


                data.each(function(d){
                    var listLi = new Element("li.listLi",{
                        "styles":_self.css.listLi,
                        "text": d.cityname
                    }).inject(_self.listDiv);
                    listLi.setStyles({
                        "color":_self.selectValueDiv.get("text")==listLi.get("text")?"#ffffff":"",
                        "background-color":_self.selectValueDiv.get("text")==listLi.get("text")?"#3d77c1":""
                    });
                    listLi.addEvents({
                        "click":function(ev){


                            _self.node.set("value",this.get("text"));
                            _self.explorer.listContentDiv.destroy();
                            _self.selectArrowDiv.setStyles({"background":"url(/x_component_CRM/$Template/default/icons/arrow.png) no-repeat center"});
                            if(_self.selectValueDiv.get("text")!=this.get("text")){
                                _self.selectValueDiv.set({"text":this.get("text")});
                                if(callback)callback(d);
                            }
                            ev.stopPropagation();


                        },
                        "mouseover":function(){
                            if(this.get("text") != _self.selectValueDiv.get("text")){
                                this.setStyles({
                                    "background-color":"#ccc",
                                    "color":"#ffffff"
                                });
                            }
                        },
                        "mouseout":function(){
                            if(this.get("text") != _self.selectValueDiv.get("text")){
                                this.setStyles({
                                    "background-color":"",
                                    "color":""
                                });
                            }
                        }
                    });
                }.bind(_self));

                data.splice(0,1);

                e.stopPropagation();
            }.bind(this)
        })
    },
    setList:function(data,callback){
        data = data || {};
        var _self = this;
        this.node.removeEvents("click");
        this.node.addEvents({
            "click":function(e){
                if(!data.childNodes)return false;
                if(_self.node.get("available")=="no") return false;
                _self.selectArrowDiv.setStyles({
                    "background":"url(/x_component_CRM/$Template/default/icons/arrow-up.png) no-repeat center"
                });
                if(_self.explorer.listContentDiv)_self.explorer.listContentDiv.destroy();
                if(_self.explorer.listDiv)_self.explorer.listDiv.destroy();
                _self.explorer.listContentDiv = new Element("div.listContentDiv",{"styles":_self.css.listContentDiv,"id":"listContentDiv"}).inject(_self.node);
                _self.explorer.listContentDiv.setStyles({
                    "width":_self.node.getSize().x+"px",
                    "margin-top":(_self.node.getSize().y)+"px",
                    "z-index":"300"
                });

                _self.listDiv = new Element("div.listDiv",{"styles":_self.css.listDiv}).inject(_self.explorer.listContentDiv);
                _self.app.setScrollBar(_self.listDiv);

                data.childNodes.unshift({
                    "configname":_self.lp.defaultSelect
                });

                data.childNodes.each(function(d){
                    var listLi = new Element("li.listLi",{
                        "styles":_self.css.listLi,
                        "text": d.configname
                    }).inject(_self.listDiv);
                    listLi.setStyles({
                        "color":_self.selectValueDiv.get("text")==listLi.get("text")?"#ffffff":"",
                        "background-color":_self.selectValueDiv.get("text")==listLi.get("text")?"#3d77c1":""
                    });
                    listLi.addEvents({
                        "click":function(ev){
                            _self.selectValueDiv.set({"text":this.get("text")});
                            _self.node.set("value",this.get("text"));
                            _self.explorer.listContentDiv.destroy();
                            _self.selectArrowDiv.setStyles({"background":"url(/x_component_CRM/$Template/default/icons/arrow.png) no-repeat center"});
                            if(callback)callback(d);
                            ev.stopPropagation();
                        },
                        "mouseover":function(){
                            if(this.get("text") != _self.selectValueDiv.get("text")){
                                this.setStyles({
                                    "background-color":"#ccc",
                                    "color":"#ffffff"
                                });
                            }
                        },
                        "mouseout":function(){
                            if(this.get("text") != _self.selectValueDiv.get("text")){
                                this.setStyles({
                                    "background-color":"",
                                    "color":""
                                });
                            }
                        }
                    });
                }.bind(_self));

                data.childNodes.splice(0,1);

                e.stopPropagation();
            }.bind(this)
        })

    }
});

MWF.xApplication.CRM.Template.PopupForm = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": 500,
        "height": 450,
        "top": 0,
        "left": 0,
        "hasTop": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasIcon": true,
        "hasScroll" : true,
        "hasBottom": true,
        "hasMask" : true,
        "title": "",
        "draggable": false,
        "maxAction" : "false",
        "closeAction": true,
        "relativeToApp" : true,
        "sizeRelateTo" : "app" //desktop
    },
    initialize: function (explorer, data, options, para) {
        alert("xxx")
        this.setOptions(options);
        this.explorer = explorer;
        if( para ){
            if( this.options.relativeToApp ){
                this.app = para.app || this.explorer.app;
                this.container = para.container || this.app.content;
                this.lp = para.lp || this.explorer.lp || this.app.lp;
                this.css = para.css || this.explorer.css || this.app.css;
                this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = para.container;
                this.lp = para.lp || this.explorer.lp;
                this.css = para.css || this.explorer.css;
                this.actions = para.actions || this.explorer.actions;
            }
        }else{
            if( this.options.relativeToApp ){
                this.app = this.explorer.app;
                this.container = this.app.content;
                this.lp = this.explorer.lp || this.app.lp;
                this.css = this.explorer.css || this.app.css;
                this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = window.document.body;
                this.lp = this.explorer.lp;
                this.css = this.explorer.css;
                this.actions = this.explorer.actions;
            }
        }
        this.data = data || {};

        this.cssPath = "/x_component_CRM/$Template/"+this.options.style+"/popup.wcss";

        this.load();
    },
    load: function () {
        this._loadCss();
    },
    selectPerson: function (showContainer,nameId,fullNameId,count) {
        var options = {
            "type" : "",
            "types": ["person"],
            "values": [],
            "count": count,
            "zIndex": 50000,
            "onComplete": function(items){
                MWF.require("MWF.widget.O2Identity", function(){
                    var invitePersonList = [];
                    var fullPersonList = [];
                    items.each(function(item){
                        var _self = this;
                        if( item.data.distinguishedName.split("@").getLast().toLowerCase() == "i" ){
                            var person = new MWF.widget.O2Identity(item.data, it.form.getItem("invitePersonList").container, {"style": "room"});
                            invitePersonList.push( item.data.distinguishedName );
                        }else{
                            //var person = new MWF.widget.O2Person(item.data, it.form.getItem("invitePersonList").container, {"style": "room"});
                            invitePersonList.push(item.data.name);
                            fullPersonList.push(item.data.distinguishedName);
                        }
                    }.bind(this));
                    document.getElementById(nameId).innerHTML = invitePersonList.join(",");
                    if(fullNameId!=""){
                        document.getElementById(fullNameId).innerHTML = fullPersonList.join(",");
                    }
                }.bind(this));
            }.bind(this)
        };
        var selector = new MWF.O2Selector(showContainer, options);
    },
    _loadCss: function(){
        var css = {};
        var r = new Request.JSON({
            url: this.cssPath,
            secure: false,
            async: false,
            method: "get",
            noCache: false,
            onSuccess: function(responseJSON, responseText){
                css = responseJSON;
                MWF.widget.css[key] = responseJSON;
            }.bind(this),
            onError: function(text, error){
                alert(error + text);
            }
        });
        r.send();

        var isEmptyObject = true;
        for( var key in css ){
            if(key)isEmptyObject = false
        }
        if( !isEmptyObject ){
            this.css = Object.merge(  css, this.css );
        }
    },
    open: function (e) {
        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this._open();
        this.fireEvent("postOpen");
    },
    create: function () {
        this.fireEvent("queryCreate");
        this.isNew = true;
        this._open();
        this.fireEvent("postCreate");
    },
    edit: function () {
        this.fireEvent("queryEdit");
        this.isEdited = true;
        this._open();
        this.fireEvent("postEdit");
    },
    _open: function () {
        if( this.options.hasMask ){
            this.formMaskNode = new Element("div.formMaskNode", {
                "styles": this.css.formMaskNode,
                "events": {
                    "mouseover": function (e) {
                        e.stopPropagation();
                    },
                    "mouseout": function (e) {
                        e.stopPropagation();
                    },
                    "click": function (e) {
                        e.stopPropagation();
                    }
                }
            }).inject( this.container || this.app.content);
        }

        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        });

        this.createFormNode();

        this.formAreaNode.inject(this.formMaskNode || this.container || this.app.content, "after");
        this.formAreaNode.fade("in");

        this.setFormNodeSize();
        this.setFormNodeSizeFun = this.setFormNodeSize.bind(this);
        if( this.app )this.app.addEvent("resize", this.setFormNodeSizeFun);

        if (this.options.draggable && this.formTopNode) {
            var size = (this.container || this.app.content).getSize();
            var nodeSize = this.formAreaNode.getSize();
            this.formAreaNode.makeDraggable({
                "handle": this.formTopNode,
                "limit": {
                    "x": [0, size.x - nodeSize.x],
                    "y": [0, size.y - nodeSize.y]
                }
            });
        }

    },
    createFormNode: function () {
        var _self = this;

        this.formNode = new Element("div.formNode", {
            "styles": this.css.formNode
        }).inject(this.formAreaNode);

        if (this.options.hasTop) {
            this.createTopNode();
        }

        if (this.options.hasIcon) {
            this.formIconNode = new Element("div.formIconNode", {
                "styles": this.isNew ? this.css.formNewNode : this.css.formIconNode
            }).inject(this.formNode);
        }

        this.createContent();
        //formContentNode.set("html", html);

        if (this.options.hasBottom) {
            this.createBottomNode();
        }

        this._setCustom();

        if( this.options.hasScroll ){
            //this.setScrollBar(this.formTableContainer)
            MWF.require("MWF.widget.ScrollBar", function () {
                new MWF.widget.ScrollBar(this.formTableContainer, {
                    "indent": false,
                    "style": "default",
                    "where": "before",
                    "distance": 30,
                    "friction": 4,
                    "axis": {"x": false, "y": true},
                    "onScroll": function (y) {
                        //var scrollSize = _self.viewContainerNode.getScrollSize();
                        //var clientSize = _self.viewContainerNode.getSize();
                        //var scrollHeight = scrollSize.y - clientSize.y;
                        //if (y + 200 > scrollHeight && _self.view && _self.view.loadElementList) {
                        //    if (!_self.view.isItemsLoaded) _self.view.loadElementList();
                        //}
                    }
                });
            }.bind(this));
        }
    },
    _setCustom : function(){

    },
    createTopNode: function () {

        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            if(this.options.hasTopIcon){
                this.formTopIconNode = new Element("div", {
                    "styles": this.css.formTopIconNode
                }).inject(this.formTopNode)
            }

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            if(this.options.hasTopContent){
                this.formTopContentNode = new Element("div.formTopContentNode", {
                    "styles": this.css.formTopContentNode
                }).inject(this.formTopNode);

                this._createTopContent();
            }

        }

    },
    _createTopContent: function () {

    },
    createContent: function () {
        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);

        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea,
            "text":"loading..."
        }).inject(this.formTableContainer);


        this._createTableContent();
    },
    _createTableContent: function () {



    },
    createBottomNode: function () {
        this.formBottomNode = new Element("div.formBottomNode", {
            "styles": this.css.formBottomNode
        }).inject(this.formNode);

        this._createBottomContent()
    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.actionCancel
        }).inject(this.formBottomNode);


        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

    },
    cancel: function (e) {
        this.fireEvent("queryCancel");
        this.close();
        this.fireEvent("postCancel");
    },
    close: function (e) {
        this.fireEvent("queryClose");
        this._close();
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        this.fireEvent("postClose");
        delete this;
    },
    _close: function(){

    },
    ok: function (e) {
        this.fireEvent("queryOk");
        var data = this.form.getResult(true, ",", true, false, true);
        if (data) {
            this._ok(data, function (json) {
                if (json.type == "error") {
                    if( this.app )this.app.notice(json.message, "error");
                } else {
                    if( this.formMaskNode )this.formMaskNode.destroy();
                    this.formAreaNode.destroy();
                    if (this.explorer && this.explorer.view)this.explorer.view.reload();
                    if( this.app )this.app.notice(this.isNew ? this.lp.createSuccess : this.lp.updateSuccess, "success");
                    this.fireEvent("postOk");
                }
            }.bind(this))
        }
    },
    _ok: function (data, callback) {
        //this.app.restActions.saveDocument( this.data.id, data, function(json){
        //    if( callback )callback(json);
        //}.bind(this), function( errorObj ){
        //    var error = JSON.parse( errorObj.responseText );
        //    this.app.notice( error.message, error );
        //}.bind(this));
    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options.width ? this.options.width : "50%";
        if (!height)height = this.options.height ? this.options.height : "50%";
        if (!top) top = this.options.top ? this.options.top : 0;
        if (!left) left = this.options.left ? this.options.left : 0;

        //var appTitleSize = this.app.window.title.getSize();

        var allSize = ( this.container || this.app.content).getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);

        top = top || parseInt((limitHeight - height) / 2, 10); //+appTitleSize.y);
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
        this.formContentNode.setStyles({
            "height": "" + contentHeight + "px"
        });
        this.formTableContainer.setStyles({
            "height": "" + contentHeight + "px"
        });
    }
});



MWF.xApplication.CRM.Template.ComplexView = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "templateUrl": "",
        "scrollEnable" : false,
        "pagingEnable" : true,
        "documentKeyWord" : null,
        "pagingPar" : {
            position : [ "bottom" ], //分页条，上下
            countPerPage : 5,
            visiblePages : 10,
            currentPage : 1,
            currentItem : null,
            hasPagingBar : true,
            hasTruningBar : true,
            // hasNextPage : true,
            // hasPrevPage : true,
            hasReturn : false
        }
    },
    initialize: function (container, openDiv, app, explorer, options, para) {
        this.container = container;
        this.openDiv = openDiv;
        this.explorer = explorer;
        if( para ){
            this.app = app || para.app || this.explorer.app;
            this.lp = para.lp || this.explorer.lp || this.app.lp;
            this.css = para.css || this.explorer.css || this.app.css;
            this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            this.isAdmin = para.isAdmin;
        }else{
            this.app = app || this.explorer.app;
            this.lp = this.explorer.lp || this.app.lp;
            this.css = this.explorer.css || this.app.css;
            this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
        }
        debugger
        if (!options.templateUrl) {
            options.templateUrl = this.explorer.path + "listItem.json"
        } else if (options.templateUrl.indexOf("/") == -1) {
            options.templateUrl = this.explorer.path + options.templateUrl;
        }
        this.setOptions(options);

    },
    initData: function () {
        this.items = [];
        this.documents = {};
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        this.count = 0;
        //this.controllers =[];
    },
    load: function () {
        this.initData();
        this.ayalyseTemplate();

        this.getContentTemplateNode(); //获取template
        this.headTableNode = new Element("div.headTableNode", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);
        //this.selectSearch = this.formatElement(this.headTableNode, this.template.selectSearch)

        this.contentTableNode = new Element("div.contentTableNode", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);
        this.createViewNode();
        this.loadResource(function(){
            this.loadSearchCondition();
            this.useTablePlugins(1,"","");
            if (jQuery( ".headTableNode:has(div)" ).length==0){
                jQuery(".headTableNode").remove();
            }
            //搜索
            var that = this;
            jQuery(".headSearchBottonDiv").on("click", function () {
                var searchText = jQuery(".headSearchInput").val();
                if(searchText!=""){
                    var spage =  parseInt(jQuery(".page-active").attr("value"));
                    //if(jQuery(".laytable-box").length > 0) jQuery(".laytable-box").remove();
                    var searchType ="";
                    if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                        searchType = jQuery(".headTableNode").find(".se-select-name").text();
                    }
                    that.useTablePlugins(1,searchText,searchType);
                }
            });
            jQuery(".headSearchRemoveImg").on("click", function () {
                var spage =  parseInt(jQuery(".page-active").attr("value"));
                var searchType ="";
                if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                    searchType = jQuery(".headTableNode").find(".se-select-name").text();
                }
                that.useTablePlugins(spage,"",searchType);
            });

        }.bind(this))
    },
    loadResource: function ( callback ) {
        if(callback)callback();
        /*COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/assets/css/notifyme.css", function(){
        }.bind(this))
        var baseUrls = [
            "/x_component_CRM/$Template/plugins/jquery.min.js",
        ];
        var fullcalendarUrl = "/x_component_CRM/$Template/plugins/layui/layui.js";
        var langUrl =  "/x_component_CRM/$Template/plugins/table2/table2.js";
        COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/plugins/table2/css/table2.css",function(){
            COMMON.AjaxModule.load(baseUrls, function(){
                jQuery.noConflict();
                COMMON.AjaxModule.load(fullcalendarUrl, function(){
                    COMMON.AjaxModule.load(langUrl, function(){
                        if(callback)callback();
                    }.bind(this));
                }.bind(this));
            }.bind(this))
        }.bind(this))*/

    },
    useTablePlugins: function (cpage,searchText,searchType) {
        if(jQuery(".notify").length > 0) jQuery(".notify").remove();
        if(jQuery(".laytable-box").length > 0) jQuery(".laytable-box").remove();
        var that = this;
        var cdata = [];
        var cols = [];
        var col = [];
        var sortField = "";
        var sortType = "";
        var clueViewObject = this.lp;
        var clueListObject = clueViewObject.fieldList;
        var count = 15;
        sortField = clueViewObject.sortField;
        sortType = clueViewObject.sortType;
        var csize = this.container.getSize();
        var hsize = this.headTableNode.getSize();
        var tHeight = (csize.y-hsize.y-80);
        debugger
        if (!cpage)cpage = 1;

        for ( i in clueListObject){
            col.push(clueListObject[i]);
        }
        cols.push(col);
        this._getCurrentPageData(function (json) {
            json.data.each(function (data ) {
                if(data.createuser){
                    var owneruser = data.owneruser;
                    var createuser = data.createuser;
                    data.owneruser = owneruser.split("@")[0];
                    data.createuser = createuser.split("@")[0];
                }else{
                    data.owneruser = "";
                    data.createuser = "";
                }

                cdata.push(data);
            }.bind(this));
            layui.config({
                base: '/x_component_CRM/$Template/plugins/table2/'
            }).use(['table2', "table2"], function () {
                var table = layui.table2;
                debugger
                var tableIns = table.render({
                    elem: "#contentTable",
                    data: cdata,
                    height: tHeight,
                    width: '100%',
                    page: {
                        align: 'right',
                        groups: 5,//显示连续页码数量
                        curr:1,
                        count: json.count,//总条数
                        limit:15,
                        limits:[15, 30, 45, 60, 75, 90]
                    },
                    initSort: {
                        sortField: sortField,
                        sortType: sortType
                    },
                    cols:cols
                });
                jQuery(".clueId").each(function(index,element){
                        jQuery(element).on("click", function () {
                            that._openDocument(jQuery(element).attr("id"),jQuery(element).text());
                        });
                    }
                );
                jQuery(".otherId").each(function(index,element){
                        jQuery(element).on("click", function () {
                            that._openOtherDocument(jQuery(element).attr("id"),jQuery(element).text());
                        });
                    }
                );
                jQuery(".laytable-page-pagination").find("a").each(function(index,element){
                        jQuery(element).on("click", function () {
                            //cpage = parseInt(jQuery(element).attr("value"))+cpage;
                            var topage = 1;
                            if(jQuery(element).attr("value")=="-1" || jQuery(element).attr("value")=="+1"){
                                topage = parseInt(jQuery(element).attr("value"))+topage;
                            }else{
                                topage = parseInt(jQuery(element).text());
                            }

                            if(jQuery(element).attr("class")!="page-item page-last rayui-disabled" && jQuery(element).attr("class")!="page-item page-prev rayui-disabled"){
                                //that.useTablePlugins(topage);
                                var searchText = jQuery(".headSearchInput").val();
                                var searchType ="";
                                if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                                    searchType = jQuery(".headTableNode").find(".se-select-name").text();
                                }
                                if(searchText!=""){
                                    that.useTablePlugins(topage,searchText,searchType);
                                }else{
                                    that.useTablePlugins(topage,"",searchType);
                                }
                            }

                        });
                    }
                );

                jQuery(".laytable-page-btnok").on("click", function () {
                    var cpage = parseInt(jQuery(".laytable-page-input").val());
                    var searchText = jQuery(".headSearchInput").val();
                    var searchType ="";
                    if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                        searchType = jQuery(".headTableNode").find(".se-select-name").text();
                    }
                    if(searchText!=""){
                        that.useTablePlugins(cpage,searchText,searchType);
                    }else{
                        that.useTablePlugins(cpage,"",searchType);
                    }
                    //that.useTablePlugins(cpage);
                });

                /* tableIns.on("tool",
                     function (evt, obj) {
                         if (evt === "del") {
                             if (window.confirm("确定删除吗？"))
                                 obj.del();
                         }else if (evt === "edit") {
                             alert('您选中了第' + index + '行');
                         }
                     });*/



                jQuery(".page-item").each(function(index,element){
                    if(jQuery(element).attr("value")==(cpage+"")){
                        jQuery(element).attr("class","page-item page-active");
                    }else{
                        if(jQuery(element).attr("value")!="-1" &&  jQuery(element).attr("value")!="+1"){
                            jQuery(element).attr("class","page-item");
                        }
                    }
                });
                var cCount = jQuery(".page-active").attr("value");
                var firstObj = jQuery(".page-prev").parent().next().find("a")[0];
                var lastObj = jQuery(".page-last").parent().prev().find("a")[0];
                if(parseInt(cCount)>parseInt(jQuery(firstObj).attr("value"))){
                    jQuery(".page-prev").attr("class","page-item page-prev");
                }else{
                    jQuery(".page-prev").attr("class","page-item page-prev rayui-disabled");
                }
                if(parseInt(cCount)==parseInt(jQuery(lastObj).attr("value"))){
                    jQuery(".page-last").attr("class","page-item page-last rayui-disabled");
                }else{
                    jQuery(".page-last").attr("class","page-item page-last");
                }
                jQuery(".laytable-page-input").attr("value",cpage+"");

            });


        }.bind(this),count, cpage,searchText,searchType);

    },
    loadSearchCondition: function () {
        //this.template.selectSearch
        var that = this;
        debugger
        if(this.template.selectSearch){
            var shtml = '<div class="vux-flexbox se-section vux-flex-row"><div class="se-name" style="font-size:15px;margin-left:10px;">筛选：</div><div class="el-dropdown" style="margin-right: 20px;">'+
                '<div class="vux-flexbox se-select vux-flex-row el-dropdown-selfdefine" style="border:0px;"><div class="se-select-name" style="font-size:15px;color:#333;">'+this.template.selectSearch.default+'</div>'+
                '<div class="el-icon-arrow-down el-icon--right"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div></div></div>';
            var ulhtml = '<ul class="el-dropdown-type" style="display: none;top:170px;" tid="selectSearch">';
            this.template.selectSearch.items.each(function (item) {
                if(item.name){
                    ulhtml = ulhtml+'<li class="el-dropdown-menu__item">'+item.name+'</li>';
                }
            }.bind(this));
            ulhtml = ulhtml+'<div class="popper__arrow" style="box-sizing: border-box !important;"></div></ul>'
            jQuery(".headTableNode").append(shtml+ulhtml);
            jQuery(".headTableNode").find(".se-select").click(function(){
                jQuery("[tid='selectSearch']").toggle(100);
            });
            jQuery('.el-dropdown-menu__item').click(function(){
                if(jQuery(this).parent().attr("tid")=="selectSearch"){
                    jQuery(".se-select-name").text(jQuery(this).text());
                    that.useTablePlugins(1,jQuery(".headSearchInput").val(),jQuery(".se-select-name").text());
                    jQuery(this).parent().toggle(100);
                }
            });
            that.dropdown = jQuery("body")[0].getElement(".el-dropdown-type");
            if(that.dropdown) that.dropdown.setStyles({"top":(jQuery(".se-name").offset().top+30)+"px","left":jQuery(".se-name").offset().left+2});
        }

        var selectDiv = "";
        if(jQuery(".contentListDiv").find(".contentLeftItemSelectd").length>0){
            selectDiv = jQuery(".contentListDiv").find(".contentLeftItemSelectd").text();
        }
        if(selectDiv !=""){
            jQuery(".headTableNode").empty();
            if(selectDiv=="今日需联系客户"){
                var aHtml = "";
                var shtml = '<div class="vux-flexbox se-section vux-flex-row headTableNode-left"><div class="el-dropdown" style="margin-right: 20px;">'+
                    '<div class="vux-flexbox se-select vux-flex-row el-dropdown-selfdefine" style="border:0px;"><div class="se-select-name" style="font-size:13px;color:#333;">'+this.template.selectContion1.record.default+'</div>'+
                    '<div class="el-icon-arrow-down el-icon--right"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div></div></div>';

                var ulhtml = '<ul class="el-dropdown-type selectSearch"  style="display: none;top:170px;width: 160px;left:395px;" tid="selectSearch">';
                this.template.selectContion1.record.items.each(function (item) {
                    if(item.name){
                        ulhtml = ulhtml+'<li class="el-dropdown-menu__item">'+item.name+'</li>';
                    }
                }.bind(this));
                ulhtml = ulhtml+'<div class="popper__arrow" style="box-sizing: border-box !important;"></div></ul>'
                aHtml = shtml+ulhtml;
                aHtml = aHtml+'<div class="vux-flexbox se-section vux-flex-row headTableNode-right"><div class="el-dropdown" style="margin-right: 20px;">'+
                    '<div class="vux-flexbox se-select vux-flex-row el-dropdown-selfdefine" style="border:0px;"><div class="se-select-name" style="font-size:13px;color:#333;">'+this.template.selectContion1.competence.default+'</div>'+
                    '<div class="el-icon-arrow-down el-icon--right"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div></div></div>';
                aHtml = aHtml+'<ul class="el-dropdown-type selectSearch_right" style="display: none;top:170px;width: 120px;left:578px;" tid="selectSearch_right">';
                this.template.selectContion1.competence.items.each(function (item) {
                    if(item.name){
                        aHtml = aHtml+'<li class="el-dropdown-menu__item">'+item.name+'</li>';
                    }
                }.bind(this));
                aHtml = aHtml+'<div class="popper__arrow" style="box-sizing: border-box !important;"></div></ul>'

                jQuery(".headTableNode").append(aHtml);
            }
            if(selectDiv=="分配给我的线索"){
                var shtml = '<div class="vux-flexbox se-section vux-flex-row headTableNode-left"><div class="el-dropdown" style="margin-right: 20px;">'+
                    '<div class="vux-flexbox se-select vux-flex-row el-dropdown-selfdefine" style="border:0px;"><div class="se-select-name" style="font-size:13px;color:#333;">'+this.template.selectContion2.record.default+'</div>'+
                    '<div class="el-icon-arrow-down el-icon--right"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div></div></div>';

                var ulhtml = '<ul class="el-dropdown-type selectSearch" style="display: none;top:170px;width: 160px;left:395px;" tid="selectSearch">';
                this.template.selectContion2.record.items.each(function (item) {
                    if(item.name){
                        ulhtml = ulhtml+'<li class="el-dropdown-menu__item">'+item.name+'</li>';
                    }
                }.bind(this));
                ulhtml = ulhtml+'<div class="popper__arrow" style="box-sizing: border-box !important;"></div></ul>'
                jQuery(".headTableNode").append(shtml+ulhtml);
            }
            if(selectDiv=="分配给我的客户"){
                var shtml = '<div class="vux-flexbox se-section vux-flex-row headTableNode-left"><div class="el-dropdown" style="margin-right: 20px;">'+
                    '<div class="vux-flexbox se-select vux-flex-row el-dropdown-selfdefine" style="border:0px;"><div class="se-select-name" style="font-size:13px;color:#333;">'+this.template.selectContion3.record.default+'</div>'+
                    '<div class="el-icon-arrow-down el-icon--right"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div></div></div>';

                var ulhtml = '<ul class="el-dropdown-type selectSearch" style="display: none;top:170px;width: 160px;left:395px;" tid="selectSearch">';
                this.template.selectContion3.record.items.each(function (item) {
                    if(item.name){
                        ulhtml = ulhtml+'<li class="el-dropdown-menu__item">'+item.name+'</li>';
                    }
                }.bind(this));
                ulhtml = ulhtml+'<div class="popper__arrow" style="box-sizing: border-box !important;"></div></ul>'
                jQuery(".headTableNode").append(shtml+ulhtml);
            }
            if(selectDiv=="待进入公海的客户"){
                var shtml = '<div class="vux-flexbox se-section vux-flex-row headTableNode-right" style="margin-left:20px;"><div class="el-dropdown" style="margin-right: 20px;">'+
                    '<div class="vux-flexbox se-select vux-flex-row el-dropdown-selfdefine" style="border:0px;"><div class="se-select-name" style="font-size:13px;color:#333;">'+this.template.selectContion4.competence.default+'</div>'+
                    '<div class="el-icon-arrow-down el-icon--right"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div></div></div>';

                var ulhtml = '<ul class="el-dropdown-type selectSearch" style="display: none;top:170px;width: 120px;left:395px;" tid="selectSearch_right">';
                this.template.selectContion4.competence.items.each(function (item) {
                    if(item.name){
                        ulhtml = ulhtml+'<li class="el-dropdown-menu__item">'+item.name+'</li>';
                    }
                }.bind(this));
                ulhtml = ulhtml+'<div class="popper__arrow" style="box-sizing: border-box !important;"></div></ul>'
                jQuery(".headTableNode").append(shtml+ulhtml);
            }

            jQuery(".headTableNode-left").find(".se-select").click(function(){
                jQuery("[tid='selectSearch']").toggle(100);
            });
            jQuery(".headTableNode-right").find(".se-select").click(function(){
                jQuery("[tid='selectSearch_right']").toggle(100);
            });
            jQuery('.el-dropdown-menu__item').click(function(){
                if(jQuery(this).parent().attr("tid")=="selectSearch"){
                    jQuery(".headTableNode-left").find(".se-select-name").text(jQuery(this).text());
                    that.useTablePlugins(1,"",jQuery(".headTableNode-left").find(".se-select-name").text());
                    jQuery(this).parent().toggle(100);
                }
                if(jQuery(this).parent().attr("tid")=="selectSearch_right"){
                    jQuery(".headTableNode-right").find(".se-select-name").text(jQuery(this).text());
                    that.useTablePlugins(1,"",jQuery(".headTableNode-right").find(".se-select-name").text());
                    jQuery(this).parent().toggle(0);
                }
            });
            that.dropdown = jQuery("body")[0].getElement(".selectSearch");
            that.dropdownRight = jQuery("body")[0].getElement(".selectSearch_right");
            if(that.dropdown) that.dropdown.setStyles({"top":(jQuery(".headTableNode").offset().top+45)+"px","left":jQuery(".headTableNode").offset().left-30});
            if(that.dropdownRight) that.dropdownRight.setStyles({"top":(jQuery(".headTableNode").offset().top+45)+"px","left":jQuery(".headTableNode").offset().left+152});
        }

    },
    //格局化日期：yyyy-MM-dd
    formatDate:function(date) {
        var myyear = date.getFullYear();
        var mymonth = date.getMonth()+1;
        var myweekday = date.getDate();

        if(mymonth < 10){
            mymonth = "0" + mymonth;
        }
        if(myweekday < 10){
            myweekday = "0" + myweekday;
        }
        return (myyear+"-"+mymonth + "-" + myweekday);
    },
    reload: function () {
        this.clear();
        this.node = new Element("div", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);
        this.createViewNode();
        this.createViewHead();
        this.createViewBody();
    },
    initSortData: function () {
        this.sortField = null;
        this.sortType = null;
        this.sortFieldDefault = null;
        this.sortTypeDefault = null;
    },
    destroy: function(){
        if(this.documentNodeTemplate){
            delete this.documentNodeTemplate;
        }
        if(this.template)delete this.template;
        if( this.scrollBar ){
            if(this.scrollBar.scrollVAreaNode){
                this.scrollBar.scrollVAreaNode.destroy();
            }
            delete this.scrollBar;
        }

        if(this.pagingContainerTop ){
            if( this.pagingContainerTopCreated ){
                this.pagingContainerTop.destroy();
            }else{
                this.pagingContainerTop.empty();
            }
        }

        if( this.pagingContainerBottom ){
            if( this.pagingContainerBottomCreated ){
                this.pagingContainerBottom.destroy();
            }else{
                this.pagingContainerBottom.empty();
            }
        }

        if( this.paging )this.paging.destroy();

        this.clear();
        delete this;
    },
    clear: function () {
        //if( this.options.pagingEnable ){
        //    this.documents = null;
        //    MWF.release(this.items);
        //    this.items = [];
        //    this.documents = {};
        //    this.node.destroy();
        //    this.container.empty();
        //    this.node.destroy();
        //    this.container.empty();
        //}else{
        //    this.documents = null;
        //    MWF.release(this.items);
        //    this.items = [];
        //    this.documents = {};
        //    this.node.destroy();
        //    this.container.empty();
        //    this.isItemsLoaded = false;
        //    this.isItemLoadding = false;
        //    this.loadItemQueue = 0;
        //}
        this.documents = null;
        MWF.release(this.items);
        this.items = [];
        this.documents = {};
        this.node.destroy();
        this.container.empty();
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
    },
    clearBody : function(){
        this.items.each( function(item,i){
            item.destroy();
        });
        this.documents = null;
        MWF.release(this.items);
        this.items = [];
        this.documents = {};
    },
    resort: function (el) {
        this.sortField = el.retrieve("sortField");
        var sortType = el.retrieve("sortType");
        if (sortType == "") {
            this.sortType = "asc";
        } else if (this.sortType == "asc") {
            this.sortType = "desc";
        } else {
            this.sortField = null;
            this.sortType = null;
        }
        this.reload();
    },
    setScroll: function(){
        MWF.require("MWF.widget.ScrollBar", function () {
            this.scrollBar = new MWF.widget.ScrollBar(this.container, {
                "indent": false,
                "style": "xApp_TaskList",
                "where": "before",
                "distance": 60,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {
                    if( !this.options.pagingEnable ){
                        var scrollSize = this.container.getScrollSize();
                        var clientSize = this.container.getSize();
                        var scrollHeight = scrollSize.y - clientSize.y;
                        if (y + 200 > scrollHeight ) {
                            if (! this.isItemsLoaded) this.loadElementList();
                        }
                    }
                }.bind(this)
            });
        }.bind(this));
    },
    ayalyseTemplate: function () {
        MWF.getJSON(this.options.templateUrl, function (json) {
            this.template = json;
        }.bind(this), false)
    },
    formatElement: function (container, setting, clear ) {
        //container.appendHTML(setting.html);
        var el = setting.html.toDOM( container, function( c , el ){
            this.formatStyles(c);
            this.formatLable(c);
            if(container)this.setEventStyle(c, setting);
        }.bind(this) )[0];
        if( setting.width ){
            el.set("width",setting.width );
        }
        if( clear && container ){
            container.empty();
        }
        return el;
    },
    formatStyles: function ( container ) {
        container.getElements("[class]").each(function (el) {
            var className = el.get("class");
            if (className && this.css[className]) {
                el.setStyles(this.css[className])
            }
        }.bind(this))
        container.getElements("[styles]").each(function (el) {
            var styles = el.get("styles");
            if (styles && this.css[styles]) {
                el.setStyles(this.css[styles])
            }
        }.bind(this))
    },
    formatLable: function (container) {
        container.getElements("[lable]").each(function (el) {
            var lable = el.get("lable");
            if (lable && this.lp[lable]) {
                el.set("text", this.lp[lable] + (el.get("colon") ? ":" : "") )
            }
        }.bind(this))
    },
    createViewHeadNode: function () {
        this.fireEvent("queryCreateViewHeadNode");
        this._queryCreateViewHeadNode();
        this.viewHeadNode = this.formatElement(this.headTableNode, this.template.viewHeadSetting);
        this._postCreateViewHeadNode( this.viewHeadNode );
        this.fireEvent("postCreateViewHeadNode");

        if (!this.viewHeadNode)return;
    },
    createViewNode: function () {
        this.fireEvent("queryCreateViewNode");
        this._queryCreateViewNode();
        this.viewNode = this.formatElement(this.contentTableNode, this.template.viewSetting);
        this._postCreateViewNode( this.viewNode );
        this.fireEvent("postCreateViewNode");
        if (!this.viewNode)return;
    },
    getContentTemplateNode: function(){
        this.documentNodeTemplate = this.formatElement(null, this.template.documentSetting);
        this.template.items.each(function (item) {
            item.nodeTemplate = this.formatElement(null, item.content);
        }.bind(this))
    },
    createViewHead: function () {
        this.fireEvent("queryCreateViewHead");
        this._queryCreateViewHead( );
        if (this.template) {
            if (!this.template.headSetting || this.template.headSetting.disable || !this.template.headSetting.html) {
                return;
            }
        }
        var _self = this;
        var headNode = this.headNode = this.formatElement(this.viewHeadNode, this.template.headSetting);

        this.template.items.each(function (item) {
            if( !item.head )return;
            ////如果设置了权限，那么options里需要有 对应的设置项才会展现
            // 比如 item.access == isAdmin 那么 this.options.isAdmin要为true才展现
            if (item.access && !this.options[item.access])return;
            if (item.head.access && !this.options[item.head.access])return;

            var headItemNode = this.formatElement(headNode, item.head);

            if (item.name == "$checkbox") {
                this.checkboxElement = new Element("input", {
                    "type": "checkbox"
                }).inject(headItemNode);
                this.checkboxElement.addEvent("click", function () {
                    this.selectAllCheckbox()
                }.bind(this))
            }
            if (item.defaultSort && item.defaultSort != "") {
                this.sortFieldDefault = item.name;
                this.sortTypeDefault = item.defaultSort;
            }
            if (item.sort && item.sort != "") {
                headItemNode.store("sortField", item.name);
                if (this.sortField == item.name && this.sortType != "") {
                    headItemNode.store("sortType", this.sortType);
                    this.sortIconNode = new Element("div", {
                        "styles": this.sortType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                    }).inject(headItemNode, "top");
                } else {
                    headItemNode.store("sortType", "");
                    this.sortIconNode = new Element("div", {"styles": this.css.sortIconNode}).inject(headItemNode, "top");
                }
                headItemNode.setStyle("cursor", "pointer");
                headItemNode.addEvent("click", function () {
                    _self.resort(this);
                })
            }
        }.bind(this));
        this.fireEvent("postCreateViewHead");
        this._postCreateViewHead( headNode )
    },
    createViewHeadGroup:function(){
        var tab = this.headTableNode.getElement("table");
        if(tab){
            this.headColGroup = new Element("colgroup.headColGroup").inject(tab);
            this.headWidthAll = 0;
            var headCol = null;
            this.template.items.each(function (item) {
                if(item.head && item.head.width){
                    headCol = new Element("col.headCol",{
                        "width":item.head.width
                    }).inject(this.headColGroup);
                    this.headWidthAll = this.headWidthAll + parseInt(item.head.width.substring(0,item.head.width.length-2));
                }
            }.bind(this));
            //alert(this.headWidthAll);
            //alert(this.headWidthAll>this.container.getWidth()?this.headWidthAll:this.container.getWidth())
            tab.setStyles({"width":(this.headWidthAll>this.container.getWidth()?this.headWidthAll:this.container.getWidth())+"px"});
        }
    },
    createViewContentGroup:function(){
        var tab = this.contentTableNode.getElement("table");
        if(tab){
            this.contentColGroup = new Element("colgroup.contentColGroup").inject(tab);
            this.contentWidthAll = 0;
            var contentCol = null;
            this.template.items.each(function (item) {
                if(item.content && item.content.width){
                    contentCol = new Element("col.contentCol",{
                        "width":item.content.width
                    }).inject(this.contentColGroup);
                    this.contentWidthAll = this.contentWidthAll + parseInt(item.content.width.substring(0,item.content.width.length-2));
                }
            }.bind(this));
            //alert(this.contentWidthAll>this.container.getWidth()?this.contentWidthAll:this.container.getWidth())
            tab.setStyles({"width":(this.contentWidthAll>this.container.getWidth()?this.contentWidthAll:this.container.getWidth())+"px"});
        }
    },
    setEventStyle: function (node, setting, bingObj, data) {
        var _self = this;
        var styles, overStyles, downStyles;
        var styleStr = setting.styles;
        if (typeOf(styleStr) == "string"){
            if (styleStr && styleStr.substr(0, "function".length) == "function") {
                eval("var fun = " + styleStr );
                styles = fun.call(bingObj, data);
            }else{
                styles = this.css[styleStr];
            }
        }else if (typeOf(styleStr) == "object"){
            styles = styleStr;
        }else if (typeOf(styleStr) == "function"){
            eval("var fun = " + styleStr );
            styles = fun.call(bingObj, data);
        }

        if (!styles) {
            var s = node.get("styles");
            if (!s)node.get("class");
            if (s)styles = this.css[s]
        }
        if (setting.icon) {
            if (!styles)styles = {};
            styles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.icon + ")";
        }

        if (typeOf(setting.mouseoverStyles) == "string")overStyles = this.css[setting.mouseoverStyles];
        if (typeOf(setting.mouseoverStyles) == "object") overStyles = setting.mouseoverStyles;
        if (setting.mouseoverIcon) {
            if (!overStyles)overStyles = {};
            overStyles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.mouseoverIcon + ")"
        }

        if (typeOf(setting.mousedownStyles) == "string")downStyles = this.css[setting.mousedownStyles];
        if (typeOf(setting.mousedownStyles) == "object") downStyles = setting.mousedownStyles;
        if (setting.mousedownIcon) {
            if (!downStyles)downStyles = {};
            downStyles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.mousedownIcon + ")"
        }

        if (styles)node.setStyles(styles);
        if (overStyles && styles) {
            node.addEvent("mouseover", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": overStyles, "node":node }));
            node.addEvent("mouseout", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": styles, "node":node}));
        }
        if (downStyles && ( overStyles || styles)) {
            node.addEvent("mousedown", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": downStyles, "node":node}));
            node.addEvent("mouseup", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": overStyles || styles, "node":node}))
        }
    },
    selectAllCheckbox: function () {
        var flag = this.checkboxElement.get("checked");
        this.items.each(function (it) {
            if (it.checkboxElement)it.checkboxElement.set("checked", flag)
        }.bind(this))
    },
    getCheckedItems : function(){
        var checkedItems = [];
        this.items.each(function (it) {
            if (it.checkboxElement.get("checked")) {
                checkedItems.push( it )
            }
        }.bind(this));
        return checkedItems;
    },
    createViewBody : function(){
        //jQuery("#headTable").attr("class","testc");<script>
        //jQuery("head").append(scriptStr);
        this.loadElementList();
    },
    loadElementList: function (count) {
        if( this.options.pagingEnable ){
            var currentItem = this.options.pagingPar.currentItem;
            var countPerPage = this.options.pagingPar.countPerPage;
            debugger;
            if( currentItem ){
                var pageNum = Math.ceil( currentItem / countPerPage );
                var itemNum = currentItem % countPerPage;
                this.loadPagingElementList( count , pageNum, currentItem );
            }else{
                this.loadPagingElementList( count , this.options.pagingPar.currentPage ); //使用分页的方式
            }
        }else{
            this.loadScrollElementList( count ); //滚动条下拉取下一页
        }
    },
    loadScrollElementList : function( count ){
        if (!this.isItemsLoaded) {
            if (!this.isItemLoadding) {
                this.isItemLoadding = true;
                this._getCurrentPageData(function (json) {
                    var length = this.dataCount = json.count;  //|| json.data.length;
                    if (length <= this.items.length) {
                        this.isItemsLoaded = true;
                    }
                    if( json.data && typeOf( json.data )=="array" ){
                        json.data.each(function (data ) {
                            var key = data[ this.options.documentKeyWord || "id" ];
                            if (!this.documents[key]) {
                                var item = this._createDocument(data, this.items.length);
                                this.items.push(item);
                                this.documents[key] = item;
                            }
                        }.bind(this));
                    }
                    this.isItemLoadding = false;

                    if (this.loadItemQueue > 0) {
                        this.loadItemQueue--;
                        this.loadElementList();
                    }


                }.bind(this), count);
            } else {
                this.loadItemQueue++;
            }
        }
    },
    loadPagingElementList : function( count, pageNum, itemNum ){
        this.currentPage = pageNum || 1;
        this._getCurrentPageData(function (json) {
            //this.documents = {};
            //this.items = [];
            this.viewNode.empty();
            this.dataCount = json.count;
            this.createPaging( json.count, pageNum );
            json.data.each(function (data ) {
                debugger
                var item = this._createDocument(data, this.items.length);
                this.items.push(item);
                var key = data[ this.options.documentKeyWord || "id" ];
                this.documents[key] = item;
            }.bind(this));
            debugger
            if( itemNum ){
                if( this.options.documentKeyWord ){
                    var top = this.documents[ itemNum ].node.getTop();
                }else{
                    var top = this.items[itemNum-1].node.getTop();
                }
                this.fireEvent( "gotoItem", top );
            }
        }.bind(this), count, pageNum );
    },
    createPaging : function( itemSize, pageNum ){
        if( !this.options.pagingEnable || this.paging )return;
        if( this.options.pagingPar.position.indexOf("top") > -1 ){
            if( !this.pagingContainerTop ){
                this.pagingContainerTopCreated = true;
                this.pagingContainerTop = new Element("div", {"styles":this.css.pagingContainer}).inject( this.viewNode, "before" );
            }
        }
        if( this.options.pagingPar.position.indexOf("bottom") > -1 ){
            debugger
            if( !this.pagingContainerBottom ){
                this.pagingContainerBottomCreated = true;
                this.pagingContainerBottom = new Element("div", {"styles":this.css.pagingContainer}).inject( this.viewNode, "after" );
            }
        }
        debugger
        var par = Object.merge( this.options.pagingPar, {
            itemSize : itemSize,
            onJumpingPage : function( par ){
                debugger
                this.loadPagingElementList( this.options.pagingPar.countPerPage, par.pageNum, par.itemNum );
            }.bind(this)
        });
        if( pageNum )par.currentPage = pageNum;

        if( this.options.pagingPar.hasPagingBar ){
            debugger
            this.paging = new MWF.xApplication.CRM.Template.Paging(this.pagingContainerTop, this.pagingContainerBottom, par, this.css);
            debugger
            this.paging.load();
        }
    },

    _getCurrentPageData: function (callback, count, page) {
        debugger
        if( this.options.pagingEnable ){
            this.actions.listDetailFilter(page, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else{
            if (!count)count = 20;
            var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
            var filter = this.filterData || {};
            this.actions.listDetailFilterNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }
    },

    getCurrentPageNum: function(){
        return this.paging.options.currentPage;
    },
    getPageSize: function(){
        return this.paging.options.pageSize;
    },
    gotoPage : function( page ){
        this.paging.gotoPage( page );
    },
    _createDocument: function (data, index) {
        return new MWF.xApplication.CRM.Template.ComplexDocument(this.viewNode, data, this.explorer, this, null,index);
    },
    _openDocument: function () {
    },
    _openOtherDocument: function () {
    },
    _removeDocument: function (documentData, all) {
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
    _create: function () {
        this.from = new MPopupForm(this.explorer);
        this.from.create();
    },
    _queryCreateViewHeadNode: function(){

    },
    _postCreateViewHeadNode: function( viewNode ){

    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }
});

MWF.xApplication.CRM.Template.ComplexViewOpen = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "templateUrl": "",
        "scrollEnable" : false,
        "pagingEnable" : true,
        "documentKeyWord" : null,
        "pagingPar" : {
            position : [ "bottom" ], //分页条，上下
            countPerPage : 5,
            visiblePages : 10,
            currentPage : 1,
            currentItem : null,
            hasPagingBar : true,
            hasTruningBar : true,
            // hasNextPage : true,
            // hasPrevPage : true,
            hasReturn : false
        }
    },
    initialize: function (container, openDiv, app, explorer, options, para) {
        this.container = container;
        this.openDiv = openDiv;
        this.explorer = explorer;
        if( para ){
            this.app = app || para.app || this.explorer.app;
            this.lp = para.lp || this.explorer.lp || this.app.lp;
            this.css = para.css || this.explorer.css || this.app.css;
            this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            this.isAdmin = para.isAdmin;
        }else{
            this.app = app || this.explorer.app;
            this.lp = this.explorer.lp || this.app.lp;
            this.css = this.explorer.css || this.app.css;
            this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
        }
        debugger
        if (!options.templateUrl) {
            options.templateUrl = this.explorer.path + "listItem.json"
        } else if (options.templateUrl.indexOf("/") == -1) {
            options.templateUrl = this.explorer.path + options.templateUrl;
        }
        this.setOptions(options);

    },
    initData: function () {
        this.items = [];
        this.documents = {};
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        this.count = 0;
        //this.controllers =[];
    },
    load: function () {
        this.initData();
        this.ayalyseTemplate();


        this.getContentTemplateNode(); //获取template
        debugger
        this.headNode = new Element("div.headNode", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);
        this.contentTableNode = new Element("div.contentTableNode", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);

        this.createViewNode();
        this.loadResource(function(){
            this.loadSearchCondition();
            this.useTablePlugins(1,"","");
            debugger
            if (jQuery( ".headTableNode:has(div)" ).length==0){
                jQuery(".headTableNode").remove();
            }
            //搜索
            var that = this;
            jQuery(".headSearchBottonDiv").on("click", function () {
                var searchText = jQuery(".headSearchInput").val();
                if(searchText!=""){
                    var spage =  parseInt(jQuery(".page-active").attr("value"));
                    //if(jQuery(".laytable-box").length > 0) jQuery(".laytable-box").remove();
                    var searchType ="";
                    if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                        searchType = jQuery(".headTableNode").find(".se-select-name").text();
                    }
                    debugger
                    that.useTablePlugins(1,searchText,searchType);
                }
            });
            jQuery(".headSearchRemoveImg").on("click", function () {
                var spage =  parseInt(jQuery(".page-active").attr("value"));
                var searchType ="";
                if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                    searchType = jQuery(".headTableNode").find(".se-select-name").text();
                }
                that.useTablePlugins(spage,"",searchType);
            });

        }.bind(this))
    },
    loadResource: function ( callback ) {
        if(callback)callback();
    },
    useTablePlugins: function (cpage,searchText,searchType) {
        if(jQuery(".notify").length > 0) jQuery(".notify").remove();
        if(jQuery(".laytable-box").length > 0) jQuery(".laytable-box").remove();
        var that = this;
        var cdata = [];
        var cols = [];
        var col = [];
        var sortField = "";
        var sortType = "";
        var clueViewObject = this.lp;
        var clueListObject = clueViewObject.fieldList;
        var count = 15;
        sortField = clueViewObject.sortField;
        sortType = clueViewObject.sortType;
        debugger
        if (!cpage)cpage = 1;

        for ( i in clueListObject){
            col.push(clueListObject[i]);
        }
        cols.push(col);
        this._getCurrentPageData(function (json) {
            json.data.each(function (data ) {
                if(data.owneruser){
                    var owneruser = data.owneruser;
                    var createuser = data.createuser;
                    data.owneruser = owneruser.split("@")[0];
                    data.createuser = createuser.split("@")[0];
                }else{
                    data.owneruser = "";
                    data.createuser = "";
                }

                cdata.push(data);
            }.bind(this));
            layui.config({
                base: '/x_component_CRM/$Template/plugins/table2/'
            }).use(['table2', "table2"], function () {
                var table = layui.table2;
                debugger
                var tableIns = table.render({
                    elem: "#contentTable",
                    data: cdata,
                    height: 850,
                    width: '100%',
                    page: {
                        align: 'right',
                        groups: 5,//显示连续页码数量
                        curr:1,
                        count: json.count,//总条数
                        limit:15,
                        limits:[15, 30, 45, 60, 75, 90]
                    },
                    initSort: {
                        sortField: sortField,
                        sortType: sortType
                    },
                    cols:cols
                });
                jQuery(".clueId").each(function(index,element){
                        jQuery(element).on("click", function () {
                            that._openDocument(jQuery(element).attr("id"),jQuery(element).text());
                        });
                    }
                );
                jQuery(".otherId").each(function(index,element){
                        jQuery(element).on("click", function () {
                            that._openOtherDocument(jQuery(element).attr("id"),jQuery(element).text());
                        });
                    }
                );
                jQuery(".laytable-page-pagination").find("a").each(function(index,element){
                        jQuery(element).on("click", function () {
                            //cpage = parseInt(jQuery(element).attr("value"))+cpage;
                            var topage = 1;
                            if(jQuery(element).attr("value")=="-1" || jQuery(element).attr("value")=="+1"){
                                topage = parseInt(jQuery(element).attr("value"))+topage;
                            }else{
                                topage = parseInt(jQuery(element).text());
                            }

                            if(jQuery(element).attr("class")!="page-item page-last rayui-disabled" && jQuery(element).attr("class")!="page-item page-prev rayui-disabled"){
                                //that.useTablePlugins(topage);
                                var searchText = jQuery(".headSearchInput").val();
                                var searchType ="";
                                if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                                    searchType = jQuery(".headTableNode").find(".se-select-name").text();
                                }
                                if(searchText!=""){
                                    that.useTablePlugins(topage,searchText,searchType);
                                }else{
                                    that.useTablePlugins(topage,"",searchType);
                                }
                            }

                        });
                    }
                );

                jQuery(".laytable-page-btnok").on("click", function () {
                    var cpage = parseInt(jQuery(".laytable-page-input").val());
                    var searchText = jQuery(".headSearchInput").val();
                    var searchType ="";
                    if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                        searchType = jQuery(".headTableNode").find(".se-select-name").text();
                    }
                    if(searchText!=""){
                        that.useTablePlugins(cpage,searchText,searchType);
                    }else{
                        that.useTablePlugins(cpage,"",searchType);
                    }
                    //that.useTablePlugins(cpage);
                });

                /* tableIns.on("tool",
                     function (evt, obj) {
                         if (evt === "del") {
                             if (window.confirm("确定删除吗？"))
                                 obj.del();
                         }else if (evt === "edit") {
                             alert('您选中了第' + index + '行');
                         }
                     });*/



                jQuery(".page-item").each(function(index,element){
                    if(jQuery(element).attr("value")==(cpage+"")){
                        jQuery(element).attr("class","page-item page-active");
                    }else{
                        if(jQuery(element).attr("value")!="-1" &&  jQuery(element).attr("value")!="+1"){
                            jQuery(element).attr("class","page-item");
                        }
                    }
                });
                var cCount = jQuery(".page-active").attr("value");
                var firstObj = jQuery(".page-prev").parent().next().find("a")[0];
                var lastObj = jQuery(".page-last").parent().prev().find("a")[0];
                if(parseInt(cCount)>parseInt(jQuery(firstObj).attr("value"))){
                    jQuery(".page-prev").attr("class","page-item page-prev");
                }else{
                    jQuery(".page-prev").attr("class","page-item page-prev rayui-disabled");
                }
                if(parseInt(cCount)==parseInt(jQuery(lastObj).attr("value"))){
                    jQuery(".page-last").attr("class","page-item page-last rayui-disabled");
                }else{
                    jQuery(".page-last").attr("class","page-item page-last");
                }
                jQuery(".laytable-page-input").attr("value",cpage+"");

            });


        }.bind(this),count, cpage,searchText,searchType);

    },
    loadSearchCondition: function () {
        //this.template.selectSearch
        var that = this;
        debugger
        //if(this.template.selectSearch){
            var shtml = '<span class="title">'+this.lp.title+'</span><div class="search-input">'+
                '<input type="text" autocomplete="off" placeholder="'+this.lp.searchText+'" class="el-input__inner">'+
                '<div class="el-input-group__append"><img  src="/x_component_CRM/$Template/search.png" class="searchimg"></div>'+
                '</div><img  src="/x_component_CRM/$Template/close.png" class="close"></div>';


            jQuery(".headNode").append(shtml);
            jQuery(".headNode").find(".el-input-group__append").click(function(){
                var searchText = jQuery(".el-input__inner").val();
                that.useTablePlugins(1,searchText);
            });
            jQuery(".headNode").find(".close").click(function(){
                jQuery(".openDiv").empty();
                jQuery(".openDiv").hide();
            });

        //}

    },
    openx: function (objx) {
        debugger
    },
    reload: function () {
        this.clear();
        this.node = new Element("div", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);
        this.createViewNode();
        this.createViewHead();
        this.createViewBody();
    },
    initSortData: function () {
        this.sortField = null;
        this.sortType = null;
        this.sortFieldDefault = null;
        this.sortTypeDefault = null;
    },
    destroy: function(){
        if(this.documentNodeTemplate){
            delete this.documentNodeTemplate;
        }
        if(this.template)delete this.template;
        if( this.scrollBar ){
            if(this.scrollBar.scrollVAreaNode){
                this.scrollBar.scrollVAreaNode.destroy();
            }
            delete this.scrollBar;
        }

        if(this.pagingContainerTop ){
            if( this.pagingContainerTopCreated ){
                this.pagingContainerTop.destroy();
            }else{
                this.pagingContainerTop.empty();
            }
        }

        if( this.pagingContainerBottom ){
            if( this.pagingContainerBottomCreated ){
                this.pagingContainerBottom.destroy();
            }else{
                this.pagingContainerBottom.empty();
            }
        }

        if( this.paging )this.paging.destroy();

        this.clear();
        delete this;
    },
    clear: function () {
        //if( this.options.pagingEnable ){
        //    this.documents = null;
        //    MWF.release(this.items);
        //    this.items = [];
        //    this.documents = {};
        //    this.node.destroy();
        //    this.container.empty();
        //    this.node.destroy();
        //    this.container.empty();
        //}else{
        //    this.documents = null;
        //    MWF.release(this.items);
        //    this.items = [];
        //    this.documents = {};
        //    this.node.destroy();
        //    this.container.empty();
        //    this.isItemsLoaded = false;
        //    this.isItemLoadding = false;
        //    this.loadItemQueue = 0;
        //}
        this.documents = null;
        MWF.release(this.items);
        this.items = [];
        this.documents = {};
        this.node.destroy();
        this.container.empty();
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
    },
    clearBody : function(){
        this.items.each( function(item,i){
            item.destroy();
        });
        this.documents = null;
        MWF.release(this.items);
        this.items = [];
        this.documents = {};
    },
    resort: function (el) {
        this.sortField = el.retrieve("sortField");
        var sortType = el.retrieve("sortType");
        if (sortType == "") {
            this.sortType = "asc";
        } else if (this.sortType == "asc") {
            this.sortType = "desc";
        } else {
            this.sortField = null;
            this.sortType = null;
        }
        this.reload();
    },
    setScroll: function(){
        MWF.require("MWF.widget.ScrollBar", function () {
            this.scrollBar = new MWF.widget.ScrollBar(this.container, {
                "indent": false,
                "style": "xApp_TaskList",
                "where": "before",
                "distance": 60,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {
                    if( !this.options.pagingEnable ){
                        var scrollSize = this.container.getScrollSize();
                        var clientSize = this.container.getSize();
                        var scrollHeight = scrollSize.y - clientSize.y;
                        if (y + 200 > scrollHeight ) {
                            if (! this.isItemsLoaded) this.loadElementList();
                        }
                    }
                }.bind(this)
            });
        }.bind(this));
    },
    ayalyseTemplate: function () {
        MWF.getJSON(this.options.templateUrl, function (json) {
            this.template = json;
        }.bind(this), false)
    },
    formatElement: function (container, setting, clear ) {
        //container.appendHTML(setting.html);
        var el = setting.html.toDOM( container, function( c , el ){
            this.formatStyles(c);
            this.formatLable(c);
            if(container)this.setEventStyle(c, setting);
        }.bind(this) )[0];
        if( setting.width ){
            el.set("width",setting.width );
        }
        if( clear && container ){
            container.empty();
        }
        return el;
    },
    formatStyles: function ( container ) {
        container.getElements("[class]").each(function (el) {
            var className = el.get("class");
            if (className && this.css[className]) {
                el.setStyles(this.css[className])
            }
        }.bind(this))
        container.getElements("[styles]").each(function (el) {
            var styles = el.get("styles");
            if (styles && this.css[styles]) {
                el.setStyles(this.css[styles])
            }
        }.bind(this))
    },
    formatLable: function (container) {
        container.getElements("[lable]").each(function (el) {
            var lable = el.get("lable");
            if (lable && this.lp[lable]) {
                el.set("text", this.lp[lable] + (el.get("colon") ? ":" : "") )
            }
        }.bind(this))
    },
    createViewHeadNode: function () {
        this.fireEvent("queryCreateViewHeadNode");
        this._queryCreateViewHeadNode();
        this.viewHeadNode = this.formatElement(this.headTableNode, this.template.viewHeadSetting);
        this._postCreateViewHeadNode( this.viewHeadNode );
        this.fireEvent("postCreateViewHeadNode");

        if (!this.viewHeadNode)return;
    },
    createViewNode: function () {
        this.fireEvent("queryCreateViewNode");
        this._queryCreateViewNode();
        this.viewNode = this.formatElement(this.contentTableNode, this.template.viewSetting);
        this._postCreateViewNode( this.viewNode );
        this.fireEvent("postCreateViewNode");
        if (!this.viewNode)return;
    },
    getContentTemplateNode: function(){
        this.documentNodeTemplate = this.formatElement(null, this.template.documentSetting);
        this.template.items.each(function (item) {
            item.nodeTemplate = this.formatElement(null, item.content);
        }.bind(this))
    },
    createViewHead: function () {
        this.fireEvent("queryCreateViewHead");
        this._queryCreateViewHead( );
        if (this.template) {
            if (!this.template.headSetting || this.template.headSetting.disable || !this.template.headSetting.html) {
                return;
            }
        }
        var _self = this;
        var headNode = this.headNode = this.formatElement(this.viewHeadNode, this.template.headSetting);

        this.template.items.each(function (item) {
            if( !item.head )return;
            ////如果设置了权限，那么options里需要有 对应的设置项才会展现
            // 比如 item.access == isAdmin 那么 this.options.isAdmin要为true才展现
            if (item.access && !this.options[item.access])return;
            if (item.head.access && !this.options[item.head.access])return;

            var headItemNode = this.formatElement(headNode, item.head);

            if (item.name == "$checkbox") {
                this.checkboxElement = new Element("input", {
                    "type": "checkbox"
                }).inject(headItemNode);
                this.checkboxElement.addEvent("click", function () {
                    this.selectAllCheckbox()
                }.bind(this))
            }
            if (item.defaultSort && item.defaultSort != "") {
                this.sortFieldDefault = item.name;
                this.sortTypeDefault = item.defaultSort;
            }
            if (item.sort && item.sort != "") {
                headItemNode.store("sortField", item.name);
                if (this.sortField == item.name && this.sortType != "") {
                    headItemNode.store("sortType", this.sortType);
                    this.sortIconNode = new Element("div", {
                        "styles": this.sortType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                    }).inject(headItemNode, "top");
                } else {
                    headItemNode.store("sortType", "");
                    this.sortIconNode = new Element("div", {"styles": this.css.sortIconNode}).inject(headItemNode, "top");
                }
                headItemNode.setStyle("cursor", "pointer");
                headItemNode.addEvent("click", function () {
                    _self.resort(this);
                })
            }
        }.bind(this));
        this.fireEvent("postCreateViewHead");
        this._postCreateViewHead( headNode )
    },
    createViewHeadGroup:function(){
        var tab = this.headTableNode.getElement("table");
        if(tab){
            this.headColGroup = new Element("colgroup.headColGroup").inject(tab);
            this.headWidthAll = 0;
            var headCol = null;
            this.template.items.each(function (item) {
                if(item.head && item.head.width){
                    headCol = new Element("col.headCol",{
                        "width":item.head.width
                    }).inject(this.headColGroup);
                    this.headWidthAll = this.headWidthAll + parseInt(item.head.width.substring(0,item.head.width.length-2));
                }
            }.bind(this));
            //alert(this.headWidthAll);
            //alert(this.headWidthAll>this.container.getWidth()?this.headWidthAll:this.container.getWidth())
            tab.setStyles({"width":(this.headWidthAll>this.container.getWidth()?this.headWidthAll:this.container.getWidth())+"px"});
        }
    },
    createViewContentGroup:function(){
        var tab = this.contentTableNode.getElement("table");
        if(tab){
            this.contentColGroup = new Element("colgroup.contentColGroup").inject(tab);
            this.contentWidthAll = 0;
            var contentCol = null;
            this.template.items.each(function (item) {
                if(item.content && item.content.width){
                    contentCol = new Element("col.contentCol",{
                        "width":item.content.width
                    }).inject(this.contentColGroup);
                    this.contentWidthAll = this.contentWidthAll + parseInt(item.content.width.substring(0,item.content.width.length-2));
                }
            }.bind(this));
            //alert(this.contentWidthAll>this.container.getWidth()?this.contentWidthAll:this.container.getWidth())
            tab.setStyles({"width":(this.contentWidthAll>this.container.getWidth()?this.contentWidthAll:this.container.getWidth())+"px"});
        }
    },
    setEventStyle: function (node, setting, bingObj, data) {
        var _self = this;
        var styles, overStyles, downStyles;
        var styleStr = setting.styles;
        if (typeOf(styleStr) == "string"){
            if (styleStr && styleStr.substr(0, "function".length) == "function") {
                eval("var fun = " + styleStr );
                styles = fun.call(bingObj, data);
            }else{
                styles = this.css[styleStr];
            }
        }else if (typeOf(styleStr) == "object"){
            styles = styleStr;
        }else if (typeOf(styleStr) == "function"){
            eval("var fun = " + styleStr );
            styles = fun.call(bingObj, data);
        }

        if (!styles) {
            var s = node.get("styles");
            if (!s)node.get("class");
            if (s)styles = this.css[s]
        }
        if (setting.icon) {
            if (!styles)styles = {};
            styles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.icon + ")";
        }

        if (typeOf(setting.mouseoverStyles) == "string")overStyles = this.css[setting.mouseoverStyles];
        if (typeOf(setting.mouseoverStyles) == "object") overStyles = setting.mouseoverStyles;
        if (setting.mouseoverIcon) {
            if (!overStyles)overStyles = {};
            overStyles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.mouseoverIcon + ")"
        }

        if (typeOf(setting.mousedownStyles) == "string")downStyles = this.css[setting.mousedownStyles];
        if (typeOf(setting.mousedownStyles) == "object") downStyles = setting.mousedownStyles;
        if (setting.mousedownIcon) {
            if (!downStyles)downStyles = {};
            downStyles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.mousedownIcon + ")"
        }

        if (styles)node.setStyles(styles);
        if (overStyles && styles) {
            node.addEvent("mouseover", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": overStyles, "node":node }));
            node.addEvent("mouseout", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": styles, "node":node}));
        }
        if (downStyles && ( overStyles || styles)) {
            node.addEvent("mousedown", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": downStyles, "node":node}));
            node.addEvent("mouseup", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": overStyles || styles, "node":node}))
        }
    },
    selectAllCheckbox: function () {
        var flag = this.checkboxElement.get("checked");
        this.items.each(function (it) {
            if (it.checkboxElement)it.checkboxElement.set("checked", flag)
        }.bind(this))
    },
    getCheckedItems : function(){
        var checkedItems = [];
        this.items.each(function (it) {
            if (it.checkboxElement.get("checked")) {
                checkedItems.push( it )
            }
        }.bind(this));
        return checkedItems;
    },
    createViewBody : function(){
        //jQuery("#headTable").attr("class","testc");<script>
        //jQuery("head").append(scriptStr);
        this.loadElementList();
    },
    loadElementList: function (count) {
        if( this.options.pagingEnable ){
            var currentItem = this.options.pagingPar.currentItem;
            var countPerPage = this.options.pagingPar.countPerPage;
            debugger;
            if( currentItem ){
                var pageNum = Math.ceil( currentItem / countPerPage );
                var itemNum = currentItem % countPerPage;
                this.loadPagingElementList( count , pageNum, currentItem );
            }else{
                this.loadPagingElementList( count , this.options.pagingPar.currentPage ); //使用分页的方式
            }
        }else{
            this.loadScrollElementList( count ); //滚动条下拉取下一页
        }
    },
    loadScrollElementList : function( count ){
        if (!this.isItemsLoaded) {
            if (!this.isItemLoadding) {
                this.isItemLoadding = true;
                this._getCurrentPageData(function (json) {
                    var length = this.dataCount = json.count;  //|| json.data.length;
                    if (length <= this.items.length) {
                        this.isItemsLoaded = true;
                    }
                    if( json.data && typeOf( json.data )=="array" ){
                        json.data.each(function (data ) {
                            var key = data[ this.options.documentKeyWord || "id" ];
                            if (!this.documents[key]) {
                                var item = this._createDocument(data, this.items.length);
                                this.items.push(item);
                                this.documents[key] = item;
                            }
                        }.bind(this));
                    }
                    this.isItemLoadding = false;

                    if (this.loadItemQueue > 0) {
                        this.loadItemQueue--;
                        this.loadElementList();
                    }


                }.bind(this), count);
            } else {
                this.loadItemQueue++;
            }
        }
    },
    loadPagingElementList : function( count, pageNum, itemNum ){
        this.currentPage = pageNum || 1;
        this._getCurrentPageData(function (json) {
            //this.documents = {};
            //this.items = [];
            this.viewNode.empty();
            this.dataCount = json.count;
            this.createPaging( json.count, pageNum );
            json.data.each(function (data ) {
                debugger
                var item = this._createDocument(data, this.items.length);
                this.items.push(item);
                var key = data[ this.options.documentKeyWord || "id" ];
                this.documents[key] = item;
            }.bind(this));
            debugger
            if( itemNum ){
                if( this.options.documentKeyWord ){
                    var top = this.documents[ itemNum ].node.getTop();
                }else{
                    var top = this.items[itemNum-1].node.getTop();
                }
                this.fireEvent( "gotoItem", top );
            }
        }.bind(this), count, pageNum );
    },
    createPaging : function( itemSize, pageNum ){
        if( !this.options.pagingEnable || this.paging )return;
        if( this.options.pagingPar.position.indexOf("top") > -1 ){
            if( !this.pagingContainerTop ){
                this.pagingContainerTopCreated = true;
                this.pagingContainerTop = new Element("div", {"styles":this.css.pagingContainer}).inject( this.viewNode, "before" );
            }
        }
        if( this.options.pagingPar.position.indexOf("bottom") > -1 ){
            debugger
            if( !this.pagingContainerBottom ){
                this.pagingContainerBottomCreated = true;
                this.pagingContainerBottom = new Element("div", {"styles":this.css.pagingContainer}).inject( this.viewNode, "after" );
            }
        }
        debugger
        var par = Object.merge( this.options.pagingPar, {
            itemSize : itemSize,
            onJumpingPage : function( par ){
                debugger
                this.loadPagingElementList( this.options.pagingPar.countPerPage, par.pageNum, par.itemNum );
            }.bind(this)
        });
        if( pageNum )par.currentPage = pageNum;

        if( this.options.pagingPar.hasPagingBar ){
            debugger
            this.paging = new MWF.xApplication.CRM.Template.Paging(this.pagingContainerTop, this.pagingContainerBottom, par, this.css);
            debugger
            this.paging.load();
        }
    },

    _getCurrentPageData: function (callback, count, page) {
        debugger
        if( this.options.pagingEnable ){
            this.actions.listDetailFilter(page, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else{
            if (!count)count = 20;
            var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
            var filter = this.filterData || {};
            this.actions.listDetailFilterNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }
    },

    getCurrentPageNum: function(){
        return this.paging.options.currentPage;
    },
    getPageSize: function(){
        return this.paging.options.pageSize;
    },
    gotoPage : function( page ){
        this.paging.gotoPage( page );
    },
    _createDocument: function (data, index) {
        return new MWF.xApplication.CRM.Template.ComplexDocument(this.viewNode, data, this.explorer, this, null,index);
    },
    _openDocument: function () {
    },
    _openOtherDocument: function () {
    },
    _removeDocument: function (documentData, all) {
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
    _create: function () {
        this.from = new MPopupForm(this.explorer);
        this.from.create();
    },
    _queryCreateViewHeadNode: function(){

    },
    _postCreateViewHeadNode: function( viewNode ){

    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }
});

MWF.xApplication.CRM.Template.ComplexDocument = new Class({
    Implements: [Options, Events],
    initialize: function (container, data, explorer, view, para, index) {
        this.explorer = explorer;
        this.data = data;
        this.container = container;
        this.view = view;
        this.index = index;
        if( para ){
            this.app = para.app || this.view.app || this.explorer.app;
            this.lp = para.lp || this.view.lp || this.explorer.lp || this.app.lp;
            this.css = para.css || this.view.css || this.explorer.css || this.app.css;
            this.actions = para.actions || this.view.actions || this.explorer.actions || this.app.actions || this.app.restActions;
        }else{
            this.app = this.view.app || this.explorer.app;
            this.lp = this.view.lp || this.explorer.lp || this.app.lp;
            this.css = this.view.css || this.explorer.css || this.app.css;
            this.actions = this.view.actions || this.explorer.actions || this.app.actions || this.app.restActions;
        }
        debugger;
        //container为openDiv
        this.load();
    },

    load: function () {
        this.fireEvent("queryCreateDocumentNode");
        this._queryCreateDocumentNode( this.data );
        var _self = this;

        this.loadResource(function(){
            this.loadClueForm(this.data);
        }.bind(this))

        /*
        this.node = this.view.documentNodeTemplate.clone().inject(this.container);

        //this.documentAreaNode =  new Element("td", {"styles": this.css.documentNode}).inject(this.node);
        this.view.template.items.each(function (item) {
            if( item.access && this._getItemAccess(item) ){
                this.loadItem(item.name, item.content, item.nodeTemplate)
            }else{
                this.loadItem(item.name, item.content, item.nodeTemplate)
            }
        }.bind(this));

        var setting = this.view.template.documentSetting;
        if( setting.styles || setting.mouseoverStyles || setting.mousedownStyles || setting.icon || setting.mouseoverIcon || setting.mousedownIcon ){
            this.view.setEventStyle( this.node, setting, this, this.data );
        }

        var available = this.getConditionResult(setting.condition);
        if( setting.action && this[setting.action] ){
            if ( available ){
                this.node.addEvent("click", function (ev) {
                    this.fun.call(_self, this.node, ev);
                    ev.stopPropagation();
                }.bind({fun: this[setting.action], node : this.node}))
            }
        }
        if( setting.event && available ){
            this.bindEvent( this.node, setting.event );
        }
        */
        this.fireEvent("postCreateDocumentNode");
        this._postCreateDocumentNode( this.node, this.data )
    },
    loadResource: function ( callback ) {
        var baseUrls = [
            "/x_component_CRM/$Template/plugins/jquery.min.js",
        ];
        var fullcalendarUrl = "/x_component_CRM/$Template/form/js/bootstrap.min.js";
        var langUrl =  "/x_component_CRM/$Template/form/js/mejs.js";
        COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/form/css/bootstrap.min.css",function(){
            COMMON.AjaxModule.load(baseUrls, function(){
                jQuery.noConflict();
                COMMON.AjaxModule.load(fullcalendarUrl, function(){
                    COMMON.AjaxModule.load(langUrl, function(){
                        if(callback)callback();
                    }.bind(this))
                }.bind(this));
            }.bind(this))
        }.bind(this))
    },
    loadClueForm: function (name) {
        alert(name);
    },
    loadItem: function (name, item, nodeTemplate ) {
        var itemNode = this[name] = nodeTemplate.clone();
        if( this.format(itemNode, name, item) ){
            itemNode.inject(this.node);
        }
        if (item.items) {
            var elements = itemNode.getElements("[item]");
            if( itemNode.get("item") )elements.push(itemNode);
            elements.each(function (el) {
                var key = el.get("item");
                var sub = item.items[key];
                if( sub ){
                    if( !sub.value && sub.value!="" )sub.value = key;
                    if( !this.format(el, name, sub) ){
                        el.dispose()
                    }
                }
            }.bind(this))
        }
    },
    format: function (itemNode, name, item) {
        var _self = this;
        if (item.access) {
            if (!this._getItemAccess(item))return false;
        }
        //if (item.condition) {
        //    if (!this.getConditionResult(item.condition))return false;
        //}
        var show = this.getConditionResult( item.show );
        if( !show )itemNode.setStyle("display","none");

        var available = this.getConditionResult(item.condition);

        if (item.text) {
            var text = this.getExecuteResult( item.text );
            //var text = item.text;
            itemNode.set("text", this.view.lp && this.view.lp[text] ? this.view.lp[text] : text);
        }
        if (item.title) {
            var title = this.getExecuteResult( item.title );
            //var title = item.title;
            itemNode.set("title", this.view.lp && this.view.lp[title] ? this.view.lp[title] : title);
        }
        if ( !item.text && item.value && item.value != "") {
            if( item.type == "html" ){
                itemNode.set("html", this.getValue(item.value));
            }else{
                itemNode.set("text", this.getValue(item.value));
            }
        }
        if( item.styles || item.mouseoverStyles || item.mousedownStyles || item.icon || item.mouseoverIcon || item.mousedownIcon ){
            this.view.setEventStyle( itemNode, item, this, this.data );
        }
        if (item.action && this[item.action]) {
            if ( available ){
                itemNode.addEvent("click", function (ev) {
                    this.fun.call(_self, this.node, ev);
                    ev.stopPropagation();
                }.bind({fun: this[item.action], node : itemNode}))
            }else{
                return false;
            }
        }
        if( item.event && available ){
            this.bindEvent( itemNode, item.event );
        }
        if( item.attr ){
            this.setAttr( itemNode, item.attr );
        }
        if ( name == "$checkbox" ) {
            if ( available ){
                this.checkboxElement = new Element("input", {
                    "type": "checkbox"
                }).inject(itemNode);
                this.checkboxElement.addEvent("click", function (ev) {
                    ev.stopPropagation();
                }.bind(this));
                itemNode.addEvent("click", function (ev) {
                    this.checkboxElement.set("checked", !this.checkboxElement.get("checked"));
                    ev.stopPropagation();
                }.bind(this))
            }else{
                //return false;
            }
        }
        return true;
    },
    getExecuteResult : function( str ){
        var result = str;
        if (str && str.substr(0, 8) == "function") { //"function".length
            eval("var fun = " + str);
            result = fun.call(this, this.data);
        }
        return result;
    },
    getValue: function (str) {
        if (str.substr(0, 8 ) == "function") { //"function".length
            eval("var fun = " + str);
            return fun.call(this, this.data);
        } else if (typeOf(this.data[str]) == "number") {
            return this.data[str];
        } else {
            return this.data[str] ? this.data[str] : "";
        }
    },
    getConditionResult: function (str) {
        var flag = true;
        if (str && str.substr(0, 8) == "function") { //"function".length
            eval("var fun = " + str);
            flag = fun.call(this, this.data);
        }
        return flag;
    },
    setAttr: function(item, attr){
        if( !attr || attr == "" || attr == "$none" )return;
        if( typeof attr == "string" ){
            if( attr.indexOf("^^") > -1 ){
                var attrsArr = attr.split("##");
                if( attrsArr[0].split("^^").length != 2 )return;
                attrs = {};
                for(var i=0;i<attrsArr.length;i++){
                    var aname = attrsArr[i].split("^^")[0];
                    var afunction = attrsArr[i].split("^^")[1];
                    if( afunction.substr(0, "function".length) == "function" ){
                        eval("var fun = " + afunction );
                        attrs[ aname ] = fun.call(this, this.data);  //字符串变对象或function，方法1
                    }else{
                        attrs[ aname ] = afunction;
                    }
                }
            }else{
                //字符串变对象或function，方法2
                eval( "var attrs = " + attr );
            }
        }
        if( typeOf(attrs) == "object" ){
            for( var a in attrs ){
                item.set( a, attrs[a] );
            }
        }
    },
    bindEvent: function(item,events){
        if( !events || events == "" || events == "$none" )return;
        if( typeof events == "string" ){
            if( events.indexOf("^^") > -1 ){
                var eventsArr = events.split("##");
                if( eventsArr[0].split("^^").length != 2 )return;
                events = {};
                for(var i=0;i<eventsArr.length;i++){
                    var ename = eventsArr[i].split("^^")[0];
                    var efunction = eventsArr[i].split("^^")[1];
                    events[ ename ] = eval( "(function(){ return "+ efunction +" })()" );  //字符串变对象或function，方法1
                }
            }else{
                //字符串变对象或function，方法2
                eval( "var events = " + events );
            }
        }
        if( typeOf(events) == "object" ){
            for( var e in events ){
                item.addEvent( e, function(ev){
                    this.fun.call( this.bingObj, this.target, ev );
                    ev.stopPropagation();
                }.bind({bingObj : this, target: item, fun : events[e]}));
            }
        }
    },
    _getItemAccess: function (item) {
        if (item.access && !this.explorer.options[item.access]) {
            return false;
        } else {
            return true;
        }
    },
    _getActionAccess: function (actionData) {
        return true;
    },
    open: function (e) {
        //var options = {"documentId": this.data.id }//this.explorer.app.options.application.allowControl};
        //this.explorer.app.desktop.openApplication(e, "cms.Document", options);
        this.view._openDocument(this.data);
    },
    remove: function (e) {
        var lp = this.app.lp;
        var text = lp.deleteDocument.replace(/{title}/g, this.data.title);
        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        //this.explorer.app.confirm("warn", e, lp.deleteDocumentTitle, text, 350, 120, function () {
        this.app.confirm("warn", e, lp.deleteDocumentTitle, text, 350, 120, function () {
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
            _self.view.lockNodeStyle = false;
            //}
            this.close();
            //}else{
            //    this.content.getElement("#deleteDocument_checkInfor").set("text", lp.deleteAllDocumentCheck).setStyle("color", "red");
            //}
        }, function () {
            _self.node.setStyles(_self.css.documentNode);
            _self.readyRemove = false;
            _self.view.lockNodeStyle = false;
            this.close();
        });
    },

    destroy: function () {
        this.node.destroy();
        delete this;
    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    }
});

MWF.xApplication.CRM.Template.Paging = new Class({
    Implements: [Options, Events],
    options : {
        position : ["bottom"],
        countPerPage : 5,
        visiblePages : 10,
        currentPage : 1,
        itemSize : 0,
        pageSize : 0,
        //hasNextPage : true,
        //hasPrevPage : true,
        hasTruningBar : true,
        hasJumper : true,
        hasReturn : false,
        //returnText : "返回首页",
        hiddenWithDisable: true,
        text : {
            prePage : "",
            nextPage : "",
            firstPage : "",
            lastPage : ""
        }
    },
    initialize: function (topContainer, bottomContainer, options, css) {
        this.setOptions( options || {});
        this.topContainer = topContainer;
        this.bottomContainer = bottomContainer;
        this.css = css;
    },
    load : function(){
        debugger;
        this.fireEvent( "queryLoad", this);
        this.options.pageSize = Math.ceil(this.options.itemSize/this.options.countPerPage);

        if( ( (this.options.pageSize == 1 || this.options.pageSize == 0) && this.options.hiddenWithDisable ) && !this.options.hasReturn )return;
        debugger;
        if( this.topContainer ){
            this.topContainer.empty();
            if(  this.options.hasTruningBar && this.options.position.indexOf("top") > -1 ){
                this.createNode( this.topContainer );
            }
        }

        if( this.bottomContainer ){
            this.bottomContainer.empty();
            if( this.options.hasPrevPage ){
                this.createPrevPageNode( this.bottomContainer );
            }
            if( this.options.hasNextPage ){
                this.createNextPageNode( this.bottomContainer );
            }
            if(  this.options.hasTruningBar && this.options.position.indexOf("bottom") > -1 ){
                this.createNode( this.bottomContainer );
            }
        }
        this.fireEvent( "postLoad", this);
    },
    createNode : function( container ){
        var _self = this;

        var visiblePages = this.options.visiblePages;
        var pageSize = this.options.pageSize;
        var currentPage = this.options.currentPage;

        var halfCount = Math.floor( visiblePages / 2);
        var i, max, min;
        if( pageSize <= visiblePages ){
            min = 1;
            max = pageSize;
        }else if( currentPage + halfCount > pageSize ){
            min = pageSize - visiblePages;
            max = pageSize;
        }else if( currentPage - halfCount < 1 ){
            min = 1;
            max = visiblePages;
        }else{
            min = currentPage - halfCount;
            max = currentPage + halfCount;
        }
        debugger;
        var node = this.node = new Element("div.pagingBar", { styles : this.css.pagingBar }  ).inject( container );

        if( this.options.hasReturn ){
            var pageReturn = this.pageReturn = new Element( "div.pageReturn" , { styles : this.css.pageReturn , "text" : this.options.returnText } ).inject(node);
            pageReturn.addEvents( {
                "mouseover" : function( ev ){ ev.target.setStyles( this.css.pageReturn_over ) }.bind(this),
                "mouseout" : function( ev ){ ev.target.setStyles( this.css.pageReturn ) }.bind(this),
                "click" : function(){ this.fireEvent( "pageReturn" , this ) }.bind(this)
            })
        }

        if( (pageSize != 1 && pageSize != 0) || !this.options.hiddenWithDisable ){
            if( min > 1 || !this.options.hiddenWithDisable){
                var firstPage = this.firstPage = new Element( "div.firstPage" , { styles : this.css.firstPage, text : "1..."  }).inject(node);
                if( this.options.text.firstPage )firstPage.set( "text", this.options.text.firstPage );
                firstPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.firstPage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.firstPage ) }.bind(this),
                    "click" : function(){ this.gotoPage(1) }.bind(this)
                } )
            }
            if( currentPage != 1 || !this.options.hiddenWithDisable){
                var prePage =  this.prePage =  new Element( "div.prePage" , { styles : this.css.prePage } ).inject(node);
                if( this.options.text.prePage )prePage.set( "text", this.options.text.prePage );
                prePage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.prePage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.prePage ) }.bind(this),
                    "click" : function(){ this.gotoPage( currentPage-1 ) }.bind(this)
                } )
            }

            this.pageTurnNodes = [];
            for( i=min; i<=max; i++ ){
                if( currentPage == i ){
                    this.currentPage = new Element("div.currentPage", {"styles" : this.css.currentPage, "text" : i }).inject(node);
                }else{
                    var pageTurnNode = new Element("div.pageItem", {"styles" : this.css.pageItem, "text" : i }).inject(node);
                    pageTurnNode.addEvents( {
                        "mouseover" : function( ev ){ ev.target.setStyles( this.css.pageItem_over ) }.bind(this),
                        "mouseout" : function( ev ){ ev.target.setStyles( this.css.pageItem ) }.bind(this),
                        "click" : function(){ this.obj.gotoPage( this.num ) }.bind({ obj : this, num : i })
                    });
                    this.pageTurnNodes.push( pageTurnNode );
                }
            }

            if( this.options.hasJumper ){
                var pageJumper = this.pageJumper = new Element("input.pageJumper", {"styles" : this.css.pageJumper , "title" : "输入页码，按回车跳转"}).inject( node );
                new Element( "div.pageText", {"styles" : this.css.pageText , "text" : "/" + pageSize }).inject( node );
                pageJumper.addEvents( {
                    "focus" : function( ev ){ ev.target.setStyles( this.css.pageJumper_over ) }.bind(this),
                    "blur" : function( ev ){ ev.target.setStyles( this.css.pageJumper ) }.bind(this),
                    "keyup" : function(e){
                        this.value=this.value.replace(/[^0-9_]/g,'')
                    },
                    "keydown" : function(e){
                        if(e.code==13 && this.value!="" ){
                            _self.gotoPage( this.value );
                            e.stopPropagation();
                            //e.preventDefault();
                        }
                    }
                });
            }

            if( currentPage != pageSize || !this.options.hiddenWithDisable){
                var nextPage = this.nextPage = new Element( "div.nextPage" , { styles : this.css.nextPage } ).inject(node);
                if( this.options.text.nextPage )nextPage.set( "text", this.options.text.nextPage );
                nextPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.nextPage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.nextPage ) }.bind(this),
                    "click" : function(){ this.gotoPage(  currentPage+1 ) }.bind(this)
                } )
            }

            if( max < pageSize || !this.options.hiddenWithDisable){
                var lastPage = this.lastPage = new Element( "div.lastPage" , { styles : this.css.lastPage, text : "..." + pageSize  }).inject(node);
                if( this.options.text.lastPage )lastPage.set( "text", this.options.text.lastPage );
                lastPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.lastPage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.lastPage ) }.bind(this),
                    "click" : function(){ this.gotoPage( pageSize ) }.bind(this)
                } )
            }
        }
    },
    createNextPageNode : function( container ){
        if( this.nextPageNode ){
            this.nextPageNode.destroy();
            delete this.nextPageNode;
        }
        var pageSize = this.options.pageSize;
        if( this.options.currentPage != pageSize && pageSize != 1 && pageSize != 0 ){
            this.nextPageNode = new Element("div.nextPageNode", {
                "styles" : this.css.nextPageNode,
                "text" : "下一页"
            }).inject(container);
            this.nextPageNode.addEvents( {
                "mouseover" : function( ev ){ ev.target.setStyles( this.css.nextPageNode_over ) }.bind(this),
                "mouseout" : function( ev ){ ev.target.setStyles( this.css.nextPageNode ) }.bind(this),
                "click" : function(){ this.gotoPage(  this.options.currentPage+1 ) }.bind(this)
            })
        }
    },
    createPrevPageNode : function( container ){
        if( this.prevPageNode ){
            this.prevPageNode.destroy();
            delete this.prevPageNode;
        }
        var pageSize = this.options.pageSize;
        if( this.options.currentPage != 1 && pageSize != 1 && pageSize != 0 ){
            this.prevPageNode = new Element("div.prevPageNode", {
                "styles" : this.css.prevPageNode,
                "text" : "上一页"
            }).inject(container);
            this.prevPageNode.addEvents( {
                "mouseover" : function( ev ){ ev.target.setStyles( this.css.prevPageNode_over ) }.bind(this),
                "mouseout" : function( ev ){ ev.target.setStyles( this.css.prevPageNode ) }.bind(this),
                "click" : function(){ this.gotoPage(  this.options.currentPage-1 ) }.bind(this)
            })
        }
    },
    gotoPage : function( num ){
        if( num < 1 || num > this.options.pageSize )return;
        this.fireEvent( "jumpingPage", { pageNum : num }  );
        this.options.currentPage = num;
        this.load();
    },
    gotoItem : function( itemNum ){
        var pageNum = Math.ceil( itemNum / this.options.countPerPage );
        var index = itemNum % this.options.countPerPage;
        this.fireEvent( "jumpingPage", { pageNum : pageNum, itemNum : itemNum, index : index } );
        this.options.currentPage = pageNum;
        this.load();
    },
    destroy : function(){
        if( this.nextPageNode )this.nextPageNode.destroy();
        //delete this;
    }
});

MWF.xApplication.CRM.Template.SelectForm = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexView,
    initialize: function (container, data, app, explorer, options, para) {
        this.container = container;
        this.data = data||{};
        this.explorer = explorer;
        if( para ){
            this.app = app || para.app || this.explorer.app;
            this.lp = para.lp || this.explorer.lp || this.app.lp;
            this.css = para.css || this.explorer.css || this.app.css;
            this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
        }else{
            this.app = app || this.explorer.app;
            this.lp = this.explorer.lp || this.app.lp;
            this.css = this.explorer.css || this.app.css;
            this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
        }
        /*template 外边传进来*/
        if (!options.templateUrl) {
            options.templateUrl = this.explorer.path + "listItem.json"
        } else if (options.templateUrl.indexOf("/") == -1) {
            options.templateUrl = this.explorer.path + options.templateUrl;
        }
        console.log(options);
        this.setOptions(options);
        this.container.setAttribute("id","selectForm");
        this.container.setStyle("padding",0);
    },
    _createDocument: function(data){
        return new MWF.xApplication.CRM.Clue.Document(this.viewNode, data, this.explorer, this);
    },
    ayalyseTemplate: function () {
        MWF.getJSON(this.options.templateUrl, function (json) {
            this.template = json;
            console.log("this is template,",json);
        }.bind(this), false)
    },
    //一般需要重写分页方法
    _getCurrentPageData: function(callback, count, page, searchText){
        var category = this.category = this.options.category;
        if (!count)count = 10;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        if(searchText){
            filter = {
                key:searchText
            };
        }
        this.actions.getCustomerListPage(page, count, filter, function (json) {
            if (callback)callback(json);
        }.bind(this));

    },
    getResult : function(){
        var reslult = {};
        console.log(this.dataList,this.selectData);
        for(index in this.dataList){
            var data = this.dataList[index];
            if(data.id == this.selectData){
                reslult = data;
                break;
            }

        }
        return reslult;
    },

    useTablePlugins: function (cpage,searchText) {
        if(this.container.getNext(".laytable-box")) this.container.getNext(".laytable-box").destroy();;

        var that = this;
        var cdata = [];
        var cols = [];
        var col = [];
        var sortField = "";
        var sortType = "";
        var selectObject = this.template;
        var count = 10;
        sortField = selectObject.sortField;
        sortType = selectObject.sortType;
        if (!cpage)cpage = 1;
        cols = selectObject.field;
        //cols.push(col);
        this._getCurrentPageData(function (json) {
            json.data.each(function (data ) {
                cdata.push(data);
            }.bind(this));
            layui.config({
                base: '/x_component_CRM/$Template/plugins/table2/'
            }).use(['table2', "table2"], function () {
                var table = layui.table2;
                console.log(cols);
                var tableIns = table.render({
                    elem: "#selectForm",
                    data: cdata,
                    height: 310,
                    width: '100%',
                    page: {
                        align: 'right',
                        groups: 5,//显示连续页码数量
                        curr:1,
                        count: json.count,//总条数
                        limit:10,
                        limits:[10, 20, 30, 40, 50, 60, 70, 80, 90]
                    },
                    initSort: {
                        sortField: sortField,
                        sortType: sortType
                    },
                    cols:[cols]
                });

            });
            var self = this;
            this.dataList = cdata;
            this.selectData = {};
            var tableform=this.container.getNext("div");
            tableform.getElements(".laytable-body tr").forEach(function(e,i){
                e.addEvents({
                    mouseover:function(){
                        this.addClass("laytable-tr-hover");
                    }.bind(e),
                    click:function () {
                        var ele = this.getElement(".checkbox");
                        if(ele.hasClass("checked")){
                            self.selectCheckbox = null;
                            self.selectData = {};
                            ele.removeClass("checked");
                        }else{
                            if(!self.selectCheckbox){
                            }else{
                                self.selectCheckbox.removeClass("checked");
                            }
                            self.selectData = ele.getAttribute("sid");//self.dataList[i];
                            self.selectCheckbox = ele;
                            ele.addClass("checked");
                        }
                    }.bind(e),
                    mouseleave:function () {
                        this.removeClass("laytable-tr-hover");
                    }.bind(e)
                });
            }.bind(this));

            jQuery(tableform).find(".laytable-page-pagination a").each(function(index,element){
                    jQuery(element).on("click", function () {
                        //cpage = parseInt(jQuery(element).attr("value"))+cpage;
                        var topage = 1;
                        if(jQuery(element).attr("value")=="-1" || jQuery(element).attr("value")=="+1"){
                            topage = parseInt(jQuery(element).attr("value"))+topage;
                        }else{
                            topage = parseInt(jQuery(element).text());
                        }

                        if(jQuery(element).attr("class")!="page-item page-last rayui-disabled" && jQuery(element).attr("class")!="page-item page-prev rayui-disabled"){
                            //that.useTablePlugins(topage);
                            var searchText = jQuery(".headSearchInput").val();
                            if(searchText!=""){
                                that.useTablePlugins(topage,searchText);
                            }else{
                                that.useTablePlugins(topage);
                            }
                        }

                    });
                }
            );
            jQuery(tableform).find(".laytable-page-btnok").on("click", function () {
                var cpage = parseInt(jQuery(".laytable-page-input").val());
                var searchText = jQuery(".headSearchInput").val();
                if(searchText!=""){
                    that.useTablePlugins(cpage,searchText);
                }else{
                    that.useTablePlugins(cpage);
                }
                //that.useTablePlugins(cpage);
            });


            jQuery(tableform).find(".page-item").each(function(index,element){
                if(jQuery(element).attr("value")==(cpage+"")){
                    jQuery(element).attr("class","page-item page-active");
                }else{
                    if(jQuery(element).attr("value")!="-1" &&  jQuery(element).attr("value")!="+1"){
                        jQuery(element).attr("class","page-item");
                    }
                }
            });

        }.bind(this),count, cpage,searchText);
    }

});