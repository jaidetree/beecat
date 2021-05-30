(ns beecat.game.features.rank.views
  (:require
   [beecat.game.store :as store]
   [beecat.game.machine :refer [game-machine]]
   [beecat.game.features.rank.styles :refer [style]]))

(def ranks
  [[0.8  "Super Genius"]
   [0.7  "Genius"]
   [0.5  "Amazing"]
   [0.4  "Great"]
   [0.25 "Nice"]
   [0.14 "Solid"]
   [0.08 "Good"]
   [0.05 "Moving Up"]
   [0.02 "Good Start"]
   [0.00 "Beginner"]])

(defn calc-score
  [answers words]
  (let [total (count answers)
        found (count words)]
    (/ found total)))

(defn score->rank
  [percent]
  (->> ranks
       (keep
        (fn [[min rank]]
          (when (>= percent min)
            rank)))
       (first)))

(defn label
  [{:keys [rank]}]
  [:span
   {:class (:label style)}
   rank])

(defn progress
  [{:keys [_rank score]}]
  [:div
   {:class (:progress style)}
   [:div.bar]
   [:div.milestones
    (for [[percent label] ranks]
      [:div.milestone
       {:key label
        :class (when (>= score percent)
                 "active")
        :style {:left (str (* percent 100) "%")}}])]
   [:div.cursor
    {:style {:width (str (* score 100) "%")}}]])

(defn rank
  []
  (let [{:keys [answers words]} @store/state
        score (calc-score answers words)
        rank (score->rank score)]
    [:div
     {:class (:rank style)}
     [label
      {:rank rank
       :score score}]
     [progress
      {:rank rank
       :score score}]]))

(defn score
  []
  (let [{:keys [context]} @game-machine
        {:keys [score machine]} context]
    [:section
     {:class (:score style)}
     (when score
       (let [{:keys [state]} @machine]
         [:span.points
          {:class (case state
                    :active "active"
                    :closing "active closing"
                    "")}
          (str "+ " score)]))]))

(comment
  (get-in @game-machine [:context :score]))
