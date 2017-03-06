var app = angular.module('app', [ 'ui.bootstrap', 'ui.grid',
		'ui.grid.draggable-rows', 'ui.grid.grouping', 'ngMaterial' ]);

app
		.controller(
				'MainCtrl',
				function($scope, $http, $timeout, $filter) {
					
					$scope.stop = function(service) {
						$http.get(
								'/api/service/startstop?service=' + service
										+ "&control=stop").then(function(data) {
							console.log(data);
						});
					}
					
					$scope.start = function(service) {
						$http.get(
								'/api/service/startstop?service=' + service
										+ "&control=start").then(function(data) {
							console.log(data);
						});
					}
					
					$scope.tirggerAction = function(service) {
						if(angular.equals(service.serviceStatus, "Running")) {
							$scope.stop(service.serviceName);
							service.serviceStatus="stopped";
						} else {
							$scope.start(service.serviceName);
							service.serviceStatus="Running"
						}
					}
					
					$scope.giveControlName = function(action) {
						if(angular.equals(action, "Running"))
							return "Stop"
						return "Start"
					}

					$scope.gridOptions = {
						enableFiltering : false,
						treeRowHeaderAlwaysVisible : false,
						columnDefs : [
								{
									name : 'serviceName',
									cellTemplate : '<div class="ui-grid-cell-contents"><strong>{{COL_FIELD}}</strong></div>'
								},
								{
									name : 'serviceStatus'
								},
								// grouping: { groupPriority: 1 }, sort: {
								// priority: 1, direction: 'asc' }
								{
									name : 'Control',
//									width : '30%',
									cellClass: 'ui-grid-vcenter',
									cellTemplate : '<div > <button  ng-click="grid.appScope.tirggerAction(row.entity)">{{grid.appScope.giveControlName(row.entity.serviceStatus)}}</button></div>'
								}
						// <div class="ui-grid-cell-contents"> <button
						// type="button"
						// ng-click="grid.appScope.start(row.entity.serviceName)">Start</button><button
						// type="button"
						// ng-click="grid.appScope.stop(row.entity.serviceName)">Stop</button>
						],
						onRegisterApi : function(gridApi) {
							$scope.gridApi = gridApi;
						}
					};

					$http.get('/api/winservicelist').success(function(data) {
						$scope.gridOptions.data = data;
					});


//					$scope.toggleRow = function(rowNum) {
//						$scope.gridApi.treeBase
//								.toggleRowTreeState($scope.gridApi.grid.renderContainers.body.visibleRowCache[rowNum]);
//					};
//
//					$scope.changeGrouping = function() {
//						$scope.gridApi.grouping.clearGrouping();
//						$scope.gridApi.grouping.groupColumn('serviceStatus');
//					};
					
					$scope.stopForLinux = function(service) {
						$http.get(
								'/api/linuxservice/startstop?service=' + service
										+ "&control=stop").then(function(data) {
							console.log(data);
						});
					}
					
					$scope.startForLinux = function(service) {
						$http.get(
								'/api/linuxservice/startstop?service=' + service
										+ "&control=start").then(function(data) {
							console.log(data);
						});
					}
					
					
					$scope.tirggerActionForLinux = function(service) {
						if(angular.equals(service.serviceStatus, "Running")) {
							$scope.stopForLinux(service.serviceName);
							service.serviceStatus="stopped";
						} else {
							$scope.startForLinux(service.serviceName);
							service.serviceStatus="Running"
						}
					}
					
					
					$scope.gridOptionsLinux = {
							enableFiltering : false,
							treeRowHeaderAlwaysVisible : false,
							columnDefs : [
									{
										name : 'serviceName',
										cellTemplate : '<div class="ui-grid-cell-contents"><strong>{{COL_FIELD}}</strong></div>'
									},
									{
										name : 'serviceStatus'
									},
									// grouping: { groupPriority: 1 }, sort: {
									// priority: 1, direction: 'asc' }
									{
										name : 'Control',
										cellClass: 'ui-grid-vcenter',
										cellTemplate : '<div > <button  ng-click="grid.appScope.tirggerActionForLinux(row.entity)">{{grid.appScope.giveControlName(row.entity.serviceStatus)}}</button></div>'
									}
							],
							onRegisterApi : function(gridApi) {
								$scope.gridApi = gridApi;
							}
						};

						$http.get('/api/linuxserviceslist').success(function(data) {
							$scope.gridOptionsLinux.data = data;
						});

				});
