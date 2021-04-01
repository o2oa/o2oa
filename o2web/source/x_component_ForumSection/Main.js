MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.xApplication.ForumSection = MWF.xApplication.ForumSection || {};
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xDesktop.requireApp("Forum", "Common", null, false);
//MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Forum", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Forum", "Access", null, false);
MWF.xDesktop.requireApp("Forum", "TopNode", null, false);
MWF.xApplication.ForumSection.options = {
	multitask: true,
	executable: true
};
MWF.xApplication.ForumSection.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "ForumSection",
		"icon": "icon.png",
		"width": "1230",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.ForumSection.LP.title,
		"hasTop" : true,
		"naviMode" : false,
		"hasBreadCrumb" : true,
		"autoWidth" : false,
		"sectionId" : ""
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Forum.LP;
	},
	onQueryClose : function(){
		this.clearContent();
	},
	loadApplication: function(callback){
		this.userName = layout.desktop.session.user.distinguishedName;
		this.restActions = MWF.Actions.get("x_bbs_assemble_control"); //new MWF.xApplication.Forum.Actions.RestActions();

		this.path = "../x_component_ForumSection/$Main/"+this.options.style+"/";
		this.createNode();
		this.loadApplicationContent();
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
	clearContent: function(){
		this.node.empty();
	},
	loadApplicationContent: function(){
		if( !this.options.sectionId && this.status && this.status.sectionId ){
			this.options.sectionId = this.status.sectionId;
		}
		this.loadController(function(){
			this.access.login( function () {
				this.loadApplicationLayout();
			}.bind(this))
		}.bind(this))
	},
	reloadAllParents : function(){
		var aid = "Forum";
		if (this.desktop.apps[aid] && this.desktop.apps[aid].reload){
			this.desktop.apps[aid].reload();
		}

		aid = "ForumCategory"+this.sectionData.forumId;
		if (this.desktop.apps[aid] && this.desktop.apps[aid].reload){
			this.desktop.apps[aid].reload();
		}
	},
	reload : function(){
		this.status = {
			sectionId : this.options.sectionId,
			viewPageNum : this.explorer.view.getCurrentPageNum(),
			noteHidden : this.noteNodeHidden
		};
		this.clearContent();

		this.contentContainerNode = new Element("div.contentContainerNode", {
			"styles": this.options.autoWidth ? this.css.contentContainerNode_inContainer : this.css.contentContainerNode
		}).inject(this.node);
		this.createTopNode();
		this.createMiddleNode();
	},
	loadApplicationLayout : function(){
		this.contentContainerNode = new Element("div.contentContainerNode", {
			"styles": this.options.autoWidth ? this.css.contentContainerNode_inContainer : this.css.contentContainerNode
		}).inject(this.node);

		if( this.options.sectionId ){
			this.restActions.listSectionPermission( this.options.sectionId, function( permission ){
				this.sectionPermission = permission.data;
				this.isAdmin = this.sectionPermission.subjectManageAble;
				this.restActions.getSection( this.options.sectionId, function( json ){
					this.sectionData = json.data;
					//this.access.hasSectionAdminAuthority( this.sectionData, function( flag ){
					//this.isAdmin = flag;
					var tail = this.inBrowser ? (MWFForum.getSystemConfigValue( MWFForum.BBS_TITLE_TAIL ) || "") : "";
					this.setTitle( this.sectionData.sectionName + tail );
					this.createTopNode();
					this.createMiddleNode();
					//}.bind(this) );
				}.bind(this) )
			}.bind(this) )
		}else{
			this.close()
		}
	},
	loadTopObject : function(naviMode){
		if( this.options.hasTop ){
			var node = this.topObject = new MWF.xApplication.Forum.TopNode(this.contentContainerNode, this, this, {
				type: this.options.style,
				naviModeEnable : true,
				naviMode : naviMode
			});
			node.load();
		}
	},
	createTopNode: function(){
		this.loadTopObject( false );

		if( this.options.hasBreadCrumb ){
			var topNode = this.topNode = new Element("div.topNode", {
				"styles": this.options.autoWidth ? this.css.topNode_inContainer : this.css.topNode
			}).inject(this.contentContainerNode);
			//topNode.setStyle("border-bottom","1px solid "+forumColor);

			var topTitleMiddleNode = this.breadCrumbNode = new Element("div.topTitleMiddleNode", {
				"styles": this.css.topTitleMiddleNode
			}).inject(topNode);
			//topTitleMiddleNode.setStyle( "background-color" , forumColor )

			var topItemTitleNode = new Element("div.topItemTitleNode", {
				"styles": this.css.topItemTitleNode,
				"text": MWFForum.getBBSName() || MWF.xApplication.Forum.LP.title
			}).inject(topTitleMiddleNode);
			topItemTitleNode.addEvent("click", function(){
				if( this.options.naviMode && this.forumNavi ){
					this.forumNavi.goto( MWFForum.NaviType.main )
				}else{
					var appId = "Forum";
					if (this.desktop.apps[appId]){
						this.desktop.apps[appId].setCurrent();
					}else {
						this.desktop.openApplication(null, "Forum", { "appId": appId });
					}
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
				if( this.obj.options.naviMode && this.obj.forumNavi ){
					this.obj.forumNavi.goto( MWFForum.NaviType.category, this.forumId )
				}else{
					var appId = "ForumCategory"+this.forumId;
					if (this.obj.desktop.apps[appId]){
						this.obj.desktop.apps[appId].setCurrent();
					}else {
						this.obj.desktop.openApplication(null, "ForumCategory", { "categoryId" : this.forumId ,"appId": appId });
					}

					if( !this.obj.inBrowser ){
						this.obj.close();
					}
				}
			}.bind({ obj: this, forumId : this.sectionData.forumId }));

			topItemSepNode = new Element("div.topItemSepNode", {
				"styles": this.css.topItemSepNode,
				"text" : ">"
			}).inject(topTitleMiddleNode);

			topItemTitleNode = new Element("div.topItemTitleNode", {
				"styles": this.css.topItemTitleLastNode,
				"text": this.sectionData.sectionName
			}).inject(topTitleMiddleNode);
		}

		if( this.sectionData.sectionNotice ){
			var topRightNode = new Element("div", {
				"styles": this.css.topRightNode
			}).inject(topNode);

			topRightNode.addEvents({
				"click" :function(){
					if( !this.noteNodeHidden ){
						this.noteNode.setStyle("display","none");
						this.topRightIconNode.setStyles(this.css.topRightIconDownNode);
						this.noteNodeHidden = true;
					}else{
						this.noteNode.setStyle("display","");
						this.topRightIconNode.setStyles(this.css.topRightIconNode);
						this.noteNodeHidden = false;
					}
				}.bind(this)
			});

			var topRightIconNode = this.topRightIconNode = new Element("div", {
				"styles": this.css.topRightIconNode
			}).inject(topRightNode);

			if( this.status && this.status.noteHidden ){
				this.topRightIconNode.setStyles(this.css.topRightIconDownNode);
				this.noteNodeHidden = true;
			}
		}
	},
	createMiddleNode: function(){
		this.middleNode = new Element("div.middleNode", {
			"styles": this.options.autoWidth ? this.css.middleNode_inContainer : this.css.middleNode
		}).inject(this.contentContainerNode);

		this.createSectionNode();
		this.createNoteNode();
		this._createMiddleNode();

		if( !this.options.naviMode ){
			this.addEvent("resize", function () {
				this.setContentSize();
			}.bind(this));
			this.setContentSize();
		}

		//MWF.require("MWF.widget.ScrollBar", function () {
		//	new MWF.widget.ScrollBar(this.contentContainerNode, {
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
	},
	openNavi: function () {
		MWF.xDesktop.requireApp("Forum", "NaviMode", null, false);
		this.clearContent();
		this.contentContainerNode = new Element("div.contentContainerNode", {
			"styles": this.css.contentContainerNode
		}).inject(this.node);
		this.loadTopObject( true );
		this.middleNode = new Element("div.middleNode", {
			"styles": this.css.middleNode
		}).inject(this.contentContainerNode);
		this.contentNode = new Element("div.contentNode", {
			"styles": this.css.contentNode
		}).inject(this.middleNode);

		this.navi = new MWF.xApplication.Forum.NaviMode(this, this.contentNode, {
			type : MWFForum.NaviType.section,
			id : this.sectionData.id
		});
		this.navi.load();
	},
	closeNavi : function(){
		if( this.navi )this.navi.close();
	},
	colorRgba : function( color, opacity ){ /*16进制颜色转为RGB格式*/
		var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
		var sColor = color.toLowerCase();
		if(sColor && reg.test(sColor)){
			if(sColor.length === 4){
				var sColorNew = "#";
				for(var i=1; i<4; i+=1){
					sColorNew += sColor.slice(i,i+1).concat(sColor.slice(i,i+1));
				}
				sColor = sColorNew;
			}
			//处理六位的颜色值
			var sColorChange = [];
			for(var i=1; i<7; i+=2){
				sColorChange.push(parseInt("0x"+sColor.slice(i,i+2)));
			}
			return "rgba(" + sColorChange.join(",") + "," + opacity + ")";
		}else{
			return sColor;
		}
	},
	createSectionNode: function(){
		var forumColor = MWF.xApplication.Forum.ForumSetting[this.sectionData.forumId].forumColor;

		var sectionNode = new Element("div.sectionNode", {
			"styles": this.options.autoWidth ? this.css.sectionNode_inContainer : this.css.sectionNode
		}).inject(this.middleNode);
		sectionNode.setStyle("background-color", this.colorRgba( forumColor, "0.05" ));

		var sectionLeftNode = new Element("div.sectionLeftNode", {
			"styles": this.css.sectionLeftNode
		}).inject(sectionNode);
		var sectionLeftIconNode = new Element("div.sectionLeftIconNode", {
			"styles": this.css.sectionLeftIconNode
		}).inject(sectionLeftNode);
		var sectionLeftIcon = new Element("img", {
			"styles": this.css.sectionLeftIcon,
			"src" : this.sectionData.icon ?  ('data:image/png;base64,'+ this.sectionData.icon) : "../x_component_Forum/$Setting/default/sectionIcon/forum_icon.png"
		}).inject(sectionLeftIconNode);

		var sectionLeftContent = new Element("div.sectionLeftContent", {
			"styles": this.css.sectionLeftContent
		}).inject(sectionLeftNode);
		var sectionTopDiv = new Element("div.sectionTopDiv", {
			"styles": this.css.sectionTopDiv
		}).inject(sectionLeftContent);
		var sectionTopInfor= new Element("div.sectionTopInfor", {
			"styles": this.css.sectionTopInfor,
			"text" : this.lp.subject + "：" + this.sectionData.subjectTotal + "，" + this.lp.replyCount + "：" + this.sectionData.replyTotal
		}).inject(sectionTopDiv);

		var sectionLeftDiv = new Element("div.sectionLeftDiv", {
			"styles": this.css.sectionLeftDiv
		}).inject(sectionLeftContent);
		var sectionLeftMemo = new Element("div.sectionLeftMemo", {
			"styles": this.css.sectionLeftMemo,
			"text" : this.sectionData.sectionDescription
		}).inject(sectionLeftDiv);

		sectionTopDiv = new Element("div.sectionTopDiv", {
			"styles": this.css.sectionTopDiv
		}).inject(sectionLeftContent);
		sectionTopInfor= new Element("div.sectionTopInfor", {
			"styles": this.css.sectionTopInfor,
			"text" : this.lp.moderatorNames + "："
		}).inject(sectionTopDiv);

		sectionTopInfor= new Element("div.sectionTopInfor", {
			"styles": this.css.sectionTopInfor
		}).inject(sectionTopDiv);
		this.createPersonNode( sectionTopInfor, this.sectionData.moderatorNames );

	},
	createNoteNode : function(){
		if( !this.sectionData.sectionNotice || this.sectionData.sectionNotice.trim() =="" ){
			return;
		}
		var noteNode = this.noteNode = new Element("div.noteNode", {
			"styles": this.css.noteNode
		}).inject(this.middleNode);

		var noteTopNode = new Element("div.noteTopNode", {
			"styles": this.css.noteTopNode
		}).inject(noteNode);
		var noteTopContent = new Element("div.noteTopContent", {
			"styles": this.css.noteTopContent
		}).inject(noteTopNode);
		var noteIcon = new Element("div.noteIcon", {
			"styles": this.css.noteIcon
		}).inject(noteTopContent);
		var noteTopText = new Element("div.noteTopText", {
			"styles": this.css.noteTopText,
			"text" : this.lp.sectionNotice
		}).inject(noteTopContent);


		var noteContent = new Element("div.noteContent", {
			"styles": this.css.noteContent,
			"html" : this.sectionData.sectionNotice
		}).inject(noteNode);

		if( this.status && this.status.noteHidden ){
			noteNode.setStyle("display" , "none");
		}
	},
	_createMiddleNode : function(){
		this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleNode);
		if( this.contentDiv )this.contentDiv.empty();
		if( this.explorer ){
			this.explorer.destroy();
			delete this.explorer;
		}
		this.explorer = new MWF.xApplication.ForumSection.Explorer(this.contentDiv, this, this,{
			style:this.options.style,
			viewPageNum : ( this.status && this.status.viewPageNum ) ? this.status.viewPageNum : 1
		});
		this.explorer.load();
	},
	setContentSize: function () {
		//var topSize = this.topNode ? this.topNode.getSize() : {"x": 0, "y": 0};
		var topSize = {"x": 0, "y": 0};
		var nodeSize = this.content.getSize();
		var pt = this.contentContainerNode.getStyle("padding-top").toFloat();
		var pb = this.contentContainerNode.getStyle("padding-bottom").toFloat();

		var height = nodeSize.y - topSize.y - pt - pb;
		this.contentContainerNode.setStyle("height", "" + height + "px");
	},
	// openLoginForm : function(){
	// 	//MWF.xDesktop.requireApp("Forum", "Login", null, false);
	// 	//var login = new MWF.xApplication.Forum.Login(this, {
	// 	//    onPostOk : function(){
	// 	//        window.location.reload();
	// 	//    }
	// 	//});
	// 	//login.openLoginForm();
	// 	MWF.require("MWF.xDesktop.Authentication", null, false);
	// 	var authentication = new MWF.xDesktop.Authentication({
	// 		style : "application",
	// 		onPostOk : function(){
	// 			window.location.reload();
	// 		}
	// 	},this);
	// 	authentication.openLoginForm({
	// 		hasMask : true
	// 	});
	// },
	// openSignUpForm : function(){
	// 	//MWF.xDesktop.requireApp("Forum", "Login", null, false);
	// 	//var login = new MWF.xApplication.Forum.Login(this, {});
	// 	//login.openSignUpForm();
	// 	MWF.require("MWF.xDesktop.Authentication", null, false);
	// 	var authentication = new MWF.xDesktop.Authentication( {
	// 		style : "application",
	// 		onPostOk : function(){
	// 		}
	// 	}, this);
	// 	authentication.openSignUpForm({
	// 		hasMask : true
	// 	});
	// },
	openLoginForm : function(){
		MWF.require("MWF.xDesktop.Authentication", null, false);
		var authentication = new MWF.xDesktop.Authentication({
			"style" : "flat",
			"popupStyle_password": "o2platformSignupFlat",
			onPostOk : function(){
				window.location.reload();
			}
		},this);
		authentication.popupOptions = {
			"draggable": true,
			"closeAction": true,
			"hasMask": true,
			"relativeToApp": true,
			"width": "420",
			"height": "640"
		};
		// authentication.loadLogin(this.app.content);
		authentication.openLoginForm();
	},
	openSignUpForm : function(){
		MWF.require("MWF.xDesktop.Authentication", null, false);
		var authentication = new MWF.xDesktop.Authentication( {
			style : "flat",
			"popupStyle_password": "o2platformSignupFlat",
			onPostOk : function(){
			}
		}, this);
		authentication.popupOptions = {
			"draggable": true,
			"closeAction": true,
			"hasMask": true,
			"relativeToApp": true,
			"width": "420",
			"height": "640"
		};
		authentication.openSignUpForm();
	},
	recordStatus: function(){
		return {
			sectionId : this.options.sectionId,
			viewPageNum : this.explorer.view.getCurrentPageNum(),
			noteHidden : this.noteNodeHidden
		};
	},
	openPerson : function( userName ){
		if( !userName || userName == "" ){
		}else{
			var appId = "ForumPerson"+userName;
			if (this.desktop.apps[userName]){
				this.desktop.apps[userName].setCurrent();
			}else {
				this.desktop.openApplication(null, "ForumPerson", {
					"personName" : userName,
					"appId": appId
				});
			}
		}
	},
	createPersonNode : function( container, personName ){
		var persons = typeOf( personName ) === "string" ? personName.split(",") : personName;
		persons.each( function(userName, i){
			if( !userName )return;
			var span = new Element("span", {
				"text" : userName.split("@")[0],
				"styles" : this.css.person
			}).inject(container);
			span.addEvents( {
				mouseover : function(){ this.node.setStyles( this.obj.css.person_over )}.bind( {node:span, obj:this} ),
				mouseout : function(){ this.node.setStyles( this.obj.css.person )}.bind( {node:span, obj:this} ),
				click : function(){ this.obj.openPerson( this.userName ) }.bind( {userName:userName, obj:this} )
			});
			if( i != persons.length - 1 ){
				new Element("span", {
					"text" : "、"
				}).inject(container);
			}
		}.bind(this))
	},
	destroy : function(){
		this.clearContent();
	},
	clearContent: function () {
		if (this.explorer)this.explorer.destroy();
		if(this.setContentSizeFun)this.removeEvent("resize", this.setContentSizeFun );
		if(this.scrollBar && this.scrollBar.scrollVAreaNode)this.scrollBar.scrollVAreaNode.destroy();
		if( this.scrollBar )delete this.scrollBar;
		if (this.contentContainerNode) {
			this.contentContainerNode.destroy();
			//this.middleNode.destroy();
			//this.contentNode.destroy();
		}
	}
});


