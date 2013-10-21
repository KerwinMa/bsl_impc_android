define(['underscore'], function(_){
	
	var Base = function(config){
		this.config = {
			observers: []
		};
		this.config = _.extend(this.config, config);
	}

	Base.prototype.method_name = function(first_argument) {
		// body...
	};

	return Base;
});