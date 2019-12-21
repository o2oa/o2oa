MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Subpage = MWF.APPSubpage =  new Class({
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.empty();
        this.getSubpage(function(){
            this.loadSubpage();
        }.bind(this));
    },
    reload: function(){
        this.node.empty();
        this.getSubpage(function(){
            this.loadSubpage();
        }.bind(this));
    },
    loadCss: function(){
        if (this.subpageData.json.css && this.subpageData.json.css.code){
            var cssText = this.form.parseCSS(this.subpageData.json.css.code);
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
    checkSubpageNested : function( id ){
        if( this.parentpageIdList ){
            return !this.parentpageIdList.contains( id );
        }else{
            return ![ this.form.json.id ].contains( id );
        }
    },
    getParentpageIdList : function(){
        var parentpageIdList;
        if( this.parentpageIdList ){
            parentpageIdList = Array.clone( this.parentpageIdList );
            parentpageIdList.push( this.subpageData.json.id )
        }else{
            parentpageIdList = [ this.form.json.id, this.subpageData.json.id ];
        }
        return parentpageIdList;
    },
    loadSubpage: function(){
        if (this.subpageData ){
            if( this.checkSubpageNested( this.subpageData.json.id ) ){
                //this.form.addEvent("postLoad", function(){

                this.loadCss();

                this.form.subpageModules =  this.form.subpageModules || {};
                var subpageModules = this.form.subpageModules[ this.json.id ] = {};

                var params = this.getPageParamenters();
                if( typeOf(params) === "object" && this.form.Macro && this.form.Macro.environment ){
                    var environment = this.form.Macro.environment;
                    environment.subpageParameters = environment.subpageParameters || {};
                    environment.subpageParameters[ this.json.id ] = params;
                }

                this.node.set("html", this.subpageData.html);
                Object.each(this.subpageData.json.moduleList, function(module, key){
                    var formKey = key;
                    if (this.form.json.moduleList[key]){
                        formKey = this.json.id+"_"+key;
                        var moduleNode = this.node.getElement("#"+key);
                        if (moduleNode) moduleNode.set("id", formKey);
                        module.orgiginalId = key;
                        module.id = formKey;
                    }
                    this.form.json.moduleList[formKey] = module;
                }.bind(this));

                var moduleNodes = this.form._getModuleNodes(this.node);
                moduleNodes.each(function(node){
                    if (node.get("MWFtype")!=="form"){
                        var _self = this;
                        var json = this.form._getDomjson(node);
                        var module = this.form._loadModule(json, node, function(){
                            this.subpage = _self;
                            this.parentpageIdList = _self.getParentpageIdList();
                        });
                        this.form.modules.push(module);
                        subpageModules[ json.orgiginalId || json.id ] = module;
                    }
                }.bind(this));
                //}.bind(this));
            }else{
                this.form.notice(MWF.xApplication.process.Xform.LP.subpageNestedError, "error");
            }
        }
        if( this.form.subpageLoadedCount ){
            this.form.subpageLoadedCount++;
        }else{
            this.form.subpageLoadedCount = 1
        }
        this.form.checkSubformLoaded();
    },
    getSubpage: function(callback){
        if (this.json.subpageType==="script"){
            if (this.json.subpageScript.code){
                var formNome = this.form.Macro.exec(this.json.subpageScript.code, this);
                if (formNome){
                    var app = this.form.businessData.pageInfor.portal;
                    o2.Actions.get("x_portal_assemble_surface").getPageByName(formNome, app, function(json){
                        this.getSubpageData(json.data);
                        if (callback) callback();
                    }.bind(this));
                }else{
                    if (callback) callback();
                }
            }
        }else{
            if (this.json.subpageSelected && this.json.subpageSelected!=="none"){
                var app = this.form.businessData.pageInfor.portal;
                o2.Actions.get("x_portal_assemble_surface").getPageByName(this.json.subpageSelected, app, function(json){
                    this.getSubpageData(json.data);
                    if (callback) callback();
                }.bind(this));
            }else{
                if (callback) callback();
            }
        }
    },
    getSubpageData: function(data){
        var subpageDataStr = null;
        if (this.form.json.mode !== "Mobile" && !layout.mobile){
            subpageDataStr = data.data;
        }else{
            subpageDataStr = data.mobileData;
        }
        this.subpageData = null;
        if (subpageDataStr){
            this.subpageData = JSON.decode(MWF.decodeJsonString(subpageDataStr));
            this.subpageData.updateTime = data.updateTime;
        }
    },
    getPageParamenters : function(){
        var params = null;
        if( this.json.parameterType === "map" ){
            params = this.json.parametersMapList;
        }else if( this.json.parameterType === "script" ){
            var code = this.json.parametersScript.code;
            if (code){
                params = this.form.Macro.exec(code, this);
            }
        }
        return params;
    }
});