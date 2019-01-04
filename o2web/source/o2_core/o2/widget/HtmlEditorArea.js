o2.widget = o2.widget || {};
o2.require("o2.widget.HtmlEditor", null, false);
o2.widget.HtmlEditorArea = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.ScriptArea,

    loadEditor: function(content){
        var value=(content) ? content.code : "";
        value = (value) ? value : "";
        this.jsEditor = new o2.widget.HtmlEditor(this.contentNode,{
            "option": {
                "value": value,
                "lineNumbers": false
            },
            "onPostLoad": function(){
                this.editor = this.jsEditor.editor;
                this.editor.on("change", function() {
                    this.fireEvent("change");
                }.bind(this));
                this.fireEvent("postLoad");
            }.bind(this),
            "onSave": function(){
                this.fireEvent("change");
                this.fireEvent("save");
            }.bind(this)
        });
        this.jsEditor.load();
        //this.createScriptReferenceMenu();
        //
        //
        //this.jsEditor.addEvent("reference", function(editor, e, e1){
        //    if (!this.scriptReferenceMenu){
        //        this.createScriptReferenceMenu(this.showReferenceMenu.bind(this));
        //    }else{
        //        this.showReferenceMenu();
        //    }
        //}.bind(this));
    },
    getValue: function(){
        return (this.editor) ? this.editor.getValue() : this.contentCode;
    },
});




