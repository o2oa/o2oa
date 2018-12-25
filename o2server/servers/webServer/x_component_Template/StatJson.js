var StatJson = new Class({
    options : {
    },
    initialize: function ( context, options ) {
        this.setOptions(options);
        this.context = context;
    },
    load : function(){
        if( this.context.statJson ){
            this.json = JSON.parse(this.context.statJson);
        }else{
            this.json = {
                total : {
                    publishedCount : 0,
                    errorCount : 0
                },
                batch: {}
            }
        }
    },
    sumit : function(){
        this.context.statJson = JSON.stringify(this.json);
    },
    addBatch : function( batchName, isSetCurrent ){
        if( !this.json.batch[batchName] ){
            this.json.batch[batchName] = {
                publishedCount : 0,
                errorCount : 0
            };
        }
        if( isSetCurrent )this.currentBatch = this.json.batch[batchName];
    },
    deleteBatch : function( batchName ){
        var json = this.json;
        if( json.batch[batchName] ){
            var batch = json.batch[batchName];
            if( batch.publishedCount ){
                json.total.publishedCount = json.total.publishedCount - batch.publishedCount;
            }
            if( batch.errorCount ){
                json.total.errorCount = json.total.errorCount - batch.errorCount;
            }
            delete this.json.batch[batchName];
        }
    },
    addData: function( cmsDocData ){
        var d = cmsDocData;
        var totalJson = this.json.total;
        var batchJson = this.currentBatch;
        if( d.docStatus == "published" ){
            totalJson.publishedCount++;
            batchJson.publishedCount++;
            this.addCount( totalJson, d );
            this.addCount( batchJson, d );
        }else if( d.docStatus == "error" ){
            totalJson.errorCount++;
            batchJson.errorCount++;
        }
    },
    addCount : function( json, d ){
        if( d.city ){
            var cityJson = json[ d.city ];
            if( !cityJson ){
                cityJson = json[ d.city ] = { publishedCount : 0 };
            }
            cityJson.publishedCount ++;
            if( d.county ){
                var countyJson = cityJson[ d.county ];
                if( !countyJson ){
                    countyJson = cityJson[ d.county ] = { publishedCount : 0 };
                }
                countyJson.publishedCount ++;
                if( d.brach ) {
                    var branchJson = countyJson[d.brach];
                    if (!branchJson) {
                        branchJson = countyJson[d.brach] = {publishedCount: 0};
                    }
                    branchJson.publishedCount++;
                }
            }
        }
    },
    reduceCount : function( json, d ){
        if( d.city ){
            var cityJson = json[ d.city ];
            if( !cityJson ){
                cityJson = json[ d.city ] = { publishedCount : 0 };
            }
            cityJson.publishedCount --;
            if( d.county ){
                var countyJson = cityJson[ d.county ];
                if( !countyJson ){
                    countyJson = cityJson[ d.county ] = { publishedCount : 0 };
                }
                countyJson.publishedCount --;
                if( d.brach ) {
                    var branchJson = countyJson[d.brach];
                    if (!branchJson) {
                        branchJson = countyJson[d.brach] = {publishedCount: 0};
                    }
                    branchJson.publishedCount--;
                }
            }
        }
    },
    getCity : function(){
        var totalJson = this.json.total;
        var city = [];
        for( var key in totalJson ){
            if( key != "publishedCount" && key != "errorCount" ){
                if( totalJson[key].publishedCount > 0 ){
                    city.push(key);
                }
            }
        }
        return city;
    },
    changeData : function( cmsDocData, oldData ){
        var d = cmsDocData;
        //var oldData = {
        //    status : "error",
        //    city : "",
        //    county : "",
        //    branch : ""
        //};

        var batchJson;
        if( d.$importBatchName && this.json.batch[d.$importBatchName]) {
            batchJson = this.json.batch[d.$importBatchName];
        }
        var totalJson = this.json.total;

        if( oldData.status == "error" ){
            totalJson.errorCount--;
            if( batchJson )batchJson.errorCount--;
        }
        if( oldData.status == "published" ){
            totalJson.publishedCount--;
            this.reduceCount( totalJson, oldData );
            if( batchJson ){
                batchJson.publishedCount--;
                this.reduceCount( batchJson, oldData );
            }
        }

        if( d.docStatus == "error"){
            totalJson.errorCount++;
            if( batchJson )batchJson.errorCount++;
        }
        if( d.docStatus == "published"){
            totalJson.publishedCount++;
            this.addCount( totalJson, d );
            if( batchJson ){
                batchJson.publishedCount++;
                this.addCount( batchJson, d );
            }
        }
    }
});