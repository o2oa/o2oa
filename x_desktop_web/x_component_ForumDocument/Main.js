MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.xApplication.ForumDocument = MWF.xApplication.ForumDocument || {};
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Forum", "Attachment", null, false);
MWF.xDesktop.requireApp("Forum", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Forum", "Access", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Forum", "TopNode", null, false);

MWF.xApplication.ForumDocument.options = {
	multitask: true,
	executable: true
};
MWF.xApplication.ForumDocument.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "ForumDocument",
		"icon": "icon.png",
		"width": "1210",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"isNew" : false,
		"isEdited" : true,
		"index" : 1,
		"replyIndex" : null,
		"viewPageNum" : 1,
		"title": MWF.xApplication.ForumDocument.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Forum.LP;
	},
	onQueryClose: function(){

	},
	loadApplication: function(callback){
		this.userData = layout.desktop.session.user;
		this.userName = this.userData.name;
		this.restActions = this.actions = new MWF.xApplication.Forum.Actions.RestActions();

		this.path = "/x_component_ForumDocument/$Main/"+this.options.style+"/";
		if( this.options.isNew && !this.options.id ){
			 this.actions.getUUID( function( id ){
				 this.advanceId = id;
				 this.createNode();
				 this.loadApplicationContent();
			 }.bind(this))
		}else{
			this.createNode();
			this.loadApplicationContent();
		}
	},
	loadController: function(callback){
		this.access = new MWF.xApplication.Forum.Access( this.restActions, this.lp );
		if(callback)callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.node
		}).inject(this.content);
	},
	loadApplicationContent: function(){
		if( this.status ){
			this.setOptions( this.status )
		}
		this.loadController(function(){
			this.access.login( function(){
				this.loadApplicationLayout();
			}.bind(this) )
		}.bind(this))
	},
	reload : function(oldid, appid){
		this.node.empty();
		this.pagingBarTop = null;
		this.pagingContainerTop = null;
		delete this.pagingBarTop;
		delete this.pagingContainerTop;

		this.loadApplicationLayout();

		//if( this.appCurrentList.indexOf( this > 0 ){
		//	this.appCurrentList.erase(this);
		//	this.appCurrentList.push(this)
		//}
		if( oldid && appid && (oldid != appid) ){
			delete this.desktop.apps[oldid];
			this.appId = appid;
			this.desktop.apps[appid] = this;
		}
	},
	loadApplicationLayout : function(){
		this.contentContainerNode = new Element("div.contentContainerNode", {
			"styles": this.css.contentContainerNode
		}).inject(this.node);

		if( this.options.id ){
			this.restActions.listSubjectPermission( this.options.id, function( permission ){
				this.permission = permission.data;
				if( this.options.isEdited ){
					this.restActions.getSubject( this.options.id , function( data ){
						this.data = data.data;
						this._loadApplicationLayout( this.data.sectionId , this.data.title );
					}.bind(this))
				}else{
					this.restActions.getSubjectView( this.options.id , function( data ){
						this.data = data.data.currentSubject;
						this.nextSubject = data.data.nextSubject;
						this.lastSubject = data.data.lastSubject;
						this._loadApplicationLayout( this.data.sectionId , this.data.title );
					}.bind(this))
				}
			}.bind(this) )
		}else{
			this._loadApplicationLayout( this.options.sectionId, this.lp.createSubject );
		}
	},
	_loadApplicationLayout : function( sectionId, title ){
		this.options.sectionId = sectionId;
		this.restActions.listSectionPermission( sectionId, function( permission ){
			this.sectionPermission = permission.data;
			this.restActions.getSection( sectionId, function( json ) {
				this.sectionData = json.data;
				//this.access.hasSectionAdminAuthority( this.sectionData , function( flag ){
				//	this.isAdmin = flag;
				this.restActions.getCategory(this.sectionData.forumId, function (formData) {
					this.formData = formData.data;
					this.setTitle( title );
					this.createTopNode();
					this.createMiddleNode();
				}.bind(this))
				//}.bind(this) );
			}.bind(this))
		}.bind(this) )
	},
	createTopNode: function(){
		var forumColor = MWF.xApplication.Forum.ForumSetting[this.sectionData.forumId].forumColor;

		var topNode = this.topNode = new Element("div.topNode", {
			"styles": this.css.topNode
		}).inject(this.contentContainerNode);
		topNode.setStyle("border-bottom","1px solid "+forumColor);

		var topTitleLeftNode = new Element("div.topTitleLeftNode", {
			"styles": this.css.topTitleLeftNode
		}).inject(topNode);
		topTitleLeftNode.setStyle( "background-color" , forumColor )

		var topTitleMiddleNode = new Element("div.topTitleMiddleNode", {
			"styles": this.css.topTitleMiddleNode
		}).inject(topNode);
		topTitleMiddleNode.setStyle( "background-color" , forumColor )

		var topTitleRightNode = new Element("div.topTitleRightNode", {
			"styles": this.css.topTitleRightNode
		}).inject(topNode);
		topTitleRightNode.setStyle( "background-color" , forumColor )

		var topItemTitleNode = new Element("div.topItemTitleNode", {
			"styles": this.css.topItemTitleNode,
			"text": this.lp.title
		}).inject(topTitleMiddleNode);
		topItemTitleNode.addEvent("click", function(){
			var appId = "Forum";
			if (this.desktop.apps[appId]){
				this.desktop.apps[appId].setCurrent();
			}else {
				this.desktop.openApplication(null, "Forum", { "appId": appId });
			}
			if( !this.inBrowser ){
				this.close();
			}
		}.bind(this))

		var topItemSepNode = new Element("div.topItemSepNode", {
			"styles": this.css.topItemSepNode
		}).inject(topTitleMiddleNode);

		var topItemTitleNode = new Element("div.topItemTitleNode", {
			"styles": this.css.topItemTitleNode,
			"text": this.sectionData.forumName
		}).inject(topTitleMiddleNode);
		topItemTitleNode.addEvent("click", function(){
			var appId = "ForumCategory"+this.forumId;
			if (this.obj.desktop.apps[appId]){
				this.obj.desktop.apps[appId].setCurrent();
			}else {
				this.obj.desktop.openApplication(null, "ForumCategory", { "categoryId" : this.forumId ,"appId": appId });
			}
			if( !this.obj.inBrowser ){
				this.obj.close();
			}
			//this.obj.close();
		}.bind({ obj: this, forumId : this.sectionData.forumId }))

		var topItemSepNode = new Element("div.topItemSepNode", {
			"styles": this.css.topItemSepNode
		}).inject(topTitleMiddleNode);

		var topItemTitleNode = new Element("div.topItemTitleNode", {
			"styles": this.css.topItemTitleNode,
			"text": this.sectionData.sectionName
		}).inject(topTitleMiddleNode);
		topItemTitleNode.addEvent("click", function(){
			var appId = "ForumSection"+this.sectionData.id;
			if (this.desktop.apps[appId]){
				this.desktop.apps[appId].setCurrent();
			}else {
				this.desktop.openApplication(null, "ForumSection", {
					"sectionId" : this.sectionData.id,
					"appId": appId
				});
			}
			if( !this.inBrowser ){
				this.close();
			}
			//this.close();
		}.bind(this))

		var topItemSepNode = new Element("div.topItemSepNode", {
			"styles": this.css.topItemSepNode
		}).inject(topTitleMiddleNode);

		var topItemTitleNode = new Element("div.topItemTitleNode", {
			"styles": this.css.topItemTitleNode,
			"text": this.options.isNew ? (this.lp.createSubject) : ("["+ this.data.type +"]" + this.data.title )
		}).inject(topTitleMiddleNode);

		//this.topRightTextNode = new Element("div", {
		//	"styles": this.css.topRightTextNode,
		//	"text": this.lp.setting
		//}).inject(this.topRightTextNode)
	},
	createMiddleNode: function(){
		this.middleNode = new Element("div.middleNode", {
			"styles": this.css.middleNode
		}).inject(this.contentContainerNode);

		this.addEvent("resize", function () {
			this.setContentSize();
		}.bind(this));
		this.setContentSize();

		this.middleNode.addEvent("selectstart", function(e){
			e.stopPropagation();
		});

		//MWF.require("MWF.widget.ScrollBar", function () {
		//	this.scrollBar = new MWF.widget.ScrollBar(this.contentContainerNode, {
		//		"indent": false,
		//		"style": "xApp_TaskList",
		//		"where": "before",
		//		"distance": 30,
		//		"friction": 4,
		//		"axis": {"x": false, "y": true},
		//		"onScroll": function (y) {
		//		}
		//	});
		//}.bind(this));

		if( this.options.isNew || this.options.isEdited ){
			this._createMiddleNode_eidt();
		}else{
			this._createMiddleNode_read();
		}
	},
	_createMiddleNode_eidt : function(){
		this.data = this.data || {}
		var _self = this;
		this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleNode);

		var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
			"<tr>" +
			"   <td styles='formTableTitle' lable='title' width='10%'></td>" +
			"   <td styles='formTableValue' item='type'width='10%'></td>" +
			"   <td styles='formTableValue' item='title'width='80%'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableTitle' lable='summary'></td>" +
			"   <td styles='formTableValue' item='summary' colspan='2'></td>" +
			"</tr><tr item='portalImageTr' style='display:none'>" +
			"   <td styles='formTableTitle' lable='portalImage'></td>" +
			"   <td styles='formTableValue' colspan='2'><div item='portalImageAre' styles='portalImageAre' ></div></td>" +
			"</tr><tr>" +
			"   <td styles='formTableTitle' lable='content'></td>" +
			"   <td styles='formTableValue' item='content' colspan='2'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableTitle'>"+ this.lp.attachment +"</td>" +
			"   <td item='attachment' colspan='2'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableTitle' lable=''></td>" +
			"   <td item='action' colspan='2'></td>" +
			"</tr>"
		"</table>"
		this.contentDiv.set("html", html);

		if( this.formData.indexListStyle == this.lp.indexListStyleImage ){
			this.contentDiv.getElements("[item='portalImageTr']")[0].setStyle("display","");
		}

		var subjectTypeSelectValue;
		if( this.sectionData.subjectType ){
			subjectTypeSelectValue = this.sectionData.subjectType.split("|");
		}else if( this.formData.subjectType ){
			subjectTypeSelectValue = this.formData.subjectType.split("|");
		}else{
			subjectTypeSelectValue = this.lp.subjectTypeDefaultValue.split("|");
		}
		MWF.xDesktop.requireApp("Template", "MForm", function () {
			this.form = new MForm(this.contentDiv, this.data , {
				style: "forum",
				isEdited: true || this.isEdited || this.isNew,
				itemTemplate: {
					title: {text: this.lp.subject, notEmpty : true },
					type: {text: this.lp.type, type : "select", selectValue : subjectTypeSelectValue , notEmpty : true },
					summary: {text: this.lp.summary, type : "text", event : { "keyup" : function( item, ev){
						if( item.getValue().length > 70 ){
							item.setValue( item.getValue().substr( 0, 70 ) );
						}
					} } },
					portalImage: {text: this.lp.portalImage },
					content: {text: this.lp.content, type : "rtf", notEmpty : true, RTFConfig : {
						isSetImageMaxWidth : true,
						reference : this.advanceId || this.data.id,
						referenceType: "forumDocument",
						skin : "bootstrapck" //,
						//filebrowserCurrentDocumentImage: function (e, callback) {
						//	_self.selectDocImage( callback );
						//}
					}}
				}
			}, this, this.css);
			this.form.load();
		}.bind(this), true);

		this.createIconNode();

		var actionTd = this.contentDiv.getElements("[item='action']")[0];
		this.saveAction = new Element("div",{
			styles : this.css.actionNode,
			text: this.lp.saveSubject
		}).inject(actionTd);
		this.saveAction.addEvent("click",function(){
			this.saveSubject();
		}.bind(this))

		var attachmentArea = this.contentDiv.getElements("[item='attachment']")[0];
		this.loadAttachment(attachmentArea)
	},
	//selectDocImage : function( callback, width ){
	//	this.selector_doc = new MWF.xApplication.Forum.Attachment(this.content, this, this.restActions, this.lp, {
	//		//documentId : this.data ? this.data.id : "",
	//		isNew : this.options.isNew,
	//		isEdited : this.options.isEdited,
	//		"onUpload" : function( attData ){
	//			this.attachment.attachmentController.addAttachment(attData);
	//			this.attachment.attachmentController.checkActions();
	//		}.bind(this)
	//	})
	//	this.selector_doc.data = this.attachment.getAttachmentData();
	//	this.selector_doc.loadAttachmentSelecter({
	//		"style": "cms",
	//		"title": "选择本文档图片",
	//		"listStyle": "preview",
	//		"toBase64" : true,
	//		"base64MaxSize" : width || 800,
	//		"selectType": "images"
	//	}, function (url, data, base64Code ) {
	//		if (callback)callback(url, base64Code, data);
	//	}.bind(this));
	//},
	//selectCloudImage : function( callback, width ){
	//	var _self = this;
	//	MWF.xDesktop.requireApp("File", "FileSelector", function(){
	//		_self.selector_cloud = new MWF.xApplication.File.FileSelector( document.body ,{
	//			"style" : "default",
	//			"title": "选择云文件图片",
	//			"listStyle": "preview",
	//			"toBase64" : true,
	//			"base64Width" : width || 800,
	//			"selectType" : "images",
	//			"onPostSelectAttachment" : function(url, base64Code){
	//				if(callback)callback(url, base64Code);
	//			}
	//		});
	//		_self.selector_cloud.load();
	//	}, true);
	//},
	//insertImage : function( callback ){
	//	var form = new MWF.xApplication.ForumDocument.ImageLinkForm(this, this.data, {
	//		onPostOk : function( data ){
	//			if( callback )callback(data);
	//		}.bind(this)
	//	},{
	//		app : this, lp : this.lp, css : this.css, actions : this.restActions
	//	})
	//	form.create()
	//},
	createIconNode: function(){
		var sectionIconArea = this.contentDiv.getElements("[item='portalImageAre']")[0];

		if( sectionIconArea ){
			this.picId =  (this.data && this.data.picId) ? this.data.picId : null;
			if( this.picId ){
				this.portalImage = new Element("img", {
					"src" : MWF.xDesktop.getImageSrc(this.picId),
					"styles" : this.css.portalImageNode
				}).inject( sectionIconArea );
			};
			this.uploadImageAction = new Element("button.uploadActionNode",{
				"styles" : this.css.uploadActionNode,
				"text" : "设置图片"
			}).inject(sectionIconArea);
			this.uploadImageAction.addEvents({
				"click": function(){
					MWF.xDesktop.requireApp("ForumDocument", "ImageClipper",null,false);
					this.clipper = new MWF.xApplication.ForumDocument.ImageClipper(this.form.app, {
						"aspectRatio": 1.5,
						"imageUrl": this.picId ? MWF.xDesktop.getImageSrc(this.picId) : "",
						"reference": this.advanceId || this.data.id,
						"referenceType": "forumDocument",
						"onChange": function () {
							if( this.portalImage )this.portalImage.destroy();
							this.portalImage = new Element("img", {
								"src" : this.clipper.imageSrc,
								"styles" : this.css.portalImageNode
							}).inject( sectionIconArea, "top" );
							this.picId = this.clipper.imageId;
						}.bind(this)
					});
					this.clipper.load();
				}.bind(this)
			});
		}
	},
	reloadAllParents : function(){
		var aid = "Forum";
		if (this.desktop.apps[aid]){
			this.desktop.apps[aid].reload();
		}

		aid = "ForumCategory"+this.sectionData.forumId;
		if (this.desktop.apps[aid]){
			this.desktop.apps[aid].reload();
		}

		aid = "ForumSection"+this.sectionData.id;
		if (this.desktop.apps[aid]){
			this.desktop.apps[aid].reload();
		}
	},
	saveSubject  : function(){
		var data = this.form.getResult(true, ",", true, false, true);
		if( this.advanceId )data.id = this.advanceId;
		data.attachmentList = this.attachment.getAttachmentIds();
		if (data) {
			data.sectionId = this.sectionData.id;
			data.picId = this.picId || ""; //this.clipper.getBase64Image() || "";
			this.restActions.saveSubject(data, function (json) {
				this.notice(this.options.isNew ? this.lp.createSuccess : this.lp.updateSuccess, "success");
				this.fireEvent("postPublish");

				this.reloadAllParents();

				var oldId = "ForumDocument"+ ( this.options.isNew ? "" : this.data.id );
				var appId = "ForumDocument"+json.data.id;
				this.setOptions({
					"id" : json.data.id,
					"appId": appId,
					"isEdited" : false,
					"isNew" : false //,
					//"index" : nextIndex
				});
				this.reload(oldId , appId );

			}.bind(this))
		}
	},
	_createMiddleNode_read: function(){
		this.isReplyPublisher = this.permission.replyPublishAble; //this.access.isReplyPublisher( this.sectionData );

		this.createPagingBar();
		this.createToolbar_read();

		this.subjectConainer = new Element("div.subjectConainer",{
			"styles" : this.css.subjectConainer
		}).inject( this.middleNode );

		this.replyViewConainer = new Element("div.replyViewConainer",{
			"styles" : this.css.replyViewConainer
		}).inject( this.middleNode );

		this.createPagingBar();

		this.createSubject();
		this.createReplyView();
		if( !this.data.stopReply && this.isReplyPublisher ){
			if( this.access.isAnonymous() ){
				this.createReplyEditor_Anonymous()
			}else{
				this.createReplyEditor();
			}
		}
		this.createTurnSubjectNode();

	},
	createPagingBar: function(){
		var pagingArea = new Element("div",{
			styles : this.css.pagingArea
		}).inject(this.middleNode)
		if( this.pagingBarTop ){
			this.pagingBarBottom = pagingArea;
		}else{
			this.pagingBarTop = pagingArea;
		}

		//if( this.access.isSubjectPublisher( this.sectionData ) ){
		if( this.sectionPermission.subjectPublishAble ){
			var createActionNode = new Element("div",{
				styles : this.css.pagingActionNode,
				text: this.lp.createSubject
			}).inject(pagingArea);
			createActionNode.addEvents(
				{
					"mouseover": function () {
						this.node.setStyles(this.obj.css.pagingActionNode_over);
					}.bind({obj: this, node: createActionNode}),
					"mouseout": function () {
						this.node.setStyles(this.obj.css.pagingActionNode);
					}.bind({obj: this, node: createActionNode}),
					"click": function () {
						if( this.access.isAnonymousDynamic() ){
							this.openLoginForm(
								function(){ this.createNewDocument(); }.bind(this)
							);
						}else{
							this.createNewDocument();
						}
					}.bind(this)
				}
			)
		}

		//var fileterNode = new Element("div",{
		//	styles : this.css.fileterNode
		//}).inject(pagingArea);

		var pagingContainer = new Element("div").inject(pagingArea);
		if( this.pagingContainerTop ){
			this.pagingContainerBottom = pagingContainer;
		}else{
			this.pagingContainerTop = pagingContainer;
		}
	},
	setContentSize: function () {
		//var topSize = this.topNode ? this.topNode.getSize() : {"x": 0, "y": 0};
		var topSize = {"x": 0, "y": 0};
		var nodeSize = this.node.getSize();
		var pt = this.contentContainerNode.getStyle("padding-top").toFloat();
		var pb = this.contentContainerNode.getStyle("padding-bottom").toFloat();

		var height = nodeSize.y - topSize.y - pt - pb;
		this.contentContainerNode.setStyle("height", "" + height + "px");
	},
	recordStatus: function(){
		return {
			"sectionId" : this.options.sectionId,
			"id" : this.data.id,
			"appId": this.data.id ? "ForumDocument"+this.data.id : undefined,
			"isEdited" : this.options.isEdited,
			"isNew" : this.options.isNew,
			"viewPageNum" : this.replyView ? this.replyView.getCurrentPageNum() : 1
		};
	},
	loadAttachment: function( area ){
		this.attachment = new MWF.xApplication.Forum.Attachment( area, this, this.restActions, this.lp, {
			documentId : this.advanceId || this.data.id,
			isNew : this.options.isNew,
			isEdited : this.options.isEdited,
			"size" : "min",
			onQueryUploadAttachment : function(){
				this.attachment.isQueryUploadSuccess = true;
			}.bind(this),
			onDelete : function( data ){
				//if( this.pictureData && this.pictureData.id == data.id ){
				//	this.iconNode.set( "src", "" );
				//	this.iconNode.setStyle("display","none");
				//	this.pictureBase64 = "";
				//	this.pictureData = null;
				//}
			}.bind(this)
		})
		this.attachment.load();
	},
	createToolbar_read : function(){
		this.toolBarRead = new Element("div.toolBarRead",{
			"styles" : this.css.toolBarRead
		}).inject( this.middleNode )
		this.toolbarLeft = new Element("div.toolbarLeft",{
			"styles" : this.css.toolbarLeft
		}).inject( this.toolBarRead )

		new Element("div.toolbarLeftItem",{
			"styles" : this.css.toolbarLeftItem,
			"text" : this.data.viewTotal + this.lp.readed
		}).inject( this.toolbarLeft )
		this.replyTotal = new Element("div.toolbarLeftItem",{
			"styles" : this.css.toolbarLeftItem,
			"text" : this.data.replyTotal + this.lp.reply
		}).inject( this.toolbarLeft )

		this.toolbarRight = new Element("div.toolbarRight",{
			"styles" : this.css.toolbarRight
		}).inject( this.toolBarRead )

		this.createActionBar();

		//this.toolbarRightTitle = new Element("div.toolbarRightTitle",{
		//	"styles" : this.css.toolbarRightTitle,
		//	"text" : "["+ this.data.type +"]"+this.data.title
		//}).inject( this.toolbarRight )

		this.toolbarRightTools = new Element("div.toolbarRightTools",{
			"styles" : this.css.toolbarRightTools
		}).inject( this.toolbarRight )

		if( this.nextSubject ){
			this.toolbarNext = new Element("div.toolbarNext",{
				"styles" : this.css.toolbarNext,
				"title" : this.lp.nextSubject + "：" + this.nextSubject.title
			}).inject( this.toolbarRightTools )
			this.toolbarNext.addEvent("click",function(){
				this.gotoDocument( 1 )
			}.bind(this))
		}

		if( this.lastSubject ){
			this.toolbarPrev = new Element("div.toolbarRightTools",{
				"styles" : this.css.toolbarPrev,
				"title" : this.lp.prevSubject + "：" + this.lastSubject.title
			}).inject( this.toolbarRightTools )
			this.toolbarPrev.addEvent("click",function(){
				this.gotoDocument( -1 )
			}.bind(this))
		}

	},
	adjustReplyCount: function( count ){
		this.data.replyTotal = this.data.replyTotal + count;
		this.replyTotal.set("text", this.data.replyTotal + this.lp.reply)
	},
	createNewDocument: function(){
		var _self = this;
		var appId = "ForumDocument";
		if (_self.desktop.apps[appId]){
			_self.desktop.apps[appId].setCurrent();
		}else {
			this.desktop.openApplication(null, "ForumDocument", {
				"sectionId": this.sectionData.id,
				"appId": appId,
				"isNew" : true,
				"isEdited" : true,
				"onPostPublish" : function(){
					//this.view.reload();
				}.bind(this)
			});
		}
	},
	edit : function(){
		var appId = "ForumDocument"+this.data.id;
		this.options.isEdited = true;
		this.reload(appId , appId );
	},
	delete : function( ev ){
		var _self = this;
		this.confirm("warn", ev, this.lp.deleteDocumentTitle, this.lp.deleteDocument, 350, 120, function(){
			_self.restActions.deleteSubject( _self.data.id, function(){
				_self.notice( _self.lp.deleteDocumentOK, "ok")
				_self.reloadAllParents();
				_self.close();
			}.bind(this) )
			this.close();
		}, function(){
			this.close();
		});
	},
	postCreateReply : function( id ){
		this.restActions.getReply( id, function( json ){
			var reply = this.replyView._createDocument( json.data );
			this.adjustReplyCount( 1 );
			var t = reply.node.getTop() //- this.content.getTop();
			this.contentContainerNode.scrollTo( 0, t );
		}.bind(this))
	},
	createReply : function(){
		var form = new MWF.xApplication.ForumDocument.ReplyForm(this, {}, {
			"toMain" : true,
			onPostOk : function( id ){
				this.postCreateReply( id )
			}.bind(this)
		},{
			app : this, lp : this.lp, css : this.css, actions : this.restActions
		})
		form.mainData = this.data;
		form.create()
	},
	createActionBar : function(){
		this.actionBar = new Element("div", { "styles" : this.css.actionBar, "html" : "&nbsp;"}).inject(this.toolbarRight)

		//var action = new Element("div", {
		//	"styles" : this.css.actionItem,
		//	"text" : this.lp.createSubject
		//}).inject( this.actionBar );
		//action.setStyle("background-image" , "url("+this.path+"icon/action_new.png)");
		//action.addEvents({
		//	"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
		//	"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
		//	"click" : function(){ this.createNewDocument() }.bind(this)
		//})

		if( this.permission.manageAble || this.data.creatorName == this.userName ){
			action = new Element("div", {
				"styles" : this.css.actionItem,
				"text" : this.lp.edit
			}).inject( this.actionBar );
			action.setStyle("background-image" , "url("+this.path+"icon/action_edit.png)");
			action.addEvents({
				"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
				"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
				"click" : function(){ this.edit() }.bind(this)
			})
		}

		if( this.permission.manageAble || this.data.creatorName == this.userName ){
			action = new Element("div", {
				"styles" : this.css.actionItem,
				"text" : this.lp.delete
			}).inject( this.actionBar );
			action.setStyle("background-image" , "url("+this.path+"icon/action_delete.png)");
			action.addEvents({
				"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
				"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
				"click" : function(ev){ this.delete(ev) }.bind(this)
			})
		}

		if( !this.data.stopReply ){
			if( this.isReplyPublisher ){
				action = new Element("div", {
					"styles" : this.css.actionItem,
					"text" : this.lp.reply
				}).inject( this.actionBar );
				action.setStyle("background-image" , "url("+this.path+"icon/action_quote.png)");
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
					"click" : function(){
						if( this.access.isAnonymousDynamic() ){
							this.openLoginForm( function(){ this.reload() }.bind(this) );
						}else{
							this.createReply();
						}
					}.bind(this)
				})
			}
		}

		if( this.permission.manageAble ){
			//if( this.data.isTopSubject ){
			//	action = new Element("div", {
			//		"styles" : this.css.actionItem,
			//		"text" : this.lp.cancelTop
			//	}).inject( this.actionBar );
			//	action.setStyle("background-image" , "url("+this.path+"icon/action_canceltop.png)");
			//	action.addEvents({
			//		"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
			//		"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
			//		"click" : function(){ this.cancelTop() }.bind(this)
			//	})
			//}else{
				action = new Element("div", {
					"styles" : this.css.actionItem,
					"text" : this.lp.setTop
				}).inject( this.actionBar );
				action.setStyle("background-image" , "url("+this.path+ ( this.data.isTopSubject ? "icon/action_canceltop.png" : "icon/action_top.png")+ ")");
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
					"click" : function(){ this.setTop() }.bind(this)
				})
			//}


			if( this.data.isCreamSubject ){
				action = new Element("div", {
					"styles" : this.css.actionItem,
					"text" : this.lp.cancelPrime
				}).inject( this.actionBar );
				action.setStyle("background-image" , "url("+this.path+"icon/action_cancelprime.png)");
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
					"click" : function(){ this.cancelPrime() }.bind(this)
				})
			}else{
				action = new Element("div", {
					"styles" : this.css.actionItem,
					"text" : this.lp.setPrime
				}).inject( this.actionBar );
				action.setStyle("background-image" , "url("+this.path+"icon/action_prime.png)");
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
					"click" : function(){ this.setPrime() }.bind(this)
				})
			}

			action = new Element("div", {
				"styles" : this.css.actionItem,
				"text" : this.lp.setHot
			}).inject( this.actionBar );
			action.setStyle("background-image" , "url("+this.path+ "icon/action_popular.png" + ")");
			action.addEvents({
				"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
				"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
				"click" : function(){ this.setHotPicture() }.bind(this)
			})

			if( this.data.stopReply ){
				action = new Element("div", {
					"styles" : this.css.actionItem,
					"text" : this.lp.unlock
				}).inject( this.actionBar );
				action.setStyle("background-image" , "url("+this.path+"icon/action_unlock.png)");
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
					"click" : function(){ this.unlock() }.bind(this)
				})
			}else{
				action = new Element("div", {
					"styles" : this.css.actionItem,
					"text" : this.lp.lock
				}).inject( this.actionBar );
				action.setStyle("background-image" , "url("+this.path+"icon/action_lock.png)");
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
					"click" : function(){ this.lock() }.bind(this)
				})
			}

		}


		//if( this.access.isRecommender( this.sectionData )){
		if( this.permission.recommendAble ){
			if( this.data.recommendToBBSIndex ){
				action = new Element("div", {
					"styles" : this.css.actionItem,
					"text" : this.lp.cancelRecommend
				}).inject( this.actionBar );
				action.setStyle("background-image" , "url("+this.path+"icon/action_cancelrecommend.png)");
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
					"click" : function(){ this.cancelRecommend() }.bind(this)
				})
			}else if( this.sectionData.sectionVisiable == this.lp.allPerson && this.sectionData.indexRecommendable == true  ){
				action = new Element("div", {
					"styles" : this.css.actionItem,
					"text" : this.lp.setRecommend
				}).inject( this.actionBar );
				action.setStyle("background-image" , "url("+this.path+"icon/action_recommend.png)");
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
					"click" : function(){ this.setRecommend() }.bind(this)
				})
			}
		}

	},
	lock : function(){
		this.restActions.lock( this.data.id, function(){
			this.notice( this.lp.lockSuccess )
			this.reload();
		}.bind(this))
	},
	unlock : function(){
		this.restActions.unlock( this.data.id, function(){
			this.notice( this.lp.unlockSuccess )
			this.reload();
		}.bind(this))
	},
	setRecommend : function(){
		this.restActions.setRecommend( this.data.id, function(){
			this.notice( this.lp.setRecommendSuccess )
			this.reload();
		}.bind(this))
	},
	cancelRecommend : function(){
		this.restActions.cancelRecommend( this.data.id, function(){
			this.notice( this.lp.cancelRecommendSuccess )
			this.reload();
		}.bind(this))
	},
	setHotPicture : function(){
		MWF.xDesktop.requireApp("ForumDocument", "HotLinkForm", null, false);

		var form = new MWF.xApplication.ForumDocument.HotLinkForm(this, this.data, {
			documentId : this.data.id,
			onPostOk : function( id ){

			}.bind(this)
		},{
			app : this, lp : this.lp, css : this.css, actions : this.restActions
		})
		form.create()
	},
	setTop : function(){
		var form = new MWF.xApplication.ForumDocument.TopSettingForm(this, this.data, {
			onPostOk : function( id ){
				this.reload();
			}.bind(this)
		},{
			app : this, lp : this.lp, css : this.css, actions : this.restActions
		})
		form.create()
		//this.restActions.topToSection( this.data.id, function(){
		//	this.notice( this.lp.setTopSuccess )
		//	this.reload();
		//}.bind(this))
	},
	cancelTop : function(){
		this.restActions.cancelTopToSection( this.data.id, function(){
			this.notice( this.lp.cancelTopSuccess )
			this.reload();
		}.bind(this))
	},
	setPrime : function(){
		this.restActions.setCream( this.data.id, function(){
			this.notice( this.lp.setPrimeSuccess )
			this.reload();
		}.bind(this))
	},
	cancelPrime : function(){
		this.restActions.cancelCream( this.data.id, function(){
			this.notice( this.lp.cancelPrimeSuccess )
			this.reload();
		}.bind(this))
	},
	createSubject : function(){
		this.subjectView = new MWF.xApplication.ForumDocument.SubjectView( this.subjectConainer, this, this, {
			templateUrl : this.path + "listItemSubject.json",
			scrollEnable : false
		} )
		this.subjectView.data = this.data;
		this.subjectView.load();
	},
	openLoginForm : function( callback ){
		//MWF.xDesktop.requireApp("Forum", "Login", null, false);
		//var login = new MWF.xApplication.Forum.Login(this, {
		//	onPostOk : function(){ if(callback)callback() }
		//});
		//login.openLoginForm();
		MWF.require("MWF.xDesktop.Authentication", null, false);
		var authentication = new MWF.xDesktop.Authentication({
			style : "application",
			onPostOk : function(){ if(callback)callback() }
		},this);
		authentication.openLoginForm();
	},
	openSignUpForm : function(callback){
		//MWF.xDesktop.requireApp("Forum", "Login", null, false);
		//var login = new MWF.xApplication.Forum.Login(this, {
		//	onPostOk : function(){ if(callback)callback() }
		//});
		//login.openSignUpForm();
		MWF.require("MWF.xDesktop.Authentication", null, false);
		var authentication = new MWF.xDesktop.Authentication({
			style : "application",
			onPostOk : function(){ if(callback)callback() }
		},this);
		authentication.openSignUpForm();
	},
	gotoReply : function( index ){
		this.replyView.paging.gotoItem( index );
	},
	createReplyView : function(){

		this.replyView = new MWF.xApplication.ForumDocument.ReplyView( this.replyViewConainer, this, this, {
			templateUrl : this.path + "listItemReply.json",
			scrollEnable : false,
			pagingEnable : true,
			documentKeyWord : "orderNumber",
			pagingPar : {
				currentPage : this.options.viewPageNum || 1,
				currentItem : this.options.replyIndex,
				returnText : this.lp.returnToList ,
				countPerPage : 10,
				onPostLoad : function( pagingBar ){
					if(pagingBar.nextPageNode){
						pagingBar.nextPageNode.inject( this.pagingBarBottom, "before" );
					}
				}.bind(this),
				onPageReturn : function( pagingBar ){
					var appId = "ForumSection"+this.sectionData.id;
					if (this.desktop.apps[appId]){
						this.desktop.apps[appId].setCurrent();
					}else {
						this.desktop.openApplication(null, "ForumSection", {
							"sectionId" : this.sectionData.id,
							"appId": appId
						});
					}
					this.close();
				}.bind(this)
			},
			onGotoItem : function( top ){
				var t = top - this.content.getTop();
				this.contentContainerNode.scrollTo( 0, t );
			}.bind(this)
		} )
		this.replyView.pagingContainerTop = this.pagingContainerTop;
		this.replyView.pagingContainerBottom = this.pagingContainerBottom;
		this.replyView.data = this.data;
		this.replyView.filterData = { "subjectId" : this.data.id };
		this.replyView.load();
	},
	createReplyEditor_Anonymous: function(){
		this.replyArea = new Element("div.replyArea",{
			"styles" : this.css.replyArea
		}).inject( this.middleNode );

		new Element("div.replyLeft",{
			"styles" : this.css.replyLeft
		}).inject( this.replyArea )

		var replyPicture = new Element("div.replyPicture",{
			"styles" : this.css.replyPicture
		}).inject( this.replyArea )

		var needloginNode = new Element("div.replyNeedLogin",{
			"styles" : this.css.replyNeedLogin
		}).inject(replyPicture)

		new Element("div.replyNeedLogin",{
			"styles" : this.css.replyNeedLoginText,
			"text" : this.lp.replyNeedLoginText
		}).inject(needloginNode)

		var loginNode = new Element("div.replyLoginAction",{
			"styles" : this.css.replyLoginAction,
			"text" : this.lp.login
		}).inject(needloginNode)
		loginNode.addEvent("click" , function(){
			this.openLoginForm(
				function(){ this.reload() }.bind(this)
			)
		}.bind(this))

		if( this.access.signUpMode != "disable" ){
			new Element("div.replyNeedLogin",{
				"styles" : this.css.replyNeedLoginText,
				"text" : "|"
			}).inject(needloginNode)

			var signupNode = new Element("div.replyLoginAction",{
				"styles" : this.css.replyLoginAction,
				"text" : this.lp.signUp
			}).inject(needloginNode)
			signupNode.addEvent("click" , function(){
				this.openSignUpForm()
			}.bind(this))
		}
	},
	createReplyEditor : function( ){
		this.replyArea = new Element("div.replyArea",{
			"styles" : this.css.replyArea
		}).inject( this.middleNode );

		this.replyEditor = new MWF.xApplication.ForumDocument.ReplyEditor( this.replyArea, this, {
			style : this.options.style,
			isNew : true,
			onPostOk : function( id ){
				this.postCreateReply( id )
			}.bind(this)
		} )
		this.replyEditor.mainData = this.data;
		this.replyEditor.load();
	},
	createTurnSubjectNode : function(){
		if( !this.lastSubject && !this.nextSubject )return;
		var turnSubjectNode = new Element("div.turnSubjectNode", {styles : this.css.turnSubjectNode}).inject( this.middleNode );
		if( this.lastSubject ){
			var lastSubjectNode = new Element( "div.lastSubjectNode", {
				styles : this.css.lastSubjectNode,
				text : this.lp.prevSubject + "：" + this.lastSubject.title
			}).inject( turnSubjectNode );
			lastSubjectNode.addEvents({
				"click" : function(){
					this.gotoDocument(-1)
				}.bind(this),
				"mouseover" : function(){ this.node.setStyles( this.obj.css.lastSubjectNode_over ) }.bind({obj :this, node : lastSubjectNode}),
				"mouseout" : function(){ this.node.setStyles( this.obj.css.lastSubjectNode ) }.bind({obj :this, node : lastSubjectNode})
			})
		}else{
			var lastSubjectNode = new Element( "div.lastSubjectNode", {
				styles : this.css.lastSubjectNoneNode
			}).inject( turnSubjectNode );
		}

		if( this.nextSubject ){
			var nextSubjectNode = new Element( "div.nextSubjectNode", {
				styles : this.css.nextSubjectNode,
				text : this.lp.nextSubject + "：" + this.nextSubject.title
			}).inject( turnSubjectNode );
			nextSubjectNode.addEvents({
				"click" : function(){
					this.gotoDocument(1)
				}.bind(this),
				"mouseover" : function(){ this.node.setStyles( this.obj.css.nextSubjectNode_over ) }.bind({obj :this, node : nextSubjectNode}),
				"mouseout" : function(){ this.node.setStyles( this.obj.css.nextSubjectNode ) }.bind({obj :this, node : nextSubjectNode})
			})
		}else{
			var nextSubjectNode = new Element( "div.nextSubjectNode", {
				styles : this.css.nextSubjectNoneNode
			}).inject( turnSubjectNode );
		}
	},
	gotoDocument : function( count ){
		if( count == 1 ){
			var documentData = this.nextSubject;
		}else{
			var documentData = this.lastSubject;
		}
		var oldId = "ForumDocument"+this.data.id;
		var appId = "ForumDocument"+documentData.id;
		if (this.desktop.apps[appId]){
			this.desktop.apps[appId].setCurrent();
			//this.close();
		}else {
			this.setOptions({
				"sectionId" : null, //this.data.sectionId,
				"id" : documentData.id,
				"appId": appId,
				"isEdited" : false,
				"isNew" : false
			});
			this.reload(oldId , appId );
		}
		//var nextIndex = this.options.index + count;
		//if( nextIndex < 0 ){
		//	this.notice( count == -1 ? this.lp.noPrevSubject : this.lp.noNextSubject );
		//	return;
		//}
		//this.restActions.listSubjectFilterPage( nextIndex+1, 1, { sectionId : this.sectionData.id },function (json) {
		//	if( !json.data || json.data.length == 0 ){
		//		this.notice( count == -1 ? this.lp.noPrevSubject : this.lp.noNextSubject )
		//	}else{
		//		var documentData = json.data[0];
		//		var oldId = "ForumDocument"+this.data.id;
		//		var appId = "ForumDocument"+documentData.id;
		//		if (this.desktop.apps[appId]){
		//			this.desktop.apps[appId].setCurrent();
		//			//this.close();
		//		}else {
		//			this.setOptions({
		//				"sectionId" : documentData.sectionId,
		//				"id" : documentData.id,
		//				"appId": appId,
		//				"isEdited" : false,
		//				"isNew" : false,
		//				"index" : nextIndex
		//			});
		//			this.reload(oldId , appId );
		//		}
		//	}
		//}.bind(this))
	},
	getDateDiff: function (publishTime) {
		if(!publishTime)return "";
		var dateTimeStamp = Date.parse(publishTime.replace(/-/gi, "/"));
		var minute = 1000 * 60;
		var hour = minute * 60;
		var day = hour * 24;
		var halfamonth = day * 15;
		var month = day * 30;
		var year = month * 12;
		var now = new Date().getTime();
		var diffValue = now - dateTimeStamp;
		if (diffValue < 0) {
			//若日期不符则弹出窗口告之
			//alert("结束日期不能小于开始日期！");
		}
		var yesterday = new Date().decrement('day', 1);
		var beforYesterday = new Date().decrement('day', 2);
		var yearC = diffValue / year;
		var monthC = diffValue / month;
		var weekC = diffValue / (7 * day);
		var dayC = diffValue / day;
		var hourC = diffValue / hour;
		var minC = diffValue / minute;
		if (yesterday.getFullYear() == dateTimeStamp.getFullYear() && yesterday.getMonth() == dateTimeStamp.getMonth() && yesterday.getDate() == dateTimeStamp.getDate()) {
			result = "昨天 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
		} else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
			result = "前天 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
		} else if (yearC > 1) {
			result = dateTimeStamp.getFullYear() + "年" + (dateTimeStamp.getMonth() + 1) + "月" + dateTimeStamp.getDate() + "日";
		} else if (monthC >= 1) {
			//result= parseInt(monthC) + "个月前";
			// s.getFullYear()+"年";
			result = (dateTimeStamp.getMonth() + 1) + "月" + dateTimeStamp.getDate() + "日";
		} else if (weekC >= 1) {
			result = parseInt(weekC) + "周前";
		} else if (dayC >= 1) {
			result = parseInt(dayC) + "天前";
		} else if (hourC >= 1) {
			result = parseInt(hourC) + "小时前";
		} else if (minC >= 1) {
			result = parseInt(minC) + "分钟前";
		} else
			result = "刚才";
		return result;
	},
	openPerson : function( userName ){
		var appId = "ForumPerson"+userName;
		if (this.desktop.apps[userName]){
			this.desktop.apps[userName].setCurrent();
		}else {
			this.desktop.openApplication(null, "ForumPerson", {
				"personName" : userName,
				"appId": appId
			});
		}
	},
	createPersonNode : function( container, personName ){
		var persons = personName.split(",");
		persons.each( function(userName, i){
			var span = new Element("span", {
				"text" : userName,
				"styles" : this.css.person
			}).inject(container);
			span.addEvents( {
				mouseover : function(){ this.node.setStyles( this.obj.css.person_over )}.bind( {node:span, obj:this} ),
				mouseout : function(){ this.node.setStyles( this.obj.css.person )}.bind( {node:span, obj:this} ),
				click : function(){ this.obj.openPerson( this.userName ) }.bind( {userName:userName, obj:this} )
			})
			if( i != persons.length - 1 ){
				new Element("span", {
					"text" : ",",
				}).inject(container);
			}
		}.bind(this))
	}
});

