rootApp.service('appNotifyService', function (toaster) {

    this.error = function (msg, title, stayFor, clear) {

        // 0 to make it sticky
        stayFor = stayFor || 4000;
        title = title || '';
        clear = clear || false;

        if (clear) {
            toaster.clear();
        }

        toaster.pop('error', title, msg, stayFor);

    };

    this.info = function (msg, title, stayFor, clear) {

        // 0 to make it sticky
        stayFor = stayFor || 3000;
        title = title || '';
        clear = clear || false;

        if (clear) {
            toaster.clear();
        }

        toaster.pop('info', title, msg, stayFor);

    };

    this.success = function (msg, title, stayFor, clear) {

        // 0 to make it sticky
        stayFor = stayFor || 4000;
        title = title || '';
        clear = clear || false;

        if (clear) {
            toaster.clear();
        }

        toaster.pop('success', title, msg, stayFor);

    };

    this.warning = function (msg, title, stayFor, clear) {

        // 0 to make it sticky
        stayFor = stayFor || 3000;
        title = title || '';
        clear = clear || false;

        if (clear) {
            toaster.clear();
        }

        toaster.pop('warning', title, msg, stayFor);
    };
});
