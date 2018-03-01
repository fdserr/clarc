(ns clarc.mini-app.core
  (:require
    [clarc.mini-app.app :as app]))

(defn render-app
  [store]
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (app/ui-app store) node)))

(defonce app-store (add-watch (atom {})
                              :render
                              (fn [_ a _ _] (render-app a))))

(defonce _ (app/dispatch! app-store app/action-init))

;;;

(comment ;; enter at the REPL

 (in-ns 'clarc.mini-app.core)

 (add-watch app-store :watch
            #(println "WATCH :value "
                      "old=" (:value %2)
                      " new=" (:value %3)))

 (swap! app-store assoc :value 10000)

 (app/dispatch! app-store app/action-init)

 "")
