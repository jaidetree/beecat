(ns beecat.game.features.modal.views
  (:require
   [goog.string :as gstr]
   [reagent.core :as r]
   [beecat.game.features.modal.machine :as modal :refer [modal-machine]]
   [beecat.game.features.modal.styles :refer [style]]))

(defn class-name
  [state context]
  (case state
    :idle          "idle"
    :start-opening "open"
    :opening       "open opening"
    :open          "open active"
    :closing       "open closing"))

(defn title
  [& children]
  (into
   [:h1
    {:class (:modal-title style)}]
   children))

(defn content
  [& children]
  (into
   [:div
    {:class (:modal-content style)}]
   children))

(defn close-modal-on-escape
  [e]
  (js/console.log e)
  (let [key (.. e -key)]
    (when (= key "Escape")
      (modal/close))))

(defn modal-container
  [& children]
  (r/with-let [_ (js/window.addEventListener "keydown" close-modal-on-escape)]
    (into [:div {:class (:modal-container style)}] children)
    (finally
      (js/window.removeEventListener "keydown" close-modal-on-escape))))

(defn modal-viewport
  []
  (let [{:keys [state context]} @modal-machine
        {:keys [view]} context]
    [:div
     {:class (r/class-names
              (class-name state context)
              (:modal-viewport style))}
     (when view
       [modal-container
        [:div
         {:class (r/class-names (:modal-overlay style)
                                "modal-overlay")}]
        [:div
         {:class (r/class-names
                  (:modal style)
                  "modal")}
         [:button
          {:class (:modal-close style)
           :on-click modal/close}
          (gstr/unescapeEntities "&#x2715;")]
         [:div
          {:class (:modal-body style)}
          [view]]]])]))
