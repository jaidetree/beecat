(ns beecat.game.features.words.store
  (:require
   [framework.reactor :refer [of-type compose-fx assign-reducers type->action]]))

(def reducer
  (assign-reducers
   {:initial (sorted-set)
    :reducers
    {:words/set
     (fn [_state action]
       (:payload action (sorted-set)))

     :words/append
     (fn [state action]
       (into (sorted-set (:payload action)) state))}}))

(defn serialize
  [words]
  (->> words
       (clj->js)
       (js/JSON.stringify)
       (js/localStorage.setItem "beecat.game.words")))

(defn deserialize
  [answers]
  (let [answers (set answers)]
    (->> (js/localStorage.getItem "beecat.game.words")
         (js/JSON.parse)
         (js->clj)
         (filter #(contains? answers %))
         (into (sorted-set)))))

(defn serialize-words-fx
  [actions {:keys [state]}]
  (-> actions
      (of-type :words/append)
      (.map (fn [_] (get @state :words)))
      (.doAction serialize)
      (.filter false)))

(defn deserialize-words-fx
  [actions {:keys [state]}]
  (-> actions
      (of-type :words/deserialize)
      (.map (fn [] (deserialize (get @state :answers))))
      (type->action :words/set)))

(defn sync-score-fx
  [actions _deps]
  (-> actions
      (of-type :words/set)
      (.filter seq)
      (.map (fn [_] [:sync nil]))
      (type->action :machine/send)))

(def fx
  (compose-fx
   [serialize-words-fx
    deserialize-words-fx
    sync-score-fx]))

(comment
  (cons "taffy" (sorted-set :a :b))
  (into (sorted-set "taffy")))
