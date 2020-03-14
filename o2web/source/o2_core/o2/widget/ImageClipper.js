o2.widget = o2.widget || {};

o2.widget.ImageClipper = o2.ImageClipper = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$ImageClipper/",
		"imageUrl" : "",

		"action" : null, //上传服务，可选，如果不设置，使用公共图片服务
		"method": "", //使用action 的方法
		"parameter": {}, //action 时的url参数

		"data": null, //formdata 的data， H5有效
		"reference": "",  //使用公共图片服务上传时的参数
		"referenceType" : "", //使用公共图片服务上传时的参数, 目前支持 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument
		"description" : "",

		"aspectRatio" : 1, //生成图片的宽高比, 0 表示不限制
		"resultMaxSize" : 800, //生成图片的最大宽或高

		"editorSize" : 340, //H5有效,图形容器
		"frameMinSize" : 30, //H5有效, 选择框的最小宽度
		"previewerSize" : 260, //H5有效, 预览区域大小

		"showPreviewer" : true,	//H5有效
		"fromLocalEnable" : true,  //H5有效，本地图片
		"fromFileEnable" : true, //H5有效，云文件图片
		"resetEnable" : false, //H5有效
		"uploadSourceEnable" : true //H5有效
	},
	initialize: function(node, options){
		this.node = node;
		if( isNaN( options.aspectRatio ) )options.aspectRatio = 0;
		if( isNaN( options.resultMaxSize ) )options.resultMaxSize = 800;
		this.setOptions(options);

		this.path = this.options.path || (o2.session.path+"/widget/$ImageClipper/");
		this.cssPath = this.path + this.options.style+"/css.wcss";

		this._loadCss();
		this.fireEvent("init");
	},
	load: function( imageBase64 ){
		this.container = new Element("div.container", { styles :  this.css.container}).inject(this.node);

		this.container.addEvent("selectstart", function(e){
			e.preventDefault();
			e.stopPropagation();
		});

		if( this.checkBroswer() ){
			//this._loadWithSWF();
			this._loadWithH5( imageBase64 );
		}else{
			this._loadWithSWF();
		}

	},
	_loadWithSWF : function(){
		this.clipper = new o2.widget.FlashImageClipper( this.container, this.options, this );
		this.clipper.load()
	},
	_loadWithH5 : function( imageBase64 ){
		this.clipper = new o2.widget.HTML5ImageClipper( this.container, this.options, this );
		this.clipper.load( imageBase64 )
	},
	uploadImage: function(  success, failure  ){
		this.clipper.uploadImage( success, failure )
	},
	getFormData : function(){
		return this.clipper.getFormData()
	},
	getResizedImage : function(){
		return this.clipper.getResizedImage();
	},
	getBase64Code : function(){
		return this.clipper.getBase64Code();
	},
	getBase64Image: function(){
		return this.clipper.getBase64Image();
	},
	close : function(){
		this.clipper.close();
	},
	checkBroswer : function(){
		if( window.Uint8Array && window.HTMLCanvasElement && window.atob && window.Blob){
			this.available = true;
			return true;
		}else{
			this.available = false;
			//this.container.set("html", "<p>您的浏览器不支持以下特性:</p><ul><li>canvas</li><li>Blob</li><li>Uint8Array</li><li>FormData</li><li>atob</li></ul>");
			return false;
		}
	}
});

