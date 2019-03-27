MWF.xDesktop = MWF.xDesktop || {};
MWF.xDesktop.requireApp = function(module, clazz, callback, async){
    o2.requireApp(module, clazz, callback, async)
};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xDesktop.Common", null, false);
MWF.require("MWF.xDesktop.UserData", null, false);
MWF.require("MWF.xAction.RestActions", null, false);
MWF.require("MWF.xDesktop.Access", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xDesktop.requireApp("Common", "", null, false);