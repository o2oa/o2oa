MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "widget.ColorPicker", null, false);
MWF.xDesktop.requireApp("Template", "MSelector", null, false);

MWF.xApplication.MinderEditor.FontFamily = new Class({
    Extends: MSelector,
    options : {
        "style": "minderFont",
        "width": "240px",
        "height": "28px",
        "defaultOptionLp" : "字体",
        "textField" : "name",
        "valueField" : "val",
        "event" : "mouseenter",
        "isSetSelectedValue" : true,
        "isChangeOptionStyle" : true,
        "emptyOptionEnable" : false
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var fontFamilyList = [{
            name: '宋体',
            val: '宋体,SimSun'
        }, {
            name: 'Microsoft YaHei',
            val: 'Microsoft YaHei,Microsoft YaHei'
        }, {
            name: '楷体',
            val: '楷体,楷体_GB2312,SimKai'
        }, {
            name: '黑体',
            val: '黑体, SimHei'
        }, {
            name: '隶书',
            val: '隶书, SimLi'
        }, {
            name: 'Andale Mono',
            val: 'andale mono'
        }, {
            name: 'Arial',
            val: 'arial,helvetica,sans-serif'
        }, {
            name: 'arialBlack',
            val: 'arial black,avant garde'
        }, {
            name: 'Comic Sans Ms',
            val: 'comic sans ms'
        }, {
            name: 'Impact',
            val: 'impact,chicago'
        }, {
            name: 'Times New Roman',
            val: 'times new roman'
        }, {
            name: 'Sans-Serif',
            val: 'sans-serif'
        }];
        if(callback)callback( fontFamilyList );
    },
    _postCreateItem: function( itemNode, data ){
        itemNode.setStyles( {
            "font-family": data.val,
            "font-size" : "14px",
            "min-height" : "30px",
            "line-height" : "30px"
        } );
    }
});

MWF.xApplication.MinderEditor.FontSize = new Class({
    Extends: MSelector,
    options : {
        "style": "minderFont",
        "width": "80px",
        "height": "28px",
        "defaultOptionLp" : "字号",
        "isSetSelectedValue" : true,
        "isChangeOptionStyle" : true,
        "emptyOptionEnable" : false,
        "event" : "mouseenter"
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var fontSizeList = ["10", "12", "16", "18", "24", "32", "48"];
        if(callback)callback( fontSizeList );
    },
    _postCreateItem: function( itemNode, data ){
        itemNode.setStyles( {
            "font-size" :  data.value +"px",
            "min-height" : ( parseInt(data.value) + 6) +"px",
            "line-height" : ( parseInt(data.value) + 6) +"px"
        } );
    }
});

MWF.xApplication.MinderEditor.PriorityImage = new Class({
    Extends: MSelector,
    options : {
        "style": "minderProgress",
        "width": "130px",
        "defaultOptionLp" : "",
        "valueField" : "command",
        "isSetSelectedValue" : false,
        "isChangeOptionStyle" : true,
        "emptyOptionEnable" : false,
        "event" : "mouseenter"
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var list = [
            {
                command : "0",
                position : "0 -180px",
                title : "移除优先级"
            },
            {
                command : "1",
                position : "0 0px",
                title : "优先级1"
            },
            {
                command : "2",
                position : "0 -20px",
                title : "优先级2"
            },
            {
                command : "3",
                position : "0 -40px",
                title : "优先级3"
            },
            {
                command : "4",
                position : "0 -60px",
                title : "优先级4"
            },
            {
                command : "5",
                position : "0 -80px",
                title : "优先级5"
            },
            {
                command : "6",
                position : "0 -100px",
                title : "优先级6"
            },
            {
                command : "7",
                position : "0 -120px",
                title : "优先级7"
            },
            {
                command : "8",
                position : "0 -140px",
                title : "优先级8"
            },
            {
                command : "9",
                position : "0 -160px",
                title : "优先级9"
            }
        ];
        if(callback)callback( list );
    },
    _postCreateItem: function( itemNode, data ){

    },
    loadContent : function( data ){
        if( !this.contentTooltip ){
            var width = parseInt(this.options.width)+"px";
            this.css.tooltipNode.width = width;
            this.css.tooltipNode["max-width"] = width;
            var options = Object.merge({
                nodeStyles : this.css.tooltipNode,
                onPostInitialize : function(){
                    if(this.options.trigger == "immediately" ){
                        this.contentTooltip.load();
                    }
                }.bind(this),
                onHide : function(){
                    this.status = "hidden";
                }.bind(this)
            }, this.options.tooltipsOptions );
            this.contentTooltip = new MWF.xApplication.MinderEditor.PriorityImage.Tootips( this.dropdownContainer || this.app.content, this.node, this.app, data, options );
            this.contentTooltip.selector = this;
        }
    }
});

