o2.widget = o2.widget || {};
o2.require("o2.widget.UUID", null, false);
o2.widget.MediaRecorder = o2.MediaRecorder = new Class({
	Implements: [Options, Events],
    Extends: o2.widget.Common,
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$MediaRecorder/",
		"videoHeight" : "232px",
		"constraints" : {
			audio: true,
			video: true
		},
		"reference": "",
		"referenceType" : ""

	},
	initialize: function(node, options){
		this.node = node;
		this.setOptions(options);

		this.path = this.options.path || (o2.session.path+"/widget/$MediaRecorder/");
		this.cssPath = this.path + this.options.style+"/css.wcss";


		this._loadCss();
		this.fireEvent("init");
	},
	load: function(){
		this.container = new Element("div.container", { styles :  this.css.container}).inject(this.node);
		if( !this.checkBroswer() )return;
		this.loadResource( function(){
			//this.checkDevice( function(){
				this._load();
			//	this.uuidInstance = new o2.widget.UUID();
			//	this.uuid = this.uuidInstance.valueOf();
			//	o2.widget.MediaRecorder.instanceObject[ this.uuid ] = this;
			//}.bind(this));
		}.bind(this))
	},
	//checkDevice : function( callback ){
	//	var flag;
	//	if( o2.MediaRecorder.hasActiveInstance() ){
	//		o2.xDesktop.confirm("warn", null, "设备已经被占用", "设备已经被占用,是否释放被占用的设备？", 350, 120, function () {
	//			o2.MediaRecorder.stopActiveInstance();
	//			this.close();
	//			callback();
	//			flag = true;
	//		}, function () {
	//			this.close();
	//			flag = false;
	//		});
	//	}else{
	//		callback();
	//		flag = true;
	//	}
	//	return flag;
	//},
	loadResource : function( callback ){
		var path = "/o2_lib/adapter/adapter.js";
		COMMON.AjaxModule.load( path, function () {
			if (callback)callback();
		}.bind(this));
	},
	_load : function(){
		/*
		 *  Copyright (c) 2015 The WebRTC project authors. All Rights Reserved.
		 *
		 *  Use of this source code is governed by a BSD-style license
		 *  that can be found in the LICENSE file in the root of the source
		 *  tree.
		 */

		// This code is adapted from
		// https://rawgit.com/Miguelao/demos/master/mediarecorder.html
		/* globals MediaRecorder */

		this.gumVideo = new Element("video", { styles : this.css.gumVideo, autoplay: true, muted : true }).inject( this.container );
		this.gumVideo.setStyles( { height : parseInt(this.options.videoHeight )+"px", width : "calc(20em - 10px)" } );

		this.recordedVideo = new Element("video", { styles : this.css.recordedVideo, loop : true, controls : true }).inject( this.container );
		this.recordedVideo.setStyles( { height : parseInt(this.options.videoHeight )+"px", width : "calc(20em - 10px)" } );

		var toolbar = new Element("div", { styles : this.css.toolbar }).inject( this.container );

		this.recordButton = new Element("button", {
			styles : this.css.recordButton, text : "开始录制",
			events : {
				click : function(){
					this.toggleRecording();
				}.bind(this)
			}
		}).inject( toolbar );

		this.playButton = new Element("button", {
			styles : this.css.playButton, text : "播放", disable : true,
			events : {
				click : function(){
					this.play();
				}.bind(this)
			}
		}).inject( toolbar );

		this.downloadButton = new Element("button", {
			styles : this.css.downloadButton, text : "下载", disable : true,
			events : {
				click : function(){
					this.download();
				}.bind(this)
			}
		}).inject( toolbar );

		var mediaSource = this.mediaSource = new MediaSource();
		mediaSource.addEventListener('sourceopen', this.handleSourceOpen.bind(this), false);

		navigator.mediaDevices.getUserMedia( this.options.constraints).
			then( this.handleSuccess.bind(this) ).
			catch( this.handleError.bind(this) );

		this.recordedVideo.addEventListener('error', function(ev) {
			console.error('MediaRecording.recordedMedia.error()');
			alert('Your browser can not play\n\n' + this.recordedVideo.src
				+ '\n\n media clip. event: ' + JSON.stringify(ev));
		}.bind(this), true);

	},
	handleSuccess: function (stream) {
		this.recordButton.disabled = false;
		console.log('getUserMedia() got stream: ', stream);
		this.stream = stream;
		this.activeFlag = true;
		this.gumVideo.srcObject = stream;
	},
	handleError: function (error) {
		console.log('navigator.getUserMedia error: ', error);
	},
	handleSourceOpen: function (event) {
		console.log('MediaSource opened');
		this.sourceBuffer = this.mediaSource.addSourceBuffer('video/webm; codecs="vp8"');
		console.log('Source buffer: ', this.sourceBuffer);
	},
	handleDataAvailable : function (event) {
		if (event.data && event.data.size > 0) {
			this.recordedBlobs.push(event.data);
		}
	},
	handleStop: function (event) {
		console.log('Recorder stopped: ', event);
	},
	toggleRecording: function () {
		if ( this.isRecording ) {
			this.stopRecording();
			this.recordButton.textContent = '开始录制';
			this.playButton.disabled = false;
			this.downloadButton.disabled = false;
			this.isRecording = false;
		} else {
			this.startRecording();
			this.recordButton.textContent = '结束录制';
			this.isRecording = true;
		}
	},
	startRecording: function () {
		this.recordedBlobs = [];
		var options = {mimeType: 'video/webm;codecs=vp9'};
		if (!MediaRecorder.isTypeSupported(options.mimeType)) {
			console.log(options.mimeType + ' is not Supported');
			options = {mimeType: 'video/webm;codecs=vp8'};
			if (!MediaRecorder.isTypeSupported(options.mimeType)) {
				console.log(options.mimeType + ' is not Supported');
				options = {mimeType: 'video/webm'};
				if (!MediaRecorder.isTypeSupported(options.mimeType)) {
					console.log(options.mimeType + ' is not Supported');
					options = {mimeType: ''};
				}
			}
		}
		try {
			var mediaRecorder = this.mediaRecorder = new MediaRecorder(this.stream, options);
		} catch (e) {
			console.error('Exception while creating MediaRecorder: ' + e);
			alert('Exception while creating MediaRecorder: '
				+ e + '. mimeType: ' + options.mimeType);
			return;
		}
		console.log('Created MediaRecorder', mediaRecorder, 'with options', options);
		this.recordButton.textContent = 'Stop Recording';
		this.playButton.disabled = true;
		this.downloadButton.disabled = true;
		mediaRecorder.onstop = this.handleStop.bind(this);
		mediaRecorder.ondataavailable = this.handleDataAvailable.bind(this);
		mediaRecorder.start(10); // collect 10ms of data
		console.log('MediaRecorder started', mediaRecorder);
	},
	stopRecording : function () {
		this.mediaRecorder.stop();
		console.log('Recorded Blobs: ', this.recordedBlobs);
		this.recordedVideo.controls = true;
	},
	play: function () {
		var superBuffer = new Blob(  this.recordedBlobs, {type: 'video/webm'});
		var recordedVideo = this.recordedVideo;
		recordedVideo.src = window.URL.createObjectURL(superBuffer);
		// workaround for non-seekable video taken from
		// https://bugs.chromium.org/p/chromium/issues/detail?id=642012#c23
		recordedVideo.addEventListener('loadedmetadata', function() {
			if (recordedVideo.duration === Infinity) {
				recordedVideo.currentTime = 1e101;
				recordedVideo.ontimeupdate = function() {
					recordedVideo.currentTime = 0;
					recordedVideo.ontimeupdate = function() {
						delete recordedVideo.ontimeupdate;
						recordedVideo.play();
					};
				};
			}
		});
	},
	download: function () {
		var blob = new Blob( this.recordedBlobs, {type: 'video/webm'});
		var url = window.URL.createObjectURL(blob);
		var a = document.createElement('a');
		a.style.display = 'none';
		a.href = url;
		a.download = 'test.webm';
		document.body.appendChild(a);
		a.click();
		setTimeout(function() {
			document.body.removeChild(a);
			window.URL.revokeObjectURL(url);
		}, 100);
	},

	getFormData : function(){
		var formData = new FormData();
		formData.append('file',this.resizedImage, this.fileName );
		return formData;
	},
	checkBroswer : function(){
		// window.isSecureContext could be used for Chrome
		var isSecureOrigin = location.protocol === 'https:' ||
			location.hostname === 'localhost';
		if (!isSecureOrigin) {
			this.available = false;
			this.container.set("html", "<p>视频录制功能需要用在 https 协议下才能使用</p>");
			//alert('getUserMedia() must be run from a secure origin: HTTPS or localhost.' +
			//	'\n\nChanging protocol to HTTPS');
			//location.protocol = 'HTTPS';

			//this.available = false;
			//return false;
		}

		var unavailableFeatures = o2.MediaRecorder.getUnavailableBrowerFeatures();
		if( unavailableFeatures.length > 0 ){
			var  html = unavailableFeatures.map(function(item, index){
				return "<li>"+ item +"</li>";
			}).join("");
			this.available = false;
			this.container.set("html", "<p>视频录制功能不能使用，您的浏览器不支持以下特性:</p><ul>" +  html + "</ul>");
			return false;
		}
		return true;
	},
	stopDevice : function(){
		if( this.sourceBuffer ){
			this.mediaSource.removeSourceBuffer( this.sourceBuffer );
		}
		if( this.stream ){
			var tracks = this.stream.getTracks();
			for( var i=0; i< tracks.length; i++ ){
				tracks[i].stop();
			}
			this.stream = null;
			this.activeFlag = false;
		}
	},
	close : function(){
		this.stopDevice();
		if(this.gumVideo)this.gumVideo.destroy();
		this.container.destroy();
		delete this;
	},
	isActive : function(){
		return !!this.activeFlag;
	},
	reset: function(){

	}

});

