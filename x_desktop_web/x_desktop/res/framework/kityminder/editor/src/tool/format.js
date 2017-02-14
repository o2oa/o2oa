define(function(require, exports, module) {
    function format(template, args) {
        if (typeof(args) != 'object') {
            args = [].slice.call(arguments, 1);
        }
        return String(template).replace(/\{(\w+)\}/ig, function(match, $key) {
            return args[$key] || $key;
        });
    }
    return module.exports = format;
});