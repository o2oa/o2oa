MWF.xDesktop.requireApp("ContentManage", "Actions.RestActions", null, false);

MWF.xApplication.ContentManage.options.multitask = true;
MWF.xApplication.ContentManage.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "ContentManage",
        "icon": "icon.png",
        "width": "600",
        "height": "500",
        "isResize": true,
        "isMax": true,
        "title": MWF.xApplication.ContentManage.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.ContentManage.LP;
        this.actions = new MWF.xApplication.ContentManage.Actions.RestActions();
    },
    loadApplication: function(callback){
        this.node = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        this.titleNode = new Element("input", {"styles": this.css.titleNode}).inject(this.node);
        this.departmentNode = new Element("input", {"styles": this.css.departmentNode}).inject(this.node);
        this.dataNode = new Element("textarea", {"styles": this.css.dataNode}).inject(this.node);

        this.submitButton = new Element("input", {
            "type": "button",
            "value": this.lp.ok,
            "styles": this.css.submitButton
        }).inject(this.node);
        this.setEvent();
    },
    setEvent: function(){
        this.submitButton.addEvent("click", function(){
            this.submit();
        }.bind(this));

    },
    submit: function(){
        var data = {
            "title": this.titleNode.get("value"),
            "department": this.departmentNode.get("value"),
            "data": this.dataNode.get("value")
        };
        this.actions.addNote(data, function(json){
            this.notice(this.lp.saveOk, "success");
        }.bind(this));
    }

});
