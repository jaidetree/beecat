(ns beecat.game.features.letters.views
  (:require
   [reagent.core :as r]
   [framework.fsm :as fsm]
   [beecat.game.machine :refer [game-machine]]
   [beecat.game.features.letters.styles :refer [style]]))

(defn msg-machine
  []
  (fsm/create
   {:initial :init
    :context {:top -10}
    :states
    {:init
     {:<-
      (fn [context _event send]
        {:timer
         (js/setTimeout
          #(send :enter nil)
          0)})
      :enter
      (fn [context _event send]
        [:active (assoc
                  context
                  :timer (js/setTimeout
                          #(send :close nil)
                          1500))])}
     :active
     {:close
      (fn [context _event _send]
        (js/clearTimeout (get context :timer))
        [:closing (assoc
                   context
                   :timer (js/setTimeout
                           #(fsm/send game-machine :ready nil)
                           250))])}}}))

(defn rejected
  [{:keys [message]}]
  (r/with-let [m (msg-machine)]
    (let [state (get @m :state)]
      [:div
       {:class (r/class-names
                (:message style)
                (:rejected style)
                (when (= state :active)
                  "active"))}
       (:message message)])
    (finally
      (fsm/destroy m))))

(defn accepted
  [{:keys [message]}]
  (r/with-let [m (msg-machine)]
    (let [state (get @m :state)]
      [:div
       {:class (r/class-names
                (:message style)
                (:accepted style)
                (when (= state :active)
                  "active"))}
       (:message message)])
    (finally
      (fsm/destroy m))))

(defn word
  []
  (let [{:keys [state context]} @game-machine
        {:keys [message required-letter word]} context]
    [:div
     {:class (:word style)}
     (when (= state :rejected)
       [rejected {:message message}])
     (when (= state :accepted)
       [accepted {:message message}])
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
  [{:keys [state]}]
  (case state
    :hidden "fade-out"
    :visible "fade-in"
    nil))

(defn honeycombs
  []
  (let [state (get @game-machine :state)
        {:keys [outer-letters required-letter transition]} (get @game-machine :context)
        transition (when transition @transition)]
    [:div
     {:class (r/class-names
              (:honeycombs style))}
     (for [letter outer-letters]
       [honeycomb
        {:key letter
         :letter letter
         :class-names [(when (= state :shuffle)
                         (shuffle-transition transition))]}])
     [honeycomb
      {:letter required-letter
       :is-center true}]]))

(defn actions
  []
  [:div
   {:class (:actions actions)}
   [:button
    {:type :button
     :on-click #(fsm/send game-machine :delete %)}
    "Delete"]
   [:button
    {:type :button
     :on-click #(fsm/send game-machine :shuffle %)}
    "Shuffle"]
   [:button
    {:type :button
     :on-click #(fsm/send game-machine :submit nil)}
    "Enter"]])
