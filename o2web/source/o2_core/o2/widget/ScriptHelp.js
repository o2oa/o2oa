o2.widget = o2.widget || {};
o2.require("o2.widget.Menu", null, false);
o2.widget.ScriptHelpCodes = {};
o2.widget.ScriptHelp = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "code": "code.json",
        "style": "default"
    },
    initialize: function(node, editor, options){
        this.setOptions(options);

        this.node = $(node);
        this.editor = editor;

        this.path = o2.session.path+"/widget/$ScriptHelp/";
        this.cssPath = o2.session.path+"/widget/$ScriptHelp/"+this.options.style+"/css.wcss";
        this.load();
    },
    getEditor: function(){
        return null;
    },
    load: function(){
        this.menu = new o2.widget.ScriptHelp.Menu(this.node, {"event": "click", "style": "script"});
        this.menu.scriptHelp = this;
        this.menu.load();

        if (!o2.widget.ScriptHelpCodes[this.path+this.options.style+"/"+this.options.code]){
            o2.getJSON(this.path+this.options.style+"/"+this.options.code, function(json){
                this.codeJson = json;
                this.loadMenuItems(this.codeJson, this.menu);

                o2.widget.ScriptHelpCodes[this.path+this.options.style+"/"+this.options.code] = json;
                this.fireEvent("postLoad");
            }.bind(this), true, true, false);
        }else{
            this.codeJson = o2.widget.ScriptHelpCodes[this.path+this.options.style+"/"+this.options.code];
            this.loadMenuItems(this.codeJson, this.menu);

            this.fireEvent("postLoad");
        }

    },
    loadMenuItems: function(json, menu){
        json.each(function(code){
            if (code=="-"){
                menu.addMenuLine();
            }else{
                if (typeOf(code.value)=="string"){
                    menu.addMenuItem(code.name, "click", function(){
                        var editor = this.getEditor();
                        if (editor){
                            editor.insert(code.value);
                            editor.focus();
                        }
                    }.bind(this));
                }else{
                    var subMenu = new o2.widget.ScriptHelp.Menu(this.node, {"style": "script"});
                    subMenu.load();
                    this.loadMenuItems(code.value, subMenu);
                    menu.addMenuMenu(code.name, "", subMenu);
                }
            }

        }.bind(this));
    },
    show: function(){
        this.menu.showIm();
    }
});

o2.widget.ScriptHelp.Menu = new Class({
    Extends: o2.widget.Menu,

    showIm: function(e){
        if (!this.options.disable){
            this.hide = this.hideIm.bind(this);
            if (this.fireEvent("queryShow", [e])){
                this.tmpBodyOncontextmenu = document.body.oncontextmenu;
                document.body.oncontextmenu = function(){return false;};
                if (this.pauseCount<=0){
                    this.setItemWidth();

                    this.node.setStyles({
                        "display": "block",
                        "opacity": this.options.opacity || 1
                    });

                    this.setPosition(e);

                    $(document.body).removeEvent("mousedown", this.hide);
                    $(document.body).addEvent("mousedown", this.hide);

                    this.show = true;
                }else{
                    this.pauseCount--;
                }

                this.node.focus();
                if (!this.isSetKeyEvents) this.setKeyEvents();

                this.fireEvent("postShow");
            }
        }
    },
    setKeyEvents: function(){
        //  this.node.addEvents({
        //      "keydown:keys(down)": function(e){this.keyDown(e);}.bind(this),
        //      "keydown:keys(up)": function(e){this.keyUp(e);}.bind(this),
        //      "keydown:keys(left)": function(e){this.keyLeft(e);}.bind(this),
        //      "keydown:keys(right)": function(e){this.keyRight(e);}.bind(this),
        //      "keydown:keys(esc)": function(e){this.keyEsc(e);}.bind(this),
        //      "keydown:keys(space)": function(e){this.keyEnter(e);}.bind(this),
        //      "keydown:keys(enter)": function(e){this.keyEnter(e);}.bind(this),
        ////      "keydown": function(e){this.keyEsc(e);}.bind(this)
        //  });

        //this.node.addEvent("keydown:keys(down)", function(e){this.keyMenuDown(e);}.bind(this));
        //this.node.addEvent("keydown:keys(up)", function(e){this.keyMenuUp(e);}.bind(this));
        //this.node.addEvent("keydown:keys(left)", function(e){this.keyMenuLeft(e);}.bind(this));
        //this.node.addEvent("keydown:keys(right)", function(e){this.keyMenuRight(e);}.bind(this));
        //this.node.addEvent("keydown:keys(esc)", function(e){this.keyMenuEsc(e);}.bind(this));
        //this.node.addEvent("keydown:keys(space)", function(e){this.keyMenuEnter(e);}.bind(this));
        //this.node.addEvent("keydown:keys(enter)", function(e){this.keyMenuEnter(e);}.bind(this));

        this.node.addEvent("keydown", function(e){this.keyMenuAction(e);}.bind(this));

        this.isSetKeyEvents = true;
    },

    keyMenuAction: function(e){
        switch (e.key){
            case "down":
                this.keyMenuDown(e);
                break;
            case "up":
                this.keyMenuUp(e);
                break;
            case "left":
                this.keyMenuLeft(e);
                break;
            case "right":
                this.keyMenuRight(e);
                break;
            case "esc":
                this.keyMenuEsc(e);
                break;
            case "space":
                this.keyMenuEnter(e);
                break;
            case "enter":
                this.keyMenuEnter(e);
                break;
            default:
        }
    },

    keyMenuDown: function(e){
        if (!this.current){
            this.items[0]._menuItemMouseOver(e);
        }else{
            idx = this.items.indexOf(this.current);
            idx++;
            while (idx<this.items.length && this.items[idx].type=="line"){
                idx++;
            }
            var item = this.items[idx];
            if ((idx)>=this.items.length) item = this.items[0];
            item._menuItemMouseOver(e);
            if (item.type=="menu") this.node.focus();
        }
    },
    keyMenuUp: function(e){
        if (!this.current){
            this.items[this.items.length-1]._menuItemMouseOver(e);
        }else{
            idx = this.items.indexOf(this.current);
            idx--;
            while (idx>=0 && this.items[idx].type=="line"){
                idx--;
            }
            var item = this.items[idx];
            if ((idx)<0) item = this.items[this.items.length-1];
            item._menuItemMouseOver(e);
            if (item.type=="menu") this.node.focus();
        }
    },
    keyMenuRight: function(e){
        if (this.current){
            if (this.current.type=="menu"){
                this.current.subMenu.showIm();
                this.current.subMenu.node.focus();
                this.current.subMenu.current = null;
                this.current.subMenu.keyMenuDown();
            }
        }
    },
    keyMenuLeft: function(e){
        if (this.topMenu){
            this.hideIm();
            this.topMenu.node.focus();
        }
    },

    keyMenuEsc: function(){
        if (this.topMenu){
            this.hideIm();
            this.topMenu.node.focus();
        }else{
            this.hideIm();
            this.scriptHelp.getEditor().focus();
        }
    },
    keyMenuEnter: function(e){
        if (this.current) this.current.doAction();
        e.stopPropagation();
    }

});