MWF.xApplication.MinderEditor.PriorityImage.Tootips = new Class({
    Extends: MSelector.Tootips,
    options : {
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "center", //x轴上left center right,  auto 系统自动计算
            y : "bottom" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : true
    },
    _customNode : function( node, contentNode ){
        //var width = ( parseInt( this.selector.options.width )  )+ "px";
        //node.setStyles({
        //    "width": width,
        //    "max-width": width
        //});
        this.createItemList( this.data, contentNode )
    },
    createItemList:function(data, node){
        data = data || [];
        var _selector = this.selector;
        this.css = _selector.css;

        _selector.listContentNode = new Element("div.listContentNode",{
            "styles":this.css.listContentNode
        }).inject( node );

        //_selector.listContentNode.setStyles({
        //    "width": node.getSize().x+"px"
        //});

        _selector.listNode = new Element("div.listNode",{
            "styles":this.css.listNode
        }).inject(_selector.listContentNode);
        _selector.setScrollBar(_selector.listNode);

        data.each(function(d){
            this.createItem( d );
        }.bind(this));

    },
    createItem: function( data ){
        var _selector = this.selector;
        var listItemNode = new Element("div.listItemNode",{
            "styles":this.css.listItemNode,
            "title" : data.title
        }).inject(_selector.listNode);
        listItemNode.setStyles({
            "background": "url("+ _selector.path + _selector.options.style + "/icon/priority.png) no-repeat "+ data.position
        });

        if(data)listItemNode.store("data",data);

        listItemNode.addEvents({
            "click":function(ev){
                var _self = this.obj;
                var data = this.itemNode.retrieve( "data" );
                _self.selector.setCurrentItem( this.itemNode );
                _self.selector._selectItem( this.itemNode, data );
                _self.selector.fireEvent("selectItem", [ this.itemNode, data ] );
                _self.hide();
                ev.stopPropagation();
            }.bind({ obj : this, itemNode : listItemNode }),
            "mouseover":function(){
                if( this.obj.selector.currentItemNode != this.itemNode || !this.obj.selector.options.isChangeOptionStyle ){
                    this.itemNode.setStyles( this.obj.selector.css.listItemNode_over );
                }
            }.bind( {obj : this, itemNode : listItemNode }),
            "mouseout":function(){
                if( this.obj.selector.currentItemNode != this.itemNode || !this.obj.selector.options.isChangeOptionStyle ){
                    this.itemNode.setStyles( this.obj.selector.css.listItemNode );
                }
            }.bind( {obj : this, itemNode : listItemNode })
        });
        _selector.itemNodeList.push( listItemNode );
        _selector.itemNodeObject[ data[ _selector.valueField ] ] = listItemNode;

        var isCurrent = false;
        if( _selector.currentItemData ){
            isCurrent = data[ _selector.valueField ] == _selector.currentItemData[ _selector.valueField ];
        }else if( _selector.value ){
            isCurrent = data[ _selector.valueField ] == _selector.value;
        }else if( _selector.text ){
            isCurrent = data[ _selector.textField ] == _selector.text;
        }
        if( isCurrent )_selector.setCurrentItem( listItemNode );

        _selector._postCreateItem(listItemNode, data)
    }
});

MWF.xApplication.MinderEditor.ProgressImage = new Class({
    Extends: MSelector,
    options : {
        "style": "minderProgress",
        "width": "130px",
        "defaultOptionLp" : "",
        "valueField" : "command",
        "isSetSelectedValue" : false,
        "isChangeOptionStyle" : true,
        "emptyOptionEnable" : false,
        "event" : "mouseenter"
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var list = [
            {
                command : "0",
                position : "0 -180px",
                title : "移除进度"
            },
            {
                command : "1",
                position : "0 0px",
                title : "未开始"
            },
            {
                command : "2",
                position : "0 -20px",
                title : "完成1/8"
            },
            {
                command : "3",
                position : "0 -40px",
                title : "完成2/8"
            },
            {
                command : "4",
                position : "0 -60px",
                title : "完成3/8"
            },
            {
                command : "5",
                position : "0 -80px",
                title : "完成4/8"
            },
            {
                command : "6",
                position : "0 -100px",
                title : "完成5/8"
            },
            {
                command : "7",
                position : "0 -120px",
                title : "完成6/8"
            },
            {
                command : "8",
                position : "0 -140px",
                title : "完成7/8"
            },
            {
                command : "9",
                position : "0 -160px",
                title : "全部完成"
            }
        ];
        if(callback)callback( list );
    },
    _postCreateItem: function( itemNode, data ){

    },
    loadContent : function( data ){
        if( !this.contentTooltip ){
            var width = parseInt(this.options.width)+"px";
            this.css.tooltipNode.width = width;
            this.css.tooltipNode["max-width"] = width;
            var options = Object.merge({
                nodeStyles : this.css.tooltipNode,
                onPostInitialize : function(){
                    if(this.options.trigger == "immediately" ){
                        this.contentTooltip.load();
                    }
                }.bind(this),
                onHide : function(){
                    this.status = "hidden";
                }.bind(this)
            }, this.options.tooltipsOptions );
            this.contentTooltip = new MWF.xApplication.MinderEditor.ProgressImage.Tootips( this.dropdownContainer || this.app.content, this.node, this.app, data, options );
            this.contentTooltip.selector = this;
        }
    }
});

