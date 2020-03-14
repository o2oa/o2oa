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
    },
    minDesktop: function(){
        var keys = Object.keys(layout.desktop.apps);
        keys.each(function(key){
            if (layout.desktop.apps[key].minSize) layout.desktop.apps[key].minSize();
        });
    },
    maxDesktop: function(){
        var keys = Object.keys(layout.desktop.apps);
        keys.each(function(key){
            if (layout.desktop.apps[key].maxSize) layout.desktop.apps[key].maxSize();
        });
    }
};

debugger;
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

        "ctrl+alt+o": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("Org"); e.stopPropagation();},
        "ctrl+alt+s": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("Setting"); e.stopPropagation();},
        "ctrl+alt+b": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("BAM");},

        "ctrl+alt+f": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("process.ApplicationExplorer"); e.stopPropagation();},
        "ctrl+alt+p": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("portal.PortalExplorer"); e.stopPropagation();},
        "ctrl+alt+c": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("cms.Column"); e.stopPropagation();},
        "ctrl+alt+q": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("query.QueryExplorer"); e.stopPropagation();},
        "ctrl+alt+v": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("service.ServiceManager"); e.stopPropagation();},
//"shift+s": function(e){MWF.shortcut.openApplication("service.ServiceManager");},

        "ctrl+alt+t": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("process.TaskCenter"); e.stopPropagation();},
        "ctrl+alt+i": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("cms.Index"); e.stopPropagation();},
        "ctrl+alt+a": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("File"); e.stopPropagation();},
        "ctrl+alt+r": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("Calendar"); e.stopPropagation();},
        "ctrl+alt+n": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("Note"); e.stopPropagation();},
        "ctrl+alt+m": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("Meeting"); e.stopPropagation();},
        "ctrl+alt+l": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("Forum"); e.stopPropagation();},
        "ctrl+alt+k": (layout.inBrowser) ? null : function(e){MWF.shortcut.openApplication("Attendance"); e.stopPropagation();},

        "ctrl+alt+d": (layout.inBrowser) ? null : function(e){MWF.shortcut.clearDesktop(); e.stopPropagation();},
        "ctrl+alt+-": (layout.inBrowser) ? null : function(e){MWF.shortcut.minDesktop(); e.stopPropagation();},
        "ctrl+alt+=": (layout.inBrowser) ? null : function(e){MWF.shortcut.maxDesktop(); e.stopPropagation();},

        "delete": function(e){MWF.shortcut.keyDelete(e);}
    }
});
MWF.shortcut.keyboard.activate();
