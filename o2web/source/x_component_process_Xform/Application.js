MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Application 门户中嵌入的系统component对象或网页iframe（模块部署中配置的网页URL）。
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
    options: {
        /**
         * component对象初始化后，加载之前触发，this.event可获取component对象。
         * @event MWF.xApplication.process.Xform.Application#queryLoadApplication
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "queryLoadApplication"]
    },

    _loadUserInterface: function(){
        /**
         * @ignore
         * @member parentLine
         * @memberOf MWF.xApplication.process.Xform.Application#
         */

        /**
         * @ignore
         * @member getSource
         * @memberOf MWF.xApplication.process.Xform.Application#
         */
        this.node.empty();
        this.loadApplication();
    },
    /**
     * @summary 重新加载嵌入对象
     * @example
     * this.form.get("fieldId").reload()
     */
    reload: function(){
        this.clean();
        this.loadApplication();
    },
    /**
     * @summary 清除当前嵌入的对象
     * @example
     * this.form.get("fieldId").clean()
     */
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

        /**
         * @summary 当模块部署中配置的是@url:开头的链接时，嵌入的iframe.
         * @member {Object}
         * @example
         * var iframe = this.form.get("fieldId").iframe; //获取iframe
         * iframe.src; //获取iframe的地址
         */
        this.iframe = new Element("iframe", attr).inject( this.node );
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

                /**
                 * @summary 嵌入的component对象.
                 * @member {Object}
                 * @example
                 * var app = this.form.get("fieldId").application; //获取component对象
                 * app.recordStatus(); //获取应用的当前状态
                 * app.refresh();      //刷新应用
                 * app.dialog(option); //弹出一个对话框（详见MWF.widget.Dialog）
                 * app.notice(content, type, target, where, offset); //显示一个通知消息
                 * app.confirm(type, e, title, text, width, height, ok, cancel); //显示一个确认框
                 * app.alert(type, e, title, text, width, height); //弹出一个信息框
                 * app.addEvent(type, fun); //为应用绑定一个事件
                 */
                this.application = new clazz.Main(this.form.app.desktop, opt);
                this.application.status = opt;
                this.fireEvent("queryLoadApplication", this.application);
                this.application.load();
                this.application.setEventTarget(this.form.app);
            }else{
                this.form.app.notice(this.form.app.lp.applicationNotFound+":"+app, "error");
            }
        }catch (e) {
            this.form.app.notice( e.message, "error" );
        }
    },
    /**
     * @summary 获取component对象的路径
     * @param {Function} callback 获取路径后的回调方法，参数为路径
     * @example
     * this.form.get("fieldId").getApplicationOptions(function(path){
     *     //path为路径
     * })
     */
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
     * @summary 获取component对象的参数
     * @return 设置的参数
     * @example
     * var param = this.form.get("fieldId").getApplicationOptions()
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
