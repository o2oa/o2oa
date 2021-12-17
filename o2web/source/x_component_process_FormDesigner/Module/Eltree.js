MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elinput", null, false);
MWF.xApplication.process.FormDesigner.Module.Eltree = MWF.FCEltree = new Class({
	Extends: MWF.FCElinput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Eltree/eltree.html"
	},



	_initModuleType: function(){
		this.className = "Eltree";
		this.moduleType = "element";
		this.moduleName = "eltree";
	},
	_afterMounted: function(el, callback){
		this.node = el;
		this.node.store("module", this);
		this._loadVueCss();
		this._createIcon();
		if (callback) callback();
	},
	_filterHtml: function(html){
		var reg = /(?:@|v-on|v-model)\S*(?:\=)\S*(?:\"|\'|\s)/g;
		var v = html.replace(reg, "");
		return v;
	},
	_createIcon: function(){
		this.iconNode = new Element("div", {
			"styles": this.css.iconNode,
			"o2icon": "eltree"
		}).inject(this.node, "top");
		new Element("div", {
			"styles": this.css.iconNodeIcon
		}).inject(this.iconNode);
		new Element("div", {
			"styles": this.css.iconNodeText,
			"text": "Eltree"
		}).inject(this.iconNode);
	},
	_createElementHtml: function(){
		debugger;
		var html =  "<el-tree";
		html += " v-model=\"tmpValue\"";

		html += " :empty-text=\"emptyText\"";
		html += " :node-key=\"nodeKey\"";
		html += " :data=\"data\"";
		html += " :props=\"defaultProps\"";
		html += " :render-after-expand=\"renderAfterExpand\"";
		html += " :highlight-current=\"highlightCurrent\"";
		html += " :default-expand-all=\"defaultExpandAll\"";
		html += " :expand-on-click-node=\"expandOnClickNode\"";
		html += " :check-on-click-node=\"checkOnClickNode\"";
		html += " :show-checkbox=\"showCheckbox\"";
		html += " :check-strictly=\"checkStrictly\"";
		html += " :accordion=\"accordion\"";
		html += " :indent=\"indent\"";
		//
		// html += " :style=\"elStyles\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		html += "></el-tree>";
		return html;
	},
	_refreshTree: function(){

	},
	_createVueExtend: function(callback){
		var _self = this;
		return {
			data: this._createVueData(),
			// mounted: function(){
			// 	_self._afterMounted(this.$el, callback);
			// },
			mounted: function(){
				this.$nextTick(function(){
					_self._afterMounted(this.$el, callback);
				});
			},
			methods: {

			}
		};
	},
	_createVueData: function(){
		return function() {
			var data = this.json;

			if (data.treeData) {
				data.data = data.treeData;
			}

			Object.assign(data, this.tmpVueData || {});

			return data;
		}.bind(this)
	},
	// _setEditStyle_custom: function(name){
	//
	// 	// switch (name){
	// 	// 	case "name": this.setPropertyName(); break;
	// 	// 	case "id": this.setPropertyId(); break;
	// 	// 	case "range":
	// 	// 	case "max":
	// 	// 	case "vertical":
	// 	// 	case "step":
	// 	// 		var max = (!this.json.max || !this.json.max.toFloat()) ? 100 : this.json.max.toFloat();
	// 	// 		var min = (!this.json.min || !this.json.min.toFloat()) ? 0 : this.json.min.toFloat();
	// 	//
	// 	// 		if (this.json.range){
	// 	// 			var d1 = ((max-min)/3);
	// 	// 			this.json.tmpValue = [d1+min, d1*2+min];
	// 	// 		}else{
	// 	// 			var d = (max-min)/2+min;
	// 	// 			this.json.tmpValue = d;
	// 	// 		}
	// 	// 		break;
	// 	// 	default: ;
	// 	// }
	// },
	setPropertyName: function(){},
	setPropertyId: function(){}
});
