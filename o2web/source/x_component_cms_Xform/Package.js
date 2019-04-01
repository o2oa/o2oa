MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};
MWF.require("MWF.xScript.CMSMacro", null, false);
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "ModuleImplements", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Label", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Textfield", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Number", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Personfield", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Orgfield", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Readerfield", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Authorfield", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Calendar", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Textarea", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Select", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Radio", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Checkbox", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Button", null, false);

MWF.xApplication.cms.Xform.Div = MWF.CMSDiv = new Class({
	Extends: MWF.APPDiv
});

MWF.xApplication.cms.Xform.Common = MWF.CMSCommon =  new Class({
    Extends: MWF.APPCommon
});

//MWF.xApplication.cms.Xform.Image = MWF.CMSImage = new Class({
//	Extends: MWF.APPImage
//});

MWF.xApplication.cms.Xform.Image = MWF.CMSImage =  new Class({
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
            }
            try{
                this.node.set("src", value);
            }catch(e){}
        }else if (this.json.srcfile && this.json.srcfile!="none"){
            value = this.json.srcfile;
            if (typeOf(value)==="object"){
                var url = (value.portal) ? MWF.xDesktop.getPortalFileUr(value.id, value.portal) : MWF.xDesktop.getProcessFileUr(value.id, value.application);
                this.node.set("src", url);
            }else{
                var host = MWF.Actions.getHost("x_portal_assemble_surface");
                var action = MWF.Actions.get("x_portal_assemble_surface");
                var uri = action.action.actions.readFile.uri;
                uri = uri.replace("{flag}", value);
                uri = uri.replace("{applicationFlag}", this.form.json.application);
                value = host+"/x_portal_assemble_surface"+uri;
                this.node.set("src", value);
            }
        }else if (typeOf(this.json.src)=="object"){
            var src = MWF.xDesktop.getImageSrc( this.json.src.imageId );
            this.node.set("src", src);
        }
    }
});

//MWF.xDesktop.requireApp("cms.Xform", "ImageClipper", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Table", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Datagrid", null, false);

MWF.xApplication.cms.Xform.Html = MWF.CMSHtml =  new Class({
	Extends: MWF.APPHtml
});

//MWF.xDesktop.requireApp("cms.Xform", "Tab", null, false);

//MWF.xApplication.cms.Xform.tab$Page = MWF.CMSTab$Page = new Class({
//	Extends: MWF.APPTab$Page
//});
//MWF.xApplication.cms.Xform.tab$Content = MWF.CMSTab$Content = new Class({
//	Extends: MWF.APPTab$Content
//});

//MWF.xDesktop.requireApp("cms.Xform", "Tree", null, false);

//MWF.xDesktop.requireApp("cms.Xform", "Iframe", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Htmleditor", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Office", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Attachment", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Actionbar", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Log", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "View", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "ViewSelector", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "Stat", null, false);