(ns ctia-ui.config)

;;------------------------------------------------------------------------------
;; Application Config
;;------------------------------------------------------------------------------

;; TODO: allow config properties to be overwritten either via query param
;; or loading a config.json file

(def default-config
  {:api-key "fake-api-key"
   :in-demo-mode? (not= -1 (.indexOf js/document.location.href "demo"))})

(def config default-config)
