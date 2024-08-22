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
    // uglify = require('gulp-uglify-es').default,
    uglify = require('gulp-terser'),
    rename = require('gulp-rename'),
    changed = require('gulp-changed'),
    gulpif = require('gulp-if'),
    http = require('http');
const ora = require('ora');
concat = require('gulp-concat');
var fg = require('fast-glob');
var logger = require('gulp-logger');
var assetRev = require('gulp-o2oa-asset-rev');
const os = require('os');
var through2 = require('through2');
var path = require('path');

var git = require('gulp-git');
const sourcemaps = require('gulp-sourcemaps');

var supportedLanguage = ["zh-cn", "en", "es"];
var translateLanguage = {
    "en": "en",
    "es": "spa"
};
var {generate} = require('@o2oa/language-tools');
function check_language_pack(token){
    return generate(null, translateLanguage, token).then(()=>{
        return generate("o2_core", translateLanguage, token);
    });
}

var downloadHost = "git.o2oa.net";
var protocol = "https";
var commonUrl = "/o2oa/evn-o2server-commons/-/archive/8.3/evn-o2server-commons-8.3.tar.gz?path=commons";
var jvmUrls = {
    "all": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm",
    "linux_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/linux_java11",
    "aix_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/aix_java11",
    "arm_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/arm_java11",
    "macosx64_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/macosx64_java11",
    "macosarm_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/macosarm_java11",
    "mips_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/mips_java11",
    "raspi_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/raspi_java11",
    "windows_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/windows_java11",
    "sw_java11": "/o2oa/evn-o2server-jvm/-/archive/master/evn-o2server-jvm-master.tar.gz?path=jvm/sw_java11"
};

