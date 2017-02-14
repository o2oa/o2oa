MWF.Selector = new Class({
    Implements: [Options],
    options: {
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
        MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
        this.setOptions(options);
        this.container = container;
        var type = this.options.type.capitalize();

        if (type){
            MWF.xDesktop.requireApp("Selector", type, function(){
                this.selector = new MWF.xApplication.Selector[type](this.container, options);
                this.selector.load();
            }.bind(this));
        }
    }
});