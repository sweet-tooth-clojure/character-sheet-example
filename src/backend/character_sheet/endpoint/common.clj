(ns character-sheet.endpoint.common
  (:require [compojure.core :refer [routes]]
            [datomic.api :as d]
            [medley.core :as medley]
            [character-sheet.db.query.utils :as qu]
            [sweet-tooth.frontend.core.utils :as u]
            [sweet-tooth.endpoint.utils :as c])
  (:refer-clojure :exclude [format]))

(defn conn
  [ctx]
  (:conn (:db ctx)))

(defn db
  [ctx]
  (d/db (conn ctx)))

(defn format
  [e]
  {:entity (c/format e :db/id)})

(defn initialize-decisions
  [component decisions]
  (medley/map-vals (fn [v] (assoc v :initialize-context
                                 (fn [ctx] (assoc ctx :db (:db component)))))
                   decisions))

(defn endpoint
  [route decisions]
  (fn [component]
    (routes (c/resource-route route decisions component))))
