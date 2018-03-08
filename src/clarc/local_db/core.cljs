(ns clarc.local-db.core
  (:require
   [datascript.core :as d]))

(defn install-card-db
  "Install a DataScript DB in the provided store (IAtom), and return it.
   This is intended for devcards usage, not for production."
  ([store]
   (install-card-db store nil nil))
  ([store schema]
   (install-card-db store schema nil))
  ([store schema tx]
   (let [c (if schema
             (d/create-conn schema)
             (d/create-conn))
         d (when tx
             (:tx-data (d/transact! c tx)))]
     (swap! store assoc-in [:__db :schema] (or schema {}))
     (swap! store assoc-in [:__db :datoms] (or d []))
     store)))

(defn store-db
  "Obtain the DB value from the store (equiv. to datascript.core/db)."
  [store]
  (let [{:keys [schema datoms]} (:__db @store)]
    (if-not (and schema datoms)
      (throw (js/Error. "DB not found in store"))
      (d/init-db datoms schema))))

(defn transact-state-db
  ;TODO prod version, without history
  "Apply transaction tx to the db present in state.
   state must be deref'd from a previously initialised store.
   Different from datascript.core/transact, as it takes a VALUE, not a REF.
   Returns new state (NOT the tx result as would DS and Datomic transact)."
  [state tx]
  (let [{:keys [schema datoms]} (:__db state)
        _ (if-not (and schema datoms)
            (throw (js/Error. "DB not found in state")))
        c (d/conn-from-datoms datoms schema)
        tx-res (d/transact! c tx)]
    (assoc-in state [:__db :datoms] (d/datoms (:db-after tx-res) :eavt))))
