(ns beecat.game.features.letters.styles
  (:require
   [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.honeycombs
   {:position :relative
    :width "285px"
    :height "285px"
    :margin "2rem 0"}]
  [:.honeycomb
   {:position :absolute
    :width "40%"
    :height "calc(100% / 3)"
    :top "calc(100% / 3)"
    :left "30%"
    :cursor :pointer
    :outline "none"}
   [:.fill
    {:stroke-width "2px"
     :stroke "var(--accent)"}]
   [:.letter
    {:fill "var(--text)"
     :pointer-events "none"
     :font-weight "bold"
     :font-size "1.3rem"
     :text-anchor "middle"
     :text-transform "uppercase"
     :opacity 1
     :transition "opacity 500ms ease-in-out"}]]
  [:.centercomb
   [:.fill
    {:fill "var(--accent)"}]
   [:.letter
    {:fill "var(--bg)"}]]
  [:.fade-out
   [:.outer
    [:.letter
     {:opacity 0}]]]
  [".honeycomb:nth-child(1)"
   {:transform "translate(-75%, -50%)"}]
  [".honeycomb:nth-child(2)"
   {:transform "translate(0, -100%)"}]
  [".honeycomb:nth-child(3)"
   {:transform "translate(75%, -50%)"}]
  [".honeycomb:nth-child(4)"
   {:transform "translate(75%, 50%)"}]
  [".honeycomb:nth-child(5)"
   {:transform "translate(0, 100%)"}]
  [".honeycomb:nth-child(6)"
   {:transform "translate(-75%, 50%)"}]

  [:.word
   {:position :relative
    :width "16.25rem; /* 260px */"
    :margin "auto"}
   [:.letters
    {:position "absolute"
     :left 0
     :top 0
     :bottom 0
     :right 0}]
   [:.required
    {:color "var(--accent)"}]
   [:.input
    {:position :relative
     :border "1px solid var(--text)"
     :padding "0.3125rem; /* 5px */"
     :font-size "2rem"
     :min-height "calc(1em + 5px)"
     :box-sizing "border-box"
     :text-transform "uppercase"
     :letter-spacing "3px"
     :text-align "center"}
    [:.required
     {:color "var(--accent)"}]]]
  [:.message
   {:position :absolute
    :padding "3px 10px"
    :border-radius "3px"
    :font-size "1.25rem"
    :text-align "center"
    :white-space "nowrap"
    :bottom "150%"
    :left "50%"
    :transform "translateX(-50%)"
    :opacity 0
    :transition "all 500ms ease-in-out"}
   [:&:before
    {:position "absolute"
     :content "''"
     :width "1px"
     :height "1px"
     :border "10px solid transparent"
     :top "100%"
     :left "50%"
     :transform "translateX(-50%)"}]
   [:&.active
    {:opacity 1
     :transition "all 250ms ease-in-out"}]]
  [:.rejected
   {:transform "translateX(-50%) translateY(10px)"
    :background "var(--text)"
    :color "var(--bg)"}
   [:&:before
    {:border-top-color "var(--text)"}]
   [:&.active
    {:transform "translateX(-50%) translateY(0)"}]]
  [:.accepted
   {:background "var(--accent)"
    :color "var(--bg)"
    :transform "translate(-50%, -10px)"}
   [:&:before
    {:border-top-color "var(--accent)"}]
   [:&.active
    {:transform "translate(-50%, 0)"}]])
