o2.widget = o2.widget || {};
o2.require("o2.widget.JavascriptEditor", null, false);
o2.widget.CssEditor = new Class({
	Implements: [Options, Events],
    Extends: o2.widget.JavascriptEditor,
	options: {
        "type": "ace",
		"title": "CssEditor",
		"style": "default",
		"option": {
			value: "",
			mode: "css",
			"lineNumbers": true
		}
	},
	initialize: function(node, options){
		this.setOptions(options);
		this.editorClass = o2.widget[this.options.type];
		this.node = $(node);
	},
    loadAce: function(callback){
        this.editorClass.load(function(){
            var exports = ace.require("ace/ext/language_tools");
            this.editor = ace.edit(this.node);
            this.editor.session.setMode("ace/mode/css");
            this.editor.setTheme("ace/theme/"+this.theme);
            this.editor.setOptions({
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableLiveAutocompletion: true
            });
            if (this.options.option.value) this.editor.setValue(this.options.option.value);

            this.focus();

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

            this.editor.commands.addCommand({
                name: 'format',
                bindKey: {win: 'Ctrl-Alt-i|Ctrl-Alt-f',  mac: 'Command-i|Command-f'},
                exec: function(editor, e, e1) {

                    o2.load("JSBeautifier_css", function(){
                        editor.setValue(css_beautify(editor.getValue()));
                    }.bind(this));

                    //this.fireEvent("reference", [editor, e, e1]);
                }.bind(this),
                readOnly: false // false if this command should not apply in readOnly mode
            });

            this.node.addEvent("keydown", function(e){
                e.stopPropagation();
            });

            this.fireEvent("postLoad");
            if (callback) callback();
        }.bind(this));
    },

	loadCodeMirror: function(callback){
		if (this.fireEvent("queryLoad")){
			this.editorClass.load(function(){
				this.editorClass.loadJavascript(function(editor){
					this.options.option.mode = "css";
					this.editor = CodeMirror(this.node, this.options.option);

					this.editor.setSize("100%", "100%");
					this.fireEvent("postLoad");

                    if (callback) callback();
				}.bind(this));
			}.bind(this));
		}
	}
});