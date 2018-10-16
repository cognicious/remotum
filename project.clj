(defproject cognicious/remotum "0.1.0-SNAPSHOT"
  :description "Simple remote launcher"
  :url "https://github.com/cognicious/remotum"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.474"]                 
                 ;; communication
                 [aleph "0.4.6"]
                 ;; cli
                 [org.clojure/tools.cli "0.3.7"]
                 ;; logging
                 [org.apache.logging.log4j/log4j-core "2.11.0"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.11.0"]]
  :plugins [[lein-cloverage "1.0.11"]]
  :main cognicious.remotum.app
  :aot :all)
