MWF.xApplication.process.Xform.Source = MWF.APPSource =  new Class({
	Extends: MWF.APPDiv,
    options: {
        "moduleEvents": ["queryLoad","postLoad","load", "postLoadData", "loadData"]
    },

	_loadUserInterface: function(){
        this.data = null;
        if (this.json.path){
            if (this.json.sourceType=="o2"){
                if (this.json.path) this._getO2Source();
            }
        }
	},
    _getO2Address: function(){
        try {
            this.json.service = JSON.parse(this.json.contextRoot);
        }catch(e){
            this.json.service = {"root": this.json.contextRoot, "action":"", "method": "", "url": ""};
        }
        var addressObj = layout.serviceAddressList[this.json.service.root];

        if (addressObj){
            this.address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
        }else{
            var host = layout.desktop.centerServer.host || window.location.hostname;
            var port = layout.desktop.centerServer.port;
            this.address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center";
        }
    },
    //_getConfigParameters: function(){
    //
    //    return pars;
    //},
    _getO2Uri: function(){
        //var uri = this.json.path || this.json.selectPath;
        var uri = this.json.path;
        var pars = {};
        if (this.json.parameters){
            Object.each(this.json.parameters, function(v, key){
                if (uri.indexOf("{"+key+"}")!=-1){
                    var reg = new RegExp("{"+key+"}", "g");
                    uri = uri.replace(reg, encodeURIComponent((v && v.code) ? (this.form.Macro.exec(v.code, this) || "") : v));
                }else{
                    pars[key] = v;
                }
            }.bind(this));
        }

        var data = null;
        if (this.json.requestBody){
            if (this.json.requestBody.code){
                data = this.form.Macro.exec(this.json.requestBody.code, this)
            }
        }

        if (this.json.httpMethod=="GET" || this.json.httpMethod=="OPTIONS" || this.json.httpMethod=="HEAD" || this.json.httpMethod=="DELETE"){
            var tag = "?";
            if (uri.indexOf("?")!=-1) tag = "&";
            Object.each(pars, function(v, k){
                var value = (v && v.code) ? (this.form.Macro.exec(v.code, this) || "") : v;
                uri = uri+tag+k+"="+value;
            }.bind(this));
        }else{
            Object.each(pars, function(v, k){
                if (!data) data = {};
                var value = (v && v.code) ? (this.form.Macro.exec(v.code, this) || "") : v;
                data[k] = value;
            }.bind(this));
        }
        this.body = data;
        this.uri = this.address+uri;
    },
    _getO2Source: function(){
        this._getO2Address();
        this._getO2Uri();

        this._invoke(function(){
            this._loadSub(this.node);
            this.fireEvent("loadData");
        }.bind(this));
    },
    _invoke: function(callback){
        MWF.restful(this.json.httpMethod, this.uri, JSON.encode(this.body), function(json){
            this.data = json;
            this.fireEvent("postLoadData");
            if (callback) callback();
        }.bind(this), true, true);

    },
    setBody: function(data){
        this.body = data;
    },
    setParameters: function(json){
        this.json.parameters = json;
        this._getO2Address();
        this._getO2Uri();
    },
    addParameters: function(json){
        if (!this.json.parameters) this.json.parameters={};
        Object.each(json, function(v, k){
            this.json.parameters[k] = v;
        }.bind(this));
        this._getO2Address();
        this._getO2Uri();
    },
    reload: function(notInit, callback){
        this._invoke(function(){
            this._loadSub(this.node, notInit);
            this.fireEvent("loadData");
            if (callback) callback();
        }.bind(this));
    },
    _loadSub: function(dom, notInit){
        var subDom = dom.getFirst();
        var module = null;
        while (subDom){
            module = null;

            module = subDom.retrieve("module", null);
            if (module){
                if (module._loadJsonData) module._loadJsonData(notInit);
            }else{
                this._loadSub(subDom);
            }

            //var type = subDom.get("MWFtype");
            //if (type){
            //    if (type=="sourceText"){
            //        module = subDom.retrieve("module");
            //        module._loadData();
            //    }else if (type=="subSource"){
            //        module = subDom.retrieve("module");
            //        module._loadData();
            //    }else{
            //        this._loadSub(subDom);
            //    }
            //}else{
            //   this._loadSub(subDom);
            //}
            subDom = subDom.getNext();
        }
    }


}); 
