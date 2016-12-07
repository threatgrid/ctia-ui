(ns ctia-ui.pages.actor-table
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

(defn- actors-url
  ([] (actors-url ""))
  ([query-str]
   (if (:in-demo-mode? config)
     "data/fake-actors.json?_slow=true"
     (str (:api-base-url config) "ctia/actor/search?query=*" (encode-uri query-str)))))

(defn- fetch-actors-error [request-page-id]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:actor-table] merge
      {:ajax-error? true
       :loading? false})))

(defn- fetch-actors-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:actor-table] merge
      {:loading? false
       :data new-data})))

(defn- fetch-actors [next-fn error-fn]
  (fetch-json-as-clj (actors-url) next-fn error-fn))

;;------------------------------------------------------------------------------
;; Page Components
;;------------------------------------------------------------------------------

(rum/defc ActorExpandedRow < rum/static
  [an-actor]
  [:div.details-wrapper-819a2
    "TODO: Actor expanded row goes here"])

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
   :entity-name "Actors"
   :expanded-row-cmp ActorExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
   :loading? true
   :search-txt ""})

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Actors")

(rum/defc ActorTablePage < rum/static
  [state]
  (EntityTablePage state left-nav-tab :actor-table))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-actor-table-page! []
  (let [new-page-id (str (random-uuid))]
    (fetch-actors (partial fetch-actors-success new-page-id)
                  (partial fetch-actors-error new-page-id))
    (swap! app-state assoc :page :actor-table
                           :page-id new-page-id
                           :actor-table initial-page-state)))

(defn destroy-actor-table-page! []
  (swap! app-state dissoc :page :page-id :actor-table))
