(ns cljoss.scan-test
  (:require
   [cljoss.core :as core]
   [cljoss.sonatype-mock :as mock]
   [clojure.data.json :as json]
   [clojure.test :refer [deftest is]]))

(deftest finds-vulnerabilities-test
  (let [{:keys [stop! port]} (mock/start!)]
    (try
      (let [classpath (str
                       "/home/foo/.m2/repository/org/apache/xmlgraphics/batik-css/1.16/batik-css-1.16.jar"
                       ":"
                       "/home/foo/.m2/repository/org/eclipse/jetty/jetty-util/9.4.48.v20220622/jetty-util-9.4.48.v20220602.jar"
                       ":"
                       "/home/foo/.m2/repository/mysql/mysql-connector-java/8.0.16/mysql-connector-java-8.0.16.jar")
            {found? :found?
             description :description}
            (core/run {:classpath classpath
                       :format "json"
                       :url (str "http://localhost:" port "/")})
            report (json/read-str description :key-fn keyword)
            vulnerabilities (:dependencies report)]
        (is found?)
        (is (= 4 (count vulnerabilities)))
        (is
         (=
          (set vulnerabilities)
          #{{:fileName "pkg:maven/mysql/mysql-connector-java@8.0.16",
             :vulnerabilities [{:name "CVE-2020-2934", :severity "medium"}]}
            {:fileName "pkg:maven/mysql/mysql-connector-java@8.0.16",
             :vulnerabilities [{:name "CVE-2021-2471", :severity "medium"}]}
            {:fileName "pkg:maven/mysql/mysql-connector-java@8.0.16",
             :vulnerabilities [{:name "CVE-2022-21363", :severity "medium"}]}
            {:fileName "pkg:maven/org.eclipse.jetty/jetty-util@9.4.48.v20220622",
             :vulnerabilities [{:name "CVE-2023-26048", :severity "medium"}]}})))
      (finally (stop!)))))
