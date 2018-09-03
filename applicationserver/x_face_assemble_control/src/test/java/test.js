var userId = "";
var userIdCN = "";
var appCode = "27";
var userId1 = "empty";
var userLocation = "";
var replaceLocation = "";
/* 信息配置 */
var infoConfig = {
    "tabLeft3_1" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/sunshineWork?opendocument",
        "obj" : "tabLeft3_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/sunshineWork.nsf"
    },
    "tabLeft3_2" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/devide?opendocument",
        "obj" : "tabLeft3_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/lib/devide.nsf"
    },
    "tabLeft3_3" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/lawCommunication?opendocument",
        "obj" : "tabLeft3_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/lawCommunication.nsf"
    },
    "tabLeft3_4" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/gwAssistants?opendocument",
        "obj" : "tabLeft3_content",
        "mode" : "1",
        "moreurl" : "http://hnpub.bf.ctc.com/public/gwAssistants.nsf"
    },
    "tabLeft4_1" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/dangjianyuandi?opendocument",
        "obj" : "tabLeft4_content",
        "mode" : "6",
        "moreurl" : "http://hnpub.bf.ctc.com/lib/party/partyNavi.nsf/source/index"
    },
    "tabLeft4_2" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/jijianjiancha?opendocument",
        "obj" : "tabLeft4_content",
        "mode" : "4",
        "moreurl" : "http://hnpub.bf.ctc.com/supervision/navi.nsf/source/supervisionStyle?openDocument"
    },
    "tabLeft4_3" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/careEmployee?opendocument",
        "obj" : "tabLeft4_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/publisher/PBIndex.nsf/default?readForm&category=5E9F5711D10CBFA948257F24003A56DE"
    },
    "tabLeft4_4" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/youthwork_lib?opendocument",
        "obj" : "tabLeft4_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/youthwork.nsf"
    },
    "tabRight1_1" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/prime?opendocument",
        "obj" : "tabRight1_content",
        "mode" : "2",
        "moreurl" : "http://hncon.bf.ctc.com/publisher/PBIndex.nsf/default?readForm"
    },
    "tabRight1_2" : {
        "dataurl" : "http://uip.hq.ctc.com/uip/jsonInterface/article_articleJsonMoreChanel.htm?column=4435868&count=17&appCode=30&account=userId&competenceFlag=0&callback=?",
        "obj" : "tabRight1_content",
        "mode" : "3",
        "moreurl" : ""
    },
    "tabRight1_3" : {
        "dataurl" : "http://uip.hq.ctc.com/uip/jsonInterface/article_articleJsonMoreChanel.htm?column=4435870&count=17&appCode=30&account=userId&competenceFlag=0&callback=?",
        "obj" : "tabRight1_content",
        "mode" : "3",
        "moreurl" : ""
    },
    "tabRight1_4" : {
        "dataurl" : "http://uip.hq.ctc.com/uip/jsonInterface/article_articleJsonMoreChanel.htm?column=4557322&count=17&appCode=30&account=userId&competenceFlag=0&callback=?",
        "obj" : "tabRight1_content",
        "mode" : "3",
        "moreurl" : ""
    },
    "tabRight1_5" : {
        "dataurl" : "http://hnunimail.bf.ctc.com/land/umail.nsf/(GetUserMailList)?openagent",
        "obj" : "tabRight1_content",
        "mode" : "5",
        "moreurl" : "http://hnunimail.bf.ctc.com/land/umail.nsf/(GetSsoUrl)?openagent"
    },
    "tabRight2_1" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/bulletin?opendocument",
        "obj" : "tabRight2_content",
        "mode" : "4",
        "moreurl" : "http://hncon.bf.ctc.com/content/bulletin.nsf"
    },
    "tabRight2_2" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/companyNews?opendocument",
        "obj" : "tabRight2_content",
        "mode" : "4",
        "moreurl" : "http://hncon.bf.ctc.com/content/lib/companyNews.nsf"
    },
    "tabRight2_3" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/localBulletin_userLocation?opendocument",
        "obj" : "tabRight2_content",
        "mode" : "4",
        "moreurl" : "http://hncon.bf.ctc.com/content/userLocation/localBulletin.nsf"
    },
    "tabRight2_4" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/companyNews_userLocation?opendocument",
        "obj" : "tabRight2_content",
        "mode" : "4",
        "moreurl" : "http://hncon.bf.ctc.com/content/userLocation/companyNews.nsf"
    },
    "tabRight3_1" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/safetyWork?opendocument",
        "obj" : "tabRight3_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/safetyWork.nsf"
    },
    "tabRight3_2" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/informationSecurity?opendocument",
        "obj" : "tabRight3_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/informationSecurity.nsf"
    },
    "tabRight3_3" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/internetOperation?opendocument",
        "obj" : "tabRight3_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/internetOperation.nsf"
    },
    "tabRight3_4" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/doublePropaganda?opendocument",
        "obj" : "tabRight3_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/doublePropaganda.nsf"
    },
    "tabRight4_1" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/honourList?opendocument",
        "obj" : "tabRight4_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/honourList.nsf"
    },
    "tabRight4_2" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/newsInformation?opendocument",
        "obj" : "tabRight4_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/newsInformation.nsf"
    },
    "tabRight4_3" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/dushuluntan?opendocument",
        "obj" : "tabRight4_content",
        "mode" : "4",
        "moreurl" : "http://hnpub.bf.ctc.com/lib/readingBook/readingBookNavi.nsf/config/index_v6?open"
    },
    "tabRight4_4" : {
        "dataurl" : "/home/JsonEngineUni.nsf/Json/employeeRestaurant_lib?opendocument",
        "obj" : "tabRight4_content",
        "mode" : "1",
        "moreurl" : "http://hncon.bf.ctc.com/content/lib/employeeRestaurant.nsf"
    },
    "tabMyTodo" : {
        "dataurl" : "",
        "obj" : "tabMyTodo_content",
        "mode" : "0",
        "moreurl" : "http://hnoa.bf.ctc.com/home/NaviUni.nsf/taskCenterMss_wrdp?OpenPage"
    },
    "tabTodoCategory" : {
        "dataurl" : "",
        "obj" : "tabTodoCategory_content",
        "mode" : "0",
        "moreurl" : ""
    },
    "tabDone" : {
        "dataurl" : "http://172.22.136.71/webrdp/_saas/_app/hnchtc.app/service/jsonService.db/JSON_DB2_TASK.jssp?open&fun=taskDone&user=userId",
        "obj" : "tabDone_content",
        "mode" : "0",
        "moreurl" : "http://hnoa.bf.ctc.com/home/NaviUni.nsf/taskCenterMss_wrdp?OpenPage&type=1"
    },
    "tabReaded" : {
        "dataurl" : "http://172.22.136.71/webrdp/_saas/_app/hnchtc.app/service/jsonService.db/JSON_DB2_TASK.jssp?open&fun=readDone&user=userId",
        "obj" : "tabReaded_content",
        "mode" : "0",
        "moreurl" : "http://hnoa.bf.ctc.com/home/NaviUni.nsf/taskCenterMss_wrdp?OpenPage&type=3"
    },
    "tabMail_oa" : {
        "dataurl" : "",
        "obj" : "tabMail_oacontent",
        "mode" : "0",
        "moreurl" : "/land/redirectLink.nsf/link/mailbox?openDocument"
    },
    "tabMail_jt" : {
        "dataurl" : "",
        "obj" : "tabMail_jtcontent",
        "mode" : "0",
        "moreurl" : "http://hnunimail.bf.ctc.com/land/umail.nsf/(GetSSoUrl)?openagent"
    }
};
var locationConfig = {
    "lib" : "省公司",
    "lib_ay" : "安阳",
    "lib_hb" : "鹤壁",
    "lib_jy" : "济源",
    "lib_jz" : "焦作",
    "lib_kf" : "开封",
    "lib_ly" : "洛阳",
    "lib_lh" : "漯河",
    "lib_ny" : "南阳",
    "lib_pds" : "平顶山",
    "lib_py" : "濮阳",
    "lib_smx" : "三门峡",
    "lib_sq" : "商丘",
    "lib_xx" : "新乡",
    "lib_xy" : "信阳",
    "lib_xc" : "许昌",
    "lib_zz" : "郑州",
    "lib_zk" : "周口",
    "lib_zmd" : "驻马店",
    "info" : "工会工作"
}

