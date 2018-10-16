(ns cognicious.remotum.process-test
  (:require [clojure.test :refer [deftest testing is]]
            [cognicious.remotum.process :refer [processes
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
