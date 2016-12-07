(ns ctia-ui.data
  "Just some pure data.")

;;------------------------------------------------------------------------------
;; CRUD Left Nav Tabs
;;------------------------------------------------------------------------------

;; NOTE: some of these routes are commented out because the pages are not ready yet
(def crud-left-nav
  [{:txt "Actors"
    :icon-id "actor"
    :route "/actor-table"}
   {:txt "Campaigns"
    :icon-id "campaign"
    :route "/campaign-table"}
   {:txt "Courses of Action"
    :icon-id "coa"
    :route "/coa-table"}
   {:txt "Exploited Targets"
    :icon-id "target"}
    ;; :route "/exploit-target-table"}])
   {:txt "Feedback"
    :icon-id "feedback"}
    ;; :route "/feedback-table"}
   {:txt "Incidents"
    :icon-id "incident"
    :route "/incident-table"}
   {:txt "Indicators"
    :icon-id "indicator"
    :route "/indicator-table"}
   {:txt "Judgements"
    :icon-id "judgment" ;; NOTE: no "e" in the spelling of "judgement" here
    :route "/judgement-table"}
   {:txt "TTPs"
    :icon-id "ttp"
    :route "/ttp-table"}
   {:txt "Verdicts"
    :icon-id "verdict"
    :route "/verdict-table"}
   {:txt "Sightings"
    :icon-id "sighting"
    :route "/sighting-table"}
   {:txt "Vocabularies"
    :icon-id "vocabulary"}])
    ;; :route "/vocabulary-table"}])
