(ns clarc.intro.card1
  (:require
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard
  "
[ [Home](/#!/clarc.index)
 | Reactive UI
 | [Basic Flux](/#!/clarc.intro.card2)
 | [Mini App](/#!/clarc.mini_app.index) ]
## Reactive?
Code: `src/clarc/intro/card1.cljs`
")

;;; basic cards

(defcard
  minimal-card   ; optional name
  "Some _text_"  ; optional text (markdown)
  {:a "mapping"})    ; value to display

(defcard ui-static
  "A simple React component"
  (html [:h3 "cljs <3 React!"]))

(defcard ui-state
  "A component that reads its props from a state atom."
  (fn [state] ; state is provided by the card
    (html [:h3 "Number " @state " FB OSS project."])) ; note deref
  (atom 1)) ; initial state

(defcard ui-reactive
  "A component reacting to state change (click text)."
  (fn [state] ; state is provided by the card
    (html [:h3
           {:style {:user-select "none"}
            :on-click #(swap! state inc)}
           "Likes: " @state])) ; note deref
  (atom 1)) ; initial state

;;; form example

(declare ui-form) ; forward declaration

(defcard form-example
  "`ui-form` in action."
  (fn [state _]
    (ui-form state)) ; call the ui component
  {:input "React"} ; a map as initial state
  ; optional card options (uncomment)
  {:inspect-data true
   :history true})

(defn do-something
  [state]
  (js/alert @state))

(defn ui-form
  [state]
  (html [:div
         {:style
          {:margin "8px"}}
         [:input
          {:value (or (:value @state) "")
           :on-change #(swap! state assoc :value (-> % .-target .-value))}]
         [:button
          {:on-click #(do-something state)}
          "Submit"]]))

;;; multiple steps form

(declare ui-screen)

(defcard steps-example
  "Application state is preserved on code reload.
   Uses a `multimethod` to switch screens (Clojure's [polymorphic function](https://clojure.org/about/runtime_polymorphism))."
  (fn [state]
    (ui-screen state))
  {:screen :step1}
  {:inspect-data true
   :history true})

(defn ui-step
  "Reusable form for 3 first steps"
  [state label key next-screen]
  (html [:div
         [:label label]
         [:input
          {:value (or (key @state) "")
           :on-change #(swap! state assoc key
                              (-> % .-target .-value))}]
         [:br]
         [:button
          {:on-click #(swap! state assoc :screen next-screen)}
          "Next..."]
         [:p "  "]]))

(defmulti ui-screen
  "Show screen according to current step in state"
  (fn [state] (:screen @state))
  :default :step1)

(defmethod ui-screen :step1
  [state]
  (html
    [:div
     [:h3 "Step 1"]
     (ui-step state "First Name :" :first-name :step2)]))

(defmethod ui-screen :step2
  [state]
  (html
    [:div
     [:h3 "Step 2"]
     (ui-step state "Last Name :" :last-name :step3)]))

(defmethod ui-screen :step3
  [state]
  (html
    [:div
     [:h3 "Step 3"]
     (ui-step state "E-mail :" :email :step4)]))

(defn action-send
  [state]
  (js/alert (str "Sending: " (dissoc @state :screen)))
  (reset! state {:screen :step1}))

(defmethod ui-screen :step4
  [state]
  (let [{:keys [first-name last-name email]} @state]
    (html [:div
           [:h2 "Thank you!"]
           [:p (str "First Name: " first-name)]
           [:p (str "Last Name: " last-name)]
           [:p (str "E-mail: " email)]
           [:button
            {:on-click #(action-send state)}
            "Send!"]])))
