MWF.xApplication.ScriptEditor.statement = MWF.xApplication.ScriptEditor.statement || {};
MWF.xApplication.ScriptEditor.statement.$Statement = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    _loadPath: function(){
        this.path = "/x_component_ScriptEditor/statement/$Statement/";
        this.cssPath = "/x_component_ScriptEditor/statement/$Statement/"+this.options.style+"/css.wcss";
    },
    initialize: function(node, block, area, options){
        this.setOptions(options);
        this._loadPath();
        this._loadCss();

        this.node = node;
        this.area = area;
        this.block = block;
        this.editor = this.area.editor;
        this.areaNode = this.area.blockArea;
        this.subStatements = [];
        this.links = [];
        this.mortises = [];
        this.inputs = [];
        this.selectors = [];
        this.mortises = [];

        this.init();
    },
    init: function(){
        this.statementType = "operation";
        this.parseNodes();
    },
    reBuild: function(){
        if (this.area.currentLink){
            this.linkTo(this.area.currentLink);
            this.setPosition(this.area.currentLink);
        }else{
            if (this.topLink){
                this.setPosition({
                    "link": this.topLink,
                    "toLink": this.topLink.toLink
                });
            }
        }
    },
    buildStatement: function(){},
    load :function(){
        this.buildStatement();
        if (!this.checkAvailable()) return false;
        this.node.setStyles(this.css.node);
        if (this.area.currentLink) this.linkTo(this.area.currentLink);
        if (this.area.currentLink) this.setPosition(this.area.currentLink);
        this.reportLinks();


        var drag = new Drag(this.node, {
            "snap": "1",
            "stopPropagation": true,
            "onStart": function(dragging, e){
                this.readyDrag();
                this.setDrag(e);
            }.bind(this)
        });

        // this.node.addEvent("mousedown", function(e){
        //     this.readyDrag();
        //     this.setDrag(e);
        //     e.stopPropagation();
        // }.bind(this));
        this.area.currentLink = null;
        this.area.setAreaSize();
    },
    checkAvailable: function(){
        if (this.topLink){
            if (!this.area.currentLink){
                this.destroy();
                return false;
            }
        }else{
            this.area.beginStatements.push(this);
        }
        return true;
    },

    setDrag: function(e){
        this.drag = new Drag.Move(this.node, {
            "stopPropagation": true,
            "droppables": [this.areaNode],
            "onStart": function(dragging){
                this.dragStart(dragging);
            }.bind(this),
            "onEnter": function(dragging, inObj){

            }.bind(this),
            "onLeave": function(dragging, inObj){

            }.bind(this),
            "onDrag": function(e){
                var links = this.getConnectedLinks();
                this.area.checkBlockDrag(this.node, this, links);
            }.bind(this),
            "onDrop": function(dragging, inObj){

            }.bind(this),
            "onComplete": function(e, event){
                this.dragComplete(event, this.node, this.drag);
                if (this.area) this.area.setAreaSize();
            }.bind(this),
            "onCancel": function(dragging){
                this.area.currentLink = null;
                this.area.currentMortise = null;
                this.reBuild();
            }.bind(this)
        });
        this.drag.start(e);
        e.stopPropagation();
    },
    readyDrag: function(){
        //var p = this.node.getPosition(this.node.getOffsetParent());
        var p = this.node.getPosition(this.areaNode);
        this.node.inject(this.areaNode);

        this.node.setStyles({
            "z-index": MWF.SES.zIndexPool.apply(),
            "position": "absolute",
            "left": ""+p.x+"px",
            "top": ""+p.y+"px"
        });
    },
    getConnectedLinks: function(){
        var links = this.links;
        if (this.centerLink){
            if (this.centerLink.toLink){
                links = links.concat(this.centerLink.toLink.statement.getConnectedLinks());
            }
        }
        if (this.bottomLink){
            if (this.bottomLink.toLink){
                links = links.concat(this.bottomLink.toLink.statement.getConnectedLinks());
            }
        }
        return links;
    },
    dragStart: function(dragNode){
        //this.readyDrag();
        //dragNode.setStyle("z-index", MWF.SES.zIndexPool.apply());
        //this.node.setStyles({"position": "absolute"});
    },
    dragComplete: function(event, dragNode, nodeDrag){
        var statement = dragNode.retrieve("statement");
        if (!this.editor.moduleAreaNode.isOutside(event)){
            this.destroy();
        }else{
            var p0 = dragNode.getPosition(dragNode.getOffsetParent());
            var p1 = this.areaNode.getPosition(this.node.getOffsetParent());
            if (p0.x-p1.x<10){
                var x = 0+p1.x+10;
                dragNode.setStyle("left", ""+x+"px");
            }
            if (p0.y-p1.y<10){
                var y = 0+p1.y+10;
                dragNode.setStyle("top", ""+y+"px");
            }
            this.reBuild();
        }
    },
    linkTo: function(link){
        if (link.linkType==="up-down"){
            link.toLink.linkDown(link.link);
        }
        if (link.linkType==="down-up"){
            link.toLink.linkUp(link.link);
        }
        if (link.linkType==="up-middle"){
            link.toLink.linkMiddle(link.link);
        }
        if (link.linkType==="middle-up"){
            link.toLink.linkUpAround(link.link);
        }
    },
    setPosition: function(link){
        this.node.inject(link.toLink.node, "top");

        if (link.link.statement.centerLink){
            if (link.link.statement.centerLink.toLink){
                link.link.statement.centerLink.toLink.statement.node.inject(link.link.statement.centerLink.node, "top");
            }
        }
        var bottomLink = link.link.statement.bottomLink;
        while (bottomLink && bottomLink.toLink){
            bottomLink.toLink.statement.node.inject(bottomLink.node, "top");
            bottomLink = bottomLink.toLink.statement.bottomLink;
        }


        if (link.link.statement.bottomLink){
            if (link.link.statement.bottomLink.toLink){
                link.link.statement.bottomLink.toLink.statement.node.inject(link.link.statement.bottomLink.node, "top");
            }
        }

        this.node.setStyles({"position": "static", "top": "auto", "left": "auto"});
        link.toLink.statement.notReadyLink();
        link.link.statement.notReadyLinkTo();
    },


    readyLink: function(link){
        if (link.toLink.type==="up") this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(link.toLink.node, "bottom");
        if (link.toLink.type==="middle") this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(link.toLink.node, "top");
        if (link.toLink.type==="down") this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(link.toLink.node, "top");
    },
    notReadyLink: function(link){
        if (this.readyLinkNode) this.readyLinkNode.destroy();
    },

    readyLinkTo: function(link){
        if (link.link.type==="up") this.readyLinkToNode = new Element("div", {"styles": this.css.readyLinkToNode}).inject(link.link.node, "bottom");
        if (link.link.type==="middle") this.readyLinkToNode = new Element("div", {"styles": this.css.readyLinkToNode}).inject(link.link.node, "top");
        if (link.link.type==="down") this.readyLinkToNode = new Element("div", {"styles": this.css.readyLinkToNode}).inject(link.link.node, "top");
    },
    notReadyLinkTo: function(link){
        if (this.readyLinkToNode) this.readyLinkToNode.destroy();
    },

    normal: function(){
        if (this.tableNode){
            this.tableNode.setStyle("height", "auto");
        }
    },

    destroy: function(){
        if (this.topLink){
            if (this.topLink.toLink){
                this.topLink.toLink.toLink = null;
            }
        }
        if (this.centerLink){
            if (this.centerLink.toLink){
                this.centerLink.toLink.statement.destroy();
            }
        }
        if (this.bottomLink){
            if (this.bottomLink.toLink){
                this.bottomLink.toLink.statement.destroy();
            }
        }
        this.links.each(function(link){
            this.area.links.erase(link);
        }.bind(this));
        this.mortises.each(function(mortise){
            this.area.mortises.erase(mortise);
        }.bind(this));

        this.area.statementNodes.erase(this.node);
        this.area.statements.erase(this);
        this.area.clearCurrentLink();
        this.node.destroy();
        if (this.area) this.area.setAreaSize();
        MWF.release(this);
    },
    setLinkStyle: function(){}
});

