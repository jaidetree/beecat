#!/usr/bin/env bb
(ns server.main
  (:require
   [hiccup2.core :as html]))

;(def state (json/parse-string (slurp "game.json") true))

(println "Content-Type: text/html")
(println "\n")
(println (str
          "<!doctype html>"
          (html/html
           [:html
            [:head
             [:title
              "Beecat"]
             [:link
              {:rel "stylesheet"
               :href "/css/style.css"}]]
            [:body
             [:div#game]
             [:script
              {:src "/js/beecat.js"}]]])))
