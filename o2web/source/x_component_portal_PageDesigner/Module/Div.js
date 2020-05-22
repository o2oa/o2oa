MWF.xApplication.portal.PageDesigner.Module.Div = MWF.PCDiv = new Class({
	Extends: MWF.FCDiv,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Div/div.html",
		"actions": [
			{
				"name": "move",
				"icon": "move1.png",
				"event": "mousedown",
				"action": "move",
				"title": MWF.APPPD.LP.formAction.move
			},
			{
				"name": "copy",
				"icon": "copy1.png",
				"event": "mousedown",
				"action": "copy",
				"title": MWF.APPPD.LP.formAction.copy
			},
			{
				"name": "delete",
				"icon": "delete1.png",
				"event": "click",
				"action": "delete",
				"title": MWF.APPPD.LP.formAction["delete"]
			},
			{
				"name" : "makeWidget",
				"icon": "makeWidget1.png",
				"event": "click",
				"action": "makeWidget",
				"title": MWF.APPPD.LP.formAction["makeWidget"]
			}
		]
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Div/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Div/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "div";
		
		this.Node = null;
		this.form = form;
		this.page = form;
	},


	loadNewWidgetData: function(name, callback){
		var url = "../x_component_portal_PageDesigner/Module/Page/template/page.json";
		MWF.getJSON(url, {
			"onSuccess": function(obj){
				obj.pcData.id="";
				obj.pcData.isNewPage = true;
				obj.pcData.json = obj.pcData.json || {};
				obj.pcData.json.name = name; //MWF.APPPD.LP.formAction.defaultWidgetName;
				obj.pcData.json.application = this.page.designer.application.id;
				obj.pcData.json.applicationName = this.page.designer.application.name;

				obj.mobileData.id="";
				obj.mobileData.isNewPage = true;
				obj.mobileData.json = obj.mobileData.json || {};
				obj.mobileData.json.application = this.page.designer.application.id;
				obj.mobileData.json.applicationName = this.page.designer.application.name;
				if (callback) callback( obj );
			}.bind(this),
			"onerror": function(text){
				this.notice(text, "error");
			}.bind(this),
			"onRequestFailure": function(xhr){
				this.notice(xhr.responseText, "error");
			}.bind(this)
		});
	},
	_getWidgetData: function( data ){
		//var data = {
		//	json : {
		//		name : MWF.APPPD.LP.formAction.defaultWidgetName,
		//		application : this.page.designer.application.id
		//	},
		//	isNewPage : true
		//};

		this.page.fireEvent("queryGetPageData");
		var copy = this.node.clone(true, true);
		copy.clearStyles(true);
		this.page.fireEvent("postGetPageData");

		this.page._clearNoId(copy);
		var html = copy.outerHTML;

		if( this.page.options.mode === "Mobile" ){
			data.mobileData.html = "<div MWFType=\"form\" id=\"\">"+html+"</div>";
			data.mobileData.json.moduleList = this._getWidgetModules( copy );
		}else{
			data.pcData.html = "<div MWFType=\"form\" id=\"\">"+html+"</div>";
			data.pcData.json.moduleList = this._getWidgetModules( copy );
			//data.pcData.json.mode = "PC"; //this.page.options.mode;
		}

		copy.destroy();
		return data;
	},
	_getWidgetModules: function( dom ){
		var modules = {};

		var json = this.page.getDomjson(dom);
		modules[json.id] = json;

		var elements = dom.getElements("[MWFtype]");
		elements.each( function( el ){
			var json = this.page.getDomjson(el);
			modules[json.id] = json;
		}.bind(this));
		return modules;
	},
	_getWidgetFieldList: function( moduleList ){
		var dataTypes = {
			"string": ["htmledit", "radio", "select", "textarea", "textfield"],
			"person": ["personfield","org"],
			"date": ["calender"],
			"number": ["number"],
			"array": ["checkbox"]
		};
		fieldList = [];
		for( var id in moduleList ){
			var module = moduleList[id];
			var key = "";
			for (k in dataTypes){
				if (dataTypes[k].indexOf( ( module.moduleName || module.type || "" ).toLowerCase())!=-1){
					key = k;
					break;
				}
			}
			if (key){
				fieldList.push({
					"name": module.id,
					"dataType": key
				});
			}
		}
		return fieldList;
	},
	makeWidget: function(){
		var module = this;
		var url = this.path+"newWidget.html";
		MWF.require("MWF.widget.Dialog", function(){
			var size = $(document.body).getSize();
			var x = size.x/2-180;
			var y = size.y/2-100;

			var dlg = new MWF.DL({
				"title": "create widget",
				"style": "property",
				"top": y,
				"left": x-40,
				"fromTop":size.y/2-65,
				"fromLeft": size.x/2,
				"width": 360,
				"height": 200,
				"url": url,
				"buttonList": [
					{
						"text": MWF.APPFD.LP.button.ok,
						"action": function(){
							var widgetName = module.widgetNameInput.get("value");
							if( !widgetName ){
								module.page.designer.notice(module.page.designer.lp.notice.widgetNameEmpty, "error");
								return;
							}

							var flag = true;
							o2.Actions.get("x_portal_assemble_designer").listWidget( module.page.designer.application.id, function( json ){
								for( var i=0; i<json.data.length; i++ ){
									if( json.data[i].name === widgetName ){
										module.page.designer.notice(module.page.designer.lp.notice.widgetNameConflict, "error");
										flag = false;
										break;
									}
								}
							}.bind(this), null, false);
							if( flag ){
								module._makeWidget( widgetName );
								this.close();
							}
						}
					},
					{
						"text": MWF.APPFD.LP.button.cancel,
						"action": function(){
							this.close();
						}
					}
				],
				"onPostShow": function(){

					this.widgetNameInput = dlg.node.getElementById("MWFNewWidgetName");

				}.bind(this)
			});

			dlg.show();
		}.bind(this));
	},
	_makeWidget : function( name ){
		//var pcData, mobileData;
		//if (this.pcPage){
		//	this.pcPage._getPageData();
		//	pcData = this.pcPage.data;
		//}
		//if (this.mobilePage){
		//	this.mobilePage._getPageData();
		//	mobileData = this.mobilePage.data;
		//}else{
		//	if (this.pageMobileData) mobileData = this.pageMobileData;
		//}

		this.loadNewWidgetData( name, function( obj ){

			var data = this._getWidgetData( obj );

			//var pcData = {};
			//var mobileData = null;

			//if( this.page.options.mode === "Mobile" ){
			//	mobileData = data;
			//}else{
			//pcData = data;
			//}

			var pcData, mobileData, fieldList;
			if( this.page.options.mode === "Mobile" ){
				pcData = obj.pcData;
				mobileData = data.mobileData;
				fieldList = this._getWidgetFieldList( mobileData.json.moduleList );
			}else{
				pcData = data.pcData;
				mobileData = obj.mobileData;
				fieldList = this._getWidgetFieldList( pcData.json.moduleList );
			}


			debugger;

			this.page.designer.actions.saveWidget(pcData, mobileData, fieldList, function(responseJSON){
				this.page.designer.notice(MWF.APPPD.LP.notice["widget_save_success"], "ok", null, {x: "left", y:"bottom"});

				//if (!this.pcPage.json.name) this.pcPage.treeNode.setText("<"+this.json.type+"> "+this.json.id);
				//this.pcPage.treeNode.setTitle(this.pcPage.json.id);
				//this.pcPage.node.set("id", this.pcPage.json.id);
				//
				//if (this.mobilePage){
				//	if (!this.mobilePage.json.name) this.mobilePage.treeNode.setText("<"+this.mobilePage.json.type+"> "+this.mobilePage.json.id);
				//	this.mobilePage.treeNode.setTitle(this.mobilePage.json.id);
				//	this.mobilePage.node.set("id", this.mobilePage.json.id+"_"+this.options.mode);
				//}
				//
				//var name = this.pcPage.json.name;
				//if (this.pcPage.data.isNewPage) this.setTitle(this.options.appTitle + "-"+name);
				//this.pcPage.data.isNewPage = false;
				//if (this.mobilePage) this.mobilePage.data.isNewPage = false;
				//
				//this.options.desktopReload = true;
				//this.options.id = this.pcPage.json.id;
				//
				//if (pcData) pcData.isNewPage = false;
				//if (mobileData) mobileData.isNewPage = false;
				//this.isSave = false;

			}.bind(this), function(xhr, text, error){
				this.isSave = false;

				var errorText = error+":"+text;
				if (xhr) errorText = xhr.responseText;
				MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
			}.bind(this));
		}.bind(this))



	}
});
