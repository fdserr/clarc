(ns clarc.local-db.index
  (:require
   [clarc.local-db.core :as db]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

;; TODO: app db

(defcard "# Local DB Setup")

(defcard test-card-db
  (fn [store _]
    (html [:div (str (:__db @store))]))
  (db/install-card-db (atom {})))
