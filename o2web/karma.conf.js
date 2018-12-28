// Karma configuration
// Generated on Thu Dec 20 2018 15:16:19 GMT+0800 (中国标准时间)

module.exports = function(config) {
    config.set({
        basePath: '',
        frameworks: ['jasmine'],
        files: [
            'source/*',
            'test/**/*.js'
        ],
        exclude: [
        ],

        preprocessors: {
        },
        reporters: ['progress'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: false,
        browsers: ['PhantomJS'],
        singleRun: false,
        concurrency: Infinity
    })
};
