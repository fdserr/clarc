(ns clarc.core
  (:require
   #_[om.core :as om :include-macros true]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true])
  (:require-macros))

(enable-console-print!)

(dc/start-devcard-ui!)

(defcard first-card
  (sab/html [:div
             [:h1 "This is your first devcard!"]]))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html
