var exec = require('cordova/exec'),
	Plugin = function() {};

Plugin.prototype.fromList = function(args, onSuccess, onError) {
	return exec(onSuccess, onError, 'SelectValue', 'fromList', [args]);
};

Plugin.prototype.fromRange = function(args, onSuccess, onError) {
	return exec(onSuccess, onError, 'SelectValue', 'fromRange', [args]);
};

module.exports = new Plugin();
