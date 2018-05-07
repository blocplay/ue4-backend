(function() {
	'use strict';

	angular.module('ue4App.services').factory('ScoreConfig', function($resource) {
		return $resource('../srest/score_configs/:scoId', {
			scoId : '@scoId'
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

	angular.module('ue4App.controllers').controller('ScoreConfigListController',
			function($scope, $state, popupService, $window, ScoreConfig) {
				$scope.scoreConfigs = ScoreConfig.query(); // fetch all score configs.
														// Issues a GET to /score_configs

				$scope.updateScoreConfig = function(scoreConfig) {
					// Update the edited game mode.
					// Issues a PUT to /score_configs/:id
					return scoreConfig.$update();
				};

				$scope.deleteScoreConfig = function(scoreConfig) {
					if (popupService.showPopup('Really delete this?')) {
						scoreConfig.$delete(function() {
							$state.go('score_configs', {},{ reload: true });
						});
					}
				};
			}).controller('ScoreConfigCreateController',
			function($scope, $state, $stateParams, ScoreConfig) {
				$scope.scoreConfig = new ScoreConfig();

				$scope.addScoreConfig = function() {
					$scope.scoreConfig.scoMinimumFactor = 0.5;
					$scope.scoreConfig.scoMaximumFactor = 10;
					$scope.scoreConfig.scoNeutralMatches = 10;
					$scope.scoreConfig.scoMonthsToConsider = 1;
					$scope.scoreConfig.$save(function() {
						$state.go('score_configs');
					});
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('score_configs', { // state for showing all score configs
			url : '/score_configs',
			templateUrl : 'partials/score_config/list.html',
			controller : 'ScoreConfigListController'
		}).state('newScoreConfig', { // state for adding a new score config
			url : '/score_configs/new',
			templateUrl : 'partials/score_config/add.html',
			controller : 'ScoreConfigCreateController'
		});
	}).run(function($state) {
		$state.go('score_configs'); // make a transition to score configs state when app
								// starts
	});
})();
