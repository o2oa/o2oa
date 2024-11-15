MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.ScriptArea", null, false);
MWF.xApplication.process.FormDesigner.widget.SectionMerger = new Class({
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
		
		// this.path = "../x_component_process_FormDesigner/widget/$SectionMerger/";
		// this.cssPath = "../x_component_process_FormDesigner/widget/$SectionMerger/"+this.options.style+"/css.wcss";
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

        this.readArea = this.node.getElement(".sectionMergeReadArea");
		this.readTypeArea = this.node.getElement(".sectionMergeReadTypeArea");
        this.readDefaultArea = this.node.getElement(".sectionMergeReadDefaultArea");
		this.readHtmlScriptArea = this.node.getElement(".sectionMergeReadHtmlScriptArea");
		this.readDataScriptArea = this.node.getElement(".sectionMergeReadDataScriptArea");
		this.keyContentSeparatorArea = this.node.getElement(".keyContentSeparatorArea");
		this.sectionKeyStylesArea = this.node.getElement(".sectionKeyStylesArea");

		this.readStyleArea = this.node.getElement(".sectionMergeReadStyleArea");

		this.readWithSectionKeyArea = this.node.getElement(".readWithSectionKeyArea");
		this.sectionKeyScriptArea = this.node.getElement(".sectionKeyScriptArea");
		this.sectionKeySequenceArea = this.node.getElement(".sectionKeySequenceArea");
		this.sectionKeyTotalArea = this.node.getElement(".sectionKeyTotalArea");

		this.editArea = this.node.getElement(".sectionMergeEditArea");
		this.editScriptArea = this.node.getElement(".sectionMergeEditScriptArea");
		this.mergeTypeEditTable = this.node.getElement("[item='mergeTypeEditTable']");


		this.sortScriptArea = this.node.getElement(".sectionMergeSortScriptArea");

		var lp = this.lp;
		var moduleName = this.module.moduleName;

		this.hasEditDefaultModuleList = ["textfield", "checkbox", "datatable", "datatemplate", "org", "textarea", "elautocomplete", "elcheckbox", "elinput"];

		if( o2.typeOf( this.data.sectionMerge ) === "null" )this.data.sectionMerge = "none";
		if( o2.typeOf( this.data.mergeTypeRead ) === "null" )this.data.mergeTypeRead = "default";
		if( o2.typeOf( this.data.showSectionKey ) === "null" )this.data.showSectionKey = true;
		if( o2.typeOf( this.data.sectionKey ) === "null" )this.data.sectionKey = "person";
		if( o2.typeOf( this.data.mergeTypeEdit ) === "null" ){
			if( !this.hasEditDefaultModuleList.contains( moduleName ) ){
				this.data.mergeTypeEdit = "script"
			}else if(["number", "elnumber"].contains(moduleName)){
				this.data.mergeTypeEdit = "amount"
			}else{
				this.data.mergeTypeEdit = "default"
			}
		}

		MWF.xDesktop.requireApp("Template", "MForm", function () {
			this.form = new MForm(this.node, this.data, {
				isEdited: true,
				style : "",
				hasColon : true,
				itemTemplate: {
					sectionMerge: { name: this.data.pid + "sectionMerge", type : "radio",
						selectValue: function(){
							return ["none", "read", "edit"];
							// switch (moduleName) {
							// 	case "datatable":
							// 	case "datatemplate":
							// 		return ["none", "read", "editSection", "edit"];
							// 	default:
							// 		return ["none", "read", "edit"]
							// }
						},
						selectText: function () {
							// switch (moduleName) {
							// 	case "datatable":
							// 	case "datatemplate":
							// 		return [lp.none, lp.mergeDisplay, lp.editCurrentSection, lp.mergeEdit];
							// 	default:
							// 		return [lp.none, lp.mergeDisplay, lp.mergeEdit]
							// }
							return [lp.none, lp.mergeDisplay, lp.mergeEdit];
						},
						event: {
							change: function (it, ev) {
								_self.property.setRadioValue("sectionMerge", this);
								_self.checkShow()
							}
						}},
					mergeTypeRead: { name: this.data.pid + "mergeTypeRead",
						type : "radio", className: "editTableRadio",
						selectValue: function(){
							switch (moduleName) {
								case "datatable":
								case "datatemplate":
									return ["default", "dataScript"];
								case "number":
								case "elnumber":
									return ["default", "amount", "average", "htmlScript"];
								default:
									return ["default", "htmlScript"]
							}
						},
						selectText: function () {
							switch (moduleName) {
								case "datatable":
								case "datatemplate":
									return [lp.default, lp.dataScript];
								case "number":
								case "elnumber":
									return [lp.default, lp.amountValue, lp.averageValue, lp.htmlScript];
								default:
									return [lp.default, lp.htmlScript];
							}
						},
						event: {
							change: function (it) {
								_self.property.setRadioValue("mergeTypeRead", this);
								_self.checkShow()
							}
						}},
					showSectionKey: { name: this.data.pid + "showSectionKey",
						type : "radio", className: "editTableRadio", selectValue: ["true", "false"], selectText: [lp.yes, lp.no], event: {
							change: function (it) {
								_self.property.setRadioValue("showSectionKey", this);
								_self.checkShow()
							}
						}},
					sequenceBySection: { name: this.data.pid + "sequenceBySection",
						type : "radio", className: "editTableRadio", selectValue: ["section","module"], selectText: [lp.bySection, lp.byModule], event: {
							change: function (it) {
								_self.property.setRadioValue("sequenceBySection", this);
								_self.checkShow()
							}
						}},
					totalRowBySection: { name: this.data.pid + "totalRowBySection",
						type : "checkbox", className: "editTableRadio", selectValue: ["section","module"], selectText: [lp.bySection, lp.byModule], event: {
							change: function (it) {
								_self.property.setCheckboxValue("totalRowBySection", this);
								_self.checkShow()
							}
						}},
					keyContentSeparator: {  name: this.data.pid + "keyContentSeparator",
						tType : "text" , className: "editTableInput", event: {
							change: function (it) {
								_self.property.setValue("keyContentSeparator", it.getValue(), this);
							}
						}},
					sectionKey: { name: this.data.pid + "sectionKey",
						type : "radio", selectValue: ["person", "unit", "activity", "splitValue", "script"], selectText: [lp.handler, lp.handlerUnit, lp.activityId, lp.splitValue, lp.script], event: {
							change: function (it) {
								_self.property.setRadioValue("sectionKey", this);
								_self.checkShow()
							}
						}},
					mergeTypeEdit: { name: this.data.pid + "mergeTypeEdit",
						type : "radio", className: "editTableRadio",
						selectValue: function(){
							return ["number", "elnumber"].contains(moduleName) ? ["amount", "average", "script"] : ["default", "script"]
						},
						selectText: function () {
							return ["number", "elnumber"].contains(moduleName) ? [lp.amountValue, lp.averageValue, lp.script] : [lp.default, lp.script]
						},
						event: {
							change: function (it) {
								_self.property.setRadioValue("mergeTypeEdit", this);
								_self.checkShow()
							}
						}},
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
			"readArea": function() {
				return d.sectionMerge==='read' || d.sectionMerge==='editSection';
			},
			"readTypeArea": function(){
				return d.sectionMerge==='read';
			},
			"readDefaultArea": function () {
				return d.mergeTypeRead==='default' || !d.mergeTypeRead || d.sectionMerge==='editSection';
			},
			"readWithSectionKeyArea": function () {
				return !!d.showSectionKey || d.sectionMerge==='editSection';
			},
			"keyContentSeparatorArea": function () {
				return !!d.showSectionKey;
			},
			"sectionKeyStylesArea": function () {
				return !!d.showSectionKey;
			},
			"sectionKeyScriptArea": function () {
				return d.sectionKey === "script";
			},
			"sectionKeyTotalArea": function () {
				return d.showSectionKey && ["datatable"].contains( _self.module.moduleName );
			},
			"sectionKeySequenceArea": function () {
				return d.showSectionKey && ["datatable", "datatemplate"].contains( _self.module.moduleName );
			},
			"readStyleArea": function () {
				return !["datatable", "datatemplate"].contains( _self.module.moduleName );
			},
			"readHtmlScriptArea": function () {
				return d.sectionMerge==='read' && d.mergeTypeRead==='htmlScript';
			},
			"readDataScriptArea": function () {
				return d.sectionMerge==='read' && d.mergeTypeRead==='dataScript';
			},
			"editArea": function() {
				return d.sectionMerge==='edit';
			},
			"editScriptArea": function () {
				return d.sectionMerge==='edit' && d.mergeTypeEdit === 'script';
			},
			"sortScriptArea": function () {
				return ( d.sectionMerge==='read' && d.mergeTypeRead === "default" ) ||
					(d.sectionMerge==='edit' && d.mergeTypeEdit === "default") ||
					d.sectionMerge==='editSection'
			},
			"mergeTypeEditTable" : function () {
				return _self.hasEditDefaultModuleList.contains( _self.module.moduleName ) ||
					["number", "elnumber"].contains( _self.module.moduleName );
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
			'        <td class="editTableTitle">'+this.lp.enableSectionMerge+':</td>' +
			'        <td class="editTableValue" item="sectionMerge"></td>' +
			'    </tr>' +
			'</table>' +
			'<div class="sectionMergeReadArea">' +
			'    <div class="sectionMergeReadTypeArea">' +
			'    	<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'        	<tr>' +
			'            	<td class="editTableTitle">'+this.lp.mergeType+':</td>' +
			'            	<td class="editTableValue" item="mergeTypeRead"></td>' +
			'        	</tr>' +
			'    	</table>' +
			'    </div>' +
			'    <div class="sectionMergeReadDefaultArea">' +
			'        <table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'            <tr>' +
			'                <td class="editTableTitle">'+this.lp.showSectionKey+':</td>' +
			'                <td class="editTableValue" item="showSectionKey"></td>' +
			'            </tr>' +
			'        </table>' +
			'        <div class="sectionMergeReadStyleArea">' +
			'        	<div class="MWFMaplist" name="sectionNodeStyles" title="'+this.lp.sectionNodeStyles+'"></div>' +
			'        	<div class="MWFMaplist" name="sectionContentStyles" title="'+this.lp.sectionContentStyles+'"></div>' +
			'        </div>' +
			'        <div class="readWithSectionKeyArea">' +
			'			 <div class="sectionKeyStylesArea">' +
			'            	<div class="MWFMaplist" name="sectionKeyStyles" title="'+this.lp.sectionKeystyles+'"></div>' +
			'			 </div>'+
			'            <div class="sectionKeySequenceArea">' +
			'            	<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'                	<tr>' +
			'                    	<td class="editTableTitle">'+this.lp.serialNumber+':</td>' +
			'                    	<td class="editTableValue" item="sequenceBySection"></td>' +
			'                	</tr>' +
			'            	</table>' +
			'            </div>' +
			'            <div class="sectionKeyTotalArea">' +
			'            	<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'                	<tr>' +
			'                    	<td class="editTableTitle">'+this.lp.totalRow+':</td>' +
			'                    	<td class="editTableValue" item="totalRowBySection"></td>' +
			'                	</tr>' +
			'            	</table>' +
			'            </div>' +
			'            <div class="keyContentSeparatorArea">' +
			'            	<table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'                <tr>' +
			'                    <td class="editTableTitle">'+this.lp.separator+':</td>' +
			'                    <td class="editTableValue" item="keyContentSeparator"></td>' +
			'                </tr>' +
			'            	</table>' +
			'            </div>' +
			'            <table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable">' +
			'                <tr>' +
			'                    <td class="editTableTitle">'+this.lp.sectionKey+':</td>' +
			'                    <td class="editTableValue" item="sectionKey"></td>' +
			'                </tr>' +
			'            </table>' +
			'            <div class="sectionKeyScriptArea">' +
			'                <div class="MWFScriptArea" name="sectionKeyScript" title="'+this.lp.sectionKeyScript+' (S)"></div>' +
			'            </div>' +
			'        </div>' +
			'    </div>' +
			'    <div class="sectionMergeReadHtmlScriptArea">' +
			'        <div class="MWFScriptArea" name="sectionMergeReadHtmlScript" title="'+this.lp.sectionMergeReadHtmlScript+' (S)"></div>' +
			'        <div style="padding: 10px;color:#999">返回需展现的html</div>' +
			'    </div>    ' +
			'    <div class="sectionMergeReadDataScriptArea">' +
			'        <div class="MWFScriptArea" name="sectionMergeReadDataScript" title="'+this.lp.sectionMergeReadDataScript+' (S)"></div>' +
			'        <div style="padding: 10px;color:#999">返回删除区段后合并的数据,不保存到后台</div>' +
			'    </div>    ' +
			'</div>' +
			'<div class="sectionMergeEditArea">' +
			'    <table width="100%" border="0" cellpadding="5" cellspacing="0" class="editTable" item="mergeTypeEditTable">' +
			'        <tr>' +
			'            <td class="editTableTitle">'+this.lp.mergeType+':</td>' +
			'            <td class="editTableValue" item="mergeTypeEdit"></td>' +
			'        </tr>' +
			'    </table>' +
			'    <div class="sectionMergeEditScriptArea">' +
			'        <div class="MWFScriptArea" name="sectionMergeEditScript" title="'+this.lp.sectionMergeEditScript+' (S)"></div>' +
			'        <div style="padding: 10px;color:#999">通过this.data[fieldId]获取字段数据，返回删除区段后合并的数据。</div>' +
			'    </div>' +
			'</div>' +
			'<div class="sectionMergeSortScriptArea">' +
			'    <div class="MWFScriptArea" name="sectionMergeSortScript" title="'+this.lp.sectionMergeSortScript+' (S)"></div>' +
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