MWF.xApplication.ForumSection.Explorer = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"viewPageNum" : 1
	},
	initialize: function (container, app, parent, options) {
		this.setOptions( options );
		this.container = container;
		this.parent = parent;
		this.app = app;
		this.css = this.parent.css;
		this.lp = this.app.lp;
	},
	load: function () {
		this.container.empty();

		this.loadToolbar();

		this.viewContainerTop = Element("div",{
			"styles" : this.css.viewContainerTop
		}).inject(this.container);

		this.viewContainer = Element("div",{
			"styles" : this.css.viewContainer
		}).inject(this.container);

		this.viewContainerPrime = Element("div",{
			"styles" : this.css.viewContainer
		}).inject(this.container);

		this.loadToolbar();

		//this.loadTopView();
		this.loadView();
	},
	destroy : function(){
		if(this.resizeWindowFun)this.app.removeEvent("resize",this.resizeWindowFun);
		this.view.destroy();
		if( this.view.refreshInterval ){
			clearInterval( this.view.refreshInterval );
		}
	},
	loadToolbar: function(){
		var toolbar = new Element("div",{
			styles : this.css.toolbar
		}).inject(this.container);
		if( this.toolbarTop ){
			this.toolbarBottom = toolbar;
		}else{
			this.toolbarTop = toolbar;
		}

		//if( this.parent.access.isSubjectPublisher( this.parent.sectionData ) ){
		if( this.app.sectionPermission.subjectPublishAble ){
			var createActionNode = new Element("div",{
				styles : this.css.toolbarActionNode,
				text: this.lp.createSubject
			}).inject(toolbar);
			createActionNode.addEvents(
				{
					"mouseover": function () {
						this.node.setStyles(this.obj.css.toolbarActionNode_over);
					}.bind({obj: this, node: createActionNode}),
					"mouseout": function () {
						this.node.setStyles(this.obj.css.toolbarActionNode);
					}.bind({obj: this, node: createActionNode}),
					"click": function () {
						if( this.app.access.isAnonymousDynamic() ){
							this.app.openLoginForm(
								function(){ this.createSubject(); }.bind(this)
							);
						}else{
							this.createSubject();
						}
					}.bind(this)
				}
			)
		}


		var fileterNode = new Element("div",{
			styles : this.css.fileterNode
		}).inject(toolbar);

		var pagingBar = new Element("div",{
			styles : this.css.fileterNode
		}).inject(toolbar);
		if( this.pagingBarTop ){
			this.pagingBarBottom = pagingBar;
		}else{
			this.pagingBarTop = pagingBar;
		}
	},
	loadView : function(){

		//this.resizeWindow();
		//this.resizeWindowFun = this.resizeWindow.bind(this)
		//this.app.addEvent("resize", this.resizeWindowFun );

		this.view = new MWF.xApplication.ForumSection.View( this.viewContainer, this.app, this, {
			templateUrl : this.parent.path+"listItem.json",
			pagingEnable : true,
			onPostCreateViewBody : function(){
				this.app.fireEvent("postCreateViewBody");
			}.bind(this),
			pagingPar : {
				hasReturn : !this.app.naviMode,
				currentPage : this.options.viewPageNum,
				countPerPage : 30,
				onPostLoad : function( pagingBar ){
					if(pagingBar.nextPageNode){
						pagingBar.nextPageNode.inject( this.toolbarBottom, "before" );
					}
				}.bind(this),
				onPageReturn : function( pagingBar ){
					var appId = "Forum";
					if (this.app.desktop.apps[appId]){
						this.app.desktop.apps[appId].setCurrent();
					}else {
						this.app.desktop.openApplication(null, "Forum", { "appId": appId });
					}
					this.app.close();
				}.bind(this)
			}
		} );
		this.view.filterData = { sectionId : this.app.sectionData.id , withTopSubject : true };
		this.view.pagingContainerTop = this.pagingBarTop;
		this.view.pagingContainerBottom = this.pagingBarBottom;
		this.view.load();
	},
	reloadView : function() {
		if( this.viewPrime )this.viewPrime.destroy();
		this.viewContainer.setStyle("display","");
		this.viewContainerTop.setStyle("display","");
		this.viewContainerPrime.setStyle("display","none");

		//this.loadTopView();
		this.loadView();
	},
	loadPrimeView : function(){
		if( this.view )this.view.destroy();
		if( this.viewTop )this.viewTop.destroy();
		this.viewContainer.setStyle("display","none");
		this.viewContainerTop.setStyle("display","none");
		this.viewContainerPrime.setStyle("display","");

		this.viewPrime = new MWF.xApplication.ForumSection.PrimeView( this.viewContainerPrime, this.app, this, {
			templateUrl : this.parent.path+"listItemPrime.json",
			pagingEnable : true,
			pagingPar : {
				hasReturn : !this.app.naviMode,
				currentPage : 1,
				countPerPage : 30,
				onPostLoad : function( pagingBar ){
					if(pagingBar.nextPageNode){
						pagingBar.nextPageNode.inject( this.toolbarBottom, "before" );
					}
				}.bind(this),
				onPageReturn : function( pagingBar ){
					var appId = "Forum";
					if (this.app.desktop.apps[appId]){
						this.app.desktop.apps[appId].setCurrent();
					}else {
						this.app.desktop.openApplication(null, "Forum", { "appId": appId });
					}
					this.app.close();
				}.bind(this)
			}
		} );
		this.viewPrime.filterData = { sectionId : this.app.sectionData.id };
		this.viewPrime.pagingContainerTop = this.pagingBarTop;
		this.viewPrime.pagingContainerBottom = this.pagingBarBottom;
		this.viewPrime.load();
	},
	resizeWindow: function(){
		var size = this.app.content.getSize();
		this.viewContainer.setStyles({"height":(size.y-121)+"px"});
	},
	createSubject: function(){
		var _self = this;
		var appId = "ForumDocument"+this.app.sectionData.id;
		if (_self.app.desktop.apps[appId]){
			_self.app.desktop.apps[appId].setCurrent();
		}else {
			this.app.desktop.openApplication(null, "ForumDocument", {
				"sectionId": this.app.sectionData.id,
				"appId": appId,
				"isNew" : true,
				"isEdited" : true,
				"onPostPublish" : function(){
					//this.view.reload();
				}.bind(this)
			});
		}
	}
});


