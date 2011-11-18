(ns findfn.core-test
  (:use clojure.test
        findfn.core))

(deftest find-fn-test
  (testing "that it works."
    (is (= (find-fn "lolomg" "lol" "omg") '[clojure.core/str]))))

(deftest find-arg-test
  (testing "that it works"
    (is (= (find-arg ["a" "b" "c"] map '% [\a \b \c])
           '[clojure.string/trim clojure.string/lower-case
             clojure.core/munge clojure.core/print-str clojure.core/str
             clojure.core/namespace-munge]))))