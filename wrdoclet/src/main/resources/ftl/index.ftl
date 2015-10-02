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
	// the API tag and link data
	var localData = ${response};
	// map key: tag, value: APIs
	var tag2APIsmap = {};
	var pagelayout;

	Request = {
		QueryString : function(item){
			var svalue = location.search.match(new RegExp("[\?\&]" + item + "=([^\&]+)","i"));
			return svalue ? decodeURIComponent( svalue[1] ) : svalue;
		}
	};

	function searchCloud() {
		var url = 'http://127.0.0.1:8080/solr/apidocs/select?wt=json&json.wrf=?&facet=true&facet.field=tags&facet.mincount=1';
		if(!!document.getElementById("searchbox").value) {
			url = url + '&q=' + document.getElementById("searchbox").value;
		} else {
			url = url + '&q=*:*';
		}
		$.ajax({
				type:'get',
				dataType: "jsonp",
 				contentType:"application/x-www-form-urlencoded; charset=UTF-8",
				url: encodeURI(url),
				success: function(data){
					if(data.response.numFound == 0) {
						alert("没有找到任何记录。可以调整搜索的参数再试试看。")
					} else {
						handleTagListDisplay(data, pagelayout);
						window.parent.loadTagList(data);
						tag2APIsmap = convertSearchResult(data);
						if(data.facet_counts.facet_fields.tags[0]) {
							//render the first tag
							loadAPIList( tag2APIsmap[data.facet_counts.facet_fields.tags[0]] );				
						}
					}
				},
				error: function(e){
					var str = '出错啦！';
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
					var str = '出错啦！';
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
		return tag2APIsmap;
	};

	function convertSearchResult(searchResult) {
		var tag2APIsmap = {};
		for(var i in searchResult.response.docs) {
			for(var j in searchResult.response.docs[i].tags) {
				var api = {};
				api.url = searchResult.response.docs[i].APIUrl;
				api.methodType = searchResult.response.docs[i].methodType;
				api.pageContent = searchResult.response.docs[i].pageContent;
				api.brief = searchResult.response.docs[i].brief;
				if(!tag2APIsmap[searchResult.response.docs[i].tags[j]]) {
					tag2APIsmap[searchResult.response.docs[i].tags[j]] = { tag: searchResult.response.docs[i].tags[j], apis: [] };
				}
				tag2APIsmap[searchResult.response.docs[i].tags[j]].apis.push(api);
			}
		}
		return tag2APIsmap;
	};

	function loadAPIList(APIs) {
		var html = template('APIURLListTmpl', APIs);
		document.getElementById('APIURLList').innerHTML = html;
		$("#mainFrame").attr("src",$("#APIDetailLink").length ? $("#APIDetailLink")[0].href : "").trigger("beforeload");
		/* TODO:
		Loading page locally, chrome raises ' Uncaught SecurityError: Blocked a frame with origin "null" from accessing a frame with origin "null". ' when clicking search button.
		*/
		if($("#APIDetailLink").length && !$("#APIDetailLink")[0].href) {
			$("#APIDetailLink")[0].click();
		}
	};

	function loadMainFrame(tag, index) {
		//$('#mainFrame').contents().find('html').html(tag2APIsmap[tag].apis[index].pageContent);
		document.getElementById("mainFrame").contentWindow.document.open();
		document.getElementById("mainFrame").contentWindow.document.write(tag2APIsmap[tag].apis[index].pageContent);
		document.getElementById("mainFrame").contentWindow.document.close();
	};

	function returnLocal() {
		handleTagListDisplay(localData, pagelayout);
		loadTagList(localData);
		tag2APIsmap = convertLocalData(localData);
		if(Object.keys(tag2APIsmap)[0]) {
			//render the first tag
			loadAPIList( tag2APIsmap[Object.keys(tag2APIsmap)[0]] );				
		}
	};
	
	function handleTagListDisplay(data, pagelayout) {
		if(data.facet_counts.facet_fields.tags.length == 2 && data.facet_counts.facet_fields.tags[0] == "default") {
			pagelayout.west.children.layout1.hide('north');
		} else {
			pagelayout.west.children.layout1.show('north');
		}
	}

	window.onload=function(){
		loadSearchBarOptions();
		if(tag2APIsmap[Request.QueryString("tag")]) {
			//render the tag specified in the request
			loadAPIList( tag2APIsmap[Request.QueryString("tag")] );
		} else {
			if(Object.keys(tag2APIsmap)[0]) {
				//render the first tag
				loadAPIList( tag2APIsmap[Object.keys(tag2APIsmap)[0]] );				
			}
		}
	};

	$(document).ready(function() {

		pagelayout = $('body').layout({
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

		handleTagListDisplay(localData, pagelayout);
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

	<input id="searchbtn" value="搜索云端" onclick='searchCloud()' type="submit"/>	
	<input id="returnbtn" value="回到本地" onclick='returnLocal()' type="submit"/>	
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
					<a onclick="loadAPIList( tag2APIsmap['{{value}}'] )">
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
			loadTagList(localData);
			tag2APIsmap = convertLocalData(localData);
		</script>
	</div>	
	<div class="ui-layout-center">
		<div class="listHeader">接口</div>
		<div id="APIURLList"></div>
		<script id="APIURLListTmpl" type="text/html">
			{{each apis as value i}}
				<ul>
					<li>
						{{if value.pageContent}}
							<a id="APIDetailLink" onclick="loadMainFrame('{{tag}}', {{i}});" target="mainFrame">
								{{if value.brief}}
									{{value.brief}} →
								{{/if}}
								{{value.url}}
								{{if value.methodType}}
									[{{value.methodType}}]
								{{/if}}
							</a>
						{{else}}
							<a id="APIDetailLink" href="{{value.filepath}}" target="mainFrame">
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
		</script>
	</div>
</div>
</body>
</html>