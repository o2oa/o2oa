MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.CMSE = MWF.xApplication.cms.Module = MWF.xApplication.cms.Module ||{};
MWF.require("MWF.widget.O2Identity", null,false);
//MWF.xDesktop.requireApp("cms.Module", "Actions.RestActions", null, false);
MWF.xApplication.cms.Module.options = {
	multitask: false,
	executable: true
};
MWF.xApplication.cms.Module.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "cms.Module",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"isCategory" : false,
		"searchKey" : "",
		"title": MWF.xApplication.cms.Module.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.cms.Module.LP;

	},
	onQueryClose : function(){
		if (window.clipboardData){
			if (this.keyCopyItemsFun)this.removeEvent("copy", this.keyCopyItemsFun);
			if (this.keyPasteItemsFun)this.removeEvent("paste", this.keyPasteItemsFun);
		}else{
			if (this.keyCopyItemsFun) document.removeEventListener('copy',  this.keyCopyItemsFun);
			if (this.keyPasteItemsFun) document.removeEventListener('paste', this.keyPasteItemsFun);
		}
	},
	loadApplication: function(callback){
		//this.controllers = [];
		this.isAdmin = false;
		this.restActions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Module.Actions.RestActions();
		this.createNode();
		this.loadApplicationContent();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.node
		}).inject(this.content);

		this.naviContainerNode = new Element("div.naviContainerNode", {
			"styles": this.css.naviContainerNode
		}).inject(this.node);
		this.leftTitleNode = new Element("div.leftTitleNode", {
			"styles": this.css.leftTitleNode
		}).inject(this.naviContainerNode);

		this.rightContentNode = new Element("div", {
			"styles":this.css.rightContentNode
		}).inject(this.node);
		this.titleBar = new Element("div", {
			"styles": this.css.titleBar
		}).inject(this.rightContentNode );
		this.titleActionBar = new Element("div", {
			"styles": this.css.titleActionBar
		}).inject(this.titleBar );
	},
	loadApplicationContent: function(){
		var columnId = (this.options.columnData && this.options.columnData.id ) ||
			(this.status && this.status.columnId) || this.options.columnId;
		var columnAlias = this.options.columnAlias || (this.options.columnData && this.options.columnData.columnAlias ) ||
			(this.status && this.status.columnAlias );
		// if( this.options.columnData ){
		// 	this.setColumnDataConfig();
		// 	this.setTitle(this.options.columnData.appName);
		// 	this.loadController(function(){
		// 		this.loadTitle(function(){
		// 			this.loadMenu();
		// 		}.bind(this));
		// 	}.bind(this))
		// }else
		if( columnId ){
			this.loadColumnData( columnId, function(){
				this.loadController(function(){
					this.loadTitle(function(){
						this.loadMenu();
					}.bind(this));
				}.bind(this))
			}.bind(this))
		}else if( columnAlias ){
			this.restActions.getColumnByAlias( columnAlias, function( json ){
				this.columnData = json.data;
				this.setColumnDataConfig();
				this.setTitle(this.columnData.appName);
				this.loadController(function(){
					this.loadTitle(function(){
						this.loadMenu();
					}.bind(this));
				}.bind(this))
			}.bind(this))
		}
	},
	setColumnDataConfig : function(){
		if( !this.columnData.config ){
			this.columnData.config = {};
		}else if( typeOf(this.columnData.config) === "string" ){
			this.columnData.config = JSON.parse( this.columnData.config || {} );
		}
	},
	loadColumnData : function(columnId, callback){
		this.restActions.getColumn( columnId, function( json ){
			this.columnData = json.data;
			this.setTitle(this.columnData.appName);
			this.setColumnDataConfig();

			//MWF.require("MWF.xScript.Actions.CMSScriptActions", null, false);
			//MWF.require("o2.xScript.Macro", null, false);
			//var scriptAction = new MWF.xScript.Actions.CMSScriptActions();
			//scriptAction.getScriptByName( this.columnData.id, "_config", [], function(json){
			//	if (json.data){
			//		try{
			//			this.columnData = Object.merge(this.columnData,JSON.parse(json.data.text));
			//		}catch(e){
			//		}
			//	}
			//}.bind(this), null, false);

			if(callback)callback()
		}.bind(this))
	},
	loadController: function(callback){
		//this.restActions.listColumnController(this.columnData.id, function( json ){
		//	json.data = json.data || [];
		//	json.data.each(function(item){
		//		this.controllers.push(item.adminUid)
		//	}.bind(this));
		//	this.isAdmin = MWF.AC.isCMSManager() || this.controllers.contains(layout.desktop.session.user.distinguishedName);
		//	if(callback)callback(json);
		//}.bind(this));
		this.restActions.isAppInfoManager( this.columnData.id, function( json ){
			this.isAdmin = MWF.AC.isCMSManager() || json.data.value;
			if(callback)callback(json);
		}.bind(this))
	},
	loadTitle : function(callback){
		if( this.isAdmin ){
			//this.loadImportActionNode();
			//this.loadExportActionNode();
		}
		this.loadCreateDocumentActionNode(
			function(){
				this.loadTitleIconNode();
				this.loadTitleContentNode();
				this.loadBatchAction();
				this.loadPastEvent();
				this.loadSearchNode();
				if(callback)callback();
			}.bind(this)
		);
	},
	loadBatchAction: function(){
		if( !this.isAdmin )return;
		this.batchAction = new Element("div", {
			"styles": this.css.batchAction,
			"text" : this.lp.select
		}).inject(this.titleActionBar);
		this.batchAction.addEvents({
			"click": function(e){
				if( this.view ){
					if( this.view.selectEnable ){
						this.selectEnable = false;
						this.batchAction.setStyles( this.css.batchAction );
						this.batchAction.set("text",this.lp.select);
						this.view.disableSelectMode();

						this.cancelBatchRemoveAction();
						this.cancelChangeCategoryAction();
						this.cancelCopyActionNode();
					}else{
						this.selectEnable = true;
						this.batchAction.setStyles( this.css.batchAction_over );
						this.batchAction.set("text", this.lp.cancelSelect);
						this.view.selectMode();

						this.loadCopyActionNode();
						this.loadChangeCategoryAction();
						this.loadBatchRemoveAction();
					}
				}
			}.bind(this),
			"mouseover" : function(e){
				if( this.view.selectEnable )return;
				this.batchAction.setStyles( this.css.batchAction_over )
			}.bind(this),
			"mouseout" : function(e){
				if( this.view.selectEnable )return;
				this.batchAction.setStyles( this.css.batchAction )
			}.bind(this)
		});
	},
	getSearchBarSize : function(){
		var x_action = this.titleActionBar.getSize().x;
		var x_titlebar = this.titleBar.getSize().x;
		return x_titlebar - x_action;
	},
	loadPastEvent : function(){
		if( !this.isAdmin )return;
		this.keyPasteItemsFun = this.keyPasteItems.bind(this);
		if (window.clipboardData){
			this.addEvent("paste", this.keyPasteItemsFun);
		}else{
			document.addEventListener('paste', this.keyPasteItemsFun);
			//this.addEvent("queryClose", function(){
			//	if (this.keyPasteItemsFun) document.removeEventListener('paste', this.keyPasteItemsFun);
			//}.bind(this));
		}
	},
	loadCreateDocumentActionNode: function( callback ){
		this.restActions.listCategoryByPublisher( this.columnData.id, function( json ){
			if( json.data && json.data.length ){
				this.createDocumentAction = new Element("div", {
					"styles": this.css.createDocumentAction,
					"text" : this.lp.start
				}).inject(this.titleActionBar);
				this.createDocumentAction.addEvents({
					"click": function(e){
						MWF.xDesktop.requireApp("cms.Index", "Newer", null, false);

						//if(this.columnData.latest===undefined) this.columnData.latest = true;
						//if(this.columnData.ignoreTitle===undefined) this.columnData.ignoreTitle = false;

						this.creater = new MWF.xApplication.cms.Index.Newer( this.columnData, null, this, this.view, {
							restrictToColumn : true
							// onAfterPublish : function () {
							// 	try{
							// 		if(this.view && this.view.reload){
							// 			this.view.reload();
							// 		}
							// 	}catch (e) {
							// 	}
							// }.bind(this)
							//ignoreTitle : this.columnData.ignoreTitle,
							//latest : this.columnData.latest
						});
						this.creater.load();
					}.bind(this),
					"mouseover" : function(e){
						this.createDocumentAction.setStyles( this.css.createDocumentAction_over )
					}.bind(this),
					"mouseout" : function(e){
						this.createDocumentAction.setStyles( this.css.createDocumentAction )
					}.bind(this)
				});
			}
			if(callback)callback();
		}.bind(this));
	},

	cancelChangeCategoryAction : function(){
		if(this.moveAction)this.moveAction.destroy();
		this.moveAction = null;
	},
	loadChangeCategoryAction : function(){
		if( !this.isAdmin )return;
		this.moveAction = new Element("div", {
			"styles": this.css.moveDocumentAction,
			"text" : this.lp.move //"移动"
		}).inject(this.titleActionBar);
		this.moveAction.addEvents({
			"click": function(e){
				var _self = this;
				if( this.view ){
					var itemIds = this.view.getSelectedIds();
					if (!itemIds.length) {
						this.notice( _self.lp.selectDocNotice, "error"); //"请先选择文档"
						return;
					}
					this.loadSelectColumnDialog( function( data ){
						if( data && data.id ){
							var text = _self.lp.moveDocConfirmContent.replace("{count}", itemIds.length ).replace("{category}", data.categoryName);
								//"移动后将在本分类删除，确定要移动选中的"+itemIds.length+"个文档到"+data.categoryName+"？";
							this.confirm("warn", e, _self.lp.moveDocConfirmTitle, text, 350, 120, function(){
								_self.restActions.moveDocumentToCategory({
									ids : itemIds,
									categoryId : data.id
								}, function(){
									_self.notice( _self.lp.moveDocSuccessNotice, "success"); //"移动成功"
									_self.view.reload();
									this.close();
								}.bind(this))
							}, function(){
								this.close();
							});
						}
					}.bind(this))
				}
			}.bind(this),
			"mouseover" : function(e){
				this.moveAction.setStyles( this.css.moveDocumentAction_over )
			}.bind(this),
			"mouseout" : function(e){
				this.moveAction.setStyles( this.css.moveDocumentAction )
			}.bind(this)
		});
	},

	cancelBatchRemoveAction : function(){
		if(this.batchRemoveAction)this.batchRemoveAction.destroy();
		this.batchRemoveAction = null;
	},
	loadBatchRemoveAction : function(){
		if( !this.isAdmin )return;
		var _self = this;

		this.batchRemoveAction = new Element("div", {
			"styles": this.css.batchRemoveDocumentAction,
			"text" : this.lp.batchRemove
		}).inject(this.titleActionBar);
		this.batchRemoveAction.addEvents({
			"click": function(e){
				var _self = this;
				if( this.view ){
					var itemIds = this.view.getSelectedIds();
					if (itemIds.length) {
						_self.readyRemove = true;
						var text = _self.lp.clearDocConfirmContent.replace("{count}", itemIds.length);
						// var text = "删除后无法恢复，确定要删除选中的"+itemIds.length+"个文档？";
						this.confirm("warn", e, _self.lp.clearDocConfirmTitle, text, 350, 120, function(){

							_self.removeDocumentList(itemIds);

							this.close();

						}, function(){
							_self.readyRemove = false;
							this.close();
						});
					}else{
						this.notice( _self.lp.selectDocNotice,"error")
					}
				}
			}.bind(this),
			"mouseover" : function(e){
				this.batchRemoveAction.setStyles( this.css.batchRemoveDocumentAction_over )
			}.bind(this),
			"mouseout" : function(e){
				this.batchRemoveAction.setStyles( this.css.batchRemoveDocumentAction )
			}.bind(this)
		});
	},
	//loadBatchRemoveAction : function(){
	//	if( !this.isAdmin )return;
	//
	//	this.batchRemoveAction = new Element("div", {
	//		"styles": this.css.batchRemoveDocumentAction,
	//		"text" : this.lp.batchRemove
	//	}).inject(this.titleBar);
	//	this.batchRemoveAction.addEvents({
	//		"click": function(e){
	//			if( this.view ){
	//				if( this.view.selectEnable ){
	//					this.view.disableSelectMode();
	//					this.batchRemoveConfirmAction.setStyle("display","none");
	//					//this.batchRemoveAction.set("text",this.lp.batchRemove);
	//				}else{
	//					this.view.selectMode();
	//					this.batchRemoveConfirmAction.setStyle("display","");
	//					//this.batchRemoveAction.set("text",this.lp.cancel);
	//				}
	//			}
	//		}.bind(this),
	//		"mouseover" : function(e){
	//			this.batchRemoveAction.setStyles( this.css.batchRemoveDocumentAction_over )
	//		}.bind(this),
	//		"mouseout" : function(e){
	//			this.batchRemoveAction.setStyles( this.css.batchRemoveDocumentAction )
	//		}.bind(this)
	//	});
	//
	//
	//	this.batchRemoveConfirmAction = new Element("div", {
	//		"styles": this.css.batchRemoveConfirmDocumentAction,
	//		"text" : this.lp.batchRemoveConfirm
	//	}).inject(this.titleBar);
	//	var _self = this;
	//	this.batchRemoveConfirmAction.addEvents({
	//		"click": function (e) {
	//			var itemIds = this.view.getSelectedIds();
	//			if (itemIds.length) {
	//				_self.readyRemove = true;
	//				var text = "删除后无法恢复，确定要删除选中的"+itemIds.length+"个文档？";
	//				this.confirm("warn", e, "清除确认", text, 350, 120, function(){
	//
	//					_self.removeDocumentList(itemIds);
	//
	//					this.close();
	//
	//				}, function(){
	//					_self.readyRemove = false;
	//					this.close();
	//				});
	//			}else{
	//				this.notice("请先选择文档","error")
	//			}
	//		}.bind(this)
	//	});
	//	this.batchRemoveConfirmAction.setStyle("display","none");
	//},
	removeDocumentList : function( itemIds ){
		var count = 0;
		itemIds.each( function(id){
			this.restActions.removeDocument(id, function(json){
				count++;
				if( count === itemIds.length ){
					this.notice( this.lp.clearDocSuccessNotice, "success");
					//this.view.disableSelectMode();
					this.view.reload();
				}
			}.bind(this));
		}.bind(this))
	},

	cancelCopyActionNode : function(){
		if (window.clipboardData){
			if (this.keyCopyItemsFun)this.removeEvent("copy", this.keyCopyItemsFun);
		}else{
			if (this.keyCopyItemsFun) document.removeEventListener('copy',  this.keyCopyItemsFun);
		}
		this.keyCopyItemsFun = null;
		if(this.copyAction)this.copyAction.destroy();
		this.copyAction = null;
	},
	loadCopyActionNode : function(){
		if( !this.isAdmin )return;
		this.copyAction = new Element("div", {
			"styles": this.css.copyDocumentAction,
			"text" : this.lp.enableCopy
		}).inject(this.titleActionBar);
		this.copyAction.addEvents({
			"click": function(e){
				if( this.view ){
					if( this.keyCopyItemsFun )return;
					this.keyCopyItemsFun = this.keyCopyItems.bind(this);
					if (window.clipboardData){
						this.addEvent("copy", this.keyCopyItemsFun);
					}else{
						document.addEventListener('copy',  this.keyCopyItemsFun);
						//this.addEvent("queryClose", function(){
						//	if (this.keyCopyItemsFun) document.removeEventListener('copy',  this.keyCopyItemsFun);
						//}.bind(this));
					}
					this.notice( this.lp.copyInfor );
				}
			}.bind(this),
			"mouseover" : function(e){
				this.copyAction.setStyles( this.css.copyDocumentAction_over )
			}.bind(this),
			"mouseout" : function(e){
				this.copyAction.setStyles( this.css.copyDocumentAction )
			}.bind(this)
		});
	},
	//loadCopyActionNode : function(){
	//	if( !this.isAdmin )return;
	//	this.copyAction = new Element("div", {
	//		"styles": this.css.copyDocumentAction,
	//		"text" : this.lp.copy
	//	}).inject(this.titleBar);
	//	this.copyAction.addEvents({
	//		"click": function(e){
	//			if( this.view ){
	//				if( this.view.selectEnable ){
	//					this.view.disableSelectMode();
	//					if (window.clipboardData){
	//						if (this.keyCopyItemsFun)this.removeEvent("copy", this.keyCopyItemsFun);
	//					}else{
	//						if (this.keyCopyItemsFun) document.removeEventListener('copy',  this.keyCopyItemsFun);
	//					}
	//					this.keyCopyItemsFun = null;
	//				}else{
	//					this.view.selectMode();
	//					this.keyCopyItemsFun = this.keyCopyItems.bind(this);
	//					if (window.clipboardData){
	//						this.addEvent("copy", this.keyCopyItemsFun);
	//					}else{
	//						document.addEventListener('copy',  this.keyCopyItemsFun);
	//						//this.addEvent("queryClose", function(){
	//						//	if (this.keyCopyItemsFun) document.removeEventListener('copy',  this.keyCopyItemsFun);
	//						//}.bind(this));
	//					}
	//					this.notice( this.lp.copyInfor );
	//				}
	//			}
	//		}.bind(this),
	//		"mouseover" : function(e){
	//			this.copyAction.setStyles( this.css.copyDocumentAction_over )
	//		}.bind(this),
	//		"mouseout" : function(e){
	//			this.copyAction.setStyles( this.css.copyDocumentAction )
	//		}.bind(this)
	//	});
	//},
	keyCopyItems: function(e){
		if (layout.desktop.currentApp && layout.desktop.currentApp.appId===this.appId) {
			var itemIds = this.view.getSelectedIds();
			if (itemIds.length) {
				var items = [];
				var i = 0;

				var checkItems = function (e) {
					if (i >= itemIds.length) {
						if (items.length) {
							var str = JSON.encode(items);
							if (e && e.clipboardData) {
								e.clipboardData.setData('text/plain', str);
								e.preventDefault();
							} else {
								window.clipboardData.setData("Text", str);
							}
							this.notice(this.lp.copyed, "success");
						}
					}
				}.bind(this);

				itemIds.each(function (id) {
					this.restActions.getDocument(id, function (json) {
						json.data.elementType = "cmsDocument";
						items.push(json.data);
						i++;
						checkItems(e);
					}.bind(this), null, false)
				}.bind(this));
			}
		}
	},
	keyPasteItems: function(e){
		if (layout.desktop.currentApp && layout.desktop.currentApp.appId===this.appId) {
			var dataStr = "";
			if (e && e.clipboardData) {
				dataStr = e.clipboardData.getData('text/plain');
			} else {
				dataStr = window.clipboardData.getData("Text");
			}
			var data = JSON.decode(dataStr);

			this.listPublishableCategoryInfo(function(){
				this.pasteItem(data, 0);
			}.bind(this))
		}
	},
	listPublishableCategoryInfo : function( callback ){
		this.publishableCategoryInfoObject_id = {};
		this.publishableCategoryInfoObject_alias = {};
		this.publishableCategoryInfoObject_name = {};
		this.categoryTransformMap = {};
		this.categoryRadioHtml = "";
		o2.Actions.load("x_cms_assemble_control").CategoryInfoAction.listPublishableCategoryInfo( this.columnData.id, function(json){
			( json.data || [] ).each( function(c){
				this.publishableCategoryInfoObject_id[c.id] = c;
				this.publishableCategoryInfoObject_alias[c.categoryAlias] = c;
				this.publishableCategoryInfoObject_name[c.categoryName] = c;
				this.categoryRadioHtml += "<div><input type='radio' name='categoryRadio' value='"+ c.id+"'/>" + c.categoryName + "(" + c.categoryAlias +")</div>"
			}.bind(this));
			this.categoryRadioHtml = "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'>" +
				this.categoryRadioHtml + "</div>";
			if(callback)callback();
		}.bind(this));
	},
	pasteItem: function(data, i){
		if (i<data.length){
			var item = data[i];
			if (item.elementType==="cmsDocument"){
				this.saveItemAs(item, function(){
					i++;
					this.pasteItem(data, i);
				}.bind(this), function(){
					i++;
					this.pasteItem(data, i);
				}.bind(this), function(){
					this.view.reload();
				}.bind(this));
			}else{
				i++;
				this.pasteItem(data, i);
			}
		}else{
			this.view.reload();
		}
	},
	saveItemAs: function(data, success, failure, cancel){
		var lp = this.lp;
		var _self = this;
		if( this.publishableCategoryInfoObject_id[ data.document.categoryId ] ){
			this._saveItemAs(data, success, failure, cancel );
		}else if( this.categoryTransformMap[ data.document.categoryId ] ){
			this._saveItemAs(data, success, failure, cancel, this.categoryTransformMap[ data.document.categoryId ] );
		}else{
			var text;
			if( this.publishableCategoryInfoObject_alias[ data.document.categoryAlias ] ){
				text = lp.copyConfirmCategoryInfor_hasSameAlias + "。<br/>" + lp.copyConfirmCateogyrInfor_withChoice
			}else if( this.publishableCategoryInfoObject_name[ data.document.categoryName ] ){
				text = lp.copyConfirmCategoryInfor_hasSameName + "。<br/>" + lp.copyConfirmCateogyrInfor_withChoice
			}else{
				text = lp.copyConfirmCategoryInfor_noCategory + "："
			}
			text = text.replace("{alias}", "（<span style='color:red;'>" + data.document.categoryAlias + "</span>）" );
			text = text.replace("{name}", "（<span style='color:red;'>" + data.document.categoryName + "</span>）" );

			var html = "<div style='overflow-y:auto;height:300px'>";
			html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'>";
			html += "	<div style='clear: both;font-weight: bold; font-size:14px;'>"+lp.copyTarget+" "+data.document.title+"</div>";
			html += "	<div style='font-size:12px; color: #666666; float: left;'>"+data.document.publishTime+"</div>";
			html += "	<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(data.document.creatorPersonShort)+"</div>";
			html += "</div>";

			html += "<div>" + text + "</div>" + this.categoryRadioHtml;
			html += "<div><input type='checkbox' value='true' name='useSameChoice'>"+lp.copyConfirm_SameCategory+"</div>";
			html += "</div>";

//                html += "<>"
			this.dlg("inofr", null, lp.copyConfirmCategoryTitle, {"html": html}, 500, 450, [
				{
					"text": lp.copy,
					"action": function(){
						var categoryRadio = this.node.getElements("[name='categoryRadio']");
						var checkbox = this.node.getElement("[name='useSameChoice']");

						var newCategory;
						for( var i=0; i<categoryRadio.length; i++ ){
							if( categoryRadio[i].checked ){
								newCategory = _self.publishableCategoryInfoObject_id[ categoryRadio[i].get("value") ];
							}
						}
						if( !newCategory ){
							if( _self.publishableCategoryInfoObject_alias[ data.document.categoryAlias ] ){
								newCategory = _self.publishableCategoryInfoObject_alias[ data.document.categoryAlias ];
							}else if( _self.publishableCategoryInfoObject_name[ data.document.categoryName ] ){
								newCategory = _self.publishableCategoryInfoObject_name[ data.document.categoryName ];
							}
						}
						if( newCategory ){
							if( checkbox.checked )_self.categoryTransformMap[ data.document.categoryId ] = newCategory;
							this.close();
							_self._saveItemAs(data, success, failure, cancel, newCategory );
						}else{
							_self.notice( lp.notSelectCategory, "error" )
						}
						//_self.saveItemAsUpdate(someItem, data, success, failure);
					}
				},
				{
					"text": lp.copyConfirm_skip,
					"action": function(){/*nothing*/ this.close(); if (success) success();}
				},
				{
					"text": lp.copyConfirm_cancel,
					"action": function(){this.close(); if (cancel) cancel();}
				}
			]);

		}
	},
	_saveItemAs: function(data, success, failure, cancel, newCategory ){
		this.restActions.getDocument(data.document.id, function(dJson){
			var someItem = dJson.data;
			var flag = false;
			if (someItem){
				if( newCategory ){
					if( newCategory.id !== someItem.document.categoryId ){ //如果已有文档的分类和新分类不一样，直接新建
						this.saveItemAsNew(data, success, failure, true, newCategory)
					}else{ //如果已有文档的分类和新分类一样，需要询问
						flag = true;
					}
				}else{  //如果使用原有分类，需要询问
					flag = true;
				}

				if( flag ){
					var lp = this.lp;
					var _self = this;

					var d1 = new Date().parse(data.document.publishTime);
					var d2 = new Date().parse(someItem.document.publishTime);
					var html = "<div>"+lp.copyConfirmInfor+"</div>";
					html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='font-weight: bold; font-size:14px;'>"+lp.copySource+" "+someItem.document.title+"</div>";
					html += "<div style='font-size:12px; color: #666666; float: left'>"+someItem.document.publishTime+"</div>" +
						"<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(someItem.document.creatorPersonShort)+"</div>" +
						"<div style='color: red; float: right;'>"+((d1>=d2) ? "": lp.copynew)+"</div></div>";
					html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='clear: both;font-weight: bold; font-size:14px;'>"+lp.copyTarget+" "+data.document.title+"</div>";
					html += "<div style='font-size:12px; color: #666666; float: left;'>"+data.document.publishTime+"</div>" +
						"<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(data.document.creatorPersonShort)+"</div>" +
						"<div style='color: red; float: right;'>"+((d1<=d2) ? "": lp.copynew)+"</div></div>";
	//                html += "<>"
					this.dlg("inofr", null, lp.copyConfirmTitle, {"html": html}, 500, 290, [
						{
							"text": lp.copyConfirm_overwrite,
							"action": function(){_self.saveItemAsUpdate(someItem, data, success, failure);this.close();}
						},
						{
							"text": lp.copyConfirm_new,
							"action": function(){_self.saveItemAsNew( data, success, failure, true, newCategory );this.close();}
						},
						{
							"text": lp.copyConfirm_skip,
							"action": function(){/*nothing*/ this.close(); if (success) success();}
						},
						{
							"text": lp.copyConfirm_cancel,
							"action": function(){this.close(); if (cancel) cancel();}
						}
					]);

				}
			}
		}.bind(this), function(){
			//if (failure) failure();
			this.saveItemAsNew(data, success, failure, false, newCategory)
		}.bind(this));
	},
	saveItemAsUpdate: function(someItem, data, success, failure){
		var doc = data.document;

		doc.id = someItem.document.id;
		doc.isNewDocument = false;

		doc.appId = someItem.document.appId;
		doc.appName = someItem.document.appName;

		doc.categoryId = someItem.document.categoryId;
		doc.categoryName = someItem.document.categoryName;
		doc.categoryAlias = someItem.document.categoryAlias;

		doc.form = someItem.document.form;
		doc.formName = someItem.document.formName;
		doc.readFormId = someItem.document.readFormId;
		doc.readFormName = someItem.document.readFormName;


		doc.docData = data.data;

		this.restActions.saveDocument(doc, function(){
			if (success) success();
		}.bind(this), function(){
			if (failure) failure();
		}.bind(this));
	},
	saveItemAsNew: function(data, success, failure, clearId, newCategory){

		var columnData = this.columnData;

		var doc = data.document;
		if( clearId ){
			delete doc.id;
		}

		delete doc.documentType;
		delete doc.appId;
		delete doc.appName;
		delete doc.appAlias;
		// delete doc.categoryId;
		delete doc.categoryName;
		delete doc.categoryAlias;
		delete doc.form;
		delete doc.formName;
		delete doc.readFormId;
		delete doc.readFormName;

		doc.appId = columnData.id;

		doc.docData = data.data;
		delete doc.docData.$document;

		var callback = function(data){
			this.restActions.publishDocumentComplex(data, function(){
				if (success) success();
			}.bind(this), function(){
				if (failure) failure();
			}.bind(this));
		}.bind(this);

		debugger;

		if( newCategory ){
			doc.categoryId = newCategory.id;
			callback( doc );
		}else{
			this.restActions.getCategory( data.document.categoryId, function( json ){
				if( json.data.appId === columnData.id ){
					doc.categoryId = data.document.categoryId;
					callback( doc );
				}else{
					this.loadSelectCategoryDialog( this.lp.selectCategoryText.replace("{title}", data.document.title ), function(id){
						doc.categoryId = id;
						callback( doc );
					}.bind(this))
				}
			}.bind(this), function(){
				this.loadSelectCategoryDialog( this.lp.selectCategoryText.replace("{title}", data.document.title), function(id){
					doc.categoryId = id;
					callback( doc );
				}.bind(this))
			}.bind(this))
		}
	},
	loadSelectColumnDialog : function( callback){
		MWF.xDesktop.requireApp("Selector", "package", null, false);
		var options = {
			"type": "CMSCategory",
			"count": 1,
			"onComplete": function(items){
				items.each(function(item){
					if( callback )callback( item.data );
				}.bind(this));
			}.bind(this)
		};

		var selector = new MWF.O2Selector(this.content, options);
	},
	loadSelectCategoryDialog : function(title, callback){
		if( !this.categoryList ){
			this.categoryList = [];
			this.restActions.listCategory( this.columnData.id, function( json ){
				json.data.each( function(d){
					this.categoryList.push( {
						name : d.categoryName,
						id : d.id
					})
				}.bind(this))
			}.bind(this), null, false)
		}
		MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
		var opt  = {
			"count": 1,
			"title": this.lp.selectCategory,
			"selectableItems" : this.categoryList,
			"values": [],
			"onComplete": function( array ){
				if( !array || array.length == 0 )return;
				var id = array[0].data.id;
				callback( id )
			}.bind(this)
		};
		var selector = new MWF.xApplication.Template.Selector.Custom(this.content, opt );
		selector.load();
	},

	loadImportActionNode : function(){
		this.importAction = new Element("div", {
			"styles": this.css.importAction,
			"text" : this.lp.import
		}).inject(this.titleActionBar);
		this.importAction.setStyle("display","none");
		this.importAction.addEvents({
			"click": function(e){
				MWF.xDesktop.requireApp("cms.Module", "ExcelForm", null, false);
				var categoryData = this.navi.currentObject.isCategory ? this.navi.currentObject.data : this.navi.currentObject.category.data ;
				this.import = new MWF.xApplication.cms.Module.ImportForm( { app : this }, categoryData, {} );
				this.import.edit();
			}.bind(this),
			"mouseover" : function(e){
				this.importAction.setStyles( this.css.importAction_over )
			}.bind(this),
			"mouseout" : function(e){
				this.importAction.setStyles( this.css.importAction )
			}.bind(this)
		});
	},
	loadExportActionNode : function(){
		this.exportAction = new Element("div", {
			"styles": this.css.exportAction,
			"text" : this.lp.export
		}).inject(this.titleActionBar);
		this.exportAction.setStyle("display","none");
		this.exportAction.addEvents({
			"click": function(e){
				MWF.xDesktop.requireApp("cms.Module", "ExcelForm", null, false);
				var categoryData = this.navi.currentObject.isCategory ? this.navi.currentObject.data : this.navi.currentObject.category.data ;
				this.export = new MWF.xApplication.cms.Module.ExportForm ( { app : this }, categoryData, {} );
				this.export.edit();
			}.bind(this),
			"mouseover" : function(e){
				this.exportAction.setStyles( this.css.exportAction_over )
			}.bind(this),
			"mouseout" : function(e){
				this.exportAction.setStyles( this.css.exportAction )
			}.bind(this)
		});
	},
	loadTitleIconNode : function(){

		this.defaultColumnIcon = "../x_component_cms_Index/$Main/"+this.options.style+"/icon/column.png";

		var iconAreaNode = this.iconAreaNode = new Element("div",{
			"styles" : this.css.titleIconAreaNode
		}).inject(this.leftTitleNode);

		var iconNode = this.iconNode = new Element("img",{
			"styles" : this.css.titleIconNode
		}).inject(iconAreaNode);
		if (this.columnData.appIcon){
			this.iconNode.set("src", "data:image/png;base64,"+this.columnData.appIcon+"");
		}else{
			this.iconNode.set("src", this.defaultColumnIcon)
		}
		iconNode.makeLnk({
			"par": this._getLnkPar()
		});
	},
	_getLnkPar: function(){
		var lnkIcon = this.defaultColumnIcon;
		if (this.columnData.appIcon) lnkIcon = "data:image/png;base64,"+this.columnData.appIcon;

		var appId = "cms.Module"+this.columnData.id;
		return {
			"icon": lnkIcon,
			"title": this.columnData.appName,
			"par": "cms.Module#{\"columnId\": \""+this.columnData.id+"\", \"appId\": \""+appId+"\"}"
		};
	},
	loadTitleContentNode: function(){
		this.titleContentNode = new Element("div.titleContentNode", {
			"styles": this.css.titleContentNode
		}).inject(this.leftTitleNode);

		this.titleTextNode = new Element("div.titleTextNode", {
			"styles": this.css.titleTextNode,
			"text": this.columnData.appName,
			"title": this.columnData.appName
		}).inject(this.titleContentNode);

		this.titleDescriptionNode =  new Element("div.titleDescriptionNode", {
			"styles": this.css.titleDescriptionNode,
			"text": this.columnData.description ? this.columnData.description : this.lp.noDescription,
			"title": this.columnData.description ? this.columnData.description : this.lp.noDescription
		}).inject(this.titleContentNode);
	},
	loadSearchNode : function(){
		this.searchNode = new Element("div").inject( this.titleBar );
	},
	loadMenu: function(callback){

		this.naviNode = new Element("div.naviNode", {
			"styles": this.css.naviNode
		}).inject(this.naviContainerNode);

		//this.setScrollBar(this.naviNode,{"where": "before"});
		MWF.require("MWF.widget.ScrollBar", function(){
			new MWF.widget.ScrollBar(this.naviContainerNode, {
				"style":"xApp_ProcessManager_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
			});
		}.bind(this));

		this.addEvent("resize", function(){this.setNaviSize();}.bind(this));

		//MWF.require("MWF.widget.ScrollBar", function(){
		//	new MWF.widget.ScrollBar(this.menuNode, {
		//		"style":"xApp_CMSModule_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
		//	});
		//}.bind(this));
		if( this.options.categoryId == "all" ){
			this.options.categoryId = "whole";
		}
		if( this.status && this.status.categoryId ){
			this._loadMenu( this.status );
		}else if( this.options.categoryId && this.options.categoryId != "" ){
			if( this.options.viewId && this.options.viewId!="" ){
				this._loadMenu( { "categoryId" :this.options.categoryId , "viewId" : this.options.viewId } )
			}else{
				//this.getCategoryDefaultList(this.options.categoryId , function(viewId){
				//	if( viewId ){
				//		this._loadMenu( { "categoryId" :this.options.categoryId , "viewId" : viewId, "isCategory" : this.options.isCategory } );
				//	}else{
				//		this._loadMenu( { "categoryId" :this.options.categoryId , "isCategory" : this.options.isCategory, "naviIndex" : (this.options.naviIndex || 0) } );
				//	}
				//}.bind(this))
				this._loadMenu( { "categoryId" :this.options.categoryId , "isCategory" : true, "naviIndex" : (this.options.naviIndex || 0) } ); //this.options.isCategory
			}
		}else if( this.options.categoryAlias && this.options.categoryAlias != "" ){
			this.restActions.getCategoryByAlias( this.options.categoryAlias, function( json ){
				this.options.categoryId = json.data.id;
				if( this.options.viewId && this.options.viewId!="" ){
					this._loadMenu( { "categoryId" :this.options.categoryId , "viewId" : this.options.viewId } )
				}else{
					this._loadMenu( { "categoryId" :this.options.categoryId , "isCategory" : true, "naviIndex" : (this.options.naviIndex || 0) } ); //this.options.isCategory
				}
			}.bind(this))
		}else{
			this._loadMenu( { "categoryId" :"whole" } )
		}
	},
	_loadMenu : function( options ){
		this.navi = new MWF.xApplication.cms.Module.Navi(this, this.naviNode, this.columnData, options );
		this.setNaviSize();
	},
	clearContent: function(){
		//debugger;
		if (this.moduleContent){
			if (this.view) delete this.view;
			this.moduleContent.destroy();
			this.searchNode.empty();
			this.moduleContent = null;
		}
	},
	openView : function(el, categoryData, revealData, searchKey, navi){
		if( revealData && revealData.type == "queryview" ){
			this.loadQueryView(el, categoryData, revealData, searchKey, navi);
		}else{
			this.loadList(el, categoryData, revealData, searchKey, navi);
		}
	},
	loadQueryView : function(el, categoryData, revealData, searchKey, navi){
		MWF.xDesktop.requireApp("cms.Module", "ViewExplorer", function(){
			this.clearContent();
			this.moduleContent = new Element("div", {
				"styles": this.css.moduleContent
			}).inject(this.rightContentNode);
			this.view = new MWF.xApplication.cms.Module.ViewExplorer(
				this.moduleContent,
				this,
				this.columnData,
				categoryData,
				revealData,
				{"isAdmin": this.isAdmin, "searchKey" : searchKey },
				this.searchNode
			);
			this.view.selectEnable = this.selectEnable;
			this.view.load();
		}.bind(this))

	},
	loadList : function(el, categoryData, revealData, searchKey, navi){

		MWF.xDesktop.requireApp("cms.Module", "ListExplorer", function(){
			this.clearContent();
			this.moduleContent = new Element("div", {
				"styles": this.css.moduleContent
			}).inject(this.rightContentNode);
			if (!this.restActions) this.restActions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Module.Actions.RestActions();
			this.view = new MWF.xApplication.cms.Module.ListExplorer(
				this.moduleContent,
				this.restActions,
				this.columnData,
				categoryData,
				revealData,
				{"isAdmin": this.isAdmin, "searchKey" : searchKey },
				this.searchNode
			);
			this.view.app = this;
			this.view.selectEnable = this.selectEnable;
			this.view.load();
		}.bind(this));
	},
	recordStatus: function(){
		var currentObject = this.navi.currentObject;
		if( currentObject ){
			var categoryId = currentObject.getCategoryId();
			if (categoryId){
				return {
					"columnId" : this.columnData.id,
					"categoryId" :categoryId,
					"isCategory" : currentObject.isCategory,
					"viewId" : currentObject.data.id
				};
			}else{
				return { "columnId" : this.columnData.id , "categoryId" : "whole"}
			}
		}else{
			return { "columnId" : this.columnData.id , "categoryId" : "whole" }
		}
	},
	setNaviSize: function(){
		//var titlebarSize = this.titleBar ? this.titleBar.getSize() : {"x":0,"y":0};
		var nodeSize = this.node.getSize();
		//var pt = this.naviContainerNode.getStyle("padding-top").toFloat();
		//var pb = this.naviContainerNode.getStyle("padding-bottom").toFloat();

		//var height = nodeSize.y-pt-pb-titlebarSize.y;
		this.naviContainerNode.setStyle("height", ""+nodeSize.y+"px");
	}
});

