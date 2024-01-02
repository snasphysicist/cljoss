(ns cljoss.user
  (:require
   [cljoss.classpath]
   [cljoss.coordinates :as coordinates]
   [cljoss.core :as cljoss.core]
   [cljoss.format :as format]
   [cljoss.maps :as maps]
   [cljoss.purl :as purl]
   [cljoss.severity :as severity]
   [cljoss.sonatype :as sonatype]))

(defn ^:private ensure-ns-loaded!
  "Use the namespaces of the provided vars somehow"
  [& _]
  nil)

(ensure-ns-loaded!
 cljoss.classpath/->entries
 coordinates/local-path->coordinates
 cljoss.core/-main
 format/->json
 maps/namespace-prefix-keys
 purl/coordinates->package-url
 severity/score->
 sonatype/flattened-reports)