MWF.xApplication.MinderEditor.ProgressImage.Tootips = new Class({
    Extends: MSelector.Tootips,
    options : {
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "center", //x轴上left center right,  auto 系统自动计算
            y : "bottom" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : true
    },
    _customNode : function( node, contentNode ){
        //var width = ( parseInt( this.selector.options.width )  )+ "px";
        //node.setStyles({
        //    "width": width,
        //    "max-width": width
        //});
        this.createItemList( this.data, contentNode )
    },
    createItemList:function(data, node){
        data = data || [];
        var _selector = this.selector;
        this.css = _selector.css;

        _selector.listContentNode = new Element("div.listContentNode",{
            "styles":this.css.listContentNode
        }).inject( node );

        //_selector.listContentNode.setStyles({
        //    "width": node.getSize().x+"px"
        //});

        _selector.listNode = new Element("div.listNode",{
            "styles":this.css.listNode
        }).inject(_selector.listContentNode);
        _selector.setScrollBar(_selector.listNode);

        data.each(function(d){
            this.createItem( d );
        }.bind(this));

    },
    createItem: function( data ){
        var _selector = this.selector;
        var listItemNode = new Element("div.listItemNode",{
            "styles":this.css.listItemNode,
            "title" : data.title
        }).inject(_selector.listNode);
        listItemNode.setStyles({
            "background": "url("+ _selector.path + _selector.options.style + "/icon/progress.png) no-repeat "+ data.position
        });

        if(data)listItemNode.store("data",data);

        listItemNode.addEvents({
            "click":function(ev){
                var _self = this.obj;
                var data = this.itemNode.retrieve( "data" );
                _self.selector.setCurrentItem( this.itemNode );
                _self.selector._selectItem( this.itemNode, data );
                _self.selector.fireEvent("selectItem", [ this.itemNode, data ] );
                _self.hide();
                ev.stopPropagation();
            }.bind({ obj : this, itemNode : listItemNode }),
            "mouseover":function(){
                if( this.obj.selector.currentItemNode != this.itemNode || !this.obj.selector.options.isChangeOptionStyle ){
                    this.itemNode.setStyles( this.obj.selector.css.listItemNode_over );
                }
            }.bind( {obj : this, itemNode : listItemNode }),
            "mouseout":function(){
                if( this.obj.selector.currentItemNode != this.itemNode || !this.obj.selector.options.isChangeOptionStyle ){
                    this.itemNode.setStyles( this.obj.selector.css.listItemNode );
                }
            }.bind( {obj : this, itemNode : listItemNode })
        });
        _selector.itemNodeList.push( listItemNode );
        _selector.itemNodeObject[ data[ _selector.valueField ] ] = listItemNode;

        var isCurrent = false;
        if( _selector.currentItemData ){
            isCurrent = data[ _selector.valueField ] == _selector.currentItemData[ _selector.valueField ];
        }else if( _selector.value ){
            isCurrent = data[ _selector.valueField ] == _selector.value;
        }else if( _selector.text ){
            isCurrent = data[ _selector.textField ] == _selector.text;
        }
        if( isCurrent )_selector.setCurrentItem( listItemNode );

        _selector._postCreateItem(listItemNode, data)
    }
});

MWF.xApplication.MinderEditor.SaveTooltips = new Class({
    Implements: [Options, Events],
    Extends: MTooltips,
    options: {
        style : "default",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x 轴上left center right, auto 系统自动计算
            y : "auto" //y轴上top middle bottom,  auto 系统自动计算
        },
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        nodeStyles : {
            "min-width" : "50px",
            "padding" : "0px",
            "border-radius" : "3px"
        }
    },
    _customNode : function( node, contentNode ){

        var div = new Element("div", {
            "text" :  (this.app.autoSaveInter / 60000) + "分钟自动保存一次",
            "styles" : { "margin" : "10px" }
        }).inject( contentNode );
        new Element("input",{
            type : "checkbox",
            value : "true",
            checked : this.app.autoSave,
            events : {
                change : function( el ){
                    if( el.target.get("checked") ){
                        this.app.startAutoSave();
                    }else{
                        this.app.stopAutoSave();
                    }
                }.bind(this)
            }
        }).inject( div, "top" );

        //var list = new Element("div", { styles : this.app.css.selectorListNode }).inject( contentNode );

        //var saveAsDiv = new Element("div", { "text": "另存为",
        //    styles : this.app.css.selectorListItemNode,
        //    events : {
        //        mouseover : function(el){
        //            el.target.setStyles( this.app.css.selectorListItemNode_over )
        //        }.bind(this),
        //        mouseleave : function(el){
        //            el.target.setStyles( this.app.css.selectorListItemNode )
        //        }.bind(this),
        //        click : function(){
        //            this.app.openSaveAsDialog();
        //        }.bind(this)
        //    }
        //}).inject( list );
        //
        //var renameDiv = new Element("div", { "text": "重命名",
        //    styles : this.app.css.selectorListItemNode,
        //    events : {
        //        mouseover : function(el){
        //            el.target.setStyles( this.app.css.selectorListItemNode_over )
        //        }.bind(this),
        //        mouseleave : function(el){
        //            el.target.setStyles( this.app.css.selectorListItemNode )
        //        }.bind(this),
        //        click : function(){
        //            this.app.openRenameDialog();
        //        }.bind(this)
        //    }
        //}).inject( list );
    }
});

