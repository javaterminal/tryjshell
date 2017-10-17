document.addEventListener("DOMContentLoaded", function (event) {

    var term = new Terminal({
        cursorBlink: true,
        scrollback: 2500
    });
    let termDom = document.querySelector('#terminal');

    window.term = term;

    let protocol = location.protocol.indexOf("https") !== -1 ? "wss" : "ws";
    let ws = new WebSocket(protocol + "://" + location.host + "/terminal");

    function initializeTerminal() {

        term.on('resize', app.resizeTerminal);
        term.on('data', app.onCommand);

        term.open(termDom, true);

        term.linkify();
        term.toggleFullscreen();
        term.fit();
        term.focus();

        app.onTerminalReady();

        window.addEventListener('resize', debounce(term.fit.bind(term), 100, false), false);
        window.addEventListener('orientationchange', debounce(term.fit.bind(term), 100, false), false);

    }

    ws.onopen = () => {
        initializeTerminal();
    }

    ws.onerror = (e) => {
        alert("Connection error, try again..")
    }

    ws.onclose = () => {
        alert("Connection closed, reload page..")
    }

    ws.onmessage = (e) => {
        let data = JSON.parse(e.data);
        switch (data.type) {
            case "TERMINAL_PRINT":
                term.write(data.text);
        }
    }

    function action(type, data) {
        let action = Object.assign({
            type
        }, data);

        return JSON.stringify(action);
    }

    let app = {
        onCommand(command) {
            ws.send(action("TERMINAL_COMMAND", {
                command
            }));
        },
        resizeTerminal({cols, rows}) {
            ws.send(action("TERMINAL_RESIZE", {
                cols, rows
            }));

            // term.refresh(1, cols)
        },
        onTerminalReady() {
            ws.send(action("TERMINAL_READY"));
        },
        initializeStyles() {

        }
    };

});

const debounce = function (func, wait, immediate) {
    var timeout;
    return () => {
        const context = this, args = arguments;
        const later = function () {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        const callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
    };
}