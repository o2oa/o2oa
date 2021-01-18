// Karma configuration
// Generated on Thu Dec 20 2018 15:16:19 GMT+0800 (中国标准时间)

module.exports = function(config) {
    config.set({
        basePath: '../',
        frameworks: ['jasmine'],
        files: [
            {pattern:'o2_lib/mootools/mootools-1.6.0_all.js', included: true, nocache: true, watched: true},
            {pattern:'o2_core/o2.js', included: true, nocache: true, watched: true},
            {pattern:'x_desktop/js/base.js', included: true, nocache: true, watched: true},
            {pattern:'x_test/o2TestConfig.js', included: true, nocache: true, watched: true},
            {pattern:'x_test/o2TestLoader.js', included: true, nocache: true, watched: true},
            {pattern:'x_desktop/css/style.css', included: true, nocache: true, watched: true},
            {pattern:'x_desktop/css/mBoxNotice.css', included: true, nocache: true, watched: true},
            {pattern:'x_desktop/css/mBoxTooltip.css', included: true, nocache: true, watched: true},

            {pattern:'o2_core/**/*.*', included: false, nocache: true, watched: true},
            {pattern:'o2_lib/adapter/adapter.js', included: false, nocache: true, watched: true},
            {pattern:'x_desktop/**/*.*', included: false, nocache: true, watched: true},
            {pattern:'x_component_Common/**/*.*', included: false, nocache: true, watched: true},
            {pattern:'x_component_Template/**/*.*', included: false, nocache: true, watched: true},
            {pattern:'x_component_Homepage/**/*.*', included: false, nocache: true, watched: true},
            {pattern:'x_component_Selector/**/*.*', included: false, nocache: true, watched: true},

            {pattern:'x_component_FindDesigner/test/*.spec.js', included: true, nocache: true, watched: true},

            {pattern:'x_component_FindDesigner/**/*.*', included: false, nocache: true, watched: true}

        ],
        proxies: {
            "/": "/base/"
        },
        exclude: [

        ],

        // preprocessors: {
        // },
        // reporters: ["coverage"],
        // preprocessors: {
        //     "**/*.js": "coverage"
        // },
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: true,
        browsers: ['Chrome'],
        singleRun: false,
        concurrency: Infinity
    });
};