MWF.xApplication.ForumSection.TopView = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	_createDocument: function(data, index){
		return new MWF.xApplication.ForumSection.TopDocument(this.viewNode, data, this.explorer, this, null,  index);
	},
	_getCurrentPageData: function(callback, count){
		if (!count)count = 30;
		//var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
		var filter = this.filterData || {};
		//page, count,  filterData, success,failure, async
		if( !this.page ){
			this.page = 1;
		}else{
			this.page ++;
		}
		this.actions.listTopSubject( this.app.sectionData.id, function (json) {
			if( !json.data )json.data = [];
			if (callback)callback(json);
		}.bind(this))
	},
	_removeDocument: function(documentData, all){
		this.actions.deleteSubject(documentData.id, function(json){
			this.reload();
			this.app.reloadAllParents();
			this.app.notice(this.app.lp.deleteDocumentOK, "success");
		}.bind(this));
	},
	_create: function(){

	},
	_openDocument: function( documentData,index ){
		var appId = "ForumDocument"+documentData.id;
		if (this.app.desktop.apps[appId]){
			this.app.desktop.apps[appId].setCurrent();
		}else {
			this.app.desktop.openApplication(null, "ForumDocument", {
				"sectionId" : documentData.sectionId,
				"id" : documentData.id,
				"appId": appId,
				"isEdited" : false,
				"isNew" : false,
				"index" : index
			});
		}
	},
	_queryCreateViewNode: function(){
	},
	_postCreateViewNode: function( viewNode ){
	},
	_queryCreateViewHead:function(){
	},
	_postCreateViewHead: function( headNode ){
		//this.allSubjectNode = headNode.getElements("[lable='allSubject']")[0];
		var primeNode = headNode.getElements("[lable='prime']")[0];
		primeNode.addEvent( "click", function(){
			this.explorer.loadPrimeView();
		}.bind(this))
	}

});

