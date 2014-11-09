class security

    constructor: (@$log, @$http, @$cookies, @$route) ->
        @$log.debug "constructing security"

    email: () ->
        @$cookies['email'].replace(/\"/g, "")

    signup: (userform, success, error) ->
        @$log.debug "SignUp"

        @$http.post('/api/auth/signup', userform)
        .success((data, status, headers) =>
            @$log.info("Successfully signed up - status #{status}")
            success
            @$route.reload()
        )
        .error((data, status, headers) =>
            @$log.error("Failed to signed up - status #{status}")
            error
        )

    signin: (userform, success, error) ->
        @$log.debug "SignIn"

        @$http.post('/api/auth/signin', userform)
        .success((data, status, headers) =>
            @$log.info("Successfully signed in - status #{status}")
            success
            @$route.reload()
        )
        .error((data, status, headers) =>
            @$log.error("Failed to signed in - status #{status}")
            error
        )

    signout: (success, error) ->
        @$log.debug "SignOut"

        @$http.get('/api/auth/signout')
        .success((data, status, headers) =>
            @$log.info("Successfully signed out - status #{status}")
            success
            @$route.reload()
        )
        .error((data, status, headers) =>
            @$log.error("Failed to signe out - status #{status}")
            error
        )

    isAuthorized: (success, error) ->
        @$log.debug "isAuthorized"

        @$http.get('/api/auth/authorize')
        .success((data, status, headers) =>
            success
        )
        .error((data, status, headers) =>
            error
        )

servicesModule.service('security', security)