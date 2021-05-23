(ns beecat.game.machine
  (:require
   [clojure.string :as s]
   [framework.reactor :refer [of-type pluck]]
   [framework.fsm :as fsm]
   [beecat.game.store :as store]))

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

(def ranks
  {0.8  "Super Genius"
   0.7  "Genius"
   0.5  "Amazing"
   0.4  "Great"
   0.25 "Nice"
   0.14 "Solid"
   0.08 "Good"
   0.05 "Moving Up"
   0.02 "Good Start"
   0.00 "Beginner"})

(defn rank-score
  [answers words]
  (let [total (count answers)
        found (count words)
        percent (/ found total)]
    (->> ranks
         (keep
          (fn [[min rank]]
            (when (>= percent min)
              rank)))
         (first))))

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
  (let [{:keys [validLetters outerLetters centerLetter answers
                pangrams]} response
        pangrams (set pangrams)]
    [:ready (assoc context
                   :letters (set validLetters)
                   :outer-letters (set outerLetters)
                   :required-letter centerLetter)]))

(defn on-letter
  [context letter _send]
  (let [letter (s/lower-case letter)]
    (when (contains? (:letters context) letter)
      (assoc context :word (str (:word context) letter)))))

(defn on-shuffle
  [context _event _send]
  (assoc context :outer-letters (shuffle (:outer-letters context))))

(defn on-delete
  [context _event _send]
  (let [word (get context :word)]
    (assoc context :word (subs word 0 (dec (count word))))))

(defn on-submit
  [context _event _send]
  (let [{:keys [required-letter word]} context
        {:keys [answers words]} @store/state
        [is-valid msg] (validate-word {:answers         answers
                                       :words           words
                                       :required-letter required-letter
                                       :word            word})]
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
              :score 0}
    :states
    {:loading
     {:init  on-init
      :ready loading->ready}

     :ready
     {:letter on-letter
      :submit on-submit
      :delete on-delete
      :shuffle on-shuffle}

     :accepted
     {:<- on-accepted
      :submit on-submit
      #_#_:letter on-letter
      :delete on-delete
      :shuffle on-shuffle
      :ready  accepted->on-ready}

     :rejected
     {#_#_:letter on-rejected-letter
      :ready  *->ready}

     40 +
     :completed
     {}}}))

(comment
  (get @store/state :words)
  (get-in @game-machine [:context :word]))
