(function() {
	'use strict';

	var ue4App = angular.module('ue4App', [ 'http-auth-interceptor',
			'ui.router', 'ngResource', 'ue4App.controllers', 'ue4App.services', 'ue4App.directives',
			'login', 'xeditable', 'ui.bootstrap', 'angularUtils.directives.dirPagination', 'angular.filter']);
	// var ue4AppControllers = angular.module('ue4App.controllers', []);
	ue4App.run(function(editableOptions, editableThemes) {
		// bootstrap3 theme. Can be also 'bs2', 'default'
		editableOptions.theme = 'bs3'; 
		editableThemes.bs3.inputClass = 'input-normal';
	});
	angular.module('ue4App.controllers', []);
	// var ue4AppServices = angular.module('ue4App.services', []);
	angular.module('ue4App.services', []).directive('ue4App', function() {
		return {
			restrict : 'C',
			link : function(scope, elem, attrs) {
				var loading = elem.find('#initializing-panel');
				var login = elem.find('#login-holder');
				var main = elem.find('#main-container');

				login.hide();
				main.hide();
				loading.hide();
				// This could be handled more gracefully and just show main here
				// if we have been authenticated
				main.show();

				// once Angular is started, remove class:
				elem.removeClass('waiting-for-angular');

				scope.$on('event:auth-loginRequired', function() {
					login.find('input').val('');
					login.show();
					main.hide();
					// login.slideDown('slow', function() {
					// main.hide();
					// });
				});

				scope.$on('event:auth-loginConfirmed', function() {
					main.show();
					login.hide();
					// login.slideUp();
				});

			}
		}
	});
	
	
	angular.module('ue4App.directives', [])
		.directive('clickAndDisable', function() {
		  return {
			  restrict: 'A',
		    scope: {
		      clickAndDisable: '&'
		    },
		    link: function(scope, iElement, iAttrs) {
		      iElement.bind('click', function() {		    	  
			        iElement.prop('class','btn btn-warning');
			        iElement.prop('disabled',true); 
		        scope.clickAndDisable().finally(function() {
			          iElement.prop('disabled',false);
				        iElement.prop('class','btn btn-default');
		        })
		      });
		    }
		  };
		});

	ue4App.config(function(paginationTemplateProvider) {
	    paginationTemplateProvider.setPath('partials/dirPagination.tpl.html');
	});
})();