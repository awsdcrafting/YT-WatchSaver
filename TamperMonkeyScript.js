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



(function(){
    function $(group, collapsed, block){
        collapsed ? console.groupCollapsed(group) : console.group(group)
        try { block() } catch(e) { console.error(e) }
        console.groupEnd(group)
    }


    function addSite()
    {
        if (window.location.href.includes ("/watch")){
            var titleMatch = document.title.match (/^(?:\([0-9]+\) )?(.*?)(?: - YouTube)$/); // ("(n) ") + "TITLE - YouTube"
            var videoUrl = window.location.href
            if (!titleMatch) {
                console.log ("ERROR: Video is deleted!");
                return;
            } else {
                var videoTitle = titleMatch[1]
                var jsData = {
                    title: videoTitle,
                    url : videoUrl
                }
                var jsString = JSON.stringify(jsData)
                console.log(jsData)
                console.log(jsString)
                GM_xmlhttpRequest ( {
                    method:     "POST",
                    url:        "http://localhost:15643/add",
                    data:       jsString,
                    headers:    {
                        "Content-Type": "application/json"
                    },
                    onload:     function (response) {
                        console.log ("got response");
                        console.log (response);
                    }
                } );
            }
        }
    }

    window.addEventListener("yt-navigate-finish", () => {
        $('[YoutubeAutotranslateCanceler]', false, () => {
            console.log('Event fired')
            setTimeout(addSite, 1000); //wait a sec so that everything can load
        })
    });
})();
