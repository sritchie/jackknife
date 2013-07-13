(ns jackknife.core
  (:import [java.net InetAddress]
           [java.util UUID]
           [java.util.concurrent.locks ReentrantReadWriteLock]))

(defn ^String path
  [x]
  (if (string? x)
    x
    (.getAbsolutePath ^java.io.File x)))

(defmacro with-timeout
  "Accepts a vector with a timeout (in ms) and any number of forms and
  executes those forms sequentially. returns the result of the last
  form or nil (if the timeout is reached.) For example:

  (with-timeout [100]
    (Thread/sleep 50)
    \"done!\")
  ;;=> \"done!\"

  (with-timeout [100]
    (Thread/sleep 200)
    \"done!\")
  ;;=> nil"
  [[ms] & body]
  `(let [^java.util.concurrent.Future f# (future ~@body)]
     (try (.get f# ~ms java.util.concurrent.TimeUnit/MILLISECONDS)
          (catch java.util.concurrent.TimeoutException e#
            (.cancel f# true)
            nil))))

(defn memoize
  "Similar to clojure.core/memoize, but allows for an optional
  initialization map."
  {:static true}
  ([f] (memoize f {}))
  ([f init-m]
     (let [mem (atom init-m)]
       (fn [& args]
         (if-let [e (find @mem args)]
           (val e)
           (let [ret (apply f args)]
             (swap! mem assoc args ret)
             ret))))))

(defn multifn? [op]
  (instance? clojure.lang.MultiFn op))

(defn uuid []
  (str (UUID/randomUUID)))

(defn unique-rooted-paths
  "Returns an infinite* sequence of unique subpaths of the supplied
  root path."
  [root]
  (repeatedly #(str root "/" (uuid))))

(defmacro safe-assert
  "Evaluates expr and throws an exception if it does not evaluate to
  logical true."
  ([x]
     (when *assert*
       `(when-not ~x
          (throw (new AssertionError
                      (str "Assert failed: " (pr-str '~x)))))))
  ([x message]
     (when *assert*
       `(when-not ~x
          (throw (new AssertionError
                      (str "Assert failed: " ~message "\n" (pr-str '~x))))))))

(defn sleep
  "Sleeps for the supplied length of time. Negative numbers (and nil) are
  treated as zero."
  [len]
  (when (and len (pos? len))
    (Thread/sleep len)))

(defn register-shutdown-hook [shutdown-func]
  (-> (Runtime/getRuntime)
      (.addShutdownHook (Thread. shutdown-func))))

(defmacro dofor [bindings & body]
  `(doall (for ~bindings (do ~@body))))

(defn future-values [futures]
  (dofor [^java.util.concurrent.Future f futures]
         (.get f)))

(defmacro p-dofor [bindings & body]
  `(let [futures# (dofor ~bindings
                         (future ~@body))]
     (future-values futures#)))

(defn do-pmap [fn & colls]
  (doall (apply pmap fn colls)))

(defn update-vals [m f]
  (into {} (for [[k v] m]
             [k (f k v)])))

(defn val-map [f m]
  (into {} (for [[k v] m]
             [k (f v)])))

(defn reverse-multimap
  "{:a [1 2] :b [1] :c [3]} -> {1 [:a :b] 2 [:a] 3 [:c]}"
  [amap]
  (apply merge-with concat
         (mapcat
          (fn [[k vlist]]
            (for [v vlist] {v [k]}))
          amap)))

(defn local-hostname []
  (.getHostAddress (InetAddress/getLocalHost)))

;; ## Control Flow

(defmacro with-ret-bound [[sym val] & body]
  `(let [~sym ~val]
     ~@body
     ~sym))

(defmacro with-ret [val & body]
  `(with-ret-bound [ret# ~val]
     ~@body))

;; ## Locking

(defn mk-rw-lock []
  (ReentrantReadWriteLock.))

(defmacro with-read-lock
  [rw-lock & body]
  (let [lock (with-meta rw-lock {:tag `ReentrantReadWriteLock})]
    `(let [rlock# (.readLock ~lock)]
       (try (.lock rlock#)
            ~@body
            (finally (.unlock rlock#))))))

(defmacro with-write-lock [rw-lock & body]
  (let [lock (with-meta rw-lock {:tag `ReentrantReadWriteLock})]
    `(let [wlock# (.writeLock ~lock)]
       (try (.lock wlock#)
            ~@body
            (finally (.unlock wlock#))))))

;; ## Error Handlers

(defn throw-illegal [& xs]
  (throw
   (IllegalArgumentException. ^String (apply str xs))))

(defn throw-runtime [& xs]
  (throw
   (RuntimeException. ^String (apply str xs))))
