(function(){
	MWF.widget.SimpleEditor = MWF.widget.SimpleEditor || {};
	MWF.widget.SimpleEditor.Actions = MWF.widget.SimpleEditor.Actions || {};
	MWF.widget.SimpleEditor.Actions.setting = MWF.widget.SimpleEditor.Actions.setting || {};

	MWF.widget.SimpleEditor.Actions.setting.emotion = {
		imagesPath: MWF.defaultPath+"/widget/$SimpleEditor/default/img/emotion/",
		imageName: MWF.LP.widget.SimpleEditor.Emotions.split(","),
		fileExt: '.gif',
		cols: 6
	};
	var emotion = MWF.widget.SimpleEditor.Actions.setting.emotion;
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