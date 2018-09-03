MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Htmleditor = MWF.APPHtmleditor =  new Class({
	Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["load", "postLoad", "afterLoad"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },
    load: function(){

        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();
            //this._loadEvents();

            this._afterLoaded();
            this.fireEvent("postLoad");
            this.fireEvent("load");
        }
    },

	_loadUserInterface: function(){
		this.node.empty();
        if (this.readonly){
            this.node.set("html", this._getBusinessData());
            this.node.setStyles({
                "-webkit-user-select": "text",
                "-moz-user-select": "text"
            });
        }else{
            var config = Object.clone(this.json.editorProperties);
            if (this.json.config){
                if (this.json.config.code){
                    var obj = MWF.Macro.exec(this.json.config.code, this);
                    Object.each(obj, function(v, k){
                        config[k] = v;
                    });
                }
            }
            this.loadCkeditor(config);
        }
    //    this._loadValue();
	},
    loadCkeditor: function(config){
        COMMON.AjaxModule.loadDom("ckeditor", function(){
            CKEDITOR.disableAutoInline = true;
            var editorDiv = new Element("div").inject(this.node);
            var htmlData = this._getBusinessData();
            if (htmlData){
                editorDiv.set("html", htmlData);
            }else if (this.json.templateCode){
                editorDiv.set("html", this.json.templateCode);
            }
            var height = this.node.getSize().y;
            var editorConfig = config || {};

            if (this.form.json.mode==="Mobile"){
                if (!editorConfig.toolbar && !editorConfig.toolbarGroups){
                    editorConfig.toolbar = [
                        { name: 'paragraph',   items: [ 'Bold', 'Italic', "-" , 'TextColor', "BGColor", 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', "-", 'Undo', 'Redo' ] },
                        { name: 'basicstyles', items: [ 'Styles', 'FontSize']}
                    ];
                }
            }

            editorConfig.localImageMaxWidth = 800;
            editorConfig.reference = this.form.businessData.work.job;
            editorConfig.referenceType = "processPlatformJob";
            
            // CKEDITOR.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/";
            // CKEDITOR.plugins.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/plugins/";
            this.editor = CKEDITOR.replace(editorDiv, editorConfig);
            this._loadEvents();
            //this.editor.on("loaded", function(){
            //    this._loadEvents();
            //}.bind(this));

            //this.setData(data)

            this.editor.on("change", function(){
                this._setBusinessData(this.getData());
            }.bind(this));
            //    this._loadEvents();
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
    getData: function(){
        return this.editor.getData();
    },
    setData: function(data){
        this._setBusinessData(data);
        if (this.editor) this.editor.setData(data);
    },
    createErrorNode: function(text){
        var node = new Element("div");
        var iconNode = new Element("div", {
            "styles": {
                "width": "20px",
                "height": "20px",
                "float": "left",
                "background": "url("+"/x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "20px",
                "margin-left": "20px",
                "color": "red"
            },
            "text": text
        }).inject(node);
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border", "1px solid red");

            this.errNode = this.createErrorNode(text).inject(this.node, "after");
            this.showNotValidationMode(this.node);
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
                    var tabNode = tabAreaNode.getChildren()[idx];
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
        var flag = this.form.Macro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    }
}); 