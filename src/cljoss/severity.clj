(ns cljoss.severity
  "Assess the severity of a vulnerability")

(defn score->
  [score]
  (cond
    (< 7 score) :high
    (< 4 score) :medium
    :else :low))
