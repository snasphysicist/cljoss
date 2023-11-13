(ns cljoss.core
  (:gen-class)
  (:require
   [cljoss.classpath]
   [cljoss.coordinates :as coordinates]
   [cljoss.format :as format]
   [cljoss.purl :as purl]
   [cljoss.sonatype :as sonatype]
   [clojure.tools.cli :as cli]
   [taoensso.timbre :as logging]))

(defn ^:private assert-all-successful
  "Throws if the reports did not all complete succesfully"
  [reports]
  (let [failures (filter
                  (complement :success)
                  reports)
        errors (set
                (map
                 :error
                 failures))]
    (assert
     (empty? failures)
     (str "Failed with errors: " errors)))
  (logging/debug (count reports) "successful Sonatype requests"))

(defn ^:private find-vulnerabilities
  "Runs the complete pipeline of processing the classpath,
   requesting component reports and extracting vulnerabilities,
   then outputting the results"
  [classpath]
  (let [entries (cljoss.classpath/->entries
                 classpath)
        _ (logging/debug "Analysing" (count entries) "classpath entries")
        entries (cljoss.classpath/without-project-entries
                 entries)
        _ (logging/debug "Analysing" (count entries) "non-project classpath entries")
        coordinates (map
                     coordinates/local-path->coordinates
                     entries)
        package-urls (map
                      cljoss.purl/coordinates->package-url
                      coordinates)
        reports (sonatype/request-reports-for
                 package-urls)
        _ (assert-all-successful reports)
        reports (sonatype/flattened-reports
                 reports)
        _ (logging/debug "Received" (count reports) "reports")
        by-highest-score (reverse
                          (sort-by
                           sonatype/highest-score
                           reports))
        vulnerabilities (flatten
                         (map
                          sonatype/flattened-vulnerabilities
                          by-highest-score))
        _ (logging/debug "Found" (count vulnerabilities) "vulnerabilities")]
    vulnerabilities))

(defn run
  "Find vulnerabilities and format them if found.
   Returns a map with truthy :found? if any are found
   and the formatted output in :description."
  [{classpath :classpath
    output-format :format}]
  (assert
   (seq classpath)
   "A classpath must be provided")
  (let [vulnerabilities (find-vulnerabilities classpath)
        output (case output-format
                 "terminal" (format/->terminal vulnerabilities)
                 "json" (format/->json vulnerabilities))]
    (when (seq vulnerabilities)
      {:found? true
       :description output})))

(def ^:private cli-flags
[["-c" "--classpath CLASSPATH" "Project classpath"
  "-o" "--output OUTPUT" "Output format (terminal/json)"]])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [options (cli/parse-opts args cli-flags)
        classpath (-> options :options :classpath)
        output (-> options :options :output)
        {:keys [:found? :description]} 
        (run {:classpath classpath
              :format output})]
    (if found?
      (do 
        (println description)
        (System/exit 1))
      (do
        (println "No vulnerabilities found")
        (System/exit 0)))))
