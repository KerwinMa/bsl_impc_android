/**
 * HTML5本地存储模块，DB实例
 */
define(['zepto', 'jquery.json'], function($){

	var Store = function(){
		
	}

	Store.saveObject = function(key, object) {
		window.localStorage[key] = $.toJSON(object);
	}

	Store.loadObject = function(key) {
		var objectString =  window.localStorage[key];
		return objectString == null ? null : $.evalJSON(objectString);
	}

	Store.deleteObject = function(key) {
		window.localStorage[key] = null;
	}

	return Store;
});