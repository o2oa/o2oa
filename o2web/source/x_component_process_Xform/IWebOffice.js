MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.IWebOffice = MWF.APPIWebOffice =  new Class({
    Extends: MWF.APP$Module,
    options:{
        "copyright": "金格科技iWebOffice2015智能文档中间件[演示版];V5.0S0xGAAEAAAAAAAAAEAAAAJ0BAACgAQAALAAAAFgHOIQT3Ejo2JZMmSVxBkTixI2YkNh+cy/HGFlQ3HNIRG8j2V9vAnBWM8B384zkV79hltghTpDkba9QJTjGCDkylzhl3rian7v/AFF5DXQ7HdoXpN2imHbGHf8ieQNf672v38ODXJum7VaWv4P3DXzlzdnmzoaTTukFl+3ntFH29dQSjC94c4v0npn9rnXK5OsjJGR5mVGJJd6GWjE9rP3fo+kWCBYzb+bhn2sL+SKoyFjUuSptNHsOn3nRKrXyqkRd+QaHR/MDuKd1kEbUpRqCz9CjgfZFX00Zaz2l6ChA6LmdMMjjECalHZQA9WuxAc7cynGbvNfEgi277ahRpSZHmjKY5s18dJM/t9gdatg5b/dCqnUEn0+HS511XaM3xvy4DCVnD1n8xC9I5w+16NGrtTMiMSZFpc3QJphdShC0j+1l/ZyVj33YM31JmuxkI5SDL2CAfMUNsioseUfpOKvpxdcA7nmwlKrpxNetm9Bq0kwJn/jUU2bQa2c+bPRX82JcmFUwAz0ctOQ+Tyi+MoRpATEYW35KZtWepeDTGHJRfaJR81x8dVU0Lp1TuxtQiw==",
        "version": "12,7,0,828",
        "clsid": "D89F482C-5045-4DB5-8C53-D2C9EE71D025",
        "codeBase": "../o2_lib/iWebOffice/iWebOffice2015.cab",
        "version64" : "12,5,0,652",
        "clsid64" : "D89F482C-5045-4DB5-8C53-D2C9EE71D024",
        "codeBase64": "../o2_lib/iWebOffice/iWebOffice2015.cab",
        "moduleEvents": [,
            "afterOpen",
            "afterCreate",
            "beforeSave",
            "afterSave"
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
        if (Browser.name==="ie"){
            this.file = null;
            if (!this.form.officeList) this.form.officeList=[];
            this.form.officeList.push(this);
        }
    },
    _afterLoaded: function(){
        if (Browser.name==="ie"){
            if(!layout.serviceAddressList["x_jg_assemble_control"]){
                this.node.set("html","<h3><font color=red>please install weboffice !!!</font></h3>");
                return false;
            }else{
                var host = layout.serviceAddressList["x_jg_assemble_control"].host;
                var port = layout.serviceAddressList["x_jg_assemble_control"].port;
                this.webUrl =  layout.protocol + "//" + host + ":" + port + "/x_jg_assemble_control/IndexServlet";
            }

            this.action = o2.Actions.load("x_jg_assemble_control");
            if (!this.json.isNotLoadNow){

                this.data = this.getData();
                if(this.data.documentId === ""){
                    this.createDocument(function (){
                        this.loadOffice();
                    }.bind(this));
                }else {
                    this.documentId = this.data.documentId;
                    this.loadOffice();
                }
            }
            if (!this.json.isNotLoadNow){
                this.loadOffice();
            }
        }else {
            //console.log(JSON.stringify(this.json))
            this.node.set("html","<h3><font color=red>只支持ie浏览器!</font></h3>");
        }
    },
    createDocument : function (callback){
        this.action.ManageJGAction.create({"fileType":this.getFileType()}, function( json ){
            this.fireEvent("afterCreate");
            this.documentId = json.data.message;
            this.setData();
            if (callback) callback();
        }.bind(this),null, false);
    },
    createNew : function (){
        this.officeOCX.CreateFile();
    },
    getData: function(){
        var data = {
            "documentId" : ""
        };
        if(this.form.businessData.data[this.json.id]){
            data.documentId = this.form.businessData.data[this.json.id].documentId;
        }
        return data;
    },
    setData: function(){
        var data = {
            "documentId" : this.documentId
        };
        this._setBusinessData(data);
    },
    loadOffice: function(){
        if (!this.officeLoaded){
            MWF.getJSON("../o2_lib/iWebOffice/config.json", function(json){
                this.officeConfig = json;
            }.bind(this), false);
            this.loadOfficeContorl();
            this.officeLoaded = true;
        }
    },
    defaultParam: function(readonly){
        var o = {
            "copyright": this.json.copyright || this.options.copyright
        };
        return o;
    },
    loadOfficeContorl: function(file){
        if (this.node.getSize().y<800) this.node.setStyle("height", "800px");

        if (!layout.desktop.offices) layout.desktop.offices = {};
        layout.desktop.offices[this.getOfficeObjectId()] = this;

        if (this.json.isReadonly){
            this.readonly  = true;
        }else if (this.json.readScript && this.json.readScript.code){
            var flag = this.form.Macro.exec(this.json.readScript.code, this);
            if (flag){
                this.readonly = true;
            }
        }
        this.loadOfficeEditor();
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
    getFormId: function(){
        var id = (!this.form.businessData.workCompleted) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        return "form"+this.json.id+id;
    },
    getFileType: function(){
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
        return ename;
    },
    getOfficeObjectId: function(){
        var id = (!this.form.businessData.workCompleted) ? this.form.businessData.work.id : this.form.businessData.workCompleted.id;
        return "WebOffice"+this.json.id+id;
    },
    loadOfficeEditor: function(){
        if (!this.officeOCX){
            this.loadOfficeSpacer();

            this.node.setStyle("pisition", "absolute");

            var codeBase = this.officeConfig.codeBase || this.json.codeBase || this.options.codeBase;
            var version = this.officeConfig.version || this.json.version || this.options.version;
            var classid = this.officeConfig.classid || this.json.clsid || this.options.clsid;
            var codeBase64 = this.officeConfig.codeBase64 || this.json.codeBase64 || this.options.codeBase64;
            var classid64 = this.officeConfig.classid64 || this.json.clsid64 || this.options.clsid64;

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

            objectHtml += "</OBJECT></form>";
            this.officeNode.appendHTML(objectHtml);
            this.officeForm = this.officeNode.getFirst();
        }
        setTimeout(function() {this.loadIwebOfficeEditor()}.bind(this), 3000);
    },
    loadIwebOfficeEditor : function (){

        // this.officeOCX.EditType="1,1";            //EditType:编辑类型  方式一、方式二  <参考技术文档>
        // //第一位可以为0,1,2,3 其中:0不可编辑;1可以编辑,无痕迹;2可以编辑,有痕迹,不能修订;3可以编辑,有痕迹,能修订；
        // //第二位可以为0,1 其中:0不可批注,1可以批注。可以参考iWebOffice2009的EditType属性，详细参考技术白皮书

        // //以下为自定义菜单↓
        // this.officeOCX.AppendMenu("1","打开本地文件(&L)", png10);
        // this.officeOCX.AppendMenu("2","保存本地文件(&S)", png2);
        // this.officeOCX.AppendMenu("3","保存到服务器(&U)", png7);
        // this.officeOCX.AppendMenu("7","-");
        // this.officeOCX.AppendMenu("11","保存并退出(&E)", png4);
        // this.officeOCX.AppendMenu("12","-");
        // this.officeOCX.AppendMenu("13","打印文档(&P)", png13);
        // //以上为自定义菜单↑
        // //this.officeOCX.VersionFontColor = "bfdbff";
        // this.officeOCX.AllowEmpty = true;


        this.officeOCX = document.getElementById(this.getOfficeObjectId()).FuncExtModule;

        this.officeOCX.WebUrl = this.webUrl;             //WebUrl:系统服务器路径，与服务器文件交互操作，如保存、打开文档，重要文件
        this.officeOCX.FileName = "文件正文.docx";            //FileName:文档名称
        this.officeOCX.UserName = layout.desktop.session.user.name;            //UserName:操作用户名，痕迹保留需要
        this.officeOCX.RecordID = this.documentId;
        this.officeOCX.FileType="." + this.getFileType();            //FileType:文档类型  .doc  .xls  .wps

        if(this.readonly){
            this.officeOCX.EditType="0,0";
        }
        this.officeOCX.ShowMenu = this.json.iWebOfficeEditProperties.Menubar;                         //控制整体菜单显示
        this.officeOCX.ShowToolBar = this.json.iWebOfficeEditProperties.ToolBars;                      //ShowToolBar:是否显示工具栏:1显示,0不显示
        this.officeOCX.ShowWindow = false;                  //控制显示打开或保存文档的进度窗口，默认不显示
        this.officeOCX.MaxFileSize = 32 * 1024;               //最大的文档大小控制，默认是8M，现在设置成4M。
        this.officeOCX.Print="1";
        this.officeOCX.WebOpen();

        this.fireEvent("afterOpen");
        this.loadMenu();

        if(!this.readonly){

            if(this.json.iWebOfficeEditProperties.IsNoCopy === "1"){
                this.unableCopy();
            }else {

                this.enableCopy();
            }
            if(this.json.trackRevisions === "1"){
                this.showRevision();
            }else {
                this.hideRevision();
            }
        }else {
            if(this.json.iWebOfficeReadProperties.IsNoCopy === "1"){
                this.unableCopy();
            }else {
                this.enableCopy();
            }
        }
    },
    setBookMark : function (name,value){
        if (!this.officeOCX.WebSetBookMarks(name,value)){
            //console.log("公文域内容设置失败");
            //console.log(WebOffice.Status);
        }else{
            //alert("公文域内容设置成功");
            //console.log(WebOffice.Status);
        }
    },
    getBookMark : function (name){
        return this.officeOCX.WebGetBookMarks(name);
    },
    openLocal : function (){
        try{
            this.officeOCX.WebOpenLocal();
        }catch(e){this.officeOCX.Alert(e.description);}
    },
    saveLocal : function (){
        try{
            this.officeOCX.WebSaveLocal();
            this.officeOCX.Alert("success");
        }catch(e){
            this.officeOCX.Alert(e.description);
        }
    },
    savePdf : function (){
        try{
            if(this.officeOCX.WebSavePDF())
            {
                this.officeOCX.Alert("success");
            }
            else{
                this.officeOCX.Alert("error");
            }
        } catch(e){
            alert(e.description);
        }
    },
    print : function (){
        try{
            var falg = this.officeOCX.WebOpenPrint();
            //alert(falg);
        }catch(e){alert(e.description);}
    },
    protect : function (){
        try{
            this.officeOCX.WebSetProtect(true,"");  //""表示密码为空
        }catch(e){this.officeOCX.Alert(e.description);}
    },
    unProtect : function (){
        try{
            this.officeOCX.WebSetProtect(false,"");  //""表示密码为空
        }catch(e){this.officeOCX.Alert(e.description);}
    },
    enableCopy : function (){
        try{
            this.officeOCX.CopyType=true;
        }catch(e){this.officeOCX.Alert(e.description);}
    },
    unableCopy : function (){
        try{
            this.officeOCX.CopyType=false;
        }catch(e){this.officeOCX.Alert(e.description);}
    },
    showRevision : function (){
        this.officeOCX.WebShow(true);
    },
    hideRevision : function (){
        this.officeOCX.WebShow(false);
    },
    acceptAllRevisions : function (){
        this.officeOCX.WebObject.Application.ActiveDocument.AcceptAllRevisions();
        var mCount = this.officeOCX.WebObject.Application.ActiveDocument.Revisions.Count;
        if(mCount>0){
            return false;
        }else{
            return true;
        }
    },
    isEmpty : function(){
    },
    save: function(){
        if (!this.readonly){
            this.fireEvent("beforeSave");
            var ret = this.officeOCX.WebSave();  //交互OfficeServer的OPTION="SAVEFILE"
            if (ret){
                //this.officeOCX.Alert(this.officeOCX.Status);
            }else{
                //alert(this.officeOCX.Status);
            }
            this.fireEvent("afterSave");
        }
    },
    loadMenu: function(){

        if (!this.isMenuLoad){
            if (this.json.menuEditButtons && this.json.menuEditButtons.length){

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
                    this.toolbarWidget.load();

                }.bind(this));

            }
            this.isMenuLoad = true;
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
    menuAction: function(button){

        switch (button.buttonID){
            case "menu_new":
                this.createNew();
                break;
            case "menu_openfile":
                this.openLocal();
                break;
            case "menu_savefile":
                this.saveLocal();
                break;
            case "revisions":
                this.toggleRevisions(button);
                break;
            case "toolbar":
                // var text = (this.officeOCX.ToolBars) ? MWF.xApplication.process.Xform.LP.menu_toolbar_show : MWF.xApplication.process.Xform.LP.menu_toolbar_hide;
                // button.setText(text);
                // this.officeOCX.ToolBars = !this.officeOCX.ToolBars;
                break;
            case "menu_preview":
                this.print();
                break;
        }
    },
    toggleRevisions: function(button){
        var t = this.revisionsItem.get("text");
        if (t===MWF.xApplication.process.Xform.LP.menu_revisions_show){
            button.setText(MWF.xApplication.process.Xform.LP.menu_revisions_hide);

            this.showRevision();
        }else{
            button.setText(MWF.xApplication.process.Xform.LP.menu_revisions_show);

            this.hideRevision();
        }
    },
    validationMode: function(){},
    validation: function(){return true}
});
