MWF.xDesktop.requireApp("cms.Xform", "Personfield", null, false);
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.cms.Xform.Readerfield = MWF.CMSReaderfield =  new Class({
	Extends: MWF.CMSPersonfield,
	iconStyle: "readerfieldIcon",
    loadDescription: function(){
        if (this.readonly || this.json.isReadonly)return;
        var v = this._getBusinessData();
        if (!v || !v.length){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-3;
                if (COMMON.Browser.safari) w = w-20;
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "width": ""+w+"px",
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            this.descriptionNode.addEvents({
                "mousedown": function(ev){
                    this.descriptionNode.setStyle("display", "none");
                    this.clickSelect(ev);
                }.bind(this)
            });
        }
    },
	_loadUserInterface: function(){
		this.field = true;

		this._loadNode();

		if (this.json.compute === "show"){
			this._setValue(this._computeValue());
		}else{
			this._loadValue();
		}

	},
	_loadNode: function(){
		if (this.readonly || this.json.isReadonly){
			this._loadNodeRead();
		}else{
            this._getOrgOptions();
            if (this.json.isInput){
                this._loadNodeInputEdit();
            }else{
                this._loadNodeEdit();
            }
		}
	},

    _getOrgOptions: function(){
        var selectType = typeOf( this.json.selectType ) == "array" ? this.json.selectType : [this.json.selectType];
        if( selectType.contains("unit") || selectType.contains("identity")){
            this.selectUnits = this.getSelectRange();
            if (this.json.range!=="all"){
                if (!this.selectUnits.length){
                    this.form.notice(MWF.xApplication.cms.Xform.LP.noSelectRange, "error", this.node);
                    return false;
                }
            }
        }else{
            this.selectUnits = [];
        }
    },
	_loadNodeRead: function(){
		this.node.empty();
		this.node.setStyle("overflow" , "hidden");
        var node = new Element("div").inject(this.node);
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
	},
    _searchConfirmPerson: function(item){
        var inforNode = item.inforNode || new Element("div");
        if (item.data){
            var text = "";
            var flag = item.data.distinguishedName.substr(item.data.distinguishedName.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    text = item.data.name+"("+item.data.unitName+")";
                    break;
                case "p":
                    text = item.data.name+(item.data.employee ? "("+item.data.employee+")" : "");
                    break;
                case "u":
                    text = item.data.levelName;
                    break;
                case "g":
                    text = item.data.name;
                    break;
                default:
                    text = item.data.name;
            }
            inforNode.set({
                "styles": {"font-size": "14px", "color": ""},
                "text": text
            });
        }else{
            inforNode.set({
                "styles": {"font-size": "14px", "color": "#bd0000"},
                "text": MWF.xApplication.process.Xform.LP.noOrgObject
            });
        }
        if (!item.inforNode){
            new mBox.Tooltip({
                content: inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: item.node,
                transition: 'flyin'
            });
            item.inforNode = inforNode;
        }

    },
    _searchOptions: function(value, callback){
        var selectType = typeOf( this.json.selectType ) == "array" ? this.json.selectType : [this.json.selectType];
        var options = {
            "type" : "",
            "types": selectType,
            "units": this.selectUnits, //范围
            "unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType
        };
        if (!this.comboxFilter) this.comboxFilter = new MWF.O2SelectorFilter(value, options);
        this.comboxFilter.filter(value, function(data){
            data.map(function(d){
                var value = Object.clone(d);
                d.value = value;
                var flag = d.distinguishedName.substr(d.distinguishedName.length-1, 1);
                switch (flag.toLowerCase()){
                    case "i":
                        d.text = d.name+"("+d.unitName+")";
                        break;
                    case "p":
                        d.text = d.name+(d.employee ? "("+d.employee+")" : "");
                        break;
                    case "u":
                        d.text = d.name;
                        break;
                    case "g":
                        d.text = d.name;
                        break;
                    default:
                        d.text = d.name;
                }
            });
            if (callback) callback(data);
        });
    },
    _loadNodeInputEdit: function(){
        var input=null;
        MWF.require("MWF.widget.Combox", function(){
            this.combox = input = new MWF.widget.Combox({
                "count": this.json.count || 0,
                "splitShow": this.json.splitShow || ", ",
                "onCommitInput": function(item){
                    this._searchConfirmPerson(item);
                    //this.fireEvent("change");
                }.bind(this),
                "onChange": function(){
                    this._setBusinessData(this.getInputData());
                    this.fireEvent("change");
                }.bind(this),
                "optionsMethod": this._searchOptions.bind(this)

            });
        }.bind(this), false);
        input.setStyles({
            "background": "transparent",
            "border": "0px"
        });
        input.set(this.json.properties);

        var node = new Element("div", {"styles": {
            "overflow": "hidden",
            //"position": "relative",
            "margin-right": "20px"
        }}).inject(this.node, "after");
        input.inject(node);
        //this.combox = input;

        this.node.destroy();
        this.node = node;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        if (this.json.showIcon!='no') this.iconNode = new Element("div", {
            "styles": this.form.css[this.iconStyle],
            "events": {
                "click": function (ev) {
                    this.clickSelect(ev);
                }.bind(this)
            }
        }).inject(this.node, "before");

        this.combox.addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));
    },
	_loadNodeEdit : function(){
		var input = this.input = new Element("div", {
			"styles": {
				"background": "transparent",
				"border": "0px",
                "min-height": "20px"
			}
		});
		input.set(this.json.properties);

		var node = new Element("div", {"styles": {
			"overflow": "hidden",
			"position": "relative",
			"min-height" : "20px",
			"margin-right": "20px"
		}}).inject(this.node, "after");
		input.inject(node);

		this.node.destroy();
		this.node = node;
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"readonly": true,
			"title" : MWF.xApplication.cms.Xform.LP.readerFieldNotice
		});
		if( !this.readonly ) {
			this.node.setStyle("cursor" , "pointer");
			this.node.addEvents({
				"click": function (ev) {
                    this.clickSelect(ev);
                }.bind(this)
                //this.clickSelect.bind(this)
			});
			if (this.json.showIcon!='no')this.iconNode = new Element("div", {  //this.form.css[this.iconStyle],
				"styles": {
					"background": "url("+"../x_component_cms_Xform/$Form/default/icon/selectreader.png) center center no-repeat",
					"width": "18px",
					"height": "18px",
					"float": "right"
				}
			}).inject(this.node, "before");
			if (this.iconNode){
                this.iconNode.setStyle("cursor" , "pointer");
                this.iconNode.addEvents({
                    "click": function (ev) {
                        this.clickSelect(ev);
                    }.bind(this)
                    //this.clickSelect.bind(this)
                });
			}
		}

	},
	getData: function(when){
		if (this.json.compute == "save") this._setValue(this._computeValue());
		return this._getBusinessData();
	},
	getInputData: function(){
        if (this.json.isInput){
            if (this.combox) return this.combox.getData();
            return this._getBusinessData();
        }else{
            return this._getBusinessData();
        }
	},
	addData: function(value){
        if (!value) return false;
        value.each(function(v){
            var vtype = typeOf(v);
            if (vtype==="string" ){
                var data;
                if( this.json.valueType === "string" ){
                    this.combox.addNewValue(v.split("@")[0], v);
                }else{
                    this.getOrgAction()[this.getValueMethod(v)](function(json){ data = json.data }.bind(this), null, v, false);
                    if (data) this.combox.addNewValue(this.getDataText(data), data);
                }
            }
            if (vtype==="object"){
                this.combox.addNewValue(this.getDataText(v), v);
            }
        }.bind(this));
    },
	setData: function(data) {
		this._setValue(data);
	},
	_computeValue: function(){
		var values = [];
		if (this.json.identityValue) {
			this.json.identityValue.each(function(v){ if (v) values.push(v)});
		}
		if (this.json.unitValue) {
			this.json.unitValue.each(function(v){ if (v) values.push(v)});
		}
		if (this.json.defaultValue && this.json.defaultValue.code){
            var fd = this.form.Macro.exec(this.json.defaultValue.code, this);
            if (typeOf(fd)!=="array") fd = (fd) ? [fd] : [];
            fd.each(function(fdd){
                if (fdd){
                    if (typeOf(fdd)==="string" && this.json.valueType !== "string"){
                        var data;
                        this.getOrgAction()[this.getValueMethod(fdd)](function(json){ data = json.data }.bind(this), null, fdd, false);
                        values.push(data);
                    }else{
                        values.push(fdd);
                    }
                }
            }.bind(this));
		}
		if (this.json.count>0){
			return values.slice(0, this.json.count);
		}
		return values;
		//return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
	},

	_getBusinessData: function(){
		if (this.json.section=="yes"){
			var data = this._getBusinessSectionData();
		}else {
			var data = this.form.businessData.data[this.json.id] || "";
		}
		if (typeOf( data ) != "array"){
            if (data) return [data];
            return [];
        }else{
		    return data;
        }
	},
	creteShowNode: function(data, islast){
        var nodeText = (data.text) ?  data.text : data;
        if (!islast) nodeText = nodeText + (this.json.splitStr || ", ");
        var node = new Element("div", {
            "styles": {
                "float": "left",
                "margin-right": "5px"
            },
            "text": nodeText
        });
        var text = "";
        if (data.value){
            var flag = data.value.distinguishedName.substr(data.value.distinguishedName.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    text = data.value.name+"("+data.value.unitName+")";
                    break;
                case "p":
                    text = data.value.name+ ( +data.value.employee ? "("+data.value.employee+")" : "");
                    break;
                case "u":
                    text = data.value.levelName;
                    break;
                case "g":
                    text = data.value.name;
                    break;
                default:
                    text = data.value.name;
            }
            var inforNode = new Element("div").set({
                "styles": {"font-size": "14px", "color": ""},
                "text": text
            });

            new mBox.Tooltip({
                content: inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: node,
                transition: 'flyin'
            });
        }
        return node;
    },
	_setValue: function(value){
        if (value.length==1 && !(value[0])) value=[];
        var values = [];
        var comboxValues = [];
        var type = typeOf(value);
        if (type==="array"){
            value.each(function(v){
                var data=null;
                var vtype = typeOf(v);
                if (vtype==="string" ){
                    if( this.json.valueType === "string" ){
                        data = v;
                        values.push(data);
                        comboxValues.push({"text": data.split("@")[0],"value": data});
                    }else{
                        var error = (this.json.isInput) ? function(){ comboxValues.push(v); } : null;
                        this.getOrgAction()[this.getValueMethod(v)](function(json){ data = json.data }.bind(this), error, v, false);
                        values.push(data);
                        comboxValues.push({"text": this.getDataText(data),"value": data});
                    }
                }
                if (vtype==="object"){
                    data = v;
                    values.push(data);
                    comboxValues.push({"text": this.getDataText(data),"value": data});
                }
                //if (data){
                //    values.push(data);
                //    comboxValues.push({"text": this.getDataText(data),"value": data});
                //}
            }.bind(this));
        }
        if (type==="string"){
            if( this.json.valueType === "string" ){
                values.push(value);
                comboxValues.push({"text": value.split("@")[0],"value": value});
            }else{
                var vData;
                var error = (this.json.isInput) ? function(){ comboxValues.push(value); } : null;
                this.getOrgAction()[this.getValueMethod(value)](function(json){ vData = json.data }.bind(this), error, value, false);
                if (vData){
                    values.push(vData);
                    comboxValues.push({"text": this.getDataText(vData),"value": vData});
                }
            }
        }
        if (type==="object"){
            values.push(value);
            comboxValues.push({"text": this.getDataText(value),"value": value});
        }

		this._setBusinessData(value);

        if (this.json.isInput) {
            if (this.combox) {
                this.combox.clear();
                this.combox.addNewValues(comboxValues);
            } else {
                var node = this.node.getFirst();
                if (node) {
                    node.empty();
                    comboxValues.each(function (v, i) {
                        this.creteShowNode(v, (i === comboxValues.length - 1)).inject(node);
                    }.bind(this));
                }
            }
        }else if( this.json.valueType==="string" ){
            var input = this.node.getFirst();
            if (!input) {
                input = this.node;
            }
            input.empty();
            var textList = [];
            values.each( function( v ){
                if( typeOf(v) === "string" )textList.push( v.split("@")[0] );
                if( typeOf(v) === "object" )textList.push(v.distinguishedName.split("@")[0] );
            });
            input.set("text", textList.join(", "))
        }else {
            var input = this.node.getFirst();
            if (!input) {
                input = this.node;
            }
            input.empty();

            this.loadOrgWidget(values, input);
        }
	},
	loadOrgWidget: function(value, node){
        var disableInfor = layout.mobile ? true : false;
        if( this.json.showCard === "no" )disableInfor = true;
		var options = {"style": "xform", "canRemove":!this.readonly , "onRemove" : this.removeItem, "disableInfor" : disableInfor};
		value.each(function(data){
			if( data.distinguishedName ){
				var flag = data.distinguishedName.substr(data.distinguishedName.length-1, 1);
				switch (flag.toLowerCase()){
					case "i":
						var widget = new MWF.widget.O2Identity(data, node, options );
						break;
					case "p":
						var widget = new MWF.widget.O2Person(data, node, options);
						break;
					case "u":
						var widget = new MWF.widget.O2Unit(data, node, options);
						break;
					case "g":
						var widget = new MWF.widget.O2Group(data, node, options);
						break;
					default:
						var widget = new MWF.widget.O2Other(data, node, options);
				}
				widget.field = this;
				if( layout.mobile ){
					widget.node.setStyles({
						"float" : "none"
					})
				}
			}
		}.bind(this));
	},
	removeItem : function( widget, ev ){
		//this 是 MWF.widget.O2Identity 之类的对象
		var _self = this.field; //这个才是Readerfield
		var dn = this.data.distinguishedName;
		var data = _self._getBusinessData();
		var index;
		data.each( function ( d , i){
			if( d.distinguishedName == dn ){
				index = i
			}
		});
		data.splice( index, 1 );
		_self._setBusinessData( data );
		this.node.destroy();
		ev.stopPropagation();
	},
    getValue: function(){
        var value = this._getBusinessData();
        if( typeOf( value ) === "array" ){
            if( value.length === 0 )value = this._computeValue();
        }else if (!value){
            value = this._computeValue();
        }
        return value || "";
    },
	_loadValue: function(){
		this._setValue(this.getValue());
	},
	clickSelect: function(ev){
		this.validationMode();
		var count = (this.json.count) ? this.json.count : 0;

		var selectType = typeOf( this.json.selectType ) == "array" ? this.json.selectType : [this.json.selectType];
		//if( selectType.contains("unit") || selectType.contains("identity")){
		//	var selectUnits = this.getSelectRange();
		//	if (this.json.range!=="all"){
		//		if (!selectUnits.length){
		//			this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
		//			return false;
		//		}
		//	}
		//}else{
		//	var selectUnits = [];
		//}
		var selectUnits = this.selectUnits;
		if( !selectType[0] ){
			this.form.notice(MWF.xApplication.process.Xform.LP.noSelectType, "error", this.node);
			return false;
		}

        var exclude = [];
        if( this.json.exclude ){
            var v = this.form.Macro.exec(this.json.exclude.code, this);
            exclude = typeOf(v)==="array" ? v : [v];
        }

        var simple = this.json.storeRange === "simple";

		var options = {
			"type" : "",
			"types": selectType,
			"values" : (this.json.isInput) ? [] : this._getBusinessData(),
			"count": count,
			"units": selectUnits, //范围
			"unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType,
            "exclude" : exclude,
            "expandSubEnable" : (this.json.expandSubEnable=="no") ? false : true,
			//"expand" : false,
			"onComplete": function(items, itemsObject){
				var values = [];
				items.each( function(it){
					values.push(MWF.org.parseOrgData(it.data, true, simple));
				});
				if (this.json.isInput){
                    this.addData(values);
                }else{
                    this.setData(values);
                }


				//this.setData( values );
				this.validation();
                this.fireEvent("select");
			}.bind(this),
			"onCancel": function(){
				this.validation();
			}.bind(this),
			"onLoad": function(){
				if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
			}.bind(this),
			"onClose": function(){
				//var v = this.node.getFirst().get("value");
				var v = this.getInputData();
				if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");

			}.bind(this)
		};

        if( this.selector && this.selector.loading ) {
        }else if( this.selector && this.selector.selector && this.selector.selector.active ){
        }else {
            this.selector = new MWF.O2Selector(this.form.app.content, options);
        }

	},
    _loadStyles: function(){
        if (this.readonly || this.json.isReadonly){
            if (this.json.styles) this.node.setStyles(this.json.styles);
        }else{
            if (this.json.styles) this.node.setStyles(this.json.styles);
            if (this.json.inputStyles) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
            if (this.iconNode && this.iconNode.offsetParent !== null){
                var size = this.node.getSize();
                this.iconNode.setStyle("height", ""+size.y+"px");
            }
        }
    }
});