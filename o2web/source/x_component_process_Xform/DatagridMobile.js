MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class DatagridMobile 数据网格组件（移动端）。从v6.2开始建议用数据表格(Datatable)代替。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var datagrid = this.form.get("name"); //获取组件
 * //方法2
 * var datagrid = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @deprecated
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.DatagridMobile = new Class(
/** @lends MWF.xApplication.process.Xform.DatagridMobile# */
{
    Implements: [Events],
    Extends: MWF.APP$Module,
    isEdit: false,
    options: {
        /**
         * 当前条目编辑完成时触发。通过this.event可以获取对应的table。
         * @event MWF.xApplication.process.Xform.DatagridMobile#completeLineEdit
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 添加条目时触发。通过this.event可以获取对应的table。
         * @event MWF.xApplication.process.Xform.DatagridMobile#addLine
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 删除条目前触发。通过this.event可以获取对应的table。
         * @event MWF.xApplication.process.Xform.DatagridMobile#deleteLine
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 删除条目后触发。
         * @event MWF.xApplication.process.Xform.DatagridMobile#afterDeleteLine
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 编辑条目时触发。
         * @event MWF.xApplication.process.Xform.DatagridMobile#editLine
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["queryLoad","postLoad","load","completeLineEdit", "addLine", "deleteLine", "afterDeleteLine","editLine"]
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
        this.node.setStyle("overflow-x", "hidden");
        this.node.setStyle("overflow-y", "hidden");
        this.table = this.node.getElement("table");

        this.createMobileTable();


        this.editable = (!this.readonly);
        if (this.editable && this.json.editableScript && this.json.editableScript.code){
            this.editable = this.form.Macro.exec(((this.json.editableScript) ? this.json.editableScript.code : ""), this);
        }
        //this.editable = false;

        this.deleteable = this.json.deleteable !== "no";
        this.addable = this.json.addable !== "no";

        this.gridData = this._getValue();

        this.totalModules = [];
        this._loadDatagridTitleModules();

        if (this.editable!=false){
            this._loadDatagridDataModules();
            //this._addTitleActionColumn();
            // this._loadEditDatagrid();
            // //this._loadReadDatagrid();
            // this.fireEvent("postLoad");
            // this.fireEvent("load");
            this._loadEditDatagrid(function(){
                this.fireEvent("postLoad");
                this.fireEvent("load");
            }.bind(this));
        }else{
            this._loadDatagridDataModules();
            this._loadReadDatagrid(function(){
                this.fireEvent("postLoad");
                this.fireEvent("load");
            }.bind(this));

            // this._loadReadDatagrid();
            // this.fireEvent("postLoad");
            // this.fireEvent("load");
        }
    },
    createMobileTable: function(){
        var mobileTable = new Element("table").inject(this.node);
        mobileTable.set(this.json.properties);
        //mobileTable.setStyle("display", "none");
        //mobileTable.setStyle("margin-bottom", "10px");
        var trs = this.table.getElements("tr");
        var titleTds = trs[0].getElements("th");
        var contentTds = trs[1].getElements("td");
        titleTds.each(function(titleTd, i){
            var mobileTr = mobileTable.insertRow();
            //var mobileTr = new Element("tr").inject(mobileTable);
            var th = new Element("th").inject(mobileTr);
            th.set({
                "html": titleTd.get("html"),
                "id": titleTd.get("id"),
                "mwftype": titleTd.get("mwftype")
            });
            var td = new Element("td").inject(mobileTr);
            td.set({
                "html": contentTds[i].get("html"),
                "id": contentTds[i].get("id"),
                "mwftype": contentTds[i].get("mwftype")
            });

            var json = this.form._getDomjson(titleTd);
            if( json && json.isShow === false )mobileTr.hide();
        }.bind(this));
        this.table.destroy();
        this.table = null;
        this.table = mobileTable;
    },

    _loadStyles: function(){
        //this.table.setStyles(this.json.tableStyles);
        this.node.setStyles(this.json.styles);
        var tables = this.node.getElements("table");
        tables.each(function(table){
            table.setStyles(this.json.tableStyles);
        }.bind(this));

    },
    _getValue: function(){
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = [];
        value = this._getBusinessData();
        if (!value){
            if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
            if (!value.isAG) if (o2.typeOf(value)=="array") value = {"data": value || []};
        }
        return value || [];
    },
    getValue: function(){
        return this._getValue();
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
                    // 	}
                    // }
                    break;
                case "Radio":
                    var ops = module.node.getElements("input");
                    for (var i=0; i<ops.length; i++){
                        if (ops[i].value == value){
                            return ops[i].get("showText");
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
            }
        }
        return value;
    },

    editValidation: function(){
        var flag = true;
        this.editModules.each(function(field, key){
            if (field.json.type!="sequence"){
                field.validationMode();
                if (!field.validation()) flag = false;
            }
        }.bind(this));
        return flag;
    },

    _loadReadDatagrid: function(callback){
        var p = o2.promiseAll(this.gridData).then(function(v){
            this.gridData = v;
            if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
            this.__loadReadDatagrid(callback);
            this.moduleValueAG = null;
            return v;
        }.bind(this), function(){});
        this.moduleValueAG = p;
        if (this.moduleValueAG) this.moduleValueAG.then(function(){
            this.moduleValueAG = null;
        }.bind(this), function(){
            this.moduleValueAG = null;
        }.bind(this));

        // if (this.gridData && this.gridData.isAG){
        //     this.moduleValueAG = this.gridData;
        //     this.gridData.addResolve(function(v){
        //         this.gridData = v;
        //         this._loadReadDatagrid(callback);
        //     }.bind(this));
        // }else{
        //     if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
        //     this.__loadReadDatagrid(callback);
        //     this.moduleValueAG = null;
        // }
    },

    __loadReadDatagrid: function(callback){
        //this.gridData = this._getValue();


        var titleHeaders = this.table.getElements("th");
        var tds = this.table.getElements("td");

        if (this.gridData.data){
            this.gridData.data.each(function(data, idx){
                var dataDiv = new Element("div.dataDiv", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}); //.inject(this.node);

                if (this.totalDiv){
                    dataDiv.inject(this.totalDiv, "before");
                }else{
                    dataDiv.inject(this.node);
                }

                this._createItemTitleNode(dataDiv, idx);

                var tableDiv = new Element("div", {"styles": this.form.css.gridMobileTableNode }).inject(dataDiv);
                var table = new Element("table").inject(tableDiv);
                table.set(this.json.properties);
                table.store("data", data);
                //table.setStyle("margin-bottom", "10px");
                titleHeaders.each(function(th, index){
                    var tr = table.insertRow(index);
                    var datath = new Element("th").inject(tr);
                    datath.set("text", th.get("text"));
                    datath.setStyle("width", "30%");

                    var cell = tr.insertCell(1);
                    cell.set("MWFId", tds[index].get("id"));

                    var cellData = data[th.get("id")];

                    if( typeOf( cellData ) !== "array" ) {
                        if (cellData) {
                            for (key in cellData) {
                                var v = cellData[key];

                                var module = this.editModules[index];
                                if (module && module.json.type == "ImageClipper") {
                                    this._createImage(cell, module, v);
                                } else if (module && (module.json.type == "Attachment" || module.json.type == "AttachmentDg")) {
                                    this._createAttachment(cell, module, v);
                                } else {
                                    text = this._getValueText(index, v);
                                    if (module && module.json.type == "Textarea") {
                                        cell.set("html", text);
                                    } else {
                                        cell.set("text", text);
                                    }
                                    //cell.set("text", text);
                                }


                                // if (typeOf(v)==="array"){
                                //     var textArray = [];
                                //     v.each( function( item ){
                                //         if (typeOf(item)==="object"){
                                //             textArray.push( item.name+((item.unitName) ? "("+item.unitName+")" : "") );
                                //         }else{
                                //             textArray.push(item);
                                //         }
                                //     }.bind(this));
                                //     cell.set("text", textArray.join(", "));
                                // }else if (typeOf(v)==="object"){
                                //     cell.set("text", v.name+((v.unitName) ? "("+v.unitName+")" : ""));
                                // }else{
                                //     cell.set("text", v);
                                // }
                                break;
                                //
                                // cell.set("text", cellData[key]);
                                // break;
                            }
                        } else { //Sequence
                            cell.setStyle("text-align", "left");
                            cell.set("text", idx + 1);
                        }
                    }

                    var json = this.form._getDomjson(th);
                    if( json && json.isShow === false )tr.hide();

                }.bind(this));
            }.bind(this));
        }
        if (callback) callback();
        //this._loadTotal();
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
        //     this.moduleValueAG = this.gridData;
        //     this.gridData.addResolve(function(v){
        //         this.gridData = v;
        //         this._loadEditDatagrid(callback);
        //     }.bind(this));
        // }else{
        //     if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
        //     this.__loadEditDatagrid(callback);
        //     this.moduleValueAG = null;
        // }
    },
    __loadEditDatagrid: function(callback){
        //this._createHelpNode();

        //this.gridData = this._getValue();

        var titleHeaders = this.table.getElements("th");
        var tds = this.table.getElements("td");

        var _self = this;

        if (this.gridData.data.length){
            if( this.addAction )this.addAction.setStyle("display","none");
            this.gridData.data.each(function(data, idx){
                var dataDiv = new Element("div.dataDiv", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}); //.inject(this.node);

                if (this.totalDiv){
                    dataDiv.inject(this.totalDiv, "before");
                }else{
                    dataDiv.inject(this.node);
                }

                this._createItemTitleNode(dataDiv, idx);

                var tableDiv = new Element("div", {"styles": this.form.css.gridMobileTableNode }).inject(dataDiv);
                var table = new Element("table").inject(tableDiv);
                table.set(this.json.properties);
                table.store("data", data);
                //table.setStyle("margin-bottom", "10px");
                titleHeaders.each(function(th, index){
                    var tr = table.insertRow(index);
                    var datath = new Element("th").inject(tr);
                    datath.set("text", th.get("text"));
                    datath.setStyle("width", "30%");

                    var cell = tr.insertCell(1);
                    cell.set("MWFId", tds[index].get("id"));

                    var cellData = data[th.get("id")];
                    if( typeOf( cellData ) !== "array" ){
                        if ( cellData ){
                            for (key in cellData){
                                var v = cellData[key];

                                var module = this.editModules[index];
                                if( module && module.json.type == "ImageClipper" ){
                                    this._createImage( cell, module, v )
                                }else if( module && (module.json.type == "Attachment" || module.json.type == "AttachmentDg") ){
                                    this._createAttachment( cell, module, v );
                                }else{
                                    text = this._getValueText(index, v);
                                    if( module && module.json.type == "Textarea" ){
                                        cell.set("html", text);
                                    }else{
                                        cell.set("text", text);
                                    }
                                    //cell.set("text", text);
                                }

                                // if (typeOf(v)==="object"){
                                //     cell.set("text", v.name+((v.unitName) ? "("+v.unitName+")" : ""));
                                // }else{
                                //     cell.set("text", v);
                                // }
                                break;
                                //
                                // cell.set("text", cellData[key]);
                                // break;
                            }
                        }else{ //Sequence
                            cell.setStyle("text-align", "left");
                            cell.set("text", idx+1);
                        }
                    }

                    var json = this.form._getDomjson(th);
                    if( json && json.isShow === false )tr.hide();
                }.bind(this));
                var size = dataDiv.getSize();
                //dataDiv.setStyle("height", ""+size.y+"px");

                //dataDiv.addEvent("touchstart", function(e){_self.actionTouchstart(this, e);});
                //dataDiv.addEvent("touchmove", function(e){_self.actionTouchmove(this, e);});
                //dataDiv.addEvent("touchend", function(e){_self.actionTouchend(this, e);});
                //dataDiv.addEvent("touchcancel", function(e){_self.actionTouchcancel(this, e);});
                //
                //dataDiv.addEvent("mousedown", function(e){_self._actionMousedown(this, e);});
                //dataDiv.addEvent("mouseup", function(e){_self._actionMouseup(this, e);});

                //this.showMoveAction(dataDiv, 60);
                //this.showEndMoveAction(dataDiv);
            }.bind(this));
        }else{
            if (this.addAction){
                if( this.addable )this.addAction.setStyle("display", "block");
            }else{
                if( this.addable )this._loadAddAction();
            }
            // this._loadAddAction();
        }
        if (callback) callback();
        //this._loadTotal();
    },
    _loadActions: function(titleDiv){
        var actionNode = new Element("div", {
            "styles": this.form.css.mobileDatagridActionNode
        }).inject(titleDiv);

        var delAction = new Element("div", {
            "styles": this.form.css.mobileDatagridDelActionNode,
            "text": MWF.xApplication.process.Xform.LP["delete"]
        }).inject(actionNode);
        if( !this.deleteable )delAction.hide();

        var editAction = new Element("div", {
            "styles": this.form.css.mobileDatagridEditActionNode,
            "text": MWF.xApplication.process.Xform.LP.edit
        }).inject(actionNode);

        var addAction = new Element("div", {
            "styles": this.form.css.mobileDatagridAddActionNode,
            "text": MWF.xApplication.process.Xform.LP.add
        }).inject(actionNode);
        if( !this.addable )addAction.hide();

        var cancelAction = new Element("div", {
            "styles": this.form.css.mobileDatagridCancelActionNode,
            "text": MWF.xApplication.process.Xform.LP.cancelEdit
        }).inject(actionNode);
        var completeAction = new Element("div", {
            "styles": this.form.css.mobileDatagridCompleteActionNode,
            "text": MWF.xApplication.process.Xform.LP.completedEdit
        }).inject(actionNode);


        var _self = this;
        if( this.deleteable )delAction.addEvent("click", function(e){
            _self._deleteLine(this.getParent().getParent().getParent(), e);
        });
        editAction.addEvent("click", function(e){
            _self._editLine(this.getParent().getParent().getParent(), e);
        });
        completeAction.addEvent("click", function(e){
            _self._completeLineEdit();
        });
        cancelAction.addEvent("click", function(e){
            _self._cancelLineEdit(e);
        });
        if( this.addable )addAction.addEvent("click", function(e){
            _self._addLine();
        });
    },
    _createImage : function( cell, module, data ){
        cell.empty();
        if( !data )return;
        var img = new Element("img",{
            src : MWF.xDesktop.getImageSrc( data )
        }).inject( cell, "top" );
        img.setStyles({
            "max-width": "90%"
        })
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
        data.each(function(d){
            var att = module.attachmentController.attachments.find(function(a){
                return d.id == a.data.id;
            });
            if (att) module.attachmentController.removeAttachment(att);
        });
        module.setAttachmentBusinessData();


        var attachmentController = new MWF.xApplication.process.Xform.AttachmentController(cell, module, options);
        attachmentController.load();

        data.each(function (att) {
            var attachment = this.form.businessData.attachmentList.find(function(a){
                return a.id==att.id;
            });
            var attData = attachment || att;
            //if (att.site===this.json.id || (this.json.isOpenInOffice && this.json.officeControlName===att.site)) this.attachmentController.addAttachment(att);
            attachmentController.addAttachment(attData);
        }.bind(this));
    },
    _createItemTitleNode: function(node, idx){
        var n = idx+1;
        var titleDiv = new Element("div", {"styles": this.json.itemTitleStyles}).inject(node);
        titleDiv.setStyle("overflow", "hidden");
        var textNode = new Element("div.sequenceDiv", {
            "styles": {"float": "left"},
            "text": MWF.xApplication.process.Xform.LP.item+n
        }).inject(titleDiv);
        //if (idx==0){
        if( this.editable != false ){
            this._loadActions(titleDiv);
        }

        //}
    },
    _createHelpNode: function(){
        this.helpNode = new Element("div", {"styles": this.form.css.mobileGridHelpNode}).inject(this.node, "top");
        this.helpContentNode = new Element("div", {"styles": this.form.css.mobileGridHelpContentNode}).inject(this.helpNode);
        this.helpContentNode.set("html", MWF.xApplication.process.Xform.LP.mobileGridHelp);
        new mBox.Tooltip({
            content: this.helpContentNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.helpNode,
            transition: 'flyin',
            position: {
                x: ['right'],
                y: ['center']
            },
            event: 'click'
        });
    },
    _editLine:function(node){
        if (this.isEdit){
            if (!this._completeLineEdit()) return false;
        }

        this.currentEditLine = node;
        var currentEditTable = node.getElement("table");
        if (this.currentEditLine){
            this.table.setStyles({
                "background-color": "#fffeb5",
                "display": "table"
            });
            this.table.inject(currentEditTable, "after");
            //this.currentEditLine.setStyle("display", "none");
            currentEditTable.setStyle("display", "none");

            var actions = this.currentEditLine.getFirst("div").getLast("div").getElements("div");
            if (actions[0]) actions[0].setStyle("display", "none");
            if (actions[1]) actions[1].setStyle("display", "none");
            if (actions[2]) actions[2].setStyle("display", "none");
            if (actions[3]) actions[3].setStyle("display", "block");
            if (actions[4]) actions[4].setStyle("display", "block");

            //this.addAction.inject(this.table, "after");
            //this.addAction.set("text", MWF.xApplication.process.Xform.LP.completedEdit);
            //this.addAction.removeEvents("click");
            //this.addAction.addEvent("click", function(){
            //    this._completeLineEdit();
            //}.bind(this));


            var data = this.currentEditLine.getElement("table").retrieve("data");
            var titleThs = this.table.getElements("th");
            titleThs.each(function(th, idx){
                var id = th.get("id");
                var module = this.editModules[idx];
                if (module){
                    if (module.json.type=="sequence"){
                        module.node.set("text", this.currentEditLine.getElement("table").getElements("tr")[idx].getElement("td").get("text"));
                    }else {
                        if (data[id]) {
                            module.setData(data[id][module.json.id]);
                        } else {
                            module.setData(null);
                        }
                    }
                }
            }.bind(this));

            //var cellIdx = this.currentEditLine.getElements("td").indexOf(td);
            //var module = this.editModules[cellIdx-1];
            //if (module) module.focus();

            this.fireEvent("editLine");

            this.isEdit =true;
        }
        this.validationMode();
    },
    _loadAddAction: function(){
        if( !this.addAction ){
            this.addAction = new Element("div", {"styles": this.form.css.gridMobileActionNode}).inject(this.node, "top");
            this.addAction.set("text", MWF.xApplication.process.Xform.LP.addLine);
            this.addAction.addEvent("click", function(){
                this._addLine();
            }.bind(this));
        }
    },
    _addLine: function(){
        if (this.isEdit){
            if (!this._completeLineEdit()) return false;
        }
        if (this.addAction) this.addAction.setStyle("display", "none");

        var tables = this.node.getElements("table");
        var idx;
        var dataDiv = new Element("div.datagridDataDiv", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}});
        if (this.totalDiv){
            idx = tables.length-2;
            dataDiv.inject(this.totalDiv, "before");
        }else{
            idx = tables.length-1;
            dataDiv.inject(this.node);
        }
        this._createItemTitleNode(dataDiv, idx);

        var tableDiv = new Element("div", {"styles":  this.form.css.gridMobileTableNode }).inject(dataDiv);

        this.table.setStyles({
            "background-color": "#fffeb5",
            "display": "table"
        });

        this.currentEditLine = null;
        this.table.inject(tableDiv);
        this.isEdit = true;

        var actions = dataDiv.getFirst("div").getLast("div").getElements("div");
        if (actions[0]) actions[0].setStyle("display", "none");
        if (actions[1]) actions[1].setStyle("display", "none");
        if (actions[2]) actions[2].setStyle("display", "none");
        if (actions[3]) actions[3].setStyle("display", "block");
        if (actions[4]) actions[4].setStyle("display", "block");

        if (!dataDiv.isIntoView()) dataDiv.scrollIntoView(true);

        //this.addAction.set("text", MWF.xApplication.process.Xform.LP.completedEdit);
        //this.addAction.removeEvents("click");
        //this.addAction.addEvent("click", function(){
        //    this._completeLineEdit();
        //}.bind(this));

        this.validationMode();
        this.fireEvent("addLine", [this.table]);
    },
    _cancelLineEdit : function(e){
        var datagrid = this;
        var _self = this;
        this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.cancelDatagridLineEditTitle, MWF.xApplication.process.Xform.LP.cancelDatagridLineEdit, 300, 120, function(){

            datagrid.isEdit = false;

            if (datagrid.currentEditLine) {
                // datagrid.currentEditLine.setStyle("display", "table-row");

                var currentEditTable = datagrid.currentEditLine.getElement("table");
                currentEditTable.setStyle("display", "table");

                var actions = datagrid.currentEditLine.getFirst("div").getLast("div").getElements("div");
                if (actions[0] && this.deleteable ) actions[0].setStyle("display", "block");
                if (actions[1] ) actions[1].setStyle("display", "block");
                if (actions[2] && this.addable ) actions[2].setStyle("display", "block");
                if (actions[3]) actions[3].setStyle("display", "none");
                if (actions[4]) actions[4].setStyle("display", "none");

                datagrid._editorTrGoBack();
            }else{
                var datagridDataDiv = e.target.getParent(".datagridDataDiv");
                datagrid._editorTrGoBack();
                if(datagridDataDiv)datagridDataDiv.destroy();
            }

            datagrid.editModules.each(function(module){
                if (module && (module.json.type=="Attachment" || module.json.type=="AttachmentDg")){
                    module.attachmentController.attachments.each(function(att){
                        datagrid.form.workAction.deleteAttachment(att.data.id, datagrid.form.businessData.work.id);
                    });
                    module.attachmentController.clear();
                }
            });

            datagrid.currentEditLine = null;

            if (!_self.gridData.data.length){
                if (_self.addAction){
                    if( _self.addable )_self.addAction.setStyle("display", "block");
                }else{
                    if( _self.addable )_self._loadAddAction();
                }
            }

            this.close();

            datagrid.fireEvent("cancelLineEdit");
        }, function(){
            // var color = currentTr.retrieve("bgcolor");
            // currentTr.tween("background", color);
            this.close();
        }, null, null, this.form.json.confirmStyle);
    },
    _completeLineEdit: function( ev ){
        if (!this.editValidation()){
            return false;
        }

        this.isEdit = false;
        var saveFlag = false;
        //var flag = true;

        var griddata = {};
        var dataNode = null;
        var table;

        if (this.currentEditLine){
            dataNode = this.currentEditLine;
            griddata = dataNode.getElement("table").retrieve("data");
            this.currentEditLine.getElement("table").setStyle("display", "table");
            table = dataNode.getElement("table");

            var actions = this.currentEditLine.getFirst("div").getLast("div").getElements("div");
            if (actions[0] && this.deleteable ) actions[0].setStyle("display", "block");
            if (actions[1]) actions[1].setStyle("display", "block");
            if (actions[2] && this.addable ) actions[2].setStyle("display", "block");
            if (actions[3]) actions[3].setStyle("display", "none");
            if (actions[4]) actions[4].setStyle("display", "none");
        }else{
            //dataNode = new Element("div", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}).inject(this.table, "before");
            //var tableDiv = new Element("div", {"styles": {"overflow": "hidden"}}).inject(dataNode);

            var dataDiv = this.table.getParent(".datagridDataDiv");
            if(dataDiv){
                dataDiv.removeClass("datagridDataDiv").addClass("dataDiv");
            }

            var actions = this.table.getParent().getPrevious("div").getLast("div").getElements("div");
            if (actions[0] && this.deleteable ) actions[0].setStyle("display", "block");
            if (actions[1]) actions[1].setStyle("display", "block");
            if (actions[2] && this.addable ) actions[2].setStyle("display", "block");
            if (actions[3]) actions[3].setStyle("display", "none");
            if (actions[4]) actions[4].setStyle("display", "none");

            table = new Element("table", {
                styles : this.json.tableStyles
            }).inject(this.table, "before");
            table.set(this.json.properties);
            griddata = {};

            //dataNode.addEvent("touchstart", function(e){_self.actionTouchstart(this, e);});
            //dataNode.addEvent("touchmove", function(e){_self.actionTouchmove(this, e);});
            //dataNode.addEvent("touchend", function(e){_self.actionTouchend(this, e);});
            //dataNode.addEvent("touchcancel", function(e){_self.actionTouchcancel(this, e);});
        }

        var tables = this.node.getElements("table");
        var titleHeaders = this.table.getElements("th");
        var tds = this.table.getElements("td");
        var dataRows = table.getElements("tr");

        titleHeaders.each(function(th, idx){
            var dataRow = dataRows[idx];
            var id = th.get("id");
            var module = this.editModules[idx];
            if (module){
                var data;
                if (module.json.type=="sequence"){
                    var i;
                    if (!this.currentEditLine){
                        i = tables.length-1;
                        if (this.totalTable) i = tables.length-2;
                    }else{
                        i = this.currentEditLine.getElement("table").getElements("tr")[idx].getElement("td").get("text");
                    }
                    data = {"value": [i], "text": [i]};
                }else if( module.json.type=="Attachment" || module.json.type == "AttachmentDg" ) {
                    saveFlag = true;
                    var data = module.getTextData();
                    //data.site = module.json.site;
                    if (!griddata[id]) griddata[id] = {};
                    griddata[id][module.json.id] = data;
                // }else if( ["Orgfield","Personfield","Org","Address"].contains(module.json.type) ){
                //     data = module.getTextData();
                //     if (!griddata[id]) griddata[id] = {};
                //     griddata[id][module.json.id] = data.value;
                }else{
                    data = module.getTextData();
                    //if (data.value[0]) flag = false;
                    if (data.value.length<2){
                        if (!griddata[id]) griddata[id] = {};
                        griddata[id][module.json.id] = data.value[0];
                    }else{
                        if (!griddata[id]) griddata[id] = {};
                        griddata[id][module.json.id] = data.value;
                    }
                }

                var cell;
                // var text = this._getValueText(idx, data.text.join(", "));

                if (dataRow){
                    cell = dataRow.getElement("td");
                    if( module.json.type == "ImageClipper" ){
                        this._createImage( cell, module, data.text );
                    }else if( module.json.type == "Attachment" || module.json.type == "AttachmentDg" ){
                        this._createAttachment( cell, module, data );
                    }else{
                        var text = this._getValueText(idx, data.text.join(", "));
                        if( module && module.json.type == "Textarea" ){
                            cell.set("html", text);
                        }else{
                            cell.set("text", text);
                        }
                        //cell.set("text", data.text.join(", "));
                    }
                }else{
                    dataRow = table.insertRow(idx);
                    var datath = new Element("th").inject(dataRow);
                    datath.set("text", th.get("text"));
                    datath.setStyle("width", "30%");

                    cell = dataRow.insertCell(1);
                    cell.set("MWFId", tds[idx].get("id"));

                    var cellData = data[th.get("id")];
                    if( module.json.type == "ImageClipper" ){
                        this._createImage( cell, module, data.text );
                    }else if( module.json.type == "Attachment" || module.json.type == "AttachmentDg" ){
                        this._createAttachment( cell, module, data );
                    }else{
                        var text = this._getValueText(idx, data.text.join(", "));
                        if( module && module.json.type == "Textarea" ){
                            cell.set("html", text);
                        }else{
                            cell.set("text", text);
                        }
                        //cell.set("text", data.text.join(", "));
                    }
                }
            }else{
                if (!dataRow) {
                    dataRow = table.insertRow(idx);
                    var datath1 = new Element("th").inject(dataRow);
                    datath1.set("text", th.get("text"));
                    datath1.setStyle("width", "30%");
                    cell = dataRow.insertCell(1);
                }
            }

            var json = this.form._getDomjson(th);
            if( json && json.isShow === false )dataRow.hide();

            module = null;
        }.bind(this));

        table.store("data", griddata);
        table.setStyle("display", "table");

        //if (flag){
        //    newTr.destroy();
        //}
        this.currentEditLine = null;

        this._editorTrGoBack();

        this.getData();
        this._loadDatagridStyle();

        this.validationMode();
        this.fireEvent("completeLineEdit", [table]);

        if (this.addAction){
            this.addAction.set("text", MWF.xApplication.process.Xform.LP.addLine);
            this.addAction.removeEvents("click");
            this.addAction.addEvent("click", function(){
                this._addLine();
            }.bind(this));
        }

        if(saveFlag){
            this.form.saveFormData();
        }
        return true;
    },
    _editorTrGoBack: function(){
        this.table.setStyle("display", "none");
        this.table.inject(this.node, "top");

        //if (this.totalDiv){
        //    this.addAction.inject(this.totalDiv, "before");
        //}else{
        //    this.addAction.inject(this.node);
        //}

//		this.editTr.removeEvents("blur");
//        if (this.totalTr){
//            this.editorTr.inject(this.totalTr, "before");
//        }else{
//            var lastTrs = this.table.getElements("tr");
//            var lastTr = lastTrs[lastTrs.length-1];
//            this.editorTr.inject(lastTr, "after");
//        }
    },

    _actionMousedown: function(node, e){
        var status = node.retrieve("editStatus");
        if (!status){
            node.store("editStatus", new Date());
        }
        this._checkMouseDown(node);
    },
    _checkMouseDown: function(node){
        var status = node.retrieve("editStatus");
        if (status){
            var d = (new Date()).getTime();
            if (d-status.getTime()>1000){
                this._editLine(node);
                node.eliminate("editStatus");
            }else{
                window.setTimeout(function(){
                    this._checkMouseDown(node);
                }.bind(this), 1000)
            }
        }
    },
    _actionMouseup: function(node, e){
        node.eliminate("editStatus");
    },

    actionTouchstart: function(node, e){
        var p = {"x": e.touches[0].pageX, "y": e.touches[0].pageY};
        //node.store("start", p);
        var action = node.retrieve("action");
        if (action){
            node.store("touchStatus", {"p": p, "status": "hide", "isHide": false});
        }else{
            node.store("touchStatus", {"p": p, "status": "show"});
        }
        this._actionMousedown(node, e);
        //e.preventDefault();
    },
    actionTouchmove: function(node, e){
        var touchStatus = node.retrieve("touchStatus");
        var p = touchStatus.p;
        var status = touchStatus.status;
        var x = e.touches[0].pageX;
        var y = e.touches[0].pageY;

        if (Math.abs(p.x-x)>10 || Math.abs(p.y-y)>10){
            this._actionMouseup(node);
        }
        if (status=="show"){
            if ((p.x-x > 20) && Math.abs(p.y-y)<40){
                this.showMoveAction(node, p.x-x);
                e.preventDefault();
            }
        }else{
            if ((x-p.x > 20) && Math.abs(p.y-y)<40){
                touchStatus.isHide = true;
                this.hideMoveAction(node, x- p.x);
                e.preventDefault();
            }
        }

    },
    actionTouchend: function(node, e){
        var touchStatus = node.retrieve("touchStatus");
        var p = touchStatus.p;
        var status = touchStatus.status;
        if (status=="show"){
            var action = node.retrieve("action");
            if (action) this.showEndMoveAction(node);
        }else{
            if (touchStatus.isHide) this.hideEndMoveAction(node);
        }
    },
    actionTouchcancel: function(node, e){
        this._actionMouseup(node);
    },
    showEndMoveAction: function(node){
        var action = node.retrieve("action");
        var tableNode = node.getLast("div");
        new Fx.Tween(action, {"duration": 100}).start("width", "60px");
        new Fx.Tween(tableNode, {"duration": 100}).start("margin-right", "60px");

        var _self = this;
        action.addEvent("click", function(e){
            _self._deleteLine(node, e);
        });
    },
    _deleteLine: function(node, e){
        var saveFlag = false;
        var currentTable = node.getElement("table");
        if (currentTable){

            var datagrid = this;
            var _self = this;
            this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
                _self.fireEvent("deleteLine", [currentTable]);

                var data = currentTable.retrieve("data");

                //var attKeys = [];

                var titleThs = _self.table.getElements("th");
                titleThs.each(function(th, i){
                    var key = th.get("id");
                    var module = _self.editModules[i];
                    if (key && module && (module.json.type=="Attachment" || module.json.type=="AttachmentDg")){
                        saveFlag = true;
                        data[key][module.json.id].each(function(d){
                            _self.form.workAction.deleteAttachment(d.id, _self.form.businessData.work.id);
                        });
                    }
                });

                node.destroy();
                //datagrid._loadZebraStyle();
                datagrid._loadSequence();
                datagrid._loadTotal();
                datagrid.getData();

                if (!_self.gridData.data.length){
                    if (_self.addAction){
                        if( _self.addable )_self.addAction.setStyle("display", "block");
                    }else{
                        if( _self.addable )_self._loadAddAction();
                    }
                }
                this.close();

                _self.fireEvent("afterDeleteLine");

                if(saveFlag){
                    _self.form.saveFormData();
                }

            }, function(){
                //var color = currentTr.retrieve("bgcolor");
                //currentTr.tween("background-color", color);
                this.close();
            }, null, null, this.form.json.confirmStyle);
        }
        this.validationMode();
    },
    hideEndMoveAction: function(node){
        var action = node.retrieve("action");
        var tableNode = node.getLast("div");

        new Fx.Tween(action, {"duration": 100}).start("width", "0px");
        new Fx.Tween(tableNode, {"duration": 100}).start("margin-right", "0px").chain(function(){
            action.destroy();
            node.eliminate("action");
        });

    },
    showMoveAction: function(node, length){
        var action = node.retrieve("action");
        var tableNode = node.getLast("div");
        if (!action){
            var size = node.getSize();
            action = new Element("div", {
                "styles": this.form.css.gridMobileDeleteActionAreaNode
            }).inject(node, "top");
            var button = new Element("div", {
                "styles": this.form.css.gridMobileDeleteActionNode,
                "text": MWF.xApplication.process.Xform.LP["delete"]
            }).inject(action);

            action.setStyle("height",""+size.y+"px");
            action.setStyle("line-height",""+size.y+"px");
            node.store("action", action);
        }
        if (length>60) length = 60;
        action.setStyle("width", ""+length+"px");
        tableNode.setStyle("margin-right", ""+length+"px");
    },
    hideMoveAction: function(node, length){
        var action = node.retrieve("action");
        var tableNode = node.getLast("div");
        var width = 60-length;
        if (width<0) width=0;
        if (action) action.setStyle("width", ""+width+"px");
        tableNode.setStyle("margin-right", ""+width+"px");
    },

    _loadDatagridStyle: function(){
        //var ths = this.node.getElements("th");
        //ths.setStyles(this.form.css.datagridTitle);

        this.loadGridTitleStyle();
        this.loadGridContentStyle();
        //this.loadGridActionStyle();
        this.loadGridEditStyle();

        this._loadTotal();
        this._loadBorderStyle();
        this._loadZebraStyle();
        this._loadSequence();
    },

    loadGridEditStyle: function(){
        if (this.table){
            if (this.json.editStyles){
                var tds = this.table.getElements("td");
                tds.setStyles(this.json.editStyles);
            }
        }
    },
    //loadGridActionStyle: function(){
    //    if (this.editable!=false){
    //        if (this.json.actionStyles){
    //            var trs = this.table.getElements("tr");
    //            trs.each(function(tr, idx){
    //                if (idx != 0) tr.getFirst().setStyles(this.json.actionStyles);
    //            }.bind(this));
    //        }
    //    }
    //},
    loadGridTitleStyle: function(){
        if (this.json.titleStyles){
            var ths = this.node.getElements("th");
            ths.setStyles(this.json.titleStyles);
        }
    },
    loadGridContentStyle: function(){
        if (this.json.contentStyles){
            var tds = this.node.getElements("td");
            tds.setStyles(this.json.contentStyles);
        }
    },

    _loadZebraStyle: function(){
        if (this.json.zebraColor || this.json.backgroundColor){
            var tables = this.node.getElements("table");
            tables.each(function(table){
                this._loadTableZebraStyle(table);
            }.bind(this));
        }
    },
    _loadTableZebraStyle: function(table){
        var trs = table.getElements("tr");
        for (var i=0; i<trs.length; i++){
            if (this.json.backgroundColor) trs[i].setStyle("background-color", this.json.backgroundColor);
            if ((i%2)==0){
                if (this.json.zebraColor) trs[i].setStyle("background-color", this.json.zebraColor);
            }
        }
    },

    createTotalDiv: function(){
        this.totalDiv = new Element("div.totalDiv", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}).inject(this.node);
        var titleNode = new Element("div", {"styles": this.json.itemTitleStyles}).inject(this.totalDiv);
        titleNode.set("text", MWF.xApplication.process.Xform.LP.amount);
        var tableNode = new Element("div", {"styles": this.form.css.gridMobileTableNode }).inject(this.totalDiv);
        this.totalTable = new Element("table", {
            styles : this.json.tableStyles
        }).inject(tableNode);
        this.totalTable.set(this.json.properties);
    },

    _loadTotal: function(){
        var data = {};
        this.totalResaults = {};
        if (this.totalModules.length){
            if (!this.totalDiv){
                this.createTotalDiv();
            }
            var totalResaults = [];
            //this.totalModules.each(function(m. i){
            //    totalResaults.push(0);
            //}.bind(this));
            var tables = this.node.getElements("table");
            //var totalTds = this.totalTr.getElements("td");
            for (var i=1; i<tables.length-1; i++){

                var cells = tables[i].getElements("td");
                this.totalModules.each(function(m, idx){
                    if (!totalResaults[idx]) totalResaults.push(0);
                    var tmpV = new Decimal(totalResaults[idx]);
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
                    totalResaults[idx] = tmpV.toString();
                    data[m.module.json.id] = totalResaults[idx];
                }.bind(this));
            }

            var trs = this.totalTable.getElements("tr");
            this.totalModules.each(function(m, i){
                var tr = trs[i];
                if (!tr){
                    tr = this.totalTable.insertRow(i);
                    var datath = new Element("th").inject(tr);
                    datath.set("text", m.module.node.get("text"));
                    //    datath.setStyle("width", "30%");
                    datath.setStyles(this.json.titleStyles);
                    tr.insertCell(1).setStyles(this.json.amountStyles).set("text", totalResaults[i] || "");
                }else{
                    tr.getElement("td").set("text", totalResaults[i] || "");
                }

                if( m.isShow === false )tr.hide();

                this.totalResaults[m.module.json.id] = totalResaults[i];
            }.bind(this));
            if (this.totalTable){
                var ths = this.totalTable.getElements("th");
                ths.setStyles(this.json.titleStyles);
                //if (this.json.border) this._loadTableBorderStyle(this.totalTable);
                //if (this.json.zebraColor || this.json.backgroundColor) this._loadTableZebraStyle(this.totalTable);
            }
        }
        return data;
    },
    _loadSequence: function(){
        var tables = this.node.getElements("table");
        tables.each(function(table, idx){
            var cells = table.getElements("td");
            cells.each(function(cell){
                var module = cell.retrieve("module");
                if (module){
                    if (module.json.cellType=="sequence"){
                        cell.set("text", idx)
                    }
                }
            }.bind(this));
        }.bind(this));

        var sequenceDivs = this.node.getElements(".sequenceDiv");
        sequenceDivs.each( function(div, index){
            div.set("text", MWF.xApplication.process.Xform.LP.item+(index+1))
        })
    },

    _loadBorderStyle: function(){
        if (this.json.border){
            var tables = this.node.getElements("table");
            tables.each(function(table){
                this._loadTableBorderStyle(table)
            }.bind(this));
        }
    },
    _loadTableBorderStyle: function(table){
        table.setStyles({
            "border-top": this.json.border,
            "border-left": this.json.border
        });
        var ths = table.getElements("th");
        ths.setStyles({
            "border-bottom": this.json.border,
            "border-right": this.json.border
        });
        var tds = table.getElements("td");
        tds.setStyles({
            "border-bottom": this.json.border,
            "border-right": this.json.border,
            "background": "transparent"
        });
    },
    _loadDatagridTitleModules: function(){
        var ths = this.node.getElements("th");
        ths.each(function(th){
            var json = this.form._getDomjson(th);
            th.store("dataGrid", this);
            if (json){
                var module = this.form._loadModule(json, th);
                this.form.modules.push(module);
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
            }
        }.bind(this));
    },
    _afterLoaded: function(){
        if (this.moduleValueAG){
            this.moduleValueAG.then(function(){
                this._loadDatagridStyle();
                if (this.table) this.table.setStyle("display", "none");
            }.bind(this));
        }else{
            this._loadDatagridStyle();
            if (this.table) this.table.setStyle("display", "none");
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
        //     this.moduleValueAG = data;
        //     data.addResolve(function(v){
        //         this._setData(v);
        //     }.bind(this));
        // }else{
        //     if (o2.typeOf(data)=="array") data = {"data": data};
        //     this.__setData(data);
        //     this.moduleValueAG = null;
        // }
    },
    __setData: function(data){
        // if( typeOf( data ) === "object" && typeOf(data.data) === "array"  ){
        // if (data){
        //     this._setBusinessData(data);
        //     this.gridData = data;
        // }else{
        //     this.gridData = this._getValue();
        // }
        this._setBusinessData(data);
        this.gridData = data;

        // if (this.isEdit) this._completeLineEdit();
        if( this.isEdit ){
            this.isEdit = false;
            if (this.currentEditLine) {
                this._editorTrGoBack();
                this.currentEditLine.destroy()
            }else{
                var datagridDataDiv = this.node.getElement(".datagridDataDiv");
                this._editorTrGoBack();
                if(datagridDataDiv)datagridDataDiv.destroy();
            }
            this.currentEditLine = null;
        }

        if (this.gridData){

            var divs = this.node.getElements(".dataDiv");
            for (var i=0; i<divs.length; i++){
                var table = divs[i].getElement("table");
                var tds = table.getElements("td");
                for (var j=0; j<tds.length; j++){
                    var td = tds[j];
                    var moduleTd = td.retrieve("module");
                    if (moduleTd){
                        this.form.modules.erase(moduleTd);
                        moduleTd = null;
                    }
                }
                var ths = table.getElements("th");
                for (var k=0; k<ths.length; k++){
                    var th = ths[k];
                    var moduleTh = th.retrieve("module");
                    if (moduleTh){
                        this.form.modules.erase(moduleTh);
                        moduleTh = null;
                    }
                }
            }
            for( var i=0; i<divs.length; i++ ){
                divs[i].destroy();
            }

            // var tables = this.node.getElements("table");
            // for (var i=1; i<tables.length-1; i++){
            //     var table = tables[i];
            //     var tds = table.getElements("td");
            //     for (var j=0; j<tds.length; j++){
            //         var td = tds[j];
            //         var moduleTd = td.retrieve("module");
            //         if (moduleTd){
            //             this.form.modules.erase(moduleTd);
            //             delete moduleTd;
            //         }
            //     }
            //     var ths = table.getElements("th");
            //     for (var k=0; k<ths.length; k++){
            //         var th = ths[k];
            //         var moduleTh = th.retrieve("module");
            //         if (moduleTh){
            //             this.form.modules.erase(moduleTh);
            //             delete moduleTh;
            //         }
            //     }
            // }
            //
            // while (tables.length>2){
            //     tables[1].destroy();
            //     tables = this.node.getElements("table");
            // }
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

            var tables = this.node.getElements("table");
            for (var i=1; i<((this.totalTable) ? tables.length-1 : tables.length); i++){
                var table = tables[i];
                var d = table.retrieve("data");
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
                "text-align": "left",
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
    }

});

MWF.xApplication.process.Xform.DatagridMobile$Title =  new Class({
    Extends: MWF.APP$Module,
    _afterLoaded: function(){
        this.dataGrid = this.node.retrieve("dataGrid");
        if ((this.json.total == "number") || (this.json.total == "count")){
            this.dataGrid.totalModules.push({
                "module": this,
                "index": this.node.getParent("tr").rowIndex,
                "type": this.json.total,
                "isShow": this.json.isShow
            })
        }
        //	this.form._loadModules(this.node);
    }
});
MWF.xApplication.process.Xform.DatagridMobile$Data =  new Class({
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
            td.empty();
        }else{
            var moduleNodes = this.form._getModuleNodes(this.node);
            moduleNodes.each(function(node){
                var json = this.form._getDomjson(node);
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
            }.bind(this));
        }
    }
});
