(ns findfn.core-test
  (:use clojure.test
        findfn.core))

(deftest find-fn-test
  (testing "that it works."
    (is (= (find-fn 1) '[clojure.core/* clojure.core/*']))
    (is (= (find-fn 6 3 3) '[clojure.core/+ clojure.core/unchecked-add
                             clojure.core/+' clojure.core/unchecked-add-int]))))

(deftest find-arg-test
  (testing "that it works"
    (is (= (find-arg [2 3 4] map '% [1 2 3])
           '[clojure.core/unchecked-inc-int clojure.core/unchecked-inc
             clojure.core/inc clojure.core/inc']))))