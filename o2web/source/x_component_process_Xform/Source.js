MWF.xDesktop.requireApp("process.Xform", "Div", null, false);

/** @class Source 数据源组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var source = this.form.get("fieldId"); //获取数据源组件
 * //方法2
 * var source = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.Div
 * @o2category FormComponents
 * @o2range {Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Source = MWF.APPSource =  new Class(
    /** @lends MWF.xApplication.process.Xform.Source# */
    {
	Extends: MWF.APPDiv,
    options: {
        /**
         * 加载数据后执行，但这时还未加载下属组件，可以可以使用this.target.data获取数据进行修改。
         * @event MWF.xApplication.process.Xform.Source#postLoadData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 加载数据、下属组件后执行。
         * @event MWF.xApplication.process.Xform.Source#loadData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
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
    /**
     * @summary 替换全部的url参数，但不刷新组件
     * @param {Object} url参数
     * @example
     * //如，原来的组件url参数为：
     * { "page" : 1, "count" : 10  }
     *
     * this.form.get("fieldId").setParameters({"id":"662ede34-4e21-428a-9c3b-f1bf14d15650"});
     *
     * //执行后变为
     * {"id":"662ede34-4e21-428a-9c3b-f1bf14d15650"}
     */
    setParameters: function(json){
        this.json.parameters = json;
        this._getO2Address();
        this._getO2Uri();
    },
    /**
     * @summary 新增url参数，但不刷新组件。如果该参数key已经存在，则覆盖
     * @param {Object} url参数
     * @example
     * * //如，原来的组件url参数为：
     * { "page" : 1, "count" : 10  }
     *
     * this.form.get("fieldId").addParameters({
     *  "page" : 2,
     *  "id":"662ede34-4e21-428a-9c3b-f1bf14d15650"
     *  });
     *
     * //执行后变为
     * {
     *  "page" : 2,
     *  "count" : 10
     *  "id":"662ede34-4e21-428a-9c3b-f1bf14d15650"
     *  }
     */
    addParameters: function(json){
        if (!this.json.parameters) this.json.parameters={};
        Object.each(json, function(v, k){
            this.json.parameters[k] = v;
        }.bind(this));
        this._getO2Address();
        this._getO2Uri();
    },
    /**
     * @summary 重新加载组件。会触发loadData事件
     * @param {Boolean} notInit - false表示不重新初始化子数据源和数据文本，true表示重新初始化，默认为false
     * @param {Function} callback 加载完成后的回调
     * @example
     * this.form.get("fieldId").reload(); //重新加载组件
     */
    reload: function(notInit, callback){
	    this._getO2Uri();
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
