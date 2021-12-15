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
	_createElementHtml: function(){
		debugger;
		var html = "<el-slider";
		html += " v-model=\"tmpValue\"";

		html += " :max=\"getMax()\"";
		html += " :min=\"getMin()\"";
		html += " :step=\"getStep()\"";
		html += " :show-stops=\"showStops\"";
		html += " :show-tooltip=\"showTooltip\"";
		html += " :range=\"range\"";
		html += " :vertical=\"vertical\"";
		html += " :height=\"getHeight()\"";
		html += " :show-input=\"showInput\"";
		html += " :show-input-controls=\"showInputControls\"";
		html += " :input-size=\"inputSize\"";
		html += " :show-tooltip=\"showTooltip\"";
		html += " :tooltip-class=\"tooltipClass\"";

		html += " :style=\"elStyles\"";
		html += "></el-slider>";
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
				getHeight: function(){
					return (this.$data.vertical && !this.$data.height) ? "100px" : this.$data.height;
				},
				getMax: function(){
					return this.$data.max.toFloat();
				},
				getMin: function(){
					return this.$data.min.toFloat();
				},
				getStep: function(){
					return this.$data.step.toFloat();
				}
			}
		};
	},
	_createVueData: function(){
		var data = this.json;
		Object.assign(data, this.tmpVueData||{});

		var max = (!this.json.max || !this.json.max.toFloat()) ? 100 : this.json.max.toFloat();
		var min = (!this.json.min || !this.json.min.toFloat()) ? 0 : this.json.min.toFloat();

		if (this.json.range){
			var d1 = ((max-min)/3);
			data.tmpValue = [d1+min, d1*2+min];
		}else{
			var d = (max-min)/2+min;
			data.tmpValue = d;
		}

		if (this.json.showInputControls===false) data.tmpShowInputControls = false;

		return data;
	},
	_setEditStyle_custom: function(name){

		switch (name){
			case "name": this.setPropertyName(); break;
			case "id": this.setPropertyId(); break;
			case "range":
			case "max":
			case "vertical":
			case "step":
				var max = (!this.json.max || !this.json.max.toFloat()) ? 100 : this.json.max.toFloat();
				var min = (!this.json.min || !this.json.min.toFloat()) ? 0 : this.json.min.toFloat();

				if (this.json.range){
					var d1 = ((max-min)/3);
					this.json.tmpValue = [d1+min, d1*2+min];
				}else{
					var d = (max-min)/2+min;
					this.json.tmpValue = d;
				}
				break;
			default: ;
		}
	},
	setPropertyName: function(){},
	setPropertyId: function(){}
});
