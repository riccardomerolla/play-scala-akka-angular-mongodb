
class MenuCtrl

    constructor: (@$log, @$location, @UserService) ->
        @$log.debug "constructing MenuController"
        @oneAtATime = true
        @groups = [
            {
                title: 'User',
                links: [
                    {
                        content: 'User Function',
                        url: '/#/'
                    }
                    ]
            },
            {
                title: 'Chat',
                content: 'Chat',
                links: [
                    {
                        content: 'Firebase Chat Function',
                        url: '/#/chat'
                    }
                ]
            }
        ]
        @items = ['Item 1', 'Item 2', 'Item 3']
        @status = {
            isFirstOpen: true,
            isFirstDisabled: false
        }


    addItem: () ->
        @$log.debug "addItem()"

        newItemNo = @items.length + 1
        @items.push('Item ' + newItemNo)


controllersModule.controller('MenuCtrl', MenuCtrl)