MWF.xApplication.ForumDocument.SubjectView = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	_createDocument: function(data, index){
		data.index = index;
		this.getUserData( data.creatorName, function(json ){
			data.userIcon = json.data.icon;
			data.signature = json.data.signature;
			this.actions.getUserInfor( {"userName":data.creatorName}, function( json ){
				data.subject = json.data.subjectCount;
				data.reply = json.data.replyCount;
				data.todaySubject = json.data.subjectCountToday;
				data.todayReply = json.data.replyCountToday;
				data.prime = json.data.creamCount;
				data.accessed = json.data.popularity;
				return new MWF.xApplication.ForumDocument.SubjectDocument(this.viewNode, data, this.explorer, this, null, data.index );
			}.bind(this))
		}.bind(this) )
	},
	getUserData : function( name, callback ){
		if( this.app.access.isAnonymous() ){
			this.actions.getPersonIcon(name, function(url){
				if( callback )callback( { data : {
					icon : url
				} } );
			}, function(){
				if( callback )callback( { data : {
					icon : "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif"
				} } );
			}.bind(this))
		}else{
			this.actions.getPerson( function( json ){
				if( !json.data )json.data = {};
				if(  json.data.icon ){
					json.data.icon = 'data:image/png;base64,'+json.data.icon;
				}else{
					json.data.icon = "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif"
				}
				if( callback )callback( json );
			}.bind(this), null, name, true )
		}
	},
	_getCurrentPageData: function(callback, count){
		var json = {
			type: "success",
			count : 1,
			size : 1,
			data : [this.data]
		};
		if (callback)callback(json)
	},
	_removeDocument: function(documentData, all){
		this.actions.deleteSection(documentData.id, function(json){
			this.reload();
			this.app.notice(this.app.lp.deleteDocumentOK, "success");
		}.bind(this));
	},
	_create: function(){

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

MWF.xApplication.ForumDocument.SubjectDocument = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
	mouseoverSubject : function(subjectNode, ev){
		//var removeNode = sectionNode.getElements("[styles='sectionRemoveNode']")[0];
		//if( removeNode )removeNode.setStyle("opacity",1)
	},
	mouseoutSubject : function(subjectNode, ev){
		//var removeNode = sectionNode.getElements("[styles='sectionRemoveNode']")[0];
		//if( removeNode )removeNode.setStyle("opacity",0)
	},
	_queryCreateDocumentNode:function( itemData ){
	},
	_postCreateDocumentNode: function( itemNode, itemData ){

		if( this.data.attachmentList && this.data.attachmentList.length > 0 ){
			var attachmentArea = itemNode.getElements("[item='attachment']")[0];
			this.app.loadAttachment(attachmentArea);
		}
	},
	sendMessage : function(itemNode, ev ){
		var self = this;
		if (layout.desktop.widgets["IMIMWidget"]) {
			var IM = layout.desktop.widgets["IMIMWidget"];
			IM.getOwner(function(){
				this.openChat(ev, {
					from : self.data.creatorName
				});
			}.bind(IM));
		}
	},
	createReply : function(itemNode, ev ){
		if( this.app.access.isAnonymousDynamic() ){
			this.app.openLoginForm( function(){ this.app.reload() }.bind(this) );
		}else{
			var form = new MWF.xApplication.ForumDocument.ReplyForm(this, {}, {
				"toMain" : true,
				onPostOk : function( id ){
					this.app.postCreateReply( id );
				}.bind(this)
			});
			form.mainData = this.data;
			form.create()
		}
	}
})

