(ns beecat.game.features.rank.views
  (:require
   [reagent.core :as r]
   [framework.stream :as stream]
   [beecat.game.store :as store]
   [beecat.game.features.modal.machine :as modal]
   [beecat.game.features.modal.views :as modal-ui]
   [beecat.game.machine :refer [game-machine score-word]]
   [beecat.game.features.rank.styles :refer [style]]))

(def ranks
  [[1.0  "Ultimate Genius"]
   [0.8  "Super Genius"]
   [0.7  "Genius"]
   [0.5  "Amazing"]
   [0.4  "Great"]
   [0.25 "Nice"]
   [0.14 "Solid"]
   [0.08 "Good"]
   [0.05 "Moving Up"]
   [0.02 "Good Start"]
   [0.00 "Beginner"]])

(defn score->rank
  [percent]
  (->> ranks
       (keep
        (fn [[min rank]]
          (when (>= percent min)
            rank)))
       (first)))

(defn calc-points
  [pangrams words]
  (->> words
       (map (fn [word]
              (let [[points _] (score-word pangrams word)]
                points)))
       (reduce + 0)))

(defn ranks-modal
  []
  (let [total (count (get-in @store/state [:answers]))]
    (js/console.log "total:" total)
    [:div
     {:class (:ranks-modal style)}
     [modal-ui/title "Rankings"]
     [modal-ui/content
      [:ul.list
       [:li.item
        [:span.rank.label
         "Rank"]
        [:span.words.label
         "Words"]]
       (for [[percent label] (reverse ranks)]
         [:li.item
          {:key label}
          [:span.rank
           label]
          [:span.words
           (js/Math.ceil (* percent total))]])]]]))

(defn complete-modal
  []
  [:div
   {:class (:complete-modal style)}
   [modal-ui/title "Bravo"]
   [modal-ui/content
    [:h1.greeting "You Did It!"]
    [:i.fas.fa-trophy-alt]
    [:p
     "Wow! You found 40% of all possible words. Your persistence is admirable but you can keep going!"]]])

(defn complete-stream
  []
  (-> (stream/from-atom game-machine)
      (.map #(get-in % [:context :progress]))
      (.map score->rank)
      (.skipDuplicates)
      (.filter #(= % "Great"))
      (.onValue
       (fn []
         (modal/open complete-modal)))))

(defn score
  []
  (let [{:keys [context]} @game-machine
        {:keys [score machine]} context]
    [:span
     {:class (:score style)}
     (when score
       (let [{:keys [state]} @machine]
         [:span.points
          {:class (case state
                    :active "active"
                    :closing "active closing"
                    "")}
          (str "+ " score)]))]))

(defn label
  [{:keys [rank points]}]
  [:span
   {:class (:label style)}
   [:button
    {:type "button"
     :on-click #(modal/open ranks-modal)}
    (str rank " ( " points " )")]
   [score]])

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
  (r/with-let [unsub (complete-stream)]
    (let [{:keys [pangrams answers words]} @store/state
          percent (get-in @game-machine [:context :progress])
          points (calc-points pangrams words)
          rank (score->rank percent)]
      [:div
       {:class (:rank style)}
       [label
        {:rank rank
         :score percent
         :points points}]
       [progress
        {:rank rank
         :score percent}]])
    (finally
      (unsub))))

(comment
  (get-in @game-machine [:context :score]))
