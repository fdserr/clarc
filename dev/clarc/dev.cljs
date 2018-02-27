(ns clarc.dev
  (:require
   [clarc.core :as clarc]

   clarc.index
   clarc.meta.index

   [devcards.core :as dc :include-macros true :refer [defcard deftest]]))

(enable-console-print!)

(let [node (.getElementById js/document "main-app-area")]
  (when-not node
    (dc/start-devcard-ui!)))

(defn js-loaded
  "Figwheel render on code reload"
  []
  (clarc/render-app clarc/app-store))
