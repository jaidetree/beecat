(ns framework.stream
  (:require
   [cljs.pprint :refer [pprint]]
   ["baconjs" :as bacon]))

(def Bus (.-Bus bacon))

(defn from-event
  [el event]
  (.fromEvent bacon el event))

(defn from-array
  [arr]
  (.fromArray bacon arr))

(defn from-seq
  [xs]
  (.fromArray bacon (clj->js xs)))

(defn merge-all
  [streams]
  (apply (.-mergeAll bacon) streams))

(defn from-promise
  [promise can-abort]
  (.fromPromise bacon promise can-abort))

(defn log
  [stream]
  (-> stream
      (.doAction pprint)))

(defn later
  [ms-delay v]
  (.later bacon ms-delay v))

(defn interval
  [ms-delay v]
  (.interval bacon ms-delay v))

(defn of
  [v]
  (.once bacon v))
