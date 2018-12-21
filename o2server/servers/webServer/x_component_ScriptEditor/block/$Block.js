MWF.xApplication.ScriptEditor.block = MWF.xApplication.ScriptEditor.block || {};
MWF.xApplication.ScriptEditor.block.$Block = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    _loadPath: function(){
        this.path = "/x_component_ScriptEditor/block/$Block/";
        this.cssPath = "/x_component_ScriptEditor/block/$Block/"+this.options.style+"/css.wcss";
    },
    initialize: function(data, type, options){
        this.setOptions(options);
        this._loadPath();
        this._loadCss();

        this.data = data;
        this.type = type;
        this.editor = this.type.editor;
        this.areaNode = this.type.blocksNode;
        this.scriptArea = this.editor.scriptArea;
        this.scriptAreaNode = this.editor.scriptArea.blockArea;
        this.load();
    },
    init: function(){
        this.before = true;
        this.after = true;
        this.around = false;
        this.blockName = ""
    },
    load: function(){
        this.init();
        this.createBlock();
        this.setEvent();
    },
    setEvent: function(){
        var drag = new Drag(this.node, {
            "snap": "1",
            "onStart": function(dragging, e){
                this.dragScript(e);
                drag.stop();
            }.bind(this)
        });

        // this.node.addEvent("drag", function(e){
        //     this.dragScript(e);
        // }.bind(this));
    },
    createDragNode: function(){
        var dragNode = this.node.clone();
        dragNode.setStyle("position", "absolute").inject(this.scriptAreaNode);
        dragNode.position({
            relativeTo: this.node,
            position: 'upperLeft',
            edge: 'upperLeft'
        });
        //dragNode.setStyle("z-index", MWF.SES.zIndexPool.apply());

        var canvas = this.node.getElements("canvas");
        var dragCanvas = dragNode.getElements("canvas");
        if (canvas.length){
            canvas.each(function(c, i){
                var dc = dragCanvas[i];
                var draw = c.retrieve("draw");
                if (draw) this[draw](dc);
            }.bind(this));
        }
        return dragNode;
    },
    dragScript: function(e){
        var dragNode = this.createDragNode();

        var droppables = [this.scriptAreaNode].concat(this.scriptArea.statementNodes);

        var nodeDrag = new Drag.Move(dragNode, {
            "droppables": droppables,
            "onStart": function(dragging){
                this.dragStart(dragging);
            }.bind(this),
            "onEnter": function(dragging, inObj){

            }.bind(this),
            "onLeave": function(dragging, inObj){

            }.bind(this),
            "onDrag": function(){
                var statement = dragNode.retrieve("statement");
                this.scriptArea.checkBlockDrag(dragNode, statement, statement.links);
            }.bind(this),
            "onDrop": function(dragging, inObj){

            }.bind(this),
            "onComplete": function(e, event){
                this.dragComplete(event, dragNode, nodeDrag);
            }.bind(this),
            "onCancel": function(){
                var statement = dragNode.retrieve("statement");
                if (statement){ statement.destroy(); }else{ dragNode.destroy(); }
            }.bind(this)
        });
        nodeDrag.start(e);
    },
    dragStart: function(dragNode){
        dragNode.setStyle("z-index", MWF.SES.zIndexPool.apply());
        var statement = this.scriptArea.createStatement(dragNode, this);
        dragNode.store("statement", statement);
    },
    dragComplete: function(event, dragNode, nodeDrag){
        var statement = dragNode.retrieve("statement");
        if (this.scriptAreaNode.isOutside(event)){
            if (statement){ statement.destroy(); }else{ dragNode.destroy(); }
        }else{
            var p0 = dragNode.getPosition(dragNode.getOffsetParent());
            var p1 = this.scriptAreaNode.getPosition(dragNode.getOffsetParent());
            if (p0.x-p1.x<10){
                var x = 0+p1.x+10;
                dragNode.setStyle("left", ""+x+"px");
            }
            if (p0.y-p1.y<10){
                var y = 0+p1.y+10;
                dragNode.setStyle("top", ""+y+"px");
            }
            if (statement){
                this.scriptArea.buildStatement(statement);
            }else{
                statement = this.scriptArea.createStatement(dragNode, this);
                dragNode.store("statement", statement);
                this.scriptArea.buildStatement(statement);
            }
            nodeDrag.detach();
        }
    }
});
MWF.xApplication.ScriptEditor.block.$Block.$Operation = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block,
    init: function(){
        var css = Object.clone(this.css.block);
        this.blockCss = Object.merge(css, this.css.operation);
        this.blockName = this.data.name;
    },
    createContent: function(){
        this.node = new Element("div", {"styles": this.blockCss.node}).inject(this.areaNode);
        this.contentNode = new Element("div", {"styles": this.blockCss.contentNode}).inject(this.node);
        this.contentNode.setStyle("background", this.type.data.color);
    },
    loadContent: function(){
        this.table = new Element("table", {
            "styles": this.blockCss.contentTable,
            "border": "0",
            "cellpadding": "0",
            "cellspacing": "0"
        });
        var tr = new Element("tr").inject(this.table);
        this.data.contents.each(function(content){
            var td = new Element("td", {"styles": this.blockCss.contentTdNode}).inject(tr);
            if (typeOf(content)==="string"){
                td.set("text", content);
            }else if (typeOf(content)==="object"){
                td.set("class", "MWFBlockContent_"+content.type);
                this.createContentNode(content).inject(td);
            }
        }.bind(this));
        this.table.inject(this.contentNode);
    },
    createBlock: function(){
        this.createContent();
        this.loadContent();
    },
    createContentNode: function(content){
        if (content.type==="input"){
            return this.createInputNode(content);
        }
        if (content.type==="select"){
            return this.createSelectNode(content);
        }
        if (content.type==="mortise"){
            return this.createMortiseNode(content);
        }
        if (content.type==="inputMortise"){
            return this.createInputMortiseNode(content);
        }
    },
    createInputNode: function(content){
        var text = content.default;
        var width = MWF.getTextSize(text, {"font-size": "12px"}).x;
        var node = new Element("input", {"styles": this.blockCss.inputNode, "value": text, "readonly": true});
        node.setStyle("width", ""+width+"px");
        return node;
    },
    createSelectNode: function(content){
        var select = new Element("select", {"styles": this.blockCss.selectNode, "class": "script"});
        content.options.each(function(t){
            var v = t.split("|");
            new Element("option", {
                "value": v[1],
                "text": v[0],
                "selected": (content.default===v[1])
            }).inject(select);
        }.bind(this));
        select.setStyle("background", this.type.data.mortiseColor);
        return select;
    },
    createMortiseNode: function(){
        var node = new Element("div", {"styles": this.blockCss.mortiseNode});
        node.setStyle("background", this.type.data.mortiseColor);
        return node;
    },
    createInputMortiseNode: function(content){
        var text = content.default;
        var width = MWF.getTextSize(text, {"font-size": "12px"}).x;
        var node = new Element("input", {"styles": this.blockCss.inputMortiseNode, "value": text, "readonly": true});
        node.setStyle("width", ""+width+"px");
        return node;
    }
});
MWF.xApplication.ScriptEditor.block.$Block.$Expression = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation,
    init: function(){
        var css = Object.clone(this.css.block);
        this.blockCss = Object.merge(css, this.css.expression);
        this.blockName = this.data.name;
    }
});
MWF.xApplication.ScriptEditor.block.$Block.$Enumerate = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation,
    init: function(){
        var css = Object.clone(this.css.block);
        this.blockCss = Object.merge(css, this.css.enumerate);
        this.blockName = this.data.name;
    }
});
MWF.xApplication.ScriptEditor.block.$Block.$Top = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation,
    init: function(){
        var css = Object.clone(this.css.block);
        this.blockCss = Object.merge(css, this.css.top);
        this.blockName = this.data.name;
    },
    createContent: function(){
        this.node = new Element("div", {"styles": this.blockCss.node}).inject(this.areaNode);
        this.topNode = new Element("div", {"styles": this.blockCss.topNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.blockCss.contentNode}).inject(this.node);
        this.contentNode.setStyle("background", this.type.data.color);
        this.topNode.setStyle("background", this.type.data.color);
    }
});

