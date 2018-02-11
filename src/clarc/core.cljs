(ns clarc.core
  (:require
    [clarc.app :as app])
  (:require-macros))

(enable-console-print!)

(defn render-app [a]
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (app/ui-app a) node)))

(defonce app-store (-> (atom {})
                       (add-watch :render
                                  (fn [k a o n] (render-app a)))))

(defonce _ (app/dispatch! app-store app/ac-init))

;;;

(defn js-loaded
  []
  (render-app app-store))
