

 function staticData() {

    return {
        defaultAppHomePageList: [
            {
                "id": "im",
                "isNative": true,
                "nativeKey": "im",
                "name": "消息",
                "iconClass": "ooicon-message"
            },
            {
                "id": "contact",
                "isNative": true,
                "nativeKey": "contact",
                "name": "通讯录",
                "iconClass": "ooicon-contacts"
            },
            {
                "id": "home",
                "isMain": true,
                "isNative": true,
                "nativeKey": "home",
                "name": "首页",
                "iconClass": "ooicon-home"
            },
            {
                "id": "app",
                "isNative": true,
                "nativeKey": "app",
                "name": "应用",
                "iconClass": "ooicon-app-center"
            },
            {
                "id": "settings",
                "isNative": true,
                "nativeKey": "settings",
                "name": "设置",
                "iconClass": "ooicon-config"
            }
        ],
        homePageUrl: new URL("../assets/app_home.png", import.meta.url).href,
        imPageUrl: new URL("../assets/app_im.png", import.meta.url).href,
        contactPageUrl: new URL("../assets/app_contact.png", import.meta.url).href,
        appPageUrl: new URL("../assets/app_application.png", import.meta.url).href,
        settingPageUrl: new URL("../assets/app_setting.png", import.meta.url).href,
    };

} 

export default staticData