MWF.xApplication.cms.Module.Navi = new Class({
	Implements: [Options, Events],
	options : {
		"categoryId" :"" ,
		"viewId" : "",
		"isCategory" : false,
		"navi" : -1
	},
	initialize: function(app, node, columnData, options){
		this.setOptions(options);
		this.app = app;
		this.node = $(node);
		this.columnData = columnData;

		this.categoryList = [];
		this.css = this.app.css;
		this.load();
	},
	load: function(){
		var self = this;
		var showAll = (typeOf(this.columnData.showAllDocuments) === "boolean" ? this.columnData.showAllDocuments : true).toString();
		if( showAll !== "false" ){
			this.allView = new MWF.xApplication.cms.Module.NaviAllView( this, this.node, {}  );
		}
		if( this.columnData.config.latest === false ){
			this.draftView = new MWF.xApplication.cms.Module.NaviDraftView( this, this.node, {}  );
		}


		new Element("div",{
			"styles" : this.css.viewNaviBottom
		}).inject(this.node);

		this.app.restActions.listCategory( this.columnData.id, function( json ) {
			json.data.each(function (d, idx) {
				var isCurrent = false;
				var category = new MWF.xApplication.cms.Module.NaviCategory(this, this.node,d, {} );
				this.categoryList.push( category );
				if( showAll == "false" && idx === 0 ){
					category.setCurrent();
				}
				this.fireEvent("postLoad");
			}.bind(this))
		}.bind(this))
	}
});

