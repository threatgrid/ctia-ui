(defproject ctia-ui "0.0.1"

  :description "A user interface to the Cisco Threat Intelligence API (CTIA)."
  :url "https://github.com/threatgrid/ctia-ui"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.40"]
                 [cljsjs/jquery "2.2.2-0"]
                 [cljsjs/jquery-ui "1.11.4-0"]
                 [cljsjs/marked "0.3.5-0"]
                 [cljsjs/moment "2.10.6-4"]
                 [com.oakmac/util "3.0.0"]
                 [rum "0.10.5"]
                 [threatgrid/ctim "0.2.0"]]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :source-paths ["src"]

  :clean-targets ["public/js/app-dev.js"
                  "public/js/app-prod.js"]

  :cljsbuild
    {:builds
      [{:id "dev"
        :source-paths ["src-cljs"]
        :compiler {:output-to "public/js/app-dev.js"
                   :optimizations :whitespace}}

       {:id "prod"
        :source-paths ["src-cljs"]
        :compiler {:output-to "public/js/app-prod.js"
                   :optimizations :advanced
                   :pretty-print false}}]})
