define(function(require, exports, module){

    function PriorityRuntime() {
        var minder = this.minder;
        var hotbox = this.hotbox;

        var main = hotbox.state('main');

        main.button({
            position: 'top',
            label: '优先级',
            key: 'P',
            next: 'priority',
            enable: function() {
                return minder.queryCommandState('priority') != -1;
            }
        });

        var priority = hotbox.state('priority');
        '123456789'.replace(/./g, function(p) {
            priority.button({
                position: 'ring',
                label: 'P' + p,
                key: p,
                action: function() {
                    minder.execCommand('Priority', p);
                }
            });
        });

        priority.button({
            position: 'center',
            label: '移除',
            key: 'Del',
            action: function() {
                minder.execCommand('Priority', 0);
            }
        });

        priority.button({
            position: 'top',
            label: '返回',
            key: 'esc',
            next: 'back'
        });

    }

    return module.exports = PriorityRuntime;

});