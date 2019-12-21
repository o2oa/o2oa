MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.require("MWF.widget.O2Identity", null,false);

MWF.xApplication.Forum.Setting = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "index" : 0
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp;
        this.path = "/x_component_Forum/$Setting/";
        this.loadCss();
        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_Forum/$Setting/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        //this.middleContent = this.app.middleContent;
        this.createNaviContent();
        this.createContentDiv();

        this.resizeWindowFun = this.resizeWindow.bind(this);
        this.resizeWindow();
        this.app.addEvent("resize", this.resizeWindowFun);
    },
    destroy: function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        if(this.resizeWindowFun)this.app.removeEvent("resize",this.resizeWindowFun);
        this.node.empty();
        delete this;
    },
    resizeWindow: function(){
        var size = this.app.node.getSize();
        var topSize = this.app.topNode ? this.app.topNode.getSize() : {"x": 0, "y": 0};
        // var topSize = {"x": 0, "y": 0};
        //var pt = this.app.contentContainerNode.getStyle("padding-top").toFloat();
        //var pb = this.app.contentContainerNode.getStyle("padding-bottom").toFloat();

        var height = size.y - topSize.y; //- pt - pb;
        this.naviDiv.setStyles({"height":height+"px"});
        this.naviContentDiv.setStyles({"height":(height-120)+"px"});
        this.contentDiv.setStyles({"height":height+"px"});
    },
    createNaviContent: function(){
        this.naviDiv = new Element("div.naviDiv",{
            "styles":this.css.naviDiv
        }).inject(this.node);

        this.naviTitleDiv = new Element("div.naviTitleDiv",{
            "styles":this.css.naviTitleDiv,
            "text": this.lp.setting
        }).inject(this.naviDiv);
        this.naviContentDiv = new Element("div.naviContentDiv",{"styles":this.css.naviContentDiv}).inject(this.naviDiv);
        this.naviBottomDiv = new Element("div.naviBottomDiv",{"styles":this.css.naviBottomDiv}).inject(this.naviDiv);

        var jsonUrl = this.path+"navi.json";
        MWF.getJSON(jsonUrl, function(json){
            json.each(function(data, i){
                var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv);
                naviContentLi.addEvents({
                    "mouseover" : function(ev){
                        if(this.bindObj.currentNaviItem != this.node) this.node.setStyles( this.styles );
                    }.bind({"styles": this.css.naviContentLi_over, "node":naviContentLi, "bindObj": this }),
                    "mouseout" : function(ev){
                        if(this.bindObj.currentNaviItem != this.node)this.node.setStyles( this.styles )
                    }.bind({"styles": this.css.naviContentLi, "node":naviContentLi, "bindObj": this }) ,
                    "click" : function(ev){
                        if( this.bindObj.currentNaviItem )this.bindObj.currentNaviItem.setStyles( this.bindObj.css.naviContentLi );
                        this.node.setStyles( this.styles );
                        this.bindObj.currentNaviItem = this.node;
                        this.node.store( "index" , i );
                        if( this.action && this.bindObj[this.action] )this.bindObj[this.action]();
                    }.bind({"styles": this.css.naviContentLi_current, "node":naviContentLi, "bindObj": this, "action" : data.action })
                });
                var naviContentImg = new Element("img.naviContentImg",{
                    "styles":this.css.naviContentImg,
                    "src":this.path+this.options.style+"/icon/"+data.icon
                }).inject(naviContentLi);
                var naviContentSpan = new Element("span.naviContentSpan",{
                    "styles":this.css.naviContentSpan,
                    "text":data.title
                }).inject(naviContentLi);
                if( i == this.options.index )naviContentLi.click();
            }.bind(this));
        }.bind(this));
    },
    createContentDiv: function(){
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.node);
    },
    openCategorySetting: function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        this.explorer = new MWF.xApplication.Forum.Setting.CategorySettingExplorer(this.contentDiv, this.app, this,{style:this.options.style});
        this.explorer.load();
    },
    openSectionSetting: function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        this.explorer = new MWF.xApplication.Forum.Setting.SectionSettingExplorer(this.contentDiv, this.app, this,{style:this.options.style});
        this.explorer.load();
    },
    openSystemSetting : function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        this.explorer = new MWF.xApplication.Forum.Setting.SystemSettingExplorer(this.contentDiv, this.app, this,{style:this.options.style});
        this.explorer.load();
    },
    openRoleSetting: function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        this.explorer = new MWF.xApplication.Forum.Setting.RoleSettingExplorer(this.contentDiv, this.app, this,{style:this.options.style});
        this.explorer.load();
    },
    openPermissionSetting: function(){
        if( this.contentDiv )this.contentDiv.empty();
        if( this.explorer ){
            this.explorer.destroy();
            delete this.explorer;
        }
        this.explorer = new MWF.xApplication.Forum.Setting.PermissionSettingExplorer(this.contentDiv, this.app, this,{style:this.options.style});
        this.explorer.load();
    }
    //selectPerson: function( item, type , count ){
    //    if( type == "person" ){
    //        var title = this.lp.selectPerson
    //    }else if( type == "unit" ){
    //        title = this.lp.selectDepartment
    //    }
    //    MWF.xDesktop.requireApp("Selector", "package", null, false);
    //    var value = item.get("text").split( "," );
    //    var options = {
    //        "type": type,
    //        "title": title ,
    //        "count" : count,
    //        "values": value || [],
    //        "onComplete": function(items){
    //            var arr = [];
    //            items.each(function(item){
    //                arr.push(item.data.distinguishedName);
    //            }.bind(this));
    //            item.set("text",arr.join(","));
    //        }.bind(this)
    //    };
    //    var selector = new MWF.O2Selector(this.app.content, options);
    //}

});

MWF.xApplication.Forum.Setting.CategorySettingExplorer = new Class({
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
        if( this.app.access.isAdmin() ){
            this.loadToolbar();
        }
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
            var form = new MWF.xApplication.Forum.Setting.CategorySettingForm(this, {}, {
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

        this.view = new MWF.xApplication.Forum.Setting.CategorySettingView( this.viewContainer, this.app, this, {
            templateUrl : this.parent.path+"listItemCategory.json",
            scrollEnable : true
        } );
        this.view.load();
    },
    resizeWindow: function(){
        var size = this.container.getSize();
        if( this.toolbar ){
            this.viewContainer.setStyles({"height":(size.y-121)+"px"});
        }else{
            this.viewContainer.setStyles({"height":(size.y-56)+"px"});
        }
    }
});

MWF.xApplication.Forum.Setting.CategorySettingView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Forum.Setting.CategorySettingDocument(this.viewNode, data, this.explorer, this);
    },
    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        //var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        //var filter = this.filterData || {};
        this.actions.listCategoryAllByAdmin( function (json) {
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
        var form = new MWF.xApplication.Forum.Setting.CategorySettingForm(this, documentData, {
            onPostOk : function(){
                this.reload();
            }.bind(this)
        });
        //if( this.app.access.hasForumAdminAuthority( documentData ) ){
        if( this.app.access.isAdmin() ){
            form.edit();
        }else{
            form.open();
        }
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

MWF.xApplication.Forum.Setting.CategorySettingDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
    }
});

