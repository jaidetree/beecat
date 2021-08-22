(ns framework.reactor
  (:require
   [framework.stream :refer [merge-all]]))

(defn update-state
  [state-atom keys value]
  (swap! state-atom assoc-in keys value))

(defn reduce-state
  [state-ref reducer action]
  (swap! state-ref
         (fn reduce-state [state]
           (reducer state action))))

#_(defn reducer
    [action-type slice-key reducer]
    (fn single-reducer
      [state action]
      (if (= (:type action) action-type)
        (if (contains? state slice-key)
          (update state slice-key #(reducer % action))
          (assoc state slice-key #(reducer ::undefined action)))
        state)))

(def init {:type ::init :payload nil})

(defn assign-reducers
  [{:keys [initial reducers]}]
  (fn created-reducer
    [state action]
    (->> reducers
         (reduce
          (fn created-reduce
            [state [action-type reducer]]
            (cond
              (= action init)
              initial

              (= (:type action) action-type)
              (reducer (if (= state ::undefined) initial state)
                       (assoc-in action [:meta :initial] initial))

              :default
              state))
          state))))

(defn combine-reducers
  [reducer-slices]
  (fn slice-reducer
    [state action]
    (->> reducer-slices
         (reduce (fn slice-reduce
                   [state [slice-key reducer]]
                   (assoc state slice-key (reducer (get state slice-key ::undefined) action)))
                 state))))

(defn of-type?
  [action action-type]
  (= (:type action) action-type))

(defn of-type
  ([actions action-type]
   (-> actions
       (.filter #(of-type? % action-type))))
  ([actions action-type & action-types]
   (-> actions
       (.filter #(contains? (into #{action-type} action-types)
                            (:type %))))))

(defn compose-fx
  [fns]
  (fn fx-flat-map
    [actions deps]
    (-> (map #(% actions deps) fns)
        (merge-all)
        (.takeUntil (-> actions
                        (of-type :store/end)
                        (.doError println))))))

(defn pluck
  [actions paths]
  (let [paths (if (keyword? paths) [paths] paths)]
    (-> actions
        (.map #(get-in % paths)))))

(defn type->action
  [payload-stream type]
  (-> payload-stream
      (.map (fn [payload]
              {:type type
               :payload payload}))))

(def action* type->action)
