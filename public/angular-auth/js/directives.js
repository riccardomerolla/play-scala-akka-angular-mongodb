'use strict';

/* Directives */

angular.module('mainApp.directives', [])

    /* Ensure user is unique */
    .directive('ensureUnique',['$http', 'serverAPI', function($http, serverAPI){
        return {
            require: 'ngModel',
            link:function(scope, element, attrs, ctrl){
                /* subscribe on changing email field */
                scope.$watch(attrs.ngModel, function(){
                    /* asking server for uniqueness */
                    serverAPI.ensureUnique(ctrl.$modelValue,
                        function(data){
                            /* success */
                            ctrl.$setValidity('unique',data.isUnique);
                        },
                        function(){
                            /* error */
                            ctrl.$setValidity('unique',false);
                        });
                });
            }
        }
    }]);