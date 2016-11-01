(ns ctia-ui.pages.reset-password
  (:require
    [clojure.string :refer [blank?]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

;;------------------------------------------------------------------------------
;; Page State Atom
;;------------------------------------------------------------------------------

(def initial-page-state
  {:email ""})

(def page-state (atom initial-page-state))

;; NOTE: useful for debugging
; (add-watch page-state :log atom-logger)

;;------------------------------------------------------------------------------
;; Events
;;------------------------------------------------------------------------------

(defn- on-change-email [js-evt]
  (let [new-text (aget js-evt "currentTarget" "value")]
    (swap! page-state assoc :email new-text)))

(defn- submit-reset-form [js-evt]
  (.preventDefault js-evt))
  ;; TODO: send their account info here

;;------------------------------------------------------------------------------
;; Login Page Component
;;------------------------------------------------------------------------------

(rum/defc BottomInfo < rum/static
  []
  [:div.help-center
    [:div.link-wrapper
      [:a {:href "#/login"} "Return to Login"]]])

;; TODO: make this an actual <form> element
;; - or just support "Enter" to submit
(rum/defc ResetPasswordPage < rum/static
  [state]
  (let [email (:email state)
        submit-btn-disabled? (blank? email)]
    [:div.login-page-wrapper
      [:div.top-bar [:h1 "IROH"]]
      [:div.center
        [:h4.info-bar "Enter your email and we'll send you account info."]
        [:div.white-wrapper.login-form
          [:form {:action ""
                  :method "post"
                  :on-submit submit-reset-form}
            [:input#emailInput.big-input
              {:on-change on-change-email
               :placeholder "Email Address"
               :type "text"
               :value (:email state)}]
            [:input
              {:class (str "login-btn" (when submit-btn-disabled? " disabled"))
               :disabled submit-btn-disabled?
               :type "submit"
               :value "Send Account Info"}]]]
        (BottomInfo)]]))

;;------------------------------------------------------------------------------
;; Render Loop
;;------------------------------------------------------------------------------

(def app-container-el (by-id "appContainer"))

(defn- on-change-page-state
  "Render the page on every state change."
  [_kwd _the-atom _old-state new-state]
  (rum/request-render
    (rum/mount (ResetPasswordPage new-state) app-container-el)))

(add-watch page-state :main on-change-page-state)

;;------------------------------------------------------------------------------
;; Page Init
;;------------------------------------------------------------------------------

(defn init! []
  ;; trigger an initial render
  (reset! page-state initial-page-state)

  ;; put the focus on the email field
  (.focus (by-id "emailInput")))
