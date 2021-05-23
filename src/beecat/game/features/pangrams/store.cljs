(ns beecat.game.features.pangrams.store
  (:require
   [framework.reactor :refer [of-type compose-fx assign-reducers pluck
                              type->action]]))

(def reducer
  (assign-reducers
   {:initial #{}
    :reducers
    {:populate
     (fn [state action]
       (get-in action [:payload :pangrams]))}}))
