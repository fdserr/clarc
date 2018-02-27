(ns clarc.index
  (:require
   [clarc.app :as app]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]])
  (:require-macros))

(defcard example
  "See `src/index.cljs`"
  (fn [a _]
    (app/ui-app a))
  {:app-title "Incrementer"
   :value 0})

(defcard q&a
  "
## How to hack the app without devcards?

Evaluate `(start-autobuild devcards dev)` in the REPL.

[Open /index.html](/index.html), probably in a new tab (SHIFT-CLICK).

When in dev mode:

- Code changes are reflected on save.
- Application state is preserved on code reload.
  ")
