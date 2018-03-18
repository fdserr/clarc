(ns clarc.index
  (:require
   clarc.intro.card1
   clarc.intro.card2
   clarc.intro.qa
   clarc.mini-app.index
   clarc.local-db.index
   clarc.socket.index
   clarc.server.index
   clarc.e2e.index
   [devcards.core :as dc :include-macros true :refer [defcard deftest]]
   [sablono.core :as sab :include-macros true :refer [html]]))

(let [node (.getElementById js/document "main-app-area")]
  (when-not node
    (dc/start-devcard-ui!)))

(defcard
  "
> \"There is a lot to learn when you are first learning ClojureScript, I recommend that you bite off very small pieces at first. Smaller bites than you would take when learning other languages like JavaScript and Ruby.
>
> Please don't invest too much time trying to set up a sweet development environment, there is a diverse set of tools that is constantly in flux and it's very difficult to suss out which ones will actually help you. If you spend a lot of time evaluating all these options it can become very frustrating. If you wait a while, and use simple tools you will have much more fun actually using the language itself.\"

-- <cite>Bruce Hauman, [Figwheel](https://github.com/bhauman/lein-figwheel#learning-clojurescript)</cite>
  ")

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

- [Reactive UI](#!/clarc.intro.card1) code in `src/clarc/intro/card1.cljs`
- [Basic Flux](#!/clarc.intro.card2): code in `src/clarc/intro/card2.cljs`
- [Minimal application](#!/clarc.mini_app.index): code in `src/clarc/mini_app/`

  ")

(defcard
  "
## Local DB

- [DataScript in-memory database](#!/clarc.local_db.index)
- Example app [TODO]
  ")

(defcard
  "
## Socket

- [WebSocket server comm](#!/clarc.socket.index)
- Example app [TODO]
  ")

(defcard
  "
## Server

- [Web Server](#!/clarc.server.index)
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
