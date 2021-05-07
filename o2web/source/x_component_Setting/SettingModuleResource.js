MWF.xApplication.Setting.ResourceModuleDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.ui_moduleSetting_resource);

        this.uploadTitleNode = new Element("div",{"styles":this.css.explorerContentItemTitleNode}).inject(this.contentAreaNode);
        this.uploadTitleNode.set("text",this.lp.resource_upload);
        this.uploadFileNode = new Element("input",{"type":"file","styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);

        this.isReplaceTitleNode = new Element("div",{"styles":this.css.explorerContentItemTitleNode}).inject(this.contentAreaNode);
        this.isReplaceTitleNode.set("text",this.lp.resource_replace);
        this.isReplaceDescNode = new Element("div",{"styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);
        this.isReplaceDescNode.set("text",this.lp.resource_replaceDesc);
        this.isReplaceNode = new Element("div",{"styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);
        this.isReplaceSelectNode = new Element("select").inject(this.isReplaceNode);
        new Element("option",{"value":false,"text":this.lp.resource_replace_yes}).inject(this.isReplaceSelectNode);
        new Element("option",{"value":true,"text":this.lp.resource_replace_no}).inject(this.isReplaceSelectNode);

        this.filePathTitleNode = new Element("div",{"styles":this.css.explorerContentItemTitleNode}).inject(this.contentAreaNode);
        this.filePathTitleNode.set("text",this.lp.resource_filePath);
        this.filePathDescNode = new Element("div",{"styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);
        this.filePathDescNode.set("text",this.lp.resource_filePathDesc);
        this.filePathNode = new Element("div",{"styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);
        this.filePathInputNode = new Element("input", {"styles": {"width": "500px"}}).inject(this.filePathNode);

        this.submitNode = new Element("div",{"styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);
        this.submitBtnNode = new Element("button",{"styles":this.css.explorerContentButtonNode,"text":this.lp.ok}).inject(this.submitNode);
        this.submitNode.setStyle("margin-top","40px");

        this.submitBtnNode.addEvent("click",function () {
            var files = this.uploadFileNode.files;
            if (files.length) {
                var file = files[0];
                var asNew = this.isReplaceSelectNode.get("value")
                var formData = new FormData();
                formData.append("file", file);
                formData.append("fileName", file.name);
                formData.append("filePath", this.filePathInputNode.get("value"));

                o2.Actions.load("x_program_center").ModuleAction.dispatchResource(asNew, formData, null, function (json){
                    this.app.notice(this.lp.resource_success, "success", this.appContentNode);
                }.bind(this),false);
            }
        }.bind(this))
    }
});

