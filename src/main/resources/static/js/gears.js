(function() {
	'use strict';

	angular.module('ue4App.services').factory('Gear', function($resource) {
		return $resource('../srest/gears/:gemId', {
			gemId : '@gemId'
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
					'GearListController',
					function($scope, $state, popupService, $window, Gear, GearSection) {
						$scope.gears = Gear.query();
						$scope.deleteGear = function(gear) {
							if (popupService
									.showPopup('Deleting the gear will remove all history and data from this gear. Really delete gear'
											+ gear.gemName + '?')) {
								gear.$delete(function() {
									$scope.gears = Gear.query();
								});
							}
						};

						$scope.copyGear = function(gear) {
							gear.gemId = null;
							gear.gemName = 'CopyOf' + gear.gemName;
							gear.$save(function(data) {
								$state.go('editGear', {
									gemId : data.gemId
								});
							});
						};
					}).controller('GearCreateController',
					function($scope, $state, $stateParams, Gear, GearSection) {
						$scope.gear = new Gear();

						$scope.gearsections = GearSection.query();

						$scope.getSectionName = function(id) {
							for (var i = 0; i < $scope.gearsections.length; i++)
							{
									if($scope.gearsections[i] && $scope.gearsections[i].gesId === id)
									{
										return $scope.gearsections[i].gesName;
									}
							}
						};

						$scope.addGear = function() {
							$scope.gear.$save(function() {
								$state.go('gears');
							});
						};
					}).controller('GearEditController',
					function($scope, $state, $stateParams, Gear, GearSection) {
						$scope.gearsections = GearSection.query();

						$scope.getSectionName = function(id) {
							for (var i = 0; i < $scope.gearsections.length; i++)
							{
									if($scope.gearsections[i] && $scope.gearsections[i].gesId === id)
									{
										return $scope.gearsections[i].gesName;
									}
							}
						};

						$scope.updateGear = function() {
							$scope.gear.$update();
						};

						$scope.gear = Gear.get({
							gemId : $stateParams.gemId
						});

					});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('gears', { // state for showing all gears
			url : '/gears',
			templateUrl : 'partials/gear/list.html',
			controller : 'GearListController'
		}).state('newGear', { // state for adding a new gear
			url : '/gears/new',
			templateUrl : 'partials/gear/add.html',
			controller : 'GearCreateController'
		}).state('editGear', { // state for updating a gear
			url : '/gears/:gemId/edit',
			templateUrl : 'partials/gear/edit.html',
			controller : 'GearEditController'
		});
	}).run(function($state) {
		$state.go('gears');
	});
})();
