MWF.xAction.RestActions.Action["x_processplatform_assemble_bam"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    getRankData: function(data){
        var taskDuration = data.map(function(item, idx){
            return {
                "name": item.name,
                "value": (item.count) ? Math.round((item.duration/item.count/60)*100)/100 : 0
            }
        });
        taskDuration.sort(function(a,b){ return b.value - a.value; });

        var taskElapsedCount = data.map(function(item, idx){
            return {
                "name": item.name,
                "value": item.expiredCount
            }
        });
        taskElapsedCount.sort(function(a,b){ return b.value - a.value; });

        var taskCompletedCount = data.map(function(item, idx){
            return {
                "name": item.name,
                "value": item.completedCount
            }
        });
        taskCompletedCount.sort(function(a,b){ return b.value - a.value; });

        var taskCompletedTimeliness = data.map(function(item, idx){
            return {
                "name": item.name,
                "value": (item.completedCount) ? Math.round(((item.completedCount-item.completedExpiredCount)/item.completedCount)*100)/100  : 0
            }
        });
        taskCompletedTimeliness.sort(function(a,b){ return b.value - a.value; });

        var taskTimeoutRate = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.count+item.completedCount) ? Math.round(((item.expiredCount+item.completedExpiredCount)/(item.count+item.completedCount))*100)/100  : 0
            }
        });
        taskTimeoutRate.sort(function(a,b){ return b.value - a.value; });

        return {
            "taskDuration": taskDuration.filter(function(item){return (item.value)}),
            "taskElapsedCount": taskElapsedCount.filter(function(item){return (item.value)}),
            "taskCompletedCount": taskCompletedCount.filter(function(item){return (item.value)}),
            "taskCompletedTimeliness": taskCompletedTimeliness.filter(function(item){return (item.value)}),
            "taskTimeoutRate": taskTimeoutRate.filter(function(item){return (item.value)}),
        };
    },
    getTaskContentData: function(data, name){
        var taskCount = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": item.taskCount
            }
        });
        taskCount.sort(function(a,b){ return b.value - a.value; });

        var taskDuration = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.taskCount) ? Math.round((item.taskDuration/item.taskCount/60)*100)/100 : 0
            }
        });
        taskDuration.sort(function(a,b){ return b.value - a.value; });

        var taskElapsedCount = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": item.taskExpiredCount
            }
        });
        taskElapsedCount.sort(function(a,b){ return b.value - a.value; });

        var taskTimeoutRate = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.taskCount) ? (item.taskExpiredCount/item.taskCount) : 0
            }
        });
        taskTimeoutRate.sort(function(a,b){ return b.value - a.value; });

        return {
            "taskCount": taskCount.filter(function(item){return (item.value)}),
            "taskDuration": taskDuration.filter(function(item){return (item.value)}),
            "taskElapsedCount": taskElapsedCount.filter(function(item){return (item.value)}),
            "taskTimeoutRate": taskTimeoutRate.filter(function(item){return (item.value)})
        };
    },

    getTaskCompletedContentData: function(data, name){
        var taskCompletedCount = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": item.taskCompletedCount
            }
        });
        taskCompletedCount.sort(function(a,b){ return b.value - a.value; });

        var taskCompletedDuration = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.taskCompletedCount) ? Math.round((item.taskCompletedDuration/item.taskCompletedCount/60)*100)/100 : 0
            }
        });
        taskCompletedDuration.sort(function(a,b){ return b.value - a.value; });

        var taskCompletedElapsedCount = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": item.taskCompletedExpiredCount
            }
        });
        taskCompletedElapsedCount.sort(function(a,b){ return b.value - a.value; });

        var taskTimeoutRate = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.taskCompletedCount) ? (item.taskCompletedExpiredCount/item.taskCompletedCount) : 0
            }
        });
        taskTimeoutRate.sort(function(a,b){ return b.value - a.value; });

        var taskTimelinessRate = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.taskCompletedCount) ? ((item.taskCompletedCount-item.taskCompletedExpiredCount)/item.taskCompletedCount) : 0
            }
        });
        taskTimelinessRate.sort(function(a,b){ return b.value - a.value; });

        return {
            "taskCompletedCount": taskCompletedCount.filter(function(item){return (item.value)}),
            "taskCompletedDuration": taskCompletedDuration.filter(function(item){return (item.value)}),
            "taskCompletedElapsedCount": taskCompletedElapsedCount.filter(function(item){return (item.value)}),
            "taskTimeoutRate": taskTimeoutRate.filter(function(item){return (item.value)}),
            "taskTimelinessRate": taskTimelinessRate.filter(function(item){return (item.value)})
        };
    },
    getWorkContentData: function(data, name){
        var workCount = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": item.workCount
            }
        });
        workCount.sort(function(a,b){ return b.value - a.value; });

        var workDuration = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.workCount) ? Math.round((item.workDuration/item.workCount/60)*100)/100 : 0
            }
        });
        workDuration.sort(function(a,b){ return b.value - a.value; });

        var workElapsedCount = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": item.workExpiredCount
            }
        });
        workElapsedCount.sort(function(a,b){ return b.value - a.value; });

        var workTimeoutRate = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.workCount) ? (item.workExpiredCount/item.workCount) : 0
            }
        });
        workTimeoutRate.sort(function(a,b){ return b.value - a.value; });

        return {
            "workCount": workCount.filter(function(item){return (item.value)}),
            "workDuration": workDuration.filter(function(item){return (item.value)}),
            "workElapsedCount": workElapsedCount.filter(function(item){return (item.value)}),
            "workTimeoutRate": workTimeoutRate.filter(function(item){return (item.value)})
        };
    },

    getWorkCompletedContentData: function(data, name){
        var workCompletedCount = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": item.workCompletedCount
            }
        });
        workCompletedCount.sort(function(a,b){ return b.value - a.value; });

        var workCompletedDuration = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.taskCompletedCount) ? Math.round((item.workCompletedDuration/item.workCompletedCount/60)*100)/100 : 0
            }
        });
        workCompletedDuration.sort(function(a,b){ return b.value - a.value; });

        var workCompletedElapsedCount = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": item.workCompletedExpiredCount
            }
        });
        workCompletedElapsedCount.sort(function(a,b){ return b.value - a.value; });

        var workTimeoutRate = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.workCompletedCount) ? (item.workCompletedExpiredCount/item.workCompletedCount) : 0
            }
        });
        workTimeoutRate.sort(function(a,b){ return b.value - a.value; });

        var workTimelinessRate = data.map(function(item, idx){
            return {
                "name": (name) ? name(item) : item.name,
                "value": (item.workCompletedCount) ? ((item.workCompletedCount-item.workCompletedExpiredCount)/item.workCompletedCount) : 0
            }
        });
        workTimelinessRate.sort(function(a,b){ return b.value - a.value; });

        return {
            "workCompletedCount": workCompletedCount.filter(function(item){return (item.value)}),
            "workCompletedDuration": workCompletedDuration.filter(function(item){return (item.value)}),
            "workCompletedElapsedCount": workCompletedElapsedCount.filter(function(item){return (item.value)}),
            "workTimeoutRate": workTimeoutRate.filter(function(item){return (item.value)}),
            "workTimelinessRate": workTimelinessRate.filter(function(item){return (item.value)})
        };
    },

    //monthly--------------------------------------------------------
    loadMonthly: function(type, sort, filter, success, failure, async){
        var actionName = "loadMonthly"+type.capitalize()+"By"+sort.type.capitalize();
        //var companyName = company || "(0)";
        //var departmentName = depat || "(0)";
        //var personName = person || "(0)";
        var parameter;
        if (sort.type==="activity" || sort.type==="application" || sort.type==="process"){
            parameter = {
                "unit": (filter.unit) ? MWF.name.ou(filter.unit) : "(0)",
                // "companyName": filter.company || "(0)",
                // "departmentName": filter.department || "(0)",
                "personName": filter.person || "(0)",
                "applicationId": sort.range.application,
                "processId": sort.range.process,
                "activityId": sort.range.activity
            };
        }else{
            parameter = {
                "unit": sort.range.unitName,
                // "companyName": sort.range.companyName,
                // "departmentName": sort.range.departmentName,
                "personName": sort.range.personName,
                "applicationId": filter.application || "(0)",
                "processId": filter.process || "(0)",
                "activityId": filter.activity || "(0)"
            };
        }
        this.action.invoke({"name": actionName,"async": async,	"parameter": parameter, "success": success,	"failure": failure});
    }
});