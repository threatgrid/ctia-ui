(ns ctia-ui.routes
  (:require
    [goog.functions :refer [once]]
    [ctia-ui.pages.actor-form :refer [destroy-actor-form-page! init-actor-form-page!]]
    [ctia-ui.pages.campaign-form :refer [destroy-campaign-form-page! init-campaign-form-page!]]
    [ctia-ui.pages.create-account :as create-account-page]
    [ctia-ui.pages.indicator-form :refer [destroy-indicator-form-page! init-indicator-form-page!]]
    [ctia-ui.pages.judgement-form :refer [destroy-judgement-form-page! init-judgement-form-page!]]
    [ctia-ui.pages.judgement-table :refer [destroy-judgement-table-page! init-judgement-table-page!]]
    [ctia-ui.pages.login :refer [destroy-login-page! init-login-page!]]
    [ctia-ui.pages.reset-password :as reset-password-page]
    [ctia-ui.pages.sighting-form :refer [destroy-sighting-form-page! init-sighting-form-page!]]
    [ctia-ui.pages.ttp-form :refer [destroy-ttp-form-page! init-ttp-form-page!]]
    [ctia-ui.pages.ttp-table :refer [destroy-ttp-table-page! init-ttp-table-page!]]))

;;------------------------------------------------------------------------------
;; Routes
;;------------------------------------------------------------------------------

(def default-route "/login")

(def routes
  {"/login" [init-login-page! destroy-login-page!]
   "/create-account" create-account-page/init!
   "/reset-password" reset-password-page/init!

   "/create-actor" [init-actor-form-page! destroy-actor-form-page!]
   "/create-campaign" [init-campaign-form-page! destroy-campaign-form-page!]
   "/create-indicator" [init-indicator-form-page! destroy-indicator-form-page!]
   "/create-judgement" [init-judgement-form-page! destroy-judgement-form-page!]
   "/create-sighting" [init-sighting-form-page! destroy-sighting-form-page!]
   "/create-ttp" [init-ttp-form-page! destroy-ttp-form-page!]

   "/judgement-table" [init-judgement-table-page! destroy-judgement-table-page!]
   "/ttp-table" [init-ttp-table-page! destroy-ttp-table-page!]})

(def previous-page-destroy-fn (atom nil))

(defn- on-hash-change []
  (let [new-route (.replace (aget js/document "location" "hash") #"^#" "")
        page (get routes new-route)
        init-fn (if (vector? page) (first page) page)
        destroy-fn (if (vector? page) (second page))]
    (if-not (fn? init-fn)
      ;; redirect to the default route if we do not recognize the hash
      (aset js/document "location" "hash" default-route)
      ;; else load the new page
      (do
        ;; run destroy function from the last page
        (when (fn? @previous-page-destroy-fn)
          (@previous-page-destroy-fn))

        ;; run init function for the new page
        (init-fn)

        ;; store destroy function for next hash change
        (reset! previous-page-destroy-fn destroy-fn)))))

;;------------------------------------------------------------------------------
;; Routes Init
;;------------------------------------------------------------------------------

(def init!
  "Initialize routing.
   NOTE: this function should be called on global app init"
  (once
    (fn []
      ;; add the event handler
      (aset js/window "onhashchange" on-hash-change)

      ;; kick off the initial page
      (on-hash-change))))
