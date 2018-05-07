(function() {
	'use strict';
	angular
			.module('login', [ 'http-auth-interceptor' ])
			.controller(
					'LoginController',
					function($scope, $state, $http, authService) {
						$scope.submit = function() {
							$http.post('../j_spring_security_check',
										$.param({j_username : $scope.username,j_password : $scope.password}),
										{
											headers : {
													'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
												}
										}).success(function() {
										authService.loginConfirmed();
									});
						};
						$scope.logout = function() {
							$http.get('../j_spring_security_logout');
							window.location.reload(true);
						}
					});
})();