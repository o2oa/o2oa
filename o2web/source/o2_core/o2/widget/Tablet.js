o2.widget = o2.widget || {};
o2.xDesktop.requireApp("Template", "MSelector", null, false);
o2.xDesktop.requireApp("Template", "widget.ColorPicker", null, false);
o2.widget.Tablet = o2.Tablet = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "path": o2.session.path+"/widget/$Tablet/",

        "iconfontEnable": false,
        "mainColorEnable": false,

        "contentWidth" : 0, //绘图区域宽度，不制定则基础 this.node的宽度
        "contentHeight" : 0, //绘图区域高度，不制定则基础 this.node的高度 - 操作条高度

        "lineWidth" : 1, //铅笔粗细
        "eraserRadiusSize": 20, //橡皮大小
        "color" : "#000000", //画笔颜色

        "zIndex": 20003,

        tools : [
            "save", "|",
            "undo",
            "redo", "|",
            "eraser", //橡皮
            "input", //输入法
            "pen", "|", //笔画
            "eraserRadius",
            "size",
            "color",
            "fontSize", "|",
            // "fontFamily",
            "image",
            "imageClipper", "|",
            "reset",
            "cancel"
        ],

        "toolHidden": [],
        "description" : "", //描述文字
        "imageSrc": "",

        "eraserEnable": true,
        "inputEnable": false,


        "action" : null, //uploadImage方法的上传服务，可选，如果不设置，使用公共图片服务
        "method": "", //使用action 的方法
        "parameter": {}, //action 时的url参数

        "data": null, //formdata 的data
        "reference": "",  //uploadImage方法的使用 使用公共图片服务上传时的参数
        "referenceType" : "", //使用公共图片服务上传时的参数, 目前支持 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument
        "resultMaxSize" : 0, //使用 reference 时有效

        "rotateWithMobile": true,
        "toolsScale": 1
    },
    initialize: function(node, options, app){
        this.node = node;
        this.app = app;

        this.fileName = "untitled.png";
        this.fileType = "image/png";

        this.reset();

        this.setOptions(options);

        if( !this.options.toolHidden )this.options.toolHidden = [];

        if( !this.options.eraserEnable ){
            this.options.toolHidden.push("eraser");
            this.options.toolHidden.push("eraserRadius");
        }

        if( !this.options.inputEnable ){
            this.options.toolHidden.push("input");
            this.options.toolHidden.push("fontSize");
            this.options.toolHidden.push("fontFamily");
        }

        this.path = this.options.path || (o2.session.path+"/widget/$Tablet/");
        this.cssPath = this.path + this.options.style+"/css.wcss";

        this.inMobileDevice = COMMON && COMMON.Browser && COMMON.Browser.Platform.isMobile;

        this.lp = {
            "save" : o2.LP.widget.save,
            "reset" : o2.LP.widget.empty,
            "undo" : o2.LP.widget.undo,
            "redo" : o2.LP.widget.redo,
            "eraser": o2.LP.widget.eraser,
            "input": o2.LP.widget.input,
            "fontSize": o2.LP.widget.fontSize,
            "fontFamily": o2.LP.widget.fontFamily,
            "pen": o2.LP.widget.pen,
            "eraserRadius": o2.LP.widget.eraserRadius,
            "size" : o2.LP.widget.thickness,
            "color" : o2.LP.widget.color,
            "image" : o2.LP.widget.insertImage,
            "imageClipper" : o2.LP.widget.imageClipper,
            "cancel": o2.LP.widget.cancel
        };

        this._loadCss();

        if( this.options.iconfontEnable ){
            this.node.loadCss(this.path + this.options.style+"/style.css");
        }

        this.fireEvent("init");
    },
    load: function(  ){
        if( layout.mobile && this.options.rotateWithMobile ){
            this.rotate = true;
        }
        //存储当前表面状态数组-上一步
        this.preDrawAry = [];
        //存储当前表面状态数组-下一步
        this.nextDrawAry = [];
        //中间数组
        this.middleAry = [];

        this.mode = "writing"; //writing表示写状态，erasing表示擦除状态, inputing表示输入法

        this.currentColor = this.options.color;
        this.currentFontFamily = "宋体,SimSun";
        this.currentFontSize = "16px";

        this.container = new Element("div.container", {
            styles :  this.css.container
        }).inject(this.node);

        if( this.rotate ){ //强制横屏显示
            this.detectOrient();
        }

        this.loadToolBar();

        this.contentNode = new Element("div.contentNode", { styles :  this.css.contentNode}).inject(this.container);
        this.contentNode.addEvent("selectstart", function(e){
            if( this.mode !== "inputing" ){
                e.preventDefault();
                e.stopPropagation();
            }
        }.bind(this));

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
        this.computeContentSize();
        this.contentNode.setStyle("width", this.contentWidth );
        this.contentNode.setStyle("height", this.contentHeight );

        if(this.canvasWrap){
            this.canvasWrap.setStyles({
                width : this.contentWidth+"px",
                height : this.contentHeight+"px"
            });
        }

        if( this.canvas ){
            var d = this.ctx.getImageData(0,0,this.canvas.clientWidth,this.canvas.clientHeight);
            this.canvas.set("width", this.contentWidth );
            this.canvas.set("height", this.contentHeight );
            this.ctx.putImageData(d,0,0);
        }
    },
    computeContentSize: function(){
        var toolbarSize,descriptionSize, m1,m2,m3;
        var nodeSize = this.node.getSize();
        if( this.rotate && this.transform > 0 ){
            this.contentWidth = this.options.contentHeight ||  nodeSize.y;
            if( this.contentWidth < 150 )this.contentWidth = 150;

            if( this.options.contentWidth ){
                this.contentHeight = this.options.contentWidth;
            }else{
                toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : { x : 0, y : 0 };
                descriptionSize = this.descriptionNode ? this.descriptionNode.getSize() : { x : 0, y : 0 };
                m1 = this.getOffsetX(this.toolbarNode);
                m2 = this.getOffsetX(this.descriptionNode);
                m3 = this.getOffsetX(this.contentNode);
                this.contentOffSetX = toolbarSize.x + descriptionSize.x + m1 + m2 + m3;
                this.contentHeight = nodeSize.x - toolbarSize.x - descriptionSize.x - m1 - m2 - m3;
            }
            if( this.contentHeight < 100 )this.contentHeight = 100;
        }else{
            this.contentWidth = this.options.contentWidth ||  nodeSize.x;
            if( this.contentWidth < 100 )this.contentWidth = 100;

            if( this.options.contentHeight ){
                this.contentHeight = this.options.contentHeight;
            }else{
                toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : { x : 0, y : 0 };
                descriptionSize = this.descriptionNode ? this.descriptionNode.getSize() : { x : 0, y : 0 };
                m1 = this.getOffsetY(this.toolbarNode);
                m2 = this.getOffsetY(this.descriptionNode);
                m3 = this.getOffsetY(this.contentNode);
                this.contentHeight = nodeSize.y - toolbarSize.y - descriptionSize.y - m1 - m2 - m3;
            }
            if( this.contentHeight < 150 )this.contentHeight = 150;
        }
    },
    getOffsetY : function(node){
        if( !node )return 0;
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    getOffsetX : function(node){
        if( !node )return 0;
        return (node.getStyle("margin-left").toInt() || 0 ) +
            (node.getStyle("margin-right").toInt() || 0 ) +
            (node.getStyle("padding-left").toInt() || 0 ) +
            (node.getStyle("padding-right").toInt() || 0 )+
            (node.getStyle("border-left-width").toInt() || 0 ) +
            (node.getStyle("border-right-width").toInt() || 0 );
    },
    loadToolBar: function(){
        if( layout.mobile ){ //this.rotate && this.transform > 0
            this.toolbar = new o2.widget.Tablet.ToolbarMobile( this );
            this.toolbar.load();
        }else{
            this.toolbarNode = new Element("div.toolbar", {
                "styles" : this.css.toolbar
            }).inject(this.container);

            this.toolbar = new o2.widget.Tablet.Toolbar( this , this.toolbarNode  );
            this.toolbar.load();
        }
    },
    storeToPreArray : function(preData){
        //当前绘图表面状态
        if(!preData)preData= this.ctx.getImageData(0,0,this.contentWidth,this.contentHeight);
        //当前绘图表面进栈
        this.preDrawAry.push(preData);
    },
    storeToMiddleArray : function( preData ){
        //当前绘图表面状态
        if( !preData )preData= this.ctx.getImageData(0,0,this.contentWidth,this.contentHeight);
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
        debugger;
        var _self = this;

        this.canvasWrap = new Element("div.canvasWrap", { styles :  this.css.canvasWrap}).inject(this.contentNode);
        this.canvasWrap.setStyles({
            width : this.contentWidth+"px",
            height : this.contentHeight+"px"
        });
        if( !this.rotate ){
            this.canvasWrap.setStyle("position", "relative");
        }

        this.canvas = new Element("canvas", {
            width : this.contentWidth,
            height : this.contentHeight
        }).inject( this.canvasWrap );

        this.ctx = this.canvas.getContext("2d");

        if( this.options.imageSrc ){
            var img = new Element("img", {
                "crossOrigin": "",
                "src": this.options.imageSrc,
                "events": {
                    "load": function () {
                        _self.ctx.drawImage(this, 0, 0);
                        var preData=_self.ctx.getImageData(0,0,_self.contentWidth,_self.contentHeight);
                        _self.middleAry.push(preData);
                        _self.toolbar.enableItem("reset");
                    }
                }
            })

        }else{
            var preData=this.ctx.getImageData(0,0,this.contentWidth,this.contentHeight);
            this.middleAry.push(preData);
        }

        this.canvas.ontouchstart = this.canvas.onmousedown = function(ev){
            var flag;
            if( this.currentInput ){
                this.currentInput.readMode();
                this.currentInput = null;
                flag = true;
            }
            if( this.mode === "inputing" ){
                if(flag)return;
                this.doInput(ev)
            }else{
                this.doWritOrErase(ev)
            }
        }.bind(this)
    },
    doInput: function(event){
        var _self = this;
        if( !this.inputList )this.inputList = [];
        var x,y;
        if(event.touches){
            var touch=event.touches[0];
            x=touch.clientX;
            y=touch.clientY;
        }else{
            x=event.clientX;
            y=event.clientY;
        }

        var coordinate =  this.canvasWrap.getCoordinates();
        x = x - coordinate.left;
        y = y- coordinate.top;

        this.currentInput = new o2.widget.Tablet.Input( this, this.canvasWrap , {
            top: y,
            left: x,
            onPostDraw : function( image ){
                Promise.resolve(image).then(function () {
                    var input = this;
                    var globalCompositeOperation = _self.ctx.globalCompositeOperation;
                    _self.ctx.globalCompositeOperation = "source-over";

                    var coordinate =  input.getCoordinates();
                    _self.storeToPreArray();
                    _self.ctx.drawImage(image, coordinate.left, coordinate.top, coordinate.width, coordinate.height);
                    _self.storeToMiddleArray();

                    if(globalCompositeOperation)_self.ctx.globalCompositeOperation = globalCompositeOperation;
                }.bind(this));
            },
            onPostCancel: function(){
                // if(this.globalCompositeOperation)this.ctx.globalCompositeOperation = this.globalCompositeOperation;
                // this.globalCompositeOperation = null;
            }.bind(this),
        });
        this.currentInput.load();
        this.inputList.push( this.currentInput );
    },
    loadImage: function(url){
        return new Promise(function(resolve, reject){
            var img = new Element("img");
            img.crossOrigin="anonymous";
            img.addEvent('load', function(){ resolve(img)});
            img.addEvent('error', function(err){ reject(err) });
            img.src = url;
        })
    },
    doWritOrErase: function(ev){
        var _self = this;
        ev = ev || event;
        var ctx = this.ctx;
        var canvas = this.canvas;
        var container = this.contentNode;
        var position = this.canvasWrap.getPosition();
        var doc = $(document);

        if( this.mode === "erasing" ) {
            if(this.inputList)this.inputList.each(function (input) {
                input.hide();
            });
            ctx.lineCap = "round";　　//设置线条两端为圆弧
            ctx.lineJoin = "round";　　//设置线条转折为圆弧
            ctx.lineWidth = this.currentEraserRadius || this.options.eraserRadiusSize;
            ctx.globalCompositeOperation = "destination-out";
        }else{
            //ctx.strokeStyle="#0000ff" 线条颜色; 默认 #000000
            if( this.options.color )ctx.strokeStyle= this.currentColor || this.options.color; // 线条颜色; 默认 #000000
            if( this.options.lineWidth  )ctx.lineWidth= this.currentWidth || this.options.lineWidth; //默认1 像素
            ctx.lineCap = "butt";　　//设置线条两端为平直的边缘
            ctx.lineJoin = "miter";　　//设置线条转折为圆弧
            ctx.globalCompositeOperation = "source-over";
        }

        if( this.mode === "erasing" ){
            var radius = this.currentEraserRadius || this.options.eraserRadiusSize;
            var hRadius = radius / 2;
            this.eraseIcon = new Element("div", {
                styles: {
                    "border": "1px solid #333",
                    "height": radius,
                    "width": radius,
                    "border-radius": radius,
                    "position": "absolute",
                    "background": "#fff"
                }
            }).inject(this.canvasWrap);
        }

        ctx.beginPath();

        var x , y;
        if(this.rotate && _self.transform > 0){
            var clientY = ev.type.indexOf('touch') !== -1 ? ev.touches[0].clientY : ev.clientY;
            var clientX = ev.type.indexOf('touch') !== -1 ? ev.touches[0].clientX : ev.clientX;
            var newX = clientY;
            var newY = _self.canvas.height - clientX; //y轴旋转偏移 // - parseInt(_self.transformOrigin)
        }else{
            x = ev.clientX-position.x;
            y = ev.clientY-position.y
        }


        ctx.moveTo(x, y);
        if( this.mode === "erasing" ){
            this.eraseIcon.setStyles({
                "top": ( y - hRadius)+"px",
                "left":( x - hRadius)+"px"
            });
            ctx.arc(x, y, 1, 0, 2*Math.PI);
            ctx.fill();
        }

        this.storeToPreArray();

        var mousemove = function(ev){
            var mx , my;
            if(_self.rotate && _self.transform > 0){
                mx = ev.client.y;
                my = _self.canvas.height - ev.client.x //y轴旋转偏移 //  - + parseInt(_self.transformOrigin);
            }else{
                mx = ev.client.x - position.x;
                my = ev.client.y - position.y;
            }

            ctx.lineTo(mx, my);
            ctx.stroke();
            if( _self.mode === "erasing" ) {
                _self.eraseIcon.setStyles({
                    "top": ( my - hRadius) + "px",
                    "left": ( mx - hRadius) + "px"
                });
            }
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
            if(_self.eraseIcon)_self.eraseIcon.destroy();

            if( _self.mode === "erasing" ) {
                if (_self.inputList) _self.inputList.each(function (input) {
                    input.show();
                });
            }
        }.bind(this);
        doc.addEvent("mouseup", mouseup);
        doc.addEvent("touchend", mouseup);
        //document.onmouseup = function(ev){
        //    document.onmousemove = document.onmouseup = null;
        //    ctx.closePath();
        //}
    },
    detectOrient: function(){
        // 利用 CSS3 旋转 对根容器逆时针旋转 90 度
        var size = $(document.body).getSize();
        var width = size.x,
            height = size.y,
            styles = {};

        if( width >= height ){ // 横屏
            this.transform = 0;
            this.transformOrigin = 0;
            styles = {
                "width": width+"px",
                "height": height+"px",
                "webkit-transform": "rotate(0)",
                "transform": "rotate(0)",
                "webkit-transform-origin": "0 0",
                "transform-origin": "0 0"
            }
        }
        else{ // 竖屏
            this.options.lineWidth = 1.5;
            this.transform = 90;
            this.transformOrigin = width / 2;
            styles = {
                "width": height+"px",
                "height": width+"px",
                "webkit-transform": "rotate(90deg)",
                "transform": "rotate(90deg)",
                "webkit-transform-origin": ( this.transformOrigin + "px " + this.transformOrigin + "px"),
                "transform-origin": ( this.transformOrigin + "px " + this.transformOrigin + "px")
            }
        }
        this.container.setStyles(styles);
    },
    uploadImage: function(  success, failure  ){
        var image = this.getImage( null, true );
        Promise.resolve( image ).then(function(image){
            if( image ){
                if( this.options.action ){
                    this.action = (typeOf(this.options.action)=="string") ? o2.Actions.get(this.options.action).action : this.options.action;
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
        })
    },
    getFormData : function( image ){
        if( !image )image = this.getImage();
        return Promise.resolve( image ).then(function(){
            var formData = new FormData();
            formData.append('file', image, this.fileName );
            if( this.options.data ){
                Object.each(this.options.data, function(v, k){
                    formData.append(k, v)
                });
            }
            return formData;
        }.bind(this));
    },
    getImage : function( base64Code, ignoreResultSize ){
        var src = base64Code || this.getBase64Code( ignoreResultSize);
        return Promise.resolve( src ).then(function( src ){
            src=window.atob(src);

            var ia = new Uint8Array(src.length);
            for (var i = 0; i < src.length; i++) {
                ia[i] = src.charCodeAt(i);
            }

            var blob = new Blob([ia], {type: this.fileType });
            var fileName = "image_"+new Date().getTime();
            if( this.fileType && this.fileType.contains("/") ) {
                blob.name = fileName + "." + this.fileType.split("/")[1];
            }else{
                blob.name = fileName + ".unknow";
            }

            return blob;

        }.bind(this));
    },
    getBase64Code : function( ignoreResultSize ){
        if( !ignoreResultSize && this.options.resultMaxSize ){
            return Promise.resolve( this.drawInput() ).then(function() {
                var src = this.canvas.toDataURL(this.fileType);
                src = src.split(',')[1];
                return src = 'data:' + this.fileType + ';base64,' + src;
            }.bind(this)).then( function( src ){
                return this.loadImage( src );
            }.bind(this)).then(function( tmpImageNode ){
                var ctx = this.ctx;
                var canvas = this.canvas;
                var width, height;
                width = Math.min(this.contentWidth, this.options.resultMaxSize);
                height = (width / this.contentWidth) * this.contentHeight;

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
            }.bind(this));
        }else{
            return Promise.resolve( this.drawInput() ).then(function(){
                var src= this.canvas.toDataURL( this.fileType );
                src=src.split(',')[1];

                if(!src){
                    return "";
                }else{
                    return src;
                }
            }.bind(this))
        }

    },
    getBase64Image: function( base64Code, ignoreResultSize ){
        if( !base64Code )base64Code = this.getBase64Code( ignoreResultSize );
        return Promise.resolve( base64Code ).then(function( base64Code ){
            if( !base64Code )return null;
            return 'data:'+ this.fileType +';base64,' + base64Code;
        }.bind(this));
    },
    drawInput: function(){
        if( this.inputList ){
            var list = this.inputList.map(function (input) {
                return input.draw();
            });
            return Promise.all( list );
        }else{
            return "";
        }
    },
    close : function(){
        if( this.inputList ){
            this.inputList.each(function (input) {
                input.close( true );
            })
        }
        this.container.destroy();
        delete this;
    },
    checkBroswer : function(){
        if( window.Uint8Array && window.HTMLCanvasElement && window.atob && window.Blob){
            this.available = true;
            return true;
        }else{
            this.available = false;
            this.container.set("html", "<p>"+o2.LP.widget.explorerNotSupportFeatures+"</p><ul><li>canvas</li><li>Blob</li><li>Uint8Array</li><li>FormData</li><li>atob</li></ul>");
            return false;
        }
    },
    isBlank: function(){
        var canvas = this.canvas;
        var blank = new Element("canvas", {
            width : canvas.width,
            height : canvas.height
        });
        // var blank = document.createElement('canvas');//系统获取一个空canvas对象
        // blank.width = canvas.width;
        // blank.height = canvas.height;
        return canvas.toDataURL() == blank.toDataURL(); //比较值相等则为空
    },
    save : function(){
        var _slef = this;
        Promise.resolve( this.getBase64Code() ).then(function ( base64code ) {
            _slef.getImage( base64code ).then(function( imageFile ){
                _slef.getBase64Image( base64code ).then(function (base64Image) {
                    _slef.options.imageSrc = base64Image;
                    _slef.fireEvent("save", [ base64code, base64Image, imageFile]);
                });
            })
        })
        // var imageFile = this.getImage( base64code );
        // var base64Image = this.getBase64Image( base64code );
        // this.fireEvent("save", [ base64code, base64Image, imageFile]);
    },
    reset : function( itemNode ){
        this.fileName = "untitled.png";
        this.fileType = "image/png";
        if( this.ctx ){
            var canvas = this.canvas;
            this.ctx.clearRect(0,0,canvas.clientWidth,canvas.clientHeight);
        }
        if( this.inputList ){
            this.inputList.each(function (input) {
                input.close();
            })
            this.currentInput = null;
        }
        this.fireEvent("reset");
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
            var container = this.inMobileDevice ? $(document.body) : this.container;
            this.sizeSelector = new o2.widget.Tablet.SizePicker(container, itemNode, null, {}, {
                "onSelect": function (width) {
                    this.currentWidth = width;
                }.bind(this),
                "onHide": function () {
                    itemNode.fireEvent("mouseout");
                },
                "event" : this.inMobileDevice ? "click" : "mouseenter",
                "hasMask": false,
                "zoom":  this.options.toolsScale || 1
            });
        }
    },
    eraserRadius : function( itemNode ){
        if( !this.eraserRadiusSelector ){
            var container = this.inMobileDevice ? $(document.body) : this.container;
            this.eraserRadiusSelector = new o2.widget.Tablet.EraserRadiusPicker(container, itemNode, null, {}, {
                "onSelect": function (width) {
                    this.currentEraserRadius = width;
                }.bind(this),
                "onHide": function () {
                    itemNode.fireEvent("mouseout");
                },
                "event" : this.inMobileDevice ? "click" : "mouseenter",
                "hasMask": false,
                "zoom":  this.options.toolsScale || 1
            });
        }
    },
    color : function( itemNode ){
        if( !this.colorSelector ){
            var container = this.inMobileDevice ? $(document.body) : this.container;
            this.colorSelector = new o2.xApplication.Template.widget.ColorPicker( container, itemNode, null, {}, {
                "lineWidth" : 1,
                "onSelect": function (color) {
                    this.currentColor = color;
                    if( this.currentInput ){
                        this.currentInput.setColor(color);
                    }
                }.bind(this),
                "onHide": function () {
                    itemNode.fireEvent("mouseout");
                },
                "event" : this.inMobileDevice ? "click" : "mouseenter",
                "hasMask": false,
                "zoom":  this.options.toolsScale || 1
            });
        }
    },
    fontFamily: function (itemNode) {
        if( !this.fontfamilySelector ){
            var container = this.inMobileDevice ? $(document.body) : this.container;
            this.fontfamilySelector = new o2.widget.Tablet.FontFamily(itemNode, {
                "onSelectItem": function (node, d) {
                    this.currentFontFamily = d.val;
                    if( this.currentInput ){
                        this.currentInput.setFontFamily(d.val);
                    }
                }.bind(this),
                "tooltipsOptions": {
                    "onHide": function () {
                        itemNode.fireEvent("mouseout");
                    },
                    "event" : this.inMobileDevice ? "click" : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
                    "hasMask": false,
                    "zoom":  this.options.toolsScale || 1
                }
            }, null, null, container);
            this.fontfamilySelector.load();
        }
    },
    fontSize: function (itemNode) {
        if( !this.fontsizeSelector ){
            var container = this.inMobileDevice ? $(document.body) : this.container;
            this.fontsizeSelector = new o2.widget.Tablet.FontSize(itemNode, {
                "onSelectItem": function (node, d) {
                    this.currentFontSize = d.value +"px";
                    if( this.currentInput ){
                        this.currentInput.setFontSize(d.value+"px");
                    }
                }.bind(this),
                "tooltipsOptions": {
                    "onHide": function () {
                        itemNode.fireEvent("mouseout");
                    },
                    "event" : this.inMobileDevice ? "click" : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
                    "hasMask": false,
                    "zoom":  this.options.toolsScale || 1
                }
            }, null, null, container);
            this.fontsizeSelector.load();
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

            var mover = new o2.widget.Tablet.ImageMover( this, imageNode, this.canvasWrap , {
                onPostOk : function(){
                    var coordinate =  mover.getCoordinates();
                    this.storeToPreArray();
                    this.ctx.drawImage(imageNode, coordinate.left, coordinate.top, coordinate.width, coordinate.height);
                    this.storeToMiddleArray();

                    if(this.globalCompositeOperation)this.ctx.globalCompositeOperation = this.globalCompositeOperation;
                    this.globalCompositeOperation = null;
                }.bind(this),
                onPostCancel: function(){
                    if(this.globalCompositeOperation)this.ctx.globalCompositeOperation = this.globalCompositeOperation;
                    this.globalCompositeOperation = null;
                }.bind(this),
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
            this.globalCompositeOperation = this.ctx.globalCompositeOperation;
            this.ctx.globalCompositeOperation = "source-over";
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
                this.globalCompositeOperation = this.ctx.globalCompositeOperation;
                this.ctx.globalCompositeOperation = "source-over";
                this.parseFileToImage( img );
            }.bind(this)
        }, this);
        clipper.load();
    },
    input: function( itemNode ){
        this.mode = "inputing";
        this.toolbar.enableItem("pen");
        this.toolbar.enableItem("eraser");
        this.toolbar.activeItem("input");
        this.toolbar.hideItem("eraserRadius");
        this.toolbar.hideItem("size");
        this.toolbar.showItem("fontSize");
        this.toolbar.showItem("fontFamily");
        this.toolbar.showItem("color");
    },
    eraseInput: function(input){
        this.inputList.erase(input);
    },
    eraser : function( itemNode ){
        this.mode = "erasing";
        this.toolbar.enableItem("pen");
        this.toolbar.activeItem("eraser");
        this.toolbar.showItem("eraserRadius");
        this.toolbar.hideItem("size");
        this.toolbar.hideItem("color");
        this.toolbar.hideItem("fontSize");
        this.toolbar.hideItem("fontFamily");
        this.toolbar.enableItem("input");
    },
    pen : function( itemNode ){
        this.mode = "writing";
        this.toolbar.activeItem("pen");
        this.toolbar.enableItem("input");
        this.toolbar.enableItem("eraser");
        this.toolbar.hideItem("eraserRadius");
        this.toolbar.hideItem("fontSize");
        this.toolbar.hideItem("fontFamily");
        this.toolbar.showItem("size");
        this.toolbar.showItem("color");
    },
    cancel: function(){
        var _self = this;
        this.reset();
        if( this.options.imageSrc ){
            var img = new Element("img", {
                "crossOrigin": "",
                "src": this.options.imageSrc,
                "events": {
                    "load": function () {
                        _self.ctx.drawImage(this, 0, 0);
                        var preData=_self.ctx.getImageData(0,0,_self.contentWidth,_self.contentHeight);
                        _self.middleAry.push(preData);
                        _self.toolbar.enableItem("reset");
                    }
                }
            })
        }
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
            eraser : {
                enable : function(){ return true },
                active : function(){ return this.tablet.mode === "erasing" }.bind(this)
            },
            eraserRadius: {
                enable : function(){ return true },
                show : function(){ return this.tablet.mode === "erasing" }.bind(this)
            },
            input: {
                enable : function(){ return true },
                active : function(){ return this.tablet.mode === "inputing" }.bind(this)
            },
            pen: {
                enable : function(){ return true },
                active : function(){ return this.tablet.mode === "writing" }.bind(this)
            },
            size : {
                enable : function(){ return true },
                show : function(){ return this.tablet.mode === "writing" }.bind(this)
            },
            fontSize : {
                enable : function(){ return true },
                show : function(){ return this.tablet.mode === "inputing" }.bind(this)
            },
            fontFamily : {
                enable : function(){ return true },
                show : function(){ return this.tablet.mode === "inputing" }.bind(this)
            },
            color : {
                enable : function(){ return true },
                show : function(){ return this.tablet.mode === "writing" || this.tablet.mode === "inputing" }.bind(this)
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
                "eraser", "|",
                "input", "|",
                "pen", "|",
                "eraserRadius","|",
                "size", "|",
                "color", "|",
                "fontSize", "|",
                "fontFamily", "|",
                "image", "|",
                "imageClipper"
            ];
        }

        if( this.tablet.options.toolHidden.contains("eraser") && this.tablet.options.toolHidden.contains("input")){
            this.tablet.options.toolHidden.push("pen");
        }
        if( this.tablet.options.toolHidden.contains("eraser")){
            this.tablet.options.toolHidden.push("eraserRadius");
        }
        if( this.tablet.options.toolHidden.contains("input")){
            this.tablet.options.toolHidden.push("fontSize");
            this.tablet.options.toolHidden.push("fontFamily");
        }



        items = items.filter(function(tool){
            return !this.tablet.options.toolHidden.contains(tool)
        }.bind(this));
        items = items.clean();

        for( var i=1; i<items.length; i++ ){
            if( items[i-1]==="|" && items[i]==="|")items[i-1] = null;
        }
        items = items.clean();

        var html = "";
        var style = this.tablet.options.iconfontEnable ? "toolItemIconfont" : "toolItem";
        var styleRight = this.tablet.options.iconfontEnable ? "toolRightItemIconfont" : "toolRightItem";
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
                case "eraser" :
                    html +=  "<div item='eraser' styles='" + style + "'>"+ this.lp.eraser  +"</div>";
                    break;
                case "eraserRadius" :
                    html +=  "<div item='eraserRadius' styles='" + style + "'>"+ this.lp.eraserRadius  +"</div>";
                    break;
                case "input" :
                    html +=  "<div item='input' styles='" + style + "'>"+ this.lp.input  +"</div>";
                    break;
                case "pen" :
                    html +=  "<div item='pen' styles='" + style + "'>"+ this.lp.pen  +"</div>";
                    break;
                case "size" :
                    html +=  "<div item='size' styles='" + style + "'>"+ this.lp.size  +"</div>";
                    break;
                case "color" :
                    html +=  "<div item='color' styles='" + style + "'>"+ this.lp.color  +"</div>";
                    break;
                case "fontSize" :
                    html +=  "<div item='fontSize' style='float: left;margin-top:20px;margin-left: 5px;'></div>";
                    break;
                case "fontFamily" :
                    html +=  "<div item='fontFamily' style='float: left;margin-top:20px;margin-left: 5px;'></div>";
                    break;
                case "image" :
                    html +=  "<div item='image' styles='" + style + "'>"+ this.lp.image  +"</div>";
                    break;
                case "imageClipper" :
                    html +=  "<div item='imageClipper' styles='" + style + "'>"+ this.lp.imageClipper  +"</div>";
                    break;
                case "cancel" :
                    html +=  "<div item='cancel' styles='"+ styleRight +"'>"+ this.lp.cancel  +"</div>";
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
                if( _self.tablet.options.iconfontEnable ){
                    var text = el.get("text");
                    el.set("text", "");
                    new Element("i.o2icon-"+item, {styles: this.css.toolItemIconfont_icon}).inject( el, "top" );
                    new Element("div", {text: text, styles: this.css.toolItemIconfont_text}).inject( el );
                }else{
                    el.setStyle("background-image","url("+ imagePath + item +"_normal.png)");
                }
                el.addEvents({
                    mouseover : function(){
                        _self._setItemNodeActive(this.el);
                    }.bind({ item : item, el : el }),
                    mouseout : function(){
                        var active = false;
                        if( _self.itemsEnableFun[item] && _self.itemsEnableFun[item].active ){
                            active = _self.itemsEnableFun[item].active();
                        }
                        if(!active)_self._setItemNodeNormal(this.el);
                    }.bind({ item : item, el : el }),
                    click : function( ev ){
                        if( _self["tablet"][this.item] )_self["tablet"][this.item]( this.el );
                    }.bind({ item : item, el : el })
                });
                if( item == "color" || item == "size" || item == "eraserRadius" ){
                    if( _self["tablet"][item] )_self["tablet"][item]( el );
                }
            }
        }.bind(this));

        var fontSizeItem = this.container.getElement("[item='fontSize']");
        if(fontSizeItem){
            this.items["fontSize"] = fontSizeItem;
            this.tablet.fontSize(fontSizeItem);
        }

        var fontFamilyItem = this.container.getElement("[item='fontFamily']");
        if(fontFamilyItem){
            this.items["fontFamily"] = fontFamilyItem;
            this.tablet.fontFamily(fontFamilyItem);
        }

        this.setAllItemsStatus();
        this.setAllItemsShow();
        this.setAllItemsActive();
    },
    setAllItemsShow : function(){
        for( var item in this.items ){
            var node = this.items[item];
            if( this.itemsEnableFun[item] && this.itemsEnableFun[item].show ){
                if( !this.itemsEnableFun[item].show() ){
                    this.hideItem( item );
                }
            }
        }
    },
    setAllItemsActive : function(){
        for( var item in this.items ){
            var node = this.items[item];
            if( this.itemsEnableFun[item] && this.itemsEnableFun[item].active ){
                if( this.itemsEnableFun[item].active() ){
                    this.activeItem( item );
                }
            }
        }
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
    showItem: function( itemName ){
        var itemNode =  this.items[ itemName ];
        if(itemNode)itemNode.show();
    },
    hideItem: function( itemName ){
        var itemNode =  this.items[ itemName ];
        if(itemNode)itemNode.hide();
    },
    disableItem : function( itemName ){
        var itemNode =  this.items[ itemName ];
        if(itemNode){
            itemNode.store("status", "disable");
            this._setItemNodeDisable( itemNode, itemName );
        }
    },
    enableItem : function( itemName ){
        var itemNode =  this.items[ itemName ];
        if(itemNode) {
            itemNode.store("status", "enable");
            this._setItemNodeNormal(itemNode, itemName);
        }
    },
    activeItem: function( itemName ){
        var itemNode =  this.items[ itemName ];
        if(itemNode) {
            itemNode.store("status", "active");
            this._setItemNodeActive(itemNode, itemName);
        }
    },
    _setItemNodeDisable : function( itemNode, itemName ){
        var item = itemNode.get("item");
        if(item){
            if( ["fontSize","fontFamily"].contains( itemName ) ){
                itemNode.hide();
            }else{
                if( this.tablet.options.iconfontEnable ){
                    itemNode.setStyles(this.css.toolItemIconfont_disable);
                    if( this.tablet.options.mainColorEnable )itemNode.removeClass("mainColor_color");
                }else {
                    itemNode.setStyles(this.css.toolItem_disable);
                    itemNode.setStyle("background-image", "url(" + this.imagePath + item + "_disable.png)");
                }
            }
        }
    },
    _setItemNodeActive: function( itemNode, itemName ){
        if( itemNode.retrieve("status") == "disable" )return;
        var item = itemNode.get("item");
        if(item){
            if( ["fontSize","fontFamily"].contains( itemName ) ){
                if( this.itemsEnableFun[itemName] && this.itemsEnableFun[itemName].show ){
                    if( this.itemsEnableFun[item].show() ){
                        itemNode.show();
                    }
                }
            }else{
                if( this.tablet.options.iconfontEnable ){
                    itemNode.setStyles(this.css.toolItemIconfont_over);
                    if( this.tablet.options.mainColorEnable )itemNode.addClass("mainColor_color");
                }else {
                    itemNode.setStyles(this.css.toolItem_over);
                    itemNode.setStyle("background-image", "url(" + this.imagePath + item + "_active.png)");
                }
            }
        }
    },
    _setItemNodeNormal: function( itemNode, itemName ){
        if( itemNode.retrieve("status") == "disable" )return;
        var item = itemNode.get("item");
        if(item){
            if( ["fontSize","fontFamily"].contains( itemName ) ){
                if( this.itemsEnableFun[itemName] && this.itemsEnableFun[itemName].show ){
                    if( this.itemsEnableFun[item].show() ){
                        itemNode.show();
                    }
                }
            }else{
                var style = itemNode.get("styles");
                if( this.tablet.options.iconfontEnable ){
                    itemNode.setStyles(this.css[style]);
                    if( this.tablet.options.mainColorEnable )itemNode.removeClass("mainColor_color");
                }else{
                    itemNode.setStyles( this.css[style] );
                    itemNode.setStyle("background-image","url("+  this.imagePath+ item +"_normal.png)");
                }
            }
        }
    }

});