MWF.xApplication.ForumDocument.ReplyEditor = new Class({
	Implements: [Options , Events],
	options: {
		"style": "default",
		"isNew" : true
	},
	initialize: function(node, app, options){
		this.setOptions(options);
		this.node = node;
		this.app = app;
	},
	load: function(){
		this.app.restActions.getUUID( function( id ){
			this.advanceReplyId = id;
			this._load();
		}.bind(this))
	},
	_load: function(){
		var html = "<div styles='itemNode'>" +
			" <div styles='itemLeftNode'>" +
			"   <div styles='itemUserFace'>" +
			"     <div styles='itemUserIcon' item='userIcon'>" +
			"     </div>" +
			"   </div>" +
			"   <div styles='replyUserName' item='creatorName'>" +
			"   </div>" +
			" </div>" +
			" <div styles='replyRightNode'>" +
			"   <div styles='itemRightMidle'>" +
			"     <div styles='itemBody' item='content'></div>" +
			"     <div styles='itemBody' item='action'></div>" +
			"   </div>" +
			" </div>" +
			"</div>"
		this.node.set("html", html);

		var actionTd = this.node.getElements("[item='action']")[0];
		this.saveReplyAction = new Element("div",{
			styles : this.app.css.actionNode,
			text: this.app.lp.saveReply
		}).inject(actionTd);
		this.saveReplyAction.addEvent("click",function(){
			this.saveReply();
		}.bind(this));

		MWF.xDesktop.requireApp("Template", "MForm", function () {
			this.form = new MForm(this.node, this.data || {}, {
				style: "forum",
				isEdited:  this.options.isEdited || this.options.isNew,
				itemTemplate: {
					userIcon: { className : "itemUserIcon2", type : "img", value : function(){
						if( this.app.userData.icon ){
							return "data:image/png;base64,"+ this.app.userData.icon
						}else{
							return "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif"
						}
					}.bind(this)},
					creatorName: { type : "innerText", value : this.app.userName },
					content: { type : "rtf", RTFConfig : {
						skin : "bootstrapck",
						"resize_enabled": false,
						isSetImageMaxWidth : true,
						reference : this.advanceReplyId,
						referenceType: "forumReply",
						//uiColor : '#9AB8F3',
						//toolbarCanCollapse : true,
						toolbar : [
							{ name: 'document', items : [ 'Preview' ] },
							//{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
							{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','-','RemoveFormat' ] },
							//{ name: 'paragraph', items : [ 'JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
							{ name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
							{ name: 'colors', items : [ 'TextColor','BGColor' ] },
							{ name: 'links', items : [ 'Link','Unlink' ] },
							{ name: 'insert', items : [ 'Image' ] },
							{ name: 'tools', items : [ 'Maximize','-','About' ] }
						]
					}}
				}
			}, this, this.app.css);
			this.form.load();
		}.bind(this), true);


	},
	saveReply : function(){
		var data = this.form.getResult(true, ",", true, false, true);
		if (data) {
			data.subjectId = this.mainData.id ;
			data.id = this.advanceReplyId;
			this.app.restActions.saveReply(data, function (json) {
				if (json.type == "error") {
					this.app.notice(json.message, "error");
				} else {
					this.app.notice( this.app.lp.saveReplySuccess, "ok" );
					this.form.getItem("content").setValue("");
					this.fireEvent("postOk", json.data.id);
				}
			}.bind(this))
		}
	}
})

MWF.xApplication.ForumDocument.ReplyForm = new Class({
	Extends: MWF.xApplication.Template.Explorer.PopupForm,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"width": "820",
		"height": "470",
		"hasTop": true,
		"hasIcon": false,
		"hasTopIcon" : true,
		"hasTopContent" : true,
		"hasBottom": true,
		"title": MWF.xApplication.Forum.LP.replyFormTitle,
		"draggable": true,
		"closeAction": true,
		"toMain" : true
	},
	_createTableContent: function(){
		if( this.isNew ){
			this.app.restActions.getUUID( function(id){
				this.advanceReplyId = id;
				this._createTableContent_();
			}.bind(this) )
		}else{
			this._createTableContent_()
		}
	},
	_createTableContent_: function () {
		var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
			"<tr>" +
			"   <td styles='formTableValue14' item='mainSubject'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableValue' item='mainContent'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableValue' item='content'></td>" +
			"</tr>"
		"</table>"
		this.formTableArea.set("html", html);

		if( !this.options.toMain && this.parentData ){
			var mainContentEl = this.formTableArea.getElements("[item='mainContent']")[0];

			var quoteTop = new Element( "div", {styles : this.css.quoteTop} ).inject( mainContentEl );
			new Element( "div", {styles : this.css.quoteLeft} ).inject( quoteTop );
			new Element( "div", {
				styles : this.css.quoteInfor,
				text : this.parentData.creatorName + this.lp.publishAt + this.parentData.createTime
			}).inject( quoteTop );

			var quoteBottom = new Element( "div", {styles : this.css.quoteBottom} ).inject( mainContentEl );
			var text = this.parentData.contentText;
			new Element( "div", {
				styles : this.css.quoteText,
				text :  text.length > 50 ? (text.substr(0, 50) + "...") : text
			}).inject( quoteBottom );
			new Element( "div", {styles : this.css.quoteRight} ).inject( quoteBottom );
		}
		MWF.xDesktop.requireApp("Template", "MForm", function () {
			this.form = new MForm(this.formTableArea, this.data, {
				style: "forum",
				isEdited: this.isEdited || this.isNew,
				itemTemplate: {
					mainSubject: { type: "innertext", defaultValue : "RE:" + this.mainData.title },
					content: { type : "rtf", RTFConfig : {
						skin : "bootstrapck",
						"resize_enabled": false,
						isSetImageMaxWidth : true,
						reference : this.advanceReplyId || this.data.id,
						referenceType: "forumReply",
						toolbar : [
							{ name: 'document', items : [ 'Preview' ] },
							//{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
							{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','-','RemoveFormat' ] },
							//{ name: 'paragraph', items : [ 'JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
							{ name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
							{ name: 'colors', items : [ 'TextColor','BGColor' ] },
							{ name: 'links', items : [ 'Link','Unlink' ] },
							{ name: 'insert', items : [ 'Image' ] },
							{ name: 'tools', items : [ 'Maximize','-','About' ] }
						]
					}}
				}
			}, this.app, this.css);
			this.form.load();
		}.bind(this), true);
	},
	_createBottomContent: function () {
		if (this.isNew || this.isEdited) {
			this.okActionNode = new Element("div.formOkActionNode", {
				"styles": this.css.formOkActionNode,
				"text": this.app.lp.saveReply
			}).inject(this.formBottomNode);

			this.okActionNode.addEvent("click", function (e) {
				this.ok(e);
			}.bind(this));
		}

		this.cancelActionNode = new Element("div.formCancelActionNode", {
			"styles": this.css.formCancelActionNode,
			"text": this.app.lp.close
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.cancel(e);
		}.bind(this));

	},
	ok: function (e) {
		this.fireEvent("queryOk");
		var data = this.form.getResult(true, ",", true, false, true);
		if (data) {
			this._ok(data, function (json) {
				if (json.type == "error") {
					this.app.notice(json.message, "error");
				} else {
					this.formMarkNode.destroy();
					this.formAreaNode.destroy();
					this.app.notice(this.isNew ? this.app.lp.createSuccess : this.app.lp.updateSuccess, "success");
					this.fireEvent("postOk", json.data.id);
				}
			}.bind(this))
		}
	},
	_ok: function (data, callback) {
		data.subjectId = this.mainData.id ;
		if( this.advanceReplyId )data.id = this.advanceReplyId;
		if( !this.options.toMain ){
			data.parentId = this.parentData.id ;
		}
		this.app.restActions.saveReply( data, function(json){
			if( callback )callback(json);
		}.bind(this));
	}
});

MWF.xApplication.ForumDocument.ReplyView = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	_createDocument: function(data, index){
		data.index = index;
		return  new MWF.xApplication.ForumDocument.ReplyDocument(this.viewNode, data, this.explorer, this, null, data.index );
	},
	_getCurrentPageData: function(callback, count, pageNum){
		this.clearBody();
		if(!count)count=10;
		if(!pageNum)pageNum = 1;
		if( pageNum == 1 ){
			this.app.subjectConainer.setStyle("display","block");
		}else{
			this.app.subjectConainer.setStyle("display","none");
		}
		//page, count,  filterData, success,failure, async
		//if( !this.page ){
		//	this.page = 1;
		//}else{
		//	this.page ++;
		//}
		//var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
		var filter = this.filterData || {};
		this.actions.listReplyFilterPage( pageNum, count, filter, function(json){
			if( !json.data )json.data = [];
			if( !json.count )json.count=0;
			if( callback )callback(json);
		}.bind(this))
	},
	_removeDocument: function(documentData, all){
		this.actions.deleteReply( documentData.id, function(){
			this.reload();
			this.app.notice( this.lp.deleteReplySuccess, "ok")
		}.bind(this) )
	},
	_create: function(){

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

MWF.xApplication.ForumDocument.ReplyDocument = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
	mouseoverSubject : function(subjectNode, ev){
		//var removeNode = sectionNode.getElements("[styles='sectionRemoveNode']")[0];
		//if( removeNode )removeNode.setStyle("opacity",1)
	},
	mouseoutSubject : function(subjectNode, ev){
		//var removeNode = sectionNode.getElements("[styles='sectionRemoveNode']")[0];
		//if( removeNode )removeNode.setStyle("opacity",0)
	},
	getUserData : function( name, callback ){
		if( this.app.access.isAnonymous() ){
			this.actions.getPersonIcon(name, function(url){
				if( callback )callback( { data : {
					icon : url
				} } );
			}, function(){
				if( callback )callback( { data : {
					icon : "/x_component_ForumDocument/$Main/"+this.view.options.style+"/icon/noavatar_big.gif"
				} } );
			}.bind(this))
		}else{
			this.actions.getPerson( function( json ){
				if( !json.data )json.data = {};
				if( json.data.icon ){
					json.data.icon = 'data:image/png;base64,'+json.data.icon;
				}else{
					json.data.icon = "/x_component_ForumDocument/$Main/"+this.view.options.style+"/icon/noavatar_big.gif"
				}
				if( callback )callback( json );
			}.bind(this), null, name, true )
		}
	},
	_queryCreateDocumentNode:function( itemData ){
	},
	_postCreateDocumentNode: function( itemNode, itemData ){
		var userIcon = itemNode.getElements( "[item='userIcon']" )[0];
		var signatureContainer = itemNode.getElements("[item='signatureContainer']")[0];
		this.getUserData( itemData.creatorName, function(json ){
			userIcon.src = json.data.icon;
			if( json.data.signature && json.data.signature!="" ){
				var signatureNode = signatureContainer.getElements("[item='signature']")[0];
				signatureNode.set("text", json.data.signature )
			}else{
				signatureContainer.destroy();
			}
		}.bind(this) );

		this.actions.getUserInfor( {"userName":itemData.creatorName}, function( json ){
			var d = json.data;
			itemNode.getElements( "[item='subject']" )[0].set("text", d.subjectCount);
			itemNode.getElements( "[item='reply']" )[0].set("text", d.replyCount);
			itemNode.getElements( "[item='prime']" )[0].set("text", d.creamCount);
			itemNode.getElements( "[item='todaySubject']" )[0].set("text", d.subjectCountToday);
			itemNode.getElements( "[item='todayReply']" )[0].set("text", d.replyCountToday);
		}.bind(this))

		if( itemData.parentId && itemData.parentId != "" ){
			var quoteContainer = itemNode.getElements( "[item='quoteContent']" )[0];
			this.actions.getReply( itemData.parentId, function( json ){
					var data = this.parentData =  json.data;
					var quoteContent = new Element("div", {  "styles" : this.css.itemQuote }).inject(quoteContainer)
					var content = quoteContent.set("html", data.content).get("text");
					quoteContent.empty();
					data.contentText = content;

					new Element( "div", {styles : this.css.quoteLeftBig} ).inject( quoteContent );

					var quoteArea = new Element( "div", {styles : this.css.quoteAreaBig } ).inject( quoteContent );
					var quoteInfor = new Element( "div", {
						styles : this.css.quoteInforBig,
						text : data.orderNumber + this.lp.floor + "：" + data.creatorName + this.lp.publishAt + data.createTime
					}).inject( quoteArea );
					quoteInfor.addEvent("click", function(){
						this.obj.app.gotoReply( this.index )
					}.bind({obj : this, index : data.orderNumber || (data.index + 2) }))
					new Element( "div", {
						styles : this.css.quoteTextBig,
						text :  content.length > 100 ? (content.substr(0, 100) + "...") : content
					}).inject( quoteArea );

					new Element( "div", {styles : this.css.quoteRightBig} ).inject( quoteContent );
				}.bind(this) , function( json ){
					new Element( "div" , {
						"styles" : this.css.replyBeinngDelete,
						"text" : this.lp.quoteReplyBeingDeleted
					}).inject(quoteContainer)
				}.bind(this)
			)
		}

	},
	sendMessage : function(itemNode, ev ){
		var self = this;
		if (layout.desktop.widgets["IMIMWidget"]) {
			var IM = layout.desktop.widgets["IMIMWidget"];
			IM.getOwner(function(){
				this.openChat(ev, {
					from : self.data.creatorName
				});
			}.bind(IM));
		}
	},
	createReply : function(itemNode, ev ){ // 对回复进行回复
		if( this.app.access.isAnonymousDynamic() ){
			this.app.openLoginForm( function(){ this.app.reload() }.bind(this) );
		}else{
			var form = new MWF.xApplication.ForumDocument.ReplyForm(this, {}, {
				toMain: false,
				onPostOk: function (id) {
					this.app.postCreateReply(id)
				}.bind(this)
			})
			this.data.contentText = this.node.getElements("[item='content']")[0].get("text");
			form.mainData = this.app.data;
			form.parentData = this.data;
			form.create()
		}
	},
	editReply : function(itemNode, ev ){	//编辑当前回复
		var form = new MWF.xApplication.ForumDocument.ReplyForm(this, this.data, {
			toMain : (this.data.parentId && this.data.parentId!="") ? false : true,
			onPostOk : function( id ){
				this.actions.getReply( id, function( json ){
					var content = this.node.getElements("[item='content']")[0];
					content.set( "html", json.data.content );
				}.bind(this))
			}.bind(this)
		})
		form.mainData = this.app.data;
		form.parentData = this.parentData;
		form.edit()
	},
	deleteReply : function( itemNode, ev ){
		var _self = this;
		this.app.confirm("warn", ev, this.lp.deleteReplyTitle, this.lp.deleteReplyText, 350, 120, function(){
			//_self.view._removeDocument(_self.data, false);
			_self.actions.deleteReply( _self.data.id, function(){
				_self.destroy();
				_self.app.adjustReplyCount( -1 );
				_self.app.notice( _self.lp.deleteReplySuccess, "ok")
			}.bind(this) )
			this.close();
		}, function(){
			this.close();
		});
	}
});

MWF.xApplication.ForumDocument.TopSettingForm = new Class({
	Extends: MWF.xApplication.Template.Explorer.PopupForm,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"width": "420",
		"height": "250",
		"hasTop": true,
		"hasIcon": false,
		"hasTopIcon" : true,
		"hasTopContent" : true,
		"hasBottom": true,
		"title": MWF.xApplication.Forum.LP.topFormTitle,
		"draggable": true,
		"closeAction": true
	},
	createTopNode: function () {

		if (!this.formTopNode) {
			this.formTopNode = new Element("div.formTopNode", {
				"styles": this.css.formTopNode
			}).inject(this.formNode);

			if(this.options.hasTopIcon){
				this.formTopIconNode = new Element("div", {
					"styles": this.css.formTopIconNodeDocument
				}).inject(this.formTopNode)
			}

			this.formTopTextNode = new Element("div", {
				"styles": this.css.formTopTextNodeTopSetting,
				"text": this.options.title
			}).inject(this.formTopNode)

			if (this.options.closeAction) {
				this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
				this.formTopCloseActionNode.addEvent("click", function () {
					this.close()
				}.bind(this))
			}
		}
	},
	_createTableContent: function () {
		var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
			"<tr>" +
			"   <td styles='formTableValue' style='font-size:14px;' lable='topType'></td>" +
			//"</tr><tr>" +
			//"   <td styles='formTableValue' item='topToBBS'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableValue' item='topToForum'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableValue' item='topToSection'></td>" +
			"</tr>"
		"</table>"
		this.formTableArea.set("html", html);

		this.topToBBS = this.data.topToBBS;
		this.topToForum = this.data.topToForum;
		this.topToSection = this.data.topToSection;
		MWF.xDesktop.requireApp("Template", "MForm", function () {
			this.form = new MForm(this.formTableArea, this.data, {
				style: "forum",
				isEdited: this.isEdited || this.isNew,
				itemTemplate: {
					topType : { text : this.lp.topType },
					//topToBBS: { type: "checkbox", selectValue : [true],  selectText : [this.lp.topToBBS] },
					topToForum: { type: "checkbox", selectValue : ["true"],  selectText : [this.lp.topToForum]},
					topToSection: { type: "checkbox", selectValue : ["true"],  selectText : [this.lp.topToSection] }
				}
			}, this.app, this.css);
			this.form.load();
		}.bind(this), true);
	},
	_createBottomContent: function () {
		if (this.isNew || this.isEdited) {
			this.okActionNode = new Element("div.formOkActionNode", {
				"styles": this.css.formOkActionNode,
				"text": this.app.lp.ok
			}).inject(this.formBottomNode);

			this.okActionNode.addEvent("click", function (e) {
				this.ok(e);
			}.bind(this));
		}

		this.cancelActionNode = new Element("div.formCancelActionNode", {
			"styles": this.css.formCancelActionNode,
			"text": this.app.lp.close
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.cancel(e);
		}.bind(this));

	},
	ok: function (e) {
		this.fireEvent("queryOk");
		var data = this.form.getResult(true, ",", true, false, true);
		if (data) {
			var flag = true;
			//if( data.topToBBS === true || data.topToBBS === "true" ){
			//	this.actions.topToBBS( this.app.data.id , function( json ){
			//		if (json.type == "error") {
			//			this.app.notice(json.userMessage, "error");
			//			flag = false;
			//		}
			//	}, function(){
			//		flag = false;
			//	}, false )
			//}else if( this.topToBBS === true || this.topToBBS === "true" ){
			//	this.actions.cancelTopToBBS( this.app.data.id , function( json ){
			//		if (json.type == "error") {
			//			this.app.notice(json.userMessage, "error");
			//			flag = false;
			//		}
			//	}, function(){
			//		flag = false;
			//	}, false )
			//}
			if( data.topToForum === true || data.topToForum === "true" ){
				this.actions.topToForum( this.app.data.id , function( json ){
					if (json.type == "error") {
						this.app.notice(json.message, "error");
						flag = false;
					}
				}, function(){
					flag = false;
				}, false )
			}else if( this.topToForum === true || this.topToForum === "true" ){
				this.actions.cancelTopToForum( this.app.data.id , function( json ){
					if (json.type == "error") {
						this.app.notice(json.message, "error");
						flag = false;
					}
				}, function(){
					flag = false;
				}, false )
			}
			if( data.topToSection === true || data.topToSection === "true" ){
				this.actions.topToSection( this.app.data.id , function( json ){
					if (json.type == "error") {
						this.app.notice(json.message, "error");
						flag = false;
					}
				}, function(){
					flag = false;
				}, false )
			}else if( this.topToSection === true || this.topToSection === "true" ){
				this.actions.cancelTopToSection( this.app.data.id , function( json ){
					if (json.type == "error") {
						this.app.notice(json.message, "error");
						flag = false;
					}
				}, function(){
					flag = false;
				}, false )
			}
			if( flag ){
				this.formMarkNode.destroy();
				this.formAreaNode.destroy();
				this.app.notice( this.app.lp.setTopSuccess );
				this.fireEvent("postOk");
			}else{
				this.app.notice( this.app.lp.setToFail , "error");
			}
		}
	}
});
