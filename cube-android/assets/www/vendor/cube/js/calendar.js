window.showDialog = window.showDialog || function(dialogId) {
	if(window.scrollers && window.scrollers[dialogId])
		window.scrollers[dialogId].show();
};

window.hideDialog = window.hideDialog || function(dialogId) {
	if(window.scrollers && window.scrollers[dialogId])
		window.scrollers[dialogId].hide();
};

define(["jquery", "cube/jqModal", "cube/cal_main"], function($){
	$("calendar").each(function(){
		var _this = $(this);
		var _config = {
			modal: true
		};
		//var id = _this.attr('id');
		var atts = this.attributes, len = atts.length, att, i = 0 ;
		for(; i < len ; i++){
			att = atts[i];
			if(att.specified){
				_config[att.name] = att.value;
			}
		}

		if(! _config.name) {
			_config.name =  _config.id;
		}
		if(! _config.style) {
			_config.style = "background-color: #F7F7F7;";
		}
		var dateItem = $("<input  style='"+_config.style+"' />").prop('readonly', true).prop("id", _config.id).prop("name",_config.name);
		
		if( _config["class"]) {
			dateItem.prop("class",_config["class"]);
		}

		var jqmWindowDiv;
		var cal_container = $("#cal_container");

		if(cal_container.size() == 0) {
			jqmWindowDiv = $('<div id="cal_jqmWindowDiv" class="jqmWindow"></div>').prependTo("body");
			cal_container = $('<div class="cal_container" id="cal_container" ></div>').appendTo(jqmWindowDiv);	
			var callback = function(day) {
				jqmWindowDiv.jqmHide();
				$("#"+cal_container.attr("cal_input_id")).val(day);
				//jqmWindowDiv.jqmHide();
				//dateItem.val(day);
			};
			cal_container.init_cal(null, callback);
			$(window).resize(
				function() {
					cal_container.init_cal(null, callback);
				}
			);
		}
		else {
			jqmWindowDiv =  $("#cal_jqmWindowDiv");
		}

		
		

		
		//alert("vvv1");
		dateItem.appendTo(_this.parent());
		//_config.dialogHtml = _this.html();
		
		//alert(_this.html());
		_this.remove();
		//alert(_config.setText1);
		jqmWindowDiv.jqm(_config);
		dateItem.click(function () {
			cal_container.attr("cal_input_id", _config.id);
			jqmWindowDiv.jqmShow();			
		});
		//$.scrollers[_config.id].show();
		//$(dateItem).scroller('destroy').scroller(_config).show();
		//alert(_config.id);
	});
});
