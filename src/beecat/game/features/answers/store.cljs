(ns beecat.game.features.answers.store
  (:require
   [framework.reactor :refer [of-type compose-fx assign-reducers pluck
                              type->action]]))

(def reducer
  (assign-reducers
   {:initial #{}
    :reducers
    {:populate
     (fn [state action]
       (set (get-in action [:payload :answers])))}}))

(defn select-answers
  [state]
  (get state :answers))
