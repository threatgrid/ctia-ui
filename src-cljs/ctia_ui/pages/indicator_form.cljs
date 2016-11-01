(ns ctia-ui.pages.indicator-form
  (:require
    [ctim.schemas.vocabularies :refer [indicator-type kill-chain]]
    [ctia-ui.components :refer [Checkbox
                                ConfidenceButtons
                                DescribableEntityInputs
                                EntityFormPage
                                HeaderBar
                                InputLabel
                                JudgementForm
                                LeftNavTabs
                                ReferenceInput
                                TextInput
                                TextareaInput
                                TimeRange
                                TokensInput
                                initial-judgement-form-state]]
    [ctia-ui.state :refer [app-state]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(def indicator-type-options (sort indicator-type))
(def kill-chain-options (sort kill-chain))

(def indicator-type-token-settings
  {:all-options indicator-type-options
   :allow-freeform? false})

(def tags-token-settings
  {:all-options []
   :allow-freeform? true})

(def related-judgements-settings
  {:entities []
   :search-txt ""
   :url "data/fake-describable-entities.json"})

(def related-ttp-settings
  {:entities []
   :search-txt ""
   :url "data/fake-describable-entities.json"})

(def related-indicator-settings
  {:entities []
   :search-txt ""
   :url "data/fake-describable-entities.json"})

(def related-coas-settings
  {:entities []
   :search-txt ""
   :url "data/fake-describable-entities.json"})

(def related-campaign-settings
  {:entities []
   :search-txt ""
   :url "data/fake-describable-entities.json"})

(def kill-chain-token-settings
  {:all-options kill-chain-options
   :allow-freeform? false})

(def test-mechanisms-token-settings
  {:all-options []
   :allow-freeform? true})

(def alternate-ids-token-settings
  {:all-options []
   :allow-freeform? true})

(def initial-page-state
  {:title ""
   :description ""
   :short_description ""
   :show-short-description? false

   :valid_time
     {:start-time ""
      :end-time ""
      :valid-to-present? true}

   :producer ""
   :alternate_ids alternate-ids-token-settings
   :tags tags-token-settings
   :indicator_type indicator-type-token-settings
   :judgements related-judgements-settings
   :indicated_TTP related-ttp-settings
   :negate false
   :likely_impact ""
   :suggested_COAs related-coas-settings
   :confidence "None"
   :related_indicators related-indicator-settings
   :related_campaigns related-campaign-settings
   :related_COAs related-coas-settings
   :kill_chain_phases kill-chain-token-settings
   :test_mechanisms test-mechanisms-token-settings
   :new-judgement initial-judgement-form-state})

;;------------------------------------------------------------------------------
;; Specification Dropdown
;;------------------------------------------------------------------------------

; (defn- on-change-specification-dropdown [js-evt]
;   (let [new-value (aget js-evt "currentTarget" "value")]
;     (swap! app-state assoc-in [:indicator-form :specification-dropdown] new-value)))

;; NOTE: fixed to "Judgement" for demo
(rum/defc SpecificationDropdown < rum/static
  [option]
  [:select.select-d38d0 {:value "Judgement"}
    [:option {:value "Judgement"} "Judgement"]
    [:option {:value "ThreatBrain"} "ThreatBrain"]
    [:option {:value "Snort"} "Snort"]
    [:option {:value "SIOC"} "SIOC"]
    [:option {:value "OpenIOC"} "OpenIOC"]])

;;------------------------------------------------------------------------------
;; Action Bar
;;------------------------------------------------------------------------------

(defn- click-create-indicator-btn []
  ;; TODO: create the Indicator here
  nil)

(defn- click-cancel-btn []
  ;; TODO: cancel here
  nil)

(rum/defc ActionBar < rum/static
  []
  [:footer.panel-footer-25e7f
    [:div.page-save-cancel-eaa54
      [:button.blue-btn-680b8 {:on-click click-create-indicator-btn}
        "Create Indicator"]
      [:button.btn-df5f9 {:on-click click-cancel-btn}
        "Cancel"]]])

;;------------------------------------------------------------------------------
;; Indicator Form
;;------------------------------------------------------------------------------

(def likely-impact-placeholder "What is likely potential impact if this Indicator were to occur?")

(rum/defc IndicatorForm < rum/static
  [{:keys [valid_time
           producer
           alternate_ids
           negate
           tags
           indicator_type
           judgements
           indicated_TTP
           suggested_COAs
           confidence
           related_indicators
           related_campaigns
           related_COAs
           likely_impact
           kill_chain_phases
           test_mechanisms
           new-judgement]
    :as state}]
  [:div.form-wrapper-d8d6f
    (DescribableEntityInputs [:indicator-form] state "Indicator")
    [:div.chunk-e556a
      (InputLabel "Producer" true)
      (TextInput [:indicator-form :producer] producer "What produced this Indicator?")]
    [:div.chunk-e556a
      (InputLabel "Alternate ID / Alias")
      (TokensInput [:indicator-form :alternate_ids] alternate_ids)]
    [:div.chunk-e556a
      (InputLabel "Indicator Types")
      (TokensInput [:indicator-form :indicator_type] indicator_type)]
    [:div.chunk-e556a
      (InputLabel "Confidence")
      (ConfidenceButtons [:indicator-form :confidence] confidence)]
    [:div.chunk-e556a
      [:label.label-de00c "Negate"]
      (Checkbox [:indicator-form :negate] "Reverse this Indicator?" negate)]
    (TimeRange [:indicator-form :valid_time] valid_time)
    [:div.chunk-e556a
      (InputLabel "Test Mechanisms")
      (TokensInput [:indicator-form :test_mechanisms] test_mechanisms)]
    [:div.chunk-e556a
      (InputLabel "Descriptor Tags")
      (TokensInput [:indicator-form :tags] tags)]
    [:div.chunk-e556a
      (InputLabel "Kill Chain Phases")
      (TokensInput [:indicator-form :kill_chain_phases] kill_chain_phases)]

    ;; NOTE: composite_indicator_expression CompositeIndicatorExpression
    ;; punting on this for now...

    [:div.chunk-e556a
      (InputLabel "Likely Impact")
      (TextareaInput [:indicator-form :likely_impact] likely_impact likely-impact-placeholder)]
    [:div.chunk-e556a
      (InputLabel "Suggested COAs")
      (ReferenceInput [:indicator-form :suggested_COAs] suggested_COAs)]

    [:div.chunk-e556a
      (InputLabel "Related TTPs")
      (ReferenceInput [:indicator-form :indicated_TTP] indicated_TTP)]
    [:div.chunk-e556a
      (InputLabel "Related Indicators")
      (ReferenceInput [:indicator-form :related_indicators] related_indicators)]
    [:div.chunk-e556a
      (InputLabel "Related Campaigns")
      (ReferenceInput [:indicator-form :related_campaigns] related_campaigns)]
    [:div.chunk-e556a
      (InputLabel "Related COAs")
      (ReferenceInput [:indicator-form :related_COAs] related_COAs)]
    [:div.chunk-e556a
      (InputLabel "Related Judgements")
      (ReferenceInput [:indicator-form :judgements] judgements)]


    ;; NOTE: this is fixed to "Judgement" for demo purposes
    [:div.chunk-e556a
      (InputLabel "Specification")
      (SpecificationDropdown "Judgement")]

    [:h3.panel-title-0235d "New Judgement"]
    [:div {:style {:padding "5px 10px"}}
      (JudgementForm [:indicator-form :new-judgement] new-judgement)]

    (ActionBar)])

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "Indicators")
(def page-title "Create New Indicator")

(rum/defc IndicatorFormPage < rum/static
  [state]
  (EntityFormPage state left-nav-tab page-title IndicatorForm :indicator-form))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-indicator-form-page! []
  (swap! app-state assoc :page :indicator-form
                         :indicator-form initial-page-state))

(defn destroy-indicator-form-page! []
  (swap! app-state dissoc :page :indicator-form))
