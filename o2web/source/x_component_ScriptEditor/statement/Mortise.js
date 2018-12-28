MWF.xApplication.ScriptEditor.statement.Mortise = new Class({
    initialize: function(statement, node, types, input){
        this.statement = statement;
        this.types = types;
        this.node = node;
        this.area = this.statement.area;
        this.block = this.statement.block;
        this.editor = this.statement.editor;
        this.tenonStatement = null;
        this.input = input;
        this.load();
    },
    shine: function(){
        if (!this.shineNode){
            this.shineNode = new Element("div", {"styles": this.statement.css.mortiseShineNode}).inject(this.statement.areaNode);
            var size = this.input.getSize();
            var width = size.x-3;
            var height = size.y-3;
            this.shineNode.setStyles({
                "width": ""+width+"px",
                "height": ""+height+"px",
                "border-radius": ""+height/2+"px",
                "z-index": MWF.SES.zIndexPool.zIndex-2
            });
            this.shineNode.position({
                "relativeTo": this.input,
                "position": 'upperLeft',
                "edge": 'upperLeft',
                "offset": {"x": 0, "y": 0}
            });
        }
    },
    unshine: function(){
        if (this.shineNode) this.shineNode.destroy();
        this.shineNode = null;
    },
    tenon: function(){
        if (this.tenonStatement){
            if (this.input){
                this.inputDisplay = this.input.getStyle("display");
                this.input.setStyle("display", "none");
            }
            this.tenonStatement.node.inject(this.node);
            this.tenonStatement.node.setStyles({"position": "static", "top": "auto", "left": "auto"});
        }
    },
    split: function(){
        if (this.tenonStatement){
            if (this.input){
                this.input.setStyle("display", this.inputDisplay);
            }
            //var p = this.node.getPosition(this.tenonStatement.areaNode.getOffsetParent());
            var p = this.node.getPosition(this.tenonStatement.areaNode);
            //this.tenonStatement.node.setStyle("z-index", MWF.SES.zIndexPool.apply());
            this.tenonStatement.node.inject(this.tenonStatement.areaNode);
            this.tenonStatement.node.setStyles({
                "z-index": MWF.SES.zIndexPool.apply(),
                "position": "absolute",
                "left": ""+p.x+"px",
                "top": ""+p.y+"px"
            });
            this.tenonStatement = null;
        }
    },
    load :function(){

    }
});