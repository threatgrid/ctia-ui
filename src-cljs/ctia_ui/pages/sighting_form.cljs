(ns ctia-ui.pages.sighting-form
  (:require
    [ctim.schemas.vocabularies :refer [sensor]]
    [ctia-ui.components :refer [ConfidenceButtons
                                DescribableEntityInputs
                                EntityFormPage
                                HeaderBar
                                InputLabel
                                LeftNavTabs
                                NumberInput
                                ObservablesInput
                                ObservedTime
                                ReferenceInput
                                TextInput
                                TokensInput]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [neutralize-event]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(def sensor-token-settings
  {:all-options (sort sensor)
   :allow-freeform? false
   :max-tokens 1
   :placeholder "OpenC2 Actuator"})

(def related-incidents-settings
  {:entities []
   :search-txt ""
   :url "data/fake-describable-entities.json"})

(def initial-page-state
  {:title ""
   :description ""
   :short_description ""
   :show-short-description? false

   :observed_time
     {:range? false
      :start_time ""
      :end_time ""}

   :confidence "None"
   :count 1

   :observables
     {:tokens []
      :type nil
      :value ""}
   :sensor sensor-token-settings
   :incidents related-incidents-settings
   :indicators related-incidents-settings})

;;------------------------------------------------------------------------------
;; Action Bar
;;------------------------------------------------------------------------------

(defn- click-create-sighting-btn []
  ;; TODO: create the Sighting here
  nil)

(defn- click-cancel-btn []
  ;; TODO: cancel here
  nil)

(rum/defc ActionBar < rum/static
  []
  [:footer.panel-footer-25e7f
    [:div.page-save-cancel-eaa54
      [:button.blue-btn-680b8 {:on-click click-create-sighting-btn}
        "Create Sighting"]
      [:button.btn-df5f9 {:on-click click-cancel-btn}
        "Cancel"]]])

;;------------------------------------------------------------------------------
;; Sighting Form
;;------------------------------------------------------------------------------

(rum/defc SightingForm < rum/static
  [{:keys [observed_time
           confidence
           count
           sensor
           observables
           incidents
           indicators]
    :as state}]
  [:div.form-wrapper-d8d6f
    (DescribableEntityInputs [:sighting-form] state "Sighting")
    (ObservedTime [:sighting-form :observed_time] observed_time)
    [:div.chunk-e556a
      (InputLabel "Confidence" true)
      (ConfidenceButtons [:sighting-form :confidence] confidence)]
    [:div.chunk-e556a
      (InputLabel "Count" true)
      (NumberInput [:sighting-form :count] count)]

    ;; Optional Fields
    [:div.chunk-e556a
      (InputLabel "Sensor")
      (TokensInput [:sighting-form :sensor] sensor)]
    [:div.chunk-e556a
      (InputLabel "Observables")
      (ObservablesInput [:sighting-form :observables] observables)]
    [:div.chunk-e556a
      (InputLabel "Related Indicators")
      (ReferenceInput [:sighting-form :indicators] indicators)]
    ;; TODO: observed relations - provide any context we can about where the observable came from
    [:div.chunk-e556a
      (InputLabel "Related Incidents")
      (ReferenceInput [:sighting-form :incidents] incidents)]
    (ActionBar)])

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Sightings")
(def page-title "Create New Sightings")

(rum/defc SightingFormPage < rum/static
  [state]
  (EntityFormPage state left-nav-tab page-title SightingForm :sighting-form))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-sighting-form-page! []
  (swap! app-state assoc :page :sighting-form
                         :sighting-form initial-page-state))

(defn destroy-sighting-form-page! []
  (swap! app-state dissoc :page :sighting-form))
