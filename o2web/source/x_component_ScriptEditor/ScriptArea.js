MWF.xApplication.ScriptEditor.ScriptArea = new Class({
    initialize: function(editor){
        this.editor = editor;
        this.node = this.editor.scriptAreaNode;
        this.css = this.editor.css;
        this.statementNodes = [];
        this.statements = [];
        this.links = [];
        this.mortises = [];
        this.beginStatements = [];
        this.currentLink = null;
        this.load();
    },
    load: function(){
        this.titleNode = new Element("div", {"styles": this.css.scriptAreaTitleNode}).inject(this.node);
        this.blockArea = new Element("div", {"styles": this.css.scriptAreaBlockNode}).inject(this.node);
        this.scriptArea = new Element("div", {"styles": this.css.scriptAreaScriptNode}).inject(this.node);
        this.blockAreaContent = new Element("div").inject(this.blockArea);

        this.loadTitleNode();
        this.setAreaSizeFun = this.setAreaSize.bind(this);
        this.editor.app.addEvent("resize", this.setAreaSizeFun);
        this.setAreaSize();
    },
    setAreaSize: function(){
        var size = this.node.getSize();
        var titleSize = this.titleNode.getSize();
        var y = size.y-titleSize.y;
        this.blockArea.setStyle("height", ""+y+"px");
        this.scriptArea.setStyle("height", ""+y+"px");

        this.beginStatements.each(function(statement){
            var p = statement.node.getPosition(this.blockArea);
            var size = statement.node.getSize();
            var h = p.y+size.y;
            this.blockAreaContent.setStyle("height", ""+h+"px");
        }.bind(this));
    },
    loadTitleNode: function(){
        this.blockTabNode = new Element("div", {"styles": this.css.scriptAreaTitleBlockNode}).inject(this.titleNode);
        this.scriptTabNode = new Element("div", {"styles": this.css.scriptAreaTitleScriptNode}).inject(this.titleNode);
        this.blockTabNode.set("text", this.editor.app.lp.block);
        this.scriptTabNode.set("text", this.editor.app.lp.script);
        this.blockTabNode.setStyles(this.css.titleActionNode_current);

        this.blockTabNode.addEvent("click", function(){
            this.blockTabNode.setStyles(this.css.titleActionNode_current);
            this.blockArea.setStyle("display", "block");
            this.scriptTabNode.setStyles(this.css.scriptAreaTitleScriptNode);
            this.scriptArea.setStyle("display", "none");
        }.bind(this));
        this.scriptTabNode.addEvent("click", function(){
            this.blockTabNode.setStyles(this.css.scriptAreaTitleBlockNode);
            this.blockArea.setStyle("display", "none");
            this.scriptTabNode.setStyles(this.css.titleActionNode_current);
            this.scriptArea.setStyle("display", "block");
        }.bind(this));
    },
    createStatement: function(node, block){
        this.statementNodes.push(node);
        var classPath = ("statement."+block.data.class).split(".");
        var clazz = MWF.xApplication.ScriptEditor;
        classPath.each(function(p){ if (clazz){clazz = clazz[p];} }.bind(this));
        if (!clazz) clazz = this.createClazz(block);
        if (clazz){
            var statement = new clazz(node, block, this);
            return statement;
        }
        return null;
    },
    createClazz: function(block){
        var clazz = MWF.xApplication.ScriptEditor;
        var classPath = ("statement."+block.data.class).split(".");
        classPath.each(function(p, i){
            if (i===(classPath.length-1)){
                clazz[p] = new Class({
                    Extends: MWF.xApplication.ScriptEditor.statement.$Statement[block.data.extend]
                });
                clazz = clazz[p];
            }else{
                clazz = clazz[p];
                if (!clazz) clazz = {};
            }
        }.bind(this));
        return clazz;
    },
    buildStatement: function(statement){
        statement.load();
        this.statements.push(statement);
        return statement;
    },


    // checkDownLinks: function(checkLink, p, max){
    //     this.downLinks.each(function(link){
    //         var v = Math.abs(link.position.x-p.x)+Math.abs(link.position.y-p.y);
    //         if (checkLink.distance===null){
    //             if (v<max){
    //                 checkLink.distance = v;
    //                 checkLink.link = link;
    //             }
    //         }else{
    //             if (v<checkLink.distance){
    //                 checkLink.distance = v;
    //                 checkLink.link = link;
    //             }
    //         }
    //     });
    //     return checkLink;
    // },
    // checkUpLinks: function(checkLink, p, max){
    //     this.upLinks.each(function(link){
    //         var v = Math.abs(link.position.x-p.x)+Math.abs(link.position.y-p.y);
    //         if (checkLink.distance===null){
    //             if (v<max){
    //                 checkLink.distance = v;
    //                 checkLink.link = link;
    //             }
    //         }else{
    //             if (v<checkLink.distance){
    //                 checkLink.distance = v;
    //                 checkLink.link = link;
    //             }
    //         }
    //     });
    //     return checkLink;
    // },
    checkLinks: function(node, statement, excludeLinks){
        var max = 200;
        var checkLink = {
            "distance": null,
            "link": null,
            "toLink": null,
            "linkType": ""
        };
        statement.normal();
        if (statement.topLink){
            var link = statement.topLink.rePosition();
            this.links.each(function(toLink){
                toLink.rePosition();
                var checkFlag = false;
                if (link.type!==toLink.type){
                    if (excludeLinks.indexOf(toLink)===-1){
                        var linkType = link.type+"-"+toLink.type;
                        if (linkType==="up-down"){
                            if (!link.statement.bottomLink && toLink.toLink){
                                checkFlag = false;
                            }else{
                                checkFlag = true;
                            }
                        }
                        if (linkType==="down-up"){
                            if (!toLink.toLink && !link.toLink) checkFlag = true;
                        }
                        if (linkType==="up-middle")  checkFlag = true;
                        if (linkType==="middle-up"){
                            if (!toLink.toLink && !link.toLink) checkFlag = true;
                        }
                    }
                }
                if (checkFlag){
                    var v = Math.abs(toLink.position.x-link.position.x)+Math.abs(toLink.position.y-link.position.y);
                    if (checkLink.distance===null){
                        if (v<max){
                            checkLink.distance = v;
                            checkLink.link = link;
                            checkLink.toLink = toLink;
                            checkLink.linkType = linkType;
                        }
                    }else{
                        if (v<checkLink.distance){
                            checkLink.distance = v;
                            checkLink.link = link;
                            checkLink.toLink = toLink;
                            checkLink.linkType = linkType;
                        }
                    }
                }

            }.bind(this));
        }
        this.clearCurrentLink();
        if (checkLink.link){
            if (this.currentLink){
                if (this.currentLink.link !== checkLink.link){
                    this.currentLink.link.statement.notReadyLinkTo(this.currentLink);
                    checkLink.link.statement.readyLinkTo(this.currentLink);
                }
                if (this.currentLink.toLink !== checkLink.toLink){
                    this.currentLink.toLink.statement.notReadyLink(this.currentLink);
                    checkLink.toLink.statement.readyLink(this.currentLink);
                }
                this.currentLink = checkLink;
            }else{
                this.currentLink = checkLink;
                this.currentLink.toLink.statement.readyLink(this.currentLink);
                this.currentLink.link.statement.readyLinkTo(this.currentLink);
            }
        }else{
            if (this.currentLink){
                this.currentLink.link.statement.notReadyLinkTo(this.currentLink);
                this.currentLink.toLink.statement.notReadyLink(this.currentLink);
                this.currentLink = null;
            }
        }
    },
    checkMortises: function(node, statement){
        this.currentMortise = null;
        if (statement.tenon){
            var p = statement.tenon.getPosition(this.blockArea);
            var size = statement.node.getSize();
            p.y = p.y+(size.y/2);
            for (var i=0; i<this.mortises.length; i++){
                var mortise = this.mortises[i];
                if (!mortise.tenonStatement){
                    if (!mortise.types.length || (mortise.types.indexOf(statement.block.blockName)!==-1)){
                        if (mortise.node.isPointIn(p.x,p.y,0,0,this.blockArea)){
                            //mortise.node.setStyles(mortise.statement.css.mortise_current);
                            mortise.shine();
                            this.currentMortise = mortise;
                            break;
                        }else{
                            mortise.unshine();
                            //mortise.node.setStyles(mortise.statement.css.mortise);
                        }
                    }
                }
            }
        }

        // if (this.currentMortise){
        //     this.currentMortise.setStyles(this.currentMortise.statement.css.)
        // }
    },
    checkBlockDrag: function(node, statement, excludeLinks){
        //var moveStatement = node.retrieve("statement");
        if (statement){
            this.checkLinks(node, statement, excludeLinks);
            this.checkMortises(node, statement);
        }
    },
    clearCurrentLink: function(){
        if (this.currentLink){
            this.currentLink.link.statement.notReadyLinkTo(this.currentLink);
            this.currentLink.toLink.statement.notReadyLink(this.currentLink);
            this.currentLink = null;
        }
    },
    getStatementGroup: function(statement){
        var statementGroup = [];
        var toLinkStatement = statement;
        while (toLinkStatement) {
            statementGroup.push(toLinkStatement);
            if (toLinkStatement.bottomLink){
                toLinkStatement = toLinkStatement.bottomLink.toLink;
            }else{
                toLinkStatement = null;
            }
        }
        return statementGroup;
    }


});