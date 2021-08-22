(ns beecat.game.features.rank.styles
  (:require
   [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.rank
   {:width "100%"
    :margin "5px"
    :align-items :center}]
  [:.label
   {:display "block"
    :position "relative"
    :text-align "center"
    :font-size "1.5rem"}
   [:button
    {:background "none"
     :border "none"
     :color "var(--text)"
     :display "inline"
     :line-height 1.5}]]
  [:.progress
   {:position :relative
    :margin "0.625rem 0"
    :height "0.625rem; /* 10px */"}
   [:.bar
    {:position :absolute
     :left 0
     :top 0
     :bottom 0
     :margin "auto"
     :width "100%"
     :height "0.1875rem; /* 3px */"
     :background "var(--med)"
     :border-radius "5px"}]
   [:.cursor
    {:position :absolute
     :left 0
     :top 0
     :bottom 0
     :margin "auto"
     :height "0.1875rem; /* 3px */"
     :background "var(--accent)"
     :border-radius "5px"
     :transition "all 1200ms ease-out"}]
   [:.milestones
    {:position :absolute
     :top "0"
     :left "0"
     :right "0"
     :bottom "0"}]
   [:.milestone
    {:position :absolute
     :top "0"
     :bottom "0"
     :margin "auto"
     :width "10px"
     :height "10px"
     :border-radius "50%"
     :background "var(--med)"
     :transition "all 250ms ease-out"
     :transform "translate(-50%, 0%)"}
    [:&.active
     {:background "var(--accent)"}]]]
  [:.score
   {:font-size "1.5rem"
    :color "var(--accent)"
    :display "inline-block"
    :vertical-align "top"
    :position "relative"
    :line-height 1.5}
   [:.points
    {:position "absolute"
     :white-space "nowrap"
     :text-align "center"
     :opacity 0
     :transform "translateY(10px)"
     :transition "all 500ms ease-in-out"}
    [:&.active
     {:opacity 1
      :transform "translateY(0)"}]
    [:&.closing
     {:opacity 0
      :transform "translateY(-10px)"}]]]

  [:.ranks-modal
   [:.list
    {:margin "0"
     :padding "0"
     :width "100%"
     :display "table"
     :list-style "none"}]
   [:.item
    {:display "table-row"}]
   [:.label
    {:font-weight "600"
     :text-transform "uppercase"
     :color "var(--black-50)"}]
   [:.words :.rank
    {:width "50%"
     :display "table-cell"
     :padding "0.5rem"
     :border-bottom "1px solid var(--light)"}]]

  [:.complete-modal
   {:text-align "center"}
   [:.greeting
    {:color "var(--accent)"
     :font-family "var(--display-font)"
     :font-size "3rem"
     :font-weight 400}]
   [:i
    {:font-size "3rem"
     :color "var(--accent)"}]
   [:p
    {:margin-top "2rem"
     :font-size "1.25rem"}]])
