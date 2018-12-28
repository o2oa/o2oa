//根据按键控制状态机的跳转
MWF.xApplication.MinderEditor.JumpingInReadMode = function( editor ) {
/**
 * @Desc: 下方使用receiver.enable()和receiver.disable()通过
 *        修改div contenteditable属性的hack来解决开启热核后依然无法屏蔽浏览器输入的bug;
 *        特别: win下FF对于此种情况必须要先blur在focus才能解决，但是由于这样做会导致用户
 *             输入法状态丢失，因此对FF暂不做处理
 * @Editor: Naixor
 * @Date: 2015.09.14
 */
    var fsm = editor.fsm;
    var receiver = editor.receiver;
    var container = editor.contentNode;

    // normal -> *
    receiver.listen('normal', function(e) {
        //console.log(  "receiver.listen('normal'" );
        // 为了防止处理进入edit模式而丢失处理的首字母,此时receiver必须为enable
        receiver.enable();

        /**
         * check
         * @editor Naixor
         * @Date 2015-12-2
         */
        switch (e.type) {
            case 'keydown': {
                // normal -> normal shortcut
                fsm.jump('normal', 'shortcut-handle', e);
                break;
            }
            case 'keyup': {
                break;
            }
            default: {}
        }
    });


    container.addEventListener('contextmenu', function(e) {
        e.preventDefault();
    });

};
