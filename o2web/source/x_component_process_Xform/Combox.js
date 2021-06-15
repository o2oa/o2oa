MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
/** @class Combox 组合框组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 * @extends MWF.xApplication.process.Xform.$Input
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Combox = MWF.APPCombox =  new Class(
    /** @lends MWF.xApplication.process.Xform.Combox# */
{
	Implements: [Events],
	Extends: MWF.APP$Input,
	iconStyle: "selectIcon",
    options: {
        /**
         * 手工输入完成后触发。
         * @event MWF.xApplication.process.Xform.Combox#commitInput
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 值改变时触发。
         * @event MWF.xApplication.process.Xform.Combox#change
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "commitInput", "change"]
    },

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
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
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
        //new Element("select").inject(this.node);
    },
    _loadNodeEdit: function(){
        this.node.empty();

        MWF.require("MWF.widget.Combox", function(){
            this.combox = select = new MWF.widget.Combox({
                "onlySelect": this.json.onlySelect==="y",
                "count": this.json.count.toInt() || 0,
                "splitStr": this.json.splitStr || ",\\s*|;\\s*|，\\s*|；\\s*",
                "splitShow": this.json.splitShow || ", ",
                "list": this.getOptions(),
                "onCommitInput": function(item){
                    this.fireEvent("commitInput");
                }.bind(this),
                "onChange": function(){
                    this.fireEvent("change");
                }.bind(this),
                "optionsMethod": this._searchOptions()
            });
        }.bind(this), false);


        // var select = new Element("select");
        // select.set(this.json.properties);
        select.inject(this.node);
        //this.node.destroy();
        // this.areaNode = this.node;
        // this.node = select;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });

        this.combox.addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));

    },
    _searchOptions: function(){
        if (this.json.itemType === "dynamic"){
			return function(value, callback){
				var event = {
					"value": value,
					"callback": callback
				};
                this.form.Macro.fire(((this.json.itemDynamic) ? this.json.itemDynamic.code : ""), this, event);
			}.bind(this);
		}else{
        	return null;
		}
	},
    /**
     * @summary 获取选择项数组.
     * @example
     * var array = this.form.get('fieldId').getOptions();
     * @return {Array} 选择项数组，如果配置为脚本返回计算结果.
     */
    getOptions: function(){
    	var list = [];
        if (this.json.itemType === "values"){
            list = this.json.itemValues;
        }else if (this.json.itemType === "script"){
            list = this.form.Macro.exec(((this.json.itemScript) ? this.json.itemScript.code : ""), this);
        }

        if (list.length){
            var options = [];
            list.each(function(v){
                if (typeOf(v)==="object"){
                    options.push(v);
                }else{
                	v = v.toString();
                	arr = v.split("|");
                	var o = { "text": "", "keyword": "", "value": "" };
                	switch (arr.length){
                        case 0: break;
						case 1:
                            o.text = arr[0];
                            o.keyword = arr[0];
                            o.value = arr[0];
							break;
						case 2:
                            o.text = arr[0];
                            o.keyword = arr[0];
                            o.value = arr[1];
							break;
						case 3:
                            o.text = arr[0];
                            o.keyword = arr[1];
                            o.value = arr[2];
							break;
						default:
                            o.text = arr[0];
                            o.keyword = arr[1];
                            o.value = arr[2];
					}
                    options.push(o);
				}
			}.bind(this));
            return options;
		}
        return [];
    },
    /**
     * 当表单上没有对应组件的时候，可以使用this.data[fieldId] = data赋值。
     * @summary 为组件赋值。
     * @param value{String} .
     * @example
     *  this.form.get("fieldId").setData("test"); //赋文本值
     * @example
     *  //如果无法确定表单上是否有组件，需要判断
     *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
     *      this.form.get('fieldId').setData( data );
     *  }else{
     *      this.data['fieldId'] = data;
     *  }
     */
    setData: function(value){
        this._setBusinessData(value);
        this._setValue(value);
	},
    _setValue: function(value){
        if (!value) value = [];
        if (value.length==1 && (!value[0])) value = [];
        if (typeOf(value) !=="array") value = [value];

		if (this.combox){
            this.combox.clear();

            comboxValues = [];
            value.each(function(v){
				if (typeOf(v)==="object"){
                    comboxValues.push({
						"text": v.text || v.title || v.subject  || v.name,
						"value": v
                    });
				}else{
                    comboxValues.push(v.toString());
				}
			}.bind(this));
            this.combox.addNewValues(comboxValues);
		}else{
		    var contentNode = new Element("div", {
		        "styles": { "overflow": "hidden"}
            }).inject(this.node);
            value.each(function(v, i){
                var text = "";
                if (typeOf(v)==="object"){
                	text = v.text || v.title || v.subject  || v.name;
                }else{
                    text = v.toString();
                }
                if (i<value.length-1) text += this.json.splitShow;
                new Element("div", {"styles": {
                    "float": "left",
                    "margin-right": "5px"
                },"text": text}).inject( contentNode ); //.inject(this.node.getFirst() || this.node);
            }.bind(this));
		}
    },
    /**
     * @summary 重新计算下拉选项，该功能通常用在下拉选项为动态计算的情况.
     * @example
     * this.form.get('fieldId').resetOption();
     */
    resetOption: function(){
        if (this.combox){
            var list = this.getOptions();
            this.combox.setOptions({"list": list});
        }
    },
    /**
     * @summary 添加下拉选项.
     * @param text  {String} 下拉选项文本
     * @param value {String} 下拉选项值
     * @example
     * this.form.get('fieldId').addOption("秘密","level1");
     */
	addOption: function(text, value){
        if (this.combox){
            var list = this.getOptions();
            list.push({
				"text": text,
				"value": value
			});
            this.combox.setOptions({"list": list});
        }
	},
    isEmpty : function(){
        var data = this.getData();
        if( typeOf(data) === "array" ){
            return data.length === 0;
        }else{
            return !data;
        }
    },
    getInputData: function(){
        if (this.combox) return this.combox.getData();
        return this._getBusinessData();
    },
    /**
     * @summary 获取选中的值和文本.
     * @example
     * var array = this.form.get('fieldId').getTextData();
     * @return {Object} 返回选中项值和文本，格式为 { 'value' : value, 'text' : text }.
     */
    getTextData: function(){
	    var v = this.getData();
        return {"value": v, "text": v};
        //return this.node.get("text");
    },
    resetData: function(){
        this.setData(this.getValue());
    }
	
}); 