(ns clarc.intro.syntax)

;; evaluate each expression at the REPL

(+ 1 2 3 (- 9 5))  ; this is a comment

(first (rest [1 "2" 3 [4 true]]))   ; [1 2 3 …] = vector literal (~ array)

(def x 1)   ; bind name to value

(inc x)   ; inc = increment

x   ; guess?

(let [x 1] (inc x))   ; local bindings (lexically scoped)

(if nil true false)  ; only false and nil are falsy, rest is truthy

(when false true); use (when …) if no res-false

(let [[a b & r] [1 2 3 4]         ; vector destructuring
      {:keys [c d]} {:c 5 :d 2}]  ; map destructuring
  (+ a b c d))                    ; guess r ?

(.log js/console (.-length "abc"))   ; direct interop (JS, Java, C#)
