angular.module('kityminderEditor')
	.service('config',  function() {

		return {
			_default: {

                // 右侧面板最小宽度
				ctrlPanelMin: 250,

                // 右侧面板宽度
				ctrlPanelWidth: parseInt(window.localStorage.__dev_minder_ctrlPanelWidth) || 250,

				// 分割线宽度
                dividerWidth: 3,

                // 默认语言
				defaultLang: 'zh-cn',

                // 放大缩小比例
                zoom: [10, 20, 30, 50, 80, 100, 120, 150, 200]
			},
			getConfig: function(key) {
				return key == undefined ? this._default : (this._default[key] || null);
			},
			setConfig: function(obj) {
				this._default = obj;
			}
		}
	});