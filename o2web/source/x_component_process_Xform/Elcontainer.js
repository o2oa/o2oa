MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Div 容器组件。
 * @o2cn 容器组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var div = this.form.get("name"); //获取组件
 * //方法2
 * var div = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Elcontainer = MWF.APPElcontainer =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        debugger;
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node.setStyle('display', 'none');
        }else{
            this.node.addClass("o2_vue");
            var elcssUrl = this.form.json.elementCssUrl || "../o2_lib/vue/element/index.css";
            o2.loadAll({"css": elcssUrl}, { "sequence": true });

            var asides = this.node.getElements("aside");
            var headers = this.node.getElements("header");
            var mains = this.node.getElements("main");
            var footers = this.node.getElements("footer");

            if (!this.asides || !this.asides.length) this.asides = [];
            if (!this.headers || !this.headers.length) this.headers = [];
            if (!this.mains || !this.mains.length) this.mains = [];
            if (!this.footers || !this.footers.length) this.footers = [];

            asides.each(function(aside){  this.asides.push(this._loadSubModule(aside)); }.bind(this));
            headers.each(function(header){  this.headers.push(this._loadSubModule(header)); }.bind(this));
            mains.each(function(main){  this.mains.push(this._loadSubModule(main)); }.bind(this));
            footers.each(function(footer){  this.footers.push(this._loadSubModule(footer)); }.bind(this));
        }

        
    },
    _loadSubModule: function(node){
        var json = this.form._getDomjson(node);
        var module = null;
        if (json){
            var container = this;
            module = this.form._loadModule(json, node, function(){
                this.container = container;
            });
            this.form.modules.push(module);
        }
        return module;
    }
});
MWF.xApplication.process.Xform.Elcontainer$Main = MWF.APPElcontainer$Main =  new Class({
    Extends: MWF.APP$Module
});
MWF.xApplication.process.Xform.Elcontainer$Aside = MWF.APPElcontainer$Aside =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node.setStyle('display', 'none');
            return '';
        }
        var css = Object.clone(this.form.css["el-container-aside"]);
        if (this.json.recoveryStyles){
            var keys = Object.keys(css);
            keys.forEach(function(key){
                if (this.json.recoveryStyles[key]) delete css[key];
            }.bind(this))
        }
        this.node.setStyles(css);
    }
});
MWF.xApplication.process.Xform.Elcontainer$Header = MWF.APPElcontainer$Header =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node.setStyle('display', 'none');
            return '';
        }
        var css = Object.clone(this.form.css["el-container-header"]);
        if (this.json.recoveryStyles){
            var keys = Object.keys(css);
            keys.forEach(function(key){
                if (this.json.recoveryStyles[key]) delete css[key];
            }.bind(this))
        }
        this.node.setStyles(css);
        //this.node.setStyles(this.form.css["el-container-header"]);
    }
});
MWF.xApplication.process.Xform.Elcontainer$Footer = MWF.APPElcontainer$Footer =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node.setStyle('display', 'none');
            return '';
        }
        var css = Object.clone(this.form.css["el-container-footer"]);
        if (this.json.recoveryStyles){
            var keys = Object.keys(css);
            keys.forEach(function(key){
                if (this.json.recoveryStyles[key]) delete css[key];
            }.bind(this))
        }
        this.node.setStyles(css);

        //this.node.setStyles(this.form.css["el-container-footer"]);
    }
});
