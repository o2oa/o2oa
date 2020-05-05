var gulp = require('gulp'),
    gutil = require('gulp-util'),
    del = require('del'),
    fs = require("fs"),
    minimist = require('minimist'),
    targz = require('targz'),
    slog = require('single-line-log').stdout,
    dateFormat = require('dateformat'),
    progress = require('progress-stream'),
    request = require("request"),
    uglify = require('gulp-tm-uglify'),
    rename = require('gulp-rename'),
    changed = require('gulp-changed'),
    gulpif = require('gulp-if'),
    http = require('http');
var fg = require('fast-glob');
var logger = require('gulp-logger');
var assetRev = require('gulp-tm-asset-rev');
const os = require('os');

//var downloadHost = "download.o2oa.net";
// var downloadHost = "release.o2oa.net";
// var protocol = "http";
// var commonUrl = "/build/commons.tar.gz";

// var jvmUrls = {
//     "all": "/build/jvm.tar.gz",
//     "linux": "/build/linux.tar.gz",
//     "aix": "/build/aix.tar.gz",
//     "arm": "/build/arm.tar.gz",
//     "macos": "/build/macos.tar.gz",
//     "risc": "/build/risc.tar.gz",
//     "raspberrypi": "/build/raspberrypi.tar.gz",
//     "windows": "/build/windows.tar.gz"
// };

var downloadHost = "git.o2oa.net";
var protocol = "https";
var commonUrl = "/o2oa/evn-o2server-commons/-/archive/master/evn-o2server-commons-master.tar.gz?path=commons";
var jvmUrls = {
    "all": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm",
    "linux": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/linux",
    "aix": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/aix",
    "arm": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/arm",
    "macos": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/macos",
    "risc": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/risc",
    "raspberrypi": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/raspberrypi",
    "windows": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/windows"
};

