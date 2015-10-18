
	function searchCloud() {
		var url = Global.searchEngine + '/search?wt=json&json.wrf=?&facet=true&facet.field=tags&facet.mincount=1&rows=0';
		Global.searchStart = 0;
		if(!!document.getElementById("searchbox").value) {
			url += '&q=' + document.getElementById("searchbox").value;
		} else {
			url += '&q=*:*';
		}
		url += '&fq=systemName:' + $("#system").find("option:selected").text();
		url += '&fq=branchName:' + $("#branch").find("option:selected").text();
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
				error: function(jqXHR, textStatus, errorThrown){
					if(textStatus === "timeout") {
						alert("道哥超时了，让他歇会再试吧。");
					} else {
						var str = '道哥出错啦！';
						for(var p in errorThrown) {
							str += p + "=" + errorThrown[p] + ";";
						}
						alert(str);
					}
				}
			});
	};

	function searchMore(tagName, searchStart) {
		var url = Global.searchEngine + '/search?wt=json&json.wrf=?&fq=tags:' + tagName + '&start=' + searchStart + '&rows=' + Global.searchRows;
		if(!!Global.searchContent) {
			url += '&q=' + Global.searchContent;
		} else {
			url += '&q=*:*';
		}
		url += '&fq=systemName:' + $("#system").find("option:selected").text();
		url += '&fq=branchName:' + $("#branch").find("option:selected").text();
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
				error: function(jqXHR, textStatus, errorThrown){
					if(textStatus === "timeout") {
						alert("道哥超时了，让他歇会再试吧。");
					} else {
						var str = '道哥出错啦！';
						for(var p in errorThrown) {
							str += p + "=" + errorThrown[p] + ";";
						}
						alert(str);
					}
				}
			});
	};

	function loadSearchBarOptions(){
		var url = Global.searchEngine + '/select?q=*&wt=json&json.wrf=?&rows=0&facet=true&facet.pivot=systemName,branchName';
		$.ajax({
				type:'get',
				dataType: "jsonp",
 				contentType:"application/x-www-form-urlencoded; charset=UTF-8",
				url: encodeURI(url),
				timeout: 1000,
				success: function(data){
					var systemSelect = document.getElementById("system");
					systemSelect.options.length = 0;  
					for(var i=0; !!data && !!data.facet_counts && data.facet_counts.facet_pivot && i<data.facet_counts.facet_pivot[Object.keys(data.facet_counts.facet_pivot)[0]].length; i+=1){
						var sysBranchArr = data.facet_counts.facet_pivot[Object.keys(data.facet_counts.facet_pivot)[0]];
						var option = document.createElement("option");
						option.text = sysBranchArr[i].value;
						option.value = i;
						systemSelect.appendChild(option);
						var branchOptions = [];
						for(var j=0; !!sysBranchArr[i].pivot && j<sysBranchArr[i].pivot.length; j+=1) {
							branchOptions.push(sysBranchArr[i].pivot[j].value);
						}
						Global.searchOptions.push(branchOptions);
					}

					loadBranchOptions(0);
				},
				error: function(jqXHR, textStatus, errorThrown){
					if(textStatus === "timeout") {
						alert("道哥超时了，让他歇会再试吧。");
					} else {
						var str = '道哥出错啦！';
						for(var p in errorThrown) {
							str += p + "=" + errorThrown[p] + ";";
						}
						alert(str);
					}
				}
			});
	};

	function loadBranchOptions(index) {
		var branchSelect = document.getElementById("branch");
		branchSelect.options.length = 0;  
		for(var i=0; !!Global.searchOptions && !!Global.searchOptions[index] && i<Global.searchOptions[index].length; i+=1){
			var option = document.createElement("option");
			option.text = Global.searchOptions[index][i];
			option.value = i;
			branchSelect.appendChild(option);
		}
	}

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