define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Minder = require('./minder');
    var MinderNode = require('./node');
    var MinderEvent = require('./event');

    var COMMAND_STATE_NORMAL = 0;
    var COMMAND_STATE_DISABLED = -1;
    var COMMAND_STATE_ACTIVED = 1;

    /**
     * 表示一个命令，包含命令的查询及执行
     */
    var Command = kity.createClass('Command', {
        constructor: function() {
            this._isContentChange = true;
            this._isSelectionChange = false;
        },

        execute: function(minder, args) {
            throw new Error('Not Implement: Command.execute()');
        },

        setContentChanged: function(val) {
            this._isContentChange = !!val;
        },

        isContentChanged: function() {
            return this._isContentChange;
        },

        setSelectionChanged: function(val) {
            this._isSelectionChange = !!val;
        },

        isSelectionChanged: function() {
            return this._isContentChange;
        },

        queryState: function(km) {
            return COMMAND_STATE_NORMAL;
        },

        queryValue: function(km) {
            return 0;
        },

        isNeedUndo: function() {
            return true;
        }
    });

    Command.STATE_NORMAL = COMMAND_STATE_NORMAL;
    Command.STATE_ACTIVE = COMMAND_STATE_ACTIVED;
    Command.STATE_DISABLED = COMMAND_STATE_DISABLED;

    kity.extendClass(Minder, {
        _getCommand: function(name) {
            return this._commands[name.toLowerCase()];
        },

        _queryCommand: function(name, type, args) {
            var cmd = this._getCommand(name);
            if (cmd) {
                var queryCmd = cmd['query' + type];
                if (queryCmd)
                    return queryCmd.apply(cmd, [this].concat(args));
            }
            return 0;
        },

        /**
         * @method queryCommandState()
         * @for Minder
         * @description 查询指定命令的状态
         *
         * @grammar queryCommandName(name) => {number}
         *
         * @param {string} name 要查询的命令名称
         *
         * @return {number}
         *   -1: 命令不存在或命令当前不可用
         *    0: 命令可用
         *    1: 命令当前可用并且已经执行过
         */
        queryCommandState: function(name) {
            return this._queryCommand(name, 'State', [].slice.call(arguments, 1));
        },

        /**
         * @method queryCommandValue()
         * @for Minder
         * @description 查询指定命令当前的执行值
         *
         * @grammar queryCommandValue(name) => {any}
         *
         * @param {string} name 要查询的命令名称
         *
         * @return {any}
         *    如果命令不存在，返回 undefined
         *    不同命令具有不同返回值，具体请查看 [Command](command) 章节
         */
        queryCommandValue: function(name) {
            return this._queryCommand(name, 'Value', [].slice.call(arguments, 1));
        },

        /**
         * @method execCommand()
         * @for Minder
         * @description 执行指定的命令。
         *
         * @grammar execCommand(name, args...)
         *
         * @param {string} name 要执行的命令名称
         * @param {argument} args 要传递给命令的其它参数
         */
        execCommand: function(name) {
            if (!name) return null;

            name = name.toLowerCase();

            var cmdArgs = [].slice.call(arguments, 1),
                cmd, stoped, result, eventParams;
            var me = this;
            cmd = this._getCommand(name);

            eventParams = {
                command: cmd,
                commandName: name.toLowerCase(),
                commandArgs: cmdArgs
            };
            if (!cmd || !~this.queryCommandState(name)) {
                return false;
            }

            if (!this._hasEnterExecCommand) {
                this._hasEnterExecCommand = true;

                stoped = this._fire(new MinderEvent('beforeExecCommand', eventParams, true));

                if (!stoped) {
                    this._fire(new MinderEvent('preExecCommand', eventParams, false));

                    result = cmd.execute.apply(cmd, [me].concat(cmdArgs));

                    this._fire(new MinderEvent('execCommand', eventParams, false));

                    if (cmd.isContentChanged()) {
                        this._firePharse(new MinderEvent('contentchange'));
                    }

                    this._interactChange();
                }
                this._hasEnterExecCommand = false;
            } else {
                result = cmd.execute.apply(cmd, [me].concat(cmdArgs));

                if (!this._hasEnterExecCommand) {
                    this._interactChange();
                }
            }

            return result === undefined ? null : result;
        }
    });

    module.exports = Command;
});