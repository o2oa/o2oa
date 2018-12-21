MWF.xApplication.ScriptEditor.statement = MWF.xApplication.ScriptEditor.statement || {};
MWF.xApplication.ScriptEditor.statement.Link = new Class({
    initialize: function(statement, type, node){
        this.statement = statement;
        this.type = type;
        this.node = node;
        this.area = this.statement.area;
        this.block = this.statement.block;
        this.editor = this.statement.editor;
        this.rePosition();
        this.linkStatement = null;
        this.toLink = null;

        if (this.type==="up"){
            this.linkType = "up";
            this.around = false;
        }
        if (this.type==="middle"){
            this.linkType = "down";
            this.around = true;
        }
        if (this.type==="down"){
            this.linkType = "down";
            this.around = false;
        }

        this.load();
    },
    load :function(){

    },
    rePosition: function(){
        this.position = this.node.getPosition(this.statement.areaNode);
        return this;
    },
    linkDown: function(link){
        var oldLink = this.toLink;

        if (link.toLink){
            link.toLink.toLink = null;
            link.toLink = null;
        }

        this.toLink = link;
        link.toLink = this;
        if (oldLink && oldLink!==link){
            if (link.statement.centerLink && !link.statement.centerLink.toLink){
                link.statement.centerLink.toLink = oldLink;
                var group = this.area.getStatementGroup(oldLink.statement);
                link.statement.centerLink.endStatement = group[group.length-1];
                oldLink.toLink = link.statement.centerLink;
            }else{
                var linkGroup = this.area.getStatementGroup(link.statement);
                var statement = linkGroup[linkGroup.length-1];

                oldLink.toLink = statement.bottomLink;
                statement.bottomLink.toLink = oldLink;
            }
        }
    },
    linkUp: function(link){
        this.toLink = link;
        link.toLink = link;
    },
    linkMiddle: function(link){
        var oldLink = this.toLink;

        if (link.toLink){
            link.toLink.toLink = null;
            link.toLink = null;
        }

        this.toLink = link;
        link.toLink = this;
        if (oldLink && oldLink!==link){
            if (link.statement.centerLink && !link.statement.centerLink.toLink){
                link.statement.centerLink.toLink = oldLink;
                var group = this.area.getStatementGroup(oldLink.statement);
                link.statement.centerLink.endStatement = group[group.length-1];
                oldLink.toLink = link.statement.centerLink;
            }else{
                var linkGroup = this.area.getStatementGroup(link.statement);
                var statement = linkGroup[linkGroup.length-1];

                oldLink.toLink = statement.bottomLink;
                statement.bottomLink.toLink = oldLink;
            }
        }else{
            this.endStatement = link.statement;
        }
    },
    linkUpAround: function(link){
        this.toLink = link;
        link.toLink = link;
        var group = this.area.getStatementGroup(this.statement);
        link.endStatement = group[group.length-1];
    },

    // link: function(link){
    //     if (this.type==="up"){
    //         this.toLink = link;
    //         link.toLink = this;
    //     }
    //     if (this.type==="down"){
    //         var oldLink = this.toLink;
    //         this.toLink = link;
    //         link.toLink = this;
    //
    //         if (link.statement.centerLink){
    //             oldLink.toLink = link.statement.centerLink;
    //             link.statement.bottomLink.toLink = oldLink;
    //
    //         }else{
    //             oldLink.toLink = link.statement.bottomLink;
    //             link.statement.bottomLink.toLink = oldLink;
    //         }
    //     }
    //     if (this.type==="middle"){
    //         var oldLink = this.toLink;
    //         this.toLink = link;
    //         link.toLink = this;
    //
    //         if (link)
    //
    //         var group = this.area.getStatementGroup(oldLink.statement);
    //
    //     }
    //
    //
    //
    //     if (!this.toLink){
    //         this.toLink = link;
    //     }else {
    //         var oldToLink = this.toLink;
    //         this.toLink = link;
    //         if (this.linkType==="up" && link.linkType==="down"){
    //
    //         }
    //
    //         if (this.linkType==="down" && link.linkType==="up"){
    //
    //         }
    //     }
    // },
    getSerialLinkGroup: function(link){
        link.statement.bottomLink
    }

});
