MWF.xApplication.Execution = MWF.xApplication.Execution || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Execution.SettingExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp;
        this.path = "/x_component_Execution/$SettingExplorer/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_Execution/$SettingExplorer/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        this.middleContent = this.app.middleContent;
        //this.middleContent.setStyles({"margin-top":"0px","border":"0px solid #f00"});
        this.createNaviContent();
        this.createContentDiv();

        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));
    },
    resizeWindow: function(){
        var size = this.app.middleContent.getSize();
        this.naviDiv.setStyles({"height":(size.y-40)+"px"});
        this.naviContentDiv.setStyles({"height":(size.y-180)+"px"});
        this.contentDiv.setStyles({"height":(size.y-40)+"px"});
    },
    createNaviContent: function(){
        this.naviDiv = new Element("div.naviDiv",{
            "styles":this.css.naviDiv
        }).inject(this.middleContent);

        this.naviTitleDiv = new Element("div.naviTitleDiv",{
            "styles":this.css.naviTitleDiv,
            "text": this.lp.systemSetting
        }).inject(this.naviDiv);
        this.naviContentDiv = new Element("div.naviContentDiv",{"styles":this.css.naviContentDiv}).inject(this.naviDiv);
        this.naviBottomDiv = new Element("div.naviBottomDiv",{"styles":this.css.naviBottomDiv}).inject(this.naviDiv);

        var jsonUrl = this.path+"navi.json";
        MWF.getJSON(jsonUrl, function(json){
            json.each(function(data, i){
                var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv);
                naviContentLi.addEvents({
                    "mouseover" : function(ev){
                        if(this.bindObj.currentNaviItem != this.node)this.node.setStyles( this.styles )
                    }.bind({"styles": this.css.naviContentLi_over, "node":naviContentLi, "bindObj": this }) ,
                    "mouseout" : function(ev){
                        if(this.bindObj.currentNaviItem != this.node)this.node.setStyles( this.styles )
                    }.bind({"styles": this.css.naviContentLi, "node":naviContentLi, "bindObj": this }) ,
                    "click" : function(ev){
                        if( this.bindObj.currentNaviItem )this.bindObj.currentNaviItem.setStyles( this.bindObj.css.naviContentLi );
                        this.node.setStyles( this.styles );
                        this.bindObj.currentNaviItem = this.node;
                        if( this.action && this.bindObj[this.action] )this.bindObj[this.action]();
                    }.bind({"styles": this.css.naviContentLi_current, "node":naviContentLi, "bindObj": this, "action" : data.action })
                });
                var naviContentImg = new Element("img.naviContentImg",{
                    "styles":this.css.naviContentImg,
                    "src":"/x_component_Execution/$Main/"+this.options.style+"/icon/"+data.icon
                }).inject(naviContentLi);
                var naviContentSpan = new Element("span.naviContentSpan",{
                    "styles":this.css.naviContentSpan,
                    "text":data.title
                }).inject(naviContentLi);
                if( i == 0 )naviContentLi.click();
            }.bind(this));
        }.bind(this));
    },
    createContentDiv: function(){
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleContent);
    },
    openSystemConfig: function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        this.explorer = new MWF.xApplication.Execution.SettingExplorer.SystemConfigExplorer(this.contentDiv, this.app, this,{style:this.options.style});
        this.explorer.load();
    },
    openSecretarySetting: function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        this.explorer = new MWF.xApplication.Execution.SettingExplorer.SecretarySettingExplorer(this.contentDiv, this.app, this,{style:this.options.style});
        this.explorer.load();
    },
    openCategorySetting: function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        this.explorer = new MWF.xApplication.Execution.SettingExplorer.CategorySettingExplorer(this.contentDiv, this.app, this,{style:this.options.style});
        this.explorer.load();
    }

});




