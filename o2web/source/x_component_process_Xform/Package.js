MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);

MWF.xApplication.process.Xform.require = function(callback){
    var modules = [
        ["process.Xform", "Form"],
        ["process.Xform", "Label"],
        ["process.Xform", "Textfield"],
        ["process.Xform", "Number"],
        ["process.Xform", "Personfield"],
        ["process.Xform", "Orgfield"],
        ["process.Xform", "Org"],
        ["process.Xform", "Calendar"],
        ["process.Xform", "Textarea"],
        ["process.Xform", "Opinion"],
        ["process.Xform", "Select"],
        ["process.Xform", "Radio"],
        ["process.Xform", "Checkbox"],
        ["process.Xform", "Button"],
        ["process.Xform", "Combox"],
        ["process.Xform", "Address"],
        ["process.Xform", "Table"],
        ["process.Xform", "Datagrid"],
        ["process.Xform", "Tab"],
        ["process.Xform", "Tree"],
        ["process.Xform", "Iframe"],
        ["process.Xform", "Htmleditor"],
        ["process.Xform", "Office"],
        ["process.Xform", "Attachment"],
        ["process.Xform", "Actionbar"],
        ["process.Xform", "Sidebar"],
        ["process.Xform", "Log"],
        ["process.Xform", "Monitor"],
        ["process.Xform", "View"],
        ["process.Xform", "ViewSelector"],
        ["process.Xform", "Stat"],
        ["process.Xform", "ImageClipper"],
        ["process.Xform", "Subform"],
        ["process.Xform", "Widget"],
        ["process.Xform", "Source"],
        ["process.Xform", "SourceText"],
        ["process.Xform", "SubSource"]
    ];
    MWF.xDesktop.requireApp(modules, null, function(){
        if (callback) callback();
    });
};


// MWF.xDesktop.requireApp("process.Xform", "Label", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Textfield", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Number", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Personfield", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Orgfield", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Calendar", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Textarea", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Opinion", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Select", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Radio", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Checkbox", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Button", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Combox", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Address", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Table", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Datagrid", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Tab", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Tree", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Iframe", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Htmleditor", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Office", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Attachment", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Actionbar", null, false);
// MWF.xDesktop.requireApp("process.Xform", "sidebar", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Log", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Monitor", null, false);
// MWF.xDesktop.requireApp("process.Xform", "View", null, false);
// MWF.xDesktop.requireApp("process.Xform", "ViewSelector", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Stat", null, false);
// MWF.xDesktop.requireApp("process.Xform", "ImageClipper", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Subform", null, false);
// MWF.xDesktop.requireApp("process.Xform", "Source", null, false);
// MWF.xDesktop.requireApp("process.Xform", "SourceText", null, false);
// MWF.xDesktop.requireApp("process.Xform", "SubSource", null, false);

MWF.xApplication.process.Xform.Div = MWF.APPDiv =  new Class({
	Extends: MWF.APP$Module
});

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
//MWF.xApplication.process.Xform.Image = MWF.APPImage =  new Class({
//	Extends: MWF.APP$Module
//});
MWF.xApplication.process.Xform.Image = MWF.APPImage =  new Class({
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
    },
    reset: function(){
        this._loadUserInterface();
    }
});



MWF.xApplication.process.Xform.Html = MWF.APPHtml =  new Class({
	Extends: MWF.APP$Module,
	load: function(){
	    debugger;
		this.node.insertAdjacentHTML("beforebegin", this.json.text);
		this.node.destroy();
	}
});

