(ns clarc.intro.card2
  (:require
   [cljs.test :as test :include-macros true :refer [testing is]]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard
  "## Flux?")

;;;

; (defn dispatch!
;   [store fun & args]
;   (try
;     (apply swap! store fun args)
;     (catch js/Error e
;       (js/alert (.-message e))
;       (throw e)))
;   nil)

(defn dispatch!
  "Dispatch action to store"
  [store fun & args]
  (apply swap! store fun args)
  nil)

;;;

(defn action-init ; actions take state & args and return new state
  "Initial state"
  [state persons]
  (assoc state :persons persons))

(deftest test-init
  (testing "Store initialisation"
    (let [store (atom {})] ; store is just an atom
      (dispatch! store action-init ["John" "Pete"]) ; actions are just functions
      (is (= {:persons ["John" "Pete"]} @store)
          "store should be initialised after action-init"))))

;;;

(defn action-add-person
  "Person added"
  [state person]
  (if-not (or (= person "")
              (nil? person)
              (some #{person} (:persons state)))
    ; (update state :persons conj person)
    (-> state
        (update :persons conj person)
        (dissoc :input))
    (throw (ex-info "Invalid person" {:input person}))))

(deftest test-add-person
  (testing "Add a person"
    (let [store (atom {})]
      (dispatch! store action-init [])
      (dispatch! store action-add-person "François")
      (is (= "François" (-> @store :persons last))
          "person should be found in store after action-add-person")
      (is (thrown? js/Error
                   (dispatch! store action-add-person "François"))
          "person should be unique")
      (is (= ["François"] (-> @store :persons))
          "store should be intact after error"))))

;;;

(defn action-change-input
  "Input changed"
  [state input]
  (assoc state :input input))

(defn ui-person
  "person component"
  [person]
  (html [:li {:key person} person]))

(defn ui-form
  "Form component"
  [store]
  (html [:div
         [:input
          {:value (or (:input @store) "")
           :on-change #(dispatch! store action-change-input
                                  (-> % .-target .-value))}]
         [:button
          {:on-click #(dispatch! store action-add-person (:input @store))}
          "Submit"]
         [:p "  "]
         [:ul (map ui-person (:persons @store))]]))

(defcard ui-persons
  "Form to add a person"
  (fn [store]
    (ui-form store))
  {:persons ["John" "Jim"]})