MWF.xApplication.cms.Module.NaviCategory = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function ( navi, container, data, options) {
		this.setOptions(options);
		this.navi = navi;
		this.app = navi.app;
		this.container = $(container);
		this.data = data;
		this.css = this.app.css;
		this.load();
	},
	load: function () {
		var _self = this;

		this.isCategory = true;
		this.isCurrent = false;
		this.isExpended = false;
		this.hasSub = false;
		this.naviViewList = [];

		if( this.navi.options.categoryId == this.data.id && this.navi.options.isCategory ){
			this.isCurrent = true;
		}

		this.reveal = this.getRevealData();

		this.node = new Element("div.categoryNaviNode", {
			"styles": this.css.categoryNaviNode
		}).inject(this.container);

		this.expendNode = new Element("div.expendNode").inject(this.node);
		this.setExpendNodeStyle();
		if( this.hasSub ){
			this.expendNode.addEvent( "click" , function(ev){
				this.triggerExpend();
				ev.stopPropagation();
			}.bind(this));
		}

		this.textNode = new Element("div.categoryNaviTextNode",{
			"styles": this.css.categoryNaviTextNode,
			"text": this.data.name //this.defaultRevealData.id == "defaultList" ? this.data.name : this.defaultRevealData.showName
		}).inject(this.node);

		this.node.addEvents({
			"mouseover": function(){ if ( !_self.isCurrent )this.setStyles(_self.app.css.categoryNaviNode_over) },
			"mouseout": function(){ if ( !_self.isCurrent )this.setStyles( _self.app.css.categoryNaviNode ) },
			click : function(){ _self.setCurrent(this);}
		});

		this.listNode = new Element("div.viewNaviListNode",{
			"styles" : this.css.viewNaviListNode
		}).inject(this.container);

		this.loadListContent();
		if( this.isCurrent ){
			this.setCurrent();
		}
	},
	getRevealData: function(){
		debugger;
		var j = this.data.extContent;
		if( j ){
			this.extContent = JSON.parse( j );
		}
		if( !this.extContent || !this.extContent.reveal || this.extContent.reveal.length == 0 ){ //兼容以前的设置
			this.extContent = { reveal : [] };
			this.app.restActions.listViewByCategory( this.data.id, function(json){
				( json.data || [] ).each( function(d){
					var itemData = {
						"type" : "list",
						"name" : d.name,
						"showName" : d.name,
						"id" : d.id,
						"alias" : d.alias,
						"appId" : d.appId,
						"formId" : d.formId,
						"formName" : d.formName
					};
					this.extContent.reveal.push( itemData );
				}.bind(this));
			}.bind(this), null, false );
		}

		this.extContent.reveal.each( function( r, i ){
			if(this.data.defaultViewName && r.id == this.data.defaultViewName ){
				this.defaultRevealData = r;
			}else if( i>0 ){
				this.isExpended = true;
				this.hasSub = true;
			}
		}.bind(this));

		if( !this.extContent || !this.extContent.reveal || this.extContent.reveal.length == 0 ){
			this.extContent = { reveal : [{
					id : "defaultList",
					showName : this.app.lp.systemList,
					name : this.app.lp.systemList
				}] };
		}
		this.revealData = this.extContent.reveal;

		if( !this.defaultRevealData ){
			this.defaultRevealData = {
				id : "defaultList",
				showName : this.app.lp.systemList,
				name : this.app.lp.systemList
			}
		}
	},
	setExpendNodeStyle : function(){
		var style;
		if( this.hasSub ){
			if( this.isExpended ){
				if( this.isCurrent ){
					style = this.css.categoryExpendNode_selected;
				}else{
					style = this.css.categoryExpendNode;
				}
			}else{
				if( this.isCurrent ){
					style = this.css.categoryCollapseNode_selected;
				}else{
					style = this.css.categoryCollapseNode;
				}
			}
		}else{
			style = this.css.emptyExpendNode;
		}
		this.expendNode.setStyles( style );
	},
	triggerExpend : function(){
		if( this.hasSub ){
			if( this.isExpended ){
				this.isExpended = false;
				this.listNode.setStyle("display","none")
			}else{
				this.isExpended = true;
				this.listNode.setStyle("display","")
			}
			this.setExpendNodeStyle();
		}
	},
	setCurrent : function(){
		if( this.navi.currentObject ){
			this.navi.currentObject.cancelCurrent();
		}

		this.node.setStyles( this.css.categoryNaviNode_selected );

		if( this.hasSub ){
			if( this.isExpended ){
				this.expendNode.setStyles( this.css.categoryExpendNode_selected );
			}else{
				this.expendNode.setStyles( this.css.categoryCollapseNode_selected );
			}
		}

		this.isCurrent = true;
		this.navi.currentObject = this;

		var action = this.app.importAction;
		if( action ){
			action.setStyle("display", (this.data.importViewId && this.app.isAdmin) ? "" : "none");
		}
		action = this.app.exportAction;
		if( action ){
			action.setStyle("display", (this.data.importViewId && this.app.isAdmin) ? "" : "none");
		}

		this.loadView();
	},
	cancelCurrent : function(){
		this.isCurrent = false;
		this.node.setStyles( this.css.categoryNaviNode );
		if( this.hasSub ){
			if( this.isExpended ){
				this.expendNode.setStyles( this.css.categoryExpendNode );
			}else{
				this.expendNode.setStyles( this.css.categoryCollapseNode );
			}
		}
	},
	loadView: function( searchkey ){
		this.app.openView( this, this.data, this.viewData || this.defaultRevealData, searchkey || "", this );
	},
	loadListContent : function(){
		this.revealData.each( function( d , i){
			if( d.id != this.defaultRevealData.id ){
				var naviView = new MWF.xApplication.cms.Module.NaviView(this.navi, this, this.listNode, d, {
					"style": this.options.style,
					"index" : i
				});
				this.naviViewList.push( naviView );
			}
		}.bind(this));
		new Element("div", {
			"styles": this.css.viewNaviSepartorNode
		}).inject( this.listNode );
	},
	getCategoryId : function(){
		return this.data.id;
	}
});

