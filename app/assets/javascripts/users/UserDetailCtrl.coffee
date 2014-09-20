
class UserDetailCtrl

    constructor: (@$log, @$location, @$routeParams, @UserService) ->
        @$log.debug "constructing UserDetailController"
        @user = {}
        @getUser(@$routeParams.uuid)

    getUser: (uuid) ->
        @$log.debug "getUser(#{uuid})"

        @UserService.getUser(uuid)
        .then(
            (data) =>
                @$log.debug "Promise returned #{angular.toJson(data, true)} User"
                @user = data
            ,
            (error) =>
                @$log.error "Unable to get User: #{error}"
            )

    updateUser: () ->
        @$log.debug "updateUser()"
        @UserService.updateUser(@user)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} User"
                @user = data
                @$location.path("/")
            ,
            (error) =>
                @$log.error "Unable to update User: #{error}"
            )


controllersModule.controller('UserDetailCtrl', UserDetailCtrl)