MWF.xApplication.ForumSection.TopDocument = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
	_queryCreateDocumentNode:function( itemData ){
	},
	_postCreateDocumentNode: function( itemNode, itemData ){
	},
	open: function (e) {
		this.view._openDocument(this.data, this.index);
	},
	edit : function(){
		var appId = "ForumDocument"+this.data.id;
		if (this.app.desktop.apps[appId]){
			this.app.desktop.apps[appId].setCurrent();
		}else {
			this.app.desktop.openApplication(null, "ForumDocument", {
				"sectionId" : this.data.sectionId,
				"id" : this.data.id,
				"appId": appId,
				"isEdited" : true,
				"isNew" : false,
				"index" : this.index
			});
		}
	}
});

MWF.xApplication.ForumSection.View = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	refreshNewSubject : function(){
		if (this.refreshTr)this.refreshTr.destroy();

		if( this.refreshInterval ){
			clearInterval( this.refreshInterval );
		}
		this.refreshInterval = setInterval( function(){
			this.actions.listSubjectFilterPage( 1, 1, this.filterData || {}, function(json){
				if( !json.count )json.count=0;
				var count = this.laterRefreshCount || this.dataCount;
				if( json.count > count ) {
					this.laterRefreshCount = json.count;
					this.createRefreshTr( json.count );
				}
			}.bind(this))
		}.bind(this), 10 * 60 * 1000 )
	},
	createRefreshTr : function( newCount ){
		if (this.refreshTr)this.refreshTr.destroy();

		var refreshTr = this.refreshTr = new Element( "tr.refreshTrNode" , {
			styles : this.css.refreshTrNode
		}).inject( this.headNode, "after" );

		var td = this.refreshTd = new Element( "td.refreshTdNode", {
			width : "60%",
			styles : this.css.refreshTdNode
		}).inject( refreshTr );
		var refreshNode = new Element("div" , {
			styles : this.css.refreshNode,
			text : this.lp.newSubjectPublishedText.replace( "{count}", newCount - this.dataCount )
		}).inject(td);
		refreshNode.addEvent("click", function(){
			this.gotoPage(1)
		}.bind(this));

		var td = new Element( "td.refreshTdNode", {
			styles : this.css.refreshTdNode,
			width : "12%"
		}).inject( refreshTr );

		var td = new Element( "td.refreshTdNode", {
			styles : this.css.refreshTdNode,
			width : "7%"
		}).inject( refreshTr );

		var td = new Element( "td.refreshTdNode", {
			styles : this.css.refreshTdNode,
			width : "7%"
		}).inject( refreshTr );

		var td = new Element( "td.refreshTdNode", {
			styles : this.css.refreshTdNode,
			width : "14%"
		}).inject( refreshTr )
	},
	_createDocument: function(data, index){
		return new MWF.xApplication.ForumSection.Document(this.viewNode, data, this.explorer, this, null,  index);
	},
	_getCurrentPageData: function(callback, count, pageNum){
		this.clearBody();
		if(!count)count=30;
		if(!pageNum)pageNum = 1;
		//if( pageNum == 1 ){
		//	this.explorer.viewContainerTop.setStyle("display","block");
		//	this.headNode.setStyle("display","none");
		//}else{
		//	this.explorer.viewContainerTop.setStyle("display","none");
		//	this.headNode.setStyle("display","");
		//}
		if( this.topSepTr )this.topSepTr.destroy();
		var filter = this.filterData || {};
		//filter.withTopSubject = false;
		this.actions.listSubjectFilterPage( pageNum, count, filter, function(json){
			if( !json.data )json.data = [];
			if( !json.count )json.count=0;
			this.refreshNewSubject();
			if( callback )callback(json);
		}.bind(this))
	},
	_removeDocument: function(documentData, all){
		this.actions.deleteSubject(documentData.id, function(json){
			this.reload();
			this.app.reloadAllParents();
			this.app.notice(this.app.lp.deleteDocumentOK, "success");
		}.bind(this));
	},
	_create: function(){

	},
	_openDocument: function( documentData,index ){
		var appId = "ForumDocument"+documentData.id;
		if (this.app.desktop.apps[appId]){
			this.app.desktop.apps[appId].setCurrent();
		}else {
			this.app.desktop.openApplication(null, "ForumDocument", {
				"sectionId" : documentData.sectionId,
				"id" : documentData.id,
				"appId": appId,
				"isEdited" : false,
				"isNew" : false,
				"index" : index
			});
		}
	},
	_queryCreateViewNode: function(){
	},
	_postCreateViewNode: function( viewNode ){
	},
	_queryCreateViewHead:function(){
	},
	_postCreateViewHead: function( headNode ){
		var primeNode = headNode.getElements("[lable='prime']")[0];
		primeNode.addEvent( "click", function(){
			this.explorer.loadPrimeView();
		}.bind(this))
	}

});


