o2.widget = o2.widget || {};
o2.require("o2.widget.codemirror", null, false);
o2.require("o2.widget.ace", null, false);
o2.require("o2.xDesktop.UserData", null, false);
o2.widget.HtmlEditor = new Class({
	Implements: [Options, Events],
	options: {
        "type": "ace",
		"title": "JavascriptEditor",
		"style": "default",
		"option": {
			value: "",
			mode: "javascript",
			"lineNumbers": true
		}
	},
	initialize: function(node, options){
		this.setOptions(options);
		this.editorClass = o2.widget[this.options.type];
		this.node = $(node);
	},
    getEditorTheme: function(callback){
        if (!o2.editorData){
            o2.UD.getData("editor", function(json){
                if (json.data){
                    o2.editorData = JSON.decode(json.data);
                }else{
                    o2.editorData = {
                        "javascriptEditor": {
                            "theme": "tomorrow"
                        }
                    };
                }
                if (callback) callback();
            });
        }else{
            if (callback) callback();
        }
    },
    load: function(callback){
        this.getEditorTheme(function(json){
            if (o2.editorData.javascriptEditor){
                this.theme = o2.editorData.javascriptEditor.theme;
            }else{
                o2.editorData.javascriptEditor = {"theme": "tomorrow"};
            }
            if (!this.theme) this.theme = "tomorrow";
            if (this.options.type.toLowerCase()=="ace"){
                this.loadAce(callback);
            }
            if (this.options.type.toLowerCase()=="codeMirror"){
                this.loadCodeMirror(callback);
            }
        }.bind(this));
    },
    loadAce: function(callback){
        this.editorClass.load(function(){
            ace.require("ace/ext/language_tools");
            this.editor = ace.edit(this.node);
            this.editor.session.setMode("ace/mode/html");
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
    },

	loadCodeMirror: function(callback){
		if (this.fireEvent("queryLoad")){
			this.editorClass.load(function(){
				this.editorClass.loadJavascript(function(editor){
					this.options.option.mode = "javascript";
					this.editor = CodeMirror(this.node, this.options.option);

					this.editor.setSize("100%", "100%");
					this.fireEvent("postLoad");

                    if (callback) callback();
				}.bind(this));
			}.bind(this));
		}
	},
    showLineNumbers: function(){
        if (this.options.type.toLowerCase()=="codeMirror") this.editor.setOption("lineNumbers", true);
    },
    max: function(){
        if (this.options.type.toLowerCase()=="codeMirror") this.editor.setSize("100%", "100%");
    },

    getCursorPixelPosition: function(){
        var session = this.editor.getSession();
        var pos = this.editor.getCursorPosition();
        var line = session.getLine(pos.row);
        var prefix = this.retrievePrecedingIdentifier(line, pos.column);

        var base = session.doc.createAnchor(pos.row, pos.column - prefix.length);
        base.$insertRight = true;

        var renderer = this.editor.renderer;
        var pos = renderer.$cursorLayer.getPixelPosition(base, true);

        var rect = this.editor.container.getBoundingClientRect();
        pos.top += rect.top - renderer.layerConfig.offset;
        pos.left += rect.left - this.editor.renderer.scrollLeft;
        pos.left += renderer.gutterWidth;

        return pos;
    },
    retrievePrecedingIdentifier: function(text, pos, regex) {
        regex = regex || /[a-zA-Z_0-9\$\-\u00A2-\uFFFF]/;
        var buf = [];
        for (var i = pos-1; i >= 0; i--) {
            if (regex.test(text[i]))
                buf.push(text[i]);
            else
                break;
        }
        return buf.reverse().join("");
    }


});