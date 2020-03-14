o2.widget = o2.widget || {};

o2.xDesktop.requireApp("Template", "widget.ColorPicker", null, false);
o2.widget.Tablet = o2.Tablet = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "path": o2.session.path+"/widget/$Tablet/",

        "contentWidth" : 0, //绘图区域宽度，不制定则基础 this.node的宽度
        "contentHeight" : 0, //绘图区域高度，不制定则基础 this.node的高度 - 操作条高度

        "lineWidth" : 1, //铅笔粗细
        "color" : "#000000", //画笔颜色

        tools : [
            "save", "|",
            "undo",
            "redo", "|",
            "size",
            "color", "|",
            "image",
            "imageClipper", "|",
            "reset",
            //"clear" //橡皮
            "cancel"
        ],
        "description" : "", //描述文字


        "action" : null, //uploadImage方法的上传服务，可选，如果不设置，使用公共图片服务
        "method": "", //使用action 的方法
        "parameter": {}, //action 时的url参数

        "data": null, //formdata 的data
        "reference": "",  //uploadImage方法的使用 使用公共图片服务上传时的参数
        "referenceType" : "", //使用公共图片服务上传时的参数, 目前支持 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument
        "resultMaxSize" : 0 //使用 reference 时有效
    },
    initialize: function(node, options, app){
        this.node = node;
        this.app = app;

        this.reset();

        this.setOptions(options);

        this.path = this.options.path || (o2.session.path+"/widget/$Tablet/");
        this.cssPath = this.path + this.options.style+"/css.wcss";

        this.lp = {
            save : "保存",
            reset : "清空",
            undo : "撤销",
            redo : "重做",
            size : "粗细",
            color : "颜色",
            image : "插入图片",
            imageClipper : "裁剪图片",
            //clear : "橡皮"
            "cancel": "取消"
        };

        this._loadCss();
        this.fireEvent("init");
    },
    load: function(  ){
        //存储当前表面状态数组-上一步
        this.preDrawAry = [];
        //存储当前表面状态数组-下一步
        this.nextDrawAry = [];
        //中间数组
        this.middleAry = [];

        this.container = new Element("div.container", {
            styles :  this.css.container
        }).inject(this.node);

        this.loadToolBar();

        this.contentNode = new Element("div.contentNode", { styles :  this.css.contentNode}).inject(this.container);
        this.contentNode.addEvent("selectstart", function(e){
            e.preventDefault();
            e.stopPropagation();
        });

        this.loadDescription();

        this.setContentSize();

        if( this.checkBroswer() ){
            this.loadContent();
        }

        //this.imageNode = new Element("img",{
        //}).inject(this.contentNode);
        //this.imageNode.setStyles({
        //    "display" : "none"
        //});

        if( this.app ){
            this.resizeFun = this.setContentSize.bind(this);
            this.app.addEvent( "resize", this.resizeFun );
        }

    },
    loadDescription : function(){
        if( this.options.description ){
            this.descriptionNode = new Element("div",{
                "styles": this.css.descriptionNode,
                "text": this.options.description
            }).inject( this.container )
        }
    },
    setContentSize : function(){
        var nodeSize = this.node.getSize();

        this.contentWidth = this.options.contentWidth ||  nodeSize.x;
        if( this.contentWidth < 100 )this.contentWidth = 100;
        this.contentNode.setStyle("width", this.contentWidth );

        if( this.options.contentHeight ){
            this.contentHeight = this.options.contentHeight;
        }else{
            var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : { x : 0, y : 0 };
            var descriptionSize = this.descriptionNode ? this.descriptionNode.getSize() : { x : 0, y : 0 };

            var toolbarMargin = this.toolbarNode ? this.toolbarNode.getStyles("margin-top", "margin-bottom", "padding-top", "padding-bottom", "bordrt-top-width", "bordrt-bottom-width") : null;
            var m1 = (toolbarMargin) ? toolbarMargin["margin-top"].toInt()+toolbarMargin["margin-bottom"].toInt()+
                toolbarMargin["padding-bottom"].toInt()+toolbarMargin["padding-top"].toInt()+
                (toolbarMargin["bordrt-top-width"].toInt() || 0)+(toolbarMargin["bordrt-bottom-width"].toInt() || 0) : 0;

            var descriptionMargin = this.descriptionNode ? this.descriptionNode.getStyles("margin-top", "margin-bottom", "padding-top", "padding-bottom", "bordrt-top-width", "bordrt-bottom-width") : null;
            var m2 = (descriptionMargin) ? descriptionMargin["margin-top"].toInt()+descriptionMargin["margin-bottom"].toInt()+
                descriptionMargin["padding-bottom"].toInt()+descriptionMargin["padding-top"].toInt()+
                (descriptionMargin["bordrt-top-width"].toInt() || 0)+(descriptionMargin["bordrt-bottom-width"].toInt() || 0) : 0;

            var contentMargin = this.contentNode.getStyles("margin-top", "margin-bottom", "padding-top", "padding-bottom", "bordrt-top-width", "bordrt-bottom-width");
            var m3 = contentMargin["margin-top"].toInt()+contentMargin["margin-bottom"].toInt()+
                contentMargin["padding-bottom"].toInt()+contentMargin["padding-top"].toInt()+
                (contentMargin["bordrt-top-width"].toInt() || 0)+(contentMargin["bordrt-bottom-width"].toInt() || 0);

            this.contentHeight = nodeSize.y - toolbarSize.y - descriptionSize.y - m1 - m2 - m3;
        }
        if( this.contentHeight < 150 )this.contentHeight = 150;
        this.contentNode.setStyle("height", this.contentHeight );

        if( this.canvas ){
            var d = this.ctx.getImageData(0,0,this.canvas.clientWidth,this.canvas.clientHeight);
            this.canvas.set("width", this.contentWidth );
            this.canvas.set("height", this.contentHeight );
            this.ctx.putImageData(d,0,0);
        }
    },
    loadToolBar: function(){
        this.toolbarNode = new Element("div.toolbar", {
            "styles" : this.css.toolbar
        }).inject(this.container);

        this.toolbar = new o2.widget.Tablet.Toolbar( this , this.toolbarNode  );
        this.toolbar.load();
    },
    storeToPreArray : function(){
        //当前绘图表面状态
        var preData= this.ctx.getImageData(0,0,this.contentWidth,this.contentHeight);
        //当前绘图表面进栈
        this.preDrawAry.push(preData);
    },
    storeToMiddleArray : function(){
        //当前绘图表面状态
        var preData= this.ctx.getImageData(0,0,this.contentWidth,this.contentHeight);
        if( this.nextDrawAry.length==0){
            //当前绘图表面进栈
            this.middleAry.push(preData);
        }else{
            this.middleAry=[];
            this.middleAry=this.middleAry.concat(this.preDrawAry);
            this.middleAry.push(preData);
            this.nextDrawAry=[];
            this.toolbar.enableItem("redo");
        }

        if(this.preDrawAry.length){
            this.toolbar.enableItem("undo");
            this.toolbar.enableItem("reset");
        }
    },
    loadContent : function( ){

        this.canvas = new Element("canvas", {
            width : this.contentWidth,
            height : this.contentHeight
        }).inject( this.contentNode );
        this.ctx = this.canvas.getContext("2d");


        var preData=this.ctx.getImageData(0,0,this.contentWidth,this.contentHeight);
        //空绘图表面进栈
        this.middleAry.push(preData);

        this.canvas.ontouchstart = this.canvas.onmousedown = function(ev){
            var ev = ev || event;
            var ctx = this.ctx;
            var canvas = this.canvas;
            var container = this.contentNode;
            var position = this.contentNode.getPosition();
            var doc = $(document);
            //ctx.strokeStyle="#0000ff" 线条颜色; 默认 #000000
            if( this.options.color )ctx.strokeStyle= this.currentColor || this.options.color; // 线条颜色; 默认 #000000
            if( this.options.lineWidth  )ctx.lineWidth= this.currentWidth || this.options.lineWidth; //默认1 像素

            ctx.beginPath();
            ctx.moveTo(ev.clientX-position.x,ev.clientY-position.y);

            this.storeToPreArray();

            var mousemove = function(ev){
                ctx.lineTo(ev.client.x - position.x,ev.client.y - position.y);
                ctx.stroke();
            };
            doc.addEvent( "mousemove", mousemove );
            doc.addEvent( "touchmove", mousemove );

            var mouseup = function(ev){
                //document.onmousemove = document.onmouseup = null;
                doc.removeEvent("mousemove", mousemove);
                doc.removeEvent("mouseup", mouseup);
                doc.removeEvent("touchmove", mousemove);
                doc.removeEvent("touchend", mouseup);

                this.storeToMiddleArray();

                ctx.closePath();
            }.bind(this);
            doc.addEvent("mouseup", mouseup);
            doc.addEvent("touchend", mouseup);
            //document.onmouseup = function(ev){
            //    document.onmousemove = document.onmouseup = null;
            //    ctx.closePath();
            //}
        }.bind(this)
    },
    uploadImage: function(  success, failure  ){
        var image = this.getImage( null, true );
        if( image ){
            if( this.options.action ){
                this.action = (typeOf(this.options.action)=="string") ? o2.Actions.get(action).action : this.options.action;
                this.action.invoke({
                    "name": this.options.method,
                    "async": true,
                    "data": this.getFormData( image ),
                    "file": image,
                    "parameter": this.options.parameter,
                    "success": function(json){
                        success(json)
                    }.bind(this)
                });
            }else if( this.options.reference && this.options.referenceType ){
                //公共图片上传服务
                var maxSize = this.options.resultMaxSize;
                o2.xDesktop.uploadImageByScale(
                    this.options.reference,
                    this.options.referenceType,
                    maxSize,
                    this.getFormData( image ),
                    image,
                    success,
                    failure
                );
            }
        }else{
        }
    },
    getFormData : function( image ){
        if( !image )image = this.getImage();
        var formData = new FormData();
        formData.append('file', image, this.fileName );
        if( this.options.data ){
            Object.each(this.options.data, function(v, k){
                formData.append(k, v)
            });
        }
        return formData;
    },
    getImage : function( base64Code, ignoreResultSize ){
        var src = base64Code || this.getBase64Code( ignoreResultSize);
        src=window.atob(src);

        var ia = new Uint8Array(src.length);
        for (var i = 0; i < src.length; i++) {
            ia[i] = src.charCodeAt(i);
        }

        return new Blob([ia], {type: this.fileType });
    },
    getBase64Code : function( ignoreResultSize ){
        var ctx = this.ctx;
        var canvas = this.canvas;
        //var container = this.contentNode;
        //var size = this.options.size;
        if( !ignoreResultSize && this.options.resultMaxSize ){

            var width, height;
            width = Math.min( this.contentWidth , this.options.resultMaxSize);
            height = ( width / this.contentWidth) * this.contentHeight;

            var src=canvas.toDataURL( this.fileType );
            src=src.split(',')[1];
            src = 'data:'+ this.fileType +';base64,' + src;

            var tmpImageNode = new Element("img", {
                width : this.contentWidth,
                height : this.contentHeight,
                src : src
            });
            var tmpCanvas = new Element("canvas", {
                width : width,
                height : height
            }).inject( this.contentNode );
            var tmpCtx = tmpCanvas.getContext("2d");

            tmpCtx.drawImage(tmpImageNode,0,0, this.contentWidth,this.contentHeight,0,0,width,height);

            var tmpsrc= tmpCanvas.toDataURL( this.fileType );
            tmpsrc=tmpsrc.split(',')[1];

            tmpImageNode.destroy();
            tmpCanvas.destroy();
            tmpCtx = null;

            if(!tmpsrc){
                return "";
            }else{
                return tmpsrc
            }
        }else{
            var src=canvas.toDataURL( this.fileType );
            src=src.split(',')[1];

            if(!src){
                return "";
            }else{
                return src
            }
        }
    },
    getBase64Image: function( base64Code, ignoreResultSize ){
        if( !base64Code )base64Code = this.getBase64Code( ignoreResultSize );
        if( !base64Code )return null;
        return 'data:'+ this.fileType +';base64,' + base64Code;
    },
    close : function(){
        this.container.destroy();
        delete this;
    },
    checkBroswer : function(){
        if( window.Uint8Array && window.HTMLCanvasElement && window.atob && window.Blob){
            this.available = true;
            return true;
        }else{
            this.available = false;
            this.container.set("html", "<p>您的浏览器不支持以下特性:</p><ul><li>canvas</li><li>Blob</li><li>Uint8Array</li><li>FormData</li><li>atob</li></ul>");
            return false;
        }
    },
    save : function(){
        var base64code = this.getBase64Code();
        var imageFile = this.getImage( base64code );
        var base64Image = this.getBase64Image( base64code );
        this.fireEvent("save", [ base64code, base64Image, imageFile]);
    },
    reset : function( itemNode ){
        this.fileName = "untitled.png";
        this.fileType = "image/png";
        if( this.ctx ){
            var canvas = this.canvas;
            this.ctx.clearRect(0,0,canvas.clientWidth,canvas.clientHeight);
        }
    },
    undo : function( itemNode ){
        if(this.preDrawAry.length>0){
            var popData=this.preDrawAry.pop();
            var midData=this.middleAry[this.preDrawAry.length+1];
            this.nextDrawAry.push(midData);
            this.ctx.putImageData(popData,0,0);
        }

        this.toolbar.setAllItemsStatus();
    },
    redo : function( itemNode ){
        if(this.nextDrawAry.length){
            var popData=this.nextDrawAry.pop();
            var midData=this.middleAry[this.middleAry.length-this.nextDrawAry.length-2];
            this.preDrawAry.push(midData);
            this.ctx.putImageData(popData,0,0);
        }
        this.toolbar.setAllItemsStatus();
    },
    size : function( itemNode ){
        if( !this.sizeSelector ){
            this.sizeSelector = new o2.widget.Tablet.SizePicker(this.container, itemNode, null, {}, {
                "onSelect": function (width) {
                    this.currentWidth = width;
                }.bind(this)
            });
        }
    },
    color : function( itemNode ){
        if( !this.colorSelector ){
            this.colorSelector = new o2.xApplication.Template.widget.ColorPicker( this.container, itemNode, null, {}, {
                "lineWidth" : 1,
                "onSelect": function (color) {
                    this.currentColor = color;
                }.bind(this)
            });
        }
    },
    getImageSize : function(naturalWidth, naturalHeight ){
        var ratio = naturalWidth / naturalHeight;
        var ww = this.contentWidth,
            wh = this.contentHeight;
        var flag = ( naturalWidth / parseInt(ww) ) > ( naturalHeight / parseInt(wh) );
        if( flag ){
            var width = Math.min( naturalWidth, parseInt( ww )  );
            return { width : width,  height : width / ratio }
        }else{
            var height = Math.min( naturalHeight, parseInt( wh )  );
            return { width : height * ratio,  height : height }
        }
    },
    parseFileToImage : function( file, callback ){
        var imageNode = new Element("img");

        var onImageLoad = function(){
            var nh = imageNode.naturalHeight,
                nw = imageNode.naturalWidth;
            if( isNaN(nh) || isNaN(nw) || nh == 0 || nw == 0 ){
                setTimeout( function(){ onImageLoad(); }.bind(this), 100 );
            }else{
                _onImageLoad();
            }
        };

        var _onImageLoad = function(){

            var nh = imageNode.naturalHeight,
                nw = imageNode.naturalWidth;
            var size = this.getImageSize( nw, nh );
            imageNode.setStyles({
                width : size.width,
                height : size.height
            });

            var mover = new o2.widget.Tablet.ImageMover( this, imageNode, this.contentNode , {
                onPostOk : function(){
                    var coordinate =  mover.getCoordinage();
                    this.storeToPreArray();
                    this.ctx.drawImage(imageNode, coordinate.left, coordinate.top, coordinate.width, coordinate.height);
                    this.storeToMiddleArray();
                }.bind(this)
            });
            mover.load();


            if( callback )callback();
        }.bind(this);

        var reader=new FileReader();
        reader.onload=function(){
            imageNode.src=reader.result;
            reader = null;
            onImageLoad();
        }.bind(this);
        reader.readAsDataURL(file);
    },
    image : function( itemNode ){
        var uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" />"; //accept=\"images/*\"

        uploadFileAreaNode.set("html", html);

        var fileUploadNode = uploadFileAreaNode.getFirst();
        fileUploadNode.addEvent("change", function () {
            var file =  fileUploadNode.files[0];
            this.parseFileToImage( file, function(){
                uploadFileAreaNode.destroy();
            })
        }.bind(this));
        fileUploadNode.click();
    },
    imageClipper : function( itemNode ){
        var clipper = new o2.widget.Tablet.ImageClipper(this.app, {
            "style": "default",
            "aspectRatio" : 0,
            "onOk" : function( img ){
                this.parseFileToImage( img );
            }.bind(this)
        });
        clipper.load();
    },
    clear : function( itemNode ){

    },
    cancel: function(){
        this.reset();
        this.fireEvent("cancel");
    }
});

