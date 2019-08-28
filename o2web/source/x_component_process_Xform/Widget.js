MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Widget = MWF.APPWidget =  new Class({
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.empty();
        this.getWidget(function(){
            this.loadWidget();
        }.bind(this));
    },
    reload: function(){
        this.node.empty();
        this.getWidget(function(){
            this.loadWidget();
        }.bind(this));
    },
    loadCss: function(){
        if (this.widgetData.json.css && this.widgetData.json.css.code){
            var cssText = this.form.parseCSS(this.widgetData.json.css.code);
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
    checkWidgetNested : function( id ){
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
            parentpageIdList.push( this.widgetData.json.id )
        }else{
            parentpageIdList = [ this.form.json.id, this.widgetData.json.id ];
        }
        return parentpageIdList;
    },
    loadWidget: function(){
        if (this.widgetData ){
            if( this.checkWidgetNested( this.widgetData.json.id ) ){
                //this.form.addEvent("postLoad", function(){

                this.loadCss();

                this.form.widgetModules =  this.form.widgetModules || {};
                var widgetModules = this.form.widgetModules[ this.json.id ] = {};

                var params = this.getPageParamenters();
                if( typeOf(params) === "object" && this.form.Macro && this.form.Macro.environment ){
                    var environment = this.form.Macro.environment;
                    environment.widgetParameters = environment.widgetParameters || {};
                    environment.widgetParameters[ this.json.id ] = params;
                }

                this.node.set("html", this.widgetData.html);
                Object.each(this.widgetData.json.moduleList, function(module, key){
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
                            this.widget = _self;
                            this.parentpageIdList = _self.getParentpageIdList();
                        });
                        this.form.modules.push(module);
                        widgetModules[ json.orgiginalId || json.id ] = module;
                    }
                }.bind(this));
                //}.bind(this));
            }else{
                this.form.notice(MWF.xApplication.process.Xform.LP.widgetNestedError, "error");
            }
        }
        if( this.form.widgetLoadedCount ){
            this.form.widgetLoadedCount++;
        }else{
            this.form.widgetLoadedCount = 1
        }
        this.form.checkSubformLoaded();
    },
    getWidget: function(callback){
        var method = (this.form.options.mode !== "Mobile" && !layout.mobile) ? "getWidgetByName" : "getWidgetByNameMobile";
        if (this.json.widgetType==="script"){
            if (this.json.widgetScript.code){
                var formNome = this.form.Macro.exec(this.json.widgetScript.code, this);
                if (formNome){
                    var app = this.form.businessData.pageInfor.portal;
                    o2.Actions.get("x_portal_assemble_surface")[method](formNome, app, function(json){
                        this.getWidgetData(json.data);
                        if (callback) callback();
                    }.bind(this));
                }else{
                    if (callback) callback();
                }
            }
        }else{
            if (this.json.widgetSelected && this.json.widgetSelected!=="none"){
                var app = this.form.businessData.pageInfor.portal;
                o2.Actions.get("x_portal_assemble_surface")[method](this.json.widgetSelected, app, function(json){
                    this.getWidgetData(json.data);
                    if (callback) callback();
                }.bind(this));
            }else{
                if (callback) callback();
            }
        }
    },
    getWidgetData: function(data){
        var widgetDataStr = null;
        //if (this.form.options.mode !== "Mobile" && !layout.mobile){
        //    widgetDataStr = data.data;
        //}else{
        //    widgetDataStr = data.mobileData;
        //}
        widgetDataStr = data.data;
        this.widgetData = null;
        if (widgetDataStr){
            this.widgetData = JSON.decode(MWF.decodeJsonString(widgetDataStr));
            this.widgetData.updateTime = data.updateTime;
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