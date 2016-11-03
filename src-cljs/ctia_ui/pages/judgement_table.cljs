(ns ctia-ui.pages.judgement-table
  (:require
    [ctia-ui.components :refer [EntityTablePage
                                EntityTable
                                ObservableCell
                                TextCell]]
    [ctia-ui.config :refer [config]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [neutralize-event vec-remove]]
    [oakmac.util :refer [atom-logger by-id fetch-json-as-clj js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

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

(defn- fetch-judgements-success [new-data]
  (swap! app-state update-in [:judgement-table] merge
    {:loading? false
     :data new-data}))

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(rum/defc ExampleCell < rum/static
  []
  [:div "Example Cell"])

(rum/defc ExampleHeaderComponent < rum/static
  []
  [:th "Example th"])

(def cols
  [{:th "Judgement"
    :td ExampleCell}
   {:th "Observable"
    :td ExampleCell}
   {:th "Disposition"
    :td :disposition}
   {:th "Confidence"
    :td :confidence}
   {:th "Severity"
    :td :severity}])

(def initial-page-state
  {:cols cols
   :data []
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
  (fetch-judgements fetch-judgements-success)
  (swap! app-state assoc :page :judgement-table
                         :judgement-table initial-page-state))

(defn destroy-judgement-table-page! []
  (swap! app-state dissoc :page :judgement-table))
