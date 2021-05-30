(ns beecat.game.features.words.views
  (:require
   [beecat.game.machine :refer [game-machine]]
   [beecat.game.store :as store]
   [beecat.game.features.words.styles :refer [style]]
   [framework.fsm :as fsm]))

(defn words
  []
  (let [found-words (get @store/state :words)]
    [:div
     {:class (:words style)}
     [:ul.list
      (for [[i word] (map-indexed vector (sort found-words))]
        [:li.word
         {:key i}
         word])
      #_(for [w (repeatedly 100 fsm/gen-id)]
          [:li.word
           {:key w}
           w])]]))
