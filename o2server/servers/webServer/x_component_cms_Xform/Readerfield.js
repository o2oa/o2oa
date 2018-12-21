MWF.xDesktop.requireApp("cms.Xform", "Personfield", null, false);
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.cms.Xform.Readerfield = MWF.CMSReaderfield =  new Class({
	Extends: MWF.CMSPersonfield,
	iconStyle: "readerfieldIcon",
	_loadUserInterface: function(){
		this.field = true;

		this._loadNode();

		if (this.json.compute == "show"){
			this._setValue(this._computeValue());
		}else{
			this._loadValue();
		}

	},
	_loadNode: function(){
		if (this.readonly || this.json.isReadonly){
			this._loadNodeRead();
		}else{
			this._loadNodeEdit();
		}
	},
	_loadNodeRead: function(){
		this.node.empty();
		this.node.setStyle("overflow" , "hidden");
	},
	_loadNodeEdit : function(){
		var input = this.input = new Element("div", {
			"styles": {
				"background": "transparent",
				"width": "100%",
				"border": "0px"
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
				"click": this.clickSelect.bind(this)
			});
			this.iconNode = new Element("div", {  //this.form.css[this.iconStyle],
				"styles": {
					"background": "url("+"/x_component_cms_Xform/$Form/default/icon/selectreader.png) center center no-repeat",
					"width": "18px",
					"height": "18px",
					"float": "right"
				}
			}).inject(this.node, "before");
			this.iconNode.setStyle("cursor" , "pointer");
			this.iconNode.addEvents({
				"click": this.clickSelect.bind(this)
			});
		}

		//this.node.getFirst().addEvent("change", function(){
		//	this.validationMode();
		//	if (this.validation()) this._setBusinessData(this.getInputData("change"));
		//}.bind(this));
	},
	getData: function(when){
		if (this.json.compute == "save") this._setValue(this._computeValue());
		return this._getBusinessData();
	},
	getInputData: function(){
		return this._getBusinessData();
		//var data = this._getBusinessData();
		//var values = [];
		//for( var key in data ){
		//	data[key].each( function(d){
		//		values.push( d.name );
		//	});
		//}
		//return values.join(",");
	},
	setData: function(data) {
		this._setValue(data);
		//this._setBusinessData(data);
		//if (this.node.getFirst()){
		//	this.node.getFirst().set("value", data);
		//}else{
		//	this.node.set("text", data);
		//}
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
			if (typeOf(fd)!=="array") fd = (fd) ? [fd.toString()] : [];
			fd.each(function(fdd){values.push(fdd);});
		}
		if (this.json.count>0){
			return values.slice(0, this.json.count);
		}
		return values;
		//return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
	},
	//_computeValue: function(value){
		//var values = {};
		//if (this.json.identityValue) {
		//	values.identityValue = [];
		//	this.json.identityValue.each( function( d ){
		//		values.identityValue.push( { name : d } )
		//	}.bind(this) );
		//}
		//if (this.json.personValue) {
		//	values.personValue = [];
		//	this.json.personValue.each( function( d ){
		//		values.personValue.push( { name : d } )
		//	}.bind(this) );
		//}
		//if (this.json.unitValue) {
		//	values.unitValue = [];
		//	this.json.unitValue.each( function( d ){
		//		values.unitValue.push( { name : d } )
		//	}.bind(this) );
		//}
		//if (this.json.groupValue) {
		//	values.groupValue = [];
		//	this.json.groupValue.each( function( d ){
		//		values.groupValue.push( { name : d } )
		//	}.bind(this) );
		//}
		//return values;
	//},

	_getBusinessData: function(){
		if (this.json.section=="yes"){
			var data = this._getBusinessSectionData();
		}else {
			var data = this.form.businessData.data[this.json.id] || [];
		}
		return typeOf( data ) != "array" ? [data] : data;
	},
	_setValue: function(value){
		this._setBusinessData(value);
		var input = this.node.getFirst();
		if (!input){
			input = this.node;
		}
		input.empty();

		this.loadOrgWidget(value, input);

		//for (var key in value) {
		//	v = value[key];
		//	if( key == "identityValue" ){
		//		v.each(function( d ){
		//			if (d.name) new MWF.widget.O2Identity( Object.merge( d, {"type" : "identity" }), input, {
		//				"style": "cmsdoc", "canRemove":!this.readonly , "onRemove" : this.removeItem
		//			});
		//		}.bind(this));
		//	}else if( key == "personValue" ){
		//		v.each(function( d ){
		//			if (d.name) new MWF.widget.O2Person(Object.merge( d, {"type" : "person" }), input, {
		//				"style": "cmsdoc", "canRemove":!this.readonly , "onRemove" : this.removeItem
		//			});
		//		}.bind(this));
		//	}else if( key == "unitValue" ){
		//		v.each(function(d){
		//			if (d.name) new MWF.widget.O2Unit(Object.merge( d, {"type" : "department" }), input, {
		//				"style": "cmsdoc", "canRemove":!this.readonly , "onRemove" : this.removeItem
		//			});
		//		}.bind(this));
		//	}else if( key == "groupValue" ){
		//		v.each(function(d){
		//			if (d.name) new MWF.widget.O2Group(Object.merge( d, {"type" : "group" }), input,{
		//				"style": "cmsdoc", "canRemove":!this.readonly , "onRemove" : this.removeItem
		//			});
		//		}.bind(this));
		//	}else{
		//		v.each(function(d){
		//			if (d.name) new MWF.widget.O2Other(Object.merge( d, {"type" : "group" }), input,{
		//				"style": "cmsdoc", "canRemove":!this.readonly , "onRemove" : this.removeItem
		//			});
		//		}.bind(this));
		//	}
		//}
	},
	loadOrgWidget: function(value, node){
		var options = {"style": "xform", "canRemove":!this.readonly , "onRemove" : this.removeItem};
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
	//removeItem : function( ev ){
	//	//this 是 MWF.widget.O2Identity 之类的对象
	//	var _self = this.explorer.field; //这个才是Readerfield
	//	var type = this.data.type;
	//	var name = this.data.name;
	//	var data = _self._getBusinessData();
	//	var arr = data[ type+"Value"];
	//	var index;
	//	arr.each( function ( d , i){
	//		if( d.name == name ){
	//			index = i
	//		}
	//	});
	//	arr.splice( index, 1 );
	//	_self._setBusinessData( data );
	//	this.node.destroy();
	//	ev.stopPropagation();
	//},
	_loadValue: function(){
		this._setValue(this.getValue());
	},
	//_loadStyles: function(){
	//	if (this.json.styles) this.node.setStyles(this.json.styles);
	//	if( this.readonly ){
	//		var parent = this.node.parentNode;
	//		if( parent.tagName.toLowerCase() == "td" ){
	//			var border = parent.getStyle("borderBottomWidth");
	//			if( border.toInt() > 0 ){
	//				this.node.setStyle("border","0px");
	//			}
	//		}
	//	}
	//	if (this.json.inputStyles) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
	//	if (this.iconNode){
	//		var size = this.node.getSize();
	//		this.iconNode.setStyle("height", ""+size.y+"px");
	//	}
	//},
	clickSelect: function(){
		this.validationMode();
		//var names = (nameValue) ? this.getInputData().split(MWF.splitStr) : [];
		//var names = nameValue;
		var count = (this.json.count) ? this.json.count : 0;
		//var companys = this.getCompanys(); //范围
		//var departments = this.getDepartments(); //范围

		//if (this.json.range=="depart"){
		//	if (!departments.length){
		//		this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
		//		return false;
		//	}
		//}
		//if (this.json.range=="company"){
		//	if (!companys.length){
		//		this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
		//		return false;
		//	}
		//}
		var selectType = typeOf( this.json.selectType ) == "array" ? this.json.selectType : [this.json.selectType];
		if( selectType.contains("unit") || selectType.contains("identity")){
			var selectUnits = this.getSelectRange();
			if (this.json.range!=="all"){
				if (!selectUnits.length){
					this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
					return false;
				}
			}
		}else{
			var selectUnits = [];
		}
		if( !selectType[0] ){
			this.form.notice(MWF.xApplication.process.Xform.LP.noSelectType, "error", this.node);
			return false;
		}

		//var nameValue = this._getBusinessData();
		//var names = {};
		//var namesArr = [];
		//if( selectType.length > 1 ){
		//	for( var key in nameValue ){
		//		var name = [];
		//		nameValue[key].each( function(value){
		//			name.push( value.name );
		//		}.bind(this));
		//		names[ key.replace("Value","") ] = name;
		//	}
		//}else{
		//	for( var key in nameValue ){
		//		nameValue[key].each( function(value){
		//			namesArr.push( value.name );
		//		}.bind(this));
		//	}
		//}

		var options = {
			"type" : "",
			"types": selectType,
			"values" : this._getBusinessData(),
			"count": count,
			"units": selectUnits, //范围
			"expand" : false,
			"onComplete": function(items, itemsObject){
				var values = [];
				items.each( function(it){
					values.push(MWF.org.parseOrgData(it.data));
				});
				//if( itemsObject ){
				//	for( var key in itemsObject ){
				//		var item = itemsObject[key];
				//		var value = [];
				//		item.each( function(it){
				//			value.push(MWF.org.parseOrgData(it.data));
				//		});
				//		values[ key + "Value" ] = value;
				//	}
				//}else{
				//	var key =  selectType[0];
				//	key = key + "Value";
				//	var values = {};
				//	values[key] = [];
				//	items.each( function(it){
				//		values[key].push(MWF.org.parseOrgData(it.data));
				//	});
				//}
				this.setData( values );

				//items.each(function(item){
				//	values.push(item.data.name);
				//}.bind(this));
				//this.setData(values.join(", "));

				//this._setBusinessData(this.getInputData());
				this.validation()
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

		//if (layout.mobile) options.style = "mobile";

		//var selector = new MWF.O2Selector(this.form.node.getParent(), options);
		var selector = new MWF.O2Selector(this.form.app.content, options);


		//value = MWF.Macro.exec(this.json.defaultValue.code, this.form);
	}
});