(set-env!
  :source-paths #{"src/frontend"
                  "src/backend"
                  "src/cross"
                  "src/dev-backend"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha12"]
                  [org.clojure/clojurescript "1.9.456"]
                  [org.clojure/test.check "0.9.0" :scope "test"]
                  [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                  [adzerk/boot-test "1.1.2" :scope "test"]
                  [org.clojure/core.async "0.2.385"]

                  ;; logging
                  [org.clojure/tools.logging "0.3.1"]
                  [sweet-tooth/sweet-tooth-workflow "0.1.0-SNAPSHOT"]
                  [sweet-tooth/sweet-tooth-endpoint "0.1.0-SNAPSHOT"]
                  [duct "0.8.0"]
                  [environ "1.0.3"]
                  [ring "1.5.0"]
                  [ring/ring-codec "1.0.1"]
                  [ring/ring-defaults "0.2.1"]
                  [ring-jetty-component "0.3.1"]
                  [ring-webjars "0.1.1"]
                  [ring-middleware-format "0.7.0"]
                  [meta-merge "0.1.1"]
                  [com.stuartsierra/component "0.3.1"]
                  [liberator "0.14.1"]
                  [com.datomic/datomic-free "0.9.5344" :exclusions [com.google.guava/guava]]
                  [com.flyingmachine/liberator-unbound "0.1.1"]
                  [com.flyingmachine/datomic-junk "0.2.3"]
                  [com.flyingmachine/webutils "0.1.6"]
                  [compojure "1.5.0"]
                  [io.rkn/conformity "0.4.0"]
                  [io.clojure/liberator-transit "0.3.0"]
                  [medley "0.7.1"]
                  [clj-time "0.11.0"]
                  [com.flyingmachine/datomic-booties "0.1.7"]
                  [cheshire "5.6.2"]

                  ;; client
                  [reagent                     "0.6.0-rc" :exclusions [cljsjs/react]]
                  [cljsjs/marked               "0.3.5-0"]
                  [cljsjs/react-with-addons    "15.1.0-0"]
                  [re-frame                    "0.8.0"]
                  [cljs-ajax                   "0.5.8"]
                  [com.andrewmcveigh/cljs-time "0.4.0"]
                  [secretary                   "1.2.3"]
                  [sweet-tooth/sweet-tooth-frontend "0.1.0-SNAPSHOT"]])

(load-data-readers!)

(require
  '[boot.core]
  '[adzerk.boot-test :refer :all]
  '[sweet-tooth.workflow.tasks :refer :all]
  '[com.flyingmachine.datomic-booties.tasks :refer [migrate-db create-db delete-db bootstrap-db recreate-db]]
  '[com.flyingmachine.datomic-junk :as dj]
  '[datomic.api :as d]
  '[system.repl :as srepl]
  '[character-sheet.core :as c]
  '[character-sheet.config :as config])

(let [config (config/full)
      db {:uri (:db config)}
      data (merge db (select-keys config [:schema :data :transform]))]
  (task-options!
    run {:files ["system.clj" "endpoint" "db" "middleware" "lib"]
         :regexes true
         :js-main 'character-sheet.core/-main}

    build {:version "0.1.0"
           :project 'ca
           :main 'character-sheet.core
           :file "app.jar"}

    dev {:port "3000"
         :uri (:db config)
         :sys #'c/system}

    create-db db
    delete-db db
    migrate-db data
    bootstrap-db data
    recreate-db data))

(defn new-conn
  []
  (d/connect (:db (config/full))))

(def conn (delay (new-conn)))

(defn user
  []
  (dj/one (d/db (new-conn)) [:user/username]))
