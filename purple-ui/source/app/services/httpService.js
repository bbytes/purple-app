rootApp.service('httpService', function ($http, $location, $rootScope) {
    this.postRequest = function (url, data, contentType, callback) {
        $http({
            method: 'POST',
            url: url,
            data: data,
            headers: {
                'Content-Type': contentType
            }
        }).success(function (data) {
            callback(data);
        }).error(function (data, status, headers, config) {

        });
    };

    this.postRequestWithParams = function (url, params, data, contentType, callback) {
        $http({
            method: 'POST',
            url: url,
            params: params,
            data: data,
            headers: {
                'Content-Type': contentType
            }
        }).success(function (data) {
            callback(data);
        }).error(function (data) {
            alert("error " + data);
        });
    };

    this.getRequest = function (url, callback) {
        $http({
            method: 'GET',
            url: url
        }).success(function (data) {
            callback(data);
        }).error(function (data, status) {

        });
    };
});


