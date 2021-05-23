(ns hippo.framework.forms)

(defn submit?
  [form-name action]
  (and (= (:type action) :forms/submit)
       (= (get-in action [:payload :form]) form-name)))

(defn on-submit
  [actions form-name]
  (-> actions
      (.filter #(submit? form-name %))))

(defn submit
  [form-name form-data]
  {:type :forms/submit
   :payload {:form form-name
             :form-data form-data}})


(defn form-update
  [form-name field value]
  {:type :forms/update
   :payload {:form form-name
             :field field
             :value value}})

(defn clear-form
  [form-name]
  {:type :forms/clear
   :payload form-name})
