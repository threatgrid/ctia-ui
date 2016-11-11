(ns ctia-ui.util
  "Miscellaneous utility functions"
  (:require
    [clojure.string :refer [split]]))

(defn encode-uri [s]
  (js/encodeURIComponent (str s)))

(defn vec-remove
  "Remove an element from a collection via idx."
  [coll idx]
  (vec (concat (subvec coll 0 idx) (subvec coll (inc idx)))))

(defn neutralize-event
  "Neutralize a native DOM or React event."
  [js-evt]
  (when (fn? (aget js-evt "preventDefault"))
    (.preventDefault js-evt))
  (when (fn? (aget js-evt "stopPropagation"))
    (.stopPropagation js-evt)))

;; FIXME: this function assumes that you will only use the first section
;; of the hash string
;; Probably always true in our case, but could be cleaner
(defn truncate-id
  "Shortens an entity id to a specified hash length."
  [id hash-length]
  (let [v (split id "-")
        entity-type (first v)
        short-hash (subs (second v) 0 hash-length)]
    (str entity-type "-" short-hash)))

(def id-counter (atom 1000))

(defn random-element-id
  "Generates a random ID for a DOM element."
  []
  (swap! id-counter inc)
  (str "element-" @id-counter))
