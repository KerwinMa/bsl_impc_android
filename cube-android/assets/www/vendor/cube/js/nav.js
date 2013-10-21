/**
 * 当前页面 .current
 *
 */
define(['require', 'cube/cube-loader', 'jquery', 'jquery.json', 'underscore'], function(require, Loader, $, na, _){

	var Nav = function(config){
		this.config = {
		}
		this.config = $.extend(this.config, config);

		//navigation stack
		if (!$('body').attr('stack')) {
			
			//初始化stack，记录当前页面
			var currentPage = $('div[cube-page]');
			if (currentPage.length == 0) {throw new Error('无法识别当前页面id，请设置cube-page属性')};

			var stack = [currentPage.attr('cube-page')];
			$('body').attr('stack', stack.join(','));
		}

		this.stack = $('body').attr('stack').split(',');

		window.onpopstate = function(event){
			alert(event);
		}
	}

	Nav.prototype.setLeft = function(items) {
		for (var i = 0; i < items.length; i++) {
			var item = items[i];
			this.find("cube").append(item);
		};
	};

	Nav.instance = function(){
		return new Nav();
	}

	Nav.prototype.saveStack = function(){
		$('body').attr('stack', this.stack.join(','));
	}

	/*动画显示某个页面*/
	Nav.prototype.push = function(pageElement){
		if (!pageElement.jquery) {pageElement = $(pageElement)};

		pageElement.css('z-index', this.stack.length + 2);
		pageElement.addClass('cube-page-in');

		if (!pageElement.attr('id')) {throw new Error('页面需要设置id以记录导航堆栈');};
		this.stack.push(pageElement.attr('id'));
		this.saveStack();
	}

	Nav.prototype.pop = function(){
		if (this.stack.length == 1) {
			console.log('已剩最后一页');
			return;
		};
		var topPageId = _.last(this.stack);
		var topPage = $("#" + topPageId);
		topPage.removeClass('cube-page-in');
		topPage.css('z-index', 'auto');

		if (topPage.attr('cube-page')) {topPage.remove()};

		this.stack.pop();
		this.saveStack();
	}

	/*
	 * 动态加载页面到当前页面
	 */
	Nav.prototype.pushPage = function(page, params){

		var me = this;

		var loader = new Loader();

		$.ajax({
			url: page + '.html',

			// if "type" variable is undefined, then "GET" method will be used
			type: 'GET',
			dataType: "html",
			data: params,
			complete: function( jqXHR, status ) {
				// if ( callback ) {
				// 	self.each( callback, response || [ jqXHR.responseText, status, jqXHR ] );
				// }
			}
		}).done(function( responseText ) {

			//将响应文博转换成dom
			var targetPage = $("<div>").append(responseText);

			var pageContent = targetPage.find('.cube-page').addClass('cube-page-ready');
			if (!pageContent || pageContent.length == 0) {throw new Error('页面加载失败：' + page);};
			var scripts = targetPage.find('script');
			var styles = targetPage.find('link');

			//目标页面添加到当前页面
			$("body").append(pageContent);

			//加载CSS

			//页面切换
			pageContent.attr('id', page);
			me.push(pageContent);


			//调用控制器js
			var PageController = require(page);
			if(PageController && PageController.onLoad) {
				PageController.onLoad({
					from: 'a'
				});
			} else {
				console.log('招不到页面控制器：' + page);
			}

			loader.hide();

			// Save response for use in complete callback
			// response = arguments;

			// See if a selector was specified
			/*
			$.html( selector ?

				// Create a dummy div to hold the results
				$("<div>")

					// inject the contents of the document in, removing the scripts
					// to avoid any 'Permission Denied' errors in IE
					.append( responseText.replace( rscript, "" ) )

					// Locate the specified elements
					.find( selector ) :

				// If not, just inject the full result
				responseText );

			*/
		});
	}

	Nav.loadCSS = function(url) {
	    var link = document.createElement("link");
	    link.type = "text/css";
	    link.rel = "stylesheet";
	    link.href = url;
	    document.getElementsByTagName("head")[0].appendChild(link);
	}

	$(".cube-nav-action").each(function(){
		var navButton = $(this);
		navButton.click(function(){
			var tagetPage = navButton.attr('cube-nav-target');
			new Nav();
		});
	});

	return Nav;
});
