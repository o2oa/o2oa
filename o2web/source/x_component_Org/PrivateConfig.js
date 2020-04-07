MWF.require("MWF.widget.O2Identity", null, false);
MWF.xApplication.Org.PrivateConfig = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "lp": {

        }
    },
    initialize: function(node, actions, options){
        this.setOptions(options);
        this.restLoadActions =  MWF.Actions.load("x_organization_assemble_control");
        this.node = $(node);
    },
    _isActionManager: function(){
        return (MWF.AC.isOrganizationManager() || MWF.AC.isPersonManager() || MWF.AC.isUnitManager());
    },
    _loadLp: function(){
        this.options.lp = {
            "queryPrivateConfigTitle": this.app.lp.queryPrivateConfigTitle,
            "queryPrivateConfigExcludUnit": this.app.lp.queryPrivateConfigExcludUnit,
            "queryPrivateConfigExcludPerson": this.app.lp.queryPrivateConfigExcludPerson,
            "queryPrivateConfigLimitOuter": this.app.lp.queryPrivateConfigLimitOuter,
            "queryPrivateConfigLimitAll": this.app.lp.queryPrivateConfigLimitAll,
            "queryPrivateConfigDescribe": this.app.lp.queryPrivateConfigDescribe,
            "queryPrivateConfigBtnEdit": this.app.lp.queryPrivateConfigBtnEdit,
            "queryPrivateConfigBtnSave": this.app.lp.queryPrivateConfigBtnSave,
            "queryPrivateConfigBtnCancel": this.app.lp.queryPrivateConfigBtnCancel
        }
    },
    _loadConfig:function(){
        this.ConfigContent = new MWF.xApplication.Org.PrivateConfig.ConfigContent(this);
        this.ConfigContent.load();
    },
    load: function(){
        this._loadLp();
        this._loadConfig();
    }

});

