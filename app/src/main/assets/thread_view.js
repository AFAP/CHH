function $(id) {
	return document.getElementById(id);
}
function doNothing() {
}
function browsePictures(objid) {
    var index = 0;
    // 主贴（一楼）里的图片路径集合
    var mainImgs = [];
    var imgobjs = document.getElementsByTagName("img");
	for(var i = 0; i < imgobjs.length; i++){
        if(imgobjs[i].getAttribute("id") != undefined && imgobjs[i].getAttribute("id").indexOf("aimg") != -1){
            mainImgs.push(imgobjs[i].getAttribute("src"));
            if(imgobjs[i].getAttribute("id") == objid){
                index = mainImgs.length -1;
            }
        }
	}

	window.chhWebView.jsLoadPictures(mainImgs.join(';'),index);
}

function attachimggroup(pid) {

	var aimgs = aimgcount[pid];
	for (var i = 0; i < aimgcount[pid].length; i++) {
		var obj = $("aimg_" + aimgs[i]);
		if (obj != null) {
		var path = obj.getAttribute("file");
			obj.src = path;
			obj.style.width = "100%";
			var funcstr = "(function(){browsePictures('"+obj.getAttribute("id")+"')})";
			obj.onclick = eval(funcstr);
			obj.onmouseover = doNothing;
		}
	}



}