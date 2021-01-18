MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ApplicationExplorer = MWF.xApplication.process.ApplicationExplorer || {};
MWF.xDesktop.requireApp("process.ApplicationExplorer", "lp."+MWF.language, null, false);
MWF.xApplication.process.ApplicationExplorer.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"mvcStyle": "style.css",
		"name": "process.ApplicationExplorer",
		"icon": "icon.png",
		"width": "1500",
		"height": "760",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.process.ApplicationExplorer.LP.title,
		// "maxWidth": 840,
		// "minWidth": 720
		"maxWidth": 840,
		"minWidth": 540
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.process.ApplicationExplorer.LP;
		this.viewPath = this.path+this.options.style+"/view.html";
		this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
		this.deleteElements = [];
	},
	loadApplication: function(callback){
		this.loadControl();
		this.content.loadHtml(this.viewPath, {"bind": {"lp": this.lp, "control": this.control}}, function(){
			if (!this.options.isRefresh){
				this.maxSize(function(){
					this.loadApp(callback);
				}.bind(this));
			}else{
				this.loadApp(callback);
			}
		}.bind(this));
	},
	loadApp: function(callback){
		this.loadNodes();

		this.resizeContent();
		this.addEvent("resize", this.resizeContent.bind(this));

		this.loadApplicationCategoryList();
		//this.loadApplicationList();
		this.clickAllCategoryNode();
		if (callback) callback();
	},


	loadControl: function(){
		this.control = {};
		this.control.canCreate = MWF.AC.isProcessPlatformCreator();
		this.control.canManage = !!(MWF.AC.isAdministrator() || MWF.AC.isProcessManager());
	},
	loadNodes: function(){
		this.node = this.content.getElement(".o2_process_AppExp_content");
		this.topNode = this.content.getElement(".o2_process_AppExp_top");
		this.allCategoryNode = this.content.getElement(".o2_process_AppExp_All");
		this.category = this.allCategoryNode;
		if (this.allCategoryNode) this.allCategoryNode.addEvent("click", this.clickAllCategoryNode.bind(this));

		this.createNode = this.content.getElement(".o2_process_AppExp_create");
		if (this.createNode) this.createNode.addEvent("click", this.createApplication.bind(this));

		this.importNode = this.content.getElement(".o2_process_AppExp_import");
		this.findNode = this.content.getElement(".o2_process_AppExp_find");

		this.categoryAreaNode = this.content.getElement(".o2_process_AppExp_category");
		this.contentArea = this.content.getElement(".o2_process_AppExp_contentArea");
		this.contentNode = this.content.getElement(".o2_process_AppExp_contentNode");
		this.bottomNode = this.content.getElement(".o2_process_AppExp_bottom");
		if (this.importNode){
			this.importNode.addEvent("click", function(e){
				this.importApplicationNew(e);
			}.bind(this));
		}

		if (this.findNode){
			this.findNode.addEvent("click", function(e){
				this.openFindDesigner();
			}.bind(this));
		}
	},
	openFindDesigner: function(){
		var options = {
			"filter": {
				"moduleList": ["processPlatform"]
			}
		};
		layout.openApplication(null, "FindDesigner", options);
	},
	importApplicationNew: function(e){
		MWF.xDesktop.requireApp("AppCenter", "", function(){
			if (!this.uploadFileAreaNode){
				this.uploadFileAreaNode = new Element("div");
				var html = "<input name=\"file\" type=\"file\" accept=\".xapp\"/>";
				this.uploadFileAreaNode.set("html", html);
				this.fileUploadNode = this.uploadFileAreaNode.getFirst();
				this.fileUploadNode.addEvent("change", this.importLocalFile.bind(this));
			}else{
				if (this.fileUploadNode) this.fileUploadNode.destroy();
				this.uploadFileAreaNode.empty();
				var html = "<input name=\"file\" type=\"file\" accept=\".xapp\"/>";
				this.uploadFileAreaNode.set("html", html);
				this.fileUploadNode = this.uploadFileAreaNode.getFirst();
				this.fileUploadNode.addEvent("change", this.importLocalFile.bind(this));
			}
			this.fileUploadNode.click();
		}.bind(this));
		return ;
		//老版导出
		MWF.xDesktop.requireApp("process.ApplicationExplorer", "Importer", function(){
			(new MWF.xApplication.process.ApplicationExplorer.Importer(this, e)).load();
		}.bind(this));
	},
	importLocalFile: function(){
		var files = this.fileUploadNode.files;
		if (files.length){
			var file = files[0];
			var position = this.topNode.getPosition(this.node);
			var size = this.contentArea.getSize();
			var width = size.x*0.9;
			if (width>600) width = 600;
			var height = size.y*0.9;
			var x = (size.x-width)/2;
			var y = (size.y-height)/2;

			var setupModule = null;
			var appCenter = new MWF.xApplication.AppCenter.Main();
			appCenter.inBrowser = true;
			appCenter.load(true);
			MWF.require("MWF.xDesktop.Dialog", function(){
				var dlg = new MWF.xDesktop.Dialog({
					"title": this.lp.setupTitle,
					"style": "appMarket",
					"top": y+20,
					"left": x,
					"fromTop":position.y,
					"fromLeft": position.x,
					"width": width,
					"height": height,
					"html": "",
					"maskNode": this.node,
					"container": this.node,
					"buttonList": [
						{
							"text": appCenter.lp.ok,
							"action": function(){
								if (setupModule) setupModule.setup();
								this.close();
							}
						},
						{
							"text": appCenter.lp.cancel,
							"action": function(){this.close();}
						}
					]
				});
				dlg.show();

				setupModule = new MWF.xApplication.AppCenter.Module.SetupLocal(file, dlg, appCenter);

				debugger
			}.bind(this));
		}
	},

	createApplication: function(){
		this.createApplicationCreateMarkNode();
		this.createApplicationCreateAreaNode();
		this.createApplicationCreateNode();

		this.applicationCreateAreaNode.inject(this.applicationCreateMarkNode, "after");
		this.applicationCreateAreaNode.fade("in");
		$("createApplicationName").focus();

		this.setApplicationCreateNodeSize();
		this.setApplicationCreateNodeSizeFun = this.setApplicationCreateNodeSize.bind(this);
		this.addEvent("resize", this.setApplicationCreateNodeSizeFun);
	},
	createApplicationCreateMarkNode: function(){
		this.applicationCreateMarkNode = new Element("div.o2_process_AppExp_applicationCreateMarkNode", {
			"events": {
				"mouseover": function(e){e.stopPropagation();},
				"mouseout": function(e){e.stopPropagation();}
			}
		}).inject(this.node, "after");
	},
	createApplicationCreateAreaNode: function(){
		this.applicationCreateAreaNode = new Element("div.o2_process_AppExp_applicationCreateAreaNode");
	},
	createApplicationCreateNode: function(){
		this.applicationCreateNode = new Element("div.o2_process_AppExp_applicationCreateNode").inject(this.applicationCreateAreaNode);
		this.applicationCreateNewNode = new Element("div.o2_process_AppExp_applicationCreateNewNode").inject(this.applicationCreateNode);
		this.applicationCreateFormNode = new Element("div.o2_process_AppExp_applicationCreateFormNode").inject(this.applicationCreateNode);

		var html = "<table width=\"100%\" height=\"80%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
			"<tr><td style=\"height: 30px; line-height: 30px; text-align: left; min-width: 80px; width:25%\">" +
			this.lp.name+":</td>" +
			"<td style=\"; text-align: right;\"><input type=\"text\" class='o2_process_AppExp_createApplicationName' id=\"createApplicationName\" " +
			"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			"height: 26px;\"/></td></tr>" +
			"<tr><td style=\"height: 30px; line-height: 30px; text-align: left\">"+this.lp.alias+":</td>" +
			"<td style=\"; text-align: right;\"><input type=\"text\" class='o2_process_AppExp_createApplicationAlias' id=\"createApplicationAlias\" " +
			"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			"height: 26px;\"/></td></tr>" +
			"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.description+":</td>" +
			"<td style=\"; text-align: right;\"><input type=\"text\" class='o2_process_AppExp_createApplicationDescription' id=\"createApplicationDescription\" " +
			"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			"height: 26px;\"/></td></tr>" +
			"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.type+":</td>" +
			"<td style=\"; text-align: right;\"><input type=\"text\" class='o2_process_AppExp_createApplicationType' id=\"createApplicationType\" " +
			"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			"height: 26px;\"/></td></tr>" +
			//"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.iconLabel+":</td>" +
			//"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createApplicationType\" " +
			//"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			//"height: 26px;\"/></td></tr>" +
			"</table>";
		this.applicationCreateFormNode.set("html", html);

		this.applicationCancelActionNode = new Element("div.o2_process_AppExp_applicationCreateCancelActionNode", {
			"text": this.lp.action_cancel
		}).inject(this.applicationCreateFormNode);
		this.applicationCreateOkActionNode = new Element("div.o2_process_AppExp_applicationCreateOkActionNode", {
			"text": this.lp.action_ok
		}).inject(this.applicationCreateFormNode);

		this.applicationCancelActionNode.addEvent("click", function(e){
			this.cancelCreateApplication(e);
		}.bind(this));
		this.applicationCreateOkActionNode.addEvent("click", function(e){
			this.okCreateApplication(e);
		}.bind(this));
	},

	setApplicationCreateNodeSize: function(){
		var size = this.node.getSize();
		var allSize = this.content.getSize();
		this.applicationCreateMarkNode.setStyles({
			"width": ""+allSize.x+"px",
			"height": ""+allSize.y+"px"
		});
		this.applicationCreateAreaNode.setStyles({
			"width": ""+size.x+"px",
			"height": ""+size.y+"px"
		});
		var hY = size.y*0.8;
		var mY = size.y*0.2/2;
		this.applicationCreateNode.setStyles({
			"height": ""+hY+"px",
			"margin-top": ""+mY+"px"
		});

		var iconSize = this.applicationCreateNewNode.getSize();
		var formHeight = hY*0.7;
		if (formHeight>250) formHeight = 250;
		var formMargin = hY*0.3/2-iconSize.y;
		this.applicationCreateFormNode.setStyles({
			"height": ""+formHeight+"px",
			"margin-top": ""+formMargin+"px"
		});
	},
	cancelCreateApplication: function(e){
		var _self = this;
		var nameNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationName");
		var aliasNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationAlias");
		var descriptionNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationDescription");
		if (nameNode.get("value") || aliasNode.get("value") || descriptionNode.get("value")){
			this.confirm("warn", e, this.lp.createApplication_cancel_title, this.lp.createApplication_cancel, 320, 100, function(){
				_self.applicationCreateMarkNode.destroy();
				_self.applicationCreateAreaNode.destroy();
				this.close();
			},function(){
				this.close();
			});
		}else{
			this.applicationCreateMarkNode.destroy();
			this.applicationCreateAreaNode.destroy();
		}
	},
	okCreateApplication: function(e){
		var nameNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationName");
		var aliasNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationAlias");
		var descriptionNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationDescription");
		var typeNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationType");
		var data = {
			"name": nameNode.get("value"),
			"alias": aliasNode.get("value"),
			"description": descriptionNode.get("value"),
			"applicationCategory": typeNode.get("value")
		};
		if (data.name){
			this.restActions.saveApplication(data, function(json){
				this.applicationCreateMarkNode.destroy();
				this.applicationCreateAreaNode.destroy();

				this.restActions.getApplication(json.data.id, function(json){
					json.data.processList = [];
					json.data.formList = [];
					this.createApplicationItem(json.data, "top");
					// var application = new MWF.xApplication.process.ApplicationExplorer.Application(this, json.data, "top");
					// application.load();
					//this.applications.push(application);
				}.bind(this));

				this.reloadApplicationCategoryList(true);
				this.notice(this.lp.application.createApplicationSuccess, "success");
				//    this.app.processConfig();
			}.bind(this));
		}else{
			nameNode.setStyle("border-color", "red");
			nameNode.focus();
			this.notice(this.lp.application.inputApplicationName, "error");
		}
	},

	clickAllCategoryNode: function(){
		if (this.category){
			this.category.removeClass("o2_process_AppExp_categoryItem_current");
			this.category.removeClass("o2_process_AppExp_All_current");
			this.category.removeClass("o2_process_AppExp_categoryItem_over");
			this.category.removeClass("o2_process_AppExp_All_over");
		}
		this.allCategoryNode.removeClass("o2_process_AppExp_categoryItem_over");
		this.allCategoryNode.addClass("o2_process_AppExp_All_current");
		this.category = this.allCategoryNode;
		this.loadApplicationList(this.allCategoryNode);
	},

	resizeContent: function(){
		var size = this.content.getSize();
		var topSize = this.topNode.getComputedSize();
		var bottomSize = this.bottomNode.getComputedSize();
		var pt = this.contentArea.getStyle("padding-top").toInt() || 0;
		var pb = this.contentArea.getStyle("padding-bottom").toInt() || 0;

		var h = size.y-topSize.totalHeight-bottomSize.totalHeight-pt-pb;

		this.contentArea.setStyle("height", ""+h+"px");
		this.getApplicationDimension();

		if (this.contentNode){
			this.contentNode.setStyles({
				"margin-left": ""+this.dimension.marginLeft+"px",
				"margin-right": ""+this.dimension.marginRight+"px"
			});
		}
	},

	createCategoryExpandButton: function(){
		//this.categoryExpandButtonArea = new Element("div.o2_process_AppExp_categoryExpandButtonArea").inject(this.categoryAreaNode);
		this.categoryExpandButton = new Element("div.o2_process_AppExp_categoryExpandButton").inject(this.categoryAreaNode, "before");
		this.categoryExpandButton.addEvent("click", this.expandOrCollapseCategory.bind(this));
	},
	expandOrCollapseCategory: function(e){
		if (!this.categoryMorph) this.categoryMorph = new Fx.Morph(this.categoryAreaNode, {"duration": 100});
		if (this.categoryAreaNode.hasClass("o2_process_AppExp_category_more")){
			this.categoryAreaNode.removeClass("o2_process_AppExp_category_more");
			this.categoryMorph.start({"height": ""+this.topNode.getSize().y+"px"});
			if (this.expandOrCollapseCategoryFun) this.content.removeEvent("click", this.expandOrCollapseCategoryFun);
		}else{
			this.categoryAreaNode.addClass("o2_process_AppExp_category_more");
			this.categoryMorph.start({"height": ""+this.categoryAreaNode.getScrollSize().y+"px"});

			this.expandOrCollapseCategoryFun = this.expandOrCollapseCategory.bind(this);
			this.content.addEvent("click", this.expandOrCollapseCategoryFun);
		}
		e.stopPropagation();
	},
	loadApplicationCategoryList: function( currentCategoryName, noRefreshContent ){
		if (this.control.canCreate){
			this.restActions.listApplicationCategory(function(json){

				var emptyCategory = null;
				json.data.each(function(category){
					var categoryName = category.applicationCategory || category.portalCategory || category.protalCategory || category.name;
					if( categoryName === "null" )categoryName = "";
					if (categoryName){
						this.createCategoryItemNode(categoryName, category.count);
					}else{
						emptyCategory = category;
					}
				}.bind(this));

				if (this.categoryAreaNode.getScrollSize().y>this.categoryAreaNode.getSize().y) this.createCategoryExpandButton();

				if( currentCategoryName ){
					var itemList = this.categoryAreaNode.getElements("div.o2_process_AppExp_categoryItem");
					if( itemList.length > 0 ){
						for( var i=0; i<itemList.length; i++ ){
							if( itemList[i].retrieve("categoryName") === currentCategoryName ){
								this.clickCategoryNode( itemList[i], noRefreshContent)
							}
						}
					}
				}

			}.bind(this));
		}
	},
	reloadApplicationCategoryList: function( noRefreshContent ){
		var categoryName = "";
		if( this.category ){
			categoryName = this.category.retrieve("categoryName") || "";
		}
		this.categoryAreaNode.empty();
		this.loadApplicationCategoryList( categoryName, noRefreshContent );
	},
	createCategoryItemNode: function(text, count){

		var categoryName = text;

		var itemNode = new Element("div.o2_process_AppExp_categoryItem", {
			"text": (count) ? categoryName+" ("+count+") " : categoryName
		}).inject(this.categoryAreaNode);

		itemNode.store("categoryName", categoryName);

		var _self = this;
		itemNode.addEvents({
			"mouseover": function(){if (_self.category != this) this.addClass("o2_process_AppExp_categoryItem_over");},
			"mouseout": function(){if (_self.category != this) this.removeClass("o2_process_AppExp_categoryItem_over");},
			"click": function(){_self.clickCategoryNode(this)}
		});
	},

	clickCategoryNode: function(item, noRefreshContent){
		// var node = this.categoryListAreaNode.getFirst("div");
		// node.setStyles(this.css.allCategoryItemNode);
		if (this.category){
			this.category.removeClass("o2_process_AppExp_categoryItem_current");
			this.category.removeClass("o2_process_AppExp_All_current");
			this.category.removeClass("o2_process_AppExp_categoryItem_over");
			this.category.removeClass("o2_process_AppExp_All_over");
		}
		item.removeClass("o2_process_AppExp_categoryItem_over");
		item.addClass("o2_process_AppExp_categoryItem_current");

		var p = item.getPosition(this.categoryAreaNode);
		var size = this.topNode.getSize();
		if (p.y>=size.y) item.inject(this.categoryAreaNode, "top");

		this.category = item;
		if( !noRefreshContent ){
			this.loadApplicationList(item);
		}
	},

	getApplicationDimension: function(){
		if (!this.dimension) this.dimension = {};
		this.dimension.count = 2;
		this.dimension.width = this.options.maxWidth;
		this.dimension.marginLeft = 40;
		this.dimension.marginRight = 20;

		//var size = this.contentNode.getSize();
		var areaSize = this.content.getSize();
		//var areaSize = this.contentArea.getSize();
		var x = areaSize.x-60;

		if (areaSize.y>=this.contentArea.getScrollSize().y) x = x-18;

		var n = (x/this.dimension.count).toInt();
		if (n<this.options.minWidth){
			this.dimension.count = 1;
			this.dimension.width = Math.min(x, this.options.maxWidth)-2;
		}else{
			while(n>this.options.maxWidth){
				this.dimension.count++;
				n = (x/this.dimension.count).toInt();
				if (n<this.options.minWidth){
					this.dimension.count--;
					n = this.options.maxWidth;
					break;
				}
			}
			this.dimension.width = n;
		}
		var margin = areaSize.x-(this.dimension.width*this.dimension.count);
		this.dimension.width = this.dimension.width-(this.dimension.count*2);

		this.dimension.marginLeft = margin/2;
		this.dimension.marginRight = margin/2-20;
	},
	loadApplicationList: function(item){
		var name = "";
		if (item){name = item.retrieve("categoryName", "")};
		this.restActions.listApplicationSummary(name, function(json){

			this.contentNode.empty();
			if (json.data.length){
				this.getApplicationDimension();
				json.data.each(function(appData){
					this.createApplicationItem(appData);
					// var application = new MWF.xApplication.process.ApplicationExplorer.Application(this, appData);
					// application.load();
					//this.applications.push(application);
				}.bind(this));
			}else {
				if (this.control.canCreate){
					var noApplicationNode = new Element("div.o2_process_AppExp_noApplicationNode", {
						"html": this.lp.noApplicationCreate
					}).inject(this.contentNode);
					noApplicationNode.addEvent("click", function(){
						this.createApplication();
					}.bind(this));
				}else{
					var noApplicationNode = new Element("div.o2_process_AppExp_noApplicationNode", {
						"text": this.lp.noApplication
					}).inject(this.contentNode);
				}
			}
		}.bind(this));
	},
	createApplicationItem: function(appData, where){
		var application = new MWF.xApplication.process.ApplicationExplorer.Application(this, appData, where);
		application.load();
	},
	checkDeleteApplication: function(){
		if (this.deleteElements.length){
			if (!this.deleteElementsNode){
				this.deleteElementsNode = new Element("div.o2_process_AppExp_deleteElements", {
					"text": this.lp.application.deleteElements
				}).inject(this.node);
				this.deleteElementsNode.position({
					relativeTo: this.contentArea,
					position: "centerTop",
					edge: "centerTop"
				});
				this.deleteElementsNode.addEvent("click", function(e){
					this.deleteSelectedElements(e);
				}.bind(this));
			}
		}else{
			if (this.deleteElementsNode){
				this.deleteElementsNode.destroy();
				this.deleteElementsNode = null;
				delete this.deleteElementsNode;
			}
		}
	},
	deleteSelectedElements: function(e){
		var _self = this;
		var applicationList = [];
		this.deleteElements.each(function(app){
			applicationList.push(app.data.name);
		});
		var confirmStr = this.lp.application.deleteElementsConfirm+" ("+applicationList.join("、")+") ";
		var check = "<br/><br/><input type=\"checkbox\" id=\"deleteApplicationAllCheckbox\" value=\"yes\">"+this.lp.application.deleteApplicationAllConfirm;
		confirmStr += check;

		this.confirm("infor", e, this.lp.application.deleteElementsTitle, {"html":confirmStr}, 530, 250, function(){
			confirmStr = _self.lp.application.deleteElementsConfirmAgain+"<br/><br/><font style='color:red; font-size:14px; font-weight: bold'>"+applicationList.join("、")+"</font>";
			var checkbox = this.content.getElement("#deleteApplicationAllCheckbox");

			var onlyRemoveNotCompleted = true;
			if (checkbox.checked){
				onlyRemoveNotCompleted = false;
				confirmStr = _self.lp.application.deleteElementsAllConfirmAgain+"<br/><br/><font style='color:red; font-size:14px; font-weight: bold'>"+applicationList.join("、")+"</font>";
			}

			this.close();

			_self.confirm("infor", e, _self.lp.application.deleteElementsTitle, {"html":confirmStr}, 500, 200, function(){
				var deleted = [];
				var doCount = 0;
				var readyCount = _self.deleteElements.length;
				var errorText = "";

				var complete = function(){
					if (doCount == readyCount){
						_self.reloadApplicationCategoryList( true );
						if (errorText){
							_self.app.notice(errorText, "error");
						}
					}
				};
				_self.deleteElements.each(function(application){
					application["delete"](onlyRemoveNotCompleted, function(){
						deleted.push(application);
						doCount++;
						if (_self.deleteElements.length==doCount){
							_self.deleteElements = _self.deleteElements.filter(function(item, index){
								return !deleted.contains(item);
							});
							_self.checkDeleteApplication();
						}
						complete();
					}, function(error){
						errorText = (errorText) ? errorText+"<br/><br/>"+error : error;
						doCount++;
						if (_self.deleteElements.length==doCount){
							_self.deleteElements = _self.deleteElements.filter(function(item, index){
								return !deleted.contains(item);
							});
							_self.checkDeleteApplication();
						}
						complete();
					});
				});
				this.close();
			}, function(){
				this.close();
			});

			this.close();
		}, function(){
			this.close();
		});
	},
	createAppCenterApp:  function(id){
		var size = this.content.getSize();
		var content = new Element("div", {
			"styles": {
				"width": size.x+"px",
				"height": size.y+"px",
				"position": "absolute",
				"top": "0px"
			}
		}).inject(this.content, "after");

		var app = new new Class({Implements: [Events]})();
		app.lp = MWF.xApplication.AppCenter.LP;

		app.css = MWF.xApplication.AppCenter.LP;

		app.actions = MWF.Actions.get("x_program_center");
		app.curAppId = id;
		app.createApplicationNode = content;
		app.content = content;
		app.notice = this.notice;
		app.path = "../x_component_AppCenter/$Main/";
		app.options = {"style": "default"};
		app.cssPath = app.path + app.options.style + "/css.wcss";
		o2.JSON.get(app.cssPath, function(json){
			app.css = json;
		}, false);

		app.addEvent("exporterClose", function(){
			this.content.hide();
		});

		this.appCenterApp = app;
	},
});

