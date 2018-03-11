(ns clarc.intro.card2
  (:require
   [cljs.test :as test :include-macros true :refer [testing is]]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard
  "
[ [Home](/#!/clarc.index)
 | [Reactive UI](/#!/clarc.intro.card1)
 | Basic Flux
 | [Mini App](/#!/clarc.mini_app.index) ]
## Flux?
Code: `src/clarc/intro/card1.cljs`

Flux is an architecture pattern.
It defines three components: `UI`, `State`, `Dispatch`,
and a model for their interaction: `State -> UI -> Dispatch -> State -> UI -> ...`

- `UI` is a pure function of `State` (never change `State` directly in the `UI`).
- `UI` always requests changes to `State` via `Dispatch`.
- `Dispatch` changes `State`, and may trigger `side effects` (call server, launch missile, ...)
- `State` holds all of the mutable values of the system.
- The legend doesn't say how `UI` is rendered on `State` change, but a common
practice is to make `State` `observable` (or `reactive`) and fire a `render`
function whenever it changes.

### Implementation:

- State is a `cljs.core/atom`, to which we add a `watcher` function to
`render` the `UI` on change. __NOTE__: You don't need to manually attach a watcher to the
`store` of a card, Devcards takes care of this.
To avoid confusion, we will call the `atom` itself: `store`, and its
current value: `state`. To obtain `state` from `store`, `(deref store)` (or just `@store`).

- `Dispatch` is a function: `dispatch!`. We pass it the `store`, an `action` and
any number of arguments. An `action` is an arbitrary function that takes a
`state` (a value, not an `atom`), any number of arguments, and returns a new `state`.
Notice that if anything goes wrong during the execution of `dispatch!`, the `store`
is left intact. That's why it's not a good idea to chain actions, each user action
should trigger _one and only one_ `dispatch!`. To re-use small changes to the `store`,
_compose_ smaller functions into one, single `action`.

- `UI` is produced by arbitrary functions that return a `ReactJS` component.
Using `sablono/html` over `hiccup` data structures is one way to achieve this ([doc here](https://github.com/r0man/sablono)).
Here we use stateless functions that take a
`store` and fetch the data they need from it (without ever changing it!).
Other models will be studied later, but we can go a long way with keeping
things simple. UI `events` (clicks, input changes, etc.) are associated to an
anonymous function, which will always call `dispatch!`. __All changes happen
via dispatching actions to the store, do not read or manipulate the DOM directly__.
You've been warned.
  ")

;;;

(defcard "### A minimalistic flux app")

(defn dispatch!
  "Dispatch action to store"
  [store action & args]
  (apply swap! store action args)
  nil)

;;; simple centralised error management:
; (defn dispatch!
;   [store fun & args]
;   (try
;     (apply swap! store fun args)
;     (catch js/Error e
;       (js/alert (.-message e))
;       (throw e)))
;   nil)

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
  "Form to add a person. Check JS console for errors."
  (fn [store]
    (ui-form store))
  {:persons ["John" "Jim"]})
