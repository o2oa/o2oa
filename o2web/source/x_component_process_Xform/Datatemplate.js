/**
 * 数据模板数据结构.
 * @typedef {Array} DatatemplateData
 * @example
 [ //数据模板数据条目
 {
           "org": [{
                "distinguishedName": "张三@bf007525-99a3-4178-a474-32865bdddec8@I",
                "id": "bf007525-99a3-4178-a474-32865bdddec8",
                "name": "张三",
                "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
                "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
                "unitLevelName": "兰德纵横/市场营销部",
                "unitName": "市场营销部"
            }],
            "org_1": [{
                "distinguishedName": "张三@bf007525-99a3-4178-a474-32865bdddec8@I",
                "id": "bf007525-99a3-4178-a474-32865bdddec8",
                "name": "张三",
                "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
                "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
                "unitLevelName": "兰德纵横/市场营销部",
                "unitName": "市场营销部"
            }, {
                "distinguishedName": "李四@bf007525-99a3-4178-a474-32865bdddec8@I",
                "id": "bf007525-99a3-4178-a474-32865bdddec8",
                "name": "李四",
                "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
                "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
                "unitLevelName": "兰德纵横/市场营销部",
                "unitName": "市场营销部"
            }],
            "number": "111",
            "textfield": "杭州",
            "attachment": [
                {
                    "activityName": "拟稿",
                    "extension": "jpg",
                    "id": "9514758e-9e28-4bfe-87d7-824f2811f173",
                    "lastUpdateTime": "2020-12-09 21:48:03",
                    "length": 452863.0,
                    "name": "111.jpg",
                    "person": "李四@lisi@P"
                }
            ]
        },
 ...
 ]
 */
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Datatemplate 数据模板组件。自定义结构和样式的多行数据编辑组件。
 * @o2cn 数据模板
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var datatemplate = this.form.get("name"); //获取组件
 * //方法2
 * var datatemplate = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @since v6.2
 * @o2range {Process|CMS|Protal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Datatemplate = MWF.APPDatatemplate = new Class(
	/** @lends MWF.xApplication.process.Xform.Datatemplate# */
	{
		Implements: [Events],
		Extends: MWF.APP$Module,
		isEdit: false,
		options: {
			/**
			 * 所有内容加载后执行（包括异步加载）。
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterLoad
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 每初始化一个条目，但未加载的时候触发，通过this.event可以获取条目对象。
			 * @event MWF.xApplication.process.Xform.Datatemplate#beforeLoadLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 每一个条目加载后时候触发，通过this.event可以获取条目对象。
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterLoadLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 数据模板改变时触发。通过this.event.lines可以获取修改的条目数组，this.event.type可以获得修改的类型。<br/>
			 * <table>
			 *     <tr><th><b>this.event.type</b></th><th><b>触发类型</b></th><th><b>this.event.lines</b></th></tr>
			 *     <tr><td>addline</td><td>添加一行</td><td>添加的行数组</td></tr>
			 *     <tr><td>deleteline</td><td>删除一行</td><td>删除的行数组</td></tr>
			 *     <tr><td>deletelines</td><td>删除多行</td><td>删除的行数组</td></tr>
			 *     <tr><td>editmodule</td><td>字段值改变时</td><td>this.event.lines为编辑的行数组<br/>this.event.module为修改的字段</td></tr>
			 *     <tr><td>import</td><td>导入数据后</td><td>数据模板所有行</td></tr>
			 * </table>
			 * @event MWF.xApplication.process.Xform.Datatemplate#change
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 添加条目时触发。通过this.event.line可以获取对应的条目对象，this.event.ev可以获得事件触发的Event。
			 * @event MWF.xApplication.process.Xform.Datatemplate#addLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 删除条目前触发。通过this.event可以获取对应的条目对象。
			 * @event MWF.xApplication.process.Xform.Datatemplate#deleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 删除条目后触发。
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterDeleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 导出前触发。
			 * @event MWF.xApplication.process.Xform.DatatablePC#beforeExport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 导出excel的时候触发，this.event指向导出的数据，您可以通过修改this.event来修改数据。
			 * @event MWF.xApplication.process.Xform.Datatemplate#export
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 * @example
			 * <caption>this.event数据格式如下：</caption>
			 * {
			 *  	data : [
			 *   		["姓名","性别","学历","专业","出生日期","毕业日期"], //标题
			 *  		[ "张三","男","大学本科","计算机","2001-1-2","2019-9-2" ], //第一行数据
			 *  		[ "李四","男","大学专科","数学","1998-1-2","2018-9-2" ]  //第二行数据
			 * 	], //导出的数据
			 *     colWidthArray : [100, 50, 100, 200, 150, 150], //每列宽度
			 *     title : "xxxx" //导出的excel文件标题
			 * }
			 */
			/**
			 * 导入前触发。
			 * @event MWF.xApplication.process.Xform.DatatablePC#beforeImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 在导入excel，进行数据校验后触发，this.event指向导入的数据。
			 * @event MWF.xApplication.process.Xform.Datatemplate#validImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 * @example
			 * <caption>this.event数据格式如下：</caption>
			 * {
			 *  	data : [
			 *  	   {
			 *  	 	"姓名" : "张三",
			 *  	 	"性别" : "男",
			 *  	 	"学历" ： "大学本科",
			 *  	    "专业" : "计算机",
			 *  	    "出生日期" : "aa01-1-2",
			 *  	 	"毕业日期" : "2019-9-2",
			 *  	 	"errorTextList" : [
			 *  	 	    "第5列：aa01-1-2不是正确的日期格式。"
			 *  	 	] //校验出的错误信息，如果该行数据正确，则无该字段
			 *  	 }
			 *  	 ...
			 *     ], //导入的数据
			 *     "validted" : true  //是否校验通过，可以在本事件中修改该参数，确定是否强制导入
			 * }
			 */
			/**
			 * 在导入excel，数据校验成功将要设置回数据模板的时候触发，this.event指向整理过的导入数据，格式见{@link DatatemplateData}。
			 * @event MWF.xApplication.process.Xform.Datatemplate#import
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 在导入excel，数据设置回数据模板以后触发，this.event指向整理过的导入数据，格式见{@link DatatemplateData}。
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			"moduleEvents": ["queryLoad","postLoad","load", "afterLoad",
				"beforeLoadLine", "afterLoadLine", "change", "addLine", "deleteLine", "afterDeleteLine", "beforeExport",
				"beforeImport", "export", "import", "validImport", "afterImport"]
		},

		initialize: function(node, json, form, options){
			this.node = $(node);
			this.node.store("module", this);
			this.json = json;
			this.form = form;
			this.field = true;
			this.fieldModuleLoaded = false;
		},
		load: function(){
			this._loadModuleEvents();
			if (this.fireEvent("queryLoad")){
				this._queryLoaded();
				this._loadUserInterface();
				this._loadStyles();
				this._loadDomEvents();
				//this._loadEvents();

				this._afterLoaded();
				this.fireEvent("afterLoad");
				// this.fireEvent("load");
			}
		},
		_loadMergeEditNodeByScript: function(){
			if (this.json.sectionMergeEditScript && this.json.sectionMergeEditScript.code) {
				var data = this.form.Macro.exec(this.json.sectionMergeEditScript.code, this);
				this._setBusinessData( data );
				//this._loadUserInterface();
			}
		},
		_loadMergeEditNodeByDefault: function(){
			var data = this.getSortedSectionData();
			var businessData = [];
			data.each(function(d){
				d.data = d.data || [];
				businessData = businessData.concat( d.data );
			});
			this._setBusinessData(businessData);
			//this._loadUserInterface();
		},
		_loadUserInterface: function(){
			// this.fireEvent("queryLoad");
			debugger;
			this.loading = true;

			if( this.isSectionMergeEdit() ){ //区段合并，删除区段值合并数据后编辑
				if( this.json.mergeTypeEdit === "script" ){
					this._loadMergeEditNodeByScript();
				}else{
					this._loadMergeEditNodeByDefault();
				}
			}

			//区段合并展现
			this.isMergeRead = this.isSectionMergeRead();

			//启用区段且显示所有区段
			this.sectionBy = this._getSectionBy();
			this.isShowAllSection = this.isAllSectionShow();

			var iconNode = this.node.getElement("div[o2icon='datatemplate']");
			if(iconNode)iconNode.destroy();

			// this.editModules = [];
			this.node.setStyle("overflow-x", "auto");
			this.node.setStyle("overflow-y", "hidden");

			this.editable = !(this.readonly || (this.json.isReadonly === true) || (this.form.json.isReadonly === true));
			if( this.isMergeRead )this.editable = false;
			if (this.editable && this.json.editableScript && this.json.editableScript.code){
				this.editable = this.form.Macro.exec(((this.json.editableScript) ? this.json.editableScript.code : ""), this);
			}

			this.getRelativeId();

			//允许导入
			this.importenable  = this.editable && (this.importActionIdList.length > 0) &&
				(this.json.impexpType === "impexp" || this.json.impexpType === "imp");

			//允许导出
			this.exportenable  = (this.exportActionIdList.length > 0) && (this.json.impexpType === "impexp" || this.json.impexpType === "exp");

			if( this.isShowAllSection ){
				this.data = this._getAllSectionData();
			}else if( this.isMergeRead ){
				this.data = this.getSectionMergeReadData();
			}else{
				this.data = this._getValue();
				if( !this._getBusinessData() ){
					this.isNew = true;
					this._setValue(this.data);
				}
			}

			this.lineList = [];
			this.sectionlineList = [];

			//this.data为object的时候才有值
			// this.lineMap = {};

			// this.totalModules = [];
			this._loadStyles();

			//获取html模板和json模板
			this.getTemplate();


			if( !this.form.isLoaded ){ //如果表单还没加载完成
				//通过表单的afterModulesLoad事件设置节点外的操作：添加、删除、导入、导出
				this.setOuterActionEvents();
			}

			//隐藏节点
			this.node.getChildren().hide();

			this.fireEvent("load");
			this._loadDataTemplate(function(){
				// this._loadImportExportAction();
				this.fieldModuleLoaded = true;
				this.loading = false;
				this.fireEvent("postLoad");
			}.bind(this));
		},
		/*
		 * @summary 重新加载数据表格。会触发load和postLoad事件。
		 * @example
		 *  this.form.get("fieldId").reload(); //重新加载
		 */
		reload: function( isReloadTemplate ){
			this.reloading = true;

			// this.editModules = [];

			this.getRelativeId();

			this.checkMerge( this.getValue() );

			this.clearSubModules();

			this._loadStyles();

			if( isReloadTemplate ){ //重新获取html模板和json模板
				this.node.getChildren().setStyle("display", "");
				this.getTemplate();
				this.node.getChildren().hide();
			}

			this.lineList = [];
			this.sectionlineList = [];

			this.fireEvent("load");
			this._loadDataTemplate(function(){
				// this._loadImportExportAction();
				this.reloading = false;
				this.fireEvent("postLoad");
			}.bind(this));
		},
		getRelativeId: function(){
			this.outerAddActionIdList = (this.json.outerAddActionId || "").split(",");
			this.outerDeleteActionIdList = (this.json.outerDeleteActionId || "").split(",");
			this.outerSelectAllIdList = (this.json.outerSelectAllId || "").split(",");

			this.addActionIdList = (this.json.addActionId || "").split(",");
			this.deleteActionIdList = (this.json.deleteActionId || "").split(",");
			this.sequenceIdList = (this.json.sequenceId || "").split(",");
			this.selectorId = this.json.selectorId;

			this.importActionIdList = (this.json.importActionId || "").split(",");
			this.exportActionIdList = (this.json.exportActionId || "").split(",");
		},
		getTemplate: function(){
			this.templateJson = {};
			this.templateHtml = this.node.get("html");
			var moduleNodes = this.form._getModuleNodes(this.node);
			moduleNodes.each(function (node) {
				if (node.get("MWFtype") !== "form") {
					var json = this.form._getDomjson(node);
					this.templateJson[json.id] = json ;
				}
			}.bind(this));
		},
		_loadStyles: function(){
			this.node.setStyles(this.json.styles);
			this.node.set(this.json.properties);
		},
		_getOuterActionModules: function( idList ){ //判断不在数据模板中，但是在表单内的Id
			var list = [];
			idList.each( function (id) {
				var module = this._getModuleByPath(id);
				var tId = id.split("..").getLast();
				if( !this.templateJson.hasOwnProperty(tId) && module ){
					list.push( module );
				}
			}.bind(this));
			return list;
		},
		_setOuterActionEvents: function(){
			this.addActionList = this._getOuterActionModules( [].concat(this.addActionIdList, this.outerAddActionIdList) );
			this.addActionList.each( function (module) {
				var addEvent = function (){
					module.node.addEvents({"click": function(e){
							this._addLine(e);
					}.bind(this)});
					if( !this.editable )module.node.hide();
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					 module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}

			}.bind(this));

			this.deleteActionList = this._getOuterActionModules( [].concat( this.outerDeleteActionIdList ) );
			this.deleteActionList.each( function (module) {
				var addEvent = function (){
					module.node.addEvents({"click": function(e){
						this._deleteSelectedLine(e);
					}.bind(this)});
					if( !this.editable )module.node.hide();
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}
			}.bind(this));

			this.selectAllList = this._getOuterActionModules( this.outerSelectAllIdList );
			this.selectAllList.each( function (module) {
				// module.setData(""); //默认不选中
				var addEvent = function (){
					module.node.addEvents({"click": function(e){
						this._checkSelectAll(e);
					}.bind(this)});
					if( !this.editable )module.node.hide();
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}
			}.bind(this));
			this.selectAllSelector = this.selectAllList[0];
			if(this.selectAllSelector){
				this.unselectAll();
			}

			this.importActionList = this._getOuterActionModules( this.importActionIdList );
			this.importActionList.each( function (module) {
				var addEvent = function (){
					module.node.addEvents({"click": function(e){
						this.importFromExcel();
					}.bind(this)});
					if( !this.editable )module.node.hide();
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}
			}.bind(this));

			this.exportActionList = this._getOuterActionModules( this.exportActionIdList );
			this.exportActionList.each( function (module) {
				var addEvent = function (){
					module.node.addEvents({"click": function(e){
						this.exportToExcel();
					}.bind(this)})
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}
			}.bind(this));
		},
		setOuterActionEvents: function(){

			this.bindEvent = function () {
				this._setOuterActionEvents();

				this.fireEvent("afterLoad");

				//加载完成以后，删除事件
				this.form.removeEvent("afterModulesLoad", this.bindEvent );
			}.bind(this);

			//去要表单的所有组件加载完成以后再去获取外部组件
			this.form.addEvent("afterModulesLoad", this.bindEvent );
		},
		isShowSectionKey: function(){
			return this.json.showSectionKey && this.isMergeRead ;
		},
		isShowSectionBy: function(){
			return this.json.showSectionBy && this.isShowAllSection ;
		},
		isSectionData: function(){ //数据是否经过区段处理
			var data = this.getBusinessDataById();
			if( o2.typeOf( data ) === "object" ){
				var keys = Object.keys(data);
				// if( o2.typeOf(data[keys[0]]) === "array"  ){
				// 	return true;
				// }
				for( var i=0; i<keys.length; i++ ){
					var key = keys[i];
					if( key !== "data" ){
						if( o2.typeOf(data[key]) === "array"  ){
							return true;
						}
					}
				}
			}
			return false;
		},
		/**
		 * @summary 当数据模板设置为区段合并展现、区段合并编辑时，可以使用本方法获取所有区段数据。
		 * @return {Object} 对象.
		 * @example
		 * var data = this.form.get("fieldId").getAllSectionData();
		 * //data格式如下：
		 * {
		 *  	"3455b82a-399c-4ee4-b9b9-e70ae40fbaf1": [ //区段1的key和data
		 *			{
		 *				"good": "yf",
		 *				"number_2": 11,
		 *				"prize": 1
		 *			}
		 *		],
		 *  	"83de86fc-60bc-4b4c-955c-1085915865a4": [ //区段2的key和data
		 *  		{
		 *  			"good": "yf",
		 *  			"number_2": 11,
		 *  			"prize": 10
		 *  		}
		 *  	]
		 *  }
		 */
		getAllSectionData: function(){
			return this.getBusinessDataById();
		},
		_getAllSectionData: function(){
			var bData = this.getBusinessDataById();
			var flag = false;
			if( !bData ){
				flag = true;
				bData = {};
			}
			if( !bData[this.sectionBy] ){
				flag = true;
				this.isNew = true;
				bData[this.sectionBy] = this.getValue();
			}
			if( flag )this.setBusinessDataById( bData );
			this.dataWithSectionBy = this.getAllSortedSectionData();
			return flag ? this.getBusinessDataById() : bData;
		},
		getAllSortedSectionData: function(){ //获取合并排序后的数据
			var data = this.getBusinessDataById();
			var array = [];
			for( var key in data ){
				array.push({
					sectionKey: key,
					key: key,
					data: data[key]
				})
			}
			if( this.json.sectionDisplaySortScript && this.json.sectionDisplaySortScript.code){
				array.sort( function(a, b){
					this.form.Macro.environment.event = {
						"a": a,
						"b": b
					};
					var flag = this.form.Macro.exec(this.json.sectionDisplaySortScript.code, this);
					this.form.Macro.environment.event = null;
					return flag;
				}.bind(this))
			}
			return array;
		},
		getSectionMergeReadData: function(){
			switch (this.json.mergeTypeRead) {
				case "dataScript":
					if (this.json.sectionMergeReadDataScript && this.json.sectionMergeReadDataScript.code) {
						return this.form.Macro.exec(this.json.sectionMergeReadDataScript.code, this);
					}else{
						return [];
					}
				default:
					var sortedData = this.getSortedSectionData();
					if( this.json.showSectionKey ){
						this.dataWithSectionKey = sortedData;
					}
					var data = [];
					//把区段值放在每行的数据里
					sortedData.each(function(d){
						( d.data || [] ).each(function( obj ){
							if( o2.typeOf(obj) === "object" ){
								// obj.sectionKey = d.sectionKey;
								data.push( obj )
							}
						});
						// data = data.concat( d.data.data );
					});
					return data;
			}
		},
		getDefaultValue: function(){
			var value;
			if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
			if (value && !value.then) if (o2.typeOf(value)==="object") value = [value];
			if(!value){
				value = [];
				var count = this.json.defaultCount ? this.json.defaultCount.toInt() : 0;
				for( var i=0; i<count; i++ )value.push({})
			}
			return value;
		},
		_getValue: function(){
			if (this.moduleValueAG) return this.moduleValueAG;
			var value = this._getBusinessData();
			if( !value ){
				value = this.getDefaultValue();
			}
			// if (!value){
			// 	if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
			// 	if (value && !value.then) if (o2.typeOf(value)==="object") value = [value];
			// }
			// if(!value){
			// 	value = [];
			// 	var count = this.json.defaultCount ? this.json.defaultCount.toInt() : 0;
			// 	for( var i=0; i<count; i++ )value.push({})
			// }
			return value;
		},
		getValue: function(){
			return this._getValue();
		},

		_setValue: function(value){
			if (!!value && o2.typeOf(value.then)=="function"){
				var p = o2.promiseAll(value).then(function(v){
					this.__setValue(v);
				}.bind(this), function(){});
				this.moduleValueAG = p;
				p.then(function(){
					this.moduleValueAG = null;
				}.bind(this), function(){
					this.moduleValueAG = null;
				}.bind(this));
			}else{
				this.moduleValueAG = null;
				this.__setValue(value);
			}
		},
		__setValue: function(value){
			this._setBusinessData(value);
			this.moduleValueAG = null;
			return value;
		},

		_loadDataTemplate: function(callback, operation){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				// if (o2.typeOf(this.data)=="object") this.data = [this.data];


				if( this.isShowAllSection ){
					this._loadSectionLineList_EditSection(callback, operation)
				}else if( this.isShowSectionKey() ){
					this._loadSectionLineList(callback, operation)
				}else{
					this._loadLineList(callback, operation);
				}

				this.moduleValueAG = null;
				return v;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
			this.moduleValueAG = p;
			if (this.moduleValueAG) this.moduleValueAG.then(function(){
				this.moduleValueAG = null;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
		},
		_loadSectionLineList_EditSection: function(callback, operation){
			var map = this.unchangedSectionLineMap || {};
			//this._getAllSectionData();
			this.dataWithSectionBy.each(function(data, idx){
				var isEdited = false;
				if( this.isSectionLineEditable( data ) && this.editable ){
					isEdited = true;
				}
				var isNew = false;
				var sectionLine;
				var beforeNode = idx > 0 ? this.sectionlineList[idx-1].node : null;
				if( map[data.sectionKey] ) {
					sectionLine =  map[data.sectionKey];
					if( !operation || operation === "moveUpList"){
						this._injectLineNode( sectionLine.node, beforeNode );
					}
					sectionLine.setIndex( data, idx, isEdited, isNew, operation );
					//console.log("setIndex", sectionLine);
				}else{
					var div = this._injectLineNode( new Element("div"), beforeNode );
					sectionLine = this._loadSectionLine_EditSection(div, data, idx, isEdited, isNew );
					//console.log("_loadSectionLine_EditSection", sectionLine);
				}
				if( this.sectionBy && this.sectionBy === data.sectionKey ){
					this.sectionLineEdited = sectionLine;
				}
				this.sectionlineList.push(sectionLine);
			}.bind(this))
			if (callback) callback();
		},
		_loadSectionLine_EditSection: function(container, data, index, isEdited, isNew){
			var sectionLine = new MWF.xApplication.process.Xform.Datatemplate.SectionLine(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isDeleteable: this.editable && this.isSectionLineEditable(data),
				isAddable: this.editable && this.isSectionLineEditable(data)
			});
			// this.fireEvent("beforeLoadLine", [line]);
			sectionLine.load();
			// this.fireEvent("afterLoadLine", [line]);
			return sectionLine;
		},
		isSectionLineEditable: function(data){
			return this.isShowAllSection && this.sectionBy && this.sectionBy === data.sectionKey;
		},

		_loadSectionLineList: function(callback, operation){
			var map = this.unchangedSectionLineMap || {};
			this.dataWithSectionKey.each(function(data, idx){
				var sectionLine;
				var isEdited = false;
				var isNew = false;
				var beforeNode = idx > 0 ? this.sectionlineList[idx-1].node : null;
				if( map[data.sectionKey] ) {
					sectionLine =  map[data.sectionKey];
					if( !operation || operation === "moveUpList"){
						this._injectLineNode( sectionLine.node, beforeNode );
					}
					//console.log("setIndex", sectionLine);
					sectionLine.setIndex( data, idx, isEdited, isNew, operation );
				}else {
					//var div = new Element("div").inject(this.node);
					var div = this._injectLineNode( new Element("div"), beforeNode );
					sectionLine = this._loadSectionLine(div, data, idx, isEdited, isNew );
					//console.log("_loadSectionLine", sectionLine);
				}
				this.sectionlineList.push(sectionLine);
			}.bind(this))
			if (callback) callback();
		},
		_loadSectionLine: function(container, data, index, isEdited, isNew){
			var sectionLine = new MWF.xApplication.process.Xform.Datatemplate.SectionLine(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isDeleteable: this.editable,
				isAddable: this.editable,
				isMergeRead: this.isMergeRead
			});
			// this.fireEvent("beforeLoadLine", [line]);
			sectionLine.load();
			// this.fireEvent("afterLoadLine", [line]);
			return sectionLine;
		},
		_loadLineList: function(callback, operation){
			var map = this.unchangedLineMap || {};
			Object.each(map, function (line, idx) {
				line.setIndex( idx.toInt() );
			});
			this.data.each(function(data, idx){
				var idxStr = idx.toString();
				var beforeNode = idx > 0 ? this.lineList[idx - 1].node : null;
				if( map[idxStr] ){
					if( !operation || operation === "moveUpList" ){
						this._injectLineNode( map[idxStr].node, beforeNode );
					}
					//console.log("inject", map[idxStr]);
					this.lineList.push( map[idxStr] );
				}else{
					var isNew = this.isNew || (o2.typeOf(this.newLineIndex) === "number" ? idx === this.newLineIndex : false);
					var div = this._injectLineNode( new Element("div"), beforeNode );
					var line = this._loadLine(div, data, idx, isNew);
					//console.log("_loadLine", line);
					this.lineList.push(line);
				}
			}.bind(this));
			this.newLineIndex = null;
			this.isNew = false;
			if (callback) callback();
		},
		_loadLine: function(container, data, index, isNew){
			var line = new MWF.xApplication.process.Xform.Datatemplate.Line(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isEdited : this.editable,
				isNew : isNew,
				isMergeRead: this.isMergeRead
			});
			this.fireEvent("beforeLoadLine", [line]);
			line.load();
			this.fireEvent("afterLoadLine", [line]);
			return line;
		},
		_injectLineNode: function( lineNode, beforeNode ){
			if( beforeNode ){
				lineNode.inject( beforeNode, "after");
			}else{
				lineNode.inject( this.node );
			}
			return lineNode;
		},
		isMax : function(){
			var maxCount = this.json.maxCount ? this.json.maxCount.toInt() : 0;
			if( this.editable && maxCount > 0 ) {
				if( this.isShowAllSection ){
					if( this.sectionLineEdited && this.sectionLineEdited.lineList.length >= maxCount )return true;
				}else{
					if( this.lineList.length >= maxCount )return true;
				}
			}
			return false;
		},
		isMin : function(){
			var minCount = this.json.minCount ? this.json.minCount.toInt() : 0;
			if( this.editable && minCount > 0 ) {
				if( this.isShowAllSection ){
					if( this.sectionLineEdited && this.sectionLineEdited.lineList.length <= minCount )return true;
				}else {
					if( this.lineList.length <= minCount )return true;
				}
			}
			return false;
		},
		_setLineData: function(line, d){
			if( line.sectionLine ){
				var data = this.getBusinessDataById();
				var sdata = data[ line.sectionLine.sectionKey ];
				if( sdata  ){
					sdata[line.options.indexInSectionLine] = d;
					this.setAllSectionData( data, false, "setLineData" );
				}
			}else{
				var index = line.options.index;
				var data = this.getInputData();
				data[index] = d;
				this.setData( data, false, "setLineData" );
			}
		},
		_addLine: function(ev, d){

			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}

			var data, index, newLine;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = [];
				}

				sdata.push(d||{});
				index = sdata.length - 1;
				this.newLineIndex = index;

				this.setAllSectionData( data , false, "addLine");
				newLine = this.sectionLineEdited.lineList[index];
			}else{
				data = this.getInputData();

				data.push(d || {});
				index = data.length-1;
				this.newLineIndex = index;
				this.setData( data, false, "addLine" );
				newLine = this.getLine(index);
			}

			this.validationMode();
			this.fireEvent("addLine",[{"line":newLine, "ev":ev}]);
			this.fireEvent("change", [{"lines":[newLine], "type":"addline"}]);
			return newLine;
		},
		_insertLine: function(ev, beforeLine){
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}

			//使用数据驱动
			var data, index, newLine;
			if( this.isShowAllSection ){
				index = beforeLine.options.indexInSectionLine + 1;

				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = [];
				}
				sdata.splice(index, 0, {});
				this.newLineIndex = index;

				this.setAllSectionData( data , false, "insertLine");
				newLine = this.sectionLineEdited.lineList[index];
			}else {
				index = beforeLine.options.index+1;
				data = this.getInputData();
				data.splice(index, 0, {});
				this.newLineIndex = index;
				this.setData( data, false, "insertLine" );
				newLine = this.getLine( index );
			}

			this.validationMode();
			this.fireEvent("addLine",[{"line":newLine, "ev":ev}]);
			this.fireEvent("change", [{"lines":[newLine], "type":"addline"}]);
			return newLine;
		},
		_insertLineByIndex: function(ev, index, d){
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}
			//使用数据驱动
			var data, newLine;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = [];
				}
				if (sdata.length < index) return null;
				sdata.splice(index, 0, d || {});
				this.newLineIndex = index;

				this.setAllSectionData( data , false, "insertLine");
				line = this.sectionLineEdited.lineList[index];
			}else {
				data = this.getInputData();
				if(data.length < index )return null;
				data.splice(index, 0, d||{});
				this.newLineIndex = index;
				this.setData( data , false, "insertLine");
				newLine = this.getLine( index );
			}

			this.validationMode();
			this.fireEvent("addLine",[{"line":newLine, "ev":ev}]);
			this.fireEvent("change", [{"lines":[newLine], "type":"addline"}]);
			return newLine;
		},
		_deleteSelectedLine: function(ev){
			var selectedLine = this.lineList.filter(function (line) { return line.selected; });
			if( selectedLine.length === 0 ){
				this.form.notice( MWF.xApplication.process.Xform.LP.selectItemNotice,"info");
				return false;
			}
			var minCount = this.json.minCount ? this.json.minCount.toInt() : 0;
			if( minCount > 0 ){
				var length;
				if( this.isShowAllSection && this.sectionLineEdited ){
					length = this.sectionLineEdited.lineList.length;
				}else{
					length = this.lineList.length;
				}
				if( length - selectedLine.length < minCount ){
					var text = MWF.xApplication.process.Xform.LP.minItemNotice.replace("{n}", minCount );
					this.form.notice(text,"info");
					return false;
				}
			}
			var _self = this;
			this.form.confirm("warn", ev, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteSelectedItemNotice, 300, 120, function(){

				_self._delLines( selectedLine );

				this.close();

			}, function(){
				this.close();
			}, null, null, this.form.json.confirmStyle);

		},
		_delLines: function(lines){
			var _self = this;
			var saveFlag = false;

			var data;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
			}else{
				data = _self.getInputData();
			}

			lines.reverse().each(function(line){
				_self.fireEvent("deleteLine", [line]);

				if(line.deleteAttachment())saveFlag = true;

				if( line.sectionLine ){
					var d = data[ line.sectionLine.sectionKey ];
					if( d ){
						d.splice(line.options.indexInSectionLine, 1);
					}
				}else {
					data.splice(line.options.index, 1);
				}

				_self.fireEvent("afterDeleteLine");
			});

			if( this.isShowAllSection ){
				_self.setAllSectionData(data, false, "deleteLines");
			}else{
				_self.setData( data , false, "deleteLines");
			}
			this.validationMode();

			_self.fireEvent("change", [{"lines":lines, "type":"deletelines"}]);

			if(saveFlag)this.form.saveFormData();
		},
		_deleteLine: function(ev, line){
			if( this.isMin() ){
				var text = MWF.xApplication.process.Xform.LP.minItemCountNotice.replace("{n}", this.json.minCount );
				this.form.notice(text,"info");
				return false;
			}
			var _self = this;
			this.form.confirm("warn", ev, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
				_self._delLine(line);
				this.close();
			}, function(){
				this.close();
			}, null, null, this.form.json.confirmStyle);
		},
		_delLine: function(line){
			this.fireEvent("deleteLine", [line]);

			var saveFlag = line.deleteAttachment();
			//使用数据驱动
			var data;
			if( line.sectionLine ){
				var data = this.getBusinessDataById();
				var d = data[ line.sectionLine.sectionKey ];
				if( d ){
					d.splice(line.options.indexInSectionLine, 1);
				}
				this.setAllSectionData( data, false, "deleteLine" );
			}else{
				data = this.getInputData();
				data.splice(line.options.index, 1);
				this.setData( data , false, "deleteLine");
			}

			this.validationMode();
			this.fireEvent("afterDeleteLine");

			this.fireEvent("change", [{"lines":[line], "type":"deleteline"}]);

			if(saveFlag)this.form.saveFormData();
		},
		_checkSelectAll: function () {
			var selectData = this.selectAllSelector.getData();
			var selected;
			if(o2.typeOf(selectData)==="array"){
				selected = selectData.contains(this.json.outerSelectAllSelectedValue);
			}else{
				selected = selectData === this.json.outerSelectAllSelectedValue;
			}
			this.selected = selected;
			if( this.isShowAllSection && this.sectionLineEdited){
				this.sectionLineEdited.lineList.each(function (line) {
					this.selected ? line.select() : line.unselect();
				}.bind(this))
			}else{
				this.lineList.each(function (line) {
					this.selected ? line.select() : line.unselect();
				}.bind(this))
			}
		},
		selectAll: function(){
			this.selected = true;
			if(this.selectAllSelector)this.selectAllSelector.setData(this.json.outerSelectAllSelectedValue);
		},
		unselectAll: function(){
			this.selected = false;
			if( this.selectAllSelector.getOptionsObj ){
				var options = this.selectAllSelector.getOptionsObj();
				var value = "";
				var arr = options.valueList || [];
				for( var i=0; i<arr.length; i++ ){
					var v = arr[i];
					if( v !== this.json.outerSelectAllSelectedValue ){
						value = v;
						break;
					}
				}
				this.selectAllSelector.setData(value);
			}else{
				this.selectAllSelector.setData("")
			}
		},

		// editValidation: function(){
		// 	var flag = true;
		// 	this.editModules.each(function(field, key){
		// 		if (field.json.type!=="sequence" && field.validationMode ){
		// 			field.validationMode();
		// 			if (!field.validation()) flag = false;
		// 		}
		// 	}.bind(this));
		// 	return flag;
		// },
		exportToExcel: function(){
			this.exporter = new MWF.xApplication.process.Xform.Datatemplate.Exporter(this);
			this.exporter.exportToExcel();
		},
		importFromExcel: function(){
			this.importer = new MWF.xApplication.process.Xform.Datatemplate.Importer(this);
			this.importer.importFromExcel();
		},


		_afterLoaded: function(){
		},
		// /**
		//  * @summary 重置数据模板的值为默认值或置空。
		//  *  @example
		//  * this.form.get('fieldId').resetData();
		//  */
		resetData: function(){
			// var value = this.getDefaultValue() || [];
			var value = this.getValue() || [];
			this.setData(value);
		},
		/**当参数为Promise的时候，请查看文档: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|使用Promise处理表单异步}<br/>
		 * 当表单上没有对应组件的时候，可以使用this.data[fieldId] = data赋值。
		 * @summary 为数据模板赋值。
		 * @param data{DatatemplateData|Promise|Array} 必选，数组或Promise.
		 * @example
		 *  this.form.get("fieldId").setData([]); //赋空值
		 * @example
		 *  //如果无法确定表单上是否有组件，需要判断
		 *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
		 *      this.form.get('fieldId').setData( data );
		 *  }else{
		 *      this.data['fieldId'] = data;
		 *  }
		 *@example
		 *  //使用Promise
		 *  var field = this.form.get("fieldId");
		 *  var promise = new Promise(function(resolve, reject){ //发起异步请求
		 *    var oReq = new XMLHttpRequest();
		 *    oReq.addEventListener("load", function(){ //绑定load事件
		 *      resolve(oReq.responseText);
		 *    });
		 *    oReq.open("GET", "/data.json"); //假设数据存放在data.json
		 *    oReq.send();
		 *  });
		 *  promise.then( function(){
		 *    var data = field.getData(); //此时由于异步请求已经执行完毕，getData方法获得data.json的值
		 * })
		 *  field.setData( promise );
		 */
		setData: function(data, fireChange, operation){
			if (!data){
				data = this._getValue();
			}
			this._setData(data, fireChange, operation);
		},
		_setData: function(data, fireChange, operation){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				// if (o2.typeOf(data)==="object") data = [data];
				this.__setData(data, fireChange, operation);
				this.moduleValueAG = null;
				return v;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
			this.moduleValueAG = p;
			if (this.moduleValueAG) this.moduleValueAG.then(function(){
				this.moduleValueAG = null;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
		},
		__setData: function(data, fireChange, operation){
			if( this.isShowAllSection ){
				//兼容外部对编辑当前区段的setData，内部的setData不走这里，直接走setAllSectionData
				this._setEditedSectionData(data, fireChange, operation);
				return;
			}else if( this.isMergeRead ){
				//合并且只读，不允许setData
				throw new Error("The data template is merged and read-only, you can use the 'setAllSectionData' method to set all section data");
			}

			var old;
			if(fireChange)old = Object.clone(this._getBusinessData() || {});

			this._setUnchangedLineMap(data, operation);

			this._setBusinessData(data);
			this.data = data;

			if (this.data){
				this.clearSubModules();
			}

			this.lineList = [];
			this.sectionlineList = [];
			this._loadDataTemplate(function(){
				this._setSubDatatemplateOuterEvents();
				if (fireChange && JSON.stringify(old) !== JSON.stringify(data)) this.fireEvent("change");
				this.unchangedLineMap = null;
			}.bind(this), operation);
		},
		_setSubDatatemplateOuterEvents: function(){
			//告诉下层的数据模板绑定外部事件
			for (var i=0; i<this.lineList.length; i++){
				this.lineList[i].setSubDatatemplateOuterActionEvents();
			}
		},
		_setEditedSectionData: function(data, fireChange, operation){
			if( this.isShowAllSection ){
				var d = this.getBusinessDataById();
				d[ this.sectionBy ] = data || { data: [] };
				this.setAllSectionData( d, fireChange , operation);
			}
		},
		/**
		 * @summary 当数据模板设置为区段合并展现、区段合并编辑时，可以使用本方法设置所有区段数据。
		 * @param data{Object} 必选，对象.
		 * @param fireChange{boolean} 可选，是否触发change事件，默认false.
		 * @example
		 *  this.form.get("fieldId").setAllSectionData({}); //赋空值
		 * @example
		 *  this.data['fieldId'].setAllSectionData({
		 *  	"3455b82a-399c-4ee4-b9b9-e70ae40fbaf1": [ //区段1的key和data
		 *			{
		 *				"good": "yf",
		 *				"number_2": 11,
		 *				"prize": 1
		 *			}
		 *		],
		 *  	"83de86fc-60bc-4b4c-955c-1085915865a4": [ //区段2的key和data
		 *  		{
		 *  			"good": "yf",
		 *  			"number_2": 11,
		 *  			"prize": 10
		 *  		}
		 *  	]
		 *  });
		 */
		setAllSectionData: function(data, fireChange, operation){
			var old;
			if(fireChange)old = Object.clone(this.getBusinessDataById() || {});

			this._setUnchangedSectionLineMap(data);

			this.setBusinessDataById(data);

			if( operation ){
				this.data = this.isShowAllSection ? this._getAllSectionData() : data;
			}else{
				this.checkMerge(data);
			}

			if (this.data){
				this.clearSubModules();
			}

			if (fireChange && JSON.stringify(old) !== JSON.stringify(data)) this.fireEvent("change");

			this.lineList = [];
			this.sectionlineList = [];
			this._loadDataTemplate(function () {
				Object.each(this.unchangedSectionLineMap, function(sline, key){
					sline.isUnchangedAll = null;
					sline.unchangedLineMap = null;
				});
				this.unchangedSectionLineMap = null;
			}.bind(this), operation);
		},

		checkMerge: function(data){
			//区段合并后编辑
			var isMergeEidt = this.isSectionMergeEdit();
			if( isMergeEidt ){ //区段合并，删除区段值合并数据后编辑
				if( this.json.mergeTypeEdit === "script" ){
					this._loadMergeEditNodeByScript();
				}else{
					this._loadMergeEditNodeByDefault();
				}
			}
			//区段合并展现
			this.isMergeRead = this.isSectionMergeRead();
			//启用区段且显示所有区段
			this.sectionBy = this._getSectionBy();
			this.isShowAllSection = this.isAllSectionShow();

			if( this.isShowAllSection ){
				this.data = this._getAllSectionData();
			}else if( this.isMergeRead ) {
				this.data = this.getSectionMergeReadData();
			}else if( isMergeEidt ){
				this.data = this._getBusinessData()
			}else{
				this.data = data;
			}
		},
		_setUnchangedSectionLineMap: function( data, operation ){
			if( !data ){
				return;
			}
			var map = {};
			if( this.sectionlineList && this.sectionlineList.length ){
				this.sectionlineList.each(function (sline, i) {
					for( var key in data ){
						if( key === sline.sectionKey ){
							var m = sline._setUnchangedLineMap(data[key], operation);
							if( m )map[key] = m;
							break;
						}
					}
				}.bind(this));
			}
			this.unchangedSectionLineMap = map;
		},
		_setUnchangedLineMap: function(data, operation){
			if( !data ){
				this.unchangedLineMap = {};
				return;
			}
			var fromOutside = !operation;
			var lineDataList = this.lineList.map(function (line) {
				if( line.options.isEdited !== this.editable )return "";
				if( line.options.isDeleteable !== this.editable )return "";
				if( line.options.isAddable !== this.editable )return "";
				return fromOutside ?  JSON.stringify(line.data) : line.data;
			}.bind(this));

			var dStr, map = {};
			data.each(function (d, idx) {
				if(fromOutside)dStr = JSON.stringify(d);
				for (var i = 0; i < this.lineList.length; i++) {
					var isEqual= fromOutside ? (dStr === lineDataList[i]) : (d === lineDataList[i] );
					if ( isEqual ) {
						map[idx] = this.lineList[i];
						lineDataList[i] = "";
						break;
					}
				}
			}.bind(this));
			this.unchangedLineMap = map;
		},
		clearSubModules: function(){
			if( this.sectionlineList && this.sectionlineList.length ){
				for( var i=0; i<this.sectionlineList.length; i++ ){
					this.sectionlineList[i].clearSubModules();
				}
			}else{
				var map = this.unchangedLineMap || {};
				var lines = [];
				Object.values(map).each(function (d) {
					lines = lines.concat(d);
				});
				for (var i=0; i<this.lineList.length; i++){
					//this.lineList[i].clearSubModules();
					var l = this.lineList[i];
					if(!lines.contains(l))l.clearSubModules();
				}
			}
		},
		resetId: function(){
			if( this.sectionlineList && this.sectionlineList.length ){
				this.sectionlineList.each(function (sline, i) {
					sline.resetId();
				}.bind(this));
			}else{
				for (var i=0; i<this.lineList.length; i++){
					this.lineList[i].resetId();
				}
			}
		},
		/**
		 * @summary 判断数据模板是否为空.
		 * @example
		 * if( this.form.get('fieldId').isEmpty() ){
		 *     this.form.notice('至少需要添加一条数据', 'warn');
		 * }
		 * @return {Boolean} 是否为空
		 */
		isEmpty: function(){
			var data = this.getInputData();
			if( !data )return true;
			if( o2.typeOf( data ) === "array" ){
				return data.length === 0;
			}
			if( o2.typeOf( data ) === "object" ){
				return Object.keys(data).length === 0;
			}
			return false;
		},
		//api 相关开始
		/**
		 * 获取对应的条目。
		 * @param {Number} index 条目序号，从零开始
		 * @return {MWF.xApplication.process.Xform.Datatemplate.Line | Null} 对应的数据模板条目
		 * @example
		 * //获取数据模板“dt1”的第一个条目。
		 * var line = this.form.get("dt1").getLine(0);
		 * //获取第一个条目subject字段的值
		 * var data = line.get("subject").getData();
		 * //设置subject字段的值
		 * line.get("subject").setData("test1");
		 */
		getLine: function(index){
			var line = this.lineList[index];
			return line || null;
		},
		/**
		 * 在数据模板末尾添加条目。
		 * @param {Object} [data] 添加条目的数据。
		 * @return {MWF.xApplication.process.Xform.Datatemplate.Line} 添加的数据模板条目
		 * @example
		 * var line = this.form.get("dt1").addLine();
		 */
		addLine: function( data ){
			return this._addLine( null, data );
		},
		/**
		 * 在数据模板指定位置添加条目。
		 * @param {Number} index 条目序号，从零开始，如果下标超过当前数据模板条目数，插入失败并返回null。
		 * @param {Object} [data] 添加条目的数据。
		 * @return {MWF.xApplication.process.Xform.Datatemplate.Line | Null} 插入的数据模板条目
		 * @example
		 * var line = this.form.get("dt1").insertLine(0);
		 */
		insertLine: function(index, data){
			return this._insertLineByIndex(null, index, data);
		},
		/**
		 * 删除指定位置的条目。
		 * @param {Number} index 条目序号，从零开始，如果下标超过当前数据模板条目数，删除失败。
		 * @example
		 * //直接删除第一个条目
		 * this.form.get("dt1").deleteLine(0);
		 */
		deleteLine: function(index, ev){
			var line = this.lineList[index];
			if( !line )return null;
			// if( ev ){
			// 	this._deleteLine(ev, line);
			// }else{
			this._delLine(line);
			// }
		},
		/**
		 * 获取对应表单组件，作用等同于get。
		 * @param {Number} index 条目序号，从零开始
		 * @param {String} id 组件标识
		 * @return {FormComponent} 对应表单组件
		 * @example
		 * //获取数据模板“dt1”的第一个条目的subject字段。
		 * var module = this.form.get("dt1").getModule(0, "subject");
		 * //获取subject字段的值
		 * var data = module.getData();
		 * //设置subject字段的值
		 * module.setData("test1");
		 */
		getModule: function(index, id){
			var line = this.lineList[index];
			if( !line )return null;
			return line.getModule(id);
		},
		/**
		 * 获取对应表单组件，作用等同于getModule。
		 * @param {Number} index 条目序号，从零开始
		 * @param {String} id 组件标识
		 * @return {FormComponent} 对应表单组件
		 * @example
		 * //获取数据模板“dt1”的第一个条目的subject字段。
		 * var module = this.form.get("dt1").get(0, "subject");
		 * //获取subject字段的值
		 * var data = module.getData();
		 * //设置subject字段的值
		 * module.setData("test1");
		 */
		get: function(index, id){
			return this.getModule(index, id);
		},
		//api 相关

		/**
		 * 在脚本中使用 this.data[fieldId] 也可以获取组件值。
		 * 区别如下：<br/>
		 * 1、当使用Promise的时候<br/>
		 * 使用异步函数生成器（Promise）为组件赋值的时候，用getData方法立即获取数据，可能返回修改前的值，当Promise执行完成以后，会返回修改后的值。<br/>
		 * this.data[fieldId] 立即获取数据，可能获取到异步函数生成器，当Promise执行完成以后，会返回修改后的值。<br/>
		 * {@link https://www.yuque.com/o2oa/ixsnyt/ws07m0#EggIl|具体差异请查看链接}<br/>
		 * 2、当表单上没有对应组件的时候，可以使用this.data[fieldId]获取值，但是this.form.get('fieldId')无法获取到组件。
		 * @summary 获取数据模板数据.
		 * @example
		 * var data = this.form.get('fieldId').getData();
		 *@example
		 *  //如果无法确定表单上是否有组件，需要判断
		 *  var data;
		 *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
		 *      data = this.form.get('fieldId').getData();
		 *  }else{
		 *      data = this.data['fieldId']; //直接从数据中获取字段值
		 *  }
		 *  @example
		 *  //使用Promise
		 *  var field = this.form.get("fieldId");
		 *  var promise = new Promise(function(resolve, reject){ //发起异步请求
		 *    var oReq = new XMLHttpRequest();
		 *    oReq.addEventListener("load", function(){ //绑定load事件
		 *      resolve(oReq.responseText);
		 *    });
		 *    oReq.open("GET", "/data.json"); //假设数据存放在data.json
		 *    oReq.send();
		 *  });
		 *  promise.then( function(){
		 *    var data = field.getData(); //此时由于异步请求已经执行完毕，getData方法获得data.json的值
		 * })
		 *  field.setData( promise );
		 * @return {DatatemplateData}
		 */
		getData: function(){
			if( this.importer ){
				this.importer.destroySimulateModule();
			}
			if (this.editable!==false){
				// var data = [];
				// this.lineList.each(function(line, index){
				// 	data.push(line.getData())
				// });
				//
				// this.data = data;
				//
				// this._setBusinessData(this.data);
				//
				// return (this.data.length) ? this.data : [];
				this.lineList.each(function (line) {
					line.computeModuleData("save");
				})
				return this._getBusinessData();
			}else{
				return this._getBusinessData();
			}
		},
		getInputData: function(){
			if( this.importer ){
				this.importer.destroySimulateModule();
			}
			if (this.editable!==false){
				return this._getBusinessData();
			}else{
				return this._getBusinessData();
			}
		},
		_getSectionBy: function(){
			if (this.json.section!=="yes"){
				return "";
			}else {
				switch (this.json.sectionBy){
					case "person":
						return layout.desktop.session.user.id;
					case "unit":
						return (this.form.businessData.task) ? this.form.businessData.task.unit : "";
					case "activity":
						return (this.form.businessData.work) ? this.form.businessData.work.activity : "";
					case "splitValue":
						return (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
					case "script":
						if( this.json.sectionByScript && this.json.sectionByScript.code){
							return this.form.Macro.exec(this.json.sectionByScript.code, this) || "";
						}else{
							return "";
						}
					default:
						return "";
				}
			}
		},
		createErrorNode: function(text){
			var node = new Element("div");
			var iconNode = new Element("div", {
				"styles": {
					"width": "20px",
					"height": "20px",
					"float": "left",
					"background": "url("+"../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
				}
			}).inject(node);
			var textNode = new Element("div", {
				"styles": {
					"line-height": "20px",
					"margin-left": "20px",
					"color": "red",
					"word-break": "keep-all"
				},
				"text": text
			}).inject(node);
			return node;
		},
		notValidationMode: function(text){
			if (!this.isNotValidationMode){
				this.isNotValidationMode = true;
				this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
				this.node.setStyle("border", "1px solid red");

				this.errNode = this.createErrorNode(text).inject(this.node, "after");
				this.showNotValidationMode(this.node);

				var parentNode = this.errNode;
				while( parentNode && parentNode.offsetParent === null ){
					parentNode = parentNode.getParent();
				}

				if (parentNode && !parentNode.isIntoView()) parentNode.scrollIntoView(false);
			}
		},
		showNotValidationMode: function(node){
			var p = node.getParent("div");
			if (p){
				if (p.get("MWFtype") == "tab$Content"){
					if (p.getParent("div").getStyle("display")=="none"){
						var contentAreaNode = p.getParent("div").getParent("div");
						var tabAreaNode = contentAreaNode.getPrevious("div");
						var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
						var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
						tabNode.click();
						p = tabAreaNode.getParent("div");
					}
				}
				this.showNotValidationMode(p);
			}
		},
		validationMode: function(){
			if (this.isNotValidationMode){
				this.isNotValidationMode = false;
				this.node.setStyles(this.node.retrieve("borderStyle"));
				if (this.errNode){
					this.errNode.destroy();
					this.errNode = null;
				}
			}
		},

		validationConfigItem: function(routeName, data){
			var flag = (data.status=="all") ? true: (routeName == data.decision);
			if (flag){
				var n = this.getData();
				if( o2.typeOf(n)==="object"){
					var arr = [];
					Object.each( n, function (d, key) {
						if(o2.typeOf(d) === "array")arr = arr.concat(d);
					});
					n = arr;
				}
				var v = (data.valueType=="value") ? n : n.length;
				switch (data.operateor){
					case "isnull":
						if (!v){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "notnull":
						if (v){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "gt":
						if (v>data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "lt":
						if (v<data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "equal":
						if (v==data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "neq":
						if (v!=data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "contain":
						if (v.indexOf(data.value)!=-1){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "notcontain":
						if (v.indexOf(data.value)==-1){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
				}
			}
			return true;
		},
		validationConfig: function(routeName, opinion){
			if (this.json.validationConfig){
				if (this.json.validationConfig.length){
					for (var i=0; i<this.json.validationConfig.length; i++) {
						var data = this.json.validationConfig[i];
						if (!this.validationConfigItem(routeName, data)) return false;
					}
				}
				return true;
			}
			return true;
		},
		saveValidation: function () {
			var flag = true;
			for (var i=0; i<this.lineList.length; i++){
				if( this.lineList[i] && !this.lineList[i].saveValidation())flag = false;
			}
			return flag;
		},
		validation: function(routeName, opinion){
			// if (this.isEdit){
			// 	if (!this.editValidation()){
			// 		return false;
			// 	}
			// }
			if (!this.validationConfig(routeName, opinion))  return false;

			if (!this.json.validation) return true;
			if (!this.json.validation.code) return true;

			this.currentRouteName = routeName;
			var flag = this.form.Macro.exec(this.json.validation.code, this);
			this.currentRouteName = "";

			if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
			if (flag.toString()!=="true"){
				this.notValidationMode(flag);
				return false;
			}
			return true;
		},
		getAttachmentRandomSite: function(){
			var i = (new Date()).getTime();
			return this.json.id+i;
		}
	});


MWF.xApplication.process.Xform.Datatemplate.SectionLine =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: false,
		isEdited: true, //是否正在编辑
		isDeleteable: true, //能否被删除
		isAddable: true, //能否添加
		isMergeRead: false, //合并阅读
		index: 0,
		indexText: "0"
	},
	initialize: function (node, template, data, options) {
		this.setOptions(options);
		this.node = node;
		this.template = template;
		this.data = data;
		this.form = this.template.form;
		this.lineList = [];
		this.sectionKey = this.data.sectionKey;
	},
	load: function () {
		if( this.template.isShowSectionKey() || this.template.isShowSectionBy() ){
			this.loadSectionKeyNode();
		}

		if( this.data.data ){
			( this.data.data || [] ).each(function(d, idx){
				if( !d )return;
				var div = new Element("div").inject(this.node);
				var isEdited = false, isNew = false;
				if( this.options.isEdited ){
					var dt = this.template;
					isNew = dt.isNew || (o2.typeOf(dt.newLineIndex) === "number" ? idx === dt.newLineIndex : false);
					isEdited = true;
					dt.isNew = false;
					dt.newLineIndex = null;
				}
				var line = this._loadLine( div, d, idx, isEdited, isNew );
				this.lineList.push(line);
				this.template.lineList.push(line);
			}.bind(this));
		}
	},
	resetId: function(){
		this.lineList.each(function (line) {
			line.resetId();
		}.bind(this))
	},
	setIndex: function( data, index, isEdited, isNew, operation ){
		if( this.isUnchangedAll && index === this.options.index )return;

		this.data = data;

		this.options.index = index;
		this.options.indexText = (index+1).toString();

		//this.node.inject(preNode, "after");

		this.lineList = [];
		var map = this.unchangedLineMap || {};
		Object.each(map, function (line, idx) {
			line.setIndex( this.template.lineList.length + idx.toInt(), idx.toInt() );
		}.bind(this));
		if( this.data &&  this.data.data ){
			this.data.data.each(function (d, idx) {
				if( !d )return;
				var idxStr = idx.toString();
				var beforeNode = idx > 0 ? this.lineList[idx - 1].node : null;
				if( map[idxStr] ){
					if( !operation || operation === "moveUpList" ){
						this._injectLineNode( map[idxStr].node, beforeNode )
					}
					//console.log("inject", map[idxStr]);
					this.lineList.push( map[idxStr] );
					this.template.lineList.push(map[idxStr]);
				}else{
					var node = this._injectLineNode( new Element("div"), beforeNode );
					var isEdited = false, isNew = false;
					if( this.options.isEdited ){
						var dt = this.template;
						isNew = dt.isNew || (o2.typeOf(dt.newLineIndex) === "number" ? idx === dt.newLineIndex : false);
						isEdited = true;
						dt.isNew = false;
						dt.newLineIndex = null;
					}
					var line = this._loadLine( node, d, idx, isEdited, isNew );
					//console.log("_loadLine", line);
					this.lineList.push(line);
					this.template.lineList.push(line);
				}
			}.bind(this))
		}
	},
	_loadLine: function(container, data, index, isEdited, isNew){
		var line = new MWF.xApplication.process.Xform.Datatemplate.Line(container, this.template, data, {
			indexInSectionLine : index,
			indexInSectionLineText : (index+1).toString(),
			index: this.template.lineList.length,
			indexText : (this.template.lineList.length + 1).toString(),
			isNew: isNew,
			isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.options.isEdited,
			isDeleteable: this.options.isDeleteable,
			isAddable: this.options.isAddable,
			isMergeRead: this.options.isMergeRead,
			sectionKey: this.sectionKey
		}, this);
		this.template.fireEvent("beforeLoadLine", [line]);
		line.load();
		this.template.fireEvent("afterLoadLine", [line]);
		return line;
	},
	_injectLineNode: function( lineNode, beforeNode ){
		if( beforeNode ){
			lineNode.inject( beforeNode, "after");
		}else{
			lineNode.inject( this.node );
		}
		return lineNode;
	},
	loadSectionKeyNode: function () {
		var sectionKeyStyles = this.template._parseStyles(this.template.json.sectionKeyStyles);
		var keyNode = new Element("div.mwf_sectionkey", {
			styles : sectionKeyStyles
		}).inject( this.node );
		this.keyNode = keyNode;
		var separator;
		if( this.template.isShowSectionKey() ){
			separator = this.template.json.keyContentSeparator;
		}else{
			separator = this.template.json.keyContentSeparatorSectionBy;
		}
		this.template.getSectionKeyWithMerge( this.data, function (key) {
			if( o2.typeOf(key) === "string" ){
				keyNode.set("text", key + (separator || ""));
			}else{
				Promise.resolve(key).then(function (k) {
					keyNode.set("text", k + (separator || ""));
				}.bind(this))
			}
		}.bind(this));
	},
	_setUnchangedLineMap: function(data, operation){
		var fromOutside = !operation;
		var dt = this.template;
		var editalbe;
		if( dt.isShowAllSection ){
			editalbe = dt.isShowAllSection && dt.sectionBy && dt.sectionBy === this.sectionKey;
		}
		var lineDataList = this.lineList.map(function (line) {
			if( dt.isShowAllSection ){
				if( line.options.isEdited !== (dt.editable && editalbe) )return "";
				if( line.options.isDeleteable !== (dt.editable && editalbe) )return "";
				if( line.options.isAddable !== (dt.editable && editalbe) )return "";
			}else{
				if( line.options.isEdited !== dt.editable )return "";
				if( line.options.isDeleteable !== dt.editable )return "";
				if( line.options.isAddable !== dt.editable )return "";
			}
			return fromOutside ?  JSON.stringify(line.data) : line.data;
		}.bind(this));

		var dStr, map = {};
		data.each(function (d, idx) {
			if(fromOutside)dStr = JSON.stringify(d);
			for (var i = 0; i < this.lineList.length; i++) {
				var isEqual = fromOutside ? (dStr === lineDataList[i]) : (d === lineDataList[i] );
				if ( isEqual ) {
					map[idx] = this.lineList[i];
					lineDataList[i] = "";
					break;
				}
			}
		}.bind(this));


		this.isUnchangedAll = data.length === this.lineList.length;
		if( this.isUnchangedAll ){
			for( var i=0; i<data.length; i++ ){
				var line = map[i.toString()];
				if( !line || line.options.indexInSectionLine !== i ){
					this.isUnchangedAll = false;
					break;
				}
			}
		}

		this.unchangedLineMap = map;

		return Object.keys(map).length ? this : null;
	},
	clearSubModules: function(){
		if( this.isUnchangedAll )return;

		var map = this.unchangedLineMap || {};
		var hasUnchangedLine = Object.keys(map).length > 0;

		if( !hasUnchangedLine ){
			if( this.keyNode ){
				this.keyNode.destroy();
				this.keyNode = null;
			}
		}

		var lines = [];
		Object.values(map).each(function (d) {
			lines = lines.concat(d);
		});
		for (var i=0; i<this.lineList.length; i++){
			var l = this.lineList[i];
			if(!lines.contains(l)){
				l.clearSubModules();
			}
		}
	}
});

MWF.xApplication.process.Xform.Datatemplate.Line =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: false,
		isEdited : true,
		isAddable: true,
		isDeleteable: true,
		index : 0,
		indexText : "0",
		indexInSectionLine: 0,
		indexInSectionLineText : "0",
		sectionKey: ""
	},
	initialize: function (node, template, data, options, sectionLine) {

		this.setOptions(options);

		this.node = node;
		this.template = template;
		this.data = data;
		this.form = this.template.form;
		this.sectionLine = sectionLine;

		this.modules = [];
		this.all = {};
		this.all_templateId = {};

		this.fields = [];
		this.allField = {};
		this.allField_templateId = {};

		this.addActionList = [];
		this.deleteActionList = [];
		this.sequenceNodeList = [];
		this.selector = null;
		this.importActionList = [];
		this.exportActionList = [];

		this.subDatatemplateModuleList = [];
	},
	resetId: function(){
		this.setIndex();
	},
	setIndex: function(index, indexInSectionLine){
		var hasIndexArg = typeOf(index) !== "null";
		var hasIndexInSectionLineArg = typeOf(indexInSectionLine) !== "null";

		if( hasIndexArg && hasIndexInSectionLineArg){
			if( this.options.index === index && this.options.indexInSectionLine === indexInSectionLine )return;
		}else if( hasIndexArg && !hasIndexInSectionLineArg){
			if( this.options.index === index )return;
		}else if(!hasIndexArg && hasIndexInSectionLineArg){
			if( this.options.indexInSectionLine === indexInSectionLine )return;
		}

		if( hasIndexArg ){
			this.options.index = index;
			this.options.indexText = (index.toInt()+1).toString();
		}

		if( hasIndexInSectionLineArg ){
			this.options.indexInSectionLine = indexInSectionLine;
			this.options.indexInSectionLineText = (indexInSectionLine.toInt()+1).toString();
		}

		//合并状态或拆分状态
		var sectionKey = this.options.sectionKey || this.template.sectionBy;
		this.modules.each(function (module) {
			var json = module.json;
			var id, oldId = json.id, templateJsonId = json.originialId;
			if( this.template.isShowAllSection ){
				id = this.template.json.id + ".." + sectionKey + ".." + this.options.indexInSectionLine + ".." + json.originialId;
			}else if( sectionKey ){
				id = this.template.json.id + ".." + sectionKey + ".." + this.options.index + ".." + json.originialId;
			}else{
				id = this.template.json.id + ".." + this.options.index + ".." + json.originialId;
			}
			json.id = id;
			switch (json.type) {
				case "Select":
					(module.areaNode || module.node).set("id", id);
					break;
				case "Datatable":
				case "Datatemplate":
					module.resetId();
					break;
				default:
					module.node.set("id", id);
					break;
			}

			if( json.type==="Attachment" || json.type==="AttachmentDg" ){
				json.site = this.getAttachmentSite(json, templateJsonId, sectionKey);
			}

			delete this.all[oldId];
			this.all[id] = module;

			delete this.allField[oldId];
			this.allField[id] = module;

			if(this.form.all[oldId]){
				delete this.form.all[oldId];
				this.form.all[id] = module;
			}

			if(this.form.forms[oldId]){
				delete this.form.forms[oldId];
				this.form.forms[id] = module;
			}

			this.checkSequence(module, templateJsonId);
		}.bind(this));
	},
	load: function(){
		this.node.set("html", this.template.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.node);

		//拆分状态
		var sectionKey = this.options.sectionKey || this.template.sectionBy;

		moduleNodes.each(function (node) {
			if (node.get("MWFtype") !== "form") {
				var _self = this;

				var tJson = this.form._getDomjson(node);
				if( tJson ){
					var json = Object.clone(tJson);

					if( !this.options.isEdited )json.isReadonly = true;

					var templateJsonId = json.id;


					var index = this.options.index;
					var id;
					if( this.template.isShowAllSection ){
						id = this.template.json.id + ".." + sectionKey + ".." + this.options.indexInSectionLine + ".." + json.id;
					}else if( sectionKey ){
						id = this.template.json.id + ".." + sectionKey + ".."+ index + ".." + json.id;
					}else{
						id = this.template.json.id + ".." + index + ".." + json.id;
					}

					json.originialId = templateJsonId;
					json.id = id;
					node.set("id", id);

					if( json.type==="Attachment" || json.type==="AttachmentDg" ){
						json.type = "AttachmentDg";
						json.ignoreSite = true;
						json.site = this.getAttachmentSite(json, templateJsonId, sectionKey);
					}

					if (this.form.all[id]) this.form.all[id] = null;
					if (this.form.forms[id])this.form.forms[id] = null;

					var module = this.form._loadModule(json, node, function () {
						if( _self.options.isMergeRead ){
							this.field = false; //不希望保存数据
							this._getBusinessData = function(){
								return _self.data[templateJsonId];
							};
							this._setBusinessData = function () {};
						}
						if( _self.widget )this.widget = _self.widget;
						this.parentLine = _self;
						this.parentDatatemplate = _self.template;

						//只读方法值在页面加载的时候或者new的时候计算一下
						if( this.json.compute === "show" ){
							var needComputeShow = false;
							if( _self.template.loading ) {
								needComputeShow = true;
							}else if( _self.options.isNew && !_self.reloading ){
								needComputeShow = true;
							}
							if( !needComputeShow ){
								this.json.compute = "create"; //
								if( this.options.moduleEvents && this.options.moduleEvents.length ){ //恢复compute
									var eventName = ( this.options.moduleEvents || [] ).contains("afterLoad") ? "afterLoad" : "load";
									var resetCompute = function () {
										this.json.compute = "show";
										this.removeEvent( eventName, resetCompute );
									}.bind(this)
									this.addEvent(eventName, resetCompute);
								}
							}
						}
					});
					if(!module.parentLine)module.parentLine = this;
					if(!module.parentDatatemplate)module.parentDatatemplate = this.template;

					if( json.type==="Attachment" || json.type==="AttachmentDg" ){
						module.addEvent("change", function(){
							_self.form.saveFormData();
						}.bind(this))
					}else if( json.type==="Datatemplate" ){
						this.subDatatemplateModuleList.push(module);
					}

					this.form.modules.push(module);

					this.modules.push(module);
					this.all[id] = module;
					this.all_templateId[templateJsonId] = module;

					if (module.field) {
						// if(this.data.hasOwnProperty(templateJsonId)){
						// 	module.setData(this.data[templateJsonId]);
						// }
						this.allField[id] = module;
						this.allField_templateId[templateJsonId] = module;
						this.fields.push( module );

						module.addEvent("change", function(){
							this.template.fireEvent("change", [{lines: [this], type: "editmodule", module: module}]);
						}.bind(this))
					}

					this.setEvents(module, templateJsonId);
					this.checkSequence(module, templateJsonId);
				}
			}
		}.bind(this));

		if(this.options.isNew){
			this.data = this.getData();
			this.options.isNew = false;
		}
	},
	setSubDatatemplateOuterActionEvents: function(){
		this.subDatatemplateModuleList.each(function(module){
			//绑定下级模板事件
			module._setOuterActionEvents();
			//让下级数据模板再去绑定下级模板外部事件
			module._setSubDatatemplateOuterEvents();
		})
	},
	getIndex: function(){
		return this.options.index;
	},
	getModule: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	get: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	getAttachmentSite: function(json, templateJsonId, sectionKey){
		//确保site最长为64，否则后台会报错

		var index = this.options.index;

		var baseSite;
		baseSite =  "." + index + "."  + (json.site || templateJsonId);

		var maxLength;
		var sectionId = "";
		if( sectionKey ){
			maxLength = Math.floor((63 - baseSite.length)/2 );

			sectionId = (sectionKey.length > maxLength) ? sectionKey.substr(sectionKey.length-maxLength, maxLength) : sectionKey;
			sectionId = "." + sectionId;
		}else{
			maxLength = 64 - baseSite.length;
		}

		var templateId = this.template.json.id;
		templateId = (templateId.length > maxLength) ? templateId.substr(templateId.length-maxLength, maxLength) : templateId;

		return templateId + sectionId + baseSite;
	},
	setEvents: function (module, id) {
		if( this.template.addActionIdList.contains( id )){
			this.addActionList.push( module );
			module.node.addEvent("click", function (ev) {
				this.template._insertLine( ev, this )
			}.bind(this))
			if( !this.template.editable )module.node.hide();
			if( !this.options.isAddable )module.node.hide();
		}

		if( this.template.deleteActionIdList.contains(id)){
			this.deleteActionList.push( module );
			module.node.addEvent("click", function (ev) {
				this.template._deleteLine( ev, this )
			}.bind(this))
			if( !this.template.editable )module.node.hide();
			if( !this.options.isDeleteable )module.node.hide();
		}

		if( this.template.selectorId === id){
			this.selector = module;
			// module.setData(""); //默认不选择
			module.node.addEvent("click", function (ev) {
				this.checkSelect();
			}.bind(this))
			if( !this.template.editable )module.node.hide();
			if( !this.options.isDeleteable )module.node.hide();
			this.unselect();
		}

		//???
		// if( this.template.importActionIdList.contains(id))this.importActionList.push( module );
		// if( this.template.exportActionIdList.contains(id))this.exportActionList.push( module );

	},
	checkSequence: function(module, id){
		if( this.template.sequenceIdList.contains(id)){
			this.sequenceNodeList.push( module );
			var indexText;
			if(
				( this.template.isShowSectionKey() && this.template.json.sequenceBySection === "section" ) ||
				( this.template.isShowSectionBy() && this.template.json.sequenceBy === "section" )
			){
				indexText = this.options.indexInSectionLineText;
			}else{
				indexText = this.options.indexText;
			}
			if(this.form.getModuleType(module) === "label"){
				if (module.json.valueType === "script" && module.json.script && module.json.script.code){
					var value = module.form.Macro.exec(module.json.script.code, module);
					module._setNodeText(value);
				}else{
					module.node.set("text", indexText );
				}
			}else if(module.setData){
				module.setData( indexText );
			}
		}
	},
	checkSelect: function () {
		var selectData = this.selector.getData();
		var selected;
		if(o2.typeOf(selectData)==="array"){
			selected = selectData.contains(this.template.json.selectorSelectedValue);
		}else{
			selected = selectData === this.template.json.selectorSelectedValue;
		}
		this.selected = selected;
	},
	select: function(){
		this.selected = true;
		if(this.selector)this.selector.setData(this.template.json.selectorSelectedValue);
	},
	unselect: function(){
		this.selected = false;
		if( this.selector.getOptionsObj ){
			var options = this.selector.getOptionsObj();
			var value = "";
			var arr = options.valueList || [];
			for( var i=0; i<arr.length; i++ ){
				var v = arr[i];
				if( v !== this.template.json.selectorSelectedValue ){
					value = v;
					break;
				}
			}
			this.selector.setData(value);
		}else{
			this.selector.setData("")
		}
	},
	reload: function(){
		this.reloading = true;
		for(var key in this.all){
			var module = this.all[key];
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.empty();
		this.load();
		this.reloading = false;
	},
	clearSubModules: function () { //把module清除掉
		for(var key in this.all){
			var module = this.all[key];
			//如果嵌套数据模板或者数据表格，还要清除掉下级
			if(module.clearSubModules)module.clearSubModules();
			if( module.json && (module.json.type==="TinyMCEEditor" || module.json.type==="Htmleditor"))module.destroy();
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.destroy();
	},
	computeModuleData: function( when ){
		for( var key in this.allField){
			var module = this.allField[key];
			var id = key.split("..").getLast();
			if( module.json.compute === when ){
				this.data[id] = module.getData();
			}
		}
	},
	getData: function () {
		var data = this.data;
		for( var key in this.allField){
			var module = this.allField[key];
			var id = key.split("..").getLast();
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ) {
				data[id] = module._getBusinessData();
			}else if( module.json.compute === "save" && module.getInputData ){
				data[id] = module.getInputData();
			}else{
				data[id] = module.getData();
			}
		}
		return data;
	},
	setData: function (data) {
		this.template._setLineData(this, data);
	},
	deleteAttachment: function(){
		var saveFlag = false;
		for( var key in this.allField){
			var module = this.allField[key];
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				var array = module._getBusinessData();
				(array || []).each(function(d){
					saveFlag = true;
					this.form.workAction.deleteAttachment(d.id, this.form.businessData.work.id);
				}.bind(this))
			}
		}
		return saveFlag;
	},
	saveValidation: function(){
		if( !this.options.isEdited )return true;
		var flag = true;
		this.fields.each(function(field, key){
			if (field.json.type!="sequence" && field.validationMode ){
				field.validationMode();
				if (!field.saveValidation()) flag = false;
			}
		}.bind(this));
		return flag;
	}
});

MWF.xApplication.process.Xform.Datatemplate.ImporterDatatemplate = new Class({
	Extends: MWF.xApplication.process.Xform.Datatemplate,
	initialize: function(mainDatatable){
		this.mainDatatable = mainDatatable;
		this.json = Object.clone( mainDatatable.json );
		this.json.id = this.json.id + "_o2simula";

		this.node = new Element("div", {
			id: this.json.id
		});
		this.node.store("module", this);

		this.form = mainDatatable.form;
		this.field = false;
		this.fieldModuleLoaded = false;
	},
	load: function(){

		this.deleteFormData = function(data){
			delete data[this.id];
			this._self.form.removeEvent("getData", this._self.deleteFormData);
		}.bind({_self: this, id: this.json.id});
		this.form.addEvent("getData", this.deleteFormData);

		this._loadModuleEvents();
		if (this.fireEvent("queryLoad")){
			this._queryLoaded();
			this._loadUserInterface();
			this._afterLoaded();
			this.fireEvent("afterLoad");
		}
	},
	_loadUserInterface: function(){

		this.editable = true;

		//是否多行同时编辑
		this.multiEditMode = true;

		this.data = [];
		this._setValue(this.data);

		this.lineList = [];

		this.loadDatatemplate();
	},
	loadDatatemplate: function(){
		this.loading = true;

		this.templateJson = this.mainDatatable.templateJson;
		this.templateHtml = this.mainDatatable.templateHtml;

		this.fireEvent("load");
		this._loadDataTemplate(function(){
			this.fieldModuleLoaded = true;
			this.loading = false;
			this.fireEvent("postLoad");
		}.bind(this));
	},
	_loadDataTemplate: function(callback){
		this._loadLineList(function(){
			if(callback)callback();
		}.bind(this));
	},
	_loadLine: function(container, data, index, isNew){
		var line = new MWF.xApplication.process.Xform.Datatemplate.ImporterLine(container, this, data, {
			index : index,
			indexText : (index+1).toString()
		});
		this.fireEvent("beforeLoadLine", [line]);
		line.load();
		this.fireEvent("afterLoadLine", [line]);
		return line;
	},
	_addLine: function(ev, d){

		var data, index, line;

		index = this.lineList.length;
		data = this.getInputData();

		data.push(d||{});
		this.newLineIndex = index;

		this.setData( data );
		line = this.getLine(index);
		line.isNewAdd = true;

		this.fireEvent("addLine", [{"line":line, "ev":ev}]);

		this.fireEvent("change", [{"lines":[line], "type":"addline"}]);

		return line;
	},
	_setSubDatatemplateOuterEvents: function(){
	},
	destroy: function(){
		this.clearSubModules();
		var id = this.json.id;
		if (this.form.all[id]) delete this.form.all[id];
		if (this.form.forms[id])delete this.form.forms[id];
		// if( this.form.businessData.data.hasOwnProperty(id) )delete this.form.businessData.data[id];
		// if( this.form.Macro.environment.data.hasOwnProperty(id) )delete this.form.Macro.environment.data[id];
		this.node.destroy();
	}
});

//Excel导入时候创建的组件
MWF.xApplication.process.Xform.Datatemplate.ImporterLine =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: true,
		isEdited : true, //是否正在编辑
		isEditable : true, //能否被编辑
		isDeleteable: true, //能否被删除
	},
	initialize: function (node, template, data, options) {

		this.setOptions(options);

		this.node = node;
		this.template = template;
		this.data = data;
		this.form = this.template.form;

		this.init()
	},
	init: function(){
		this.modules = [];
		this.all = {};
		this.all_templateId = {};

		this.fields = [];
		this.allField = {};
		this.allField_templateId = {};
	},
	load: function(){
		this.loadModules();
	},
	loadModules: function(){
		this.node.set("html", this.template.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.node);

		//合并状态或拆分状态
		// var sectionKey = this.options.sectionKey || this.datatable.sectionBy;
		moduleNodes.each(function (node) {
			var mwfType = node.get("MWFtype");
			if (mwfType === "form")return;

			var _self = this;

			var tJson = this.form._getDomjson(node);
			if( tJson ){
				var json = Object.clone(tJson);

				if( !this.options.isEdited )json.isReadonly = true;

				var templateJsonId = json.id;

				var index = "0"; //this.options.index;

				var id = this.template.json.id + "..data.." + index + ".." + json.id;

				json.id = id;
				node.set("id", id);

				if (this.form.all[id]) this.form.all[id] = null;
				if (this.form.forms[id])this.form.forms[id] = null;

				var module = this.form._loadModule(json, node, function () {
					if( _self.widget )this.widget = _self.widget;
					this.parentLine = _self;
					this.parentDatatemplate = _self.template;

					//只读方法值在页面加载的时候或者new的时候计算一下
					if( this.json.compute === "show" ){
						var needComputeShow = false;
						if( _self.template.loading ) {
							needComputeShow = true;
						}else if( _self.options.isNew && !_self.reloading ){
							needComputeShow = true;
						}
						if( !needComputeShow ){
							this.json.compute = "create"; //
							if( this.options.moduleEvents && this.options.moduleEvents.length ){ //恢复compute
								var eventName = ( this.options.moduleEvents || [] ).contains("afterLoad") ? "afterLoad" : "load";
								var resetCompute = function () {
									this.json.compute = "show";
									this.removeEvent( eventName, resetCompute );
								}.bind(this)
								this.addEvent(eventName, resetCompute);
							}
						}
					}
				});
				if(!module.parentLine)module.parentLine = this;
				if(!module.parentDatatemplate)module.parentDatatemplate = this.template;

				this.form.modules.push(module);

				this.modules.push(module);
				this.all[id] = module;
				this.all_templateId[templateJsonId] = module;

				if( module.field && this.options.isEdited ) {
					this.allField[id] = module;
					this.allField_templateId[templateJsonId] = module;
					this.fields.push( module );
				}
			}
		}.bind(this));

	},
	clearSubModules: function () { //把module清除掉
		for(var key in this.all){
			var module = this.all[key];
			//如果嵌套数据模板或者数据表格，还要清除掉下级
			if(module.clearSubModules)module.clearSubModules();
			if( module.json && (module.json.type==="TinyMCEEditor" || module.json.type==="Htmleditor"))module.destroy();
			this.form.modules.erase(module);
			// if( this.form.businessData.data.hasOwnProperty(key) )delete this.form.businessData.data[key];
			// if( this.form.Macro.environment.data.hasOwnProperty(key) )delete this.form.Macro.environment.data[key];
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.destroy();
		this.init();
	},
	getModule: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	get: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	getData: function () {
		var data = this.data;
		for( var key in this.allField_templateId){
			var module = this.allField_templateId[key];
			// var id = key.split("..").getLast();
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				data[key] = module._getBusinessData();
			}else{
				data[key] = module.getData();
			}
		}
		return data;
	}
});

