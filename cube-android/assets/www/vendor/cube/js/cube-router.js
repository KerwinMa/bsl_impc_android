require(function(){

	var Router = function(){}

	Router.prototype.param = function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); 
		var r = window.location.search.substr(1).match(reg); 
		if (r != null) {
			return unescape(r[2]); 
		} else {
			return null; 
		}
	}

	return Router;
});