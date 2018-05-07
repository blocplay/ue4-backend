(function() {
	'use strict';

	angular.module('ue4App.services').factory('Gamemode', function($resource) {
		return $resource('../srest/gamemodes/:gamId', {
			gamId : '@gamId'
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

	angular.module('ue4App.controllers').controller('GamemodeListController',
			function($scope, $state, popupService, $window, Gamemode) {
				$scope.gamemodes = Gamemode.query(); // fetch all game modes.
														// Issues a GET to /gamemodes

				$scope.updateGamemode = function(gamemode) {
					// Update the edited game mode.
					// Issues a PUT to /gamemodes/:id
					return gamemode.$update();
				};

				$scope.deleteGamemode = function(gamemode) {
					if (popupService.showPopup('Really delete this?')) {
						gamemode.$delete(function() {
							$state.go('gamemodes', {},{ reload: true });
						});
					}
				};
			}).controller('GamemodeCreateController',
			function($scope, $state, $stateParams, Gamemode) {
				$scope.gamemode = new Gamemode();

				$scope.addGamemode = function() {
					$scope.gamemode.$save(function() {
						$state.go('gamemodes');
					});
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('gamemodes', { // state for showing all gamemodes
			url : '/gamemodes',
			templateUrl : 'partials/gamemode/list.html',
			controller : 'GamemodeListController'
		}).state('newGamemode', { // state for adding a new gamemode
			url : '/gamemodes/new',
			templateUrl : 'partials/gamemode/add.html',
			controller : 'GamemodeCreateController'
		});
	}).run(function($state) {
		$state.go('gamemodes'); // make a transition to gamemodes state when app
								// starts
	});
})();
