var colors = [ "#EEF", "#FEE", "#EFE" ];

function loadChart() {
	$("#tabs ul").append("<li><a href=\"#hc\">Figure Presence Chart</a></li>");
	$("#tabs").append("<div class=\"hc\" id=\"hc\"></div>");

	$("#hc")
			.highcharts(
					{
						title : null,
						chart : {
							type : 'line',
							zoomType : 'x'
						},
						xAxis : {
							plotBands : function() {
								var segments;
								if ("seg2" in data["segments"]) {
									segments = data["segments"]["seg2"];
								} else /*if ("seg1" in data["segments"])*/ {
									segments = data["segments"]["seg1"];
								}
								return segments.map(function(cur, i, _) {
									var o = {
											from:cur["begin"],
											to:cur["end"],
											color:colors[i % 3],
											label:{
												text:cur["heading"],
												rotation:270,
												align:"center",
												verticalAlign:"bottom",
												y:-30
											}
										};
									console.log(o);
									return o;
								})
							}
						},
						yAxis : {
						//categories: speakers
						},
						plotOptions : {
							series : {
								lineWidth : 1
							// connectNulls: false // by default
							}
						},
						tooltip : {
							crosshairs : true,
							headerFormat : "",
							useHTML : true,
							pointFormat : "<div style=\"width:200px;max-width:300px; white-space:normal\"><span style=\"color:{point.color}\">{series.name}</span>: {point.name}</div>"
						},
						series : data["data"]
					});
}

function loadTable() {
	$("#tabs ul")
			.append("<li><a href=\"#speech\">Figure Speech Table</a></li>");
	$("#tabs")
			.append("<div id=\"speech\"><table width=\"100%\"></table></div>");

	$("#speech table").DataTable(
			{
				data : data["data"].map(function(cur, ind, arr) {
					return [
							cur['name'],
							cur['stats']['words'],
							cur['stats']['utterances'],
							Number(cur['stats']['meanUtteranceLength'])
									.toFixed(2) ]
				}),
				columns : [ {
					title : "Figure"
				}, {
					title : "Words"
				}, {
					title : "Utterances"
				}, {
					title : "Mean Utt. Length"
				} ]
			});
}

function loadFieldTable() {
	$("#tabs ul").append("<li><a href=\"#fields\">Figures and Semantic Fields</a></li>");
	$("#tabs").append("<div id=\"fields\"><table width=\"100%\"></table></div>");

	$("#fields table").DataTable(
	{
		data : data["field"]["figures"].map(function(cur, ind, arr) {
			return [cur['name']].concat(cur["fields"]);
		}),
		columns : [ {
				title : "Figure"
			} ].concat(data["field"]["fields"].map(function(cur, _, _) {
				return {title:cur};
			}))
	}
	);
}

function loadNetwork() {
	$("#tabs ul").append(
			"<li><a href=\"#mentionnetwork\">Mention Network</a></li>");

	$("#tabs").append(
			"<div id=\"mentionnetwork\" style=\"height:400px;\"></div>");
	
	var width = 960, height = 500;
	var svg = d3.select("#mentionnetwork")
			.append("svg").attr("width", width)
			.attr("height", height);

	var force = d3.layout.force().size(
			[ width, height ]).charge(-400)
			.linkDistance(40);

	force.drag().on("dragstart", dragstart);

	force.nodes(data["network"]["nodes"])
			.links(data["network"]["links"])
			.start();

	var link = svg.selectAll(".link").data(
			data["network"]["links"]).enter()
			.append("line").attr("class",
					"link");

	var node = svg.selectAll(".node").data(
			data["network"]["nodes"]).enter()
			.append("g").attr("class", "node")
			.call(force.drag);

	node.append("circle").attr("r", 12).on(
			"dblclick", dblclick);

	node.append("text").attr("dx", 12).attr(
			"dy", ".35em").style("font-weight",
			"normal").text(function(d) {
		return d.label
	});

	force.on("tick", function() {
		link.attr("x1", function(d) {
			return d.source.x;
		}).attr("y1", function(d) {
			return d.source.y;
		}).attr("x2", function(d) {
			return d.target.x;
		}).attr("y2", function(d) {
			return d.target.y;
		});

		node.attr("transform", function(d) {
			return "translate(" + d.x + ","
					+ d.y + ")";
		});
	});
}

function dblclick(d) {
	d3.select(this).classed("fixed", d.fixed = false);
}

function dragstart(d) {
	d3.select(this).classed("fixed", d.fixed = true);
}