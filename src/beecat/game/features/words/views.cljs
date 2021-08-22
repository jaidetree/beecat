(ns beecat.game.features.words.views
  (:require
   [reagent.core :as r]
   [beecat.game.store :as store]
   [beecat.game.features.words.styles :refer [style]]))

(defn words
  []
  (let [found-words (get @store/state :words)]
    [:div
     {:class (r/class-names
              (:words style)
              "words-list")}
     [:ul.list
      (for [[i word] (map-indexed vector found-words)]
        [:li.word
         {:key i}
         word])
      #_(for [w (repeatedly 100 fsm/gen-id)]
          [:li.word
           {:key w}
           w])]]))
