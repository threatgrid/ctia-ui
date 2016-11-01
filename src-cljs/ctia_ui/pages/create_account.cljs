(ns ctia-ui.pages.create-account
  (:require
    [clojure.string :refer [blank?]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

;;------------------------------------------------------------------------------
;; Page State Atom
;;------------------------------------------------------------------------------

(def initial-page-state
  {:email ""
   :password ""
   :sending-info? false
   :info-sent? false})

(def page-state (atom initial-page-state))

;; NOTE: useful for debugging
; (add-watch page-state :log atom-logger)

;;------------------------------------------------------------------------------
;; Events
;;------------------------------------------------------------------------------

(defn- on-change-text-field [kwd js-evt]
  (let [new-text (aget js-evt "currentTarget" "value")]
    (swap! page-state assoc kwd new-text)))

(defn- yay-it-worked []
  (swap! page-state assoc :info-sent? true))

(defn- submit-create-account-form [js-evt]
  (.preventDefault js-evt)
  (swap! page-state assoc :sending-info? true)
  ;; TODO: submit account information here
  (js/setTimeout yay-it-worked (+ 400 (rand-int 400))))

;;------------------------------------------------------------------------------
;; Login Page Component
;;------------------------------------------------------------------------------

(rum/defc BottomInfo < rum/static
  []
  [:div.help-center
    [:div.link-wrapper
      [:a {:href "#/login"} "Return to Login"]]])

(rum/defc SuccessMsg < rum/static
  []
  [:h4.info-bar "Please check your email for further instructions."])

(rum/defc CreateAccountForm < rum/static
  [{:keys [email password sending-info?]}]
  (let [submit-btn-disabled? (or (blank? email)
                                 (blank? password))]
    [:div
      [:h4.info-bar "Enter your information to create an account."]
      [:div.white-wrapper.login-form
        [:form {:action ""
                :method "post"
                :on-submit submit-create-account-form}
          [:input#emailInput.big-input
            {:on-change (partial on-change-text-field :email)
             :placeholder "Email Address"
             :type "text"
             :value email}]
          [:input.big-input
            {:on-change (partial on-change-text-field :password)
             :placeholder "New Password"
             :type "password"
             :value password}]
          [:input ;; TODO: need a "loading" state here
            {:class (str "login-btn" (when submit-btn-disabled? " disabled"))
             :disabled submit-btn-disabled?
             :type "submit"
             :value "Create Account"}]]]]))

(rum/defc CreateAccountPage < rum/static
  [state]
  [:div.login-page-wrapper
    [:div.top-bar [:h1 "IROH"]]
    [:div.center
      (if (:info-sent? state)
        (SuccessMsg)
        (CreateAccountForm state))
      (BottomInfo)]])

;;------------------------------------------------------------------------------
;; Render Loop
;;------------------------------------------------------------------------------

(def app-container-el (by-id "appContainer"))

(defn- on-change-page-state
  "Render the page on every state change."
  [_kwd _the-atom _old-state new-state]
  (rum/request-render
    (rum/mount (CreateAccountPage new-state) app-container-el)))

(add-watch page-state :main on-change-page-state)

;;------------------------------------------------------------------------------
;; Page Init
;;------------------------------------------------------------------------------

(defn init! []
  ;; trigger an initial render
  (reset! page-state initial-page-state)

  ;; put the focus on the email field
  (.focus (by-id "emailInput")))
