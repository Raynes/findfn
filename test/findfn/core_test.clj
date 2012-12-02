(ns findfn.core-test
  (:use clojure.test
        [clojail.testers :only [secure-tester]]
        findfn.core))

(deftest find-fn-for-pred-test
  (testing "that it works"
    (is (= (find-fn-for-pred secure-tester #(= (count %) 6) "lol" "omg")
           '[clojure.core/lazy-cat clojure.core/concat
             clojure.core/interleave clojure.core/str]))))

(deftest find-fn-test
  (testing "that it works."
    (is (= (find-fn secure-tester "lolomg" "lol" "omg") '[clojure.core/str]))))

(deftest find-arg-test
  (testing "that it works"
    (is (= (find-arg secure-tester ["a" "b" "c"] 'map '% [\a \b \c])
           '[clojure.string/trim clojure.string/lower-case
             clojure.core/munge clojure.core/print-str clojure.core/str
             clojure.core/namespace-munge]))))