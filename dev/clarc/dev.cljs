(ns clarc.dev
  (:require
   [clarc.mini-app.core :as app]

   clarc.index
   clarc.mini-app.index

   [devcards.core :as dc :include-macros true :refer [defcard deftest]]))

(enable-console-print!)

(defn js-loaded
  "Figwheel render on code reload"
  []
  (app/render-app app/app-store))
