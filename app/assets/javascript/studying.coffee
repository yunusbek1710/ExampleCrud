$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/create'
    getList: '/getList'
    delete: '/delete'
    update: '/update'

  vm = ko.mapping.fromJS
    computer: ''
    getList: []
    id: 0

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')


  vm.onSubmit = ->
    toastr.clear()
    console.log 'computerName: ', vm.computer()
    if (!vm.computer())
      toastr.error("Please enter a name")
      return no
    else
      data =
        computer: vm.computer()
      $.ajax
        url: apiUrl.send
        type: 'POST'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)


  vm.getAllComputer = ->
    $.ajax
       url: apiUrl.getList
       type: 'GET'
    .fail handleError
    .done (response) ->
      console.log('1: ', vm.getList().length)
      vm.getList(response)
      console.log('2: ', vm.getList().length)

  vm.delete = ->
    console.log 'computerId: ', vm.id()
    data =
      id: parseInt(vm.id())
    $.ajax
      url: apiUrl.delete
      type: 'DELETE'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)

  vm.update = ->
    console.log 'computerId: ', vm.id()
    data =
      id: parseInt(vm.id())
      computer: vm.computer()
    $.ajax
      url: apiUrl.update
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)


  ko.applyBindings {vm}