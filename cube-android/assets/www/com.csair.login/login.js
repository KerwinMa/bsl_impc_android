/**
 * 本方法进行登陆操作，并且把登陆后的用户信息保存道Session Storage中。
 * username: 员工号
 * password: 密码
 * onSuccess: 登陆成功后执行的回调方法。
 * onError: 登陆失败后的回调。
 * 
 * 登陆成功后把用户信息存在session中，key为“loginUser”
 * 格式为：
 * {
		"count":0,
		"email":"chenmingmin@csair.com",
		"instantNotice":"Y",
		"mobile":null,
		"name":"*****",
		"notice":"Y",
		"password":"***",
		"sex":null,
		"username":"*****",
		"address":null,
		"airport":"广州",
		"airportCode":"CAN",
		"configs": {
			"message_setting":{
				"instantNotice":true,
				"instantNoticeVoice":false
			},
			"warning_setting":{
				"standStation":true,
				"planeIdChange":true,
				"less2hour":true,
				"between2and4":true,
				"planeType":true,
				"estimateDeptime":true,
				"great4hour":true,
				"estimateArrtime":true,
				"notice":true,
				"boardingGate":true
			}
		}
 *}
 * 
 */
define(['jquery', "cube/store", "../com.csair.istation/config"], function($, Store, Config){
	var Login = function(username, password, onSuccess, onError){
		$.ajax({
	        block: true,
	        timeout: 20 * 1000,
	        type: "POST",
	        url: (Config.server + "/security/login.inf"),
	        data: {
	            "username" : username,
	            "password" : password,
	            "timestamp" : new Date().getTime()
	        },
	        dataType : "json",
	        success: function(data){
	        	if(data.loginUser != null) {
	        		console.log("login success");
		          	Store.saveObject("user", data.loginUser);
		          	onSuccess();
	        	} else {
	        		console.log("login fail");
	        		var msg = "登陆失败！";
	        		if(data.msg) {
	        			msg += data.mes
	        		} else {
	        			msg += "请检查用户名和密码。";
	        		}
	        		alert(msg);
	        		onError();
	        	}
	        },
	        error: function(xhr, type){
	        	console.log("login fail");
	        	console.log(type + "/" + xhr);
	        	var msg = "登陆失败！";
	    		alert(msg);
	        	onError();
	        }
	    });
	}

	Login.getLoginUser = function() {
		return Store.loadObject("user");
	}

	return Login;
});