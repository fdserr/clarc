(defproject clarc "0.1.1-SNAPSHOT"
;  :description "FIXME: write this!"
  :url "https://github.com/fdserr/clarc"
  :license {:name "Creative Commons CC0"
            :url "https://creativecommons.org/publicdomain/zero/1.0/"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [devcards "0.2.3"]
                 [datascript "0.16.3"]
                 [sablono "0.7.4"]
                 [cljsjs/react "15.3.1-0"]
                 [cljsjs/react-dom "15.3.1-0"]
                 ;---server
                 [ring "1.6.3"]
                 [ring-cors "0.1.11"]
                 [compojure "1.6.0"]
                 [com.taoensso/sente "1.12.0"]
                 [http-kit "2.2.0"]
                 ; [jetty/javax.servlet "5.1.12"]
                 [danlentz/clj-uuid "0.1.6"]]

  :plugins [[lein-figwheel "0.5.14"]
            [lein-cljsbuild "1.1.5" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]

  :main clarc.server.core

  :cljsbuild {:builds [{:id "devcards"
                        ;; Development using figwheel + devcards
                        :source-paths ["dev" "src"]
                        :figwheel {:devcards true}
                        :compiler { :main       "clarc.dev"
                                    :asset-path "js/compiled/devcards_out"
                                    :output-to  "resources/public/js/compiled/clarc_cards.js"
                                    :output-dir "resources/public/js/compiled/devcards_out"
                                    :source-map-timestamp true}}
                       {:id "cards"
                        ;; Devcards prod build (no figwheel)
                        :source-paths ["src"]
                        :compiler {:main       "clarc.index"
                                   :devcards   true
                                   :asset-path "js/compiled/cards_out"
                                   :output-to  "resources/public/js/compiled/clarc_cards.js"
                                   :output-dir "resources/public/js/compiled/cards_out"
                                   :optimizations :advanced}}
                       {:id "dev"
                        ;; Development using figwheel (no cards)
                        :source-paths ["dev" "src"]
                        :figwheel {:on-jsload "clarc.dev/js-loaded"}
                        :compiler {:main       "clarc.dev"
                                   :asset-path "../js/compiled/dev_out"
                                   :output-to  "resources/public/js/compiled/clarc.js"
                                   :output-dir "resources/public/js/compiled/dev_out"
                                   :source-map-timestamp true}}
                       {:id "prod"
                        ;; Prod build ;;FIXME paths
                        :source-paths ["src"]
                        :compiler {:main       "clarc.mini-app.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/clarc.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :optimizations :advanced}}]}

  :figwheel {:css-dirs ["resources/public/css"]
             :server-port 3450
             :ring-handler clarc.server.core/server-reload}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.2"]
                                  [figwheel-sidecar "0.5.14"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   :source-paths ["src" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
