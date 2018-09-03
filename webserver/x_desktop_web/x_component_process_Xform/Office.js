MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Office = MWF.APPOffice =  new Class({
	Extends: MWF.APP$Module,
	isActive: false,
    options:{
        "version": "5,0,3,1",
        "ProductCaption": "O2",
        "ProductKey": "EDCC626CB85C9A1D3E0D7BDDDC2637753C596725",
        "MakerCaption": "浙江兰德纵横网络技术股份有限公司",
        "MakerKey": "E138DABB4AC26C2D8E09FAE59AB3BDE87AFB9D7B",
        "clsid": "A64E3073-2016-4baf-A89D-FFE1FAA10EC0",
        "codeBase": "/x_desktop/res/framework/officecontrol/OfficeControl.cab",
        "clsid64": "A64E3073-2016-4baf-A89D-FFE1FAA10EC0",
        "codeBase64": "/x_desktop/res/framework/officecontrol/OfficeControl.cab",
        "moduleEvents": ["redFile",
            "afterOpen",
            "afterOpenOffice",
            "afterCreate",
            "seal",
            "beforeSave",
            "afterSave",
            "afterCloseOffice"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
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
	},
    loadOffice: function(){
        if (!this.isActive){
            this.loadOfficeNotActive();
        }else{
            this.loadOfficeContorl();
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
            "MakerCaption": this.json.makerCaption || this.options.MakerCaption,
            "MakerKey": this.options.makerKey || this.options.MakerKey,
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
    loadOfficeContorl: function(){
	    if (this.node.getSize().y<800) this.node.setStyle("height", "800px");

        if (!layout.desktop.offices) layout.desktop.offices = {};
        layout.desktop.offices[this.getOfficeObjectId()] = this;

        if (this.readonly){
            this.loadOfficeRead();
        }else if (this.json.isReadonly){
            this.readonly  = true;
            this.loadOfficeRead();
        }else{
            if (this.json.readScript && this.json.readScript.code){
                var flag = this.form.Macro.exec(this.json.readScript.code, this);
                if (flag){
                    this.readonly = true;
                    this.loadOfficeRead();
                }else{
                    this.loadOfficeEdit();
                }
            }else{
                this.loadOfficeEdit();
            }
        }
	},
    loadOfficeSpacer: function(){
		var size = this.node.getSize();

        this.officeNode = new Element("div", {
            "styles": this.form.css.officeAreaNode
        }).inject(this.node);

        this.officeNode.setStyle("height", ""+size.y+"px");

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

    getFormId: function(){
        var id = (this.form.businessData.work) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        return "form"+this.json.id+id;
    },
    getFileName: function(){
        var ename = "doc";
        switch (this.json.officeType){
            case "word":
                ename = "doc";
                break;
            case "excel":
                ename = "xls";
                break;
            case "ppt":
                ename = "ppt";
        }
        var id = (this.form.businessData.work) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        return "file"+this.json.id+id+"."+ename;
    },
    getOfficeObjectId: function(){
        var id = (this.form.businessData.work) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        return "NTKOOCX"+this.json.id+id;
    },
    getFileInputName: function(){
        var id = (this.form.businessData.work) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
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
                var host = MWF.Actions.getHost(root)
                return (flag==="/") ? host+this.json.template : host+"/"+this.json.template
            }
        }
        return this.json.template;
    },
    getOfficeFileUrl: function(){
        var fileName = this.getFileName();

        var file = null;
        atts = this.form.businessData.attachmentList;
        for (var i=0; i<atts.length; i++){
            if ((atts[i].name===fileName) || (atts[i].site===this.json.id)){
            //if (atts[i].site===this.json.id){
                file = atts[i];
                break;
            }
        }
        if (file){
            this.file = file;
            var url = "";
            if (this.form.businessData.work){
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
        this.officeOCX.ActiveDocument.Application.UserName = layout.desktop.session.user.name;
        if (!this.isNew){
            this.officeOCX.ActiveDocument.TrackRevisions = true;
            this.officeOCX.ActiveDocument.showRevisions = false;
        }else{
            this.officeOCX.ActiveDocument.TrackRevisions = false;
            this.officeOCX.ActiveDocument.showRevisions = false;
        }
        //this.officeOCX.FullScreenMode = true;
    },
    stopRevisions: function(accept){
        this.officeOCX.ActiveDocument.TrackRevisions = false;
        this.officeOCX.ActiveDocument.showRevisions = false;
        if (accept) this.officeOCX.ActiveDocument.AcceptAllRevisions();
    },
    loadMenu: function(){
        if (!this.isMenuLoad){
            if (this.json.menuEditButtons.length){
                this.menuNode = new Element("div", {"styles": this.form.css.officeMenuNode}).inject(this.node, "top");
                if (this.json.menuEditButtons.indexOf("new")!==-1){
                    this.newItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": MWF.xApplication.process.Xform.LP.menu_new}).inject(this.menuNode);
                    this.newItem.addEvent("click", function(){this.officeOCX.CreateNew(this.getProgID());}.bind(this));
                }
                if (this.json.menuEditButtons.indexOf("open")!==-1){
                    this.openItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": MWF.xApplication.process.Xform.LP.menu_openfile}).inject(this.menuNode);
                    this.openItem.addEvent("click", function(){this.officeOCX.ShowDialog(1);}.bind(this));
                }
                if (this.json.menuEditButtons.indexOf("save")!==-1){
                    this.saveItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": MWF.xApplication.process.Xform.LP.menu_savefile}).inject(this.menuNode);
                    this.saveItem.addEvent("click", function(){this.officeOCX.ShowDialog(3);}.bind(this));
                }

                if (this.json.menuEditButtons.indexOf("revisions")!==-1){
                    var text = MWF.xApplication.process.Xform.LP.menu_revisions_show;
                    try {
                        if (this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup !== 0){
                            text = MWF.xApplication.process.Xform.LP.menu_revisions_hide;
                        }
                    }catch(e){}
                    this.revisionsItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": text}).inject(this.menuNode);
                    this.revisionsItem.addEvent("click", this.toggleRevisions.bind(this));
                }

                if (this.json.menuEditButtons.indexOf("fullscreen")!==-1){
                    this.fullscreenItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": MWF.xApplication.process.Xform.LP.menu_fullscreen}).inject(this.menuNode);
                    this.fullscreenItem.addEvent("click", function(){this.officeOCX.FullScreenMode = true;}.bind(this));
                }

                if (this.json.menuEditButtons.indexOf("toolbar")!==-1){
                    if (!this.readonly){
                        var text = MWF.xApplication.process.Xform.LP.menu_toolbar_show;
                        if (this.officeOCX.ToolBars){
                            text = MWF.xApplication.process.Xform.LP.menu_toolbar_hide;
                        }
                        this.toolbarItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": text}).inject(this.menuNode);
                        this.toolbarItem.addEvent("click", function(){
                            var text = (this.officeOCX.ToolBars) ? MWF.xApplication.process.Xform.LP.menu_toolbar_show : MWF.xApplication.process.Xform.LP.menu_toolbar_hide;
                            this.toolbarItem.set("text", text);
                            this.officeOCX.ToolBars = !this.officeOCX.ToolBars;
                        }.bind(this));
                    }
                }

                if (this.json.menuEditButtons.indexOf("preview")!==-1){
                    this.previewItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": MWF.xApplication.process.Xform.LP.menu_preview}).inject(this.menuNode);
                    this.previewItem.addEvent("click", function(){this.officeOCX.PrintPreview();}.bind(this));
                }

                if (this.json.menuEditButtons.indexOf("showHistory")!==-1){
                    this.historyItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": MWF.xApplication.process.Xform.LP.menu_showHistory}).inject(this.menuNode);
                    this.historyItem.addEvent("click", this.showHistory.bind(this));
                }
                if (this.json.menuEditButtons.indexOf("redfile")!==-1){
                    if (!this.readonly){
                        this.redItem = new Element("div", {"styles": this.form.css.officeMenuItemNode, "text": MWF.xApplication.process.Xform.LP.menu_redfile}).inject(this.menuNode);
                        this.redItem.addEvent("click", this.redFile.bind(this));
                    }
                }

                if (this.json.menuEditButtons.indexOf("seal")!==-1){
                    if (!this.readonly) {
                        this.redItem = new Element("div", {
                            "styles": this.form.css.officeMenuItemNode,
                            "text": MWF.xApplication.process.Xform.LP.menu_seal
                        }).inject(this.menuNode);
                        this.redItem.addEvent("click", this.seal.bind(this));
                    }
                }
            }
            this.isMenuLoad = true;
        }
    },
    showHistory: function(){
        MWF.require("MWF.xDesktop.Dialog", function(){
            var width = 680;
            var height = 500;
            var p = MWF.getCenterPosition(this.form.app.content, width, height);

            var _self = this;
            var dlg = new MWF.xDesktop.Dialog({
                "title": MWF.xApplication.process.Xform.LP.menu_showHistory,
                "style": "work",
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
                    this.showHistoryContent(dlg)
                }.bind(this)
            });
            dlg.show();
        }.bind(this));
    },
    showHistoryContent: function(dlg){
        dlg.content.setStyle("overflow", "auto");
        atts = this.form.businessData.attachmentList;
        var site = this.json.id+"history";
        for (var i=0; i<atts.length; i++){
            if ((atts[i].site===site)){
        for (var x=0; x<31; x++){
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
                        this.openOfficeHistory(e);
                        dlg.close();
                    }.bind(this)
                }
            }).inject(div);
        }

            }
        }
    },
    openOfficeHistory: function(e){
        var fileName = e.target.getParent().get("value");
        if (this.form.businessData.work){
            url = this.form.workAction.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(fileName));
            url = this.form.workAction.action.address+url.replace("{workid}", encodeURIComponent(this.form.businessData.work.id));
        }else{
            url = this.form.workAction.action.actions.getWorkcompletedAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(fileName));
            url = this.form.workAction.action.address+url.replace("{workid}", encodeURIComponent(this.form.businessData.workCompleted.id));
        }

        this.officeOCX.BeginOpenFromURL(url, true, true);

    },
    seal: function(){
        this.fireEvent("seal");
    },
    redFile: function(){
       // try {

            this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 0;
            this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
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
            this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 2;
            this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
            this.officeOCX.ActiveDocument.showRevisions = true;
        }catch(e){}
    },
    hideRevisions: function(){
        try {
            this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 0;
            this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
            this.officeOCX.ActiveDocument.showRevisions = false;
        }catch(e){}
    },
    toggleRevisions: function(){
        var t = this.revisionsItem.get("text");
        if (t===MWF.xApplication.process.Xform.LP.menu_revisions_show){
            this.revisionsItem.set("text", MWF.xApplication.process.Xform.LP.menu_revisions_hide);

            try {
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.Markup = 2;
                this.officeOCX.ActiveDocument.ActiveWindow.View.RevisionsFilter.View = 0;
            }catch(e){}
            try {
                this.officeOCX.ActiveDocument.showRevisions = true;
            }catch(e){}
        }else{
            this.revisionsItem.set("text", MWF.xApplication.process.Xform.LP.menu_revisions_show);

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
        //layout.desktop.tmpOffice = null;
    },

    loadOfficeEditFirefox: function(){
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
        objectHtml += "<SPAN STYLE='color:red'>尚未安装NTKO Web Chrome跨浏览器插件。请点击<a href=\"/x_desktop/res/framework/officecontrol/ntkoplugins.xpi\">安装组件</a></SPAN>";

        objectHtml += "</OBJECT><input type='hidden' value='"+this.json.id+"' name='site'><input style='display:none' name=\"file\" type=\"file\"/></form>";
        this.officeNode.appendHTML(objectHtml);
        this.officeForm = this.officeNode.getFirst();
        this.officeOCX = this.officeNode.getFirst().getFirst();

        this.doOfficeOCXEvents();

        var url = this.getOfficeFileUrl();
        if (url){
            this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
        }else{
            this.officeOCX.CreateNew(this.getProgID());
            this.fireEvent("afterCreate");
        }
    },
    loadOfficeEditChrome: function(){
        this.loadOfficeSpacer();
        this.node.setStyle("pisition", "absolute");

        var codeBase = this.json.codeBase || this.options.codeBase;
        var version = this.json.version || this.options.version;
        var classid = this.json.clsid || this.options.clsid;

        var objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id='"+this.getOfficeObjectId()+"' " +
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
        objectHtml += "<SPAN STYLE='color:red'>尚未安装NTKO Web Chrome跨浏览器插件。请点击<a href=\"/x_desktop/res/framework/officecontrol/ntkoplugins.crx\">安装组件</a></SPAN>";

        objectHtml += "</OBJECT><input type='hidden' value='"+this.json.id+"' name='site'><input style='display:none' name=\"file\" type=\"file\"/></form>";
        this.officeNode.appendHTML(objectHtml);
        this.officeForm = this.officeNode.getFirst();
        this.officeOCX = this.officeNode.getFirst().getFirst();

        this.doOfficeOCXEvents();

        var url = this.getOfficeFileUrl();
        if (url){
            this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
        }else{
            this.officeOCX.CreateNew(this.getProgID());
            this.fireEvent("afterCreate");
        }
    },
    loadOfficeEdit: function(){
        if (Browser.name==="chrome"){
            this.loadOfficeEditChrome();
        }else if (Browser.name==="firefox") {
            this.loadOfficeEditFirefox();
        }else{
            this.loadOfficeEditIE();
        }
    },
    loadOfficeEditIE: function(){
		this.loadOfficeSpacer();
		//this.loadMenu();
		this.node.setStyle("pisition", "absolute");

		//if (this.json.ntkoEditProperties.load)

		var codeBase = this.json.codeBase || this.options.codeBase;
		var version = this.json.version || this.options.version;
		var classid = this.json.clsid || this.options.clsid;

        var objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id=\""+this.getOfficeObjectId()+"\" " +
            "style=\"HEIGHT: 99%; WIDTH: 100%\" " +
            "codeBase=\""+codeBase+"#version="+version+"\" " +
            "classid=\"clsid:"+classid+"\">";

        var pars = this.defaultParam();
        pars = Object.merge(pars, this.json.ntkoEditProperties);
        pars = Object.merge(pars, this.json.editProperties);

        Object.each(pars, function(p, key){
            objectHtml += "<PARAM NAME=\""+key+"\" value=\""+p+"\">";
        });
        objectHtml += "</OBJECT><input type='hidden' value='"+this.json.id+"' name='site'><input type='hidden' value='' name='fileName'><input style='display:none' name=\"file\" type=\"file\"/></form>";
        this.officeNode.appendHTML(objectHtml);
        this.officeForm = this.officeNode.getFirst();
        this.officeOCX = this.officeNode.getFirst().getFirst();

        this.officeOCX.AddDocTypePlugin(".pdf","PDF.NtkoDocument","4.0.0.3","/x_desktop/res/framework/officecontrol/ntkooledocall.cab",51,true);
        //TANGER_OCX_OBJ.CreateNew("word.document");

        //this.officeOCX.AddDocTypePlugin(".pdf","PDF.NtkoDocument","4.0.0.7","/x_desktop/res/framework/officecontrol/ntkooledocall.cab",51,true);

        this.doOfficeOCXEvents();

        var url = this.getOfficeFileUrl();
        if (url){
            //layout.desktop.tmpOffice = this;
            this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
        }else{
            this.isNew = true;
            // if (this.json.officeType!=="other"){
                this.officeOCX.CreateNew(this.getProgID());
                this.fireEvent("afterCreate");
            // }else{
            //
            // }
        }
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

    loadOfficeRead: function(){
        this.loadOfficeSpacer();
        this.node.setStyle("pisition", "absolute");

        var codeBase = this.json.codeBase || this.options.codeBase;
        var version = this.json.version || this.options.version;
        var classid = this.json.clsid || this.options.clsid;

        var objectHtml = "<form id='"+this.getFormId()+"' style='height:100%'><OBJECT id=\""+this.getOfficeObjectId()+"\""+
            "style=\"HEIGHT: 99%; WIDTH: 100%\" " +
            "codeBase=\""+codeBase+"#version="+version+"\" " +
            "classid=\"clsid:"+classid+"\">";

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

        this.officeOCX.AddDocTypePlugin(".pdf","PDF.NtkoDocument","4.0.0.3","/x_desktop/res/framework/officecontrol/ntkooledocall.cab",51,true);

        var url = this.getOfficeFileUrl();
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
        if (this.officeOCX){
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
            if (!this.officeForm) return true;
            this.fireEvent("beforeSave");
            if (history){
                if (this.json.isHistory) this.saveHistory();
            }
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

            // this.stopRevisions(true);
            // this.saveHTML();
            // var url = this.getOfficeFileUrl();
            // if (url){
            //     //layout.desktop.tmpOffice = this;
            //     this.officeOCX.BeginOpenFromURL(url, true, this.readonly);
            // }else{
            //     // if (this.json.officeType!=="other"){
            //     this.officeOCX.CreateNew(this.getProgID());
            //     this.fireEvent("afterCreate");
            //     // }else{
            //     //
            //     // }
            // }

            this.fireEvent("afterSave");
        }
    },
    getHistoryFileName: function(){
        var ename = "doc";
        switch (this.json.officeType){
            case "word":
                ename = "doc";
                break;
            case "excel":
                ename = "xls";
                break;
            case "ppt":
                ename = "ppt";
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
        var id = (this.form.businessData.work) ? this.form.businessData.work.id : this.form.businessData.workCompleted.workId;
        return id+this.json.id+".mht";
    },
    saveHTML: function(){
        debugger;
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
            if (this.form.businessData.work){
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
        debugger;
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
                    if (window.o2){
                        window.o2.openDocument(url);
                    }else if(window.webkit){
                        window.webkit.messageHandlers.openDocument.postMessage(url);
                    }else{
                        window.open(url);
                    }
                }
            }.bind(this));

	}
}); 