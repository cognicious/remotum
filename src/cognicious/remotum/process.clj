(ns cognicious.remotum.process
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]))

(def processes (atom {}))

(defn launcher 
  "
  Launch an application using its path. Application arguments must be passed as
  extra arguments. By instance:
  ```
  # launches emacs 
  (launch \"/usr/bin/emacs\") 
  # launches emacs with argumentas
  (launch \"/usr/bin/emacs\" \"--no-splash\")
  ```
  "
  [& args]
  (log/debug (pr-str {:laucher args}))
  (let [process (-> (ProcessBuilder. args)
                    (.start))]
    {:out (-> process
              (.getInputStream)
              (io/reader))
     :err (-> process
              (.getErrorStream)
              (io/reader))
     :in (-> process
             (.getOutputStream)
             (io/writer))
     :process process}))

(defn init!
  "
  Initializes `processes` atom.
  `processes` borns with app configuration. When an application is launched,
  its original configuration are merged with :out :err :in :process.
  "
  [apps]
  (log/debug (pr-str {:init! apps}))
  (reset! processes apps))

(defn dispatcher 
  "
  Dispatch a new program.
  If action is `start` launches the application. When an application has already
  started, it destroys it and starts it again.
  "
  [app-alias action app-cfg]
  (log/debug (pr-str {:dispatcher [app-alias action app-cfg]}))
  (let [{:keys [path process]} app-cfg]
    (log/info (pr-str {:app-cfg [path process]}))
    (when (and process (.isAlive process))
      (log/info (pr-str {:message (format "Destroying already started App alias '%s'" app-alias)}))
      (.destroy process))
    (if (= action "start")
      (cond (string? path) (launcher path)
            (coll? path) (apply launcher path)
            :else (log/warn (pr-str {:message (format ":path for App alias '%s' is invalid (not string, not coll), check your config EDN" app-alias)}))))))

(defn exec!
  "Take an app-alias and try to dispatch its program"
  [app-alias action]
  (log/debug (pr-str {:exec! [app-alias action]}))
  (if (not (empty? @processes))
    (if-let [app-cfg (get @processes app-alias)]
      (swap! processes update app-alias merge (dispatcher app-alias action app-cfg))
      (log/warn (pr-str {:message (format "App alias '%s' not initialized, check your config EDN" app-alias)})))
    (log/error (pr-str {:message "Apps not initialized, check your config EDN"}))))