$(document)
        .ready(
                function () {
                    // mss单点登录;
                    loginPortal();
                    loginHr();
                    // 用户id;
                    $.ajax({
                        type : "get",
                        url : "/land/sso.nsf/getUserId?openpage",
                        cache : false,
                        async : false,
                        dataType : "json",
                        success : function (data) {
                            userId = data.userId;
                            userIdCN = data.userIdCN;
                        }
                    });
                    $.ajax({
                        type : "get",
                        url : "/land/sso.nsf/getUserIdCN?openpage",
                        cache : false,
                        async : false,
                        dataType : "json",
                        success : function (data) {
                            userIdCN = data.userId;
                        }
                    });

                    // 初始化频道选择
                    userLocation = jQuery("#userLoaction").text();
                    replaceLocation = userLocation;
                    intiSelectLocation();

                    // 图片新闻
                    COMMON.JSON
                            .get(
                                    "/home/JsonEngineUni.nsf/Json/picNews?opendocument",
                                    function (data) {
                                        var menu;
                                        var html = "";
                                        $
                                                .each(
                                                        data.items,
                                                        function (key, val) {
                                                            if (val) {
                                                                menu = '<a href="'
                                                                        + val.link
                                                                        + '" target="_blank" id="pica'
                                                                        + (key + 1)
                                                                        + '"><IMG width="360px" height="260px" src="'
                                                                        + val.img
                                                                        + '" alt="" title="'
                                                                        + val.title
                                                                        + '"></IMG></a>';
                                                                html += menu;
                                                            }
                                                        });
                                        if (data.items && data.items.length > 0) {
                                            $('#slider').html(html)
                                                    .nivoSlider();
                                        }
                                    });
                    gotoLeft();
                    gotoRight();

                    // 加载待办
                    setTimeout("initialTask()", 1000);

                    // 样式 切换
                    jQuery('.main_1_le_ti ul li').hover(function () {
                        jQuery(this).find("a").addClass("current");
                        jQuery(this).siblings().find("a").attr("class", "");
                        changeDispalyInfo(this);
                    }, function () {

                    });

                    // 显示每个tab的第一分类
                    showCurrentCompanyInfo();

                    // 公务邮箱
                    // transXMLWithXSL('/land/RSSEngine.nsf/RSSFeed/indexMail2014?open',
                    // '../maillisthomepage.xsl', 'tabMail_oacontent');
                    // 集团统一邮箱
                    // transXMLWithXSL('http://hnunimail.bf.ctc.com/land/umail.nsf/(GetUserMailList)?openagent',
                    // '../jttymailhomepage.xsl',
                    // 'tabMail_jtcontent',"",getJTmailCount);
                    getMailCount();

                    // 首页头部隐藏
                    /*
                     * var htime = setTimeout("headhide()",8000);
                     * 
                     * jQuery("#headarea").hover(function(){
                     * clearTimeout(htime); },function(){ htime =
                     * setTimeout("headhide()",8000); });
                     * 
                     * jQuery("#headline").hover(function(){ headshow(); htime =
                     * setTimeout("headhide()",8000); },function(){ });
                     */

                    // 显示图片链接
                    var startpost = 0;
                    var maxwidth = 0;
                    var linkpicswidth = jQuery(".imgLink").width();
                    var usedwidth = 0;
                    var imgs = jQuery(".imgLink a img");
                    imgs.each(function (index, img) {
                        var curimg = jQuery(img);
                        usedwidth = usedwidth + curimg.width() + 14; // 这里7是a的padding
                        if (usedwidth < linkpicswidth) {
                            curimg.parent("a").first().show();
                        } else {
                            curimg.parent("a").first().hide();
                        }
                        maxwidth = usedwidth;
                    });

                    // 图片链接右箭头的点击函数
                    jQuery("#imgrightarrow")
                            .click(
                                    function () {
                                        if (startpost < (maxwidth - linkpicswidth)) {
                                            startpost += linkpicswidth;
                                        }
                                        var usedwidth = 0;
                                        imgs.parent("a").show();
                                        imgs
                                                .each(function (index, img) {
                                                    var curimg = jQuery(img);
                                                    usedwidth = usedwidth
                                                            + curimg.width()
                                                            + 14; // 这里7是a的padding
                                                    if (startpost < usedwidth
                                                            && usedwidth < (startpost + linkpicswidth)) {
                                                        curimg.parent("a")
                                                                .first().show();
                                                    } else {
                                                        curimg.parent("a")
                                                                .first().hide();
                                                    }
                                                });
                                    });
                    // 图片链接左箭头的点击函数
                    jQuery("#imgleftarrow")
                            .click(
                                    function () {
                                        if (startpost > 0) {
                                            startpost -= linkpicswidth;
                                        }
                                        var usedwidth = 0;
                                        imgs.parent("a").show();
                                        imgs
                                                .each(function (index, img) {
                                                    var curimg = jQuery(img);
                                                    usedwidth = usedwidth
                                                            + curimg.width()
                                                            + 14; // 这里7是a的padding
                                                    if (startpost < usedwidth
                                                            && usedwidth < (startpost + linkpicswidth)) {
                                                        curimg.parent("a")
                                                                .first().show();
                                                    } else {
                                                        curimg.parent("a")
                                                                .first().hide();
                                                    }
                                                });
                                    });

                });
