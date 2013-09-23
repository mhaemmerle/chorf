(ns chorf.core
  (:require [goog.storage.mechanism.HTML5LocalStorage :as local-storage]
            [goog.iter :as g-iter]
            [domina.xpath :as dx]
            [domina :as d]
            [shodan.console :as console]
            [clojure.set :as set]))

(def storage (goog.storage.mechanism.HTML5LocalStorage.))

(defn get-elements
  []
  (let [result (dx/xpath "//div[contains(concat(' ', normalize-space(@class), ' '), 'fn')]")]
    (map d/text (d/nodes result))))

(defn clear-storage
  []
  (.clear storage))

;; (clear-storage)

(defn store-snapshot
  [elements timestamp]
  (.set storage timestamp (JSON/stringify (clj->js {:elements elements}))))

(defn take-snapshot
  [timestamp]
  (store-snapshot (get-elements) timestamp))

(defn storage-keys
  []
  (g-iter/toArray (.__iterator__ storage true)))

(defn get-diff
  [ks]
  (let [sorted-ks (sort (fn [a b] (< a b)) ks)]
    (reduce (fn [acc k]
              (let [elements (set (get (js->clj (JSON/parse (.get storage k))) "elements"))
                    diff (if (seq acc)
                           (let [s1 elements
                                 s2 (:elements (last acc))]
                             [(set/difference s1 s2)
                              (set/difference s2 s1)])
                           nil)]
                (conj acc {:key k :diff diff :elements elements}))) [] sorted-ks)))

(defn needs-update?
  [current-timestamp timestamps]
  (let [sorted-timestamps (reverse (sort timestamps))
        latest-timestamp (first sorted-timestamps)]
    (or (nil? latest-timestamp) (> current-timestamp latest-timestamp))))

(defn local-midnight
  [date]
  (let [seconds (+ (* (.getHours date) 3600)
                   (* (.getMinutes date) 60)
                   (.getSeconds date))
        milliseconds (+ (.getMilliseconds date) (* seconds 1000))]
    (Math/round (/ (- (.getTime date) milliseconds) 1000))))

(defn print-summary
  [data]
  (let [diffs (map
               #(let [diff (:diff %)
                      s (if (nil? diff)
                          "+/- []"
                          (let [[a b] diff
                                s1 (clojure.string/join ", " b)
                                s2 (clojure.string/join ", " a)]
                            (str "- [" s1 "], + [" s2 "]")))]
                  (str (:key %) " -> " s)) data)]
    (console/log (clojure.string/join ",\n" diffs))))

(defn init
  []
  (let [current-timestamp (local-midnight (js/Date.))]
    (when (needs-update? current-timestamp (storage-keys))
      (take-snapshot current-timestamp)))
  (print-summary (get-diff (storage-keys))))

(init)
