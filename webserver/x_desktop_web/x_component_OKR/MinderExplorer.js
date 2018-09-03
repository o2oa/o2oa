MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("OKR", "Minder", null, false);
MWF.xApplication.OKR.MinderExplorer = new Class({
	Extends: MWF.xApplication.Template.Explorer,
	Implements: [Options, Events],
    options: {
        "style": "default",
        "hasFilter" : false,
        "isAdmin": false,
        "searchKey" : ""
    },
    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_OKR/$MinderExplorer/";
        //this.exlorerPath = "/x_component_OKR/$MinderExplorer/"+this.options.style+"/css.wcss";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
    },
    load: function(){
        this.loadToolbar();
        if( this.options.hasFilter )this.loadFilter();
        this.loadContentNode();
        this.loadView();
        this.setNodeScroll();
        this.app.addEvent("resize", function(){this.reloadView();}.bind(this));
    },
    reloadView : function(){
        if( this.viewContainerNode )this.viewContainerNode.empty();
        if( this.minder )delete this.minder;
        this.loadView();
    },
    loadView : function(){
        this.actions.getWorksById( "010909f1-7a72-49e3-a1be-20adb39cff43" , function(json){
            alert( JSON.stringify(json) )
        } )
        var data = {
            "root": {
                "data": {"id": "9f92035021ac", "created": 1463069003, "text": "软装修"},
                "children": [{
                    "data": {"id": "b45yogtullsg", "created": 1463069010918, "text": "包阳台"},
                    "children": [{
                        "data" : {"id":"3jl3i3j43", "created": 1463069010923, "text": "凤铝"}
                    },{
                        "data" : {"id":"3jl3i3j44", "created": 1463069010923, "text": "断桥"}
                    }]
                },
                    {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "衣柜"}, "children": [
                        {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "主卧"}},
                        {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "次卧"}}
                    ]},
                    {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "床"}, "children": []},
                    {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "餐桌"}, "children": []},
                    {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "灯具"}, "children": []},
                    {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "窗帘"}, "children": []}
                ]
            }//, "template": "default", "theme": "fresh-blue", "version": "1.4.33"
        };
        this.minder = new MWF.xApplication.OKR.Minder(this.viewContainerNode,this,data, {
            "template" : "default",
            "theme" : "fresh-blue",
            "onClickKMNode" : function(node, data){
                this.form = new MWF.xApplication.OKR.MinderExplorer.Form(this,data);
                this.form.edit();
            }.bind(this)
        });
        this.minder.load();
        this.setContentSize();
    },
    createDocument: function(){


    }
});

MWF.xApplication.OKR.MinderExplorer.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.OKR.MinderExplorer.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
            this.actions.listPersonSetting(function(json){
                if (callback) callback(json);
            });
    },
    _removeDocument: function(document, all){
        this.actions.deletePersonSetting(document.id, function(json){
            this.explorer.view.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){
        var form = new MWF.xApplication.OKR.MinderExplorer.Form(this.explorer);
        form.create();
    },
    _openDocument: function( documentData ){
        var form = new MWF.xApplication.OKR.MinderExplorer.Form(this.explorer, documentData );
        form.edit();
    }

})

MWF.xApplication.OKR.MinderExplorer.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.Document

})


MWF.xApplication.OKR.MinderExplorer.Form = new Class({
    Extends: MPopupForm,
    _createTableContent: function(){

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='2' styles='formTableHead'>考勤人员设置</td></tr>" +
            "<tr><td styles='formTabelTitle' lable='text'></td>"+
            "    <td styles='formTableValue' item='text'></td></tr>"
            "</table>"
        this.formTableArea.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, this.data, {
                style : "popup",
                isEdited : this.isEdited || this.isNew,
                itemTemplate : {
                    text : { text:"主题" }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function( data, callback ){
        this.app.restActions.savePersonSetting( data, function(json){
            if( callback )callback(json);
        }.bind(this));
    }
});