MWF.xApplication.Forum.Setting.CategorySettingForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "1011",
        "height": "90%",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Forum.LP.categoryFormTitle,
        "draggable": true,
        "closeAction": true
    },
    createContent: function () {
        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);

        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);

        this._createTableContent();
    },
    _createTableContent: function () {
        if( !this.data.indexListStyle ){
            this.data.indexListStyle = "type_1_0";
        }
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' lable='forumName' width='10%'></td>" +
            "   <td styles='formTableValue' item='forumName' width='40%'></td>" +
            "   <td styles='formTableTitle' width='10%'></td>" +
            "   <td styles='formTableValue' width='40%'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='forumStatus'></td>" +
            "   <td styles='formTableValue' item='forumStatus'></td>" +
            "   <td styles='formTableTitle' lable='creatorName'></td>" +
            "   <td styles='formTableValue' item='creatorName'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='forumManagerName'></td>" +
            "   <td styles='formTableValue' item='forumManagerName'></td>" +
            "   <td styles='formTableTitle' lable='orderNumber'></td>" +
            "   <td styles='formTableValue' item='orderNumber'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='forumVisible'></td>" +
            "   <td styles='formTableValue' item='forumVisible'></td>" +
            "   <td styles='formTableValue' colspan='2'>" +
            "   </td>" +
            "</tr><tr>" +
            "   <td></td>" +
            "   <td colspan='3'>"+
            "       <div item='forumVisibleResult'></div>"+
            "   </td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='forumColorArea'></td>" +
            "   <td styles='formTableValue'>"+
            "       <div item='forumColorArea' style='float:left;'></div>"+
            "       <div item='forumColorButton' style='float:left;'></div>"+
            "   </td>" +
            "   <td styles='formTableTitle' lable='subjectType'></td>" +
            "   <td styles='formTableValue' item='subjectType'></td>" +
                //"</tr><tr>" +
                //"   <td styles='formTableTitle' lable='indexRecommendable'></td>" +
                // "   <td styles='formTableValue' item='indexRecommendable'></td>" +
                //"   <td styles='formTableTitle' lable=''></td>" +
                //"   <td styles='formTableValue' item=''></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='indexListStyleLable'></td>" +
            "   <td styles='formTableValue' colspan='3'><div item='indexListStyleShow'></div><div item='indexListStyleButton'></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='forumNotice'></td>" +
            "   <td styles='formTableValue' item='forumNotice' colspan='3'></td>" +
            "</tr>"+
        "</table>";
        this.formTableArea.set("html", html);

        //value : function(){ return this.lp.defaultForumColor }.bind(this), defaultValue : this.lp.defaultForumColor

        var formVisibleButtonStyle = (( !this.isEdited && !this.isNew ) || !this.data.forumVisible ||(this.data.forumVisible == this.lp.allPerson )) ? { display:"none"} : { display:""};
        var formVisibleStyle = ( !this.data.forumVisible ||(this.data.forumVisible == this.lp.allPerson )) ? { display:"none"} : { display:""};
        var selectColorButtonStyle = (!this.isEdited && !this.isNew) ? { display : "none" } : {};

        this.indexListStyleShow = this.formTableArea.getElements("[item='indexListStyleShow']")[0];
        if( this.data.indexListStyle ){
            this.getDefaultTypeTemplateList( function(){
                new Element("img", {
                    src : this.defalutTypeTemplateList[this.data.indexListStyle].preview,
                    styles : this.css.indexListStylePreview
                }).inject(this.indexListStyleShow);
            }.bind(this));
        }

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "forum",
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    forumName: {text: this.lp.forumName, notEmpty: true},
                    forumManagerName: { type : "org", text: this.lp.forumManagerName, orgType : "person", "defaultValue" : this.app.userName, "count" : 0 },
                    forumVisible: {text: this.lp.forumVisible, type : "select", selectValue : this.lp.forumVisibleValue.split(","), event : {
                        change : function( it, ev ){
                            var styles = it.getValue() == this.lp.allPerson ? { display : "none" } : { display : "" };
                            it.form.getItem("forumVisibleResult").setStyles( styles );
                        }.bind(this)
                    }},
                    forumVisibleResult : { type : "org", count : 0,  orgType : ["person","unit"], value : function(){ return this.getRoleMemberByCode("FORUM_GUEST_") }.bind(this), style : formVisibleStyle },
                    indexListStyleLable: {text: this.lp.indexListStyle }, //selectValue : this.lp.indexListStyleValue.split(",")
                    indexListStyleButton: { type : "button", value: this.lp.indexListStyleButton, event : {
                        click : function( it, ev ){ this.selectIndexType() }.bind(this)
                    } },
                    forumIndexStyle: {text: this.lp.forumIndexStyle, type : "select", selectValue : this.lp.forumIndexStyleValue.split(",") },
                    indexRecommendable: {text: this.lp.indexRecommendable, type : "select", selectValue : ["true","false"], selectText : this.lp.yesOrNo.split(",") },
                    subjectNeedAudit: {text: this.lp.subjectNeedAudit, type : "select", selectValue : ["true","false"], selectText : this.lp.yesOrNo.split(","), defaultValue : "false" },
                    replyNeedAudit: {text: this.lp.replyNeedAudit, type : "select", selectValue : ["true","false"], selectText : this.lp.yesOrNo.split(","), defaultValue : "false"  },
                    //sectionCreateAble: {text: this.lp.sectionCreateAble, type : "select", selectValue : ["true","false"], selectText : this.lp.yesOrNo.split(",")  },
                    creatorName: {text: this.lp.creatorName, type : "org", isEdited : false,  "defaultValue" : this.app.userName },
                    forumStatus: {text: this.lp.forumStatus, type : "select", selectValue : this.lp.forumStatusValue.split(",") },
                    orderNumber: {text: this.lp.orderNumber, tType : "number" },
                    forumColorArea: {text: this.lp.forumColor, type: "innerText" },
                    forumColorButton : { value : this.lp.selectColor , type : "button", style : selectColorButtonStyle, event : {
                        click : function( it, ev ){ this.selectColor()  }.bind(this)
                    }},
                    subjectType: {text: this.lp.subjectType, type: "text", defaultValue : this.lp.subjectTypeDefaultValue },
                    forumNotice: {text: this.lp.forumNotice, type: "rtf", RTFConfig : { skin : "bootstrapck" }}
                }
            }, this.app);
            this.form.load();

            var forumColorArea = this.formTableArea.getElements("[item='forumColorArea']")[0];
            this.forumColorNode = new Element( "div", {
                styles : { "font-size":"12px", height : "24px", "line-height" : "24px", width : "70px", "margin-right" : "20px" , "color" : "#fff", "text-align" : "center", "background-color" : this.data.forumColor || this.lp.defaultForumColor },
                text : "效果"
            }).inject( forumColorArea )


        }.bind(this), true);
    },
    selectColor: function(){
        var form = new MWF.xApplication.Forum.Setting.ForumColorForm(this, {}, {
            onPostOk : function( color ){
                this.forumColorNode.setStyle( "background-color" , color );
                this.data.forumColor = color;
            }.bind(this)
        });
        form.edit();
    },
    _ok: function (data, callback) {
        this.app.restActions.saveCategory( data, function(json){
            this.saveRole( json.data.id, function(){
                if( callback )callback(json);
                this.fireEvent("postOk")
            }.bind(this))
        }.bind(this));
    },
    saveRole : function( id, callback ){

        var data = this.form.getResult(true, null, true, false, true);
        if( this.isNew )data.id = id;

        this.saveRoleMember( true,  "forumManagerName" ,"FORUM_SUPER_MANAGER_", data, true );

        var flag = data.forumVisible != this.lp.allPerson;
        this.saveRoleMember( flag,  "forumVisibleResult" ,"FORUM_GUEST_", data );


        if( callback )callback();
    },
    saveRoleMember : function( flag, dataKey, code, data , isSingle, callback ){
        var orgArray = [];
        if( flag ){
            if( isSingle ){
                if( data[ dataKey ] ){
                    data[ dataKey ].each( function( p ){
                        if( p!= "") orgArray.push( { objectName : p, objectType : "人员" } );
                    })
                }
            }else{
                if( data[ dataKey ] ){
                    data[ dataKey ].each( function( p ){
                        var flag = p.substr(p.length-1, 1);
                        switch (flag.toLowerCase()){
                            case "p":
                                orgArray.push( { objectName : p, objectType : "人员" } );
                                break;
                            case "u":
                                orgArray.push( { objectName : p, objectType : "组织" } );
                                break;
                            default:
                                orgArray.push( { objectName : p, objectType : "人员" } );
                        }
                    })
                }
            }
        }
        var d = {
            bindObjectArray : orgArray,
            bindRoleCode : code + this.data.id
        };
        this.app.restActions.bindRole( d, function( rData ){
            if( callback )callback(json);
        }.bind(this))
    },
    getRoleMemberByCode : function( code ){
        if( !this.RoleMember )this.RoleMember = {};
        if( this.RoleMember[ code ] ){
            return this.RoleMember[ code ];
        }
        var r = this.RoleMember[ code ] = [];
        if( !this.data.id ){
            return r;
        }
        this.RoleMember[ code ] = r;
        if( this.data && this.data.id ){
            this.actions.listRoleMemberByCode( { "bindRoleCode" : code+ this.data.id }, function(json){
                json.data = json.data || [];
                json.data.each( function( d ){
                    r.push( d.objectName );
                }.bind(this) )
            }, function(){}, false );
        }
        //}
        return r;
    },
    selectIndexType : function(){
        this.getDefaultTypeTemplateList( function(){
            this.selectTypeTemplate();
        }.bind(this))
    },
    getDefaultTypeTemplateList : function(callback){
        if (this.defalutTypeTemplateList){
            if (callback) callback();
        }else{
            var url = "/x_component_Forum/$ColumnTemplate/template/setting.json";
            MWF.getJSON(url, function(json){
                this.defalutTypeTemplateList = json;
                if (callback) callback();
            }.bind(this));
        }
    },
    selectTypeTemplate: function(e){
        this.typeTemplateList = null;
        var _self = this;

        var createTemplateMaskNode = new Element("div", {"styles": this.css.createTemplateMaskNode}).inject(this.app.content);
        var createTemplateAreaNode = new Element("div", {"styles": this.css.createTypeTemplateAreaNode}).inject(this.app.content);
        createTemplateAreaNode.fade("in");

        var createTemplateTitleNode = new Element("div", {"styles": this.css.createTemplateFormTitleNode, "text": this.app.lp.selectIndexType }).inject(createTemplateAreaNode);
        var createTemplateCategoryNode = new Element("div", {"styles": this.css.createTemplateFormCategoryNode}).inject(createTemplateAreaNode);
        var createTemplateCategoryTitleNode = new Element("div", {"styles": this.css.createTemplateFormCategoryTitleNode, "text": this.app.lp.typeColumn}).inject(createTemplateCategoryNode);

        var createTemplateContentNode = new Element("div", {"styles": this.css.createTemplateFormContentNode}).inject(createTemplateAreaNode);

        var createTemplateCategoryAllNode = new Element("div", {"styles": this.css.createTemplateFormCategoryItemNode, "text": this.app.lp.all}).inject(createTemplateCategoryNode);
        createTemplateCategoryAllNode.addEvent("click", function(){
            loadAllTemplates();
        });

        var columnCountList = [];
        this.getDefaultTypeTemplateList( function(){
            for( var key in this.defalutTypeTemplateList ) {
                var template = this.defalutTypeTemplateList[key];
                if( !columnCountList.contains( template.column ) )columnCountList.push( template.column )
            }
        }.bind(this));
        columnCountList.each(function( columnCount ){
            var createTemplateCategoryItemNode = new Element("div", {"styles": this.css.createTemplateFormCategoryItemNode, "text":  columnCount+"列", "value": columnCount}).inject(createTemplateCategoryNode);
            createTemplateCategoryItemNode.addEvent("click", function(){
                createTemplateContentNode.empty();
                createTemplateCategoryNode.getElements("div").each(function(node, i){
                    if (i>0) node.setStyles(_self.css.createTemplateFormCategoryItemNode);
                });
                this.setStyles(_self.css.createTemplateFormCategoryItemNode_current);
                loadDefaultTemplate(this.get("value"));
            });
        }.bind(this));

        var resize = function(){
            var size = this.app.content.getSize();
            var y = (size.y*0.1)/2;
            var x = (size.x*0.1)/2;
            if (y<0) y=0;
            if (x<0) x=0;
            createTemplateAreaNode.setStyles({
                "top": ""+y+"px",
                "left": ""+x+"px"
            });
            y = size.y*0.9-createTemplateCategoryNode.getSize().y-70;
            createTemplateContentNode.setStyle("height", ""+y+"px");
        }.bind(this);
        resize();
        this.app.addEvent("resize", resize);


        var loadDefaultTemplate = function( columnCount ){
            this.getDefaultTypeTemplateList(function(){
                for( var key in this.defalutTypeTemplateList ){
                    var template = this.defalutTypeTemplateList[key];
                    if( columnCount && template.column != parseInt( columnCount ) ){
                        continue;
                    }
                    template.key = key;
                    var templateNode = new Element("div", {"styles": this.css.typeTemplateNode}).inject(createTemplateContentNode);
                    var templateIconNode = new Element("div", {"styles": this.css.typeTemplateIconNode}).inject(templateNode);
                    //var templateTitleNode = new Element("div", {"styles": this.css.typeTemplateTitleNode, "text": template.title}).inject(templateNode);
                    templateNode.store("template", template.key);

                    var templateIconImgNode = new Element("img", {"styles": this.css.typeTemplateIconImgNode}).inject(templateIconNode);
                    templateIconImgNode.set("src", template.preview);

                    templateNode.addEvents({
                        "mouseover": function(){this.setStyles(_self.css.typeTemplateNode_over)},
                        "mouseout": function(){this.setStyles(_self.css.typeTemplateNode)},
                        "mousedown": function(){this.setStyles(_self.css.typeTemplateNode_down)},
                        "mouseup": function(){this.setStyles(_self.css.typeTemplateNode_over)},
                        "click": function(e){
                            selectType(e, this.retrieve("template"));
                            _self.app.removeEvent("resize", resize);
                            createTemplateAreaNode.destroy();
                            createTemplateMaskNode.destroy();
                        }
                    });
                }
            }.bind(this));
        }.bind(this);

        var selectType = function( e, type ){
            this.data.indexListStyle = type;
            this.indexListStyleShow.empty();
            new Element("img", {
                src : this.defalutTypeTemplateList[type].preview,
                styles : this.css.indexListStylePreview
            }).inject(this.indexListStyleShow);
        }.bind(this);

        var loadAllTemplates = function(){
            createTemplateContentNode.empty();
            createTemplateCategoryNode.getElements("div").each(function(node, i){
                if (i>0) node.setStyles(_self.css.createTemplateFormCategoryItemNode);
            });
            createTemplateCategoryAllNode.setStyles(_self.css.createTemplateFormCategoryItemNode_current);
            loadDefaultTemplate();
        };
        loadAllTemplates();

        createTemplateMaskNode.addEvent("click", function(){
            this.app.removeEvent("resize", resize);
            createTemplateAreaNode.destroy();
            createTemplateMaskNode.destroy();
        }.bind(this));

    }
});