MWF.require("MWF.widget.ImageClipper", null, false);

MWF.xApplication.MinderEditor.HyperLinkForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 700,
        //"height": 300,
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "链接"
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='url' width='20%'></td>" +
            "    <td styles='formTableValue14' item='url' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='title'></td>" +
            "    <td styles='formTableValue14' item='title' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        var data = this.app.minder.queryCommandValue('HyperLink');

        this.form = new MForm(this.formTableArea, data, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                url: { text : "链接地址",  notEmpty : true,
                    validRule : { isInvalid : function( value, it ){
                        var urlRegex = '^(?!mailto:)(?:(?:http|https|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?:(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[0-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))|localhost)(?::\\d{2,5})?(?:(/|\\?|#)[^\\s]*)?$';
                        var R_URL = new RegExp(urlRegex, 'i');
                        return R_URL.test( value )
                    }.bind(this)},
                    validMessage : { isInvalid : "请输入正确的链接" },
                    attr : { placeholder : "必填：以 http(s):// 或 ftp:// 开头" }
                },
                title: { text : "提示文本", attr : { placeholder : "选填：鼠标在链接上悬停时提示的文本" } }
            }
        }, this.app);
        this.form.load();

    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.removeAction = new Element("button.inputCancelButton", {
            "styles": this.css.inputCancelButton,
            "text": "删除链接"
        }).inject(this.formBottomNode);

        this.removeAction.addEvent("click", function (e) {
            this.remove(e);
        }.bind(this));

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            this.app.minder.execCommand('HyperLink', data.url, data.title || '');
            this.close();
        }
    },
    remove: function( ev ){
        this.app.minder.execCommand('HyperLink', null );
        this.close();
    }
});

