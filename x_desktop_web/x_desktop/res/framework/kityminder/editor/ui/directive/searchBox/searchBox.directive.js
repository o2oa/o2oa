angular.module('kityminderEditor')
    .directive('searchBox', function() {
        return {
            restrict: 'A',
            templateUrl: 'ui/directive/searchBox/searchBox.html',
            scope: {
                minder: '='
            },
            replace: true,
            controller: function ($scope) {
                var minder = $scope.minder;
                var editor = window.editor;
                $scope.handleKeyDown = handleKeyDown;
                $scope.doSearch = doSearch;
                $scope.exitSearch = exitSearch;
                $scope.showTip = false;
                $scope.showSearch = false;

                // 处理输入框按键事件
                function handleKeyDown(e) {
                    if (e.keyCode == 13) {
                        var direction = e.shiftKey ? 'prev' : 'next';
                        doSearch($scope.keyword, direction);
                    }
                    if (e.keyCode == 27) {
                        exitSearch();
                    }
                }

                function exitSearch() {
                    $('#search-input').blur();
                    $scope.showSearch = false;
                    minder.fire('hidenoterequest');
                    editor.receiver.selectAll();
                }

                function enterSearch() {
                    $scope.showSearch = true;
                    setTimeout(function() {
                        $('#search-input').focus();
                    }, 10);

                    if ($scope.keyword) {
                        $('#search-input')[0].setSelectionRange(0, $scope.keyword.length);
                    }
                }

                $('body').on('keydown', function(e) {
                    if (e.keyCode == 70 && (e.ctrlKey || e.metaKey) && !e.shiftKey) {
                        enterSearch();

                        $scope.$apply();
                        e.preventDefault();
                    }
                });

                minder.on('searchNode', function() {
                    enterSearch();
                });


                var nodeSequence = [];
                var searchSequence = [];


                minder.on('contentchange', makeNodeSequence);

                makeNodeSequence();


                function makeNodeSequence() {
                    nodeSequence = [];
                    minder.getRoot().traverse(function(node) {
                        nodeSequence.push(node);
                    });
                }

                function makeSearchSequence(keyword) {
                    searchSequence = [];

                    for (var i = 0; i < nodeSequence.length; i++) {
                        var node = nodeSequence[i];
                        var text = node.getText().toLowerCase();
                        if (text.indexOf(keyword) != -1) {
                            searchSequence.push({node:node});
                        }
                        var note = node.getData('note');
                        if (note && note.toLowerCase().indexOf(keyword) != -1) {
                            searchSequence.push({node: node, keyword: keyword});
                        }
                    }
                }


                function doSearch(keyword, direction) {
                    $scope.showTip = false;
                    minder.fire('hidenoterequest');

                    if (!keyword || !/\S/.exec(keyword)) {
                        $('#search-input').focus();
                        return;
                    }

                    // 当搜索不到节点时候默认的选项
                    $scope.showTip = true;
                    $scope.curIndex = 0;
                    $scope.resultNum = 0;


                    keyword = keyword.toLowerCase();
                    var newSearch = doSearch.lastKeyword != keyword;

                    doSearch.lastKeyword = keyword;

                    if (newSearch) {
                        makeSearchSequence(keyword);
                    }

                    $scope.resultNum = searchSequence.length;

                    if (searchSequence.length) {
                        var curIndex = newSearch ? 0 : (direction === 'next' ? doSearch.lastIndex + 1 : doSearch.lastIndex - 1) || 0;
                        curIndex = (searchSequence.length + curIndex) % searchSequence.length;

                        setSearchResult(searchSequence[curIndex].node, searchSequence[curIndex].keyword);

                        doSearch.lastIndex = curIndex;

                        $scope.curIndex = curIndex + 1;

                        function setSearchResult(node, previewKeyword) {
                            minder.execCommand('camera', node, 50);
                            setTimeout(function () {
                                minder.select(node, true);
                                if (!node.isExpanded()) minder.execCommand('expand', true);
                                if (previewKeyword) {
                                    minder.fire('shownoterequest', {node: node, keyword: previewKeyword});
                                }
                            }, 60);
                        }
                    }
                }


            }
        }
    });