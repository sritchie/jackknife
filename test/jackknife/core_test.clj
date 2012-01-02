(ns jackknife.core-test
  (:use jackknife.core
        midje.sweet))

(fact "update-vals test."
  (letfn [(square [k v] (* v v))]
    (update-vals {1 1, 2 2, 3 3} square) => {1 1, 2 4, 3 9}))
