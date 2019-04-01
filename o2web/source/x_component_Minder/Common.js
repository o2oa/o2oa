MWF.xApplication.Minder = MWF.xApplication.Minder || {};
//MWF.xDesktop.requireApp("Minder", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.require("MWF.widget.O2Identity", null, false);

MWF.xApplication.Minder.FolderSelector = new Class({
    Extends: MTooltips,
    options : {
        style : "", //如果有style，就加载 style/css.wcss
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "right", //x轴上left center right,  auto 系统自动计算
            y : "auto" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : false,
        overflow : "scroll", //弹出框高宽超过container的时候怎么处理，hidden 表示超过的隐藏，scroll 表示超过的时候显示滚动条
        nodeStyles : {
            "font-size" : "12px",
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "260px",
            "z-index" : "11",
            "background-color" : "#fff",
            "padding" : "5px",
            "border-radius" : "4px",
            "box-shadow": "0 0 4px 0 #999999",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        }
    },
    _loadCustom : function( callback ){
        var _self = this;
        this.treeNode = new Element("div.treeNode",{
            "styles" : {
                "position": "relative",
                "overflow": "hidden",
                "min-width" : "395px",
                "box-shadow": "0 1px 3px 0 rgba(0,0,0,0.25)",
                "float" : "left",
                "background-color" : "#F8F8F8"
            }
        }).inject(this.contentNode);
        this.tree = new MWF.xApplication.Minder.Tree( { app : this.app }, this.treeNode, {
            style : "default",
            defaultNode : this.options.defaultNode || "root",
            onPostLoad : function(){
                if(callback)callback();
            }.bind(this),
            onSelect : function( treeNode ){
                this.fireEvent( "select", treeNode.data );
                this.hide();
            }.bind(this),
            "minWidth" : 425,
            "maxWidth" : 425
        } );
    }
});

MWF.xApplication.Minder.Tree = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options : {
        "style" :"default",
        "defaultNode" : "root",
        "minWidth" : null,
        "maxWidth" : null
    },
    initialize: function(explorer, node, options){
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.node = $(node);
        this.cssPath = "/x_component_Minder/$Common/"+this.options.style+"/css.wcss";
        this.load();
    },
    load: function(){
        this._loadCss();
        var _self = this;
        this.treeContentNode = new Element("div",{
            "styles" : this.css.treeContentNode
        }).inject(this.node);

        var rootData = {
            id : "root",
            name : "根目录",
            orderNumber : "1",
            description : ""
        };
        this.app.restActions.listMyFolder( function( json ){
            rootData.children = json.data;
            this.loadTreeNode( rootData );

            this.fireEvent("postLoad");
        }.bind(this),function(){
            this.fireEvent("postLoad");
        }.bind(this), true)

    },
    loadTreeNode : function( rootData ){
        this.treeNode = new MWF.xApplication.Minder.Tree.Node( this, this.treeContentNode, rootData, {
            "style" : this.options.style,
            "isCurrent" : this.options.defaultNode == rootData.id,
            "minWidth" : this.options.minWidth,
            "maxWidth" : this.options.maxWidth
        });
    },
    reload : function(){
        this.node.empty();
        this.load();
    },
    getCurrentNode : function(){
        return this.currentItem;
    },
    getCurrentFolderData : function(){
        var item = this.getCurrentNode();
        if( item ){
            return item.data;
        }else{
            return {};
        }
    },
    getCurrentFolderId : function(){
        var item = this.getCurrentNode();
        if( item ){
            return item.data.id;
        }else{
            return "";
        }
    }
});

