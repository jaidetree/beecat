(ns beecat.game.styles
  (:require
   [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.head
   {:background "var(--accent)"
    :color "var(--bg)"
    :text-align "center"}
   [:h1
    {:margin 0
     :padding 0}]]
  [:.main
   {:padding "3rem 1rem"}]
  [:.game
   {:display :flex
    :flex-flow "column nowrap"
    :align-items "center"}])
