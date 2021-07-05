MWF.xApplication.AppMarketV2.Application.options.multitask = true;
MWF.require("MWF.widget.MaskNode", null, false);
MWF.xApplication.AppMarketV2.Application.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
		"mvcStyle": "style.css",
		"view": "applicationView.html",
        "name": "AppMarketV2.Application",
        "icon": "icon.png",
        "width": "1000",
        "height": "700",
        "isResize": true,
		"isMax": true,
        "title": MWF.xApplication.AppMarketV2.Application.LP.title,
        "minHeight": 700
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.AppMarketV2.Application.LP;
        this.actions = MWF.Actions.load("x_program_center");
		this.viewPath = this.path+this.options.style+"/"+this.options.view;
		this.iconPath = this.path+this.options.style+"/icon/";
		this.collectToken = "";
		this.collectUrl = "";
		if (!this.status) {
        } else {
			this.options.appid = this.status.appid;
			this.options.appname = this.status.appname;			
        }
		this.appdata = {};
	},
	mask: function(){
        if (!this.maskNode){
			this.introducenode.setStyle("overflow","hidden");
            this.maskNode = new MWF.widget.MaskNode(this.introducenode, {"style": "bam"});
            this.maskNode.load();
        }
    },
    unmask: function(){
        if (this.maskNode) this.maskNode.hide(function(){
            MWF.release(this.maskNode);
			this.maskNode = null;
			this.introducenode.setStyle("overflow","auto");
        }.bind(this));
    },
    loadApplication: function(callback){

		if (this.collectToken=="" || this.collectUrl==""){
            //先登录collcect
            this.actions.CollectAction.login(//平台封装好的方法
                function( json ){ //服务调用成功的回调函数, json为服务传回的数据
                    if (json.type && json.type=="success"){
                        data = json.data; //为变量data赋值
                        this.collectUrl = data.collectUrl;
                        this.collectToken = data.collectToken;
                        this.content.loadHtml(this.viewPath, {"bind": {"lp": this.lp}, "module": this}, function(){
							if (!this.options.isRefresh){
								this.maxSize(function(){
									this.loadIntroduce(callback);
								}.bind(this));
							}else{
								this.loadIntroduce(callback);
							}
						}.bind(this));
                    }
                }.bind(this),null,false //同步执行 
            );
		}	
		
	},
	initNodeSize: function(){
		this.resizeNodeSize();
		this.addEvent("resize", this.resizeNodeSize.bind(this));
	},
	resizeNodeSize: function(){
		var size = this.content.getSize();
		var edge = this.introducenode.getEdgeHeight();
		var height = size.y - edge;
		if (height<this.options.minHeight) height = this.options.minHeight;
		this.introducenode.setStyle("height", ""+height+"px");
	},
	loadIntroduce:function(callback){
		this.initNodeSize();
		if (this.options.appid){
			debugger
			this.loadBbsInfo(this);
			this.actions.MarketAction.get(this.options.appid,function(json){
				if (json.data && json.data.icon){					
					this.appdata = json.data;
					this.setTitle(MWF.xApplication.AppMarketV2.Application.LP.title+"_"+this.appdata.name);
					var applicationicon = new Element("div",{"class":"o2_appmarket_application_introduce_icon"}).inject(this.applicationintroduceiconcontain);
					applicationicon.setStyle("background-image", "url(data:image/png;base64,"+this.appdata.icon+")");
					if (this.applicationintroduceiconcontain.clientWidth<300){
						applicationicon.setStyle("width",this.applicationintroduceiconcontain.clientWidth);
						applicationicon.setStyle("height",450*this.applicationintroduceiconcontain.clientWidth/300);
					}
					this.loadCommentsGrade(this.appdata);
					var price=this.appdata.price>0?this.appdata.price+"":"Free";
					this.applicationintroducememofree.set("text",price);
					this.applicationintroducememoname.set("text",this.appdata.name);
					debugger;
					var commentcount = 0;
					var grade = 0;
					var totalgrade = 0;
					var commentratiolist = this.gradeData.data;
					var gradeList = ["0","0","0","0","0"];
					commentratiolist.each(function(pergrade){
						gradeList[parseInt(pergrade.grade)-1]=pergrade.count;
						commentcount +=parseInt(pergrade.count)
					}.bind(this));

					gradeList.each(function(pergrade,index){
						totalgrade += parseInt(pergrade)*(index+1)
					})
					if (commentcount>0){
						grade = this.numberFix(totalgrade/commentcount,1)
					}
					this.applicationintroducememoremarkgrade.set("text",grade+"");
					var intgrade = parseInt(grade);
					var dotgrade = grade - intgrade;
					/*var grade = this.numberFix(this.appdata.grade,1);
					this.applicationintroducememoremarkgrade.set("text",grade);
					var intgrade = parseInt(grade);
					var dotgrade = grade - intgrade;*/
debugger;


					for (var tmpnum=0;tmpnum<intgrade;tmpnum++){
						new Element("img",{"src":this.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(this.applicationintroducememoremarkiconangular)
					}
					if (dotgrade>=0.5){
						new Element("img",{"src":this.iconPath+"halffiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(this.applicationintroducememoremarkiconangular);
						intgrade++;
					}
					for (var tmpnum=0;tmpnum<5-intgrade;tmpnum++){
						new Element("img",{"src":this.iconPath+"whitefiveangular.png","class":"o2_appmarket_application_introduce_memo_remark_inner_pic"}).inject(this.applicationintroducememoremarkiconangular);
					}
					if (!this.appdata.commentCount) this.appdata.commentCount=0;					
					this.applicationintroducememoremarkcommentcount.set("text",this.lp.commentCountText.replace("{n}", commentcount))
					//this.applicationintroducememodownload.set("text",this.appdata.downloadCount);
					this.applicationintroducememocategory.set("text", this.lp.category+":"+this.appdata.category);
					this.applicationintroducememocontent.set("text",this.appdata.describe);
					//this.applicationintroducedownloadprice.set("text","$"+this.appdata.price);
					this.applicationintroducedownloadprice.set("text","");

					var bottomtext =this.lp.setup;
					if (this.appdata.installedVersion && this.appdata.installedVersion!=""){
						if (this.appdata.installedVersion==this.appdata.version){
							 bottomtext = this.lp.setupDone;
						}else{
							 bottomtext = this.lp.update;
						}
					}
					this.applicationintroducedownloadbtntext.set("text",bottomtext);
					
					var _self = this;
					this.applicationintroducedownloadbtn.store("data",this.appdata);
					this.applicationintroducedownloadbtn.addEvents({
						"click": function(e){
							//updateorinstall application
							var d = this.retrieve("data");
							if (d){
								_self.installapp(e,d);
							}
						}
					})
					//add by xlq @20201104 for adding button which goes to bbs.
					this.applicationintroduceforumbtntext.set("text",this.lp.bbsname);
					this.applicationintroduceforumbtn.addEvents({
						"click": function(e){
							//updateorinstall application
							window.open(_self.lp.bbslink);
						}
					})
					//this.applicationintroducefavbtntext.set("text","下载");

					this.loadIntroduceInfo();
					
				}
				this.fireEvent("load");
			}.bind(this));
		}
		
		if (callback) callback();
	},
	loadBbsInfo: function(content){
		var json = null;
		debugger;
		var commenturl = content.collectUrl +'/o2_collect_assemble/jaxrs/collect/config/key/(0)?time='+(new Date()).getMilliseconds();
		debugger;
		var res = new Request.JSON({
			url: commenturl,
			headers : {'x-debugger' : true,'Authorization':content.collectToken,'c-token':content.collectToken},
			secure: false,
			method: "get",
			async: false,
			withCredentials: true,
			contentType : 'application/json',
			crossDomain : true,
			onSuccess: function(responseJSON, responseText){
				json = responseJSON;
				debugger;
				this.bbsUrlPath = json.data.bbsUrlPath;
				this.bbsUrl = json.data.bbsUrl;
			}.bind(this),
			onFailure: function(xhr){
				o2.runCallback(callback, "requestFailure", [xhr]);
			}.bind(this),
			onError: function(text, error){
				o2.runCallback(callback, "error", [text, error]);
			}.bind(this)
		});
		res.send();
	},
	loadCommentsGrade: function(appdata){
    	debugger;
		var json = null;
		var commenturl =  this.bbsUrlPath +'/x_bbs_assemble_control/jaxrs/subject/statgrade/sectionName/'+encodeURI(this.lp.title)+'/subjectType/'+encodeURI(appdata.name)+'?time='+(new Date()).getMilliseconds();
		var res = new Request.JSON({
			url: commenturl,
			headers : {'x-debugger' : true,'Authorization':this.collectToken,'c-token':this.collectToken},
			secure: false,
			method: "get",
			async: false,
			withCredentials: true,
			contentType : 'application/json',
			crossDomain : true,
			onSuccess: function(responseJSON, responseText){
				debugger;
				this.gradeData = responseJSON;
			}.bind(this)
		});
		res.send();
	},

    tabover: function(e){
        e.currentTarget.addClass("o2_appmarket_appcategory_tab_over");
    },
    tabout: function(e){
        e.currentTarget.removeClass("o2_appmarket_appcategory_tab_over");
        //e.currentTarget.removeClass("mainColor_border").removeClass("mainColor_color");
	},
	mouseover:function(){
		this.addClass("o2_appmarket_appcategory_tab_over");
	},
	mouseout:function(){
		this.removeClass("o2_appmarket_appcategory_tab_over");
	},
	installapp:function(e,d){
		var p = e.target.getPosition();
		var tmpe = {"event": {"x": p.x+40, "y": p.y}};
		var confirmtitle = d.installedVersion==""?this.lp.confirmsetupTitle:this.lp.confirmupdateTitle;
		var confirmcontent = d.installedVersion==""?this.lp.confirmsetupContent:this.lp.confirmupdateContent;
		var _self = this;
		_self.confirm("warn", tmpe, confirmtitle, confirmcontent, 300, 120, function(){
			_self.mask();
			//this.createLoading(this.container,true);  
			//alert("after createLoading")          
			_self.actions.MarketAction.installOrUpdate(
				d.id,
			function( json ){ 
				data = json.data; 
				_self.notice(d.name+" "+_self.lp.setupSuccess, "success");
				_self.unmask();
				//this.clearLoading()
			}.bind(_self),
			function( json ){ 
				data = json.data; 
				_self.unmask();
				//this.clearLoading()
			}.bind(_self),
				true
			);
			this.close();
		}, function(){
			this.close();
		}, null, null, "o2");        
	},

	loadIntroduceInfo: function(callback){
		var _self = this;
		this.applicationintroducesinfoTab.getParent().getElements(".o2_appmarket_application_introduce_tab_current").removeClass("mainColor_color").removeClass("o2_appmarket_application_introduce_tab_current").addClass("o2_appmarket_application_introduce_tab");
		this.applicationintroducesinfoTab.removeClass("o2_appmarket_application_introduce_tab").addClass("mainColor_color").addClass("o2_appmarket_application_introduce_tab_current");
		this.applicationintroducecontent.set("html","");
		this.applicationintroducecontent.set("html",this.appdata.abort);
		this.applicationintroducepicslable.set("html","");
		this.applicationintroducepics.set("html","")
		this.applicationintroducepicslable.set("html", this.lp.screenshot);			//截图
		this.appdata.attList.each(function(peratt,i){
				if (peratt.type == "image"){
					picdiv = new Element("img",{"class":"o2_appmarket_application_introduce_pic"}).inject(this.applicationintroducepics);
					picdiv.setProperty("src", "data:image/png;base64,"+peratt.icon);
					picdiv.setProperty("data-original",this.collectUrl +'/o2_collect_assemble/jaxrs/attachment/download/'+peratt.id+"?c-token="+this.collectToken);
					picdiv.setProperty("alt",peratt.name);
					//picdiv.store("id",peratt.id);
				}
		}.bind(this));
		this.loadImgView(this.appdata.id);
	},
	
	loadImgView:function(viewid){			
        if(this.viewer) this.viewer.destroy();
        this.applicationintroducepics.setProperty("id",viewid);
        o2.loadCss(this.path+this.options.style+"/viewer.css", this.content,function(){
            o2.load(this.path+this.options.style+"/viewer.js", function(){
                this.viewer = new Viewer(document.getElementById(viewid), {
                	url: 'data-original'
                });                
            }.bind(this));
        }.bind(this));           
	},
	
	loadIntroduceDemand:function(callback){
		this.applicationintroducedemandTab.getParent().getElements(".o2_appmarket_application_introduce_tab_current").removeClass("mainColor_color").removeClass("o2_appmarket_application_introduce_tab_current").addClass("o2_appmarket_application_introduce_tab");
		this.applicationintroducedemandTab.removeClass("o2_appmarket_application_introduce_tab").addClass("mainColor_color").addClass("o2_appmarket_application_introduce_tab_current");
		this.applicationintroducecontent.set("html","");
		this.applicationintroducepicslable.set("html","");
		this.applicationintroducepics.set("html","")
		this.applicationintroducecontent.set("html",this.appdata.installSteps);

	},
	loadIntroduceComment:function(callback){
		this.applicationintroducecommentTab.getParent().getElements(".o2_appmarket_application_introduce_tab_current").removeClass("mainColor_color").removeClass("o2_appmarket_application_introduce_tab_current").addClass("o2_appmarket_application_introduce_tab");
		this.applicationintroducecommentTab.removeClass("o2_appmarket_application_introduce_tab").addClass("mainColor_color").addClass("o2_appmarket_application_introduce_tab_current");
		this.applicationintroducecontent.set("html","");
		this.applicationintroducepicslable.set("html","");
		this.applicationintroducepics.set("html","");
		o2.requireApp("AppMarketV2.Application", "Comment", function(){
			new MWF.xApplication.AppMarketV2.Application.Comment(this, this.applicationintroducecontent, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	},
	recordStatus: function(){
	    debugger;
        return {"appid": this.options.appid,"appname":this.options.appname};
    },
    numberFix:function(data,n){
        var numbers = '';
        // 保留几位小数后面添加几个0
        for (var i = 0; i < n; i++) {
            numbers += '0';
        }
        var s = 1 + numbers;
        // 如果是整数需要添加后面的0
        var spot = "." + numbers;
        // Math.round四舍五入  
        //  parseFloat() 函数可解析一个字符串，并返回一个浮点数。
        var value = Math.round(parseFloat(data) * s) / s;
        // 从小数点后面进行分割
        var d = value.toString().split(".");
        if (d.length == 1) {
            value = value.toString();
            return value;
        }
        if (d.length > 1) {
            if (d[1].length < n) {
                value = value.toString() + "0";
            }
            return value;
        }
    }
});
