MWF.xApplication.ScriptEditor.statement = MWF.xApplication.ScriptEditor.statement || {};
MWF.xApplication.ScriptEditor.statement.form = MWF.xApplication.ScriptEditor.statement.form || {};
MWF.xDesktop.requireApp("ScriptEditor", "statement.$Statement", null, false);
MWF.xApplication.ScriptEditor.statement.form.Get = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement,
    Implements: [Options, Events],

    parseNodes: function(){
        this.contentNode = this.node.getFirst();
        this.inputNode = this.node.getElement("input");
        this.inputAreaNode = this.inputNode.getParent();

        this.downLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.node);
        this.upLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_up}).inject(this.node, "top");

        this.downLink =  new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.downLinkStatementNode);
        this.upLink =  new MWF.xApplication.ScriptEditor.statement.Link(this, "up", this.upLinkStatementNode);

        this.links.push(this.upLink);
        this.links.push(this.downLink);
        this.topLink = this.upLink;
        this.bottomLink = this.downLink;
    },
    buildStatement: function(){
        this.setInputNode();
    },
    setInputNode: function(){
        this.inputNode.set("readonly", false);
        this.inputNode.setStyle("max-width", "none");

        this.inputNode.addEvents({
            "mousedown": function(e){
                this.inputNode.focus();
                e.stopPropagation();
            }.bind(this),
            "keyup": function(e){
                var width = MWF.getTextSize(this.inputNode.get("value"), {"font-size": "12px"}).x;
                this.inputNode.setStyle("width", ""+width+"px");
                e.stopPropagation();
            }.bind(this)
        });
    },

    reportLinks: function(){
        this.area.links.include(this.downLink.rePosition());
        this.area.links.include(this.upLink.rePosition());
    },

    setLinkStyle: function(){

    }

});