MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elinput", null, false);
MWF.xApplication.process.FormDesigner.Module.Elselect = MWF.FCElselect = new Class({
	Extends: MWF.FCElinput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elselect/elselect.html"
	},

	_initModuleType: function(){
		this.className = "Elselect";
		this.moduleType = "element";
		this.moduleName = "elselect";
	},
	_createElementHtml: function(){
		//var html = "<el-input placeholder=\"请输入内容\"></el-input>";
		var html = "<el-select";
		// if (this.json.description) html += " placeholder=\""+this.json.description+"\"";
		// if (this.json.size && this.json.size!=="default") html += " size=\""+this.json.size+"\"";
		// if (this.json.clearable) html += " clearable";
		// if (this.json.popperClass) html += " popper-class=\""+this.json.popperClass+"\"";
		// if (this.json.multiple ) html += " multiple";
		// if (this.json.collapseTags) html += " collapse-tags";
		// if (this.json.multipleLimit) html += " multiple-limit=\""+this.json.multipleLimit+"\"";
		// if (this.json.filterable) html += " filterable";
		// if (this.json.allowCreate) html += " allow-create";
		// if (this.json.noMatchText) html += " no-match-text=\""+this.json.noMatchText+"\"";
		// if (this.json.noDataText) html += " no-data-text=\""+this.json.noDataText+"\"";
		// if (this.json.filterRemote) html += " remote";
		// if (this.json.loadingText) html += " loading-text=\""+this.json.loadingText+"\"";
		// html += " :fetch-suggestions=\"$fetchSuggestions\"";


		html += " :placeholder=\"description\"";
		html += " :size=\"size+\"";
		html += " :clearable=\"clearable\"";
		html += " :popper-class=\"popperClass+\"";
		html += " :multiple=\"multiple\"";
		html += " :collapse-tags=\"collapseTags\"";
		html += " :multiple-limit=\"multipleLimit\"";
		html += " :filterable=\"filterable\"";
		html += " :allow-create=\"allowCreate\"";
		html += " :no-match-text=\"noMatchText\"";
		html += " :no-data-text=\"noDataText\"";
		html += " :remote=\"filterRemote\"";
		html += " :loading-text=\"loadingText\"";


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
		html += "<el-option></el-option>";
		if (this.json.vueSlot) html += this.json.vueSlot;
		html += "</el-select>";
		return html;
	},
	_createVueExtend: function(callback){
		debugger;
		var _self = this;
		return {
			//data: this._createVueData(),
			data: this.json,
			mounted: function(){
				_self._afterMounted(this.$el, callback);
			}
			// methods: {
			// 	$fetchSuggestions: function(qs, cb){
			// 		if (this.json.itemType!=='script'){
			// 			if (this.json.itemValues){
			// 				cb(this.json.itemValues.map(function(v){
			// 					return {"value": v};
			// 				}));
			// 				return;
			// 			}
			// 		}
			// 		cb([]);
			// 	}.bind(this)
			// }
		};
	},
	_createVueData: function(){
		//var data = this.json;
		return function(){
			return Object.assign(this.json, this.tmpVueData||{});
		}.bind(this);
	},
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
		this.node.getElement("input").removeEvents("click");
		if (callback) callback();
	},
});
