(ns ctia-ui.pages.judgement-table
  (:require
    [ctia-ui.components :refer [EntityTablePage
                                JudgementReasonCell
                                ObservableCell]]
    [ctia-ui.config :refer [config]]
    [ctia-ui.data :refer [tenzin-base-url]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [encode-uri]]
    [oakmac.util :refer [atom-logger by-id fetch-json-as-clj js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Data Fetching
;;------------------------------------------------------------------------------

;; NOTE: these functions should probably be in their own namespace

(defn- judgements-url
  ([] (judgements-url ""))
  ([query-str]
   (if (:use-demo-data? config)
     "data/fake-judgements.json?_slow=true"
     (str tenzin-base-url "ctia/judgement/search?query=" (encode-uri query-str)))))

(defn- fetch-judgements-error [request-page-id]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:judgement-table] merge
      {:ajax-error? true
       :loading? false})))

(defn- fetch-judgements-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:judgement-table] merge
      {:loading? false
       :data new-data})))

(defn- fetch-judgements [next-fn error-fn]
  (fetch-json-as-clj (judgements-url) next-fn error-fn))

;;------------------------------------------------------------------------------
;; Page Components
;;------------------------------------------------------------------------------

(rum/defc JudgementLeftCol < rum/static
  [judgement]
  [:div.left-4a390
    [:div.map-entry-wrapper-0def3
      [:table.map-entry-e11e5
        [:tbody
          [:tr
            [:th "Disposition Name"]
            [:td "Content"]]
          [:tr
            [:th "Disposition"]
            [:td "Content"]]
          [:tr
            [:th "Observable"]
            [:td "Content"]]
          [:tr
            [:th "Source"]
            [:td "Content"]]
          [:tr
            [:th "Confidence"]
            [:td "Content"]]
          [:tr
            [:th "Severity"]
            [:td "Content"]]
          [:tr
            [:th "Priority"]
            [:td "Content"]]
          [:tr
            [:th "Type"]
            [:td "Content"]]
          [:tr
            [:th "ID"]
            [:td "Content"]]
          [:tr
            [:th "Valid Time"]
            [:td "Content"]]
          [:tr
            [:th "Schema Version"]
            [:td "Content"]]]]]])

(rum/defc JudgementRightCol < rum/static
  [judgement]
  [:div.right-03e80
    [:div.map-entry-wrapper-0def3
      [:table.map-entry-e11e5
        [:tbody
          [:tr
            [:th "External IDs"]
            [:td "Content"]]
          [:tr
            [:th "Indicators"]
            [:td "Content"]]
          [:tr
            [:th "Language"]
            [:td "Content"]]
          [:tr
            [:th "Reason"]
            [:td "Content"]]
          [:tr
            [:th "Reason URI"]
            [:td "Content"]]
          [:tr
            [:th "Revision"]
            [:td "Content"]]
          [:tr
            [:th "Source URI"]
            [:td "Content"]]
          [:tr
            [:th "Timestamp"]
            [:td "Content"]]
          [:tr
            [:th "TLP"]
            [:td "Content"]]
          [:tr
            [:th "URI"]
            [:td "Content"]]]]]])

(rum/defc JudgementExpandedRow < rum/static
  [judgement]
  [:div.details-wrapper-819a2
    (JudgementLeftCol judgement)
    (JudgementRightCol judgement)])

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(def cols
  [{:th "Judgement"
    :td JudgementReasonCell}
   {:th "Observable"
    :td ObservableCell}
   {:th "Confidence"
    :td :confidence}
   {:th "Severity"
    :td :severity}])

(def initial-page-state
  {:ajax-error? false
   :cols cols
   :data []
   :entity-name "Judgements"
   :expanded-row-cmp JudgementExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
   :loading? true
   :search-txt ""})

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Judgements")

(rum/defc JudgementTablePage < rum/static
  [state]
  (EntityTablePage state left-nav-tab :judgement-table))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-judgement-table-page! []
  (let [new-page-id (str (random-uuid))]
    (fetch-judgements (partial fetch-judgements-success new-page-id)
                      (partial fetch-judgements-error new-page-id))
    (swap! app-state assoc :page :judgement-table
                           :page-id new-page-id
                           :judgement-table initial-page-state)))

(defn destroy-judgement-table-page! []
  (swap! app-state dissoc :page :page-id :judgement-table))
