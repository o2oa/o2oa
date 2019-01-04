MWF.xApplication.ScriptEditor.statement.variable.Variable = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
    // Implements: [Options, Events],
    //
    // parseNodes: function(){
    //     this.contentNode = this.node.getFirst();
    //     this.nameInput = this.node.getElement("input");
    //
    //     // this.downLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.node);
    //     // this.upLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_up}).inject(this.node, "top");
    //     this.statementType = "expression";
    //
    //     // this.downLink =  new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.downLinkStatementNode);
    //     // this.upLink =  new MWF.xApplication.ScriptEditor.statement.Link(this, "up", this.upLinkStatementNode);
    //     //
    //     // this.links.push(this.upLink);
    //     // this.links.push(this.downLink);
    //     // this.topLink = this.upLink;
    //     // this.bottomLink = this.downLink;
    // },
    // buildStatement: function(){
    //     this.setInputNode();
    // },
    // setInputNode: function(){
    //     this.nameInput.set("readonly", false);
    //     this.nameInput.setStyle("max-width", "none");
    //
    //     this.nameInput.addEvents({
    //         "mousedown": function(e){
    //             this.nameInput.focus();
    //             e.stopPropagation();
    //         }.bind(this),
    //         "keyup": function(e){
    //             var width = MWF.getTextSize(this.valueInput.get("value")).x;
    //             this.nameInput.setStyle("width", ""+width+"px");
    //             e.stopPropagation();
    //         }.bind(this)
    //     });
    // },
    //
    // reportLinks: function(){
    //     // this.area.links.include(this.downLink.rePosition());
    //     // this.area.links.include(this.upLink.rePosition());
    // },
    //
    // setLinkStyle: function(){
    //
    // }
});