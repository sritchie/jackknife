(ns jackknife.seq-test
  (:refer-clojure :exclude [flatten some?])
  (:use jackknife.seq
        midje.sweet))

(fact "transpose test."
  (transpose [[1 3] [2 4]]) => [[1 2] [3 4]])

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
 (fact "collectify test."
   (collectify ?coll) => ?result)
 ?coll       ?result
 '(1 2 3)    '(1 2 3)
 [5 5]       [5 5]
 "aaa"       ["aaa"]
 {:a 1 :b 2} [{:a 1 :b 2}]
 #{1 2 3}    [#{1 2 3}])

(tabular
 (fact "Unweave testing."
   (unweave ?coll) => ?result)
 ?coll ?result
 []               [[] []]
 [1 2 3 4 5 6]    [[1 3 5] [2 4 6]]
 ["a" 99 "q" "c"] [["a" "q"] [99 "c"]]
 ["a" "b" "c"] [["a" "c"] ["b"]]
 [100]         [[100] []])

(tabular
 (fact "duplicates test."
   (duplicates ?coll) => ?result)
 ?coll               ?result
 [1 2 2 1 3]         [1 2]
 (range 4)           []
 [1 "face" 2 "face"] ["face"])

(tabular
 (fact "test-all-pairs"
   (all-pairs ?input) => ?result)
 ?input      ?result
 [1]         []
 [1 2 3]     [[1 2] [1 3] [2 3]]
 [1 :a :a 2] [[1 :a] [1 :a] [1 2] [:a :a] [:a 2] [:a 2]])

(facts "count= tests."
  (count= [1] []) => false

  (count= [1] [1] [3]) => true
  (count= [1 2] [4 3]) => true

  (not-count= [1] []) => true
  (not-count= [1 2] [3 4] []) => true
  (not-count= [1] [1]) => false
  (not-count= [1 2] [4 3]) => false)
