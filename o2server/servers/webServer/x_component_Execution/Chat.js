MWF.xApplication.Execution.Chat = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style" : "default",
        "documentId" : ""
    },

    initialize: function (dialogContainer, editorContainer, app, actions, lp, options) {
        this.setOptions(options);
        this.dialogContainer = $(dialogContainer);
        this.editorContainer = $(editorContainer);
        this.app = app;
        this.actions = actions;
        this.lp = lp;
        this.userName = layout.desktop.session.user.name;
        this.userName = layout.desktop.session.user.distinguishedName;
        this.path = "/x_component_Execution/$Chat/"+this.options.style+"/";

        this.actionSettingPath = MWF.defaultPath+"/widget/$SimpleEditor/"+this.options.style+"/ActionSetting.js";

        this.cssPath = this.path + "css.wcss";
        this._loadCss();
        this.orgActionsAlpha = MWF.Actions.get("x_organization_assemble_control");
    },

    load: function () {

        this._loadEmotionSetting();
        if( this.dialogContainer ){
            this.dialogNode = new Element( "div.dialogNode", {
                "styles" : this.css.dialogNode
            }).inject(this.dialogContainer);

            //this.dialogContainer.setStyle("height","400px");

            var _self = this;
            MWF.require("MWF.widget.ScrollBar", function () {
                this.scrollObj = new MWF.widget.ScrollBar(this.dialogContainer, {
                    "indent": false,
                    "style": "default",
                    "where": "before",
                    "distance": 60,
                    "friction": 4,
                    "axis": {"x": false, "y": true},
                    "onScroll": function (y) {
                        //var scrollSize = _self.dialogContainer.getScrollSize();
                        //var clientSize = _self.dialogContainer.getSize();
                        //var scrollHeight = scrollSize.y - clientSize.y;
                        //
                        //if (y + 200 > scrollHeight  && _self.loadDialog) {
                        //    if (! _self.isItemsLoaded) _self.loadDialog();
                        //}
                        if( y == 0 ){
                            if (! _self.isItemsLoaded) _self.loadDialog();
                        }
                    }
                });

                //this.scrollObj.scrollVNode.setStyles({"margin-top":"300px"})
            }.bind(this), false);

            this.items = [];
            this.isItemsLoaded = false;
            this.isItemLoadding = false;
            this.loadDialog( function(){
                this.scrollToLater();
            }.bind(this) );
        }

        if(this.editorContainer){
            this.loadEditor( this.editorContainer, "" );
        }

    },
    scrollToLater : function(){
        setTimeout( function(){
            var clientSize = this.scrollObj.node.getSize();
            if( !this.scrollObj.scrollVNode )this.scrollObj.checkScroll();
            if( this.scrollObj.scrollVNode ){
                var scrollVNodeSize = this.scrollObj.scrollVNode.getSize();
                var maxY = (clientSize.y.toFloat())-(scrollVNodeSize.y.toFloat());
                this.scrollObj.scroll( maxY, null );
            }
        }.bind(this), 500 )
    },
    _loadEmotionSetting : function(){
        if( this.emotionSetting )return;
        var r = new Request({
            url: this.actionSettingPath,
            async: false,
            method: "get",
            onSuccess: function(responseText, responseXML){
                this.emotionSetting = MWF.widget.SimpleEditor.Actions.setting.emotion;
            }.bind(this),
            onFailure: function(xhr){
                alert(xhr.responseText);
            }
        });
        r.send();
    },
    loadDialog: function( callback ){
        if (!this.isItemsLoaded) {
            if (!this.isItemLoadding) {
                this.isItemLoadding = true;


                this.getCurrentPageData(function (json) { //alert(JSON.stringify(json))
                    var length = json.count;  //|| json.data.length;
                    if (length <= this.items.length) {
                        this.isItemsLoaded = true;
                    }


                    json.data.each(function (d) { //alert(JSON.stringify(d))
                        this.loadDialogItem( d );
                    }.bind(this));
                    this.isItemLoadding = false;
                    if( callback )callback();
                }.bind(this), 10 )
            }
        }



        //this.dialogContainer.scrollTo(0, 400 );
        //alert(this.dialogContainer.getScrollSize().y)



    },
    loadDialogItem: function( d , position){
        this.items.push(d.id);
        var isCurrentUser = ( d.senderName == this.userName );
        //var isCurrentUser = false

        var msg_li = new Element("div.msg_li", {"styles": this.css.msg_li}).inject(this.dialogNode, position || "top");

        var msg_item = new Element("div", {"styles": this.css.msg_item}).inject(msg_li);

        var msg_person = new Element("div", {
            "styles": this.css[isCurrentUser ? "msg_person_right" : "msg_person_left"]
        }).inject(msg_item);
        var msg_face = new Element("img", {
            "styles": this.css.msg_face
        }).inject(msg_person);
        this.setUserFace(d.senderName, msg_face);
        if (!isCurrentUser) {
            var msg_name = new Element("div", {
                "styles": this.css.msg_person_name,
                "text": d.senderName.split("@")[0]
            }).inject(msg_person)
        }

        var msg_arrow_left = new Element("div", {
            "styles": this.css[isCurrentUser ? "msg_arrow_right" : "msg_arrow_left"]
        }).inject(msg_item);
        var msg_content_body = new Element("div", {
            "styles": this.css[isCurrentUser ? "msg_content_body_right" : "msg_content_body_left"]
        }).inject(msg_item);

        //if (isCurrentUser) {
        //    var msg_del = new Element("span", {
        //        "styles": this.css.msg_del,
        //        "title": "点击删除"
        //    }).inject(msg_content_body);
        //    msg_del.addEvent("click", function () {
        //        if (confirm("删除后无法恢复，确定要删除该信息？")) {
        //            this.deleteSingleMessage(docid, seq, jQuery(this).parents(".msg_li"));
        //        }
        //    }.bind(this))
        //}

        var msg_content_area = new Element("div", {
            "styles": this.css.msg_content_area
        }).inject(msg_content_body);

        var msg_content_text = new Element("p", {
            "styles": this.css.msg_content_text,
            "html":  this.parseEmotion( d.content )
        }).inject(msg_content_area);

        var msg_content_time = new Element("p", {
            "styles": this.css.msg_content_time,
            "text": d.createTime
        }).inject(msg_content_area);
    },
    parseEmotion : function( content ){
        return content.replace(/\[emotion=(.*?)\]/g, function( a,b ){
            return "<img imagename='"+b+"' style='cursor:pointer;border:0;padding:2px;' " +" class='MWF_editor_emotion' src='"+ this.emotionSetting.imagesPath + b + this.emotionSetting.fileExt +"'>";
        }.bind(this));
    },
    //listMessageData: function( callback ){
    //    var json =  {
    //        "type": "success",
    //        "data": [
    //            {
    //                "id": "53a508ec-7862-4036-a273-c15830cd3f86",
    //                "createTime": "2016-04-19 15:38:50",
    //                "updateTime": "2016-04-19 15:38:50",
    //                "sequence": "2016041915385053a508ec-7862-4036-a273-c15830cd3f46",
    //                "content": "飞哥，找时间和孟总对一下设计，看看情况。飞哥，找时间和孟总对一下设计，看看情况飞哥，找时间和孟总对一下设计，看看情况飞哥，找时间和孟总对一下设计，看看情况",
    //                "person": "李义"
    //            },
    //            {
    //                "id": "53a508ec-7862-4036-a273-c15830cd3f87",
    //                "createTime": "2016-04-19 15:38:50",
    //                "updateTime": "2016-04-19 15:38:50",
    //                "sequence": "2016041915385053a508ec-7862-4036-a273-c15830cd3f46",
    //                "content": "下周我们就开始写代码.下周我们就开始写代码.下周我们就开始写代码.下周我们就开始写代码.下周我们就开始写代码下周我们就开始写代码.",
    //                "person": "金飞"
    //            }
    //        ],
    //        "date": "2016-05-27 14:20:07",
    //        "spent": 2,
    //        "size": 2,
    //        "count": 0,
    //        "position": 0,
    //        "message": ""
    //    }
    //
    //},
    getCurrentPageData:function(callback,count){
        if (!count)count = 5;
        var id = (this.items && this.items.length) ? this.items[this.items.length - 1] : "(0)";
        var filter = {"workId":this.options.workId} || {};

        this.actions.getChatListNext(id, count, filter, function (json) { //alert("action="+JSON.stringify(json))
            if (callback) callback(json);
        }.bind(this),null,false)


    },
    setUserFace: function(userName, faceNode ){

        faceNode.set("src",this.orgActionsAlpha.getPersonIcon(userName));
        //this.orgActionsAlpha.getPersonIcon(userName, function(url){
        //    faceNode.set("src",url);
        //    //var json =  { data : { icon : url } };
        //    //this.userCache[ name ] = json;
        //    //if( callback )callback( json );
        //}.bind(this), function(){ alert("err");
        //    //var json =  { data : { icon : "/x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.gif" } };
        //    //this.userCache[ name ] = json;
        //    //if( callback )callback( json );
        //}.bind(this));



        //this.getUserData( userName, function( userData ){
        //    var icon;
        //    if( userData.icon ){
        //        icon = "data:image/png;base64,"+userData.icon;
        //    }else{
        //        icon = this.path+ ( userData.genderType=="f" ? "female.png" : "man.png");
        //    }
        //    faceNode.set("src", icon );
        //})
    },
    //getUserData : function( userName, callback ){
    //    this.userData = this.userData || {};
    //    if( this.userData[userName] ){
    //        if(callback)callback(this.userData[userName])
    //    }else{
    //        this.actions.getPerson(function(json){
    //            this.userData[userName] = json.data;
    //            if(callback)callback(json.data)
    //        }.bind(this),null,userName,false)
    //    }
    //},
    sendMessage : function(content, callback){
        var d = {
            "workId" : this.options.workId,
            "createTime": new Date().format("db"),
            "content": content,
            "targetIdentity": this.app.identity,
            "senderName" : this.userName
        };
        this.actions.submitChat(d, function(json){

            }.bind(this),
            function(xhr,text,error){
                var errorText = error;
                if (xhr) errorMessage = xhr.responseText;
                var e = JSON.parse(errorMessage);
                if(e.message){
                    this.app.notice( e.message,"error");
                }else{
                    this.app.notice( errorText,"error");
                }
            }.bind(this),false);

        this.loadDialogItem(d, "bottom");
        if(callback)callback();
    },
    loadEditor: function ( container, data ) {
        MWF.require("MWF.widget.SimpleEditor", function () {
            this.editor = new MWF.widget.SimpleEditor({
                "style": "chatReceive",			//使用的样式文件夹
                "hasHeadNode" : false,
                "hasTitleNode": false,		//是否有标题区
                "editorDisabled": false,	//编辑区是否能进行编辑
                "hasToolbar": true,		//是否生成操作条
                "toolbarDisable": false,	//操作条是否失效
                "hasSubmit": true,			//是否形成提交按钮
                "submitDisable": false,	//提交按钮是否失效
                "hasCustomArea": true,		//是否有提示区
                "paragraphise": false,		//回车是否形成段落
                "minHeight": 100,			//最小高度
                "maxHeight": 100,			//编辑区的最大高度
                "overFlow": "visible",		//可选项为 visible, auto 和 max ,visible 高度随内容变化， auto 内容高度超过minHeight时滚动条，max 内容高度超过maxHeight时滚动条(ie6 和 文档模式为杂项时，失效)
                "width": "100%",				//编辑器的宽度
                "action": "Emotion",	//操作条上面有哪些操作，如果是all，表示使用 toolbarItems.json 里配置的所有操作，否则传入操作的 action ，用 空格隔开，比如 "Image | Emotion"
                "limit": 255,				//输入长度限制，0表示无限制
                "onQueryLoad": function () {
                    return true;
                },
                "onPostLoad": function ( editor ) {
                    editor.setCustomInfo("");
                },
                "onSubmitForm": function ( editor ) {
                    var content = editor.getContent(true);
                    if( content.trim() != "<br>"  && content.trim()!=""){
                        this.sendMessage( content, function(){
                            editor.setContent("");
                            this.scrollToLater();
                        }.bind(this));
                    }else{
                        this.app.notice("不能发送空消息","error");
                    }
                }.bind(this)
            }, container, data||"", null, null);
            this.editor.load();
        }.bind(this));
    }

}); 