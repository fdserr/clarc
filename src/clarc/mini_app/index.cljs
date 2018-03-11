(ns clarc.mini-app.index
  (:require
   [clarc.mini-app.app :as app]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard
  "
[ [Home](/#!/clarc.index)
 | [Reactive UI]((/#!/clarc.intro.card1))
 | [Basic Flux](/#!/clarc.intro.card2)
 | Mini App ]
## Minimal App
Code: `src/clarc/mini_app/*`

To run and tweak the app outside devcards,
evaluate `(start-autobuild devcards dev)` at the REPL, and browse to
[/mini-app/index.html](/mini-app/index.html).")

(defcard mini-app
  "See devcard in `src/clarc/mini_app/index.cljs`"
  (fn [store]
    (app/ui-app store))
  {:app-title "Incrementer"
   :value 0}
  {:inspect-data true
   :history true})

;;;
