(ns cognicious.remotum.process-test
  (:require [clojure.test :refer [deftest testing is]]
            [cognicious.remotum.process :refer [dispatcher
                                                entry-parser
                                                processes
                                                init!
                                                exec!]]))

(def java-version (-> #"([0-9]{1,2})\.([0-9]+)\.([0-9_]+)" 
                      (re-matches (System/getProperty "java.version"))
                      (second)
                      (Integer/parseInt)))

(deftest exec!-happy-path
  (testing "exec! uninitialized"
    (reset! processes {})
    (exec! "" "")
    (is (empty? @processes)))
  (testing "exec! initialized"
    (init! {"top" {:path "top"}})
    (is (not (empty? @processes)))
    (exec! "top" "start")
    (is (not (empty? @processes)))
    (is (not (nil? (:process (get @processes "top")))))
    (when (> java-version 1)
      (is (.isAlive (:process (get @processes "top")))))
    (exec! "top" "stop")
    (is (not (empty? @processes)))
    (when (> java-version 1)
      (is (not (.isAlive (:process (get @processes "top"))))))))

(deftest exec!-wrong-path
  (testing "exec! uninitialized"
    (reset! processes {})
    (exec! "" "")
    (is (empty? @processes)))
  (testing "exec! initialized"
    (init! {"top" {:path "top"}})
    (is (not (empty? @processes)))
    (exec! "oko" "start")
    (is (not (empty? @processes)))
    (is (nil? (:process (get @processes "top"))))
    (exec! "oko" "stop")
    (is (not (empty? @processes)))
    (is (nil? (:process (get @processes "top"))))))

(deftest dispatcher-
  (testing "dispatcher cases"
    (reset! processes {})
    (exec! "" "")
    (is (empty? @processes)))
  (testing "exec! initialized"
    (let [app-cfg {:path ["top" ""]}]
      (is (not (nil? (dispatcher "top" "start" app-cfg)))))
    (let [app-cfg {:path 1000}]
      (is (nil? (dispatcher "top" "start" app-cfg))))))

(deftest entry-parser-
  (testing "entry parser cases"
    (let [handler-fn (fn [x y] (pr-str [x y]))]
      (is (= (pr-str ["foo" "start"])  (entry-parser "foo start\r\n" handler-fn)))
      (is (= (pr-str ["foo" "start"])  (entry-parser "foo\tstart" handler-fn)))
      (is (= (pr-str ["foostart" ""])  (entry-parser "foostart" handler-fn)))
      (is (= nil  (entry-parser "\n" handler-fn))))))