function intiSelectLocation() {
    var selectHtml = "";
    jQuery.each(locationConfig, function (key, val) {
        selectHtml += '<span location="' + key + '">' + val + '</span>';
    });
    jQuery("#selectOption").html(selectHtml);
    jQuery("#selectLocation").attr("location", replaceLocation);
    jQuery("#selectLocation option").text(locationConfig[replaceLocation]);

    var selecttime;
    jQuery("#selectLocation").hover(function () {
        clearTimeout(selecttime);
        jQuery("#selectOption").show();
    }, function () {
        selecttime = setTimeout("jQuery('#selectOption').hide()", 1000);
    });
    jQuery("#selectOption").hover(function () {
        clearTimeout(selecttime);
    }, function () {
        jQuery("#selectOption").hide();
    });

    jQuery("#selectOption")
            .find("span")
            .each(
                    function () {
                        var currspan = jQuery(this);
                        currspan.css("cursor", "pointer");
                        currspan
                                .click(function () {
                                    var key = jQuery(this).attr("location");
                                    var name = jQuery(this).text();

                                    jQuery("#selectLocation").attr("location",
                                            key);
                                    jQuery("#selectLocation").find("option")
                                            .text(name);
                                    jQuery("#selectOption").hide();
                                    // 跳转到对应页面
                                    var url = "";
                                    if (key == "info") {
                                        url = "http://hncon.bf.ctc.com/publisher/PBIndex.nsf/default?readForm&category=5E9F5711D10CBFA948257F24003A56DE";
                                        window.open(url, "_self");
                                    } else {
                                        replaceLocation = key;
                                        showCurrentCompanyInfo();
                                    }
                                });
                    });
}

function showCurrentCompanyInfo() {
    if (replaceLocation == "lib") {
        jQuery("#tabRight2_3").hide();
        jQuery("#tabRight2_4").hide();
        jQuery("#tabLeft4_4").show();
    } else {
        jQuery("#tabRight2_3").show();
        jQuery("#tabRight2_4").show();
        jQuery("#tabLeft4_4").show();
    }
    jQuery('.main_1_le_ti ul').each(function () {
        var firstli = jQuery(this).find("li").first();
        firstli.find("a").addClass("current");
        firstli.siblings().find("a").attr("class", "");
        changeDispalyInfo(jQuery(firstli));
    })
}

function headshow() {
    jQuery("#headarea").show("slow");
    jQuery("#headline").hide();
    jQuery(".hanging_wrap").css("top", "213px");
}
function headhide() {
    jQuery("#headarea").hide("slow");
    jQuery("#headline").show();
    jQuery(".hanging_wrap").css("top", "106px");
}

// **************信息展现开始----------
function changeDispalyInfo(tab) {
    var id = jQuery(tab).attr("id");
    var data = infoConfig[id];
    if (data) {
        showInfo(data, tab);
    }
}
function showInfo(data, tab) {
    // 添加模块链接
    // alert(data.moreurl)
    if (data.moreurl != ""
            && (jQuery(tab).find("a").attr("href") == "#" || data.moreurl
                    .indexOf("userLocation") > -1)) {
        var moreurl = data.moreurl.replace("userLocation", replaceLocation);
        jQuery(tab).find("a").attr("href", moreurl);
        jQuery(tab).find("a").attr("target", "_blank");
    }
    // 展现方式
    var mode = data.mode;
    if (mode == "0") {
        showInfoAsMode0(data);
        return true;
    }
    if (mode == "1") {
        showInfoAsMode1(data);
        return true;
    }
    if (mode == "2") {
        showInfoAsMode2(data);
        return true;
    }
    if (mode == "3") {
        showInfoAsMode3(data, tab);
        return true;
    }
    if (mode == "4") {
        showInfoAsMode4(data);
        return true;
    }
    if (mode == "5") {
        showInfoAsMode5(data);
        return true;
    }
    if (mode == "6") {
        showInfoAsMode6(data);
        return true;
    }
}
// 待办tab切换
function showInfoAsMode0(d) {
    var jsonurl = d.dataurl;
    var obj = jQuery("#" + d.obj);
    obj.show();
    obj.siblings().hide();
    if (d.obj == "tabTodoCategory_content") {
        setCategoryCount();
    }
    if (jsonurl != "") {
        var jsonurl = jsonurl.replace("userId", userId);

        COMMON.JSON.get(jsonurl, function (data) {
            var items = [];
            if (data.items) {
                jsondata = data.items;
            } else {
                jsondata = data;
            }
            $.each(jsondata, function (key, val) {
                if (val) {
                    items.push('<li><span class="time">'
                            + getDateCustom(val.pubDate) + '</span><a  href="'
                            + val.link + ' " target="_blank">'
                            + getLeftSubStr(decodeURI(val.title), 18)
                            + '</a></li>');
                }
            });
            obj.html('<ul class="newsList" >' + items.join('') + '</ul>');
        });
    }
}

