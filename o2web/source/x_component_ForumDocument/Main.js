MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.xApplication.ForumDocument = MWF.xApplication.ForumDocument || {};
MWF.require("MWF.widget.O2Identity", null,false);
//MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Forum", "Common", null, false);
MWF.xDesktop.requireApp("Forum", "Attachment", null, false);
MWF.xDesktop.requireApp("Forum", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Forum", "Access", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
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
		"width": "1324",
		"height": "720",
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
		if( this.userCache ){
			for( var key in this.userCache ){
				delete this.userCache[key];
			}
		}
		this.userCache;
	},
	loadApplication: function(callback){
		this.userData = layout.desktop.session.user;
		this.userName = this.userData.distinguishedName;
		this.restActions = this.actions = MWF.Actions.get("x_bbs_assemble_control"); //new MWF.xApplication.Forum.Actions.RestActions();

		this.path = "/x_component_ForumDocument/$Main/"+this.options.style+"/";

		if( this.status ){
			this.setOptions( this.status )
		}

		if( this.options.isNew && !this.options.id ){
			if( this.options.advanceId ){
				this.advanceId = this.options.advanceId;
				this.createNode();
				this.loadApplicationContent();
			}else{
				this.actions.getUUID( function( id ){
					this.advanceId = id;
					this.createNode();
					this.loadApplicationContent();
				}.bind(this))
			}
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
		this.loadController(function(){
			this.access.login( function(){
				this.loadApplicationLayout();
			}.bind(this) )
		}.bind(this))
	},
	clearContent : function(){
		this.node.empty();
		this.pagingBarTop = null;
		this.pagingContainerTop = null;
		delete this.pagingBarTop;
		delete this.pagingContainerTop;
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
				this.restActions.getCategory(this.sectionData.forumId, function (forumData) {
					this.forumData = forumData.data;
					this.createTopNode( title );
					var tail = this.inBrowser ? (MWFForum.getSystemConfigValue( MWFForum.BBS_TITLE_TAIL ) || "") : "";
					this.setTitle( title + tail );
					this.createMiddleNode();
				}.bind(this));
				//}.bind(this) );
			}.bind(this))
		}.bind(this) )
	},
	createTopNode: function( title ){
		var node = new MWF.xApplication.Forum.TopNode(this.contentContainerNode, this, this, {
			type: this.options.style
		});
		this.topNode = node;
		node.load();

		//var forumSetting = MWF.xApplication.Forum.ForumSetting[this.sectionData.forumId];
		//var forumColor = forumSetting ? forumSetting.forumColor : "";

		var topNode = this.topNode = new Element("div.topNode", {
			"styles": this.css.topNode
		}).inject(this.contentContainerNode);

		var topTitleMiddleNode = new Element("div.topTitleMiddleNode", {
			"styles": this.css.topTitleMiddleNode
		}).inject(topNode);

		var topItemTitleNode = new Element("div.topItemTitleNode", {
			"styles": this.css.topItemTitleNode,
			"text": MWFForum.getBBSName() || MWF.xApplication.Forum.LP.title
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
		}.bind(this));

		var topItemSepNode = new Element("div.topItemSepNode", {
			"styles": this.css.topItemSepNode,
			"text" : ">"
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
		}.bind({ obj: this, forumId : this.sectionData.forumId }));

		var topItemSepNode = new Element("div.topItemSepNode", {
			"styles": this.css.topItemSepNode,
			"text" : ">"
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
		}.bind(this));

		var topItemSepNode = new Element("div.topItemSepNode", {
			"styles": this.css.topItemSepNode,
			"text" : ">"
		}).inject(topTitleMiddleNode);

		var topItemTitleNode = new Element("div.topItemTitleNode", {
			"styles": this.css.topItemTitleLastNode,
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
		this.data = this.data || {};
		var _self = this;
		this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleNode);

		var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
			"<tr>" +
			"   <td styles='formTableTitle' lable='title' width='10%' style='min-width:100px;'></td>" +
			"   <td styles='formTableValue' item='typeCategory' width='10%'></td>" +
			"   <td styles='formTableValue' item='type' width='10%'></td>" +
			"   <td styles='formTableValue' item='title' width='70%'></td>" +
			"</tr><tr>" +
			"   <td></td>" +
			"   <td item='tipNode' colspan='3'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableTitle' lable='summary'></td>" +
			"   <td styles='formTableValue' item='summary' colspan='3'></td>" +
			"</tr><tr item='portalImageTr' style='display:none'>" +
			"   <td styles='formTableTitle' lable='picId'></td>" +
			"   <td styles='formTableValue' colspan='3'><div item='picId' styles='portalImageAre' ></div></td>" +
			"</tr><tr>" +
			"   <td styles='formTableTitle' lable='content'></td>" +
			"   <td styles='formTableValue' item='content' colspan='3'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableTitle'>"+ this.lp.attachment +"</td>" +
			"   <td item='attachment' colspan='3'></td>" +
			"</tr><tr style='display:none' item='voteArea'>" +
			"   <td styles='formTableTitle'>"+ this.lp.vote +"</td>" +
			"   <td item='voteContainer' colspan='3'></td>" +
			"</tr><tr>" +
			"   <td styles='formTableTitle' lable=''></td>" +
			"   <td item='action' colspan='3'></td>" +
			"</tr>"+
			"</table>";
		this.contentDiv.set("html", html);
		var tipNode = this.contentDiv.getElement("[item='tipNode']");

		var typeSettings = this._loadTypeSetting();
		var typeSetting = typeSettings[ this.forumData.indexListStyle ];
		if( typeSetting.image ){
			this.contentDiv.getElements("[item='portalImageTr']")[0].setStyle("display","");
		}

		var subjectTypeSelectValue;
		if( this.sectionData.subjectType ){
			subjectTypeSelectValue = this.sectionData.subjectType.split("|");
		}else if( this.forumData.subjectType ){
			subjectTypeSelectValue = this.forumData.subjectType.split("|");
		}else{
			subjectTypeSelectValue = this.lp.subjectTypeDefaultValue.split("|");
		}
		var typeCategorySelectValue;
		if( this.sectionData.typeCategory ){
			typeCategorySelectValue = this.sectionData.typeCategory.split("|");
		}else if( this.forumData.typeCategory ){
			typeCategorySelectValue = this.forumData.typeCategory.split("|");
		}else{
			typeCategorySelectValue = this.lp.typeCategoryDefaultValue.split("|");
		}
		MWF.xDesktop.requireApp("Template", "MForm", function () {
			this.form = new MForm(this.contentDiv, this.data , {
				style: "forum",
				verifyType: "batch",
				isEdited:  true,
				itemTemplate: {
					title: {text: this.lp.subject, notEmpty : true,
						onPostLoad : function(item) {
							item.tipNode = tipNode;
						}
					},
					typeCategory :{ type : "select", selectValue : typeCategorySelectValue , notEmpty : true, event : {
						change : function(item, ev){
							if( item.getValue() == this.lp.vote ){
								this.contentDiv.getElements( "[item='voteArea']").setStyle("display","");
								this.loadVoteArea();
							}else{
								this.contentDiv.getElements( "[item='voteArea']").setStyle("display","none");
							}
						}.bind(this)}
					},
					type: {text: this.lp.type, type : "select", selectValue : subjectTypeSelectValue , notEmpty : true },
					summary: {text: this.lp.summary, type : "text", event : { "keyup" : function( item, ev){
						if( item.getValue().length > 70 ){
							item.setValue( item.getValue().substr( 0, 70 ) );
						}
					} } },
					picId: { text: this.lp.portalImage, type : "imageClipper",
						disable : !typeSetting.image,
						style : {
							imageStyle : this.css.portalImageNode,
							actionStyle : this.css.uploadActionNode
						},
						aspectRatio : 1.5,
						reference : this.advanceId || this.data.id,
						referenceType: "forumDocument"
					},
					content: {text: this.lp.content, type : "rtf", notEmpty : true, RTFConfig : {
						isSetImageMaxWidth : true,
						reference : this.advanceId || this.data.id,
						referenceType: "forumDocument"//,
						//skin : "bootstrapck" //,
						//filebrowserCurrentDocumentImage: function (e, callback) {
						//	_self.selectDocImage( callback );
						//}
					}}
				}
			}, this, this.css);
			this.form.load();
		}.bind(this), true);

		if( this.data.typeCategory == this.lp.vote ){
			this.contentDiv.getElement( "[item='voteArea']").setStyle("display","");
			this.loadVoteArea();
		}

		var actionTd = this.contentDiv.getElements("[item='action']")[0];
		this.saveAction = new Element("div",{
			styles : this.css.actionNode,
			text: this.lp.saveSubject
		}).inject(actionTd);
		this.saveAction.addEvent("click",function(ev){
			this.saveSubject( ev );
		}.bind(this));

		var attachmentArea = this.contentDiv.getElements("[item='attachment']")[0];
		this.loadAttachment(attachmentArea)
	},
	_loadTypeSetting: function(){
		var path = "/x_component_Forum/$ColumnTemplate/template/setting.json";
		var templateSetting;
		MWF.xApplication.Forum.ColumnTemplate = MWF.xApplication.Forum.ColumnTemplate || {};
		if (MWF.xApplication.Forum.ColumnTemplate.Setting){
			templateSetting = MWF.xApplication.Forum.ColumnTemplate.Setting;
		}else{
			var r = new Request.JSON({
				url: path,
				secure: false,
				async: false,
				method: "get",
				noCache: false,
				onSuccess: function(responseJSON, responseText){
					templateSetting = MWF.xApplication.Forum.ColumnTemplate.Setting = responseJSON;
				}.bind(this),
				onError: function(text, error){
					alert(error + text);
				}
			});
			r.send();
		}
		return templateSetting;
	},
	loadVoteArea : function(){
		this.voteContainer = this.contentDiv.getElement("[item='voteContainer']");
		MWF.xDesktop.requireApp("ForumDocument", "Vote", function(){
			this.vote = new MWF.xApplication.ForumDocument.Vote(this.voteContainer, this, {
				isNew : this.options.isNew,
				isEdited : this.options.isEdited
			}, this.data);
			this.vote.load();
		}.bind(this), true)
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
	saveSubject  : function(ev){
		var _self = this;
		var data = this.form.getResult(true, ",", true, false, true);
		if( !data ){ //校验没通过
			// 校验投票
			var typeCategory = this.form.getItem("typeCategory");
			if( typeCategory.getValue() == this.lp.vote ){
				this.vote.getVoteInfor()
			}
			return;
		}
		if( data.typeCategory == this.lp.vote ){
			var voteData = this.vote.getVoteInfor();
			if( !voteData )return;
			for( var key in voteData ){
				data[key] = voteData[key];
			}
			this.confirm("warn", ev, this.lp.confirmPublishVoteDocumentTitle, this.lp.confirmPublishVoteDocumentContent, 350, 120, function(){
				_self._saveSubject( data );
				this.close();
			}, function(){
				this.close();
			});
		}else{
			this._saveSubject( data );
		}
	},
	_saveSubject : function( data ){
		if( this.advanceId )data.id = this.advanceId;
		data.attachmentList = this.attachment.getAttachmentIds();
		if (data) {
			data.sectionId = this.sectionData.id;
			//data.picId = this.picId || "";
			this.restActions.saveSubject(data, function (json) {
				this.notice(this.options.isNew ? this.lp.createSuccess : this.lp.updateSuccess, "success");
				this.fireEvent("postPublish");

				this.reloadAllParents();

				var oldId = "ForumDocument"+ ( this.options.isNew ? this.sectionData.id : this.data.id );
				var appId = "ForumDocument"+json.data.id;
				this.advanceId = "";
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

		this.createSidebar();

		this.createPagingBar();
		this.createToolbar_read();

		var contentConainer = new Element("div.subjectConainer",{
			"styles" : this.css.contentConainer
		}).inject( this.middleNode );

		this.subjectConainer = new Element("div.subjectConainer",{
			"styles" : this.css.subjectConainer
		}).inject( contentConainer );

		if( this.data.typeCategory == this.lp.question ){
			this.satisfiedReplyViewConainer = new Element("div.satisfiedReplyViewConainer",{
				"styles" : this.css.replyViewConainer
			}).inject( contentConainer );
		}

		this.replyViewConainer = new Element("div.replyViewConainer",{
			"styles" : this.css.replyViewConainer
		}).inject( contentConainer );

		this.createPagingBar();

		this.createSubject();
		if( this.data.typeCategory == this.lp.question ) {
			if( this.data.acceptReplyId ){
				this.createSatisfiedReplyView();
			}
		}
		this.createReplyView();
		if( !this.data.stopReply && this.isReplyPublisher ){
			if( this.access.isAnonymous() ){
				this.createReplyEditor_Anonymous()
			}else{
				this.createReplyEditor();
			}
		}
		//this.createTurnSubjectNode();

	},
	createPagingBar: function(){
		var pagingArea = new Element("div",{
			styles : this.css.pagingArea
		}).inject(this.middleNode);
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
			"id" : this.data ? this.data.id : "",
			"advanceId" : this.advanceId,
			"appId": ( this.data && this.data.id ) ? "ForumDocument"+this.data.id : "ForumDocument"+this.advanceId,
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
		});
		this.attachment.load();
	},
	createToolbar_read : function(){
		this.toolBarReadTop = new Element("div.toolBarReadTop",{
			"styles" : this.css.toolBarReadTop
		}).inject( this.middleNode );

		this.toolBarRead = new Element("div.toolBarRead",{
			"styles" : this.css.toolBarRead
		}).inject( this.middleNode );

		this.toolbarLeft = new Element("div.toolbarLeft",{
			"styles" : this.css.toolbarLeft
		}).inject( this.toolBarRead );

		var toolbarLeftItem = new Element("div.toolbarViewItem",{
			"styles" : this.css.toolbarViewItem
		}).inject( this.toolbarLeft );
		new Element("span.toolbarLeftTextItem",{
			"styles" : this.css.toolbarLeftTextItem,
			"text" : this.lp.readed + "："
		}).inject( toolbarLeftItem );
		new Element("span.toolbarLeftCountItem",{
			"styles" : this.css.toolbarLeftCountItem,
			"text" : this.data.viewTotal
		}).inject( toolbarLeftItem );

		new Element("div.toolbarSepItem",{
			"styles" : this.css.toolbarSepItem
		}).inject( this.toolbarLeft );

		var toolbarLeftItem = new Element("div.toolbarReplyItem",{
			"styles" : this.css.toolbarReplyItem
		}).inject( this.toolbarLeft );
		new Element("span.toolbarLeftTextItem",{
			"styles" : this.css.toolbarLeftTextItem,
			"text" : this.lp.reply + "："
		}).inject( toolbarLeftItem );
		this.replyTotal = new Element("span.toolbarLeftCountItem",{
			"styles" : this.css.toolbarLeftCountItem,
			"text" : this.data.replyTotal
		}).inject( toolbarLeftItem );

		this.toolbarRight = new Element("div.toolbarRight",{
			"styles" : this.css.toolbarRight
		}).inject( this.toolBarRead );

		//this.createActionBar();
		if(this.data.isTopSubject){
			new Element( "div.top", {
				"styles" : this.css.toolbarZhiding,
				"title" : this.lp.setTop
			}).inject( this.toolbarRight );
		}else if( this.data.isCreamSubject ){
			new Element( "div.prime", {
				"styles" : this.css.toolbarPrime,
				"title" : (this.data.screamSetterName || "").split("@")[0]+ this.lp.at + this.data.screamSetterTime + this.lp.setPrime
			}).inject( this.toolbarRight );
		}else if( this.data.typeCategory == this.lp.vote ){
			new Element( "div.vote", { "styles" : this.css.toolbarVote, "title" : this.lp.vote }).inject( this.toolbarRight );
		}else if( this.data.typeCategory == this.lp.question ){
			new Element( "div.question", { "styles" : this.css.toolbarQuestion, "title" : this.lp.question }).inject( this.toolbarRight );
		}

		this.toolbarRightTitle = new Element("div.toolbarRightTitle",{
			"styles" : this.css.toolbarRightTitle,
			"text" : "["+ this.data.type +"]"+this.data.title
		}).inject( this.toolbarRight );

		this.toolbarRightTools = new Element("div.toolbarRightTools",{
			"styles" : this.css.toolbarRightTools
		}).inject( this.toolbarRight );

		if( this.nextSubject ){
			this.toolbarNext = new Element("div.toolbarNext",{
				"styles" : this.css.toolbarNext,
				"title" : this.lp.nextSubject + "：" + this.nextSubject.title
			}).inject( this.toolbarRightTools );

			this.toolbarNext.addEvents({
				"click" : function(){ this.gotoDocument( 1 ); }.bind(this),
				"mouseover" : function(){
					this.toolbarNext.setStyles( this.css.toolbarNext_over );
				}.bind(this),
				"mouseout" : function(){
					this.toolbarNext.setStyles( this.css.toolbarNext );
				}.bind(this)
			})
		}

		if( this.lastSubject ){
			this.toolbarPrev = new Element("div.toolbarRightTools",{
				"styles" : this.css.toolbarPrev,
				"title" : this.lp.prevSubject + "：" + this.lastSubject.title
			}).inject( this.toolbarRightTools );
			this.toolbarPrev.addEvents({
				"click" : function(){ this.gotoDocument( -1 ); }.bind(this),
				"mouseover" : function(){
					this.toolbarPrev.setStyles( this.css.toolbarPrev_over );
				}.bind(this),
				"mouseout" : function(){
					this.toolbarPrev.setStyles( this.css.toolbarPrev );
				}.bind(this)
			})
		}

	},
	adjustReplyCount: function( count ){
		this.data.replyTotal = this.data.replyTotal + count;
		this.replyTotal.set("text", this.data.replyTotal )
	},
	createNewDocument: function(){
		var _self = this;
		var appId = "ForumDocument"+this.sectionData.id;
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
				_self.notice( _self.lp.deleteDocumentOK, "ok");
				_self.reloadAllParents();
				_self.close();
			}.bind(this) );
			this.close();
		}, function(){
			this.close();
		});
	},
	postCreateReply : function( id ){
		this.restActions.getReply( id, function( json ){
			var reply = this.replyView._createDocument( json.data );
			this.adjustReplyCount( 1 );
			var t = reply.node.getTop() - this.contentContainerNode.getCoordinates().top + this.contentContainerNode.scrollTop.toFloat();
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
		});
		form.mainData = this.data;
		form.create()
	},
	createActionBar : function( container ){
		this.actionBar = new Element("div", { "styles" : this.css.actionBar, "html" : "&nbsp;"}).inject(container);

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


		if( this.permission.manageAble ){

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
				"text" : this.lp.moveto
			}).inject( this.actionBar );
			action.setStyle("background-image" , "url("+this.path+"icon/action_moveto.png)");
			action.addEvents({
				"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
				"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
				"click" : function(){ this.moveTo() }.bind(this)
			});


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
			});
			//}

		}

		if( MWF.AC.isHotPictureManager() ){
			action = new Element("div", {
				"styles" : this.css.actionItem,
				"text" : this.lp.setHot
			}).inject( this.actionBar );
			action.setStyle("background-image" , "url("+this.path+ "icon/action_popular.png" + ")");
			action.addEvents({
				"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
				"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
				"click" : function(){ this.setHotPicture() }.bind(this)
			});
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
			}else if( this.sectionData.sectionVisible == this.lp.allPerson && this.sectionData.indexRecommendable == true  ){
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


		if( this.permission.manageAble || this.permission.editAble || this.data.creatorName == this.userName ){
			action = new Element("div", {
				"styles" : this.css.actionItem,
				"text" : this.lp.delete
			}).inject( this.actionBar );
			action.setStyle("background-image" , "url("+this.path+"icon/action_delete.png)");
			action.addEvents({
				"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.actionItem_over ) }.bind({ obj : this, itemNode : action }),
				"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.actionItem ) }.bind({ obj : this, itemNode : action }),
				"click" : function(ev){ this["delete"](ev) }.bind(this)
			})
		}

		if( this.data.typeCategory != this.lp.vote ){
			if( this.permission.manageAble || this.permission.editAble || this.data.creatorName == this.userName ){
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

	},
	lock : function(){
		this.restActions.lock( this.data.id, function(){
			this.notice( this.lp.lockSuccess );
			this.reload();
		}.bind(this))
	},
	unlock : function(){
		this.restActions.unlock( this.data.id, function(){
			this.notice( this.lp.unlockSuccess );
			this.reload();
		}.bind(this))
	},
	setRecommend : function(){
		this.restActions.setRecommend( this.data.id, function(){
			this.notice( this.lp.setRecommendSuccess );
			this.reload();
		}.bind(this))
	},
	cancelRecommend : function(){
		this.restActions.cancelRecommend( this.data.id, function(){
			this.notice( this.lp.cancelRecommendSuccess );
			this.reload();
		}.bind(this))
	},
	setHotPicture : function(){
		MWF.xDesktop.requireApp("ForumDocument", "HotLinkForm", null, false);

		var form = new MWF.xApplication.ForumDocument.HotLinkForm(this, this.data, {
			documentId : this.data.id,
			summary : this.data.summary,
			onPostOk : function( id ){

			}.bind(this)
		},{
			app : this, lp : this.lp, css : this.css, actions : this.restActions
		});
		form.create()
	},
	setTop : function(){
		var form = new MWF.xApplication.ForumDocument.TopSettingForm(this, this.data, {
			onPostOk : function( id ){
				this.reload();
			}.bind(this)
		},{
			app : this, lp : this.lp, css : this.css, actions : this.restActions
		});
		form.create();
		//this.restActions.topToSection( this.data.id, function(){
		//	this.notice( this.lp.setTopSuccess )
		//	this.reload();
		//}.bind(this))
	},
	cancelTop : function(){
		this.restActions.cancelTopToSection( this.data.id, function(){
			this.notice( this.lp.cancelTopSuccess );
			this.reload();
		}.bind(this))
	},
	setPrime : function(){
		this.restActions.setCream( this.data.id, function(){
			this.notice( this.lp.setPrimeSuccess );
			this.reload();
		}.bind(this))
	},
	cancelPrime : function(){
		this.restActions.cancelCream( this.data.id, function(){
			this.notice( this.lp.cancelPrimeSuccess );
			this.reload();
		}.bind(this))
	},
	createSubject : function(){
		this.subjectView = new MWF.xApplication.ForumDocument.SubjectView( this.subjectConainer, this, this, {
			templateUrl : this.path + "listItemSubject.json",
			scrollEnable : false
		} );
		this.subjectView.data = this.data;
		this.subjectView.load();
	},
	moveTo : function(){
		MWF.xDesktop.requireApp("Forum", "SectionSelector", null, false);
		var selector = new MWF.xApplication.Forum.SectionSelector(this.content, {
			"count": 1,
			"title": "选择移动到的版块",
			"values": [],
			"onComplete": function( array ){
				if( typeOf( array ) == "array" ){
					var sectionId = array[0].data.id;
					this.restActions.changeSection( {"subjectIds":[ this.data.id ],"sectionId" : sectionId }, function(){
						this.notice( "帖子已经移动到"+array[0].data.name );
						this.reload();
					}.bind(this))
				}
			}.bind(this)
		} );
		selector.load();
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
		authentication.openLoginForm({
			hasMask : true
		});
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
		authentication.openSignUpForm({
			hasMask : true
		});
	},
	gotoReply : function( index ){
		this.replyView.paging.gotoItem( index );
	},
	createSatisfiedReplyView : function( ){
		this.satisfiedReplyView = new MWF.xApplication.ForumDocument.SatisfiedReplyView( this.satisfiedReplyViewConainer, this, this, {
			templateUrl : this.path + "listItemSatisfied.json",
			scrollEnable : false
		} );
		this.satisfiedReplyView.data = this.data;
		this.satisfiedReplyView.load();
	},
	createReplyView : function( ){

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
		} );
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
		}).inject( this.replyArea );

		var replyPicture = new Element("div.replyPicture",{
			"styles" : this.css.replyPicture
		}).inject( this.replyArea );

		var needloginNode = new Element("div.replyNeedLogin",{
			"styles" : this.css.replyNeedLogin
		}).inject(replyPicture);

		new Element("div.replyNeedLogin",{
			"styles" : this.css.replyNeedLoginText,
			"text" : this.lp.replyNeedLoginText
		}).inject(needloginNode);

		var loginNode = new Element("div.replyLoginAction",{
			"styles" : this.css.replyLoginAction,
			"text" : this.lp.login
		}).inject(needloginNode);
		loginNode.addEvent("click" , function(){
			this.openLoginForm(
				function(){ this.reload() }.bind(this)
			)
		}.bind(this));

		if( this.access.signUpMode != "disable" ){
			new Element("div.replyNeedLogin",{
				"styles" : this.css.replyNeedLoginText,
				"text" : "|"
			}).inject(needloginNode);

			var signupNode = new Element("div.replyLoginAction",{
				"styles" : this.css.replyLoginAction,
				"text" : this.lp.signUp
			}).inject(needloginNode);
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
		} );
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
	},
	createSidebar: function(){
		if( this.inBrowser ){
			var crd = this.middleNode.getCoordinates();
			this.sideBar = new Element("div.sideBar", {
				styles : {
					"position" : "fixed",
					"left" : (crd.right+4)+"px",
					"bottom" : "100px",
					"width" : "50px",
					"height" : "155px",
					"padding-top" : "10px",
					"text-align" : "center",
					"background-color" : "#fff",
					"box-shadow": "0 0 4px rgba(0,0,0,0.20)"
				}
			}).inject( this.middleNode );
			window.onresize = function(){
				var crd = this.middleNode.getCoordinates();
				this.sideBar.setStyles( {
					"left" : (crd.right+4)+"px"
				})
			}.bind(this)
		}else{
			var contentCrd = this.content.getCoordinates();
			var middleNodeCrd = this.middleNode.getCoordinates();
			this.sideBar = new Element("div.sideBar", {
				styles : {
					"position" : "fixed",
					"top" : (contentCrd.top + contentCrd.height-220)+"px",
					"left" : (middleNodeCrd.right+4)+"px",
					"width" : "50px",
					"height" : "155px",
					"padding-top" : "10px",
					"text-align" : "center",
					"background-color" : "#fff",
					"box-shadow": "0 0 4px #ccc"
				}
			}).inject( this.middleNode );
			this.addEvent("moveDrop", function(){
				var contentCrd = this.content.getCoordinates();
				var middleNodeCrd = this.middleNode.getCoordinates();
				this.sideBar.setStyles( {
					"top" : (contentCrd.top + contentCrd.height-220)+"px",
					"left" : (middleNodeCrd.right+4)+"px"
				})
			}.bind(this));
			this.addEvent("resize", function(){
				var contentCrd = this.content.getCoordinates();
				var middleNodeCrd = this.middleNode.getCoordinates();
				this.sideBar.setStyles( {
					"top" : (contentCrd.top + contentCrd.height-220)+"px",
					"left" : (middleNodeCrd.right+4)+"px"
				})
			}.bind(this));
		}
		this._createSidebar();
	},
	_createSidebar: function(){
		var count = 1;
		var sidebarTop = new Element("div",{
			styles : this.css.sidebarTop,
			title: this.lp.gotoTop
		}).inject(this.sideBar);
		sidebarTop.addEvents(
			{
				"mouseover": function () {
					this.node.setStyles(this.obj.css.sidebarTop_over);
				}.bind({obj: this, node: sidebarTop}),
				"mouseout": function () {
					this.node.setStyles(this.obj.css.sidebarTop);
				}.bind({obj: this, node: sidebarTop}),
				"click": function () {
					this.contentContainerNode.scrollTo( 0, 0 );
				}.bind(this)
			}
		);

		if( this.sectionPermission.subjectPublishAble ){
			count++;
			var createActionNode = new Element("div",{
				styles : this.css.sidebarCreate,
				title: this.lp.createSubject
			}).inject(this.sideBar);
			createActionNode.addEvents(
				{
					"mouseover": function () {
						this.node.setStyles(this.obj.css.sidebarCreate_over);
					}.bind({obj: this, node: createActionNode}),
					"mouseout": function () {
						this.node.setStyles(this.obj.css.sidebarCreate);
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

		if( !this.data.stopReply ){
			if( this.isReplyPublisher ){
				count++;
				var action = new Element("div", {
					"styles" : this.css.sidebarReply,
					"title" : this.lp.reply
				}).inject( this.sideBar );
				action.addEvents({
					"mouseover" : function(){ this.itemNode.setStyles( this.obj.css.sidebarReply_over ) }.bind({ obj : this, itemNode : action }),
					"mouseout" : function(){ this.itemNode.setStyles( this.obj.css.sidebarReply ) }.bind({ obj : this, itemNode : action }),
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

		var container = new Element("div",{}).inject( this.sideBar );
		if( this.nextSubject ){
			count++;
			this.sidebarNext = new Element("div.sidebarNext",{
				"styles" : this.css.sidebarNext,
				"title" : this.lp.nextSubject + "：" + this.nextSubject.title
			}).inject( container );

			this.sidebarNext.addEvents({
				"click" : function(){ this.gotoDocument( 1 ); }.bind(this),
				"mouseover" : function(){
					this.sidebarNext.setStyles( this.css.sidebarNext_over );
				}.bind(this),
				"mouseout" : function(){
					this.sidebarNext.setStyles( this.css.sidebarNext );
				}.bind(this)
			})
		}

		if( this.lastSubject ){
			count++;
			this.sidebarPrev = new Element("div.sidebarPrev",{
				"styles" : this.css.sidebarPrev,
				"title" : this.lp.prevSubject + "：" + this.lastSubject.title
			}).inject( container );
			this.sidebarPrev.addEvents({
				"click" : function(){ this.gotoDocument( -1 ); }.bind(this),
				"mouseover" : function(){
					this.sidebarPrev.setStyles( this.css.sidebarPrev_over );
				}.bind(this),
				"mouseout" : function(){
					this.sidebarPrev.setStyles( this.css.sidebarPrev );
				}.bind(this)
			})
		}

		this.sideBar.setStyle( "height" , (count * 30 + 5 ) +"px" );

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
			});
			if( i != persons.length - 1 ){
				new Element("span", {
					"text" : ","
				}).inject(container);
			}
		}.bind(this))
	},
	getUserData : function( name, callback ){
		if( this.userCache && this.userCache[name] ){
			if( callback )callback( this.userCache[name] );
			return
		}
		if( !this.userCache )this.userCache = {};
		if( this.access.isAnonymous() ){
			var url = MWF.Actions.get("x_organization_assemble_personal").getIcon(name);
			if( url ){
				var json =  { data : { icon : url } };
				this.userCache[ name ] = json;
				if( callback )callback( json );
			}else{
				var json =  { data : { icon : "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif" } };
				this.userCache[ name ] = json;
				if( callback )callback( json );
			}
			//MWF.Actions.get("x_organization_assemble_control").getPersonIcon(name, function(url){
			//	var json =  { data : { icon : url } };
			//	this.userCache[ name ] = json;
			//	if( callback )callback( json );
			//}.bind(this), function(){
			//	var json =  { data : { icon : "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif" } };
			//	this.userCache[ name ] = json;
			//	if( callback )callback( json );
			//}.bind(this))
		}else{
			MWF.Actions.get("x_organization_assemble_control").getPerson( function( json ){
				if( !json.data )json.data = {};
				var url = MWF.Actions.get("x_organization_assemble_personal").getIcon(name);
				if( url ){
					if( json.data ){
						json.data.icon = url;
						this.userCache[ name ] = json;
						if( callback )callback( json );
					}
				}else{
					if( json.data ){
						json.data.icon = "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif";
						this.userCache[ name ] = json;
						if( callback )callback( json );
					}
				}
				//MWF.Actions.get("x_organization_assemble_control").getPersonIcon(name, function(url){
				//	if( json.data ){
				//		json.data.icon = url;
				//		this.userCache[ name ] = json;
				//		if( callback )callback( json );
				//	}
				//}.bind(this), function(){
				//	if( json.data ){
				//		json.data.icon = "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif";
				//		this.userCache[ name ] = json;
				//		if( callback )callback( json );
				//	}
				//}.bind(this));
			}.bind(this), function(){
				var json =  { data : { icon : "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif" } };
				this.userCache[ name ] = json;
				if( callback )callback( json );
			}.bind(this), name, true )
		}
	}
});

MWF.xApplication.ForumDocument.SubjectView = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	_createDocument: function(data, index){
		data.index = index;
		var document;
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
				document = new MWF.xApplication.ForumDocument.SubjectDocument(this.viewNode, data, this.explorer, this, null, data.index );
			}.bind(this))
		}.bind(this) );
		return document;
	},
	getUserData : function( name, callback ){
		this.app.getUserData( name, callback );
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
		var toolbar = itemNode.getElement("[item='itemSubjectTools']");
		this.app.createActionBar(toolbar);

		if( this.data.attachmentList && this.data.attachmentList.length > 0 ){
			var attachmentArea = itemNode.getElement("[item='attachment']");
			this.app.loadAttachment(attachmentArea);
		}

		if( this.data.typeCategory == this.lp.vote ){
			var voteArea = itemNode.getElement("[item='vote']");
			MWF.xDesktop.requireApp("ForumDocument", "Vote", function(){
				this.vote = new MWF.xApplication.ForumDocument.Vote(voteArea, this.app, {
					isNew : false,
					isEdited : false
				}, this.data);
				this.vote.load();
			}.bind(this), true)
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
});

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
			"     <div styles='itemBodyReply' item='content'></div>" +
			"     <div styles='itemBodyReply' item='action'></div>" +
			"   </div>" +
			" </div>" +
			"</div>";
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
				isEdited:  true,
				itemTemplate: {
					userIcon: { className : "itemUserIcon2", type : "img", value : function(){
						if( this.app.userData.icon ){
							return "data:image/png;base64," + this.app.userData.icon
						}else{
							return "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif"
						}
					}.bind(this)},
					creatorName: { type : "innerText", value : ( this.app.userName || "" ).split('@')[0] },
					content: { type : "rtf", RTFConfig : {
						//skin : "bootstrapck",
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
					this.app.restActions.getUUID( function( id ){
						this.advanceReplyId = id;
					}.bind(this));
					this.app.notice( this.app.lp.saveReplySuccess, "ok" );
					this.form.getItem("content").setValue("");
					this.fireEvent("postOk", json.data.id);
				}
			}.bind(this))
		}
	}
});

