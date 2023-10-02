# electric-table

Table component for [electric clojure](https://github.com/hyperfiddle/electric). WIP!

The component offers 
* sorting
* filtering
* pagination (WIP)
* slots for child components

## Usage

```clj
(Table. data fields items-per-page enable-search? classmap slotmap)
```
See `app.table_page.cljc`.

## Setup

Initialize test data in postgres via `docker-compose up`.

```
$ clj -A:dev -X user/main

Starting Electric compiler and server...
shadow-cljs - server version: 2.20.1 running at http://localhost:9630
shadow-cljs - nREPL server started on port 9001
[:app] Configuring build.
[:app] Compiling ...
[:app] Build completed. (224 files, 0 compiled, 0 warnings, 1.93s)

👉 App server available at http://0.0.0.0:8080
```

