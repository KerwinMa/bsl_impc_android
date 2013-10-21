define(function(require){
	var Cube = {};

	Cube.List = require('cube/cube-list');
	Cube.Nav = require('cube/nav');
	Cube.Segment = require('cube/cube-segment');
	Cube.Loader = require('cube/cube-loader');
	Cube.Dialog = require('cube/cube-dialog');
	Cube.Form = require('cube/cube-form');
	Cube.DS = require('cube/cube-ds');
	Cube.Store = require('cube/store');

	//检查是否登陆
	$(document).ajaxError(function(event, jqXHR, options, thrownError){
		var text = jqXHR.responseText;
		var status = jqXHR.status;
		var errorType = thrownError.type;
		if(status == 200 && errorType == "unexpected_token" && text && text.indexOf("<html>")>=0){
			new Cube.Dialog({autoshow : true, target: 'body', title: '登陆提示',content: "请您先登陆再操作！"},
				{configs:[{title:'确定',eventName:'ok'}],
					ok:function(){
			 			window.location.href="../com.csair.login/index.html";
					}
				}
			);
		}
	});
	return Cube;
});