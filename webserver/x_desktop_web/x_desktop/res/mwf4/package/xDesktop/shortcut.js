MWF.clipboard = {"data": null};

MWF.shortcut = {
    copy: function(e){
        var app = layout.desktop.currentApp;
        if (app) app.fireEvent("copy", [e]);
    },
    cut: function(e){
        var app = layout.desktop.currentApp;
        if (app) app.fireEvent("cut", [e]);
    },
    paste: function(e){
        var app = layout.desktop.currentApp;
        if (app) app.fireEvent("paste", [e]);
    },
    save: function(e){
        var app = layout.desktop.currentApp;
        if (app) app.fireEvent("keySave", [e]);
    },
    keyDelete: function(e){
        var app = layout.desktop.currentApp;
        if (app) app.fireEvent("keyDelete", [e]);
    },
    openApplication: function(app){
        layout.desktop.openApplication(null, app);
    },
    clearDesktop: function(){
        var keys = Array.clone(Object.keys(layout.desktop.apps));
        keys.each(function(key){
            layout.desktop.apps[key].close();
        });
        keys = null;
    }
};

MWF.shortcut.keyboard = new Keyboard({
    defaultEventType: 'keydown',
    events: {
        "ctrl+c": function(e){MWF.shortcut.copy(e);},
        "ctrl+x": function(e){MWF.shortcut.cut(e);},
        "ctrl+v": function(e){MWF.shortcut.paste(e);},
        "ctrl+s": function(e){MWF.shortcut.save(e);},

        "meta+c": function(e){MWF.shortcut.copy(e);},
        "meta+x": function(e){MWF.shortcut.cut(e);},
        "meta+v": function(e){MWF.shortcut.paste(e);},
        "meta+s": function(e){MWF.shortcut.save(e);},

        "shift+o": function(e){MWF.shortcut.openApplication("Org");},
        "shift+s": function(e){MWF.shortcut.openApplication("Setting");},

        "shift+f": function(e){MWF.shortcut.openApplication("process.ApplicationExplorer");},
        "shift+p": function(e){MWF.shortcut.openApplication("portal.PortalExplorer");},
        "shift+c": function(e){MWF.shortcut.openApplication("cms.Column");},
        "shift+q": function(e){MWF.shortcut.openApplication("query.QueryExplorer");},
        //"shift+s": function(e){MWF.shortcut.openApplication("service.ServiceManager");},

        "shift+t": function(e){MWF.shortcut.openApplication("process.TaskCenter");},
        "shift+i": function(e){MWF.shortcut.openApplication("cms.Index");},

        "shift+alt+c": function(e){MWF.shortcut.clearDesktop();},

        "delete": function(e){MWF.shortcut.keyDelete(e);}
    }
});
MWF.shortcut.keyboard.activate();
