
class UserCtrl

    constructor: (@$log, @$location, @UserService) ->
        @$log.debug "constructing UserController"
        @currentPage = 0
        @numPerPage = 1
        @maxSize = 5
        @bigTotalItems = 0
        @users = []
        @getAllUsers()

    getAllUsers: () ->
        @$log.debug "getAllUsers()"

        @UserService.countUsers()
        .then(
            (data) =>
                @$log.debug "Promise returned Users count " + data
                @bigTotalItems = Math.ceil(data / @numPerPage)
            ,
            (error) =>
                @$log.error "Unable to get Users count: #{error}"
                @bigTotalItems = 0
            )

        @UserService.listUsers(@currentPage, @numPerPage)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Users"
                @users = data
            ,
            (error) =>
                @$log.error "Unable to get Users: #{error}"
            )

controllersModule.controller('UserCtrl', UserCtrl)