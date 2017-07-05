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

(defn format
  [e]
  {:data (c/format e :db/id)})

(defn initialize-decisions
  [component decisions]
  (medley/map-vals (fn [v] (assoc v :initialize-context
                                 (fn [ctx] (assoc ctx :db (:db component)))))
                   decisions))

(defn endpoint
  [route decisions]
  (fn [component]
    (routes (c/resource-route route decisions component))))
