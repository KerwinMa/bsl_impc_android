/*
 * 列表组件，最终转换出html5
 * <div id="passenger-list">
 * 	<div class="contentScroller">
 *	</div>
 * </div>
 *
 * 下拉刷新 
 * http://cubiq.org/dropbox/iscroll4/examples/pull-to-refresh/
 */
 define(['zepto', 'jquery.tmpl', 'underscore', 'cube/cube-loader', 'cube/cache', 'iscroll.lite'], function($, na, _, Loader, Cache, iScroll){

	//判断是否已替换，来判断是否已经构造完成
	var List = function(config) {
		this.config = {
			/*提取到父类*/
			observers: [],
			/*自有*/
			autoLoad: true,
			pageParam: 'page',
			pageSizeParam: 'pageSize',
			page: 1,
			pageSize: 10,
			pullDownEnable: false,
			pagingEnable: true,
			iScroll: true,
			method: 'GET'
		}
		this.config = $.extend(this.config, config);

		this.requestParams = {};

		this.parseConfig($("#" + config.id).get(0));

		this.generatedCount = 0;

		if (!this.rendered()) {this.render()};
	}

	/*
	 * 
	 * 不同的子标签用不同的handler解析，handler作为实例方法存在，可继承
	 * 
	 */
	List.prototype.parseConfig = function(element) {

		var me = this;

		var jqObject = $(element);
		this.config['id']			= jqObject.attr('id');
		this.config['itemTemplate']	= jqObject.attr('itemTemplate');
		this.config['moreItemElement']	= jqObject.attr('moreItemElement');
		var url = jqObject.attr('url');
		if(url && url != "") {
			this.config['url']			= url;
		}
		this.config['jsonRoot']		= jqObject.attr('jsonRoot');
		this.config['class']		= jqObject.attr('class');
		this.config['paging']		= jqObject.attr('paging');

		// var cfg = {};
		// _.map(jqObject.get(0).attributes, function(attr){
		// 	cfg[attr.nodeName] = attr.nodeValue;
		// });

		// this.config = _.extend(this.config, cfg);

		jqObject.children().each(function(index, element){
			//find suitable handler
			var handler = me[element.tagName.toLowerCase() + 'TagHandler'];
			if (_.isFunction(handler)) {
				handler.apply(me, [element]);
			};
		});
	};

	List.prototype.setRequestParams = function(params) {
		this.requestParams = _.extend(this.requestParams, params);
		this.reload();
	}

	//此组件是否已渲染
	List.prototype.rendered = function() {
		console.log("Rendered:" + (document.getElementById(this.config.id).nodeName == "DIV"));
		return document.getElementById(this.config.id).nodeName == "DIV";
	}

	//内容容器的ID，由列表id加后缀生成
	List.prototype.contentId = function() {
		return this.config['id'] + '-content';
	}

	//渲染组件
	List.prototype.render = function() {

		console.log("Cube:List.render");

		var me = this;

		$('.cube-list-item').live('click', function(){
			var index = $(this).attr('index');
			var CACHE_ID = 'cube-list-' + me.config['id'];
			if(Cache.get(CACHE_ID)){
				var olddata = Cache.get(CACHE_ID);
				var data = olddata[index];
				if(me.config.onSelect) {
					me.config.onSelect(data);
				}
			}
		});
		
		var list = $('#' + this.config['id']);
		var target_list = $("<div/>").attr('class', this.config['class']);
		var target_contentScroller = $("<div/>").addClass("contentScroller");
		target_list.append(target_contentScroller);
		target_list.insertBefore(list);
		list.remove();
		target_list.attr('id', this.config['id']);

		$('<div id="' + me.contentId() + '"></div>').appendTo(target_contentScroller);

		//iScroll
		if(me.config.iScroll){
			var list_iScroll = new iScroll(this.config['id'], {
				useTransition: true
			});
			this.iScroll = list_iScroll;
		}

		if (this.config.autoLoad) {this.reload()};
	}

	List.prototype.reload = function(){
		this.config.page = 1;
		this.loadNextPage();
	}

	List.prototype.loadNextPage = function(){

		var me = this;

		me.requestParams[me.config.pageParam] = this.config['page'];
		me.requestParams['pageSize'] = this.config['pageSize'];

		
		var itemTemplateName = this.config['itemTemplate'];
		var moreItemElementName = this.config['moreItemElement'];
		var paging = this.config['paging'];

		var loader = new Loader({text:"查询中..."});
		$.ajax({
			block: true,
			timeout: 20 * 1000,
			traditional:true,
			url: me.config['url'],
			type: me.config['method'],
			data: me.requestParams,
			dataType : "json",
			success: function(data, textStatus, jqXHR){
				console.log('列表数据加载成功：' + textStatus + " response:[" + data + "]");

				var jsonRoot = data;
				_.each(me.config['jsonRoot'].split('.'), function(element){
					jsonRoot = jsonRoot[element];
				});

				//编译一下
				$("#" + itemTemplateName).template(itemTemplateName);

				//clear list
				var contentScroller = $("#" + me.config['id']).find(".contentScroller");

				//将list的配置保存在div里
				$("#" + me.config['id']).attr('list-config',$.toJSON(me.config));

				if(me.config['page'] == 1) {
					contentScroller.find("li").remove();
				}

				//append
				if(null==jsonRoot.length){
				    $.tmpl(itemTemplateName, jsonRoot).appendTo(document.getElementById(me.contentId()));
				}else{
					for (var i = 0; i < jsonRoot.length; i++) {
						var item = jsonRoot[i];
						// item.index = i;
						var li = $("<li/>");
						li.addClass('cube-list-item');
						li.attr('index', (me.config['page'] - 1) * me.config['pageSize'] + i);
						li.append($.tmpl(itemTemplateName, item));
						li.appendTo(document.getElementById(me.contentId()));
					}

					if(document.getElementById(me.config['id'] + '-more')){
						$('#' + me.config['id'] + '-more').remove();
					}

					//加载更多按钮
					if(paging=='true' && me.config['pageSize']==jsonRoot.length){
						//加上一个加载更多的cell
						var moreLi = $("<li/>");
						moreLi.addClass('cube-list-item-more');
						moreLi.attr('id', (me.config['id'] + '-more'));
						moreLi.appendTo(document.getElementById(me.contentId()));
						if(moreItemElementName!=null){
							$("#" + moreItemElementName).template(moreItemElementName);
							moreLi.append($.tmpl(moreItemElementName,null));
						}else{
	  						defalutMoreItemDiv = $("<div>更多...</div>");
	  						moreLi.append(defalutMoreItemDiv);
						}
						moreLi.click(function(){
							me.loadNextPage();
						});
					}

				}

				//iScroll
				if(me.config.iScroll){
					me.iScroll.refresh();
				}

				var data = jsonRoot;
				var CACHE_ID = 'cube-list-' + me.config['id'];
				if(Cache.get(CACHE_ID)){
					var olddata = Cache.get(CACHE_ID);
					data = olddata.concat(jsonRoot);
				}

				Cache.put(CACHE_ID, data);

				//update current number
				me.config.page = me.config.page + 1;
				loader.hide();
			},
			error: function(e, xhr, type){
				console.error('列表数据加载失败：' + e + "/" + type + "/" + xhr);
				loader.hide();
			}
		});
	}

	/*observer解析器*/
	List.prototype.observerTagHandler = function(element) {

		var me = this;

		var eventName = $(element).attr('event');
		var targetName = $(element).attr('target');
		var actionName = $(element).attr('action');

		// this.config.observers.push({
		// 	event: eventName,
		// 	target: targetName,
		// 	action: actionName
		// });

		console.log('注册监听器:' + targetName + '.' + eventName + ' -> ' + actionName);
		$(targetName).on(eventName, function(e, params){
			me[actionName].apply(me, [params]);
		});
	};

	//要有一个稳固的纯js组件，标签属于扩展功能，标签的行为是解析标签的属性，作为参数传入组件的构造函数里
	List.renderAll = function() {
		$("list").each(function(){
			new List({id: $(this).attr("id")});
		});
	}

	return List;
});