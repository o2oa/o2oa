MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.ScriptArea", null, false);
MWF.xApplication.process.FormDesigner.widget.SectionDisplayer = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
		"maxObj": document.body
	},
	initialize: function(node, property, options){

		this.setOptions(options);
		this.node = $(node);
		this.property = property;
        this.app = this.property.designer;
		this.designer = this.property.designer;
		this.form = this.property.form;
		this.module = this.property.module;
		
		// this.path = "../x_component_process_FormDesigner/widget/$SectionDisplayer/";
		// this.cssPath = "../x_component_process_FormDesigner/widget/$SectionDisplayer/"+this.options.style+"/css.wcss";
		this.lp = this.app.lp.propertyTemplate;
		if( !this.lp ){
			o2.xDesktop.requireApp("process.FormDesigner", "lp."+o2.language, null, false);
			this.lp = MWF.xApplication.process.FormDesigner.LP.propertyTemplate;
		}
		// this._loadCss();
	},
	
	load: function(data){
		var _self = this;

		this.data = data;

        this.node.set("html", this.getHtml());

        this.sectionDisplayArea = this.node.getElement(".sectionDisplayArea");
		this.displayWithSectionKeyArea = this.node.getElement(".displayWithSectionKeyArea");
		this.sortScriptArea = this.node.getElement(".sectionDisplaySortScriptArea");

		var lp = this.lp;
		var moduleName = this.module.moduleName;

		if( o2.typeOf( this.data.showAllSection ) === "null" )this.data.showAllSection = false;
		if( o2.typeOf( this.data.showSectionBy ) === "null" )this.data.showSectionBy = true;
		if( o2.typeOf( this.data.sequenceBy ) === "null" )this.data.sequenceBy = "section";
		if( o2.typeOf( this.data.keyContentSeparatorSectionBy ) === "null" )this.data.keyContentSeparatorSectionBy = "：";

		MWF.xDesktop.requireApp("Template", "MForm", function () {
			this.form = new MForm(this.node, this.data, {
				isEdited: true,
				style : "",
				hasColon : true,
				itemTemplate: {
					showAllSection: { name: this.data.pid + "showAllSection",
						type : "radio", className: "editTableRadio", selectValue: ["true", "false"], selectText: [lp.yes, lp.no], event: {
							change: function (it) {
								_self.property.setRadioValue("showAllSection", this);
								_self.checkShow()
							}
						}},
					showSectionBy: { name: this.data.pid + "showSectionBy",
						type : "radio", className: "editTableRadio", selectValue: ["true", "false"], selectText: [lp.yes, lp.no], event: {
							change: function (it) {
								_self.property.setRadioValue("showSectionBy", this);
								_self.checkShow()
							}
						}},
					sequenceBy: { name: this.data.pid + "sequenceBy",
						type : "radio", className: "editTableRadio", selectValue: ["section","module"], selectText: [lp.bySection, lp.byModule], event: {
							change: function (it) {
								_self.property.setRadioValue("sequenceBy", this);
								_self.checkShow()
							}
						}},
					totalRowBy: { name: this.data.pid + "totalRowBy",
						type : "checkbox", className: "editTableRadio", selectValue: ["section","module"], selectText: [lp.bySection, lp.byModule], event: {
							change: function (it) {
								_self.property.setCheckboxValue("totalRowBy", this);
								_self.checkShow()
							}
						}},
					keyContentSeparatorSectionBy: {  name: this.data.pid + "keyContentSeparatorSectionBy",
						tType : "text" , className: "editTableInput", event: {
							change: function (it) {
								_self.property.setValue("keyContentSeparatorSectionBy", it.getValue(), this);
							}
						}}
				}
			}, this.app, this.form.css);
			this.form.load();
			// this.setEditNodeStyles( this.node );
			this.loadMaplist();
			this.loadScriptArea();
			this.checkShow( this.data );
			this.fireEvent("postLoad");
		}.bind(this), true);
	},
	checkShow: function(d){
		if( !d )d = this.data;
		var _self = this;
		var showCondition = {
			"sectionDisplayArea": function () {
				return !!d.showAllSection;
			},
			"displayWithSectionKeyArea": function () {
				return !!d.showSectionBy
			},
			"sortScriptArea": function () {
				return !!d.showAllSection
			}
		};
		for( var key in showCondition ){
			if( showCondition[key]() ){
				this[key].setStyle("display", "");
			}else{
				this[key].hide()
			}
		}
	},
	getHtml: function(){
		return '<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'    <tr>' +
			'        <td class="editTableTitle">'+this.lp.showAllSection+':</td>' +
			'        <td class="editTableValue" item="showAllSection"></td>' +
			'    </tr>' +
			'</table>' +
			'<div class="sectionDisplayArea">' +
			'        <table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'            <tr>' +
			'                <td class="editTableTitle">'+this.lp.showSectionKey+':</td>' +
			'                <td class="editTableValue" item="showSectionBy"></td>' +
			'            </tr>' +
			'        </table>' +
			'        <div class="displayWithSectionKeyArea">' +
			'            	<div class="MWFMaplist" name="sectionByStyles" title="'+this.lp.sectionKeystyles+'"></div>' +
			'            	<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'                	<tr>' +
			'                    	<td class="editTableTitle">'+this.lp.serialNumber+':</td>' +
			'                    	<td class="editTableValue" item="sequenceBy"></td>' +
			'                	</tr>' +
			'            	</table>' +
			'            	<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'                	<tr>' +
			'                    	<td class="editTableTitle">'+this.lp.totalRow+':</td>' +
			'                    	<td class="editTableValue" item="totalRowBy"></td>' +
			'                	</tr>' +
			'            	</table>' +
			'            	<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'                <tr>' +
			'                    <td class="editTableTitle">'+this.lp.separator+':</td>' +
			'                    <td class="editTableValue" item="keyContentSeparatorSectionBy"></td>' +
			'                </tr>' +
			'            	</table>' +
			'        </div>' +
			'</div>' +
			'<div class="sectionDisplaySortScriptArea">' +
			'    <div class="MWFScriptArea" name="sectionDisplaySortScript" title="'+this.lp.sectionMergeSortScript+' (S)"></div>' +
			'    <div style="padding: 10px;color:#999">排序脚本通过this.event.a和this.event.b获取数据，处理后返回正数表示升序，负数表示降序。this.event.a和this.event.b值如: <br/>' +
			'        { <br/>' +
			'        key: "张三@zhangsan@P", //区段值 <br/>' +
			'        data: "内容" //字段内容 <br/>' +
			'        }' +
			'    </div>' +
			'</div>'
	},
	setEditNodeStyles: function(node){
		var nodes = node.getChildren();
		if (nodes.length){
			nodes.each(function(el){
				var cName = el.get("class");
				if (cName){
					if (this.form.css[cName]) el.setStyles(this.form.css[cName]);
				}
				this.setEditNodeStyles(el);
			}.bind(this));
		}
	},
	loadMaplist: function(){
		var maplists = this.node.getElements(".MWFMaplist");
		maplists.each(function(node){
			var title = node.get("title");
			var name = node.get("name");
			var lName = name.toLowerCase();
			var collapse = node.get("collapse");
			var mapObj = this.data[name];
			if (!mapObj) mapObj = {};
			MWF.require("MWF.widget.Maplist", function(){
				node.empty();
				var maplist = new MWF.widget.Maplist(node, {
					"title": title,
					"collapse": (collapse) ? true : false,
					"onChange": function(){
						//this.data[name] = maplist.toJson();
						//
						var oldData = this.data[name];
						this.property.changeJsonDate(name, maplist.toJson());
						this.property.changeStyle(name, oldData);
						this.property.changeData(name, null, oldData);
					}.bind(this),
					"onDelete": function(key){
						this.module.deletePropertiesOrStyles(name, key);
					}.bind(this),
					"isProperty": (lName.contains("properties") || lName.contains("property") || lName.contains("attribute"))
				});
				maplist.load(mapObj);
				this.property.maplists[name] = maplist;
			}.bind(this));
		}.bind(this));
	},
	loadScriptArea: function(){
		var scriptAreas = this.node.getElements(".MWFScriptArea");
		var formulaAreas = this.node.getElements(".MWFFormulaArea");
		this.loadScriptEditor(scriptAreas);
		this.loadScriptEditor(formulaAreas, "formula");
	},
	loadScriptEditor: function(scriptAreas, style){
		scriptAreas.each(function(node){
			var title = node.get("title");
			var name = node.get("name");
			if (!this.data[name]) this.data[name] = {"code": "", "html": ""};
			var scriptContent = this.data[name];

			var mode = node.dataset["mode"];
			MWF.require("MWF.widget.ScriptArea", function(){
				var scriptArea = new MWF.widget.ScriptArea(node, {
					"title": title,
					"isbind": false,
					"mode": mode || "javascript",
					//"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
					"maxObj": this.designer.formContentNode || this.designer.pageContentNode,
					"onChange": function(){
						//this.data[name] = scriptArea.toJson();
						if (!this.data[name]){
							this.data[name] = {"code": "", "html": ""};
							if (this.module.form.scriptDesigner) this.module.form.scriptDesigner.addScriptItem(this.data[name], "code", this.data, name);
						}
						var oldValue = this.data[name].code;
						var json = scriptArea.toJson();
						this.data[name].code = json.code;
						this.property.checkHistory(name+".code", oldValue, this.data[name].code);
						//this.data[name].html = json.html;
					}.bind(this),
					"onSave": function(){
						this.designer.saveForm();
					}.bind(this),
					"style": style || "default",
					"runtime": "web"
				});
				scriptArea.load(scriptContent);
			}.bind(this));

		}.bind(this));
	}
	
});

