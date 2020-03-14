MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Office = MWF.APPOffice =  new Class({
	Extends: MWF.APP$Module,
	isActive: false,
    options:{
        "ProductCaption": "O2",
        "ProductKey": "EDCC626CB85C9A1D3E0D7BDDDC2637753C596725",
        "makerCaption": "浙江兰德纵横网络技术股份有限公司",
        "makerKey": "E138DABB4AC26C2D8E09FAE59AB3BDE87AFB9D7B",
        "version": "5.0.4.0",
        "clsid": "A64E3073-2016-4baf-A89D-FFE1FAA10EC0",
        "codeBase": "/o2_lib/officecontrol/5040/OfficeControl.cab",

        "clsid64": "A64E3073-2016-4baf-A89D-FFE1FAA10EE1",
        "codeBase64": "/o2_lib/officecontrol/5040/ofctnewclsid.cab",

        "pdfType": "PDF.NtkoDocument",
        "pdfVersion": "4.0.0.3",
        "pdfCodeBase": "/o2_lib/officecontrol/5040/ntkooledocall.cab",
        "pdfCodeBase64": "/o2_lib/officecontrol/5040/ntkooledocall64.cab",

        "files": ["doc","docx","dotx","dot","xls","xlsx","xlsm","xlt","xltx","pptx","ppt","pot","potx","potm","pdf"],

        "moduleEvents": ["redFile",
            "afterOpen",
            "afterOpenOffice",
            "afterCreate",
            "seal",
            "beforeSave",
            "afterSave",
            "afterCloseOffice",
            "load"
        ]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
        this.openedAttachment = null;
    },
	_loadUserInterface: function(){
		this.node.empty();
		this.node.setStyles({
			"min-height": "100px"
		});
        // this.isActive = true;
        //if (Browser.name==="ie" || Browser.name==="chrome" || Browser.name==="firefox"){
		if (Browser.name==="ie"){
            this.isActive = true;
            this.file = null;
            if (!this.form.officeList) this.form.officeList=[];
            this.form.officeList.push(this);
        }
	},
	
	_afterLoaded: function(){
        if (!this.json.isNotLoadNow){
            this.loadOffice();
        }
        //this.fireEvent("load");
	},
    loadOffice: function(file){
	    if (!this.officeLoaded){
            if (!this.isActive){
                this.loadOfficeNotActive();
            }else{
                MWF.getJSON("/o2_lib/officecontrol/config.json", function(json){
                    this.officeConfig = json;
                }.bind(this), false);
                this.loadOfficeContorl(file);
            }
            this.officeLoaded = true;
        }else{
            if (this.officeOCX) this.officeOCX.BeginOpenFromURL(file, true, this.readonly);
        }
    },
    getProgID: function(){
        switch (this.json.officeType){
            case "word":
                return "Word.Document";
            case "excel":
                return "Excel.Sheet";
            case "ppt":
                return "PowerPoint.Show";
        }
        return "Word.Document"
    },
	defaultParam: function(readonly){
		var o = {
			"ProductCaption": this.json.productCaption || this.options.ProductCaption,
            "ProductKey": this.json.productKey || this.options.ProductKey,
            "MakerCaption": this.officeConfig.makerCaption || this.json.makerCaption || this.options.makerCaption,
            "MakerKey": this.officeConfig.makerKey || this.options.makerKey || this.options.MakerKey,
			"Titlebar": "0",
			"Menubar": "0",
			"ToolBars": (readonly) ? "0" : "1",
			"Statusbar": "0",
            "IsUseUTF8URL": "1",
            "IsUseUTF8Data": "1",
			"BorderStyle": (readonly) ? "0" : "0",
			"IsNoCopy": "0",
            "IsResetToolbarsOnOpen": "1",
            "FileNew": "0",
            "FileOpen": "1",
            "FileClose": "0",
            "FileSave": "0",
            "FileProperties": "0"
		};
        return o;
	},
    loadOfficeContorl: function(file){
        if (this.node.getSize().y<800) this.node.setStyle("height", "800px");

        if (!layout.desktop.offices) layout.desktop.offices = {};
        layout.desktop.offices[this.getOfficeObjectId()] = this;

        if (this.readonly){
            this.loadOfficeRead(file);
        }else if (this.json.isReadonly){
            this.readonly  = true;
            this.loadOfficeRead(file);
        }else{
            if (this.json.readScript && this.json.readScript.code){
                var flag = this.form.Macro.exec(this.json.readScript.code, this);
                if (flag){
                    this.readonly = true;
                    this.loadOfficeRead(file);
                }else{
                    this.loadOfficeEdit(file);
                }
            }else{
                this.loadOfficeEdit(file);
            }
        }
	},
    loadOfficeSpacer: function(){
		var size = this.node.getSize();

        this.officeNode = new Element("div#officeNode", {
            "styles": this.form.css.officeAreaNode
        }).inject(this.node);
        var y = size.y-40;
        this.officeNode.setStyle("height", ""+y+"px");

        this.form.app.addEvent("uncurrent", function(){
            var display = this.officeNode.getStyle("display");
            this.officeNode.store("officeDisplay", display);
            this.officeNode.setStyle("display", "none");
		}.bind(this));
        this.form.app.addEvent("current", function(){
            var display = this.officeNode.retrieve("officeDisplay");
            if (display) this.officeNode.setStyle("display", display);
            if (this.officeOCX) this.officeOCX.Activate(true);
        }.bind(this));

        this.form.app.addEvent("queryClose", function(){
            this.fireEvent("queryClose");
            var id = this.getOfficeObjectId();
            layout.desktop.offices[id] = null;
            delete layout.desktop.offices[id];
        }.bind(this));
	},
    hide: function(){
        if (this.officeNode.getStyle("display")!="none"){
            var display = this.officeNode.getStyle("display");
            this.officeNode.store("officeDisplay", display);
            this.officeNode.setStyle("display", "none");
        }
    },
    show: function(){
        if ((layout.desktop.currentApp && layout.desktop.currentApp.appId===this.form.app.appId) || this.form.app.inBrowser){
            var display = this.officeNode.retrieve("officeDisplay");
            if (display) this.officeNode.setStyle("display", display);
            if (this.officeOCX) this.officeOCX.Activate(true);
        }
    },
    isCover: function(node){

    },

    getFormId: function(){
        var id = (!this.form.businessData.workCompleted) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
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
        var id = (!this.form.businessData.workCompleted) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        return "file"+this.json.id+id+"."+ename;
    },
    getOfficeObjectId: function(){
        var id = (!this.form.businessData.workCompleted) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        return "NTKOOCX"+this.json.id+id;
    },
    getFileInputName: function(){
        var id = (!this.form.businessData.workCompleted) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        return "fileInput"+this.json.id+id;
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
            if (["x_processplatform_assemble_surface", "x_portal_assemble_surface"].indexOf(root.toLowerCase())!==-1){
                var host = MWF.Actions.getHost(root);
                return (flag==="/") ? host+this.json.template : host+"/"+this.json.template
            }
        }
        return this.json.template;
    },
    getFile: function(site){
        var file = null;
        atts = this.form.businessData.attachmentList;
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
            if (!this.form.businessData.workCompleted){
                url = this.form.workAction.action.actions.getAttachmentData.uri;
                url = url.replace("{id}", encodeURIComponent(file.id));
                return this.form.workAction.action.address+url.replace("{workid}", encodeURIComponent(this.form.businessData.work.id));
            }else{
                url = this.form.workAction.action.actions.getWorkcompletedAttachmentData.uri;
                url = url.replace("{id}", encodeURIComponent(file.id));
                return this.form.workAction.action.address+url.replace("{workCompletedId}", encodeURIComponent(this.form.businessData.workCompleted.id));
            }
        }else{
            return this.getTempleteUrl();
        }
    },

    editEnabled: function(){
        try {
            this.officeOCX.ActiveDocument.Unprotect();
        }catch(e){}
    },
    docReadonly: function(){
        this.protect(3);
    },
    protect: function(type){
        // wdAllowOnlyComments = 1         //批注
        // wdAllowOnlyFormFields = 2       //填写窗体
        // wdAllowOnlyReading = 3          //只读
        // wdAllowOnlyRevisions = 0        //修订
        // wdNoProtection = -1             //限制编辑样式
        try {
            this.officeOCX.ActiveDocument.Protect(type);
        }catch(e){}
    },
    startRevisions: function(){
        if (this.officeOCX && (this.officeOCX.DocType==1 || this.officeOCX.DocType==6)){
            this.officeOCX.ActiveDocument.Application.UserName = layout.desktop.session.user.name;
            this.officeOCX.ActiveDocument.Application.UserInitials = layout.desktop.session.user.name;

            if (!this.isNew){
                this.officeOCX.ActiveDocument.TrackRevisions = true;
                this.officeOCX.ActiveDocument.showRevisions = false;
            }else{
                this.officeOCX.ActiveDocument.TrackRevisions = false;
                this.officeOCX.ActiveDocument.showRevisions = false;
            }
            this.officeOCX.ActiveDocument.Application.UserName = layout.desktop.session.user.name;

            if( this.officeOCX.ActiveDocument && this.officeOCX.ActiveDocument.Application ){
                if(15==this.officeOCX.getOfficeVer()){//如果是OFFICE 2013则设置Options.UseLocalUserInfo属性为true,TANGER_OCX_OBJ为文档控件对象
                    this.officeOCX.ActiveDocument.Application.Options.UseLocalUserInfo=true;
                    this.officeOCX.WebUserName= layout.desktop.session.user.name;
                }
            }
        }
        //this.officeOCX.FullScreenMode = true;
    },
    stopRevisions: function(accept){
        this.officeOCX.ActiveDocument.TrackRevisions = false;
        this.officeOCX.ActiveDocument.showRevisions = false;
        if (accept) this.officeOCX.ActiveDocument.AcceptAllRevisions();
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
    menuAction: function(button){
        switch (button.buttonID){
            case "menu_new":
                this.officeOCX.CreateNew(this.getProgID());
                break;
            case "menu_openfile":
                this.officeOCX.ShowDialog(1);
                break;
            case "menu_savefile":
                this.officeOCX.ShowDialog(3);
                break;
            case "revisions":
                this.toggleRevisions(button);
                break;
            case "menu_fullscreen":
                this.officeOCX.FullScreenMode = true;
                break;
            case "toolbar":
                var text = (this.officeOCX.ToolBars) ? MWF.xApplication.process.Xform.LP.menu_toolbar_show : MWF.xApplication.process.Xform.LP.menu_toolbar_hide;
                button.setText(text);
                this.officeOCX.ToolBars = !this.officeOCX.ToolBars;
                break;
            case "menu_preview":
                this.officeOCX.PrintPreview();
                break;
            case "menu_showHistory":
                this.showHistory(button);
                break;
            case "menu_redfile":
                this.redFile();
                break;
            case "menu_seal":
                this.seal();
                break;
            case "menu_att":
                button.menu.clearItems();
                button._loadMenuItem(this.createMenuActionMenuItem("", MWF.xApplication.process.Xform.LP.menu_file, "109.png", "openFile"));
                button._loadMenuLine();

                var attcs = this.json.attachmentIds.split(/,\s*|;\s*|，\s*|；\s*/g);
                attcs.each(function(att){
                    this.form.businessData.attachmentList.each(function (attachement) {
                        if(attachement.site===att){
                            if (attachement.control.allowEdit){
                                if (this.options.files.indexOf(attachement.extension.toLowerCase())!==-1){
                                    button._loadMenuItem(this.createMenuActionMenuItem(attachement.id, attachement.name, "14.png", "openAttachment:"+attachement.id+":"+att+":"+attachement.name));
                                }
                            }
                        }
                    }.bind(this));
                    /*var attc = this.form.all[att];
                    if (attc){
                        attc.attachmentController.attachments.each(function(a){
                            if (a.data.control.allowEdit){
                                if (this.options.files.indexOf(a.data.extension.toLowerCase())!==-1){
                                    button._loadMenuItem(this.createMenuActionMenuItem(a.data.id, a.data.name, "14.png", "openAttachment:"+a.data.id+":"+att+":"+a.data.name));
                                }
                            }
                        }.bind(this));
                    }*/
                }.bind(this));
        }
    },
    openFile: function(bt, e, item){
        if (this.openedAttachment){
            this.save();
            this.loadOfficeEdit();
        }
    },
    openAttachment: function(id, site, name){
        if (!this.openedAttachment || this.openedAttachment.id!==id){
            this.save();
            if (this.form.businessData.workCompleted){
                MWF.Actions.get("x_processplatform_assemble_surface").getAttachmentWorkcompletedUrl(id, this.form.businessData.workCompleted.id, function(url){
                    this.openedAttachment = {"id": id, "site": site, "name": name};
                    this.officeOCX.BeginOpenFromURL(url, true, this.readonly || this.json.isAttReadonly );
                }.bind(this));
            }else{
                MWF.Actions.get("x_processplatform_assemble_surface").getAttachmentUrl(id, this.form.businessData.work.id, function(url){
                    this.openedAttachment = {"id": id, "site": site, "name": name};
                    this.officeOCX.BeginOpenFromURL(url, true, this.readonly || this.json.isAttReadonly);
                }.bind(this));
            }
        }
    },
    loadMenu: function(){
        if (!this.isMenuLoad){
            if (this.json.menuEditButtons.length){
                this.menuNode = new Element("div", {"styles": this.form.css.officeMenuNode}).inject(this.node, "top");
                MWF.require("MWF.widget.Toolbar", function(){
                    this.toolbarWidget = new MWF.widget.Toolbar(this.menuNode, {"style": "xform_blue_simple"}, this);

                    if (this.json.menuEditButtons.indexOf("new")!==-1){
                        this.newItem = this.createMenuAction("menu_new", "", "99.png");
                    }

                    if (this.json.menuEditButtons.indexOf("open")!==-1){
                        this.openItem = this.createMenuAction("menu_openfile", "", "77.png");
                    }

                    if (this.json.menuEditButtons.indexOf("save")!==-1){
                        this.saveItem = this.createMenuAction("menu_savefile", "", "67.png");
                    }

                    if (this.json.menuEditButtons.indexOf("revisions")!==-1){
                        var text = MWF.xApplication.process.Xform.LP.menu_revisions_show;
                        try {
                            if (this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup !== 0){
                                text = MWF.xApplication.process.Xform.LP.menu_revisions_hide;
                            }
                        }catch(e){}

                        this.revisionsItem = this.createMenuAction("revisions", text, "76.png");
                    }
                    if (this.json.menuEditButtons.indexOf("fullscreen")!==-1){
                        this.fullscreenItem = this.createMenuAction("menu_fullscreen", "", "4.png");
                    }
                    if (this.json.menuEditButtons.indexOf("toolbar")!==-1){
                        if (!this.readonly){
                            var text = MWF.xApplication.process.Xform.LP.menu_toolbar_show;
                            if (this.officeOCX.ToolBars){
                                text = MWF.xApplication.process.Xform.LP.menu_toolbar_hide;
                            }
                            this.toolbarItem = this.createMenuAction("toolbar", text, "91.png");
                        }

                    }
                    if (this.json.menuEditButtons.indexOf("preview")!==-1){
                        this.fullscreenItem = this.createMenuAction("menu_preview", "", "21.png");
                    }
                    if (this.json.menuEditButtons.indexOf("showHistory")!==-1){
                        atts = this.form.businessData.attachmentList;
                        if (atts.some(function(att){
                                return att.site == this.json.id+"history";
                            }.bind(this))){

                            this.historyItem = this.createMenuAction("menu_showHistory", "", "115.png");
                        }
                    }
                    if (this.json.menuEditButtons.indexOf("redfile")!==-1){
                        if (!this.readonly) this.redItem = this.createMenuAction("menu_redfile", "", "12.png");
                    }
                    if (this.json.menuEditButtons.indexOf("seal")!==-1){
                        if (!this.readonly) this.sealItem = this.createMenuAction("menu_seal", "", "84.png");
                    }

                    if (this.json.isOpenAttachment){
                        if (this.json.attachmentIds){
                            this.sealItem = this.createMenuActionMenu("menu_att", "", "14.png");
                        }
                    }

                    this.toolbarWidget.load();

                }.bind(this));

            }
            this.isMenuLoad = true;
        }
    },
    showHistory: function(button){
        if (this.historyItem.get("text") == MWF.xApplication.process.Xform.LP.menu_hideHistory){
            var url = this.getOfficeFileUrl();
            if (url){
                var id = this.getOfficeObjectId();
                this.addOfficeEvent(id, "OnDocumentOpened(url, doc)", "if (layout.desktop.offices[\""+id+"\"]) layout.desktop.offices[\""+id+"\"].OnDocumentOpened(url, doc);");
                this.addOfficeEvent(id, "AfterOpenFromURL(doc, statusCode)", "if (layout.desktop.offices[\""+id+"\"]) layout.desktop.offices[\""+id+"\"].AfterOpenFromURL(doc, statusCode);");

                button.setText(MWF.xApplication.process.Xform.LP.menu_showHistory);
                this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
                this.historyMode = false;
            }
        }else{
            MWF.require("MWF.xDesktop.Dialog", function(){
                var width = 680;
                var height = 500;
                var p = MWF.getCenterPosition(this.form.app.content, width, height);

                var _self = this;
                var dlg = new MWF.xDesktop.Dialog({
                    "title": MWF.xApplication.process.Xform.LP.menu_showHistory,
                    //"style": "work",
                    "style" : this.form.json.dialogStyle || "user",
                    "top": p.y-100,
                    "left": p.x,
                    "fromTop": p.y-100,
                    "fromLeft": p.x,
                    "width": width,
                    "height": height,
                    "html": "<div></div>",
                    "container": this.form.app.content,
                    "isClose": true,
                    "buttonList": [
                        {
                            "text": MWF.xApplication.process.Xform.LP.close,
                            "action": function(){this.close();}
                        }
                    ],
                    "onPostShow": function(){
                        this.showHistoryContent(dlg, button)
                    }.bind(this)
                });
                dlg.show();
            }.bind(this));
        }
    },
    showHistoryContent: function(dlg, button){
        dlg.content.setStyle("overflow", "auto");
        atts = this.form.businessData.attachmentList;
        var site = this.json.id+"history";
        for (var i=0; i<atts.length; i++){
            if ((atts[i].site===site)){
                //for (var x=0; x<31; x++){
                    file = atts[i];
                    var div = new Element("div", {
                        "styles": {
                            "margin": "20px auto 0px auto",
                            "height": "30px",
                            "line-height": "30px",
                            "width": "80%",
                            "font-size": "16px",
                            "color": "#666666",
                            "border-bottom": "1px solid #CCCCCC"
                        },
                        "value": file.id
                    }).inject(dlg.content);
                    var fileNameNode = new Element("div", {
                        "styles": {"float": "left"},
                        "text": file.name
                    }).inject(div);
                    var buttonNode = new Element("input", {
                        "type": "button",
                        "styles": {"float": "right"},
                        "value": "查看版本",
                        "events": {
                            "click": function(e){
                                this.openOfficeHistory(e, dlg, button);
                            }.bind(this)
                        }
                    }).inject(div);
                //}
            }
        }
    },
    openOfficeHistory: function(e, dlg, button){
        var fileName = e.target.getParent().get("value");
        if (!this.form.businessData.workCompleted){
            url = this.form.workAction.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(fileName));
            url = this.form.workAction.action.address+url.replace("{workid}", encodeURIComponent(this.form.businessData.work.id));
        }else{
            url = this.form.workAction.action.actions.getWorkcompletedAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(fileName));
            url = this.form.workAction.action.address+url.replace("{workCompletedId}", encodeURIComponent(this.form.businessData.workCompleted.id));
        }
        dlg.close();
        this.save();

        this.officeOCX.BeginOpenFromURL(url, true, true);
        this.historyMode = true;

        if (button){
            button.setText(MWF.xApplication.process.Xform.LP.menu_hideHistory)
        }
    },
    seal: function(){
        this.fireEvent("seal");
    },
    redFile: function(){
       // try {
        if (this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter){
            this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 0;
            this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
        }

        this.officeOCX.ActiveDocument.showRevisions = false;
        this.stopRevisions(true);

        this.fireEvent("redFile");
            // this.officeOCX.ActiveDocument.Application.Selection.WholeStory();
            // this.officeOCX.ActiveDocument.Application.Selection.Font.Name = "仿宋";
            // this.officeOCX.ActiveDocument.Application.Selection.Font.Size = 14;
            // this.officeOCX.ActiveDocument.Application.Selection.Cut();
            //
            // this.officeOCX.OpenFromUrl("/x_desktop/temp/1.doc", false);
            //
            // var mark = this.officeOCX.ActiveDocument.Bookmarks("bodyCw");
            //
            // mark.Range.PasteSpecial(false, false, 0, false, 2);
            //mark.Range.Paste();
        // }catch(e){
        //     throw e;
        // }
    },
    showRevisions: function(){
        try {
            if (this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter) {
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 2;
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
            }
            this.officeOCX.ActiveDocument.showRevisions = true;
        }catch(e){}
    },
    hideRevisions: function(){
        try {
            if (this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter) {
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 0;
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
            }
            this.officeOCX.ActiveDocument.showRevisions = false;
        }catch(e){}
    },
    toggleRevisions: function(button){
        var t = this.revisionsItem.get("text");
        if (t===MWF.xApplication.process.Xform.LP.menu_revisions_show){
            button.setText(MWF.xApplication.process.Xform.LP.menu_revisions_hide);

            try {
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 2;
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
            }catch(e){}
            try {
                this.officeOCX.ActiveDocument.showRevisions = true;
            }catch(e){}
        }else{
            button.setText(MWF.xApplication.process.Xform.LP.menu_revisions_show);

            try {
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 0;
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
            }catch(e){}
            try {
                this.officeOCX.ActiveDocument.showRevisions = false;
            }catch(e){}
        }
    },
    afterOpen: function(){
        // this.officeOCX.AddCustomButtonOnMenu(0,"按钮0",false);
        // this.officeOCX.AddCustomButtonOnMenu(1,"按钮1",true,1);
        // this.officeOCX.AddCustomButtonOnMenu(2,"按钮2",true,2);
        // this.officeOCX.AddCustomButtonOnMenu(3,"按钮3",false,3);
        //
        //
        // this.officeOCX.AddCustomToolButton("预览(0)", 10);
        //
        // alert(111);

        if (this.readonly) this.docReadonly();
        if (this.json.trackRevisions==="1") this.startRevisions();

        var scale = (this.readonly) ? this.json.readScale : this.json.editScale;
        if (scale) scale = scale.toInt();
        if (scale){
            this.officeOCX.ActiveDocument.ActiveWindow.ActivePane.View.Zoom.Percentage = scale;
        }

        var display = this.officeNode.getStyle("display");
        //window.setTimeout(function(){
            this.officeOCX.Activate(false);
            this.officeNode.setStyle("display", "none");
            window.setTimeout(function(){
                this.officeNode.setStyle("display", display);
                this.officeOCX.Activate(true);
            }.bind(this), 10);
        //}.bind(this), 10);


        //if (this.officeOCX) this.officeOCX.Activate(true);

        // this.officeNode.scrollIntoView();
        // this.form.app.node.scrollTo(0);

    },

    loadOfficeEditFirefox: function(file){
        if (!this.officeOCX){
            this.loadOfficeSpacer();
            this.node.setStyle("pisition", "absolute");

            var codeBase = this.json.codeBase || this.options.codeBase;
            var version = this.json.version || this.options.version;
            var classid = this.json.clsid || this.options.clsid;

            var objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id='"+this.getOfficeObjectId()+"' " +
                "type='application/ntko-plug' " +
                "style='HEIGHT: 99%; WIDTH: 100%' " +
                "height='99%' width='100%' " +
                "codeBase='"+codeBase+"#version="+version+"' " +
                "classid='{"+classid+"}' ";

            objectHtml += "ForOnSaveToURL='OnComplete2' ";
            objectHtml += "ForOnBeginOpenFromURL='OnComplete' ";
            objectHtml += "ForOndocumentopened='OnComplete3' ";
            objectHtml += "ForOnpublishAshtmltourl='publishashtml' ";

            var pars = this.defaultParam();
            pars = Object.merge(pars, this.json.ntkoEditProperties);
            pars = Object.merge(pars, this.json.editProperties);

            Object.each(pars, function(p, key){
                objectHtml += "_"+key+"='"+p+"'";
            });
            objectHtml += ">";
            objectHtml += "<SPAN STYLE='color:red'>尚未安装NTKO Web Chrome跨浏览器插件。请点击<a href=\"/o2_lib/officecontrol/ntkoplugins.xpi\">安装组件</a></SPAN>";

            objectHtml += "</OBJECT><input type='hidden' value='"+this.json.id+"' name='site'><input style='display:none' name=\"file\" type=\"file\"/></form>";
            this.officeNode.appendHTML(objectHtml);
            this.officeForm = this.officeNode.getFirst();
            this.officeOCX = this.officeNode.getFirst().getFirst();

            this.doOfficeOCXEvents();
        }

        var url = this.getOfficeFileUrl();
        if (url){
            this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
        }else{
            this.officeOCX.CreateNew(this.getProgID());
            this.fireEvent("afterCreate");
        }
    },
    loadOfficeEditChrome: function(file){
        if (!this.officeOCX){
            this.loadOfficeSpacer();
            this.node.setStyle("pisition", "absolute");

            var codeBase = this.officeConfig.codeBase || this.json.codeBase || this.options.codeBase;
            var version = this.officeConfig.version || this.json.version || this.options.version;
            var classid = this.officeConfig.classid || this.json.clsid || this.options.clsid;
            var codeBase64 = this.officeConfig.codeBase64 || this.json.codeBase64 || this.options.codeBase64;
            var classid64 = this.officeConfig.classid64 || this.json.clsid64 || this.options.clsid64;
            var pdfType = this.officeConfig.pdfType || this.json.pdfType || this.options.pdfType;
            var pdfVersion = this.officeConfig.pdfVersion || this.json.pdfVersion || this.options.pdfVersion;
            var pdfCodeBase = this.officeConfig.pdfCodeBase || this.json.pdfCodeBase || this.options.pdfCodeBase;
            var pdfCodeBase64 = this.officeConfig.pdfCodeBase64 || this.json.pdfCodeBase64 || this.options.pdfCodeBase64;

            var objectHtml = "";

            if(window.navigator.platform=="Win64"){
                objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id='"+this.getOfficeObjectId()+"' " +
                    "style='HEIGHT: 99%; WIDTH: 100%' " +
                    "codeBase='"+codeBase64+"#version="+version+"' " +
                    "classid='{"+classid64+"}'>";
            }else{
                objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id='"+this.getOfficeObjectId()+"' " +
                    "style='HEIGHT: 99%; WIDTH: 100%' " +
                    "codeBase='"+codeBase+"#version="+version+"' " +
                    "classid='{"+classid+"}'";
            }

            objectHtml += "ForOnSaveToURL='OnComplete2' ";
            objectHtml += "ForOnBeginOpenFromURL='OnComplete' ";
            objectHtml += "ForOndocumentopened='OnComplete3' ";
            objectHtml += "ForOnpublishAshtmltourl='publishashtml' ";

            var pars = this.defaultParam();
            pars = Object.merge(pars, this.json.ntkoEditProperties);
            pars = Object.merge(pars, this.json.editProperties);

            Object.each(pars, function(p, key){
                objectHtml += "_"+key+"='"+p+"'";
            });
            objectHtml += ">";
            objectHtml += "<SPAN STYLE='color:red'>尚未安装NTKO Web Chrome跨浏览器插件。请点击<a href=\"/o2_lib/officecontrol/ntkoplugins.crx\">安装组件</a></SPAN>";

            objectHtml += "</OBJECT><input type='hidden' value='"+this.json.id+"' name='site'><input style='display:none' name=\"file\" type=\"file\"/></form>";
            this.officeNode.appendHTML(objectHtml);
            this.officeForm = this.officeNode.getFirst();
            this.officeOCX = this.officeNode.getFirst().getFirst();

            if(window.navigator.platform=="Win64"){
                this.officeOCX.AddDocTypePlugin(".pdf",pdfType,pdfVersion,pdfCodeBase64,51,true);
            }else{
                this.officeOCX.AddDocTypePlugin(".pdf",pdfType,pdfVersion,pdfCodeBase,51,true);
            }

            //this.doOfficeOCXEvents();
        }

        var url = this.getOfficeFileUrl();
        if (url){
            this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
        }else{
            this.officeOCX.CreateNew(this.getProgID());
            this.fireEvent("afterCreate");
        }
    },
    loadOfficeEdit: function(file){
        if (Browser.name==="chrome"){
            this.loadOfficeEditChrome(file);
        }else if (Browser.name==="firefox") {
            this.loadOfficeEditFirefox(file);
        }else{
            this.loadOfficeEditIE(file);
        }
        this.openedAttachment = null
    },
    getAutoSavedAttachments: function(){
        this.autoSavedAttachments = [];
        this.form.businessData.attachmentList.each(function(att){
            if (att.site===this.json.id+"autosave") this.autoSavedAttachments.push(att);
        }.bind(this));
    },
    loadOfficeEditIE: function(file){
        if (!this.officeOCX){
            this.loadOfficeSpacer();
            //this.loadMenu();
            this.node.setStyle("pisition", "absolute");

            var codeBase = this.officeConfig.codeBase || this.json.codeBase || this.options.codeBase;
            var version = this.officeConfig.version || this.json.version || this.options.version;
            var classid = this.officeConfig.classid || this.json.clsid || this.options.clsid;
            var codeBase64 = this.officeConfig.codeBase64 || this.json.codeBase64 || this.options.codeBase64;
            var classid64 = this.officeConfig.classid64 || this.json.clsid64 || this.options.clsid64;
            var pdfType = this.officeConfig.pdfType || this.json.pdfType || this.options.pdfType;
            var pdfVersion = this.officeConfig.pdfVersion || this.json.pdfVersion || this.options.pdfVersion;
            var pdfCodeBase = this.officeConfig.pdfCodeBase || this.json.pdfCodeBase || this.options.pdfCodeBase;
            var pdfCodeBase64 = this.officeConfig.pdfCodeBase64 || this.json.pdfCodeBase64 || this.options.pdfCodeBase64;

            var objectHtml = "";

            if(window.navigator.platform=="Win64"){
                objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id=\""+this.getOfficeObjectId()+"\" " +
                    "style=\"HEIGHT: 99%; WIDTH: 100%\" " +
                    "codeBase=\""+codeBase64+"#version="+version+"\" " +
                    "classid=\"clsid:"+classid64+"\">";
            }else{
                objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id=\""+this.getOfficeObjectId()+"\" " +
                    "style=\"HEIGHT: 99%; WIDTH: 100%\" " +
                    "codeBase=\""+codeBase+"#version="+version+"\" " +
                    "classid=\"clsid:"+classid+"\">";
            }

            var pars = this.defaultParam();
            pars = Object.merge(pars, this.json.ntkoEditProperties);
            pars = Object.merge(pars, this.json.editProperties);

            Object.each(pars, function(p, key){
                objectHtml += "<PARAM NAME=\""+key+"\" value=\""+p+"\">";
            });

            //objectHtml += "<div style=\"color:red; position:relative; top:-800px; background:#eeeeee; height:760px; padding:20px; text-align:center; font-size:18px; cursor: pointer \">如果不能自动安装控件，请将在点击此处下载并安装签章客户端。</div>";
            objectHtml += "</OBJECT><input type='hidden' value='"+this.json.id+"' name='site'><input type='hidden' value='' name='fileName'><input style='display:none' name=\"file\" type=\"file\"/></form>";
            this.officeNode.appendHTML(objectHtml);
            this.officeForm = this.officeNode.getFirst();
            this.officeOCX = this.officeNode.getFirst().getFirst();

            if(window.navigator.platform=="Win64"){
                this.officeOCX.AddDocTypePlugin(".pdf",pdfType,pdfVersion,pdfCodeBase64,51,true);
            }else{
                this.officeOCX.AddDocTypePlugin(".pdf",pdfType,pdfVersion,pdfCodeBase,51,true);
            }

            //TANGER_OCX_OBJ.CreateNew("word.document");

            //this.officeOCX.AddDocTypePlugin(".pdf","PDF.NtkoDocument","4.0.0.7","/x_desktop/res/framework/officecontrol/ntkooledocall.cab",51,true);

            this.doOfficeOCXEvents();
        }

        this.getAutoSavedAttachments();
        if (this.autoSavedAttachments && this.autoSavedAttachments.length){
            this.openRecoverAutoSaveDlg();
        }else{
            this.openOfficeFile(file);
        }
    },
    openOfficeFile: function(file){
        var url = file || this.getOfficeFileUrl();
        if (url){
            //layout.desktop.tmpOffice = this;
            this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
        }else{
            this.isNew = true;
            this.officeOCX.CreateNew(this.getProgID());
            this.fireEvent("afterCreate");
        }

        //begin auto save
        if (this.json.isAutoSave){
            if (!this.autoSaveTimerID){
                this.autoSave();
                this.form.app.addEvent("queryClose", function(){
                    if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
                }.bind(this));
            }
        }
    },

    clearAutoSaveAttachments: function(){
        this.form.businessData.attachmentList.each(function(att){
            if (att.site===this.json.id+"autosave") o2.Actions.get("x_processplatform_assemble_surface").deleteAttachment(att.id, this.form.businessData.work.id);
        }.bind(this));
        this.autoSavedAttachments = [];
    },
    getRecoverItems: function(recoverItemNode){
        var css = this.form.css;
        var _self = this;
        this.autoSavedAttachments.each(function(att){
            var node = new Element("div", {"styles": css.officeRecoverItemNode}).inject(recoverItemNode);
            var actionNode = new Element("div", {"styles": css.officeRecoverItemActionNode}).inject(node);
            var titleNode = new Element("div", {"styles": css.officeRecoverItemTitleNode, "text": att.name}).inject(node);
            node.store("att", att);

            actionNode.addEvent("click", function(e){
                var n = this.getParent();
                var att = n.retrieve("att");
                _self.form.workAction.getAttachmentData(att.id, _self.form.businessData.work.id);
                e.stopPropagation();
            });
            node.addEvents({
                "mouseover": function(){
                    var isSelected = this.retrieve("isSelected");
                    if (!isSelected) this.setStyles(css.officeRecoverItemNode_over);
                },
                "mouseout": function(){
                    var isSelected = this.retrieve("isSelected");
                    if (!isSelected) this.setStyles(css.officeRecoverItemNode)
                },
                "click": function(){
                    var isSelected = this.retrieve("isSelected");
                    if (isSelected){
                        this.setStyles(css.officeRecoverItemNode);
                        this.getFirst().setStyles(css.officeRecoverItemActionNode);
                        this.store("isSelected", false);
                    }else{
                        var items = recoverItemNode.getChildren();
                        items.each(function(item){
                            item.setStyles(css.officeRecoverItemNode);
                            item.getFirst().setStyles(css.officeRecoverItemActionNode);
                            item.store("isSelected", false);
                        });
                        this.setStyles(css.officeRecoverItemNode_current);
                        this.getFirst().setStyles(css.officeRecoverItemActionNode_current);
                        this.store("isSelected", true);
                    }
                }
            });

        }.bind(this));
    },
    openRecoverAutoSaveDlg: function(){
        var node = new Element("div", {"styles": {"overflow": "hidden", "padding": "0 30px"}});
        var html = "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden\">请选择要恢复的正文版本：</div>";
        html += "<div style=\"max-height: 300px; margin-bottom:10px; margin-top:10px; overflow-y:auto;\"></div>";
        node.set("html", html);
        var recoverItemNode = node.getLast();
        this.getRecoverItems(recoverItemNode);
        node.inject(this.form.app.content);

        var _self = this;
        var dlg = o2.DL.open({
            "title": "恢复正文",
            //"style": "work",
            "isResize": false,
            "content": node,
            "width": 600,
            "onPostClose": function(){
                _self.clearAutoSaveAttachments();
            },
            "buttonList": [
                {
                    "text": MWF.xApplication.process.Xform.LP.recover,
                    "action": function(d, e){
                        this.doRecoverFile(node, e, dlg);
                    }.bind(this)
                },
                {
                    "text": MWF.xApplication.process.Xform.LP.notRecover,
                    "action": function(d, e){
                        this.doNotRecoverFile(node, e, dlg);
                    }.bind(this)
                }
            ]
        });
    },
    doNotRecoverFile: function(node, e, dlg){
        var _self = this;
        this.form.app.confirm("infor", e, this.form.app.lp.notRecoverFileConfirmTitle, this.form.app.lp.notRecoverFileConfirmContent, 450, 120, function(){
            this.close();
            dlg.close();
            _self.openOfficeFile();
        }, function(){
            this.close();
        }, null, null, this.form.json.confirmStyle);
    },

    doRecoverFile: function(node, e, dlg){
        var recoverItemNode = node.getLast();
        var items = recoverItemNode.getChildren();
        var _self = this;
        for (var i=0; i<items.length; i++){
            if (items[i].retrieve("isSelected")){
                var text = this.form.app.lp.recoverFileConfirmContent;
                var att = items[i].retrieve("att");
                text = text.replace("{att}", att.name);
                this.form.app.confirm("infor", e, this.form.app.lp.recoverFileConfirmTitle, text, 450, 120, function(){
                    this.close();
                    dlg.close();
                    _self.form.workAction.getAttachmentUrl(att.id, _self.form.businessData.work.id, function(file){
                        _self.openOfficeFile(file);
                        dlg.close();
                    });
                }, function(){
                    this.close();
                }, null, null, this.form.json.confirmStyle);
                break;
            }
        }
    },
    checkAutoSaveNumber: function(callback){
        if (!this.autoSavedAttachments) this.autoSavedAttachments = [];
        if (this.autoSavedAttachments.length >= this.json.autoSaveNumber.toInt()){
            //delete first att
            var att = this.autoSavedAttachments.shift();
            o2.Actions.get("x_processplatform_assemble_surface").deleteAttachment(att.id, this.form.businessData.work.id, function(){
                this.checkAutoSaveNumber(callback);
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    getAutoSaveFileName: function(){
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
        var d = Date.parse(new Date());
        var dText = d.format("%Y-%m-%d %H:%M:%S");
        return MWF.xApplication.process.Xform.LP.autosave+"("+dText+")."+ename;
    },
    autoSave: function(){
        var interval = (this.json.autoSaveTime) ? this.json.autoSaveTime.toInt()*60*1000 : (5*60*1000);
        this.autoSaveTimerID = window.setInterval(function(){
            if (!this.openedAttachment){
                this.checkAutoSaveNumber(function(){
                    try{
                        var fileName = this.getAutoSaveFileName();
                        this.officeForm.getElement("input").set("value", this.json.id+"autosave");
                        url = this.form.workAction.action.actions.uploadAttachment.uri;
                        url = this.form.workAction.action.address+url.replace("{id}", this.form.businessData.work.id);
                        this.officeOCX.SaveToURL(url, "file", "", fileName, this.getFormId());

                        this.form.workAction.listAttachments(this.form.businessData.work.id, function(json){
                            this.form.businessData.attachmentList = json.data;
                            for (var i=0; i<json.data.length; i++){
                                var att = json.data[i];
                                if (att.name===fileName){
                                    this.autoSavedAttachments.push(att);
                                    break;
                                }
                            }
                        }.bind(this), null, false);
                    }catch(e){}
                }.bind(this));
            }
        }.bind(this), interval);

    },
    doOfficeOCXEvents: function(){
        var id = this.getOfficeObjectId();
        this.addOfficeEvent(id, "AfterOpenFromURL(doc, statusCode)", "if (layout.desktop.offices[\""+id+"\"]) layout.desktop.offices[\""+id+"\"].AfterOpenFromURL(doc, statusCode);");
        this.addOfficeEvent(id, "OnDocumentOpened(url, doc)", "if (layout.desktop.offices[\""+id+"\"]) layout.desktop.offices[\""+id+"\"].OnDocumentOpened(url, doc);");
        this.addOfficeEvent(id, "OnDocumentClosed()", "if (layout.desktop.offices[\""+id+"\"]) layout.desktop.offices[\""+id+"\"].OnDocumentClosed();");
    },
    OnDocumentClosed: function(){
        this.fireEvent("afterCloseOffice");
    },
    OnDocumentOpened: function(url, doc){
        this.afterOpen();
        this.loadMenu();

        this.fireEvent("afterOpenOffice");
    },
    AfterOpenFromURL: function(doc, statusCode){
        this.fireEvent("afterOpen", [doc, statusCode]);
    },
    addOfficeEvent: function(id, event, code){
        var script = document.createElement("script");
        script.setAttribute("for", id);
        script.setAttribute("event", event);
        script.innerText = code;
        this.officeForm.appendChild(script);
    },

    loadOfficeRead: function(file){
        this.loadOfficeSpacer();
        this.node.setStyle("pisition", "absolute");

        // var codeBase = this.json.codeBase || this.options.codeBase;
        // var version = this.json.version || this.options.version;
        // var classid = this.json.clsid || this.options.clsid;
        var codeBase = this.officeConfig.codeBase || this.json.codeBase || this.options.codeBase;
        var version = this.officeConfig.version || this.json.version || this.options.version;
        var classid = this.officeConfig.classid || this.json.clsid || this.options.clsid;
        var codeBase64 = this.officeConfig.codeBase64 || this.json.codeBase64 || this.options.codeBase64;
        var classid64 = this.officeConfig.classid64 || this.json.clsid64 || this.options.clsid64;
        var pdfType = this.officeConfig.pdfType || this.json.pdfType || this.options.pdfType;
        var pdfVersion = this.officeConfig.pdfVersion || this.json.pdfVersion || this.options.pdfVersion;
        var pdfCodeBase = this.officeConfig.pdfCodeBase || this.json.pdfCodeBase || this.options.pdfCodeBase;
        var pdfCodeBase64 = this.officeConfig.pdfCodeBase64 || this.json.pdfCodeBase64 || this.options.pdfCodeBase64;

        // var objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id=\""+this.getOfficeObjectId()+"\""+
        //     "style=\"HEIGHT: 99%; WIDTH: 100%\" " +
        //     "codeBase=\""+codeBase+"#version="+version+"\" " +
        //     "classid=\"clsid:"+classid+"\">";
        var objectHtml = "";
        if(window.navigator.platform=="Win64"){
            objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id=\""+this.getOfficeObjectId()+"\" " +
                "style=\"HEIGHT: 99%; WIDTH: 100%\" " +
                "codeBase=\""+codeBase64+"#version="+version+"\" " +
                "classid=\"clsid:"+classid64+"\">";
        }else{
            objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id=\""+this.getOfficeObjectId()+"\" " +
                "style=\"HEIGHT: 99%; WIDTH: 100%\" " +
                "codeBase=\""+codeBase+"#version="+version+"\" " +
                "classid=\"clsid:"+classid+"\">";
        }

        var pars = this.defaultParam(true);
        pars = Object.merge(pars, this.json.ntkoReadProperties);
        pars = Object.merge(pars, this.json.readProperties);
        Object.each(pars, function(p, key){
            objectHtml += "<PARAM NAME=\""+key+"\" value=\""+p+"\">";
        });
        objectHtml += "</object></form>";

        this.officeNode.set("html", objectHtml);
        this.officeForm = this.officeNode.getFirst();
        this.officeOCX = this.officeNode.getFirst().getFirst();

        if(window.navigator.platform=="Win64"){
            this.officeOCX.AddDocTypePlugin(".pdf",pdfType,pdfVersion,pdfCodeBase64,51,true);
        }else{
            this.officeOCX.AddDocTypePlugin(".pdf",pdfType,pdfVersion,pdfCodeBase,51,true);
        }
        //this.officeOCX.AddDocTypePlugin(".pdf","PDF.NtkoDocument","4.0.0.3","/x_desktop/res/framework/officecontrol/ntkooledocall.cab",51,true);

        var url = file || this.getOfficeFileUrl();
        if (url){
            var id = this.getOfficeObjectId();
            this.addOfficeEvent(id, "OnDocumentOpened(url, doc)", "if (layout.desktop.offices[\""+id+"\"]) layout.desktop.offices[\""+id+"\"].OnDocumentOpened(url, doc);");
            this.addOfficeEvent(id, "AfterOpenFromURL(doc, statusCode)", "if (layout.desktop.offices[\""+id+"\"]) layout.desktop.offices[\""+id+"\"].AfterOpenFromURL(doc, statusCode);");

            this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
        }
    },
    createUploadFileNode: function(){
        this.uploadFileAreaNode = new Element("div", {"styles": {"display": "none"}});
        var html = "<input name=\"file\" type=\"file\"/>";
        this.uploadFileAreaNode.set("html", html);
        this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        this.uploadFileAreaNode.inject(this.officeForm);
    },
    getData: function(){
        if (this.officeOCX && (this.officeOCX.DocType==1 || this.officeOCX.DocType==6)){
            this.officeOCX.ActiveDocument.Application.Selection.WholeStory();
            var content = this.officeOCX.ActiveDocument.Application.Selection.Text;
            return content;
        }else{
            return this._getBusinessData();
        }
    },
    setData: function(){},
    save: function(history){
        //if (!this.uploadFileAreaNode) this.createUploadFileNode();
        if (!this.readonly){
            if (this.historyMode) return true;
            if (!this.officeForm) return true;

            this.fireEvent("beforeSave");
            try{
                if (this.openedAttachment){
                    this.officeForm.getElement("input").set("value", this.openedAttachment.site);

                    url = this.form.workAction.action.actions.replaceAttachment.uri;
                    url = url.replace("{id}", this.openedAttachment.id);
                    url = this.form.workAction.action.address+url.replace("{workid}", this.form.businessData.work.id);

                    this.officeOCX.SaveToURL(url, "file", "", this.openedAttachment.name, this.getFormId());

                }else{
                    if (history){
                        if (this.json.isHistory) this.saveHistory();
                    }

                    this.clearAutoSaveAttachments();

                    // if (this.autoSavedAttachments && this.autoSavedAttachments.length){
                    //     this.autoSavedAttachments.each(function(att){
                    //         o2.Actions.get("x_processplatform_assemble_surface").deleteAttachment(att.id, this.form.businessData.work.id);
                    //     }.bind(this));
                    //     this.autoSavedAttachments = [];
                    // }
                    //this.saveHTML();
                    this.officeForm.getElement("input").set("value", this.json.id);
                    var url = "";
                    if (this.file){
                        url = this.form.workAction.action.actions.replaceAttachment.uri;
                        url = url.replace("{id}", this.file.id);
                        url = this.form.workAction.action.address+url.replace("{workid}", this.form.businessData.work.id);

                        this.officeOCX.SaveToURL(url, "file", "", this.getFileName(), this.getFormId());
                    }else{
                        url = this.form.workAction.action.actions.uploadAttachment.uri;
                        url = this.form.workAction.action.address+url.replace("{id}", this.form.businessData.work.id);

                        this.officeOCX.SaveToURL(url, "file", "", this.getFileName(), this.getFormId());
                        this.form.workAction.getWorkContent(this.form.businessData.work.id, function(json){
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
        var activity = (this.form.businessData.work) ? this.form.businessData.work.activityName : MWF.xApplication.process.Xform.LP.completed;
        var name = MWF.name.cn(layout.session.user.name);
        var d = Date.parse(new Date());
        var dText = d.format("%Y-%m-%d %H:%M");
        return activity+"("+name+")-"+dText+"."+ename;
    },
    saveHistory: function(){
        var fileName = this.getHistoryFileName();
        this.officeForm.getElement("input").set("value", this.json.id+"history");
        url = this.form.workAction.action.actions.uploadAttachment.uri;
        url = this.form.workAction.action.address+url.replace("{id}", this.form.businessData.work.id);
        this.officeOCX.SaveToURL(url, "file", "", fileName, this.getFormId());
    },
    getHTMLFileName: function(){
        //var id = (this.form.businessData.work) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        var id = (!this.form.businessData.workCompleted) ? this.form.businessData.work.id : this.form.businessData.workCompleted.workId;
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
            url = this.form.workAction.action.actions.replaceAttachment.uri;
            url = url.replace("{id}", file.id);
            url = this.form.workAction.action.address+url.replace("{workid}", this.form.businessData.work.id);
        }else{
            url = this.form.workAction.action.actions.uploadAttachment.uri;
            url = this.form.workAction.action.address+url.replace("{id}", this.form.businessData.work.id);
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
            if (!this.form.businessData.workCompleted){
                url = this.form.workAction.action.actions.getAttachmentData.uri;
                url = url.replace("{id}", encodeURIComponent(file.id));
                return this.form.workAction.action.address+url.replace("{workid}", encodeURIComponent(this.form.businessData.work.id));
            }else{
                url = this.form.workAction.action.actions.getWorkcompletedAttachmentData.uri;
                url = url.replace("{id}", encodeURIComponent(file.id));
                return this.form.workAction.action.address+url.replace("{workCompletedId}", encodeURIComponent(this.form.businessData.workCompleted.id));
            }
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
                alert("onreadystatechange"+ this.readyState );
                alert(this.body.firstChild);
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
            if (this.json.isShowSummary!==false){
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