MWF.xApplication.Minder.Tree.Node = new Class({
    Implements: [Options, Events],
    options : {
        "style" :"default",
        "isCurrent" : false,
        "isExpanded" : true,
        "level" : 0,
        "minWidth" : null,
        "maxWidth" : null
    },
    initialize: function( tree, node, data, options){
        this.setOptions(options);
        this.tree = tree;
        this.explorer = tree.explorer;
        this.app = tree.app;
        this.css = this.tree.css;
        this.data = data;
        this.node = $(node);
        this.load();
    },
    load : function() {
        var _self = this;
        this.itemNode = new Element("div.treeItemNode", {
            "styles": this.css.treeItemNode
        }).inject(this.node);
        this.itemNode.setStyle("padding-left", (this.options.level * 12 + 10 ) +"px");

        this.itemExpendNode = new Element("div.treeItemExpendNode", {
            "styles": this.css.emptyExpendNode
        }).inject(this.itemNode);
        if( this.data.children && this.data.children.length ){
            this.itemExpendNode.addEvent("click", function( ev ){
                if( _self.options.isExpanded ){
                    _self.collapse();
                }else{
                    _self.expand();
                }
                ev.stopPropagation();
            })
        }

        this.itemIconNode = new Element("div.treeItemIconNode", {
            "styles": this.css.treeItemIconNode
        }).inject(this.itemNode);

        this.itemTextNode = new Element("div.treeItemTextNode", {
            "styles": this.css.treeItemTextNode,
            "text": this.data.name
        }).inject(this.itemNode);
        this.setTextNodeWidth();

        this.itemNode.addEvents({
            "mouseover": function () {
                if ( !_self.options.isCurrent ){
                    this.setStyles(_self.css.treeItemNode_over);
                    _self.itemIconNode.setStyles( _self.css.treeItemIconNode_over );
                }
            },
            "mouseout": function () {
                if ( !_self.options.isCurrent ){
                    this.setStyles(_self.css.treeItemNode);
                    _self.itemIconNode.setStyles( _self.css.treeItemIconNode );
                }
            },
            click: function () {
                _self.tree.fireEvent("select", _self );
                _self.setCurrent();
            }
        });

        if (this.data.children) {
            this.treeContentNode = new Element("div.treeContentNode", {
                "styles": this.css.treeContentNode
            }).inject(this.node);
            this.children = [];
            this.data.children.each( function( d ){
                _self.children.push( _self.getTreeNode( d ) )
            })
        }

        if( this.options.isCurrent ){
            this.setCurrent();
        }
        if( this.data.children && this.data.children.length ){
            if( this.options.isExpanded ){
                this.expand();
            }else{
                this.collapse();
            }
        }
    },
    setTextNodeWidth : function(){
        if( this.options.minWidth ){
            this.itemTextNode.setStyle(  "min-width" , ( this.options.minWidth - this.options.level * 12 - 10 - 80  ) +"px");
        }
        if( this.options.maxWidth ){
            this.itemTextNode.setStyle(  "max-width" , ( this.options.maxWidth - this.options.level * 12 - 10 - 80  ) +"px");
        }
    },
    getTreeNode: function( data ){
        return new MWF.xApplication.Minder.Tree.Node(this.tree, this.treeContentNode, data, {
            style :this.options.style,
            level : this.options.level + 1,
            isCurrent : this.tree.options.defaultNode == data.id,
            "minWidth" : this.options.minWidth,
            "maxWidth" : this.options.maxWidth
        })
    },
    cancelCurrent : function(){
        this.itemNode.setStyles( this.css.treeItemNode );
        this.itemIconNode.setStyles( this.css.treeItemIconNode );
        if( this.data.children && this.data.children.length > 0 ){
            if( this.options.isExpanded ){
                this.itemExpendNode.setStyles( this.css.treeItemExpendNode );
            }else{
                this.itemExpendNode.setStyles( this.css.treeItemCollapseNode );
            }
        }
        this._cancelCurrent();
        this.options.isCurrent = false;
        this.tree.currentItem = null;
    },
    _cancelCurrent : function(){
        //if( this.toolbar )this.toolbar.setStyle("display","none");
    },
    setCurrent : function(){
        if(this.tree.currentItem ){
            this.tree.currentItem.cancelCurrent();
        }
        this.itemNode.setStyles( this.css.treeItemNode_selected );
        this.itemIconNode.setStyles( this.css.treeItemIconNode_selected );
        if( this.data.children && this.data.children.length > 0 ){
            if( this.options.isExpanded ){
                this.itemExpendNode.setStyles( this.css.treeItemExpendNode_selected );
            }else{
                this.itemExpendNode.setStyles( this.css.treeItemCollapseNode_selected );
            }
        }
        this.options.isCurrent = true;
        this.tree.currentItem = this;
        this._setCurrent()
    },
    _setCurrent: function(){
        //this.explorer.loadList({
        //    folderId : this.data.id
        //});
        //if( !this.toolbar ){
        //    this.createToolbar()
        //}else{
        //    this.toolbar.setStyle("display","");
        //}
    },
    expand: function(){
        if( this.options.isCurrent ){
            this.itemExpendNode.setStyles( this.css.treeItemExpendNode_selected );
        }else{
            this.itemExpendNode.setStyles( this.css.treeItemExpendNode );
        }
        if( this.treeContentNode )this.treeContentNode.setStyle("display","");
        this.options.isExpanded = true;
    },
    collapse: function(){
        if( this.options.isCurrent ){
            this.itemExpendNode.setStyles( this.css.treeItemCollapseNode_selected );
        }else{
            this.itemExpendNode.setStyles( this.css.treeItemCollapseNode );
        }
        if( this.treeContentNode )this.treeContentNode.setStyle("display","none");
        this.options.isExpanded = false;
    }
});

