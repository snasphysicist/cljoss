(defproject pw.snas/cljoss "1.0.0"
  :description "Finds vulnerabilities in jars on classpaths"
  :url "https://github.com/snasphysicist/cljoss"
  :license {:name "MIT"
            :url "https://mit-license.org/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [babashka/fs "0.4.19"]
                 [clj-http "3.12.3"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [com.taoensso/timbre "6.3.1"]]
  :main ^:skip-aot cljoss.core
  :target-path "target/%s"
  :profiles {:test {:dependencies [[http-kit "2.3.0"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :repl-options {:init-ns cljoss.user})
