(ns clarc.socket.core
  (:require
   [cljs.core.async 
    :refer [put! chan <! >! timeout close!]])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]))

(defn send!
  [state msg]
  (put! (:send-channel state) msg)
  nil)

(defmulti received
  (fn [store {:keys [msg-type]}] msg-type))

(defmulti process
  (fn [{:keys [msg-type]}] msg-type))

(defn connect-mock
  ([store]
   (connect-mock store 16))
  ([store delay]
   (let [c (chan)]
     (swap! store assoc :send-channel c)
     (go-loop [m (<! c)]
       (.setTimeout js/window
                    #(received store (process m))
                    delay)
       (recur (<! c)))
     store)))
