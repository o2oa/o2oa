window.RangeArrayUtils = {
    //补集 range [ start, end ]  rangeList  [ [start1, end1], [ start2, end2 ] ... ]
    complementary : function( range, rangeList, type ){
        if( !range )return range;
        var r = this.getRangeObject( range );
        if( !rangeList || rangeList.length == 0 )return this.parse( [r] , type);
        var unitedList = this.union( rangeList );

        var newRange = {};
        if( unitedList[0][0] > r.start ){
            newRange.start = r.start;
        }else if( r.end > unitedList[0][1] ){
            newRange.start = unitedList[0][1];
            unitedList.shift();
        }else{
            return [];
        }
        var newList = [];
        while( unitedList.length > 0 ){
            if( unitedList[0][0] >= r.end ){
                newRange.end = r.end;
                newList.push( Object.clone(newRange) );
                return this.parse( newList , type);
            }else if( r.end <= unitedList[0][1] ){
                newRange.end = unitedList[0][0];
                newList.push( Object.clone(newRange) );
                return this.parse( newList, type );
            }else{
                newRange.end = unitedList[0][0];
                newList.push( Object.clone(newRange) );
                newRange.start = unitedList[0][1];
                unitedList.shift();
            }
        }
        newRange.end = r.end;
        newList.push( Object.clone(newRange ));
        return this.parse( newList, type );
    },
    //取区域并集rangeList  [ [start1, end1], [ start2, end2 ] ... ]
    union : function( ranges, type ){
        if( !ranges || ranges.length == 0)return ranges; //this.parse(this.getRangeObject( ranges ) ) ;
        var rangeList = Array.clone( ranges );
        for( var i=0; i<rangeList.length; i++ ){
            rangeList[i] = this.getRangeObject( rangeList[i] );
        }
        rangeList.sort( function( a, b ){
            return a.start - b.start;
        });

        var newRangeList = [];
        var newRange = rangeList.shift();
        while( rangeList.length > 0 ){
            var nextRange = rangeList.shift();
            if( this.isIntersection( newRange, nextRange ) ){
                newRange.end =  Math.max( newRange.end, nextRange.end );
            }else{
                newRangeList.push(  Object.clone( newRange ) );
                newRange = nextRange;
            }
        }
        if( !nextRange ){
            newRangeList.push(  Object.clone( newRange ) );
        }else if( this.isIntersection( newRange, nextRange ) ){
            newRange.end = Math.max( newRange.end, nextRange.end );
            newRangeList.push(  Object.clone( newRange ) );
        }else{
            newRangeList.push(  Object.clone( nextRange ) );
        }

        return this.parse( newRangeList, type );
    },
    //区域是否相交
    isIntersection : function( range1, range2 ){
        var r1 = typeOf( range1 == "array" ) ? range1 : this.getRangeObject( range1 );
        var r2 = typeOf( range2 == "array" ) ? range2 : this.getRangeObject( range2 );
        if( r1.start > r2.end )return false;
        if( r2.start > r1.end )return false;
        return true;
    },
    parse: function( objectList, type ){
        var list = [];
        for( var i=0; i<objectList.length; i++ ){
            var range = objectList[i];
            if( type && type == "date" ){
                list.push(  [ Date.parse(range.start), Date.parse(range.end) ] );
            }else{
                list.push(  [range.start, range.end] );
            }
        }
        return list;
    },
    getRangeObject: function( range ){
        return {
            start : Math.min( range[0], range[1] ),
            end : Math.max( range[0], range[1]  )
        }
    }
};