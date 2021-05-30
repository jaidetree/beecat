(ns beecat.game.machine
  (:require
   [clojure.string :as s]
   [framework.reactor :refer [of-type pluck]]
   [framework.fsm :as fsm]
   [beecat.game.store :as store]))

(defn shuffle-machine
  [{:keys [letters on-shuffle on-done]}]
  (fsm/create
   {:id "shuffle"
    :initial :init
    :context {:timer nil
              :letters letters}
    :states
    {:init
     {:<-
      (fn [context _event send]
        (merge context
               {:timer
                (js/setTimeout
                 #(send :hide nil)
                 0)}))

      :hide
      (fn [context _event send]
        (js/requestAnimationFrame
         identity)
        [:hidden (merge context
                        {:timer (js/setTimeout
                                 #(send :shuffle nil)
                                 500)})])}

     :hidden
     {:shuffle
      (fn [context _event _send]
        (on-shuffle (shuffle (:letters context)))
        [:visible (merge context
                         {:timer (js/setTimeout
                                  on-done
                                  500)})])}}}))
(defn msg-machine
  [{:keys [send-parent]}]
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
                           #(send-parent :ready nil)
                           250))])}}}))

(defn completed?
  [answers words]
  (>= (/ (count words) (count answers)) 0.75))

(defn score-word
  [pangrams word]
  (let [length (count word)]
    (cond
      (contains? pangrams word)
      [(+ 7 length) "Whoa! Look at you"]

      (> length 8)
      [length       "wow"]

      (> length 6)
      [length       "Super"]

      (> length 4)
      [length       "Good find"]

      :else
      [1            "Nice"])))

(defn validate-word
  [{:keys [answers words required-letter word]}]
  (cond
    (empty? word)
    [false "No ❤"]

    (<= (count word) 3)
    [false "Too short! Entries must be more than 3 letters"]

    (not (s/includes? word required-letter))
    [false (str "Missing letter " required-letter)]

    (contains? words word)
    [false "You already found this word remember?"]

    (not (contains? answers word))
    [false "Hmph! Not on the words list"]

    (contains? answers word)
    [true nil]

    :else
    [true   "No idea what this is"]))

(defn on-init
  [context event send]
  (store/dispatch
   {:type :request/pending
    :payload {:name :state
              :url "/api/words.clj"
              :method "get"}})
  (assoc context
         :unubscribe (-> store/actions
                         (of-type :machine/send)
                         (pluck [:payload])
                         (.onValue
                          (fn [[event arg]]
                            (send event arg))))))

(defn loading->ready
  [context response _send]
  (let [{:keys [validLetters outerLetters centerLetter]} response]
    [:ready (assoc context
                   :letters (set validLetters)
                   :outer-letters (set outerLetters)
                   :required-letter centerLetter)]))

(defn on-letter
  [context letter _send]
  (let [letter (s/lower-case letter)]
    (when (contains? (:letters context) letter)
      (assoc context :word (str (:word context) letter)))))

(defn ready->on-shuffle
  [context _event send]
  [:shuffle (merge context
                   {:machine (shuffle-machine
                              {:letters (:outer-letters context)
                               :on-shuffle #(send :update %)
                               :on-done    #(send :ready nil)})})])

(defn shuffle->on-update
  [context letters _send]
  (assoc context :outer-letters letters))

(defn shuffle->on-ready
  [context _event _send]
  (fsm/destroy (:machine context))
  [:ready (dissoc context :machine)])

(defn on-delete
  [context _event _send]
  (let [word (get context :word)]
    (assoc context :word (subs word 0 (dec (count word))))))

(defn on-submit
  [context _event send]
  (let [{:keys [required-letter word]} context
        {:keys [answers words]} @store/state
        [is-valid msg] (validate-word {:answers         answers
                                       :words           words
                                       :required-letter required-letter
                                       :word            word})
        machine (msg-machine {:send-parent send})
        context (assoc context :machine machine)]
    (if is-valid
      [:accepted context]
      [:rejected (assoc context
                        :message {:type :error
                                  :message msg})])))

(comment
  @store/state
  (get-in @store/state [:answers])
  (get-in @store/state [:words])
  (completed? (:answers @store/state) nil))

(defn on-accepted
  [context _word send]
  (let [word (get context :word)
        pangrams (get @store/state :pangrams)
        [score msg] (score-word pangrams word)]
    (store/dispatch
     {:type :words/append
      :payload word})
    (assoc context
           :score score
           :message {:type :success
                     :message msg})))

(defn accepted->on-ready
  [context _event _send]
  (let [{:keys [answers words]} @store/state
        context (assoc context
                       :word ""
                       :message ""
                       :score nil)]
    (if (completed? answers words)
      [:completed context]
      [:ready context])))

(defn on-rejected-letter
  [context letter _send]
  [:ready (assoc context
                 :word letter)])

(defn *->ready
  [context _event _send]
  [:ready (assoc context
                 :message nil
                 :word "")])

(def game-machine
  (fsm/create
   {:id      :game
    :initial :loading
    :context {:word ""
              :score nil}
    :states
    {:loading
     {:init  on-init
      :ready loading->ready}

     :ready
     {:letter on-letter
      :submit on-submit
      :delete on-delete
      :shuffle ready->on-shuffle}

     :accepted
     {:<- on-accepted
      :submit on-submit
      #_#_:letter on-letter
      #_#_:delete on-delete
      :ready  accepted->on-ready}

     :rejected
     {#_#_:letter on-rejected-letter
      :ready  *->ready}

     :shuffle
     {:update shuffle->on-update
      :ready shuffle->on-ready}

     :completed
     {}}}))

(comment
  (conj #{} "taffy")
  (get @store/state :words)
  (get-in @game-machine [:context]))
