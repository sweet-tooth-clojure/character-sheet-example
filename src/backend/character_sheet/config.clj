(ns character-sheet.config
  (:require [environ.core :refer [env]]
            [meta-merge.core :refer [meta-merge]]
            [character-sheet.specs :as cs]
            [clojure.spec :as s]
            [datomic.api :as d]))

(defn seed-post-inflate
  [x]
  (assoc x :db/id (d/tempid :db.part/user)))

(def local-db "datomic:free://localhost:4334/character-sheet")

(def defaults
  {:http      ^:displace {:port 3000}
   :db        ^:displace local-db
   :schema    ["db/base/character.edn"]
   :data      ["db/seeds.edn"]
   ;; TODO check if it's possible to just pass function
   :transform 'character-sheet.config/seed-post-inflate})

(s/def ::port pos-int?)
(s/def ::http (s/keys :req-un [::port]))

(s/def ::db :common/not-empty-string)

(s/def ::schema (s/coll-of :common/not-empty-string))

(s/def ::data (s/coll-of :common/not-empty-string))

(s/def ::transform symbol?)

(s/def ::full-config (s/keys :req-un [::http ::db ::schema ::data ::transform]))
(s/def ::db-config (s/keys :req-un [::db ::schema ::data ::transform]))

(defn merged-config
  []
  (meta-merge defaults
              {:http  (some-> env :http-server-port Integer. (#(hash-map :port %)))
               :db    (:db-uri env)}))

(defn full
  []
  (cs/validate ::full-config (merged-config)))

(defn db
  []
  (cs/validate ::db-config (merged-config)))