o2.widget.FlashImageClipper = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$ImageClipper/",
		"imageUrl" : "",

		"action" : null, //可选，如果不设置，使用公共图片服务
		"method": "", //使用action 的方法
		"parameter": {}, //action 时的url参数

		"reference": "",  //使用公共图片服务上传时的参数
		"referenceType" : "", //使用公共图片服务上传时的参数, 目前支持 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument
		"description" : "",

		"aspectRatio" : 1, //生成图片的宽高比, 0 表示不限制
		"resultMaxSize" : 800 //生成图片的最大宽或高
	},
	initialize: function(node, options, parent){
		this.container = node;
		this.setOptions(options);
		this.parent = parent;

		if( !this.options.aspectRatio ){
			this.options.aspectRatio = 1;
		}
		//this.path = this.options.path || (o2.session.path+"/widget/$ImageClipper/");
		//this.cssPath = this.path + this.options.style+"/css.wcss";

		this.Previer_MaxSize = 240;

		this.css = this.parent.css;
		this.fireEvent("init");
	},
	load : function(){
		window.uploadevent_flash = function( value ){
			if( typeOf( value ) == "number" ){
				value += '';
				switch(value){
					case '1':
						alert("上传成功！");
						var time = new Date().getTime();
						ok();
						break;

					case '2':
						if( this.isUploading ){
							return 0;
						}else{
							this.isUploading = true;
							return 1;
						}
						//if(confirm('js call upload')){
						//	return 1;
						//}else{
						//	return 0;
						//}
						break;

					case '-1':
						this.isUploading = false;
						//alert("您已取消!");
						//cancel();

						break;
					case '-2':
						this.isUploading = false;
						alert("上传失败，请重新上传!");
						window.location.href = "#";
						break;

					default:
					//alert(typeof(status) + ' ' + status);
				}
			}else{
				this.isUploading = false;
				var json = JSON.parse( value );
				if( json.type == "error" ){
					alert( json.message );
				}else{
					if(this.uploadSuccess)this.uploadSuccess( json.data );
				}
			}
		}.bind(this);

		this.getUploadUrl( function(){ 
			this.renderFlash()
		}.bind(this));
	},
	renderFlash : function(){
		this.contentNode = new Element("div.contentNode", {
			styles :  this.css.contentNode,
			id : "imageclipper_swf"
		}).inject(this.container);
		var path = o2.session.path+"/widget/$ImageClipper/";
		var imageUrl = this.options.imageUrl || path + "flash_default.png";
		if( this.options.aspectRatio > 1 ){
			var previewerWidth = this.Previer_MaxSize;
			var previewerHeight = this.Previer_MaxSize / this.options.aspectRatio;
		}else{
			var previewerWidth = this.Previer_MaxSize * this.options.aspectRatio;
			var previewerHeight = this.Previer_MaxSize;
		}
		COMMON.AjaxModule.load( path +"swfobject.js", function () {
			swfobject.embedSWF( path+"FaustCplus.swf", this.contentNode, "650", "370", "9.0.0", path+"expressInstall.swf", {
				"jsfunc":"uploadevent_flash",
				"imgUrl": imageUrl,
				"tip" : this.options.description,
				"aspectRatio" : this.options.aspectRatio,
				"reqContentDisposition" : "", //"" 或者 formData
				"imageUploadKey" : "thumb.jpg", //reqContentDisposition 为 formdata 时候有效，文件名称
				"imageSrcUploadKey" : "src.jpg", //reqContentDisposition 为 formdata 时候有效，文件名称
				"showZoom" : false,
				"showSaveBtns" : false,
				"showTurn" : false,
				"showColorAdjuster" : false,
				"pid":"75642723",
				"uploadSrc":false,
				"showBrow":true,
				"showCame":false,
				"pSize":"300|300|"+previewerWidth+"|"+previewerHeight, //|80|40|40|20"//,
				"uploadUrl": this.uploadUrl  //+encodeURI("<计算的值>")+"?shortname=<计算的值>?filepath=<计算的值>?sizes=160|80|40?imgnames=face.jpg|face_medium.jpg|face_small.jpg"	//sizes 几张图片的比例
			}, {
				menu: "false",
				scale: "noScale",
				allowFullscreen: "true",
				allowScriptAccess: "always",
				wmode:"transparent",
				bgcolor: "#FFFFFF"
			}, {
				id:"FaustCplus"
			}, function( swf ){
				this.swf = swf;
			}.bind(this));
		}.bind(this))
	},
	getUploadUrl : function( callback ){
		if( this.options.action ){
			this.action = (typeOf(this.options.action)=="string") ? o2.Actions.get(action).action : this.options.action;
			this.action.getActions(function(){
				var url = this.action.actions[this.options.method];
				url = this.action.address+url.uri;
				if(this.options.parameter){
					Object.each(this.options.parameter, function(v, k){
						url = url.replace("{"+k+"}", v);
					});
				}
				this.uploadUrl = url;
				if(callback)callback(url);
			}.bind(this));
		}else{
			//公共图片服务
			var addressObj = layout.serviceAddressList["x_file_assemble_control"];
			if (addressObj){
				var address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
			}else{
				var host = layout.config.center.host || window.location.hostname;
				var port = layout.config.center.port;
				var address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center";
			}
			var url = "/jaxrs/file/upload/referencetype/"+ this.options.referenceType + "/reference/" + this.options.reference + "/scale/" + this.options.resultMaxSize;
			this.uploadUrl = address+url;
			if(callback)callback(this.uploadUrl);
		}
	},
	uploadImage: function(  success, failure  ){
		this.uploadSuccess = success;
		if( this.swf ){
			this.swf.ref["jscall_updateAvatar"](); //JsCallAs是flash里addCallback的函数
		}
	},
	getFormData : function(){
		return true;
	},
	getResizedImage : function(){
		return true;
	},
	getBase64Code : function(){
		return true;
	},
	getBase64Image: function(){
		return true;
	},
	close : function(){

	}
});

