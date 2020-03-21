var gulp = require('gulp'),
    gutil = require('gulp-util'),
    fs = require("fs"),
    minimist = require('minimist'),
    targz = require('targz'),
    slog = require('single-line-log').stdout,
    dateFormat = require('dateformat'),
    progress = require('progress-stream'),
    request = require("request"),
    http = require('http');

//var downloadHost = "download.o2oa.net";
var downloadHost = "release.o2oa.net";
var protocol = "http";
var commonUrl = "/build/commons.tar.gz";

var jvmUrls = {
    "all": "/build/jvm.tar.gz",
    "linux": "/build/linux.tar.gz",
    "aix": "/build/aix.tar.gz",
    "kylinos": "/build/kylinos_phytium.tar",
    "macos": "/build/macos.tar.gz",
    "neokylin": "/build/neokylin_loongson.tar.gz",
    "raspberrypi": "/build/raspberrypi.tar.gz",
    "windows": "/build/windows.tar.gz"
};
var scripts = {
    "all": ["o2server/*.sh", "o2server/*.jar", "o2server/*.html", "o2server/*.bat", "o2server/version.o2"],
    "linux": ["o2server/*linux*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "aix": ["o2server/*aix*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "kylinos": ["o2server/*kylinos_phytium*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "macos": ["o2server/*macos*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "neokylin": ["o2server/*neokylin_loongson*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "raspberrypi": ["o2server/*raspberrypi*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "windows": ["o2server/*windows*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"]
};

var o_options = minimist(process.argv.slice(2), {//upload: local ftp or sftp
    string: ["e"]
});
var options = {};
options.ev = o_options.e || "all";
var jvmUrl = jvmUrls[options.ev];
var scriptSource = scripts[options.ev];

function ProgressBar(description, bar_length){
    this.description = description || 'Progress';
    this.length = bar_length || 50;

    this.render = function (opts){
        var percent = (opts.completed / opts.total).toFixed(4);
        var cell_num = Math.floor(percent * this.length);

        var speed = "";
        if (opts.time){
            speed = (opts.completed/1024/1024)/(opts.time/1000);
            speed = speed.toFixed(2);
            speed = speed+"M/S";
        }
        var count = "";
        if (opts.count){
            count = "["+opts.count+"/"+opts.total+"]"
        }

        var cell = '';
        for (var i=0;i<cell_num;i++) { cell += '>'; }

        var empty = '';
        for (var i=0;i<this.length-cell_num;i++) { empty += '-'; }

        var d = new Date();
        var cmdText = "["+dateFormat(d, "HH:MM:ss")+"]"+" "+this.description + ': ' + cell + empty + ' ' + (100*percent).toFixed(2) + '% '+speed+count;
        slog(cmdText);
    };
}

function downloadFile(path, filename, headcb, progresscb, cb){
    var dest = `o2server/${filename}`;

    // fs.exists(dest, function(exists) {
    //     if (exists){
    //         headcb(1);
    //         progresscb({transferred:1});
    //         cb();
    //     }else{
            let stream = fs.createWriteStream(dest);
            var options = { url:protocol+"://"+downloadHost+path };
            var fileHost = downloadHost;
            var filePath =  path;
            stream.on('finish', () => {
                //gutil.log("download", ":", gutil.colors.green(filename), " completed!");
                cb();
            });
            stream.on('error', (err) => {
                gutil.log(gutil.colors.red("download error"), ":", gutil.colors.red(filename), err);
            });

            var req = http.request({
                host:fileHost,
                path:filePath,
                method:'HEAD'
            },function (res){
                if (res.statusCode == 200) {
                    res.setEncoding(null);
                    var time = 0;
                    var l = res.headers['content-length'];
                    var str = progress({
                        length: l,
                        time: 100 /* ms */
                    });
                    headcb(l);

                    str.on('progress', function(progress) {
                        if (pb){
                            progresscb(progress);
                            pb.render({ completed: currentLength, total: totalLength, time: time+=100 });
                        }

                    });
                    request.get(options).pipe(str).pipe(stream);
                } else {
                    gutil.log(gutil.colors.red("download error"), ":", gutil.colors.red(filename), "statusCode:"+response.statusCode);
                }
            })
            req.on('error', (e) => {
                gutil.log(gutil.colors.red("download error"), ":", gutil.colors.red(filename), e);
            });
            req.end();
    //    }
    //});
}

