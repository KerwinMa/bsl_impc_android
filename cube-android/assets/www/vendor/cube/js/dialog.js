window.showDialog = window.showDialog || function(dialogId) {
	if(window.scrollers && window.scrollers[dialogId])
		window.scrollers[dialogId].show();
};

window.hideDialog = window.hideDialog || function(dialogId) {
	if(window.scrollers && window.scrollers[dialogId])
		window.scrollers[dialogId].hide();
};

define("dialog", ["jquery", "jqModal"], function($){
	$("dialog").each(function(){
		var _this = $(this);
		var _config = {
		overlay: 88, 
		modal: true,
		trigger: false
			
		};
		//var id = _this.attr('id');
		var atts = this.attributes, len = atts.length, att, i = 0 ;
		for(; i < len ; i++){
			att = atts[i];
			if(att.specified){
				_config[att.name] = att.value;
			}
		}
		var dialogItem = $("<div  class='ui-btn-corner-all jqmWindow' style='margin-top:40%'/>").attr("id", _config.id);
		if(_config.title) {
			var title = $("<div  class='ui-corner-top ui-bar-d ui-header'/>").appendTo(dialogItem);
			title.append($("<h1  class='ui-title'/>").html(_config.title));
		}
		var _content = $("<div class='ui-body-c ui-content ui-corner-bottom' />").appendTo(dialogItem).html(_this.html());
		$("<div style='text-align:center'>确定是否退出？</div>").appendTo(_content);
		$("<div name='ensureBtn' class='ui-btn ui-shadow  ui-btn-up-b ui-btn-corner-all ui-btn-inner' onclick='javascript:do_ensureThing()'>确定</div>").appendTo(_content);
		$("<div name='cancleBtn' class='ui-btn ui-shadow  ui-btn-up-c ui-btn-corner-all ui-btn-inner' onclick='javascript:do_cancleThing()'>取消</div>").appendTo(_content);
		
		dialogItem.appendTo(_this.parent());
		_this.remove();
		
		dialogItem.jqm(_config);
		dialogItem.jqmShow();
		$("div[name='ensureBtn']").focus(function(){
		    $(this).addClass("ui-btn-hover-b"); 
		}).blur(function(){
		    $(this).removeClass("ui-btn-hover-b");
		}).mouseover(function(){
		    $(this).addClass("ui-btn-hover-b"); 
		}).mouseout(function(){
		    $(this).removeClass("ui-btn-hover-b");  
		}).keydown(function(){
		    $(this).removeClass("ui-btn-hover-b");  
		}).keyup(function(){
		    $(this).removeClass("ui-btn-hover-b");  
		});
		$("div[name='cancleBtn']").focus(function(){
		    $(this).addClass("ui-btn-hover-c"); 
		}).blur(function(){
		    $(this).removeClass("ui-btn-hover-c");
		}).mouseover(function(){
		    $(this).addClass("ui-btn-hover-c"); 
		}).mouseout(function(){
		    $(this).removeClass("ui-btn-hover-c");  
		}).keydown(function(){
		    $(this).removeClass("ui-btn-hover-c");  
		}).keyup(function(){
		    $(this).removeClass("ui-btn-hover-c");  
		});
		/*
		dialogItem.appendTo(_this.parent());
		_config.dialogHtml = _this.html();
		if(_config.callback && (typeof (window[_config.callback]) == "function")) {
		  _config.callback = window[_config.callback];
		} else {
			_config.callback = undefined;
		}
		//alert(_this.html());
		_this.remove();
		//alert(_config.setText1);
		$(dialogItem).dialog(_config);
		//$.scrollers[_config.id].show();
		//$(dialogItem).scroller('destroy').scroller(_config).show();
		//alert(_config.id);
		*/
	});
});
function do_ensureThing(){
 alert("ensure");
}
function do_cancleThing(){
alert("cancle");
}