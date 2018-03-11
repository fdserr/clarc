(ns clarc.socket.index
  (:require
   [clarc.socket.core :as socket]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard "# Socket: 2 ways server communication")

(defcard
  (html [:button {:on-click #(socket/test-session)} "TEST"]))
