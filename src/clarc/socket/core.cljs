(ns clarc.socket.core
  (:require
   [clarc.socket.impl :as impl]
   [datascript.core :as d]))

(defn install-card-socket
  "Install a mock server socket in card store.
   Messages sent are just printed to console.
   Obviously not for production use."
  [store]
  (swap! store
         #(-> %
              (assoc-in [:__socket :type] :mock)
              (assoc-in [:__socket :sent] {})))
  store)

(defmulti send
  "Send a message to the server via the socket installed in store.
   msg: a map with a :msg-type key." ;TODO spec msg
  (fn [state _] (get-in state [:__socket :type]))
  :default :sente)

(defn envelope
  "Wrap msg in envelope."
  [state msg]
  (let [req-id (d/squuid)
        msg (-> msg
                (assoc :user (:user state))
                (assoc :req-id req-id))]))

(defmethod send :mock
  [state msg]
  (println "SEND: " msg)
  (let [msg (envelope state msg)
        req-id (:req-id msg)]
    (update-in state [:__socket :sent] assoc req-id)))
