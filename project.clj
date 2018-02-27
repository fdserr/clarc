(defproject clarc "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [devcards "0.2.3"]
                 [sablono "0.7.4"]
                 [cljsjs/react "15.3.1-0"]
                 [cljsjs/react-dom "15.3.1-0"]]

  :plugins [[lein-figwheel "0.5.14"]
            [lein-cljsbuild "1.1.5" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]

  :cljsbuild {:builds [{:id "devcards"
                        ;; Development using figwheel + devcards
                        :source-paths ["src"]
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
                        :source-paths ["src"]
                        :figwheel {:on-jsload "clarc.dev/js-loaded"}
                        :compiler {:main       "clarc.dev"
                                   :asset-path "../js/compiled/dev_out"
                                   :output-to  "resources/public/js/compiled/clarc.js"
                                   :output-dir "resources/public/js/compiled/dev_out"
                                   :source-map-timestamp true}}
                       {:id "prod"
                        ;; Prod build ;;FIXME paths
                        :source-paths ["src"]
                        :compiler {:main       "clarc.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/clarc.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :optimizations :advanced}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.2"]
                                  [figwheel-sidecar "0.5.14"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   :source-paths ["src" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
