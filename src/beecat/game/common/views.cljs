(ns beecat.game.common.views)

(defn spinner
  [{:keys [width height padding color]}]
  [:span
   {:class "loader"
    :style {:padding padding}}
   [:span.spinner
    {:class (if (= color :dark)
              "dark"
              "light")
     :style {:width width
             :height height}}]])
