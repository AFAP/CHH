function $(id) {
	return document.getElementById(id);
}
function doNothing() {
}
function browsePictures(objsrc) {
    var index = 0;
    // 主贴（一楼）里的图片路径集合
    var mainImgs = [];
    var imgobjs = document.getElementsByTagName("img");
	for(var i = 0; i < imgobjs.length; i++){
            mainImgs.push(imgobjs[i].getAttribute("src"));
            if(imgobjs[i].getAttribute("src") == objsrc){
                index = mainImgs.length -1;
            }
	}

	window.chhWebView.jsLoadPictures(mainImgs.join(';'),index);
}

function init() {
	var aimgs =  document.getElementsByTagName("img");
	for (var i = 0; i < aimgs.length; i++) {
        aimgs[i].style.width = "100%";
        var funcstr = "(function(){browsePictures('"+aimgs[i].getAttribute("src")+"')})";
        aimgs[i].onclick = eval(funcstr);
	}
}