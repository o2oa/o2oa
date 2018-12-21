MWF.xApplication.ScriptEditor.statement.control.DefineFunction = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Top
    // Implements: [Options, Events],
    //
    // parseNodes: function(){
    //     var divs = this.node.getElements("div");
    //     this.topNode = divs[0];
    //     this.contentNode = divs[1];
    //     this.inputNode = this.node.getElement("input");
    //     this.inputAreaNode = this.inputNode.getParent();
    //
    //     this.linkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.node);
    //     this.link =  new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.linkStatementNode);
    //     this.links.push(this.link);
    //     this.topLink = null;
    //     this.bottomLink = this.link;
    // },
    // buildStatement: function(){
    //     this.setInputNode();
    // },
    // setInputNode: function(){
    //     debugger;
    //     this.inputNode.set("readonly", false);
    //     this.inputNode.setStyle("max-width", "none");
    //
    //     this.inputNode.addEvents({
    //         "mousedown": function(e){
    //             this.inputNode.focus();
    //             e.stopPropagation();
    //         }.bind(this),
    //         "keyup": function(e){
    //             var width = MWF.getTextSize(this.inputNode.get("value"), {"font-size": "12px"}).x;
    //             debugger;
    //             this.inputNode.setStyle("width", ""+width+"px");
    //             e.stopPropagation();
    //         }.bind(this)
    //     });
    // },
    //
    // reportLinks: function(){
    //     this.area.links.include(this.link.rePosition());
    // },
    // readyLink: function(link){
    //     this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(this.linkStatementNode, "top");
    // },
    // notReadyLink: function(link){
    //     if (this.readyLinkNode) this.readyLinkNode.destroy();
    // },
    //
    // readyLinkTo: function(link){
    //     this.readyLinkToNode = new Element("div", {"styles": this.css.readyLinkToNode}).inject(this.linkStatementNode, "top");
    // },
    // notReadyLinkTo: function(link){
    //     if (this.readyLinkToNode) this.readyLinkToNode.destroy();
    // },
    // setLinkStyle: function(){
    //
    // }

});