(ns cljoss.severity
  "Assess the severity of a vulnerability")

(defn score->
  "Converts an integer numerical score
   to a severity :high, :medium or :low"
  [score]
  (cond
    (< 7 score) :high
    (< 4 score) :medium
    :else :low))
