(ns ctia-ui.pages.indicator-table
  (:require
    [clojure.string :refer [blank? capitalize lower-case]]
    [ctia-ui.components :refer [ConfidenceCell
                                EntityTablePage
                                JudgementReasonCell
                                ObservableCell
                                TLPCell]]
    [ctia-ui.config :refer [config]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [encode-uri]]
    [oakmac.util :refer [atom-logger by-id fetch-json-as-clj js-log log]]
    [rum.core :as rum]))

;;------------------------------------------------------------------------------
;; Data Fetching
;;------------------------------------------------------------------------------

;; NOTE: these functions should probably be in their own namespace

(defn- indicators-url
  ([] (indicators-url ""))
  ([query-str]
   (if (:in-demo-mode? config)
     "data/fake-indicators.json?_slow=true"
     (str (:api-base-url config) "ctia/indicator/search?query=*" (encode-uri query-str)))))

(defn- fetch-indicators-error [request-page-id]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:indicator-table] merge
      {:ajax-error? true
       :loading? false})))

(defn- fetch-indicators-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:indicator-table] merge
      {:loading? false
       :data new-data})))

(defn- fetch-indicators [next-fn error-fn]
  (fetch-json-as-clj (indicators-url) next-fn error-fn))

;;------------------------------------------------------------------------------
;; Page Components
;;------------------------------------------------------------------------------

(rum/defc IndicatorExpandedRow < rum/static
  [an-indicator]
  [:div.details-wrapper-819a2
    "TODO: Indicator Expanded row goes here"])

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

;; NOTE: just guessing at these columns for now
;; more discussion: https://github.com/threatgrid/ctia-ui/issues/27
(def cols
  [{:th "Indicator"
    :td :description}
   {:th "Producer"
    :td :producer}
   {:th "Conf."
    :td ConfidenceCell}
   {:th "TLP"
    :td TLPCell}
   {:th "Last Modified"
    :td :modified}])

(def initial-page-state
  {:ajax-error? false
   :cols cols
   :data []
   :entity-name "Indicators"
   :expanded-row-cmp IndicatorExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
   :loading? true
   :search-txt ""})

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Indicators")

(rum/defc IndicatorTablePage < rum/static
  [state]
  (EntityTablePage state left-nav-tab :indicator-table))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-indicator-table-page! []
  (let [new-page-id (str (random-uuid))]
    (fetch-indicators (partial fetch-indicators-success new-page-id)
                      (partial fetch-indicators-error new-page-id))
    (swap! app-state assoc :page :indicator-table
                           :page-id new-page-id
                           :indicator-table initial-page-state)))

(defn destroy-indicator-table-page! []
  (swap! app-state dissoc :page :page-id :indicator-table))