MWF.xApplication.ForumDocument.ReplyForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"width": "860",
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
			"</tr>"+
			"</table>";
		this.formTableArea.set("html", html);

		if( !this.options.toMain && this.parentData ){
			var mainContentEl = this.formTableArea.getElements("[item='mainContent']")[0];

			var quoteTop = new Element( "div", {styles : this.css.quoteTop} ).inject( mainContentEl );
			new Element( "div", {styles : this.css.quoteLeft} ).inject( quoteTop );
			new Element( "div", {
				styles : this.css.quoteInfor,
				text : this.parentData.creatorName.split("@")[0] + this.lp.publishAt + this.parentData.createTime
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
				isEdited: true,
				itemTemplate: {
					mainSubject: { type: "innertext", defaultValue : "RE:" + this.mainData.title },
					content: { type : "rtf", RTFConfig : {
						//skin : "bootstrapck",
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
					if(this.formMaskNode)this.formMaskNode.destroy();
					this.formAreaNode.destroy();
					this.app.notice(this.isNew ? this.app.lp.createReplySuccess : this.app.lp.updateSuccess, "success");
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
			if( this.app.satisfiedReplyViewConainer )this.app.satisfiedReplyViewConainer.setStyle("display","block");
		}else{
			this.app.subjectConainer.setStyle("display","none");
			if( this.app.satisfiedReplyViewConainer )this.app.satisfiedReplyViewConainer.setStyle("display","none");
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
		this.app.getUserData( name, callback );
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
		}.bind(this));

		if( itemData.parentId && itemData.parentId != "" ){
			var quoteContainer = itemNode.getElements( "[item='quoteContent']" )[0];
			this.actions.getReply( itemData.parentId, function( json ){
					var data = this.parentData =  json.data;
					var quoteContent = new Element("div", {  "styles" : this.css.itemQuote }).inject(quoteContainer);
					var content = quoteContent.set("html", data.content).get("text");
					quoteContent.empty();
					data.contentText = content;

					new Element( "div", {styles : this.css.quoteLeftBig} ).inject( quoteContent );

					var quoteArea = new Element( "div", {styles : this.css.quoteAreaBig } ).inject( quoteContent );
					var quoteInfor = new Element( "div", {
						styles : this.css.quoteInforBig,
						text : data.orderNumber + this.lp.floor + "：" + data.creatorName.split('@')[0] + this.lp.publishAt + data.createTime
					}).inject( quoteArea );
					quoteInfor.addEvent("click", function(){
						this.obj.app.gotoReply( this.index )
					}.bind({obj : this, index : data.orderNumber || (data.index + 2) }));
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
			});
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
		});
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
			}.bind(this) );
			this.close();
		}, function(){
			this.close();
		});
	},
	satisfiedAction : function(){
		this.actions.acceptReply({"id": this.data.id }, function(){
			this.app.notice( this.lp.acceptReplySuccess, "ok");
			this.app.reload();
		}.bind(this))
	}
});

