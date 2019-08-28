MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
COMMON.AjaxModule.load("JSONTemplate", null, false);
MWF.xApplication.process.Xform.SubSource = MWF.APPSubSource =  new Class({
    Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["queryLoad","postLoad","load", "postLoadData", "loadData"]
    },
    load: function(){
        this._loadModuleEvents();
        this._queryLoaded();
        this._loadUserInterface();
        //this._loadStyles();
        //this._loadEvents();
        this._loadDomEvents();

        this._afterLoaded();
    },
    _loadUserInterface: function(){
        this.loopNodes = [];
        this.subSourceItems = [];
        var node = new Element("div").inject(this.node, "before");
        this.node.inject(node);
        this.loopNode = this.node.dispose();
        this.node = node;
        var id = node.get("id");
        node.set("id", "");
        this.node.set({
            "id": id,
            "mwftype": node.get("mwftype")
        });
        this.node.store("module", this);
        this._loadJsonData();
    },
    _getSource: function(){
        var parent = this.node.getParent();
        while(parent && (parent.get("MWFtype")!="source" && parent.get("MWFtype")!="subSource" && parent.get("MWFtype")!="subSourceItem")) parent = parent.getParent();
        return (parent) ? parent.retrieve("module") : null;
    },
    _getSourceData: function(sourceData){
        var data = sourceData;
        if (this.json.jsonPath!="."){
            var paths = this.json.jsonPath.split(".");
            paths.each(function(p){
                data = data[p];
            }.bind(this));
        }
        this.data = data;
    },
    _loopSub: function(dom, i){
        var moduleNodes = this.form._getModuleNodes(dom);
        moduleNodes.each(function(node){
            var json = this.form._getDomjson(node);
            var subJson = Object.clone(json);
            subJson.id = subJson.id+"_"+i;
            node.set("id", subJson.id);

            var module = this.form._loadModule(subJson, node);
            //this.modules.push(module);
        }.bind(this));
    },
    _loopData: function(){
        this.data.each(function(d, i){
            var node = this.loopNode.clone(true, true);
            node.inject(this.node);
            var json = Object.clone(this.json);
            json.id = json.id+"_"+i;
            json.type = "SubSourceItem";
            node.set({
                "id": json.id,
                "mwftype": "subSourceItem"
            });

            var module = this.form._loadModule(json, node, function(){
                this.data = d;
                this.position = i;
            });
            this.subSourceItems.push(module);
            this.loopNodes.push(node);

            this._loopSub(node, i);

        }.bind(this));
    },
    _initSubSource: function(){
        if (this.loopNode){
            var moduleNodes = this.form._getModuleNodes(this.node);
            moduleNodes.each(function(node){
                var module = node.retrieve("module");
                if (module){
                    if (module.json.type=="SubSource"){
                        module._initSubSource();
                    }else{
                        MWF.release(module);
                    }
                }
            }.bind(this));
            this.node.empty();
        }
        this.loopNodes = [];
        this.subSourceItems = [];
    },
    _loadJsonData: function(notInit){
        if (!notInit) this._initSubSource();
        this.source = this._getSource();
        if (this.source){
            if (this.source.data){
                this._getSourceData(this.source.data);
                this.fireEvent("postLoadData");
                if (typeOf(this.data)=="array"){
                    this._loopData();
                    this.fireEvent("loadData");
                }else{
                    this._loadModules(this.node);
                }

                //this.tmpDiv = new Element("div");
                //var html = "{loop:"+this.json.jsonPath+"}"+this.node.outerHTML+"{/loop:"+this.json.jsonPath+"}";
                ////this.template = new Template();
                ////var loopHtml = this.template.substitute("{"+this.json.jsonPath+"}", this.source.data);
                //this.node.set("text", this.text);

            }
        }
    }
});

MWF.xApplication.process.Xform.SubSourceItem = MWF.APPSubSourceItem =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        this.loopNodes = [];
        this.subSourceItems = [];
    }
});