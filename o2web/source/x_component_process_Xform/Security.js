MWF.xDesktop.requireApp("process.Xform", "Select", null, false);
/** @class Security 密级选择组件。
 * 启用密级标识功能后，此组件允许选择客体密级
 * @o2cn 下拉选择
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 *
 * field.hide(); //隐藏字段
 * var id = field.json.id; //获取字段标识
 * var flag = field.isEmpty(); //字段是否为空
 * @extends MWF.xApplication.process.Xform.$Selector
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Security = MWF.APPSecurity =  new Class(
	/** @lends MWF.xApplication.process.Xform.Select# */
	{
		Implements: [Events],
		Extends: MWF.APPSelect,
		iconStyle: "selectIcon",

		/**
		 * 值改变时触发。可以通过this.event获取修改后的选择项（Dom对象）。
		 * @event MWF.xApplication.process.Xform.Security#change
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */

		/**
		 * @ignore
		 * @member {Element} descriptionNode
		 * @memberOf MWF.xApplication.process.Xform.Security#
		 */
		initialize: function(node, json, form, options){
			this.node = $(node);
			this.node.store("module", this);
			this.json = json;
			this.form = form;
			this.field = true;
			this.fieldModuleLoaded = false;
			this.nodeHtml = this.node.get("html");
		},
		__showValue: function(node, value, optionItems){
			if (value){
				value = value.toString();
				if (typeOf(value)!=="array") value = [value];
				var texts = [];
				optionItems.each(function(item){
					var tmps = item.split("|");
					var t = tmps[0];
					var v = tmps[1] || t;

					if (v){

						if (value.indexOf(v)!=-1){
							texts.push(t);
						}
					}

				});
				node.set("text", texts.join(", "));
			}
		},


		_getOptions: function(async, refresh){
			if (this.securityLabelList) return Promise.resolve(this.securityLabelList);

			return o2.Actions.load("x_general_assemble_control").SecurityClearanceAction.object().then(function(json){
				var opts = ["|"];
				Object.keys(json.data).forEach(function(k){
					opts.push(k+"|"+json.data[k]);
				}.bind(this))
				this.securityLabelList = opts;
				return this.securityLabelList;
			}.bind(this));
		},
		_setBusinessData: function(v, id){
			var value = v.toInt();
			this.setBusinessDataById(value, id);
			// this.form.businessData.data.$work.objectSecurityClearance = value;
		},
		getInputData: function(){
			if( this.isReadonly()){
				return this._getBusinessData();
			}else{
				var ops = this.node.getElements("option");
				var value = [];
				ops.each(function(op){
					if (op.selected){
						var v = op.get("value");
						value.push(v || "");
					}
				});
				if (!value.length) return null;
				return (value.length==1) ? value[0].toInt() : value.toInt();
			}
		},
		addOption: function(){}
	});
