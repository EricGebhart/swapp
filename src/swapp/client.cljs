(ns swapp.client
  (:require
   [ajax.core :refer [POST GET]]
   [json-html.core :refer [edn->hiccup]]
   [reagent-forms.core :refer [bind-fields init-field value-of]]
   [clojure.walk :refer [keywordize-keys]]
   [reagent.core :as r :refer [atom]]))

(def reload)

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn radio [label name value]
  [:div.radio
   [:label
    [:input {:field :radio :name name :value value}]
    label]])

(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))

(defn clear-scramble-found [doc]
  (swap! doc assoc-in
         [:scramble :found]
         false))

(defn friend-source [text]
  (filter
   #(-> % (.toLowerCase %) (.indexOf text) (> -1))
   ["Alice" "Alan" "Bob" "Beth" "Jim" "Jane" "Kim" "Rob" "Zoe"]))

(defn scramble [doc]
  (fn []
    (if (empty? (get-in @doc [:scramble :src-str]))
      (swap! doc assoc-in [:errors :src-str]
             "scramble source string is empty")
      (GET "http://localhost:8080/s"
           {:params (:scramble @doc)
            ;;:response-format :json
            ;;:keywords? true
            :handler
            (fn [res]
              ;; I don't know why the middleware doesn't
              ;; take care of the keys.
              (let [res (keywordize-keys res)]
                ;;(js/console.log "wat" res)
                (swap! doc assoc-in
                       [:scramble :found]
                       (get res  :found))))}))))

(defn testget [doc]
  (GET "http://localhost:8080/test"
       {:params doc
        :response-format :json
        :keywords? true
        :handler #(js/console.log "wat" %)}))

(defn scramble-form [doc]
  (let [scramble-func (scramble doc)]
    [:div
     (input "scramble string" :text :scramble.src-str)
     [:div.row
      [:div.col-md-2]
      [:div.col-md-5
       [:div.alert.alert-danger
        {:field :alert :id :errors.src-str :event empty?}
        "Please enter a list of letters."]]]

     (input "scramble word" :text :scramble.word)
     [:div.row
      [:div.col-md-2]
      [:div.col-md-5
       [:div.alert.alert-success
        {:field :alert :id :scramble.word :event empty?}
        "Please enter a word to find in the scramble."]]]

     [:button.btn.btn-default
      {:on-click scramble-func}
      "Check for the word in the scramble"]]))



(defn form-template [doc]

  [:div

   (scramble-form doc)

   (input "first name" :text :person.first-name)
   [:div.row
    [:div.col-md-2]
    [:div.col-md-5
     [:div.alert.alert-danger
      {:field :alert :id :errors.first-name}]]]

   (input "last name" :text :person.last-name)
   [:div.row
    [:div.col-md-2]
    [:div.col-md-5
     [:div.alert.alert-success
      {:field :alert :id :person.last-name :event empty?}
      "last name is empty!"]]]

   [:div.row
    [:div.col-md-2 [:label "Age"]]
    [:div.col-md-5
     [:div
      [:label
       [:input
        {:field :datepicker :id :age :date-format "yyyy/mm/dd" :inline true}]]]]]

   (input "email" :email :person.email)
   (row
    "comments"
    [:textarea.form-control
     {:field :textarea :id :comments}])

   [:hr]
   (input "kg" :numeric :weight-kg)
   (input "lb" :numeric :weight-lb)

   [:hr]
   [:h3 "BMI Calculator"]
   (input "height" :numeric :height)
   (input "weight" :numeric :weight)
   (row "BMI"
        [:input.form-control
         {:field :numeric :fmt "%.2f" :id :bmi :disabled true}])
   [:hr]

   (row "Best friend"
        [:div {:field           :typeahead
               :id              :ta
               :data-source     friend-source
               :input-placeholder "Who's your best friend? You can pick only one"
               :input-class     "form-control"
               :list-class      "typeahead-list"
               :item-class      "typeahead-item"
               :highlight-class "highlighted"}])
   [:br]

   (row "isn't data binding lovely?"
        [:input {:field :checkbox :id :databinding.lovely}])
   [:label
    {:field :label
     :preamble "The level of awesome: "
     :placeholder "N/A"
     :id :awesomeness}]

   [:input {:field :range :min 1 :max 10 :id :awesomeness}]

   [:h3 "option list"]
   [:div.form-group
    [:label "pick an option"]
    [:select.form-control {:field :list :id :many.options}
     [:option {:key :foo} "foo"]
     [:option {:key :bar} "bar"]
     [:option {:key :baz} "baz"]]]

   (radio
    "Option one is this and that—be sure to include why it's great"
    :foo :a)
   (radio
    "Option two can be something else and selecting it will deselect option one"
    :foo :b)

   [:h3 "multi-select buttons"]
   [:div.btn-group {:field :multi-select :id :every.position}
    [:button.btn.btn-default {:key :left} "Left"]
    [:button.btn.btn-default {:key :middle} "Middle"]
    [:button.btn.btn-default {:key :right} "Right"]]

   [:h3 "single-select buttons"]
   [:div.btn-group {:field :single-select :id :unique.position}
    [:button.btn.btn-default {:key :left} "Left"]
    [:button.btn.btn-default {:key :middle} "Middle"]
    [:button.btn.btn-default {:key :right} "Right"]]

   [:h3 "single-select list"]
   [:div.list-group {:field :single-select :id :pick-one}
    [:div.list-group-item {:key :foo} "foo"]
    [:div.list-group-item {:key :bar} "bar"]
    [:div.list-group-item {:key :baz} "baz"]]

   [:h3 "multi-select list"]
   [:ul.list-group {:field :multi-select :id :pick-a-few}
    [:li.list-group-item {:key :foo} "foo"]
    [:li.list-group-item {:key :bar} "bar"]
    [:li.list-group-item {:key :baz} "baz"]]])

(defn page []
  (let [doc (atom {:person {:first-name "John"
                            :age 35
                            :email "foo@bar.baz"}
                   :scramble {:src-str nil
                              :word    nil
                              :found   false}
                   :weight 100
                   :height 200
                   :bmi 0.5
                   :comments "some interesting comments\non this subject"
                   :radioselection :b
                   :position [:left :right]
                   :pick-one :bar
                   :unique {:position :middle}
                   :pick-a-few [:bar :baz]
                   :many {:options :bar}})]
    (fn []
      [:div
       [:div.page-header [:h1 "Sample Form"]]

       [bind-fields
        (form-template doc)
        doc
        (fn [[id] value {:keys [weight-lb weight-kg] :as document}]
          (cond
            (= id :weight-lb)
            (assoc document :weight-kg (/ value 2.2046))
            (= id :weight-kg)
            (assoc document :weight-lb (* value 2.2046))
            :else nil))
        (fn [[id] value {:keys [height weight] :as document}]
          (when (and (some #{id} [:height :weight]) weight height)
            (assoc document :bmi (/ weight (* height height)))))]

       [:button.btn.btn-default
        {:on-click
         #(if (empty? (get-in @doc [:person :first-name]))
            (swap! doc assoc-in [:errors :first-name]"first name is empty"))}
        "save"]

       [:hr]
       [:h1 "Document State"]
       [edn->hiccup @doc]])))

#_(defn main-app-component
    []
    [:h1 "Hello, world!"])

#_(defn reload
    []
    (r/render-component [main-app-component] (.getElementById js/document "app")))

(defn reload
  []
  (r/render-component [page] (.getElementById js/document "app")))

(defn ^:export run
  []
  (enable-console-print!)
  (reload))