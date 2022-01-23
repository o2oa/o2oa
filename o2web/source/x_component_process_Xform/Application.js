MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Application 门户中嵌入的系统应用组件。
 * @o2cn 嵌入的系统应用
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var application = this.form.get("fieldId"); //获取组件
 * //方法2
 * var application = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Application = MWF.APPApplication =  new Class(
    /** @lends MWF.xApplication.process.Xform.Application# */
    {
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.empty();
        this.loadApplication();
    },
    /**
     * @summary 重新加载嵌入应用
     * @example
     * this.form.get("fieldId").reload()
     */
    reload: function(){
        this.clean();
        this.loadApplication();
    },
    clean: function(){
        if(this.application){
            try{
                this.application.close();
            }catch (e) {}
            this.application = null;
        }
        if( this.iframe ){
            this.iframe.destroy();
            this.iframe = null;
        }
    },
    loadApplication: function ( ) {
        if(this.node){
            this.node.empty();
            this.node.setStyles({
                "position": "relative",
                "background-color": "#eee"
            });
        }
        this.clean();
        var options = this.getApplicationOptions() || {};
        this.getApplicationPath(function (componentPath) {
            debugger;
            if( componentPath.indexOf("@url:") === 0 ){
                this._loadIframe( componentPath.substring(5, componentPath.length ) );
            }else{
                this._loadApplication( componentPath, options );
            }
        }.bind(this))
    },
    _loadIframe: function( src ){
        var attr = {
            "src": src,
            "width": "100%",
            "height": "100%",
            "frameborder": "0px",
            "scrolling": "auto",
            "seamless": "seamless"
        };
        this.iframe = new Element("iframe", attr).inject( this.node );
        this.loadMask();
    },
    _loadApplication: function ( app, options ) {
        var clazz = MWF.xApplication;
        app.split(".").each(function (a) {
            clazz[a] = clazz[a] || {};
            clazz = clazz[a];
        });
        clazz.options = clazz.options || {};
        try{
            MWF.xDesktop.requireApp(app, "lp."+o2.language, null, false);
            MWF.xDesktop.requireApp(app, "Main", null, false);
            if( clazz.Main ){
                var opt = options || {};
                opt.embededParent = this.node;
                this.application = new clazz.Main(this.form.app.desktop, opt);
                this.application.status = opt;
                this.application.load();
                this.application.setEventTarget(this.form.app);
            }else{
                this.form.app.notice(this.form.app.lp.applicationNotFound+":"+app, "error");
            }
        }catch (e) {
            this.form.app.notice( e.message, "error" );
        }
    },
    getApplicationPath: function(callback){
        var path;
        if (this.json.componentType==="script"){
            if (this.json.componentScript && this.json.componentScript.code){
                path = this.form.Macro.exec(this.json.componentScript.code, this);
            }
        }else{
            if (this.json.componentSelected && this.json.componentSelected!=="none"){
                path = this.json.componentSelected;
            }else{
                path = ""
            }
        }
        Promise.resolve(path).then(function (p) {
            callback(p);
        })
    },
    /**
     * @summary 获取应用的参数
     * @return 设置的参数
     * @example
     * var param = this.form.get("fieldId").getPageParamenters()
     */
    getApplicationOptions : function(){
        var params = "";
        if( this.json.optionsType === "map" ){
            params = this.json.optionssMapList;
        }else if( this.json.optionsType === "script" ){
            var code = (this.json.optionsScript) ? this.json.optionsScript.code : "";
            if (code){
                params = this.form.Macro.exec(code, this);
            }
        }
        return params;
    }
});
