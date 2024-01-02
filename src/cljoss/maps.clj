(ns cljoss.maps)

(defn ^:private unqualified-keyword?
  "Returns true only for inputs which are
   keywords and are not yet namespace qualified"
  [k]
  (and (keyword? k)
       (not (qualified-keyword? k))))

(defn ^:private prefixed-with
  "Prefix the given keyword with given keyword"
  [prefix k]
  (keyword (name prefix) (name k)))

(defn namespace-prefix-keys
  [m n]
  (reduce-kv
   (fn
     [m k v]
     (assoc 
      m
      (if (unqualified-keyword? k)
        (prefixed-with n k)
        k)
      v))
     {}
   m))
