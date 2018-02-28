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
  (update-in state [:value] inc))

(defn ui-app
  [store]
  (let [{:keys [app-title value]} @store]
    (html
     [:div
      [:h1 app-title]
      [:p (str value)]
      [:button {:on-click #(dispatch! store action-inc)} "Inc"]])))
