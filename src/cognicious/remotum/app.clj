(ns cognicious.remotum.app
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.tools.logging :as log]
            [cognicious.remotum.server :as server]))

(def meta-project "META-INF/leiningen/cognicious/remotum/project.clj")

(defn project-clj 
  "Returns project.clj into the JAR, otherwise, return local file"
  [meta]
  (if-let [project (io/resource meta)]
    project
    (do
      (log/warn (pr-str {:not-found meta :trying-with "project.clj"}))
      "project.clj")))

(defn name-version
  "Returns name and version reading project.clj"
  []
  (let [[_ name version] (-> (project-clj meta-project) slurp read-string vec)]
    {:name name :version version}))

(defn banner
  "Fancy app banner"
  []
  (s/split-lines (slurp (io/resource "banner.txt"))))

(defn -main [& args]
  (let [app (name-version)]
    (doall (map #(log/info %) (banner)))
    (log/info (pr-str {:start app}))
    (server/start-server 8080)
    (.addShutdownHook
     (Runtime/getRuntime)
     (Thread. #(log/info (pr-str {:stop app}))))))
