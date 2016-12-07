(ns ctia-ui.core
  (:require
    ;; NOTE: requiring these once in the core namespace pulls them into the
    ;;       JavaScript build output
    ;; https://github.com/cljsjs/packages/wiki/Using-Packages
    [cljsjs.jquery]
    [cljsjs.jquery-ui]
    [cljsjs.marked]
    [cljsjs.moment]
    [goog.functions :refer [once]]
    [ctia-ui.pages.actor-form :refer [ActorFormPage]]
    [ctia-ui.pages.campaign-form :refer [CampaignFormPage]]
    [ctia-ui.pages.indicator-form :refer [IndicatorFormPage]]
    [ctia-ui.pages.indicator-table :refer [IndicatorTablePage]]
    [ctia-ui.pages.judgement-form :refer [JudgementFormPage]]
    [ctia-ui.pages.judgement-table :refer [JudgementTablePage]]
    [ctia-ui.pages.login :refer [LoginPage]]
    [ctia-ui.pages.sighting-form :refer [SightingFormPage]]
    [ctia-ui.pages.ttp-form :refer [TTPFormPage]]
    [ctia-ui.pages.ttp-table :refer [TTPTablePage]]
    [ctia-ui.routes :as routes]
    [ctia-ui.state :refer [app-state]]
    [oakmac.util :refer [by-id js-log log]]
    [rum.core :as rum]))

;;------------------------------------------------------------------------------
;; Top Level Application Component
;;------------------------------------------------------------------------------

(def pages
  {:login LoginPage

   :actor-form ActorFormPage
   :campaign-form CampaignFormPage
   :indicator-form IndicatorFormPage
   :judgement-form JudgementFormPage
   :sighting-form SightingFormPage
   :ttp-form TTPFormPage

   :indicator-table IndicatorTablePage
   :judgement-table JudgementTablePage
   :ttp-table TTPTablePage})

(rum/defc IrohApp < rum/static
  [state]
  (when-let [top-level-component (get pages (:page state))]
    (top-level-component state)))

;;------------------------------------------------------------------------------
;; Main Render Loop
;;------------------------------------------------------------------------------

(def app-container-el (by-id "appContainer"))

(defn- on-change-app-state
  "Render the app on every state change."
  [_kwd _the-atom _old-state new-state]
  (rum/mount (IrohApp new-state) app-container-el))

(add-watch app-state :render-loop on-change-app-state)

;;------------------------------------------------------------------------------
;; Global App Init
;;------------------------------------------------------------------------------

(def global-init!
  "Global application init."
   (once
     (fn []
       (routes/init!))))

(global-init!)
