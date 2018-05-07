(function() {
	'use strict';

	angular.module('ue4App.services').factory('ActiveServer', function($http) {
		return {
			getActiveServers: function() {
	             //return the promise directly.
	             return $http.get('../srest/servers_status')
	                       .then(function(result) {
	                            //resolve the promise as the data
	                            return result.data;
	                        });				
			}
		}
	}).service('popupService', function($window) {
		this.showPopup = function(message) {
			return $window.confirm(message);
		}
		this.showAlert = function(message) {
			return $window.alert(message);
		}
	});

	angular.module('ue4App.controllers').controller('ActiveServerListController',
			function($scope, $http, $interval, popupService, ActiveServer) {
				ActiveServer.getActiveServers().then(function(activeServers){
					$scope.activeServers = activeServers;
				});
				
				$interval(function(){
					ActiveServer.getActiveServers().then(function(activeServers){
						$scope.activeServers = activeServers;
					});
				},5000);

				$scope.stopMap = function(server) {
					if (popupService.showPopup('Stop ' + server[1]  + '\'s current match?')) {
						$http.get('../srest/' + server[2] +'/stop_map')
							.then(function(result) {
								if(result.data.Success)
								{
									popupService.showAlert('Server ' + server[1]  + ' has been sent the signal to restart!');
								}
								else
								{
									popupService.showAlert('Server ' + server[1]  + ' could not be signaled to restart!');
								}
	                        });	
					}
				};

				$scope.reloadMapCyle = function(server) {
					if (popupService.showPopup('Reload ' + server[1]  + '\' map cycle?')) {
						$http.get('../srest/' + server[2] +'/reload_cycle')
							.then(function(result) {
								if(result.data.Success)
								{
									popupService.showAlert('Server ' + server[1]  + ' has been sent the signal to reload the map cycle!');
								}
								else
								{
									popupService.showAlert('Server ' + server[1]  + ' could not be signaled to reload map cycle!');
								}
	                        });	
					}
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('active_servers', { // state for showing all servers
			url : '/active_servers',
			templateUrl : 'partials/active_server/list.html',
			controller : 'ActiveServerListController'
		});
	});
})();
