(ns ctia-ui.data
  "Just some pure data.")

;;------------------------------------------------------------------------------
;; CRUD Left Nav Tabs
;;------------------------------------------------------------------------------

;; NOTE: some of these routes are commented out because the pages are not ready yet
(def crud-left-nav
  [{:txt "Actors"
    :icon-id "actor"
    :route "/create-actor"}
   {:txt "Campaigns"
    :icon-id "campaign"
    :route "/create-campaign"}
   {:txt "Courses of Action"
    :icon-id "coa"}
    ;; :route "/create-coa"}])
   {:txt "Exploited Targets"
    :icon-id "target"}
    ;; :route "/create-exploit-target"}])
   {:txt "Feedback"
    :icon-id "feedback"}
    ;; :route "/create-feedback"}
   {:txt "Incidents"
    :icon-id "incident"}
    ;: route "/create-incident"}
   {:txt "Indicators"
    :icon-id "indicator"
    :route "/create-indicator"}
   {:txt "Judgements"
    :icon-id "judgment" ;; NOTE: no "e" in the spelling of "judgement" here
    :route "/create-judgement"}
   {:txt "TTPs"
    :icon-id "ttp"
    :route "/create-ttp"}
   {:txt "Verdicts"
    :icon-id "verdict"}
    ;; :route "/create-verdict"}
   {:txt "Sightings"
    :icon-id "sighting"
    :route "/create-sighting"}
   {:txt "Vocabularies"
    :icon-id "vocabulary"}])
    ;; :route "/create-vocabulary"}])
