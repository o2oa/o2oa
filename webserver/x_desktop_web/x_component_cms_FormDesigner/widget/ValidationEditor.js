MWF.xApplication.cms.FormDesigner.widget = MWF.xApplication.cms.FormDesigner.widget || {};
MWF.xDesktop.requireApp("process.FormDesigner", "widget.ValidationEditor", null, false);
MWF.xApplication.cms.FormDesigner.widget.ValidationEditor = new Class({
    Extends: MWF.xApplication.process.FormDesigner.widget.ValidationEditor,


    loadStatus: function(tds){
        var html = "<table width='100%' border='0' cellpadding='0' cellspacing='0'><tr>" +
        "<td width='140px'>"+"<input type='radio' value='all' checked />"+this.designer.lp.validation.anytime+
        "<input type='radio' value='publish' />"+this.designer.lp.validation.publish+"</td>" +
        //"<td><input type='text' value='"+this.designer.lp.validation.decisionName+"'></td>" +
        "</tr></table>";

        tds[0].set("html", html);
        var inputs = tds[0].getElements("input");
        var randomId = new MWF.widget.UUID().toString();
        inputs[0].set("name", "condition"+randomId);
        inputs[1].set("name", "condition"+randomId);
        //inputs[2].setStyles(this.css.decisionNameInput);

        //this.decisionInputNode = inputs[2];
        this.statusRadioNodes = inputs;
        //this.statusRadioNodes.pop();

        //this.decisionInputNode.addEvents({
        //    "focus": function(){
        //        if (this.decisionInputNode.get("value")==this.designer.lp.validation.decisionName) this.decisionInputNode.set("value", "");
        //    }.bind(this),
        //    "blur": function(){
        //        if (!this.decisionInputNode.get("value")) this.decisionInputNode.set("value", this.designer.lp.validation.decisionName);
        //    }.bind(this)
        //});
    },
    getData: function(){
        var status = this.getStatusValue();
        //var decision = this.decisionInputNode.get("value");
        var valueType = this.valueTypeSelectNode.options[this.valueTypeSelectNode.selectedIndex].value;
        var operateor = this.operateorSelectNode.options[this.operateorSelectNode.selectedIndex].value;
        var value = this.valueInputNode.get("value");
        var prompt = this.promptInputNode.get("value");
        //if (decision == this.designer.lp.validation.decisionName) decision = "";
        if (value == this.designer.lp.validation.valueInput) value = "";

        return {
            "status": status,
            //"decision": decision,
            "valueType": valueType,
            "operateor": operateor,
            "value": value,
            "prompt": prompt
        };
    },
    addValidation: function(){
        this.hideErrorNode();
        var data = this.getData();

        //if (data.status!="all"){
        //    if (!data.decision || data.decision==this.designer.lp.validation.decisionName){
        //        this.showErrorNode(this.designer.lp.validation.inputDecisionName);
        //        return false;
        //    }
        //}
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
        var item = new MWF.xApplication.cms.FormDesigner.widget.ValidationEditor.Item(data, this);
        this.items.push(item);
        item.selected();
        this.fireEvent("change");
    },
    modifyValidation: function(){
        if (this.currentItem){
            this.hideErrorNode();

            var data = this.getData();

            //if (data.status!="all"){
            //    if (!data.decision || data.decision==this.designer.lp.validation.decisionName){
            //        this.showErrorNode(this.designer.lp.validation.inputDecisionName);
            //        return false;
            //    }
            //}
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
                    var item = new MWF.xApplication.cms.FormDesigner.widget.ValidationEditor.Item(itemData, this);
                    this.items.push(item);
                }.bind(this));
            }
        }
    },

    setData: function(data){
        //if (data.decision) this.decisionInputNode.set("value", data.decision);
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
    }

});
MWF.xApplication.cms.FormDesigner.widget.ValidationEditor.Item = new Class({
    Extends : MWF.xApplication.process.FormDesigner.widget.ValidationEditor.Item,
    getText: function(){
        var text = "";
        if (this.data.status=="all"){
            text = this.lp.validation.anytime+" ";
        }else{
            text = this.lp.validation.publish ;
        }
        text += this.lp.validation[this.data.valueType]+" ";
        text += this.lp.validation[this.data.operateor]+" ";
        text += " \""+this.data.value+"\" ";

        text += this.lp.validation.prompt+": \""+this.data.prompt+"\"";
        return text;
    }
});