(ns clarc.local-db.index
  (:require
   [clarc.local-db.core :as db]
   [cljs.test :refer [is]]
   [datascript.core :as d]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

;; TODO: app db

(defcard
  "# Local DB
We want to be able to use a store* as a DataLog db, so we can transact and
query against a rich data model. Nevertheless, we don't want to give up
the plain `atom` interface of the store, to deal with simpler state values.

(*)store: any implementation of `IAtom` (eg. `reagent/atom`),
used to hold the current state.
 ")

(defcard
  "## Setup
There are two ways to setup a DataScript db in local `store`:

1. when using the store with a devcard, we want to preserve
the state history feature: pass the store to `clarc.local-db.core/install-card-db`.
2. if history is not needed (eg. main app), use `clarc.local-db.core/install-db`.
  ")

(defcard
  "## Transactions
Because they'll mostly happen within a state transition (a function of state
that returns a new state), transactions are made against the value of the store,
not the store itself.
Also, transactions return a new state (not a transaction result).
Other than that they are similar to DataScript/Datomic transactions.
```
(transact-state-db @store [<tx-data>])
```
  ")

(defcard
  "## Queries
Queries are run against the db current value, which can be obtained from the
store with `clarc.local-db.core/store-db`. This is similar to using `datascript.core/db`.
```
(let [conn (store-db store)])
```
Use query API from DataScript. `pull` expressions are also available.
```
(datascript.core/pull conn '[*] [:id 1])
```
  ")

(deftest test-card-db
  (let [store (-> (atom {})
                  (db/install-card-db {:db/ident {:db.unique :db.unique/identity}
                                       ::k1 {:db.unique :db.unique/identity}}))
        _ (swap! store db/transact-state-db [{::k1 1 ::k2 2}])
        conn (db/store-db store)
        res (d/pull conn '[::k1 ::k2] [::k1 1])]
    (is (= {::k1 1 ::k2 2} res))))

(deftest test-store-db
  (let [store (-> (atom {})
                  (db/install-db {:db/ident {:db.unique :db.unique/identity}
                                  ::k1 {:db.unique :db.unique/identity}}))
        _ (swap! store db/transact-state-db [{::k1 1 ::k2 2}])
        conn (db/store-db store)
        res (d/pull conn '[::k1 ::k2] [::k1 1])]
    (is (= {::k1 1 ::k2 2} res))))
