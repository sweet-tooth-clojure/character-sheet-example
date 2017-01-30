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

(defn page-params
  [ctx & allowed-keys]
  (-> (c/params ctx)
      (select-keys (into [:page :per-page :sort-order :sort-by :query-id :type]
                         allowed-keys))
      (u/update-vals {[:page :per-page] #(Integer. %)
                      [:sort-by :sort-order :query-id :type] #(keyword (subs % 1))})))

;; formatting query results
(def types
  {:character-sheet/name :character-sheet})

(defn format-single
  [ent ent-type id-key]
  (when ent-type
    {:data {ent-type (qu/key-by id-key [ent])}}))

(defn format-coll
  [ent-type id-key ents]
  (when ent-type
    {:data {ent-type (qu/key-by id-key ents)}}))

(defmulti format (fn [e] (if (map? e) :single :coll)))

(defmethod format :single
  [e]
  (format-single e (some types (keys e)) :db/id))

(defmethod format :coll
  [e]
  (format-coll (some types (keys (first e))) :db/id e))


(defn initialize-decisions
  [component decisions]
  (medley/map-vals (fn [v] (assoc v :initialize-context
                                 (fn [ctx] (assoc ctx :db (:db component)))))
                   decisions))

(defn endpoint
  [route decisions]
  (fn [component]
    (routes (c/resource-route route decisions component))))
