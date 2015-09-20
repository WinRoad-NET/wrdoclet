<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="css/stylesheet.css" title="Style" />
<link rel="stylesheet" type="text/css" href="css/layout-default-1.4.0.css" title="Style" />
<script type="text/javascript" src="js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.11.4.min.js"></script>
<script type="text/javascript" src="js/jquery.layout-1.4.0.js"></script>
<script type="text/javascript" src="js/template.min.js"></script>
<script type="text/javascript">
	// the API tag and link data
	var data = ${response};
	// map key: tag, value: APIs
	var map = {};

	Request = {
		QueryString : function(item){
			var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]+)","i"));
			return svalue ? decodeURIComponent( svalue[1] ) : svalue;
		}
	}	
		
	function loadAPIList(data) {
		var html = template('APIURLListTmpl', data);
		document.getElementById('APIURLList').innerHTML = html;
		$("#mainFrame").attr("src",$("#APIDetailLink")[0].href).trigger("beforeload");
	};
	
	window.onload=function(){
		if(map[Request.QueryString("tag")]) {
			loadAPIList(map[Request.QueryString("tag")]);
		} else {
			loadAPIList(map[Object.keys(map)[0]]);
		}
	};

	$(document).ready(function() {

		var pagelayout = $('body').layout({
			minSize:					50	// ALL panes
		,	west__size:					200
		,	east__size:					200
		,	stateManagement__enabled:	true
		,   north : {
				initClosed:				true
			,	size:					60
		    }

		,	west__childOptions:	{
				minSize:				50	// ALL panes
			,	north__size:			300
			}

		});

		if(data.facet_counts.facet_fields.tags.length == 2 && data.facet_counts.facet_fields.tags[0] == "default") {
			pagelayout.west.children.layout1.hide('north');
		}

	});
</script>
</head>

<body>

<iframe id="searchBarFrame" name="searchBarFrame" class="ui-layout-north"
	width="100%" frameborder="0" scrolling="auto"
	src="html/searchbar.html">
</iframe>

<iframe id="mainFrame" name="mainFrame" class="ui-layout-center"
	width="100%" height="600" frameborder="0" scrolling="auto"
	src="">
</iframe>

<div class="ui-layout-west">
	<div class="ui-layout-north">
		<h3>
		分类
		</h3>
		<div id="tagList"></div>
		<script id="tagListTmpl" type="text/html">
		{{each facet_counts.facet_fields.tags as value i}}
			{{if i%2==0 }}
			<ul>
				<li>
					<a onclick="loadAPIList(map['{{value}}'])">
						{{value}}
					</a>
			{{else}}
				({{value}})
				</li>
			</ul>
			{{/if}}
		{{/each}}
		</script>
		<script>
		var html = template('tagListTmpl', data);
		document.getElementById('tagList').innerHTML = html;
		
		for(var i in data.docs) {
			map[data.docs[i].tag] = data.docs[i];
		}
		</script>
		</div>	
		<div id="APIURLList" class="ui-layout-center"></div>
		<script id="APIURLListTmpl" type="text/html">
		{{each apis as value}}
			<ul>
				<li>
					<a id="APIDetailLink" href="{{value.filepath}}" target="mainFrame">
						{{value.url}}
					</a>
				</li>
			</ul>
		{{/each}}
		</script>
	</div>

</div>
</body>
</html>