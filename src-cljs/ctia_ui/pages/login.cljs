(ns ctia-ui.pages.login
  (:require
    [clojure.string :refer [blank?]]
    [ctia-ui.state :refer [app-state]]
    [ctia-ui.util :refer [neutralize-event]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

;;------------------------------------------------------------------------------
;; Page State
;;------------------------------------------------------------------------------

(def initial-page-state
  {:error false
   :keep-logged-in? false
   :password ""
   :username ""})

;;------------------------------------------------------------------------------
;; Events
;;------------------------------------------------------------------------------

(defn- on-change-username [js-evt]
  (let [new-text (aget js-evt "currentTarget" "value")]
    (swap! app-state assoc-in [:login-page :username] new-text)))

(defn- on-change-password [js-evt]
  (let [new-text (aget js-evt "currentTarget" "value")]
    (swap! app-state assoc-in [:login-page :password] new-text)))

(defn- click-keep-logged-in [js-evt]
  (neutralize-event js-evt)
  (swap! app-state update-in [:login-page :keep-logged-in?] not))

(defn- submit-login-form [js-evt]
  (neutralize-event js-evt)
  ;; TODO: log the user in here

  ;; FIXME: temporary login redirect
  (aset js/document "location" "hash" "/create-indicator"))

;;------------------------------------------------------------------------------
;; Login Page Component
;;------------------------------------------------------------------------------

(rum/defc HelpCenter < rum/static
  []
  [:div.help-center
    [:div.link-wrapper
      [:a {:href "#/reset-password"} "Forgot your ID or password?"]]
    [:div.link-wrapper
      [:a {:href "#/create-account"} "Don't have an account?"]]])

(rum/defc LoginPage < rum/static
  [state]
  (let [{:keys [error keep-logged-in? password username]} (:login-page state)
        login-btn-disabled? (or (blank? password)
                                (blank? username))]
    [:div.login-page-wrapper
      [:div.top-bar [:h1 "IROH"]]
      [:div.center
        [:div.white-wrapper.login-form
          [:form {:action ""
                  :method "post"
                  :on-submit submit-login-form}
            [:input#usernameInput.big-input
              {:on-change on-change-username
               :placeholder "Username"
               :type "text"
               :value username}]
            [:input.big-input
              {:on-change on-change-password
               :placeholder "Password"
               :type "password"
               :value password}]
            ;; FIXME: temporary style hack
            [:div {:style {:height "5px"}}]
            [:button
              {:class (if keep-logged-in? "selected-checkbox-da5f6" "checkbox-7b421")
               :on-click click-keep-logged-in
               :type "button"}
              "Keep me logged in"]
            [:input#loginBtn
              {:class (str "login-btn" (when login-btn-disabled? " disabled"))
               :disabled login-btn-disabled?
               :type "submit"
               :value "Log In"}]]]
        (HelpCenter)]]))

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-login-page! []
  (swap! app-state assoc :page :login
                         :login-page initial-page-state)

  ;; put the focus on the username field
  ;; NOTE: should this be a mixin on the component instead?
  (.focus (by-id "usernameInput")))

(defn destroy-login-page! []
  (swap! app-state dissoc :page :login-page))
