module.exports = {
    // "dev": {
    //     'location': 'E:/o2server/servers/webServer/',
    //     'host': 'dev.o2oa.net',
    //     'user': 'xadmin',
    //     'pass': 'o2No.one',
    //     "port": 21,
    //     "remotePath": "/",
    //     "dest": "dest"
    // },
    "dev": {
        'location': 'E:/o2server/servers/webServer/',
        'host': 'develop.o2oa.net',
        'user': 'root',
        'pass': 'zone2019',
        "port": 22132,
        "remotePath": "/data/jenkins/workspace/develop/target/o2server/servers/webServer/",
        "dest": "dest"
    },
    "release": {
        'host': 'release.o2oa.net',
        'user': 'o2web',
        'pass': 'o2No.one',
        "port": 21,
        "remotePath": "/",
        "dest": "D:/O2/github/o2oa/o2oa/o2web/source"
    },
    "wrdp": {}
};
