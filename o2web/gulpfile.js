var gulp = require('gulp'),
    //var deleted = require('gulp-deleted');
    del = require('del'),
    //uglify = require('gulp-tm-uglify'),
    uglify = require('gulp-uglify-es').default,
    rename = require('gulp-rename'),
    changed = require('gulp-changed'),
    gulpif = require('gulp-if'),
    minimist = require('minimist'),
    ftp = require('gulp-ftp'),
    sftp = require('gulp-sftp-up4'),
    JSFtp = require('jsftp'),
    gutil = require('gulp-util'),
    fs = require("fs");
    concat = require('gulp-concat');
//let uglify = require('gulp-uglify-es').default;
var through2 = require('through2');

var assetRev = require('gulp-tm-asset-rev');
var apps = require('./gulpapps.js');
var ftpconfig = require('./gulpconfig.js');

var o_options = minimist(process.argv.slice(2), {//upload: local ftp or sftp
    string: ["ev", "upload", "location", "host", "user", "pass", "port", "remotePath", "dest", "src"]
});
function getEvOptions(ev){
    options.ev = ev;
    return (ftpconfig[ev]) || {
        'location': '',
        'host': '',
        'user': '',
        'pass': '',
        "port": null,
        "remotePath": "",
        "dest": "dest",
        "upload": ""
    }
}
function setOptions(op1, op2){
    if (!op2) op2 = {};
    options.upload = op1.upload || op2.upload || "";
    options.location = op1.location || op2.location || "";
    options.host = op1.host || op2.host || "";
    options.user = op1.user || op2.user || "";
    options.pass = op1.pass || op2.pass || "";
    options.port = op1.port || op2.port || "";
    options.remotePath = op1.remotePath || op2.remotePath || "";
    options.dest = op1.dest || op2.dest || "dest";
}
var options = {};
setOptions(o_options, getEvOptions(o_options.ev));

var appTasks = [];

function createDefaultTask(path, isMin, thisOptions) {
    gulp.task(path, function (cb) {
        //var srcFile = 'source/' + path + '/**/*';
        var option = thisOptions || options;

        var src;
        var dest = option.dest+'/' + path + '/';

        let ev = option.ev
        var evList = Object.keys(ftpconfig).map((i)=>{ return (i==ev) ? "*"+i : i; });

        if (isMin){
            var src_min = ['source/' + path + '/**/*.js', '!**/*.spec.js', '!**/test/**'];
            var src_move = ['source/' + path + '/**/*', '!**/*.spec.js', '!**/test/**'];

            gutil.log("Move-Uglify", ":", gutil.colors.green(gutil.colors.blue(path), gutil.colors.white('->'), dest));

            return gulp.src(src_min)
                .pipe(changed(dest))
                .pipe(uglify())
                .pipe(rename({ extname: '.min.js' }))
                .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/')))
                .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                    host: option.host,
                    user: option.user || 'anonymous',
                    pass: option.pass || '@anonymous',
                    port: option.port || 21,
                    remotePath: (option.remotePath || '/') + path
                })))
                .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                    host: option.host,
                    user: option.user || 'anonymous',
                    pass: option.pass || null,
                    port: option.port || 22,
                    remotePath: (option.remotePath || '/') + path
                })))
                .pipe(gulpif((option.ev == "dev" || option.ev == "pro") ,gulp.dest(dest)))

                .pipe(gulp.src(src_move))
                .pipe(changed(dest))
                .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/')))
                .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                    host: option.host,
                    user: option.user || 'anonymous',
                    pass: option.pass || '@anonymous',
                    port: option.port || 21,
                    remotePath: (option.remotePath || '/') + path
                })))
                .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                    host: option.host,
                    user: option.user || 'anonymous',
                    pass: option.pass || null,
                    port: option.port || 22,
                    remotePath: (option.remotePath || '/') + path
                })))
                .pipe(gulp.dest(dest))
                .pipe(gutil.noop());


        }else{
            src = ['source/' + path + '/**/*', '!**/*.spec.js', '!**/test/**'];
            gutil.log("Move", ":", gutil.colors.green(gutil.colors.blue(path), gutil.colors.white('->'), dest));
            return gulp.src(src)
                .pipe(changed(dest))
                .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/')))
                .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                    host: option.host,
                    user: option.user || 'anonymous',
                    pass: option.pass || '@anonymous',
                    port: option.port || 21,
                    remotePath: (option.remotePath || '/') + path
                })))
                .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                    host: option.host,
                    user: option.user || 'anonymous',
                    pass: option.pass || null,
                    port: option.port || 22,
                    remotePath: (option.remotePath || '/') + path
                })))
                .pipe(gulp.dest(dest))
                .pipe(gutil.noop());
        }
    });
}

