MWF.xApplication.cms = MWF.xApplication.cms || {};
//MWF.xApplication.cms.Column = MWF.xApplication.cms.Column || {};
MWF.xDesktop.requireApp("cms.Column", "Actions.RestActions", null, false);
MWF.xApplication.cms.Column.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "cms.Column",
		"icon": "icon.png",
		"width": "1000",
		"height": "600",
		"isResize": false,
		"isMax": true,
		"title": MWF.xApplication.cms.Column.LP.title,
		"tooltip" : {
			"description" : MWF.xApplication.cms.Column.LP.description,
			"column" : {
				"title" : MWF.xApplication.cms.Column.LP.column.title,
				"create" : MWF.xApplication.cms.Column.LP.column.create,
				"nameLabel" : MWF.xApplication.cms.Column.LP.column.nameLabel,
				"aliasLabel": MWF.xApplication.cms.Column.LP.column.aliasLabel,
				"descriptionLabel": MWF.xApplication.cms.Column.LP.column.descriptionLabel,
				"sortLabel": MWF.xApplication.cms.Column.LP.column.sortLabel,
				"iconLabel": MWF.xApplication.cms.Column.LP.column.iconLabel,
				"cancel": MWF.xApplication.cms.Column.LP.column.cancel,
				"ok": MWF.xApplication.cms.Column.LP.column.ok,
				"inputName" : MWF.xApplication.cms.Column.LP.column.inputName,
				"create_cancel_title" : MWF.xApplication.cms.Column.LP.column.create_cancel_title,
				"create_cancel" : MWF.xApplication.cms.Column.LP.column.create_cancel,
                "noDescription" : MWF.xApplication.cms.Column.LP.column.noDescription,
				"delete" : MWF.xApplication.cms.Column.LP.column.delete,
				"edit" : MWF.xApplication.cms.Column.LP.column.edit
			},
			"category" : {
				"title": MWF.xApplication.cms.Column.LP.category.title,
				"create" : MWF.xApplication.cms.Column.LP.category.create,
				"nameLabel" : MWF.xApplication.cms.Column.LP.category.nameLabel,
				"aliasLabel": MWF.xApplication.cms.Column.LP.category.aliasLabel,
				"descriptionLabel": MWF.xApplication.cms.Column.LP.category.descriptionLabel,
				"sortLabel": MWF.xApplication.cms.Column.LP.category.sortLabel,
				"iconLabel": MWF.xApplication.cms.Column.LP.category.iconLabel,
				"columnLabel": MWF.xApplication.cms.Column.LP.category.columnLabel,
				"cancel": MWF.xApplication.cms.Column.LP.category.cancel,
				"ok": MWF.xApplication.cms.Column.LP.category.ok,
				"inputName" : MWF.xApplication.cms.Column.LP.category.inputName,
				"create_cancel_title" : MWF.xApplication.cms.Column.LP.category.create_cancel_title,
				"create_cancel" : MWF.xApplication.cms.Column.LP.category.create_cancel,
                "noDescription" : MWF.xApplication.cms.Column.LP.category.noDescription,
				"edit" : MWF.xApplication.cms.Column.LP.category.edit
			}
		}
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.cms.Column.LP;
        this.defaultColumnIcon = "/x_component_cms_Column/$Main/"+this.options.style+"/icon/column.png";
        this.defaultCategoryIcon = "/x_component_cms_Column/$Main/"+this.options.style+"/icon/category2.png";
	},
	loadApplication: function(callback){
		if (!this.restActions) this.restActions = new MWF.xApplication.cms.Column.Actions.RestActions();
		this.columns = [];
		this.categorys = [];
		this.deleteElements = [];
		this.createNode();
		this.loadApplicationContent();
		if (callback) callback();
	},
	loadApplicationContent: function(){
		this.loadToolbar();
		this.loadColumnArea();
		this.loadCategoryArea();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
	loadToolbar: function(){
		this.toolbarAreaNode = new Element("div", {
			"styles": this.css.toolbarAreaNode,
			"text" : this.options.tooltip.description
		}).inject(this.node);
		//this.createCreateAction();
		//this.createSearchAction();
	},
	createCreateAction: function(){
		//if (MWF.AC.isProcessPlatformCreator()){
			this.createCategoryNode = new Element("div", {
				"styles": this.css.createCategoryNode,
				"title": this.options.tooltip.category.create
			}).inject(this.toolbarAreaNode);
			this.createCategoryNode.addEvent("click", function(){
				this.createCategory();
			}.bind(this));
		//}
	},
	loadColumnArea: function(){
		this.columnAreaNode = new Element("div",{
			"styles" : this.css.columnAreaNode
		}).inject(this.node);

		this.columnToolbarAreaNode = new Element("div",{
			"styles" : this.css.columnToolbarAreaNode
		}).inject(this.columnAreaNode);

        if (MWF.AC.isProcessPlatformCreator()) {
            if ( MWF.AC.isAdministrator()) {
                this.createColumnNode = new Element("button", {
                    "styles": this.css.createColumnNode,
                    "text": this.options.tooltip.column.create
                }).inject(this.columnToolbarAreaNode);
                this.createColumnNode.addEvent("click", function () {
                    this.createColumn();
                }.bind(this));
            }
        }

		this.columnToolbarTextNode = new Element("div",{
			"styles" : this.css.columnToolbarTextNode,
			"text" : this.options.tooltip.column.title
		}).inject(this.columnToolbarAreaNode);


		this.loadColumnContentArea();
	},
	loadColumnContentArea: function(){

		this.columnContentAreaNode = new Element("div",{
			"styles" : this.css.columnContentAreaNode
		}).inject(this.columnAreaNode);

		this.createColumnNodes();
	},
	createColumnNodes: function(){
		this.restActions.listColumn(function(json){
			var emptyColumn = null;
			json.data.each(function(column){
				var column = new MWF.xApplication.cms.Column.Column(this, column);
				column.load();
				this.columns.push(column);
			}.bind(this));
			//   if (emptyColumn) this.createColumnItemNode()
		}.bind(this));
	},
    createColumn: function(text, alias, memo, icon, creator){
        var column = new MWF.xApplication.cms.Column.Column(this);
        column.createColumn( this.node );
	},
    /*
	createLoadding: function(){
		this.loaddingNode = new Element("div", {
			"styles": this.css.noApplicationNode,
			"text": this.options.tooltip.loadding
		}).inject(this.applicationContentNode);
	},
	removeLoadding: function(){
		if (this.loaddingNode) this.loaddingNode.destroy();
	},
	*/
	loadCategoryArea: function(){
		this.categoryAreaNode = new Element("div",{
			"styles" : this.css.categoryAreaNode
		}).inject(this.node);

		this.categoryToolbarAreaNode = new Element("div",{
			"styles" : this.css.categoryToolbarAreaNode
		}).inject(this.categoryAreaNode);

		this.createCategoryNode = new Element("button",{
			"styles" : this.css.createCategoryNode,
			"text" : this.options.tooltip.category.create
		}).inject(this.categoryToolbarAreaNode);
		this.createCategoryNode.addEvent("click", function(){
			this.createCategory();
		}.bind(this));

		this.categoryToolbarTextNode = new Element("div",{
			"styles" : this.css.categoryToolbarTextNode,
			"text" : this.options.tooltip.category.title
		}).inject(this.categoryToolbarAreaNode);

		this.categoryContentAreaNode = new Element("div",{
			"styles" : this.css.categoryContentAreaNode
		}).inject(this.categoryAreaNode);
	},
    createCategory: function(){
        var category = new MWF.xApplication.cms.Column.Category( this );
        category.createCategory();
    }
});

