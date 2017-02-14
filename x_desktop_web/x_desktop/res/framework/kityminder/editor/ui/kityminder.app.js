angular.module('kityminderEditor', [
    'ui.bootstrap',
	'ui.codemirror',
	'ui.colorpicker'
])
	.config(function($sceDelegateProvider) {
		$sceDelegateProvider.resourceUrlWhitelist([
			// Allow same origin resource loads.
			'self',
			// Allow loading from our assets domain.  Notice the difference between * and **.
			'http://agroup.baidu.com:8910/**',
            'http://cq01-fe-rdtest01.vm.baidu.com:8910/**',
            'http://agroup.baidu.com:8911/**'
		]);
	});