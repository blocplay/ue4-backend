(function() {
	'use strict';

	angular.module('ue4App.services').factory('CustomMapmode', function($resource) {
		return $resource('../srest/custom_mapmodes/:mamSrvId', {
			mamId : '@mamId',			
			mamSrvId : '@mamSrvId'			
		}, {
			update : {
				method : 'PUT'
			}
		});
	}).service('popupService', function($window) {
		this.showPopup = function(message) {
			return $window.confirm(message);
		}
		this.showAlert = function(message) {
			return $window.alert(message);
		}
	});

	angular.module('ue4App.controllers').controller(
			'CustomMapmodeListController',
			function($scope, $http, $state, $stateParams, popupService, $window, Server, CustomMapmode) {
				$scope.mapmodes = CustomMapmode.query({
					mamSrvId : $stateParams.mamSrvId
				});
				
				$scope.mamSrvId = $stateParams.mamSrvId;

				$scope.server = Server.get({
					srvId : $stateParams.mamSrvId
				});
				
				$scope.deleteMapmode = function(mapmode) {
					if (popupService.showPopup('Really delete this?')) {
						mapmode.$delete(function() {
							$state.go('custom_mapmodes', {mamSrvId:$scope.mamSrvId}, {
								reload : true
							});
						});
					}
				};
				
				$scope.updateMapmode = function(mapmode) {
					mapmode.$update(function() {
						$state.go('custom_mapmodes', {mamSrvId:$scope.mamSrvId}, {
							reload : true
						});
					});
				};

				$scope.ableMapmode = function(mapmode, able) {
					mapmode.mamEnabled = able;
					mapmode.$update(function() {
						$state.go('custom_mapmodes', {mamSrvId:$scope.mamSrvId}, {
							reload : true
						});
					});
				};

				$scope.reloadMapCycle = function() {
					return $http.get('../srest/' + $scope.server.srvId +'/reload_cycle')
					.then(function(result) {
						if(result.data.Success)
						{
							popupService.showAlert('Server ' + $scope.server.srvName  + ' has been sent the signal to reload the map cycle!');
						}
						else
						{
							popupService.showAlert('Server ' + $scope.server.srvName  + ' could not be signaled to reload map cycle!');
						}
                    });	
				};

			}).controller('CustomMapmodeCreateController',
			function($scope, $state, $stateParams, Server, CustomMapmode, Gamemap, Gamemode) {

				$scope.gamemaps = Gamemap.query();
				$scope.gamemodes = Gamemode.query();
				$scope.mamSrvId = $stateParams.mamSrvId; 

				$scope.server = Server.get({
					srvId : $stateParams.mamSrvId
				});

				$scope.mapmode = new CustomMapmode(); // create new map mode instance.
				// Properties
				// will be set via ng-model on UI

				$scope.addMapmode = function() { // create a new map mode.
					// Issues
					// a POST
					// to /mapmodes
					$scope.mapmode.mamSrvId = $scope.mamSrvId;
					$scope.mapmode.$save(function() {
						$state.go('custom_mapmodes', {mamSrvId:$scope.mamSrvId}); // on success go back to home
						// i.e.
						// mapmodes state.
					});
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('custom_mapmodes', { // state for showing all mapmodes
			url : '/custom_mapmodes/:mamSrvId',
			templateUrl : 'partials/custom_mapmode/list.html',
			controller : 'CustomMapmodeListController'
		}).state('customNewMapmode', { // state for adding a new mapmode
			url : '/custom_mapmodes/:mamSrvId/new',
			templateUrl : 'partials/custom_mapmode/add.html',
			controller : 'CustomMapmodeCreateController'
		});
	});
})();
