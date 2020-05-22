MWF.xApplication.Setting.ServiceModuleDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.ui_moduleSetting_service);

        this.uploadTitleNode = new Element("div",{"styles":this.css.explorerContentItemTitleNode}).inject(this.contentAreaNode);
        this.uploadTitleNode.set("text",this.lp.resource_upload);
        this.uploadFileNode = new Element("input",{"type":"file","styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);

        this.ctlTitleNode = new Element("div",{"styles":this.css.explorerContentItemTitleNode}).inject(this.contentAreaNode);
        this.ctlTitleNode.set("text",this.lp.service_ctl);
        this.ctlNode = new Element("div",{"styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);
        this.ctlSelectNode = new Element("select").inject(this.ctlNode);
        new Element("option",{"value":"customJar","text":"customJar"}).inject(this.ctlSelectNode);
        new Element("option",{"value":"customWar","text":"customWar"}).inject(this.ctlSelectNode);
        new Element("option",{"value":"storeJar","text":"storeJar"}).inject(this.ctlSelectNode);
        new Element("option",{"value":"storeWar","text":"storeWar"}).inject(this.ctlSelectNode);

        this.nodeTitleNode = new Element("div",{"styles":this.css.explorerContentItemTitleNode}).inject(this.contentAreaNode);
        this.nodeTitleNode.set("text",this.lp.service_node);
        this.serverNode = new Element("div",{"styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);
        this.nodeSelectNode = new Element("select").inject(this.serverNode);
        new Element("option", {
            "value": "*",
            "text": this.lp.service_allNode
        }).inject(this.nodeSelectNode);
        o2.Actions.load("x_program_center").CommandAction.getNodeInfoList(
            function( json ){
                var nodeList = json.data.nodeList;
                nodeList.each(function (node) {
                    new Element("option", {
                        "value": node.node.nodeAgentPort,
                        "text": node.nodeAddress
                    }).inject(this.nodeSelectNode);
                }.bind(this));
            }.bind(this),null, false
        );


        this.submitNode = new Element("div",{"styles":this.css.explorerContentInputInforNode}).inject(this.contentAreaNode);
        this.submitBtnNode = new Element("button",{"styles":this.css.explorerContentButtonNode,"text":this.lp.ok}).inject(this.submitNode);
        this.submitNode.setStyle("margin-top","40px");

        this.submitBtnNode.addEvent("click",function () {
            var files = this.uploadFileNode.files;
            if (files.length) {
                var file = files[0];
                var nodePort = this.nodeSelectNode.getElement("option:selected").get("value");
                var nodeName = this.nodeSelectNode.getElement("option:selected").get("text");
                if(nodePort==="*") nodeName = "*";
                var formData = new FormData();
                formData.append("file", file);
                formData.append("fileName", file.name);
                formData.append("ctl", this.ctlSelectNode.get("value"));
                formData.append("nodeName", nodeName);
                formData.append("nodePort", nodePort);

                o2.Actions.load("x_program_center").CommandAction.upload(formData,null,function (json){
                    this.app.notice(this.lp.service_success, "success", this.appContentNode);
                }.bind(this),null,false);
            }
        }.bind(this))
    }
});

