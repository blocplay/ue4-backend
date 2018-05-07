(function() {
	'use strict';

	angular.module('ue4App.services').factory('Leaderboard',
			function($resource) {
				return $resource('../srest/leaderboards/:escScoId', {
					escId : '@escId',
					escScoId : '@escScoId'
				});
			});

	angular.module('ue4App.controllers').controller(
			'LeaderboardListController',
			function($scope, $http, $state, $stateParams, popupService,
					$window, ScoreConfig, EventScore, Leaderboard) {
				$scope.pilotstats = Leaderboard.query({
					escScoId : $stateParams.escScoId
				});

				$scope.escScoId = $stateParams.escScoId;

				$scope.scoreConfigs = ScoreConfig.query();

				$scope.scoreConfig = ScoreConfig.get({
					scoId : $stateParams.escScoId
				});

				$scope.eventscores = EventScore.query({
					escScoId : $stateParams.escScoId
				});

				$scope.$watch('scoreConfig', function() {
					var newpilotstats = Leaderboard.query({
						escScoId : $scope.scoreConfig.scoId
					}, function() {
						$scope.pilotstats = newpilotstats;
					});

					var neweventscores = EventScore.query({
						escScoId : $scope.scoreConfig.scoId
					}, function() {
						$scope.eventscores = neweventscores;
					});
				});

			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('leaderboards', { // state for showing all
												// pilotstats
			url : '/leaderboards/:escScoId',
			templateUrl : 'partials/leaderboard/list.html',
			controller : 'LeaderboardListController'
		});
	});
})();