MWF.xApplication.Forum.Setting.ForumColorForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "820",
        "height": "280",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": false,
        "title": MWF.xApplication.Forum.LP.forumColorFormTitle,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {

        var div = new Element("div",{
            "styles" : this.css.sectionFormContent
        }).inject(this.formTableArea);
        div.setStyle( "margin-top" , "10px" );

        this.lp.optionsForumColors.each(function(arr, i){
            arr.each( function(c , j ){
                var iconAreaNode = new Element("div",{
                    "styles" : this.css.iconAreaNode
                }).inject(div);
                var iconNode = new Element("div",{
                    "styles" : {height : "20px", width : "40px", "margin" : "5px 5px 5px 5px" , "background-color" : c, "cursor" : "pointer"}
                }).inject(iconAreaNode);

                iconAreaNode.store( "color", c );
                iconAreaNode.addEvents({
                    "click" : function(){
                        this.obj.fireEvent("postOk", this.node.retrieve("color") );
                        this.obj.close();
                    }.bind({ obj : this, node : iconAreaNode })
                })
            }.bind(this))
        }.bind(this));
    }
});

MWF.xApplication.Forum.Setting.SectionSettingExplorer = new Class({
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
            text: this.lp.createSection
        }).inject(this.toolbar);
        this.createActionNode.addEvent("click",function(){
            var form = new MWF.xApplication.Forum.Setting.SectionSettingForm(this, {}, {
                onPostOk : function(){
                    this.view.reload();
                }.bind(this)});
            form.create();
        }.bind(this));

        this.loadCategoryBar();

        //this.fileterNode = new Element("div",{
        //    styles : this.css.fileterNode
        //}).inject(this.toolbar);
    },
    loadCategoryBar : function(){
        var _self = this;
        //this.categoryBar = new Element("div.categoryBar",{"styles":this.css.categoryBar}).inject(this.contentDiv);

        this.allCategoryNode = new Element("li.allCategoryNode", {
            "styles": this.css.categoryNode,
            "text" : "全部"
        }).inject(this.toolbar);
        this.allCategoryNode.addEvents({
            "mouseover" : function(){ if( this.currentCategoryNode != this.allCategoryNode)this.allCategoryNode.setStyles(this.css.categoryNode_over) }.bind(this),
            "mouseout" : function(){ if( this.currentCategoryNode != this.allCategoryNode)this.allCategoryNode.setStyles(this.css.categoryNode) }.bind(this),
            "click":function(){
                if( this.currentCategoryNode )this.currentCategoryNode.setStyles(this.css.categoryNode);
                this.currentCategoryNode = this.allCategoryNode;
                this.allCategoryNode.setStyles(this.css.categoryNode_current);
                this.loadView(  )
            }.bind(this)
        });
        var isManager = false;
        this.forumAdminObj = {};
        this.app.restActions.listCategoryAllByAdmin( function( json ){
                json.data = json.data || [];
                json.data.each( function( d ){
                    var flag = this.app.access.hasForumAdminAuthority( d );
                    this.forumAdminObj[d.id] = flag;
                    if( !isManager ){
                        isManager = flag;
                    }
                    var categoryNode = new Element("li.categoryNode", {
                        "styles": this.css.categoryNode,
                        "text" : d.forumName
                    }).inject(this.toolbar);
                    categoryNode.store( "categoryId" , d.id );
                    categoryNode.addEvents({
                        "mouseover" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.categoryNode_over) }.bind({node : categoryNode }),
                        "mouseout" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.categoryNode) }.bind({node : categoryNode }),
                        "click":function(){
                            if( _self.currentCategoryNode )_self.currentCategoryNode.setStyles(_self.css.categoryNode);
                            _self.currentCategoryNode = this.node;
                            this.node.setStyles(_self.css.categoryNode_current);
                            _self.loadView(  )
                        }.bind({ name : d.id, node : categoryNode })
                    })
                }.bind(this))
            }.bind(this), null, false
        );
        if( !isManager )this.createActionNode.destroy();
        this.allCategoryNode.click();
    },
    loadView : function(){
        var categoryId;
        if( this.currentCategoryNode ){
            categoryId = this.currentCategoryNode.retrieve("categoryId");
        }
        categoryId = categoryId || "all";

        if(this.viewContainer)this.viewContainer.destroy();
        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.container);

        this.resizeWindow();
        this.resizeWindowFun = this.resizeWindow.bind(this);
        this.app.addEvent("resize", this.resizeWindowFun );

        this.view = new MWF.xApplication.Forum.Setting.SectionSettingView( this.viewContainer, this.app, this, {
            templateUrl : this.parent.path+"listItemSection.json",
            scrollEnable : true,
            categoryId : categoryId
        } );
        this.view.load();
    },
    resizeWindow: function(){
        var size = this.container.getSize();
        this.viewContainer.setStyles({"height":(size.y-65)+"px"});
    }
});

