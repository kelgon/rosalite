agentList();

function agentList() {
	$("#loading").modal();
	$.ajax({
		url: "s?bn=ams&mn=agentList", 
		method: "get",
		cache: false, 
		dataType: "json",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		success: function(data) {
			$("#loading").modal("hide");
			if(data.code == "0") {
				var target = $("#agents");
				for(var i=0; i<data.obj.length; i++) {
					var agent = data.obj[i];
					var agTitle = $("<a data-toggle='collapse'></a>");
					agTitle.append("["+agent.name+"] "+agent.host+"(pid "+agent.pid+"). Status: <span class='"
							+getColor(agent.status)+"'>"+agent.status.toUpperCase()+"</span> (last heartbeat <span class='"
							+getColor(agent.lastHeartbeatDiff)+"'>"+formatTimeDiff(agent.lastHeartbeatDiff)+"</span> ago)");
					agTitle.attr("href", "#col_"+agent.name);
					var h4 = $("<h4 class='panel-title'></h4>");
					h4.append(agTitle);
					var head = $("<div class='panel-heading' id='hd_"+agent.name+"'></div>");
					head.append(h4);
					var col = $("<div id='col_"+agent.name+"' class='panel-collapse collapse in'></div>");
					var ul = $("<ul></ul>")
					for(var j=0; j<agent.trackers.length; j++) {
						var tracker = agent.trackers[j];
						ul.append("<li>Tracker No."+tracker.no+" tracks: <span class='bg-info'>"+tracker.logpath
								+tracker.logfile+"</span> (last submit <span class='"+getColor(tracker.lastModifiedDiff)+"'>"
								+formatTimeDiff(tracker.lastModifiedDiff)+"</span> ago). Status: <span class='"
								+getColor(tracker.status)+"'>"+tracker.status.toUpperCase()+"</span> (last heartbeat "
								+"<span class='"+getColor(tracker.lastHeartbeatDiff)+"'>"
								+formatTimeDiff(tracker.lastHeartbeatDiff)+"</span> ago)"+"</li>");
					}
					var colBody = $("<div class='panel-body'></div>");
					colBody.append(ul);
					col.append(colBody);
					var agDiv = $("<div class='panel panel-default'></div>");
					agDiv.append(head);
					agDiv.append(col);
					$("#agents").append(agDiv);
				}
			}
		}
	});
}

function formatTimeDiff(diff) {
	if(Number(diff) < 1000)
		return diff+"ms";
	else {
		return Math.floor(Number(diff)/1000)+"s";
	}
}

function getColor(input) {
	if(typeof input=="string") {
		if(input.toUpperCase() == "RUNNING")
			return "bg-primary";
		if(input.toUpperCase() == "STOPPED")
			return "bg-danger";
	}
	if(typeof input=="number") {
		if(input > 1000*60*10)
			return "text-danger";
		else
			return "text-success";
	}
}