function createXFormConcatTask(path, isMin, thisOptions) {
    gulp.task(path+" : concat", function(){
        var option = thisOptions || options;
        var src = [
            'source/' + path + '/Form.js',
            'source/' + path + '/$Module.js',
            'source/' + path + '/$Input.js',
            'source/' + path + '/Div.js',
            'source/' + path + '/Combox.js',
            'source/' + path + '/DatagridMobile.js',
            'source/' + path + '/DatagridPC.js',
            'source/' + path + '/Textfield.js',
            'source/' + path + '/Personfield.js',
            'source/' + path + '/*.js',
            '!source/' + path + '/Office.js'
        ];
        var dest = option.dest+'/' + path + '/';
        return gulp.src(src)
            .pipe(concat('$all.js'))
            .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/')))
            .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || '@anonymous',
                port: option.port || 21,
                remotePath: (option.remotePath || '/') + path
            })))
            .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || null,
                port: option.port || 22,
                remotePath: (option.remotePath || '/') + path
            })))
            .pipe(gulp.dest(dest))
            .pipe(uglify())
            .pipe(rename({ extname: '.min.js' }))
            .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/')))
            .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || '@anonymous',
                port: option.port || 21,
                remotePath: (option.remotePath || '/') + path
            })))
            .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || null,
                port: option.port || 22,
                remotePath: (option.remotePath || '/') + path
            })))
            .pipe(gulp.dest(dest))
    });
}

function createO2ConcatTask(path, isMin, thisOptions) {
    gulp.task(path+" : concat", function(){
        var option = thisOptions || options;
        var src = [
            'source/o2_lib/mootools/mootools-1.6.0_all.js',
            'source/' + path + '/o2.js'
        ];
        var dest = option.dest+'/' + path + '/';
        return gulp.src(src)
            .pipe(concat('o2.js'))
            .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/')))
            .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || '@anonymous',
                port: option.port || 21,
                remotePath: (option.remotePath || '/') + path
            })))
            .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || null,
                port: option.port || 22,
                remotePath: (option.remotePath || '/') + path
            })))
            .pipe(gulp.dest(dest))
            .pipe(uglify())
            .pipe(rename({ extname: '.min.js' }))
            .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/')))
            .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || '@anonymous',
                port: option.port || 21,
                remotePath: (option.remotePath || '/') + path
            })))
            .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || null,
                port: option.port || 22,
                remotePath: (option.remotePath || '/') + path
            })))
            .pipe(gulp.dest(dest))
    });

    gulp.task(path+".xDesktop : concat", function(){
        var option = thisOptions || options;
        var src = [
            'source/'+path+'/o2/widget/Common.js',
            'source/'+path+'/o2/widget/Dialog.js',
            'source/'+path+'/o2/widget/UUID.js',
            'source/'+path+'/o2/xDesktop/Common.js',
            'source/'+path+'/o2/xDesktop/Actions/RestActions.js',
            'source/'+path+'/o2/xAction/RestActions.js',
            'source/'+path+'/o2/xDesktop/Access.js',
            'source/'+path+'/o2/xDesktop/Dialog.js',
            'source/'+path+'/o2/xDesktop/Menu.js',
            'source/'+path+'/o2/xDesktop/UserData.js',
            'source/x_component_Template/MPopupForm.js',
            'source/'+path+'/o2/xDesktop/Authentication.js',
            'source/'+path+'/o2/xDesktop/Dialog.js',
            'source/'+path+'/o2/xDesktop/Window.js',
            'source/x_component_Common/Main.js'
        ];
        var dest = option.dest+'/' + path + '/o2/xDesktop/';
        return gulp.src(src)
            .pipe(concat('$all.js'))
            .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/o2/xDesktop/')))
            .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || '@anonymous',
                port: option.port || 21,
                remotePath: (option.remotePath || '/') + path+"/o2/xDesktop/"
            })))
            .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || null,
                port: option.port || 22,
                remotePath: (option.remotePath || '/') + path+"/o2/xDesktop/"
            })))
            .pipe(gulp.dest(dest))
            .pipe(uglify())
            .pipe(rename({ extname: '.min.js' }))
            .pipe(gulpif((option.upload == 'local' && option.location != ''), gulp.dest(option.location + path + '/o2/xDesktop/')))
            .pipe(gulpif((option.upload == 'ftp' && option.host != ''), ftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || '@anonymous',
                port: option.port || 21,
                remotePath: (option.remotePath || '/') + path+"/o2/xDesktop/"
            })))
            .pipe(gulpif((option.upload == 'sftp' && option.host != ''), sftp({
                host: option.host,
                user: option.user || 'anonymous',
                pass: option.pass || null,
                port: option.port || 22,
                remotePath: (option.remotePath || '/') + path+"/o2/xDesktop/"
            })))
            .pipe(gulp.dest(dest))
    });
}

