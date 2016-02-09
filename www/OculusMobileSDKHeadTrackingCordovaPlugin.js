var exec = require('cordova/exec');
var cordova = require('cordova');

(function() {

    window.addEventListener('error', function(event) {
        var errorMessage = event.message;
        var url = event.filename;
        var lineNumber = event.lineno;
        var columnNumber = event.colno;
        alert("ERROR: " + errorMessage + " at " + url + " : " + lineNumber + " : " + columnNumber);
    });

    var eventsListeners = {}; 
    var data = {
        timeStamp: 0,
        orientation: { x: 0, y: 0, z: 0, w: 1 },
        linearVelocity: { x: 0, y: 0, z: 0 },
        linearAcceleration: { x: 0, y: 0, z: 0 },
        angularVelocity: { x: 0, y: 0, z: 0 },
        angularAcceleration: { x: 0, y: 0, z: 0 }
    };
    function eventHandler(event) {
        if (event.name === 'EVAL_JS') { 
            window.eval(event.data); 
            return; 
        } 
        var eventListeners = eventsListeners[event.name]; 
        if (!eventListeners) return; 
        for(var i = 0; i < eventListeners.length; i) { 
            eventListeners[i](event.data); 
        } 
    }
    function dataUpdateSuccess(d) {
        data = d;
    }
    if (!window.OculusMobileSDKHeadTracking) {
        window.OculusMobileSDKHeadTracking = {
            start: function() {
                cordova.exec(eventHandler, eventHandler, "OculusMobileSDKHeadTracking", "start", []);
                cordova.exec(dataUpdateSuccess, eventHandler, "OculusMobileSDKHeadTracking", "setDataUpdateCallback", []);
            },
            addEventListener: function(eventName, listener) { 
                if (typeof(eventName) !== 'string') return; 
                if (typeof(listener) !== 'function') return; 
                var eventListeners = eventsListeners[eventName]; 
                if (!eventListeners) { 
                    eventListeners = []; 
                    eventsListeners[eventName] = eventListeners; 
                } 
                if (eventListeners.indexOf(listener) < 0) { 
                    eventListeners.push(listener); 
                } 
            },
            removeEventListener: function(eventName, listener) { 
                if (typeof(eventName) !== 'string') return; 
                if (typeof(listener) !== 'function') return; 
                var eventListeners = eventsListeners[eventName]; 
                if (!eventListeners) return; 
                var i = eventListeners.indexOf(listener); 
                if (i > 0) { 
                  eventListeners.splice(i, 1); 
                } 
            },
            getTimeStamp: function() { 
                return data.timeStamp;
            },
            getOrientation: function() { 
                return data.orientation;
            },
            getLinearVelocity: function() { 
                return data.linearVelocity;
            },
            getLinearAcceleration: function() { 
                return data.linearAcceleration;
            },
            getAngularVelocity: function() { 
                return data.angularVelocity;
            },
            getAngularAcceleration: function() { 
                return data.angularAcceleration;
            },
            getData: function() { 
                return data;
            }
        };
    }
})();
