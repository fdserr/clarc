(ns clarc.intro.qa
  (:require
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(defcard q&a
  "
## How to hack the app without devcards?

Evaluate `(start-autobuild devcards dev)` in the REPL.

[Open /index.html](/index.html), probably in a new tab (SHIFT-CLICK).

When in dev mode:

- Code changes are reflected on save.
- Application state is preserved on code reload.
  ")
