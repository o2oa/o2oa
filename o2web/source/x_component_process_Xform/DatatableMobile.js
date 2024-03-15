MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class DatatableMobile 数据表格组件（移动端）。表格形式的多行数据编辑组件。
 * @o2cn 数据表格移动端
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var datatable = this.form.get("name"); //获取组件
 * //方法2
 * var datatable = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.DatatablePC
 * @o2category FormComponents
 * @since v6.2
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.DatatableMobile = new Class(
	/** @lends MWF.xApplication.process.Xform.DatatablePC# */
	{
		Implements: [Events],
		Extends: MWF.xApplication.process.Xform.DatatablePC,
		loadDatatable: function(){
			this.loading = true;
			this._loadStyles();

			this._loadTitleTr();
			this._loadTemplate();
			this._createTemplateTable();
			this._loadTotalTr();

			this.fireEvent("load");

			this._loadDatatable(function(){
				this._loadImportExportAction();
				this.fieldModuleLoaded = true;
				this.loading = false;
				this.fireEvent("postLoad");
			}.bind(this));
		},
		_removeEl: function(){
			if( this.templateNode )this.templateNode.destroy();

			if( this.totalDiv ){
				this.totalDiv.destroy();
				this.totalDiv = null;
			}
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
				// th.store("dataTable", this);
				th.addClass("mwf_origional");
				if (json){
					// var module = this.form._loadModule(json, th);
					// this.form.modules.push(module);
					// if( json.isShow === false )th.hide(); //隐藏列
					if((json.total === "number") || (json.total === "count"))this.totalFlag = true;
				}
			}.bind(this));
		},
		_loadTemplate: function(){
			this.templateJson = {};

			var trs = this.table.getElements("tr");
			this.templateTr = trs[trs.length-1];

			var tds = this.templateTr.getElements("td");
			if (this.json.border) {
				tds.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border,
					"background": "transparent"
				});
			}
			if (this.json.contentStyles)tds.setStyles(this.json.contentStyles);

			//datatable$Data Module
			var idx = 0;
			tds.each(function(td, index){
				var json = this.form._getDomjson(td);
				// td.store("dataTable", this);
				td.addClass("mwf_origional");
				if (json){
					// var module = this.form._loadModule(json, td);
					// this.form.modules.push(module);
					if( json.cellType === "sequence" )td.addClass("mwf_sequence"); //序号列

					if( json.isShow === false ){
						td.hide(); //隐藏列
					}else{
						if ((idx%2)===0 && this.json.zebraColor){
							td.setStyle("background-color", this.json.zebraColor);
						}else if(this.json.backgroundColor){
							td.setStyle("background-color", this.json.backgroundColor);
						}
						idx++;
					}
				}
			}.bind(this));

			// var moduleNodes = this.form._getModuleNodes(this.templateTr);
			// moduleNodes.each(function (node) {
			// 	if (node.get("MWFtype") !== "form") {
			// 		var json = this.form._getDomjson(node);
			// 		this.templateJson[json.id] = json ;
			// 	}
			// }.bind(this));
			this.templateTr.hide();
		},
		_createTemplateTable: function(){
			this.templateNode = new Element("div").inject(this.node);

			var titleDiv = new Element("div", {"styles": this.json.itemTitleStyles}).inject(this.templateNode);
			titleDiv.setStyle("overflow", "hidden");
			new Element("div.sequenceDiv", {
				"styles": {"float": "left"},
				"text": MWF.xApplication.process.Xform.LP.item
			}).inject(titleDiv);
			new Element("div.mwf_sequence", { "styles": {"float": "left"} }).inject(titleDiv);
			new Element("div.mwf_editaction", { "styles": this.form.css.mobileDatagridActionNode }).inject(titleDiv);

			var table = new Element("table").inject(this.templateNode);
			if (this.json.border) {
				table.setStyles({
					"border-top": this.json.border,
					"border-left": this.json.border
				});
			}
			table.setStyles(this.json.tableStyles);
			table.set(this.json.properties);

			var ths = this.titleTr.getElements("th");
			var tds = this.templateTr.getElements("td");
			ths.each(function(th, index){
				var newTr = new Element("tr").inject(table);

				var thJson = this.form._getDomjson(th);
				var newTh = th.clone().inject(newTr);
				newTh.set("html", th.get("html"));
				newTh.set("MWFId",th.get("id"));
				if( thJson.isShow === false )newTr.hide();

				var moduleJson;
				var td = tds[index];
				var newTd = td.clone().inject(newTr);
				newTd.set("html", td.get("html"));
				newTd.set("MWFId",td.get("id"));
			}.bind(this));

			this.templateHtml = this.templateNode.get("html");

			this.table.hide();
			this.templateNode.hide();
		},
		_loadTotalTr: function(){
			if( !this.totalFlag )return;
			this.totalDiv = new Element("div.mwf_totaltr", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}).inject(this.node);

			if( this.isShowSectionKey() && !(this.json.totalRowBySection || [] ).contains("module")){
				this.totalDiv.hide()
			}
			if( this.isShowSectionBy() && !(this.json.totalRowBy || [] ).contains("module") ){
				this.totalDiv.hide()
			}

			var titleDiv = new Element("div", {"styles": this.json.itemTitleStyles}).inject(this.totalDiv);
			titleDiv.setStyle("overflow", "hidden");
			new Element("div.sequenceDiv", {
				"styles": {"float": "left"},
				"text": MWF.xApplication.process.Xform.LP.amount
			}).inject(titleDiv);

			this.totalTable = new Element("table").inject(this.totalDiv);
			if (this.json.border) {
				this.totalTable.setStyles({
					"border-top": this.json.border,
					"border-left": this.json.border
				});
			}
			this.totalTable.setStyles(this.json.tableStyles);
			this.totalTable.set(this.json.properties);

			var ths = this.titleTr.getElements("th");
			var idx = 0;
			//datatable$Title Module
			ths.each(function(th, index){
				var tr = new Element("tr").inject(this.totalTable);

				var json = this.form._getDomjson(th);
				if (json){
					if ((json.total === "number") || (json.total === "count")){

						var datath = new Element("th").inject(tr);
						datath.set("text", th.get("text"));
						if (this.json.border){
							ths.setStyles({
								"border-bottom": this.json.border,
								"border-right": this.json.border
							});
						}
						datath.setStyles(this.json.titleStyles);

						var datatd = new Element("td").inject(tr);
						if (this.json.border) {
							datatd.setStyles({
								"border-bottom": this.json.border,
								"border-right": this.json.border,
								"background": "transparent"
							});
						}
						datatd.setStyles(this.json.amountStyles);

						if( json.isShow === false ){
							tr.hide(); //隐藏列
						}else{
							if ((idx%2)===0 && this.json.zebraColor){
								datatd.setStyle("background-color", this.json.zebraColor);
							}else if(this.json.backgroundColor){
								datatd.setStyle("background-color", this.json.backgroundColor);
							}
							idx++;
						}

						this.totalColumns.push({
							"th" : datath,
							"td" : datatd,
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
		_getTotalTr: function(){
			return this.totalDiv;
		},
		_createLineNode: function(beforeNode){
			var div;
			if( beforeNode ) {
				div = new Element("div").inject(beforeNode, "after");
			}else if( this.totalDiv ){
				div = new Element("div").inject(this.totalDiv, "before");
			}else{
				div = new Element("div").inject(this.node);
			}
			return div;
		},
		_checkAddAction: function(){
			if( this.data.data && this.data.data.length > 0 ){
				if(this.addAction)this.addAction.hide();
				return;
			}
			if( !this.editable || !this.addable ){
				if(this.addAction)this.addAction.hide();
				return;
			}
			if( !this.addAction ){
				this._createAddAction();
			}else{
				this.addAction.show();
			}
		},
		_createAddAction: function(){
			if( !this.addAction ){
				this.addAction = new Element("div", {"styles": this.form.css.gridMobileActionNode}).inject(this.node, "top");
				this.addAction.set("text", MWF.xApplication.process.Xform.LP.addLine);
				this.addAction.addEvent("click", function(e){
					this._addLine();
				}.bind(this));
			}
		},
		_loadStyles: function(){
			if( this.json.recoveryStyles ){
				this.node.setStyles(this.json.recoveryStyles);
			}
			// if (this.json.border) {
			// 	this.table.setStyles({
			// 		"border-top": this.json.border,
			// 		"border-left": this.json.border
			// 	});
			// }
			// this.node.setStyles(this.json.styles);
			// this.table.setStyles(this.json.tableStyles);
			// this.table.set(this.json.properties);
		},

		_loadSectionLine_EditSection: function(container, data, index, isEdited, isNew){
			var sectionLine = new MWF.xApplication.process.Xform.DatatableMobile.SectionLine(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isEditable: this.editable && this.isSectionLineEditable(data),
				isDeleteable: this.deleteable && this.isSectionLineEditable(data),
				isAddable: this.addable && this.isSectionLineEditable(data)
			});
			// this.fireEvent("beforeLoadLine", [line]);
			sectionLine.load();
			// this.fireEvent("afterLoadLine", [line]);
			return sectionLine;
		},

		_loadSectionLine: function(container, data, index, isEdited, isNew){
			var sectionLine = new MWF.xApplication.process.Xform.DatatableMobile.SectionLine(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isEditable: this.editable,
				isDeleteable: this.deleteable,
				isAddable: this.addable,
				isMergeRead: this.isMergeRead
			});
			// this.fireEvent("beforeLoadLine", [line]);
			sectionLine.load();
			// this.fireEvent("afterLoadLine", [line]);
			return sectionLine;
		},

		_loadLine: function(container, data, index, isEdited, isNew){
			var line = new MWF.xApplication.process.Xform.DatatableMobile.Line(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isEditable: this.editable,
				isDeleteable: this.deleteable,
				isAddable: this.addable,
				isMergeRead: this.isMergeRead
			});
			this.fireEvent("beforeLoadLine", [line]);
			line.load();
			this.fireEvent("afterLoadLine", [line]);
			return line;
		},

		_loadImportExportAction: function(){
			this.impexpNode = this.node.getElement("div.impexpNode");
			if( this.impexpNode )this.impexpNode.destroy();
		}
	});

MWF.xApplication.process.Xform.DatatableMobile$Title = new Class({
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		if(this.json.recoveryStyles){
			this.node.setStyles(this.json.recoveryStyles);
		}
		if (this.json.prefixIcon || this.json.suffixIcon){
			var text = this.node.get("text");
			this.node.empty();

			var lineheight = this.node.getStyle("line-height") || "28px";
			this.wrapNode = new Element("div", {
				"styles": {
					"display": "flex",
					"align-items": "center"
					// "justify-content": "center"
				}
			}).inject(this.node);

			if (this.json.prefixIcon){
				this.prefixNode = new Element("div", {"styles": {
						"width": "20px",
						"min-width": "20px",
						"height": lineheight,
						"background": "url("+this.json.prefixIcon+") center center no-repeat"
					}}).inject(this.wrapNode);
			}

			this.textNode = new Element("div", {"styles": {
					"line-height": lineheight,
					"vertical-align": "top",
					"padding": "1px"
				}, "text": text}).inject(this.wrapNode);

			if (this.json.suffixIcon){
				this.suffixNode = new Element("div", {"styles": {
						"width": "20px",
						"min-width": "20px",
						"height": lineheight,
						"background": "url("+this.json.suffixIcon+") center center no-repeat"
					}}).inject(this.wrapNode);
			}
		}
	}
});

