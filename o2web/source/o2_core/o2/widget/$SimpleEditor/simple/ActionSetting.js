(function(){
	o2.widget.SimpleEditor = o2.widget.SimpleEditor || {};
	o2.widget.SimpleEditor.Actions = o2.widget.SimpleEditor.Actions || {};
	o2.widget.SimpleEditor.Actions.setting = o2.widget.SimpleEditor.Actions.setting || {};

	o2.widget.SimpleEditor.Actions.setting.emotion = {
		imagesPath: o2.session.path+"/widget/$SimpleEditor/default/img/emotion/",
		imageName: o2.LP.widget.SimpleEditor.Emotions.split(","),
		fileExt: '.gif',
		cols: 6
	};
	var emotion = o2.widget.SimpleEditor.Actions.setting.emotion;
	emotion.rows = Math.ceil( emotion.imageName.length / emotion.cols );

	emotion.dialog = {
		fx : false,
		width : emotion.cols * 30,
		height: emotion.rows * 35,
		isMax: false,
		isClose: false,
		isResize: false,
		isMove: false,
		mark: false
	}
})();