MWF.xApplication.Org.PrivateConfig.ConfigContent = new Class({
    initialize: function(content){
        debugger;
        this.content = content;
        this.lp = this.content.options.lp;
        this.restLoadActions = MWF.Actions.load("x_organization_assemble_control");       
        this.data = {};
        this.data["id"] = "";
        this.data["excludeUnit"]=[];
        this.data["excludePerson"]=[];
        this.data["limitQueryOuter"]=[];
        this.data["limitQueryAll"]=[];
        this.data["explain"]="";
        this.data["status"]="发布";
        this.contentNode = this.content.node;
        this.mode = "read";
    },
    load: function(){
        //get data
        this.restLoadActions.PermissionSettingAction.list(function( json ){ 
            if (json.data.length){
				this.data = json.data[0];
			}
        }.bind(this),null,false);
        debugger;
        this.node = new Element("div").inject(this.contentNode);
        this.editContentNode = new Element("div").inject(this.node);

        this.editContentNode.set("html", this.getContentHtml());

        var n = this.editContentNode.getElement(".excludeUnit");
        var displaynames =[];
        this.data.excludeUnit.each(function(ptv){
            displaynames.push(ptv.split("@")[0]);
        })
        if (n) n.set("text", displaynames.join() || "");

        displaynames =[];
        this.data.excludePerson.each(function(ptv){
            displaynames.push(ptv.split("@")[0]);
        })
        var n = this.editContentNode.getElement(".excludePerson");
        if (n) n.set("text", displaynames.join() || "");

        displaynames =[];
        this.data.limitQueryOuter.each(function(ptv){
            displaynames.push(ptv.split("@")[0]);
        })
        var n = this.editContentNode.getElement(".limitQueryOuter");
        if (n) n.set("text", displaynames.join() || "");

        displaynames =[];
        this.data.limitQueryAll.each(function(ptv){
            displaynames.push(ptv.split("@")[0]);
        })
        var n = this.editContentNode.getElement(".limitQueryAll");
        if (n) n.set("text", displaynames.join() || "");

        var n = this.editContentNode.getElement(".explain");
        if (n) n.set("text", this.data.explain || "");

        var tdContents = this.editContentNode.getElements("td.inforContent");
        //if (this.data.excludeUnit) new MWF.widget.O2Unit({"name": this.data.excludeUnit}, tdContents[0], {"style": "xform"});
        //if (this.data.excludePerson) new MWF.widget.O2Person({"name": this.data.excludePerson}, tdContents[1], {"style": "xform"});
        //if (this.data.limitQueryOuter) new MWF.widget.O2Identity({"name": this.data.limitQueryOuter}, tdContents[2], {"style": "xform"});
        //if (this.data.limitQueryAll) new MWF.widget.O2Identity({"name": this.data.limitQueryAll}, tdContents[3], {"style": "xform"});

        this.loadAction();
    },
    getContentHtml: function(){
        var html = "<table class='queryPrivateConfigTable'>";
		html += "<tr><td class='tabletitle' colspan=2>"+this.lp.queryPrivateConfigTitle+"</td></tr>"
        html += "<tr><td class='inforTitle'>"+this.lp.queryPrivateConfigExcludUnit+":</td><td class='inforContent excludeUnit'></td></tr>";
        html += "<td class='inforTitle'>"+this.lp.queryPrivateConfigExcludPerson+":</td><td class='inforContent excludePerson'></td></tr>";
		html += "<tr><td class='inforTitle'>"+this.lp.queryPrivateConfigLimitOuter+":</td><td class='inforContent limitQueryOuter'></td></tr>" ;
        html += "<td class='inforTitle'>"+this.lp.queryPrivateConfigLimitAll+":</td><td class='inforContent limitQueryAll'></td></tr>";
		html += "<tr><td class='inforTitle'>"+this.lp.queryPrivateConfigDescribe+":</td><td class='inforContent explain'></td></tr>";
        html += "<tr><td colspan='2' class='inforAction'></td></tr>";
        //this.baseInforRightNode.set("html", html);
        return html;
    },

    loadAction: function(){
        //this.explorer.app.lp.edit
        var actionAreas = this.editContentNode.getElements("td");
        var actionArea = actionAreas[actionAreas.length-1];

        if (MWF.AC.isOrganizationManager() || MWF.AC.isPersonManager() || MWF.AC.isUnitManager()){
            this.baseInforEditActionAreaNode = new Element("div", {"class": "queryPrivateConfigBtnNode"}).inject(actionArea);

            this.editNode = new Element("div", {"class": "queryPrivateConfigBtnEditNode", "text": this.lp.queryPrivateConfigBtnEdit}).inject(this.baseInforEditActionAreaNode);

            this.saveNode = new Element("div", {"class": "queryPrivateConfigBtnSaveNode", "text": this.lp.queryPrivateConfigBtnSave}).inject(this.baseInforEditActionAreaNode);
            this.cancelNode = new Element("div", {"class":"queryPrivateConfigBtnCancelNode", "text": this.lp.queryPrivateConfigBtnCancel}).inject(this.baseInforEditActionAreaNode);

            this.editNode.setStyle("display", "block");
            this.editNode.addEvent("click", this.edit.bind(this));
            this.saveNode.addEvent("click", this.save.bind(this));
            this.cancelNode.addEvent("click", this.cancel.bind(this));
        }else{

        }
    },
    edit: function(){
        var tdContents = this.editContentNode.getElements("td.inforContent");
        tdContents[0].empty();
        this.excludeUnitInputNode = new Element("div", {"class": "inputPersonNode"}).inject(tdContents[0]);  
        if (this.data.excludeUnit){
            this.data.excludeUnit.each(function(perv){
                new MWF.widget.O2Unit({"name":perv.split("@")[0]}, this.excludeUnitInputNode, {"style": "xform"}); 
            }.bind(this))
        } 
        this.excludeUnitInputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                var options = {
                    "type": "unit",
                    "values": this.data.excludeUnit,
                    "count": 0,
                    "onComplete": function(items){
                        var ids= [];
                        var persons = [];
                        var displaynames = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                            persons.push(item.data.distinguishedName);
                        });
                        this.data.excludeUnit = persons;
                        this.excludeUnitInputNode.empty();
                        this.data.excludeUnit.each(function(perv){
                            new MWF.widget.O2Unit({"name":perv.split("@")[0]}, this.excludeUnitInputNode, {"style": "xform"}); 
                        }.bind(this))
                    }.bind(this)
                };
                var selector = new MWF.O2Selector(this.contentNode, options);
            }.bind(this));
        }.bind(this));

        tdContents[1].empty();
        this.excludePersonInputNode = new Element("div", {"class": "inputPersonNode"}).inject(tdContents[1]);
        //this.superiorInputNode.set("value", (this.data.superior));
        if (this.data.excludePerson){
            this.data.excludePerson.each(function(perv){
                new MWF.widget.O2Person({"name":perv.split("@")[0]}, this.excludePersonInputNode, {"style": "xform"}); 
            }.bind(this))
        } 
        this.excludePersonInputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                var options = {
                    "type": "person",
                    "values": this.data.excludePerson,
                    "count": 0,
                    "onComplete": function(items){
                        var ids= [];
                        var persons = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                            persons.push(item.data.distinguishedName);
                        });
                        this.data.excludePerson = persons;
                        this.excludePersonInputNode.empty();
                        this.data.excludePerson.each(function(perv){
                            new MWF.widget.O2Person({"name":perv.split("@")[0]}, this.excludePersonInputNode, {"style": "xform"}); 
                        }.bind(this))
                    }.bind(this)
                };
                var selector = new MWF.O2Selector(this.contentNode, options);
            }.bind(this));
        }.bind(this));

        tdContents[2].empty();
        this.limitQueryOuterInputNode = new Element("div", {"class": "inputPersonNode"}).inject(tdContents[2]);
        //this.superiorInputNode.set("value", (this.data.superior));
        if (this.data.limitQueryOuter){
            this.data.limitQueryOuter.each(function(perv){
                new MWF.widget.O2Person({"name":perv.split("@")[0]}, this.limitQueryOuterInputNode, {"style": "xform"}); 
            }.bind(this))
        }
        this.limitQueryOuterInputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                var options = {
                    "type": "person",
                    "values": this.data.limitQueryOuter,
                    "count": 0,
                    "onComplete": function(items){
                        var ids= [];
                        var persons = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                            persons.push(item.data.distinguishedName);
                        });
                        this.data.limitQueryOuter = persons;
                        this.limitQueryOuterInputNode.empty();
                        this.data.limitQueryOuter.each(function(perv){
                            new MWF.widget.O2Person({"name":perv.split("@")[0]}, this.limitQueryOuterInputNode, {"style": "xform"}); 
                        }.bind(this))
                    }.bind(this)
                };
                var selector = new MWF.O2Selector(this.contentNode, options);
            }.bind(this));
        }.bind(this));

        tdContents[3].empty();
        this.limitQueryAllInputNode = new Element("div", {"class": "inputPersonNode"}).inject(tdContents[3]);
        //this.superiorInputNode.set("value", (this.data.superior));
        if (this.data.limitQueryAll){
            this.data.limitQueryAll.each(function(perv){
                new MWF.widget.O2Person({"name":perv.split("@")[0]}, this.limitQueryAllInputNode, {"style": "xform"}); 
            }.bind(this))
        }
        this.limitQueryAllInputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                var options = {
                    "type": "person",
                    "values": this.data.limitQueryAll,
                    "count": 0,
                    "onComplete": function(items){
                        var ids= [];
                        var persons = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                            persons.push(item.data.distinguishedName);
                        });
                        this.data.limitQueryAll = persons;
                        this.limitQueryAllInputNode.empty();
                        this.data.limitQueryAll.each(function(perv){
                            new MWF.widget.O2Person({"name":perv.split("@")[0]}, this.limitQueryAllInputNode, {"style": "xform"}); 
                        }.bind(this))
                    }.bind(this)
                };
                var selector = new MWF.O2Selector(this.contentNode, options);
            }.bind(this));
        }.bind(this));

        tdContents[4].empty();
        this.explainInputNode = new Element("input", {"class": "inputNode"}).inject(tdContents[4]);
        this.explainInputNode.set("value", (this.data.explain));

       
        var _self = this;
        /*
        this.editContentNode.getElements("input").addEvents({
            "focus": function(){if (this.get("type").toLowerCase()==="text"){this.setStyles(_self.style.inputNode_focus);}},
            "blur": function(){if (this.get("type").toLowerCase()==="text"){this.setStyles(_self.style.inputNode_blur);}}
        });
        */
        this.mode = "edit";

        this.editNode.setStyle("display", "none");
        this.saveNode.setStyle("display", "block");
        this.cancelNode.setStyle("display", "block");
    },
    save: function(){
        //this.data.genderType = gender;
        debugger;
        var tdContents = this.editContentNode.getElements("td.inforContent");
        this.data["explain"]=tdContents[4].getElements(".inputNode")[0].get("value");
        /*
        this.content.propertyContentScrollNode.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });
        */
        if (this.data.id=="") {
               this.restLoadActions.PermissionSettingAction.create(
                    this.data,
                    function( json ){ 
                        data = json.data;
                        this.cancel();
                        //this.content.propertyContentScrollNode.unmask();
                    }.bind(this),null,false);
        }else{
            this.restLoadActions.PermissionSettingAction.update(
                this.data.id,
                this.data,
                function( json ){ 
                    data = json.data;
                    this.cancel();
                    //this.content.propertyContentScrollNode.unmask();
                }.bind(this),null,false);
        }
        debugger;
    },
    cancel: function(){
        this.node.empty();
        this.load();
    },
    destroy: function(){
        this.node.empty();
        this.node.destroy();
        MWF.release(this);
    }
});