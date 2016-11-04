(ns ctia-ui.pages.judgement-table
  (:require
    [ctia-ui.components :refer [EntityTablePage
                                EntityTable
                                JudgementReasonCell
                                ObservableCell
                                TextCell]]
    [ctia-ui.config :refer [config]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [neutralize-event vec-remove]]
    [oakmac.util :refer [atom-logger by-id fetch-json-as-clj js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Page ID
;;------------------------------------------------------------------------------

;; TODO: we might want to handle this at the routing level and put the page-id
;;       on app-state
(def page-id
  "Give every instance of this page a random id."
  (atom nil))

;;------------------------------------------------------------------------------
;; Data Fetching
;;------------------------------------------------------------------------------

;; NOTE: these functions should probably be in their own namespace

(defn- judgements-url []
  (if (:use-demo-data? config)
    "data/fake-judgements.json"
    "TODO: wire this up to beta tenzin"))

(defn- fetch-judgements [next-fn]
  (fetch-json-as-clj (judgements-url) next-fn))

(defn- fetch-judgements-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id @page-id)
    (swap! app-state update-in [:judgement-table] merge
      {:loading? false
       :data new-data})))

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
  {:cols cols
   :data []
   :expanded-cmp ExampleExpandedRow
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
    (reset! page-id new-page-id)
    (fetch-judgements (partial fetch-judgements-success new-page-id))
    (swap! app-state assoc :page :judgement-table
                           :judgement-table initial-page-state)))

(defn destroy-judgement-table-page! []
  (swap! app-state dissoc :page :judgement-table)
  (reset! page-id nil))
