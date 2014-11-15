directivesModule
.directive 'confirmClick', () ->
  priority: 1,
  terminal: true,
  link: (scope, element, attrs) ->
    msg = attrs.confirmClick || "Are you sure?"
    clickAction = attrs.ngClick

    # handle click on button
    element.bind 'click', () ->
      # First Click
      if window.confirm(msg)
        scope.$eval(clickAction)