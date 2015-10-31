<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="css/stylesheet.css" title="Style" />
<link rel="stylesheet" type="text/css" href="css/layout-default-1.4.0.css" title="Style" />
<script type="text/javascript" src="js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.11.4.min.js"></script>
<script type="text/javascript" src="js/jquery.layout-1.4.0.js"></script>
<script type="text/javascript" src="js/template.min.js"></script>
<script type="text/javascript" src="js/wrdoclet.js"></script>
<script type="text/javascript">
	Global = {
		searchEngine : '${searchengine}',
		// the API tag and link data
		localData : ${response},
		//localData : {"docs":[{}],"facet_counts":{"facet_fields":{"tags":[]}}}
		// map key: tag, value: APIs
		tag2APIsmap : {},
		pagelayout : {},
		searchContent : '',
		searchRows : 10,
		searchOptions : []
	};

	Request = {
		QueryString : function(item){
			var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]+)","i"));
			return svalue ? decodeURIComponent( svalue[1] ) : svalue;
		}
	};

	window.onload=function(){
		if(location.host != "winroad-net.github.io") {
			loadSearchBarOptions();
		}
		if(Global.tag2APIsmap[Request.QueryString("tag")]) {
			//render the tag specified in the request
			loadAPIList( Global.tag2APIsmap[Request.QueryString("tag")] );
		} else {
			if(Object.keys(Global.tag2APIsmap)[0]) {
				//render the first tag
				loadAPIList( Global.tag2APIsmap[Object.keys(Global.tag2APIsmap)[0]] );
			}
		}
	};

	$(document).ready(function() {

		Global.pagelayout = $('body').layout({
			center__maskContents:		true	// IMPORTANT - enable iframe masking
		,	minSize:					60		// ALL panes
		,	west__size:					200
		,	east__size:					200
		,	stateManagement__enabled:	true
		,   north : {
				initClosed:				true
			,	size:					82
		    }

		,	west__childOptions:	{
				minSize:				50		// ALL panes
			,	north__size:			300
			}

		});

		handleTagListDisplay(Global.localData, Global.pagelayout);

		$(document).keyup(function(event){
			if(event.keyCode ==13){
				$("#searchbtn").trigger("click");
			}
		});	
	});
</script>
</head>

<body>

<div id="searchBar" class="ui-layout-north">
	<div class="sitename"></div>
	<div class="searchcomponent">
		<input id="searchbox" type="text"/>

		<label for="system">系统:</label>
		<select name="system" id="system" onChange="loadBranchOptions(this.value)">
		</select>

		<label for="system">代码分支:</label>
		<select name="branch" id="branch">
		</select>

		<input id="searchbtn" value="云端问道" onclick='searchCloud()' type="submit"/>	
		<input id="returnbtn" value="本地取经" onclick='returnLocal()' type="submit"/>
	</div>
</div>

<iframe id="mainFrame" name="mainFrame" class="ui-layout-center"
	width="100%" height="600" frameborder="0" scrolling="auto"
	src="">
</iframe>

<div class="ui-layout-west">
	<div class="ui-layout-north">
		<div class="listHeader">标签</div>
		<div id="tagList"></div>
		<script id="tagListTmpl" type="text/html">
		{{each facet_counts.facet_fields.tags as value i}}
			{{if i%2==0 }}
			<ul>
				<li>
					<a onclick="loadAPIList( Global.tag2APIsmap['{{value}}'] )">
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
			loadTagList(Global.localData);
			Global.tag2APIsmap = convertLocalData(Global.localData);
		</script>
	</div>	
	<div class="ui-layout-center">
		<div class="listHeader">接口</div>
		<div id="APIURLList"></div>
		<script id="APIURLListTmpl" type="text/html">
			{{each APIs as value i}}
				<ul>
					<li>
						{{if value.pageContent}}
							<a id="APIDetailLink" title="{{value.tooltip}}" onclick="loadMainFrame('{{tag}}', {{i}});" target="mainFrame">
								{{if value.brief}}
									{{value.brief}} →
								{{/if}}
								{{value.url}}
								{{if value.methodType}}
									[{{value.methodType}}]
								{{/if}}
							</a>
						{{else}}
							<a id="APIDetailLink" title="{{value.tooltip}}" href="{{value.filepath}}" target="mainFrame">
								{{if value.brief}}
									{{value.brief}} → 
								{{/if}}
								{{value.url}}
								{{if value.methodType}}
									[{{value.methodType}}]
								{{/if}}
							</a>
						{{/if}}
					</li>
				</ul>
			{{/each}}
			{{if APIs.length < totalCount }}
				<div id="loadMore" onclick="searchMore('{{tag}}', {{APIs.length}})">加载更多</div>
			{{/if}}
		</script>
	</div>
</div>
</body>
</html>