o2.widget.Tablet.ToolbarMobile = new Class({
    Extends: o2.widget.Tablet.Toolbar,
    Implements: [Options, Events],
    load: function(){
        this.tablet.container.setStyle("position","relative");
        Array.each([{
            "name": "cancel", "text": this.lp.cancel
        },{
            "name": "save", "text": this.lp.save
        },{
            "name": "undo"
        },{
            "name": "redo"
        },{
            "name": "reset"
        }], function (item) {
            this.items[item.name] = new Element("div",{
                styles : this.css[item.name+"_mobile"],
                events: {
                    click: function () {
                        if( this.tablet[item.name] )this.tablet[item.name]( this.items[item.name] );
                    }.bind(this)
                }
            }).inject(this.tablet.container);
            if(item.text)this.items[item.name].set("text", item.text);
            if( item.name === "save" )this.items[item.name].addClass("mainColor_color");
        }.bind(this));
        this.setAllItemsStatus();
    },
    _setItemNodeDisable : function( itemNode, itemName ){
        var item = itemNode.get("item");
        itemNode.setStyles( this.css[itemName+"_mobile_disable"] );
    },
    _setItemNodeActive: function( itemNode, itemName ){
        if( itemNode.retrieve("status") == "disable" )return;
        var item = itemNode.get("item");
        itemNode.setStyles( this.css[itemName+"_mobile_over"] );
    },
    _setItemNodeNormal: function( itemNode, itemName ){
        if( itemNode.retrieve("status") == "disable" )return;
        var item = itemNode.get("item");
        itemNode.setStyles( this.css[itemName+"_mobile"] );
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
        this.range = [1, 30];
        this.ruleList = ["0.1","0.5","1","5","10", "15","20"];
        o2.UD.getDataJson("sizePicker", function(json) {
            this._loadContent(json);
        }.bind(this));
    },
    changeValue: function(value){
        if( value < 10 ){
            this.lineWidth = (value / 10)
        }else{
            this.lineWidth = value - 9;
        }
        this.drawPreview( this.lineWidth );
        this.fireEvent("select", this.lineWidth )
    },
    reset: function(){
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
    },
    _loadContent: function(json){
        this.rulerContainer = new Element("div",{
            styles : {
                "margin-left": " 23px",
                "margin-right": " 1px",
                "width" : "228px"
            }
        }).inject(this.node);


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
            range: this.range,
            initialStep: 10,
            onChange: function(value){
                this.changeValue( value );
            }.bind(this)
        });

        var previewContainer = new Element("div").inject(this.node);
        new Element("div",{ text : o2.LP.widget.preview, styles : {
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
            text : o2.LP.widget.reset,
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
                    this.reset();
                }.bind(this)
            }
        }).inject( this.node );
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