MWF.xApplication.ForumDocument.SatisfiedReplyView = new Class({
	Extends: MWF.xApplication.ForumDocument.ReplyView,
	_createDocument: function (data, index) {
		data.index = index;
		return new MWF.xApplication.ForumDocument.SatisfiedReplyDocument(this.viewNode, data, this.explorer, this, null, data.index);
	},
	_getCurrentPageData: function(callback, count, pageNum){
		this.clearBody();
		if(!count)count=1;
		if(!pageNum)pageNum = 1;
		var filter = this.filterData || {};
		this.actions.getAcceptedReply( this.data.acceptReplyId, function(json){
			if( !json.data ){
				json.data = [];
			}else if( typeOf( json.data ) == "object" ){
				json.data = [ json.data ];
				json.count = 1;
			}
			if( !json.count )json.count=0;
			if( callback )callback(json);
		}.bind(this))
	}
});

MWF.xApplication.ForumDocument.SatisfiedReplyDocument = new Class({
	Extends: MWF.xApplication.ForumDocument.ReplyDocument
});

MWF.xApplication.ForumDocument.TopSettingForm = new Class({
	Extends: MPopupForm,
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
			}).inject(this.formTopNode);

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
			"</tr>"+
			"</table>";
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
				if(this.formMaskNode)this.formMaskNode.destroy();
				this.formAreaNode.destroy();
				this.app.notice( this.app.lp.setTopSuccess );
				this.fireEvent("postOk");
			}else{
				this.app.notice( this.app.lp.setToFail , "error");
			}
		}
	}
});
