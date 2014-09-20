
class CreateUserCtrl

    constructor: (@$log, @$location, @UserService) ->
        @$log.debug "constructing CreateUserController"
        @user = {}

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

controllersModule.controller('CreateUserCtrl', CreateUserCtrl)