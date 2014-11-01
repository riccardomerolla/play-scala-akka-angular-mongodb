
class UserCtrl

    constructor: (@$log, @$location, @UserService) ->
        @$log.debug "constructing UserController"
        @currentPage = 1
        @numPerPage = 10
        @maxSize = 5
        @numPages = 0
        @users = []
        @getAllUsers()

    getAllUsers: () ->
        @$log.debug "getAllUsers()"

        @UserService.listUsers()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Users"
                @users = data
                @numPages = data.length
            ,
            (error) =>
                @$log.error "Unable to get Users: #{error}"
                @numPages = 0
            )

controllersModule.controller('UserCtrl', UserCtrl)