// 图文合并展现
function showInfoAsMode1(d) {
    // var jsonurl = d.dataurl;
    // 根据用户所在地市切换数据源
    var jsonurl = d.dataurl.replace("userLocation", replaceLocation);

    var obj = jQuery("#" + d.obj);
    obj.html("");
    if (jsonurl != "") {
        COMMON.JSON.get(jsonurl, function (data) {
            var menu;
            var html = "";
            var items = data.items;
            if (items.length > 0) {
                // 排序
                if (d.dataurl.indexOf("userLocation") > -1) {
                    items.sort(getCompare());
                }

                var firstitem = items[0];
                var otheritems = [];
                for (i = 1; i < 6 && i <= items.length; i++) {
                    otheritems.push(items[i]);
                }

                if (firstitem.img == "") {
                    html += '<div class="main_1_le_tx2">' + '<h2><a href="'
                            + firstitem.link + '" target="_blank">'
                            + getLeftSubStr(firstitem.title, 24) + '</a></h2>'
                            + '<p>' + getLeftSubStr(firstitem.brief, 98)
                            + '<a href="' + firstitem.link
                            + '" target="_blank">[详细]</a></p></div>';
                } else {
                    html += '<div class="main_1_cen_4txt">'
                            + '<samp><img src="' + firstitem.img
                            + '" width="145" height="103" /></samp>';
                    html += '<span><h2><a href="' + firstitem.link
                            + '" target="_blank">'
                            + getLeftSubStr(firstitem.title, 11) + '</a></h2>'
                            + '<p>' + getLeftSubStr(firstitem.brief, 46)
                            + '<a href="' + firstitem.link
                            + '" target="_blank">[详细]</a></p></span></div>';
                }
                html += '<div class="main_1_cen_3txt"><ul>';
                $.each(otheritems, function (key, val) {
                    if (val) {
                        menu = '<li><span><a href="' + val.link
                                + '" target="_blank">'
                                + getLeftSubStr(val.title, 16) + '</a>'
                                + getNewsTag(val.pubDate) + '</span><samp>'
                                + getDateCustom(val.pubDate) + '</samp></li>';
                        html += menu;
                    }
                });
                html += '</ul></div>';
                obj.html(html);
            }
        });
    }
}
// 精华展现
function showInfoAsMode2(d) {
    // var jsonurl = d.dataurl;
    // 根据用户所在地市切换数据源
    var jsonurl = d.dataurl.replace("userLocation", replaceLocation);
    var obj = jQuery("#" + d.obj);
    obj.html("");
    if (jsonurl != "") {
        COMMON.JSON.get(jsonurl, function (data) {
            var menu;
            var html = "";
            var items = data.items;
            if (items.length > 0) {
                if (d.dataurl.indexOf("userLocation") > -1) {
                    items.sort(getCompare());
                }

                var otheritems1 = [];
                var otheritems2 = [];
                for (i = 0; i < 9 && i <= items.length; i++) {
                    otheritems1.push(items[i]);
                }
                for (i = 9; i < 18 && i <= items.length; i++) {
                    otheritems2.push(items[i]);
                }

                html += '<ul>';
                $.each(otheritems1, function (key, val) {
                    if (val) {
                        menu = '<li><a href="' + val.link
                                + '" target="_blank">'
                                + getLeftSubStr(val.title, 18) + '</a>'
                                + getNewsTag(val.pubDate) + '</li>';
                        html += menu;
                    }
                });
                html += '</ul><ul>';
                $.each(otheritems2, function (key, val) {
                    if (val) {
                        menu = '<li><a href="' + val.link
                                + '" target="_blank">'
                                + getLeftSubStr(val.title, 18) + '</a>'
                                + getNewsTag(val.pubDate) + '</li>';
                        html += menu;
                    }
                });
                html += '</ul>';
                obj.html(html);
            }
        })
    }
}
// 集团信息展现
function showInfoAsMode3(d, tab) {
    var jsonurl = d.dataurl.replace("userId", userId);
    var obj = jQuery("#" + d.obj);
    var moreobj = jQuery(tab).find("a");
    obj.html("");
    if (jsonurl != "") {
        jQuery.ajax({
            type : "post",
            async : true,
            url : jsonurl,
            dataType : "jsonp",
            jsonp : "callback",
            success : function (json) {
                var menu;
                var html = "";
                var data = eval(json);
                var ssoUrl = data.ssoUrl;
                var moreUrl = ssoUrl + data.data[0].moreUrl;
                if (moreobj.attr("href") == "#") {
                    moreobj.attr("href", moreUrl);
                    moreobj.attr("target", "_blank");
                }
                var items = data.data[0].data;
                if (items.length > 0) {
                    var otheritems1 = [];
                    for (i = 0; i < 9 && i <= items.length; i++) {
                        otheritems1.push(items[i]);
                    }

                    html += '<ul style="width:100%">';
                    $.each(otheritems1, function (key, val) {
                        if (val) {
                            menu = '<li style="width:100%"><a href="' + ssoUrl
                                    + val.url + '" target="_blank">'
                                    + getLeftSubStr(val.title, 38) + '</a>'
                                    + getNewsTag(val.createTime) + '</li>';
                            html += menu;
                        }
                    });
                    html += '</ul>';
                    obj.html(html);

                }

            },
            error : function () {
                // alert('fail');
            }
        });
    }
}

// 列表展现
function showInfoAsMode4(d) {
    // 根据用户所在地市切换数据源
    var jsonurl = d.dataurl.replace("userLocation", replaceLocation);
    var obj = jQuery("#" + d.obj);
    obj.html("");
    if (jsonurl != "") {
        COMMON.JSON.get(jsonurl, function (data) {
            var menu;
            var html = "";
            var items = data.items;
            if (items.length > 0) {
                // 排序
                // if(d.dataurl.indexOf("userLocation")>-1){
                items.sort(getCompare());
                // }

                html += '<div class="main_1_cen_2txt"><ul>';
                $.each(items, function (key, val) {
                    if (val) {
                        menu = '<li><span><a href="' + val.link
                                + '" target="_blank">'
                                + getLeftSubStr(val.title, 16) + '</a>'
                                + getNewsTag(val.pubDate) + '</span><samp>'
                                + getDateCustom(val.pubDate) + '</samp></li>';
                        html += menu;
                    }
                });
                html += '</ul></div>';
                obj.html(html);
            }
        });
    }
}
// 集团统一邮箱
function showInfoAsMode5(d) {
    transXMLWithXSL(d.dataurl, '../jttymailhomepage.xsl', d.obj, "",
            setJTmailCount);
}

function setJTmailCount() {
    var obj = this;
    var countobj = jQuery("#jtmailCount");
    if (obj && countobj) {
        var count = jQuery(obj).find("rss>channel>count").text();
        if (count != "") {
            countobj.text(count);
        }
    }
}

