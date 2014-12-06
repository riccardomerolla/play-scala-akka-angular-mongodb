
dependencies = [
    'ngRoute',
    'ngCookies',
    'ui.bootstrap',
    'myApp.filters',
    'myApp.services',
    'myApp.controllers',
    'myApp.directives',
    'myApp.common',
    'myApp.routeConfig'
]

app = angular.module('myApp', dependencies)

angular.module('myApp.routeConfig', ['ngRoute', 'ngCookies'])
    .config ($routeProvider) ->
        $routeProvider
            .when('/', {
                templateUrl: '/assets/partials/list.html'
            })
            .when('/users/create', {
                templateUrl: '/assets/partials/detail.html'
            })
            .when('/user/detail/:uuid', {
                templateUrl: '/assets/partials/detail.html'
            })
            .when('/signin', {
                templateUrl: '/assets/partials/signin.html'
            })
            .when('/signup', {
                templateUrl: '/assets/partials/signup.html'
            })
            .when('/chat', {
                  templateUrl: '/assets/partials/chat/chat.html', requireAuthorization: true
            })
            .otherwise({redirectTo: '/'})

    .run (@$rootScope, $location, security) ->
        @$rootScope.$on('$routeChangeStart',
          (event, next) ->
            security.isAuthorized()
                .success(
                    () =>
                        @$rootScope.security = {
                            'isAuthorized': true,
                            'email': security.email
                        })
                .error(
                    () =>
                        @$rootScope.security = {
                            'isAuthorized': false,
                            'email': ''
                        }
                    $location.path('/signin') if (next.requireAuthorization || false)
                )
        )

@commonModule = angular.module('myApp.common', [])
@controllersModule = angular.module('myApp.controllers', [])
@servicesModule = angular.module('myApp.services', [])
@modelsModule = angular.module('myApp.models', [])
@directivesModule = angular.module('myApp.directives', [])
@filtersModule = angular.module('myApp.filters', [])