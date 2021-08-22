(ns beecat.game.features.modal.styles
  (:require
   [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.modal-viewport
   [:&.active :&.opening
    [:.modal
     {:opacity "1.0"
      :transform "translateY(0)"}]
    [:.modal-overlay
     {:opacity "1.0"}]]
   ["&.closing"
    [:.modal
     {:transform "translateY(100px)"}]]]
  [:.modal-container
   {:position :fixed
    :top 0
    :left 0
    :bottom 0
    :right 0
    :pointer-event "none"}]
  [:.modal-overlay
   {:position "absolute"
    :top 0
    :left 0
    :bottom 0
    :right 0
    :background "rgba(0, 0, 0, 0.8)"
    :opacity "0"
    :transition "opacity 500ms ease-out"}]
  [:.modal
   {:position "relative"
    :top "10%"
    :background "var(--med)"
    :width "90%"
    :max-width "800px"
    :margin "auto"
    :transform "translateY(-100px)"
    :opacity "0"
    :transition "transform 500ms ease-in, opacity 500ms ease-out"}]
  [:.modal-close
   {:position "absolute"
    :top "0.6rem"
    :right "0.6rem"
    :width "2.0rem"
    :height "2.0rem"
    :border "none"
    :font-size "0.8rem"
    :padding "0"
    :margin "0"
    :border-radius "50%"
    :text-align "center"
    :background "var(--bg)"
    :color "var(--accent)"}]
  [:.modal-title
   {:margin "0"
    :padding "0.5rem"
    :background "var(--accent)"
    :border-top-left-radius "0.5rem"
    :border-top-right-radius "0.5rem"
    :color "var(--bg)"}]
  [:.modal-content
   {:padding "2rem"}])
