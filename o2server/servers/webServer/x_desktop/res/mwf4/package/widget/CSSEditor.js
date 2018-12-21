MWF.widget = MWF.widget || {};
MWF.require("MWF.widget.JavascriptEditor", null, false);
MWF.widget.CSSEditor = new Class({
    Extends: MWF.widget.JavascriptEditor,
	Implements: [Options, Events],
	options: {
        "type": "ace",
		"title": "CSSEditor",
		"style": "default",
		"option": {
			value: "",
			mode: "javascript",
			"lineNumbers": true
		}
	},
    loadAce: function(callback){
        this.editorClass.load(function(){
            ace.require("ace/ext/language_tools");
            this.editor = ace.edit(this.node);
            this.editor.session.setMode("ace/mode/css");
            this.editor.setTheme("ace/theme/"+this.theme);
            this.editor.setOptions({
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableLiveAutocompletion: false
            });
            if (this.options.option.value) this.editor.setValue(this.options.option.value);
            //this.editor.focus();
            //this.editor.navigateFileStart();

            this.editor.commands.addCommand({
                name: 'save',
                bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
                exec: function(editor) {
                    this.fireEvent("save");
                }.bind(this),
                readOnly: false // false if this command should not apply in readOnly mode
            });
            this.editor.commands.addCommand({
                name: 'help',
                bindKey: {win: 'Ctrl-Q|Ctrl-Alt-Space|Ctrl-Space|Alt-/',  mac: 'Command-Q'},
                exec: function(editor, e, e1) {
                    this.fireEvent("reference", [editor, e, e1]);
                }.bind(this),
                readOnly: false // false if this command should not apply in readOnly mode
            });

            this.node.addEvent("keydown", function(e){
                e.stopPropagation();
            });

            this.fireEvent("postLoad");
            if (callback) callback();
        }.bind(this));
    }
});