o2.widget.HTML5ImageClipper = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$ImageClipper/",
		"imageUrl" : "",

		"editorSize" : 340, //图形容器
		"aspectRatio" : 1, //生成图片的宽高比, 0 表示不限制
		"frameMinSize" : 30, //选择框的最小宽度
		"previewerSize" : 260, //预览区域大小
		"resultMaxSize" : 800, //生成图片的最大宽或高

		"action" : null, //可选，如果不设置，使用公共图片服务
		"method": "", //使用action 的方法
		"parameter": {}, //action 时的url参数

		"data": null, //formdata 的data
		"reference": "",  //使用公共图片服务上传时的参数
		"referenceType" : "", //使用公共图片服务上传时的参数, 目前支持 processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument

		"uploadSourceEnable" : true, //是否允许上传原图

		"showPreviewer" : true,
		"fromLocalEnable" : true,  //本地图片
		"fromFileEnable" : true, //云文件图片
		"resetEnable" : false,
		"description" : ""
	},
	initialize: function(node, options, parent){
		this.container = node;
		this.setOptions(options);
		this.parent = parent;

		//this.path = this.options.path || (o2.session.path+"/widget/$ImageClipper/");
		//this.cssPath = this.path + this.options.style+"/css.wcss";

		this.fileName = "untitled.png";
		this.fileType = "image/png";
		this.fileSize = null;

		this.css = this.parent.css;
		this.fireEvent("init");
	},
	load : function( imageBase64 ){
		this.lastPoint=null;

		this.loadToolBar();

		this.contentNode = new Element("div.contentNode", { styles :  this.css.contentNode}).inject(this.container);
		this.loadEditorNode();
		this.loadResultNode();

		if( this.options.description ){
			this.loadDescriptionNode();
		}

		if( this.options.imageUrl ){
			this.loadImageAsUrl( this.options.imageUrl );
		}
		if( imageBase64 ){
			this.loadImageAsFile( this.base64ToBlob( imageBase64 ) );
		}
	},
	uploadImage: function(  success, failure  ){
		if( this.resizedImage ){
			if( this.options.action ){
				this.action = (typeOf(this.options.action)=="string") ? o2.Actions.get(action).action : this.options.action;
				this.action.invoke({
					"name": this.options.method,
					"async": true,
					"data": this.getFormData(),
					"file": this.resizedImage,
					"parameter": this.options.parameter,
					"success": function(json){
						success(json)
					}.bind(this)
				});
			}else{
				//公共图片上传服务
				var maxSize = this.options.resultMaxSize;
				if( this.uploadSourceInput && this.uploadSourceInput.get("checked") ){
					maxSize = 0; //上传原图
				}
				o2.xDesktop.uploadImageByScale(
					this.options.reference,
					this.options.referenceType,
					maxSize,
					this.getFormData(),
					this.resizedImage,
					success,
					failure
				);
			}
		}else{
		}
	},
	getFormData : function(){
		var formData = new FormData();
		formData.append('file',this.resizedImage, this.fileName );
		if( this.options.data ){
			Object.each(this.options.data, function(v, k){
				formData.append(k, v)
			});
		}
		return formData;
	},
	getResizedImage : function(){
		return this.resizedImage;
	},
	getBase64Code : function(){
		return this.base64Code;
	},
	getBase64Image: function(){
		if( !this.base64Code )return null;
		return 'data:'+ this.fileType +';base64,' + this.base64Code;
	},
	checkBroswer : function(){
		if( window.Uint8Array && window.HTMLCanvasElement && window.atob && window.Blob){
			this.available = true;
			return true;
		}else{
			this.available = false;
			//this.container.set("html", "<p>您的浏览器不支持以下特性:</p><ul><li>canvas</li><li>Blob</li><li>Uint8Array</li><li>FormData</li><li>atob</li></ul>");
			return false;
		}
	},
	close : function(){
		this.docBody.removeEvent("touchmove",this.bodyMouseMoveFun);
		this.docBody.removeEvent("mousemove",this.bodyMouseMoveFun);
		this.docBody.removeEvent("touchend",this.bodyMouseEndFun);
		this.docBody.removeEvent("mouseup",this.bodyMouseEndFun);

		this.container.destroy();
		delete this;
	},
	loadToolBar: function(){
		this.uploadToolbar = new Element("div.uploadToolbar", {
			"styles" : this.css.uploadToolbar
		}).inject(this.container);
		//var width = this.options.editorSize;
		//this.uploadToolbar.setStyle( "width" ,  width+ "px");

		if( this.options.fromLocalEnable ){
			this.uploadLocalImage = new Element("button.uploadActionNode",{
				"styles" : this.css.uploadActionNode,
				"text" : "选择本地图片"
			}).inject(this.uploadToolbar);
			this.uploadLocalImage.addEvents({
				"click": function(){ this.fileNode.click(); }.bind(this)
			});


			this.fileNode = new Element("input.file", {
				"type" : "file",
				"accept":"images/*",
				"styles" : {"display":"none"}
			}).inject(this.container);
			this.fileNode.addEvent("change", function(event){
				var file=this.fileNode.files[0];
				this.fileType = file.type;
				this.fileName = file.name;
				this.fileSize = file.size;
				this.loadImageAsFile( file );
			}.bind(this));
		}

		this._createUploadButtom();

		this.uploadCloudFileArea = new Element("span").inject( this.uploadToolbar );
		if( this.options.fromFileEnable ){
			var url = o2.session.path+"/xDesktop/$Layout/applications.json";
			o2.getJSON(url, function(json){
				json.each( function(obj){
					if( obj.name == "File" || obj.path == "File" ){
						this.uploadCloudFile = new Element("button.uploadActionNode",{
							"styles" : this.css.uploadActionNode,
							"text" : "选择云文件图片"
						}).inject(this.uploadCloudFileArea );
						this.uploadCloudFile.addEvents({
							"click": function(){ this.selectFileImage(
								function( url, id ,attachmentInfo ){
									this.fileName = attachmentInfo.name;
									this.fileType = ["jpeg","jpg"].contains( attachmentInfo.extension.toLowerCase() ) ? "image/jpeg" : "image/png" ;
									this.fileSize = attachmentInfo.length;
									this.loadImageAsUrl( url );
								}.bind(this)
							); }.bind(this)
						});
					}
				}.bind(this));
			}.bind(this));
		}

		if( this.options.resetEnable ){
			this.resetAction = new Element("button.resetAction",{
				"styles" : this.css.resetActionNode,
				"text" : "重置"
			}).inject(this.uploadToolbar);
			this.resetAction.addEvents({
				"click": function(){ this.reset(); }.bind(this)
			});
		}

		if( this.options.uploadSourceEnable ){
			this.uploadSourceInput = new Element( "input", {
				type : "checkbox",
				events : {
					change : function(){
						this.clipImage();
					}.bind(this)
				}
			}).inject( this.uploadToolbar );
			new Element("span",{
				text : "上传原图"
			}).inject( this.uploadToolbar );
		}
	},
	_createUploadButtom : function(){

	},
	reset: function(){
		this.fileName = "untitled.png";
		this.fileType = "image/png";
		this.fileSize = null;
		this.resizedImage = "";
		this.base64Code = "";
		this.resetImage();
		this.setFrameSize({width:0, height:0});
		this.frameOffset.top = 0;
		this.frameOffset.left = 0;
		this.frameNode.setStyles({
			top:0,
			left:0
		});
		this.resultNode.empty();
		this.editorContainer.setStyles( this.css.editorContainer );
		this.imageNode.setStyle("display","none");
		this.innerNode.setStyles({
			"width" : 0,
			"height" : 0
		})
	},
	selectFileImage : function( callback ){
		var _self = this;
		o2.xDesktop.requireApp("File", "FileSelector", function(){
			_self.selector_cloud = new o2.xApplication.File.FileSelector( document.body ,{
				"style" : "default",
				"title": "选择云文件图片",
				"copyToPublic" : false,
				//"reference" :  _self.options.reference,
				//"referenceType" : _self.options.referenceType,
				"listStyle": "preview",
				"selectType" : "images",
				"onPostSelectAttachment" : function( url, id, attachmentInfor ){
					if(callback)callback(url, id, attachmentInfor );
				}
			});
			_self.selector_cloud.load();
		}, true);
	},
	loadResultNode: function(){
		if( this.options.showPreviewer ){
			this.resultContainer = new Element("div", {"styles":this.css.resultContainer}).inject(this.contentNode);
			var containerHeight = Math.max( this.options.editorSize ,this.options.previewerSize ) - (parseInt(this.resultContainer.getStyle("padding-left"))*2);
			this.resultContainer.setStyles( {
				"width": this.options.previewerSize+"px",
				"height": containerHeight +"px"
			} );

			this.resultTitleNode = new Element("div", {
					"styles":this.css.resultTitleNode,
					"text" : "预览"
				}
			).inject(this.resultContainer);

			var titleHeight = this.resultTitleNode.getSize().y;
			var nodeHeight = this.options.aspectRatio ? ( this.options.previewerSize / this.options.aspectRatio) : this.options.previewerSize ;
			this.resultNode = new Element("div.resultNode", {
				"styles":this.css.resultNode
			}).inject(this.resultContainer);
			this.resultNode.setStyles( {
				"padding-top": (containerHeight-titleHeight-nodeHeight)/2 - 10 +"px"
			} );
		}else{
			this.resultNode = new Element("div", {
				styles : {display : "none"}
			}).inject(this.contentNode);
		}
	},
	loadEditorNode: function(){
		this.docBody = window.document.body;

		this.editorContainer = new Element("div.editorContainer", { styles :  this.css.editorContainer}).inject(this.contentNode);
		this.editorContainer.setStyles( {
			"width": this.options.editorSize+"px",
			"height": this.options.editorSize+"px"
		} );

		this.editorNode = new Element("div.editorNode", { styles :  this.css.editorNode}).inject(this.editorContainer);

		this.innerNode = new Element("div.innerNode",{ styles :  this.css.innerNode } ).inject(this.editorNode);

		this.imageNode = new Element("img",{
			styles :  this.css.imageNode
		}).inject(this.innerNode);
		this.imageNode.ondragstart = function(){
			return false;
		};

		this.frameNode = new Element("div.frameNode",{ styles :  this.css.frameNode }).inject(this.innerNode);
		this.frameOffset={ top:0, left:0 };
		this.frameNode.addEvents({
			"touchstart" : function(ev){ this.getOffset(ev) }.bind(this),
			"mousedown" : function(ev){ this.getOffset(ev) }.bind(this),
			"touchmove" : function(ev){
				if(!this.lastPoint)return;
				var offset= this.getOffset(ev);
				if( this.resizeMode ){
					this.resizeFrames( offset );
				}else{
					this.moveFrames( offset );
				}
				ev.stopPropagation();
			}.bind(this),
			"mousemove" : function(ev){
				if(!this.lastPoint)return;
				var offset= this.getOffset(ev);
				if( this.resizeMode ){
					this.resizeFrames( offset );
				}else{
					this.moveFrames( offset );
				}
				ev.stopPropagation();
			}.bind(this),
			"touchend" : function(ev){
				this.lastPoint=null;
				if( this.resizeMode ){
					this.frameNode.setStyle("cursor", "move" );
					this.docBody.setStyle("cursor", "default" );
					this.resizeMode = false;
				}
				this.clipImage();
				ev.stopPropagation();
			}.bind(this),
			"mouseup" : function(ev){
				this.lastPoint=null;
				if( this.resizeMode ){
					this.frameNode.setStyle("cursor", "move" );
					this.docBody.setStyle("cursor", "default" );
					this.resizeMode = false;
				}
				this.clipImage();
				ev.stopPropagation();
			}.bind(this)
		});

		this.reizeNode = new Element("div.reizeNode",{ styles :  this.css.reizeNode }).inject(this.frameNode);
		this.reizeNode.addEvents({
			"touchstart" : function(ev){
				this.frameNode.setStyle("cursor", "nw-resize" );
				this.docBody.setStyle("cursor", "nw-resize" );
				this.resizeMode = true;
				this.getOffset(ev);
				ev.stopPropagation();
			}.bind(this),
			"mousedown" : function(ev){
				this.frameNode.setStyle("cursor", "nw-resize" );
				this.docBody.setStyle("cursor", "nw-resize" );
				this.resizeMode = true;
				this.getOffset(ev);
				ev.stopPropagation();
			}.bind(this),
			"touchmove" : function(ev){
				if(!this.lastPoint)return;
				var offset= this.getOffset(ev);
				this.resizeFrames( offset );
				ev.stopPropagation();
			}.bind(this),
			"mousemove" : function(ev){
				if(!this.lastPoint)return;
				var offset= this.getOffset(ev);
				this.resizeFrames( offset );
				ev.stopPropagation();
			}.bind(this),
			"touchend" : function(ev){
				this.frameNode.setStyle("cursor", "move" );
				this.docBody.setStyle("cursor", "default" );
				this.resizeMode = false;
				this.lastPoint=null;
				this.clipImage();
				ev.stopPropagation();
			}.bind(this),
			"mouseup" : function(ev){
				this.frameNode.setStyle("cursor", "move" );
				this.docBody.setStyle("cursor", "default" );
				this.resizeMode = false;
				this.lastPoint=null;
				this.clipImage();
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
	loadDescriptionNode: function(){
		new Element("div",{
			"styles": this.css.descriptionNode,
			"text": this.options.description
		}).inject( this.container )
	},
	bodyMouseMove: function(ev){
		if(!this.lastPoint)return;
		if( this.resizeMode ){
			var offset= this.getOffset(ev);
			this.resizeFrames( offset );
		}
	},
	bodyMouseEnd: function(ev){
		this.lastPoint=null;
		if( this.resizeMode ){
			this.frameNode.setStyle("cursor", "move" );
			this.docBody.setStyle("cursor", "default" );
			this.resizeMode = false;
			this.clipImage();
		}
	},
	clipImage: function(){
		this.resultNode.empty();

		var nh=this.imageNode.naturalHeight,
			nw=this.imageNode.naturalWidth,
			max = (this.uploadSourceInput && this.uploadSourceInput.get("checked")) ? 0 : this.options.resultMaxSize,
			size,
			ratio;

		ratio = this.options.aspectRatio ? this.options.aspectRatio : (this.frameOffset.size.width / this.frameOffset.size.height );
		if( max == 0 || ( nh<=max && nw<=max )){
			size = this.getRatioMaxSize(nw, nh , ratio);
		}else{
			var min = Math.min(max, nh, nw);
			size = this.getRatioMaxSize(min, min, ratio);
		}

		var canvas = new Element("canvas", size);
		var	ctx=canvas.getContext('2d'),
			scale=nw/this.offset.width,
			x=this.frameOffset.left*scale,
			y=this.frameOffset.top*scale,
			w=this.frameOffset.size.width*scale,
			h=this.frameOffset.size.height*scale;

		ctx.drawImage(this.imageNode,x,y,w,h,0,0,size.width,size.height);
		var src=canvas.toDataURL( this.fileType );
		this.canvas=canvas;
		canvas.inject(this.resultNode);

		src=src.split(',')[1];
		if(!src){
			this.resizedImage = null;
			this.base64Code = "";
			return;
		}
		this.base64Code = src;
		src=window.atob(src);

		var ia = new Uint8Array(src.length);
		for (var i = 0; i < src.length; i++) {
			ia[i] = src.charCodeAt(i);
		}

		this.resizedImage = new Blob([ia], {type: this.fileType });

		var min = Math.min(this.options.previewerSize, nh, nw, this.options.resultMaxSize);
		size = this.getRatioMaxSize(min, min, ratio);
		canvas.setStyles({
			width : size.width + "px",
			height : size.height + "px"
		});
	},
	loadImageAsFile: function( file ){
		this.resetImage();
		this.editorContainer.setStyles( this.css.editorContainer_active );
		this.imageNode.setStyle("display","");

		this.setFrameSize({width:0, height:0});
		this.frameOffset.top = 0;
		this.frameOffset.left = 0;
		this.frameNode.setStyles({
			top:0,
			left:0
		});
		var reader=new FileReader();
		reader.onload=function(){
			this.imageNode.src=reader.result;
			reader = null;
			//this.setImageSize();
			//this.setFrameSize( this.getDefaultSize() );
			//this.clipImage();
			this.onImageLoad();
		}.bind(this);
		reader.readAsDataURL(file);
	},
	loadImageAsUrl: function( url ){
		this.resetImage();
		this.editorContainer.setStyles( this.css.editorContainer_active );
		this.imageNode.setStyle("display","");
		this.setFrameSize({width:0, height:0});
		this.frameOffset.top = 0;
		this.frameOffset.left = 0;
		this.frameNode.setStyles({
			top:0,
			left:0
		});
		this.onImageLoadFun = this.onImageLoad.bind(this);
		this.imageNode.addEvent( "load",this.onImageLoadFun );
		this.imageNode.src = url;
	},
	onImageLoad: function(){
		var nh=this.imageNode.naturalHeight,
			nw=this.imageNode.naturalWidth;
		if( isNaN(nh) || isNaN(nw) || nh == 0 || nw == 0 ){
			setTimeout( function(){
				this.onImageLoad();
			}.bind(this), 100 );
		}else{
			this._onImageLoad();
		}
	},
	_onImageLoad: function(){
		this.setImageSize();
		this.setFrameSize( this.getDefaultSize() );
		this.clipImage();
		if(this.onImageLoadFun){
			this.imageNode.removeEvent("load", this.onImageLoadFun);
			this.onImageLoadFun = null;
		}
	},
	resetImage: function(){
		this.imageNode.src='';
		if( this.canvas )this.canvas.destroy();
	},
	setImageSize: function(){
		var nh=this.imageNode.naturalHeight,
			nw=this.imageNode.naturalWidth,
			size;
		if( nw > nh ){
			size = {
				width : this.options.editorSize,
				height : this.options.editorSize * (nh / nw)
			}
		}else{
			size = {
				width : this.options.editorSize  * (nw / nh),
				height : this.options.editorSize
			}
		}
		//if( isNaN(size.width) || isNaN(size.height) ){
		//	debugger;
		//}
		this.offset = size;
		this.imageNode.setStyles( size );
	},
	setFrameSize: function(size){
		this.frameOffset.size=size;

		return this.frameNode.setStyles({
			width:size.width+'px',
			height:size.height+'px'
		});
	},
	getDefaultSize: function(){
		this.innerNode.setStyles({
			"width" : this.offset.width,
			"height" : this.offset.height,
			"margin-left" : (this.options.editorSize-this.offset.width)/2 +"px",
			"margin-top" : (this.options.editorSize-this.offset.height)/2 +"px"
		});
		if( this.options.aspectRatio ){
			return this.getRatioMaxSize(this.offset.width, this.offset.height);
		}else{
			//var min = Math.min(this.offset.width, this.offset.height);
			//return { width : min, height : min };
			return {
				width : this.offset.width,
				height : this.offset.height
			};
		}
	},
	getRatioMaxSize: function( width, height , radio ){
		if( !radio )radio = this.options.aspectRatio;
		var r = width / height;
		if( r > radio ){
			return {
				width : height * radio,
				height : height
			}
		}else{
			return {
				width : width,
				height : width / radio
			}
		}
	},
	resizeFrames: function( offset ){
		var x=offset.x;
		if( x == 0 )return;

		var	y=offset.y;
		if( y == 0 )return;

		var	top=this.frameOffset.top,
			left=this.frameOffset.left,
			size=this.frameOffset.size,
			width=this.offset.width,
			height=this.offset.height,
			ratio = this.options.aspectRatio,
			w,
			h;

		if( ratio ){
			if( Math.abs(x)/Math.abs(y) > ratio ){
				if( x+size.width+left>width ){
					return;
				}else{
					w = x + size.width;
					h = w / ratio;
					if( h+top > height ){
						return;
					}
				}
			}else{
				if(y+size.height+top>height){
					return;
				}else{
					h = y+ size.height;
					w = h * ratio;
				}
				if( w+left > width ){
					return;
				}
			}
		}else{
			if( x+size.width+left>width ){
				return;
			}else{
				w = x + size.width
			}
			if(y+size.height+top>height){
				return;
			}else{
				h = y+ size.height;
			}
		}

		var minWidth = this.options.frameMinSize;
		var minHeight = ratio ? minWidth / ratio : minWidth;
		w=w< minWidth ?minWidth:w;
		h=h< minHeight ? minHeight:h;

		this.frameNode.setStyles({
			width:w+'px',
			height:h+'px'
		});


		this.frameOffset.size={
			width : w,
			height : h
		}
	},
	moveFrames: function(offset){
		var x=offset.x,
			y=offset.y,
			top=this.frameOffset.top,
			left=this.frameOffset.left,
			size=this.frameOffset.size,
			width=this.offset.width,
			height=this.offset.height;

		if(x+size.width+left>width){
			x=width-size.width;
		}else{
			x=x+left;
		}

		if(y+size.height+top>height){
			y=height-size.height;
		}else{
			y=y+top;
		}
		x=x<0?0:x;
		y=y<0?0:y;
		this.frameNode.setStyles({
			top:y+'px',
			left:x+'px'
		});

		this.frameOffset.top=y;
		this.frameOffset.left=x;
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
	base64ToBlob : function( base64 ){
		if( base64.substr( 0, 10 ) == 'data:image' ){
			var bytes=window.atob(base64.split(',')[1]);        //去掉url的头，并转换为byte
		}else{
			var bytes=window.atob(base64)
		}

		//处理异常,将ascii码小于0的转换为大于0
		var ab = new ArrayBuffer(bytes.length);
		var ia = new Uint8Array(ab);
		for (var i = 0; i < bytes.length; i++) {
			ia[i] = bytes.charCodeAt(i);
		}

		return new Blob( [ab] , {type : this.fileType });
	}

});
