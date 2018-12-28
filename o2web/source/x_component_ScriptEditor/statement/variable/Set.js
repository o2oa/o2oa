MWF.xApplication.ScriptEditor.statement.variabler.Set = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
    // Extends: MWF.xApplication.ScriptEditor.statement.$Statement,
    // Implements: [Options, Events],
    //
    // parseNodes: function () {
    //     this.contentNode = this.node.getFirst();
    //     this.table = this.contentNode.getElement("table");
    //
    //     this.valueInput = this.table.getElement("input");
    //     this.nameMortiseNode = this.table.getElements("td")[1].getFirst().getFirst();
    //
    //     this.downLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.node);
    //     this.upLinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_up}).inject(this.node, "top");
    //
    //     this.downLink = new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.downLinkStatementNode);
    //     this.upLink = new MWF.xApplication.ScriptEditor.statement.Link(this, "up", this.upLinkStatementNode);
    //
    //     this.links.push(this.upLink);
    //     this.links.push(this.downLink);
    //     this.topLink = this.upLink;
    //     this.bottomLink = this.downLink;
    // },
    // buildStatement: function () {
    //     this.setInputNode();
    //     this.nameMortise = new MWF.xApplication.ScriptEditor.statement.Mortise(this, this.nameMortiseNode.getParent(), this.block.data.nameTenonTypes, this.nameMortiseNode);
    //     this.valueMortise = new MWF.xApplication.ScriptEditor.statement.Mortise(this, this.valueInput.getParent(), this.block.data.valueTenonTypes, this.valueInput);
    //
    //     this.area.mortises.push(this.nameMortise);
    //     this.mortises.push(this.nameMortise);
    //     this.area.mortises.push(this.valueMortise);
    //     this.mortises.push(this.valueMortise);
    // },
    // setInputNode: function () {
    //     this.valueInput.set("readonly", false);
    //     this.valueInput.setStyle("max-width", "none");
    //
    //     this.valueInput.addEvents({
    //         "mousedown": function (e) {
    //             this.valueInput.focus();
    //             e.stopPropagation();
    //         }.bind(this),
    //         "keyup": function (e) {
    //             var width = MWF.getTextSize(this.valueInput.get("value")).x;
    //             this.valueInput.setStyle("width", "" + width + "px");
    //             e.stopPropagation();
    //         }.bind(this)
    //     });
    // },
    //
    // reportLinks: function () {
    //     this.area.links.include(this.downLink.rePosition());
    //     this.area.links.include(this.upLink.rePosition());
    // },
    //
    // setLinkStyle: function () {
    //
    // }

});