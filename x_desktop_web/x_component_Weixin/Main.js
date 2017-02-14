MWF.xApplication.Weixin.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Weixin",
		"icon": "icon.png",
		"width": "1200",
		"height": "630",
		"title": MWF.xApplication.Weixin.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Weixin.LP;
	},
	loadApplication: function(callback){
		this.createNode();
		this.loadApplicationContent();
		if (callback) callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
	loadApplicationContent: function(){
		this.contentNode = new Element("div", {
			"styles": {
				"margin": "30px",
				"overflow": "hidden",
				"font-size": "18px"
			}
		}).inject(this.node);
		var html = "<table width='90%' align='center'><tr>" +
				"<td width='100px'>发送给</td><td><input style='width:100%' type='text' id='sendTo'/></td><td rowspan='3' style='width:160px' VALIGN='top'>" +
				"<img src='"+this.path+this.options.style+"/icon/zone.png"+"'/><p style='font-size: 14px; text-align:center'>微信扫描二维码<br>关注兰德纵横企业微信</p></td></tr><tr>" +
				"<td width='100px'>标　题</td><td><input style='width:100%' type='text' id='subject'/></td></tr><tr>" +
				"<td width='100px'>内　容</td><td id='contentArea'></td></tr></table>";
		this.contentNode.set("html", html);
		
		this.contentAreaNode = this.node.getElement("#contentArea");
		this.loadCkeditor();
		
		this.actionNode = new Element("div", {
			"styles": {
				"width": "220px",
				"margin":"auto",
				"overflow": "hidden",
				"font-size": "14px"
			}
		}).inject(this.node);
		
		this.button = new Element("button", {
			"text": "通过微信发送公告",
			"styles": {
				"margin": "auto",
				"width": "220px",
				"height": "40px",
				"font-size": "16px",
				"cursor": "pointer",
				"background-color": "#7285bb",
				"border": "1px solid #666",
				"color": "#FFF"
			},
			"events": {
				"click": function(){
					var user = this.node.getElement("#sendTo").get("value");
					var title = this.node.getElement("#subject").get("value");
				//	var content = this.node.getElement("#content").get("value");
				//	alert(this.editor.getData());
					var json = {
						"appId":"xbpm",
						"appPwd":"zone2009",
						"toUsers":user,
						"msgType":"5",
						"title":title,
						"busType":"通知公告",
						"content":this.editor.getData(),
						"sendDate":new Date().format("db")
					};
					var myRequest = new Request({
						url:"http://mvn.zoneland.net/wx/api/rest/sendmsg",
						method:"post",
//						data:{"json":jsonStr},
						success:function(data){
							this.notice("信息已发送", "success", this.appContentNode);
					    }.bind(this)
//					    onProgress: function(event, xhr){
//					        var loaded = event.loaded, total = event.total;
//
//					        console.log(parseInt(loaded / total * 100, 10));
//					    }
					});

					myRequest.send("json="+JSON.encode(json));
					
					
					
					
				}.bind(this)
			}
		}).inject(this.actionNode);
	},
	loadCkeditor: function(config){
		COMMON.AjaxModule.load("ckeditor", function(){
//			var editorDiv = new Element("div").inject(this.node);
//			var height = this.node.getSize().y;
			var editorConfig = {};
			this.editor = CKEDITOR.appendTo(this.contentAreaNode, editorConfig);
			
//			this.editor.on("loaded", function(){
//				this.editor.setReadOnly(true);
//			}, this);
		}.bind(this));
	}
	
});