MWF.xApplication.cms.Column.Category = new Class({
    Implements: [Options, Events],
    options: {
        "bgColor": ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"]
    },

    initialize: function (app, data, options) {
        this.setOptions(options);
        this.app = app;
        //this.container = this.app.categoryContentAreaNode;
        this.data = data;
        this.isNew = false;
    },
    load: function () {

        var name = this.data.cms_catagory_name;
        var alias = this.data.cms_catagory_alias;
        var memo = this.data.cms_catagory_description;
        var order = this.data.cms_catagory_order;
        var creator =this.data.creator_uid;
        var column = this.data.cms_catagory_app_id;
        var form = this.data.cms_catagory_form_id;
        var icon = this.data.cms_app_icon;
        if( !icon || icon == "")icon = this.app.defaultCategoryIcon;

        var itemNode = this.itemNode = new Element("div.categoryItem", {
            "styles": this.app.css.categoryItemNode
        }).inject(this.container);
        itemNode.store("name", name);
        itemNode.setStyle("background-color", this.options.bgColor[(Math.random()*10).toInt()]);

        var iconNode = new Element("div",{
            "styles" : this.app.css.categoryItemIconNode
        }).inject(itemNode);
        iconNode.setStyles({
            "background-image" : "url("+icon+")"
        });

        var textNode = new Element("div",{
            "styles" : this.app.css.categoryItemTextNode
        }).inject(itemNode)

        var titleNode = new Element("div",{
            "styles" : this.app.css.categoryItemTitleNode,
            "text" : name,
            "title": (alias) ? name+" ("+alias+") " : name
        }).inject(textNode)

        var description = ( memo && memo!="") ? memo : this.app.options.tooltip.category.noDescription;
        var descriptionNode = new Element("div",{
            "styles" : this.app.css.categoryItemDescriptionNode,
            "text" : description,
            "title" : description
        }).inject(textNode)

        var _self = this;
        itemNode.addEvents({
            "mouseover": function(){if (!_self.selected) this.setStyles(_self.app.css.categoryItemNode_over);},
            "mouseout": function(){if (!_self.selected) this.setStyles(_self.app.css.categoryItemNode);},
            "click": function(){_self.clickNode(_self,this)}
        });

        if (MWF.AC.isProcessPlatformCreator()){
            if ((creator==layout.desktop.session.user.name) || MWF.AC.isAdministrator()){
                this.delAdctionNode = new Element("div.delNode", {
                    "styles": this.app.css.categoryItemDelActionNode,
                    "title" : this.app.options.tooltip.category.delete
                }).inject(itemNode );

                itemNode.addEvents({
                    "mouseover": function(){ this.delAdctionNode.fade("in"); }.bind(this),
                    "mouseout": function(){ this.delAdctionNode.fade("out"); }.bind(this)
                });
                this.delAdctionNode.addEvent("click", function(e){
                    this.checkDeleteColumn(e);
                    e.stopPropagation();
                }.bind(this));
            }
        }

        if (MWF.AC.isProcessPlatformCreator()){
            if ((creator==layout.desktop.session.user.name) || MWF.AC.isAdministrator()){
                this.editAdctionNode = new Element("div.editNode", {
                    "styles": this.app.css.categoryItemEditActionNode,
                    "title" : this.app.options.tooltip.category.edit
                }).inject(itemNode );

                itemNode.addEvents({
                    "mouseover": function(){ this.editAdctionNode.fade("in"); }.bind(this),
                    "mouseout": function(){ this.editAdctionNode.fade("out"); }.bind(this)
                });
                this.editAdctionNode.addEvent("click", function(e){
                    this.edit(e);
                    e.stopPropagation();
                }.bind(this));
            }
        }
    },
    clickNode : function(_self, el ){

    },
    createCategory: function(){
        this.createCategoryCreateMarkNode();
        this.createCategoryCreateAreaNode();
        this.createCategoryCreateNode();

        this.categoryCreateAreaNode.inject(this.categoryCreateMarkNode, "after");
        this.categoryCreateAreaNode.fade("in");
        $("createCategoryName").focus();

        this.setCategoryCreateNodeSize();
        this.setCategoryCreateNodeSizeFun = this.setCategoryCreateNodeSize.bind(this);
        this.addEvent("resize", this.setCategoryCreateNodeSizeFun);
    },
    createCategoryCreateMarkNode: function(){
        this.categoryCreateMarkNode = new Element("div", {
            "styles": this.app.css.categoryCreateMarkNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.node, "after");
    },
    createCategoryCreateAreaNode: function(){
        this.categoryCreateAreaNode = new Element("div", {
            "styles": this.app.css.categoryCreateAreaNode
        });
    },
    createCategoryCreateNode: function(){
        this.categoryCreateNode = new Element("div", {
            "styles": this.app.css.categoryCreateNode
        }).inject(this.categoryCreateAreaNode);
        this.categoryCreateNewNode = new Element("div", {
            "styles": this.app.css.categoryCreateNewNode
        }).inject(this.categoryCreateNode);

        this.categoryCreateFormNode = new Element("div", {
            "styles": this.app.css.categoryCreateFormNode
        }).inject(this.categoryCreateNode);

        var html = "<table width=\"100%\" height=\"80%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
            "<tr><td style=\"height: 30px; line-height: 30px; text-align: left; min-width: 80px; width:25%\">" +
            this.app.options.tooltip.category.nameLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createCategoryName\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px; text-align: left\">"+this.app.options.tooltip.category.aliasLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createCategoryAlias\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.options.tooltip.category.descriptionLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createCategoryDescription\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.options.tooltip.category.sortLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createCategorySort\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.options.tooltip.category.columnLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createCategoryColumn\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
                //"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.iconLabel+":</td>" +
                //"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createCategoryType\" " +
                //"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
                //"height: 26px;\"/></td></tr>" +
            "</table>";
        this.categoryCreateFormNode.set("html", html);

        this.categoryCancelActionNode = new Element("div", {
            "styles": this.app.css.categoryCreateCancelActionNode,
            "text": this.app.options.tooltip.category.cancel
        }).inject(this.categoryCreateFormNode);
        this.categoryCreateOkActionNode = new Element("div", {
            "styles": this.app.css.categoryCreateOkActionNode,
            "text": this.app.options.tooltip.category.ok
        }).inject(this.categoryCreateFormNode);

        this.categoryCancelActionNode.addEvent("click", function(e){
            this.cancelCreateCategory(e);
        }.bind(this));
        this.categoryCreateOkActionNode.addEvent("click", function(e){
            this.okCreateCategory(e);
        }.bind(this));
    },

    setCategoryCreateNodeSize: function(){
        var size = this.app.node.getSize();
        var allSize = this.app.content.getSize();
        this.categoryCreateMarkNode.setStyles({
            "width": ""+allSize.x+"px",
            "height": ""+allSize.y+"px"
        });
        this.categoryCreateAreaNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px"
        });
        var hY = size.y*0.8;
        var mY = size.y*0.2/2;
        this.categoryCreateNode.setStyles({
            "height": ""+hY+"px",
            "margin-top": ""+mY+"px"
        });

        var iconSize = this.categoryCreateNewNode.getSize();
        var formHeight = hY*0.7;
        if (formHeight>250) formHeight = 250;
        var formMargin = hY*0.3/2-iconSize.y;
        this.categoryCreateFormNode.setStyles({
            "height": ""+formHeight+"px",
            "margin-top": ""+formMargin+"px"
        });
    },
    cancelCreateCategory: function(e){
        var _self = this;
        if ($("createCategoryName").get("value") || $("createCategoryAlias").get("value") || $("createCategoryDescription").get("value")){
            this.confirm("warn", e,
                this.app.options.tooltip.category.create_cancel_title,
                this.app.options.tooltip.category.create_cancel, "320px", "100px",
                function(){
                    _self.categoryCreateMarkNode.destroy();
                    _self.categoryCreateAreaNode.destroy();
                    this.close();
                },function(){
                    this.close();
                }
            );
        }else{
            this.categoryCreateMarkNode.destroy();
            this.categoryCreateAreaNode.destroy();
        }
    },
    okCreateCategory: function(e){
        var data = {
            "name": $("createCategoryName").get("value"),
            "alias": $("createCategoryAlias").get("value"),
            "description": $("createCategoryDescription").get("value"),
            "createCategorySort": $("createCategorySort").get("value"),
            "createCategoryColumn":$("createCategoryColumn").get("value")
        };
        if (data.name){
            this.restActions.saveCategory(data, function(json){
                this.categoryCreateMarkNode.destroy();
                this.categoryCreateAreaNode.destroy();

                this.restActions.getCategory(json.data.id, function(json){
                    json.data.processList = [];
                    json.data.formList = [];
                    var category = new MWF.xCategory.process.CategoryColumn.Category(this, json.data, {"where": "top"});
                    category.load();
                    this.categorys.push(category);
                }.bind(this));

                if( this.app.noElementNode ){

                }
                this.notice(this.options.tooltip.createCategorySuccess, "success");
                //    this.app.processConfig();
            }.bind(this));
        }else{
            $("createCategoryName").setStyle("border-color", "red");
            $("createCategoryName").focus();
            this.notice(this.options.tooltip.category.inputName, "error");
        }
    },
    checkDeleteCategory: function(){
        if (this.deleteElements.length){
            if (!this.deleteElementsNode){
                this.deleteElementsNode = new Element("div", {
                    "styles": this.app.css.deleteElementsNode,
                    "text": this.app.lp.category.deleteElements
                }).inject(this.node);
                this.deleteElementsNode.position({
                    relativeTo: this.app.categoryContentNode,
                    position: "centerTop",
                    edge: "centerbottom"
                });
                this.deleteElementsNode.addEvent("click", function(e){
                    this.deleteSelectedElements(e);
                }.bind(this));
            }
        }else{
            if (this.deleteElementsNode){
                this.deleteElementsNode.destroy();
                this.deleteElementsNode = null;
                delete this.deleteElementsNode;
            }
        }
    }
})


