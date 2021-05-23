(ns beecat.game.features.letters.store
  (:require
   [framework.stream :refer [from-event]]
   [framework.reactor :refer [of-type type->action compose-fx]]))

(defn key-up-fx
  [_actions _deps]
  (-> js/window
      (from-event "keyup")
      (.map (fn [e]
              (let [key (.-key e)]
                (case (keyword key)
                  :Enter [:submit nil]
                  :Backspace [:delete e]
                  [:letter (.-key e)]))))
      (type->action :machine/send)))

(def fx (compose-fx [key-up-fx]))