MWF.xApplication.Minder.ShareForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 700,
        //"height": 300,
        "height": "400",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "脑图分享"
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='fileName' width='18%'></td>" +
            "    <td styles='formTableValue14' item='fileName'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='shareTo' width='18%'></td>" +
            "    <td styles='formTableValue14' item='shareTo'></td></tr>" +
            "<tr><td styles='formTableTitle' width='18%'></td>" +
            "    <td styles='formTableValue14'>邀请其他人查看此文档</td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.form = new MForm(this.formTableArea, this.data || {}, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                fileName : { text : "脑图名称", type : "innerHTML", value : function(){
                    var name = [];
                    this.checkedItemData.each( function(d){
                        name.push(d.name );
                    });
                    return name.join("<br>");
                }.bind(this)},
                shareTo: { type : "org", orgType:["person","unit","group"],text : "分享对象", notEmpty : true, count : 0, style : {
                    "min-height" : "100px"
                } }
            }
        }, this.app);
        this.form.load();

    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.share(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    share: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            var json = {
                sharePersons : [],
                shareUnits : [],
                shareGroups : []
            };
            data.shareTo.each( function( s ){
                var flag = s.substr(s.length-1, 1);
                switch (flag.toLowerCase()){
                    case "p":
                        json.sharePersons.push( s );
                        break;
                    case "u":
                        json.shareUnits.push( s );
                        break;
                    case "g":
                        json.shareGroups.push( s );
                        break;
                    default :
                        break;
                }
            }.bind(this));
            var count = 0;
            this.checkedItemData.each( function(d){
                this.app.restActions.shareMind( d.id, json, function(){
                    count++;
                    if( count ==  this.checkedItemData.length){
                        this.app.notice( "分享成功！" );
                        this.close();
                    }
                }.bind(this));
            }.bind(this));
        }
    }
});

