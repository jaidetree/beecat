(ns beecat.game.core
  (:require
   [clojure.string :as s]
   [reagent.dom :as rdom]
   [framework.fsm :as fsm]
   [beecat.game.views :refer [game]]
   [beecat.game.machine :refer [game-machine]]))

(defn init
  []
  (rdom/render [game] (js/document.getElementById "game"))
  (fsm/send game-machine :init (js/Date.now)))
