MWF.xApplication = MWF.xApplication || {};

MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.FormDesigner = MWF.xApplication.cms.FormDesigner || {};

MWF.xDesktop.requireApp("portal.PageDesigner", "Import", null, false);

MWF.xApplication.cms.FormDesigner.Import = MWF.CMSFormImport = new Class({
    Extends : MWF.FormImport
});
MWF.CMSFormImport.O2 = new Class({
    Extends: MWF.FormImport.O2
});
MWF.CMSFormImport.Html = new Class({
    Extends: MWF.FormImport.Html,
    parseImplodeCSS: function(css, doc, callback){
        var rex = /(url\(.*\))/g;
        var match;
        while ((match = rex.exec(css)) !== null) {
            var pic = match[0];
            var len = pic.length;
            var s = pic.substring(pic.length-2, pic.length-1);
            var n = (s==="'" || s==="\"") ? 2 : 1;
            pic = pic.substring(pic.lastIndexOf("/")+1, pic.length-n);
            //var root = (this.options.type==="portal") ? "x_portal_assemble_surface" : "x_processplatform_assemble_surface";
            var root = "x_cms_assemble_control";
            var url = root + o2.Actions.get(root).action.actions.readFile.uri;
            url = url.replace("{flag}", pic);
            url = url.replace("{applicationFlag}", this.form.json.application || this.form.json.portal);
            url = "url('"+url+"')";
            var len2 = url.length;

            css = css.substring(0, match.index) + url + css.substring(rex.lastIndex, css.length);
            rex.lastIndex = rex.lastIndex + (len2-len);
        }
        return css;
    },
    convertImgNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Image", (subNode.get("id") || "image"), subNode, function(id, moduleData){
            debugger;
            var src = subNode.get("src");
            if (src){
                //var root = (this.options.type==="portal") ? "x_portal_assemble_surface" : "x_processplatform_assemble_surface";
                var root = "x_cms_assemble_control";
                var pic = src.substring(src.lastIndexOf("/")+1, src.length);
                var url = root + o2.Actions.get(root).action.actions.readFile.uri;
                url = url.replace("{flag}", pic);
                url = url.replace("{applicationFlag}", this.form.json.application || this.form.json.portal);
                moduleData.properties.src = url;
                subNode.set("src", url);
            }
            subNode.set({"mwftype": "img", "id": id});
        }.bind(this));
        return subNode;
    }
});
MWF.CMSFormImport.Office = new Class({
    Extends: MWF.CMSFormImport.Html,
    options: {
        "stylePath": "/x_component_portal_PageDesigner/$Import/{style}/style_office.css"
    },
    init: function(){
        this.inforText = this.form.designer.lp.importOffice_infor;
        this.inforText2 = this.form.designer.lp.importOffice_infor2;
        this.panelTitle = this.form.designer.lp.importOffice;
        this.panelWidth = 800;
        this.panelHeight = 240;
        this.editorMode = "html";
    },
    loadEditor: function(){
        //this.contentHtml
        if (this.contentCss) this.contentCss.destroy();
        if (this.inforText2Node) this.inforText2Node.destroy();

        this.file = new Element("input.importFile", {
            "type": "file",
            "accept": ".doc,.docx,.xls,.xlsx"
        }).inject(this.contentHtml);
    },
    loadEvent: function(){
        this.cancelNode.addEvent("click", function(){
            this.implodePanel.closePanel();
        }.bind(this));
        this.okNode.addEvent("click", function(e){
            var files = this.file.files;
            if (!files.length){
                this.form.designer.notice(this.form.designer.lp.implodeOfficeEmpty, "error", this.node);
                return false;
            }
            var _self = this;
            this.form.designer.confirm("warn", e, this.form.designer.lp.implodeConfirmTitle, this.form.designer.lp.implodeConfirmText, 400, 100, function(){
                _self.implodeOffice(files);
                this.close();
            }, function(){
                this.close();
            });
        }.bind(this));
    },
    implodeOffice: function(files){
        var file = files.item(0);
        var formData = new FormData();
        formData.append('file', file);

        MWF.Actions.get("x_general_assemble_control").convertHtml(formData, file, function(json){
            var html = json.data.value;
            this.implode(html);
        }.bind(this));
    }
});
MWF.CMSFormImport.create = function(type, form, options){
    return new MWF.CMSFormImport[type.capitalize()](form, options);
};