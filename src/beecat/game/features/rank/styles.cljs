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
  [:.label
   {:flex "0 1 auto"}]
  [:.progress
   {:position :relative
    :flex "1 1 auto"
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
     :transition "all 1200ms ease-out"}]
   [:.milestone
    {:position :absolute
     :top "-2.5px"
     :width "10px"
     :height "10px"
     :border-radius "50%"
     :background "var(--med)"
     :transition "all 250ms ease-out"}
    [:&.active
     {:background "var(--accent)"}]]]
  [:.score
   {:margin "1rem 0"
    :padding "2rem 0"
    :font-size "1.5rem"
    :color "var(--text)"
    :width "100%"
    :position "relative"}
   [:.points
    {:position "absolute"
     :top "0"
     :left 0
     :right 0
     :text-align "center"
     :opacity 0
     :transform "translateY(10px)"
     :transition "all 500ms ease-in-out"}
    [:&.active
     {:opacity 1
      :transform "translateY(0)"}]
    [:&.closing
     {:opacity 0
      :transform "translateY(-10px)"}]]])
