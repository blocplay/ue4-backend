(function() {
	'use strict';

	angular.module('ue4App.services').factory('InventoryObject', function($resource) {
		return $resource('../srest/inventory_objects/:inoId', {
			inoId : '@inoId'
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

	angular.module('ue4App.controllers').controller('InventoryObjectListController',
			function($scope, $state, popupService, $window, InventoryObject) {
				$scope.inventoryObjects = InventoryObject.query(); // fetch all game modes.
														// Issues a GET to /inventory_objects

				$scope.updateInventoryObject = function(inventoryObject) {
					// Update the edited game mode.
					// Issues a PUT to /inventory_objects/:id
					return inventoryObject.$update();
				};

				$scope.deleteInventoryObject = function(inventoryObject) {
					if (popupService.showPopup('Really delete this?')) {
						inventoryObject.$delete(function() {
							$state.go('inventory_objects', {},{ reload: true });
						});
					}
				};
			}).controller('InventoryObjectCreateController',
			function($scope, $state, $stateParams, InventoryObject) {
				$scope.inventoryObject = new InventoryObject();

				$scope.addInventoryObject = function() {
					$scope.inventoryObject.$save(function() {
						$state.go('inventory_objects');
					});
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('inventory_objects', { // state for showing all inventory objects
			url : '/inventory_objects',
			templateUrl : 'partials/inventory_object/list.html',
			controller : 'InventoryObjectListController'
		}).state('newInventoryObject', { // state for adding a new inventory object
			url : '/inventory_objects/new',
			templateUrl : 'partials/inventory_object/add.html',
			controller : 'InventoryObjectCreateController'
		});
	}).run(function($state) {
		$state.go('inventory_objects'); // make a transition to inventory objects state when app
								// starts
	});
})();