MWF.xApplication.MinderEditor.ImageForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 800,
        "height": 640,
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "图片"
    },
    createContent: function () {

        this.createTab();

        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);

        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);

        this._createTableContent();
    },
    _createTableContent: function () {

        this.linkContainer = new Element("div.linkContainer").inject(this.formTableArea);

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='url' width='20%'></td>" +
            "    <td styles='formTableValue14' item='url' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='title'></td>" +
            "    <td styles='formTableValue14' item='title' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle'>预览：</td>" +
            "    <td styles='formTableValue14' item='preview' colspan='3'></td></tr>" +
            "</table>";
        this.linkContainer.set("html", html);

        var data = this.app.minder.queryCommandValue('image');

        this.linkform = new MForm(this.linkContainer, data, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                url: { text : "图片地址",  notEmpty : true,
                    validRule : { isInvalid : function( value, it ){
                        var R_URL = /^https?\:\/\/\w+/;
                        return R_URL.test( value )
                    }.bind(this)},
                    validMessage : { isInvalid : "请输入正确的链接" },
                    attr : { placeholder : "必填：以 http(s):// 开始" },
                    event : { blur : function( it ){
                        if( it.getValue() )it.form.getItem("preview").setValue( it.getValue() )
                    }.bind(this)}
                },
                title: { text : "提示文本", attr : { placeholder : "选填：鼠标在图片上悬停时提示的文本" } },
                preview : { type : "img", defaultValue : data.url || "", style : { "max-width" : "400px", "max-height" : "260px" } }
            }
        }, this.app);
        this.linkform.load();



        this.uploadContainer = new Element("div.uploadContainer", { styles : {"display":"none"} }).inject( this.formTableArea );

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td item='image' colspan='4' style='padding-bottom: 10px;'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='title2' width='20%'></td>" +
            "    <td styles='formTableValue14' item='title2' colspan='3'></td></tr>" +
            "</table>";
        this.uploadContainer.set("html", html);

        var data = this.app.minder.queryCommandValue('image');

        this.uploadform = new MForm(this.uploadContainer, data, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                title2: { text : "提示文本", attr : { placeholder : "选填：鼠标在图片上悬停时提示的文本" } }
            }
        }, this.app);
        this.uploadform.load();

        this.image = new MWF.widget.ImageClipper(this.uploadContainer.getElement("[item='image']"), {
            "aspectRatio": 0,
            "description" : "",
            "imageUrl" : "",
            "ratioAdjustedEnable" : true,
            "reference" :  this.app.data.id || "1111",
            "referenceType": "mindInfo",
            "fromFileEnable" : false,
            "resetEnable" : true
        });
        this.image.load();

    },
    createTab: function(){
        var _self = this;

        this.tabContainer = new Element("div.formTabContainer",{
            styles : this.css.formTabContainer
        }).inject(this.formNode);

        var tabNode = new Element("div.formTabNode", {
            "styles": this.css.formTabNode,
            "text" : "外链图片"
        }).inject(this.tabContainer);
        tabNode.addEvents({
            "mouseover" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode_over) }.bind({node : tabNode }),
            "mouseout" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode) }.bind({node : tabNode }),
            "click":function(){
                if( _self.currentTabNode )_self.currentTabNode.setStyles(_self.css.formTabNode);
                _self.currentTabNode = this.node;
                this.node.setStyles(_self.css.formTabNode_current);
                _self.linkContainer.setStyle("display","");
                _self.uploadContainer.setStyle("display","none");
            }.bind({ node : tabNode })
        });
        tabNode.setStyles( this.css.formTabNode_current );
        _self.currentTabNode = tabNode;

        var tabNode = new Element("div.tabNode", {
            "styles": this.css.formTabNode,
            "text" : "上传图片"
        }).inject(this.tabContainer);
        tabNode.addEvents({
            "mouseover" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode_over) }.bind({node : tabNode }),
            "mouseout" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode) }.bind({node : tabNode }),
            "click":function(){
                if( _self.currentTabNode )_self.currentTabNode.setStyles(_self.css.formTabNode);
                _self.currentTabNode = this.node;
                this.node.setStyles(_self.css.formTabNode_current);
                _self.linkContainer.setStyle("display","none");
                _self.uploadContainer.setStyle("display","");
            }.bind({ node : tabNode })
        })
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.removeAction = new Element("button.inputCancelButton", {
            "styles": this.css.inputCancelButton,
            "text": "删除图片"
        }).inject(this.formBottomNode);

        this.removeAction.addEvent("click", function (e) {
            this.remove(e);
        }.bind(this));

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function(){
        if( this.image.getResizedImage() ){
            this.image.uploadImage( function( json ){
                var data = {
                    url : MWF.xDesktop.getImageSrc( json.id ),
                    title :  this.uploadform.getResult(true,null,true,false,true)["title2"]
                };
                this.app.minder.execCommand('image', data.url, data.title || '', json.id );
                this.close();
            }.bind(this));
        }else{
            var data = this.linkform.getResult(true,null,true,false,true);
            if( data ){
                this.app.minder.execCommand('image', data.url, data.title || '', '');
                this.close();
            }
        }
    },
    remove: function( ev ){
        this.app.minder.execCommand('image', '' );
        this.close();
    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options.width ? this.options.width : "50%";
        if (!height)height = this.options.height ? this.options.height : "50%";
        if (!top) top = this.options.top ? this.options.top : 0;
        if (!left) left = this.options.left ? this.options.left : 0;

        var containerSize = this.container.getSize();
        if( containerSize.x < width )width = containerSize.x;
        if( containerSize.y < height )height = containerSize.y;

        var allSize = this.app.content.getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);

        top = top || parseInt((limitHeight - height) / 2, 10); //+appTitleSize.y);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.formAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.formNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};
        var tabSize = this.tabContainer ? this.tabContainer.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y - tabSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.formContentNode.setStyles({
            "height": "" + contentHeight + "px"
        });
        this.formTableContainer.setStyles({
            "height": "" + contentHeight + "px"
        });
    }
});

MWF.xApplication.MinderEditor.SaveAsForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 800,
        //"height": 300,
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "另存为"
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='folder' width='25%'></td>" +
            "    <td styles='formTableValue14' item='folder' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='newname'></td>" +
            "    <td styles='formTableValue14' item='newname' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.form = new MForm(this.formTableArea, this.data || {}, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                folder: { text : "选择文件夹",  notEmpty : true, attr : { readonly : true }, defaultValue : "根目录" },
                newname: { text : "新文件名称" }
            }
        }, this.app);
        this.form.load();
        this.loadFolderSelect();
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            this.app.saveAs( this.folderId || "root", data.newname );
            this.close();
        }
    },
    loadFolderSelect: function() {
        MWF.xDesktop.requireApp("Minder", "Common", null, false);
        this.folderSelect =  new MWF.xApplication.Minder.FolderSelector( this.app.content, this.form.getItem("folder").getElements()[0], this.app, {}, {
            onSelect : function( folderData ){
                this.form.getItem("folder").setValue( folderData.name );
                this.folderId = folderData.id;
            }.bind(this)
        } );

    }
});

