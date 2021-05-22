MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
/** @class Textfield 文本输入框。
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
MWF.xApplication.process.Xform.Textfield = MWF.APPTextfield =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Input,
	iconStyle: "textFieldIcon",
	
	_loadUserInterface: function(){
		this._loadNode();
        if (this.json.compute === "show"){
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
    loadDescription: function(){
        if (this.readonly || this.json.isReadonly)return;
        var v = this._getBusinessData();
        if (!v){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-3;
                if( this.json.showIcon!='no' && !this.form.json.hideModuleIcon ){
                    if (COMMON.Browser.safari) w = w-20;
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
                        this.node.getFirst().fireEvent("click");
                    }.bind(this)
                });
            }else if (COMMON.Browser.Platform.name==="android"){
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                        this.node.getFirst().fireEvent("click");
                    }.bind(this)
                });
            }else{
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                        this.node.getFirst().fireEvent("click");
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
    },
    _loadNodeRead: function(){
        this.node.empty();
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
    },

    _resetNodeEdit: function(){
        var input = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "display": "block",
                "border": "0px"
            }
        });

        var node = new Element("div", {"styles": {
                "overflow": "hidden",
                "position": "relative",
                "margin-right": "20px",
                "padding-right": "4px"
            }}).inject(this.node, "after");
        input.inject(node);

        this.node.destroy();
        this.node = node;
    },

    _loadNodeEdit: function(){
        if (!this.json.preprocessing) this._resetNodeEdit();
        var input = this.node.getFirst();
        input.set(this.json.properties);
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type,
            "events": {
                "click": this.clickSelect.bind(this)
            }
        });
        if (this.json.showIcon!='no' && !this.form.json.hideModuleIcon){
            this.iconNode = new Element("div", {
                "styles": this.form.css[this.iconStyle]
            }).inject(this.node, "before");
        }else if( this.form.json.nodeStyleWithhideModuleIcon ){
            this.node.setStyles(this.form.json.nodeStyleWithhideModuleIcon);
        }

        this.node.getFirst().addEvent("change", function(){
            var v = this.getInputData("change");
            this._setBusinessData(v);
            this.validationMode();
            if (this.validation()) {
                this._setBusinessData(v);
                this.fireEvent("change");
            }
        }.bind(this));
        if (this.json.ANNModel){
            this.node.getFirst().addEvent("focus", function(){
                o2.Actions.get("x_query_assemble_surface").calculateNeural(this.json.ANNModel, this.form.businessData.work.id, function(json){
                    var arr = json.data.filter(function(d){
                        var value = this.node.getFirst().get("value");
                        return d.score>0.1 && (value.indexOf(d.value)===-1)
                    }.bind(this));
                    if (arr.length){
                        if (!this.modelNode) this.createModelNode();
                        this.modelNode.getLast().empty();
                        this.modelNode.show();
                        this.modelNode.position({ "relativeTo": this.node, "position": "bottomLeft", "edge": 'upperLeft' });

                        arr.each(function(v){
                            var node = new Element("div", {"text": v.value, "styles": this.form.css.modelItemNode}).inject(this.modelNode.getLast());
                            node.addEvents({
                                "mouseover": function(){this.setStyle("color", "#0000ff");},
                                "mouseout": function(){this.setStyle("color", "#a31515");},
                                "mousedown": function(e){
                                    var str = this.node.getFirst().get("value")
                                    this.node.getFirst().set("value", ((str) ? str+", "+e.target.get("text") : e.target.get("text")));
                                    this.modelNode.hide();
                                }.bind(this)
                            });
                        }.bind(this));
                    }
                }.bind(this));
            }.bind(this));

            this.node.getFirst().addEvent("blur", function(){
                if (this.modelNode) this.modelNode.hide();
            }.bind(this));
        }

        this.node.getFirst().addEvent("blur", function(){
            this.validation();
        }.bind(this));
        this.node.getFirst().addEvent("keyup", function(){
            this.validationMode();
        }.bind(this));
	},
    createModelNode: function(){
        this.modelNode = new Element("div", {"styles": this.form.css.modelNode}).inject(this.node, "after");
        new Element("div", {"styles": this.form.css.modelNodeTitle, "text": MWF.xApplication.process.Xform.LP.ANNInput}).inject(this.modelNode);
        new Element("div", {"styles": this.form.css.modelNodeContent, "text": MWF.xApplication.process.Xform.LP.ANNInput}).inject(this.modelNode);
    },


    getInputData: function(){
        if (this.node.getFirst()){
            var v = this.node.getElement("input").get("value");
            if (this.json.dataType=="number"){
                var n = v.toFloat();
                return (isNaN(n)) ? 0 : n;
            }
        }else{
            return this._getBusinessData();
        }
        return v;
    }

}); 
