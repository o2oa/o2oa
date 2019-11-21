MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.ScriptText",null,false);
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default"
	},
	initialize: function(node, text, options){
		this.setOptions(options);
		this.node = $(node);
        this.data = (text) ? JSON.decode(text) : [];
        this.name = node.get("name");
		this.path = "/x_component_process_ProcessDesigner/widget/$SerialEditor/";
		this.cssPath = "/x_component_process_ProcessDesigner/widget/$SerialEditor/"+this.options.style+"/css.wcss";
		this._loadCss();
        this.selectedItems = [];
        this.items = {};
	},

    load: function(){
        this.titleNode = new Element("div", {"styles": this.css.titleNode}).inject(this.node);
        this.titleNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.serialSelectTitle);

        this.selectNode = new Element("div", {"styles": this.css.selectNode}).inject(this.node);

        this.downNode = new Element("div", {"styles": this.css.downNode}).inject(this.node);

        this.previewNode = new Element("div", {"styles": this.css.previewNode}).inject(this.node);
        this.showNode = new Element("div", {"styles": this.css.showNode}).inject(this.node);

        this.propertyNode = new Element("div", {"styles": this.css.propertyNode}).inject(this.node);

        this.loadSelectNode();
    //    this.loadSerialActivity();
    },
    loadSelectNode: function(){
        this.getSerialRule(function(){
            this.loadSelectNodeItems();
            this.loadSelectedNodeItems();
        }.bind(this));
    },

    loadSelectedNodeItems: function(){
        this.data.each(function(itemData){
            this.selectedItems.push(new MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem[itemData.key.capitalize()](this.items[itemData.key], itemData));
        }.bind(this));
        this.fireEvent("change");
    },

    loadSelectNodeItems: function(){
        Object.each(this.serialRuleJson, function(v, k){
            this.loadSelectNodeItem(v, k);
        }.bind(this));
    },

    loadSelectNodeItem: function(v, k){
        this.items[k] = new MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.Item(v, k, this);
    },

    getSerialRule: function(callback){
        if (!this.serialRuleJson){
            var serialConifgUrl = "/x_component_process_ProcessDesigner/$Process/serialRule.json";
            MWF.getJSON(serialConifgUrl, function(json){
                this.serialRuleJson = json;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    getData: function(){
        var data = [];
        this.selectedItems.each(function(item){
            data.push(item.getData());
        });
        this.data = data;
        return data;
    }
});

MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.Item = new Class({
    initialize: function(value, key, editor){
        this.editor = editor;
        this.json = value;
        this.key = key;
        this.css = this.editor.css;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.itemNode}).inject(this.editor.selectNode);
        this.iconNode = new Element("div", {"styles": this.css.itemIconNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.itemTextNode}).inject(this.node);


        this.textNode.set({
            "text": this.json.text,
            "title": this.json.description
        });
        this.node.addEvents({
            "mouseover": function(){this.node.setStyles(this.css.itemNode_over); this.iconNode.setStyles(this.css.itemIconNode_over);}.bind(this),
            "mouseout": function(){this.node.setStyles(this.css.itemNode); this.iconNode.setStyles(this.css.itemIconNode);}.bind(this),
            "click": function(){this.selectNumberItem();}.bind(this)
        });
    },
    selectNumberItem: function(){
        this.editor.selectedItems.push(new MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem[this.key.capitalize()](this));
        this.editor.fireEvent("change");
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem = new Class({
    initialize: function(item, itemData){
        this.item = item;
        this.json = item.json;
        this.key = item.key;
        this.editor = item.editor;
        this.css = this.editor.css;
        this.data = itemData;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.selectedItemNode}).inject(this.editor.showNode);

        this.textNode = new Element("div", {"styles": this.css.selectedItemTextNode}).inject(this.node);
        this.textNode.set({
            "text": this.json.text,
            "title": this.json.description
        });

        this.node.addEvents({
            "mouseover": function(){
                if (this.editor.currentItem!=this) this.node.setStyles(this.css.selectedItemNode_over);
            }.bind(this),
            "mouseout": function(){
                if (this.editor.currentItem!=this) this.node.setStyles(this.css.selectedItemNode);
            }.bind(this),

            "click": function(){this.selectItem();}.bind(this)
        });

        this.closeNode = new Element("div", {"styles": this.css.selectedItemCloseNode}).inject(this.node);
        this.closeNode.addEvent("click", function(){
            this.deleteItem();
        }.bind(this));

        this.loadProperty();
        this.selectItem();
    },
    loadProperty: function(){},

    deleteItem: function(){
        this.node.destroy();
        if (this.propertyNode) this.propertyNode.destroy();
        this.editor.selectedItems.erase(this);
        if (this.editor.currentItem === this) this.editor.currentItem = null;
        this.editor.fireEvent("change");
        MWF.release(this);
    },
    selectItem: function(){
        if (this.editor.currentItem) this.editor.currentItem.unSelectItem();
        if (this.propertyNode){
            this.propertyNode.setStyle("display", "block");
            if (this.key==="number"){
                this.loadNumberBy();
            }
        }
        this.node.setStyles(this.css.selectedItemNode_check);
        this.editor.currentItem = this;
    },
    unSelectItem: function(){
        this.node.setStyles(this.css.selectedItemNode);
        if (this.propertyNode) this.propertyNode.setStyle("display", "none");
        this.editor.currentItem = null;
    }
});

MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Text = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,

    loadProperty: function(){
        this.propertyNode = new Element("div", {"styles": this.css.itemPropertyNode}).inject(this.editor.propertyNode);
        this.propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(this.propertyNode);
        this.propertyTitleNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.serialTextTitle);
        this.propertyInputDivNode = new Element("div", {"styles": this.css.propertyInputDivNode}).inject(this.propertyNode);
        this.propertyInputNode = new Element("input", {
            "type": "text",
            "value": (this.data) ? this.data.value: "",
            "styles": this.css.propertyInputNode
        }).inject(this.propertyInputDivNode);
        this.changeText();

        this.propertyInputNode.addEvents({
            "change": function(){
                this.changeText();
            }.bind(this),
            "blur": function(){},
        });
    },
    changeText: function(){
        var value = this.propertyInputNode.get("value");
        if (value){
            this.textNode.set("text", "\""+value+"\"");
        }else{
            this.textNode.set("text", this.json.text);
        }
        this.editor.fireEvent("change");
    },
    getData: function(){
        var value = this.propertyInputNode.get("value");
        var key = this.key;
        var script = "return serial.text(\""+value+"\")";
        return {
            "key": key,
            "value": value,
            "script": script
        }
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Year = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,
    loadProperty: function(){
        this.value = "created";
        var i = Math.random();
        this.propertyNode = new Element("div", {"styles": this.css.itemPropertyNode}).inject(this.editor.propertyNode);
        var propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(this.propertyNode);
        propertyTitleNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.serialDateTitle);
        var propertyInputDivNode = new Element("div", {"styles": this.css.propertyInputDivNode}).inject(this.propertyNode);

        var v = (this.data) ? this.data.value: "created";
        html = "<input name=\"serialDateSelect"+i+"\" "+((v=="created") ? "checked" : "")+" type=\"radio\" value=\"created\"/>" + MWF.xApplication.process.ProcessDesigner.LP.serialCreatedDateTitle;
        html += "<input name=\"serialDateSelect"+i+"\" "+((v=="current") ? "checked" : "")+" type=\"radio\" value=\"current\"/>" + MWF.xApplication.process.ProcessDesigner.LP.serialCurrentDateTitle;
        propertyInputDivNode.set("html", html);
        this.changeText((this.data) ? this.data.value: "created");
        propertyInputDivNode.getElements("input").addEvent("click", function(e){
            if (e.target.checked){
                var v = e.target.get("value");
                this.changeText(v);
            }
        }.bind(this));
    },
    changeText: function(v){
        var text = MWF.xApplication.process.ProcessDesigner.LP.serialCreated;
        if (v=="current"){
            text = MWF.xApplication.process.ProcessDesigner.LP.serialCurrent;
        }
        this.value = v;
        this.textNode.set("text", this.json.text+"("+text+")");
        this.editor.fireEvent("change");
    },
    getData: function(){
        var key = this.key;
        var f = (this.value=="current") ? "year" : "createYear";
        var script = "return serial."+f+"(\"yyyy\")";
        return {
            "key": key,
            "value": this.value,
            "script": script
        }
    }
});

MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Month = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Year,
    getData: function(){
        var key = this.key;
        var f = (this.value=="current") ? "month" : "createMonth";
        var script = "return serial."+f+"(\"MM\")";
        return {
            "key": key,
            "value": this.value,
            "script": script
        }
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Day = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Year,
    getData: function(){
        var key = this.key;
        var f = (this.value=="current") ? "day" : "createDay";
        var script = "return serial."+f+"(\"dd\")";
        return {
            "key": key,
            "value": this.value,
            "script": script
        }
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Company = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,
    getData: function(){
        var key = this.key;
        var script = "return serial.company()";
        return {
            "key": key,
            "value": "",
            "script": script
        }
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Department = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,
    getData: function(){
        var key = this.key;
        var script = "return serial.department()";
        return {
            "key": key,
            "value": "",
            "script": script
        }
    }
});

MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.CompanyAttribute = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,

    loadProperty: function(){
        this.propertyNode = new Element("div", {"styles": this.css.itemPropertyNode}).inject(this.editor.propertyNode);
        this.propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(this.propertyNode);
        this.propertyTitleNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.serialAttributeTitle);
        this.propertyInputDivNode = new Element("div", {"styles": this.css.propertyInputDivNode}).inject(this.propertyNode);
        this.propertyInputNode = new Element("input", {
            "type": "text",
            "value": (this.data) ? this.data.value: "",
            "styles": this.css.propertyInputNode
        }).inject(this.propertyInputDivNode);
        this.changeText();

        this.propertyInputNode.addEvents({
            "change": function(){
                this.changeText();
            }.bind(this),
            "blur": function(){},
        });
    },
    changeText: function(){
        var value = this.propertyInputNode.get("value");
        if (value){
            this.textNode.set("text", this.json.text+"("+value+")");
        }else{
            this.textNode.set("text", this.json.text);
        }
        this.editor.fireEvent("change");
    },
    getData: function(){
        var value = this.propertyInputNode.get("value");
        var key = this.key;
        var script = "return serial.companyAttribute(\""+value+"\")";
        return {
            "key": key,
            "value": value,
            "script": script
        }
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.DepartmentAttribute = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.CompanyAttribute,
    getData: function(){
        var value = this.propertyInputNode.get("value");
        var key = this.key;
        var script = "return serial.departmentAttribute(\""+value+"\")";
        return {
            "key": key,
            "value": value,
            "script": script
        }
    }
});

MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Number = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,
    loadProperty: function(){
        this.propertyNode = new Element("div", {"styles": this.css.itemPropertyNode}).inject(this.editor.propertyNode);

        var lineNode = new Element("div", {"styles": this.css.lineNode}).inject(this.propertyNode);
        var propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(lineNode);
        propertyTitleNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.serialNumberByTitle);
        this.propertyNumberByDivNode = new Element("div", {"styles": this.css.propertyInputDivNode}).inject(lineNode);
        this.loadNumberBy();

        lineNode = new Element("div", {"styles": this.css.lineNode}).inject(this.propertyNode);
        propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(lineNode);
        propertyTitleNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.serialNumberLongTitle);
        this.propertyInputDivNode = new Element("div", {"styles": this.css.propertyInputDivNode}).inject(lineNode);
        this.propertyInputNode = new Element("select").inject(this.propertyInputDivNode);
        var value = (this.data) ? this.data.value: {};
        var numberLong = value.lng || 0;
        var optionsHtml = "<option "+((numberLong==0) ? "selected": "")+" value=\"0\">auto</option>";
        optionsHtml += "<option "+((numberLong==2) ? "selected": "")+" value=\"2\">2</option>";
        optionsHtml += "<option "+((numberLong==3) ? "selected": "")+" value=\"3\">3</option>";
        optionsHtml += "<option "+((numberLong==4) ? "selected": "")+" value=\"4\">4</option>";
        optionsHtml += "<option "+((numberLong==5) ? "selected": "")+" value=\"5\">5</option>";
        optionsHtml += "<option "+((numberLong==6) ? "selected": "")+" value=\"6\">6</option>";
        optionsHtml += "<option "+((numberLong==7) ? "selected": "")+" value=\"7\">7</option>";
        optionsHtml += "<option "+((numberLong==8) ? "selected": "")+" value=\"8\">8</option>";
        optionsHtml += "<option "+((numberLong==9) ? "selected": "")+" value=\"9\">9</option>";
        this.propertyInputNode.set("html", optionsHtml);
        this.propertyInputNode.addEvents({
            "change": function(){
                this.editor.fireEvent("change");
            }.bind(this)
        });

    },
    loadNumberBy: function(){
        this.propertyNumberByDivNode.empty();
        var i = Math.random();

        var value = (this.data) ? this.data.value: {};
        var numberBy = value.by || [];

        var html = "";
        this.editor.selectedItems.each(function(item, n){
            if (item.key!="number"){
                var check = (numberBy.indexOf(n)==-1)? "" : "checked"
                html += "<input "+check+" name=\"serialNumberBySelect"+i+"\" type=\"checkbox\" value=\""+n+"\"/>" + item.json.text;
            }
        });
        this.propertyNumberByDivNode.set("html", html);
        this.propertyNumberByDivNode.getElements("input").addEvent("click", function(e){
            this.editor.fireEvent("change");
        }.bind(this));

    },
    getData: function(){
        var numberLong = this.propertyInputNode.options[this.propertyInputNode.selectedIndex].value;
        var numberBy = [];
        var inputs = this.propertyNumberByDivNode.getElements("input");
        inputs.each(function(input){
            if (input.checked) numberBy.push(input.get("value").toInt());
        }.bind(this));
        var value = {"lng": numberLong, "by": numberBy};
        var code = "return serial.nextSerialNumber("+JSON.encode(numberBy)+", "+numberLong+")"

        return {
            "key": this.key,
            "value": value,
            "script": code
        }
    }

});

MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Script = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,
    loadProperty: function(){
        debugger;
        this.code = (this.data) ? this.data.value: "";
        this.propertyNode = new Element("div", {"styles": this.css.itemPropertyNode}).inject(this.editor.propertyNode);
        this.scriptNode = new Element("div", {"styles": this.css.scriptNode}).inject(this.propertyNode);
        this.scriptNode.set("title", MWF.xApplication.process.ProcessDesigner.LP.serialScriptTitle);

        this.scriptArea = new MWF.xApplication.process.ProcessDesigner.widget.ScriptText(this.scriptNode, (this.data) ? this.data.value: "", this.editor.process.designer, {
            "maskNode": this.editor.process.designer.content,
            "maxObj": this.editor.process.designer.paperNode,
            "onChange": function(code){
                this.code = code;
                this.editor.fireEvent("change");
            }.bind(this)
        });
    },
    getData: function(){
        var value = this.code;
        var key = this.key;
        return {
            "key": key,
            "value": value,
            "script": value
        }
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Unit = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,
    getData: function(){
        var key = this.key;
        var script = "return serial.unit()";
        return {
            "key": key,
            "value": "",
            "script": script
        }
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.Unit = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem,
    getData: function(){
        var key = this.key;
        var script = "return serial.unit()";
        return {
            "key": key,
            "value": "",
            "script": script
        }
    }
});
MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.UnitAttribute = new Class({
    Extends: MWF.xApplication.process.ProcessDesigner.widget.SerialEditor.SelectedItem.CompanyAttribute,
    getData: function(){
        var value = this.propertyInputNode.get("value");
        var key = this.key;
        var script = "return serial.unitAttribute(\""+value+"\")";
        return {
            "key": key,
            "value": value,
            "script": script
        }
    }
});