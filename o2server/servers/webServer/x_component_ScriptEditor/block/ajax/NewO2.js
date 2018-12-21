MWF.xApplication.ScriptEditor.block.ajax.NewO2 = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around,

    createSelectNode: function(content){
        var select = new Element("select", {"styles": this.blockCss.selectNode, "class": "script"});

        var list = this.editor.app.desktop.serviceAddressList;
        Object.each(list, function(v, k){
            var value = JSON.encode(v);
            new Element("option", {"value": value, "text": v.name}).inject(select);
        }.bind(this));


        // content.options.each(function(t){
        //     var v = t.split("|");
        //     new Element("option", {"value": v[1], "text": v[0]}).inject(select);
        // }.bind(this));
        select.setStyle("background", this.type.data.mortiseColor);
        return select;
    },
});