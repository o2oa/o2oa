define(function(require, exports, module) {
    var kity = require('../core/kity');
    var data = require('../core/data');
    var Promise = require('../core/promise');

    var DomURL = window.URL || window.webkitURL || window;

    function loadImage(info, callback) {
        return new Promise(function(resolve, reject) {
            var image = document.createElement("img");
            image.onload = function() {
                resolve({
                    element: this,
                    x: info.x,
                    y: info.y,
                    width: info.width,
                    height: info.height
                });
            };
            image.onerror = function(err) {
                reject(err);
            };
            //image.setAttribute('crossOrigin', 'anonymous');
            image.crossOrigin = '';
            image.src = info.url;
        });
    }
    function getSVGInfo(minder) {
        var paper = minder.getPaper(),
            paperTransform,
            domContainer = paper.container,
            svgXml,
            svgContainer,
            svgDom,

            renderContainer = minder.getRenderContainer(),
            renderBox = renderContainer.getRenderBox(),
            width = renderBox.width + 1,
            height = renderBox.height + 1,

            blob, svgUrl, img;

        // 保存原始变换，并且移动到合适的位置
        paperTransform = paper.shapeNode.getAttribute('transform');
        paper.shapeNode.setAttribute('transform', 'translate(0.5, 0.5)');
        renderContainer.translate(-renderBox.x, -renderBox.y);

        // 获取当前的 XML 代码
        svgXml = paper.container.innerHTML;

        // 回复原始变换及位置
        renderContainer.translate(renderBox.x, renderBox.y);
        paper.shapeNode.setAttribute('transform', paperTransform);

        // 过滤内容
        svgContainer = document.createElement('div');
        svgContainer.innerHTML = svgXml;
        svgDom = svgContainer.querySelector('svg');
        svgDom.setAttribute('width', renderBox.width + 1);
        svgDom.setAttribute('height', renderBox.height + 1);
        svgDom.setAttribute('style', 'font-family: Arial, "Microsoft Yahei","Heiti SC";');

        svgContainer = document.createElement('div');
        svgContainer.appendChild(svgDom);

        svgXml = svgContainer.innerHTML;

        // Dummy IE
        svgXml = svgXml.replace(' xmlns="http://www.w3.org/2000/svg" ' +
            'xmlns:NS1="" NS1:ns1:xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:NS2="" NS2:xmlns:ns1=""', '');

        // svg 含有 &nbsp; 符号导出报错 Entity 'nbsp' not defined
        svgXml = svgXml.replace(/&nbsp;/g, '&#xa0;');

        blob = new Blob([svgXml], {
            type: 'image/svg+xml'
        });

        svgUrl = DomURL.createObjectURL(blob);

        //svgUrl = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svgXml);

        var allNodes = minder.getAllNode();
        var imagesInfo = [];

        for(var i = 0; i < allNodes.length; i++) {
            var nodeData = allNodes[i].data;

            if (nodeData.image) {
                /*
                * 导出之前渲染这个节点，否则取出的 contentBox 不对
                * by zhangbobell
                * */
                minder.renderNode(allNodes[i]);
                var imageUrl = nodeData.image;
                var imageSize = nodeData.imageSize;

                var imageRenderBox = allNodes[i].getRenderBox('ImageRenderer', minder.getRenderContainer());

                var imageInfo = {
                    url: imageUrl,
                    width: imageSize.width,
                    height: imageSize.height,
                    x: -renderContainer.getBoundaryBox().x + imageRenderBox.x + 20,
                    y: -renderContainer.getBoundaryBox().y + imageRenderBox.y + 20
                };

                imagesInfo.push(imageInfo);
            }
        }

        return {
            width: width,
            height: height,
            dataUrl: svgUrl,
            xml: svgXml,
            imagesInfo: imagesInfo
        };
    }


    function encode(json, minder) {

        var resultCallback;

        var Promise = kityminder.Promise;

        /* 绘制 PNG 的画布及上下文 */
        var canvas = document.createElement('canvas');
        var ctx = canvas.getContext('2d');

        /* 尝试获取背景图片 URL 或背景颜色 */
        var bgDeclare = minder.getStyle('background').toString();
        var bgUrl = /url\(\"(.+)\"\)/.exec(bgDeclare);
        var bgColor = kity.Color.parse(bgDeclare);

        /* 获取 SVG 文件内容 */
        var svgInfo = getSVGInfo(minder);
        var width = svgInfo.width;
        var height = svgInfo.height;
        var svgDataUrl = svgInfo.dataUrl;
        var imagesInfo = svgInfo.imagesInfo;

        /* 画布的填充大小 */
        var padding = 20;

        canvas.width = width + padding * 2;
        canvas.height = height + padding * 2;

        function fillBackground(ctx, style) {
            ctx.save();
            ctx.fillStyle = style;
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.restore();
        }

        function drawImage(ctx, image, x, y, width, height) {
            if (width && height) {
                ctx.drawImage(image, x, y, width, height);
            } else {
                ctx.drawImage(image, x, y);
            }
        }

        function generateDataUrl(canvas) {
            return canvas.toDataURL('image/png');
        }

        // 加载节点上的图片
        function loadImages(imagesInfo) {
            var imagePromises = imagesInfo.map(function(imageInfo) {
                return loadImage(imageInfo);
            });

            return Promise.all(imagePromises);
        }

        function drawSVG() {
            var svgData = {url: svgDataUrl};

            return loadImage(svgData).then(function($image) {
                drawImage(ctx, $image.element, padding, padding);
                return loadImages(imagesInfo);
            }).then(function($images) {
                for(var i = 0; i < $images.length; i++) {
                    drawImage(ctx, $images[i].element, $images[i].x, $images[i].y, $images[i].width, $images[i].height);
                }

                DomURL.revokeObjectURL(svgDataUrl);
                document.body.appendChild(canvas);
                return generateDataUrl(canvas);
            }, function(err) {
                // 这里处理 reject，出错基本上是因为跨域，
                // 出错后依然导出，只不过没有图片。
                alert('脑图的节点中包含跨域图片，导出的 png 中节点图片不显示，你可以替换掉这些跨域的图片并重试。');
                DomURL.revokeObjectURL(svgDataUrl);
                document.body.appendChild(canvas);
                return generateDataUrl(canvas);
            });
        }

        if (bgUrl) {
            var bgInfo = {url: bgUrl[1]};
            return loadImage(bgInfo).then(function($image) {
                fillBackground(ctx, ctx.createPattern($image.element, "repeat"));
                return drawSVG();
            });
        } else {
            fillBackground(ctx, bgColor.toString());
            return drawSVG();
        }
    }
    data.registerProtocol("png", module.exports = {
        fileDescription: "PNG 图片",
        fileExtension: ".png",
        mineType: "image/png",
        dataType: "base64",
        encode: encode
    });
});