MWF.xApplication.process.Xform.Datatemplate.Exporter = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function (template, options) {

		this.setOptions(options);

		this.template = template;
		this.form = this.template.form;

	},
	exportToExcel : function () {
		MWF.require("MWF.widget.Mask", null, false);
		this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
		// 适配移动端
		if (layout.mobile) {
			this.mask.load();
		} else {
			this.mask.loadNode(this.form.app.content);
		}

		this.template.fireEvent("beforeExport");

		var resultArr = [];
		var titleArr = this.template.json.excelFieldConfig.map(function(config){
			return config.title;
		});
		resultArr.push( titleArr );

		var lineList = [];

		if( this.template.isShowAllSection ){
			lineList = this.template.sectionLineEdited ? this.template.sectionLineEdited.lineList : this.template.lineList;
		}else if( this.template.isMergeRead ) {
			lineList = this.template.lineList; 
		}else{
			lineList = this.template.lineList;
		}

		lineList.each(function (line, index) {
			var lineData = this.getLineExportData(line, index);
			var p = Promise.all(lineData).then(function (arr) {
				return arr;
			});
			resultArr.push( p );
		}.bind(this));

		Promise.all(resultArr).then(function ( rstArr ) {
			var colWidthArr = this.getColWidthArray();
			var excelName = this.getExcelName();

			var arg = {
				data : rstArr,
				colWidthArray : colWidthArr,
				title : excelName
			};
			this.template.fireEvent("export", [arg]);

			new MWF.xApplication.process.Xform.Datatemplate.ExcelUtils( this.template ).exportToExcel(
				arg.data || rstArr,
				arg.title || excelName,
				arg.colWidthArray || colWidthArr,
				this.getDateIndexArray(),  //日期格式列下标
				null,
				function () {
					if (this.mask) {
						this.mask.hide();
						this.mask = null;
					}
				}.bind(this)
			);
		}.bind(this))
	},
	getLineExportData: function(line, index ){
		var exportData = [];
		this.template.json.excelFieldConfig.each(function (config) {

			var module = line.all_templateId[config.field];
			var json = module ? module.json : "";

			if ( !module || !json || !this.isAvaliableField( json ) ) {
				exportData.push("");
			}else{
				var text;
				if ( json.type === "Label" && module.node) {
					text = module.node.get("text");
				}else{
					text = module.getExcelData();
				}

				if( !text && o2.typeOf(text) !== "number" ){
					text = "";
				}

				exportData.push( text );
			}
		}.bind(this));
		return exportData;
	},
	// getLineExportData: function(line, index ){
	// 	var exportData = [];
	// 	this.template.json.excelFieldConfig.each(function (config) {
	//
	// 		var module = line.all_templateId[config.field];
	// 		var json = module ? module.json : "";
	//
	// 		if ( !module || !json || !this.isAvaliableField( json ) ) {
	// 			exportData.push("");
	// 		}else{
	// 			var value = module.getData();
	// 			var text = "";
	//
	//
	// 			if( value ){
	// 				switch (module.json.type) {
	// 					case "Org":
	// 					case "Reader":
	// 					case "Author":
	// 					case "Personfield":
	// 					case "Orgfield":
	// 						if (o2.typeOf(value) === "array") {
	// 							var textArray = [];
	// 							value.each(function (item) {
	// 								if (o2.typeOf(item) === "object") {
	// 									textArray.push(item.distinguishedName);
	// 								} else {
	// 									textArray.push(item);
	// 								}
	// 							}.bind(this));
	// 							text = textArray.join(", \n");
	// 						} else if (o2.typeOf(value) === "object") {
	// 							text = value.distinguishedName;
	// 						} else {
	// 							text = value;
	// 						}
	// 						break;
	// 					case "Combox":
	// 					case "Address":
	// 						text = o2.typeOf(value) === "array" ? value.join(", ") : value;
	// 						break;
	// 					case "Checkbox":
	// 						var options = module.getOptionsObj();
	// 						var value = o2.typeOf(value) === "array" ? value : [value];
	// 						var arr = [];
	// 						value.each( function( a, i ){
	// 							var idx = options.valueList.indexOf( a );
	// 							arr.push( idx > -1 ? options.textList[ idx ] : "") ;
	// 						});
	// 						text = arr.join(", ");
	// 						break;
	// 					case "Radio":
	// 					case "Select":
	// 						var options = module.getOptionsObj();
	// 						var idx = options.textList.indexOf( value );
	// 						text = idx > -1 ? options.valueList[ idx ] : "";
	// 						break;
	// 					case "Textarea":
	// 						text = value;
	// 						break;
	// 					case "Calendar":
	// 						text = value;
	// 						break;
	// 					default:
	// 						text = value;
	// 						break;
	// 				}
	// 			} else if ( json.type === "Label" && module.node) {
	// 				text = module.node.get("text");
	// 			}
	//
	// 			if( !text && o2.typeOf(text) !== "number" ){
	// 				text = "";
	// 			}
	//
	// 			exportData.push( text );
	// 		}
	// 	}.bind(this));
	// 	return exportData;
	// },
	isAvaliableField : function(json){
		if (["Image","Button","ImageClipper","WritingBoard","Attachment","AttachmentDg","Label",
			"Elbutton","Elbutton","Elcarousel","Eldropdown","Elicon","Eltree"].contains( json.type) )return false; //图片，附件,Label不导入导出
		return true;
	},
	getExcelName: function(){
		var title;
		if( this.template.json.excelName && this.template.json.excelName.code ){
			title = this.form.Macro.exec(this.template.json.excelName.code, this);
		}else{
			title = MWF.xApplication.process.Xform.LP.datatemplateExportDefaultName;
		}
		var titleA = title.split(".");
		if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
			titleA.splice( titleA.length-1 );
		}
		title = titleA.join(".");
		return title;
	},
	getColWidthArray : function(){
		var colWidthArr = [];
		this.template.json.excelFieldConfig.each(function (config) {
			var json = this.form.json.moduleList[config.field];
			if ( !json ){
				colWidthArr.push(150);
			}else if ( ["Org","Reader","Author","Personfield","Orgfield"].contains(json.type)) {
				colWidthArr.push(340);
			} else if (json.type === "Address") {
				colWidthArr.push(170);
			} else if (json.type === "Textarea") {
				colWidthArr.push(260);
			} else if (json.type === "Htmleditor") {
				colWidthArr.push(500);
			} else if (json.type === "TinyMCEEditor") {
				colWidthArr.push(500);
			} else if (json.type === "Calendar") {
				colWidthArr.push(150);
			} else {
				colWidthArr.push(150);
			}
		}.bind(this));
		return colWidthArr;
	},
	getDateIndexArray : function(){
		var dateIndexArr = []; //日期格式列下标
		this.template.json.excelFieldConfig.each(function (config, i) {
			var json = this.form.json.moduleList[config.field];
			if (json && json.type === "Calendar") {
				dateIndexArr.push(i);
			}
		}.bind(this));
		return dateIndexArr;
	},

	exportWithImportDataToExcel : function ( columnList, importedData ) {
		MWF.require("MWF.widget.Mask", null, false);
		this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
		// 适配移动端
		if (layout.mobile) {
			this.mask.load();
		} else {
			this.mask.loadNode(this.form.app.content);
		}

		this.template.fireEvent("beforeExport");

		var resultArr = [];
		var titleArr = this.template.json.excelFieldConfig.map(function(config){
			return config.title;
		});
		titleArr.push( MWF.xApplication.process.Xform.LP.validationInfor );
		resultArr.push( titleArr );


		// this.template.lineList.each(function (line, index) {
		// 	resultArr.push( this.getLineExportData(line, index) );
		// }.bind(this));

		importedData.each( function( lineData, lineIndex ){
			var array = [];
			columnList.each( function (obj, i) {
				array.push( ( lineData[ obj.text ] || '' ).replace(/&#10;/g, "\n") );
			});
			array.push( lineData.errorTextListExcel ? lineData.errorTextListExcel.join("\n") : ""  );

			resultArr.push( array );
		}.bind(this));

		var colWidthArr = this.getColWidthArray();
		colWidthArr.push(300); //提示信息

		var excelName = this.getExcelName();

		var arg = {
			data : resultArr,
			colWidthArray : colWidthArr,
			title : excelName,
			withError: true
		};
		this.template.fireEvent("export", [arg]);

		new MWF.xApplication.process.Xform.Datatemplate.ExcelUtils( this.template ).exportToExcel(
			arg.data || resultArr,
			arg.title || excelName,
			arg.colWidthArray || colWidthArr,
			this.getDateIndexArray(),  //日期格式列下标
			null,
			function () {
				if (this.mask) {
					this.mask.hide();
					this.mask = null;
				}
			}.bind(this)
		);
	}
});

