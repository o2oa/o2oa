MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Codeeditor = MWF.APPCodeeditor =  new Class({
    Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["save", "change", "blur", "postLoadEditor", "destroy", "queryLoad", "load", "postLoad", "afterLoad", "maxSize", "returnSize"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
        this.fieldModuleLoaded = false;
    },
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();
            this._afterLoaded();
            this.fireEvent("postLoad");
            this.fireEvent("load");
        }
    },


    _loadUserInterface: function(){

        this.node.empty();
        if (this.isReadonly()){
            var value = this._getBusinessData();
            this.highlighting(value);
            this.node.setStyles({
                "padding": "0.5em 0.6em",
                "background-color": "#f7f7f7",
                "overflow": "auto",
                "font-size": "0.875em",
                "border-radius": "var(--oo-default-radius)"
            })
        }else{
            this.loadCodeeditor();
        }
    },
    highlighting: function(value){
        var contentType = this.json.mode ? 'text/'+this.json.mode : 'text/javascript';
        this.preNode = new Element('pre').inject(this.node);
        this.preNode.set('data-lang', contentType);
        this.preNode.set('text', value || ' ');

        o2.require("o2.widget.monaco", function(){
            o2.widget.monaco.load(function(){
                monaco.editor.colorizeElement(this.preNode, {});
            }.bind(this));
        }.bind(this));
    },
    loadCodeeditor: function(){
        MWF.require("MWF.widget.ScriptArea", function(){
            this.editor = new MWF.widget.ScriptArea(this.node, {
                "title": this.json.title || "",
                "isbind": false,
                "mode": this.json.mode || "javascript",
                "maxObj": this.form.node,
                "maxPosition": "absolute",
                "onChange": function(){
                    this._setBusinessData(this.getData());
                    this.fireEvent('change');
                }.bind(this),
                "onSave": function(){
                    this._setBusinessData(this.getData());
                    this.fireEvent('save');
                }.bind(this),

                "onQueryLoad": function(){
                    this.fireEvent('queryLoad');
                }.bind(this),
                "onPostLoad": function(){
                    this.fireEvent('postLoad');
                    this.fireEvent('load');
                    this.fireEvent('afterLoad');

                }.bind(this),
                "onMaxSize": function(){
                    this.fireEvent('maxSize');
                }.bind(this),
                "onReturnSize": function(){
                    this.fireEvent('returnSize');
                }.bind(this),
                "onBlur": function(){
                    this.fireEvent('blur');
                }.bind(this),
                "onPostLoadEditor": function(){
                    this.fireEvent('postLoadEditor');
                }.bind(this),
                "onDestroy": function(){
                    this.fireEvent('destroy');
                }.bind(this),
                "style": this.json.style || "v10"
            });
            this.editor.load({code: this._getBusinessData()});
        }.bind(this));
    },


    _loadEvents: function(editorConfig){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                this.editor.on(key, function(event){
                    return this.form.Macro.fire(e.code, this, event);
                }.bind(this), this);
            }
        }.bind(this));

    },

    _loadValue: function(){
        var data = this._getBusinessData();
    },

    resetData: function(){
        this.setData(this._getBusinessData());
    },

    isEmpty : function(){
        return !this.getData().trim();
    },

    getData: function(){
        return this.editor ? this.editor.getData() : this._getBusinessData();
    },

    setData: function(data){
        this._setBusinessData(data);
        if (this.editor) this.editor.setData(data);
    },
    destroy: function(){
        if( this.editor )this.editor.destroy();
    },

    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border", "1px solid red");

            this.errNode = this.createErrorNode(text).inject(this.node, "after");
            this.showNotValidationMode(this.node);
            if (!this.errNode.isIntoView()) this.errNode.scrollIntoView(false);
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            if (p.get("MWFtype") == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
                    var contentAreaNode = p.getParent("div").getParent("div");
                    var tabAreaNode = contentAreaNode.getPrevious("div");
                    var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                    var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
                    tabNode.click();
                    p = tabAreaNode.getParent("div");
                }
            }
            this.showNotValidationMode(p);
        }
    },
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    },

    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == data.decision);
        if (flag){
            var n = this.getData();
            var v = (data.valueType=="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },
    validation: function(routeName, opinion){
        if (!this.validationConfig(routeName, opinion))  return false;

        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;

        this.currentRouteName = routeName;
        var flag = this.form.Macro.exec(this.json.validation.code, this);
        this.currentRouteName = "";

        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    }
}); 
