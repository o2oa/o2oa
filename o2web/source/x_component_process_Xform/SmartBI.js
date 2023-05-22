MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class SmartBI 统计图表组件。
 * @o2cn 统计图表组件
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
MWF.xApplication.process.Xform.SmartBI = MWF.APPSmartBI =  new Class({
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        if (!this.json.smartbiresource || this.json.smartbiresource==="none") this.node.destroy();
        else{
            var url;
            var value = this.json.smartbiresource;
            var SmartBIAction = o2.Actions.load("x_custom_smartbi_assemble_control");
            var addressUri = SmartBIAction.ResourceAction.address;
            
            if(addressUri){
                SmartBIAction.ResourceAction.address(value,function(json){ 
                    if(json.data.value !==""){
                        url = json.data.value;
                        url = url +"&showtoolbar="+this.json.smartbidisplaytoolbar+"&showLeftTree="+this.json.smartbidisplaylefttree;
                    }
                }.bind(this),null,false)
            }else{
                var address = SmartBIAction.ResourceAction.action.getAddress();
                var uri = SmartBIAction.ResourceAction.action.actions.open.uri;
                var url = uri.replace("{id}", encodeURIComponent(value));

                url = url +"?showtoolbar="+this.json.smartbidisplaytoolbar+"&showLeftTree="+this.json.smartbidisplaylefttree;
                
                url = o2.filterUrl(address+url);
            }
                        
            this.iframe = new Element("iframe",{
                src:url,
                frameborder:"0",
                scrolling:"auto"
            }).inject(this.node,"after");
            
            var _properties = this.json.properties||{};
            this.node.destroy();
            this.node = this.iframe.setStyles({
                "width":"100%",
                "height":"100%",
                "min-height":"300px",
                "min-width":"300px"
            });

			this.node.set(_properties)
			
        }
        
	}
});