MWF.xApplication.ScriptEditor.statement.control.If = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Around
    // Implements: [Options, Events],
    //
    // parseNodes: function(){
    //     this.beginNode = this.node.getFirst();
    //     this.beginContentNode = this.beginNode.getFirst();
    //
    //     this.beginUplinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_up}).inject(this.beginNode, "before");
    //
    //     this.middleNode = this.beginNode.getNext();
    //     this.tableNode = this.middleNode.getElement("table");
    //
    //     var tds = this.tableNode.getElements("td");
    //     this.middleLeftNode = tds[0];
    //     this.middleRightNode = tds[1];
    //     this.subContentNode = this.middleRightNode.getFirst();
    //
    //     this.middlelinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.subContentNode);
    //
    //     this.endNode = this.middleNode.getNext();
    //     this.endContentNode = this.endNode.getFirst();
    //
    //     this.endDownlinkStatementNode = new Element("div", {"styles": this.css.linkStatementNode_down}).inject(this.endNode, "after");
    //
    //     this.conditionNode = this.beginContentNode.getFirst().getNext();
    //
    //     this.upLink = new MWF.xApplication.ScriptEditor.statement.Link(this, "up", this.beginUplinkStatementNode);
    //     this.middleLink = new MWF.xApplication.ScriptEditor.statement.Link(this, "middle", this.middlelinkStatementNode);
    //     this.downLink = new MWF.xApplication.ScriptEditor.statement.Link(this, "down", this.endDownlinkStatementNode);
    //
    //     this.links.push(this.upLink);
    //     this.links.push(this.middleLink);
    //     this.links.push(this.downLink);
    //     this.topLink = this.upLink;
    //     this.centerLink = this.middleLink;
    //     this.bottomLink = this.downLink;
    // },
    // reportLinks: function(){
    //     this.area.links.include(this.upLink.rePosition());
    //     this.area.links.include(this.middleLink.rePosition());
    //     this.area.links.include(this.downLink.rePosition());
    // },
    // readyLink: function(link){
    //     if (link.linkType==="up-down" || link.linkType==="up-middle"){
    //         this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(link.toLink.node, "top");
    //     }
    //     if (link.linkType==="down-up" || link.linkType==="middle-up"){
    //         this.readyLinkNode = new Element("div", {"styles": this.css.readyLinkNode}).inject(link.toLink.node, "bottom");
    //     }
    // },
    // notReadyLink: function(link){
    //     if (this.readyLinkNode) this.readyLinkNode.destroy();
    // },
    // readyLinkTo: function(link){
    //     if (link.linkType==="up-down" || link.linkType==="up-middle"){
    //         this.readyLinkToNode = new Element("div", {"styles": this.css.readyLinkToNode}).inject(link.link.node, "bottom");
    //         if (link.link.statement.centerLink && !link.link.statement.centerLink.toLink){
    //             if (link.toLink.toLink){
    //                 if (link.toLink.toLink.statement!==this){
    //                     var height = link.toLink.toLink.statement.node.getSize().y;
    //                     this.tableNode.setStyle("height", ""+height+"px");
    //                 }
    //             }
    //         }
    //     }
    // },
    // notReadyLinkTo: function(link){
    //     if (this.readyLinkToNode) this.readyLinkToNode.destroy();
    //     if (link){
    //         if (link.link.type==="middle"){
    //             this.tableNode.setStyle("height", "auto");
    //         }
    //     }else{
    //         this.tableNode.setStyle("height", "auto");
    //     }
    // },
    //
    // getStatementGroupHeight: function(group){
    //     var first = group[0];
    //     var last = group[group.length-1];
    //     var topP = first.node.getPosition(this.areaNode);
    //     var bottomP = last.node.getPosition(this.areaNode);
    //     var bottomSize = last.node.getSize();
    //
    //     return bottomP.y+bottomSize.y-topP.y
    // },
    // createAroundReadyLinkNode: function(){
    //
    // },
    //
    //
    //
    // setLinkStyle: function(link){
    //
    // }

});