MWF.xApplication.Forum.Setting.SectionSettingView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Forum.Setting.SectionSettingDocument(this.viewNode, data, this.explorer, this);
    },
    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        //var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        //var filter = this.filterData || {};
        if( this.options.categoryId == "all" ){
            this.actions.listSectionAll( function (json) {
                if( !json.data )json.data = [];
                if (callback)callback(json);
            }.bind(this))
        }else{
            this.actions.listSectionByAdmin( this.options.categoryId, function (json) {
                if( !json.data )json.data = [];
                if (callback)callback(json);
            }.bind(this))
        }
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSection(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        var form = new MWF.xApplication.Forum.Setting.SectionSettingForm(this, documentData, {
            title : this.lp.sectionFormTitle + " - " + documentData.sectionName,
            onPostOk : function(){
                this.reload();
            }.bind(this)
        });
        //this.app.access.hasSectionAdminAuthority( documentData , function( flag ){  只有分区管理员可以对分区以下的板块进行增删改，版主不能
        this.app.access.hasForumAdminAuthority( documentData.forumId , function( flag ){
            flag ?  form.edit() : form.open();
        } )
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

MWF.xApplication.Forum.Setting.SectionSettingDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverSection : function(sectionNode, ev){
        var removeNode = sectionNode.getElements("[styles='sectionRemoveNode']")[0];
        if( removeNode )removeNode.setStyle("opacity",1)
    },
    mouseoutSection : function(sectionNode, ev){
        var removeNode = sectionNode.getElements("[styles='sectionRemoveNode']")[0];
        if( removeNode )removeNode.setStyle("opacity",0)
    },
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
    }
});

