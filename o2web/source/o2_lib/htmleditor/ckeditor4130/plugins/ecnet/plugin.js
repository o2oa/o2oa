CKEDITOR.plugins.add( 'ecnet', {
    icons: 'ecnet',
    init: function( editor ) {
        var _self = this;
        editor.addCommand( 'ecnet', {
            exec: function( editor ) {
                _self.ecnet(editor);
            }
        });
        editor.ui.addButton( 'ecnet', {
            label: '智能纠错',
            command: 'ecnet',
            toolbar: 'tools'
        });
        var pluginDirectory = this.path;
        editor.addContentsCss( pluginDirectory + 'styles/style.css' );
    },
    getEcnetString: function(node, nodes){
        for (var i=0; i<node.childNodes.length; i++){
            if (node.childNodes[i].nodeType===Node.TEXT_NODE){
                var s = this.ecnetString.length;
                this.ecnetString += node.childNodes[i].nodeValue;
                var e = this.ecnetString.length;

                nodes.push({
                    "pnode": node,
                    "node": node.childNodes[i],
                    "start": s, "end": e
                });
            }else{
                this.getEcnetString(node.childNodes[i], nodes);
            }
        }
    },
    clearEcnetNodes: function(){
        try{
            if (this.ecnetNodes && this.ecnetNodes.length){
                this.ecnetNodes.each(function(node){
                    if (node.ecnetAreaNodes){
                        node.ecnetAreaNodes.each(function(ecnetAreaNode){
                            ecnetAreaNode.destroy();
                        });
                        node.ecnetAreaNodes = [];
                    }
                    if (node.pnode.ecnetNode){
                        if (node.pnode.ecnetInforNode) node.pnode.ecnetInforNode.destroy();
                        node.pnode.ecnetInforNode = null;
                        node.pnode.replaceChild(node.pnode.textNode, node.pnode.ecnetNode);
                    }
                }.bind(this));
                this.ecnetNodes = [];
            }
        }catch(e){}
    },
    createEcnetNode: function(node,editor){
        var newNode = node.node.ownerDocument.createElement("span");

        var increment = 0;
        var html = node.node.nodeValue;
        node.ecnets.each(function(ecnet){
            var s = ecnet.begin+increment-node.start;
            var e = ecnet.end+increment-node.start;
            if (s<0) s=0;
            if (e>node.end+increment) e = node.end+increment;
            var length = html.length;

            var left = html.substring(0, s);
            var ecnetStr = html.substring(s, e);
            var right = html.substring(e, html.length);

            html = left+"<span class='o2_ecnet_item'><u>"+ecnetStr+"</u></span>"+right;
            increment += (html.length-length);

        }.bind(this));
        newNode.innerHTML = html;
        node.pnode.replaceChild(newNode, node.node);
        node.pnode.textNode = node.node;
        node.pnode.ecnetNode = newNode;

        if (!node.ecnetAreaNodes) node.ecnetAreaNodes = [];
        var _self = this;
        var editorFrame;
        if (editor.elementMode === CKEDITOR.ELEMENT_MODE_INLINE){
            editorFrame = editor.element.$;
        }else{
            editorFrame = editor.document.$.defaultView.frameElement;
        }

        var spans = newNode.getElementsByTagName("span");
        if (spans.length){
            for (var i = 0; i<spans.length; i++){
                var span = spans[i];
                if (span.className==="o2_ecnet_item"){
                    var ecnetNode = new Element("div.o2_ecnet_ecnetNode",{"styles": {
                            "border": "1px solid #999999",
                            "box-shadow": "0px 0px 5px #999999",
                            "background-color": "#ffffff",
                            "position": "fixed",
                            "z-index": 100,
                            "display": "none"
                        }}).inject(editorFrame, "after");
                    node.ecnetAreaNodes.push(ecnetNode);

                    var correctNode = new Element("div.o2_ecnet_correctNode", {
                        "styles": {
                            "padding": "3px 10px",
                            "font-weight": "bold",
                            "font-size": "12px",
                            "cursor": "pointer"
                        },
                        "text": node.ecnets[i].origin+"->"+node.ecnets[i].correct,
                        "events": {
                            "mouseover": function(){this.setStyle("background-color", "#dddddd")},
                            "mouseout": function(){this.setStyle("background-color", "#ffffff")},
                            "mousedown": function(){
                                var ecnetNode = this.getParent();
                                var node = ecnetNode.node;
                                //var item = ecnetNode.node.ecnets[ecnetNode.idx];
                                var item = ecnetNode.item;
                                var textNode = node.node.ownerDocument.createTextNode(item.correct);
                                ecnetNode.span.parentNode.replaceChild(textNode, ecnetNode.span);
                                ecnetNode.destroy();
                                node.node.nodeValue = node.pnode.ecnetNode.innerText;

                                node.ecnets.erase(item);
                                if (!node.ecnets.length){
                                    _self.ecnetNodes.erase(node);
                                }
                            }
                        }
                    }).inject(ecnetNode);
                    var ignoreNode = new Element("div.o2_ecnet_ignoreNode", {
                        "styles": {
                            "padding": "3px 10px",
                            "font-size": "12px",
                            "cursor": "pointer"
                        },
                        "text": MWF.xApplication.process.Xform.LP.ignore,
                        "events": {
                            "mouseover": function(){this.setStyle("background-color", "#dddddd")},
                            "mouseout": function(){this.setStyle("background-color", "#ffffff")},
                            "mousedown": function(){
                                var ecnetNode = this.getParent();
                                var node = ecnetNode.node;
                                //var item = ecnetNode.node.ecnets[ecnetNode.idx];
                                var item = ecnetNode.item;
                                var textNode = node.node.ownerDocument.createTextNode(ecnetNode.span.innerText);
                                ecnetNode.span.parentNode.replaceChild(textNode, ecnetNode.span);
                                ecnetNode.destroy();
                                node.node.nodeValue = node.pnode.ecnetNode.innerText;

                                node.ecnets.erase(item);
                                if (!node.ecnets.length){
                                    _self.ecnetNodes.erase(node);
                                }
                            }
                        }
                    }).inject(ecnetNode);
                    ecnetNode.node = node;
                    ecnetNode.idx = i;
                    ecnetNode.item = node.ecnets[i];

                    span.ecnetNode = ecnetNode;
                    ecnetNode.span = span;
                    span.addEventListener("click", function(){
                        var ecnetNode = this.ecnetNode;
                        ecnetNode.show();
                        if (editor.elementMode !== CKEDITOR.ELEMENT_MODE_INLINE){
                            var y = this.offsetTop;
                            var x = this.offsetLeft;
                            var w = this.offsetWidth;
                            var h = this.offsetHeight;
                            var p = editorFrame.getPosition();
                            var s = ecnetNode.getSize();
                            var pos = editor.window.getScrollPosition();

                            var top = y+p.y+h+5-pos.y;
                            var left = x+p.x-((s.x-w)/2)-pos.x;

                            ecnetNode.style.left = ""+left+"px";
                            ecnetNode.style.top = ""+top+"px";
                        }else{
                            ecnetNode.position({
                                "relativeTo": this,
                                "position": "bottomCenter",
                                "edge": "topCenter",
                                "offset": {
                                    "x": 0,
                                    "y": 5
                                }
                            })
                        }
                        var _span = this;
                        var hideEcnetNode = function(){
                            ecnetNode.hide();
                            _span.ownerDocument.removeEventListener("mousedown", hideEcnetNode);
                        };
                        this.ownerDocument.addEventListener("mousedown", hideEcnetNode);

                    });

                }
            }
        }
    },
    ecnet: function(editor){
        //this.editor.document.$.body.innerText
        var body;
        if (editor.elementMode === CKEDITOR.ELEMENT_MODE_INLINE){
            body = editor.element.$;
        }else{
            var editorFrame = editor.document.$.defaultView.frameElement;
            //var data = this.editor.getData();
            body = editor.document.$.body;
        }


        if (!this.ecnetNodes) this.ecnetNodes = [];
        if (this.ecnetNodes.length) this.clearEcnetNodes();

        var nodes = [];
        this.ecnetString = "";
        this.getEcnetString(body, nodes);

        o2.Actions.get("x_general_assemble_control").ecnetCheck({"value": this.ecnetString}, function(json){
            if (json.data.itemList && json.data.itemList.length){

                nodes.each(function(node){
                    var items = [];
                    json.data.itemList.each(function(item){
                        if ((node.end<=item.end && node.end>item.begin) || (node.start>=item.begin && node.start<item.end) || (node.start<=item.begin && node.end>item.end)){
                            items.push(item);
                        }
                    }.bind(this));
                    if (items.length){
                        node.ecnets = items;
                        this.ecnetNodes.push(node);
                    }
                }.bind(this));


                this.ecnetNodes.each(function(node){
                    this.createEcnetNode(node, editor);
                }.bind(this));
            }else{
                body = null;
                nodes = null;
            }
        }.bind(this), null, false);
    }
});