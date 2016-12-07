(ns ctia-ui.pages.sighting-table
  (:require
    [clojure.string :refer [blank? capitalize lower-case]]
    [ctia-ui.components :refer [ConfidenceCell
                                EntityTablePage
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

(defn- sightings-url
  ([] (sightings-url ""))
  ([query-str]
   (if (:in-demo-mode? config)
     "data/fake-sightings.json?_slow=true"
     (str (:api-base-url config) "ctia/sighting/search?query=*" (encode-uri query-str)))))

(defn- fetch-sightings-error [request-page-id]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:sighting-table] merge
      {:ajax-error? true
       :loading? false})))

(defn- fetch-sightings-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:sighting-table] merge
      {:loading? false
       :data new-data})))

(defn- fetch-sightings [next-fn error-fn]
  (fetch-json-as-clj (sightings-url) next-fn error-fn))

;;------------------------------------------------------------------------------
;; Page Components
;;------------------------------------------------------------------------------

(rum/defc SightingExpandedRow < rum/static
  [a-sighting]
  [:div.details-wrapper-819a2
    "TODO: Sighting expanded row goes here"])

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

;; NOTE: just guessing at these columns for now
;; more discussion: https://github.com/threatgrid/ctia-ui/issues/30

(def cols
  [{:th "Description"
    :td :description}
   {:th "Observables"
    :td ObservableCell}
   {:th "Indicators"
    :td :source}
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
   :entity-name "Sightings"
   :expanded-row-cmp SightingExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
   :loading? true
   :search-txt ""})

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Sightings")

(rum/defc SightingTablePage < rum/static
  [state]
  (EntityTablePage state left-nav-tab :sighting-table))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-sighting-table-page! []
  (let [new-page-id (str (random-uuid))]
    (fetch-sightings (partial fetch-sightings-success new-page-id)
                  (partial fetch-sightings-error new-page-id))
    (swap! app-state assoc :page :sighting-table
                           :page-id new-page-id
                           :sighting-table initial-page-state)))

(defn destroy-sighting-table-page! []
  (swap! app-state dissoc :page :page-id :sighting-table))
