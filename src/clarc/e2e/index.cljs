(ns clarc.e2e.index
  (:require
   [clarc.mini-app.app :as app]
   [cljs.test :refer [is]]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard
  "
[ [Home](#!/clarc.index) ]
## End to end
Code: `src/clarc/e2e/*`

  ")
