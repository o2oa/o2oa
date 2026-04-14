MWF.xApplication.OnlyOffice.Setting = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "attendanceV2",
        "width": "800",
        "height": "620",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "resizeable" : true,
        "closeAction": true,
        "title": "编辑配置文件",
        "closeByClickMaskWhenReading": true,
        "hasBottom" : true,
        "buttonList" : [{ "type":"ok", "text": "保存" },{ "type":"cancel", "text": "取消" }]
    },
    onQueryOpen : function (){

    },
    _postLoad: function(){

        this._createTableContent_();
    },
    _createTableContent: function(){},
    _createTableContent_: function () {

        this.formTableArea.set("html", this.getHtml());
        if(this.data.ipWhiteList!==""){
            this.data = Object.merge(this.data, JSON.parse(this.data.ipWhiteList));
        }

        this.form = new MForm(this.formTableArea, this.data, {
            isEdited: true,
            style : "attendance",
            itemTemplate: {
                docserviceApi: {"text": "前端api地址", "type": "text","style": {"width": "90%"}},
                docserviceConverter: {"text": "转换地址", "type": "text","style": {"width": "90%"}},
                downLoadUrl: {"text": "OnlyOffice访问O2地址", "type": "text","style": {"width": "90%"}},
                secret: {"text": "OnlyOffice密钥", "type": "text","style": {"width": "90%"}},
                docserviceViewedDocs: {"text": "查看类型", "type": "text","style": {"width": "90%"}},
                docserviceEditedDocs: {"text": "编辑类型", "type": "text","style": {"width": "90%"}},


                address: {"text": "地址", "type": "text","style": {"width": "90%"}},
                info: {"text": "信息", "type": "text","style": {"width": "90%"}},
                logo: {"text": "Logo", "type": "text","style": {"width": "90%"}},
                logoDark: {"text": "深色Logo", "type": "text","style": {"width": "90%"}},
                mail: {"text": "邮箱", "type": "text","style": {"width": "90%"}},
                name: {"text": "名称", "type": "text","style": {"width": "90%"}},
                phone: {"text": "联系方式", "type": "text","style": {"width": "90%"}},
                www: {"text": "网址", "type": "text","style": {"width": "90%"}},
            },
            onPostLoad:function(){

            }.bind(this)
        },this.app,this.css);
        this.form.load();

    },

    getHtml : function(){
        return  "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable'>" +


            "<tr ><td styles='formTableTitleRight' lable='docserviceConverter' style='width: 150px'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='docserviceConverter'></div>" +
            "   </td>" +
            "</tr>" +

            "<tr ><td styles='formTableTitleRight' lable='docserviceApi'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='docserviceApi'></div>" +
            "   </td>" +
            "</tr>" +

            "<tr ><td styles='formTableTitleRight' lable='docserviceViewedDocs'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='docserviceViewedDocs'></div>" +
            "   </td>" +
            "</tr>" +

            "<tr ><td styles='formTableTitleRight' lable='docserviceEditedDocs'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='docserviceEditedDocs'></div>" +
            "   </td>" +
            "</tr>" +

            "<tr ><td styles='formTableTitleRight' lable='secret'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='secret'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='downLoadUrl'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='downLoadUrl'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td colspan='3' style='text-align: center; color: rgb(51, 51, 51); font-size: 14px; padding: 0px 20px 0px 0px; height: 35px; line-height: 35px;'>---开发板白标自定义---</td>" +
            "</tr>" +


            "<tr ><td styles='formTableTitleRight' lable='address'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='address'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='info'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='info'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='logo'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='logo'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='logoDark'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='logoDark'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='mail'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='mail'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='name'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='name'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='phone'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='phone'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='www'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='www'></div>" +
            "   </td>" +
            "</tr>" +
            "</table>";
    },
    _ok: function (data, callback) {

        data.ipWhiteList = JSON.stringify(data);
        this.app.action.OnlyofficeConfigAction.saveConfig(data,function (){
            this.app.notice("创建成功","success");
            this.close();
        }.bind(this),null,false);
    },
});
