(ns beecat.game.features.letters.views
  (:require
   [reagent.core :as r]
   [framework.fsm :as fsm]
   [beecat.game.machine :refer [game-machine]]
   [beecat.game.features.letters.styles :refer [style]]))

(defn rejected
  [{:keys [message machine]}]
  (let [state (get @machine :state)]
    [:div
     {:class (r/class-names
              (:message style)
              (:rejected style)
              (when (= state :active)
                "active"))}
     (:message message)]))

(defn accepted
  [{:keys [message machine]}]
  (let [state (get @machine :state)]
    [:div
     {:class (r/class-names
              (:message style)
              (:accepted style)
              (when (= state :active)
                "active"))}
     (:message message)]))

(defn word
  []
  (let [{:keys [state context]} @game-machine
        {:keys [message required-letter word machine]} context]
    [:div
     {:class (:word style)}
     (when (= state :rejected)
       [rejected {:message message
                  :machine machine}])
     (when (= state :accepted)
       [accepted {:message message
                  :machine machine}])
     [:div.input
      {:class (case state
                :rejected "shake"
                "")}
      [:div
       {:class (r/class-names
                "letters"
                (when (= state :accepted)
                  "ascend"))}
       (for [[i letter] (map-indexed vector word)]
         [:span
          {:key i
           :class (r/class-names
                   (:letter style)
                   (when (= letter required-letter) "required"))}
          letter])]]]))

(defn honeycomb
  [{:keys [letter is-center class-names]}]
  [:svg
   {:class (r/class-names
            (:honeycomb style)
            (if is-center
              (:centercomb style)
              "outer"))
    :view-box "0 0 120 106"
    :on-click #(fsm/send game-machine :letter letter)}
   [:polygon.fill
    {:points "0,50 30,0 90,0 120,50 90,100 30,100"}]
   [:text.letter
    {:class (r/class-names class-names)
     :x "50%"
     :y "50%"
     :dy "4%"}
    letter]])

(defn shuffle-transition
  [machine]
  (let [{:keys [state]} @machine]
    (case state
      :hidden "fade-out"
      :visible "fade-in"
      nil)))

(defn honeycombs
  []
  (let [{:keys [state context]} @game-machine
        {:keys [outer-letters required-letter machine]} context]
    [:div
     {:class (r/class-names
              "honeycombs"
              (:honeycombs style))}
     (doall
      (for [letter outer-letters]
        [honeycomb
         {:key letter
          :letter letter
          :class-names [(when (= state :shuffle)
                          (shuffle-transition machine))]}]))
     [honeycomb
      {:letter required-letter
       :is-center true}]]))

(defn actions
  []
  [:div
   {:class (r/class-names "actions" (:actions style))}
   [:button
    {:type :button
     :on-click #(fsm/send game-machine :delete %)}
    [:i.fal.fa-backspace]
    " Delete"]
   [:button
    {:type :button
     :on-click #(fsm/send game-machine :shuffle %)
     :title "Shuffle"}
    [:i.fal.fa-sync]]
   [:button
    {:type :button
     :on-click #(fsm/send game-machine :submit nil)}
    "Enter "
    [:i.fal.fa-share]]])