var scripts = {
    "all": ["o2server/*.sh", "o2server/*.jar", "o2server/*.html", "o2server/*.bat", "o2server/version.o2"],
    "linux": ["o2server/*linux*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "aix": ["o2server/*aix*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "arm": ["o2server/*arm*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "macosx64": ["o2server/*macosx64*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "macosarm": ["o2server/*macosarm*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "mips": ["o2server/*mips*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "raspi": ["o2server/*raspi*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "windows": ["o2server/*windows*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"],
    "sw": ["o2server/*sw*", "o2server/*.jar", "o2server/*.html", "o2server/version.o2"]
};

var o_options = minimist(process.argv.slice(2), {//upload: local ftp or sftp
    string: ["e", "lp", "w", "m", "d"]
});
var options = {};
options.ev = o_options.e || "all";
options.lp = o_options.lp || "zh-cn";
options.webSite = o_options.w || "https://www.o2oa.net";
options.mirrorSite = o_options.m || "http://mirror1.o2oa.net";
options.downloadSite = o_options.d || "https://download.o2oa.net";
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
            var cmdText = "["+dateFormat(d, "HH:MM:ss")+"]"+" "+this.description + ': ' + cell + empty + ' ' + (100*percent).toFixed(2) + '% '+speed+count+'\n';
            slog(cmdText);
        }
    };
}

function downloadFile_progress(path, filename, headcb, progresscb, cb){
    var dest = `o2server/${filename}`;

    let stream = fs.createWriteStream(dest);
    var options = { url:protocol+"://"+downloadHost+path };
    var fileHost = downloadHost;
    var filePath =  path;
    stream.on('finish', () => {
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
            downloadFile(path, filename, headcb, progresscb, cb)
        }
    })
    req.on('error', (e) => {
        downloadFile(path, filename, headcb, progresscb, cb)
    });
    req.end();
    //    }
    //});
}
function downloadFile(path, filename, headcb, progresscb, cb){
    var dest = `o2server/${filename}`;

    const spinner = ora({
        'prefixText': 'Downloading '+filename+' ...',
        'spinner': {
            interval: 80, // Optional
            frames: ['⠋','⠙','⠹','⠸','⠼','⠴','⠦','⠧','⠇','⠏']
        }
    }).start();

    let stream = fs.createWriteStream(dest);
    var options = { url:protocol+"://"+downloadHost+path };
    var fileHost = downloadHost;
    var filePath =  path;
    stream.on('finish', () => {
        spinner.stop();
        spinner.succeed(filename + ' Downloaded!');
        cb();
    });
    stream.on('error', (err) => {
        gutil.log(gutil.colors.red("download error"), ":", gutil.colors.red(filename), err);
    });

    request.get(options).pipe(stream);
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
        downloadFile_progress(commonUrl, "commons_git.tar.gz", (length)=>{
            commonsLength = +length;
            initProgress();
        }, (progress)=>{
            commonsCurrentLength = progress.transferred;
            currentLength = +commonsCurrentLength+jvmCurrentLength;
        }, ()=>{
            commonLoaded = true;
            if (jvmLoaded && commonLoaded) resolve();
        });
        downloadFile_progress(jvmUrl, "jvm_git.tar.gz", (length)=>{
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
            dest: 'o2server/tmp',
            tar: {map: function(header){
                    count++;
                    var d = new Date();
                    slog("["+dateFormat(d, "HH:MM:ss")+"] " + count +" "+ header.name+" ...");
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
            dest: 'o2server/tmp',
            tar: {map: function(header){
                    count++;
                    var d = new Date();
                    slog("["+dateFormat(d, "HH:MM:ss")+"] " + count +" "+ header.name+" ...");
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
    return gulp.src("o2server/tmp/evn-o2server-commons-8.3-commons/commons/**/*")
        .pipe(gulp.dest("o2server/commons/"));
}
function move_jvm(){
    console.log(`---------------------------------------------------------------------
  . move jvm files to o2server/jvm ...
---------------------------------------------------------------------`);
    var path;
    if (options.ev=="all"){
        path = "o2server/tmp/evn-o2server-jvm-master-jvm/jvm/**/*"
    }else{
        path = "o2server/tmp/evn-o2server-jvm-master-jvm-"+options.ev+"/jvm/**/*"
    }
    return gulp.src(path)
        .pipe(gulp.dest("o2server/jvm/"));
}
async function clear_commons_git(cb) {
    var dest = ['o2server/tmp/evn-o2server-commons-8.3-commons/', 'o2server/commons_git.tar.gz'];
    await del(dest, {force: true});
    cb();
}
async function clear_jvm_git(cb){
    await del(['o2server/tmp/', 'o2server/jvm_git.tar.gz'], { force: true });
    cb();
}

function build_web_language_pack(cb){
    if (fs.existsSync('./gulpconfig.js')){
        const {token} = require('./gulpconfig.js');
        return check_language_pack(token);
    }
    cb();
}

exports.build_web_language_pack = build_web_language_pack

var moduleFolder = [];
async function build_web_module() {
    var dest = 'target/o2server/servers/webServer/';
    var srcPath = 'o2web/source';
    const fp = fs.promises;

    return fp.readdir(srcPath).then((files)=>{
        let statP = [];
        files.forEach((file) => {
            let p = path.resolve(srcPath, file)
            statP.push(fp.stat(p).then((s)=>{
                if (s.isDirectory()){
                    var pkgPath = path.resolve(srcPath, p, 'package.json');
                    if (fs.existsSync(pkgPath)){
                        var pkg = require(pkgPath);
                        if (pkg.scripts['o2-build']){
                            moduleFolder.push(file);
                        }
                    }
                }
            }));
        });
        const shelljs = require('shelljs');
        return Promise.all(statP).then(()=>{
            moduleFolder.forEach((f)=>{
                shelljs.config.verbose = true;
                shelljs.exec('npm install && npm run o2-build', {cwd: path.resolve(srcPath, f)});
            });
        });
    });
}

function build_web_minimize(cb) {
    console.log(`---------------------------------------------------------------------
  . Start compiling the web ...
---------------------------------------------------------------------`);

    var dest = 'target/o2server/servers/webServer/';
    var lpFiles = supportedLanguage.join('|');

    var src_min = ['o2web/source/**/*.js', '!**/lp/!('+lpFiles+').js', '!o2web/source/o2_core/o2.js', '!**/*.spec.js', '!**/test/**', '!o2web/source/o2_lib/**/*', '!**/node_modules/**/*', '!**/dist/**/*'];
    moduleFolder.forEach((f)=>{
        src_min.push('!o2web/source/'+f+'/**/*');
    })

    var entries = fg.sync(src_min, { dot: false});
    var size = entries.length;

    var pb = new ProgressBar('', 50);
    var doCount = 0;

    var stream = gulp.src(src_min);

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

    var lpFiles = supportedLanguage.join('|');

    var src_move = ['o2web/source/**/*', '!**/lp/!('+lpFiles+').js', '!o2web/source/o2_core/o2.js', '!**/*.spec.js', '!**/test/**', '!**/node_modules/**/*', '!**/dist/**/*'];
    moduleFolder.forEach((f)=>{
        src_move.push('!o2web/source/'+f+'/**/*');
    })

    var entries = fg.sync(src_move, { dot: false});
    var size = entries.length;
    var pb = new ProgressBar('', 50);
    var doCount = 0;

    var stream = gulp.src(src_move);

    return stream.pipe(gulp.dest(dest))
        .pipe(logger(function(){
            doCount++;
            if (doCount <= size) {pb.render({ completed: doCount, total: size, count: doCount})};
        }))
        .pipe(gutil.noop());
}

function build_concat_o2(){
    var src = [
        'o2web/source/o2_core/polyfill.js',
        'o2web/source/o2_lib/mootools/mootools-1.6.0_all.js',
        'o2web/source/o2_lib/mootools/plugin/mBox.js',
        'o2web/source/o2_core/o2.js'
    ];
    var dest = 'target/o2server/servers/webServer/o2_core/';
    return gulp.src(src)
        .pipe(concat('o2.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('o2.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(dest))
}
function build_concat_base(){
    var src = [
        'o2web/source/x_desktop/js/base.js',
        'o2web/source/o2_core/o2/xScript/PageEnvironment.js',
        'o2web/source/o2_core/o2/framework.js',
        'o2web/source/x_desktop/js/base_loader.js'
    ];
    var dest = 'target/o2server/servers/webServer/x_desktop/js/';
    return gulp.src(src)
        .pipe(concat('base.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('base.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(dest));
}
function build_concat_desktop(){
    let path = "o2_core";
    var src = [
        'o2web/source/'+path+'/o2/widget/Common.js',
        'o2web/source/'+path+'/o2/widget/Dialog.js',
        'o2web/source/'+path+'/o2/widget/UUID.js',
        'o2web/source/'+path+'/o2/xDesktop/Common.js',
        'o2web/source/'+path+'/o2/xDesktop/Actions/RestActions.js',
        'o2web/source/'+path+'/o2/xAction/RestActions.js',
        'o2web/source/'+path+'/o2/xDesktop/Access.js',
        'o2web/source/'+path+'/o2/xDesktop/Dialog.js',
        'o2web/source/'+path+'/o2/xDesktop/Menu.js',
        'o2web/source/'+path+'/o2/xDesktop/UserData.js',
        'o2web/source/x_component_Template/MPopupForm.js',
        'o2web/source/'+path+'/o2/xDesktop/Authentication.js',
        'o2web/source/'+path+'/o2/xDesktop/Dialog.js',
        'o2web/source/'+path+'/o2/xDesktop/Window.js',
        'o2web/source/x_component_Common/Main.js'
    ];
    var dest = 'target/o2server/servers/webServer/o2_core/o2/xDesktop/';
    return gulp.src(src)
        .pipe(concat('$all.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('$all.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(dest))
}
function build_concat_xform(){
    let path = "x_component_process_Xform";
    var src = [
        'o2web/source/o2_core/o2/widget/AttachmentController.js',
        'o2web/source/o2_core/o2/xScript/Macro.js',
        'o2web/source/o2_core/o2/widget/Tab.js',
        'o2web/source/o2_core/o2/widget/O2Identity.js',
        'o2web/source/' + path + '/Form.js',
        'o2web/source/' + path + '/$Module.js',
        'o2web/source/' + path + '/$Input.js',
        'o2web/source/' + path + '/Div.js',
        'o2web/source/' + path + '/Combox.js',
        'o2web/source/' + path + '/DatagridMobile.js',
        'o2web/source/' + path + '/DatagridPC.js',
        'o2web/source/' + path + '/Textfield.js',
        'o2web/source/' + path + '/Personfield.js',
        'o2web/source/' + path + '/Button.js',
        'o2web/source/' + path + '/ViewSelector.js',
        'o2web/source/' + path + '/*.js',
        'o2web/source/x_component_process_Work/Processor.js',
        '!o2web/source/' + path + '/Documenteditor.js ',
        '!o2web/source/' + path + '/Office.js',
        '!o2web/source/' + path + '/WpsOffice.js',
        '!o2web/source/' + path + '/WpsOffice2.js',
        '!o2web/source/' + path + '/YozoOffice.js',
        '!o2web/source/' + path + '/IWebOffice.js',
        '!o2web/source/' + path + '/OnlyOffice.js',
        '!o2web/source/' + path + '/TinyMCEEditor.js'
    ];
    var dest = 'target/o2server/servers/webServer/'+path+'/';
    return gulp.src(src)
        .pipe(concat('$all.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('$all.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(dest))
}

function build_concat_cms_xform(){
    let processPath = "x_component_process_Xform";
    let path = "x_component_cms_Xform";
    var src = [
        'o2web/source/o2_core/o2/widget/AttachmentController.js',
        // 'source/o2_core/o2/xScript/CMSEnvironment.js',
        'o2web/source/o2_core/o2/xScript/CMSMacro.js',
        'o2web/source/o2_core/o2/widget/Tab.js',
        'o2web/source/o2_core/o2/widget/O2Identity.js',
        'o2web/source/' + processPath + '/Form.js',
        'o2web/source/' + processPath + '/$Module.js',
        'o2web/source/' + processPath + '/$Input.js',
        'o2web/source/' + processPath + '/Div.js',
        //'source/' + processPath + '/Combox.js',
        'o2web/source/' + processPath + '/DatagridMobile.js',
        'o2web/source/' + processPath + '/DatagridPC.js',
        'o2web/source/' + processPath + '/Textfield.js',
        //'source/' + processPath + '/Personfield.js',
        'o2web/source/' + processPath + '/Button.js',
        //'source/' + processPath + '/ViewSelector.js',
        'o2web/source/' + processPath + '/Org.js',
        // 'source/' + processPath + '/*.js',
        'o2web/source/' + processPath + '/Actionbar.js',
        //'source/' + processPath + '/Address.js',
        'o2web/source/' + processPath + '/Attachment.js',
        'o2web/source/' + processPath + '/Calendar.js',
        'o2web/source/' + processPath + '/Checkbox.js',
        'o2web/source/' + processPath + '/Datagrid.js',
        'o2web/source/' + processPath + '/Htmleditor.js',
        //'source/' + processPath + '/Iframe.js',
        'o2web/source/' + processPath + '/Label.js',
        'o2web/source/' + processPath + '/Number.js',
        'o2web/source/' + processPath + '/Common.js',
        'o2web/source/' + processPath + '/Image.js',
        'o2web/source/' + processPath + '/ImageClipper.js',
        'o2web/source/' + processPath + '/Html.js',
        'o2web/source/' + processPath + '/Radio.js',
        'o2web/source/' + processPath + '/Select.js',
        //'source/' + processPath + '/Stat.js',
        //'source/' + processPath + '/Statement.js',
        //'source/' + processPath + '/StatementSelector.js',
        //'source/' + processPath + '/Subform.js',
        'o2web/source/' + processPath + '/Tab.js',
        'o2web/source/' + processPath + '/Table.js',
        'o2web/source/' + processPath + '/Textarea.js',
        //'source/' + processPath + '/Tree.js',
        //'source/' + processPath + '/View.js',
        // 'source/x_component_process_Work/Processor.js',
        // '!source/' + processPath + '/Office.js'


        'o2web/source/o2_core/o2/widget/SimpleToolbar.js',
        'o2web/source/' + path + '/ModuleImplements.js',
        'o2web/source/' + path + '/Package.js',
        'o2web/source/' + path + '/Form.js',
        //'source/' + path + '/widget/Comment.js',
        'o2web/source/' + path + '/widget/Log.js',
        'o2web/source/' + path + '/Org.js',
        'o2web/source/' + path + '/Author.js',
        'o2web/source/' + path + '/Reader.js',
        'o2web/source/' + path + '/Textfield.js',
        'o2web/source/' + path + '/Actionbar.js',
        'o2web/source/' + path + '/Attachment.js',
        'o2web/source/' + path + '/Button.js',
        'o2web/source/' + path + '/Calendar.js',
        'o2web/source/' + path + '/Checkbox.js',
        'o2web/source/' + path + '/Datagrid.js',
        'o2web/source/' + path + '/Htmleditor.js',
        'o2web/source/' + path + '/ImageClipper.js',
        'o2web/source/' + path + '/Label.js',
        'o2web/source/' + path + '/Number.js',
        'o2web/source/' + path + '/Radio.js',
        'o2web/source/' + path + '/Select.js',
        'o2web/source/' + path + '/Tab.js',
        'o2web/source/' + path + '/Table.js',
        'o2web/source/' + path + '/Textarea.js'
        //'source/' + path + '/Personfield.js',
        //'source/' + path + '/Readerfield.js',
        //'source/' + path + '/Authorfield.js',
        //'source/' + path + '/Orgfield.js',
        // 'source/' + path + '/*.js',
        // '!source/' + path + '/Office.js'
    ];
    var dest = 'target/o2server/servers/webServer/'+path+'/';
    return gulp.src(src)
        .pipe(concat('$all.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('$all.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(dest))
}

function build_bundle(){
    let path = "o2_core";
    var src = [
        'o2web/source/o2_core/polyfill.js',
        'o2web/source/o2_lib/mootools/mootools-1.6.0_all.js',
        'o2web/source/o2_lib/mootools/plugin/mBox.js',
        'o2web/source/o2_core/o2.js',
        'o2web/source/x_desktop/js/base.js',
        'o2web/source/x_desktop/js/base_loader.js',
        'o2web/source/o2_core/o2/xScript/PageEnvironment.js',
        "o2web/source/o2_core/o2/framework.js"
    ];
    var dest = 'target/o2server/servers/webServer/'+path+'/';
    return gulp.src(src)
        .pipe(concat('bundle.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('bundle.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(dest))
}


function concat_Actions(){
    return through2.obj(function (file, enc, cb) {
        if (file.isNull()) {
            this.push(file);
            return cb();
        }

        if (file.isStream()) {
            this.emit('error', new gutil.PluginError(PLUGIN_NAME, 'Streaming not supported'));
            return cb();
        }
        var content = file.contents.toString();

        var o = path.parse(file.path);
        var name = o.name;
        content = "var actionJson = "+content;
        content = content+"\nif (!o2.xAction.RestActions.Action[\""+name+"\"]) o2.xAction.RestActions.Action[\""+name+"\"] = new Class({Extends: o2.xAction.RestActions.Action});";
        content = content+"\no2.Actions.actions[\""+name+"\"] = new o2.xAction.RestActions.Action[\""+name+"\"](\""+name+"\", actionJson);";

        file.contents = new Buffer.from(content);
        this.push(file);
        cb();
    });
}
function concat_Style(){
    return through2.obj(function (file, enc, cb) {
        if (file.isNull()) {
            this.push(file);
            return cb();
        }

        if (file.isStream()) {
            this.emit('error', new gutil.PluginError(PLUGIN_NAME, 'Streaming not supported'));
            return cb();
        }
        var content = file.contents.toString();
        var name = file.path.replace(process.cwd(), "").replace(/\\/g, "/")
        name = ".."+name.substring(name.indexOf("/source")+7);
        content = "var csskey = encodeURIComponent(\""+name+"\");\no2.widget.css[csskey]="+content;

        file.contents = new Buffer.from(content);
        this.push(file);
        cb();
    });
}

function build_concat_basework_style(){
    return gulp.src([
        "o2web/source/x_component_process_Work/$Main/default/css.wcss",
        "o2web/source/x_component_process_Xform/$Form/default/css.wcss",
        "o2web/source/o2_core/o2/widget/$Tab/mobileForm/css.wcss",
        "o2web/source/o2_core/o2/widget/$Menu/tab/css.wcss",
        "o2web/source/o2_core/o2/widget/$Tab/form/css.wcss",
        "o2web/source/x_component_process_Xform/$Form/default/doc.wcss",
        "o2web/source/o2_core/o2/widget/$Toolbar/documentEdit/css.wcss",
        "o2web/source/o2_core/o2/widget/$Toolbar/documentEdit_side/css.wcss",
        "o2web/source/x_component_process_Xform/$Form/default/css.wcss"
    ])
        .pipe(concat_Style())
        .pipe(concat('js/base_work_style_temp.js'))
        .pipe(gulp.dest('o2web/source/x_desktop/'));
}

function build_concat_basework_action(){
    return gulp.src([
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_authentication.json",
        "o2web/source/o2_core/o2/xAction/services/x_processplatform_assemble_surface.json",
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_control.json",
        "o2web/source/o2_core/o2/xAction/services/x_query_assemble_surface.json",
        "o2web/source/o2_core/o2/xAction/services/x_cms_assemble_control.json",
        "o2web/source/o2_core/o2/xAction/services/x_program_center.json",
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_personal.json"
    ])
        .pipe(concat_Actions())
        .pipe(concat('js/base_work_actions_temp.js'))
        .pipe(gulp.dest('o2web/source/x_desktop/'));
}

function build_concat_basework_clean(cb) {
    var dest = [
        'o2web/source/x_desktop/js/base_work_actions_temp.js',
        'o2web/source/x_desktop/js/base_work_style_temp.js'
    ];
    return del(dest, cb);
}



function build_concat_lp(cb) {
    var lpTasks = [];
    supportedLanguage.forEach(function(lp){
        var src = [
            'o2web/source/o2_core/o2/lp/'+(lp)+'.js',
            'o2web/source/x_component_process_Work/lp/'+(lp)+'.js',
            'o2web/source/x_component_process_Xform/lp/'+(lp)+'.js',
            'o2web/source/x_component_Selector/lp/'+(lp)+'.js',
            'o2web/source/x_component_Template/lp/'+(lp)+'.js',
            'o2web/source/x_component_portal_Portal/lp/'+(lp)+'.js',
            'o2web/source/x_component_cms_Document/lp/'+(lp)+'.js',
            'o2web/source/x_component_cms_Xform/lp/'+(lp)+'.js',
        ];
        var dest = 'target/o2server/servers/webServer/x_desktop/js/';
        var stream = gulp.src(src, {"allowEmpty": true});
        lpTasks.push(new Promise((resolve)=>{
            stream.on("end", ()=>{  resolve(); });
        }));
        stream.pipe(concat('base_lp_' + lp + '.js'))
            .pipe(gulp.dest(dest))
            .pipe(sourcemaps.init())
            .pipe(concat('base_lp_' + lp + '.min.js'))
            .pipe(uglify())
            .pipe(sourcemaps.write('./'))
            .pipe(gulp.dest(dest));
    });

    return Promise.all(lpTasks);
}



function build_concat_basework_body() {
    var src = [
        'o2web/source/x_desktop/js/base_concat_head.js',
        //'o2web/source/o2_core/o2/lp/'+(options.lp || 'zh-cn')+'.js',

        'o2web/source/x_desktop/js/base_work_style_temp.js',

        'o2web/source/o2_core/o2/widget/Common.js',
        'o2web/source/o2_core/o2/widget/Dialog.js',
        'o2web/source/o2_core/o2/widget/UUID.js',
        'o2web/source/o2_core/o2/widget/Menu.js',
        'o2web/source/o2_core/o2/widget/Toolbar.js',
        'o2web/source/o2_core/o2/xDesktop/Common.js',
        'o2web/source/o2_core/o2/xDesktop/Actions/RestActions.js',
        'o2web/source/o2_core/o2/xAction/RestActions.js',
        'o2web/source/o2_core/o2/xDesktop/Access.js',
        'o2web/source/o2_core/o2/xDesktop/Dialog.js',
        'o2web/source/o2_core/o2/xDesktop/Menu.js',
        'o2web/source/o2_core/o2/xDesktop/UserData.js',
        'o2web/source/x_component_Template/MPopupForm.js',
        'o2web/source/o2_core/o2/xDesktop/Authentication.js',
        'o2web/source/o2_core/o2/xDesktop/Dialog.js',
        'o2web/source/o2_core/o2/xDesktop/Window.js',
        'o2web/source/x_component_Common/Main.js',

        // 'o2web/source/o2_core/o2/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_process_Work/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_process_Xform/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_Selector/lp/'+(options.lp || 'zh-cn')+'.js',

        'o2web/source/x_component_process_Work/Main.js',
        'o2web/source/x_component_Selector/package.js',
        // 'o2web/source/x_component_Selector/Person.js',
        // 'o2web/source/x_component_Selector/Identity.js',
        // 'o2web/source/x_component_Selector/Unit.js',
        // 'o2web/source/x_component_Selector/IdentityWidthDuty.js',
        // 'o2web/source/x_component_Selector/IdentityWidthDutyCategoryByUnit.js',
        // 'o2web/source/x_component_Selector/UnitWithType.js',
        'o2web/source/o2_core/o2/xScript/Actions/UnitActions.js',
        'o2web/source/o2_core/o2/xScript/Actions/ScriptActions.js',
        'o2web/source/o2_core/o2/xScript/Actions/CMSScriptActions.js',
        'o2web/source/o2_core/o2/xScript/Actions/PortalScriptActions.js',
        'o2web/source/o2_core/o2/xScript/Environment.js',
        'o2web/source/x_component_Template/MTooltips.js',
        'o2web/source/x_component_Template/MSelector.js',

        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_authentication.js',
        'o2web/source/o2_core/o2/xAction/services/x_processplatform_assemble_surface.js',
        'o2web/source/o2_core/o2/xAction/services/x_cms_assemble_control.js',
        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_control.js',
        'o2web/source/o2_core/o2/xAction/services/x_query_assemble_surface.js',
        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_personal.js',

        'o2web/source/x_desktop/js/base_work_actions_temp.js',

        'o2web/source/x_desktop/js/base.js',
        'o2web/source/x_desktop/js/base_loader.js'
    ];
    var dest = 'target/o2server/servers/webServer/x_desktop/js/';
    return gulp.src(src)
        .pipe(concat('base_work.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('base_work.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write("./"))
        .pipe(gulp.dest(dest));
}

function build_concat_baseportal_style(){
    return gulp.src([
        "o2web/source/x_component_process_Work/$Main/default/css.wcss",
        "o2web/source/x_component_portal_Portal/$Main/default/css.wcss",
        "o2web/source/x_component_process_Xform/$Form/default/css.wcss",
        "o2web/source/o2_core/o2/widget/$Tab/mobileForm/css.wcss",
        "o2web/source/o2_core/o2/widget/$Menu/tab/css.wcss",
    ])
        .pipe(concat_Style())
        .pipe(concat('js/base_portal_style_temp.js'))
        .pipe(gulp.dest('o2web/source/x_desktop/'));
}

function build_concat_baseportal_action(){
    return gulp.src([
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_authentication.json",
        "o2web/source/o2_core/o2/xAction/services/x_portal_assemble_surface.json",
        "o2web/source/o2_core/o2/xAction/services/x_processplatform_assemble_surface.json",
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_control.json",
        "o2web/source/o2_core/o2/xAction/services/x_query_assemble_surface.json",
        "o2web/source/o2_core/o2/xAction/services/x_cms_assemble_control.json",
        "o2web/source/o2_core/o2/xAction/services/x_program_center.json",
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_personal.json"
    ])
        .pipe(concat_Actions())
        .pipe(concat('js/base_portal_actions_temp.js'))
        .pipe(gulp.dest('o2web/source/x_desktop/'));
}

function build_concat_baseportal_clean(cb) {
    var dest = [
        'o2web/source/x_desktop/js/base_portal_actions_temp.js',
        'o2web/source/x_desktop/js/base_portal_style_temp.js'
    ];
    return del(dest, cb);
}

function build_concat_baseportal_body() {
    var src = [
        'o2web/source/x_desktop/js/base_concat_head.js',
        //'o2web/source/o2_core/o2/lp/'+(options.lp || 'zh-cn')+'.js',

        'o2web/source/x_desktop/js/base_portal_style_temp.js',

        'o2web/source/o2_core/o2/widget/Common.js',
        'o2web/source/o2_core/o2/widget/Dialog.js',
        'o2web/source/o2_core/o2/widget/UUID.js',
        'o2web/source/o2_core/o2/widget/Menu.js',
        'o2web/source/o2_core/o2/widget/Toolbar.js',
        'o2web/source/o2_core/o2/xDesktop/Common.js',
        'o2web/source/o2_core/o2/xDesktop/Actions/RestActions.js',
        'o2web/source/o2_core/o2/xAction/RestActions.js',
        'o2web/source/o2_core/o2/xDesktop/Access.js',
        'o2web/source/o2_core/o2/xDesktop/Dialog.js',
        'o2web/source/o2_core/o2/xDesktop/Menu.js',
        'o2web/source/o2_core/o2/xDesktop/UserData.js',
        'o2web/source/x_component_Template/MPopupForm.js',
        'o2web/source/o2_core/o2/xDesktop/Authentication.js',
        'o2web/source/o2_core/o2/xDesktop/Window.js',

        'o2web/source/x_component_Common/Main.js',

        // 'o2web/source/x_component_process_Work/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_portal_Portal/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_process_Xform/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_Selector/lp/'+(options.lp || 'zh-cn')+'.js',

        'o2web/source/x_component_portal_Portal/Main.js',

        'o2web/source/x_component_Selector/package.js',
        'o2web/source/x_component_Selector/Person.js',
        'o2web/source/x_component_Selector/Identity.js',
        'o2web/source/x_component_Selector/Unit.js',
        'o2web/source/x_component_Selector/IdentityWidthDuty.js',
        'o2web/source/x_component_Selector/IdentityWidthDutyCategoryByUnit.js',
        'o2web/source/x_component_Selector/UnitWithType.js',

        'o2web/source/o2_core/o2/xScript/Actions/UnitActions.js',
        'o2web/source/o2_core/o2/xScript/Actions/ScriptActions.js',
        'o2web/source/o2_core/o2/xScript/Actions/CMSScriptActions.js',
        'o2web/source/o2_core/o2/xScript/Actions/PortalScriptActions.js',
        'o2web/source/o2_core/o2/xScript/PageEnvironment.js',

        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_authentication.js',
        'o2web/source/o2_core/o2/xAction/services/x_processplatform_assemble_surface.js',
        'o2web/source/o2_core/o2/xAction/services/x_cms_assemble_control.js',
        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_control.js',
        'o2web/source/o2_core/o2/xAction/services/x_query_assemble_surface.js',
        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_personal.js',

        'o2web/source/x_desktop/js/base_portal_actions_temp.js',

        'o2web/source/x_desktop/js/base.js',
        'o2web/source/x_desktop/js/base_loader.js'
    ];
    var dest = 'target/o2server/servers/webServer/x_desktop/js/';
    return gulp.src(src)
        .pipe(concat('base_portal.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('base_portal.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(dest));
}


function build_concat_basedocument_style(){
    return gulp.src([
        "o2web/source/x_component_cms_Document/$Main/default/css.wcss",
        "o2web/source/x_component_cms_Xform/$Form/default/css.wcss",
        "o2web/source/o2_core/o2/widget/$AttachmentController/default/css.wcss"
    ])
        .pipe(concat_Style())
        .pipe(concat('js/base_document_style_temp.js'))
        .pipe(gulp.dest('o2web/source/x_desktop/'));
}

function build_concat_basedocument_action(){
    return gulp.src([
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_authentication.json",
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_control.json",
        "o2web/source/o2_core/o2/xAction/services/x_cms_assemble_control.json",
        "o2web/source/o2_core/o2/xAction/services/x_program_center.json",
        "o2web/source/o2_core/o2/xAction/services/x_organization_assemble_personal.json"
    ])
        .pipe(concat_Actions())
        .pipe(concat('js/base_document_actions_temp.js'))
        .pipe(gulp.dest('o2web/source/x_desktop/'));
}

function build_concat_basedocument_clean(cb) {
    var dest = [
        'o2web/source/x_desktop/js/base_document_actions_temp.js',
        'o2web/source/x_desktop/js/base_document_style_temp.js'
    ];
    return del(dest, cb);
}

function build_concat_basedocument_body() {
    var src = [
        'o2web/source/x_desktop/js/base_concat_head.js',
        //'o2web/source/o2_core/o2/lp/'+(options.lp || 'zh-cn')+'.js',

        'o2web/source/x_desktop/js/base_document_style_temp.js',

        'o2web/source/o2_core/o2/widget/Common.js',
        'o2web/source/o2_core/o2/widget/Dialog.js',
        'o2web/source/o2_core/o2/widget/UUID.js',
        'o2web/source/o2_core/o2/widget/Menu.js',
        'o2web/source/o2_core/o2/widget/Mask.js',
        'o2web/source/o2_core/o2/xDesktop/Common.js',
        'o2web/source/o2_core/o2/xDesktop/Actions/RestActions.js',
        'o2web/source/o2_core/o2/xAction/RestActions.js',
        'o2web/source/o2_core/o2/xDesktop/Access.js',
        'o2web/source/o2_core/o2/xDesktop/Dialog.js',
        'o2web/source/o2_core/o2/xDesktop/Menu.js',
        'o2web/source/o2_core/o2/xDesktop/UserData.js',
        'o2web/source/x_component_Template/MPopupForm.js',
        'o2web/source/o2_core/o2/xDesktop/Authentication.js',
        'o2web/source/o2_core/o2/xDesktop/Window.js',

        'o2web/source/x_component_Common/Main.js',

        // 'o2web/source/x_component_cms_Document/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_process_Xform/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_Selector/lp/'+(options.lp || 'zh-cn')+'.js',
        // 'o2web/source/x_component_cms_Xform/lp/'+(options.lp || 'zh-cn')+'.js',

        'o2web/source/x_component_cms_Document/Main.js',

        'o2web/source/x_component_Selector/package.js',

        'o2web/source/o2_core/o2/xScript/Actions/UnitActions.js',
        'o2web/source/o2_core/o2/xScript/Actions/CMSScriptActions.js',
        'o2web/source/o2_core/o2/xScript/CMSEnvironment.js',

        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_authentication.js',
        'o2web/source/o2_core/o2/xAction/services/x_cms_assemble_control.js',
        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_control.js',
        'o2web/source/o2_core/o2/xAction/services/x_organization_assemble_personal.js',

        'o2web/source/x_desktop/js/base_document_actions_temp.js',

        'o2web/source/x_desktop/js/base.js',
        'o2web/source/x_desktop/js/base_loader.js'
    ];
    var dest = 'target/o2server/servers/webServer/x_desktop/js/';
    return gulp.src(src)
        .pipe(concat('base_document.js'))
        .pipe(gulp.dest(dest))
        .pipe(sourcemaps.init())
        .pipe(concat('base_document.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(dest));
}

function getGitV(){
    var tagPromise = new Promise(function(s, f){
        git.exec({args : 'describe --tag'}, function (err, stdout) {
            if (err){
                f(err);
            }
            var v = stdout.substring(0, stdout.lastIndexOf("-"));
            s(v);
        });
    });
    var revPromise = new Promise(function(s, f){
        git.exec({args : 'rev-parse --short HEAD'}, function (err, hash) {
            if (err){
                f(err);
            }
            s(hash.trim());
        });
    });
    return Promise.all([tagPromise,revPromise])
}

function build_web_v_html() {
    var src = 'o2web/source/x_desktop/*.html';
    var dest = 'target/o2server/servers/webServer/x_desktop/';

    return getGitV().then(function(arr){
        return gulp.src(src)
            .pipe(assetRev({"verConnecter": arr[0], "md5": arr[1]}))
            .pipe(gulp.dest(dest))
            .pipe(gutil.noop());
    }, function(){
        return gulp.src(src)
            .pipe(assetRev())
            .pipe(gulp.dest(dest))
            .pipe(gutil.noop());
    });
}
function build_web_api() {
    var src = 'o2web/api/**/*';
    var dest = 'target/o2server/servers/webServer/api/';
    return gulp.src(src)
        .pipe(gulp.dest(dest))
}

function build_doc(){
    return getGitV().then(function(arr){
        return (shell.task('jsdoc -c o2web/jsdoc.conf.json -q version='+arr[0]+'-'+arr[1]+''))();
    }, function(){
        return (shell.task('jsdoc -c o2web/jsdoc.conf.json -q version='))();
    });
}
exports.build_api = gulp.series(build_doc, build_web_api);


function build_web_v_o2() {
    var src = [
        'target/o2server/servers/webServer/o2_core/o2.js',
        'target/o2server/servers/webServer/o2_core/o2.min.js'
    ];
    var dest = 'target/o2server/servers/webServer/o2_core/';

    return getGitV().then(function(arr){
        debugger;
        return gulp.src(src)
            .pipe(assetRev({"verConnecter": arr[0], "md5": arr[1], "replace": true}))
            .pipe(gulp.dest(dest))
            .pipe(gutil.noop());
    }, function(){
        return gulp.src(src)
            .pipe(assetRev())
            .pipe(gutil.noop());
    });
}
exports.build_version = gulp.parallel(build_web_v_o2, build_web_v_html);

async function clear_build(cb) {
    console.log(`---------------------------------------------------------------------
  . clear old build ...
---------------------------------------------------------------------`);
    var dest = 'target';
    await del(dest, {force: true});
    cb();
}
async function clear_deploy(cb) {
    console.log(`---------------------------------------------------------------------
  . clear old deploy ...
---------------------------------------------------------------------`);
    var dest = ["target/o2server/store/", "target/o2server/commons/", "target/o2server/jvm/", "target/o2server/configSample/", "target/o2server/localSample/", "target/o2server/servers/"];
    dest = dest.concat(["target/o2server/*.sh", "target/o2server/*.jar", "target/o2server/*.html", "target/o2server/*.bat", "target/o2server/version.o2"]);
    await del(dest, {force: true});
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

    return stream.pipe(gulp.dest(dest))
        .pipe(logger(function(){
            doCount++;
            if (doCount <= size) {pb.render({ completed: doCount, total: size, count: doCount})};
        }));
}

exports.preperation =  gulp.series(download_commons_and_jvm, decompress_commons_and_jvm, move_commons, move_jvm, clear_commons_git, clear_jvm_git);

var shell = require('gulp-shell')
const shelljs = require("shelljs");
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
function chmod_servers(){
    return (shell.task('chmod 777 -R target/o2server/servers'))();
}
exports.build_web = gulp.series(
    build_web_language_pack,
    build_web_module,
    build_web_minimize,
    build_web_move,
    gulp.parallel(
        build_concat_o2,
        build_concat_base,
        build_concat_desktop,
        build_concat_xform,
        build_concat_cms_xform,
        build_concat_lp,
        gulp.series(build_concat_basework_style, build_concat_basework_action, build_concat_basework_body,build_concat_basework_clean),
        gulp.series(build_concat_baseportal_style, build_concat_baseportal_action, build_concat_baseportal_body,build_concat_baseportal_clean),
        gulp.series(build_concat_basedocument_style, build_concat_basedocument_action, build_concat_basedocument_body,build_concat_basedocument_clean),
        build_bundle
    ),
    build_web_v_html,
    build_web_v_o2,
);

if (os.platform().indexOf("win")==-1){
    exports.deploy = gulp.series(deploy_server, chmod_jvm, chmod_commons, chmod_sh, chmod_servers);
}else{
    exports.deploy = gulp.series(deploy_server);
}

function createHistoryJsonFile(url, fileName, host){
    const fp = fs.promises;
    return new Promise(function(resolve){
        request.get({'url': url}, function(error, response, body) {
            if (!error && response.statusCode == 200) {
                const historyJsons = JSON.parse(body);
                fp.readFile(path.resolve(process.cwd(), 'download-pro.json'), 'utf8').then(function(str){
                    const downloadJson = JSON.parse(str);
                    downloadJson.windows.url = host+downloadJson.windows.url;
                    downloadJson.linux.url = host+downloadJson.linux.url
                    downloadJson.macosx64.url = host+downloadJson.macosx64.url
                    downloadJson.macosarm.url = host+downloadJson.macosarm.url
                    // downloadJson.aix.url = host+downloadJson.aix.url
                    downloadJson.raspi.url = host+downloadJson.raspi.url
                    downloadJson.mips.url = host+downloadJson.mips.url
                    downloadJson.arm.url = host+downloadJson.arm.url
                    downloadJson.sw.url = host+downloadJson.sw.url;
                    let append = true;
                    for (var i=0; i<historyJsons.length; i++){
                        var o = historyJsons[i];
                        if (o.title == downloadJson.title){
                            historyJsons.splice(i, 1, downloadJson)
                            append = false;
                            break;
                        }
                    }
                    if (append){
                        historyJsons.unshift(downloadJson);
                    }
                    const jsonStr = JSON.stringify(historyJsons, null, '\t');
                    fp.writeFile(path.resolve(process.cwd(), fileName), jsonStr).then(()=>{resolve();});
                });
            }
        });
    });
}

async function createHistroyJson(cb) {
    const host = options.webSite;
    const mirrorHost = options.mirrorSite;
    const downloadHost = options.downloadSite

    if (host) {
        const url = host + "/website/history.json?t=" + (new Date()).getTime();
        const mirrorUrl = mirrorHost + "/download/download-history.json?t=" + (new Date()).getTime();

        var doneWebSite = false;
        var doneMirror = false;
        var check = function () {
            if (doneWebSite && doneMirror) cb();
        };
        await createHistoryJsonFile(url, 'history.json', downloadHost);
        await createHistoryJsonFile(mirrorUrl, 'download-history.json', mirrorHost);
    }
    cb();
}
exports.build_historyJson = createHistroyJson;
