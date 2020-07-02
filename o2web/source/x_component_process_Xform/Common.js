MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Common = MWF.APPCommon =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        if (this.json.innerHTML){
            var nodes = this.node.childNodes;
            for (var i=0; i<nodes.length; i++){
                if (nodes[i].nodeType===Node.ELEMENT_NODE){
                    if (!nodes[i].get("MWFtype")){
                        nodes[i].destroy();
                        i--;
                    }
                }else{
                    if (nodes[i].removeNode){
                        nodes[i].removeNode();
                    }else{
                        nodes[i].parentNode.removeChild(nodes[i]);
                    }
                    i--;
                    //nodes[i]
                }
            }
            this.node.appendHTML(this.json.innerHTML);

            // if (this.node.get("html") !== this.json.innerHTML){
            //this.node.appendHTML(this.json.innerHTML);
            // }
        }
        this.node.setProperties(this.json.properties);
    }
});