var commonsLength = 0;
var jvmLenght = 0;
var totalLength = 0;
var currentLength = 0;
var commonsCurrentLength = 0;
var jvmCurrentLength = 0;

var pb = null;
function initProgress(){
    if (commonsLength && jvmLenght){
        totalLength = +commonsLength + jvmLenght;
        var t = (totalLength/1024/1024).toFixed(2);
        pb = new ProgressBar('total: '+t+"M", 50);

    }
}

function download_commons_and_jvm(cb){
    gutil.log(gutil.colors.green("begin download commons and jvm"));
    var downloader = new Promise((resolve, reject) => {
        var commonLoaded = false;
        var jvmLoaded = false;
        downloadFile(commonUrl, "commons.tar.gz", (length)=>{
            commonsLength = +length;
            initProgress();
        }, (progress)=>{
            commonsCurrentLength = progress.transferred;
            currentLength = +commonsCurrentLength+jvmCurrentLength;
        }, ()=>{
            commonLoaded = true;
            if (jvmLoaded && commonLoaded) resolve();
        });
        // var jvmName = jvmUrl.substr(jvmUrl.lastIndexOf("/"+1, jvmUrl.length));
        // console.log(jvmName);
        // console.log(jvmUrl);
        downloadFile(jvmUrl, "jvm.tar.gz", (length)=>{
            jvmLenght = +length;
            initProgress();
        }, (progress)=>{
            jvmCurrentLength = progress.transferred;
            currentLength = +commonsCurrentLength+jvmCurrentLength;
        }, ()=>{
            jvmLoaded = true;
            if (jvmLoaded && commonLoaded) resolve();
        });
    });
    downloader.then(()=>{
        console.log();
        gutil.log(gutil.colors.green("download commons and jvm completed"));
        cb();
    });
}

function decompress_commons_and_jvm(cb){
    gutil.log(gutil.colors.green("begin decompress commons and jvm"));
    var count =0;
    var decompressor = new Promise((resolve, reject) => {
        var commonUnziped = false;
        var jvmUnziped = false;
        targz.decompress({
            src: 'o2server/commons.tar.gz',
            dest: 'o2server',
            tar: {map: function(header){
                count++;
                    var d = new Date();
                    slog("["+dateFormat(d, "HH:MM:ss")+"] " + count +" "+ header.name+" ...");
                //gutil.log(gutil.colors.cyan(header.name), gutil.colors.yellow("..."));
            }}
        }, function(err){
            if(err) {
                gutil.log(gutil.colors.red("decompress error"), ":", gutil.colors.red("common.tar.gz "), err);
            } else {
                commonUnziped = true;
                if (jvmUnziped && commonUnziped) resolve();
            }
        });
        targz.decompress({
            src: 'o2server/jvm.tar.gz',
            dest: 'o2server',
            tar: {map: function(header){
                    count++;
                    var d = new Date();
                    slog("["+dateFormat(d, "HH:MM:ss")+"] " + count +" "+ header.name+" ...");
                    //slog(count +" "+ header.name+" ...");
                    //gutil.log(gutil.colors.cyan(header.name), gutil.colors.yellow("..."));
                }}
        }, function(err){
            if(err) {
                gutil.log(gutil.colors.red("decompress error"), ":", gutil.colors.red("jvm.tar.gz "), err);
            } else {
                jvmUnziped = true;
                if (jvmUnziped && commonUnziped) resolve();
            }
        });
    });
    decompressor.then(()=>{
        gutil.log(gutil.colors.green("decompress commons and jvm completed. " + count+" files"));
        cb();
    });
}

function getFileCount(p){
    var fileCount = 0;
    function readFile(path,filesList, ){
        files = fs.readdirSync(path);
        files.forEach(walk);
        function walk(file){
            states = fs.statSync(path+'/'+file);
            if(states.isDirectory()){
                readFile(path+'/'+file,filesList);
            }else{
                // fileCount+=states.size;
                fileCount++;
            }
        }
    }
    var filesList = [];
    readFile(p, filesList);
    return fileCount;
}


