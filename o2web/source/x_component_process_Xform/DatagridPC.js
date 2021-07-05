/**
 * 数据网格数据结构.
 * @typedef {Object} DatagridData
 * @property {Array} data - 数据网格列表数据
 * @property {Object} total - 统计数据
 * @example
 * 	{
	  "data": [ //数据网格条目
		{
		  "datagrid_datagrid$Title": { //数据网格第1列title标识
			"org_20": {  //数据网格第1列字段标识，人员组件单个对象，存的是对象
			  "distinguishedName": "张三@bf007525-99a3-4178-a474-32865bdddec8@I",
			  "id": "bf007525-99a3-4178-a474-32865bdddec8",
			  "name": "张三",
			  "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
			  "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
			  "unitLevelName": "兰德纵横/市场营销部",
			  "unitName": "市场营销部"
			}
		  },
		  "datagrid_datagrid$Title1": { //数据网格第2列title标识
			"org_21": [{  //数据网格第2列字段标识，人员组件多个对象，存的是数组
			  "distinguishedName": "张三@bf007525-99a3-4178-a474-32865bdddec8@I",
			  "id": "bf007525-99a3-4178-a474-32865bdddec8",
			  "name": "张三",
			  "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
			  "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
			  "unitLevelName": "兰德纵横/市场营销部",
			  "unitName": "市场营销部"
			},{
			  "distinguishedName": "李四@bf007525-99a3-4178-a474-32865bdddec8@I",
			  "id": "bf007525-99a3-4178-a474-32865bdddec8",
			  "name": "李四",
			  "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
			  "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
			  "unitLevelName": "兰德纵横/市场营销部",
			  "unitName": "市场营销部"
			}]
		  },
		  "datagrid_datagrid$Title_2": { //数据网格第2列title标识
			"number": "111" //数据网格第3列字段标识和值
		  },
		  "datagrid_datagrid$Title_3": { //数据网格第3列title标识
			"textfield_2": "杭州" //数据网格第4列字段标识和值
		  },
		  "datagrid_datagrid$Title_4": { //数据网格第4列title标识
			"attachment_1": [  //数据网格第5列字段标识
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
		},
		...
	  ],
	  "total": {  //统计数据，列title设置了总计
		"datagrid_datagrid$Title_2": "333", //总计列2
		"datagrid_datagrid$Title_3": "2" //总计列3
	  }
	}
 */
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class DatagridPC 数据网格组件（PC端）。从v6.2开始建议用数据表格(Datatable)代替。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var datagrid = this.form.get("name"); //获取组件
 * //方法2
 * var datagrid = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.DatagridPC = new Class(
	/** @lends MWF.xApplication.process.Xform.DatagridPC# */
	{
		Implements: [Events],
		Extends: MWF.APP$Module,
		isEdit: false,
		options: {
			/**
			 * 当前条目编辑完成时触发。通过this.event可以获取对应的tr。
			 * @event MWF.xApplication.process.Xform.DatagridPC#completeLineEdit
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 添加条目时触发。通过this.event可以获取对应的tr。
			 * @event MWF.xApplication.process.Xform.DatagridPC#addLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 删除条目前触发。通过this.event可以获取对应的tr。
			 * @event MWF.xApplication.process.Xform.DatagridPC#deleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 删除条目后触发。
			 * @event MWF.xApplication.process.Xform.DatagridPC#afterDeleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 编辑条目时触发。
			 * @event MWF.xApplication.process.Xform.DatagridPC#editLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 导出excel的时候触发，this.event指向导出的数据，您可以通过修改this.event来修改数据。
			 * @event MWF.xApplication.process.Xform.DatagridPC#export
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
			 * @event MWF.xApplication.process.Xform.DatagridPC#validImport
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
			 * 在导入excel，数据校验成功将要设置回数据网格的时候触发，this.event指向整理过的导入数据，格式见{@link DatagridData}。
			 * @event MWF.xApplication.process.Xform.DatagridPC#import
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			"moduleEvents": ["queryLoad","postLoad","load","completeLineEdit", "addLine", "deleteLine", "afterDeleteLine","editLine", "export", "import", "validImport"]
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

			this.editable = (this.readonly || this.json.isReadonly === true ) ? false : true;
			if (this.editable && this.json.editableScript && this.json.editableScript.code){
				this.editable = this.form.Macro.exec(((this.json.editableScript) ? this.json.editableScript.code : ""), this);
			}

			this.deleteable = this.json.deleteable !== "no";
			this.addable = this.json.addable !== "no";

			//允许导入
			this.importenable  = this.editable && (this.json.impexpType === "impexp" || this.json.impexpType === "imp");
			//允许导出
			this.exportenable  = this.json.impexpType === "impexp" || this.json.impexpType === "exp";

			this.gridData = this._getValue();

			this.totalModules = [];
			this._loadDatagridTitleModules();

			if (this.editable!=false){
				this._loadDatagridDataModules();
				this._addTitleActionColumn();

				this._loadEditDatagrid(function(){
					this._loadImportExportAction();
					this.fireEvent("postLoad");
					this.fireEvent("load");
				}.bind(this));
				//this._loadReadDatagrid();
			}else{
				this._loadDatagridDataModules();
				this._getDatagridEditorTr();
				this._loadReadDatagrid(function(){
					if(this.editorTr)this.editorTr.setStyle("display", "none");
					this._loadImportExportAction();
					this.fireEvent("postLoad");
					this.fireEvent("load");
				}.bind(this));

			}
		},
		_loadStyles: function(){
			this.table.setStyles(this.json.tableStyles);
			this.node.setStyles(this.json.styles);
		},
		_getValue: function(){
			if (this.moduleValueAG) return this.moduleValueAG;
			var value = [];
			value = this._getBusinessData();
			if (!value){
				if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
				if (!value.then) if (o2.typeOf(value)=="array") value = {"data": value || []};
			}
			return value || {};
		},
		getValue: function(){
			return this._getValue();
		},
		_getDatagridTr: function(){
			this._getDatagridTitleTr();
			this._getDatagridEditorTr();
		},
		_getDatagridTitleTr: function(){
			this.titleTr = this.table.getElement("tr");
			return this.titleTr;
		},
		_getDatagridEditorTr: function(){
			var trs = this.table.getElements("tr");
			this.editorTr = trs[trs.length-1];
			this.editorTr.addClass("datagridEditorTr");

			return this.editorTr;
		},
		_addTitleActionColumn: function(){
			if (!this.titleTr) this._getDatagridTitleTr();
			if (!this.editorTr) this._getDatagridEditorTr();

			var actionTh = new Element("th", {"styles": {"width": "46px"}}).inject(this.titleTr, "top");
			new Element("th").inject(this.titleTr, "bottom");
			if( this.addable ){
				this._createAddLineAction(actionTh);
			}
			//this._createDelLineAction(actionTh);

			var actionEditTd = new Element("td").inject(this.editorTr, "top");
			this._createCompleteAction(actionEditTd);
			if( this.deleteable ){
				this._createCancelAction(actionEditTd);
			}

			new Element("td").inject(this.editorTr, "bottom");

			//if (this.totalTr){
			//    new Element("td").inject(this.totalTr, "top");
			//    new Element("td").inject(this.totalTr, "bottom");
			//    this.totalModules.each(function(m){
			//        m.index = m.index+1;
			//    });
			//}
		},

		_loadEditDatagrid: function(callback){
			var p = o2.promiseAll(this.gridData).then(function(v){
				this.gridData = v;
				if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
				this.__loadEditDatagrid(callback);
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
			// if (this.gridData && this.gridData.isAG){
			// 	this.moduleValueAG = this.gridData;
			// 	this.gridData.addResolve(function(v){
			// 		this.gridData = v;
			// 		this._loadEditDatagrid(callback);
			// 	}.bind(this));
			// }else{
			// 	if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
			// 	this.__loadEditDatagrid(callback);
			// 	this.moduleValueAG = null;
			// }
		},
		__loadEditDatagrid: function(callback){
			var titleThs = this.titleTr.getElements("th");
			var editorTds = this.editorTr.getElements("td");

			if (this.gridData.data){
				this.gridData.data.each(function(data, idx){
					var tr = $(this.table.insertRow(idx+1));
					tr.store("data", data);
					titleThs.each(function(th, index){
						var cellData = data[th.get("id")];
						var text = "";
						if( typeOf( cellData ) !== "array" ) {
							for (key in cellData) {
								var value = cellData[key];
								text = this._getValueText(index - 1, value);
								break;
							}
						}
						this._createNewEditTd(tr, index, editorTds[index].get("id"), text, titleThs.length-1, idx);
					}.bind(this));
				}.bind(this));
			}
			this.editorTr.setStyle("display", "none");
			if (callback) callback();
		},


		_getValueText: function(idx, value){

			var module = this.editModules[idx];
			if (module){
				switch (module.json.type){
					case "Select":
						for (var i=0; i<module.json.itemValues.length; i++){
							var itemv = module.json.itemValues[i];
							var arr = itemv.split(/\|/);
							var text = arr[0];
							var v = (arr.length>1) ? arr[1] : arr[0];
							if (value===v) return text;
						}
						// var ops = module.node.getElements("option");
						// for (var i=0; i<ops.length; i++){
						// 	if (ops[i].value == value){
						// 		return ops[i].get("text");
						// 		break;
						// 	}
						// }
						break;
					case "Radio":
						var ops = module.node.getElements("input");
						for (var i=0; i<ops.length; i++){
							if (ops[i].value == value){
								return ops[i].get("showText");
								break;
							}
						}
						break;
					case "Checkbox":
						var ops = module.node.getElements("input");
						var texts = [];
						for (var i=0; i<ops.length; i++){
							if (value.indexOf(ops[i].value) != -1) texts.push(ops[i].get("showText"));
						}
						if (texts.length) return texts.join(", ");
						break;
					case "Orgfield":
					case "Personfield":
					case "Org":
						//var v = module.getTextData();
						//return v.text[0];

						if (typeOf(value)==="array"){
							var textArray = [];
							value.each( function( item ){
								if (typeOf(item)==="object"){
									textArray.push( item.name+((item.unitName) ? "("+item.unitName+")" : "") );
								}else{
									textArray.push(item);
								}
							}.bind(this));
							return textArray.join(", ");
						}else if (typeOf(value)==="object"){
							return value.name+((value.unitName) ? "("+value.unitName+")" : "");
						}else{
							return value;
						}

						break;
					case "Textarea":
						var reg = new RegExp("\n","g");
						var reg2 = new RegExp("\u003c","g"); //尖括号转义，否则内容会截断
						var reg3 = new RegExp("\u003e","g");
						value = value.replace(reg2,"&lt").replace(reg3,"&gt").replace(reg,"<br/>");
						break;
					// case "address":
					// 	if (typeOf(value)==="array"){
					//
					// 	}
					// 	break;
				}
			}
			return value;
		},

		_createNewEditTd: function(tr, idx, id, text, lastIdx, rowIndex){
			var cell = $(tr.insertCell(idx));
			if (idx==0){
				cell.setStyles(this.form.css.gridLineActionCell);
				if( this.addable )this._createAddLineAction(cell);
				if( this.deleteable )this._createDelLineAction(cell);
			}else if (idx == lastIdx){
				cell.setStyles(this.form.css.gridMoveActionCell);
				this._createMoveLineAction(cell);
			}else{
				cell.set("MWFId", id);

				var module = this.editModules[idx-1];
				if( module && module.json.type == "ImageClipper" ){
					this._createImage( cell, module, text )
				}else if( module && module.json.type == "Image" ) {
					this._createImg(cell, module, rowIndex);
				}else if( module && module.json.type == "Button" ) {
					this._createButton(cell, module, rowIndex);
				}else if( module && module.json.type == "Label" ) {
					this._createLabel(cell, module, rowIndex);
				}else if( module && (module.json.type == "Attachment" || module.json.type == "AttachmentDg") ){
					this._createAttachment( cell, module, text );
				}else{
					if( module && module.json.type == "Textarea" ){
						cell.set("html", text);
					}else{
						cell.set("text", text);
					}
					// /cell.set("text", text);
				}
				if( !module ||  !["Button"].contains( module.json.type ) ){
					cell.addEvent("click", function(e){
						this._editLine(e.target);
					}.bind(this));
				}
			}
			var json = this.form._getDomjson(cell);

			if (json){
				cell.store("dataGrid", this);
				var module = this.form._loadModule(json, cell);
				cell.store("module", module);
				this.form.modules.push(module);
				if( json.isShow === false )cell.hide();
			}

		},

		_createAddLineAction: function(td){
			var addLineAction = new Element("div", {
				"styles": this.form.css.addLineAction,
				"events": {
					"click": function(e){
						this._addLine(e.target);
					}.bind(this)
				}
			});
			addLineAction.inject(td);
		},
		_createDelLineAction: function(td){
			var delLineAction = new Element("div", {
				"styles": this.form.css.delLineAction,
				"events": {
					"click": function(e){
						this._deleteLine(e);
					}.bind(this)
				}
			});
			delLineAction.inject(td);
		},
		_createCompleteAction: function(td){
			var completeAction = new Element("div", {
				"styles": this.form.css.completeLineAction,
				"events": {
					"click": function(e){
						this._completeLineEdit(e);
					}.bind(this)
				}
			});
			completeAction.inject(td);
		},
		_createCancelAction: function(td){
			var cancelAction = new Element("div", {
				"styles": this.form.css.delLineAction,
				"events": {
					"click": function(e){
						this._cancelLineEdit(e);
					}.bind(this)
				}
			});
			cancelAction.inject(td);
		},

		_editLine:function(td){
			if (this.isEdit){
				if (!this._completeLineEdit()) return false;
			}

			this.currentEditLine = td.getParent("tr");
			if (this.currentEditLine){
				this.editorTr.setStyles({
					//"background-color": "#fffeb5",
					"display": "table-row"
				});
				this.editorTr.inject(this.currentEditLine, "before");
				this.currentEditLine.setStyle("display", "none");

				var data = this.currentEditLine.retrieve("data");
				var titleThs = this.titleTr.getElements("th");
				titleThs.each(function(th, idx){
					var id = th.get("id");
					var module = this.editModules[idx-1];
					if (module){
						if (module.json.type=="sequence"){
							module.node.set("text", module.node.getParent("tr").rowIndex);
						}else if( module.setData ){

							if (data[id]) {
								module.setData(data[id][module.json.id]);
							} else {
								module.setData(null);
							}
						}
					}
				}.bind(this));



				var cellIdx = this.currentEditLine.getElements("td").indexOf(td);
				var module = this.editModules[cellIdx-1];
				if (module) module.focus();


				this.fireEvent("editLine");

				this.isEdit =true;
			}
			this.validationMode();
		},
		editValidation: function(){
			var flag = true;
			this.editModules.each(function(field, key){
				if (field.json.type!="sequence" && field.validationMode ){
					field.validationMode();
					if (!field.validation()) flag = false;
				}
			}.bind(this));
			return flag;
		},

		// _cancelLineEdit: function(e){
		// 	this.isEdit = false;
		//
		// 	var flag = true;
		//
		// 	var griddata = {};
		// 	var newTr = null;
		//
		// 	if (this.currentEditLine){
		// 		newTr = this.currentEditLine;
		// 		griddata = this.currentEditLine.retrieve("data");
		// 	}else{
		// 		newTr = new Element("tr").inject(this.editorTr, "before");
		// 		griddata = {};
		// 	}
		//
		// 	if (flag){
		// 		newTr.destroy();
		// 	}
		// 	this.currentEditLine = null;
		//
		// 	this._editorTrGoBack();
		//
		// 	// if (this.json.contentStyles){
		// 	// 	var tds = newTr.getElements("td");
		// 	// 	tds.setStyles(this.json.contentStyles);
		// 	// }
		// 	// if (this.json.actionStyles){
		// 	// 	newTr.getFirst().setStyles(this.json.actionStyles);
		// 	// }
		//
		// 	// this._loadBorderStyle();
		// 	// this._loadZebraStyle();
		// 	// this._loadSequence();
		//
		// 	this.fireEvent("cancelLineEdit");
		// },
		_cancelLineEdit: function(e){

			var datagrid = this;
			this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.cancelDatagridLineEditTitle, MWF.xApplication.process.Xform.LP.cancelDatagridLineEdit, 300, 120, function(){
				if (datagrid.currentEditLine) {
					datagrid.currentEditLine.setStyle("display", "table-row");
				}

				datagrid.editModules.each(function(module){
					if (module && (module.json.type=="Attachment" || module.json.type=="AttachmentDg")){
						module.attachmentController.attachments.each(function(att){
							datagrid.form.workAction.deleteAttachment(att.data.id, datagrid.form.businessData.work.id);
						});
						module.attachmentController.clear();
					}
				});

				datagrid.isEdit = false;
				datagrid.currentEditLine = null;

				datagrid._editorTrGoBack();

				// this._loadBorderStyle();
				// this._loadZebraStyle();
				// this._loadSequence();
				// this.getData();

				// datagrid._loadZebraStyle();
				// datagrid._loadSequence();
				// datagrid._loadTotal();
				// datagrid.getData();
				this.close();

				datagrid.fireEvent("cancelLineEdit");
			}, function(){
				// var color = currentTr.retrieve("bgcolor");
				// currentTr.tween("background", color);
				this.close();
			}, null, null, this.form.json.confirmStyle);

		},
		_completeLineEdit: function( ev ){

			debugger;

			//this.currentEditLine.getElemets(td);
			if (!this.editValidation()){
				return false;
			}

			this.isEdit = false;

			var flag = true;
			var saveFlag = false;

			var griddata = {};
			var newTr = null;

			if (this.currentEditLine){
				newTr = this.currentEditLine;
				griddata = this.currentEditLine.retrieve("data");
			}else{
				newTr = new Element("tr").inject(this.editorTr, "before");
				griddata = {};
			}

			var titleThs = this.titleTr.getElements("th");
			var editorTds = this.editorTr.getElements("td");
			var cells = newTr.getElements("td");
			titleThs.each(function(th, idx){
				var cell = cells[idx];
				var id = th.get("id");
				var module = this.editModules[idx-1];
				if (module){
					if (module.json.type=="sequence"){
						flag = false;
						var i = newTr.rowIndex;
						var data = {"value": [i], "text": [i]};
					}else if (module.json.type=="Attachment" || module.json.type == "AttachmentDg"){
						saveFlag = true;
						flag = false;
						var data = module.getTextData();
						//data.site = module.json.site;
						if (!griddata[id]) griddata[id] = {};
						griddata[id][module.json.id] = data;
						// }else if( ["Orgfield","Personfield","Org","Address"].contains(module.json.type) ){
						// 	data = module.getTextData();
						// 	if( data.value && data.value.length )flag = false;
						// 	if (!griddata[id]) griddata[id] = {};
						// 	griddata[id][module.json.id] = data.value;
					}else if( module.getTextData ){
						var data = module.getTextData();
						if (data.value[0]) flag = false;
						if (data.value.length<2){
							if (!griddata[id]) griddata[id] = {};
							griddata[id][module.json.id] = data.value[0];
						}else{
							if (!griddata[id]) griddata[id] = {};
							griddata[id][module.json.id] = data.value;
						}
					}

					if( data ){
						if (cell){
							if( module.json.type == "ImageClipper" ){
								this._createImage( cell, module, data.text[0] );
							}else if( module.json.type == "Attachment" || module.json.type == "AttachmentDg" ){
								this._createAttachment( cell, module, data );
							}else{
								var text = this._getValueText(idx-1, data.text.join(", "));
								if( module.json.type == "Textarea"){
									cell.set("html", text);
								}else{
									cell.set("text", data.text.join(", "));
								}
							}
						}else{
							if( module.json.type == "Attachment" || module.json.type == "AttachmentDg" ){
								this._createNewEditTd(newTr, idx, editorTds[idx].get("id"), data, titleThs.length-1);
							}else{
								var text = this._getValueText(idx-1, data.text.join(", "));
								this._createNewEditTd(newTr, idx, editorTds[idx].get("id"), text, titleThs.length-1);
							}
						}
					}else{
						if (!cell) this._createNewEditTd(newTr, idx, id, "", titleThs.length-1);
					}
				}else{
					if (!cell) this._createNewEditTd(newTr, idx, id, "", titleThs.length-1);
				}
				module = null;
			}.bind(this));

			newTr.store("data", griddata);
			newTr.setStyle("display", "table-row");

			if (flag){
				newTr.destroy();
			}
			this.currentEditLine = null;

			this._editorTrGoBack();

			if (this.json.contentStyles){
				var tds = newTr.getElements("td");
				tds.setStyles(this.json.contentStyles);
			}
			if (this.json.actionStyles){
				newTr.getFirst().setStyles(this.json.actionStyles);
			}

			this._loadBorderStyle();
			this._loadZebraStyle();
			this._loadSequence();
			this.getData();
			this.validationMode();
			this.fireEvent("completeLineEdit", [newTr]);

			if( saveFlag ){
				this.form.saveFormData();
			}

			return true;
		},
		// _createImg : function(cell, module, idx){
		// 	cell.empty();
		// 	if( module.node ){
		// 		var node = module.node.clone();
		// 		node.set("id", module.json ? (module.json.id +"_"+idx) : "" );
		// 		node.inject(cell);
		// 	}
		// },
		_createImg : function(cell, module, idx){
			this._cloneModule(cell, module, idx);
		},
		_createLabel : function(cell, module, idx){
			this._cloneModule(cell, module, idx);
		},
		_createButton : function(cell, module, idx){
			this._cloneModule(cell, module, idx);
		},
		_cloneModule : function(cell, module, idx){
			debugger;
			cell.empty();
			if( module.node && module.json ){
				var json = Object.clone( module.json );
				json.id = json.id +"_"+idx;

				var node = module.node.clone();
				node.set("id", json.id);
				node.inject(cell);

				this.form._loadModule(json, node)
			}
		},
		_createImage : function( cell, module, data ){
			cell.empty();
			if( !data )return;
			var img = new Element("img",{
				src : MWF.xDesktop.getImageSrc( data )
			}).inject( cell, "top" );
			if( module.json.clipperType == "size" ){
				var width = module.json.imageWidth;
				var height = module.json.imageHeight;
				if (width && height) {
					img.setStyles({
						width: width + "px",
						height: height + "px"
					})
				}
			}
		},
		_createAttachment: function ( cell, module, data ){
			cell.empty();
			var options = {
				"style": module.json.style || "default",
				"title": MWF.xApplication.process.Xform.LP.attachmentArea,
				"listStyle": module.json.dg_listStyle || "icon",
				"size": module.json.dg_size || "min",
				"resize": (module.json.dg_resize === "y" || this.json.dg_resize === "true"),
				"attachmentCount": 0,
				"isUpload": false,
				"isDelete": false,
				"isReplace": false,
				"isDownload": true,
				"isSizeChange": (module.json.dg_isSizeChange === "y" || module.json.dg_isSizeChange === "true"),
				"readonly": true,
				"availableListStyles": module.json.dg_availableListStyles ? module.json.dg_availableListStyles : ["list", "seq", "icon", "preview"],
				"isDeleteOption": "n",
				"isReplaceOption": "n",
				"toolbarGroupHidden": module.json.dg_toolbarGroupHidden || []
			};
			if (this.readonly) options.readonly = true;
			if(!this.editable && !this.addable)options.readonly = true;

			var atts = [];
			( data || [] ).each(function(d){
				var att = module.attachmentController.attachments.find(function(a){
					return d.id == a.data.id;
				});
				if (att) module.attachmentController.removeAttachment(att);
			});
			module.setAttachmentBusinessData();


			var attachmentController = new MWF.xApplication.process.Xform.AttachmentController(cell, module, options);
			attachmentController.load();

			( data || [] ).each(function (att) {
				var attachment = this.form.businessData.attachmentList.find(function(a){
					return a.id==att.id;
				});
				var attData = attachment || att;
				//if (att.site===this.json.id || (this.json.isOpenInOffice && this.json.officeControlName===att.site)) this.attachmentController.addAttachment(att);
				attachmentController.addAttachment(attData);
			}.bind(this));
		},
		_editorTrGoBack: function(){
			this.editorTr.setStyle("display", "none");
//		this.editTr.removeEvents("blur");
			if (this.totalTr){
				this.editorTr.inject(this.totalTr, "before");
			}else{
				var lastTrs = this.table.getElements("tr");
				var lastTr = lastTrs[lastTrs.length-1];
				this.editorTr.inject(lastTr, "after");
			}
		},
		_addLine: function(node){
			if (this.isEdit){
				if (!this._completeLineEdit()) return false;
			}
			this.editorTr.setStyles({
				//"background-color": "#fffeb5",
				"display": "table-row"
			});
			this.currentEditLine = null;
			var currentTr = node.getParent("tr");
			if (currentTr){
				this.editorTr.inject(currentTr, "after");
			}
			this.isEdit =true;
			this.validationMode();
			this.fireEvent("addLine",[this.editorTr]);
//		newTr.addEvent("blur", function(e){
//			this._completeLineEdit();
//		}.bind(this));
		},
		_deleteLine: function(e){
			var saveFlag = false;
			var currentTr = e.target.getParent("tr");
			if (currentTr){
				var color = currentTr.getStyle("background");
				currentTr.store("bgcolor", color);
				currentTr.tween("background-color", "#ffd4d4");
				var datagrid = this;
				var _self = this;
				this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
					_self.fireEvent("deleteLine", [currentTr]);

					var data = currentTr.retrieve("data");

					//var attKeys = [];
					var titleThs = _self.titleTr.getElements("th");
					titleThs.each(function(th, i){
						var key = th.get("id");
						var module = (i>0) ? _self.editModules[i-1] : null;
						if (key && module && (module.json.type=="Attachment" || module.json.type=="AttachmentDg")){
							data[key][module.json.id].each(function(d){
								_self.form.workAction.deleteAttachment(d.id, _self.form.businessData.work.id);
							});
							saveFlag = true;
						}
					});

					currentTr.destroy();
					datagrid._loadZebraStyle();
					datagrid._loadSequence();
					datagrid._loadTotal();
					datagrid.getData();
					this.close();

					_self.fireEvent("afterDeleteLine");

					if(saveFlag){
						_self.form.saveFormData();
					}
				}, function(){
					var color = currentTr.retrieve("bgcolor");
					currentTr.tween("background", color);
					this.close();
				}, null, null, this.form.json.confirmStyle);
			}
			this.validationMode();
		},
		_createMoveLineAction: function(td){
			var moveLineAction = new Element("div", {
				"styles": this.form.css.moveLineAction,
				"events": {
					"mousedown": function(e){
						this._moveLine(e);
					}.bind(this)
				}
			});
			moveLineAction.inject(td);
		},
		_getMoveDragNode: function(tr){
			var table = tr.getParent("table");
			var div = table.getParent("div");
			var size = div.getSize();
			var dragNode = div.clone().setStyle("width", size.x).inject(document.body);
			var dragtable = dragNode.getElement("table");
			dragtable.empty();

			var clone = tr.clone().setStyles(this.form.css.gridMoveLineDragNodeTr).inject(dragtable);
			var tds = tr.getElements("td");
			var clonetds = clone.getElements("td");
			tds.each(function(td, idx){
				var size = td.getComputedSize();
				clonetds[idx].setStyle("width", size.width+1);
			});

			var coordinates = tr.getCoordinates();
			dragNode.setStyles(this.form.css.gridMoveLineDragNode);
			dragNode.setStyles(coordinates);

			return dragNode;
		},
		_moveLine: function(e){
			var trs = this.table.getElements("tr");
			var div = e.target;
			var tr = div.getParent("tr");

			var dragNode = this._getMoveDragNode(tr);
			coordinates = dragNode.getCoordinates();
			var dragTr = dragNode.getElement("tr");
			var dragTable = dragNode.getElement("table");

			var color = tr.getStyle("background");
			tr.store("bgcolor", color);
			//tr.tween('background-color', '#f3f1ad');
			tr.tween('background-color', '#e4f6e9');


			var drag = new Drag.Move(dragNode, {
				"droppables": trs.erase(tr),
				"limit": {"x": [coordinates.left, coordinates.left]},
				onDrop: function(dragging, droppable){
					dragging.destroy();
					//debugger;
					var color = tr.retrieve("bgcolor");
					if (color){
						tr.tween("background", color);
					}else{
						tr.tween("background", "transparent");
					}
					//if (droppable){
					//    color = droppable.retrieve("bgcolor");
					//    if (color){
					//        droppable.tween("background", color);
					//    }else{
					//        droppable.tween("background", "transparent");
					//    }
					//}

					tr.setStyle("display", "table-row");
					if (droppable != null){
						tr.inject(dragTr, "after");
						dragTr.destroy();
						//this._loadZebraStyle();
						//this._loadSequence();
						//this._loadTotal();
						this._loadDatagridStyle();
						this.getData();
					}

				}.bind(this),
				"onEnter": function(dragging, drop){
					//var color = drop.getStyle("background");
					//if (color.toUpperCase()!='#d1eaf3') drop.store("bgcolor", color);
					//drop.tween("background-color", "#d1eaf3");
					dragNode.setStyle("display", "none");
					dragTr.inject(drop, "after");
				},
				"onLeave": function(dragging, drop){
					//var color = drop.retrieve("bgcolor");
					//if (color){
					//	drop.tween("background", color);
					//}else{
					//	drop.tween("background", "transparent");
					//}
					dragTr.inject(dragTable);
					dragNode.setStyle("display", "block");
//				tr.setStyle("display", "table-row");
				},
				"ondrag": function(){
					this.table.setStyle("cursor", "move");
					dragNode.setStyle("cursor", "move");
				},
				"onCancel": function(dragging){
					dragging.destroy();
					var color = tr.retrieve("bgcolor");
					if (color){
						tr.tween("background", color);
					}else{
						tr.tween("background", "transparent");
					}
					tr.setStyle("display", "table-row");
				}
			});
			drag.start(e);
			tr.setStyle("display", "none");
		},
		_loadReadDatagrid: function(callback){
			var p = o2.promiseAll(this.gridData).then(function(v){
				this.gridData = v;
				if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
				this.__loadReadDatagrid(callback);
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

			// if (this.gridData && this.gridData.isAG){
			// 	this.moduleValueAG = this.gridData;
			// 	this.gridData.addResolve(function(v){
			// 		this.gridData = v;
			// 		this._loadReadDatagrid(callback);
			// 	}.bind(this));
			// }else{
			// 	if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
			// 	this.__loadReadDatagrid(callback);
			// 	this.moduleValueAG = null;
			// }
		},

		__loadReadDatagrid: function(callback){
			//this.gridData = this._getValue();
			if (!this.titleTr) this._getDatagridTitleTr();
			//var titleTr = this.table.getElement("tr");
			var titleHeaders = this.titleTr.getElements("th");

			var lastTrs = this.table.getElements("tr");
			var lastTr = lastTrs[lastTrs.length-1];
			//var tds = lastTr.getElements("td");

			debugger;

			if (this.gridData.data){
				this.gridData.data.each(function(data, idx){
					var tr = this.table.insertRow(idx+1);
					tr.store("data", data);

					titleHeaders.each(function(th, index){
						var cell = tr.insertCell(index);
						// cell.set("MWFId", tds[index].get("id"));
						var cellData = data[th.get("id")];

						var module = this.editModules[index];
						if( typeOf( cellData ) !== "array" ){
							if (cellData) {
								for (key in cellData) {
									var v = cellData[key];
									if (module && module.json.type == "ImageClipper") {
										this._createImage(cell, module, v);
									} else if (module && (module.json.type == "Attachment" || module.json.type == "AttachmentDg")) {
										this._createAttachment(cell, module, v);
									} else {
										var text = this._getValueText(index, v);
										if (module && module.json.type == "Textarea") {
											cell.set("html", text);
										} else {
											cell.set("text", text);
										}
									}
									break;
								}
							}else if( module && module.json.type == "Image" ) {
								this._createImg(cell, module, idx);
							}else if( module && module.json.type == "Button" ) {
								this._createButton(cell, module, idx);
							}else if( module && module.json.type == "Label" ) {
								this._createLabel(cell, module, idx);
							}else if( module && module.json.type == "sequence" ){ //Sequence
								cell.setStyle("text-align", "center");
								cell.set("text", tr.rowIndex);
							}
						}


						var json = this.form._getDomjson(th);
						if( json && json.isShow === false )cell.hide();
					}.bind(this));
				}.bind(this));
			}
			this._loadTotal();

			if (callback) callback();
		},

		_loadImportExportAction: function(){
			debugger;
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

		_loadDatagridStyle: function(){
			//var ths = this.titleTr.getElements("th");
			//ths.setStyles(this.form.css.datagridTitle);
			this.loadGridTitleStyle();
			this.loadGridContentStyle();
			this.loadGridActionStyle();
			this.loadGridEditStyle();

			this._loadTotal();
			this._loadBorderStyle();
			this._loadZebraStyle();
			this._loadSequence();
		},
		loadGridEditStyle: function(){
			if (this.editorTr){
				if (this.json.editStyles){
					var tds = this.editorTr.getElements("td");
					tds.setStyles(this.json.editStyles);
				}
			}
		},
		loadGridActionStyle: function(){
			if (this.editable!=false){
				if (this.json.actionStyles){
					var trs = this.table.getElements("tr");
					trs.each(function(tr, idx){
						if (idx != 0) tr.getFirst().setStyles(this.json.actionStyles);
					}.bind(this));
				}
			}
		},
		loadGridTitleStyle: function(){
			if (this.json.titleStyles){
				var ths = this.titleTr.getElements("th");
				ths.setStyles(this.json.titleStyles);
			}
		},
		loadGridContentStyle: function(){
			if (this.json.contentStyles){
				var tds = this.table.getElements("td");
				tds.setStyles(this.json.contentStyles);
			}
		},

		_loadZebraStyle: function(){
			var trs = this.table.getElements("tr");
			for (var i=1; i<trs.length; i++){
				if (!trs[i].hasClass("datagridTotalTr")){
					if (this.json.backgroundColor) trs[i].setStyle("background-color", this.json.backgroundColor);
					if ((i%2)==0){
						if (this.json.zebraColor) trs[i].setStyle("background-color", this.json.zebraColor);
					}
				}
			}
		},
		createTotalTr: function(){
			var trs = this.node.getElements("tr");
			var lastTr = trs[trs.length-1];
			this.totalTr = new Element("tr.datagridTotalTr", {"styles": this.form.css.datagridTotalTr}).inject(lastTr, "after");
			var ths = this.node.getElements("th");
			ths.each(function(th, idx){
				var td = new Element("td", {"text": "", "styles": this.form.css.datagridTotalTd}).inject(this.totalTr);
				if (this.json.amountStyles) td.setStyles(this.json.amountStyles);

				var json = this.form._getDomjson(th);
				if( json && json.isShow === false )td.hide();
			}.bind(this));
		},
		_loadTotal: function(){
			var data = {};
			this.totalResaults = {};
			if (this.totalModules.length){
				if (!this.totalTr){
					this.createTotalTr();
				}

				var totalResaults = [];
				//this.totalModules.each(function(m. i){
				//    totalResaults.push(0);
				//}.bind(this));

				var trs = this.table.getElements("tr");
				var totalTds = this.totalTr.getElements("td");

				for (var i=1; i<trs.length; i++){
					if (!trs[i].hasClass("datagridTotalTr") && (!trs[i].hasClass("datagridEditorTr"))){
						var cells = trs[i].getElements("td");

						this.totalModules.each(function(m, i){
							if (!totalResaults[i]) totalResaults.push(0);
							var tmpV = new Decimal(totalResaults[i]);
							if (m.type=="number"){
								var cell = cells[m.index];
								var addv = cell.get("text").toFloat();
								tmpV = tmpV.plus(addv||0);
								//tmpV = tmpV + addv;
							}
							if (m.type=="count"){
								tmpV = tmpV.plus(1);
								//tmpV = tmpV+1;
							}
							totalResaults[i] = tmpV.toString();
							data[m.module.json.id] = totalResaults[i];

						}.bind(this));
					}
				}

				this.totalModules.each(function(m, i){
					this.totalResaults[m.module.json.id] = totalResaults[i];
					var td = totalTds[m.index];
					td.set("text", isNaN( totalResaults[i] ) ? "" : totalResaults[i] );
				}.bind(this));
			}
			return data;
		},
		_loadSequence: function(){
			var trs = this.table.getElements("tr");
			for (var i=1; i<trs.length; i++){
				var cells = trs[i].getElements("td");
				cells.each(function(cell){
					var module = cell.retrieve("module");
					if (module){
						if (module.json.cellType=="sequence"){
							cell.set("text", i)
						}
					}
				}.bind(this));
			}
		},

		_loadBorderStyle: function(){
			if (this.json.border){
				this.table.setStyles({
					"border-top": this.json.border,
					"border-left": this.json.border
				});
				var ths = this.table.getElements("th");
				ths.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border
				});
				var tds = this.table.getElements("td");
				tds.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border,
					"background": "transparent"
				});
			}
		},
		_loadDatagridTitleModules: function(){
			var ths = this.node.getElements("th");
			ths.each(function(th){
				var json = this.form._getDomjson(th);
				th.store("dataGrid", this);
				if (json){
					var module = this.form._loadModule(json, th);
					this.form.modules.push(module);
					if( json.isShow === false )th.hide();
				}
			}.bind(this));
		},
		_loadDatagridDataModules: function(){
			var tds = this.node.getElements("td");
			tds.each(function(td){
				var json = this.form._getDomjson(td);
				td.store("dataGrid", this);
				if (json){
					var isField = false;
					var module = this.form._loadModule(json, td, function(){
						isField = this.field;
						this.field = false;
					});
					if( isField ){
						module.node.setStyle("padding-right","0px");
					}
					td.store("module", module);
					this.form.modules.push(module);
					if( json.isShow === false )td.hide();
				}
			}.bind(this));
		},
		_afterLoaded: function(){
			if (this.moduleValueAG){
				this.moduleValueAG.then(function(){
					this._loadDatagridStyle();
				}.bind(this));
			}else{
				this._loadDatagridStyle();
			}
		},
		/**
		 * @summary 重置数据网格的值为默认值或置空。
		 *  @example
		 * this.form.get('fieldId').resetData();
		 */
		resetData: function(){
			this.setData(this._getValue());
		},
		/**当参数为Promise的时候，请查看文档: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|使用Promise处理表单异步}<br/>
		 * 当表单上没有对应组件的时候，可以使用this.data[fieldId] = data赋值。
		 * @summary 为数据网格赋值。
		 * @param data{DatagridData|Promise|Array} 必选，数组或Promise.
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
			}
			this._setData(data);
		},
		_setData: function(data){
			var p = o2.promiseAll(this.data).then(function(v){
				this.gridData = v;
				if (o2.typeOf(data)=="array") data = {"data": data};
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

			// if (data && data.isAG){
			// 	this.moduleValueAG = data;
			// 	data.addResolve(function(v){
			// 		this._setData(v);
			// 	}.bind(this));
			// }else{
			// 	if (o2.typeOf(data)=="array") data = {"data": data};
			// 	this.__setData(data);
			// 	this.moduleValueAG = null;
			// }
		},
		__setData: function(data){
			// if( typeOf( data ) === "object" && typeOf(data.data) === "array"  ){
			this._setBusinessData(data);
			this.gridData = data;

			// if (this.isEdit) this._completeLineEdit();
			if( this.isEdit ){ //如果有在编辑的，取消编辑行
				if (this.currentEditLine) {
					this.currentEditLine.setStyle("display", "table-row");
				}
				this.isEdit = false;
				this.currentEditLine = null;
				this._editorTrGoBack();
			}

			if (this.gridData){
				var trs = this.table.getElements("tr");
				for (var i=1; i<trs.length-1; i++){
					var tr = trs[i];
					if( tr.hasClass("datagridEditorTr") )continue;
					var tds = tr.getElements("td");
					for (var j=0; j<tds.length; j++){
						var td = tds[j];
						var module = td.retrieve("module");
						if (module){
							this.form.modules.erase(module);
							module = null;
						}
					}
				}
				for (var i=1; i<trs.length-1; i++){
					if( trs[i].hasClass("datagridTotalTr") )continue;
					if( trs[i].hasClass("datagridEditorTr") )continue;
					trs[i].destroy();
				}
				//while (this.table.rows.length>2){
				//this.table.rows[1].destroy();
				//}
				if (this.editable!=false){
					this._loadEditDatagrid();
					//this._loadReadDatagrid();
				}else{
					this._loadReadDatagrid();
				}
				this._loadDatagridStyle();
			}
		},
		/**
		 * @summary 获取总计数据.
		 * @example
		 * var totalObject = this.form.get('fieldId').getTotal();
		 * @return {Object} 总计数据
		 */
		getTotal: function(){
			this._loadTotal();
			return this.totalResaults;
		},
		/**
		 * @summary 判断数据网格是否为空.
		 * @example
		 * if( this.form.get('fieldId').isEmpty() ){
		 *     this.form.notice('至少需要添加一条数据', 'warn');
		 * }
		 * @return {Boolean} 是否为空
		 */
		isEmpty: function(){
			var data = this.getData();
			if( !data )return true;
			if( typeOf( data ) === "object" ){
				if( typeOf( data.data ) !== "array" )return true;
				if( data.data.length === 0 )return true;
			}
			return false;
		},

		/**
		 * 在脚本中使用 this.data[fieldId] 也可以获取组件值。
		 * 区别如下：<br/>
		 * 1、当使用Promise的时候<br/>
		 * 使用异步函数生成器（Promise）为组件赋值的时候，用getData方法立即获取数据，可能返回修改前的值，当Promise执行完成以后，会返回修改后的值。<br/>
		 * this.data[fieldId] 立即获取数据，可能获取到异步函数生成器，当Promise执行完成以后，会返回修改后的值。<br/>
		 * {@link https://www.yuque.com/o2oa/ixsnyt/ws07m0#EggIl|具体差异请查看链接}<br/>
		 * 2、当表单上没有对应组件的时候，可以使用this.data[fieldId]获取值，但是this.form.get('fieldId')无法获取到组件。
		 * @summary 获取数据网格数据.
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
		 * @return {DatagridData}
		 */
		getData: function(){
			if (this.editable!=false){
				if (this.isEdit) this._completeLineEdit();
				var data = [];
				var trs = this.table.getElements("tr");
				for (var i=1; i<trs.length-1; i++){
					var tr = trs[i];
					var d = tr.retrieve("data");
					if (d) data.push(d);
				}

				this.gridData = {};
				this.gridData.data = data;

				this._loadTotal();
				this.gridData.total = this.totalResaults;

				this._setBusinessData(this.gridData);

				return (this.gridData.data.length) ? this.gridData : {data:[]};
			}else{
				return this._getBusinessData();
			}
		},
		getAmount: function(){
			return this._loadTotal();
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
				var n = this.getData();
				if( typeOf(n)==="object" && JSON.stringify(n) === JSON.stringify({data:[]}) )n = "";
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
			if (flag.toString()!="true"){
				this.notValidationMode(flag);
				return false;
			}
			return true;
		},
		getAttachmentRandomSite: function(){
			var i = (new Date()).getTime();
			return this.json.id+i;
		},


		isAvaliableImpExpColumn : function(thJson, module, type){
			if (thJson && ( thJson.isShow === false || thJson.isImpExp === false ))return false; //隐藏列，不允许导入导出
			if (module && (module.json.type == "sequence" || module.json.cellType == "sequence") )return false; //序号列
			if (module && ["Image","Button","ImageClipper","Attachment","AttachmentDg","Label"].contains(module.json.type) )return false; //图片，附件,Label列不导入导出
			// if (type==="import" && module && ["Label"].contains(module.json.type))return false; //Label 不导入
			return true;
		},
		getExportColWidthArray : function(){
			var titleThs = this.titleTr.getElements("th");
			var colWidthArr = [];
			titleThs.each(function(th, index){
				if ( this.editable && (index===0 || index === titleThs.length-1) )return; //第一列操作列和最后一列排序列

				var thJson = this.form._getDomjson( th );
				var module = this.editModules[this.editable ? (index-1) : index];
				if ( this.isAvaliableImpExpColumn( thJson, module ) ) {
					if (module && ["Org","Reader","Author","Personfield","Orgfield"].contains(module.json.type)) {
						colWidthArr.push(340);
					} else if (module && module.json.type === "Address") {
						colWidthArr.push(170);
					} else if (module && module.json.type === "Textarea") {
						colWidthArr.push(260);
					} else if (module && module.json.type === "Htmleditor") {
						colWidthArr.push(500);
					} else if (module && module.json.type === "Calendar") {
						colWidthArr.push(150);
					} else {
						colWidthArr.push(150);
					}
				}
			}.bind(this));
			return colWidthArr;
		},
		getExportDateIndexArray : function(){
			var titleThs = this.titleTr.getElements("th");
			var dateIndexArr = []; //日期格式列下标
			var idx=0;
			titleThs.each(function(th, index){
				if ( this.editable && (index===0 || index === titleThs.length-1) )return; //第一列操作列和最后一列排序列
				var thJson = this.form._getDomjson( th );
				var module = this.editModules[this.editable ? (index-1) : index];
				if ( this.isAvaliableImpExpColumn( thJson, module ) ) {
					if (module && module.json.type === "Calendar") {
						dateIndexArr.push(idx);
					}
					idx++;
				}
			}.bind(this));
			return dateIndexArr;
		},
		getExportTitleArray : function( type ){
			var titleThs = this.titleTr.getElements("th");
			var titleArr = [];
			titleThs.each(function(th, index){
				if ( this.editable && (index===0 || index === titleThs.length-1) )return; //第一列操作列和最后一列排序列
				var thJson = this.form._getDomjson( th );
				var module = this.editModules[this.editable ? (index-1) : index];
				if ( this.isAvaliableImpExpColumn( thJson, module, type ) ) {
					titleArr.push(th.get("text"));
				}
			}.bind(this));
			return titleArr;
		},

		exportToExcel : function () {
			debugger;
			var titleThs = this.titleTr.getElements("th");
			// var editorTds = this.editorTr.getElements("td");

			var resultArr = [];

			var titleArr = this.getExportTitleArray();
			var colWidthArr = this.getExportColWidthArray();
			var dateIndexArr = this.getExportDateIndexArray(); //日期格式列下标

			// var idx=0;
			// titleThs.each(function(th, index){
			// 	if ( this.editable && (index===0 || index === titleThs.length-1) )return; //第一列操作列和最后一列排序列
			//
			// 	var thJson = this.form._getDomjson( th );
			// 	var module = this.editModules[this.editable ? (index-1) : index];
			// 	if ( this.isAvaliableImpExpColumn( thJson, module ) ) {
			// 		if (module && ["Org","Reader","Author","Personfield","Orgfield"].contains(module.json.type)) {
			// 			colWidthArr.push(340);
			// 		} else if (module && module.json.type === "Address") {
			// 			colWidthArr.push(170);
			// 		} else if (module && module.json.type === "Textarea") {
			// 			colWidthArr.push(260);
			// 		} else if (module && module.json.type === "Htmleditor") {
			// 			colWidthArr.push(500);
			// 		} else if (module && module.json.type === "Calendar") {
			// 			colWidthArr.push(150);
			// 			dateIndexArr.push(idx);
			// 		} else {
			// 			colWidthArr.push(150);
			// 		}
			// 		idx++;
			// 		titleArr.push(th.get("text"));
			// 	}
			// }.bind(this));

			resultArr.push( titleArr );

			if (this.gridData.data){
				this.gridData.data.each(function(data, idx){
					var array = [];

					titleThs.each(function(th, index){
						if ( this.editable && (index===0 || index === titleThs.length-1) )return; //第一列操作列和最后一列排序列

						var module = this.editModules[ this.editable ? (index-1) : index];
						var thJson = this.form._getDomjson( th );

						if ( this.isAvaliableImpExpColumn( thJson, module ) ) {

							var cellData = data[th.get("id")];
							var text = "";

							if( cellData ) {
								if (typeOf(cellData) !== "array") { //序号
									for (key in cellData) {
										var value = cellData[key];
										if (module && ["Org", "Reader", "Author", "Personfield", "Orgfield"].contains(module.json.type)) {
											if (typeOf(value) === "array") {
												var textArray = [];
												value.each(function (item) {
													if (typeOf(item) === "object") {
														textArray.push(item.distinguishedName);
													} else {
														textArray.push(item);
													}
												}.bind(this));
												text = textArray.join(", \n");
											} else if (typeOf(value) === "object") {
												text = value.distinguishedName;
											} else {
												text = value;
											}
										} else if (module && module.json.type === "Textarea") {
											text = value;
										} else {
											text = this._getValueText(this.editable ? (index - 1) : index, value);
										}
										break;
									}
								}
							} else if (module && module.json.type === "Label" && module.node) {
								text = module.node.get("text");
							}

							if( !text && typeOf(text) !== "number" ){
								text = "";
							}

							array.push( text );
						}


					}.bind(this));

					resultArr.push( array );

				}.bind(this));
			}

			var title;
			if( this.json.excelName && this.json.excelName.code ){
				title = this.form.Macro.exec(this.json.excelName.code, this);
			}else{
				title = MWF.xApplication.process.Xform.LP.exportDefaultName;
			}
			var titleA = title.split(".");
			if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
				titleA.splice( titleA.length-1 );
			}
			title = titleA.join(".");

			var arg = { data : resultArr, colWidthArray : colWidthArr, title : title };
			this.fireEvent("export", [arg]);

			new MWF.xApplication.process.Xform.DatagridPC.ExcelUtils(this).exportToExcel( resultArr, arg.title || title, colWidthArr, dateIndexArr );
		},
		importFromExcel : function () {
			debugger;
			var columnList = [];

			var dateColArray = []; //日期列
			var titleThs = this.titleTr.getElements("th");

			var idx = 1;
			var orgTitleList = [];
			titleThs.each(function(th, index){
				if (this.editable && (index===0 || index === titleThs.length-1 ))return; //第一列操作列和最后一列排序列
				var module = this.editModules[this.editable ? (index-1) : index];
				var thJson = this.form._getDomjson( th );
				if ( this.isAvaliableImpExpColumn( thJson, module, "import" )){
					columnList.push({
						text : th.get("text").trim(),
						index: idx,
						thJson: thJson,
						module: module
					});
					idx++;
					if (module && module.json.type === "Calendar"){
						dateColArray.push(idx);
					}else if( module && ["Org","Reader","Author","Personfield","Orgfield"].contains(module.json.type) ){
						orgTitleList.push(th.get("text"));
					}
				}
			}.bind(this));


			new MWF.xApplication.process.Xform.DatagridPC.ExcelUtils(this).upload( dateColArray, function (importedData) {

				var checkAndImport = function () {
					if( !this.checkImportedData( columnList, importedData ) ){
						this.openImportedErrorDlg( columnList, importedData );
					}else{
						this.setImportData( columnList, importedData )
					}
				}.bind(this);

				if( orgTitleList.length > 0 ){
					this.listImportAllOrgData( orgTitleList, importedData, function () {
						checkAndImport();
					}.bind(this));
				}else{
					checkAndImport();
				}


			}.bind(this));
		},
		parseImportedData: function(columnList, importedData){
			var data = {
				"data" : []
			};

			importedData.each( function( importedLineData, lineIndex ){

				var lineData = {};

				columnList.each( function (obj, i) {
					var index = obj.index;
					var module = obj.module;
					var thJson = obj.thJson;
					var text = obj.text;

					var d = importedLineData[text] || "";

					var value;
					switch (module.json.type) {
						case "Org":
						case "Reader":
						case "Author":
						case "Personfield":
						case "Orgfield":
							if( !d ){
								value = [];
							}else{
								var arr = d.split(/\s*,\s*/g ); //空格,空格
								// if( arr.length === 0 ){
								// 	value = this.getImportOrgData( d );
								// }else{
								value = [];
								arr.each( function(d, idx){
									var obj = this.getImportOrgData( d );
									value.push( obj );
								}.bind(this));
								// }
							}
							break;
						case "Combox":
						case "Address":
							arr = d.split(/\s*,\s*/g ); //空格,空格
							value = arr; //arr.length === 1  ? arr[0] : arr;
							break;
						case "Checkbox":
							arr = d.split(/\s*,\s*/g ); //空格,空格
							var options = module.getOptionsObj();
							arr.each( function( a, i ){
								var idx = options.textList.indexOf( a );
								arr[ i ] = idx > -1 ? options.valueList[ idx ] : "";
							});
							value = arr.length === 1  ? arr[0] : arr;
							break;
						case "Radio":
						case "Select":
							value = d.replace(/&#10;/g,""); //换行符&#10;
							var options = module.getOptionsObj();
							var idx = options.textList.indexOf( value );
							value = idx > -1 ? options.valueList[ idx ] : "";
							break;
						case "Textarea":
							value = d.replace(/&#10;/g,"\n"); //换行符&#10;
							break;
						case "Calendar":
							value = d.replace(/&#10;/g,""); //换行符&#10;
							if( value ){
								var format;
								if (!module.json.format){
									if (module.json.selectType==="datetime" || module.json.selectType==="time"){
										format = (module.json.selectType === "time") ? "%H:%M" : (Locale.get("Date").shortDate + " " + "%H:%M")
									}else{
										format = Locale.get("Date").shortDate;
									}
								}else{
									format = module.json.format;
								}
								value = Date.parse( value ).format( format );
							}
							break;
						default:
							value = d.replace(/&#10;/g,""); //换行符&#10;
							break;
					}

					lineData[ thJson.id ] = {};
					lineData[ thJson.id ][ module.json.id ] = value;

				}.bind(this));

				data.data.push( lineData );
			}.bind(this));
			return data;
		},
		setImportData: function(columnList, importedData){

			var data = this.parseImportedData( columnList, importedData );

			this.fireEvent("import", [data] );

			this.setData( data );
			this.form.notice( MWF.xApplication.process.Xform.LP.importSuccess );

		},
		openImportedErrorDlg : function( columnList, tableData ){
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
				return arr.join( " " )
			}

			var htmlArray = ["<table "+ objectToString( this.json.properties ) +" style='"+objectToString( this.json.tableStyles, "style" )+"'>"];

			var titleStyle = objectToString( this.json.titleStyles, "style" );
			htmlArray.push( "<tr>" );
			columnList.each( function (obj, i) {
				htmlArray.push( "<th style='"+titleStyle+"'>"+obj.text+"</th>" );
			});
			htmlArray.push( "<th style='"+titleStyle+"'> "+MWF.xApplication.process.Xform.LP.validationInfor +"</th>" );
			htmlArray.push( "</tr>" );

			var contentStyles = Object.clone( this.json.contentStyles );
			if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
			var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

			tableData.each( function( lineData, lineIndex ){

				htmlArray.push( "<tr>" );
				columnList.each( function (obj, i) {
					htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ obj.text ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //换行符&#10;
				});
				htmlArray.push( "<td style='"+contentStyle+"'>"+( lineData.errorTextList ? lineData.errorTextList.join("<br/>") : "" )+"</td>" );
				htmlArray.push( "</tr>" );

			}.bind(this));
			htmlArray.push( "</table>" );

			var div = new Element("div", { style : "padding:10px;", html : htmlArray.join("") });
			var dlg = o2.DL.open({
				"style" : this.form.json.dialogStyle || "user",
				"title": MWF.xApplication.process.Xform.LP.importFail,
				"content": div,
				"offset": {"y": 0},
				"isMax": true,
				"width": 1000,
				"height": 700,
				"buttonList": [
					{
						"type": "exportWithError",
						"text": MWF.xApplication.process.Xform.LP.datagridExport,
						"action": function () { _self.exportWithImportDataToExcel(columnList, tableData); }
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
		exportWithImportDataToExcel : function ( columnList, importedData ) {
			debugger;
			var titleThs = this.titleTr.getElements("th");
			// var editorTds = this.editorTr.getElements("td");

			var resultArr = [];

			var colWidthArr = this.getExportColWidthArray();
			colWidthArr.push( 220 );

			var dateIndexArr = this.getExportDateIndexArray(); //日期格式列下标

			var titleArr = this.getExportTitleArray("import");
			titleArr.push( MWF.xApplication.process.Xform.LP.validationInfor );
			resultArr.push( titleArr );

			importedData.each( function( lineData, lineIndex ){
				var array = [];
				columnList.each( function (obj, i) {
					array.push( ( lineData[ obj.text ] || '' ).replace(/&#10;/g, "\n") );
				});
				array.push( lineData.errorTextListExcel ? lineData.errorTextListExcel.join("\n") : ""  );

				resultArr.push( array );
			}.bind(this));

			var title;
			if( this.json.excelName && this.json.excelName.code ){
				title = this.form.Macro.exec(this.json.excelName.code, this);
			}else{
				title = MWF.xApplication.process.Xform.LP.exportDefaultName;
			}
			var titleA = title.split(".");
			if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
				titleA.splice( titleA.length-1 );
			}
			title = titleA.join(".");

			var arg = { data : resultArr, colWidthArray : colWidthArr, title : title, withError : true };
			this.fireEvent("export", [arg]);

			new MWF.xApplication.process.Xform.DatagridPC.ExcelUtils(this).exportToExcel( resultArr, arg.title || title, colWidthArr, dateIndexArr );
		},
		checkImportedData : function( columnList, tableData ){
			var flag = true;

			var parsedData = this.parseImportedData(columnList, tableData);

			var lp = MWF.xApplication.process.Xform.LP;
			var columnText =  lp.importValidationColumnText;
			var columnTextExcel = lp.importValidationColumnTextExcel;
			var excelUtil = new MWF.xApplication.process.Xform.DatagridPC.ExcelUtils(this);

			tableData.each( function(lineData, lineIndex){

				var parsedLineData = (parsedData && parsedData.data) ? parsedData.data[lineIndex] : {};

				var errorTextList = [];
				var errorTextListExcel = [];

				columnList.each( function (obj, i) {
					var index = obj.index;
					var module = obj.module;
					var thJson = obj.thJson;
					var text = obj.text;

					var colInfor = columnText.replace( "{n}", index );
					var colInforExcel = columnTextExcel.replace( "{n}", excelUtil.index2ColName( index-1 ) );

					var d = lineData[text] || "";

					var parsedD = "";
					var ptd = parsedLineData[thJson.id];
					if( typeOf(ptd) === "object")parsedD = ptd[module.json.id];

					if( d ){
						switch (module.json.type) {
							case "Org":
							case "Reader":
							case "Author":
							case "Personfield":
							case "Orgfield":
								var arr = d.split(/\s*,\s*/g ); //空格,空格
								arr.each( function(d, idx){
									var obj = this.getImportOrgData( d );
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
					if (module.json.type!="sequence" && module.setData && module.json.type!=="Address"){
						var hasError = false;
						if(["Org","Reader","Author","Personfield","Orgfield"].contains(module.json.type)){
							if(o2.typeOf(parsedD)==="array" && parsedD.length){
								hasError = parsedD.some(function (item) { return item.errorText; })
							}
						}
						if( !hasError ){
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

				debugger;

			}.bind(this));

			var arg = {
				validted : flag,
				data : tableData
			};
			this.fireEvent( "validImport", [arg] );

			return arg.validted;
		},
		getImportOrgData : function( str ){
			str = str.trim();
			var flag = str.substr(str.length-2, 2);
			switch (flag.toLowerCase()){
				case "@i":
					return this.identityMapImported[str] || {"errorText": str + MWF.xApplication.process.Xform.LP.notExistInSystem };
				case "@p":
					return this.personMapImported[str] || {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
				case "@u":
					return this.unitMapImported[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
				case "@g":
					return this.groupMapImported[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
				default:
					return this.identityMapImported[str] ||
						this.personMapImported[str] ||
						this.unitMapImported[str] ||
						this.groupMapImported[str] ||
						{"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };

			}
		},
		listImportAllOrgData : function (orgTitleList, tableData, callback) {
			var identityList = [], personList = [], unitList = [], groupList = [];
			if( orgTitleList.length > 0 ){
				tableData.each( function( lineData, lineIndex ){
					// if( lineIndex === 0 )return;

					orgTitleList.each( function (title, index) {

						if( !lineData[title] )return;

						var arr = lineData[title].split(/\s*,\s*/g );
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
					})
				});
				var identityLoaded, personLoaded, unitLoaded, groupLoaded;
				var check = function () {
					if( identityLoaded && personLoaded && unitLoaded && groupLoaded ){
						if(callback)callback();
					}
				};

				this.identityMapImported = {};
				if( identityList.length ){
					o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({ identityList : identityList }, function (json) {
						json.data.each( function (d) { this.identityMapImported[ d.matchKey ] = d; }.bind(this));
						identityLoaded = true;
						check();
					}.bind(this))
				}else{
					identityLoaded = true;
					check();
				}

				this.personMapImported = {};
				if( personList.length ){
					o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({ personList : personList }, function (json) {
						json.data.each( function (d) { this.personMapImported[ d.matchKey ] = d; }.bind(this));
						personLoaded = true;
						check();
					}.bind(this))
				}else{
					personLoaded = true;
					check();
				}

				this.unitMapImported = {};
				if( unitList.length ){
					o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ unitList : unitList }, function (json) {
						json.data.each( function (d) { this.unitMapImported[ d.matchKey ] = d; }.bind(this));
						unitLoaded = true;
						check();
					}.bind(this))
				}else{
					unitLoaded = true;
					check();
				}

				this.groupMapImported = {};
				if( groupList.length ){
					o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({ groupList : groupList }, function (json) {
						json.data.each( function (d) { this.groupMapImported[ d.matchKey ] = d; }.bind(this));
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

MWF.xApplication.process.Xform.DatagridPC$Title =  new Class({
	Extends: MWF.APP$Module,
	_afterLoaded: function(){
		this.dataGrid = this.node.retrieve("dataGrid");
		if ((this.json.total == "number") || (this.json.total == "count")){
			this.dataGrid.totalModules.push({
				"module": this,
				"index": (this.dataGrid.editable!=false) ? this.node.cellIndex+1 : this.node.cellIndex,
				"type": this.json.total
			})
		}
		//	this.form._loadModules(this.node);
	}
});
MWF.xApplication.process.Xform.DatagridPC$Data =  new Class({
	Extends: MWF.APP$Module,
	_afterLoaded: function(){
		//this.form._loadModules(this.node);
		this.dataGrid = this.node.retrieve("dataGrid");

		var td = this.node;

		if (this.json.cellType == "sequence"){
			var flag = true;
			for (var i=0; i<this.dataGrid.editModules.length; i++){
				if (this.dataGrid.editModules[i].json.id == this.json.id){
					flag = false;
					break;
				}
			}
			if (flag){
				this.dataGrid.editModules.push({
					"json": {"type": "sequence", "id": this.json.id},
					"node": td  ,
					"focus": function(){}
				});
			}
		}else{
			var moduleNodes = this.form._getModuleNodes(this.node);
			moduleNodes.each(function(node){
				var json = this.form._getDomjson(node);
				if( json ){
					var isField = false;
					if (json.type=="Attachment" || json.type=="AttachmentDg" ){
						json.type = "AttachmentDg";
						//json.site = this.dataGrid.getAttachmentRandomSite();
						//json.id = json.site;
					}
					var module = this.form._loadModule(json, node, function(){
						isField = this.field;
						this.field = false;
					});
					if( isField ){
						module.node.setStyle("padding-right","0px");
					}
					module.dataModule = this;
					this.dataGrid.editModules.push(module);
				}
			}.bind(this));
		}
	}
});

MWF.xApplication.process.Xform.DatagridPC.ExcelUtils = new Class({
	initialize: function( datagrid ){
		this.datagrid = datagrid;
		this.form = datagrid.form;
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
				if( file.name.indexOf(" ") > -1 ){
					this.form.notice( MWF.xApplication.process.Xform.LP.uploadedFilesCannotHaveSpaces, "error");
					return false;
				}

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

					debugger;

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
					debugger;
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
