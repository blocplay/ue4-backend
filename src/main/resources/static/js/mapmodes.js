(function() {
	'use strict';

	angular.module('ue4App.services').factory('Mapmode', function($resource) {
		return $resource('../srest/mapmodes/', {
			mamId : '@mamId'
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
			'MapmodeListController',
			function($scope, $http, $state, popupService, $window, Mapmode) {
				$scope.mapmodes = Mapmode.query();

				$scope.deleteMapmode = function(mapmode) {
					if (popupService.showPopup('Really delete this?')) {
						mapmode.$delete(function() {
							$state.go('mapmodes', {}, {
								reload : true
							});
						});
					}
				};
				
				$scope.updateMapmode = function(mapmode) {
					mapmode.$update(function() {
						$state.go('mapmodes', {}, {
							reload : true
						});
					});
				};

				$scope.ableMapmode = function(mapmode, able) {
					mapmode.mamEnabled = able;
					mapmode.$update(function() {
						$state.go('mapmodes', {}, {
							reload : true
						});
					});
				};

				$scope.reloadMapCycle = function() {
					return $http.post('../srest/reload_cycle').success(
							function(data, status, headers, config) {
								popupService.showAlert('Map cycle reloaded in all visible servers using global map cycle');
							}).error(function(data, status, headers, config) {
						//alert('Error!');
					});
				};

			}).controller('MapmodeCreateController',
			function($scope, $state, $stateParams, Mapmode, Gamemap, Gamemode) {

				$scope.gamemaps = Gamemap.query();
				$scope.gamemodes = Gamemode.query();

				$scope.mapmode = new Mapmode(); // create new map mode instance.
				// Properties
				// will be set via ng-model on UI

				$scope.addMapmode = function() { // create a new map mode.
					// Issues
					// a POST
					// to /mapmodes
					$scope.mapmode.$save(function() {
						$state.go('mapmodes'); // on success go back to home
						// i.e.
						// mapmodes state.
					});
				};
			}).controller('MapmodeEditController',
			function($scope, $state, $stateParams, Mapmode) {
				$scope.updateMapmode = function() { // Update the edited map
					// mode.
					// Issues a PUT to
					// /mapmodes/:id
					$scope.mapmode.$update(function() {
						$state.go('mapmodes'); // on success go back to home
						// i.e.
						// mapmodes state.
					});
				};

				$scope.loadMapmode = function() { // Issues a GET request to
					// /mapmodes/:id to get a map mode to
					// update
					$scope.mapmode = Mapmode.get({
						mamMapId : $stateParams.mamMapId,
						mamGamId : $stateParams.mamGamId,
						mamAiEnabled : $stateParams.mamAiEnabled
					});
				};

				$scope.loadMapmode(); // Load a map mode which can be edited
				// on UI
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('mapmodes', { // state for showing all mapmodes
			url : '/mapmodes',
			templateUrl : 'partials/mapmode/list.html',
			controller : 'MapmodeListController'
		}).state('newMapmode', { // state for adding a new mapmode
			url : '/mapmodes/new',
			templateUrl : 'partials/mapmode/add.html',
			controller : 'MapmodeCreateController'
		});
	}).run(function($state) {
		$state.go('mapmodes'); // make a transition to mapmodes state when app
		// starts
	});
})();
