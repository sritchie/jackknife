(ns jackknife.meta)

(defn meta-update
  "Returns the supplied symbol with the supplied `attr` map conj-ed
  onto the symbol's current metadata."
  [sym f]
  (with-meta sym (f (meta sym))))

(defn meta-conj
  "Returns the supplied symbol with the supplied `attr` map conj-ed
  onto the symbol's current metadata."
  [sym attr]
  (meta-update sym (fn [m] (if m (conj m attr) attr))))

(defn set-namespace-value
  "Merges the supplied kv-pair into the metadata of the namespace in
  which the function is called."
  [key-name newval]
  (alter-meta! *ns* merge {key-name newval}))
