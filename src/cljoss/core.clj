(ns cljoss.core
  (:gen-class)
  (:require
   [cljoss.classpath]
   [cljoss.coordinates :as coordinates]
   [cljoss.format :as format]
   [cljoss.purl :as purl]
   [cljoss.sonatype :as sonatype]
   [clojure.string :as string]
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
  [classpath url]
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
                 package-urls
                 url)
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
   and the formatted output in :description.
   
   Required inputs:
   - :classpath is the full classpath, as string
   - :format determines the output format, json or terminal
   
   Optional inputs:
   - :url is the url of the Sonatype endpoint,
       mostly changed for testing. 
       Defaults to https://ossindex.sonatype.org/api/v3/component-report
   "
  [opts]
  (let [{:keys [classpath format url]} opts
        url (or 
             url
             "https://ossindex.sonatype.org/api/v3/component-report")]
  (assert
   (seq classpath)
   "A classpath must be provided")
  (let [vulnerabilities (find-vulnerabilities classpath url)
        output (case format
                 "terminal" (format/->terminal vulnerabilities)
                 "json" (format/->json vulnerabilities))]
    (when (seq vulnerabilities)
      {:found? true
       :description output}))))

(defn ^:private on-failure!
  [message]
  (println message)
  (System/exit 1))

(def ^:private cli-flags
[["-c" "--classpath CLASSPATH" "Project classpath"
  "-o" "--output OUTPUT" "Output format (terminal/json)"]])

(defn ^:private validated-options
  "Throws if the options are not valid, 
   else returns :options from the input"
  [options]
  (let [classpath (:classpath (:options options))
        classpath-message (when 
                           (string/blank? classpath) 
                            "A classpath is required")
        output (:output (:options options))
        output-message (when-not
                        (#{"terminal" "json"} output)
                         "Output must be 'terminal' or 'json'")
        messages (remove
                  nil?
                  [classpath-message output-message])]
    (when (seq messages)
      (on-failure! 
       (string/join 
        "; " 
        messages)))
    (:options options)))

(defn -main
  "Entrypoint for the cljoss CLI"
  [& args]
  (let [options (cli/parse-opts args cli-flags)
        {:keys [classpath output]} (validated-options options)
        {:keys [:found? :description]} 
        (run {:classpath classpath
              :format output})]
    (if found?
      (on-failure! description)
      (do
        (println "No vulnerabilities found")
        (System/exit 0)))))