MWF.xApplication.Forum.Setting.SectionSettingForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "1100",
        "height": "90%",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Forum.LP.sectionFormTitle,
        "draggable": true,
        "closeAction": true
    },
    createToolbar: function(){
        var _self = this;

        this.toolbar = new Element("div",{
            styles : this.css.formToolbar
        }).inject(this.formNode);

        var categoryNode = new Element("li.categoryNode", {
            "styles": this.css.formCategoryNode,
            "text" : this.lp.baseSetting
        }).inject(this.toolbar);
        categoryNode.addEvents({
            "mouseover" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.formCategoryNode_over) }.bind({node : categoryNode }),
            "mouseout" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.formCategoryNode) }.bind({node : categoryNode }),
            "click":function(){
                if( _self.currentCategoryNode )_self.currentCategoryNode.setStyles(_self.css.formCategoryNode);
                _self.currentCategoryNode = this.node;
                this.node.setStyles(_self.css.formCategoryNode_current);
                _self.baseContainer.setStyle("display","");
                _self.permissionContainer.setStyle("display","none");
            }.bind({ node : categoryNode })
        });
        categoryNode.setStyles( this.css.formCategoryNode_current );
        _self.currentCategoryNode = categoryNode;

        var categoryNode = new Element("li.categoryNode", {
            "styles": this.css.formCategoryNode,
            "text" : this.lp.permissionSetting
        }).inject(this.toolbar);
        categoryNode.addEvents({
            "mouseover" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.formCategoryNode_over) }.bind({node : categoryNode }),
            "mouseout" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.formCategoryNode) }.bind({node : categoryNode }),
            "click":function(){
                if( _self.currentCategoryNode )_self.currentCategoryNode.setStyles(_self.css.formCategoryNode);
                _self.currentCategoryNode = this.node;
                this.node.setStyles(_self.css.formCategoryNode_current);
                _self.baseContainer.setStyle("display","none");
                _self.permissionContainer.setStyle("display","");
            }.bind({ node : categoryNode })
        })
    },
    createContent: function () {
        this.createToolbar();

        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);

        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);

        this._createTableContent();
    },
    _createTableContent: function () {
        _self = this;
        this.baseContainer = new Element("div").inject(this.formTableArea);

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' lable='sectionName' width='10%'></td>" +
            "   <td styles='formTableValue' item='sectionName' width='40%'></td>" +
            "   <td styles='formTableTitle' lable='forumId' width='10%'></td>" +
            "   <td styles='formTableValue' item='forumId' width='40%'></td>" +
            "</tr><tr>" +
                //"   <td styles='formTableTitle' lable='sectionType'></td>" +
                //"   <td styles='formTableValue' item='sectionType'></td>" +
                //"</tr><tr>" +
            "   <td styles='formTableTitle' lable='sectionStatus'></td>" +
            "   <td styles='formTableValue' item='sectionStatus'></td>" +
            "   <td styles='formTableTitle' lable='orderNumber'></td>" +
            "   <td styles='formTableValue' item='orderNumber'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='typeCatagory'></td>" +
            "   <td styles='formTableValue' item='typeCatagory'></td>" +
            "   <td styles='formTableTitle' lable='subjectType'></td>" +
            "   <td styles='formTableValue' item='subjectType'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='creatorName'></td>" +
            "   <td styles='formTableValue' item='creatorName'></td>" +
            "   <td styles='formTableTitle' lable='createTime'></td>" +
            "   <td styles='formTableValue' item='createTime'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='sectionIcon'></td>" +
            "   <td styles='formTableValue' item='sectionIconArea' colspan='3' valign='bottom'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='sectionDescription'></td>" +
            "   <td styles='formTableValue' item='sectionDescription' colspan='3'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='sectionNotice'></td>" +
            "   <td styles='formTableValue' item='sectionNotice' colspan='3'></td>" +
            "</tr>"+
        "</table>";
        this.baseContainer.set("html", html);

        this.permissionContainer = new Element("div", { styles : {"display":"none"} }).inject( this.formTableArea );

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' lable='moderatorNames'></td>" +
            "   <td styles='formTableValue' item='moderatorNames' colspan='3'></td>"+
            "</tr><tr>" +

            "   <td styles='formTableTitle' lable='sectionVisible' width='10%'></td>" +
            "   <td styles='formTableValue' item='sectionVisible' width='20%'></td>" +
            "   <td styles='formTableValue' width='70%' colspan='2'></td>" +
            "</tr><tr>" +
            "   <td></td>"+
            "   <td styles='formTableValue' colspan='3'>"+
            "       <div styles='formItemSpan' item='sectionVisibleResult'></div>"+
            " </td>" +

            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='subjectPublishAble'></td>" +
            "   <td styles='formTableValue' item='subjectPublishAble'></td>" +
            "   <td styles='formTableValue' colspan='2'></td>" +
            "</tr><tr>" +
            "   <td></td>"+
            "   <td styles='formTableValue' colspan='3'>"+
            "       <div styles='formItemSpan' item='subjectPublishResult'></div>"+
            " </td>" +

            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='replyPublishAble'></td>" +
            "   <td styles='formTableValue' item='replyPublishAble'></td>" +
            "   <td styles='formTableValue' colspan='2'></td>" +
            "</tr><tr>" +
            "   <td></td>"+
            "   <td styles='formTableValue' colspan='3'>"+
            "       <div styles='formItemSpan' item='replyPublishResult'></div>"+
            " </td>" +

            "</tr><tr item='indexRecommendTr' style='"+ ( this.data.sectionVisible == this.lp.byPermission  ? "display:none;" : "display:;" ) +"'>" +
            "   <td styles='formTableTitle' lable='indexRecommendable'></td>" +
            "   <td styles='formTableValue' item='indexRecommendable'></td>" +
            "   <td styles='formTableTitle' lable='indexRecommenPerson' style='"+ ( this.data.indexRecommendable == false  ? "display:none;" : "display:;" ) +"'></td>" +
            "   <td styles='formTableValue' item='indexRecommenPerson' style='"+ ( this.data.indexRecommendable == false ? "display:none;" : "display:;" ) +"'></td>" +

            //"</tr><tr>" +
            //"   <td styles='formTableTitle' lable='subjectNeedAudit' width='10%'></td>" +
            //"   <td styles='formTableValue' item='subjectNeedAudit' width='20%'></td>" +
            //"   <td styles='formTableTitle' lable='subjectAuditPerson' width='10%' style='"+ ( this.data.subjectNeedAudit == true  ? "display:;" : "display:none;" ) +"'></td>" +
            //"   <td styles='formTableValue' item='subjectAuditPerson' width='60%' style='"+ ( this.data.subjectNeedAudit == true  ? "display:;" : "display:none;" ) +"'></td>" +
            //
            //"</tr><tr>" +
            //"   <td styles='formTableTitle' lable='replyNeedAudit' width='10%'></td>" +
            //"   <td styles='formTableValue' item='replyNeedAudit' width='20%'></td>" +
            //"   <td styles='formTableTitle' lable='replyAuditPerson' width='10%' style='"+ ( this.data.replyNeedAudit == true  ? "display:;" : "display:none;" ) +"'></td>" +
            //"   <td styles='formTableValue' item='replyAuditPerson' width='60%' style='"+ ( this.data.replyNeedAudit == true  ? "display:;" : "display:none;" ) +"'></td>" +
            "</tr>"+
        "</table>";
        this.permissionContainer.set("html", html);

        var forumNames = [""];
        var forumIds = [""];

        this.app.restActions.listCategoryAllByAdmin( function( json ){
            json.data.each( function( d ){
                if( this.isNew ){
                    if( this.app.access.hasForumAdminAuthority( d ) ){
                        forumNames.push(d.forumName );
                        forumIds.push(d.id);
                    }
                }else{
                    forumNames.push(d.forumName );
                    forumIds.push(d.id);
                }
            }.bind(this))
        }.bind(this), null ,false);

        if( !this.data.typeCatagory ){
            this.data.typeCatagory = this.lp.typeCategorySelectValue.split("|");
        }else{
            this.data.typeCatagory = typeof this.data.typeCatagory == "string" ? this.data.typeCatagory.split("|") : this.data.typeCatagory;
        }
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "forum",
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    sectionName: {text: this.lp.sectionName, notEmpty: true},
                    forumId: {text: this.lp.owneForum, type : "select", "selectText" : forumNames, "selectValue" : forumIds , notEmpty: true, isEdited : function(){ return this.isNew }.bind(this)},

                    sectionVisible: {text: this.lp.sectionVisible, type : "select", selectValue : this.lp.sectionVisibleValue.split(","), event : {
                        change : function( it, ev ){
                            this.setItemStyle( it, "sectionVisible" );
                            if( it.getValue() == this.lp.allPerson ){
                                this.formTableArea.getElements("[item='indexRecommendTr']")[0].setStyle("display","")
                            }else{
                                this.formTableArea.getElements("[item='indexRecommendTr']")[0].setStyle("display","none")
                            }
                        }.bind(this)
                    }},
                    sectionVisibleResult : { type : "org", orgType : ["person","unit"], count :0 , value : function(){ return this.getRoleMemberByCode("SECTION_GUEST_") }.bind(this), style : this.getContainerStyle("sectionVisible") },

                    subjectPublishAble: {text: this.lp.subjectPublishAble, type : "select", selectValue : this.lp.subjectPublishAbleValue.split(","), event : {
                        change : function( it, ev ){  this.setItemStyle( it, "subjectPublish" ); }.bind(this)
                    }},
                    subjectPublishResult : { type : "org", orgType : ["person","unit"], count :0 , value : function(){ return this.getRoleMemberByCode("SECTION_SUBJECT_PUBLISHER_") }.bind(this), style : this.getContainerStyle("subjectPublishAble") },

                    replyPublishAble: {text: this.lp.replyPublishAble, type : "select", selectValue : this.lp.replyPublishAbleValue.split(","), event : {
                        change : function( it, ev ){  this.setItemStyle( it, "replyPublish" ); }.bind(this)
                    }},
                    replyPublishResult : { type : "org", orgType : ["person","unit"], count :0 , value : function(){ return this.getRoleMemberByCode("SECTION_REPLY_PUBLISHER_") }.bind(this), style : this.getContainerStyle("replyPublishAble") },

                    indexRecommendable: {text: this.lp.indexRecommendable, type : "select", selectValue : ["true","false"], selectText : this.lp.yesOrNo.split(",") , defaultValue : "true" ,event : {
                        change : function( it, ev ){
                            var styles = it.getValue() == "true" ? {"display":""} : {"display":"none"};
                            this.permissionContainer.getElements("[item='indexRecommenPerson']")[0].setStyles( styles );
                            this.permissionContainer.getElements("[lable='indexRecommenPerson']")[0].setStyles( styles );
                        }.bind(this)
                    }},
                    indexRecommenPerson : { type : "org", text: this.lp.indexRecommenPerson , orgType : "person", count : 0, value : function(){
                        var v = this.getRoleMemberByCode("SECTION_RECOMMENDER_");
                        return v == "" ? this.app.userName : v;
                    }.bind(this)},

                    subjectNeedAudit: {text: this.lp.subjectNeedAudit, type : "select", selectValue : ["true","false"], selectText : this.lp.yesOrNo.split(","), defaultValue : "false" ,event : {
                        change : function( it, ev ){
                            var styles = it.getValue() == "true" ? {"display":""} : {"display":"none"};
                            this.permissionContainer.getElements("[item='subjectAuditPerson']")[0].setStyles( styles );
                            this.permissionContainer.getElements("[lable='subjectAuditPerson']")[0].setStyles( styles );
                        }.bind(this)
                    }},
                    subjectAuditPerson : { type : "org", text: this.lp.auditPerson , orgType : "person", count : 0, value : function(){ return this.getRoleMemberByCode("SECTION_SUBJECT_AUDITOR_") }.bind(this) },

                    replyNeedAudit: {text: this.lp.replyNeedAudit, type : "select", selectValue : ["true","false"], selectText : this.lp.yesOrNo.split(",") , defaultValue : "false" ,event : {
                        change : function( it, ev ){
                            var styles = it.getValue() == "true" ? {"display":""} : {"display":"none"};
                            this.permissionContainer.getElements("[item='replyAuditPerson']")[0].setStyles( styles );
                            this.permissionContainer.getElements("[lable='replyAuditPerson']")[0].setStyles( styles );
                        }.bind(this)
                    }},
                    replyAuditPerson : { type : "org", text: this.lp.auditPerson , orgType : "person", count : 0, value : function(){ return this.getRoleMemberByCode("SECTION_REPLY_AUDITOR_") }.bind(this) },

                    moderatorNames : {type : "org",text:this.lp.moderatorNames, orgType:"person", count : 0,  defaultValue : this.app.userName },
                    sectionType : {text: this.lp.sectionType, type : "select", selectValue : this.lp.sectionTypeValue.split(",") },
                    //sectionCreateAble: {text: this.lp.sectionCreateAble, type : "select", selectValue : ["true","false"], selectText : this.lp.yesOrNo.split(",")  },
                    creatorName: { text: this.lp.creatorName, type : "org", isEdited : false, "defaultValue" : this.app.userName },
                    createTime: {text: this.lp.createTime, type : "innerText" },
                    sectionStatus: {text: this.lp.sectionStatus, type : "select", selectValue : this.lp.sectionStatusValue.split(",") },
                    orderNumber: {text: this.lp.orderNumber, tType : "number" },
                    subjectType: {text: this.lp.subjectType, defaultValue : this.lp.subjectTypeDefaultValue },
                    typeCatagory: {text: this.lp.typeCatagory, selectValue : this.lp.typeCategorySelectValue.split("|"),  type : "checkbox", notEmpty : true},
                    sectionDescription : {text: this.lp.sectionDescription, type: "textarea", style:{"height":"45px"} },
                    sectionNotice: {text: this.lp.sectionNotice, type: "rtf", RTFConfig : { skin : "bootstrapck" } },
                    sectionIcon : { text : this.lp.sectionIcon }
                }
            }, this.app);
            this.form.load();

            this.formTableArea.getElement("[lable='indexRecommenPerson']").setStyle("text-align","right");

            this.createIconNode();


        }.bind(this), true);
    },
    saveRole : function( id, callback ){
        var data = this.form.getResult(true, null, true, false, true);
        if( this.isNew )data.id = id;

        this.saveRoleMember( true,  "moderatorNames" ,"SECTION_MANAGER_", data, true );
        //this.saveRoleMember( true,  "moderatorNames" ,"SECTION_MANAGER_", data );

        var flag = data.sectionVisible != this.lp.allPerson;
        this.saveRoleMember( flag,  "sectionVisibleResult" ,"SECTION_GUEST_", data );

        flag = data.subjectPublishAble != this.lp.allPerson;
        this.saveRoleMember( flag,  "subjectPublishResult" ,"SECTION_SUBJECT_PUBLISHER_", data );

        flag = data.replyPublishAble != this.lp.allPerson;
        this.saveRoleMember( flag,  "replyPublishResult" ,"SECTION_REPLY_PUBLISHER_", data );

        flag = data.indexRecommendable == "true";
        this.saveRoleMember( flag,  "indexRecommenPerson" ,"SECTION_RECOMMENDER_", data, true );

        flag = data.subjectNeedAudit == "true";
        this.saveRoleMember( flag,  "subjectAuditPerson" ,"SECTION_SUBJECT_AUDITOR_", data, true );

        flag = data.replyNeedAudit == "true";
        this.saveRoleMember( flag,  "replyAuditPerson" ,"SECTION_REPLY_AUDITOR_", data, true );

        if( callback )callback();
    },
    saveRoleMember : function( flag, dataKey, code, data , isSingle, callback ){
        var orgArray = [];
        if( flag ){
            if( isSingle ){
                if( data[ dataKey ] ){
                    data[ dataKey ].each( function( p ){
                        if( p!= "") orgArray.push( { objectName : p, objectType : "人员" } );
                    })
                }
            }else{
                if( data[ dataKey ] ){
                    data[ dataKey ].each( function( p ){
                        var flag = p.substr(p.length-1, 1);
                        switch (flag.toLowerCase()){
                            case "p":
                                orgArray.push( { objectName : p, objectType : "人员" } );
                                break;
                            case "u":
                                orgArray.push( { objectName : p, objectType : "组织" } );
                                break;
                            default:
                                orgArray.push( { objectName : p, objectType : "人员" } );
                        }
                    })
                }
            }
        }
        var d = {
            bindObjectArray : orgArray,
            bindRoleCode : code + this.data.id
        };
        this.app.restActions.bindRole( d, function( rData ){
            if( callback )callback(json);
        }.bind(this))
    },

    getRoleMemberByCode : function( code ){
        if( !this.RoleMember )this.RoleMember = {};
        if( this.RoleMember[ code ] ){
            return this.RoleMember[ code ];
        }
        var r = this.RoleMember[ code ] = [];
        if( !this.data.id ){
            return r;
        }
        this.RoleMember[ code ] = r;
        if( this.data && this.data.id ){
            this.actions.listRoleMemberByCode( { "bindRoleCode" : code+ this.data.id }, function(json){
                json.data = json.data || [];
                json.data.each( function( d ){
                    r.push( d.objectName );
                }.bind(this) )
            }, function(){}, false );
        }
        //}
        return r;
    },
    getButtonStyle : function( key ){
        if( this.isEdited || this.isNew  ){
            return this.getContainerStyle( key )
        }else{
            return { display:"none"};
        }
    },
    getContainerStyle : function( key ){
        return (!this.data[key] ||(this.data[key] == this.lp.allPerson )) ? { display:"none"} : { display:""};
    },
    setItemStyle : function( it, preStr ){
        var styles = it.getValue() == this.lp.allPerson ? { display : "none" } : { display : "" };
        it.form.getItem(preStr + "Result").setStyles( styles );
    },
    createIconNode: function(){
        var iconPth = "/x_component_Forum/$Setting/"+ this.options.style +"/sectionIcon/";
        var defaultIconSrc = iconPth + "forum_icon.png";
        var sectionIconArea = this.formTableArea.getElements("[item='sectionIconArea']")[0];
        this.iconNode = new Element("img",{
            "styles" : this.css.iconNode
        }).inject(sectionIconArea);
        if (this.data.icon){
            this.iconNode.set("src", "data:image/png;base64,"+this.data.icon+"");
        }else{
            this.iconNode.set("src", defaultIconSrc)
        }
        if( this.isEdited || this.isNew ){
            //var selectIconActionNode = new Element("div", {
            //    "styles": this.css.changeIconActionNode,
            //    "text": this.lp.selectIcon
            //}).inject(sectionIconArea);
            //selectIconActionNode.addEvent("click", function () {
            //    this.selectIcon();
            //}.bind(this));

            var changeIconActionNode = new Element("div", {
                "styles": this.css.changeIconActionNode,
                "text": this.lp.uploadIcon
            }).inject(sectionIconArea);
            changeIconActionNode.addEvent("click", function () {
                this.changeIcon();
            }.bind(this));
        }

    },
    selectIcon: function(){
        var form = new MWF.xApplication.Forum.Setting.SectionIconForm(this, {}, {
            onPostOk : function( icon ){
                if( this.formData )this.formData = null;
                this.iconNode.set("src",icon.path );
            }.bind(this)
        });
        form.edit();
    },
    changeIcon: function () {
        if (!this.uploadFileAreaNode) {
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\"/>";
            this.uploadFileAreaNode.set("html", html);

            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", function () {

                var files = fileNode.files;
                if (files.length) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);
                        if (!file.type.match('image.*'))continue;

                        this.file = file;
                        this.formData = new FormData();
                        this.formData.append('file', this.file);

                        if (!window.FileReader) continue;
                        var reader = new FileReader();
                        reader.onload = (function (theFile) {
                            return function (e) {
                                this.iconNode.set("src",e.target.result)
                            }.bind(this);
                        }.bind(this))(file);
                        reader.readAsDataURL(file);
                    }
                }

            }.bind(this));
        }
        var fileNode = this.uploadFileAreaNode.getFirst();
        fileNode.click();
    },
    saveIcon: function (id, callback) {
        this.app.restActions.uploadSectionIcon(id, function () {
            if (callback)callback();
        }.bind(this), null, this.formData, this.file);
    },
    _ok: function (data, callback) {
        debugger;
        if( typeOf(data.moderatorNames) === "string" ){
            data.moderatorNames = data.moderatorNames.split(",");
        }
        data.sectionLevel = "主版块";
        data.typeCatagory = data.typeCatagory.split(",").join("|");
        this.app.restActions.saveSection( data, function(json){
            if( this.formData ){
                this.saveIcon( json.data.id, function(){
                    this.saveRole( json.data.id, function( data ){
                        if( callback )callback(json);
                    }.bind(this) )
                }.bind(this) );
            }else{
                this.saveRole( json.data.id, function( data ){
                    if( callback )callback(json);
                }.bind(this) )
            }
            this.fireEvent("postOk")
        }.bind(this));
    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options.width ? this.options.width : "50%";
        if (!height)height = this.options.height ? this.options.height : "50%";
        if (!top) top = this.options.top ? this.options.top : 0;
        if (!left) left = this.options.left ? this.options.left : 0;

        var allSize = this.app.content.getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);

        top = top || parseInt((limitHeight - height) / 2, 10); //+appTitleSize.y);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.formAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.formNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};
        var toolbarSize = this.toolbar ? this.toolbar.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y - toolbarSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.formContentNode.setStyles({
            "height": "" + contentHeight + "px"
        });
        this.formTableContainer.setStyles({
            "height": "" + contentHeight + "px"
        });
    }
});