MWF.xApplication.cms.Module.NaviView = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default",
		"index" : 0
	},
	initialize: function ( navi, category, container, data, options) {
		this.setOptions(options);
		this.navi = navi;
		this.category = category;
		this.app = navi.app;
		this.data = data;
		this.container = $(container);
		this.css = this.app.css;
		this.load();
	},
	load: function(){
		this.isDefault = this.data.id == "defaultList";
		this.isCurrent = false;
		this.isCategory = false;

		if( this.navi.options.categoryId == this.category.data.id && !this.navi.options.isCategory ){
			if( this.navi.options.viewId == "defaultList" && this.isDefault ){
				this.isCurrent = true;
			}else if( this.navi.options.viewId == this.data.id ){
				this.isCurrent = true;
			}else if( this.navi.options.naviIndex == this.options.index ){
				this.isCurrent = true;
			}
		}

		var _self = this;
		this.node = new Element("div.viewNaviNode", {
			"styles": this.css.viewNaviNode,
			"text" : (this.isDefault && !this.data.showName) ? this.app.lp.defaultView : this.data.showName
		}).inject(this.container);

		this.node.addEvents({
			"mouseover": function(){ if (!_self.isCurrent)this.setStyles(_self.css.viewNaviNode_over) },
			"mouseout": function(){ if (!_self.isCurrent)this.setStyles( _self.css.viewNaviNode ) },
			"click": function (el) {
				_self.setCurrent();
			}
		});

		if( this.isCurrent ){
			this.setCurrent()
		}
	},
	setCurrent : function(){
		if( this.navi.currentObject ){
			this.navi.currentObject.cancelCurrent();
		}

		this.node.setStyles( this.css.viewNaviNode_selected );

		this.isCurrent = true;
		this.navi.currentObject = this;

		var action = this.app.importAction;
		if( action ){
			action.setStyle("display", (this.category.data.importViewId && this.app.isAdmin) ? "" : "none");
		}
		action = this.app.exportAction;
		if( action ){
			action.setStyle("display", (this.category.data.importViewId && this.app.isAdmin) ? "" : "none");
		}

		this.loadView();
	},
	cancelCurrent : function(){
		this.isCurrent = false;
		this.node.setStyles( this.css.viewNaviNode );
	},
	getCategoryId : function(){
		return this.category.data.id;
	},
	loadView : function( searchKey ){
		this.app.openView( this, this.category.data, this.data, searchKey || "", this );
	}
});

