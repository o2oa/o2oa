MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elinput", null, false);
MWF.xApplication.process.FormDesigner.Module.Elslider = MWF.FCElslider = new Class({
	Extends: MWF.FCElinput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elslider/elslider.html"
	},

	_initModuleType: function(){
		this.className = "Elslider";
		this.moduleType = "element";
		this.moduleName = "elslider";
	},
	_createElementHtml: function(){
		debugger;
		//var html = "<el-input placeholder=\"请输入内容\"></el-input>";
		var html = "<el-slider";
		html += " v-model=\"tmpValue\"";
		// if (this.json.max) html += " max=\""+this.json.max+"\"";
		// if (this.json.min) html += " min=\""+this.json.min+"\"";
		// if (this.json.step) html += " step=\""+this.json.step+"\"";
		// if (this.json.showStops) html += " show-stops";
		// if (this.json.showTooltip) html += " show-tooltip";
		// if (this.json.range) html += " range";
		// if (this.json.vertical) html += " vertical";
		// if (this.json.vertical && !this.json.height) this.json.height = "100px";
		// if (this.json.height) html += " height=\""+this.json.height+"\"";
		// if (this.json.showInput) html += " show-input";
		// if (this.json.showInputControls===false) html += " :show-input-controls=\"tmpShowInputControls\"";
		// if (this.json.inputSize) html += " input-size=\""+this.json.inputSize+"\"";
		// if (this.json.showTooltip!==false) html += " show-tooltip";
		// if (this.json.tooltipClass) html += " tooltip-class=\""+this.json.tooltipClass+"\"";

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
