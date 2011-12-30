(ns jackknife.seq-test
  (:use midje.sweet))

(tabular
 (fact "remove-first testing."
   (remove-first ?pred ?coll) => ?result)
 ?pred         ?coll        ?result
 even?         [1 3 5]       (throws AssertionError)
 keyword?      [1 :b 2 :a]   [1 2 :a]
 (partial = 1) [1 1 2 3 4 5] [1 2 3 4 5]
 (partial = 4) [1 1 2 3 4 5] [1 1 2 3 5])

(tabular
 (fact "Wipe index from the supplied collection."
   (wipe ?idx ?coll) => ?res)
 ?idx ?coll    ?res
 1    [1 2 3 4] [1 3 4]
 0    [1 2 3 4] [2 3 4]
 6    [1 2 3 4] [1 2 3 4])

(tabular
 (fact "Unweave testing."
   (unweave ?coll) => ?result)
 ?coll ?result
 []               [[] []]
 [1 2 3 4 5 6]    [[1 3 5] [2 4 6]]
 ["a" 99 "q" "c"] [["a" "q"] [99 "c"]]
 ["a" "b" "c"] (throws IllegalArgumentException)
 [100]         (throws IllegalArgumentException))