MWF.xApplication.process.Xform.DatatableMobile$Data =  new Class({
	Extends: MWF.APP$Module
});

MWF.xApplication.process.Xform.DatatableMobile.SectionLine =  new Class({
	Extends: MWF.xApplication.process.Xform.DatatablePC.SectionLine,
	_loadLine: function(container, data, index, isEdited, isNew){
		var line = new MWF.xApplication.process.Xform.DatatableMobile.Line(container, this.datatable, data, {
			indexInSectionLine : index,
			indexInSectionLineText : (index+1).toString(),
			index: this.datatable.lineList.length,
			indexText : (this.datatable.lineList.length + 1).toString(),
			isNew: isNew,
			isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.options.isEdited,
			isEditable: this.options.isEditable,
			isDeleteable: this.options.isDeleteable,
			isAddable: this.options.isAddable,
			isMergeRead: this.options.isMergeRead,
			sectionKey: this.sectionKey
		}, this);
		this.datatable.fireEvent("beforeLoadLine", [line]);
		line.load();
		this.datatable.fireEvent("afterLoadLine", [line]);
		return line;
	},
	_createLineNode: function( beforeNode ){
		var div;
		if( beforeNode ){
			div = new Element("div").inject(beforeNode, "after");
		}else if( this.totalDiv ){
			div = new Element("div").inject(this.totalDiv, "before");
		}else{
			div = this.datatable._createLineNode();
			// tr = new Element("tr").inject(this.tBody || this.table);
		}
		return div;
	},
	loadSectionKeyNode: function () {
		debugger;
		var styleName = this.datatable.isShowSectionKey() ? "sectionKeyStyles" : "sectionByStyles";
		var sectionKeyStyles = this.datatable._parseStyles( this.datatable.json[styleName] || {} );
		var keyNode = new Element("div.mwf_sectionkey", {
			styles : sectionKeyStyles
		}).inject( this.sectionKeyNode );
		this.keyNode = keyNode;
		var separator;
		if( this.datatable.isShowSectionKey() ){
			separator = this.datatable.json.keyContentSeparator;
		}else{
			separator = this.datatable.json.keyContentSeparatorSectionBy;
		}
		this.datatable.getSectionKeyWithMerge( this.data, function (key) {
			if( o2.typeOf(key) === "string" ){
				keyNode.set("text", key + (separator || ""));
			}else{
				Promise.resolve(key).then(function (k) {
					keyNode.set("text", k + (separator || ""));
				}.bind(this))
			}
		}.bind(this));
	},
	clearSubModules: function(){
		if( this.isUnchangedAll )return;

		var map = this.unchangedLineMap || {};
		var hasUnchangedLine = Object.keys(map).length > 0;

		if( !hasUnchangedLine ){
			if( this.sectionKeyNode ){
				this.sectionKeyNode.destroy();
				this.sectionKeyNode = null;
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

		if( !hasUnchangedLine ) {
			if( this.totalDiv ){
				this.totalDiv.destroy();
				this.totalDiv = null;
			}
		}
	},
	_loadTotalTr: function(){
		if( !this.datatable.totalFlag )return false;
		this.totalDiv = new Element("div.mwf_totaltr", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}).inject(this.sectionKeyNode, "after");
		if( !this.isTotalTrShow() )this.totalDiv.hide();

		var titleDiv = new Element("div", {"styles": this.datatable.json.itemTitleStyles}).inject(this.totalDiv);
		titleDiv.setStyle("overflow", "hidden");
		new Element("div.sequenceDiv", {
			"styles": {"float": "left"},
			"text": MWF.xApplication.process.Xform.LP.sectionAmount
		}).inject(titleDiv);

		this.totalTable = new Element("table").inject(this.totalDiv);
		if (this.datatable.json.border) {
			this.totalTable.setStyles({
				"border-top": this.json.border,
				"border-left": this.json.border
			});
		}
		this.totalTable.setStyles(this.datatable.json.tableStyles);
		this.totalTable.set(this.datatable.json.properties);

		var ths = this.datatable.titleTr.getElements("th");
		var idx = 0;

		//datatable$Title Module
		ths.each(function(th, index){
			var tr = new Element("tr").inject(this.totalTable);

			var json = this.datatable.form._getDomjson(th);
			if (json){
				if ((json.total === "number") || (json.total === "count")){

					var datath = new Element("th").inject(tr);
					datath.set("text", th.get("text"));
					if (this.datatable.json.border){
						ths.setStyles({
							"border-bottom": this.datatable.json.border,
							"border-right": this.datatable.json.border
						});
					}
					datath.setStyles(this.datatable.json.titleStyles);

					var datatd = new Element("td").inject(tr);
					if (this.datatable.json.border) {
						datatd.setStyles({
							"border-bottom": this.datatable.json.border,
							"border-right": this.datatable.json.border,
							"background": "transparent"
						});
					}
					datatd.setStyles(this.datatable.json.sectionAmountStyles || {});

					if( json.isShow === false ){
						tr.hide(); //隐藏列
					}else{
						if ((idx%2)===0 && this.datatable.json.zebraColor){
							datatd.setStyle("background-color", this.datatable.json.zebraColor);
						}else if(this.datatable.json.backgroundColor){
							datatd.setStyle("background-color", this.datatable.json.backgroundColor);
						}
						idx++;
					}

					this.totalColumns.push({
						"th" : datath,
						"td" : datatd,
						"index": index,
						"type": json.total
					})
				}
			}
		}.bind(this));

		var tds = this.datatable.templateTr.getElements("td");
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
	_getTotalTr: function(){
		return this.totalDiv;
	},
	getLastTr: function () {
		if( this.totalDiv )return this.totalDiv;
		if( this.lineList.length )return this.lineList.getLast().node;
		return this.sectionKeyNode;
	}
});

MWF.xApplication.process.Xform.DatatableMobile.Line =  new Class({
	Extends: MWF.xApplication.process.Xform.DatatablePC.Line,

	load: function(){
		if( !this.datatable.multiEditMode && this.options.isEdited )this.datatable.currentEditedLine = this;
		this.node.addClass("mwf_datatable");
		this.node.setStyles( Object.merge({"margin-bottom": "10px"}, this.datatable.json.styles||{} )); //"overflow": "hidden",

		this.loadModules();
		this.loadSequence();
		this.createActions();
		// this.loadZebraStyle();
		// this.loadEditedStyle();


		if( !this.datatable.multiEditMode )this.originalData = Object.clone(this.data);

		// if(this.options.isNew && this.options.isEdited){
		// 	debugger;
		// 	this.data = this.getData();
		// 	if( !this.datatable.multiEditMode )this.originalData = Object.clone(this.data);
		// 	this.options.isNew = false;
		// }
	},
	createActions: function () {
		//不允许编辑，直接返回
		if(!this.options.isEditable)return;

		var editActionTd = this.node.getElement(".mwf_editaction");
		//this.moveActionTd = this.node.getElement(".moveAction");

		if(this.datatable.multiEditMode){ //多行编辑模式
			if(this.options.isDeleteable)this.createDelAction(editActionTd);
			if(this.options.isAddable)this.createAddAction(editActionTd);
		}else{ //单行编辑模式
			if(this.options.isDeleteable)this.createDelAction(editActionTd);
			if(this.options.isEditable)this.createEditAction(editActionTd);
			if(this.options.isAddable)this.createAddAction(editActionTd);
			this.createCancelEditAction(editActionTd);
			this.createCompleteAction(editActionTd);
			this.checkActionDisplay();
		}

	},
	checkActionDisplay: function(){
		if( this.options.isEdited ){
			if( this.addLineAction )this.addLineAction.hide();
			if( this.editLineAction )this.editLineAction.hide();
			if( this.delLineAction )this.delLineAction.hide();
			if( this.completeLineAction )this.completeLineAction.show();
			if( this.cancelLineEditAction )this.cancelLineEditAction.show();
		}else{
			if( this.addLineAction )this.addLineAction.show();
			if( this.editLineAction )this.editLineAction.show();
			if( this.delLineAction )this.delLineAction.show();
			if( this.completeLineAction )this.completeLineAction.hide();
			if( this.cancelLineEditAction )this.cancelLineEditAction.hide();
		}
	},
	createEditAction: function(td){
		this.editLineAction = new Element("div", {
			"styles": this.form.css.mobileDatagridEditActionNode,
			"text": MWF.xApplication.process.Xform.LP.edit,
			"events": {
				"click": function(ev){
					if( !this.options.isEdited ){
						this.datatable._changeEditedLine(this)
					}
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createAddAction: function(td){
		this.addLineAction = new Element("div", {
			"styles": this.form.css.mobileDatagridAddActionNode,
			"text": MWF.xApplication.process.Xform.LP.add,
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
			"styles": this.form.css.mobileDatagridCompleteActionNode,
			"text": MWF.xApplication.process.Xform.LP.completedEdit,
			"events": {
				"click": function(ev){
					this.datatable._completeLineEdit(ev, true);
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
	},
	createCancelEditAction: function(td){
		this.cancelLineEditAction = new Element("div", {
			"styles": this.form.css.mobileDatagridDelActionNode,
			"text": MWF.xApplication.process.Xform.LP.cancelEdit,
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
			"styles": this.form.css.mobileDatagridCancelActionNode,
			"text": MWF.xApplication.process.Xform.LP["delete"],
			"events": {
				"click": function(ev){
					this.datatable._deleteLine( ev, this );
					// if( this.datatable.currentEditedLine === this )this.datatable.currentEditedLine = null;
					ev.stopPropagation();
				}.bind(this)
			}
		}).inject(td);
		this.delLineAction.show()
	}
});