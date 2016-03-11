rootApp.service('signupService', function(httpService) {
	this.submitSignUp = function(data, callback){
		httpService.postRequest('signup/user', data, 'application/json', callback);
	};
	
	
});