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
  [e]
  (into {} (remove (fn [[k v]] (nil? v))) e))

; (s/def :db/id int?)
(s/def :person/first-name string?)
(s/def :person/last-name string?)
(s/def :person/e-mail string?)
(s/def :person/age int?)
(s/def :person/entity
  (s/keys :req [:db/id :person/last-name :person/e-mail]
          :opt [:person/first-name :person/age]))

(defn transact-update-person
  [conn person]
  (let [new (prune-entity person)
        new (if-not (s/valid? :person/entity new)
              (throw (ex-info "Invalid entity: Person."
                              (s/explain-str :person/entity new)))
              new)
        old (d/touch (d/entity conn (:db/id person)))
        rm-keys (difference (set (keys person)) (set (keys new)))
        rm-keyvals (map (fn [k] [k (k old)]) rm-keys)
        retract-datoms (map (fn [[attr val]]
                              [:db/retract (:db/id person) attr val])
                            rm-keyvals)]
    (into [new] retract-datoms)))

(defn nil-blanks
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
      (assoc :db/id (let [i (js/parseInt id)]
                      (if (js/isNaN i) nil i)))
      (assoc :person/age (let [i (js/parseInt age)]
                           (if (js/isNaN i) nil i)))
      (nil-blanks)))

(defn action-update-person
  [state]
  (let [e (person-form->entity (:form state))
        r (try
            (db/transact-state-db
             state [[:db.fn/call transact-update-person e]])
            (catch ExceptionInfo e
              (.error js/console (str (.-message e) "\n" (.-data e)))
              (throw e)))]
    (dissoc r :form)))

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

(defcard update-entity
  "### Updating an existing entity"
  (fn [store]
    (html
     [:div
      (ui-form-person store)
      [:hr]
      (ui-list-persons store)]))
  (setup-test-store)
  {:inspect-data true})

(deftest test-update-entity

  (testing "Entity update: happy flow"
    (let [store (setup-test-store)]
      (dispatch! store action-edit-person store "jram@me.com")
      (dispatch! store action-change-form-input :person/first-name "Joe")
      (dispatch! store action-change-form-input :person/last-name "Ramonez")
      (dispatch! store action-change-form-input :person/age "70")
      (dispatch! store action-update-person)
      (let [res (d/pull (db/store-db store)
                        '[*] [:person/e-mail "jram@me.com"])]
        (is (= {:person/age 70
                :person/e-mail "jram@me.com"
                :person/first-name "Joe"
                :person/last-name "Ramonez"}
               (dissoc res :db/id))
            "Non-key attributes should be updated."))))

  (testing "Entity update: conform to spec"
    (let [store (setup-test-store)]
      (dispatch! store action-edit-person store "jram@me.com")
      (dispatch! store action-change-form-input :person/first-name "Joe")
      (dispatch! store action-change-form-input :person/last-name "")
      (dispatch! store action-change-form-input :person/e-mail "")
      (dispatch! store action-change-form-input :person/age "")
      (is (thrown-with-msg? ExceptionInfo #"Invalid entity: Person."
            (dispatch! store action-update-person))
          "Non-conforming entity should be rejected.")))

  (testing "Entity update: blank non-key fields"
    (let [store (setup-test-store)]
      (dispatch! store action-edit-person store "jram@me.com")
      (dispatch! store action-change-form-input :person/first-name "")
      (dispatch! store action-change-form-input :person/last-name "Ramonez")
      (dispatch! store action-change-form-input :person/age "")
      (dispatch! store action-update-person)
      (let [res (d/pull (db/store-db store)
                        '[*] [:person/e-mail "jram@me.com"])]
        (is (= {:person/e-mail "jram@me.com"
                :person/last-name "Ramonez"}
               (dissoc res :db/id))
            "Blank non-key attributes should be retracted.")))))
