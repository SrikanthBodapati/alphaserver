angular.module('tabApp', ['ui.router', 'ui.grid',
    'ui.grid.draggable-rows', 'ui.grid.grouping', 'ngMaterial', 'ui.tree'])
  .controller('TabController', ['$rootScope','$scope', '$http','$window', function($rootScope, $scope, $http, $window) {
	  if(!$rootScope.admin){
		  $scope.tabs = [ {
				title : 'Application',
				url : 'application.html'
			}, {
				title : 'Database',
				url : 'database.html'
			}, {
				title : 'ESSBase',
				url : 'essbase.html'
			}, {
				title : 'WebLogic',
				url : 'weblogic.html'
			}];
	  }else{
	  $scope.tabs = [ {
		title : 'Application',
		url : 'application.html'
	}, {
		title : 'Database',
		url : 'database.html'
	}, {
		title : 'ESSBase',
		url : 'essbase.html'
	}, {
		title : 'WebLogic',
		url : 'weblogic.html'
	}, {
		title : 'Alerts',
		url : 'alerts.html',
		
	} ];
	  }

  $scope.currentTab = 'application.html';

  $scope.onClickTab = function (tab) {
      $scope.currentTab = tab.url;
  }
  
  $scope.isActiveTab = function(tabUrl) {
      return tabUrl == $scope.currentTab;
  }
  
  $scope.stop = function(hostname,service) {
		
	}
	
	$scope.start = function(hostname,service) {
		
	}
	
	$scope.tirggerAction = function(service) {
		if(angular.equals(service.status, "Running")) {
			$http.get('/startandstopservice?hostname='+service.ipaddr+'&service=' + service.servicename+
			 "&action=stop").then(function(data) {
				 if(data.data.status == "pass"){
                   service.status="Stopped";
                   service.port= "";
				 }else{
				  service.status="Running";
				  $http.get('/names').success(function(data) {
			        	var hostaddr;
			        	var reqJson = {};
			        	for (var key in data) {
			        		  if (data.hasOwnProperty(key) && key != "error") {
			        			  hostaddr = key;
			        			  console.log("hostaddr"+hostaddr);
			        			  var value = JSON.parse(data[key]);
			        		        reqJson = value;
			        		  }
			        		}
			            for (i in reqJson.services){
			                for (key in reqJson.services[i]){
			                	if(key.indexOf("hostname") !== -1)
			                	    reqJson.services[i]['host']=reqJson.services[i][key];
			                	    reqJson.services[i]['ipaddr']=hostaddr;
			                }
			            }
			            $scope.gridOptions.data = reqJson.services;
			        });
				 }
          });
			
		} else {
			$http.get('/startandstopservice?hostname='+service.ipaddr+'&service=' + service.servicename+
			 "&action=start").then(function(data) {
				 if(data.data.status == "pass"){
	                   service.status="Running";
	                   $http.get('/names').success(function(data) {
	                   	var hostaddr;
	                   	var reqJson = {};
	                   	for (var key in data) {
	                   		  if (data.hasOwnProperty(key) && key != "error") {
	                   			  hostaddr = key;
	                   			  console.log("hostaddr"+hostaddr);
	                   			  var value = JSON.parse(data[key]);
	                   		        reqJson = value;
	                   		  }
	                   		}
	                       for (i in reqJson.services){
	                           for (key in reqJson.services[i]){
	                           	if(key.indexOf("hostname") !== -1)
	                           	    reqJson.services[i]['host']=reqJson.services[i][key];
	                           	    reqJson.services[i]['ipaddr']=hostaddr;
	                           }
	                       }
	                       $scope.gridOptions.data = reqJson.services;
	                   });
				 }else{
					  service.status="Stopped";
					  service.port="";
				 }
	          });
			
		}
		
		
	}
	
  $scope.giveControlName = function(action) {
		if(angular.equals(action, "Running"))
			return "Stop"
		return "Start"
	}
  
    $scope.gridOptions = {
            enableFiltering: false,
            treeRowHeaderAlwaysVisible: false,
            columnDefs: [{
            	name: 'host',
                displayName: 'Host Name',
                cellTemplate: '<div class="ui-grid-cell-contents"><strong>{{COL_FIELD}}</strong></div>'
            }, {
              	name: 'servicename',
                displayName: 'Service Name'
            }, {
                name: 'status',
                displayName: 'Service Status'
            }, {
                name: 'port',
                displayName: 'Port'
            },{
            	name : 'Control',
                width : '30%',
            	cellClass: 'ui-grid-vcenter',
            	cellTemplate : '<div > <button  ng-click="grid.appScope.tirggerAction(row.entity)">{{grid.appScope.giveControlName(row.entity.status)}}</button></div>'
            	}
            ],
            onRegisterApi: function(gridApi) {
                $scope.gridApi = gridApi;
            }
        };
      
        $http.get('/names').success(function(data) {
        	console.log(data);
        	var hostaddr;
        	var reqJson = {};
        	for (var key in data) {
        		  if (data.hasOwnProperty(key) && key != "error") {
        			  hostaddr = key;
        			  var value = JSON.parse(data[key]);
        		        reqJson = value;
        		  }
        		}
            for (i in reqJson.services){
                for (key in reqJson.services[i]){
                	if(key.indexOf("hostname") !== -1)
                	    reqJson.services[i]['host']=reqJson.services[i][key];
                	    reqJson.services[i]['ipaddr']=hostaddr;
                }
            }
            $scope.gridOptions.data = reqJson.services;
           
            
        });
        $http.get('/myalerts').success(function(data) {
        	console.log(data);
        $scope.myalerts = data.something;
        });
        
        $http.get('/user').success(function(data) {
        	console.log(data.admin);
        $rootScope.admin = data.admin;
        $rootScope.username = data.username;
        });
      
        
}]);

