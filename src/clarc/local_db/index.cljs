(ns clarc.local-db.index
  (:require
   [clarc.local-db.core :as db]
   [cljs.test :refer [is testing]]
   [datascript.core :as d]
   [clojure.spec.alpha :as s]
   [clojure.set :refer [intersection difference]]
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
   Blank or nil person is rejected, as well as duplicates.
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
        (dissoc :error-msg) ; clear error
        (dissoc :error-data))
    (catch ExceptionInfo e
           (-> state
               (assoc :error-msg (.-message e))
               (assoc :error-data (ex-data e)))))) ; error data might be helpful

(defn ui-person
  "Person UI component"
  [person]
  (html [:li {:key (:id person)} (:name person)]))

(defn ui-form
  "Form UI component"
  [store]
  (let [state @store
        input (or (:input state) "")
        error (or (:error-msg state) "")
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
           [:button
            {:on-click #(dispatch! store action-add-person input)}
            "Submit"]
           [:p {:style {:color "red"}} error]
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

;;; Updating an existing entity

(defcard
  "
## Updating entities

What we're trying to solve:

- we want an idiomatic way to apply model constraints (mandatory value, type,
format, validity rules, ...)

- when we update an existing entity, we need to take into account that some
values may be reset to nothing (nil, NULL, \"\"); Datalog has no concept of NULL
so transaction should _retract_ these attributes. Retraction datoms need the old value.

Data flow for a typical user interaction:

1. entity data is fecthed from db
2. entity data is tramsformed into form data (strings)
3. form data items are changed by the user, wrong changes should be highlited for the user to correct
4. form data is submitted, if wrong the reason should be highlited for the user
5. form data is transformed into entity data (typed values or nils, foreign keys resolution, ...), this should succeed because of (4)
6. entity data is checked against attributes constraints, if wrong it should be rejected
7. entity data is checked against relationships constraints (existence, components, ...), if wrong it should be rejected
8. entity data is transformed into an entity transaction
9. the transaction is upgraded to impact relationships (cascade delete, link removals, ...)
10. the transaction is applied to the db
  ")

(defcard
  "
### Entities with no relationship

We use `clojure.spec` to declare attribute constraints. Basically, if you can write a predicate, you can write specs.
Read the [spec Guide](https://clojure.org/guides/spec), only up to section \"Using spec for validation\" for now.

We implement the complete data flow using simple functions.
There is room for enhancements and optimisations, the point is to get the flow right and clear.

In this example, we skip steps (7) and (9). A generic solution to manage all kinds of relationships
has yet to be invented. Transactions just don't compose easily. The best fit to date is still to write specific procedures
for the problem at hand (SQL has stored procedures for that purpose). We'll propose some patterns in a later section.

Inserting a new entity is quite trivial in Datalog, so we focus on updating existing ones.
Most of the core flow is reusable for inserts, keep specific, explicit UIs and actions.

The form intentionally has no validation, to be able to transact any values.
Open the JS console to check for transaction errors (some are already printed due to the tests).
  ")

(defn fetch-person
  [store e-mail]
  (let [conn (db/store-db store)]
    (d/pull conn '[*] [:person/e-mail e-mail])))

(defn fetch-persons
  [store]
  (let [conn (db/store-db store)]
    (d/pull-many conn '[*] [[:person/e-mail "jdoe@me.com"]
                            [:person/e-mail "jram@me.com"]
                            [:person/e-mail "mjones@me.com"]])))

(defn person-entity->form
  "Note: we keep :db/id as string."
  [person]
  (into {} (map (fn [[k v]] [k (str v)]) person)))

(defn fetch-form-data
  [store]
  (:form @store))

(defn action-edit-person
  [state store e-mail]
  (let [person (fetch-person store e-mail)]
    (assoc state :form (person-entity->form person))))

(defn action-change-form-input
  [state key value]
  (assoc-in state [:form key] value))

(defn prune-entity
  "Remove nil attributes."
  [e]
  (into {} (remove (fn [[k v]] (nil? v))) e))

(defn transact-upsert
  "Insert or update an entity (map) conforming to the given spec."
  [conn entity spec]
  (let [new (prune-entity entity) ;remove nil attrs
        new (if-not (s/valid? spec new) ;conform to spec
              (throw (ex-info (str "Invalid entity " spec)
                              (s/explain-data spec new)))
              new)]
    (if-let [old (d/touch (d/entity conn (:db/id entity)))] ;if updating
      (let [removed-keys (difference (set (keys entity)) (set (keys new)))
            removed-keyvals (map (fn [k] [k (k old)]) removed-keys)
            retract-datoms (map (fn [[attr val]] ;build retracts
                                  [:db/retract (:db/id entity) attr val])
                                removed-keyvals)]
        (into [new] retract-datoms)) ;tr with retracts
      [new]))) ;tr

; Person spec
(s/def :person/first-name string?)
(s/def :person/last-name string?)
(s/def :person/e-mail string?)
(s/def :person/age (s/and int? #(< % 130) #(> % 0)))
(s/def :person/entity
  (s/keys :req [:db/id :person/last-name :person/e-mail]
          :opt [:person/first-name :person/age]))

(defn transact-update-person
  [conn person]
  (transact-upsert conn person :person/entity))

(defn nil-blanks
  "Convert empty string values in hmap to nil."
  [hmap]
  (into {}
        (map (fn [[k v]] (if (= "" v) [k nil] [k v]))
             hmap)))

(defn person-form->entity
  [{:keys [db/id
           person/first-name
           person/last-name
           person/e-mail
           person/age] :as data}]
  (-> data
      ;convert attrs to relevant types
      (assoc :db/id (let [i (js/parseInt id)]
                      (if (js/isNaN i) nil i)))
      (assoc :person/age (let [i (js/parseInt age)]
                           (if (js/isNaN i) nil i)))
      (nil-blanks))) ;IMPORTANT!

(defn action-update-person
  [state]
  (let [e (person-form->entity (:form state))
        r (try
            (db/transact-state-db
             state [[:db.fn/call transact-update-person e]])
            (catch ExceptionInfo e
              (.error js/console (str (.-message e) "\n" (.-data e)))
              (throw e)))] ;in real app: set error in state to show in UI
    (dissoc r :form))) ;clear form and set other stuff (screen, op status, ...)

(defn ui-form-input
  [store label key value]
  (html
    [:div
     [:label (str label ": ")]
     [:input {:value value
              :on-change #(dispatch! store action-change-form-input
                                     key
                                     (-> % (.-target) (.-value)))}]]))

(defn ui-form-person
  [store]
  (let [{:keys [person/first-name
                person/last-name
                person/e-mail
                person/age]} (fetch-form-data store)]
    (html
     [:div
      (ui-form-input store "First Name" :person/first-name first-name)
      (ui-form-input store "Last Name*" :person/last-name last-name)
      (ui-form-input store "E-mail*" :person/e-mail e-mail)
      (ui-form-input store "Age" :person/age age)
      [:div
       [:button
        {:on-click #(dispatch! store action-update-person)}
        "Update"]]])))

(defn ui-person-item
  [store {:keys [person/first-name
                 person/last-name
                 person/e-mail
                 person/age]}]
  (html
   [:li
    {:key e-mail}
    (str first-name " " last-name ", " e-mail ", " age)
    [:button
     {:on-click #(dispatch! store action-edit-person store e-mail)}
     "Edit"]]))

(defn ui-list-persons
  [store]
  (let [persons (fetch-persons store)]
    (html
     [:div "Persons:"
      [:ul
       (map (partial ui-person-item store) persons)]])))

(defn setup-test-store
  []
  (-> {}
      (atom)
      (db/install-card-db
        {:db/ident {:db.unique :db.unique/identity}
         :person/e-mail {:db.unique :db.unique/identity}}
        [#:person{:first-name "John" :last-name "Doe" :e-mail "jdoe@me.com" :age 40}
         #:person{:first-name "Joey" :last-name "Ramones" :e-mail "jram@me.com" :age 50}
         #:person{:first-name "Mick" :last-name "Jones" :e-mail "mjones@me.com" :age 60}])))

(defcard card-update-entity
  "Open th JS console to check for transaction errors."
  (fn [store]
    (html
     [:div
      (ui-form-person store)
      [:hr]
      (ui-list-persons store)]))
  (setup-test-store))
  ; {:inspect-data true})

(deftest test-update-entity

  (testing "Entity update: happy flow"
    (let [store (setup-test-store)]
      (dispatch! store action-edit-person store "jram@me.com")
      (dispatch! store action-change-form-input :person/first-name "Joe")
      (dispatch! store action-change-form-input :person/last-name "Ramonez")
      (dispatch! store action-change-form-input :person/e-mail "jram@eeleven.com")
      (dispatch! store action-change-form-input :person/age "100")
      (dispatch! store action-update-person)
      (let [result (d/pull (db/store-db store)
                           '[*] [:person/e-mail "jram@eeleven.com"])]
        (is (= result
               {:db/id 2
                :person/age 100
                :person/e-mail "jram@eeleven.com",
                :person/first-name "Joe",
                :person/last-name "Ramonez"})
            "All attributes should be updated."))))

  (testing "Entity update: conform to spec"
    (let [store (setup-test-store)]
      (dispatch! store action-edit-person store "jram@me.com")
      (dispatch! store action-change-form-input :person/first-name "Joe")
      (dispatch! store action-change-form-input :person/last-name "")
      (dispatch! store action-change-form-input :person/e-mail "")
      (dispatch! store action-change-form-input :person/age "")
      (is (thrown-with-msg? ExceptionInfo #"Invalid entity :person/entity"
            (dispatch! store action-update-person))
          "Non-conforming entity should be rejected.")))

  (testing "Entity update: blank non-key fields"
    (let [store (setup-test-store)]
      (dispatch! store action-edit-person store "jram@me.com")
      (dispatch! store action-change-form-input :person/first-name "")
      (dispatch! store action-change-form-input :person/last-name "Ramonez")
      (dispatch! store action-change-form-input :person/age "")
      (dispatch! store action-update-person)
      (let [result (d/pull (db/store-db store)
                           '[*] [:person/e-mail "jram@me.com"])]
        (is (= result
               {:db/id 2
                :person/e-mail "jram@me.com"
                :person/last-name "Ramonez"})
            "Blank non-key attributes should be retracted.")))))