MWF.xApplication.ScriptEditor.statement.$Statement.$Operation = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement,
    parseNodes: function(){
        this.contentNode = this.node.getFirst();
        this.table = this.contentNode.getElement("table");
        this.createLinks();
        this.loadContents();
    },
    createLinks: function(){
        this.downLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.node);
        this.upLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_up}).inject(this.node, "top");

        this.downLink =  new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.downLinkStatementNode);
        this.upLink =  new MWF.xApplication.ScriptEditor.statement.Link(this, "up", this.upLinkStatementNode);

        this.links.push(this.upLink);
        this.links.push(this.downLink);
        this.topLink = this.upLink;
        this.bottomLink = this.downLink;
    },
    loadContents: function(){
        this.loadInputs();
        this.loadSelectors();
        this.loadMortises();
        this.loadInputMortises();
    },
    loadInputs: function(){
        var inputs = this.node.getElements(".MWFBlockContent_input");
        inputs.each(function(input){
            var inputNode = input.getElement("input");
            inputNode.set("readonly", false);
            inputNode.setStyle("max-width", "none");
            inputNode.addEvents({
                "mousedown": function(e){e.stopPropagation();},
                "keyup": function(e){
                    var width = MWF.getTextSize(this.get("value")).x;
                    this.setStyle("width", ""+width+"px");
                    e.stopPropagation();
                }
            });
            this.inputs.push(input.getFirst());
        }.bind(this));
    },
    loadSelectors: function(){
        var selectors = this.node.getElements(".MWFBlockContent_select");
        selectors.each(function(selector){
            selector.getFirst().addEvents({"mousedown": function(e){e.stopPropagation();}.bind(this)});
            this.selectors.push(selector.getFirst());
        }.bind(this));
    },
    loadMortises: function(){
        var mortises = this.node.getElements(".MWFBlockContent_mortise");
        mortises.each(function(mortiseNode){
            var content = this.block.data.contents[mortiseNode.cellIndex];
            var mortise = new MWF.xApplication.ScriptEditor.statement.Mortise(this, mortiseNode, content.tenonTypes, mortiseNode.getFirst());
            this.mortises.push(mortise);
            this.area.mortises.push(mortise);
        }.bind(this));
    },
    loadInputMortises: function(){
        var inputMortises = this.node.getElements(".MWFBlockContent_inputMortise");
        inputMortises.each(function(inputMortiseNode){
            var inputNode = inputMortiseNode.getElement("input");
            inputNode.set("readonly", false);
            inputNode.setStyle("max-width", "none");
            inputNode.addEvents({
                "mousedown": function(e){e.stopPropagation();},
                "keyup": function(e){
                    var width = MWF.getTextSize(this.get("value")).x;
                    this.setStyle("width", ""+width+"px");
                    e.stopPropagation();
                }
            });

            var content = this.block.data.contents[inputMortiseNode.cellIndex];
            var inputMortise = new MWF.xApplication.ScriptEditor.statement.Mortise(this, inputMortiseNode, content.tenonTypes, inputMortiseNode.getFirst());
            this.mortises.push(inputMortise);
            this.area.mortises.push(inputMortise);
        }.bind(this));
    },
    buildStatement: function(){},
    reportLinks: function(){
        this.area.links.include(this.downLink.rePosition());
        this.area.links.include(this.upLink.rePosition());
    }
});
MWF.xApplication.ScriptEditor.statement.$Statement.$Expression = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation,
    createLinks: function(){
        this.tenon = this.node;
    },
    reBuild: function(){
        if (this.area.currentMortise){
            this.tenonTo(this.area.currentMortise);
            this.setPosition(this.area.currentMortise);
        }else{
            if (this.toMortise){
                this.tenonTo(this.toMortise);
                this.setPosition(this.toMortise);
            }else{
                this.destroy();
            }
        }
    },
    load :function(){
        this.buildStatement();
        if (!this.checkAvailable()) return false;
        this.node.setStyles(this.css.node);
        if (this.area.currentMortise){
            this.tenonTo(this.area.currentMortise);
            this.setPosition(this.area.currentMortise);
        }
        this.node.addEvent("mousedown", function(e){
            this.readyDrag();
            this.setDrag(e);
            e.stopPropagation();
        }.bind(this));
        this.area.currentMortise = null;
    },
    checkAvailable: function(){
        if (!this.area.currentMortise){
            this.destroy();
            return false;
        }
        return true;
    },
    tenonTo: function(mortise){
        if (this.toMortise){
            this.toMortise.tenonStatement = null;
        }
        mortise.tenonStatement = this;
        this.toMortise = mortise;
    },
    setPosition: function(mortise){
        mortise.unshine();
        mortise.tenon();

    },
    readyDrag: function(){
        this.toMortise.split();
    },
});
MWF.xApplication.ScriptEditor.statement.$Statement.$Enumerate = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.$Statement.$Top = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation,
    parseNodes: function(){
        var divs = this.node.getElements("div");
        this.topNode = divs[0];
        this.contentNode = divs[1];
        this.table = this.contentNode.getElement("table");
        this.createLinks();
        this.loadContents();
    },
    createLinks: function(){
        this.linkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.node);
        this.link =  new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.linkStatementNode);
        this.links.push(this.link);
        this.topLink = null;
        this.bottomLink = this.link;
    },
    reportLinks: function(){
        this.area.links.include(this.link.rePosition());
    },
    readyLink: function(link){
        this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(this.linkStatementNode, "top");
    },
    readyLinkTo: function(link){
        this.readyLinkToNode = new Element("div", {"styles": this.css.readyLinkToNode}).inject(this.linkStatementNode, "top");
    }
});

