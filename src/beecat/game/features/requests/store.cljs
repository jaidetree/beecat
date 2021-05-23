(ns beecat.game.features.requests.store
  (:require
   [framework.reactor :refer [of-type assign-reducers compose-fx pluck]]
   [beecat.game.features.requests.api :refer [send-request]]))

(def reducer
  (assign-reducers
   {:initial {}
    :reducers
    {:request/pending
     (fn [state action]
       (let [request (:payload action)]
         (assoc state
                (or (:name request) (:url request))
                {:name     (:name request)
                 :request  request
                 :status   :pending
                 :data     {}
                 :errors []})))

     :request/response
     (fn [state {:keys [payload]}]
       (let [{:keys [request response]} payload]
         (update
          state
          (or (:name request) (:url request))
          #(merge % {:data   response
                     :status :fulfilled
                     :errors []}))))

     :request/error
     (fn [state {:keys [payload]}]
       (let [{:keys [errors request]} payload]
         (update
          state
          (or (:name request) (:url request))
          #(merge % {:status :error
                     :data   {}
                     :error  errors}))))

     :request/clear
     (fn [state {:keys [payload]}]
       (dissoc state payload))}}))

(defn create-request
  [params]
  {:type    :request/pending
   :payload params})

(defn clear-request
  [resource]
  {:type :request/clear
   :payload resource})

(defn send-on-request-fx
  [actions _]
  (-> actions
      (of-type :request/pending)
      (pluck :payload)
      (.flatMap send-request)))

(def fx
  (compose-fx
   [send-on-request-fx]))