// 党建展现
function showInfoAsMode6(d) {
    // var jsonurl = d.dataurl;
    // 根据用户所在地市切换数据源
    var jsonurl = d.dataurl.replace("userLocation", replaceLocation);

    var obj = jQuery("#" + d.obj);
    obj.html("");
    if (jsonurl != "") {
        COMMON.JSON
                .get(
                        jsonurl,
                        function (data) {
                            var menu;
                            var html = "";
                            var items = data.items;
                            if (items.length > 0) {
                                // 排序
                                if (d.dataurl.indexOf("userLocation") > -1) {
                                    items.sort(getCompare());
                                }
                                var otheritems1 = [];
                                var otheritems2 = [];
                                for (i = 0; i < 4 && i <= items.length; i++) {
                                    otheritems1.push(items[i]);
                                }
                                for (i = 4; i < 9 && i <= items.length; i++) {
                                    otheritems2.push(items[i]);
                                }

                                html += '<div class="main_1_cen_5txt"><div class="main_1_cen_6txt">'
                                        + '<samp><img src="/homepage2015/v2/images/djyd.jpg" width="145" height="103" /></samp></div>';

                                html += '<div class="main_1_cen_7txt"><ul>';

                                $.each(otheritems1, function (key, val) {
                                    if (val) {
                                        menu = '<li><span><a href="' + val.link
                                                + '" target="_blank">'
                                                + getLeftSubStr(val.title, 12)
                                                + '</a></span></li>';
                                        html += menu;
                                    }
                                });

                                html += '</ul></div></div>';

                                html += '<div class="main_1_cen_3txt"><ul>';
                                $.each(otheritems2, function (key, val) {
                                    if (val) {
                                        menu = '<li><span><a href="' + val.link
                                                + '" target="_blank">'
                                                + getLeftSubStr(val.title, 16)
                                                + '</a>'
                                                + getNewsTag(val.pubDate)
                                                + '</span><samp>'
                                                + getDateCustom(val.pubDate)
                                                + '</samp></li>';
                                        html += menu;
                                    }
                                });
                                html += '</ul></div>';
                                obj.html(html);
                            }
                        });
    }
}

function getDateCustom(pubDate) {
    if (pubDate) {
        if (pubDate.replace(/(^\s*)|(\s*$)/g, "") == "")
            return ("");
        var date = new Date(pubDate.replace(/-/gi, "/"));
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        if (month < 10)
            month = "0" + month;
        if (day < 10)
            day = "0" + day;

        return year + "-" + month + "-" + day;
    }
}
// ****************信息展现结束----------

// 集团统一邮件数量
function getMailCount() {
    var url = "http://hnunimail.bf.ctc.com/land/umail.nsf/(GetUserMailCount)?openagent";
    jQuery.ajax({
        type : "get",
        async : true,
        url : url,
        dataType : "xml",
        success : function (data) {
            var obj = data;
            var countobj = jQuery("#jtmailCount");
            if (obj && countobj) {
                var count = jQuery(obj).find("rss>channel>item>title").text();
                // alert(count)
                if (count != "") {
                    countobj.text(count);
                    var countobj2 = jQuery("#menu_mailcount");
                    if (countobj2) {
                        countobj2.text(count);
                    }
                }
            }
        }
    })
    // jQuery("#menu_mailcount").text(jQuery("#cat_oldmailcount").text());
}

// 待办
function initialTask() {
    // 加载OA待办
    initialOATask();
    // mss待办,mss 待办延时再加载
    setTimeout(function () {
        initialMSSTask();
    }, 2000);
    setTimeout(function () {
        initialJT();
    }, 3000);
    // itsm待办
    // initialITSM();
    // 其他待办
    initialOther();
    // 待办
    window.setInterval(function () {
        waitFlashTask();
    }, 2000);
}

function initialOATask() {
    transXMLListWithXSLAndGetCount(
            '/lib/RSSEngine.nsf/RSSFeed/hnportal_oa_wrdp?open',
            '../indexTodoXsl.xsl', 'taskOA', 'innerHTML', 'countOA',
            'innertext', "/rss/channel/item", "/rss/channel", "");
}

function initialMSSTask() {

    jQuery("#taskMSS").transform({
        xmls : '/lib/RSSEngine.nsf/RSSFeed/indexTodoMss881?open',
        xsl : '../indexTodoXsl.xsl',
        xmlpath : '/channel/item',
        complete : function () {
            var num = 0
            num = this.xmlobj.selectNodes("//item").length;
            jQuery("#countMSS").text(num);
            totalTaskCount();
        }
    });

    jQuery("#taskMSS2").transform({
        xmls : '/lib/RSSEngine.nsf/RSSFeed/indexTodoMss882?open',
        xsl : '../indexTodoXsl.xsl',
        xmlpath : '/channel/item',
        complete : function () {
            var num = 0
            num = this.xmlobj.selectNodes("//item").length;
            jQuery("#countMSS2").text(num);
            if (num > 0) {
                $("#countMSS2").parent().parent(".daiban_item").show();
            }
        }
    });
}

function initialJT() {
    jQuery("#taskJT").transform({
        xmls : '/lib/RSSEngine.nsf/RSSFeed/indexTodoJT88?open',
        xsl : '../indexTodoXsl.xsl',
        xmlpath : '/channel/item',
        complete : function () {
            var num = 0
            num = this.xmlobj.selectNodes("//item").length;
            jQuery("#countJT").text(num);
            totalTaskCount();
        }
    });
}

/*
 * function initialITSM() {
 * transXMLListWithXSLAndGetCount('/lib/RSSEngine.nsf/RSSFeed/indexTodoITSMnew?open',
 * '../indexTodoITSMXslnew.xsl', 'taskITSM', 'innerHTML', 'countITSM',
 * 'innertext', "/rss/channel/item", "/rss/channel", ""); }
 * 
 * 
 * function initialOther() {
 * transXMLListWithXSLAndGetCount('/lib/RSSEngine.nsf/RSSFeed/indexTodoITSMnew?open',
 * '../indexTodoITSMXslnew.xsl', 'taskOther', 'innerHTML', 'countOther',
 * 'innertext', "/rss/channel/item", "/rss/channel", ""); }
 */
function initialOther() {
    jQuery
            .ajax({
                url : "http://172.22.94.22:7001/hnGetEventQController/getEventQCount.do?UserName="
                        + userId + "",
                type : "post",
                dataType : "jsonp",
                jsonpCallback : "callback",
                success : function (data) {
                    jQuery("#countOther").text(data.Count);
                    // 获得列表
                    if (jQuery("#countOther").text() != "0") {

                        jQuery
                                .ajax({
                                    url : "http://172.22.94.22:7001/hnGetEventQController/getEventQList.do?UserName="
                                            + userId + "&PageNum=1",
                                    type : "post",
                                    dataType : "jsonp",
                                    jsonpCallback : "callback",
                                    success : function (items) {
                                        var html = "";
                                        jQuery
                                                .each(
                                                        items,
                                                        function (n, value) {

                                                            var _pubdate = value.Pubdate;
                                                            if (_pubdate
                                                                    && _pubdate.length > 10) {
                                                                _pubdate = _pubdate
                                                                        .substring(
                                                                                0,
                                                                                10)
                                                                        + "";
                                                            } else {
                                                                _pubdate = _pubdate;
                                                            }
                                                            var _title = value.Title;
                                                            if (_title
                                                                    && _title.length > 15) {
                                                                _title = _title
                                                                        .substring(
                                                                                0,
                                                                                15)
                                                                        + "...";
                                                            } else {
                                                                _title = _title;
                                                            }
                                                            html += "<li><span class=\"time\">["
                                                                    + _pubdate
                                                                    + "]</span><a onclick=\"openITSMTask(this,'"
                                                                    + value.Link
                                                                    + "')\" href=\"javascript:;\"> "
                                                                    + _title
                                                                    + "</a></li>";
                                                        });
                                        if (html != '') {
                                            jQuery("#taskOther").html(html);
                                            jQuery("#taskOther").show();
                                        }

                                    }
                                });
                    }
                }
            });
}
function getMssCount() {

    var url = "http://flowassist.mss.ctc.com/ApprovePend2/pendingNoSSO.do?action=getPendingCountBySSO&userID="
            + userId + "&now=" + new Date().getTime();
    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    xmlhttp.open("GET", url, true);
    xmlhttp.send(null);
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4) {
            if (xmlhttp.status == 200) {
                jsonData = eval("(" + xmlhttp.responseText + ")");
                mssCount = jsonData[0].value;

                var val = $("#countMSS").text()
                if (isNaN(val)) {
                    $("#countMSS").text(jsonData[0].value);
                    totalTaskCount();
                } else {
                    $("#countMSS").text(
                            parseInt(jsonData[0].value) + parseInt(val));
                    totalTaskCount();
                }

            }
        }
    };
}

