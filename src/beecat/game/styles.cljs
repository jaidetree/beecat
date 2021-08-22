(ns beecat.game.styles
  (:require
   [clojure.string :as s]
   [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [:.head
   {:background "var(--accent)"
    :color "var(--bg)"
    :text-align "center"
    :font-family "var(--display-font)"}
   [:h1
    {:margin 0
     :padding 0}]]
  [:.main
   {:padding "1rem 1rem"}]
  [:.game
   {:display :flex
    :flex-flow "column nowrap"
    :align-items "center"}])

(defn num->hex-str
  [num]
  (let [hex (.toString num 16)
        hex (if (= (count hex) 1) (str "0" hex) hex)]
    (->> (repeat 3 hex)
         (s/join "")
         (str "#"))))

(defn black
  [percent]
  (->> percent
       (* 255)
       (- 255)
       (js/Math.round)
       (num->hex-str)))

(comment
  (->> [0.05 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9]
       (map black)))