MWF.xApplication.Minder.NewNameForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 700,
        //"height": 300,
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "新建脑图"
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='folder' width='25%'></td>" +
            "    <td styles='formTableValue14' item='folder' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='name' width='25%'></td>" +
            "    <td styles='formTableValue14' item='name' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        var currentFolderData = this.explorer.getCurrentFolderData ? this.explorer.getCurrentFolderData() : {};
        this.folderId = currentFolderData.id || "root";
        this.form = new MForm(this.formTableArea, this.data || {}, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                folder: { text : "选择文件夹",  notEmpty : true, attr : { readonly : true }, defaultValue : currentFolderData.name || "根目录" },
                name: { text : "脑图名称", notEmpty : true }
            }
        }, this.app);
        this.form.load();
        this.loadFolderSelect();
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            data.content = "{\"root\":{\"data\":{\"text\":\""+ data.name +"\"},\"children\":[]}}";
            data.folderId = this.folderId; //this.explorer.getCurrentFolderId();
            this.app.restActions.saveMind( data, function( json ){
                this.app.desktop.openApplication(null, "MinderEditor", {
                    "folderId" : data.folderId,
                    "minderName" : data.name,
                    "id" : json.data.id,
                    "isEdited" : true,
                    "isNew" : false
                });
                if(this.explorer.currentView)this.explorer.currentView.reload();
                this.close();
            }.bind(this));
        }
    },
    loadFolderSelect: function() {
        this.folderSelect =  new MWF.xApplication.Minder.FolderSelector( this.app.content, this.form.getItem("folder").getElements()[0], this.app, {}, {
            defaultNode : this.folderId,
            onSelect : function( folderData ){
                this.form.getItem("folder").setValue( folderData.name );
                this.folderId = folderData.id;
            }.bind(this)
        } );

    }
});

MWF.xApplication.Minder.FolderForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 700,
        //"height": 300,
        "height": "200",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "新建目录"
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='name' width='25%'></td>" +
            "    <td styles='formTableValue14' item='name' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.form = new MForm(this.formTableArea, this.data || {}, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                name: { text : "名称", notEmpty : true }
            }
        }, this.app);
        this.form.load();

    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            if( this.isNew )data.parentId = this.explorer.getCurrentFolderId();
            this.app.restActions.saveFolder( data, function( json ){
                this.explorer.tree.reload();
                this.close();
            }.bind(this));
        }
    }
});

MWF.xApplication.Minder.ReNameForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 700,
        //"height": 300,
        "height": "200",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "重命名脑图",
        "id" : ""
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='name' width='25%'></td>" +
            "    <td styles='formTableValue14' item='name' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.form = new MForm(this.formTableArea, this.data || {}, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                name: { text : "名称", notEmpty : true }
            }
        }, this.app);
        this.form.load();

    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            this.app.restActions.getMind( this.options.id, function( json ){
                var d = json.data;
                d.name = data.name;
                this.app.restActions.saveMind( d, function( json ){
                    this.app.notice("重命名成功");
                    this.explorer.currentView.reload();
                    this.close();
                }.bind(this));
            }.bind(this))
        }
    }
});