var scripts = {
    "all": ["o2server/*.sh", "o2server/*.jar", "o2server/*.html", "o2server/*.bat", "o2server/version.o2"],
    "linux": ["o2server/*linux*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "aix": ["o2server/*aix*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "arm": ["o2server/*arm*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "macos": ["o2server/*macos*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "risc": ["o2server/*risc*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
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
        for (var i=0;i<this.length-cell_num;i++) { empty += '='; }

        if (opts.completed <= opts.total){
            var d = new Date();
            var cmdText = "["+dateFormat(d, "HH:MM:ss")+"]"+" "+this.description + ': ' + cell + empty + ' ' + (100*percent).toFixed(2) + '% '+speed+count;
            slog(cmdText);
        }
    };
}

function downloadFile_progress(path, filename, headcb, progresscb, cb){
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
                    gutil.log(gutil.colors.red("download error"), ":", gutil.colors.red(filename), "statusCode:"+res.statusCode);
                }
            })
            req.on('error', (e) => {
                gutil.log(gutil.colors.red("download error"), ":", gutil.colors.red(filename), e);
            });
            req.end();
    //    }
    //});
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

    // var req = http.request({
    //     host:fileHost,
    //     path:filePath,
    //     method:'HEAD'
    // },function (res){
    //     if (res.statusCode == 200) {
    //         res.setEncoding(null);
    //         var time = 0;
    //         var l = res.headers['content-length'];
    //         var str = progress({
    //             length: l,
    //             time: 100 /* ms */
    //         });
    //         headcb(l);
    //
    //         str.on('progress', function(progress) {
    //             if (pb){
    //                 progresscb(progress);
    //                 pb.render({ completed: currentLength, total: totalLength, time: time+=100 });
    //             }
    //
    //         });
             request.get(options).pipe(stream);
    //     } else {
    //         gutil.log(gutil.colors.red("download error"), ":", gutil.colors.red(filename), "statusCode:"+res.statusCode);
    //     }
    // })
    // req.on('error', (e) => {
    //     gutil.log(gutil.colors.red("download error"), ":", gutil.colors.red(filename), e);
    // });
    // req.end();
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
    console.log(`---------------------------------------------------------------------
  . Start to download the dependencies needed for compilation ...
---------------------------------------------------------------------`);
    var downloader = new Promise((resolve, reject) => {
        var commonLoaded = false;
        var jvmLoaded = false;
        downloadFile(commonUrl, "commons_git.tar.gz", (length)=>{
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
        downloadFile(jvmUrl, "jvm_git.tar.gz", (length)=>{
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
        //console.log();
        gutil.log(gutil.colors.green("download commons and jvm completed"));
        cb();
    });
}

function decompress_commons_and_jvm(cb){
    console.log(`---------------------------------------------------------------------
  . Start to decompress the dependencies needed for compilation ...
---------------------------------------------------------------------`);
    gutil.log(gutil.colors.green("begin decompress commons and jvm"));
    var count =0;
    var decompressor = new Promise((resolve, reject) => {
        var commonUnziped = false;
        var jvmUnziped = false;
        targz.decompress({
            src: 'o2server/commons_git.tar.gz',
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
            src: 'o2server/jvm_git.tar.gz',
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
function move_commons(){
    console.log(`---------------------------------------------------------------------
  . move commons files to o2server/commons ...
---------------------------------------------------------------------`);
    return gulp.src("o2server/evn-o2server-commons-master-commons/commons/**/*")
        .pipe(gulp.dest("o2server/commons/"));
}
function move_jvm(){
    console.log(`---------------------------------------------------------------------
  . move jvm files to o2server/jvm ...
---------------------------------------------------------------------`);
    var path;
    if (options.ev=="all"){
        path = "o2server/evn-o2server-jvm-master-jvm/jvm/**/*"
    }else{
        path = "o2server/evn-o2server-jvm-master-jvm-"+options.ev+"/jvm/**/*"
    }
    return gulp.src(path)
        .pipe(gulp.dest("o2server/jvm/"));
}
function clear_commons_git(cb){
    var dest = ['o2server/evn-o2server-commons-master-commons/', 'o2server/commons_git.tar.gz'];
    del(dest, { force: true });
    cb();
}
function clear_jvm_git(cb){
    var path;
    if (options.ev=="all"){
        path = "o2server/evn-o2server-jvm-master-jvm/"
    }else{
        path = "o2server/evn-o2server-jvm-master-jvm-"+options.ev+"/"
    }
    del([path, 'o2server/jvm_git.tar.gz'], { force: true });
    cb();
}

function build_web_minimize(cb) {
    console.log(`---------------------------------------------------------------------
  . Start compiling the web ...
---------------------------------------------------------------------`);

    var dest = 'target/o2server/servers/webServer/';
    var src_min = ['o2web/source/**/*.js', '!**/*.spec.js', '!**/test/**', '!o2web/source/o2_lib/**/*'];

    var entries = fg.sync(src_min, { dot: false});
    var size = entries.length;

    var pb = new ProgressBar('', 50);
    var doCount = 0;

    var stream = gulp.src(src_min);
    stream.on("end", ()=>{console.log();});

    return stream.pipe(uglify())
        .pipe(rename({ extname: '.min.js' }))
        .pipe(gulp.dest(dest))
        .pipe(logger(function(){
            doCount++;
            if (doCount <= size){pb.render({ completed: doCount, total: size, count: doCount})};
        }))
        .pipe(gutil.noop());
}

function build_web_move() {
    var dest = 'target/o2server/servers/webServer/';
    var src_move = ['o2web/source/**/*', '!**/*.spec.js', '!**/test/**'];

    var entries = fg.sync(src_move, { dot: false});
    var size = entries.length;
    var pb = new ProgressBar('', 50);
    var doCount = 0;

    var stream = gulp.src(src_move);
    stream.on("end", ()=>{console.log();});

    return stream.pipe(gulp.dest(dest))
        .pipe(logger(function(){
            doCount++;
            if (doCount <= size) {pb.render({ completed: doCount, total: size, count: doCount})};
        }))
        .pipe(gutil.noop());
}
exports.build_web_move = build_web_move;

function build_web_v_html() {
    var src = 'o2web/source/x_desktop/*.html';
    var dest = 'target/o2server/servers/webServer/x_desktop/';
    return gulp.src(src)
        .pipe(assetRev())
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}
function build_web_v_o2() {
    var src = 'o2web/source/o2_core/o2.js';
    var dest = 'target/o2server/servers/webServer/o2_core/';
    return gulp.src(src)
        .pipe(assetRev())
        .pipe(gulp.dest(dest))
        .pipe(uglify())
        .pipe(rename({ extname: '.min.js' }))
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
}



function clear_build(cb){
    console.log(`---------------------------------------------------------------------
  . clear old build ...
---------------------------------------------------------------------`);
    var dest = 'target';
    del(dest, { force: true });
    cb();
}
function clear_deploy(cb){
    console.log(`---------------------------------------------------------------------
  . clear old deploy ...
---------------------------------------------------------------------`);
    var dest = ["target/o2server/store/", "target/o2server/commons/", "target/o2server/jvm/", "target/o2server/configSample/", "target/o2server/localSample/", "target/o2server/servers/"];
    dest = dest.concat(["target/o2server/*.sh", "target/o2server/*.jar", "target/o2server/*.html", "target/o2server/*.bat", "target/o2server/version.o2"]);
    del(dest, { force: true });
    cb();
}
exports.clear_build = clear_build;
exports.clear_deploy = clear_deploy;



function deploy_server(){
    console.log(`---------------------------------------------------------------------
  . deploy to target ...
---------------------------------------------------------------------`);
    var source = ["o2server/*store/**/*", "o2server/*commons/**/*", "o2server/*jvm/**/*", "o2server/*configSample/**/*", "o2server/*localSample/**/*"];
    source = source.concat(scriptSource);
    console.log(source)
    var dest = "target/o2server/"

    var entries = fg.sync(source, { dot: false});
    var size = entries.length;
    var pb = new ProgressBar('', 50);
    var doCount = 0;

    var stream = gulp.src(source);
    stream.on("end", ()=>{console.log();});

    return stream.pipe(gulp.dest(dest))
        .pipe(logger(function(){
            doCount++;
            if (doCount <= size) {pb.render({ completed: doCount, total: size, count: doCount})};
        }));
}

exports.preperation =  gulp.series(download_commons_and_jvm, decompress_commons_and_jvm, move_commons, move_jvm, clear_commons_git, clear_jvm_git);

var shell = require('gulp-shell')
exports.build_server = function(){
    console.log(`---------------------------------------------------------------------
  . Start compiling the server ...
---------------------------------------------------------------------`);
    return (shell.task('npm run build_server_script'))();
};
function chmod_jvm(){
    return (shell.task('chmod 777 -R target/o2server/jvm'))();
}
function chmod_commons(){
    return (shell.task('chmod 777 -R target/o2server/commons'))();
}
function chmod_sh(){
    return (shell.task('chmod 777 target/o2server/*.sh'))();
}
exports.build_web = gulp.series(build_web_minimize, build_web_move, build_web_v_html, build_web_v_o2);
if (os.platform().indexOf("win")==-1){
    exports.deploy = gulp.series(deploy_server, chmod_jvm, chmod_commons, chmod_sh);
}else{
    exports.deploy = gulp.series(deploy_server);
}