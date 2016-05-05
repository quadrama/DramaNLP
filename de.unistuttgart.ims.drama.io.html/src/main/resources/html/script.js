function loadChart() {
	$("#tabs ul").append("<li><a href=\"#hc\">Figure Presence Chart</a></li>");
	$("#tabs").append("<div class=\"hc\" id=\"hc\"></div>");

	$("#hc").highcharts({
		title:{
			text:data['id']
		},
		chart: {
			type:'line',
			zoomType: 'x'
		},
    	xAxis: {
             plotBands: data["plotBands"]
        },
        yAxis: {
        	//categories: speakers
        },
        plotOptions: {
            series: {
            	lineWidth:1
                // connectNulls: false // by default
            }
        },
        tooltip: {
            crosshairs: true,
            headerFormat:"",
            useHTML:true,
            pointFormat: "<div style=\"width:200px;max-width:300px; white-space:normal\"><span style=\"color:{point.color}\">{series.name}</span>: {point.name}</div>"
        },
        series:data["data"]
    });
}

function loadTable() {
	$("#tabs ul").append("<li><a href=\"#speech\">Figure Speech Table</a></li>");
	$("#tabs").append("<div id=\"speech\"><table width=\"100%\"></table></div>");

	$("#tabs table").DataTable( {
	        data: data["data"].map(function(cur, ind, arr) {
				return [
					cur['name'],
					cur['stats']['words'],
					cur['stats']['utterances'],
					Number(cur['stats']['meanUtteranceLength']).toFixed(2)
				]
			}),
	        columns: [
	            { title: "Figure" },
	            { title: "Words" },
	            { title: "Utterances" },
	            { title: "Mean Utt. Length" }
	        ]
	    } );
}

function loadNetwork() {
	$("#tabs ul").append("<li><a href=\"#mentionnetwork\">Mention Network</a></li>");

	$("#tabs").append("<div id=\"mentionnetwork\" style=\"height:400px;\"><div id=\"mnet\"></div></div>");
	$("#mnet").css("height", "100%");
}