(ns ctia-ui.pages.judgement-form
  (:require
    [ctia-ui.components :refer [EntityFormPage
                                HeaderBar
                                JudgementForm
                                LeftNavTabs
                                initial-judgement-form-state]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [neutralize-event vec-remove]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(def initial-page-state initial-judgement-form-state)

;;------------------------------------------------------------------------------
;; Action Bar
;;------------------------------------------------------------------------------

(defn- click-create-judgement-btn []
  ;; TODO: create the Judgement here
  nil)

(defn- click-cancel-btn []
  ;; TODO: cancel here
  nil)

(rum/defc ActionBar < rum/static
  []
  [:footer.panel-footer-25e7f
    [:div.page-save-cancel-eaa54
      [:button.blue-btn-680b8 {:on-click click-create-judgement-btn}
        "Create Judgement"]
      [:button.btn-df5f9 {:on-click click-cancel-btn}
        "Cancel"]]])

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(rum/defc JudgementFormPageBody < rum/static
  [body-state]
  [:div
    (JudgementForm [:judgement-form] body-state)
    (ActionBar)])

(def left-nav-tab "Judgements")
(def page-title "Create New Judgement")

(rum/defc JudgementFormPage < rum/static
  [state]
  (EntityFormPage state left-nav-tab page-title JudgementFormPageBody :judgement-form))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-judgement-form-page! []
  (swap! app-state assoc :page :judgement-form
                         :judgement-form initial-page-state))

(defn destroy-judgement-form-page! []
  (swap! app-state dissoc :page :judgement-form))
