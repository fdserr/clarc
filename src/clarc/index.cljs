(ns clarc.index
  (:require
   [clarc.core :as clarc]
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]])
  (:require-macros))

(dc/start-devcard-ui!)

(defcard
  "
## How to run the app without devcards?

Evaluate `(start-autobuild devcards dev)` in the REPL.

[Open /index.html](/index.html), probably in a new tab (SHIFT-CLICK).

When in dev mode:

- Code changes are reflected on save.
- Application state is preserved on code reload.
  ")

(defcard app
  (fn [a _]
    (clarc/ui-app a))
  {:app-title "Incrementer"
   :value 0})
