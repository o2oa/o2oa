MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Common 通用组件。
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
        if (this.json.innerHTML){
            var nodes = this.node.childNodes;
            for (var i=0; i<nodes.length; i++){
                if (nodes[i].nodeType===Node.ELEMENT_NODE){
                    if (!nodes[i].get("MWFtype")){
                        nodes[i].destroy();
                        i--;
                    }
                }else{
                    if (nodes[i].removeNode){
                        nodes[i].removeNode();
                    }else{
                        nodes[i].parentNode.removeChild(nodes[i]);
                    }
                    i--;
                    //nodes[i]
                }
            }
            this.node.appendHTML(this.json.innerHTML);

            // if (this.node.get("html") !== this.json.innerHTML){
            //this.node.appendHTML(this.json.innerHTML);
            // }
        }
        this.node.setProperties(this.json.properties);
    }
});