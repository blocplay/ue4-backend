(function() {
	'use strict';

	angular.module('ue4App.services').factory('User', function($resource) {
		return $resource('../srest/users/:usrId', {
		  usrId : '@usrId'
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

	angular.module('ue4App.controllers').controller('UserListController',
			function($scope, $state, popupService, $window, User) {
				$scope.users = User.query();

        $scope.updateLists = function() {
          var usersUpdate = User.query(function() {
            $scope.users = usersUpdate;
          });
        };

				
        $scope.enableUser = function(user) {
          if (popupService.showPopup('Enabling the user will allow him to play even without a valid steam id. Really enable user' + user.name +  '?')) {
            user.enabled = true;
            user.$update(function() {
              $scope.updateLists();
            });
          }
        };

        $scope.updateUser = function(user) {
          user.$update(function() {
            $scope.updateLists();
          }, function (error) {
            popupService.showPopup('Error updating user ' +  error.data)
            $scope.updateLists();
          });
        };

        $scope.deleteUser = function(user) {
					if (popupService.showPopup('Deleting the user will remove all history and data from this user. Really delete user' + user.name +  '?')) {
						user.$delete(function() {
						  $scope.updateLists();
						});
					}
				};

			});

	angular.module('ue4App').config(function($stateProvider) {
		$stateProvider.state('users', { // state for showing all users
			url : '/users',
			templateUrl : 'partials/user/list.html',
			controller : 'UserListController'
		});
	}).run(function($state) {
		$state.go('users');
	});
})();
