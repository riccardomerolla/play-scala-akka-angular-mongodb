
class UserDetailCtrl

    constructor: (@$log, @$location, @$routeParams, @UserService) ->
        @$log.debug "constructing UserDetailController"
        @user = {}
        @doUpdate = false
        @getUser(@$routeParams.uuid)

    getUser: (uuid) ->
        @doUpdate = true if uuid
        if @doUpdate
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

    createUser: () ->
        @$log.debug "createUser()"
        @user.active = true
        @UserService.generateUUID()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} UUID"
                @user.uuid = data
                @UserService.createUser(@user)
                    .then(
                        (data) =>
                            @$log.debug "Promise returned #{data} User"
                            @user = data
                            @$location.path("/")
                        ,
                        (error) =>
                            @$log.error "Unable to create User: #{error}"
                        )
            ,
            (error) =>
                @$log.error "Unable to create UUID: #{error}"
            )


controllersModule.controller('UserDetailCtrl', UserDetailCtrl)