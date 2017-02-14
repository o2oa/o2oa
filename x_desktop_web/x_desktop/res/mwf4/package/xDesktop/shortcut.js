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
        debugger;
        var app = layout.desktop.currentApp;
        if (app) app.fireEvent("keyDelete", [e]);
    }
}

MWF.shortcut.keyboard = new Keyboard({
    defaultEventType: 'keydown',
    events: {
        "ctrl+c": function(e){MWF.shortcut.copy(e);},
        "ctrl+x": function(e){MWF.shortcut.cut(e);},
        "ctrl+v": function(e){MWF.shortcut.paste(e);},
        "ctrl+s": function(e){MWF.shortcut.save(e);},
        "delete": function(e){MWF.shortcut.keyDelete(e);}
    }
});
MWF.shortcut.keyboard.activate();
