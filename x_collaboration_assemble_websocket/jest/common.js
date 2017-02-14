function failure(data) {
    console.log(data);
    alert(data.message);
}

function splitValue(str) {
    if (str) {
	if (str.length > 0) {
	    return str.split(',');
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

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
	var c = ca[i];
	while (c.charAt(0) == ' ') {
	    c = c.substring(1);
	}
	if (c.indexOf(name) == 0) {
	    return c.substring(name.length, c.length);
	}
    }
    return "";
}