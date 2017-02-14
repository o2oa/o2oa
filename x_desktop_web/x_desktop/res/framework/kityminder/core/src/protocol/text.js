define(function(require, exports, module) {
    var data = require('../core/data');
    var Browser = require('../core/kity').Browser;

    /**
     * @Desc: 增加对不容浏览器下节点中文本\t匹配的处理，不同浏览器下\t无法正确匹配，导致无法使用TAB来批量导入节点
     * @Editor: Naixor
     * @Date: 2015.9.17
     */
    var LINE_ENDING = '\r',
        LINE_ENDING_SPLITER = /\r\n|\r|\n/,
        TAB_CHAR = (function(Browser) {
            if (Browser.gecko) {
                return {
                    REGEXP: new RegExp('^(\t|'+ String.fromCharCode(160,160,32,160) +')'),
                    DELETE: new RegExp('^(\t|'+ String.fromCharCode(160,160,32,160) +')+')
                }
            } else if (Browser.ie || Browser.edge) {
                // ie系列和edge比较特别，\t在div中会被直接转义成SPACE故只好使用SPACE来做处理
                return {
                    REGEXP: new RegExp('^('+ String.fromCharCode(32) +'|'+ String.fromCharCode(160) +')'),
                    DELETE: new RegExp('^('+ String.fromCharCode(32) +'|'+ String.fromCharCode(160) +')+')
                }
            } else {
                return {
                    REGEXP: /^(\t|\x20{4})/,
                    DELETE: /^(\t|\x20{4})+/
                }
            }
        })(Browser);

    function repeat(s, n) {
        var result = '';
        while (n--) result += s;
        return result;
    }

    /**
     * 对节点text中的换行符进行处理
     * @method encodeWrap
     * @param  {String}   nodeText MinderNode.data.text
     * @return {String}            \n -> '\n'; \\n -> '\\n'
     */
    function encodeWrap(nodeText) {
        if (!nodeText) {
            return '';
        }
        var textArr = [],
            WRAP_TEXT = ['\\', 'n'];
        for (var i = 0, j = 0, l = nodeText.length; i < l; i++) {
            if (nodeText[i] === '\n' || nodeText[i] === '\r') {
                textArr.push('\\n');
                j = 0;
                continue;
            }
            if (nodeText[i] === WRAP_TEXT[j]) {
                j++;
                if (j === 2) {
                    j = 0;
                    textArr.push('\\\\n');
                }
                continue;
            }
            switch (j) {
                case 0: {
                    textArr.push(nodeText[i]);
                    break;
                }
                case 1: {
                    textArr.push(nodeText[i-1], nodeText[i]);
                }
            }
            j = 0;
        }
        return textArr.join('');
    }

    /**
     * 将文本内容中的'\n'和'\\n'分别转换成\n和\\n
     * @method decodeWrap
     * @param  {[type]}   text [description]
     * @return {[type]}        [description]
     */
    function decodeWrap(text) {
        if (!text) {
            return '';
        }
        var textArr = [],
            WRAP_TEXT = ['\\', '\\', 'n'];
        for (var i = 0, j = 0, l = text.length; i < l; i++) {
            if (text[i] === WRAP_TEXT[j]) {
                j++;
                if (j === 3) {
                    j = 0;
                    textArr.push('\\n');
                }
                continue;
            }
            switch (j) {
                case 0: {
                    textArr.push(text[i]);
                    j = 0;
                    break;
                }
                case 1: {
                    if (text[i] === 'n') {
                        textArr.push('\n');
                    } else {
                        textArr.push(text[i-1], text[i]);
                    }
                    j = 0;
                    break;
                }
                case 2: {
                    textArr.push(text[i-2]);
                    if (text[i] !== '\\') {
                        j = 0;
                        textArr.push(text[i-1], text[i]);
                    }
                    break;
                }
            }
        }
        return textArr.join('');
    }

    function encode(json, level) {
        var local = '';
        level = level || 0;
        local += repeat('\t', level);
        local += encodeWrap(json.data.text) + LINE_ENDING;
        if (json.children) {
            json.children.forEach(function(child) {
                local += encode(child, level + 1);
            });
        }
        return local;
    }

    function isEmpty(line) {
        return !/\S/.test(line);
    }

    function getLevel(line) {
        var level = 0;
        while (TAB_CHAR.REGEXP.test(line)) {
            line = line.replace(TAB_CHAR.REGEXP, '');
            level++;
        }

        return level;
    }

    function getNode(line) {
        return {
            data: {
                text: decodeWrap(line.replace(TAB_CHAR.DELETE, ""))
            }
        };
    }

    function decode(local) {
        var json,
            parentMap = {},
            lines = local.split(LINE_ENDING_SPLITER),
            line, level, node;

        function addChild(parent, child) {
            var children = parent.children || (parent.children = []);
            children.push(child);
        }

        for (var i = 0; i < lines.length; i++) {
            line = lines[i];
            if (isEmpty(line)) continue;

            level = getLevel(line);
            node = getNode(line);

            if (level === 0) {
                if (json) {
                    throw new Error('Invalid local format');
                }
                json = node;
            } else {
                if (!parentMap[level - 1]) {
                    throw new Error('Invalid local format');
                }
                addChild(parentMap[level - 1], node);
            }
            parentMap[level] = node;
        }
        return json;
    }

    /**
     * @Desc: 增加一个将当前选中节点转换成text的方法
     * @Editor: Naixor
     * @Date: 2015.9.21
     */
    function Node2Text(node) {
        function exportNode(node) {
            var exported = {};
            exported.data = node.getData();
            var childNodes = node.getChildren();
            exported.children = [];
            for (var i = 0; i < childNodes.length; i++) {
                exported.children.push(exportNode(childNodes[i]));
            }
            return exported;
        }
        if (!node) return;
        if (/^\s*$/.test(node.data.text)) {
            node.data.text = "分支主题";
        }
        return encode(exportNode(node));
    }

    data.registerProtocol('text', module.exports = {
        fileDescription: '大纲文本',
        fileExtension: '.txt',
        dataType: 'text',
        mineType: 'text/plain',

        encode: function(json) {
            return encode(json.root, 0);
        },

        decode: function(local) {
            return decode(local);
        },

        Node2Text: function(node) {
            return Node2Text(node);
        }
    });
});
