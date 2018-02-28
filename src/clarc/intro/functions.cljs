(ns clarc.intro.functions)

;; evaluate each expression at the REPL

(defn f [x] (inc x))

(def g (fn [x] (inc x)))

(fn [x] (inc x))   ; anonymous fn (lambda)

#(inc %)   ; same as above

(f (g (#(inc %) 1)))
; or
((comp #(inc %) g f) 1)

(-> 1
    inc
    g
    f) ; same as above, thread first

(reduce + 4
  (filter even?
    (map inc [0 1 2 3 4]))) ; no need for loop/iterate
; or
(->> [0 1 2 3 4]
     (map inc)
     (filter even?)
     (reduce + 4)) ; thread last
