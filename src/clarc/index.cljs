(ns clarc.index
  (:require
   clarc.intro.card1
   clarc.intro.card2
   clarc.intro.qa
   clarc.mini-app.index
   clarc.local-db.index
   clarc.socket.index
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(let [node (.getElementById js/document "main-app-area")]
  (when-not node
    (dc/start-devcard-ui!)))


(defcard
  "
## Minimum ClojureScript

Open these files in your editor and experiment at the REPL.

- `src/clarc/intro/syntax.cljs`
- `src/clarc/intro/functions.cljs`
- `src/clarc/intro/maps.cljs`
- `src/clarc/intro/mutable_state.cljs`

  ")

(defcard
  "
## Playing with Devcards

- [Reactive UI](/#!/clarc.intro.card1) code in `src/clarc/intro/card1.cljs`
- [Basic Flux](/#!/clarc.intro.card2): code in `src/clarc/intro/card2.cljs`
- [Minimal application](/#!/clarc.mini_app.index): code in `src/clarc/mini_app/`
To run the app outside devcards,
evaluate `(start-autobuild devcards dev)` in the REPL, and browse to
[/app/index.html](/app/index.html).
  ")

(defcard
  "
## Local DB

- [Setup](/#!/clarc.local_db.index): plug a DataScript database into the local store.
  ")

(defcard
  "
## Socket

- [Setup](/#!/clarc.socket.index): plug a socket into the local store to communicate with the server.
  ")
