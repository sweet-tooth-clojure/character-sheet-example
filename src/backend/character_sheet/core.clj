(ns character-sheet.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.stacktrace :as stacktrace]
            [datomic.api :as d]
            [duct.core :as duct]
            [com.flyingmachine.datomic-booties.core :as datb]
            [integrant.core :as ig]
            [character-sheet.duct.datomic]))

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
      (let [{:keys [uri schema data]} (:sweet-tooth.endpoint/datomic (prep))]
        (d/create-database uri)
        (datb/conform (d/connect uri)
                      schema
                      data
                      character-sheet.duct.datomic/seed-post-inflate)))

    "deploy/check"
    ;; ensure that all config vars are set
    ;; TODO bring this back
    (final nil)))
