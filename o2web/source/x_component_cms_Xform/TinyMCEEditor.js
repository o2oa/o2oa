MWF.xDesktop.requireApp("process.Xform", "TinyMCEEditor", null, false);
MWF.xApplication.cms.Xform.TinyMCEEditor = MWF.CMSTinyMCEEditor = new Class({
    Extends: MWF.APPTinyMCEEditor,
    getImageUploadOption: function(){
        return {
            localImageMaxWidth : 2000,
            reference: this.form.businessData.document.id,
            referenceType: "cmsDocument"
        };
    },
    getEditorId: function(){
        return this.form.businessData.document.id +"_"+this.json.id.split(".").join("_") + "_" + (layout.mobile ? "mobile" : "pc");
    },
    getText: function () {
        return (this.editor && this.editor.getContent) ? this.editor.getContent({format: 'text'}) : "";
    },
    getImages: function () {
        if( !this.editor || !this.editor.getBody || !this.editor.getBody() )return [];
        return this.editor.getBody().getElementsByTagName("img");
    },
    getImageIds: function () {
        var result = [];
        var images = this.getImages();
        for (var i = 0; i < images.length; i++) {
            var img = images[i];
            if (img.getAttribute("data-id")) {
                result.push(img.getAttribute("data-id"))
            }
        }
        return result;
    },
    _loadStyles: function () {
        if (this.json.styles) this.node.setStyles(this.json.styles);
        this.node.setStyle("overflow", "hidden");
    },
    validationConfigItem: function (routeName, data) {
        var flag = (data.status == "all") ? true : (routeName == "publish");
        if (flag) {
            var n = this.getData();
            var v = (data.valueType == "value") ? n : n.length;
            switch (data.operateor) {
                case "isnull":
                    if (!v) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v > data.value) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v < data.value) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v == data.value) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v != data.value) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value) != -1) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value) == -1) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
}); 