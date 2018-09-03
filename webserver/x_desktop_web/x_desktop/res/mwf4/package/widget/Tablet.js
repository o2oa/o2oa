MWF.widget = MWF.widget || {};

MWF.widget.Tablet = MWF.Tablet = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "path": MWF.defaultPath+"/widget/$Tablet/",

        "action" : null, //上传服务，可选，如果不设置，使用公共图片服务
        "method": "", //使用action 的方法
        "parameter": {}, //action 时的url参数

        "data": null, //formdata 的data， H5有效
        "reference": "",  //使用公共图片服务上传时的参数
        "referenceType" : "", //使用公共图片服务上传时的参数, 目前支持 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument
        "description" : "",

        "resultMaxSize" : 0,

        "size" : 340
    },
    initialize: function(node, options){
        this.node = node;

        this.reset();

        this.setOptions(options);

        this.path = this.options.path || (MWF.defaultPath+"/widget/$Tablet/");
        this.cssPath = this.path + this.options.style+"/css.wcss";

        this._loadCss();
        this.fireEvent("init");
    },
    load: function(  ){
        this.container = new Element("div.container", {
            styles :  this.css.container
        }).inject(this.node);
        this.container.setStyles({
            width : this.options.size + "px",
            height : this.options.size + "px"
        });

        this.container.addEvent("selectstart", function(e){
            e.preventDefault();
            e.stopPropagation();
        });

        this.imageNode = new Element("img",{
            width : this.options.size,
            height : this.options.size
        }).inject(this.container);
        this.imageNode.setStyles({
            "display" : "none"
        });
        this.imageNode.ondragstart = function(){
            return false;
        };

        if( this.checkBroswer() ){
            this._load();
        }

    },
    _load : function( ){
        this.canvas = new Element("canvas", {
            width : this.options.size,
            height : this.options.size
        }).inject( this.container );
        this.ctx = this.canvas.getContext("2d");

        this.canvas.onmousedown = function(ev){
            var ev = ev || event;
            var ctx = this.ctx;
            var canvas = this.canvas;
            var container = this.container;
            var position = this.container.getPosition();
            var doc = $(document);
            //ctx.strokeStyle="#0000ff" 线条颜色; 默认 #000000
            //ctx.lineWidth=10; 默认1 像素

            ctx.beginPath();
            ctx.moveTo(ev.clientX-position.x,ev.clientY-position.y);
            var mousemove = function(ev){
                ctx.lineTo(ev.client.x - position.x,ev.client.y - position.y);
                ctx.stroke();
            };
            doc.addEvent( "mousemove", mousemove );
            var mouseup = function(ev){
                //document.onmousemove = document.onmouseup = null;
                doc.removeEvent("mousemove", mousemove);
                doc.removeEvent("mouseup", mouseup);
                ctx.closePath();
            };
            doc.addEvent("mouseup", mouseup);
            //document.onmouseup = function(ev){
            //    document.onmousemove = document.onmouseup = null;
            //    ctx.closePath();
            //}
        }.bind(this)
    },
    reset : function(){
        this.fileName = "untitled.png";
        this.fileType = "image/png";
        if( this.ctx ){
            var canvas = this.canvas;
            this.ctx.clearRect(0,0,canvas.clientWidth,canvas.clientHeight);
        }
    },
    uploadImage: function(  success, failure  ){
        var image = this.getImage();
        if( image ){
            if( this.options.action ){
                this.action = (typeOf(this.options.action)=="string") ? MWF.Actions.get(action).action : this.options.action;
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
            }else{
                //公共图片上传服务
                var maxSize = this.options.resultMaxSize;
                MWF.xDesktop.uploadImageByScale(
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
    getImage : function(){
        var src = this.getBase64Code();
        src=window.atob(src);

        var ia = new Uint8Array(src.length);
        for (var i = 0; i < src.length; i++) {
            ia[i] = src.charCodeAt(i);
        }

        return new Blob([ia], {type: this.fileType });
    },
    getBase64Code : function(){
        var ctx = this.ctx;
        var canvas = this.canvas;
        var container = this.container;
        var size = this.options.size;

        ctx.drawImage(this.imageNode,0,0,size,size,0,0,size,size);
        var src=canvas.toDataURL( this.fileType );
        src=src.split(',')[1];

        if(!src){
            return "";
        }else{
            return src
        }
    },
    getBase64Image: function(){
        var base64Code = this.getBase64Code();
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
    }
});
