function loadChart(id) {
	$("#tabs").append("<div id=\"tab"+index+"\"><h2>Figure presence chart</h2><div class=\"hc\" id=\"hc"+index+"\"></div></div>");

	$("#hc"+id).highcharts({
		title:{
			text:data[id]['id']
		},
		chart: {
			type:'line',
			zoomType: 'x'
		},
    	xAxis: {
             plotBands: data[id]["plotBands"]
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
        series:data[id]["data"]
    });
}

function loadTable(id) {
	
	var dramaData = data[id];

	$("#tab"+id).append("<h2>Figure speech overview</h2><div><table width=\"100%\"></table></div>");

	$("#tab"+id+" table").DataTable( {
	        data: dramaData["data"].map(function(cur, ind, arr) {
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

function loadNetwork(id) {
	$("#tab"+id).append("<h2>Mention Network</h2><div class=\"network\"><div id=\"network"+id+"\" style=\"height:400px;\"></div></div>");
	
}