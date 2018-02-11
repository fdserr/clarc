(ns clarc.core
  (:require
   [sablono.core :as sab :include-macros true :refer [html]])
  (:require-macros))

(enable-console-print!)

(defn dispatch!
  [f a & args]
  (apply swap! a f args))

(defn a-inc
  [s]
  (update-in s [:value] inc))

(defn ui-app
  [a]
  (html [:div
         [:h1 (:app-title @a)]
         [:p (:value @a)]
         [:button {:on-click #(dispatch! a-inc a)} "Inc"]]))

(defn render-app [a]
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (ui-app a) node)))

(defonce app-store (-> (atom {:app-title "My App"
                              :value 0})
                       (add-watch :render
                                  (fn [k a o n] (render-app a)))))

(defn loaded
  []
  (render-app app-store))

(loaded)
