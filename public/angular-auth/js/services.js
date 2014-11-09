'use strict';

/* Services */

angular.module('mainApp.services', [])

    /* Server API Service */
    .factory('serverAPI', function($http, $route){
        return {

            /* Ensure that Email is unique */
            ensureUnique: function(email, success, error){
                $http
                    .get('/api/auth/user/' + email)
                    .success(function(data){
                        success(data);
                    })
                    .error(function(){
                        error();
                    });
            },

            /* Get User info */
            userInfo: function(success, error){
                $http
                    .get('/api/auth/user')
                    .success(function(data){
                        console.log('!!! Success getUser:');
                        success(data);
                    })
                    .error(function(){
                        console.log('??? Fail... getUser:');
                        error();
                    })
            },

            /* Delete Account */
            deleteAccount: function(success, error){
                $http
                    .delete('/api/auth/user')
                    .success(function(data){
                        console.log('!!! Success deleteAccount:');
                        success(data);
                        $route.reload();
                    })
                    .error(function(){
                        console.log('??? Fail... deleteAccount:');
                        error();
                    })
            }
        };
    })

    /* Security Service */
    .factory('security', function($http, $cookies, $route){
        return {

            /* Get Email from cookies */
            email: function(){
                return $cookies['email'].replace(/\"/g, "");
            },

            /* Register a new User */
            signup: function(userform, success, error){
                $http
                    .post('/api/auth/signup', userform)
                    .success(function(){
                        console.log('!!! SUCCESS Registration');
                        success();
                        $route.reload();
                    })
                    .error(function(){
                        console.log('??? FAILED Registration...')
                        error();
                    });
            },

            /* Authenticate a User */
            signin: function (userform, success, error){
                $http
                    .post('/api/auth/signin', userform)
                    .success(function(){
                        console.log('!!! SUCCESS Signin');
                        success();
                        $route.reload();
                    })
                    .error(function(){
                        console.log('??? FAILED Signin...')
                        error();
                    });
            },

            /* Sign out with new session */
            signout: function(success,error){
                $http
                    .get('/api/auth/signout')
                    .success(function(){
                        console.log('!!! SUCCESS Signout');
                        success();
                        $route.reload();
                    })
                    .error(function(){
                        console.log('??? FAILED Signout...');
                        error();
                    });
            },

            /* Check a User is authorized */
            isAuthorized: function(success, error){
                $http
                    .get('/api/auth/authorize')
                    .success(function(){
                        success();
                    })
                    .error(function(){
                        error();
                    });
            }
        };
    })