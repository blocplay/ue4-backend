(function() {
	'use strict';

	// ue4App.config([ '$routeProvider', function($routeProvider) {
	// $routeProvider.when('/gamemaps', {
	// templateUrl : 'partials/gamemaps.html',
	// controller : 'GamemapCtrl'
	// // }).when('/phones/:phoneId', {
	// // templateUrl : 'partials/phone-detail.html',
	// // controller : 'PhoneDetailCtrl'
	// }).otherwise({
	// redirectTo : '/gamemaps'
	// });
	// } ]);

	angular.module('ue4App.services').factory('Gamemap', function($resource) {
		return $resource('../srest/gamemaps/:mapId', {
			mapId : '@mapId'
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

	angular.module('ue4App.controllers').controller('GamemapListController',
			function($scope, $state, popupService, $window, Gamemap) {
				$scope.gamemaps = Gamemap.query();

				$scope.updateGamemap = function(gamemap) {
					return gamemap.$update();
				};

				$scope.deleteGamemap = function(gamemap) {
					if (popupService.showPopup('Really delete this?')) {
						gamemap.$delete(function() {
							// Refresh
							$state.go('gamemaps', {}, {
								reload : true
							});
						});
					}
				};
			}).controller('GamemapCreateController',
			function($scope, $state, $stateParams, Gamemap) {
				$scope.gamemap = new Gamemap();

				$scope.addGamemap = function() {
					$scope.gamemap.$save(function() {
						$state.go('gamemaps');
					});
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('gamemaps', {
			url : '/gamemaps',
			templateUrl : 'partials/gamemap/list.html',
			controller : 'GamemapListController'
		}).state('newGamemap', {
			url : '/gamemaps/new',
			templateUrl : 'partials/gamemap/add.html',
			controller : 'GamemapCreateController'
		});
	}).run(function($state) {
		$state.go('gamemaps');
	});
})();
