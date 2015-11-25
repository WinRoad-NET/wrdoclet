
// Apply the ClsButton code to the appropriate INPUT elements:    
$(function(){
    $("input.cls-button").ClsButton({
        clsFn: function(p){
            p.value = '';
        }
    });
});

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
					if(!!document.getElementById("searchbox").value) {
						Global.searchContent = document.getElementById("searchbox").value;
					} else {
						Global.searchContent = '';
					}
					Global.tag2APIsmap = convertSearchResult(data);
					if(data.facet_counts.facet_fields.tags[0]) {
						//render the first tag
						searchMore(data.facet_counts.facet_fields.tags[0], 0);
						//loadAPIList( Global.tag2APIsmap[data.facet_counts.facet_fields.tags[0]] );				
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


$.fn.ClsButton = function(cfg){
    
    return this.each(function(){
        
        // 默认点击按钮操作
        var defaultClsFn = function(p){
            $(p).val('');
        }
        
        this.clsCfg = {
            clsFn:cfg && cfg.clsFn ? cfg.clsFn : defaultClsFn,
            showClass: cfg && cfg.showClass ? cfg.showClass : 'show',
            hideClass: cfg && cfg.hideClass ? cfg.hideClass : 'hide',
            focusClass: cfg && cfg.focusClass ? cfg.focusClass : 'focus', /* 按钮获取焦点时配置 */
            _btn_width: 16,
            _btn_height: 16,
            _btn_focus: false, /* 按钮是否获取焦点 */
            _not_empty: false /* 输入框是否为空 */
        }
        
        /* 按钮初始化状态 */
        if(this.value){
            this.clsCfg._not_empty = true;
            $(this).removeClass(this.clsCfg.hideClass).addClass(this.clsCfg.showClass);
        }else{
            $(this).removeClass(this.clsCfg.showClass).addClass(this.clsCfg.hideClass);
        }
        
        $(this)
        .addClass(cfg && cfg.clsClass ? cfg.clsClass : 'cls-button')
        
        .mousemove(function(e){
            
            if(this.clsCfg._not_empty){
                
                var x = e.pageX || e.x;
                var y = e.pageY || e.y;
                var el = e.target || e.srcElement;
                var btn_focus = (x > coord(el,'offsetLeft') + el.offsetWidth - this.clsCfg._btn_width)
                && (y < coord(el,'offsetTop') + this.clsCfg._btn_height);
                
                if(btn_focus){
                    if(!this.clsCfg._btn_focus){                 
                        $(this).addClass(this.clsCfg.focusClass); /* 按钮获取焦点 */
                        this.clsCfg._btn_focus = true;
                    }
                }else{
                    if(this.clsCfg._btn_focus){
                        this.clsCfg._btn_focus = false;
                        $(this).removeClass(this.clsCfg.focusClass); /* 按钮失去焦点 */
                    }
                }
            }
        })
        
        .mouseout(function(){
            if(this.clsCfg._not_empty){
                this.clsCfg._btn_focus = false;
                
                $(this).removeClass(this.clsCfg.focusClass);
            }
        })
        
        .mousedown(function(e){
            if(this.clsCfg._btn_focus){
                this.clsCfg._btn_focus = false;
                
                $(this).removeClass(this.clsCfg.focusClass);
                this.clsCfg.clsFn(this);
                
                if(!$.browser.msie){ /* 此时,非IE浏览器不会触发input事件 */
                    this.clsCfg._not_empty = false;
                    $(this).removeClass(this.clsCfg.showClass).addClass(this.clsCfg.hideClass);
                }
            }
            return true;
        })
        
        function valueCheck(p){
            
            var el = p.value != undefined ? p: (p.target || p.srcElement);
            
            if(!el.clsCfg._not_empty && el.value){ /* 输入框变为非空 */
                el.clsCfg._not_empty = true;
                $(el).removeClass(el.clsCfg.hideClass).addClass(el.clsCfg.showClass);
            }else if(el.clsCfg._not_empty && !el.value){ /* 输入框变为空 */
                el.clsCfg._not_empty = false;
                $(el).removeClass(el.clsCfg.showClass).addClass(el.clsCfg.hideClass);
            }
        }
        
        if($.browser.msie){
            
            var version = $.browser.version; // 8.0 9.0 ...
            if(version == undefined){                
                version=navigator.appVersion.split(";")[1].replace(/[ ]/g,""); // MSIE8.0 MSIE9.0 ...
            }
            if(version.indexOf('8.0') != -1 || version.indexOf('9.0') != -1){
                
                var el = this;
                window.setInterval(function() {  /* 用定时器弥补IE8/IE9下onpropertychange的bug  */
                    
                    if (!el.clsCfg._not_empty && el.value) { /* 输入框变为非空 */
                        el.clsCfg._not_empty = true;
                        $(el).removeClass(el.clsCfg.hideClass).addClass(el.clsCfg.showClass);
                    } else if (el.clsCfg._not_empty && !el.value) { /* 输入框变为空 */
                        el.clsCfg._not_empty = false;
                        $(el).removeClass(el.clsCfg.showClass).addClass(el.clsCfg.hideClass);
                    }
                },500);
            }else{
                this.attachEvent('onpropertychange', valueCheck);
            }
            
        }else{
            
            this.addEventListener('input', valueCheck, false);
        }
        
    });
    
    function coord(el,prop) {
        var c = el[prop], b = document.body;
        
        while ((el = el.offsetParent) && (el != b)) {
            if (!$.browser.msie || (el.currentStyle.position != 'relative'))
                c += el[prop];
        }
        
        return c;
    }
}	


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
	if(location.host != "") {
		$("#returnbtn").css('display','none'); 
	}
	if(Global.tag2APIsmap[Request.QueryString("tag")]) {
		//render the tag specified in the request
		loadAPIList( Global.tag2APIsmap[Request.QueryString("tag")] );
	} else {
		if(Object.keys(Global.tag2APIsmap)[0] && Object.keys(Global.tag2APIsmap)[0] != "undefined") {
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

	var url = Global.searchEngine + '/select?q=*&wt=json&json.wrf=?&rows=0&facet=true&facet.field=text&facet.mincount=1';
    $( "#searchbox" ).autocomplete({  
        source: function( request, response ) {  
                $.ajax({  
					type:'get',
					contentType:"application/x-www-form-urlencoded; charset=UTF-8",
                    url: encodeURI(url + '&fq=systemName:' + $("#system").find("option:selected").text() + '&fq=branchName:' + $("#branch").find("option:selected").text()),  
                    dataType: "jsonp",  
                    data: {  
                        'facet.prefix': request.term  
                    },  
                    success: function( data ) {  
						var tmp = $.grep( data.facet_counts.facet_fields.text, function(val,key) { 
							return key%2==0;
						});
                        response( tmp );  
                    }  
                });  
            },  
		minLength: 1 
    });  
	
});

var _hmt = _hmt || [];
if(location.host != "") {
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?90ec6e904315589bc394f9562ea3de43";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
};