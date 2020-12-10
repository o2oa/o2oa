MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Common", null, false);
MWF.xApplication.cms.FormDesigner.Module.Common = MWF.CMSFCCommon = new Class({
    Extends: MWF.FCCommon,
    Implements : [MWF.CMSFCMI],
    setCustomStyles: function(){
        var border = this.node.getStyle("border");
        this.node.clearStyles();
        var styles = this.node.getStyles("display", "padding");
        this.node.setStyles(this.css.moduleNode);
        var style = Object.clone(this.json.styles);
        //style = Object.merge(style, styles);
        if (styles.display.toString().toLowerCase()==="inline"){
            if (!style.display) style.display = "inline-block";
            if (!style.padding && !style["padding-left"] && !style["padding-right"]) style.padding = "0px 2px";
        }
        if (this.json.tagName==="button"){
            if (!style["min-height"]) style["min-height"] = "20px";
        }

        if (this.initialStyles) this.node.setStyles(this.initialStyles);
        this.node.setStyle("border", border);

        if (style) Object.each(style, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
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

            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                if (key){
                    if (key.toString().toLowerCase()==="display"){
                        if (value.toString().toLowerCase()==="none"){
                            this.node.setStyle("opacity", 0.3);
                        }else{
                            this.node.setStyle("opacity", 1);
                            this.node.setStyle(key, value);
                        }
                    }else{
                        this.node.setStyle(key, value);
                    }
                }
            }
        }.bind(this));
    }
});