MWF.xApplication.Minder.Toolbar = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "availableTool" : [
            ["createMinder", "createFolder"],
            ["rename", "recycle"],
            //["import", "export"],
            ["share"]
        ],
        "viewType" : "list"
    },
    initialize : function( container, explorer, options ) {
        this.container = container;
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = explorer.app.lp;
        //this.css = explorer.app.css;

        this.iconPath = "/x_component_Minder/$Common/"+this.options.style+"/icon_tool/";
        this.cssPath = "/x_component_Minder/$Common/"+this.options.style+"/css.wcss";

        this.setOptions(options);
        this.tools = {
            createMinder : {
                action : "createMinder",
                text : "新建脑图",
                icon : "createminder"
            },
            createFolder : {
                action : "createFolder",
                text : "新建目录",
                icon : "createfolder"
            },
            rename : {
                action : "rename",
                text : "重命名",
                icon : "rename"
            },
            //import : {
            //    action : "import",
            //    text : "导入",
            //    icon : "import"
            //},
            //export : {
            //    action : "export",
            //    text : "导出",
            //    icon : "export"
            //},
            recycle : {
                action : "recycle",
                text : "删除",
                icon : "recycle"
            },
            destroyFromRecycle : {
                action : "destroyFromRecycle",
                text : "彻底删除",
                icon : "delete"
            },
            delete : {
                action : "delete",
                text : "彻底删除",
                icon : "delete"
            },
            share : {
                action : "share",
                text : "分享",
                icon : "share"
            },
            restore : {
                action : "restore",
                text : "恢复",
                icon : "restore"
            }
        }
    },
    load : function(){
        this._loadCss();
        this.node = new Element("div",{ styles : this.css.toolbarNode }).inject( this.container );

        this.options.availableTool.each( function( group ){
            var toolgroupNode = new Element("div",{
                styles : this.css.toolgroupNode
            }).inject( this.node );
            var length = group.length;
            group.each( function( t, i ){
                var className;
                if( length == 1 ){
                    className = "toolItemNode_single";
                }else{
                    if( i == 0 ){
                        className = "toolItemNode_left";
                    }else if( i + 1 == length ){
                        className = "toolItemNode_right";
                    }else{
                        className = "toolItemNode_center";
                    }
                }

                var tool = this.tools[ t ];
                var toolNode = new Element( "div", {
                    styles : this.css[className],
                    text : tool.text,
                    events : {
                        click : function( ev ){ this[tool.action]( ev ) }.bind(this),
                        mouseover : function( ev ){
                            ev.target.setStyles( this.css.toolItemNode_over );
                            ev.target.setStyle("background-image","url("+this.iconPath+ tool.icon +"_active.png)")
                        }.bind(this),
                        mouseout : function( ev ){
                            ev.target.setStyles( this.css.toolItemNode_normal );
                            ev.target.setStyle("background-image","url("+this.iconPath+ tool.icon +".png)")
                        }.bind(this)
                    }
                }).inject( toolgroupNode );
                toolNode.setStyle("background-image", "url("+this.iconPath+ tool.icon +".png)")

            }.bind(this))
        }.bind(this));

        this.loadRightNode()
    },
    createMinder : function(){
        var form = new MWF.xApplication.Minder.NewNameForm(this.explorer, {
        }, {}, {
            app: this.app
        });
        form.edit()
    },
    createFolder : function(){
        var form = new MWF.xApplication.Minder.FolderForm(this.explorer, {
        }, {}, {
            app: this.app
        });
        form.create()
    },
    rename : function(){
        var data = this.explorer.currentView.getCheckedItemData();
        if( data.length == 0 ){
            this.app.notice("请先选择文件","error");
            return;
        }
        var form = new MWF.xApplication.Minder.ReNameForm(this.explorer, {
            name : data[0].name
        }, {
            id : data[0].id
        }, {
            app: this.app
        });
        form.edit()
    },
    recycle : function( e ){
        var _self = this;
        var ids = this.explorer.currentView.getCheckedItemIds();
        if( ids.length == 0 ){
            this.app.notice("请先选择文件","error");
            return;
        }

        this.app.confirm("warn", e, "删除文件确认", "是否删除选中的"+ids.length+"个文件？删除的文件会放到回收站。", 350, 120, function () {
            var count = 0;
            ids.each( function(id){
                _self.app.restActions.recycleMind( id , function(){
                    count++;
                    if( ids.length == count ){
                        _self.app.notice("成功删除"+count+"个文件，您可以从回收站找到文件。");
                        _self.explorer.currentView.reload();
                    }
                });
            }.bind(this));
            this.close();
        }, function () {
            this.close();
        });
    },
    delete : function( e ){
        var _self = this;
        var ids = this.explorer.currentView.getCheckedItemIds();
        if( ids.length == 0 ){
            this.app.notice("请先选择文件","error");
            return;
        }

        this.app.confirm("warn", e, "彻底删除文件确认", "删除的文件无法恢复！是否彻底删除选中的"+ids.length+"个文件？", 350, 120, function () {
            var count = 0;
            ids.each( function(id){
                _self.app.restActions.deleteMind( id , function(){
                    count++;
                    if( ids.length == count ){
                        _self.app.notice("成功删除"+count+"个文件");
                        _self.explorer.currentView.reload();
                    }
                });
            }.bind(this));
            this.close();
        }, function () {
            this.close();
        });
    },
    destroyFromRecycle : function( e ){
        var _self = this;
        var ids = this.explorer.currentView.getCheckedItemIds();
        if( ids.length == 0 ){
            this.app.notice("请先选择文件","error");
            return;
        }

        this.app.confirm("warn", e, "彻底删除文件确认", "删除的文件无法恢复！是否彻底删除选中的"+ids.length+"个文件？", 350, 120, function () {
            var count = 0;
            ids.each( function(id){
                _self.app.restActions.destroyFromRecycle( id , function(){
                    count++;
                    if( ids.length == count ){
                        _self.app.notice("成功删除"+count+"个文件");
                        _self.explorer.currentView.reload();
                    }
                });
            }.bind(this));
            this.close();
        }, function () {
            this.close();
        });
    },
    restore : function(){
        var _self = this;
        var ids = this.explorer.currentView.getCheckedItemIds();
        if( ids.length == 0 ){
            this.app.notice("请先选择文件","error");
            return;
        }
        var count = 0;
        ids.each( function(id){
            _self.app.restActions.restoreMind( id , function(){
                count++;
                if( ids.length == count ){
                    _self.app.notice("成功恢复"+count+"个文件");
                    _self.explorer.currentView.reload();
                }
            });
        }.bind(this));
    },
    loadRightNode : function(){
        this.toolabrRightNode = new Element("div",{
            "styles": this.css.toolabrRightNode
        }).inject(this.node);

        this.loadSearch();
        this.loadListType();
    },
    loadSearch : function(){
        this.searchBarAreaNode = new Element("div", {
            "styles": this.css.searchBarAreaNode
        }).inject(this.toolabrRightNode);

        this.searchBarNode = new Element("div", {
            "styles": this.css.searchBarNode
        }).inject(this.searchBarAreaNode);

        this.searchBarInputBoxNode = new Element("div", {
            "styles": this.css.searchBarInputBoxNode
        }).inject(this.searchBarNode);
        this.searchBarInputNode = new Element("input", {
            "type": "text",
            "placeHolder": this.lp.searchKey,
            "styles": this.css.searchBarInputNode
        }).inject(this.searchBarInputBoxNode);

        this.searchBarResetActionNode = new Element("div", {
            "styles": this.css.searchBarResetActionNode
        }).inject(this.searchBarInputBoxNode);
        this.searchBarResetActionNode.setStyle("display","none");

        this.searchBarActionNode = new Element("div", {
            "styles": this.css.searchBarActionNode
        }).inject(this.searchBarNode);

        var _self = this;
        this.searchBarActionNode.addEvent("click", function(){
            this.search();
        }.bind(this));
        this.searchBarResetActionNode.addEvent("click", function(){
            this.reset();
        }.bind(this));

        this.searchBarInputNode.addEvents({
            "keydown": function(e){
                if (e.code==13){
                    this.search();
                    e.preventDefault();
                }
            }.bind(this)
        });
    },
    getListType : function(){
        return this.viewType || this.options.viewType
    },
    loadListType : function(){
        this.listViewTypeNode = new Element("div", {
            "styles": this.css[ this.options.viewType == "list" ?  "listViewTypeNode_active" : "listViewTypeNode"],
            events : {
                click : function(){
                    this.viewType = "list";
                    this.listViewTypeNode.setStyles( this.css.listViewTypeNode_active );
                    this.tileViewTypeNode.setStyles( this.css.tileViewTypeNode );
                    this.explorer.loadList( this.explorer.currentView.filterData );
                }.bind(this)
            }
        }).inject(this.toolabrRightNode);

        this.tileViewTypeNode = new Element("div", {
            "styles": this.css[ this.options.viewType != "list" ?  "tileViewTypeNode_active" : "tileViewTypeNode"],
            events : {
                click : function(){
                    this.viewType = "tile";
                    this.listViewTypeNode.setStyles( this.css.listViewTypeNode );
                    this.tileViewTypeNode.setStyles( this.css.tileViewTypeNode_active );
                    this.explorer.loadList( this.explorer.currentView.filterData );
                }.bind(this)
            }
        }).inject(this.toolabrRightNode);
    },
    search : function(){
        var value = this.searchBarInputNode.get("value");
        var filterData = Object.clone(this.explorer.currentView.filterData);
        if(value){
            filterData.name = value;
        }else if( filterData.name ){
            delete filterData.name;
        }
        this.explorer.loadList( filterData );
    },
    share : function(){
        var data = this.explorer.currentView.getCheckedItemData();
        if( data.length == 0 ){
            this.app.notice("请先选择文件","error");
            return;
        }
        var form = new MWF.xApplication.Minder.ShareForm(this.explorer, {}, {
        }, {
            app: this.app
        });
        form.checkedItemData = data;
        form.edit()
    }
});

