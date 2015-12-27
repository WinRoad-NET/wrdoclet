<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="shortcut icon" href="img/favicon.ico"/>
<link rel="bookmark" href="img/favicon.ico"/>
<link rel="stylesheet" type="text/css" href="css/stylesheet.css" title="Style" />
<link rel="stylesheet" type="text/css" href="css/layout-default-1.4.0.css" title="Style" />
<link rel="stylesheet" type="text/css" href="css/jquery-ui-1.12.0-pre.css" title="Style" />
<script type="text/javascript" src="js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="js/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.11.4.min.js"></script>
<script type="text/javascript" src="js/jquery.layout-1.4.0.min.js"></script>
<script type="text/javascript" src="js/template.min.js"></script>
<script type="text/javascript" src="js/wrdoclet.min.js"></script>
<script type="text/javascript">
	Global = {
		searchEngine : '${searchengine}',
		// the API tag and link data
		localData : ${response},
		//localData : {"docs":[{}],"facet_counts":{"facet_fields":{"tags":[]}}},
		// map key: tag, value: APIs
		tag2APIsmap : {},
		pagelayout : {},
		searchContent : '',
		searchBranch : '',
		searchSystem : '',
		searchRows : 10,
		searchOptions : []
	};
</script>
</head>
<body>

<div id="searchBar" class="ui-layout-north">
	<div class="sitename"></div>
	<div class="help"><a href="http://wrdoclet.winroad.net/" target="_blank">help</a></div>
	<div class="searchcomponent">
		<input id="searchbox" type="text" class="cls-button" />
		<label for="tagfilter" id="tagfilterlabel">过滤标签:</label>
		<input name="tagfilter" id="tagfilter" type="text" />
		<label for="system">系统:</label>
		<select name="system" id="system" onChange="document.getElementById('searchbox').value='';loadBranchOptions(this.value)">
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