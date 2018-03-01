(ns clarc.mini-app.index
  (:require
   [clarc.mini-app.app :as app]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard app-example
  "See `src/mini_app/*`"
  (fn [store]
    (app/ui-app store))
  {:app-title "Incrementer"
   :value 0}
  {:inspect-data true
   :history true})

;;;
