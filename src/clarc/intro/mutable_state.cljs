(ns clarc.intro.mutable-state)

;; evaluate each expression at the REPL

(def a (atom 1))

a   ; ?

(deref a)   ; or just @a  -> open the box to get the value

(swap! a inc)   ; swap the content of the box with the result of
                ; applying inc to what’s inside

@a  ; ?

(add-watch a :my-watch
           (fn [k a o n] (print n)))   ; log new value on change
