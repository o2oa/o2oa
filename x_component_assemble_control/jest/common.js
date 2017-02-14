function failure(data) {
    console.log(data);
    alert(data.message);
}

function splitValue(str, split) {
    var s = ',';
    if (split) {
	s = '' + split;
    }
    if (str) {
	if (str.length > 0) {
	    return str.split(s);
	}
    }
    return [];
}

function joinValue(o, split) {
    var s = ',';
    if (split) {
	s = '' + split;
    }
    if (o) {
	if (toString.apply(o) === '[object Array]') {
	    return o.join(s);
	}
    }
    return o;
}
