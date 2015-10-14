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
<script type="text/javascript">
	Global = {
		// the API tag and link data
		localData : ${response},
		// map key: tag, value: APIs
		tag2APIsmap : {},
		pagelayout : {},
		searchContent : '',
		searchRows : 10 
	};

	Request = {
		QueryString : function(item){
			var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]+)","i"));
			return svalue ? decodeURIComponent( svalue[1] ) : svalue;
		}
	};

	function searchCloud() {
		// todo: configurable solr server address
		var url = 'http://127.0.0.1:8080/solr/apidocs/search?wt=json&json.wrf=?&facet=true&facet.field=tags&facet.mincount=1&rows=0';
		Global.searchStart = 0;
		if(!!document.getElementById("searchbox").value) {
			url += '&q=' + document.getElementById("searchbox").value;
		} else {
			url += '&q=*:*';
		}
		url += '&fq=systemName:' + document.getElementById("system").value;
		url += '&fq=branchName:' + document.getElementById("branch").value;
		$.ajax({
				type:'get',
				dataType: "jsonp",
 				contentType:"application/x-www-form-urlencoded; charset=UTF-8",
				url: encodeURI(url),
				success: function(data){
					if(data.response.numFound == 0) {
						alert("道可道，非常道。您搜的东西没找到:(");
					} else {
						handleTagListDisplay(data, Global.pagelayout);
						window.parent.loadTagList(data);
						Global.tag2APIsmap = convertSearchResult(data);
						if(data.facet_counts.facet_fields.tags[0]) {
							//render the first tag
							searchMore(data.facet_counts.facet_fields.tags[0], 0);
							//loadAPIList( Global.tag2APIsmap[data.facet_counts.facet_fields.tags[0]] );				
						}
						if(!!document.getElementById("searchbox").value) {
							Global.searchContent = document.getElementById("searchbox").value;
						} else {
							Global.searchContent = '';
						}
					}
				},
				error: function(e){
					var str = '道哥出错啦！';
					for(var p in e) {
						str += p + "=" + e[p] + ";";
					}
					alert(str);
				}
			});
	};

	function searchMore(tagName, searchStart) {
		var url = 'http://127.0.0.1:8080/solr/apidocs/search?wt=json&json.wrf=?&fq=tags:' + tagName + '&start=' + searchStart + '&rows=' + Global.searchRows;
		if(!!Global.searchContent) {
			url += '&q=' + Global.searchContent;
		} else {
			url += '&q=*:*';
		}
		url += '&fq=systemName:' + document.getElementById("system").value;
		url += '&fq=branchName:' + document.getElementById("branch").value;
		$.ajax({
				type:'get',
				dataType: "jsonp",
 				contentType:"application/x-www-form-urlencoded; charset=UTF-8",
				url: encodeURI(url),
				success: function(data){
					if(data.response.numFound == 0) {
						alert("别再翻了，道哥就这么多了。");
					} else {
						var convertedResult = convertSearchResult(data);
						Global.tag2APIsmap[tagName].APIs = Global.tag2APIsmap[tagName].APIs.concat( convertedResult[tagName].APIs );
						var html = template('APIURLListTmpl', Global.tag2APIsmap[tagName]);
						document.getElementById('APIURLList').innerHTML = html;
						if(searchStart == 0) {
							$("#mainFrame").attr("src",$("#APIDetailLink").length ? $("#APIDetailLink")[0].href : "").trigger("beforeload");
							if($("#APIDetailLink").length && !$("#APIDetailLink")[0].href) {
								$("#APIDetailLink")[0].click();
							}
						}
					}
				},
				error: function(e){
					var str = '道哥出错啦！';
					for(var p in e) {
						str += p + "=" + e[p] + ";";
					}
					alert(str);
				}
			});
	};

	function loadSearchBarOptions(){
		//todo: make solr server address configurable
		var url = 'http://127.0.0.1:8080/solr/apidocs/select?wt=json&json.wrf=?&facet=true&facet.field=systemName&facet.field=branchName';
		$.ajax({
				type:'get',
				dataType: "jsonp",
 				contentType:"application/x-www-form-urlencoded; charset=UTF-8",
				url: encodeURI(url),
				success: function(data){
					var systemSelect = document.getElementById("system");
					systemSelect.options.length = 0;  
					for(var i=0; !!data && !!data.facet_counts && i<data.facet_counts.facet_fields.systemName.length; i+=2){
						if(!!data.facet_counts.facet_fields.systemName[i]) {
							var option = document.createElement("option");
							option.text = data.facet_counts.facet_fields.systemName[i];
							option.value = data.facet_counts.facet_fields.systemName[i];
							systemSelect.appendChild(option);
						}
					}

					var branchSelect = document.getElementById("branch");
					branchSelect.options.length = 0;  
					for(var i=0; !!data && !!data.facet_counts && i<data.facet_counts.facet_fields.branchName.length; i+=2){
						if(!!data.facet_counts.facet_fields.branchName[i]) {
							var option = document.createElement("option");
							option.text = data.facet_counts.facet_fields.branchName[i];
							option.value = data.facet_counts.facet_fields.branchName[i];
							branchSelect.appendChild(option);
						}
					}
				},
				error: function(e){
					var str = '道哥出错啦！';
					for(var p in e) {
						str += p + "=" + e[p] + ";";
					}
					alert(str);
				}
			});
	};

	function loadTagList(data) {
		var html = template('tagListTmpl', data);
		document.getElementById('tagList').innerHTML = html;
	};

	function convertLocalData(localData) {
		// map key: tag, value: APIs
		var tag2APIsmap = {};
		for(var i in localData.docs) {
			tag2APIsmap[localData.docs[i].tag] = localData.docs[i];
		}
		for (var i = 0; i < localData.facet_counts.facet_fields.tags.length; i += 2) {
			tag2APIsmap[ localData.facet_counts.facet_fields.tags[i] ].totalCount = localData.facet_counts.facet_fields.tags[i+1];
		};
		return tag2APIsmap;
	};

	function convertSearchResult(searchResult) {
		var tag2APIsmap = {};
		for(var i in searchResult.response.docs) {
			for(var j in searchResult.response.docs[i].tags) {
				var api = {};
				api.url = searchResult.response.docs[i].APIUrl;
				api.tooltip = searchResult.response.docs[i].tooltip;
				api.methodType = searchResult.response.docs[i].methodType;
				api.pageContent = searchResult.response.docs[i].pageContent;
				api.brief = searchResult.response.docs[i].brief;
				if(!tag2APIsmap[searchResult.response.docs[i].tags[j]]) {
					tag2APIsmap[searchResult.response.docs[i].tags[j]] = { tag: searchResult.response.docs[i].tags[j], APIs: [] };
				}
				tag2APIsmap[searchResult.response.docs[i].tags[j]].APIs.push(api);
			}
		}
		if(searchResult.facet_counts) {
			for (var i = 0; i < searchResult.facet_counts.facet_fields.tags.length; i += 2) {
				if(!tag2APIsmap[ searchResult.facet_counts.facet_fields.tags[i] ]) {
					tag2APIsmap[ searchResult.facet_counts.facet_fields.tags[i] ] = { tag: searchResult.facet_counts.facet_fields.tags[i], APIs: [] };
				}
				tag2APIsmap[ searchResult.facet_counts.facet_fields.tags[i] ].totalCount = searchResult.facet_counts.facet_fields.tags[i+1];
			};
		}
		return tag2APIsmap;
	};

	function loadAPIList(tagAPIs) {
		if(tagAPIs.APIs.length == 0) {
			searchMore(tagAPIs.tag, 0);
		} else {
			var html = template('APIURLListTmpl', tagAPIs);
			document.getElementById('APIURLList').innerHTML = html;
			$("#mainFrame").attr("src",$("#APIDetailLink").length ? $("#APIDetailLink")[0].href : "").trigger("beforeload");
			/* TODO:
			Loading page locally, chrome raises ' Uncaught SecurityError: Blocked a frame with origin "null" from accessing a frame with origin "null". ' when clicking search button.
			*/
			if($("#APIDetailLink").length && !$("#APIDetailLink")[0].href) {
				$("#APIDetailLink")[0].click();
			}
		}
	};

	function loadMainFrame(tag, index) {
		document.getElementById("mainFrame").contentWindow.document.open();
		document.getElementById("mainFrame").contentWindow.document.write(Global.tag2APIsmap[tag].APIs[index].pageContent);
		document.getElementById("mainFrame").contentWindow.document.close();
	};

	function returnLocal() {
		handleTagListDisplay(Global.localData, Global.pagelayout);
		loadTagList(Global.localData);
		Global.tag2APIsmap = convertLocalData(Global.localData);
		if(Object.keys(Global.tag2APIsmap)[0]) {
			//render the first tag
			loadAPIList( Global.tag2APIsmap[Object.keys(Global.tag2APIsmap)[0]] );
		}
	};
	
	function handleTagListDisplay(data, pagelayout) {
		if(data.facet_counts.facet_fields.tags.length == 2) {
			pagelayout.west.children.layout1.close('north');
		} else {
			pagelayout.west.children.layout1.open('north');
		}
	}

	window.onload=function(){
		loadSearchBarOptions();
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
		,	minSize:					50		// ALL panes
		,	west__size:					200
		,	east__size:					200
		,	stateManagement__enabled:	true
		,   north : {
				initClosed:				true
			,	size:					60
		    }

		,	west__childOptions:	{
				minSize:				50		// ALL panes
			,	north__size:			300
			}

		});

		handleTagListDisplay(Global.localData, Global.pagelayout);
	});
</script>
</head>

<body>

<div id="searchBar" class="ui-layout-north">
	<input id="searchbox" type="text"/>

	<label for="system">系统:</label>
	<select name="system" id="system">
	</select>

	<label for="system">代码分支:</label>
	<select name="branch" id="branch">
	</select>

	<input id="searchbtn" value="云端问道" onclick='searchCloud()' type="submit"/>	
	<input id="returnbtn" value="本地取经" onclick='returnLocal()' type="submit"/>	
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