$(function() {
    var appsElement = $("#appName");
    var apisElement = $("#apiName");
    var applicationList = {};
    populateAppList();

    /**
     * Populate Application Name 'select' element with subscribed application list
    **/
    function populateAppList() {
        var params = {action: "getApplications"};
        
        $.get("../blocks/application/application-list/ajax/application-list.jag", params, function(data) {
            if (data && data.error == false) {
                applicationList = data.applications;

                $.each(data.applications, function() {
                    appsElement.append($("<option />").val(this.id).text(this.name));
                });

                appsElement.selectpicker('refresh');
                populateApiList();
            }
        });
    }

    /**
     * Populate API Name 'select' element with subscribed list of APIs
     * provider--apiName--apiVersion format is used to list the APIs. This is to
     * avoid complexity in the UI and the logic required to show the API list
    **/
    function populateApiList() {
        var appId = $("#appName option:selected").text();
        var params = {action: "getSubscriptionByApplication", app: appId, groupId: ""};
        apisElement.empty();

        $.get("../blocks/subscription/subscription-list/ajax/subscription-list.jag", params, function(data) {
            if (data && data.error == false) {
                $.each(data.apis, function() {
                    var apiId = this.apiName + '--' + this.apiVersion;
                    apisElement.append($("<option />").val(apiId).text(apiId));
                });

                apisElement.selectpicker('refresh');
            }
        });
    }


    // Data table configuration for loading configured alert information
    $('#configTable').datatables_extended({
        "ajax": {
            "url": jagg.getBaseUrl()+ "/site/blocks/configure-alert/ajax/configure-alert.jag?action=getAlertConfigs",
            "dataSrc": function (json) {
                if (json.error) {
                    return {};
                }

                // Find the application name of each applicationId in the result
                // This is required to populate the application name in the table
                var configs = json.list;
                configs.forEach(function(config) {
                    applicationList.forEach(function(app) {
                        if (app.id == config.applicationId) {
                            config.applicationName = app.name;
                        }
                    });
                });

                return configs;
            }
        },
        "columns": [
            {"data": "applicationName"},
            {"data": "apiName"},
            {"data": "apiVersion"},
            {"data": "thresholdRequestCountPerMin"},
            {
                "data": "apiName",
                "render": function (data, type, rec, meta) {

                    return '<a href="#" title="' + i18n.t("Remove") + '" class="btn btn-sm padding-reduce-on-grid-view deleteConfig"' +
                            'data-id="' + rec.applicationId + '" data-name="' + rec.apiName + '" data-version="' + rec.apiVersion + '">' +
                                '<span class="fw-stack">' +
                                    '<i class="fw fw-ring fw-stack-2x"></i>' +
                                    '<i class="fw fw-delete fw-stack-1x"></i>' +
                                '</span>' +
                                '<span class="hidden-xs">' + i18n.t("Remove") + '</span>' +
                            '</a>';
                }
            }
        ],
    });

    // Register on change event listener for Application Name 'select' element
    appsElement.on('changed.bs.select', function (e) {
        populateApiList();
    });

});
