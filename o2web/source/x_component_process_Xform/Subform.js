MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Subform = MWF.APPSubform =  new Class({
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.empty();
        this.getSubform(function(){
            this.loadSubform();
        }.bind(this));
    },
    reload: function(){
        this.node.empty();
        this.getSubform(function(){
            this.loadSubform();
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
    checkSubformNested : function( id ){
        if( this.parentformIdList ){
            return !this.parentformIdList.contains( id );
        }else{
            return ![ this.form.json.id ].contains( id );
        }
    },
    checkSubformUnique : function( id ){
        if( !this.form.subformLoaded )return true;
        return !this.form.subformLoaded.contains( id );
    },
    getParentformIdList : function(){
        var parentformIdList;
        if( this.parentformIdList ){
            parentformIdList = Array.clone( this.parentformIdList );
            parentformIdList.push( this.subformData.json.id )
        }else{
            parentformIdList = [ this.form.json.id, this.subformData.json.id ];
        }
        return parentformIdList;
    },
    loadSubform: function(){
        if (this.subformData){
            if( !this.checkSubformNested( this.subformData.json.id ) ){
                this.form.notice(MWF.xApplication.process.Xform.LP.subformNestedError, "error");
            }else if( !this.checkSubformUnique( this.subformData.json.id ) ){
                this.form.notice(MWF.xApplication.process.Xform.LP.subformUniqueError, "error");
            }else{
                //this.form.addEvent("postLoad", function(){

                this.loadCss();

                this.node.set("html", this.subformData.html);
                Object.each(this.subformData.json.moduleList, function (module, key) {
                    var formKey = key;
                    if (this.form.json.moduleList[key]) {
                        formKey = this.json.id + "_" + key;
                        var moduleNode = this.node.getElement("#" + key);
                        if (moduleNode) moduleNode.set("id", formKey);
                        module.id = formKey;
                    }
                    this.form.json.moduleList[formKey] = module;
                }.bind(this));

                var moduleNodes = this.form._getModuleNodes(this.node);
                moduleNodes.each(function (node) {
                    if (node.get("MWFtype") !== "form") {
                        var _self = this;
                        var json = this.form._getDomjson(node);
                        //if( json.type === "Subform" || json.moduleName === "subform" )this.form.subformCount++;
                        var module = this.form._loadModule(json, node, function(){
                            this.parentformIdList = _self.getParentformIdList();
                        });
                        this.form.modules.push(module);
                    }
                }.bind(this));

                this.form.subformLoaded.push( this.subformData.json.id );

                //}.bind(this));
            }
        }
        if( this.form.subformLoadedCount ){
            this.form.subformLoadedCount++;
        }else{
            this.form.subformLoadedCount = 1
        }
        //console.log( "add subformLoadedCount , this.form.subformLoadedCount = "+ this.form.subformLoadedCount)
        this.form.checkSubformLoaded();
    },
    getSubform: function(callback){
        if (this.json.subformType==="script"){
            if (this.json.subformScript.code){
                var data = this.form.Macro.exec(this.json.subformScript.code, this);
                if (data){
                    var formName, app;
                    if( typeOf( data ) === "string" ){
                        formName = data;
                    }else{
                        if( data.application )app = data.application;
                        if( data.subform )formName = data.subform;
                    }
                    if( formName ){
                        if( !app )app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                        MWF.Actions.get("x_processplatform_assemble_surface").getForm(formName, app, function(json){
                            this.getSubformData(json.data);
                            if (callback) callback();
                        }.bind(this));
                    }else{
                        if (callback) callback();
                    }
                }else{
                    if (callback) callback();
                }
            }
        }else{
            if (this.json.subformSelected && this.json.subformSelected!=="none"){
                var app;
                if( this.json.subformAppSelected ){
                    app = this.json.subformAppSelected;
                }else{
                    app = (this.form.businessData.work || this.form.businessData.workCompleted).application;
                }
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
        if( !data || typeOf(data)!=="object" )return;
        var subformDataStr = null;
        if ( this.form.json.mode !== "Mobile" && !layout.mobile){
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