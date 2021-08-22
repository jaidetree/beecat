(ns beecat.game.store
  (:require
   [reagent.core :as r]
   [framework.stream :refer [log Bus]]
   [framework.reactor :refer [init reduce-state of-type compose-fx combine-reducers]]
   [beecat.game.features.initialize.store :as initialize]
   [beecat.game.features.answers.store :as answers]
   [beecat.game.features.words.store :as words]
   [beecat.game.features.letters.store :as letters]
   [beecat.game.features.pangrams.store :as pangrams]
   [beecat.game.features.requests.store :as requests]))

(defn log-actions-fx
  [actions _deps]
  (-> actions
      (log)
      (.filter false)))

(def store-reducer
  (combine-reducers
   {:requests requests/reducer
    :answers  answers/reducer
    :pangrams pangrams/reducer
    :words    words/reducer}))

(def store-fx
  (compose-fx
   [#_log-actions-fx
    requests/fx
    letters/fx
    initialize/fx
    words/fx]))

(defonce state (r/atom (store-reducer {} init)))
(defonce actions (Bus.))
(defonce unsubscribe (atom (constantly nil)))

(defn dispatch
  [action]
  (reduce-state state store-reducer action)
  (.push actions action))

(defn subscribe
  []
  (-> (store-fx actions {:state state})
      (.takeUntil (-> actions
                      (of-type :store/end)))
      (.onValue dispatch)))

(defn create
  []
  (reset! unsubscribe (subscribe)))

(defn destroy
  []
  (.push actions
         {:type :store/end
          :payload (js/Date.now)})
  (@unsubscribe))

(defn get-state
  ([]
   @state)
  ([keys]
   (get-in @state keys))
  ([keys default]
   (get-in @state keys default)))

(set! (.-store js/window)
      #js {:getState (fn []
                       (clj->js (get-state)))})
