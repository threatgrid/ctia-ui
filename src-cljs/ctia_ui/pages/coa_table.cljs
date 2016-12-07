(ns ctia-ui.pages.coa-table
  (:require
    [clojure.string :refer [blank? capitalize lower-case]]
    [ctia-ui.components :refer [ConfidenceCell
                                EntityTablePage]]
    [ctia-ui.config :refer [config]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [encode-uri]]
    [oakmac.util :refer [atom-logger by-id fetch-json-as-clj js-log log]]
    [rum.core :as rum]))

;;------------------------------------------------------------------------------
;; Data Fetching
;;------------------------------------------------------------------------------

;; NOTE: these functions should probably be in their own namespace

(defn- coas-url
  ([] (coas-url ""))
  ([query-str]
   (if (:in-demo-mode? config)
     "data/fake-coas.json?_slow=true"
     (str (:api-base-url config) "ctia/coa/search?query=*" (encode-uri query-str)))))

(defn- fetch-coas-error [request-page-id]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:coa-table] merge
      {:ajax-error? true
       :loading? false})))

(defn- fetch-coas-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:coa-table] merge
      {:loading? false
       :data new-data})))

(defn- fetch-coas [next-fn error-fn]
  (fetch-json-as-clj (coas-url) next-fn error-fn))

;;------------------------------------------------------------------------------
;; Page Components
;;------------------------------------------------------------------------------

(rum/defc COAExpandedRow < rum/static
  [coa]
  [:div.details-wrapper-819a2
    "TODO: COA expanded row goes here"])

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

;; NOTE: just guessing at these columns for now
;; more discussion: https://github.com/threatgrid/ctia-ui/issues/21

(def cols
  [{:th "Description"
    :td :description}
   {:th "Motivation"
    :td :motivation}
   {:th "Sophistication"
    :td :sophistication}
   {:th "Source"
    :td :source}
   {:th "Sev."
    :td :severity}
   {:th "Conf."
    :td ConfidenceCell}
   {:th "Last Modified"
    :td :modified}])

(def initial-page-state
  {:ajax-error? false
   :cols cols
   :data []
   :entity-name "COAs"
   :expanded-row-cmp COAExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
   :loading? true
   :search-txt ""})

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "COAs")

(rum/defc COATablePage < rum/static
  [state]
  (EntityTablePage state left-nav-tab :coa-table))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-coa-table-page! []
  (let [new-page-id (str (random-uuid))]
    (fetch-coas (partial fetch-coas-success new-page-id)
                (partial fetch-coas-error new-page-id))
    (swap! app-state assoc :page :coa-table
                           :page-id new-page-id
                           :coa-table initial-page-state)))

(defn destroy-coa-table-page! []
  (swap! app-state dissoc :page :page-id :coa-table))
