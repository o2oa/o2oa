MWF.xApplication.File = MWF.xApplication.File || {};
MWF.xDesktop.requireApp("File", "lp.zh-cn", null, false);
// MWF.xDesktop.requireApp("File", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("File", "AttachmentSelector", null, false);
MWF.xDesktop.requireApp("File", "Main", null, false);
MWF.require("MWF.widget.Tree", null, false);
MWF.xApplication.File.FileSelector = new Class({
	Extends: MWF.xApplication.File.Main,
	Implements: [Options, Events],

	options: {
		"title" : "",
		"style": "default",
		"listStyle" : "icon",
		"selectType" : "all",
		"copyToPublic" : true,
		"scale" : 800,
		"reference" : "",
		"referenceType" : "",
		//"toBase64" : false,
		//"base64Width" : 800,
		//"base64Height" : 0,
		"images": ["bmp", "gif", "png", "jpeg", "jpg", "jpe", "ico"],
		"audios": ["mp3", "wav", "wma", "wmv"],
		"videos": ["avi", "mkv", "mov", "ogg", "mp4", "mpa", "mpe", "mpeg", "mpg", "rmvb"]
	},
    initialize: function(container, options){
        this.setOptions(options);
		this.container = $(container);

        this.path = "../x_component_File/$FileSelector/";
        this.cssPath =this.path+this.options.style+"/css.wcss";
        this._loadCss();
		this.lp = MWF.xApplication.File.LP;
    },
	onQueryLoad: function(){
		this.lp = MWF.xApplication.File.LP;
	},
    load : function(){
        this.loadApplication();
    },
	loadApplication: function(callback){
		this.history = [];
		this.currentHistory = 1;
		this.currentFolder = null;
		
		//this.restActions = new MWF.xApplication.File.Actions.RestActions();

        this.restActions = MWF.Actions.get("x_file_assemble_control");
		MWF.getJSON("../x_component_File/$Main/icon.json", function(json){
			this.icons = json;
		}.bind(this), false, false);
		
		this.createNode();
		if (callback) callback();
	},
	createNode: function(){
		this.markNode = new Element("div", {
			"styles": this.css.markNode,
			"events": {
				"mouseover": function(e){e.stopPropagation();},
				"mouseout": function(e){e.stopPropagation();}
			}
		}).inject(this.container);

		this.content = new Element("div", {
			"styles": this.css.container
		});

		this.node = new Element("div",{
			"styles": this.css.node
		}).inject(this.content);

		this.createTopNode();
		this.loadApplicationContent();
		this.createBottomToolbarNode();

		this.content.inject(this.markNode, "after");
		this.content.fade("in");

		this.setNodeSize();

		var size = this.container.getSize();
		var nodeSize = this.content.getSize();
		this.content.makeDraggable({
			"handle": this.titleNode,
			"limit": {
				"x": [0, size.x-nodeSize.x],
				"y": [0, size.y-nodeSize.y]
			}
		});
	},
	close : function(){
		this.content.destroy();
		this.markNode.destroy();
		delete this;
	},
	createTopNode: function(){
		if (this.options.title){
			if (!this.titleNode){
				this.titleNode = new Element("div.titleNode", {"styles": this.css.titleNode, "text": this.options.title}).inject(this.node);

				this.titleActionNode = new Element("div", {"styles": this.css.titleActionNode}).inject(this.titleNode);
				this.titleActionNode.addEvent("click",function(){this.close() }.bind(this))
			}
		}
	},
	loadFileContentAreaNode: function(){
		this.controller = new MWF.xApplication.File.AttachmentSelector(this.attachmentContentNode, this, {
			"resize": false,
			"isSizeChange": false,
			"style": this.options.style,
			"listStyle": this.options.listStyle
		})
		this.controller.load();
	},
	createBottomToolbarNode : function(  ){
		this.bottomToolbarNode = new Element("div.bottomToolbarNode",{ styles : this.css.bottomToolbarNode }).inject(this.node);

		this.cancelButton = new Element("div",{
			styles : this.css.cancelButton,
			text : MWF.LP.widget.cancel
		}).inject(this.bottomToolbarNode);
		this.cancelButton.addEvent("click",function(){
			this.close();
		}.bind(this));

		this.okButton = new Element("div",{
			styles : this.css.okButton,
			text : MWF.LP.widget.ok
		}).inject(this.bottomToolbarNode);
		this.okButton.addEvent("click",function(){
			if( this.controller.selectedAttachments.length ){
				this.openAttachment(null, null, this.controller.selectedAttachments );
			}
			this.close();
		}.bind(this));
	},
	fiterByExtension : function( attachments ){
		var availableExtensions = this.options[ this.options.selectType ];
		if( availableExtensions ){
			var atts = [];
			while (attachments.length){
				var att = attachments.shift();
				if( availableExtensions.contains(att.extension) ){
					atts.push(att);
				}
			}
			attachments = atts;
		}
		return attachments;
	},
	loadShareFile: function(treeNode){
		var person = treeNode.data.name;
		this.restActions.listShareAttachment(function(json){
			//this.fileContentAreaNode.empty();
			this.controller.clear();
			this.fiterByExtension( json.data ).each(function(file){
				this.controller.addAttachment(file);
				//new MWF.xApplication.File.ShareAttachment(file, this);
			}.bind(this));
		}.bind(this), null, person);
	},
	loadEditorFile: function(treeNode){
		var person = treeNode.data.name;
		this.restActions.listEditorAttachment(function(json){
			//this.fileContentAreaNode.empty();
			this.controller.clear();
			this.fiterByExtension( json.data ).each(function(file){
				this.controller.addAttachment(file);
				//new MWF.xApplication.File.ShareAttachment(file, this);
			}.bind(this));
		}.bind(this), null, person);
	},
	loadSub: function(treeNode){
		this.setPathNode(treeNode);

		//this.fileContentAreaNode.empty();
		this.controller.clear();

		treeNode.children.each(function(node){
			var folder = this.controller.addAttachmentFolder(node.data);
			folder.treeNode = node;
			//var folder = new MWF.xApplication.File.Folder(node.data, this);
			//folder.treeNode = node;
		}.bind(this));

		this.currentFolder = treeNode;
		if (treeNode.data){
			this.restActions.listAttachment(function(json){
				this.fiterByExtension( json.data ).each(function(file){

					this.controller.addAttachment(file);

					//new MWF.xApplication.File.Attachment(file, this);
				}.bind(this));
			}.bind(this), null, treeNode.data.id);
		}else{
			this.restActions.listAttachmentTop(function(json){
				this.fiterByExtension( json.data ).each(function(file){
					this.controller.addAttachment(file);
					//new MWF.xApplication.File.Attachment(file, this);
				}.bind(this));
			}.bind(this));
		}
	},
    openAttachment: function(e, node, attachment){
		var id = attachment[0].data.id;
        this.restActions.getFileUrl( id, function(url){
            //if( this.options.toBase64 ){
			//	this.restActions.getBase64Code(function(json){
			//		var data = json.data ? "data:image/png;base64,"+json.data.value : null;
			//		this.fireEvent("postSelectAttachment",[url, data ]);
			//	}.bind(this), null, id, this.options.base64Width, this.options.base64Height )
			//}else{
			if( this.options.copyToPublic && this.options.reference && this.options.referenceType ) {
				MWF.xDesktop.copyImage( this.options.reference, this.options.referenceType, id, this.options.scale, function( json ){
					var url = MWF.xDesktop.getImageSrc( json.data.id );
					this.fireEvent("postSelectAttachment",[url, json.data.id, attachment[0].data ]);
				}.bind(this))
			}else{
				this.fireEvent("postSelectAttachment",[url, "", attachment[0].data ]);
			}
			this.close();
		}.bind(this));
    },
	setContentHeight : function(node){

	},
	setNodeSize: function(node,width, height){

		width = width || "50%";
		height = height || "50%";
		"string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(window.screen.width * parseInt(width, 10) / 100, 10));
		"string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(window.screen.height * parseInt(height, 10) / 100, 10));
		700 > width && (width = 700);
		420 > height && (height = 420);
		var top = parseInt((window.screen.height - height) / 2, 10);
		var left = parseInt((window.screen.width - width) / 2, 10);
		//c = window.open("", null, d, !0);
		this.content.setStyles({
			"width": "" + width + "px",
			"height": "" + height + "px",
			"top": "" + top + "px",
			"left": "" + left + "px"
		});
		this.node.setStyles({
			"width": "" + width + "px",
			"height": "" + height + "px"
		});
		var titleNodeHeight = this.titleNode ? this.titleNode.getSize().y : 0;
		var topNodeHeight = this.topNode ? this.topNode.getSize().y : 0;
		var bottomToolbarNodeHeight = this.bottomToolbarNode ? this.bottomToolbarNode.getSize().y : 0;

		var mtt = this.topNode.getStyle("margin-top").toFloat();
		var mbt = this.topNode.getStyle("margin-bottom").toFloat();

		var mtc = this.fileContentNode.getStyle("margin-top").toFloat();
		var mbc = this.fileContentNode.getStyle("margin-bottom").toFloat();

		var h = height - titleNodeHeight - topNodeHeight - bottomToolbarNodeHeight-mtt-mbt-mtc-mbc;
		this.fileContentNode.setStyle("height", h);
		this.attachmentContentNode.setStyle("height", h);

		var attTopSzie = this.controller.topNode.getSize();
		var y = h-attTopSzie.y;
		this.controller.contentScrollNode.setStyle("height", ""+y+"px");

		var tabSize = this.treeTab.tabNodeContainer.getSize();
		var h = h-tabSize.y;

		this.folderTreeAreaScrollNode.setStyle("height", h);
		this.shareTreeAreaScrollNode.setStyle("height", h);
		this.editorTreeAreaScrollNode.setStyle("height", h);
	}
});
