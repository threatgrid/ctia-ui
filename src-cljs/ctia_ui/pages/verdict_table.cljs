(ns ctia-ui.pages.verdict-table
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

(defn- verdicts-url
  ([] (verdicts-url ""))
  ([query-str]
   (if (:in-demo-mode? config)
     "data/fake-verdicts.json?_slow=true"
     (str (:api-base-url config) "ctia/verdict/search?query=*" (encode-uri query-str)))))

(defn- fetch-verdicts-error [request-page-id]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:verdict-table] merge
      {:ajax-error? true
       :loading? false})))

(defn- fetch-verdicts-success [request-page-id new-data]
  ;; make sure we are still on the same page instance when the request returns
  (when (= request-page-id (:page-id @app-state))
    (swap! app-state update-in [:verdict-table] merge
      {:loading? false
       :data new-data})))

(defn- fetch-verdicts [next-fn error-fn]
  (fetch-json-as-clj (verdicts-url) next-fn error-fn))

;;------------------------------------------------------------------------------
;; Page Components
;;------------------------------------------------------------------------------

(rum/defc VerdictExpandedRow < rum/static
  [an-actor]
  [:div.details-wrapper-819a2
    "TODO: Verdict expanded row goes here"])

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

;; NOTE: just guessing at these columns for now
;; more discussion: https://github.com/threatgrid/ctia-ui/issues/29

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
   :entity-name "Verdicts"
   :expanded-row-cmp VerdictExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
   :loading? true
   :search-txt ""})

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Verdicts")

(rum/defc VerdictTablePage < rum/static
  [state]
  (EntityTablePage state left-nav-tab :verdict-table))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-verdict-table-page! []
  (let [new-page-id (str (random-uuid))]
    (fetch-verdicts (partial fetch-verdicts-success new-page-id)
                  (partial fetch-verdicts-error new-page-id))
    (swap! app-state assoc :page :verdict-table
                           :page-id new-page-id
                           :verdict-table initial-page-state)))

(defn destroy-verdict-table-page! []
  (swap! app-state dissoc :page :page-id :verdict-table))
