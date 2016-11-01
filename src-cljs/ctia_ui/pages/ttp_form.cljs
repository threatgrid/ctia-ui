(ns ctia-ui.pages.ttp-form
  (:require
    [clojure.string :refer [blank?]]
    [ctim.schemas.vocabularies :refer [attack-infrastructure
                                       attack-tool-type
                                       information-type
                                       intended-effect
                                       malware-type
                                       system-type
                                       threat-actor-type]]
    [ctia-ui.components :refer [CAPECInput
                                ConfidenceButtons
                                DescribableEntityInputs
                                EntityFormPage
                                HeaderBar
                                InputLabel
                                LeftNavTabs
                                ReferenceInput
                                TextareaInput
                                TextInput
                                TimeRange
                                TokensInput]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [neutralize-event vec-remove]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(def attack-infrastructure-options (sort attack-infrastructure))
(def attack-tool-type-options (sort attack-tool-type))
(def information-type-options (sort information-type))
(def intended-effect-options (sort intended-effect))
(def malware-type-options (sort malware-type))
(def system-type-options (sort system-type))

(def intended-effect-token-settings
  {:all-options intended-effect-options
   :allow-freeform? false})

(def malware-types-token-settings
  {:all-options malware-type-options
   :allow-freeform? false})

(def tool-types-token-settings
  {:all-options attack-tool-type-options
   :allow-freeform? false})

(def infrastructure-type-token-settings
  {:all-options attack-infrastructure-options
   :allow-freeform? false
   :max-tokens 1})

;; TODO: this needs to change to a list of identities
(def identity-token-settings
  {:all-options attack-infrastructure-options
   :allow-freeform? false})

(def systems-targeted-token-settings
  {:all-options system-type-options
   :allow-freeform? false})

(def information-token-settings
  {:all-options information-type-options
   :allow-freeform? false})

;; TODO: this probably needs to change to a ReferenceInput
(def observables-token-settings
  {:all-options information-type-options
   :allow-freeform? false})

(def exploit-targets-settings
 {:entities []
  :search-txt ""
  :url "data/fake-describable-entities.json"})

(def related-ttp-settings
 {:entities []
  :search-txt ""
  :url "data/fake-describable-entities.json"})

(def indicators-settings
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
   :tlp "Green"
   :indicators indicators-settings
   :show-optional-fields? true

   :show-source? false
   :source ""

   :intended-effect intended-effect-token-settings

   :behavior-resource-dropdown "-1"

   :attack-pattern-description ""

   :attack-pattern-capec
     {:search-txt ""
      :capec nil}

   :malware-description ""
   :malware-types malware-types-token-settings

   :infrastructure-description ""
   :infrastructure-type infrastructure-type-token-settings

   :tool
     {:description ""
      :types tool-types-token-settings
      :references [""]
      :vendor ""
      :version ""
      :service-pack ""}

   :persona
     {:identity ""
      :description ""
      :related-identity ""
      :confidence "Low"
      :info-source ""
      :relationship ""}

   :identity-targeted identity-token-settings
   :systems-targeted systems-targeted-token-settings
   :info-targeted information-token-settings
   :observables-targeted observables-token-settings

   :exploit_targets exploit-targets-settings
   :related_TTPs related-ttp-settings})

(def page-state
  "A cursor to the :ttp-form key in app-state.
   NOTE: most things in this module operate on this cursor."
  (rum/cursor-in app-state [:ttp-form]))

;; some other useful cursors
(def intended-effect-cursor (rum/cursor-in app-state [:ttp-form :intended-effect]))
(def malware-types-cursor (rum/cursor-in app-state [:ttp-form :malware-types]))
(def tool-types-cursor (rum/cursor-in app-state [:ttp-form :tool :types]))
(def infrastructure-type-cursor (rum/cursor-in app-state [:ttp-form :infrastructure-type]))
(def identity-targeted-cursor (rum/cursor-in app-state [:ttp-form :identity-targeted]))
(def systems-targeted-cursor (rum/cursor-in app-state [:ttp-form :systems-targeted]))
(def info-targeted-cursor (rum/cursor-in app-state [:ttp-form :info-targeted]))
(def observables-targeted-cursor (rum/cursor-in app-state [:ttp-form :observables-targeted]))

;;------------------------------------------------------------------------------
;; Generic Select Input
;;------------------------------------------------------------------------------

;; TOOD: move this to components.cljs

(rum/defc BasicSelect < rum/static
  [items active-item key]
  [:div.input-wrapper-ac07d
    [:select.select-d38d0
      {:on-change (fn [js-evt] (swap! page-state assoc key (aget js-evt "currentTarget" "value")))
       :value active-item}
      (map (fn [itm] [:option {:value itm} itm]) items)]])

;;------------------------------------------------------------------------------
;; TLP Buttons
;;------------------------------------------------------------------------------

;; TODO: move this to components.cljs

(defn- click-tlp-btn [color]
  (swap! page-state assoc :tlp color))

(def tlp-color-classes
  {"White" "white-2ca39"
   "Green" "green-1eed1"
   "Amber" "amber-2a2ee"
   "Red" "red-714b6"})

(rum/defc TLPButton < rum/static
  [color active?]
  [:label
    {:class (str "outline-btn-series-f1745"
                 (when active? (str " " (get tlp-color-classes color))))
     :on-click (partial click-tlp-btn color)}
    color])

(rum/defc TLPButtons < rum/static
  [selected]
  [:div.group-be764
    (TLPButton "White" (= selected "White"))
    (TLPButton "Green" (= selected "Green"))
    (TLPButton "Amber" (= selected "Amber"))
    (TLPButton "Red"   (= selected "Red"))])

;;------------------------------------------------------------------------------
;; Show Section Button
;;------------------------------------------------------------------------------

(defn- click-show-section-btn [key]
  (swap! page-state assoc key true))

(rum/defc ShowSectionBtn < rum/static
  [txt key]
  [:button.outline-btn-med-1c619
    {:on-click (partial click-show-section-btn key)}
    txt])

;;------------------------------------------------------------------------------
;; Behavior / Resource Dropdown
;;------------------------------------------------------------------------------

(defn- change-behavior-dropdown [js-evt]
  (let [new-value (aget js-evt "currentTarget" "value")]
    (swap! page-state assoc :behavior-resource-dropdown new-value)))

(rum/defc BehaviorResourceDropdown < rum/static
  [option]
  [:select.select-d38d0
    {:on-change change-behavior-dropdown
     :value option}
    [:option {:value "-1"} "None"]
    [:optgroup {:label "Behavior"}
      [:option {:value "Attack Pattern"} "Attack Pattern"]
      [:option {:value "Malware"} "Malware"]]
    [:optgroup {:label "Resource"}
      [:option {:value "Tool"} "Tool"]
      [:option {:value "Persona"} "Persona"]
      [:option {:value "Infrastructure"} "Infrastructure"]]])

;;------------------------------------------------------------------------------
;; Attack Pattern Input
;;------------------------------------------------------------------------------

(rum/defc AttackPatternInput < rum/static
  [description capec]
  [:fieldset.fieldset-3cd7f
    [:div.chunk-e556a
      (InputLabel "Description of Attack Pattern" true)
      (TextInput [:ttp-form :attack-pattern-description] description "What does the pattern do?")]
    (CAPECInput [:ttp-form :attack-pattern-capec] capec)])

;;------------------------------------------------------------------------------
;; Malware Input
;;------------------------------------------------------------------------------

(rum/defc MalwareInput < rum/static
  [description malware-types]
  [:div.fieldset-3cd7f
    [:div.chunk-e556a
      (InputLabel "Description of Malware Instance" true)
      (TextInput [:ttp-form :malware-description] description "What does the malware do?")]
    [:div.chunk-e556a
      (InputLabel "Type of Malware" true)
      (TokensInput malware-types-cursor malware-types)]])

;;------------------------------------------------------------------------------
;; Tool Input
;;------------------------------------------------------------------------------

(defn- on-change-text-input [cursor js-evt]
  (let [new-txt (aget js-evt "currentTarget" "value")
        cursor (if (keyword? cursor) [cursor] cursor)]
    (swap! page-state assoc-in cursor new-txt)))

(defn- click-remove-reference-row [idx js-evt]
  (neutralize-event js-evt)
  ;; remove this row from the references vector
  (swap! page-state update-in [:tool :references] vec-remove idx))

(rum/defc ReferenceInputLine < rum/static
  [idx txt]
  [:div.input-wrapper-ac07d
    [:input.text-input-c27c0
      {:on-change (partial on-change-text-input [:tool :references idx])
       :type "text"
       :value txt}]
    (when-not (zero? idx)
      [:a.hash-c1a96
        {:href "#"
         :on-click (partial click-remove-reference-row idx)}
        "remove"])])

(defn- click-add-reference []
  (swap! page-state update-in [:tool :references] conj ""))

(rum/defc ToolInput < rum/static
  [{:keys [description types references version vendor service-pack]}]
  [:div.fieldset-3cd7f
    [:div.chunk-e556a
      (InputLabel "Description of Tool" true)
      (TextInput [:tool :description] description "What does the tool do?")]
    [:div.chunk-e556a
      (InputLabel "Type of Tool" true)
      (TokensInput tool-types-cursor types)]
    [:div.chunk-e556a
      (InputLabel "References")
      (map-indexed ReferenceInputLine references)
      [:button.outline-btn-med-1c619 {:on-click click-add-reference} "Add Reference"]]
    [:div.chunk-e556a
      (InputLabel "Vendor")
      (TextInput [:tool :vendor] vendor)]
    [:div.chunk-e556a
      (InputLabel "Version")
      (TextInput [:tool :version] version)]
    [:div.chunk-e556a
      (InputLabel "Service Pack")
      (TextInput [:tool :service-pack] service-pack)]])

;;------------------------------------------------------------------------------
;; Persona Input
;;------------------------------------------------------------------------------

(rum/defc PersonaInput < rum/static
  [{:keys [identity description related-identity confidence info-source relationship]}]
  [:div.fieldset-3cd7f
    [:div.chunk-e556a
      (InputLabel "Identity" true)
      (TextInput [:persona :identity] identity)]
    [:div.chunk-e556a
      (InputLabel "Description of Identity" true)
      (TextInput [:persona :description] description)]
    ;; TODO: this needs an autocomplete dropdown
    [:div.chunk-e556a
      (InputLabel "Related Identity" true)
      (TextInput [:persona :related-identity] related-identity)]
    [:div.chunk-e556a
      (InputLabel "Confidence")
      (ConfidenceButtons [:ttp-form :persona :confidence] confidence)]
    [:div.chunk-e556a
      (InputLabel "Information Source")
      (TextInput [:persona :info-source] info-source)]
    [:div.chunk-e556a
      (InputLabel "Relationship")
      (TextInput [:persona :relationship] relationship)]])

;;------------------------------------------------------------------------------
;; Infrastructure Input
;;------------------------------------------------------------------------------

(rum/defc InfrastructureInput < rum/static
  [description type]
  [:div.fieldset-3cd7f
    [:div.chunk-e556a
      (InputLabel "Description of Infrastructure" true)
      (TextInput [:ttp-form :infrastructure-description] description)]
    [:div.chunk-e556a
      (InputLabel "Type of Infrastructure" true)
      (TokensInput infrastructure-type-cursor type)]])

;;------------------------------------------------------------------------------
;; Exploit Target Row
;;------------------------------------------------------------------------------

(defn- click-remove-exploit-target-row [idx js-evt]
  (neutralize-event js-evt)
  (swap! page-state update-in [:exploit-targets] vec-remove idx))

(rum/defc ExploitTargetRow < rum/static
  [idx {:keys [name short-description description]}]
  [:div.row-b1250
    [:div [:span.name-8e9a0 name]
          " - "
          [:span.short-desc-4d5b5 short-description]
          [:a.hash-c1a96
            {:on-click (partial click-remove-exploit-target-row idx)
             :href "#"}
            "remove"]]
    [:div.description-5c53e description]])

;;------------------------------------------------------------------------------
;; Action Bar
;;------------------------------------------------------------------------------

(defn- click-create-ttp-btn []
  ;; TODO: create the TTP here
  nil)

(defn- click-cancel-btn []
  ;; TODO: cancel here
  nil)

(rum/defc ActionBar < rum/static
  []
  [:footer.panel-footer-25e7f
    [:div.page-save-cancel-eaa54
      [:button.blue-btn-680b8 {:on-click click-create-ttp-btn}
        "Create TTP"]
      [:button.btn-df5f9 {:on-click click-cancel-btn}
        "Cancel"]]])

;;------------------------------------------------------------------------------
;; TTP Form
;;------------------------------------------------------------------------------

;; NOTE: disabled during development
(defn- toggle-optional-fields [])
  ;; (swap! page-state update-in [:show-optional-fields?] not))

(rum/defc TTPForm < rum/static
  [{:keys [indicators
           tlp
           valid_time
           type
           intended-effect
           show-source?
           infrastructure-description infrastructure-type
           show-optional-fields? systems-targeted
           behavior-resource-dropdown
           attack-pattern-description
           attack-pattern-capec
           malware-description malware-types
           tool persona
           identity-targeted systems-targeted info-targeted observables-targeted
           exploit_targets
           related_TTPs]
    :as state}]
  [:div.form-wrapper-d8d6f
    (DescribableEntityInputs [:ttp-form] state "TTP")
    (TimeRange [:ttp-form :valid_time] valid_time)
    [:div.chunk-e556a
      (InputLabel "Indicators" true)
      (ReferenceInput [:ttp-form :indicators] indicators)]
    [:div.chunk-e556a
      (InputLabel "Traffic Light Protocol" false)
      (TLPButtons tlp)]

    ;; Optional Fields
    [:div.chunk-e556a
      (InputLabel "Intended Effect" false)
      (TokensInput intended-effect-cursor intended-effect)]

    [:div.chunk-e556a
      (InputLabel "Add Behavior or Resource" false)
      (BehaviorResourceDropdown behavior-resource-dropdown)]

    (condp = behavior-resource-dropdown
      "Attack Pattern" (AttackPatternInput attack-pattern-description attack-pattern-capec)
      "Malware" (MalwareInput malware-description malware-types)
      "Tool" (ToolInput tool)
      "Persona" (PersonaInput persona)
      "Infrastructure" (InfrastructureInput infrastructure-description infrastructure-type)
      nil)

    [:h3.panel-title-0235d "Victim Targeting"]
    [:div.chunk-e556a
      (InputLabel "Identity")
      (TokensInput identity-targeted-cursor identity-targeted)]
    [:div.chunk-e556a
      (InputLabel "Systems Targeted")
      (TokensInput systems-targeted-cursor systems-targeted)]
    [:div.chunk-e556a
      (InputLabel "Information Targeted")
      (TokensInput info-targeted-cursor info-targeted)]
    [:div.chunk-e556a
      (InputLabel "Observable Targeted")
      (TokensInput observables-targeted-cursor observables-targeted)]

    [:h3.panel-title-0235d "Exploit Targets"]
    (ReferenceInput [:ttp-form :exploit_targets] exploit_targets)

    [:h3.panel-title-0235d "Related TTPs"]
    (ReferenceInput [:ttp-form :related_TTPs] related_TTPs)

    ;; TODO: Source
    ;; TODO: Attack / Kill Chain

    (ActionBar)])

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(def left-nav-tab "TTPs")
(def page-title "Create New TTP")

(rum/defc TTPFormPage < rum/static
  [state]
  (EntityFormPage state left-nav-tab page-title TTPForm :ttp-form))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-ttp-form-page! []
  (swap! app-state assoc :page :ttp-form
                         :ttp-form initial-page-state))

(defn destroy-ttp-form-page! []
  (swap! app-state dissoc :page :ttp-form))