o2.widget.Tablet.Toolbar = new Class({
    Implements: [Options, Events],
    initialize: function (tablet, container) {
        this.tablet = tablet;
        this.container = container;
        this.css = tablet.css;
        this.lp = this.tablet.lp;
        this.imagePath = o2.session.path+"/widget/$Tablet/"+ this.tablet.options.style +"/icon/";

        this.items = {};

        this.itemsEnableFun = {
            save : {
                enable : function(){ return true }
            },
            reset : {
                enable : function(){ return this.tablet.preDrawAry.length > 0}.bind(this)
            },
            undo : {
                enable : function(){ return this.tablet.preDrawAry.length > 0 }.bind(this)
            },
            redo : {
                enable : function(){ return this.tablet.nextDrawAry.length > 0 }.bind(this)
            },
            size : {
                enable : function(){ return true }
            },
            color : {
                enable : function(){ return true }
            },
            image : {
                enable : function(){ return true }
            },
            imageClipper : {
                enable : function(){ return true }
            }
        }
    },
    getHtml : function(){
        var items;
        var tools = this.tablet.options.tools;
        if( tools ){
            items = tools;
        }else{
            items = [
                "save", "|",
                "reset", "|",
                "undo", "|",
                "redo", "|",
                "size", "|",
                "color", "|",
                "image", "|",
                "imageClipper"
                //"clear" //橡皮
            ];
        }

        var html = "";
        var style = "toolItem";
        items.each( function( item ){
            switch( item ){
                case "|":
                    html +=  "<div styles='" + "separator" + "'></div>";
                    break;
                case "save" :
                    html +=  "<div item='save' styles='" + style + "'>"+ this.lp.save +"</div>";
                    break;
                case "reset" :
                    html +=  "<div item='reset' styles='" + style + "'>"+ this.lp.reset  +"</div>";
                    break;
                case "undo" :
                    html +=  "<div item='undo' styles='" + style + "'>"+ this.lp.undo  +"</div>";
                    break;
                case "redo" :
                    html +=  "<div item='redo' styles='" + style + "'>"+ this.lp.redo  +"</div>";
                    break;
                case "size" :
                    html +=  "<div item='size' styles='" + style + "'>"+ this.lp.size  +"</div>";
                    break;
                case "color" :
                    html +=  "<div item='color' styles='" + style + "'>"+ this.lp.color  +"</div>";
                    break;
                case "image" :
                    html +=  "<div item='image' styles='" + style + "'>"+ this.lp.image  +"</div>";
                    break;
                case "imageClipper" :
                    html +=  "<div item='imageClipper' styles='" + style + "'>"+ this.lp.imageClipper  +"</div>";
                    break;
                case "clear" :
                    html +=  "<div item='clear' styles='" + style + "'>"+ this.lp.clear  +"</div>";
                    break;
                case "cancel" :
                    html +=  "<div item='cancel' styles='toolRightItem'>"+ this.lp.cancel  +"</div>";
                    break;

            }
        }.bind(this));
        return html;
    },
    load: function () {
        var _self = this;
        var imagePath = this.imagePath;
        this.items = {};
        var html = this.getHtml();

        this.container.set("html", html);
        this.container.getElements("[styles]").each(function (el) {
            el.setStyles(_self.css[el.get("styles")]);
            var item =  el.get("item");
            if ( item ) {
                this.items[ item ] = el;
                el.setStyle("background-image","url("+ imagePath + item +"_normal.png)");
                el.addEvents({
                    mouseover : function(){
                        _self._setItemNodeActive(this.el);
                    }.bind({ item : item, el : el }),
                    mouseout : function(){
                        _self._setItemNodeNormal(this.el);
                    }.bind({ item : item, el : el }),
                    click : function( ev ){
                        if( _self["tablet"][this.item] )_self["tablet"][this.item]( this.el );
                    }.bind({ item : item, el : el })
                });
                if( item == "color" || item == "size" ){
                    if( _self["tablet"][item] )_self["tablet"][item]( el );
                }
            }
        }.bind(this));
        this.setAllItemsStatus();
    },
    setAllItemsStatus : function(){
        for( var item in this.items ){
            var node = this.items[item];
            if( this.itemsEnableFun[item] ){
                if( this.itemsEnableFun[item].enable() ){
                    this.enableItem( item )
                }else{
                    this.disableItem( item );
                }
            }
        }
    },
    disableItem : function( itemName ){
        var itemNode =  this.items[ itemName ]; //this.container.getElement("[item='+itemName+']");
        itemNode.store("status", "disable");
        this._setItemNodeDisable( itemNode );
    },
    enableItem : function( itemName ){
        var itemNode =  this.items[ itemName ];
        itemNode.store("status", "enable");
        this._setItemNodeNormal( itemNode );
    },
    _setItemNodeDisable : function( itemNode ){
        var item = itemNode.get("item");
        itemNode.setStyles( this.css.toolItem_disable );
        itemNode.setStyle("background-image","url("+  this.imagePath+ item +"_disable.png)");
    },
    _setItemNodeActive: function( itemNode ){
        if( itemNode.retrieve("status") == "disable" )return;
        var item = itemNode.get("item");
        itemNode.setStyles( this.css.toolItem_over );
        itemNode.setStyle("background-image","url("+  this.imagePath+ item +"_active.png)");
    },
    _setItemNodeNormal: function( itemNode ){
        if( itemNode.retrieve("status") == "disable" )return;
        var item = itemNode.get("item");
        var style = itemNode.get("styles");
        itemNode.setStyles( this.css[style] );
        itemNode.setStyle("background-image","url("+  this.imagePath+ item +"_normal.png)");
    }

});

