MWF.xApplication.Note.options.multitask = false;
MWF.xApplication.Note.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Note",
		"icon": "icon.png",
		"width": "400",
		"height": "500",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.Note.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Note.LP;
	},
    loadWindowFlat: function(isCurrent){
        this.loadWindow(false);
    },
    loadWindow: function(isCurrent){
        this.fireAppEvent("queryLoadWindow");
        this.window = new MWF.xDesktop.WindowTransparent(this);
        this.fireAppEvent("loadWindow");
        this.window.show();
        this.content = this.window.content;

        if (isCurrent) this.setCurrent();
        this.fireAppEvent("postLoadWindow");
        this.fireAppEvent("queryLoadApplication");
        this.loadApplication(function(){
            this.fireAppEvent("postLoadApplication");
        }.bind(this));
    },
    setCurrent: function () {
        if (this.desktop.currentApp == this) return true;
        if (this.desktop.currentApp) {
            this.desktop.currentApp.taskitem.unSelected();
            this.desktop.currentApp.desktop.currentApp = null;
            //this.desktop.currentApp.setUncurrent();
        }

        this.window.setCurrent();

        if (this.window.isHide) {
            if (this.window.isMax) {
                this.window.maxSize(function () { this.fireAppEvent("current"); }.bind(this));
            } else {
                this.window.restore(function () { this.fireAppEvent("current"); }.bind(this));
            }
        } else {
            this.fireAppEvent("current");
        }

        if (this.taskitem) this.taskitem.selected();
        this.desktop.currentApp = this;

        this.desktop.appCurrentList.erase(this);
        this.desktop.appCurrentList.push(this);
    },

	loadApplication: function(callback){
        this.noteList = [];
        this.notes = [];
        this.topIndex = 101;

        MWF.UD.getData("noteList", function(json){
            if (json.data) {
                this.noteList = JSON.decode(json.data);
            }
            this.loadNotes();
            if (callback) callback();
        }.bind(this));
        this.addEvent("queryClose", function(){
            this.notes.each(function(note){ note.save(false); });
        }.bind(this));
        this.unloadSaveFun = function(){
            this.notes.each(function(note){ note.save(false); });
        }.bind(this);
        this.desktop.addEvent("unload", this.unloadSaveFun);
        this.addEvent("postClose", function(){
            this.desktop.removeEvent("unload", this.unloadSaveFun);
        }.bind(this));
	},
    loadNotes: function(){
        if (this.noteList.length){
            this.noteList.each(function(noteId){
                this.loadNote(noteId);
            }.bind(this));
        }else{
            this.loadNewNote();
        }
    },
    loadNote: function(id){
        this.notes.push(new MWF.xApplication.Note.NoteItem(this, id));
    },
    loadNewNote: function(where){
        this.notes.push(new MWF.xApplication.Note.NoteItem(this, "", where));
    }

});
MWF.xApplication.Note.NoteItem = new Class({
    Implements: [Events],
    initialize: function(note, id, where){
        this.note = note;
        this.css = this.note.css;
        this.id = id;
        this.load(where);
    },
    load: function(where){
        if (this.id){
            MWF.UD.getData(this.id, function(json){
                if (json.data) {
                    this.noteData = JSON.decode(json.data);
                    this.loadNode();
                }
            }.bind(this));
        }else{
            var d = (new Date()).getTime();
            this.id = "node"+d;
            var position = where;
            if (!position){
                var p = this.note.desktop.desktopNode.getPosition();
                var s = this.note.desktop.desktopNode.getSize();
                var y = p.y+10;
                var x = s.x-645;
                position = {"left": ""+x+"px", "top": ""+y+"px"};
            }
            this.noteData = {
                "id": this.id,
                "data": "",
                "position": position,
                "size": {"width": "200px", "height": "200px"}
            };
            this.loadNode();
        }
    },
    loadNode: function(){
        this.node = new Element("div", {"styles": this.css.itemNode}).inject(this.note.content);
        this.titleNode = new Element("div", {"styles": this.css.itemTitleNode}).inject(this.node);
        this.textarea = new Element("textarea", {"styles": this.css.itemTextarea}).inject(this.node);
        this.bottomNode = new Element("div", {"styles": this.css.itemBottomNode}).inject(this.node);

        this.addActionNode = new Element("div", {"styles": this.css.addActionNode}).inject(this.titleNode);
        this.closeActionNode = new Element("div", {"styles": this.css.closeActionNode}).inject(this.titleNode);

        this.resizeActionNode = new Element("div", {"styles": this.css.resizeActionNode}).inject(this.bottomNode);

        this.node.setStyles({
            "height": this.noteData.size.height,
            "width": this.noteData.size.width
        });
        Object.each(this.noteData.position, function(v, k){
            this.node.setStyle(k, v);
        }.bind(this));

        var x = this.noteData.size.width.toFloat()-10;
        var titleHeight = this.titleNode.getSize().y;
        var y = this.noteData.size.height.toFloat()-10-titleHeight-10-10;
        this.textarea.setStyles({
            "height": ""+y+"px",
            "width": ""+x+"px"
        });
        this.textarea.set("value", this.noteData.data);


        var drag = new Drag.Move(this.node, {
            "handle": this.titleNode,
            "container": this.note.desktop.desktopNode,
            "onDrop": function(){
                this.save();
                //var p = this.node.getPosition();
                //this.noteData.position = {
                //    "left": p.x,
                //    "top": p.y
                //}
            }.bind(this)
        });

        this.node.makeResizable({
            "handle": this.resizeActionNode,
            "stopPropagation": true,
            "preventDefault": true,
            "limit": {
                "x": [100, null],
                "y": [100, null]
            },
            "onDrag": function(){
                var s = this.node.getSize();
                this.noteData.size = {"width": ""+ s.x+"px", "height": ""+ s.y+"px"};
                var x = this.noteData.size.width.toFloat()-10;
                var titleHeight = this.titleNode.getSize().y;
                var y = this.noteData.size.height.toFloat()-10-titleHeight-10-10;
                this.textarea.setStyles({
                    "height": ""+y+"px",
                    "width": ""+x+"px"
                });
            }.bind(this),
            "onComplete": function(){
                this.save();
            }.bind(this)
        });

        this.setEvents();
    },
    setEvents: function(){
        var css = this.css;
        this.addActionNode.addEvents({
            "mouseover": function(){this.setStyles(css.addActionNode_over);},
            "mouseout": function(){this.setStyles(css.addActionNode);},
            "click": function(){
                this.addNote();
            }.bind(this)
        });
        this.closeActionNode.addEvents({
            "mouseover": function(){this.setStyles(css.closeActionNode_over);},
            "mouseout": function(){this.setStyles(css.closeActionNode);},
            "click": function(e){
                this.closeNote(e);
            }.bind(this)
        });
        this.textarea.addEvents({
            "change": function(){this.save();}.bind(this),
            "blur": function(){this.save();}.bind(this)
        });

        this.node.addEvent("click", function(){
            this.note.setCurrent();
            var count = this.note.notes.length;
            if (this.node.getStyle("z-index")<this.note.topIndex){
                this.node.setStyle("z-index", this.note.topIndex);
                this.note.topIndex++;
            }
        }.bind(this));
    },
    save: function(async){
        var asyncFlag = true;
        if (async===false) asyncFlag = false;
        this.noteData.data = this.textarea.get("value");
        var p = this.node.getPosition();
        var s = this.node.getSize();
        this.noteData.position = {"left": ""+ p.x+"px", "top": ""+ p.y+"px"};
        this.noteData.size = {"width": ""+ s.x+"px", "height": ""+ s.y+"px"};

        if (this.noteData.data){
            MWF.UD.putData(this.id, this.noteData, function(json){
                if (this.note.noteList.indexOf(this.id)==-1){
                    this.note.noteList.push(this.id);
                    MWF.UD.putData("noteList", this.note.noteList);
                }
            }.bind(this), asyncFlag);
        }
    },

    addNote: function(){

        var s = this.note.desktop.desktopNode.getSize();
        var p = this.node.getPosition();
        var x = p.x-205;
        var y = p.y;
        if (x<0) x = 0;
        if (x>s.x) x = s.x;
        if (y<0) y = 0;
        if (y>s.y) y = s.y;

        this.note.loadNewNote({"left": ""+x+"px", "top": ""+y+"px"});
    },

    closeNote: function(e){
        var _self = this;
        this.note.confirm("warn", e, this.note.lp.deleteNoteTitle, this.note.lp.deleteNote, 300, 120, function(){
            MWF.UD.deleteData(this.id, function(json){
                this.note.notes.erase(this);
                this.note.noteList.erase(this.id);
                MWF.UD.putData("noteList", this.note.noteList);
                this.node.destroy();
                if (!this.note.notes.length) this.note.close();
                MWF.release(this);
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        });
    }


});
