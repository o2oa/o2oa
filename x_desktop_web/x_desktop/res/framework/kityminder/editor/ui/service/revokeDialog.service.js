angular.module('kityminderEditor').service('revokeDialog', ['$modal', 'minder.service', function($modal, minderService) {

    minderService.registerEvent(function() {

        // 触发导入节点或导出节点对话框
        var minder = window.minder;
        var editor = window.editor;
        var parentFSM = editor.hotbox.getParentFSM();


        minder.on('importNodeData', function() {
            parentFSM.jump('modal', 'import-text-modal');

            var importModal = $modal.open({
                animation: true,
                templateUrl: 'ui/dialog/imExportNode/imExportNode.tpl.html',
                controller: 'imExportNode.ctrl',
                size: 'md',
                resolve: {
                    title: function() {
                        return '导入节点';
                    },
                    defaultValue: function() {
                        return '';
                    },
                    type: function() {
                        return 'import';
                    }
                }
            });

            importModal.result.then(function(result) {
                try{
                    minder.Text2Children(minder.getSelectedNode(), result);
                } catch(e) {
                    alert(e);
                }
                parentFSM.jump('normal', 'import-text-finish');
                editor.receiver.selectAll();
            }, function() {
                parentFSM.jump('normal', 'import-text-finish');
                editor.receiver.selectAll();
            });
        });

        minder.on('exportNodeData', function() {
            parentFSM.jump('modal', 'export-text-modal');

            var exportModal = $modal.open({
                animation: true,
                templateUrl: 'ui/dialog/imExportNode/imExportNode.tpl.html',
                controller: 'imExportNode.ctrl',
                size: 'md',
                resolve: {
                    title: function() {
                        return '导出节点';
                    },
                    defaultValue: function() {
                        var selectedNode = minder.getSelectedNode(),
                            Node2Text = window.kityminder.data.getRegisterProtocol('text').Node2Text;

                        return Node2Text(selectedNode);
                    },
                    type: function() {
                        return 'export';
                    }
                }
            });

            exportModal.result.then(function(result) {
                parentFSM.jump('normal', 'export-text-finish');
                editor.receiver.selectAll();
            }, function() {
                parentFSM.jump('normal', 'export-text-finish');
                editor.receiver.selectAll();
            });
        });

    });

    return {};
}]);