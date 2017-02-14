angular.module('kityminderEditor')
    .service('minder.service',  function() {

        var callbackQueue = [];

        function registerEvent(callback) {
            callbackQueue.push(callback);
        }

        function executeCallback() {
            callbackQueue.forEach(function(ele) {
                ele.apply(this, arguments);
            })
        }

        return {
            registerEvent: registerEvent,
            executeCallback: executeCallback
        }
    });