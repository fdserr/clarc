(ns clarc.socket.index
  (:require
   [clarc.socket.core :as socket]
   [clarc.socket.impl :as impl]
   [clarc.mini-app.app :as w] ;TODO make lib
   [cljs.test :refer [is]]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard "# Socket: 2 ways async server communication")

(defcard
  (html [:button {:on-click #(impl/test-session)} "TEST"]))

(defn action-dummy
  [state]
  ; transition state
  ; ...
  (socket/send ; effect => new state
    state {:msg-type :msg.type/dummy
           :msg-payload {}}))

(deftest test-card-socket
  (let [store (-> (atom {})
                  (socket/install-card-socket))
        _ (w/dispatch! store action-dummy)
        [[_ msg]] (seq (get-in @store [:__socket :sent]))]
    (is (= :msg.type/dummy (:msg-type msg))
        "Message type of sent message should be 'dummy'")))