MWF.xApplication.process.ApplicationExplorer.Application = new Class({
	Implements: [Events],
	initialize: function (app, data, where) {
		this.app = app;
		this.lp = this.app.lp;
		this.dimension = this.app.dimension;
		this.container = this.app.contentNode;
		this.data = data;
		this.where = where || "bottom";
		this.canManage = this.checkManage();
	},

	checkManage: function(){
		if (this.app.control.canManage) return true;
		if (this.app.control.canCreate && (this.data.creatorPerson==layout.desktop.session.user.name)) return true;
		//if (this.data.controllerList.indexOf(layout.desktop.session.user.distinguishedName)!==-1) return true;
		return false;
	},

	load: function(){
		this.node = new Element("div.o2_process_AppExp_item_node").inject(this.container, this.where);
		// this.node.addEvents({
		// 	"mouseover": function(){this.node.addClass("o2_process_AppExp_item_node_over");}.bind(this),
		// 	"mouseout": function(){this.node.removeClass("o2_process_AppExp_item_node_over");}.bind(this)
		// });

		var w = this.dimension.width-20;
		this.node.setStyle("width", ""+w+"px");
		this.node.loadHtml(this.app.path+this.app.options.style+"/application.html", {"bind": {"lp": this.lp, "data": this.data, "canManage": this.canManage}}, function(){
			this.loadNodes();
			this.loadElements();
			this.loadNewNode();
		}.bind(this));

		this.resizeContentFun = this.resizeContent.bind(this);
		this.app.addEvent("resize", this.resizeContentFun);
	},
	loadElements: function(){
		this.loadElementList("formList", this.formListNode, this.openForm.bind(this), this.lp.noForm, this.createNewForm.bind(this));
		this.loadElementList("processList", this.processListNode, this.openProcess.bind(this), this.lp.noProcess, this.createNewProcess.bind(this));
	},
	loadNewNode: function(){
		this.newNode = this.node.getElement(".o2_process_AppExp_item_newNode");
		if (this.data.updateTime){
			var createDate = Date.parse(this.data.createTime);
			var currentDate = new Date();
			if (createDate.diff(currentDate, "hour")<12) {
				this.newNode.show();
			}else{
				this.newNode.hide();
			}
		}
	},
	loadElementList: function(list, container, click, noElement, noElementClick){
		if (this.data[list].length){
			this.data[list].each(function(el){
				var item = new Element("div.o2_process_AppExp_item_content_element").inject(container);
				item.set("text", el.name);
				//item.set("title", (el.description) ? el.name+"\n"+el.description : el.name);
				item.store("elementId", el.id);
				item.addEvents({
					"mouseover": function(){this.addClass("o2_process_AppExp_item_content_element_over")},
					"mouseout": function(){this.removeClass("o2_process_AppExp_item_content_element_over")},
					"click": function(e){
						var id = this.retrieve("elementId");
						if (click) click(id, e);
					}
				});
			}.bind(this));
		}else{
			var node = new Element("div.o2_process_AppExp_item_content_element", {
				"text": noElement,
				"styles": { "color": "#999999" }
			}).inject(container);
			node.addEvent("click", function(e){ if (noElementClick) noElementClick(e); }.bind(this));
		}
	},
	createNewForm: function(e){
		this.openApplication(e, 0);
	},
	createNewProcess: function(e){
		this.openApplication(e, 1);
	},
	openApplication: function(e, navi){
		var appId = "process.ProcessManager"+this.data.id;
		if (this.app.desktop.apps[appId]){
			this.app.desktop.apps[appId].setCurrent();
		}else {
			this.app.desktop.openApplication(e, "process.ProcessManager", {
				"application": this.data,
				"appId": appId,
				"onQueryLoad": function(){
					this.status = {"navi": navi || null};
				}
			});
		}
	},
	openForm: function(id, e){
		if (id){
			layout.desktop.getFormDesignerStyle(function(){
				var _self = this;
				var options = {
					"style": layout.desktop.formDesignerStyle,
					"appId": "process.FormDesigner"+id,
					"onQueryLoad": function(){
						this.actions = _self.app.actions;
						this.options.id = id;
						this.application = _self.data;
					}
				};
				this.app.desktop.openApplication(e, "process.FormDesigner", options);
			}.bind(this));
		}
	},
	openProcess: function(id, e){
		if (id){
			var _self = this;
			var options = {
				"appId": "process.ProcessDesigner"+id,
				"onQueryLoad": function(){
					this.actions = _self.app.actions;
					this.options.id = id;
					this.application = _self.data;
				}
			};
			this.app.desktop.openApplication(e, "process.ProcessDesigner", options);
		}
	},

	setIconNode: function(){
		if (this.data.icon){
			this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
		}else{
			this.iconNode.setStyle("background-image", "url("+"../x_component_process_ApplicationExplorer/$Main/default/icon/application.png)")
		}
		this.iconNode.makeLnk({
			"par": this._getLnkPar()
		});
	},
	_getLnkPar: function(){
		var lnkIcon = "../x_component_process_ApplicationExplorer/$Main/default/lnk.png";
		if (this.data.icon) lnkIcon = "data:image/png;base64,"+this.data.icon;

		var appId = "process.ProcessManager"+this.data.id;
		return {
			"icon": lnkIcon,
			"title": this.data.name,
			"par": "process.ProcessManager#{\"application\": \""+this.data.id+"\", \"appId\": \""+appId+"\"}"
		};
	},
	loadNodes: function(){
		this.iconNode = this.node.getElement(".o2_process_AppExp_item_icon");
		this.setIconNode();
		this.titleNode = this.node.getElement(".o2_process_AppExp_item_titleNode");

		this.formListNode = this.node.getElement(".o2_process_AppExp_item_contentFormList");
		this.processListNode = this.node.getElement(".o2_process_AppExp_item_contentProcessList");
		this.pageListNode = this.node.getElement(".o2_process_AppExp_item_contentPageList");
		this.viewListNode = this.node.getElement(".o2_process_AppExp_item_contentViewList");
		this.statListNode = this.node.getElement(".o2_process_AppExp_item_contentStatList");

		this.titleNode.addEvent("click", function(e){
			this.openApplication(e);
		}.bind(this));
		this.categoryNode = this.node.getElement(".o2_process_AppExp_item_categoryNode");
		var category = this.data.applicationCategory || this.data.portalCategory || this.data.protalCategory;
		var categoryText = category;
		if( !category || category === "null" )categoryText = this.lp.unCategory;
		this.categoryNode.set("text", categoryText );
		if ( category && category !== "null" ){
			this.categoryNode.set("title", category );
			this.categoryNode.addClass("o2_process_AppExp_item_categoryColorNode");
		}
		this.actionArea = this.node.getElement(".o2_process_AppExp_item_ActionArea");
		this.actionDelete = this.node.getElement(".o2_process_AppExp_item_Action_delete");
		this.actionExport = this.node.getElement(".o2_process_AppExp_item_Action_export");

		if (this.actionArea) this.setActionEvent();
	},

	setActionEvent: function(){
		this.node.addEvents({
			"mouseover": function(){
				if (!this.readyDelete) this.actionArea.fade("in");
				this.node.addClass("o2_process_AppExp_item_node_over");
			}.bind(this),
			"mouseout": function(){
				if (!this.readyDelete) this.actionArea.fade("out");
				this.node.removeClass("o2_process_AppExp_item_node_over");
			}.bind(this)
		});
		this.actionDelete.addEvent("click", function(e){
			this.checkDeleteApplication(e);
			e.stopPropagation();
		}.bind(this));

		this.actionExport.addEvent("click", function(e){
			MWF.xDesktop.requireApp("AppCenter", "", function(){
				// var appCenter = new MWF.xApplication.AppCenter.Main();
				// appCenter.inBrowser = true;
				// appCenter.load(true, content);

				if (!this.app.appCenterApp) this.app.createAppCenterApp(this.data.id);
				this.app.appCenterApp.curAppId = this.data.id;
				this.app.appCenterApp.content.show();
				new MWF.xApplication.AppCenter.Exporter(this.app.appCenterApp);

				//appCenter.createApplication(this.app.content,this.data.id);
				//appCenter.createApplication(null,this.data.id);
			}.bind(this));
			e.stopPropagation();
			//老版本导出
			// this.exportApplication(e);
			// e.stopPropagation();
		}.bind(this));
	},
	checkDeleteApplication: function(e){
		if (!this.readyDelete){
			this.actionDelete.addClass("o2_process_AppExp_item_Action_delete_select");
			this.node.addClass("o2_process_AppExp_item_node_del");
			this.readyDelete = true;
			this.app.deleteElements.push(this);
		}else{
			this.actionDelete.removeClass("o2_process_AppExp_item_Action_delete_select");
			this.node.removeClass("o2_process_AppExp_item_node_del");
			this.readyDelete = false;
			this.app.deleteElements.erase(this);
		}
		this.app.checkDeleteApplication();
	},

	exportApplication: function(){

		MWF.xDesktop.requireApp("process.ApplicationExplorer", "Exporter", function(){
			(new MWF.xApplication.process.ApplicationExplorer.Exporter(this.app, this.data)).load();
		}.bind(this));
	},
	resizeContent: function(){
		var w = this.dimension.width-20;
		this.node.setStyle("width", ""+w+"px");
	},
	"delete": function(onlyRemoveNotCompleted, success, failure){
		this._deleteElement(this.data.id, onlyRemoveNotCompleted, function(){
			this.destroy();
			if (success) success();
		}.bind(this), function(xhr, text, error){
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			//	this.explorer.app.notice(errorText, "error", this.explorer.propertyContentNode, {x: "left", y:"top"});

			if (failure) failure(errorText);
		}.bind(this));
	},
	_deleteElement: function(id, onlyRemoveNotCompleted, success, failure){
		this.app.restActions.deleteApplication(id, onlyRemoveNotCompleted, success, failure);
	},
	destroy: function(){
		if (this.resizeContentFun) this.app.removeEvent("resize", this.resizeContentFun);
		this.node.destroy();
		o2.release(this);
	}
});












