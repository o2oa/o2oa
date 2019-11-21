MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.xDesktop.requireApp("process.ProcessDesigner", "Property", null, false);
MWF.xApplication.process.ProcessDesigner.widget.OrgEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default"
	},
	initialize: function(node, route, data, options){
		this.setOptions(options);
		this.node = $(node);
        if( !data ){
            this.data = [];
        }else{
            this.data = typeOf( data ) === "string" ? JSON.parse(data) : data;
        }
        this.route = route;
        this.process = route.process;
		this.path = "/x_component_process_ProcessDesigner/widget/$OrgEditor/";
		this.cssPath = "/x_component_process_ProcessDesigner/widget/$OrgEditor/"+this.options.style+"/css.wcss";
		this._loadCss();
        this.selectedItems = [];
        this.lp = MWF.xApplication.process.ProcessDesigner.LP;
	},

    load: function(){

        this.scrollNode = this.process.panel.propertyTabPage.contentNodeArea;

        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.node);

        //this.previewNode = new Element("div", {
        //    "styles": this.css.previewNode,
        //    "title" : this.lp.preview
        //}).inject(this.toolbarNode);

        this.copyNode = new Element("div", {
            "styles": this.css.copyNode,
            "title" : this.lp.copy,
            "events" : {
                "click" : function( ev ){
                    this.selectOtherConfig( ev )
                }.bind(this)
            }
        }).inject(this.toolbarNode);

        this.selectedNode = new Element("div", {"styles": this.css.selectedNode}).inject(this.node);

        this.upNode = new Element("div", { "styles": this.css.upNode, "text" : "添加选择项" }).inject(this.node);
        this.upNode.addEvent("click", function( ev ){
            if( this.currentItem ){
                if( !this.currentItem.data.title || !this.currentItem.data.name ){
                    MWF.xDesktop.notice("error", {"y":"top", "x": "left"}, this.lp.notice.saveRouteOrgNoName, ev.target);
                    return;
                }
                var d = this.currentItem.getData;
                if( this.checkName(d.name, d.id ) ){
                    this.currentItem.save();
                    this.currentItem.unSelectItem();
                    if( this.defaultProperty ){
                        this.defaultProperty.show();
                    }else{
                        this.loadDefaultProperty();
                    }
                    this.scrollNode.scrollTo(0,0);
                    this.upNode.set("text","添加选择项");
                }else{
                    MWF.xDesktop.notice("error", {"y":"top", "x": "right"}, "该路由已经有重名配置："+ d.name, ev.target);
                }

            }else if( this.defaultProperty ){
                if( !this.defaultData.title || !this.defaultData.name ){
                    MWF.xDesktop.notice("error", {"y":"top", "x": "left"}, this.lp.notice.saveRouteOrgNoName, ev.target);
                    return;
                }
                var d = this.defaultData;
                if( this.checkName(d.name, d.id ) ) {
                    this.data.push(this.defaultData);
                    this.createSelectedItem(this.defaultData);
                    this.save();
                    this.defaultProperty.propertyContent.destroy();
                    this.loadDefaultProperty();
                    this.upNode.set("text","添加选择项");
                }else{
                    MWF.xDesktop.notice("error", {"y":"top", "x": "right"}, "该路由已经有重名配置："+ d.name, ev.target);
                }
            }
        }.bind(this));

        this.propertyNode = new Element("div", {"styles": this.css.propertyNode}).inject(this.node);

        this.getTemplate( function(){
            this.loadSelectedItems();
            this.loadDefaultProperty();
        }.bind(this));

        this.setUpNodeFixed();
    },
    setUpNodeFixed : function(){
        var scrollNode = this.scrollNode;
        scrollNode.getParent().setStyle("position","relative");
        scrollNode.addEvent("scroll",function(ev){
            if( this.node.offsetParent === null )return;
            var position = this.propertyNode.getPosition( scrollNode );
            if( position.y < 30 ){
                this.upNode.setStyles({
                    "position" : "absolute",
                    "top" : 1,
                    "left" : 0,
                    "border" : "1px solid #ffa200",
                    "background-color" : "#fff",
                    "width" : "99%"
                })
            }else{
                this.upNode.setStyles({
                    "position" : "static",
                    "border" : "0px"
                })
            }
        }.bind(this))
    },
    loadSelectedItems: function(){
        this.data.each(function(itemData){
            this.selectedItems.push(new MWF.xApplication.process.ProcessDesigner.widget.OrgEditor.SelectedItem(this, itemData));
        }.bind(this));
        this.fireEvent("change");
    },
    createSelectedItem : function( itemData ){
        this.selectedItems.push(new MWF.xApplication.process.ProcessDesigner.widget.OrgEditor.SelectedItem(this, itemData));
        this.fireEvent("change");
    },

    getTemplate: function(callback){
        if (!this.templateJson){
            MWF.getJSON("/x_component_process_ProcessDesigner/widget/$OrgEditor/org.json", function(json){
                this.templateJson = json;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    save : function(){
        this.route.data.selectConfig = JSON.stringify(this.getData());
    },
    getData: function(){
        var data = [];
        this.selectedItems.each(function(item){
            data.push(item.getData());
        });
        this.data = data;
        return data;
    },
    checkName : function( name, id ){
        var flag = true;
        debugger;
        this.selectedItems.each(function(item){
            var d = item.getData();
            if( d.name === name && d.id !== id ){
                flag = false;
            }
        });
        return flag;
    },
    loadDefaultProperty: function(){
        this.defaultData = Object.clone(this.templateJson);
        this.defaultProperty = new MWF.APPPD.widget.OrgEditor.Property(this, this.defaultData, {
            "onPostLoad": function () {
                this.defaultProperty.show();
            }.bind(this)
        });
        this.defaultProperty.load();
    },
    selectOtherConfig : function( ev ){
        var selectableItems = [];
        if( this.process && this.process.routes ){
            Object.each( this.process.routes, function(route){
                if(route.data.selectConfig){
                    var array = JSON.parse( route.data.selectConfig );
                    if( array && array.length ){
                        var json = {
                            "name": route.data.name,
                            "id": route.data.id,
                            "subItemList" : []
                        }
                        array.each( function( d ){
                            json.subItemList.push( { name : d.name + " - "+d.title,  id : d.id , data : JSON.stringify(d) } );
                        });
                        selectableItems.push( json );
                    }
                }
            });
        }

        o2.xDesktop.requireApp("Template", "Selector.Custom", function () {
            var options = {
                "count": 0,
                "title": "拷贝选择配置",
                "selectableItems": selectableItems,
                "expand": true,
                "category": true,
                "onComplete": function (items) {
                    if( items.length > 0 ){
                        var idList = [];
                        var nameConflictList = [];
                        this.process.designer.actions.getId( items.length, function(ids){
                            idList = ids.data;
                        }.bind(this),null,false);
                        items.each( function(item, i){
                            var data = JSON.parse(item.data.data);
                            data.id = idList[i].id;
                            if( this.checkName( data.name, data.id ) ){
                                this.selectedItems.push(new MWF.xApplication.process.ProcessDesigner.widget.OrgEditor.SelectedItem(this, data));
                            }else{
                                nameConflictList.push( data.name );
                            }
                            if( nameConflictList.length > 0 ){
                                MWF.xDesktop.notice("error", {"y":"top", "x": "right"}, "该路由已经有重名配置："+ nameConflictList.join(","), ev.target);
                            }
                        }.bind(this));
                        this.save();
                        this.fireEvent("change");
                    }
                }.bind(this)
            };
            var selector = new o2.xApplication.Template.Selector.Custom(this.process.designer.node, options);
            selector.load();
        }.bind(this))
    }
});

MWF.xApplication.process.ProcessDesigner.widget.OrgEditor.SelectedItem = new Class({
    initialize: function(editor, itemData){
        this.editor = editor;
        this.css = this.editor.css;
        this.data = itemData;

        if( !this.data.events || Object.keys(this.data.events).length === 0 ){
            this.data.events = Object.clone( this.editor.templateJson.events )
        }

        this.tmpData = Object.clone(itemData);
        this.node = new Element("div", {"styles": this.css.selectedItemNode}).inject(this.editor.selectedNode);
        this.load();
    },
    load: function(){

        this.textNode = new Element("div", {"styles": this.css.selectedItemTextNode}).inject(this.node);
        this.textNode.set({
            "text": this.data.name,
            "title": this.data.description
        });
        new Element("span", {
            "styles": this.css.selectedItemTextNode,
            "html" :  ( this.data.title ? ("(&nbsp;"+this.data.title+"&nbsp;)") : "" )
        }).inject(this.textNode);

        this.selectTypeNode = new Element("div", {
            "styles": this.css.selectedItemLabelNode,
            "text" :  this.editor.lp.selectType + ":"
        }).inject(this.node);
       new Element("span", {
            "styles": this.css.selectedItemTextNode,
            "text" :  this.data.selectType === "identity" ? this.editor.lp.identity : this.editor.lp.unit
        }).inject(this.selectTypeNode);

        this.selectCountNode = new Element("div", {
            "styles": this.css.selectedItemLabelNode,
            "text" :  this.editor.lp.selectCount + ":"
        }).inject(this.node);
        new Element("span", {
            "styles": this.css.selectedItemTextNode,
            "text" :  this.data.selectType === "identity" ? this.data.identityCount : this.data.unitCount
        }).inject(this.selectCountNode);

        this.node.addEvents({
            "mouseover": function(){
                if (this.editor.currentItem!=this) this.node.setStyles(this.css.selectedItemNode_over);
            }.bind(this),
            "mouseout": function(){
                if (this.editor.currentItem!=this) this.node.setStyles(this.css.selectedItemNode);
            }.bind(this),

            "click": function(){this.selectItem();}.bind(this)
        });

        this.closeNode = new Element("div", {
            "styles": this.css.selectedItemCloseNode,
            "title" : this.editor.lp.delete
        }).inject(this.node);
        this.closeNode.addEvent("click", function(ev){
            this.deleteItem(ev);
            ev.stopPropagation();
        }.bind(this));

        //this.loadProperty();
        //this.selectItem();
    },
    reload : function(){
        this.node.empty();
        this.load();
    },
    save : function(){
        this.data = Object.clone( this.tmpData );
        this.editor.save();
        this.reload();
    },
    getData : function( isClone ){
        var d = this.tmpData || this.data;
        return isClone ? Object.clone(d) : d;
    },
    loadProperty: function(){
        this.property = new MWF.APPPD.widget.OrgEditor.Property(this.editor, this.tmpData, {
            "onPostLoad": function () {
                this.property.show();
            }.bind(this)
        });
        this.property.load();
    },
    deleteItem: function(e){
        var _self = this;
        MWF.xDesktop.confirm("warn", e, this.editor.lp.deleteOrgConfirmTitle, this.editor.lp.deleteOrgConfirmContent, "300", "100", function(){
            _self._deleteItem();
            this.close();
        }, function(){
            this.close();
        })
    },
    _deleteItem: function(){
        this.node.destroy();
        if (this.property) this.property.propertyContent.destroy();
        this.editor.selectedItems.erase(this);
        //this.editor.data.erase(this.data);
        this.editor.save();
        if (this.editor.currentItem === this) this.editor.currentItem = null;
        this.editor.fireEvent("change");
        if( !this.editor.currentItem ){
            if(this.editor.defaultProperty){
                this.editor.defaultProperty.show();
                this.editor.upNode.set("text","添加选择项");
            }
        }
        MWF.release(this);
    },
    selectItem: function(){
        this.editor.upNode.set("text","修改选择项");
        if(this.editor.currentItem) this.editor.currentItem.unSelectItem();
        if(this.editor.defaultProperty)this.editor.defaultProperty.hide();
        if (this.property){
            this.property.show();
        }else{
            this.loadProperty();
        }
        this.node.setStyles(this.css.selectedItemNode_check);
        this.editor.currentItem = this;
    },
    unSelectItem: function(){
        this.node.setStyles(this.css.selectedItemNode);
        if (this.property) this.property.hide();
        this.editor.currentItem = null;
    }
});

MWF.xApplication.process.ProcessDesigner.widget.OrgEditor.Property = new Class({
    Implements: [Options, Events],
    Extends: MWF.APPPD.Property,
    initialize: function(org, data, options){
        this.setOptions(options);

        this.org = org;
        this.process = org.route.process;
        this.paper = this.process.paper;
        this.node = org.propertyNode;
        this.data = data;
        if( !this.data.id ){
            this.process.designer.actions.getId(1, function(ids){
                this.data.id = ids.data[0].id;
            }.bind(this),null,false);
        }
        this.data.pid = this.data.id; //this.process.process.id+this.org.route.data.id+this.data.id;
        this.htmlPath = "/x_component_process_ProcessDesigner/widget/$OrgEditor/org.html";
    },
    show: function(){
        if (!this.process.options.isView){
            if (!this.propertyContent){
                this.getHtmlString(function(){
                    this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
                    //this.process.panel.propertyTabPage.showTabIm();
                    this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
                    this.propertyContent.set("html", this.JsonTemplate.load());
                    //this.process.panel.data = this.data;

                    this.setEditNodeEvent();
                    this.setEditNodeStyles(this.propertyContent);
                    this.loadPropertyTab();
                    this.loadFormFieldInput();
                    this.loadPersonInput();
                    this.loadScriptInput();
                    this.loadScriptText();
                    this.loadScriptArea();
                    this.loadUnitTypeSelector();
                    this.loadEventsEditor();
                }.bind(this));
                //this.loadDutySelector();
            }else{
                //this.process.panel.data = this.data;
                this.propertyContent.setStyle("display", "block");
                //this.process.panel.propertyTabPage.showTabIm();
            }


            //    this.process.isFocus = true;
        }
    },
    setEditNodeEvent: function(){
        var property = this;
        //	var inputs = this.propertyContent.getElements(".editTableInput");
        var inputs = this.propertyContent.getElements("input");
        inputs.each(function(input){

            var jsondata = input.get("name");

            var id = this.data.id;
            input.set("name", id+jsondata);

            if (jsondata){
                var inputType = input.get("type").toLowerCase();
                switch (inputType){
                    case "radio":
                        input.addEvent("change", function(e){
                            property.setRadioValue(jsondata, this);
                        });
                        input.addEvent("blur", function(e){
                            property.setRadioValue(jsondata, this);
                        });
                        input.addEvent("keydown", function(e){
                            e.stopPropagation();
                        });
                        property.setRadioValue(jsondata, input);
                        break;
                    case "checkbox":
                        input.addEvent("keydown", function(e){
                            e.stopPropagation();
                        });

                        break;
                    default:
                        input.addEvent("change", function(e){
                            property.setValue(jsondata, this.value);
                        });
                        input.addEvent("blur", function(e){
                            property.setValue(jsondata, this.value);
                        });
                        input.addEvent("keydown", function(e){
                            if (e.code===13){
                                property.setValue(jsondata, this.value);
                            }
                            e.stopPropagation();
                        });
                        property.setValue(jsondata, input.get("value"));
                }
            }
        }.bind(this));

        var selects = this.propertyContent.getElements("select");
        selects.each(function(select){
            var jsondata = select.get("name");
            if (jsondata){
                select.addEvent("change", function(e){
                    property.setSelectValue(jsondata, this);
                });
                //property.setSelectValue(jsondata, select);
            }
        });

        var textareas = this.propertyContent.getElements("textarea");
        textareas.each(function(input){
            var jsondata = input.get("name");
            if (jsondata){
                input.addEvent("change", function(e){
                    property.setValue(jsondata, this.value);
                });
                input.addEvent("blur", function(e){
                    property.setValue(jsondata, this.value);
                });
                input.addEvent("keydown", function(e){
                    e.stopPropagation();
                });
            }
        }.bind(this));

    },
    loadEventsEditor: function(){
        var events = this.propertyContent.getElement(".MWFEventsArea");
        if (events){
            var name = events.get("name");
            var eventsObj = this.data[name];

            if( !MWF.xApplication.process.FormDesigner )MWF.xApplication.process.FormDesigner = {};
            if( !MWF.xApplication.process.FormDesigner.widget )MWF.xApplication.process.FormDesigner.widget = {};

            MWF.xDesktop.requireApp("process.FormDesigner", "widget.EventsEditor", function(){
                var eventsEditor = new MWF.xApplication.process.FormDesigner.widget.EventsEditor(events, this.process.designer, {
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.process.designer.content
                });
                eventsEditor.load(eventsObj, this.data, name);
            }.bind(this));
        }
    }

});