MWF.xApplication.Execution.SettingExplorer.SystemConfigExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app, parent, options) {
        this.container = container;
        this.parent = parent;
        this.app = app;
        this.css = this.parent.css;
        this.lp = this.app.lp;
    },
    load: function () {
        this.container.empty();
        //this.loadToolbar();
        this.loadView();
    },
    destroy : function(){
        if(this.resizeWindowFun)this.app.removeEvent("resize",this.resizeWindowFun);
        this.view.destroy();
    },
    aa:function(){
        if(d.configValue && d.configValue!=''){vl=''}else {var v = d.configValue.split(',');v1 = '';for (i = 0; i < v.length; i++) {if (v1 == '') {v1 = v[i].split('@')[0];} else {v1 = v1 + ',' + v[i].split('@')[0];};}} return v1
    },
    loadToolbar: function(){
        this.toolbar = new Element("div",{
            styles : this.css.toolbar
        }).inject(this.container);

        this.createActionNode = new Element("div",{
            styles : this.css.toolbarActionNode,
            text: this.lp.createConfig
        }).inject(this.toolbar);
        this.createActionNode.addEvent("click",function(){
            var form = new MWF.xApplication.Execution.SettingExplorer.SystemConfigForm(this, {}, {
                onPostOk : function(){
                    this.view.reload();
                }.bind(this)});
            form.create();
        }.bind(this));

        this.fileterNode = new Element("div",{
            styles : this.css.fileterNode
        }).inject(this.toolbar);
    },
    loadView : function(){
        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.container);

        this.resizeWindow();
        this.resizeWindowFun = this.resizeWindow.bind(this);
        this.app.addEvent("resize", this.resizeWindowFun );

        this.view = new MWF.xApplication.Execution.SettingExplorer.SystemConfigView( this.viewContainer, this.app, this, {
            templateUrl : this.parent.path+"listItem_config.json",
            scrollEnable : true
        } );
        this.view.load();
    },
    resizeWindow: function(){
        var size = this.app.content.getSize();
        this.viewContainer.setStyles({"height":(size.y-65)+"px"});
    }
});






MWF.xApplication.Execution.SettingExplorer.SystemConfigView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,

    _createDocument: function(data){
        return new MWF.xApplication.Execution.SettingExplorer.SystemConfigDocument(this.viewNode, data, this.explorer, this);
    },
    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        //var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        //var filter = this.filterData || {};
        this.actions.listConfigAll( function (json) {
            if (callback)callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteConfig(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        var form = new MWF.xApplication.Execution.SettingExplorer.SystemConfigForm(this, documentData, {
            onPostOk : function(){
                this.reload();
            }.bind(this)
        });
        form.edit();
    },
    _queryCreateViewNode: function(){
    },
    _postCreateViewNode: function( viewNode ){
    },
    _queryCreateViewHead:function(){
    },
    _postCreateViewHead: function( headNode ){
    }

});

MWF.xApplication.Execution.SettingExplorer.SystemConfigDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
    }
});


