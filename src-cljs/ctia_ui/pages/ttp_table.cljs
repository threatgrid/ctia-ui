(ns ctia-ui.pages.ttp-table
  (:require
    [clojure.string :refer [blank?]]
    [ctia-ui.components :refer [HeaderBar LeftNavTabs]]
    [ctia-ui.state :refer [app-state]]
    [oakmac.util :refer [atom-logger by-id js-log log]]
    [rum.core :as rum]))

(def $ js/jQuery)

;;------------------------------------------------------------------------------
;; Fake Data
;;------------------------------------------------------------------------------

;; Just some fake data for demo / development purposes.

(def row1-example-data
  {:name "Average Panda"
   :description "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur facilisis arcu a nulla malesuada bibendum."
   :version 5.2
   :status "Ongoing"
   :intent-primary "Advantage"
   :confidence "Low"
   :id "41f2963a6f8f5efe1fe47a7c20403d5c"
   ;; NOTE: these will be datetime objects in practice, not strings
   :last-modified "12:00 AM"
   :expires "Dec 1, 2016"})

(def row2-example-data
  {:name "Dragon Soup"
   :description "Aliquam vestibulum fermentum magna ac condimentum. Suspendisse auctor diam convallis, placerat massa non, fringilla felis."
   :version 4.8
   :status "Historic"
   :intent-primary "Advantage"
   :intent-secondary "Economic"
   :confidence "High"
   :id "c81e728d9d4c2f636f067f89cc14862c"
   :last-modified "Yesterday"
   :expires "Jun 12, 2016"})

(def row3-example-data
  {:name "Red Empire"
   :description "Proin quis bibendum mi, sed iaculis nisi. Sed at elit purus. Suspendisse hendrerit nulla imperdiet tempus dignissim."
   :version 1.1
   :status "Future"
   :intent-primary "Theft"
   :intent-secondary "Intellectual Property"
   :confidence "Unknown"
   :id "550a141f12de6341fba65b0ad0433500"
   :last-modified "Last Week"
   :expires "Jul 17, 2016"})

(def sample-indicators
  {"0" "Manhattan, KS"
   "1" "Montgomery, AL"
   "2" "Seattle, WA"})

(def initial-attack-patterns
  {"0" {:capec-id "436"
        :description "Gain Physical Access"}
   "1" {:capec-id "210"
        :description "Abuse of Functionality"}
   "2" {:capec-id "281"
        :description "Analyze Target"}})

(def initial-malware-types
  {"0" {:type "Automated Transfer Scripts"
        :description "Nullam at vestibulum nunc. Donec sodales lorem in ultricies malesuada."}
   "1" {:type "Dialer"
        :description "Integer mauris magna, accumsan sit amet libero non, aliquet cursus magna."}})

;;------------------------------------------------------------------------------
;; Page State Cursor
;;------------------------------------------------------------------------------

(def initial-page-state
  {})

(def page-state
  "A cursor to the :ttp-table key in app-state.
   NOTE: most things in this module operate on this cursor."
  (rum/cursor-in app-state [:ttp-table]))

;;------------------------------------------------------------------------------
;; Events
;;------------------------------------------------------------------------------

;; TODO: we need to actually do a search here
(defn- on-change-search [js-evt]
  (let [new-text (aget js-evt "currentTarget" "value")]
    (swap! page-state assoc :entity-search-txt new-text)))

(defn- click-left-tab [category-txt js-evt]
  (.preventDefault js-evt)
  (swap! page-state assoc :active-nav-section category-txt))

(defn- do-nothing
  "Do not pass Go. Do not collect $200."
  [js-evt]
  (.preventDefault js-evt)
  (.stopPropagation js-evt))

;;------------------------------------------------------------------------------
;; Entity Search Bar
;;------------------------------------------------------------------------------

(rum/defc EntitySearch < rum/static
  [entity-type search-text]
  [:div.search-wrapper-8ffea
    [:input.big-search-1c153
      {:on-change on-change-search
       :placeholder (str "Search " entity-type "...")
       :type "text"
       :value search-text}]])

;;------------------------------------------------------------------------------
;; Entity Table
;;------------------------------------------------------------------------------

;; NOTE: this is temporary
(defn- click-id [js-evt]
  (do-nothing js-evt)
  (swap! page-state assoc :page :new-ttp))

(rum/defc DropdownMenuTableCell < rum/static
  []
  [:td
    [:div.menu-link-7551a
      ;;[:img.icon-dots-a2f0c {:src "images/icons/menu-ellipsis-grey.svg" :alt ""}]
      [:img.icon-caret-04c3a {:src "images/icons/menu-caret-down.svg" :alt ""}]]])

