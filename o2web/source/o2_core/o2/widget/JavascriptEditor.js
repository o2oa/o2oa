o2.widget = o2.widget || {};
//o2.require("o2.widget.codemirror", null, false);
o2.require("o2.xDesktop.UserData", null, false);
o2.widget.JavascriptEditor = new Class({
	Implements: [Options, Events],
	options: {
        //"type": "ace",
        "type": "monaco",
        "forceType": null,
		"title": "JavascriptEditor",
		"style": "default",
		"option": {
			value: "",
			mode: "javascript",
			"lineNumbers": true
		},
		"runtime": "all"
	},
	initialize: function(node, options){
		this.setOptions(options);
		this.unbindEvents = [];
		this.node = $(node);
		this.id = o2.uuid();
		if (!o2.JSEditorCWE.isInit) o2.JSEditorCWE.init();
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
            this.options.type = this.options.forceType || o2.editorData.javascriptEditor.editor || "monaco";
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

    loadMonacoEditor: function(callback){
        if (!window.monaco){
            o2.load("monaco", {"sequence": true}, function(){
                require.config({ paths: { "vs": "../o2_lib/vs" }});
                require(["vs/editor/editor.main"], function() {
                    if (callback) callback();
                });
            }.bind(this));
        }else{
            if (callback) callback();
        }
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


        o2.require("o2.widget.monaco", function () {
            this.editorClass = o2.widget.monaco;

            this.editorClass.load(function(){

                this.editor = monaco.editor.create(this.node, {
                    value: this.options.option.value,
                    language: this.options.option.mode,
                    theme: this.theme,
                    fontSize: this.fontSize,
                    lineNumbersMinChars: 3,
                    lineNumbers: this.options.option.lineNumbers ? "on" : "off",
                    mouseWheelZoom: true,
                    automaticLayout: true
                });
                this.focus();

                this.editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_S, function(e){
                    this.fireEvent("save");
                }.bind(this));

                this.editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KEY_I, function(e){
                    this.format();
                }.bind(this));
                this.editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KEY_F, function(e){
                    this.format();
                }.bind(this));

                this.editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Alt | monaco.KeyCode.KEY_F, function(e){
                    this.format();
                }.bind(this));

                // this.editor.onKeyDown(function(e){
                //     debugger;
                //     e.preventDefault();
                // });

                if( this.fontSize ){
                    this.editor.updateOptions( {"fontSize": this.fontSize} );
                }

                this.editor.onDidFocusEditorText(function(e){
                    o2.shortcut.keyboard.deactivate();
                }.bind(this));
                this.editor.onDidBlurEditorText(function(e){
                    o2.shortcut.keyboard.activate();
                    this.fireEvent("blur");
                }.bind(this));
                this.editor.onDidChangeModelContent(function(e){
                    this.fireEvent("change");
                }.bind(this))

                //o2.widget.JavascriptEditor.getCompletionEnvironment(this.options.runtime, function(){
                    this.monacoModel = this.editor.getModel();
                    this.monacoModel.o2Editor = this;
                    this.registerCompletion();
                //}.bind(this));

                this.fireEvent("postLoad");
                if (callback) callback();

            }.bind(this));
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

        o2.require("o2.widget.ace", function(){
            this.editorClass = o2.widget.ace;

            this.editorClass.load(function(){
                this.editor = ace.edit(this.node);
                this.editor.session.setMode("ace/mode/"+this.options.option.mode);
                this.editor.setTheme("ace/theme/"+this.theme);
                this.editor.setOptions({
                    enableBasicAutocompletion: true,
                    enableSnippets: true,
                    enableLiveAutocompletion: true,
                    showLineNumbers: this.options.option.lineNumbers
                });
                if (this.options.option.value) this.editor.setValue(this.options.option.value);
                this.editor.o2Editor = this;

                this.focus();

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

                this.editor.on("blur", function(){
                    this.fireEvent("blur");
                }.bind(this));
                this.editor.on("change", function(){
                    this.fireEvent("change");
                }.bind(this));


                this.node.addEvent("keydown", function(e){
                    e.stopPropagation();
                });

                if( this.fontSize ){
                    this.setFontSize( this.fontSize );
                }

                //o2.widget.JavascriptEditor.getCompletionEnvironment(this.options.runtime, function(){
                    this.registerCompletion();
                //}.bind(this));

                this.fireEvent("postLoad");
                if (callback) callback();
            }.bind(this));
        }.bind(this));
    },
    registerCompletion: function(){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.registerCompletionAce(); break;
                case "monaco": this.registerCompletionMonaco(); break;
            }
        }
    },
    registerCompletionMonaco: function(){
        if (!o2.widget.monaco.registeredCompletion){
            monaco.languages.registerCompletionItemProvider('javascript', {
                "triggerCharacters": ["."],
                provideCompletionItems: function (model, position, context, token) {
                    var textUntilPosition = model.getValueInRange({ startLineNumber: position.lineNumber, startColumn: 1, endLineNumber: position.lineNumber, endColumn: position.column });
                    var textPrefix = textUntilPosition.substr(0, textUntilPosition.lastIndexOf("."));

                    if (textPrefix){
                        var preCode = "";
                        var endLineNumber = (position.lineNumber>1) ? position.lineNumber-1 : 0;
                        if (endLineNumber>0){
                            var range = {
                                endColumn: model.getLineMaxColumn(endLineNumber),
                                endLineNumber: endLineNumber,
                                startColumn: 1,
                                startLineNumber: 1
                            };
                            preCode = model.getValueInRange(range);

                        }

                        var sufCode = "";
                        var lineCount = model.getLineCount();
                        var nextLineNumber = position.lineNumber+1;
                        if (nextLineNumber<=lineCount){
                            range = {
                                endColumn: model.getLineMaxColumn(lineCount),
                                endLineNumber: lineCount,
                                startColumn: 1,
                                startLineNumber: nextLineNumber
                            };
                            sufCode = model.getValueInRange(range);
                        }

                        var word = model.getWordUntilPosition(position);
                        var insertRange = { startLineNumber: position.lineNumber, endLineNumber: position.lineNumber, startColumn: word.startColumn, endColumn: word.endColumn };

                        return new Promise(function(s){
                            //if (this.getCompletionObject)
                            o2.widget.JavascriptEditor.getCompletionObject(textPrefix, preCode+"\n"+sufCode, insertRange, model.o2Editor.options.runtime, function(o){
                                s({suggestions: o});
                            }.bind(this), "monaco");
                        }.bind(this));
                    }
                }.bind(this)
            });
            o2.widget.monaco.registeredCompletion = true;
        }
    },
    registerCompletionAce: function(){
        if (!o2.widget.ace.registeredCompletion){
            //添加自动完成列表
            var exports = ace.require("ace/ext/language_tools");
            exports.addCompleter({
                identifierRegexps: [
                    /[a-zA-Z_0-9\$\-\u00A2-\uFFFF]/
                ],
                getCompletions: function(editor, session, pos, prefix, callback){
                    debugger;
                    var codeRange = session.getWordRange(pos.row, 0);
                    codeRange.setEnd(pos.row, pos.column);
                    var x = session.getTextRange(codeRange);
                    x = x.substr(0, x.lastIndexOf("."));

                    if (x){
                        var endLineNumber = (pos.row>0) ? pos.row-1 : -1;
                        var preCode = "";
                        if (endLineNumber>-1){
                            var range = session.getWordRange(0,0);
                            range.setEnd(endLineNumber, session.getLine(endLineNumber).length);
                            preCode = session.getTextRange(range);
                        }

                        var sufCode = "";
                        var lineCount = session.getLength()-1;
                        var nextLineNumber = pos.row+1;
                        if (nextLineNumber<=lineCount){
                            var range = session.getWordRange(nextLineNumber,0);
                            range.setEnd(lineCount, session.getLine(lineCount).length);
                            sufCode = session.getTextRange(range);
                        }


                        return new Promise(function(s){
                            o2.widget.JavascriptEditor.getCompletionObject(x, preCode+"\n"+sufCode, null, editor.o2Editor.options.runtime, function(o){
                                callback(null, o);
                                if (s) s(o);
                            }.bind(this), "ace");
                        }.bind(this));
                    }
                }.bind(this)
            });
            o2.widget.ace.registeredCompletion = true;
        }
    },
    changeEditor: function(type){

        if (this.editor){
            var value = this.getValue();
            this.destroyEditor();
            this.options.type = type;
            this.load(function(){
                this.setValue(value);
            }.bind(this));
        }else{
            this.options.type = o2.editorData.javascriptEditor.editor;
            if (this.options.type.toLowerCase()=="ace"){
                this.loadAce(callback);
            }
            if (this.options.type.toLowerCase()=="monaco"){
                this.loadMonaco(callback);
            }
            if (this.options.type.toLowerCase()=="codeMirror"){
                this.loadCodeMirror(callback);
            }
        }
    },
    destroyEditor: function(){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.destroy(); this.node.empty(); break;
                case "monaco": this.editor.dispose(); break;
            }
        }
    },
    destroy: function(){
	    this.fireEvent("destroy");
	    this.destroyEditor();
	    o2.release(this);
    },
    setTheme: function(theme){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.setTheme( "ace/theme/"+theme); break;
                case "monaco": monaco.editor.setTheme(theme); break;
            }
        }
    },
    setFontSize: function(fontSize){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.setFontSize( fontSize ); break;
                case "monaco": this.editor.updateOptions({"fontSize": fontSize}); break;
            }
        }
    },

    getRange: function(startLine, startCol, endLine, endCol){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace":
                    var range = this.editor.getSelection().getWordRange( startLine-1, startCol-1 );
                    range.setStart(startLine-1, startCol-1);
                    if (endLine && endCol){
                        range.setEnd(endLine-1, endCol-1);
                    }
                    return range;
                case "monaco":
                    return {
                        "endColumn": endCol,
                        "endLineNumber": endLine,
                        "startColumn": startCol,
                        "startLineNumber": startLine
                    }
                    ;
            }
        }
        return null;
    },
    selectRange: function(range){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace":
                    if (o2.typeOf(range)==="array"){
                        this.editor.getSelection().setSelectionRange( range[0] );
                        for (var i=1; i<range.length; i++) this.editor.getSelection().addRange( range[i] );
                    }else{
                        this.editor.getSelection().setSelectionRange( range );
                    }
                    break;
                case "monaco":
                    if (o2.typeOf(range)==="array"){
                        var selections = [];
                        range.each(function(r){
                            selections.push({
                                "positionColumn": r.endColumn,
                                "positionLineNumber": r.endLineNumber,
                                "selectionStartColumn": r.startColumn,
                                "selectionStartLineNumber": r.startLineNumber
                            });
                        });
                        this.editor.setSelections(selections);
                    }else{
                        var selection = {
                            "positionColumn": range.endColumn,
                            "positionLineNumber": range.endLineNumber,
                            "selectionStartColumn": range.startColumn,
                            "selectionStartLineNumber": range.startLineNumber
                        }
                        this.editor.setSelection(selection);
                    }
                    this.editor.focus();
                    break;
            }
        }
    },

    setValue: function(v){
        if (this.editor) this.editor.setValue(v);
    },
    insertValue : function(v){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace":
                    this.editor.insert(v);
                    break;
                case "monaco":
                    // this.editor.getModel().applyEdits([{
                    this.editor.executeEdits("", [{
                        range: monaco.Range.fromPositions(this.editor.getPosition()),
                        text: v
                    }]);
                    break;
            }
        }
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
                        case "blur": ev = "onDidBlurEditorText"; break;

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
    gotoLine: function(line, col){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.gotoLine(line-1, col-1, true); break;
                case "monaco": this.editor.revealPositionInCenterIfOutsideViewport({
                    "column": col,
                    "lineNumber": line
                }, 0);
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
    showLineNumbers: function(){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.setOption("showLineNumbers", true); break;
                case "codeMirror":  this.editor.setOption("lineNumbers", true); break;
                case "monaco": this.editor.updateOptions({"lineNumbers": "on"});
            }
        }
    },
    hideLineNumbers: function(){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "ace": this.editor.setOption("showLineNumbers", false); break;
                case "codeMirror":  this.editor.setOption("lineNumbers", false); break;
                case "monaco": this.editor.updateOptions({"lineNumbers": "off"});
            }
        }
    },
    max: function(){
        if (this.editor){
            switch (this.options.type.toLowerCase()) {
                case "codeMirror": this.editor.setSize("100%", "100%"); break;
                case "ace": this.editor.resize(); break;
                case "monaco": this.editor.layout(); break;
            }
        }
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

// o2.widget.JavascriptEditor.runtimeEnvironment = {};
// o2.widget.JavascriptEditor.getCompletionEnvironment = function(runtime, callback) {
//
//     if (!o2.widget.JavascriptEditor.runtimeEnvironment[runtime]) {
//         o2.require("o2.xScript.Macro", function() {
//             switch (runtime) {
//                 case "service":
//                     o2.widget.JavascriptEditor.getServiceCompletionEnvironment(runtime,callback);
//                     break;
//                 case "server":
//                     o2.widget.JavascriptEditor.getServerCompletionEnvironment(runtime,callback);
//                     break;
//                 case "all":
//                     o2.widget.JavascriptEditor.getAllCompletionEnvironment(runtime,callback);
//                     break;
//                 default:
//                     o2.widget.JavascriptEditor.getDefaultCompletionEnvironment(runtime,callback);
//             }
//         });
//     } else {
//         if (callback) callback();
//     }
// };
//
// o2.widget.JavascriptEditor.getServiceCompletionEnvironment = function(runtime, callback) {
//     //var serviceScriptText = null;
//     var serviceScriptSubstitute = null;
//     var check = function () {
//         //if (o2.typeOf(serviceScriptText) !== "null" && o2.typeOf(serviceScriptSubstitute) !== "null") {
//         if (o2.typeOf(serviceScriptSubstitute) !== "null") {
//             //var code = "o2.Macro.swapSpace.tmpMacroCompletionFunction = function (){\n" + serviceScriptSubstitute + "\n" + serviceScriptText + "\nreturn bind;" + "\n};";
//             var code = "o2.Macro.swapSpace.tmpMacroCompletionFunction = function (){\n" + serviceScriptSubstitute + "\nreturn bind;" + "\n};";
//             Browser.exec(code);
//             var ev = o2.Macro.swapSpace.tmpMacroCompletionFunction() ;
//             o2.widget.JavascriptEditor.runtimeEnvironment[runtime] = {
//                 "environment": ev,
//                 exec: function(code){
//                     return o2.Macro.exec(code, this.environment);
//                 }
//             }
//             if (callback) callback();
//         }
//     }
//
//     // o2.xhr_get("../x_desktop/js/initialServiceScriptText.js", function (xhr) {
//     //     serviceScriptText = xhr.responseText;
//     //     check();
//     // }, function () {
//     //     serviceScriptText = "";
//     //     check();
//     // });
//     o2.xhr_get("../x_desktop/js/initalServiceScriptSubstitute.js", function (xhr) {
//         serviceScriptSubstitute = xhr.responseText;
//         check();
//     }, function () {
//         serviceScriptSubstitute = "";
//         check();
//     });
// };
//
// o2.widget.JavascriptEditor.getServerCompletionEnvironment = function(runtime, callback) {
//    //var serverScriptText = null;
//     var serverScriptSubstitute = null;
//     var check = function () {
//         // if (o2.typeOf(serverScriptText) !== "null" && o2.typeOf(serverScriptSubstitute) !== "null") {
//         //     var code = "o2.Macro.swapSpace.tmpMacroCompletionFunction = function (){\n" + serverScriptSubstitute + "\n" + serverScriptText + "\nreturn bind;" + "\n};";
//         if (o2.typeOf(serverScriptSubstitute) !== "null") {
//             var code = "o2.Macro.swapSpace.tmpMacroCompletionFunction = function (){\n" + serverScriptSubstitute + "\nreturn bind;" + "\n};";
//             Browser.exec(code);
//             var ev = o2.Macro.swapSpace.tmpMacroCompletionFunction();
//             o2.widget.JavascriptEditor.runtimeEnvironment[runtime] = {
//                 "environment": ev,
//                 exec: function(code){
//                     return o2.Macro.exec(code, this.environment);
//                 }
//             }
//             if (callback) callback();
//         }
//     }
//
//     // o2.xhr_get("../x_desktop/js/initialScriptText.js", function (xhr) {
//     //     serverScriptText = xhr.responseText;
//     //     check();
//     // }, function () {
//     //     serverScriptText = "";
//     //     check();
//     // });
//     o2.xhr_get("../x_desktop/js/initalScriptSubstitute.js", function (xhr) {
//         serverScriptSubstitute = xhr.responseText;
//         check();
//     }, function () {
//         serverScriptSubstitute = "";
//         check();
//     });
// };
//
// o2.widget.JavascriptEditor.getDefaultCompletionEnvironment = function(runtime, callback){
//     var json = null;
//     o2.getJSON("../o2_core/o2/widget/$JavascriptEditor/environment.json", function (data) {
//         json = data;
//         o2.widget.JavascriptEditor.runtimeEnvironment[runtime] = new o2.Macro.FormContext(json);
//         if (callback) callback();
//     });
// }
//
// o2.widget.JavascriptEditor.getAllCompletionEnvironment = function(runtime, callback){
//     var check = function(){
//         if (o2.widget.JavascriptEditor.runtimeEnvironment["service"] && o2.widget.JavascriptEditor.runtimeEnvironment["server"] && o2.widget.JavascriptEditor.runtimeEnvironment["web"] ){
//         //if (o2.widget.JavascriptEditor.runtimeEnvironment["web"] ){
//             var ev = Object.merge(o2.widget.JavascriptEditor.runtimeEnvironment["service"].environment,
//                 o2.widget.JavascriptEditor.runtimeEnvironment["server"].environment,
//                 o2.widget.JavascriptEditor.runtimeEnvironment["web"].environment)
//
//             //var ev = o2.widget.JavascriptEditor.runtimeEnvironment["web"].environment;
//
//             o2.widget.JavascriptEditor.runtimeEnvironment[runtime] = {
//                 "environment": ev,
//                 exec: function(code){
//                     return o2.Macro.exec(code, this.environment);
//                 }
//             }
//             if (callback) callback();
//         }
//     }
//     o2.widget.JavascriptEditor.getServiceCompletionEnvironment("service", check);
//     o2.widget.JavascriptEditor.getServerCompletionEnvironment("server", check);
//     o2.widget.JavascriptEditor.getDefaultCompletionEnvironment("web", check);
//
// }

o2.widget.JavascriptEditor.filterRangeScript = function(s, f1, f2){
    var textScript = "";
    var n = 0;
    for (var i=s.length-1; i>=0; i--){
        var char = s.charAt(i);
        if (char==f2) n++;
        if (char==f1){
            n--;
            if (n<0) break;
        }
        textScript = char+textScript;
    }
    return textScript;
},

o2.widget.JavascriptEditor.getCompletionObject = function(textPrefix, preCode, range, runtime, callback, type){
    textPrefix = o2.widget.JavascriptEditor.filterRangeScript(textPrefix, "(", ")");
    textPrefix = o2.widget.JavascriptEditor.filterRangeScript(textPrefix, "{", "}");
    textPrefix = o2.widget.JavascriptEditor.filterRangeScript(textPrefix, "[", "]");

    if (textPrefix.lastIndexOf("=")!=-1) textPrefix = textPrefix.substr(textPrefix.lastIndexOf("=")+1);
    if (textPrefix.lastIndexOf(" new ")!=-1) textPrefix = textPrefix.substr(textPrefix.lastIndexOf(" new ")+5);
    //if (preCode.lastIndexOf("{")!=-1) preCode = preCode.substr(preCode.lastIndexOf("{")+1);

    var codeObj = {
        "code": textPrefix,
        "preCode": preCode,
        "runtime": runtime,
        "id": o2.uuid(),
        "type": type,
        "range": range
    }

    return o2.JSEditorCWE.exec(codeObj, callback);
},

o2.widget.JavascriptEditor.completionWorkerEnvironment = o2.JSEditorCWE = {
    init: function(){
        this.callbackPool = {};
        this.scriptWorker = new Worker("../o2_core/scriptWorker.js");
        this.scriptWorker.onmessage = function(e) {
            if (e.data && e.data.type=="ready") this.setOnMessage();
        }.bind(this);
        this.isInit = true;
        return this;
    },
    setOnMessage: function(){
        this.scriptWorker.onmessage = function(e) {
            var o = e.data;
            if (o){
                var bo = this.callbackPool[o.id];
                if (bo.uuid==o.uuid) if (bo.callback) bo.callback(o.o);
            }
        }.bind(this);
    },
    exec: function(o, callback){
        if (this.scriptWorker){
            var uuid = o2.uuid();
            o.uuid = uuid;
            this.callbackPool[o.id] = {
                "callback": callback,
                "uuid": uuid
            }
            this.scriptWorker.postMessage(o);
        }
    }
};
