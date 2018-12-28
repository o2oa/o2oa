MWF.xApplication.cms.Xform.widget = MWF.xApplication.cms.Xform.widget || {};
MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
MWF.xApplication.cms.Xform.widget.View = new Class({
    Extends: MWF.xApplication.process.Xform.widget.View,
    getLookupAction: function(callback){
        if (!this.lookupAction){
            MWF.require("MWF.xDesktop.Actions.RestActions", function(){
                this.lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
                this.lookupAction.getActions = function(actionCallback){
                    this.actions = {
                        //"lookup": {"uri": "/jaxrs/view/{id}"},
                        //"lookupName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}"},
                        //"getView": {"uri": "/jaxrs/view/{id}/design"},
                        //"getViewName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}/design"}
                        //"lookup": {"uri": "/jaxrs/view/{id}"},
                        "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                        "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                        //"getViewName": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}/design"}
                    };
                    if (actionCallback) actionCallback();
                };
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    }
});