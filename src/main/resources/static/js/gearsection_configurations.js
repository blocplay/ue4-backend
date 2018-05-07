(function() {
	'use strict';

	angular.module('ue4App.services').factory('GearSectionConfiguration', function($resource) {
		return $resource('../srest/gearsectionconfigurations/:gscGesId/:gscId', {
			gscGesId : '@gscGesId',			
			gscId : '@gscId'			
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
			'GearSectionConfigurationListController',
			function($scope, $http, $state, $stateParams, popupService, $window, GearSection, GearSectionConfiguration) {
				$scope.gearsectionconfigurations = GearSectionConfiguration.query({
					gscGesId : $stateParams.gscGesId
				});
				
				$scope.gscGesId = $stateParams.gscGesId;

				$scope.gearsection = GearSection.get({
					gesId : $stateParams.gscGesId
				});
				
				$scope.deleteGearSectionConfiguration = function(gearsectionconfiguration) {
					if (popupService.showPopup('Really delete this?')) {
						gearsectionconfiguration.$delete(function() {
							$state.go('gearsection_configurations', {gscGesId:$scope.gscGesId}, {
								reload : true
							});
						});
					}
				};
				
				$scope.updateGearSectionConfiguration = function(gearsectionconfiguration) {
					gearsectionconfiguration.$update({gscGesId:$stateParams.gscGesId},function() {
						$state.go('gearsection_configurations', {gscGesId:$scope.gscGesId}, {
							reload : true
						});
					});
				};

			}).controller('GearSectionConfigurationCreateController',
			function($scope, $state, $stateParams, GearSection, GearSectionConfiguration) {

				$scope.gscGesId = $stateParams.gscGesId; 

				$scope.gearsection = GearSection.get({
					gesId : $stateParams.gscGesId
				});

				$scope.gearsectionconfiguration = new GearSectionConfiguration(); // create new map mode instance.
				// Properties
				// will be set via ng-model on UI

				$scope.addGearSectionConfiguration = function() { // create a new map mode.
					// Issues
					// a POST
					// to /gearsectionconfigurations
					$scope.gearsectionconfiguration.gscGesId = $scope.gscGesId;
					$scope.gearsectionconfiguration.$save(function() {
						$state.go('gearsection_configurations', {gscGesId:$scope.gscGesId}); // on success go back to home
						// i.e.
						// gearsectionconfigurations state.
					});
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('gearsection_configurations', { // state for showing all
			url : '/gearsectionconfigurations/:gscGesId',
			templateUrl : 'partials/gearsection_configuration/list.html',
			controller : 'GearSectionConfigurationListController'
		}).state('newGearSectionConfiguration', { // state for adding 
			url : '/gearsectionconfigurations/:gscGesId/new',
			templateUrl : 'partials/gearsection_configuration/add.html',
			controller : 'GearSectionConfigurationCreateController'
		});
	});
})();
