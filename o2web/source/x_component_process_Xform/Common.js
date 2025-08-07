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
    _loadUserInterface: function(){
        if (!this.isReadable){
            this.node.setStyle('display', 'none');
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
});