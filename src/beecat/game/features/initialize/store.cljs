(ns beecat.game.features.initialize.store
  (:require
   [framework.reactor :refer [of-type compose-fx assign-reducers pluck
                              type->action]]))

(defn populate-state-fx
  [actions _deps]
  (-> actions
      (of-type :request/response)
      (.filter #(= (get-in % [:payload :request :name]) :state))
      (pluck [:payload :response])
      (.map (fn [response]
              {:type :populate
               :payload response}))))

(defn populate-fsm-fx
  [actions _deps]
  (-> actions
      (of-type :populate)
      (pluck [:payload])
      (.map (fn [response]
              {:type :machine/send
               :payload [:ready response]}))))

(def fx
  (compose-fx [populate-state-fx
               populate-fsm-fx]))
