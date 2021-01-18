MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//COMMON.AjaxModule.load("JSONTemplate", null, false);
/** @class SubSource 子数据源。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var subSource = this.form.get("fieldId"); //获取组件
 * //方法2
 * var subSource = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.SubSource = MWF.APPSubSource =  new Class(
    /** @lends MWF.xApplication.process.Xform.Subform# */
{
    Extends: MWF.APP$Module,
    options: {
        /**
         * 加载数据后执行，但这时还未加载下属组件，可以可以使用this.target.data获取数据进行修改。
         * @event MWF.xApplication.process.Xform.SubSource#postLoadData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 加载数据、下属组件后执行。
         * @event MWF.xApplication.process.Xform.SubSource#loadData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
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
                if (typeOf(this.data)!=="array") this.data = [this.data];
                if (typeOf(this.data)=="array"){
                    this._loopData();
                    this.fireEvent("loadData");
                }else{
                    this.form._loadModules(this.node);
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
