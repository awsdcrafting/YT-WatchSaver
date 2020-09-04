// ==UserScript==
// @name         YTWS script
// @namespace    https://github.com/awsdcrafting/YT-WatchSaver
// @version      0.2
// @description  YTWS log script
// @author       scisneromam
// @match        https://www.youtube.com/*
// @run-at       document-start
// @grant        GM_xmlhttpRequest
// @noframes
// @require      http://xregexp.com/v/3.2.0/xregexp-all.js
// ==/UserScript==


(function () {
    function $(group, collapsed, block) {
        collapsed ? console.groupCollapsed(group) : console.group(group)
        try {
            block()
        } catch (e) {
            console.error(e)
        }
        console.groupEnd(group)
    }


    window.addEventListener("yt-navigate-finish", () => {
        $('[YoutubeAutotranslateCanceler]', false, () => {
            console.log('Event fired')
            if (window.location.href.includes("/watch")) {
                var titleMatch = document.title.match(/^(?:\([0-9]+\) )?(.*?)(?: - YouTube)$/); // ("(n) ") + "TITLE - YouTube"
                var videoUrl = window.location.href
                if (!titleMatch) {
                    console.log("ERROR: Video is deleted!");

                } else {
                    var title = titleMatch[1]
                    GM_xmlhttpRequest({
                        method: "POST",
                        url: "http://localhost:15643/add",
                        data: "{\"title\":\"" + title + "\",\"url\":\"" + videoUrl + "\"}",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        onload: function (response) {
                            console.log("got response");
                            console.log(response);
                        }
                    });
                }
            }
        })
    });
})();
