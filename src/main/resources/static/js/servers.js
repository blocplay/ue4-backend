(function() {
	'use strict';

	angular.module('ue4App.services').factory('Server', function($resource) {
		return $resource('../srest/servers/:srvId', {
			srvId : '@srvId'
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

	angular.module('ue4App.controllers').controller('ServerListController',
			function($scope, $state, popupService, $window, Server, Championship) {
				$scope.servers = Server.query();
				$scope.championships = Championship.query();

				$scope.deleteServer = function(server) {
					if (popupService.showPopup('Deleting the server will remove all history and data from this server. Really delete server' + server.srvName +  '?')) {
						server.$delete(function() {
							$scope.servers = Server.query();
						});
					}
				};

				$scope.updateChampionship = function(server) {
					var message = 'Assign server to this championship?';
					if(!server.srvChaId || server.srvChaId=='')
					{
						message = 'Retire server from championship?';
					}
					if (popupService.showPopup(message)) {
						server.$update(function() {
							$scope.servers = Server.query();
						});
					}
				};
			}).controller('ServerCreateController',
			function($scope, $state, $stateParams, Server) {
				$scope.server = new Server();

				$scope.addServer = function() {
					$scope.server.$save(function() {
						$state.go('servers');
					});
				};
			}).controller('ServerEditController',
			function($scope, $state, $stateParams, Server) {
				$scope.updateServer = function() {
					$scope.server.$update();
				};

				$scope.server = Server.get({
					srvId : $stateParams.srvId
				});

			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('servers', { // state for showing all servers
			url : '/servers',
			templateUrl : 'partials/server/list.html',
			controller : 'ServerListController'
		}).state('newServer', { // state for adding a new server
			url : '/servers/new',
			templateUrl : 'partials/server/add.html',
			controller : 'ServerCreateController'
		}).state('editServer', { // state for updating a server
			url : '/servers/:srvId/edit',
			templateUrl : 'partials/server/edit.html',
			controller : 'ServerEditController'
		});
	}).run(function($state) {
		$state.go('servers');
	});
})();
