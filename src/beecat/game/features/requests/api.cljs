(ns beecat.game.features.requests.api
  (:require
   [clojure.string :as s]
   [kitchen-async.promise :as p]
   [framework.stream :as stream]
   [cljs.reader :refer [read-string]]))

(def default-headers
  {:accept "application/json"
   :content-type "application/json"})

(defn format-response
  [body request]
  {:type     :request/response
   :payload  {:response body
              :request  request}})

(defn format-server-error
  [body request]
  (if (and (= (:status body) :error) (:errors body))
    {:type    :request/error
     :payload {:request request
               :errors (:errors body)}}
    {:type    :request/error
     :paylod  {:request request
               :errors [{:name :server-request-error
                         :label "Error"
                         :message (str
                                   "A major error has occured preventing us from completing your request. Please contact your administrator and provide the error uuid "
                                   (:error_uuid body))
                         :section :form}]}}))

(defn format-errors
  [body request]
  {:type    :request/error
   :payload {:request request
             :errors (->> body
                          (:errors))}})

(defn response-body->clj
  [response]
  (let [headers (js->clj (.-headers response) :keywordize-keys true)
        [content-type] (s/split (.get headers "content-type") #";")]
    (case content-type
      "application/json" (p/-> response
                               (.json)
                               (js->clj :keywordize-keys true))
      "application/edn" (p/-> response
                              (.text)
                              (read-string))
      (p/-> response
            (.text)))))

(defn response->clj
  [response]
  (try
    (response-body->clj response)
    (catch js/Error e
      {:status :error
       :errors [{:name :server-request-error
                 :label "Error"
                 :message "A major error has occurred preventing us from completing your request. Please contact your administrator."
                 :section :form}]})))

(defn ^:private send!
  [url request options]
  (p/let [response (js/fetch url (clj->js options))]
    (case (.-status response)
      400       (p/-> response
                      (response->clj)
                      (format-errors request))
      (200 201) (p/-> response
                      (response->clj)
                      (format-response request))
      (p/-> response
            (response->clj)
            (format-server-error request)))))

(defn send-request
  [{:keys [url method body headers] :as request}]
  (let [options {:method (or method :get)
                 :headers (merge
                           default-headers
                           headers)
                 :body (when (contains? #{:post :put :patch} method)
                         (js/JSON.stringify
                          (clj->js body)))}]
    (-> (send! url request options)
        (stream/from-promise true)
        (.throttle 1000)
        (.doError js/console.error))))

(comment
  (-> (send-request
       {:name     :some-data
        :url      "/data.json"
        :method   :get})
      (stream/log)))
