(ns ctia-ui.config
  "Application config.")

;;------------------------------------------------------------------------------
;; Application Config
;;------------------------------------------------------------------------------

;; TODO: allow config properties to be overwritten either via query param
;; or loading a config.json file

(def tenzin-base-url
  "https://tenzin-beta.amp.cisco.com/")

(def default-config
  {:api-key "fake-api-key"
   :api-base-url tenzin-base-url
   :in-demo-mode? (not= -1 (.indexOf js/document.location.href "demo"))})

(def config default-config)
