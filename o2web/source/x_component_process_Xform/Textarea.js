MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
/** @class Textarea 多行文本组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 *
 * var data = field.getData(); //获取值
 * field.setData("字符串值"); //设置值
 * field.hide(); //隐藏字段
 * var id = field.json.id; //获取字段标识
 * var flag = field.isEmpty(); //字段是否为空
 * field.resetData();  //重置字段的值为默认值或置空
 * @extends MWF.xApplication.process.Xform.$Input
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Textarea = MWF.APPTextarea =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Input,
	
	_loadUserInterface: function(){
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
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
    },


    _setValue: function(value){
	    if (!value) value = "";
        var p = o2.promiseAll(value).then(function(v){
            if (o2.typeOf(v)=="array") v = v[0];
            this._setBusinessData(v);
            if (this.node.getFirst()) this.node.getFirst().set("value", v || "");
            if (this.readonly || this.json.isReadonly){
                var reg = new RegExp("\n","g");
                var reg2 = new RegExp("\u003c","g"); //尖括号转义，否则内容会截断
                var reg3 = new RegExp("\u003e","g");
                var text = value.replace(reg2,"&lt").replace(reg3,"&gt").replace(reg,"<br/>");
                this.node.set("html", text);
            }
            //this.__setValue(v);
        }.bind(this), function(){});
        this.moduleValueAG = p;
        p.then(function(){
            this.moduleValueAG = null;
        }.bind(this), function(){
            this.moduleValueAG = null;
        }.bind(this));

        // this.moduleValueAG = o2.AG.all(value).then(function(v){
        //     this.moduleValueAG = null;
        //     if (o2.typeOf(v)=="array") v = v[0];
        //     this._setBusinessData(v);
        //     if (this.node.getFirst()) this.node.getFirst().set("value", v || "");
        //     if (this.readonly || this.json.isReadonly){
        //         var reg = new RegExp("\n","g");
        //         var reg2 = new RegExp("\u003c","g"); //尖括号转义，否则内容会截断
        //         var reg3 = new RegExp("\u003e","g");
        //         var text = value.replace(reg2,"&lt").replace(reg3,"&gt").replace(reg,"<br/>");
        //         this.node.set("html", text);
        //     }
        // }.bind(this));
        //
        // if (this.moduleValueAG) this.moduleValueAG.then(function(){
        //     this.moduleValueAG = null;
        // }.bind(this));

        return value;

        // if (value && value.isAG){
        //     this.moduleValueAG = value;
        //     value.addResolve(function(v){
        //         this._setValue(v);
        //     }.bind(this));
        // }else{
        //     this._setBusinessData(value);
        //     if (this.node.getFirst()) this.node.getFirst().set("value", value || "");
        //     if (this.readonly || this.json.isReadonly){
        //         var reg = new RegExp("\n","g");
        //         var reg2 = new RegExp("\u003c","g"); //尖括号转义，否则内容会截断
        //         var reg3 = new RegExp("\u003e","g");
        //         var text = value.replace(reg2,"&lt").replace(reg3,"&gt").replace(reg,"<br/>");
        //         this.node.set("html", text);
        //     }
        //     return value;
        // }
    },

    // _setValue: function(value){
    //     this._setBusinessData(value);
    //     if (this.node.getFirst()) this.node.getFirst().set("value", value || "");
    //     if (this.readonly || this.json.isReadonly){
    //             var reg = new RegExp("\n","g");
    //             var reg2 = new RegExp("\u003c","g"); //尖括号转义，否则内容会截断
    //             var reg3 = new RegExp("\u003e","g");
    //             var text = value.replace(reg2,"&lt").replace(reg3,"&gt").replace(reg,"<br/>");
    //             this.node.set("html", text);
    //     }
    // },
    _resetNodeEdit: function(){
        var input = new Element("textarea", {"styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px"
            }});
        var node = new Element("div", {"styles": {
                "ovwrflow": "hidden",
                "position": "relative",
                "padding-right": "2px"
            }}).inject(this.node, "after");
        input.inject(node);
        this.node.destroy();
        this.node = node;
    },
    _loadNodeEdit: function(){
        if (!this.json.preprocessing) this._resetNodeEdit();
        var input = this.node.getFirst();
        input.set(this.json.properties);

        if( this.form.json.textareaDisableResize )input.setStyle("resize","none");

		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type
		});
        this.node.addEvent("change", function(){
            this._setBusinessData(this.getInputData());
            this.fireEvent("change");
        }.bind(this));

        this.node.getFirst().addEvent("blur", function(){
            this.validation();
        }.bind(this));
        this.node.getFirst().addEvent("keyup", function(){
            this.validationMode();
        }.bind(this));
	},
	_afterLoaded: function(){
        if (!this.readonly){
            this.loadDescription();
        }
	},
    loadDescription: function(){
        if (this.readonly || this.json.isReadonly)return;
        var v = this._getBusinessData();
        if (!v){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-3;
                if( this.json.showIcon!='no' && !this.form.json.hideModuleIcon ){
                    w = size.x-23;
                }
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "width": ""+w+"px",
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            if (COMMON.Browser.Platform.name==="ios"){
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }else if (COMMON.Browser.Platform.name==="android"){
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }else{
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }
            this.node.getFirst().addEvents({
                "focus": function(){
                    this.descriptionNode.setStyle("display", "none");
                }.bind(this),
                "blur": function(){
                    if (!this.node.getFirst().get("value")) this.descriptionNode.setStyle("display", "block");
                }.bind(this)
            });
        }
    }
	
}); 
