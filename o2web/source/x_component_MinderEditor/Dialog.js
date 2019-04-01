MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.require("MWF.widget.ImageClipper", null, false);

MWF.xApplication.MinderEditor.HyperLinkForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": 700,
        //"height": 300,
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "链接"
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='url' width='20%'></td>" +
            "    <td styles='formTableValue14' item='url' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='title'></td>" +
            "    <td styles='formTableValue14' item='title' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        var data = this.app.minder.queryCommandValue('HyperLink');

        this.form = new MForm(this.formTableArea, data, {
            isEdited: true,
            style : "report",
            hasColon : true,
            itemTemplate: {
                url: { text : "链接地址",  notEmpty : true,
                    validRule : { isInvalid : function( value, it ){
                        var urlRegex = '^(?!mailto:)(?:(?:http|https|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?:(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[0-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))|localhost)(?::\\d{2,5})?(?:(/|\\?|#)[^\\s]*)?$';
                        var R_URL = new RegExp(urlRegex, 'i');
                        return R_URL.test( value )
                    }.bind(this)},
                    validMessage : { isInvalid : "请输入正确的链接" },
                    attr : { placeholder : "必填：以 http(s):// 或 ftp:// 开头" }
                },
                title: { text : "提示文本", attr : { placeholder : "选填：鼠标在链接上悬停时提示的文本" } }
            }
        }, this.app);
        this.form.load();

    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.removeAction = new Element("button.inputCancelButton", {
            "styles": this.css.inputCancelButton,
            "text": "删除链接"
        }).inject(this.formBottomNode);

        this.removeAction.addEvent("click", function (e) {
            this.remove(e);
        }.bind(this));

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            this.app.minder.execCommand('HyperLink', data.url, data.title || '');
            this.close();
        }
    },
    remove: function( ev ){
        this.app.minder.execCommand('HyperLink', null );
        this.close();
    }
});

