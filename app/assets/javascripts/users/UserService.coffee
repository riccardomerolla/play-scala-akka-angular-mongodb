
class UserService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.debug "constructing UserService"

    listAllUsers: () ->
        @$log.debug "listUsers()"
        deferred = @$q.defer()

        @$http.get("/users")
        .success((data, status, headers) =>
                @$log.info("Successfully listed Users - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list Users - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    listUsers: (page, perPage) ->
        @$log.debug "listUsers()"
        deferred = @$q.defer()

        @$http.get("/users/#{page}/#{perPage}")
        .success((data, status, headers) =>
                @$log.info("Successfully listed Users - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list Users - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    countUsers: () ->
        @$log.debug "countUsers()"
        deferred = @$q.defer()

        @$http.get("/users/count")
        .success((data, status, headers) =>
                @$log.info("Successfully counted Users - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to count Users - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    getUser: (uuid) ->
        @$log.debug "getUser()"
        deferred = @$q.defer()

        @$http.get("/user/#{uuid}")
        .success((data, status, headers) =>
                @$log.info("Successfully retrieve User - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to retrieve User - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    deleteUser: (uuid) ->
        @$log.debug "deleteUser()"
        deferred = @$q.defer()

        @$http.delete("/user/#{uuid}")
        .success((data, status, headers) =>
          @$log.info("Successfully remove User - status #{status}")
          deferred.resolve(data)
        )
        .error((data, status, headers) =>
          @$log.error("Failed to remove User - status #{status}")
          deferred.reject(data);
        )
        deferred.promise

    createUser: (user) ->
        @$log.debug "createUser #{angular.toJson(user, true)}"
        deferred = @$q.defer()

        @$http.post('/user', user)
        .success((data, status, headers) =>
                @$log.info("Successfully created User - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to create user - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    updateUser: (user) ->
        @$log.debug "updateUser #{angular.toJson(user, true)}"
        deferred = @$q.defer()

        @$http.post('/user/update', user)
        .success((data, status, headers) =>
          @$log.info("Successfully update User - status #{status}")
          deferred.resolve(data)
        )
        .error((data, status, headers) =>
          @$log.error("Failed to update user - status #{status}")
          deferred.reject(data);
        )
        deferred.promise

    generateUUID: () ->
        @$log.debug "generate UUID"
        deferred = @$q.defer()

        @$http.get("/randomUUID")
        .success((data, status, headers) =>
                @$log.info("Successfully retrieve UUID - status #{status}")
                deferred.resolve(data)
                @$log.debug "UUID #{data}"
            )
        .error((data, status, headers) =>
                @$log.error("Failed to retrieve UUID - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

servicesModule.service('UserService', UserService)