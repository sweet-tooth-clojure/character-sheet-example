(ns character-sheet.db.query.utils
  (:require [datomic.api :as d]))

(defn key-by
  [k xs]
  (into {} (map (juxt k identity) xs)))

(defn key-by-first
  "Like key by, but useful for datomic results"
  [k xs]
  (into {} (map (comp (juxt k identity) first) xs)))

(defn mv-key
  [x k1 k2]
  (-> x
      (assoc k2 (k1 x))
      (dissoc k1)))

(defn cardinality
  "Returns the cardinality (:db.cardinality/one or
   :db.cardinality/many) of the attribute"
  [db attr]
  (->>
    (d/q '[:find ?v
           :in $ ?attr
           :where
           [?attr :db/cardinality ?card]
           [?card :db/ident ?v]]
         db attr)
    ffirst))

(defn maybe-result
  [result db attr if-not]
  (if (seq result)
    (case (cardinality db attr)
      :db.cardinality/one (ffirst result)
      :db.cardinality/many (into #{} (map first result)))
    if-not))

(defn maybe
  "Returns the value of attr for e, or if-not if e does not possess
   any values for attr. Cardinality-many attributes will be
   returned as a set"
  [db e attr if-not]
  (let [result (d/q '[:find ?v
                      :in $ ?e ?a
                      :where [?e ?a ?v]]
                    db e attr)]
    (maybe-result result db attr if-not)))

(defn _maybe
  "Returns the e for value/attr pair, or if-not if e does not possess
  any values for attr. Cardinality-many attributes will be returned as
  a set"
  [db v attr if-not]
  (let [result (d/q '[:find ?e
                      :in $ ?v ?a
                      :where [?e ?a ?v]]
                    db v attr)]
    (maybe-result result db attr if-not)))

(defn maybe-user-toggle
  [db toggle-target toggle-target-attr user-id if-not]
  (-> (d/q [:find '?toggle-ent
            :in '$ '?toggle-target '?user-id
            :where
            ['?toggle-ent toggle-target-attr '?toggle-target]
            '[?toggle-ent :meta/owner ?user-id]]
           db toggle-target user-id)
      (maybe-result db toggle-target-attr if-not)))

(defn query-user-vote
  "Transform a query so that it also pulls user votes for the content"
  [query user-id]
  (if user-id
    (-> query
        (update :find conj '?user-vote)
        (update :where conj [(list 'ca.db.query.utils/maybe-user-toggle '$ '?e :vote/content user-id false)
                             '?user-vote]))
    query))

(defn query-user-flag
  "Transform a query so that it also pulls user flag for the content"
  [query user-id]
  (if user-id
    (-> query
        (update :find conj '?user-flag)
        (update :where conj [(list 'ca.db.query.utils/maybe-user-toggle '$ '?e :flag/flagged user-id false)
                             '?user-flag]))
    query))

(defn assoc-user-votes
  [xs]
  (map (fn [[result user-vote]] (assoc result :user-voted user-vote)) xs))


(defn find-by
  [db find e & ks]
  (d/q (into [:find find
              :in '$
              :where]
             (map #(into [e] %) ks))
       db))
