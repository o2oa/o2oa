MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Subform = MWF.APPSubform =  new Class({
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.empty();
        this.getSubform(function(){
            if (this.subformData){
                this.loadSubform();
            }
        }.bind(this));
    },
    reload: function(){
        this.node.empty();
        this.getSubform(function(){
            if (this.subformData){
                this.loadSubform();
            }
        }.bind(this));
    },
    loadCss: function(){
        if (this.subformData.json.css && this.subformData.json.css.code){
            var cssText = this.form.parseCSS(this.subformData.json.css.code);
            var rex = new RegExp("(.+)(?=\\{)", "g");
            var match;
            var id = this.form.json.id.replace(/\-/g, "");
            while ((match = rex.exec(cssText)) !== null) {
                var prefix = ".css" + id + " ";
                var rule = prefix + match[0];
                cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                rex.lastIndex = rex.lastIndex + prefix.length;
            }

            var styleNode = $("style"+this.form.json.id);
            if (!styleNode){
                var styleNode = document.createElement("style");
                styleNode.setAttribute("type", "text/css");
                styleNode.id="style"+this.form.json.id;
                styleNode.inject(this.form.container, "before");
            }

            if(styleNode.styleSheet){
                var setFunc = function(){
                    styleNode.styleSheet.cssText += cssText;
                };
                if(styleNode.styleSheet.disabled){
                    setTimeout(setFunc, 10);
                }else{
                    setFunc();
                }
            }else{
                var cssTextNode = document.createTextNode(cssText);
                styleNode.appendChild(cssTextNode);
            }
        }
    },
    loadSubform: function(){
        if (this.subformData){
            //this.form.addEvent("postLoad", function(){

            this.loadCss();

                this.node.set("html", this.subformData.html);
                Object.each(this.subformData.json.moduleList, function(module, key){
                    var formKey = key;
                    if (this.form.json.moduleList[key]){
                        formKey = this.json.id+"_"+key;
                        var moduleNode = this.node.getElement("#"+key);
                        if (moduleNode) moduleNode.set("id", formKey);
                        module.id = formKey;
                    }
                    this.form.json.moduleList[formKey] = module;
                }.bind(this));

                var moduleNodes = this.form._getModuleNodes(this.node);
                moduleNodes.each(function(node){
                    if (node.get("MWFtype")!=="form"){
                        var json = this.form._getDomjson(node);
                        var module = this.form._loadModule(json, node);
                        this.form.modules.push(module);
                    }
                }.bind(this));
            //}.bind(this));
        }
    },
    getSubform: function(callback){
        if (this.json.subformType==="script"){
            if (this.json.subformScript.code){
                var formNome = this.form.Macro.exec(this.json.subformScript.code, this);
                if (formNome){
                    var app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                    MWF.Actions.get("x_processplatform_assemble_surface").getForm(formNome, app, function(json){
                        this.getSubformData(json.data);
                        if (callback) callback();
                    }.bind(this));
                }
            }
        }else{
            if (this.json.subformSelected && this.json.subformSelected!=="none"){
                var app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                MWF.Actions.get("x_processplatform_assemble_surface").getForm(this.json.subformSelected, app, function(json){
                    this.getSubformData(json.data);
                    if (callback) callback();
                }.bind(this));
            }else{
                if (callback) callback();
            }
        }
    },
    getSubformData: function(data){
        var subformDataStr = null;
        if (this.form.options.mode !== "Mobile"){
            subformDataStr = data.data;
        }else{
            subformDataStr = data.mobileData;
        }
        this.subformData = null;
        if (subformDataStr){
            this.subformData = JSON.decode(MWF.decodeJsonString(subformDataStr));
            this.subformData.updateTime = data.updateTime;
        }
    }
});