const config = require('@o2oa/vue-cli-plugin-o2component/config');
config.devServer.proxy = Object.assign(config.devServer.proxy, {
    //my proxy here
    '/tree': {
        target: 'http://10.34.0.201:10073',
        changeOrigin: true,
        pathRewrite: {
          '^/tree': ''
        }
      }
})
module.exports = Object.assign(config, {
    //my config here,
})