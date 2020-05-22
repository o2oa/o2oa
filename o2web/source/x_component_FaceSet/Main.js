MWF.xDesktop.requireApp("FaceSet", "Actions.RestActions", null, false);
MWF.xApplication.FaceSet.options.multitask = false;
MWF.xApplication.FaceSet.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "FaceSet",
		"icon": "icon.png",
		"width": "1010",
		"height": "560",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.FaceSet.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.FaceSet.LP;
        this.action = MWF.Actions.get("x_faceset_control");
        this.faceTokens = [];
        //this.action = new MWF.xApplication.FaceSet.Actions.RestActions();
	},

	loadApplication: function(callback){
		this.node = new Element("div", {"styles": this.css.node}).inject(this.content);
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.picAreaNode = new Element("div", {"styles": this.css.picAreaNode}).inject(this.node);
        COMMON.AjaxModule.loadDom("../o2_lib/adapter/adapter.js", function(){
			this.createVideo();
			this.createAction();
            this.createInfor();
            if (callback) callback();
        }.bind(this));
	},
	createVideo: function(){
        this.videoAreaNode = new Element("div", {"styles": this.css.videoAreaNode}).inject(this.contentNode);
        this.videoAreaNode.set("html", "<video autoplay>"+this.lp.noMedia+"</video>");
        this.video = this.videoAreaNode.getFirst().setStyles(this.css.video);
        this.videoStart();
	},
	videoStart: function(){
        navigator.mediaDevices.getUserMedia({
            audio: false,
            video: true
        }).then(function(stream){
            this.video.srcObject = stream;
        }.bind(this)).catch(function(error){
            console.log('navigator.getUserMedia error: ', error);
        });
	},
    createAction: function(){
	    this.actionAreaNode = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.contentNode);
		this.actionNode = new Element("button", {"styles": this.css.actionNode, "text": this.lp.button_takePic}).inject(this.actionAreaNode);
        this.actionNode.addEvent("click", function(){
			this.takePhoto();
		}.bind(this));

        this.actionCompletedNode = new Element("button", {"styles": this.css.actionNode, "text": this.lp.button_completed}).inject(this.actionAreaNode);
        this.actionCompletedNode.setStyle("display", "none");
        this.actionCompletedNode.addEvent("click", function(){
            this.completed();
        }.bind(this));
	},
    createInfor: function(){
        this.inforNode = new Element("div", {"styles": this.css.inforNode}).inject(this.contentNode);
        this.setInfor();
    },
    setInfor: function(text){
        if (text){
            this.inforNode.set("text", text);
        }else{
            switch (this.faceTokens.length){
                case 1:
                    this.inforNode.set("text", this.lp.pic2);
                    break;
                case 2:
                    this.inforNode.set("text", this.lp.pic3);
                    break;
                case 3:
                    this.inforNode.set("text", this.lp.completed);
                    break;
                default:
                    this.inforNode.set("text", this.lp.pic1);
                    break;
            }
        }
    },
	takePhoto: function(){
        this.inforNode.set("text", this.lp.getFaceToken);
        this.actionNode.set("disable", true);

        var canvasAreaNode = new Element("div", {"styles": this.css.canvasAreaNode}).inject(this.picAreaNode);
		var canvas = new Element("canvas", {"styles": this.css.canvas}).inject(canvasAreaNode);
        // canvas.width = this.video.videoWidth;
        // canvas.height = this.video.videoHeight;
        canvas.width = 308;
        canvas.height = (308/this.video.videoWidth)*this.video.videoHeight;
        var margin = (225-canvas.height)/2;
        canvas.setStyle("margin-top", ""+margin+"px");
        canvas.getContext('2d').drawImage(this.video, 0, 0, canvas.width, canvas.height);

        this.detect(canvas);
	},
    detect: function(canvas){
        var blob = this.toBlob(canvas.toDataURL());
        var formData = new FormData();
        formData.append('file', blob);

		this.action.detect(this.desktop.session.user.unique, "1", formData, {"name": "pic", "size": blob.size}, function(json){
		    var face = json.data.faces[0];
            this.faceTokens.push(face.face_token);
            this.setInfor();
            if (this.faceTokens.length>=3){
                this.actionCompletedNode.setStyle("display", "block");
                this.actionNode.setStyle("display", "none");
            }else{
                this.actionCompletedNode.setStyle("display", "none");
                this.actionNode.setStyle("display", "block");
                this.actionNode.set("disable", false);
            }
            this.drawToken(canvas, face);
		}.bind(this));
	},
    drawToken: function(canvas, face){
        var ctx = canvas.getContext('2d');
        ctx.fillStyle = "#00b9eb";
        Object.each(face.landmark, function(v, k){
            ctx.fillRect(v.x, v.y, 2,2);
        }.bind(this));
    },

    // recordFaceToken: function(face){
    //     var action = MWF.Actions.get("x_organization_assemble_control");
    //     action.listPersonAttribute(this.desktop.session.user.id, function(json){
    //         var attr = null;
    //         for (var i = 0; i<json.data.length; i++){
    //             if (json.data[i].name==="facetoken"){
    //                 attr = json.data[i];
    //                 break;
    //             }
    //         }
    //         if (attr){
    //             if (!attr.attributeList) attr.attributeList = [];
    //             if (attr.attributeList.length>=3){
    //                 var removeFace = attr.attributeList.shift();
    //
    //             }
    //         }else{
    //
    //         }
    //
    //     }.bind(this));
    // },

    toBlob : function( base64 ){
        var bytes;
        if( base64.substr( 0, 10 ) === 'data:image' ){
            bytes=window.atob(base64.split(',')[1]);
        }else{
            bytes=window.atob(base64)
        }
        var ab = new ArrayBuffer(bytes.length);
        var ia = new Uint8Array(ab);
        for (var i = 0; i < bytes.length; i++) {
            ia[i] = bytes.charCodeAt(i);
        }
        return new Blob( [ab] , {type : "image/png" });
    },

    completed: function(){
        var action = MWF.Actions.get("x_organization_assemble_control");
        MWF.UD.getDataJson("faceTokens", function(json){
            if (!json) json = {"tokens": []};

            var faceset = window.location.host;
            faceset = faceset.replace(/\./g, "_");

            this.action.getFaceSet(faceset, null, function(){
                this.completedToken(json);
            }.bind(this), function(){
                this.action.createFaceSet(faceset, null, function(){
                    this.completedToken(json);
                }.bind(this));
            }.bind(this));
        }.bind(this));

        // action.listPersonAttribute(this.desktop.session.user.id, function(json){
        //     var attr = null;
        //     for (var i = 0; i<json.data.length; i++){
        //         if (json.data[i].name==="facetoken"){
        //             attr = json.data[i];
        //             break;
        //         }
        //     }
        //     if (!attr){
        //         attr = {
        //             "description": this.lp.description,
        //             "name": "facetoken",
        //             "person": this.desktop.session.user.id,
        //             "attributeList": []
        //         };
        //     }
        //
        // }.bind(this),function(e){
        //     this.inforNode.set("text", this.lp.saveAttrError);
        // }.bind(this));
    },
    completedToken: function(json){
        this.removeTokenFromFaceset(json, function(){
            this.saveTokenToFaceset(function(){

                json.tokens = this.faceTokens;
                MWF.UD.putData("faceTokens", json, function(){
                    this.close();
                }.bind(this),function(e){
                    this.inforNode.set("text", this.lp.saveAttrError);
                }.bind(this))

            }.bind(this), function(){
                this.inforNode.set("text", this.lp.saveAttrError);
            }.bind(this));
        }.bind(this), function(){
            this.inforNode.set("text", this.lp.saveAttrError);
        }.bind(this));
    },

    removeTokenFromFaceset:function(json, success, error){
        var faceset = window.location.host;
        faceset = faceset.replace(/\./g, "_");

        if (json && json.tokens.length){
            var data = {"data": json.tokens};
            this.action.removeface(faceset, data, success, error);
        }else{
            if (success) success();
        }
    },
    saveTokenToFaceset: function(success, error){
        var faceset = window.location.host;
        faceset = faceset.replace(/\./g, "_");
        var data = {"data": this.faceTokens};
        this.action.addface(faceset, data, success, error);
    }

});
