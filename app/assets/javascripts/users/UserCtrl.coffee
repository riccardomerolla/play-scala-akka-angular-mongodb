
class UserCtrl

    constructor: (@$log, @$location, @UserService) ->
        @$log.debug "constructing UserController"
        @currentPage = 1
        @numPerPage = 10
        @maxSize = 5
        @bigTotalItems = 0
        @users = []
        @getAllUsers()
        @pageChanged()

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

        @UserService.listUsers(@currentPage - 1, @numPerPage)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Users"
                @users = data
            ,
            (error) =>
                @$log.error "Unable to get Users: #{error}"
            )

    pageChanged: () ->
        @$log.debug "Page changed to: #{@currentPage}"

        @getAllUsers()

controllersModule.controller('UserCtrl', UserCtrl)