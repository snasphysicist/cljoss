(ns cljoss.classpath
  "For dealing with classpaths" 
  (:require [clojure.string :as string]))

(defn ->entries
  "Split the string classpath into individual entries"
  [classpath]
  (string/split
   classpath
   #":"))

(defn without-project-entries
  "Removes non-project entries, e.g. local source code.
   Anything that's not in .m2/repositories"
  [entries]
  (filter
   #(and (string/includes? % "repository")
         (string/includes? % ".m2"))
   entries))
