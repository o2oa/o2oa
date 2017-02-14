MWF.xDesktop.requireApp("cms.ColumnManager", "Explorer", null, false);
MWF.xApplication.cms.ColumnManager.CategoryExplorer = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer,
	Implements: [Options, Events],

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "/x_component_cms_ColumnManager/$CategoryExplorer/";
        this.cssPath = "/x_component_cms_ColumnManager/$CategoryExplorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.initData();
    },

    _createElement: function(e){

        this.category = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category(this, null );
        this.category.css = this.css;
        this.category.app = this.app;
        this.category.parent = this;
        this.category.createCategory();
    },

    _loadItemDataList: function(callback){
        this.app.restActions.listCategory(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        var category = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category(this, item);
        category.css = this.css;
        category.app = this.app;
        category.parent = this;
        return category;
    },
    deleteItems: function(){
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteCategory();
            }else{
                item.deleteCategory(function(){
                    //    this.reloadItems();
                    this.hideDeleteAction();
                    this.reload();
                }.bind(this));
            }
        }
    }
});


MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer.Item,
    Implements: [Events],

	_open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.app.options.application;
            }
        };
        this.isNew = false;
        this.isEdited = false;
        //this.explorer.app.desktop.openApplication(e, "cms.CategoryDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*33).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"processIcon/lnk.png",
			"title": this.data.name,
			"par": "cms.CategoryDesigner#{\"id\": \""+this.data.id+"\"}"
		};
	},
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.cms.deleteCategoryTitle, this.explorer.app.lp.cms.deleteCategory, 320, 110, function(){
//			_self.deleteCategory();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
	deleteCategory: function(callback){
		this.app.restActions.removeCategory(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this),function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            if( errorText.indexOf( "referenced" ) > -1 ){
                var lp = this.explorer.app.lp.category;
                var text = lp.deleteFailAsHasDocument.replace(/{title}/g, this.data.name );
                this.app.notice( text,"error");
            }
        }.bind(this));
	},
    createCategory: function(){
        this.isNew = true;
        this._openCategory();
    },
    _open: function(){
        this.isEdited = true;
        this.app.restActions.getCategory( this.data.id, function( json ){
            this.data = json.data;
            this._openCategory();
        }.bind(this))
    }, 
    _openCategory : function(){
        this.categoryCreateMarkNode = new Element("div", {
            "styles": this.css.categoryCreateMarkNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content, "after");

        this.categoryCreateAreaNode = new Element("div", {
            "styles": this.css.categoryCreateAreaNode
        });

        this.createCategoryCreateNode();

        this.categoryCreateAreaNode.inject(this.categoryCreateMarkNode, "after");
        this.categoryCreateAreaNode.fade("in");

        $("createCategoryName").focus();

        this.setCategoryCreateNodeSize();
        this.setCategoryCreateNodeSizeFun = this.setCategoryCreateNodeSize.bind(this);
        this.addEvent("resize", this.setCategoryCreateNodeSizeFun);
    },
    createCategoryCreateNode: function(){
        this.categoryCreateNode = new Element("div", {
            "styles": this.css.categoryCreateNode
        }).inject(this.categoryCreateAreaNode);


        this.categoryCreateIconNode = new Element("div", {
            "styles": this.isNew ? this.css.categoryCreateNewNode : this.css.categoryCreateIconNode
        }).inject(this.categoryCreateNode);


        this.categoryCreateFormNode = new Element("div", {
            "styles": this.css.categoryCreateFormNode
        }).inject(this.categoryCreateNode);

        var formName = this.data && this.data.formName ? this.data.formName : "";
        var formId = this.data && this.data.formId ? this.data.formId : "";
        var readFormName = this.data && this.data.readFormName ? this.data.readFormName : "";
        var readFormId = this.data && this.data.readFormId ? this.data.readFormId : "";
        var defaultViewName = this.data && this.data.defaultViewName ? this.data.defaultViewName : "";

        var inputStyle = "width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC;height: 26px;";
        var html = "<table width=\"100%\" height=\"80%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
            "<tr>"+
                "<td style=\"height: 30px; line-height: 30px; text-align: left; min-width: 80px; width:25%\">" + this.app.lp.category.nameLabel+":</td>" +
                "<td style=\"; text-align: right;\">"+
                    (!this.isNew && !this.isEdited  ? "" :
                      ("<input type=\"text\" id=\"createCategoryName\" " + "style=\"" + inputStyle +"\"" + " value=\"" + ( this.data && this.data.name ? this.data.name : "") + "\"/>")) +
                "</td>"+
            "</tr>" +
            "<tr>"+
                "<td style=\"height: 30px; line-height: 30px; text-align: left\">"+this.app.lp.category.aliasLabel+":</td>" +
                "<td style=\"; text-align: right;\">"+
                    (!this.isNew && !this.isEdited  ? "" :
                    ("<input type=\"text\" id=\"createCategoryAlias\" " + "style=\"" + inputStyle +"\"" + " value=\"" + ( this.data && this.data.alias ? this.data.alias : "") + "\"/>")) +
                "</td>" +
            "</tr>" +
            "<tr>" +
                "<td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.lp.category.descriptionLabel+":</td>" +
                "<td style=\"; text-align: right;\">" +
                    (!this.isNew && !this.isEdited  ? "" :
                 ("<input type=\"text\" id=\"createCategoryDescription\" " + "style=\"" + inputStyle +"\"" + " value=\"" + ( this.data && this.data.description ? this.data.description : "") + "\"/>")) +
                 "</td>" +
            "</tr>" +
            "<tr>" +
                "<td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.lp.category.sortLabel+":</td>" +
                "<td style=\"; text-align: right;\">" +
                    (!this.isNew && !this.isEdited  ? "" :
                    ("<input type=\"text\" id=\"createCategorySort\" " + "style=\"" + inputStyle +"\"" + " value=\"" + ( this.data && this.data.catagorySeq ? this.data.catagorySeq : "") + "\"/>")) +
                "</td>" +
            "</tr>" +
            "<tr>" +
                "<td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.lp.category.formLabelEdit+":</td>" +
                "<td style=\"; text-align: left;\">" +
                        (!this.isNew && !this.isEdited  ?  formName : this.getFormString( formName, formId, "edit" )) +
                    //("<input type=\"text\" id=\"createCategoryForm\" " + "style=\"" + inputStyle +"\"" + " value=\"" + ( this.data && this.data.formName ? this.data.formName : "") + "\"/>")) +
                "</td>" +
            "</tr>" +
            "<tr>" +
                "<td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.lp.category.formLabelRead+":</td>" +
                "<td style=\"; text-align: left;\">" +
                (!this.isNew && !this.isEdited  ?  readFormName : this.getFormString( readFormName, readFormId, "read" )) +
                    //("<input type=\"text\" id=\"createCategoryForm\" " + "style=\"" + inputStyle +"\"" + " value=\"" + ( this.data && this.data.formName ? this.data.formName : "") + "\"/>")) +
                "</td>" +
            "</tr>" +
            "<tr>" +
                "<td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.lp.category.viewLabel+":</td>" +
                "<td style=\"; text-align: left;\" class=\"viewArea\">" +
                //("<input type=\"text\" id=\"createCategoryForm\" " + "style=\"" + inputStyle +"\"" + " value=\"" + ( this.data && this.data.formName ? this.data.formName : "") + "\"/>")) +
            "</td>" +
            "</tr>" +
            "<tr>" +
            "<td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.app.lp.category.defaultViewLabel+":</td>" +
            "<td style=\"; text-align: left;\" class=\"defaultViewArea\">" +
            "</td>" +
            "</tr>" +
                //"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.iconLabel+":</td>" +
                //"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createCategoryType\" " +
                //"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
                //"height: 26px;\"/></td></tr>" +
            "</table>";
        this.categoryCreateFormNode.set("html", html);

        this.viewArea = this.categoryCreateFormNode.getElements(".viewArea")[0];
        this.defaultViewArea = this.categoryCreateFormNode.getElements(".defaultViewArea")[0];

        this.createCategoryForm = this.categoryCreateFormNode.getElements("#createCategoryForm_edit")[0];
        if( formId != "" ){
            this.setViewElement((this.data && this.data.id) ? this.data.id :"", formId, this.viewArea,!this.isNew && !this.isEdited );
            this.setDefaultViewElement( defaultViewName, (this.data && this.data.id) ? this.data.id :"", formId, this.defaultViewArea );
        }
        //if(this.data)
        this.createCategoryForm.addEvent("change",function(el){
            this.viewArea.set("html","");
            this.defaultViewArea.set("html","");
            var opt = this.getFormSelectedOption();
            //opt.value
            this.setViewElement((this.data && this.data.id) ? this.data.id :"",opt.value,this.viewArea,!this.isNew && !this.isEdited);
            this.setDefaultViewElement( defaultViewName, (this.data && this.data.id) ? this.data.id :"",opt.value,this.defaultViewArea );
        }.bind(this))

        this.categoryCancelActionNode = new Element("div", {
            "styles": this.css.categoryCreateCancelActionNode,
            "text": this.app.lp.category.cancel
        }).inject(this.categoryCreateFormNode);
        this.categoryCreateOkActionNode = new Element("div", {
            "styles": this.css.categoryCreateOkActionNode,
            "text": this.app.lp.category.ok
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
        /*
         this.categoryCreateMarkNode.setStyles({
         "width": ""+allSize.x+"px",
         "height": ""+allSize.y+"px"
         });
         */
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

        var iconSize = this.categoryCreateIconNode.getSize();
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
        if ( this.isNew && ( $("createCategoryName").get("value") || $("createCategoryAlias").get("value") || $("createCategoryDescription").get("value") )){
            this.app.confirm("warn", e,
                this.app.lp.category.create_cancel_title,
                this.app.lp.category.create_cancel, "320px", "100px",
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
            delete _self;
        }
    },
    okCreateCategory: function(e){
        var formOption_edit = this.getFormSelectedOption("edit");
        var formOption_read = this.getFormSelectedOption("read");
        var defaultViewOption = this.getDefaultViewSelectedOption();
        var data = {
            "isNew" : this.isNew,
            "id" : (this.data && this.data.id) ? this.data.id : this.app.restActions.getUUID(),
            "catagoryName": $("createCategoryName").get("value"),
            "catagoryAlias": $("createCategoryAlias").get("value"),
            "description": $("createCategoryDescription").get("value"),
            "catagorySeq": $("createCategorySort").get("value"),
            "formName": formOption_edit.get("text"),
            "formId": formOption_edit.get("value"),
            "readFormName" : formOption_read.get("text"),
            "readFormId": formOption_read.get("value"),
            "defaultViewName" : defaultViewOption.get("value"),
            "appId": this.app.options.application.id
        };

        if (data.catagoryName){
            this.app.restActions.saveCategory(data, function(json){
                this.saveViewData(this.viewArea, json.data.id, data.formId );
                this.categoryCreateMarkNode.destroy();
                this.categoryCreateAreaNode.destroy();
                if(this.explorer.noElementNode)this.explorer.noElementNode.destroy();
                this.app.restActions.getCategory(json.data.id, function(json) {
                    json.data.processList = [];
                    json.data.formList = [];
                    if (!this.isNew) this.node.destroy();
                    var category = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category(this.parent, json.data, {"where": "top"});
                    category.load();
                    category.css = this.css;
                    category.app = this.app;
                    category.parent = this.parent;
                    if (this.app.categorys)this.app.categorys.push(category);
                }.bind(this));

                this.app.notice( this.isNew ? MWF.CMSCM.LP.category.createCategorySuccess : MWF.CMSCM.LP.category.updateCategorySuccess  , "success");
                //    this.app.processConfig();
            }.bind(this));
        }else{
            $("createCategoryName").setStyle("border-color", "red");
            $("createCategoryName").focus();
            this.app.notice(MWF.CMSCM.LP.category.inputName, "error");
        }
    },
    setViewElement : function( categoryId, formId, container, readonly, callback ){
        if( !formId || formId=="")return;
        var views;
        this.app.restActions.listViewByForm(formId, function(json){
            views = json.data;
            if (callback) callback();
        }.bind(this),null,false);

        this.selectedViews = [];
        this.selectedCategoryViews = [];
        if( categoryId && categoryId!="" ){
            this.app.restActions.listCategoryViewByCatagory(categoryId, function(json){
                json.data.each(function( d  ){
                    this.selectedViews.push(d.viewId);
                    this.selectedCategoryViews.push(d.id);
                }.bind(this));
                if (callback) callback();
            }.bind(this),null,false);
        }

        var _self = this;
        var node = new Element("span", {"styles":_self.css.categoryCheckBox } ).inject( container );
        //var input = new Element("input",{
        //    "type" : "checkbox",
        //    "value" : "default"
        //}).inject( node );
        //if( _self.selectedViews.contains( "default" ) || this.isNew )input.checked = true;
        //new Element("span" , { "text" : MWF.CMSCM.LP.category.defaultView } ).inject( node );
        views.each(function( view ){
            if( readonly ){
                if( _self.selectedViews.contains( view.id ) ){
                    new Element("span" , { "text" : view.name, "styles":_self.css.categoryCheckBox  } ).inject( container );
                }
            }else{
                var node = new Element("span", {"styles":_self.css.categoryCheckBox } ).inject( container );
                var input = new Element("input",{
                    "type" : "checkbox",
                    "value" : view.id
                }).inject( node );
                if( _self.selectedViews.contains( view.id ) )input.checked = true;
                new Element("span" , { "text" : view.name } ).inject( node );
            }
        })
    },
    setDefaultViewElement : function( defaultView, categoryId, formId, container, callback ){
        if( !formId || formId=="")return;
        var views;
        this.app.restActions.listViewByForm(formId, function(json){
            views = json.data;
            if (callback) callback();
        }.bind(this),null,false);

        //this.selectedViews = [];
        //this.selectedCategoryViews = [];
        //if( categoryId && categoryId!="" ){
        //    this.app.restActions.listCategoryViewByCatagory(categoryId, function(json){
        //        json.data.each(function( d  ){
        //            this.selectedViews.push(d.viewId);
        //            this.selectedCategoryViews.push(d.id);
        //        }.bind(this));
        //        if (callback) callback();
        //    }.bind(this),null,false);
        //}

        var _self = this;
        var node = new Element("select", {id : "defaultViewSelect"}).inject( container );
        var input = new Element("option",{
            "text" : MWF.CMSCM.LP.category.defaultView,
            "value" : "default"
        }).inject( node );
        if( (defaultView == "default") || this.isNew )input.selected = true;
        views.each(function( view ){
            var input = new Element("option",{
                 "text" : view.name,
                 "value" : view.id
            }).inject( node );
            if( defaultView == view.id )input.selected = true;
        });
    },
    getDefaultViewSelectedOption : function(){
        var result;
        this.categoryCreateFormNode.getElement("#defaultViewSelect").getElements("option").each(function(option){
            if( option.selected )result = option;
        })
        return result;
    },
    getFormString : function(formName, formId, status ){
        var formString = "<select id=\"createCategoryForm_"+ status +"\">";
        formString += ("<option value=''></option>");
        if( this.forms ){
            this.forms.each(function( form ){
                if( formId == form.id ){
                    formString += ("<option value='"+form.id+"' selected>"+form.name+"</option>")
                }else{
                    formString += ("<option value='"+form.id+"'>"+form.name+"</option>")
                }
            })
            formString += "</select>"
        }else{
            this.app.restActions.listForm(this.app.options.application.id, function(json){
                this.forms = json.data;
                json.data.each(function( form ){
                    if( formId == form.id ){
                        formString += ("<option value='"+form.id+"' selected>"+form.name+"</option>")
                    }else{
                        formString += ("<option value='"+form.id+"'>"+form.name+"</option>")
                    }
                })
            }.bind(this), null, false);
            formString += "</select>"
        }

        return formString;
    },
    getFormSelectedOption : function( status ){
        var result;
        //$$("#createCategoryForm option")
        if(!status)status="edit";
        this.categoryCreateFormNode.getElement("#createCategoryForm_"+status).getElements("option").each(function(option){
            if( option.selected )result = option;
        })
        return result;
    },
    saveViewData : function(container, categoryId, formId ){
        var views = [];
        container.getElements("input:checked").each(function(view){
            views.push(view.value);
        })
        if(  this.isNew ){
            for(var i=0; i<views.length;i++){
                var data = {
                    "catagoryId" : categoryId,
                    "viewId" : views[i]
                }
                this.app.restActions.addCategoryView(data);
            }
        }else if( this.data && this.data.formId != formId ){
            //var tmpSelectedViews = [];
            //var tmpSelectedCategoryViews = [];
            for(var i=0; i<this.selectedCategoryViews.length;i++){
                this.app.restActions.deleteCategoryView(this.selectedCategoryViews[i]);
            }
            for(var i=0; i<views.length;i++){
                var data = {
                    "catagoryId" : categoryId,
                    "viewId" : views[i]
                }
                this.app.restActions.addCategoryView(data, function(json){
                    //tmpSelectedViews.push(views[i]);
                    //tmpSelectedCategoryViews.push(json.data.id);
                }.bind(this), null, false);
            }
        }else{
            //var tmpSelectedViews = this.selectedViews;
            //var tmpSelectedCategoryViews = this.selectedCategoryViews;
            //取消的删除掉
            for(var i=0; i<this.selectedViews.length;i++){
                if( !views.contains(this.selectedViews[i]) ){
                    this.app.restActions.deleteCategoryView(this.selectedCategoryViews[i]);
                    //tmpSelectedViews.splice(i,1);
                    //tmpSelectedCategoryViews.splice(i,1);
                }
            }
            //新增的增加
            for(var i=0; i<views.length;i++){
                if( !this.selectedViews.contains(views[i]) ){
                    var data = {
                        "catagoryId" : categoryId,
                        "viewId" : views[i]
                    }
                    this.app.restActions.addCategoryView(data, function(json){
                        //tmpSelectedViews.push(views[i]);
                        //tmpSelectedCategoryViews.push(json.data.id);
                    }.bind(this), null, false);
                }
            }
        }
        //this.selectedViews = tmpSelectedViews;
        //this.selectedCategoryViews = tmpSelectedCategoryViews;
    }
});

