(ns clarc.app
  (:require
    [sablono.core :as sab :include-macros true :refer [html]]))

(defn dispatch!
  [a f & args]
  (apply swap! a f args)
  nil)

(defn ac-init
  [_]
  {:app-title "My App"
   :value 0})

(defn ac-inc
  [s]
  (update-in s [:value] inc))

(defn ui-app
  [a]
  (html [:div
         [:h1 (:app-title @a)]
         [:p (:value @a)]
         [:button {:on-click #(dispatch! a ac-inc)} "Inc"]]))
