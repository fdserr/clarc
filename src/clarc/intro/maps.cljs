(ns clarc.intro.maps)

;; evaluate each expression at the REPL

{:k1 1
 :k2 "2"
 :k3 [0 1 2]
 :k4 {:a 1 :b 2}}

(assoc {:x "XYZ" :y true} :k 2)

(dissoc {:k 2} :k)

(update {:k 2} :k inc)

(get {:k 2} :z :not-found)

(:k {:k 2})   ; ({:k 2} :k) works too, prefer key first for readability

(get-in {:k [0 {:x "X"}]} [:k 1 :x]) ; also assoc-in and update-in
