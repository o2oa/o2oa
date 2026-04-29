MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Common 通用组件。
 * @o2cn 通用组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var el = this.form.get("name"); //获取组件
 * //方法2
 * var el = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Common = MWF.APPCommon =  new Class({
    Extends: MWF.APP$Module,
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();
            this._loadDomEvents();
            //this._loadEvents();
            this._afterLoaded();
            this.fireEvent("postLoad");

            if( this.moduleSelectAG && typeOf(this.moduleSelectAG.then) === "function" ){
                this.moduleSelectAG.then(function () {
                    this.fireEvent("load");
                    this.isLoaded = true;
                    !!this.downloading && this._loadOONodeDownloading();
                }.bind(this))
            }else{
                this.fireEvent("load");
                this.isLoaded = true;
                !!this.downloading && this._loadOONodeDownloading();
            }
        }
    },
    _loadUserInterface: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
        }else{
            if (this.json.innerHTML){
                this.node.innerHTML = this.json.innerHTML;
            }
            this.node.setProperties(this.json.properties);
        }
        
    },
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    this.node.addEventListener(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadOONodeDownloading: function (){
        if(!this.node?.offsetParent){
            //this.node?.destroy();
            return;
        }
        if( this.node.tagName.toLowerCase().startsWith('oo-')){
            const text = this.node.getAttribute('text');
            const value = this.node.getAttribute('value');
            if (text || value){
                this.downloadingNode = new Element('div', {
                    text: text || value
                }).inject(this.node, 'after');
                this.node.addClass('hide');
            }
        }
    }
});