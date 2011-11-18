(ns findfn.core
  (:use [clojail.testers :only [secure-tester]])
  (:require [clojail.core :as sb]
            [clojure set string])
  (:import clojure.lang.LispReader$ReaderException))

(def ^{:dynamic true
       :doc "A set of namespaces where find-fn will look."}
  *ns-set*
  (map the-ns '#{clojure.core clojure.set clojure.string}))

(defn- fn-name [var]
  (apply symbol
         (map str
              ((juxt (comp ns-name :ns)
                     :name)
               (meta var)))))

(defn- filter-vars [testfn]
  (for [f (remove (comp secure-tester :name meta)
                  (mapcat (comp vals ns-publics) *ns-set*))
        :when (try
               (sb/thunk-timeout
                #(binding [*out* (java.io.StringWriter.)]
                   (testfn f))
                50 :ms sb/eagerly-consume)
               (catch Throwable _ nil))]
    (fn-name f)))

(defn find-fn
  "Takes expected output and expected input to produce that output and
   runs every single function and macro against the input, collecting the
   names of the ones that match the output."
  [out & in]
  (filter-vars
   (fn [f]
     (= out
        (apply
         (if (-> f meta :macro)
           (fn [& args]
             (eval `(~f ~@args)))
           f)
         in)))))

(defn find-arg
  "Basically find-fn for finding functions to pass to higher order functions. out is
   the expected output to look for and in is a function with arguments where one of the
   arguments (supposedly a function) is replaced with %. find-arg will execute the function
   with those arguments, replacing the % argument with a different function each time collecting
   the functions that produce the correct output."
  [out & in]
  (filter-vars
   (fn [f]
     (when-not (-> f meta :macro)
       (= out
          (eval `(let [~'% ~f]
                   (~@in))))))))

(defn read-arg-string
  "From an input string like \"in1 in2 in3 out\", return a vector of [out
  in1 in2 in3], for use in findfn."
  [argstr]
  (apply concat
         ((juxt (comp list last) butlast)
          (with-in-str argstr
            (let [sentinel (Object.)]
              (doall
               (take-while (complement #{sentinel})
                           (repeatedly
                            #(try
                               (sb/safe-read)
                               (catch LispReader$ReaderException _
                                 sentinel))))))))))
