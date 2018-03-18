(ns clarc.server.index
  (:require
   [clarc.local-db.core :as db]
   [cljs.test :refer [is]]
   [datascript.core :as d]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

;; TODO: app server

(defcard
  "# Server
To start the web server, in a terminal, from the main app folder:
```
$ lein run
```
  ")
