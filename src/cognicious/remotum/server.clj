(ns cognicious.remotum.server
  (:require [aleph.tcp :as tcp]
            [byte-streams :as bs]
            [clojure.core.async :as a]
            [clojure.tools.logging :as log]
            [cognicious.remotum.process :refer [init! entry-parser exec!]]
            [manifold.stream :as s]))

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

(defn entry-handler [handler-fn]
  (fn [request] 
    (if-let [response (entry-parser request handler-fn)]
      (str "remote> " (entry-parser request handler-fn) "\r\n")
      (str "remote> nil\r\n"))))

(defn start-server [server-opts apps]
  (log/info (pr-str {:start-server server-opts}))
  (tcp/start-server
   (fn [stream info]
     (log/debug {:stream stream})
     (log/debug {:info info})
     (init! apps)
     (wrap-duplex-stream stream (entry-handler exec!)))
   (clojure.set/rename-keys server-opts 
                            {:rmt/port :port :rmt/host :socket-address})))