MWF.xApplication.process.Xform.Datatemplate.Importer = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function (template, options) {

		this.setOptions(options);

		this.template = template;
		this.form = this.template.form;

		this.lp = MWF.xApplication.process.Xform.LP;
		this.columnText =  this.lp.importValidationColumnText;
		this.columnTextExcel = this.lp.importValidationColumnTextExcel;
		this.excelUtil = new MWF.xApplication.process.Xform.Datatemplate.ExcelUtils( this.template );

	},
	isAvaliableField : function(json, module, type){
		if (["Image","Button","ImageClipper","WritingBoard","Attachment","AttachmentDg","Label",
			"Elbutton","Elcarousel","Eldropdown","Elicon","Eltree"].contains( json.type) )return false; //图片，附件,Label不导入导出
		return true;
	},
	importFromExcel : function () {
		this.template.fireEvent("beforeImport");

		this.getFieldArray();
		var dateColArray = this.getDateIndexArray(); //日期列
		var orgTitleArray = this.getOrgTitleArray();

		this.excelUtil.upload( dateColArray, function (data) {

			MWF.require("MWF.widget.Mask", null, false);
			this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
			// 适配移动端
			if (layout.mobile) {
				this.mask.load();
			} else {
				this.mask.loadNode(this.form.app.content);
			}

			this.importedData = data;

			if( !this.checkCount() )return;

			this.loadSimulateModule();

			this.fieldArray.each(function (c) {
				c.module = this.importerLine.getModule(c.field)
			}.bind(this));

			if( orgTitleArray.length > 0 ){
				this.listAllOrgData( orgTitleArray, function () {
					this.checkAndImport();
				}.bind(this));
			}else{
				this.checkAndImport();
			}


		}.bind(this));
	},
	checkAndImport: function(){
		this.checkData( function (flag) {

			if( !flag ){
				this.openErrorDlg();
			}else{
				this.importData()
			}
			this.destroySimulateModule();

		}.bind(this));
	},
	destroySimulateModule: function(){
		if( !this.importerDatatemplate ){
			this.form.disallowSaving = false;
			return;
		}

		this.importerDatatemplate.destroy();
		this.importerDatatemplate = null;
		this.form.disallowSaving = false;
	},
	loadSimulateModule: function(){
		this.form.disallowSaving = true;
		this.importerDatatemplate = new MWF.xApplication.process.Xform.Datatemplate.ImporterDatatemplate( this.template );
		this.importerDatatemplate.load();

		this.importerLine = this.importerDatatemplate.addLine({});
	},
	getFieldArray: function(){
		//this.loadSimulateModule();
		this.fieldArray = []; //日期格式列下标
		this.template.json.excelFieldConfig.each(function (config, i) {
			this.fieldArray.push({
				"text": config.title,
				"field": config.field,
				"index": i,
				// "module": this.simelateModuleMap[config.field],
				"json": this.form.json.moduleList[config.field]
			})
		}.bind(this));
	},
	getDateIndexArray : function(){
		var dateIndexArr = []; //日期格式列下标
		this.template.json.excelFieldConfig.each(function (config, i) {
			var json = this.form.json.moduleList[config.field];
			if (json && json.type === "Calendar" && (c.mJson.format === "%Y-%m-%d" || c.mJson.format === "%Y-%m-%d %H:%M:%S")) {
				dateIndexArr.push(i);
			}
		}.bind(this));
		return dateIndexArr;
	},
	getOrgTitleArray : function(){
		var orgTitleArr = []; //日期格式列下标
		this.template.json.excelFieldConfig.each(function (config, i) {
			var json = this.form.json.moduleList[config.field];
			if (json && ["Org","Reader","Author","Personfield","Orgfield"].contains(json.type) ) {
				orgTitleArr.push(config.title);
			}
		}.bind(this));
		return orgTitleArr;
	},
	parseImportedData: function(){
		var data = [];

		this.importedData.each( function( ilineData ){
			var lineData = {};

			this.fieldArray.each( function (obj, i) {
				var index = obj.index;
				var module = obj.module;
				var json = obj.json;
				var text = obj.text;

				var d = ilineData[text] || "";

				var value;
				if( d === "" || d === undefined || d === null ){
					value = "";
				}else{
					switch (json.type) {
						case "Org":
						case "Reader":
						case "Author":
						case "Personfield":
						case "Orgfield":
							var arr = this.stringToArray(d); //空格,空格
							if( arr.length === 0 ){
								value = this.getOrgData( d );
							}else{
								value = [];
								arr.each( function(d, idx){
									var obj = this.getOrgData( d );
									value.push( obj );
								}.bind(this));
							}
							break;
						default:
							value = d; //d.replace(/&#10;/g,""); //换行符&#10;
							break;
					}
				}

				lineData[ json.id ] = value;

			}.bind(this));

			data.push( lineData );

		}.bind(this));

		return data;
	},
	stringToArray: function(string){
		return string.replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
			return !!s;
		});
	},
	importData: function(){
		var data = this.parsedData;

		this.template.fireEvent("import", [data] );

		this.template.setData( data );

		this.template.fireEvent("afterImport", [data] );

		this.template.fireEvent("change", [{lines: this.template.lineList, type : "import"}]);

		if (this.mask) {
			this.mask.hide();
			this.mask = null;
		}

		this.form.notice( MWF.xApplication.process.Xform.LP.importSuccess );

	},
	openErrorDlg : function(){
		var eData = this.importedData;
		var _self = this;

		var objectToString = function (obj, type) {
			if(!obj)return "";
			var arr = [];
			Object.each(obj,  function (value, key) {
				if( type === "style" ){
					arr.push( key + ":"+ value +";" )
				}else{
					arr.push( key + "='"+ value +"'" )
				}
			});
			return arr.join(" ")
		}

		var htmlArray = ["<table "+ objectToString( this.template.json.impExpTableProperties ) +" style='"+objectToString( this.template.json.impExpTableStyles, "style" )+"'>"];

		var titleStyle = objectToString(this.template.json.impExpTableTitleStyles, "style");
		htmlArray.push("<tr>");
		this.fieldArray.each(function (obj, i) {
			htmlArray.push( "<th style='"+titleStyle+"'>"+obj.text+"</th>" );
		});
		htmlArray.push("<th style='"+titleStyle+"'> "+MWF.xApplication.process.Xform.LP.validationInfor +"</th>");
		htmlArray.push("</tr>" );

		var contentStyles = Object.clone( this.template.json.impExpTableContentStyles );
		if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
		var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

		eData.each( function( lineData, lineIndex ){

			htmlArray.push( "<tr>" );
			this.fieldArray.each( function (obj, i) {
				htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ obj.text ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //换行符&#10;
			});
			htmlArray.push( "<td style='"+contentStyle+"'>"+( lineData.errorTextList ? lineData.errorTextList.join("<br/>") : "" )+"</td>" );
			htmlArray.push( "</tr>" );

		}.bind(this));
		htmlArray.push( "</table>" );

		var width = this.template.json.impExpDlgWidth || 1000;
		var height = this.template.json.impExpDlgHeight || 700;
		width = width.toInt();
		height = height.toInt();

		var div = new Element("div", { style : "padding:10px;", html : htmlArray.join("") });
		var dlg = o2.DL.open({
			"style" : this.form.json.dialogStyle || "user",
			"title": MWF.xApplication.process.Xform.LP.importFail,
			"content": div,
			"offset": {"y": 0},
			"isMax": true,
			"width": width,
			"height": height,
			"buttonList": [
				{
					"type": "exportWithError",
					"text": MWF.xApplication.process.Xform.LP.datagridExport,
					"action": function () { _self.exportWithImportDataToExcel(eData); }
				},
				{
					"type": "cancel",
					"text": MWF.LP.process.button.cancel,
					"action": function () { dlg.close(); }
				}
			],
			"onPostClose": function(){
				dlg = null;
			}.bind(this)
		});

		if (this.mask) {
			this.mask.hide();
			this.mask = null;
		}
	},
	checkCount: function(){
		var idata = this.importedData;

		var lp = MWF.xApplication.process.Xform.LP;

		var exceeded = false;
		var maxCount = this.template.json.maxCount ? this.template.json.maxCount.toInt() : 0;
		if( maxCount > 0 && idata.length > maxCount )exceeded = true;

		var less = false;
		var minCount = this.template.json.minCount ? this.template.json.minCount.toInt() : 0;
		if( minCount > 0 && idata.length < minCount) less = true;

		if( exceeded ) {
			var text = lp.importTooManyNotice.replace("{n1}", idata.length).replace("{n2}", this.template.json.maxCount);
			this.form.notice(text, "error");
			return false;
		}

		if( less ){
			var text = lp.importTooFewNotice.replace("{n1}", idata.length).replace("{n2}", this.template.json.minCount );
			this.form.notice(text,"error");
			return false;
		}
		return true;
	},
	checkData : function( callback){
		this.parsedData = this.parseImportedData();
		this.isImportSuccess = true;

		this.checkLineData(0, function () {
			var arg = {
				validted : this.isImportSuccess,
				data : this.importedData
			};
			this.template.fireEvent( "validImport", [arg] );
			callback( arg.validted )
		}.bind(this));
	},
	checkLineData: function(lineIndex, callback){
		if( lineIndex < this.importedData.length ){
			this._checkLineData(this.importedData[lineIndex], lineIndex, function (flag) {
				lineIndex++;
				if( !flag )this.isImportSuccess = false;
				this.checkLineData(lineIndex, callback);
			}.bind(this));
		}else{
			if(callback)callback();
		}
	},
	_checkLineData: function(lineData, lineIndex, callback){
		lineData.errorTextList = lineData.errorTextList || [];
		lineData.errorTextListExcel = lineData.errorTextListExcel || [];

		var parsedLineData = (this.parsedData && this.parsedData[lineIndex]) ? this.parsedData[lineIndex] : [];

		this.checkModuleData(0, lineData, parsedLineData, function () {
			var flag = !lineData.errorTextList.length;
			callback(flag);
		});
	},
	checkModuleData: function(index, lineData, parsedLineData, callback){
		if( index < this.fieldArray.length ){
			var result = this._checkModuleData(this.fieldArray[index], lineData, parsedLineData);
			Promise.resolve(result).then(function (flag) {
				index++;
				this.checkModuleData(index, lineData, parsedLineData, callback);
			}.bind(this))
		}else{
			if(callback)callback();
		}
	},
	_checkModuleData: function (field, lineData, parsedLineData) {
		var index = field.index;
		var json = field.json;
		var module = field.module;
		var text = field.text;

		var colInfor = this.columnText.replace( "{n}", index+1 );
		var colInforExcel = this.columnTextExcel.replace( "{n}", this.excelUtil.index2ColName( index ) );

		var d = lineData[text] || "";
		var parsedD = parsedLineData[json.id] || "";
		var lp = this.lp;
		var flag = true;

		if(d){
			switch (json && json.type) {
				case "Org":
				case "Reader":
				case "Author":
				case "Personfield":
				case "Orgfield":
					var arr = this.stringToArray(d);
					arr.each( function(d, idx){
						var obj = this.getOrgData( d );
						if( obj.errorText ){
							lineData.errorTextList.push( colInfor + obj.errorText + lp.fullstop );
							lineData.errorTextListExcel.push( colInforExcel + obj.errorText + lp.fullstop );
							flag = false;
						}
					}.bind(this));
					break;
				case "Number":
				case "Currency":
				case "Elnumber":
					if (isNaN(d)){
						lineData.errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
						lineData.errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
						flag = false;
					}
					break;
				case "Calendar":
				case "Eldate":
				case "Eldatetime":
					if( json.format === "%Y-%m-%d" || json.format === "%Y-%m-%d %H:%M:%S" ) {
						if (!(isNaN(d) && !isNaN(Date.parse(d)))) {
							lineData.errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop);
							lineData.errorTextListExcel.push(colInforExcel + d + lp.notValidDate + lp.fullstop);
							flag = false;
						}
					}
					break;
				default:
					break;
			}
		}
		if (module && module.setData && json.type !== "Address"){
			var hasError = false;
			if(["Org","Reader","Author","Personfield","Orgfield"].contains(json.type)){
				if(o2.typeOf(parsedD)==="array" && parsedD.length){
					hasError = parsedD.some(function (item) { return item.errorText; });
					flag = false;
				}
			}
			if(!hasError){
				module.setExcelData(parsedD);
				var ps = [];
				if( module.moduleExcelAG )ps.push( module.moduleExcelAG );
				if( module.moduleValueAG && !ps.contains(module.moduleValueAG) )ps.push( module.moduleValueAG );
				if( module.moduleSelectAG && !ps.contains(module.moduleSelectAG) )ps.push( module.moduleSelectAG );
				return Promise.all( ps ).then(function () {
					var result = module.validationExcel();
					if ( result && result.length ){
						lineData.errorTextList.push(colInfor + result.join("\n") );
						lineData.errorTextListExcel.push( colInforExcel + result.join("\n"));
						flag = false;
					}
					parsedLineData[json.id] = module.getData();
					return flag;
				})
			}
		}
		return flag
	},
	exportWithImportDataToExcel: function(eData){
		var exporter = new MWF.xApplication.process.Xform.Datatemplate.Exporter(this.template);
		exporter.exportWithImportDataToExcel(this.fieldArray, eData)
	},
	getOrgData : function( str ){
		str = str.trim();
		var flag = str.substr(str.length-2, 2);
		switch (flag.toLowerCase()){
			case "@i":
				return this.identityMap[str] || {"errorText": str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@p":
				return this.personMap[str] || {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@u":
				return this.unitMap[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@g":
				return this.groupMap[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			default:
				return this.identityMap[str] ||
					this.personMap[str] ||
					this.unitMap[str] ||
					this.groupMap[str] ||
					{"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };

		}
	},
	listAllOrgData : function (orgTitleList, callback) {
		var identityList = [], personList = [], unitList = [], groupList = [];
		if( orgTitleList.length > 0 ){
			this.importedData.each( function( lineData, lineIndex ){
				// if( lineIndex === 0 )return;

				orgTitleList.each( function (title, index) {

					if( !lineData[title] )return;

					var arr = this.stringToArray(lineData[title]);
					arr.each( function( a ){
						a = a.trim();
						var flag = a.substr(a.length-2, 2);
						switch (flag.toLowerCase()){
							case "@i":
								identityList.push( a ); break;
							case "@p":
								personList.push( a ); break;
							case "@u":
								unitList.push( a ); break;
							case "@g":
								groupList.push( a ); break;
							default:
								identityList.push( a );
								personList.push( a );
								unitList.push( a );
								groupList.push( a );
								break;
						}
					})
				}.bind(this))
			}.bind(this));
			var identityLoaded, personLoaded, unitLoaded, groupLoaded;
			var check = function () {
				if( identityLoaded && personLoaded && unitLoaded && groupLoaded ){
					if(callback)callback();
				}
			};

			this.identityMap = {};
			if( identityList.length ){
				identityList = identityList.unique();
				o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({ identityList : identityList }, function (json) {
					json.data.each( function (d) { this.identityMap[ d.matchKey ] = d; }.bind(this));
					identityLoaded = true;
					check();
				}.bind(this))
			}else{
				identityLoaded = true;
				check();
			}

			this.personMap = {};
			if( personList.length ){
				personList = personList.unique();
				o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({ personList : personList }, function (json) {
					json.data.each( function (d) { this.personMap[ d.matchKey ] = d; }.bind(this));
					personLoaded = true;
					check();
				}.bind(this))
			}else{
				personLoaded = true;
				check();
			}

			this.unitMap = {};
			if( unitList.length ){
				unitList = unitList.unique();
				o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ unitList : unitList }, function (json) {
					json.data.each( function (d) { this.unitMap[ d.matchKey ] = d; }.bind(this));
					unitLoaded = true;
					check();
				}.bind(this))
			}else{
				unitLoaded = true;
				check();
			}

			this.groupMap = {};
			if( groupList.length ){
				groupList = groupList.unique();
				o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({ groupList : groupList }, function (json) {
					json.data.each( function (d) { this.groupMap[ d.matchKey ] = d; }.bind(this));
					groupLoaded = true;
					check();
				}.bind(this))
			}else{
				groupLoaded = true;
				check();
			}
		}
	}
});

MWF.xDesktop.requireApp("Template", "utils.ExcelUtils", null, false);
MWF.xApplication.process.Xform.Datatemplate.ExcelUtils = new Class({
	Extends: MWF.xApplication.Template.utils.ExcelUtils,
	initialize: function(){
		this.sheet2JsonOptions = {};
		this.pollyfill();
	}
});