;; NOTE: will use moment.js to format time objects here; using strings for now
;;       for simplicity
(rum/defc LastModifiedTableCell < rum/static
  [last-modified-time expiry-time]
  [:td
    [:div.last-modified-b8bbf last-modified-time]
    [:div.expires-c99cb expiry-time]])

(def id-truncate-length 5)

;; TODO:
;; - need a pop-over showing the full id text with ability to copy/paste
(rum/defc IDTableCell < rum/static
  [id]
  [:td.id-cell-a23da
    [:a.id-link-38e0b {:href "#" :on-click click-id}
      (subs id 0 id-truncate-length)]])

(def default-confidence-color "#626e80")

(def confidence-colors
  "Mapping of confidence text to colors."
   {"Low" "#626e80"
    "Medium" "#626e80"
    "High" "#24b253"
    "None" "#9aa1aa"
    "Unknown" "#9aa1aa"})

(rum/defc ConfidenceTableCell < rum/static
  [txt]
  [:td {:style {:color (get confidence-colors txt default-confidence-color)}} txt])

(rum/defc IntentTableCell < rum/static
  [primary-intent secondary-intent]
  [:td.intent-cell-3d567 primary-intent
    (when-not (blank? secondary-intent)
      [:span.muted-0c5dd (str " - " secondary-intent)])])

(def default-status-color "#626e80")

(def status-colors
  "Mapping of status text to color."
  {"Ongoing"  "#24b253"
   "Historic" "#9aa1aa"
   "Future"   "#ff8833"})

(rum/defc StatusTableCell < rum/static
  [status-txt]
  [:td {:style {:color (get status-colors status-txt default-status-color)}} status-txt])

(rum/defc VersionTableCell < rum/static
  [txt]
  [:td.version-cell-5c236 txt])

(def description-truncate-length 45)

;; TODO: need a pop-over for description to show the full text + ability to copy/paste
(rum/defc NameAndDescriptionTableCell < rum/static
  [name description]
  [:td
    [:div.name-9d061 name]
    [:div.description-b1e7b
      (if (> (count description) description-truncate-length)
        (str (subs description 0 description-truncate-length) "...")
        description)]])

(rum/defc TableHeaderCell < rum/static
  [txt]
  [:th.header-cell-a445e
    txt
    (when-not (blank? txt)
      [:img.caret-f2f3e {:src "images/icons/table-header-caret-down.svg" :alt ""}])])

(rum/defc EntityTableHeader < rum/static
  []
  [:thead
    [:tr.header-row-c2217
      (TableHeaderCell "Campaign")
      (TableHeaderCell "Vers.")
      (TableHeaderCell "Status")
      (TableHeaderCell "Intent")
      (TableHeaderCell "Conf.")
      (TableHeaderCell "ID")
      (TableHeaderCell "Modified")
      (TableHeaderCell "")]])

(rum/defc EntityRow < rum/static
  [{:keys [name description version status intent-primary intent-secondary
           confidence id last-modified expires]}]
  [:tr.tbl-row-d3a4c
    (NameAndDescriptionTableCell name description)
    (VersionTableCell version)
    (StatusTableCell status)
    (IntentTableCell intent-primary intent-secondary)
    (ConfidenceTableCell confidence)
    (IDTableCell id)
    (LastModifiedTableCell last-modified expires)
    (DropdownMenuTableCell)])

(rum/defc EntityTable < rum/static
  []
  [:table.entity-tbl-1b087
    (EntityTableHeader)
    [:tbody
      (EntityRow row1-example-data)
      (EntityRow row2-example-data)
      (EntityRow row3-example-data)
      (EntityRow row2-example-data)
      (EntityRow row2-example-data)
      (EntityRow row1-example-data)
      (EntityRow row3-example-data)
      (EntityRow row2-example-data)
      (EntityRow row1-example-data)
      (EntityRow row2-example-data)]])

;;------------------------------------------------------------------------------
;; TTPs Body
;;------------------------------------------------------------------------------

(rum/defc TTPsBody < rum/static
  [entity-search-txt]
  [:div
    (EntitySearch "TTPs" entity-search-txt)
    (EntityTable "TTPs")])

;;------------------------------------------------------------------------------
;; Top Level Page Component
;;------------------------------------------------------------------------------

(rum/defc TTPTablePage < rum/static
  [state]
  [:div.crud-page-5a6ea
    (HeaderBar (:header-bar state))
    [:div.body-wrapper-1227b
      [:div.left-col-88c65
        (LeftNavTabs "TTPs")]
      [:div.right-col-71df3
        (EntityTable)]]])

;;------------------------------------------------------------------------------
;; Page Init / Destroy
;;------------------------------------------------------------------------------

(defn init-ttp-table-page! []
  (swap! app-state assoc :page :ttp-table
                         :ttp-table initial-page-state))

(defn destroy-ttp-table-page! []
  (swap! app-state dissoc :page :ttp-table))
