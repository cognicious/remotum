(ns cognicious.remotum.server-test
  (:require [byte-streams :as bs]
            [clojure.test :refer [deftest testing is]]
            [cognicious.remotum.server :refer [wrap-duplex-stream entry-handler]]
            [manifold.stream :as s]))

(deftest wrap-duplex-stream-test
  (testing "Testing wrap with a simple validation function"
    (let [stream (s/stream)
          value-atm (atom nil)
          validate-length-fn (fn [input] 
                               (reset! value-atm (str (> (count input) 10)))
                               @value-atm)
          wrapper (wrap-duplex-stream stream validate-length-fn)]      
      @(s/put! stream "1234567890")
      (println (s/description stream))
      (is (= "false" @value-atm))

      @(s/put! stream "12345678901")
      (println (s/description stream))
      (is (= "true" @value-atm)))))

(deftest entry-handler-
  (testing "entry-handler output"
    (let [entry-handler-fn (entry-handler str)]
      (is (= "remote> xxx\r\n" (entry-handler-fn "xxx\n")))
      (is (= "remote> nil\r\n" (entry-handler-fn "\n"))))))