//MWF.xApplication.MinderEditor.ImageForm = new Class({
//    Extends: MPopupForm,
//    Implements: [Options, Events],
//    options: {
//        "style": "report",
//        "width": 800,
//        "height": 640,
//        "hasTop": true,
//        "hasIcon": false,
//        "draggable": true,
//        "title" : "图片"
//    },
//    createContent: function () {
//
//        this.createTab();
//
//        this.formContentNode = new Element("div.formContentNode", {
//            "styles": this.css.formContentNode
//        }).inject(this.formNode);
//
//        this.formTableContainer = new Element("div.formTableContainer", {
//            "styles": this.css.formTableContainer
//        }).inject(this.formContentNode);
//
//        this.formTableArea = new Element("div.formTableArea", {
//            "styles": this.css.formTableArea
//        }).inject(this.formTableContainer);
//
//        this._createTableContent();
//    },
//    _createTableContent: function () {
//
//        this.linkContainer = new Element("div.linkContainer").inject(this.formTableArea);
//
//        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
//            "<tr><td styles='formTableTitle' lable='url' width='20%'></td>" +
//            "    <td styles='formTableValue14' item='url' colspan='3'></td></tr>" +
//            "<tr><td styles='formTableTitle' lable='title'></td>" +
//            "    <td styles='formTableValue14' item='title' colspan='3'></td></tr>" +
//            "<tr><td styles='formTableTitle'>预览：</td>" +
//            "    <td styles='formTableValue14' item='preview' colspan='3'></td></tr>" +
//            "</table>";
//        this.linkContainer.set("html", html);
//
//        var data = this.app.minder.queryCommandValue('image');
//
//        this.linkform = new MForm(this.linkContainer, data, {
//            isEdited: true,
//            style : "report",
//            hasColon : true,
//            itemTemplate: {
//                url: { text : "图片地址",  notEmpty : true,
//                    validRule : { isInvalid : function( value, it ){
//                        var R_URL = /^https?\:\/\/\w+/;
//                        return R_URL.test( value )
//                    }.bind(this)},
//                    validMessage : { isInvalid : "请输入正确的链接" },
//                    attr : { placeholder : "必填：以 http(s):// 开始" },
//                    event : { blur : function( it ){
//                        if( it.getValue() )it.form.getItem("preview").setValue( it.getValue() )
//                    }.bind(this)}
//                },
//                title: { text : "提示文本", attr : { placeholder : "选填：鼠标在图片上悬停时提示的文本" } },
//                preview : { type : "img", defaultValue : data.url || "", style : { "max-width" : "400px", "max-height" : "260px" } }
//            }
//        }, this.app);
//        this.linkform.load();
//
//
//
//        this.uploadContainer = new Element("div.uploadContainer", { styles : {"display":"none"} }).inject( this.formTableArea );
//
//        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
//            "<tr><td item='image' colspan='4' style='padding-bottom: 10px;'></td></tr>" +
//            "<tr><td styles='formTableTitle' lable='title2' width='20%'></td>" +
//            "    <td styles='formTableValue14' item='title2' colspan='3'></td></tr>" +
//            "</table>";
//        this.uploadContainer.set("html", html);
//
//        var data = this.app.minder.queryCommandValue('image');
//
//        this.uploadform = new MForm(this.uploadContainer, data, {
//            isEdited: true,
//            style : "report",
//            hasColon : true,
//            itemTemplate: {
//                title2: { text : "提示文本", attr : { placeholder : "选填：鼠标在图片上悬停时提示的文本" } }
//            }
//        }, this.app);
//        this.uploadform.load();
//
//        this.image = new MWF.widget.ImageClipper(this.uploadContainer.getElement("[item='image']"), {
//            "aspectRatio": 0,
//            "description" : "",
//            "imageUrl" : "",
//            "ratioAdjustedEnable" : true,
//            "reference" :  this.app.data.id || "1111",
//            "referenceType": "mindInfo",
//            "fromFileEnable" : false,
//            "resetEnable" : true
//        });
//        this.image.load();
//
//    },
//    createTab: function(){
//        var _self = this;
//
//        this.tabContainer = new Element("div.formTabContainer",{
//            styles : this.css.formTabContainer
//        }).inject(this.formNode);
//
//        var tabNode = new Element("div.formTabNode", {
//            "styles": this.css.formTabNode,
//            "text" : "外链图片"
//        }).inject(this.tabContainer);
//        tabNode.addEvents({
//            "mouseover" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode_over) }.bind({node : tabNode }),
//            "mouseout" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode) }.bind({node : tabNode }),
//            "click":function(){
//                if( _self.currentTabNode )_self.currentTabNode.setStyles(_self.css.formTabNode);
//                _self.currentTabNode = this.node;
//                this.node.setStyles(_self.css.formTabNode_current);
//                _self.linkContainer.setStyle("display","");
//                _self.uploadContainer.setStyle("display","none");
//            }.bind({ node : tabNode })
//        })
//        tabNode.setStyles( this.css.formTabNode_current );
//        _self.currentTabNode = tabNode;
//
//        var tabNode = new Element("div.tabNode", {
//            "styles": this.css.formTabNode,
//            "text" : "上传图片"
//        }).inject(this.tabContainer);
//        tabNode.addEvents({
//            "mouseover" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode_over) }.bind({node : tabNode }),
//            "mouseout" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode) }.bind({node : tabNode }),
//            "click":function(){
//                if( _self.currentTabNode )_self.currentTabNode.setStyles(_self.css.formTabNode);
//                _self.currentTabNode = this.node;
//                this.node.setStyles(_self.css.formTabNode_current);
//                _self.linkContainer.setStyle("display","none");
//                _self.uploadContainer.setStyle("display","");
//            }.bind({ node : tabNode })
//        })
//    },
//    _createBottomContent: function () {
//
//        if (this.isNew || this.isEdited) {
//
//            this.okActionNode = new Element("button.inputOkButton", {
//                "styles": this.css.inputOkButton,
//                "text": "确定"
//            }).inject(this.formBottomNode);
//
//            this.okActionNode.addEvent("click", function (e) {
//                this.save(e);
//            }.bind(this));
//        }
//
//        this.removeAction = new Element("button.inputCancelButton", {
//            "styles": this.css.inputCancelButton,
//            "text": "删除图片"
//        }).inject(this.formBottomNode);
//
//        this.removeAction.addEvent("click", function (e) {
//            this.remove(e);
//        }.bind(this));
//
//        this.cancelActionNode = new Element("button.inputCancelButton", {
//            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
//            "text": "关闭"
//        }).inject(this.formBottomNode);
//
//        this.cancelActionNode.addEvent("click", function (e) {
//            this.close(e);
//        }.bind(this));
//
//    },
//    save: function(){
//        if( this.image.resizedImage ){
//            this.image.uploadImage( function( json ){
//                var data = {
//                    url : MWF.xDesktop.getImageSrc( json.id ),
//                    title :  this.uploadform.getResult(true,null,true,false,true)["title2"]
//                };
//                this.app.minder.execCommand('image', data.url, data.title || '', json.id);
//                this.close();
//            }.bind(this));
//        }else{
//            var data = this.linkform.getResult(true,null,true,false,true);
//            if( data ){
//                this.app.minder.execCommand('image', data.url, data.title || '', '');
//                this.close();
//            }
//        }
//    },
//    remove: function( ev ){
//        this.app.minder.execCommand('image', '' );
//        this.close();
//    },
//    setFormNodeSize: function (width, height, top, left) {
//        if (!width)width = this.options.width ? this.options.width : "50%";
//        if (!height)height = this.options.height ? this.options.height : "50%";
//        if (!top) top = this.options.top ? this.options.top : 0;
//        if (!left) left = this.options.left ? this.options.left : 0;
//
//        var containerSize = this.container.getSize();
//        if( containerSize.x < width )width = containerSize.x;
//        if( containerSize.y < height )height = containerSize.y;
//
//        var allSize = this.app.content.getSize();
//        var limitWidth = allSize.x; //window.screen.width
//        var limitHeight = allSize.y; //window.screen.height
//
//        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
//        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
//        300 > width && (width = 300);
//        220 > height && (height = 220);
//
//        top = top || parseInt((limitHeight - height) / 2, 10); //+appTitleSize.y);
//        left = left || parseInt((limitWidth - width) / 2, 10);
//
//        this.formAreaNode.setStyles({
//            "width": "" + width + "px",
//            "height": "" + height + "px",
//            "top": "" + top + "px",
//            "left": "" + left + "px"
//        });
//
//        this.formNode.setStyles({
//            "width": "" + width + "px",
//            "height": "" + height + "px"
//        });
//
//        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
//        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
//        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};
//        var tabSize = this.tabContainer ? this.tabContainer.getSize() : {x: 0, y: 0};
//
//        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y - tabSize.y;
//        //var formMargin = formHeight -iconSize.y;
//        this.formContentNode.setStyles({
//            "height": "" + contentHeight + "px"
//        });
//        this.formTableContainer.setStyles({
//            "height": "" + contentHeight + "px"
//        });
//    }
//});

