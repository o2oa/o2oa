MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Elautocomplete = MWF.FCElautocomplete = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elautocomplete/elautocomplete.html"
	},

	_initModuleType: function(){
		this.className = "Elautocomplete";
		this.moduleType = "element";
		this.moduleName = "elautocomplete";
	},
	_createElementHtml: function(){
		//var html = "<el-input placeholder=\"请输入内容\"></el-input>";
		var html = "<el-autocomplete";
		if (this.json.description) html += " placeholder=\""+this.json.description+"\"";

		if (this.json.placement) html += " placement=\""+this.json.placement+"\"";
		if (this.json.popperClass) html += " popper-class=\""+this.json.popperClass+"\"";
		if (this.json.triggerOnFocus===false ) html += " :trigger-on-focus='false'";
		if (this.json.prefixIcon) html += " prefix-icon=\""+this.json.prefixIcon+"\"";
		if (this.json.suffixIcon) html += " suffix-icon=\""+this.json.suffixIcon+"\"";
		html += " :fetch-suggestions=\"$fetchSuggestions\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		if (this.json.elStyles){
			var style = "";
			Object.keys(this.json.elStyles).forEach(function(k){
				if (this.json.elStyles[k]) style += k+":"+this.json.elStyles[k]+";";
			}, this);
			html += " style=\""+style+"\"";
		}
		html += " value=\""+this.json.id+"\">";
		if (this.json.vueSlot) html += this.json.vueSlot;
		html += "</el-autocomplete>";
		return html;
	},
	_createVueExtend: function(callback){
		var _self = this;
		return {
			data: this._createVueData(),
			mounted: function(){
				_self._afterMounted(this.$el, callback);
			},
			methods: {
				$fetchSuggestions: function(qs, cb){
					if (this.json.itemType!=='script'){
						if (this.json.itemValues){
							cb(this.json.itemValues.map(function(v){
								return {"value": v};
							}));
							return;
						}
					}
					cb([]);
				}.bind(this)
			}
		};
	},
	_createCopyNode: function(){
		this.copyNode = new Element("div", {
			"styles": this.css.moduleNodeShow
		});
		this.copyNode.addEvent("selectstart", function(){
			return false;
		});
	},
	_getCopyNode: function(){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyle("display", "inline-block");
		return this.copyNode;
	},
	setPropertyName: function(){
		if (this.json.name){
			var button = this.node.getElement("button");
			if (!button) button = this.node.getFirst("input");
			if (button) button.set("text", this.json.name);
		}
	},
	setPropertyId: function(){
		if (!this.json.name){
			var button = this.node.getElement("button");
			if (!button) button = this.node.getFirst("input");
			if (button) button.set("text", this.json.id);
		}
	}
});
