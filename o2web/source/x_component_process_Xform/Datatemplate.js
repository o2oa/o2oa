/**
 * 数据模板数据结构.
 * @typedef {Object} DatatemplateData
 * @property {Array} data - 数据网格列表数据
 * @property {Object} total - 统计数据
 * @example
 {
    "data": [ //数据模板数据条目
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
    ],
    "total": {  //统计数据，列title设置了总计
        "datagrid_datagrid$Title_2": "333", //总计列2
        "datagrid_datagrid$Title_3": "2" //总计列3
    }
}
 */
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Datatemplate 数据模板组件。
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
MWF.xApplication.process.Xform.Datatemplate = new Class(
	/** @lends MWF.xApplication.process.Xform.Datatemplate# */
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

			this.addActionIdList = (this.json.addActionId || "").split(",");
			this.addable = this.addActionIdList.length > 0;

			this.deleteActionIdList = (this.json.deleteActionId || "").split(",");
			this.deleteable = this.deleteActionIdList.length > 0;

			this.sequenceIdList = (this.json.sequenceId || "").split(",");

			this.selectorId = this.json.selectorId;

			this.importActionIdList = (this.json.importActionId || "").split(",");
			//允许导入
			this.importenable  = this.editable && (this.importActionIdList.length > 0) &&
				(this.json.impexpType === "impexp" || this.json.impexpType === "imp");

			//允许导出
			this.exportActionIdList = (this.json.exportActionId || "").split(",");
			this.exportenable  = (this.exportActionIdList.length > 0) && (this.json.impexpType === "impexp" || this.json.impexpType === "exp");

			this.gridData = this._getValue();

			this.lineList = [];

			// this.totalModules = [];
			this._loadStyles();

			//获取html模板和json模板
			// this.getTemplate();

			//设置节点外的操作：添加、删除、导入、导出
			this.setEvents_OuterActions();

			//隐藏节点
			this.node.getChildren().hide();

			if (this.editable!==false){
				this._loadEdit(function(){
					this._loadImportExportAction();
					this.fireEvent("postLoad");
					this.fireEvent("load");
				}.bind(this));
				//this._loadReadDatagrid();
			}else{

				this._loadReadDatagrid(function(){
					if(this.editorTr)this.editorTr.setStyle("display", "none");
					this._loadImportExportAction();
					this.fireEvent("postLoad");
					this.fireEvent("load");
				}.bind(this));

			}
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
		},
		setEvents_OuterActions: function(){

			var getModules = function (idList) {
				var list = [];
				idList.each( function (id) {
					if( !this.node.getElement(id) && this.form.all[id] ){
						list.push( this.form.all[id] );
					}
				}.bind(this));
				return list;
			};

			this.addActionList = getModules( this.addActionIdList );
			this.addActionList.each( function (node) {
				node.addEvents({"click": function(e){
						this._addLine(e.target);
					}.bind(this)})
			}.bind(this));

			this.deleteActionList = getModules( this.deleteActionIdList );
			this.deleteActionList.each( function (node) {
				node.addEvents({"click": function(e){
						this._deleteLine(e.target);
					}.bind(this)})
			}.bind(this));

			this.importActionList = getModules( this.importActionIdList );
			this.importActionList.each( function (node) {
				node.addEvents({"click": function(e){
						this.importFromExcel();
					}.bind(this)})
			}.bind(this));

			this.exportActionList = getModules( this.exportActionIdList );
			this.exportActionList.each( function (node) {
				node.addEvents({"click": function(e){
						this.exportToExcel();
					}.bind(this)})
			}.bind(this));
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

		_loadEdit: function(callback){
			var p = o2.promiseAll(this.gridData).then(function(v){
				this.gridData = v;
				if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
				this._loadEditLineList(callback);
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
		_loadEditLineList: function(callback){
			if (this.gridData.data){
				this.gridData.data.each(function(data, idx){
					var div = new Element("div").inject(this.node);
					this._loadEditLine(div, data, idx );
				}.bind(this));
			}
			if (callback) callback();
		},
		_loadEditLine: function(container, data, index, isEdited){
			var line = new MWF.xApplication.process.Xform.Datatemplate.Line( container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isEdited : typeOf(isEdited) === "boolean" ? isEdited : this.editable
			});
			line.load();
			this.lineList.push(line);
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

			this.getData();
			this.validationMode();
			this.fireEvent("completeLineEdit", [newTr]);

			if( saveFlag ){
				this.form.saveFormData();
			}

			return true;
		},
		_addLine: function(node){
			if (this.isEdit){
				if (!this._completeLineEdit()) return false;
			}
			this.editorTr.setStyles({
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

		_loadDatagridStyle: function(){

			this._loadTotal();
			this._loadSequence();
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
		}

	});

MWF.xApplication.process.Xform.Datatemplate.Line =  new Class({
	Implements: [Options, Events],
	options: {
		isEdited : true,
		index : 0,
		indexText : "0"
	},
	initialize: function (node, template, data, options) {

		this.setOptions(options);

		this.node = node;
		this.template = template;
		this.data = data;
		this.form = this.template.form;

		this.moduleList = [];
		this.fieldList = [];
	},
	load:function(){
		if( this.options.isEdited ){
		   this.load_Edit();
		}else{
		   this.load_Read();
		}
	},
	load_Edit: function(){
		var copyNode = this.template.node.clone();
		var moduleNodes = this.form._getModuleNodes( copyNode );
		moduleNodes.each(function (node) {
			if (node.get("MWFtype") !== "form") {
				var _self = this;

				var json = this.form._getDomjson(node);
				if( json ){
					json.id = this.template.json.id + ".."+this.options.index + ".." + json.id;
					node.set("id", json.id);

					var module = this.form._loadModule(json, node, function () {
						this.parentformIdList = _self.getParentformIdList();
					});
					this.form.modules.push(module);

				}
			}
		}.bind(this));

	},
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
	_createNewEditTd: function(tr, idx, id, text, lastIdx, rowIndex){
		var cell = $(tr.insertCell(idx));
		if (idx==0){
			cell.setStyles(this.form.css.gridLineActionCell);
			if( this.addable )this._createAddLineAction(cell);
			if( this.deleteable )this._createDelLineAction(cell);
		}else if (idx == lastIdx){
			cell.setStyles(this.form.css.gridMoveActionCell);
			// this._createMoveLineAction(cell);
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
	}
});