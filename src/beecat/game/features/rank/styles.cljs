(ns beecat.game.features.rank.styles
  (:require
   [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.rank
   {:display :flex
    :flex-flow "row nowrap"
    :width "100%"
    :margin "5px"
    :align-items :center}]
  [:.progress
   {:position :relative
    :flex "1 1 100%"
    :top "-2.5px"
    :margin-left "5px"}
   [:.bar
    {:position :absolute
     :left 0
     :top 0
     :width "100%"
     :height "0.3125rem; /* 5px */"
     :background "var(--med)"
     :border-radius "5px"}]
   [:.cursor
    {:position :absolute
     :left 0
     :top 0
     :height "0.3125rem; /* 5px */"
     :background "var(--accent)"
     :border-radius "5px"
     :transition "all 500ms ease-out"}]
   [:.milestone
    {:position :absolute
     :top "-2.5px"
     :width "10px"
     :height "10px"
     :border-radius "50%"
     :background "var(--med)"
     :transition "all 250ms ease-out"}
    [:&.active
     {:background "var(--accent)"}]]])
