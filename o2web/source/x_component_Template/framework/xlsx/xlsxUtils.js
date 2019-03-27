/**
 * Created by caixiangyi on 2018/12/23.
 */
/**
 * 对象合并polyfill
 * */
function zyEs6AssignPolyfill() {
    if (!Object.assign) {
        Object.defineProperty(Object, "assign", {
            enumerable: false,
            configurable: true,
            writable: true,
            value: function (target, firstSource) {
                "use strict";
                if (target === undefined || target === null)
                    throw new TypeError("Cannot convert first argument to object");
                var to = Object(target);
                for (var i = 1; i < arguments.length; i++) {
                    var nextSource = arguments[i];
                    if (nextSource === undefined || nextSource === null) continue;
                    var keysArray = Object.keys(Object(nextSource));
                    for (var nextIndex = 0, len = keysArray.length; nextIndex < len; nextIndex++) {
                        var nextKey = keysArray[nextIndex];
                        var desc = Object.getOwnPropertyDescriptor(nextSource, nextKey);
                        if (desc !== undefined && desc.enumerable) to[nextKey] = nextSource[nextKey];
                    }
                }
                return to;
            }
        });
    }
}
zyEs6AssignPolyfill();
var xlsxUtils = (function () { var t = { Binary: { fixdata: function (e) { for (var r = "", t = 0, n = 10240; t < e.byteLength / n; ++t)r += String.fromCharCode.apply(null, new Uint8Array(e.slice(t * n, t * n + n))); return r += String.fromCharCode.apply(null, new Uint8Array(e.slice(t * n))) }, s2ab: function (e) { for (var r = new ArrayBuffer(e.length), t = new Uint8Array(r), n = 0; n != e.length; ++n)t[n] = 255 & e.charCodeAt(n); return r } }, _wb: null, _rABS: !1, import: function (e, r) { this.wb = null; var n = new FileReader; n.onload = function (e) { var n = e.target.result; t._wb = t._rABS ? XLSX.read(btoa(t.Binary.fixdata(n)), { type: "base64" }) : XLSX.read(n, { type: "binary" }), "function" == typeof r && r(t._wb) }, t._rABS ? n.readAsArrayBuffer(e) : n.readAsBinaryString(e) }, getSheetByName: function (e) { return XLSX.utils.sheet_to_json(t._wb.Sheets[e]) }, getSheetByIndex: function () { var e = arguments.length > 0 && void 0 !== arguments[0] ? arguments[0] : 0; return t.getSheetByName(t._wb.SheetNames[e]) }, export: function (e, r) { var n = null; for (var o in e) { var a = t.format2Sheet(e[o]); n = t.format2WB(a, o, n) } return t.format2Blob(n, r) }, readDataHead: function (e) { var r = {}, t = Array.isArray(e) ? Object.keys(e[0]) : e, n = !0, o = !1, a = void 0; try { for (var i, f = t[Symbol.iterator](); !(n = (i = f.next()).done); n = !0) { var u = i.value; r[u] = u } } catch (e) { o = !0, a = e } finally { try { !n && f.return && f.return() } finally { if (o) throw a } } return r }, format2Sheet: function (e, r, n, o) { o = o || Object.keys(e[0]), r = r || 0, n = n || 0; var a = {}; return e.map(function (e, a) { return o.map(function (o, i) { return Object.assign({}, { v: e[o], position: (i + r > 25 ? t.getCharCol(i + r) : String.fromCharCode(65 + (i + r))) + (a + 1 + n) }) }) }).reduce(function (e, r) { return e.concat(r) }).forEach(function (e, r) { return a[e.position] = { v: e.v } }), a }, format2WB: function (e, r, t, n) { r = r || "mySheet"; var o = Object.keys(e); return t || (t = { Sheets: {}, SheetNames: [] }), t.SheetNames.push(r), t.Sheets[r] = Object.assign({}, e, { "!ref": n || o[0] + ":" + o[o.length - 1] }), t }, format2Blob: function (e, r) { return new Blob([t.Binary.s2ab(XLSX.write(e, { bookType: void 0 == r ? "xlsx" : r, bookSST: !1, type: "binary" }))], { type: "" }) }, getCharCol: function (e) { for (var r = "", t = 0; e > 0;)t = e % 26 + 1, r = String.fromCharCode(t + 64) + r, e = (e - t) / 26; return r } }; return t })();