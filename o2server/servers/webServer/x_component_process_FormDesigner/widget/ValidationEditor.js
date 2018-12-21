MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.process.FormDesigner.widget.ValidationEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
		"maxObj": document.body
	},
	initialize: function(node, designer, options){
		this.setOptions(options);
		this.node = $(node);
        this.designer = designer;
		
		this.path = "/x_component_process_FormDesigner/widget/$ValidationEditor/";
		this.cssPath = "/x_component_process_FormDesigner/widget/$ValidationEditor/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.items = [];
	},
    load: function(data){
        this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.designer.lp.validation.validation}).inject(this.node);
		this.editorNode = new Element("div", {"styles": this.css.editorNode}).inject(this.node);
        this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.node);
        this.listNode = new Element("div", {"styles": this.css.listNode}).inject(this.node);
        this.loadEditorNode();
        this.loadActionNode();
        this.loadListNode(data);
	},
    loadEditorNode: function(){
        //this.statusNode = new Element("div", {"styles": this.css.statusNode}).inject(this.editorNode);
        //this.conditionNode = new Element("div", {"styles": this.css.conditionNode}).inject(this.editorNode);
        //this.valueNode = new Element("div", {"styles": this.css.valueNode}).inject(this.editorNode);
        //this.promptNode = new Element("div", {"styles": this.css.promptNode}).inject(this.editorNode);

        var html = "<table width='100%' border='0' cellpadding='5' cellspacing='0' class='editTable'>" +
            "<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr></table>";
        this.editorNode.set("html", html);
        var tds = this.editorNode.getElements("td").setStyles(this.css.editTableTdValue);
        this.loadStatus(tds);
        this.loadConditions(tds);
        this.loadPrompt(tds);

    },
    loadStatus: function(tds){
        var html = "<table width='100%' border='0' cellpadding='0' cellspacing='0'><tr>" +
        "<td width='140px'>"+"<input type='radio' value='all' checked />"+this.designer.lp.validation.anytime+
        "<input type='radio' value='decision' />"+this.designer.lp.validation.decision+"</td>" +
        "<td><input type='text' value='"+this.designer.lp.validation.decisionName+"'></td>" +
        "</tr></table>";

        tds[0].set("html", html);
        var inputs = tds[0].getElements("input");
        var randomId = new MWF.widget.UUID().toString();
        inputs[0].set("name", "condition"+randomId);
        inputs[1].set("name", "condition"+randomId);
        inputs[2].setStyles(this.css.decisionNameInput);

        this.decisionInputNode = inputs[2];
        this.statusRadioNodes = inputs;
        this.statusRadioNodes.pop();

        this.decisionInputNode.addEvents({
            "focus": function(){
                if (this.decisionInputNode.get("value")==this.designer.lp.validation.decisionName) this.decisionInputNode.set("value", "");
            }.bind(this),
            "blur": function(){
                if (!this.decisionInputNode.get("value")) this.decisionInputNode.set("value", this.designer.lp.validation.decisionName);
            }.bind(this)
        });
    },
    loadConditions: function(tds){
        var html = "<table width='100%' border='0' cellpadding='0' cellspacing='0'><tr>" +
            "<td width='140px'>"+"<select><option value='value'>"+this.designer.lp.validation.value+"</option>" +
            "<option value='length'>"+this.designer.lp.validation.length+"</option></select>"+
            "&nbsp;<select><option value='isnull'>"+this.designer.lp.validation.isnull+"</option>" +
            "<option value='notnull'>"+this.designer.lp.validation.notnull+"</option>" +
            "<option value='gt'>"+this.designer.lp.validation.gt+"</option>" +
            "<option value='lt'>"+this.designer.lp.validation.lt+"</option>" +
            "<option value='equal'>"+this.designer.lp.validation.equal+"</option>" +
            "<option value='neq'>"+this.designer.lp.validation.neq+"</option>" +
            "<option value='contain'>"+this.designer.lp.validation.contain+"</option>" +
            "<option value='notcontain'>"+this.designer.lp.validation.notcontain+"</option>" +
            "</select>"+"</td>" +
            "<td><input style='display: none' type='text' value='"+this.designer.lp.validation.valueInput+"'/></td>" +
            "</tr></table>";

        tds[1].set("html", html);
        var selects = tds[1].getElements("select");
        selects.setStyles(this.css.valueSelect);
        this.valueTypeSelectNode = selects[0];
        this.operateorSelectNode = selects[1];
        this.valueInputNode = tds[1].getElement("input").setStyles(this.css.valueInput);
        this.operateorSelectNode.addEvent("change", function(){
            var v = this.operateorSelectNode.options[this.operateorSelectNode.selectedIndex].value;
            if (v && (v!="isnull") && (v!="notnull")){
                this.valueInputNode.setStyle("display", "inline");
            }else{
                this.valueInputNode.setStyle("display", "none");
            }
        }.bind(this));

        this.valueInputNode.addEvents({
            "focus": function(){
                if (this.valueInputNode.get("value")==this.designer.lp.validation.valueInput) this.valueInputNode.set("value", "");
            }.bind(this),
            "blur": function(){
                if (!this.valueInputNode.get("value")) this.valueInputNode.set("value", this.designer.lp.validation.valueInput);
            }.bind(this)
        });
    },
    loadPrompt: function(tds){
        var html = "<table width='100%' border='0' cellpadding='0' cellspacing='0'><tr><td width='60px'>"+this.designer.lp.validation.prompt+"</td><td><input type='text' /></td></tr></table>";
        tds[2].set("html", html);
        tds[2].getElements("td")[0].setStyles(this.css.titleTd);
        this.promptInputNode = tds[2].getElement("input").setStyles(this.css.promptInput);
    },
    loadActionNode: function(){
        this.actionAreaNode = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.actionNode);
        this.addAction = new Element("div", {"styles": this.css.addAction, "text": this.designer.lp.validation.add}).inject(this.actionAreaNode);
        this.modifyAction = new Element("div", {"styles": this.css.modifyAction_disabled, "text": this.designer.lp.validation.modify}).inject(this.actionAreaNode);

        this.addAction.addEvent("click", function(){
            this.addValidation();
        }.bind(this));
        this.modifyAction.addEvent("click", function(){
            this.modifyValidation();
        }.bind(this));
    },
    getData: function(){
        var status = this.getStatusValue();
        var decision = this.decisionInputNode.get("value");
        var valueType = this.valueTypeSelectNode.options[this.valueTypeSelectNode.selectedIndex].value;
        var operateor = this.operateorSelectNode.options[this.operateorSelectNode.selectedIndex].value;
        var value = this.valueInputNode.get("value");
        var prompt = this.promptInputNode.get("value");
        if (decision == this.designer.lp.validation.decisionName) decision = "";
        if (value == this.designer.lp.validation.valueInput) value = "";

        return {
            "status": status,
            "decision": decision,
            "valueType": valueType,
            "operateor": operateor,
            "value": value,
            "prompt": prompt
        };
    },
    addValidation: function(){
        this.hideErrorNode();
        var data = this.getData();

        if (data.status!="all"){
            if (!data.decision || data.decision==this.designer.lp.validation.decisionName){
                this.showErrorNode(this.designer.lp.validation.inputDecisionName);
                return false;
            }
        }
        if (data.operateor!="isnull" && data.operateor!="notnull"){
            if (!data.value || data.value==this.designer.lp.validation.valueInput){
                this.showErrorNode(this.designer.lp.validation.inputValue);
                return false;
            }
        }
        if (!data.prompt){
            this.showErrorNode(this.designer.lp.validation.inputPrompt);
            return false;
        }
        var item = new MWF.xApplication.process.FormDesigner.widget.ValidationEditor.Item(data, this);
        this.items.push(item);
        item.selected();
        this.fireEvent("change");
    },
    showErrorNode: function(text){
        this.errorNode = new Element("div", {"styles": this.css.errorNode}).inject(this.actionNode, "before");
        this.errorTextNode = new Element("div", {"styles": this.css.errorTextNode}).inject(this.errorNode);
        this.errorTextNode.set("text", text);
        this.errorNode.addEvent("click", function(){this.hideErrorNode();}.bind(this));
    },
    hideErrorNode: function(){
        if (this.errorNode) this.errorNode.destroy();
    },
    getStatusValue: function(){
        for (var i=0; i<this.statusRadioNodes.length; i++){
            var item = this.statusRadioNodes[i];
            if (item.checked) return item.value;
        }
        return "";
    },
    modifyValidation: function(){
        if (this.currentItem){
            this.hideErrorNode();

            var data = this.getData();

            if (data.status!="all"){
                if (!data.decision || data.decision==this.designer.lp.validation.decisionName){
                    this.showErrorNode(this.designer.lp.validation.inputDecisionName);
                    return false;
                }
            }
            if (data.operateor!="isnull" && data.operateor!="notnull"){
                if (!data.value || data.value==this.designer.lp.validation.valueInput){
                    this.showErrorNode(this.designer.lp.validation.inputValue);
                    return false;
                }
            }
            if (!data.prompt){
                this.showErrorNode(this.designer.lp.validation.inputPrompt);
                return false;
            }

            this.currentItem.reload(data);
            this.currentItem.unSelected();
            this.disabledModify();
            this.fireEvent("change");
        }
    },
    loadListNode: function(data){
        if (data){
            if (data.length){
                data.each(function(itemData){
                    var item = new MWF.xApplication.process.FormDesigner.widget.ValidationEditor.Item(itemData, this);
                    this.items.push(item);
                }.bind(this));
            }
        }
    },
    enabledModify: function(){
        this.modifyAction.setStyles(this.css.modifyAction);
    },
    disabledModify: function(){
        this.modifyAction.setStyles(this.css.modifyAction_disabled);
    },
    setData: function(data){
        if (data.decision) this.decisionInputNode.set("value", data.decision);
        if (data.status){
            for (var i=0; i<this.statusRadioNodes.length; i++){
                if (data.status == this.statusRadioNodes[i].get("value")){
                    this.statusRadioNodes[i].set("checked", true);
                    break;
                }
            }
        }else{
            this.statusRadioNodes[0].set("checked", true);
        }
        for (var i=0; i<this.valueTypeSelectNode.options.length; i++){
            if (data.valueType == this.valueTypeSelectNode.options[i].get("value")){
                this.valueTypeSelectNode.options[i].set("selected", true);
                break;
            }
        }
        for (var i=0; i<this.operateorSelectNode.options.length; i++){
            if (data.operateor == this.operateorSelectNode.options[i].get("value")){
                this.operateorSelectNode.options[i].set("selected", true);
                break;
            }
        }
        if (data.value) this.valueInputNode.set("value", data.value);
        if (data.prompt) this.promptInputNode.set("value", data.prompt);
    },

    deleteItem: function(item){
        if (this.currentItem == item) item.unSelected();
        this.items.erase(item);
        item.node.destroy();
        MWF.release(item);
        this.fireEvent("change");
    },
    getValidationData: function(){
        var data = [];
        this.items.each(function(item){
            data.push(item.data);
        });
        return data;
    }

});
MWF.xApplication.process.FormDesigner.widget.ValidationEditor.Item = new Class({
    initialize: function(data, editor){
        this.data = data;
        this.editor = editor;
        this.container = this.editor.listNode;
        this.css = this.editor.css;
        this.lp = this.editor.designer.lp;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.itemNode}).inject(this.container);
        this.deleteNode = new Element("div", {"styles": this.css.itemDeleteNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.itemContentNode}).inject(this.node);
        this.contentNode.set("text", this.getText());

        this.contentNode.addEvent("click", function(){
            this.selected();
        }.bind(this));

        this.deleteNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));
    },
    reload: function(data){
        this.data = data;
        this.contentNode.set("text", this.getText());
    },
    getText: function(){
        var text = "";
        if (this.data.status=="all"){
            text = this.lp.validation.anytime+" ";
        }else{
            text = this.lp.validation.when+this.lp.validation.decision+" \""+this.data.decision+"\" "+this.lp.validation.as+" ";
        }
        text += this.lp.validation[this.data.valueType]+" ";
        text += this.lp.validation[this.data.operateor]+" ";
        text += " \""+this.data.value+"\" ";

        text += this.lp.validation.prompt+": \""+this.data.prompt+"\"";
        return text;
    },
    selected: function(){
        if (this.editor.currentItem) this.editor.currentItem.unSelected();
        this.node.setStyles(this.css.itemNode_current);
        this.editor.currentItem = this;
        this.editor.setData(this.data);
        this.editor.enabledModify();
    },
    unSelected: function(){
        this.node.setStyles(this.css.itemNode);
        this.editor.currentItem = this;
        //this.editor.modifyValidation();
        this.editor.disabledModify();
    },
    deleteItem: function(e){
        var _self = this;
        this.editor.designer.confirm("warn", e, this.lp.validation.delete_title, this.lp.validation.delete_text, 300, 120, function(){
            _self.destroy();
            this.close();
        }, function(){
            this.close();
        });
    },
    destroy: function(){
        this.editor.deleteItem(this);
    }

});