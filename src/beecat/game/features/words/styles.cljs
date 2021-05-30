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
     :border "1px solid var(--med)"}]
   [:.word
    {:padding "0.5rem"}]])
