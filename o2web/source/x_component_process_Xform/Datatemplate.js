/**
 * 数据模板数据结构.
 * @typedef {Object} DatatemplateData
 * @property {Array} data - 数据网格列表数据
 * @property {Object} total - 统计数据
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
/** @class Datatemplate 数据模板组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var datatemplate = this.form.get("name"); //获取组件
 * //方法2
 * var datatemplate = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
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
			 * 当前条目编辑完成时触发。通过this.event可以获取对应的tr。
			 * @event MWF.xApplication.process.Xform.Datatemplate#completeLineEdit
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 添加条目时触发。通过this.event可以获取对应的tr。
			 * @event MWF.xApplication.process.Xform.Datatemplate#addLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 删除条目前触发。通过this.event可以获取对应的tr。
			 * @event MWF.xApplication.process.Xform.Datatemplate#deleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 删除条目后触发。
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterDeleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
			 */
			/**
			 * 编辑条目时触发。
			 * @event MWF.xApplication.process.Xform.Datatemplate#editLine
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
			 *  	 	] //校验出的错误信息，如果改行数据正确，则无该字段
			 *  	 }
			 *  	 ...
			 *     ], //导入的数据
			 *     "validted" : true  //是否校验通过，可以在本事件中修改该参数，确定是否强制导入
			 * }
			 */
			/**
			 * 在导入excel，数据校验成功将要设置回数据网格的时候触发，this.event指向整理过的导入数据，格式见{@link DatatemplateData}。
			 * @event MWF.xApplication.process.Xform.Datatemplate#import
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

			this.data = this._getValue();

			this.lineList = [];

			// this.totalModules = [];
			this._loadStyles();

			//获取html模板和json模板
			this.getTemplate();

			//设置节点外的操作：添加、删除、导入、导出
			this.setOuterActionsEvents();

			debugger;

			//隐藏节点
			this.node.getChildren().hide();

			this._loadDataTemplate(function(){
				// this._loadImportExportAction();
				this.fireEvent("postLoad");
				this.fireEvent("load");
			}.bind(this));
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
		setOuterActionsEvents: function(){
			//判断不在数据模板中，但是在表单内的Id
			var getModules = function (idList) {
				var list = [];
				idList.each( function (id) {
					if( !this.templateJson.hasOwnProperty(id) && this.form.all[id] ){
						list.push( this.form.all[id] );
					}
				}.bind(this));
				return list;
			}.bind(this);

			this.bindEvent = function () {
				this.addActionList = getModules( this.addActionIdList );
				this.addActionList.each( function (module) {
					module.node.addEvents({"click": function(e){
							this._addLine(e.target);
						}.bind(this)})
				}.bind(this));

				this.deleteActionList = getModules( this.deleteActionIdList );
				this.deleteActionList.each( function (module) {
					module.node.addEvents({"click": function(e){
							this._deleteLine(e.target);
						}.bind(this)})
				}.bind(this));

				this.importActionList = getModules( this.importActionIdList );
				this.importActionList.each( function (module) {
					module.node.addEvents({"click": function(e){
							this.importFromExcel();
						}.bind(this)})
				}.bind(this));

				this.exportActionList = getModules( this.exportActionIdList );
				this.exportActionList.each( function (module) {
					module.node.addEvents({"click": function(e){
							this.exportToExcel();
						}.bind(this)})
				}.bind(this));
				//加载完成以后，删除事件
				this.form.removeEvent("afterModulesLoad", this.bindEvent );
			}.bind(this);

			//去要表单的所有组件加载完成以后再去获取外部组件
			this.form.addEvent("afterModulesLoad", this.bindEvent );
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

		_loadDataTemplate: function(callback){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				if (o2.typeOf(this.data)=="object") this.data = [this.data];
				this._loadLineList(callback);
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
			if (this.data){
				this.data.each(function(data, idx){
					var div = new Element("div").inject(this.node);
					var line = this._loadLine(div, data, idx );
					this.lineList.push(line);
				}.bind(this));
			}
			if (callback) callback();
		},
		_loadLine: function(container, data, index, isEdited){
			var line = new MWF.xApplication.process.Xform.Datatemplate.Line(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isEdited : typeOf(isEdited) === "boolean" ? isEdited : this.editable
			});
			line.load();
			return line;
		},
		_addLine: function(ev){
			var index = this.lineList.length;
			var div = new Element("div").inject(this.node);
			var line = this._loadLine(div, {}, index );
			this.lineList.push(line);
			this.fireEvent("addLine", [line, ev]);
		},
		_insertLine: function(ev, beforeLine){
			debugger;
			//使用数据驱动
			var index = beforeLine.options.index+1;
			// var d = Object.clone(this.getTemplateData());
			var data = this.getData();
			data.splice(index, 0, {});
			this.setData( data );
			this.fireEvent("addLine",[this.lineList[index], ev]);
		},
		_deleteLine: function(ev, line){
			var _self = this;
			this.form.confirm("warn", ev, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
				_self.fireEvent("deleteLine", [line]);

				//使用数据驱动
				var index = line.options.index;
				var data = _self.getData();
				data.splice(index, 1);
				_self.setData( data );
				this.close();

				_self.fireEvent("afterDeleteLine");
			}, function(){
				this.close();
			}, null, null, this.form.json.confirmStyle);
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
		//
		// 	var datagrid = this;
		// 	this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.cancelDatagridLineEditTitle, MWF.xApplication.process.Xform.LP.cancelDatagridLineEdit, 300, 120, function(){
		// 		if (datagrid.currentEditLine) {
		// 			datagrid.currentEditLine.setStyle("display", "table-row");
		// 		}
		//
		// 		datagrid.editModules.each(function(module){
		// 			if (module && (module.json.type=="Attachment" || module.json.type=="AttachmentDg")){
		// 				module.attachmentController.attachments.each(function(att){
		// 					datagrid.form.workAction.deleteAttachment(att.data.id, datagrid.form.businessData.work.id);
		// 				});
		// 				module.attachmentController.clear();
		// 			}
		// 		});
		//
		// 		datagrid.isEdit = false;
		// 		datagrid.currentEditLine = null;
		//
		// 		this.close();
		//
		// 		datagrid.fireEvent("cancelLineEdit");
		// 	}, function(){
		// 		this.close();
		// 	}, null, null, this.form.json.confirmStyle);
		//
		// },
		// _completeLineEdit: function( ev ){
		//
		// 	debugger;
		//
		// 	//this.currentEditLine.getElemets(td);
		// 	if (!this.editValidation()){
		// 		return false;
		// 	}
		//
		// 	this.isEdit = false;
		//
		// 	var flag = true;
		// 	var saveFlag = false;
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
		// 	var titleThs = this.titleTr.getElements("th");
		// 	var editorTds = this.editorTr.getElements("td");
		// 	var cells = newTr.getElements("td");
		// 	titleThs.each(function(th, idx){
		// 		var cell = cells[idx];
		// 		var id = th.get("id");
		// 		var module = this.editModules[idx-1];
		// 		if (module){
		// 			if (module.json.type=="sequence"){
		// 				flag = false;
		// 				var i = newTr.rowIndex;
		// 				var data = {"value": [i], "text": [i]};
		// 			}else if (module.json.type=="Attachment" || module.json.type == "AttachmentDg"){
		// 				saveFlag = true;
		// 				flag = false;
		// 				var data = module.getTextData();
		// 				//data.site = module.json.site;
		// 				if (!griddata[id]) griddata[id] = {};
		// 				griddata[id][module.json.id] = data;
		// 				// }else if( ["Orgfield","Personfield","Org","Address"].contains(module.json.type) ){
		// 				// 	data = module.getTextData();
		// 				// 	if( data.value && data.value.length )flag = false;
		// 				// 	if (!griddata[id]) griddata[id] = {};
		// 				// 	griddata[id][module.json.id] = data.value;
		// 			}else if( module.getTextData ){
		// 				var data = module.getTextData();
		// 				if (data.value[0]) flag = false;
		// 				if (data.value.length<2){
		// 					if (!griddata[id]) griddata[id] = {};
		// 					griddata[id][module.json.id] = data.value[0];
		// 				}else{
		// 					if (!griddata[id]) griddata[id] = {};
		// 					griddata[id][module.json.id] = data.value;
		// 				}
		// 			}
		//
		// 			if( data ){
		// 				if (cell){
		// 					if( module.json.type == "ImageClipper" ){
		// 						this._createImage( cell, module, data.text[0] );
		// 					}else if( module.json.type == "Attachment" || module.json.type == "AttachmentDg" ){
		// 						this._createAttachment( cell, module, data );
		// 					}else{
		// 						var text = this._getValueText(idx-1, data.text.join(", "));
		// 						if( module.json.type == "Textarea"){
		// 							cell.set("html", text);
		// 						}else{
		// 							cell.set("text", data.text.join(", "));
		// 						}
		// 					}
		// 				}else{
		// 					if( module.json.type == "Attachment" || module.json.type == "AttachmentDg" ){
		// 						this._createNewEditTd(newTr, idx, editorTds[idx].get("id"), data, titleThs.length-1);
		// 					}else{
		// 						var text = this._getValueText(idx-1, data.text.join(", "));
		// 						this._createNewEditTd(newTr, idx, editorTds[idx].get("id"), text, titleThs.length-1);
		// 					}
		// 				}
		// 			}else{
		// 				if (!cell) this._createNewEditTd(newTr, idx, id, "", titleThs.length-1);
		// 			}
		// 		}else{
		// 			if (!cell) this._createNewEditTd(newTr, idx, id, "", titleThs.length-1);
		// 		}
		// 		module = null;
		// 	}.bind(this));
		//
		// 	newTr.store("data", griddata);
		// 	newTr.setStyle("display", "table-row");
		//
		// 	if (flag){
		// 		newTr.destroy();
		// 	}
		// 	this.currentEditLine = null;
		//
		// 	this.getData();
		// 	this.validationMode();
		// 	this.fireEvent("completeLineEdit", [newTr]);
		//
		// 	if( saveFlag ){
		// 		this.form.saveFormData();
		// 	}
		//
		// 	return true;
		// },


		_afterLoaded: function(){
			// if (this.moduleValueAG){
			// 	this.moduleValueAG.then(function(){
			//
			// 	}.bind(this));
			// }else{
			//
			// }
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
		setData: function(data){
			if (!data){
				data = this._getValue();
			}
			this._setData(data);
		},
		_setData: function(data){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				if (o2.typeOf(data)=="object") data = [data];
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
				for (var i=0; i<this.lineList.length; i++){
					this.lineList[i].clear();
				}
			}

			this.lineList = [];
			this._loadDataTemplate()
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
			if( o2.typeOf( data ) === "array" ){
				return data.data.length === 0;
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
		 * @return {DatatemplateData}
		 */
		getData: function(){
			if (this.editable!==false){
				var data = [];
				this.lineList.each(function(line, index){
					data.push(line.getData())
				});

				this.data = data;

				this._setBusinessData(this.data);

				return (this.data.length) ? this.data : [];
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

		this.modules = [];
		this.all = {};

		this.fields = [];
		this.allField = {};

		this.addActionList = [];
		this.deleteActionList = [];
		this.sequenceNodeList = [];
		this.selector = null;
		this.importActionList = [];
		this.exportActionList = [];


	},
	load: function(){
		this.node.set("html", this.template.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.node);
		moduleNodes.each(function (node) {
			if (node.get("MWFtype") !== "form") {
				var _self = this;

				var tJson = this.form._getDomjson(node);
				if( tJson ){
					var json = Object.clone(tJson);

					var templateJsonId = json.id;
					var id = this.template.json.id + ".."+this.options.index + ".." + json.id;

					json.id = id;
					if( !this.options.isEdited )json.isReadonly = true;

					node.set("id", id);

					if (this.form.all[id]) this.form.all[id] = null;
					if (this.form.forms[id])this.form.forms[id] = null;

					var module = this.form._loadModule(json, node, function () {});
					this.form.modules.push(module);

					this.modules.push(module);
					this.all[id] = module;

					if (module.field) {
						if(this.data.hasOwnProperty(templateJsonId)){
							module.setData(this.data[templateJsonId]);
						}
						this.allField[id] = module;
						this.fields.push( module );
					}

					this.setEvents(module, templateJsonId);

				}
			}
		}.bind(this));
	},
	setEvents: function (module, id) {
		if( this.template.addActionIdList.contains( id )){
			this.addActionList.push( module );
			module.node.addEvent("click", function (ev) {
				this.template._insertLine( ev, this )
			}.bind(this))
		}

		if( this.template.deleteActionIdList.contains(id)){
			this.deleteActionList.push( module );
			module.node.addEvent("click", function (ev) {
				this.template._deleteLine( ev, this )
			}.bind(this))
		}

		if( this.template.selectorId === id){
			this.selector = module;
			module.node.addEvent("click", function (ev) {
				this.checkSelect();
			}.bind(this))
		}

		if( this.template.sequenceIdList.contains(id)){
			this.sequenceNodeList.push( module );
			if(this.form.getModuleType(module) === "label"){
				module.node.set("text", this.options.indexText );
			}else{
				module.set( this.options.indexText );
			}
		}

		//???
		// if( this.template.importActionIdList.contains(id))this.importActionList.push( module );
		// if( this.template.exportActionIdList.contains(id))this.exportActionList.push( module );

	},
	checkSelect: function () {
		//???
		if( this.form.getModuleType( this.selector ) === "radio" ){

		}else if( this.form.getModuleType( this.selector ) === "checkbox" ){
			var selectData = this.selected.getData();
			if( selectData.length > 0 ){ //表示选中
				this.selected = false;
				// this.selector.setData("");
			}else{
				this.selected = true;
				// this.selector.setData( this.selector.getOptionsObj().valueList[0] );
			}
		}
	},
	clear: function () { //把module清除掉
		for(var key in this.all){
			var module = this.all[key];
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.destroy();
	},
	getData: function () {
		var data = {};
		for( var key in this.allField){
			var id = key.split("..").getLast();
			data[id] = this.allField[key].getData();
		}
		return data;
	}

	// _createImg : function(cell, module, idx){
	// 	this._cloneModule(cell, module, idx);
	// },
	// _createLabel : function(cell, module, idx){
	// 	this._cloneModule(cell, module, idx);
	// },
	// _createButton : function(cell, module, idx){
	// 	this._cloneModule(cell, module, idx);
	// },
	// _cloneModule : function(cell, module, idx){
	// 	debugger;
	// 	cell.empty();
	// 	if( module.node && module.json ){
	// 		var json = Object.clone( module.json );
	// 		json.id = json.id +"_"+idx;
	//
	// 		var node = module.node.clone();
	// 		node.set("id", json.id);
	// 		node.inject(cell);
	//
	// 		this.form._loadModule(json, node)
	// 	}
	// },
	// _createImage : function( cell, module, data ){
	// 	cell.empty();
	// 	if( !data )return;
	// 	var img = new Element("img",{
	// 		src : MWF.xDesktop.getImageSrc( data )
	// 	}).inject( cell, "top" );
	// 	if( module.json.clipperType == "size" ){
	// 		var width = module.json.imageWidth;
	// 		var height = module.json.imageHeight;
	// 		if (width && height) {
	// 			img.setStyles({
	// 				width: width + "px",
	// 				height: height + "px"
	// 			})
	// 		}
	// 	}
	// },
	// _createAttachment: function ( cell, module, data ){
	// 	cell.empty();
	// 	var options = {
	// 		"style": module.json.style || "default",
	// 		"title": MWF.xApplication.process.Xform.LP.attachmentArea,
	// 		"listStyle": module.json.dg_listStyle || "icon",
	// 		"size": module.json.dg_size || "min",
	// 		"resize": (module.json.dg_resize === "y" || this.json.dg_resize === "true"),
	// 		"attachmentCount": 0,
	// 		"isUpload": false,
	// 		"isDelete": false,
	// 		"isReplace": false,
	// 		"isDownload": true,
	// 		"isSizeChange": (module.json.dg_isSizeChange === "y" || module.json.dg_isSizeChange === "true"),
	// 		"readonly": true,
	// 		"availableListStyles": module.json.dg_availableListStyles ? module.json.dg_availableListStyles : ["list", "seq", "icon", "preview"],
	// 		"isDeleteOption": "n",
	// 		"isReplaceOption": "n",
	// 		"toolbarGroupHidden": module.json.dg_toolbarGroupHidden || []
	// 	};
	// 	if (this.readonly) options.readonly = true;
	// 	if(!this.editable && !this.addable)options.readonly = true;
	//
	// 	var atts = [];
	// 	( data || [] ).each(function(d){
	// 		var att = module.attachmentController.attachments.find(function(a){
	// 			return d.id == a.data.id;
	// 		});
	// 		if (att) module.attachmentController.removeAttachment(att);
	// 	});
	// 	module.setAttachmentBusinessData();
	//
	//
	// 	var attachmentController = new MWF.xApplication.process.Xform.AttachmentController(cell, module, options);
	// 	attachmentController.load();
	//
	// 	( data || [] ).each(function (att) {
	// 		var attachment = this.form.businessData.attachmentList.find(function(a){
	// 			return a.id==att.id;
	// 		});
	// 		var attData = attachment || att;
	// 		//if (att.site===this.json.id || (this.json.isOpenInOffice && this.json.officeControlName===att.site)) this.attachmentController.addAttachment(att);
	// 		attachmentController.addAttachment(attData);
	// 	}.bind(this));
	// },
	// _createNewEditTd: function(tr, idx, id, text, lastIdx, rowIndex){
	// 	var cell = $(tr.insertCell(idx));
	// 	if (idx==0){
	// 		cell.setStyles(this.form.css.gridLineActionCell);
	// 		if( this.addable )this._createAddLineAction(cell);
	// 		if( this.deleteable )this._createDelLineAction(cell);
	// 	}else if (idx == lastIdx){
	// 		cell.setStyles(this.form.css.gridMoveActionCell);
	// 		// this._createMoveLineAction(cell);
	// 	}else{
	// 		cell.set("MWFId", id);
	//
	// 		var module = this.editModules[idx-1];
	// 		if( module && module.json.type == "ImageClipper" ){
	// 			this._createImage( cell, module, text )
	// 		}else if( module && module.json.type == "Image" ) {
	// 			this._createImg(cell, module, rowIndex);
	// 		}else if( module && module.json.type == "Button" ) {
	// 			this._createButton(cell, module, rowIndex);
	// 		}else if( module && module.json.type == "Label" ) {
	// 			this._createLabel(cell, module, rowIndex);
	// 		}else if( module && (module.json.type == "Attachment" || module.json.type == "AttachmentDg") ){
	// 			this._createAttachment( cell, module, text );
	// 		}else{
	// 			if( module && module.json.type == "Textarea" ){
	// 				cell.set("html", text);
	// 			}else{
	// 				cell.set("text", text);
	// 			}
	// 			// /cell.set("text", text);
	// 		}
	// 		if( !module ||  !["Button"].contains( module.json.type ) ){
	// 			cell.addEvent("click", function(e){
	// 				this._editLine(e.target);
	// 			}.bind(this));
	// 		}
	// 	}
	// 	var json = this.form._getDomjson(cell);
	//
	// 	if (json){
	// 		cell.store("dataGrid", this);
	// 		var module = this.form._loadModule(json, cell);
	// 		cell.store("module", module);
	// 		this.form.modules.push(module);
	// 		if( json.isShow === false )cell.hide();
	// 	}
	//
	// },
	//
	//
	// _getValueText: function(idx, value){
	//
	// 	var module = this.editModules[idx];
	// 	if (module){
	// 		switch (module.json.type){
	// 			case "Select":
	// 				for (var i=0; i<module.json.itemValues.length; i++){
	// 					var itemv = module.json.itemValues[i];
	// 					var arr = itemv.split(/\|/);
	// 					var text = arr[0];
	// 					var v = (arr.length>1) ? arr[1] : arr[0];
	// 					if (value===v) return text;
	// 				}
	// 				// var ops = module.node.getElements("option");
	// 				// for (var i=0; i<ops.length; i++){
	// 				// 	if (ops[i].value == value){
	// 				// 		return ops[i].get("text");
	// 				// 		break;
	// 				// 	}
	// 				// }
	// 				break;
	// 			case "Radio":
	// 				var ops = module.node.getElements("input");
	// 				for (var i=0; i<ops.length; i++){
	// 					if (ops[i].value == value){
	// 						return ops[i].get("showText");
	// 						break;
	// 					}
	// 				}
	// 				break;
	// 			case "Checkbox":
	// 				var ops = module.node.getElements("input");
	// 				var texts = [];
	// 				for (var i=0; i<ops.length; i++){
	// 					if (value.indexOf(ops[i].value) != -1) texts.push(ops[i].get("showText"));
	// 				}
	// 				if (texts.length) return texts.join(", ");
	// 				break;
	// 			case "Orgfield":
	// 			case "Personfield":
	// 			case "Org":
	// 				//var v = module.getTextData();
	// 				//return v.text[0];
	//
	// 				if (typeOf(value)==="array"){
	// 					var textArray = [];
	// 					value.each( function( item ){
	// 						if (typeOf(item)==="object"){
	// 							textArray.push( item.name+((item.unitName) ? "("+item.unitName+")" : "") );
	// 						}else{
	// 							textArray.push(item);
	// 						}
	// 					}.bind(this));
	// 					return textArray.join(", ");
	// 				}else if (typeOf(value)==="object"){
	// 					return value.name+((value.unitName) ? "("+value.unitName+")" : "");
	// 				}else{
	// 					return value;
	// 				}
	//
	// 				break;
	// 			case "Textarea":
	// 				var reg = new RegExp("\n","g");
	// 				var reg2 = new RegExp("\u003c","g"); //尖括号转义，否则内容会截断
	// 				var reg3 = new RegExp("\u003e","g");
	// 				value = value.replace(reg2,"&lt").replace(reg3,"&gt").replace(reg,"<br/>");
	// 				break;
	// 			// case "address":
	// 			// 	if (typeOf(value)==="array"){
	// 			//
	// 			// 	}
	// 			// 	break;
	// 		}
	// 	}
	// 	return value;
	// }
});