o2.widget.Tablet.EraserRadiusPicker = new Class({
    Extends: o2.widget.Tablet.SizePicker,
    options: {
        lineWidth : 20,
        nodeStyles : {
            "min-width" : "260px"
        },
    },
    _loadContent: function(json){
        this.rulerContainer = new Element("div",{
            styles : {
                "margin-left": " 23px",
                "margin-right": " 1px",
                "width" : "228px"
            }
        }).inject(this.node);


        this.rulerTitleContainer = new Element("div",{
            styles : { "overflow" : "hidden" }
        }).inject( this.rulerContainer );
        this.ruleList.each( function( rule ){
            new Element("div", {
                text : rule,
                styles : {
                    width : "24px",
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
                    width : "24px",
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
            range: this.range,
            initialStep: 20,
            onChange: function(value){
                this.changeValue( value );
            }.bind(this)
        });

        var previewContainer = new Element("div", {"style":"overflow:hidden;"}).inject(this.node);
        new Element("div",{ text : o2.LP.widget.preview, styles : {
                "float" : "left",
                "margin-top" : "15px",
                "width" : "30px"
            }}).inject(this.silderContainer);
        this.previewNode = new Element("div", {
            styles : {
                "float" : "left",
                "margin" : "0px 0px 0px 37px",
                "width" : "100px"
            }
        }).inject( this.node );
        this.canvas = new Element("canvas", {
            width : 200,
            height : 60
        }).inject( this.previewNode );
        this.ctx = this.canvas.getContext("2d");
        this.drawPreview();

        new Element("button", {
            text : o2.LP.widget.reset,
            type : "button",
            styles :{
                "float" : "left",
                "margin-top" : "10px",
                "font-size" : "12px",
                "border-radius" : "3px",
                "cursor" : "pointer" ,
                "border" : "1px solid #ccc",
                "padding" : "5px 10px",
                "background-color" : "#f7f7f7"
            },
            events : {
                click : function(){
                    this.reset();
                }.bind(this)
            }
        }).inject( this.node );
    },
    _customNode : function( node ){
        this.range = [1, 40];
        this.ruleList = ["1","5","10", "15","20","25","30","35","40"];
        o2.UD.getDataJson("eraserRadiusPicker", function(json) {
            this._loadContent(json);
        }.bind(this));
    },
    changeValue: function(value){
        this.lineWidth = value;
        this.drawPreview( this.lineWidth );
        this.fireEvent("select", this.lineWidth )
    },
    reset: function(){
        this.lineWidth = this.options.lineWidth || 20;
        var step = this.lineWidth;
        this.slider.set( parseInt( step ) );
        this.drawPreview( this.lineWidth );
        this.fireEvent("select", this.lineWidth )
    },
    drawPreview : function( lineWidth ){
        if( !lineWidth )lineWidth = this.options.lineWidth || 20;
        var canvas = this.canvas;
        var ctx = this.ctx;
        ctx.clearRect(0,0,canvas.clientWidth,canvas.clientHeight);

        // ctx.strokeStyle="#000000"; //线条颜色; 默认 #000000
        // ctx.lineCap = "round";　　//设置线条两端为圆弧
        // ctx.lineJoin = "round";　　//设置线条转折为圆弧
        // ctx.lineWidth=  lineWidth ;
        // ctx.beginPath();
        // ctx.lineTo( 28, 25  );
        // ctx.stroke();

        ctx.beginPath();
        ctx.arc(30, 30, lineWidth/2, 0, 2 * Math.PI, false);
        //ctx.fillStyle = 'green';
        ctx.fill();
        // ctx.lineWidth = 5;
        // ctx.strokeStyle = '#000000';
        //ctx.stroke();

    }
});

MWF.require("MWF.widget.ImageClipper", null, false);
o2.widget.Tablet.ImageClipper = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "imageUrl" : "",
        "resultMaxSize" : 700,
        "description" : "",
        "title": o2.LP.widget.imageClipper,
        "style": "default",
        "aspectRatio": 0
    },
    initialize: function(app, options, tablet){
        this.setOptions(options);
        this.app = app;
        this.tablet = tablet;
        this.path = "../x_component_process_Xform/widget/$ImageClipper/";
        this.cssPath = "../x_component_process_Xform/widget/$ImageClipper/"+this.options.style+"/css.wcss";
        this._loadCss();
    },

    load: function(data){
        this.data = data;

        var options = {};
        var width = "700";
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
                "zindex": this.tablet.options.zIndex,
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
                    //this.node.setStyle("z-index",1003);
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
        this.css = this.tablet.css;
        this.imageNode = imageNode;
        this.relativeNode = relativeNode;
        this.path = this.tablet.path + this.tablet.options.style + "/"
    },
    load: function(){
        this.maskNode = new Element("div.maskNode",{
            styles : this.css.imageMoveMaskNode
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
                "z-index" : this.tablet.options.zIndex + 3,
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
            styles : this.css.imageMoveOkNode,
            events : {
                click : function(){
                    this.ok();
                    this.close();
                }.bind(this)
            }
        }).inject(this.dragNode);

        this.cancelNode = new Element("div",{
            styles : this.css.imageMoveCancelNode,
            events : {
                click : function(){
                    this.fireEvent("postCancel");
                    this.close();
                }.bind(this)
            }
        }).inject(this.dragNode);

        this.drag = this.dragNode.makeDraggable({
            "container" : this.node,
            "handle": this.dragNode
        });


        this.resizeNode = new Element("div.resizeNode",{
            styles : this.css.imageMoveResizeNode
        }).inject(this.dragNode);

        this.docBody = window.document.body;
        this.resizeNode.addEvents({
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
    getCoordinates : function(){
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

o2.widget.Tablet.Input = new Class({
    Implements: [Options, Events],
    options: {
        minWidth: 100,
        minHeight: 30,
        width: 200,
        height: 60,
        top: 0,
        left: 0,
        isEditing: true,
        editable: true,
        text: "",
        scale: 1
    },
    initialize: function (tablet, relativeNode, options, data) {
        this.setOptions(options);
        this.tablet = tablet;
        this.css = this.tablet.css;
        this.relativeNode = relativeNode;
        this.path = this.tablet.path + this.tablet.options.style + "/";
        this.data = data || {};
        var styles = this.data.styles || {};
        this.color = styles.color || this.tablet.currentColor;
        this.fontSize = styles["font-size"] || this.tablet.currentFontSize;
        this.fontFamily = styles["font-family"] || this.tablet.currentFontFamily;
    },
    readMode: function(){
        if( this.editarea && !this.editarea.get("html") ){
            this.close();
            return;
        }
        this.options.isEditing = false;
        // if(this.drag)this.drag.detach();
        if( this.dragNode )this.dragNode.hide(); //.setStyle("cursor","none");
        if( this.resizeNode )this.resizeNode.hide(); //.setStyle("cursor", "none" );
        if( this.cancelNode )this.cancelNode.hide();
        if( this.editareaWrap )this.editareaWrap.setStyle("border", "1px dashed transparent");
        this.node.setStyle("background" , "rgba(255,255,255,0)")
    },
    editMode: function(){
        if(this.tablet.currentInput)this.tablet.currentInput.readMode();
        this.tablet.currentInput = this;
        this.options.isEditing = true;
        // if(this.drag)this.drag.attach();
        if( this.dragNode )this.dragNode.show(); //.setStyle("cursor","move");
        if( this.resizeNode )this.resizeNode.show(); //.setStyle("cursor", "nw-resize" );
        if( this.editareaWrap )this.editareaWrap.setStyle("border", "1px dashed red");
        if( this.cancelNode )this.cancelNode.show();
        this.node.setStyle("background" , "rgba(255,255,255,0.5)")
    },
    scaleTo: function( scale ){
        if( this.options.scale === scale )return;
        this.options.scale = scale;
        if( layout.mobile ){
            var width = (24/this.options.scale);
            if(this.cancelNode)this.cancelNode.setStyles({
                "width" : width+"px",
                "height" : width +"px",
                "top": "-"+(width/2)+"px",
                "right": "-"+(width/2)+"px",
                "background-size": (16/this.options.scale)+"px " + (16/this.options.scale)+"px"
            });
            if(this.dragNode)this.dragNode.setStyles({
                "width" : width+"px",
                "height" : width +"px",
                "top": "-"+(width/2)+"px",
                "left": "-"+(width/2)+"px",
                "background-size": (16/this.options.scale)+"px " + (16/this.options.scale)+"px"
            });
            if(this.resizeNode)this.resizeNode.setStyles({
                "width" : width+"px",
                "height" : width +"px",
                "bottom": "-"+(width/2)+"px",
                "right": "-"+(width/2)+"px",
                "background-size": (16/this.options.scale)+"px " + (16/this.options.scale)+"px"
            });
        }
    },
    getUrl: function ( id ) {
        var address = o2.Actions.getHost("x_processplatform_assemble_surface");
        var serviceName = o2.Actions.load("x_processplatform_assemble_surface").AttachmentAction.action.serviceName;
        var u = o2.Actions.load("x_processplatform_assemble_surface").AttachmentAction.action.actions.downloadTransfer.uri;
        var url = u.replace("{flag}", id);
        url = address + "/" + serviceName +  url;

        var uri = new URI( url );
        uri.setData({"stream":"true"}, true);
        if( !uri.getData( o2.tokenName ) ){
            var token = {};
            token[ o2.tokenName ] = this.getToken();
            uri.setData(token, true);
        }
        return uri.toString();
    },
    getToken: function(){
        var token = (layout.config && layout.config.sessionStorageEnable) ? sessionStorage.getItem("o2LayoutSessionToken") : "";
        if (!token) {
            if (layout.session && (layout.session.user || layout.session.token)) {
                token = layout.session.token;
                if (!token && layout.session.user && layout.session.user.token) token = layout.session.user.token;
            }
        }
        return token;
    },
    draw: function( callback ){
        var _self = this;
        if( !this.editarea.offsetParent && this.image ){ //如果被隐藏了
            return this.image || null;
        }else {
            var editareaSize = this.scaleSize(this.editarea.getSize());
            var nodeSize = this.scaleSize(this.node.getSize());
            var size = {
                x : Math.max(editareaSize.x, nodeSize.x),
                y: Math.max(editareaSize.y, nodeSize.y)
            }
            var div = new Element("div", {
                "styles":{
                    "padding": "0px",
                    "margin": "0px",
                    "width": size.x + "px",
                    "height": size.y + "px",
                },
                "html": this.editarea.outerHTML
            });
            return o2.Actions.load("x_processplatform_assemble_surface").AttachmentAction.htmlToImage({
                workHtml: div.outerHTML,
                htmlWidth: size.x,
                htmlHeight:size.y,
                startX: 0,
                startY: 0,
                omitBackground: true
            }).then(function (json) {
                return _self.tablet.loadImage( _self.getUrl(json.data.id) );
                // o2.Actions.load(x_processplatform_assemble_surface).AttachmentAction.downloadTransfer(json.data.id,function () {
                //
                // })
            }).then(function (image) {
                _self.image = image;
                _self.fireEvent("postDraw", [image]);
                return image;
            });
        }
    },
    // draw: function( callback ){
    //     var _self = this;
    //     if( !this.editarea.offsetParent && this.image ){ //如果被隐藏了
    //         return this.image || null;
    //     }else {
    //         var opt = {
    //             useCORS: true,
    //             allowTaint: true,
    //             backgroundColor: null
    //         };
    //         var scale = this.tablet.options.scale;
    //         if( scale && scale !== 1 ){
    //             opt.fontScale = scale;
    //             //opt.scale = 1;
    //         }
    //         return window.html2canvas(this.editarea, opt).then(function (canvas) {
    //             var src = canvas.toDataURL(_self.tablet.fileType);
    //             src = src.split(',')[1];
    //             src = 'data:' + _self.tablet.fileType + ';base64,' + src;
    //             canvas.destroy();
    //             return src
    //         }).then(function (src) {
    //             return _self.tablet.loadImage(src)
    //         }).then(function (image) {
    //             _self.image = image;
    //             _self.fireEvent("postDraw", [image]);
    //             return image;
    //         });
    //     }
    // },
    // load: function(){
    //     o2.load("../o2_lib/html2canvas/html2canvas.js", function() {
    //         this._load();
    //     }.bind(this))
    // },
    load: function(){
        // var coordinates = this.relativeNode.getCoordinates();
        this.relativeCoordinates = this.scaleSize( this.relativeNode.getCoordinates() );
        var top, left, width, height;
        if( this.data.coordinates ){
            top = this.data.coordinates.top;
            left = this.data.coordinates.left;
            width = this.data.coordinates.width;
            height = this.data.coordinates.height;
        }else{
            var top = this.options.top;
            if( top + this.options.height > this.relativeCoordinates.height ){
                top = this.relativeCoordinates.height - this.options.height;
                this.options.top = top;
            }
            var left = this.options.left;
            if( left + this.options.width > this.relativeCoordinates.width ){
                left = this.relativeCoordinates.width - this.options.width;
                this.options.left = left;
            }
            width = this.options.width;
            height = this.options.height;
        }

        this.node = new Element( "div", {
            styles : {
                "width" : width+"px",
                "min-height" : height+"px",
                "position" : "absolute",
                "top" : top+"px",
                "left" : left+"px",
                "background" : "rgba(255,255,255,0.5)",
                // "z-index" : 1003,
                "-webkit-user-select": "none",
                "-moz-user-select": "none",
                "user-select" : "none"
            },
            events:{
                "touchstart": function (ev) {
                    if( !this.options.editable )return;
                    if( !this.options.isEditing )this.editMode();
                    if( this.tablet.mode !== "inputing" )this.tablet.input();
                    ev.stopPropagation();
                }.bind(this),
                "mousedown": function (ev) {
                    if( !this.options.editable )return;
                    if( !this.options.isEditing )this.editMode();
                    if( this.tablet.mode !== "inputing" )this.tablet.input();
                    ev.stopPropagation();
                }.bind(this)
            }
        }).inject(this.relativeNode);

        this.editareaWrap = new Element("div", { styles: this.css.inputEditareaWrap }).inject(this.node);

        this.editarea = new Element("div", {
            "contenteditable": true,
            "styles": this.css.inputEditarea
        }).inject( this.editareaWrap );
        this.editarea.setStyles({
            "color": this.color,
            "font-family": this.fontFamily,
            "font-size": this.fontSize
        });
        if( this.data.html )this.editarea.set("html", this.data.html);
        if( this.options.editable ){
            this.editarea.addEvent("blur", function(e){
                this.checkPosition();
            }.bind(this));
        }
        // if(Browser && Browser.name === "ie" ){
        //     if( window.MutationObserver ){
        //         var mo = new window.MutationObserver(function(e) {
        //             this.checkPosition();
        //         }.bind(this));
        //         mo.observe(this.editarea, { childList: true, subtree: true, characterData: true });
        //     }
        // }else{
        //     this.editarea.addEvent("input", function(e){
        //         this.checkPosition();
        //     }.bind(this));
        // }

        if( this.options.editable ){
            this.cancelNode = new Element("div",{
                styles :  this.css[ layout.mobile? "inputCancelNode_mobile" : "inputCancelNode"],
                events : {
                    click : function(){
                        this.fireEvent("postCancel");
                        this.tablet.currentInput = null;
                        this.close();
                    }.bind(this)
                }
            }).inject(this.editareaWrap);
            if( layout.mobile ){
                var width = (24/this.options.scale);
                this.cancelNode.setStyles({
                    "width" : width+"px",
                    "height" : width +"px",
                    "top": "-"+(width/2)+"px",
                    "right": "-"+(width/2)+"px",
                    "background-size": (16/this.options.scale)+"px " + (16/this.options.scale)+"px"
                });
            }

            this.loadDragNode();

            this.loadResizeNode();
        }

        if( this.options.isEditing ){
            window.setTimeout(function () {
                this.editarea.focus();
            }.bind(this), 100)
        }else{
            this.readMode();
        }
    },
    loadDragNode: function(){
        this.dragNode = new Element("div.dragNode",{
            styles : this.css[ layout.mobile? "inputDragNode_mobile" : "inputDragNode"]
        }).inject(this.editareaWrap);
        if( layout.mobile ){
            var width = (24/this.options.scale);
            this.dragNode.setStyles({
                "width" : width+"px",
                "height" : width +"px",
                "top": "-"+(width/2)+"px",
                "left": "-"+(width/2)+"px",
                "background-size": (16/this.options.scale)+"px " + (16/this.options.scale)+"px"
            });
        }

        this.dragBody = this.relativeNode; //window.document.body;

        var startFun = function(ev){
            if( !this.options.isEditing )return;
            this.dragBody.setStyle("cursor", "move" );
            this.relativeCoordinates = this.scaleSize( this.relativeNode.getCoordinates() );
            this.dragMode = true;
            this.fireEvent("dragStart");
            ev.stopPropagation();
        }.bind(this);
        var moveFun = function(ev){
            if( !this.dragMode )return;
            var point = this.getLastPoint(ev);
            this.drag( point );
            this.fireEvent("drag");
            ev.stopPropagation();
        }.bind(this);
        var endFun = function(ev){
            this.dragBody.setStyle("cursor", "default" );
            this.dragMode = false;
            this.lastPoint=null;
            this.fireEvent("dragComplete");
            ev.stopPropagation();
        }.bind(this);

        this.dragNode.addEvents({
            "touchstart" : startFun,
            "mousedown" : startFun,
            "touchmove" : moveFun,
            "mousemove" : moveFun,
            "touchend" : endFun,
            "mouseup" : endFun
        });

        this.bodyDragMoveFun = this.bodyDragMove.bind(this);
        this.dragBody.addEvent("touchmove", this.bodyDragMoveFun);
        this.dragBody.addEvent("mousemove", this.bodyDragMoveFun);

        this.bodyDragEndFun = this.bodyDragEnd.bind(this);
        this.dragBody.addEvent("touchend", this.bodyDragEndFun);
        this.dragBody.addEvent("mouseup", this.bodyDragEndFun);

        // this.drag = this.node.makeDraggable({
        //     "container" : this.relativeNode,
        //     "handle": this.dragNode,
        //     "onStart": function(el, e){
        //         this.draging = true;
        //         this.fireEvent("dragStart");
        //     }.bind(this),
        //     "onComplete": function(e){
        //         this.draging = false;
        //         this.fireEvent("dragComplete");
        //     }.bind(this),
        //     "onDrag": function(el, e) {
        //         this.fireEvent("drag");
        //     }.bind(this)
        // });
    },
    bodyDragMove: function(ev){
        if(!this.lastPoint)return;
        if( this.dragMode ){
            var point = this.getLastPoint(ev);
            this.drag( point );
        }
    },
    bodyDragEnd: function(ev){
        this.lastPoint=null;
        if( this.dragMode ){
            this.docBody.setStyle("cursor", "default" );
            this.dragMode = false;
        }
    },
    drag : function(lastPoint){
        var x=lastPoint.x;
        var	y=lastPoint.y;

        var nodeSize = this.scaleSize( this.node.getSize() );
        var inputSize = this.scaleSize( this.editarea.getSize() );

        // var	top=coordinates.top,
        //     left=coordinates.left,

        var lft,
            tp,
            size = {
                x: Math.max( nodeSize.x, inputSize.x ),
                y: Math.max( nodeSize.y, inputSize.y )
            };

        if( x < this.relativeCoordinates.left ){
            lft = 0;
        }else if( x + size.x > this.relativeCoordinates.right ){
            lft = this.relativeCoordinates.width - size.x;
        }else{
            lft = x - this.relativeCoordinates.left;
        }

        if( y < this.relativeCoordinates.top ){
            tp = 0;
        }else if( y + size.y  > this.relativeCoordinates.bottom){
            tp = this.relativeCoordinates.height - size.y
        }else{
            tp = y - this.relativeCoordinates.top;
        }

        this.node.setStyles({
            "top":tp+'px',
            "left":lft+'px'
        });
    },


    loadResizeNode: function(){
        this.resizeNode = new Element("div.resizeNode",{
            styles :  this.css[ layout.mobile? "inputResizeNode_mobile" : "inputResizeNode"]
        }).inject(this.editareaWrap);
        if( layout.mobile ){
            var width = (24/this.options.scale);
            this.resizeNode.setStyles({
                "width" : width+"px",
                "height" : width +"px",
                "bottom": "-"+(width/2)+"px",
                "right": "-"+(width/2)+"px",
                "background-size": (16/this.options.scale)+"px " + (16/this.options.scale)+"px"
            });
        }

        this.docBody = this.relativeNode; //window.document.body;

        var startFun = function(ev){
            if( !this.options.isEditing )return;
            // this.drag.detach();
            this.dragNode.setStyle("cursor", "nw-resize" );
            this.docBody.setStyle("cursor", "nw-resize" );
            this.relativeCoordinates = this.scaleSize( this.relativeNode.getCoordinates() );
            this.resizeMode = true;
            this.fireEvent("resizeStart");
            ev.stopPropagation();
        }.bind(this);
        var moveFun = function(ev){
            if( !this.resizeMode )return;
            var point = this.getLastPoint(ev);
            this.resize( point );
            this.fireEvent("resizeMove");
            ev.stopPropagation();
        }.bind(this);
        var endFun = function(ev){
            // this.drag.attach();
            this.dragNode.setStyle("cursor", "move" );
            this.docBody.setStyle("cursor", "default" );
            this.resizeMode = false;
            this.lastPoint=null;
            this.fireEvent("resizeEnd");
            ev.stopPropagation();
        }.bind(this);

        this.resizeNode.addEvents({
            "touchstart" : startFun,
            "mousedown" : startFun,
            "touchmove" : moveFun,
            "mousemove" : moveFun,
            "touchend" : endFun,
            "mouseup" : endFun
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
            var point = this.getLastPoint(ev);
            this.resize( point );
        }
    },
    bodyMouseEnd: function(ev){
        this.lastPoint=null;
        if( this.resizeMode ){
            // this.drag.attach();
            this.dragNode.setStyle("cursor", "move" );
            this.docBody.setStyle("cursor", "default" );
            this.resizeMode = false;
        }
    },
    resize : function(lastPoint){
        var x=lastPoint.x;
        if( x == 0 )return;

        var	y=lastPoint.y;
        if( y == 0 )return;

        var coordinates = this.scaleSize( this.node.getCoordinates() );
        var inputSize = this.scaleSize( this.editarea.getSize()  );

        var	top=coordinates.top,
            left=coordinates.left,
            w,
            h;

        if( x > this.relativeCoordinates.right ){
            return;
        }else{
            w = x - left;
        }
        if( y  > this.relativeCoordinates.bottom){
            return;
        }else{
            h = y - top;
        }
        if( inputSize.y > h ){
            h = inputSize.y;
        }

        var minWidth = this.scaleSize( this.options.minWidth );
        var minHeight = this.scaleSize( this.options.minHeight );
        w=w< minWidth ? minWidth:w;
        h=h< minHeight ? minHeight:h;

        this.node.setStyles({
            "width":w+'px',
            "min-height":h+'px'
        });
    },
    getLastPoint: function(event){
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
        this.lastPoint= this.scaleSize({
            x:x,
            y:y
        });
        return this.lastPoint;
    },
    checkPosition: function(){
        var coordinates = this.scaleSize( this.editarea.getCoordinates( this.relativeNode ));
        var containerSize = this.scaleSize( this.relativeNode.getSize() );
        if( coordinates.height > containerSize.y ){
            this.node.setStyle("top", "0px");
        }else if( coordinates.bottom > containerSize.y ){
            this.node.setStyle("top", containerSize.y - coordinates.height );
        }
        // if( this.isChecking )return;
        // this.isChecking = true;
        // window.setTimeout(function () {
        //     var coordinates = this.editarea.getCoordinates( this.canvasWrap );
        //     console.log(coordinates);
        //     this.isChecking = false;
        // }.bind(this), 100)
    },
    setColor: function( color ){
        this.color = color;
        this.editarea.setStyle("color", color);
    },
    setFontFamily: function( fontFamily ){
        this.fontFamily = fontFamily;
        this.editarea.setStyle("font-family", fontFamily);
    },
    setFontSize: function( fontSize ){
        this.fontSize = fontSize;
        this.editarea.setStyle("font-size", fontSize);
    },
    getCoordinates: function(){
        return this.editarea.getCoordinates( this.relativeNode );
    },
    getDrawImageCoordinates : function(){
        var size = this.scaleSize(this.editarea.getSize());
        var coordinates = this.scaleSize(this.node.getCoordinates( this.relativeNode ));
        coordinates.width  = Math.max(coordinates.width, size.x);
        coordinates.height  = Math.max(coordinates.height, size.y);
        coordinates.top = coordinates.top - 7; //后台服务的偏差
        coordinates.left = coordinates.left - 7; //后台服务的偏差
        return coordinates;
    },
    ok : function(){
        this.fireEvent("postOk")
    },
    close : function( flag ){
        if(!flag)this.tablet.eraseInput(this);

        this.docBody.removeEvent("touchmove",this.bodyMouseMoveFun);
        this.docBody.removeEvent("mousemove",this.bodyMouseMoveFun);
        this.docBody.removeEvent("touchend",this.bodyMouseEndFun);
        this.docBody.removeEvent("mouseup",this.bodyMouseEndFun);

        //this.backgroundNode.destroy();
        this.node.destroy();

        delete this;
    },
    getData: function () {
        // var coordinates;
        // if( this.node.getBoundingClientRect ){
        //     var size = this.node.getSize();
        //     var rectOut = this.relativeNode.getBoundingClientRect();
        //     var rectIn = this.node.getBoundingClientRect();
        //     coordinates = {
        //         "top": rectIn.top - rectOut.top,
        //         "left": rectIn.left - rectOut.left,
        //         "height": size.y,
        //         "width": size.x
        // }
        // }else{
        //     coordinates = this.node.getCoordinates( this.relativeNode );
        // }
        return {
            coordinates : this.scaleSize( this.node.getCoordinates( this.relativeNode ) ), //coordinates
            styles: {
                "color": this.color,
                "font-family": this.fontFamily,
                "font-size": this.fontSize
            },
            html: this.editarea.get("html")
        }
    },
    show: function () {
        this.node.show();
    },
    hide: function () {
        this.node.hide();
    },
    scaleSize: function (size) {
        var s;
        if( this.options.scale !== 1 ){
            var t = o2.typeOf( size );
            if( t === "number" ){
                s = size/this.options.scale;
            }else if( t === "object" ){
                s = {};
                for( var k in size ){
                    s[k] = size[k]/this.options.scale;
                }
            }
        }
        return s || size;
    }
})

o2.widget.Tablet.FontFamily = new Class({
    Extends: MSelector,
    options : {
        "containerIsTarget": false,
        "style": "minderFont",
        "width": "120px",
        "height": "28px",
        "defaultOptionLp" : "字体",
        "textField" : "name",
        "valueField" : "val",
        "event" : "mouseenter",
        "isSetSelectedValue" : true,
        "isChangeOptionStyle" : true,
        "emptyOptionEnable" : false,
        "tooltipsOptions": {
            "displayDelay" : 300,
            "event": "mouseenter" //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        }
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var fontFamilyList = [{
            name: '宋体',
            val: '宋体,SimSun'
        }, {
            name: '微软雅黑',
            val: '微软雅黑,Microsoft YaHei'
        }, {
            name: '楷体',
            val: '楷体,楷体_GB2312,SimKai'
        }, {
            name: '黑体',
            val: '黑体, SimHei'
        }, {
            name: '隶书',
            val: '隶书, SimLi'
        }, {
            name: 'Andale Mono',
            val: 'andale mono'
        }, {
            name: 'Arial',
            val: 'arial,helvetica,sans-serif'
        }, {
            name: 'arialBlack',
            val: 'arial black,avant garde'
        }, {
            name: 'Comic Sans Ms',
            val: 'comic sans ms'
        }, {
            name: 'Impact',
            val: 'impact,chicago'
        }, {
            name: 'TimesNewRoman',
            val: 'times new roman'
        }, {
            name: 'Sans-Serif',
            val: 'sans-serif'
        }];
        if(callback)callback( fontFamilyList );
    },
    _postCreateItem: function( itemNode, data ){
        itemNode.setStyles( {
            "font-family": data.val,
            "font-size" : "13px",
            "min-height" : "30px",
            "line-height" : "30px"
        } );
    }
});

o2.widget.Tablet.FontSize = new Class({
    Extends: MSelector,
    options : {
        "containerIsTarget": false,
        "style": "minderFont",
        "width": "60px",
        "height": "28px",
        "defaultOptionLp" : "16",
        "defaultVaue": "16",
        "isSetSelectedValue" : true,
        "isChangeOptionStyle" : true,
        "emptyOptionEnable" : false,
        "event" : "mouseenter",
        "tooltipsOptions": {
            "displayDelay" : 300,
            "event": "mouseenter" //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        }
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var fontSizeList = ["10", "12", "14", "16", "18", "24", "32", "48"];
        if(callback)callback( fontSizeList );
    },
    _postCreateItem: function( itemNode, data ){
        itemNode.setStyles( {
            "font-size" :  "13px" //data.value +"px"
            // "min-height" : ( parseInt(data.value) + 6) +"px",
            // "line-height" : ( parseInt(data.value) + 6) +"px"
        } );
    }
});
