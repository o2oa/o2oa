import {Notice} from "@o2oa/ui";

/**消息提醒框
 * @Object notice
 */
const notice = {
    /**显示错误消息
     * @method error
     * @param {String} title 错误标题
     * @param {String} text  错误描述
     * @param {Object} options  消息框显示参数
     * @param {boolean} normal 是否使用默认样式
     */
    error: (title, text, options= {}, normal)=>{
        const normalStyle =  {skin: 'default', location: 'topRight', marginTop: 10, duration: 5000};
        if (normal) options = Object.assign(options, normalStyle);
        const opts = Object.assign({
            title, text, duration: 0, skin: 'banner', marginTop: 0, type: 'error'
        }, options);
        new Notice(opts);

        const e = Error(text, {cause: opts.err});
        e.name = title;
        throw e;
    },

    /**显示消息
     * @method msg
     * @param {String} title 消息标题
     * @param {String} text  消息描述
     * @param {String} type  消息类型 error success info warn
     * @param {Object} options 消息框显示参数
     */
    msg: (title, text, type, options = {})=>{
        const opts = Object.assign({  title, text, duration: 5000, type, location: 'topRight', marginTop: 10}, options);
        new Notice(opts);
    },

    /**显示成功消息
     * @method msg
     * @param {String} title 消息标题
     * @param {String} text  消息描述
     * @param {Object} options 消息框显示参数
     */
    success:  (title, text, options= {} )=>{
        notice.msg(title, text, 'success', options);
    },

    /**显示普通消息
     * @method msg
     * @param {String} title 消息标题
     * @param {String} text  消息描述
     * @param {Object} options 消息框显示参数
     */
    info:  (title, text, options = {})=>{
        notice.msg(title, text, 'info', options);
    },

    /**显示警告消息
     * @method msg
     * @param {String} title 消息标题
     * @param {String} text  消息描述
     * @param {Object} options 消息框显示参数
     */
    warn:  (title, text, options = {})=>{
        notice.msg(title, text, 'warn', options);
    }
}
export {notice};