function setCategoryCount() {
    jQuery("#cat_oacount").text("" + jQuery("#countOA").text());
    jQuery("#cat_msscount").text("" + jQuery("#countMSS").text());
    jQuery("#cat_jtcount").text("" + jQuery("#countJT").text());
    jQuery("#cat_othercount").text("" + jQuery("#countOther").text());
}

// 待办结束

// 登录Portal
// mss相关------------------begin
var intervalPortalWin = setInterval("testPortalWindow()", 500);
var portalWin;
function loginPortal() {
    jQuery('#loginPortal_iframe').attr('src',
            "/land/sso.nsf/sso?openagent&redirectTo=blank");
}

function logoutOaAndSap() {
    document.getElementById("loginPortal_iframe").contentWindow.location = "/land/sso.nsf/logoutSap";
    setTimeout(
            "window.location.replace('/land/redirectLink.nsf/link/logout?open')",
            1000);
}

function testPortalWindow() {
    try {
        if (typeof (portalWin) == "object") {
            if (portalWin.closed) {
                // window.status = "刷新";
                portalWin = "";
                loginPortal();
                // setTimeout("showMssCount()",1000);
            }
        }
    } catch (e) {
    }
}

function openPortal() {// 打portal待办
    if (typeof (portalWin) == "object") {
        portalWin.focus();
    } else {
        var w = screen.width;
        var h = screen.height;
        // userId = "hnadmin";
        parm = "width="
                + w
                + ",height="
                + h
                + ",toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes";
        url = "/land/sso.nsf/sso?openagent&appid=bfep&userId=" + userId
                + "&redirectTo=";
        portalWin = window
                .open(
                        url,
                        "portalWin",
                        "width="
                                + w
                                + ",height="
                                + h
                                + ",toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes");
    }
}

// mss相关------------------end

// 待办相关

function transXMLListWithXSLAndGetCount(xmlURL, xslURL, obj, s, tcObj, tcS,
        itemNode, channelNode, countNode) {
    /*
     * 解析xml数组，可以指定取得的item数目，并获取所有item数量 xmlURL： 包含多个xml链接的xml文件地址,该xml文件形式如下：
     * <rss> <channel> <item> <title> <![CDATA[ domini待办]]> </title> <link>
     * <![CDATA[/land/RSSEngine.nsf/RSSFeed/indexTodoContact?open]]> </link>
     * <title> <![CDATA[ 统一待办]]> </title> <link>
     * <![CDATA[http://172.20.11.17:9080/UnitityToDo/listData.do?uid=admin&num=6]]>
     * </link> </item> </channel> </rss> xslURL: 解析的xsl的URL地址 obj: 解后赋值的对象
     * s:解析后赋值的方式，可以为innerHTML、innerText、value，如果参数空，为innerHTML tcObj:总条目数的赋值对象
     * tcS：总条目数的赋值方式 itemNode:item 节点 channelNode：channel 节点 countNode 是否有总数节点
     */
    var argv = transXMLListWithXSLAndGetCount.arguments;
    var argc = transXMLListWithXSLAndGetCount.arguments.length;
    var sumNode = countNode;

    var listXml = newXMLObject();
    listXml.async = false;
    listXml.load(xmlURL);
    var nodes = listXml.selectNodes("/rss/channel/item");

    if (nodes.length > 0) {
        var xmlList = [];
        var xmlCount = nodes.length;
        var loadXml = 0;
        var xslReady = false;
        var itemCount = 0;
        var sFlag = true;
        var tcSFlag = true;

        var xsl = newXMLObject();
        xsl.onreadystatechange = xslRead;
        xsl.async = true;

        function xmlRead(xml) {
            return function () {
                if (xml.readyState == 4) {
                    loadXml++;
                    doTrans();
                }
            };
        }

        function xslRead() {
            if (xsl.readyState == 4) {
                xslReady = true;
                doTrans();
            }
        }

        function doTrans() {
            if (loadXml == xmlCount && xslReady == true) {
                var xml = xmlList[0];
                var node = xml.selectSingleNode(channelNode);
                var tmpNodes = xml.selectNodes(itemNode);
                itemCount = itemCount + tmpNodes.length;
                for (var j = 1; j < xmlList.length; j++) {
                    var tmpXml = xmlList[j];
                    var tmpNodes = tmpXml.selectNodes(itemNode);
                    if (sumNode != "") {
                        var countNode = tmpXml.selectSingleNode(sumNode);
                        if (countNode.text != "") {
                            itemCount = itemCount + parseInt(sumNode.text);
                        }
                    } else {
                        itemCount = itemCount + tmpNodes.length;
                    }
                    for (var n = 0; n < tmpNodes.length; n++) {
                        node.appendChild(copyNode(xml, tmpNodes[n]));
                    }
                }

                var outEl;
                switch (typeof (obj)) {
                case "string":
                    outEl = document.getElementById(obj);
                    if (obj == "") {
                        sFlag = false;
                    }
                    break;
                case "object":
                    outEl = obj;
                }
                if (outEl == null) {
                    outEl = document.all.item(obj, 0);
                }
                if (outEl == null) {
                    outEl = document.all[obj];
                }
                if (sFlag != false) {
                    if (s != "") {
                        if (outEl != null) {
                            switch (s.toLowerCase()) {
                            case "innerhtml":
                                outEl.innerHTML = xml.transformNode(xsl);
                                break;
                            case "innertext":
                                outEl.innerText = xml.transformNode(xsl);
                                break;
                            case "value":
                                outEl.value = xml.transformNode(xsl);
                                break;
                            }
                        } else {
                            outEl.innerHTML = xml.transformNode(xsl);
                        }
                    } else {
                        outEl.innerHTML = xml.transformNode(xsl);
                    }
                } else {
                    document.write(xml.transformNode(xsl));
                }

                var tcOutEl;
                switch (typeof (tcObj)) {
                case "string":
                    tcOutEl = document.getElementById(tcObj);
                    if (tcObj == "") {
                        tcSFlag = false;
                    }
                    break;
                case "object":
                    tcOutEl = tcObj;
                }
                if (tcOutEl == null) {
                    tcOutEl = document.all.item(tcObj, 0);
                }
                if (tcOutEl == null) {
                    tcOutEl = document.all[tcObj];
                }

                if (tcSFlag != false) {
                    if (tcS != "") {
                        if (tcOutEl != null) {
                            switch (tcS.toLowerCase()) {
                            case "innerhtml":
                                tcOutEl.innerHTML = itemCount;
                                break;
                            case "innertext":
                                tcOutEl.innerText = itemCount;
                                break;
                            case "value":
                                tcOutEl.value = itemCount;
                                break;
                            }
                        } else {
                            tcOutEl.innerHTML = itemCount;
                        }
                    } else {
                        tcOutEl.innerHTML = itemCount;
                    }
                } else {
                    document.write(itemCount);
                }
                // 触发计算总待办及显示
                if (typeof (obj) == "string") {
                    /*
                     * switch(obj){ case "taskOA": //alert("22");
                     * taskoacountFlag = true; if(outEl != null){
                     * outEl.parentNode.style.display = ""; } break; }
                     */
                    totalTaskCount();
                }
            }
        }

        for (var i = 0; i < nodes.length; i++) {
            var tmpXml = newXMLObject();
            // debugger
            tmpXml.onreadystatechange = xmlRead(tmpXml);
            tmpXml.async = true;
            xmlList.push(tmpXml);
            try {
                tmpXml.load(nodes[i].selectSingleNode("link").text);
            } catch (err) {
            }
        }
        xsl.load(xslURL);
    }

}

