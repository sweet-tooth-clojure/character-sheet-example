(set-env!
  :source-paths #{"src/frontend"
                  "src/backend"
                  "src/cross"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha16"]
                  [org.clojure/clojurescript "1.9.456"]
                  [org.clojure/test.check "0.9.0" :scope "test"]
                  [adzerk/boot-cljs "1.7.228-1" :scope "test"]
                  [adzerk/boot-test "1.1.2" :scope "test"]
                  [org.clojure/core.async "0.2.385"]
                  [com.taoensso/timbre "4.10.0"]
                  [com.cemerick/url "0.1.1"]

                  ;; logging
                  [org.clojure/tools.logging "0.3.1"]
                  [sweet-tooth/sweet-tooth-workflow "0.2.0-SNAPSHOT"]
                  [sweet-tooth/sweet-tooth-endpoint "0.2.0-SNAPSHOT"]
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
                  [binaryage/devtools          "0.9.4"]
                  [venantius/accountant        "0.2.0"]
                  [bidi                        "2.1.1"]
                  [sweet-tooth/sweet-tooth-frontend "0.2.0-SNAPSHOT"]]

  :checkouts    '[[sweet-tooth/sweet-tooth-endpoint "0.2.0-SNAPSHOT"]
                  [sweet-tooth/sweet-tooth-frontend "0.2.0-SNAPSHOT"]])

(load-data-readers!)

(require
  '[boot.core]
  '[adzerk.boot-test :refer :all]
  '[adzerk.boot-cljs :refer [cljs]]
  '[boot.pod :as pod]
  '[sweet-tooth.workflow.tasks :refer [run dev]]
  '[com.flyingmachine.datomic-booties.tasks :refer [migrate-db create-db delete-db bootstrap-db recreate-db]]
  '[com.flyingmachine.datomic-junk :as dj]
  '[datomic.api :as d]
  '[system.repl :as srepl]
  '[character-sheet.core :as c]
  '[character-sheet.config :as config])

(defn new-conn
  []
  (d/connect (:db (config/full))))

(def conn (delay (new-conn)))

(deftask build
  "Builds an uberjar"
  [v version VERSION str "version number"
   p project PROJECT sym "project name"
   m main    MAIN    sym "server ns with main fn"
   f file    FILE    str "name of jar file"]
  [version project main file]
  (merge-env! :resource-paths (get-env :source-paths))
  (comp (cljs :optimizations :advanced
              :compiler-options {:parallel-build true})
        (pom :project project :version version)
        (uber :exclude (conj pod/standard-jar-exclusions #".*\.html" #"license" #"LICENSE")) ; needed for arcane magic reasons
        (aot :namespace #{main})
        (jar :main main :file file :project project)
        (target :dir #{"target/build"})))

(let [config (config/full)
      db {:uri (:db config)}
      data (merge db (select-keys config [:schema :data :transform]))]
  (task-options!
    cljs {:compiler-options {:asset-path "/main.out"
                             :parallel-build true
                             :preloads '[devtools.preload]}
          :source-map true}
    
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