MWF.xApplication.cms.Module.NaviAllView = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function ( navi, container, options) {
		this.setOptions(options);
		this.navi = navi;
		this.app = navi.app;
		this.container = $(container);
		this.css = this.app.css;
		this.data = {
			"isAll" : true,
			"id" : "defaultList"
		};
		this.load();
	},
	load: function(){
		var _self = this;
		this.isDefault = true;
		this.isAll = true;
		this.isCurrent = false;
		this.isCategory = false;

		if( this.navi.options.categoryId == "whole" ){
			this.isCurrent = true;
		}

		this.listNode  = new Element("div.viewNaviListNode_all",{
			"styles" : this.css.viewNaviListNode_all
		}).inject(this.container);

		this.node = new Element("div.viewNaviNode_all", {
			"styles": this.css.viewNaviNode_all,
			"text" : this.app.lp.allDocument
		}).inject(this.listNode);

		this.node.addEvents({
			"mouseover": function(){ if ( !_self.isCurrent )this.setStyles(_self.css.viewNaviNode_all_over) },
			"mouseout": function(){ if ( !_self.isCurrent )this.setStyles( _self.css.viewNaviNode_all ) },
			"click": function (el) {
				_self.setCurrent();
			}
		});

		new Element("div", {
			"styles": this.css.viewNaviSepartorNode
		}).inject(this.listNode);

		if( this.isCurrent ){
			this.setCurrent()
		}
	},
	setCurrent : function(){

		if( this.navi.currentObject ){
			this.navi.currentObject.cancelCurrent();
		}

		this.node.setStyles( this.css.viewNaviNode_all_selected );

		this.isCurrent = true;
		this.navi.currentObject = this;

		var action = this.app.importAction;
		if( action ){
			action.setStyle("display","none");
		}
		var action = this.app.exportAction;
		if( action ){
			action.setStyle("display","none");
		}

		this.loadView();
	},
	cancelCurrent : function(){
		this.isCurrent = false;
		this.node.setStyles( this.css.viewNaviNode_all );
	},
	getCategoryId : function(){
		return null;
	},
	loadView : function( searchKey ){
		this.app.openView( this, null, this.data, searchKey || "", this );
	}
});

