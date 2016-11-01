(ns ctia-ui.pages.campaign-form
  (:require
    [ctim.schemas.vocabularies :refer [campaign-status intended-effect]]
    [ctia-ui.components :refer [CampaignStatusButtons
                                ConfidenceButtons
                                DescribableEntityInputs
                                EntityFormPage
                                InputLabel
                                ReferenceInput
                                TextInput
                                TimeRange
                                TokensInput]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [neutralize-event]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(def intended-effect-options (sort intended-effect))

(def names-tokens-settings
  {:all-options []
   :allow-freeform? true})

(def related-indicator-settings
  {:entities []
   :search-txt ""
   :url "data/fake-describable-entities.json"})

(def intended-effect-token-settings
  {:all-options intended-effect-options
   :allow-freeform? false})

(def related-ttp-settings
 {:entities []
  :search-txt ""
  :url "data/fake-describable-entities.json"})

(def related-incidents-settings
 {:entities []
  :search-txt ""
  :url "data/fake-describable-entities.json"})

(def related-actors-settings
 {:entities []
  :search-txt ""
  :url "data/fake-describable-entities.json"})

(def related-campaigns-settings
 {:entities []
  :search-txt ""
  :url "data/fake-describable-entities.json"})

(def initial-page-state
  {:title ""
   :description ""
   :short_description ""
   :show-short-description? false

   :valid_time
     {:start-time ""
      :end-time ""
      :valid-to-present? true}

   :campaign_type ""
   :names names-tokens-settings
   :indicators related-indicator-settings
   :intended_effect intended-effect-token-settings
   :status "Ongoing"
   :confidence "Low"
   :related_TTPs related-ttp-settings
   :related_incidents related-incidents-settings
   :attribution related-actors-settings
   :associated_campaigns related-campaigns-settings

   :activity []})

;; token input cursors
(def names-cursor (rum/cursor-in app-state [:campaign-form :names]))
(def intended-effect-cursor (rum/cursor-in app-state [:campaign-form :intended_effect]))

;;------------------------------------------------------------------------------
;; Action Bar
;;------------------------------------------------------------------------------

(defn- click-create-campaign-btn []
  ;; TODO: create the Campaign here
  nil)

(defn- click-cancel-btn []
  ;; TODO: cancel here
  nil)

(rum/defc ActionBar < rum/static
  []
  [:footer.panel-footer-25e7f
    [:div.page-save-cancel-eaa54
      [:button.blue-btn-680b8 {:on-click click-create-campaign-btn}
        "Create Campaign"]
      [:button.btn-df5f9 {:on-click click-cancel-btn}
        "Cancel"]]])

;;------------------------------------------------------------------------------
;; Campaign Form
;;------------------------------------------------------------------------------

(def campaign-type-placeholder
  "What kind of campaign is this?")

(rum/defc CampaignForm < rum/static
  [{:keys [valid_time
           campaign_type
           names
           indicators
           intended_effect
           status
           confidence
           related_TTPs
           related_incidents
           attribution
           associated_campaigns
           activity]
    :as state}]
  [:div.form-wrapper-d8d6f
    (DescribableEntityInputs [:campaign-form] state "Campaign")
    (TimeRange [:campaign-form :valid_time] valid_time)
    [:div.chunk-e556a
      (InputLabel "Type" true)
      (TextInput [:campaign-form :campaign_type] campaign_type campaign-type-placeholder)]

    ;; Optional Fields
    [:div.chunk-e556a
      (InputLabel "Names")
      (TokensInput names-cursor names)]
    [:div.chunk-e556a
      (InputLabel "Related Indicators")
      (ReferenceInput [:campaign-form :indicators] indicators)]
    [:div.chunk-e556a
      (InputLabel "Intended Effect")
      (TokensInput intended-effect-cursor intended_effect)]
    [:div.chunk-e556a
      (InputLabel "Status")
      (CampaignStatusButtons [:campaign-form :status] status)]

    ;; NOTE: we might want to make a "Related XYZs" component
    ;; these cluster together often
    [:div.chunk-e556a
      (InputLabel "Related TTPs")
      (ReferenceInput [:campaign-form :related_TTPs] related_TTPs)]
    [:div.chunk-e556a
      (InputLabel "Related Incidents")
      (ReferenceInput [:campaign-form :related_incidents] related_incidents)]
    [:div.chunk-e556a
      (InputLabel "Related Actors")
      (ReferenceInput [:campaign-form :attribution] attribution)]
    [:div.chunk-e556a
      (InputLabel "Related Campaigns")
      (ReferenceInput [:campaign-form :associated_campaigns] associated_campaigns)]

    [:div.chunk-e556a
      (InputLabel "Confidence")
      (ConfidenceButtons [:campaign-form :confidence] confidence)]

    ;; TODO: need to figure out an Activity input
    (comment
      (f/entry :activity c/Activity
               :description "Actions taken in regards to this Campaign"))

    (ActionBar)])

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Campaigns")
(def page-title "Create New Campaign")

(rum/defc CampaignFormPage < rum/static
  [state]
  (EntityFormPage state left-nav-tab page-title CampaignForm :campaign-form))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-campaign-form-page! []
  (swap! app-state assoc :page :campaign-form
                         :campaign-form initial-page-state))

(defn destroy-campaign-form-page! []
  (swap! app-state dissoc :page :campaign-form))
