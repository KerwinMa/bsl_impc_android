/**
 * HTML5 Session存储模块，Session DB实例
 * 
 */
define(['zepto', 'jquery.json'], function($){

	var Session = function(){
		
	}

	Session.saveObject = function(key, object) {
		window.sessionStorage[key] = $.toJSON(object);
	}

	Session.loadObject = function(key) {
		var objectString =  window.sessionStorage[key];
		return objectString == null ? null : $.evalJSON(objectString);
	}

	Session.deleteObject = function(key) {
		window.sessionStorage[key] = null;
	}

	return Session;
});