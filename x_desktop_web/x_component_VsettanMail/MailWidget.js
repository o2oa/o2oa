MWF.xDesktop.requireApp("VsettanMail", "Actions.RestActions", null, false);
MWF.xApplication.VsettanMail = MWF.xApplication.VsettanMail || {};
MWF.xApplication.VsettanMail.MailWidget = new Class({
    Extends: MWF.xApplication.Common.Widget,
    Implements: [Options, Events],
	options: {
        "style": "default",
        "title": MWF.xApplication.VsettanMail.LP.title,
        "appName": "VsettanMail",
        "name": "MailWidget",
        "position": {"right": 10, "bottom": 10},
        "width": "400",
        "height": "550"
	},
    loadContent: function(callback){
        this.ssoCount = 0;
        this.widget.node.setStyle("display", "none");
        //this.setTimeout();
        this.action = new MWF.xApplication.VsettanMail.Actions.RestActions();
        if (this.desktop.session.user.name!="xadmin") this.getUnreadCount();
        this.widget.close()
    },
    setTimeout: function(){
        window.setTimeout(function(){
            this.getUnreadCount();
        }.bind(this), 30000);
    },
    getUnreadCount: function(){
        this.checkUser(function(mail){
            this.getUnreadCountXml(mail);
        }.bind(this));
    },
    getUnreadCountXml: function(mail){
        var url = mail+"/iNotes/Proxy/?OpenDocument&Form=s_ReadViewEntries_JSONP&PresetFields=FolderName;($Inbox),UnreadCountInfo;1,callback;Request.JSONP.request_map.request_"+Request.JSONP.counter+"&Start=1&Count=0";

        //var url = "http://mail.vsettan.com.cn/land/xsso.nsf/(getUserMail)?openpage";

        MWF.getJSONP(url, {
            "onSuccess": function(json){
                if (json.unreadcount.toInt()>0) this.setFlagText(json.unreadcount);
                this.setTimeout();
            }.bind(this)
        });

        //try {
        //    var xml = new COMMON.XML();
        //    xml.get(url, function(xml){
        //        var unreadcount = xml.queryNode("readviewentries/unreadinfo/unreadcount").text();
        //        this.setFlagText(unreadcount);
        //        this.setTimeout();
        //    }.bind(this));
        //}catch(e){
        //    var iframe = new Element("iframe", {"styles": {"display": "none"}}).inject(this.desktop.desktopNode);
        //    iframe.set("src", url);
        //    window.setTimeout(function(){
        //        xml = new COMMON.XML(iframe.contentDocument)
        //        var unreadcount = xml.queryNode("readviewentries/unreadinfo/unreadcount").text();
        //        this.setFlagText(unreadcount);
        //        this.setTimeout();
        //        iframe.destroy();
        //    }.bind(this), 1000);
        //}

    },

    checkUser: function(callback, nosso){
        var url = "http://"+layout.config.mail+"/land/xsso.nsf/(getUserMail)?openpage";
        MWF.getJSONP(url, {
            "onSuccess": function(json){
                if (json.name==this.desktop.session.user.unique){
                    this.ssoCount = 0;
                    if (callback) callback(json.mail);
                }else{
                    if (!nosso){
                        if (this.ssoCount<5){
                            this.ssoMail(json.mail);
                            this.ssoCount++;
                        }
                    }
                }
            }.bind(this)
        }, true);
    },
    ssoMail: function(){
        this.action.getPassword(function(json){
            var url = "http://"+layout.config.mail+"/names.nsf?login?login&username="+this.desktop.session.user.unique+"&password="+json.data.password+"&RedirectTo=/land/xsso.nsf/(callback)?openpage";
            var iframe = new Element("iframe", {"styles": {"display": "none"}}).inject(this.desktop.desktopNode);
            iframe.set("src", url);

            window.setTimeout(function(){
                this.setTimeout();
                iframe.destroy();

                this.checkUser(function(mail){
                    this.getUnreadCountXml(mail);
                }.bind(this), true);

            }.bind(this), 2000);
        }.bind(this));
    },
    setFlagText: function(unreadcount){
        this.unreadcount = unreadcount;
        this.desktop.lnks.each(function(lnk){
            if (lnk.par=="VsettanMail"){
                if (!lnk.flagNode){
                    var node = this.createFlagNode();
                    node.inject(lnk.node);
                    lnk.flagNode = node;
                    var lnkSize = lnk.node.getSize();
                    var top = lnkSize.y;
                    node.setStyles({
                        "top": "-"+top+"px"
                    });
                }
                var txt = unreadcount;
                if (unreadcount.toInt()>99) txt = "99+";
                lnk.flagNode.set("text", txt);
            }
        }.bind(this));
        this.desktop.navi.navis.each(function(navi){
            var json = navi.retrieve("navi");
            if (json){
                if (json.action=="VsettanMail"){
                    var flagNode = navi.retrieve("flagNode");
                    if (!flagNode){
                        var node = this.createFlagNode();
                        node.inject(navi);
                        navi.store("flagNode", node);
                        flagNode = node;

                        var lnkSize = navi.getSize();
                        var top = lnkSize.y-5;
                        flagNode.setStyles({
                            "top": "-"+top+"px",
                            "margin-right": "12px"
                        });
                    }
                    var txt = unreadcount;
                    if (unreadcount.toInt()>99) txt = "99+";
                    flagNode.set("text", txt);
                }
            }
        }.bind(this));


        //if (this.desktop.top.loadMenuAction){
        //    if (!this.desktop.top.flagCountNode){
        //        var node = this.createFlagNode(this.css.flagNodeMenu);
        //        this.desktop.top.flagCountNode = node;
        //        node.inject(this.desktop.top.loadMenuAction);
        //
        //        var actionSize = this.desktop.top.flagCountNode.getSize();
        //        var top = actionSize.y-node.getSize().y;
        //        node.setStyles({
        //            "top": "-"+top+"px"
        //        });
        //    }
        //    var txt = unreadcount;
        //    if (unreadcount.toInt()>99) txt = "99+";
        //    this.desktop.top.flagCountNode.set("text", txt);
        //}
    },
    createFlagNode:function(css){
        var node = new Element("div", {
            "styles": css || this.css.flagNode
        });
        return node;
    }
});