MWF.xApplication.ForumSection.Document = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
	_queryCreateDocumentNode:function( itemData ){
	},
	_postCreateDocumentNode: function( itemNode, itemData ){
		//置顶帖和一般帖之间添加分割线
		if( this.index != 1 && !itemData.isTopSubject  ){
			var prev = this.view.items[this.index-1];
			if( prev && prev.data.isTopSubject ){
				var tr = this.view.topSepTr = new Element( "tr" , {styles: this.css.documentNode_empty } ).inject( itemNode, "before" );
				new Element( "td", {colspan:5}).inject( tr )
			}
		}
	},
	open: function (e) {
		this.view._openDocument(this.data, this.index);
	},
	edit : function(){
		var appId = "ForumDocument"+this.data.id;
		if (this.app.desktop.apps[appId]){
			this.app.desktop.apps[appId].setCurrent();
		}else {
			this.app.desktop.openApplication(null, "ForumDocument", {
				"sectionId" : this.data.sectionId,
				"id" : this.data.id,
				"appId": appId,
				"isEdited" : true,
				"isNew" : false,
				"index" : this.index
			});
		}
	}
});



MWF.xApplication.ForumSection.PrimeView = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	_createDocument: function(data, index){
		return new MWF.xApplication.ForumSection.PrimeDocument(this.viewNode, data, this.explorer, this, null,  index);
	},
	_getCurrentPageData: function(callback, count, pageNum){
		this.clearBody();
		if(!count)count=30;
		if(!pageNum)pageNum = 1;
		var filter = this.filterData || {};
		this.actions.listCreamSubjectFilterPage( pageNum, count, filter, function(json){
			if( !json.data )json.data = [];
			if( !json.count )json.count=0;
			if( callback )callback(json);
		}.bind(this))
	},
	_removeDocument: function(documentData, all){
		this.actions.deleteSubject(documentData.id, function(json){
			this.reload();
			this.app.reloadAllParents();
			this.app.notice(this.app.lp.deleteDocumentOK, "success");
		}.bind(this));
	},
	_create: function(){

	},
	_openDocument: function( documentData,index ){
		var appId = "ForumDocument"+documentData.id;
		if (this.app.desktop.apps[appId]){
			this.app.desktop.apps[appId].setCurrent();
		}else {
			this.app.desktop.openApplication(null, "ForumDocument", {
				"sectionId" : documentData.sectionId,
				"id" : documentData.id,
				"appId": appId,
				"isEdited" : false,
				"isNew" : false,
				"index" : index
			});
		}
	},
	_queryCreateViewNode: function(){
	},
	_postCreateViewNode: function( viewNode ){
	},
	_queryCreateViewHead:function(){
	},
	_postCreateViewHead: function( headNode ){
		var allSubjectNode = headNode.getElements("[lable='allSubject']")[0];
		allSubjectNode.addEvent( "click", function(){
			this.explorer.reloadView();
		}.bind(this))
	}

});

MWF.xApplication.ForumSection.PrimeDocument = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
	_queryCreateDocumentNode:function( itemData ){
	},
	_postCreateDocumentNode: function( itemNode, itemData ){
	},
	open: function (e) {
		this.view._openDocument(this.data, this.index);
	},
	edit : function(){
		var appId = "ForumDocument"+this.data.id;
		if (this.app.desktop.apps[appId]){
			this.app.desktop.apps[appId].setCurrent();
		}else {
			this.app.desktop.openApplication(null, "ForumDocument", {
				"sectionId" : this.data.sectionId,
				"id" : this.data.id,
				"appId": appId,
				"isEdited" : true,
				"isNew" : false,
				"index" : this.index
			});
		}
	}
});