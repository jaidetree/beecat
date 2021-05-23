(ns beecat.game.features.words.store
  (:require
   [framework.reactor :refer [of-type compose-fx assign-reducers]]))

(def reducer
  (assign-reducers
   {:initial #{}
    :reducers
    {:words/append
     (fn [state action]
       (conj (set state) (:payload action)))}}))
