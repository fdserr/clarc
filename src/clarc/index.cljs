(ns clarc.index
  (:require
   clarc.intro.card1
   clarc.intro.card2
   clarc.intro.qa
   clarc.mini-app.index
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(let [node (.getElementById js/document "main-app-area")]
  (when-not node
    (dc/start-devcard-ui!)))