function totalTaskCount() {

    var countOA = $("#countOA").text();
    var countMSS = $("#countMSS").text();
    var countJT = $("#countJT").text();
    var countOther = $("#countOther").text();

    jQuery("#menu_oacount").text(countOA);
    jQuery("#menu_msscount").text(countMSS);
    jQuery("#menu_jtcount").text(countJT);
    jQuery("#menu_othercount").text(countOther);

    if (countJT != "0") {
        jQuery("#taskJT").parent(".daiban_item").show();
    }
    if (countOther != "0") {
        jQuery("#taskOther").parent(".daiban_item").show();
    }

    // var totalcount = parseInt(countOA) + parseInt(countMSS) +
    // parseInt(countJT) + parseInt(countOther);
    // $("#categorytotlecount").html(totalcount);

}

function toggleFoldOrOpen(obj) {

    var taskLiList = jQuery(".daiban_item_tit");
    jQuery(taskLiList).each(function (i, n) {
        jQuery(n).removeClass();
        jQuery(n).addClass("daiban_item_tit fold");
        var ulobj = jQuery(n).siblings("ul");
        jQuery(ulobj).hide("fast");
    });
    jQuery(obj).removeClass();
    jQuery(obj).addClass("daiban_item_tit open");
    var ulobj = jQuery(obj).siblings("ul");
    // alert(ulobj.length)
    ulobj.show("fast");
}

function toggle(tab) {

    if (document.all.navi_second) {
        document.all.navi_second.scrollTop = 0;
    }
    var objs = document.all.ul_second.getElementsByTagName("li");
    for (i = 0; i < objs.length; i++) {
        var obj = objs[i];
        obj.style.display = "none";
    }
    objs = document.all.item(tab);
    if (objs == null) {
        return;
    }
    if (objs.length == null) {
        return;
    } else {
        // alert(objs.length)
        for (i = 0; i < objs.length; i++) {
            var obj = objs[i];
            obj.style.display = "";
        }
        /*
         * menuObjs = document.all.ul_first.getElementsByTagName("li");
         * 
         * for(j=0;j<menuObjs.length;j++){ var obj = menuObjs[j];
         * if(obj.id==tab){ obj.className="aLevel active"; }else{
         * obj.className=""; } }
         */
    }
}

// 待开相理
var index = window.index || {};
index.subTaskWindow = [];
index.arrDD = [];

function openTask(aobj, link) {
    var id = getTaskId(link);
    if (id == "") {
        var subWindow = window.open(link, "_blank");
    } else {
        var subWindow = window.open(link, id);
        // 在同一个窗口打开同一个文档
        subWindow.focus();
    }
    var ddobj = jQuery(aobj).parents("ul").first();
    if (subWindow) {
        index.arrDD.push(ddobj);
        index.subTaskWindow.push(subWindow);
    }
}
// 打开ITSM待办
function openITSMTask(aobj, link) {
    var subWindow = window.open(link, "_blank");
    var ddobj = jQuery(aobj).parents("ul").first();
    if (subWindow) {
        index.arrDD.push(ddobj);
        index.subTaskWindow.push(subWindow);
    }
}

function getTaskId(link) {
    var id = getQueryString(link, "id");
    if (id == "")
        id = getQueryString(link, "ID");
    if (id == "") {
        var idx1 = link.indexOf("/0/");
        var idx2 = link.indexOf("?open");
        if (idx1 != -1 && idx2 != -1 && idx2 - 35 == idx1) {
            id = link.substring(idx1 + 3, idx2);
        }
    }
    return id.toUpperCase();
}

// 获取URL参数
function getQueryString(url, pName) {
    var result = url.match(new RegExp("[\?\&]" + pName + "=([^\&]+)", "i"));
    if (result == null || result.length < 1) {
        return "";
    }
    return result[1];
}

function waitFlashTask() {
    var closedWindows = [];
    var skey = null;
    var num = null;
    for (var i = 0; i < index.subTaskWindow.length; i++) {
        var subWindow = index.subTaskWindow[i];
        if (subWindow.closed) {
            closedWindows.push(subWindow);
            skey = index.arrDD[i];
            num = i;
        }
    }

    if (closedWindows.length) {
        // alert(skey)
        reLoad(skey);
        clearArr(num);
    }
}

