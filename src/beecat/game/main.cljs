(ns beecat.game.main
  (:require
   [beecat.game.store :as store]
   [beecat.game.core :as game]))

(defn stop
  []
  (store/destroy)
  (println "Stopping game"))

(defn start
  []
  (println "Starting game")
  (store/create)
  (game/init))

(defn -main
  []
  (println "Initializing game")
  (store/create)
  (store/dispatch
   {:type :store/initialize
    :payload (js/Date.now)})
  (game/init))