MWF.xApplication.Minder.List = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    options : {
        "scrollEnable" : true,
        "scrollType" : "window"
    },
    _createDocument: function(data, index){
        return new MWF.xApplication.Minder.Document(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || {};
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }

        //{"name":"","folderId":"root","description":"","creator":"","creatorUnit":"","shared":"","orderField":"","orderType":""}//
        this.actions.listNextMindWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSubject(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){},
    _openDocument: function( documentData,index ){
        //var appId = "ReportDocument"+documentData.id;
        //if (this.app.desktop.apps[appId]){
        //    this.app.desktop.apps[appId].setCurrent();
        //}else {
        //    this.app.desktop.openApplication(null, "ReportDocument", {
        //        "id" : documentData.id,
        //        "isEdited" : false,
        //        "isNew" : false
        //    });
        //}
        var appId = "MinderEditor"+documentData.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "MinderEditor", {
                "appId" : appId,
                "folderId" : documentData.folderId,
                "id" : documentData.id,
                "isEdited" : true,
                "isNew" : false
            });
        }
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){
    },
    _queryCreateViewHead:function(){},
    _postCreateViewHead: function( headNode ){
        var selectAll = headNode.getElement("[item='selectAll']");
        if( selectAll ){
            selectAll.addEvent("click", function(){
                if( this.selectedAll ){
                    this.selectAllCheckbox_custom( false );
                    this.selectedAll = false;
                    selectAll.setStyles( this.css.tileSelectAllNode );
                }else{
                    this.selectAllCheckbox_custom( true );
                    this.selectedAll = true;
                    selectAll.setStyles( this.css.tileSelectAllNode_selected );
                }
            }.bind(this))
        }
    },
    getCheckedItemData : function(){
        if( this.viewType == "tile" ){
            var items = this.getCheckedItems_custom();
        }else{
            var items = this.getCheckedItems();
        }
        var array = [];
        items.each( function( item ){
            array.push( item.data )
        });
        return array;
    },
    getCheckedItemIds : function(){
        if( this.viewType == "tile" ){
            var items = this.getCheckedItems_custom();
        }else{
            var items = this.getCheckedItems();
        }
        var ids = [];
        items.each( function( item ){
            ids.push( item.data.id )
        });
        return ids;
    },
    selectAllCheckbox_custom: function ( flag ) {
        this.items.each(function (it) {
            it.setSelect( flag )
        }.bind(this))
    },
    getCheckedItems_custom : function(){
        var checkedItems = [];
        this.items.each(function (it) {
            if (it.selected) {
                checkedItems.push( it )
            }
        }.bind(this));
        return checkedItems;
    }
});