MWF.xApplication.MinderEditor.NewNameForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 700,
        //"height": 300,
        "height": "200",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : "重命名"
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='newname' width='25%'></td>" +
            "    <td styles='formTableValue14' item='newname' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.form = new MForm(this.formTableArea, this.data || {}, {
            isEdited: true,
            style : "minder",
            hasColon : true,
            itemTemplate: {
                newname: { text : "新文件名称", notEmpty : true }
            }
        }, this.app);
        this.form.load();

    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": "关闭"
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            this.app.setNewName( data.newname );
            this.close();
        }
    }
});

MWF.xApplication.MinderEditor.ExportTooltips = new Class({
    Implements: [Options, Events],
    Extends: MTooltips,
    options: {
        style : "default",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x 轴上left center right, auto 系统自动计算
            y : "auto" //y轴上top middle bottom,  auto 系统自动计算
        },
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        nodeStyles : {
            "padding-top" : "5px",
            "min-width" : "210px",
            "padding" : "0px",
            "border-radius" : "5px"
        }
    },
    _loadCustom : function( callback ){
        this.css = this.app.css;

        if( document.id( "km-csrf" ) ){
            document.id( "km-csrf").set( "value", this.app.data.id );
        }else{
            new Element("input", {
                id : "km-csrf",
                "styles" : { display : "none" }
            }).inject( this.contentNode )
        }
        this.app.loadExtentResource( function(){
            var contentNode = this.contentNode;
            var list = new Element("div", { styles : this.css.selectorListNode }).inject( contentNode );

            var protocols = [];

            var pool = kityminder.data.getRegisterProtocol();
            for(var name in pool) {
                if (pool.hasOwnProperty(name) && pool[name].encode) {
                    protocols.push(pool[name]);
                }
            }
            protocols.each(function( p ) {
                new Element("div", { "text": (p.fileDescription + "("+ p.fileExtension +")"),
                    styles : this.css.selectorListItemNode,
                    events : {
                        mouseover : function(el){
                            el.target.setStyles( this.css.selectorListItemNode_over )
                        }.bind(this),
                        mouseleave : function(el){
                            el.target.setStyles( this.css.selectorListItemNode )
                        }.bind(this),
                        click : function(){
                            this.exportFile(p);
                        }.bind(this)
                    }
                }).inject( list );
            }.bind(this));
            if(callback)callback();
        }.bind(this))

    },
    exportFile : function( protocol ){

        this.createProgressBar();

        this.minder = this.app.minder;
        var fileName = this.app.data.name || this.minder.getRoot().getText();
        filename = fileName + protocol.fileExtension;
        var mineType = protocol.mineType || 'text/plain';

        var options = {
            download: true,
            filename: filename
        };

        if( protocol.name == "png" ){
            var converter = new MWF.xApplication.MinderEditor.Converter(this.app, this.minder);
            converter.toPng(null, null, function( img ){
                var link = new Element("a", {"text": filename}).inject(this.progressBarTextNode);
                link.download = fileName;
                link.href = URL.createObjectURL(img);
                //link.href = "data:text/plain," + content;

                var evt = document.createEvent("HTMLEvents");
                evt.initEvent("click", false, false);
                link.dispatchEvent(evt);
                link.click();

                this.progressBarNode.destroy();
                this.progressBarNode = null;
                this.progressBarTextNode = null;
                this.progressBar = null;
                this.progressBarPercent = null;
            }.bind(this));
        }else{
            this.minder.exportData(protocol.name, options).then(function(data) {

                if (protocol.name == 'freemind') {
                    return;
                }


                switch (protocol.dataType) {
                    case 'text':
                        return this.doDownload(this.buildDataUrl(mineType, data), filename, 'text');
                    case 'base64':
                        return this.doDownload(data, filename, 'base64');
                    case 'blob':
                        return null;
                }

                return null;

            }.bind(this));
        }
    },
    doDownload : function (url, filename, type) {
        var Promise = kityminder.Promise;
        var stamp = +new Date() * 1e5 + Math.floor(Math.random() * (1e5 - 1));

        stamp = stamp.toString(36);

        var ret = new Promise(function(resolve, reject) {
            var ticker = 0;
            var MAX_TICK = 30;
            var interval = 1000;

            //function check() {
            //    if (document.cookie.indexOf(stamp + '=1') != -1) return resolve([stamp, ticker]);
            //    if (++ticker > MAX_TICK) {
            //        resolve([stamp, ticker]);
            //    }
            //    setTimeout(check, interval);
            //}
            //
            //setTimeout(check, interval);


            var content = url.split(',')[1];

            this.saveToLocal( content, filename );

            return resolve([stamp, ticker]);

        }.bind(this));


        //var form = new Element("form",{
        //    'action': 'home/download',
        //    'method': 'POST',
        //    'accept-charset': 'utf-8'
        //});

        //var $content = new Element("input",{
        //    name: 'content',
        //    type: 'hidden',
        //    value: decodeURIComponent(content)
        //}).inject( from );
        //
        //var $type = new Element("input",{
        //    name: 'type',
        //    type: 'hidden',
        //    value: type
        //}).inject( from );
        //
        //var $filename = new Element("input",{
        //    name: 'filename',
        //    type: 'hidden',
        //    value: filename
        //}).inject( from );
        //
        //var $csrfToken = new Element("input",{
        //    name: 'csrf_token',
        //    type: 'hidden',
        //    value: $('#km-csrf').val()
        //}).inject( from );
        //
        //
        //if (kity.Browser.ie) {
        //    new Element("input",{
        //        name : "iehack",
        //        value : "1"
        //    }).inject( form );
        //}
        //
        //new Element("input",{
        //    name : "stamp",
        //    value : stamp
        //}).inject( form );
        //
        //form.inject('body');
        //form.submit();
        //form.destroy();

        return ret;
    },
    buildDataUrl: function (mineType, data) {
        return 'data:' + mineType + '; utf-8,' + encodeURIComponent(data);
    },
    saveToLocal: function( data, filename ){
        if (window.hasOwnProperty("ActiveXObject")){
            var win = window.open("", "_blank");
            win.document.write(decodeURIComponent(data));
        }else{
            this.downloadFile(filename, decodeURIComponent(data));
        }


        this.progressBarNode.destroy();
        this.progressBarNode = null;
        this.progressBarTextNode = null;
        this.progressBar = null;
        this.progressBarPercent = null;

        this.close();
    },

    downloadFile: function(fileName, content){
        var link = new Element("a", {"text": this.data.name}).inject(this.progressBarTextNode);
        var blob = new Blob([content]);
        link.download = fileName;
        link.href = URL.createObjectURL(blob);
        //link.href = "data:text/plain," + content;

        var evt = document.createEvent("HTMLEvents");
        evt.initEvent("click", false, false);
        link.dispatchEvent(evt);
        link.click();
    },
    createProgressBar: function(){
        this.node.hide();
        this.progressBarNode = new Element("div", {"styles": this.css.progressBarNode});
        this.progressBarNode.inject(this.container);
        this.progressBarNode.position({
            relativeTo: this.container,
            position: 'center',
            edge: 'center'
        });

        this.progressBarTextNode = new Element("div", {"styles": this.css.progressBarTextNode}).inject(this.progressBarNode);
        this.progressBar = new Element("div", {"styles": this.css.progressBar}).inject(this.progressBarNode);
        this.progressBarPercent = new Element("div", {"styles": this.css.progressBarPercent}).inject(this.progressBar);

    }
});

