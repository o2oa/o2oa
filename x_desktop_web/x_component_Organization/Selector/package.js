MWF.xApplication.Organization = MWF.xApplication.Organization || {};
MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};

MWF.xApplication.Organization.Selector.Selector = MWF.OrgSelector = new Class({
    Implements: [Options],
    options: {
        "type": "person",
        "count": 0,
        "title": "Select Person",
        "groups": [],
        "roles": [],
        "companys": [],
        "departments": [],
        "values": [],
        "names": []
    },
    initialize: function(container, options){
        debugger;
        MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
        this.setOptions(options);
        this.container = container;
        var type = this.options.type.capitalize();

        if (type){
            MWF.xDesktop.requireApp("Organization", "Selector."+type, function(){
                this.selector = new MWF.xApplication.Organization.Selector[type](this.container, options);
                this.selector.load();
            }.bind(this));
        }
    }
});