MWF.xApplication.cms.Column.Column = new Class({
	Implements: [Options, Events],
	options: {
		"bgColor": ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"]
	},

	initialize: function (app, data, options) {
		this.setOptions(options);
		this.app = app;
		this.container = this.app.columnContentAreaNode;
		this.data = data;
        this.isNew = false;
	},
	load: function () {

		var columnName = this.data.cms_app;
		var alias = this.data.cms_app_alias;
		var memo = this.data.cms_app_memo;
		var creator =this.data.creator_uid;
		var icon = this.data.cms_app_icon;
		if( !icon || icon == "")icon = this.app.defaultColumnIcon;

		var itemNode = this.itemNode = new Element("div.columnItem", {
			"styles": this.app.css.columnItemNode
		}).inject(this.container);
		itemNode.store("columnName", columnName);
		//itemNode.setStyle("background-color", this.options.bgColor[(Math.random()*10).toInt()]);

		var iconNode = new Element("div",{
			"styles" : this.app.css.columnItemIconNode
		}).inject(itemNode);
		iconNode.setStyles({
			"background-image" : "url("+icon+")"
		});

		var textNode = new Element("div",{
			"styles" : this.app.css.columnItemTextNode
		}).inject(itemNode)

		var titleNode = new Element("div",{
			"styles" : this.app.css.columnItemTitleNode,
			"text" : columnName,
			"title": (alias) ? columnName+" ("+alias+") " : columnName
		}).inject(textNode)

		var description = ( memo && memo!="") ? memo : this.app.options.tooltip.column.noDescription;
		var descriptionNode = new Element("div",{
			"styles" : this.app.css.columnItemDescriptionNode,
			"text" : description,
			"title" : description
		}).inject(textNode)

		var _self = this;
		itemNode.addEvents({
			"mouseover": function(){if (!_self.selected) this.setStyles(_self.app.css.columnItemNode_over);},
			"mouseout": function(){if (!_self.selected) this.setStyles(_self.app.css.columnItemNode);},
			"click": function(){_self.clickColumnNode(_self,this)}
		});

		if (MWF.AC.isProcessPlatformCreator()){
			if ((creator==layout.desktop.session.user.name) || MWF.AC.isAdministrator()){
				this.delAdctionNode = new Element("div.delNode", {
					"styles": this.app.css.columnItemDelActionNode,
					"title" : this.app.options.tooltip.column.delete
				}).inject(itemNode );

				itemNode.addEvents({
					"mouseover": function(){ this.delAdctionNode.fade("in"); }.bind(this),
					"mouseout": function(){ this.delAdctionNode.fade("out"); }.bind(this)
				});
				this.delAdctionNode.addEvent("click", function(e){
					this.checkDeleteColumn(e);
					e.stopPropagation();
				}.bind(this));
			}
		}

		if (MWF.AC.isProcessPlatformCreator()){
			if ((creator==layout.desktop.session.user.name) || MWF.AC.isAdministrator()){
				this.editAdctionNode = new Element("div.editNode", {
					"styles": this.app.css.columnItemEditActionNode,
					"title" : this.app.options.tooltip.column.edit
				}).inject(itemNode );

				itemNode.addEvents({
					"mouseover": function(){ this.editAdctionNode.fade("in"); }.bind(this),
					"mouseout": function(){ this.editAdctionNode.fade("out"); }.bind(this)
				});
				this.editAdctionNode.addEvent("click", function(e){
					this.edit(e);
					e.stopPropagation();
				}.bind(this));
			}
		}
	},
	clickColumnNode : function(_self, el ){
		_self.app.columns.each(function( column ){
			if( column.selected ){
				column.itemNode.setStyles( _self.app.css.columnItemNode );
			}
		})
		this.selected = true;
		el.setStyles( _self.app.css.columnItemNode_select );
	},
	checkDeleteColumn: function(){
		if (this.deleteElements.length){
			if (!this.deleteElementsNode){
				this.deleteElementsNode = new Element("div", {
					"styles": this.app.css.deleteElementsNode,
					"text": this.app.lp.column.deleteElements
				}).inject(this.node);
				this.deleteElementsNode.position({
					relativeTo: this.container,
					position: "centerTop",
					edge: "centerbottom"
				});
				this.deleteElementsNode.addEvent("click", function(e){
					this.deleteSelectedElements(e);
				}.bind(this));
			}
		}else{
			if (this.deleteElementsNode){
				this.deleteElementsNode.destroy();
				this.deleteElementsNode = null;
				delete this.deleteElementsNode;
			}
		}
	},
    edit : function(){
        this.isNew = false;
        this.createContainer = this.app.node;
        this.createColumnCreateMarkNode();
        this.createColumnCreateAreaNode();
        this.createColumnCreateNode();

        this.columnCreateAreaNode.inject(this.columnCreateMarkNode, "after");
        this.columnCreateAreaNode.fade("in");
        $("createColumnName").focus();

        this.setColumnCreateNodeSize();
        this.setColumnCreateNodeSizeFun = this.setColumnCreateNodeSize.bind(this);
        this.addEvent("resize", this.setColumnCreateNodeSizeFun);
    },
	createColumn: function( container ){
        this.isNew = true;

        this.createContainer = container;
		this.createColumnCreateMarkNode();
		this.createColumnCreateAreaNode();
		this.createColumnCreateNode();

		this.columnCreateAreaNode.inject(this.columnCreateMarkNode, "after");
		this.columnCreateAreaNode.fade("in");
		$("createColumnName").focus();

		this.setColumnCreateNodeSize();
		this.setColumnCreateNodeSizeFun = this.setColumnCreateNodeSize.bind(this);
		this.addEvent("resize", this.setColumnCreateNodeSizeFun);
	},
	createColumnCreateMarkNode: function(){
		this.columnCreateMarkNode = new Element("div", {
			"styles": this.app.css.columnCreateMarkNode,
			"events": {
				"mouseover": function(e){e.stopPropagation();},
				"mouseout": function(e){e.stopPropagation();}
			}
		}).inject(this.createContainer, "after");
	},
	createColumnCreateAreaNode: function(){
		this.columnCreateAreaNode = new Element("div", {
			"styles": this.app.css.columnCreateAreaNode
		});
	},
	createColumnCreateNode: function(){

        if(!this.isNew){
            var columnName = this.data.cms_app;
            var alias = this.data.cms_app_alias;
            var memo = this.data.cms_app_memo;
            var creator =this.data.creator_uid;
			var order  = this.data.cms_app_order;
            var icon = this.data.cms_app_icon;
            if( !icon || icon == "")icon = this.app.defaultColumnIcon;
        }else{
            var columnName = "";
            var alias = "";
            var memo = "";
			var order = "";
            var creator ="";
            var icon = "";
        }

		this.columnCreateNode = new Element("div", {
			"styles": this.app.css.columnCreateNode
		}).inject(this.columnCreateAreaNode);

		this.columnCreateNewNode = new Element("div", {
			"styles": ( this.isNew ? this.app.css.columnCreateNewNode : this.app.css.columnCreateEditNode )
		}).inject(this.columnCreateNode);

		this.columnCreateFormNode = new Element("div", {
			"styles": this.app.css.columnCreateFormNode
		}).inject(this.columnCreateNode);

		var html = "<table width=\"100%\" height=\"80%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
			"<tr><td style=\"height: 30px; line-height: 30px; text-align: left; min-width: 80px; width:25%\">" +
            this.app.options.tooltip.column.nameLabel+":</td>" +
			"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnName\" " +
			"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			"height: 26px;\" value=\""+columnName+"\"/></td></tr>" +
			"<tr><td style=\"height: 30px; line-height: 30px; text-align: left\">"+this.app.options.tooltip.column.aliasLabel+":</td>" +
			"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnAlias\" " +
			"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			"height: 26px;\" value=\""+alias+"\"/></td></tr>" +
			"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.options.tooltip.column.descriptionLabel+":</td>" +
			"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnDescription\" " +
			"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			"height: 26px;\" value=\""+memo+"\"/></td></tr>" +
			"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.options.tooltip.column.sortLabel+":</td>" +
			"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnSort\" " +
			"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
			"height: 26px;\" value=\""+order+"\"/></td></tr>"+
				//"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.column.iconLabel+":</td>" +
				//"<td style=\"; text-align: right;\"><div " +
				//"style=\"height:72px; width:72px;background:url(/x_component_cms_Column/$Main/default/icon/column.png) center center no-repeat \"></div></td></tr>" +
				//"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.iconLabel+":</td>" +
				//"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnType\" " +
				//"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
				//"height: 26px;\"/></td></tr>" +
			"</table>";
		this.columnCreateFormNode.set("html", html);

		this.columnCancelActionNode = new Element("div", {
			"styles": this.app.css.columnCreateCancelActionNode,
			"text": this.app.options.tooltip.column.cancel
		}).inject(this.columnCreateFormNode);
		this.columnCreateOkActionNode = new Element("div", {
			"styles": this.app.css.columnCreateOkActionNode,
			"text": this.app.options.tooltip.column.ok
		}).inject(this.columnCreateFormNode);

		this.columnCancelActionNode.addEvent("click", function(e){
			this.cancelCreateColumn(e);
		}.bind(this));
		this.columnCreateOkActionNode.addEvent("click", function(e){
			this.okCreateColumn(e);
		}.bind(this));
	},

	setColumnCreateNodeSize: function(){
		var size = this.createContainer.getSize();
		var allSize = this.app.content.getSize();
		this.columnCreateMarkNode.setStyles({
			"width": ""+allSize.x+"px",
			"height": ""+allSize.y+"px"
		});
		this.columnCreateAreaNode.setStyles({
			"width": ""+size.x+"px",
			"height": ""+size.y+"px"
		});
		var hY = size.y*0.8;
		var mY = size.y*0.2/2;
		this.columnCreateNode.setStyles({
			"height": ""+hY+"px",
			"margin-top": ""+mY+"px"
		});

		var iconSize = this.columnCreateNewNode.getSize();
		var formHeight = hY*0.7;
		if (formHeight>250) formHeight = 250;
		var formMargin = hY*0.3/2-iconSize.y;
		this.columnCreateFormNode.setStyles({
			"height": ""+formHeight+"px",
			"margin-top": ""+formMargin+"px"
		});
	},
	cancelCreateColumn: function(e){
		if(this.isNew){
			this.cancelNewColumn(e)
		}else{
			this.cancelEditColumn(e)
		}
	},
	cancelNewColumn: function(e){
		var _self = this;
		if ($("createColumnName").get("value") || $("createColumnAlias").get("value") || $("createColumnDescription").get("value")){
			this.app.confirm("warn", e, this.app.options.tooltip.column.create_cancel_title,
                this.app.options.tooltip.column.create_cancel, "320px", "100px", function(){
				_self.columnCreateMarkNode.destroy();
				_self.columnCreateAreaNode.destroy();
				this.close();
			},function(){
				this.close();
			});
		}else{
			this.columnCreateMarkNode.destroy();
			this.columnCreateAreaNode.destroy();
		}
	},
	cancelEditColumn : function(e){
		this.columnCreateMarkNode.destroy();
		this.columnCreateAreaNode.destroy();
	},
	okCreateColumn: function(e){
		var data = {
			"name": $("createColumnName").get("value"),
			"alias": $("createColumnAlias").get("value"),
			"description": $("createColumnDescription").get("value"),
			"createColumnSort": $("createColumnSort").get("value")
		};
		if (data.name){
			this.restActions.saveColumn(data, function(json){
				this.columnCreateMarkNode.destroy();
				this.columnCreateAreaNode.destroy();

				this.restActions.getColumn(json.data.id, function(json){
					json.data.processList = [];
					json.data.formList = [];
					var column = new MWF.xColumn.process.ColumnColumn.Column(this, json.data, {"where": "top"});
					column.load();
					this.app.columns.push(column);
				}.bind(this));

				this.app.notice(this.options.tooltip.createColumnSuccess, "success");
				//    this.app.processConfig();
			}.bind(this));
		}else{
			$("createColumnName").setStyle("border-color", "red");
			$("createColumnName").focus();
			this.app.notice(this.app.options.tooltip.column.inputName, "error");
		}
	}

})