MWF.xApplication.Minder.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument : function(){
        this.node.setStyles( this.css.tileNode_over );
        if( !this.selected ) {
            var select = this.node.getElement("[item=select]");
            select.setStyle("display", "");
        }
    },
    mouseoutDocument : function(){
        this.node.setStyles( this.css.tileNode );
        if( !this.selected ){
            var select = this.node.getElement("[item=select]");
            select.setStyle("display","none");
        }
    },
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        var select = itemNode.getElement("[item=select]");
        if( select ){
            select.addEvent("click", function( ev ){
                this.setSelect( !this.selected );
                ev.stopPropagation()
            }.bind(this))
        }
        //this.app.restActions.getMindIcon( itemData.id, function( json ){
        //    var thumbnailNode = itemNode.getElement("[item=thumbnail]");
        //    thumbnailNode.set("src", "data:image/png;base64,"+ json.data.value );
        //}, function(){
        //    var thumbnailNode = itemNode.getElement("[item=thumbnail]");
        //    thumbnailNode.set("src", this.app.path +  this.view.options.style + "/icon/default_thumbnail.png" );
        //}.bind(this))
        if( itemData.icon ){
            var thumbnailNode = itemNode.getElement("[item=thumbnail]");
            thumbnailNode.set("src", MWF.xDesktop.getImageSrc(itemData.icon) );
        }else{
            var thumbnailNode = itemNode.getElement("[item=thumbnail]");
            thumbnailNode.set("src", this.app.path +  this.view.options.style + "/icon/default_thumbnail.png" );
        }
    },
    setSelect : function( flag ){
        var select = this.node.getElement("[item=select]");
        if( !flag ){
            this.selected = false;
            select.setStyles( this.css.tileItemSelectNode )
        }else{
            this.selected = true;
            select.setStyles( this.css.tileItemSelectNode_selected )
        }
    },
    open: function (e) {
        this.view._openDocument(this.data, this.index);
    },
    edit : function( menuAction ){
        var appId = "MinderEditor"+this.data.id;
        var app = this.app.desktop.apps[appId];
        if (app){
            app.setCurrent();
            if( menuAction ){
                app.openMainMenu(menuAction)
            }
        }else {
            this.app.desktop.openApplication(null, "MinderEditor", {
                "appId" : appId,
                "folderId" : this.data.folderId,
                "id" : this.data.id,
                "isEdited" : true,
                "isNew" : false,
                "menuAction" : menuAction
            });
        }
    },
    remove : function(){

    },
    showFileVersion : function(ev){
        this.edit( "openFileVersion" );
        ev.stopPropagation()
    },
    showShareRecord : function(ev){
        this.edit( "openShare" );
        ev.stopPropagation()
    }
});

