MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.cms.QueryViewDesigner = MWF.xApplication.cms.QueryViewDesigner || {};
MWF.xApplication.cms.QueryViewDesigner.widget = MWF.xApplication.cms.QueryViewDesigner.widget || {};
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", null, false);
MWF.require("MWF.widget.O2Identity", null, false);

MWF.widget.O2CMSApplication = new Class({
    Extends: MWF.widget.O2Application,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
            this.action.actions = {"getCMSApplication": {"uri": "/jaxrs/appinfo/{id}"}};
            this.action.invoke({"name": "getCMSApplication", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
                this.data.name = this.data.appName;
            }.bind(this)});
        }
    }
});

MWF.widget.O2CMSCategory = new Class({
    Extends: MWF.widget.O2CMSApplication,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
            this.action.actions = {"getCMSCategory": {"uri": "/jaxrs/categoryinfo/{id}"}};
            this.action.invoke({"name": "getCMSCategory", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
                this.data.name = this.data.categoryName;
            }.bind(this)});
        }
    }
});

MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector = new Class({
    Implements: [Options, Events],
    Extends: MWF.xApplication.process.ProcessDesigner.widget.PersonSelector,
    options: {
        "style": "default",
        "type": "identity",
        "names": []
    },
    loadIdentitys: function(){
        var explorer = {
            "actions": this.restActions,
            "app": {
                "lp": this.app.lp
            }
        };
        if (this.options.names){
            if (this.options.type.toLowerCase()=="duty"){
                var dutys = JSON.decode(this.options.names);
                dutys.each(function(d){
                    var dutyItem = new MWF.widget.Duty(d, this.node, explorer, true, function(e){
                        var _self = this;
                        var text = this.selector.app.lp.deleteDutyText.replace(/{duty}/g, this.data.name);
                        this.selector.app.confirm("warm", e, this.selector.app.lp.deleteDutyTitle, text, 300, 120, function(){
                            _self.selector.fireEvent("removeDuty", [_self]);
                            this.close();
                        }, function(){
                            this.close();
                        });
                        e.stopPropagation();
                    });
                    dutyItem.selector = this;
                    dutyItem.explorer = explorer;
                    this.identitys.push(dutyItem);
                }.bind(this));
            }else{
                this.options.names.each(function(name){
                    MWF.require("MWF.widget.O2Identity", function(){
                        if (this.options.type.toLowerCase()=="identity") this.identitys.push(new MWF.widget.O2Identity({"name": name.name, "id": name.id}, this.node, {}));
                        if (this.options.type.toLowerCase()=="unit") this.identitys.push(new MWF.widget.O2Unit({"name": name.name, "id": name.id}, this.node, {}));
                        if (this.options.type.toLowerCase()=="person") this.identitys.push(new MWF.widget.O2Person({"name": name.name, "id": name.id}, this.node, {}));

                        if (this.options.type.toLowerCase()=="application") this.identitys.push(new MWF.widget.O2CMSApplication({"name": name.name, "id": name.id}, this.node, {}));
                        if (this.options.type.toLowerCase()=="category") this.identitys.push(new MWF.widget.O2CMSCategory({"name": name.name, "id": name.id}, this.node, {}));
                        if (this.options.type.toLowerCase()=="formfield") this.identitys.push(new MWF.widget.O2FormField({"name": name, "id": name}, this.node, {}));
                        if (this.options.type.toLowerCase()==="view") this.identitys.push(new MWF.widget.O2View(data, this.node));
                        if (this.options.type.toLowerCase()==="cmsview") this.identitys.push(new MWF.widget.O2CMSView(data, this.node));
                    }.bind(this));
                }.bind(this));
            }
        }

    },
    createAddNode: function(){
        this.addNode = new Element("div", {"styles": this.css.addPersonNode}).inject(this.node, "before");
        this.addNode.addEvent("click", function(e){

            var selecteds = [];
            this.identitys.each(function(id){selecteds.push(id.data.id)});

            var explorer = {
                "actions": this.restActions,
                "app": {
                    "lp": this.app.lp
                }
            };

            var type = this.options.type;
            if( type == "application" ){
                type = "CMSApplication";
            }else if( type == "category" ){
                type = "CMSCategory";
            }else if( type == "formField" ){
                type = "CMSFormField";
            }
            var options = {
                "type": type,
                "application": this.options.application,
                "fieldType": this.options.fieldType,
                "count": (this.options.type.toLowerCase()=="duty")? 1: 0,
                "values": selecteds,
                "zIndex": 20000,
                "form" : this.options.form,
                "onComplete": function(items){
                    this.identitys = [];
                    if (this.options.type.toLowerCase()!="duty") this.node.empty();

                    MWF.require("MWF.widget.O2Identity", function(){
                        items.each(function(item){
                            if (this.options.type.toLowerCase()=="identity") this.identitys.push(new MWF.widget.O2Identity(item.data, this.node, {}));
                            if (this.options.type.toLowerCase()=="unit") this.identitys.push(new MWF.widget.O2Unit(item.data, this.node, {}));
                            if (this.options.type.toLowerCase()=="person") this.identitys.push(new MWF.widget.O2Person(item.data, this.node, {}));

                            if (this.options.type.toLowerCase()=="application") this.identitys.push(new MWF.widget.O2CMSApplication(item.data, this.node, {}));
                            if (this.options.type.toLowerCase()=="category") this.identitys.push(new MWF.widget.O2CMSCategory(item.data, this.node, {}));
                            if (this.options.type.toLowerCase()=="formfield") this.identitys.push(new MWF.widget.O2FormField(item.data, this.node, {}));
                            if (this.options.type.toLowerCase()==="view") this.identitys.push(new MWF.widget.O2View(data, this.node));
                            if (this.options.type.toLowerCase()==="cmsview") this.identitys.push(new MWF.widget.O2CMSView(data, this.node));

                        }.bind(this));
                        if (this.options.type.toLowerCase()=="duty") {
                            items.each(function(item){
                                new MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector.DutyInput(this, item.data, this.node, explorer, 20000);
                            }.bind(this));
                        }

                        this.fireEvent("change", [this.identitys]);
                    }.bind(this));
                }.bind(this)
            };

            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    }
});
MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector.DutyInput = Class({
    Extends : MWF.xApplication.process.ProcessDesigner.widget.PersonSelector.DutyInput
});
