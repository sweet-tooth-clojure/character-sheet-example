(set-env!
  :source-paths #{"src/frontend"
                  "src/backend"
                  "src/cross"
                  "dev/src"}
  :resource-paths #{"resources"
                    "dev/resources"}
  :dependencies '[[org.clojure/clojure "1.9.0-beta2"]
                  [org.clojure/clojurescript "1.9.908"]
                  [org.clojure/test.check "0.9.0" :scope "test"]
                  [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                  [adzerk/boot-test "1.1.2" :scope "test"]
                  [org.clojure/core.async "0.2.385"]
                  [com.taoensso/timbre "4.10.0"]

                  [org.clojure/tools.logging "0.3.1"]
                  [ring "1.5.0" :exclusions [org.clojure/tools.namespace]]
                  [ring/ring-codec "1.0.1"]
                  [ring/ring-defaults "0.2.1"]
                  [ring-jetty-component "0.3.1"]
                  [ring-middleware-format "0.7.0"]
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
                  [com.flyingmachine/datomic-booties "0.1.8"]
                  [cheshire "5.6.2"]

                  ;; client
                  [reagent                     "0.7.0" :exclusions [cljsjs/react]]
                  [cljsjs/marked               "0.3.5-0"]
                  [cljsjs/react-dom            "15.6.1-0" :exclusions [cljsjs/react]]
                  [cljsjs/react-with-addons    "15.6.1-0"]
                  [re-frame                    "0.9.4"]
                  [cljs-ajax                   "0.5.8"]
                  [secretary                   "1.2.3"]
                  [binaryage/devtools          "0.9.4"]
                  [venantius/accountant        "0.2.0"]
                  [bidi                        "2.1.1"]

                  ;; duct
                  [duct/core "0.5.0"]
                  [duct/module.logging "0.2.0"]
                  [duct/module.web "0.5.4"]
                  [integrant "0.4.1"]

                  ;; local dev
                  [integrant/repl "0.2.0" :scope "test"]])

(def sweet-tooth-packages
  "Define this seperately so packages can get included as checkouts"
  '[[sweet-tooth/sweet-tooth-frontend "0.2.11"]
    [sweet-tooth/sweet-tooth-endpoint "0.2.3-SNAPSHOT"]
    [sweet-tooth/sweet-tooth-workflow "0.2.4"]])

(set-env! :dependencies #(into % sweet-tooth-packages)
          ;; for dev
          :checkouts sweet-tooth-packages)

(load-data-readers!)

(require
  '[boot.core]
  '[adzerk.boot-test :refer :all]
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-reload :refer [reload]]
  '[sweet-tooth.workflow.tasks :refer [dev build reload-integrant] :as tasks]
  '[com.flyingmachine.datomic-booties.tasks :refer [migrate-db create-db delete-db bootstrap-db recreate-db]]
  '[com.flyingmachine.datomic-junk :as dj]
  '[datomic.api :as d]
  '[integrant.repl :as ir]
  '[dev])

(defn new-conn
  []
  (d/connect (:uri (:sweet-tooth.endpoint/datomic (dev/prep)))))

(def conn (delay (new-conn)))

(let [db     (:sweet-tooth.endpoint/datomic (dev/prep))
      db-uri (select-keys db [:uri])]
  (task-options!
    cljs {:compiler-options {:asset-path "/main.out"
                             :parallel-build true
                             :preloads '[devtools.preload]}}

    reload {:on-jsload 'character-sheet.core/-main}
    
    build {:version "0.2.0"
           :project 'ca
           :main 'character-sheet.core
           :file "app.jar"}

    reload-integrant {:prep-fn 'dev/prep}
    
    create-db    db-uri
    delete-db    db-uri
    migrate-db   db
    bootstrap-db db
    recreate-db  db))
