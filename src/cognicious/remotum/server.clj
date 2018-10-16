(ns cognicious.remotum.server
  (:require [aleph.tcp :as tcp]
            [byte-streams :as bs]
            [clojure.core.async :as a]
            [manifold.stream :as s]
            [clojure.tools.logging :as log]))

(defn wrap-duplex-stream
  [raw-stream processor-fn]
  (let [out (s/stream)]
    (s/connect out raw-stream)
    (a/go-loop []
      (if-let [input @(s/take! raw-stream)]
        @(s/put! out (-> input 
                         bs/to-string
                         processor-fn
                         bs/to-byte-array)))
      (recur))))

(defn start-server [port]
  (tcp/start-server
   (fn [stream info]
     (log/debug {:stream stream})
     (log/debug {:info info})
     (wrap-duplex-stream stream (fn [request] (log/info {:req request}) (str "REC: " request))))
   {:port port}))
