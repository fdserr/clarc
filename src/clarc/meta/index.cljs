(ns clarc.meta.index
  (:require
    [cljs.test :refer [is]]
    [devcards.core :as dc :include-macros true :refer [defcard deftest]]
    [sablono.core :as sab :include-macros true :refer [html]]))

(defcard "# Metacircular Interpreter")

(defcard "## eval")

(defn eval
  [expr]
  (if (list? expr)
    (apply (first expr) (rest expr))
    expr))

(deftest basic_arithmetics
  (is (= 3 (eval '(+ 1 2)))))
