(ns cljoss.format
  "Formatters for the output from the tool"
  (:require
   [cljoss.severity :as severity]
   [cljoss.sonatype :as sonatype] 
   [clojure.data.json :as json]
   [clojure.string :as string]))

(defn ^:private ->lein-nvd-format
  [vulnerability]
  {:fileName (:component/coordinates vulnerability)
   :vulnerabilities {:name (:vulnerability/cve vulnerability)
                     :severity (severity/score->
                                (:vulnerability/cvssScore vulnerability))}})

(defn ->json
  "Format the vulnerabilities in a JSON format
   roughly compatible with lein nvd"
  [vulnerabilities]
  (json/pprint 
   {:dependencies (map
                   ->lein-nvd-format
                   vulnerabilities)}))

(defn ^:private indent
  [s]
  (str "  " s))

(defn ^:private ->human-readable
  "Converts the vulnerability to a human readable string"
  [vulnerability]
  (let [nist-link (sonatype/nist-reference
                   vulnerability)]
    (string/join
     "\n"
     ["#########################"
      "Dependency:"
      (indent (:component/coordinates vulnerability))
      "CVE:"
      (indent (:vulnerability/title vulnerability))
      "Score:"
      (indent
       (str
        (:vulnerability/cvssScore vulnerability)
        "/"
        (severity/score->
         (:vulnerability/cvssScore vulnerability))))
      "OSS Index Link:"
      (indent (:vulnerability/reference vulnerability))
      "NIST Link:"
      (indent (or nist-link "NONE FOUND"))])))

(defn ->terminal
  "Format the vulnerabilities to be printed in
   a roughly human readable way, e.g. to a terminal"
  [vulnerabilities]
  (let [formatted (map
                   ->human-readable
                   vulnerabilities)
        output (string/join
                "\n"
                formatted)]
    output))
