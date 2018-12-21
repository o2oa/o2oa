MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};
MWF.xApplication.Strategy.Template = MWF.xApplication.Strategy.Template || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);

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
            el.inject( container );
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



MWF.xApplication.Strategy.Template.Select = new Class({
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
        this.path = "/x_component_Strategy/Template/";
        this.loadCss();

        this.node = $(node);
        this.actions = actions;
    },
    loadCss: function () {
        this.cssPath = "/x_component_Strategy/$Template/" + this.options.style + "/css.wcss";
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
                    "background":"url(/x_component_Strategy/$Template/default/icons/arrow-up.png) no-repeat center"
                });
                //if(_self.explorer.listContentDiv)_self.explorer.listContentDiv.destroy();
                //if(_self.explorer.listDiv)_self.explorer.listDiv.destroy();
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
                            _self.selectArrowDiv.setStyles({"background":"url(/x_component_Strategy/$Template/default/icons/arrow.png) no-repeat center"});
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
    setDeptList:function(data,callback){
        data = data || {};
        var _self = this;
        this.node.removeEvents("click");
        this.node.addEvents({
            "click":function(e){
                //if(!data.childNodes)return false;
                if(_self.node.get("available")=="no") return false;
                _self.selectArrowDiv.setStyles({
                    "background":"url(/x_component_Strategy/$Template/default/icons/arrow-up.png) no-repeat center"
                });
                //if(_self.explorer.listContentDiv)_self.explorer.listContentDiv.destroy();
                //if(_self.explorer.listDiv)_self.explorer.listDiv.destroy();
                _self.explorer.listContentDiv = new Element("div.listContentDiv",{"styles":_self.css.listContentDiv,"id":"listContentDiv"}).inject(_self.node);
                _self.explorer.listContentDiv.setStyles({
                    "width":_self.node.getSize().x+"px",
                    "margin-top":(_self.node.getSize().y)+"px",
                    "z-index":"300"
                });

                _self.listDiv = new Element("div.listDiv",{"styles":_self.css.listDiv}).inject(_self.explorer.listContentDiv);
                _self.app.setScrollBar(_self.listDiv);

                data.unshift(_self.lp.defaultSelect);


                data.each(function(d){
                    var listLi = new Element("li.listLi",{
                        "styles":_self.css.listLi,
                        "unit":d==_self.lp.defaultSelect ? _self.lp.defaultSelect : d,
                        "text": d.split("@")[0]
                    }).inject(_self.listDiv);
                    listLi.setStyles({
                        "color":_self.selectValueDiv.get("unit")==listLi.get("unit")?"#ffffff":"",
                        "background-color":_self.selectValueDiv.get("unit")==listLi.get("unit")?"#3d77c1":""
                    });
                    listLi.addEvents({
                        "click":function(ev){
                            _self.selectValueDiv.set({
                                "text":this.get("text"),
                                "unit":this.get("unit")
                            });
                            _self.node.set("unit",this.get("unit"));
                            _self.explorer.listContentDiv.destroy();
                            _self.selectArrowDiv.setStyles({"background":"url(/x_component_Strategy/$Template/default/icons/arrow.png) no-repeat center"});
                            if(callback)callback(d);
                            ev.stopPropagation();
                        },
                        "mouseover":function(){
                            if(this.get("unit") != _self.selectValueDiv.get("unit")){
                                this.setStyles({
                                    "background-color":"#ccc",
                                    "color":"#ffffff"
                                });
                            }
                        },
                        "mouseout":function(){
                            if(this.get("unit") != _self.selectValueDiv.get("unit")){
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
    setPerPageList:function(data,callback){
        data = data || {};
        var _self = this;
        this.node.removeEvents("click");
        this.node.addEvents({
            "click":function(e){
                if(!data.value || !data.text)return false;
                if(_self.node.get("available")=="no") return false;
                _self.selectArrowDiv.setStyles({
                    "background":"url(/x_component_Strategy/$Template/default/icons/arrow-up.png) no-repeat center"
                });
                //if(_self.explorer.listContentDiv)_self.explorer.listContentDiv.destroy();
                //if(_self.explorer.listDiv)_self.explorer.listDiv.destroy();
                _self.explorer.listContentDiv = new Element("div.listContentDiv",{"styles":_self.css.listContentDiv,"id":"listContentDiv"}).inject(_self.node);
                _self.explorer.listContentDiv.setStyles({
                    "width":_self.node.getSize().x+"px",
                    "margin-top":(_self.node.getSize().y)+"px",
                    "z-index":"300"
                });

                _self.listDiv = new Element("div.listDiv",{"styles":_self.css.listDiv}).inject(_self.explorer.listContentDiv);

                _self.app.setScrollBar(_self.listDiv);

                data.text.unshift({
                    "configname":_self.lp.defaultSelect
                });


                data.text.each(function(d){
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
                            _self.selectArrowDiv.setStyles({"background":"url(/x_component_Strategy/$Template/default/icons/arrow.png) no-repeat center"});
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

                data.text.splice(0,1);

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
                    "background":"url(/x_component_Strategy/$Template/default/icons/arrow-up.png) no-repeat center"
                });
                //if(_self.explorer.listContentDiv)_self.explorer.listContentDiv.destroy();
                //if(_self.explorer.listDiv)_self.explorer.listDiv.destroy();
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
                            _self.selectArrowDiv.setStyles({"background":"url(/x_component_Strategy/$Template/default/icons/arrow.png) no-repeat center"});
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

MWF.xApplication.Strategy.Template.PopupForm = new Class({
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
        "hasMark" : true,
        "title": "",
        "draggable": false,
        "maxAction" : "false",
        "closeAction": true,
        "relativeToApp" : true,
        "sizeRelateTo" : "app" //desktop
    },
    initialize: function (explorer, data, options, para) {
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

        this.cssPath = "/x_component_Strategy/$Template/"+this.options.style+"/popup.wcss";

        this.load();
    },
    load: function () {
        this._loadCss();
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
            if(key)isEmptyObject = false;
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

MWF.xApplication.Strategy.Template.view = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    Implements: [Options, Events],

    createViewHead: function () {
        this.fireEvent("queryCreateViewHead");
        this._queryCreateViewHead( );
        if (this.template) {
            if (!this.template.headSetting || this.template.headSetting.disable || !this.template.headSetting.html) {
                return;
            }
        }
        var _self = this;

        ////////////修改 增加thead,tbody//////////////////
        this.viewHeadNode = this.formatElement(this.viewNode, this.template.viewHeadSetting);
        //this.viewBodyNode = this.formatElement(this.viewNode, this.template.viewBodySetting);

        var headNode = this.headNode = this.formatElement(this.viewHeadNode, this.template.headSetting);
        //var headNode = this.headNode = this.formatElement(this.viewNode, this.template.headSetting);
        ////////////修改 增加thead,tbody//////////////////

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
                    this.selectAllCheckbox();
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
        this._postCreateViewHead( headNode );
    },
    createViewBody : function(){
        this.viewBodyNode = this.formatElement(this.viewNode, this.template.viewBodySetting);
        this.loadElementList();
    }


});

MWF.xApplication.Strategy.Template.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    Implements: [Options, Events]
});

MWF.xApplication.Strategy.Template.Paging = new Class({
    Extends: MWF.xApplication.Template.Explorer.Paging,
    Implements: [Options, Events]

});