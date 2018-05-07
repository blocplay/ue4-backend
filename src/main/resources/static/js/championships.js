(function() {
	'use strict';

	angular.module('ue4App.services').factory('Championship', function($resource) {
		return $resource('../srest/championships/:chaId', {
			chaId : '@chaId'
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

	angular.module('ue4App.controllers').controller('ChampionshipListController',
			function($scope, $state, popupService, $window, Championship) {
				$scope.championships = Championship.query();

				$scope.deleteChampionship = function(championship) {
					if (popupService.showPopup('Really delete this?')) {
						championship.$delete(function() {
							$state.go('championships', {}, {
								reload : true
							});
						});
					}
				};
			}).controller('ChampionshipCreateController',
			function($scope, $state, $stateParams, Championship) {
				$scope.championship = new Championship();

				$scope.addChampionship = function() {
					$scope.championship.$save(function() {
						$state.go('championships');
					});
				};
			}).controller('ChampionshipEditController',
			function($scope, $state, $stateParams, Championship) {
				$scope.updateChampionship = function() {
					$scope.championship.$update();
				};

				$scope.championship = Championship.get({
					chaId : $stateParams.chaId
				});

			}).controller('ChampionshipViewController',
			function($scope, $http, $state, $stateParams, Championship) {
				
				$scope.championship = Championship.get({
					chaId : $stateParams.chaId
				});
				
				$scope.loadData = function() {
					$http.get('../srest/championships/' + $stateParams.chaId + '/pilots').
					  success(function(data, status, headers, config) {
						  $scope.pilots = data;
					  }).
					  error(function(data, status, headers, config) {
					    // called asynchronously if an error occurs
					    // or server returns response with an error status.
					  });					
				};
				
				$scope.loadData();

			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('championships', { // state for showing all championships
			url : '/championships',
			templateUrl : 'partials/championship/list.html',
			controller : 'ChampionshipListController'
		}).state('newChampionship', { // state for adding a new championship
			url : '/championships/new',
			templateUrl : 'partials/championship/add.html',
			controller : 'ChampionshipCreateController'
		}).state('editChampionship', { // state for updating a championship
			url : '/championships/:chaId/edit',
			templateUrl : 'partials/championship/edit.html',
			controller : 'ChampionshipEditController'
		}).state('viewChampionship', { // viewing a championship
			url : '/championships/:chaId/view',
			templateUrl : 'partials/championship/view.html',
			controller : 'ChampionshipViewController'
		});
	}).run(function($state) {
		$state.go('championships');
	});
})();