o2.xDesktop.requireApp("Template", "MTooltips", null, false);
o2.widget.Tablet.SizePicker = new Class({
    Implements: [Options, Events],
    Extends: MTooltips,
    options: {
        style : "default",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x 轴上left center right, auto 系统自动计算
            y : "auto" //y轴上top middle bottom,  auto 系统自动计算
        },
        //event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        nodeStyles : {
            "min-width" : "260px"
        },
        lineWidth : 1
    },
    initialize : function( container, target, app, data, options, targetCoordinates ){
        //可以传入target 或者 targetCoordinates，两种选一
        //传入target,表示触发tooltip的节点，本类根据 this.options.event 自动绑定target的事件
        //传入targetCoordinates，表示 出发tooltip的位置，本类不绑定触发事件
        if( options ){
            this.setOptions(options);
        }
        this.container = container;
        this.target = target;
        this.targetCoordinates = targetCoordinates;
        this.app = app;
        if(app)this.lp = app.lp;
        this.data = data;

        if( this.target ){
            this.setTargetEvents();
        }
    },
    _customNode : function( node ){
        o2.UD.getDataJson("sizePicker", function(json) {

            this.rulerContainer = new Element("div",{
                styles : {
                    "margin-left": " 23px",
                    "margin-right": " 1px",
                    "width" : "228px"
                }
            }).inject(this.node);

            this.ruleList = ["0.1","0.5","1","5","10", "15","20"];
            this.rulerTitleContainer = new Element("div",{
                styles : { "overflow" : "hidden" }
            }).inject( this.rulerContainer );
            this.ruleList.each( function( rule ){
                new Element("div", {
                    text : rule,
                    styles : {
                        width : "32px",
                        float : "left",
                        "text-align" : "center"
                    }
                }).inject( this.rulerTitleContainer )
            }.bind(this));

            this.rulerContentContainer = new Element("div",{
                styles : { "overflow" : "hidden" }
            }).inject( this.rulerContainer );
            new Element("div", {
                styles : {
                    width : "14px",
                    height : "10px",
                    "text-align" : "center",
                    float : "left",
                    "border-right" : "1px solid #aaa"
                }
            }).inject( this.rulerContentContainer );
            this.ruleList.each( function( rule, i ){
                if( i == this.ruleList.length - 1 )return;
                new Element("div", {
                    styles : {
                        width : "32px",
                        height : "10px",
                        "text-align" : "center",
                        float : "left",
                        "border-right" : "1px solid #aaa"
                    }
                }).inject( this.rulerContentContainer )
            }.bind(this));


            this.silderContainer = new Element("div", {
                "height" : "25px",
                "line-height" : "25px",
                "margin-top" : "4px"
            }).inject( this.node );

            this.sliderArea = new Element("div", {styles : {
                "margin-top": "2px",
                "margin-bottom": "10px",
                "height": "10px",
                "overflow": " hidden",
                "margin-left": " 37px",
                "margin-right": " 15px",
                "border-top": "1px solid #999",
                "border-left": "1px solid #999",
                "border-bottom": "1px solid #E1E1E1",
                "border-right": "1px solid #E1E1E1",
                "background-color": "#EEE",
                "width" : "200px"
            }}).inject( this.silderContainer );
            this.sliderKnob = new Element("div", {styles : {
                "height": "8px",
                "width": " 8px",
                "background-color": "#999",
                "z-index": " 99",
                "border-top": "1px solid #DDD",
                "border-left": "1px solid #DDD",
                "border-bottom": "1px solid #777",
                "border-right": "1px solid #777",
                "cursor": "pointer"
            } }).inject( this.sliderArea );

            this.slider = new Slider(this.sliderArea, this.sliderKnob, {
                range: [1, 30],
                initialStep: 10,
                onChange: function(value){
                    if( value < 10 ){
                        this.lineWidth = (value / 10)
                    }else{
                        this.lineWidth = value - 9;
                    }
                    this.drawPreview( this.lineWidth );
                    this.fireEvent("select", this.lineWidth )
                }.bind(this)
            });

            var previewContainer = new Element("div").inject(this.node);
             new Element("div",{ text : "预览", styles : {
                "float" : "left",
                 "margin-top" : "5px",
                "width" : "30px"
            }}).inject(this.silderContainer);
            this.previewNode = new Element("div", {
                styles : {
                    "margin" : "0px 0px 0px 37px",
                    "width" : "200px"
                }
            }).inject( this.node );
            this.canvas = new Element("canvas", {
                width : 200,
                height : 30
            }).inject( this.previewNode );
            this.ctx = this.canvas.getContext("2d");
            this.drawPreview();

            new Element("button", {
                text : "重置",
                type : "button",
                styles :{
                    "margin-left" : "40px",
                    "font-size" : "12px",
                    "border-radius" : "3px",
                    "cursor" : "pointer" ,
                    "border" : "1px solid #ccc",
                    "padding" : "5px 10px",
                    "background-color" : "#f7f7f7"
                },
                events : {
                    click : function(){
                        this.lineWidth = this.options.lineWidth || 1;
                        var step;
                        if( this.lineWidth < 1 ){
                            step = this.lineWidth * 10
                        }else{
                            step = this.lineWidth + 9
                        }
                        this.slider.set( parseInt( step ) );
                        this.drawPreview( this.lineWidth );
                        this.fireEvent("select", this.lineWidth )
                    }.bind(this)
                }
            }).inject( this.node );
        }.bind(this));


        //this.resultInput = new Element("input").inject(this.contentNode);
    },
    drawPreview : function( lineWidth ){
        if( !lineWidth )lineWidth = this.options.lineWidth || 1;
        var canvas = this.canvas;
        var ctx = this.ctx;
        ctx.clearRect(0,0,canvas.clientWidth,canvas.clientHeight);

        var coordinates = this.previewNode.getCoordinates();
        var doc = $(document);
        ctx.strokeStyle="#000000"; //线条颜色; 默认 #000000
        //ctx.strokeStyle= this.currentColor || this.options.color; // 线条颜色; 默认 #000000
        ctx.lineWidth=  lineWidth ; //默认1 像素

        ctx.beginPath();
        //ctx.moveTo( (coordinates.bottom-coordinates.top - lineWidth ) / 2, coordinates.left);

        ctx.moveTo( 1 , 15 );

       ctx.lineTo( 200, 15  );
        ctx.stroke();
    }
});

