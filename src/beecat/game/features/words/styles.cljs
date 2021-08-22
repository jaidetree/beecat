(ns beecat.game.features.words.styles
  (:require
   [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.words
   {:width "100%"
    :margin "0.5rem 0"
    :font-size "1.25rem"
    :text-transform "uppercase"
    :overflow "auto"
    :box-sizing "border-box"}
   [:.list
    {:list-style :none
     :margin 0
     :padding 0
     :display :flex
     :flex-flow "row nowrap"
     :height "calc(1.25rem + 1rem + 2px)"
     :border "1px solid var(--med)"}]
   [:.word
    {:padding "0.5rem"}]])
