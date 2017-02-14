MWF.require("MWF.widget.Tree", null, false);
MWF.xApplication.Organization.OrgExplorer = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	
	initialize: function(node, actions, options){
		this.setOptions(options);
		
		this.path = "/x_component_Organization/$OrgExplorer/";
		this.cssPath = "/x_component_Organization/$OrgExplorer/"+this.options.style+"/css.wcss";
		this._loadCss();

        this.deleteElements = [];

		this.actions = actions;
		this.node = $(node);
	},
	load: function(){
        MWF.AC.getCompanyList();
		this.loadLayout();
		this.loadChart();
	},
	loadLayout: function(){
		this.chartAreaNode = new Element("div", {"styles": this.css.chartAreaNode}).inject(this.node);
		this.propertyAreaNode = new Element("div", {"styles": this.css.propertyAreaNode}).inject(this.node);
		
		this.resizeBarNode = new Element("div", {"styles": this.css.resizeBarNode}).inject(this.propertyAreaNode);
		this.propertyNode = new Element("div", {"styles": this.css.propertyNode}).inject(this.propertyAreaNode);
		
		this.propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(this.propertyNode);
		this.propertyContentNode = new Element("div", {"styles": this.css.propertyContentNode}).inject(this.propertyNode);
		
		this.loadToolbar();
		this.chartScrollNode = new Element("div", {"styles": this.css.chartScrollNode}).inject(this.chartAreaNode);
		this.chartNode = new Element("div", {"styles": this.css.chartNode}).inject(this.chartScrollNode);
		
		this.resizePropertyContentNode();
		this.app.addEvent("resize", function(){this.resizePropertyContentNode();}.bind(this));
		
		MWF.require("MWF.widget.ScrollBar", function(){
			new MWF.widget.ScrollBar(this.chartScrollNode, {
				"style":"xApp_Organization_Explorer", "where": "before", "distance": 20, "friction": 4,	"axis": {"x": false, "y": true},
                "onScroll": function(y){this.checkDeleteMasks();}.bind(this)
			});
			new MWF.widget.ScrollBar(this.propertyContentNode, {
				"style":"xApp_Organization_Explorer", "where": "before", "distance": 20, "friction": 4,	"axis": {"x": false, "y": true}
			});
		}.bind(this));
		
		this.propertyResize = new Drag(this.resizeBarNode,{
			"snap": 1,
			"onStart": function(el, e){
				var x = e.event.clientX;
				var y = e.event.clientY;
				el.store("position", {"x": x, "y": y});
				
				var size = this.chartAreaNode.getSize();
				el.store("initialWidth", size.x);
			}.bind(this),
			"onDrag": function(el, e){
				var x = e.event.clientX;
//				var y = e.event.y;
				var bodySize = this.node.getSize();
				var position = el.retrieve("position");
				var initialWidth = el.retrieve("initialWidth").toFloat();
				var dx = position.x.toFloat()-x.toFloat();
				
				var width = initialWidth-dx;
				if (width> bodySize.x/1.5) width = bodySize.x/1.5;
				if (width<400) width = 400;
				this.chartAreaNode.setStyle("width", width+1);
				this.propertyAreaNode.setStyle("margin-left", width);
			}.bind(this)
		});
	},
	resizePropertyContentNode: function(){
		var size = this.node.getSize();
		var tSize = this.propertyTitleNode.getSize();
		var mtt = this.propertyTitleNode.getStyle("margin-top").toFloat();
		var mbt = this.propertyTitleNode.getStyle("margin-bottom").toFloat();
		var mtc = this.propertyContentNode.getStyle("margin-top").toFloat();
		var mbc = this.propertyContentNode.getStyle("margin-bottom").toFloat();		
		var height = size.y-tSize.y-mtt-mbt-mtc-mbc;
		this.propertyContentNode.setStyle("height", height);
		
		tSize = this.toolbarNode.getSize();
		mtt = this.toolbarNode.getStyle("margin-top").toFloat();
		mbt = this.toolbarNode.getStyle("margin-bottom").toFloat();
		mtc = this.toolbarNode.getStyle("margin-top").toFloat();
		mbc = this.toolbarNode.getStyle("margin-bottom").toFloat();		
		height = size.y-tSize.y-mtt-mbt-mtc-mbc;
		this.chartScrollNode.setStyle("height", height);
	},
	
	loadToolbar: function(){
		this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.chartAreaNode);

        if (MWF.AC.isCompanyCreator()){
            this.addTopCompanyNode = new Element("div", {"styles": this.css.addTopCompanyNode}).inject(this.toolbarNode);
            this.addTopCompanyNode.addEvent("click", function(){
                this.addTopCompany();
            }.bind(this));
        }
		//this.createSearchNode();
	},
	createSearchNode: function(){
		this.searchNode = new Element("div", {"styles": this.css.searchNode}).inject(this.toolbarNode);
		
		this.searchButtonNode = new Element("div", {
			"styles": this.css.searchButtonNode,
			"title": this.app.lp.search
		}).inject(this.searchNode);
		
		this.searchInputAreaNode = new Element("div", {
			"styles": this.css.searchInputAreaNode
		}).inject(this.searchNode);
		
		this.searchInputBoxNode = new Element("div", {
			"styles": this.css.searchInputBoxNode
		}).inject(this.searchInputAreaNode);
		
		this.searchInputNode = new Element("input", {
			"type": "text",
			"value": this.app.lp.searchText,
			"styles": this.css.searchInputNode,
			"x-webkit-speech": "1"
		}).inject(this.searchInputBoxNode);
		var _self = this;
		this.searchInputNode.addEvents({
			"focus": function(){
				if (this.value==_self.app.lp.searchText) this.set("value", "");
			},
			"blur": function(){if (!this.value) this.set("value", _self.app.lp.searchText);},
			"keydown": function(e){
				if (e.code==13){
					this.searchOrg();
					e.preventDefault();
				}
			}.bind(this),
			"selectstart": function(e){
				e.preventDefault();
			}
		});
		this.searchButtonNode.addEvent("click", function(){this.searchOrg();}.bind(this));
	},
	addTopCompany: function(){
        var isNewElement = true;
        if (this.currentItem) isNewElement = this.currentItem.unSelected();
        if (isNewElement){
            var newElementData = this._getAddCompanyData();
            var item = this._newElement(newElementData, this);
            item.load();
            item.selected();
            item.editBaseInfor();

            (new Fx.Scroll(this.chartScrollNode)).toElementCenter(item.node);
        }else{
            this.app.notice(this.app.lp.organizationSave, "error", this.propertyContentNode);
        }
	},
    _newElement: function(data, explorer){
        return new MWF.xApplication.Organization.OrgExplorer.Company(data, explorer);
    },
    _getAddCompanyData: function(){
        return {
            "name": "",
            "superior": ""
        };
    },
    _getAddDepartmentData: function(){
        return {
            "name": "",
            "superior": "",
            "company": ""
        };
    },
	searchOrg: function(){
		alert("--- search ---");
	},
	loadChart: function(){
		this.actions.listTopCompany(function(json){
			this.loadChartContent(json.data, "Company", null);
		}.bind(this));
	},
	loadChartContent: function(data, type, pitem){
		data.each(function(itemData){
			var item = new MWF.xApplication.Organization.OrgExplorer[type](itemData, this);
			if (pitem) item.parentItem = pitem;
			item.load();
			
			if (type=="Company"){
				this.loadChildCompanyNodes(item);
				this.loadChildDepartmentNodes(item);
			}else{
				this.loadChildSubDepartmentNodes(item);
			}
			
		}.bind(this));
	},
	loadChildCompanyNodes: function(item, callback){
		this.actions.listSubCompany(function(json){
			this.loadChartContent(json.data, "Company", item);

		}.bind(this), null, item.data.id);
	},
	loadChildDepartmentNodes: function(item){
		this.actions.listDepartment(function(json){
			this.loadChartContent(json.data, "Department", item);
		}.bind(this), null, item.data.id);
	},
	loadChildSubDepartmentNodes: function(item){
		this.actions.listSubDepartment(function(json){
			this.loadChartContent(json.data, "Department", item);
		}.bind(this), null, item.data.id);
	},

    checkDeleteMasks: function(y) {
        this.deleteElements.each(function(item){
            var maskNode = this.chartScrollNode.getElementById("mask"+item.data.id);
            if (maskNode){
                maskNode.position({
                    relativeTo: item.childNode,
                    position: 'upperLeft',
                    edge: 'upperLeft'
                });
            }
        }.bind(this));
    },
    checkDeleteElements: function(){
        if (this.deleteElements.length){
            if (!this.deleteElementsNode){
                this.deleteElementsNode = new Element("div", {
                    "styles": this.css.deleteGroupsNode,
                    "text": this.app.lp.deleteOrganization
                }).inject(this.node);
                this.deleteElementsNode.position({
                    relativeTo: this.chartScrollNode,
                    position: "centerTop",
                    edge: "centerTop"
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
    checkDeleteElementsConfirm: function(){
        var confirmText = this.app.lp.deleteOrganizationConfirm;
        var size = {"x": 300, "y": 120};
        for (var i=0; i<this.deleteElements.length; i++){
            var item = this.deleteElements[i];

            if (item.childNode.getFirst()){
                confirmText = this.app.lp.deleteOrganizationSubConfirm;
                size = {"x": 450, "y": 230};
                break;
            }
            if (item.getDutyCount()>0){
                confirmText = this.app.lp.deleteOrganizationAllConfirm;
                size = {"x": 450, "y": 180};
                break;
            }
            if (item.getAttributeCount()>0){
                confirmText = this.app.lp.deleteOrganizationAllConfirm;
                size = {"x": 450, "y": 180};
                break;
            }
            if (item.getMemberCount()>0){
                confirmText = this.app.lp.deleteOrganizationAllConfirm;
                size = {"x": 450, "y": 180};
                break;
            }
        }
        return {"text": confirmText, "size": size};
    },
    deleteSelectedElements: function(e){
        var _self = this;
        confirm = this.checkDeleteElementsConfirm();
        this.app.confirm("infor", e, this.app.lp.deleteOrganizationTitle, {"html": confirm.text}, confirm.size.x, confirm.size.y, function(){
            var deleted = [];
            var doCount = 0;
            var readyCount = _self.deleteElements.length;
            var errorText = "";

            var complete = function(){
                if (doCount == readyCount){
                    if (errorText){
                        _self.app.notice(errorText, "error", _self.propertyContentNode, {x: "left", y:"top"});
                    }
                }
            }
            _self.deleteElements.each(function(item){
                item["delete"](function(){
                    deleted.push(item);
                    doCount++;
                    if (_self.deleteElements.length==doCount){
                        _self.deleteElements = _self.deleteElements.filter(function(item, index){
                            return !deleted.contains(item);
                        });
                        _self.checkDeleteElements();
                    }
                    complete();
                }, function(error){
                    errorText = (errorText) ? errorText+"<br/><br/>"+error : error;
                    doCount++;
                    if (_self.deleteElements.length==doCount){
                        _self.deleteElements = _self.deleteElements.filter(function(item, index){
                            return !deleted.contains(item);
                        });
                        _self.checkDeleteElements();
                    }
                    complete();
                });
            });
            this.close();
        }, function(){
            this.close();
        });
    },
});

MWF.xApplication.Organization.OrgExplorer.Item = new Class({
	initialize: function(data, explorer){
		this.data = data;
		this.explorer = explorer;
		this.chartNode = this.explorer.chartNode;
		
		this.prevItem = null;
		this.nextItem = null;
		this.parentItem = null;
		this.children = [];
		this.selectedDutys = [];
		this.selectedAttributes = [];
        this.isEdit = false;
        this.isEditor = false;

        this.deleteSelected = false;
		
		this.initStyle();
	}, initStyle: function(){
		this.style = this.explorer.css.companyItem;
	},
	load: function(){
		this.node = new Element("div", {"styles": this.style.node}).inject(this.chartNode);

		this.contentNode = new Element("div", {"styles": this.style.contentNode}).inject(this.node);
		this.childNode = new Element("div", {"styles": this.style.childNode}).inject(this.node);

        this.childTween = new Fx.Tween(this.childNode, {
            "duration": 200,
            "transition": Fx.Transitions.Quint.easeOut,
            "onComplete": function(){
                var y = this.childNode.getSize().y;
                if (y>0)this.childNode.setStyle("height", "auto");
            }.bind(this)
        });
		this.flagNode = new Element("div", {"styles": this.style.flagNode}).inject(this.contentNode);
        this.flagIconNode = new Element("div", {"styles": this.style.flagIconNode}).inject(this.flagNode);

		this.iconNode = new Element("div", {"styles": this.style.iconNode}).inject(this.contentNode);
		this.actionNode = new Element("div", {"styles": this.style.actionNode}).inject(this.contentNode);
		
		this.textNode = new Element("div", {"styles": this.style.textNode}).inject(this.contentNode);
		this.textNode.set({
			"text": this.data.name
		});
//		var indent = this.getIndent();
//		this.indentNode.setStyle("width", ""+indent+"px");

        this.setNewItem();

		if (this.parentItem){
            this.parentItem.children.push(this);
            this.parentItem.flagIconNode.setStyles(this.style.flagIconNode_e);
			this.node.inject(this.parentItem.childNode);
		}else{
			this.node.inject(this.chartNode);
		}
		this.addActions();
		this.setEvent();
	},
    setNewItem: function(){
        if (!this.created){
            if (!this.data.id){
                this.created = false;
                this.contentNode.setStyles(this.style.contentNodeNew);
            }else {
                this.created = true;
                this.contentNode.setStyles(this.style.contentNode);
            }
        }
    },

	getIndent: function(){
		return (this.data.level.toFloat()-1)*20;
	},
	addActions: function(){
        MWF.AC.isCompanyEditor({
            "id": this.data.id,
            "yes": function(){
                this.isEditor = true;
                this.deleteNode = new Element("div", {"styles": this.style.actionDeleteNode}).inject(this.actionNode);
                this.addDepartmentNode = new Element("div", {"styles": this.style.actionAddDepartmentNode, "title": this.explorer.app.lp.createSubDepartment}).inject(this.actionNode);
                this.addCompanyNode = new Element("div", {"styles": this.style.actionAddCompanyNode, "title": this.explorer.app.lp.createSubCompany}).inject(this.actionNode);
                this.actionConfigNode = new Element("div", {"styles": this.style.actionConfigNode, "title": this.explorer.app.lp.configCompany}).inject(this.actionNode);

                this.deleteNode.addEvent("click", function(e){
                    this.deleteButton();
                    e.stopPropagation();
                }.bind(this));

                this.addDepartmentNode.addEvent("click", function(e){
                    if (!this.checkDelete()) if (this.data.id) this.addDepartment();
                    e.stopPropagation();
                }.bind(this));
                this.addCompanyNode.addEvent("click", function(e){
                    if (!this.checkDelete()) if (this.data.id) this.addCompany();
                    e.stopPropagation();
                }.bind(this));

                this.actionConfigNode.addEvent("click", function(e){
                    if (!this.checkDelete()) if (this.data.id) this.configCompany(e);
                    e.stopPropagation();
                }.bind(this));
            }.bind(this)
        });
	},
    deleteButton: function(){
        if (!this.deleteSelected){
            this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
            this.contentNode.setStyles(this.style.contentNode_selected);
            this.node.setStyles(this.style.node_forDelete);
            //this.node.setStyles(this.style.node_forDelete);
            if (this.addDepartmentNode) this.addDepartmentNode.setStyle("display", "none");
            if (this.addCompanyNode) this.addCompanyNode.setStyle("display", "none");
            if (this.actionConfigNode) this.actionConfigNode.setStyle("display", "none");

            //this.childNode.mask({
            //    "id": "mask"+this.data.id,
            //    "inject": this.node,
            //    "style": this.style.nodeMask
            //});
            var removeItems = [];
            this.explorer.deleteElements.each(function(item){
                if (this.childNode.contains(item.node)){
                    item.deleteNode.setStyles(item.style.actionDeleteNode);
                    item.contentNode.setStyles(item.style.contentNode);
                    item.node.setStyles(this.style.node);
                    if (item.addDepartmentNode) item.addDepartmentNode.setStyle("display", "block");
                    if (item.addCompanyNode) item.addCompanyNode.setStyle("display", "block");
                    if (item.actionConfigNode) item.actionConfigNode.setStyle("display", "block");
                    item.actionNode.setStyle("opacity", "0");
   //               item.childNode.unmask();
                    item.deleteSelected = false;
                    removeItems.push(item);
                }
            }.bind(this));
            removeItems.each(function(item){
                this.explorer.deleteElements.erase(item);
            }.bind(this));

            this.explorer.deleteElements.push(this);
            this.deleteSelected = true;

            this.explorer.checkDeleteElements();
        }else{
            this.deleteNode.setStyles(this.style.actionDeleteNode);
            this.contentNode.setStyles(this.style.contentNode);
            this.node.setStyles(this.style.node);
            if (this.addDepartmentNode) this.addDepartmentNode.setStyle("display", "block");
            if (this.addCompanyNode) this.addCompanyNode.setStyle("display", "block");
            if (this.actionConfigNode) this.actionConfigNode.setStyle("display", "block");
  //          this.actionNode.setStyle("opacity", "0");
  //          this.childNode.unmask();

            this.explorer.deleteElements.erase(this);
            this.deleteSelected = false;
            this.explorer.checkDeleteElements();
        }

    },
    addCompany: function(){
        var isNewElement = true;
        if (this.explorer.currentItem) isNewElement = this.explorer.currentItem.unSelected();
        if (isNewElement){
            var newElementData = this.explorer._getAddCompanyData();
            newElementData.superior = this.data.id;
            var item = new MWF.xApplication.Organization.OrgExplorer.Company(newElementData, this.explorer);
            item.parentItem = this;
            item.load();
            item.selected();
            item.editBaseInfor();

            (new Fx.Scroll(this.explorer.chartScrollNode)).toElementCenter(item.node);
        }else{
            this.app.notice(this.explorer.app.lp.organizationSave, "error", this.propertyContentNode);
        }
    },
    addDepartment: function(){
        var isNewElement = true;
        if (this.explorer.currentItem) isNewElement = this.explorer.currentItem.unSelected();
        if (isNewElement){
            var newElementData = this.explorer._getAddDepartmentData();
            newElementData.company = this.data.id;
            var item = new MWF.xApplication.Organization.OrgExplorer.Department(newElementData, this.explorer);
            item.parentItem = this;
            item.load();
            item.selected();
            item.editBaseInfor();

            (new Fx.Scroll(this.explorer.chartScrollNode)).toElementCenter(item.node);
        }else{
            this.app.notice(this.explorer.app.lp.organizationSave, "error", this.propertyContentNode);
        }
    },
    configCompany: function(e) {
        var controllerList = this.data.controllerList || [];
        var options = {
            "title": this.explorer.app.lp.configCompany,
            "type": "person",
            "values": controllerList,
            "onComplete": function(items){
                var ids = [];
                items.each(function(item){
                    ids.push(item.data.id);
                });
                if (!ids.length){
                    var _self = this;
                    this.explorer.app.confirm("warn", e, this.explorer.app.lp.configCompanyNullTitle, {"html": this.explorer.app.lp.configCompanyNull}, 400, 160, function(){
                        _self.data.controllerList = ids;
                        _self.explorer.actions.saveCompany(_self.data, function(json){
                            this.explorer.app.notice(this.explorer.app.lp.configCompanyOk, "success");
                        }.bind(_self));
                        this.close();
                    }, function(){
                        this.close();
                    });
                }else{
                    this.data.controllerList = ids;
                    this.explorer.actions.saveCompany(this.data, function(json){
                        this.explorer.app.notice(this.explorer.app.lp.configCompanyOk, "success");
                    }.bind(this));
                }
            }.bind(this)
        }
        var selector = new MWF.OrgSelector(this.explorer.app.content, options)
    },
    checkDelete: function(){
        //if (this.deleteSelected) reutrn true;
        var item = this;
        while (item){
            if (item.deleteSelected) return true;
            var item = item.parentItem
        }
        return false;
    },
	setEvent: function(){
//		this.node.addEvents({
//			"mouseover": function(e){
//				this.node.setStyles(this.style.nodeOver);
//				e.stopPropagation();
//			}.bind(this),
//			"mouseout": function(e){
//				this.node.setStyles(this.style.node);
//			}.bind(this)
//		});
		this.contentNode.addEvents({
			"mouseover": function(e){
				if (this.explorer.currentItem!=this){
					this.flagNode.setStyles(this.style.flagNodeOver);
				}
                if (!this.checkDelete()) if (this.data.id) this.actionNode.fade("in");
			}.bind(this),
			"mouseout": function(e){
				if (this.explorer.currentItem!=this){
					this.flagNode.setStyles(this.style.flagNode);
				}
                if (!this.checkDelete()) if (this.data.id) this.actionNode.fade("out");
			}.bind(this),
			"click": function(e){
				this.itemClick();
			}.bind(this)
		});
        this.flagNode.addEvents({
            "click": function(e){

                if (!this.childTween.isRunning()){
                    var oy = this.childNode.retrieve("c-height", 0);
                    var cy = this.childNode.getSize().y;
                    if (cy>0){
                        this.childNode.store("c-height", cy);
                        this.childTween.start("height", cy, 0);
                        this.flagIconNode.setStyles(this.style.flagIconNode_c);
                    }else{
                        this.childTween.start("height", 0, oy);
                        this.flagIconNode.setStyles(this.style.flagIconNode_e);
                    }
                }
            }.bind(this)
        });
	},
    itemClick: function(){
        if (this.explorer.currentItem){
            if (this.explorer.currentItem.unSelected()){
                this.selected();
            }else{
                this.explorer.app.notice(this.explorer.app.lp.organizationSave, "error", this.propertyContentNode);
            }
        }else{
            this.selected();
        }
    },
	selected: function(){
		this.explorer.currentItem = this;
		this.flagNode.setStyles(this.style.flagNodeSelected);
		this.showItemProperty();
	},
	unSelected: function(){
        if (this.isEdit) return false;

		this.explorer.currentItem = null;
		this.flagNode.setStyles(this.style.flagNode);
		this.clearItemProperty();

        return true;
	},
	showItemProperty: function(){
		this.explorer.propertyTitleNode.set("text", this.data.name);
		this.showItemPropertyBase();
		this.showItemPropertyDuty();
		this.showItemPropertyAttribute();
	},
	showItemPropertyBase: function(){
		this.propertyBaseNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.baseActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyBaseNode);
		this.propertyBaseTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.app.lp.companyBaseText
		}).inject(this.propertyBaseNode);

        if (this.isEditor) this.createEditBaseNode();
		
		this.propertyBaseContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyBaseNode);
		
		var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center'>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.companyName+"</td><td id='formCompanyName'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.companyNumber+"</td><td id='formCompanyNumber'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.companyAddress+"</td><td id='formCompanyAddress'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.companyShortname+"</td><td id='formCompanyShortname'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.orderNumber+"</td><td id='formOrderNumber'></td><tr>";
		html += "</table>";
		
		this.propertyBaseContentNode.set("html", html);
//		this.propertyBaseContentNode.getElements("td").setStyles({
//			"border-bottom": "1px solid #c9c9c9"
//		});
		this.propertyBaseContentNode.getElements("td.formTitle").setStyles(this.style.propertyBaseContentTdTitle);
		
		this.companyNameInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formCompanyName"), this.data.name, this.explorer.css.formInput);
		this.companyNumberInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formCompanyNumber"), this.data.number, this.explorer.css.formInput);
		this.companyAddressInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formCompanyAddress"), this.data.address, this.explorer.css.formInput);
		this.companyShortnameInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formCompanyShortname"), this.data.shortname, this.explorer.css.formInput);
		this.orderNumberInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formOrderNumber"), this.data.orderNumber, this.explorer.css.formInput);;
	},
	createEditBaseNode: function(){
		this.editBaseNode = new Element("button", {
			"styles": this.style.editBaseNode,
			"text": this.explorer.app.lp.edit,
			"events": {"click": this.editBaseInfor.bind(this)}
		}).inject(this.baseActionNode);
	},
	createCancelBaseNode: function(){
		this.cancelBaseNode = new Element("button", {
			"styles": this.style.cancelBaseNode,
			"text": this.explorer.app.lp.cancel,
			"events": {"click": this.cancelBaseInfor.bind(this)}
		}).inject(this.baseActionNode);
	},
	createSaveBaseNode: function(){
		this.saveBaseNode = new Element("button", {
			"styles": this.style.saveBaseNode,
			"text": this.explorer.app.lp.save,
			"events": {"click": this.saveBaseInfor.bind(this)}
		}).inject(this.baseActionNode);
	},
	editBaseInfor: function(){
		this.baseActionNode.empty();
		this.editBaseNode = null;
		this.createCancelBaseNode();
		this.createSaveBaseNode();
		
		this.editMode();
	},
	cancelBaseInfor: function(){
        if (this.data.name){
            this.baseActionNode.empty();
            this.cancelBaseNode = null;
            this.saveBaseNode = null;
            this.createEditBaseNode();

            this.readMode();
        }else{
            this.destroy();
        }
	},
    destroy: function(){
        if (this.parentItem) this.parentItem.children.erase(this);
        this.explorer.currentItem = null;
        this.clearItemProperty();
        this.node.destroy();
        delete this;
    },
	saveBaseInfor: function(callback){
        if (!this.companyNameInput.input.get("value")){
            this.explorer.app.notice(this.explorer.app.lp.inputOrganizationInfor, "error", this.explorer.propertyContentNode);
            return false;
        }
		this.propertyBaseNode.mask({
			"style": {
				"opacity": 0.7,
				"background-color": "#999"
			}
		});
		this.save(function(){
			this.baseActionNode.empty();
			this.cancelBaseNode = null;
			this.saveBaseNode = null;
			this.createEditBaseNode();
			
			this.readMode();

            this.setNewItem();
			this.propertyBaseNode.unmask();

            if (callback) callback();
		}.bind(this), function(xhr, text, error){
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.explorer.app.notice("request json error: "+errorText, "error");
			this.propertyBaseNode.unmask();
		}.bind(this));
	},
	
	clearItemProperty: function(){
		this.explorer.propertyTitleNode.empty();
		this.explorer.propertyContentNode.empty();
	},
	editMode: function(){
		this.companyNameInput.editMode();
		this.companyNumberInput.editMode();
		this.companyAddressInput.editMode();
		this.companyShortnameInput.editMode();
		this.orderNumberInput.editMode();

        this.isEdit = true;
	},
	readMode: function(){
		this.companyNameInput.readMode();
		this.companyNumberInput.readMode();
		this.companyAddressInput.readMode();
		this.companyShortnameInput.readMode();
		this.orderNumberInput.readMode();
        this.isEdit = false;
	},
	save: function(callback, cancel){
		this.data.name = this.companyNameInput.save();
		this.data.number = this.companyNumberInput.save();
		this.data.address = this.companyAddressInput.save();
		this.data.shortname = this.companyShortnameInput.save();
		this.data.orderNumber = this.orderNumberInput.save();
		
		this.explorer.actions.saveCompany(this.data, function(json){
            if (!this.data.id) this.data.id = json.data.id;
			this.textNode.set("text", this.data.name);

			if (callback) callback();
		}.bind(this), function(xhr, text, error){
			if (cancel) cancel(xhr, text, error);
		}.bind(this));
	},
	
	showItemPropertyDuty: function(){
		this.propertyDutyNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.dutyActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyDutyNode);
		this.propertyDutyTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.app.lp.companyDutyText
		}).inject(this.propertyDutyNode);
	//	this.createEditBaseNode();
		
		this.propertyDutyContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyDutyNode);

        if (this.isEditor) {
            this.createDeleteDutyNode();
            this.createAddDutyNode();
        }
		this.listDuty();
	},
	createAddDutyNode: function(){
		this.addDutyNode = new Element("button", {
			"styles": this.style.addDutyNode,
			"text": this.explorer.app.lp.add,
			"events": {"click": this.addDuty.bind(this)}
		}).inject(this.dutyActionNode);
	},
	createDeleteDutyNode: function(){
		this.deleteDutyNode = new Element("button", {
			"styles": this.style.deleteDutyNode_desable,
			"text": this.explorer.app.lp["delete"],
			"disable": true
		}).inject(this.dutyActionNode);
	},
	addDuty: function(){
		var data = this.getNewDutyData();
        if (!this.created){
            this.saveBaseInfor(function(){
                new MWF.xApplication.Organization.CompanyDuty(this.propertyDutyContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
            }.bind(this));
        }else{
            new MWF.xApplication.Organization.CompanyDuty(this.propertyDutyContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
        }
	},
	getNewDutyData: function(){
		return {
			"company": this.data.id,
			"name": "",
		};
	},
	checkDeleteDutyAction: function(){

		if (this.selectedDutys.length){
			if (this.deleteDutyNode.get("disable")){
				this.deleteDutyNode.set({
					"styles": this.style.deleteDutyNode
				});
				this.deleteDutyNode.removeProperty("disable");
				this.deleteDutyNode.addEvent("click", function(e){this.deleteDuty(e);}.bind(this));
			}
		}else{
			if (!this.deleteDutyNode.get("disable")){
				this.deleteDutyNode.set({
					"styles": this.style.deleteDutyNode_desable,
					"disable": true
				});
				this.deleteDutyNode.removeEvents("click");
			}
		}
	},
	deleteDuty: function(e){
		var _self = this;
		this.explorer.app.confirm("infor", e, this.explorer.app.lp.deleteDutyTitle, this.explorer.app.lp.deleteDuty, 300, 120, function(){
			this.close();
			_self.selectedDutys.each(function(duty){
				duty.remove();
			});
			delete _self.selectedDutys;
			_self.selectedDutys = [];
			_self.checkDeleteDutyAction();
		}, function(){this.close();});
	},
	
	listDuty: function(){
		var html = "<table cellspacing='0' cellpadding='5' border='0' width='95%' align='center' style='line-height:normal'>";
		html += "<tr><th style='width:20px'></th>";
		html += "<th style='width: 30%; border-right: 1px solid #FFF'>"+this.explorer.app.lp.dutyName+"</th>";
		html += "<th>"+this.explorer.app.lp.dutyMember+"</th><th style='width:20px'></th></tr>";
		html += "</table>";
		this.propertyDutyContentNode.set("html", html);
		this.propertyDutyContentNode.getElements("th").setStyles(this.style.propertyDutyContentTdTitle);

        if (this.data.id){
            this.explorer.actions.listCompanyDuty(function(json){
                json.data.each(function(item){
                    new MWF.xApplication.Organization.CompanyDuty(this.propertyDutyContentNode.getElement("table").getFirst(), item, this, this.explorer.css.map);
                }.bind(this));
            }.bind(this), null, this.data.id);
        }
	},

    getDutyCount: function(){
        if (this.data.id){
            var count = 0;
            this.explorer.actions.listCompanyDuty(function(json){
                count = json.data.length;
            }.bind(this), null, this.data.id, false);
            return count;
        }
        return 0;
    },
	
	
	showItemPropertyAttribute: function(){
		this.propertyAttributeNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.attributeActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyAttributeNode);
		this.propertyAttributeTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.app.lp.companyAttributeText
		}).inject(this.propertyAttributeNode);
	//	this.createEditBaseNode();
		
		this.propertyAttributeContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyAttributeNode);

        if (this.isEditor) {
            this.createDeleteAttributeNode();
            this.createAddAttributeNode();
        }
		this.listAttribute();
	},
	createAddAttributeNode: function(){
		this.addAttributeNode = new Element("button", {
			"styles": this.style.addDutyNode,
			"text": this.explorer.app.lp.add,
			"events": {"click": this.addAttribute.bind(this)}
		}).inject(this.attributeActionNode);
	},
	createDeleteAttributeNode: function(){
		this.deleteAttributeNode = new Element("button", {
			"styles": this.style.deleteDutyNode_desable,
			"text": this.explorer.app.lp["delete"],
			"disable": true
		}).inject(this.attributeActionNode);
	},
	addAttribute: function(){
		var data = this.getNewAttributeData();
        if (!this.created){
            this.saveBaseInfor(function(){
                new MWF.xApplication.Organization.CompanyAttribute(this.propertyAttributeContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
            }.bind(this));
        }else{
            new MWF.xApplication.Organization.CompanyAttribute(this.propertyAttributeContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
        }

	},
	getNewAttributeData: function(){
		return {
			"company": this.data.id,
			"name": "",
			"attributeList":[]
		};
	},
	checkDeleteAttributeAction: function(){

		if (this.selectedAttributes.length){
			if (this.deleteAttributeNode.get("disable")){
				this.deleteAttributeNode.set({
					"styles": this.style.deleteDutyNode
				});
				this.deleteAttributeNode.removeProperty("disable");
				this.deleteAttributeNode.addEvent("click", function(e){this.deleteAttribute(e);}.bind(this));
			}
		}else{
			if (!this.deleteAttributeNode.get("disable")){
				this.deleteAttributeNode.set({
					"styles": this.style.deleteDutyNode_desable,
					"disable": true
				});
				this.deleteAttributeNode.removeEvents("click");
			}
		}
	},
	deleteAttribute: function(e){
		var _self = this;
		this.explorer.app.confirm("infor", e, this.explorer.app.lp.deleteAttributeTitle, this.explorer.app.lp.deleteAttribute, 300, 120, function(){
			this.close();
			_self.selectedAttributes.each(function(attribute){
				attribute.remove();
			});
			delete _self.selectedAttributes;
			_self.selectedAttribute = [];
			_self.checkDeleteAttributeAction();
		}, function(){this.close();});
	},
	listAttribute: function(){
		var html = "<table cellspacing='0' cellpadding='5' border='0' width='95%' align='center' style='line-height:normal'>";
		html += "<tr><th style='width:20px'></th>";
		html += "<th style='width: 30%; border-right: 1px solid #FFF'>"+this.explorer.app.lp.attributeName+"</th>";
		html += "<th>"+this.explorer.app.lp.attributeValue+"</th><th style='width:20px'></th></tr>";
		html += "</table>";
		this.propertyAttributeContentNode.set("html", html);
		this.propertyAttributeContentNode.getElements("th").setStyles(this.style.propertyDutyContentTdTitle);

        if (this.data.id){
            this.explorer.actions.listCompanyAttribute(function(json){
                json.data.each(function(item){
                    new MWF.xApplication.Organization.CompanyAttribute(this.propertyAttributeContentNode.getElement("table").getFirst(), item, this, this.explorer.css.map);
                }.bind(this));
            }.bind(this), null, this.data.id);
        }
	},
    getAttributeCount: function(){
        if (this.data.id){
            var count = 0;
            this.explorer.actions.listCompanyAttribute(function(json){
                count = json.data.length;
            }.bind(this), null, this.data.id, false);
            return count;
        }
        return 0;
    },
    getMemberCount: function(){return 0;},

    "delete": function(success, failure){

        this.children.each(function(item){
            item["delete"]();
        });

        this._deleteDutys();
        this._deleteAttributes();
        this._deleteMembers();

        this._deleteItem(this.data.id, function(){
            this.destroy();
            if (success) success();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            if (failure) failure(errorText);
        }.bind(this));
    },

    _deleteItem: function(id, success, failure){
        this.explorer.actions.deleteCompany(id, success, failure, false);
    },

    _deleteDutys: function(){
        this._listDutys(function(json){
            json.data.each(function(duty){
                this.explorer.actions.deleteCompanyDuty(duty.id, null, null, false);
            }.bind(this));
        }.bind(this));
    },
    _listDutys: function(success){
        this.explorer.actions.listCompanyDuty(function(json){
            if (success) success(json);
        }.bind(this), null, this.data.id, false);
    },
    _deleteAttributes: function(){
        this._listAttributes(function(json){
            json.data.each(function(att){
                this.explorer.actions.deleteCompanyAttribute(att.id, null, null, false);
            }.bind(this));
        }.bind(this));
    },
    _listAttributes: function(success){
        this.explorer.actions.listCompanyAttribute(function(json){
            if (success) success(json);
        }.bind(this), null, this.data.id, false);
    },
    _deleteMembers: function(){
        return true;
    }

	
});
MWF.xApplication.Organization.OrgExplorer.Company = new Class({
	Extends: MWF.xApplication.Organization.OrgExplorer.Item
});
MWF.xApplication.Organization.OrgExplorer.Department = new Class({
	Extends: MWF.xApplication.Organization.OrgExplorer.Item,
	initStyle: function(){
		this.style = this.explorer.css.departmentItem;
	},

    addActions: function(){
        MWF.AC.isDepartmentEditor({
            "id": this.data.company,
            "yes": function(){
                this.isEditor = true;
                this.deleteNode = new Element("div", {"styles": this.style.actionDeleteNode}).inject(this.actionNode);
                this.addDepartmentNode = new Element("div", {"styles": this.style.actionAddDepartmentNode, "title": this.explorer.app.lp.createSubDepartment}).inject(this.actionNode);

                this.deleteNode.addEvent("click", function(e){
                    this.deleteButton();
                    e.stopPropagation();
                }.bind(this));

                this.addDepartmentNode.addEvent("click", function(e){
                    if (!this.checkDelete()) if (this.data.id) this.addDepartment();
                    e.stopPropagation();
                }.bind(this));
            }.bind(this)
        });
    },
    addDepartment: function(){
        var isNewElement = true;
        if (this.explorer.currentItem) isNewElement = this.explorer.currentItem.unSelected();
        if (isNewElement){
            var newElementData = this.explorer._getAddDepartmentData();
            newElementData.superior = this.data.id;
            var item = new MWF.xApplication.Organization.OrgExplorer.Department(newElementData, this.explorer);
            item.parentItem = this;
            item.load();
            item.selected();
            item.editBaseInfor();

            (new Fx.Scroll(this.explorer.chartScrollNode)).toElementCenter(item.node, "y");
        }else{
            this.app.notice(this.explorer.app.lp.organizationSave, "error", this.propertyContentNode);
        }
    },

    saveBaseInfor: function(callback){
        if (!this.departmentNameInput.input.get("value")){
            this.explorer.app.notice(this.explorer.app.lp.inputOrganizationInfor, "error", this.explorer.propertyContentNode);
            return false;
        }
        this.propertyBaseNode.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });
        this.save(function(){
            this.baseActionNode.empty();
            this.cancelBaseNode = null;
            this.saveBaseNode = null;
            this.createEditBaseNode();

            this.readMode();
            this.setNewItem();

            this.propertyBaseNode.unmask();
            if (callback) callback();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.explorer.app.notice("request json error: "+errorText, "error");
            this.propertyBaseNode.unmask();
        }.bind(this));
    },

	showItemProperty: function(){
		this.explorer.propertyTitleNode.set("text", this.data.name);
		this.showItemPropertyBase();
		this.showItemPropertyDuty();
		this.showItemPropertyAttribute();
		
		this.showItemMembers();
	},
	
	showItemPropertyBase: function(){
		this.propertyBaseNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.baseActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyBaseNode);
		this.propertyBaseTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.app.lp.departmentBaseText
		}).inject(this.propertyBaseNode);

        if (this.isEditor) this.createEditBaseNode();
		
		this.propertyBaseContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyBaseNode);
		
		var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center'>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.departmentName+"</td><td id='formDepartmentName'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.departmentNumber+"</td><td id='formDepartmentNumber'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.departmentShortname+"</td><td id='formDepartmentShortname'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.orderNumber+"</td><td id='formOrderNumber'></td></tr>";
		html += "</table>";
		
		this.propertyBaseContentNode.set("html", html);
