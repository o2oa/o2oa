/**
 * 数据表格数据结构.
 * @typedef {Array} DatatableData
 * @example
 { //数据表格数据条目
  "data": [
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
    }
    ...
  ],
  "total": {
    "number": 222, //总计采用字段id
    "textfield": 2
  }
}
 */
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class DatatablePC 数据表格组件。表格形式的多行数据编辑组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var datatable = this.form.get("name"); //获取组件
 * //方法2
 * var datatable = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @since v6.2
 * @o2range {Process|CMS|Protal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.DatatablePC = new Class(
	/** @lends MWF.xApplication.process.Xform.DatatablePC# */
	{
		Implements: [Events],
		Extends: MWF.APP$Module,
		isEdit: false,
		options: {
			/**
			 * 所有内容加载后执行（包括异步加载）。
			 * @event MWF.xApplication.process.Xform.DatatablePC#afterLoad
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 每初始化一个条目，但未加载的时候触发，通过this.event可以获取条目对象。
			 * @event MWF.xApplication.process.Xform.DatatablePC#beforeLoadLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 每一个条目加载后时候触发，通过this.event可以获取条目对象。
			 * @event MWF.xApplication.process.Xform.DatatablePC#afterLoadLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 添加条目时触发。通过this.event.line可以获取对应的条目对象，this.event.ev可以获得事件触发的Event。
			 * @event MWF.xApplication.process.Xform.DatatablePC#addLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 删除条目前触发。通过this.event可以获取对应的条目对象。
			 * @event MWF.xApplication.process.Xform.DatatablePC#deleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 删除条目后触发。
			 * @event MWF.xApplication.process.Xform.DatatablePC#afterDeleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 导出excel的时候触发，this.event指向导出的数据，您可以通过修改this.event来修改数据。
			 * @event MWF.xApplication.process.Xform.DatatablePC#export
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
			 * 在导入excel，进行数据校验后触发，this.event指向导入的数据。
			 * @event MWF.xApplication.process.Xform.DatatablePC#validImport
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
			 *  	 	] //校验出的错误信息，如果改行数据正确，则无该字段
			 *  	 }
			 *  	 ...
			 *     ], //导入的数据
			 *     "validted" : true  //是否校验通过，可以在本事件中修改该参数，确定是否强制导入
			 * }
			 */
			/**
			 * 在导入excel，数据校验成功将要设置回数据表格的时候触发，this.event指向整理过的导入数据，格式见{@link DatatableData}。
			 * @event MWF.xApplication.process.Xform.DatatablePC#import
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			"moduleEvents": ["queryLoad","postLoad","load", "afterLoad",
				"beforeLoadLine", "afterLoadLine", "addLine", "deleteLine", "afterDeleteLine","completeLineEdit", "cancelLineEdit", "export", "import", "validImport"]
		},

		initialize: function(node, json, form, options){
			this.node = $(node);
			this.node.store("module", this);
			this.json = json;
			this.form = form;
			this.field = true;
		},

		_loadUserInterface: function(){
			this.fireEvent("queryLoad");

			this.editModules = [];
			this.node.setStyle("overflow-x", "auto");
			this.node.setStyle("overflow-y", "hidden");
			this.table = this.node.getElement("table");
			this.tBody = this.table.getElement("tbody");

			this.editable = !(this.readonly || (this.json.isReadonly === true));
			if (this.editable && this.json.editableScript && this.json.editableScript.code){
				this.editable = this.form.Macro.exec(((this.json.editableScript) ? this.json.editableScript.code : ""), this);
			}

			this.deleteable = this.json.deleteable !== "no";
			this.addable = this.json.addable !== "no";

			//允许导入
			this.importenable  = this.editable && (this.json.impexpType === "impexp" || this.json.impexpType === "imp");
			//允许导出
			this.exportenable  = this.json.impexpType === "impexp" || this.json.impexpType === "exp";

			//是否多行同时编辑
			this.multiEditMode = this.json.editMode === "multi";

			//是否有总计列
			this.totalFlag = false;
			this.totalColumns = [];
			this.totalNumberModuleIds = [];

			debugger;

			// this.hiddenColIndexList = [];

			this.data = this.getValue();
			if( !this._getBusinessData() ){
				this.isNew = true;
				this._setValue(this.data);
			}


			this.lineList = [];

			this.loadDatatable();
		},
		loadDatatable: function(){
			this._loadStyles();

			this._loadTitleTr();
			this._loadTemplate();
			this._loadTotalTr();

			this.fireEvent("load");
			this._loadDatatable(function(){
				this._loadImportExportAction();
				this.fireEvent("postLoad");
			}.bind(this));
		},
		_loadTitleTr: function(){
			this.titleTr = this.table.getElement("tr");

			var ths = this.titleTr.getElements("th");
			if (this.json.border){
				ths.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border
				});
			}
			if (this.json.titleStyles)ths.setStyles(this.json.titleStyles);

			//datatable$Title Module
			ths.each(function(th, index){
				var json = this.form._getDomjson(th);
				th.store("dataTable", this);
				th.addClass("mwf_origional");
				if (json){
					var module = this.form._loadModule(json, th);
					this.form.modules.push(module);
					if( json.isShow === false )th.hide(); //隐藏列
					if((json.total === "number") || (json.total === "count"))this.totalFlag = true;
				}
			}.bind(this));


			if(this.editable){
				var actionTh = new Element("th", {"styles": {"width": "46px"}}).inject(this.titleTr, "top"); //操作列
				if(this.addable){
					var addLineAction = new Element("div", {
						"styles": this.form.css.addLineAction,
						"events": {
							"click": function(e){ this._addLine(e.target, true); }.bind(this)
						}
					}).inject(actionTh);
				}
				var moveTh = new Element("th").inject(this.titleTr, "bottom"); //总计列
				if (this.json.border){
					Array.each([actionTh,moveTh], function(th){
						th.setStyles({
							"border-bottom": this.json.border,
							"border-right": this.json.border
						})
					}.bind(this));
				}
				if (this.json.titleStyles){
					actionTh.setStyles(this.json.titleStyles);
					moveTh.setStyles(this.json.titleStyles);
				}
			}
		},
		_loadTemplate: function(){
			// this.templateJson = {};

			var trs = this.table.getElements("tr");
			this.templateTr = trs[trs.length-1];

			this.templateNode = this.templateTr;

			var tds = this.templateNode.getElements("td");

			if (this.json.border) {
				tds.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border,
					"background": "transparent"
				});
			}
			if (this.json.contentStyles)tds.setStyles(this.json.contentStyles);

			//datatable$Data Module
			tds.each(function(td, index){
				var json = this.form._getDomjson(td);
				td.store("dataTable", this);
				td.addClass("mwf_origional");
				if (json){
					// var module = this.form._loadModule(json, td);
					// this.form.modules.push(module);
					if( json.cellType === "sequence" )td.addClass("mwf_sequence"); //序号列
					if( json.isShow === false )td.hide(); //隐藏列
				}
			}.bind(this));

			if(this.editable){
				var eTd = new Element("td.mwf_editaction",{"styles": this.json.actionStyles || {}}).inject(this.templateNode, "top"); //操作列
				var mTd = new Element("td.mwf_moveaction", {"styles": this.form.css.gridMoveActionCell || {}}).inject(this.templateNode, "bottom"); //排序列
				if (this.json.border){
					Array.each([eTd,mTd], function(td){
						td.setStyles({
							"border-bottom": this.json.border,
							"border-right": this.json.border,
							"background": "transparent"
						})
					}.bind(this));
				}
				if (this.json.contentStyles){
					eTd.setStyles(this.json.contentStyles);
					mTd.setStyles(this.json.contentStyles);
				}
			}

			this.templateHtml = this.templateNode.get("html");
			// var moduleNodes = this.form._getModuleNodes(this.templateNode);
			// moduleNodes.each(function (node) {
			// 	if (node.get("MWFtype") !== "form") {
			// 		var json = this.form._getDomjson(node);
			// 		this.templateJson[json.id] = json ;
			// 	}
			// }.bind(this));
			this.templateNode.hide();
		},
		_loadTotalTr: function(){
			if( !this.totalFlag )return;
			this.totalTr = new Element("tr.mwf_totaltr", {"styles": this.form.css.datagridTotalTr}).inject(this.tBody||this.table);

			var ths = this.titleTr.getElements("th");
			//datatable$Title Module
			ths.each(function(th, index){
				var td = new Element("td", {"text": "", "styles": this.form.css.datagridTotalTd}).inject(this.totalTr);
				if (this.json.amountStyles) td.setStyles(this.json.amountStyles);

				var json = this.form._getDomjson(th);
				if (json){
					if( json.isShow === false )td.hide(); //隐藏列
					if ((json.total === "number") || (json.total === "count")){
						this.totalColumns.push({
							"th" : th,
							"td" : td,
							"index": index,
							"type": json.total
						})
					}
				}
			}.bind(this));

			var tds = this.templateTr.getElements("td");
			//datatable$Data Module
			tds.each(function(td, index){
				var json = this.form._getDomjson(td);
				if (json){
					//总计列
					var tColumn = this.totalColumns.find(function(a){ return  a.index === index });
					if(tColumn){
						var moduleNodes = this.form._getModuleNodes(td); //获取总计列内的填写组件
						if( moduleNodes.length > 0 ){
							tColumn.moduleJson = this.form._getDomjson(moduleNodes[0]);
							if(tColumn.type === "number")this.totalNumberModuleIds.push( tColumn.moduleJson.id );
						}
					}
				}
			}.bind(this));
		},
		_loadTotal: function(){
			var totalData = {};
			if (!this.totalFlag)return totalData;
			if (!this.totalTr)this._loadTotalTr();
			var data = this.getValue();
			this.totalColumns.each(function(column, index){
				var json = column.moduleJson;
				if(!json)return;

				var pointLength = 0; //小数点后的最大数位
				var tmpV;
				if (column.type === "count"){
					tmpV = data.data.length;
				}else if(column.type === "number"){
					tmpV = new Decimal(0);
					for (var i=0; i<data.data.length; i++){
						var d = data.data[i];
						if(d[json.id]){
							tmpV = tmpV.plus(d[json.id].toFloat() || 0);
							var v = d[json.id].toString();
							if( v.indexOf(".") > -1 ){
								pointLength = Math.max(pointLength, v.split(".")[1].length);
							}
						}
					}
				}

				if( isNaN( tmpV ) ){
					totalData[json.id] = "";
					column.td.set("text", "" );
				}else{
					if( pointLength > 0 && tmpV.toString() !== "0" ){
						var s = tmpV.toString();
						if( s.indexOf(".") > -1 ){
							var length = s.split(".")[1].length;
							if( length < pointLength ){
								totalData[json.id] = s + "0".repeat(pointLength-length);
							}else{
								totalData[json.id] = s;
							}
						}else{
							totalData[json.id] = s +"."+ "0".repeat(pointLength)
						}
					}else{
						totalData[json.id] = tmpV.toString();
					}
					column.td.set("text", totalData[json.id] );
				}
			}.bind(this));
			data.total = totalData;
			return totalData;
		},
		_createLineNode: function(){
			var tr;
			if( this.totalTr ){
				tr = new Element("tr").inject(this.totalTr, "before");
			}else{
				tr = new Element("tr").inject(this.tBody || this.table);
			}
			return tr;
		},
		_loadStyles: function(){
			if (this.json.border) {
				this.table.setStyles({
					"border-top": this.json.border,
					"border-left": this.json.border
				});
			}
			this.node.setStyles(this.json.styles);
			this.table.setStyles(this.json.tableStyles);
			this.table.set(this.json.properties);
		},
		_getValue: function(){
			if (this.moduleValueAG) return this.moduleValueAG;
			var value = this._getBusinessData();
			if (!value){
				if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
				if (!value.then) if (o2.typeOf(value)==="array") value = {"data": value || [], "total":{}};
			}
			if(!value && this.multiEditMode){
				value = {"data": [], "total":{}};
				var count = this.json.defaultCount ? this.json.defaultCount.toInt() : 0;
				for( var i=0; i<count; i++ ){
					value.data.push({})
				}
			}
			return value || {"data": [], "total":{}};
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

		_loadDatatable: function(callback){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;

				this._loadLineList(function(){
					this._loadTotal();
					if(callback)callback();
				}.bind(this));

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
		_loadLineList: function(callback){
			this.data.data.each(function(data, idx){
				var isNew = this.isNew || (o2.typeOf(this.newLineIndex) === "number" ? idx === this.newLineIndex : false);
				var isEdited = (!this.multiEditMode && o2.typeOf(this.newLineIndex) === "number") ? idx === this.newLineIndex : this.multiEditMode;
				var node = this._createLineNode();
				var line = this._loadLine(node, data, idx, isEdited, isNew );
				this.lineList.push(line);
			}.bind(this));
			this.isNew = false;
			this.newLineIndex = null;
			if (callback) callback();
		},
		isMax : function(){
			var maxCount = this.json.maxCount ? this.json.maxCount.toInt() : 0;
			if( this.editable && maxCount > 0 ) {
				if( this.lineList.length >= maxCount )return true;
			}
			return false;
		},
		isMin : function(){
			var minCount = this.json.minCount ? this.json.minCount.toInt() : 0;
			if( this.editable && minCount > 0 ) {
				if( this.lineList.length <= minCount )return true;
			}
			return false;
		},
		_loadLine: function(container, data, index, isEdited, isNew){
			var line = new MWF.xApplication.process.Xform.DatatablePC.Line(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isEditable: this.editable,
				isDeleteable: this.deleteable,
				isAddable: this.addable
			});
			this.fireEvent("beforeLoadLine", [line]);
			line.load();
			this.fireEvent("afterLoadLine", [line]);
			return line;
		},
		_setLineData: function(line, d){
			var index = line.options.index;
			var data = this.getData();
			data.data[index] = d;
			this.setData( data );
		},
		_addLine: function(ev, edited, d){
			if( !this._completeLineEdit() )return;
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}
			var index = this.lineList.length;
			var data = this.getData();


				data.data.push(d||{});
				this.newLineIndex = index;


			this.setData( data );
			var line = this.getLine(index);
			line.isNewAdd = true;

			this.fireEvent("addLine", [{"line":line, "ev":ev}]);
			return line;
		},
		_insertLine: function(ev, beforeLine){
			if( !this._completeLineEdit() )return;
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}
			//使用数据驱动
			var index = beforeLine.options.index+1;

			var data = this.getData();
			data.data.splice(index, 0, {});
			this.newLineIndex = index;
			this.setData( data );
			var line = this.getLine(index);
			line.isNewAdd = true;

			this.fireEvent("addLine",[{"line":line, "ev":ev}]);
			return line;
		},
		_insertLineByIndex: function(ev, index, d){
			if( !this._completeLineEdit() )return;
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}
			//使用数据驱动
			var data = this.getData();
			if(data.data.length < index )return null;
			data.data.splice(index, 0, d||{});
			this.setData( data );
			var line = this.getLine(index);
			line.isNewAdd = true;

			this.fireEvent("addLine",[{"line":line, "ev":ev}]);
			return line;
		},
		_deleteSelectedLine: function(ev){
			var selectedLine = this.lineList.filter(function (line) { return line.selected; });
			if( selectedLine.length === 0 ){
				this.form.notice( MWF.xApplication.process.Xform.LP.selectItemNotice,"info");
				return false;
			}
			var minCount = this.json.minCount ? this.json.minCount.toInt() : 0;
			if( minCount > 0 ){
				if( this.lineList.length - selectedLine.length < minCount ){
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

			var data = _self.getData();

			lines.reverse().each(function(line){
				_self.fireEvent("deleteLine", [line]);

				if(line.deleteAttachment())saveFlag = true;

				data.data.splice(line.options.index, 1);

				if(this.currentEditedLine === line)this.currentEditedLine = null;

				_self.fireEvent("afterDeleteLine");
			});

			_self.setData( data );
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

			debugger;

			var saveFlag = line.deleteAttachment();
			//使用数据驱动
			var data = this.getData();

			data.data.splice(line.options.index, 1);

			if(this.currentEditedLine === line)this.currentEditedLine = null;
			this.setData( data );
			this.fireEvent("afterDeleteLine");

			if(saveFlag)this.form.saveFormData();
		},
		_cancelLineEdit: function(){
			var line = this.currentEditedLine;
			if( !line )return true;
			if( line.isNewAdd ){
				// var saveFlag = line.deleteAttachment();
				this._delLine( line );
				this.currentEditedLine = null;
				// if(saveFlag)this.form.saveFormData();
			}else{
				// line.data = Object.clone(line.getOriginalDataWithCheckAttachment());
				line.resetDataWithOriginalData();
				line.changeEditMode(false);
				this._loadTotal();
				if(line.attachmentChangeFlag){
					this.form.saveFormData();
					line.attachmentChangeFlag = false;
				}
				this.currentEditedLine = null;
				this.fireEvent("cancelLineEdit", [line]);
			}
			return true;
		},
		_completeLineEdit: function(){
			var line = this.currentEditedLine;
			if( !line )return true;
			if( !line.validation() )return false;
			line.isNewAdd = false;
			// line.data = line.getData();
			line.originalData = Object.clone(line.data);
			line.changeEditMode(false);
			this._loadTotal();
			if(line.attachmentChangeFlag){
				this.form.saveFormData();
				line.attachmentChangeFlag = false;
			}
			this.currentEditedLine = null;
			this.fireEvent("completeLineEdit", [line]);
			return true;
		},
		_moveUpLine: function(ev, line){
			if( this.currentEditedLine && !this._completeLineEdit() )return false;
			if( line.options.index === 0 )return;

			var data = this.getData();
			var upData = data.data[line.options.index-1];
			var curData = data.data[line.options.index];
			data.data[line.options.index] = upData;
			data.data[line.options.index-1] = curData;
			this.setData( data );
		},
		_changeEditedLine: function(line){
			if( this.currentEditedLine ){
				if( line ===  this.currentEditedLine )return;
				if( !this._completeLineEdit() )return;
			}
			line.changeEditMode(true);
			this.currentEditedLine = line;
		},

		editValidation: function(){
			var flag = true;
			this.editModules.each(function(field, key){
				if (field.json.type!=="sequence" && field.validationMode ){
					field.validationMode();
					if (!field.validation()) flag = false;
				}
			}.bind(this));
			return flag;
		},


		_afterLoaded: function(){
		},
		/**
		 * @summary 重置数据表格的值为默认值或置空。
		 *  @example
		 * this.form.get('fieldId').resetData();
		 */
		resetData: function(){
			this.setData(this._getValue());
		},
		/**当参数为Promise的时候，请查看文档: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|使用Promise处理表单异步}<br/>
		 * 当表单上没有对应组件的时候，可以使用this.data[fieldId] = data赋值。
		 * @summary 为数据表格赋值。
		 * @param data{DatatableData|Promise|Array} 必选，数组或Promise.
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
		setData: function(data){
			if (!data){
				data = this._getValue();
			}else{
				//todo 计算total
			}
			this._setData(data);
		},
		_setData: function(data){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				// if (o2.typeOf(data)==="object") data = [data];
				this.__setData(data);
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
		__setData: function(data){
			// if( typeOf( data ) === "object" && typeOf(data.data) === "array"  ){
			this._setBusinessData(data);
			this.data = data;

			if (this.data){
				this.clearSubModules();
			}

			this.lineList = [];
			this._loadDatatable()
		},
		clearSubModules: function(){
			for (var i=0; i<this.lineList.length; i++){
				this.lineList[i].clearSubModules();
			}
		},
		/**
		 * @summary 判断数据表格是否为空.
		 * @example
		 * if( this.form.get('fieldId').isEmpty() ){
		 *     this.form.notice('至少需要添加一条数据', 'warn');
		 * }
		 * @return {Boolean} 是否为空
		 */
		isEmpty: function(){
			var data = this.getData();
			if( !data || !data.data )return true;
			if( o2.typeOf( data.data ) === "array" ){
				return data.data.length === 0;
			}
			//????
			// if( o2.typeOf( data ) === "object" ){
			// 	return Object.keys(data).length === 0;
			// }
			return false;
		},
		//api 相关开始
		/**
		 * 获取对应的条目。
		 * @param {Number} index 条目序号，从零开始
		 * @return {MWF.xApplication.process.Xform.DatatablePC.Line | Null} 对应的数据表格条目
		 * @example
		 * //获取数据表格“dt1”的第一个条目。
		 * var line = this.form.get("dt1").getLine(0);
		 * //获取第一个条目subject字段的值
		 * var data = line.getModule("subject").getData();
		 * //设置subject字段的值
		 * line.getModule("subject").setData("test1");
		 */
		getLine: function(index){
			var line = this.lineList[index];
			return line || null;
		},
		/**
		 * 在数据表格末尾添加条目。
		 * @param {Object} [data] 添加条目的数据。
		 * @return {MWF.xApplication.process.Xform.DatatablePC.Line} 添加的数据表格条目
		 * @example
		 * var line = this.form.get("dt1").addLine();
		 */
		addLine: function( data ){
			return this._addLine( null, null,  data );
		},
		/**
		 * 在数据表格指定位置添加条目。
		 * @param {Number} index 条目序号，从零开始，如果下标超过当前数据表格条目数，插入失败并返回null。
		 * @param {Object} [data] 添加条目的数据。
		 * @return {MWF.xApplication.process.Xform.DatatablePC.Line | Null} 插入的数据表格条目
		 * @example
		 * var line = this.form.get("dt1").insertLine(0);
		 */
		insertLine: function(index, data){
			return this._insertLineByIndex(null,  index, data);
		},
		/**
		 * 删除指定位置的条目。
		 * @param {Number} index 条目序号，从零开始，如果下标超过当前数据表格条目数，删除失败。
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
		 * 获取对应表单组件。
		 * @param {Number} index 条目序号，从零开始
		 * @param {String} id 组件标识
		 * @return {FormComponent} 对应表单组件
		 * @example
		 * //获取数据表格“dt1”的第一个条目的subject字段。
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
		//api 相关

		/**
		 * 在脚本中使用 this.data[fieldId] 也可以获取组件值。
		 * 区别如下：<br/>
		 * 1、当使用Promise的时候<br/>
		 * 使用异步函数生成器（Promise）为组件赋值的时候，用getData方法立即获取数据，可能返回修改前的值，当Promise执行完成以后，会返回修改后的值。<br/>
		 * this.data[fieldId] 立即获取数据，可能获取到异步函数生成器，当Promise执行完成以后，会返回修改后的值。<br/>
		 * {@link https://www.yuque.com/o2oa/ixsnyt/ws07m0#EggIl|具体差异请查看链接}<br/>
		 * 2、当表单上没有对应组件的时候，可以使用this.data[fieldId]获取值，但是this.form.get('fieldId')无法获取到组件。
		 * @summary 获取数据表格数据.
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
		 * @return {DatatableData}
		 */
		getData: function(){
			if( this.importer ){
				this.importer.destroySimulateModule();
			}
			if (this.editable!==false){
				// this.lineList.each(function(line, index){
				// 	if( !this.multiEditMode && line.options.isEdited ){
				// 		line.data = line.getData();
				// 	}else{
				// 		line.data = line.getData();
				// 	}
				// });
				return this._getBusinessData();
			}else{
				return this._getBusinessData();
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
				//???
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
		/**
		 * @summary 根据组件的校验设置进行校验。
		 *  @param {String} [routeName] - 可选，路由名称.
		 *  @example
		 *  if( !this.form.get('fieldId').validation() ){
		 *      return false;
		 *  }
		 *  @return {Boolean} 是否通过校验
		 */
		validation: function(routeName, opinion){
			if (this.isEdit){
				if (!this.editValidation()){
					return false;
				}
			}
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
		},
		_loadImportExportAction: function(){
			this.impexpNode = this.node.getElement("div.impexpNode");
			if( this.impexpNode )this.impexpNode.destroy();
			this.impexpNode = null;

			if( !this.exportenable && !this.importenable )return;

			var position = ["leftTop","centerTop","rightTop"].contains( this.json.impexpPosition || "" ) ? "top" : "bottom";
			var container = new Element("div").inject(this.node, position);

			this.importExportAreaNode = new Element("div").inject( container );
			if( ["leftTop","leftBottom"].contains( this.json.impexpPosition || "" ) ){
				this.importExportAreaNode.setStyles({ "float" : "left" })
			}else if( ["rightTop","rightBottom"].contains( this.json.impexpPosition || "" ) ){
				this.importExportAreaNode.setStyles({ "float" : "right" })
			}else{
				this.importExportAreaNode.setStyles({ "margin" : "0px auto" })
			}

			if( this.exportenable ){
				this.exportActionNode = new Element("div", {
					text : this.json.exportActionText || MWF.xApplication.process.Xform.LP.datagridExport
				}).inject(this.importExportAreaNode);
				var styles;
				if( this.json.exportActionStyles ){
					styles = this.json.exportActionStyles
				}else{
					styles = this.form.css.gridExportActionStyles;
				}
				this.exportActionNode.setStyles(styles);

				this.exportActionNode.addEvent("click", function () {
					this.exportToExcel();
				}.bind(this))
			}

			if( this.importenable ){
				this.importActionNode = new Element("div", {
					text : this.json.importActionText || MWF.xApplication.process.Xform.LP.datagridImport
				}).inject(this.importExportAreaNode);
				var styles;
				if( this.json.importActionStyles ){
					styles = this.json.importActionStyles;
				}else{
					styles = this.form.css.gridImportActionStyles;
				}
				this.importActionNode.setStyles(styles);

				this.importActionNode.addEvent("click", function () {
					this.importFromExcel();
				}.bind(this))
			}

			if( ["centerTop","centerBottom"].contains( this.json.impexpPosition ) ){
				var width = 2;

				if( this.exportActionNode ){
					width = width + this.exportActionNode.getSize().x +
						this.exportActionNode.getStyle("padding-left").toFloat() +
						+ this.exportActionNode.getStyle("padding-right").toFloat() +
						+ this.exportActionNode.getStyle("margin-left").toFloat() +
						+ this.exportActionNode.getStyle("margin-right").toFloat()
				}

				if( this.importActionNode ){
					width = width + this.importActionNode.getSize().x +
						this.importActionNode.getStyle("padding-left").toFloat() +
						+ this.importActionNode.getStyle("padding-right").toFloat() +
						+ this.importActionNode.getStyle("margin-left").toFloat() +
						+ this.importActionNode.getStyle("margin-right").toFloat()
				}

				this.importExportAreaNode.setStyle( "width", width+"px" );
			}
		},
		exportToExcel: function(){
			this.exporter = new MWF.xApplication.process.Xform.DatatablePC.Exporter(this);
			this.exporter.exportToExcel();
		},
		importFromExcel: function(){
			this.importer = new MWF.xApplication.process.Xform.DatatablePC.Importer(this);
			this.importer.importFromExcel();
		}
	});

MWF.xApplication.process.Xform.DatatablePC$Title = new Class({
	Extends: MWF.APP$Module
});

MWF.xApplication.process.Xform.DatatablePC$Data =  new Class({
	Extends: MWF.APP$Module
});

MWF.xApplication.process.Xform.DatatablePC.Line =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: false,
		isEdited : true, //是否正在编辑
		isEditable : true, //能否被编辑
		isDeleteable: true, //能否被删除
		isAddable: true, //能否添加
		index : 0,
		indexText : "0"
	},
	initialize: function (node, datatable, data, options) {

		this.setOptions(options);

		this.node = node;
		this.datatable = datatable;
		this.data = data;
		this.form = this.datatable.form;

		// if( !this.datatable.multiEditMode && !this.options.isNew){
		// 	this.originalData = Object.clone(data);
		// }

		this.init()

	},
	init: function(){
		this.modules = [];
		this.all = {};
		this.all_templateId = {};

		this.fields = [];
		this.allField = {};
		this.allField_templateId = {};

		this.changedAttachmentMap = {};
	},
	load: function(){
		if( !this.datatable.multiEditMode && this.options.isEdited )this.datatable.currentEditedLine = this;

		this.loadModules();
		this.loadSequence();
		this.createActions();
		this.loadZebraStyle();
		this.loadEditedStyle();
		this.addNodeEvent();

		if( !this.datatable.multiEditMode )this.originalData = Object.clone(this.data);

		// if(this.options.isNew && this.options.isEdited){
		// 	debugger;
		// 	this.data = this.getData();
		// 	if( !this.datatable.multiEditMode )this.originalData = Object.clone(this.data);
		// 	this.options.isNew = false;
		// }
	},
	loadModules: function(){
		this.node.set("html", this.datatable.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.node, true);
		moduleNodes.each(function (node) {
			var mwfType = node.get("MWFtype");
			if (mwfType === "form")return;

			var _self = this;

			var tJson = this.form._getDomjson(node);
			if( tJson ){
				var json = Object.clone(tJson);

				if( !this.options.isEdited || !this.options.isEditable )json.isReadonly = true;

				var templateJsonId = json.id;

				var index = this.options.index;

				var id = this.datatable.json.id + "..data.." + index + ".." + json.id;

				json.id = id;
				node.set("id", id);

				if( json.type==="Attachment" || json.type==="AttachmentDg" ){
					json.type = "AttachmentDg";
					json.ignoreSite = true;
					json.site = this.getAttachmentSite(json, templateJsonId);
				}

				if (this.form.all[id]) this.form.all[id] = null;
				if (this.form.forms[id])this.form.forms[id] = null;

				var hasData = this.data.hasOwnProperty(templateJsonId);

				var module = this.form._loadModule(json, node, function () {});

				if((json.type==="Attachment" || json.type==="AttachmentDg")){
					module.addEvent("change", function(){
						if( this.datatable.multiEditMode ){
							_self.form.saveFormData();
						}else{
							_self.attachmentChangeFlag = true;
						}
					}.bind(this))
				}

				this.form.modules.push(module);

				this.modules.push(module);
				this.all[id] = module;
				this.all_templateId[templateJsonId] = module;

				if (module.field) {
					// if(hasData){
					// 	module.setData(this.data[templateJsonId]);
					// }else if(this.options.isEdited){
					// 	this.data[templateJsonId] = module.getData();
					// }
					if(this.options.isEdited) {
						if (json.type !== "Attachment" && json.type !== "AttachmentDg"){
							this.data[templateJsonId] = module.getData();
						}
					}
					this.allField[id] = module;
					this.allField_templateId[templateJsonId] = module;
					this.fields.push( module );

					//该字段是合集数值字段
					if(this.datatable.multiEditMode && this.datatable.totalNumberModuleIds.contains(templateJsonId)){
						//module
						module.addEvent("change", function(){
							this.datatable._loadTotal();
						}.bind(this))
					}
				}
			}
		}.bind(this));
	},
	getModule: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	getAttachmentSite: function(json, templateJsonId){
		//确保site最长为64，否则后台会报错

		var index = this.options.index;

		var baseSite;
		baseSite =  "." + index + "."  + (json.site || templateJsonId);

		var maxLength = 64 - baseSite.length;

		var templateId = this.datatable.json.id;
		templateId = (templateId.length > maxLength) ? templateId.substr(templateId.length-maxLength, maxLength) : templateId;

		return templateId + baseSite;
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

					for( var i=0; i<this.form.businessData.attachmentList.length; i++ ){
						var attData = this.form.businessData.attachmentList[i];
						if( attData.id === d.id ){
							this.form.businessData.attachmentList.erase(attData);
							break;
						}
					}

				}.bind(this))
			}
		}
		return saveFlag;
	},
	loadSequence: function(){
		var sequenceTd = this.node.getElements(".mwf_sequence");
		if(sequenceTd)sequenceTd.set("text", this.options.indexText)
	},
	loadZebraStyle: function(){
		if ((this.options.index%2)===0 && this.datatable.json.zebraColor){
			this.node.setStyle("background-color", this.datatable.json.zebraColor);
		}else if(this.datatable.json.backgroundColor){
			this.node.setStyle("background-color", this.datatable.json.backgroundColor);
		}
	},
	loadEditedStyle: function(){
		if (!this.datatable.multiEditMode && this.options.isEdited && this.datatable.json.editStyles){
			var tds = this.node.getElements("td");
			tds.setStyles(this.datatable.json.editStyles);
		}
	},
	addNodeEvent: function(){
		if(!this.datatable.multiEditMode && this.options.isEditable){
			this.editFun = function(){
				if( !this.options.isEdited ){
					this.datatable._changeEditedLine(this)
				}
			}.bind(this);
			this.node.addEvent("click", this.editFun)
		}
	},
	createActions: function () {
		//不允许编辑，直接返回
		if(!this.options.isEditable)return;

		var editActionTd = this.node.getElement(".mwf_editaction");
		var moveActionTd = this.node.getElement(".mwf_moveaction");

		if(this.datatable.multiEditMode){ //多行编辑模式
			if(this.options.isAddable)this.createAddAction(editActionTd);
			if(this.options.isDeleteable)this.createDelAction(editActionTd);
		}else{ //单行编辑模式
			if(this.options.isAddable)this.createAddAction(editActionTd);
			if(this.options.isDeleteable)this.createDelAction(editActionTd);
			this.createCompleteAction(editActionTd);
			this.createCancelEditAction(editActionTd);
			this.checkActionDisplay();
		}
		this.createMoveAction(moveActionTd);

	},
	checkActionDisplay: function(){
		if( this.options.isEdited ){
			if( this.addLineAction )this.addLineAction.hide();
			if( this.delLineAction )this.delLineAction.hide();
			if( this.completeLineAction )this.completeLineAction.show();
			if( this.cancelLineEditAction )this.cancelLineEditAction.show();
		}else{
			if( this.addLineAction )this.addLineAction.show();
			if( this.delLineAction )this.delLineAction.show();
			if( this.completeLineAction )this.completeLineAction.hide();
			if( this.cancelLineEditAction )this.cancelLineEditAction.hide();
		}
	},
	createAddAction: function(td){
		this.addLineAction = new Element("div", {
			"styles": this.form.css.addLineAction,
			"events": {
				"click": function(ev){
					this.datatable._insertLine( ev, this );
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createCompleteAction: function(td){
		this.completeLineAction = new Element("div", {
			"styles": this.form.css.completeLineAction,
			"events": {
				"click": function(ev){
					this.datatable._completeLineEdit(ev);
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createCancelEditAction: function(td){
		this.cancelLineEditAction = new Element("div", {
			"styles": this.form.css.delLineAction,
			"events": {
				"click": function(ev){
					this.datatable._cancelLineEdit(ev, this);
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createDelAction: function(td){
		this.delLineAction = new Element("div", {
			"styles": this.form.css.delLineAction,
			"events": {
				"click": function(ev){
					this.datatable._deleteLine( ev, this );
					// if( this.datatable.currentEditedLine === this )this.datatable.currentEditedLine = null;
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createMoveAction: function(td){
		this.moveAction = new Element("div", {
			"styles": this.form.css.datatableMoveLineAction,
			"text": "↑",
			"events": {
				"click": function(ev){
					this.datatable._moveUpLine( ev, this );
					// if( this.datatable.currentEditedLine === this )this.datatable.currentEditedLine = null;
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	changeEditMode: function( isEdited ){
		if( isEdited === this.options.isEdited )return;
		if( !this.options.isEditable )return;
		this.options.isEdited = isEdited;
		this.reload();
	},
	reload: function(){
		for(var key in this.all){
			var module = this.all[key];
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.empty();
		if(this.editFun){
			this.node.removeEvent("click", this.editFun);
			this.editFun = null;
		}
		this.init();
		this.load();
	},
	clearSubModules: function () { //把module清除掉
		for(var key in this.all){
			var module = this.all[key];
			//如果嵌套数据模板或者数据表格，还要清除掉下级
			if(module.clearSubModules)module.clearSubModules();
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.destroy();
		this.init();
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
	},
	setData: function (data) {
		this.datatable._setLineData(this, data);
	},
	validation: function(){
		if( !this.options.isEdited || !this.options.isEditable )return true;
		var flag = true;
		this.fields.each(function(field, key){
			if (field.json.type!="sequence" && field.validationMode ){
				field.validationMode();
				if (!field.validation()) flag = false;
			}
		}.bind(this));
		return flag;
	},
	resetDataWithOriginalData: function () {
		var data = this.originalData;
		var attachmentList = this.form.businessData.attachmentList;
		for( var key in this.allField_templateId){
			var module = this.allField_templateId[key];
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				data[key] = (data[key] || []).filter(function(att, index){
					for( var i=0; i<attachmentList.length; i++){
						if( attachmentList[i].id === att.id )return true;
					}
					return false;
				}.bind(this))
			}
			this.data[key] = data[key];
		}
		return data;
	}
});

MWF.xApplication.process.Xform.DatatablePC.Exporter = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function (datatable, options) {

		this.setOptions(options);

		this.datatable = datatable;
		this.form = this.datatable.form;

		this.columnJsonList = [];
	},
	exportToExcel : function () {
		this.getColumnList();

		var resultArr = [];
		var titleArr = this.getTitleArray();
		resultArr.push( titleArr );


		this.datatable.lineList.each(function (line, index) {
			resultArr.push( this.getLineExportData(line, index) );
		}.bind(this));

		var colWidthArr = this.getColWidthArray();
		var excelName = this.getExcelName();

		var arg = {
			data : resultArr,
			colWidthArray : colWidthArr,
			title : excelName
		};
		this.datatable.fireEvent("export", [arg]);

		new MWF.xApplication.process.Xform.DatatablePC.ExcelUtils( this.datatable ).exportToExcel(
			arg.data || resultArr,
			arg.title || excelName,
			arg.colWidthArray || colWidthArr,
			this.getDateIndexArray()  //日期格式列下标
		);
	},
	getColumnList: function(){
		this.columnJsonList = [];

		var ths = this.datatable.titleTr.getElements("th.mwf_origional");
		var tds = this.datatable.templateTr.getElements("td.mwf_origional");

		ths.each(function(th, index){
			var thJson = this.form._getDomjson( th );

			var mJson;
			if(tds[index]){
				var mNodes = this.form._getModuleNodes(tds[index]); //获取总计列内的填写组件
				if( mNodes.length > 0 )mJson = this.form._getDomjson(mNodes[0]);
			}

			if(thJson && mJson && this.isAvaliableColumn(thJson, mJson)){
				this.columnJsonList.push({
					"thJson": thJson,
					"title": th.get("text"),
					"mJson" : mJson,
					"available": true
				})
			}
		}.bind(this));
	},
	getLineExportData: function(line, index ){
		var exportData = [];
		this.columnJsonList.each(function (column) {

			var module;
			if( column.mJson && column.available ){
				module = line.all_templateId[column.mJson.id];
			}
			if ( !module ) {
				exportData.push("");
			}else{
				var value = module.getData();
				var text = "";

				if( value ){
					switch (column.mJson.type) {
						case "Org":
						case "Reader":
						case "Author":
						case "Personfield":
						case "Orgfield":
							if (o2.typeOf(value) === "array") {
								var textArray = [];
								value.each(function (item) {
									if (o2.typeOf(item) === "object") {
										textArray.push(item.distinguishedName);
									} else {
										textArray.push(item);
									}
								}.bind(this));
								text = textArray.join(", \n");
							} else if (o2.typeOf(value) === "object") {
								text = value.distinguishedName;
							} else {
								text = value;
							}
							break;
						case "Combox":
						case "Address":
							text = o2.typeOf(value) === "array" ? value.join(", ") : value;
							break;
						case "Checkbox":
							var options = module.getOptionsObj();
							var value = o2.typeOf(value) === "array" ? value : [value];
							var arr = [];
							value.each( function( a, i ){
								var idx = options.valueList.indexOf( a );
								arr.push( idx > -1 ? options.textList[ idx ] : "") ;
							});
							text = arr.join(", ");
							break;
						case "Radio":
						case "Select":
							var options = module.getOptionsObj();
							var idx = options.textList.indexOf( value );
							text = idx > -1 ? options.valueList[ idx ] : "";
							break;
						case "Textarea":
							text = value;
							break;
						case "Calendar":
							text = value;
							break;
						default:
							text = value;
							break;
					}
				} else if ( column.mJson.type === "Label" && module.node) {
					text = module.node.get("text");
				}

				if( !text && o2.typeOf(text) !== "number" ){
					text = "";
				}

				exportData.push( text );
			}
		}.bind(this));
		return exportData;
	},
	isAvaliableColumn : function(thJson, mJson){
		if (thJson && ( thJson.isShow === false || thJson.isImpExp === false ))return false; //隐藏列，不允许导入导出
		if (mJson && (mJson.type == "sequence" || mJson.cellType == "sequence") )return false; //序号列
		if (mJson && ["Image","Button","ImageClipper","Attachment","AttachmentDg","Label"].contains(mJson.type) )return false; //图片，附件,Label列不导入导出
		// if (type==="import" && module && ["Label"].contains(module.json.type))return false; //Label 不导入
		return true;
	},
	getColWidthArray : function(){
		var colWidthArr = [];
		this.columnJsonList.each(function(c, index){
			if ( c.available ) {
				if (c.mJson && ["Org","Reader","Author","Personfield","Orgfield"].contains(c.mJson.type)) {
					colWidthArr.push(340);
				} else if (c.mJson && c.mJson.type === "Address") {
					colWidthArr.push(170);
				} else if (c.mJson && c.mJson.type === "Textarea") {
					colWidthArr.push(260);
				} else if (c.mJson && c.mJson.type === "Htmleditor") {
					colWidthArr.push(500);
				} else if (c.mJson && c.mJson.type === "Calendar") {
					colWidthArr.push(150);
				} else {
					colWidthArr.push(150);
				}
			}
		}.bind(this));
		return colWidthArr;
	},
	getDateIndexArray : function(){
		var dateIndexArr = []; //日期格式列下标
		var idx=0;
		this.columnJsonList.each(function(c){
			if ( c.available && c.mJson && c.mJson.type === "Calendar") {
				dateIndexArr.push(idx);
				idx++;
			}
		}.bind(this));
		return dateIndexArr;
	},
	getTitleArray : function(){
		var arr = [];
		this.columnJsonList.each(function(c, index){
			if ( c.available && c.mJson )arr.push(c.title);
		}.bind(this));
		return arr;
	},
	getExcelName: function(){
		var title;
		if( this.datatable.json.excelName && this.datatable.json.excelName.code ){
			title = this.form.Macro.exec(this.datatable.json.excelName.code, this);
		}else{
			title = MWF.xApplication.process.Xform.LP.datatableExportDefaultName;
		}
		var titleA = title.split(".");
		if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
			titleA.splice( titleA.length-1 );
		}
		title = titleA.join(".");
		return title;
	},

	exportWithImportDataToExcel : function ( importedData ) {
		this.getColumnList();

		var resultArr = [];

		var titleArr = this.getTitleArray("import");
		titleArr.push( MWF.xApplication.process.Xform.LP.validationInfor );
		resultArr.push( titleArr );

		importedData.each( function( lineData, lineIndex ){
			var array = [];
			this.columnJsonList.each( function (obj, i) {
				array.push( ( lineData[ obj.title ] || '' ).replace(/&#10;/g, "\n") );
			});
			array.push( lineData.errorTextListExcel ? lineData.errorTextListExcel.join("\n") : ""  );

			resultArr.push( array );
		}.bind(this));

		var colWidthArr = this.getColWidthArray();
		colWidthArr.push( 300 ); //提示信息

		var excelName = this.getExcelName();

		var arg = {
		    data : resultArr,
		    colWidthArray : colWidthArr,
		    title : excelName,
		    withError : true
		};
		this.datatable.fireEvent("export", [arg]);

		new MWF.xApplication.process.Xform.DatatablePC.ExcelUtils( this.datatable ).exportToExcel(
		    arg.data || resultArr,
		    arg.title || excelName,
		    arg.colWidthArray || colWidthArr,
		    this.getDateIndexArray() //日期格式列下标
		 );
	}
});

MWF.xApplication.process.Xform.DatatablePC.Importer = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function (datatable, options) {

		this.setOptions(options);

		this.datatable = datatable;
		this.form = this.datatable.form;

		this.columnJsonList = [];
	},
	isAvaliableColumn : function(thJson, mJson){
		if (thJson && ( thJson.isShow === false || thJson.isImpExp === false ))return false; //隐藏列，不允许导入导出
		if (mJson && (mJson.type == "sequence" || mJson.cellType == "sequence") )return false; //序号列
		if (mJson && ["Image","Button","ImageClipper","Attachment","AttachmentDg","Label"].contains(mJson.type) )return false; //图片，附件,Label列不导入导出
		// if (type==="import" && module && ["Label"].contains(module.json.type))return false; //Label 不导入
		return true;
	},
	importFromExcel : function () {
		this.getColumnList();
		var dateColArray = this.getDateIndexArray(); //日期列
		var orgTitleArray = this.getOrgTitleArray();

		new MWF.xApplication.process.Xform.DatatablePC.ExcelUtils( this.datatable ).upload( dateColArray, function (data) {
			if( !this.checkCount(data) )return;
			var checkAndImport = function () {
				if( !this.checkData( data ) ){
					this.openErrorDlg( data );
				}else{
					this.importData( data )
				}
				this.destroySimulateModule();
			}.bind(this);

			if( orgTitleArray.length > 0 ){
				this.listAllOrgData( orgTitleArray, data, function () {
					checkAndImport();
				}.bind(this));
			}else{
				checkAndImport();
			}


		}.bind(this));
	},
	destroySimulateModule: function(){
		if( !this.simelateModuleMap )return;
		var keys = Object.keys(this.simelateModuleMap);
		keys.each(function (key, i) {
			var module = this.simelateModuleMap[key];
			if( module ){
				var id = module.json.id;
				if( this.form.businessData.data.hasOwnProperty(id) )delete this.form.businessData.data[id];
				delete this.simelateModuleMap[key];
			}
		}.bind(this))
		this.simelateModuleMap = null;

		if(this.simulateNode){
			this.simulateNode.destroy();
			this.simulateNode = null;
		}
	},
	loadSimulateModule: function(){
		if( this.simelateModuleMap ){
			this.destroySimulateModule();
		}
		//加载模拟字段
		this.simelateModuleMap = {};
		this.simulateNode = new Element("div").inject(this.datatable.node);
		this.simulateNode.hide();
		this.simulateNode.set("html", this.datatable.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.simulateNode);
		moduleNodes.each(function (node) {
			if (node.get("MWFtype") !== "form") {
				var _self = this;

				var tJson = this.form._getDomjson(node);
				if( tJson && this.isAvaliableColumn(null, tJson) ){
					var json = Object.clone(tJson);

					var templateJsonId = json.id;

					json.id = "dtSimulate_"+json.id;
					node.set("id", json.id);

					if (!MWF["APP" + json.type]) {
						MWF.xDesktop.requireApp("process.Xform", json.type, null, false);
					}
					var module = new MWF["APP" + json.type](node, json, this.form);

					this.simelateModuleMap[templateJsonId] = module;

					module.load();

				}
			}
		}.bind(this));
	},
	getColumnList: function(){
		this.loadSimulateModule();

		this.columnJsonList = [];

		var ths = this.datatable.titleTr.getElements("th.mwf_origional");
		var tds = this.datatable.templateTr.getElements("td.mwf_origional");
		var idx = 0;

		ths.each(function(th, index){
			var thJson = this.form._getDomjson( th );

			var mJson;
			if(tds[index]){
				var mNodes = this.form._getModuleNodes(tds[index]); //获取总计列内的填写组件
				if( mNodes.length > 0 )mJson = this.form._getDomjson(mNodes[0]);
			}

			if(thJson && mJson && this.isAvaliableColumn(thJson, mJson)){
				this.columnJsonList.push({
					"thJson": thJson,
					"title": th.get("text"),
					"mJson" : mJson,
					"field": mJson.id,
					"index": idx,
					"module": this.simelateModuleMap[mJson.id]
				})
				idx++;
			}
		}.bind(this));

		return this.columnJsonList;
	},
	getDateIndexArray : function(){
		var dateIndexArr = []; //日期格式列下标
		var idx=0;
		this.columnJsonList.each(function(c){
			if ( c.mJson && c.mJson.type === "Calendar") {
				dateIndexArr.push(idx);
				idx++;
			}
		}.bind(this));
		return dateIndexArr;
	},
	getOrgTitleArray : function(){
		var orgTitleArr = [];
		this.columnJsonList.each(function(c){
			if ( c.mJson && ["Org","Reader","Author","Personfield","Orgfield"].contains(c.mJson.type) ) {
				orgTitleArr.push(c.title);
			}
		}.bind(this));
		return orgTitleArr;
	},
	parseImportedData: function(idata){
		var data = [];

		idata.each( function( ilineData ){
			var lineData = {};

			this.columnJsonList.each( function (obj, i) {
				var index = obj.index;
				var module = obj.module;
				var json = obj.mJson;
				var text = obj.title;

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
							var arr = this.stringToArray(d);
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
						case "Combox":
						case "Address":
							arr = this.stringToArray(d);
							value = arr.length === 0  ? arr[0] : arr;
							break;
						case "Checkbox":
							arr = this.stringToArray(d);
							var options = module.getOptionsObj();
							arr.each( function( a, i ){
								var idx = options.textList.indexOf( a );
								arr[ i ] = idx > -1 ? options.valueList[ idx ] : a;
							});
							value = arr.length === 1  ? arr[0] : arr;
							break;
						case "Radio":
						case "Select":
							value = d.replace(/&#10;/g,""); //换行符&#10;
							var options = module.getOptionsObj();
							var idx = options.textList.indexOf( value );
							value = idx > -1 ? options.valueList[ idx ] : value;
							break;
						case "Textarea":
							value = d.replace(/&#10;/g,"\n"); //换行符&#10;
							break;
						case "Calendar":
							value = d.replace(/&#10;/g,""); //换行符&#10;
							if( value && (new Date(value).isValid()) ){
								var format;
								if (!json.format){
									if (json.selectType==="datetime" || json.selectType==="time"){
										format = (json.selectType === "time") ? "%H:%M" : (Locale.get("Date").shortDate + " " + "%H:%M")
									}else{
										format = Locale.get("Date").shortDate;
									}
								}else{
									format = json.format;
								}
								value = Date.parse( value ).format( format );
							}
							break;
						default:
							value = d.replace(/&#10;/g,""); //换行符&#10;
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
	importData: function(idata){

		var data = this.parseImportedData(idata);

		this.datatable.fireEvent("import", [data] );

		this.datatable.setData( { "data" : data } );
		this.form.notice( MWF.xApplication.process.Xform.LP.importSuccess );

	},
	openErrorDlg : function(eData){
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
			})
			return arr.join(" ")
		}

		var htmlArray = ["<table "+ objectToString( this.datatable.json.impExpTableProperties ) +" style='"+objectToString( this.datatable.json.impExpTableStyles, "style" )+"'>"];

		var titleStyle = objectToString(this.datatable.json.impExpTableTitleStyles, "style");
		htmlArray.push("<tr>");
		this.columnJsonList.each(function (obj, i) {
			htmlArray.push( "<th style='"+titleStyle+"'>"+obj.title+"</th>" );
		});
		htmlArray.push("<th style='"+titleStyle+"'> "+MWF.xApplication.process.Xform.LP.validationInfor +"</th>");
		htmlArray.push("</tr>" );

		var contentStyles = Object.clone( this.datatable.json.impExpTableContentStyles );
		if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
		var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

		eData.each( function( lineData, lineIndex ){

			htmlArray.push( "<tr>" );
			this.columnJsonList.each( function (obj, i) {
				htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ obj.title ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //换行符&#10;
			});
			htmlArray.push( "<td style='"+contentStyle+"'>"+( lineData.errorTextList ? lineData.errorTextList.join("<br/>") : "" )+"</td>" );
			htmlArray.push( "</tr>" );

		}.bind(this));
		htmlArray.push( "</table>" );

		var width = this.datatable.json.impExpDlgWidth || 1000;
		var height = this.datatable.json.impExpDlgHeight || 700;
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

	},
	checkCount: function(idata){
		var lp = MWF.xApplication.process.Xform.LP;

		var exceeded = false;
		var maxCount = this.datatable.json.maxCount ? this.datatable.json.maxCount.toInt() : 0;
		if( maxCount > 0 && idata.length > maxCount )exceeded = true;

		var less = false;
		var minCount = this.datatable.json.minCount ? this.datatable.json.minCount.toInt() : 0;
		if( minCount > 0 && idata.length < minCount) less = true;

		if( exceeded ) {
			var text = lp.importTooManyNotice.replace("{n1}", idata.length).replace("{n2}", this.datatable.json.maxCount);
			this.form.notice(text, "error");
			return false;
		}

		if( less ){
			var text = lp.importTooFewNotice.replace("{n1}", idata.length).replace("{n2}", this.datatable.json.minCount );
			this.form.notice(text,"error");
			return false;
		}
		return true;
	},
	checkData : function( idata ){
		var flag = true;

		var lp = MWF.xApplication.process.Xform.LP;
		var columnText =  lp.importValidationColumnText;
		var columnTextExcel = lp.importValidationColumnTextExcel;
		var excelUtil = new MWF.xApplication.process.Xform.DatatablePC.ExcelUtils( this.datatable );

		var parsedData = this.parseImportedData(idata, true);

		idata.each( function(lineData, lineIndex){

			var errorTextList = [];
			var errorTextListExcel = [];

			var parsedLineData = (parsedData && parsedData[lineIndex]) ? parsedData[lineIndex] : [];

			this.columnJsonList.each( function (obj, i) {
				var index = obj.index;
				var json = obj.mJson;
				var module = obj.module;
				var text = obj.title;

				var colInfor = columnText.replace( "{n}", index+1 );
				var colInforExcel = columnTextExcel.replace( "{n}", excelUtil.index2ColName( index ) );

				var d = lineData[text] || "";
				var parsedD = parsedLineData[json.id] || "";

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
									errorTextList.push( colInfor + obj.errorText + lp.fullstop );
									errorTextListExcel.push( colInforExcel + obj.errorText + lp.fullstop );
								}
							}.bind(this));
							break;
						case "Number":
							if (isNaN(d)){
								errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
								errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
							}
							break;
						case "Calendar":
							if( !( isNaN(d) && !isNaN(Date.parse(d) ))){
								errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop );
								errorTextListExcel.push( colInforExcel + d + lp.notValidDate + lp.fullstop );
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
							hasError = parsedD.some(function (item) { return item.errorText; })
						}
					}
					if(!hasError){
						module.setData(parsedD);
						module.validationMode();
						if (!module.validation() && module.errNode){
							errorTextList.push(colInfor + module.errNode.get("text"));
							errorTextListExcel.push( colInforExcel + module.errNode.get("text"));
							module.errNode.destroy();
						}
					}
				}
			}.bind(this));

			if(errorTextList.length>0){
				lineData.errorTextList = errorTextList;
				lineData.errorTextListExcel = errorTextListExcel;
				flag = false;
			}

		}.bind(this));

		var arg = {
			validted : flag,
			data : idata
		};
		this.datatable.fireEvent( "validImport", [arg] );

		return arg.validted;
	},
	exportWithImportDataToExcel: function(eData){
		var exporter = new MWF.xApplication.process.Xform.DatatablePC.Exporter(this.datatable);
		exporter.exportWithImportDataToExcel(eData)
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
	listAllOrgData : function (orgTitleList, iData, callback) {
		var identityList = [], personList = [], unitList = [], groupList = [];
		if( orgTitleList.length > 0 ){
			iData.each( function( lineData, lineIndex ){
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

MWF.xApplication.process.Xform.DatatablePC.ExcelUtils = new Class({
	initialize: function( datatable ){
		this.datatable = datatable;
		this.form = datatable.form;
		if (!FileReader.prototype.readAsBinaryString) {
			FileReader.prototype.readAsBinaryString = function (fileData) {
				var binary = "";
				var pt = this;
				var reader = new FileReader();
				reader.onload = function (e) {
					var bytes = new Uint8Array(reader.result);
					var length = bytes.byteLength;
					for (var i = 0; i < length; i++) {
						binary += String.fromCharCode(bytes[i]);
					}
					//pt.result  - readonly so assign binary
					pt.content = binary;
					pt.onload();
				};
				reader.readAsArrayBuffer(fileData);
			}
		}
	},
	_loadResource : function( callback ){
		if( !window.XLSX || !window.xlsxUtils ){
			var uri = "../x_component_Template/framework/xlsx/xlsx.full.js";
			var uri2 = "../x_component_Template/framework/xlsx/xlsxUtils.js";
			COMMON.AjaxModule.load(uri, function(){
				COMMON.AjaxModule.load(uri2, function(){
					callback();
				}.bind(this))
			}.bind(this))
		}else{
			callback();
		}
	},
	_openDownloadDialog: function(url, saveName){
		/**
		 * 通用的打开下载对话框方法，没有测试过具体兼容性
		 * @param url 下载地址，也可以是一个blob对象，必选
		 * @param saveName 保存文件名，可选
		 */
		if( Browser.name !== 'ie' ){
			if(typeof url == 'object' && url instanceof Blob){
				url = URL.createObjectURL(url); // 创建blob地址
			}
			var aLink = document.createElement('a');
			aLink.href = url;
			aLink.download = saveName || ''; // HTML5新增的属性，指定保存文件名，可以不要后缀，注意，file:///模式下不会生效
			var event;
			if(window.MouseEvent && typeOf( window.MouseEvent ) == "function" ) event = new MouseEvent('click');
			else
			{
				event = document.createEvent('MouseEvents');
				event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
			}
			aLink.dispatchEvent(event);
		}else{
			window.navigator.msSaveBlob( url, saveName);
		}
	},

	index2ColName : function( index ){
		if (index < 0) {
			return null;
		}
		var num = 65;// A的Unicode码
		var colName = "";
		do {
			if (colName.length > 0)index--;
			var remainder = index % 26;
			colName =  String.fromCharCode(remainder + num) + colName;
			index = (index - remainder) / 26;
		} while (index > 0);
		return colName;
	},

	upload : function ( dateColIndexArray, callback ) {
		var dateColArray = [];
		dateColIndexArray.each( function (idx) {
			dateColArray.push( this.index2ColName( idx ));
		}.bind(this))


		var uploadFileAreaNode = new Element("div");
		var html = "<input name=\"file\" type=\"file\" accept=\"csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel\" />";
		uploadFileAreaNode.set("html", html);

		var fileUploadNode = uploadFileAreaNode.getFirst();
		fileUploadNode.addEvent("change", function () {
			var files = fileNode.files;
			if (files.length) {
				var file = files.item(0);
				// if( file.name.indexOf(" ") > -1 ){
				// 	this.form.notice( MWF.xApplication.process.Xform.LP.uploadedFilesCannotHaveSpaces, "error");
				// 	return false;
				// }

				//第三个参数是日期的列
				this.importFromExcel( file, function(json){
					//json为导入的结果
					if(callback)callback(json);
					uploadFileAreaNode.destroy();
				}.bind(this), dateColArray ); //["E","F"]

			}
		}.bind(this));
		var fileNode = uploadFileAreaNode.getFirst();
		fileNode.click();
	},
	exportToExcel : function(array, fileName, colWidthArr, dateIndexArray){
		// var array = [["姓名","性别","学历","专业","出生日期","毕业日期"]];
		// array.push([ "张三","男","大学本科","计算机","2001-1-2","2019-9-2" ]);
		// array.push([ "李四","男","大学专科","数学","1998-1-2","2018-9-2" ]);
		// this.exportToExcel(array, "导出数据"+(new Date).format("db"));
		this._loadResource( function(){
			var data = window.xlsxUtils.format2Sheet(array, 0, 0, null);//偏移3行按keyMap顺序转换
			var wb = window.xlsxUtils.format2WB(data, "sheet1", undefined);
			var wopts = { bookType: 'xlsx', bookSST: false, type: 'binary' };
			var dataInfo = wb.Sheets[wb.SheetNames[0]];

			var widthArray = [];
			array[0].each( function( v, i ){ //设置标题行样式

				if( !colWidthArr )widthArray.push( {wpx: 100} );

				var at = String.fromCharCode(97 + i).toUpperCase();
				var di = dataInfo[at+"1"];
				// di.v = v;
				// di.t = "s";
				di.s = {  //设置副标题样式
					font: {
						//name: '宋体',
						sz: 12,
						color: {rgb: "#FFFF0000"},
						bold: true,
						italic: false,
						underline: false
					},
					alignment: {
						horizontal: "center" ,
						vertical: "center"
					}
				};
			}.bind(this));

			if( dateIndexArray && dateIndexArray.length ){
				dateIndexArray.each( function( value, index ){
					dateIndexArray[ index ] = this.index2ColName(value);
				}.bind(this))
			}

			for( var key in dataInfo ){
				//设置所有样式，wrapText=true 后 /n会被换行
				if( key.substr(0, 1) !== "!" ){
					var di = dataInfo[key];
					if( !di.s )di.s = {};
					if( !di.s.alignment )di.s.alignment = {};
					di.s.alignment.wrapText = true;

					if( dateIndexArray && dateIndexArray.length ){
						var colName = key.replace(/\d+/g,''); //清除数字
						var rowNum = key.replace( colName, '');
						if( rowNum > 1 && dateIndexArray.contains( colName ) ){
							//di.s.numFmt = "yyyy-mm-dd HH:MM:SS"; //日期列 两种方式都可以
							di.z = 'yyyy-mm-dd HH:MM:SS'; //日期列
						}
					}
				}

			}

			if( colWidthArr ){
				colWidthArr.each( function (w) {
					widthArray.push( {wpx: w} );
				})
			}
			dataInfo['!cols'] = widthArray; //列宽度

			this._openDownloadDialog(window.xlsxUtils.format2Blob(wb), fileName +".xlsx");
		}.bind(this))
	},
	importFromExcel : function( file, callback, dateColArray ){
		this._loadResource( function(){
			var reader = new FileReader();
			var workbook, data;
			reader.onload = function (e) {
				//var data = data.content;
				if (!e) {
					data = reader.content;
				}else {
					data = e.target.result;
				}
				workbook = window.XLSX.read(data, { type: 'binary' });
				//wb.SheetNames[0]是获取Sheets中第一个Sheet的名字
				//wb.Sheets[Sheet名]获取第一个Sheet的数据
				var sheet = workbook.SheetNames[0];
				if (workbook.Sheets.hasOwnProperty(sheet)) {
					// fromTo = workbook.Sheets[sheet]['!ref'];
					// console.log(fromTo);
					var worksheet = workbook.Sheets[sheet];

					if( dateColArray && typeOf(dateColArray) == "array" && dateColArray.length ){
						var rowCount;
						if( worksheet['!range'] ){
							rowCount = worksheet['!range'].e.r;
						}else{
							var ref = worksheet['!ref'];
							var arr = ref.split(":");
							if(arr.length === 2){
								rowCount = parseInt( arr[1].replace(/[^0-9]/ig,"") );
							}
						}
						if( rowCount ){
							for( var i=0; i<dateColArray.length; i++ ){
								for( var j=1; j<=rowCount; j++ ){
									var cell = worksheet[ dateColArray[i]+j ];
									if( cell ){
										delete cell.w; // remove old formatted text
										cell.z = 'yyyy-mm-dd'; // set cell format
										window.XLSX.utils.format_cell(cell); // this refreshes the formatted text.
									}
								}
							}
						}
					}

					var json = window.XLSX.utils.sheet_to_json( worksheet );
					//var data = window.XLSX.utils.sheet_to_row_object_array(workbook.Sheets[sheet], {dateNF:'YYYY-MM-DD'});
					if(callback)callback(json);
					// console.log(JSON.stringify(json));
					// break; // 如果只取第一张表，就取消注释这行
				}
				// for (var sheet in workbook.Sheets) {
				//     if (workbook.Sheets.hasOwnProperty(sheet)) {
				//         fromTo = workbook.Sheets[sheet]['!ref'];
				//         console.log(fromTo);
				//         var json = window.XLSX.utils.sheet_to_json(workbook.Sheets[sheet]);
				//         console.log(JSON.stringify(json));
				//         // break; // 如果只取第一张表，就取消注释这行
				//     }
				// }
			};
			reader.readAsBinaryString(file);
		})
	}
});