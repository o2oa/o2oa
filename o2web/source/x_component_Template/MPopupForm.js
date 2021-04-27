MWF.xApplication.Template = MWF.xApplication.Template || {};
//MWF.xDesktop.requireApp("Template", "lp." + MWF.language, null, false);
MWF.xApplication.Template.MPopupForm = MPopupForm = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": 500,
        "height": 450,
        "top": null,
        "left": null,
        "bottom" : null,
        "right" : null,
        "minWidth" : 300,
        "minHeight" : 220,

        "isLimitSize": true,
        "ifFade": true,
        "hasTop": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasIcon": true,
        "hasBottom": true,
        "hasMask" : true,
        "closeByClickMask" : false,
        "hasScroll" : true,
        "scrollType" : "",

        "title": "",
        "draggable": false,
        "resizeable" : false,
        "maxAction" : false,
        "closeAction": true,

        "relativeToApp" : true,
        "sizeRelateTo" : "app", //desktop
        "resultSeparator" : ","
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

        this.cssPath = "../x_component_Template/$MPopupForm/"+this.options.style+"/css.wcss";

        this.load();
    },
    load: function () {
        this._loadCss();
    },
    _loadCss: function(){
        var css = {};
        var r = new Request.JSON({
            url: o2.filterUrl(this.cssPath),
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
            if(key){
                isEmptyObject = false;
                break;
            }
        }
        if( !isEmptyObject ){
            this.css = Object.merge(  css, this.css );
        }
    },
    reload : function( keepData ){
        if( keepData ){
            this.data = this.form.getResult(false, this.options.resultSeparator, false, false, true);
        }
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        if( this.formAreaNode )this.formAreaNode.destroy();
        if( this.isNew ){
            this.create();
        }else if( this.isEdited ){
            this.edit();
        }else{
            this.open();
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
                    },
                    "mousewheel": function (e) {
                        if (e.stopPropagation) e.stopPropagation();
                        else e.cancelBubble = true;

                        if (e.preventDefault) e.preventDefault();
                        else e.returnValue = false;
                    },
                    "DOMMouseScroll": function (e) {
                        if (e.stopPropagation) e.stopPropagation();
                        else e.cancelBubble = true;

                        if (e.preventDefault) e.preventDefault();
                        else e.returnValue = false;
                    }
                }
            }).inject( this.container || this.app.content);
        }

        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        });

        this.createFormNode();

        if( this.formMaskNode ){
            this.formAreaNode.inject(this.formMaskNode , "after");
        }else{
            this.formAreaNode.inject( this.container || this.app.content );
        }
        if (this.options.ifFade){
            this.formAreaNode.fade("in");
        }else{
            this.formAreaNode.setStyle("opacity", 1);
        }


        this.setFormNodeSize();
        this.setFormNodeSizeFun = this.setFormNodeSize.bind(this);
        if( this.app && this.app.addEvent )this.app.addEvent("resize", this.setFormNodeSizeFun);

        if (this.options.draggable && this.formTopNode) {
            var size = (this.container || this.app.content).getSize();
            var nodeSize = this.formAreaNode.getSize();
            this.formAreaNode.makeDraggable({
                "handle": this.formTopNode,
                "limit": {
                    "x": [0, size.x - nodeSize.x],
                    "y": [0, size.y - nodeSize.y]
                },
                "onDrag": function(){
                    this.fireEvent("drag");
                }.bind(this),
                "onComplete": function(){
                    this.fireEvent("dragCompleted");
                }.bind(this)
            });
        }

        if( this.options.closeByClickMask && this.formMaskNode ){
            this.formMaskNode.addEvent("click", function(e){
                this.close(e)
            }.bind(this));
        }

        if (this.options.resizeable){
            this.resizeNode = new Element("div.resizeNode", {
                "styles": this.css.resizeNode
            }).inject(this.formNode);
            this.formAreaNode.makeResizable({
                "handle": this.resizeNode,
                "limit": {x:[ this.options.minWidth, null], y:[this.options.minHeight, null]},
                "onDrag": function(){
                    var size = this.formAreaNode.getComputedSize();
                    this.setNodesSize( size.width, size.height );
                    this.fireEvent("resize");
                }.bind(this),
                "onComplete": function(){
                    var size = this.formAreaNode.getComputedSize();
                    this.options.width = size.width;
                    this.options.height = size.height;
                    if( this.oldCoordinate ){
                        this.oldCoordinate.width = size.width;
                        this.oldCoordinate.height = size.height;
                    }
                    this.fireEvent("resizeCompleted");
                }.bind(this)
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
            if( this.options.scrollType == "window" ){
                this.formContentNode.setStyle("overflow","auto");
                this.formTableContainer.setStyle("overflow","visible");
            }else{
                MWF.require("MWF.widget.ScrollBar", function () {
                    new MWF.widget.ScrollBar(this.formTableContainer, {
                        "indent": false,
                        "style": "xApp_TaskList",
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
        }
    },
    _setCustom : function(){

    },
    createTopNode: function () {

        this.fireEvent("queryCreateTop");
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
                this.formTopCloseActionNode = new Element("div", {
                    "styles": this.css.formTopCloseActionNode,
                    "title" : MWF.xApplication.Template.LP.MPopupForm.close
                }).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function ( ev ) {
                    this.close();
                    ev.stopPropagation();
                }.bind(this))
            }

            if( this.options.maxAction ){
                this.formTopMaxActionNode = new Element("div", {
                    "styles": this.css.formTopMaxActionNode,
                    "title" : MWF.xApplication.Template.LP.MPopupForm.max
                }).inject(this.formTopNode);
                this.formTopMaxActionNode.addEvent("click", function () {
                    this.maxSize()
                }.bind(this));

                this.formTopRestoreActionNode = new Element("div", {
                    "styles": this.css.formTopRestoreActionNode,
                    "title": MWF.xApplication.Template.LP.MPopupForm.restore
                }).inject(this.formTopNode);
                this.formTopRestoreActionNode.addEvent("click", function () {
                    this.restoreSize()
                }.bind(this));

                this.formTopNode.addEvent("dblclick", function(){
                    this.switchMax();
                }.bind(this));
            }

            if(this.options.hasTopContent){
                this.formTopContentNode = new Element("div.formTopContentNode", {
                    "styles": this.css.formTopContentNode
                }).inject(this.formTopNode);

                this._createTopContent();
            }

        }

        this.fireEvent("postCreateTop");

        //if (!this.formTopNode) {
        //    this.formTopNode = new Element("div.formTopNode", {
        //        "styles": this.css.formTopNode,
        //        "text": this.options.title
        //    }).inject(this.formNode);
        //
        //    this._createTopContent();
        //
        //    if (this.options.closeAction) {
        //        this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
        //        this.formTopCloseActionNode.addEvent("click", function () {
        //            this.close()
        //        }.bind(this))
        //    }
        //}
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
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);


        this._createTableContent();
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
                //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
            "<tr><td styles='formTableTitle' lable='empName'></td>" +
            "    <td styles='formTableValue' item='empName'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='departmentName'></td>" +
            "    <td styles='formTableValue' item='departmentName'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='recordDateString'></td>" +
            "    <td styles='formTableValue' item='recordDateString'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='status'></td>" +
            "    <td styles='formTableValue' item='status'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='appealReason'></td>" +
            "    <td styles='formTableValue' item='appealReason'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='appealDescription'></td>" +
            "    <td styles='formTableValue' item='appealDescription'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='opinion1'></td>" +
            "    <td styles='formTableValue' item='opinion1'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, {empName: "xadmin"}, {
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    empName: {text: "姓名", type: "innertext"},
                    departmentName: {text: "部门", tType: "department", notEmpty: true},
                    recordDateString: {text: "日期", tType: "date"},
                    status: {text: "状态", tType: "number"},
                    appealReason: {
                        text: "下拉框",
                        type: "select",
                        selectValue: ["测试1", "测试2"]
                    },
                    appealDescription: {text: "描述", type: "textarea"},
                    opinion1: {text: "测试", type: "button", "value": "测试"}
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    createBottomNode: function () {
        this.fireEvent("queryCreateBottom");
        this.formBottomNode = new Element("div.formBottomNode", {
            "styles": this.css.formBottomNode
        }).inject(this.formNode);

        this._createBottomContent();
        this.fireEvent("postCreateBottom");
    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.cancel
        }).inject(this.formBottomNode);


        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("div.formOkActionNode", {
                "styles": this.css.formOkActionNode,
                "text": this.lp.ok
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.ok(e);
            }.bind(this));
        }
    },
    cancel: function (e) {
        this.fireEvent("queryCancel");
        this.close();
        this.fireEvent("postCancel");
    },
    close: function (e) {
        this.fireEvent("queryClose");
        this._close();
        //if( this.form ){
        //    this.form.destroy();
        //}
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        if( this.formAreaNode )this.formAreaNode.destroy();
        this.fireEvent("postClose");
        delete this;
    },
    _close: function(){

    },
    ok: function (e) {
        this.fireEvent("queryOk");
        var data = this.form.getResult(true, this.options.resultSeparator, true, false, true);
        if (data) {
            this._ok(data, function (json) {
                if (json.type == "error") {
                    if( this.app && this.app.notice )this.app.notice(json.message, "error");
                } else {
                    if( this.formMaskNode )this.formMaskNode.destroy();
                    if( this.formAreaNode )this.formAreaNode.destroy();
                    if (this.explorer && this.explorer.view)this.explorer.view.reload();
                    if( this.app && this.app.notice)this.app.notice(this.isNew ? this.lp.createSuccess : this.lp.updateSuccess, "success");
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
    switchMax : function(){
        if( !this.isMax ){
            this.maxSize();
        }else{
            this.restoreSize();
        }
    },
    maxSize: function(){
        if(!this.oldCoordinate)this.oldCoordinate = {
            width : this.options.width,
            height : this.options.height,
            top : this.options.top,
            left : this.options.left,
            bottom : this.options.bottom,
            right : this.options.right
        };
        this.options.width = "100%";
        this.options.height = "100%";
        this.options.top = null;
        this.options.left = null;
        this.options.bottom = null;
        this.options.right = null;
        this.setFormNodeSize();
        this.formTopMaxActionNode.setStyle("display","none");
        this.formTopRestoreActionNode.setStyle("display","");
        this.isMax = true;
        this.fireEvent("max");
    },
    restoreSize : function(){
        if( this.oldCoordinate){
            this.options.width = this.oldCoordinate.width;
            this.options.height = this.oldCoordinate.height;
            this.options.top = this.oldCoordinate.top;
            this.options.left = this.oldCoordinate.left;
            this.options.bottom = this.oldCoordinate.bottom;
            this.options.right = this.oldCoordinate.right;
        }
        this.setFormNodeSize();
        this.formTopMaxActionNode.setStyle("display","");
        this.formTopRestoreActionNode.setStyle("display","none");
        this.isMax = false;
        this.fireEvent("restore");
    },
    setFormNodeSize: function (width, height, top, left, bottom, right) {

        this._beforeFormNodeSize();

        if (!width)width = this.options.width ? this.options.width : "50%";
        if (!height)height = this.options.height ? this.options.height : "50%";
        if (!top && top != 0 ) top = this.options.top; // ? this.options.top : 0;
        if (!left && left != 0) left = this.options.left; // ? this.options.left : 0;
        if (!bottom && bottom != 0) bottom = this.options.bottom; // ? this.options.bottom : 0;
        if (!right && right != 0) right = this.options.right; // ? this.options.right : 0;

        width = width.toString();
        height = height.toString();

        var allSize = ( this.container || this.app.content).getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        if (this.options.isLimitSize){
            if( "%" != width.substr(width.length - 1, 1) ){
                if( allSize.x < parseInt(width) )width = allSize.x;
            }
            if( "%" != height.substr(height.length - 1, 1) ){
                if( allSize.y < parseInt(height) )height = allSize.y;
            }
        }

        //if( width != "auto" ){
            "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
            this.options.minWidth > width && (width = this.options.minWidth);
        //}
        //if( height != "auto" ){
            "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
            this.options.minHeight > height && (height = this.options.minHeight);
        //}

        var styles = {
            "width": "" + width + "px",
            "height": "" + height + "px"
        };

        if( top != null ){
            styles.top = "" + top + "px";
            styles.bottom = "auto";
        }else if( bottom != null ){
            styles.top = "auto";
            styles.bottom = "" + bottom + "px";
        }else{
            styles.top = "" + parseInt((limitHeight - height) / 2, 10) + "px";
            styles.bottom = "auto";
        }

        if( left != null ){
            styles.left = "" + left + "px";
            styles.right = "auto";
        }else if( right != null ){
            styles.left = "auto";
            styles.right = "" + right + "px";
        }else{
            styles.left = "" + parseInt((limitWidth - width) / 2, 10) + "px";
            styles.right = "auto";
        }

        if( this.formAreaNode )this.formAreaNode.setStyles(styles);

        this._setFormNodeSize(styles);

        this.setNodesSize( width, height );
    },
    _beforeFormNodeSize : function(){

    },
    _setFormNodeSize: function( styles ){

    },
    setNodesSize: function(width, height){
        //if( height == "auto" )return;
        this.options.minWidth > width && (width = this.options.minWidth);
        this.options.minHeight > height && (height = this.options.minHeight);

        this.formNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y;
        var marginTop = parseFloat(this.formContentNode.getStyle( "margin-top" )) || 0;
        var marginBottom = parseFloat(this.formContentNode.getStyle( "margin-bottom" )) || 0;
        var formContentHeight = contentHeight - marginTop - marginBottom;
        this.formContentNode.setStyles({
            "height": "" + formContentHeight + "px"
        });

        var paddingTop = parseFloat( this.formContentNode.getStyle( "padding-top" )) || 0;
        var paddingBottom = parseFloat( this.formContentNode.getStyle( "padding-bottom" )) || 0;
        marginTop = parseFloat( this.formTableContainer.getStyle( "margin-top" )) || 0;
        marginBottom = parseFloat( this.formTableContainer.getStyle( "margin-bottom" )) || 0;
        var tablePaddingTop = parseFloat( this.formTableContainer.getStyle( "padding-top" )) || 0;
        var tablePaddingTBottom = parseFloat( this.formTableContainer.getStyle( "padding-bottom" )) || 0;
        var formTableHeight = contentHeight - marginTop - marginBottom - paddingTop - paddingBottom - tablePaddingTop - tablePaddingTBottom;

        if( this.options.scrollType == "window" ){
            formTableHeight = formTableHeight - 10;
        }

        this.formTableContainer.setStyles({
            "height": "" + formTableHeight + "px"
        });
        this._setNodesSize( width, height, formContentHeight, formTableHeight );
        this.fireEvent("resizeForm");
    },
    _setNodesSize : function(width, height, formContentHeight, formTableHeight ){

    }
});
