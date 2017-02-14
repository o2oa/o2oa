angular.module('kityminderEditor')
    .directive('selectAll', function() {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/selectAll/selectAll.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function($scope) {
                var minder = $scope.minder;

                $scope.items = ['revert', 'siblings', 'level', 'path', 'tree'];

                $scope.select = {
                    all: function() {
                        var selection = [];
                        minder.getRoot().traverse(function(node) {
                            selection.push(node);
                        });
                        minder.select(selection, true);
                        minder.fire('receiverfocus');
                    },
                    revert: function() {
                        var selected = minder.getSelectedNodes();
                        var selection = [];
                        minder.getRoot().traverse(function(node) {
                            if (selected.indexOf(node) == -1) {
                                selection.push(node);
                            }
                        });
                        minder.select(selection, true);
                        minder.fire('receiverfocus');
                    },
                    siblings: function() {
                        var selected = minder.getSelectedNodes();
                        var selection = [];
                        selected.forEach(function(node) {
                            if (!node.parent) return;
                            node.parent.children.forEach(function(sibling) {
                                if (selection.indexOf(sibling) == -1) selection.push(sibling);
                            });
                        });
                        minder.select(selection, true);
                        minder.fire('receiverfocus');
                    },
                    level: function() {
                        var selectedLevel = minder.getSelectedNodes().map(function(node) {
                            return node.getLevel();
                        });
                        var selection = [];
                        minder.getRoot().traverse(function(node) {
                            if (selectedLevel.indexOf(node.getLevel()) != -1) {
                                selection.push(node);
                            }
                        });
                        minder.select(selection, true);
                        minder.fire('receiverfocus');
                    },
                    path: function() {
                        var selected = minder.getSelectedNodes();
                        var selection = [];
                        selected.forEach(function(node) {
                            while(node && selection.indexOf(node) == -1) {
                                selection.push(node);
                                node = node.parent;
                            }
                        });
                        minder.select(selection, true);
                        minder.fire('receiverfocus');
                    },
                    tree: function() {
                        var selected = minder.getSelectedNodes();
                        var selection = [];
                        selected.forEach(function(parent) {
                            parent.traverse(function(node) {
                                if (selection.indexOf(node) == -1) selection.push(node);
                            });
                        });
                        minder.select(selection, true);
                        minder.fire('receiverfocus');
                    }
                };
            }
        }
    });