MWF.require("MWF.widget.ImageClipper", null, false);
o2.widget.Tablet.ImageClipper = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "imageUrl" : "",
        "resultMaxSize" : 800,
        "description" : "",
        "title": "裁剪图片",
        "style": "default",
        "aspectRatio": 0
    },
    initialize: function(app, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_process_Xform/widget/$ImageClipper/";
        this.cssPath = "/x_component_process_Xform/widget/$ImageClipper/"+this.options.style+"/css.wcss";
        this._loadCss();
    },

    load: function(data){
        this.data = data;

        var options = {};
        var width = "668";
        var height = "510";
        width = width.toInt();
        height = height.toInt();

        var size = (( this.app && this.app.content )  || $(document.body) ).getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;
        if (layout.mobile){
            x = 20;
            y = 0;
        }

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function() {
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.options.title || "Select Image",
                "style": options.style || "user",
                "top": y,
                "left": x - 20,
                "fromTop": y,
                "fromLeft": x - 20,
                "width": width,
                "height": height,
                "html": "<div></div>",
                "maskNode": this.app ? this.app.content : $(document.body),
                "container": this.app ? this.app.content : $(document.body),
                "buttonList": [
                    {
                        "text": MWF.LP.process.button.ok,
                        "action": function () {
                            var img = _self.image.getResizedImage();
                            _self.fireEvent("ok", [img] );
                            this.close();
                        }
                    },
                    {
                        "text": MWF.LP.process.button.cancel,
                        "action": function () {
                            this.close();
                        }
                    }
                ],
                "onPostShow" : function(){
                    this.node.setStyle("z-index",1003);
                    this.content.setStyle("margin-left","20px");
                }
            });
            dlg.show();

            this.image = new MWF.widget.ImageClipper(dlg.content.getFirst(), {
                "description" : this.options.description,
                "resetEnable" : true
            });
            this.image.load(this.data);
        }.bind(this))
    }

});

