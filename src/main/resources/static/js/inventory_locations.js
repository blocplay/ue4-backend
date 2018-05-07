(function() {
	'use strict';

	angular.module('ue4App.services').factory('InventoryLocation', function($resource) {
		return $resource('../srest/inventory_locations/:inlId', {
			inlId : '@inlId'
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

	angular.module('ue4App.controllers').controller('InventoryLocationListController',
			function($scope, $state, popupService, $window, InventoryLocation) {
				$scope.inventoryLocations = InventoryLocation.query(); // fetch all inventory locations.
														// Issues a GET to /inventory_locations

				$scope.updateInventoryLocation = function(inventoryLocation) {
					// Update the edited game mode.
					// Issues a PUT to /inventory_locations/:id
					return inventoryLocation.$update();
				};

				$scope.deleteInventoryLocation = function(inventoryLocation) {
					if (popupService.showPopup('Really delete this?')) {
						inventoryLocation.$delete(function() {
							$state.go('inventory_locations', {},{ reload: true });
						});
					}
				};
			}).controller('InventoryLocationCreateController',
			function($scope, $state, $stateParams, InventoryLocation) {
				$scope.inventoryLocation = new InventoryLocation();

				$scope.addInventoryLocation = function() {
					$scope.inventoryLocation.$save(function() {
						$state.go('inventory_locations');
					});
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('inventory_locations', { // state for showing all inventory locations
			url : '/inventory_locations',
			templateUrl : 'partials/inventory_location/list.html',
			controller : 'InventoryLocationListController'
		}).state('newInventoryLocation', { // state for adding a new inventory location
			url : '/inventory_locations/new',
			templateUrl : 'partials/inventory_location/add.html',
			controller : 'InventoryLocationCreateController'
		});
	}).run(function($state) {
		$state.go('inventory_locations'); // make a transition to inventory locations state when app
								// starts
	});
})();
