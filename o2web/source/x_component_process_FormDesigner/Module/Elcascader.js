MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elinput", null, false);
MWF.xApplication.process.FormDesigner.Module.Elcascader = MWF.FCElcascader = new Class({
	Extends: MWF.FCElinput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elcascader/elcascader.html"
	},

	_initModuleType: function(){
		this.className = "Elcascader";
		this.moduleType = "element";
		this.moduleName = "elcascader";
	},
	_createElementHtml: function(){
		var html = "<el-cascader";
		html += " :placeholder=\"id\"";
		html += " :size=\"size\"";
		html += " :clearable=\"clearable\"";
		html += " :popper-class=\"popperClass\"";
		html += " :show-all-levels=\"multiple\"";
		html += " :collapse-tags=\"collapseTags\"";
		html += " :separator=\"separator\"";

		html += " :style=\"elStyles\"";

		html += " :value=\"id\">";

		if (this.json.vueSlot) html += this.json.vueSlot;

		html += "</el-cascader>";
		return html;
	},
	// _createVueData: function(){
	// 	//var data = this.json;
	// 	return function(){
	// 		this.json[this.json.id] = this.json.id;
	// 		return Object.assign(this.json, this.tmpVueData||{});
	// 	}.bind(this)
	// },
	_setEditStyle_custom: function(name){
		// switch (name){
		// 	case "name": this.setPropertyName(); break;
		// 	case "id": this.setPropertyId(); break;
		// 	default: if (this.isPropertyLoaded) if (this.vm) this.resetElement();
		// }
	},
	_afterMounted: function(el, callback){
		this.node = el;
		this.node.store("module", this);
		this._loadVueCss();
		if (callback) callback();
		//window.setTimeout(function(){
		this.node.getElement("input").addEvent("click", function(e){
			this.selected();
			e.stopPropagation();
		}.bind(this));
		this.node.getElement(".el-cascader").addEvent("click", function(e){
			this.selected();
			e.stopPropagation();
		}.bind(this));
		this.node.getElement("i").addEvent("click", function(e){
			this.selected();
			e.stopPropagation();
		}.bind(this));

	},
});
