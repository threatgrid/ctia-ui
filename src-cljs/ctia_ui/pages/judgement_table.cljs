(ns ctia-ui.pages.judgement-table
  (:require
    [clojure.string :refer [blank? capitalize lower-case]]
    [ctia-ui.components :refer [EntityTablePage
                                JudgementReasonCell
                                ObservableCell]]
    [ctia-ui.config :refer [config]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [encode-uri]]
    [oakmac.util :refer [atom-logger by-id fetch-json-as-clj js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Data Fetching
;;------------------------------------------------------------------------------

;; NOTE: these functions should probably be in their own namespace

(defn- judgements-url
  ([] (judgements-url ""))
  ([query-str]
   (if (:in-demo-mode? config)
     "data/fake-judgements.json?_slow=true"
     (str (:api-base-url config) "ctia/judgement/search?query=*" (encode-uri query-str)))))

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
;; Page Components
;;------------------------------------------------------------------------------

;; NOTE: some of these components can probably be moved to components.cljs

(def lorem-ipsum "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")

;; NOTE: reason might be Markdown here
(rum/defc JudgementReason < rum/static
  [reason-txt]
  [:div.expanded-cell-7f813
    [:div.expanded-title-a18ca "Reason"]
    [:div.expanded-details-2072d reason-txt]])

(def indicator-row-mixin
  {:key-fn
     (fn [idx _indicator]
       (str idx))})

(rum/defc JudgementIndicator < (merge rum/static indicator-row-mixin)
  [idx indicator]
  [:div
    [:div.expanded-indicator-d1f16 "Indicator Title"]
    [:div.expanded-secondary-info-3a554 "Secondary Info"]
    [:div.expanded-details-2072d lorem-ipsum]])

(rum/defc JudgementIndicators < rum/static
  [indicators]
  [:div.expanded-cell-7f813
    [:div.expanded-title-a18ca "Indicators"]
    (map-indexed JudgementIndicator indicators)])

(rum/defc JudgementSource < rum/static
  [source-txt]
  [:div.expanded-cell-7f813
    [:div.expanded-title-a18ca "Source"]
    [:div.expanded-details-2072d source-txt]])

(rum/defc JudgementMetadata < rum/static
  [language priority]
  [:div.expanded-cell-7f813
    (when-not (blank? language)
      [:div.meta-data-e653c (str "Language: " language)])
    [:div.meta-data-e653c (str "Priority: " priority)]])

(rum/defc JudgementExpandedRow < rum/static
  [{:keys [indicators reason source language priority]}]
  [:div.details-wrapper-819a2
    (JudgementReason reason)
    (JudgementIndicators indicators)
    (JudgementSource source)
    (JudgementMetadata language priority)])

(def disposition-name-classes
  {"malicious" "malicious-e4c73"})
  ;; TODO: add more disposition name classes here

(rum/defc JudgementObservableCell < rum/static
  [{:keys [disposition_name observable]}]
  [:div
    ;; TODO: we will want different formats for different observable types here
    [:div.title-a77cb (:value observable)]
    [:div.disposition-06e65
      [:div {:class (get disposition-name-classes (lower-case disposition_name) "")}
        (capitalize disposition_name)]]])

(rum/defc IndicatorToken < rum/static
  [txt]
  [:div.indicator-241b3 txt])

;; TODO: this may change once we figure out indicator "injection"
(rum/defc IndicatorsCell < rum/static
  [{:keys [indicators]}]
  (let [num-indicators (count indicators)]
    [:div
      (IndicatorToken (-> indicators first :title))
      (when (> num-indicators 1)
        (IndicatorToken (str "+" (dec num-indicators) " More")))]))

(def severity-cell-classes
  {"high" "high-severity-3e887"})
  ;; TODO: add more classes here as necessary

(rum/defc SeverityCell < rum/static
  [{:keys [severity]}]
  [:div {:class (get severity-cell-classes (lower-case severity) "")}
    severity])

(def confidence-cell-classes
  {"high" "high-conf-117f2"
   "none" "no-conf-b8d6d"
   "unknown" "unknown-5dbe8"})
  ;; NOTE: no classes needed for "low" and "medium"

(rum/defc ConfidenceCell < rum/static
  [{:keys [confidence]}]
  [:div {:class (get confidence-cell-classes (lower-case confidence) "")}
    (capitalize confidence)])

(def tlp-cell-classes
  {"amber" "tlp-amber-689ed"
   "green" "tlp-green-793b3"
   "red" "tlp-red-95c3e"
   ;; NOTE: no specific class needed for "white"
   "white" ""})

(rum/defc TLPCell < rum/static
  [{:keys [tlp]}]
  [:div {:class (get tlp-cell-classes (lower-case tlp) "")}
    (capitalize tlp)])

(rum/defc TimeCell < rum/static
  [{:keys [valid_time]}]
  [:div "Time cell"])

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(def cols
  [{:th "Observable"
    :td JudgementObservableCell}
   {:th "Reason"
    :td :reason}
   {:th "Indicators"
    :td IndicatorsCell}
   {:th "Source"
    :td :source}
   {:th "Sev."
    :td :severity}
   {:th "Conf."
    :td ConfidenceCell}
   {:th "TLP"
    :td TLPCell}])
   ;; FIXME: punting on the time cell for now...
   ; {:th "Time"
   ;  :td TimeCell}])

(def initial-page-state
  {:ajax-error? false
   :cols cols
   :data []
   :entity-name "Judgements"
   :expanded-row-cmp JudgementExpandedRow
   :expanded-rows #{}
   :hovered-row-id nil
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