MWF.xApplication.ScriptEditor.block.$Block.$Around = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation,
    init: function(){
        var css = Object.clone(this.css.block);
        this.blockCss = Object.merge(css, this.css.around);
        this.blockName = this.data.name;
    },
    createBlock: function(){
        this.node = new Element("div", {"styles": this.blockCss.node}).inject(this.areaNode);
        this.createBeginNode();
        this.createMiddleNode();
        this.createEndNode();
    },
    createBeginNode: function(){
        this.beginNode = new Element("div", {"styles": this.blockCss.beginNode}).inject(this.node);
        this.beginContentNode = new Element("div", {"styles": this.blockCss.beginContentNode}).inject(this.beginNode);
        this.beginContentNode.setStyle("background", this.type.data.color);

        this.contentNode = this.beginContentNode;
        this.loadContent();
    },

    createMiddleNode: function(){
        this.middleNode = new Element("div", {"styles": this.blockCss.middleNode}).inject(this.node);
        var html = "<table border='0' cellspacing='0' cellpadding='0'><tr><td valign='top'></td><td valign='top'></td></tr></table>";
        this.middleNode.set("html", html);
        this.tableNode = this.middleNode.getElement("table").setStyles(this.blockCss.middleTableNode);
        var tds = this.tableNode.getElements("td");
        this.middleLeftNode = tds[0].setStyles(this.blockCss.middleLeftNode);
        this.middleRightNode = tds[1].setStyles(this.blockCss.middleRightNode);
        this.subContentNode = new Element("div", {"styles": this.blockCss.subContentNode}).inject(this.middleRightNode);

        this.middleLeftNode.setStyle("background", this.type.data.color);
    },

    createEndNode: function(){
        this.endNode = new Element("div", {"styles": this.blockCss.endNode}).inject(this.node);
        this.endContentNode = new Element("div", {"styles": this.blockCss.endContentNode}).inject(this.endNode);
        this.endContentNode.setStyle("background", this.type.data.color);
    }
    // createContent: function(){
    //     this.node = new Element("div", {"styles": this.css.node}).inject(this.areaNode);
    //     this.topNode = new Element("div", {"styles": this.css.topNode}).inject(this.node);
    //     this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
    //     this.contentNode.setStyle("background", this.type.data.color);
    //     this.topNode.setStyle("background", this.type.data.color);
    // }
});
MWF.xApplication.ScriptEditor.block.$Block.$EnumerateAround = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around,
    init: function(){
        var css = Object.clone(this.css.block);
        css = Object.merge(css, this.css.around);
        this.blockCss = Object.merge(css, this.css.enumerateAround);
        this.blockName = this.data.name;
    }
});
