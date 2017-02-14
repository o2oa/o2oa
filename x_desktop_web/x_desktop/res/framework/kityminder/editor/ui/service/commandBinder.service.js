angular.module('kityminderEditor').service('commandBinder', function() {
	return {
		bind: function(minder, command, scope) {

			minder.on('interactchange', function() {
				scope.commandDisabled = minder.queryCommandState(command) === -1;
				scope.commandValue = minder.queryCommandValue(command);
				scope.$apply();
			});
		}
	};
});