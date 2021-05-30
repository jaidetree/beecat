(ns beecat.game.views
  (:require
   [beecat.game.styles :refer [style]]
   [beecat.game.machine :refer [game-machine]]
   [beecat.game.common.views :refer [spinner]]
   [beecat.game.features.letters.views :refer [actions honeycombs word]]
   [beecat.game.features.rank.views :refer [rank score]]
   [beecat.game.features.words.views :refer [words]]))

(defn game
  []
  [:section
   [:header
    {:class (:head style)}
    [:h1
     "Beecat"]]
   [:main
    {:class (:main style)}
    (let [is-loading (= (get @game-machine :state) :loading)]
      (if is-loading
        [spinner
         {:width  "3rem"
          :height "3rem"
          :color  :dark}]
        [:div
         {:class (:game style)}
         [rank]
         [words]
         [score]
         [word]
         [honeycombs]
         [actions]]))]])
