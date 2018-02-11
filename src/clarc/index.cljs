(ns clarc.index
  (:require
   [clarc.core :as clarc]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true])
  (:require-macros))

(dc/start-devcard-ui!)

(defcard
  "[Go to App](/index.html)")
