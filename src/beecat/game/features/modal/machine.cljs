(ns beecat.game.features.modal.machine
  (:require
   [framework.fsm :as fsm]))

(def modal-machine
  (fsm/create
   {:initial :idle
    :context {:active false
              :view nil
              :timer  nil}

    :states
    {:idle
     {:open
      (fn [context view send]
        [:start-opening
         (merge context
                {:view view
                 :timer (js/setTimeout #(send :start nil) 0)})])}

     :start-opening
     {:start
      (fn [context _event send]
        [:opening
         (merge context
                {:timer (js/setTimeout #(send :opened nil) 500)})])}

     :opening
     {:opened
      (fn [context _event _send]
        [:open (merge context
                      {:timer nil})])}

     :open
     {:close
      (fn [context _event send]
        [:closing
         (merge context
                {:timer (js/setTimeout #(send :closed nil) 500)})])}

     :closing
     {:closed
      (fn [context _event _send]
        [:idle (merge context
                      {:view nil
                       :timer nil})])}}}))

(defn close
  [& [_event & _args]]
  (fsm/send modal-machine :close nil))

(defn open
  [view]
  (fsm/send modal-machine :open view))
