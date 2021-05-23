#!/usr/bin/env bb
(ns beecat.api.words
  (:require
   [cheshire.core :as json]))

(def state (json/parse-string (slurp "../game.json") true))

(println "Content-Type: application/json")
(println "\n")
(println
 (str
  (json/generate-string state)))