o2.widget.Tablet.ImageMover = new Class({
    Implements: [Options, Events],
    options: {
        imageMinSize : 100
    },
    initialize: function(tablet, imageNode, relativeNode, options){
        this.setOptions(options);
        this.tablet = tablet;
        this.imageNode = imageNode;
        this.relativeNode = relativeNode;
        this.path = this.tablet.path + this.tablet.options.style + "/"
    },
    load: function(){
        this.maskNode = new Element("div.maskNode",{
            styles : {
                "width": "100%",
                "height": "100%",
                "opacity": 0.6,
                "position": "absolute",
                "background-color": "#CCC",
                "top": "0px",
                "left": "0px",
                "z-index" : 1002,
                "-webkit-user-select": "none",
                "-moz-user-select": "none",
                "user-select" : "none"
            }
        }).inject($(document.body));

        var coordinates = this.relativeNode.getCoordinates();

        this.node = new Element( "div", {
            styles : {
                "width" : coordinates.width,
                "height" : coordinates.height,
                "position" : "absolute",
                "top" : coordinates.top,
                "left" : coordinates.left,
                "background" : "rgba(255,255,255,0.5)",
                "z-index" : 1003,
                "-webkit-user-select": "none",
                "-moz-user-select": "none",
                "user-select" : "none"
            }
        }).inject($(document.body));

        this.dragNode = new Element("div",{
            styles : {
                "cursor" : "move"
            }
        }).inject( this.node );

        this.imageNode.inject( this.dragNode );

        //this.maskNode.ondragstart = function(){
        //    return false;
        //};
        //this.node.ondragstart = function(){
        //    return false;
        //};
        //this.imageNode.ondragstart = function(){
        //    return false;
        //};

        this.originalImageSize = this.imageNode.getSize();
        this.dragNode.setStyles({
            width : this.originalImageSize.x,
            height : this.originalImageSize.y
        });

        this.okNode = new Element("div",{
            styles : {
                "background" : "url("+ this.path + "icon/ok.png) no-repeat",
                "width" : "16px",
                "height" : "16px",
                "right" : "-20px",
                "top" : "5px",
                "position" : "absolute",
                "cursor" : "pointer"
            },
            events : {
                click : function(){
                    this.ok();
                    this.close();
                }.bind(this)
            }
        }).inject(this.dragNode);

        this.cancelNode = new Element("div",{
            styles : {
                "background" : "url("+ this.path + "icon/cancel.png) no-repeat",
                "width" : "16px",
                "height" : "16px",
                "right" : "-20px",
                "top" : "30px",
                "position" : "absolute",
                "cursor" : "pointer"
            },
            events : {
                click : function(){
                    this.close();
                }.bind(this)
            }
        }).inject(this.dragNode);

        this.drag = this.dragNode.makeDraggable({
            "container" : this.node,
            "handle": this.dragNode
        });


        this.reizeNode = new Element("div.reizeNode",{ styles :  {
            "cursor" : "nw-resize",
            "position": "absolute",
            "bottom": "0px",
            "right": "0px",
            "border" : "2px solid #52a3f5",
            "width" : "8px",
            "height" : "8px"
        }}).inject(this.dragNode);

        this.docBody = window.document.body;
        this.reizeNode.addEvents({
            "touchstart" : function(ev){
                this.drag.detach();
                this.dragNode.setStyle("cursor", "nw-resize" );
                this.docBody.setStyle("cursor", "nw-resize" );
                this.resizeMode = true;
                this.getOffset(ev);
                ev.stopPropagation();
            }.bind(this),
            "mousedown" : function(ev){
                this.drag.detach();
                this.dragNode.setStyle("cursor", "nw-resize" );
                this.docBody.setStyle("cursor", "nw-resize" );
                this.resizeMode = true;
                this.getOffset(ev);
                ev.stopPropagation();
            }.bind(this),
            "touchmove" : function(ev){
                if(!this.lastPoint)return;
                var offset= this.getOffset(ev);
                this.resizeDragNode( offset );
                ev.stopPropagation();
            }.bind(this),
            "mousemove" : function(ev){
                if(!this.lastPoint)return;
                var offset= this.getOffset(ev);
                this.resizeDragNode( offset );
                ev.stopPropagation();
            }.bind(this),
            "touchend" : function(ev){
                this.drag.attach();
                this.dragNode.setStyle("cursor", "move" );
                this.docBody.setStyle("cursor", "default" );
                this.resizeMode = false;
                this.lastPoint=null;
                ev.stopPropagation();
            }.bind(this),
            "mouseup" : function(ev){
                this.drag.attach();
                this.dragNode.setStyle("cursor", "move" );
                this.docBody.setStyle("cursor", "default" );
                this.resizeMode = false;
                this.lastPoint=null;
                ev.stopPropagation();
            }.bind(this)
        });

        this.bodyMouseMoveFun = this.bodyMouseMove.bind(this);
        this.docBody.addEvent("touchmove", this.bodyMouseMoveFun);
        this.docBody.addEvent("mousemove", this.bodyMouseMoveFun);

        this.bodyMouseEndFun = this.bodyMouseEnd.bind(this);
        this.docBody.addEvent("touchend", this.bodyMouseEndFun);
        this.docBody.addEvent("mouseup", this.bodyMouseEndFun);
    },
    bodyMouseMove: function(ev){
        if(!this.lastPoint)return;
        if( this.resizeMode ){
            var offset= this.getOffset(ev);
            this.resizeDragNode( offset );
        }
    },
    bodyMouseEnd: function(ev){
        this.lastPoint=null;
        if( this.resizeMode ){
            this.drag.attach();
            this.dragNode.setStyle("cursor", "move" );
            this.docBody.setStyle("cursor", "default" );
            this.resizeMode = false;
        }
    },
    resizeDragNode : function(offset){
        var x=offset.x;
        if( x == 0 )return;

        var	y=offset.y;
        if( y == 0 )return;

        debugger;

        var coordinates = this.dragNode.getCoordinates( this.node );
        var containerSize = this.node.getSize();
        var	top=coordinates.top,
            left=coordinates.left,
            width=containerSize.x,
            height=containerSize.y,
            ratio = this.originalImageSize.x / this.originalImageSize.y,
            w,
            h;

        //if( ratio ){
            if( Math.abs(x)/Math.abs(y) > ratio ){
                if( x+coordinates.width+left>width ){
                    return;
                }else{
                    w = x + coordinates.width;
                    h = w / ratio;
                    if( h+top > height ){
                        return;
                    }
                }
            }else{
                if(y+coordinates.height+top>height){
                    return;
                }else{
                    h = y+ coordinates.height;
                    w = h * ratio;
                }
                if( w+left > width ){
                    return;
                }
            }
        //}else{
        //    if( x+coordinates.width+left>width ){
        //        return;
        //    }else{
        //        w = x + coordinates.width
        //    }
        //    if(y+coordinates.height+top>height){
        //        return;
        //    }else{
        //        h = y+ coordinates.height;
        //    }
        //}

        var minWidth = this.options.imageMinSize;
        var minHeight = this.options.imageMinSize;
        w=w< minWidth ? minWidth:w;
        h=h< minHeight ? minHeight:h;

        this.dragNode.setStyles({
            width:w+'px',
            height:h+'px'
        });
        this.imageNode.setStyles({
            width:w+'px',
            height:h+'px'
        });
    },
    getOffset: function(event){
        event=event.event;
        var x,y;
        if(event.touches){
            var touch=event.touches[0];
            x=touch.clientX;
            y=touch.clientY;
        }else{
            x=event.clientX;
            y=event.clientY;
        }

        if(!this.lastPoint){
            this.lastPoint={
                x:x,
                y:y
            };
        }

        var offset={
            x:x-this.lastPoint.x,
            y:y-this.lastPoint.y
        };
        this.lastPoint={
            x:x,
            y:y
        };
        return offset;
    },
    getCoordinage : function(){
        return this.imageNode.getCoordinates( this.node );
    },
    ok : function(){
        this.fireEvent("postOk")
    },
    close : function(){
        this.docBody.removeEvent("touchmove",this.bodyMouseMoveFun);
        this.docBody.removeEvent("mousemove",this.bodyMouseMoveFun);
        this.docBody.removeEvent("touchend",this.bodyMouseEndFun);
        this.docBody.removeEvent("mouseup",this.bodyMouseEndFun);

        //this.backgroundNode.destroy();
        this.maskNode.destroy();
        this.node.destroy();

        delete this;
    }
});