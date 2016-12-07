(ns ctia-ui.pages.campaign-table
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

(defn- campaigns-url
  ([] (campaigns-url ""))
  ([query-str]
   (if (:in-demo-mode? config)
     "data/fake-campaigns.json?_slow=true"
     (str (:api-base-url config) "ctia/campaign/search?query=*" (encode-uri query-str)))))

(defn- fetch-campaigns-error [request-page-id]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:campaign-table] merge
      {:ajax-error? true
       :loading? false})))

(defn- fetch-campaigns-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:campaign-table] merge
      {:loading? false
       :data new-data})))

(defn- fetch-campaigns [next-fn error-fn]
  (fetch-json-as-clj (campaigns-url) next-fn error-fn))

;;------------------------------------------------------------------------------
;; Page Components
;;------------------------------------------------------------------------------

(rum/defc CampaignExpandedRow < rum/static
  [a-campaign]
  [:div.details-wrapper-819a2
    "TODO: Campaign expanded row goes here"])

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
   :entity-name "Campaigns"
   :expanded-row-cmp CampaignExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
   :loading? true
   :search-txt ""})

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Campaigns")

(rum/defc CampaignTablePage < rum/static
  [state]
  (EntityTablePage state left-nav-tab :campaign-table))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-campaign-table-page! []
  (let [new-page-id (str (random-uuid))]
    (fetch-campaigns (partial fetch-campaigns-success new-page-id)
                  (partial fetch-campaigns-error new-page-id))
    (swap! app-state assoc :page :campaign-table
                           :page-id new-page-id
                           :campaign-table initial-page-state)))

(defn destroy-campaign-table-page! []
  (swap! app-state dissoc :page :page-id :campaign-table))
