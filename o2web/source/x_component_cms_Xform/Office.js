MWF.xDesktop.requireApp("process.Xform", "Office", null, false);
MWF.xApplication.cms.Xform.Office = MWF.CMSOffice =  new Class({
	Extends: MWF.APPOffice,
    getFormId: function(){
        var id = this.form.businessData.document.id;
        return "form"+this.json.id+id;
    },
    getFileName: function(){
        var ename = "docx";
        switch (this.json.officeType){
            case "word":
                ename = "docx";
                break;
            case "excel":
                ename = "xlsx";
                break;
            case "ppt":
                ename = "pptx";
        }
        var id = this.form.businessData.document.id;
        return "file"+this.json.id+id+"."+ename;
    },
    getOfficeObjectId: function(){
        var id = this.form.businessData.document.id;
        return "NTKOOCX"+this.json.id+id;
    },
    getFileInputName: function(){
        var id = this.form.businessData.document.id;
        return "fileInput"+this.json.id+id;
    },
    getFile: function(site){
        var file = null;
        atts = this.form.businessData.attachmentList || [];
        for (var i=0; i<atts.length; i++){
            //if ((atts[i].name===fileName) || (atts[i].site===this.json.id)){
            //if (atts[i].site===this.json.id){
            if (atts[i].site===site){
                file = atts[i];
                break;
            }
        }
        return file
    },
    getTempleteUrl: function(){
        //return "/x_desktop/temp/杭州城管委文件.doc";
        if (this.json.template){
            var root = "";
            var flag = this.json.template.substr(0,1);
            if (flag==="/"){
                root = this.json.template.substr(1, this.json.template.indexOf("/", 1)-1);
            }else{
                root = this.json.template.substr(0, this.json.template.indexOf("/"));
            }
            if (["x_cms_assemble_control"].indexOf(root.toLowerCase())!==-1){
                var host = MWF.Actions.getHost(root);
                return (flag==="/") ? host+this.json.template : host+"/"+this.json.template
            }
        }
        return this.json.template;
    },
    getOfficeFileUrl: function(){
        var fileName = this.getFileName();

        this.readSite = this.json.id;
        if (this.json.fileSite && this.json.fileSite.code){
            this.readSite = this.form.Macro.exec(this.json.fileSite.code, this);
        }
        var file = this.getFile(this.readSite);
        if (!file) if (this.readSite !== this.json.id) file = this.getFile(this.json.id);

        if (file){
            this.file = file;
            var url = "";
            url = this.form.documentAction.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(file.id));
            return this.form.documentAction.action.address+url.replace("{documentid}", encodeURIComponent(this.form.businessData.document.id));
        }else{
            return this.getTempleteUrl();
        }
    },


    createMenuAction: function(id, title, img){
        var title = title || MWF.xApplication.process.Xform.LP[id];
        return new Element("div", {
            "MWFnodeid": id,
            "MWFnodetype": "MWFToolBarButton",
            "MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+img,
            "title": title,
            "MWFButtonAction": "menuAction",
            "MWFButtonText": title
        }).inject(this.menuNode);
    },
    createMenuActionMenu: function(id, title, img){
        var title = title || MWF.xApplication.process.Xform.LP[id];
        return new Element("div", {
            "MWFnodeid": id,
            "MWFnodetype": "MWFToolBarMenu",
            "MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+img,
            "title": title,
            "MWFButtonAction": "menuAction",
            "MWFButtonText": title
        }).inject(this.menuNode);
    },
    createMenuActionMenuItem: function(id, title, img, action){
        return new Element("div", {
            "MWFnodeid": id,
            "MWFnodetype": "MWFToolBarMenuItem",
            "MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+img,
            "title": title,
            "MWFButtonAction": action,
            "MWFButtonText": title
        }).inject(this.menuNode);
    },

    openAttachment: function(id, site, name){
        if (!this.openedAttachment || this.openedAttachment.id!==id){
            this.save();
            MWF.Actions.get("x_cms_assemble_control").getAttachmentUrl(id, this.form.businessData.document.id, function(url){
                this.openedAttachment = {"id": id, "site": site, "name": name};
                this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
            }.bind(this));
        }
    },
    openOfficeHistory: function(e, dlg){
        var fileName = e.target.getParent().get("value");
        url = this.form.documentAction.action.actions.getAttachmentData.uri;
        url = url.replace("{id}", encodeURIComponent(fileName));
        url = this.form.documentAction.action.address+url.replace("{documentid}", encodeURIComponent(this.form.businessData.document.id));
        dlg.close();
        this.save();

        this.officeOCX.BeginOpenFromURL(url, true, true);
        this.historyMode = true;

        if (button){
            button.setText(MWF.xApplication.process.Xform.LP.menu_hideHistory)
        }
    },
    save: function(history){
        //if (!this.uploadFileAreaNode) this.createUploadFileNode();
        if (!this.readonly){
            if (this.historyMode) return true;
            if (!this.officeForm) return true;

            this.fireEvent("beforeSave");
            try{
                if (this.openedAttachment){
                    this.officeForm.getElement("input").set("value", this.openedAttachment.site);

                    url = this.form.documentAction.action.actions.replaceAttachment.uri;
                    url = url.replace("{id}", this.openedAttachment.id);
                    url = this.form.documentAction.action.address+url.replace("{documentid}", this.form.businessData.document.id);

                    this.officeOCX.SaveToURL(url, "file", "", this.openedAttachment.name, this.getFormId());

                }else{
                    if (history){
                        if (this.json.isHistory) this.saveHistory();
                    }
                    //this.saveHTML();
                    this.officeForm.getElement("input").set("value", this.json.id);
                    var url = "";
                    if (this.file){
                        url = this.form.documentAction.action.actions.replaceAttachment.uri;
                        url = url.replace("{id}", this.file.id);
                        url = this.form.documentAction.action.address+url.replace("{documentid}", this.form.businessData.document.id);

                        this.officeOCX.SaveToURL(url, "file", "", this.getFileName(), this.getFormId());
                    }else{
                        url = this.form.documentAction.action.actions.uploadAttachment.uri;
                        url = this.form.documentAction.action.address+url.replace("{id}", this.form.businessData.document.id);

                        this.officeOCX.SaveToURL(url, "file", "", this.getFileName(), this.getFormId());
                        this.form.documentAction.getDocument(this.form.businessData.document.id, function(json){
                            this.form.businessData.attachmentList = json.data.attachmentList;
                            this.getOfficeFileUrl();
                        }.bind(this));
                    }
                }
            }catch (e){}
            this.fireEvent("afterSave");
        }
    },
    getHistoryFileName: function(){
        var ename = "docx";
        switch (this.json.officeType){
            case "word":
                ename = "docx";
                break;
            case "excel":
                ename = "xlsx";
                break;
            case "ppt":
                ename = "pptx";
        }
        //var id = (this.form.businessData.work) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        var activity = "";
        var name = MWF.name.cn(layout.session.user.name);
        var d = Date.parse(new Date());
        var dText = d.format("%Y-%m-%d %H:%M");
        return activity+"("+name+")-"+dText+"."+ename;
    },
    saveHistory: function(){
        var fileName = this.getHistoryFileName();
        this.officeForm.getElement("input").set("value", this.json.id+"history");
        url = this.form.documentAction.action.actions.uploadAttachment.uri;
        url = this.form.documentAction.action.address+url.replace("{documentid}", this.form.businessData.document.id);
        this.officeOCX.SaveToURL(url, "file", "", fileName, this.getFormId());
    },
    getHTMLFileName: function(){
        //var id = (this.form.businessData.work) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        var id = this.form.businessData.document.id;
        return id+this.json.id+".mht";
    },
    saveHTML: function(){
        this.officeForm.getElement("input").set("value", this.json.id+"$view");

        var file = null;
        for (var i=0; i<this.form.businessData.attachmentList.length; i++){
            var att = this.form.businessData.attachmentList[i];
            if (att.site==this.json.id+"$view"){
                file = att;
            }
        }

        var fileName = (file) ? file.name : this.getHTMLFileName();
        this.officeForm.getElement("input").getNext().set("value", fileName);
        if (file){
            url = this.form.documentAction.action.actions.replaceAttachment.uri;
            url = url.replace("{id}", file.id);
            url = this.form.documentAction.action.address+url.replace("{documentid}", this.form.businessData.document.id);
        }else{
            url = this.form.documentAction.action.actions.uploadAttachment.uri;
            url = this.form.documentAction.action.address+url.replace("{id}", this.form.businessData.document.id);
        }

        //this.officeOCX.PublishAsHTMLToURL(url, "file", "", fileName, this.getFormId());
        this.officeOCX.SaveAsOtherFormatToURL(1, url, "file", "", fileName, this.getFormId());
        //this.officeOCX.PublishAsPDFToURL(url, "file", "", fileName, this.getFormId());
    },

    getHTMLFileUrl: function(name){
        var fileName = name || this.getHTMLFileName();

        var file = null;
        atts = this.form.businessData.attachmentList;
        for (var i=0; i<atts.length; i++){
            if ((atts[i].name===fileName) || (atts[i].site===this.json.id+"$view")){
                file = atts[i];
                break;
            }
        }
        if (file){
            //this.file = file;
            var url = "";
            url = this.form.documentAction.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(file.id));
            return this.form.documentAction.action.address+url.replace("{documentid}", encodeURIComponent(this.form.businessData.document.id));
        }else{
            return this.getTempleteUrl();
        }
    },

    validationMode: function(){},
    validation: function(){return true},
	loadOfficeNotActive: function(){
        var fileName = this.getFileName();
        var htmlName = "";

        var isHtml = false;
        for (var i=0; i<this.form.businessData.attachmentList.length; i++){
            var att = this.form.businessData.attachmentList[i];
            if (att.site==this.json.id+"$view"){
                htmlName = att.name;
            }
        }
        if (false){
            this.node.setStyles({
                "min-height": "600px",
                "padding": "0px",
                "border": "0px solid #999999",
                "background-color": "#e6e6e6",
                "overflow": "hidden"
            });
            if (this.node.getSize().y<800) this.node.setStyle("height", "800px");
            //this.node.setStyles(this.json.styles);
            var wordNode = new Element("div", {
                "styles": {
                    "padding": "40px",
                    "border": "1px solid #999999",
                    "background-color": "#e6e6e6",
                    "overflow": "auto"
                }
            }).inject(this.node);
            var size = this.node.getSize();
            var y = (size.y-80-80);
            wordNode.setStyle("height", ""+y+"px");

            var node = new Element("div", {
                "styles": {
                    "width": "90%",
                    "height": "1900px",
                    "margin": "auto",
                    "background-color": "#ffffff"
                }
            }).inject(wordNode);

            var iframe = new Element("iframe", {
                "styles": {
                    "width": "100%",
                    "height": "100%",
                    "min-height": "600px",
                    "overflow": "auto",
                    "border": "1px solid #cccccc"
                }
                //"src": this.getHTMLFileUrl(htmlName)
            }).inject(node);
            //alert(iframe.contentWindow.document.body.firstChild);
            iframe.contentWindow.document.addEventListener("readystatechange", function(){
                this.body.style.padding = "20px 40px";
            });
            // iframe.contentWindow.document.onreadystatechange = function(){
            //     alert("onreadystatechange"+ this.readyState );
            //     alert(this.body.firstChild);
            //     this.body.style.padding = "20px 40px";
            // };
            iframe.set("src", this.getHTMLFileUrl(htmlName));

            // iframe.contentWindow.document.body.firstChild.style.paddingTop = "20px";
            // iframe.contentWindow.document.body.firstChild.style.paddingBottom = "20px";
            // iframe.contentWindow.document.body.firstChild.style.paddingLeft = "40px";
            // iframe.contentWindow.document.body.firstChild.style.paddingRight = "40px";


        }else{
            this.node.setStyles({
                "overflow": "hidden",
                "background-color": "#f3f3f3",
                "min-height": "24px",
                "padding": "18px"
            });

            var str = this.getData();
            if (layout.mobile || COMMON.Browser.Platform.isMobile){
                if (str.length>300) str = str.substr(0,300)+"……";
            }

            var text = new Element("div", {
                "text": str
            }).inject(this.node);
        }
        var text = MWF.xApplication.process.Xform.LP.openOfficeInfor;
        text = text.replace("{type}", this.json.officeType);
            var icon = new Element("div", {
                "styles": {
                    "width": "200px",
                    "height": "24px",
                    "margin": "auto",
                    "margin-top": "18px",
                    "padding-left": "30px",
                    "font-size": "16px",
                    "font-weight": "bold",
                    "color": "#2b5797",
                    "font-family": "Gadugi",
                    "cursor": "pointer",
                    "background": "url("+this.form.path+""+this.form.options.style+"/icon/"+this.json.officeType+".png"+") no-repeat left center"
                },
                "text": text
            }).inject(this.node);

            var url = this.getOfficeFileUrl();
            if (!url){
                this.node.setStyle("display", "none");
            }
            icon.addEvent("click", function(){
                var url = this.getOfficeFileUrl();
                if (url){
                    if (window.o2android){
                        window.o2android.openDocument(url);
                    }else if(window.webkit){
                        window.webkit.messageHandlers.openDocument.postMessage(url);
                    }else{
                        window.open(url);
                    }
                }
            }.bind(this));

	}
}); 