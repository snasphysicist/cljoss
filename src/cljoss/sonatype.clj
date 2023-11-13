(ns cljoss.sonatype
  "Interaction with the OSS index API"
  (:require
   [clj-http.client :as client]
   [cljoss.maps :as maps]
   [clojure.data.json :as json]
   [clojure.string :as string]
   [taoensso.timbre :as logging]))

(def ^:private url
  "https://ossindex.sonatype.org/api/v3/component-report")

(defn ^:private request-reports-for*
  "Requests reports for all package urls in a single
   request, will therefore fail if there are more
   urls than Sonatype allows (currently 128)"
  [package-urls]
  (logging/debug 
   "Requesting reports for " 
   (count package-urls) 
   "packages : " 
   package-urls)
  (client/request
   {:method :post
    :url url
    :headers {"Content-Type" "application/json"}
    :body (json/write-str
           {"coordinates" package-urls})
    :throw-exceptions false
    :cookie-policy :none}))

(defn ^:private body-deserialised
  [report]
  (update-in
   report
   [:body]
   (fn 
     [body]
     (json/read-str
      body
      :key-fn keyword))))

(defn ^:private code->error
  "Converts a HTTP status code to an error keyword, if any"
  [status-code]
  (get 
   {200 nil 
    429 :rate-limited}
   status-code
   :unknown))

(defn ^:private with-result
  "Adds result indicators :success and :error
   to a report response"
  [report]
  (let [error (code->error (:status report))
        ok? (nil? error)]
    (if ok?
      (assoc report :success true)
      (merge report {:success false
                     :error error}))))

(defn request-reports-for
  "Requests reports for all the package urls
   and returns a sequence of reponses.
   The requests may be done in batches of a size
   determined by this function, hence the sequence.
   Each response will contain :success boolean,
   :error if the request failed,
   and :reports as a map."
  [package-urls]
  (let [_ (logging/debug (count package-urls) "packages")
        batches (partition-all 100 package-urls)
        _ (logging/debug (count batches) "batches")
        reports (doall
                 (map
                  request-reports-for*
                  batches))
        reports (map
                 body-deserialised
                 reports)]
    (map
     with-result
     reports)))

(defn flattened-reports
  "Given a set of report responses, 
   returns all of the report data as a seq"
  [reports]
  (mapcat :body reports))

(defn highest-score
  "Returns the highest CVSS score for a component"
  [component-report]
  (apply
   max
   0
   (map
    :cvssScore
    (:vulnerabilities component-report))))

(defn flattened-vulnerabilities
  "Merges the component information with each
   vulnerability for that component into a single seq"
  [component-report]
  (map
   (fn
     [vulnerability]
     (merge
      (maps/namespace-prefix-keys 
       component-report 
       :component)
      (maps/namespace-prefix-keys
       vulnerability
       :vulnerability)))
   (:vulnerabilities component-report)))

(defn nist-reference
  "Finds the nist reference in a component vulnerability, if any"
  [vulnerability]
  (let [nist-links (filter
                    #(string/includes? % "nist.gov")
                    (:vulnerability/externalReferences
                     vulnerability))]
    (when (seq nist-links)
      (first nist-links))))
