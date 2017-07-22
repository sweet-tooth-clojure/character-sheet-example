(ns character-sheet.duct.datomic
  (:require [integrant.core :as ig]
            [datomic.api :as d]))

(defn seed-post-inflate
  [x]
  (assoc x :db/id (d/tempid :db.part/user)))

(defmethod ig/init-key :character-sheet.duct/datomic [_ spec]
  spec)
