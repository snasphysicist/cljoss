(ns cljoss.purl
  "Handles package urls required for querying
   packages in Sonatype's OSS index")

(defn coordinates->package-url
  "Given coordinates with :group-id, :artifact-id
   and version, returns the maven package url"
  [{group-id :group-id
    artifact-id :artifact-id
    version :version}]
  (str
   "maven:/"
   group-id
   "/"
   artifact-id
   "@"
   version))
