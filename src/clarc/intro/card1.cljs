(ns clarc.intro.card1
  (:require
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard
  "## Devcards basics")

;;;

(defcard
  minimal-card ; optional name
  "Some _text_"  ; optional text (markdown)
  {:a "map"})  ; value to display

;;;

(defcard ui-static
  "A simple React component"
  (html [:h3 "I am React!"]))

;;;

(defcard ui-state
  "A component that reads its props from a state atom."
  (fn [state] ; state is provided by the card
    (html [:h3 "I am Number " @state])) ; note deref
  (atom 1)) ; initial state

;;;

(defcard ui-reactive
  "A component reacting to state change (click text)."
  (fn [state] ; state is provided by the card
    (html [:h3
           {:on-click #(swap! state inc)}
           "I am Number " @state])) ; note deref
  (atom 1)) ; initial state

;;;

(declare ui-form) ; forward declaration

(defcard form-example
  "`ui-form` in action."
  (fn [state _]
    (ui-form state)) ; call the ui component
  {:input "React"}) ; a map as initial state
  ; card options
  ; {:inspect-data true
  ;  :history true})

(defn do-something
  [state]
  (js/alert @state))

(defn ui-form
  [state]
  (html [:div
         {:style
          {:margin "16 8 16 8"}}
         [:input
          {:value (or (:input @state) "")
           :on-change #(swap! state assoc :input (-> % .-target .-value))}]
         [:button
          {:on-click #(do-something state)}
          "Submit"]
         [:p "  "]]))

;;;

(declare screen)

(defcard steps-example
  "Application state preserved on code reload."
  (fn [state]
    (screen state))
  {:screen :step1}
  {:inspect-data true
   :history true})

(defn ui-step ; reusable step component
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

(defmulti screen
  (fn [state] (:screen @state))
  :default :step1)

(defmethod screen :step1
  [state]
  (html
    [:div
     [:h3 "Step 1"]
     (ui-step state "First Name :" :first-name :step2)]))

(defmethod screen :step2
  [state]
  (html
    [:div
     [:h3 "Step 2"]
     (ui-step state "Last Name :" :last-name :step3)]))

(defmethod screen :step3
  [state]
  (html
    [:div
     [:h3 "Step 3"]
     (ui-step state "E-mail :" :email :step4)]))

(defn action-send
  [state]
  (js/alert (str "Sending: " (dissoc @state :screen)))
  (reset! state {:screen :step1}))

(defmethod screen :step4
  [state]
  (let [{:keys [first-name last-name email]} @state]
    (html [:div "Thank you!"
           [:p (str "First Name: " first-name)]
           [:p (str "Last Name: " last-name)]
           [:p (str "E-mail: " email)]
           [:button
            {:on-click #(action-send state)}
            "Send!"]])))
