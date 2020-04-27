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
	    debugger;
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
                language: "javascript",
                theme: this.theme,
                fontSize: this.fontSize,
                lineNumbersMinChars: 3,
                mouseWheelZoom: true,
                automaticLayout: true
            });
            this.focus();

            this.editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_S, function(e){
                this.fireEvent("save");
            }.bind(this));

            this.fireEvent("postLoad");
            if (callback) callback();

        }.bind(this));
    },

    setValue: function(v){
        //if (this.options.type.toLowerCase()=="ace"){
        if (this.editor) this.editor.setValue(v);
        //}
        // if (this.options.type.toLowerCase()=="monaco"){
        //     setValue
        //
        //     setTimeout(function() {
        //         this.editor.updateOptions({
        //             "value": v
        //         });
        //     }, 1000);
        // }
    },
    getValue: function(){
        return (this.editor) ? this.editor.getValue() : "";
    },
    resize: function(y){
        if (this.editor){
            if (this.options.type.toLowerCase()=="ace"){
                this.editor.resize();
            }
            if (this.options.type.toLowerCase()=="monaco"){
                this.editor.layout();
            }
        }
    },
    addEditorEvent: function(name, fun){
        if (this.editor){
            if (this.options.type.toLowerCase()=="ace"){
                this.editor.on(name, fun);
            }
            if (this.options.type.toLowerCase()=="monaco"){
                var ev = name;
                switch (ev) {
                    case "change": ev = "onDidChangeModelContent";
                }
                if (this.editor[ev]) this.editor[ev](fun);
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

    focus: function(){
        if (this.editor){
            this.editor.focus();
            if (this.options.type.toLowerCase()=="ace") this.goto();
        }
    },
    goto: function(){
        var p = this.editor.getCursorPosition();
        if (p.row==0){
            p.row = this.editor.renderer.getScrollBottomRow();
        }
        this.editor.gotoLine(p.row+1, p.column+1, true);
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
                                        meta: "function O2"
                                    });
                                }else{
                                    arr2.push({
                                        caption: key,
                                        value: x+"."+key,
                                        score: 3,
                                        meta: (type!="null") ? typeOf(o[key])+" O2" : "O2"
                                    });
                                }
                            });
                            callback(null, arr1.concat(arr2));
                        }
                    }.bind(this)
                });
            }.bind(this));
            // this.editor.on("change", function(e){
            //     if (e.start.row!==e.end.row){
            //         debugger;
            //         var code = "";
            //         for (var i=e.start.row; i<e.end.row; i++){
            //             code+=this.editor.getSession().getLine(i);
            //         }
            //         code = "try{"+code+"}catch(e){}";
            //         this.Macro.exec(code);
            //     }
            // }.bind(this));


            this.editor.commands.addCommand({
                name: 'save',
                bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
                exec: function(editor) {
                    this.fireEvent("save");
                }.bind(this),
                readOnly: false // false if this command should not apply in readOnly mode
            });
            // this.editor.commands.addCommand({
            //     name: 'help',
            //     bindKey: {win: 'Ctrl-Q|Ctrl-Alt-Space|Ctrl-Space|Alt-/',  mac: 'Command-Q'},
            //     exec: function(editor, e, e1) {
            //         this.fireEvent("reference", [editor, e, e1]);
            //     }.bind(this),
            //     readOnly: false // false if this command should not apply in readOnly mode
            // });

            this.editor.commands.addCommand({
                name: 'format',
                bindKey: {win: 'Ctrl-Alt-i|Ctrl-Alt-f',  mac: 'Command-i|Command-f'},
                exec: function(editor, e, e1) {
                    var mode = this.options.option.mode.toString().toLowerCase();
                    if (mode==="javascript"){
                        o2.load("JSBeautifier", function(){
                            editor.setValue(js_beautify(editor.getValue()));
                        }.bind(this));
                    }else if (mode==="html"){
                        o2.load("JSBeautifier_html", function(){
                            editor.setValue(html_beautify(editor.getValue()));
                        }.bind(this));
                    }else if (mode==="css"){
                        o2.load("JSBeautifier_css", function(){
                            editor.setValue(css_beautify(editor.getValue()));
                        }.bind(this));
                    }else{
                        o2.load("JSBeautifier", function(){
                            editor.setValue(js_beautify(editor.getValue()));
                        }.bind(this));
                    }


                    //this.fireEvent("reference", [editor, e, e1]);
                }.bind(this),
                readOnly: false // false if this command should not apply in readOnly mode
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