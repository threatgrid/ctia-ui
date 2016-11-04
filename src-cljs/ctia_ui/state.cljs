(ns ctia-ui.state
  "App state atom."
  (:require
    [oakmac.util :refer [atom-logger]]))

(def initial-app-state
  "Initial application state."
  {:header-bar
    {:create-new-dropdown-showing? false
     :search-txt ""}})

(def app-state
  "This atom holds the application state."
  (atom initial-app-state))

;; NOTE: useful for debugging
; (add-watch app-state :log atom-logger)
