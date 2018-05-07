(function() {
	'use strict';

	angular.module('ue4App.services').factory('EventScore', function($resource) {
		return $resource('../srest/event_scores/:escScoId', {
			escId : '@escId',			
			escScoId : '@escScoId'			
		}, {
			update : {
				method : 'PUT'
			}
		});
	});

	angular.module('ue4App.controllers').controller(
			'EventScoreListController',
			function($scope, $http, $state, $stateParams, popupService, $window, ScoreConfig, EventScore) {
				$scope.eventscores = EventScore.query({
					escScoId : $stateParams.escScoId
				});
				
				$scope.escScoId = $stateParams.escScoId;

				$scope.scoreConfig = ScoreConfig.get({
					scoId : $stateParams.escScoId
				});
				
				$scope.deleteEventScore = function(eventscore) {
					if (popupService.showPopup('Really delete this?')) {
						eventscore.$delete(function() {
							$state.go('event_scores', {escScoId:$scope.escScoId}, {
								reload : true
							});
						});
					}
				};
				
				$scope.updateEventScore = function(eventscore) {
					eventscore.$update(function() {
						$state.go('event_scores', {escScoId:$scope.escScoId}, {
							reload : true
						});
					});
				};

			}).controller('EventScoreCreateController',
			function($scope, $state, $stateParams, ScoreConfig, EventScore) {
				$scope.escScoId = $stateParams.escScoId; 

				$scope.scoreConfig = ScoreConfig.get({
					scoId : $stateParams.escScoId
				});

				$scope.eventscore = new EventScore(); // create new event score instance.
				// Properties
				// will be set via ng-model on UI

				$scope.addEventScore = function() { // create a new event score.
					// Issues
					// a POST
					// to /eventscores
					$scope.eventscore.escScoId = $scope.escScoId;
					$scope.eventscore.escEventScore = 0;
					$scope.eventscore.$save(function() {
						$state.go('event_scores', {escScoId:$scope.escScoId}); // on success go back to home
						// i.e.
						// eventscores state.
					});
				};
			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('event_scores', { // state for showing all eventscores
			url : '/event_scores/:escScoId',
			templateUrl : 'partials/event_score/list.html',
			controller : 'EventScoreListController'
		}).state('newEventScore', { // state for adding a new eventscore
			url : '/event_scores/:escScoId/new',
			templateUrl : 'partials/event_score/add.html',
			controller : 'EventScoreCreateController'
		});
	});
})();
