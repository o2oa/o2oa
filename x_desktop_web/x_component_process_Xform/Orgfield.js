MWF.xDesktop.requireApp("process.Xform", "Personfield", null, false);
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xApplication.process.Xform.Orgfield = MWF.APPOrgfield =  new Class({
	Extends: MWF.APPPersonfield,
	iconStyle: "orgfieldIcon",
	_loadUserInterface: function(){
		this.field = true;

		this._loadNode();

		if (this.json.compute == "show"){
			this._setValue(this._computeValue());
		}else{
			this._loadValue();
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
			"readonly": true
		});
		if( !this.readonly ) {
			this.node.setStyle("cursor" , "pointer");
			this.node.addEvents({
				"click": this.clickSelect.bind(this)
			});
			this.iconNode = new Element("div", {  //this.form.css[this.iconStyle],
				"styles": {
					"background": "url("+"/x_component_process_Xform/$Form/default/icon/selectorg.png) center center no-repeat",
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
		//debugger;
		//var v = this.node.getElement("input").get("value");
		//return (v) ? v.split(/,\s*/g) : "";
		var data = this._getBusinessData();
		var values = [];
		for( var key in data ){
			data[key].each( function(d){
				values.push( d.name );
			});
		}
		return values.join(",");
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

	_computeValue: function(value){
		var values = {};
		if (this.json.identityValue) {
			values.identityValue = [];
			this.json.identityValue.each( function( d ){
				values.identityValue.push( { name : d } )
			}.bind(this) )
			//this.json.identityValue.each(function(v){ if (v) values.push(v)});
		}
		if (this.json.personValue) {
			values.personValue = [];
			this.json.personValue.each( function( d ){
				values.personValue.push( { name : d } )
			}.bind(this) );
			//this.json.identityValue.each(function(v){ if (v) values.push(v)});
		}
		if (this.json.departmentValue) {
			values.departmentValue = [];
			this.json.departmentValue.each( function( d ){
				values.departmentValue.push( { name : d } )
			}.bind(this) );
			//this.json.departmentValue.each(function(v){ if (v) values.push(v)});
		}
		if (this.json.companyValue) {
			values.companyValue = [];
			this.json.companyValue.each( function( d ){
				values.companyValue.push( { name : d } )
			}.bind(this) );
			//this.json.companyValue.each(function(v){ if (v) values.push(v)});
		}
		if (this.json.groupValue) {
			values.groupValue = [];
			this.json.groupValue.each( function( d ){
				values.groupValue.push( { name : d } )
			}.bind(this) );
			//this.json.groupValue.each(function(v){ if (v) values.push(v)});
		}
		//if (this.json.defaultValue && this.json.defaultValue.code){
		//	var fd = this.form.Macro.exec(this.json.defaultValue.code, this);
		//	if (typeOf(fd)!="array") fd = (fd) ? [fd.toString()] : [];
		//	fd.each(function(fdd){values.push(fdd);});
		//}
		//if (this.json.count>0){
		//	return values.slice(0, this.json.count);
		//}
		return values;
		//return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
	},
	_setValue: function(value){
		this._setBusinessData(value);
		var input = this.node.getFirst();
		if (!input){
			input = this.node;
		}
		//if (this.readonly)var input = this.node;
		input.empty();
		//if (this.node.getFirst()) this.node.getFirst().set("value", value);
		var explorer = {
			actions : this.form.documentAction,
			app : this.form.app,
			field : this
		};
		for (var key in value) {
			v = value[key];
			if( key == "identityValue" ){
				v.each(function( d ){
					if (d.name) new MWF.widget.Identity( Object.merge( d, {"type" : "identity" }), input, explorer, !this.readonly, this.removeItem, {"style": "cmsdoc"});
				}.bind(this));
			}else if( key == "personValue" ){
				v.each(function( d ){
					if (d.name) new MWF.widget.Department(Object.merge( d, {"type" : "person" }), input, explorer, !this.readonly, this.removeItem, {"style": "cmsdoc"});
				}.bind(this));
			}else if( key == "departmentValue" ){
				v.each(function(d){
					if (d.name) new MWF.widget.Department(Object.merge( d, {"type" : "department" }), input, explorer, !this.readonly, this.removeItem, {"style": "cmsdoc"});
				}.bind(this));
			}else if( key == "companyValue" ){
				v.each(function(d){
					if (d.name) new MWF.widget.Company(Object.merge( d, {"type" : "company" }), input, explorer, !this.readonly, this.removeItem, {"style": "cmsdoc"});
				}.bind(this));
			}else if( key == "groupValue" ){
				v.each(function(d){
					if (d.name) new MWF.widget.Department(Object.merge( d, {"type" : "group" }), input, explorer, !this.readonly, this.removeItem, {"style": "cmsdoc"});
				}.bind(this));
			}
		}
	},
	removeItem : function( ev ){
		//this 是 MWF.widget.Identity 之类的对象
		var _self = this.explorer.field; //这个才是Orgfield
		var type = this.data.type;
		var name = this.data.name;
		var data = _self._getBusinessData();
		var arr = data[ type+"Value"];
		var index;
		arr.each( function ( d , i){
			if( d.name == name ){
				index = i
			}
		});
		arr.splice( index, 1 );
		_self._setBusinessData( data );
		this.node.destroy();
		ev.stopPropagation();
	},
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
		var companys = this.getCompanys(); //范围
		var departments = this.getDepartments(); //范围

		if (this.json.range=="depart"){
			if (!departments.length){
				this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
				return false;
			}
		}
		if (this.json.range=="company"){
			if (!companys.length){
				this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
				return false;
			}
		}

		var nameValue = this._getBusinessData();
		var names = {};
		var namesArr = [];
		if( typeOf( this.json.selectType ) == "array" && this.json.selectType.length > 1 ){
			for( var key in nameValue ){
				var name = [];
				nameValue[key].each( function(value){
					name.push( value.name );
				}.bind(this));
				names[ key.replace("Value","") ] = name;
			}
		}else{
			for( var key in nameValue ){
				nameValue[key].each( function(value){
					namesArr.push( value.name );
				}.bind(this));
			}
		}

		var options = {
			"types": typeOf( this.json.selectType ) == "array" ? this.json.selectType : [this.json.selectType],
			"names" : namesArr,
			"multipleNames": names, //已选值
			"count": count,
			"departments": departments, //范围
			"companys": companys, //范围
			"onComplete": function(items, itemsObject){
				var values = {};
				if( itemsObject ){
					for( var key in itemsObject ){
						var item = itemsObject[key];
						var value = [];
						item.each( function(it){
							value.push({
								id : it.data.id,
								name: it.data.name
							})
						});
						values[ key + "Value" ] = value;
					}
				}else{
					var key =  typeOf( this.json.selectType ) == "array" ? this.json.selectType[0] : this.json.selectType;
					key = key + "Value";
					var values = {};
					values[key] = [];
					items.each( function(it){
						values[key].push({
							id : it.data.id,
							name: it.data.name
						})
					});
				}
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

		//var selector = new MWF.OrgSelector(this.form.node.getParent(), options);
		var selector = new MWF.OrgSelector(this.form.app.content, options);


		//value = MWF.Macro.exec(this.json.defaultValue.code, this.form);
	}
}); 