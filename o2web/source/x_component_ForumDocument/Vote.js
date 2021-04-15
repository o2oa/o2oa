MWF.xApplication.ForumDocument = MWF.xApplication.ForumDocument || {};
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.ForumDocument.Vote = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isNew" : false,
        "isEdited" : true
    },
    initialize : function( container, app, options, data ){
        this.setOptions(options);
        this.container = container;
        this.app = app;
        this.lp = app.lp;
        this.actions = app.restActions || app.action;
        this.data = data;
        this.userName = (layout.user && layout.user.distinguishedName) ? layout.user.distinguishedName :layout.desktop.session.user.distinguishedName;
        this.path = "../x_component_ForumDocument/$Vote/";

        this.cssPath = "../x_component_ForumDocument/$Vote/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function(){
        this.voted = this.data.voted || false;
        if( !this.options.isNew && this.data.voteOptionGroupList  ){
            this.sortData();
        }
        if( this.options.isNew || this.options.isEdited ){
            //this.loadContent_read()
            this.loadContent_edit();
        }else{
            this.loadContent_read();
        }
    },
    reload : function(){
        this.container.empty();
        this.getData( function(){
            this.load( true );
        }.bind(this))
    },
    getData : function( callback ){
        this.actions.getSubjectView( this.data.id , function( data ){
            this.data = data.data.currentSubject;
            if( callback )callback();
        }.bind(this))
    },
    sortData: function(){
        var groupList = this.data.voteOptionGroupList || [];
        groupList.sort( function( a, b ){
            return a.orderNumber - b.orderNumber;
        });
        for( var i=0; i<groupList.length;i++ ){
            var group = groupList[i];
            group.voteOptions.sort( function( a, b ){
                return a.orderNumber - b.orderNumber;
            })
        }
    },
    loadContent_read : function(){
        var overTime = false;
        var dateStr = "";
        if( this.data.voteLimitTime && this.data.voteLimitTime != "" ){
            var now = new Date();
            var end = new Date( this.data.voteLimitTime.replace(/-/g,"/") );
            if( now < end ){
                dateStr = this.lp.timeToEndText.replace("{time}",this.diffTime( now , end ));
            }else{
                overTime = true;
                dateStr = this.lp.voteCompleteText
            }
        }

        var personStr = "";
        if( this.data.voteUserCount || this.data.voteCount ){
            personStr = this.lp.votedPersonCountText.replace("{count}", (this.data.voteUserCount || this.data.voteCount) )
        }else{
            personStr = this.lp.noPersonVoteText
        }

        var inforNode = new Element("div", {
            styles : this.css.inforNode,
            text : this.lp.vote
        }).inject(this.container);

        new Element("span", {
            styles : this.css.infor2Node,
            "text" : personStr + dateStr
        }).inject(inforNode);

        if( this.data.votePersonVisible && this.data.votePerson!=0 ){
            //var showPersonNode = new Element("span", {
            //    styles : this.showPersonNode,
            //    text : "查看投票参与人"
            //}).inject( inforNode );
            //showPersonNode.addEvents({
            //    "click" : function(){ this.showVotePerson(); }.bind(this)
            //})
        }

        this.groupContainer = new Element("div", {
            styles : this.css.groupContainer
        }).inject( this.container );

        this.data.voteOptionGroupList.each( function( d , i){
            if( this.voted || overTime ){
                if( this.data.voteResultVisible || this.data.creatorName == this.userName){
                    this.createGroupVoted(d, i);
                }
            }else{
                this.createGroupVoting(d, i);
            }
        }.bind(this));

        if( this.voted || overTime ){
            if( !this.data.voteResultVisible && this.data.creatorName != this.userName){
                new Element("div",{
                    "styles" : { "margin-bottom" : "5px" },
                    "text" : this.lp.privateVoteText
                }).inject( this.container );
            }
        }

        if( !this.voted ){
            if( !overTime ){
                var bottomContainer = new Element( "div").inject( this.container );
                var button = new Element( "input", {
                    "type" : "button",
                    styles : this.css.submitButton,
                    value : this.lp.submit
                }).inject( bottomContainer );
                button.addEvent("click", function(){
                    this.submitVote();
                }.bind(this));
                button.addEvents({
                    mouseover : function(){
                        this.node.setStyles( this.obj.css.submitButton_over )
                    }.bind({obj : this, node : button}),
                    mouseout : function(){
                        this.node.setStyles( this.obj.css.submitButton )
                    }.bind({obj : this, node : button})
                });
                if( this.data.votePersonVisible ){
                    new Element("span", {
                        styles : { "margin-left" : "10px" },
                        text : this.lp.publicVoteText
                    }).inject( bottomContainer )
                }else{
                    new Element("span", {
                        styles : { "margin-left" : "10px" },
                        text : this.lp.anonymousVoteText
                    }).inject( bottomContainer )
                }
            }
        }else{
            new Element("span",{
                "text" : this.lp.youAreVoted
            }).inject( this.container );
        }

    },
    createGroupVoted : function(data, idx){
        var _self = this;
        var bgColor = this.getRandomColor();
        var maxWidth = "800";
        var sum = 0;

        var contentUsePicture = false;
        data.voteOptions.each( function(opt){
            sum += parseInt( opt.chooseCount );
            if( opt.optionContentType == _self.lp.picture ){
                contentUsePicture = true;
            }
        });
        if( sum!=0 ) {
            var groupTitle = new Element("div", {
                styles : this.css.groupTitle
            }).inject( this.groupContainer );


            var str = parseInt( data.voteChooseCount ) > 1 ? this.lp.multiSelectText.replace("{n}", data.voteChooseCount) : this.lp.singleSelectText;
            new Element( "span", { styles : this.css.groupSubject , text : str }).inject( groupTitle );
            new Element( "span", { styles : this.css.groupSubject , text : data.groupName } ).inject( groupTitle );

            var optionContainer = new Element("div", { styles : this.css.optionContainer }).inject( this.groupContainer );

            var optionTable;
            if( contentUsePicture ){
                if( layout.mobile ){
                    optionTable =  new  Element("div").inject( optionContainer )
                }else {
                    optionTable = new Element("table", {
                        "cellSpacing": "0",
                        "border": "0"
                    }).inject(optionContainer);
                }

                var tr;
                data.voteOptions.each( function( n, i ){

                    var optionsDiv;

                    if( layout.mobile ){
                        optionsDiv = new Element("div", {
                            styles : this.css.optionsPictureDiv_mobile
                        }).inject( optionTable );

                        var area = new Element("div",{
                            styles : this.css.optionPictureArea_mobile
                        }).inject( optionsDiv );

                        var img = new Element("img", {
                            "src" : MWF.xDesktop.getImageSrc( n.optionPictureId ),
                            "styles" : this.css.optionPicture_mobile
                        }).inject( area );
                    }else {
                        if (i % 3 == 0) {
                            tr = new Element("tr").inject(optionTable);
                        }

                        var td = new Element("td").inject(tr);
                        optionsDiv = new Element("div", {
                            styles: this.css.optionsPictureDiv
                        }).inject(td);

                        var area = new Element("div", {
                            styles: this.css.optionPictureArea
                        }).inject(optionsDiv);

                        var img = new Element("img", {
                            "src": MWF.xDesktop.getImageSrc(n.optionPictureId),
                            "styles": this.css.optionPicture
                        }).inject(area);
                        img.addEvent("click", function () {
                            window.open(o2.filterUrl(MWF.xDesktop.getImageSrc(this.id)), "_blank");
                        }.bind({id: n.optionPictureId}));
                    }

                    var present = ( ( n.chooseCount / sum ) * 100 ).toString().substr(0, 5) + "%";
                    var pre = new Element("div", {
                        text: present,
                        styles : this.css.presentDiv
                    }).inject(optionsDiv);
                    new Element("span", {
                        styles: {color: "#ccc"},
                        text: "(" + n.chooseCount + ")"
                    }).inject(pre);

                    var textNode = new Element("div", {
                        text : (i+1)+"." + n.optionTextContent
                    }).inject( optionsDiv );

                }.bind(this));

            }

            data.voteOptions.each( function( n, i ) {

                var optionText = new Element("div.optionText", {
                    styles : {},
                    text : (i+1)+".　"+ n.optionTextContent
                }).inject( optionContainer );

                var resultContainer = new Element("div.resultContainer", {
                    styles: this.css.optionItem
                }).inject(optionContainer);
                var result = new Element("div", {
                    styles: this.css.optionItemBack
                }).inject(resultContainer);
                result.setStyle("width", layout.mobile ? "70%" : "85%");
                var width = Math.floor(( n.chooseCount / sum) * 100 );
                var front = new Element("div.optionItemFront", {
                    styles: this.css.optionItemFront,
                    text: " "
                }).inject( result );
                front.setStyles({"background-color": bgColor, width: width+"%"});
                if( this.data.votePersonVisible && !layout.mobile){
                    front.setStyles({"cursor": "pointer"});
                    front.addEvents({
                        "click" : function(){
                            this.obj.openLog( this.data );
                        }.bind({ obj : this, data : n }),
                        "mouseover" : function(){
                            this.node.setStyle("opacity" , "0.8");
                        }.bind({ node : front }),
                        "mouseout" : function(){
                            this.node.setStyle("opacity" , "1");
                        }.bind({ node : front })
                    })
                }

                var present = ( ( n.chooseCount / sum ) * 100 ).toString().substr(0, 5) + "%";
                var pre = new Element("div", {
                    text: present,
                    styles : this.css.presentNode
                }).inject(resultContainer);
                new Element("span", {
                    styles: {color: "#ccc"},
                    text: "(" + n.chooseCount + ")"
                }).inject(pre);

                if( n.voted ){
                    new Element("div", {
                        styles : this.css.checkedOption
                    }).inject(resultContainer);
                }
            }.bind(this));
        }
    },
    createGroupVoting : function( data, idx ){
        this.groupObject = this.groupObject || {};
        this.groupObject[idx] = {};
        this.groupObject[idx].id = data.id;
        this.groupObject[idx].inputs = [];

        var groupTitle = new Element("div", {
            styles : this.css.groupTitle
        }).inject( this.groupContainer );

        var str = parseInt(data.voteChooseCount) > 1 ? this.lp.multiSelectText.replace("{n}", data.voteChooseCount) : this.lp.singleSelectText;
        new Element( "span", { styles : this.css.groupSubject , text : str }).inject( groupTitle );
        new Element( "span", { styles : this.css.groupSubject , text : data.groupName } ).inject( groupTitle );

        var contentUsePicture = false;
        for( var i=0; i<data.voteOptions.length; i++ ){
            if( data.voteOptions[i].optionContentType === this.lp.picture ){
                contentUsePicture = true;
                break;
            }
        }

        var optionContainer = new Element("div", {
            styles : contentUsePicture ? this.css.optionPictureContainer : this.css.optionContainer
        }).inject( this.groupContainer );

        var optionTable;
        if( contentUsePicture ){
            if( layout.mobile ){
                optionTable =  new  Element("div").inject( optionContainer )
            }else{
                optionTable =  new  Element("table", {
                    "cellSpacing" : "0",
                    "border" : "0"
                }).inject( optionContainer )
            }
        }

        var tr;
        data.voteOptions.each( function( n, i ){

            var optionsDiv;
            if( contentUsePicture ){
                if( layout.mobile ){
                    optionsDiv = new Element("div", {
                        styles : this.css.optionsPictureDiv_mobile
                    }).inject( optionTable );

                    var area = new Element("div",{
                        styles : this.css.optionPictureArea_mobile
                    }).inject( optionsDiv );

                    var img = new Element("img", {
                        "src" : MWF.xDesktop.getImageSrc( n.optionPictureId ),
                        "styles" : this.css.optionPicture_mobile
                    }).inject( area );
                }else{
                    if( i % 3 == 0 ){
                        tr = new Element("tr").inject( optionTable );
                    }

                    var td = new Element("td").inject( tr );
                    optionsDiv = new Element("div", {
                        styles : this.css.optionsPictureDiv
                    }).inject( td );

                    var area = new Element("div",{
                        styles : this.css.optionPictureArea
                    }).inject( optionsDiv );

                    var img = new Element("img", {
                        "src" : MWF.xDesktop.getImageSrc( n.optionPictureId ),
                        "styles" : this.css.optionPicture
                    }).inject( area );
                    img.addEvent("click",function(){
                        window.open( o2.filterUrl(MWF.xDesktop.getImageSrc( this.id )), "_blank" );
                    }.bind({id: n.optionPictureId}))
                }
            }else{
                optionsDiv = new Element("div", {
                    styles : this.css.optionsDiv
                }).inject( optionContainer );
            }

            var input = new Element("input", {
                type : data.voteChooseCount == 1 ? "radio" : "checkbox",
                name : "voteCheck_" +  idx,
                value : n.id,
                styles : contentUsePicture ? this.css.optionsPictureInput : this.css.optionsInput
            }).inject( optionsDiv );

            var textNode = new Element("span", {
                text : (i+1)+"." + ( contentUsePicture ? "" : "　") + n.optionTextContent
            }).inject( optionsDiv );

            if( data.voteChooseCount > 1 ){
                input.addEvents({
                    "click" : function( ev ){
                        var inputs = this.obj.groupObject[this.groupIndex].inputs;
                        this.obj.checkCountLimited( this.limit, inputs );
                    }.bind( { obj : this, groupIndex : idx, optionIndex : i, limit : data.voteChooseCount } )
                });
                textNode.addEvents({
                    "click" : function(ev){
                        if( this.input.get("checked") ){
                            this.input.set("checked", false);
                        }else if( !this.input.get("disabled") ){
                            this.input.set("checked", true );
                        }
                        var inputs = this.obj.groupObject[this.groupIndex].inputs;
                        this.obj.checkCountLimited( this.limit, inputs );
                    }.bind( { obj : this, groupIndex : idx, optionIndex : i, limit : data.voteChooseCount  , input : input } )
                })
            }else{
                textNode.addEvents({
                    "click" : function(ev){
                        if( this.input.get("checked") ){
                            return;
                        }
                        var inputs = this.obj.groupObject[this.groupIndex].inputs;
                        inputs.each( function( input ){
                            input.set("checked", false);
                        });
                        this.input.set("checked", true);
                    }.bind( { obj : this, groupIndex : idx, optionIndex : i, input : input } )
                })
            }


            this.groupObject[idx].inputs.push( input );
        }.bind(this));
    },
    checkCountLimited : function( limit, inputs ){
        var checkedCount = 0;
        inputs.each( function( input ){
            if( input.get("checked") )checkedCount++;
        });
        if( checkedCount >= limit ){
            inputs.each( function( input ){
                if( !input.get("checked") )input.set("disabled", true);
            });
        }else{
            inputs.each( function( input ){
                if( !input.get("checked") )input.set("disabled", false);
            });
        }
    },
    loadContent_edit : function(){
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' style='margin-top:15px;'>" +
            "<tr>" +
            "   <td styles='formTableTitleRight'  lable='voteLimitTime' width='13%'></td>" +
            "   <td styles='formTableValue' item='voteLimitTime' width='20%'></td>" +
            "   <td styles='formTableTitleRight' lable='voteResultVisible' width='16%'></td>" +
            "   <td styles='formTableValue' item='voteResultVisible' width='14%'></td>" +
            "   <td styles='formTableTitleRight' lable='votePersonVisible' width='16%'></td>" +
            "   <td styles='formTableValue' item='votePersonVisible' width='20%'></td>" +
            "</tr>"+
            "</table>";
        this.container.set("html", html);
        this.voteTable = this.container.getElement("table");

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form_vote = new MForm(this.container, this.data , {
                style: "forum",
                isEdited: true || this.isEdited || this.isNew,
                itemTemplate: {
                    voteLimitTime: {text: this.lp.voteLimitTime, tType : "date" },
                    voteResultVisible :{ text: this.lp.voteResultVisible, type : "select", selectText : this.lp.yesOrNo.split(","), selectValue : ["true","false"] },
                    votePersonVisible : { text: this.lp.votePersonVisible, type : "select", selectText : this.lp.votePersonValue.split(","), selectValue : ["true","false"] }
                }
            }, this );
            this.form_vote.load();
        }.bind(this), true);

        this.addVoteGroupTr = new Element("tr").inject( this.voteTable );
        var td = new Element( "td", {colspan : 6}).inject( this.addVoteGroupTr );
        var input = new Element("input", { type : "button", styles : this.css.addVoteGroupNode , value : this.lp.addVoteGroup }).inject(td);
        input.addEvent("click", function(){
            this.createGroupEdited();
        }.bind(this));

        if( this.options.isNew || !this.data.voteOptionGroupList ){
            this.createGroupEdited()
        }else{
            this.data.voteOptionGroupList.each( function( d , i){
                this.createGroupEdited(d, i);
            }.bind(this));
        }


    },
    createGroupEdited : function( d, index ){
        this.groupObject = this.groupObject || {};
        this.groupIndex = this.groupIndex || 1;
        var data = d || {};

        var contentUsePicture = false;
        if( data.voteOptions ){
            for( var i=0; i<data.voteOptions.length; i++ ){
                if( data.voteOptions[i].optionContentType === this.lp.picture ){
                    contentUsePicture = true;
                    break;
                }
            }
        }

        var tr = new Element("tr").inject( this.addVoteGroupTr, "before" );
        var td = new Element( "td", {colspan : 6}).inject( tr );
        var grid;
        MWF.xDesktop.requireApp("Template", "MGrid", function () {
            var vote_grid = new MGrid(td, data.voteOptions , {
                style: "forum",
                isEdited: true || this.isEdited || this.isNew,
                hasOperation : true,
                hasSequence : false,
                minTrCount : 2,
                tableAttributes : { width : "100%", border : "0" , cellpadding : "5", cellspacing : "0" },
                itemTemplate: {
                    optionTextContent: { text: this.lp.option, defaultValue : this.lp.option, notEmpty : true, defaultValueAsEmpty : true,  event : {
                        focus : function( item, ev ){ if( item.getValue() == this.lp.option )item.setValue("") }.bind(this),
                        blur : function( item, ev ){ if( item.getValue() == "" )item.setValue(this.lp.option) }.bind(this)
                    }},
                    optionPictureId : { type : "imageClipper",
                        text : "图片",
                        notEmpty : true,
                        disable : ( !contentUsePicture ),
                        style : {
                            imageStyle : this.css.optionPicture,
                            imageWrapStyle : this.css.optionPictureArea,
                            actionStyle : this.css.uploadActionNode
                        },
                        aspectRatio : 0,
                        ratioAdjustedEnable : true,
                        reference : this.app.advanceId || this.app.data.id,
                        referenceType: "forumDocument"
                    }
                },
                onQueryCreateTr : function(){
                    if( this.form ){
                        if( this.form.getItem( "voteContentType").getValue() == "true"){
                            this.itemTemplate["optionPictureId"].disable = false;
                        }else{
                            this.itemTemplate["optionPictureId"].disable = true;
                        }
                    }
                }
            }, this.app );
            vote_grid.setThTemplate("<tr><th style='text-align: left;font-size:14px;font-weight: normal;'></th><th button_add></th></tr>");
            vote_grid.setTrTemplate( "<tr><td><div item='optionTextContent' style='padding-top:10px'></div><div item='optionPictureId' style='padding-top: 5px;'></div></td><td button_remove style='vertical-align: top;padding-top:15px;'></td></tr>" );
            vote_grid.load();
            if( !d ){
                vote_grid.addTrs(3);
            }

            var th = vote_grid.tableHead.getElement("th");
            var html = "<div><span style='font-size:18px;margin-right:10px;' item='groupLabel' index='"+this.groupIndex+"'>"+this.lp.group+ (this.groupIndex) +"：</span>"+
                "<span item='groupName' style='margin-right:15px;'></span>"+
                "<span lable='voteChooseCount' style='margin-right:5px;'></span><span item='voteChooseCount' style='margin-right:5px;'></span><span style='margin-right:10px;''>"+this.lp.opt+"</span>" +
                "<span item='voteContentType' style='margin-right:15px;'></span>"+
                "<span style='margin-right:15px;' item='removeVoteGroup'></span></div>"+
                "<div item='tipNode'></div>";
            th.set("html", html);
            var groupNameNode = th.getElement("[item='groupLabel']");
            var tipNode = th.getElement("[item='tipNode']");
            var vote_form = new MForm(th, data , {
                style: "forum",
                verifyType : "batch",
                isEdited: true || this.isEdited || this.isNew,
                itemTemplate: {
                    groupName: { text: this.lp.voteSubject, defaultValue : this.lp.voteSubject, className : "inputTextNoWidth", style : { width : "500px" },
                        notEmpty : true, defaultValueAsEmpty : true,
                        event : {
                            focus : function( item, ev ){ if( item.getValue() == this.lp.voteSubject )item.setValue("") }.bind(this),
                            blur : function( item, ev ){ if( item.getValue() == "" )item.setValue(this.lp.voteSubject) }.bind(this)
                        },
                        onPostLoad : function(item){
                            item.tipNode = tipNode;
                        }
                    },
                    voteChooseCount : {text: this.lp.voteCountLimit, defaultValue : 1,  className : "inputTextNoWidth", tType : "number", style : { width : "30px" } },
                    voteContentType : { type : "checkbox", defaultValue : contentUsePicture ? "true" : "",  selectValue: ["true"], selectText : [ this.lp.uploadPicture ], event : {
                        change : function( item, ev ){ this.obj.setPicture( item.getValue(), this.grid ) }.bind( { obj : this, grid : vote_grid} )
                    }},
                    removeVoteGroup : {
                        disable : this.groupIndex == 1, type : "button", value : this.lp.removeVoteGroup, className : "removeVoteGroupNode", event : {
                            click : function( item ,ev ){
                                var _self = this;
                                _self.obj.app.confirm("warn", ev, _self.obj.lp.confirmRemoveVoteGroupTitle, _self.obj.lp.confirmRemoveVoteGroupContent, 350, 120, function(){
                                    _self.obj.removeGroup( parseInt( _self.node.get("index")) );
                                    this.close();
                                }, function(){
                                    this.close();
                                });
                            }.bind({ obj : this, node : groupNameNode })
                        }
                    }
                }
            }, this.app, this.css );
            vote_form.load();
            vote_grid.form = vote_form;
            this.groupObject[ this.groupIndex ] = {
                grid : vote_grid,
                form : vote_form,
                groupNameNode : groupNameNode
            };
            this.groupIndex++;
        }.bind(this), true);

    },
    removeGroup : function( key ){
        var voteObject = this.groupObject[ key ];
        var container = voteObject.grid.container;
        container.destroy();
        delete this.groupObject[ key ];
        for( var i=key; i<this.groupIndex-1; i++ ){
            this.groupObject[i] = this.groupObject[i+1];
            this.groupObject[i].groupNameNode.set("text",this.lp.group+ (i) +"：" );
            this.groupObject[i].groupNameNode.set("index",i);
        }
        this.groupObject[this.groupIndex-1] = null;
        this.groupIndex --;
    },
    getVoteInfor : function(){
        var flag = true;
        var result = this.form_vote.getResult(true, ",", true, false, true);
        if( !result )flag = false;
        if(flag)result.optionGroups = [];
        for( var key in this.groupObject ){
            var obj = this.groupObject[key];
            if( obj ){
                var formResult = obj.form.getResult(true, ",", true, false, true);
                if( !formResult )flag = false;
                var gridResult = obj.grid.getResult(true, ",", true, false, true);
                if( !gridResult )flag = false;
                if( flag ){
                    for( var i=0; i<gridResult.length;i++ ){
                        gridResult[i].optionContentType = (formResult.voteContentType === "true") ? this.lp.picture : this.lp.word;
                    }
                    formResult.voteOptions = gridResult;
                    result.optionGroups.push( formResult );
                }
            }
        }
        return flag ? result : null;
    },
    getVoteResult : function(){
        var flag = true;
        var result = {};
        result.id = this.data.id;
        result.optionGroups = [];
        for( var key in this.groupObject ){
            var g = {};
            g.selectedVoteOptionIds = [];
            var group = this.groupObject[key];
            g.id = group.id;
            for( var i=0;i<group.inputs.length; i++ ){
                var input = group.inputs[i];
                if( input.get("checked") ){
                    g.selectedVoteOptionIds.push( input.get("value") );
                }
            }
            if( g.selectedVoteOptionIds.length == 0 ){
                flag = false;
            }
            result.optionGroups.push( g );
        }
        if( !flag ){
            //this.app.notice("请至少选择一组投票再提交","error");
            if( this.data.voteOptionGroupList.length >= 1 ){
                this.app.notice( this.lp.notSelectGroupNotice ,"error");
            }else{
                this.app.notice( this.lp.notSelectItemNotice,"error");
            }
            return null;
        }else{
            return result;
        }
    },
    submitVote : function(){
        var data = this.getVoteResult();
        if(data){
            this.actions.submitVote( data, function(){
                this.reload();
            }.bind(this))
        }
    },
    setPicture: function( show, grid ){
        if( show == "true" ){
            grid.enableItem( "optionPictureId" )
        }else{
            grid.disableItem( "optionPictureId" );
        }
    },
    openLog : function( optionData ){
        var form = new MWF.xApplication.ForumDocument.VoteLog(this, this.data, {
            onPostOk : function( icon ){

            }.bind(this)
        });
        form.optionData = optionData;
        form.edit();
    },
    diffTime : function(startDate,endDate) {
        var diff=endDate.getTime() - startDate.getTime();//时间差的毫秒数

        //计算出相差天数
        var days=Math.floor(diff/(24*3600*1000));

        //计算出小时数
        var leave1=diff%(24*3600*1000);    //计算天数后剩余的毫秒数
        var hours=Math.floor(leave1/(3600*1000));
        //计算相差分钟数
        var leave2=leave1%(3600*1000);        //计算小时数后剩余的毫秒数
        var minutes=Math.floor(leave2/(60*1000));

        //计算相差秒数
        var leave3=leave2%(60*1000);      //计算分钟数后剩余的毫秒数
        var seconds=Math.round(leave3/1000);

        var returnStr = seconds + this.lp.second;
        if(minutes>0) {
            returnStr = minutes + this.lp.minute + returnStr;
        }
        if(hours>0) {
            returnStr = hours + this.lp.hour + returnStr;
        }
        if(days>0) {
            returnStr = days + this.lp.day + returnStr;
        }
        return returnStr;
    },
    getRandomColor : function(){
        //return "hsb(" + Math.random()  + ", 1, 1)";
        //return '#'+('00000'+(Math.random()*0x1000000<<0).toString(16)).slice(-6);
        var flag = false;
        do{
            var color = ('00000'+(Math.random()*0x1000000<<0).toString(16)).slice(-6);
            var r = parseInt( color.substr( 0, 2 ) , 16);
            var g = parseInt( color.substr( 3, 2 ) , 16);
            var b = parseInt( color.substr( 5, 2 ) , 16);
            if( r > 0xf0 && g>0xf0 && b>0xf0 ){
                flag = true
            }
        }
        while( flag );
        return '#'+color;
    }
});



