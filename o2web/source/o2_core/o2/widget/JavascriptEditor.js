o2.widget = o2.widget || {};
o2.require("o2.widget.codemirror", null, false);
o2.require("o2.widget.ace", null, false);
o2.require("o2.widget.monaco", null, false);
o2.require("o2.xDesktop.UserData", null, false);
o2.widget.JavascriptEditor = new Class({
	Implements: [Options, Events],
	options: {
        //"type": "ace",
        "type": "monaco",
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
		this.unbindEvents = [];
		this.editorClass = o2.widget[this.options.type];
		this.node = $(node);
	},
    getDefaultEditorData: function(){
	    switch (this.options.type) {
            case "ace":
                return {
                    "javascriptEditor": {
                        "theme": "tomorrow",
                        "fontSize" : "12px"
                    }
                };
            case "monaco":
                return {
                    "javascriptEditor": {
                        "monaco_theme": "vs",
                        "fontSize" : "12px"
                    }
                };
        }
    },
    getEditorTheme: function(callback){
        if (!o2.editorData){
            o2.UD.getData("editor", function(json){
                if (json.data){
                    o2.editorData = JSON.decode(json.data);
                }else{
                    o2.editorData = this.getDefaultEditorData();
                }
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    load: function(callback){
        this.getEditorTheme(function(json){
            if (this.options.type.toLowerCase()=="ace"){
                this.loadAce(callback);
            }
            if (this.options.type.toLowerCase()=="monaco"){
                this.loadMonaco(callback);
            }
            if (this.options.type.toLowerCase()=="codeMirror"){
                this.loadCodeMirror(callback);
            }

            while (this.unbindEvents.length){
                var ev = this.unbindEvents.shift();
                this.addEditorEvent(ev.name, ev.fun);
            }
        }.bind(this));
    },

    loadMonaco: function(callback){
        if (o2.editorData.javascriptEditor){
            this.theme = o2.editorData.javascriptEditor.monaco_theme;
            this.fontSize = o2.editorData.javascriptEditor.fontSize;
        }else{
            o2.editorData.javascriptEditor = {
                "monaco_theme": "vs",
                "fontSize" : "12px"
            };
        }
        if (!this.theme) this.theme = "vs";
        if( !this.fontSize )this.fontSize = "12px";

        this.editorClass.load(function(){
            this.editor = monaco.editor.create(this.node, {
                value: this.options.option.value,
                language: this.options.option.mode,
                theme: this.theme,
                fontSize: this.fontSize,
                lineNumbersMinChars: 3,
                lineNumbers: (this.options.option.lineNumbers) ? "on" : "off",
                mouseWheelZoom: true,
                automaticLayout: true
            });
            this.focus();

            o2.require("o2.xScript.Macro", function() {
                var json = null;
                o2.getJSON("/o2_core/o2/widget/$JavascriptEditor/environment.json", function (data) {
                    json = data;
                }, false);
                this.Macro = new o2.Macro.FormContext(json);

                //registerReferenceProvider
                //monaco.languages.registerReferenceProvider('javascript', {
                monaco.languages.registerCompletionItemProvider('javascript', {
                    "triggerCharacters": ["."],
                    provideCompletionItems: function (model, position, context, token) {
                        debugger;
                        var textUntilPosition = model.getValueInRange({
                            startLineNumber: position.lineNumber,
                            startColumn: 1,
                            endLineNumber: position.lineNumber,
                            endColumn: position.column
                        });
                        var textPrefix = textUntilPosition.substr(0, textUntilPosition.lastIndexOf("."));
                        code = "try {return "+textPrefix+";}catch(e){return null;}";
                        //code = "try {return this;}catch(e){return null;}";
                        var o = this.Macro.exec(code);

                        var word = model.getWordUntilPosition(position);
                        var range = {
                            startLineNumber: position.lineNumber,
                            endLineNumber: position.lineNumber,
                            startColumn: word.startColumn,
                            endColumn: word.endColumn
                        };

                        if (o) {
                            var arr = [];
                            Object.keys(o).each(function (key) {
                                var type = typeOf(o[key]);
                                if (type === "function") {
                                    var count = o[key].length;
                                    var v = key + "(";
                                    for (var i = 1; i <= count; i++) v += (i == count) ? "par" + i : "par" + i + ", ";
                                    v += ")";
                                    arr.push({
                                        label: key,
                                        kind: monaco.languages.CompletionItemKind.Function,
                                        //documentation: "Fast, unopinionated, minimalist web framework",
                                        insertText: v,
                                        range: range,
                                        detail: type
                                    });
                                } else {
                                    arr.push({
                                        label: key,
                                        kind: monaco.languages.CompletionItemKind.Interface,
                                        //documentation: "Fast, unopinionated, minimalist web framework",
                                        insertText: key,
                                        range: range,
                                        detail: type
                                    });
                                }
                            });
                        }
                        return {suggestions: arr}
                    }.bind(this)
                });

            });

            this.editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_S, function(e){
                this.fireEvent("save");
            }.bind(this));

            this.editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KEY_I, function(e){
                this.format();
            }.bind(this));
            this.editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KEY_F, function(e){
                this.format();
            }.bind(this));

            if( this.fontSize ){
                this.editor.updateOptions( {"fontSize": this.fontSize} );
            }

            this.fireEvent("postLoad");
            if (callback) callback();

        }.bind(this));
    },

    loadAce: function(callback){
        if (o2.editorData.javascriptEditor){
            this.theme = o2.editorData.javascriptEditor.theme;
            this.fontSize = o2.editorData.javascriptEditor.fontSize;
        }else{
            o2.editorData.javascriptEditor = {
                "theme": "tomorrow",
                "fontSize" : "12px"
            };
        }
        if (!this.theme) this.theme = "tomorrow";
        if( !this.fontSize )this.fontSize = "12px";

        this.editorClass.load(function(){
            var exports = ace.require("ace/ext/language_tools");
            this.editor = ace.edit(this.node);
            this.editor.session.setMode("ace/mode/"+this.options.option.mode);
            this.editor.setTheme("ace/theme/"+this.theme);
            this.editor.setOptions({
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableLiveAutocompletion: true,
                lineNumbers: this.options.option.lineNumbers
            });
            if (this.options.option.value) this.editor.setValue(this.options.option.value);

            this.focus();

            //this.editor.focus();
            //this.editor.navigateFileStart();
            //添加自动完成列表
            o2.require("o2.xScript.Macro", function(){
                var json = null;
                o2.getJSON("/o2_core/o2/widget/$JavascriptEditor/environment.json", function(data){ json = data; }, false);
                this.Macro = new o2.Macro.FormContext(json);
                exports.addCompleter({
                    identifierRegexps: [
                        /[a-zA-Z_0-9\$\-\u00A2-\uFFFF\.]/
                    ],
                    getCompletions: function(editor, session, pos, prefix, callback){
                        var x = prefix.substr(0, prefix.lastIndexOf("."));
                        code = "try {return "+x+";}catch(e){return null;}";
                        var o = this.Macro.exec(code);

                        if (o){
                            var arr1 = [];
                            var arr2 = [];
                            Object.keys(o).each(function(key){
                                var type = typeOf(o[key]);
                                if (type==="function") {
                                    var count = o[key].length;
                                    var v = x+"."+key+"(";
                                    for (var i=1; i<=count; i++) v+= (i==count) ? "par"+i :  "par"+i+", ";
                                    v+=");";
                                    arr1.push({
                                        caption: key,
                                        value: v,
                                        score: 3,
                                        meta: type
                                    });
                                }else{
                                    arr2.push({
                                        caption: key,
                                        value: x+"."+key,
                                        score: 3,
                                        meta: type
                                    });
                                }
                            });
                            callback(null, arr1.concat(arr2));
                        }
                    }.bind(this)
                });
            }.bind(this));

            this.editor.commands.addCommand({
                name: 'save',
                bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
                exec: function(editor) {
                    this.fireEvent("save");
                }.bind(this),
                readOnly: false
            });

            this.editor.commands.addCommand({
                name: 'format',
                bindKey: {win: 'Ctrl-Alt-i|Ctrl-Alt-f',  mac: 'Command-i|Command-f'},
                exec: function(editor, e, e1) {
                    this.format();
                }.bind(this),
                readOnly: false
            });

            this.editor.commands.addCommand({
                name: "showKeyboardShortcuts",
                bindKey: {win: "Ctrl-Alt-h", mac: "Command-Alt-h"},
                exec: function(editor) {
                    ace.config.loadModule("ace/ext/keybinding_menu", function(module) {
                        module.init(editor);
                        editor.showKeyboardShortcuts()
                    })
                }.bind(this)
            });

            this.node.addEvent("keydown", function(e){
                e.stopPropagation();
            });

            if( this.fontSize ){
                this.editor.setFontSize( this.fontSize );
            }

            this.fireEvent("postLoad");
            if (callback) callback();
        }.bind(this));
    },

    setValue: function(v){
        if (this.editor) this.editor.setValue(v);
    },
    getValue: function(){
        return (this.editor) ? this.editor.getValue() : "";
    },
    resize: function(y){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.resize(); break;
                case "monaco": this.editor.layout(); break;
            }
        }
    },
    addEditorEvent: function(name, fun){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.on(name, fun); break;
                case "monaco":
                    var ev = name;
                    switch (ev) {
                        case "change": ev = "onDidChangeModelContent"; break;
                        case "blue": ev = "onDidBlurEditorText"; break;

                    }
                    if (this.editor[ev]) this.editor[ev](fun);
                    break;
            }
        }else{
            this.unbindEvents.push({"name": name, "fun": fun});
        }
    },
    validatedAce: function(){
        var session = this.editor.getSession();
        var annotations = session.getAnnotations();
        for (var i=0; i<annotations.length; i++){
            if (annotations[i].type=="error") return false;
        }
        return true;
    },
    validatedMonaco: function(){
        var mod = this.editor.getModel();
        var ms = monaco.editor.getModelMarkers({"resource": mod.uri});
        for (var i=0; i<ms.length; i++){
            if (ms[i].severity==8) return false;
        }
        return true;
    },

    validated: function(){
        if (this.editor){
           switch (this.options.type.toLowerCase()) {
               case "ace": return this.validatedAce();
               case "monaco": return this.validatedMonaco();
           }
            return true;
        }
        return true
    },

    formatAce: function(){
        var mode = this.options.option.mode.toString().toLowerCase();
        if (mode==="javascript"){
            o2.load("JSBeautifier", function(){
                this.editor.setValue(js_beautify(editor.getValue()));
            }.bind(this));
        }else if (mode==="html"){
            o2.load("JSBeautifier_html", function(){
                this.editor.setValue(html_beautify(editor.getValue()));
            }.bind(this));
        }else if (mode==="css"){
            o2.load("JSBeautifier_css", function(){
                this.editor.setValue(css_beautify(editor.getValue()));
            }.bind(this));
        }else{
            o2.load("JSBeautifier", function(){
                this.editor.setValue(js_beautify(editor.getValue()));
            }.bind(this));
        }
    },
    formatMonaco: function(){
        this.editor.getAction("editor.action.formatDocument").run();
    },
    format: function(){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.formatAce();
                case "monaco": this.formatMonaco();
            }
        }
    },

    focus: function(){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.focus(); this.goto(); break;
                case "monaco": this.editor.focus();
            }
        }
    },
    goto: function(){
        var p = this.editor.getCursorPosition();
        if (p.row==0){
            p.row = this.editor.renderer.getScrollBottomRow();
        }
        this.editor.gotoLine(p.row+1, p.column+1, true);
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