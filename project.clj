(defproject jackknife "0.1.7"
  :description "Useful clojure utilities."
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.clojure/tools.macro "0.1.2"]
                 [org.clojure/math.combinatorics "0.0.4"]
                 [log4j/log4j "1.2.16"]]
  :plugins [[lein-midje "3.0.0"]]
  :profiles {:dev {:dependencies [[midje "1.5.0"]]}})