MWF.xApplication.Execution.SettingExplorer.SystemConfigForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "600",
        "height": "290",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Execution.LP.categoryFormTitle,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='configName' width='20%'></td>" +
            "    <td styles='formTableValue' item='configName' width='80%'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='configValue'></td>" +
            "    <td styles='formTableValue' item='configValue'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='orderNumber'></td>" +
            "    <td styles='formTableValue' item='orderNumber'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='description'></td>" +
            "    <td styles='formTableValue' item='description'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        var configValueSetting = {text: this.lp.configValue };
        //alert(JSON.stringify(this.data))
        //alert(this.data.valueType)
        //if( this.data.configCode == "REPORT_WORKFLOW_TYPE" ){
        //    configValueSetting.type = "select";
        //    configValueSetting.selectValue = ["ADMIN_AND_ALLLEADER","DEPLOYER"];
        //}else if( this.data.configCode == "REPORT_SUPERVISOR" ){
        //    configValueSetting.tType = "identity";
        //    configValueSetting.count = 1;
        //}else if( this.data.configCode == "REPORT_AUDIT_LEADER" ){
        //    configValueSetting.tType = "identity";
        //    configValueSetting.count = 0;
        //}else if( this.data.configCode == "COMPANY_WORK_ADMIN" ){
        //    configValueSetting.tType = "identity";
        //    configValueSetting.count = 0;
        //}else if( this.data.configCode == "ARCHIVEMANAGER" ){
        //    configValueSetting.tType = "identity";
        //    configValueSetting.count = 0;
        //}else if( this.data.configCode == "REPORT_AUDIT_LEVEL" ){
        //    configValueSetting.tType = "number";
        //}
        //alert(this.data.valueType)
        configValueSetting.tType = this.data.valueType;
        if(configValueSetting.tType=="select"){
            configValueSetting.type = "select";
            configValueSetting.selectValue = this.data.selectContent.split("|")
        }else if(configValueSetting.tType=="identity"){
            configValueSetting.type="org";
            configValueSetting.orgType = "identity";
            if(this.data.isMultiple){
                configValueSetting.count = 0
            }
        }else if(configValueSetting.tType == "workflow"){
            configValueSetting.type="org";
            configValueSetting.orgType = "process";
        }else{

        }

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "execution",
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    configName: {text: this.lp.configName, type : "innerText" },
                    configValue : configValueSetting,
                    orderNumber: {text: this.lp.orderNumber, type : "innerText" },
                    description: {text: this.lp.description, type : "innerText" }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function (data, callback) {
        //if(this.data.configCode == "APPRAISE_WORKFLOW_ID"){
        if(this.data.valueType == "workflow"){

            if(this.form.getItem("configValue").orgObject){
                this.data.configValue = this.form.getItem("configValue").orgObject[0].data.id
            }else if(this.form.getItem("configValue").dom.orgData){
                this.data.configValue = this.form.getItem("configValue").dom.orgData[0]
            }else{
                this.data.configValue = ""
            }


        }

        this.app.restActions.saveConfig( data, function(json){
            if( callback )callback(json);
            this.fireEvent("postOk")
        }.bind(this));
    }
});



MWF.xApplication.Execution.SettingExplorer.SecretarySettingExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app, parent, options) {
        this.container = container;
        this.parent = parent;
        this.app = app;
        this.css = this.parent.css;
        this.lp = this.app.lp;
    },
    load: function () {
        this.container.empty();
        this.loadToolbar();
        this.loadView();
    },
    destroy : function(){
        if(this.resizeWindowFun)this.app.removeEvent("resize",this.resizeWindowFun);
        this.view.destroy();
    },
    loadToolbar: function(){
        this.toolbar = new Element("div",{
            styles : this.css.toolbar
        }).inject(this.container);

        this.createActionNode = new Element("div",{
            styles : this.css.toolbarActionNode,
            text: this.lp.createSecretary
        }).inject(this.toolbar);
        this.createActionNode.addEvent("click",function(){
            var form = new MWF.xApplication.Execution.SettingExplorer.SecretarySettingForm(this, {}, {
                onPostOk : function(){
                    this.view.reload();
                }.bind(this)});
            form.create();
        }.bind(this));

        this.fileterNode = new Element("div",{
            styles : this.css.fileterNode
        }).inject(this.toolbar);
    },
    loadView : function(){
        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.container);

        this.resizeWindow();
        this.resizeWindowFun = this.resizeWindow.bind(this);
        this.app.addEvent("resize", this.resizeWindowFun );

        this.view = new MWF.xApplication.Execution.SettingExplorer.SecretarySettingView( this.viewContainer, this.app, this, {
            templateUrl : this.parent.path+"listItem_secretary.json",
            scrollEnable : true
        } );
        this.view.load();
    },
    resizeWindow: function(){
        var size = this.app.content.getSize();
        this.viewContainer.setStyles({"height":(size.y-121)+"px"});
    }
});

MWF.xApplication.Execution.SettingExplorer.SecretarySettingView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.SettingExplorer.SecretarySettingDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listSecretaryNext(id, count, filter, function (json) {
            if (callback)callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSecretary(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        var form = new MWF.xApplication.Execution.SettingExplorer.SecretarySettingForm(this, documentData, {
            onPostOk : function(){
                this.reload();
            }.bind(this)
        });
        form.edit();
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

});

MWF.xApplication.Execution.SettingExplorer.SecretarySettingDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    }
});