MWF.xApplication.MinderEditor.NoteForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": 300,
        "height": "100%",
        "right" : 0,
        "hasTop": true,
        "hasIcon": false,
        "hasMask" : false,
        "hasBottom" : false,
        "draggable": true,
        "resizeable": true,
        "maxAction" : true,
        "title" : "备注"
    },
    _createTableContent: function () {
        this.formTableContainer.setStyle("width","100%");
        this.formTableArea.setStyle("height","100%");

        var html = "<div item='content' style='height: 100%;'></div>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, {}, {
                style: "report",
                isEdited: true,
                itemTemplate: {
                    content: { type : "rtf", RTFConfig : {
                        //skin : "bootstrapck",
                        "resize_enabled": false,
                        toolbar : [
                            { name: 'document', items : [ 'Preview' ] },
                            //{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
                            { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','-','RemoveFormat' ] },
                            //{ name: 'paragraph', items : [ 'JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
                            { name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
                            { name: 'colors', items : [ 'TextColor','BGColor' ] },
                            { name: 'links', items : [ 'Link','Unlink' ] },
                            //{ name: 'insert', items : [ 'Image' ] },
                            { name: 'tools', items : [ 'Maximize','-','About' ] }
                        ]
                    }}
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _setNodesSize : function( width, height, formContentHeight, formTableHeight ){
        this.form.getItem("content").editor.resize(null,formTableHeight );
    }
});

//MWF.xApplication.MinderEditor.NoteForm = new Class({
//    Extends: MPopupForm,
//    Implements: [Options, Events],
//    options: {
//        "style": "report",
//        "width": 300,
//        "height": "100%",
//        "right" : 0,
//        "hasTop": true,
//        "hasIcon": false,
//        "hasMask" : false,
//        "hasBottom" : false,
//        "draggable": true,
//        "resizeable": true,
//        "maxAction" : true,
//        "title" : "备注"
//    },
//    _createTableContent: function () {
//        this.formTableContainer.setStyle("width","100%");
//        this.formTableArea.setStyle("height","100%");
//        var codeMirrorPath = COMMON.contentPath+"/res/framework/codemirror";
//        var markedPath = COMMON.contentPath+"/res/framework/marked";
//
//        var jsModules = [
//            codeMirrorPath + "/lib/codemirror.js",
//            codeMirrorPath + "/mode/xml/xml.js",
//            codeMirrorPath + "/mode/javascript/javascript.js",
//            codeMirrorPath + "/mode/css/css.js",
//            codeMirrorPath + "/mode/htmlmixed/htmlmixed.js",
//            codeMirrorPath + "/mode/markdown/markdown.js",
//            codeMirrorPath + "/addon/mode/overlay.js",
//            codeMirrorPath + "/mode/gfm/gfm.js",
//            markedPath + "/lib/marked.js"
//        ];
//
//        COMMON.AjaxModule.loadCss( codeMirrorPath + "/lib/codemirror.css", function () {
//            COMMON.AjaxModule.load( jsModules, function () {
//                this.textarea = new Element("textarea").inject(this.formTableArea);
//                var codeMirrorEditor = CodeMirror.fromTextArea(this.textarea, {
//                    theme: "default",
//                    gfm: true,
//                    breaks: true,
//                    lineWrapping : true,
//                    mode: 'gfm',
//                    dragDrop: false,
//                    lineNumbers:true
//                });
//                codeMirrorEditor.setSize("100%","50%");
//
//                this.previewer = new Element("div").inject(this.formTableArea);
//                marked.setOptions({
//                    gfm: true,
//                    tables: true,
//                    breaks: true,
//                    pedantic: false,
//                    sanitize: true,
//                    smartLists: true,
//                    smartypants: false
//                });
//                var str = "月份|收入|支出\n"+
//                    "----|----|---\n"+
//                    "8   |1000|500\n"+
//                    "9   |1200|600\n"+
//                    "10  |1400|650\n";
//                this.previewer.set("html",marked(str));
//            }.bind(this));
//        }.bind(this));
//    },
//    save: function(){
//
//        this.app.minder.execCommand('note', data.url, data.title || '');
//    },
//    remove: function( ev ){
//        this.app.minder.execCommand('note', null );
//    }
//});