MWF.xApplication.Forum.Setting.SectionIconForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "650",
        "height": "400",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": false,
        "title": MWF.xApplication.Forum.LP.sectionIconFormTitle,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        var iconPath = "/x_component_Forum/$Setting/" + this.options.style +"/sectionIcon/";
        var jsonPath = "/x_component_Forum/$Setting/sectionIcon.json";

        var div = new Element("div",{
            "styles" : this.css.sectionFormContent
        }).inject(this.formTableArea);

        MWF.getJSON(jsonPath, function(json){
            json.icons.each(function(iconName, i){
                var iconAreaNode = new Element("div",{
                    "styles" : this.css.iconAreaNode
                }).inject(div);
                var iconNode = new Element("img",{
                    "styles" : this.css.iconSelectNode,
                    "src" : iconPath + iconName
                }).inject(iconAreaNode);

                iconAreaNode.store( "iconName", iconName );
                iconAreaNode.store( "iconPath", iconPath + iconName );
                iconAreaNode.addEvents({
                    "mouseover" : function(){
                        this.node.setStyles( this.obj.css.iconAreaNodeOver )
                    }.bind({ obj : this, node : iconAreaNode }),
                    "mouseout" : function(){
                        this.node.setStyles( this.obj.css.iconAreaNode )
                    }.bind({ obj : this, node : iconAreaNode }),
                    "click" : function(){
                        var icon = {
                            "path": this.node.retrieve("iconPath"),
                            "name": this.node.retrieve("iconName")
                        };
                        this.obj.fireEvent("postOk", icon );
                        this.obj.close();
                    }.bind({ obj : this, node : iconAreaNode })
                })
            }.bind(this));
        }.bind(this));
    }
});