function reLoad(skey) {
    var arrDD = skey;
    var loadid = arrDD.attr("id");

    switch (loadid) {
    case "taskOA":
        initialOATask();
        break;
    case "taskMSS":
        initialMSSTask();
        // getMssCount();
        break;
    case "taskOther":
        initialOther();
        break;
    case "taskJT":
        initialJT();
        break;
    default:
    }

}

function clearArr(num) {
    index.subTaskWindow.splice(num, 1);
    index.arrDD.splice(num, 1);
}

// 左右导航条

function gotoLeft() {
    var leftindex = 0;
    var leftmax = 0;
    var shownum = 4;
    var curnode = $("#leftNavi");
    // leftmax = parseInt(curnode.find("ul").attr("count"));
    leftmax = curnode.find("ul li").length;
    curnode.find(".prev").bind("click", function () {
        if (leftindex > 0) {
            if (leftindex < shownum) {
                leftindex = 0;
            } else {
                leftindex = leftindex - shownum;
            }
            curnode.find("li").each(function () {
                var index = parseInt($(this).attr("index"));
                if (index > leftindex && index <= (leftindex + shownum)) {
                    $(this).css("display", "");
                } else {
                    $(this).css("display", "none");
                }
            });
        }
    });
    curnode.find(".next").bind("click", function () {
        if ((leftindex + shownum) < leftmax) {
            if ((leftindex + shownum * 2) > leftmax) {
                leftindex = leftmax - shownum;
            } else {
                leftindex = leftindex + shownum;
            }
        }
        curnode.find("li").each(function () {
            var index = parseInt($(this).attr("index"));
            if (index > leftindex && index <= (leftindex + shownum)) {
                $(this).css("display", "");
            } else {
                $(this).css("display", "none");
            }
        });
    });
}

function gotoRight() {
    var rightindex = 0;
    var rightmax = 0;
    var shownum = 4;
    var curnode = $("#rightNavi");
    // rightmax = parseInt(curnode.find("ul").attr("count"));
    rightmax = curnode.find("ul li").length;
    curnode.find(".prev").bind("click", function () {
        if (rightindex > 0) {
            if (rightindex < shownum) {
                rightindex = 0;
            } else {
                rightindex = rightindex - shownum;
            }
            curnode.find("li").each(function () {
                var index = parseInt($(this).attr("index"));
                if (index > rightindex && index <= (rightindex + shownum)) {
                    $(this).css("display", "");
                } else {
                    $(this).css("display", "none");
                }
            });
        }
    });
    curnode.find(".next").bind("click", function () {
        if ((rightindex + shownum) < rightmax) {
            if ((rightindex + shownum * 2) > rightmax) {
                rightindex = rightmax - shownum;
            } else {
                rightindex = rightindex + shownum;
            }
        }
        curnode.find("li").each(function () {
            var index = parseInt($(this).attr("index"));
            if (index > rightindex && index <= (rightindex + shownum)) {
                $(this).css("display", "");
            } else {
                $(this).css("display", "none");
            }
        });
    });
}

function loadNavi() {
    $("ul.nav > li > ul > il").hover(function (e) {
        $(e.target).next("ul").show();
    }, function () {
        $(e.target).next("ul").hide();
    });
}

function getNewsTag(pubDate) {
    if (pubDate) {
        if (pubDate.replace(/(^\s*)|(\s*$)/g, "") == "")
            return ("");
        var d2 = new Date();
        var d1 = new Date(pubDate.replace(/-/gi, "/"));
        // if ((d1.getYear() == d2.getYear()) && (d1.getMonth() ==
        // d2.getMonth()) && (d1.getDate() == d2.getDate())) {
        if ((d2 - d1) / (1000 * 60 * 60 * 24) < 3) {
            return "<img style='width:25px;height:10px;display:inline;float:none;' src='/homepage2015/images/new.gif' border='0'/>";
        }
    }
    return "";
}

function getLeftSubStr(str, len) {
    // alert(len)
    var result = "";
    if (str && str.length > len) {
        result = str.substring(0, len) + "...";
    } else {
        result = str;
    }
    return result;
}

function setCookie(name, value) {
    var ck = name + "=" + value + ";path=/;domain=.bf.ctc.com";
    document.cookie = ck;
}
function loginHr() {
    jQuery.get("/land/sso.nsf/hrsso3?openagent&now=" + new Date().getTime(),
            function (key) {
                if (key) {
                    if (key.indexOf("^") > -1) {
                        var ssokeys = key.split("^")
                        setCookie("MssSsoToken", ssokeys[0]);
                        setCookie("JtSsoToken", ssokeys[1]);
                    } else {
                        setCookie("MssSsoToken", "errorfalse");
                        setCookie("JtSsoToken", "errorfalse");
                    }
                    window.setTimeout(refreshHqSso, 500);
                }
            })
}

function refreshHqSso() {
    jQuery('#SsoHq_iframe')
            .attr(
                    'src',
                    "http://tyrz-hq.bf.ctc.com/uap/sso2.do?appCode=mssPortal&provinceCode=30&preLogin=true");
    // window.setTimeout(loginPortal,500);
}

// 集团MSS预登陆
var preLoginFlag = false;
var preLoginCount = 0;
function preLoginHandler() {
    if (preLoginCount > 40 && timer != null) {
        clearInterval(timer);
    }
    preLoginCount++;
    var result = getCookieValue('mssPortalPreLogin');
    if (result == null) {
        return;
    }
    if (result != null) {
        clearInterval(timer);
    }
    if (result == "0") {
        preLoginFlag = true;
    }
}

function getCookieValue(name) {
    var arg = name + "=";
    var alen = arg.length;
    var cookieStr = document.cookie;
    var clen = cookieStr.length;
    var i = 0;
    var j = 0;
    while (i < clen) {
        j = i + alen;
        if (cookieStr.substring(i, j) == arg) {
            var endstr = cookieStr.indexOf(";", j);
            if (endstr == -1) {
                endstr = cookieStr.length;
            }
            return unescape(cookieStr.substring(j, endstr));
        }
        i = document.cookie.indexOf(" ", i) + 1;
        if (i == 0)
            break;
    }
    return null;
}

var timer = setInterval(preLoginHandler, 1500);

function checkUrl(preUrl, ssoUrl) {
    if (preLoginFlag == true) {
        window.open(preUrl);
    } else {
        window.open(ssoUrl);
    }
}

// 排序
function getCompare() {
    return function (d1, d2) {
        var val1 = d1["seque"];
        var val2 = d2["seque"];
        if (!val1)
            val1 = d1["pubDate"];
        if (!val2)
            val2 = d2["pubDate"];
        if (val1 && val2) {
            return val2.localeCompare(val1);
        }
        return true;
    }
}