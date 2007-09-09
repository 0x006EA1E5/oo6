var swfu;
function initSwfUpload()
{
	var target = "/go/workbench/upload;jsessionid=" + readCookie('JSESSIONID');
	console.log("Building SWF uploader: " + target); // RTA
	swfu = new SWFUpload({
		upload_script : target,
		target : "SWFUploadTarget",
		flash_path : "/resources/otherobjects.resources/static/swfupload-1.0.2/SWFUpload.swf",
		allowed_filesize : 30720,	// 30 MB
		allowed_filetypes : "*.jpg;*.gif;*.png",
		allowed_filetypes_description : "All image files...",
		browse_link_innerhtml : "Browse",
		upload_link_innerhtml : "Upload queue",
		browse_link_class : "swfuploadbtn browsebtn",
		upload_link_class : "swfuploadbtn uploadbtn",
		flash_loaded_callback : 'swfu.flashLoaded',
		upload_file_queued_callback : "fileQueued",
		upload_file_start_callback : 'uploadFileStart',
		upload_progress_callback : 'uploadProgress',
		upload_file_complete_callback : 'uploadFileComplete',
		upload_file_cancel_callback : 'uploadFileCancelled',
		upload_queue_complete_callback : 'uploadQueueComplete',
		//upload_file_error_callback : 'uploadError',
		upload_cancel_callback : 'uploadCancel',
		auto_upload : false			
	});
};


function fileQueued(file, queuelength) {
	var listingfiles = document.getElementById("SWFUploadFileListingFiles");

	if(!listingfiles.getElementsByTagName("ul")[0]) {
		
		var info = document.createElement("h4");
		//info.appendChild(document.createTextNode("File queue"));
		
		listingfiles.appendChild(info);
		
		var ul = document.createElement("ul")
		listingfiles.appendChild(ul);
	}
	
	listingfiles = listingfiles.getElementsByTagName("ul")[0];
	
	var li = document.createElement("li");
	li.id = file.id;
	li.className = "SWFUploadFileItem";
	li.innerHTML = file.name + " <span class='progressBar' id='" + file.id + "progress'></span><a id='" + file.id + "deletebtn' class='cancelbtn' href='javascript:swfu.cancelFile(\"" + file.id + "\");'><!-- IE --></a>";

	listingfiles.appendChild(li);
	
	var queueinfo = document.getElementById("queueinfo");
	queueinfo.innerHTML = queuelength + " files queued";
	//RTA document.getElementById(swfu.movieName + "UploadBtn").style.display = "block";
	//RTA document.getElementById("cancelqueuebtn").style.display = "block";
}

function uploadFileCancelled(file, queuelength) {
	var li = document.getElementById(file.id);
	li.innerHTML = file.name + " - cancelled";
	li.className = "SWFUploadFileItem uploadCancelled";
	var queueinfo = document.getElementById("queueinfo");
	queueinfo.innerHTML = queuelength + " files queued";
}

function uploadFileStart(file, position, queuelength) {
	var div = document.getElementById("queueinfo");
	div.innerHTML = "Uploading file " + position + " of " + queuelength;

	var li = document.getElementById(file.id);
	li.className += " fileUploading";
}

function uploadProgress(file, bytesLoaded) {
	console.log(bytesLoaded);
	var progress = document.getElementById(file.id + "progress");
	var percent = Math.ceil((bytesLoaded / file.size) * 200)
	progress.style.background = "#f0f0f0 url(/resources/otherobjects.resources/static/graphics/progress-bar.png) no-repeat -" + (200 - percent) + "px 0";
}

function uploadError(errno) {
	//alert(errno);
	//var li = document.getElementById(file.id);
	//alert(li);
	//li.className = "SWFUploadFileItem uploadFailed";
}

function uploadFileComplete(file) {
	var li = document.getElementById(file.id);
	li.className = "SWFUploadFileItem uploadCompleted";
}

function cancelQueue() {
	swfu.cancelQueue();
	document.getElementById(swfu.movieName + "UploadBtn").style.display = "none";
	document.getElementById("cancelqueuebtn").style.display = "none";
}

function uploadQueueComplete(file) {
	var div = document.getElementById("queueinfo");
	div.innerHTML = "All files uploaded..."
	document.getElementById("cancelqueuebtn").style.display = "none";
}