MWF.xApplication.ForumDocument.VoteLog = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "750",
        "height": "600",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": false,
        "title": MWF.xApplication.Forum.LP.seeVotedPerson,
        "draggable": true,
        "closeAction": true,
        "closeByClickMask" : true
    },
    _createTableContent: function () {
        var html = "<table width='95%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='group' width='15%'></td>" +
            "    <td styles='formTableValue' item='group' width='85%'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='option'></td>" +
            "    <td styles='formTableValue' item='option'></td></tr>" +
            "<tr><td styles='formTableTitle'></td>" +
            "   <td styles='formTableValue'><div item='logArea' styles='logArea'></div></td>" +
            "   </tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.logArea = this.formTableArea.getElement("[item='logArea']");

        var groupNames = [], groupIds = [], optionNames = [], optionIds = [], currentGroup, currentOption;
        this.data.voteOptionGroupList.each( function( d, i ){
            var flag = false;
            groupNames.push(  MWF.xApplication.Forum.LP.group + (i+1) + "：" + d.groupName );
            groupIds.push( d.id );
            if(  this.optionData ){
                if ( this.optionData.optionGroupId == d.id ){
                    flag = true;
                }
            }else if( i == 0 ){
                flag = true;
            }
            if( flag ){
                currentGroup = d.id;
                d.voteOptions.each( function( opt, j ){
                    optionNames.push( opt.optionTextContent );
                    optionIds.push( opt.id );
                    if( this.optionData && this.optionData.id == opt.id ){
                        currentOption = opt.id
                    }else if( j == 0 ){
                        currentOption = opt.id
                    }
                }.bind(this))
            }
        }.bind(this));

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            var form = new MForm(this.formTableArea, this.data , {
                style: "forum",
                isEdited: true || this.isEdited || this.isNew,
                itemTemplate: {
                    group :{ type : "select",
                        text : MWF.xApplication.Forum.LP.group1,
                        selectText : groupNames,
                        selectValue : groupIds,
                        defaultValue : currentGroup,
                        event : { change : function( item, ev ){
                            var groupId = item.getValue();
                            var opts = [], ids = [];
                            this.data.voteOptionGroupList.each( function( d, i ){
                                if( groupId == d.id ){
                                    d.voteOptions.each( function( opt, j ){
                                        opts.push( opt.optionTextContent );
                                        ids.push( opt.id );
                                        if( j == 0 )currentOption = opt.id;
                                    })
                                }
                            }.bind(this));
                            item.form.getItem("option").resetItemOptions( ids, opts );
                            this.showLog( currentOption )
                        }.bind(this)}
                    },
                    option :{ type : "select",
                        text : MWF.xApplication.Forum.LP.option1,
                        selectText : optionNames,
                        selectValue : optionIds,
                        defaultValue : currentOption,
                        event : { change : function( item, ev ){
                            this.showLog( item.getValue() );
                        }.bind(this)}
                    }
                }
            }, this, this.css );
            form.load();
        }.bind(this), true);

        this.showLog( currentOption );
    },
    showLog : function( optId ){
        this.logArea.empty();
        this.recordView = new MWF.xApplication.ForumDocument.VoteRecordView( this.logArea, this.app, this, {
            templateUrl : this.explorer.path + "listItemVote.json",
            scrollEnable : true
        } );
        this.recordView.filterData = {
            subjectId : this.data.id,
            voteOptionId : optId
        };
        this.recordView.load();
    }
});

MWF.xApplication.ForumDocument.VoteRecordView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        data.index = index;
        return  new MWF.xApplication.ForumDocument.VoteRecordDocument(this.viewNode, data, this.explorer, this, null, data.index );
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=50;
        if(!pageNum)pageNum = 1;
        if( pageNum == 1 ){
        }else{
        }
        var filter = this.filterData || {};
        this.actions.listVoteRecord( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){

    },
    _create: function(){

    },
    _queryCreateViewNode: function(){
    },
    _postCreateViewNode: function( viewNode ){
    },
    _queryCreateViewHead:function(){
    },
    _postCreateViewHead: function( headNode ){
    }
});

MWF.xApplication.ForumDocument.VoteRecordDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if (itemData.votorName) new MWF.widget.O2Person({"name" : itemData.votorName }, itemNode,  {"style": "xform"});
    }
});
