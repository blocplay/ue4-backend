(function() {
	'use strict';

	angular.module('ue4App.services').factory('GearSection', function($resource) {
		return $resource('../srest/gearsections/:gesId', {
			gesId : '@gesId'
		}, {
			update : {
				method : 'PUT'
			}
		});
	}).service('popupService', function($window) {
		this.showPopup = function(message) {
			return $window.confirm(message);
		}
	});

	angular
			.module('ue4App.controllers')
			.controller(
					'GearSectionListController',
					function($scope, $state, popupService, $window, GearSection) {
						$scope.gearsections = GearSection.query();

						$scope.updateGearSection = function(gearSection) {
							return gearSection.$update();
						};

						$scope.deleteGearSection = function(gearSection) {
							if (popupService
									.showPopup('Really delete gear section'
											+ gearSection.gesName + '?')) {
								gearSection.$delete(function() {
									$scope.gearsections = GearSection.query();
								});
							}
						};

					}).controller('GearSectionCreateController',
					function($scope, $state, $stateParams, GearSection) {
						$scope.gearsections = GearSection.query();
						$scope.gearsection = new GearSection();

						$scope.addGearSection = function() {
							$scope.gearsection.$save(function() {
								$state.go('gearsections');
							});
						};
					});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('gearsections', { // state for showing all gear sections
			url : '/gearsections',
			templateUrl : 'partials/gearsection/list.html',
			controller : 'GearSectionListController'
		}).state('newGearSection', { // state for adding a new gear
			url : '/gearsections/new',
			templateUrl : 'partials/gearsection/add.html',
			controller : 'GearSectionCreateController'
		});
	}).run(function($state) {
		$state.go('gearsections');
	});
})();
