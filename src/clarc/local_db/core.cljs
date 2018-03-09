(ns clarc.local-db.core
  (:require
   [datascript.core :as d]))

(defn install-card-db
  "Install a DataScript DB in the provided store. Returns the store.
   This is intended for devcards usage, not for production.
   store: an atom (or a type implementing IAtom, such as `reagent.core/atom`).
   schema: optional DataScript schema.
   tx: initial data, as DataScript transaction data."
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
     (swap! store
            #(-> %
                 (assoc-in [:__db :type] :history)
                 (assoc-in [:__db :schema] (or schema {}))
                 (assoc-in [:__db :datoms] (or d []))))
     store)))

(defn install-db
  "Install a DataScript DB in the provided store. Returns the store.
   store: an atom (or a type implementing IAtom, such as `reagent.core/atom`).
   schema: optional DataScript schema.
   tx: initial data, as DataScript transaction data."
  ([store]
   (install-db store nil nil))
  ([store schema]
   (install-db store schema nil))
  ([store schema tx]
   (let [conn (if schema
                (d/create-conn schema)
                (d/create-conn))]
     (d/transact! conn (or tx []))
     (swap! store assoc-in [:__db :conn] conn)
;     (println @store)
     store)))

;;;

(defmulti store-db
  "Obtain the DB value from the store (equiv. to datascript.core/db)."
  (fn [store] (get-in @store [:__db :type]))
  :default :conn)

(defmethod store-db :conn
  [store]
  (let [conn (get-in @store [:__db :conn])]
    (println conn)
    (if-not conn
      (throw (js/Error. "DB not found in store"))
      (d/db conn))))

(defmethod store-db :history
  [store]
  (let [{:keys [schema datoms]} (:__db @store)]
    (if-not (and schema datoms)
      (throw (js/Error. "DB not found in store"))
      (d/init-db datoms schema))))

;;;

(defmulti transact-state-db
  "Apply transaction tx to the db present in state.
   state must be deref'd from a previously initialised store.
   Different from datascript.core/transact, as it takes a VALUE, not a REF.
   Returns new state (NOT the tx result as would DS and Datomic transact)."
  (fn [state tx] (get-in state [:__db :type]))
  :default :conn)

(defmethod transact-state-db :conn
  [state tx]
  (let [conn (get-in state [:__db :conn])
;        _ (println conn)
        _ (when-not conn
            (throw (js/Error. "DB not found in state")))
        tx-res (d/transact! conn tx)]
    (assoc-in state [:__db :last-transaction] (:tx tx-res))))

(defmethod transact-state-db :history
  [state tx]
  (let [{:keys [schema datoms]} (:__db state)
        _ (when-not (and schema datoms)
            (throw (js/Error. "DB not found in state")))
        c (d/conn-from-datoms datoms schema)
        tx-res (d/transact! c tx)]
;        _ (println tx-res)]  ;TODO: check for errors
    (assoc-in state [:__db :datoms] (d/datoms (:db-after tx-res) :eavt))))