MWF.xApplication.Forum.Setting.SystemSettingExplorer = new Class({
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
        this.loadView();
    },
    destroy : function(){
        if(this.resizeWindowFun)this.app.removeEvent("resize",this.resizeWindowFun);
        this.view.destroy();
    },
    loadView : function(){
        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.container);

        this.resizeWindow();
        this.resizeWindowFun = this.resizeWindow.bind(this);
        this.app.addEvent("resize", this.resizeWindowFun );

        this.view = new MWF.xApplication.Forum.Setting.SystemSettingView( this.viewContainer, this.app, this, {
            templateUrl : this.parent.path+"listItemSystem.json",
            scrollEnable : true
        } );
        this.view.load();
    },
    resizeWindow: function(){
        var size = this.container.getSize();
        this.viewContainer.setStyles({"height":(size.y)+"px"});
    }
});

MWF.xApplication.Forum.Setting.SystemSettingView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Forum.Setting.SystemSettingDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        //var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        //var filter = this.filterData || {};
        this.actions.listSystemSettingAll(function (json) {
            if (callback)callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){

    },
    _openDocument: function( documentData ){
        var form = new MWF.xApplication.Forum.Setting.SystemSettingForm(this, documentData, {
            onPostOk : function(){
                this.reload();
            }.bind(this)
        });
        if( MWF.AC.isBBSManager()  ){
            form.edit();
        }else{
            form.open();
        }
    }

});

