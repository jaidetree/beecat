(ns framework.fsm
  (:refer-clojure :exclude [atom])
  (:require
   [clojure.string :as s]
   [framework.stream :refer [Bus]]
   [reagent.core :refer [atom]]))

(defonce fsms (atom {}))

(defn resolve-fsm-state
  "
  Resolves a transition result into a vector of [state context]
  Takes state, context, and a result
  Returns [state context] vector
  "
  [state context result]
  (cond
    ;; Result was a vector of [state context]
    (and (sequential? result) (= (count result) 2))
    result

    ;; Only context was returned
    (map? result)
    [state result]

    ;; No changes to state or context, typically only side-effects
    :else
    [state context]))

(defn exit-transition
  "
  Calls the exit transition hook for the current state:
  {:loading {:-> (fn [] args)}}

  Takes states map, prev state, next state, current context, event arg, and a
  send function scoped to current fsm

  Returns updated context
  "
  [states prev next context arg send]
  (let [f (get-in states [prev :->])]
    (or (and f (f context arg send))
        context)))

(defn enter-transition
  "
  Calls the enter transition hook for the current state:
  {:loading {:<- (fn [] args)}}

  Takes states map, prev state, next state, current context, event arg, and a
  send function scoped to current fsm

  Returns updated context
  "
  [states prev next context arg send]
  (let [f (get-in states [next :<-])]
    (or (and f (f context arg send))
        context)))

(defn transition
  "
  Perform a transition between two states and calls any enter :<- or :-> exit
  hooks as specified by the current and next state

  Takes an fsm hash-map derefed from an atom, a transition function, an event
  arg, and a wrapped send function to queue more transitions

  Returns updated fsm state
  "
  [{:keys [state context states] :as fsm-state} f arg send]
  (let [result (f context arg send)
        [next context] (resolve-fsm-state state context result)
        _ (println "trans" state "->" next context)
        context (exit-transition states state next context arg send)
        context (enter-transition states state next context arg send)]
    (assoc fsm-state
           :state next
           :context context)))

(defn send
  "
  Send an event to an fsm
  Takes an fsm atom, event name keyword, and any arg
  Returns updated context if event is supported within current state
  "
  [fsm event arg]
  (when-let [fsm-send (get @fsm :send)]
    (fsm-send event arg)))

(defn ^:private register-fsm
  "
  Registers the fsm subscription in the global registry
  Takes an id string and an fsm atom
  Causes side-effects to update fsms atom state
  "
  [id fsm]
  (let [{:keys [state states queue]} @fsm]
    (swap! fsms assoc id
           (-> queue
               (.onValue
                (fn [[event arg]]
                  (when-let [f (get-in states [(get @fsm :state) event])]
                    (swap! fsm transition f arg (partial send fsm)))))))))

(defn gen-id
  "
  Generate a random id 7 characters long

  Returns a string of 7 random characters
  "
  []
  (.toString
   (->> (range 8)
        (map #(rand-int 10))
        (s/join "")
        (js/Number))
   16))

(defn create
  "
  Creates an fsm machine atom and its own queue stream

  Takes a hash-map with the following:
  :id      string   - Friendly name of fsm, should be unique
  :initial keyword  - Initial state must match a key in states
  :context *        - Initial context of fsm
  :states  hash-map - Maps current state keys to hash-map of events and actions

  The fsm actions can cause further transitions by calling the send function
  provided to each action and hook.

  Returns an fsm atom
  "
  [{:keys [id initial context states]}]
  (let [queue (Bus.)
        id (or id (gen-id))
        fsm (atom {:id id
                   :state initial
                   :context context
                   :states states
                   :queue queue
                   :send (fn [name arg]
                           (.push queue [name arg]))})]
    (register-fsm id fsm)
    (when (get-in @fsm [:states initial :<-])
      (swap! fsm assoc :context
             (enter-transition states nil initial context nil
                               (:send @fsm))))
    fsm))

(defn destroy-all
  []
  (doseq [[id unsubscribe] @fsms]
    (unsubscribe)))

(defn destroy
  [id-or-fsm]
  (let [id (if (keyword? id-or-fsm) id-or-fsm (get @id-or-fsm :id))]
    (when-let [unsubscribe (get @fsms id)]
      (unsubscribe))))

(comment

  (def test-fsm
    (create
     {:id :test
      :initial :loading
      :context {:color :blue :word ""}
      :states {:loading
               {:<-
                (fn [context event send]
                  (println "loading" context)
                  (assoc context :color :green))

                :ready
                (fn [context event send]
                  (println "ready" context)
                  [:ready (assoc context :color :red)])}

               :ready
               {:letter
                (fn [context letter send]
                  (println "letter" context)
                  (assoc context :word (str (:word context) letter)))}}}))

  (send test-fsm :ready "hello world")
  (send test-fsm :letter "p")
  @test-fsm)
