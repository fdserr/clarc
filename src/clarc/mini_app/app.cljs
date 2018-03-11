(ns clarc.mini-app.app
  (:require
    [sablono.core :include-macros true :refer [html]]))

;;; Remember this: store is just a box, state is the value in the box.

(defn dispatch!
  "Single dispatch entry point used by all actions.
   store: an atom (or any ISwap type).
   action: a fn of state that returns a new state.
   On success, the value in store will be the result of fun.
   On error, the value in store will be unchanged."
  [store action & args]
  (apply swap! store action args)
  nil)

(defn action-init
  "Action to initialise the store value."
  [_]
  {:app-title "My App"
   :value 0})

(defn action-inc
  "Action to increment :value in state."
  [state]
  (update-in state [:value] inc))

(defn ui-app
  "Application UI."
  [store]
  (let [state @store ; get the state from store
        {:keys [app-title value]} state] ; destructure state
    (html
     [:div {:class-name "d-flex flex-column justify-content-center"}
      [:div
       [:h2 {:class-name "text-center"} app-title]
       [:h1 {:class-name "text-center"} (str value)]]
      [:button {:on-click #(dispatch! store action-inc)} ; dispatch an action
       "Increment"]
      [:p "   "]])))
