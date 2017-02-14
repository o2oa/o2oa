angular.module('kityminderEditor')
    .filter('commandState', function() {
        return function(minder, command) {
            return minder.queryCommandState(command);
        }
    })
    .filter('commandValue', function() {
        return function(minder, command) {
            return minder.queryCommandValue(command);
        }
    });

