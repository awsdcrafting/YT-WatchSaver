Features:
---
It creates a local webserver. <br>
The webserver listens on a port 15643 on default, or a port specified by you.

Features:
- json post request to '/add' to add an url with a titel
    - example: { "title": "An example title", "url": "http://example.org"}
- json post request to '/save' to save all logged to a csv/json file
    - example: { "type": "csv" }
- add sqlite db

Planned:
- gui

Why does this project exists?

Mainly because I wanted to log the youtube videos that are played by the youtube mix / autoplay feature.
So that I can create a playlist with the songs later.