MWF.xApplication.cms.Module.NaviDraftView = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function ( navi, container, options) {
		this.setOptions(options);
		this.navi = navi;
		this.app = navi.app;
		this.container = $(container);
		this.css = this.app.css;
		this.data = {
			"isDraft" : true,
			"id" : "defaultList"
		};
		this.load();
	},
	load: function(){
		var _self = this;
		this.isDefault = true;
		this.isAll = true;
		this.isCurrent = false;
		this.isCategory = false;

		this.listNode  = new Element("div.viewNaviListNode_all",{
			"styles" : this.css.viewNaviListNode_all
		}).inject(this.container);

		this.node = new Element("div.viewNaviNode_all", {
			"styles": this.css.viewNaviNode_all,
			"text" : this.app.lp.draftStatus
		}).inject(this.listNode);

		this.node.addEvents({
			"mouseover": function(){ if ( !_self.isCurrent )this.setStyles(_self.css.viewNaviNode_all_over) },
			"mouseout": function(){ if ( !_self.isCurrent )this.setStyles( _self.css.viewNaviNode_all ) },
			"click": function (el) {
				_self.setCurrent();
			}
		});

		new Element("div", {
			"styles": this.css.viewNaviSepartorNode
		}).inject(this.listNode);

		if( this.isCurrent ){
			this.setCurrent()
		}
	},
	setCurrent : function(){

		if( this.navi.currentObject ){
			this.navi.currentObject.cancelCurrent();
		}

		this.node.setStyles( this.css.viewNaviNode_all_selected );

		this.isCurrent = true;
		this.navi.currentObject = this;

		var action = this.app.importAction;
		if( action ){
			action.setStyle("display","none");
		}
		var action = this.app.exportAction;
		if( action ){
			action.setStyle("display","none");
		}

		this.loadView();
	},
	cancelCurrent : function(){
		this.isCurrent = false;
		this.node.setStyles( this.css.viewNaviNode_all );
	},
	getCategoryId : function(){
		return null;
	},
	loadView : function( searchKey ){
		this.app.openView( this, null, this.data, searchKey || "", this );
	}
});
