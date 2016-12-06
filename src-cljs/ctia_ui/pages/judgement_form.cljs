(ns ctia-ui.pages.judgement-form
  (:require
    [ctia-ui.components :refer [EntityFormPage
                                HeaderBar
                                initial-judgement-form-state
                                JudgementForm
                                LeftNavTabs
                                LoadingButton]]
    [ctia-ui.config :refer [config]]
    [ctia-ui.data :refer [tenzin-base-url]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [json-stringify neutralize-event vec-remove]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Initial Page State
;;------------------------------------------------------------------------------

(def initial-page-state
  (merge initial-judgement-form-state
         {:loading? false}))

;;------------------------------------------------------------------------------
;; Create New Judgement
;;------------------------------------------------------------------------------

(def judgement-keys (keys initial-judgement-form-state))

(defn- get-judgement-from-form []
  (let [new-j (select-keys (:judgement-form @app-state) judgement-keys)]
    ;; TODO: convert valid_time
    ;; TODO: convert indicators
    new-j))

(defn- new-judgement-url []
  (if (:in-demo-mode? config)
    "data/create-judgement.json?_slow=true"
    (str tenzin-base-url "ctia/judgement")))

(defn- create-judgement [new-judgement success-fn error-fn]
  (.ajax $
    (js-obj "data" (json-stringify new-judgement)
            "error" error-fn
            "headers" (js-obj "api_key" (:api-key config))
            "success" success-fn
            "type" (if (:in-demo-mode? config) "GET" "POST")
            "url" (new-judgement-url))))

;;------------------------------------------------------------------------------
;; Action Bar
;;------------------------------------------------------------------------------

;; TODO: need to add a page-id to the request cycle here
;;       or prevent the user from changing pages while the request is active

(defn- create-judgement-error []
  ;; TODO: show an error bar here
  (swap! app-state assoc-in [:judgement-form :loading?] false))

(defn- create-judgement-success []
  ;; TODO: redirect to the Judgements table here?
  (swap! app-state assoc-in [:judgement-form :loading?] false))

(defn- click-create-judgement-btn []
  ;; set the loading state
  (swap! app-state assoc-in [:judgement-form :loading?] true)
  ;; send the create Judgement request
  (create-judgement (get-judgement-from-form) create-judgement-success create-judgement-error))

(defn- click-cancel-btn []
  ;; TODO: what happens here? take them back to the Judgements table?
  ;;       reset the form?
  nil)

(rum/defc LoadingActionBar < rum/static
  []
  [:footer.panel-footer-25e7f
    [:div.page-save-cancel-eaa54
      (LoadingButton "Creating Judgement …")]])

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
  [:div.form-wrapper-d8d6f
    (JudgementForm [:judgement-form] body-state)
    (if (:loading? body-state)
      (LoadingActionBar)
      (ActionBar))])

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
