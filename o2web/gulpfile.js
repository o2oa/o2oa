var gulp = require('gulp'),
//var deleted = require('gulp-deleted');
    del = require('del'),
    uglify = require('gulp-uglify'),
    rename = require('gulp-rename'),
    changed = require('gulp-changed'),
    gulpif = require('gulp-if'),
    minimist = require('minimist'),
    ftp = require('gulp-ftp'),
    sftp = require('gulp-sftp'),
    JSFtp = require('jsftp'),
    gutil = require('gulp-util'),
    fs = require("fs");

var apps = [
    {"folder": "o2_lib",                                    "tasks": ["move", "clean"]},
    {"folder": "o2_core",                                   "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ANN",                           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_AppCenter",                     "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_AppMarket",                     "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Attendance",                    "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_BAM",                           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Calendar",                      "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Chat",                          "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_Column",                    "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_ColumnManager",             "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_DictionaryDesigner",        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_Document",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_FormDesigner",              "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_Index",                     "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_Module",                    "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_QueryViewDesigner",         "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_ScriptDesigner",            "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_ViewDesigner",              "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_cms_Xform",                     "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Collect",                       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Common",                        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Console",                       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ControlPanel",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_CRM",                           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Deployment",                    "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_DesignCenter",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Empty",                         "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Execution",                     "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ExeManager",                    "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_FaceSet",                       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_File",                          "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Forum",                         "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ForumCategory",                 "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ForumDocument",                 "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ForumPerson",                   "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ForumSearch",                   "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ForumSection",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_HotArticle",                    "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_IM",                            "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_LogViewer",                     "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Meeting",                       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Message",                       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Minder",                        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_MinderEditor",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Note",                          "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_OKR",                           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_OnlineMeeting",                 "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_OnlineMeetingRoom",             "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Org",                           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_portal_PageDesigner",           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_portal_Portal",                 "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_portal_PortalExplorer",         "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_portal_PortalManager",          "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_portal_ScriptDesigner",         "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_portal_WidgetDesigner",         "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_Application",           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_ApplicationExplorer",   "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_DictionaryDesigner",    "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_FormDesigner",          "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_ProcessDesigner",       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_ProcessManager",        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_ScriptDesigner",        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_StatDesigner",          "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_TaskCenter",            "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_ViewDesigner",          "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_Work",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_process_Xform",                 "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Profile",                       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_query_Query",                   "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_query_QueryExplorer",           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_query_QueryManager",            "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_query_StatDesigner",            "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_query_ViewDesigner",            "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_query_TableDesigner",           "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_query_StatementDesigner",       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Report",                        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ReportDocument",                "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ReportMinder",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_ScriptEditor",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Search",                        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_SelecterTest",                  "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Selector",                      "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_service_AgentDesigner",         "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_service_InvokeDesigner",        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_service_ServiceManager",        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Setting",                       "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_SmartOfficeRoom",               "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Snake",                         "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Strategy",                      "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Template",                      "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_component_Weixin",                        "tasks": ["move", "min", "clean", "watch"]},
    {"folder": "x_desktop",                                 "tasks": ["move", "min", "clean", "watch"]}
];

var uploadOptions = {
    'location': '',
    'host': '',
    'user': '',
    'pass': '',
    "remotePath": "/"
};
var options = minimist(process.argv.slice(2), {//upload: local ftp or sftp
    string: ["upload", "location", "host", "user", "pass", "port", "remotePath"]
});
options.upload = options.upload || "";
options.location = options.location || uploadOptions.location;
options.host = options.host || uploadOptions.host;
options.user = options.user || uploadOptions.user;
options.pass = options.pass || uploadOptions.pass;
options.port = options.port || 0;
options.remotePath = options.remotePath || uploadOptions.remotePath;


var minTasks = [];      //压缩修改过的js文件，更名为 *.min.js,并移动到编译目录（dest）
var moveTasks = [];     //将修改过的文件移动到编译目录（dest）
var watchTasks = [];    //监控任务
var cleanTasks = [];

function getMinTask(path){
    return function(){
        var src = 'source/'+path+'/**/*.js';
        var dest = 'dest/'+path+'/';
        return gulp.src(src)
            .pipe(changed(dest))
            .pipe(uglify())
            .pipe(rename({ extname: '.min.js' }))
            .pipe(gulpif((options.upload=='local'&&options.location!=''), gulp.dest(options.location+path+'/')))
            .pipe(gulpif((options.upload=='ftp'&&options.host!=''), ftp({
                host: options.host,
                user: options.user || 'anonymous',
                pass: options.pass || '@anonymous',
                port: options.port || 21,
                remotePath: (options.remotePath || '/')+path
            })))
            .pipe(gulpif((options.upload=='sftp'&&options.host!=''), ftp({
                host: options.host,
                user: options.user || 'anonymous',
                pass: options.pass || null,
                port: options.port || 22,
                remotePath: (options.remotePath || '/')+path
            })))
            .pipe(gulp.dest(dest))
            .pipe(gutil.noop());
    }
}
function getMoveTask(path){
    return function(){
        var src = 'source/'+path+'/**/*';
        var dest = 'dest/'+path+'/';
        return gulp.src(src)
            .pipe(changed(dest))
            .pipe(gulpif((options.upload=='local'&&options.location!=''), gulp.dest(options.location+path+'/')))
            .pipe(gulpif((options.upload=='ftp'&&options.host!=''), ftp({
                host: options.host,
                user: options.user || 'anonymous',
                pass: options.pass || '@anonymous',
                port: options.port || 21,
                remotePath: (options.remotePath || '/')+path
            })))
            .pipe(gulpif((options.upload=='sftp'&&options.host!=''), ftp({
                host: options.host,
                user: options.user || 'anonymous',
                pass: options.pass || null,
                port: options.port || 22,
                remotePath: (options.remotePath || '/')+path
            })))
            .pipe(gulp.dest(dest))
            .pipe(gutil.noop());
    }
}

function getCleanTask(path){
    return function(cb){
        var dest = 'dest/'+path+'/';
        del(dest, cb);
    }
}

function cleanRemoteFtp(f, cb){
    var file = options.remotePath+f;

    var ftp = new JSFtp({
        host: options.host,
        user: options.user || 'anonymous',
        pass: options.pass || null,
        port: options.port || 21
    });

    ftp.raw('dele '+file, function(err) {
        if (err){ cb(); return; }
        if (file.substring(file.length-3).toLowerCase()==".js"){
            file = file.replace('.js', ".min.js");
            ftp.raw('dele '+file, function(err) {
                if (err){ cb(); return; }

                if (file.indexOf("/")!=-1){
                    var p = file.substring(0, file.lastIndexOf("/"));
                    ftp.raw('rmd '+p, function(err) {
                        if (err){ cb(); return; }

                        ftp.raw.quit();
                        cb();
                    });
                }

            });
        }else{
            if (file.indexOf("/")!=-1){
                var pPath = file.substring(0, file.lastIndexOf("/"));
                ftp.raw('rmd '+pPath, function(err) {
                    if (err){ cb(); return; }
                    ftp.raw.quit();
                    cb();
                });
            }
        }
    });
}
function cleanRemoteLocal(f, cb){
    var file = options.location+f;
    del(file, {force: true, dryRun: true}, function(){
        if (file.substring(file.length-3).toLowerCase()==".js"){
            var minfile = file.replace('.js', ".min.js");
            del(minfile, {force: true, dryRun: true}, function(){
                var p = file.substring(0, file.lastIndexOf("/"));
                fs.rmdir(p,function(err){
                    if(err){}
                    cb();
                })
            });
        }else{
            var p = file.substring(0, file.lastIndexOf("/"));
            fs.rmdir(p,function(err){
                if(err){}
                cb();
            })
        }
    });
}

function getCleanRemoteTask(path){
    return function(cb){
        if (options.upload){
            var file = path.replace(/\\/g, "/");
            file = file.substring(file.indexOf("source/")+7);

            if (options.upload=='local'&&options.location!='') cleanRemoteLocal(file, cb);
            if (options.upload=='ftp'&&options.host!='') cleanRemoteFtp(file, cb);
        }else{
            if (cb) cb();
        }
    }
}
function getWatchTask(path, min){
    return function(cb){
        var moveTask = "move:"+path;
        var minTask = "min:"+path;
        var cleanTask = "clean:"+path;
        if (min) gulp.watch('source/'+path+'/**/*.js', {"events": ['add','change']}, gulp.parallel(minTask));
        gulp.watch('source/'+path+'/**/*', {"events": ['addDir', 'add','change']},  gulp.parallel(moveTask));

        // gulp.watch('source/'+path+'/**/*', {"events": ['unlinkDir']},  function(file){
        //     console.log("into unlinkDir watch ......."+file);
        // });


        watcher = gulp.watch('source/'+path+'/**/*', {delay:500});
        watcher.on('unlink', function(file, stats){
            console.log("into unlink watch ......."+file);
            gulp.task("cleanRemote", getCleanRemoteTask(file))
            gulp.series(gulp.parallel(cleanTask, "cleanRemote"), gulp.parallel(minTask, moveTask))();
        });
        // watcher.on('unlinkDir', function(file, stats){
        //     console.log("into unlinkDir watch ......."+file);
        //     // gulp.task("cleanRemoteDir", getCleanRemoteTask(file))
        //     // gulp.series(gulp.parallel(cleanTask, "cleanRemoteDir"), gulp.parallel(minTask, moveTask))();
        // });
    }
}

apps.map(function(app){
    var taskName = "";
    if (app.tasks.indexOf("min")!==-1){
        taskName = "min:"+app.folder;
        minTasks.push(taskName);
        gulp.task(taskName, getMinTask(app.folder));
    }
    if (app.tasks.indexOf("move")!==-1){
        taskName = "move:"+app.folder;
        moveTasks.push(taskName);
        gulp.task(taskName, getMoveTask(app.folder));
    }
    if (app.tasks.indexOf("clean")!==-1){
        taskName = "clean:"+app.folder;
        cleanTasks.push(taskName);
        gulp.task(taskName, getCleanTask(app.folder));
    }
    if (app.tasks.indexOf("watch")!==-1){
        taskName = "watch:"+app.folder;
        watchTasks.push(taskName);
        gulp.task(taskName, getWatchTask(app.folder, (app.tasks.indexOf("min")!==-1)));
    }
})

gulp.task("default", gulp.parallel(minTasks, moveTasks));
gulp.task("clean", gulp.series(cleanTasks));
gulp.task("sync", gulp.series(
    gulp.series(cleanTasks),
    gulp.parallel(minTasks, moveTasks)
));
gulp.task("watch", gulp.parallel(watchTasks));