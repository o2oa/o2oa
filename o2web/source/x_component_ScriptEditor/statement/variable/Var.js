MWF.xApplication.ScriptEditor.statement.variabler.Var = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
    // Extends: MWF.xApplication.ScriptEditor.statement.$Statement,
    // Implements: [Options, Events],
    //
    // parseNodes: function(){
    //     this.contentNode = this.node.getFirst();
    //     this.inputNode = this.node.getElement("input");
    //     this.inputAreaNode = this.inputNode.getParent();
    //
    //     this.nameInput = this.node.getElement("input");
    //     this.typeSelect = this.node.getElement("select");
    //
    //     this.downLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.node);
    //     this.upLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_up}).inject(this.node, "top");
    //
    //     this.downLink =  new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.downLinkStatementNode);
    //     this.upLink =  new MWF.xApplication.ScriptEditor.statement.Link(this, "up", this.upLinkStatementNode);
    //
    //     this.links.push(this.upLink);
    //     this.links.push(this.downLink);
    //     this.topLink = this.upLink;
    //     this.bottomLink = this.downLink;
    // },
    // buildStatement: function(){
    //     this.setInputNode();
    //     this.setSelectNode();
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
    //             var width = MWF.getTextSize(this.nameInput.get("value")).x;
    //             this.nameInput.setStyle("width", ""+width+"px");
    //             e.stopPropagation();
    //         }.bind(this)
    //     });
    // },
    // setSelectNode: function(){
    //     this.typeSelect.addEvents({
    //         "mousedown": function(e){
    //             e.stopPropagation();
    //         }.bind(this)
    //     });
    // },
    //
    // reportLinks: function(){
    //     this.area.links.include(this.downLink.rePosition());
    //     this.area.links.include(this.upLink.rePosition());
    // },
    //
    // setLinkStyle: function(){
    //
    // }

});