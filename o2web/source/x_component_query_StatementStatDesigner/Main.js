MWF.APPDSMSD = MWF.xApplication.query.StatementStatDesigner;
MWF.APPDSMSD.options = {
	"multitask": true,
	"executable": false
};
MWF.xDesktop.requireApp("query.StatementStatDesigner", "StatementStat", null, false);

MWF.xApplication.query.StatementStatDesigner.Main = new Class({
	Extends: MWF.xApplication.query.StatementDesigner.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "query.StatementStatDesigner",
		"icon": "icon.png",
		"title": MWF.APPDSMD.LP.title,
		"appTitle": MWF.APPDSMD.LP.title,
		"id": "",
        "tooltip": {
            "unCategory": MWF.APPDSMD.LP.unCategory
        },
		"actions": null,
		"category": null,
		"processData": null
	},

    loadStatementList: function(){
        this.actions.listStatement(this.application.id, function (json) {
            json.data.each(function(statement){
                this.createListStatementItem(statement);
            }.bind(this));
        }.bind(this), null, false);
    },
    //列示所有查询配置列表
    createListStatementItem: function(statementStat, isNew){
        var _self = this;
        var listStatementItem = new Element("div", {"styles": this.css.listStatementItem}).inject(this.statementListAreaNode, (isNew) ? "top": "bottom");
        var listStatementItemIcon = new Element("div", {"styles": this.css.listStatementItemIcon}).inject(listStatementItem);
        listStatementItemIcon.setStyle("background-img", "url(../x_component_query_StatementStatDesigner/$Main/default/statementStat.png)");
        var listStatementItemText = new Element("div", {"styles": this.css.listStatementItemText, "text": (statement.name) ? statement.name+" ("+statement.alias+")" : this.lp.newStatement}).inject(listStatementItem);

        listStatementItem.store("statementStat", statementStat);
        listStatementItem.addEvents({
            "click": function(e){_self.loadStatementByData(this, e);},
            "mouseover": function(){if (_self.currentListStatementItem!=this) this.setStyles(_self.css.listStatementItem_over);},
            "mouseout": function(){if (_self.currentListStatementItem!=this) this.setStyles(_self.css.listStatementItem);}
        });
    },
    //打开查询配置
    loadStatementByData: function(node, e){
        var statementStat = node.retrieve("statementStat");
        if (!statementStat.isNewStatement){
            var _self = this;
            var options = {
                "appId": "query.StatementStatDesigner"+statementStat.id,
                "id" : statementStat.id,
                // "application": _self.application.id,
                "application": {
                    "name": _self.application.name,
                    "id": _self.application.id,
                },
                "onQueryLoad": function(){
                    this.actions = _self.actions;
                    this.category = _self;
                    this.options.id = statement.id;
                    this.application = _self.application;
                    this.explorer = _self.explorer;
                }
            };
            this.desktop.openApplication(e, "query.StatementStatDesigner", options);
        }
    },


    loadContentToolbar: function(callback){
        this.getFormToolbarHTML(function(toolbarNode){
            var spans = toolbarNode.getElements("span");
            spans.each(function(item, idx){
                var img = item.get("MWFButtonImage");
                if (img){
                    item.set("MWFButtonImage", this.path+""+this.options.style+"/toolbar/"+img);
                }
            }.bind(this));

            $(toolbarNode).inject(this.contentToolbarNode);
            MWF.require("MWF.widget.Toolbar", function(){
                this.toolbar = new MWF.widget.Toolbar(toolbarNode, {"style": "ProcessCategory"}, this);
                this.toolbar.load();
                if (this.statementStat) if (this.statementStat.checkToolbars) this.statementStat.checkToolbars();
                if (callback) callback();
            }.bind(this));
        }.bind(this));
    },

    setDesignerStatementResize: function(){
        var size = this.designerContentNode.getSize();
        var contentHeight;
        debugger;
        if( this.statementStat && this.statementStat.selectMode && this.statementStat.selectMode.contains("view") ){
            this.designerContentResizeNode.show();
            this.designerStatementArea.show();

            var resizeNodeSize = this.designerContentResizeNode.getSize();
            var height = size.y-resizeNodeSize.y;

            var domHeight = this.designerStatementPercent*height;
            contentHeight = height-domHeight;

            this.designerStatementArea.setStyle("height", ""+domHeight+"px");
            this.designerContentArea.setStyle("height", ""+contentHeight+"px");
        }else{
            contentHeight = size.y;
            this.designerContentResizeNode.hide();
            this.designerStatementArea.hide();

            this.designerContentArea.setStyle("height", ""+contentHeight+"px");
        }

        if (this.statementStat){
            if (this.statementStat.currentSelectedModule){
                if (this.statementStat.currentSelectedModule.property){
                    var tab = this.statementStat.currentSelectedModule.property.propertyTab;
                    if (tab){
                        var tabTitleSize = tab.tabNodeContainer.getSize();

                        tab.pages.each(function(page){
                            var topMargin = page.contentNodeArea.getStyle("margin-top").toFloat();
                            var bottomMargin = page.contentNodeArea.getStyle("margin-bottom").toFloat();

                            var tabContentNodeAreaHeight = contentHeight - topMargin - bottomMargin - tabTitleSize.y.toFloat()-15;
                            page.contentNodeArea.setStyle("height", tabContentNodeAreaHeight);
                        }.bind(this));

                    }
                }
            }
        }
    },

	//loadStatement------------------------------------------
    loadStatement: function(callback){
	    debugger;
		this.getStatementData(this.options.id, function(vdata){
            this.setTitle(this.options.appTitle + "-"+vdata.name);
            if(this.taskitem)this.taskitem.setText(this.options.appTitle + "-"+vdata.name);
            this.options.appTitle = this.options.appTitle + "-"+vdata.name;
            this.statementStat = new MWF.xApplication.query.StatementStatDesigner.StatementStat(this, vdata);
			this.statementStat.load();
			if(callback)callback()
		}.bind(this));
	},
    getStatementData: function(id, callback){
        if (!this.options.id){
            this.loadNewStatementData(callback);
        }else{
            this.loadStatementData(id, callback);
        }
    },
    loadNewStatementData: function(callback){
        var url = "../x_component_query_StatementStatDesigner/$Main/statementStat.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.actions.getUUID(function(id){
                    obj.id=id;
                    obj.isNewStatement = true;
                    obj.application = this.application.id;
                    this.createListStatementItem(obj, true);
                    if (callback) callback(obj);
                }.bind(this));
            }.bind(this),
            "onerror": function(text){
                this.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function(xhr){
                this.notice(xhr.responseText, "error");
            }.bind(this)
        });
	},
    loadStatementData: function(id, callback){
		this.actions.getStatement(id, function(json){
			if (json){
				var data = json.data;
                if (!this.application){
                    this.actions.getApplication(data.query, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback(data);
                    }.bind(this));
                }else{
                    if (callback) callback(data);
                }
			}
		}.bind(this));
	},

    preview : function(){
        this.statementStat.preview();
    },

    saveStatement: function(){
        this.statementStat.save(function(){
            var name = this.statementStat.data.name;
            this.setTitle(MWF.APPDSMD.LP.title + "-"+name);
            this.options.desktopReload = true;
            this.options.id = this.statementStat.data.id;
        }.bind(this));
    },

    recordStatus: function(){
        //if (this.tab){
        var openViews = [];
        openViews.push(this.statementStat.data.id);
        var currentId = this.statementStat.data.id;
        var application = o2.typeOf(this.application) === "object" ? {
            name: this.application.name,
            id: this.application.id
        } : this.application;
        return {
            "id": this.options.id,
            "application": application,
            "openViews": openViews,
            "currentId": currentId
        };
    }


});