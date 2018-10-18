(ns cognicious.remotum.app
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as spec]
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

(defn shutdown-hook [app]
  (.addShutdownHook
   (Runtime/getRuntime)
   (Thread. #(log/info (pr-str {:stop app})))))

(defn get-config [path]
  (try 
    (read-string (slurp path))
    (catch Exception e
      (log/fatal (pr-str {:message (.getMessage e)})))))

(spec/def :rmt/port number?)
(spec/def :rmt/host string?)
(spec/def :rmt/apps map?)
(spec/def :rmt/server (spec/keys :req [:rmt/port] :opt [:rmt/host]))
(spec/def :rmt/config (spec/keys :req [:rmt/server :rmt/apps]))
(spec/fdef get-config 
           :args (spec/cat :path string?) 
           :ret :rmt/config)

(defn -main [& args]
  (let [app (name-version)
        _ (shutdown-hook app)
        config (.getCanonicalPath (clojure.java.io/file "./config.edn"))]
    (doall (map #(log/info %) (banner)))
    (log/info (pr-str {:start app}))
    (log/info (pr-str {:reading-config=file config}))
    (let [{:rmt/keys [server apps] :as cfg} (get-config config)]
      (if (spec/valid? :rmt/config cfg)
        (server/start-server server apps)
        (log/fatal (spec/explain-str :rmt/config cfg))))))