function getAppTask(path, isMin, thisOptions) {
    if (path==="x_component_process_Xform"){
        createDefaultTask(path, isMin, thisOptions);
        createXFormConcatTask(path, isMin, thisOptions);
        return gulp.series(path, path+" : concat");
    }else if (path==="o2_core"){
        createDefaultTask(path, isMin, thisOptions);
        createO2ConcatTask(path, isMin, thisOptions);
        return gulp.series(path, path+" : concat", path+".xDesktop : concat");
    }else{
        createDefaultTask(path, isMin, thisOptions);
        return gulp.series(path);
    }
}

//var taskObj = {};
apps.map(function (app) {
    var taskName;
    var isMin = (app.tasks.indexOf("min")!==-1);
    taskName = app.folder;
    appTasks.push(taskName);
    gulp.task(taskName, getAppTask(app.folder, isMin));

    // //var isMin = (app.tasks.indexOf("min")!==-1);
    // taskName = app.folder+"_release";
    // //appTasks.push(taskName);
    // gulp.task(taskName, getAppTask(app.folder, isMin, release_options));
});

// Object.keys(taskObj).map(function(k){
//     exports[k] = parallel(taskObj[k]);
// });

//exports[app.folder] = parallel(minTask, moveTask);

function getCleanTask(path) {
    return function (cb) {
        if (path){
            var dest = (path=="/") ? options.dest+"/" : options.dest+'/' + path + '/';
            gutil.log("Clean", ":", gutil.colors.red(dest));
            del.sync(dest, cb);
            cb();
        }else{
            cb();
        }
    }
}

function cleanRemoteFtp(f, cb) {
    var file = options.remotePath + f;

    var ftp = new JSFtp({
        host: options.host,
        user: options.user || 'anonymous',
        pass: options.pass || null,
        port: options.port || 21
    });

    ftp.raw('dele ' + file, function (err) {
        if (err) { cb(); return; }
        if (file.substring(file.length - 3).toLowerCase() == ".js") {
            file = file.replace('.js', ".min.js");
            ftp.raw('dele ' + file, function (err) {
                if (err) { cb(); return; }

                if (file.indexOf("/") != -1) {
                    var p = file.substring(0, file.lastIndexOf("/"));
                    ftp.raw('rmd ' + p, function (err) {
                        if (err) { cb(); return; }

                        ftp.raw.quit();
                        cb();
                    });
                }

            });
        } else {
            if (file.indexOf("/") != -1) {
                var pPath = file.substring(0, file.lastIndexOf("/"));
                ftp.raw('rmd ' + pPath, function (err) {
                    if (err) { cb(); return; }
                    ftp.raw.quit();
                    cb();
                });
            }
        }
    });
}
function cleanRemoteLocal(f, cb) {
    var file = options.location + f;
    del(file, { force: true, dryRun: true }, function () {
        if (file.substring(file.length - 3).toLowerCase() == ".js") {
            var minfile = file.replace('.js', ".min.js");
            del(minfile, { force: true, dryRun: true }, function () {
                var p = file.substring(0, file.lastIndexOf("/"));
                fs.rmdir(p, function (err) {
                    if (err) { }
                    cb();
                })
            });
        } else {
            var p = file.substring(0, file.lastIndexOf("/"));
            fs.rmdir(p, function (err) {
                if (err) { }
                cb();
            })
        }
    });
}

function getCleanRemoteTask(path) {
    return function (cb) {
        if (options.upload) {
            var file = path.replace(/\\/g, "/");
            file = file.substring(file.indexOf("source/") + 7);

            if (options.upload == 'local' && options.location != '') cleanRemoteLocal(file, cb);
            if (options.upload == 'ftp' && options.host != '') cleanRemoteFtp(file, cb);
        } else {
            if (cb) cb();
        }
    }
}

function getWatchTask(path) {
    return (path) ? function (cb) {
        gutil.log("watch", ":", gutil.colors.green(path, "is watching ..."));
        gulp.watch(['source/' + path + '/**/*', "!./**/test/**"], { "events": ['addDir', 'add', 'change'] }, gulp.parallel([path]));
    } : function(cb){cb();};
}

gulp.task("clean", getCleanTask(options.src))
gulp.task("watch", getWatchTask(options.src));