o2.MediaRecorder.getUnavailableBrowerFeatures = function(){
	var unavailableFeatures = [];
	if( !window.navigator || !window.navigator.mediaDevices ){ unavailableFeatures.push( "mediaDevices" ) }
	if( !window.Blob ){ unavailableFeatures.push( "Blob" ) }
	if( !window.URL ){ unavailableFeatures.push( "URL" ) }
	if( !window.MediaRecorder ){ unavailableFeatures.push( "MediaRecorder" ) }
	if( !window.MediaSource ){ unavailableFeatures.push( "MediaSource" ) }
	return unavailableFeatures;
};

o2.MediaRecorder.isAvailable = function(){
	// window.isSecureContext could be used for Chrome
	var isSecureOrigin = location.protocol === 'https:' ||
		location.hostname === 'localhost';
	if (!isSecureOrigin) {
		return false;
	}
	if( o2.MediaRecorder.getUnavailableBrowerFeatures().length > 0 ){
		return false;
	}
	return true;
};

//o2.MediaRecorder.instanceObject = {};
//
//o2.MediaRecorder.hasActiveInstance = function(){
//	for( var id in o2.MediaRecorder.instanceObject ){
//		var instance = o2.MediaRecorder.instanceObject[id];
//		if( instance.isActive() ){
//			return true;
//		}
//	}
//	return false;
//};
//
//o2.MediaRecorder.stopActiveInstance = function(){
//	for( var id in o2.MediaRecorder.instanceObject ){
//		var instance = o2.MediaRecorder.instanceObject[id];
//		instance.stopDevice();
//	}
//};