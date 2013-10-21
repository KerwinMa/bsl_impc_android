require(["jquery", "cube/cube-form", "cube/cube-loader", "../com.csair.login/login", "cube/store", "cube/session"], 
		function($, CubeForm, Loader, Login, Store, Session) {

	var form = new CubeForm({id: "login-form"});
	//屏蔽表单自动跳转
	$("#login-form").removeAttr('onSubmit').submit(function(e){
		return false;
	});;

	//1. 初始化rememberme
	var remember ;
	try{ remember = Store.loadObject("rememberme"); } catch (e) {}
	if(remember && remember.username) {
		$("#username").val(remember.username);
		$("#password").val(remember.password);
		$("#rememberme").attr("checked", "checked");
	}
	
	//跳转至主页面
	var loader1 = new Loader({autoshow:false, target:"body", text: "登陆中..."});

	//2. 登陆事件
	$("#login").click(function(){
		$(this).focus();
		var username = $("#username").val();
		var password = $("#password").val();
		//2.1 为空判断
		if( !username || username.trim().length==0 || !password || password.trim().length==0) {
			alert("请输入员工号和密码！");
			return;
		}
		//2.2 记住用户名和密码
		var remembermeVal = $("#rememberme").attr("checked");
		if(remembermeVal == "checked" || remembermeVal =="true") {
			var remembermeObj = {"username":username.trim(), "password":password.trim()};
			Store.saveObject("rememberme",remembermeObj);
		} else {
			Store.deleteObject("rememberme");
		}
		
		//2.3 登陆请求
		loader1.show();
		//Login('743337', '743337@mm', function(){
		Login(username, password, function(){
			// alert("login success");
			loader1.hide();
			alert("login success");
			//跳转至主页面
			Cordova.exec(function(){},function(){}, "LoginPlugin","login",[]);
			// window.location.href="../com.csair.notice/index.html"
			//window.location.href="../com.csair.flightstatus/index.html"

		}, function(){
			loader1.hide();
		});
	});
});
