MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Image 图片。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var img = this.form.get("name"); //获取组件
 * //方法2
 * var img = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Image = MWF.APPImage =  new Class(
    {
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        if (this.json.properties && this.json.properties["src"]){
            var value = this.json.properties["src"];
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1 || value.indexOf("x_cms_assemble_control")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                var host3 = MWF.Actions.getHost("x_cms_assemble_control");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
                if (value.indexOf("/x_cms_assemble_control")!==-1){
                    value = value.replace("/x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }else if (value.indexOf("x_cms_assemble_control")!==-1){
                    value = value.replace("x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }
                value = o2.filterUrl(value);
            }
            try{
                this.node.set("src", value);
            }catch(e){}
        }else if (this.json.srcfile && this.json.srcfile!="none"){
            value = this.json.srcfile;
            if (typeOf(value)==="object"){
                var url = (value.portal) ? MWF.xDesktop.getPortalFileUr(value.id, value.portal) : MWF.xDesktop.getProcessFileUr(value.id, value.application);
                url = o2.filterUrl(url);
                this.node.set("src", url);
            }else{
                var host = MWF.Actions.getHost("x_portal_assemble_surface");
                var action = MWF.Actions.get("x_portal_assemble_surface");
                var uri = action.action.actions.readFile.uri;
                uri = uri.replace("{flag}", value);
                uri = uri.replace("{applicationFlag}", this.form.json.application);
                value = host+"/x_portal_assemble_surface"+uri;
                value = o2.filterUrl(value);
                this.node.set("src", value);
            }
        }else if (typeOf(this.json.src)=="object"){
            var src = MWF.xDesktop.getImageSrc( this.json.src.imageId );
            this.node.set("src", src);
        }
    },
    reset: function(){
        this._loadUserInterface();
    }
});