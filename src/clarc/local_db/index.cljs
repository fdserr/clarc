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
  "## DataScript
DataScript is a small footprint, in-memory DataLog DB written by Nikita Prokopov
(_aka._ \"Tonsky\"). We use our own, slightly different API to create,
obtain, and transact  against a db instance hosted in our store.

The rest is plain DataScript,
as [documented here](https://github.com/tonsky/datascript).
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
Other than that they are similar to DataScript transactions.
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

;;; Example

(defn dispatch!
  "Dispatch action to store"
  [store fun & args]
  (apply swap! store fun args)
  nil)

(defn action-change-input
  "Input changed"
  [state input]
  (assoc state :input input))

(defn transact-add-person
  "Transaction function to add a person by name.
   Blank or nil name is rejected, as well as duplicates.
   person: a string."
  [conn person]
  (when (or (nil? person)
            (= "" person))
    (throw (ex-info "Person name cannot be blank."
                    {:input person})))
  (let [found (d/q '[:find ?e ?name :in $ ?name :where [?e :person/name ?name]]
                   conn person)]
    (when-not (empty? found)
      (throw (ex-info "Person name must be unique."
                      {:input person :found found}))))
  [(-> {:db/id -1} ; temp ID
       (assoc :person/name person))]) ; return transaction data (vector!)

(defn action-add-person
  "Person added"
  [state person]
  (try
    (-> state
        (db/transact-state-db
          [[:db.fn/call transact-add-person person]]) ; prefer transaction fn
        (dissoc :input) ; clear form
        (dissoc :error)) ; clear error
    (catch ExceptionInfo e
           (assoc state :error (.-message e)))))

(defn ui-person
  "person component"
  [person]
  (html [:li {:key (:id person)} (:name person)]))

(defn ui-form
  "Form component"
  [store]
  (let [state @store
        input (or (:input state) "")
        error (or (:error state) "")
        conn (db/store-db store)
        query '[:find ?e ?name
                :in $
                :where [?e :person/name ?name]]
        persons-data (d/q query conn) ; query db for persons
        persons (map #(zipmap [:id :name] %) ; format for ui
                     persons-data)]
    (html [:div
           [:input
            {:value input
             :on-change #(dispatch! store action-change-input
                                    (-> % .-target .-value))}]
           [:p {:style {:color "red"}} error]
           [:button
            {:on-click #(dispatch! store action-add-person input)}
            "Submit"]
           [:p "  "]
           [:ul (map ui-person persons)]])))

(defcard db-persons
  "Add a person in local DB. Try empty input or duplicate."
  (fn [store]
    (ui-form store))
  (db/install-card-db ; install db
   (atom {})         ; in store
   {:db/ident {:db.unique :db.unique/identity} ; using schema
    :person/name {:db.unique :db.unique/identity}}
   [{:person/name "John"}  ; with initial data
    {:person/name "Jim"}])
  {:history true})
   ; :inspect-data true})
