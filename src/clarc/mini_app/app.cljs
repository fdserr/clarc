(ns clarc.mini-app.app
  (:require
    [sablono.core :include-macros true :refer [html]]))

(defn dispatch!
  [store fun & args]
  (apply swap! store fun args)
  nil)

(defn action-init
  [_]
  {:app-title "My App"
   :value 0})

(defn action-inc
  [state]
  (update-in state [:value] dec))

(defn ui-app
  [store]
  (let [{:keys [app-title value]} @store]
    (html
     [:div {:class-name "d-flex flex-column justify-content-center"}
      [:div
       [:h2 {:class-name "text-center"} app-title]
       [:h1 {:class-name "text-center"} (str value)]]
      [:button {:on-click #(dispatch! store action-inc)} "Increment"]
      [:p "   "]])))
