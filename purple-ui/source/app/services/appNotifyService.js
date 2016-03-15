rootApp.service('appNotifyService', function (toaster, $filter) {

    this.error = function (msg, title, stayFor, clear) {

        // 0 to make it sticky
        stayFor = stayFor || 5000;
        title = title || '';
        clear = clear || false;

        if (clear) {
            toaster.clear();
        }

        toaster.pop('error', title, $filter('translate')(msg), stayFor);

    };

    this.info = function (msg, title, stayFor, clear) {

        // 0 to make it sticky
        stayFor = stayFor || 5000;
        title = title || '';
        clear = clear || false;

        if (clear) {
            toaster.clear();
        }

        toaster.pop('info', title, $filter('translate')(msg), stayFor);

    };

    this.success = function (msg, title, stayFor, clear) {

        // 0 to make it sticky
        stayFor = stayFor || 5000;
        title = title || '';
        clear = clear || false;

        if (clear) {
            toaster.clear();
        }

        toaster.pop('success', title, $filter('translate')(msg), stayFor);

    };

    this.warning = function (msg, title, stayFor, clear) {

        // 0 to make it sticky
        stayFor = stayFor || 5000;
        title = title || '';
        clear = clear || false;

        if (clear) {
            toaster.clear();
        }

        toaster.pop('warning', title, $filter('translate')(msg), stayFor);
    };
});
