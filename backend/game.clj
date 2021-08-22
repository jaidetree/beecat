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
             [:meta
              {:name "viewport"
               :content "width=device-width, initial-scale=1"}]
             [:link
              {:rel "stylesheet"
               :href "/css/style.css"}]]
            [:body
             [:div#game]
             [:script
              {:src "/js/beecat.js"}]
             [:script
              {:src "https://kit.fontawesome.com/1ad808c8ff.js"
               :crossorigin "anonymous"}]
             ]])))
