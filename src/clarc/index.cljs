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

  ")

(defcard
  "
## Local DB

- [DataScript in-memory database](/#!/clarc.local_db.index)
- Example app [TODO]
  ")

(defcard
  "
## Socket

- [WebSocket server comm](/#!/clarc.socket.index)
- Example app [TODO]
  ")

(defcard
  "
## Reference Links

- [Leiningen](https://leiningen.org)
- [Parinfer](https://shaunlebron.github.io/parinfer/)
- [ClojureScript cheat sheet](http://cljs.info/cheatsheet/)
- [Devcards](https://github.com/bhauman/devcards)
- [DataScript](https://github.com/tonsky/datascript)
- [Onyx Platform](http://www.onyxplatform.org)
- [Applicative State Transition systems](http://wwwusers.di.uniroma1.it/~lpara/LETTURE/backus.pdf)
(John Backus 1977 Turing Award Lecture)
  ")
