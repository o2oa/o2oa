o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elcarousel 基于Element UI的输入框组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var input = this.form.get("name"); //获取组件
 * //方法2
 * var input = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|Element UI Tree 树形控件}
 */
MWF.xApplication.process.Xform.Elcarousel = MWF.APPElcarousel =  new Class(
    /** @lends o2.xApplication.process.Xform.Elcarousel# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 幻灯片切换时触发。this.event[0]为目前激活的幻灯片的索引；
         * @event MWF.xApplication.process.Xform.Elcarousel#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/carousel|幻灯片组件的Events章节}
         */
        "elEvents": ["change"]
    },
    _loadNode: function(){
        this._loadNodeEdit();
    },
    _loadNodeEdit: function(){
        this._createElementHtml().then(function(html){
            var id = (this.json.id.indexOf("..")!==-1) ? this.json.id.replace(/\.\./g, "_") : this.json.id;
            this.json["$id"] = (id.indexOf("-")!==-1) ? id.replace(/-/g, "_") : id;
            this.node.appendHTML(html, "before");
            var input = this.node.getPrevious();

            this.node.destroy();
            this.node = input;
            this.node.set({
                "id": this.json.id,
                "MWFType": this.json.type
            });
            this.node.addClass("o2_vue");
            this._createVueApp();
        }.bind(this));
    },
    _appendVueData: function(){
        if (!this.json.heightText) this.json.heightText = this.json.height+"px";
        if( !this.json.initialIndex )this.json.initialIndex = 0;
        if( !this.json.carouselType )this.json.carouselType = "";
        if( !this.json.trigger )this.json.trigger = "";
        // if (this.json.dataType === "script"){
        //     this.json.data = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);
        // }else{
        //     this.json.data = this.json.dataJson;
        // }
        // this.parseData();
    },
    _createElementHtml: function() {
        return this.parseItem().then(function ( data ) {
            var itemHtml = data[0];
            var html = "<el-carousel";
            html += " :height=\"heightText\"";
            html += " :initial-index=initialIndex";
            html += " :trigger=\"trigger\"";
            html += " :autoplay=\"autoplay\"";
            html += " :interval=interval";
            html += " :indicator-position=\"indicatorPosition\"";
            html += " :arrow=\"arrow\"";
            html += " :type=\"carouselType\"";
            html += " :loop=\"loop\"";
            html += " :direction=\"direction\"";

            this.options.elEvents.forEach(function(k){
                html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
            });

            if (this.json.elProperties){
                Object.keys(this.json.elProperties).forEach(function(k){
                    if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
                }, this);
            }

            if (this.json.elStyles) html += " :style=\"elStyles\"";

            html += ">";

            if (this.json.vueSlot){
                html += this.json.vueSlot;
            }else{
                html += itemHtml;
            }

            html += "</el-carousel>";
            return html;
        }.bind(this))
    },
    parseItem: function(){
        return Promise.all([
            this.parseItemHtml(),
            this.parseItemData()
        ]);
    },
    parseItemData: function(){
        if( this.json.dataType === "hotpicture" ) {
            var obj;
            if (this.json.filterScript && this.json.filterScript.code) {
                obj = this.form.Macro.exec(((this.json.filterScript) ? this.json.filterScript.code : ""), this);
            }
            if (obj) obj = {};
            var action = o2.Actions.load("x_hotpic_assemble_control").HotPictureInfoAction.listForPage(this.json.page, this.json.count, obj);
            return action.then(function (json) {
                // var lineHeight = this.json.height ? ( "line-height:"+this.json.height + "px;") : "";
                this.json.items = json.data;
                return this.json.items;
            }.bind(this))
        }else if( this.json.dataType === "source" ){
            return this._getO2Source().then(function (json) {
                var paths = ( this.json.dataItemPath || "data" ).split(".");
                var d = json;
                for(var i=0; i<paths.length; i++){
                    if( d && d[paths[i]] )d = d[paths[i]];
                }
                if( !d )d = [];
                if( o2.typeOf(d) !== "array" )d = [ d ];
                this.json.items = d;
                return this.json.items;
            }.bind(this))
        }else if( this.json.dataType === "script" ){
            var data;
            if (this.json.dataScript && this.json.dataScript.code) {
                data = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);
            }
            if( !data ){
                return;
            }else if (typeof data.then !== 'function') {
                data = Promise.resolve(data);
            }
            return data.then(function ( d ) {
                var paths = ( this.json.scriptDataItemPath || "data" ).split(".");
                for(var i=0; i<paths.length; i++){
                    if( d && d[paths[i]] )d = d[paths[i]];
                }
                if( !d )d = [];
                if( o2.typeOf(d) !== "array" )d = [ d ];
                this.json.items = d;
                return this.json.items;
            }.bind(this))
        }
    },
    parseItemHtml: function(data){
        var html = "";
        if( this.json.dataType === "hotpicture" ){
            // if( !this.json.contentType || this.json.contentType === "config" ){
                if( (!this.json.contentConfig || !this.json.contentConfig.length) ){
                    html += "<el-carousel-item v-for=\"item in items\">";
                    html +=     "<img :src=\"getImageSrc(item.picId)\"  @click=\"clickHotpictureItem(item, $event)\" style=\"height: 100%; width:100%\"/>";
                    html +=     "<div style=\"line-height:30px;height: 30px;width:100%;text-align: center;position: absolute;bottom:0px;left:0px;color:#fff;background: rgba(104, 104, 104, 0.5);\">{{item.title}}</div>";
                    html += "</el-carousel-item>";
                    return html;
                }else{
                    return this.parseContentConfig()
                }
            // }
        }else{ //if( this.json.contentType === "config" ){
            return this.parseContentConfig()
        }
    },
    parseContentConfig: function(){
        var _self = this;
        var html = "";
        html += "<el-carousel-item v-for=\"item in items\">";
        this.configFun = {};
        this.json.contentConfig.each(function (config, i) {
            var srcFunName, textFunName, clickFunName, srcHtml = "", clickHtml = "", textHtml="";
            if( config.type === "img" && config.srcScript && config.srcScript.code) {
                srcFunName = "getImageSrc_" + i;
                srcHtml = " :src=\"" + srcFunName + "(item)\"";
                this.configFun[srcFunName] = function (item) {
                    return _self.form.Macro.fire(this.srcScript.code, _self, item);
                }.bind(config);
            }
            if( config.clickScript && config.clickScript.code ){
                clickFunName = "click_"+i;
                clickHtml = " @click=\""+clickFunName+"(item, $event)\"";
                this.configFun[clickFunName] = function (item, ev) {
                    return _self.form.Macro.fire(this.clickScript.code, _self, [item, ev]);
                }.bind(config);
            }
            if( config.type === "text" && config.textScript && config.textScript.code) {
                textFunName = "getText_" + i;
                textHtml = "{{"+textFunName+"(item)}}";
                this.configFun[textFunName] = function (item) {
                    return _self.form.Macro.fire(this.textScript.code, _self, item);
                }.bind(config);
            }
            if( config.type === "img" ){
                html +=  "<img" + srcHtml + clickHtml +" style=\""+this.jsonToStyle(config.styles)+"\"/>";
            }else{
                html +=  "<div"+ clickHtml +" style=\""+this.jsonToStyle(config.styles)+"\">"+textHtml+"</div>";
            }
        }.bind(this));
        html += "</el-carousel-item>";
        return html;
    },
    _afterCreateVueExtend: function (app) {
        var _self = this;
        var flag = false;
        if( this.json.dataType === "hotpicture" ) {
            // if (this.json.contentType === "config") {
                if( (!this.json.contentConfig || !this.json.contentConfig.length) ) {
                    app.methods.clickHotpictureItem = function (item, ev) {
                        if (item.application === "CMS") {
                            _self.openDocument(item.infoId, item.title)
                        } else if (item.application === "BBS") {
                            _self.openBBSDocument(item.infoId);
                        }
                    };
                    app.methods.getImageSrc = function (picId) {
                        return MWF.xDesktop.getImageSrc(picId);
                    }
                    flag = true;
                }
            // }
        }
        if( !flag && this.configFun ){
            Object.each(this.configFun, function (fun, key) {
                app.methods[key] = function (item, ev) {
                    return fun(item, ev);
                }.bind(this)
            })
        }
    },

    jsonToStyle: function(styles){
        var style = "";
        for( var key in styles ){
            style += key+":"+styles[key]+";";
        }
        return style;
    },
    _getO2Source: function(){
        this._getO2Address();
        this._getO2Uri();
        return MWF.restful(this.json.httpMethod, this.uri, JSON.encode(this.body), function(json){
            return json;
        }.bind(this), true, true);
    },
    _getO2Address: function(){
        try {
            this.json.service = JSON.parse(this.json.contextRoot);
        }catch(e){
            this.json.service = {"root": this.json.contextRoot, "action":"", "method": "", "url": ""};
        }
        var addressObj = layout.serviceAddressList[this.json.service.root];
        var defaultPort = layout.config.app_protocol==='https' ? "443" : "80";
        if (addressObj){
            var appPort = addressObj.port || window.location.port;
            this.address = layout.config.app_protocol+"//"+(addressObj.host || window.location.hostname)+((!appPort || appPort.toString()===defaultPort) ? "" : ":"+appPort)+addressObj.context;
        }else{
            var host = layout.desktop.centerServer.host || window.location.hostname;
            var port = layout.desktop.centerServer.port || window.location.port;
            this.address = layout.config.app_protocol+"//"+host+((!port || port.toString()===defaultPort) ? "" : ":"+port)+"/"+this.json.service.root;
        }
    },
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

    openDocument: function(id, title, options){
        var op = options || {};
        op.documentId = id;
        op.docTitle = title;
        op.appId = (op.appId) || ("cms.Document"+id);
        return layout.desktop.openApplication(null, "cms.Document", op);
    },
    openBBSDocument: function(id){
        var appId = "ForumDocument"+id;
        if (layout.desktop.apps && layout.desktop.apps[appId]){
            layout.desktop.apps[appId].setCurrent();
        }else {
            return layout.desktop.openApplication(null, "ForumDocument", {
                "id" : id,
                "appId": appId,
                "isEdited" : false,
                "isNew" : false
            });
        }
    }

}); 
