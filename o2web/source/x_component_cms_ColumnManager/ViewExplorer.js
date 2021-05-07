MWF.xDesktop.requireApp("cms.ColumnManager", "Explorer", null, false);
MWF.xApplication.cms.ColumnManager.ViewExplorer = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.Explorer,
    Implements: [Options, Events],
    options: {
        "style" : "default",
        "create": MWF.CMSCM.LP.view.create,
        "search": MWF.CMSCM.LP.view.search,
        "searchText": MWF.CMSCM.LP.view.searchText,
        "noElement": MWF.CMSCM.LP.view.noViewNoticeText
    },


    //_createElement: function(e){
    //    var _self = this;
    //    var options = {
    //        "onQueryLoad": function(){
    //            this.actions = _self.app.restActions;
    //            this.application = _self.app.options.column;
    //            this.column = _self.app.options.column;
    //        }
    //    };
    //    this.app.desktop.openApplication(e, "cms.ViewDesigner", options);
    //},
    _createElement: function(e){
        var _self = this;
        var createView = function(e, form){
            layout.desktop.getFormDesignerStyle(function(){
                var options = {
                    "style": layout.desktop.formDesignerStyle,
                    "onQueryLoad": function(){
                        this.actions = _self.app.restActions;
                        this.column = _self.app.options.column;
                        this.application = _self.app.options.column;
                        this.relativeForm = form;
                    },
                    "onPostSave" : function(){
                        _self.reload();
                    }
                };
                layout.desktop.openApplication(e, "cms.ViewDesigner", options);
            }.bind(this));

        };

        this.loadSelectFormDialog( createView );

    },
    loadSelectFormDialog : function(callback, title, appId){
        var _self = this;

        var selectFormMaskNode = new Element("div", {"styles": this.css.selectFormMaskNode}).inject(this.app.content);
        var selectFormAreaNode = new Element("div", {"styles": this.css.selectFormTemplateAreaNode}).inject(this.app.content);
        selectFormAreaNode.fade("in");

        var selectFormTitleNode = new Element("div",{
            "styles":this.css.createTemplateFormTitleNode,
            "text":title || this.app.lp.view.selectRelativeForm
        }).inject(selectFormAreaNode);

        var selectFormScrollNode = new Element("div", {"styles": this.css.selectFormScrollNode}).inject(selectFormAreaNode);
        var selectFormContentNode = new Element("div", {"styles": this.css.selectFormContentNode}).inject(selectFormScrollNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(selectFormScrollNode, {"indent": false});
        }.bind(this));

        var _self = this;
        this.app.restActions.listForm( appId || this.app.options.column.id, function(json){
            json.data.each(function(form){

                var formNode = new Element("div", {
                    "styles": this.css.formNode
                }).inject(selectFormContentNode);

                var x = "process_icon_" + (Math.random()*33).toInt() + ".png";
                var iconUrl = this.path+this.options.style+"/processIcon/"+x;

                var formIconNode = new Element("div", {
                    "styles": this.css.formIconNode
                }).inject(formNode);
                formIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");

                new Element("div", {
                    "styles": this.css.formTitleNode,
                    "text": form.name
                }).inject(formNode);

                new Element("div", {
                    "styles": this.css.formDescriptionNode,
                    "text": form.description || "",
                    "title": form.description || ""
                }).inject(formNode);

                new Element("div", {
                    "styles": this.css.formDateNode,
                    "text": (form.updateTime || "")
                }).inject(formNode);

                formNode.store("form", {"name":form.name, "id":form.id});

                formNode.addEvents({
                    "mouseover": function(){this.setStyles(_self.css.formNode_over)},
                    "mouseout": function(){this.setStyles(_self.css.formNode)},
                    "mousedown": function(){this.setStyles(_self.css.formNode_down)},
                    "mouseup": function(){this.setStyles(_self.css.formNode_over)},
                    "click": function(e){
                        //createView(e, this.retrieve("form"));
                        if(callback)callback(e, this.retrieve("form"));
                        selectFormAreaNode.destroy();
                        selectFormMaskNode.destroy();
                    }
                });

            }.bind(this));

            var size = this.app.content.getSize();
            var nodeSize = selectFormAreaNode.getSize();

            var y = (size.y - nodeSize.y)/2;
            var x = (size.x - nodeSize.x)/2;
            if (y<0) y=0;
            if (x<0) x=0;
            selectFormAreaNode.setStyles({
                "top": ""+y+"px",
                "left": ""+x+"px"
            });

        }.bind(this));

        selectFormMaskNode.addEvent("click", function(){
            selectFormAreaNode.destroy();
            selectFormMaskNode.destroy();
        });

    },
    _loadItemDataList: function(callback){
        this.actions.listView(this.app.options.column.id,callback);
    },
    _getItemObject: function(item, index){
        return new MWF.xApplication.cms.ColumnManager.ViewExplorer.View(this, item, {index:index})
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.CMSCM.LP.view.create,
            "search": MWF.CMSCM.LP.view.search,
            "searchText": MWF.CMSCM.LP.view.searchText,
            "noElement": MWF.CMSCM.LP.view.noViewNoticeText
        };
    },
    loadElementList: function(){
        this._loadItemDataList(function(json){
            json.data = json.data || [];
            if (json.data.length){
                json.data.each(function(item){
                    var itemObj = this._getItemObject(item, this.itemArray.length+1);
                    itemObj.load();
                    this.itemObject[ item.id ] = itemObj;
                    this.itemArray.push( itemObj );
                }.bind(this));
            }else{
                var noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": (this.options.noCreate) ? MWF.CMSCM.LP.view.noViewNoCreateNoticeText : this.options.tooltip.noElement
                }).inject(this.elementContentListNode);
                if (!this.options.noCreate){
                    noElementNode.addEvent("click", function(e){
                        this._createElement(e);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    deleteItems: function(){
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteView();
            }else{
                item.deleteView(function(){
                    //    this.reloadItems();
                    this.hideDeleteAction();
                    this.reload();
                }.bind(this));
            }
        }
    },
    keyCopy: function(e){
        if (this.selectMarkItems.length){
            var items = [];
            var i = 0;

            var checkItems = function(e){
                if (i>=this.selectMarkItems.length){
                    if (items.length){
                        var str = JSON.encode(items);
                        if (e){
                            e.clipboardData.setData('text/plain', str);
                        }else {
                            window.clipboardData.setData("Text", str);
                        }
                        this.app.notice(this.app.lp.copyed, "success");
                    }
                }
            }.bind(this);

            this.selectMarkItems.each(function(item){
                this.app.restActions.getView(item.data.id, function(json){
                    json.data.elementType = "view";
                    items.push(json.data);
                    i++;
                    checkItems(e);
                }.bind(this), null, false)
            }.bind(this));
        }
    },
    keyPaste: function(e){
        var dataStr = "";
        if (e){
            dataStr = e.clipboardData.getData('text/plain');
        }else{
            dataStr = window.clipboardData.getData("Text");
        }
        var data = JSON.decode(dataStr);

        this.loadSelectFormDialog( function( e, form ){
            this.pasteItem(data, 0, form);
        }.bind(this),  MWF.CMSCM.LP.view.selectRelativeFormNoticeText ); //"请选择需粘贴视图的关联表单"

    },
    pasteItem: function(data, i, form){
        if (i<data.length){
            var item = data[i];
            if (item.elementType==="view"){
                this.saveItemAs(item, function(){
                    i++;
                    this.pasteItem(data, i, form);
                }.bind(this), function(){
                    i++;
                    this.pasteItem(data, i, form);
                }.bind(this), function(){
                    this.reload();
                }.bind(this), form );
            }else{
                i++;
                this.pasteItem(data, i, form);
            }
        }else{
            this.reload();
        }
    },
    saveItemAs: function(data, success, failure, cancel, form){
        this.app.restActions.listView(this.app.options.application.id, function(dJson){
            dJson.data = dJson.data || [];
            var i=1;
            var someItems = dJson.data.filter(function(d){ return d.id===data.id });
            if (someItems.length){
                var someItem = someItems[0];
                var lp = this.app.lp;

                var _self = this;
                var d1 = new Date().parse(data.updateTime);
                var d2 = new Date().parse(someItem.updateTime);
                var html = "<div>"+lp.copyConfirmInfor+"</div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='font-weight: bold; font-size:14px;'>"+lp.copySource+" "+someItem.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left'>"+someItem.updateTime+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'></div>" +
                    "<div style='color: red; float: right;'>"+((d1>=d2) ? "": lp.copynew)+"</div></div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='clear: both;font-weight: bold; font-size:14px;'>"+lp.copyTarget+" "+data.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left;'>"+data.updateTime+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'></div>" +
                    "<div style='color: red; float: right;'>"+((d1<=d2) ? "": lp.copynew)+"</div></div>";
//                html += "<>"
                this.app.dlg("inofr", null, this.app.lp.copyConfirmTitle, {"html": html}, 500, 290, [
                    {
                        "text": lp.copyConfirm_overwrite,
                        "action": function(){_self.saveItemAsUpdate(someItem, data, success, failure);this.close();}
                    },
                    {
                        "text": lp.copyConfirm_new,
                        "action": function(){_self.saveItemAsNew(dJson, data, success, failure);this.close();}
                    },
                    {
                        "text": lp.copyConfirm_skip,
                        "action": function(){/*nothing*/ this.close(); if (success) success();}
                    },
                    {
                        "text": lp.copyConfirm_cancel,
                        "action": function(){this.close(); if (cancel) cancel();}
                    }
                ]);
            }else{
                this.saveItemAsNew(dJson, data, success, failure, form)
            }
        }.bind(this), function(){if (failure) failure();}.bind(this));
    },
    saveItemAsUpdate: function(someItem, data, success, failure, form){
        data.isNew = false;
        data.id = someItem.id;

        data.application = someItem.appId || someItem.application;
        data.applicationName = someItem.appName || someItem.applicationName;
        data.appId = data.application;
        data.appName = data.applicationName;

        data.name = someItem.name;
        data.alias = someItem.alias;

        data.formId = form.id;

        var content = JSON.parse( data.content );
        content.application = data.application;
        content.applicationName = data.applicationName;
        content.relativeForm = form;
        content.id = data.id;
        content.name = someItem.name;
        content.alias = someItem.alias;

        var fields = [];
        content.columns.each( function( c ){
            c.id = this.app.restActions.getUUID();
            c.isNew = false;

            var field = {};
            field.id = c.id;
            field.isNew = true;
            field.viewId = data.id;
            field.fieldTitle = c.title;
            field.fieldName = c.value;
            field.xshowSequence = form.id;
            fields.push( field )
        }.bind(this));

        //data.fields.each( function(field){
        //    var list = content.columns.filter(function(d){ return d.id===field.id });
        //    field.xshowSequence = form.id;
        //    field.viewId = data.id;
        //    field.id = this.app.restActions.getUUID();
        //    if(list.length > 0 ){
        //        list[0].id = field.id;
        //    }
        //}.bind(this));
        data.content = JSON.stringify( content );

        this.app.restActions.saveView(data, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    },
    saveItemAsNew: function(dJson, data, success, failure, form){
        var item = this.app.options.application;
        var id = item.id;
        var name = item.name;
        var oldName = data.name;

        var i=1;
        while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
            data.name = oldName+"_copy"+i;
            data.alias = oldName+"_copy"+i;
            i++;
        }
        data.isNew = true;
        data.id = this.app.restActions.getUUID();
        data.application = id;
        data.applicationName = name;

        data.appId = id;
        data.appName = name;

        data.formId = form.id;

        var content = JSON.parse( data.content );
        content.application = data.application;
        content.applicationName = data.applicationName;
        content.relativeForm = form;
        content.id = data.id;
        content.name = data.name;
        content.alias = data.alias;

        var fields = [];
        content.columns.each( function( c ){
            c.id = this.app.restActions.getUUID();
            c.isNew = false;

            var field = {};
            field.id = c.id;
            field.isNew = true;
            field.viewId = data.id;
            field.fieldTitle = c.title;
            field.fieldName = c.value;
            field.xshowSequence = form.id;
            fields.push( field )
        }.bind(this));

        data.content = JSON.stringify( content );

        delete data.createTime;
        delete data.updateTime;
        delete data.elementType;

        this.app.restActions.saveView(data, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    }
});

MWF.xApplication.cms.ColumnManager.ViewExplorer.View = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.Explorer.Item,

    load_bak: function(){
        if( this.options.index % 2 == 0 ){
            this.itemNodeCss = this.explorer.css.itemNode_even
        }else{
            this.itemNodeCss = this.explorer.css.itemNode
        }
        this.node = new Element("div", {
            "styles": this.itemNodeCss,
            "events": {
                "click": function(e){this._open(e);e.stopPropagation();}.bind(this),
                "mouseover": function(){
                    this.node.setStyles( this.explorer.css.itemNode_over )
                }.bind(this),
                "mouseout": function(){
                    this.node.setStyles( this.itemNodeCss )
                }.bind(this)
            }
        }).inject(this.container,this.options.where);

        if (this.data.name.icon) this.icon = this.data.name.icon;
        var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;

        var itemIconNode = new Element("div", {
            "styles": this.explorer.css.itemIconNode
        }).inject(this.node);
        itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");
        //new Element("img", {
        //    "src": iconUrl, "border": "0"
        //}).inject(itemIconNode);

        itemIconNode.makeLnk({
            "par": this._getLnkPar()
        });

        this.actionsArea = new Element("div.actionsArea",{
            styles : this.explorer.css.actionsArea
        }).inject(this.node);
        if (!this.explorer.options.noDelete){
            this.deleteActionNode = new Element("div.deleteAction", {
                "styles": this.explorer.css.deleteAction
            }).inject(this.actionsArea);
            this.deleteActionNode.addEvent("click", function(e){
                this.deleteItem(e);
                e.stopPropagation();
            }.bind(this));
            this.deleteActionNode.addEvents({
                "mouseover" : function(ev){
                    this.deleteActionNode.setStyles( this.explorer.css.deleteAction_over )
                }.bind(this),
                "mouseout" : function(ev){
                    this.deleteActionNode.setStyles( this.explorer.css.deleteAction )
                }.bind(this)
            })
        }

        var inforNode = new Element("div.itemInforNode", {
            "styles": this.explorer.css.itemInforNode
        }).inject(this.node);
        var inforBaseNode = new Element("div.itemInforBaseNode", {
            "styles": this.explorer.css.itemInforBaseNode
        }).inject(inforNode);

        new Element("div.itemTextTitleNode", {
            "styles": this.explorer.css.itemTextTitleNode,
            "text": this.data.name,
            "title": this.data.name
        }).inject(inforBaseNode);

        new Element("div.itemTextAliasNode", {
            "styles": this.explorer.css.itemTextAliasNode,
            "text": this.data.alias,
            "title": this.data.alias
        }).inject(inforBaseNode);
        new Element("div.itemTextDateNode", {
            "styles": this.explorer.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(inforBaseNode);

        new Element("div.itemTextDescriptionNode", {
            "styles": this.explorer.css.itemTextDescriptionNode,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(inforBaseNode);

        this._customNodes();

        //this._isNew();
    },
    _customNodes: function(){},

    _open: function(e){
        var _self = this;
        var options = {
            "appId": "cms.ViewDesigner"+_self.data.id,
            "id": _self.data.id,
            "application":_self.explorer.app.options.column.id,
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.column = _self.explorer.app.options.column;
                this.application = _self.explorer.app.options.column;
                this.options.noModifyName = _self.explorer.options.noModifyName;
                this.options.readMode = _self.explorer.options.readMode,
                this.options.formId = _self.data.formId;
            }
        };
        this.explorer.app.desktop.openApplication(e, "cms.ViewDesigner", options);
    },
    _getIcon: function(){
        var x = (Math.random()*33).toInt();
        return "process_icon_"+x+".png";
    },
    _getLnkPar: function(){
        return {
            "icon": this.explorer.path+this.explorer.options.style+"/viewIcon/lnk.png",
            "title": this.data.name,
            "par": "cms.ViewDesigner#{\"id\": \""+this.data.id+"\", \"application\": "+JSON.stringify( this.explorer.app.options.application )+"}"
        };
    },
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.form.deleteFormTitle, this.explorer.app.lp.form.deleteForm, 320, 110, function(){
//			_self.deleteForm();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
    deleteView: function(callback){
        this.explorer.app.restActions.deleteView(this.data.id, function(){
            this.node.destroy();
            if (callback) callback();
        }.bind(this));
    },
    saveas: function(){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var app = this.explorer.app.options.application;
            app.name = app.appName;
            var selector = new MWF.O2Selector(this.explorer.app.content, {
                "title": this.explorer.app.lp.copyto,
                "type": "CMSApplication",
                "count" : 1,
                "values": [app],
                "onComplete": function(items){
                    items.each(function(item){
                        this.saveItemAs(item.data);
                    }.bind(this));
                }.bind(this)
            });
        }.bind(this));
    },
    saveItemAs: function(item){
        //var selectForm = function(){
        //    this.explorer.loadSelectFormDialog( function( e, form ){
        //        this._saveItemAs(item, form);
        //    }.bind(this), "请选择需粘贴视图的关联表单", item.id);
        //}.bind(this);
        //this.explorer.app.restActions.getForm(item.formId, function( json ){
        //    if( json && json.data && json.data.id  ){
        //        this._saveItemAs(item, form);
        //    }else{
        //        selectForm();
        //    }
        //}.bind(this), function(){
        //    selectForm();
        //}.bind(this));
        var text =  MWF.xApplication.cms.ColumnManager.LP.selectRelateFormNotice;
        this.explorer.loadSelectFormDialog( function( e, form ){
            this._saveItemAs(item, form);
        }.bind(this), text, item.id);
    },
    _saveItemAs: function(item, form){
        this.app = this.app || this.explorer.app;
        var id = item.id;
        var name = item.name || item.appName;
        this.explorer.app.restActions.getView(this.data.id, function(json){
            var data = json.data;
            var oldName = data.name;
            this.explorer.app.restActions.listView(id, function(dJson){
                dJson.data = dJson.data || [];
                var i=1;
                while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
                    data.name = oldName+"_copy"+i;
                    data.alias = oldName+"_copy"+i;
                    i++;
                }
                data.isNew = true;
                data.id = this.app.restActions.getUUID();
                data.application = id;
                data.applicationName = name;

                data.appId = id;
                data.appName = name;

                data.formId = form.id;

                var content = JSON.parse( data.content );
                content.application = data.application;
                content.applicationName = data.applicationName;
                content.relativeForm = form;
                content.id = data.id;
                content.name = data.name;
                content.alias = data.alias;

                var fields = [];
                content.columns.each( function( c ){
                    c.id = this.app.restActions.getUUID();
                    c.isNew = false;

                    var field = {};
                    field.id = c.id;
                    field.isNew = true;
                    field.viewId = data.id;
                    field.fieldTitle = c.title;
                    field.fieldName = c.value;
                    field.xshowSequence = form.id;
                    fields.push( field )
                }.bind(this));

                data.content = JSON.stringify( content );

                delete data.createTime;
                delete data.updateTime;
                delete data.elementType;

                this.explorer.app.restActions.saveView(data, function(){
                    if (id == this.explorer.app.options.application.id) this.explorer.reload();
                }.bind(this));

            }.bind(this));
        }.bind(this));
    }
});
