(ns ctia-ui.pages.judgement-table
  (:require
    [ctia-ui.components :refer [EntityTablePage
                                JudgementReasonCell
                                ObservableCell]]
    [ctia-ui.config :refer [config]]
    [ctia-ui.state :refer [app-state]]
    [oakmac.util :refer [atom-logger by-id fetch-json-as-clj js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Data Fetching
;;------------------------------------------------------------------------------

;; NOTE: these functions should probably be in their own namespace

(defn- judgements-url []
  (if (:use-demo-data? config)
    "data/fake-judgements.json?_slow=true"
    "TODO: wire this up to beta tenzin"))

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

(rum/defc ExampleExpandedRow < rum/static
  [row]
  [:div {:style {:font-size "16px"
                 :padding "20px 0"}}
    "TODO: example expanded row component goes here"])

(def initial-page-state
  {:ajax-error? false
   :cols cols
   :data []
   :entity-name "Judgements"
   :expanded-row-cmp ExampleExpandedRow
   :expanded-rows #{}
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
