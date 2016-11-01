(ns ctia-ui.pages.actor-form
  (:require
    [ctim.schemas.vocabularies :refer [intended-effect
                                       motivation
                                       sophistication
                                       threat-actor-type]]
    [ctia-ui.components :refer [ConfidenceButtons
                                DescribableEntityInputs
                                EntityFormPage
                                InputLabel
                                ReferenceInput
                                TextareaInput
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

(def threat-actor-type-options (sort threat-actor-type))
(def motivation-options (sort motivation))
(def sophistication-options (sort sophistication))
(def intended-effect-options (sort intended-effect))

(def actor-type-token-settings
  {:all-options threat-actor-type-options
   :allow-freeform? false
   :max-tokens 1})

(def motivation-token-settings
  {:all-options motivation-options
   :allow-freeform? false
   :max-tokens 1})

(def sophistication-token-settings
  {:all-options sophistication-options
   :allow-freeform? false
   :max-tokens 1})

(def intended-effect-token-settings
  {:all-options intended-effect-options
   :allow-freeform? false
   :max-tokens 1})

(def related-ttp-settings
 {:entities []
  :search-txt ""
  :url "data/fake-describable-entities.json"})

(def related-campaigns-settings
 {:entities []
  :search-txt ""
  :url "data/fake-describable-entities.json"})

(def related-actors-settings
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

   :actor_type actor-type-token-settings

   ;; TODO: figure out Identity input
   :identity nil

   :motivation motivation-token-settings
   :sophistication sophistication-token-settings
   :intended_effect intended-effect-token-settings
   :planning_and_operational_support ""
   :observed_TTPs related-ttp-settings
   :associated_campaigns related-campaigns-settings
   :associated_actors related-actors-settings
   :confidence "Low"})

;;------------------------------------------------------------------------------
;; Action Bar
;;------------------------------------------------------------------------------

(defn- click-create-actor-btn []
  ;; TODO: create the Actor here
  nil)

(defn- click-cancel-btn []
  ;; TODO: cancel here
  nil)

(rum/defc ActionBar < rum/static
  []
  [:footer.panel-footer-25e7f
    [:div.page-save-cancel-eaa54
      [:button.blue-btn-680b8 {:on-click click-create-actor-btn}
        "Create Actor"]
      [:button.btn-df5f9 {:on-click click-cancel-btn}
        "Cancel"]]])

;;------------------------------------------------------------------------------
;; Actor Form
;;------------------------------------------------------------------------------

(rum/defc ActorForm < rum/static
  [{:keys [valid_time
           actor_type
           identity
           motivation
           sophistication
           intended_effect
           planning_and_operational_support
           observed_TTPs
           associated_campaigns
           associated_actors
           confidence]
    :as state}]
  [:div.form-wrapper-d8d6f
    (DescribableEntityInputs [:actor-form] state "Actor")
    (TimeRange [:actor-form :valid_time] valid_time)
    [:div.chunk-e556a
      (InputLabel "Actor Type" true)
      (TokensInput [:actor-form :actor_type] actor_type)]

    ;; Optional Fields
    ;; TODO: identity c/Identity
    [:div.chunk-e556a
      (InputLabel "Motivation")
      (TokensInput [:actor-form :motivation] motivation)]
    [:div.chunk-e556a
      (InputLabel "Sophistication")
      (TokensInput [:actor-form :sophistication] sophistication)]
    [:div.chunk-e556a
      (InputLabel "Intended Effect")
      (TokensInput [:actor-form :intended_effect] intended_effect)]
    [:div.chunk-e556a
      (InputLabel "Planning and Operational Support")
      (TextareaInput [:actor-form :planning_and_operational_support] planning_and_operational_support)]
    [:div.chunk-e556a
      (InputLabel "Related TTPs")
      (ReferenceInput [:actor-form :observed_TTPs] observed_TTPs)]
    [:div.chunk-e556a
      (InputLabel "Related Campaigns")
      (ReferenceInput [:actor-form :associated_campaigns] associated_campaigns)]
    [:div.chunk-e556a
      (InputLabel "Related Actors")
      (ReferenceInput [:actor-form :associated_actors] associated_actors)]
    [:div.chunk-e556a
      (InputLabel "Confidence")
      (ConfidenceButtons [:actor-form :confidence] confidence)]

    (ActionBar)])

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Actors")
(def page-title "Create New Actor")

(rum/defc ActorFormPage < rum/static
  [state]
  (EntityFormPage state left-nav-tab page-title ActorForm :actor-form))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-actor-form-page! []
  (swap! app-state assoc :page :actor-form
                         :actor-form initial-page-state))

(defn destroy-actor-form-page! []
  (swap! app-state dissoc :page :actor-form))
