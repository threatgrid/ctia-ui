(ns ctia-ui.pages.incident-table
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

(defn- incidents-url
  ([] (incidents-url ""))
  ([query-str]
   (if (:in-demo-mode? config)
     "data/fake-incidents.json?_slow=true"
     (str (:api-base-url config) "ctia/incident/search?query=*" (encode-uri query-str)))))

(defn- fetch-incidents-error [request-page-id]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:incident-table] merge
      {:ajax-error? true
       :loading? false})))

(defn- fetch-incidents-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:incident-table] merge
      {:loading? false
       :data new-data})))

(defn- fetch-incidents [next-fn error-fn]
  (fetch-json-as-clj (incidents-url) next-fn error-fn))

;;------------------------------------------------------------------------------
;; Page Components
;;------------------------------------------------------------------------------

(rum/defc IncidentExpandedRow < rum/static
  [incident]
  [:div.details-wrapper-819a2
    "TODO: Incident expanded row goes here"])

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

;; NOTE: just guessing at these columns for now
;; more discussion: https://github.com/threatgrid/ctia-ui/issues/26

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
   :entity-name "Incidents"
   :expanded-row-cmp IncidentExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
   :loading? true
   :search-txt ""})

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Incidents")

(rum/defc IncidentTablePage < rum/static
  [state]
  (EntityTablePage state left-nav-tab :incident-table))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-incident-table-page! []
  (let [new-page-id (str (random-uuid))]
    (fetch-incidents (partial fetch-incidents-success new-page-id)
                     (partial fetch-incidents-error new-page-id))
    (swap! app-state assoc :page :incident-table
                           :page-id new-page-id
                           :incident-table initial-page-state)))

(defn destroy-incident-table-page! []
  (swap! app-state dissoc :page :page-id :incident-table))