MWF.xApplication.Minder.ShareTooltip = new Class({
    Extends: MTooltips,
    options : {
        style : "", //如果有style，就加载 style/css.wcss
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x轴上left center right,  auto 系统自动计算
            y : "auto" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        overflow : "scroll" //弹出框高宽超过container的时候怎么处理，hidden 表示超过的隐藏，scroll 表示超过的时候显示滚动条
    },
    _getHtml : function(  ){
        var data = this.data;
        var titleStyle = "font-size:14px;color:#333";
        var valueStyle = "font-size:14px;color:#666;padding-right:20px";
        var html =
                "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
                //"   <div style='font-size: 12px;color:#666; float: right'>"+ this.lp.applyPerson  +":" + data.applicant.split("@")[0] +"</div>" +
                "   <div style='font-size: 16px;color:#333;float: left;'>分享细节</div>"+
                "</div>"+
            "<div style='font-size: 14px;color:#333;padding:0px 10px 15px 20px;'>"+ data.name +"</div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+"' width='70'>个人:</td>" +
            "    <td style='"+valueStyle+"'>" + this.getCn( data.sharePersonList ) + "</td></tr>" +
            "<tr><td style='"+titleStyle+"'>组织:</td>" +
            "    <td style='"+valueStyle+"'>" + this.getCn( data.shareUnitList ) + "</td></tr>" +
            "<tr><td style='"+titleStyle+"'>群组:</td>" +
            "    <td style='"+valueStyle+"'>" + this.getCn( data.shareGroupList ) + "</td></tr>" +
            "</table>";
        return "";
    }
});

MWF.xApplication.Minder.ShareRecordForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": "80%",
        //"height": 300,
        "height": "80%",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "分享记录"
    },
    _createTableContent: function () {

    },
    _createBottomContent: function () {

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    }
});