//		this.propertyBaseContentNode.getElements("td").setStyles({
//			"border-bottom": "1px solid #c9c9c9"
//		});
		this.propertyBaseContentNode.getElements("td.formTitle").setStyles(this.style.propertyBaseContentTdTitle);
		
		this.departmentNameInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formDepartmentName"), this.data.name, this.explorer.css.formInput);
		this.departmentNumberInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formDepartmentNumber"), this.data.number, this.explorer.css.formInput);
		this.departmentShortnameInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formDepartmentShortname"), this.data.shortname, this.explorer.css.formInput);
		this.orderNumberInput = new MWF.xApplication.Organization.Input(this.propertyBaseContentNode.getElement("#formOrderNumber"), this.data.orderNumber, this.explorer.css.formInput);;
	},
	editMode: function(){
		this.departmentNameInput.editMode();
		this.departmentNumberInput.editMode();
		this.departmentShortnameInput.editMode();
		this.orderNumberInput.editMode();
        this.isEdit = true;
	},
	readMode: function(){
		this.departmentNameInput.readMode();
		this.departmentNumberInput.readMode();
		this.departmentShortnameInput.readMode();
		this.orderNumberInput.readMode();
        this.isEdit = false;
	},
	save: function(callback){
		this.data.name = this.departmentNameInput.save();
		this.data.number = this.departmentNumberInput.save();
		this.data.shortname = this.departmentShortnameInput.save();
		this.data.orderNumber = this.orderNumberInput.save();
		
		this.explorer.actions.saveDepartment(this.data, function(json){
            if (!this.data.id) this.data.id = json.data.id;
			this.textNode.set("text", this.data.name);
			if (callback) callback();
		}.bind(this));
	},
	getNewDutyData: function(){
		return {
			"department": this.data.id,
			"name": ""
		};
	},
	getNewAttributeData: function(){
		return {
			"department": this.data.id,
			"name": "",
			"attributeList":[]
		};
	},
	listDuty: function(){
		this.propertyDutyTextNode.set("text", this.explorer.app.lp.departmentDutyText);
		var html = "<table cellspacing='0' cellpadding='5' border='0' width='95%' align='center' style='line-height:normal'>";
		html += "<tr><th style='width:20px'></th>";
		html += "<th style='width: 30%; border-right: 1px solid #FFF'>"+this.explorer.app.lp.dutyName+"</th>";
		html += "<th>"+this.explorer.app.lp.dutyMember+"</th><th style='width:20px'></th></tr>";
		html += "</table>";
		this.propertyDutyContentNode.set("html", html);
		this.propertyDutyContentNode.getElements("th").setStyles(this.style.propertyDutyContentTdTitle);

        if (this.data.id){
            this.explorer.actions.listDepartmentDuty(function(json){
                json.data.each(function(item){
                    new MWF.xApplication.Organization.DepartmentDuty(this.propertyDutyContentNode.getElement("table").getFirst(), item, this, this.explorer.css.map);
                }.bind(this));
            }.bind(this), null, this.data.id);
        }
	},
    getDutyCount: function(){
        if (this.data.id){
            var count = 0;
            this.explorer.actions.listDepartmentDuty(function(json){
                count = json.data.length;
            }.bind(this), null, this.data.id, false);
            return count;
        }
        return 0;
    },
	addDuty: function(){
		var data = this.getNewDutyData();
        if (!this.created){
            this.saveBaseInfor(function(){
                new MWF.xApplication.Organization.DepartmentDuty(this.propertyDutyContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
            }.bind(this));
        }else{
            new MWF.xApplication.Organization.DepartmentDuty(this.propertyDutyContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
        }
	},
	listAttribute: function(){
		this.propertyAttributeTextNode.set("text", this.explorer.app.lp.departmentAttributeText);
		var html = "<table cellspacing='0' cellpadding='5' border='0' width='95%' align='center' style='line-height:normal'>";
		html += "<tr><th style='width:20px'></th>";
		html += "<th style='width: 30%; border-right: 1px solid #FFF'>"+this.explorer.app.lp.attributeName+"</th>";
		html += "<th>"+this.explorer.app.lp.attributeValue+"</th><th style='width:20px'></th></tr>";
		html += "</table>";
		this.propertyAttributeContentNode.set("html", html);
		this.propertyAttributeContentNode.getElements("th").setStyles(this.style.propertyDutyContentTdTitle);

        if (this.data.id){
            this.explorer.actions.listDepartmentAttribute(function(json){
                json.data.each(function(item){
                    new MWF.xApplication.Organization.DepartmentAttribute(this.propertyAttributeContentNode.getElement("table").getFirst(), item, this, this.explorer.css.map);
                }.bind(this));
            }.bind(this), null, this.data.id);
        }
	},
    getAttributeCount: function(){
        if (this.data.id){
            var count = 0;
            this.explorer.actions.listDepartmentAttribute(function(json){
                count = json.data.length;
            }.bind(this), null, this.data.id, false);
            return count;
        }
        return 0;
    },
	addAttribute: function(){
		var data = this.getNewAttributeData();
        if (!this.created){
            this.saveBaseInfor(function(){
                new MWF.xApplication.Organization.DepartmentAttribute(this.propertyAttributeContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
            }.bind(this));
        }else{
            new MWF.xApplication.Organization.DepartmentAttribute(this.propertyAttributeContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
        }
	},
	showItemMembers: function(){
		this.propertyMembersNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.membersActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyMembersNode);
		this.propertyMembersTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.app.lp.departmentMemberText
		}).inject(this.propertyMembersNode);
		
		this.propertyMemberContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyMembersNode);
		
	//	this.createDeleteMemberNode();
        if (this.isEditor) this.createAddMemberNode();
		
		this.listMembers();
	},
	createAddMemberNode: function(){
		this.addMemberNode = new Element("button", {
			"styles": this.style.addDutyNode,
			"text": this.explorer.app.lp.add,
			"events": {"click": this.addMember.bind(this)}
		}).inject(this.membersActionNode);
	},
	createDeleteMemberNode: function(){
		this.deleteMemberNode = new Element("button", {
			"styles": this.style.deleteDutyNode_desable,
			"text": this.explorer.app.lp["delete"],
			"disable": true
		}).inject(this.membersActionNode);
	},
    checkSaveBaseInfor: function(callback){
        if (!this.created){
            this.saveBaseInfor(function(){
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
	addMember: function(){
        this.checkSaveBaseInfor(function(){
            MWF.xDesktop.requireApp("Organization", "Selector.Person", function(){
                var selector = new MWF.xApplication.Organization.Selector.Person(this.explorer.app.content,{
                    "values": [],
                    "onComplete": function(items){
                        var ids = 0;
                        items.each(function(item){
                            var newIData = {
                                "person": item.data.id,
                                "department": this.data.id,
                                "name": item.data.name+"("+this.data.name+")"
                            };
                            this.explorer.actions.saveIdentity(newIData, function(json){
                                newIData.id = json.data.id;
                                this.createIdentity(newIData);
                            }.bind(this));
                        }.bind(this));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));
	},
	listMembers: function(){
        if (this.data.id) {
            this.explorer.actions.listIdentity(function (json) {
                json.data.each(function (item) {
                    var _department = this;
                    this.createIdentity(item);
                }.bind(this));
            }.bind(this), null, this.data.id);
        }
	},

    getMemberCount: function(){
        if (this.data.id){
            var count = 0;
            this.explorer.actions.listIdentity(function(json){
                count = json.data.length;
            }.bind(this), null, this.data.id, false);
            return count;
        }
        return 0;
    },
	createIdentity: function(item){
        _department = this;

        MWF.require("MWF.widget.Identity", function(){
            new MWF.widget.Identity(item, this.propertyMemberContentNode, this.explorer, this.isEditor, function(e){
                var _self = this;
                var text = this.explorer.app.lp.deleteIdentityInDepartment;
                text = text.replace("{depart}", _department.data.name);
                text = text.replace("{identity}", this.data.name);
                _self.explorer.app.confirm("warn", e, this.explorer.app.lp.deleteIdentityInDepartmentTitle, text, 400, 140, function(){
                    _self.explorer.actions.deleteIdentity(_self.data.id, function(){
                        _self.node.destroy();
                        delete _self;

                        //	_department.
                    });

                    this.close();
                }, function(){
                    this.close();
                });
            });
        }.bind(this));
	},

    _deleteItem: function(id, success, failure){
        this.explorer.actions.deleteDepartment(id, success, failure, false);
    },

    _deleteDutys: function(){
        this._listDutys(function(json){
            json.data.each(function(duty){
                this.explorer.actions.deleteDepartmentDuty(duty.id, null, null, false);
            }.bind(this));
        }.bind(this));
    },
    _listDutys: function(success){
        this.explorer.actions.listDepartmentDuty(function(json){
            if (success) success(json);
        }.bind(this), null, this.data.id, false);
    },
    _deleteAttributes: function(){
        this._listAttributes(function(json){
            json.data.each(function(att){
                this.explorer.actions.deleteDepartmentAttribute(att.id, null, null, false);
            }.bind(this));
        }.bind(this));
    },
    _listAttributes: function(success){
        this.explorer.actions.listDepartmentAttribute(function(json){
            if (success) success(json);
        }.bind(this), null, this.data.id, false);
    },
    _deleteMembers: function(){
        this.explorer.actions.listIdentity(function(json){
            json.data.each(function(i){
                this.explorer.actions.deleteIdentity(i.id, null, null, false);
            }.bind(this));
        }.bind(this), null, this.data.id, false);
    }
});

MWF.xApplication.Organization.Input = new Class({
	Implements: [Events],
	initialize: function(node, value, style){
		this.node = $(node);
		this.value = value || "";
		this.style = style;
		this.load();
	},
	load: function(){
		this.content = new Element("div", {
			"styles": this.style.content,
			"text": this.value
		}).inject(this.node);
	},
	editMode: function(){
		this.content.empty();
		this.input = new Element("input",{
			"styles": this.style.input,
			"value": this.value
		}).inject(this.content);
		
		this.input.addEvents({
			"focus": function(){
				this.input.setStyles(this.style.input_focus);
			}.bind(this),
			"blur": function(){
				this.input.setStyles(this.style.input);
			}.bind(this)
		});
		
	},
	readMode: function(){
		this.content.empty();
		this.input = null;
		this.content.set("text", this.value);
	},
	save: function(){
		if (this.input) this.value = this.input.get("value");
		return this.value;
	}
});

MWF.xApplication.Organization.CompanyDuty = new Class({
	initialize: function(container, data, item, style){
		this.container = $(container);
		this.data = data;
		if (this.data.identityList) this.data.identityList = data.identityList.filter(function(item){return item;});
		this.style = style;
		this.item = item;
		this.identitys = [];
		this.selected = false;
		this.load();
	},
	load: function(){
		this.node = new Element("tr", {
			"styles": this.style.contentNode
		}).inject(this.container);
		
		this.selectNode = new Element("td", {
			"styles": this.style.selectNode
		}).inject(this.node);
		
		this.nameNode = new Element("td", {
			"styles": this.style.nameNode,
			"html": (this.data.name) ? this.data.name : "<input type='text'/>"
		}).inject(this.node);
		this.input = this.nameNode.getFirst("input");
		if (this.input) this.setEditNameInput();
		
		this.valueNode = new Element("td", {
			"styles": this.style.valueNode
		}).inject(this.node);
		
		this.createActionNode();
		this.setEvent();
		this.loadValue();
	},
	createActionNode: function(){
        this.actionNode = new Element("td", {
            "styles": this.style.actionNode
        }).inject(this.node);
        if (this.item.isEditor) {
            this.actionNode.addEvent("click", function () {
                this.addIdentitys();
            }.bind(this));
        }else{
            this.actionNode.setStyle("background", "transparent");
        }
	},
	setEvent: function(){
		this.selectNode.addEvent("click", function(){
			this.selectNodeClick();
		}.bind(this));
		
		this.nameNode.addEvent("click", function(){
			if (!this.input){
				this.nameNode.empty();
				this.input = new Element("input", {"type": "text", "value": this.data.name}).inject(this.nameNode);
				this.setEditNameInput();
			}
		}.bind(this));
		
		this.valueNodeClick();
	},
	selectNodeClick: function(){
		if (!this.selected){
			this.selected = true;
			this.selectNode.setStyles(this.style.selectNode_selected);
			this.node.setStyles(this.style.contentNode_selected);
			this.item.selectedDutys.push(this);
			this.item.checkDeleteDutyAction();
		}else{
			this.selected = false;
			this.selectNode.setStyles(this.style.selectNode);
			this.node.setStyles(this.style.contentNode);
			this.item.selectedDutys.erase(this);
			this.item.checkDeleteDutyAction();
		}
	},
	valueNodeClick: function(){
		
	},
	setEditNameInput: function(){
		this.input.setStyles(this.style.nameInputNode);
		this.input.focus();
		this.input.addEvents({
			"blur": function(){
				var name = this.input.get("value");
				if (name){
					if (name != this.data.name){
						this.save(name);
					}else{
						this.nameNode.empty();
						this.input = null;
						this.nameNode.set("text", this.data.name);
					}
				}else{
					if (!this.data.id){
						this.node.destroy();
						delete this;
					}else{
						this.nameNode.empty();
						this.input = null;
						this.nameNode.set("text", this.data.name);
					}
				}
			}.bind(this)
		});
	},
	save: function(name){

		var oldName = this.data.name;
		if (name) this.data.name = name;
		this.item.explorer.actions.saveCompanyDuty(this.data, function(json){
			this.data.id = json.data.id;
			this.nameNode.empty();
			this.input = null;
			this.nameNode.set("text", this.data.name)
            this.loadValue();
		}.bind(this), function(xhr, text, error){
			this.data.name = oldName;
			this.input.focus();
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.item.explorer.app.notice("request json error: "+errorText, "error");
		}.bind(this));
	},
	loadValue: function(){

        this.identitys = [];
        this.valueNode.empty();
		if (this.data.identityList){
			this.data.identityList.each(function(id){
				if (id){
					this.item.explorer.actions.getIdentity(function(identity){
                        MWF.require("MWF.widget.Identity", function(){
                            this.identitys.push(new MWF.widget.Identity(identity.data, this.valueNode, this.item.explorer));
                        }.bind(this));
					}.bind(this), null, id, false);
				}
			}.bind(this));
		}
	},
	addIdentitys: function(){
        var selector = new MWF.OrgSelector(this.item.explorer.app.content, {
            "type": "Identity",
            "values": this.data.identityList || [],
            "onComplete": function(items){
                var ids = [];
                items.each(function(item){ids.push(item.data.id);});
                this.data.identityList = ids;
                this.save();
            }.bind(this)
        });
        //selector.load();
	},
	remove: function(){
		this.item.explorer.actions.deleteCompanyDuty(this.data.id, function(){
			this.node.destroy();
			delete this;
		}.bind(this));
	}
});
MWF.xApplication.Organization.DepartmentDuty = new Class({
	Extends: MWF.xApplication.Organization.CompanyDuty,
	save: function(name){
		var oldName = this.data.name;
        if (name) this.data.name = name;
		this.item.explorer.actions.saveDepartmentDuty(this.data, function(json){
			this.data.id = json.data.id;
			this.nameNode.empty();
			this.input = null;
			this.nameNode.set("text", this.data.name);
            this.loadValue();
		}.bind(this), function(xhr, text, error){
			this.data.name = oldName;
			this.input.focus();
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.item.explorer.app.notice("request json error: "+errorText, "error");
		}.bind(this));
	},
	remove: function(){
		this.item.explorer.actions.deleteDepartmentDuty(this.data.id, function(){
			this.node.destroy();
			delete this;
		}.bind(this));
	}
});

MWF.xApplication.Organization.CompanyAttribute = new Class({
	Extends: MWF.xApplication.Organization.CompanyDuty,
	
	createActionNode: function(){
		this.actionNode = new Element("td", {"styles": this.style.actionAttributeNode}).inject(this.node);
	},
	selectNodeClick: function(){
		if (!this.selected){
			this.selected = true;
			this.selectNode.setStyles(this.style.selectNode_selected);
			this.node.setStyles(this.style.contentNode_selected);
			this.item.selectedAttributes.push(this);
			this.item.checkDeleteAttributeAction();
		}else{
			this.selected = false;
			this.selectNode.setStyles(this.style.selectNode);
			this.node.setStyles(this.style.contentNode);
			this.item.selectedAttributes.erase(this);
			this.item.checkDeleteAttributeAction();
		}
	},
	valueNodeClick: function(){
		this.valueNode.addEvent("click", function(){
			if (!this.valueInput){
				this.valueNode.empty();
				this.valueInput = new Element("input", {"type": "text", "value": (this.data.attributeList) ? this.data.attributeList.join(",") : ""}).inject(this.valueNode);
				this.setEditValueInput();
			}
		}.bind(this));
	},
	setEditValueInput: function(){
		this.valueInput.setStyles(this.style.nameInputNode);
		this.valueInput.focus();
		this.valueInput.addEvents({
			"blur": function(){
				var value = this.valueInput.get("value");
				if (value){
					if (value != this.data.attributeList.join(",")){
						this.saveValue(value);
					}else{
						this.valueNode.empty();
						this.valueInput = null;
						this.valueNode.set("text", this.data.attributeList.join(","));
					}
				}else{
					if (!this.data.id){
						this.node.destroy();
						delete this;
					}else{
						this.valueNode.empty();
						this.valueInput = null;
						this.valueNode.set("text", this.data.attributeList.join(","));
					}
				}
			}.bind(this)
		});
	},
	saveValue: function(value){
		var oldValue = this.data.attributeList;
		this.data.attributeList = value.split("/,\s*/");
		this.item.explorer.actions.saveCompanyAttribute(this.data, function(json){
			this.data.id = json.data.id;
			this.valueNode.empty();
			this.valueInput = null;
			this.valueNode.set("text", this.data.attributeList.join(","));
		}.bind(this), function(xhr, text, error){
			this.data.attributeList = oldValue;
			this.valueInput.focus();
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.item.explorer.app.notice("request json error: "+errorText, "error");
		}.bind(this));
	},
	loadValue: function(){
		if (this.data.attributeList) this.valueNode.set("text", this.data.attributeList.join(","));
	},
	save: function(name){
		var oldName = this.data.name;
		this.data.name = name;
		this.item.explorer.actions.saveCompanyAttribute(this.data, function(json){
			this.data.id = json.data.id;
			this.nameNode.empty();
			this.input = null;
			this.nameNode.set("text", this.data.name);
		}.bind(this), function(xhr, text, error){
			this.data.name = oldName;
			this.input.focus();
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.item.explorer.app.notice("request json error: "+errorText, "error");
		}.bind(this));
	},
	remove: function(){
		this.item.explorer.actions.deleteCompanyAttribute(this.data.id, function(){
			this.node.destroy();
			delete this;
		}.bind(this));
	}
});
MWF.xApplication.Organization.DepartmentAttribute = new Class({
	Extends: MWF.xApplication.Organization.CompanyAttribute,
	saveValue: function(value){
		var oldValue = this.data.attributeList;
		this.data.attributeList = value.split("/,\s*/");
		this.item.explorer.actions.saveDepartmentAttribute(this.data, function(json){
			this.data.id = json.data.id;
			this.valueNode.empty();
			this.valueInput = null;
			this.valueNode.set("text", this.data.attributeList.join(","));
		}.bind(this), function(xhr, text, error){
			this.data.attributeList = oldValue;
			this.valueInput.focus();
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.item.explorer.app.notice("request json error: "+errorText, "error");
		}.bind(this));
	},
	save: function(name){
		var oldName = this.data.name;
		this.data.name = name;
		this.item.explorer.actions.saveDepartmentAttribute(this.data, function(json){
			this.data.id = json.data.id;
			this.nameNode.empty();
			this.input = null;
			this.nameNode.set("text", this.data.name);
		}.bind(this), function(xhr, text, error){
			this.data.name = oldName;
			this.input.focus();
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.item.explorer.app.notice("request json error: "+errorText, "error");
		}.bind(this));
	},
	remove: function(){
		this.item.explorer.actions.deleteDepartmentAttribute(this.data.id, function(){
			this.node.destroy();
			delete this;
		}.bind(this));
	}
});
//MWF.xApplication.Organization.Identity = new Class({
//	initialize: function(data, container, explorer, style, canRemove, removeAction){
//		this.container = $(container);
//		this.data = data;
//		this.style = style;
//		this.explorer = explorer;
//		this.canRemove = canRemove || false;
//		this.removeAction = removeAction;
//		this.load();
//	},
//	load: function(){
//		this.node = new Element("div", {
//			"styles": this.style.identityNode,
//			"text": this.data.name
//		}).inject(this.container);
//
//		if (this.canRemove){
//			this.removeNode = new Element("div", {
//				"styles": this.style.identityRemoveNode
//			}).inject(this.node);
//			if (this.removeAction) this.removeNode.addEvent("click", this.removeAction.bind(this));
////			var pr = this.node.getStyle("padding-right").toFloat();
////			pr = pr+this.removeNode.getSize().x;
////			this.node.setStyle("padding-right", ""+pr+"px");
//		}
//
//		this.explorer.actions.getPerson(function(person){
//			this.inforNode = new Element("div", {
//				"styles": this.style.identityInforNode
//			});
//			var nameNode = new Element("div", {
//				"styles": this.style.identityInforNameNode
//			}).inject(this.inforNode);
//			var picNode = new Element("div", {
//				"styles": this.style.identityInforPicNode,
//				"html": "<img width='50' height='50' border='0' src='"+"/x_component_Organization/$OrgExplorer/default/icon/head.png'></img>"
//			}).inject(nameNode);
//			var nameTextNode = new Element("div", {
//				"styles": this.style.identityInforNameTextNode,
//				"text": person.data.display
//			}).inject(nameNode);
//
//			var phoneNode = new Element("div", {
//				"styles": this.style.identityInforPhoneNode,
//				"html": "<div style='width:30px; float:left'>"+this.explorer.app.lp.phone+": </div><div style='width:90px; float:left; margin-left:10px'>"+person.data.mobile+"</div>"
//			}).inject(this.inforNode);
//			var mailNode = new Element("div", {
//				"styles": this.style.identityInforPhoneNode,
//				"html": "<div style='width:30px; float:left'>"+this.explorer.app.lp.mail+": </div><div style='width:90px; float:left; margin-left:10px'>"+person.data.mail+"</div>"
//			}).inject(this.inforNode);
//
//
//			new mBox.Tooltip({
//				content: this.inforNode,
//				setStyles: {content: {padding: 15, lineHeight: 20}},
//				attach: this.node,
//				transition: 'flyin'
//			});
//
//		}.bind(this), null, this.data.person);
//
//
//		this.node.addEvents({
//			"mouseover": function(){
//				this.node.setStyles(this.style.identityNode_over);
//		//		this.showPersonInfor();
//			}.bind(this),
//			"mouseout": function(){
//				this.node.setStyles(this.style.identityNode);
//		//		this.hidePersonInfor();
//			}.bind(this)
//		});
//	}
//});

