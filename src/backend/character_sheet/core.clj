(ns character-sheet.core
  (:gen-class)
  (:require [datomic.api :as d]
            [com.stuartsierra.component :as component]
            [com.flyingmachine.datomic-booties.core :as datb]
            [character-sheet.system :as system]
            [character-sheet.config :as config]))

(defmacro final
  [& body]
  `(do (try (do ~@body)
            (catch Exception exc#
              (do (println "ERROR: " (.getMessage exc#))
                  (clojure.stacktrace/print-stack-trace exc#)
                  (System/exit 1))))
       (System/exit 0)))

(defn system
  []
  (system/new-system (config/full)))

(defn -main
  [cmd & args]
  (case cmd
    "server"
    (component/start-system (system))
    
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
