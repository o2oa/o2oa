MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.process.FormDesigner.widget.ScriptIncluder = new Class({
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
		
		this.path = "../x_component_process_FormDesigner/widget/$ScriptIncluder/";
		this.cssPath = "../x_component_process_FormDesigner/widget/$ScriptIncluder/"+this.options.style+"/css.wcss";
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
        var html = "<table width='100%' border='0' cellpadding='5' cellspacing='0' class='editTable'>" +
            "<tr><td></td><td></td></tr><tr><td></td><td></td></tr></table>";
        this.editorNode.set("html", html);
        var tds = this.editorNode.getElements("td").setStyles(this.css.editTableTdValue);

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
        var item = new MWF.xApplication.process.FormDesigner.widget.ScriptIncluder.Item(data, this);
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
                    var item = new MWF.xApplication.process.FormDesigner.widget.ScriptIncluder.Item(itemData, this);
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
MWF.xApplication.process.FormDesigner.widget.ScriptIncluder.Item = new Class({
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