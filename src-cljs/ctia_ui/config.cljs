(ns ctia-ui.config)

;;------------------------------------------------------------------------------
;; Application Config
;;------------------------------------------------------------------------------

;; TODO: allow config properties to be overwritten either via query param
;; or loading a config.json file

(def default-config
  {:use-demo-data? true})

(def config default-config)
