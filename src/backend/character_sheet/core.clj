(ns character-sheet.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.stacktrace :as stacktrace]
            [datomic.api :as d]
            [duct.core :as duct]
            [com.flyingmachine.datomic-booties.core :as datb]
            [character-sheet.config :as config]
            [integrant.core :as ig]))

(defmacro final
  [& body]
  `(do (try (do ~@body)
            (catch Exception exc#
              (do (println "ERROR: " (.getMessage exc#))
                  (stacktrace/print-stack-trace exc#)
                  (System/exit 1))))
       (System/exit 0)))

(defn prep
  []
  (duct/prep (duct/read-config (io/resource "character_sheet_example/config.edn"))))

(defn -main
  [cmd & args]
  (case cmd
    "server"
    (ig/init (prep) [:duct/daemon])
    
    "db/install-schemas"
    (final
      (let [{:keys [db schema data]} (config/db)]
        (d/create-database db)
        (datb/conform (d/connect db)
                      schema
                      data
                      config/seed-post-inflate)))

    "deploy/check"
    ;; ensure that all config vars are set
    (final (config/full))))