MWF.xApplication.Execution.SettingExplorer.SecretarySettingForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "600",
        "height": "260",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Execution.LP.secretaryFormTitle,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='secretaryIdentity'></td>" +
            "    <td styles='formTableValue' item='secretaryIdentity'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='leaderIdentity'></td>" +
            "    <td styles='formTableValue' item='leaderIdentity'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='description'></td>" +
            "    <td styles='formTableValue' item='description'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "execution",
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    secretaryIdentity: {text: this.lp.secretaryIdentity, type:"org",orgType: "identity", notEmpty: true},
                    leaderIdentity: {text: this.lp.leaderIdentity, type:"org",orgType: "identity", notEmpty: true},
                    description: {text: this.lp.description, type: "textarea"}
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function (data, callback) {
        this.app.restActions.saveSecretary( data, function(json){
            if( callback )callback(json);
            this.fireEvent("postOk")
        }.bind(this));
    }
});





MWF.xApplication.Execution.SettingExplorer.CategorySettingExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app, parent, options) {
        this.container = container;
        this.parent = parent;
        this.app = app;
        this.css = this.parent.css;
        this.lp = this.app.lp;
    },
    load: function () {
        this.container.empty();
        this.loadToolbar();
        this.loadView();
    },
    destroy : function(){
        if(this.resizeWindowFun)this.app.removeEvent("resize",this.resizeWindowFun);
        this.view.destroy();
    },
    loadToolbar: function(){
        this.toolbar = new Element("div",{
            styles : this.css.toolbar
        }).inject(this.container);

        this.createActionNode = new Element("div",{
            styles : this.css.toolbarActionNode,
            text: this.lp.createCategory
        }).inject(this.toolbar);
        this.createActionNode.addEvent("click",function(){
            var form = new MWF.xApplication.Execution.SettingExplorer.CategorySettingForm(this, {}, {
                onPostOk : function(){
                    this.view.reload();
                }.bind(this)});
            form.create();
        }.bind(this));

        this.fileterNode = new Element("div",{
            styles : this.css.fileterNode
        }).inject(this.toolbar);
    },
    loadView : function(){
        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.container);

        this.resizeWindow();
        this.resizeWindowFun = this.resizeWindow.bind(this);
        this.app.addEvent("resize", this.resizeWindowFun );

        this.view = new MWF.xApplication.Execution.SettingExplorer.CategorySettingView( this.viewContainer, this.app, this, {
            templateUrl : this.parent.path+"listItem_category.json",
            scrollEnable : true
        } );
        this.view.load();
    },
    resizeWindow: function(){
        var size = this.app.content.getSize();
        this.viewContainer.setStyles({"height":(size.y-121)+"px"});
    }
});

MWF.xApplication.Execution.SettingExplorer.CategorySettingView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    
    _createDocument: function(data){
        return new MWF.xApplication.Execution.SettingExplorer.CategorySettingDocument(this.viewNode, data, this.explorer, this);
    },
    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        //var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        //var filter = this.filterData || {};
        this.actions.listCategoryAll( function (json) {
            if (callback)callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteCategory(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        var form = new MWF.xApplication.Execution.SettingExplorer.CategorySettingForm(this, documentData, {
            onPostOk : function(){
                this.reload();
            }.bind(this)
        });
        form.edit();
    },
    _queryCreateViewNode: function(){
    },
    _postCreateViewNode: function( viewNode ){
    },
    _queryCreateViewHead:function(){
    },
    _postCreateViewHead: function( headNode ){
    }

});

MWF.xApplication.Execution.SettingExplorer.CategorySettingDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
    }
});


MWF.xApplication.Execution.SettingExplorer.CategorySettingForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "600",
        "height": "260",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Execution.LP.categoryFormTitle,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='workTypeName'></td>" +
            "    <td styles='formTableValue' item='workTypeName'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='orderNumber'></td>" +
            "    <td styles='formTableValue' item='orderNumber'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='description'></td>" +
            "    <td styles='formTableValue' item='description'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "execution",
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    workTypeName: {text: this.lp.workTypeName, notEmpty: true},
                    orderNumber: {text: this.lp.orderNumber, tType : "number" },
                    description: {text: this.lp.description, type: "textarea"}
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function (data, callback) {
        this.app.restActions.saveCategory( data, function(json){
            if( callback )callback(json);
            this.fireEvent("postOk")
        }.bind(this));
    }
});