MWF.xApplication.Forum.Setting.SystemSettingDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    }
});

MWF.xApplication.Forum.Setting.SystemSettingForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "600",
        "height": "320",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Forum.LP.systemSettingFormTitle,
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
        configValueSetting.tType = "text";

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
        this.app.restActions.saveSystemSetting( data, function(json){
            if( callback )callback(json);
            this.fireEvent("postOk")
        }.bind(this));
    }
});



MWF.xApplication.Forum.Setting.SelectOrgForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "900",
        "height": "230",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Forum.LP.SelectOrgForm,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='person' width='15%'></td>" +
            "    <td styles='formTableValue' item='person' colspan='3' width='85%'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='unit'></td>" +
            "    <td styles='formTableValue' item='unit' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "forum",
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    person: {type : "org",text: this.lp.selectPerson, orgType: "person", count: 0},
                    unit: {type : "org",text: this.lp.selectDepartment, orgType: "unit", count: 0}
                }
            }, this.app);
            this.form.load();
        }.bind(this), false);
    },
    _ok: function (data, callback) {
        var orgArray = [];
        data.person.split(",").each( function( p ){
            orgArray.push( p+"#人员" )
        });
        data.department.split(",").each( function( p ){
            orgArray.push( p+"#组织" )
        });
        data.company.split(",").each( function( p ){
            orgArray.push( p+"#组织" )
        });
        this.app.restActions.saveRole( data, function(json){
            this.app.restActions.getRole( data.id, function( j ){
                var roleData = {
                    bindObjectArray : orgArray,
                    bindRoleCode : j.roleCode
                };
                this.app.restActions.bindRole( roleData, function( rData ){
                    if( callback )callback(json);
                    this.fireEvent("postOk")
                }.bind(this))
            })

        }.bind(this));
    }
});