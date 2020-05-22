o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.Identity = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default"
	},
	initialize: function(data, container, explorer, canRemove, removeAction, options){
		this.setOptions(options);

		this.path = o2.session.path+"/widget/$Identity/";
		this.cssPath = o2.session.path+"/widget/$Identity/"+this.options.style+"/css.wcss";
		this._loadCss();

        this.container = $(container);
        this.data = data;
        this.style = this.css;
        this.explorer = explorer;
        this.canRemove = canRemove || false;
        this.removeAction = removeAction;
        this.load();
	},
    load: function(){
        this.node = new Element("div", {
            "styles": this.style.identityNode,
            "text": this.data.name
        }).inject(this.container);

        if (this.canRemove){
            this.removeNode = new Element("div", {
                "styles": this.style.identityRemoveNode
            }).inject(this.node);
            if (this.removeAction) this.removeNode.addEvent("click", this.removeAction.bind(this));
//			var pr = this.node.getStyle("padding-right").toFloat();
//			pr = pr+this.removeNode.getSize().x;
//			this.node.setStyle("padding-right", ""+pr+"px");
        }
        this.createInforNode(function(){
            this.fireEvent("loadedInfor");
        }.bind(this));

        this.node.addEvents({
            "mouseover": function(){
                this.node.setStyles(this.style.identityNode_over);
                //		this.showPersonInfor();
            }.bind(this),
            "mouseout": function(){
                this.node.setStyles(this.style.identityNode);
                //		this.hidePersonInfor();
            }.bind(this)
        });
        this.setEvent();
    },
    setEvent: function(){},
    getPerson: function(callback){
        var method = "getPersonByIdentity";
        var key = this.data.name;
        if (!this.explorer.actions.getPersonByIdentity){
            method = "getPerson";
            key = this.data.person;
        }
        this.explorer.actions[method](function(json){
            if (callback) callback(json);
        }, null, key);

    },
    createInforNode: function(callback){
        this.getPerson(function(person){
            if (person.data){
                this.inforNode = new Element("div", {
                    "styles": this.style.identityInforNode
                });
                var nameNode = new Element("div", {
                    "styles": this.style.identityInforNameNode
                }).inject(this.inforNode);

                if (person.data.icon){
                    img = "<img width='50' height='50' border='0' src='data:image/png;base64,"+person.data.icon+"'></img>"
                }else{
                    if (person.genderType=="f"){
                        img = "<img width='50' height='50' border='0' src='"+"../x_component_Organization/$PersonExplorer/default/icon/female.png'></img>";
                    }else{
                        img = "<img width='50' height='50' border='0' src='"+"../x_component_Organization/$PersonExplorer/default/icon/man.png'></img>";
                    }
                }

                var picNode = new Element("div", {
                    "styles": this.style.identityInforPicNode,
                    "html": img
                }).inject(nameNode);
                var nameTextNode = new Element("div", {
                    "styles": this.style.identityInforNameTextNode,
                    "text": person.data.display
                }).inject(nameNode);

                var phoneNode = new Element("div", {
                    "styles": this.style.identityInforPhoneNode,
                    "html": "<div style='width:30px; float:left'>"+this.explorer.app.lp.phone+": </div><div style='width:90px; float:left; margin-left:10px'>"+(person.data.mobile || "")+"</div>"
                }).inject(this.inforNode);
                var mailNode = new Element("div", {
                    "styles": this.style.identityInforPhoneNode,
                    "html": "<div style='width:30px; float:left'>"+this.explorer.app.lp.mail+": </div><div style='width:90px; float:left; margin-left:10px'>"+(person.data.mail || "")+"</div>"
                }).inject(this.inforNode);


                new mBox.Tooltip({
                    content: this.inforNode,
                    setStyles: {content: {padding: 15, lineHeight: 20}},
                    attach: this.node,
                    transition: 'flyin'
                });
            }
            if (callback) callback();
        }.bind(this));
    }
});
o2.widget.Person = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Identity,
    getPerson: function(callback){
        if (this.data.name && this.data.id){
            if (callback) callback({"data": this.data});
        }else{
            var key = this.data.name;
            this.explorer.actions["getPerson"](function(json){
                if (callback) callback(json);
            }, null, key);
        }
    }
});

o2.widget.Department = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Identity,
    createInforNode: function(){
        return true;
    }
});
o2.widget.Company = new Class({
    Extends: o2.widget.Department
});
o2.widget.Application = new Class({
    Extends: o2.widget.Department
});
o2.widget.Process = new Class({
    Extends: o2.widget.Department
});
o2.widget.FormField = new Class({
    Extends: o2.widget.Department
});