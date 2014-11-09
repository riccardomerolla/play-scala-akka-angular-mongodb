'use strict';

// Declare app level module which depends on controllers, services and directives
var app = angular.module('mainApp', ['mainApp.controllers', 'mainApp.services', 'mainApp.directives', 'ngCookies', 'ngRoute'])

    .config(function($routeProvider) {

        $routeProvider
            .when('/', {templateUrl:'partials/home.html', controller: 'MainCtrl'})
            .when('/about', {templateUrl:'partials/about.html', controller: 'MainCtrl'})
            .when('/todo', {templateUrl: 'partials/todo.html', controller: 'TodoCtrl', requireAuthorization: true})

            .when('/signin', {templateUrl:'partials/signin.html', controller: 'SigninCtrl'})
            .when('/signup', {templateUrl:'partials/signup.html',controller:'SignupCtrl'})

            .otherwise({redirectTo: '/'});

    })

    .run(function($rootScope, $location, security){

        /* Subscribe on Route Change event */
        $rootScope.$on('$routeChangeStart',function(event,next){

            /* Check authorization */
            security.isAuthorized(
                function success(){
                    /* Is Authorized */
                    $rootScope.security = {
                        'isAuthorized': true,
                        'email': security.email
                    };
                },
                function error(){
                    /* Is Not Authorized */
                    $rootScope.security = {
                        'isAuthorized': false,
                        'email': ''
                    };

                    /* Check if the page requires authorization */
                    if (next.requireAuthorization || false) {
                        $location.path('/signin');
                    }
                }
            );
        });
    })