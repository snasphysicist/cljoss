(ns cljoss.coordinates
  "For handling package coordinates"
  (:require
   [babashka.fs :as fs]
   [clojure.string :as string]))

(defn ^:private segments
  "Splits a path like a/b/c into segments ('a' 'b' 'c')"
  [path]
  (map
   fs/file-name
   (fs/components
    path)))

(defn ^:private without-local-prefix
  "Keeps only segments of a path to a local jar assumed
   to be in .m2/respository after the repository segment"
  [segments]
  (drop
   1
   (drop-while
    #(not= "repository" %)
    segments)))

(defn ^:private group-id
  "Given segments of a path to a jar after .m2/repository
   returns the group-id of that jar"
  [segments]
  (string/join
   "."
   (drop-last
    3
    segments)))

(defn ^:private artifact-id
  "Given segments of a path to a jar after .m2/repository
   returns the artifact-id of that jar"
  [segments]
  (nth (reverse segments) 2))

(defn ^:private version
  "Given segments of a path to a jar after .m2/repository
   returns the version of that jar"
  [segments]
  (second (reverse segments)))

(defn local-path->coordinates
  "Takes a local path, assumed to be in .m2/respository,
   and converts this to artifact coordinates as a map
   with keys :group-id, :artifact-id and :version"
  [local-path]
  (let [segments (segments local-path)
        segments (without-local-prefix segments)]
    {:group-id (group-id segments)
     :artifact-id (artifact-id segments)
     :version (version segments)}))