MWF.xApplication.MinderEditor.ExportForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 400,
        //"height": 300,
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "hasBottom" : false,
        "title" : "选择导出的文件类型"
    },
    _createTableContent : function( callback ){
        this.css = this.app.css;

        if( document.id( "km-csrf" ) ){
            document.id( "km-csrf").set( "value", this.app.data.id );
        }else{
            new Element("input", {
                id : "km-csrf",
                "styles" : { display : "none" }
            }).inject( this.formTableArea )
        }
        this.app.loadExtentResource( function(){
            var contentNode = this.formTableArea;
            var list = new Element("div", { styles : this.css.selectorListNode }).inject( contentNode );

            var protocols = [];

            var pool = kityminder.data.getRegisterProtocol();
            for(var name in pool) {
                if (pool.hasOwnProperty(name) && pool[name].encode) {
                    protocols.push(pool[name]);
                }
            }
            protocols.each(function( p ) {
                new Element("div", { "text": (p.fileDescription + "("+ p.fileExtension +")"),
                    styles : this.css.selectorListItemNode,
                    events : {
                        mouseover : function(el){
                            el.target.setStyles( this.css.selectorListItemNode_over )
                        }.bind(this),
                        mouseleave : function(el){
                            el.target.setStyles( this.css.selectorListItemNode )
                        }.bind(this),
                        click : function(){
                            this.exportFile(p);
                        }.bind(this)
                    }
                }).inject( list );
            }.bind(this));
            if(callback)callback();
        }.bind(this))

    },
    exportFile : function( protocol ){

        this.createProgressBar();

        this.minder = this.app.minder;
        var fileName = this.app.data.name || this.minder.getRoot().getText();
        filename = fileName + protocol.fileExtension;
        var mineType = protocol.mineType || 'text/plain';

        var options = {
            download: true,
            filename: filename
        };

        if( protocol.name == "png" ){
            var converter = new MWF.xApplication.MinderEditor.Converter(this.app, this.minder);
            converter.toPng(null, null, function( img ){
                var link = new Element("a", {"text": filename}).inject(this.progressBarTextNode);
                link.download = fileName;
                link.href = URL.createObjectURL(img);
                //link.href = "data:text/plain," + content;

                var evt = document.createEvent("HTMLEvents");
                evt.initEvent("click", false, false);
                link.dispatchEvent(evt);
                link.click();

                this.progressBarNode.destroy();
                this.progressBarNode = null;
                this.progressBarTextNode = null;
                this.progressBar = null;
                this.progressBarPercent = null;
            }.bind(this));
        }else{
            this.minder.exportData(protocol.name, options).then(function(data) {

                if (protocol.name == 'freemind') {
                    return;
                }


                switch (protocol.dataType) {
                    case 'text':
                        return this.doDownload(this.buildDataUrl(mineType, data), filename, 'text');
                    case 'base64':
                        return this.doDownload(data, filename, 'base64');
                    case 'blob':
                        return null;
                }

                return null;

            }.bind(this));
        }
    },
    doDownload : function (url, filename, type) {
        var Promise = kityminder.Promise;
        var stamp = +new Date() * 1e5 + Math.floor(Math.random() * (1e5 - 1));

        stamp = stamp.toString(36);

        var ret = new Promise(function(resolve, reject) {
            var ticker = 0;
            var MAX_TICK = 30;
            var interval = 1000;

            //function check() {
            //    if (document.cookie.indexOf(stamp + '=1') != -1) return resolve([stamp, ticker]);
            //    if (++ticker > MAX_TICK) {
            //        resolve([stamp, ticker]);
            //    }
            //    setTimeout(check, interval);
            //}
            //
            //setTimeout(check, interval);


            var content = url.split(',')[1];

            this.saveToLocal( content, filename );

            return resolve([stamp, ticker]);

        }.bind(this));


        //var form = new Element("form",{
        //    'action': 'home/download',
        //    'method': 'POST',
        //    'accept-charset': 'utf-8'
        //});

        //var $content = new Element("input",{
        //    name: 'content',
        //    type: 'hidden',
        //    value: decodeURIComponent(content)
        //}).inject( from );
        //
        //var $type = new Element("input",{
        //    name: 'type',
        //    type: 'hidden',
        //    value: type
        //}).inject( from );
        //
        //var $filename = new Element("input",{
        //    name: 'filename',
        //    type: 'hidden',
        //    value: filename
        //}).inject( from );
        //
        //var $csrfToken = new Element("input",{
        //    name: 'csrf_token',
        //    type: 'hidden',
        //    value: $('#km-csrf').val()
        //}).inject( from );
        //
        //
        //if (kity.Browser.ie) {
        //    new Element("input",{
        //        name : "iehack",
        //        value : "1"
        //    }).inject( form );
        //}
        //
        //new Element("input",{
        //    name : "stamp",
        //    value : stamp
        //}).inject( form );
        //
        //form.inject('body');
        //form.submit();
        //form.destroy();

        return ret;
    },
    buildDataUrl: function (mineType, data) {
        return 'data:' + mineType + '; utf-8,' + encodeURIComponent(data);
    },
    saveToLocal: function( data, filename ){
        if (window.hasOwnProperty("ActiveXObject")){
            var win = window.open("", "_blank");
            win.document.write(decodeURIComponent(data));
        }else{
            this.downloadFile(filename, decodeURIComponent(data));
        }


        this.progressBarNode.destroy();
        this.progressBarNode = null;
        this.progressBarTextNode = null;
        this.progressBar = null;
        this.progressBarPercent = null;

        this.close();
    },

    downloadFile: function(fileName, content){
        var link = new Element("a", {"text": this.data.name}).inject(this.progressBarTextNode);
        var blob = new Blob([content]);
        link.download = fileName;
        link.href = URL.createObjectURL(blob);
        //link.href = "data:text/plain," + content;

        var evt = document.createEvent("HTMLEvents");
        evt.initEvent("click", false, false);
        link.dispatchEvent(evt);
        link.click();
    },
    createProgressBar: function(){
        this.progressBarNode = new Element("div", {"styles": this.css.progressBarNode});
        this.progressBarNode.inject(this.container);
        this.progressBarNode.position({
            relativeTo: this.container,
            position: 'center',
            edge: 'center'
        });

        this.progressBarTextNode = new Element("div", {"styles": this.css.progressBarTextNode}).inject(this.progressBarNode);
        this.progressBar = new Element("div", {"styles": this.css.progressBar}).inject(this.progressBarNode);
        this.progressBarPercent = new Element("div", {"styles": this.css.progressBarPercent}).inject(this.progressBar);

        this.close();
    }
});
