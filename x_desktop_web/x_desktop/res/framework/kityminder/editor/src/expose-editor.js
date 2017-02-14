/**
 * @fileOverview
 *
 * 打包暴露
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define('expose-editor', function(require, exports, module) {
    return module.exports = kityminder.Editor = require('./editor');
});