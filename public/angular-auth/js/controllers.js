'use strict';

/* Controllers */

angular.module('mainApp.controllers', [])

    /* Main Controller */
    .controller('MainCtrl',function($scope, $location, security, serverAPI){

        /* Handle active location for header */
        $scope.isActive = function(viewLocation){
            return viewLocation === $location.path();
        };

        /* Account information */
        $scope.account = function(){
            serverAPI.userInfo(
                function(data){
                    $scope.user = data;
                },
                function(){
                    $scope.user = 'Error...';
                }
            );
        };

        /* Delete Account */
        $scope.deleteAccount = function(){
            serverAPI.deleteAccount(
                function(){
                    /* success */
                    $location.path('/')
                },
                function(){
                    /* error */
                }
            );
        };

        /* Signout */
        $scope.signoutError = false;
        $scope.signout = function(){
            security.signout(
                function(){
                    /* success */
                    $location.path('/')
                },
                function(){
                    /* error */
                    $scope.signinError = true;
                });
        };
    })

    /* Signin Controller */
    .controller('SigninCtrl', function($scope,$location,security){

        $scope.signinError = false;

        $scope.signin = function(){
            security.signin($scope.userform,
                function(){
                    /* success */
                    $location.path('/');
                },
                function(){
                    /* error */
                    $scope.signinError = true;
                });
        };

    })

    /* Signup Controller */
    .controller('SignupCtrl', function($scope,$location,security){

        $scope.signupError = false;

        $scope.signup = function(){
            security.signup($scope.userform,
                function(){
                    /* success */
                    $location.path('/');
                },
                function(){
                    /* error */
                    $scope.signupError = true;
                });
        };

    })

    /* TODO Controller */
    .controller('TodoCtrl', function(){

    })