gulp.task("index", function () {
    var src = ['source/favicon.ico', 'source/index.html'];
    var dest = options.dest;
    return gulp.src(src)
        .pipe(changed(dest))
        .pipe(gulpif((options.upload == 'local' && options.location != ''), gulp.dest(options.location + '/')))
        .pipe(gulpif((options.upload == 'ftp' && options.host != ''), ftp({
            host: options.host,
            user: options.user || 'anonymous',
            pass: options.pass || '@anonymous',
            port: options.port || 21,
            remotePath: (options.remotePath || '/')
        })))
        .pipe(gulpif((options.upload == 'sftp' && options.host != ''), ftp({
            host: options.host,
            user: options.user || 'anonymous',
            pass: options.pass || null,
            port: options.port || 22,
            remotePath: (options.remotePath || '/')
        })))
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
});
gulp.task("cleanAll", getCleanTask('/'));

gulp.task("o2:new-v:html", function () {
    var path = "x_desktop";
    var src = 'source/x_desktop/*.html';
    var dest = options.dest + '/x_desktop/';
    return gulp.src(src)
        .pipe(assetRev())
        .pipe(gulpif((options.upload == 'local' && options.location != ''), gulp.dest(options.location + path + '/')))
        .pipe(gulpif((options.upload == 'ftp' && options.host != ''), ftp({
            host: options.host,
            user: options.user || 'anonymous',
            pass: options.pass || '@anonymous',
            port: options.port || 21,
            remotePath: (options.remotePath || '/') + path
        })))
        .pipe(gulpif((options.upload == 'sftp' && options.host != ''), sftp({
            host: options.host,
            user: options.user || 'anonymous',
            pass: options.pass || null,
            port: options.port || 22,
            remotePath: (options.remotePath || '/') + path
        })))
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());

});
gulp.task("o2:new-v:o2", function () {
    var path = "o2_core";
    var src = 'source/o2_core/o2.js';
    var dest = options.dest +'/o2_core/';
    return gulp.src(src)
        .pipe(assetRev())
        .pipe(gulpif((options.upload == 'local' && options.location != ''), gulp.dest(options.location + path + '/')))
        .pipe(gulpif((options.upload == 'ftp' && options.host != ''), ftp({
            host: options.host,
            user: options.user || 'anonymous',
            pass: options.pass || '@anonymous',
            port: options.port || 21,
            remotePath: (options.remotePath || '/') + path
        })))
        .pipe(gulpif((options.upload == 'sftp' && options.host != ''), sftp({
            host: options.host,
            user: options.user || 'anonymous',
            pass: options.pass || null,
            port: options.port || 22,
            remotePath: (options.remotePath || '/') + path
        })))
        .pipe(gulp.dest(dest))
        .pipe(uglify())
        .pipe(rename({ extname: '.min.js' }))
        .pipe(gulpif((options.upload == 'local' && options.location != ''), gulp.dest(options.location + path + '/')))
        .pipe(gulpif((options.upload == 'ftp' && options.host != ''), ftp({
            host: options.host,
            user: options.user || 'anonymous',
            pass: options.pass || '@anonymous',
            port: options.port || 21,
            remotePath: (options.remotePath || '/') + path
        })))
        .pipe(gulpif((options.upload == 'sftp' && options.host != ''), sftp({
            host: options.host,
            user: options.user || 'anonymous',
            pass: options.pass || null,
            port: options.port || 22,
            remotePath: (options.remotePath || '/') + path
        })))
        .pipe(gulp.dest(dest))
        .pipe(gutil.noop());
});
gulp.task("o2:new-v", gulp.parallel("o2:new-v:o2", "o2:new-v:html"));


gulp.task("git_clean", function (cb) {
    var dest = 'D:/O2/github/huqi1980/o2oa/o2web/source/';
    del(dest, { dryRun: true, force: true }, cb);
});

gulp.task("git_dest", function () {
    var dest = "D:/O2/github/huqi1980/o2oa/o2web/source";
    return gulp.src(["source/**/*", "!./**/test/**"])
        .pipe(changed(dest))
        .pipe(gulp.dest(dest))
});
gulp.task("git", gulp.series('git_clean', 'git_dest'));

gulp.task("default", gulp.series(gulp.parallel(appTasks, 'index'), "o2:new-v"));

function build(){
    options.ev = "p";
    options.upload = o_options.upload || "";
    options.location = o_options.location || uploadOptions.location;
    options.host = o_options.host || uploadOptions.host;
    options.user = o_options.user || uploadOptions.user;
    options.pass = o_options.pass || uploadOptions.pass;
    options.port = o_options.port || uploadOptions.port;
    options.remotePath = o_options.remotePath || uploadOptions.remotePath;
    options.dest = o_options.dest || uploadOptions.dest || "dest";
};
gulp.task("build", gulp.series("clean", gulp.parallel(appTasks, 'index'), "o2:new-v"))
