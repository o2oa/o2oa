if (!window.FormData || !window.WebSocket || !window.JSON){
    var loadingNode = document.getElementById("browser_loading");
    if (loadingNode) loadingNode.style.display = "none";

    var errorHtml = "<div id=\"browser_error\" style=\"overflow: hidden; position: absolute; top: 0px; left: 0px; width: 100%; height: 100%;background: #f1f1f1\">\n" +
        "        <div style=\"height:540px; width: 920px; overflow: hidden;  margin: 8% auto;\">\n" +
        "            <div id=\"browser_error_area\" style=\"height:540px; width: 920px;\"></div>\n" +
        "            <div style=\"height:540px; width: 920px; position: relative; top: -540px\">\n" +
        "                <div style=\"background-image: url(img/warn1.png); background-repeat: no-repeat; background-position:center center; width: 90px; height: 80px; float:left; margin-left: 144px; margin-top: 74px\"></div>\n" +
        "                <div style=\"color: #333333; width: 530px; height: 80px; float:left; margin-left: 30px; margin-top: 74px; font-size: 28px; line-height: 40px;\" id=\"browser_error_area_text\">\n" +
        "                    您的浏览器版本过低啦！~系统已经不支持IE9及以下版本了!\n" +
        "                </div>\n" +
        "                <div style=\"color: #666666; width: 650px; text-align:center; height: 50px; float:left; margin-left: 144px; margin-top: 60px; font-size: 28px; line-height: 40px; border-bottom: 2px solid #999999\" id=\"browser_error_area_up_text\">\n" +
        "                    请升级您的浏览器：\n" +
        "                </div>\n" +
        "                <div style=\"width: 100px; height: 100px; float:left; margin-left: 144px; margin-top: 20px\">\n" +
        "                    <div style=\"background-position: center; background-repeat: no-repeat; background-image: url(img/logo_edge.png); width: 100px; height: 100px;\"></div>\n" +
        "                    <div style=\"width: 100px; height: 40px; font-size: 24px; color: #666666; line-height: 40px; text-align: center\">Edge</div>\n" +
        "                </div>\n" +
        "\n" +
        "                <div style=\"width: 100px; height: 100px; float:left; margin-left: 80px; margin-top: 20px\">\n" +
        "                    <div style=\"background-position: center; background-repeat: no-repeat;background-image: url(img/logo_chrome.png); width: 100px; height: 100px;\"></div>\n" +
        "                    <div style=\"width: 100px; height: 40px; font-size: 24px; color: #666666; line-height: 40px; text-align: center\">Chrome</div>\n" +
        "                </div>\n" +
        "\n" +
        "                <div style=\"width: 100px; height: 100px; float:left; margin-left: 80px; margin-top: 20px\">\n" +
        "                    <div style=\"background-position: center; background-repeat: no-repeat;background-image: url(img/logo_firefox.png); width: 100px; height: 100px;\"></div>\n" +
        "                    <div style=\"width: 100px; height: 40px; font-size: 24px; color: #666666; line-height: 40px; text-align: center\">Firefox</div>\n" +
        "                </div>\n" +
        "\n" +
        "                <div style=\"width: 100px; height: 100px; float:left; margin-left: 80px; margin-top: 20px\">\n" +
        "                    <div style=\"background-position: center; background-repeat: no-repeat;background-image: url(img/logo_safari.png); width: 100px; height: 100px;\"></div>\n" +
        "                    <div style=\"width: 100px; height: 40px; font-size: 24px; color: #666666; line-height: 40px; text-align: center\">Safari</div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>";
    document.body.insertAdjacentHTML("beforeend", errorHtml);

    window.layout = {};
    layout.desktop = {}
    layout.addReady = function(){};
    layout.isReady = true;
}