MWF.xApplication.ScriptEditor.statement.$Statement.$Around = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation,
    parseNodes: function(){
        this.beginNode = this.node.getFirst();
        this.beginContentNode = this.beginNode.getFirst();
        this.table = this.beginContentNode.getElement("table");
        this.contentNode = this.beginContentNode;

        this.middleNode = this.beginNode.getNext();
        this.tableNode = this.middleNode.getElement("table");
        var tds = this.tableNode.getElements("td");
        this.middleLeftNode = tds[0];
        this.middleRightNode = tds[1];
        this.subContentNode = this.middleRightNode.getFirst();

        this.endNode = this.middleNode.getNext();
        this.endContentNode = this.endNode.getFirst();
        this.conditionNode = this.beginContentNode.getFirst().getNext();

        this.createLinks();
        this.loadContents();
    },
    createLinks: function(){
        this.beginUplinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_up}).inject(this.beginNode, "before");
        this.middlelinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.subContentNode);
        this.endDownlinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.endNode, "after");

        this.upLink = new MWF.xApplication.ScriptEditor.statement.Link(this, "up", this.beginUplinkStatementNode);
        this.middleLink = new MWF.xApplication.ScriptEditor.statement.Link(this, "middle", this.middlelinkStatementNode);
        this.downLink = new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.endDownlinkStatementNode);

        this.links.push(this.upLink);
        this.links.push(this.middleLink);
        this.links.push(this.downLink);
        this.topLink = this.upLink;
        this.centerLink = this.middleLink;
        this.bottomLink = this.downLink;
    },
    reportLinks: function(){
        this.area.links.include(this.upLink.rePosition());
        this.area.links.include(this.middleLink.rePosition());
        this.area.links.include(this.downLink.rePosition());
    },
    readyLink: function(link){
        if (link.linkType==="up-down" || link.linkType==="up-middle"){
            this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(link.toLink.node, "top");
        }
        if (link.linkType==="down-up" || link.linkType==="middle-up"){
            this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(link.toLink.node, "bottom");
        }
    },
    readyLinkTo: function(link){
        if (link.linkType==="up-down" || link.linkType==="up-middle"){
            this.readyLinkToNode = new Element("div", {"styles": this.css.readyLinkToNode}).inject(link.link.node, "bottom");
            if (link.link.statement.centerLink && !link.link.statement.centerLink.toLink){
                if (link.toLink.toLink){
                    if (link.toLink.toLink.statement!==this){
                        var height = link.toLink.toLink.statement.node.getSize().y;
                        this.tableNode.setStyle("height", ""+height+"px");
                    }
                }
            }
        }
    },
    notReadyLinkTo: function(link){
        if (this.readyLinkToNode) this.readyLinkToNode.destroy();
        if (link){
            if (link.link.type==="middle"){
                this.tableNode.setStyle("height", "auto");
            }
        }else{
            this.tableNode.setStyle("height", "auto");
        }
    },
    getStatementGroupHeight: function(group){
        var first = group[0];
        var last = group[group.length-1];
        var topP = first.node.getPosition(this.areaNode);
        var bottomP = last.node.getPosition(this.areaNode);
        var bottomSize = last.node.getSize();

        return bottomP.y+bottomSize.y-topP.y
    }
});

MWF.xApplication.ScriptEditor.statement.$Statement.$EnumerateAround = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Around
});

