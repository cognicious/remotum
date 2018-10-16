(ns cognicious.remotum.app-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]
            [cognicious.remotum.app :refer [project-clj
                                            name-version
                                            banner]]))

(deftest project-clj-test
  (testing "Getting project.clj path"
    (is (.exists (io/file (project-clj "./project.clj"))))
    (is (not (nil? (project-clj "project.clj"))))))

(deftest name-version-test
  (testing "Getting name & version"
    (let [{:keys [name version]} (name-version)]
      (is (= name 'cognicious/remotum)))))

(deftest banner-test
  (testing "Getting banner as array"
    (is (coll? (banner)))))