function deploy_web(){
    var path = "o2server/servers/"
    var fileCount = getFileCount(path);

    //console.log(fileCount);
    var pb = new ProgressBar('', 50);
    var progressStream = progress({
        length: fileCount,
        time: 100,
        objectMode: true
    });
    progressStream.on('progress', function (stats) {
        var n = (fileCount*stats.percentage/100).toFixed(0);
        if (n>fileCount) n = fileCount;
        pb.render({ completed: n, total: fileCount, count: n});
    });

    var source = "o2server/servers/**/*";
    var dest = "target/o2server/servers/"
    return gulp.src(source)
        .pipe(progressStream)
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}
function deploy_server_store(){
    var path = "o2server/store/"
    var fileCount = getFileCount(path);

    var pb = new ProgressBar('total: '+fileCount, 50);
    var progressStream = progress({
        length: fileCount,
        time: 100,
        objectMode: true
    });
    progressStream.on('progress', function (stats) {
        var n = (fileCount*stats.percentage/100).toFixed(0);
        if (n>fileCount) n = fileCount;
        pb.render({ completed: n, total: fileCount, count: n});
    });

    var source = "o2server/store/**/*";
    var dest = "target/o2server/store/";
    return gulp.src(source)
        .pipe(progressStream)
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}
function deploy_server_commons(){
    var path = "o2server/commons/";
    var fileCount = getFileCount(path);

    var pb = new ProgressBar('total: '+fileCount, 50);
    var progressStream = progress({
        length: fileCount,
        time: 100,
        objectMode: true
    });
    progressStream.on('progress', function (stats) {
        var n = (fileCount*stats.percentage/100).toFixed(0);
        if (n>fileCount) n = fileCount;
        pb.render({ completed: n, total: fileCount, count: n});
    });

    var source = "o2server/commons/**/*";
    var dest = "target/o2server/commons/";
    return gulp.src(source)
        .pipe(progressStream)
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}
function deploy_server_jvm(){
    var path = "o2server/jvm/";
    var fileCount = getFileCount(path);

    var pb = new ProgressBar('total: '+fileCount, 50);
    var progressStream = progress({
        length: fileCount,
        time: 100,
        objectMode: true
    });
    progressStream.on('progress', function (stats) {
        var n = (fileCount*stats.percentage/100).toFixed(0);
        if (n>fileCount) n = fileCount;
        pb.render({ completed: n, total: fileCount, count: n});
    });

    var source = "o2server/jvm/**/*";
    var dest = "target/o2server/jvm/";
    return gulp.src(source)
        .pipe(progressStream)
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}
function deploy_server_config(){
    var path = "o2server/configSample/";
    var fileCount = getFileCount(path);

    var pb = new ProgressBar('total: '+fileCount, 50);
    var progressStream = progress({
        length: fileCount,
        time: 100,
        objectMode: true
    });
    progressStream.on('progress', function (stats) {
        var n = (fileCount*stats.percentage/100).toFixed(0);
        if (n>fileCount) n = fileCount;
        pb.render({ completed: n, total: fileCount, count: n});
    });

    var source = "o2server/configSample/**/*";
    var dest = "target/o2server/configSample/";
    return gulp.src(source)
        .pipe(progressStream)
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}
function deploy_server_local(){
    var path = "o2server/localSample/";
    var fileCount = getFileCount(path);

    var pb = new ProgressBar('total: '+fileCount, 50);
    var progressStream = progress({
        length: fileCount,
        time: 100,
        objectMode: true
    });
    progressStream.on('progress', function (stats) {
        var n = (fileCount*stats.percentage/100).toFixed(0);
        if (n>fileCount) n = fileCount;
        pb.render({ completed: n, total: fileCount, count: n});
    });

    var source = "o2server/localSample/**/*";
    var dest = "target/o2server/localSample/";
    return gulp.src(source)
        .pipe(progressStream)
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}
function deploy_server_script(){
    var dest = "target/o2server/";
    return gulp.src(scriptSource)
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}


exports.preperation = gulp.series(download_commons_and_jvm, decompress_commons_and_jvm);
//exports.deploy_web = deploy_web;
exports.deploy_server_store = deploy_server_store;
exports.deploy_server_commons = deploy_server_commons;
exports.deploy_server_jvm = deploy_server_jvm;
exports.deploy_server_config = deploy_server_config;
exports.deploy_server_local = deploy_server_local;
exports.deploy_server_script = deploy_server_script;
exports.deploy_server = gulp.series(deploy_server_store, deploy_server_commons, deploy_server_jvm, deploy_server_config, deploy_server_local, deploy_server_script);
exports.deploy = gulp.series(deploy_server_store, deploy_server_commons, deploy_server_jvm, deploy_server_config, deploy_server_local, deploy_server_script);
